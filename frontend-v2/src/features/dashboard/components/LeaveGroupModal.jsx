import { LogOut } from "lucide-react";
import Button from "../../../components/ui/Button.jsx";
import Modal from "../../../components/ui/Modal.jsx";

export default function LeaveGroupModal({ group, leaving = false, onClose, onConfirm, open }) {
  return (
    <Modal open={open} title="Salir del grupo" onClose={leaving ? undefined : onClose}>
      <div className="grid gap-4">
        <div className="rounded-control border border-app bg-[color:var(--state-danger-bg)] px-4 py-3 text-sm text-[color:var(--state-danger-text)]">
          <p className="font-semibold">¿Seguro que quieres salir de {group?.nombre || "este grupo"}?</p>
          <p className="mt-1 leading-6">Dejarás de ver este grupo y sus futuras asignaciones.</p>
        </div>

        <div className="flex flex-col-reverse gap-2 sm:flex-row sm:justify-end">
          <Button disabled={leaving} onClick={onClose} type="button" variant="secondary">
            Cancelar
          </Button>
          <Button
            className="danger-action-button"
            disabled={leaving}
            onClick={onConfirm}
            type="button"
            variant="secondary"
          >
            <LogOut size={17} />
            {leaving ? "Saliendo..." : "Salir del grupo"}
          </Button>
        </div>
      </div>
    </Modal>
  );
}
