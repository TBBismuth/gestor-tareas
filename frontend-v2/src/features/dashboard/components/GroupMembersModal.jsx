import { useEffect, useState } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { Shield, ShieldMinus, Trash2, UserCheck, UserPlus, Users } from "lucide-react";
import { toast } from "sonner";
import { useForm } from "react-hook-form";
import Badge from "../../../components/ui/Badge.jsx";
import Button from "../../../components/ui/Button.jsx";
import Modal from "../../../components/ui/Modal.jsx";
import {
  addGroupMember,
  getGroupMembers,
  removeGroupMember,
  updateGroupMemberRole,
} from "../api/groupsApi.js";
import RemoveGroupMemberModal from "./RemoveGroupMemberModal.jsx";

const addMemberDefaultValues = {
  email: "",
};

function getRoleLabel(member, group) {
  if (member.creador || String(member.idUsuario) === String(group?.idCreador)) {
    return "Creador";
  }

  return member.rol === "ADMIN" ? "Admin" : "Miembro";
}

function getRoleTone(member, group) {
  const role = getRoleLabel(member, group);

  if (role === "Creador") return "var(--color-brand)";
  if (role === "Admin") return "var(--state-success-text)";
  return "var(--color-text-muted)";
}

function getMemberDisplayName(member) {
  return member.nombre || member.email || "Miembro";
}

function FieldError({ error }) {
  if (!error) return null;

  return <p className="auth-error">{error.message}</p>;
}

export default function GroupMembersModal({
  currentUserId,
  group,
  onClose,
  onMembersLoaded,
  open,
}) {
  const queryClient = useQueryClient();
  const [memberToRemove, setMemberToRemove] = useState(null);
  const groupId = group?.idGrupo;
  const membersQueryKey = ["groups", groupId, "members"];
  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm({ defaultValues: addMemberDefaultValues });
  const membersQuery = useQuery({
    queryKey: membersQueryKey,
    queryFn: () => getGroupMembers(groupId),
    enabled: open && Boolean(groupId),
  });
  const members = membersQuery.data ?? [];
  const currentMember = members.find(
    (member) => String(member.idUsuario) === String(currentUserId)
  );
  const currentUserIsCreator =
    currentMember?.creador || String(group?.idCreador) === String(currentUserId);
  const currentUserIsAdmin = currentUserIsCreator || currentMember?.rol === "ADMIN";
  const canAddMembers = currentUserIsAdmin;
  const canChangeRoles = currentUserIsCreator;
  const addMemberMutation = useMutation({
    mutationFn: (payload) => addGroupMember(groupId, payload),
    onSuccess: () => {
      toast.success("Miembro añadido.");
      reset(addMemberDefaultValues);
      queryClient.invalidateQueries({ queryKey: membersQueryKey });
      queryClient.invalidateQueries({ queryKey: ["groups", "mine"] });
    },
    onError: (error) => {
      toast.error(error?.response?.data?.error || "No se pudo añadir el miembro.");
    },
  });
  const removeMemberMutation = useMutation({
    mutationFn: ({ member }) => removeGroupMember(groupId, member.idUsuario),
    onSuccess: () => {
      toast.success("Miembro expulsado.");
      setMemberToRemove(null);
      queryClient.invalidateQueries({ queryKey: membersQueryKey });
      queryClient.invalidateQueries({ queryKey: ["groups", "mine"] });
    },
    onError: (error) => {
      toast.error(error?.response?.data?.error || "No se pudo expulsar el miembro.");
    },
  });
  const updateRoleMutation = useMutation({
    mutationFn: ({ member, nextRole }) =>
      updateGroupMemberRole(groupId, member.idUsuario, { rol: nextRole }),
    onSuccess: () => {
      toast.success("Rol actualizado.");
      queryClient.invalidateQueries({ queryKey: membersQueryKey });
      queryClient.invalidateQueries({ queryKey: ["groups", "mine"] });
    },
    onError: (error) => {
      toast.error(error?.response?.data?.error || "No se pudo actualizar el rol.");
    },
  });

  useEffect(() => {
    if (!open) {
      reset(addMemberDefaultValues);
      setMemberToRemove(null);
    }
  }, [open, reset]);

  useEffect(() => {
    if (membersQuery.isSuccess && groupId) {
      onMembersLoaded?.(groupId, members);
    }
  }, [groupId, members, membersQuery.isSuccess, onMembersLoaded]);

  function canRemoveMember(member) {
    const targetIsCurrentUser = String(member.idUsuario) === String(currentUserId);
    const targetIsCreator = member.creador || String(member.idUsuario) === String(group?.idCreador);

    if (targetIsCurrentUser || targetIsCreator) {
      return false;
    }

    if (currentUserIsCreator) {
      return true;
    }

    return currentMember?.rol === "ADMIN" && member.rol === "MIEMBRO";
  }

  function canChangeMemberRole(member) {
    const targetIsCurrentUser = String(member.idUsuario) === String(currentUserId);
    const targetIsCreator = member.creador || String(member.idUsuario) === String(group?.idCreador);

    return canChangeRoles && !targetIsCurrentUser && !targetIsCreator;
  }

  function handleAddMember(values) {
    return addMemberMutation.mutateAsync({ email: values.email.trim() });
  }

  function handleConfirmRemoveMember() {
    if (!memberToRemove || removeMemberMutation.isPending) {
      return;
    }

    removeMemberMutation.mutate({ member: memberToRemove });
  }

  function handleToggleRole(member) {
    if (updateRoleMutation.isPending) {
      return;
    }

    updateRoleMutation.mutate({
      member,
      nextRole: member.rol === "ADMIN" ? "MIEMBRO" : "ADMIN",
    });
  }

  return (
    <Modal open={open} title={group ? `Miembros de ${group.nombre}` : "Miembros"} onClose={onClose}>
      <div className="grid gap-4">
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

        {membersQuery.isSuccess && canAddMembers && (
          <form
            className="rounded-panel border border-app bg-[color:var(--color-surface-card-muted)] p-3"
            onSubmit={handleSubmit(handleAddMember)}
            noValidate
          >
            <div className="grid gap-2">
              <label className="text-sm font-semibold text-primary" htmlFor="group-member-email">
                Email del usuario
              </label>
              <div className="flex flex-col gap-2 sm:flex-row">
                <input
                  id="group-member-email"
                  className="auth-input"
                  placeholder="usuario@correo.com"
                  type="email"
                  {...register("email", {
                    required: "El email es obligatorio.",
                    pattern: {
                      value: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
                      message: "Introduce un email válido.",
                    },
                  })}
                />
                <Button
                  className="shrink-0"
                  disabled={addMemberMutation.isPending}
                  type="submit"
                >
                  <UserPlus size={17} />
                  {addMemberMutation.isPending ? "Añadiendo..." : "Añadir miembro"}
                </Button>
              </div>
              <FieldError error={errors.email} />
            </div>
          </form>
        )}

        {membersQuery.isSuccess && members.length === 0 && (
          <p className="rounded-control border border-app bg-[color:var(--color-surface-card-muted)] px-4 py-3 text-sm text-secondary">
            Este grupo todavía no tiene miembros.
          </p>
        )}

        {members.length > 0 && (
          <div className="grid gap-2">
            {members.map((member) => {
              const isCurrentUser = String(member.idUsuario) === String(currentUserId);
              const roleLabel = getRoleLabel(member, group);
              const removable = canRemoveMember(member);
              const roleEditable = canChangeMemberRole(member);

              return (
                <article
                  className="rounded-control border border-app bg-[color:var(--color-surface-card-muted)] px-3 py-3"
                  key={member.idGrupoMiembro}
                >
                  <div className="flex items-start justify-between gap-3">
                    <div className="flex min-w-0 items-start gap-3">
                      <span className="grid size-9 shrink-0 place-items-center rounded-control border border-app bg-[color:var(--state-inactive-bg)] text-secondary">
                        {roleLabel === "Creador" ? <UserCheck size={17} /> : <Users size={17} />}
                      </span>
                      <div className="min-w-0">
                        <h3 className="truncate text-sm font-semibold text-primary">
                          {getMemberDisplayName(member)}
                        </h3>
                        {member.email && (
                          <p className="mt-1 truncate text-xs text-muted">{member.email}</p>
                        )}
                      </div>
                    </div>
                    <div className="flex shrink-0 flex-wrap justify-end gap-2">
                      <Badge toneColor={getRoleTone(member, group)}>{roleLabel}</Badge>
                      {isCurrentUser && <Badge>Tu</Badge>}
                    </div>
                  </div>
                  {(removable || roleEditable) && (
                    <div className="mt-3 flex flex-wrap justify-end gap-2">
                      {roleEditable && (
                        <Button
                          disabled={updateRoleMutation.isPending}
                          onClick={() => handleToggleRole(member)}
                          size="sm"
                          type="button"
                          variant="secondary"
                        >
                          {member.rol === "ADMIN" ? (
                            <ShieldMinus size={15} />
                          ) : (
                            <Shield size={15} />
                          )}
                          {member.rol === "ADMIN" ? "Quitar admin" : "Hacer admin"}
                        </Button>
                      )}
                      {removable && (
                        <Button
                          className="danger-action-button"
                          disabled={removeMemberMutation.isPending}
                          onClick={() => setMemberToRemove(member)}
                          size="sm"
                          type="button"
                          variant="secondary"
                        >
                          <Trash2 size={15} />
                          Expulsar
                        </Button>
                      )}
                    </div>
                  )}
                </article>
              );
            })}
          </div>
        )}
      </div>
      <RemoveGroupMemberModal
        member={memberToRemove}
        onClose={() => {
          if (!removeMemberMutation.isPending) {
            setMemberToRemove(null);
          }
        }}
        onConfirm={handleConfirmRemoveMember}
        open={Boolean(memberToRemove)}
        removing={removeMemberMutation.isPending}
      />
    </Modal>
  );
}
