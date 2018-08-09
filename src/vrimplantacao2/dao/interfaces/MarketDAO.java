package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoPostgres;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;

/**
 *
 * @author Importacao
 */
public class MarketDAO extends InterfaceDAO implements MapaTributoProvider{

    public boolean v_usar_arquivoBalanca;
    public String lojaMesmoID;
    
    @Override
    public String getSistema() {
        return "Market";
    }
    
    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "	cd_loja,\n" +
                    "	nm_loja\n" +
                    "from\n" +
                    "	cadastro.tb_loja")){
                while (rs.next()) {
                    result.add(new Estabelecimento(rs.getString("cd_loja"), rs.getString("nm_loja")));
                }
            }
        }
        return result;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	cd_produto,\n" +
                    "	cd_base_fornecedor\n" +
                    "from \n" +
                    "	produto.tb_produto_loja_forn")) {
                while (rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setIdProduto(rs.getString("cd_produto"));
                    imp.setIdFornecedor(rs.getString("cd_base_fornecedor"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }
}
