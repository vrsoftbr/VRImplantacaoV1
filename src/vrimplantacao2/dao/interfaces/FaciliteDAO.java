package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Leandro
 */
public class FaciliteDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "FACILITE";
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    E.EMP_CODIGO,\n" +
                    "    E.EMP_DESCRICAO\n" +
                    "from\n" +
                    "    EMPRESA E\n" +
                    "order by\n" +
                    "    1, 2"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("emp_codigo"), rst.getString("emp_descricao")));
                }
            }
        }
        
        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    g.gru_codigo merc1,\n" +
                    "    g.gru_descricao merc1_descricao,\n" +
                    "    coalesce(s.sgru_codigo, '01') merc2,\n" +
                    "    coalesce(nullif(trim(s.sgru_descricao),''), g.gru_descricao) merc2_descricao\n" +
                    "from\n" +
                    "    grupo g\n" +
                    "    left join sgrupo s on\n" +
                    "        g.gru_codigo = s.sgru_grupo\n" +
                    "order by\n" +
                    "    merc1, merc2"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setMerc1ID(rst.getString("merc1"));
                    imp.setMerc1Descricao(rst.getString("merc1_descricao"));
                    imp.setMerc2ID(rst.getString("merc2"));
                    imp.setMerc2Descricao(rst.getString("merc2_descricao"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }
    
    

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    PROFA_ID,\n" +
                    "    PROFA_DESCRICAO\n" +
                    "from\n" +
                    "    produtofamilia\n" +
                    "order by\n" +
                    "    1"
            )) {
                while (rst.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("PROFA_ID"));
                    imp.setDescricao(rst.getString("PROFA_DESCRICAO"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    ""
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString(""));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }
    
}
