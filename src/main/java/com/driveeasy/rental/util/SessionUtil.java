package com.driveeasy.rental.util;

import jakarta.servlet.http.HttpSession;

/**
 * Keeps all session key names in one place.
 */
public final class SessionUtil {

    public static final String USER_ID = "loggedUserId";

    private SessionUtil() {
    }

    public static String getUserId(HttpSession session) {
        Object value = session.getAttribute(USER_ID);
        return value == null ? null : value.toString();
    }

    public static void login(HttpSession session, String userId) {
        session.setAttribute(USER_ID, userId);
    }

    public static void logout(HttpSession session) {
        session.invalidate();
    }
}
