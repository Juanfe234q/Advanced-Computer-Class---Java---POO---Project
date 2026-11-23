package exceptions;

/*Esta se manda cuando no existe un equipo con ese codigo*/
public class EquipoNoEncontradoException extends LaboratorioException{

    public EquipoNoEncontradoException(){
        super("No existe un equipo con ese codigo sorry");
    }

    public EquipoNoEncontradoException(String mensaje){
        super(mensaje);
    }
}
