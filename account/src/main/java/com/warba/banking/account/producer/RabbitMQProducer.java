package com.warba.banking.account.producer;

import com.warba.banking.common.event.AccountClosedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import static com.warba.banking.common.constant.RabbitMQConstants.QUEUE_ROUTING_KEY;
import static com.warba.banking.common.constant.RabbitMQConstants.EXCHANGE_NAME;

@RequiredArgsConstructor
@Service
public class RabbitMQProducer {
    private final RabbitTemplate rabbitTemplate; // Will automatically inject the custom RabbitTemplate

    public void sendCloseAccount(AccountClosedEvent event){

        rabbitTemplate.convertAndSend(EXCHANGE_NAME, QUEUE_ROUTING_KEY, event);
    }
}
