package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import vrimplantacao.classe.ConexaoPostgres;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.vo.cadastro.mercadologico.MercadologicoNivelIMP;

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
    public List<MercadologicoNivelIMP> getMercadologicoPorNivel() throws Exception {
        List<MercadologicoNivelIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select codigo, descricao from secoes where secaonivel2 is null order by codigo"
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP imp = new MercadologicoNivelIMP();                    
                    imp.setId(rst.getString("codigo"));
                    imp.setDescricao(rst.getString("descricao"));
                    
                    try (Statement stm2 = ConexaoPostgres.getConexao().createStatement()) {
                        try (ResultSet rst2 = stm2.executeQuery(
                                "select codigo, descricao from secoes where secaonivel2 = " + imp.getId() + " order by codigo"
                        )) {
                            while (rst2.next()) {
                                imp.addFilho(rst2.getString("codigo"), rst2.getString("descricao"));
                            }
                        }
                    }
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        Set<OpcaoProduto> opt = new HashSet<>();
        
        opt.addAll(Arrays.asList(
                OpcaoProduto.MERCADOLOGICO_POR_NIVEL,
                OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR
        ));
        opt.addAll(OpcaoProduto.getFamilia());
        
        return opt;
    }
    
}
