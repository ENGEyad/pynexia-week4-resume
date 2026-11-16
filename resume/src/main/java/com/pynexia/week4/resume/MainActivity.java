package com.pynexia.week4.resume;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Map;

public final class MainActivity extends Activity {
    private static final int INK = Color.rgb(29, 48, 44);
    private static final int MUTED = Color.rgb(103, 119, 114);
    private static final int GREEN = Color.rgb(21, 139, 121);
    private ResumeData data;
    private int step;
    private LinearLayout fields;
    private TextView stepLabel;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(Color.rgb(245, 247, 246));
        getWindow().setNavigationBarColor(Color.WHITE);
        data = new ResumeData(this);
        step = state == null ? 0 : state.getInt("step", 0);
        render();
    }

    @Override protected void onSaveInstanceState(Bundle state) {
        state.putInt("step", step);
        super.onSaveInstanceState(state);
    }

    private void render() {
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setPadding(dp(24), dp(20), dp(24), dp(28));
        page.setBackgroundColor(Color.rgb(245, 247, 246));
        scroll.addView(page);

        TextView brand = text("RESUME STUDIO", 12, GREEN, true);
        brand.setLetterSpacing(.12f);
        page.addView(brand);
        page.addView(text("Build your next chapter", 27, INK, true), margin(0, 8, 0, 6));
        page.addView(text("A polished resume, made one step at a time.", 15, MUTED, false), margin(0, 0, 0, 22));

        LinearLayout progress = new LinearLayout(this);
        progress.setGravity(Gravity.CENTER_VERTICAL);
        progress.setPadding(dp(16), dp(14), dp(16), dp(14));
        progress.setBackground(roundRect(Color.WHITE, dp(16)));
        page.addView(progress, margin(0, 0, 0, 20));
        stepLabel = text(getString(R.string.step_count, step + 1), 12, GREEN, true);
        progress.addView(stepLabel, new LinearLayout.LayoutParams(0, -2, 1));
        progress.addView(text(new String[]{"Profile", "Experience", "Skills & review"}[step], 13, INK, true));

        TextView section = text(new String[]{"About you", "Your background", "Final details"}[step], 20, INK, true);
        page.addView(section, margin(0, 0, 0, 6));
        page.addView(text(new String[]{"Start with the details employers need to reach you.", "Add education and the work that shaped your experience.", "List your strongest skills, then preview your resume."}[step], 14, MUTED, false), margin(0, 0, 0, 18));

        fields = new LinearLayout(this);
        fields.setOrientation(LinearLayout.VERTICAL);
        page.addView(fields);
        switch (step) {
            case 0:
                addField("full_name", "Full name", "e.g. Alex Morgan", false, false);
                addField("headline", "Professional title", "e.g. Android Developer", false, false);
                addField("email", "Email address", "alex@example.com", false, false);
                addField("phone", "Phone number", "+1 555 0100", false, false);
                addField("location", "Location", "City, Country", false, false);
                addField("summary", "Profile summary", "A short introduction about your strengths and goals.", true, false);
                break;
            case 1:
                addField("education", "Education", "Degree or course · Institution · Year\nOne entry per line", true, true);
                addField("experience", "Work experience", "Role · Company · Dates\nImpact and responsibilities on the next line.\nOne role per section.", true, true);
                break;
            default:
                addField("skills", "Skills", "Separate skills with commas, e.g. Kotlin, UI design, teamwork", true, false);
                break;
        }

        LinearLayout actions = new LinearLayout(this);
        actions.setOrientation(LinearLayout.HORIZONTAL);
        page.addView(actions, margin(0, 16, 0, 0));
        if (step > 0) {
            Button back = button("Back", false);
            actions.addView(back, new LinearLayout.LayoutParams(0, dp(54), 1));
            back.setOnClickListener(v -> { saveVisibleFields(); step--; render(); });
            addGap(actions, 10);
        }
        Button next = button(step == 2 ? "Preview resume" : "Continue", true);
        actions.addView(next, new LinearLayout.LayoutParams(0, dp(54), 2));
        next.setOnClickListener(v -> advance());
        if (step == 2) {
            TextView clear = text("Clear saved draft", 13, MUTED, true);
            clear.setGravity(Gravity.CENTER);
            page.addView(clear, margin(0, 18, 0, 0));
            clear.setOnClickListener(v -> {
                data.clear(); step = 0; render();
                Toast.makeText(this, "Draft cleared", Toast.LENGTH_SHORT).show();
            });
        }
        setContentView(scroll);
    }

    private void advance() {
        saveVisibleFields();
        Map<String, String> values = data.read();
        if (step == 0) {
            String name = ResumeData.value(values, "full_name");
            String email = ResumeData.value(values, "email");
            if (!ResumeValidator.isValidName(name)) { invalid("Enter your full name"); return; }
            if (!ResumeValidator.isValidEmail(email)) { invalid("Enter a valid email address"); return; }
        }
        hideKeyboard();
        if (step < 2) { step++; render(); }
        else {
            if (!ResumeValidator.hasResumeContent(values)) {
                invalid("Add at least one education, experience, or skill entry"); return;
            }
            startActivity(new Intent(this, PreviewActivity.class));
        }
    }

    private void invalid(String message) { Toast.makeText(this, message, Toast.LENGTH_LONG).show(); }

    private void saveVisibleFields() {
        if (fields == null) return;
        for (int i = 0; i < fields.getChildCount(); i++) {
            View child = fields.getChildAt(i);
            if (child instanceof EditText) {
                EditText edit = (EditText) child;
                String key = (String) edit.getTag();
                data.save(key, edit.getText().toString());
            }
        }
    }

    private void addField(String key, String label, String hint, boolean multiline, boolean tall) {
        fields.addView(text(label, 13, INK, true), margin(2, 0, 0, 7));
        EditText input = new EditText(this);
        input.setTag(key);
        input.setSingleLine(!multiline);
        input.setTextSize(15);
        input.setTextColor(INK);
        input.setHintTextColor(Color.rgb(145, 158, 153));
        input.setHint(hint);
        input.setPadding(dp(14), dp(12), dp(14), dp(12));
        input.setBackground(roundRect(Color.WHITE, dp(12)));
        if (key.equals("email")) input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        else if (key.equals("phone")) input.setInputType(InputType.TYPE_CLASS_PHONE);
        else if (multiline) { input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES); input.setGravity(Gravity.TOP | Gravity.START); }
        input.setMinHeight(dp(tall ? 132 : multiline ? 100 : 52));
        input.setText(data.read().get(key));
        input.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { data.save(key, s.toString()); }
            @Override public void afterTextChanged(Editable editable) { }
        });
        fields.addView(input, margin(0, 0, 0, 14));
    }

    private Button button(String title, boolean primary) {
        Button result = new Button(this);
        result.setText(title);
        result.setAllCaps(false);
        result.setTextSize(15);
        result.setTextColor(primary ? Color.WHITE : INK);
        result.setBackground(roundRect(primary ? GREEN : Color.WHITE, dp(14)));
        return result;
    }

    private TextView text(String value, int size, int color, boolean bold) {
        TextView view = new TextView(this);
        view.setText(value); view.setTextSize(size); view.setTextColor(color);
        if (bold) view.setTypeface(null, android.graphics.Typeface.BOLD);
        return view;
    }

    private android.graphics.drawable.GradientDrawable roundRect(int color, int radius) {
        android.graphics.drawable.GradientDrawable shape = new android.graphics.drawable.GradientDrawable();
        shape.setColor(color); shape.setCornerRadius(radius); return shape;
    }

    private LinearLayout.LayoutParams margin(int left, int top, int right, int bottom) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.setMargins(dp(left), dp(top), dp(right), dp(bottom)); return lp;
    }

    private void addGap(LinearLayout parent, int width) { View gap = new View(this); parent.addView(gap, new LinearLayout.LayoutParams(dp(width), 1)); }
    private int dp(int value) { return (int) (value * getResources().getDisplayMetrics().density + .5f); }

    private void hideKeyboard() {
        View current = getCurrentFocus();
        if (current != null) ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(current.getWindowToken(), 0);
    }
}
