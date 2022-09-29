package dk.kmd.cnap.examples.spring.boot.rest.kafka.json.service.service;

import dk.kmd.cnap.examples.spring.boot.rest.kafka.json.service.config.KafkaConfig;
import dk.kmd.cnap.examples.spring.boot.rest.kafka.json.service.config.KafkaTopicName;
import dk.kmd.cnap.examples.spring.boot.rest.kafka.json.service.model.entity.Greeting;
import dk.kmd.cnap.examples.spring.boot.rest.kafka.json.service.model.message.GreetingCreateRequest;
import dk.kmd.cnap.examples.spring.boot.rest.kafka.json.service.model.message.GreetingCreateResponse;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static dk.kmd.cnap.examples.spring.boot.rest.kafka.json.service.util.LogBuilder.logBuilder;
import static java.time.LocalDateTime.now;
import static org.slf4j.event.Level.ERROR;
import static org.slf4j.event.Level.INFO;

/**
 * A service that generates greetings when requested.
 */
@Service
public class GreetingService {
  private final KafkaProducer<String, GreetingCreateResponse> greetingCreateResponseKafkaProducer;

  private Map<String, Greeting> greetingByKey = new ConcurrentHashMap();

  public GreetingService(KafkaProducer<String, GreetingCreateResponse> greetingCreateResponseKafkaProducer) {
    this.greetingCreateResponseKafkaProducer = greetingCreateResponseKafkaProducer;
  }

  @KafkaListener(topics = KafkaConfig.TOPIC_GREETING_CREATE_REQUEST, containerFactory = "greetingCreateRequestKafkaListenerContainerFactory", groupId = "GreetingService-JSON")
  public void consumeGreetingCreateRequest(GreetingCreateRequest greetingCreateRequest) {
    send(processGreetingCreateRequest(greetingCreateRequest));
  }

  GreetingCreateResponse processGreetingCreateRequest(GreetingCreateRequest greetingCreateRequest) {
    logBuilder()
        .loggerName(getClass())
        .level(INFO)
        .message("Greeting create request received from Kafka")
        .parameter("transactionId", greetingCreateRequest.getTransactionId())
        .parameter("requestTime", greetingCreateRequest.getRequestTime())
        .parameter("firstName", greetingCreateRequest.getFirstName())
        .parameter("lastName", greetingCreateRequest.getLastName())
        .build();

    String greetingMsg = "Hello "+greetingCreateRequest.getFirstName()+" "+greetingCreateRequest.getLastName();
    Greeting greeting = new Greeting();
    greeting.setTransactionId(greetingCreateRequest.getTransactionId());
    greeting.setRequestTime(greetingCreateRequest.getRequestTime());
    greeting.setFirstName(greetingCreateRequest.getFirstName());
    greeting.setLastName(greetingCreateRequest.getLastName());
    greeting.setResponseTime(now());
    greeting.setGreeting(greetingMsg);
    greetingByKey.put(greetingCreateRequest.getTransactionId(), greeting);

    return createGreetingCreateResponse(greeting);
  }

  GreetingCreateResponse createGreetingCreateResponse(Greeting greeting) {
    GreetingCreateResponse greetingCreateResponse = new GreetingCreateResponse();
    greetingCreateResponse.setTransactionId(greeting.getTransactionId());
    greetingCreateResponse.setFirstName(greeting.getFirstName());
    greetingCreateResponse.setLastName(greeting.getLastName());
    greetingCreateResponse.setResponseTime(greeting.getResponseTime());
    greetingCreateResponse.setGreeting(greeting.getGreeting());
    return greetingCreateResponse;
  }

  private void send(GreetingCreateResponse greetingCreateResponse) {
    final ProducerRecord<String, GreetingCreateResponse> record = new ProducerRecord(KafkaTopicName.TOPIC_GREETING_CREATE_RESPONSE, greetingCreateResponse.getTransactionId(), greetingCreateResponse);
    greetingCreateResponseKafkaProducer.send(record,
            (recordMetadata, e) -> {
              if (e != null)
                logBuilder()
                        .loggerName(getClass())
                        .level(ERROR)
                        .message("Failed to send BanqView loan response on Kafka")
                        .parameter("transactionId", greetingCreateResponse.getTransactionId())
                        .parameter("kafkaTopic", KafkaTopicName.TOPIC_GREETING_CREATE_RESPONSE)
                        .parameter("kafkaPartition", recordMetadata.partition())
                        .parameter("kafkaOffset", recordMetadata.offset())
                        .parameter("exceptionMessage", e.getMessage())
                        .build();
              else
                logBuilder()
                        .loggerName(getClass())
                        .level(INFO)
                        .message("Sent BanqView loan response on Kafka")
                        .parameter("transactionId", greetingCreateResponse.getTransactionId())
                        .parameter("kafkaTopic", recordMetadata.topic())
                        .parameter("kafkaPartition", recordMetadata.partition())
                        .parameter("kafkaOffset", recordMetadata.offset())
                        .build();
            });
  }
}
