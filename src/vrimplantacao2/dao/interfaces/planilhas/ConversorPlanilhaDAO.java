package vrimplantacao2.dao.interfaces.planilhas;

import java.sql.Statement;
import vrframework.classe.Conexao;

/**
 *
 * @author guilhermegomes
 */
public class ConversorPlanilhaDAO {
    
    public void converter(String sql) throws Exception {
        try(Statement stm = Conexao.createStatement()) {
            stm.execute(sql);
        }
    }
}
