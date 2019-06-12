package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoOracle;
import vrimplantacao2.dao.cadastro.Estabelecimento;

/**
 *
 * @author Importacao
 */
public class ViaSoftDAO extends InterfaceDAO {

    @Override
    public String getSistema() {
        return "ViaSoft";
    }
    
    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try(Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "  estab id,\n" +
                    "  reduzido || ' - CNPJ: ' || cnpj fantasia\n" +
                    "from\n" +
                    "  viasoftsys.filial\n" +
                    "where\n" +
                    "  inativa = 'N'")) {
                while(rs.next()) {
                    result.add(new Estabelecimento(rs.getString("id"), rs.getString("fantasia")));
                }
            }
        }
        return result;
    }
}
