/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import java.time.LocalTime;

/**
 * Paciente en la sala de emergencias.
 *
 * El triaje sigue la escala de Manchester: 1 es lo más urgente y 5 lo menos.
 *
 * @author ASUS
 */
public class Paciente {

    private final String codigo;
    private final String nombre;
    private final int edad;
    private final int triaje;
    private final LocalTime llegada;

    public Paciente(String codigo, String nombre, int edad, int triaje, LocalTime llegada) {
        if (triaje < 1 || triaje > 5) {
            throw new IllegalArgumentException("El triaje debe estar entre 1 y 5");
        }
        this.codigo = codigo;
        this.nombre = nombre;
        this.edad = edad;
        this.triaje = triaje;
        this.llegada = llegada;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public int getEdad() {
        return edad;
    }

    public int getTriaje() {
        return triaje;
    }

    public LocalTime getLlegada() {
        return llegada;
    }

    /** Nombre del nivel de triaje (escala de Manchester). */
    public static String nombreTriaje(int triaje) {
        return switch (triaje) {
            case 1 -> "Resucitación";
            case 2 -> "Emergencia";
            case 3 -> "Urgente";
            case 4 -> "Menos urgente";
            default -> "No urgente";
        };
    }

    @Override
    public String toString() {
        return codigo + " " + nombre + " (triaje " + triaje + ")";
    }
}
