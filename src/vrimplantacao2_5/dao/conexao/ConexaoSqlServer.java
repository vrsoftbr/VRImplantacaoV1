package vrimplantacao2_5.dao.conexao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import vrimplantacao2.parametro.Parametros;
import vrimplantacao2_5.gui.componente.conexao.DriverConexao;

public class ConexaoSqlServer implements DriverConexao {

    private int contBegin = 0;
    private static Connection con;
    private String ip = "";
    private String ipSec = "";
    private int porta = 0;
    private String dataBase = "";
    private String usuario = "";
    private String senha = "";
    public String instance = "SQL2014";

    public static Connection getNewConnection(String host, int port, String database, String user, String pass, String encoding) throws Exception {
        Class.forName(Driver.get().getDriver());
        try {
            return DriverManager.getConnection(Driver.get().getConnectionString(host, port, database), user, pass);
        } catch (SQLException ex) {
            throw ex;
        }
    }
    
    @Override
    public void abrirConexao(String i_ip, int i_porta, String i_database, String i_usuario, String i_senha) throws Exception {
        abrirConexao(i_ip, "", i_porta, i_database, i_usuario, i_senha);
    }

    @Override
    public void abrirConexao(String conString, String i_usuario, String i_senha) throws Exception {
        Class.forName(Driver.get().getDriver());
        
        usuario = i_usuario;
        senha = i_senha;

        try {
            con = DriverManager.getConnection(conString, i_usuario, i_senha);
        } catch (SQLException ex) {
            throw ex;
        }
    }

    public void abrirConexao(String i_ip, String i_ipSec, int i_porta, String i_database, String i_usuario, String i_senha) throws Exception {
        Class.forName(Driver.get().getDriver());

        ip = i_ip;
        ipSec = i_ipSec;
        porta = i_porta;
        dataBase = i_database;
        usuario = i_usuario;
        senha = i_senha;
        
        if (!instance.trim().isEmpty()) {
            Class.forName(Driver.get().getDriver());
            con = DriverManager.getConnection(Driver.get().getConnectionString(i_ip + "\\" + instance, porta, dataBase), i_usuario, i_senha);
        } else {
            con = DriverManager.getConnection(Driver.get().getConnectionString(i_ip, porta, dataBase), i_usuario, i_senha);
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

    @Override
    public void close() throws Exception {
        try {
            con.close();
            con = null;

        } catch (SQLException ex) {
        }
    }

    private void testarConexao() throws Exception {
        Statement stm = null;

        try {
            stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            stm.execute("SELECT 1");
            stm.close();

        } catch (SQLException ex) {
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
    
    
    
    public static enum Driver {
        
        MICROSOFT (
            "com.microsoft.sqlserver.jdbc.SQLServerDriver",
            "jdbc:sqlserver://{host}:{port};databaseName={database}"
        ),
        JTDS (
            "net.sourceforge.jtds.jdbc.Driver",
            "jdbc:jtds:sqlserver://{host}:{port}/{database}"
        );

        public static Driver get() {
            return get(Parametros.get().get("SQLServer","Driver"));
        }
        
        public static Driver get(String driver) {
            for (Driver d: values()) {
                if (d.getDriver().equals(driver)) {
                    return d;
                }
            }
            return MICROSOFT;
        }
        
        private final String driver;
        private final String connectionString;

        private Driver(String driver, String connectionString) {
            this.driver = driver;
            this.connectionString = connectionString;
        }

        public String getDriver() {
            return driver;
        }

        public String getConnectionString(String host, int port, String database) {
            return connectionString
                    .replace("{host}", host)
                    .replace("{port}", String.valueOf(port))
                    .replace("{database}", database) + ";encrypt=false;trustServerCertificate=false";
        }
        
    }
    
}
