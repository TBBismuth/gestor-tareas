import { useNavigate } from "react-router-dom";
import BaseButton from "./base/BaseButton";

export default function Sidebar({ onLogout }) {
    const navigate = useNavigate();

    return (
        <aside className="h-screen w-60 bg-gray-100 border-r p-4 flex flex-col justify-between">
            <div>
                <h2 className="text-xl font-semibold mb-6">Tu Gestor</h2>

                <nav className="flex flex-col gap-2">
                    <BaseButton variant="primary" size="sm" onClick={() => navigate("/home")}>Mis tareas</BaseButton>
                    <BaseButton variant="primary" size="sm" onClick={() => navigate("/categories")}>Mis categorías</BaseButton>
                    {/*<BaseButton variant="primary" size="sm" onClick={() => navigate("/perfil")}>Mi perfil</BaseButton>*/}
                </nav>
            </div>

            <BaseButton variant="secondary" size="sm" className="w-full" onClick={onLogout}>Cerrar sesión</BaseButton>
        </aside>
    );
}
