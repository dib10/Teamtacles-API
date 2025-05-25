package com.teamtacles.teamtacles_api.service;

import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;

import com.teamtacles.teamtacles_api.model.User;

@Service
public class JwtService {
    private final JwtEncoder jwtEncoder;

    public JwtService(JwtEncoder encoder) {
        this.jwtEncoder = encoder;
    }

    public String generateToken(User user) {
        Instant now = Instant.now();
        long expire = 3600L;

        var claims = JwtClaimsSet.builder()
                .issuer("spring-security")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expire))
                .subject(user.getUserName())
                .claim("userId", user.getUserId())
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

}
