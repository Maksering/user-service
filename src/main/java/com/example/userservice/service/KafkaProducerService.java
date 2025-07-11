package com.example.userservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.kafka.core.KafkaTemplate;

@Service
public class KafkaProducerService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public static final String CREATE_OPERATION = "create";
    static final String DELETE_OPERATION = "delete";
    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendUserCreate(String email) {
        kafkaTemplate.send(kafkaTemplate.getDefaultTopic(),
                CREATE_OPERATION,
                email);
        logger.info("Create kafka message was sent");
    }

    public void sendUserDelete(String email) {
       kafkaTemplate.send(kafkaTemplate.getDefaultTopic(),
                DELETE_OPERATION,
                email);
        logger.info("Delete kafka message was sent");
    }
}
