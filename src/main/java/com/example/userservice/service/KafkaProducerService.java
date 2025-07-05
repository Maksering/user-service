package com.example.userservice.service;

import org.springframework.stereotype.Service;
import org.springframework.kafka.core.KafkaTemplate;

@Service
public class KafkaProducerService {

    public static final String CREATE_OPERATION = "create";
    static final String DELETE_OPERATION = "delete";
    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendUserCreate(String email) {
       /* kafkaTemplate.send(kafkaTemplate.getDefaultTopic(),
                CREATE_OPERATION,
                email);*/
    }

    public void sendUserDelete(String email) {
      /* kafkaTemplate.send(kafkaTemplate.getDefaultTopic(),
                DELETE_OPERATION,
                email);*/
    }
}
