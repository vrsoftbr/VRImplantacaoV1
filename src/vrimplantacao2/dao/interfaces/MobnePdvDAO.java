package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import vrimplantacao.classe.ConexaoMySQL;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.cadastro.mercadologico.MercadologicoNivelIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author leandro
 */
public class MobnePdvDAO extends InterfaceDAO implements MapaTributoProvider {

    private String complemento = "";
    public void setComplemento(String complemento) {
        this.complemento = complemento == null ? "" : complemento.trim();
    }
    
    @Override
    public String getSistema() {
        return "".equals(complemento) ? "Mobne" : "Mobne - " + complemento;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        
        try (Statement st = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = st.executeQuery(
                    ""
            )) {
                while (rs.next()) {
                
                }
            }
        }
        
        return result;
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        
        try (Statement st = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = st.executeQuery(
                    "select nroempresa, nomereduzido from tb_empresa order by nroempresa"
            )) {
                while (rs.next()) {
                    result.add(
                            new Estabelecimento(rs.getString("nroempresa"), rs.getString("nomereduzido"))
                    );
                }
            }
        }
        
        return result;
    }

    @Override
    public List<MercadologicoNivelIMP> getMercadologicoPorNivel() throws Exception {
        Map<String, MercadologicoNivelIMP> result = new LinkedHashMap<>();
        
        try (Statement st = ConexaoMySQL.getConexao().createStatement()) {
            //Nível 1
            try (ResultSet rs = st.executeQuery(
                    "select\n" +
                    "	seqcategoria id,\n" +
                    "	categoria descricao\n" +
                    "FROM \n" +
                    "	tb_categoria tc\n" +
                    "where\n" +
                    "	nivelhierarquia = 1\n" +
                    "order by\n" +
                    "	seqcategoria "
            )) {
                while (rs.next()) {
                    result.put(
                            rs.getString("id"),
                            new MercadologicoNivelIMP(rs.getString("id"), rs.getString("descricao"))
                    );
                }
            }
            
            //Nível 2
            Map<String, MercadologicoNivelIMP> nv2 = new LinkedHashMap<>();
            try (ResultSet rs = st.executeQuery(
                    "select\n" +
                    "	seqcategoriapai pai,\n" +
                    "	seqcategoria id,\n" +
                    "	categoria descricao\n" +
                    "FROM \n" +
                    "	tb_categoria tc\n" +
                    "where\n" +
                    "	nivelhierarquia = 2\n" +
                    "order by\n" +
                    "	pai, seqcategoria "
            )) {
                while (rs.next()) {
                    MercadologicoNivelIMP pai = result.get(rs.getString("pai"));
                    if (pai != null) {
                        nv2.put(
                                rs.getString("id"),
                                pai.addFilho(rs.getString("id"), rs.getString("descricao"))
                        );
                    }
                }
            }
            
            //Nível 3
            Map<String, MercadologicoNivelIMP> nv3 = new LinkedHashMap<>();
            try (ResultSet rs = st.executeQuery(
                    "select\n" +
                    "	seqcategoriapai pai,\n" +
                    "	seqcategoria id,\n" +
                    "	categoria descricao\n" +
                    "FROM \n" +
                    "	tb_categoria tc\n" +
                    "where\n" +
                    "	nivelhierarquia = 3\n" +
                    "order by\n" +
                    "	pai, seqcategoria "
            )) {
                while (rs.next()) {
                    MercadologicoNivelIMP pai = nv2.get(rs.getString("pai"));
                    if (pai != null) {
                        nv3.put(
                                rs.getString("id"),
                                pai.addFilho(rs.getString("id"), rs.getString("descricao"))
                        );
                    }
                }
            }
            
            //Nível 4
            try (ResultSet rs = st.executeQuery(
                    "select\n" +
                    "	seqcategoriapai pai,\n" +
                    "	seqcategoria id,\n" +
                    "	categoria descricao\n" +
                    "FROM \n" +
                    "	tb_categoria tc\n" +
                    "where\n" +
                    "	nivelhierarquia = 4\n" +
                    "order by\n" +
                    "	pai, seqcategoria "
            )) {
                while (rs.next()) {
                    MercadologicoNivelIMP pai = nv3.get(rs.getString("pai"));
                    if (pai != null) {
                        pai.addFilho(rs.getString("id"), rs.getString("descricao"));
                    }
                }
            }
        }
        
        return new ArrayList<>(result.values());
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        
        try (Statement st = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = st.executeQuery(
                    ""
            )) {
                while (rs.next()) {
                   ProdutoIMP imp = new ProdutoIMP(); 
                }
            }
        }
        
        return result;
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.MERCADOLOGICO_POR_NIVEL,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR
        ));
    }
    
}
