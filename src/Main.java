/*Pimero importamos las clases necesarias para poder trabajar :) */
import java.util.List;
import java.util.Scanner;

import exceptions.CredencialesInvalidasException;
import exceptions.CuentaBloqueadaException;
import exceptions.SesionYaIniciadaException;
import exceptions.UsuarioInactivoException;
import exceptions.UsuarioNoAutenticadoException;
import model.SistemaLaboratorio;
import model.enums.OpcionMenu;
import service.ServicioAuditoria;
import service.ServicioAutenticacion;
import service.ServicioEquipos;
import service.ServicioInformes;
import service.ServicioSesiones;
import service.ServicioUsuarios;
import ui.LectorConsola;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.LocalDate;
import model.RegistroAuditoria;
import model.enums.TipoOperacion;
import persistence.ExportadorInformes;
import exceptions.AccesoDenegadoException;
import exceptions.CodigoDuplicadoException;
import model.enums.TipoEquipo;
import persistence.GestorPersistencia;
import model.enums.EstadoEquipo;
import model.Usuario;
import model.Estudiante;
import model.Laboratorio;
import model.Equipo; 
import model.Monitor;
import model.enums.Rol;
import model.Sesion;

/*Conectamos el menu principal con el mainnn */
import ui.MenuPrincipal;
import model.Administrador;
import exceptions.DatoInvalidoException;
import exceptions.LaboratorioException;

public class Main {

    public static void main(String[] args) {
        
        //Iniciamos creando el sistema principal, o sea, donde almacenamos toda la info :)
        SistemaLaboratorio sistema = new SistemaLaboratorio();

        //Ahora creamos los servicios que nos ayudaran a manejar la info del sistema
        ServicioAuditoria servicioAuditoria = new ServicioAuditoria(sistema);
        ServicioAutenticacion servicioAutenticacion = new ServicioAutenticacion(sistema, servicioAuditoria);        
        ServicioUsuarios servicioUsuarios = new ServicioUsuarios(sistema, servicioAutenticacion, servicioAuditoria);
        ServicioEquipos servicioEquipos = new ServicioEquipos(sistema, servicioAutenticacion, servicioAuditoria);
        ServicioSesiones servicioSesiones = new ServicioSesiones(sistema, servicioAutenticacion, servicioAuditoria);
        ServicioInformes servicioInformes = new ServicioInformes(sistema);
        ExportadorInformes exportador = new ExportadorInformes(servicioInformes);
        GestorPersistencia gestorPersistencia = new GestorPersistencia();

        //Finalmente hacemos un mensajito para confirmar que el codigo corra bien :)
        System.out.println("Sistema de Laboratorio iniciado correctamente.");

        /* Creamos Scanner para recibir las opciones que ingrese el usuario */
        Scanner entrada = new Scanner(System.in);
        LectorConsola lector = new LectorConsola(entrada);

        /* Verificamos si necesitamos crear el administrador inicial */
        crearAdministradorInicial(sistema, servicioUsuarios, entrada);

        MenuPrincipal menu = new MenuPrincipal(servicioAutenticacion, lector);
        
        boolean continuar = true; 

        while (continuar) {

            menu.mostrarMenu();
            OpcionMenu opcion=menu.leerOpcion(); 

            if(opcion==null){
                continue;
            }

            try {

                switch (opcion) {

                    //opcion 1
                    case AUTENTICACION:{

                        if (!servicioAutenticacion.hayUsuarioAutenticado()) {

                            /* Pedimos la info para iniciar sesión :)*/
                            String codigo = lector.leerTexto("Ingrese su codigo: ");
                            String clave = lector.leerTexto("Ingrese su clave: ");

                            servicioAutenticacion.login(codigo, clave);
                            System.out.println("Inicio de sesion exitoso");


                        } else {

                            /* Si ya hay un usuario conectado, cerramos su sesión */
                            servicioAutenticacion.logout();

                            System.out.println("Sesion cerrada correctamente.");
                        }

                        break;
                    }
                    //opcion 2
                    case REGISTRAR_USUARIO: {

                        System.out.println("\nREGISTRO DE USUARIO");

                        String codigo = lector.leerTexto("Codigo del usuario: ");
                        String nombre = lector.leerTexto("Nombre completo: ");
                        String clave = lector.leerTexto("Clave: ");

                        String rolTexto = lector.leerTexto(
                                "Rol (ESTUDIANTE, MONITOR, ADMINISTRADOR): "
                        );

                        try {

                            Rol rol = Rol.valueOf(rolTexto.trim().toUpperCase());

                            Usuario nuevo;

                            switch (rol) {

                                case ESTUDIANTE: {

                                    String programa = lector.leerTexto("Programa academico: ");
                                    int semestre = lector.leerEntero("Semestre: ");

                                    nuevo = new Estudiante(
                                            codigo, nombre, clave, programa, semestre
                                    );

                                    break;
                                }

                                case MONITOR: {

                                    String programa = lector.leerTexto("Programa academico: ");
                                    int semestre = lector.leerEntero("Semestre: ");

                                    nuevo = new Monitor(
                                            codigo, nombre, clave, programa, semestre
                                    );

                                    break;
                                }

                                case ADMINISTRADOR: {

                                    nuevo = new Administrador(
                                            codigo, nombre, clave
                                    );

                                    break;
                                }

                                default:
                                    throw new IllegalArgumentException("Rol no valido.");
                            }

                            servicioUsuarios.registrarUsuario(nuevo);

                            System.out.println("\nUsuario registrado correctamente.");

                        } catch (CodigoDuplicadoException | DatoInvalidoException e) {

                            System.out.println("Error al registrar usuario: " + e.getMessage());

                        } catch (IllegalArgumentException e) {

                            System.out.println("Error: el rol ingresado no es valido.");

                        }

                        break;
                    }
                    //opcion 3
                    case REGISTRAR_EQUIPO: {

                        System.out.println("\nREGISTRO DE EQUIPO");

                            // Solicitamos los datos del equipo
                        String codigo = lector.leerTexto("Codigo del equipo: ");
                        String nombre = lector.leerTexto("Nombre del equipo: ");
                        String tipoTexto = lector.leerTexto("Tipo de equipo (FABRICACION, MEDICION, ELECTRONICA, ROBOTICA, COMPUTO, OPTICA, MECANICA): ");
                        String laboratorio = lector.leerTexto("Nombre del laboratorio: ");
                        String estadoTexto = lector.leerTexto("Estado (DISPONIBLE, EN_USO, EN_MANTENIMIENTO, DADO_DE_BAJA): ");
                        String valorTexto = lector.leerTexto("Valor del equipo: ");
                        int semestreMinimo = lector.leerEntero("Semestre minimo: ");                            String descripcion = lector.leerTexto("Descripcion del equipo: ");

                        try {

                            // Convertimos los textos a sus respectivos enums
                            TipoEquipo tipo = TipoEquipo.valueOf(tipoTexto.trim().toUpperCase());
                            EstadoEquipo estado = EstadoEquipo.valueOf(estadoTexto.trim().toUpperCase());

                            // Convertimos el valor a double
                            double valor = Double.parseDouble(valorTexto);

                            // Registramos el equipo utilizando el servicio
                            servicioEquipos.registrarEquipo(codigo, nombre, tipo, laboratorio, estado, valor, semestreMinimo, descripcion );
                            System.out.println("\nEquipo registrado correctamente.");

                        } catch (CodigoDuplicadoException | DatoInvalidoException e) {

                            System.out.println("Error al registrar el equipo: " + e.getMessage());

                        } catch (IllegalArgumentException e) {

                            System.out.println("Error: El tipo, estado o valor ingresado no es valido.");

                        }

                        break;
                    }
                    //opcion 4
                    case CARGAR_EQUIPOS:{

                        String rutaEquipos = lector.leerTexto("Ingrese la ruta del archivo de equipos: ");

                        int equiposCargados = servicioEquipos.cargarEquiposDesdeArchivo(rutaEquipos);

                        System.out.println("\nCarga de equipos finalizada.");
                        System.out.println("\nEquipos cargados correctamente: " + equiposCargados);

                        break;
                    }
                    //opcion 5
                    case CARGAR_USUARIOS:{

                        String rutaUsuarios = lector.leerTexto("Ingrese la ruta del archivo de usuarios: ");

                        int usuariosCargados = servicioUsuarios.cargarUsuariosDesdeArchivo(rutaUsuarios);

                        System.out.println("\nCarga de usuarios finalizada.");
                        System.out.println("\nUsuarios cargados correctamente: " + usuariosCargados);

                        break;                        
                    }
                    //opcion 6
                    case GESTION_ADMINISTRACION: {

                        gestionarAdministracion(servicioUsuarios, servicioEquipos, lector);
                        break;   
                    }
                    //opcion 7
                    case ESTADO_LABORATORIO:{
                        consultarEstadoLaboratorio(servicioEquipos, lector);
                        break;
                    }
                    //opcion 8
                    case PROGRAMAR_SESION:{
                        programarSesion(servicioSesiones, lector);
                        break;             
                    }       
                    //opcion 9
                    case CANCELAR_SESION:{
                        cancelarSesion(servicioSesiones, lector);
                        break;
                    }
                    //opcion 10
                    case CERRAR_SESION_USO:{
                        cerrarSesion(servicioSesiones, lector);
                        break;
                    }
                    //opcion 11
                    case HISTORIAL_SESIONES:{
                        consultarHistorial(servicioSesiones, lector);
                        break;
                    }
                    //opcion 12
                    case INFORMES:{
                        mostrarInformes(servicioInformes,exportador, lector);
                        break;
                    }
                    //opcion 13
                    case AUDITORIA:{
                        consultarAuditoria(servicioAuditoria, lector);
                        break;
                    }
                    //opcion 14
                    case GUARDAR_RECUPERAR:{
                        
                        SistemaLaboratorio sistemaRecuperado = gestionarPersistencia(gestorPersistencia, sistema, lector);

                            if (sistemaRecuperado != sistema) {

                                sistema = sistemaRecuperado;
                                servicioAuditoria = new ServicioAuditoria(sistema);
                                servicioAutenticacion = new ServicioAutenticacion(sistema, servicioAuditoria);
                                servicioUsuarios = new ServicioUsuarios(sistema, servicioAutenticacion, servicioAuditoria);
                                servicioEquipos = new ServicioEquipos(sistema, servicioAutenticacion, servicioAuditoria);
                                servicioSesiones = new ServicioSesiones(sistema, servicioAutenticacion, servicioAuditoria);
                                servicioInformes = new ServicioInformes(sistema);
                                exportador = new ExportadorInformes(servicioInformes);
                                gestorPersistencia = new GestorPersistencia();
                                menu = new MenuPrincipal(servicioAutenticacion, lector);

                                System.out.println("Las referencias del sistema fueron actualizadas.");
                                System.out.println("Inicie sesion nuevamente.");
                            }

                        break;
                    }
                    //opcion 15
                    case SALIR:{
                        continuar = false;
                        System.out.println("Saliendo del sistema ;) ");
                        break;
                    }
                    default:
                        System.out.println("Error.");
                        break;
                }

            } catch (CredencialesInvalidasException | CuentaBloqueadaException |UsuarioInactivoException | SesionYaIniciadaException e) {
                System.out.println("Error de autenticacion: " + e.getMessage());

            } catch (UsuarioNoAutenticadoException e) {
                System.out.println("No hay un usuario autenticado.");
            }

            catch (AccesoDenegadoException | IOException e) {
                System.out.println("Error al cargar el archivo: " + e.getMessage());

            }
        }
        /* Cerramos la entrada al terminar el programa */
        entrada.close();
    }

    /* Creamos el administrador inicial únicamente cuando todavía no existe */
    private static void crearAdministradorInicial(SistemaLaboratorio sistema, ServicioUsuarios servicioUsuarios, Scanner entrada) {

        if (!servicioUsuarios.listarAdministradores().isEmpty()) {
            return;
        }

        System.out.println("\nCONFIGURACION INICIAL");
        System.out.println("No existe un administrador. \nCreemos uno :D.");

        boolean creado = false;

        while (!creado) {

            System.out.print("Codigo del administrador: ");
            String codigo = entrada.nextLine().trim();

            System.out.print("Nombre completo: ");
            String nombre = entrada.nextLine().trim();

            System.out.print("Clave: ");
            String clave = entrada.nextLine();

            try {
                /* Validamos los datos antes de guardar el administrador */
                Administrador admin = new Administrador(codigo, nombre, clave);

                if (codigo.isEmpty() || nombre.isEmpty() || clave.trim().isEmpty() || clave.contains("*")) {
                    throw new DatoInvalidoException("Los datos no pueden estar vacios ni contener * en la clave.");
                }

                servicioUsuarios.validarDatos(admin);

                if (sistema.getUsuarios().containsKey(codigo)) {
                    System.out.println("Ese codigo ya esta registrado.");
                    continue;
                }

                /* Al ser el primer administrador, lo agregamos directamente
                porque todavía no existe una sesión autenticada */
                sistema.getUsuarios().put(codigo, admin);

                creado = true;

                System.out.println("Administrador inicial creado correctamente.");
                System.out.println("Codigo de acceso: " + codigo);

            } catch (DatoInvalidoException e) {
                System.out.println("Datos invalidos: " + e.getMessage());
            }
        }
    }

    //Metodos para la opcion 6
    private static void mostrarMenuAdministracion() {

        System.out.println("GESTION DE ADMINISTRACION");

        System.out.println("1. Listar usuarios");
        System.out.println("2. Modificar nombre de usuario");
        System.out.println("3. Modificar datos academicos");
        System.out.println("4. Restablecer clave");
        System.out.println("5. Suspender usuario");
        System.out.println("6. Reactivar usuario");
        System.out.println("7. Desbloquear cuenta");
        System.out.println("8. Desactivar administrador");
        System.out.println("9. Eliminar usuario");

        System.out.println("10. Modificar equipo");
        System.out.println("11. Cambiar estado de equipo");
        System.out.println("12. Enviar equipo a mantenimiento");
        System.out.println("13. Dar de baja equipo");
        System.out.println("14. Eliminar equipo");

        System.out.println("0. Volver al menu principal");
    }

    private static void gestionarAdministracion(ServicioUsuarios servicioUsuarios, ServicioEquipos servicioEquipos, LectorConsola lector) {

        int opcionAdmin = -1;

        while (opcionAdmin != 0) {

            mostrarMenuAdministracion();
            opcionAdmin = lector.leerEntero("Seleccione una opcion: ");

            try {
                switch (opcionAdmin) {

                    // 1. LISTAR USUARIOS
                    case 1: {

                        System.out.println("\nLista De Usuarios");

                        for (Usuario u : servicioUsuarios.listarUsuarios()) {
                            System.out.println(u.obtenerInfo());
                        }
                        break;
                    }

                    // 2. MODIFICAR NOMBRE
                    case 2: {

                        System.out.println("\nModificar Nombre");

                        String codigo = lector.leerTexto("Codigo del usuario: ");
                        String nombre = lector.leerTexto("Nuevo nombre: ");

                        servicioUsuarios.modificarNombre(codigo, nombre);
                        System.out.println("Nombre modificado correctamente.");
                        break;
                    }

                    // 3. MODIFICAR DATOS ACADEMICOS
                    case 3: {

                        System.out.println("\nModificar Datos Academicos");

                        String codigo = lector.leerTexto("Codigo del usuario: ");
                        String programa = lector.leerTexto("Nuevo programa: ");
                        int semestre = lector.leerEntero("Nuevo semestre: ");

                        servicioUsuarios.modificarDatosAcademicos(codigo, programa, semestre);
                        System.out.println("Datos academicos modificados correctamente.");
                        break;
                    }

                    // 4. RESTABLECER CLAVE
                    case 4: {

                        System.out.println("\nRestablecer Clave");

                        String codigo = lector.leerTexto("Codigo del usuario: ");
                        String clave = lector.leerTexto("Nueva clave: ");

                        servicioUsuarios.restablecerClave(codigo, clave);
                        System.out.println("Clave restablecida correctamente.");
                        break;
                    }

                    // 5. SUSPENDER USUARIO
                    case 5: {

                        System.out.println("\nSuspender Usuario");

                        String codigo = lector.leerTexto("Codigo del usuario: ");
                        servicioUsuarios.suspender(codigo);
                        System.out.println("Usuario suspendido correctamente.");
                        break;
                    }

                    // 6. REACTIVAR USUARIO
                    case 6: {

                        System.out.println("\nReactivar Usuario");

                        String codigo = lector.leerTexto("Codigo del usuario: ");
                        servicioUsuarios.reactivar(codigo);
                        System.out.println("Usuario reactivado correctamente.");
                        break;
                    }

                    // 7. DESBLOQUEAR CUENTA
                    case 7: {

                        System.out.println("\nDesbloquear Cuenta");

                        String codigo = lector.leerTexto("Codigo del usuario: ");
                        servicioUsuarios.desbloquear(codigo);
                        System.out.println("Cuenta desbloqueada correctamente.");
                        break;
                    }

                    // 8. DESACTIVAR ADMINISTRADOR
                    case 8: {

                        System.out.println("\nDesactivar Administrador");

                        String codigo = lector.leerTexto("Codigo del administrador: ");
                        servicioUsuarios.desactivarAdministrador(codigo);
                        System.out.println("Administrador desactivado correctamente.");
                        break;
                    }

                    // 9. ELIMINAR USUARIO
                    case 9: {

                        System.out.println("\nEliminar Usuario");

                        String codigo = lector.leerTexto("Codigo del usuario: ");
                        servicioUsuarios.eliminarUsuario(codigo);
                        System.out.println("Usuario eliminado correctamente.");
                        break;
                    }

                    // 10. MODIFICAR EQUIPO
                    case 10: {

                        System.out.println("\nModificar Equipo");

                        String codigo = lector.leerTexto("Codigo del equipo: ");
                        String nombre = lector.leerTexto("Nuevo nombre: ");
                        TipoEquipo tipo = TipoEquipo.valueOf(lector.leerTexto("Tipo de equipo: ").trim().toUpperCase());
                        double valor = Double.parseDouble(lector.leerTexto("Nuevo valor: "));
                        int semestre = lector.leerEntero("Semestre minimo: ");
                        String descripcion = lector.leerTexto("Nueva descripcion: ");
                        servicioEquipos.modificarEquipo(codigo, nombre, tipo, valor, semestre, descripcion);
                        System.out.println("Equipo modificado correctamente.");
                        break;
                    }

                    // 11. CAMBIAR ESTADO
                    case 11: {

                        System.out.println("\nCambiar Estado Del Equipo");

                        String codigo = lector.leerTexto("Codigo del equipo: ");
                        EstadoEquipo estado = EstadoEquipo.valueOf(lector.leerTexto("Nuevo estado (DISPONIBLE, EN_USO, EN_MANTENIMIENTO, DADO_DE_BAJA): ").trim().toUpperCase());
                        servicioEquipos.cambiarEstado(codigo, estado);
                        System.out.println("Estado del equipo actualizado.");
                        break;
                    }

                    // 12. ENVIAR A MANTENIMIENTO
                    case 12: {

                        System.out.println("\nEnviar a Mantenimiento");

                        String codigo = lector.leerTexto("Codigo del equipo: ");
                        servicioEquipos.enviarAMantenimiento(codigo);
                        System.out.println("Equipo enviado a mantenimiento.");
                        break;
                    }

                    // 13. DAR DE BAJA
                    case 13: {

                        System.out.println("\nDar De Baja Equipos");

                        String codigo = lector.leerTexto("Codigo del equipo: ");
                        servicioEquipos.darDeBaja(codigo);
                        System.out.println("Equipo dado de baja correctamente.");
                        break;
                    }

                    // 14. ELIMINAR EQUIPO
                    case 14: {

                        System.out.println("\nEliminar Equipo");

                        String codigo = lector.leerTexto("Codigo del equipo: ");
                        servicioEquipos.eliminarEquipo(codigo);
                        System.out.println("Equipo eliminado correctamente.");
                        break;
                    }

                    case 0:

                        System.out.println("\nVolviendo al menu principal");
                        break;

                    default:

                        System.out.println("\nOpcion no valida.");
                        break;
                }

            } catch (Exception e) {

                System.out.println("\nNo se pudo realizar la operacion.");
                System.out.println("Motivo: " + e.getMessage());
            }
        }
    }

    //Metodos para la opcion7
    private static void consultarEstadoLaboratorio(ServicioEquipos servicioEquipos, LectorConsola lector) {

        int opcion;

        do {
            System.out.println("\nESTADO DE LABORATORIOS");
            System.out.println("1. Consultar todos los laboratorios");
            System.out.println("2. Consultar un laboratorio");
            System.out.println("0. Volver al menu principal");

            opcion = lector.leerEntero("Seleccione una opcion: ");

            try {
                switch (opcion) {

                    case 1:
                        for (Laboratorio lab : servicioEquipos.listarLaboratorios()) {
                            mostrarInformacionLaboratorio(lab);
                        }
                        break;

                    case 2:
                        String nombre = lector.leerTexto( "Ingrese el nombre del laboratorio: ");

                        Laboratorio lab = servicioEquipos.consultarLaboratorio(nombre);
                        mostrarInformacionLaboratorio(lab);
                        break;

                    case 0:
                        System.out.println("\nVolviendo al menu principal");
                        break;

                    default:
                        System.out.println("\nOpcion no valida.");
                }

            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }

        } while (opcion != 0);
    }

    private static void mostrarInformacionLaboratorio(Laboratorio lab) {

        System.out.println("\nLABORATORIO" + lab.getNombre());
        System.out.println("Inventario:");

        if (lab.getEquipos().isEmpty()) {
            System.out.println("No hay equipos registrados.");
        } else {
            for (Equipo equipo : lab.getEquipos()) {
                System.out.println(equipo.getCodigo()+" | "+equipo.getNombre()+" | "+equipo.getEstado());
            }
        }

        System.out.println("Cantidad Por Estado");

        for (EstadoEquipo estado : EstadoEquipo.values()) {
            System.out.println(estado + ": " + lab.contarPorEstado(estado));
        }

        System.out.println("\nValor total del inventario: "+ lab.calcularValorTotal());

        System.out.println("Equipos Con Restriccion");

        List<Equipo> restringidos = lab.obtenerEquiposConRestriccion();

        if (restringidos.isEmpty()) {
            System.out.println("No hay equipos con restriccion.");
        } else {
            for (Equipo equipo : restringidos) {
                System.out.println(equipo.getCodigo() + " | "+ equipo.getNombre() + " | Semestre minimo: "+ equipo.getSemestreMinimo());
            }
        }
    }

    //Metodos para la opcion 8
    private static void programarSesion(ServicioSesiones servicioSesiones, LectorConsola lector) {        
        System.out.println("\nPROGRAMAR SESIÓN");

        try {
            String codigoEquipo = lector.leerTexto("Código del equipo: ");
            String codigoUsuario = lector.leerTexto("Código del usuario: ");

            System.out.println("Fecha y hora de inicio (dd/MM/yyyy HH:mm): ");
            String fechaTexto = lector.leerTexto("Inicio: ");

            DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            LocalDateTime inicio = LocalDateTime.parse(fechaTexto, formato);
            int duracion = lector.leerEntero("Duración en minutos: ");
            Sesion sesion = servicioSesiones.programarSesion(codigoEquipo, codigoUsuario, inicio, duracion);

            System.out.println("Sesión programada correctamente :D");
            System.out.println("Código de sesión: " + sesion.getCodigo());

        } catch (DateTimeParseException e) {
            System.out.println("Formato de fecha incorrecto. Usa dd/MM/yyyy HH:mm.");

        } catch (LaboratorioException e) {
            System.out.println("No fue posible programar la sesión: " + e.getMessage());
        }

    }

    //Metdodo para la opcion 9
    private static void cancelarSesion(ServicioSesiones servicioSesiones, LectorConsola lector) {

        System.out.println("\nCANCELAR SESIÓN");

        try {
            String codigo = lector.leerTexto("Código de la sesión que deseas cancelar: ");
            servicioSesiones.cancelarSesion(codigo);
            System.out.println("\n¡Sesión cancelada correctamente!");

        } catch (LaboratorioException e) {
            System.out.println("No fue posible cancelar la sesión: " + e.getMessage());
        }
    }

    //Metodo para la opcion 10
    private static void cerrarSesion(ServicioSesiones servicioSesiones, LectorConsola lector) {

        System.out.println("\nCERRAR SESION DE USO");

        try {
            String codigo = lector.leerTexto("Codigo de la sesion: ");
            System.out.println("Ingrese la fecha y hora de finalizacion.");
            System.out.println("Formato: dd/MM/yyyy HH:mm");
            String fechaTexto = lector.leerTexto("Fecha y hora: ");
            DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            LocalDateTime finReal = LocalDateTime.parse(fechaTexto, formato);
            String observaciones = lector.leerTexto("Observaciones del uso: ");

            String respuesta = lector.leerTexto("¿Se reportaron danos? (SI/NO): ");

            boolean dano = respuesta.equalsIgnoreCase("SI");
            double penalizacion = servicioSesiones.cerrarSesion(codigo, finReal, observaciones, dano);

            System.out.println("\nSesion cerrada correctamente.");
            System.out.println("Penalizacion generada: " + penalizacion);

        } catch (DateTimeParseException e) {

            System.out.println("Formato de fecha incorrecto.");

        } catch (Exception e) {

            System.out.println("No fue posible cerrar la sesion: " + e.getMessage());
        }
    }

    //Metodo para la opcion 11
    private static void consultarHistorial(ServicioSesiones servicioSesiones, LectorConsola lector) {

        System.out.println("\nHISTORIAL DE SESIONES");
        System.out.println("1. Consultar historial de un usuario");
        System.out.println("2. Consultar historial de un equipo");

        int opcion = lector.leerEntero("Seleccione una opcion: ");

        try {

            List<Sesion> historial;

            switch (opcion) {

                case 1:

                    String codigoUsuario = lector.leerTexto("Codigo del usuario: ");
                    historial = servicioSesiones.historialDeUsuario(codigoUsuario);
                    break;

                case 2:

                    String codigoEquipo =lector.leerTexto("Codigo del equipo: ");
                    historial = servicioSesiones.historialDeEquipo(codigoEquipo);
                    break;

                default:
                    System.out.println("Opcion no valida.");
                    return;
            }

            if (historial.isEmpty()) {
                System.out.println("\nNo se encontraron sesiones.");
                return;
            }

            System.out.println("RESULTADOS");

            for (Sesion sesion : historial) {
                System.out.println(sesion.obtenerInfo());
            }

        } catch (Exception e) {

            System.out.println("No fue posible consultar el historial: " + e.getMessage());
        }
    }

    //Metodo para la opcion 12

    private static void mostrarInformes(ServicioInformes servicioInformes, ExportadorInformes exportador, LectorConsola lector) {

        System.out.println("\nINFORMES Y RANKINGS");
        System.out.println("1. Ranking por horas de uso");
        System.out.println("2. Ranking por numero de sesiones");
        System.out.println("3. Ranking por valor");
        System.out.println("4. Exportar un ranking");
        System.out.println("0. Volver");

        int opcion = lector.leerEntero("Seleccione una opcion: ");

        List<Equipo> ranking;

        switch (opcion) {
            case 1:
                ranking = servicioInformes.obtenerRankingPorHorasUso();
                System.out.println("EQUIPOS POR HORAS DE USO");
                break;

            case 2:
                ranking = servicioInformes.obtenerRankingPorSesiones();
                System.out.println("EQUIPOS POR NUMERO DE SESIONES");
                break;

            case 3:
                ranking = servicioInformes.obtenerRankingPorValor();
                System.out.println("EQUIPOS POR VALOR");
                break;

            case 4:
                exportarRanking(exportador, lector);
                return;

            case 0:
                return;

            default:
                System.out.println("Opcion no valida.");
                return;
        }

        if (ranking.isEmpty()) {
            System.out.println("No hay equipos registrados.");
            return;
        }

        for (int i = 0; i < ranking.size(); i++) {

            Equipo equipo = ranking.get(i);

            System.out.println((i + 1) + ". " + equipo.getCodigo() + " | " + equipo.getNombre()
            + " | Horas: " + equipo.getHorasUso() + " | Sesiones: " + equipo.getNumeroSesiones() + " | Valor: $" + equipo.getValor());
        }
    }

    private static void exportarRanking(ExportadorInformes exportador, LectorConsola lector) {

        System.out.println("\nEXPORTAR RANKING");
        System.out.println("1. Ranking por horas de uso");
        System.out.println("2. Ranking por numero de sesiones");
        System.out.println("3. Ranking por valor");

        int opcion = lector.leerEntero("Seleccione el ranking: ");

        String ruta = lector.leerTexto("Ingrese el nombre del archivo (ejemplo: ranking.txt): ");

        try {
            switch (opcion) {

                case 1:
                    exportador.exportarRankingHoras(ruta);
                    break;
                case 2:
                    exportador.exportarRankingSesiones(ruta);
                    break;
                case 3:
                    exportador.exportarRankingValor(ruta);
                    break;
                default:
                    System.out.println("Opcion no valida.");
                    return;
            }

            System.out.println("Informe exportado correctamente.");

        } catch (IOException e) {

            System.out.println("No fue posible exportar el informe: "+ e.getMessage());
        }
    }

    //Metodos para la opcion 13
    private static void consultarAuditoria(ServicioAuditoria servicioAuditoria, LectorConsola lector) {

        System.out.println("\nHISTORIAL DE AUDITORIA");
        System.out.println("1. Consultar todos los registros");
        System.out.println("2. Filtrar por usuario");
        System.out.println("3. Filtrar por fecha");
        System.out.println("4. Filtrar por tipo de operacion");

        int opcion = lector.leerEntero("Seleccione una opcion: ");
        List<RegistroAuditoria> registros;

        try {
            switch (opcion) {

                case 1:
                    registros = servicioAuditoria.consultarTodos();
                    break;

                case 2:
                    String codigo = lector.leerTexto("Codigo del usuario: ");
                    registros = servicioAuditoria.filtrarPorUsuario(codigo);
                    break;

                case 3:
                    String fechaTexto =lector.leerTexto("Fecha (yyyy-MM-dd): ");
                    LocalDate fecha = LocalDate.parse(fechaTexto);
                    registros = servicioAuditoria.filtrarPorFecha(fecha);
                    break;

                case 4:

                    System.out.println("Tipos de operacion disponibles:");
                    for (TipoOperacion tipo : TipoOperacion.values()) {
                        System.out.println("- " + tipo);
                    }

                    String tipoTexto =lector.leerTexto("Ingrese el tipo de operacion: ");
                    TipoOperacion tipo = TipoOperacion.valueOf(tipoTexto.trim().toUpperCase());
                    registros = servicioAuditoria.filtrarPorTipo(tipo);
                    break;

                default:
                    System.out.println("Opcion no valida.");
                    return;
            }

            if (registros.isEmpty()) {
                System.out.println("\nNo se encontraron registros.");
                return;
            }

            System.out.println("\nREGISTROS DE AUDITORIA");

            for (RegistroAuditoria registro : registros) {
                System.out.println(registro);
            }

        } catch (DateTimeParseException e) {

            System.out.println("El formato de fecha es incorrecto.");

        } catch (IllegalArgumentException e) {

            System.out.println("El tipo de operacion ingresado no existe.");

        } catch (Exception e) {

            System.out.println("No fue posible consultar la auditoria: " + e.getMessage());
        }
    }

    //Metodo para la opcion 14
    private static SistemaLaboratorio gestionarPersistencia(GestorPersistencia gestorPersistencia, SistemaLaboratorio sistema, LectorConsola lector) {

        System.out.println("\nGUARDAR Y RECUPERAR SISTEMA");
        System.out.println("1. Guardar sistema");
        System.out.println("2. Recuperar sistema");
        System.out.println("0. Volver");

        int opcion = lector.leerEntero("Seleccione una opcion: ");
        String ruta;

        try {

            switch (opcion) {

                case 1:

                    ruta = lector.leerTexto( "Nombre del archivo (ejemplo: respaldo.dat): ");
                    gestorPersistencia.guardarSistema(sistema, ruta);
                    System.out.println("Sistema guardado correctamente.");
                    return sistema;

                case 2:

                    ruta = lector.leerTexto("Nombre del archivo que desea recuperar: ");
                    SistemaLaboratorio recuperado = gestorPersistencia.cargarSistema(ruta);
                    System.out.println("Sistema recuperado correctamente.");
                    return recuperado;

                case 0:
                    return sistema;

                default:
                    System.out.println("Opcion no valida.");
                    return sistema;
            }

        } catch (IOException | ClassNotFoundException e) {

            System.out.println("Error en la persistencia: " + e.getMessage());
            return sistema;
        }
    }
}