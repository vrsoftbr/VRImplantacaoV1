package vrimplantacao2.dao.interfaces;

import java.util.HashSet;
import java.util.Set;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;

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
    
}
