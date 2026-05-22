import TaskCard from "./TaskCard.jsx";

export default function TaskList({
  completingTaskId,
  isCompleting = false,
  onCompleteTask,
  tasks,
}) {
  return (
    <div className="mt-5 grid gap-3">
      {tasks.map((task) => (
        <TaskCard
          isCompleting={isCompleting && completingTaskId === task.idTarea}
          key={task.idTarea}
          onComplete={onCompleteTask}
          task={task}
        />
      ))}
    </div>
  );
}
