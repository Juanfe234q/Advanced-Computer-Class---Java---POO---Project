package exceptions;

/*Este se manda cuando se intenta eliminar un estudiante o monitor que ya tiene sesiones programasdas*/
public class UsuarioConSesionesProgramadasException extends LaboratorioException{

    public UsuarioConSesionesProgramadasException(){
        super("Como aun tienen sesiones programadas no se pueden borrar :O");
    }

    public UsuarioConSesionesProgramadasException(String mensaje){
        super(mensaje);
    }
}
