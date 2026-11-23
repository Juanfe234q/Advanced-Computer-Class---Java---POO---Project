package exceptions;

/*Esta se manda cuando no existe una sesion con ese codigo*/
public class SesionNoEncontradaException extends LaboratorioException{

    public SesionNoEncontradaException(){
        super("Pues yo ese codigo no lo encuentro :(");
    }

    public SesionNoEncontradaException(String mensaje){
        super(mensaje);
    }
}
