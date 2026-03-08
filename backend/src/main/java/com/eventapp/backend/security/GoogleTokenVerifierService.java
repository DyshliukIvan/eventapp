package com.eventapp.backend.security;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Service
public class GoogleTokenVerifierService {

    private final GoogleIdTokenVerifier verifier;

    public GoogleTokenVerifierService(
            @Value("${app.google.web-client-id}") String webClientId
    ) {
        this.verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(),
                GsonFactory.getDefaultInstance()
        )
                .setAudience(Collections.singletonList(webClientId))
                .build();
    }

    public GoogleIdToken.Payload verify(String idTokenString) {
        try {
            GoogleIdToken idToken = verifier.verify(idTokenString);

            if (idToken == null) {
                throw new IllegalStateException("Invalid Google ID token");
            }

            GoogleIdToken.Payload payload = idToken.getPayload();

            String issuer = payload.getIssuer();
            if (!"accounts.google.com".equals(issuer) &&
                    !"https://accounts.google.com".equals(issuer)) {
                throw new IllegalStateException("Invalid token issuer");
            }

            return payload;
        } catch (GeneralSecurityException | IOException e) {
            throw new IllegalStateException("Failed to verify Google ID token", e);
        }
    }
}