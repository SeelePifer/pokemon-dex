package com.pokemondex.infrastructure.security;

import com.pokemondex.domain.model.AuthToken;
import com.pokemondex.domain.model.AuthenticatedUser;
import com.pokemondex.domain.model.Role;
import com.pokemondex.domain.model.UserAccount;
import com.pokemondex.domain.port.TokenIssuer;
import com.pokemondex.infrastructure.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Component
public class JwtTokenAdapter implements TokenIssuer {

    private final SecretKey key;
    private final long expirationSeconds;

    public JwtTokenAdapter(JwtProperties properties) {
        this.key = Keys.hmacShaKeyFor(properties.secret().getBytes(StandardCharsets.UTF_8));
        this.expirationSeconds = properties.expirationMinutes() * 60;
    }

    @Override
    public AuthToken issue(UserAccount user) {
        Instant now = Instant.now();
        String token = Jwts.builder()
                .subject(user.id().toString())
                .claim("email", user.email())
                .claim("role", user.role().name())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(expirationSeconds)))
                .signWith(key)
                .compact();
        return new AuthToken(token, expirationSeconds);
    }

    @Override
    public Optional<AuthenticatedUser> parse(String token) {
        try {
            Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
            return Optional.of(new AuthenticatedUser(
                    UUID.fromString(claims.getSubject()),
                    claims.get("email", String.class),
                    Role.valueOf(claims.get("role", String.class))
            ));
        } catch (RuntimeException exception) {
            return Optional.empty();
        }
    }
}
