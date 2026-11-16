package com.pynexia.week4.resume;

import java.util.Map;
import java.util.regex.Pattern;

public final class ResumeValidator {
    private static final Pattern EMAIL = Pattern.compile("^[A-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[A-Z0-9](?:[A-Z0-9-]{0,61}[A-Z0-9])?(?:\\.[A-Z0-9](?:[A-Z0-9-]{0,61}[A-Z0-9])?)+$", Pattern.CASE_INSENSITIVE);

    private ResumeValidator() { }

    public static boolean isValidName(String name) { return name != null && name.trim().length() >= 2; }
    public static boolean isValidEmail(String email) { return email == null || email.trim().isEmpty() || EMAIL.matcher(email.trim()).matches(); }
    public static boolean hasResumeContent(Map<String, String> values) {
        return notBlank(values.get("skills")) || notBlank(values.get("education")) || notBlank(values.get("experience"));
    }
    private static boolean notBlank(String value) { return value != null && !value.trim().isEmpty(); }
}
