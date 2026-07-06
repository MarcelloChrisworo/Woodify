package com.woodify.config;

import com.woodify.model.User;

public class SessionManager {
    private static User currentUser = null;

    private SessionManager() {
    }

    public static synchronized void login(User user) {
        currentUser = user;
        System.out.println("User login berhasil: " + user.getNamaLengkap() + " (" + user.getRole() + ")");
    }

    public static synchronized void logout() {
        if (currentUser != null) {
            System.out.println("User logout: " + currentUser.getNamaLengkap());
            currentUser = null;
        }
    }

    public static synchronized User getCurrentUser() {
        return currentUser;
    }

    public static synchronized boolean isLoggedIn() {
        return currentUser != null;
    }
}
