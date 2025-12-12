package Back_End.it;

import Back_End.dto.AuthDtos;
import Back_End.dto.ProjectDtos;
import Back_End.dto.TaskDtos;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
class ProjectTaskIntegrationTest extends BaseMySqlContainerTest {

  @Autowired MockMvc mvc;
  @Autowired ObjectMapper om;

  String token;

  @BeforeEach
  void setupUser() throws Exception {
    var email = "owner@example.com";
    var pwd = "Password#123";
    mvc.perform(post("/api/auth/sign-in").contentType(MediaType.APPLICATION_JSON)
        .content(om.writeValueAsString(new AuthDtos.SignUpRequest(email, pwd))))
      .andExpect(status().isOk());

    var login = mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
        .content(om.writeValueAsString(new AuthDtos.LoginRequest(email, pwd))))
      .andExpect(status().isOk())
      .andReturn().getResponse().getContentAsString();

    token = "Bearer " + om.readTree(login).get("token").asText();
  }

  @Test
  void project_and_tasks_crud() throws Exception {
    // Create project
    var pReq = new ProjectDtos.ProjectRequest("My Project", "Desc");
    var pResStr = mvc.perform(post("/api/projects")
        .header("Authorization", token)
        .contentType(MediaType.APPLICATION_JSON)
        .content(om.writeValueAsString(pReq)))
      .andExpect(status().isCreated())
      .andReturn().getResponse().getContentAsString();
    JsonNode pRes = om.readTree(pResStr);
    long projectId = pRes.get("id").asLong();

    // Create task
    var tReq = new TaskDtos.TaskRequest("Task 1", "Do it", java.time.LocalDate.now(), false);
    var tResStr = mvc.perform(post("/api/projects/{id}/tasks", projectId)
        .header("Authorization", token)
        .contentType(MediaType.APPLICATION_JSON)
        .content(om.writeValueAsString(tReq)))
      .andExpect(status().isCreated())
      .andReturn().getResponse().getContentAsString();
    JsonNode tRes = om.readTree(tResStr);
    long taskId = tRes.get("id").asLong();

    // List tasks
    mvc.perform(get("/api/projects/{id}/tasks", projectId)
        .header("Authorization", token))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[0].title").value("Task 1"));

    // Update task
    var tUpd = new TaskDtos.TaskRequest("Task 1 updated", "Do it now", java.time.LocalDate.now(), true);
    mvc.perform(put("/api/projects/{pid}/tasks/{tid}", projectId, taskId)
        .header("Authorization", token)
        .contentType(MediaType.APPLICATION_JSON)
        .content(om.writeValueAsString(tUpd)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.completed").value(true));

    // Get project and check progress
    var projStr = mvc.perform(get("/api/projects/{id}", projectId).header("Authorization", token))
      .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
    assertThat(om.readTree(projStr).get("progress").asDouble()).isEqualTo(100.0);

    // Delete task
    mvc.perform(delete("/api/projects/{pid}/tasks/{tid}", projectId, taskId)
        .header("Authorization", token))
      .andExpect(status().isNoContent());

    // Delete project
    mvc.perform(delete("/api/projects/{id}", projectId)
        .header("Authorization", token))
      .andExpect(status().isNoContent());
  }
}

