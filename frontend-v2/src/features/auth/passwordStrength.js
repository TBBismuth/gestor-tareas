export function getPasswordStrength(password = "") {
  const hasMinLength = password.length >= 8;
  const hasLowercase = /[a-z]/.test(password);
  const hasUppercase = /[A-Z]/.test(password);
  const hasNumber = /\d/.test(password);
  const hasSymbol = /[^A-Za-z0-9]/.test(password);
  const numbersCount = (password.match(/\d/g) || []).length;
  const uniqueChars = new Set(password).size;

  const missing = [];
  if (!hasMinLength) missing.push("Anade al menos 8 caracteres");
  if (!hasLowercase) missing.push("Anade una minuscula");
  if (!hasUppercase) missing.push("Anade una mayuscula");
  if (!hasNumber) missing.push("Anade un numero");

  if (missing.length > 0) {
    return {
      score: 0,
      level: "weak",
      label: "Débil",
      hint: missing[0],
    };
  }

  const extraScore = [
    password.length >= 10,
    password.length >= 12,
    numbersCount >= 2,
    uniqueChars >= 8,
  ].filter(Boolean).length;

  if (hasSymbol && extraScore >= 2) {
    return {
      score: 5,
      level: "strong",
      label: "Fuerte",
      hint: "Buena combinacion",
    };
  }

  return {
    score: 3,
    level: "medium",
    label: "Media",
    hint: hasSymbol
      ? "Anade longitud o variedad para reforzarla"
      : "Anade un simbolo para una contrasena fuerte",
  };
}
