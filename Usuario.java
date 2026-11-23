package model;

import model.enums.EstadoUsuario;
import model.enums.OpcionMenu;
import model.enums.Rol;

import java.io.Serializable;

/**
 * 
 * Usuario
 * - 1. Pq es abstracta?
Basicamente porque en el sistema nunca existe un solo usuario a secas
Entonces lo comun va en el usuario:
- Tiene un codigo
- Tiene un nombre
- Tiene una clave
- Tiene un rol
- Tiene un estado

y luego ya cada una se implementa lo que la hace diferente
 */
public abstract class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;
    private String codigo;
    private String nombreCompleto;
    private String clave;
    private Rol rol;
    private EstadoUsuario estado;
    private int intentosfallidos;


    /**
     * Constructor de un usuario
     * @param codigo  codigo del usuario
     * @param nombreCompleto nombre del usuario
     * @param clave clave del usuario
     * @param rol  rol dentro del programa
     */
    protected Usuario(String codigo, String nombreCompleto, String clave, Rol rol) {
        this.codigo = codigo;
        this.nombreCompleto = nombreCompleto;
        this.clave = clave;
        this.rol = rol;
        /*Notas importantes:
    Todo usuario nuevo empieza activo y no tendra intentos fallidos
     */
        this.estado = EstadoUsuario.ACTIVO;
        this.intentosfallidos = 0;
    }

    /*Este es un metodo abstracto vacio, notita importante!
    Y cual es su funcion si esta vacio?
    basicamente garantiza que todos los "hijos" o sea cada tipo de usuario
    lo tenga e impleemnte

    Y asi se pueden usar todos :D
    */

    /**
     * Genera una linea de texto con la informacion del usuario
     * @return texto con los datos
     */
    public abstract String obtenerInfo();

    
    /**
     * La logica de antes con el return clave.equals es como un if, retorna true si
    coinciden, es como un if metidito ahi
     * @param claveIngresada Contraseña ingresada
     * @return true si la clave coincide o false si no
     */
    public boolean verificarClave(String claveIngresada){
        return clave != null && clave.equals(claveIngresada);
    }

    /**
     * Comprueba si el usuario esta habilitado
     * @return true si esta activo, false si no
     */
    public boolean estaActivo(){
        return estado == EstadoUsuario.ACTIVO;
    }

    /*Aqui muchachos se complementa la logica del usuario con lo de
    configuracion del sistema de si el usuario puede acceder a algo del menu y el mismo menu */

    /**
     * Determina si el usuario tiene acceso a una opccion del menu
     * @param opcion elegida en el menu
     * @return true si tiene acceso, false si no
     */
    public boolean puedeAcceder(OpcionMenu opcion){
        return opcion.permiteRol(rol);
    }

    /*Ahora vamos con los intentos de Logearse*/


       
    public void registrarIntentoFallido(){
        intentosfallidos++;
    }

    public void reiniciarIntentos(){
        intentosfallidos = 0;
    }

    /*Ahora los getters :D*/

    public String getCodigo(){
        return codigo;
    }

    public String getNombreCompleto(){
        return nombreCompleto;
    }

    public Rol getRol(){
        return rol;
    }

    public String getClave() {
        return clave;
    }

    public EstadoUsuario getEstado(){
        return estado;
    }

    public int getIntentosFallidos(){
        return intentosfallidos;
    }

    /*Y otra vez los setters que puede cambiar el admin :D*/

    public void setNombreCompleto(String nombreCompleto){
        this.nombreCompleto = nombreCompleto;
    }

    public void setClave(String clave){
        this.clave = clave;
    }

    public void setEstado(EstadoUsuario estado){
        this.estado = estado;
    }

}

