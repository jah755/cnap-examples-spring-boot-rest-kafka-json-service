package dk.kmd.cnap.examples.spring.boot.rest.kafka.json.service.service;

import dk.kmd.cnap.examples.spring.boot.rest.kafka.json.service.config.KafkaConfig;
import dk.kmd.cnap.examples.spring.boot.rest.kafka.json.service.config.KafkaTopicName;
import dk.kmd.cnap.examples.spring.boot.rest.kafka.json.service.model.message.GreetingCreateRequest;
import dk.kmd.cnap.examples.spring.boot.rest.kafka.json.service.model.message.GreetingCreateResponse;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static dk.kmd.cnap.examples.spring.boot.rest.kafka.json.service.util.LogBuilder.logBuilder;
import static java.time.LocalDateTime.now;
import static org.slf4j.event.Level.ERROR;
import static org.slf4j.event.Level.INFO;

/**
 * A Service producing requests on Kafka and consuming responses from Kafka
 */
@Service
public class GreetingClientService {
  private final KafkaProducer<String, GreetingCreateRequest> greetingCreateRequestKafkaProducer;

  private Map<String, GreetingCreateResponse> greetingCreateResponseByKey = new ConcurrentHashMap();

  public GreetingClientService(KafkaProducer<String, GreetingCreateRequest> greetingCreateRequestKafkaProducer) {
    this.greetingCreateRequestKafkaProducer = greetingCreateRequestKafkaProducer;
  }

  @KafkaListener(topics = KafkaConfig.TOPIC_GREETING_CREATE_RESPONSE, containerFactory = "greetingCreateResponseKafkaListenerContainerFactory", groupId = "GreetingService-JSON")
  public void consumeGreetingCreateRequest(GreetingCreateResponse greetingCreateResponse) {
    logBuilder()
            .loggerName(getClass())
            .level(INFO)
            .message("Greeting create response received from Kafka")
            .parameter("transactionId", greetingCreateResponse.getTransactionId())
            .parameter("responseTime", greetingCreateResponse.getResponseTime())
            .parameter("firstName", greetingCreateResponse.getFirstName())
            .parameter("lastName", greetingCreateResponse.getLastName())
            .parameter("greeting", greetingCreateResponse.getGreeting())
            .build();

    greetingCreateResponseByKey.put(greetingCreateResponse.getTransactionId(), greetingCreateResponse);
  }

  public GreetingCreateResponse greetingCreateResponseByKey(String key) {
    return greetingCreateResponseByKey.get(key);
  }

  public GreetingCreateRequest requestGreeting(String firstName, String lastName) {
    logBuilder()
            .loggerName(getClass())
            .level(INFO)
            .message("Greeting create request")
            .parameter("firstName", firstName)
            .parameter("lastName", lastName)
            .build();

    return send(createGreetingCreateRequest(firstName, lastName));
  }

  GreetingCreateRequest createGreetingCreateRequest(String firstName, String lastName) {
    GreetingCreateRequest greetingCreateRequest = new GreetingCreateRequest();
    greetingCreateRequest.setTransactionId(UUID.randomUUID().toString());
    greetingCreateRequest.setRequestTime(now());
    greetingCreateRequest.setFirstName(firstName);
    greetingCreateRequest.setLastName(lastName);
    return greetingCreateRequest;
  }

  private GreetingCreateRequest send(GreetingCreateRequest greetingCreateRequest) {
    final ProducerRecord<String, GreetingCreateRequest> record = new ProducerRecord(KafkaTopicName.TOPIC_GREETING_CREATE_REQUEST, greetingCreateRequest.getTransactionId(), greetingCreateRequest);
    greetingCreateRequestKafkaProducer.send(record,
            (recordMetadata, e) -> {
              if (e != null)
                logBuilder()
                        .loggerName(getClass())
                        .level(ERROR)
                        .message("Failed to send greeting create request on Kafka")
                        .parameter("transactionId", greetingCreateRequest.getTransactionId())
                        .parameter("kafkaTopic", KafkaTopicName.TOPIC_GREETING_CREATE_RESPONSE)
                        .parameter("kafkaPartition", recordMetadata.partition())
                        .parameter("kafkaOffset", recordMetadata.offset())
                        .parameter("exceptionMessage", e.getMessage())
                        .build();
              else
                logBuilder()
                        .loggerName(getClass())
                        .level(INFO)
                        .message("Sent greeting create request on Kafka")
                        .parameter("transactionId", greetingCreateRequest.getTransactionId())
                        .parameter("kafkaTopic", recordMetadata.topic())
                        .parameter("kafkaPartition", recordMetadata.partition())
                        .parameter("kafkaOffset", recordMetadata.offset())
                        .build();
            });
    return greetingCreateRequest;
  }

}
