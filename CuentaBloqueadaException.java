package exceptions;

/* Este se manda cuando una cuenta que esta bloqueada x intentos fallidos busca entrar*/
public class CuentaBloqueadaException extends LaboratorioException{

    public CuentaBloqueadaException(){
        super("La cuenta esta bloqueada y solo un administrador la puede reactivar sorry");
    }

    public CuentaBloqueadaException(String mensaje){
        super(mensaje);
    }
}
