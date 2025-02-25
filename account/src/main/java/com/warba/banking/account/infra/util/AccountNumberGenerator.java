package com.warba.banking.account.infra.util;

import com.warba.banking.account.model.enums.AccountType;

import java.util.Map;
import java.util.Random;

public class AccountNumberGenerator {
    public static Long generateAccountNumber(Long customerId, AccountType accountType, Integer accountCount) {
        // Define account type mapping
        Map<AccountType, Integer> accountTypeMap = Map.of(
                AccountType.SAVING, 300,
                AccountType.SALARY, 500,
                AccountType.INVESTMENT, 700
        );

        // Get the account type code
        Integer accountTypeCode = accountTypeMap.get(accountType);
        if (accountTypeCode == null) {
            throw new IllegalArgumentException("Invalid account type");
        }

        // Generate the account number
        Random rand = new Random();
        return Long.parseLong(String.valueOf(customerId) + String.valueOf(accountTypeCode + rand.nextInt(99) + 1));
    }
}
