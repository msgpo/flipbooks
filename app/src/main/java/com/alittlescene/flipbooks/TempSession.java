package com.alittlescene.flipbooks;

/**
 * Created by Sean on 4/3/15.
 * This is a temporary class to manage local session.
 * Will be removed and reworked accordingly once the project has a server.
 */

public class TempSession {
    private static String username;
    private static boolean loggedIn;

    public TempSession (String username) {
        this.username = username;
        this.loggedIn = false;
    }

    public static String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public static boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

}
