import { Routes, Route, Navigate } from "react-router-dom";
import LoginPage from "./pages/LoginPage";
import TasksPage from "./pages/TasksPage";
import RequireAuth from "./components/RequireAuth";
import CategoryPage from "./pages/CategoryPage";


export default function App() {
  return (
    <Routes>
      <Route path="/" element={<Navigate to="/Login" replace />} />
      <Route path="/login" element={<LoginPage />} />
      <Route path="/home" element={<RequireAuth> <TasksPage /> </RequireAuth>} />
      <Route path="/categories" element={<RequireAuth> <CategoryPage /> </RequireAuth>} />
    </Routes>
  );
}


