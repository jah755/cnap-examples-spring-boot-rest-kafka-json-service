package dk.kmd.cnap.examples.spring.boot.rest.kafka.json.service.config;

public interface KafkaTopicName {
    String TOPIC_GREETING_CREATE_REQUEST = "public.command.kmd.cnap.examples.spring.boot.rest.kafka.json.greeting.create.request";
    String TOPIC_GREETING_CREATE_RESPONSE = "public.command.kmd.cnap.examples.spring.boot.rest.kafka.json.greeting.create.response";
}
