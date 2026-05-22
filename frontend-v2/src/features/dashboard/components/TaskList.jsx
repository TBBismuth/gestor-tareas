import TaskCard from "./TaskCard.jsx";

export default function TaskList({ tasks }) {
  return (
    <div className="mt-5 grid gap-3">
      {tasks.map((task) => (
        <TaskCard key={task.idTarea} task={task} />
      ))}
    </div>
  );
}
