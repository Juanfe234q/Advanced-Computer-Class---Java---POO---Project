package model.enums;

/**
 * 
 * TipoOperacion   Tipos de operacion que se guardan en el historial de auditoria
 */
public enum TipoOperacion{
    LOGIN,
    LOGOUT,
    LOGIN_FALLIDO,
    ALTA,
    MODIFICACION,
    ELIMINACION,
    CARGA_ARCHIVO,
    PROGRAMACION_SESION,
    CANCELACION_SESION,
    CIERRE_SESION,
    CAMBIO_ESTADO,
    ACCESO_DENEGADO,
    GUARDAR_ESTADO,
    RECUPERAR_ESTADO,
    EXPORTACION
}
