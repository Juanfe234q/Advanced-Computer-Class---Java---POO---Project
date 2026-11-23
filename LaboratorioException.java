package exceptions;

/*La logica de las excepciones es que todas sean por herencia, so de las veinti tantas
que creo van a salir esta va a ser el core :D

Y las otras pss se extienden de esta bofff
 */
public class LaboratorioException extends Exception{

    public LaboratorioException(){
        super("Error en el sistema (en el core :D)");
    }

    public LaboratorioException(String mensaje){
        super(mensaje);
    }
}
