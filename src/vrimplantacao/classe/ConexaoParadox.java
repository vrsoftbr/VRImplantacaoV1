package vrimplantacao.classe;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

public class ConexaoParadox {

    private int contBegin = 0;
    private static Connection con;
    private String ip = "";
    private String ipSec = "";
    private int porta = 0;
    private String dataBase = "";
    private String usuario = "";
    private String senha = "";
    public static boolean usarOdbc = false;

    public static void abrirConexao(String i_database) throws Exception {
        if (!usarOdbc) {    
            //Class.forName("com.hxtt.sql.paradox.ParadoxDriver");
            Class.forName("com.hxtt.sql.paradox.ParadoxDriver");
        } else {            
            Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
        }
        
        try {
            if (!usarOdbc) {
                con = DriverManager.getConnection("jdbc:Paradox:/" + i_database, "", "");
            } else {
                con = DriverManager.getConnection("jdbc:odbc:" + i_database+";CollatingSequence=ASCII", "", "");
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
            abrirConexao(dataBase);
        }
    }

    public PreparedStatement prepareStatement(String i_sql) throws Exception {
        return con.prepareStatement(i_sql);
    }

    public void forceCommit() throws Exception {
        con.createStatement().execute("commit");
    }

}
