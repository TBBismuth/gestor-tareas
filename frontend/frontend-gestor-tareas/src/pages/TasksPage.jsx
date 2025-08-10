import { useEffect, useState } from "react";
import AppHeader from "../components/AppHeader";
import { getTareas } from "../services/taskService";
import { addTarea } from "../services/taskService";
import BaseButton from "../components/base/BaseButton";
import { PRIORIDAD_BG, ESTADO_BG, pick } from "../styles/themeColors";
import BaseModal from "../components/base/BaseModal";

// NUEVOS IMPORTS (componentes base para el formulario)
import BaseField from "../components/base/BaseField";
import BaseInput from "../components/base/BaseInput";
import BaseTextarea from "../components/base/BaseTextarea";
import SelectPrioridad from "../components/base/SelectPrioridad";
import SelectCategoria from "../components/base/SelectCategoria";

export default function Home() {
    const [tareas, setTareas] = useState([]);
    const [cargando, setCargando] = useState(true);
    const [error, setError] = useState("");
    const [tareasExpandidas, setTareasExpandidas] = useState([]);
    const [BaseModalNuevaTarea, setBaseModalNuevaTarea] = useState(false);

    // Estado del formulario "Nueva tarea"
    const [nuevaTarea, setNuevaTarea] = useState({
        titulo: "",
        descripcion: "",
        tiempo: "",
        prioridad: "",
        fechaEntrega: "",
        idCategoria: "",
    });
    const [errorNuevaTarea, setErrorNuevaTarea] = useState("");

    useEffect(() => {
        let cancelado = false;
        (async () => {
            try {
                setCargando(true);
                const { data } = await getTareas();
                if (!cancelado) setTareas(Array.isArray(data) ? data : []);
            } catch (e) {
                console.error(e);
                if (!cancelado) setError("No se pudieron cargar las tareas.");
            } finally {
                if (!cancelado) setCargando(false);
            }
        })();
        return () => {
            cancelado = true;
        };
    }, []);

    const toggleExpandir = (id) =>
        setTareasExpandidas((prev) =>
            prev.includes(id) ? prev.filter((x) => x !== id) : [...prev, id]
        );

    const expandirTodas = () => setTareasExpandidas(tareas.map((t) => t.idTarea));
    const colapsarTodas = () => setTareasExpandidas([]);

    return (
        <div className="min-h-screen bg-green-50">
            <AppHeader />
            <main className="max-w-5xl mx-auto px-4 py-6">
                <div className="flex justify-between mb-4">
                    <h2 className="text-xl font-bold text-green-700">Tus tareas</h2>
                    <div className="flex gap-2 items-start">
                        <BaseButton
                            onClick={() => setBaseModalNuevaTarea(true)}
                            variant="success"
                            size="sm"
                        >
                            Nueva tarea
                        </BaseButton>
                        <BaseButton onClick={expandirTodas} variant="primary" size="sm">
                            Expandir todas
                        </BaseButton>
                        <BaseButton onClick={colapsarTodas} variant="secondary" size="sm">
                            Colapsar todas
                        </BaseButton>
                    </div>
                </div>

                {cargando && <p className="mt-4">Cargando…</p>}
                {error && <p className="mt-4 text-red-600">{error}</p>}
                {!cargando && !error && tareas.length === 0 && (
                    <p className="mt-4 text-gray-700">No hay tareas.</p>
                )}

                <div className="mt-4 grid gap-4">
                    {tareas.map((t) => {
                        const expandida = tareasExpandidas.includes(t.idTarea);
                        const cPrioridad = pick(PRIORIDAD_BG, t.prioridad);
                        const cEstado = pick(ESTADO_BG, t.estado);

                        return (
                            <div
                                key={t.idTarea}
                                className="rounded-lg border overflow-hidden shadow-sm cursor-pointer"
                                onClick={() => toggleExpandir(t.idTarea)}
                            >
                                {/* CABECERA: diagonal ↗ (135deg) */}
                                <div
                                    className="relative"
                                    style={{
                                        background: `linear-gradient(135deg, ${cPrioridad} 50%, ${cEstado} 50%)`,
                                    }}
                                >
                                    <div className="relative p-4 bg-white/80">
                                        <div className="flex justify-between items-start">
                                            <div>
                                                <h3 className="text-lg font-semibold text-gray-800">
                                                    {t.titulo}
                                                </h3>
                                                {expandida && t.descripcion && (
                                                    <p className="text-sm text-gray-600 mt-1">
                                                        {t.descripcion}
                                                    </p>
                                                )}
                                            </div>
                                            {t.fechaEntrega && (
                                                <span className="text-xs text-gray-500">
                                                    {new Date(t.fechaEntrega).toLocaleDateString()}
                                                </span>
                                            )}
                                        </div>
                                    </div>
                                </div>

                                {/* CONTENIDO EXPANDIDO: diagonal invertida ↙ (225deg) y SIN bordes internos */}
                                {expandida && (
                                    <div
                                        className="relative text-sm text-gray-800"
                                        style={{
                                            background: `linear-gradient(225deg, ${cEstado} 50%, ${cPrioridad} 50%)`,
                                        }}
                                    >
                                        <div className="relative z-10 p-3 bg-white/80">
                                            <p>
                                                <strong>Prioridad:</strong> {t.prioridad}
                                            </p>
                                            <p>
                                                <strong>Estado:</strong> {t.estado}
                                            </p>
                                            <p>
                                                <strong>Tiempo estimado:</strong> {t.tiempo}h
                                            </p>
                                        </div>
                                    </div>
                                )}
                            </div>
                        );
                    })}
                </div>

                {/* BaseModal para nueva tarea */}
                <BaseModal
                    open={BaseModalNuevaTarea}
                    onClose={() => {
                        setBaseModalNuevaTarea(false);
                        setNuevaTarea({
                            titulo: "",
                            descripcion: "",
                            tiempo: "",
                            prioridad: "",
                            fechaEntrega: "",
                            idCategoria: "",
                        });
                        setErrorNuevaTarea("");
                    }}
                    title="Nueva tarea"
                >
                    <form
                        onSubmit={async (e) => {
                            e.preventDefault();
                            setErrorNuevaTarea("");

                            // 🔎 TRACE 1: el submit salta
                            console.log("[NuevaTarea] SUBMIT disparado. Estado:", nuevaTarea);

                            // Validación mínima
                            if (!nuevaTarea.titulo?.trim()) return setErrorNuevaTarea("El título es obligatorio.");
                            if (!nuevaTarea.prioridad) return setErrorNuevaTarea("La prioridad es obligatoria.");
                            if (!nuevaTarea.tiempo || Number.isNaN(Number(nuevaTarea.tiempo)))
                                return setErrorNuevaTarea("El tiempo (min) es obligatorio y numérico.");
                            if (!nuevaTarea.idCategoria) return setErrorNuevaTarea("La categoría es obligatoria.");

                            const payload = {
                                titulo: nuevaTarea.titulo.trim(),
                                descripcion: (nuevaTarea.descripcion || "").trim(),
                                tiempo: Number(nuevaTarea.tiempo),
                                prioridad: nuevaTarea.prioridad,
                                fechaEntrega:
                                    nuevaTarea.fechaEntrega.length === 16
                                        ? `${nuevaTarea.fechaEntrega}:00`
                                        : nuevaTarea.fechaEntrega,
                            };
                            if (nuevaTarea.idCategoria) payload.idCategoria = Number(nuevaTarea.idCategoria);

                            // 🔎 TRACE 2: payload que vamos a enviar
                            console.log("[NuevaTarea] Payload POST /api/tarea/add:", payload);

                            try {
                                const resp = await addTarea(payload); // debe apuntar a "/tarea/add" porque apiClient.baseURL === "/api"
                                // 🔎 TRACE 3: respuesta OK
                                console.log("[NuevaTarea] OK:", resp?.status, resp?.data);

                                const { data } = await getTareas();
                                setTareas(Array.isArray(data) ? data : []);

                                setNuevaTarea({
                                    titulo: "",
                                    descripcion: "",
                                    tiempo: "",
                                    prioridad: "",
                                    fechaEntrega: "",
                                    idCategoria: "",
                                });
                                setBaseModalNuevaTarea(false);
                            } catch (err) {
                                // 🔎 TRACE 4: error detallado
                                console.error("[NuevaTarea] ERROR POST:", {
                                    message: err?.message,
                                    status: err?.response?.status,
                                    data: err?.response?.data,
                                    url: err?.config?.baseURL + err?.config?.url, // debe verse "/api/tarea/add"
                                    method: err?.config?.method,
                                });
                                setErrorNuevaTarea(err?.response?.data?.message || "No se pudo crear la tarea.");
                            }
                        }}

                        className="space-y-4"
                    >
                        {/* Título -> usa BaseField sin children (inyecta BaseInput) */}
                        <BaseField
                            id="titulo"
                            label="Título"
                            requerido
                            placeholder="Ej.: Comprar pan"
                            value={nuevaTarea.titulo}
                            onChange={(e) => setNuevaTarea((s) => ({ ...s, titulo: e.target.value }))}
                            required
                        />

                        {/* Descripción -> BaseField con BaseTextarea como children */}
                        <BaseField id="descripcion" label="Descripción">
                            <BaseTextarea
                                id="descripcion"
                                rows={4}
                                placeholder="Detalles opcionales…"
                                value={nuevaTarea.descripcion}
                                onChange={(e) => setNuevaTarea((s) => ({ ...s, descripcion: e.target.value }))}
                            />
                        </BaseField>

                        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                            {/* Prioridad -> BaseField con SelectPrioridad como children */}
                            <BaseField id="prioridad" label="Prioridad" requerido>
                                <SelectPrioridad
                                    id="prioridad"
                                    value={nuevaTarea.prioridad}
                                    onChange={(e) => setNuevaTarea((s) => ({ ...s, prioridad: e.target.value }))}
                                    required
                                />
                            </BaseField>

                            {/* Categoría -> BaseField con SelectCategoria + botón */}
                            <BaseField id="categoria" label="Categoría" requerido>
                                <div className="flex gap-2 items-end">
                                    <div className="flex-1">
                                        <SelectCategoria
                                            id="categoria"
                                            value={nuevaTarea.idCategoria}
                                            onChange={(e) => setNuevaTarea((s) => ({ ...s, idCategoria: e.target.value }))}
                                            required
                                        />
                                    </div>
                                    <BaseButton
                                        type="button"
                                        variant="secondary"
                                        size="sm"
                                        onClick={() => console.log("TODO: abrir modal Añadir categoría")}
                                        title="Añadir nueva categoría"
                                    >
                                        + Añadir
                                    </BaseButton>
                                </div>
                            </BaseField>
                        </div>

                        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                            {/* Fecha de entrega -> BaseField con input datetime-local como children */}
                            <BaseField id="fechaEntrega" label="Fecha de entrega">
                                <input
                                    id="fechaEntrega"
                                    type="datetime-local"
                                    className="px-3 py-2 border rounded w-full focus:outline-none focus:ring-2 focus:ring-blue-400"
                                    value={nuevaTarea.fechaEntrega}
                                    onChange={(e) =>
                                        setNuevaTarea((s) => ({ ...s, fechaEntrega: e.target.value }))
                                    }
                                />
                            </BaseField>

                            {/* Tiempo (min) -> BaseField sin children (usa BaseInput) */}
                            <BaseField
                                id="tiempo"
                                label="Tiempo (min)"
                                requerido
                                type="number"
                                min="1"
                                placeholder="ej.: 30"
                                value={nuevaTarea.tiempo}
                                onChange={(e) => setNuevaTarea((s) => ({ ...s, tiempo: e.target.value }))}
                                required
                            />
                        </div>

                        {errorNuevaTarea && <p className="text-sm text-red-600">{errorNuevaTarea}</p>}

                        <div className="flex justify-end gap-3 pt-2">
                            <BaseButton
                                variant="secondary"
                                type="button"
                                onClick={() => {
                                    setBaseModalNuevaTarea(false);
                                    setNuevaTarea({
                                        titulo: "",
                                        descripcion: "",
                                        tiempo: "",
                                        prioridad: "",
                                        fechaEntrega: "",
                                        idCategoria: "",
                                    });
                                    setErrorNuevaTarea("");
                                }}
                            >
                                Cancelar
                            </BaseButton>

                            <BaseButton type="submit">Guardar</BaseButton>
                        </div>
                    </form>
                </BaseModal>

            </main>
        </div>
    );
}
