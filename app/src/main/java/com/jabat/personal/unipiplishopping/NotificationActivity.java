package com.jabat.personal.unipiplishopping;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class NotificationActivity extends BaseActivity {
    DatabaseReference ordersRef,productsRef;
    TextView title, description, price;
    Button btnCancelOrder;
    private Product product;

    String productCode;
    String orderKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notification);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_notification), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        applyBackgroundColor(R.id.activity_notification);

        // References to the orders and products nodes
         ordersRef = FirebaseDatabase.getInstance().getReference("orders");
         productsRef =  FirebaseDatabase.getInstance().getReference("products");

        title = findViewById(R.id.product_title);
        description = findViewById(R.id.product_description);
        price = findViewById(R.id.product_price);
        btnCancelOrder = findViewById(R.id.btn_cancel_order);

        Intent intent = getIntent();
        productCode = intent.getStringExtra("productCode");
        orderKey = intent.getStringExtra("orderKey");
        /*String productCodeEdited = productCode.getText().toString().trim();*/

        passProductData(productCode);


        btnCancelOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d("xxxxxxxxxxxxxxxxxxx", "PRODUCT CODE: "+ productCode + " | ORDER KEY: " +orderKey);

                if (orderKey != null && productCode != null) {
                    Log.d("xxxxxxxxxxxxxxxxxxx", "PRODUCT CODE: "+ productCode + " | ORDER KEY: " +orderKey);
                    // Step 1: Delete the order from "orders"
                    ordersRef.child(orderKey).removeValue().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Step 2: Update the product's availability in "products"
                            Log.d("xxxxxxxxxxxxxxxxxxx", "PRODUCT CODE: "+ productCode + " | ORDER KEY: " +orderKey);
                            productsRef.orderByChild("code").equalTo(productCode).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                                        // Update the "available" field to true
                                        Log.d("xxxxxxxxxxxxxxxxxxx", "PRODUCT CODE: "+ productCode + " | ORDER KEY: " +orderKey);
                                        productSnapshot.getRef().child("available").setValue(true);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(NotificationActivity.this, "Failed to update product availability", Toast.LENGTH_SHORT).show();
                                }
                            });

                            // Go back to the MainActivity

                            Intent intent = new Intent(NotificationActivity.this, MainActivity.class);
                            intent.putExtra("name", User.getName());
                            intent.putExtra("username", User.getUsername());
                            startActivity(intent);
                        } else {
                            Toast.makeText(NotificationActivity.this, "Failed to cancel order", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(NotificationActivity.this, "Order or product information is missing", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

     public void getLater(View view) {
         Intent intent = new Intent(NotificationActivity.this, MainActivity.class);
         intent.putExtra("name", User.getName());
         intent.putExtra("username", User.getUsername());
         startActivity(intent);
     }

    /*public void cancelOrder(View view) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        // References to the orders and products nodes
        DatabaseReference ordersRef = database.getReference("orders");
        DatabaseReference productsRef = database.getReference("products");

        Log.d("xxxxxxxxxxxxxxxxxxx", "PRODUCT CODE: "+ productCode + " | ORDER KEY: " +orderKey);

        if (orderKey != null && productCode != null) {
            Log.d("xxxxxxxxxxxxxxxxxxx", "PRODUCT CODE: "+ productCode + " | ORDER KEY: " +orderKey);
            // Step 1: Delete the order from "orders"
            ordersRef.child(orderKey).removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Step 2: Update the product's availability in "products"
                    productsRef.orderByChild("code").equalTo(productCode).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                                // Update the "available" field to true
                                productSnapshot.getRef().child("available").setValue(true);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(NotificationActivity.this, "Failed to update product availability", Toast.LENGTH_SHORT).show();
                        }
                    });

                    // Go back to the MainActivity

                    Intent intent = new Intent(NotificationActivity.this, MainActivity.class);
                    intent.putExtra("name", User.getName());
                    intent.putExtra("username", User.getUsername());
                    startActivity(intent);
                } else {
                    Toast.makeText(NotificationActivity.this, "Failed to cancel order", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Order or product information is missing", Toast.LENGTH_SHORT).show();
        }
    }*/

    public void passProductData(String productCode){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("products");
        Query checkProductDatabase = reference.orderByChild("code").equalTo(productCode);
        checkProductDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                        Product product = productSnapshot.getValue(Product.class);
                        if (product != null) {
                            // Update the UI
                            title.setText(product.getTitle());
                            description.setText(product.getDescription());
                            price.setText(NotificationActivity.this.getString(R.string.price_label) + " " + product.getPrice());
                        }
                       /* String titleFromDB = productSnapshot.child("title").getValue(String.class);
                        String descriptionFromDB = productSnapshot.child("description").getValue(String.class);
                        *//*String priceFromDB = productSnapshot.child("price").getValue(String.class);*//*
                        Object priceObject = productSnapshot.child("price").getValue();
                        String priceFromDB = priceObject != null ? priceObject.toString() : "0";
                        title.setText(titleFromDB);
                        description.setText(descriptionFromDB);
                        price.setText(NotificationActivity.this.getString(R.string.price_label) + " " + priceFromDB);*/
                    }

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

}