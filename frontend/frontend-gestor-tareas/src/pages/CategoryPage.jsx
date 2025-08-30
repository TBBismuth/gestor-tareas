import { useEffect, useState } from "react";
import { getCategorias, deleteCategoria } from "../services/categoriaService";
import BaseCard from "../components/base/BaseCard";
import Sidebar from "../components/Sidebar";
import { useNavigate } from "react-router-dom";
import BaseButton from "../components/base/BaseButton";
import AddCategoriaModal from "../components/AddCategoriaModal";

export default function CategoryPage() {
    const [categorias, setCategorias] = useState([]);
    const [editandoCategoria, setEditandoCategoria] = useState(null);
    const navigate = useNavigate();
    const [openCreate, setOpenCreate] = useState(false);


    useEffect(() => {
        async function cargarCategorias() {
            try {
                const data = await getCategorias();
                setCategorias(data);
            } catch (err) {
                console.error("Error al cargar categorías:", err);
            }
        }
        cargarCategorias();
    }, []);

    function handleLogout() {
        localStorage.removeItem("token");
        navigate("/");
    }

    function handleEdit(categoria) {
        setEditandoCategoria(categoria);
    }

    async function handleDelete(categoria) {
        const ok = window.confirm(`¿Eliminar la categoría "${categoria.nombre}"? Esta acción es irreversible. Las tareas asociadas a esta categoria se quedarán huerfanas de dicha.`);
        if (!ok) return;
        try {
            await deleteCategoria(categoria.idCategoria);
            const data = await getCategorias();
            setCategorias(data);
        } catch (err) {
            console.error("Error al eliminar la categoría:", err);
            alert("No se pudo eliminar la categoría.");
        }
    }



    return (
        <div className="flex">
            <Sidebar onLogout={handleLogout} />

            <main className="flex-1 min-h-screen bg-green-50 p-6">
                <div className="flex justify-between items-center mb-4">
                    <h2 className="text-xl font-bold text-green-700">Tus categorias</h2>
                    <BaseButton variant="success" size="md" onClick={() => { setEditandoCategoria(null); setOpenCreate(true); }}>
                        Nueva Categoria
                    </BaseButton>
                </div>

                <div className="grid gap-4 grid-cols-1 sm:grid-cols-2 md:grid-cols-3">
                    {categorias.map((cat) => (
                        <BaseCard
                            key={cat.idCategoria}
                            className="relative p-4"
                            style={{ backgroundColor: cat.color }}
                        >
                            <div className="flex items-center gap-4 pr-10">
                                <span className="text-xl">{cat.icono}</span>
                                <span className="text-sm font-medium">{cat.nombre}</span>
                            </div>

                            {/* Botón eliminar (encima del editar) */}
                            <BaseButton
                                variant="ghost"
                                size="xs"
                                className="absolute bottom-8 right-2"
                                onClick={() => handleDelete(cat)}
                                title="Eliminar categoría"
                            >
                                🗑️
                            </BaseButton>

                            {/* Botón editar */}
                            <BaseButton
                                variant="ghost"
                                size="xs"
                                className="absolute bottom-2 right-2"
                                onClick={() => setEditandoCategoria(cat)}
                                title="Editar categoría"
                            >
                                ✏️
                            </BaseButton>
                        </BaseCard>
                    ))}
                </div>

                {/* Modal CREAR */}
                <AddCategoriaModal
                    open={openCreate}
                    onClose={() => setOpenCreate(false)}
                    onCreated={() => {
                        setOpenCreate(false);
                        getCategorias().then(setCategorias);
                    }}
                />

                {/* Modal EDITAR */}
                <AddCategoriaModal
                    open={!!editandoCategoria}
                    onClose={() => setEditandoCategoria(null)}
                    onCreated={() => {
                        setEditandoCategoria(null);
                        getCategorias().then(setCategorias);
                    }}
                    initialData={editandoCategoria}
                />
            </main>
        </div>
    );


}
