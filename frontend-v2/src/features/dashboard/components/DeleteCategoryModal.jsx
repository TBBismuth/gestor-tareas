import Button from "../../../components/ui/Button.jsx";
import Modal from "../../../components/ui/Modal.jsx";

export default function DeleteCategoryModal({
  category,
  deleting = false,
  onClose,
  onConfirm,
  onConfirmAndCreate,
  open,
}) {
  return (
    <Modal open={open} title="Eliminar categoría" onClose={onClose}>
      <div className="grid gap-4">
        <div className="grid gap-2 text-sm leading-6 text-secondary">
          <p>
            Las tareas asociadas a esta categoría quedarán sin categoría, pero podrás asignarles otra a continuacion.
          </p>
          <p className="font-semibold text-primary">
            ¿Seguro que quieres eliminar {category?.nombre || "esta categoría"}?
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
            {deleting ? "Borrando..." : "Borrar"}
          </Button>
          <Button disabled={deleting} type="button" onClick={onConfirmAndCreate}>
            Borrar y crear otra
          </Button>
        </div>
      </div>
    </Modal>
  );
}
