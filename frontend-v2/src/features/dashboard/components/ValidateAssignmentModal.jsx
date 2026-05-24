import { useEffect } from "react";
import { useForm } from "react-hook-form";
import Button from "../../../components/ui/Button.jsx";
import Modal from "../../../components/ui/Modal.jsx";

const defaultValues = {
  comentarioRevision: "",
};

function getRecipientName(recipient) {
  return recipient?.nombreUsuarioMiembro || recipient?.emailUsuarioMiembro || "este destinatario";
}

export default function ValidateAssignmentModal({
  recipient,
  onClose,
  onConfirm,
  open,
  validating = false,
}) {
  const { handleSubmit, register, reset } = useForm({ defaultValues });

  useEffect(() => {
    if (!open) {
      reset(defaultValues);
    }
  }, [open, reset]);

  async function submit(values) {
    const comentarioRevision = values.comentarioRevision?.trim();
    await onConfirm?.({
      comentarioRevision: comentarioRevision || null,
    });
  }

  return (
    <Modal open={open} title="Validar entrega" onClose={validating ? undefined : onClose} zIndexClass="z-[70]">
      <form className="grid gap-4" onSubmit={handleSubmit(submit)} noValidate>
        <p className="text-sm leading-6 text-secondary">
          ¿Quieres validar la entrega de {getRecipientName(recipient)}?
        </p>

        <div className="grid gap-2">
          <label className="text-sm font-semibold text-primary" htmlFor="validate-comment">
            Comentario de revision
          </label>
          <textarea
            id="validate-comment"
            className="auth-input min-h-24 resize-y"
            placeholder="Comentario opcional"
            {...register("comentarioRevision")}
          />
        </div>

        <div className="mt-2 flex flex-col-reverse gap-2 sm:flex-row sm:justify-end">
          <Button disabled={validating} onClick={onClose} type="button" variant="secondary">
            Cancelar
          </Button>
          <Button disabled={validating} type="submit">
            {validating ? "Validando..." : "Validar entrega"}
          </Button>
        </div>
      </form>
    </Modal>
  );
}
