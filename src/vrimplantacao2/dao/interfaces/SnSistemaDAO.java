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
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author leandro
 */
public class SnSistemaDAO extends InterfaceDAO implements MapaTributoProvider {

    
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

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        
        try (Statement st = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = st.executeQuery(
                    "select\n" +
                    "	t.GRUPOICMS,\n" +
                    "	cfop.OPERACAO,\n" +
                    "	case t.TIPOCALCULO when 2 then 'CONTRIB' else 'NAO CONTRIB' end contribicms,\n" +
                    "	case when t.UF = cid.CODESTADO then 'DENT_UF' else 'FORA_UF' end tipo,\n" +
                    "	concat(\n" +
                    "		'cst: ', cst.codigo, ' ',\n" +
                    "		'aliq: ', t.PERCENTUALICMS, ' ',\n" +
                    "		'red: ', case t.PERCENTUALBASECALCULO when 100 then 0 else t.PERCENTUALBASECALCULO end, ' ',\n" +
                    "		g.DESCRICAO\n" +
                    "	) descricao,\n" +
                    "	cst.codigo cst,\n" +
                    "	t.PERCENTUALICMS aliquota,\n" +
                    "	case t.PERCENTUALBASECALCULO when 100 then 0 else t.PERCENTUALBASECALCULO end reduzido\n" +
                    "from\n" +
                    "	TRIBUTACAOICMS t\n" +
                    "	join EMPRESA e on\n" +
                    "		e.CODIGO = 1\n" +
                    "	join CIDADE cid on\n" +
                    "		e.CODCIDADE = cid.CODMUNICIPIO \n" +
                    "	join CST on\n" +
                    "		t.cst = cst.id\n" +
                    "	join cfop on\n" +
                    "		t.CFOP = cfop.CODOPERACAO\n" +
                    "	join GRUPOICMS g on\n" +
                    "		t.GRUPOICMS = g.ID \n" +
                    "where\n" +
                    "	cfop.CODOPERACAO in (1102, 2102,5102)\n" +
                    "order by\n" +
                    "	1"
            )) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(
                            getTributacaoKey(
                                    rs.getString("GRUPOICMS"),
                                    rs.getInt("operacao"),
                                    rs.getString("contribicms"),
                                    rs.getString("tipo")
                            ),
                            rs.getString("descricao"),
                            rs.getInt("cst"),
                            rs.getDouble("aliquota"),
                            rs.getDouble("reduzido")
                    ));
                }
            }
        }
        
        return result;
    }

    private String getTributacaoKey(String grupoIcms, int operacao, String contribIcms, String tipo) {
        return String.format(
                "%s-%d-%s-%s",
                grupoIcms,
                operacao,
                contribIcms,
                tipo
        );
    }
    
    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        
        try (Statement st = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = st.executeQuery(
                    ""
            )) {
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    /*
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.set(rs.getString(""));
                    imp.set(rs.getString(""));
                    imp.set(rs.getString(""));
                    imp.set(rs.getString(""));
                    imp.set(rs.getString(""));
                    imp.set(rs.getString(""));
                    imp.set(rs.getString(""));
                    imp.set(rs.getString(""));
                    imp.set(rs.getString(""));
                    imp.set(rs.getString(""));
                    imp.set(rs.getString(""));
                    imp.set(rs.getString(""));
                    imp.set(rs.getString(""));
                    imp.set(rs.getString(""));
                    imp.set(rs.getString(""));
                    imp.set(rs.getString(""));
                    imp.set(rs.getString(""));
                    imp.set(rs.getString(""));
                    imp.set(rs.getString(""));
                    imp.set(rs.getString(""));
                    imp.set(rs.getString(""));
                    imp.set(rs.getString(""));
                    imp.set(rs.getString(""));
                    imp.set(rs.getString(""));
                    imp.set(rs.getString(""));
                    imp.set(rs.getString(""));
                    imp.set(rs.getString(""));
                    */
                    result.add(imp);
                }
            }
        }
        
        return result;
    }
    
    
}
