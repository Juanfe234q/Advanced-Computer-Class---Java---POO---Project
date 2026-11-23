package model;
import model.enums.EstadoEquipo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * Laboratorio
 * esta es la logica, la idea es que tengamos la clase
   de laboratorios, para que se pueda relacionar cada equipo con
   su lab y viceversa!
 */
public class Laboratorio implements Serializable{
    /*Lo mismo de siempre :D*/
    private static final long serialVersionUID=1L;
    private String nombre;

    /*So, empezamos con un ArrayList porque solo se
    agregan y se recorren los equipos :D*/
    private List<Equipo> equipos;

    /**
     * Constructor
     * @param nombre identificador del lab
     */
    public Laboratorio(String nombre){
        this.nombre=nombre;
        this.equipos=new ArrayList<>();
    }

    /*Y ya los metodos :D
    - Agregar equipo
    - Quitar el equipo
    - Y el calculo del valor de todos los equipos del lab
    - Cuantos equipos es un estado
    - Y los equipos con restriccion
    */

    /**
     * Agregar un equipo al lab
     * @param equipo 
     */
    public void agregarEquipo(Equipo equipo){
        equipos.add(equipo);
    }

    /**
     * Elimina un equipo del lab
     * @param equipo
     */
    public void quitarEquipo(Equipo equipo){
        equipos.remove(equipo);
    }

    /**
     * Nota importante, aca en la suma del valor total de
      equipos importante aclarar que si esta dado de baja
      no cuenta
     * @return suma el valor de todos los equipos
     */
    public double calcularValorTotal(){
        double total=0;
        /*Aqui vamos con el for each con su if verificando
        lo de dado de baja*/
        for(Equipo e : equipos){
            if(e.getEstado()!=EstadoEquipo.DADO_DE_BAJA){
                total+=e.getValor();
            }
        }
        return total;
    }

    /**
     * Cuenta cuantos equipos del laboratorio se encuentran en un estado operativo
     * @param estado filtramos por el estado que se busca
     * @return cantidad de equipos en este estado
     */
    public int contarPorEstado(EstadoEquipo estado){
        int contador=0;
        /*Same, con un for  each de su estado*/
        for(Equipo e : equipos){
            if(e.getEstado()==estado){
                contador++;
            }
        }
        return contador;
    }

    /* Aqui los equipos con restriccion osea
    desde segundo semestre en adelante*/

    /*Entonces ahi con la lista de equipos*/

    /**
     * Obtiene los equipos que requieren un semestre minimo
     * @return La lista de los equipos
     */
    public List<Equipo> obtenerEquiposConRestriccion(){
        /*Creamos la nueva lista*/
        List<Equipo> resultado=new ArrayList<>();
        /*For each igual que siempre con el if*/
        for(Equipo e : equipos){
            if(e.getSemestreMinimo()>1){
                resultado.add(e);
            }
        }
        return resultado;
    }

    /*Y yap aqui los getters*/
    public String getNombre(){
        return nombre;
    }

    public List<Equipo> getEquipos(){
        return equipos;
    }
}
