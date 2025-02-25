package com.warba.banking.account.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.warba.banking.common.constant.RabbitMQConstants.EXCHANGE_NAME;
import static com.warba.banking.common.constant.RabbitMQConstants.QUEUE_NAME;
import static com.warba.banking.common.constant.RabbitMQConstants.QUEUE_ROUTING_KEY;

@Configuration
public class RabbitMQConfig {

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());  // If you have Java 8 DateTime types
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter(objectMapper);
        rabbitTemplate.setMessageConverter(converter);
        return rabbitTemplate;
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue accountClosedQueue() {
        return new Queue(QUEUE_NAME, false); // Only declare here
    }

    @Bean
    public Binding binding(Queue accountClosedQueue, DirectExchange exchange) {
        return BindingBuilder.bind(accountClosedQueue).to(exchange).with(QUEUE_ROUTING_KEY);
    }
}
