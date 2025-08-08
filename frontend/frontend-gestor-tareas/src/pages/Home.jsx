import { useEffect, useState } from "react";
import Header from "../components/Header";
import { getTareas } from "../services/tareas";
import Boton from "../components/base/Boton";
import { PRIORIDAD_BG, ESTADO_BG, pick } from "../styles/themeColors";

export default function Home() {
    const [tareas, setTareas] = useState([]);
    const [cargando, setCargando] = useState(true);
    const [error, setError] = useState("");
    const [tareasExpandidas, setTareasExpandidas] = useState([]);

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
        return () => { cancelado = true; };
    }, []);

    const toggleExpandir = (id) =>
        setTareasExpandidas((prev) =>
            prev.includes(id) ? prev.filter((x) => x !== id) : [...prev, id]
        );

    const expandirTodas = () => setTareasExpandidas(tareas.map((t) => t.idTarea));
    const colapsarTodas = () => setTareasExpandidas([]);

    return (
        <div className="min-h-screen bg-green-50">
            <Header />
            <main className="max-w-5xl mx-auto px-4 py-6">
                <div className="flex justify-between mb-4">
                    <h2 className="text-xl font-bold text-green-700">Tus tareas</h2>
                    <div className="flex gap-2 items-start">
                        <Boton onClick={expandirTodas} variant="primary" size="lg">Expandir todas</Boton>
                        <Boton onClick={colapsarTodas} variant="secondary" size="sm">Colapsar todas</Boton>
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
                                                <h3 className="text-lg font-semibold text-gray-800">{t.titulo}</h3>
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
                                </div>

                                {/* CONTENIDO EXPANDIDO: diagonal invertida ↙ (225deg) y SIN bordes internos */}
                                {expandida && (
                                    <div
                                        className="relative text-sm text-gray-800"
                                        style={{
                                            // invertimos el ángulo para que ambas diagonales se unan formando "<"
                                            background: `linear-gradient(225deg, ${cEstado} 50%, ${cPrioridad} 50%)`,
                                        }}
                                    >
                                        <div className="relative z-10 p-3 bg-white/80">
                                            <p><strong>Prioridad:</strong> {t.prioridad}</p>
                                            <p><strong>Estado:</strong> {t.estado}</p>
                                            <p><strong>Tiempo estimado:</strong> {t.tiempo}h</p>
                                        </div>
                                    </div>
                                )}
                            </div>
                        );
                    })}
                </div>
            </main>
        </div>
    );
}
