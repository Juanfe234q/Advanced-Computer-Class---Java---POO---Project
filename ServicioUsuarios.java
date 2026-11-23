package service;
import exceptions.AccesoDenegadoException;
import exceptions.CodigoDuplicadoException;
import exceptions.DatoInvalidoException;
import exceptions.UltimoAdministradorException;
import exceptions.UsuarioConSesionesProgramadasException;
import exceptions.UsuarioInactivoException;
import exceptions.UsuarioNoAutenticadoException;
import exceptions.UsuarioNoEncontradoException;
import model.Administrador;
import model.Estudiante;
import model.Monitor;
import model.SistemaLaboratorio;
import model.Usuario;
import model.UsuarioAcademico;
import model.enums.EstadoUsuario;
import model.enums.OpcionMenu;
import model.enums.Rol;
import model.enums.TipoOperacion;
import java.util.ArrayList;
import java.util.List;

/*Ahora importamos las clases para la lectura de archivos */
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Normalizer;

/*El proposito de la clase de servicio usuarios es administrar
todo lo relacionado con los usuarios
 - registra usuarios nuevos
 - modifica sus datos
 - suspende, reactiva, etc..
 - eliminarlos tambien :D*/
public class ServicioUsuarios{

    /*Chicos aqui hice unas varables final para que tenga sentido como que tipo alguien no este en semestre 45*/
    public static final int SEMESTRE_MINIMO=1;
    public static final int SEMESTRE_MAXIMO=10;

    private SistemaLaboratorio sistema;
    private ServicioAutenticacion auth;
    private ServicioAuditoria auditoria;

    /*Constructor igual que siempre :D*/
    public ServicioUsuarios(SistemaLaboratorio sistema, ServicioAutenticacion auth, ServicioAuditoria auditoria){
        this.sistema=sistema;
        /*Este viene del servicio de autienticacion*/
        this.auth=auth;
        this.auditoria=auditoria;
    }

    /*Vamos con los metodos, registrar el usuario :D*/
    public void registrarUsuario(Usuario nuevo)
    /*Tira todas las excepciones necesarias*/
            throws UsuarioNoAutenticadoException, AccesoDenegadoException, UsuarioInactivoException, CodigoDuplicadoException, DatoInvalidoException{
                /*Primero autenticamos, reutilizando el metodo de servicio autenticacion*/
                auth.validarOperacion(OpcionMenu.REGISTRAR_USUARIO);
                /*Y listo lo agregamos con el metodo agregar de abajo :D*/
        agregar(nuevo);
    }

    /*Ahora el metodo de registrarlo desde el archivo, osea registrarlo pero desde el txt
    del resto igual hasta con las mismas excepciones :D*/
    public void registrarDesdeArchivo(Usuario nuevo)
            throws UsuarioNoAutenticadoException, AccesoDenegadoException, UsuarioInactivoException, CodigoDuplicadoException, DatoInvalidoException{
        auth.validarOperacion(OpcionMenu.CARGAR_USUARIOS);
        agregar(nuevo);
    }

    /*Agregamos este metodo para poder normalizar los enums y asi podamos 
    contener tildes y espacios :D*/
    private String normalizarEnum(String texto){
    return Normalizer.normalize(texto.trim(), Normalizer.Form.NFD).replaceAll("\\p{M}", "").toUpperCase().replace(' ', '_');
    }

    /*El metodo de agregar que es clave pa todos :D
    - Chicos importante recordar que es private pq solo Serviciousuarios lo puede usar, o sea
    solo dentro de esta clase :D*/
    private void agregar(Usuario nuevo) throws CodigoDuplicadoException, DatoInvalidoException{
        /*Aqui toca hacer un metodo de validar datos para usarlo
        Listo ya quedo :D */
        validarDatos(nuevo);

        /*Aqui verificamos que el codigo no este duplicado
        con el containsKey :D*/
        if(sistema.getUsuarios().containsKey(nuevo.getCodigo())){
            /*Aqui en la auditoria tambien se registra :D*/
            auditoria.registrar(auth.getUsuarioActual(), TipoOperacion.ALTA, "Codigo duplicado :O " + nuevo.getCodigo(), false);
            throw new CodigoDuplicadoException("Ya existe un usuario con el codigo " + nuevo.getCodigo());
        }
        /*Y yap, si no pues todo good :D
        - Guardamos el usuario en el sistema
        - y se registra con la auditoria tmb para llevar el registro :D*/
        sistema.getUsuarios().put(nuevo.getCodigo(), nuevo);
        auditoria.registrar(auth.getUsuarioActual(), TipoOperacion.ALTA, "Usuario " + nuevo.getCodigo() + " " + nuevo.getRol(), true);
    }

    /*Ahora el metodo para validar los datos para la de arriba de agregar :D
    La idea es esta :D
    - Mirar que cada uno de los datos sean los que tienen que estar
    - Asumo que es con muchos getters
    - Verificar que nada este vacio
    - Ah y tambien no puede haber el * porque ese es el separador!
    */

    public void validarDatos(Usuario u) throws DatoInvalidoException{
        if(estaVacio(u.getCodigo()) || estaVacio(u.getNombreCompleto())){
            throw new DatoInvalidoException("El codigo y el nombre no pueden estar vacios :O");
        }
        if(u.getCodigo().contains("*") || u.getNombreCompleto().contains("*")){
            throw new DatoInvalidoException("Los datos no pueden tener *");
        }
        /* Verificamos que la clave no esté vacía ni contenga el separador */
        if (estaVacio(u.getClave()) || u.getClave().contains("*")) {
            throw new DatoInvalidoException("La clave no es valida");
        }

        /*Ahora vamos ver si es un usuario academico con instance of), si es asi miramos que
        contenga lo que tiene que tener: - Programa y semestre :D*/
        if(u instanceof UsuarioAcademico){
            UsuarioAcademico a=(UsuarioAcademico)u;
            if(estaVacio(a.getPrograma())){
                throw new DatoInvalidoException("El programa no puede estar vacio :o");
            }
            if(a.getSemestre()<SEMESTRE_MINIMO || a.getSemestre()>SEMESTRE_MAXIMO){
                /*Aqui es lo que puse arriba pa mantener la logica chicos de semstre, en este caso max 10*/
                throw new DatoInvalidoException("El semestre tiene que estar entre " + SEMESTRE_MINIMO + " y " + SEMESTRE_MAXIMO);
            }
        }
    }

    /*Aqui el metodo pa verificar si algo esta vacio esto sirve para ahorita en la parte de gestionar

    */
    private boolean estaVacio(String texto){
        return texto==null || texto.trim().isEmpty();
    }

    /*Ahora vamos con el metodo de buscar usuario :D*/
    public Usuario buscarUsuario(String codigo) throws UsuarioNoEncontradoException{
        Usuario u=sistema.getUsuarios().get(codigo);
        if(u==null){
            throw new UsuarioNoEncontradoException("No existe un usuario con el codigo: " + codigo + " :( ");
        }
        return u;
    }

    /*Ahora el metodo de listar usuarios, aca pss devuvle el arreglo (la lista)*/
    public List<Usuario> listarUsuarios(){
        return new ArrayList<>(sistema.getUsuarios().values());
    }

    /*Same que el de antes, listar admins :D
    aca la unica diferencia es que pues con el for each y el if se detecta si es
    o no admin :D*/
    public List<Usuario> listarAdministradores(){
        List<Usuario> resultado=new ArrayList<>();
        for(Usuario u : sistema.getUsuarios().values()){
            if(u.getRol()==Rol.ADMINISTRADOR){
                resultado.add(u);
            }
        }
        return resultado;
    }

    /*Este metodo yo se que es medio chistoso el why su existencia
    pero acuerdensen que dice que el proyecto dice:

     ni un administrador puede eliminarse o desactivarse a sí mismo si es el único administrador activo

     so, despues este sirve para verificar eso y que no ocurra :D
     */
    public int contarAdministradoresActivos(){
        int contador=0;
        for(Usuario u : sistema.getUsuarios().values()){
            if(u.getRol()==Rol.ADMINISTRADOR && u.estaActivo()){
                contador++;
            }
        }
        return contador;
    }

    /*Ahora para la parde de gestionar del admin :D
    - So primero el metodo de modificar nombre
    - Tambien debe haber uno de modificar los datos academicos
    - Otra de suspender la cuenta
    - Otro de reactivarla
    - Otro de desbloquearla tras haberla bloqueado x intentos fallidos
    - Otro de eleiminar cuenta
    - Y el de que el admin se elimine a si mismo */

    /*Listo con este metodo, puede mandar la excepcion :D y yap del resto creo que nada raro tambien todo lo manda al registro de la auditoria*/
    public void modificarNombre(String codigo, String nuevoNombre)
            throws UsuarioNoAutenticadoException, AccesoDenegadoException, UsuarioInactivoException, UsuarioNoEncontradoException, DatoInvalidoException{
        /*Aqui reutilizamos el de validar operacion aqui hace mucho sentido x si no lo habian entendido :D*/
        auth.validarOperacion(OpcionMenu.GESTION_ADMINISTRACION);
        Usuario u=buscarUsuario(codigo);
        if(estaVacio(nuevoNombre) || nuevoNombre.contains("*")){
            throw new DatoInvalidoException("Nombre no valido");
        }
        u.setNombreCompleto(nuevoNombre);
        auditoria.registrar(auth.getUsuarioActual(), TipoOperacion.MODIFICACION, "Nombre de " + codigo, true);
    }

    /*Casi la misma logica de antes un poquito mas largo :D*/
    public void modificarDatosAcademicos(String codigo, String programa, int semestre)
            throws UsuarioNoAutenticadoException, AccesoDenegadoException, UsuarioInactivoException, UsuarioNoEncontradoException, DatoInvalidoException{
        /*Aqui reutilizamos el de validar operacion aqui hace mucho sentido :D*/
        auth.validarOperacion(OpcionMenu.GESTION_ADMINISTRACION);
        Usuario u=buscarUsuario(codigo);
        /*Aqui con instanceof volvemos a ver si es usuario academico pa mandar la excepcion*/
        if(!(u instanceof UsuarioAcademico)){
            throw new DatoInvalidoException("Los admins no tienen programa ni semestre");
        }
        /*Aqui utilizamos tambien el estavacio de arribita :D*/
        if(estaVacio(programa) || semestre<SEMESTRE_MINIMO || semestre>SEMESTRE_MAXIMO){
            throw new DatoInvalidoException("Programa o semestre no validos :(");
        }
        /*Aqui lo dejamos definido y registramos en la auditoria :D*/
        UsuarioAcademico a=(UsuarioAcademico)u;
        a.setPrograma(programa);
        a.setSemestre(semestre);
        auditoria.registrar(auth.getUsuarioActual(), TipoOperacion.MODIFICACION, "Programa y semestre de " + codigo, true);
    }

    /*Ahora el de restablecer la clave tras haberla bloqueado :O*/
    public void restablecerClave(String codigo, String nuevaClave)
            throws UsuarioNoAutenticadoException, AccesoDenegadoException, UsuarioInactivoException, UsuarioNoEncontradoException, DatoInvalidoException{
        /*Volvemos a validar la operacion con ese metodo :D*/
        auth.validarOperacion(OpcionMenu.GESTION_ADMINISTRACION);
        /*Primero tambien buscamos el usuario*/
        Usuario u=buscarUsuario(codigo);
        /*Ahora al crear la clave tambien se puede generar una excepcion!
        - Primero que este vacio con el metodo de arribita
        - O que contenga el * ahi pailas*/
        if(estaVacio(nuevaClave) || nuevaClave.contains("*")){
            throw new DatoInvalidoException("Clave no valida no puede ni estar vacia o tener un *");
        }
        /*Sino pues ahi con el setter y quedo :D*/
        u.setClave(nuevaClave);
        /*tmb lo registramos en la auditoria*/
        auditoria.registrar(auth.getUsuarioActual(), TipoOperacion.MODIFICACION, "Clave restablecida de " + codigo, true);
    }

    /*Ahora la de suspender una cuenta :D*/
    public void suspender(String codigo)
            throws UsuarioNoAutenticadoException, AccesoDenegadoException, UsuarioInactivoException, UsuarioNoEncontradoException, DatoInvalidoException{
        /*As always validamos la operacion :D*/
        auth.validarOperacion(OpcionMenu.GESTION_ADMINISTRACION);
        Usuario u=buscarUsuario(codigo);
        /*Aqui va una excepcion pq los admins no se les puede suspender*/
        if(!(u instanceof UsuarioAcademico)){
            throw new DatoInvalidoException("A los admins no se les puede suspender!");
        }
        /*Ahi ya con el metodo de cambiar de estado y listones!*/
        cambiarEstado(u, EstadoUsuario.SUSPENDIDO);
        /*Nota: cree tambien un metodo privado para que esto sea mas bonito de cambiar el estado*/
    }

    /*Aqui esta - Maybe toca reusarlo en los otros metodos de cambiar el estado de la cuenta :D*/
    private void cambiarEstado(Usuario u, EstadoUsuario nuevo){
        /*Ahi con el getter :D*/
        EstadoUsuario anterior=u.getEstado();
        /*Le hago el set y registro tambien en auditoria :O (demasiada auditoria jajajaja)*/
        u.setEstado(nuevo);
        auditoria.registrar(auth.getUsuarioActual(), TipoOperacion.CAMBIO_ESTADO, u.getCodigo() + " de " + anterior + " a " + nuevo, true);
    }


    /*Ahora vamos al  revez a reactivar la cuenta :D
    lit es la misma logica de ahorita pero al revez*/
    public void reactivar(String codigo)
            throws UsuarioNoAutenticadoException, AccesoDenegadoException, UsuarioInactivoException, UsuarioNoEncontradoException, DatoInvalidoException{
        /*As always validamos la operacion :D*/
        auth.validarOperacion(OpcionMenu.GESTION_ADMINISTRACION);
        Usuario u=buscarUsuario(codigo);
        if(u.getEstado()!=EstadoUsuario.SUSPENDIDO && u.getEstado()!=EstadoUsuario.INACTIVO){
            /*Aqui va una excepcion pq si no esta suspendido pss nada que hacer doesn't makes sense*/
            throw new DatoInvalidoException("El usuario no esta suspendido asi que no puedes reactivarlo :0");
        }
        /*Ahi ya con el metodo de cambiar de estado y listones!*/
        cambiarEstado(u, EstadoUsuario.ACTIVO);
        /*Nota: cree tambien un metodo privado para que esto sea mas bonito de cambiar el estado*/
    }

    /*Ahora vamos con el de desbloquear una cuenta bloqueada por intentos fallidos :D
    - Tambien toca reiniciarle los intentos */
    public void desbloquear(String codigo)
            throws UsuarioNoAutenticadoException, AccesoDenegadoException, UsuarioInactivoException, UsuarioNoEncontradoException, DatoInvalidoException{
        /*Tmb como siempre validamos la operacion :D*/
        auth.validarOperacion(OpcionMenu.GESTION_ADMINISTRACION);
        Usuario u=buscarUsuario(codigo);
        /*Aqui miramos con el if que la cuenta este bloqueada, si no lo esta pues manda la excepcion*/
        if(u.getEstado()!=EstadoUsuario.BLOQUEADO){
            throw new DatoInvalidoException("La cuenta no esta bloqueada :V");
        }
        /*Y yap reiniciamos intentos y cambiamos el estado a activo*/
        u.reiniciarIntentos();
        cambiarEstado(u, EstadoUsuario.ACTIVO);
        /*Nota: cree tambien un metodo privado para que esto sea mas bonito de cambiar el estado*/
    }

    /*Ahora la de Desactivar el admin :D
    - Si es el unico mandamos la excepcion del resto todo normal :D*/
    public void desactivarAdministrador(String codigo)
            throws UsuarioNoAutenticadoException, AccesoDenegadoException, UsuarioInactivoException, UsuarioNoEncontradoException, DatoInvalidoException, UltimoAdministradorException{
        /*Validamos la operacion del admin :D*/
        auth.validarOperacion(OpcionMenu.GESTION_ADMINISTRACION);
        Usuario u=buscarUsuario(codigo);
        /*Nuevamente verificamos con instance of que sea admin :D*/
        if(!(u instanceof Administrador)){
            throw new DatoInvalidoException("El usuario no es admin :D");
        }
        /*Aqui chicos abajito cree un metodo privado pa ver que no sea el ultimo admin*/
        revisarUltimoAdmin(u);
        /*Y yap le cambias el usuario*/
        cambiarEstado(u, EstadoUsuario.INACTIVO);
        /*Nota: cree tambien un metodo privado para que esto sea mas bonito de cambiar el estado*/
    }

    /*Lit simplemente con el instance of miramos si primero es admin y si es el unico :D
    Ah y tambien se registra en la auditoria que se intento quitar al unico admin activo si es el caso*/
    private void revisarUltimoAdmin(Usuario u) throws UltimoAdministradorException{
        if(u instanceof Administrador && u.estaActivo() && contarAdministradoresActivos()<=1){
            auditoria.registrar(auth.getUsuarioActual(), TipoOperacion.CAMBIO_ESTADO, "Intento de quitar al ultimo admin activo " + u.getCodigo(), false);
            throw new UltimoAdministradorException();
        }
    }


    /*Ahora la de eliminar un usuario
    - Nota: no se puede si tiene sesiones
    - y tambien si es el ultimo  admin activo */

    public void eliminarUsuario(String codigo)
            throws UsuarioNoAutenticadoException, AccesoDenegadoException, UsuarioInactivoException, UsuarioNoEncontradoException, UsuarioConSesionesProgramadasException, UltimoAdministradorException{
        /*Validamos que quien lo haga pss sea admin :D*/
        auth.validarOperacion(OpcionMenu.GESTION_ADMINISTRACION);
        /*Y buscamos a ese usuario con el metodo de buscarlo x el codigo :D*/
        Usuario u=buscarUsuario(codigo);

        /*Si tiene sesiones programadas....*/
        if(u instanceof UsuarioAcademico && ((UsuarioAcademico)u).contarSesionesProgramadas()>0){
            /*Tiramos exception y lo registramos tambien en la auditoria :D*/
            auditoria.registrar(auth.getUsuarioActual(), TipoOperacion.ELIMINACION, "Usuario " + codigo + " tiene sesiones programadas asi que no se puede eliminar", false);
            throw new UsuarioConSesionesProgramadasException();
        }
        /*tambien miramos que no sea el ultimo admin reutilizamos tmb ahi**/
        /*Ahi con el get quitamos el codigo de usuario y listo chaolin pin pin*/
        revisarUltimoAdmin(u);
        sistema.getUsuarios().remove(codigo);
        /*Como siempre otro registro de auditoriaaa :O*/
        auditoria.registrar(auth.getUsuarioActual(), TipoOperacion.ELIMINACION, "Usuario " + codigo, true);
    }

    /*Ahora, para los archivos, creamos un metodo con el que podamos leer linea por linea, 
    separar los campos, identificar los roles y crear los usuarios :D*/

    public int cargarUsuariosDesdeArchivo(String rutaArchivo)
            throws UsuarioNoAutenticadoException, AccesoDenegadoException,UsuarioInactivoException, IOException {

        /*Primero verifiquemos que el usuario esté autenticado y tenga permisos :) */
        auth.validarOperacion(OpcionMenu.CARGAR_USUARIOS);

        int cargados = 0;
        int numeroLinea = 0;
        /*Convertimos la ruta a un Path, para representar la ubicacion del archivoooo */
        Path path = Path.of(rutaArchivo);

        try (BufferedReader br = Files.newBufferedReader(path)) {

            String linea;
            while ((linea = br.readLine()) != null) {
                numeroLinea++;
                //si la linea esta vacia, solo ignoramos y ya :)
                if (linea.trim().isEmpty()) {
                    continue;
                }

                try {
                    String[] partes = linea.split("\\*", -1);

                    if (partes.length != 6) {
                        throw new DatoInvalidoException("La línea debe tener 6 campos");
                    }

                    String codigo = partes[0].trim();
                    String nombre = partes[1].trim();
                    //Convertimos el rol a enum, y evitamos problemas con tildes y espacios 
                    Rol rol = Rol.valueOf(normalizarEnum(partes[2]));
                    String programa = partes[3].trim();
                    int semestre = Integer.parseInt(partes[4].trim());
                    String clave = partes[5].trim();

                    Usuario nuevo;

                    //Dependiendo del rol, creamos el usuario correspondiente
                    switch (rol) {

                        case ESTUDIANTE:
                            nuevo = new Estudiante(codigo, nombre, clave, programa, semestre);
                            break;

                        case MONITOR:
                            nuevo = new Monitor(codigo, nombre, clave, programa, semestre);
                            break;

                        case ADMINISTRADOR:
                            nuevo = new Administrador(codigo, nombre, clave);
                            break;
                        //ponemos un default por si acaso ;)
                        default:
                            throw new DatoInvalidoException("Rol de usuario no válido");
                    }
                    agregar(nuevo);
                    cargados++;

                } catch (IllegalArgumentException | DatoInvalidoException | CodigoDuplicadoException e) {
                    System.out.println("Error en la línea " + numeroLinea
                            + ": " + e.getMessage());
                }
            }
        }

        return cargados;
    }

}