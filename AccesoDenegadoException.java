package exceptions;

/*Lo mandamos cuando el rol del usuario no tiene permiso */
public class AccesoDenegadoException extends LaboratorioException{
    public AccesoDenegadoException(){
        super("No tienes permiso para hacer esto :(");
    }
    public AccesoDenegadoException(String mensaje){
        super(mensaje);
    }
}
