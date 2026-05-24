import { useEffect, useMemo, useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { ClipboardList, Users } from "lucide-react";
import Badge from "../../../components/ui/Badge.jsx";
import Modal from "../../../components/ui/Modal.jsx";
import { getPriorityVisual } from "../../../styles/taskVisualMaps.js";
import { cn } from "../../../lib/cn.js";
import { getGroupAssignmentDetail, getGroupAssignments } from "../api/groupsApi.js";
import { getAssignedGroupTasks } from "../api/tasksApi.js";
import {
  formatAssignmentType,
  formatDuration,
  formatGroupReviewState,
  formatTaskFullDate,
} from "../utils/taskFormatters.js";

const reviewStateTones = {
  PENDIENTE: "var(--color-text-muted)",
  ENTREGADA: "var(--state-warning-text)",
  VALIDADA: "var(--state-success-text)",
  REABIERTA: "var(--state-danger-text)",
};

function StateMessage({ tone = "default", children }) {
  const toneClass =
    tone === "danger"
      ? "border-[color:var(--state-danger-border)] bg-[color:var(--state-danger-bg)] text-[color:var(--state-danger-text)]"
      : "border-app bg-[color:var(--color-surface-card-muted)] text-secondary";

  return (
    <p className={cn("rounded-control border px-4 py-3 text-sm", toneClass)}>
      {children}
    </p>
  );
}

function ReviewBadge({ state }) {
  return (
    <Badge toneColor={reviewStateTones[state] || "var(--color-text-muted)"}>
      {formatGroupReviewState(state)}
    </Badge>
  );
}

function PriorityBadge({ priority }) {
  const visual = getPriorityVisual(priority);
  return <Badge toneColor={visual.color}>{visual.label}</Badge>;
}

function DetailItem({ label, children }) {
  return (
    <div>
      <dt className="text-xs font-semibold uppercase text-muted">{label}</dt>
      <dd className="mt-1 text-sm text-secondary">{children}</dd>
    </div>
  );
}

function AssignmentSummaryCard({ assignment, active, onSelect }) {
  return (
    <button
      className={cn(
        "w-full rounded-control border bg-[color:var(--color-surface-card-muted)] p-3 text-left transition hover:bg-[color:var(--color-bg-muted)]",
        active ? "border-[color:var(--color-brand)]" : "border-app"
      )}
      type="button"
      onClick={() => onSelect(assignment)}
    >
      <div className="flex items-start justify-between gap-3">
        <div className="min-w-0">
          <h3 className="truncate text-sm font-semibold text-primary">{assignment.titulo}</h3>
          {assignment.nombreCreadorAsignacion && (
            <p className="mt-1 truncate text-xs text-muted">
              Creada por {assignment.nombreCreadorAsignacion}
            </p>
          )}
        </div>
        <Badge toneColor="var(--color-brand)">{assignment.totalDestinatarios ?? 0}</Badge>
      </div>
      <div className="mt-3 flex flex-wrap items-center gap-2">
        <PriorityBadge priority={assignment.prioridad} />
        <Badge>{formatAssignmentType(assignment.tipoAsignacion)}</Badge>
      </div>
      <dl className="mt-3 grid gap-2 text-xs text-secondary sm:grid-cols-2">
        <DetailItem label="Fecha de entrega">
          {formatTaskFullDate(assignment.fechaEntrega)}
        </DetailItem>
        <DetailItem label="Fecha de creacion">
          {formatTaskFullDate(assignment.fechaCreacion)}
        </DetailItem>
        <DetailItem label="Tiempo">{formatDuration(assignment.tiempo)}</DetailItem>
        <DetailItem label="Total destinatarios">
          {assignment.totalDestinatarios ?? 0}
        </DetailItem>
      </dl>
    </button>
  );
}

function RecipientCard({ recipient }) {
  const displayName =
    recipient.nombreUsuarioMiembro || recipient.emailUsuarioMiembro || "Destinatario";

  return (
    <article className="rounded-control border border-app bg-[color:var(--color-surface-card-muted)] p-3">
      <div className="flex items-start justify-between gap-3">
        <div className="min-w-0">
          <h4 className="truncate text-sm font-semibold text-primary">{displayName}</h4>
          {recipient.emailUsuarioMiembro && (
            <p className="mt-1 truncate text-xs text-muted">{recipient.emailUsuarioMiembro}</p>
          )}
        </div>
        <ReviewBadge state={recipient.estadoRevision} />
      </div>
      <dl className="mt-3 grid gap-2 sm:grid-cols-3">
        <DetailItem label="Asignacion">
          {formatTaskFullDate(recipient.fechaAsignacion)}
        </DetailItem>
        <DetailItem label="Entrega">
          {formatTaskFullDate(recipient.fechaEntregaActual)}
        </DetailItem>
        <DetailItem label="Revision">
          {formatTaskFullDate(recipient.fechaRevision)}
        </DetailItem>
      </dl>
      {recipient.comentarioRevision && (
        <div className="mt-3 rounded-control border border-app bg-[color:var(--color-surface-card)] px-3 py-2 text-sm text-secondary">
          <p className="font-semibold text-primary">Comentario de revision</p>
          <p className="mt-1 leading-6">{recipient.comentarioRevision}</p>
        </div>
      )}
    </article>
  );
}

function AssignmentDetail({ detail, loading, error }) {
  if (loading) {
    return <StateMessage>Cargando detalle...</StateMessage>;
  }

  if (error) {
    return <StateMessage tone="danger">No se pudo cargar el detalle de la asignacion.</StateMessage>;
  }

  if (!detail) {
    return (
      <StateMessage>
        Selecciona una asignacion para ver su detalle y destinatarios.
      </StateMessage>
    );
  }

  const recipients = detail.destinatarios ?? [];

  return (
    <section className="grid gap-4">
      <div className="rounded-panel border border-app bg-[color:var(--color-surface-card-muted)] p-4">
        <div className="flex flex-col gap-3 sm:flex-row sm:items-start sm:justify-between">
          <div className="min-w-0">
            <h3 className="text-base font-semibold text-primary">{detail.titulo}</h3>
            {detail.descripcion && (
              <p className="mt-2 text-sm leading-6 text-secondary">{detail.descripcion}</p>
            )}
          </div>
          <div className="flex shrink-0 flex-wrap gap-2">
            <PriorityBadge priority={detail.prioridad} />
            <Badge>{formatAssignmentType(detail.tipoAsignacion)}</Badge>
          </div>
        </div>
        <dl className="mt-4 grid gap-3 sm:grid-cols-2 xl:grid-cols-4">
          <DetailItem label="Grupo">{detail.nombreGrupo || "Grupo"}</DetailItem>
          <DetailItem label="Tiempo">{formatDuration(detail.tiempo)}</DetailItem>
          <DetailItem label="Fecha de entrega">
            {formatTaskFullDate(detail.fechaEntrega)}
          </DetailItem>
          <DetailItem label="Total destinatarios">
            {detail.totalDestinatarios ?? recipients.length}
          </DetailItem>
        </dl>
      </div>

      <div>
        <h3 className="mb-3 text-sm font-semibold text-primary">Destinatarios</h3>
        {recipients.length === 0 ? (
          <StateMessage>Esta asignacion no tiene destinatarios.</StateMessage>
        ) : (
          <div className="grid gap-2">
            {recipients.map((recipient) => (
              <RecipientCard
                key={recipient.idAsignacionGrupoMiembro}
                recipient={recipient}
              />
            ))}
          </div>
        )}
      </div>
    </section>
  );
}

function AdminAssignmentsView({ group, open }) {
  const [selectedAssignmentId, setSelectedAssignmentId] = useState(null);
  const groupId = group?.idGrupo;
  const assignmentsQuery = useQuery({
    queryKey: ["groups", groupId, "assignments"],
    queryFn: () => getGroupAssignments(groupId),
    enabled: open && Boolean(groupId),
  });
  const assignments = assignmentsQuery.data ?? [];
  const selectedAssignment = assignments.find(
    (assignment) => String(assignment.idAsignacionGrupo) === String(selectedAssignmentId)
  );
  const detailQuery = useQuery({
    queryKey: ["groups", groupId, "assignments", selectedAssignmentId],
    queryFn: () => getGroupAssignmentDetail(groupId, selectedAssignmentId),
    enabled: open && Boolean(groupId) && Boolean(selectedAssignmentId),
  });

  useEffect(() => {
    if (!open) {
      setSelectedAssignmentId(null);
    }
  }, [open]);

  useEffect(() => {
    if (!selectedAssignmentId && assignments.length > 0) {
      setSelectedAssignmentId(assignments[0].idAsignacionGrupo);
    }
  }, [assignments, selectedAssignmentId]);

  if (assignmentsQuery.isLoading) {
    return <StateMessage>Cargando asignaciones...</StateMessage>;
  }

  if (assignmentsQuery.isError) {
    return <StateMessage tone="danger">No se pudieron cargar las asignaciones.</StateMessage>;
  }

  if (assignments.length === 0) {
    return <StateMessage>Este grupo todavia no tiene asignaciones.</StateMessage>;
  }

  return (
    <div className="grid gap-4 lg:grid-cols-[minmax(0,0.9fr)_minmax(0,1.25fr)]">
      <section>
        <h3 className="mb-3 flex items-center gap-2 text-sm font-semibold text-primary">
          <ClipboardList size={17} />
          Asignaciones
        </h3>
        <div className="grid max-h-[60vh] gap-2 overflow-y-auto pr-1">
          {assignments.map((assignment) => (
            <AssignmentSummaryCard
              active={String(selectedAssignment?.idAsignacionGrupo) === String(assignment.idAsignacionGrupo)}
              assignment={assignment}
              key={assignment.idAsignacionGrupo}
              onSelect={(nextAssignment) => setSelectedAssignmentId(nextAssignment.idAsignacionGrupo)}
            />
          ))}
        </div>
      </section>
      <section>
        <h3 className="mb-3 text-sm font-semibold text-primary">Detalle</h3>
        <AssignmentDetail
          detail={detailQuery.data}
          error={detailQuery.isError}
          loading={detailQuery.isLoading || detailQuery.isFetching}
        />
      </section>
    </div>
  );
}

function MemberTaskCard({ task }) {
  return (
    <article className="rounded-control border border-app bg-[color:var(--color-surface-card-muted)] p-3">
      <div className="flex flex-col gap-3 sm:flex-row sm:items-start sm:justify-between">
        <div className="min-w-0">
          <h3 className="text-sm font-semibold text-primary">{task.titulo}</h3>
          {task.descripcion && (
            <p className="mt-2 text-sm leading-6 text-secondary">{task.descripcion}</p>
          )}
        </div>
        <div className="flex shrink-0 flex-wrap gap-2">
          <PriorityBadge priority={task.prioridad} />
          <ReviewBadge state={task.estadoRevisionAsignacion} />
        </div>
      </div>
      <dl className="mt-4 grid gap-3 sm:grid-cols-2 xl:grid-cols-4">
        <DetailItem label="Grupo">{task.nombreGrupoOrigen || "Grupo"}</DetailItem>
        <DetailItem label="Tipo">{formatAssignmentType(task.tipoAsignacion)}</DetailItem>
        <DetailItem label="Tiempo">{formatDuration(task.tiempo)}</DetailItem>
        <DetailItem label="Fecha de entrega">
          {formatTaskFullDate(task.fechaEntrega)}
        </DetailItem>
      </dl>
      {task.comentarioRevision && (
        <div className="mt-3 rounded-control border border-app bg-[color:var(--color-surface-card)] px-3 py-2 text-sm text-secondary">
          <p className="font-semibold text-primary">Comentario de revision</p>
          <p className="mt-1 leading-6">{task.comentarioRevision}</p>
        </div>
      )}
    </article>
  );
}

function MemberTasksView({ group, open }) {
  const groupId = group?.idGrupo;
  const tasksQuery = useQuery({
    queryKey: ["groups", groupId, "assigned-tasks"],
    queryFn: () => getAssignedGroupTasks(groupId),
    enabled: open && Boolean(groupId),
  });
  const tasks = tasksQuery.data ?? [];

  if (tasksQuery.isLoading) {
    return <StateMessage>Cargando tareas del grupo...</StateMessage>;
  }

  if (tasksQuery.isError) {
    return <StateMessage tone="danger">No se pudieron cargar tus tareas de este grupo.</StateMessage>;
  }

  if (tasks.length === 0) {
    return <StateMessage>No tienes tareas asignadas en este grupo.</StateMessage>;
  }

  return (
    <section>
      <h3 className="mb-3 flex items-center gap-2 text-sm font-semibold text-primary">
        <Users size={17} />
        Mis tareas
      </h3>
      <div className="grid gap-2">
        {tasks.map((task) => (
          <MemberTaskCard key={task.idAsignacionGrupoMiembro || task.idTarea} task={task} />
        ))}
      </div>
    </section>
  );
}

export default function GroupAssignmentsModal({ group, mode, onClose, open }) {
  const resolvedMode = useMemo(() => {
    if (mode) return mode;
    return group?.creadorActual === true || group?.rolUsuarioActual === "ADMIN" ? "admin" : "member";
  }, [group, mode]);
  const title =
    resolvedMode === "admin"
      ? `Asignaciones de ${group?.nombre || "grupo"}`
      : `Mis tareas en ${group?.nombre || "grupo"}`;

  return (
    <Modal open={open} title={title} onClose={onClose} size="wide">
      {resolvedMode === "admin" ? (
        <AdminAssignmentsView group={group} open={open} />
      ) : (
        <MemberTasksView group={group} open={open} />
      )}
    </Modal>
  );
}
