import { Navigate, createBrowserRouter } from "react-router-dom";
import DashboardPage from "../features/dashboard/DashboardPage.jsx";
import LoginPage from "../features/auth/LoginPage.jsx";
import RegisterPage from "../features/auth/RegisterPage.jsx";
import { ProtectedRoute, PublicOnlyRoute } from "../features/auth/AuthRoutes.jsx";

export const router = createBrowserRouter([
  {
    path: "/",
    element: <Navigate to="/app" replace />,
  },
  {
    element: <PublicOnlyRoute />,
    children: [
      {
        path: "/login",
        element: <LoginPage />,
      },
      {
        path: "/register",
        element: <RegisterPage />,
      },
    ],
  },
  {
    element: <ProtectedRoute />,
    children: [
      {
        path: "/app",
        element: <DashboardPage />,
      },
    ],
  },
]);
