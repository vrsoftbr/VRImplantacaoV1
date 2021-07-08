package vrimplantacao2_5.dao.conexao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class ConexaoOracle {

    private static int contBegin = 0;
    private static Connection con;
    private static String ip = "";
    private static String ipSec = "";
    private static int porta = 0;
    private static String dataBase = "";
    private static String usuario = "";
    private static String senha = "";
    private static String strCon = "";
    private static boolean usandoString = false;
    
    
    public static void abrirConexao(String i_ip, int i_porta, String i_database, String i_usuario, String i_senha) throws Exception {
        abrirConexao(i_ip, "", i_porta, i_database, i_usuario, i_senha);
    }
    
    public static void abrirConexao() throws Exception {
        Class.forName("oracle.jdbc.driver.OracleDriver");

        if (usandoString) {
            try {
                con = DriverManager.getConnection(strCon, usuario, senha);
            } catch (Exception ex) {
                throw ex;
            }
        } else {
            try {
                con = DriverManager.getConnection("jdbc:oracle:thin:@" + ip + ":" + porta + ":" + dataBase, usuario, senha);
            } catch (Exception ex) {
                if (!ipSec.isEmpty()) {
                    con = DriverManager.getConnection("jdbc:oracle:thin:@" + ipSec + ":" + porta + ":" + dataBase, usuario, senha);
                } else {
                    throw ex;
                }
            }
        }
    }

    public static void abrirConexao(String i_ip, String i_ipSec, int i_porta, String i_database, String i_usuario, String i_senha) throws Exception {
        Class.forName("oracle.jdbc.driver.OracleDriver");

        ip = i_ip;
        ipSec = i_ipSec;
        porta = i_porta;
        dataBase = i_database;
        usuario = i_usuario;
        senha = i_senha;
        strCon = "";
        usandoString = false;

        try {
            con = DriverManager.getConnection("jdbc:oracle:thin:@" + i_ip + ":" + i_porta + ":" + i_database, i_usuario, i_senha);
        } catch (Exception ex) {
            if (!ipSec.isEmpty()) {
                con = DriverManager.getConnection("jdbc:oracle:thin:@" + i_ipSec + ":" + i_porta + ":" + i_database, i_usuario, i_senha);
            } else {
                throw ex;
            }
        }
    }
    
    public static void abrirConexao(String conString, String i_usuario, String i_senha) throws Exception {
        Class.forName("oracle.jdbc.driver.OracleDriver");

        usuario = i_usuario;
        senha = i_senha;
        strCon = conString;
        usandoString = true;

        try {
            con = DriverManager.getConnection(conString, i_usuario, i_senha);
        } catch (Exception ex) {
            throw ex;
        }
    }

    public static Connection getConexao() {
        return con;
    }

    public static Statement createStatement() throws Exception {
        if (contBegin == 0) {
            testarConexao();
        }
        
        return con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
    }

    public static void begin() throws Exception {
        con.setAutoCommit(false);
    }

    public static void commit() throws Exception {

        con.commit();
    }

    public static void rollback() throws Exception {
        contBegin--;

        if (contBegin <= 0) {
            contBegin = 0;

            if (con != null) {
                con.rollback();
            }
        }
    }

    public static void close() throws Exception {
        try {
            con.close();
            con = null;

        } catch (Exception ex) {
        }
    }

    private static void testarConexao() throws Exception {
        Statement stm = null;

        try {
            stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            stm.execute("SELECT 1 FROM DUAL");
            stm.close();

        } catch (Exception ex) {
            close();
            abrirConexao();
        }
    }

    public static PreparedStatement prepareStatement(String i_sql) throws Exception {
        return con.prepareStatement(i_sql);
    }

    public static void destruir(ResultSet rst, Connection conn, Statement stm) throws Exception {
        if (rst != null) {
            rst.close();
        }
        if (conn != null) {
            conn.close();
        }
        if (stm != null) {
            stm.close();
        }
    }

    public static Connection getNewConnection(String host, int port, String database, String user, String pass) throws Exception {
        Class.forName("oracle.jdbc.driver.OracleDriver");

        try {
            return DriverManager.getConnection("jdbc:oracle:thin:@" + host + ":" + port + ":" + database, user, pass);
        } catch (Exception ex) {
            throw ex;
        }
    }
}