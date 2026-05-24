import { useEffect } from "react";
import { useForm } from "react-hook-form";
import Button from "../../../components/ui/Button.jsx";
import Modal from "../../../components/ui/Modal.jsx";

const defaultValues = {
  codigoInvitacion: "",
};

function FieldError({ error }) {
  if (!error) return null;

  return <p className="auth-error">{error.message}</p>;
}

export default function JoinGroupModal({ joining = false, onClose, onSubmit, open }) {
  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm({ defaultValues });

  useEffect(() => {
    reset(defaultValues);
  }, [open, reset]);

  async function submit(values) {
    try {
      await onSubmit({ codigoInvitacion: values.codigoInvitacion.trim() });
      reset(defaultValues);
    } catch {
      // El contenedor muestra el mensaje de error de la mutacion.
    }
  }

  return (
    <Modal open={open} title="Unirse por código" onClose={joining ? undefined : onClose}>
      <form className="grid gap-4" onSubmit={handleSubmit(submit)} noValidate>
        <div className="grid gap-2">
          <label className="text-sm font-semibold text-primary" htmlFor="group-invitation-code">
            Código de invitación *
          </label>
          <input
            id="group-invitation-code"
            className="auth-input"
            placeholder="Introduce el código de invitación"
            {...register("codigoInvitacion", {
              required: "El código de invitación es obligatorio.",
            })}
          />
          <FieldError error={errors.codigoInvitacion} />
        </div>

        <div className="mt-2 flex flex-col-reverse gap-2 sm:flex-row sm:justify-end">
          <Button disabled={joining} onClick={onClose} type="button" variant="secondary">
            Cancelar
          </Button>
          <Button disabled={joining} type="submit">
            {joining ? "Uniendo..." : "Unirse"}
          </Button>
        </div>
      </form>
    </Modal>
  );
}
