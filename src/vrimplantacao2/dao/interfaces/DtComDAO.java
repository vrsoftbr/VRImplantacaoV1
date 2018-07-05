package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoDBF;
import vrimplantacao2.dao.cadastro.Estabelecimento;

/**
 *
 * @author Importacao
 */
public class DtComDAO extends InterfaceDAO {

    @Override
    public String getSistema() {
        return "DTCOM";
    }
    
    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try(Statement stm = ConexaoDBF.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n"
                      + "codloja,\n"
                      + "nomeloja\n"
                  + "from\n"
                      + "lojas")) {
                while(rs.next()) {
                    result.add(new Estabelecimento(rs.getString("codloja"), rs.getString("nomeloja")));
                }
            }
        }
        return result;
    }  
}
