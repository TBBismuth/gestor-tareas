import { Crown } from "lucide-react";
import Button from "../../../components/ui/Button.jsx";
import Modal from "../../../components/ui/Modal.jsx";

function getMemberDisplayName(member) {
  return member?.nombre || member?.email || "este miembro";
}

export default function TransferGroupOwnershipModal({
  member,
  onClose,
  onConfirm,
  open,
  transferring = false,
}) {
  const memberName = getMemberDisplayName(member);

  return (
    <Modal open={open} title="Transferir propiedad" onClose={transferring ? undefined : onClose}>
      <div className="grid gap-4">
        <div className="rounded-control border border-app bg-[color:var(--color-surface-card-muted)] px-4 py-3 text-sm text-secondary">
          <p className="font-semibold text-primary">
            ¿Seguro que quieres transferir la propiedad del grupo a {memberName}?
          </p>
          <p className="mt-1 leading-6">
            La otra persona pasará a ser la creadora del grupo. Tú conservarás tu pertenencia al
            grupo, pero perderás las acciones exclusivas del creador.
          </p>
        </div>

        <div className="flex flex-col-reverse gap-2 sm:flex-row sm:justify-end">
          <Button disabled={transferring} onClick={onClose} type="button" variant="secondary">
            Cancelar
          </Button>
          <Button disabled={transferring} onClick={onConfirm} type="button">
            <Crown size={17} />
            {transferring ? "Transfiriendo..." : "Transferir propiedad"}
          </Button>
        </div>
      </div>
    </Modal>
  );
}
