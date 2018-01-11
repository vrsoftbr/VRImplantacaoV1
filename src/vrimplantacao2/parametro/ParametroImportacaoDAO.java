package vrimplantacao2.parametro;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.openide.util.Exceptions;
import vrframework.classe.Conexao;
import vrimplantacao.utils.Utils;

/**
 * @author Leandro Caires
 */
public class ParametroImportacaoDAO {
    
    private static final String KEY_DELIMITER = ";";

    public ParametroImportacaoDAO() {
        try (Statement stm = Conexao.createStatement()) {
            stm.executeUpdate(
                "do $$\n" +
                "declare\n" +
                "begin\n" +
                "	if not exists(select table_name from information_schema.tables where table_schema = 'implantacao' and table_name = 'parametro') then\n" +
                "		create table implantacao.parametro (\n" +
                "			key varchar not null primary key,\n" +
                "			value varchar\n" +
                "		);\n" +
                "		raise notice 'tabela criada';\n" +
                "	end if;\n" +
                "end;\n" +
                "$$;"
            );
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }
    
    public List<Parametro> getParametros() throws Exception {
        List<Parametro> parametros = new ArrayList<>();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select key, value from implantacao.parametro order by key"
            )) {
                while (rst.next()) {
                    String[] keys = rst.getString("key").split(KEY_DELIMITER);
                    String value = rst.getString("value");
                    
                    value = value.replace("\\\\", "\\");
                            
                    parametros.add(new Parametro(keys, value));
                }
            }
        }      
        return parametros;
    }

    public void salvar(Parametros parametros) throws Exception {
        Conexao.begin();
        try (Statement stm = Conexao.createStatement()) {
            for (Parametro parametro: parametros) {
                String key = formatKeys(parametro.getKeys());
                String value = parametro.getValue();
                
                value = value.replace("\\", "\\\\");
                
                try (ResultSet rst = stm.executeQuery(
                        "select * from implantacao.parametro where key = " + Utils.quoteSQL(key)
                )) {
                    if (!rst.next()) {
                        stm.execute("insert into implantacao.parametro "
                                + "(key, value) values (" 
                                + Utils.quoteSQL(key) + "," 
                                + Utils.quoteSQL(value) + ");");
                    } else {
                        stm.execute("update implantacao.parametro set value = " 
                                + Utils.quoteSQL(value) 
                                + " where key = " + Utils.quoteSQL(key));
                    }
                }
            }
            Conexao.commit();
        } catch (Exception e) {
            Conexao.rollback();
            throw e;
        }
    }

    private String formatKeys(List<String> keys) {
        String result = "";
        for (Iterator<String> iterator = keys.iterator(); iterator.hasNext();) {
            String param = iterator.next();
            result += param;
            if (iterator.hasNext()) {
                result += KEY_DELIMITER;
            }
        }
        return result;
    }
    
}
