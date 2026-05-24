import { Copy, RefreshCw } from "lucide-react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { toast } from "sonner";
import Button from "../../../components/ui/Button.jsx";
import Modal from "../../../components/ui/Modal.jsx";
import { getInvitationCode, regenerateInvitationCode } from "../api/groupsApi.js";

function getCodeValue(response) {
  return response?.codigoInvitacion || "";
}

export default function InvitationCodeModal({ canRegenerate = false, group, onClose, open }) {
  const queryClient = useQueryClient();
  const groupId = group?.idGrupo;
  const invitationQueryKey = ["groups", groupId, "invitation-code"];
  const invitationQuery = useQuery({
    queryKey: invitationQueryKey,
    queryFn: () => getInvitationCode(groupId),
    enabled: open && Boolean(groupId),
  });
  const regenerateMutation = useMutation({
    mutationFn: () => regenerateInvitationCode(groupId),
    onSuccess: (data) => {
      queryClient.setQueryData(invitationQueryKey, data);
      toast.success("Código regenerado.");
    },
    onError: (error) => {
      toast.error(error?.response?.data?.error || "No se pudo regenerar el código.");
    },
  });
  const code = getCodeValue(invitationQuery.data);

  async function handleCopyCode() {
    try {
      if (!navigator.clipboard || !code) {
        throw new Error("Clipboard no disponible.");
      }

      await navigator.clipboard.writeText(code);
      toast.success("Código copiado.");
    } catch {
      toast.error("No se pudo copiar el código.");
    }
  }

  return (
    <Modal
      open={open}
      title={group ? `Invitación de ${group.nombre}` : "Código de invitación"}
      onClose={regenerateMutation.isPending ? undefined : onClose}
    >
      <div className="grid gap-4">
        {invitationQuery.isLoading && (
          <p className="rounded-control border border-app bg-[color:var(--color-surface-card-muted)] px-4 py-3 text-sm text-secondary">
            Cargando código de invitación...
          </p>
        )}

        {invitationQuery.isError && (
          <p className="rounded-control border border-app bg-[color:var(--state-danger-bg)] px-4 py-3 text-sm text-[color:var(--state-danger-text)]">
            No se pudo cargar el código de invitación.
          </p>
        )}

        {invitationQuery.isSuccess && (
          <>
            <div className="grid gap-2">
              <p className="text-sm font-semibold text-primary">Código de invitación</p>
              <div className="rounded-control border border-app bg-[color:var(--color-surface-card-muted)] px-3 py-3">
                <code className="block break-all text-sm font-semibold text-primary">{code}</code>
              </div>
            </div>

            <div className="flex flex-col-reverse gap-2 sm:flex-row sm:justify-end">
              <Button onClick={onClose} type="button" variant="secondary">
                Cerrar
              </Button>
              <Button onClick={handleCopyCode} type="button" variant="secondary">
                <Copy size={17} />
                Copiar código
              </Button>
              {canRegenerate && (
                <Button
                  disabled={regenerateMutation.isPending}
                  onClick={() => regenerateMutation.mutate()}
                  type="button"
                  variant="secondary"
                >
                  <RefreshCw size={17} />
                  {regenerateMutation.isPending ? "Regenerando..." : "Regenerar código"}
                </Button>
              )}
            </div>
          </>
        )}
      </div>
    </Modal>
  );
}
