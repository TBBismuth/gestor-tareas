import { getPasswordStrength } from "./passwordStrength";

export default function PasswordStrengthMeter({ password }) {
  const strength = getPasswordStrength(password);
  const filledSegments = {
    weak: 1,
    medium: 2,
    strong: 3,
  }[strength.level];

  return (
    <div className="password-strength" data-strength={strength.level}>
      <div className="password-strength-header">
        <span>Seguridad</span>
        <strong>{strength.label}</strong>
      </div>
      <div className="password-strength-track" aria-hidden="true">
        {[0, 1, 2].map((index) => (
          <span
            key={index}
            data-active={index < filledSegments}
          />
        ))}
      </div>
      {strength.hint && <p className="password-strength-hint">{strength.hint}</p>}
    </div>
  );
}
