package Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Genera las rotaciones de guardia (permutaciones) de un equipo de doctores de
 * forma recursiva: se fija un doctor para el turno actual y se ordenan
 * recursivamente los restantes en los turnos siguientes.
 *
 * Como n! crece muy rápido, se detiene al llegar a un límite de opciones.
 */
public final class RotacionGuardias {

    private RotacionGuardias() {
    }

    /** Devuelve hasta {@code limite} rotaciones, en orden (Opción 1 = orden original). */
    public static List<List<String>> generar(List<String> doctores, int limite) {
        List<List<String>> opciones = new ArrayList<>();
        permutar(new ArrayList<>(), new ArrayList<>(doctores), opciones, limite);
        return opciones;
    }

    private static void permutar(List<String> asignados, List<String> libres, List<List<String>> opciones, int limite) {
        if (opciones.size() >= limite) {
            return;
        }
        if (libres.isEmpty()) {                         // caso base: todos los turnos cubiertos
            opciones.add(List.copyOf(asignados));
            return;
        }
        for (int i = 0; i < libres.size(); i++) {
            String doctor = libres.remove(i);           // este doctor toma el turno actual
            asignados.add(doctor);
            permutar(asignados, libres, opciones, limite);
            asignados.remove(asignados.size() - 1);     // deshace la elección
            libres.add(i, doctor);
        }
    }
}
