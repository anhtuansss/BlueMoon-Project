package com.bluemoon.service;

import com.bluemoon.dto.request.LoginRequest;
import com.bluemoon.dto.response.LoginResponse;
import com.bluemoon.dto.response.UserInfoDto;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    UserInfoDto getCurrentUser(Long accountId);
    void logout(String token);
}
