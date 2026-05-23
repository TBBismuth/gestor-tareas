import TaskCard from "./TaskCard.jsx";

export default function TaskList({
  completingTaskId,
  deletingTaskId,
  isCompleting = false,
  isDeleting = false,
  onCompleteTask,
  onDeleteTask,
  onEditTask,
  tasks,
}) {
  return (
    <div className="mt-5 grid gap-3">
      {tasks.map((task) => (
        <TaskCard
          isCompleting={isCompleting && completingTaskId === task.idTarea}
          isDeleting={isDeleting && deletingTaskId === task.idTarea}
          key={task.idTarea}
          onComplete={onCompleteTask}
          onDelete={onDeleteTask}
          onEdit={onEditTask}
          task={task}
        />
      ))}
    </div>
  );
}
