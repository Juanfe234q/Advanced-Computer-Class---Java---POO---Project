package model.enums;

/**
 * OpcionMenu
 * Esto es para hacernos la vida más fácil: Son las 15 opciones del menu principal
 */
public enum OpcionMenu{

    /* Va asi: numero-texto-estudiante-monitor-administrador
    Porque es asi?
    La verdad es para hacernos la vida más fácil, asi no toca hacer un menu por usuario, a los 3 tienen el mismo menu
    pero al estudiante por ejemplo no se le permite GESTION_ADMINISTRACION

    luego esto sirve para que con el constructor se pueda verificar el permite o no y luego que el switch al momento del menu sea facilisimo*/
    AUTENTICACION(1, "Iniciar o cerrar sesion",                    true,  true,  true),
    REGISTRAR_EQUIPO(2, "Registrar equipo por consola",            false, false, true),
    REGISTRAR_USUARIO(3, "Registrar usuario por consola",          false, false, true),
    CARGAR_EQUIPOS(4, "Cargar equipos desde archivo",              false, false, true),
    CARGAR_USUARIOS(5, "Cargar usuarios desde archivo",            false, false, true),
    GESTION_ADMINISTRACION(6, "Gestion de administracion",         false, false, true),
    ESTADO_LABORATORIO(7, "Estado de laboratorio e inventario",    true,  true,  true),
    PROGRAMAR_SESION(8, "Programar sesion de uso",                 true,  true,  true),
    CANCELAR_SESION(9, "Cancelar sesion",                          true,  true,  true),
    CERRAR_SESION_USO(10, "Cerrar sesion de uso :V",                  true,  true,  true),
    HISTORIAL_SESIONES(11, "Historial de sesiones...",                true,  true,  true),
    INFORMES(12, "Informes y estadisticas!",                        false, true,  true),
    AUDITORIA(13, "Auditoria y exportacion :O",                       false, false, true),
    GUARDAR_RECUPERAR(14, "Guardar o recuperar estado",            false, false, true),
    SALIR(15, "Salir -> byee :D",                                             true,  true,  true);

    private final int numero;
    private final String texto;
    private final boolean permiteEstudiante;
    private final boolean permiteMonitor;
    private final boolean permiteAdministrador;

    /*Ahora el constructor del Menu, so:*/
    /**
     * 
     * @param numero La opccion que elige el usuario.
     * @param texto  Muestra el menu.
     * @param permiteEstudiante Valida si el estudiante tiene acceso.
     * @param permiteMonitor  Valida si el monitor tiene acceso.
     * @param permiteAdministrador  Valida si el admin tiene acceso.
     */
    OpcionMenu(int numero, String texto, boolean permiteEstudiante, boolean permiteMonitor, boolean permiteAdministrador){
        this.numero=numero;
        this.texto=texto;
        this.permiteEstudiante=permiteEstudiante;
        this.permiteMonitor=permiteMonitor;
        this.permiteAdministrador=permiteAdministrador;
    }

    /**
     * 
     * @return retorna la opccion elegida en menu
     */
    public int getNumero(){
        return numero;
    }
    
    /**
     * 
     * @return  retorna el texto de la opccion elegida.
     */
    public String getTexto(){
        return texto;
    }

    /**
     * Aqui un metodo para verificar si permite que use o no una opcion
     * @param rol  este es el rol con el que se intenta acceder
     * @return  retorna true si tiene acceso o false si no lo tiene.
     */
    public boolean permiteRol(Rol rol){

        /*usamos un switch por si en algun caso agregen un nuevo rol, 
        no lo trate como un caso por defecto */
        switch(rol){
        case ESTUDIANTE:
            return permiteEstudiante;
        case MONITOR:
            return permiteMonitor;
        case ADMINISTRADOR:
            return permiteAdministrador;
        default:
            return false;
        }
    }

    /**
     * Aqui para buscar la opción que corresponde al numero que escribio el usuario
      Basicamente es un traductor de numero a opcion
     La logica es esta:
     El for-each Busca la opcion que corresponde al numero que escribio el usuario.
     y devuelve la opción encontrada

     * @param numero el numero seleccionado por el usuario.
     * @return  retorna la opccion de menu que coincida o null si el numero no existe.
     */
    public static OpcionMenu desdeNumero(int numero){
        for(OpcionMenu o : values()){
            if(o.numero==numero){
                return o;
            }
        }
        return null;
    }
}
