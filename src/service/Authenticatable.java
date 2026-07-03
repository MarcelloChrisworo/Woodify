package com.woodify.service;

import com.woodify.model.User;

public interface Authenticatable {
    User authenticate(String username, String password);
}
