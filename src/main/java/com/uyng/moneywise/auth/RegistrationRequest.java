package com.uyng.moneywise.auth;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record RegistrationRequest(

        @NotEmpty(message = "Firstname is mandatory")
        @NotNull(message = "Firstname is mandatory")
        String firstname,

        @NotEmpty(message = "Lastname is mandatory")
        @NotNull(message = "Lastname is mandatory")
        String lastname,

        @Email(message = "Email is not well formatted")
        @NotEmpty(message = "Email is mandatory")
        @NotNull(message = "Email is mandatory")
        @Column(unique=true)
        String email,

        @NotEmpty(message = "Password is mandatory")
        @NotNull(message = "Password is mandatory")
        @Pattern(
                regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*\\W).{8,}$",
                message = "Password must be at least 8 characters long and contain at least one digit, " +
                        "one lowercase letter, one uppercase letter, and one special character"
        )
        String password
) {
}