import { useEffect, useState } from "react";
import AppHeader from "../components/AppHeader";
import { getTareas, addTarea, updateTarea, getTareasOrdenadas } from "../services/taskService";
import BaseButton from "../components/base/BaseButton";
import { PRIORIDAD_BG, ESTADO_BG, pick } from "../styles/themeColors";
import BaseModal from "../components/base/BaseModal";
import Sidebar from "../components/Sidebar";
import { useNavigate } from "react-router-dom";

// NUEVOS IMPORTS (componentes base para el formulario)
import BaseField from "../components/base/BaseField";
import BaseInput from "../components/base/BaseInput";
import BaseSelect from "../components/base/BaseSelect";
import BaseTextarea from "../components/base/BaseTextarea";
import SelectPrioridad from "../components/base/SelectPrioridad";
import SelectCategoria from "../components/base/SelectCategoria";
import AddCategoriaModal from "../components/AddCategoriaModal";
import { filtrarTareas } from "../services/taskService";

// NUEVO: categorías para pintar icono/nombre
import { getCategorias } from "../services/categoriaService";

export default function Home() {
    const [tareas, setTareas] = useState([]);
    const [cargando, setCargando] = useState(true);
    const [error, setError] = useState("");
    const [tareasExpandidas, setTareasExpandidas] = useState([]);
    const [BaseModalNuevaTarea, setBaseModalNuevaTarea] = useState(false);
    const [openAddCat, setOpenAddCat] = useState(false);
    const [catReloadKey, setCatReloadKey] = useState(0);

    // NUEVO: mapa id->categoria para icono/nombre
    const [categoriasMap, setCategoriasMap] = useState({});

    const [ordenActual, setOrdenActual] = useState("");
    const [tipoFiltro, setTipoFiltro] = useState("");
    const [valorFiltro, setValorFiltro] = useState("");


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

    // Estado del modal de edición
    const [editOpen, setEditOpen] = useState(false);
    const [tareaEdit, setTareaEdit] = useState({
        idTarea: null,
        titulo: "",
        descripcion: "",
        tiempo: "",
        prioridad: "",
        fechaEntrega: "",
        idCategoria: "",
    });
    const [errorEditar, setErrorEditar] = useState("");


    // 👇 Util para mostrar enums de forma friendly (e.g. EN_CURSO -> En curso)
    const pretty = (txt) =>
        typeof txt === "string"
            ? txt.toLowerCase().replace(/_/g, " ").replace(/^\w/, (c) => c.toUpperCase())
            : txt;

    // Cargar tareas
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

    // Cargar categorías una vez y crear mapa id -> {nombre, icono, color}
    useEffect(() => {
        let cancel = false;
        (async () => {
            try {
                const data = await getCategorias(); // esta función devuelve el array directamente
                if (cancel) return;
                const map = {};
                (Array.isArray(data) ? data : []).forEach((c) => {
                    map[c.idCategoria] = { nombre: c.nombre, icono: c.icono, color: c.color };
                });
                setCategoriasMap(map);
            } catch (e) {
                console.error("No se pudieron cargar categorías", e);
            }
        })();
        return () => {
            cancel = true;
        };
    }, []);

    // Cargar tareas ordenadas según el orden seleccionado
    useEffect(() => {
        async function cargarTareasOrdenadas() {
            if (!ordenActual) return;

            try {
                const response = await getTareasOrdenadas(ordenActual);
                setTareas(response);
            } catch (err) {
                console.error("Error al obtener tareas ordenadas:", err);
            }
        }

        cargarTareasOrdenadas();
    }, [ordenActual]);

    // Aplicar filtro de tareas según tipo y valor
    useEffect(() => {
        async function aplicarFiltro() {
            if (!tipoFiltro || !valorFiltro) return;

            try {
                console.log("Aplicando filtro:", tipoFiltro, valorFiltro);

                const resultado = await filtrarTareas(tipoFiltro, valorFiltro);
                setTareas(resultado);
            } catch (err) {
                console.error("Error al aplicar filtro:", err);
            }
        }

        aplicarFiltro();
    }, [tipoFiltro, valorFiltro]);

    const toggleExpandir = (id) =>
        setTareasExpandidas((prev) =>
            prev.includes(id) ? prev.filter((x) => x !== id) : [...prev, id]
        );

    const expandirTodas = () => setTareasExpandidas(tareas.map((t) => t.idTarea));
    const colapsarTodas = () => setTareasExpandidas([]);

    const navigate = useNavigate();
    function handleLogout() {
        localStorage.removeItem("token");
        navigate("/");
    }


    return (
        <div className="flex">
            <Sidebar onLogout={handleLogout} />
            <main className="flex-1 min-h-screen bg-green-50 p-6">
                <div className="flex justify-between mb-4">
                    <h2 className="text-xl font-bold text-green-700">Tus tareas</h2>
                    <div className="flex gap-2 items-start">
                        <BaseSelect
                            name="orden"
                            value={ordenActual}
                            onChange={(e) => setOrdenActual(e.target.value)}
                            className="w-38"
                        >
                            <option value="" disabled>Ordenar por</option>
                            <option value="titulo">Título</option>
                            <option value="tiempo">Tiempo estimado</option>
                            <option value="prioridad">Prioridad</option>
                            <option value="hoy">Tareas de hoy</option>
                        </BaseSelect>
                        <BaseSelect
                            name="tipoFiltro"
                            value={tipoFiltro}
                            onChange={(e) => setTipoFiltro(e.target.value)}
                            className="w-32"
                        >
                            <option value="" disabled>Filtrar por</option>
                            <option value="prioridad">Prioridad</option>
                            <option value="estado">Estado</option>
                            <option value="categoria">Categoría</option>
                            <option value="tiempo">Tiempo máximo</option>
                            <option value="palabras">Palabras clave</option>
                        </BaseSelect>
                        {tipoFiltro === "prioridad" && (
                            <BaseSelect
                                name="valorFiltro"
                                value={valorFiltro}
                                onChange={(e) => setValorFiltro(e.target.value)}
                                className="w-30"
                            >
                                <option value="" disabled>Prioridad</option>
                                <option value="BAJA">Baja</option>
                                <option value="MEDIA">Media</option>
                                <option value="ALTA">Alta</option>
                                <option value="IMPRESCINDIBLE">Imprescindible</option>
                            </BaseSelect>
                        )}
                        {tipoFiltro === "estado" && (
                            <BaseSelect
                                name="valorFiltro"
                                value={valorFiltro}
                                onChange={(e) => setValorFiltro(e.target.value)}
                                className="w-40"
                            >
                                <option value="" disabled>Estado</option>
                                <option value="EN_CURSO">En curso</option>
                                <option value="COMPLETADA">Completada</option>
                                <option value="COMPLETADA_CON_RETRASO">Completada con retraso</option>
                                <option value="VENCIDA">Vencida</option>
                                <option value="SIN_FECHA">Sin fecha</option>
                            </BaseSelect>
                        )}
                        {tipoFiltro === "categoria" && (
                            <BaseSelect
                                name="valorFiltro"
                                value={valorFiltro}
                                onChange={(e) => setValorFiltro(e.target.value)}
                                className="w-64"
                            >
                                <option value="" disabled>Categoría</option>
                                {Object.entries(categoriasMap).map(([id, cat]) => (
                                    <option key={id} value={id}>
                                        {cat.icono ? `${cat.icono} ` : ""}{cat.nombre}
                                    </option>
                                ))}
                            </BaseSelect>
                        )}
                        {tipoFiltro === "tiempo" && (
                            <BaseInput
                                name="valorFiltro"
                                value={valorFiltro}
                                onChange={(e) => setValorFiltro(e.target.value)}
                                type="number"
                                placeholder="Minutos"
                                className="w-40"
                            />
                        )}
                        {tipoFiltro === "palabras" && (
                            <BaseInput
                                name="valorFiltro"
                                value={valorFiltro}
                                onChange={(e) => setValorFiltro(e.target.value)}
                                placeholder="Buscar palabras clave..."
                                className="w-64"
                            />
                        )}

                        <BaseButton onClick={() => setBaseModalNuevaTarea(true)} variant="success" size="md">
                            Nueva tarea
                        </BaseButton>
                        <BaseButton onClick={expandirTodas} variant="primary" size="md">
                            Expandir todas
                        </BaseButton>
                        <BaseButton onClick={colapsarTodas} variant="secondary" size="md">
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

                        // Categoría desde el mapa (si existe)
                        const cat = categoriasMap[t.idCategoria] || null;

                        return (
                            <div
                                key={t.idTarea}
                                className="relative rounded-lg border overflow-hidden shadow-sm cursor-pointer"
                                onClick={() => toggleExpandir(t.idTarea)}
                            >
                                {/* FONDO ÚNICO: barra (colapsado) o flecha (expandido) */}
                                <div
                                    className="absolute inset-0"
                                    style={{
                                        backgroundImage: `linear-gradient(135deg, ${cPrioridad} 50%, ${cEstado} 50%), linear-gradient(225deg, ${cEstado} 50%, ${cPrioridad} 50%)`,
                                        backgroundSize: expandida ? "100% 50%, 100% 50%" : "100% 100%, 0 0",
                                        backgroundPosition: "top left, bottom left",
                                        backgroundRepeat: "no-repeat",
                                    }}
                                />

                                {/* CONTENIDO */}
                                <div className="relative">
                                    {/* Cabecera */}
                                    <div className="p-4 bg-white/80">
                                        <div className="flex justify-between items-start">
                                            <div>
                                                <div className="flex items-center gap-2">
                                                    <h3 className="text-lg font-semibold text-gray-800">{t.titulo}</h3>
                                                    {/* Icono de categoría en cabecera (si existe) */}
                                                    {cat?.icono && (
                                                        <span
                                                            className="text-xl leading-none"
                                                            title={cat?.nombre || t?.categoriaNombre || "Categoría"}
                                                        >
                                                            {cat.icono}
                                                        </span>
                                                    )}
                                                </div>

                                                {expandida && t.descripcion && (
                                                    <p className="text-sm text-gray-600 mt-1">{t.descripcion}</p>
                                                )}
                                            </div>
                                            {t.fechaEntrega && (
                                                <span className="text-xs text-gray-500">
                                                    {new Date(t.fechaEntrega).toLocaleDateString()}
                                                </span>
                                            )}
                                        </div>
                                    </div>

                                    {/* Detalle expandido */}
                                    {expandida && (
                                        <div className="text-sm text-gray-800">
                                            <div className="relative z-10 p-3 bg-white/80">
                                                <p>
                                                    <strong>Prioridad:</strong> {pretty(t.prioridad)}
                                                </p>
                                                <p>
                                                    <strong>Estado:</strong> {pretty(t.estado)}
                                                </p>
                                                <p>
                                                    <strong>Tiempo estimado:</strong> {t.tiempo}min
                                                </p>
                                                {(cat || t?.categoriaNombre) && (
                                                    <p>
                                                        <strong>Categoría:</strong>
                                                        <span className="ml-1">
                                                            {cat?.icono ? `${cat.icono} ` : ""}
                                                            {cat?.nombre || t?.categoriaNombre}
                                                        </span>
                                                    </p>
                                                )}

                                                {/* Pie con botón Editar */}
                                                <div className="mt-3 flex justify-end">
                                                    <BaseButton
                                                        size="sm"
                                                        variant="primary"
                                                        onClick={(e) => {
                                                            e.stopPropagation(); // evitar colapsar
                                                            setErrorEditar("");
                                                            setTareaEdit({
                                                                idTarea: t.idTarea,
                                                                titulo: t.titulo || "",
                                                                descripcion: t.descripcion || "",
                                                                tiempo: String(t.tiempo ?? ""), // mantener como string en input
                                                                prioridad: t.prioridad || "",
                                                                fechaEntrega: t.fechaEntrega
                                                                    ? new Date(t.fechaEntrega).toISOString().slice(0, 16) // yyyy-MM-ddTHH:mm
                                                                    : "",
                                                                idCategoria: t.idCategoria ? String(t.idCategoria) : "",
                                                            });
                                                            setEditOpen(true);
                                                        }}
                                                    >
                                                        Editar
                                                    </BaseButton>
                                                </div>
                                            </div>
                                        </div>
                                    )}


                                </div>
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

                            console.log("[NuevaTarea] SUBMIT disparado. Estado:", nuevaTarea);

                            // Validación mínima
                            if (!nuevaTarea.titulo?.trim())
                                return setErrorNuevaTarea("El título es obligatorio.");
                            if (!nuevaTarea.prioridad)
                                return setErrorNuevaTarea("La prioridad es obligatoria.");
                            if (!nuevaTarea.tiempo || Number.isNaN(Number(nuevaTarea.tiempo)))
                                return setErrorNuevaTarea("El tiempo (min) es obligatorio y numérico.");
                            if (!nuevaTarea.idCategoria)
                                return setErrorNuevaTarea("La categoría es obligatoria.");

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
                            if (nuevaTarea.idCategoria)
                                payload.idCategoria = Number(nuevaTarea.idCategoria);

                            console.log("[NuevaTarea] Payload POST /api/tarea/add:", payload);

                            try {
                                const resp = await addTarea(payload);
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
                                console.error("[NuevaTarea] ERROR POST:", {
                                    message: err?.message,
                                    status: err?.response?.status,
                                    data: err?.response?.data,
                                    url: err?.config?.baseURL + err?.config?.url,
                                    method: err?.config?.method,
                                });
                                setErrorNuevaTarea(
                                    err?.response?.data?.message || "No se pudo crear la tarea."
                                );
                            }
                        }}
                        className="space-y-4"
                    >
                        {/* Título */}
                        <BaseField
                            id="titulo"
                            label="Título"
                            requerido
                            placeholder="Ej.: Comprar pan"
                            value={nuevaTarea.titulo}
                            onChange={(e) =>
                                setNuevaTarea((s) => ({ ...s, titulo: e.target.value }))
                            }
                            required
                        />

                        {/* Descripción */}
                        <BaseField id="descripcion" label="Descripción">
                            <BaseTextarea
                                id="descripcion"
                                rows={4}
                                placeholder="Detalles opcionales…"
                                value={nuevaTarea.descripcion}
                                onChange={(e) =>
                                    setNuevaTarea((s) => ({ ...s, descripcion: e.target.value }))
                                }
                            />
                        </BaseField>

                        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                            {/* Prioridad */}
                            <BaseField id="prioridad" label="Prioridad" requerido>
                                <SelectPrioridad
                                    id="prioridad"
                                    value={nuevaTarea.prioridad}
                                    onChange={(e) =>
                                        setNuevaTarea((s) => ({ ...s, prioridad: e.target.value }))
                                    }
                                    required
                                />
                            </BaseField>

                            {/* Categoría */}
                            <BaseField id="categoria" label="Categoría" requerido>
                                <div className="flex gap-2 items-end">
                                    <div className="flex-1">
                                        <SelectCategoria
                                            key={catReloadKey}
                                            id="categoria"
                                            value={nuevaTarea.idCategoria}
                                            onChange={(e) =>
                                                setNuevaTarea((s) => ({
                                                    ...s,
                                                    idCategoria: e.target.value,
                                                }))
                                            }
                                            required
                                        />
                                    </div>
                                    <BaseButton
                                        type="button"
                                        variant="secondary"
                                        size="sm"
                                        onClick={() => setOpenAddCat(true)}
                                        title="Añadir nueva categoría"
                                    >
                                        + Añadir
                                    </BaseButton>
                                </div>
                            </BaseField>
                        </div>

                        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                            {/* Fecha de entrega (opcional) */}
                            <BaseField id="fechaEntrega" label="Fecha de entrega">
                                <input
                                    id="fechaEntrega"
                                    type="datetime-local"
                                    className="px-3 py-2 border rounded w-full focus:outline-none focus:ring-2 focus:ring-blue-400"
                                    value={nuevaTarea.fechaEntrega}
                                    onChange={(e) =>
                                        setNuevaTarea((s) => ({
                                            ...s,
                                            fechaEntrega: e.target.value,
                                        }))
                                    }
                                />
                            </BaseField>

                            {/* Tiempo (min) */}
                            <BaseField
                                id="tiempo"
                                label="Tiempo (min)"
                                requerido
                                type="number"
                                min="1"
                                placeholder="ej.: 30"
                                value={nuevaTarea.tiempo}
                                onChange={(e) =>
                                    setNuevaTarea((s) => ({ ...s, tiempo: e.target.value }))
                                }
                                required
                            />
                        </div>

                        {errorNuevaTarea && (
                            <p className="text-sm text-red-600">{errorNuevaTarea}</p>
                        )}

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
                {/* Modal: Editar tarea */}
                <BaseModal
                    open={editOpen}
                    onClose={() => {
                        setEditOpen(false);
                        setErrorEditar("");
                    }}
                    title="Editar tarea"
                >
                    <form
                        onSubmit={async (e) => {
                            e.preventDefault();
                            setErrorEditar("");

                            if (!tareaEdit.idTarea) return setErrorEditar("ID de tarea no válido.");
                            if (!tareaEdit.titulo?.trim()) return setErrorEditar("El título es obligatorio.");
                            if (!tareaEdit.prioridad) return setErrorEditar("La prioridad es obligatoria.");
                            if (!tareaEdit.tiempo || Number.isNaN(Number(tareaEdit.tiempo)))
                                return setErrorEditar("El tiempo (min) es obligatorio y numérico.");
                            if (!tareaEdit.idCategoria) return setErrorEditar("La categoría es obligatoria.");

                            const payload = {
                                titulo: tareaEdit.titulo.trim(),
                                descripcion: (tareaEdit.descripcion || "").trim(),
                                tiempo: Number(tareaEdit.tiempo),
                                prioridad: tareaEdit.prioridad,
                                fechaEntrega:
                                    tareaEdit.fechaEntrega?.length === 16
                                        ? `${tareaEdit.fechaEntrega}:00`
                                        : tareaEdit.fechaEntrega || null, // opcional
                                idCategoria: Number(tareaEdit.idCategoria),
                            };

                            try {
                                await updateTarea(tareaEdit.idTarea, payload);
                                const { data } = await getTareas();
                                setTareas(Array.isArray(data) ? data : []);
                                setEditOpen(false);
                            } catch (err) {
                                console.error("[EditarTarea] ERROR PUT:", {
                                    message: err?.message,
                                    status: err?.response?.status,
                                    data: err?.response?.data,
                                    url: err?.config?.baseURL + err?.config?.url,
                                    method: err?.config?.method,
                                });
                                setErrorEditar(err?.response?.data?.message || "No se pudo actualizar la tarea.");
                            }
                        }}
                        className="space-y-4"
                    >
                        {/* Título */}
                        <BaseField
                            id="edit_titulo"
                            label="Título"
                            requerido
                            placeholder="Ej.: Comprar pan"
                            value={tareaEdit.titulo}
                            onChange={(e) => setTareaEdit((s) => ({ ...s, titulo: e.target.value }))}
                            required
                        />

                        {/* Descripción */}
                        <BaseField id="edit_descripcion" label="Descripción">
                            <BaseTextarea
                                id="edit_descripcion"
                                rows={4}
                                placeholder="Detalles opcionales…"
                                value={tareaEdit.descripcion}
                                onChange={(e) => setTareaEdit((s) => ({ ...s, descripcion: e.target.value }))}
                            />
                        </BaseField>

                        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                            {/* Prioridad */}
                            <BaseField id="edit_prioridad" label="Prioridad" requerido>
                                <SelectPrioridad
                                    id="edit_prioridad"
                                    value={tareaEdit.prioridad}
                                    onChange={(e) => setTareaEdit((s) => ({ ...s, prioridad: e.target.value }))}
                                    required
                                />
                            </BaseField>

                            {/* Categoría */}
                            <BaseField id="edit_categoria" label="Categoría" requerido>
                                <SelectCategoria
                                    id="edit_categoria"
                                    value={tareaEdit.idCategoria}
                                    onChange={(e) => setTareaEdit((s) => ({ ...s, idCategoria: e.target.value }))}
                                    required
                                />
                            </BaseField>
                        </div>

                        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                            {/* Fecha de entrega (opcional) */}
                            <BaseField id="edit_fechaEntrega" label="Fecha de entrega">
                                <input
                                    id="edit_fechaEntrega"
                                    type="datetime-local"
                                    className="px-3 py-2 border rounded w-full focus:outline-none focus:ring-2 focus:ring-blue-400"
                                    value={tareaEdit.fechaEntrega}
                                    onChange={(e) => setTareaEdit((s) => ({ ...s, fechaEntrega: e.target.value }))}
                                />
                            </BaseField>

                            {/* Tiempo (min) */}
                            <BaseField
                                id="edit_tiempo"
                                label="Tiempo (min)"
                                requerido
                                type="number"
                                min="1"
                                placeholder="ej.: 30"
                                value={tareaEdit.tiempo}
                                onChange={(e) => setTareaEdit((s) => ({ ...s, tiempo: e.target.value }))}
                                required
                            />
                        </div>

                        {errorEditar && <p className="text-sm text-red-600">{errorEditar}</p>}

                        <div className="flex justify-end gap-3 pt-2">
                            <BaseButton
                                variant="secondary"
                                type="button"
                                onClick={() => {
                                    setEditOpen(false);
                                    setErrorEditar("");
                                }}
                            >
                                Cancelar
                            </BaseButton>

                            <BaseButton type="submit">Guardar cambios</BaseButton>
                        </div>
                    </form>
                </BaseModal>

                {/* Modal: Añadir categoría */}
                <AddCategoriaModal
                    open={openAddCat}
                    onClose={() => setOpenAddCat(false)}
                    onCreated={(cat) => {
                        // seleccionar la nueva categoría y recargar el selector
                        setNuevaTarea((s) => ({
                            ...s,
                            idCategoria: String(cat.idCategoria ?? cat.id),
                        }));
                        setCatReloadKey((k) => k + 1);
                        setOpenAddCat(false);
                    }}
                />
            </main>
        </div>

    );
}
