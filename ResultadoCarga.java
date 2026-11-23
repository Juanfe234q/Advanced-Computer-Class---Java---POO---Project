package persistence;

/**
 * 
 * ResultadoCarga
 * Nos ayuda a saber cuantos registros fueron importados correctamente y cuantos fallaron.
 */
public class ResultadoCarga {
    
    //Primero declaramos los atributos de la clasesita :)
    private int registrosCargados;
    private int registrosConError;

    /**
     * Hacemos el constructor e inicializamos los atributos  
     * @param registrosCargados cantidad de lineas procesadas y registradas exitosamente
     * @param registrosConError cantidad de lineas descartadas por errores 
     */
    public ResultadoCarga(int registrosCargados, int registrosConError) {
        this.registrosCargados = registrosCargados;
        this.registrosConError = registrosConError;
    }

    //Getter y setters necesarios 
    public int getRegistrosCargados() {
        return registrosCargados;
    }

    public int getRegistrosConError() {
        return registrosConError;
    }

    /**
     * Genera un resumen en dos líneas con el total de registros cargados y con error.
     *
     * @return texto con los resultados de la carga
     */
    @Override
    public String toString() {
        return "Registros cargados: " + registrosCargados + "\nRegistros con error: " + registrosConError;
    }

}
