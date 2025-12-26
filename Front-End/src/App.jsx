import { useMemo, useState } from "react";
import "./App.css";
import { api } from "./api/client";
import { useLanguage } from "./i18n/LanguageContext";
import LanguageSwitcher from "./components/LanguageSwitcher";
import LoginPage from "./pages/LoginPage";
import SignupPage from "./pages/SignupPage";
import ProjectsPage from "./pages/ProjectsPage";
import TasksPage from "./pages/TasksPage";
import CreateProjectPage from "./pages/CreateProjectPage";
import CreateTaskPage from "./pages/CreateTaskPage";

const App = () => {
  const { t } = useLanguage();
  const [view, setView] = useState("login");
  const [token, setToken] = useState(null);
  const [projects, setProjects] = useState([]);
  const [tasksByProject, setTasksByProject] = useState({});
  const [selectedProjectId, setSelectedProjectId] = useState(null);
  const [editingProject, setEditingProject] = useState(null);
  const [editingTask, setEditingTask] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const currentProject = useMemo(() => {
    if (!projects.length) return null;
    return projects.find((p) => p.id === selectedProjectId) ?? projects[0];
  }, [projects, selectedProjectId]);

  const currentTasks = currentProject
    ? tasksByProject[currentProject.id] || []
    : [];

  const resetToAuth = () => {
    setToken(null);
    setProjects([]);
    setTasksByProject({});
    setSelectedProjectId(null);
    setEditingProject(null);
    setEditingTask(null);
    setView("login");
  };

  const run = async (fn) => {
    setLoading(true);
    setError(null);
    try {
      return await fn();
    } catch (err) {
      setError(err?.message || t("errorOccurred"));
      if (err?.status === 401) resetToAuth();
      return null;
    } finally {
      setLoading(false);
    }
  };

  const fetchProjects = async (authToken = token) => {
    const data = await api.listProjects(authToken);
    setProjects(data);
    if (!data.length) {
      setSelectedProjectId(null);
      return data;
    }
    const stillSelected = data.find((p) => p.id === selectedProjectId);
    setSelectedProjectId(stillSelected ? stillSelected.id : data[0].id);
    return data;
  };

  const fetchTasks = async (projectId, authToken = token) => {
    const data = await api.listTasks(authToken, projectId);
    setTasksByProject((prev) => ({ ...prev, [projectId]: data }));
    return data;
  };

  const handleLogin = async ({ email, password }) => {
    await run(async () => {
      const { token: received } = await api.login(email, password);
      setToken(received);
      await fetchProjects(received);
      setView("projects");
    });
  };

  const handleSignup = async ({ email, password }) => {
    await run(async () => {
      const { token: received } = await api.signUp(email, password);
      setToken(received);
      await fetchProjects(received);
      setView("projects");
    });
  };

  const handleSelectProject = async (project) => {
    setSelectedProjectId(project.id);
    setView("tasks");
    if (!tasksByProject[project.id]) {
      await run(() => fetchTasks(project.id));
    }
  };

  const handleCreateProject = async (payload) => {
    await run(async () => {
      const created = await api.createProject(token, payload);
      setProjects((prev) => [...prev, created]);
      setSelectedProjectId(created.id);
      setView("projects");
    });
  };

  const handleUpdateProject = async (projectId, payload) => {
    await run(async () => {
      const updated = await api.updateProject(token, projectId, payload);
      setProjects((prev) =>
        prev.map((p) => (p.id === projectId ? updated : p))
      );
      setSelectedProjectId(projectId);
      setEditingProject(null);
      setView("projects");
    });
  };

  const handleDeleteProject = async (projectId) => {
    await run(async () => {
      await api.deleteProject(token, projectId);
      setProjects((prev) => prev.filter((p) => p.id !== projectId));
      setTasksByProject((prev) => {
        const copy = { ...prev };
        delete copy[projectId];
        return copy;
      });
      if (selectedProjectId === projectId) {
        setSelectedProjectId(null);
        setView("projects");
      }
    });
  };

  const handleCreateTask = async (payload) => {
    if (!currentProject) return;
    await run(async () => {
      const created = await api.createTask(token, currentProject.id, payload);
      setTasksByProject((prev) => {
        const list = prev[currentProject.id] || [];
        return { ...prev, [currentProject.id]: [...list, created] };
      });
      setView("tasks");
    });
  };

  const handleUpdateTask = async (taskId, payload) => {
    if (!currentProject) return;
    await run(async () => {
      const updated = await api.updateTask(
        token,
        currentProject.id,
        taskId,
        payload
      );
      setTasksByProject((prev) => {
        const list = prev[currentProject.id] || [];
        return {
          ...prev,
          [currentProject.id]: list.map((t) => (t.id === taskId ? updated : t)),
        };
      });
      setEditingTask(null);
      setView("tasks");
    });
  };

  const handleDeleteTask = async (taskId) => {
    if (!currentProject) return;
    await run(async () => {
      await api.deleteTask(token, currentProject.id, taskId);
      setTasksByProject((prev) => {
        const list = prev[currentProject.id] || [];
        return {
          ...prev,
          [currentProject.id]: list.filter((t) => t.id !== taskId),
        };
      });
    });
  };

  const handleToggleTaskCompletion = (task) => {
    handleUpdateTask(task.id, {
      title: task.title,
      description: task.description,
      dueDate: task.dueDate,
      completed: !task.completed,
    });
  };

  const renderPage = () => {
    if (view === "login") {
      return (
        <LoginPage
          onConnect={handleLogin}
          onGoSignup={() => setView("signup")}
        />
      );
    }

    if (view === "signup") {
      return (
        <SignupPage
          onSignup={handleSignup}
          onGoLogin={() => setView("login")}
        />
      );
    }

    if (view === "projects") {
      return (
        <ProjectsPage
          projects={projects}
          onSelectProject={handleSelectProject}
          onBackToAuth={resetToAuth}
          onCreateProject={() => setView("createProject")}
          onEditProject={(project) => {
            setEditingProject(project);
            setView("editProject");
          }}
          onDeleteProject={handleDeleteProject}
        />
      );
    }

    if (view === "createProject") {
      return (
        <CreateProjectPage
          onSubmit={handleCreateProject}
          onCancel={() => setView("projects")}
        />
      );
    }

    if (view === "editProject" && editingProject) {
      return (
        <CreateProjectPage
          mode="edit"
          initialProject={editingProject}
          onSubmit={(payload) =>
            handleUpdateProject(editingProject.id, payload)
          }
          onCancel={() => {
            setEditingProject(null);
            setView("projects");
          }}
        />
      );
    }

    if (view === "createTask" && currentProject) {
      return (
        <CreateTaskPage
          project={currentProject}
          onSubmit={handleCreateTask}
          onCancel={() => setView("tasks")}
        />
      );
    }

    if (view === "editTask" && currentProject && editingTask) {
      return (
        <CreateTaskPage
          project={currentProject}
          mode="edit"
          initialTask={editingTask}
          onSubmit={(payload) => handleUpdateTask(editingTask.id, payload)}
          onCancel={() => {
            setEditingTask(null);
            setView("tasks");
          }}
        />
      );
    }

    if (!currentProject) {
      return (
        <div className="rounded-3xl border border-slate-800 bg-slate-900/70 p-8 text-slate-200">
          {t("noProjectsYet")}
          <div className="mt-4">
            <button
              type="button"
              onClick={() => setView("projects")}
              className="rounded-full border border-slate-700 px-4 py-2 text-sm font-semibold text-slate-200 hover:border-sky-400"
            >
              {t("backToProjects")}
            </button>
          </div>
        </div>
      );
    }

    return (
      <TasksPage
        project={currentProject}
        tasks={currentTasks}
        onAddTask={() => setView("createTask")}
        onBack={() => setView("projects")}
        onEditTask={(task) => {
          setEditingTask(task);
          setView("editTask");
        }}
        onDeleteTask={handleDeleteTask}
        onToggleTask={handleToggleTaskCompletion}
        onRefreshTasks={() =>
          currentProject && run(() => fetchTasks(currentProject.id))
        }
      />
    );
  };

  return (
    <div className="min-h-screen bg-slate-950 text-slate-50">
      <LanguageSwitcher />
      <div className="hero-backdrop">
        <div className="hero-content mx-auto max-w-5xl px-6 py-12 space-y-6">
          {loading && (
            <div className="rounded-xl border border-slate-800 bg-slate-900/80 px-4 py-3 text-sm text-slate-200">
              Loading...
            </div>
          )}
          {error && (
            <div className="rounded-xl border border-red-900/60 bg-red-900/30 px-4 py-3 text-sm text-red-100">
              {error}
            </div>
          )}
          {renderPage()}
        </div>
      </div>
    </div>
  );
};

export default App;
