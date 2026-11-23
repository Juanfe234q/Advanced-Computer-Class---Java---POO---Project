package exceptions;

/*Esta se manda cuando el usuario no cumple el semestre minimo del equipo*/
public class SemestreInsuficienteException extends LaboratorioException{

    public SemestreInsuficienteException(){
        super("No cumples el semestre minimo para el uso del equipo sorry :(");
    }

    public SemestreInsuficienteException(String mensaje){
        super(mensaje);
    }
}
