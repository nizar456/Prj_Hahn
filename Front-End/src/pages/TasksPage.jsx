import { getProjectTasks, getProjectProgress } from "../data/mockData";

const TasksPage = ({ project, tasks, onBack, onAddTask }) => {
  const projectTasks = getProjectTasks(project.id, tasks);
  const progress = getProjectProgress(project.id, tasks);

  return (
    <div className="space-y-6 rounded-3xl border border-slate-800 bg-slate-900/70 p-8 shadow-2xl shadow-amber-900/30">
      <div className="flex flex-col gap-3 lg:flex-row lg:items-center lg:justify-between">
        <div>
          <p className="text-xs uppercase tracking-[0.3em] text-amber-200">
            Tasks
          </p>
          <h1 className="text-3xl font-semibold text-white">
            /api/projects/{project.id}/tasks
          </h1>
          <p className="text-sm text-slate-300">
            Tasks for {project.title}. Progress shown above the list as
            requested.
          </p>
        </div>
        <div className="flex flex-wrap gap-2 text-xs font-semibold text-slate-200">
          <button
            type="button"
            onClick={onAddTask}
            className="rounded-full bg-amber-400 px-4 py-2 text-slate-950 shadow-sm shadow-amber-400/30 transition hover:bg-amber-300"
          >
            Ajouter une tâche
          </button>
          <button
            type="button"
            onClick={onBack}
            className="rounded-full border border-slate-700 px-4 py-2 text-slate-200 hover:border-sky-400"
          >
            Retour projets
          </button>
        </div>
      </div>

      <div className="w-full max-w-xl">
        <div className="flex items-center justify-between text-xs font-semibold text-slate-200">
          <span>Progress</span>
          <span>{progress}%</span>
        </div>
        <div className="mt-2 h-3 rounded-full bg-slate-800">
          <div
            className="h-3 rounded-full bg-gradient-to-r from-amber-300 via-amber-400 to-amber-200"
            style={{ width: `${progress}%` }}
          />
        </div>
      </div>

      <div className="grid gap-3 lg:grid-cols-[2fr,1fr]">
        <div className="space-y-3">
          {projectTasks.map((task) => (
            <div
              key={task.id}
              className="flex flex-col gap-2 rounded-2xl border border-slate-800 bg-slate-950/60 p-4"
            >
              <div className="flex items-start justify-between gap-3">
                <div className="space-y-1">
                  <p className="text-sm font-semibold text-white">
                    {task.title}
                  </p>
                  <p className="text-xs text-slate-400">{task.description}</p>
                  <p className="text-[11px] text-slate-500">
                    Due: {task.dueDate}
                  </p>
                </div>
                <span
                  className={`rounded-full px-3 py-1 text-[11px] font-semibold ${
                    task.completed
                      ? "bg-emerald-900/40 text-emerald-200"
                      : "bg-slate-800 text-slate-200"
                  }`}
                >
                  {task.completed ? "Completed" : "Pending"}
                </span>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};

export default TasksPage;
