import { Trash2 } from "lucide-react";
import Button from "../../../components/ui/Button.jsx";
import Modal from "../../../components/ui/Modal.jsx";

export default function DeleteGroupModal({ deleting = false, group, onClose, onConfirm, open }) {
  return (
    <Modal open={open} title="Eliminar grupo" onClose={deleting ? undefined : onClose}>
      <div className="grid gap-4">
        <div className="rounded-control border border-app bg-[color:var(--state-danger-bg)] px-4 py-3 text-sm text-[color:var(--state-danger-text)]">
          <p className="font-semibold">¿Seguro que quieres eliminar {group?.nombre || "este grupo"}?</p>
          <p className="mt-1 leading-6">
            Esta acción eliminará el grupo y sus datos asociados. No podrás deshacerla.
          </p>
        </div>

        <div className="flex flex-col-reverse gap-2 sm:flex-row sm:justify-end">
          <Button disabled={deleting} onClick={onClose} type="button" variant="secondary">
            Cancelar
          </Button>
          <Button
            className="danger-action-button"
            disabled={deleting}
            onClick={onConfirm}
            type="button"
            variant="secondary"
          >
            <Trash2 size={17} />
            {deleting ? "Eliminando..." : "Eliminar grupo"}
          </Button>
        </div>
      </div>
    </Modal>
  );
}
