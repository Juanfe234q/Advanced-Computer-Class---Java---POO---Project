package exceptions;

/*Esta se manda cuando el unico admin intenta autodestruirse jajajajaja*/
public class UltimoAdministradorException extends LaboratorioException{

    public UltimoAdministradorException(){
        super("Broky eres el unico admin no te autodestruyas :(");
    }

    public UltimoAdministradorException(String mensaje){
        super(mensaje);
    }
}
