import { UserMinus } from "lucide-react";
import Button from "../../../components/ui/Button.jsx";
import Modal from "../../../components/ui/Modal.jsx";

export default function RemoveGroupMemberModal({
  member,
  onClose,
  onConfirm,
  open,
  removing = false,
}) {
  const memberName = member?.nombre || member?.email || "este miembro";

  return (
    <Modal open={open} title="Expulsar miembro" onClose={removing ? undefined : onClose}>
      <div className="grid gap-4">
        <div className="rounded-control border border-app bg-[color:var(--state-danger-bg)] px-4 py-3 text-sm text-[color:var(--state-danger-text)]">
          <p className="font-semibold">¿Seguro que quieres expulsar a {memberName}?</p>
          <p className="mt-1 leading-6">
            Esta persona dejará de tener acceso al grupo y a sus futuras asignaciones.
          </p>
        </div>

        <div className="flex flex-col-reverse gap-2 sm:flex-row sm:justify-end">
          <Button disabled={removing} onClick={onClose} type="button" variant="secondary">
            Cancelar
          </Button>
          <Button
            className="danger-action-button"
            disabled={removing}
            onClick={onConfirm}
            type="button"
            variant="secondary"
          >
            <UserMinus size={17} />
            {removing ? "Expulsando..." : "Expulsar miembro"}
          </Button>
        </div>
      </div>
    </Modal>
  );
}
