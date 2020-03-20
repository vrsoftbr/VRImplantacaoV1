package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import vrframework.classe.ProgressBar;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorContatoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 * DAO de importação do Milênio.
 * @author leandro
 */
public class MilenioDAO extends InterfaceDAO implements MapaTributoProvider {
    
    private String complemento = "";
    public void setComplemento(String complemento) {
        this.complemento = complemento == null ? "" : complemento.trim();
    }
    
    private String localEstoque = "001";
    public void setLocalEstoque(String localEstoque) {
        this.localEstoque = localEstoque == null ? "001" : localEstoque.trim();
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
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.FAMILIA_PRODUTO,
                OpcaoProduto.FAMILIA,
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.CUSTO_COM_IMPOSTO,
                OpcaoProduto.CUSTO_SEM_IMPOSTO,
                OpcaoProduto.PRECO,
                OpcaoProduto.ATACADO,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.NATUREZA_RECEITA,
                OpcaoProduto.QTD_EMBALAGEM_COTACAO,
                OpcaoProduto.QTD_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.DATA_CADASTRO,
                OpcaoProduto.DATA_ALTERACAO,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.VALIDADE,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.PESO_BRUTO,
                OpcaoProduto.PESO_LIQUIDO,
                OpcaoProduto.ATIVO,
                OpcaoProduto.DESCONTINUADO,
                OpcaoProduto.NCM,
                OpcaoProduto.CEST,
                OpcaoProduto.FABRICANTE,
                OpcaoProduto.MARGEM,
                OpcaoProduto.PRODUTO_ECOMMERCE,
                OpcaoProduto.ICMS
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
                    "select\n" +
                    "	t.TRBID id,\n" +
                    "	t.TRBDES descricao,\n" +
                    "	t.TRBTABB cst,\n" +
                    "	t.TRBALQ aliquota,\n" +
                    "	t.TRBRED reduzido\n" +
                    "from\n" +
                    "	tributacao t\n" +
                    "order by\n" +
                    "	id"
            )) {
                while (rs.next()) {
                    result.add(
                            new MapaTributoIMP(
                                    rs.getString("id"),
                                    rs.getString("descricao"),
                                    rs.getInt("cst"),
                                    rs.getDouble("aliquota"),
                                    rs.getDouble("reduzido")
                            )
                    );
                }
            }
        }        
        return result;
    }
    
    private class ProdutoMilenio {
        String id;
        double estoque;
        double precoVenda;
        double atacado;
        int qtdAtacado;
        double custoSemImposto;
        double custoComImposto;
        String pisCofinsCredito;
        String pisCofinsDebito;
        String naturezaReceita;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        
        try (Statement st = ConexaoSqlServer.getConexao().createStatement()) {
            Map<String, ProdutoMilenio> estoque = new HashMap<>();
            ProgressBar.setStatus("Obtendo o estoque dos produtos do Milênio...");
            try (ResultSet rs = st.executeQuery(
                    "select\n" +
                    "	est.REFPLU id_produto,\n" +
                    "	est.ESTTOT estoque\n" +
                    "from\n" +
                    "	REFERENCIA r\n" +
                    "	join LOJA lj on\n" +
                    "		lj.LOJCOD = '" + this.getLojaOrigem() + "'\n" +
                    "	join [LOCAL] lc on\n" +
                    "		lc.LOJCOD = lj.LOJCOD and\n" +
                    "		lc.LOCCOD = '" + this.localEstoque + "'\n" +
                    "	left join ESTOQUE est on\n" +
                    "		est.REFPLU = r.REFPLU AND \n" +
                    "		est.LOJCOD = lj.LOJCOD and\n" +
                    "		est.LOCCOD = lc.LOCCOD"
            )) {
                while (rs.next()) {
                    ProdutoMilenio p = estoque.get(rs.getString("id_produto"));
                    if (p == null) {
                        p = new ProdutoMilenio();
                        p.id = rs.getString("id_produto");
                        estoque.put(rs.getString("id_produto"), p);
                    }
                    p.estoque = rs.getDouble("estoque");
                }
            }
            ProgressBar.setStatus("Obtendo os custos dos produtos do Milênio...");
            try (ResultSet rs = st.executeQuery(
                    "select\n" +
                    "	cs.REFPLU id_produto,\n" +
                    "	cs.CSTREP custocomimposto,\n" +
                    "	cs.CSTSEMIMP custosemimposto\n" +
                    "from\n" +
                    "	REFERENCIA r\n" +
                    "	join LOJA lj on\n" +
                    "		lj.LOJCOD = '" + getLojaOrigem() + "'\n" +
                    "	join [LOCAL] lc on\n" +
                    "		lc.LOJCOD = lj.LOJCOD and\n" +
                    "		lc.LOCCOD = '" + this.localEstoque + "'\n" +
                    "	left join CUSTO cs on\n" +
                    "		cs.REFPLU = r.REFPLU and\n" +
                    "		cs.LOJCOD = lj.LOJCOD"
            )) {
                while (rs.next()) {
                    ProdutoMilenio p = estoque.get(rs.getString("id_produto"));
                    if (p == null) {
                        p = new ProdutoMilenio();
                        p.id = rs.getString("id_produto");
                        estoque.put(rs.getString("id_produto"), p);
                    }
                    p.custoComImposto = rs.getDouble("custocomimposto");
                    p.custoSemImposto = rs.getDouble("custosemimposto");
                }
            }
            ProgressBar.setStatus("Obtendo os preços dos produtos do Milênio...");            
            try (ResultSet rs = st.executeQuery(
                    "select\n" +
                    "	pr.REFPLU id_produto,\n" +
                    "	pr.PRCVDA1 precovenda,\n" +
                    "	p.PROQTDMINPRC2 qtdatacado,\n" +
                    "	pr.PRCVDA2 atacado\n" +
                    "from\n" +
                    "	REFERENCIA r\n" +
                    "	join PRODUTO p on\n" +
                    "		r.PROCOD = p.PROCOD\n" +
                    "	join LOJA lj on\n" +
                    "		lj.LOJCOD = '" + getLojaOrigem() + "'\n" +
                    "	join [LOCAL] lc on\n" +
                    "		lc.LOJCOD = lj.LOJCOD and\n" +
                    "		lc.LOCCOD = '" + this.localEstoque + "'\n" +
                    "	left join PRECO pr on\n" +
                    "		pr.REFPLU = r.REFPLU and\n" +
                    "		pr.LOJCOD = lj.LOJCOD"
            )) {
                while (rs.next()) {
                    ProdutoMilenio p = estoque.get(rs.getString("id_produto"));
                    if (p == null) {
                        p = new ProdutoMilenio();
                        p.id = rs.getString("id_produto");
                        estoque.put(rs.getString("id_produto"), p);
                    }
                    p.precoVenda = rs.getDouble("precovenda");
                    if (
                            rs.getDouble("atacado") > 0 &&
                            rs.getDouble("qtdatacado") > 1)
                    {
                        p.qtdAtacado = rs.getInt("qtdatacado");
                        p.atacado = rs.getDouble("atacado");
                    }
                }
            }
            ProgressBar.setStatus("Obtendo o PIS/COFINS dos produtos do Milênio...");            
            try (ResultSet rs = st.executeQuery(
                    "SELECT\n" +
                    "	R.REFPLU,\n" +
                    "	R.REFPLUDV,\n" +
                    "	R.REFCODINT,\n" +
                    "	PIS.nrccod AS cod_natureza_receita,\n" +
                    "	PIS.ifasittrbent AS pis_cst_e,\n" +
                    "	PIS.ifasittrbsai AS pis_cst_s,\n" +
                    "	COFINS.ifasittrbent AS cofins_cst_e,\n" +
                    "	COFINS.ifasittrbsai AS cofins_cst_s\n" +
                    "FROM\n" +
                    "	v_loja AS L WITH (nolock)\n" +
                    "	CROSS JOIN referencia AS R WITH (nolock)\n" +
                    "	INNER JOIN produto AS P WITH (nolock) ON\n" +
                    "		P.procod = R.procod\n" +
                    "	INNER JOIN secao AS S WITH (nolock) ON\n" +
                    "		P.seccod = S.seccod\n" +
                    "	INNER JOIN grupo AS G WITH (nolock) ON\n" +
                    "		P.seccod = G.seccod\n" +
                    "		AND P.grpcod = G.grpcod\n" +
                    "	INNER JOIN subgrupo AS SG WITH (nolock) ON\n" +
                    "		P.seccod = SG.seccod\n" +
                    "		AND P.grpcod = SG.grpcod\n" +
                    "		AND P.sbgcod = SG.sbgcod\n" +
                    "	INNER JOIN (\n" +
                    "		SELECT\n" +
                    "			F.procod,\n" +
                    "			IA.ifaregfed,\n" +
                    "			IA.ifasittrbent,\n" +
                    "			IA.ifasittrbsai,\n" +
                    "			IA.ifaalqent,\n" +
                    "			IA.ifaalqsai,\n" +
                    "			n.nrccod\n" +
                    "		FROM\n" +
                    "			sysacme.dbo.fat_pro AS F WITH (nolock)\n" +
                    "		INNER JOIN imposto_federal AS I WITH (nolock) ON\n" +
                    "			I.imfcod = F.imfcod\n" +
                    "		INNER JOIN imposto_federal_aplicacao AS IA WITH (nolock) ON\n" +
                    "			IA.imfcod = I.imfcod\n" +
                    "		LEFT OUTER JOIN natureza_receita_pis_cofins AS n ON\n" +
                    "			n.nrccodext = IA.ifanatrecpiscofins\n" +
                    "		WHERE\n" +
                    "			(I.imftip = '01' )\n" +
                    "	) AS PIS ON\n" +
                    "		PIS.procod = P.procod\n" +
                    "		AND PIS.ifaregfed = L.lojregfed\n" +
                    "	INNER JOIN (\n" +
                    "		SELECT\n" +
                    "			F.procod,\n" +
                    "			IA.ifaregfed,\n" +
                    "			IA.ifasittrbent,\n" +
                    "			IA.ifasittrbsai,\n" +
                    "			IA.ifaalqent,\n" +
                    "			IA.ifaalqsai,\n" +
                    "			n.nrccod\n" +
                    "		FROM\n" +
                    "			sysacme.dbo.fat_pro AS F\n" +
                    "		INNER JOIN imposto_federal AS I WITH (nolock) ON\n" +
                    "			I.imfcod = F.imfcod\n" +
                    "		INNER JOIN imposto_federal_aplicacao AS IA WITH (nolock) ON\n" +
                    "			IA.imfcod = I.imfcod\n" +
                    "		LEFT OUTER JOIN natureza_receita_pis_cofins AS n ON\n" +
                    "			n.nrccodext = IA.ifanatrecpiscofins\n" +
                    "		WHERE\n" +
                    "			( I.imftip = '02' )\n" +
                    "	) AS COFINS ON\n" +
                    "		COFINS.procod = P.procod\n" +
                    "		AND COFINS.ifaregfed = L.lojregfed\n" +
                    "union\n" +
                    "SELECT	\n" +
                    "	R.REFPLU,\n" +
                    "	R.REFPLUDV,\n" +
                    "	R.REFCODINT,\n" +
                    "	PIS.nrccod AS cod_natureza_receita,\n" +
                    "	PIS.ifasittrbent AS pis_cst_e,\n" +
                    "	PIS.ifasittrbsai AS pis_cst_s,\n" +
                    "	COFINS.ifasittrbent AS cofins_cst_e,\n" +
                    "	COFINS.ifasittrbsai AS cofins_cst_s\n" +
                    "FROM\n" +
                    "	v_loja AS L WITH (nolock)\n" +
                    "	CROSS JOIN referencia AS R WITH (nolock)\n" +
                    "	INNER JOIN produto AS P WITH (nolock) ON\n" +
                    "		P.procod = R.procod\n" +
                    "	INNER JOIN secao AS S WITH (nolock) ON\n" +
                    "		P.seccod = S.seccod\n" +
                    "	INNER JOIN grupo AS G WITH (nolock) ON\n" +
                    "		P.seccod = G.seccod\n" +
                    "		AND P.grpcod = G.grpcod\n" +
                    "	INNER JOIN subgrupo AS SG WITH (nolock) ON\n" +
                    "		P.seccod = SG.seccod\n" +
                    "		AND P.grpcod = SG.grpcod\n" +
                    "		AND P.sbgcod = SG.sbgcod\n" +
                    "	INNER JOIN (\n" +
                    "		SELECT\n" +
                    "			F.procod,\n" +
                    "			IA.ifaregfed,\n" +
                    "			IA.ifasittrbent,\n" +
                    "			IA.ifasittrbsai,\n" +
                    "			IA.ifaalqent,\n" +
                    "			IA.ifaalqsai,\n" +
                    "			n.nrccod\n" +
                    "		FROM\n" +
                    "			sysacme.dbo.fat_pro AS F WITH (nolock)\n" +
                    "		INNER JOIN imposto_federal AS I WITH (nolock) ON\n" +
                    "			I.imfcod = F.imfcod\n" +
                    "		INNER JOIN imposto_federal_aplicacao AS IA WITH (nolock) ON\n" +
                    "			IA.imfcod = I.imfcod\n" +
                    "		LEFT OUTER JOIN natureza_receita_pis_cofins AS n ON\n" +
                    "			n.nrccodext = IA.ifanatrecpiscofins\n" +
                    "		WHERE\n" +
                    "			( I.imftip = '01' )\n" +
                    "	) AS PIS ON\n" +
                    "		PIS.procod = P.procod\n" +
                    "		AND PIS.ifaregfed = L.lojregfed\n" +
                    "	INNER JOIN (\n" +
                    "		SELECT\n" +
                    "			F.procod,\n" +
                    "			IA.ifaregfed,\n" +
                    "			IA.ifasittrbent,\n" +
                    "			IA.ifasittrbsai,\n" +
                    "			IA.ifaalqent,\n" +
                    "			IA.ifaalqsai,\n" +
                    "			n.nrccod\n" +
                    "		FROM\n" +
                    "			sysacme.dbo.fat_pro AS F\n" +
                    "		INNER JOIN imposto_federal AS I WITH (nolock) ON\n" +
                    "			I.imfcod = F.imfcod\n" +
                    "		INNER JOIN imposto_federal_aplicacao AS IA WITH (nolock) ON\n" +
                    "			IA.imfcod = I.imfcod\n" +
                    "		LEFT OUTER JOIN natureza_receita_pis_cofins AS n ON\n" +
                    "			n.nrccodext = IA.ifanatrecpiscofins\n" +
                    "		WHERE\n" +
                    "			( I.imftip = '02' )\n" +
                    "	) AS COFINS ON\n" +
                    "		COFINS.procod = P.procod\n" +
                    "		AND COFINS.ifaregfed = L.lojregfed\n" +
                    "order by\n" +
                    "	R.REFPLU,\n" +
                    "	R.REFPLUDV"
            )) {
                while (rs.next()) {
                    ProdutoMilenio p = estoque.get(rs.getString("REFPLU"));
                    if (p == null) {
                        p = new ProdutoMilenio();
                        p.id = rs.getString("REFPLU");
                        estoque.put(rs.getString("REFPLU"), p);
                    }
                    p.pisCofinsCredito = rs.getString("pis_cst_e");
                    p.pisCofinsDebito = rs.getString("pis_cst_s");
                    p.naturezaReceita = rs.getString("cod_natureza_receita");
                }
            }                            
            try (ResultSet rs = st.executeQuery(
                    "select\n" +
                    "	r.REFPLU,\n" +
                    "	r.REFPLUDV,\n" +
                    "	r.REFCODINT,\n" +
                    "	r.REFDATCAD datacadastro,\n" +
                    "	p.PRODATALT dataalteracao,\n" +
                    "	ean.EANCOD ean,\n" +
                    "	coalesce(ean.EANQTD,p.PROQTDUNDVDA) qtdembalagem,\n" +
                    "	p.PROQTDUNDCMP qtdunidadecotacao,\n" +
                    "	p.PROUNDCMP unidadecotacao,\n" +
                    "	p.PROUNDVDA unidade,\n" +
                    "	case when p.PROENVBAL = 'S' then 1 else 0 end pesavel,\n" +
                    "	p.PROVAL validade,\n" +
                    "	r.REFDES descricaocompleta,\n" +
                    "	r.REFDESRDZ descricaoreduzida,\n" +
                    "	p.SECCOD merc1,\n" +
                    "	p.GRPCOD merc2,\n" +
                    "	p.SBGCOD merc3,\n" +
                    "	r.PROCOD id_familia,\n" +
                    "	p.PROPESBRTVDA pesobruto,\n" +
                    "	p.PROPESLIQVDA pesoliquido,\n" +
                    "	case when r.REFDATFIMLIN < CURRENT_TIMESTAMP then 0 else 1 end ativo,\n" +
                    "	case when r.REFBLQCMP = 'S' then 1 else 0 end descontinuado,\n" +
                    "	p.PRONCM ncm,\n" +
                    "	r.CSTCOD cest,\n" +
                    "	r.FORCOD fabricante,\n" +
                    "	p.PROMRG1 margem,\n" +
                    "	case when p.PROEXPWEB = 'S' then 1 else 0 end enviaweb,\n" +
                    "	p.TRBID id_icms\n" +
                    "from\n" +
                    "	REFERENCIA r\n" +
                    "	join LOJA lj on\n" +
                    "		lj.LOJCOD = '" + getLojaOrigem() + "'\n" +
                    "	join [LOCAL] lc on\n" +
                    "		lc.LOJCOD = lj.LOJCOD and\n" +
                    "		lc.LOCCOD = '" + this.localEstoque + "'\n" +
                    "	join PRODUTO p on\n" +
                    "		r.PROCOD = p.PROCOD\n" +
                    "	left join ean on\n" +
                    "		ean.REFPLU = r.REFPLU"
            )) {
                int cont1 = 0, cont2 = 0;
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();                    
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("REFPLU") + rs.getString("REFPLUDV"));
                    
                    ProdutoMilenio m = estoque.get(rs.getString("REFPLU"));
                    imp.setQtdEmbalagem(rs.getInt("qtdembalagem"));
                    if (m != null) {
                        imp.setCustoSemImposto(m.custoSemImposto);
                        imp.setCustoComImposto(m.custoComImposto);
                        imp.setPrecovenda(m.precoVenda);
                        if (m.atacado > 0 && m.qtdAtacado > 1) {
                            imp.setAtacadoPreco(m.atacado);
                            imp.setQtdEmbalagem(m.qtdAtacado);
                        }
                        imp.setPiscofinsCstCredito(m.pisCofinsCredito);
                        imp.setPiscofinsCstDebito(m.pisCofinsDebito);
                        imp.setPiscofinsNaturezaReceita(m.naturezaReceita);
                        imp.setEstoque(m.estoque);
                    }
                    
                    imp.setDataCadastro(rs.getDate("datacadastro"));
                    imp.setDataAlteracao(rs.getDate("dataalteracao"));
                    imp.setEan(rs.getString("ean"));
                    imp.setQtdEmbalagemCotacao(rs.getInt("qtdunidadecotacao"));
                    imp.setTipoEmbalagemCotacao(rs.getString("unidadecotacao"));
                    imp.setTipoEmbalagem(rs.getString("unidade"));
                    imp.seteBalanca(rs.getBoolean("pesavel"));
                    imp.setValidade(rs.getInt("validade"));
                    imp.setDescricaoCompleta(rs.getString("descricaocompleta"));
                    imp.setDescricaoGondola(rs.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rs.getString("descricaoreduzida"));
                    imp.setCodMercadologico1(rs.getString("merc1"));
                    imp.setCodMercadologico2(rs.getString("merc2"));
                    imp.setCodMercadologico3(rs.getString("merc3"));
                    imp.setIdFamiliaProduto(rs.getString("id_familia"));
                    imp.setPesoBruto(rs.getDouble("pesobruto"));
                    imp.setPesoLiquido(rs.getDouble("pesoliquido"));
                    imp.setSituacaoCadastro(rs.getInt("ativo"));
                    imp.setDescontinuado(rs.getBoolean("descontinuado"));
                    imp.setNcm(rs.getString("ncm"));
                    imp.setCest(rs.getString("cest"));
                    imp.setFornecedorFabricante(rs.getString("fabricante"));
                    imp.setMargem(rs.getDouble("margem"));
                    imp.setProdutoECommerce(rs.getBoolean("enviaweb"));
                    imp.setIcmsDebitoId(rs.getString("id_icms"));
                    imp.setIcmsDebitoForaEstadoId(rs.getString("id_icms"));
                    imp.setIcmsDebitoForaEstadoNfId(rs.getString("id_icms"));
                    imp.setIcmsCreditoId(rs.getString("id_icms"));
                    imp.setIcmsCreditoForaEstadoId(rs.getString("id_icms"));
                    
                    result.add(imp);
                    
                    cont1++;
                    cont2++;
                    if (cont1 == 1000) {
                        cont1 = 0;
                        ProgressBar.setStatus("Carregando os produtos do Milênio..." + cont2);
                    }
                }
            }
        }
        
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        try (Statement st = ConexaoSqlServer.getConexao().createStatement()) {
            Map<String, List<FornecedorContatoIMP>> contatos = new HashMap<>();
            try (ResultSet rs = st.executeQuery(
                    "select\n" +
                    "	c.AGECOD id_fornecedor,\n" +
                    "	c.CNTNOM nome,\n" +
                    "	c.CNTTEL telefone,\n" +
                    "	c.CNTCEL celular,\n" +
                    "	c.CNTMAIL email\n" +
                    "from\n" +
                    "	contato c\n" +
                    "	join agente a on\n" +
                    "		c.AGECOD = a.AGECOD\n" +
                    "	join FORNECEDOR F on\n" +
                    "		f.AGECOD = a.AGECOD\n" +
                    "order by\n" +
                    "   id_fornecedor"
            )) {
                while (rs.next()) {
                    List<FornecedorContatoIMP> list = contatos.get(rs.getString("id_fornecedor"));
                    if (list == null) {
                        list = new ArrayList<>();
                        contatos.put(rs.getString("id_fornecedor"), list);
                    }
                    FornecedorContatoIMP imp = new FornecedorContatoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId("CONT" + (list.size() + 1));                    
                    imp.setImportFornecedorId(rs.getString("id_fornecedor")); 
                    imp.setNome(rs.getString("nome"));
                    imp.setTelefone(rs.getString("telefone"));
                    imp.setCelular(rs.getString("celular"));
                    imp.setTipoContato(TipoContato.COMERCIAL);
                    imp.setEmail(rs.getString("email"));
                    
                    list.add(imp);
                }
            }
            
            try (ResultSet rs = st.executeQuery(
                    "SELECT\n" +
                    "	f.FORCOD id,\n" +
                    "	a.AGECOD,\n" +
                    "	a.AGEDES razao,\n" +
                    "	a.AGEFAN fantasia,\n" +
                    "	a.AGECGCCPF cnpj,\n" +
                    "	a.AGECGFRG ierg,\n" +
                    "	a.AGETEL1 fone,\n" +
                    "	a.AGEEND endereco,\n" +
                    "	a.AGENUM numero,\n" +
                    "	a.AGECPL complemento,\n" +
                    "	a.AGEBAI bairro,\n" +
                    "	a.AGECID municipio,\n" +
                    "	a.AGEEST uf,\n" +
                    "	a.AGECEP cep,\n" +
                    "	a.AGETEL1 tel1,\n" +
                    "	a.AGETEL2 tel2,\n" +
                    "	a.AGEFAX fax,\n" +
                    "	a.AGEATA atacado,\n" +
                    "	a.AGEDATCAD datacadastro,\n" +
                    "	a.AGEDATBLO databloqueio,\n" +
                    "	a.AGEDATALT dataalteracao,\n" +
                    "	a.AGEINSMUN inscricaomunicipal,\n" +
                    "	a.AGECTRICMS tributadoicms,\n" +
                    "	a.AGEOBS observacoes,\n" +
                    "	f.FORPRZENT prazoentrega,\n" +
                    "	f.FORPRZ prazopagamento,\n" +
                    "	(SELECT TOP (1) CNTMAIL FROM dbo.CONTATO AS C WHERE (C.AGECOD = F.AGECOD) AND CNTMAIL IS NOT NULL) AS EMAIL\n" +
                    "from\n" +
                    "	FORNECEDOR F\n" +
                    "INNER JOIN AGENTE A ON\n" +
                    "	A.AGECOD = F.AGECOD\n" +
                    "where\n" +
                    "	AGECGCCPF is not null\n" +
                    "order by\n" +
                    "	FORCOD"
            )) {
                while (rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("id"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setCnpj_cpf(rs.getString("cnpj"));
                    imp.setIe_rg(rs.getString("ierg"));
                    imp.setTel_principal(rs.getString("fone"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("municipio"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));
                    imp.addTelefone("TEL 1", rs.getString("tel1"));
                    imp.addTelefone("TEL 2", rs.getString("tel2"));
                    imp.addTelefone("FAX", rs.getString("fax"));
                    //imp.setTipoFornecedor("S".equals(rs.getString("atacado")) ? TipoFornecedor.ATACADO);
                    imp.setDatacadastro(rs.getDate("datacadastro"));
                    //imp.set(rs.getString("databloqueio"));
                    //imp.set(rs.getString("dataalteracao"));
                    imp.setInsc_municipal(rs.getString("inscricaomunicipal"));
                    //imp.set(rs.getString("tributadoicms"));
                    imp.setObservacao(rs.getString("observacoes"));
                    imp.setPrazoEntrega(rs.getInt("prazoentrega"));
                    imp.setPrazoPedido(rs.getInt("prazopagamento"));
                    
                    List<FornecedorContatoIMP> get = contatos.get(rs.getString("agecod"));
                    if (get != null) {                    
                        for (FornecedorContatoIMP cont: get) {
                            imp.addContato(
                                    cont.getNome(),
                                    cont.getTelefone(),
                                    cont.getCelular(),
                                    cont.getTipoContato(),
                                    cont.getEmail()
                            );
                        }
                    }
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();
        
        try (Statement st = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = st.executeQuery(
                    "SELECT distinct\n" +
                    "	r.REFPLU,\n" +
                    "	r.REFPLUDV,\n" +
                    "	cf.FORCOD id_fornecedor,\n" +
                    "	cf.REFFOR codigoexterno,\n" +
                    "	cf.CATQTDUND qtdembalagem,\n" +
                    "	cf.CATPESLIQ pesoembalagem\n" +
                    "FROM \n" +
                    "	[CATALOGO_FORNECEDOR]  cf \n" +
                    "	inner join [REFERENCIA] R\n" +
                    "		on R.REFPLU = cf.REFPLU             \n" +
                    "	inner join [V_FORNECEDOR] f \n" +
                    "		on f.FORCOD = cf.FORCOD \n" +
                    "ORDER BY\n" +
                    "	r.REFPLU"
            )) {
                while (rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setIdFornecedor(rs.getString("id_fornecedor"));
                    imp.setIdProduto(rs.getString("REFPLU") + rs.getString("REFPLUDV"));
                    imp.setCodigoExterno(rs.getString("codigoexterno"));
                    imp.setQtdEmbalagem(rs.getInt("qtdembalagem"));
                    imp.setPesoEmbalagem(rs.getDouble("pesoembalagem"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }
    
}
