package com.jonas.eurder.service;

import com.jonas.eurder.domain.Feature;
import com.jonas.eurder.domain.User;
import com.jonas.eurder.repository.UserRepository;
import com.jonas.eurder.service.exception.ForbiddenException;
import com.jonas.eurder.service.exception.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class SecurityService {
    private static final Logger logger = LoggerFactory.getLogger(SecurityService.class);
    private final UserRepository userRepository;

    public SecurityService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User validateAuthorization(String authorizationHeader, Feature feature) {
        User user = authenticate(authorizationHeader);
        if (!user.hasAccessTo(feature)) {
            logger.warn("User {} tried to use {} without permission", user.getEmail(), feature);
            throw new ForbiddenException("You don't have access to " + feature);
        }
        return user;
    }

    private User authenticate(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Basic ")) {
            throw new UnauthorizedException("Missing credentials");
        }
        String[] credentials = decode(authorizationHeader.substring("Basic ".length()));
        return userRepository.getByEmail(credentials[0])
                .filter(user -> user.passwordMatches(credentials[1]))
                .orElseThrow(() -> new UnauthorizedException("Wrong email or password"));
    }

    private String[] decode(String encoded) {
        try {
            String decoded = new String(Base64.getDecoder().decode(encoded), StandardCharsets.UTF_8);
            String[] parts = decoded.split(":", 2);
            if (parts.length != 2) {
                throw new UnauthorizedException("Invalid credentials");
            }
            return parts;
        } catch (IllegalArgumentException exception) {
            throw new UnauthorizedException("Invalid credentials");
        }
    }


}
