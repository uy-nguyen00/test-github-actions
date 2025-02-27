package com.uyng.moneywise.transaction;

import com.uyng.moneywise.category.Category;
import com.uyng.moneywise.category.CategoryType;
import com.uyng.moneywise.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class TransactionRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private TransactionRepository transactionRepository;

    private User user;
    private Category category;
    private Transaction transaction;

    @BeforeEach
    public void setUp() {
        user = User.builder()
                .email("test@example.com")
                .build();
        entityManager.persist(user);

        category = Category.builder()
                .user(user)
                .name("Groceries")
                .type(CategoryType.EXPENSE)
                .isDefault(true)
                .build();
        entityManager.persist(category);

        transaction = transactionRepository.save(
                Transaction.builder()
                        .user(user)
                        .category(category)
                        .amount(15.0000)
                        .date(LocalDate.now())
                        .description("test description")
                        .build()
        );
    }

    @Test
    public void testCreateTransaction() {
        assertThat(transaction).isNotNull();
        assertThat(transaction.getId()).isNotNull();
        assertThat(transaction.getUser()).isEqualTo(user);
        assertThat(transaction.getCategory()).isEqualTo(category);
        assertThat(transaction.getAmount()).isEqualTo(15.00);
        assertThat(transaction.getDate()).isEqualTo(LocalDate.now());
        assertThat(transaction.getDescription()).isEqualTo("test description");
    }

    @Test
    public void testUpdateTransaction() {
        transaction.setAmount(10.30);
        transaction.setDate(transaction.getDate().plusDays(1));
        transaction.setDescription("updated description");

        transactionRepository.save(transaction);

        assertThat(transaction.getAmount()).isEqualTo(10.30);
        assertThat(transaction.getDate()).isEqualTo(LocalDate.now().plusDays(1));
        assertThat(transaction.getDescription()).isEqualTo("updated description");
    }

    @Test
    public void testRetrieveTransaction() {
        Transaction foundTransaction = transactionRepository.findById(transaction.getId()).orElse(null);

        assertThat(foundTransaction).isNotNull();
        assertThat(foundTransaction.getId()).isNotNull();
        assertThat(foundTransaction.getUser()).isEqualTo(user);
        assertThat(foundTransaction.getCategory()).isEqualTo(category);
        assertThat(foundTransaction.getAmount()).isEqualTo(15.00);
        assertThat(foundTransaction.getDate()).isEqualTo(LocalDate.now());
        assertThat(foundTransaction.getDescription()).isEqualTo("test description");
        assertThat(foundTransaction.getLinkedTransactions()).isEmpty();
    }

    @Test
    public void testRetrieveTransaction_WithLinkedTransactions() {
        Transaction linkedTransaction1 = transactionRepository.save(
                Transaction.builder()
                        .user(user)
                        .category(category)
                        .amount(999.01)
                        .date(LocalDate.now())
                        .description("linkedTransaction1")
                        .build()
        );
        Transaction linkedTransaction2 = transactionRepository.save(
                Transaction.builder()
                        .user(user)
                        .category(category)
                        .amount(2523.25)
                        .date(LocalDate.now())
                        .description("linkedTransaction2")
                        .build()
        );

        Set<Transaction> linkedTransactions = new HashSet<>();
        linkedTransactions.add(linkedTransaction1);
        linkedTransactions.add(linkedTransaction2);

        transaction.setLinkedTransactions(linkedTransactions);
        transactionRepository.save(transaction);

        Transaction foundTransaction = transactionRepository.findById(transaction.getId()).orElse(null);
        assertThat(foundTransaction).isNotNull();
        assertThat(foundTransaction.getLinkedTransactions()).containsExactlyInAnyOrder(linkedTransaction1, linkedTransaction2);
    }

    @Test
    public void testDeleteTransaction() {
        transactionRepository.delete(transaction);
        Transaction foundTransaction = transactionRepository.findById(transaction.getId()).orElse(null);
        assertThat(foundTransaction).isNull();
    }

    @Test
    public void testFindByUser() {
        Transaction transaction2 = transactionRepository.save(
                Transaction.builder()
                        .user(user)
                        .category(category)
                        .amount(20.35)
                        .date(LocalDate.now())
                        .description("description2")
                        .build()
        );

        List<Transaction> transactions = transactionRepository.findByUser(user);

        assertThat(transactions).isNotEmpty();
        assertThat(transactions).contains(transaction, transaction2);
    }

    @Test
    public void testFindByUser_UserWithNoTransactions_ReturnsEmpty() {
        User user2 = User.builder()
                .email("invalid@example.com")
                .build();
        entityManager.persist(user2);

        List<Transaction> transactions = transactionRepository.findByUser(user2);

        assertThat(transactions).isEmpty();
    }

    @Test
    public void testFindByUser_ReturnsTransactionsForDifferentUser() {
        User user2 = User.builder()
                .email("invalid@example.com")
                .build();
        entityManager.persist(user2);

        Transaction transaction2 = transactionRepository.save(
                Transaction.builder()
                        .user(user2)
                        .category(category)
                        .amount(20.35)
                        .date(LocalDate.now())
                        .description("description2")
                        .build()
        );

        List<Transaction> transactionsForUser = transactionRepository.findByUser(user);
        List<Transaction> transactionsForUser2 = transactionRepository.findByUser(user2);

        assertThat(transactionsForUser).containsExactly(transaction);
        assertThat(transactionsForUser2).containsExactly(transaction2);
    }


}
