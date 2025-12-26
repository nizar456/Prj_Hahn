const ProjectsPage = ({
  projects,
  onSelectProject,
  onBackToAuth,
  onCreateProject,
  onEditProject,
  onDeleteProject,
}) => {
  return (
    <div className="space-y-6 rounded-3xl border border-slate-800 bg-slate-900/70 p-8 shadow-2xl 
    shadow-purple-900/30">
      <div className="flex flex-col gap-2 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <p className="text-xs uppercase tracking-[0.3em] text-amber-200">
            Vos projets
          </p>
          <h1 className="text-3xl font-semibold text-white">Tableau de bord</h1>
          <p className="text-sm text-slate-300">
            Consultez, créez et gérez vos projets.
          </p>
        </div>
        <div className="flex flex-wrap gap-2">
          <button
            type="button"
            onClick={onCreateProject}
            className="rounded-full bg-sky-500 px-4 py-2 text-xs font-semibold text-slate-950 shadow-sm 
            shadow-sky-500/30 transition hover:bg-sky-400"
          >
            Créer un projet
          </button>
          <button
            type="button"
            onClick={onBackToAuth}
            className="rounded-full border border-slate-700 px-4 py-2 text-xs font-semibold text-slate-200 
            hover:border-slate-500"
          >
            Déconnexion
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
              <span className="rounded-full bg-slate-800 px-3 py-1 text-[11px] font-semibold 
              text-slate-200 group-hover:bg-sky-500 group-hover:text-slate-950">
                Progress: {Math.round(project.progress ?? 0)}%
              </span>
            </div>
            <p className="text-xs text-slate-400 line-clamp-3">
              {project.description || "Pas de description"}
            </p>
            <div className="flex flex-wrap gap-2 text-xs font-semibold text-slate-200">
              <button
                type="button"
                onClick={() => onSelectProject(project)}
                className="rounded-full bg-sky-500 px-3 py-2 text-slate-950 shadow-sm shadow-sky-500/30 
                transition hover:bg-sky-400"
              >
                Voir les tâches
              </button>
              <button
                type="button"
                onClick={() => onEditProject(project)}
                className="rounded-full border border-slate-700 px-3 py-2 text-slate-200 
                hover:border-sky-400"
              >
                Éditer
              </button>
              <button
                type="button"
                onClick={() => onDeleteProject(project.id)}
                className="rounded-full border border-red-900/70 px-3 py-2 text-red-200 
                hover:border-red-500 hover:text-red-100"
              >
                Supprimer
              </button>
            </div>
          </div>
        ))}
        {!projects.length && (
          <div className="rounded-2xl border border-dashed border-slate-700 bg-slate-950/40 p-6 text-sm 
          text-slate-300">
            Aucun projet pour le moment. Ajoutez votre premier projet pour
            commencer.
          </div>
        )}
      </div>
    </div>
  );
};

export default ProjectsPage;
