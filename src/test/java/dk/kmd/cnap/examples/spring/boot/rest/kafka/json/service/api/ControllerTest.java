package dk.kmd.cnap.examples.spring.boot.rest.kafka.json.service.api;

import dk.kmd.cnap.examples.spring.boot.rest.kafka.json.service.model.message.GreetingCreateRequest;
import dk.kmd.cnap.examples.spring.boot.rest.kafka.json.service.service.GreetingClientService;
import dk.kmd.cnap.examples.spring.boot.rest.kafka.json.service.service.GreetingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static java.time.LocalDateTime.now;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(Controller.class)
public class ControllerTest {
  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private GreetingClientService greetingClientService;

  @MockBean
  private GreetingService greetingService;


  //@Test
  public void givenRequestURI_whenMockMVC_thenVerifyResponse() throws Exception {

    GreetingCreateRequest greetingCreateRequest = new GreetingCreateRequest();
    greetingCreateRequest.setTransactionId(UUID.randomUUID().toString());
    greetingCreateRequest.setRequestTime(now());
    greetingCreateRequest.setFirstName("John");
    greetingCreateRequest.setLastName("Doe");
    when(greetingClientService.requestGreeting("John", "Doe")).thenReturn(greetingCreateRequest);

    MvcResult mvcResult =
        this.mockMvc
            .perform(get("/api/request?firstName=\"John\"&lastName=\"Doe\""))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().string("Default message"))
            .andReturn();
  }

}
