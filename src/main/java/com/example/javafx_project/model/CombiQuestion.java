package com.example.javafx_project.model;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class CombiQuestion implements Question {
    private final QuestionType type;
    private final String title;

    // MULTIPLE
    private final List<String> choices;   // null for BOOLEAN
    private final String correctText;     // used when type = MULTIPLE

    // BOOLEAN
    private final boolean correctBool;    // used when type = BOOLEAN

    private final int timeLimit;

    /**
     * Factory for MULTIPLE-CHOICE questions.
     */
    public static CombiQuestion multiple(String title, List<String> choices, String correctText, int timeLimit) {
        Objects.requireNonNull(title, "title");
        Objects.requireNonNull(choices, "choices");
        Objects.requireNonNull(correctText, "correctText");
        if (!choices.contains(correctText)) {
            throw new IllegalArgumentException("correctText must be one of the provided choices");
        }
        return new CombiQuestion(QuestionType.MULTIPLE, title, List.copyOf(choices), correctText, false, timeLimit);
    }

    /**
     * Factory for BOOLEAN questions.
     */
    public static CombiQuestion bool(String title, boolean correct, int timeLimit) {
        Objects.requireNonNull(title, "title");
        return new CombiQuestion(QuestionType.BOOLEAN, title, null, null, correct, timeLimit);
    }

    /**
     * Unified private ctor; prefer using the two factories above.
     */
    private CombiQuestion(QuestionType type, String title,
                          List<String> choices, String correctText, boolean correctBool, int timeLimit) {
        this.type = Objects.requireNonNull(type, "type");
        this.title = Objects.requireNonNull(title, "title");
        this.choices = (choices == null ? null : List.copyOf(choices));
        this.correctText = correctText;
        this.correctBool = correctBool;
        this.timeLimit = Math.max(0, timeLimit);

        // validate cross-fields
        if (type == QuestionType.MULTIPLE) {
            if (this.choices == null || this.choices.isEmpty())
                throw new IllegalArgumentException("MULTIPLE requires non-empty choices");
            if (this.correctText == null)
                throw new IllegalArgumentException("MULTIPLE requires correctText");
        } else { // BOOLEAN
            if (this.choices != null || this.correctText != null) {
                // we tolerate but ignore, just a guard
            }
        }
    }

    // ---- Question API

    @Override public String getTitle()     { return title; }
    @Override public int getTimeLimit()    { return timeLimit; }
    @Override public QuestionType getType(){ return type; }

    @Override
    public List<String> getChoices() {
        return type == QuestionType.MULTIPLE ? choices : List.of("True", "False");
    }

    @Override
    public boolean isCorrect(Object a) {
        if (type == QuestionType.MULTIPLE) {
            if (a instanceof String s) {
                return safeEq(correctText, s);
            }
            // be forgiving if the UI passed a toggle's text object
            return a != null && safeEq(correctText, a.toString());
        } else { // BOOLEAN
            Boolean parsed = toBoolean(a);
            return parsed != null && parsed == correctBool;
        }
    }

    // ---- helpers

    private static boolean safeEq(String a, String b) {
        return a != null && b != null && a.trim().equals(b.trim());
    }

    /**
     * Parses many forms to boolean: Boolean, "true/false", "t/f", "yes/no", "y/n", "1/0".
     */
    private static Boolean toBoolean(Object a) {
        if (a instanceof Boolean b) return b;
        if (a instanceof String s) {
            String v = s.trim().toLowerCase();
            switch (v) {
                case "true", "t", "yes", "y", "1":  return Boolean.TRUE;
                case "false","f","no","n","0":      return Boolean.FALSE;
            }
        }
        return null;
    }
}
