package service;

import model.SistemaLaboratorio;
import model.Equipo;

import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

/*Con esta clase nos encargaremos de generar los informes del sistema  
Entonces, sabemos que la clase Equipo almacena pues la info del equipo, codigo, nombre, valor, etc.
Y ademas sabemos que la clase SitemaLaboratorio nos ayuda a acceder a esta info, y es justo eso lo que 
vamos a usar :D

Lo ideal es primero implementar los rankings 
- Equipos mas usados-> de mayor a menor cantidad de horas de uso 
- Equipos con mas sesiones-> de mayor a menor numero de sesiones
- Equipos de mayor valor-> los ordenamos del mas caro al mas barato*/

public class ServicioInformes {

    //Primero los atributos y el constructor 
    private SistemaLaboratorio sistema;

    public ServicioInformes(SistemaLaboratorio sistema) {
        this.sistema = sistema;
    }

    //Ahora si iniciamos los metodos :) 
    //Primero comenzamos con el ranking de equipos mas usados 
    public List<Equipo> obtenerRankingPorHorasUso() {

        //Primero obtenemos los objetos Equipos y creamos una lista de ellos 
        List<Equipo> ranking = new ArrayList<>(sistema.getEquipos().values());

        ranking.sort(Comparator.comparingDouble(Equipo::getHorasUso).reversed());
        /*Notas: 
        Recordemos que comparingdouble nos sirve para comparar los atributos, en este caso las horas de uso de cada
        equipo, y asi mismo, reversed invierte el orden para que asi quede de mayor a menor :) */

        return ranking;
    }

    //Continuamos con el ranking de equipos con mas sesiones, que es basicamente igual a la anterior  
    public List<Equipo> obtenerRankingPorSesiones() {
        List<Equipo> ranking = new ArrayList<>(sistema.getEquipos().values());
        ranking.sort(Comparator.comparingInt(Equipo::getNumeroSesiones).reversed());
        return ranking;
    }

    //Finalizamos con el ranking de equipos de mayor valor 
    public List<Equipo> obtenerRankingPorValor() {
        List<Equipo> ranking = new ArrayList<>(sistema.getEquipos().values());
        ranking.sort(Comparator.comparingDouble(Equipo::getValor).reversed());
        return ranking;
    }
}
