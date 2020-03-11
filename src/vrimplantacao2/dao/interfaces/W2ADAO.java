package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoAccess;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.importacao.MapaTributoIMP;

/**
 *
 * @author Importacao
 */
public class W2ADAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "W2A";
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
  
    public List<Estabelecimento> getLojaCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        
        try (Statement stm = ConexaoAccess.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n" +
                    " codigo_da_empresa as id,\n" +
                    " nome_fantasia\n" +
                    "from \n" +
                    " empresas"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("id"), rst.getString("nome_fantasia")));
                }
            }
        }
        
        return result;
    }
}
