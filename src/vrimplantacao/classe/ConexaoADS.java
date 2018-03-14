package vrimplantacao.classe;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class ConexaoADS {

    private int contBegin = 0;
    private static Connection con;
    private static String ip = "";
    private String ipSec = "";
    private static int porta = 0;
    private static String dataBase = "";
    private static String usuario = "";
    private static String senha = "";
    public static boolean usarOdbc = false;

    public static void abrirConexao(String i_ip, int i_porta, String i_database, String i_usuario, String i_senha) throws Exception {

        ip = i_ip;
        porta = i_porta;
        dataBase = i_database;
        usuario = i_usuario;
        senha = i_senha;

        if (!usarOdbc) {
            //Class.forName("com.hxtt.sql.paradox.ParadoxDriver");
            Class.forName("com.extendedsystems.jdbc.advantage.ADSDriver").newInstance();
        } else {
            Class.forName("com.extendedsystems.jdbc.advantage.ADSDriver");
        }
        try {
            if (!usarOdbc) {
                con = DriverManager.getConnection("jdbc:extendedsystems:advantage://" + i_ip + ":" + i_porta + ";catalog=C:/DADOS/" + i_database + ";TableType=adt;LockType=proprietary", i_usuario, i_senha);
                //"jdbc:extendedsystems:advantage://localhost:6262;catalog=C:\\yourDatbase\\data;TableType=adt;LockType=proprietary","AdsSys","no"
            } else {
                con = DriverManager.getConnection("jdbc:extendedsystems:advantage:" + i_database, "", "");
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    public static Connection getConexao() {
        return con;
    }

    public Statement createStatement() throws Exception {
        if (contBegin == 0) {
            testarConexao();
        }

        return con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
    }

    public void begin() throws Exception {
        if (con == null) {
            testarConexao();
        }

        if (contBegin == 0) {
            con.createStatement().execute("begin");
        }

        contBegin++;
    }

    public void commit() throws Exception {
        contBegin--;

        if (contBegin == 0) {
            con.createStatement().execute("commit");
        }
    }

    public void rollback() throws Exception {
        contBegin--;

        if (contBegin <= 0) {
            contBegin = 0;

            if (con != null) {
                con.createStatement().execute("rollback");
            }
        }
    }

    public static void close() throws Exception {
        if (con != null && !con.isClosed()) {
            con.close();
            con = null;
        }
    }

    private void testarConexao() throws Exception {
        Statement stm = null;

        try {
            stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            stm.execute("SELECT 1");
            stm.close();

        } catch (Exception ex) {
            close();
            abrirConexao(ip, porta, dataBase, usuario, senha);
        }
    }

    public PreparedStatement prepareStatement(String i_sql) throws Exception {
        return con.prepareStatement(i_sql);
    }

    public void forceCommit() throws Exception {
        con.createStatement().execute("commit");
    }

    public void retornaVersaoDriver() throws ClassNotFoundException, 
                                             InstantiationException, 
                                             IllegalAccessException {
        Driver dr = (Driver) (Class.forName("com.extendedsystems.jdbc.advantage.ADSDriver").newInstance());

        System.out.println(dr.getClass()
                + " " + Integer.toString(dr.getMajorVersion())
                + " " + Integer.toString(dr.getMinorVersion()));
    }

}
