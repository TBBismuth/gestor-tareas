import GroupCard from "./GroupCard.jsx";

export default function GroupGrid({
  currentUserId,
  groupRoles = {},
  groups,
  onDelete,
  onEdit,
  onInvite,
  onLeave,
  onOpenAssignments,
  onToggleActive,
  onViewMembers,
}) {
  return (
    <div className="mt-5 grid items-start gap-3 sm:grid-cols-2 xl:grid-cols-3 2xl:grid-cols-4">
      {groups.map((group, index) => (
        <GroupCard
          animationDelay={`${Math.min(index * 35, 250)}ms`}
          currentUserId={currentUserId}
          group={group}
          knownRole={group.rolUsuarioActual || groupRoles[group.idGrupo]}
          key={group.idGrupo}
          onDelete={onDelete}
          onEdit={onEdit}
          onInvite={onInvite}
          onLeave={onLeave}
          onOpenAssignments={onOpenAssignments}
          onToggleActive={onToggleActive}
          onViewMembers={onViewMembers}
        />
      ))}
    </div>
  );
}
