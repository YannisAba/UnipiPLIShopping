package com.jabat.personal.unipiplishopping;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

//Παρέχει σε όλες τις activity που την κληρωνομούν τις λειτουργίες της AppCompatActivity και την επαφη με τα shared preferences για την γλώσσα και το φόντο.
public class BaseActivity extends AppCompatActivity {
    public SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_base);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);

        String language = sharedPreferences.getString("language", "en");

        LocaleHelper.setLocale(this, language);


    }

    //παίρνει το id απί το αντίστοιχο activity
    protected void applyBackgroundColor(int layoutId) {
        View layout = findViewById(layoutId);
        if (sharedPreferences.getBoolean("isDarkTheme", false)) {
            layout.setBackgroundColor(getColor(R.color.dark_blue));
        } else {
            layout.setBackgroundColor(getColor(R.color.blue));
        }
    }
}