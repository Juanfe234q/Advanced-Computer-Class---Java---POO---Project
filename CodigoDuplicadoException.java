package exceptions;

/*Lo mandamos cuando se registra un usuario equipo o sesion con un codigo que ya existe*/
public class CodigoDuplicadoException extends LaboratorioException{

    public CodigoDuplicadoException(){
        super("Ese codigo ya existe sorry");
    }

    public CodigoDuplicadoException(String mensaje){
        super(mensaje);
    }
}
