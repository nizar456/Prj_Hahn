package Back_End.it;

import Back_End.dto.AuthDtos;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class AuthIntegrationTest extends BaseMySqlContainerTest {

  @Autowired MockMvc mvc;
  @Autowired ObjectMapper om;

  @Test
  void signIn_and_login_success() throws Exception {
    var signUp = new AuthDtos.SignUpRequest("john.doe@example.com", "Password#123");
    var signUpRes = mvc.perform(post("/api/auth/sign-in")
        .contentType(MediaType.APPLICATION_JSON)
        .content(om.writeValueAsString(signUp)))
      .andExpect(status().isOk())
      .andReturn().getResponse().getContentAsString();
    var token1 = om.readTree(signUpRes).get("token").asText();
    assertThat(token1).isNotBlank();

    var login = new AuthDtos.LoginRequest("john.doe@example.com", "Password#123");
    var loginRes = mvc.perform(post("/api/auth/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(om.writeValueAsString(login)))
      .andExpect(status().isOk())
      .andReturn().getResponse().getContentAsString();
    var token2 = om.readTree(loginRes).get("token").asText();
    assertThat(token2).isNotBlank();
  }
}

