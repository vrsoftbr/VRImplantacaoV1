package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import vrimplantacao.classe.ConexaoPostgres;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.vo.importacao.MercadologicoIMP;

/**
 *
 * @author Leandro
 */
public class WShopDAO extends InterfaceDAO {

    @Override
    public String getSistema() {
        return "WShop";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        Set<OpcaoProduto> opt = new HashSet<>(OpcaoProduto.getMercadologico());
        opt.addAll(OpcaoProduto.getPadrao());
        opt.addAll(OpcaoProduto.getFamilia());
        opt.addAll(OpcaoProduto.getComplementos());
        opt.addAll(OpcaoProduto.getTributos());
        return opt;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    ""
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setMerc1ID(rst.getString("merc1"));
                    imp.setMerc1Descricao(rst.getString("merc1_desc"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }
    
}
