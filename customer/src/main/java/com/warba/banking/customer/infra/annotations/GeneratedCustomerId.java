package com.warba.banking.customer.infra.annotations;

import com.warba.banking.customer.infra.util.SevenDigitIdGenerator;
import org.hibernate.annotations.IdGeneratorType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@IdGeneratorType(value = SevenDigitIdGenerator.class)
// Links this annotation to the custom ID generator.

@Retention(RetentionPolicy.RUNTIME)
// Ensures the annotation is available at runtime so Hibernate can use it.

@Target({ElementType.FIELD})
// Allows this annotation to be applied only to fields (e.g., entity attributes).
public @interface GeneratedCustomerId {
}
