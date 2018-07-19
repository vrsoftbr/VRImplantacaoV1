package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao2.dao.cadastro.Estabelecimento;

/**
 *
 * @author Importacao
 */
public class CadastraFacilDAO extends InterfaceDAO {

    public String id_loja;
    
    @Override
    public String getSistema() {
        return "Cadastra Facil" + id_loja;
    }
    
    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        
        try(Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "    id_empresa,\n" +
                    "    nome_razao\n" +
                    "from\n" +
                    "    empresa")) {
                while(rs.next()) {
                    result.add(new Estabelecimento(rs.getString("id_empresa"), rs.getString("nome_razao")));
                }
            }
        }
        return result;
    }
}
