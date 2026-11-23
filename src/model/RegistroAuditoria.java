package model;

import model.enums.TipoOperacion;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


/**
 * 
 * RegistroAuditoria
 * a siguiente clase es para el registo del historial de la auditoria
 */
public class RegistroAuditoria implements Serializable{
    /*El serializable va porque se pide que el estado de todo
    el sistema se guarde en un archivo binario usando serializacion*/

    private static final long serialVersionUID=1L;
    private LocalDateTime fechaHora;
    /*Usuario aun falta arreglarlo pq no se ha creado*/
    private Usuario usuario;
    private TipoOperacion tipo;
    private String descripcion;
    private boolean exitoso;

    /**
     * El constructor de auditoria
     * @param usuario
     * @param tipo
     * @param descripcion
     * @param exitoso si la operacion fue exitosa da true si no false
     */
    public RegistroAuditoria(Usuario usuario, TipoOperacion tipo, String descripcion, boolean exitoso){
        this.fechaHora=LocalDateTime.now();
        this.usuario=usuario;
        this.tipo=tipo;
        this.descripcion=descripcion;
        this.exitoso=exitoso;
    }

    /**
     * obtiene la fecha y hora del registro
     * @return
     */
    public LocalDateTime getFechaHora(){
        return fechaHora;
    }

    /**
     * Obtiene el usuario de la operacion
     * @return
     */
    public Usuario getUsuario(){
        return usuario;
    }

    /**
     * Obtiene el tipo de operacion
     * @return
     */
    public TipoOperacion getTipo(){
        return tipo;
    }
    
    /**
     * Descripccion del evento
     * @return
     */
    public String getDescripcion(){
        return descripcion;
    }
    
    /**
     * Indica si la operacion se realizo correctamente.
     * @return
     */
    public boolean isExitoso(){
        return exitoso;
    }
    /*Ahora el metodo del famoso tostring para convertirla en una linea de texto */
    /*Basicamente arma la linea que se muestra en consola y se exporta al archivo*/
    /*Nota recuerden que el override dice este metodo reemplaza a lo que herede del padre*/

    /**
     * Deja el registro de auditoría en una linea dividida por |
     * para verla en consola y exporta el archivo de texto.
     *
     * @return Muestra el resgitro de auditoria en una linea de texto
     */
    @Override
    public String toString(){
        DateTimeFormatter formato=DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        /*Por default sin usuario seria NA*/
        String quien="NA";
        if(usuario!=null){
            quien=usuario.getCodigo();
        }
        String resultado="Fallo";
        if(exitoso){
            resultado="Oks";
        }
        return"|"+fechaHora.format(formato)+"|"+quien+"|"+tipo+"|"+resultado+"|"+descripcion;
    }
}



