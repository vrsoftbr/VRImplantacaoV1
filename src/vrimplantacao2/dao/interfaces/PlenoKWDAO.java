/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import vrframework.classe.Conexao;
import vrimplantacao.classe.ConexaoMySQL;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Lucas
 */
public class PlenoKWDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "PlenoKW";
    }

    private String getAliquotaKey(String cst, double aliq, double red, double fcp) throws Exception {
        return String.format(
                "%s-%.2f-%.2f-%.2f",
                cst,
                aliq,
                red,
                fcp
        );
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	cfg06_id as id,\n"
                    + "	cfg06_nome as nome\n"
                    + "from cfg06_filial cf \n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(
                            rst.getString("id"), rst.getString("nome")));
                }
            }
        }
        return result;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select distinct\n"
                    + "	cst_icms,\n"
                    + "	aliq_icms,\n"
                    + "	red_icms,\n"
                    + "	fcp\n"
                    + "from implantacao.tributacao_produtos_plenokw\n"
                    + "order by 1, 2, 3"
            )) {
                while (rst.next()) {
                    String id = getAliquotaKey(
                            rst.getString("cst_icms"),
                            rst.getDouble("aliq_icms"),
                            rst.getDouble("red_icms"),
                            rst.getDouble("fcp")
                    );

                    result.add(new MapaTributoIMP(
                            id,
                            id,
                            rst.getInt("cst_icms"),
                            rst.getDouble("aliq_icms"),
                            rst.getDouble("red_icms"),
                            rst.getDouble("fcp"),
                            false,
                            0
                    ));
                }
            }
        }
        return result;
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                new OpcaoProduto[]{
                    OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                    OpcaoProduto.MERCADOLOGICO_PRODUTO,
                    OpcaoProduto.MERCADOLOGICO,
                    OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                    OpcaoProduto.FAMILIA_PRODUTO,
                    OpcaoProduto.FAMILIA,
                    OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                    OpcaoProduto.MANTER_DESCRICAO_PRODUTO,
                    OpcaoProduto.PRODUTOS,
                    OpcaoProduto.PRODUTOS_BALANCA,
                    OpcaoProduto.EAN,
                    OpcaoProduto.EAN_EM_BRANCO,
                    OpcaoProduto.DATA_CADASTRO,
                    OpcaoProduto.TIPO_EMBALAGEM_EAN,
                    OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                    OpcaoProduto.QTD_EMBALAGEM_COTACAO,
                    OpcaoProduto.QTD_EMBALAGEM_EAN,
                    OpcaoProduto.PESAVEL,
                    OpcaoProduto.VALIDADE,
                    OpcaoProduto.DESC_COMPLETA,
                    OpcaoProduto.DESC_GONDOLA,
                    OpcaoProduto.DESC_REDUZIDA,
                    OpcaoProduto.ESTOQUE_MAXIMO,
                    OpcaoProduto.ESTOQUE_MINIMO,
                    OpcaoProduto.PRECO,
                    OpcaoProduto.CUSTO,
                    OpcaoProduto.CUSTO_COM_IMPOSTO,
                    OpcaoProduto.CUSTO_SEM_IMPOSTO,
                    OpcaoProduto.ESTOQUE,
                    OpcaoProduto.ATIVO,
                    OpcaoProduto.NCM,
                    OpcaoProduto.CEST,
                    OpcaoProduto.PIS_COFINS,
                    OpcaoProduto.NATUREZA_RECEITA,
                    OpcaoProduto.ICMS,
                    OpcaoProduto.PAUTA_FISCAL,
                    OpcaoProduto.PAUTA_FISCAL_PRODUTO,
                    OpcaoProduto.MARGEM,
                    OpcaoProduto.OFERTA,
                    OpcaoProduto.EXCECAO,
                    OpcaoProduto.TIPO_PRODUTO,
                    OpcaoProduto.ATACADO,
                    OpcaoProduto.CODIGO_BENEFICIO,
                    OpcaoProduto.MAPA_TRIBUTACAO
                }
        ));
    }

    @Override
    public List<ProdutoIMP> getProdutosBalanca() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	p.mcd01_id,\n"
                    + "	p.mcd01_codint,\n"
                    + "	ean.mcd02_codigo as codigobarras,\n"
                    + "	ean.mcd02_multiplicador as qtdembalagem,\n"
                    + "	un.adm01_unidade as tipoembalagem,\n"
                    + "	p.mcd01_flgbalanca as balanca,\n"
                    + "	p.mcd01_validade as validade,\n"
                    + "	p.mcd01_descricao as descricaocompleta,\n"
                    + "	p.mcd01_descricao_curta as descricaoreduzida,	\n"
                    + "	p.mcd01_flgativa as situacaocadastro,\n"
                    + "	p.mcd01_flgsazonal as sazonal,\n"
                    + "	pl.mcd03_flgativa_compra as descontinuado,\n"
                    + "	pl.mcd03_flgativa_venda as vendapdv,\n"
                    + "	p.mcd01_data_inclusao as datacadastro,\n"
                    + "	p.mcd01_flgativa as situacaocadastro,\n"
                    + "	p.mcd01_dthr_ultima_alteracao as dataalteracao,\n"
                    + "	pl.mcd03_qtdestoque_maximo as estoquemaximo,\n"
                    + "	est.est06_qtd as estoque,\n"
                    + "	est.est06_qtdtroca as estoquetroca,\n"
                    + "	p.mcd01_perc_margem_ideal as margem,\n"
                    + "	pl.mcd03_preco_venda as precovenda,\n"
                    + "	est.est06_vlrunit_ultnf as custo,\n"
                    + "	p.fis11_ncm_id as ncm_id,\n"
                    + "	ncm.fis11_codigo as ncm,\n"
                    + "	cest.fis15_codigo as cest,\n"
                    + "	cstpiss.fis08_codigo as piscofins_saida,\n"
                    + "	cstpise.fis08_codigo as piscofins_entrada,\n"
                    + "	pis.fis20_codigo_natureza_receita as naturezareceita,\n"
                    + "	pl.mcd03_perc_aliquota_efetiva_fcx,\n"
                    + "	pl.mcd03_perc_aliquota_normal_st,\n"
                    + "	pl.mcd03_perc_aliquota_original,\n"
                    + "	pl.mcd03_aliq_fcp as fcp,\n"
                    + "	pl.mcd03_cbenef as codigobeneficio,\n"
                    + "	cst.fis10_codigo as csticms,\n"
                    + "	coalesce(fa.fse02_perc_aliquota, 0) as perc_aliquota,\n"
                    + "	coalesce(frb.fse03_perc_aliquota_aplicavel, 0) as perc_aliquota_aplicavel,\n"
                    + "	coalesce(frb.fse03_perc_aliquota_efetiva, 0) as perc_aliquota_efetiva,\n"
                    + "	coalesce(frb.fse03_perc_reducao, 0) as perc_reducao,\n"
                    + "	coalesce(iva.fse11_mva_interna, 0) as mva_interna,\n"
                    + "	coalesce(iva.fse11_mva_interestadual, 0) as mva_interestadual,\n"
                    + "	coalesce(iva.fse11_mva_importados, 0) as mva_importados\n"
                    + "from mcd01_mercadoria p\n"
                    + "left join adm01_unidade_mercadoria un\n"
                    + "	on un.adm01_id = p.adm01_unidade_mercadoria_id \n"
                    + "left join mcd02_codigo_mercadoria ean\n"
                    + "	on ean.mcd01_mercadoria_id = p.mcd01_id\n"
                    + "left join fis11_ncm ncm\n"
                    + "	on ncm.fis11_id = p.fis11_ncm_id\n"
                    + "left join fis15_cest cest\n"
                    + "	on cest.fis15_id = p.fis15_cest_id \n"
                    + "left join mcd03_mercadoria_filial pl\n"
                    + "	on pl.mcd01_mercadoria_id = p.mcd01_id \n"
                    + "	and pl.cfg06_filial_id = " + getLojaOrigem() + "\n"
                    + "left join est06_estoque_atual est\n"
                    + "	on est.mcd03_mercadoria_filial_id = pl.mcd03_id\n"
                    + "	and est.dom18_finalidadenf_id = 1\n"
                    + "left join fis20_regras_pis_cofins pis\n"
                    + "	on pis.fis11_ncm_id = ncm.fis11_id\n"
                    + "	and fis20_id = p.fis20_regras_pis_cofins_id \n"
                    + "left join fis08_cstpiscofins cstpise\n"
                    + "	on cstpise.fis08_codigo = pis.fis08_cstpiscofins_entrada_id \n"
                    + "	and cstpise.fis08_flgentrada = 1\n"
                    + "left join fis08_cstpiscofins cstpiss\n"
                    + "	on cstpiss.fis08_codigo = pis.fis08_cstpiscofins_saida_id \n"
                    + "	and cstpiss.fis08_flgentrada = 0\n"
                    + "left join fis10_csticms cst\n"
                    + "	on cst.fis10_id = pl.fis10_csticms_id_fcx\n"
                    + "left join fse11_valorbcst_ncm iva\n"
                    + "	on iva.fis11_ncm_id = ncm.fis11_id \n"
                    + "left join fse06_aliquota_ncm aliq\n"
                    + "	on aliq.fis11_ncm_id = ncm.fis11_id\n"
                    + "left join fse02_aliquota fa \n"
                    + "	on fa.fse02_id = aliq.fse02_aliquota_id\n"
                    + "left join fse09_reducaobc_ncm frn \n"
                    + "	on frn.fis11_ncm_id = ncm.fis11_id \n"
                    + "left join fse03_reducao_bc frb \n"
                    + "	on frb.fse03_id = frn.fse03_reducao_bc_id\n"
                    + "where p.mcd01_flgbalanca = 1\n"
                    + "and ean.mcd02_codigo is not null \n"
                    + "and ean.mcd02_codigo != '' \n"
                    //+ "and p.mcd01_descricao like '%MORCELA OURO%' \n"        
                    + "order by 1"
            )) {
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().carregarProdutosBalanca();
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    ProdutoBalancaVO produtoBalanca;
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("mcd01_codint"));
                    
                    String ean = rst.getString("codigobarras");
                    
                    //imp.setEan(rst.getString("codigobarras"));
                    
                    if ((ean != null)
                            && (!ean.trim().isEmpty())) {

                        if (ean.startsWith("20") && ean.trim().length() == 13) {
                            
                            long codigoProduto;
                            String codigobalanca = ean.substring(0, 5);
                            codigoProduto = Long.parseLong(codigobalanca.substring(2, 5));
                            
                            if (codigoProduto <= Integer.MAX_VALUE) {
                                produtoBalanca = produtosBalanca.get((int) codigoProduto);
                            } else {
                                produtoBalanca = null;
                            }
                            
                            if (produtoBalanca != null) {
                                imp.setEan(String.valueOf(codigoProduto));
                                imp.seteBalanca(true);
                                imp.setValidade(produtoBalanca.getValidade() > 1 ? produtoBalanca.getValidade() : 0);
                            } else {
                                imp.setEan(ean);
                                imp.setValidade(rst.getInt("validade"));
                                imp.seteBalanca(false);
                            }                            
                        } else {
                           imp.setEan(ean);
                           imp.seteBalanca(false);
                           imp.setValidade(rst.getInt("validade"));
                        }
                    } else {
                        imp.seteBalanca(false);
                        imp.setValidade(rst.getInt("validade"));
                    }

                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                    imp.setDescontinuado(rst.getInt("descontinuado") == 1);
                    imp.setVendaPdv(rst.getInt("vendapdv") == 1);
                    imp.setSituacaoCadastro(rst.getInt("situacaocadastro"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setDataAlteracao(rst.getDate("dataalteracao"));
                    imp.setEstoqueMaximo(rst.getDouble("estoquemaximo"));
                    imp.setTroca(rst.getDouble("estoquetroca"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setCustoComImposto(rst.getDouble("custo"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getString("piscofins_saida"));
                    imp.setPiscofinsCstCredito(rst.getString("piscofins_entrada"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("naturezareceita"));
                    imp.setBeneficio(rst.getString("codigobeneficio"));
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
                    "select \n"
                    + "	p.mcd01_id,\n"
                    + "	p.mcd01_codint,\n"
                    + "	ean.mcd02_codigo as codigobarras,\n"
                    + "	ean.mcd02_multiplicador as qtdembalagem,\n"
                    + "	un.adm01_unidade as tipoembalagem,\n"
                    + "	p.mcd01_flgbalanca as balanca,\n"
                    + "	p.mcd01_validade as validade,\n"
                    + "	p.mcd01_descricao as descricaocompleta,\n"
                    + "	p.mcd01_descricao_curta as descricaoreduzida,	\n"
                    + "	p.mcd01_flgativa as situacaocadastro,\n"
                    + "	p.mcd01_flgsazonal as sazonal,\n"
                    + "	pl.mcd03_flgativa_compra as descontinuado,\n"
                    + "	pl.mcd03_flgativa_venda as vendapdv,\n"
                    + "	p.mcd01_data_inclusao as datacadastro,\n"
                    + "	p.mcd01_flgativa as situacaocadastro,\n"
                    + "	p.mcd01_dthr_ultima_alteracao as dataalteracao,\n"
                    + "	pl.mcd03_qtdestoque_maximo as estoquemaximo,\n"
                    + "	est.est06_qtd as estoque,\n"
                    + "	est.est06_qtdtroca as estoquetroca,\n"
                    + "	p.mcd01_perc_margem_ideal as margem,\n"
                    + "	pl.mcd03_preco_venda as precovenda,\n"
                    + "	est.est06_vlrunit_ultnf as custo,\n"
                    + "	p.fis11_ncm_id as ncm_id,\n"
                    + "	ncm.fis11_codigo as ncm,\n"
                    + "	cest.fis15_codigo as cest,\n"
                    + "	cstpiss.fis08_codigo as piscofins_saida,\n"
                    + "	cstpise.fis08_codigo as piscofins_entrada,\n"
                    + "	pis.fis20_codigo_natureza_receita as naturezareceita,\n"
                    + "	pl.mcd03_perc_aliquota_efetiva_fcx,\n"
                    + "	pl.mcd03_perc_aliquota_normal_st,\n"
                    + "	pl.mcd03_perc_aliquota_original,\n"
                    + "	pl.mcd03_aliq_fcp as fcp,\n"
                    + "	pl.mcd03_cbenef as codigobeneficio,\n"
                    + "	cst.fis10_codigo as csticms,\n"
                    + "	coalesce(fa.fse02_perc_aliquota, 0) as perc_aliquota,\n"
                    + "	coalesce(frb.fse03_perc_aliquota_aplicavel, 0) as perc_aliquota_aplicavel,\n"
                    + "	coalesce(frb.fse03_perc_aliquota_efetiva, 0) as perc_aliquota_efetiva,\n"
                    + "	coalesce(frb.fse03_perc_reducao, 0) as perc_reducao,\n"
                    + "	coalesce(iva.fse11_mva_interna, 0) as mva_interna,\n"
                    + "	coalesce(iva.fse11_mva_interestadual, 0) as mva_interestadual,\n"
                    + "	coalesce(iva.fse11_mva_importados, 0) as mva_importados\n"
                    + "from mcd01_mercadoria p\n"
                    + "left join adm01_unidade_mercadoria un\n"
                    + "	on un.adm01_id = p.adm01_unidade_mercadoria_id \n"
                    + "left join mcd02_codigo_mercadoria ean\n"
                    + "	on ean.mcd01_mercadoria_id = p.mcd01_id\n"
                    + "left join fis11_ncm ncm\n"
                    + "	on ncm.fis11_id = p.fis11_ncm_id\n"
                    + "left join fis15_cest cest\n"
                    + "	on cest.fis15_id = p.fis15_cest_id \n"
                    + "left join mcd03_mercadoria_filial pl\n"
                    + "	on pl.mcd01_mercadoria_id = p.mcd01_id \n"
                    + "	and pl.cfg06_filial_id = " + getLojaOrigem() + "\n"
                    + "left join est06_estoque_atual est\n"
                    + "	on est.mcd03_mercadoria_filial_id = pl.mcd03_id\n"
                    + "	and est.dom18_finalidadenf_id = 1\n"
                    + "left join fis20_regras_pis_cofins pis\n"
                    + "	on pis.fis11_ncm_id = ncm.fis11_id\n"
                    + "	and fis20_id = p.fis20_regras_pis_cofins_id \n"
                    + "left join fis08_cstpiscofins cstpise\n"
                    + "	on cstpise.fis08_codigo = pis.fis08_cstpiscofins_entrada_id \n"
                    + "	and cstpise.fis08_flgentrada = 1\n"
                    + "left join fis08_cstpiscofins cstpiss\n"
                    + "	on cstpiss.fis08_codigo = pis.fis08_cstpiscofins_saida_id \n"
                    + "	and cstpiss.fis08_flgentrada = 0\n"
                    + "left join fis10_csticms cst\n"
                    + "	on cst.fis10_id = pl.fis10_csticms_id_fcx\n"
                    + "left join fse11_valorbcst_ncm iva\n"
                    + "	on iva.fis11_ncm_id = ncm.fis11_id \n"
                    + "left join fse06_aliquota_ncm aliq\n"
                    + "	on aliq.fis11_ncm_id = ncm.fis11_id\n"
                    + "left join fse02_aliquota fa \n"
                    + "	on fa.fse02_id = aliq.fse02_aliquota_id\n"
                    + "left join fse09_reducaobc_ncm frn \n"
                    + "	on frn.fis11_ncm_id = ncm.fis11_id \n"
                    + "left join fse03_reducao_bc frb \n"
                    + "	on frb.fse03_id = frn.fse03_reducao_bc_id\n"
                    //+ "where p.mcd01_descricao like '%NABO KG%'\n"        
                    + "order by 1"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    ProdutoBalancaVO produtoBalanca;
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("mcd01_codint"));
                    imp.setEan(rst.getString("codigobarras"));
                    imp.seteBalanca(rst.getInt("balanca") == 1);
                    imp.setValidade(rst.getInt("validade"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                    imp.setDescontinuado(rst.getInt("descontinuado") == 1);
                    imp.setVendaPdv(rst.getInt("vendapdv") == 1);
                    imp.setSituacaoCadastro(rst.getInt("situacaocadastro"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setDataAlteracao(rst.getDate("dataalteracao"));
                    imp.setEstoqueMaximo(rst.getDouble("estoquemaximo"));
                    imp.setTroca(rst.getDouble("estoquetroca"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setCustoComImposto(rst.getDouble("custo"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getString("piscofins_saida"));
                    imp.setPiscofinsCstCredito(rst.getString("piscofins_entrada"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("naturezareceita"));
                    imp.setBeneficio(rst.getString("codigobeneficio"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos(OpcaoProduto opt) throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        if (opt == OpcaoProduto.ICMS) {
            try (Statement stm = Conexao.createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select\n"
                        + "	codigointerno,\n"
                        + "	cst_icms,\n"
                        + "	aliq_icms,\n"
                        + "	red_icms,\n"
                        + "	fcp\n"
                        + "from implantacao.tributacao_produtos_plenokw\n"
                        + "order by 1"
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("codigointerno"));

                        String idIcms = getAliquotaKey(
                                rst.getString("cst_icms"),
                                rst.getDouble("aliq_icms"),
                                rst.getDouble("red_icms"),
                                rst.getDouble("fcp")
                        );

                        imp.setIcmsDebitoId(idIcms);
                        imp.setIcmsDebitoForaEstadoId(idIcms);
                        imp.setIcmsDebitoForaEstadoNfId(idIcms);
                        imp.setIcmsCreditoId(idIcms);
                        imp.setIcmsCreditoForaEstadoId(idIcms);
                        imp.setIcmsConsumidorId(idIcms);

                        result.add(imp);
                    }
                }
            }
            return result;
        }
        return null;
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	p.mcd01_id,\n"
                    + "	p.mcd01_codint,\n"
                    + "	ean.mcd02_codigo as codigobarras,\n"
                    + "	ean.mcd02_multiplicador as qtdembalagem,\n"
                    + "	un.adm01_unidade as tipoembalagem\n"
                    + "from mcd01_mercadoria p\n"
                    + "left join adm01_unidade_mercadoria un\n"
                    + "	on un.adm01_id = p.adm01_unidade_mercadoria_id \n"
                    + "left join mcd02_codigo_mercadoria ean\n"
                    + "	on ean.mcd01_mercadoria_id = p.mcd01_id\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("mcd01_codint"));
                    imp.setEan(rst.getString("codigobarras"));
                    imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
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
                    "select	\n"
                    + "	f.com01_id as id,\n"
                    + "	pes.pes01_pessoa_fisica_id as pessoafisica,\n"
                    + "	pj.pes02_id as pessoajuridica, \n"
                    + "	pj.pes02_razao_social as razao,\n"
                    + "	pe.pes03_nome_fantasia as fantasia,\n"
                    + "	pf.pes01_nome as nomepessoa,\n"
                    + "	pf.pes01_cpf as cpf,\n"
                    + "	pe.pes03_cnpj as cnpj,\n"
                    + "	pes.pes04_inscricao_estadual as ie,\n"
                    + "	pe.pes03_inscricao_estadual,\n"
                    + "	pe.pes03_inscricao_municipal,\n"
                    + "	concat(upper(pes.pes04_tipo_logradouro), ' ', upper(pes.pes04_nome_logradouro))  as endereco,\n"
                    + "	pes.pes04_endereco_numero as numero,\n"
                    + "	pes.pes04_endereco_complemento as complemento,\n"
                    + "	pes.pes04_bairro as bairro,\n"
                    + "	mun.pes08_cidade as municipio,\n"
                    + "	mun.pes08_codigo_ibge_municipio as municipioibge,\n"
                    + "	uf.dom16_sigla as uf,\n"
                    + "	pes.pes04_cep as cep,\n"
                    + "	pes.pes04_fone as telefone,\n"
                    + "	pes.pes04_fax as fax,\n"
                    + "	pes.pes04_email as email,\n"
                    + "	pes.pes04_dthr_cadastro as datacadastro,\n"
                    + "	f.com01_prazo_pagto,\n"
                    + "	f.com01_prazo_entrega_pedido,\n"
                    + "	f.com01_flgativo as situacaocadastro,\n"
                    + "	f.com01_vlrminimo_pedido as pedidominimo,\n"
                    + "	f.com01_fone_comercial,\n"
                    + "	f.com01_fone_financeiro,\n"
                    + "	f.com01_fone_vendedor,\n"
                    + "	f.com01_email_financeiro,\n"
                    + "	f.com01_email_comercial,\n"
                    + "	f.com01_email_vendedor,\n"
                    + "	f.com01_dtcadastro as datacadastro,\n"
                    + "	pes.pes04_flgprodutor_rural as e_produtorural,\n"
                    + "	pes.pes04_flgtransportador as e_transportador\n"
                    + "from com01_fornecedor f\n"
                    + "join pes04_pessoa pes\n"
                    + "	on pes.pes04_id = f.pes04_pessoa_id \n"
                    + "left join dom16_uf uf\n"
                    + "	on uf.dom16_id = pes.dom16_uf_id\n"
                    + "left join pes08_ibge_municipio mun\n"
                    + "	on mun.pes08_id = pes.pes08_ibge_municipio_id\n"
                    + "left join pes03_estabelecimento pe \n"
                    + "	on pe.pes03_id = pes.pes03_estabelecimento_id\n"
                    + "left join pes02_pessoa_juridica pj 	\n"
                    + "	on pj.pes02_id = pe.pes02_pessoa_juridica_id\n"
                    + "left join pes01_pessoa_fisica pf 	\n"
                    + "	on pf.pes01_id = pes.pes01_pessoa_fisica_id\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));

                    if ((rst.getString("pessoafisica") != null)
                            && (!rst.getString("pessoafisica").trim().isEmpty())) {
                        imp.setRazao(rst.getString("nomepessoa"));
                        imp.setFantasia(imp.getRazao());
                        imp.setCnpj_cpf(rst.getString("cpf"));
                    } else {
                        imp.setRazao(rst.getString("razao"));
                        imp.setFantasia(rst.getString("fantasia"));
                        imp.setCnpj_cpf(rst.getString("cnpj"));
                    }

                    imp.setIe_rg(rst.getString("ie"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setIbge_municipio(rst.getInt("municipioibge"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setDatacadastro(rst.getDate("datacadastro"));
                    imp.setAtivo(rst.getInt("situacaocadastro") == 1);
                    imp.setPrazoEntrega(rst.getInt("com01_prazo_entrega_pedido"));
                    imp.setTel_principal(rst.getString("telefone"));
                    imp.setValor_minimo_pedido(rst.getDouble("pedidominimo"));

                    if ((rst.getString("fax") != null)
                            && (!rst.getString("fax").trim().isEmpty())) {
                        imp.addTelefone("FAX", rst.getString("fax"));
                    }

                    if ((rst.getString("com01_fone_comercial") != null)
                            && (!rst.getString("com01_fone_comercial").trim().isEmpty())) {
                        imp.addTelefone("FONE COMERCIAL", rst.getString("com01_fone_comercial"));
                    }

                    if ((rst.getString("com01_fone_financeiro") != null)
                            && (!rst.getString("com01_fone_financeiro").trim().isEmpty())) {
                        imp.addTelefone("FONE FINANCEIRO", rst.getString("com01_fone_financeiro"));
                    }

                    if ((rst.getString("com01_fone_vendedor") != null)
                            && (!rst.getString("com01_fone_vendedor").trim().isEmpty())) {
                        imp.addTelefone("FONE VENDEDOR", rst.getString("com01_fone_vendedor"));
                    }

                    if ((rst.getString("email") != null)
                            && (!rst.getString("email").trim().isEmpty())) {
                        imp.addEmail("EMAIL", rst.getString("email").toLowerCase(), TipoContato.NFE);
                    }

                    if ((rst.getString("com01_email_financeiro") != null)
                            && (!rst.getString("com01_email_financeiro").trim().isEmpty())) {
                        imp.addEmail("EMAIL FINANCEIRO", rst.getString("com01_email_financeiro").toLowerCase(), TipoContato.FINANCEIRO);
                    }

                    if ((rst.getString("com01_email_comercial") != null)
                            && (!rst.getString("com01_email_comercial").trim().isEmpty())) {
                        imp.addEmail("EMAIL COMERCIAL", rst.getString("com01_email_comercial").toLowerCase(), TipoContato.COMERCIAL);
                    }

                    if ((rst.getString("com01_email_vendedor") != null)
                            && (!rst.getString("com01_email_vendedor").trim().isEmpty())) {
                        imp.addEmail("EMAIL VENDEDOR", rst.getString("com01_email_vendedor").toLowerCase(), TipoContato.COMERCIAL);
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
                    "select \n"
                    + "	mcd01_mercadoria_id as idproduto,\n"
                    + "	com01_fornecedor_id as idfornecedor,\n"
                    + "	com02_codmerc_fornecedor as codigoexterno,\n"
                    + "	com02_nrounid_embalagem as qtdembalagem,\n"
                    + "	com02_fator_conversao as fatorconversao\n"
                    + "from com02_mercadoria_fornecedor cmf \n"
                    + "order by 2, 1"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rst.getString("idproduto"));
                    imp.setIdFornecedor(rst.getString("idfornecedor"));
                    imp.setCodigoExterno(rst.getString("codigoexterno"));
                    imp.setQtdEmbalagem(rst.getDouble("qtdembalagem"));
                    imp.setFatorEmbalagem(rst.getDouble("fatorconversao"));
                    result.add(imp);
                }
            }
        }
        return result;
    }
}
