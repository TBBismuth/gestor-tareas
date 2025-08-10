import BaseSelect from "./BaseSelect";

/**
 * Selector de prioridad (BAJA, MEDIA, ALTA, IMPRESCINDIBLE) usando BaseSelect centralizado.
 */
export default function SelectPrioridad({ id, value, onChange, required = false, className = "" }) {
    return (
        <BaseSelect
            id={id}
            value={value}
            onChange={onChange}
            required={required}
            className={className}
            data-role="prioridad-select"
        >
            <option value="" disabled>Selecciona prioridad</option>
            <option value="BAJA">Baja</option>
            <option value="MEDIA">Media</option>
            <option value="ALTA">Alta</option>
            <option value="IMPRESCINDIBLE">Imprescindible</option>
        </BaseSelect>
    );
}
