import { useEffect } from "react";
import { useForm } from "react-hook-form";
import { Clipboard } from "lucide-react";
import { toast } from "sonner";
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
    setValue,
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

  async function handlePasteCode() {
    if (!navigator.clipboard?.readText) {
      toast.error("No se pudo pegar el código.");
      return;
    }

    try {
      const clipboardText = await navigator.clipboard.readText();
      setValue("codigoInvitacion", clipboardText.trim(), {
        shouldDirty: true,
        shouldValidate: true,
      });
      toast.success("Código pegado.");
    } catch {
      toast.error("No se pudo pegar el código.");
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

        <div className="mt-2 flex flex-wrap justify-end gap-2">
          <Button disabled={joining} onClick={onClose} type="button" variant="secondary">
            Cancelar
          </Button>
          <Button disabled={joining} onClick={handlePasteCode} type="button" variant="secondary">
            <Clipboard size={15} />
            Pegar código
          </Button>
          <Button disabled={joining} type="submit">
            {joining ? "Uniendo..." : "Unirse"}
          </Button>
        </div>
      </form>
    </Modal>
  );
}
