package vrimplantacao2_5.dao.sistema;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ConveniadoIMP;
import vrimplantacao2.vo.importacao.ConvenioEmpresaIMP;
import vrimplantacao2.vo.importacao.ConvenioTransacaoIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.OfertaIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2_5.dao.conexao.ConexaoFirebird;

/**
 *
 * @author Bruno
 */
public class Fenix2_5DAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "Fenix";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.DATA_CADASTRO,
                OpcaoProduto.QTD_EMBALAGEM_COTACAO,
                OpcaoProduto.QTD_EMBALAGEM_EAN,
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.TIPO_PRODUTO,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.VALIDADE,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.ATIVO,
                OpcaoProduto.PESO_BRUTO,
                OpcaoProduto.PESO_LIQUIDO,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.ESTOQUE_MINIMO,
                OpcaoProduto.ESTOQUE_MAXIMO,
                OpcaoProduto.TROCA,
                OpcaoProduto.MARGEM,
                OpcaoProduto.PRECO,
                OpcaoProduto.CUSTO,
                OpcaoProduto.CUSTO_COM_IMPOSTO,
                OpcaoProduto.CUSTO_SEM_IMPOSTO,
                OpcaoProduto.NCM,
                OpcaoProduto.CEST,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.NATUREZA_RECEITA,
                OpcaoProduto.ICMS,
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.ATUALIZAR_SOMAR_ESTOQUE,
                OpcaoProduto.OFERTA,
                OpcaoProduto.DESCONTINUADO,
                OpcaoProduto.VOLUME_QTD,
                OpcaoProduto.VOLUME_TIPO_EMBALAGEM,
                OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                OpcaoProduto.FABRICANTE,
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.FAMILIA,
                OpcaoProduto.FAMILIA_PRODUTO,
                OpcaoProduto.NUTRICIONAL,
                OpcaoProduto.ASSOCIADO,
                OpcaoProduto.VENDA_PDV,
                OpcaoProduto.PDV_VENDA,
                OpcaoProduto.MANTER_CODIGO_MERCADOLOGICO,
                OpcaoProduto.RECEITA
        ));
    }

    @Override
    public Set<OpcaoFornecedor> getOpcoesDisponiveisFornecedor() {
        return new HashSet<>(Arrays.asList(
                OpcaoFornecedor.ENDERECO,
                OpcaoFornecedor.DADOS,
                OpcaoFornecedor.CONTATOS,
                OpcaoFornecedor.SITUACAO_CADASTRO,
                OpcaoFornecedor.TIPO_EMPRESA,
                OpcaoFornecedor.PAGAR_FORNECEDOR,
                OpcaoFornecedor.PRODUTO_FORNECEDOR
        ));
    }

    @Override
    public Set<OpcaoCliente> getOpcoesDisponiveisCliente() {
        return new HashSet<>(Arrays.asList(
                OpcaoCliente.DADOS,
                OpcaoCliente.ENDERECO,
                OpcaoCliente.CONTATOS,
                OpcaoCliente.DATA_CADASTRO,
                OpcaoCliente.DATA_NASCIMENTO,
                OpcaoCliente.VENCIMENTO_ROTATIVO,
                OpcaoCliente.RECEBER_CREDITOROTATIVO,
                OpcaoCliente.CONVENIO_EMPRESA,
                OpcaoCliente.CONVENIO_CONVENIADO,
                OpcaoCliente.CONVENIO_TRANSACAO,
                OpcaoCliente.RECEBER_CHEQUE,
                OpcaoCliente.CLIENTE_EVENTUAL
        ));
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    /*"SELECT\n"
                    + "	DISTINCT  replace(COALESCE (CST_PIS_SAI_PRO || CST_IPI_ENT_PRO || ALIQUOTA_PRO || ALIQUOTA_REDUZ_PRO,0),',','.')  AS id,\n"
                    + "	COALESCE (p.cd_cst_sai_pro,	0) AS descricao,\n"
                    + "	COALESCE (p.cd_cst_sai_pro,	0) AS cst,\n"
                    + "	replace(COALESCE (p.vl_aliquota_sai_pro,0), ',','.') AS aliquota,\n"
                    + "	replace(COALESCE (p.aliquota_reduz_pro,	0),',','.') AS reducao\n"
                    + "FROM\n"
                    + "	PRODUTO p\n"
                    + "	ORDER BY 1"*/
                    "SELECT\n"
                    + "	DISTINCT  replace(COALESCE (p.cd_cst_sai_pro,0)||COALESCE (p.vl_aliquota_sai_pro,0)||COALESCE (p.aliquota_reduz_pro,0),',','.')  AS id,\n"
                    + "	COALESCE (p.cd_cst_sai_pro,	0) AS descricao,\n"
                    + "	COALESCE (p.cd_cst_sai_pro,	0) AS cst,\n"
                    + "	replace(COALESCE (p.vl_aliquota_sai_pro,0), ',','.') AS aliquota,\n"
                    + "	replace(COALESCE (p.aliquota_reduz_pro,	0),',','.') AS reducao\n"
                    + "FROM\n"
                    + "	PRODUTO p\n"
                    + "UNION\n"
                    + "SELECT\n"
                    + "	DISTINCT  \n"
                    + "	'-E'||replace(p.CD_CST_PRO||COALESCE(p.ALIQUOTA_PRO,0)||COALESCE(p.ALIQUOTA_REDUZ_PRO,0),',','.') AS id,\n"
                    + "	COALESCE (p.CD_CST_PRO,	0) AS descricao,\n"
                    + "	COALESCE (p.CD_CST_PRO,	0) AS cst,\n"
                    + "	replace(COALESCE (p.ALIQUOTA_PRO,0), ',','.') AS aliquota,\n"
                    + "	replace(COALESCE (p.ALIQUOTA_REDUZ_PRO,	0),',','.') AS reducao\n"
                    + "FROM\n"
                    + "	PRODUTO p"
            )) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(
                            rs.getString("id"),
                            rs.getString("descricao"),
                            rs.getInt("cst"),
                            rs.getDouble("aliquota"),
                            rs.getDouble("reducao"))
                    );
                }
            }
        }

        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT DISTINCT \n"
                    + "CDFAM_PRO AS id_merc1,\n"
                    + "f.DS_FAM AS descricao1,\n"
                    + "s.ID_SFAM AS id_merc2,\n"
                    + "s.DS_SFAM AS descricao2\n"
                    + "FROM produto p\n"
                    + "JOIN FAMILIA f ON f.id_fam = p.CDFAM_PRO \n"
                    + "JOIN SUBFAMILIA s ON s.ID_SFAM = p.CDSFAM_PRO ")) {
                while (rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setMerc1ID(rs.getString("id_merc1"));
                    imp.setMerc1Descricao(rs.getString("descricao1"));
                    imp.setMerc2ID(rs.getString("id_merc2"));
                    imp.setMerc2Descricao(rs.getString("descricao2"));
                    imp.setMerc3ID("1");
                    imp.setMerc3Descricao("1");

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<OfertaIMP> getOfertas(Date dataTermino) throws Exception {
        List<OfertaIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	RIGHT (CD_PRO,9)AS id_produto,\n"
                    + "	DT_INI_PROMOCAO_PRO AS dataInicio,\n"
                    + "	DT_FIM_PROMOCAO_PRO AS dataFim,\n"
                    + "	VL_VENDA_PRO AS precoNormal,\n"
                    + "	VL_PROMOCAO_PRO AS precoOferta\n"
                    + "FROM\n"
                    + "	PRODUTO p\n"
                    + "WHERE\n"
                    + "	HR_INI_PROMOCAO_PRO IS NOT NULL\n"
                    + "	AND DT_FIM_PROMOCAO_PRO >= CURRENT_DATE"
            )) {
                while (rst.next()) {
                    OfertaIMP imp = new OfertaIMP();
                    imp.setIdProduto(rst.getString("id_produto"));
                    imp.setDataInicio(rst.getDate("dataInicio"));
                    imp.setDataFim(rst.getDate("dataFim"));
                    imp.setPrecoNormal(rst.getDouble("precoNormal"));
                    imp.setPrecoOferta(rst.getDouble("precoOferta"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    /* 
    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	cd_produto_semelhante as id,\n"
                    + "	nm_produto_semelhante as nomeproduto,\n"
                    + "	dt_inc as dtcadastro\n"
                    + "from \n"
                    + "	produto.tb_produto_semelhante \n"
                    + "order by \n"
                    + "	nm_produto_semelhante")) {
                while (rs.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("id"));
                    imp.setDescricao(rs.getString("nomeproduto"));

                    result.add(imp);
                }
            }
        }
        return result;
    }
     */
    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "	CD_PRO AS id_produto,\n"
                    + "	CD_PRO AS ean,\n"
                    + "	1 AS qtd_embalagem,\n"
                    + "	UN_PRO AS tipo_embalagem\n"
                    + "FROM\n"
                    + "	PRODUTO p"
            )) {
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rs.getString("id_produto"));
                    imp.setEan(rs.getString("ean"));
                    imp.setQtdEmbalagem(rs.getInt("qtd_embalagem"));
                    imp.setTipoEmbalagem(rs.getString("tipo_embalagem"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "	RIGHT (CD_PRO,9)AS id,\n"
                    + "	CD_PRO AS ean,\n"
                    + "	UN_PRO AS unidade,\n"
                    + "	FL_BALANCA_PRO AS ebalanca,\n"
                    + "	COALESCE (VAL_BALANCA_PRO,\n"
                    + "	0) AS validade,\n"
                    + "	1 AS embalagem,\n"
                    + "	COALESCE(trim(p.ds_pro),'') || ' ' || COALESCE(trim(p.marca_pro),'') descricaocompleta,\n"
                    + "	COALESCE(trim(p.ds_pro),'') || ' ' || COALESCE(trim(p.un_pro),'') descricaoreduzida,\n"
                    + "	CDFAM_PRO AS mercadologico,\n"
                    + "	1 AS mercadologico1,\n"
                    + "	COALESCE (QT_EST_MIN_PRO,0) AS estoqueminimo,\n"
                    + "	COALESCE (QT_EST_IDEAL_PRO,0) AS estoquemaximo,\n"
                    + "	COALESCE (QT_EST_ATUAL_PRO,0) AS estoque,\n"
                    + "	MARKUP_PRO AS margem,\n"
                    + "	VL_COMPRA_PRO AS custocomimposto,\n"
                    + "	VL_COM_BRUTO_PRO AS custosemimposto,\n"
                    + "	VL_VENDA_PRO AS preco,\n"
                    + "	FL_ATIVO_PRO AS ativo,\n"
                    + "	COALESCE (NCM_PRO,0) AS ncm,\n"
                    + "	COALESCE (CEST_ST_PRO,0)AS cest,\n"
                    + "	CST_PIS_SAI_PRO AS pisconfis_saida,\n"
                    + "	CST_PIS_ENT_PRO AS piscofins_entrada,\n"
                    + "	CST_PIS_ENT_PRO AS piscofins_natrec,\n"
                    + "	NAT_REC_PIS_PRO AS id_fornecedor,\n"
                    + "	replace(COALESCE (p.cd_cst_sai_pro,0)||COALESCE (p.vl_aliquota_sai_pro,0)||COALESCE (p.aliquota_reduz_pro,0),',','.') AS id_icms,\n"
                    + " '-E'||replace(p.CD_CST_PRO||COALESCE(p.ALIQUOTA_PRO,0)||COALESCE(p.ALIQUOTA_REDUZ_PRO,0),',','.') as id_icms_entrada\n"
                    + "FROM\n"
                    + "	PRODUTO p"
            //SELECT * FROM PRODUTO p WHERE RIGHT (CD_PRO,9) IN select para validar quantidades faltantes devido id ser EAN
            )) {
                Map<Integer, vrimplantacao2.vo.cadastro.ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().getProdutosBalanca();
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rs.getString("id"));
                    imp.seteBalanca(rs.getBoolean("ebalanca"));
                    imp.setEan(rs.getString("ean"));

//                  ProdutoBalancaVO bal = produtosBalanca.get(Utils.stringToInt(rs.getString("ean"), -2));
                    imp.setDescricaoCompleta(rs.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rs.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(imp.getDescricaoReduzida());

                    imp.setTipoEmbalagem(rs.getString("unidade"));
                    imp.setQtdEmbalagem(1);

                    imp.setNcm(rs.getString("ncm"));
                    imp.setCest(rs.getString("cest"));
                    imp.setSituacaoCadastro(rs.getInt("ativo"));

                    imp.setEstoqueMinimo(rs.getDouble("estoqueminimo"));
                    imp.setEstoqueMaximo(rs.getDouble("estoquemaximo"));
                    imp.setEstoque(rs.getDouble("estoque"));

                    imp.setCodMercadologico1(rs.getString("mercadologico"));
                    imp.setCodMercadologico2(rs.getString("mercadologico1"));
                    imp.setFornecedorFabricante(rs.getString("id_fornecedor"));

                    imp.setMargem(rs.getDouble("margem"));
                    imp.setCustoComImposto(rs.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(rs.getDouble("custosemimposto"));
                    imp.setPrecovenda(rs.getDouble("preco"));

                    String idIcms;

                    idIcms = rs.getString("id_icms").trim();
                    System.out.println(rs.getString("id") + " icms saida? -> " + rs.getString("id_icms") + " icms entrada? -> " + rs.getString("id_icms_entrada"));
                    imp.setIcmsDebitoId(idIcms);
                    imp.setIcmsDebitoForaEstadoId(idIcms);
                    imp.setIcmsDebitoForaEstadoNfId(idIcms);
                    imp.setIcmsConsumidorId(idIcms);

                    imp.setIcmsCreditoId(rs.getString("id_icms_entrada"));
                    imp.setIcmsCreditoForaEstadoId(imp.getIcmsCreditoId());

                    imp.setPiscofinsCstDebito(rs.getString("pisconfis_saida"));
                    imp.setPiscofinsCstCredito(rs.getString("piscofins_entrada"));
                    imp.setPiscofinsNaturezaReceita(rs.getString("piscofins_natrec"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    /*
    @Override
    public List<AssociadoIMP> getAssociados(Set<OpcaoAssociado> opt) throws Exception {
        List<AssociadoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "    ass.nr_produto idproduto_item,\n"
                    + "    ass.nm_produto as descricaoproduto_principal,\n"
                    + "    ass.qt_embalagem qtdembalagem_item,\n"
                    + "    pro.nr_produto as idproduto_principal,\n"
                    + "    pro.nm_produto as descproduto_item,\n"
                    + "    pro.qt_embalagem as qtdembalagem,\n"
                    + "    '' percentualpreco,\n"
                    + "    '' aplicapreco,\n"
                    + "    '' aplicacusto,\n"
                    + "    '' aplicaestoque,\n"
                    + "    '' percentualcustoestoque\n"
                    + "from \n"
                    + "    produto.tb_produto pro\n"
                    + "join\n"
                    + "    produto.tb_produto ass  on ass.cd_produto = pro.cd_produto_movimento\n"
                    + "left join\n"
                    + "    saldo.vw_saldo_loja_un est\n"
                    + "    on est.nr_produto = pro.nr_produto\n"
                    + "    and est.nr_loja = " + getLojaOrigem() + "\n"
                    + "order by \n"
                    + "    2,5"
            )) {
                while (rst.next()) {
                    AssociadoIMP imp = new AssociadoIMP();
                    imp.setId(rst.getString("idproduto_principal"));
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                    imp.setProdutoAssociadoId(rst.getString("idproduto_item"));
                    imp.setQtdEmbalagemItem(rst.getInt("qtdembalagem_item"));
                    result.add(imp);
                }
            }
        }
        return result;
    }
     */
    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT \n"
                    + "  ID_CLI AS id,\n"
                    + "  NM_CLI AS razao,\n"
                    + "  NM_FANTASIA_CLI AS fantasia,\n"
                    + "  CNPJ_CLI AS cnpj_for,\n"
                    + "  IE_CLI AS ie_for,\n"
                    + "  END_CLI AS endereco,\n"
                    + "  NUMERO_CLI AS numero,\n"
                    + "  BAIRRO_CLI AS bairro,\n"
                    + "  CIDADE_CLI AS cidade,\n"
                    + "  UF_CLI AS uf,\n"
                    + "  CEP_CLI AS cep,\n"
                    + "  FONE1_CLI AS fone1,\n"
                    + "  FONE2_CLI AS fone2,\n"
                    + "  FL_ATIVO_CLI AS ativo,\n"
                    + "  EMAIL_CLI AS email\n"
                    + "  FROM CLIENTE c \n"
                    + "  WHERE TIPO_DADOS_CLI = 'F'"
            )) {
                while (rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rs.getString("id"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setCnpj_cpf(rs.getString("cnpj_for"));
                    imp.setIe_rg(rs.getString("ie_for"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));
                    imp.setTel_principal(Utils.acertarTexto(rs.getString("fone1")));

                    imp.setAtivo(rs.getBoolean("ativo"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    /*
    @Override
    public List<ContaPagarIMP> getContasPagar() throws Exception {
        List<ContaPagarIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	cd_pagar as id, \n"
                    + "	cp.cd_base_fornecedor as id_fornecedor,\n"
                    + "	nr_nota_fiscal as documento, \n"
                    + "	cp.dt_emissao as emissao,\n"
                    + "	cp.dt_inc as entrada, \n"
                    + "	dt_vcto as vencimento,\n"
                    + "	vl_valor as valor,\n"
                    + "	cp.ds_obs as observacao\n"
                    + " from\n"
                    + "	pagar.tb_pagar cp\n"
                    + " where\n"
                    + "	cd_loja = " + getLojaOrigem() + "\n"
                    + "	and cd_titulo_carteira = 7\n"
                    + "	and dt_ultima_baixa is null"
            )) {
                while (rst.next()) {
                    ContaPagarIMP imp = new ContaPagarIMP();

                    imp.setId(rst.getString("id"));
                    imp.setIdFornecedor(rst.getString("id_fornecedor"));
                    imp.setNumeroDocumento(rst.getString("documento"));
                    imp.setDataEmissao(rst.getDate("emissao"));
                    imp.setDataEntrada(rst.getDate("entrada"));
                    imp.addVencimento(rst.getDate("vencimento"), rst.getDouble("valor"), rst.getString("observacao"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ChequeIMP> getCheques() throws Exception {
        List<ChequeIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	cd_cheque_item as id, \n"
                    + "	ch.dt_inc as emissao,\n"
                    + "	dt_vcto as vencimento,\n"
                    + "	nr_cheque as numcheque,\n"
                    + "	vl_valor as valor,\n"
                    + "	doc.nr_cpf_cnpj cpfcnpj,\n"
                    + "	z.nr_banco as banco,\n"
                    + "	z.nr_ag as  agencia ,\n"
                    + "	z.nr_conta as conta,\n"
                    + "	tdoc.nr_telefone as telefone,\n"
                    + "	tdoc.nm_titular as titular,\n"
                    + "	ch.ds_obs obs\n"
                    + "from\n"
                    + "	predatado.tb_cheque_item ch \n"
                    + "	left join predatado.tb_cheque t on t.cd_cheque = ch.cd_cheque 	\n"
                    + "	left join predatado.tb_cheque_cmc7_cpf_cnpj doc on doc.cd_cheque_cmc7_cpf_cnpj = ch.cd_cheque_cmc7_cpf_cnpj \n"
                    + "	left join predatado.tb_cheque_cpf_cnpj tdoc on tdoc.nr_cpf_cnpj = doc.nr_cpf_cnpj \n"
                    + "	left join predatado.tb_cheque_cmc7 z on z.cd_cheque_cmc7 = doc.cd_cheque_cmc7\n"
                    + "	where  tp_baixa is null and t.cd_loja = " + getLojaOrigem()
            )) {
                while (rst.next()) {
                    ChequeIMP imp = new ChequeIMP();

                    imp.setId(rst.getString("id"));
                    imp.setDate(rst.getDate("emissao"));
                    imp.setDataDeposito(rst.getDate("vencimento"));
                    imp.setNumeroCheque(rst.getString("numcheque"));
                    imp.setNome(rst.getString("titular"));
                    imp.setTelefone(rst.getString("telefone"));
                    imp.setBanco(rst.getInt("banco"));
                    imp.setAgencia(rst.getString("agencia"));
                    imp.setConta(rst.getString("conta"));
                    imp.setCpf(rst.getString("cpfcnpj"));
                    imp.setObservacao(rst.getString("obs"));
                    imp.setValor(rst.getDouble("valor"));

                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }


     */
    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "    pf.cdfor_pfor id_fornecedor,\n"
                    + "    RIGHT (p.CD_PRO,9)AS id_produto,\n"
                    + "    pf.cdprofor_pfor codigoexterno,\n"
                    + "    1 AS qtd_embalagem\n"
                    + "from\n"
                    + "    produto_fornecedor pf\n"
                    + "    JOIN PRODUTO p ON p.CD_PRO = pf.CDPRO_PRODUTO_PFOR \n"
                    + "order by\n"
                    + "    1,2"
            )) {
                while (rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setIdProduto(rs.getString("id_produto"));
                    imp.setIdFornecedor(rs.getString("id_fornecedor"));
                    imp.setCodigoExterno(rs.getString("codigoexterno"));
                    imp.setQtdEmbalagem(rs.getDouble("qtd_embalagem"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    /*
    @Override
    public List<ReceitaIMP> getReceitas() throws Exception {
        List<ReceitaIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "with pai as \n"
                    + "(\n"
                    + "select\n"
                    + "	distinct \n"
                    + "	rec.cd_produto_receita as id_receita,\n"
                    + "	prod.nr_produto as id_produtopai,\n"
                    + "	prod.nm_produto as descricao\n"
                    + "from\n"
                    + "	produto.tb_produto_receita rec\n"
                    + "join produto.tb_produto_receita_item ri on\n"
                    + "	rec.cd_produto_receita = ri.cd_produto_receita\n"
                    + "join produto.tb_produto prod on\n"
                    + "	prod.cd_produto = rec.cd_produto\n"
                    + "where rec.cd_produto != 158396\n"
                    + "order by 1,2)\n"
                    + "     select \n"
                    + "         ri.cd_produto_receita id_receita,\n"
                    + "		pai.id_produtopai,\n"
                    + "		pai.descricao descricao,\n"
                    + "		p.nr_produto id_produtofilho,\n"
                    + "		p.nm_produto desc_filho,\n"
                    + "		case when r.qt_rendimento_peso = 0 then r.qt_rendimento_unidade else r.qt_rendimento_peso end as rendimento_peso,\n"
                    + "		ri.qt_utilizado as qtde\n"
                    + "from\n"
                    + "	produto.tb_produto_receita_item ri\n"
                    + "join produto.tb_produto p on\n"
                    + "	p.cd_produto = ri.cd_produto\n"
                    + "join pai on\n"
                    + "	pai.id_receita = ri.cd_produto_receita \n"
                    + "join produto.tb_produto_receita r on r.cd_produto_receita = ri.cd_produto_receita "
            //                   + "where pai.id_receita = 4109"
            )) {
                while (rst.next()) {
                    ReceitaIMP imp = new ReceitaIMP();
                    imp.setImportsistema(getSistema());
                    imp.setImportloja(getLojaOrigem());

                    imp.setImportid(rst.getString("id_receita"));
                    imp.setIdproduto(rst.getString("id_produtopai"));
                    imp.setDescricao(rst.getString("descricao"));
                    imp.setRendimento(rst.getDouble("rendimento_peso"));
                    imp.setQtdembalagemreceita(rst.getInt("qtde"));
                    imp.setQtdembalagemproduto(1000);
                    imp.setFator(1);
                    imp.setFichatecnica("");
                    imp.getProdutos().add(rst.getString("id_produtofilho"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

     */
    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "	ID_CLI AS id_cliente,\n"
                    + "	CNPJ_CLI AS cnpj,\n"
                    + "	IE_CLI AS ie,\n"
                    + "	NM_CLI AS razao,\n"
                    + "	COALESCE (NM_FANTASIA_CLI,NM_CLI)AS fantasia,\n"
                    + "	END_CLI AS endereco,\n"
                    + "	NUMERO_CLI AS numero,\n"
                    + "	BAIRRO_CLI AS bairro,\n"
                    + "	CIDADE_CLI AS cidade,\n"
                    + " UF_CLI AS estado,\n"
                    + "	CEP_CLI AS cep,\n"
                    + "	FONE1_CLI AS contato1,\n"
                    + "	FONE2_CLI AS contato2,\n"
                    + "	EMAIL_CLI AS email,\n"
                    + "	DT_NASC_CLI AS data_nasc,\n"
                    + "	CASE WHEN FL_ATIVO_CLI = 1 THEN 1 ELSE 0 END ativo,\n"
                    + "	VL_LIMITE AS limite\n"
                    + "FROM\n"
                    + "	CLIENTE c\n"
                    + "	WHERE TIPO_DADOS_CLI = 	'C'"
            )) {
                while (rs.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rs.getString("id_cliente"));
                    imp.setCnpj(rs.getString("cnpj"));
                    imp.setInscricaoestadual(rs.getString("ie"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));

                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("estado"));
                    imp.setCep(rs.getString("cep"));

                    imp.setTelefone(rs.getString("contato1"));
                    imp.setCelular(rs.getString("contato2"));
                    imp.setEmail(rs.getString("email"));

//                    imp.setDataNascimento(rs.getDate("data_nasc"));
//                    imp.setDataCadastro(rs.getDate("data_cadastro"));
                    imp.setAtivo(rs.getBoolean("ativo"));
                    imp.setValorLimite(rs.getDouble("limite"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT  \n"
                    + " c.ID_CONVENIO as id ,\n"
                    + " c.CD_CLIENTE as id_cliente,\n"
                    + " c.VL_CONVENIO,\n"
                    + " c.ST_CONVENIO,\n"
                    + " c.DH_INCLUSAO,\n"
                    + " p.DT_INICIO,\n"
                    + " p.DH_VENCIMENTO,\n"
                    + " p.VL_ORIGINAL_SEM_JUROS \n"
                    + "FROM CONVENIO c\n"
                    + "LEFT JOIN PAGAMENTO p ON p.ID_PAGAMENTO = c.ID_PAGAMENTO \n"
                    + "LEFT JOIN CONVENIO_PAGTO cp ON cp.ID_CONVENIO = c.ID_CONVENIO\n"
                    + "WHERE c.CD_CLIENTE = 34\n"
                    + "AND c.ST_CONVENIO = 'R' \n"
                    + "AND cp.ID_CONVENIO IS NULL"
            )) {
                while (rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rs.getString("id"));
                    imp.setIdCliente(rs.getString("id_cliente"));
                    imp.setCnpjCliente(rs.getString("cnpj"));
                    imp.setNumeroCupom(rs.getString("coo"));
                    imp.setDataEmissao(rs.getDate("dt_emissao"));
                    imp.setDataVencimento(rs.getDate("dt_vcto"));
                    imp.setEcf(rs.getString("ecf"));
                    imp.setObservacao(rs.getString("ds_obs"));
                    imp.setValor(rs.getDouble("vl_valor"));

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
                    "SELECT\n"
                    + "	ID_EMPRESA AS id,\n"
                    + "	CNPJ AS cnpj,\n"
                    + "	ie AS ie,\n"
                    + "	NM_CONTRIBUINTE AS razao,\n"
                    + "	LOGRADOURO AS endereco,\n"
                    + "	NUMERO AS numero,\n"
                    + "	COMPLEMENTO AS complemento,\n"
                    + "	BAIRRO AS bairro,\n"
                    + "	MUNICIPIO AS cidade,\n"
                    + "	uf,\n"
                    + "	CEP AS cep,\n"
                    + "	TELEFONE 	\n"
                    + "FROM\n"
                    + "	EMPRESA e"
            )) {
                while (rst.next()) {
                    ConvenioEmpresaIMP imp = new ConvenioEmpresaIMP();

                    imp.setId(rst.getString("id"));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setInscricaoEstadual(rst.getString("ie"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setTelefone(rst.getString("telefone"));

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
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "	CD_CLIENTE AS id_cliente,\n"
                    + "	cl.NM_CLI AS nome,\n"
                    + "	1 AS id_empresa,\n"
                    + "	cl.CNPJ_CLI AS cnpj,\n"
                    + "	cl.VL_LIMITE AS limite,\n"
                    + "	cl.FL_ATIVO_CLI AS ativo\n"
                    + "FROM\n"
                    + "	CONVENIO cc\n"
                    + "JOIN cliente cl ON cl.ID_CLI = cc.CD_CLIENTE "
            )) {
                while (rs.next()) {
                    ConveniadoIMP imp = new ConveniadoIMP();
                    imp.setId(rs.getString("id_cliente"));
                    imp.setNome(rs.getString("nome"));
                    imp.setIdEmpresa(rs.getString("id_empresa"));
                    imp.setCnpj(rs.getString("cnpj"));
                    imp.setConvenioLimite(rs.getDouble("limite"));
                    imp.setLojaCadastro(Integer.parseInt(getLojaOrigem()));
                    imp.setSituacaoCadastro(rs.getInt("ativo") == 1 ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);

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
                    "SELECT\n"
                    + "	cc.ID_CONTA AS id,\n"
                    + "	cc.ID_CONVENIO AS id_conveniado,\n"
                    + "	c.NM_ESTACAO AS ecf,\n"
                    + "	c.NR_GERADOR AS documento,\n"
                    + "	cc.DH_INCLUSAO AS data_hora,\n"
                    + "	cc.VL_CONVENIO  AS valor\n"
                    + "FROM\n"
                    + "	CONVENIO cc\n"
                    + "	JOIN CONTA c ON c.ID_CONTA = cc.ID_CONTA "
            )) {
                while (rst.next()) {
                    ConvenioTransacaoIMP imp = new ConvenioTransacaoIMP();

                    imp.setId(rst.getString("id"));
                    imp.setIdConveniado(rst.getString("id_conveniado"));
                    imp.setEcf(rst.getString("ecf"));
                    imp.setNumeroCupom(rst.getString("documento"));
                    imp.setDataHora(rst.getTimestamp("data_hora"));
                    imp.setValor(rst.getDouble("valor"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    /*  
    private Date dataInicioVenda;
    private Date dataTerminoVenda;

    public void setDataInicioVenda(Date dataInicioVenda) {
        this.dataInicioVenda = dataInicioVenda;
    }

    public void setDataTerminoVenda(Date dataTerminoVenda) {
        this.dataTerminoVenda = dataTerminoVenda;
    }

    @Override
    public Iterator<VendaIMP> getVendaIterator() throws Exception {
        return new Fenix2_5DAO.VendaIterator(getLojaOrigem(), this.dataInicioVenda, this.dataTerminoVenda);
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        return new Fenix2_5DAO.VendaItemIterator(getLojaOrigem(), this.dataInicioVenda, this.dataTerminoVenda);
    }

    private static class VendaIterator implements Iterator<VendaIMP> {

        public final static SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");

        private Statement stm = ConexaoFirebird.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaIMP next;
        private Set<String> uk = new HashSet<>();

        private void obterNext() {
            try {
                SimpleDateFormat timestampDate = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat timestamp = new SimpleDateFormat("yyyy-MM-dd hh:mm");

                if (next == null) {
                    if (rst.next()) {
                        next = new VendaIMP();
                        String id = rst.getString("id_venda");
                        if (!uk.add(id)) {
                            LOG.warning("Venda " + id + " já existe na listagem");
                        }
                        next.setId(id);
                        next.setNumeroCupom(Utils.stringToInt(rst.getString("numerocupom")));
                        next.setEcf(Utils.stringToInt(rst.getString("ecf")));
                        next.setData(rst.getDate("data"));

                        String horaInicio = timestampDate.format(rst.getDate("data")) + " " + rst.getString("hora");
                        String horaTermino = timestampDate.format(rst.getDate("data")) + " " + rst.getString("hora");
                        next.setHoraInicio(timestamp.parse(horaInicio));
                        next.setHoraTermino(timestamp.parse(horaTermino));
                        // next.setSubTotalImpressora(rst.getDouble("valor"));
                        //next.setIdClientePreferencial(rst.getString("id_cliente"));
                        //next.setCpf(rst.getString("cpf"));
                        // next.setNomeCliente(rst.getString("nomecliente"));
                        next.setCancelado(rst.getBoolean("cancelado"));
                    }
                }
            } catch (SQLException | ParseException ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {

            String strDataInicio = new SimpleDateFormat("yyyy-MM-dd").format(dataInicio);
            String strDataTermino = new SimpleDateFormat("yyyy-MM-dd").format(dataTermino);
            this.sql
                    = "select\n"
                    + "	v.cd_log_venda as id_venda,\n"
                    + "	v.nr_coo as numerocupom,\n"
                    + "	v.cd_caixa as ecf,\n"
                    + "	v.dt_cupom as data,\n"
                    + "	v.hr_cupom as hora,\n"
                    + "	case when tp_status = 'C' then 1 else 0 end cancelado\n"
                    + "from\n"
                    + "	logs.tb_log_venda v\n"
                    + "	 where cd_loja  = " + idLojaCliente + "\n"
                    + "	and  v.dt_cupom between '" + strDataInicio + "' and '" + strDataTermino + "'";
            LOG.log(Level.FINE, "SQL da venda: " + sql);
            rst = stm.executeQuery(sql);
        }

        @Override
        public boolean hasNext() {
            obterNext();
            return next != null;
        }

        @Override
        public VendaIMP next() {
            obterNext();
            VendaIMP result = next;
            next = null;
            return result;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported.");
        }
    }

    private static class VendaItemIterator implements Iterator<VendaItemIMP> {

        private Statement stm = ConexaoFirebird.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaItemIMP next;

        private void obterNext() {
            try {
                if (next == null) {
                    if (rst.next()) {
                        next = new VendaItemIMP();

                        next.setVenda(rst.getString("id_venda"));
                        next.setId(rst.getString("id_item"));
                        next.setSequencia(rst.getInt("seq"));
                        next.setProduto(rst.getString("produto"));
                        next.setUnidadeMedida(rst.getString("unidade"));
                        next.setCodigoBarras(rst.getString("ean"));
                        next.setDescricaoReduzida(rst.getString("descricao"));
                        next.setQuantidade(rst.getDouble("quantidade"));
                        next.setPrecoVenda(rst.getDouble("valor"));
                        next.setCancelado(rst.getBoolean("cancelado"));
                        next.setValorDesconto(rst.getDouble("desconto"));
                        next.setValorAcrescimo(rst.getDouble("acrescimo"));

                    }
                }
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaItemIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {
            this.sql
                    = "select\n"
                    + "	v.cd_log_venda as id_venda,\n"
                    + "	v.cd_log_venda::varchar || v.nr_coo::varchar || nr_item_log_venda::varchar id_item,\n"
                    + "	i.nr_item_log_venda as seq,\n"
                    + "	i.cd_produto as produto,\n"
                    + "	case when preco.tp_unidade_medida = 'GR' then 'KG' else 'UN' end unidade,\n"
                    + "	i.cd_barra ean,\n"
                    + "	i.nm_reduzido as descricao,\n"
                    + "	i.vl_qtd as quantidade,\n"
                    + "	i.vl_unitario as valor,\n"
                    + "	i.vl_venda as valor_total,\n"
                    + "	case when i.tp_status = 'C' then 1 else 0 end cancelado,\n"
                    + "	i.vl_desconto as desconto,\n"
                    + "	i.vl_acrescimo as acrescimo\n"
                    + " FROM logs.tb_log_venda v\n"
                    + " JOIN logs.tb_log_venda_item i ON i.cd_log_venda::integer = v.cd_log_venda::integer\n"
                    + " left join  precos.tb_preco preco on preco.cd_produto = i.cd_produto and preco.nr_loja = " + idLojaCliente + "\n"
                    + " where cd_loja  = " + idLojaCliente + "  \n"
                    + " and v.dt_cupom between '" + VendaIterator.FORMAT.format(dataInicio) + "' and '" + VendaIterator.FORMAT.format(dataTermino) + "' \n"
                    + " ORDER BY 1,3;";
            LOG.log(Level.FINE, "SQL da venda: " + sql);
            rst = stm.executeQuery(sql);
        }

        @Override
        public boolean hasNext() {
            obterNext();
            return next != null;
        }

        @Override
        public VendaItemIMP next() {
            obterNext();
            VendaItemIMP result = next;
            next = null;
            return result;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported.");
        }
    }*/
}
