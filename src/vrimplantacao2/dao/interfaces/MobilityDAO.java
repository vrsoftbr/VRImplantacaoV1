package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ClienteIMP;
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

    @Override
    public String getSistema() {
        return "MOBILITY";
    }
    
    public List<Estabelecimento> getLojaCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try(Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select * from lojas"
            )) {
                while(rs.next()) {
                    result.add(new Estabelecimento("1", "CAMAROTTO BOX"));
                }
            }
        }
        return result;
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
                    "SELECT\n" +
                    "	ID,\n" +
                    "	S_DESCRICAO\n" +
                    "FROM\n" +
                    "	FAMILIAS\n" +
                    "ORDER BY\n" +
                    "	ID"
            )) {
                while(rs.next()) {
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
                    "   p.icms,\n" +
                    "   p.s_cod_cst_pis_entrada pis_entrada,\n" +
                    "   p.s_cod_cst_pis_saida pis_saida,\n" +
                    "   p.s_cod_cst_cofins_entrada cofins_entrada,\n" +
                    "   p.s_cod_cst_cofins_saida cofins_saida,\n" +
                    "   p.s_cest cest,\n" +
                    "   p.aliquota id_aliquotadebito,\n" +
                    "   p.s_cod_pis_saida naturezareceita\n" +        
                    "from\n" +
                    "    produtos p\n" +
                    "order by\n" +
                    "    p.id"
            )) {
                while(rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("codigo_interno"));
                    imp.setEan(rs.getString("ean"));
                    if(rs.getInt("pesado") != 0) {
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
                    imp.setIcmsDebitoId(rs.getString("id_aliquotadebito"));
                    
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
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        try(Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "    id,\n" +
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
                        imp.setTel_principal(rs.getString("ddd") + rs.getString("telefone"));
                    }
                    if(rs.getString("telefone2") != null
                            && !"0".equals(rs.getString("telefone2"))
                                && !"".equals(rs.getString("telefone2"))) {
                        imp.addContato("1", 
                                    "TELEFONE2", 
                                    (rs.getString("ddd2") + rs.getString("telefone2")), 
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
                                    (rs.getString("ddd_cel") + rs.getString("celular")), 
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
                    if(rs.getString("observacao") != null && !"".equals(rs.getString("observacao"))) {
                        imp.setObservacao(rs.getString("observacao"));
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
                    "    id_fornecedor,\n" +
                    "    p.codigo_interno id_produto,\n" +
                    "    case when s_sequencial = '' then p.codigo_interno\n" +
                    "    else s_sequencial end as codexterno,\n" +
                    "    i_embalagem embalagem,\n" +
                    "    s_unidade_medida unidade,\n" +
                    "    pf.d_data_compra dataalteracao\n" +        
                    "from\n" +
                    "    codigo_ref_fornecedor pf\n" +
                    "join produtos p on pf.id_produto = p.id\n" +
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
}
