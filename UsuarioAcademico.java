package model;
import model.enums.EstadoSesion;
import model.enums.Rol;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * UsuarioAcademico
 * - Basicamente agrupa lo que van a compaertir en caracteristicas
estudiantes y monitores :D

Programa - Semestre - Penalizacion Acumulada

- Tmb es abstracta pq no hay un usuario academico solito
- Implementa un Penalizable tmb abstracto pq eso va como la funcion
de obtener info en usuario cada uno lo desarrolla
 */

public abstract class UsuarioAcademico extends Usuario implements Penalizable{

    private static final long serialVersionUID=1L;
    private String programa;
    private int semestre;
    private double penalizacionAcumulada;

    /*Ahi va la lista de sesiones del usuario
    Nota: Muchachos Aparece como error pq aun no creamos Sesiones
    tecnically debería funcionar pero si acaso luego lo cambiamos*/
    private List<Sesion> sesiones;


    /**
     * Crea un nuevo usuario
     * @param codigo codigo usuario
     * @param nombreCompleto nombre usuario
     * @param clave clave del usuario
     * @param rol estudiante o monitor
     * @param programa nombre de la carrera
     * @param semestre numero de semestre
     */
    protected UsuarioAcademico(String codigo, String nombreCompleto, String clave, Rol rol, String programa, int semestre){
        super(codigo, nombreCompleto, clave, rol);
        this.programa=programa;
        this.semestre=semestre;
        this.penalizacionAcumulada=0;
        this.sesiones=new ArrayList<>();
    }

    /**
     * Monto total de la multa + la nueva multa
     * @param valor nuevo valor de multa a pagar
     */
    public void agregarPenalizacion(double valor){
        penalizacionAcumulada+=valor;
    }

    
    
    /**
     * Registra una sesion en el historial del usuario
     * @param sesion la sesion programada 
    */
    public void agregarSesion(Sesion sesion){
        sesiones.add(sesion);
    }

    /**
     * Cuentas sesiones tiene programadas el usuario
     * @return la cantidad de sesiones
     */
    public int contarSesionesProgramadas(){
        int contador=0;
        for(Sesion s : sesiones){
            if(s.getEstado()==EstadoSesion.PROGRAMADA){
                contador++;
            }
        }
        return contador;
    }

    /**
     * Genera una cadena  con los datos completos del usuario.
     *
     * @return linea de texto con la informacion completa del usuario 
     */
    public String obtenerInfo(){
        return getCodigo()+" | "+getNombreCompleto()+" | "+getRol()+" | "+programa+
                " | Semestre: "+semestre +" | "+getEstado()+" | Multas: "+penalizacionAcumulada;
    }

    /*ahora los getters de always :D */

    public String getPrograma(){
        return programa;
    }

    public int getSemestre(){
        return semestre;
    }

    public double getPenalizacionAcumulada(){
        return penalizacionAcumulada;
    }

    public List<Sesion> getSesiones(){
        return sesiones;
    }

    /* y otra vez los setters que usa el admin luego ;D*/

    public void setPrograma(String programa){
        this.programa=programa;
    }

    public void setSemestre(int semestre){
        this.semestre=semestre;
    }
}