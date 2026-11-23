package exceptions;

/*Este se manda cuando se intenta usar una opcion sin logearse*/
public class UsuarioNoAutenticadoException extends LaboratorioException{

    public UsuarioNoAutenticadoException(){
        super("Recuerda primero iniciar sesion :D");
    }

    public UsuarioNoAutenticadoException(String mensaje){
        super(mensaje);
    }
}
