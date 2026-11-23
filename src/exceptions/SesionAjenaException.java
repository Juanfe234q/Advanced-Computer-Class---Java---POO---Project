package exceptions;

/*Esta se manda cuando un estudiante o monitor intenta operar una sesion que no es de el*/
public class SesionAjenaException extends LaboratorioException{

    public SesionAjenaException(){
        super("Esa sesion no es tuya bro :O");
    }

    public SesionAjenaException(String mensaje){
        super(mensaje);
    }
}
