package com.jabat.personal.unipiplishopping;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends BaseActivity {

    EditText signupName, signupSurname, signupUsername, signupPassword;
    TextView loginRedirectText;
    Button signupButton;
    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.signup), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        applyBackgroundColor(R.id.signup);

        FirebaseApp.initializeApp(this);

        signupName = findViewById(R.id.signup_name);
        signupSurname = findViewById(R.id.signup_surname);
        signupUsername = findViewById(R.id.signup_username);
        signupPassword = findViewById(R.id.signup_password);
        loginRedirectText = findViewById(R.id.loginRedirectText);
        signupButton = findViewById(R.id.signup_button);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                database = FirebaseDatabase.getInstance();
                reference = database.getReference("users");

                String name = signupName.getText().toString();
                String surname = signupSurname.getText().toString();
                String username = signupUsername.getText().toString();
                String password = signupPassword.getText().toString();

                SignUpHelper helperClass = new SignUpHelper(name, surname, username, password);
                reference.child(username).setValue(helperClass)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(SignupActivity.this, R.string.toast_signup_success, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(SignupActivity.this, R.string.toast_signup_fail + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                        .addOnFailureListener(e -> {
                            Log.e("Firebase", "Error: " + e.getMessage());
                            Toast.makeText(SignupActivity.this, "Write failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        });

        //ανακατεύθυνση σε login activity
        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}