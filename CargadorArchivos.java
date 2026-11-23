package persistence;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import service.ServicioEquipos;
import service.ServicioUsuarios;

import exceptions.UsuarioNoAutenticadoException;
import exceptions.AccesoDenegadoException;
import exceptions.UsuarioInactivoException;


/**
 * 
 * CargadorArchivos
 * En esta clase nos encargaremos de coordinar la carga de los archivos txt, nuestra idea 
   es aprovechar los metodos que ya hicimos en Servicio usuarios y equipos :)
 */ 
public class CargadorArchivos {
    
    //El primer paso de siempre, los atributos y el constructor :)
    private ServicioUsuarios servicioUsuarios;
    private ServicioEquipos servicioEquipos;

    /**
     * Constructor
     * @param servicioUsuarios valida y registra los usuarios
     * @param servicioEquipos  valida y registra los equipos
     */
    public CargadorArchivos(ServicioUsuarios servicioUsuarios, ServicioEquipos servicioEquipos) {
        this.servicioUsuarios = servicioUsuarios;
        this.servicioEquipos = servicioEquipos;
    }

    /**
     * Ahora, procedemos a cargar los usuarios
       Este metodo nos ayuda a contar las lineas del archivo, o sea, los usuarios. Luego validamos la info 
       que lee, restamos para obtener los errores y al final solo nos queda devolver la info de lo que se leyo bien y que 
       errores hubieron ;)
     * @param rutaArchivo archivo que tiene los registros de usuarios
     * @return cantidad de registros cargados y los errores
     * @throws IOException si no se pudo leer el archivo
     * @throws UsuarioNoAutenticadoException si no hay usuario con la sesion iniciada
     * @throws AccesoDenegadoException si el usuario no tiene rol de admon
     * @throws UsuarioInactivoException si el usuario se encuentra desactivado
     */
    public ResultadoCarga cargarUsuarios(String rutaArchivo) throws IOException, UsuarioNoAutenticadoException,
        AccesoDenegadoException, UsuarioInactivoException{

        int totalRegistros;
        try (var lineas = Files.lines(Path.of(rutaArchivo))) {
            totalRegistros = (int) lineas.count();
        }

        int cargados = servicioUsuarios.cargarUsuariosDesdeArchivo(rutaArchivo);
        int errores = totalRegistros - cargados;

        return new ResultadoCarga(cargados, errores);
    }

    //Seguimos con nuestra logica anterior, pero ahora con los equiposss :)
    /**
     * Leer un archivo de texto donde estan los datos de los equipos
     * @param rutaArchivo nombre del archivo 
     * @return conteo de registro de equipos 
     * @throws IOException  no se pudo leer el archivo
     * @throws UsuarioNoAutenticadoException si no hay usuario con la sesion iniciada
     * @throws AccesoDenegadoException si el usuario no tiene rol de admon
     * @throws UsuarioInactivoException si el usuario se encuentra desactivado
     
     */
    public ResultadoCarga cargarEquipos(String rutaArchivo) throws IOException, UsuarioNoAutenticadoException, AccesoDenegadoException, UsuarioInactivoException  {

        int totalRegistros;

        try (var lineas = Files.lines(Path.of(rutaArchivo))) {
            totalRegistros = (int) lineas.count();
        }

        int cargados = servicioEquipos.cargarEquiposDesdeArchivo(rutaArchivo);
        int errores = totalRegistros - cargados;

        return new ResultadoCarga(cargados, errores);
    }
        

}
