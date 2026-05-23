import { useState } from "react";
import { Check, ChevronDown, Pencil, Trash2, Users } from "lucide-react";
import Badge from "../../../components/ui/Badge.jsx";
import Button from "../../../components/ui/Button.jsx";
import { cn } from "../../../lib/cn";
import { getPriorityVisual, getStateVisual } from "../../../styles/taskVisualMaps";
import CategoryLabel from "./CategoryLabel.jsx";
import {
  formatDuration,
  formatGroupReviewState,
  formatTaskDate,
  formatTaskFullDate,
  getTaskCategoryName,
} from "../utils/taskFormatters.js";

function DetailItem({ label, children }) {
  return (
    <div>
      <dt className="font-semibold text-primary">{label}</dt>
      <dd>{children}</dd>
    </div>
  );
}

export default function TaskCard({
  isCompleting = false,
  isDeleting = false,
  onComplete,
  onDelete,
  onEdit,
  task,
}) {
  const [expanded, setExpanded] = useState(false);
  const priority = getPriorityVisual(task.prioridad);
  const state = getStateVisual(task.estado);
  const isGroupTask = task.origenTarea === "GRUPO";
  const isCompleted =
    task.completada ||
    task.estado === "COMPLETADA" ||
    task.estado === "COMPLETADA_CON_RETRASO";
  const categoryName = getTaskCategoryName(task);
  const renderCategoryLabel = () => (
    <CategoryLabel color={task.colorCategoria} icon={task.iconoCategoria} name={categoryName} />
  );
  const metaItems = [
    formatDuration(task.tiempo),
    formatTaskDate(task.fechaEntrega),
    renderCategoryLabel(),
  ];

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
              {isGroupTask && (
                <Badge toneColor="var(--color-brand)">
                  <Users size={13} />
                  {task.nombreGrupoOrigen || "Grupo"}
                </Badge>
              )}
            </div>
            <div className="mt-2 flex flex-wrap items-center gap-2">
              <Badge toneColor={priority.color}>{priority.label}</Badge>
              <Badge toneColor={state.color}>{state.label}</Badge>
              <span className="task-card-meta-separator" aria-hidden="true" />
              <span className="task-card-meta text-sm text-muted">
                {metaItems.map((item, index) => (
                  <span key={index}>{item}</span>
                ))}
              </span>
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
            {task.descripcion && (
              <p className="task-card-detail-description text-sm leading-6 text-secondary">
                {task.descripcion}
              </p>
            )}
            <dl className="mt-4 grid gap-3 text-sm text-secondary sm:grid-cols-2 xl:grid-cols-4">
              <DetailItem label="Origen">{isGroupTask ? "Grupo" : "Personal"}</DetailItem>
              {isGroupTask ? (
                <>
                  <DetailItem label="Estado grupal">
                    {formatGroupReviewState(task.estadoRevisionAsignacion)}
                  </DetailItem>
                  <DetailItem label="Fecha de entrega">
                    {formatTaskFullDate(task.fechaEntrega)}
                  </DetailItem>
                  <DetailItem label="Fecha de creacion">
                    {formatTaskFullDate(task.fechaAgregado)}
                  </DetailItem>
                </>
              ) : (
                <>
                  <DetailItem label="Categoria">{renderCategoryLabel()}</DetailItem>
                  <DetailItem label="Fecha de entrega">
                    {formatTaskFullDate(task.fechaEntrega)}
                  </DetailItem>
                  <DetailItem label="Fecha de creacion">
                    {formatTaskFullDate(task.fechaAgregado)}
                  </DetailItem>
                </>
              )}
            </dl>
            {isGroupTask && task.comentarioRevision && (
              <div className="mt-4 rounded-control border border-app bg-[color:var(--color-surface-card)] px-3 py-2 text-sm text-secondary">
                <p className="font-semibold text-primary">Comentario de revision</p>
                <p className="mt-1 leading-6">{task.comentarioRevision}</p>
              </div>
            )}
            <div className="mt-4 flex flex-wrap items-center gap-2">
              {!isCompleted && (
                <Button
                  className="success-action-button"
                  disabled={isCompleting}
                  onClick={() => onComplete?.(task)}
                >
                  <Check size={17} />
                  {isCompleting ? "Completando..." : "Completar"}
                </Button>
              )}
              <Button disabled={isDeleting} variant="secondary" onClick={() => onEdit?.(task)}>
                <Pencil size={17} />
                Editar
              </Button>
              <Button
                variant="secondary"
                className="danger-action-button"
                disabled={isDeleting}
                onClick={() => onDelete?.(task)}
              >
                <Trash2 size={17} />
                {isDeleting ? "Eliminando..." : "Borrar"}
              </Button>
            </div>
          </div>
        )}
      </div>
    </article>
  );
}
