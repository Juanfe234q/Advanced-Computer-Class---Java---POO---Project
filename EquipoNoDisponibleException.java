package exceptions;

/*Esta se manda cuando el equipo no esta disponible*/
public class EquipoNoDisponibleException extends LaboratorioException{

    public EquipoNoDisponibleException(){
        super("El equipo no esta disponible :(");
    }

    public EquipoNoDisponibleException(String mensaje){
        super(mensaje);
    }
}
