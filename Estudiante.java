package model;
import model.enums.Rol;

/**
 * 
 * Estudiante
 * El estudiante hereda lo de UsuarioAcademico
 */

public class Estudiante extends UsuarioAcademico{
    /*Same as always :D*/
    private static final long serialVersionUID=1L;

    /**
     * el constructor de estudiante ya con su rol asignado
     * @param codigo codigo estudiante
     * @param nombreCompleto  nombre del estudiante
     * @param clave  clave de acceso
     * @param programa  programa el cual cursa
     * @param semestre  el semestre en el que va
     */
    public Estudiante(String codigo, String nombreCompleto, String clave, String programa, int semestre){
        /*El super de la herencia*/
        super(codigo, nombreCompleto, clave, Rol.ESTUDIANTE, programa, semestre);
    }

    /*Ahora si van los metodos :D
    - Primero calcular penalizacion
    - Luego el del maximo de sesiones
    - Luego el de duracion maxima
    */

    /**
    * Calcula la multa por retraso aplicando el porcentaje configurado para estudiantes.
    *
    * @param horasRetraso cantidad de horas de retraso en la entrega
    * @param valorEquipo  valor del equipo utilizado
    * @param config       los porcentajes vigentes de configuracion de sistema
    * @return monto total de la penalización 
    */
    @Override
    public double calcularPenalizacion(int horasRetraso, double valorEquipo, ConfiguracionSistema config){
        return valorEquipo * (config.getPorcentajeEstudiante() / 100) * horasRetraso;
    }
    
    /**
     * Obtiene la cantidad máxima de sesiones programadas permitidas para un estudiante.
     *
     * @param config de la configuracion del sistema saca los límites vigentes
     * @return número máximo de sesiones permitidas
     */
    @Override
    public int getMaxSesiones(ConfiguracionSistema config){
        return config.getMaxSesionesEstudiante();
    }
    
     /**
     * Obtiene la duración máxima permitida para una sesión reservada por un estudiante.
     *
     * @param config límites vigentes sacados de configuracion del sistema.
     * @return duración máxima en minutos
     */
    @Override
    public int getDuracionMaxima(ConfiguracionSistema config){
        return config.getDuracionMaxEstudiante();
    }
}

