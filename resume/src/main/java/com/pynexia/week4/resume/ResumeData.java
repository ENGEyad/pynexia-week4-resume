package com.pynexia.week4.resume;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.LinkedHashMap;
import java.util.Map;

final class ResumeData {
    static final String[] KEYS = {"full_name", "headline", "email", "phone", "location", "summary", "education", "experience", "skills"};
    static final String[] LABELS = {"Full name", "Professional title", "Email", "Phone", "Location", "Profile summary", "Education", "Work experience", "Skills"};
    private final SharedPreferences preferences;

    ResumeData(Context context) {
        preferences = context.getSharedPreferences("resume_draft", Context.MODE_PRIVATE);
    }

    Map<String, String> read() {
        Map<String, String> values = new LinkedHashMap<>();
        for (int i = 0; i < KEYS.length; i++) values.put(KEYS[i], preferences.getString(KEYS[i], ""));
        return values;
    }

    void save(String key, String value) {
        preferences.edit().putString(key, value.trim()).apply();
    }

    void clear() {
        preferences.edit().clear().apply();
    }

    static String value(Map<String, String> values, String key) {
        String value = values.get(key);
        return value == null ? "" : value.trim();
    }
}
