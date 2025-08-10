import { useEffect, useState } from "react";
import BaseSelect from "./BaseSelect";
import { getCategorias } from "../../services/categoriaService";

/**
 * Selector de categorías desde backend usando BaseSelect centralizado.
 */
export default function SelectCategoria({ id, value, onChange, required = false, className = "" }) {
    const [categorias, setCategorias] = useState([]);

    useEffect(() => {
        let alive = true;
        getCategorias()
            .then((data) => alive && setCategorias(Array.isArray(data) ? data : []))
            .catch((e) => {
                console.error("Error cargando categorías", e);
                if (alive) setCategorias([]);
            });
        return () => {
            alive = false;
        };
    }, []);

    return (
        <BaseSelect
            id={id}
            value={value}
            onChange={onChange}
            required={required}
            className={className}
            data-role="categoria-select"
        >
            <option value="" disabled>Sin categoría</option>
            {categorias.map((c) => (
                <option key={c.idCategoria} value={c.idCategoria}>
                    {c.nombre}
                </option>
            ))}
        </BaseSelect>
    );
}
