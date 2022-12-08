package com.example.star_bank.Conexion;

public class conexion {

    //ACCESOS A LAS APIS DEL SERVIDOR LOCAL MEDIANTE XAMMP POR LA URL ESPECIFICA QUE NOS DA NUESTRA PC, CON ESTO HACE EL FUNCIONAMIENTO ADECUADO DE LOS PHP.
    public static final String URL = "http://192.168.0.13/";
    public static final String ROOT_URL = URL + "star_bank/rest_api-login/";

    public static final String URL_ADD_USER = ROOT_URL + "add_user.php";
    public static final String URL_GENERATE_USER = ROOT_URL + "generate_user.php";

    public static final String URL_ADD_CUENTA = ROOT_URL + "add_cuenta.php";

    public static final String URL_UPDATE_PASS = ROOT_URL + "update_pass.php";
//    public static final String URL_SELECT = ROOT_URL + "select.php";

    public static final String URL_SELECT_ID = ROOT_URL + "select_id.php";
    public static final String URL_RECU_ID = ROOT_URL + "select_id_recu.php";

    public static final String URL_ADD_TRAN = ROOT_URL + "add_transaccion.php";
}
