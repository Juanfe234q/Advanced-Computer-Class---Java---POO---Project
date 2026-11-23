package model;
import model.enums.Rol;
/*Same, se hereda de UsuarioAcademico*/

/**
 * 
 * Monitor
 * Ahora vamos con monitor basicamente la misma estructura de Estudiante
*/
public class Monitor extends UsuarioAcademico{
    /*Same as always*/
    private static final long serialVersionUID=1L;

    /**
     * El rol tambien siempre sera monitor asi que tampoco
    va por parametro
     * @param codigo codigo monitor
     * @param nombreCompleto  nombre del monitor
     * @param clave  clave de acceso del monitor
     * @param programa  porgrama al cual pertenece
     * @param semestre  semestre del monitor
     */
    public Monitor(String codigo, String nombreCompleto, String clave, String programa, int semestre){
        super(codigo, nombreCompleto, clave, Rol.MONITOR, programa, semestre);
    }

    /*Y igual tambien van los metodos
    - Primero calcular penalizacion
    - Luego el del maximo de sesiones
    - Luego el de duracion maxima
    */


    /**
     * Calcula la multa aplicando el descuento para monitores.
     *
     * @param horasRetraso cantidad de horas de retraso 
     * @param valorEquipo  valor del equipo 
     * @param config        porcentajes de la configuracion del sistema
     * @return   monto total de la multa
     */
    @Override
    public double calcularPenalizacion(int horasRetraso, double valorEquipo, ConfiguracionSistema config){
        return valorEquipo * (config.getPorcentajeMonitor() / 100) * horasRetraso;
    }

     /**
     * Cantidad maxima de sesiones permitidas para un monitor.
     *
     * @param config limites de configuracion del sistema
     * @return numero de sesiones permitidas
     */
    @Override
    public int getMaxSesiones(ConfiguracionSistema config){
        return config.getMaxSesionesMonitor();
    }

    /**
     * Obtiene la duracion maxima permitida para una sesion de monitor.
     *
     * @param config saca el limite de configuracion del sistema
     * @return duracion maxima permitida
     */
    @Override
    public int getDuracionMaxima(ConfiguracionSistema config){
        return config.getDuracionMaxMonitor();
    }
}