package model.valueobjects;

import model.enums.NivelEscolar;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Curso en formato chileno: nivel (7° Básico a 4° Medio) + letra (A a H). Ej: "1° Medio C". */
public class Curso {

    public static final char PRIMERA_LETRA = 'A';
    public static final char ULTIMA_LETRA = 'H'; // 8 cursos por nivel

    private final NivelEscolar nivel;
    private final char letra;

    public Curso(NivelEscolar nivel, char letra) {
        this.nivel = Objects.requireNonNull(nivel, "El nivel es obligatorio");
        char mayuscula = Character.toUpperCase(letra);
        if (mayuscula < PRIMERA_LETRA || mayuscula > ULTIMA_LETRA) {
            throw new IllegalArgumentException("La letra del curso debe estar entre A y H");
        }
        this.letra = mayuscula;
    }

    /** Los 48 cursos del liceo (6 niveles x 8 letras). Útil para un JComboBox. */
    public static List<Curso> todos() {
        List<Curso> cursos = new ArrayList<>();
        for (NivelEscolar nivel : NivelEscolar.values()) {
            for (char l = PRIMERA_LETRA; l <= ULTIMA_LETRA; l++) {
                cursos.add(new Curso(nivel, l));
            }
        }
        return cursos;
    }

    public NivelEscolar getNivel() { return nivel; }

    public char getLetra() { return letra; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Curso)) return false;
        Curso otro = (Curso) o;
        return nivel == otro.nivel && letra == otro.letra;
    }

    @Override
    public int hashCode() { return Objects.hash(nivel, letra); }

    @Override
    public String toString() { return nivel.getEtiqueta() + " " + letra; }
}