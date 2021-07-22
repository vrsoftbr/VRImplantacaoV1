package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Importacao
 */
public class MobilityDAO extends InterfaceDAO implements MapaTributoProvider {

    public boolean importarSomenteBalanca = false;

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
                    OpcaoProduto.OFERTA,
                }
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
                    "SELECT \n" +
                    "   p.id,\n" +
                    "   p.codigo_barras ean,\n" +
                    "   p.codigo_interno,   \n" +
                    "   p.ativo,   \n" +
                    "   p.descricao descricaocompleta,\n" +
                    "   CASE\n" +
                    "   	WHEN p.id = 31843 THEN p.descricao\n" +
                    "   	ELSE p.descricao_resumida\n" +
                    "   END descricaoreduzida, \n" +
                    "   p.grupo merc1,\n" +
                    "   p.departamento merc2,\n" +
                    "   p.sessao merc3,\n" +
                    "   p.familia,\n" +
                    "   p.unidade,\n" +
                    "   p.margem,   \n" +
                    "   p.f_ult_preco_compra custoanterior,\n" +
                    "   p.preco_compra custosemimposto,\n" +
                    "   p.preco_custo custocomimposto,\n" +
                    "   p.preco_venda1 precovenda,\n" +
                    "   p.pesado,\n" +
                    "   p.validade,\n" +
                    "   p.estoque_max,\n" +
                    "   p.estoque_min,\n" +
                    "   p.estoque_atual,\n" +
                    "   p.data_inclusao,\n" +
                    "   p.s_ncm ncm,\n" +
                    "   p.f_mva_st mva,\n" +
                    "   p.aliquota id_aliquota,\n" +
                    "   p.icms icms_credito,\n" +
                    "   p.st cst_credito,\n" +
                    "   p.f_porcent_red_icms icms_red_credito,\n" +
                    "   p.s_cod_cst_pis_entrada pis_entrada,\n" +
                    "   p.s_cod_cst_pis_saida pis_saida,\n" +
                    "   p.s_cod_cst_cofins_entrada cofins_entrada,\n" +
                    "   p.s_cod_cst_cofins_saida cofins_saida,\n" +
                    "   p.s_cest cest,\n" +
                    "   p.s_cod_pis_saida naturezareceita\n" +
                    "from\n" +
                    "    produtos p\n" +
                    "order by\n" +
                    "    p.id"
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
                    /*"select\n" +
                     "    p.codigo_interno id,\n" +
                     "    s_codigo ean\n" +
                     "from\n" +
                     "    cod_auxiliares c\n" +
                     "join produtos p on c.id_produto = p.id"*/
                    " select\n"
                    + "     codigo_interno id,\n"
                    + "     codigo_barras ean\n"
                    + " from produtos\n"
                    + "     order by 1"
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

                    imp.setId(rs.getString("id"));
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
                    ""
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();

                    imp.setId(rst.getString("CTRID"));
                    imp.setNumeroCupom(rst.getString("ctrnum"));
                    imp.setIdCliente(rst.getString("clicod"));
                    imp.setEcf(rst.getString("cxanum"));
                    imp.setDataEmissao(rst.getDate("ctrdatemi"));
                    imp.setDataVencimento(rst.getDate("ctrdatvnc"));
                    imp.setValor(rst.getDouble("ctrvlrdev"));
                    imp.setObservacao(rst.getString("ctrobs"));

                    result.add(imp);
                }
            }
        }

        return result;
    }
}
