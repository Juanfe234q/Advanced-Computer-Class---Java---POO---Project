package service;
import exceptions.AccesoDenegadoException;
import exceptions.CredencialesInvalidasException;
import exceptions.CuentaBloqueadaException;
import exceptions.SesionYaIniciadaException;
import exceptions.UsuarioInactivoException;
import exceptions.UsuarioNoAutenticadoException;
import model.SistemaLaboratorio;
import model.Usuario;
import model.enums.EstadoUsuario;
import model.enums.OpcionMenu;
import model.enums.Rol;
import model.enums.TipoOperacion;

/*So, chicos esta clase lo que hace es manejar
el logerase:
- Login
-Logout
. Ah y tambien los permismos :D*/
/**
 * 
 * ServicioAutenticacion
 */
public class ServicioAutenticacion{
    private SistemaLaboratorio sistema;
    private ServicioAuditoria auditoria;
    private Usuario usuarioActual;

    /*Entonces como siempre constructor*/
    /**
     * 
     * @param sistema
     * @param auditoria
     */
    public ServicioAutenticacion(SistemaLaboratorio sistema, ServicioAuditoria auditoria){
        this.sistema=sistema;
        this.auditoria=auditoria;
        this.usuarioActual=null;
    }

    

    /**
     * Ahora el metodo de Login
     So, para iniciar sesion con su codigo y clave

      - Importante:
        hay un contador de intentos fallidos si llega al maximo se bloquea la cuenta
      - El usuario Suspendido si puede ingresar pero no puede programar sesiones
      - Un usuario bloqueado o inactivo si no puede ni entrar
     * @param codigo
     * @param clave
     * @return
     * @throws SesionYaIniciadaException si ya existe una sesión activa en el sistema
     * @throws CredencialesInvalidasException si el codigo no existe o la contraseña no coincide
     * @throws CuentaBloqueadaException  si la cuenta ya estaba bloqueada o se bloquea tras agotar los intentos
     * @throws UsuarioInactivoException si la cuenta fue desactivada por un administrador
     */

    public Usuario login(String codigo, String clave)
        /*Hay una excepcion si ya ingreso - Otra si estan mal los datos - Otra si esta bloqueada la cuenta y la de Inactivo*/
            throws SesionYaIniciadaException, CredencialesInvalidasException, CuentaBloqueadaException, UsuarioInactivoException {
                if(usuarioActual != null) {
                    /*Aqui otra exception por si quiere logarse y ya esta logeado*/
                    throw new SesionYaIniciadaException("Ya hay una sesion iniciada :D");
                }
                /*Ahora vamos a buscar el codigo con el getter*/
                Usuario u = sistema.getUsuarios().get(codigo);

                /*Situacion 1:
                - No hay ese usuario y se registra en servicio auditoria, asi se tiene el historial del sistema
                */
                if(u==null){
                    /*Chicos esto lo dije arriba pero para que no se les olvide! auditoria.registrar sale de nuestra clase Servicio Auditoria!
                    tambien manda la excepcion*/
                    auditoria.registrar(null, TipoOperacion.LOGIN_FALLIDO, "Codigo " + codigo + " no existe", false);
                    throw new CredencialesInvalidasException();
                }

                /*Segunda situacion:
                - Esta bloqueada no puede hacer nada, tambien va con la clase servicio auditoria y manda la excepcion*/
                if(u.getEstado()==EstadoUsuario.BLOQUEADO){
                    auditoria.registrar(u, TipoOperacion.LOGIN_FALLIDO, "Intento con cuenta bloqueada", false);
                    throw new CuentaBloqueadaException();
                }

                /*Siguiente caso:
                El usuario esta inactivo, igual que si esta bloqueada y manda la excepcion*/
                if(u.getEstado()==EstadoUsuario.INACTIVO){
                    auditoria.registrar(u, TipoOperacion.LOGIN_FALLIDO, "Intento con cuenta desactivada", false);
                    throw new UsuarioInactivoException("La cuenta esta desactivada");
                }

                /*Ahora bien! CON LA CLAVE! :D*/

                /* Si la clave es incorrecta le sumamos el intento y se revisa si toca bloquear */
                if(!u.verificarClave(clave)){
                    u.registrarIntentoFallido();
                    int maximo=sistema.getConfiguracion().getMaxIntentosLogin();
                    /*Aqui todo se registra con el de auditoria tambien!*/
                    auditoria.registrar(u, TipoOperacion.LOGIN_FALLIDO, "Clave incorrecta intento " + u.getIntentosFallidos() + " de " + maximo, false);

                    /*Aca vemos si toca bloquear*/
                    if(u.getIntentosFallidos()>=maximo){
                        u.setEstado(EstadoUsuario.BLOQUEADO);
                        /*En dado caso tambien lo registramos con la auditoria*/
                        auditoria.registrar(u, TipoOperacion.CAMBIO_ESTADO, "Cuenta bloqueada por intentos fallidos", true);
                        throw new CuentaBloqueadaException("Te pasaste de intentos y la cuenta quedo bloqueada :(");
                    }
                    /*Aqui pues si no se pado mandamos la excepcion y los intentos que le quedan :D*/
                    throw new CredencialesInvalidasException("Clave incorrecta te quedan " + (maximo - u.getIntentosFallidos()) + " intentos");
                }

                /* Y si la clave estuvo bien pues todo good, una vez
                iniciamos sesion si puso nose, 1 vez mal la contraseña
                y a la segunda ya todo good, se reinician una vez logeado todos los intentos
                */
                u.reiniciarIntentos();
                usuarioActual=u;
                /*Tambien registramos que se inicia sesion en la auditoria*/
                auditoria.registrar(u, TipoOperacion.LOGIN, "Hubo un inicio de sesion", true);
                return u;
            }
            /*-----------------------------------------------------------------------------------------------------------------------------------------------------------*/
    /*Ahora vamos con el cierre de sesion!*/

    /*Metodo de cerrar sesion :D*/
    /**
     * 
     * @throws UsuarioNoAutenticadoException  si no hay ningun usuario con sesión iniciada
     */
    public void logout() throws UsuarioNoAutenticadoException{
        if(usuarioActual==null){
            /*Si no se a logeado y intenta cerrar sesion pues manda la excepcion */
            throw new UsuarioNoAutenticadoException("No hay ninguna sesion para cerrar :0");
        }
        /*Ahora si la cerramos :D*/
        auditoria.registrar(usuarioActual, TipoOperacion.LOGOUT, "Cierre de sesion", true);
        usuarioActual=null;
    }

    /**
     * Ahora un metodo de verificar el acceso a las opciones segun cada uno de los roles
       si no lo deja en la autoria lo dejamos como que el acceso esta denegado
     * @param opcion la que se escoje en el menu
     * @throws UsuarioNoAutenticadoException si no hay sesion iniciada
     * @throws AccesoDenegadoException si el rol del usuario no tiene permisos
     */
    public void verificarAcceso(OpcionMenu opcion) throws UsuarioNoAutenticadoException, AccesoDenegadoException{
        /*Entonces vamos una por una:
        - Logearse (Iniciar sesion y salir por todos see puede usar) */
        if(opcion==OpcionMenu.AUTENTICACION || opcion==OpcionMenu.SALIR){
            return;
        }
        /*Pero tambien si no se ha logeado toca mandar el de ah pss intento hacerlo sin logearse*/
        if(usuarioActual==null){
            auditoria.registrar(null, TipoOperacion.ACCESO_DENEGADO, "Intento de usar " + opcion + " sin iniciar sesion", false);
            throw new UsuarioNoAutenticadoException();
        }
        /*Y el de cada uno es mas facil, porque reutilizamos el metodo que estaba en la clase Usuario y  yap igual que siempre
        se registra en la auditoria :D*/
        if(!usuarioActual.puedeAcceder(opcion)){
            /*Lo registramos :D*/
            auditoria.registrar(usuarioActual, TipoOperacion.ACCESO_DENEGADO, "Sin permiso para " + opcion, false);
            throw new AccesoDenegadoException("Tu rol no tiene permiso para " + opcion.getTexto());
        }
    }
    
    /**
     * Ahora otro metodo de validar que la operacion se pueda hacer es lit como verificar acceso
       reutilizando el metodo de esta Activo de Usuario :D
     * @param opcion elegida en el menu
     * @throws UsuarioNoAutenticadoException si no hay sesión iniciada
     * @throws AccesoDenegadoException  si el rol del usuario no está autorizado
     * @throws UsuarioInactivoException  si el estado del usuario no es activo
     */
    public void validarOperacion(OpcionMenu opcion)
            throws UsuarioNoAutenticadoException, AccesoDenegadoException, UsuarioInactivoException{
        verificarAcceso(opcion);
        if(!usuarioActual.estaActivo()){
            auditoria.registrar(usuarioActual, TipoOperacion.ACCESO_DENEGADO, "Usuario " + usuarioActual.getEstado() + " intento " + opcion, false);
            throw new UsuarioInactivoException("Tu cuenta esta " + usuarioActual.getEstado() + " y no puedes hacer esto");
        }
    }

   
    /**
     * Metodo para ver si hay alguien concetado ahorita la idea es esto usarlo en menu
     * @return true si hay un usuario autenticado, false si no.
     */
    public boolean hayUsuarioAutenticado(){
        return usuarioActual!=null;
    }


    /**
     * Y yap el ultimo es el de verificar es administrador :D
     * @return true si es admin y tiene sesion activa
     */
    public boolean esAdministrador(){
        return usuarioActual!=null && usuarioActual.getRol()==Rol.ADMINISTRADOR;
    }

   
    /**
     * Y otro metodo para ver el usuario actual
     * @return el usuario autenticado o null si no hay sesion iniciada
     */
    public Usuario getUsuarioActual(){
        return usuarioActual;
    }
}
