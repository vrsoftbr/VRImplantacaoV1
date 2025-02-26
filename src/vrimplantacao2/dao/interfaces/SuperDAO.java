package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoEstadoCivil;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

public class SuperDAO extends InterfaceDAO {

    @Override
    public String getSistema() {
        return "Super";
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    cd_loja id,\n" +
                    "    cd_loja || ' - ' || nm_fantazia descricao\n" +
                    "from\n" +
                    "    loja\n" +
                    "order by\n" +
                    "    id"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("id"), rst.getString("descricao")));
                }
            }
        }
        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    s.cd_secao,\n" +
                    "    s.dsc_secao,\n" +
                    "    g.cd_grupo,\n" +
                    "    g.dsc_grupo,\n" +
                    "    sb.cd_subgrupo,\n" +
                    "    sb.dsc_subgrupo\n" +
                    "from\n" +
                    "    subgrupo sb\n" +
                    "    join grupo g on\n" +
                    "        sb.cd_grupo = g.cd_grupo\n" +
                    "    join secao s on\n" +
                    "        g.cd_secao = s.cd_secao\n" +
                    "order by\n" +
                    "    1,3,5"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setMerc1ID(rst.getString("cd_secao"));
                    imp.setMerc1Descricao(rst.getString("dsc_secao"));
                    imp.setMerc2ID(rst.getString("cd_grupo"));
                    imp.setMerc2Descricao(rst.getString("dsc_grupo"));
                    imp.setMerc3ID(rst.getString("cd_subgrupo"));
                    imp.setMerc3Descricao(rst.getString("dsc_subgrupo"));
                    
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
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    p.cd_prod id,\n" +
                    "    p.dt_cdto datacadastro,\n" +
                    "    ean.cd_brr ean,\n" +
                    "    ean.qtd_emb qtdembalagem,\n" +
                    "    coalesce(emb.sigla, emb.dsc_emb) tipoembalagem,\n" +
                    "    case when p.flag_balanca = 'S' then 1 else 0 end ebalanca,\n" +
                    "    coalesce(p.dia_validade,0) validade,\n" +
                    "    p.dsc_prod descricaocompleta,\n" +
                    "    p.dsc_reduz descricaoreduzida,\n" +
                    "    sg.cd_segmt,\n" +
                    "    sg.cd_depart,\n" +
                    "    sg.cd_secao,\n" +
                    "    sg.cd_grupo,\n" +
                    "    p.cd_sub_grupo,\n" +
                    "    p.peso,\n" +
                    "    pe.qtd_maxima estoquemaximo,\n" +
                    "    pe.qtd_minima estoqueminimo,\n" +
                    "    pe.qtd_estq estoque,\n" +
                    "    pe.mrg_lucr margem,\n" +
                    "    pe.custo_ult_cmp custocomimposto,\n" +
                    "    pe.custo_ult_cmp_icms custosemimposto,\n" +
                    "    pe.prc_venda precovenda,\n" +
                    "    case when p.flag_ativo = 'S' then 1 else 0 end situacaocadastro,\n" +
                    "    p.cd_ncm ncm,\n" +
                    "    p.cest cest,\n" +
                    "    piscofins.cst_saida_pis piscofinssaida,\n" +
                    "    piscofins.cst_entrada_pis piscofinsentrada,\n" +
                    "    p.nat_rec piscofinsnatrec,\n" +
                    "    trib.cst icmscst,\n" +
                    "    trib.aliquota icsmaliq,\n" +
                    "    coalesce(pe.preco_atac, 0) precoatacado,\n" +
                    "    coalesce(pe.qtd_atac, 0) qtdatacado,\n" +
                    "    (select cd_fornec from fornecedor where cpfcnpj = p.cnpjfab) fabricante\n" +
                    "from\n" +
                    "    produto p\n" +
                    "    join loja lj on lj.cd_loja = " + getLojaOrigem() + "\n" +
                    "    left join codigo_barra ean on\n" +
                    "        p.cd_prod = ean.cd_prod\n" +
                    "    join tipo_embalagem emb on\n" +
                    "        emb.cd_emb = p.cd_emb_venda\n" +
                    "    left join produto_empresa pe on\n" +
                    "        pe.cd_emp = lj.cd_emp\n" +
                    "        and pe.cd_loja = lj.cd_loja\n" +
                    "        and pe.cd_prod = p.cd_prod\n" +
                    "    join pis_cofins piscofins on\n" +
                    "        p.cd_piscofins = piscofins.cd_piscofins\n" +
                    "    left join tributacao_venda trib on\n" +
                    "        trib.cd_emp = lj.cd_emp\n" +
                    "        and trib.cd_loja = lj.cd_loja\n" +
                    "        and pe.cd_tribut = trib.cd_tribut\n" +
                    "    left join subgrupo sg on\n" +
                    "        p.cd_sub_grupo = sg.cd_subgrupo\n" +
                    "order by\n" +
                    "    p.cd_prod"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setEan(rst.getString("ean"));
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                    imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
                    imp.seteBalanca(rst.getBoolean("ebalanca"));
                    imp.setValidade(rst.getInt("validade"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoGondola(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setCodMercadologico1(rst.getString("cd_secao"));
                    imp.setCodMercadologico2(rst.getString("cd_grupo"));
                    imp.setCodMercadologico3(rst.getString("cd_sub_grupo"));
                    imp.setPesoBruto(rst.getDouble("peso"));
                    imp.setPesoLiquido(rst.getDouble("peso"));
                    imp.setEstoqueMaximo(rst.getDouble("estoquemaximo"));
                    imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setSituacaoCadastro(rst.getInt("situacaocadastro"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getString("piscofinssaida"));
                    imp.setPiscofinsCstCredito(rst.getString("piscofinsentrada"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("piscofinsnatrec"));
                    imp.setIcmsCst(rst.getInt("icmscst"));
                    imp.setIcmsAliq(rst.getDouble("icsmaliq"));
                    imp.setAtacadoPreco(rst.getDouble("precoatacado"));
                    imp.setFornecedorFabricante(rst.getString("fabricante"));
                    
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
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    f.cd_fornec id,\n" +
                    "    f.razao_social razao,\n" +
                    "    f.nom_fantasia fantasia,\n" +
                    "    f.cpfcnpj cnpj_cpf,\n" +
                    "    f.ins_estadual_rg ie_rg,\n" +
                    "    f.insc_suframa suframa,\n" +
                    "    f.insc_municipal,\n" +
                    "    case when f.status = 'A' then 1 else 0 end ativo,\n" +
                    "    f.endereco,\n" +
                    "    f.nro_logradouro numero,\n" +
                    "    f.end_compl complemento,\n" +
                    "    f.dsc_bairro bairro,\n" +
                    "    m.cd_ibge id_municipio,\n" +
                    "    m.dsc_municipio municipio,\n" +
                    "    m.uf,\n" +
                    "    f.cep,\n" +
                    "    f.tel_fornec,\n" +
                    "    f.tel_celular,\n" +
                    "    f.tel_fax,\n" +
                    "    f.tel_vendedor,\n" +
                    "    f.e_mail,\n" +
                    "    f.dt_cdto datacadastros,\n" +
                    "    f.obs observacoes,\n" +
                    "    f.dias_entrega prazoentrega,\n" +
                    "    f.dias_visita prazovisita,\n" +
                    "    coalesce(pp.primeira_parc, 0) condicaopagamento,\n" +
                    "    f.dsc_vendedor vendedor\n" +
                    "from\n" +
                    "    fornecedor f\n" +
                    "    left join municipio m on\n" +
                    "        f.cd_municipio = m.cd_municipio\n" +
                    "    left join fornecedor_prazo_pagamento pp on\n" +
                    "        f.cd_przpagto = pp.cd_przpagto\n" +
                    "order by\n" +
                    "    f.cd_fornec"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("cnpj_cpf"));
                    imp.setIe_rg(rst.getString("ie_rg"));
                    imp.setSuframa(rst.getString("suframa"));
                    imp.setInsc_municipal(rst.getString("insc_municipal"));
                    imp.setAtivo(rst.getBoolean("ativo"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setIbge_municipio(rst.getInt("id_municipio"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setTel_principal(rst.getString("tel_fornec"));                    
                    imp.addCelular("CELULAR", rst.getString("tel_celular"));                    
                    imp.addTelefone("FAX", rst.getString("tel_fax"));
                    imp.addContato(rst.getString("vendedor"), rst.getString("tel_vendedor"), "", TipoContato.COMERCIAL, "");
                    imp.addEmail("EMAIL", rst.getString("e_mail"), TipoContato.COMERCIAL);
                    imp.setDatacadastro(rst.getDate("datacadastros"));
                    imp.setObservacao(rst.getString("observacoes"));
                    imp.setPrazoEntrega(rst.getInt("prazoentrega"));
                    imp.setPrazoVisita(rst.getInt("prazovisita"));
                    imp.setCondicaoPagamento(rst.getInt("condicaopagamento"));
                    
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
                    "select\n" +
                    "    pf.cd_fornec idfornecedor,\n" +
                    "    pf.cd_prod idproduto,\n" +
                    "    nullif(trim(pf.cd_prod_for), '') codigoexterno,\n" +
                    "    coalesce(pf.qtd_emb, 1) qtdembalagem,\n" +
                    "    coalesce(nullif(pf.vlr_tab_for,0), pf.vlr_unit) custotabela\n" +
                    "from\n" +
                    "    fornecedor_produto pf\n" +
                    "order by\n" +
                    "    1,2,3"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setIdFornecedor(rst.getString("idfornecedor"));
                    imp.setIdProduto(rst.getString("idproduto"));
                    imp.setCodigoExterno(rst.getString("qtdembalagem"));
                    imp.setCustoTabela(rst.getDouble("custotabela"));
                    
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
                    "select\n" +
                    "    c.cd_cli id,\n" +
                    "    c.cgccpf cnpj,\n" +
                    "    c.i_estad inscricaoestadual,\n" +
                    "    c.nom_cli razao,\n" +
                    "    c.nom_fant fantasia,\n" +
                    "    case when c.status = 'A' then 1 else 0 end ativo,\n" +
                    "    c.endereco,\n" +
                    "    c.nro_logradouro numero,\n" +
                    "    c.end_compl complemento,\n" +
                    "    c.dsc_bairro bairro,\n" +
                    "    m.cd_ibge municipio_ibge,\n" +
                    "    c.dsc_municipio municipio,\n" +
                    "    m.uf,\n" +
                    "    c.cep,\n" +
                    "    c.est_civil,\n" +
                    "    c.dt_nasc datanascimento,\n" +
                    "    c.dt_cdto datacadastro,\n" +
                    "    c.loc_trab empresa,\n" +
                    "    c.tel_trab empresatelefone,\n" +
                    "    c.vlr_contrato valorlimite,\n" +
                    "    c.nom_conjuge conjuge,\n" +
                    "    c.obs observacoes,\n" +
                    "    c.tel telefone,\n" +
                    "    c.cel celular,\n" +
                    "    c.e_mail email,\n" +
                    "    c.fax fax,\n" +
                    "    c.i_munic inscricaomunicipal\n" +
                    "from\n" +
                    "    cliente c\n" +
                    "    left join municipio m on\n" +
                    "        c.cd_municipio = m.cd_municipio\n" +
                    "order by\n" +
                    "    c.cd_cli"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    
                    imp.setId(rst.getString("id"));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setInscricaoestadual(rst.getString("inscricaoestadual"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setAtivo(rst.getBoolean("ativo"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipioIBGE(rst.getInt("municipio_ibge"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    switch (Utils.acertarTexto(rst.getString("est_civil"))) {
                        case "S": imp.setEstadoCivil(TipoEstadoCivil.SOLTEIRO); break;
                        default: imp.setEstadoCivil(TipoEstadoCivil.OUTROS);
                    }
                    imp.setDataNascimento(rst.getDate("datanascimento"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setEmpresa(rst.getString("empresa"));
                    imp.setEmpresaTelefone(rst.getString("empresatelefone"));
                    imp.setValorLimite(rst.getDouble("valorlimite"));
                    imp.setNomeConjuge(rst.getString("conjuge"));
                    imp.setObservacao2(rst.getString("observacoes"));
                    imp.setTelefone(rst.getString("telefone"));
                    imp.setCelular(rst.getString("celular"));
                    imp.setEmail(rst.getString("email"));
                    imp.setFax(rst.getString("fax"));
                    imp.setInscricaoMunicipal(rst.getString("inscricaomunicipal"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }
    
    
}
