package exceptions;

/*Esta se manda cuando ya hay usuario logeado y otro intenta logearse*/
public class SesionYaIniciadaException extends LaboratorioException{

    public SesionYaIniciadaException(){
        super("Ya hay un usuario Logeado :V");
    }

    public SesionYaIniciadaException(String mensaje){
        super(mensaje);
    }
}
