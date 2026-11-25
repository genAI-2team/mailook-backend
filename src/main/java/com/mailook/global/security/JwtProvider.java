package com.mailook.global.security;

import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.stereotype.Component;

import com.mailook.user.entity.Login;
import com.nimbusds.jose.jwk.source.ImmutableSecret;

@Component
public class JwtProvider {
    private final SecretKey key;
    private final JwtEncoder encoder;

    public JwtProvider(@Value("${jwt.secret}") String secret) {
        byte[] bytes = Base64.getDecoder().decode(secret);
        if (bytes.length < 32)
            throw new IllegalArgumentException("JWT_SECRET must contain at least 32 random bytes encoded as Base64");
        key = new SecretKeySpec(bytes, "HmacSHA256");
        encoder = new NimbusJwtEncoder(new ImmutableSecret<>(key));
    }

    public String createToken(Login login) {
        Instant now = Instant.now();
        JwtClaimsSet.Builder claims = JwtClaimsSet.builder()
                .issuer("mailook-backend").subject(login.getEmail()).issuedAt(now)
                .id(UUID.randomUUID().toString()).claim("userId", login.getUserId());
        // 운영 전 아래 주석을 해제하면 발급 후 30분에 만료된다.
        // claims.expiresAt(now.plusSeconds(30 * 60));
        // 테스트 기간: exp를 넣지 않아 만료 시간 없이 발급한다.
        return encoder.encode(JwtEncoderParameters.from(
                JwsHeader.with(MacAlgorithm.HS256).build(), claims.build())).getTokenValue();
    }

    public JwtDecoder decoder() {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(key).macAlgorithm(MacAlgorithm.HS256).build();
        decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer("mailook-backend"));
        return decoder;
    }
}
