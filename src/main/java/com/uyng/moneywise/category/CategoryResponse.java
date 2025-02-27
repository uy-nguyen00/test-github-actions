package com.uyng.moneywise.category;

public record CategoryResponse(
        Integer id,
        String name,
        CategoryType type
) {}