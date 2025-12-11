import { useMemo, useState } from "react";
import "./App.css";
import {
  users,
  projects as initialProjects,
  tasks as initialTasks,
} from "./data/mockData";
import LoginPage from "./pages/LoginPage";
import SignupPage from "./pages/SignupPage";
import ProjectsPage from "./pages/ProjectsPage";
import TasksPage from "./pages/TasksPage";
import CreateProjectPage from "./pages/CreateProjectPage";
import CreateTaskPage from "./pages/CreateTaskPage";

const App = () => {
  const [view, setView] = useState("login");
  const [currentUser, setCurrentUser] = useState(null);
  const [selectedProject, setSelectedProject] = useState(null);
  const [projectList, setProjectList] = useState(initialProjects);
  const [taskList, setTaskList] = useState(initialTasks);

  const currentProject = useMemo(
    () => selectedProject || projectList[0],
    [selectedProject, projectList]
  );

  const handleLogin = ({ email }) => {
    const found = users.find((u) => u.email === email) || users[0];
    setCurrentUser(found);
    setView("projects");
  };

  const handleSignup = ({ email }) => {
    const provisionalUser = {
      id: "new-user",
      email,
      passwordHash: "hashed-new",
    };
    setCurrentUser(provisionalUser);
    setView("projects");
  };

  const handleSelectProject = (project) => {
    setSelectedProject(project);
    setView("tasks");
  };

  const handleCreateProject = (payload) => {
    const newProject = {
      id: `p${Date.now()}`,
      ...payload,
    };
    setProjectList((prev) => [...prev, newProject]);
    setSelectedProject(newProject);
    setView("projects");
  };

  const handleCreateTask = (payload) => {
    const newTask = {
      id: `t${Date.now()}`,
      ...payload,
    };
    setTaskList((prev) => [...prev, newTask]);
    setView("tasks");
  };

  const resetToAuth = () => {
    setSelectedProject(null);
    setCurrentUser(null);
    setView("login");
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
          currentUser={currentUser}
          projects={projectList}
          onSelectProject={handleSelectProject}
          onBackToAuth={resetToAuth}
          onCreateProject={() => setView("createProject")}
        />
      );
    }

    if (view === "createProject") {
      return (
        <CreateProjectPage
          currentUser={currentUser}
          onCreate={handleCreateProject}
          onCancel={() => setView("projects")}
        />
      );
    }

    if (view === "createTask") {
      return (
        <CreateTaskPage
          project={currentProject}
          onCreate={handleCreateTask}
          onCancel={() => setView("tasks")}
        />
      );
    }

    return (
      <TasksPage
        project={currentProject}
        tasks={taskList}
        onAddTask={() => setView("createTask")}
        onBack={() => {
          setSelectedProject(null);
          setView("projects");
        }}
      />
    );
  };

  return (
    <div className="min-h-screen bg-slate-950 text-slate-50">
      <div className="hero-backdrop">
        <div className="hero-content mx-auto max-w-5xl px-6 py-12 space-y-8">
          {renderPage()}
        </div>
      </div>
    </div>
  );
};

export default App;
