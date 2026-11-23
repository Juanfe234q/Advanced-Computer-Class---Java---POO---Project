package exceptions;

/* Esta se manda cuando se intenta eliminar o dar de baja un equipo que tiene sesiones programadas*/
public class EquipoConSesionesProgramadasException extends LaboratorioException{

    public EquipoConSesionesProgramadasException(){
        super("El equipo tiene sesiones programadas y no puedes hacerle nada de modificaciones aun :D");
    }

    public EquipoConSesionesProgramadasException(String mensaje){
        super(mensaje);
    }
}
