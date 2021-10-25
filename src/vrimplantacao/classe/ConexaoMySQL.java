package vrimplantacao.classe;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class ConexaoMySQL {

    private int contBegin = 0;
    private static Connection con;
    private String ip = "";
    private String ipSec = "";
    private int porta = 0;
    private String dataBase = "";
    private String usuario = "";
    private String senha = "";
    private String strCon = "";
    private boolean usandoString = false;

    public void abrirConexao(String i_ip, int i_porta, String i_database, String i_usuario, String i_senha) throws Exception {
        abrirConexao(i_ip, "", i_porta, i_database, i_usuario, i_senha);
    }

    public void abrirConexao(String i_ip, String i_ipSec, int i_porta, String i_database, String i_usuario, String i_senha) throws Exception {
        Class.forName("com.mysql.jdbc.Driver");

        ip = i_ip;
        ipSec = i_ipSec;
        porta = i_porta;
        dataBase = i_database;
        usuario = i_usuario;
        senha = i_senha;

        try {
            con = DriverManager.getConnection("jdbc:mysql://" + i_ip + ":" + i_porta + "/" + i_database + "?zeroDateTimeBehavior=convertToNull", i_usuario, i_senha);

        } catch (Exception ex) {
            if (!ipSec.isEmpty()) {
                con = DriverManager.getConnection("jdbc:mysql://" + i_ipSec + ":" + i_porta + "/" + i_database, i_usuario, i_senha);
            } else {
                throw ex;
            }
        }
    }
    
    public void abrirConexao(String conString, String i_usuario, String i_senha) throws Exception {
        Class.forName("com.mysql.jdbc.Driver");

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

    public void close() throws Exception {
        try {
            con.close();
            con = null;

        } catch (Exception ex) {
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
            abrirConexao(ip, ipSec, porta, dataBase, usuario, senha);
        }
    }

    public PreparedStatement prepareStatement(String i_sql) throws Exception {
        return con.prepareStatement(i_sql);
    }

    public void forceCommit() throws Exception {
        con.createStatement().execute("commit");
    }
}
