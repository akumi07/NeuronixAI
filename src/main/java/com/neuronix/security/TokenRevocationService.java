
package com.neuronix.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenRevocationService {

    private static final Logger logger =
            LoggerFactory.getLogger(TokenRevocationService.class);

    private final Set<String> revokedTokens =
            ConcurrentHashMap.newKeySet();

    public void revokeToken(String token) {
        revokedTokens.add(token);

        logger.info("Token has been revoked successfully");
    }

    public boolean isTokenRevoked(String token) {

        boolean revoked = revokedTokens.contains(token);

        logger.debug("Token revocation check completed: {}", revoked);

        return revoked;
    }
}
