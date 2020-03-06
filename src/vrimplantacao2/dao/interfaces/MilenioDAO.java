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
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;

/**
 * DAO de importação do Milênio.
 * @author leandro
 */
public class MilenioDAO extends InterfaceDAO implements MapaTributoProvider {
    
    private String complemento = "";
    public void setComplemento(String complemento) {
        this.complemento = complemento == null ? "" : complemento.trim();
    }

    @Override
    public String getSistema() {
        if ("".equals(this.complemento)) {
            return "Milenio";
        } else {
            return "Milenio - " + this.complemento;
        }
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        
        try (Statement st = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = st.executeQuery(
                    "select lojcod, LOJFAN, LOJCGC, LOJEST from loja order by lojcod"
            )) {
                while (rs.next()) {
                    result.add(new Estabelecimento(
                            rs.getString("lojcod"),
                            String.format(
                                    "%s - %s",
                                    rs.getString("LOJFAN"),
                                    rs.getString("LOJCGC")
                            )
                    ));
                }
            }
        }
        
        return result;
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<OpcaoProduto>(Arrays.asList(
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                OpcaoProduto.FAMILIA_PRODUTO
        ));
    }
    
    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        try (Statement st = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = st.executeQuery(
                    "select\n" +
                    "	A.SECCOD,\n" +
                    "	A.SECDES,\n" +
                    "	B.GRPCOD,\n" +
                    "	B.GRPDES,\n" +
                    "	C.SBGCOD,\n" +
                    "	C.SBGDES\n" +
                    "from\n" +
                    "	SECAO A\n" +
                    "inner join GRUPO B on\n" +
                    "	B.SECCOD = A.SECCOD\n" +
                    "inner join SUBGRUPO C on\n" +
                    "	C.SECCOD = A.SECCOD\n" +
                    "	and C.GRPCOD = B.GRPCOD\n" +
                    "order by\n" +
                    "	A.SECCOD,\n" +
                    "	B.GRPCOD,\n" +
                    "	C.SBGCOD"
            )) {
                while (rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setMerc1ID(rs.getString("SECCOD"));
                    imp.setMerc1Descricao(rs.getString("SECDES"));
                    imp.setMerc2ID(rs.getString("GRPCOD"));
                    imp.setMerc2Descricao(rs.getString("GRPDES"));
                    imp.setMerc3ID(rs.getString("SBGCOD"));
                    imp.setMerc3Descricao(rs.getString("SBGDES"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();
        try (Statement st = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = st.executeQuery(
                    "select\n" +
                    "	PROCOD id,\n" +
                    "	prodes descricao\n" +
                    "from\n" +
                    "	produto\n" +
                    "where\n" +
                    "	procod in (\n" +
                    "	select\n" +
                    "		procod\n" +
                    "	from\n" +
                    "		referencia\n" +
                    "	group by\n" +
                    "		PROCOD\n" +
                    "	having\n" +
                    "		COUNT(*) > 1)\n" +
                    "order by\n" +
                    "	id"
            )) {
                while (rs.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("id"));
                    imp.setDescricao(rs.getString("descricao"));
                    
                    result.add(imp);
                }
            }
        }        
        return result;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        try (Statement st = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = st.executeQuery(
                    ""
            )) {
                while (rs.next()) {
                    result.add(
                            new MapaTributoIMP(
                                    rs.getString(""),
                                    rs.getString(""),
                                    rs.getInt(""),
                                    rs.getDouble(""),
                                    rs.getDouble("")
                            )
                    );
                }
            }
        }        
        return result;
    }
    
    
    
}
