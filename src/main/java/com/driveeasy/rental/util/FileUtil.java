package com.driveeasy.rental.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper methods for turning objects into one line of text and back.
 * Each record is stored as one line, with fields separated by "|".
 * Special characters inside a field are escaped so they never break the format.
 */
public final class FileUtil {

    public static final String SEPARATOR = "|";

    private FileUtil() {
        // Utility class - no objects needed
    }

    /** Joins values into one line, escaping each value. */
    public static String join(Object... values) {
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            if (i > 0) {
                line.append(SEPARATOR);
            }
            line.append(escape(values[i] == null ? "" : String.valueOf(values[i])));
        }
        return line.toString();
    }

    /** Splits a stored line back into its (unescaped) fields. */
    public static String[] split(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean escaping = false;
        for (char ch : line.toCharArray()) {
            if (escaping) {
                switch (ch) {
                    case 'p' -> current.append('|');
                    case 'n' -> current.append('\n');
                    default -> current.append(ch);
                }
                escaping = false;
            } else if (ch == '\\') {
                escaping = true;
            } else if (ch == '|') {
                fields.add(current.toString());
                current.setLength(0);
            } else {
                current.append(ch);
            }
        }
        fields.add(current.toString());
        return fields.toArray(new String[0]);
    }

    private static String escape(String value) {
        return value.replace("\\", "\\\\")
                .replace("|", "\\p")
                .replace("\r", "")
                .replace("\n", "\\n");
    }
}
