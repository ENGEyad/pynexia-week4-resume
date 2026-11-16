package com.pynexia.week4.resume;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.HashMap;
import java.util.Map;

public class ResumeValidatorTest {
    @Test public void nameMustContainAtLeastTwoCharacters() {
        assertFalse(ResumeValidator.isValidName(" A "));
        assertTrue(ResumeValidator.isValidName(" Al "));
        assertFalse(ResumeValidator.isValidName(null));
    }

    @Test public void blankEmailIsOptionalButMalformedEmailIsRejected() {
        assertTrue(ResumeValidator.isValidEmail("  "));
        assertFalse(ResumeValidator.isValidEmail("person@"));
        assertTrue(ResumeValidator.isValidEmail("person@example.org"));
    }

    @Test public void resumeNeedsAtLeastOneContentSection() {
        Map<String, String> values = new HashMap<>();
        assertFalse(ResumeValidator.hasResumeContent(values));
        values.put("experience", "  "); assertFalse(ResumeValidator.hasResumeContent(values));
        values.put("skills", "Java"); assertTrue(ResumeValidator.hasResumeContent(values));
    }
}
