package vrimplantacao.classe;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public abstract class AbstractConexao {

    private int contBegin = 0;
    protected String host = "";
    protected String ipSec = "";
    protected int porta = 0;
    protected String dataBase = "";
    protected String usuario = "";
    protected String senha = "";
    
    protected abstract String getClasseConexao();
    protected abstract String buildStringDeConexao();
    protected abstract String startTransactionSQL();
    protected abstract String commitTransactionSQL();
    protected abstract String rollbackTransactionSQL();
    protected abstract String getSelectParaTesteDeConexao();
    protected abstract Connection getConnection();
    protected abstract void setConnection(Connection connection);

    public void abrirConexao(String i_ip, int i_porta, String i_database, String i_usuario, String i_senha) throws Exception {
        abrirConexao(i_ip, "", i_porta, i_database, i_usuario, i_senha);
    }

    public void abrirConexao(String i_ip, String i_ipSec, int i_porta, String i_database, String i_usuario, String i_senha) throws Exception {
        Class.forName(getClasseConexao());

        host = i_ip;
        ipSec = i_ipSec;
        porta = i_porta;
        dataBase = i_database;
        usuario = i_usuario;
        senha = i_senha;

        try {
            DriverManager.setLoginTimeout(15);
            setConnection(DriverManager.getConnection(buildStringDeConexao(), usuario, senha));

        } catch (Exception ex) {
            if (!ipSec.isEmpty()) {
                setConnection(DriverManager.getConnection(buildStringDeConexao()));
            } else {
                throw ex;
            }
        }
    }

    public Statement createStatement() throws Exception {
        if (contBegin == 0) {
            testarConexao();
        }

        return getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
    }

    public void begin() throws Exception {
        if (getConnection() == null) {
            testarConexao();
        }

        if (contBegin == 0) {
            getConnection().createStatement().execute(startTransactionSQL());
        }

        contBegin++;
    }

    public void commit() throws Exception {
        contBegin--;

        if (contBegin == 0) {
            getConnection().createStatement().execute(commitTransactionSQL());
        }
    }

    public void rollback() throws Exception {
        contBegin--;

        if (contBegin <= 0) {
            contBegin = 0;

            if (getConnection() != null) {
                getConnection().createStatement().execute(rollbackTransactionSQL());
            }
        }
    }

    public void close() throws Exception {
        try {
            getConnection().close();
            setConnection(null);

        } catch (Exception ex) {
        }
    }

    private void testarConexao() throws Exception {
        Statement stm = null;

        try {
            stm = getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            stm.execute(getSelectParaTesteDeConexao());
            stm.close();

        } catch (Exception ex) {
            close();
            abrirConexao(host, ipSec, porta, dataBase, usuario, senha);
        }
    }

    public PreparedStatement prepareStatement(String i_sql) throws Exception {
        return getConnection().prepareStatement(i_sql);
    }

    public void forceCommit() throws Exception {
        getConnection().createStatement().execute(commitTransactionSQL());
    }
}
