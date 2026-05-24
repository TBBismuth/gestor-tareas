import { useEffect } from "react";
import { useQuery } from "@tanstack/react-query";
import { UserCheck, Users } from "lucide-react";
import Badge from "../../../components/ui/Badge.jsx";
import Modal from "../../../components/ui/Modal.jsx";
import { getGroupMembers } from "../api/groupsApi.js";

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

export default function GroupMembersModal({
  currentUserId,
  group,
  onClose,
  onMembersLoaded,
  open,
}) {
  const groupId = group?.idGrupo;
  const membersQuery = useQuery({
    queryKey: ["groups", groupId, "members"],
    queryFn: () => getGroupMembers(groupId),
    enabled: open && Boolean(groupId),
  });
  const members = membersQuery.data ?? [];

  useEffect(() => {
    if (membersQuery.isSuccess && groupId) {
      onMembersLoaded?.(groupId, members);
    }
  }, [groupId, members, membersQuery.isSuccess, onMembersLoaded]);

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
                          {member.nombre || member.email || "Miembro"}
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
                </article>
              );
            })}
          </div>
        )}
      </div>
    </Modal>
  );
}
