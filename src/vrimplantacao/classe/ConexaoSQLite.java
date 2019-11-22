package vrimplantacao.classe;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

public class ConexaoSQLite {
    
    private static final Logger LOG = Logger.getLogger(ConexaoSQLite.class.getName());
    
    private int contBegin = 0;
    private static Connection con;
    private String database = "";

    public ConexaoSQLite(String database) throws Exception {
        this.database = database;
    }
    
    public ConexaoSQLite(){}
    
    public ConexaoSQLite conectar() throws SQLException, ClassNotFoundException {        
        Class.forName(org.sqlite.JDBC.class.getName());
        con = DriverManager.getConnection("jdbc:sqlite:" + database);
        return this;
    }
    
    public Connection get() throws SQLException, ClassNotFoundException {
        if (con == null && !con.isValid(2)) {
            conectar();
        }
        return con;
    }

    public void begin() throws Exception {
        if (contBegin == 0) {
            get().createStatement().execute("begin");
        }
        contBegin++;
    }

    public void commit() throws Exception {
        contBegin--;

        if (contBegin == 0) {
            get().createStatement().execute("commit");
        }
    }

    public void rollback() throws Exception {        
        contBegin = 0;

        if (con != null) {
            get().createStatement().execute("rollback");
        }        
    }

    public void close() throws Exception {
        if (!con.isClosed()) {
            con.close();
        }
        con = null;
    }

}
