package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import vrimplantacao.classe.ConexaoPostgres;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.vo.importacao.MercadologicoIMP;

/**
 *
 * @author leandro
 */
public class ViggoDAO extends InterfaceDAO {

    private String complemento = "";

    public void setComplemento(String complemento) {
        this.complemento = complemento == null ? "" : complemento.trim();
    }
    
    @Override
    public String getSistema() {
        return "VIGGO" + (!complemento.equals("") ? " - " + complemento : "");
    }

    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        
        try (
                Statement st = ConexaoPostgres.getConexao().createStatement();
                ResultSet rs = st.executeQuery(
                        "select codigo, nome, cnpj from empresa order by 1"
                )
        ) {
            while (rs.next()) {
                result.add(new Estabelecimento(rs.getString("codigo"), rs.getString("nome") + " - " + rs.getString("cnpj")));
            }
        }
        
        return result;
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.MAPA_TRIBUTACAO,
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR
        ));
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        
        try (
                Statement st = ConexaoPostgres.getConexao().createStatement();
                ResultSet rs = st.executeQuery(
                        "select\n" +
                        "	distinct\n" +
                        "	g.codigo merc1,\n" +
                        "	g.nome merc1_desc,\n" +
                        "	sg.codigo merc2,\n" +
                        "	sg.nome merc2_desc\n" +
                        "from\n" +
                        "	produto p\n" +
                        "	join grupo g on\n" +
                        "		p.codigo_grupo = g.codigo\n" +
                        "	left join subgrupo sg on\n" +
                        "		p.codigo_subgrupo = sg.codigo\n" +
                        "order by\n" +
                        "	g.codigo"
                )
        ) {
            while (rs.next()) {
                MercadologicoIMP imp = new MercadologicoIMP();
                
                imp.setImportSistema(getSistema());
                imp.setImportLoja(getLojaOrigem());
                imp.setMerc1ID(rs.getString("merc1"));
                imp.setMerc1Descricao(rs.getString("merc1_desc"));
                //imp.setMerc2ID(rs.getString("merc2"));
                //imp.setMerc2Descricao(rs.getString("merc2_desc"));
                
                result.add(imp);
            }
        }
        
        return result;
    }
    
    
    
}
