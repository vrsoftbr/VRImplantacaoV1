package vrimplantacao.classe;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import vrimplantacao2.parametro.Parametros;

public class ConexaoAccess {

    private int contBegin = 0;
    private static Connection con;
    private String dataBase = "";
    private String usuario = "";
    private String senha = "";

    public static void abrirConexao(String i_database, String i_usuario, String i_senha) throws Exception {
        
        Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
        
        switch (TipoConexaoAccess.get(Parametros.get().getInt(0, "ODBC", "TIPO_CONEXAO"))) {
            case DRIVER: 
                con = DriverManager.getConnection(
                        "jdbc:odbc:Driver={" + 
                        Parametros.get().getWithNull("Microsoft Access Driver (*.mdb)", "ODBC", "DRIVER_ODBC") + 
                        "};DBQ=" + i_database
                );
                break;
            default: FONTE_DE_DADOS:
                con = DriverManager.getConnection(
                        "jdbc:odbc:" + i_database,
                        i_usuario,
                        i_senha
                );
                break;                
        }

        try {
            
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
            abrirConexao(dataBase, usuario, senha);
        }
    }

    public PreparedStatement prepareStatement(String i_sql) throws Exception {
        return con.prepareStatement(i_sql);
    }

    public void forceCommit() throws Exception {
        con.createStatement().execute("commit");
    }
}