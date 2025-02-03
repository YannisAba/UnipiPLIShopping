package com.jabat.personal.unipiplishopping;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

//ο χρήστης επεξεργάζεται το όνομα και το επώνυμο του, τα αποθηκεύσει στη βάση και επιστρέφει στην κύρια οθόνη.

public class ProfileActivity extends BaseActivity {

    EditText editName, editSurname;
    Button saveButton;
    String nameUser, surnameUser, usernameUser;
    ImageView backImage;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.editprofile), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        applyBackgroundColor(R.id.editprofile);

        reference = FirebaseDatabase.getInstance().getReference("users");

        editName = findViewById(R.id.editName);
        editSurname = findViewById(R.id.editSurname);
        saveButton = findViewById(R.id.saveButton);

        backImage = findViewById(R.id.backImg);

        //πίσω στην αρχική οθόνη
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                intent.putExtra("name", nameUser);
                intent.putExtra("username", usernameUser);
                startActivity(intent);
            }
        });

        //κλήση μεθόδου εμφάνισης δεδομένων
        showData();

        //Επεξεργασία δεδομένων όταν πατηθεί αποθήκευση
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reference.child(usernameUser).child("name").setValue(editName.getText().toString());
                nameUser = editName.getText().toString();
                reference.child(usernameUser).child("surname").setValue(editSurname.getText().toString());
                surnameUser = editSurname.getText().toString();
                Toast.makeText(ProfileActivity.this, R.string.toast_saved_name_surname, Toast.LENGTH_SHORT).show();
            }
        });


    }
    //περνάει δεδομένα απο το intent και τα εμφανίζει στα πεδία κειμένου.
    public void showData(){
        Intent intent = getIntent();
        nameUser = intent.getStringExtra("name");
        surnameUser = intent.getStringExtra("surname");
        usernameUser = intent.getStringExtra("username");
        editName.setText(nameUser);
        editSurname.setText(surnameUser);
    }
}