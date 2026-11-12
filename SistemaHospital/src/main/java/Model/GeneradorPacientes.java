package Model;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** Genera pacientes de prueba con datos aleatorios pero realistas. */
public final class GeneradorPacientes {

    private static final String[] NOMBRES = {
        "María", "José", "Ana", "Luis", "Carmen", "Carlos", "Rosa", "Jorge", "Lucía", "Miguel",
        "Elena", "Andrés", "Sofía", "Diego", "Valeria", "Pedro", "Gabriela", "Fernando", "Daniela", "Ricardo"
    };
    private static final String[] APELLIDOS = {
        "Zambrano", "Vera", "Mendoza", "Cedeño", "Moreira", "Macías", "Alcívar", "Loor", "Bravo", "Intriago",
        "Torres", "Pinargote", "Chávez", "Ponce", "Andrade", "Ruiz", "Salazar", "Espinoza", "Murillo", "Castro"
    };
    /** Probabilidad acumulada de cada nivel de triaje (1..5): hay pocos casos críticos. */
    private static final int[] TRIAJE_ACUMULADO = {8, 25, 60, 88, 100};

    private GeneradorPacientes() {
    }

    /**
     * Genera pacientes que llegaron durante las últimas 4 horas (sin pasar de la
     * medianoche), para que el tiempo de espera sea real respecto a la hora actual.
     */
    public static List<Paciente> generar(int cantidad, Random azar) {
        LocalTime ahora = LocalTime.now().withSecond(0).withNano(0);
        int ventana = Math.max(1, Math.min(4 * 60, ahora.getHour() * 60 + ahora.getMinute()));
        List<Paciente> lista = new ArrayList<>(cantidad);
        for (int i = 1; i <= cantidad; i++) {
            String nombre = NOMBRES[azar.nextInt(NOMBRES.length)] + " " + APELLIDOS[azar.nextInt(APELLIDOS.length)];
            int edad = 1 + azar.nextInt(92);
            int triaje = triajeAleatorio(azar);
            LocalTime llegada = ahora.minusMinutes(azar.nextInt(ventana));
            lista.add(new Paciente(String.format("P-%03d", i), nombre, edad, triaje, llegada));
        }
        return lista;
    }

    private static int triajeAleatorio(Random azar) {
        int r = azar.nextInt(100);
        for (int t = 0; t < TRIAJE_ACUMULADO.length; t++) {
            if (r < TRIAJE_ACUMULADO[t]) {
                return t + 1;
            }
        }
        return 5;
    }
}
