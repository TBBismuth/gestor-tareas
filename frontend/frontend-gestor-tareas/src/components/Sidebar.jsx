import BaseButton from "./base/BaseButton";

export default function Sidebar({ onLogout }) {
    return (
        <aside className="h-screen w-60 bg-gray-100 border-r p-4 flex flex-col justify-between">
            <div>
                <h2 className="text-xl font-semibold mb-6">Tu Gestor</h2>

                <nav className="flex flex-col gap-2">
                    <BaseButton variant="secundario" size="sm">Mis tareas</BaseButton>
                    <BaseButton variant="secundario" size="sm">Categorías</BaseButton>
                    <BaseButton variant="secundario" size="sm">Mi perfil</BaseButton>
                </nav>
            </div>

            <BaseButton
                variant="secundario"
                size="sm"
                className="w-full"
                onClick={onLogout}
            >
                Cerrar sesión
            </BaseButton>
        </aside>
    );
}
