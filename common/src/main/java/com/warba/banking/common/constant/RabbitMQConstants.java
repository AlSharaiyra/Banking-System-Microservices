package com.warba.banking.common.constant;

public class RabbitMQConstants {
    public static final String QUEUE_NAME = "account.closed.queue";
    public static final String EXCHANGE_NAME = "account.exchange";
    public static final String QUEUE_ROUTING_KEY = "account.closed";

    private RabbitMQConstants() {
        // Private constructor to prevent instantiation
    }
}
