package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoAccess;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;

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
        List<MapaTributoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoAccess.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    " codigo_do_icms as id,\n" +
                    " descricao_do_icms as descricao,\n" +
                    " aliquota,\n" +
                    " situacao_tributaria_tab_b as cst,\n" +
                    " percentual_base as reducao,\n" +
                    " percentual_base_st,\n" +
                    " aliquota_st \n" +
                    "from \n" +
                    " icms \n" +
                    "where \n" +
                    " codigo_do_icms in \n" +
                    "    (select codigo_do_icms from produtos)")) {
                while(rs.next()) {
                    result.add(new MapaTributoIMP(
                            rs.getString("id"), 
                            rs.getString("descricao"), 
                            rs.getInt("cst"), 
                            rs.getDouble("aliquota"), 
                            rs.getDouble("reducao")));
                }
            }
            
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    " codigo_do_icms as id,\n" +
                    " descricao_do_icms as descricao,\n" +
                    " aliquota,\n" +
                    " situacao_tributaria_tab_b as cst,\n" +
                    " percentual_base as reducao,\n" +
                    " percentual_base_st,\n" +
                    " aliquota_st \n" +
                    "from \n" +
                    " icms \n" +
                    "where \n" +
                    " codigo_do_icms in \n" +
                    "    (select codigo_icms_compra from produtos)")) {
                while(rs.next()) {
                    result.add(new MapaTributoIMP(
                            rs.getString("id"),
                            rs.getString("descricao"), 
                            rs.getInt("cst"),
                            rs.getDouble("aliquota"),
                            rs.getDouble("reducao")));
                }
            }
        }
        
        return result;
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
    
    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoAccess.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n" +
                    " distinct\n" +
                    " p.codigo_do_grupo as merc1,\n" +
                    " g.nome_do_grupo as descmerc1,\n" +
                    " s.codigo_subgrupo as merc2,\n" +
                    " s.nome_subgrupo as descmerc2\n" +
                    "from\n" +
                    " produtos p,\n" +
                    " grupos_de_produtos g,\n" +
                    " subgrupo s\n" +
                    "where\n" +
                    "  p.codigo_do_grupo = g.codigo_do_grupo and\n" +
                    "  p.codigo_subgrupo = s.codigo_subgrupo\n" +
                    "order by\n" +
                    "  2, 4"
            )) {
                while (rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());                   
                    imp.setMerc1ID(rs.getString("merc1"));
                    imp.setMerc1Descricao(rs.getString("descmerc1"));
                    imp.setMerc2ID(rs.getString("merc2"));
                    imp.setMerc2Descricao(rs.getString("descmerc2"));
                    imp.setMerc3ID(imp.getMerc1ID());
                    imp.setMerc3Descricao(imp.getMerc2Descricao());
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }
}
