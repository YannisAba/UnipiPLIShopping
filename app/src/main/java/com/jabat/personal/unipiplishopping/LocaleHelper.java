package com.jabat.personal.unipiplishopping;

import android.content.Context;
import android.content.res.Configuration;

import java.util.Locale;

//παρέχει μια μέθοδο για την αλλαγή της γλώσσας της εφαρμογής δυναμικά.
public class LocaleHelper {
    public static void setLocale(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
    }

}
