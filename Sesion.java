package model;

import exceptions.EstadoSesionInvalidoException;
import model.enums.EstadoSesion;
import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/*Ahora la clase de sesion :D
 */

/**
 * 
 * Sesion
 * Representa una sesion de uso de un equipo
 */
public class Sesion implements Serializable{

    /*Same as always*/
    private static final long serialVersionUID=1L;
    private String codigo;
    private Equipo equipo;
    private UsuarioAcademico usuario;
    private LocalDateTime inicio;
    private int duracionMinutos;
    private LocalDateTime finReal;
    private String observaciones;
    private EstadoSesion estado;
    private double penalizacion;
    private boolean danoReportado;

    /*El constructor:
    Nota:
    Creamos la sesion como programada y de una vez la mandamos al historial
    del equipo y el usuario*/

    /**
     * Crea una nueva sesion y al pone de una vez en el historial del usuario y del equipo.
     * @param codigo codigo de la sesion
     * @param equipo codigo del equipo
     * @param usuario codigo del usuario
     * @param inicio fecha y hora
     * @param duracionMinutos duracion de la sesion
     */
    public Sesion(String codigo, Equipo equipo, UsuarioAcademico usuario, LocalDateTime inicio, int duracionMinutos){
        this.codigo=codigo;
        this.equipo=equipo;
        this.usuario=usuario;
        this.inicio=inicio;
        this.duracionMinutos=duracionMinutos;
        this.finReal=null;
        this.observaciones="";
        this.estado=EstadoSesion.PROGRAMADA;
        this.penalizacion=0;
        this.danoReportado=false;
        /*Aqui la agregamos al equipo y al usuario*/
        equipo.agregarSesion(this);
        usuario.agregarSesion(this);
    }

    /*Ahora los metodos
     * Hora en la que deberia terminar: inicio + duracion*/

    /**
     * Calcula la fecha cuandos se acaba la sesion
     * @return fecha de finalizacion
     */
    public LocalDateTime getFinProgramado(){
        return inicio.plusMinutes(duracionMinutos);
    }

    /**
     * Aqui el metodo para verificar si se cruzan entre si
       tipo si una sesion empieza antes de que otra termine
     * @param inicioNuevo fecha y hora de la nueva sesion
     * @param duracionNueva duracion de la nueva sesion
     * @return true si se cruzan horarios, false si no
     */
    public boolean seCruzaCon(LocalDateTime inicioNuevo, int duracionNueva){
        /*Si ya no esta programada pues no se cruzan*/
        if(estado!=EstadoSesion.PROGRAMADA){
            return false;
        }
        /*Y aqui verificamos con el isBefore para ver si si se cruzan o no*/
        LocalDateTime finNuevo=inicioNuevo.plusMinutes(duracionNueva);
        if(inicioNuevo.isBefore(getFinProgramado()) && finNuevo.isAfter(inicio)){
            return true;
        }
        return false;
    }

    /**
     * "Se calcula una penalizacion equivalente a un pocentaje del valor del equipo
     por cada hora o fraccion de retraso"
     vamos a decir que 10 minutos tarde = 1 hora.
     * @param fin fecha y hora de la entrega 
     * @return numero de horas de retraso a cobrar
     */
    public int calcularHorasRetraso(LocalDateTime fin){
        long minutosTarde=Duration.between(getFinProgramado(), fin).toMinutes();
        /*Aqui pues si no hay retraso todo good*/
        if(minutosTarde<=0){
            return 0;
        }
        /*Otra cosa aqui, so, se le suma 59 pq si tipo llego 10 min tarde 10/60 0 0.16
        el sistema dice que no paga multa porque pues fue 0 practicamente, entonces si le sumamos
        59 siempre se obliga a pagar lo que es :D*/
        return (int)((minutosTarde + 59) / 60);
    }

   
    /**
     * Cierra la sesion: guarda fin real observaciones y dano
     * @param fin fecha de cierre de sesion
     * @param observaciones comentarios o notas
     * @param danoReportado true si se reporto daño, false si no
     * @param config calcula multas a partir de configuracion del sistema
     * @return monto de multa por retraso
     * @throws EstadoSesionInvalidoException si la sesion no se encuentra programada
     */
    public double cerrar(LocalDateTime fin, String observaciones, boolean danoReportado, ConfiguracionSistema config)
            /*Aqui mandamos la excepcion :D*/
            throws EstadoSesionInvalidoException{
        if(estado!=EstadoSesion.PROGRAMADA){
            throw new EstadoSesionInvalidoException("Solo se puede cerrar una sesion programada ;/");
        }
        /*El cosntructor :D*/
        this.finReal=fin;
        this.observaciones=observaciones;
        this.danoReportado=danoReportado;
        int horasRetraso=calcularHorasRetraso(fin);
        this.penalizacion=usuario.calcularPenalizacion(horasRetraso, equipo.getValor(), config);
        this.estado=EstadoSesion.CERRADA;
        return penalizacion;
    }

    /*Ahora el metodo de cancelar la sesion*/
    /**
     * Cambia el estado de una sesion a Cancelada
     * @throws EstadoSesionInvalidoException si la sesion ya esta cancelada o esta cerrada.
     */
    public void cancelar() throws EstadoSesionInvalidoException{
        /*Aqui mandamos la excepcion :D*/
        if(estado!=EstadoSesion.PROGRAMADA){
            throw new EstadoSesionInvalidoException("Solo se puede cancelar una sesion programada ;/");
        }
        estado=EstadoSesion.CANCELADA;
    }

    /*Ahora el metodo de las horas reales de uso del equipo! :D*/
    /**
     * Calcula el tiempo de uso
     * @return la duracion o si no se ha cerrado retorna 0
     */
    public double getHorasReales(){
        if(finReal==null){
            return 0;
        }
        /*Dividimos entre 60 pss pa que sea en horas*/
        return Duration.between(inicio, finReal).toMinutes() / 60.0;
    }

    /*Y nuevamente nuestro famoso obtener info!*/
    /**
     * Genera una linea resumen con los datos de la sesion
     * @return La linea de texto separada con |
     */
    public String obtenerInfo(){
        DateTimeFormatter formato=DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return codigo+" | "+equipo.getCodigo()+" "+ equipo.getNombre()+" | "+usuario.getCodigo()+
                " | "+inicio.format(formato)+"---->"+getFinProgramado().format(formato)+
                " | "+estado+" | Multa: "+penalizacion;
    }

    /* Ahora los getters de siempre :D*/

    public String getCodigo(){
        return codigo;
    }

    public Equipo getEquipo(){
        return equipo;
    }

    public UsuarioAcademico getUsuario(){
        return usuario;
    }

    public LocalDateTime getInicio(){
        return inicio;
    }

    public int getDuracionMinutos(){
        return duracionMinutos;
    }

    public LocalDateTime getFinReal(){
        return finReal;
    }

    public String getObservaciones(){
        return observaciones;
    }

    public EstadoSesion getEstado(){
        return estado;
    }

    public double getPenalizacion(){
        return penalizacion;
    }

    public boolean isDanoReportado(){
        return danoReportado;
    }

    /* Y los famosos setters de siempre pal admin :D */

    public void setInicio(LocalDateTime inicio){
        this.inicio=inicio;
    }

    public void setDuracionMinutos(int duracionMinutos){
        this.duracionMinutos=duracionMinutos;
    }
}
