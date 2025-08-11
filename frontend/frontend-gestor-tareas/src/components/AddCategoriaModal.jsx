import { useState } from "react";
import BaseModal from "./base/BaseModal";
import BaseField from "./base/BaseField";
import BaseInput from "./base/BaseInput";
import BaseButton from "./base/BaseButton";
import { addCategoria } from "../services/categoriaService";
import BaseIconSelect from "./base/BaseIconSelect";

export default function AddCategoriaModal({ open, onClose, onCreated }) {
    const [form, setForm] = useState({ nombre: "", color: "", icono: "" });
    const [error, setError] = useState("");

    const reset = () => {
        setForm({ nombre: "", color: "", icono: "" });
        setError("");
    };

    const handleClose = () => {
        reset();
        onClose?.();
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError("");

        if (!form.nombre.trim()) {
            setError("El nombre es obligatorio.");
            return;
        }

        // Construir payload: mínimo 'nombre'; 'color' e 'icono' son opcionales
        const payload = { nombre: form.nombre.trim() };
        if (form.color) payload.color = form.color;     // ej.: #ff0000
        if (form.icono?.trim()) payload.icono = form.icono.trim();

        try {
            const { data } = await addCategoria(payload); // POST /api/categoria/add
            // Avisar arriba para refrescar el selector y seleccionar la nueva
            onCreated?.(data);
            handleClose();
        } catch (err) {
            console.error(err);
            setError(err?.response?.data?.message || "No se pudo crear la categoría.");
        }
    };

    return (
        <BaseModal open={open} onClose={handleClose} title="Añadir categoría">
            <form onSubmit={handleSubmit} className="space-y-4">
                <BaseField
                    id="nombre"
                    label="Nombre"
                    requerido
                    placeholder="Ej.: Trabajo"
                    value={form.nombre}
                    onChange={(e) => setForm((s) => ({ ...s, nombre: e.target.value }))}
                    required
                />

                {/* Opcionales: dejamos listos para el futuro */}
                <BaseIconSelect
                    id="icono"
                    label="Icono (opcional)"
                    value={form.icono}
                    onChange={(e) => setForm((s) => ({ ...s, icono: e.target.value }))}
                    required={false}
                />

                <BaseField id="color" label="Color (opcional)">
                    <input
                        id="color"
                        type="color"
                        className="w-12 h-9 p-0 border rounded cursor-pointer"
                        value={form.color || "#ffffff"}
                        onChange={(e) => setForm((s) => ({ ...s, color: e.target.value }))}
                    />
                </BaseField>

                {error && <p className="text-sm text-red-600">{error}</p>}

                <div className="flex justify-end gap-3 pt-2">
                    <BaseButton type="button" variant="secondary" onClick={handleClose}>
                        Cancelar
                    </BaseButton>
                    <BaseButton type="submit">Crear</BaseButton>
                </div>
            </form>
        </BaseModal>
    );
}
