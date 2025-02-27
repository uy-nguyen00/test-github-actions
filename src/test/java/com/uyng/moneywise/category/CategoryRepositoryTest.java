package com.uyng.moneywise.category;

import com.uyng.moneywise.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class CategoryRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private CategoryRepository categoryRepository;
    private User user;
    private Category category;

    @BeforeEach
    public void setUp() {
        user = User.builder()
                .email("test@mail.com")
                .build();
        entityManager.persist(user);

        category = categoryRepository.save(
                Category.builder()
                        .user(user)
                        .name("Groceries")
                        .type(CategoryType.EXPENSE)
                        .isDefault(true)
                        .build()
        );
    }

    @Test
    public void testCreateCategory() {

        assertThat(category).isNotNull();
        assertThat(category.getId()).isNotNull();
        assertThat(category.getName()).isEqualTo("Groceries");
        assertThat(category.getType()).isEqualTo(CategoryType.EXPENSE);
        assertThat(category.isDefault()).isTrue();
    }

    @Test
    public void testUpdateCategory() {
        category.setName("Utilities");
        category.setType(CategoryType.INCOME);
        category.setDefault(false);

        Category updatedCategory = categoryRepository.save(category);

        assertThat(updatedCategory.getName()).isEqualTo("Utilities");
        assertThat(updatedCategory.getType()).isEqualTo(CategoryType.INCOME);
        assertThat(updatedCategory.isDefault()).isFalse();
    }

    @Test
    public void testRetrieveCategory() {
        Category foundCategory = categoryRepository.findById(category.getId()).orElse(null);

        assertThat(foundCategory).isNotNull();
        assertThat(foundCategory.getId()).isEqualTo(category.getId());
        assertThat(foundCategory.getName()).isEqualTo("Groceries");
        assertThat(foundCategory.getType()).isEqualTo(CategoryType.EXPENSE);
        assertThat(foundCategory.isDefault()).isTrue();
    }

    @Test
    public void testDeleteCategory() {
        categoryRepository.delete(category);
        Category foundCategory = categoryRepository.findById(category.getId()).orElse(null);
        assertThat(foundCategory).isNull();
    }

    @Test
    public void testCategoryAttributes() {
        assertThat(category.getUser()).isEqualTo(user);
        assertThat(category.getName()).isEqualTo("Groceries");
        assertThat(category.getType()).isEqualTo(CategoryType.EXPENSE);
        assertThat(category.isDefault()).isTrue();
    }

    @Test
    public void testFindByIdAndUserEmail() {
        Optional<Category> foundCategory = categoryRepository.findByIdAndUserEmail(category.getId(), user.getEmail());
        assertThat(foundCategory).isPresent();
        assertThat(foundCategory.get().getId()).isEqualTo(category.getId());
        assertThat(foundCategory.get().getUser()).isEqualTo(user);
    }

    @Test
    public void testFindByIdAndUserEmail_InvalidId() {
        Optional<Category> result = categoryRepository.findByIdAndUserEmail(-1, user.getEmail());
        assertThat(result).isNotPresent();
    }

    @Test
    public void testFindByIdAndUserEmail_InvalidEmail() {
        Optional<Category> result = categoryRepository.findByIdAndUserEmail(category.getId(), "invalid@example.com");
        assertThat(result).isNotPresent();
    }

    @Test
    public void testFindByUserEmail_OrderByTypeAscCreatedDateDesc_ReturnsAllCategories() {
        Category category2 = categoryRepository.save(
                Category.builder()
                        .user(user)
                        .name("Category2")
                        .type(CategoryType.INCOME)
                        .isDefault(false)
                        .build()
        );
        List<Category> expectedCategories = Arrays.asList(category, category2);

        List<Category> actualCategories = categoryRepository.findByUserEmailOrderByTypeAscCreatedDateDesc(user.getEmail());

        assertThat(actualCategories).containsExactlyInAnyOrderElementsOf(expectedCategories);
    }

    @Test
    public void testFindByUserEmailOrderByTypeAscCreatedDateDesc_ReturnsCategoriesForDifferentUser() {
        User user2 = User.builder().email("user2@example.com").build();
        entityManager.persist(user2);

        Category category2 = categoryRepository.save(
                Category.builder()
                        .user(user2)
                        .name("Category2")
                        .type(CategoryType.INCOME)
                        .isDefault(false)
                        .build()
        );

        List<Category> actualCategoriesForUser1 = categoryRepository.findByUserEmailOrderByTypeAscCreatedDateDesc(user.getEmail());
        List<Category> actualCategoriesForUser2 = categoryRepository.findByUserEmailOrderByTypeAscCreatedDateDesc(user2.getEmail());

        assertThat(actualCategoriesForUser1).containsExactly(category);
        assertThat(actualCategoriesForUser2).containsExactly(category2);
    }
}