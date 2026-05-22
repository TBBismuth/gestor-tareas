export function isTaskCompleted(task) {
  return (
    task.completada === true ||
    task.estado === "COMPLETADA" ||
    task.estado === "COMPLETADA_CON_RETRASO"
  );
}

function getTime(value) {
  if (!value) return null;

  const time = new Date(value).getTime();
  return Number.isNaN(time) ? null : time;
}

function getSortDate(task, completed) {
  if (completed) {
    return getTime(task.fechaCompletada) ?? getTime(task.fechaAgregado);
  }

  return getTime(task.fechaAgregado);
}

function getFallbackId(task) {
  return Number.isFinite(Number(task.idTarea)) ? Number(task.idTarea) : null;
}

export function sortMyTasks(tasks) {
  return tasks
    .map((task, index) => ({ task, index }))
    .sort((left, right) => {
      const leftCompleted = isTaskCompleted(left.task);
      const rightCompleted = isTaskCompleted(right.task);

      if (leftCompleted !== rightCompleted) {
        return leftCompleted ? 1 : -1;
      }

      const leftDate = getSortDate(left.task, leftCompleted);
      const rightDate = getSortDate(right.task, rightCompleted);

      if (leftDate !== null && rightDate !== null && leftDate !== rightDate) {
        return rightDate - leftDate;
      }

      if (leftDate !== null && rightDate === null) {
        return -1;
      }

      if (leftDate === null && rightDate !== null) {
        return 1;
      }

      const leftId = getFallbackId(left.task);
      const rightId = getFallbackId(right.task);

      if (leftId !== null && rightId !== null && leftId !== rightId) {
        return rightId - leftId;
      }

      return left.index - right.index;
    })
    .map(({ task }) => task);
}
