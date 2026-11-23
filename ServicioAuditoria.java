package service;
import model.RegistroAuditoria;
import model.SistemaLaboratorio;
import model.Usuario;
import model.enums.TipoOperacion;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * ServicioAuditoria
 */
public class ServicioAuditoria{

    private SistemaLaboratorio sistema;

    /*Este es el constructor, aqui es super cortico porque literal es decirle a sistema que se guarde
    en sistema xd :D*/
    /**
     * 
     * @param sistema
     */
    public ServicioAuditoria(SistemaLaboratorio sistema){
        this.sistema = sistema;
    }

    /*Metodo de registro para la auditoria, entonces registramos con todo, se va con el new y luego se
    agrega al sistema con el .add :D*/
    /**
     * 
     * @param usuario
     * @param tipo
     * @param descripcion
     * @param exitoso
     */
    public void registrar(Usuario usuario, TipoOperacion tipo, String descripcion, boolean exitoso){
        RegistroAuditoria registro=new RegistroAuditoria(usuario, tipo, descripcion, exitoso);
        sistema.getAuditoria().add(registro);
    }

    /*El metodo para retornar por asi
    decirlo todo el historial de las auditorias*/
    /**
     * 
     * @return
     */
    public List<RegistroAuditoria> consultarTodos(){
        return sistema.getAuditoria();
    }

    /*Ahora filtra que hay de un usuario en especficico*/
    /**
     * 
     * @param codigoUsuario
     * @return
     */
    public List<RegistroAuditoria> filtrarPorUsuario(String codigoUsuario){
        List<RegistroAuditoria> resultado=new ArrayList<>();
        /*La logica de recorrido de siempre con el for each*/
        for(RegistroAuditoria r : sistema.getAuditoria()){
            if(r.getUsuario()!=null && r.getUsuario().getCodigo().equals(codigoUsuario)){
                resultado.add(r);
                /*Recuerden que es con el .add :D */
            }
        }
        return resultado;
    }

    /*Ahora muy literalmente la mismaaaa logica
    pero en vez de un usuario ahora es para un dia, entonces
    eso con localdate :D*/
    /**
     * 
     * @param fecha
     * @return
     */
    public List<RegistroAuditoria> filtrarPorFecha(LocalDate fecha){
        List<RegistroAuditoria> resultado=new ArrayList<>();
        for(RegistroAuditoria r : sistema.getAuditoria()){
            if(r.getFechaHora().toLocalDate().equals(fecha)){
                resultado.add(r);
            }
        }
        return resultado;
    }

    /*Ahora finalmente para un tipo de operacion :D
    Esa si puede ser cualquiera de la de los enums*/
    /**
     * 
     * @param tipo
     * @return
     */
    public List<RegistroAuditoria> filtrarPorTipo(TipoOperacion tipo){
        List<RegistroAuditoria> resultado=new ArrayList<>();
        for(RegistroAuditoria r : sistema.getAuditoria()){
            if(r.getTipo()==tipo){
                resultado.add(r);
            }
        }
        return resultado;
    }
}
