import { useLanguage } from "../i18n/useLanguage";

const ProjectsPage = ({
  projects,
  onSelectProject,
  onBackToAuth,
  onCreateProject,
  onEditProject,
  onDeleteProject,
}) => {
  const { t } = useLanguage();

  return (
    <div
      className="space-y-6 rounded-3xl border border-slate-800 bg-slate-900/70 p-8 shadow-2xl 
    shadow-purple-900/30"
    >
      <div className="flex flex-col gap-2 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <p className="text-xs uppercase tracking-[0.3em] text-amber-200">
            {t("yourProjects")}
          </p>
          <h1 className="text-3xl font-semibold text-white">
            {t("dashboard")}
          </h1>
          <p className="text-sm text-slate-300">{t("dashboardDescription")}</p>
        </div>
        <div className="flex flex-wrap gap-2">
          <button
            type="button"
            onClick={onCreateProject}
            className="rounded-full bg-sky-500 px-4 py-2 text-xs font-semibold text-slate-950 shadow-sm 
            shadow-sky-500/30 transition hover:bg-sky-400"
          >
            {t("createProject")}
          </button>
          <button
            type="button"
            onClick={onBackToAuth}
            className="rounded-full border border-slate-700 px-4 py-2 text-xs font-semibold text-slate-200 
            hover:border-slate-500"
          >
            {t("logout")}
          </button>
        </div>
      </div>

      <div className="grid gap-4 md:grid-cols-2">
        {projects.map((project) => (
          <div
            key={project.id}
            className="group flex flex-col gap-3 rounded-2xl border border-slate-800 bg-slate-950/60 
            p-5 transition hover:border-sky-400"
          >
            <div className="flex items-center justify-between">
              <p className="text-sm font-semibold text-white">
                {project.title}
              </p>
              <span
                className="rounded-full bg-slate-800 px-3 py-1 text-[11px] font-semibold 
              text-slate-200 group-hover:bg-sky-500 group-hover:text-slate-950"
              >
                {t("progress")}: {Math.round(project.progress ?? 0)}%
              </span>
            </div>
            <p className="text-xs text-slate-400 line-clamp-3">
              {project.description || t("noDescription")}
            </p>
            <div className="flex flex-wrap gap-2 text-xs font-semibold text-slate-200">
              <button
                type="button"
                onClick={() => onSelectProject(project)}
                className="rounded-full bg-sky-500 px-3 py-2 text-slate-950 shadow-sm shadow-sky-500/30 
                transition hover:bg-sky-400"
              >
                {t("viewTasks")}
              </button>
              <button
                type="button"
                onClick={() => onEditProject(project)}
                className="rounded-full border border-slate-700 px-3 py-2 text-slate-200 
                hover:border-sky-400"
              >
                {t("edit")}
              </button>
              <button
                type="button"
                onClick={() => onDeleteProject(project.id)}
                className="rounded-full border border-red-900/70 px-3 py-2 text-red-200 
                hover:border-red-500 hover:text-red-100"
              >
                {t("delete")}
              </button>
            </div>
          </div>
        ))}
        {!projects.length && (
          <div
            className="rounded-2xl border border-dashed border-slate-700 bg-slate-950/40 p-6 text-sm 
          text-slate-300"
          >
            {t("noProjectsYet")}
          </div>
        )}
      </div>
    </div>
  );
};

export default ProjectsPage;
