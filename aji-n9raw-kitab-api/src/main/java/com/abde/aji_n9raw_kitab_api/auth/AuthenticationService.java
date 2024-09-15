package com.abde.aji_n9raw_kitab_api.auth;

import com.abde.aji_n9raw_kitab_api.email.EmailService;
import com.abde.aji_n9raw_kitab_api.email.EmailTemplateName;
import com.abde.aji_n9raw_kitab_api.role.RoleRepo;
import com.abde.aji_n9raw_kitab_api.security.JwtService;
import com.abde.aji_n9raw_kitab_api.user.Token;
import com.abde.aji_n9raw_kitab_api.user.TokenRepo;
import com.abde.aji_n9raw_kitab_api.user.User;
import com.abde.aji_n9raw_kitab_api.user.UserRepo;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final JwtService jwtService;
    @Value("${application.mailing.frontend.activationUrl}")
    private String ActivationUrl;

    private final RoleRepo roleRepo;
    private final PasswordEncoder passwordEncoder;
    private final UserRepo userRepo;
    private final TokenRepo tokenRepo;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;


    public void register(RequestRegestration request) throws MessagingException {
        var userRole = roleRepo.findByName("USER")
                .orElseThrow(()-> new IllegalStateException("User role not found"));
        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .accountLocked(false)
                .enabled(false)
                .roles(List.of(userRole))
                .build();

        userRepo.save(user);
        sendValidationEmail(user);
    }

    private void sendValidationEmail(User user) throws MessagingException {
        var newToken = generateAndSaveActivationToken(user);
        // send email(
        emailService.sendEmail(
                user.getEmail(),
                user.getFullName(),
                EmailTemplateName.ACTIVATE_ACCOUNT,
                ActivationUrl,
                newToken,
                "Account activation"
        );
    }

    private String generateAndSaveActivationToken(User user) {
        String generatedToken = generatedActivationToken(6);
        var token = Token.builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .build();

        tokenRepo.save(token);

        return generatedToken;
    }

    private String generatedActivationToken(int length) {
        String characters = "0123456789";
        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();
        for(int i = 0; i < length; i++){
            int randomIndex = secureRandom.nextInt(characters.length());
            codeBuilder.append(characters.charAt(randomIndex));
        }
        return codeBuilder.toString();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var claims = new HashMap<String,Object>();
        var user = (User) auth.getPrincipal();
        claims.put("fullName", user.getFullName());
        var jwtToken = jwtService.generateToken(claims,user);
        return AuthenticationResponse.builder()
                .token(jwtToken).build();

    }


    public void activateAccount(String token) throws MessagingException {
        Token savedToken = tokenRepo.findByToken(token)
                .orElseThrow(()-> new IllegalStateException("Token not found"));
        if(LocalDateTime.now().isAfter(
                savedToken.getExpiresAt()
        )){
            sendValidationEmail(savedToken.getUser());
            throw new IllegalStateException("Token expired, A new token has been sent");
        }
        var user = userRepo.findById(savedToken.getUser().getId())
                .orElseThrow(()-> new IllegalStateException("User not found"));
        user.setEnabled(true);
        userRepo.save(user);
        savedToken.setValidatedAt(LocalDateTime.now());
        tokenRepo.save(savedToken);
    }
}
