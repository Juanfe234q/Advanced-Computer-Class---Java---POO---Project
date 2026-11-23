package exceptions;

/*Esta se manda cuando se intenta cancelar o cerrar una sesion pss ya cerrada*/

public class EstadoSesionInvalidoException extends LaboratorioException{

    public EstadoSesionInvalidoException(){
        super("La sesion ya no esta...se fue...");
    }

    public EstadoSesionInvalidoException(String mensaje){
        super(mensaje);
    }
}
