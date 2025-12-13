package Back_End.service.project;

import Back_End.dto.ProjectDtos;
import Back_End.entity.Project;
import Back_End.entity.User;
import Back_End.repository.ProjectRepository;
import Back_End.repository.TaskRepository;
import Back_End.repository.UserRepository;
import Back_End.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProjectService - toResponse() method tests")
class ProjectServiceToResponseTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private ProjectService projectService;

    private Project project;
    private User owner;

    @BeforeEach
    void setUp() {
        owner = User.builder()
                .id(1L)
                .email("owner@test.com")
                .password("password123")
                .build();

        project = Project.builder()
                .id(10L)
                .title("Test Project")
                .description("Test Description")
                .owner(owner)
                .build();
    }

    @Nested
    @DisplayName("Project attributes mapping tests")
    class ProjectAttributesMappingTests {

        @Test
        @DisplayName("toResponse() - should return ProjectResponse with correct id")
        void toResponse_ShouldReturnCorrectId() {
            // Arrange
            when(taskRepository.countByProjectId(project.getId())).thenReturn(0L);

            // Act
            ProjectDtos.ProjectResponse response = projectService.toResponse(project);

            // Assert
            assertThat(response.id()).isEqualTo(project.getId());
        }

        @Test
        @DisplayName("toResponse() - should return ProjectResponse with correct title")
        void toResponse_ShouldReturnCorrectTitle() {
            // Arrange
            when(taskRepository.countByProjectId(project.getId())).thenReturn(0L);

            // Act
            ProjectDtos.ProjectResponse response = projectService.toResponse(project);

            // Assert
            assertThat(response.title()).isEqualTo(project.getTitle());
        }

        @Test
        @DisplayName("toResponse() - should return ProjectResponse with correct description")
        void toResponse_ShouldReturnCorrectDescription() {
            // Arrange
            when(taskRepository.countByProjectId(project.getId())).thenReturn(0L);

            // Act
            ProjectDtos.ProjectResponse response = projectService.toResponse(project);

            // Assert
            assertThat(response.description()).isEqualTo(project.getDescription());
        }

        @Test
        @DisplayName("toResponse() - should return ProjectResponse matching all project attributes")
        void toResponse_ShouldReturnResponseMatchingAllProjectAttributes() {
            // Arrange
            when(taskRepository.countByProjectId(project.getId())).thenReturn(0L);

            // Act
            ProjectDtos.ProjectResponse response = projectService.toResponse(project);

            // Assert
            assertThat(response.id()).isEqualTo(project.getId());
            assertThat(response.title()).isEqualTo(project.getTitle());
            assertThat(response.description()).isEqualTo(project.getDescription());
        }

        @Test
        @DisplayName("toResponse() - should handle null description correctly")
        void toResponse_WhenDescriptionIsNull_ShouldReturnNullDescription() {
            // Arrange
            Project projectWithNullDesc = Project.builder()
                    .id(20L)
                    .title("No Description Project")
                    .description(null)
                    .owner(owner)
                    .build();
            when(taskRepository.countByProjectId(projectWithNullDesc.getId())).thenReturn(0L);

            // Act
            ProjectDtos.ProjectResponse response = projectService.toResponse(projectWithNullDesc);

            // Assert
            assertThat(response.id()).isEqualTo(20L);
            assertThat(response.title()).isEqualTo("No Description Project");
            assertThat(response.description()).isNull();
        }
    }

    @Nested
    @DisplayName("Progress calculation tests")
    class ProgressCalculationTests {

        @Test
        @DisplayName("toResponse() - should return 0% progress when project has no tasks")
        void toResponse_WhenNoTasks_ShouldReturnZeroProgress() {
            // Arrange
            when(taskRepository.countByProjectId(project.getId())).thenReturn(0L);

            // Act
            ProjectDtos.ProjectResponse response = projectService.toResponse(project);

            // Assert
            assertThat(response.progress()).isEqualTo(0f);
        }

        @Test
        @DisplayName("toResponse() - should return 0% progress when no tasks are completed")
        void toResponse_WhenNoTasksCompleted_ShouldReturnZeroProgress() {
            // Arrange
            when(taskRepository.countByProjectId(project.getId())).thenReturn(5L);
            when(taskRepository.countByProjectIdAndCompletedTrue(project.getId())).thenReturn(0L);

            // Act
            ProjectDtos.ProjectResponse response = projectService.toResponse(project);

            // Assert
            assertThat(response.progress()).isEqualTo(0f);
        }

        @Test
        @DisplayName("toResponse() - should return 100% progress when all tasks are completed")
        void toResponse_WhenAllTasksCompleted_ShouldReturn100Progress() {
            // Arrange
            when(taskRepository.countByProjectId(project.getId())).thenReturn(10L);
            when(taskRepository.countByProjectIdAndCompletedTrue(project.getId())).thenReturn(10L);

            // Act
            ProjectDtos.ProjectResponse response = projectService.toResponse(project);

            // Assert
            assertThat(response.progress()).isEqualTo(100f);
        }

        @Test
        @DisplayName("toResponse() - should return 50% progress when half tasks are completed")
        void toResponse_WhenHalfTasksCompleted_ShouldReturn50Progress() {
            // Arrange
            when(taskRepository.countByProjectId(project.getId())).thenReturn(4L);
            when(taskRepository.countByProjectIdAndCompletedTrue(project.getId())).thenReturn(2L);

            // Act
            ProjectDtos.ProjectResponse response = projectService.toResponse(project);

            // Assert
            assertThat(response.progress()).isEqualTo(50f);
        }

        @Test
        @DisplayName("toResponse() - should calculate correct progress for partial completion (3/10 = 30%)")
        void toResponse_WhenPartialCompletion_ShouldReturnCorrectProgress() {
            // Arrange
            when(taskRepository.countByProjectId(project.getId())).thenReturn(10L);
            when(taskRepository.countByProjectIdAndCompletedTrue(project.getId())).thenReturn(3L);

            // Act
            ProjectDtos.ProjectResponse response = projectService.toResponse(project);

            // Assert
            assertThat(response.progress()).isEqualTo(30f);
        }

        @Test
        @DisplayName("toResponse() - should calculate correct progress for 1/3 tasks (33.33%)")
        void toResponse_WhenOneOfThreeTasks_ShouldReturnCorrectProgress() {
            // Arrange
            when(taskRepository.countByProjectId(project.getId())).thenReturn(3L);
            when(taskRepository.countByProjectIdAndCompletedTrue(project.getId())).thenReturn(1L);

            // Act
            ProjectDtos.ProjectResponse response = projectService.toResponse(project);

            // Assert
            // 1/3 * 100 = 33.333...
            assertThat(response.progress()).isCloseTo(33.33f, org.assertj.core.data.Offset.offset(0.01f));
        }

        @Test
        @DisplayName("toResponse() - should calculate correct progress for 2/3 tasks (66.67%)")
        void toResponse_WhenTwoOfThreeTasks_ShouldReturnCorrectProgress() {
            // Arrange
            when(taskRepository.countByProjectId(project.getId())).thenReturn(3L);
            when(taskRepository.countByProjectIdAndCompletedTrue(project.getId())).thenReturn(2L);

            // Act
            ProjectDtos.ProjectResponse response = projectService.toResponse(project);

            // Assert
            // 2/3 * 100 = 66.666...
            assertThat(response.progress()).isCloseTo(66.67f, org.assertj.core.data.Offset.offset(0.01f));
        }

        @Test
        @DisplayName("toResponse() - should return 100% progress when single task is completed")
        void toResponse_WhenSingleTaskCompleted_ShouldReturn100Progress() {
            // Arrange
            when(taskRepository.countByProjectId(project.getId())).thenReturn(1L);
            when(taskRepository.countByProjectIdAndCompletedTrue(project.getId())).thenReturn(1L);

            // Act
            ProjectDtos.ProjectResponse response = projectService.toResponse(project);

            // Assert
            assertThat(response.progress()).isEqualTo(100f);
        }
    }
}

