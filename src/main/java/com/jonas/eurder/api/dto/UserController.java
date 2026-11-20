package com.jonas.eurder.api.dto;

import com.jonas.eurder.api.dto.user.UserDTO;
import com.jonas.eurder.domain.User;
import com.jonas.eurder.service.SecurityService;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/users")
public class UserController {

    private final SecurityService securityService;

    public UserController(SecurityService securityService) {
        this.securityService = securityService;
    }

    @GetMapping(path = "/me", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public UserDTO getCurrentUser(@Parameter(hidden = true) @RequestHeader(value = "Authorization", required = false) String authorization) {
        User user = securityService.authenticate(authorization);
        return new UserDTO(user.getEmail(), user.getRole().name(), user.getCustomerId());
    }
}
