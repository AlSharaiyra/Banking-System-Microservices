package com.warba.banking.customer.infra.util;

import com.warba.banking.customer.infra.exception.RetryLimitExceededException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;
import java.util.Random;

public class SevenDigitIdGenerator implements IdentifierGenerator {

    private static final long MIN_ID = 1000000L;  // Smallest 7-digit number
    private static final long MAX_ID = 9999999L;  // Largest 7-digit number
    private static final int MAX_ATTEMPTS = 10;   // Maximum retries

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) {
        Random random = new Random();
        Long generatedId;
        int attempts = 0;

        do {
            if (attempts++ >= MAX_ATTEMPTS) {
                throw new RetryLimitExceededException("Failed to generate unique ID after " + MAX_ATTEMPTS + " attempts");
            }
            generatedId = MIN_ID + random.nextInt((int) (MAX_ID - MIN_ID + 1));
        } while (idExists(session, generatedId));

        return generatedId;
    }

    private boolean idExists(SharedSessionContractImplementor session, Long id) {

        String hql = "SELECT 1 FROM Customer WHERE id = :id";
        return session.createQuery(hql, Integer.class)
                .setParameter("id", id)
                .setMaxResults(1)  // Stop scanning after the first match
                .uniqueResult() != null;
    }

}
