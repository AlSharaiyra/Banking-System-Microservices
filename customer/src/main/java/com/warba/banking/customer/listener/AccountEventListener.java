package com.warba.banking.customer.listener;

import com.warba.banking.common.constant.RabbitMQConstants;
import com.warba.banking.common.event.AccountClosedEvent;
import com.warba.banking.customer.service.AccountManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AccountEventListener {
    private final AccountManagementService accountManagementService;

    @RabbitListener(queues = RabbitMQConstants.QUEUE_NAME)
    public void listen(AccountClosedEvent event) {
        accountManagementService.handleAccountClosed(event.getCustomerId(), event.getIsSalaryAccount());
    }
}
