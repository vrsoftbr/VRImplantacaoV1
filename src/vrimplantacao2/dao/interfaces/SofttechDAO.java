package vrimplantacao2.dao.interfaces;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;

/**
 *
 * @author Leandro
 */
public class SofttechDAO extends InterfaceDAO {
    
    public static final String NOME_SISTEMA = "Softtech";
    private static final Logger LOG = Logger.getLogger(SofttechDAO.class.getName());

    @Override
    public String getSistema() {
        return NOME_SISTEMA;
    }

    public List<Estabelecimento> getLojas() {
        return Arrays.asList(new Estabelecimento("1", "SOFTTECH 01"));
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        Set<OpcaoProduto> opt = new HashSet<>();
        
        opt.addAll(OpcaoProduto.getMercadologico());
        opt.addAll(OpcaoProduto.getFamilia());
        
        return opt;
    }
    
}
