package com.warba.banking.customer.config;

import com.warba.banking.customer.listener.AccountEventListener;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConsumerRabbitMQConfig {
    @Bean
    public SimpleMessageListenerContainer messageListenerContainer(ConnectionFactory connectionFactory,
                                                                   MessageListenerAdapter messageListenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames("account.closed.queue");  // Specify the queue name
        container.setMessageListener(messageListenerAdapter);
        return container;
    }

    @Bean
    public MessageListenerAdapter messageListenerAdapter(AccountEventListener accountEventListener) {
        MessageListenerAdapter listenerAdapter = new MessageListenerAdapter(accountEventListener, "listen");
        listenerAdapter.setMessageConverter(new Jackson2JsonMessageConverter());  // Set the Jackson converter
        return listenerAdapter;
    }
}
