package vrimplantacao.classe;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class ConexaoFirebird {

    private static int contBegin = 0;
    private static Connection con;
    private static String ip = "";
    private static int porta = 0;
    private static String dataBase = "";
    private static String usuario = "";
    private static String senha = "";
    public static String encoding = "";

    public void abrirConexao(String i_ip, int i_porta, String i_database, String i_usuario, String i_senha) throws Exception {
        Class.forName("org.firebirdsql.jdbc.FBDriver");

        ip = i_ip;
        porta = i_porta;
        dataBase = i_database;
        usuario = i_usuario;
        senha = i_senha;

        try {
            String extra = "";
            if (encoding != null && !encoding.equals("")) {
                extra = "encoding=" + encoding;
            }
            con = DriverManager.getConnection("jdbc:firebirdsql:" + i_ip + "/" + i_porta + ":" + i_database + ("".equals(extra) ? "" : "?" + extra), i_usuario, i_senha);

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
            con.setAutoCommit(false);
        }

        contBegin++;
    }

    public void commit() throws Exception {
        contBegin--;

        if (contBegin == 0) {
            con.commit();
            con.setAutoCommit(true);
        }
    }

    public void rollback() throws Exception {
        contBegin--;

        if (contBegin <= 0) {
            contBegin = 0;

            if (con != null) {
                con.rollback();
                con.setAutoCommit(true);
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

    public void testarConexao() throws Exception {
        Statement stm = null;

        try {
            stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            stm.execute("SELECT CURRENT_DATE FROM RDB$DATABASE");
            stm.close();

        } catch (Exception ex) {
            close();
            abrirConexao(ip, porta, dataBase, usuario, senha);
        }
    }

    public static PreparedStatement prepareStatement(String i_sql) throws Exception {
        return con.prepareStatement(i_sql);
    }

    public static boolean isAutoCommit() throws Exception {
        return con.getAutoCommit();
    }
}
