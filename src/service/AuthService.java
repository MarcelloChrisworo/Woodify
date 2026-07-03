package com.woodify.service;

import com.woodify.model.User;

/**
 * Abstraction (Interface): Kontrak untuk layanan autentikasi pengguna.
 * Memisahkan kontrak dari implementasi (AuthServiceImpl).
 */
public interface AuthService {
    /**
     * Melakukan autentikasi pengguna berdasarkan username dan password.
     * @param username Username pengguna
     * @param password Password pengguna
     * @return User yang berhasil login (Owner atau Kasir)
     * @throws com.woodify.exception.AuthenticationException jika autentikasi gagal
     */
    User authenticate(String username, String password);
}
