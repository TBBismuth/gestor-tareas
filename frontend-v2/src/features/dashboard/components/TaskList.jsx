import { useLayoutEffect, useMemo, useRef } from "react";
import TaskCard from "./TaskCard.jsx";

const FLIP_MAX_TASKS = 40;
let previousTaskRects = new Map();

function prefersReducedMotion() {
  return (
    typeof window !== "undefined" &&
    window.matchMedia?.("(prefers-reduced-motion: reduce)").matches
  );
}

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
  const itemRefs = useRef(new Map());
  const taskOrderSignature = useMemo(
    () => tasks.map((task) => task.idTarea).join("|"),
    [tasks]
  );

  useLayoutEffect(() => {
    const currentRects = new Map();

    for (const task of tasks) {
      const node = itemRefs.current.get(task.idTarea);
      if (node) {
        currentRects.set(task.idTarea, node.getBoundingClientRect());
      }
    }

    const canAnimate = tasks.length <= FLIP_MAX_TASKS && !prefersReducedMotion();

    if (canAnimate && previousTaskRects.size > 0) {
      for (const task of tasks) {
        const node = itemRefs.current.get(task.idTarea);
        const previousRect = previousTaskRects.get(task.idTarea);
        const currentRect = currentRects.get(task.idTarea);

        if (!node || !previousRect || !currentRect) {
          continue;
        }

        const deltaX = previousRect.left - currentRect.left;
        const deltaY = previousRect.top - currentRect.top;

        if (Math.abs(deltaX) < 1 && Math.abs(deltaY) < 1) {
          continue;
        }

        node.getAnimations().forEach((animation) => animation.cancel());
        node.animate(
          [
            { transform: `translate(${deltaX}px, ${deltaY}px)` },
            { transform: "translate(0, 0)" },
          ],
          {
            duration: 240,
            easing: "cubic-bezier(0.2, 0, 0, 1)",
          }
        );
      }
    }

    previousTaskRects = currentRects;

    return () => {
      const cleanupRects = new Map();

      for (const task of tasks) {
        const node = itemRefs.current.get(task.idTarea);
        if (node) {
          cleanupRects.set(task.idTarea, node.getBoundingClientRect());
        }
      }

      if (cleanupRects.size > 0) {
        previousTaskRects = cleanupRects;
      }
    };
  }, [taskOrderSignature]);

  return (
    <div className="mt-5 grid gap-3">
      {tasks.map((task, index) => (
        <div
          className="task-flip-item"
          key={task.idTarea}
          ref={(node) => {
            if (node) {
              itemRefs.current.set(task.idTarea, node);
            } else {
              itemRefs.current.delete(task.idTarea);
            }
          }}
        >
          <TaskCard
            animationDelay={`${Math.min(index * 35, 250)}ms`}
            collapseSignal={taskOrderSignature}
            isCompleting={isCompleting && completingTaskId === task.idTarea}
            isDeleting={isDeleting && deletingTaskId === task.idTarea}
            isUpdatingSmartReminder={
              isUpdatingSmartReminder && updatingSmartReminderTaskId === task.idTarea
            }
            onComplete={onCompleteTask}
            onDelete={onDeleteTask}
            onEdit={onEditTask}
            onToggleSmartReminder={onToggleSmartReminder}
            showSmartReminderAction={showSmartReminderAction}
            smartReminderActive={smartReminderByTaskId[task.idTarea]}
            task={task}
          />
        </div>
      ))}
    </div>
  );
}
