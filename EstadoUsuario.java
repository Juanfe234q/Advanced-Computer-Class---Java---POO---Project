package model.enums;
/**
 * Estados en los que puede estar un usuario:
*/
public enum EstadoUsuario{
    ACTIVO,      /**usa el sistema normal*/
    SUSPENDIDO,  /**paso el umbral de penalizacion*/
    BLOQUEADO,   /**se paso de intentos fallidos de login*/
    INACTIVO     /**el administrador lo desactivo a mano*/
}
