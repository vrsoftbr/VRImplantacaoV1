package vrimplantacao;

import com.j256.ormlite.db.DatabaseType;
import com.j256.ormlite.db.DatabaseTypeUtils;
import com.j256.ormlite.db.PostgresDatabaseType;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import java.sql.SQLException;

public class DadosConexaoPostgreSQL {

    public String ipBanco = "";
    public String ipSecBanco = "";
    public int portaBanco = 0;
    public String nomeBanco = "";
    public String usuarioBanco = "";
    public String senhaBanco = "";
    public String alias = "";
    
    public JdbcConnectionSource getSource() throws SQLException {
        String url = "jdbc:postgresql://" + ipBanco + ":" + portaBanco + "/" + nomeBanco;
        DatabaseType databaseType = new PostgresDatabaseType() {

            @Override
            public boolean isCreateIfNotExistsSupported() {
                return true;
            }
            
        };
        return new JdbcConnectionSource(
                url, usuarioBanco
                , senhaBanco
                , databaseType);
    }
    
}
