package model;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * SistemaLaboratorio
 * El sistema laboratorio tiene un único proposito contener
   todo el sistema chicos, asi se pueden compartir los datos pero no se usan
   variables estaticas que prohibe el enunciado
 */
public class SistemaLaboratorio implements Serializable{
    /*Same as always*/
    private static final long serialVersionUID=1L;

    /*Lit aca estamos guardando todas las clases! :D*/
    /*Nota importante: Aqui el map nos ayuda a buscar super  easy (hashmap)*/
    private Map<String, Usuario> usuarios;
    private Map<String, Equipo> equipos;
    private Map<String, Sesion> sesiones;
    private Map<String, Laboratorio> laboratorios;
    private List<RegistroAuditoria> auditoria;
    private ConfiguracionSistema configuracion;
    /*Y este es el contador para los codigos de sesion*/
    private int contadorSesiones;

    /*Constructor :D arranca con todo nuevecito :D*/
    public SistemaLaboratorio(){
        usuarios=new HashMap<>();
        equipos=new HashMap<>();
        sesiones=new HashMap<>();
        laboratorios=new HashMap<>();
        auditoria=new ArrayList<>();
        configuracion=new ConfiguracionSistema();
        contadorSesiones=0;
    }

    /*Ahora los metodos:
    - Generar el siguiente codigo de sesion ejemplo
     S1
     S2
     etc....*/

    /**
     * Genera un nuevo codigo para la sesion
     * @return codigo unico para la sesion
     */
    public String generarCodigoSesion(){
        contadorSesiones++;
        return "S" + contadorSesiones;
    }

    /*Ahora los metodos:*/
   
    /**
     * Buscar un lab por nombre y crearlo si no aparece
     * @param nombre nombre del lab buscado
     * @return el lab buscado o el nuevo creado
     */
    public Laboratorio obtenerOCrearLaboratorio(String nombre){
        Laboratorio lab=laboratorios.get(nombre);
        if(lab==null){
            lab=new Laboratorio(nombre);
            laboratorios.put(nombre, lab);
        }
        return lab;
    }

    /*Ahora los getters de always :D */
    public Map<String, Usuario> getUsuarios(){
        return usuarios;
    }

    public Map<String, Equipo> getEquipos(){
        return equipos;
    }

    public Map<String, Sesion> getSesiones(){
        return sesiones;
    }

    public Map<String, Laboratorio> getLaboratorios(){
        return laboratorios;
    }

    public List<RegistroAuditoria> getAuditoria(){
        return auditoria;
    }

    public ConfiguracionSistema getConfiguracion(){
        return configuracion;
    }
}