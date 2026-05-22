import { useState } from "react";
import AppShell from "../../components/layout/AppShell.jsx";
import RightSidebar from "../../components/layout/RightSidebar.jsx";
import ViewActionsBar from "../../components/layout/ViewActionsBar.jsx";
import MegaFilterBar from "./components/MegaFilterBar.jsx";
import TaskList from "./components/TaskList.jsx";
import { mockTasks } from "./data/mockTasks.js";

export default function DashboardPage() {
  const [focusArea, setFocusArea] = useState("filter");

  return (
    <AppShell
      focusArea={focusArea}
      topBar={<MegaFilterBar onFocus={() => setFocusArea("filter")} />}
      secondaryBar={
        <ViewActionsBar
          dimmed={focusArea === "filter"}
          onFocus={() => setFocusArea("sidebar")}
        />
      }
      sidebar={
        <RightSidebar
          dimmed={focusArea === "filter"}
          onFocus={() => setFocusArea("sidebar")}
        />
      }
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
