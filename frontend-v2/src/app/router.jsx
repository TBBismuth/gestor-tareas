import { Navigate, createBrowserRouter } from "react-router-dom";
import DashboardPage from "../features/dashboard/DashboardPage.jsx";

function LoginPlaceholder() {
  return (
    <main className="grid min-h-screen place-items-center bg-app px-4">
      <section className="w-full max-w-sm rounded-panel border border-app bg-panel p-6 shadow-panel">
        <p className="text-sm font-semibold uppercase tracking-wide text-muted">Acceso</p>
        <h1 className="mt-2 text-2xl font-semibold text-primary">Login pendiente</h1>
        <p className="mt-3 text-sm leading-6 text-secondary">
          Placeholder del bloque 1. La autenticacion real se implementara en el
          bloque 2.
        </p>
      </section>
    </main>
  );
}

export const router = createBrowserRouter([
  {
    path: "/",
    element: <Navigate to="/app" replace />,
  },
  {
    path: "/login",
    element: <LoginPlaceholder />,
  },
  {
    path: "/app",
    element: <DashboardPage />,
  },
]);
