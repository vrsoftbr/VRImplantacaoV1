package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.vo.importacao.MercadologicoIMP;

/**
 *
 * @author leandro
 */
public class SnSistemaDAO extends InterfaceDAO {

    
    private String complemento = "";

    public void setComplemento(String complemento) {
        this.complemento = complemento == null ? "" : complemento.trim();
    }
    
    @Override
    public String getSistema() {        
        return "SN Sistema" + (
                "".equals(complemento) ?
                "" :
                " - " + complemento
        );
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        
        try (Statement st = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = st.executeQuery(
                    "select\n" +
                    "	codigo,\n" +
                    "	fantasia\n" +
                    "from\n" +
                    "	empresa \n" +
                    "order by\n" +
                    "	codigo"
            )) {
                while (rs.next()) {
                    result.add(new Estabelecimento(
                            rs.getString("codigo"),
                            rs.getString("fantasia")
                    ));
                }
            }
        }
        
        return result;
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR
        ));
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        
        try (Statement st = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = st.executeQuery(
                    "select\n" +
                    "	dp.CODDEPARTAMENTO merc1,\n" +
                    "	dp.DESCRICAO merc1_desc,\n" +
                    "	sc.CODSECAO merc2,\n" +
                    "	sc.DESCRICAO merc2_desc\n" +
                    "from\n" +
                    "	departamento dp\n" +
                    "	join SECAO sc on\n" +
                    "		sc.CODDEPARTAMENTO = dp.CODDEPARTAMENTO\n" +
                    "order by\n" +
                    "	dp.CODDEPARTAMENTO,\n" +
                    "	sc.CODSECAO"
            )) {
                while (rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setMerc1ID(rs.getString("merc1"));
                    imp.setMerc1Descricao(rs.getString("merc1_desc"));
                    imp.setMerc2ID(rs.getString("merc2"));
                    imp.setMerc2Descricao(rs.getString("merc2_desc"));
                    result.add(imp);
                }
            }
        }
        
        return result;
    }
    
}
