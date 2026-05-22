import { UserPlus } from "lucide-react";
import { useForm } from "react-hook-form";
import { Link, useNavigate } from "react-router-dom";
import { toast } from "sonner";
import Button from "../../components/ui/Button.jsx";
import { getAuthErrorMessage } from "./authErrors.js";
import { useAuth } from "./AuthContext.jsx";
import PasswordField from "./PasswordField.jsx";
import PasswordStrengthMeter from "./PasswordStrengthMeter.jsx";
import TextField from "./TextField.jsx";

export default function RegisterForm() {
  const navigate = useNavigate();
  const { register: registerUser } = useAuth();
  const {
    register,
    handleSubmit,
    watch,
    formState: { errors, isSubmitting },
  } = useForm({
    defaultValues: {
      nombre: "",
      email: "",
      password: "",
      confirmPassword: "",
    },
  });

  const password = watch("password");

  async function onSubmit(values) {
    try {
      const session = await registerUser({
        nombre: values.nombre,
        email: values.email,
        password: values.password,
      });

      if (session.accessToken) {
        toast.success("Cuenta creada.");
        navigate("/app", { replace: true });
        return;
      }

      toast.success("Cuenta creada. Inicia sesion.");
      navigate("/login", { replace: true, state: { registered: true } });
    } catch (error) {
      toast.error(getAuthErrorMessage(error, "No se ha podido crear la cuenta."));
    }
  }

  return (
    <div className="auth-card">
      <div>
        <p className="text-xs font-semibold uppercase tracking-wide text-muted">Nueva cuenta</p>
        <h2 className="mt-2 text-3xl font-semibold text-primary">Crear cuenta</h2>
        <p className="mt-2 text-sm leading-6 text-secondary">
          Prepara tu espacio para organizar tareas personales y de grupo.
        </p>
      </div>

      <form className="mt-8 grid gap-5" onSubmit={handleSubmit(onSubmit)} noValidate>
        <TextField
          id="name"
          label="Nombre"
          autoComplete="name"
          placeholder="Tu nombre"
          error={errors.nombre}
          register={register("nombre", {
            required: "El nombre es obligatorio.",
          })}
        />

        <TextField
          id="email"
          label="Email"
          type="email"
          autoComplete="email"
          placeholder="tu@email.com"
          error={errors.email}
          register={register("email", {
            required: "El email es obligatorio.",
          })}
        />

        <PasswordField
          id="password"
          label="Contrasena"
          autoComplete="new-password"
          error={errors.password}
          register={register("password", {
            required: "La contrasena es obligatoria.",
          })}
        />

        <PasswordStrengthMeter password={password} />

        <PasswordField
          id="confirmPassword"
          label="Repetir contrasena"
          autoComplete="new-password"
          placeholder="Repite tu contrasena"
          error={errors.confirmPassword}
          register={register("confirmPassword", {
            required: "Repite la contrasena.",
            validate: (value) => value === password || "Las contrasenas no coinciden.",
          })}
        />

        <p className="rounded-control border border-app bg-[color:var(--state-inactive-bg)] px-3 py-2 text-xs leading-5 text-muted">
          Recomendacion: usa al menos 8 caracteres, una mayuscula, una minuscula y un numero.
        </p>

        <Button type="submit" size="lg" className="w-full" disabled={isSubmitting}>
          <UserPlus size={18} />
          {isSubmitting ? "Creando..." : "Crear cuenta"}
        </Button>
      </form>

      <p className="mt-6 text-center text-sm text-secondary">
        Ya tienes cuenta?{" "}
        <Link className="font-semibold text-[color:var(--color-brand)] hover:underline" to="/login">
          Iniciar sesion
        </Link>
      </p>
    </div>
  );
}
