package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao.classe.ConexaoSQLite;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto.ProdutoAnteriorDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ClienteIMP;
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
public class SircomDAO extends InterfaceDAO implements MapaTributoProvider {

    //private Date vendasDataInicio = null;
    //private Date vendasDataTermino = null;
    private static final Logger LOG = Logger.getLogger(SircomDAO.class.getName());
    
    @Override
    public String getSistema() {
        return "SIRCOM";
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
                    OpcaoProduto.MARGEM
                }
        ));
    }
    
    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        try(Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "    id,\n" +
                    "    s_descricao || ' - ' ||\n" +
                    "    f_taxa || '% R ' ||\n" +
                    "    f_reducao_base_calculo as descricao\n" +
                    "from\n" +
                    "    aliquotas\n" +
                    "where\n" +
                    "    i_ativa = 1"
            )) {
                while(rs.next()) {
                    result.add(new MapaTributoIMP(rs.getString("id"), rs.getString("descricao")));
                }
            }
        }
        return result;
    }
    
    public List<Estabelecimento> getLojaCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try(Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "    id,\n" +
                    "    s_razao_social razaosocial\n" +
                    "from\n" +
                    "    lojas"
            )) {
                while(rs.next()) {
                    result.add(new Estabelecimento(rs.getString("id"), rs.getString("razaosocial")));
                }
            }
        }
        return result;
    }
    
    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        try(Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    " SELECT \n" +
                    "	DISTINCT\n" +
                    "	P.GRUPO,\n" +
                    "	M1.S_DESCRICAO AS MERC1,\n" +
                    "	P.DEPARTAMENTO, \n" +
                    "	M2.S_DESCRICAO AS MERC2,\n" +
                    "	P.SESSAO, \n" +
                    "	M3.S_DESCRICAO AS MERC3\n" +
                    "FROM\n" +
                    "	PRODUTOS P\n" +
                    "INNER JOIN GRUPOS M1 ON M1.ID = P.GRUPO\n" +
                    "INNER JOIN DEPARTAMENTOS M2 ON M2.ID = P.DEPARTAMENTO\n" +
                    "INNER JOIN SESSOES M3 ON M3.ID = P.SESSAO\n" +
                    "ORDER BY\n" +
                    "	P.GRUPO,\n" +
                    "	P.DEPARTAMENTO,\n" +
                    "	P.SESSAO"
            )) {
                while(rs.next()) {
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
        try(Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "    id,\n" +
                    "    s_descricao descricao\n" +
                    "from\n" +
                    "    familias\n" +
                    "order by    \n" +
                    "    s_descricao"
            )) {
                while(rs.next()) {
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
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try(Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "    p.codigo_interno id,\n" +
                    "    s_codigo ean\n" +
                    "from\n" +
                    "    cod_auxiliares c\n" +
                    "join produtos p on c.id_produto = p.id"
            )) {
                while(rs.next()) {
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
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try(Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "   p.id,\n" +
                    "   p.codigo_barras ean,\n" +
                    "   p.codigo_interno,\n" +
                    "   p.ativo,\n" +
                    "   p.descricao descricaocompleta,\n" +
                    "   p.descricao_resumida descricaoreduzida,\n" +
                    "   p.grupo merc1,\n" +
                    "   p.departamento merc2,\n" +
                    "   p.sessao merc3,\n" +
                    "   p.familia,\n" +
                    "   p.unidade,\n" +
                    "   p.margem,\n" +
                    "   p.f_ult_preco_compra custoanterior,\n" +
                    "   p.preco_compra custocomimposto,\n" +
                    "   p.preco_custo custosemimposto,\n" +
                    "   p.preco_venda1 precovenda,\n" +
                    "   p.pesado,\n" +
                    "   p.alt_balanca enviabalanca,\n" +        
                    "   p.validade,\n" +
                    "   p.estoque_max,\n" +
                    "   p.estoque_min,\n" +
                    "   p.estoque_atual,\n" +
                    "   p.data_inclusao,\n" +
                    "   p.s_ncm ncm,\n" +
                    "   p.f_mva_st mva,\n" +
                    "   p.icms icms_credito,\n" +
                    "   p.st cst_credito,\n" +
                    "   p.f_porcent_red_icms icms_red_credito,\n" +
                    "   a.s_tipo tipoaliquota,\n" +
                    "   a.f_taxa icms_debito,\n" +
                    "   a.f_reducao_base_calculo icms_red_debito,\n" +
                    "   p.s_cod_cst_pis_entrada pis_entrada,\n" +
                    "   p.s_cod_cst_pis_saida pis_saida,\n" +
                    "   p.s_cod_cst_cofins_entrada cofins_entrada,\n" +
                    "   p.s_cod_cst_cofins_saida cofins_saida,\n" +
                    "   p.s_cest cest,\n" +
                    "   p.s_cod_pis_saida naturezareceita\n" +
                    "from\n" +
                    "    produtos p\n" +
                    "left join aliquotas a on p.aliquota = a.id\n" +
                    "order by\n" +
                    "    p.id"
            )) {
                while(rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("codigo_interno"));
                    imp.setEan(rs.getString("ean"));
                    if(rs.getInt("pesado") != 0 ||
                            (rs.getLong("ean") == rs.getLong("codigo_interno") && rs.getInt("enviabalanca") == 1)) {
                        imp.setEan(imp.getImportId());
                        imp.seteBalanca(true);
                    }                    
                    imp.setValidade(rs.getInt("validade"));
                    imp.setSituacaoCadastro(rs.getInt("ativo"));
                    imp.setDescricaoCompleta(rs.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rs.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setCodMercadologico1(rs.getString("merc1"));
                    imp.setCodMercadologico2(rs.getString("merc2"));
                    imp.setCodMercadologico3(rs.getString("merc3"));
                    imp.setIdFamiliaProduto(rs.getString("familia"));
                    imp.setTipoEmbalagem(rs.getString("unidade"));
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
                    imp.setIcmsAliqSaida(rs.getDouble("icms_debito"));
                    imp.setIcmsReducaoSaida(rs.getDouble("icms_red_debito"));
                    
                    if(rs.getString("tipoaliquota") != null && !"".equals(rs.getString("tipoaliquota"))) {
                        switch(rs.getString("tipoaliquota").trim()) {
                            case "F" : imp.setIcmsCstSaida(60);
                                break;
                            case "I" : imp.setIcmsCstSaida(40);
                                break;
                            case "N" : imp.setIcmsCstSaida(41);
                                break;
                            case "T" : imp.setIcmsCstSaida(0);
                                break;
                            case "R" : imp.setIcmsCstSaida(20);
                                if(rs.getDouble("icms_red_debito") == 0) {
                                    imp.setIcmsCstSaida(0);
                                }
                                break;
                            default : imp.setIcmsCstSaida(40);
                                break;
                        }
                    }
                    imp.setIcmsAliqSaidaForaEstado(imp.getIcmsAliqSaida());
                    imp.setIcmsCstSaidaForaEstado(imp.getIcmsCstSaida());
                    imp.setIcmsReducaoSaidaForaEstado(imp.getIcmsReducaoSaida());
                    
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
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        try(Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "    i_numero id,\n" +
                    "    ativo,\n" +
                    "    razao_social,\n" +
                    "    nome_fantasia,\n" +
                    "    cpf,\n" +
                    "    cnpj,\n" +
                    "    insc_estadual ie,\n" +
                    "    endereco,\n" +
                    "    complemento,\n" +
                    "    bairro,\n" +
                    "    codigo_cidade,\n" +
                    "    end_num numero,\n" +
                    "    cidade,\n" +
                    "    uf,\n" +
                    "    cep,\n" +
                    "    ddd,\n" +
                    "    ddd2,\n" +
                    "    ddd_cel,\n" +
                    "    ddd || '' || telefone1 as telefone,\n" +
                    "    ddd2 || '' || telefone2 as telefone2,\n" +
                    "    fax,\n" +
                    "    celular,\n" +
                    "    contato,\n" +
                    "    site,\n" +
                    "    observacoes,\n" +
                    "    prazo_entrega,\n" +
                    "    condicao_pagto,\n" +
                    "    data_cadastro,\n" +
                    "    email\n" +
                    "from\n" +
                    "    fornecedores"
            )) {
                while(rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id"));
                    imp.setRazao(rs.getString("razao_social"));
                    imp.setAtivo(rs.getInt("ativo") == 1);
                    imp.setFantasia(rs.getString("nome_fantasia"));
                    imp.setCnpj_cpf(rs.getString("cnpj"));
                    if(imp.getCnpj_cpf() == null) {
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
                    if(rs.getString("telefone") != null
                            && !"0".equals(rs.getString("telefone"))
                                && !"".equals(rs.getString("telefone"))) {
                        imp.setTel_principal(rs.getString("telefone"));
                    }
                    if(rs.getString("telefone2") != null
                            && !"0".equals(rs.getString("telefone2"))
                                && !"".equals(rs.getString("telefone2"))) {
                        imp.addContato("1", 
                                    "TELEFONE2", 
                                    (rs.getString("telefone2")), 
                                    null, 
                                    TipoContato.COMERCIAL,
                                    null);
                    }
                    if(rs.getString("contato") != null
                            && !"0".equals(rs.getString("contato"))
                                && !"".equals(rs.getString("contato"))) {
                        imp.addContato("2", 
                                    (rs.getString("contato")), 
                                    null, 
                                    null, 
                                    TipoContato.COMERCIAL,
                                    null);
                    }
                    if(rs.getString("celular") != null
                            && !"0".equals(rs.getString("celular"))
                                && !"".equals(rs.getString("celular"))) {
                        imp.addContato("3", 
                                    "CELULAR", 
                                    null, 
                                    (rs.getString("ddd_cel") + "" + rs.getString("celular")), 
                                    TipoContato.COMERCIAL,
                                    null);
                    }
                    if(rs.getString("site") != null
                                && !"".equals(rs.getString("site"))) {
                        imp.addContato("4", 
                                    rs.getString("site"), 
                                    null, 
                                    null, 
                                    TipoContato.COMERCIAL,
                                    null);
                    }
                    if(rs.getString("email") != null
                                && !"".equals(rs.getString("email"))) {
                        imp.addContato("4", 
                                    "EMAIL", 
                                    null, 
                                    null, 
                                    TipoContato.COMERCIAL,
                                    rs.getString("email"));
                    }
                    if(rs.getString("observacoes") != null && !"".equals(rs.getString("observacoes"))) {
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
        try(Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "    pf.id,\n" +
                    "    f.i_numero id_fornecedor,\n" +
                    "    p.codigo_interno id_produto,\n" +
                    "    case when s_sequencial = '' then p.codigo_interno\n" +
                    "    else s_sequencial end as codexterno,\n" +
                    "    i_embalagem embalagem,\n" +
                    "    s_unidade_medida unidade,\n" +
                    "    pf.d_data_compra dataalteracao        \n" +
                    "from\n" +
                    "    codigo_ref_fornecedor pf\n" +
                    "join produtos p on pf.id_produto = p.id\n" +
                    "join fornecedores f on pf.id_fornecedor = f.id\n" +
                    "order by\n" +
                    "    pf.id_fornecedor, p.codigo_interno"
            )) {
                while(rs.next()) {
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
        try(Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "    id,\n" +
                    "    numero,\n" +
                    "    ativo,\n" +
                    "    nome,\n" +
                    "    endereco,\n" +
                    "    complemento,\n" +
                    "    bairro,\n" +
                    "    cidade,\n" +
                    "    uf,\n" +
                    "    cep,\n" +
                    "    tipo_pessoa,\n" +
                    "    cpf,\n" +
                    "    cnpj,\n" +
                    "    insc_estadual,\n" +
                    "    rg,\n" +
                    "    ddd,\n" +
                    "    telefone,\n" +
                    "    fax, \n" +
                    "    celular,\n" +
                    "    site,\n" +
                    "    data_cadastro,\n" +
                    "    data_aniversario,\n" +
                    "    limite_credito, \n" +
                    "    email,\n" +
                    "    s_codigo_municipio,\n" +
                    "    end_num,\n" +
                    "    comentarios\n" +
                    "from \n" +
                    "    clientes\n" +
                    "order by\n" +
                    "    id "
            )) {
                while(rs.next()) {
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
                    if(imp.getCnpj() == null && "".equals(imp.getCnpj())) {
                        imp.setCnpj(rs.getString("cpf"));
                    }
                    imp.setInscricaoestadual(rs.getString("insc_estadual"));
                    if(imp.getInscricaoestadual() == null && "".equals(imp.getInscricaoestadual())) {
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
    
    
    /*public List<Estabelecimento> getLojaClienteSQLite() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        ConexaoSQLite conSQLite = new ConexaoSQLite();
        try(Statement stm = conSQLite.get().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "    1 id,\n" +
                    "    'LOJA - SQL LITE' razaosocial"
            )) {
                while(rs.next()) {
                    result.add(new Estabelecimento(rs.getString("id"), rs.getString("razaosocial")));
                }
            }
        }
        return result;
    }*/
    
    
/*    public Date getVendasDataInicio() {
        return vendasDataInicio;
    }

    public void setVendasDataInicio(Date vendasDataInicio) {
        this.vendasDataInicio = vendasDataInicio;
    }

    public void setVendasDataTermino(Date vendasDataTermino) {
        this.vendasDataTermino = vendasDataTermino;
    }

    public Date getVendasDataTermino() {
        return vendasDataTermino;
    }
    
    @Override
    public Iterator<VendaIMP> getVendaIterator() throws Exception {
        return new VendaIterator(getLojaOrigem(), getVendasDataInicio(), getVendasDataTermino());
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        return new VendaItemIterator(getLojaOrigem(), getVendasDataInicio(), getVendasDataTermino());
    }
    
    private static class VendaIterator implements Iterator<VendaIMP> {

        private Statement stm;
        private ResultSet rst;
        private VendaIMP next;

        public VendaIterator(String idLojaCliente, Date dataInicio, Date dataTermino) {
            try {
                ConexaoSQLite sqLite = new ConexaoSQLite();
                this.stm = sqLite.get().createStatement();
                this.rst = stm.executeQuery(
                        "select	\n" +
                        "	v.id,\n" +
                        "	v.sCCF_CVC_CBP ccf,\n" +
                        "	ecf.sECF ecf,\n" +
                        "	v.sCOO coo,\n" +
                        "	v.sCPF_CNPJ_ADQUIRENTE cnpj,\n" +
                        "	v.sNOME_ADQUIRENTE razao,\n" +
                        "	v.sDataINICIO_EMISSAO data,\n" +
                        "	v.sHORA horaemissao,\n" +
                        "	v.rSUB_TOTAL_DOCUMENTO subtotalimpressora\n" +
                        "from	\n" +
                        "	r04 v\n" +
                        "join r01 ecf on v.id_r01 = ecf.id\n" +
                        "where	\n" +
                        "	substr(sDataINICIO_EMISSAO,7)||substr(sDataINICIO_EMISSAO,4,2)||substr(sDataINICIO_EMISSAO,1,2) between '" + 
                                DATE_FORMAT.format(dataInicio) + "' and '" + DATE_FORMAT.format(dataTermino) + "'"
                );
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Erro ao obter a venda", ex);
                throw new RuntimeException(ex);
            }
        }

        @Override
        public boolean hasNext() {
            processarNext();
            return next != null;
        }

        @Override
        public VendaIMP next() {
            processarNext();
            VendaIMP result = next;
            next = null;
            return result;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported.");
        }

        private void processarNext() {
            try {
                if (next == null) {
                    if (rst.next()) {
                        next = new VendaIMP();

                        next.setId(rst.getString("id"));
                        next.setNumeroCupom(rst.getInt("coo"));
                        next.setEcf(Integer.valueOf(Utils.formataNumero(rst.getString("ecf"))));
                        next.setData(new SimpleDateFormat("dd/MM/yy").parse(rst.getString("data")));
                        //next.setIdClientePreferencial(rst.getString("id_cliente"));
                        next.setHoraInicio(new SimpleDateFormat("hh:mm:ss").parse(rst.getString("horaemissao")));
                        next.setHoraTermino(new SimpleDateFormat("hh:mm:ss").parse(rst.getString("horaemissao")));
                        next.setSubTotalImpressora(rst.getDouble("subtotalimpressora"));
                    }
                }
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Erro ao obter a venda", ex);
                throw new RuntimeException(ex);
            }
        }

    }
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyMMdd");

    private static class VendaItemIterator implements Iterator<VendaItemIMP> {

        private Statement stm;
        private ResultSet rst;
        private VendaItemIMP next;
        private String loja = "";

        public VendaItemIterator(String idLojaCliente, Date dataInicio, Date dataTermino) {
            try {
                loja = idLojaCliente;
                ConexaoSQLite sqLite = new ConexaoSQLite();
                stm = sqLite.get().createStatement();
                rst = stm.executeQuery(
                        "select	\n" +
                        "	i.id,\n" +
                        "       v.id id_venda,\n" +
                        "	i.scoo coo,\n" +
                        "	i.sccf_cvc_cbp ccf,\n" +
                        "	i.snum_item sequencia,\n" +
                        "	i.scodigo_produto id_produto,\n" +
                        "	i.sdescricao descricao,\n" +
                        "	i.sunidade unidade,\n" +
                        "	i.rquantidade qtd,\n" +
                        "	i.rvalor_unitario valorvenda,\n" +
                        "	i.rvalor_total_liquido subtotalimpressora,\n" +
                        "	i.sst cst,\n" +
                        "	i.rtaxa_aliquota icms,\n" +
                        "	i.sindicador_cancelamento cancelado\n" +
                        "from	\n" +
                        "	r05 i\n" +
                        "join r04 v on i.scoo = v.scoo\n" +
                        "where	\n" +
                        "	substr(sDataINICIO_EMISSAO,7)||substr(sDataINICIO_EMISSAO,4,2)||substr(sDataINICIO_EMISSAO,1,2) between '" + 
                                DATE_FORMAT.format(dataInicio) + "' and '" + DATE_FORMAT.format(dataTermino) + "'"
                );

            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Erro ao obter a venda", ex);
                throw new RuntimeException(ex);
            }
        }

        @Override
        public boolean hasNext() {
            processarNext();
            return next != null;
        }

        @Override
        public VendaItemIMP next() {
            processarNext();
            VendaItemIMP result = next;
            next = null;
            return result;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported.");
        }

        private void processarNext() {
            try {
                if (next == null) {
                    if (rst.next()) {
                        ProdutoAnteriorDAO antDAO = new ProdutoAnteriorDAO();
                        next = new VendaItemIMP();

                        next.setId(rst.getString("id"));
                        next.setVenda(rst.getString("id_venda"));
                        
                        int idProduto = antDAO.getCodigoAtualEANant("SOPHYX", loja, rst.getString("id_produto"));
                        
                        next.setProduto(String.valueOf(idProduto));
                        next.setDescricaoReduzida(rst.getString("descricao"));
                        next.setQuantidade(rst.getDouble("qtd"));
                        next.setTotalBruto(rst.getDouble("subtotalimpressora"));
                        next.setCancelado("S".equals(rst.getString("cancelado").trim()));
                        next.setCodigoBarras(rst.getString("id_produto"));
                        next.setUnidadeMedida(rst.getString("unidade"));
                        
                        if(rst.getString("cst") != null && !"".equals(rst.getString("cst"))) {
                            switch(rst.getString("cst").trim()) {
                            case "T": next.setIcmsAliq(rst.getDouble("icms"));
                                next.setIcmsCst(0);
                                next.setIcmsReduzido(0);
                                break;
                            case "F": next.setIcmsAliq(rst.getDouble("icms"));
                                next.setIcmsCst(60);
                                next.setIcmsReduzido(0);
                                break;
                            default: 
                                next.setIcmsAliq(rst.getDouble("icms"));
                                next.setIcmsCst(40);
                                next.setIcmsReduzido(0);
                                break;
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Erro ao obter a venda", ex);
                throw new RuntimeException(ex);
            }
        }
    }*/
}
