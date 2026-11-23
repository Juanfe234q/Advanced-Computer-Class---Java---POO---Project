package model;

import model.enums.Rol;

/**
 * 
 * Administrador   
 *  Ahora vamos con el Admin
    Basicamente tiene control total del sistema.

    Primera nota importante: hijo de usuario (herencia)
 */
public class Administrador extends Usuario{

    /*Lo mismo de siempre*/
    private static final long serialVersionUID=1L;

    /**
     * Constructor del Admin:
    - Su rol siempre es ese asi que no va por parametro

     * @param codigo    identificador del admin
     * @param nombreCompleto    nombre del admin
     * @param clave  la clave de acceso al sistema
     */
    public Administrador(String codigo, String nombreCompleto, String clave){
        /*Ahi con el super por herencia de usuario*/
        super(codigo, nombreCompleto, clave, Rol.ADMINISTRADOR);
    }

    /*Y aqui el famoso obtener info entra a jugar :D
    Aqui deja de ser abstracto y va con lo que hace al admin admin*/
    @Override
    public String obtenerInfo(){
        return getCodigo()+" | "+getNombreCompleto()+ " | "+getRol() + " | "+getEstado();
    }
}

