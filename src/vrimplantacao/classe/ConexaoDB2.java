package vrimplantacao.classe;

import com.ibm.db2.jcc.DB2Connection;
import java.sql.Connection;

/**
 *
 * @author Leandro
 */
public class ConexaoDB2 extends AbstractConexao {
    
    private static Connection connection;

    @Override
    protected String getClasseConexao() {
        return com.ibm.db2.jcc.DB2Driver.class.getName();
    }

    @Override
    protected String buildStringDeConexao() {
        return "jdbc:db2://" + host + ":" + porta + "/" + dataBase;
    }

    @Override
    protected String startTransactionSQL() {
        return "BEGIN TRANSACTION;";
    }

    @Override
    protected String commitTransactionSQL() {
        return "COMMIT TRANSACTION;";
    }

    @Override
    protected String rollbackTransactionSQL() {
        return "ROLLBACK";
    }

    @Override
    protected String getSelectParaTesteDeConexao() {
        return "select 1 as ok from SYSIBM.SYSDUMMY1";
    }

    @Override
    protected Connection getConnection() {
        return connection;
    }

    @Override
    protected void setConnection(Connection connection) {
        ConexaoDB2.connection = connection;
    }
    
    public static Connection getConexao() {
        return ConexaoDB2.connection;
    }
    
}
