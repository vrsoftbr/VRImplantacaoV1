package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao.classe.ConexaoOracle;
import vrimplantacao.dao.interfaces.AriusDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;

/**
 *
 * @author Importacao
 */
public class MobilityDAO extends InterfaceDAO implements MapaTributoProvider {

    public boolean importarSomenteBalanca = false;

    private Date vendaDataInicio;
    private Date vendaDataTermino;

    private static final Logger LOG = Logger.getLogger(AriusDAO.class.getName());

    @Override
    public String getSistema() {
        return "MOBILITY";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                new OpcaoProduto[]{
                    OpcaoProduto.MERCADOLOGICO,
                    OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                    OpcaoProduto.MERCADOLOGICO_PRODUTO,
                    OpcaoProduto.FAMILIA,
                    OpcaoProduto.FAMILIA_PRODUTO,
                    OpcaoProduto.IMPORTAR_MANTER_BALANCA,
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
                    OpcaoProduto.ESTOQUE_MAXIMO,
                    OpcaoProduto.ESTOQUE_MINIMO,
                    OpcaoProduto.PRECO,
                    OpcaoProduto.CUSTO,
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
                    OpcaoProduto.OFERTA,}
        ));
    }

    public List<Estabelecimento> getLojaCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select id, s_nome_fantasia fantasia from configuracoes"
            )) {
                while (rs.next()) {
                    result.add(new Estabelecimento(rs.getString("id"), rs.getString("fantasia")));
                }
            }
        }
        return result;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "    id,\n"
                    + "    s_descricao || ' - ' ||\n"
                    + "    f_taxa || '% R ' ||\n"
                    + "    f_reducao_base_calculo as descricao\n"
                    + "from\n"
                    + "    aliquotas\n"
                    + "where\n"
                    + "    i_ativa = 1"
            )) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(rs.getString("id"), rs.getString("descricao")));
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
                    " SELECT \n"
                    + "	DISTINCT\n"
                    + "	P.GRUPO,\n"
                    + "	M1.S_DESCRICAO AS MERC1,\n"
                    + "	P.DEPARTAMENTO, \n"
                    + "	M2.S_DESCRICAO AS MERC2,\n"
                    + "	P.SESSAO, \n"
                    + "	M3.S_DESCRICAO AS MERC3\n"
                    + "FROM\n"
                    + "	PRODUTOS P\n"
                    + "INNER JOIN GRUPOS M1 ON M1.ID = P.GRUPO\n"
                    + "INNER JOIN DEPARTAMENTOS M2 ON M2.ID = P.DEPARTAMENTO\n"
                    + "INNER JOIN SESSOES M3 ON M3.ID = P.SESSAO\n"
                    + "ORDER BY\n"
                    + "	P.GRUPO,\n"
                    + "	P.DEPARTAMENTO,\n"
                    + "	P.SESSAO"
            )) {
                while (rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rs.getString("GRUPO"));
                    imp.setMerc1Descricao(rs.getString("MERC1"));
                    imp.setMerc2ID(rs.getString("DEPARTAMENTO"));
                    imp.setMerc2Descricao(rs.getString("MERC2"));
                    imp.setMerc3ID(rs.getString("SESSAO"));
                    imp.setMerc3Descricao(rs.getString("MERC3"));

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
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "	ID,\n"
                    + "	S_DESCRICAO\n"
                    + "FROM\n"
                    + "	FAMILIAS\n"
                    + "ORDER BY\n"
                    + "	ID"
            )) {
                while (rs.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("id"));
                    imp.setDescricao(rs.getString("S_DESCRICAO"));

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
                    "SELECT \n"
                    + "   p.id,\n"
                    + "   p.codigo_barras ean,\n"
                    + "   p.codigo_interno,   \n"
                    + "   p.ativo,   \n"
                    + "   p.descricao descricaocompleta,\n"
                    + "   CASE\n"
                    + "   	WHEN p.id = 31843 THEN p.descricao\n"
                    + "   	ELSE p.descricao_resumida\n"
                    + "   END descricaoreduzida, \n"
                    + "   p.grupo merc1,\n"
                    + "   p.departamento merc2,\n"
                    + "   p.sessao merc3,\n"
                    + "   p.familia,\n"
                    + "   p.unidade,\n"
                    + "   p.margem,   \n"
                    + "   p.f_ult_preco_compra custoanterior,\n"
                    + "   p.preco_compra custosemimposto,\n"
                    + "   p.preco_custo custocomimposto,\n"
                    + "   p.preco_venda1 precovenda,\n"
                    + "   p.pesado,\n"
                    + "   p.validade,\n"
                    + "   p.estoque_max,\n"
                    + "   p.estoque_min,\n"
                    + "   p.estoque_atual,\n"
                    + "   p.data_inclusao,\n"
                    + "   p.s_ncm ncm,\n"
                    + "   p.f_mva_st mva,\n"
                    + "   p.aliquota id_aliquota,\n"
                    + "   p.icms icms_credito,\n"
                    + "   p.st cst_credito,\n"
                    + "   p.f_porcent_red_icms icms_red_credito,\n"
                    + "   p.s_cod_cst_pis_entrada pis_entrada,\n"
                    + "   p.s_cod_cst_pis_saida pis_saida,\n"
                    + "   p.s_cod_cst_cofins_entrada cofins_entrada,\n"
                    + "   p.s_cod_cst_cofins_saida cofins_saida,\n"
                    + "   p.s_cest cest,\n"
                    + "   p.s_cod_pis_saida naturezareceita\n"
                    + "from\n"
                    + "    produtos p\n"
                    + "order by\n"
                    + "    p.id"
            )) {
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().getProdutosBalanca();
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("codigo_interno"));

                    imp.setEan(rs.getString("ean"));
                    int eanBalanca = Utils.stringToInt(imp.getEan().substring(0, imp.getEan().length() - 1), -2);
                    ProdutoBalancaVO balanca = produtosBalanca.get(eanBalanca);
                    if (balanca != null) {
                        imp.setEan(String.valueOf(eanBalanca));
                        imp.seteBalanca(true);
                        imp.setTipoEmbalagem("U".equals(balanca.getPesavel()) ? "UN" : "KG");
                        imp.setValidade(balanca.getValidade());
                        imp.setQtdEmbalagem(1);
                    } else {
                        if (this.importarSomenteBalanca) {
                            continue;
                        }
                        imp.seteBalanca(rs.getInt("pesado") == 1);
                        imp.setEan(rs.getString("ean"));
                        imp.setTipoEmbalagem(rs.getString("unidade"));
                        imp.setValidade(rs.getInt("validade"));
                        imp.setQtdEmbalagem(1);
                        long ean = Utils.stringToLong(rs.getString("ean"), -2);
                        imp.setManterEAN(ean > 0 && ean <= 999999 && !imp.isBalanca());
                    }

                    imp.setSituacaoCadastro(rs.getInt("ativo"));
                    imp.setDescricaoCompleta(rs.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rs.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setCodMercadologico1(rs.getString("merc1"));
                    imp.setCodMercadologico2(rs.getString("merc2"));
                    imp.setCodMercadologico3(rs.getString("merc3"));
                    imp.setIdFamiliaProduto(rs.getString("familia"));
                    imp.setMargem(rs.getDouble("margem"));
                    imp.setCustoAnteriorComImposto(rs.getDouble("custoanterior"));
                    imp.setCustoAnteriorSemImposto(rs.getDouble("custoanterior"));
                    imp.setCustoComImposto(rs.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(rs.getDouble("custosemimposto"));
                    imp.setPrecovenda(rs.getDouble("precovenda"));
                    imp.setEstoqueMaximo(rs.getDouble("estoque_max"));
                    imp.setEstoqueMinimo(rs.getDouble("estoque_min"));
                    imp.setEstoque(rs.getDouble("estoque_atual"));
                    imp.setDataCadastro(rs.getDate("data_inclusao"));
                    imp.setNcm(rs.getString("ncm"));
                    imp.setCest(rs.getString("cest"));
                    imp.setPiscofinsCstCredito(rs.getString("pis_entrada"));
                    imp.setPiscofinsCstDebito(rs.getString("pis_saida"));
                    imp.setPiscofinsNaturezaReceita(rs.getString("naturezareceita"));

                    // Icms debito
                    imp.setIcmsDebitoId(rs.getString("id_aliquota"));
                    imp.setIcmsDebitoForaEstadoId(rs.getString("id_aliquota"));
                    imp.setIcmsDebitoForaEstadoNfId(rs.getString("id_aliquota"));
                    imp.setIcmsConsumidorId(rs.getString("id_aliquota"));

                    //Icms Credito
                    imp.setIcmsAliqEntrada(rs.getDouble("icms_credito"));
                    imp.setIcmsReducaoEntrada(rs.getDouble("icms_red_credito"));
                    imp.setIcmsCstEntrada(rs.getInt("cst_credito"));

                    imp.setIcmsAliqEntradaForaEstado(imp.getIcmsAliqEntrada());
                    imp.setIcmsCstEntradaForaEstado(imp.getIcmsCstEntrada());
                    imp.setIcmsReducaoEntradaForaEstado(imp.getIcmsReducaoEntrada());

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "    p.codigo_interno id,\n"
                    + "    s_codigo ean\n"
                    + "from\n"
                    + "    cod_auxiliares c\n"
                    + "join produtos p on c.id_produto = p.id"
            )) {
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("id"));
                    imp.setEan(rs.getString("ean"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "    i_numero id,\n"
                    + "    ativo,\n"
                    + "    razao_social,\n"
                    + "    nome_fantasia,\n"
                    + "    cpf,\n"
                    + "    cnpj,\n"
                    + "    insc_estadual ie,\n"
                    + "    endereco,\n"
                    + "    complemento,\n"
                    + "    bairro,\n"
                    + "    codigo_cidade,\n"
                    + "    end_num numero,\n"
                    + "    cidade,\n"
                    + "    uf,\n"
                    + "    cep,\n"
                    + "    ddd,\n"
                    + "    ddd2,\n"
                    + "    ddd_cel,\n"
                    + "    ddd || '' || telefone1 as telprincipal,\n"
                    + "    ddd2 || '' || telefone2 as telefone2,\n"
                    + "    fax,\n"
                    + "    celular,\n"
                    + "    contato,\n"
                    + "    site,\n"
                    + "    observacoes,\n"
                    + "    prazo_entrega,\n"
                    + "    condicao_pagto,\n"
                    + "    data_cadastro,\n"
                    + "    email\n"
                    + "from\n"
                    + "    fornecedores"
            )) {
                while (rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id"));
                    imp.setRazao(rs.getString("razao_social"));
                    imp.setAtivo(rs.getInt("ativo") == 1);
                    imp.setFantasia(rs.getString("nome_fantasia"));
                    imp.setCnpj_cpf(rs.getString("cnpj"));
                    if (imp.getCnpj_cpf() == null) {
                        imp.setCnpj_cpf(rs.getString("cpf"));
                    }
                    imp.setIe_rg(rs.getString("ie"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setIbge_municipio(rs.getInt("codigo_cidade"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));

                    if (rs.getString("telprincipal") != null
                            && !"0".equals(rs.getString("telprincipal"))
                            && !"".equals(rs.getString("telprincipal"))) {
                        imp.setTel_principal(rs.getString("telprincipal"));
                    }
                    if (rs.getString("telefone2") != null
                            && !"0".equals(rs.getString("telefone2"))
                            && !"".equals(rs.getString("telefone2"))) {
                        imp.addContato("1",
                                "TELEFONE2",
                                (rs.getString("telefone2")),
                                null,
                                TipoContato.COMERCIAL,
                                null);
                    }
                    if (rs.getString("contato") != null
                            && !"0".equals(rs.getString("contato"))
                            && !"".equals(rs.getString("contato"))) {
                        imp.addContato("2",
                                (rs.getString("contato")),
                                null,
                                null,
                                TipoContato.COMERCIAL,
                                null);
                    }
                    if (rs.getString("celular") != null
                            && !"0".equals(rs.getString("celular"))
                            && !"".equals(rs.getString("celular"))) {
                        imp.addContato("3",
                                "CELULAR",
                                null,
                                (rs.getString("ddd_cel") + rs.getString("celular")),
                                TipoContato.COMERCIAL,
                                null);
                    }
                    if (rs.getString("site") != null
                            && !"".equals(rs.getString("site"))) {
                        imp.addContato("4",
                                rs.getString("site"),
                                null,
                                null,
                                TipoContato.COMERCIAL,
                                null);
                    }
                    if (rs.getString("email") != null
                            && !"".equals(rs.getString("email"))) {
                        imp.addContato("4",
                                "EMAIL",
                                null,
                                null,
                                TipoContato.COMERCIAL,
                                rs.getString("email"));
                    }
                    if (rs.getString("observacoes") != null && !"".equals(rs.getString("observacoes"))) {
                        imp.setObservacao(rs.getString("observacoes"));
                    }
                    imp.setPrazoEntrega(rs.getInt("prazo_entrega"));
                    imp.setDatacadastro(rs.getDate("data_cadastro"));

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
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "    pf.id,\n"
                    + "    f.i_numero id_fornecedor,\n"
                    + "    p.codigo_interno id_produto,\n"
                    + "    case when s_sequencial = '' then p.codigo_interno\n"
                    + "    else s_sequencial end as codexterno,\n"
                    + "    i_embalagem embalagem,\n"
                    + "    s_unidade_medida unidade,\n"
                    + "    pf.d_data_compra dataalteracao\n"
                    + "from\n"
                    + "    codigo_ref_fornecedor pf\n"
                    + "join produtos p on pf.id_produto = p.id\n"
                    + "join fornecedores f on pf.id_fornecedor = f.id\n"
                    + "order by\n"
                    + "    f.numero, p.codigo_interno"
            )) {
                while (rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setIdProduto(rs.getString("id_produto"));
                    imp.setIdFornecedor(rs.getString("id_fornecedor"));
                    imp.setCodigoExterno(rs.getString("codexterno"));
                    imp.setDataAlteracao(rs.getDate("dataalteracao"));
                    imp.setQtdEmbalagem(rs.getDouble("embalagem"));

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
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "    id,\n"
                    + "    numero,\n"
                    + "    ativo,\n"
                    + "    nome,\n"
                    + "    endereco,\n"
                    + "    complemento,\n"
                    + "    bairro,\n"
                    + "    cidade,\n"
                    + "    uf,\n"
                    + "    cep,\n"
                    + "    tipo_pessoa,\n"
                    + "    cpf,\n"
                    + "    cnpj,\n"
                    + "    insc_estadual,\n"
                    + "    rg,\n"
                    + "    ddd,\n"
                    + "    telefone,\n"
                    + "    fax, \n"
                    + "    celular,\n"
                    + "    site,\n"
                    + "    data_cadastro,\n"
                    + "    data_aniversario,\n"
                    + "    limite_credito, \n"
                    + "    email,\n"
                    + "    s_codigo_municipio,\n"
                    + "    end_num,\n"
                    + "    comentarios\n"
                    + "from \n"
                    + "    clientes\n"
                    + "order by\n"
                    + "    id"
            )) {
                while (rs.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rs.getString("numero"));
                    imp.setRazao(rs.getString("nome"));
                    imp.setAtivo(rs.getInt("ativo") == 1);
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setNumero(rs.getString("end_num"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));
                    imp.setCnpj(rs.getString("cnpj"));

                    if (imp.getCnpj() != null && !imp.getCnpj().trim().isEmpty()) {
                        imp.setCnpj(rs.getString("cnpj"));
                    } else {
                        imp.setCnpj(rs.getString("cpf"));
                    }

                    String ie = rs.getString("insc_estadual");

                    if (ie != null && !"".equals(ie)) {
                        imp.setInscricaoestadual(ie);
                    } else {
                        imp.setInscricaoestadual(rs.getString("rg"));
                    }

                    imp.setTelefone(rs.getString("ddd") + rs.getString("telefone"));
                    imp.setCelular(rs.getString("ddd") + rs.getString("celular"));
                    imp.setDataCadastro(rs.getDate("data_cadastro"));
                    imp.setDataNascimento(rs.getDate("data_aniversario"));
                    imp.setValorLimite(rs.getDouble("limite_credito"));
                    imp.setEmail(rs.getString("email"));

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
            StringBuilder builder = new StringBuilder();
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + " cv.id AS id,\n"
                    + " cl.numero AS clienteid,\n"
                    + " cl.id,\n"
                    + " cl.nome AS nome,\n"
                    + " cv.s_cupom AS numerocupom,\n"
                    + " cv.f_debito AS valor,\n"
                    + " cv.f_val_pago_ref AS valpago,\n"
                    + " CASE WHEN cv.f_val_pago_ref <> 0 THEN (cv.f_debito - cv.f_val_pago_ref)\n"
                    + " ELSE cv.f_debito\n"
                    + " END AS valortotal,\n"
                    + " cv.f_juros AS juros,\n"
                    + " cv.d_data AS dataemissao,\n"
                    + " cv.d_dia_pagamento AS datavencimento,\n"
                    + " cv.s_observacao AS obs\n"
                    + "FROM CONVENIOS cv\n"
                    + "JOIN CLIENTES cl ON cl.id = cv.id_cliente \n"
                    + "WHERE \n"
                    + "cv.f_debito <> cv.f_val_pago_ref\n"
                    + "AND \n"
                    + "cv.f_debito > cv.f_val_pago_ref"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();

                    imp.setId(rst.getString("id"));
                    imp.setNumeroCupom(rst.getString("numerocupom"));
                    imp.setIdCliente(rst.getString("clienteid"));
                    imp.setDataEmissao(rst.getDate("dataemissao"));
                    imp.setDataVencimento(rst.getDate("datavencimento"));
                    imp.setValor(rst.getDouble("valortotal"));
                    imp.setObservacao(rst.getString("obs"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ContaPagarIMP> getContasPagar() throws Exception {
        List<ContaPagarIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + " icp.id as id,\n"
                    + " --cp.id,\n"
                    + "	--CAST(cp.id AS varchar(100)) ||'.'|| CAST(icp.pp AS varchar(100)) AS id_concat,\n"
                    + "	f.id AS fornecedorid,\n"
                    + "	f.cnpj AS cnpj,\n"
                    + "	cp.doc_fiscal AS numerodocumento,\n"
                    + "	icp.dt_cadastro AS dataemissao,\n"
                    + "	cp.dt_cadastro AS dataentrada,\n"
                    + "	icp.vlr_real AS valor,\n"
                    + "	icp.vlr_pago,\n"
                    + "	icp.juros AS juros,\n"
                    + "	icp.mora,\n"
                    + "	icp.dt_vecto AS datavencimento,\n"
                    + "	icp.desconto AS desconto,\n"
                    + "	icp.pp AS parcela,\n"
                    + "	COALESCE(icp.obs,\n"
                    + "	cp.descricao) AS obs\n"
                    + "FROM\n"
                    + "	CONTAS_PAGAR cp\n"
                    + "JOIN FORNECEDORES f ON	f.id = cp.id_fornecedor\n"
                    + "JOIN ITENS_CONTAS_PAGAR icp ON	icp.id_contas_pagar = cp.id\n"
                    + "WHERE\n"
                    + "	icp.vlr_pago = 0"
            )) {
                while (rs.next()) {
                    ContaPagarIMP imp = new ContaPagarIMP();

                    imp.setId(rs.getString("id"));
                    imp.setIdFornecedor(rs.getString("fornecedorid"));
                    imp.setCnpj(rs.getString("cnpj"));
                    imp.setNumeroDocumento(rs.getString("numerodocumento"));
                    imp.setDataEmissao(rs.getDate("dataemissao"));
                    imp.setDataEntrada(rs.getDate("dataentrada"));
                    imp.setValor(rs.getDouble("valor"));
                    imp.addVencimento(rs.getDate("datavencimento"), rs.getDouble("valor"));
                    imp.setObservacao(rs.getString("obs"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    public void setVendaDataInicio(Date vendaDataInicio) {
        this.vendaDataInicio = vendaDataInicio;
    }

    public void setVendaDataTermino(Date vendaDataTermino) {
        this.vendaDataTermino = vendaDataTermino;
    }

    @Override
    public Iterator<VendaIMP> getVendaIterator() throws Exception {
        return new VendaIterator(getLojaOrigem(), vendaDataInicio, vendaDataTermino);
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        return new VendaItemIterator(getLojaOrigem(), vendaDataInicio, vendaDataTermino);
    }

    private static class VendaIterator implements Iterator<VendaIMP> {

        private Statement stm;
        private ResultSet rst;
        private final String sql;

        public VendaIterator(String origem, Date vendaDataInicio, Date vendaDataTermino) throws Exception {
            this.stm = ConexaoOracle.createStatement();
            this.sql
                    = "SELECT\n"
                    + "	id AS id,\n"
                    + "	S_COO AS numerocupom,\n"
                    + "	rpad(S_NUMERO_FABRICACAO,2) AS ecf,\n"
                    + "	D_DATA_INICIO_EMISSAO AS DATA,\n"
                    + "	S_HORA AS hora,\n"
                    + "	CASE\n"
                    + "		WHEN S_INDICADOR_CANCELAMENTO = 'N' THEN 0\n"
                    + "		ELSE 1\n"
                    + "	END AS cancelado\n"
                    + "FROM\n"
                    + "	R04\n"
                    + "WHERE\n"
                    + "	D_DATA_INICIO_EMISSAO >= '" + DATE_FORMAT.format(vendaDataInicio) + "'\n"
                    + "	AND\n"
                    + " D_DATA_INICIO_EMISSAO <= '" + DATE_FORMAT.format(vendaDataTermino) + "'";

            this.stm.setFetchSize(10000);
            this.rst = stm.executeQuery(sql);
        }

        @Override
        public boolean hasNext() {
            try {
                return !rst.isClosed() && !rst.isLast();
            } catch (SQLException ex) {
                LOG.log(Level.SEVERE, "Erro no hasNext()\n" + sql, ex);
                throw new RuntimeException(ex);
            }
        }

        @Override
        public VendaIMP next() {
            try {
                if (rst.next()) {
                    VendaIMP imp = new VendaIMP();
                    imp.setId(rst.getString("id"));
                    imp.setNumeroCupom(rst.getInt("numerocupom"));
                    imp.setEcf(rst.getInt("ecf"));
                    imp.setData(rst.getDate("data"));
                    imp.setHoraInicio(rst.getDate("data"));
                    imp.setHoraTermino(rst.getDate("data"));
                    imp.setCancelado(rst.getBoolean("cancelado"));

                    return imp;
                } else {
                    rst.close();
                    stm.close();
                    return null;
                }
            } catch (SQLException ex) {
                LOG.log(Level.SEVERE, "Erro no next()", ex);
                throw new RuntimeException(ex);
            }
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Não suportado.");
        }
    }

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

    private static class VendaItemIterator implements Iterator<VendaItemIMP> {

        private Statement stm;
        private ResultSet rst;
        private String sql;

        public VendaItemIterator(String origem, Date vendaDataInicio, Date vendaDataTermino) throws Exception {
            this.stm = ConexaoOracle.createStatement();
            this.sql
                    = "SELECT\n"
                    + "	R05.id AS id,\n"
                    + "	R04.id AS idvenda,\n"
                    + "	R05.S_COO AS numerocupom,\n"
                    + "	R05.S_NUMERO_FABRICACAO AS ecf,\n"
                    + "	R05.S_NUM_ITEM AS sequencia,\n"
                    + "	R04.D_DATA_INICIO_EMISSAO AS DATA,\n"
                    + "	R04.S_HORA AS hora,\n"
                    + "	R05.R_QUANTIDADE AS qtde,\n"
                    + "	R05.S_UNIDADE AS unidade,\n"
                    + "	R05.S_CODIGO_PRODUTO AS ean,\n"
                    + "	R05.R_VALOR_UNITARIO AS valor,\n"
                    + "	R05.R_VALOR_TOTAL_LIQUIDO AS valor2,\n"
                    + "	R05.R_DESCONTO_ITEM AS desconto,\n"
                    + "	R05.R_ACRESCIMO_ITEM AS acrescimo,\n"
                    + "	R05.R_TAXA_ALIQUOTA AS aliquota,\n"
                    + "	R05.S_ST AS cst\n"
                    + " FROM\n"
                    + "	R05\n"
                    + " JOIN R04 ON	R04.id = R05.id_R04\n"
                    + " WHERE\n"
                    + "    R04.D_DATA_INICIO_EMISSAO >= '2021-10-07'\n"
                    + "	AND\n"
                    + "    R04.D_DATA_INICIO_EMISSAO <= '2021-10-07'\n"
                    + "    ORDER BY 1";
            this.stm.setFetchSize(10000);
            this.rst = stm.executeQuery(sql);
        }

        @Override
        public boolean hasNext() {
            try {
                return !rst.isClosed() && !rst.isLast();
            } catch (SQLException ex) {
                LOG.log(Level.SEVERE, "Erro no hasNext()\n" + sql, ex);
                throw new RuntimeException(ex);
            }
        }

        public void obterAliquota(VendaItemIMP item, String icms, String st) throws SQLException {
            /*
             0700   7.00    ALIQUOTA 07%
             1200   12.00   ALIQUOTA 12%
             1800   18.00   ALIQUOTA 18%
             2500   25.00   ALIQUOTA 25%
             1100   11.00   ALIQUOTA 11%
             I      0.00    ISENTO
             F      0.00    SUBST TRIBUTARIA
             N      0.00    NAO INCIDENTE
             */
            int cst;
            double aliq = 0;
            switch (st) {
                case "N":
                    cst = 41;
                    aliq = 0;
                    break;
                case "T":
                    if ("18".equals(icms)) {
                        aliq = 18;
                    } else if ("12".equals(icms)) {
                        aliq = 12;
                    }
                    cst = 0;
                    break;
                default:
                    cst = 40;
                    aliq = 0;
                    break;
            }
            item.setIcmsCst(cst);
            item.setIcmsAliq(aliq);
        }

        @Override
        public VendaItemIMP next() {
            try {
                if (rst.next()) {
                    VendaItemIMP imp = new VendaItemIMP();

                    imp.setId(rst.getString("id"));
                    imp.setSequencia(rst.getInt("sequencia"));
                    imp.setVenda(rst.getString("idvenda"));
                    imp.setCodigoBarras(rst.getString("ean"));
                    imp.setDescricaoReduzida(rst.getString("descritivo_pdv"));
                    imp.setQuantidade(rst.getDouble("qtde"));
                    imp.setPrecoVenda(rst.getDouble("valor"));
                    imp.setValorDesconto(rst.getDouble("desconto"));
                    imp.setValorAcrescimo(rst.getDouble("acrescimo"));
                    imp.setUnidadeMedida(rst.getString("unidade"));

                    obterAliquota(imp, rst.getString("aliquota"), rst.getString("cst"));

                    return imp;
                } else {
                    rst.close();
                    stm.close();
                    return null;
                }
            } catch (SQLException ex) {
                LOG.log(Level.SEVERE, "Erro no next()", ex);
                throw new RuntimeException(ex);
            }
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Não suportado.");
        }
    }
}
