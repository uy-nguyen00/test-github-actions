package com.uyng.moneywise.category;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    List<Category> findByUserEmailOrderByTypeAscCreatedDateDesc(String email);
    Optional<Category> findByIdAndUserEmail(Integer id, String email);
}
