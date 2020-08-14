package vrimplantacao.classe;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class ConexaoDBF {

    private int contBegin = 0;
    private static Connection con;
    private String ip = "";
    private String ipSec = "";
    private int porta = 0;
    private static String dataBase = "";
    private static String usuario = "";
    private static String senha = "";
    public static boolean usarOdbc = false;

    public static void abrirConexao(String i_database) throws Exception {
        
        if (!usarOdbc) {
            Class.forName("com.hxtt.sql.dbf.DBFDriver");
        } else {
            Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
        }

        dataBase = i_database;

        try {
            if (!usarOdbc) {
                con = DriverManager.getConnection("jdbc:DBF:/" + i_database+"?loadIndices=false");
            } else {
                //con = DriverManager.getConnection("jdbc:odbc:Driver={Microsoft dBASE Driver (*.dbf)};DefaultDir=" + i_database);
                con = DriverManager.getConnection("jdbc:odbc:Driver={Driver do Microsoft dBase (*.dbf)};DefaultDir=" + i_database);
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
            abrirConexao(dataBase);
        }
    }

    public PreparedStatement prepareStatement(String i_sql) throws Exception {
        return con.prepareStatement(i_sql);
    }

    public void forceCommit() throws Exception {
        con.createStatement().execute("commit");
    }
    
    public static Connection getNewConnection(String i_database) throws Exception {

        if (!usarOdbc) {
            Class.forName("com.hxtt.sql.dbf.DBFDriver");
        } else {
            Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
        }

        dataBase = i_database;
        
        try {
            if (!usarOdbc) {
                con = DriverManager.getConnection("jdbc:DBF:/" + i_database+"?loadIndices=false");
            } else {
                con = DriverManager.getConnection("jdbc:odbc:DRIVER={Driver do Microsoft dBase (*.dbf)};DefaultDir=" + i_database);
            }
            
            return con;
        } catch (Exception ex) {
            throw ex;
        }
    }
    
}
