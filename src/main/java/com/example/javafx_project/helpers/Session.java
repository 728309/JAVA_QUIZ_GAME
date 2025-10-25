package com.example.javafx_project.helpers;

import com.example.javafx_project.model.Player;

public final class Session {
    private Session() {}
    private static Player current;

    public static void setCurrent(Player p) { current = p; }
    public static Player getCurrent() { return current; }
    public static boolean isLoggedIn() { return current != null; }
    public static void logout() { current = null; }
}
