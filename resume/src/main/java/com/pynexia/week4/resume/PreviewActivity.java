package com.pynexia.week4.resume;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public final class PreviewActivity extends Activity {
    private static final int CREATE_PDF = 20;
    private static final int INK = Color.rgb(29, 48, 44);
    private static final int MUTED = Color.rgb(103, 119, 114);
    private static final int GREEN = Color.rgb(21, 139, 121);
    private ResumeData data;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        data = new ResumeData(this);
        render();
    }

    private void render() {
        Map<String, String> values = data.read();
        ScrollView scroll = new ScrollView(this);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL); root.setPadding(dp(22), dp(18), dp(22), dp(24)); root.setBackgroundColor(Color.rgb(245, 247, 246));
        scroll.addView(root);
        TextView title = text("RESUME PREVIEW", 12, GREEN, true); title.setLetterSpacing(.1f); root.addView(title);
        root.addView(text(value(values, "full_name", "Your name"), 29, INK, true), margin(0, 12, 0, 4));
        root.addView(text(value(values, "headline", "Professional title"), 16, GREEN, true), margin(0, 0, 0, 8));
        String contact = join(value(values, "email", ""), value(values, "phone", ""), value(values, "location", ""));
        if (!contact.isEmpty()) root.addView(text(contact, 13, MUTED, false), margin(0, 0, 0, 16));
        addSection(root, "PROFILE", value(values, "summary", ""));
        addSection(root, "EXPERIENCE", value(values, "experience", ""));
        addSection(root, "EDUCATION", value(values, "education", ""));
        addSection(root, "SKILLS", value(values, "skills", ""));
        Button download = new Button(this); download.setText(R.string.download_pdf); download.setAllCaps(false); download.setTextSize(15); download.setTextColor(Color.WHITE); download.setBackground(roundRect(GREEN, dp(14)));
        root.addView(download, margin(0, 22, 0, 0));
        download.setOnClickListener(v -> launchPdfPicker(values));
        Button edit = new Button(this); edit.setText(R.string.edit_resume); edit.setAllCaps(false); edit.setTextColor(INK); edit.setBackground(roundRect(Color.WHITE, dp(14)));
        root.addView(edit, margin(0, 8, 0, 0)); edit.setOnClickListener(v -> finish());
        setContentView(scroll);
    }

    private void addSection(LinearLayout root, String heading, String body) {
        if (body.trim().isEmpty()) return;
        View divider = new View(this); divider.setBackgroundColor(Color.rgb(219, 228, 224)); root.addView(divider, new LinearLayout.LayoutParams(-1, dp(1)));
        root.addView(text(heading, 12, GREEN, true), margin(0, 14, 0, 7));
        root.addView(text(body, 15, INK, false), margin(0, 0, 0, 15));
    }

    private void launchPdfPicker(Map<String, String> values) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE); intent.setType("application/pdf");
        String filename = value(values, "full_name", "Resume").replaceAll("[^A-Za-z0-9_-]", "_");
        intent.putExtra(Intent.EXTRA_TITLE, filename + "_Resume.pdf");
        startActivityForResult(intent, CREATE_PDF);
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        super.onActivityResult(requestCode, resultCode, result);
        if (requestCode != CREATE_PDF || resultCode != RESULT_OK || result == null || result.getData() == null) return;
        try (OutputStream output = getContentResolver().openOutputStream(result.getData())) {
            if (output == null) throw new IOException("Could not open the selected destination");
            writePdf(output, data.read());
            Toast.makeText(this, "Resume PDF saved", Toast.LENGTH_LONG).show();
        } catch (IOException error) {
            Toast.makeText(this, "Could not save PDF: " + error.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void writePdf(OutputStream output, Map<String, String> values) throws IOException {
        PdfDocument document = new PdfDocument();
        PdfWriter writer = new PdfWriter(document);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        String contact = join(value(values, "email", ""), value(values, "phone", ""), value(values, "location", ""));
        paint.setColor(INK); paint.setTypeface(android.graphics.Typeface.create("sans-serif", android.graphics.Typeface.BOLD)); paint.setTextSize(25);
        writer.addText(value(values, "full_name", "Your name"), paint, 32);
        paint.setColor(GREEN); paint.setTextSize(14); paint.setTypeface(android.graphics.Typeface.create("sans-serif-medium", android.graphics.Typeface.NORMAL));
        writer.addText(value(values, "headline", ""), paint, 21);
        paint.setColor(MUTED); paint.setTextSize(10); paint.setTypeface(android.graphics.Typeface.DEFAULT);
        writer.addText(contact, paint, 16);
        String[][] sections = {{"PROFILE", "summary"}, {"EXPERIENCE", "experience"}, {"EDUCATION", "education"}, {"SKILLS", "skills"}};
        for (String[] section : sections) {
            String body = value(values, section[1], "");
            if (body.isEmpty()) continue;
            paint.setColor(GREEN); paint.setTextSize(11); paint.setTypeface(android.graphics.Typeface.create("sans-serif", android.graphics.Typeface.BOLD));
            writer.addText(section[0], paint, 27);
            paint.setColor(INK); paint.setTextSize(11); paint.setTypeface(android.graphics.Typeface.DEFAULT);
            writer.addText(body, paint, 17);
            writer.addSpace(12);
        }
        writer.finish();
        try { document.writeTo(output); } finally { document.close(); }
    }

    private static final class PdfWriter {
        private static final int WIDTH = 595, HEIGHT = 842, LEFT = 52, RIGHT = WIDTH - 52, BOTTOM = HEIGHT - 54;
        private final PdfDocument document;
        private PdfDocument.Page page;
        private Canvas canvas;
        private int pageNumber, y = 70;

        PdfWriter(PdfDocument document) { this.document = document; startPage(); }

        void addText(String text, Paint paint, int lineHeight) {
            if (text == null || text.trim().isEmpty()) return;
            for (String paragraph : text.split("\\n", -1)) {
                String line = "";
                for (String word : paragraph.trim().split("\\s+")) {
                    if (word.isEmpty()) continue;
                    String candidate = line.isEmpty() ? word : line + " " + word;
                    if (paint.measureText(candidate) > RIGHT - LEFT && !line.isEmpty()) {
                        drawLine(line, paint, lineHeight);
                        line = word;
                    } else line = candidate;
                }
                if (!line.isEmpty()) drawLine(line, paint, lineHeight);
                else if (paragraph.isEmpty()) addSpace(lineHeight / 2);
            }
        }

        void addSpace(int amount) { y += amount; }

        private void drawLine(String line, Paint paint, int lineHeight) {
            if (y + lineHeight > BOTTOM) startPage();
            canvas.drawText(line, LEFT, y, paint);
            y += lineHeight;
        }

        private void startPage() {
            if (page != null) document.finishPage(page);
            pageNumber++;
            page = document.startPage(new PdfDocument.PageInfo.Builder(WIDTH, HEIGHT, pageNumber).create());
            canvas = page.getCanvas();
            Paint accent = new Paint(Paint.ANTI_ALIAS_FLAG);
            accent.setColor(GREEN);
            canvas.drawRect(0, 0, WIDTH, 15, accent);
            y = 70;
        }

        void finish() { if (page != null) { document.finishPage(page); page = null; } }
    }

    private String value(Map<String, String> values, String key, String fallback) { String value = ResumeData.value(values, key); return value.isEmpty() ? fallback : value; }
    private String join(String a, String b, String c) { String result = a; if (!b.isEmpty()) result += (result.isEmpty() ? "" : "   ·   ") + b; if (!c.isEmpty()) result += (result.isEmpty() ? "" : "   ·   ") + c; return result; }
    private TextView text(String value, int size, int color, boolean bold) { TextView view = new TextView(this); view.setText(value); view.setTextSize(size); view.setTextColor(color); if (bold) view.setTypeface(null, android.graphics.Typeface.BOLD); return view; }
    private LinearLayout.LayoutParams margin(int left, int top, int right, int bottom) { LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2); lp.setMargins(dp(left), dp(top), dp(right), dp(bottom)); return lp; }
    private android.graphics.drawable.GradientDrawable roundRect(int color, int radius) { android.graphics.drawable.GradientDrawable shape = new android.graphics.drawable.GradientDrawable(); shape.setColor(color); shape.setCornerRadius(radius); return shape; }
    private int dp(int value) { return (int) (value * getResources().getDisplayMetrics().density + .5f); }
}
