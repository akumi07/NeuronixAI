package com.neuronix.security;

import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenRevocationService {

    private final Set<String> revokedTokens =
            ConcurrentHashMap.newKeySet();


        public void revokeToken(String token) {
            revokedTokens.add(token);
            System.out.println("REVOKED TOKEN: " + token);
        }


    public boolean isTokenRevoked(String token) {

        boolean revoked = revokedTokens.contains(token);

        System.out.println("TOKEN CHECK: " + revoked);

        return revoked;
    }
}