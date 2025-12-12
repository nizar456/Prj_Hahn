package Back_End.controller.project;

import Back_End.controller.GlobalExceptionHandler;
import Back_End.controller.ProjectController;
import Back_End.dto.ProjectDtos;
import Back_End.entity.Project;
import Back_End.entity.User;
import Back_End.security.JwtAuthenticationFilter;
import Back_End.security.JwtService;
import Back_End.service.ProjectService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ProjectController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({GlobalExceptionHandler.class})
@DisplayName("ProjectController - create() endpoint tests")
class ProjectControllerCreateTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProjectService projectService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private UserDetailsService userDetailsService;

    private MockMvc mockMvc;
    private User authenticatedUser;
    private Project createdProject;

    @BeforeEach
    void setUp() {
        // Build MockMvc from WebApplicationContext
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        authenticatedUser = User.builder()
                .id(1L)
                .email("user@test.com")
                .password("password123")
                .build();

        createdProject = Project.builder()
                .id(100L)
                .title("New Project")
                .description("Project Description")
                .owner(authenticatedUser)
                .build();

        // Configure SecurityContext for @AuthenticationPrincipal to work
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(authenticatedUser, null, authenticatedUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Nested
    @DisplayName("Happy path tests")
    class HappyPathTests {

        @Test
        @DisplayName("POST /api/projects - should return 201 Created with correct response body")
        void create_WhenValidRequest_ShouldReturn201WithProjectResponse() throws Exception {
            // Arrange
            ProjectDtos.ProjectRequest request = new ProjectDtos.ProjectRequest(
                    "New Project",
                    "Project Description"
            );

            ProjectDtos.ProjectResponse expectedResponse = new ProjectDtos.ProjectResponse(
                    100L,
                    "New Project",
                    "Project Description",
                    0f
            );

            when(projectService.create(any(Long.class), any(ProjectDtos.ProjectRequest.class)))
                    .thenReturn(createdProject);
            when(projectService.toResponse(any(Project.class))).thenReturn(expectedResponse);

            // Act & Assert
            mockMvc.perform(post("/api/projects")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", "/api/projects/100"))
                    .andExpect(jsonPath("$.id").value(100))
                    .andExpect(jsonPath("$.title").value("New Project"))
                    .andExpect(jsonPath("$.description").value("Project Description"))
                    .andExpect(jsonPath("$.progress").value(0.0));
        }

        @Test
        @DisplayName("POST /api/projects - should return 201 when description is null")
        void create_WhenDescriptionIsNull_ShouldReturn201() throws Exception {
            // Arrange
            ProjectDtos.ProjectRequest request = new ProjectDtos.ProjectRequest(
                    "Project Without Description",
                    null
            );

            Project projectWithoutDesc = Project.builder()
                    .id(101L)
                    .title("Project Without Description")
                    .description(null)
                    .owner(authenticatedUser)
                    .build();

            ProjectDtos.ProjectResponse expectedResponse = new ProjectDtos.ProjectResponse(
                    101L,
                    "Project Without Description",
                    null,
                    0f
            );

            when(projectService.create(any(Long.class), any(ProjectDtos.ProjectRequest.class)))
                    .thenReturn(projectWithoutDesc);
            when(projectService.toResponse(any(Project.class))).thenReturn(expectedResponse);

            // Act & Assert
            mockMvc.perform(post("/api/projects")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(101))
                    .andExpect(jsonPath("$.title").value("Project Without Description"))
                    .andExpect(jsonPath("$.description").isEmpty());
        }
    }

    @Nested
    @DisplayName("Validation error tests - 400 Bad Request")
    class ValidationErrorTests {

        @Test
        @DisplayName("POST /api/projects - should return 400 when title is blank")
        void create_WhenTitleIsBlank_ShouldReturn400WithValidationError() throws Exception {
            // Arrange
            ProjectDtos.ProjectRequest request = new ProjectDtos.ProjectRequest(
                    "",
                    "Some description"
            );

            // Act & Assert
            mockMvc.perform(post("/api/projects")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error").value("Validation error"))
                    .andExpect(jsonPath("$.message").value("Invalid request"))
                    .andExpect(jsonPath("$.path").value("/api/projects"))
                    .andExpect(jsonPath("$.validationErrors.title").exists());
        }

        @Test
        @DisplayName("POST /api/projects - should return 400 when title is null")
        void create_WhenTitleIsNull_ShouldReturn400WithValidationError() throws Exception {
            // Arrange
            ProjectDtos.ProjectRequest request = new ProjectDtos.ProjectRequest(
                    null,
                    "Some description"
            );

            // Act & Assert
            mockMvc.perform(post("/api/projects")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error").value("Validation error"))
                    .andExpect(jsonPath("$.validationErrors.title").exists());
        }

        @Test
        @DisplayName("POST /api/projects - should return 400 when title is too short (less than 3 chars)")
        void create_WhenTitleIsTooShort_ShouldReturn400WithValidationError() throws Exception {
            // Arrange
            ProjectDtos.ProjectRequest request = new ProjectDtos.ProjectRequest(
                    "AB",  // Only 2 characters, minimum is 3
                    "Some description"
            );

            // Act & Assert
            mockMvc.perform(post("/api/projects")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error").value("Validation error"))
                    .andExpect(jsonPath("$.validationErrors.title").exists());
        }

        @Test
        @DisplayName("POST /api/projects - should return 400 when title exceeds 120 characters")
        void create_WhenTitleIsTooLong_ShouldReturn400WithValidationError() throws Exception {
            // Arrange
            String longTitle = "A".repeat(121); // 121 characters, max is 120
            ProjectDtos.ProjectRequest request = new ProjectDtos.ProjectRequest(
                    longTitle,
                    "Some description"
            );

            // Act & Assert
            mockMvc.perform(post("/api/projects")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error").value("Validation error"))
                    .andExpect(jsonPath("$.validationErrors.title").exists());
        }

        @Test
        @DisplayName("POST /api/projects - should return 400 when description exceeds 1000 characters")
        void create_WhenDescriptionIsTooLong_ShouldReturn400WithValidationError() throws Exception {
            // Arrange
            String longDescription = "A".repeat(1001); // 1001 characters, max is 1000
            ProjectDtos.ProjectRequest request = new ProjectDtos.ProjectRequest(
                    "Valid Title",
                    longDescription
            );

            // Act & Assert
            mockMvc.perform(post("/api/projects")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error").value("Validation error"))
                    .andExpect(jsonPath("$.validationErrors.description").exists());
        }
    }
}
