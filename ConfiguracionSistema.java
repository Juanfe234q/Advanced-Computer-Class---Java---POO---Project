package model;

import exceptions.ParametroInvalidoException;
import java.io.Serializable;


/**
 * ConfiguracionSistema
 * La configuracion del sistema guarda todo los numeros que el admin puede cambiar
Nota importante chicos: las duraciones van en minutos y los porcentajes son por cada hora de atraso
 */
public class ConfiguracionSistema implements Serializable {
    private static final long serialVersionUID = 1L;

    /*Aqui valores con los que arranca el sistema*/
    public static final double PORCENTAJE_ESTUDIANTE_INICIAL = 1.0;
    public static final double PORCENTAJE_MONITOR_INICIAL = 0.5;
    public static final int MAX_SESIONES_ESTUDIANTE_INICIAL = 2;
    public static final int MAX_SESIONES_MONITOR_INICIAL = 4;
    public static final int DURACION_MIN_INICIAL = 30;
    public static final int DURACION_MAX_ESTUDIANTE_INICIAL = 180;
    public static final int DURACION_MAX_MONITOR_INICIAL = 240;
    public static final double UMBRAL_SUSPENSION_INICIAL = 500000;
    public static final int MAX_INTENTOS_INICIAL = 3;

    private double porcentajeEstudiante;
    private double porcentajeMonitor;
    private int maxSesionesEstudiante;
    private int maxSesionesMonitor;
    private int duracionMinima;
    private int duracionMaxEstudiante;
    private int duracionMaxMonitor;
    private double umbralSuspension;
    private int maxIntentosLogin;

    /*--------------------------------------------------------------------------------------------------------------------------------*/
    
    /**
     * Ahora vamos con nuestro primer metodo:
     Crear la configuracion con valores iniciales
     */
    public ConfiguracionSistema(){
        porcentajeEstudiante=PORCENTAJE_ESTUDIANTE_INICIAL;
        porcentajeMonitor=PORCENTAJE_MONITOR_INICIAL;
        maxSesionesEstudiante=MAX_SESIONES_ESTUDIANTE_INICIAL;
        maxSesionesMonitor=MAX_SESIONES_MONITOR_INICIAL;
        duracionMinima=DURACION_MIN_INICIAL;
        duracionMaxEstudiante=DURACION_MAX_ESTUDIANTE_INICIAL;
        duracionMaxMonitor=DURACION_MAX_MONITOR_INICIAL;
        umbralSuspension=UMBRAL_SUSPENSION_INICIAL;
        maxIntentosLogin=MAX_INTENTOS_INICIAL;
    }

    /*----------------------------------------------------------------------------------------------------------------------------------------*/
    /*Ahora los getters para que otras partes del sistema los usen*/

    public double getPorcentajeEstudiante(){
        return porcentajeEstudiante;
    }
    public double getPorcentajeMonitor(){
        return porcentajeMonitor;
    }
    public int getMaxSesionesEstudiante(){
        return maxSesionesEstudiante;
    }
    public int getMaxSesionesMonitor(){
        return maxSesionesMonitor;
    }
    public int getDuracionMinima(){
        return duracionMinima;
    }
    public int getDuracionMaxEstudiante(){
        return duracionMaxEstudiante;
    }
    public int getDuracionMaxMonitor(){
        return duracionMaxMonitor;
    }
    public double getUmbralSuspension(){
        return umbralSuspension;
    }
    public int getMaxIntentosLogin(){
        return maxIntentosLogin;
    }
    /*------------------------------------------------------------------------------------------------------------------------------------*/
    /*Los setters que en este caso son la unica forma de cambiar los valores fuera de la clase y los usara el admin cuando
    quiera cambiarlos*/

    /**
     * Penalizacion para estudiantes
     * @param p  porcentaje que debe estar de 0 y 100
     * @throws ParametroInvalidoException  si es porcentaje no esta en el rango ya establecido
     */
    public void setPorcentajeEstudiante(double p) throws ParametroInvalidoException{
        if(p<0 || p>100){
            throw new ParametroInvalidoException("El porcentaje tiene que estar entre 0 y 100 :O");
        }
        porcentajeEstudiante=p;
    }

    /**
     * Penalizacion para monitores
     * @param p  porcentaje que debe estar de 0 y 100
     * @throws ParametroInvalidoException   si es porcentaje no esta en el rango ya establecido
     */
    public void setPorcentajeMonitor(double p) throws ParametroInvalidoException{
        if(p<0 || p>100){
            throw new ParametroInvalidoException("El porcentaje tiene que estar entre 0 y 100 :0");
        }
        porcentajeMonitor=p;
    }

    /**
     * Sesiones maximas simulataneas.
     * @param max  cantidad maxima, minima 1.
     * @throws ParametroInvalidoException si el valor es menor que 1.
     */
    public void setMaxSesionesEstudiante(int max) throws ParametroInvalidoException{
        if(max<1){
            throw new ParametroInvalidoException("Tiene que poder programar al menos 1 sesion");
        }
        maxSesionesEstudiante=max;
    }

    /**
     * Cantidad de sesiones maximas simultaneas de un monitor
     * @param max cantidad maxima de sesiones
     * @throws ParametroInvalidoException  si el valor es menor que 1
     */
    public void setMaxSesionesMonitor(int max) throws ParametroInvalidoException{
        if(max<1){
            throw new ParametroInvalidoException("Tiene que poder programar al menos 1 sesion");
        }
        maxSesionesMonitor=max;
    }

    /**
     * duracion minima de cada sesion
     * @param min  duracion minima en minutos, no debe ser mayor que la maxima y debe ser positiva
     * @throws ParametroInvalidoException si alguna de estas dos condiciones no se cumple
     */
    public void setDuracionMinima(int min) throws ParametroInvalidoException{
        if(min<1 || min>duracionMaxEstudiante || min>duracionMaxMonitor){
            throw new ParametroInvalidoException("La duracion minima tiene que ser positiva y menor que la max :O");
        }
        duracionMinima=min;
    }

    /**
     * Establece la duracion maxima para las sesiones de estudiantes.
     * @param max la duracion maxima.
     * @throws ParametroInvalidoException si llega a ser menor a la duracion minima.
     */
    public void setDuracionMaxEstudiante(int max) throws ParametroInvalidoException{
        if(max<duracionMinima){
            throw new ParametroInvalidoException("La duracion maxima no puede ser menor que la minima :D");
        }
        duracionMaxEstudiante=max;
    }

    /**
     * Establece la duracion maxima para las sesiones de monitores 
     * @param max
     * @throws ParametroInvalidoException
     */
    public void setDuracionMaxMonitor(int max) throws ParametroInvalidoException{
        if(max<duracionMinima){
            throw new ParametroInvalidoException("La duracion maxima no puede ser menor que la minima :D");
        }
        duracionMaxMonitor=max;
    }
   
    /**
     * Monto de multas para suspendera un usuario
     * @param umbral  Representa el monto
     * @throws ParametroInvalidoException  Si el monto es 0 o menor que 1.
     */
    public void setUmbralSuspension(double umbral) throws ParametroInvalidoException{
        if(umbral<=0){
            throw new ParametroInvalidoException("El umbral tiene que ser mayor que 0! :O");
        }
        umbralSuspension=umbral;
    }

    /**
     * Numero de intentos de inicio de sesion maximos
     * @param max cantidad de intentos >1
     * @throws ParametroInvalidoException si el maximo ingresado es menor que 1
     */
    public void setMaxIntentosLogin(int max) throws ParametroInvalidoException{
        if(max<1){
            throw new ParametroInvalidoException("Tiene que haber al menos 1 intento :O");
        }
        maxIntentosLogin=max;
    }
}