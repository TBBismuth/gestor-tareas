import { useState } from "react";
import { useNavigate } from "react-router-dom";
import BaseCard from "../components/base/BaseCard";
import BaseField from "../components/base/BaseField";
import BaseButton from "../components/base/BaseButton";
import { loginUser, registerUser, setToken } from "../services/authService";

export default function Login() {
    const [mode, setMode] = useState("login"); // "login" | "register"
    const [nombre, setNombre] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [password2, setPassword2] = useState("");
    const [showPassword, setShowPassword] = useState(false);
    const [error, setError] = useState("");
    const navigate = useNavigate();

    function validateRegister() {
        if (!nombre.trim() || nombre.trim().length < 3 || nombre.trim().length > 32) {
            return "El nombre debe tener entre 3 y 32 caracteres.";
        }
        const emailOk = /^\S+@\S+\.\S+$/.test(email);
        if (!emailOk) return "Email no válido.";
        const passOk = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{8,}$/.test(password);
        if (!passOk) return "La contraseña debe tener al menos 8 caracteres, una mayúscula, una minúscula y un número.";
        if (password !== password2) return "Las contraseñas no coinciden.";
        return "";
    }

    async function handleSubmit(e) {
        e.preventDefault();
        setError("");

        try {
            if (mode === "register") {
                // Validación mínima client-side según backend
                const errMsg = validateRegister();
                if (errMsg) {
                    setError(errMsg);
                    return;
                }

                // 1) Registrar
                await registerUser({ nombre: nombre.trim(), email: email.trim(), password });

                // 2) Auto-login
                const loginRes = await loginUser({ email: email.trim(), password });
                setToken(loginRes.token);
                navigate("/home");
                return;
            }

            // LOGIN
            const response = await loginUser({ email: email.trim(), password });
            setToken(response.token);
            navigate("/home");
        } catch (err) {
            console.error(err);

            const status = err?.response?.status;
            const backendMsg =
                err?.response?.data?.message ||
                err?.response?.data?.error ||
                "";

            if (mode === "register") {
                // Detectar error de email duplicado
                const isEmailDuplicado =
                    status === 400 ||
                    /email/i.test(backendMsg) && /duplicad|exist|registrad/i.test(backendMsg);

                setError(
                    isEmailDuplicado
                        ? "Ese email ya está registrado."
                        : "No se pudo registrar. Revisa los datos."
                );
            } else {
                setError("Credenciales inválidas");
            }
        }
    }

    return (
        <div className="min-h-screen flex items-center justify-center p-4 bg-gray-50">
            <BaseCard className="w-full max-w-sm">
                <h1 className="mb-4 text-xl font-semibold">
                    {mode === "login" ? "Iniciar sesión" : "Crear cuenta"}
                </h1>

                <form onSubmit={handleSubmit}>
                    {mode === "register" && (
                        <BaseField
                            id="nombre"
                            label="Nombre"
                            requerido
                            value={nombre}
                            onChange={(e) => setNombre(e.target.value)}
                            placeholder="Tu nombre"
                        />
                    )}

                    <BaseField
                        id="email"
                        label="Email"
                        requerido
                        type="email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        placeholder="tú@correo.com"
                    />

                    <BaseField
                        id="password"
                        label="Contraseña"
                        requerido
                        type={showPassword ? "text" : "password"}
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        placeholder="••••••••"
                    />
                    <div className="mt-2 flex items-center gap-2">
                        <input
                            id="togglePass"
                            type="checkbox"
                            checked={showPassword}
                            onChange={(e) => setShowPassword(e.target.checked)}
                        />
                        <label htmlFor="togglePass" className="text-sm">Mostrar contraseña</label>
                    </div>


                    {mode === "register" && (
                        <BaseField
                            id="password2"
                            label="Repite la contraseña"
                            requerido
                            type={showPassword ? "text" : "password"}
                            value={password2}
                            onChange={(e) => setPassword2(e.target.value)}
                            placeholder="••••••••"
                        />
                    )}

                    {mode === "register" && (
                        <p className="text-xs text-gray-500 mt-1">
                            Debe incluir al menos 8 caracteres, 1 mayúscula, 1 minúscula y 1 número.
                        </p>
                    )}

                    {error && <p className="text-red-600 text-sm mt-2">{error}</p>}

                    <BaseButton type="submit" className="w-full mt-3">
                        {mode === "login" ? "Entrar" : "Crear cuenta"}
                    </BaseButton>
                </form>

                <div className="mt-3 text-sm text-center">
                    {mode === "login" ? (
                        <button
                            type="button"
                            className="text-blue-600 underline cursor-pointer hover:text-blue-700"
                            onClick={() => { setError(""); setMode("register"); }}
                            title="Crear una nueva cuenta"
                        >
                            ¿No tienes cuenta? Regístrate
                        </button>
                    ) : (
                        <button
                            type="button"
                            className="text-blue-600 underline cursor-pointer hover:text-blue-700"
                            onClick={() => { setError(""); setMode("login"); }}
                            title="Ir a iniciar sesión"
                        >
                            ¿Ya tienes cuenta? Inicia sesión
                        </button>
                    )}
                </div>
            </BaseCard>
        </div>
    );
}

