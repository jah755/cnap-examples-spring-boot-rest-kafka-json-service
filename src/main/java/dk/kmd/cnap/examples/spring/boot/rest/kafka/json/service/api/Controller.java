package dk.kmd.cnap.examples.spring.boot.rest.kafka.json.service.api;

import dk.kmd.cnap.examples.spring.boot.rest.kafka.json.service.model.message.GreetingCreateRequest;
import dk.kmd.cnap.examples.spring.boot.rest.kafka.json.service.model.message.GreetingCreateResponse;
import dk.kmd.cnap.examples.spring.boot.rest.kafka.json.service.service.GreetingClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static dk.kmd.cnap.examples.spring.boot.rest.kafka.json.service.util.LogBuilder.logBuilder;
import static org.slf4j.event.Level.INFO;

/** The Rest API controller of the Application */
@RestController
public class Controller {
  private Logger logger = LoggerFactory.getLogger(getClass());

  private GreetingClientService greetingClientService;

  public Controller(GreetingClientService greetingClientService) {
    this.greetingClientService = greetingClientService;
  }

  @GetMapping(value = "/api/request")
  public ResponseEntity<GreetingCreateRequest> request(@RequestParam String firstName, @RequestParam String lastName) {
    GreetingCreateRequest greetingCreateRequest = greetingClientService.requestGreeting(firstName, lastName);

    logBuilder()
            .loggerName(getClass())
            .level(INFO)
            .message("Create greeting create request")
            .parameter("transactionId", greetingCreateRequest.getTransactionId())
            .parameter("requestTime", greetingCreateRequest.getRequestTime())
            .parameter("firstName", greetingCreateRequest.getFirstName())
            .parameter("lastName", greetingCreateRequest.getLastName())
            .build();

    return ResponseEntity.ok(greetingCreateRequest);
  }

  @GetMapping(value = "/api/response")
  public ResponseEntity<GreetingCreateResponse> response(@RequestParam String key) {
    GreetingCreateResponse greetingCreateResponse = greetingClientService.greetingCreateResponseByKey(key);

    logBuilder()
            .loggerName(getClass())
            .level(INFO)
            .message("Lookup greeting create response by key")
            .parameter("key", key)
            .build();

    return ResponseEntity.ok(greetingCreateResponse);
  }


}
