package com.jabat.personal.unipiplishopping;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.annotation.NonNull;
import android.util.Log;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import android.Manifest;


public class MainActivity extends BaseActivity {

    //παίρνουμε το location και με τους δυο τροπους σε περιπτωση που ο κλασσικός δεν λειτουργεί
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;

    //δημιουργούμε Channel_id για τα notification
    private static final String CHANNEL_ID ="nearby_products_channel";

    private NotificationManager notificationManager;

    private static final int LOCATION_REQUEST_CODE = 1001;

    TextView profileName, profileUsername, locationText;
    ImageView profileImage,productsImage,locationImage, settingsImage;

    String key;
    private boolean isSearchActive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //παίρνουμε απο το BaseActivity το theme
        applyBackgroundColor(R.id.main);


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);


        //κλήση μεθόδου δημιουργίας notification channel
        createNotificationChannel();

        settingsImage = findViewById(R.id.settingsImg);
        profileImage = findViewById(R.id.profileImg);
        productsImage = findViewById(R.id.productsImg);
        locationImage = findViewById(R.id.locationImg);
        profileName = findViewById(R.id.titleName);
        profileUsername = findViewById(R.id.titleUsername);
        locationText = findViewById(R.id.titleLocation);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //παίρνουμε από τα preferences τι είχε ο χρήστης πριν ξανα συνδεθεί ώστε να του εμφανίσουμε την ανάλογη εικόνα και κείμενο και να ξεκινήσει αυτοματα η διαδικασία αναζήτησης προιόντος αν ήταν ανοιχτή
        sharedPreferences = getSharedPreferences("LocationPreferences", MODE_PRIVATE);
        isSearchActive = sharedPreferences.getBoolean("isLocationActive", false);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            if (isSearchActive) {
                // Start search if previously active
                startLocationUpdates();
                locationText.setText(R.string.location_label_on);
                locationImage.setImageResource(R.drawable.baseline_location_on_24);
            } else {
                locationText.setText(R.string.location_label_off);
                locationImage.setImageResource(R.drawable.baseline_location_off_24);
            }
        } else {
            //αν η τοποθεσία είναι κλειστή τότε να δειχνεί κλειστό ανεξαρτήτως με το τι είχε ο χρήστης πριν
            locationText.setText(R.string.location_label_off);
            locationImage.setImageResource(R.drawable.baseline_location_off_24);
            isSearchActive = false; // Override saved preference
            saveLocationState(false);
            Toast.makeText(MainActivity.this, R.string.toast_location_disabled, Toast.LENGTH_SHORT).show();
        }

        //κλήση μεθόδου εμφάνισης στοιχείων στο προφίλ του.
        showUserData();

        //πάει στα settings
        settingsImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        //πάει στο profile.
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passUserData();
            }
        });

        //πάει στο activity με τα προιόντα ώστε να κάνει order αν θέλει
        productsImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ProductsActivity.class);
                startActivity(intent);
            }
        });

        //όταν πατάει την τοποθεσία
        locationImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //αν είναι ανοιχτή η αναζήτηση τότε κλείνει την αναζήτηση
                if (isSearchActive) {
                    // Location updates are active, stop them
                    stopLocationUpdates();
                    locationText.setText(R.string.location_label_off);
                    locationImage.setImageResource(R.drawable.baseline_location_off_24);
                    /*Toast.makeText(getApplicationContext(), "Location turned off", Toast.LENGTH_SHORT).show();*/
                    isSearchActive = false;

                    saveLocationState(false);

                    //αν ειναι κλειστή
                } else {
                    //και ειναι ανοιχτή η τοπθεσία του κινητού
                    if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                        //ενεργοποιείται η αναζήτηση
                        startLocationUpdates();
                        locationText.setText(R.string.location_label_on);
                        locationImage.setImageResource(R.drawable.baseline_location_on_24);
                        Toast.makeText(MainActivity.this, R.string.toast_location_on, Toast.LENGTH_SHORT).show();
                        isSearchActive = true;

                        saveLocationState(true);
                        //και είναι κλειστή η τοποθεσία του κινητού
                    } else {
                        //ζητάει ενεργοποίηση τοποθεσίας
                        Toast.makeText(MainActivity.this, R.string.toast_location_off, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent, LOCATION_REQUEST_CODE);
                    }
                }


            }


        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LOCATION_REQUEST_CODE) {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                    locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {

                startLocationUpdates();
                TextView locationText = findViewById(R.id.titleLocation);
                ImageView locationImage = findViewById(R.id.locationImg);
                locationText.setText(R.string.location_label_on);
                locationImage.setImageResource(R.drawable.baseline_location_on_24);
                Toast.makeText(MainActivity.this, R.string.toast_location_asked_on, Toast.LENGTH_SHORT).show();
                isSearchActive = true;

                saveLocationState(true);
            } else {
                Toast.makeText(MainActivity.this, R.string.toast_location_asked_off, Toast.LENGTH_SHORT).show();
            }
        }
    }

    //μέθοδος αποθήκευσης στα preferences για το αν είναι ανοιχτή ή κλειστή η λειτουργία αναζήτησης
    private void saveLocationState(boolean isActive) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLocationActive", isActive);
        editor.apply();
    }

    //μέθοδος εμφάνισης ονόματος και username
    public void showUserData(){
        Intent intent = getIntent();
        String nameUser = intent.getStringExtra("name");
        String usernameUser = intent.getStringExtra("username");

        profileName.setText(nameUser);
        profileUsername.setText(usernameUser);

    }

    //μεταφορά των δεδομένων στην ProfileActivity
    public void passUserData(){
        String userUsername = profileUsername.getText().toString().trim();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query checkUserDatabase = reference.orderByChild("username").equalTo(userUsername);
        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String nameFromDB = snapshot.child(userUsername).child("name").getValue(String.class);
                    String surnameFromDB = snapshot.child(userUsername).child("surname").getValue(String.class);
                    String usernameFromDB = snapshot.child(userUsername).child("username").getValue(String.class);
                    /*String passwordFromDB = snapshot.child(userUsername).child("password").getValue(String.class);*/
                    Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                    intent.putExtra("name", nameFromDB);
                    intent.putExtra("surname", surnameFromDB);
                    intent.putExtra("username", usernameFromDB);
                    /*intent.putExtra("password", passwordFromDB);*/
                    startActivity(intent);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    //παίρνει την τοποθεσία του χρήστη και καλεί την μέθοδο αναζήτησης προιόντων κοντά του.
    private void startLocationUpdates() {
        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY,10000).build();



        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {

                    return;
                }

                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        double userLatitude = location.getLatitude();
                        double userLongitude = location.getLongitude();


                        // Κάλεσε τη μέθοδο για έλεγχο κοντινών προϊόντων
                        checkNearbyProducts(userLatitude, userLongitude);
                    }
                }
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUEST_CODE);
            return;
        }

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        isSearchActive = true;
    }

    //σταματάει να παίρνει τοποθεσία
    private void stopLocationUpdates() {
        if (fusedLocationProviderClient != null && locationCallback != null) {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
            isSearchActive = false;
        }
    }

    //βάσει της τοποθεσίας του χρήστη ψάχνει προιόντα σε ακτίνα 200 μέτρων, αν τα βρει τα αντιστοιχεί με την παραγγελία και τον χρήστη από την βάση και καλέι μέθοδο δημιουργίας notification
    private void checkNearbyProducts(double userLat, double userLng) {
        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("orders");
        DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference("products");
        String currentUser = User.getUsername();

        productsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot productSnapshot) {
                for (DataSnapshot productData : productSnapshot.getChildren()) {
                    Product product = productData.getValue(Product.class);

                    if (product != null) {
                        double productLat = product.getLatitude();
                        double productLng = product.getLongitude();

                        Log.d("MainActivity", "userLat: " + userLat + " userLng: " + userLng + " prLat: " + productLat + " prLng: " + productLng);

                        //ελέγχει αν το προιον είναι σε ακτίνα 200 μετρων
                        if (isWithinRadius(userLat, userLng, productLat, productLng, 200)) {

                            //παίρνει τα orders βάσει του productCode να ισούται με το προιον που πέρασε τη συνθήκη
                            ordersRef.orderByChild("productCode").equalTo(product.getCode()).addListenerForSingleValueEvent(new ValueEventListener() {

                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
                                        Order order = orderSnapshot.getValue(Order.class);
                                        Log.d("OrderKey", "Order Key: " + orderSnapshot.getKey());


                                        if(currentUser.equals(order.getUserUsername())){
                                            key = orderSnapshot.getKey();

                                            //εμφάνιση ειδοποίησης
                                            showNotification(MainActivity.this.getString(R.string.not_title),
                                                    MainActivity.this.getString(R.string.not_message) + " " + product.getTitle(),
                                                    MainActivity.this,
                                                    NotificationActivity.class,
                                                    product,
                                                    key);
                                        }


                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(MainActivity.this, "Failed to fetch orders: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Failed to fetch products: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    //μέθοδος ελέγχου αν οι συντεταγμένες του προιόντος με του χρήστη είναι σε ακτίνα 200 μέτρων
    private boolean isWithinRadius(double lat1, double lng1, double lat2, double lng2, int radiusInMeters) {
        float[] results = new float[1];
        Location.distanceBetween(lat1, lng1, lat2, lng2, results);
        Log.d("MainActivity", "Distance calculated: " + results[0] + " meters");
        //αν ισχυει επιστρέφει true
        return results[0] <= radiusInMeters;
    }

    //μέθοδος για εμφάνιση notification με ανακατεύθυνση σε άλλη Activity
    @SuppressLint("MissingPermission")
    private void showNotification(String title, String message, Context context, Class<?> destinationActivity, Product productObject, String key) {
        Intent intent = new Intent(context, destinationActivity);
        intent.putExtra("productCode", productObject.getCode());
        intent.putExtra("orderKey", key);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,  PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.baseline_notification_important_24)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                NotificationChannel channel = new NotificationChannel(
                        CHANNEL_ID,
                        "Nearby Products Notifications",
                        NotificationManager.IMPORTANCE_HIGH
                );
                channel.setDescription("Notifications for nearby products");
                notificationManager.createNotificationChannel(channel);
            }
        }

        // Ελέγχει για POST_NOTIFICATIONS δικαιώματα που χρειάζετι για Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                // Ζητάει POST_NOTIFICATIONS δικαιώματα
                ActivityCompat.requestPermissions((Activity) context,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
                return;
            }
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        int uniqueNotificationId = (int) System.currentTimeMillis(); //δημιουργεί μοναδικό id για το notification
        notificationManager.notify(uniqueNotificationId, builder.build());
    }

    //δημιουργεί notification channel
    private void createNotificationChannel(){

        CharSequence channelName = "Nearby Products Notifications";
        String channelDescription = "Notifications for nearby products";

        int importance = NotificationManager.IMPORTANCE_HIGH;

        NotificationChannel channel = new NotificationChannel(CHANNEL_ID,channelName,importance);
        channel.setDescription(channelDescription);

        if (notificationManager != null) {
            notificationManager.createNotificationChannel(channel);
            Log.d("MainActivity", "Notification channel created.");
        } else {
            Log.e("MainActivity", "NotificationManager is null.");
        }

    }


}