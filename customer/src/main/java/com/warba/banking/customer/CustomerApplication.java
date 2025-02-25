package com.warba.banking.customer;

import com.warba.banking.customer.infra.util.SevenDigitIdGenerator;
import com.warba.banking.customer.repository.entity.Customer;
import com.warba.banking.customer.repository.repo.CustomerRepository;
import com.warba.banking.customer.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;

@SpringBootApplication
@EnableRabbit
public class CustomerApplication {
    public static void main(String[] args) {
        SpringApplication.run(CustomerApplication.class, args);

    }
}
