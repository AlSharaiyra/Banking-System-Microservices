package com.warba.banking.account.repository.entity;

import com.warba.banking.account.model.enums.AccountStatus;
import com.warba.banking.account.model.enums.AccountType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_number", nullable = false, unique = true)
    @NotNull(message = "Account number is required")
    private Long accountNumber;

    @Column(name = "customer_id", nullable = false)
//    @Size(min = 7, max = 7, message = "Customer ID must be exactly 7 digits")
    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotNull
    @Column(nullable = false, columnDefinition = "DECIMAL(12,3) CHECK (balance >= 0)")
    @PositiveOrZero(message = "Balance cannot be negative")
    private BigDecimal balance;

    @Column(name = "account_type", nullable = false)
    @NotNull(message = "Account type must be defined")
    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    @Column(name = "account_status", nullable = false)
    @NotNull(message = "Account status must be defined")
    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus;

    @CreationTimestamp
    @Column(name = "registration_date", nullable = false, updatable = false)
    private LocalDateTime registrationDate;

    @UpdateTimestamp
    @Column(name = "last_modified", nullable = false)
    private LocalDateTime lastModified;
}
