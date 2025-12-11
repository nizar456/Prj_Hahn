import { getUserProjects } from "../data/mockData";

const ProjectsPage = ({
  currentUser,
  projects,
  onSelectProject,
  onBackToAuth,
  onCreateProject,
}) => {
  const visibleProjects = currentUser
    ? getUserProjects(currentUser.id, projects)
    : projects;

  return (
    <div className="space-y-6 rounded-3xl border border-slate-800 bg-slate-900/70 p-8 shadow-2xl shadow-purple-900/30">
      <div className="flex flex-col gap-2 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <p className="text-xs uppercase tracking-[0.3em] text-amber-200">
            Projects
          </p>
          <h1 className="text-3xl font-semibold text-white">/api/projects</h1>
          <p className="text-sm text-slate-300">
            List projects for the authenticated user. Click any project to view
            its tasks.
          </p>
        </div>
        <div className="flex flex-wrap gap-2">
          <button
            type="button"
            onClick={onCreateProject}
            className="rounded-full bg-sky-500 px-4 py-2 text-xs font-semibold text-slate-950 shadow-sm shadow-sky-500/30 transition hover:bg-sky-400"
          >
            Créer un projet
          </button>
          <button
            type="button"
            onClick={onBackToAuth}
            className="rounded-full border border-slate-700 px-4 py-2 text-xs font-semibold text-slate-200 hover:border-slate-500"
          >
            Retour Auth
          </button>
        </div>
      </div>

      <div className="grid gap-4 md:grid-cols-2">
        {visibleProjects.map((project) => (
          <button
            key={project.id}
            type="button"
            onClick={() => onSelectProject(project)}
            className="group flex flex-col gap-2 rounded-2xl border border-slate-800 bg-slate-950/60 p-5 text-left transition hover:border-sky-400"
          >
            <div className="flex items-center justify-between">
              <p className="text-sm font-semibold text-white">
                {project.title}
              </p>
              <span className="rounded-full bg-slate-800 px-3 py-1 text-[11px] font-semibold text-slate-200 group-hover:bg-sky-500 group-hover:text-slate-950">
                GET
              </span>
            </div>
            <p className="text-xs text-slate-400">{project.description}</p>
            <p className="text-[11px] text-slate-500">
              Owner: {project.user_id}
            </p>
          </button>
        ))}
      </div>
    </div>
  );
};

export default ProjectsPage;
