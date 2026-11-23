package service;
import exceptions.AccesoDenegadoException;
import exceptions.CodigoDuplicadoException;
import exceptions.DatoInvalidoException;
import exceptions.EquipoConSesionesProgramadasException;
import exceptions.EquipoNoEncontradoException;
import exceptions.UsuarioInactivoException;
import exceptions.UsuarioNoAutenticadoException;
import model.Equipo;
import model.Laboratorio;
import model.SistemaLaboratorio;
import model.enums.EstadoEquipo;
import model.enums.OpcionMenu;
import model.enums.TipoEquipo;
import model.enums.TipoOperacion;
import java.util.ArrayList;
import java.util.List;

/* ahora importamos estas clases para manejar rutas y leer archivos de texto :) */
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Normalizer;

/**
 * 
 * ServicioEquipos
 * Gestiona el inventario de los equipos del laboratorio
 */
public class ServicioEquipos{

    private SistemaLaboratorio sistema;
    private ServicioAutenticacion auth;
    private ServicioAuditoria auditoria;

    /**
     * constructor
     * @param sistema almacena equipos y labs
     * @param auth validar pemisos del admin
     * @param auditoria registrar movimientos en el invetario
     */
    public ServicioEquipos(SistemaLaboratorio sistema, ServicioAutenticacion auth, ServicioAuditoria auditoria){
        this.sistema=sistema;
        this.auth=auth;
        this.auditoria=auditoria;
    }

    /**
     * Registra un nuevo equipo en el sistema por consola.
     *
     * @param codigo            codigo del equipo
     * @param nombre            nombre del equipo
     * @param tipo              categoria del equipo
     * @param nombreLaboratorio nombre del laboratorio
     * @param estado            estado operativo inicial 
     * @param valor             valor del equipo (debe ser mayor a cero)
     * @param semestreMinimo    semestre minimo requerido
     * @param descripcion       descripcion del equipo
     * @return el objeto creado y registrado
     * @throws UsuarioNoAutenticadoException si no hay sesion iniciada
     * @throws AccesoDenegadoException       si el usuario no es administrador
     * @throws UsuarioInactivoException      si la cuenta del usuario no esta activa
     * @throws CodigoDuplicadoException      si ya existe un equipo con el mismo código
     * @throws DatoInvalidoException         si algun campo obligatorio es invalido
     */
    public Equipo registrarEquipo(String codigo, String nombre, TipoEquipo tipo, String nombreLaboratorio, EstadoEquipo estado, double valor, int semestreMinimo, String descripcion)
            throws UsuarioNoAutenticadoException, AccesoDenegadoException, UsuarioInactivoException, CodigoDuplicadoException, DatoInvalidoException{
        /*Empezamos como en Autenticacion tambien aca validamos la operacion*/
        auth.validarOperacion(OpcionMenu.REGISTRAR_EQUIPO);
        /*Y le hacemos return utilizando el metodo privado agregar :D
        Esta aqui abajito :D*/
        return agregar(codigo, nombre, tipo, nombreLaboratorio, estado, valor, semestreMinimo, descripcion);
    }

    
    /**
     * Valida los datos despues incorpora el nuevo equipo al sistema.
     *
     * @param codigo            codigo del equipo
     * @param nombre            nombre del equipo
     * @param tipo              categoria del equipo
     * @param nombreLaboratorio nombre del laboratorio
     * @param estado            estado operativo inicial
     * @param valor             valor  del equipo
     * @param semestreMinimo    semestre minimo permitido
     * @param descripcion       descripcion del equipo
     * @return equipo registrado
     * @throws CodigoDuplicadoException si el codigo ya esta registrado en el arreglo de equipos
     * @throws DatoInvalidoException    si los datos incumplen las reglas 
     */
    private Equipo agregar(String codigo, String nombre, TipoEquipo tipo, String nombreLaboratorio, EstadoEquipo estado, double valor, int semestreMinimo, String descripcion)
            throws CodigoDuplicadoException, DatoInvalidoException{
        /*Reutilizamos el metodo de validar datos */
        validarDatos(codigo, nombre, nombreLaboratorio, valor, semestreMinimo, descripcion);
        if(tipo==null || estado==null){
            throw new DatoInvalidoException("El tipo y el estado no pueden estar vacios!");
        }

        /*Si el sistema encuenta al equipo con el codigo dado pues hay una duplicacion
        esto lit es casi que el copy paste del metood de servicio autenticacion*/
        if(sistema.getEquipos().containsKey(codigo)){
            /*Lo metemos a la auditoria al registro y tambien lanzamos la excepcion*/
            auditoria.registrar(auth.getUsuarioActual(), TipoOperacion.ALTA, "Codigo de equipo duplicado " + codigo, false);
            throw new CodigoDuplicadoException("Ya existe un equipo con el codigo " + codigo);
        }

        /*Ahora si aqui se obtiene o crea el lab :D*/
        /*Chiago y Malu by the way aqui reusamos el metodo de obtenerocrearlab
        yo se que es confuso pero esta en sistema laboratorio jajaja
        es que son un monton de metodos! :D*/
        Laboratorio lab=sistema.obtenerOCrearLaboratorio(nombreLaboratorio);
        Equipo nuevo=new Equipo(codigo, nombre, tipo, lab, estado, valor, semestreMinimo, descripcion);
        /*Ahi lo creamos! :D*/
        sistema.getEquipos().put(codigo, nuevo);
        /*Y tambien registramos en auditoria*/
        auditoria.registrar(auth.getUsuarioActual(), TipoOperacion.ALTA, "Nuevo equipo " + codigo + " en " + nombreLaboratorio, true);
        return nuevo;
    }

     /**
     * Registra un desde un archivo.
     *
     * @param codigo            codigo del equipo
     * @param nombre            nombre del equipo
     * @param tipo              categoria del equipo
     * @param nombreLaboratorio nombre del laboratorio
     * @param estado            estado operativo inicial
     * @param valor             valor  del equipo
     * @param semestreMinimo    semestre minimo permitido
     * @param descripcion       descripcion del equipo
     * @return Equipo creado
     * @throws UsuarioNoAutenticadoException si no hay sesión iniciada
     * @throws AccesoDenegadoException       si el usuario no tiene rol de administrador
     * @throws UsuarioInactivoException      si la cuenta del usuario no está activa
     * @throws CodigoDuplicadoException      si el codigo del equipo ya existe
     * @throws DatoInvalidoException         si algún dato es invalido
     */
    public Equipo registrarDesdeArchivo(String codigo, String nombre, TipoEquipo tipo, String nombreLaboratorio, EstadoEquipo estado, double valor, int semestreMinimo, String descripcion)
            throws UsuarioNoAutenticadoException, AccesoDenegadoException, UsuarioInactivoException, CodigoDuplicadoException, DatoInvalidoException{
        /*Validamos la operacion*/
        auth.validarOperacion(OpcionMenu.CARGAR_EQUIPOS);
        /*Y retornamos con la clase privada Agregar tambien usamos agregar :D*/
        return agregar(codigo, nombre, tipo, nombreLaboratorio, estado, valor, semestreMinimo, descripcion);
    }

    /*Ahora el metodo de cargar desde archivo :D*/
    public int cargarEquiposDesdeArchivo(String rutaArchivo)
            throws UsuarioNoAutenticadoException, AccesoDenegadoException,
            UsuarioInactivoException, IOException {

        // Validamos que el usuario pueda cargar equipos
        auth.validarOperacion(OpcionMenu.CARGAR_EQUIPOS);

        int cargados = 0;
        int numeroLinea = 0;

        Path path = Path.of(rutaArchivo);

        try (BufferedReader br = Files.newBufferedReader(path)) {

            String linea;

            while ((linea = br.readLine()) != null) {

                numeroLinea++;

                if (linea.trim().isEmpty()) {
                    continue;
                }

                try {

                    // Separamos los ocho campos
                    String[] partes = linea.split("\\*", -1);

                    if (partes.length != 8) {
                        throw new DatoInvalidoException(
                                "La línea debe tener 8 campos :)");
                    }

                    // Obtenemos los datos
                    String codigo = partes[0].trim();
                    String nombre = partes[1].trim();

                    TipoEquipo tipo = TipoEquipo.valueOf(normalizarEnum(partes[2]));

                    String nombreLaboratorio = partes[3].trim();

                    EstadoEquipo estado = EstadoEquipo.valueOf(normalizarEnum(partes[4]));

                    double valor = Double.parseDouble(partes[5].trim());
                    int semestreMinimo = Integer.parseInt(partes[6].trim());

                    String descripcion = partes[7].trim();

                    // Registramos el equipo
                    agregar(codigo, nombre, tipo, nombreLaboratorio, estado, valor, semestreMinimo, descripcion);
                    cargados++;

                } catch (IllegalArgumentException | DatoInvalidoException | CodigoDuplicadoException e) {
                    System.out.println("Error en la línea " + numeroLinea + ": " + e.getMessage());
                }
            }
        }
        return cargados;
    }

    private String normalizarEnum(String texto) {
        return Normalizer.normalize(texto.trim(), Normalizer.Form.NFD)
            .replaceAll("\\p{M}", "")
            .toUpperCase()
            .replace(' ', '_');
    }

    /*Ahora validamos que los datos del equipo tengan sentido :D*/

    public void validarDatos(String codigo, String nombre, String nombreLaboratorio, double valor, int semestreMinimo, String descripcion) throws DatoInvalidoException{
        /*Lit un monton de if-excepciones :D para validar todo :D*/
        if(estaVacio(codigo) || estaVacio(nombre) || estaVacio(nombreLaboratorio) || estaVacio(descripcion)){
            throw new DatoInvalidoException("Codigo, nombre, nombrelaboratorio y descripcion no pueden estar vacios!");
        }
        if(codigo.contains("*") || nombre.contains("*") || nombreLaboratorio.contains("*") || descripcion.contains("*")){
            throw new DatoInvalidoException("Los datos no pueden tener *");
        }
        if(valor<=0){
            throw new DatoInvalidoException("El valor tiene que ser mayor que 0");
        }
        if(semestreMinimo<ServicioUsuarios.SEMESTRE_MINIMO || semestreMinimo>ServicioUsuarios.SEMESTRE_MAXIMO){
            throw new DatoInvalidoException("El semestre minimo tiene que estar entre "
                    + ServicioUsuarios.SEMESTRE_MINIMO + " y " + ServicioUsuarios.SEMESTRE_MAXIMO);
        }
    }

    /*Aqui un metodo privado para ver si esta vacio :v*/
    private boolean estaVacio(String texto){
        return texto==null || texto.trim().isEmpty();
    }

    /*Y ahora si el metodo de buscar un equipo*/
    public Equipo buscarEquipo(String codigo) throws EquipoNoEncontradoException{
        /*Getter de always */
        Equipo e=sistema.getEquipos().get(codigo);
        if(e==null){
            /*Excepcion si no se encuentra con el codigo*/
            throw new EquipoNoEncontradoException("No existe un equipo con el codigo " + codigo);
        }
        return e;
    }

    /*Muy parecido a en autenticacion ahora con listado de equipos mi gente :D*/
    public List<Equipo> listarEquipos(){
        return new ArrayList<>(sistema.getEquipos().values());
    }

    /*Y listar laboratorios casi un copy paste :D*/
    public List<Laboratorio> listarLaboratorios(){
        return new ArrayList<>(sistema.getLaboratorios().values());
    }

    /*Ahora la opcion de buscar un lab :D*/
    public Laboratorio consultarLaboratorio(String nombre) throws UsuarioNoAutenticadoException, AccesoDenegadoException, DatoInvalidoException{
        /*Primero verificamos el acceso :D*/
        auth.verificarAcceso(OpcionMenu.ESTADO_LABORATORIO);
        /*Miramos que exista el lab*/
        Laboratorio lab=sistema.getLaboratorios().get(nombre);
        if(lab==null){
            throw new DatoInvalidoException("No existe el laboratorio " + nombre +" :(");
        }
        /*Lo retornamos :D*/
        return lab;
    }

    /*Modificar el equipo :D esto acueerdensen solo lo hace el admin*/
    public void modificarEquipo(String codigo, String nombre, TipoEquipo tipo, double valor, int semestreMinimo, String descripcion)
            throws UsuarioNoAutenticadoException, AccesoDenegadoException, UsuarioInactivoException, EquipoNoEncontradoException, DatoInvalidoException{
        /*Ahora validamos la operacion*/
        auth.validarOperacion(OpcionMenu.GESTION_ADMINISTRACION);
        Equipo e=buscarEquipo(codigo);
        /*Ahi usamos el metodo de validar datos :D*/
        validarDatos(codigo, nombre, e.getLaboratorio().getNombre(), valor, semestreMinimo, descripcion);
        if(tipo==null){
            /*Aqui mandamos la excepcion si esta en null el tipo*/
            throw new DatoInvalidoException("El tipo es obligatorio");
        }
        /*Y yap aqui lo creamos con los setters :D*/
        e.setNombre(nombre);
        e.setTipo(tipo);
        e.setValor(valor);
        e.setSemestreMinimo(semestreMinimo);
        e.setDescripcion(descripcion);
        /*Y como casi siempre tambien va en el registro de auditoria :D*/
        auditoria.registrar(auth.getUsuarioActual(), TipoOperacion.MODIFICACION, "Equipo modificado: " + codigo, true);
    }

    /*Ahora el metodo de cambiar el estado de un equipo :V*/
    public void cambiarEstado(String codigo, EstadoEquipo nuevo)
            throws UsuarioNoAutenticadoException, AccesoDenegadoException, UsuarioInactivoException, EquipoNoEncontradoException, EquipoConSesionesProgramadasException{
        /*Again validamos la operacion que sea admin*/
        auth.validarOperacion(OpcionMenu.GESTION_ADMINISTRACION);
        Equipo e=buscarEquipo(codigo);
        /*Si tiene sesiones programadas no se podra mandamos la excepcion*/
        if(nuevo==EstadoEquipo.DADO_DE_BAJA && e.tieneSesionesProgramadas()){
            auditoria.registrar(auth.getUsuarioActual(), TipoOperacion.CAMBIO_ESTADO, "Equipo " + codigo + " tiene sesiones programadas", false);
            throw new EquipoConSesionesProgramadasException();
        }
        /*Definimos el estado con el que esta*/
        EstadoEquipo anterior=e.getEstado();
        /*Y con el setter le asignamos el nuevo*/
        e.setEstado(nuevo);
        /*Igual que siempre va al registro de auditoria*/
        auditoria.registrar(auth.getUsuarioActual(), TipoOperacion.CAMBIO_ESTADO, "Equipo " + codigo + " de " + anterior + " a " + nuevo, true);
    }



    /*Ahora el metodo de enviar a mantenimiento :D
    reutilizamos cambiar equipo */
    public void enviarAMantenimiento(String codigo)
            throws UsuarioNoAutenticadoException, AccesoDenegadoException, UsuarioInactivoException, EquipoNoEncontradoException, EquipoConSesionesProgramadasException{
        cambiarEstado(codigo, EstadoEquipo.EN_MANTENIMIENTO);
    }

    /*El de dar de baja basicamente igual */
    public void darDeBaja(String codigo)
            throws UsuarioNoAutenticadoException, AccesoDenegadoException, UsuarioInactivoException, EquipoNoEncontradoException, EquipoConSesionesProgramadasException{
        cambiarEstado(codigo, EstadoEquipo.DADO_DE_BAJA);
    }

    /*NOTA: Como tal chicos esto es lo que pide el proyecto como esos shorcuts a enviaramantenimiento y dar de baja*/

    /*Y ahora eliminar un equipo :D*/
    public void eliminarEquipo(String codigo)
            throws UsuarioNoAutenticadoException, AccesoDenegadoException, UsuarioInactivoException, EquipoNoEncontradoException, EquipoConSesionesProgramadasException{
        /*Como siempre validamos que el admin sea admin :D*/
        auth.validarOperacion(OpcionMenu.GESTION_ADMINISTRACION);
        /*Buscamos el equipo con el metodo :V*/
        Equipo e=buscarEquipo(codigo);
        /*Si tiene sesiones programadas no se le puede eliminar :V*/
        if(e.tieneSesionesProgramadas()){
            auditoria.registrar(auth.getUsuarioActual(), TipoOperacion.ELIMINACION, "Equipo " + codigo + " tiene sesiones programadas", false);
            throw new EquipoConSesionesProgramadasException();
        }
        /*Y yap ahora si queda eliminado del lab como del sistema :D
        tambien registramos en la auditoria como siempre*/
        e.getLaboratorio().quitarEquipo(e);
        sistema.getEquipos().remove(codigo);
        auditoria.registrar(auth.getUsuarioActual(), TipoOperacion.ELIMINACION, "Equipo " + codigo, true);
    }
}
