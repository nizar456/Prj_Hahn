export const users = [
  { id: "u1", email: "samir@product.dev", passwordHash: "hashed-abc123" },
  { id: "u2", email: "lea@ops.dev", passwordHash: "hashed-xyz789" },
];

export const projects = [
  {
    id: "p1",
    title: "Client Onboarding",
    description: "Kick-off workflows and welcome materials for new clients.",
    user_id: "u1",
  },
  {
    id: "p2",
    title: "Mobile Refresh",
    description: "Polish the mobile UI and tighten performance budgets.",
    user_id: "u2",
  },
  {
    id: "p3",
    title: "Data Guardrails",
    description: "Define retention and access policies.",
    user_id: "u1",
  },
];

export const tasks = [
  {
    id: "t1",
    title: "Create welcome kit",
    description: "Templates, FAQs, and first-week checklist.",
    dueDate: "2025-01-15",
    completed: true,
    project_id: "p1",
  },
  {
    id: "t2",
    title: "Schedule kickoff",
    description: "Invite stakeholders and confirm agenda.",
    dueDate: "2025-01-18",
    completed: false,
    project_id: "p1",
  },
  {
    id: "t3",
    title: "Audit navigation",
    description: "Reduce taps for key user paths.",
    dueDate: "2025-02-02",
    completed: false,
    project_id: "p2",
  },
  {
    id: "t4",
    title: "Optimize images",
    description: "Move hero assets to AVIF + lazy-load.",
    dueDate: "2025-01-26",
    completed: true,
    project_id: "p2",
  },
  {
    id: "t5",
    title: "Draft policy v1",
    description: "Outline retention periods and access tiers.",
    dueDate: "2025-02-10",
    completed: false,
    project_id: "p3",
  },
];

export const getUserProjects = (userId, list = projects) =>
  list.filter((project) => project.user_id === userId);

export const getProjectTasks = (projectId, list = tasks) =>
  list.filter((task) => task.project_id === projectId);

export const getProjectProgress = (projectId, list = tasks) => {
  const scoped = getProjectTasks(projectId, list);
  if (!scoped.length) return 0;
  const done = scoped.filter((task) => task.completed).length;
  return Math.round((done / scoped.length) * 100);
};
