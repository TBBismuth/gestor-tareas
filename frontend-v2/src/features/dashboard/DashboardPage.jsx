import { useState } from "react";
import AppShell from "../../components/layout/AppShell.jsx";
import RightSidebar from "../../components/layout/RightSidebar.jsx";
import MegaFilterBar from "./components/MegaFilterBar.jsx";
import TaskList from "./components/TaskList.jsx";

const mockTasks = [
  {
    idTarea: 1,
    titulo: "Preparar defensa del TFG",
    descripcion: "Revisar guion, capturas y flujo de demostracion.",
    prioridad: "IMPRESCINDIBLE",
    estado: "EN_CURSO",
    tiempo: 90,
    fechaEntrega: "2026-05-27T18:00:00",
    categoria: "Universidad",
    origenTarea: "PERSONAL",
  },
  {
    idTarea: 2,
    titulo: "Validar tareas asignadas del grupo",
    descripcion: "Comprobar entregas pendientes y dejar comentarios de revision.",
    prioridad: "ALTA",
    estado: "VENCIDA",
    tiempo: 45,
    fechaEntrega: "2026-05-20T12:00:00",
    categoria: "Grupo",
    origenTarea: "GRUPO",
    nombreGrupoOrigen: "Equipo backend",
  },
  {
    idTarea: 3,
    titulo: "Ordenar backlog visual",
    descripcion: "Agrupar ideas de UI que no entran en el primer bloque.",
    prioridad: "MEDIA",
    estado: "SIN_FECHA",
    tiempo: 30,
    categoria: "Frontend",
    origenTarea: "PERSONAL",
  },
  {
    idTarea: 4,
    titulo: "Cerrar checklist de documentacion",
    descripcion: "Marcar puntos completados y dejar riesgos abiertos.",
    prioridad: "BAJA",
    estado: "COMPLETADA",
    tiempo: 25,
    fechaEntrega: "2026-05-21T09:00:00",
    categoria: "Documentacion",
    origenTarea: "PERSONAL",
  },
];

export default function DashboardPage() {
  const [focusArea, setFocusArea] = useState("filter");

  return (
    <AppShell
      focusArea={focusArea}
      topBar={<MegaFilterBar onFocus={() => setFocusArea("filter")} />}
      sidebar={<RightSidebar onFocus={() => setFocusArea("sidebar")} />}
    >
      <div className="flex items-start justify-between gap-4">
        <div>
          <p className="text-xs font-semibold uppercase tracking-wide text-muted">
            Pantalla principal
          </p>
          <h1 className="mt-1 text-2xl font-semibold text-primary">Tareas</h1>
        </div>
        <p className="max-w-sm text-right text-sm leading-6 text-secondary">
          Vista mock del bloque 1. La API real y el megafiltro se conectaran en
          bloques posteriores.
        </p>
      </div>
      <TaskList tasks={mockTasks} />
    </AppShell>
  );
}
