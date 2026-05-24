import { useEffect, useMemo, useState } from "react";
import { useMutation, useQuery } from "@tanstack/react-query";
import { ClipboardList, Users } from "lucide-react";
import { useForm } from "react-hook-form";
import { toast } from "sonner";
import Badge from "../../../components/ui/Badge.jsx";
import Button from "../../../components/ui/Button.jsx";
import Modal from "../../../components/ui/Modal.jsx";
import { getPriorityVisual } from "../../../styles/taskVisualMaps.js";
import { cn } from "../../../lib/cn.js";
import { createGroupAssignment, getGroupMembers } from "../api/groupsApi.js";
import { formatAssignmentType } from "../utils/taskFormatters.js";

const priorities = [
  { value: "BAJA", label: "Baja" },
  { value: "MEDIA", label: "Media" },
  { value: "ALTA", label: "Alta" },
  { value: "IMPRESCINDIBLE", label: "Imprescindible" },
];

const defaultValues = {
  titulo: "",
  descripcion: "",
  prioridad: "MEDIA",
  tiempo: "",
  fechaEntrega: "",
  tipoAsignacion: "TODO_GRUPO",
};

function FieldError({ error }) {
  if (!error) return null;

  return <p className="auth-error">{error.message}</p>;
}

function getMemberName(member) {
  return member.nombre || member.email || "Miembro";
}

function isCreatorMember(member, group) {
  return (
    member.creador === true ||
    (group?.idCreador != null && String(member.idUsuario) === String(group.idCreador))
  );
}

function toPayload(values, selectedUserIds) {
  const tipoAsignacion = values.tipoAsignacion;

  return {
    titulo: values.titulo.trim(),
    descripcion: values.descripcion?.trim() || null,
    prioridad: values.prioridad,
    tiempo: Number(values.tiempo),
    fechaEntrega: values.fechaEntrega || null,
    tipoAsignacion,
    idsUsuarios: tipoAsignacion === "SELECCION_MANUAL" ? selectedUserIds : null,
  };
}

export default function GroupAssignmentFormModal({
  group,
  onClose,
  onCreated,
  open,
}) {
  const [selectedUserIds, setSelectedUserIds] = useState([]);
  const groupId = group?.idGrupo;
  const {
    register,
    handleSubmit,
    reset,
    watch,
    formState: { errors },
  } = useForm({ defaultValues });
  const selectedType = watch("tipoAsignacion");
  const selectedPriority = watch("prioridad");
  const selectedPriorityVisual = getPriorityVisual(selectedPriority);
  const membersQuery = useQuery({
    queryKey: ["groups", groupId, "members"],
    queryFn: () => getGroupMembers(groupId),
    enabled: open && Boolean(groupId),
  });
  const members = membersQuery.data ?? [];
  const assignableMembers = useMemo(
    () => members.filter((member) => !isCreatorMember(member, group)),
    [group, members]
  );
  const createAssignmentMutation = useMutation({
    mutationFn: (payload) => createGroupAssignment(groupId, payload),
    onSuccess: (assignment) => {
      toast.success("Asignacion creada.");
      reset(defaultValues);
      setSelectedUserIds([]);
      onCreated?.(assignment);
    },
    onError: (error) => {
      toast.error(error?.response?.data?.error || "No se pudo crear la asignacion.");
    },
  });

  useEffect(() => {
    if (!open) {
      reset(defaultValues);
      setSelectedUserIds([]);
    }
  }, [open, reset]);

  useEffect(() => {
    if (selectedType !== "SELECCION_MANUAL" && selectedUserIds.length > 0) {
      setSelectedUserIds([]);
    }
  }, [selectedType, selectedUserIds.length]);

  function toggleUser(member) {
    const id = member.idUsuario;
    if (id == null || isCreatorMember(member, group)) {
      return;
    }

    setSelectedUserIds((current) =>
      current.includes(id)
        ? current.filter((selectedId) => selectedId !== id)
        : [...current, id]
    );
  }

  async function submit(values) {
    if (values.tipoAsignacion === "SELECCION_MANUAL" && selectedUserIds.length === 0) {
      toast.error("Selecciona al menos un destinatario.");
      return;
    }

    await createAssignmentMutation.mutateAsync(toPayload(values, selectedUserIds));
  }

  return (
    <Modal
      open={open}
      title={`Nueva asignacion${group?.nombre ? ` en ${group.nombre}` : ""}`}
      onClose={createAssignmentMutation.isPending ? undefined : onClose}
      size="xl"
      zIndexClass="z-[60]"
    >
      <form className="grid gap-4" onSubmit={handleSubmit(submit)} noValidate>
        <div className="grid gap-2">
          <label className="text-sm font-semibold text-primary" htmlFor="assignment-title">
            Titulo *
          </label>
          <input
            id="assignment-title"
            className="auth-input"
            placeholder="Titulo de la asignacion"
            {...register("titulo", {
              required: "El titulo es obligatorio.",
              minLength: { value: 3, message: "El titulo debe tener al menos 3 caracteres." },
              maxLength: { value: 100, message: "El titulo no puede superar 100 caracteres." },
            })}
          />
          <FieldError error={errors.titulo} />
        </div>

        <div className="grid gap-2">
          <label className="text-sm font-semibold text-primary" htmlFor="assignment-description">
            Descripcion
          </label>
          <textarea
            id="assignment-description"
            className="auth-input min-h-24 resize-y"
            placeholder="Detalles opcionales"
            {...register("descripcion", {
              maxLength: {
                value: 1000,
                message: "La descripcion no puede superar 1000 caracteres.",
              },
            })}
          />
          <FieldError error={errors.descripcion} />
        </div>

        <div className="grid gap-4 sm:grid-cols-2">
          <div className="grid gap-2">
            <label className="text-sm font-semibold text-primary" htmlFor="assignment-priority">
              Prioridad *
            </label>
            <select
              id="assignment-priority"
              className="auth-input"
              style={{ color: selectedPriorityVisual.color }}
              {...register("prioridad", { required: "La prioridad es obligatoria." })}
            >
              {priorities.map((priority) => (
                <option
                  key={priority.value}
                  style={{ color: getPriorityVisual(priority.value).color }}
                  value={priority.value}
                >
                  {priority.label}
                </option>
              ))}
            </select>
            <span className="inline-flex items-center gap-2 text-xs font-semibold text-secondary">
              <span
                className="size-3 rounded-full border border-app"
                style={{ background: selectedPriorityVisual.color }}
                aria-hidden="true"
              />
              {selectedPriorityVisual.label}
            </span>
            <FieldError error={errors.prioridad} />
          </div>

          <div className="grid gap-2">
            <label className="text-sm font-semibold text-primary" htmlFor="assignment-time">
              Tiempo estimado (min) *
            </label>
            <input
              id="assignment-time"
              className="auth-input"
              min="1"
              placeholder="45"
              type="number"
              {...register("tiempo", {
                required: "El tiempo estimado es obligatorio.",
                valueAsNumber: true,
                min: { value: 1, message: "El tiempo debe ser mayor a 0." },
              })}
            />
            <FieldError error={errors.tiempo} />
          </div>
        </div>

        <div className="grid gap-4 sm:grid-cols-2">
          <div className="grid gap-2">
            <label className="text-sm font-semibold text-primary" htmlFor="assignment-due-date">
              Fecha de entrega
            </label>
            <input
              id="assignment-due-date"
              className="auth-input"
              type="datetime-local"
              {...register("fechaEntrega")}
            />
          </div>

          <div className="grid gap-2">
            <label className="text-sm font-semibold text-primary" htmlFor="assignment-type">
              Tipo de asignacion *
            </label>
            <select
              id="assignment-type"
              className="auth-input"
              {...register("tipoAsignacion", {
                required: "El tipo de asignacion es obligatorio.",
              })}
            >
              <option value="TODO_GRUPO">{formatAssignmentType("TODO_GRUPO")}</option>
              <option value="SELECCION_MANUAL">
                {formatAssignmentType("SELECCION_MANUAL")}
              </option>
            </select>
            <FieldError error={errors.tipoAsignacion} />
          </div>
        </div>

        {selectedType === "TODO_GRUPO" && (
          <p className="rounded-control border border-app bg-[color:var(--color-surface-card-muted)] px-4 py-3 text-sm text-secondary">
            Se asignara a todos los miembros del grupo excepto al creador.
          </p>
        )}

        {selectedType === "SELECCION_MANUAL" && (
          <section className="grid gap-3">
            <div className="flex flex-col gap-2 sm:flex-row sm:items-center sm:justify-between">
              <h3 className="flex items-center gap-2 text-sm font-semibold text-primary">
                <Users size={17} />
                Destinatarios *
              </h3>
              <Badge toneColor="var(--color-brand)">
                {selectedUserIds.length} seleccionados
              </Badge>
            </div>

            {membersQuery.isLoading && (
              <p className="rounded-control border border-app bg-[color:var(--color-surface-card-muted)] px-4 py-3 text-sm text-secondary">
                Cargando miembros...
              </p>
            )}

            {membersQuery.isError && (
              <p className="rounded-control border border-app bg-[color:var(--state-danger-bg)] px-4 py-3 text-sm text-[color:var(--state-danger-text)]">
                No se pudieron cargar los miembros.
              </p>
            )}

            {membersQuery.isSuccess && assignableMembers.length === 0 && (
              <p className="rounded-control border border-app bg-[color:var(--color-surface-card-muted)] px-4 py-3 text-sm text-secondary">
                No hay miembros asignables en este grupo.
              </p>
            )}

            {members.length > 0 && (
              <div className="grid max-h-72 gap-2 overflow-y-auto pr-1">
                {members.map((member) => {
                  const creator = isCreatorMember(member, group);
                  const checked = selectedUserIds.includes(member.idUsuario);

                  return (
                    <button
                      className={cn(
                        "flex w-full items-center gap-3 rounded-control border border-app bg-[color:var(--color-surface-card-muted)] px-3 py-3 text-left",
                        creator
                          ? "cursor-not-allowed opacity-60"
                          : "cursor-pointer transition hover:bg-[color:var(--color-bg-muted)]"
                      )}
                      aria-pressed={checked}
                      disabled={creator}
                      key={member.idGrupoMiembro}
                      onClick={() => toggleUser(member)}
                      type="button"
                    >
                      <span className="min-w-0 flex-1">
                        <span className="block truncate text-sm font-semibold text-primary">
                          {getMemberName(member)}
                        </span>
                        {member.email && (
                          <span className="mt-1 block truncate text-xs text-muted">
                            {member.email}
                          </span>
                        )}
                      </span>
                      <span className="flex shrink-0 flex-wrap items-center justify-end gap-2">
                        <Badge>{creator ? "Creador" : member.rol === "ADMIN" ? "Admin" : "Miembro"}</Badge>
                        <span
                          className={cn(
                            "relative inline-flex h-6 w-11 shrink-0 rounded-full border transition",
                            checked
                              ? "border-[color:var(--color-brand)] bg-[color:var(--color-brand)]"
                              : "border-app bg-[color:var(--state-inactive-bg)]"
                          )}
                          aria-hidden="true"
                        >
                          <span
                            className={cn(
                              "absolute top-1/2 size-4 -translate-y-1/2 rounded-full bg-[color:var(--color-text-inverse)] shadow-sm transition",
                              checked ? "left-6" : "left-1"
                            )}
                          />
                        </span>
                      </span>
                    </button>
                  );
                })}
              </div>
            )}
          </section>
        )}

        <div className="mt-2 flex flex-col-reverse gap-2 sm:flex-row sm:justify-end">
          <Button
            disabled={createAssignmentMutation.isPending}
            onClick={onClose}
            type="button"
            variant="secondary"
          >
            Cancelar
          </Button>
          <Button disabled={createAssignmentMutation.isPending} type="submit">
            <ClipboardList size={17} />
            {createAssignmentMutation.isPending ? "Creando..." : "Crear asignacion"}
          </Button>
        </div>
      </form>
    </Modal>
  );
}
