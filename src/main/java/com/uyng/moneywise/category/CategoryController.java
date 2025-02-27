package com.uyng.moneywise.category;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("categories")
@RequiredArgsConstructor
@Tag(name = "Category")
public class CategoryController {

    private final CategoryService service;

    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(
            @Valid @RequestBody CategoryRequest request,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(service.createCategory(request, connectedUser));
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> findAllCategoriesByUser(Authentication connectedUser) {
        return ResponseEntity.ok(service.findAllCategoriesByUser(connectedUser));
    }

    @GetMapping("/{category-id}")
    public ResponseEntity<CategoryResponse> findById(
            @PathVariable("category-id") Integer id,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(service.findById(id, connectedUser));
    }

    @PatchMapping("/{category-id}")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable("category-id") Integer id,
            @Valid @RequestBody CategoryRequest request,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(service.updateCategory(id, request, connectedUser));
    }

    @DeleteMapping("/{category-id}")
    public ResponseEntity<Integer> deleteCategory(
            @PathVariable("category-id") Integer id,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(service.deleteCategory(id, connectedUser));
    }
}