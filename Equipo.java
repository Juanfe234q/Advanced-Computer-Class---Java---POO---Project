package model;
import model.enums.EstadoEquipo;
import model.enums.EstadoSesion;
import model.enums.TipoEquipo;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * 
 * Equipo  
 * Bueno ahora la clase de Equipo donde:
   - Tenemos el equipo con toda su info
   - Y su historial de sesiones y uso acumulado
 */
public class Equipo implements Serializable{
    private static final long serialVersionUID=1L;

    private String codigo;
    private String nombre;
    private TipoEquipo tipo;
    /*Aqui metemos la relacion con lab :D*/
    private Laboratorio laboratorio;
    private EstadoEquipo estado;
    private double valor;
    private int semestreMinimo;
    private String descripcion;
    /*Y aqui esto nos sirve despues para todo lo relacionado al uso*/
    private int numeroSesiones;
    private double horasUso;

    /*Aqui vamos con el array list :D*/
    private List<Sesion> sesiones;

    /**
     * Aqui el constructor de equipo con su monton de vaina
     * @param codigo  codigo del equipo
     * @param nombre   nombre del equipo
     * @param tipo    Categoria del equipo
     * @param laboratorio   A que lab pertenece
     * @param estado  Su estado operativo inicial
     * @param valor  Valor comercial del equipo
     * @param semestreMinimo  semestre requerido para manipular este equipo
     * @param descripcion  notas adicionales del equipo.
     */
    public Equipo(String codigo, String nombre, TipoEquipo tipo, Laboratorio laboratorio,
                  EstadoEquipo estado, double valor, int semestreMinimo, String descripcion){
        this.codigo=codigo;
        this.nombre=nombre;
        this.tipo=tipo;
        this.laboratorio=laboratorio;
        this.estado=estado;
        this.valor=valor;
        this.semestreMinimo=semestreMinimo;
        this.descripcion=descripcion;
        this.numeroSesiones=0;
        this.horasUso=0;
        this.sesiones=new ArrayList<>();
        /*Aqui en el constructor de una vez lo metemos a la lista de su lab*/
        laboratorio.agregarEquipo(this);
    }

    /*Vamos con los metodos! :D*/

    /**
     * Verifica que se pueda hacer la reserva con el estado actual
     * @return true si el equipo esta disponible o false si no
     */
    public boolean estaDisponible(){
        return estado.permiteUso();
    }

    /**
     * Aqui se suma la sesion y la cantidad de horas de uso
       Este metodo nos sirve cuando se quiere cerrar la sesion
     * @param horas cantidad de horas a registrar.
     */
    public void registrarUso(double horas){
        numeroSesiones++;
        horasUso+=horas;
    }

    /**
     * Añade la sesion al historial del equipo
     * @param sesion 
     */
    public void agregarSesion(Sesion sesion){
        sesiones.add(sesion);
    }


    /**
     * Aqui revisamos si hay alguna sesion programada, esto nos sirve
       para ver si se puede o no darle de baja al equipo o quitarlo (si estn
       activas las sesiones pues no dejara hacerlo luego)
     * @return true si tiene sesiones pendientes o false si no
     */
    public boolean tieneSesionesProgramadas(){
        /*Misma logica del for each*/
        for(Sesion s : sesiones){
            if(s.getEstado()==EstadoSesion.PROGRAMADA){
                return true;
            }
        }
        return false;
    }

    /**
     *  Y aqui nuestro queridismo obtener info.
     * @return una cadena de texto con la informacion completa del equipo.
     */
    public String obtenerInfo(){
        return codigo+" | "+nombre+" | "+tipo+" | "+laboratorio.getNombre()+" | "+estado+
                " | Valor: "+valor+" | Semestre minimo: "+semestreMinimo+" | "+
                " Sesiones: "+numeroSesiones+" | Horas de uso: "+horasUso;
    }

    /*Ahora vamos con los Getters :D*/

    public String getCodigo(){
        return codigo;
    }

    public String getNombre(){
        return nombre;
    }

    public TipoEquipo getTipo(){
        return tipo;
    }

    public Laboratorio getLaboratorio(){
        return laboratorio;
    }

    public EstadoEquipo getEstado(){
        return estado;
    }

    public double getValor(){
        return valor;
    }

    public int getSemestreMinimo(){
        return semestreMinimo;
    }

    public String getDescripcion(){
        return descripcion;
    }

    public int getNumeroSesiones(){
        return numeroSesiones;
    }

    public double getHorasUso(){
        return horasUso;
    }

    public List<Sesion> getSesiones(){
        return sesiones;
    }

    /* Y los setters otra vez que son los que usa el admin :D*/

    public void setNombre(String nombre){
        this.nombre=nombre;
    }

    public void setTipo(TipoEquipo tipo){
        this.tipo=tipo;
    }

    public void setEstado(EstadoEquipo estado){
        this.estado=estado;
    }

    public void setValor(double valor){
        this.valor=valor;
    }

    public void setSemestreMinimo(int semestreMinimo){
        this.semestreMinimo=semestreMinimo;
    }

    public void setDescripcion(String descripcion){
        this.descripcion=descripcion;
    }
}
