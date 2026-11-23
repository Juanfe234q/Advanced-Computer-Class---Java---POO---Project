package persistence;

import model.SistemaLaboratorio;

import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;

 /**
  * 
  * GestorPersistencia
  * El fin de esta clase es poder guardar todo el sistema, esta clase es importante para evitar perdidas de info al 
    cerrar el programa o cosas por el estilo. La idea principal, es guardar toda la info que registremos y poderla usar 
    sin tener problemas si ejecutamos varias veces el programa :D  
  */
public class GestorPersistencia {

    /*Aqui usamos este metodo para guardar el sistema.
    La idea de como funciona es que recibe el sistema y su ruta, abre el archivo y guarda el objeto ;)*/
    public void guardarSistema(SistemaLaboratorio sistema, String rutaArchivo) throws IOException {

        try (ObjectOutputStream salida = new ObjectOutputStream(new FileOutputStream(rutaArchivo))) {
            salida.writeObject(sistema);
        }
    }

    
    /**
     * Ahora usamos un metodo inverso, con el cual podamos recuperar el sistema
       La idea es que lea el archivo, luego el objeto y recupere el sistema que queramos 
     * @param rutaArchivo archivo binario desde donde se recuperara el estado
     * @return sistema de laboratorio restaurado
     * @throws IOException si no se pudo acceder al archivo
     * @throws ClassNotFoundException alguna clase serializada no coincide o no se encuentra
     */
    public SistemaLaboratorio cargarSistema(String rutaArchivo) throws IOException, ClassNotFoundException {

        try (ObjectInputStream entrada = new ObjectInputStream(new FileInputStream(rutaArchivo))) {
            return (SistemaLaboratorio) entrada.readObject();
        }
    }

}