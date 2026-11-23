package model.enums;

/**
 * 
 * EstadoEquipo     Estados operativos de un equipo de laboratorio
 */
public enum EstadoEquipo{
    DISPONIBLE,
    EN_USO,
    EN_MANTENIMIENTO,
    DADO_DE_BAJA;


/**
 * Nota Esto nos sirve para mas tarde verificar el uso por eso el this
aqui es super importante
 * Verificamos si con el estado actual podemos iniciar una sesion.
 * @return  retorna un true si el equipo esta disponible o en uso, retorna false de lo contario.
 */
    public boolean permiteUso(){
        if(this==DISPONIBLE || this==EN_USO){
            return true;
        }
        return false;
    }
}

