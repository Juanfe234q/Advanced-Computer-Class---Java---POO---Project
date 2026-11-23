package ui;

import java.util.Scanner;

public class LectorConsola {
       
    private Scanner entrada;

    /* Recibimos el Scanner principal para reutilizar la misma entrada */
    public LectorConsola(Scanner entrada) {
        this.entrada = entrada;
    }

    /* Solicitamos un número y repetimos la lectura hasta recibir uno válido */
    public int leerEntero(String mensaje) {

        while (true) {

            System.out.print(mensaje);
            String dato = entrada.nextLine();

            try {
                return Integer.parseInt(dato.trim());

            } catch (NumberFormatException e) {
                System.out.println("Debes ingresar un numero porfa :).");
            }
        }
    }

    /* Leemos textos como nombres, códigos y contraseñas */
    public String leerTexto(String mensaje) {

        System.out.print(mensaje);
        return entrada.nextLine();
    }
}
