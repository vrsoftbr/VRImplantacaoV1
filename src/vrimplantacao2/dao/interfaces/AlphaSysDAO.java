package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.vo.cadastro.mercadologico.MercadologicoNivelIMP;

/**
 *
 * @author Leandro
 */
public class AlphaSysDAO extends InterfaceDAO {
    
    private static final Logger LOG = Logger.getLogger(AlphaSysDAO.class.getName());

    @Override
    public String getSistema() {
        return "AlphaSys";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.MERCADOLOGICO_POR_NIVEL,
                OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR
        ));
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select cod_empresa, cod_empresa||' - '||razao descricao from empresa order by 1"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("cod_empresa"), rst.getString("descricao")));
                }
            }
        }
        
        return result;
    }

    @Override
    public List<MercadologicoNivelIMP> getMercadologicoPorNivel() throws Exception {
        List<MercadologicoNivelIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    cod_grupo merc1,\n" +
                    "    nome merc1_desc\n" +
                    "from\n" +
                    "    grupo g\n" +
                    "where\n" +
                    "    nivel = 0\n" +
                    "order by\n" +
                    "    1"
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP imp = new MercadologicoNivelIMP(rst.getString("merc1"), rst.getString("merc1_desc"));
                    
                    mercNivel2(imp);
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    private void mercNivel2(MercadologicoNivelIMP pai) throws Exception {
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    cod_grupo merc2,\n" +
                    "    nome merc2_desc\n" +
                    "from\n" +
                    "    grupo g\n" +
                    "where\n" +
                    "    g.nivel = 1 and\n" +
                    "    g.cod_juncao = " + pai.getId() + "\n" +
                    "order by\n" +
                    "    1"
            )) {
                while (rst.next()) {
                    pai.addFilho(rst.getString("merc2"), rst.getString("merc2_desc"));
                }
            }
        }
    }
    
    
    
}
