package exceptions;

/* Esta se manda cuando una lintea del txt esta mal :D*/
public class LineaInvalidaException extends LaboratorioException{

    /*Esta excepción es un poco mas chistosita entonces
    les explico la logica x si las flies:

    Tonces pues la variable del numero de linea nada raro
    Luego el constructor :D
    En el constructor super arma el mensaje y se lo pasa a lab exception
    y eso se lo pasa a la exception que luego manda el mensaje
    */
    private int numeroLinea;
    public LineaInvalidaException(int numeroLinea){
        super("Linea " + numeroLinea + " invalida");
        this.numeroLinea=numeroLinea;
    }

    /*Y yap aqui simplemente lo devuelve*/
    public int getNumeroLinea(){
        return numeroLinea;
    }
}
