package com.aldeamo.messaging.apiproducer.service;

import com.aldeamo.messaging.apiproducer.dto.MessageRequest;
import com.aldeamo.messaging.apiproducer.repository.OriginRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Slf4j
public class MessageService {

    private final OriginRepository originRepository;
    private final RabbitTemplate rabbitTemplate;
    private final String exchange;
    private final String routingKey;
    private final Counter messagesPublishedCounter;
    private final Counter messagesRejectedCounter;

    public MessageService(OriginRepository originRepository,
                          RabbitTemplate rabbitTemplate,
                          @Value("${app.rabbitmq.exchange}") String exchange,
                          @Value("${app.rabbitmq.routing-key}") String routingKey,
                          MeterRegistry meterRegistry) {
        this.originRepository = originRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.exchange = exchange;
        this.routingKey = routingKey;
        this.messagesPublishedCounter = Counter.builder("messages.published")
                .description("Total messages published to RabbitMQ")
                .register(meterRegistry);
        this.messagesRejectedCounter = Counter.builder("messages.rejected")
                .description("Total messages rejected due to invalid origin")
                .register(meterRegistry);
    }

    public void validateAndPublish(MessageRequest request) {
        log.info("Processing message from origin: {} to destination: {}", request.getOrigin(), request.getDestination());

        boolean originExists = originRepository.existsByPhoneNumberAndActiveTrue(request.getOrigin());
        if (!originExists) {
            messagesRejectedCounter.increment();
            log.warn("Origin not registered: {}", request.getOrigin());
            throw new OriginNotFoundException("Origin '" + request.getOrigin() + "' is not registered in the system");
        }

        long timestamp = Instant.now().toEpochMilli();

        rabbitTemplate.convertAndSend(exchange, routingKey, request, message -> {
            MessageProperties props = message.getMessageProperties();
            props.setHeader("timestamp", timestamp);
            return message;
        });

        messagesPublishedCounter.increment();
        log.info("Message published to RabbitMQ. Origin: {}, Destination: {}, Type: {}",
                request.getOrigin(), request.getDestination(), request.getMessageType());
    }

    public static class OriginNotFoundException extends RuntimeException {
        public OriginNotFoundException(String message) {
            super(message);
        }
    }
}
