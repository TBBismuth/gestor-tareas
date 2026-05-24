import GroupCard from "./GroupCard.jsx";

export default function GroupGrid({
  currentUserId,
  groupRoles = {},
  groups,
  onDelete,
  onEdit,
  onInvite,
  onLeave,
  onToggleActive,
  onViewMembers,
}) {
  return (
    <div className="mt-5 grid gap-3 sm:grid-cols-2 xl:grid-cols-3 2xl:grid-cols-4">
      {groups.map((group) => (
        <GroupCard
          currentUserId={currentUserId}
          group={group}
          knownRole={groupRoles[group.idGrupo]}
          key={group.idGrupo}
          onDelete={onDelete}
          onEdit={onEdit}
          onInvite={onInvite}
          onLeave={onLeave}
          onToggleActive={onToggleActive}
          onViewMembers={onViewMembers}
        />
      ))}
    </div>
  );
}
