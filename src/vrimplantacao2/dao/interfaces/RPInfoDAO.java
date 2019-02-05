package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoPostgres;
import vrimplantacao2.dao.cadastro.Estabelecimento;

/**
 *
 * @author Leandro
 */
public class RPInfoDAO extends InterfaceDAO {
    
    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select empr_codigo, empr_nome from empresas order by empr_codigo"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("empr_codigo"), rst.getString("empr_nome")));
                }
            }
        }
        
        return result;
    }

    @Override
    public String getSistema() {
        return "RPInfo";
    }
    
}
