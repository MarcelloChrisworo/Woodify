package com.woodify.service.impl;

import com.woodify.config.SessionManager;
import com.woodify.exception.AuthenticationException;
import com.woodify.model.User;
import com.woodify.repository.UserRepository;
import com.woodify.repository.impl.UserRepositoryImpl;
import com.woodify.service.AuthService;

public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;

    public AuthServiceImpl() {
        this.userRepository = new UserRepositoryImpl();
    }

    public AuthServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User authenticate(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            throw new AuthenticationException("Username tidak boleh kosong.");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new AuthenticationException("Password tidak boleh kosong.");
        }

        User user = userRepository.findByUsername(username);
        
        if (user == null) {
            throw new AuthenticationException("Username tidak ditemukan.");
        }
        
        // PBO Validation: Sederhana (bisa kembangkan dengan hash jika perlu, sekarang plain text)
        if (!user.getPassword().equals(password)) {
            throw new AuthenticationException("Password salah.");
        }

        // Simpan ke sesi global
        SessionManager.login(user);
        
        return user;
    }
}
