package exceptions;

/* Se manda cuando el usuario tiene el max de sesiones
programadas que puede y esto varia por rol*/
public class LimiteSesionesExcedidoException extends LaboratorioException{

    public LimiteSesionesExcedidoException(){
        super("Ya tienes el max de sesiones programadas sorry bro");
    }

    public LimiteSesionesExcedidoException(String mensaje){
        super(mensaje);
    }
}
