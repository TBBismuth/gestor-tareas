import Boton from "./base/Boton";
import { clearToken } from "../services/auth";
import { useNavigate } from "react-router-dom";

export default function Header() {
    const navigate = useNavigate();

    function handleLogout() {
        clearToken();
        navigate("/login", { replace: true });
    }

    return (
        <header className="w-full bg-white border-b shadow-sm">
            <div className="max-w-5xl mx-auto px-4 py-3 flex items-center justify-between">
                <h1 className="text-lg font-semibold">Gestor de Tareas</h1>
                <Boton onClick={handleLogout}>Cerrar sesión</Boton>
            </div>
        </header>
    );
}
