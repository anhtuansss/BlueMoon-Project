package com.bluemoon.service.impl;

import com.bluemoon.dto.request.LoginRequest;
import com.bluemoon.dto.response.LoginResponse;
import com.bluemoon.dto.response.UserInfoDto;
import com.bluemoon.entity.Account;
import com.bluemoon.exception.AppException;
import com.bluemoon.repository.AccountRepository;
import com.bluemoon.security.JwtTokenProvider;
import com.bluemoon.security.UserPrincipal;
import com.bluemoon.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public AuthServiceImpl(AccountRepository accountRepository,
                           PasswordEncoder passwordEncoder,
                           JwtTokenProvider tokenProvider) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    @Override
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        // Tìm tài khoản theo username
        Account account = accountRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException("INVALID_CREDENTIALS", "Tên đăng nhập hoặc mật khẩu không chính xác", HttpStatus.UNAUTHORIZED));

        if (Boolean.FALSE.equals(account.getIsActive())) {
            throw new AppException("ACCOUNT_LOCKED", "Tài khoản đã bị khóa hoặc ngừng hoạt động", HttpStatus.FORBIDDEN);
        }

        // Kiểm tra mật khẩu (hỗ trợ BCrypt hoặc mật khẩu tạm demo ban đầu)
        boolean passwordMatches = passwordEncoder.matches(request.getPassword(), account.getPasswordHash())
                || request.getPassword().equals(account.getPasswordHash());

        if (!passwordMatches) {
            throw new AppException("INVALID_CREDENTIALS", "Tên đăng nhập hoặc mật khẩu không chính xác", HttpStatus.UNAUTHORIZED);
        }

        Long personId = account.getPerson() != null ? account.getPerson().getPersonId() : null;
        String fullName = account.getPerson() != null ? account.getPerson().getFullName() : account.getUsername();

        UserPrincipal principal = new UserPrincipal(
                account.getAccountId(),
                personId,
                account.getUsername(),
                account.getPasswordHash(),
                account.getRole()
        );

        String token = tokenProvider.generateToken(principal);

        // TODO [Nguyên - Week 1]: Lưu lịch sử đăng nhập / kiểm tra refresh token nếu cần
        return new LoginResponse(token, account.getAccountId(), account.getUsername(), account.getRole(), fullName);
    }

    @Override
    @Transactional(readOnly = true)
    public UserInfoDto getCurrentUser(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AppException("ACCOUNT_NOT_FOUND", "Không tìm thấy thông tin tài khoản", HttpStatus.NOT_FOUND));

        Long personId = account.getPerson() != null ? account.getPerson().getPersonId() : null;
        String fullName = account.getPerson() != null ? account.getPerson().getFullName() : account.getUsername();
        String email = account.getPerson() != null ? account.getPerson().getEmail() : null;
        String phone = account.getPerson() != null ? account.getPerson().getPhone() : null;

        // TODO [Nguyên - Week 1]: Bổ sung thông tin căn hộ hiện tại cho cư dân nếu có
        return new UserInfoDto(account.getAccountId(), personId, account.getUsername(), account.getRole(), fullName, email, phone);
    }

    @Override
    public void logout(String token) {
        // TODO [Nguyên - Week 1]: Thêm token vào blacklist (Redis/Memory) nếu cần vô hiệu hóa token lập tức
    }
}
