package exceptions;

/*Este se manda cuando un usuario suspendido o bloqueado intenta hacer algo*/
public class UsuarioInactivoException extends LaboratorioException{

    public UsuarioInactivoException(){
        super("Bro estas bloqueadisimo, no puedes hacer nadaa :(");
    }

    public UsuarioInactivoException(String mensaje){
        super(mensaje);
    }
}
