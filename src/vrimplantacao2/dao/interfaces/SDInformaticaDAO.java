package vrimplantacao2.dao.interfaces;

import java.io.File;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.nutricional.OpcaoNutricional;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto.ProdutoAnteriorDAO;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.vo.cadastro.oferta.TipoOfertaVO;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ConveniadoIMP;
import vrimplantacao2.vo.importacao.ConvenioEmpresaIMP;
import vrimplantacao2.vo.importacao.ConvenioTransacaoIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorContatoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.NutricionalIMP;
import vrimplantacao2.vo.importacao.OfertaIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Leandro
 */
public class SDInformaticaDAO extends InterfaceDAO implements MapaTributoProvider {

    public String i_arquivoOferta;

    private String complemento = "";
    
    private boolean mercadologico4 = false;
    
    public void setMercadologico4(boolean mercadologico4) {
        this.mercadologico4 = mercadologico4;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento != null ? complemento.trim() : "";
    }

    @Override
    public String getSistema() {
        return "SDInformatica" + (!complemento.equals("") ? " - " + complemento : "");
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.DATA_CADASTRO,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.VALIDADE,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.FAMILIA,
                OpcaoProduto.FAMILIA_PRODUTO,
                OpcaoProduto.PESO_BRUTO,
                OpcaoProduto.PESO_LIQUIDO,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.MARGEM,
                OpcaoProduto.CUSTO_COM_IMPOSTO,
                OpcaoProduto.CUSTO_SEM_IMPOSTO,
                OpcaoProduto.PRECO,
                OpcaoProduto.ATIVO,
                OpcaoProduto.NUTRICIONAL,
                OpcaoProduto.NCM,
                OpcaoProduto.CEST,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.NATUREZA_RECEITA,
                OpcaoProduto.ICMS,
                OpcaoProduto.ICMS_CONSUMIDOR,
                OpcaoProduto.ICMS_ENTRADA,
                OpcaoProduto.ICMS_ENTRADA_FORA_ESTADO,
                OpcaoProduto.ICMS_SAIDA,
                OpcaoProduto.ICMS_SAIDA_FORA_ESTADO,
                OpcaoProduto.ICMS_SAIDA_NF
        ));
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    widfilial,\n"
                    + "    wfantasia\n"
                    + "from\n"
                    + "    filiais  \n"
                    + "order by\n"
                    + "    widfilial"
            )) {
                while (rst.next()) {
                    result.add(
                            new Estabelecimento(rst.getString("widfilial"), rst.getString("wfantasia"))
                    );
                }
            }
        }
        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();

        String sql = "select distinct\n"
                + "    p.widsecao merc1,\n"
                + "    s.wnomesecao merc1_desc,\n"
                + "    p.widprodutogrupo merc2,\n"
                + "    f.wnomeprodutogrupo merc2_desc,\n"
                + "    sg.widprodutosubgrupo merc3,\n"
                + "    sg.wnomeprodutosubgrupo merc3_desc\n"
                + "from\n"
                + "    produtos p\n"
                + "    join secoes s on p.widsecao = s.widsecao\n"
                + "    left join produtosgrupos f on p.widprodutogrupo = f.widprodutogrupo\n"
                + "    left join produtossubgrupos sg on p.widprodutosubgrupo = sg.widprodutosubgrupo\n"
                + "order by\n"
                + "    1, 3, 5";

        if (mercadologico4) {
            sql = "select distinct\n"
                    + "    p.widsecao merc1,\n"
                    + "    s.wnomesecao merc1_desc,\n"
                    + "    p.WIDFAMILIA merc2,\n"
                    + "    fa.WNOMEFAMILIA merc2_desc,\n"
                    + "    p.widprodutogrupo merc3,\n"
                    + "    f.wnomeprodutogrupo merc3_desc,\n"
                    + "    sg.widprodutosubgrupo merc4,\n"
                    + "    sg.wnomeprodutosubgrupo merc4_desc\n"
                    + "from\n"
                    + "    produtos p\n"
                    + "    join secoes s on p.widsecao = s.widsecao\n"
                    + "    left join produtosgrupos f on p.widprodutogrupo = f.widprodutogrupo\n"
                    + "    left join produtossubgrupos sg on p.widprodutosubgrupo = sg.widprodutosubgrupo\n"
                    + "    LEFT JOIN FAMILIAS fa ON fa.WIDFAMILIA = p.WIDFAMILIA\n"
                    + "order by\n"
                    + "    1, 3, 5, 7";
        }

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(sql)) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setMerc1ID(rst.getString("merc1"));
                    imp.setMerc1Descricao(rst.getString("merc1_desc"));
                    imp.setMerc2ID(rst.getString("merc2"));
                    imp.setMerc2Descricao(rst.getString("merc2_desc"));
                    imp.setMerc3ID(rst.getString("merc3"));
                    imp.setMerc3Descricao(rst.getString("merc3_desc"));

                    if (mercadologico4) {
                        imp.setMerc4ID(rst.getString("merc4"));
                        imp.setMerc4Descricao(rst.getString("merc4_desc"));
                    }

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "widvinculo as id,\n"
                    + "wnomevinculo as descricao\n"
                    + "from vinculos\n"
                    + "order by widvinculo"
            )) {
                while (rst.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setDescricao(rst.getString("descricao"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        String sql = "SELECT\n"
                + "	p.widproduto id,\n"
                + "	p.wdatacadastro datacadastro,\n"
                + "	ean.wcodigopro ean,\n"
                + "	COALESCE(u.wsiglaunidade,'UN') unidade,\n"
                + "	CASE\n"
                + "		WHEN (ean.wbalanca = 'S' OR p.wfracionado = 'S') THEN 1\n"
                + "		ELSE 0\n"
                + "	END balanca,\n"
                + "	COALESCE(p.wvalidade,\n"
                + "	0) validade,\n"
                + "	COALESCE(ean.wnomepro, p.wnomeproduto) descricaocompleta,\n"
                + "	COALESCE(ean.wnomeprofiscal, p.wnomefiscal) descricaoreduzida,\n"
                + "	COALESCE(ean.wnomeprogondola, p.wnomegondola) descricaogondola,\n"
                + "	p.widsecao id_mercadologico1,\n"
                + "	p.widprodutogrupo id_mercadologico2,\n"
                + "	p.widprodutosubgrupo id_mercadologico3,\n"
                + "	p.wpeso peso,\n"
                + "	COALESCE(e.wquantidade,\n"
                + "	0) estoque,\n"
                + "	COALESCE(p.wlucro,\n"
                + "	0) margem,\n"
                + "	COALESCE(c.wpreco,\n"
                + "	0) custo,\n"
                + "	COALESCE(v.wpreco,\n"
                + "	0) preco,\n"
                + "	p.widvinculo AS id_familia,\n"
                + "	p.wstatus AS situacao,\n"
                + "	p.wncm ncm,\n"
                + "	p.wcest cest,\n"
                + "	p.wcstpiscofins piscofins_cst,\n"
                + "	p.wnaturezapis piscofins_nat,\n"
                + "	a.wsigla icms_id\n"
                + "FROM\n"
                + "	produtos p\n"
                + "	JOIN filiais f ON\n"
                + "		f.widfilial = " + getLojaOrigem() + "\n"
                + "	LEFT JOIN (\n"
                + "		SELECT\n"
                + "			*\n"
                + "		FROM\n"
                + "			codigospro\n"
                + "		WHERE\n"
                + "			wnaoreverter IS NULL\n"
                + "			AND wstatus IS NULL) ean ON\n"
                + "		ean.widproduto = p.widproduto\n"
                + "	LEFT JOIN unidades u ON\n"
                + "		p.widunidade = u.widunidade\n"
                + "	LEFT JOIN estoque e ON\n"
                + "		e.widproduto = p.widproduto\n"
                + "		AND e.widfilial = f.widfilial\n"
                + "		AND e.WTIPO = 1\n"
                + "	LEFT JOIN precoscusto c ON\n"
                + "		c.widproduto = p.widproduto\n"
                + "		AND c.widfilial = f.widfilial\n"
                + "	LEFT JOIN precosvenda v ON\n"
                + "		v.widproduto = p.widproduto\n"
                + "		AND v.widfilial = f.widfilial\n"
                + "		AND v.WIDTABPRECO = 1\n"
                + "		AND v.wstatus IS NULL\n"
                + "	LEFT JOIN aliquotas a ON\n"
                + "		a.widaliquota = p.widaliquotaicms\n"
                + "WHERE\n"
                + "	p.wstatus IS NULL\n"
                + "ORDER BY\n"
                + "	p.WIDPRODUTO, ean.wcodigopro";

        if (mercadologico4) {
            sql = "SELECT\n"
                    + "	p.widproduto id,\n"
                    + "	p.wdatacadastro datacadastro,\n"
                    + "	ean.wcodigopro ean,\n"
                    + "	COALESCE(u.wsiglaunidade,'UN') unidade,\n"
                    + "	CASE\n"
                    + "		WHEN (ean.wbalanca = 'S' OR p.wfracionado = 'S') THEN 1\n"
                    + "		ELSE 0\n"
                    + "	END balanca,\n"
                    + "	COALESCE(p.wvalidade,\n"
                    + "	0) validade,\n"
                    + "	COALESCE(ean.wnomepro, p.wnomeproduto) descricaocompleta,\n"
                    + "	COALESCE(ean.wnomeprofiscal, p.wnomefiscal) descricaoreduzida,\n"
                    + "	COALESCE(ean.wnomeprogondola, p.wnomegondola) descricaogondola,\n"
                    + "	p.widsecao id_mercadologico1,\n"
                    + "	p.widfamilia id_mercadologico2,\n"
                    + "	p.widprodutogrupo id_mercadologico3,\n"
                    + "	p.widprodutosubgrupo id_mercadologico4,\n"
                    + "	p.wpeso peso,\n"
                    + "	COALESCE(e.wquantidade,\n"
                    + "	0) estoque,\n"
                    + "	COALESCE(p.wlucro,\n"
                    + "	0) margem,\n"
                    + "	COALESCE(c.wpreco,\n"
                    + "	0) custo,\n"
                    + "	COALESCE(v.wpreco,\n"
                    + "	0) preco,\n"
                    + "	p.widvinculo AS id_familia,\n"
                    + "	p.wstatus AS situacao,\n"
                    + "	p.wncm ncm,\n"
                    + "	p.wcest cest,\n"
                    + "	p.wcstpiscofins piscofins_cst,\n"
                    + "	p.wnaturezapis piscofins_nat,\n"
                    + "	a.wsigla icms_id\n"
                    + "FROM\n"
                    + "	produtos p\n"
                    + "	JOIN filiais f ON\n"
                    + "		f.widfilial = " + getLojaOrigem() + "\n"
                    + "	LEFT JOIN (\n"
                    + "		SELECT\n"
                    + "			*\n"
                    + "		FROM\n"
                    + "			codigospro\n"
                    + "		WHERE\n"
                    + "			wnaoreverter IS NULL\n"
                    + "			AND wstatus IS NULL) ean ON\n"
                    + "		ean.widproduto = p.widproduto\n"
                    + "	LEFT JOIN unidades u ON\n"
                    + "		p.widunidade = u.widunidade\n"
                    + "	LEFT JOIN estoque e ON\n"
                    + "		e.widproduto = p.widproduto\n"
                    + "		AND e.widfilial = f.widfilial\n"
                    + "		AND e.WTIPO = 1\n"
                    + "	LEFT JOIN precoscusto c ON\n"
                    + "		c.widproduto = p.widproduto\n"
                    + "		AND c.widfilial = f.widfilial\n"
                    + "	LEFT JOIN precosvenda v ON\n"
                    + "		v.widproduto = p.widproduto\n"
                    + "		AND v.widfilial = f.widfilial\n"
                    + "		AND v.WIDTABPRECO = 1\n"
                    + "		AND v.wstatus IS NULL\n"
                    + "	LEFT JOIN aliquotas a ON\n"
                    + "		a.widaliquota = p.widaliquotaicms\n"
                    + "WHERE\n"
                    + "	p.wstatus IS NULL\n"
                    + "ORDER BY\n"
                    + "	p.WIDPRODUTO, ean.wcodigopro";
        }
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(sql)) {
                Map<Integer, ProdutoBalancaVO> balanca = new ProdutoBalancaDAO().getProdutosBalanca();
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));

                    Integer ean = Utils.stringToInt(rst.getString("ean"), -2);
                    ProdutoBalancaVO bal = balanca.get(ean);
                    if (bal != null) {
                        imp.setImportId("000" + bal.getCodigo());
                        imp.seteBalanca(true);
                        imp.setTipoEmbalagem("U".equals(bal.getPesavel()) ? "UN" : "KG");
                        imp.setValidade(bal.getValidade());
                        imp.setEan(String.valueOf(bal.getCodigo()));
                    } else {
                        imp.seteBalanca(rst.getBoolean("balanca"));
                        imp.setTipoEmbalagem(rst.getString("unidade"));
                        imp.setValidade(rst.getInt("validade"));
                        imp.setEan(rst.getString("ean"));
                    }
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(rst.getString("descricaogondola"));
                    imp.setCodMercadologico1(rst.getString("id_mercadologico1"));
                    imp.setCodMercadologico2(rst.getString("id_mercadologico2"));
                    imp.setCodMercadologico3(rst.getString("id_mercadologico3"));
                    if(mercadologico4){
                        imp.setCodMercadologico4(rst.getString("id_mercadologico4"));
                    }
                    imp.setIdFamiliaProduto(rst.getString("id_familia"));
                    imp.setPesoBruto(rst.getDouble("peso"));
                    imp.setPesoLiquido(rst.getDouble("peso"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setCustoComImposto(rst.getDouble("custo"));
                    imp.setCustoSemImposto(rst.getDouble("custo"));
                    imp.setPrecovenda(rst.getDouble("preco"));
                    imp.setSituacaoCadastro(rst.getString("situacao") == null ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getInt("piscofins_cst"));
                    imp.setPiscofinsNaturezaReceita(rst.getInt("piscofins_nat"));
                    imp.setIcmsCreditoId(rst.getString("icms_id"));
                    imp.setIcmsCreditoForaEstadoId(rst.getString("icms_id"));
                    imp.setIcmsDebitoId(rst.getString("icms_id"));
                    imp.setIcmsDebitoForaEstadoId(rst.getString("icms_id"));
                    imp.setIcmsDebitoForaEstadoNfId(rst.getString("icms_id"));
                    imp.setIcmsConsumidorId(rst.getString("icms_id"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    /*
    @Override
    public List<ProdutoIMP> getProdutos(OpcaoProduto opc) throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        if (opc == OpcaoProduto.PRECO) {
            try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "WITH "
                        + "alteracao AS( "
                        + "select pc.widproduto, MAX(wdataalteracao) as dataalteracao "
                        + "FROM precosvenda as pc "
                        + "where pc.widfilial in(" + getLojaOrigem() + ") "
                        + "and pc.wdataalteracao is not null "
                        + "and pc.wstatus is null "
                        + "group by pc.widproduto) "
                        + "select  pc.widproduto,  pc.wdataalteracao, pc.wpreco "
                        + "FROM precosvenda as pc "
                        + "inner join alteracao as alt on alt.widproduto = pc.widproduto and alt.dataalteracao = pc.wdataalteracao "
                        + "where pc.widfilial in (" + getLojaOrigem() + ")"
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("widproduto"));
                        imp.setPrecovenda(rst.getDouble("wpreco"));
                        result.add(imp);
                    }
                }
                return result;
            }
        }
        return null;
    }*/
    @Override
    public List<ProdutoIMP> getProdutosComplemento() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "ean.widcodigopro||'-'||p.widproduto id,\n"
                    + "p.wdatacadastro datacadastro,\n"
                    + "ean.wcodigopro ean,\n"
                    + "ean.wquantidade,\n"
                    + "coalesce(u.wsiglaunidade, 'UN') unidade,\n"
                    + "case when (ean.wbalanca = 'S' or p.wfracionado = 'S') then 1 else 0 end balanca,\n"
                    + "coalesce(p.wvalidade, 0) validade,\n"
                    + "ean.wcomplemento descricaocompleta,\n"
                    + "ean.wcomplemento descricaoreduzida,\n"
                    + "ean.wcomplemento descricaogondola,\n"
                    + "p.widsecao id_mercadologico1,\n"
                    + "p.widprodutogrupo id_mercadologico2,\n"
                    + "p.widprodutosubgrupo id_mercadologico3,\n"
                    + "p.widfamilia id_familia,\n"
                    + "p.wpeso peso,\n"
                    + "coalesce(e.wquantidade, 0) estoque,\n"
                    + "coalesce(p.wlucro, 0) margem,\n"
                    + "coalesce(c.wpreco, 0) custo,\n"
                    + "coalesce(v.wpreco, 0) preco,\n"
                    + "p.wstatus as situacao,\n"
                    + "p.wncm ncm,\n"
                    + "p.wcest cest,\n"
                    + "p.wcstpiscofins piscofins_cst,\n"
                    + "p.wnaturezapis piscofins_nat,\n"
                    + "a.wsigla icms_id\n"
                    + "from\n"
                    + "produtos p\n"
                    + "join filiais f on f.widfilial = " + getLojaOrigem() + "\n"
                    + "left join (select * from codigospro where wnaoreverter is null and wstatus is null) ean on ean.widproduto = p.widproduto\n"
                    + "left join unidades u on p.widunidade = u.widunidade\n"
                    + "left join estoque e on e.widproduto = p.widproduto and e.widfilial = f.widfilial\n"
                    + "left join precoscusto c on c.widproduto = p.widproduto and c.widfilial = f.widfilial\n"
                    + "left join precosvenda v on v.widproduto = p.widproduto and v.widfilial = f.widfilial\n"
                    + "left join aliquotas a on a.widaliquota = p.widaliquotaicms\n"
                    + "where p.wstatus is null\n"
                    + "and ean.wstatus is null\n"
                    + "and ean.wbalanca = 'S'\n"
                    + "and ean.wcomplemento is not null\n"
                    + "order by\n"
                    + "ean.wcodigopro"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setEan(rst.getString("ean"));
                    imp.setTipoEmbalagem(rst.getString("unidade"));
                    imp.seteBalanca(rst.getBoolean("balanca"));
                    imp.setValidade(rst.getInt("validade"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(rst.getString("descricaogondola"));
                    imp.setCodMercadologico1(rst.getString("id_mercadologico1"));
                    imp.setCodMercadologico2(rst.getString("id_mercadologico2"));
                    imp.setCodMercadologico3(rst.getString("id_mercadologico3"));
                    imp.setIdFamiliaProduto(rst.getString("id_familia"));
                    imp.setPesoBruto(rst.getDouble("peso"));
                    imp.setPesoLiquido(rst.getDouble("peso"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setCustoComImposto(rst.getDouble("custo"));
                    imp.setCustoSemImposto(rst.getDouble("custo"));
                    imp.setPrecovenda(rst.getDouble("preco"));
                    imp.setSituacaoCadastro(rst.getString("situacao") == null ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getInt("piscofins_cst"));
                    imp.setPiscofinsNaturezaReceita(rst.getInt("piscofins_nat"));
                    imp.setIcmsCreditoId(rst.getString("icms_id"));
                    imp.setIcmsDebitoId(rst.getString("icms_id"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<NutricionalIMP> getNutricional(Set<OpcaoNutricional> opcoes) throws Exception {
        List<NutricionalIMP> result = new ArrayList<>();

        try (
                Statement st = ConexaoFirebird.getConexao().createStatement();
                ResultSet rs = st.executeQuery(
                        "WITH nut AS (\n"
                        + "SELECT\n"
                        + "	n.WIDNUTRICIONAL,\n"
                        + "	n.WNOMENUTRICIONAL nome,\n"
                        + "	n.WVALORENERGETICO caloria,\n"
                        + "	n.WVALORCARBOIDRATOS carboidrato,\n"
                        + "	n.WVALORPROTEINAS proteina,\n"
                        + "	n.WVALORGORDURASSATURADAS gordurasaturada,\n"
                        + "	n.WVALORGORDURASTRANS gorduratrans,\n"
                        + "	n.WVALORGORDURASTOTAIS gorduratotal,\n"
                        + "	n.WVALORFIBRAALIMENTAR fibras,\n"
                        + "	n.WVALORSODIO sodio,\n"
                        + "	n.WQTDFRACIONADA qtd,\n"
                        + "	n.WQTDUNIDADE tipomedida,\n"
                        + "	n.WQTDINTEIRA qtdinteira,\n"
                        + "	n.WPORCAO porcao\n"
                        + "FROM\n"
                        + "	NUTRICIONAIS n\n"
                        + "WHERE\n"
                        + "	n.WSTATUS IS NULL\n"
                        + "), prod AS (\n"
                        + "SELECT\n"
                        + "	p.WIDPRODUTO id,\n"
                        + "	ean.wcodigopro ean,\n"
                        + "	CASE\n"
                        + "		WHEN (ean.wbalanca = 'S' OR p.wfracionado = 'S') THEN 1\n"
                        + "		ELSE 0\n"
                        + "	END pesavel,\n"
                        + "	p.WIDNUTRICIONAL \n"
                        + "FROM\n"
                        + "	PRODUTOS p\n"
                        + "	LEFT JOIN (\n"
                        + "	    SELECT\n"
                        + "	    	*\n"
                        + "	    FROM\n"
                        + "	    	codigospro\n"
                        + "	    WHERE\n"
                        + "	    	wnaoreverter IS NULL\n"
                        + "	    	AND wstatus IS NULL\n"
                        + "	 ) ean ON\n"
                        + "	 	ean.widproduto = p.widproduto \n"
                        + "WHERE\n"
                        + "	p.WSTATUS IS null\n"
                        + "	AND NOT WIDNUTRICIONAL IS NULL\n"
                        + ")\n"
                        + "SELECT\n"
                        + "	CASE \n"
                        + "		WHEN pesavel = 1 THEN '000' || ean\n"
                        + "		ELSE id\n"
                        + "	END id_produto,\n"
                        + "	nut.WIDNUTRICIONAL,\n"
                        + "	nut.nome,\n"
                        + "	nut.caloria,\n"
                        + "	nut.carboidrato,\n"
                        + "	nut.proteina,\n"
                        + "	nut.gordurasaturada,\n"
                        + "	nut.gorduratrans,\n"
                        + "	nut.gorduratotal,\n"
                        + "	nut.fibras,\n"
                        + "	nut.sodio,\n"
                        + "	nut.tipomedida,\n"
                        + "	nut.qtdinteira,\n"
                        + "	nut.qtd,\n"
                        + "	nut.porcao\n"
                        + "FROM \n"
                        + "	prod\n"
                        + "	JOIN nut ON\n"
                        + "		prod.widnutricional = nut.widnutricional"
                )) {
            while (rs.next()) {
                NutricionalIMP imp = new NutricionalIMP();

                imp.setId(rs.getString("WIDNUTRICIONAL"));
                imp.addProduto(rs.getString("id_produto"));
                imp.setDescricao(rs.getString("nome"));
                imp.setCaloria(rs.getInt("caloria"));
                imp.setCarboidrato(rs.getDouble("carboidrato"));
                imp.setProteina(rs.getDouble("proteina"));
                imp.setGorduraSaturada(rs.getDouble("gordurasaturada"));
                imp.setGorduraTrans(rs.getDouble("gorduratrans"));
                imp.setGordura(rs.getDouble("gorduratotal"));
                imp.setFibra(rs.getDouble("fibras"));
                imp.setSodio(rs.getDouble("sodio"));
                imp.setIdTipoMedida(rs.getInt("tipomedida"));
                imp.setMedidaInteira(rs.getInt("qtdinteira"));
                imp.setPorcao(rs.getString("porcao"));
                imp.calcularPorcentagens();

                result.add(imp);
            }
        }

        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            Map<String, List<FornecedorContatoIMP>> contatos = new HashMap<>();
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "    c.widfornecedor id_fornecedor,\n"
                    + "    c.widfornecedorvendedor id,\n"
                    + "    c.wnomefornecedorvendedor nome,\n"
                    + "    c.wtelefonefornecedorvendedor telefone,\n"
                    + "    c.wcelularfornecedorvendedor celular,\n"
                    + "    c.wemailfornecedorvendedor email\n"
                    + "from\n"
                    + "    fornecedoresvendedores c\n"
                    + "order by\n"
                    + "    id_fornecedor, id"
            )) {
                while (rs.next()) {
                    List<FornecedorContatoIMP> ct = contatos.get(rs.getString("id_fornecedor"));
                    if (ct == null) {
                        ct = new ArrayList<>();
                        contatos.put(rs.getString("id_fornecedor"), ct);
                    }
                    FornecedorContatoIMP imp = new FornecedorContatoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("id"));
                    imp.setImportFornecedorId(rs.getString("id_fornecedor"));
                    imp.setNome(rs.getString("nome"));
                    imp.setTelefone(rs.getString("telefone"));
                    imp.setCelular(rs.getString("celular"));
                    imp.setEmail(rs.getString("email"));
                    ct.add(imp);
                }
            }
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    f.widfornecedor id,\n"
                    + "    f.wnomefornecedor razao,\n"
                    + "    f.wfantasia fantasia,\n"
                    + "    f.wcnpj,\n"
                    + "    f.wcpf, \n"
                    + "    f.winscricaoestadual insc_estadual,\n"
                    + "    f.winscricaomunicipal insc_municipal,\n"
                    + "    f.wsuframa suframa,\n"
                    + "    f.wstatus status,\n"
                    + "    f.wenderecocom endereco,\n"
                    + "    f.wnumero numero,\n"
                    + "    f.wcomplemento complemento,\n"
                    + "    bai.wnomebairro bairro,\n"
                    + "    cd.wibge id_municipio,\n"
                    + "    cd.wnomecidade municipio,\n"
                    + "    uf.wsigla uf,\n"
                    + "    f.wcepcom cep,\n"
                    + "    f.wtelefonecom tel_principal,\n"
                    + "    f.wminimovalor valor_pedido_minimo,\n"
                    + "    f.wminimocaixas qtd_pedido_minimo,\n"
                    + "    f.wfaxcom fax,\n"
                    + "    f.winternetcom web,\n"
                    + "    f.wemailcom email,     \n"
                    + "    f.wvencimento vencimento,\n"
                    + "    f.wdatacadastro datacadastro,\n"
                    + "    f.wobs observacao\n"
                    + "from\n"
                    + "    fornecedores f\n"
                    + "    left join bairros bai on f.widbairrocom = bai.widbairro\n"
                    + "    left join cidades cd on f.widcidadecom = cd.widcidade\n"
                    + "    left join estados uf on cd.widestado = uf.widestado\n"
                    + "order by\n"
                    + "    f.widfornecedor"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    long cpfCnpj = Utils.stringToLong(rst.getString("wcnpj"), -2);
                    if (cpfCnpj < 999999) {
                        cpfCnpj = Utils.stringToLong(rst.getString("wcpf"), -2);
                    }
                    imp.setCnpj_cpf(String.valueOf(cpfCnpj));
                    imp.setIe_rg(rst.getString("insc_estadual"));
                    imp.setInsc_municipal(rst.getString("insc_municipal"));
                    imp.setSuframa(rst.getString("suframa"));
                    imp.setAtivo(true);
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setIbge_municipio(rst.getInt("id_municipio"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setTel_principal(rst.getString("tel_principal"));
                    String fax = Utils.formataNumero(rst.getString("fax"));
                    if (!"0".equals(fax)) {
                        imp.addContato("A", "FAX", fax, "", TipoContato.COMERCIAL, "");
                    }
                    String email = Utils.formataNumero(rst.getString("email"));
                    if (!"0".equals(email)) {
                        imp.addContato("B", "EMAIL", "", "", TipoContato.COMERCIAL, email);
                    }
                    if (rst.getInt("vencimento") > 0) {
                        imp.addPagamento("A", rst.getInt("vencimento"));
                    }
                    imp.setDatacadastro(rst.getDate("datacadastro"));
                    imp.setObservacao(rst.getString("observacao"));

                    List<FornecedorContatoIMP> cts = contatos.get(imp.getImportId());
                    if (cts != null) {
                        for (FornecedorContatoIMP ct : cts) {
                            imp.addContato(
                                    ct.getImportId(),
                                    ct.getNome(),
                                    ct.getTelefone(),
                                    ct.getCelular(),
                                    TipoContato.COMERCIAL,
                                    ct.getEmail()
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

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    pf.widfornecedor,\n"
                    + "    pf.widproduto\n"
                    + "from\n"
                    + "    fornecedorescarteiras pf"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setIdFornecedor(rst.getString("widfornecedor"));
                    imp.setIdProduto(rst.getString("widproduto"));
                    imp.setCodigoExterno(rst.getString("widproduto"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    c.widcliente id,\n"
                    + "    c.wcnpj,\n"
                    + "    c.wcpf,\n"
                    + "    c.winscricaoestadual insc_estadual,\n"
                    + "    c.wemissor orgaoemissor,\n"
                    + "    c.wnomecliente nome,\n"
                    + "    coalesce(c.wfantasia, c.wnomecliente) fantasia,\n"
                    + "    c.wstatus,\n"
                    + "    c.wbloqueado,\n"
                    + "    c.wenderecocom endereco,\n"
                    + "    c.wnumero numero,\n"
                    + "    c.wcomplemento complemento,\n"
                    + "    b.wnomebairro bairro,\n"
                    + "    cd.wibge id_municipio,\n"
                    + "    cd.wnomecidade municipio,\n"
                    + "    uf.wsigla uf,\n"
                    + "    c.wcepcom cep,\n"
                    + "    c.wdatanascimento datanascimento,\n"
                    + "    c.wdatacadastro datacadastro,\n"
                    + "    coalesce(c.wsexo, 1) sexo,\n"
                    + "    c.wdataadmissao dataadmissao,\n"
                    + "    c.wsalario salario,\n"
                    + "    c.wlimite limite,\n"
                    + "    c.wconjuge nomeconjuge,\n"
                    + "    c.wpai nomepai,\n"
                    + "    c.wmae nomemae,\n"
                    + "    coalesce(c.wobscred, '') obs1,\n"
                    + "    coalesce(c.wobsfiscal, '') obs2,\n"
                    + "    c.wvencimento vencimento,\n"
                    + "    c.wtelefonecom telefone,\n"
                    + "    c.wcepcom celular,\n"
                    + "    c.wemailcom email,\n"
                    + "    c.wfaxcom fax,\n"
                    + "    c.winscricaomunicipal insc_municipal\n"
                    + "from\n"
                    + "    clientes c\n"
                    + "    left join bairros b on c.widbairrocom = b.widbairro\n"
                    + "    left join cidades cd on c.widcidadecom = cd.widcidade\n"
                    + "    left join estados uf on cd.widestado = uf.widestado\n"
                    + "where\n"
                    + "    c.widcliente <> 1\n"
                    + "order by\n"
                    + "    c.widcliente"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rst.getString("id"));
                    long cpfCnpj = Utils.stringToLong(rst.getString("wcnpj"), -2);
                    if (cpfCnpj < 999999) {
                        continue;
                        //cpfCnpj = Utils.stringToLong(rst.getString("wcpf"), -2);
                    }
                    imp.setCnpj(String.valueOf(cpfCnpj));
                    imp.setInscricaoestadual(rst.getString("insc_estadual"));
                    imp.setOrgaoemissor(rst.getString("orgaoemissor"));
                    imp.setRazao(rst.getString("nome"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setBloqueado("S".equals(rst.getString("wbloqueado")));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipioIBGE(rst.getInt("id_municipio"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setDataNascimento(rst.getDate("datanascimento"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setSexo(rst.getInt("sexo") == 1 ? TipoSexo.MASCULINO : TipoSexo.FEMININO);
                    imp.setDataAdmissao(rst.getDate("dataadmissao"));
                    imp.setSalario(rst.getDouble("salario"));
                    imp.setValorLimite(rst.getDouble("limite"));
                    imp.setNomeConjuge(rst.getString("nomeconjuge"));
                    imp.setNomePai(rst.getString("nomepai"));
                    imp.setNomeMae(rst.getString("nomemae"));
                    imp.setObservacao2(rst.getString("obs1") + " " + rst.getString("obs2"));
                    imp.setDiaVencimento(rst.getInt("vencimento"));
                    imp.setTelefone(rst.getString("telefone"));
                    imp.setCelular(rst.getString("celular"));
                    imp.setEmail(rst.getString("email"));
                    imp.setFax(rst.getString("fax"));
                    imp.setInscricaoMunicipal(rst.getString("insc_municipal"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ConvenioEmpresaIMP> getConvenioEmpresa() throws Exception {
        List<ConvenioEmpresaIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    f.widfilial id,\n"
                    + "    f.wfantasia nome,\n"
                    + "    f.wcnpj cnpj,\n"
                    + "    f.winscricaoestadual inscricaoestadual,\n"
                    + "    f.wendereco endereco,\n"
                    + "    f.wnumero numero,\n"
                    + "    f.wcomplemento complemento,\n"
                    + "    b.wnomebairro bairro,\n"
                    + "    cd.wibge municipioibge,\n"
                    + "    cd.wnomecidade municipio,\n"
                    + "    uf.wsigla uf,\n"
                    + "    f.wcep cep,\n"
                    + "    f.wtelefonegeral telefone,\n"
                    + "    current_date data_inicio,\n"
                    + "    current_date + 30 data_fim,\n"
                    + "    10 diaPagamento\n"
                    + "from\n"
                    + "    filiais f\n"
                    + "    left join bairros b on f.widbairro = b.widbairro\n"
                    + "    left join cidades cd on f.widcidade = cd.widcidade\n"
                    + "    left join estados uf on cd.widestado = uf.widestado\n"
                    + "where\n"
                    + "    f.widfilial = " + getLojaOrigem()
            )) {
                while (rst.next()) {
                    ConvenioEmpresaIMP imp = new ConvenioEmpresaIMP();

                    imp.setId(rst.getString("id"));
                    imp.setRazao(rst.getString("nome"));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setInscricaoEstadual(rst.getString("inscricaoestadual"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setIbgeMunicipio(rst.getInt("municipioibge"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setTelefone(rst.getString("telefone"));
                    imp.setDataInicio(rst.getDate("data_inicio"));
                    imp.setDataTermino(rst.getDate("data_fim"));
                    imp.setDiaPagamento(rst.getInt("diaPagamento"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ConveniadoIMP> getConveniado() throws Exception {
        List<ConveniadoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    f.widfuncionario id,\n"
                    + "    f.wnomefuncionario nome,\n"
                    + "    f.widfilial filial,\n"
                    + "    case f.wstatus when 'X' then 0 else 1 end situacaocadastro,\n"
                    + "    f.wcpf cpf,\n"
                    + "    f.wobs obs,\n"
                    + "    f.wlimitecompras limite\n"
                    + "from\n"
                    + "    funcionarios f\n"
                    + "where\n"
                    + "    f.widfilial = " + getLojaOrigem()
            )) {
                while (rst.next()) {
                    ConveniadoIMP imp = new ConveniadoIMP();

                    imp.setId(rst.getString("id"));
                    imp.setNome(rst.getString("nome"));
                    imp.setIdEmpresa(rst.getString("filial"));
                    imp.setSituacaoCadastro(SituacaoCadastro.getById(rst.getInt("situacaocadastro")));
                    imp.setCnpj(rst.getString("cpf"));
                    imp.setObservacao(rst.getString("obs"));
                    imp.setConvenioLimite(rst.getDouble("limite"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ConvenioTransacaoIMP> getConvenioTransacao() throws Exception {
        List<ConvenioTransacaoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    vf.widvalefuncionario id,\n"
                    + "    vf.widfuncionario idfuncionario,\n"
                    + "    vf.widpdv pdv,\n"
                    + "    vf.wdata data,\n"
                    + "    vf.wvalor valor,\n"
                    + "    vf.wdatadescontado datamovimentacao,\n"
                    + "    case vf.wbaixado when  'B' then 1 else 0 end baixado,\n"
                    + "    vf.whistorico,\n"
                    + "    vf.wobs\n"
                    + "from\n"
                    + "    valesfuncionarios vf\n"
                    + "where\n"
                    + "    vf.widfilial = " + getLojaOrigem() + " and\n"
                    + "    vf.wstatus is null"
            )) {
                while (rst.next()) {
                    ConvenioTransacaoIMP imp = new ConvenioTransacaoIMP();

                    imp.setId(rst.getString("id"));
                    imp.setIdConveniado(rst.getString("idfuncionario"));
                    imp.setEcf(rst.getString("pdv"));
                    imp.setNumeroCupom(rst.getString("id"));
                    imp.setDataHora(rst.getTimestamp("data"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setDataMovimento(rst.getDate("datamovimentacao"));
                    imp.setFinalizado(rst.getBoolean("baixado"));
                    imp.setObservacao(rst.getString("whistorico") + "  " + rst.getString("wobs"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    a.wsigla,\n"
                    + "    a.wnomealiquota\n"
                    + "from\n"
                    + "    aliquotas a\n"
                    + "where\n"
                    + "    a.wtipo = 1\n"
                    + "order by\n"
                    + "    wsigla"
            )) {
                while (rst.next()) {
                    result.add(
                            new MapaTributoIMP(rst.getString("wsigla"), rst.getString("wnomealiquota"))
                    );
                }
            }
        }

        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    c.widcrediario id,\n"
                    + "    c.wdata dataemissao,\n"
                    + "    c.wpdvcupom cupom,\n"
                    + "    p.wnumpdv pdv,\n"
                    + "    c.wvalor valor,\n"
                    + "    c.wvalormulta multa,\n"
                    + "    c.wvalorjuros juros,\n"
                    + "    c.wobs,\n"
                    + "    c.whistorico,\n"
                    + "    c.widcliente id_cliente,\n"
                    + "    c.wdatavencimento vencimento,\n"
                    + "    c.wparcela parcela\n"
                    + "from\n"
                    + "    crediario c\n"
                    + "    left join pdvs p on c.widpdv = p.widpdv\n"
                    + "where\n"
                    + "    c.wdatabaixa is null and\n"
                    + "    c.widfilial = " + getLojaOrigem() + "\n"
                    + "order by\n"
                    + "    id"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();

                    imp.setId(rst.getString("id"));
                    imp.setDataEmissao(rst.getDate("dataemissao"));
                    imp.setNumeroCupom(rst.getString("cupom"));
                    imp.setParcela(rst.getInt("pdv"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setMulta(rst.getDouble("multa"));
                    imp.setJuros(rst.getDouble("juros"));
                    imp.setObservacao(
                            (Utils.acertarTexto(rst.getString("whistorico"))
                                    + " "
                                    + Utils.acertarTexto(rst.getString("wobs"))).trim()
                    );
                    imp.setIdCliente(rst.getString("id_cliente"));
                    imp.setDataVencimento(rst.getDate("vencimento"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<OfertaIMP> getOfertas(Date dataTermino) throws Exception {
        List<OfertaIMP> result = new ArrayList<>();
        java.sql.Date dataFimOferta, dataInicioOferta;
        DateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
        WorkbookSettings settings = new WorkbookSettings();
        Workbook arquivo = Workbook.getWorkbook(new File(i_arquivoOferta), settings);
        Sheet[] sheets = arquivo.getSheets();
        int linha;

        try {

            for (int sh = 0; sh < sheets.length; sh++) {

                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;

                for (int i = 0; i < sheet.getRows(); i++) {
                    linha++;
                    if (linha == 1) {
                        continue;
                    }

                    Cell cellCodigoBarras = sheet.getCell(2, i);
                    Cell cellPrecoOferta = sheet.getCell(6, i);
                    Cell cellDataInicio = sheet.getCell(7, i);
                    Cell cellDataFinal = sheet.getCell(8, i);

                    dataFimOferta = new java.sql.Date(fmt.parse(cellDataFinal.getContents()).getTime());
                    dataInicioOferta = new java.sql.Date(fmt.parse(cellDataInicio.getContents()).getTime());

                    if (cellCodigoBarras.getContents().length() >= 7) {

                        OfertaIMP imp = new OfertaIMP();
                        imp.setTipoOferta(TipoOfertaVO.CAPA);
                        imp.setIdProduto(new ProdutoAnteriorDAO().getCodigoAnteriorEAN(getSistema(), getLojaOrigem(), cellCodigoBarras.getContents()));
                        imp.setPrecoOferta(Double.parseDouble(cellPrecoOferta.getContents()));
                        imp.setDataInicio(dataInicioOferta);
                        imp.setDataFim(dataFimOferta);
                        result.add(imp);
                    }
                }
            }
            return result;
        } catch (Exception ex) {
            throw ex;
        }
    }
}
