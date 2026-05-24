import {
  ChevronDown,
  ClipboardList,
  KeyRound,
  Layers3,
  LogOut,
  Pencil,
  Power,
  Trash2,
  Users,
} from "lucide-react";
import { useState } from "react";
import Badge from "../../../components/ui/Badge.jsx";
import Button from "../../../components/ui/Button.jsx";
import { cn } from "../../../lib/cn";

function SecondaryActionButton({ children, danger = false, icon: Icon, onClick }) {
  return (
    <button
      className={cn(
        "inline-flex min-h-10 items-center justify-center gap-2 rounded-control border px-3 text-sm font-semibold transition",
        danger
          ? "border-[color:var(--state-danger-border)] bg-[color:var(--state-danger-bg)] text-[color:var(--state-danger-text)] hover:bg-[color:var(--state-danger-bg-hover)]"
          : "border-app bg-[color:var(--state-inactive-bg)] text-secondary hover:bg-[color:var(--color-bg-muted)]"
      )}
      type="button"
      onClick={(event) => {
        event.stopPropagation();
        onClick?.();
      }}
    >
      <Icon size={15} />
      {children}
    </button>
  );
}

export default function GroupCard({
  currentUserId,
  group,
  knownRole,
  onDelete,
  onEdit,
  onInvite,
  onLeave,
  onOpenAssignments,
  onToggleActive,
  onViewMembers,
}) {
  const [expanded, setExpanded] = useState(false);
  const isCreator =
    group.creadorActual === true ||
    (group.idCreador != null &&
      currentUserId != null &&
      String(group.idCreador) === String(currentUserId));
  const userRole = group.rolUsuarioActual || knownRole;
  const roleLabel = isCreator ? "Creador" : userRole === "ADMIN" ? "Admin" : "Miembro";
  const canEdit = isCreator || userRole === "ADMIN";
  const canInvite = isCreator || userRole === "ADMIN";
  const canToggleActive = isCreator;
  const canDelete = isCreator;
  const canLeave = !isCreator;
  const hasSecondaryActions = canEdit || canInvite || canToggleActive || canDelete || canLeave;
  const actionCount = [canEdit, canInvite, canToggleActive, canLeave, canDelete].filter(Boolean).length;
  const visualState = !expanded
    ? "collapsed"
    : actionCount === 1
      ? "expanded-single"
      : "expanded-multi";

  function runAction(action) {
    action?.(group);
  }

  function toggleExpanded() {
    if (!hasSecondaryActions) {
      return;
    }

    setExpanded((current) => !current);
  }

  return (
    <article
      className={cn(
        "category-card group relative overflow-hidden rounded-panel border border-app bg-card p-4 shadow-card transition-[box-shadow,transform] hover:-translate-y-0.5",
        visualState === "collapsed" && "group-card--collapsed",
        visualState === "expanded-single" && "group-card--expanded-single",
        visualState === "expanded-multi" && "group-card--expanded-multi",
        hasSecondaryActions && "cursor-pointer",
        visualState === "expanded-multi" && "xl:col-span-2 2xl:col-span-2"
      )}
      aria-expanded={expanded}
      onClick={toggleExpanded}
    >
      {group.color && (
        <div
          className="category-card-accent"
          style={{ "--category-accent-color": group.color }}
          aria-hidden="true"
        />
      )}

      <div className="relative z-10 flex min-h-44 flex-col justify-between gap-4 pb-6">
        <div className="min-w-0">
          <div className="flex items-start justify-between gap-3">
            <div className="flex min-w-0 items-center gap-2">
              <span className="grid size-10 shrink-0 place-items-center rounded-control border border-app bg-[color:var(--state-inactive-bg)] text-lg text-secondary">
                {group.icono || <Users size={19} />}
              </span>
              <div className="min-w-0">
                <h2 className="truncate text-base font-semibold text-primary">{group.nombre}</h2>
                {group.codigoPublico && (
                  <p className="mt-1 truncate text-xs font-semibold uppercase text-muted">
                    Codigo publico: {group.codigoPublico}
                  </p>
                )}
              </div>
            </div>
            <Layers3 className="shrink-0 text-muted" size={18} aria-hidden="true" />
          </div>

          {group.descripcion && (
            <p className="group-card-description mt-3 text-sm leading-6 text-secondary">
              {group.descripcion}
            </p>
          )}

          {group.nombreCreador && (
            <p className="mt-3 truncate text-sm text-muted">Creado por {group.nombreCreador}</p>
          )}
        </div>

        <div className="flex flex-wrap items-center gap-2">
          <Badge toneColor={group.activo ? "var(--state-success-text)" : "var(--color-text-muted)"}>
            {group.activo ? "Activo" : "Inactivo"}
          </Badge>
          <Badge toneColor={isCreator ? "var(--color-brand)" : "var(--color-text-muted)"}>
            {roleLabel}
          </Badge>
          <Button
            size="sm"
            type="button"
            variant="secondary"
            onClick={(event) => {
              event.stopPropagation();
              onViewMembers?.(group);
            }}
          >
            <Users size={15} />
            Ver miembros
          </Button>
          <Button
            size="sm"
            type="button"
            variant="secondary"
            onClick={(event) => {
              event.stopPropagation();
              onOpenAssignments?.(group, canEdit ? "admin" : "member");
            }}
          >
            <ClipboardList size={15} />
            {canEdit ? "Asignaciones" : "Mis tareas"}
          </Button>
        </div>

        {hasSecondaryActions && expanded && (
          <div
            className="group-card-actions-panel rounded-panel border border-app p-3"
            onClick={(event) => event.stopPropagation()}
          >
            <p className="mb-3 text-xs font-semibold uppercase tracking-wide text-muted">
              Acciones del grupo
            </p>
            <div
              className={cn(
                actionCount === 1
                  ? "flex flex-wrap justify-start gap-2"
                  : "grid gap-2 sm:grid-cols-2"
              )}
            >
              {canEdit && (
                <SecondaryActionButton icon={Pencil} onClick={() => runAction(onEdit)}>
                  Editar
                </SecondaryActionButton>
              )}
              {canInvite && (
                <SecondaryActionButton icon={KeyRound} onClick={() => runAction(onInvite)}>
                  Invitación
                </SecondaryActionButton>
              )}
              {canToggleActive && (
                <SecondaryActionButton icon={Power} onClick={() => runAction(onToggleActive)}>
                  {group.activo ? "Inactivar grupo" : "Activar grupo"}
                </SecondaryActionButton>
              )}
              {canLeave && (
                <SecondaryActionButton danger icon={LogOut} onClick={() => runAction(onLeave)}>
                  Salir del grupo
                </SecondaryActionButton>
              )}
              {canDelete && (
                <SecondaryActionButton danger icon={Trash2} onClick={() => runAction(onDelete)}>
                  Eliminar grupo
                </SecondaryActionButton>
              )}
            </div>
          </div>
        )}
      </div>

      {hasSecondaryActions && (
        <button
          aria-label={expanded ? "Contraer acciones del grupo" : "Expandir acciones del grupo"}
          aria-expanded={expanded}
          className="absolute bottom-2 left-1/2 z-20 grid size-8 -translate-x-1/2 place-items-center text-secondary transition hover:text-primary"
          type="button"
          onClick={(event) => {
            event.stopPropagation();
            toggleExpanded();
          }}
        >
          <ChevronDown
            size={18}
            className={cn("transition group-hover:text-primary", expanded && "rotate-180")}
          />
        </button>
      )}
    </article>
  );
}
