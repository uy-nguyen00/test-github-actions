package com.uyng.moneywise.category;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record CategoryRequest(
        @NotNull
        @NotEmpty
        String name,

        @Enumerated(EnumType.STRING)
        CategoryType type
) {}
