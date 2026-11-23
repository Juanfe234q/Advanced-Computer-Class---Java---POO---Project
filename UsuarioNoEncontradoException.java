package exceptions;

/*Esta se manda cuando no existe un usuario con ese codigo*/
public class UsuarioNoEncontradoException extends LaboratorioException{

    public UsuarioNoEncontradoException(){
        super(" Ese codigo no esta :(");
    }

    public UsuarioNoEncontradoException(String mensaje){
        super(mensaje);
    }
}
