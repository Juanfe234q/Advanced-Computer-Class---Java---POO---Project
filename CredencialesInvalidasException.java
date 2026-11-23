package exceptions;

/*Este lo mandamos cuando el codigo no existe o la clave esta mal*/
public class CredencialesInvalidasException extends LaboratorioException{

    public CredencialesInvalidasException(){
        super("Codigo o clave incorrectos :O");
    }

    public CredencialesInvalidasException(String mensaje){
        super(mensaje);
    }
}
