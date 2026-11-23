package persistence;

import model.Equipo;
import service.ServicioInformes;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/*En esta clase el fin es poder exportar los informes y rankings que generamos en 
la clase Servicio Informes :D */

/**
 * 
 * ExportadorInformes
 * En esta clase el fin es poder exportar los informes y rankings que generamos en 
   la clase Servicio Informes :D
 */
public class ExportadorInformes {
    
    //Como siempre, iniciamos con los atributos y el constructor 
    private ServicioInformes servicioInformes;

    /**
     * Constructor
     * @param servicioInformes Se obtienen los datos
     */
    public ExportadorInformes(ServicioInformes servicioInformes) {
        this.servicioInformes = servicioInformes;
    }

    //Ahora si, iniciamos con los metodos de esta clase :)
    /*El fin de este metodo es recibir la ruta del archivo donde vamos a escribir el informe, recibir la info que 
    queremos guardar y escribirla linea por linea */

    /**
     * Ahora si, iniciamos con los metodos de esta clase :)
      El fin de este metodo es recibir la ruta del archivo donde vamos a escribir el informe, recibir la info que 
      queremos guardar y escribirla linea por linea 
     * @param rutaArchivo archivo destino
     * @param contenido lista de lineas que se van a escribir en el archivo
     * @throws IOException si ocurre un error de escritura
     */
    private void escribirArchivo(String rutaArchivo, List<String> contenido) throws IOException {

        try (BufferedWriter escritor = Files.newBufferedWriter(Path.of(rutaArchivo))) {

            for (String linea : contenido) {
                escritor.write(linea);
                escritor.newLine();
            }
        }
    }

   
    /**
     * Ahora exportemos los ranking que hicimos antes :)
     * @param rutaArchivo archivo donde se exportara el reporte
     * @throws IOException si no se puede crear el arhivo o escribir el texto
     */   
    public void exportarRankingHoras(String rutaArchivo) throws IOException {

        List<Equipo> ranking = servicioInformes.obtenerRankingPorHorasUso();
        List<String> contenido = new ArrayList<>();

        contenido.add("Ranking de Equipos Por Horas de Uso");
        contenido.add("");

        for (int i = 0; i < ranking.size(); i++) {
            Equipo equipo = ranking.get(i);

            contenido.add((i + 1) + ". " + equipo.getNombre() + " | Horas: " + equipo.getHorasUso());
        }
        escribirArchivo(rutaArchivo, contenido);
    }    

    /**
     * Exportar los ranking de equipos
     * @param rutaArchivo archivo donde se exportara el reporte
     * @throws IOException si no se puede crear el arhivo o escribir el texto
     */
    public void exportarRankingSesiones(String rutaArchivo) throws IOException {

        List<Equipo> ranking = servicioInformes.obtenerRankingPorSesiones();
        List<String> contenido = new ArrayList<>();

        contenido.add("Ranking de Equipos Por Numero de Sesiones");
        contenido.add("");

        for (int i = 0; i < ranking.size(); i++) {
            Equipo equipo = ranking.get(i);

            contenido.add((i + 1) + ". " + equipo.getNombre() + " | Sesiones: " + equipo.getNumeroSesiones());
        }
        escribirArchivo(rutaArchivo, contenido);
    }

    /**
     * Ranking de los valores de los equipos organizados
     * @param rutaArchivo archivo donde se exportara el reporte
     * @throws IOException si no se puede crear el arhivo o escribir el texto
     */
    public void exportarRankingValor(String rutaArchivo) throws IOException {

        List<Equipo> ranking = servicioInformes.obtenerRankingPorValor();
        List<String> contenido = new ArrayList<>();

        contenido.add("Ranking de Equipos Por Valor");
        contenido.add("");

        for (int i = 0; i < ranking.size(); i++) {
            Equipo equipo = ranking.get(i);
            contenido.add((i + 1) + ". " + equipo.getNombre() + " | Valor: " + equipo.getValor());
        }
        escribirArchivo(rutaArchivo, contenido);
    }
    
}
