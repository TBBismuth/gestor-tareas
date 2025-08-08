import { useState } from "react";
import { useNavigate } from "react-router-dom";
import Card from "../components/base/Card";
import Campo from "../components/base/Campo";
import Boton from "../components/base/Boton";
import api from "../services/api";

export default function Login() {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");
    const navigate = useNavigate();

    async function handleSubmit(e) {
        e.preventDefault();
        setError("");

        try {
            const response = await api.post("/usuario/login", { email, password });

            const token = response.data.token;
            localStorage.setItem("token", token); // guardar token
            navigate("/home"); // redirigir a la página mock
        } catch (err) {
            console.error(err);
            setError("Credenciales inválidas");
        }
    }

    return (
        <div className="min-h-screen flex items-center justify-center p-4 bg-gray-50">
            <Card className="w-full max-w-sm">
                <h1 className="mb-4 text-xl font-semibold">Iniciar sesión</h1>
                <form onSubmit={handleSubmit}>
                    <Campo
                        id="email"
                        label="Email"
                        requerido
                        type="email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        placeholder="tú@correo.com"
                    />
                    <Campo
                        id="password"
                        label="Contraseña"
                        requerido
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        placeholder="••••••••"
                    />
                    {error && <p className="text-red-600 text-sm mt-2">{error}</p>}
                    <Boton type="submit" className="w-full mt-2">Entrar</Boton>
                </form>
            </Card>
        </div>
    );
}
