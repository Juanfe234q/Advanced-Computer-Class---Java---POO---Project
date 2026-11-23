package model;

/* Entonces todo usuario que puede programar sesiones
   - puede recibir multas
   */



/**
 * 
 * Penalizable
 * Digamos que son las reglas que tiene un laboratorio, multas y horas maximas o minimas.
 */
public interface Penalizable{
    /*So, los metodos que van a haber son
    - Calcular la penalizacion por entregar tarde el equipo
    - Cuantas sesiones max puede tener
    - Cuanta es la duracion max de la sesion que puede tener
     */

    /*El configuracion del sistema config va pq literal es los parametros de
    la configuracion del sistema*/

    
    /**
     * Calcular el valor de la multa
     * @param horasRetraso
     * @param valorEquipo
     * @param config
     * @return monto de la multa
     */
    double calcularPenalizacion(int horasRetraso, double valorEquipo, ConfiguracionSistema config);

    /**
     * Obtiene el numero de sesiones maximas que un usuario puede tener
     * @param config
     * @return numero de sesiones
     */
    int getMaxSesiones(ConfiguracionSistema config);

    /**
     * Obtiene el tiempo maximo para una sesion
     * @param config
     * @return la duracion maxima
     */
    int getDuracionMaxima(ConfiguracionSistema config);
}
