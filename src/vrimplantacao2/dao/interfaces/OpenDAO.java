package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import vrimplantacao.classe.ConexaoMySQL;
import vrimplantacao2.dao.cadastro.Estabelecimento;

/**
 *
 * @author Leandro
 */
public class OpenDAO extends InterfaceDAO {

    @Override
    public String getSistema() {
        return "Open";
    }

    public ArrayList<Estabelecimento> getLojasCliente() throws Exception {
        ArrayList<Estabelecimento> result = new ArrayList<>();
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select codigo, abrev, reduzido, cgc  from genfil order by 1"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("codigo"), rst.getString("abrev") + " - " + rst.getString("reduzido") + " - " + rst.getString("cgc")));
                }
            }
        }
        
        return result;
    }
    
}
