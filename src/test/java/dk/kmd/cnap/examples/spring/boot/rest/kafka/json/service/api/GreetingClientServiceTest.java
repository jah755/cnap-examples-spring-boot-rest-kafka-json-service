package dk.kmd.cnap.examples.spring.boot.rest.kafka.json.service.api;

import dk.kmd.cnap.examples.spring.boot.rest.kafka.json.service.model.message.GreetingCreateRequest;
import dk.kmd.cnap.examples.spring.boot.rest.kafka.json.service.service.GreetingClientService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest(classes = GreetingClientService.class)
public class GreetingClientServiceTest {
  @Autowired
  private GreetingClientService greetingClientService;

  //@Test
  public void requestGreeting() {
    GreetingCreateRequest request = greetingClientService.requestGreeting("John", "Doe");
    assertThat(request.getTransactionId(), is(notNullValue()));
    assertThat(request.getRequestTime(), is(notNullValue()));
    assertThat(request.getFirstName(), is("John"));
    assertThat(request.getLastName(), is("Doe"));
  }
}
