package com.warba.banking.customer.repository.entity;

import com.warba.banking.customer.infra.annotations.GeneratedCustomerId;
import com.warba.banking.customer.model.enums.CustomerType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class Customer {
    @Id
    @GeneratedCustomerId
    private Long id;

    @NotBlank(message = "Customer name is required")
    private String name;

    @Column(name = "legal_id", unique = true)
    @NotBlank(message = "Legal ID is required")
    private String legalId;

    @Column(name = "customer_type", nullable = false)
    @NotNull(message = "Customer type must be defined")
    @Enumerated(EnumType.STRING)
    private CustomerType customerType;

    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "\\d{10,15}", message = "Mobile number must be 10 to 15 digits")
    @Column(name = "mobile_number", unique = true)
    private String mobileNumber;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "address_id", referencedColumnName = "id", nullable = false)
    private Address address;

    @Column(name = "total_accounts", nullable = false)
    private Integer totalAccounts;

    @Column(name = "has_salary_account", nullable = false)
    private Boolean hasSalaryAccount;

    @CreationTimestamp
    @Column(name = "registration_date", nullable = false, updatable = false)
    private LocalDateTime registrationDate;

    @UpdateTimestamp
    @Column(name = "last_modified", nullable = false)
    private LocalDateTime lastModified;
}
