import Modal from "../../../components/ui/Modal.jsx";
import Button from "../../../components/ui/Button.jsx";

export default function DescriptionModal({ description, onClose, open, title }) {
  return (
    <Modal
      closeOnOverlayClick
      open={open}
      onClose={onClose}
      size="lg"
      title={title}
      zIndexClass="z-[120]"
    >
      <div className="max-h-[60vh] overflow-y-auto whitespace-pre-wrap break-words rounded-control border border-app bg-[color:var(--color-surface-card-muted)] px-4 py-3 text-sm leading-6 text-secondary">
        {description}
      </div>
      <div className="mt-4 flex justify-end">
        <Button type="button" variant="secondary" onClick={onClose}>
          Cerrar
        </Button>
      </div>
    </Modal>
  );
}
