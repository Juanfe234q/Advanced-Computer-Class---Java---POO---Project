package ui;

import model.enums.OpcionMenu;
import model.enums.Rol;
import service.ServicioAutenticacion;

public class MenuPrincipal {

    //Primero declaramos los atributos de esta clasesita :)
    private ServicioAutenticacion auth;
    private LectorConsola lector;

    /* Hacemos el constructor de siempre :) */
    public MenuPrincipal(ServicioAutenticacion auth, LectorConsola lector) {
        this.auth = auth;
        this.lector = lector;
    }

    /*Y ahora si, iniciamos con los metodoss.

    Mostramos únicamente las opciones permitidas para el usuario actual */
    public void mostrarMenu() {

        System.out.println("\nMENU PRINCIPAL");

        if (!auth.hayUsuarioAutenticado()) {

            System.out.println("1. Iniciar sesion");
            System.out.println("15. Salir");

        } else {

            Rol rolActual = auth.getUsuarioActual().getRol();

            for (OpcionMenu opcion : OpcionMenu.values()) {
            /*Usamos opcionMenu.values para devolver las constantes del enum, y asi poder usar el for each :) */
                if (opcion.permiteRol(rolActual)) {
                    System.out.println(
                            opcion.getNumero() + ". " + opcion.getTexto()
                    );
                }
            }
        }
    }

    /* Convertimos el número que pusieron en una opción y verificamos
       que exista y que el usuario pueda utilizarla */
    public OpcionMenu leerOpcion() {

        try {
            int numero = lector.leerEntero("Seleccione una opcion: ");

            OpcionMenu opcion = OpcionMenu.desdeNumero(numero);

            if (opcion == null) {
                System.out.println("La opcion ingresada no existe.");
                return null;
            }

            if (!auth.hayUsuarioAutenticado()) {

                if (opcion != OpcionMenu.AUTENTICACION
                        && opcion != OpcionMenu.SALIR) {

                    System.out.println("Primero debes iniciar sesion.");
                    return null;
                }

            } else {

                Rol rolActual = auth.getUsuarioActual().getRol();

                if (!opcion.permiteRol(rolActual)) {
                    System.out.println("No tienes permiso para esta opcion.");
                    return null;
                }
            }

            return opcion;

        } catch (NumberFormatException e) {
            System.out.println("Debes ingresar un numero valido.");
            return null;
        }
    }
}
