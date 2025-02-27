package com.uyng.moneywise.auth;

import com.uyng.moneywise.email.EmailService;
import com.uyng.moneywise.email.EmailTemplateName;
import com.uyng.moneywise.exception.ActivationCodeException;
import com.uyng.moneywise.role.Role;
import com.uyng.moneywise.role.RoleRepository;
import com.uyng.moneywise.security.JwtService;
import com.uyng.moneywise.user.Token;
import com.uyng.moneywise.user.TokenRepository;
import com.uyng.moneywise.user.User;
import com.uyng.moneywise.user.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Value("${application.mailing.frontend.activation-url}")
    private String activationUrl;

    public void register(RegistrationRequest request) throws MessagingException {
        Role userRole = roleRepository
                .findByName("USER")
                .orElseThrow(() -> new IllegalStateException("ROLE USER was not initialized"));

        if (userRepository.findByEmail(request.email()).isPresent()) {

        }

        User user = User
                .builder()
                .firstname(request.firstname())
                .lastname(request.lastname())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .accountLocked(false)
                .enabled(false)
                .roles(List.of(userRole))
                .build();

        userRepository.save(user);
        sendValidationEmail(user);
    }

    private void sendValidationEmail(User user) throws MessagingException {
        String activationCode = generateAndSaveActivationCode(user);
        emailService.sendEmail(
                user.getEmail(),
                user.getFullName(),
                EmailTemplateName.ACTIVATE_ACCOUNT,
                activationUrl,
                activationCode,
                "Account Activation"
        );
    }

    private String generateAndSaveActivationCode(User user) {
        String activationCode = generateActivationCode(6);
        Token token = Token
                .builder()
                .activationCode(activationCode)
                .createdAt(LocalDateTime.now())
                .expiredAt(LocalDateTime.now().plusMinutes(1))
                .user(user)
                .build();

        tokenRepository.save(token);

        return activationCode;
    }

    private String generateActivationCode(int length) {
        final String[] characters = "0123456789".split("");
        StringBuilder codeBuilder = new StringBuilder(length);
        SecureRandom secureRandom = new SecureRandom();

        for (int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(characters.length);
            codeBuilder.append(characters[randomIndex]);
        }

        return codeBuilder.toString();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        User authenticatedUser = (User) authentication.getPrincipal();
        HashMap<String, Object> claims = new HashMap<>();
        claims.put("fullName", authenticatedUser.getFullName());

        String jwtToken = jwtService.generateToken(claims, authenticatedUser);
        return new AuthenticationResponse(jwtToken);
    }

//    @Transactional
    public void activateAccount(String activationCode) throws MessagingException {
        Token savedToken = tokenRepository.findByActivationCode(activationCode)
                // todo handle exception
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (LocalDateTime.now().isAfter(savedToken.getExpiredAt())) {
            sendValidationEmail(savedToken.getUser());
            throw new ActivationCodeException("Activation token has expired. A new token has been send to the same email address");
        }

        User user = savedToken.getUser();
        user.setEnabled(true);
        userRepository.save(user);

        savedToken.setValidatedAt(LocalDateTime.now());
        tokenRepository.save(savedToken);
    }
}
