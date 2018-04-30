package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;

/**
 *
 * @author Leandro
 */
public class PwsDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "PWS";
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    f.id_fam,\n" +
                    "    f.ds_fam\n" +
                    "from\n" +
                    "    familia f\n" +
                    "order by\n" +
                    "    1"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setMerc1ID(rst.getString("id_fam"));
                    imp.setMerc1Descricao(rst.getString("ds_fam"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    id_empresa,\n" +
                    "    nm_fantasia\n" +
                    "from\n" +
                    "    empresa\n" +
                    "order by\n" +
                    "    1"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("id_empresa"), rst.getString("nm_fantasia")));
                }
            }
        }
        
        return result;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
