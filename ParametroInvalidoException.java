package exceptions;

/*Esta se manda cuando un parametro de configuracion tiene un valor crazy nada que ver*/
public class ParametroInvalidoException extends LaboratorioException{

    public ParametroInvalidoException(){
        super("El valor del parametro esta como chistosito");
    }

    public ParametroInvalidoException(String mensaje){
        super(mensaje);
    }
}
