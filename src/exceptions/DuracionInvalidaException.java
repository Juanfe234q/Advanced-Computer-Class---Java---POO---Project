package exceptions;

/*Esta se manda cuando la duracion de la sesion no es valida*/
public class DuracionInvalidaException extends LaboratorioException{

    public DuracionInvalidaException(){
        super("La duracion de la sesion no es valida :O");
    }

    public DuracionInvalidaException(String mensaje){
        super(mensaje);
    }
}
