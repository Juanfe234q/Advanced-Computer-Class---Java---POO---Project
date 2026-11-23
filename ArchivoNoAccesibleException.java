package exceptions;

/*Lo mandamos cuando el archivo de texto no existe o no se puede leer*/
public class ArchivoNoAccesibleException extends LaboratorioException{

    public ArchivoNoAccesibleException(){
        super("No se pudo abrir el archivo :(");
    }

    public ArchivoNoAccesibleException(String mensaje){
        super(mensaje);
    }
}
