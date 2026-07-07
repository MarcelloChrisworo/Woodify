package com.woodify.view.product.access;

import com.woodify.config.SessionManager;
import com.woodify.model.User;

public final class ProdukAccessPolicy {
    private static final String READ_ONLY_MESSAGE = "Mode Lihat-Saja (Akses Kasir Terbatas)";

    private ProdukAccessPolicy() {
    }

    public static boolean canManageProducts() {
        User currentUser = SessionManager.getCurrentUser();
        return currentUser != null && currentUser.hasAccessToProductManagement();
    }

    public static String getReadOnlyMessage() {
        return READ_ONLY_MESSAGE;
    }
}