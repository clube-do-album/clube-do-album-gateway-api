package br.com.clubedoalbum.gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

  private final SecretKey secretKey;

  public JwtService(@Value("${app.jwt.secret}") String secret) {
    this.secretKey = Keys.hmacShaKeyFor(normalizeSecret(secret).getBytes(StandardCharsets.UTF_8));
  }

  public AuthenticatedUser validate(String token) {
    Claims claims = Jwts.parser()
        .verifyWith(secretKey)
        .build()
        .parseSignedClaims(token)
        .getPayload();

    return new AuthenticatedUser(claims.getSubject(), claims.get("email", String.class));
  }

  private String normalizeSecret(String secret) {
    if (secret == null || secret.length() < 32) {
      return "clube-do-album-local-development-secret-key-change-me";
    }

    return secret;
  }
}
