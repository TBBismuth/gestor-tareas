import { useState } from "react";
import { ChevronDown, Users } from "lucide-react";
import Badge from "../../../components/ui/Badge.jsx";
import { cn } from "../../../lib/cn";
import { formatShortDate } from "../../../lib/dates";
import { getPriorityVisual, getStateVisual } from "../../../styles/taskVisualMaps";

export default function TaskCard({ task }) {
  const [expanded, setExpanded] = useState(false);
  const priority = getPriorityVisual(task.prioridad);
  const state = getStateVisual(task.estado);

  return (
    <article
      className="task-card-shell group relative overflow-hidden rounded-panel border border-app shadow-card transition hover:-translate-y-0.5"
      style={{
        "--task-priority-color": priority.color,
        "--task-state-color": state.color,
      }}
    >
      <div className="task-card-color-zone task-card-priority-zone" aria-hidden="true" />
      <div className="task-card-color-zone task-card-state-zone" aria-hidden="true" />

      <div className="task-card-center">
        <button
          type="button"
          className="grid w-full grid-cols-[1fr_auto] items-center gap-4 p-4 text-left"
          onClick={() => setExpanded((current) => !current)}
          aria-expanded={expanded}
        >
          <div className="min-w-0">
            <div className="flex flex-wrap items-center gap-2">
              <h3 className="truncate text-base font-semibold text-primary">{task.titulo}</h3>
              {task.origenTarea === "GRUPO" && (
                <Badge toneColor="var(--color-brand)">
                  <Users size={13} />
                  {task.nombreGrupoOrigen || "Grupo"}
                </Badge>
              )}
            </div>
            <div className="mt-2 flex flex-wrap items-center gap-2">
              <Badge toneColor={priority.color}>{priority.label}</Badge>
              <Badge toneColor={state.color}>{state.label}</Badge>
              <span className="text-sm text-muted">{task.tiempo} min</span>
              <span className="text-sm text-muted">{formatShortDate(task.fechaEntrega)}</span>
              {task.categoria && <span className="text-sm text-muted">{task.categoria}</span>}
            </div>
          </div>

          <ChevronDown
            size={20}
            className={cn(
              "mr-1 text-secondary transition group-hover:text-primary",
              expanded && "rotate-180"
            )}
          />
        </button>

        {expanded && (
          <div className="task-card-detail border-t border-app px-4 py-4">
            <p className="text-sm leading-6 text-secondary">{task.descripcion}</p>
            <dl className="mt-3 grid gap-2 text-sm text-secondary sm:grid-cols-3">
              <div>
                <dt className="font-semibold text-primary">Origen</dt>
                <dd>{task.origenTarea === "GRUPO" ? "Grupo" : "Personal"}</dd>
              </div>
              <div>
                <dt className="font-semibold text-primary">Prioridad</dt>
                <dd>{priority.label}</dd>
              </div>
              <div>
                <dt className="font-semibold text-primary">Estado</dt>
                <dd>{state.label}</dd>
              </div>
            </dl>
          </div>
        )}
      </div>
    </article>
  );
}
