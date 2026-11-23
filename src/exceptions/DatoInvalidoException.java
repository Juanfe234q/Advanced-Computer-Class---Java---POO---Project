package exceptions;

/*Esta se manda cuando un dato tiene formato o rango incorrecto*/
public class DatoInvalidoException extends LaboratorioException{

    public DatoInvalidoException(){
        super("El dato ingresado esta mal");
    }

    public DatoInvalidoException(String mensaje){
        super(mensaje);
    }
}
