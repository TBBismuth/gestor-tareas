import TaskCard from "./TaskCard.jsx";

export default function TaskList({
  completingTaskId,
  deletingTaskId,
  isCompleting = false,
  isDeleting = false,
  isUpdatingSmartReminder = false,
  onCompleteTask,
  onDeleteTask,
  onEditTask,
  onToggleSmartReminder,
  showSmartReminderAction = false,
  smartReminderByTaskId = {},
  updatingSmartReminderTaskId,
  tasks,
}) {
  return (
    <div className="mt-5 grid gap-3">
      {tasks.map((task, index) => (
        <TaskCard
          animationDelay={`${Math.min(index * 35, 250)}ms`}
          isCompleting={isCompleting && completingTaskId === task.idTarea}
          isDeleting={isDeleting && deletingTaskId === task.idTarea}
          isUpdatingSmartReminder={
            isUpdatingSmartReminder && updatingSmartReminderTaskId === task.idTarea
          }
          key={task.idTarea}
          onComplete={onCompleteTask}
          onDelete={onDeleteTask}
          onEdit={onEditTask}
          onToggleSmartReminder={onToggleSmartReminder}
          showSmartReminderAction={showSmartReminderAction}
          smartReminderActive={smartReminderByTaskId[task.idTarea]}
          task={task}
        />
      ))}
    </div>
  );
}
