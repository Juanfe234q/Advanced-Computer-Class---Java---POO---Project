package exceptions;

/* Este lo mandamos cuando hay otra sesion del mismo equiipo en ese horario*/
public class CruceHorarioException extends LaboratorioException{

    public CruceHorarioException(){
        super("El equipo ya tiene una sesion en ese horario");
    }

    public CruceHorarioException(String mensaje){
        super(mensaje);
    }
}
