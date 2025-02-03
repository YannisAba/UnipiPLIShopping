package com.jabat.personal.unipiplishopping;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SettingsActivity extends BaseActivity {

    private Switch themeSwitch;
    private Spinner languageSpinner;

    private ArrayAdapter<String> adapter;
    private ImageView backImg;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setContentView(R.layout.activity_settings);
        themeSwitch = findViewById(R.id.themeSwitch);
        languageSpinner = findViewById(R.id.languageSpinner);

        //φόρτωση SharedPreferences για τις ρυθμίσεις της εφαρμογής
        sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        applyBackgroundColor(R.id.main);
        editor = sharedPreferences.edit();

        //φόρτωση γλωσσών από τα resources
        String[] languages = getResources().getStringArray(R.array.languages);
        adapter = new ArrayAdapter<>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, languages);
        languageSpinner.setAdapter(adapter);

        //ανακτά την τρέχουσα γλώσσα από τις προτιμήσεις
        String currentLanguage = sharedPreferences.getString("language", "en");
        switch (currentLanguage) {
            case "el":
                languageSpinner.setSelection(1);
                break;
            case "fr":
                languageSpinner.setSelection(2);
                break;
            default:
                languageSpinner.setSelection(0);
        }

        //όταν αλλάζει η γλώσσα
        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String selectedLanguage = "en";
                if (position == 1) {
                    selectedLanguage = "el";
                } else if (position == 2) {
                    selectedLanguage = "fr";
                }

                //αποθηκεύει την επιλογή
                if (!selectedLanguage.equals(currentLanguage)) {
                    editor.putString("language", selectedLanguage);
                    editor.apply();

                    //ενημερώνει την γλώσσα και το activity
                    LocaleHelper.setLocale(SettingsActivity.this, selectedLanguage);
                    recreate();
                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });




        backImg = findViewById(R.id.backImg);

        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                intent.putExtra("name", User.getName());
                intent.putExtra("username", User.getUsername());
                startActivity(intent);
            }
        });

        //default τιμή και listener όταν αλλάζει το theme
        themeSwitch.setChecked(sharedPreferences.getBoolean("isDarkTheme", false));
        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            editor.putBoolean("isDarkTheme", isChecked);
            editor.apply();

            recreate();
        });


    }
}