package exceptions;

/*Esta se manda cuando falla el guardar o cargar el estado del sistema en el archivo.dat*/
public class PersistenciaException extends LaboratorioException{

    public PersistenciaException(){
        super("No se pudo guardar o cargar el estado del sistema :(");
    }

    public PersistenciaException(String mensaje){
        super(mensaje);
    }
}
