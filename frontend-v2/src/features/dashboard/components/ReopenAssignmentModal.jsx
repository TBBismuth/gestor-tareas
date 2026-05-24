import { useEffect } from "react";
import { useForm } from "react-hook-form";
import Button from "../../../components/ui/Button.jsx";
import Modal from "../../../components/ui/Modal.jsx";

const defaultValues = {
  comentarioRevision: "",
};

function FieldError({ error }) {
  if (!error) return null;

  return <p className="auth-error">{error.message}</p>;
}

function getRecipientName(recipient) {
  return recipient?.nombreUsuarioMiembro || recipient?.emailUsuarioMiembro || "este destinatario";
}

export default function ReopenAssignmentModal({
  recipient,
  onClose,
  onConfirm,
  open,
  reopening = false,
}) {
  const {
    formState: { errors },
    handleSubmit,
    register,
    reset,
  } = useForm({ defaultValues });

  useEffect(() => {
    if (!open) {
      reset(defaultValues);
    }
  }, [open, reset]);

  async function submit(values) {
    await onConfirm?.({
      comentarioRevision: values.comentarioRevision.trim(),
    });
  }

  return (
    <Modal open={open} title="Reabrir entrega" onClose={reopening ? undefined : onClose} zIndexClass="z-[70]">
      <form className="grid gap-4" onSubmit={handleSubmit(submit)} noValidate>
        <div className="grid gap-2 text-sm leading-6 text-secondary">
          <p>¿Seguro que quieres reabrir la entrega de {getRecipientName(recipient)}?</p>
          <p>La tarea volvera a quedar pendiente para esa persona.</p>
        </div>

        <div className="grid gap-2">
          <label className="text-sm font-semibold text-primary" htmlFor="reopen-comment">
            Motivo de reapertura *
          </label>
          <textarea
            id="reopen-comment"
            className="auth-input min-h-24 resize-y"
            placeholder="Explica el motivo"
            {...register("comentarioRevision", {
              required: "El motivo de reapertura es obligatorio.",
              validate: (value) =>
                value.trim().length > 0 || "El motivo de reapertura es obligatorio.",
            })}
          />
          <FieldError error={errors.comentarioRevision} />
        </div>

        <div className="mt-2 flex flex-col-reverse gap-2 sm:flex-row sm:justify-end">
          <Button disabled={reopening} onClick={onClose} type="button" variant="secondary">
            Cancelar
          </Button>
          <Button className="danger-action-button" disabled={reopening} type="submit" variant="secondary">
            {reopening ? "Reabriendo..." : "Reabrir entrega"}
          </Button>
        </div>
      </form>
    </Modal>
  );
}
