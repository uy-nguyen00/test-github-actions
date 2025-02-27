package com.uyng.moneywise.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record AuthenticationRequest(
        @Email(message = "Email is not well formatted")
        @NotEmpty(message = "Email is mandatory")
        @NotNull(message = "Email is mandatory")
        String email,

        @NotEmpty(message = "Password is mandatory")
        @NotNull(message = "Password is mandatory")
        String password
) {}
