/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import vrimplantacao.classe.ConexaoMySQL;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto.ProdutoAnteriorDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.OpcaoFiscal;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.SituacaoCheque;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoIva;
import vrimplantacao2.vo.importacao.ChequeIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.PautaFiscalIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author lucasrafael
 */
public class G3DAO extends InterfaceDAO implements MapaTributoProvider {

    private boolean lite = false;

    public void setLite(boolean lite) {
        this.lite = lite;
    }

    @Override
    public String getSistema() {
        return "G3";
    }

    public List<Estabelecimento> getLojas() throws SQLException {
        List<Estabelecimento> result = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "   idempresa id,\n"
                    + "	  RazaoSocial razao\n"
                    + "from\n"
                    + "	  empresa e ")) {
                while (rs.next()) {
                    result.add(new Estabelecimento(rs.getInt("id") + "", rs.getString("razao")));
                }
            }
        }
        return result;
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet(Arrays.asList(new OpcaoProduto[]{
            OpcaoProduto.IMPORTAR_MANTER_BALANCA,
            OpcaoProduto.PRODUTOS,
            OpcaoProduto.ATIVO,
            OpcaoProduto.DESC_COMPLETA,
            OpcaoProduto.DESC_GONDOLA,
            OpcaoProduto.DESC_REDUZIDA,
            OpcaoProduto.DATA_CADASTRO,
            OpcaoProduto.EAN,
            OpcaoProduto.EAN_EM_BRANCO,
            OpcaoProduto.TIPO_EMBALAGEM_EAN,
            OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
            OpcaoProduto.CUSTO,
            OpcaoProduto.MARGEM,
            OpcaoProduto.PRECO,
            OpcaoProduto.ESTOQUE,
            OpcaoProduto.PAUTA_FISCAL,
            OpcaoProduto.PAUTA_FISCAL_PRODUTO,
            OpcaoProduto.EXCECAO,
            OpcaoProduto.PESAVEL,
            OpcaoProduto.NCM,
            OpcaoProduto.CEST,
            OpcaoProduto.ICMS,
            OpcaoProduto.ICMS_SAIDA,
            OpcaoProduto.ICMS_SAIDA_FORA_ESTADO,
            OpcaoProduto.ICMS_ENTRADA,
            OpcaoProduto.ICMS_ENTRADA_FORA_ESTADO,
            OpcaoProduto.ICMS_CONSUMIDOR,
            OpcaoProduto.USAR_CONVERSAO_ALIQUOTA_COMPLETA,
            OpcaoProduto.PIS_COFINS,
            OpcaoProduto.NATUREZA_RECEITA,
            OpcaoProduto.ATACADO,
            OpcaoProduto.VALIDADE,
            OpcaoProduto.MERCADOLOGICO,
            OpcaoProduto.MERCADOLOGICO_PRODUTO,
            OpcaoProduto.MAPA_TRIBUTACAO,}));
    }
    /*
     @Override
     public List<MapaTributoIMP> getTributacao() throws Exception {
     List<MapaTributoIMP> result = new ArrayList();
     try (Statement stmt = ConexaoMySQL.getConexao().createStatement()) {
     try (ResultSet rs = stmt.executeQuery(
     " select distinct\n"
     + "	 idCadTributacao id,\n"
     + "	 c.descricao,\n"
     + "	 aliquotaIcms,\n"
     + "	 coalesce(RedBaseVenda,0) reducao\n"
     + "from\n"
     + "	 cadtributacao c \n"
     + "	   left join produto p on c.idCadTributacao = p.SitTrib"
     )) {
     while (rs.next()) {
     result.add(new MapaTributoIMP(rs.getString("id"),
     rs.getString("descricao") 
     + "(ALI: " + rs.getString("aliquotaIcms")
     + " RED: " + rs.getString("reducao") + ")"));
     }
     }
     }
     return result;
     }*/

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    " select distinct\n"
                    + "	 idCadTributacao id,\n"
                    + "	 c.descricao,\n"
                    + "	 aliquotaIcms,\n"
                    + "	 coalesce(RedBaseVenda,0) reducao\n"
                    + "from\n"
                    + "	 cadtributacao c \n"
                    + "	   left join produto p on c.idCadTributacao = p.SitTrib"
            )) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(rs.getString("id"),
                            rs.getString("descricao")
                            + "(ALI: " + rs.getString("aliquotaIcms")
                            + " RED: " + rs.getString("reducao") + ")"));
                }
            }
            try (ResultSet rs = stm.executeQuery(
                    "select distinct \n"
                    + "icmscompra aliquota,\n"
                    + "RedBase reducao,\n"
                    + "substring(tabicmsprodentrada,1,2) cst\n"
                    + "from produto p  "
            )) {
                while (rs.next()) {
                    String id = getAliquotaCreditoKey(
                            rs.getString("cst"),
                            rs.getDouble("aliquota"),
                            rs.getDouble("reducao")
                    );
                    result.add(new MapaTributoIMP(
                            id,
                            id,
                            Utils.stringToInt(rs.getString("cst")),
                            rs.getDouble("aliquota"),
                            rs.getDouble("reducao")
                    ));
                }
            }
        }
        return result;
    }

    private String getAliquotaCreditoKey(String cst, double aliq, double red) throws SQLException {
        return String.format(
                "%s-%.2f-%.2f",
                cst,
                aliq,
                red
        );
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    /*"SELECT \n"
                     + "	idgrupo, nome\n"
                     + "FROM grupo\n"
                     + "ORDER BY idgrupo"*/
                    "select\n"
                    + "     m1.idgrupo as m1grupo,\n"
                    + "     m1.nome as m1desc,\n"
                    + "     m2.idSubGrupo as m2subgrupo,\n"
                    + "     m2.Nome as m2desc,\n"
                    + "     m3.idsubgrupo1 as m3subgrupo2,\n"
                    + "     m3.nome as m3desc\n"
                    + "from grupo m1 \n"
                    + "	left join subgrupo m2\n"
                    + "		on m2.idGrupo = m1.idgrupo \n"
                    + "	left join subgrupo1 m3\n"
                    + "		on m3.idsubgrupo = m2.idSubGrupo and m3.idsubgrupo = m2.idSubGrupo \n"
                    + "order by m1grupo, m2subgrupo, m3subgrupo2"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rst.getString("m1grupo"));
                    imp.setMerc1Descricao(rst.getString("m1desc"));
                    imp.setMerc2ID(rst.getString("m2subgrupo"));
                    imp.setMerc2Descricao(rst.getString("m2desc"));
                    imp.setMerc3ID(rst.getString("m3subgrupo2"));
                    imp.setMerc3Descricao(rst.getString("m3desc"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    /*"SELECT \n"
                     + "	p.ID AS id,\n"
                     + "	p.DESCRICAO_PDV AS descricao,\n"
                     + "	p.ID_GRUPO AS mercadologico,\n"
                     + "	TRUNCATE(p.lucro,2) margem,\n"
                     + "	TRUNCATE(p.valor_compra, 2) custosemimposto,\n"
                     + "	TRUNCATE(p.valor_custo, 2) custocomimposto,\n"
                     + "	TRUNCATE(p.VALOR_VENDA, 2) precovenda,\n"
                     + "	p.DATA_CADASTRO AS datacadastro,\n"
                     + "	TRUNCATE(p.ESTOQUE_MAX, 0) estoquemaximo,\n"
                     + "	TRUNCATE(p.ESTOQUE_MIN, 0) estoqueminimo,\n"
                     + "	p.QTD_ESTOQUE AS estoque,\n"
                     + "	p.GTIN,\n"
                     + "	p.EAN,\n"
                     + "	u.NOME AS tipoembalagem,\n"
                     + "	p.NCM AS ncm,\n"
                     + "	p.CEST AS cest,\n"
                     + "	p.CST_PIS_SAIDA,\n"
                     + "	p.CST_PIS_ENTRADA,\n"
                     + "	p.CST_COFINS_SAIDA,\n"
                     + "	p.CST_COFINS_ENTRADA,\n"
                     + "	gps.cst AS cst_grupo_pis_saida,\n"
                     + "	gpe.cst AS cst_grupo_pis_entrada,\n"
                     + "	gcs.cst AS cst_grupo_cofins_saida,\n"
                     + "	gcs.cst AS cst_grupo_cofins_entrada,\n"
                     + "	p.cod_nat_rec AS naturezareceita,\n"
                     + "	p.COD_CST_DENTRO,\n"
                     + "	p.COD_CST_FORA,\n"
                     + "	p.ALIQUOTA_ICMS_DENTRO,\n"
                     + "	p.ALIQUOTA_ICMS_FORA,\n"
                     + "	p.REDUCAO_BC_DENTRO,\n"
                     + "	p.REDUCAO_BC_FORA,\n"
                     + "	p.ECF_ICMS_ST AS aliquotaconsumidor,\n"
                     + "	case p.DESATIVADO when 0 then 'ATIVO' ELSE 'INATIVO' end situacaocadastro\n"
                     + "FROM produto p\n"
                     + "LEFT JOIN unidade_produto u ON u.ID = p.ID_UNIDADE_PRODUTO\n"
                     + "LEFT JOIN grupopis gps ON gps.id = p.id_grupo_pis_saida\n"
                     + "LEFT JOIN grupopis gpe ON gpe.id = p.id_grupo_pis_entrada\n"
                     + "LEFT JOIN grupocofins gcs ON gcs.id = p.id_grupo_cofins_saida\n"
                     + "LEFT JOIN grupocofins gce ON gce.id = p.id_grupo_cofins_entrada\n"
                     + "ORDER BY p.ID"*/
                    "select \n" +
"	p.idproduto AS id,\n" +
"	p.descricao,\n" +
"	p.descrred reduzida,\n" +
"	descricaoetq gondola,\n" +
"	p.idgrupo AS mercadologico1,\n" +
"	p.idsubgrupo as mercadologico2,\n" +
"	p.idsubgrupo1 as mercadologico3,\n" +
"	pp.margem,\n" +
"	pp.custo custosemimposto,\n" +
"	pp.custo custocomimposto,\n" +
"	pp.venda1 precovenda,\n" +
"	dtcadastro datacadastro,\n" +
"	coalesce(pesoproduto,0) pesobruto,\n" +
"	coalesce(pesovariavel,0) pesoliquido,\n" +
"	estmax estoquemaximo,\n" +
"	estmin estoqueminimo,\n" +
"	estoque_atual AS estoque,\n" +
"	ean,\n" +
"	unidsaida tipoembalagem,\n" +
"	classfiscal AS ncm,\n" +
"	p.cest as cest,\n" +
"	case p.idsituacao when 1 then 'ATIVO' ELSE 'INATIVO' end situacaocadastro,\n" +
"	concat('0', substr(p.tabIcmsProdEntrada, 1, 3)) as icms_cst_e,\n" +
"	p.IcmsCompra as icms_alqt_e,\n" +
"	p.RedBase as icms_rbc_e,\n" +
"	concat('0', substr(p.TabIcmsProd, 1, 3)) as icms_cst_s,\n" +
"	p.Icms as icms_alqt_s,\n" +
"	p.RedBaseVenda as icms_rbc_s,\n" +
"	substr(p.CST_PIS,1,2) as piscofins_cst_e,\n" +
"	substr(p.CST_PIS_SAIDA,1,2) as piscofins_cst_s,\n" +
" p.icmscompra aliquota,\n" +
" p.RedBase reducao,\n" +
" substring(p.tabicmsprodentrada,1,2) cst,\n" +
"	coalesce(nat_receita,'') naturezareceita,\n" +
" p.sittrib as icms \n" +
"FROM produto p \n" +
"	left join produto_estoque pe\n" +
"		on pe.idproduto = p.idproduto\n" +
"	left join produto_preco pp\n" +
"		on pp.idproduto = p.idproduto and pe.id_loja = pp.id_loja\n" +
" left join cadtributacao ct\n" +
"		on p.SitTrib = ct.idCadTributacao"
            )) {
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().carregarProdutosBalanca();
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    ProdutoBalancaVO produtoBalanca;
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    String ean = Utils.formataNumero(rst.getString("EAN"));

                    long codigoProduto;

                    if ((ean != null)
                            && (!ean.trim().isEmpty())) {

                        if (ean.trim().length() == 4) {

                            codigoProduto = Long.parseLong(ean);
                            if (codigoProduto <= Integer.MAX_VALUE) {
                                produtoBalanca = produtosBalanca.get((int) codigoProduto);
                            } else {
                                produtoBalanca = null;
                            }

                            if (produtoBalanca != null) {
                                imp.seteBalanca(true);
                                imp.setValidade(produtoBalanca.getValidade() > 1 ? produtoBalanca.getValidade() : 0);
                            } else {
                                imp.setValidade(0);
                                imp.seteBalanca(false);
                            }

                        } else {
                            imp.seteBalanca(false);
                        }
                    } else {
                        imp.seteBalanca(false);
                    }

                    imp.setImportId(rst.getString("id"));
                    imp.setDescricaoCompleta(rst.getString("descricao"));
                    imp.setDescricaoReduzida(rst.getString("reduzida"));
                    imp.setDescricaoGondola(rst.getString("gondola"));
                    imp.setCodMercadologico1(rst.getString("mercadologico1"));
                    imp.setCodMercadologico2(rst.getString("mercadologico2"));
                    imp.setCodMercadologico3(rst.getString("mercadologico3"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setPesoBruto(rst.getDouble("pesobruto"));
                    imp.setPesoLiquido(rst.getDouble("pesoliquido"));
                    imp.setEstoqueMaximo(rst.getDouble("estoquemaximo"));
                    imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setEan(ean);
                    imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setSituacaoCadastro("ATIVO".equals(rst.getString("situacaocadastro")) ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);

                    // PIS COFINS
                    imp.setPiscofinsCstDebito(rst.getString("piscofins_cst_s"));
                    imp.setPiscofinsCstCredito(rst.getString("piscofins_cst_e"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("naturezareceita"));

                    /*// ICMS SAIDA DENTRO ESTADO
                     imp.setIcmsCstSaida(rst.getInt("icms_cst_s"));
                     imp.setIcmsAliqSaida(rst.getDouble("icms_alqt_s"));
                     imp.setIcmsReducaoSaida(rst.getDouble("icms_rbc_s"));

                     // ICMS SAIDA FORA ESTADO
                     imp.setIcmsCstSaidaForaEstado(rst.getInt("icms_cst_s"));
                     imp.setIcmsAliqSaidaForaEstado(rst.getDouble("icms_alqt_s"));
                     imp.setIcmsReducaoSaidaForaEstado(rst.getDouble("icms_rbc_s"));

                     // ICMS SAIDA FORA ESTADO NF
                     imp.setIcmsCstSaidaForaEstadoNF(rst.getInt("icms_cst_s"));
                     imp.setIcmsAliqSaidaForaEstadoNF(rst.getDouble("icms_alqt_s"));
                     imp.setIcmsReducaoSaidaForaEstadoNF(rst.getDouble("icms_rbc_s"));

                     // ICMS CONSUMIDOR
                     imp.setIcmsCstConsumidor(rst.getInt("icms_cst_s"));
                     imp.setIcmsAliqConsumidor(rst.getDouble("icms_alqt_s"));
                     imp.setIcmsReducaoConsumidor(rst.getDouble("icms_rbc_s"));

                     // ICMS ENTRADA DENTRO ESTADO
                     imp.setIcmsCstEntrada(rst.getInt("icms_cst_e"));
                     imp.setIcmsAliqEntrada(rst.getDouble("icms_alqt_e"));
                     imp.setIcmsReducaoEntrada(rst.getDouble("icms_rbc_e"));

                     // ICMS ENTRADA FORA ESTADO
                     imp.setIcmsCstEntradaForaEstado(rst.getInt("icms_cst_e"));
                     imp.setIcmsAliqEntradaForaEstado(rst.getDouble("icms_alqt_e"));
                     imp.setIcmsReducaoEntradaForaEstado(rst.getDouble("icms_rbc_e"));
                     */
                    imp.setIcmsDebitoId(rst.getString("icms"));
                    imp.setIcmsDebitoForaEstadoId(rst.getString("icms"));
                    imp.setIcmsDebitoForaEstadoNfId(rst.getString("icms"));

                    String icmsCre = getAliquotaCreditoKey(
                            rst.getString("cst"),
                            rst.getDouble("aliquota"),
                            rst.getDouble("reducao")
                    );
                    imp.setIcmsCreditoId(icmsCre);
                    imp.setIcmsCreditoForaEstadoId(icmsCre);

                    imp.setIcmsConsumidorId(rst.getString("icms"));

                    imp.setManterEAN(Utils.stringToLong(imp.getEan()) <= 999999);

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<PautaFiscalIMP> getPautasFiscais(Set<OpcaoFiscal> opcoes) throws Exception {
        List<PautaFiscalIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "     p.idproduto importid,\n"
                    + "     p.ClassFiscal ncm,\n"
                    + "     p.iva per_iva,\n"
                    + "     substring(tabicmsprod,1,2) cst_debito,\n"
                    + "     p.icms aliquota_debito,\n"
                    + "     redbasevenda reducao_debito,\n"
                    + "     substring(tabicmsprodentrada,1,2) cst_credito,\n"
                    + "     icmscompra aliquota_credito,\n"
                    + "     p.redbase reducao_credito\n"
                    + "from produto p\n"
                    + "     where p.iva > 0 and  p.ClassFiscal is not null and p.ClassFiscal != ''"
            )) {
                while (rst.next()) {
                    PautaFiscalIMP imp = new PautaFiscalIMP();

                    imp.setId(rst.getString("importid"));
                    imp.setTipoIva(TipoIva.VALOR);
                    imp.setIva(rst.getDouble("per_iva"));
                    imp.setIvaAjustado(imp.getIva());
                    imp.setNcm(rst.getString("ncm"));

                    // DÉBITO
                    if ((rst.getDouble("aliquota_debito") > 0) && (rst.getDouble("reducao_debito") == 0)) {

                        imp.setAliquotaDebito(0, rst.getDouble("aliquota_debito"), rst.getDouble("reducao_debito"));
                        imp.setAliquotaDebitoForaEstado(0, rst.getDouble("aliquota_debito"), rst.getDouble("reducao_debito"));

                    } else if ((rst.getDouble("aliquota_debito") > 0) && (rst.getDouble("reducao_debito") > 0)) {

                        imp.setAliquotaDebito(20, rst.getDouble("aliquota_debito"), rst.getDouble("reducao_debito"));
                        imp.setAliquotaDebitoForaEstado(20, rst.getDouble("aliquota_debito"), rst.getDouble("reducao_debito"));

                    } else {

                        imp.setAliquotaDebito(rst.getInt("cst_debito"), rst.getDouble("aliquota_debito"), rst.getDouble("reducao_debito"));
                        imp.setAliquotaDebitoForaEstado(rst.getInt("cst_debito"), rst.getDouble("aliquota_debito"), rst.getDouble("reducao_debito"));
                    }

                    // CRÉDITO
                    if ((rst.getDouble("aliquota_credito") > 0) && (rst.getDouble("reducao_credito") == 0)) {

                        imp.setAliquotaCredito(0, rst.getDouble("aliquota_credito"), rst.getDouble("reducao_credito"));
                        imp.setAliquotaCreditoForaEstado(0, rst.getDouble("aliquota_credito"), rst.getDouble("reducao_credito"));

                    } else if ((rst.getDouble("aliquota_credito") > 0) && (rst.getDouble("reducao_credito") > 0)) {

                        imp.setAliquotaCredito(20, rst.getDouble("aliquota_credito"), rst.getDouble("reducao_credito"));
                        imp.setAliquotaCreditoForaEstado(20, rst.getDouble("aliquota_credito"), rst.getDouble("reducao_credito"));

                    } else {

                        imp.setAliquotaCredito(rst.getInt("cst_credito"), rst.getDouble("aliquota_credito"), rst.getDouble("reducao_credito"));
                        imp.setAliquotaCreditoForaEstado(rst.getInt("cst_credito"), rst.getDouble("aliquota_credito"), rst.getDouble("reducao_credito"));
                    }

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos(OpcaoProduto opt) throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        if (opt == OpcaoProduto.EXCECAO) {
            try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select\n"
                        + "     p.idproduto importid,\n"
                        + "     p.ClassFiscal ncm,\n"
                        + "     p.iva per_iva,\n"
                        + "     substring(tabicmsprod,1,2) cst_debito,\n"
                        + "     p.icms aliquota_debito,\n"
                        + "     redbasevenda reducao_debito,\n"
                        + "     substring(tabicmsprodentrada,1,2) cst_credito,\n"
                        + "     icmscompra aliquota_credito,\n"
                        + "     p.redbase reducao_credito\n"
                        + "from produto p\n"
                        + "     where p.iva > 0 and  p.ClassFiscal is not null and p.ClassFiscal != ''"
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("importid"));
                        imp.setPautaFiscalId(imp.getImportId());
                        result.add(imp);
                    }
                }
                return result;
            }
        }

        if (opt == OpcaoProduto.ATACADO) {
            try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "SELECT \n"
                        + "	id, \n"
                        + "	qtd_atacado,\n"
                        + "	TRUNCATE(valor_venda_atacado, 2) precoatacado,\n"
                        + "	truncate(valor_venda, 2) precovenda\n"
                        + "FROM produto \n"
                        + "WHERE qtd_atacado > 1\n"
                        + "AND coalesce(valor_venda_atacado, 0) > 0"
                )) {
                    while (rst.next()) {
                        int codigoAtual = new ProdutoAnteriorDAO().getCodigoAnterior2(getSistema(), getLojaOrigem(), rst.getString("id"));

                        if (codigoAtual > 0) {

                            ProdutoIMP imp = new ProdutoIMP();
                            imp.setImportLoja(getLojaOrigem());
                            imp.setImportSistema(getSistema());
                            imp.setImportId(rst.getString("id"));
                            imp.setEan("999999" + String.valueOf(codigoAtual));
                            imp.setQtdEmbalagem(rst.getInt("qtd_atacado"));
                            imp.setPrecovenda(rst.getDouble("precovenda"));
                            imp.setAtacadoPreco(rst.getDouble("precoatacado"));
                            result.add(imp);
                        }
                    }
                }
            }
            return result;
        }
        return null;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	idcliente id,\n"
                    + "	cpf,\n"
                    + " rg,\n"
                    + "	nome razao,\n"
                    + "	nome fantasia,\n"
                    + "	status_cadastro ativo,\n"
                    + "	endereco,\n"
                    + "	numero,\n"
                    + "	complemento,\n"
                    + "	bairro,\n"
                    + "	codmunicipio municipioIBGE,\n"
                    + "	cidade,\n"
                    + "	cUf ufIBGE,\n"
                    + "	uf estado,\n"
                    + "	cep,\n"
                    + "	dt_nasc dataNascimento,\n"
                    + "	dtabertura dataCadastro,\n"
                    + "	coalesce(empresa,'') empresa,\n"
                    + "	coalesce(fone_emp,'') empresaTelefone,\n"
                    + "	salario,\n"
                    + "	limite valorLimite,\n"
                    + "	coalesce(conjuge,'') nomeConjuge,\n"
                    + "	obs observacao,\n"
                    + "	coalesce(vencimento,'') diaVencimento,\n"
                    + "	fone telefone,\n"
                    + "	celular,\n"
                    + "	coalesce(email,'') email,\n"
                    + "	enderecocob cobrancaEndereco,\n"
                    + "	numerocob cobrancaNumero,\n"
                    + "	complementocob cobrancaComplemento,\n"
                    + "	bairrocob cobrancaBairro,\n"
                    + "	cidadecob cobrancaMunicipio,\n"
                    + "	ufcob cobrancaUf,\n"
                    + "	cepcob cobrancaCep\n"
                    + "from cliente c")) {
                while (rs.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rs.getString("id"));
                    imp.setCnpj(rs.getString("cpf"));
                    imp.setInscricaoestadual(rs.getString("rg"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setAtivo(rs.getBoolean("ativo"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipioIBGE(rs.getString("municipioIBGE"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setMunicipioIBGE(rs.getString("ufIBGE"));
                    imp.setUf(rs.getString("estado"));
                    imp.setCep(rs.getString("cep"));
                    imp.setDataNascimento(rs.getDate("dataNascimento"));
                    imp.setDataCadastro(rs.getDate("datacadastro"));
                    imp.setEmpresa(rs.getString("empresa"));
                    imp.setEmpresaTelefone(rs.getString("empresaTelefone"));
                    imp.setSalario(rs.getDouble("salario"));
                    imp.setValorLimite(rs.getDouble("valorlimite"));
                    imp.setNomeConjuge(rs.getString("nomeconjuge"));
                    imp.setObservacao(rs.getString("observacao"));
                    imp.setDiaVencimento(rs.getInt("diavencimento"));
                    imp.setTelefone(rs.getString("telefone"));
                    imp.setCelular(rs.getString("celular"));
                    imp.setEmail(rs.getString("email"));

                    imp.setCobrancaEndereco(rs.getString("cobrancaEndereco"));
                    imp.setCobrancaNumero(rs.getString("cobrancaNumero"));
                    imp.setCobrancaComplemento(rs.getString("cobrancaComplemento"));
                    imp.setCobrancaBairro(rs.getString("cobrancaBairro"));
                    imp.setCobrancaMunicipio(rs.getString("cobrancaMunicipio"));
                    imp.setCobrancaUf(rs.getString("cobrancaUf"));
                    imp.setCobrancaCep(rs.getString("cobrancaCep"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> Result = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	iddebito id,\n"
                    + "	dt_venda dataEmissao,\n"
                    + "	nr_venda numeroCupom,\n"
                    + "	ecf,\n"
                    + "	vl_vista valor,\n"
                    + "	observacao,\n"
                    + "	r.idCliente,\n"
                    + "	cpf cnpjCliente,\n"
                    + "	dt_venc dataVencimento\n"
                    + "from debito r \n"
                    + "	left join cliente c\n"
                    + "		on r.IDCLIENTE = c.idCliente \n"
                    + "where SITUACAO != 'P'\n"
                    + "	and r.loja = " + getLojaOrigem() + ""
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();

                    imp.setId(rst.getString("id"));
                    imp.setDataEmissao(rst.getDate("dataemissao"));
                    imp.setNumeroCupom(rst.getString("numerocupom"));
                    imp.setEcf(rst.getString("ecf"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setObservacao(rst.getString("observacao"));
                    imp.setIdCliente(rst.getString("idCliente"));
                    imp.setCnpjCliente(rst.getString("cnpjCliente"));
                    imp.setDataVencimento(rst.getDate("dataVencimento"));

                    Result.add(imp);
                }
            }
        }
        return Result;
    }

    @Override
    public List<ChequeIMP> getCheques() throws Exception {
        List<ChequeIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    " \n"
                    + "select\n"
                    + "	idchequepre id,\n"
                    + "	cgc_cpf cpf,\n"
                    + "	cheque numerocheque,\n"
                    + "	banco,\n"
                    + "	ch.agencia,\n"
                    + "	ch.conta,\n"
                    + "	emissao,\n"
                    + "	dt_baixa datadeposito,\n"
                    + "	cupom numerocupom,\n"
                    + "	ecf,\n"
                    + "	valor,\n"
                    + "	c.rg,\n"
                    + "	c.fone telefone,\n"
                    + "	c.nome,\n"
                    + "	ch.obs observacao,\n"
                    + "	situacao situacaocheque,\n"
                    + "	datahora_alteracao alteracao\n"
                    + "from\n"
                    + "	chequepre ch\n"
                    + "	left join cliente c\n"
                    + "		on c.idCliente = ch.idCliente \n"
                    + "where Situacao != 'P'\n"
                    + "and ch.loja = " + getLojaOrigem() + ""
            )) {
                while (rst.next()) {
                    ChequeIMP imp = new ChequeIMP();

                    imp.setId(rst.getString("id"));
                    imp.setCpf(rst.getString("cpf"));
                    imp.setNumeroCheque(rst.getString("numerocheque"));
                    imp.setBanco(rst.getInt("banco"));
                    imp.setAgencia(rst.getString("agencia"));
                    imp.setConta(rst.getString("conta"));
                    imp.setDate(rst.getDate("datadeposito"));
                    imp.setNumeroCupom(rst.getString("numerocupom"));
                    imp.setEcf(rst.getString("ecf"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setRg(rst.getString("rg"));
                    imp.setTelefone(rst.getString("telefone"));
                    imp.setNome(rst.getString("nome"));
                    imp.setObservacao(rst.getString("observacao"));
                    imp.setSituacaoCheque(("A".equals(rst.getString("situacaocheque")) ? SituacaoCheque.ABERTO : SituacaoCheque.BAIXADO));
                    imp.setDataHoraAlteracao(rst.getTimestamp("alteracao"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + "	f.idfornecedor,\n"
                    + "	f.nome,\n"
                    + "	f.fantasia,\n"
                    + "	f.CPF_CGC AS cnpj,\n"
                    + "	f.RG_IE ie,\n"
                    + "	f.endereco,\n"
                    + "	f.numero,\n"
                    + "	f.complemento,\n"
                    + "	f.bairro,\n"
                    + "	f.cep,\n"
                    + "	f.CIDADE,\n"
                    + "	f.codmunicipio,\n"
                    + "	f.uf,\n"
                    + "	f.contato,\n"
                    + "	f.email,\n"
                    + "	f.fax,\n"
                    + "	f.telefone,\n"
                    + "	f.DTCADASTRO,\n"
                    + " f.obs\n"
                    + "FROM fornecedor f\n"
                    + "ORDER BY f.idfornecedor"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("idfornecedor"));
                    imp.setRazao(rst.getString("nome"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    imp.setIe_rg(rst.getString("ie"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setCep(rst.getString("cep"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setIbge_municipio(rst.getInt("codmunicipio"));
                    imp.setUf(rst.getString("uf"));
                    imp.setTel_principal(rst.getString("telefone"));
                    imp.setDatacadastro(rst.getDate("DTCADASTRO"));
                    imp.setObservacao(rst.getString("obs"));
                    //imp.setAtivo("1".equals(rst.getString("ativo")));

                    if ((rst.getString("contato") != null)
                            && (!rst.getString("contato").trim().isEmpty())) {
                        imp.setObservacao("CONTATO - " + rst.getString("contato"));
                    }

                    if ((rst.getString("email") != null)
                            && (!rst.getString("email").trim().isEmpty())) {
                        imp.addEmail(
                                "EMAIL",
                                rst.getString("email").toLowerCase(),
                                TipoContato.NFE
                        );
                    }
                    if ((rst.getString("fax") != null)
                            && (!rst.getString("fax").trim().isEmpty())) {
                        imp.addTelefone(
                                "FAX",
                                rst.getString("fax").toLowerCase()
                        );
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

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "  idFornecedor,\n"
                    + "  idProduto,\n"
                    + "  Referencia codexterno,\n"
                    + "  Embalagem qtdembalagem\n"
                    + "from\n"
                    + "  itensfornecedor\n"
                    + "order by \n"
                    + "	idfornecedor "
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rst.getString("idProduto"));
                    imp.setIdFornecedor(rst.getString("idFornecedor"));
                    imp.setCodigoExterno(rst.getString("codexterno"));
                    imp.setQtdEmbalagem(rst.getDouble("qtdembalagem"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ContaPagarIMP> getContasPagar() throws Exception {
        List<ContaPagarIMP> Result = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "   idParcelaPagar idPagar,\n"
                    + "   p.idFornecedor,\n"
                    + "   f.CPF_CGC cnpj,\n"
                    + "   doc documento,\n"
                    + "   p.dt_entrada dtentrada,\n"
                    + "   p.dt_emissao dtemissao,\n"
                    + "   p.dt_entrada dtalteracao,\n"
                    + "   valor,\n"
                    + "   venc dtvencto,\n"
                    + "   p.obs,\n"
                    + "   p.historico ob2\n"
                    + "from parcelapagar pp\n"
                    + "	left join pagar p on p.idPagar = pp.idPagar \n"
                    + "	left join fornecedor f on f.IDFORNECEDOR = p.idFornecedor \n"
                    + "where p.situacao is null and loja = " + getLojaOrigem() + ""
            )) {
                while (rst.next()) {
                    ContaPagarIMP imp = new ContaPagarIMP();
                    imp.setId(rst.getString("idPagar"));
                    imp.setIdFornecedor(rst.getString("idFornecedor"));
                    imp.setCnpj(rst.getString("cnpj"));

                    String doc = Utils.formataNumero(rst.getString("documento"));

                    imp.setNumeroDocumento(doc);

                    if (doc != null && !"".equals(doc)) {
                        if (doc.length() > 6) {
                            imp.setNumeroDocumento(doc.substring(0, 6));
                        }
                    }

                    imp.setDataEntrada(rst.getDate("dtentrada"));
                    imp.setDataEmissao(rst.getDate("dtemissao"));
                    imp.setDataHoraAlteracao(rst.getTimestamp("dtalteracao"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setObservacao((rst.getString("obs") == null ? "" : rst.getString("obs")) + " "
                            + (rst.getString("ob2") == null ? "" : rst.getString("ob2")));
                    imp.addVencimento(rst.getDate("dtvencto"), imp.getValor());

                    Result.add(imp);
                }
            }
        }
        return Result;
    }
}
