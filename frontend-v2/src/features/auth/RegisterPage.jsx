import AuthLayout from "./AuthLayout.jsx";
import RegisterForm from "./RegisterForm.jsx";

export default function RegisterPage() {
  return (
    <AuthLayout variant="register">
      <RegisterForm />
    </AuthLayout>
  );
}
