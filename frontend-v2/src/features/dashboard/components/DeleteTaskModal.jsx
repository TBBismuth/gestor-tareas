import Button from "../../../components/ui/Button.jsx";
import Modal from "../../../components/ui/Modal.jsx";

export default function DeleteTaskModal({ deleting = false, onClose, onConfirm, open, task }) {
  return (
    <Modal open={open} title="Eliminar tarea" onClose={deleting ? undefined : onClose}>
      <div className="grid gap-4">
        <div className="grid gap-2 text-sm leading-6 text-secondary">
          <p className="font-semibold text-primary">
            ¿Seguro que quieres eliminar {task?.titulo || "esta tarea"}?
          </p>
          <p>
            La tarea aún no está completada. Esta acción eliminará la tarea de forma permanente.
          </p>
        </div>
        <div className="flex flex-col-reverse gap-2 sm:flex-row sm:justify-end">
          <Button disabled={deleting} type="button" variant="secondary" onClick={onClose}>
            Cancelar
          </Button>
          <Button
            className="danger-action-button"
            disabled={deleting}
            type="button"
            variant="secondary"
            onClick={onConfirm}
          >
            {deleting ? "Eliminando..." : "Eliminar tarea"}
          </Button>
        </div>
      </div>
    </Modal>
  );
}
