package service;
import exceptions.CruceHorarioException;
import exceptions.DatoInvalidoException;
import exceptions.DuracionInvalidaException;
import exceptions.EquipoNoDisponibleException;
import exceptions.EquipoNoEncontradoException;
import exceptions.EstadoSesionInvalidoException;
import exceptions.LaboratorioException;
import exceptions.LimiteSesionesExcedidoException;
import exceptions.SemestreInsuficienteException;
import exceptions.SesionAjenaException;
import exceptions.SesionNoEncontradaException;
import exceptions.UsuarioInactivoException;
import exceptions.UsuarioNoEncontradoException;
import model.ConfiguracionSistema;
import model.Equipo;
import model.Sesion;
import model.SistemaLaboratorio;
import model.Usuario;
import model.UsuarioAcademico;
import model.enums.EstadoEquipo;
import model.enums.EstadoSesion;
import model.enums.EstadoUsuario;
import model.enums.OpcionMenu;
import model.enums.TipoOperacion;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


/*Bueno muchachos este es literal lo mas importante pq esta es la clase donde se desarrollan las sesiones
es larguisimo pero no tan crazy :D*/

public class ServicioSesiones{
    private SistemaLaboratorio sistema;
    private ServicioAutenticacion auth;
    private ServicioAuditoria auditoria;

    /*Constructor :D*/
    public ServicioSesiones(SistemaLaboratorio sistema, ServicioAutenticacion auth, ServicioAuditoria auditoria){
        this.sistema=sistema;
        this.auth=auth;
        this.auditoria=auditoria;
    }

    /* ==================== programar (opcion 8) ==================== */

    /*Empezemos por lo que es Programar la sesion que va a ser la opcion 8 del menu :D*/
    public Sesion programarSesion(String codigoEquipo, String codigoUsuario, LocalDateTime inicio, int duracionMinutos) throws LaboratorioException{

        /*Primero validamos la operacion que quien la opere este activo :D
        El metodo viene de autenticacion recuerden!*/
        auth.validarOperacion(OpcionMenu.PROGRAMAR_SESION);

        /*Nota: Si es un estudiante o monitor es para si mismo la sesion!!*/
        if(!auth.esAdministrador() && !auth.getUsuarioActual().getCodigo().equals(codigoUsuario)){
            /*Nota hice un metodo privado de cuando hay un fallo :D para registrarlo en la auditoria esta abajo de este metodo*/
            fallo(TipoOperacion.PROGRAMACION_SESION, "Intento de programar para otro usuario " + codigoUsuario);
            throw new SesionAjenaException("Solo puedes programar sesiones para ti :D");
        }

        /*El usuario tiene que ser academico y estar activo :D */
        Usuario u=sistema.getUsuarios().get(codigoUsuario);
        if(u==null){
            throw new UsuarioNoEncontradoException("No existe un usuario con el codigo " + codigoUsuario);
        }
        /*Aqui volvemos a usar instance of para verificar que sea usuario academico*/
        if(!(u instanceof UsuarioAcademico)){
            throw new DatoInvalidoException("Los adminis no pueden programar sesiones propias");
        }
        UsuarioAcademico usuario=(UsuarioAcademico)u;

        /*Si el usuario no esta activo*/
        if(!usuario.estaActivo()){
            /*Otra vez usamos fallo :D*/
            fallo(TipoOperacion.PROGRAMACION_SESION, "Usuario " + codigoUsuario + " esta " + usuario.getEstado());
            throw new UsuarioInactivoException("El usuario esta " + usuario.getEstado() + " y no puede programar sesiones");
        }

        /*Si el equipo no existe*/
        Equipo equipo=sistema.getEquipos().get(codigoEquipo);
        if(equipo==null){
            throw new EquipoNoEncontradoException("No existe un equipo con el codigo " + codigoEquipo);
        }
        /*Y no esta disponible...*/
        if(!equipo.estaDisponible()){
            /*Otra vez fallo para el registro*/
            fallo(TipoOperacion.PROGRAMACION_SESION, "Equipo " + codigoEquipo + " esta " + equipo.getEstado());
            throw new EquipoNoDisponibleException("El equipo esta " + equipo.getEstado());
        }

        /*Si no llega al semestre minimo que necesita :O*/
        if(usuario.getSemestre()<equipo.getSemestreMinimo()){
            /*Otra vez fallo pal registro*/
            fallo(TipoOperacion.PROGRAMACION_SESION, "Semestre " + usuario.getSemestre() + " es menor al minimo: " + equipo.getSemestreMinimo());
            throw new SemestreInsuficienteException("El equipo pide semestre " + equipo.getSemestreMinimo() + " y el usuario esta en " + usuario.getSemestre());
        }

        /* Si la fecha no esta despues de :o */
        /*Recuerden que usamos LocalDateTime y el .now y el .before */
        if(inicio==null || inicio.isBefore(LocalDateTime.now())){
            throw new DatoInvalidoException("La sesion tiene que ser en el futuro");
        }
        /*Aqui chicos tambien cree un metodo private para validar la duracion tambien esta despues de este metodo :D*/
        validarDuracion(usuario, duracionMinutos);

        /*con el getter de la configuracion la traemos y la guardamos :D*/
        ConfiguracionSistema config=sistema.getConfiguracion();

        /*Y si se llega al maximo de sesiones programadas*/
        if(usuario.contarSesionesProgramadas()>=usuario.getMaxSesiones(config)){
            /*Otra vez con fallo para el registro :D*/
            fallo(TipoOperacion.PROGRAMACION_SESION, "Usuario " + codigoUsuario + " llego al maximo de sesiones");
            throw new LimiteSesionesExcedidoException("Ya tiene el maximo de " + usuario.getMaxSesiones(config) + " sesiones programadas, sorry :(");
        }

        /* Tambien toca ver si se cruzan otras sesiones del mismo equipo con esta aqui tambien cree una
        funcion private de revisar cruzes :D*/
        revisarCruces(equipo, inicio, duracionMinutos, null);

        /*Si ya finalmente todo esta bien! se crea la sesion
        - Se mete en el historial del equipo
        - Se mete en el historial del usuario*/

        Sesion sesion=new Sesion(sistema.generarCodigoSesion(), equipo, usuario, inicio, duracionMinutos);
        sistema.getSesiones().put(sesion.getCodigo(), sesion);
        /*Y en la auditoria lo agregamos: */
        auditoria.registrar(auth.getUsuarioActual(), TipoOperacion.PROGRAMACION_SESION,
                "Sesion " + sesion.getCodigo() + " equipo " + codigoEquipo + " usuario " + codigoUsuario, true);
        return sesion;
    }

    /*Nota hice un metodo privado de cuando hay un fallo :D para registrarlo en la auditoria*/
    private void fallo(TipoOperacion tipo, String detalle){
        auditoria.registrar(auth.getUsuarioActual(), tipo, detalle, false);
    }

    /*Y aqui la private de validar Duracion :D que tambien nos ayuda pa arribita :D
    -
    */
    private void validarDuracion(UsuarioAcademico usuario, int duracionMinutos) throws DuracionInvalidaException{
        /*Empezamos con los getters de configuracion para la configuracion
        minima y maxima
        - luego miramos que la duracion sea mayor que el minimo y viceversa con el max*/
        ConfiguracionSistema config=sistema.getConfiguracion();
        int minimo=config.getDuracionMinima();
        int maximo=usuario.getDuracionMaxima(config);
        if(duracionMinutos<minimo || duracionMinutos>maximo){
            /*Aqui si ocurre eso registramos again con la funcion fallo y mandamos la excepcion*/
            fallo(TipoOperacion.PROGRAMACION_SESION, "Duracion " + duracionMinutos + " fuera de " + minimo + " a " + maximo);
            throw new DuracionInvalidaException("La duracion tiene que estar entre " + minimo + " y " + maximo + " minutos");
        }
    }

    /*Aqui la private de validar cruces que nos sirve pa arribita :D*/
    private void revisarCruces(Equipo equipo, LocalDateTime inicio, int duracion, Sesion ignorar) throws CruceHorarioException{
        /*Con el for each miramos con el if reutilizando el metodo de la clase sesion*/
        for(Sesion otra : equipo.getSesiones()){
            if(otra!=ignorar && otra.seCruzaCon(inicio, duracion)){
                fallo(TipoOperacion.PROGRAMACION_SESION, "Hay un cruce con la sesion " + otra.getCodigo());
                /*Mandamos la excepcion*/
                throw new CruceHorarioException("El equipo ya tiene la sesion " + otra.getCodigo() + " en ese horario");
            }
        }
    }

    /*Ahora vamos con el metodo de cancelar sesion que es la funcion #9*/
    public void cancelarSesion(String codigo) throws LaboratorioException{
        /*Tmb empezamos validando la operacion
        - Buscamos la sesion, tambien cree un metodo de buscar la sesion
        - Tambien cree un metodo private de verificarProiedad
        para verificar que le pertenezca la sesion :D
        - Y finalmente se cancela */
        auth.validarOperacion(OpcionMenu.CANCELAR_SESION);
        Sesion s=buscarSesion(codigo);
        verificarPropiedad(s);
        s.cancelar();
        /*Al final registramos tambien :D*/
        auditoria.registrar(auth.getUsuarioActual(), TipoOperacion.CANCELACION_SESION, "Sesion cancelada: " + codigo, true);
    }

    /*Aqui esta la de buscar sesion :D*/
    public Sesion buscarSesion(String codigo) throws SesionNoEncontradaException{
        Sesion s=sistema.getSesiones().get(codigo);
        if(s==null){
            throw new SesionNoEncontradaException("No existe una sesion con el codigo " + codigo);
        }
        return s;
    }

    /* Nota:
    La propiedad de la sesion depende
    - Estudiantes y Monitories solo sesiones propias
    - El admin tecnicamente le pertenecen todas
    */
    private void verificarPropiedad(Sesion s) throws SesionAjenaException{
        /*Si hacemos la verificacion y no es admin y difiere registramos otra
         vez con la funcion fallo y mandamos la excepcion*/
        if(!auth.esAdministrador() && s.getUsuario()!=auth.getUsuarioActual()){
            fallo(TipoOperacion.ACCESO_DENEGADO, "Intento de operar la sesion ajena " + s.getCodigo());
            throw new SesionAjenaException();
        }
    }

    /*Ahora vamos con el cierre de la sesion*/
    public double cerrarSesion(String codigo, LocalDateTime finReal, String observaciones, boolean dano) throws LaboratorioException{
        /*Validamos la operacion*/
        auth.validarOperacion(OpcionMenu.CERRAR_SESION_USO);
        /*Tambien utilizamos el de buscar sesion y verificar la propiedad*/
        Sesion s=buscarSesion(codigo);
        verificarPropiedad(s);
        /*Verificamos que se vaya a finalizar y que el fin sea despues del inicio
        si no pss lanzamos la excepcion*/
        if(finReal==null || !finReal.isAfter(s.getInicio())){
            throw new DatoInvalidoException("La hora de cierre tiene que ser despues del inicio");
        }

        /*Configuramos la configuracion valga redundancia*/
        ConfiguracionSistema config=sistema.getConfiguracion();

        /*Ahora usamos con usuario.calcularpenalizacion (esto chicos es con polimorfismo)*/
        double multa=s.cerrar(finReal, observaciones, dano, config);

        UsuarioAcademico usuario=s.getUsuario();
        Equipo equipo=s.getEquipo();

        /*aqui reutilizamos registrar uso*/
        equipo.registrarUso(s.getHorasReales());

        /*Si hay multa agregamos la penalizacion
         y si se pasa del umbral se suspende*/
        if(multa>0){
            usuario.agregarPenalizacion(multa);
            if(usuario.getPenalizacionAcumulada()>config.getUmbralSuspension() && usuario.getEstado()==EstadoUsuario.ACTIVO){
                /*Aqui dejamos el estado suspendido*/
                usuario.setEstado(EstadoUsuario.SUSPENDIDO);
                /*Y registramos en la auditoria :D*/
                auditoria.registrar(auth.getUsuarioActual(), TipoOperacion.CAMBIO_ESTADO, "Usuario " + usuario.getCodigo() + " suspendido automaticamente por multas", true);
            }
        }

        /* Ahora bien si hay daño reportdado tenemos que pasarlo a en mantenimiento */
        if(dano){
            equipo.setEstado(EstadoEquipo.EN_MANTENIMIENTO);
            /*registramos tambien*/
            auditoria.registrar(auth.getUsuarioActual(), TipoOperacion.CAMBIO_ESTADO, "Equipo " + equipo.getCodigo() + " a mantenimiento por dano reportado", true);
        }

        /*Finalmente registramos toda la info y retornamos la multa*/
        auditoria.registrar(auth.getUsuarioActual(), TipoOperacion.CIERRE_SESION, "Sesion cerrada" + codigo + " multa " + multa, true);
        return multa;
    }

    /*Ahora vamos con el historial del usuario*/
    public List<Sesion> historialDeUsuario(String codigoUsuario) throws LaboratorioException{
        auth.verificarAcceso(OpcionMenu.HISTORIAL_SESIONES);
        /*Funciona la logica igual si no es admin solo uno puede ver su propio historial*/
        if(!auth.esAdministrador() && !auth.getUsuarioActual().getCodigo().equals(codigoUsuario)){
            throw new SesionAjenaException("Solo puedes ver tu propio historial");
        }
        Usuario u=sistema.getUsuarios().get(codigoUsuario);
        if(u==null){
            /*Aqui el caso de que no haya usuario encontrado*/
            throw new UsuarioNoEncontradoException("No existe un usuario con el codigo " + codigoUsuario);
        }
        if(!(u instanceof UsuarioAcademico)){
            /*Verificamos con el instanceof que sea usuario academico, si es un admin mandamos la excepcion*/
            throw new DatoInvalidoException("Los administradores no tienen sesiones propias");
        }
        /*Retornamos el arraylist con todo el historial*/
        return new ArrayList<>(((UsuarioAcademico)u).getSesiones());
    }

    /*Ahora vamos con el metodo de historial del Equipo :D
    Es parecido pero aca lo que pasa es verificamos el acceso, miramos que se
    pueda acceder al equipo y creamos un nuevo arraylist para las sesiones del equipo*/

    public List<Sesion> historialDeEquipo(String codigoEquipo) throws LaboratorioException{
        auth.verificarAcceso(OpcionMenu.HISTORIAL_SESIONES);
        Equipo e=sistema.getEquipos().get(codigoEquipo);
        if(e==null){
            throw new EquipoNoEncontradoException("No existe un equipo con el codigo " + codigoEquipo);
        }
        List<Sesion> resultado=new ArrayList<>();
        for(Sesion s : e.getSesiones()){
            /*Verificamos con el if si es administrador o si la sesion es la
            del usuario :D*/
            if(auth.esAdministrador() || s.getUsuario()==auth.getUsuarioActual()){
                resultado.add(s);
            }
        }
        return resultado;
    }

    /*Ahora todo lo  que hace el admin es lo que falta aca en los metodos
    - Uno de listar todas las sesiones
    - Uno de modificarlas
    */

    /*Metodo de listarlas: simplemente retornamos el arraylist con el getter de las sesiones*/
    public List<Sesion> listarSesiones() throws LaboratorioException{
        auth.verificarAcceso(OpcionMenu.GESTION_ADMINISTRACION);
        return new ArrayList<>(sistema.getSesiones().values());
    }

    /*El metodo de modificarla :D*/
    public void modificarSesion(String codigo, LocalDateTime nuevoInicio, int nuevaDuracion) throws LaboratorioException{
        auth.validarOperacion(OpcionMenu.GESTION_ADMINISTRACION);
        /*Reutilizamos buscando la sesion*/
        Sesion s=buscarSesion(codigo);
        /*verificamos que la sesion este programada*/
        if(s.getEstado()!=EstadoSesion.PROGRAMADA){
            throw new EstadoSesionInvalidoException("Solo se puede modificar una sesion programada :o");
        }
        /*Verificamos la sesion sea en el futuro si se modifica el inicio*/
        if(nuevoInicio==null || nuevoInicio.isBefore(LocalDateTime.now())){
            throw new DatoInvalidoException("La sesion tiene que ser en el futuro :V");
        }
        /*Y yap reutilizamos el metodo de validar la duracion :D*/
        validarDuracion(s.getUsuario(), nuevaDuracion);
        /*Ah y tambien se revisa no se cruce el nuevo inicio y la nueva duracion de la sesion :D*/
        revisarCruces(s.getEquipo(), nuevoInicio, nuevaDuracion, s);
        /*Y del resto todo queda seteado :D*/
        s.setInicio(nuevoInicio);
        s.setDuracionMinutos(nuevaDuracion);
        /*Finalmente agregamos a auditoria*/
        auditoria.registrar(auth.getUsuarioActual(), TipoOperacion.MODIFICACION, "Sesion " + codigo, true);
    }

}
