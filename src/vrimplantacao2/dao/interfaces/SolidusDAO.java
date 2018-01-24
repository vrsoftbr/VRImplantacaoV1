package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.gui.interfaces.custom.solidus.Entidade;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.vo.importacao.ChequeIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.OfertaIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;

/**
 *
 * @author Leandro
 */
public class SolidusDAO extends InterfaceDAO {
    
    private static final Logger LOG = Logger.getLogger(SolidusDAO.class.getName());
    
    private Date vendasDataInicio = null;
    private Date rotativoDtaInicio = null;
    private Date rotativoDtaFim = null;
    private Date contasDtaInicio = null;
    private Date contasDtaFim = null;
    private List<Entidade> entidadesCheques;
    private List<Entidade> entidadesCreditoRotativo;
    private List<Entidade> entidadesContas;

    public Date getVendasDataInicio() {
        return vendasDataInicio;
    }

    public void setVendasDataInicio(Date vendasDataInicio) {
        this.vendasDataInicio = vendasDataInicio;
    }

    public void setRotativoDtaInicio(Date rotativoDtaInicio) {
        this.rotativoDtaInicio = rotativoDtaInicio;
    }

    public void setRotativoDtaFim(Date rotativoDtaFim) {
        this.rotativoDtaFim = rotativoDtaFim;
    }

    public void setContasDtaInicio(Date contasDtaInicio) {
        this.contasDtaInicio = contasDtaInicio;
    }

    public void setContasDtaFim(Date contasDtaFim) {
        this.contasDtaFim = contasDtaFim;
    }

    public void setEntidadesContas(List<Entidade> entidadesContas) {
        this.entidadesContas = entidadesContas;
    }
    
    @Override
    public String getSistema() {
        return "Solidus";
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select l.cod_loja, l.cod_loja || ' - ' || l.des_fantasia descricao from tab_loja l order by 1"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("cod_loja"), rst.getString("descricao")));
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
                    "select cod_produto_similar, des_produto_similar from tab_produto_similar order by 1"
            )) {
                while (rst.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("cod_produto_similar"));
                    imp.setDescricao(rst.getString("des_produto_similar"));
                    
                    result.add(imp);                    
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
                    "    s.cod_secao merc1,\n" +
                    "    s.des_secao merc1_desc,\n" +
                    "    g.cod_grupo merc2,\n" +
                    "    g.des_grupo merc2_desc,\n" +
                    "    sg.cod_sub_grupo merc3,\n" +
                    "    sg.des_sub_grupo merc3_desc\n" +
                    "from\n" +
                    "    tab_secao s\n" +
                    "    left join tab_grupo g on g.cod_secao = s.cod_secao\n" +
                    "    left join tab_subgrupo sg on sg.cod_secao = g.cod_secao and sg.cod_grupo = g.cod_grupo\n" +
                    "order by\n" +
                    "    merc1, merc2, merc3"
            )) {
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
                    "    p.cod_produto id,\n" +
                    "    p.dta_cadastro datacadastro,\n" +
                    "    ean.ean,\n" +
                    "    p.qtd_embalagem_venda qtdembalagem,\n" +
                    "    p.qtd_embalagem_compra qtdembalagemcotacao,\n" +
                    "    p.des_unidade_venda unidade,\n" +
                    "    case p.flg_envia_balanca when 'S' then 1 else 0 end as ebalanca,\n" +
                    "    p.dias_validade validade,\n" +
                    "    p.des_produto decricaocompleta,\n" +
                    "    coalesce(p.des_reduzida, p.des_produto) descricaoreduzida,\n" +
                    "    (select first 1\n" +
                    "        cod_fornecedor\n" +
                    "    from\n" +
                    "        tab_produto_fornecedor\n" +
                    "    where\n" +
                    "        flg_preferencial = 'S'\n" +
                    "        and cod_produto = p.cod_produto\n" +
                    "        and cod_loja = loja.cod_loja) fabricante,\n" +
                    "    p.cod_secao,\n" +
                    "    p.cod_grupo, \n" +
                    "    p.cod_sub_grupo,\n" +
                    "    p.cod_produto_similar id_familia,\n" +
                    "    p.val_peso peso,\n" +
                    "    pl.qtd_est_atual estoque,\n" +
                    "    pl.qtd_est_minimo estoqueminimo,\n" +
                    "    pl.val_margem margem,\n" +
                    "    case when pl.val_custo_tabela = 0 then pl.val_custo_cheio else pl.val_custo_tabela end custosemimposto,\n" +
                    "    case when pl.val_custo_cheio = 0 then pl.val_custo_tabela else pl.val_custo_cheio end custocomimposto,\n" +
                    "    pl.val_venda precovenda,\n" +
                    "    case when pl.inativo = 'S' then 0 else 1 end as situacaocadastro,\n" +
                    "    ncmcest.ncm,\n" +
                    "    ncmcest.cest,\n" +
                    "    p.cst_pis_cof_entrada,\n" +
                    "    p.cst_pis_cof_saida,\n" +
                    "    p.cod_tab_sped natreceita,\n" +
                    "    trib.cod_sit_tributaria icms_saida_cst,\n" +
                    "    trib.val_icms icms_saida_aliq,\n" +
                    "    trib.val_reducao_base_calculo icms_saida_reducao,\n" +
                    "    tribent.cod_sit_tributaria icms_entrada_cst,\n" +
                    "    tribent.val_icms icms_entrada_aliq,\n" +
                    "    tribent.val_reducao_base_calculo icms_entrada_reducao\n" +
                    "from\n" +
                    "    tab_produto p\n" +
                    "    join tab_loja loja on loja.cod_loja = " + getLojaOrigem() + "\n" +
                    "    left join (\n" +
                    "        select\n" +
                    "            cod_produto,\n" +
                    "            cod_barra_principal ean\n" +
                    "        from\n" +
                    "            tab_produto\n" +
                    "        union\n" +
                    "        select\n" +
                    "            cod_produto,\n" +
                    "            cod_ean\n" +
                    "        from\n" +
                    "            tab_codigo_barra\n" +
                    "    ) ean on p.cod_produto = ean.cod_produto\n" +
                    "    left join tab_produto_loja pl on\n" +
                    "        p.cod_produto = pl.cod_produto and\n" +
                    "        pl.cod_loja = loja.cod_loja\n" +
                    "    left join (\n" +
                    "        select\n" +
                    "            pl.*,\n" +
                    "            ncm.num_ncm ncm,\n" +
                    "            cest.num_cest cest\n" +
                    "        from\n" +
                    "            (select\n" +
                    "                cod_produto,\n" +
                    "                min(COD_NCM) cod_ncm\n" +
                    "            from\n" +
                    "                tab_produto_loja\n" +
                    "            group by\n" +
                    "                cod_produto) pl\n" +
                    "            left join tab_ncm ncm on\n" +
                    "                pl.cod_ncm = ncm.cod_ncm\n" +
                    "            left join tab_cest cest on\n" +
                    "                ncm.cod_cest = cest.cod_cest\n" +
                    "    ) ncmcest on p.cod_produto = ncmcest.cod_produto\n" +
                    "    left join tab_tributacao trib on\n" +
                    "        pl.cod_tributacao = trib.cod_tributacao\n" +
                    "    left join tab_tributacao tribent on\n" +
                    "        pl.cod_trib_entrada = tribent.cod_tributacao\n" +
                    "order by\n" +
                    "    id"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setEan(rst.getString("ean"));
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                    imp.setQtdEmbalagemCotacao(rst.getInt("qtdembalagemcotacao"));
                    imp.setTipoEmbalagem(rst.getString("unidade"));
                    imp.seteBalanca(rst.getBoolean("ebalanca"));
                    imp.setValidade(rst.getInt("validade"));
                    imp.setDescricaoCompleta(rst.getString("decricaocompleta"));
                    imp.setDescricaoGondola(rst.getString("decricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setCodMercadologico1(rst.getString("cod_secao"));
                    imp.setCodMercadologico2(rst.getString("cod_grupo"));
                    imp.setCodMercadologico3(rst.getString("cod_sub_grupo"));
                    imp.setIdFamiliaProduto(rst.getString("id_familia"));
                    imp.setFornecedorFabricante(rst.getString("fabricante"));
                    imp.setPesoBruto(rst.getDouble("peso"));
                    imp.setPesoLiquido(rst.getDouble("peso"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setSituacaoCadastro(SituacaoCadastro.getById(rst.getInt("situacaocadastro")));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstCredito(rst.getInt("cst_pis_cof_entrada"));
                    imp.setPiscofinsCstDebito(rst.getInt("cst_pis_cof_saida"));
                    imp.setPiscofinsNaturezaReceita(Utils.stringToInt(rst.getString("natreceita")));
                    imp.setIcmsCstSaida(rst.getInt("icms_saida_cst"));
                    imp.setIcmsAliqSaida(rst.getDouble("icms_saida_aliq"));
                    imp.setIcmsReducaoSaida(rst.getDouble("icms_saida_reducao"));
                    imp.setIcmsCstEntrada(rst.getInt("icms_entrada_cst"));
                    imp.setIcmsAliqEntrada(rst.getDouble("icms_entrada_aliq"));
                    imp.setIcmsReducaoEntrada(rst.getDouble("icms_entrada_reducao"));
                    
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
                    "    f.cod_fornecedor id,\n" +
                    "    f.des_fornecedor razao,\n" +
                    "    f.des_fantasia fantasia,\n" +
                    "    f.num_cgc cnpj,\n" +
                    "    f.num_insc_est ie,\n" +
                    "    f.flg_simples,\n" +
                    "    case bloq.flg_bloqueado when 'S' then 0 else 1 end ativo,\n" +
                    "    f.des_endereco endereco,\n" +
                    "    f.num_endereco numero,\n" +
                    "    f.des_bairro bairro,\n" +
                    "    cd.des_cidade municipio,\n" +
                    "    cd.des_sigla uf,\n" +
                    "    f.num_cep cep,\n" +
                    "    f.num_fone tel_principal,\n" +
                    "    f.ped_min_val valor_minimo_pedido,\n" +
                    "    f.dta_cadastro datacadastro,\n" +
                    "    f.des_observacao observacao,\n" +
                    "    f.num_prazo prazoEntrega,\n" +
                    "    f.num_freq_visita prazovisita,\n" +
                    "    f.qtd_dia_carencia prazoseguranca,\n" +
                    "    f.des_contato,\n" +
                    "    f.des_email,\n" +
                    "    f.num_fax\n" +
                    "from\n" +
                    "    tab_fornecedor f\n" +
                    "    left join tab_cidade cd on\n" +
                    "        f.cod_cidade = cd.cod_cidade \n" +
                    "    left join tab_fornecedor_bloqueio bloq on\n" +
                    "        f.cod_fornecedor = bloq.cod_fornecedor\n" +
                    "order by\n" +
                    "    id"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    imp.setIe_rg(rst.getString("ie"));
                    imp.setAtivo(rst.getBoolean("ativo"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setTel_principal(rst.getString("tel_principal"));
                    imp.setValor_minimo_pedido(rst.getDouble("valor_minimo_pedido"));
                    imp.setDatacadastro(rst.getDate("datacadastro"));
                    imp.setObservacao(rst.getString("observacao"));
                    imp.setPrazoEntrega(rst.getInt("prazoEntrega"));
                    imp.setPrazoVisita(rst.getInt("prazovisita"));
                    imp.setPrazoSeguranca(rst.getInt("prazoseguranca"));
                    if (
                            !"".equals(Utils.acertarTexto(rst.getString("des_contato"))) ||
                            !"".equals(Utils.acertarTexto(rst.getString("tel_principal"))) ||
                            !"".equals(Utils.acertarTexto(rst.getString("des_email")))
                    ) {
                        imp.addContato("A", rst.getString("des_contato"), rst.getString("tel_principal"), "", TipoContato.COMERCIAL, rst.getString("des_email"));
                    }
                    String fax = Utils.formataNumero(rst.getString("num_fax"));
                    if (!"0".equals(fax)) {
                        imp.addContato("B", "FAX", fax, "", TipoContato.COMERCIAL, "");
                    }
                    
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
                    "select distinct\n" +
                    "    pl.cod_produto,\n" +
                    "    pl.dta_valida_oferta,\n" +
                    "    pl.val_oferta\n" +
                    "from\n" +
                    "    tab_produto_loja pl\n" +
                    "where\n" +
                    "    pl.cod_loja = " + getLojaOrigem() + "\n" +
                    "    and not pl.dta_valida_oferta is null\n" +
                    "order by\n" +
                    "    1"
            )) {
                while (rst.next()) {
                    OfertaIMP imp = new OfertaIMP();
                    
                    imp.setIdProduto(rst.getString("cod_produto"));
                    imp.setDataInicio(rst.getDate("dta_valida_oferta"));
                    imp.setDataFim(rst.getDate("dta_valida_oferta"));
                    imp.setPrecoOferta(rst.getDouble("val_oferta"));
                    
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
                    "    pf.cod_fornecedor,\n" +
                    "    pf.cod_produto,\n" +
                    "    pf.des_referencia,\n" +
                    "    max(pf.dta_alteracao) dta_alteracao,\n" +
                    "    max(pf.qtd_embalagem_compra) qtd_embalagem_compra,\n" +
                    "    max(pf.val_custo_embalagem) val_custo_embalagem\n" +
                    "from\n" +
                    "    tab_produto_fornecedor pf\n" +
                    "group by\n" +
                    "    pf.cod_fornecedor,\n" +
                    "    pf.cod_produto,\n" +
                    "    pf.des_referencia,\n" +
                    "    pf.val_custo_embalagem\n" +
                    "order by 1,2,3"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setIdFornecedor(rst.getString("cod_fornecedor"));
                    imp.setIdProduto(rst.getString("cod_produto"));
                    imp.setCodigoExterno(rst.getString("des_referencia"));
                    imp.setDataAlteracao(rst.getDate("dta_alteracao"));
                    imp.setQtdEmbalagem(rst.getInt("qtd_embalagem_compra"));
                    imp.setCustoTabela(rst.getDouble("val_custo_embalagem"));
                    
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
                    "    c.cod_cliente id,\n" +
                    "    c.num_cgc cnpj,\n" +
                    "    coalesce(nullif(trim(c.num_insc_est), ''), c.num_rg) inscricaoestadual,\n" +
                    "    c.des_cliente razao,\n" +
                    "    coalesce(nullif(trim(c.des_fantasia), ''), c.des_cliente) fantasia,\n" +
                    "    c.cod_status_pdv ativobloq,\n" +
                    "    c.des_endereco endereco,\n" +
                    "    c.num_endereco numero,\n" +
                    "    c.des_complemento complemento,\n" +
                    "    c.des_bairro bairro,\n" +
                    "    cd.des_cidade municipio,\n" +
                    "    cd.des_sigla estado,\n" +
                    "    c.num_cep cep,\n" +
                    "    c.flg_est_civil,\n" +
                    "    c.dta_nascimento datanascimento,\n" +
                    "    c.dta_cadastro datacadastro,\n" +
                    "    case c.flg_sexo when 1 then 0 else 1 end sexo,\n" +
                    "    c.des_empresa_trab empresa,\n" +
                    "    c.dta_admissao_trab dataadmissao,\n" +
                    "    c.des_cargo cargo,\n" +
                    "    c.val_renda salario,\n" +
                    "    c.val_limite_credito,\n" +
                    "    c.des_conjuge nomeconjuge,\n" +
                    "    c.des_pai nomepai,\n" +
                    "    c.des_mae nomemae,\n" +
                    "    c.des_observacao observacao2,\n" +
                    "    c.num_dia_fecha diavencimento,\n" +
                    "    c.num_fone,\n" +
                    "    c.num_fax,\n" +
                    "    c.num_celular,\n" +
                    "    c.des_email\n" +
                    "from\n" +
                    "    tab_cliente c\n" +
                    "    left join tab_cidade cd on\n" +
                    "        c.cod_cidade = cd.cod_cidade\n" +
                    "order by\n" +
                    "    1"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    
                    imp.setId(rst.getString("id"));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setInscricaoestadual(rst.getString("inscricaoestadual"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    setSituacaoCadastro(imp, rst.getInt("ativobloq"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setUf(rst.getString("estado"));
                    imp.setCep(rst.getString("cep"));
                    //imp.set(rst.getString("flg_est_civil"));
                    imp.setDataNascimento(rst.getDate("datanascimento"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setSexo(rst.getInt("sexo") == 0 ? TipoSexo.FEMININO : TipoSexo.MASCULINO);
                    imp.setEmpresa(rst.getString("empresa"));
                    imp.setDataAdmissao(rst.getDate("dataadmissao"));
                    imp.setCargo(rst.getString("cargo"));
                    imp.setSalario(rst.getDouble("salario"));
                    imp.setValorLimite(rst.getDouble("val_limite_credito"));
                    imp.setNomeConjuge(rst.getString("nomeconjuge"));
                    imp.setNomePai(rst.getString("nomepai"));
                    imp.setNomeMae(rst.getString("nomemae"));
                    imp.setObservacao2(rst.getString("observacao2"));
                    imp.setDiaVencimento(rst.getInt("diavencimento"));
                    imp.setTelefone(rst.getString("num_fone"));
                    imp.setFax(rst.getString("num_fax"));
                    imp.setCelular(rst.getString("num_celular"));
                    imp.setEmail(rst.getString("des_email"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    private void setSituacaoCadastro(ClienteIMP imp, int cod) {
        switch (cod) {
            case 1: 
                imp.setAtivo(true);
                imp.setBloqueado(true);
                break;
            case 2: 
                imp.setAtivo(true);
                imp.setBloqueado(true);
                break;
            case 3: 
                imp.setAtivo(false);
                imp.setBloqueado(false);
                break;
            case 4: 
                imp.setAtivo(true);
                imp.setBloqueado(true);
                break;
            case 5: 
                imp.setAtivo(true);
                imp.setBloqueado(true);
                break;
            case 6: 
                imp.setAtivo(true);
                imp.setBloqueado(false);
                break;
            case 7: 
                imp.setAtivo(false);
                imp.setBloqueado(false);
                break;
            default: 
                imp.setAtivo(true);
                imp.setBloqueado(false);
                break;
        }
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    f.cod_loja || '-' || f.tipo_conta || '-' || f.tipo_parceiro || '-' || f.cod_parceiro || '-' || f.num_registro id,\n" +
                    "    f.cod_entidade || ' - ' || e.des_entidade pagamento,\n" +
                    "    f.num_cgc_cpf cpf,\n" +
                    "    c.num_cgc cpf2,\n" +
                    "    f.dta_emissao dataemissao,\n" +
                    "    f.num_cupom_fiscal numerocupom,\n" +
                    "    f.num_pdv pdv,\n" +
                    "    f.val_parcela valor,\n" +
                    "    f.des_cc,\n" +
                    "    f.cod_parceiro idcliente,\n" +
                    "    f.dta_vencimento datavencimento,\n" +
                    "    f.num_parcela parcela,\n" +
                    "    f.val_juros juros,\n" +
                    "    f.num_cgc_cpf cpf,\n" +
                    "    f.dta_pgto,\n" +
                    "    f.val_desconto,\n" +
                    "    f.val_juros,\n" +
                    "    f.flg_quitado\n" +
                    "from\n" +
                    "    tab_fluxo f\n" +
                    "    left join tab_entidade e on f.cod_entidade = e.cod_entidade\n" +
                    "    left join tab_cliente c on f.cod_parceiro = c.cod_cliente\n" +
                    "where\n" +
                    "    f.dta_emissao >= '" + DATE_FORMAT.format(rotativoDtaInicio) + "'\n" +
                    "    and f.dta_emissao <= '" + DATE_FORMAT.format(rotativoDtaFim) + "'\n" +
                    "    and f.tipo_parceiro = 0\n" +
                    "    and f.cod_parceiro = 1017030\n" +
                    "    and f.cod_entidade in (" + implodeList(this.entidadesCreditoRotativo) + ")\n" +
                    "    and f.cod_loja = " + getLojaOrigem() + "\n" +                            
                    "order by\n" +
                    "    f.dta_emissao, id"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    
                    imp.setId(rst.getString("id"));   
                    imp.setDataEmissao(rst.getDate("dataemissao"));
                    imp.setNumeroCupom(rst.getString("numerocupom"));
                    imp.setEcf(rst.getString("pdv"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setObservacao(rst.getString("des_cc"));
                    imp.setIdCliente(rst.getString("idcliente"));
                    imp.setDataVencimento(rst.getDate("datavencimento"));
                    imp.setParcela(rst.getInt("parcela"));
                    imp.setJuros(rst.getDouble("juros"));
                    imp.setCnpjCliente(rst.getString("cpf"));
                    if ("S".equals(rst.getString("flg_quitado"))) {
                        imp.addPagamento(
                                rst.getString("id"),
                                imp.getValor(), 
                                rst.getDouble("val_desconto"), 
                                rst.getDouble("val_juros"),
                                rst.getDate("dta_pgto"),
                                ""
                        );
                    }
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<ChequeIMP> getCheques() throws Exception {
        List<ChequeIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    f.cod_loja || '-' || f.tipo_conta || '-' || f.tipo_parceiro || '-' || f.cod_parceiro || '-' || f.num_registro id,\n" +
                    "    f.cod_entidade || ' - ' || e.des_entidade pagamento,\n" +
                    "    f.num_cgc_cpf cpf,\n" +
                    "    c.num_cgc cpf2,\n" +
                    "    f.dta_emissao dataemissao,\n" +
                    "    f.num_cupom_fiscal numerocupom,\n" +
                    "    f.num_pdv pdv,\n" +
                    "    f.val_parcela valor,\n" +
                    "    c.num_rg rg,\n" +
                    "    c.num_fone fone,\n" +
                    "    c.des_cliente nome,\n" +
                    "    f.des_observacao\n" +
                    "from\n" +
                    "    tab_fluxo f\n" +
                    "    left join tab_entidade e on f.cod_entidade = e.cod_entidade\n" +
                    "    left join tab_cliente c on f.cod_parceiro = c.cod_cliente\n" +
                    "where\n" +
                    "    f.flg_quitado = 'N'\n" +
                    "    and f.tipo_parceiro = 0\n" +
                    "    and f.cod_entidade in (" + implodeList(entidadesCheques) + ")\n" +
                    "    and f.cod_loja = " + getLojaOrigem() + "\n" +
                    "order by\n" +
                    "    f.dta_emissao, id"
            )) {
                while (rst.next()) {
                    ChequeIMP imp = new ChequeIMP();
                    
                    imp.setId(rst.getString("id"));
                    imp.setCpf(rst.getString("cpf"));
                    imp.setDate(rst.getDate("dataemissao"));
                    imp.setNumeroCupom(rst.getString("numerocupom"));
                    imp.setEcf(rst.getString("pdv"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setRg(rst.getString("rg"));
                    imp.setTelefone(rst.getString("fone"));
                    imp.setNome(rst.getString("nome"));
                    imp.setObservacao(rst.getString("pagamento") + " - " + rst.getString("des_observacao"));
                    
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
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    f.cod_loja || '-' || f.tipo_conta || '-' || f.tipo_parceiro || '-' || f.cod_parceiro || '-' || f.num_registro id,\n" +
                    "    f.cod_parceiro fornecedor,\n" +
                    "    f.cod_entidade || ' - ' || e.des_entidade pagamento,\n" +
                    "    f.num_docto,\n" +
                    "    f.dta_emissao dataemissao,\n" +
                    "    f.dta_vencimento datavencimento,\n" +
                    "    f.dta_alteracao dataalteracao,\n" +
                    "    f.val_parcela valor,\n" +
                    "    f.des_cc,\n" +
                    "    f.num_parcela parcela,\n" +
                    "    f.val_juros juros,\n" +
                    "    f.num_cgc_cpf cpf\n" +
                    "from\n" +
                    "    tab_fluxo_referencia f\n" +
                    "    left join tab_entidade e on f.cod_entidade = e.cod_entidade\n" +
                    "where\n" +
                    "    f.flg_quitado = 'N' and\n" +
                    "    f.tipo_parceiro = 1 and\n" +
                    "    f.cod_entidade in (" + implodeList(entidadesContas) + ") and\n" +
                    "    f.cod_loja = " + getLojaOrigem() + "\n" +
                    "order by\n" +
                    "    f.dta_emissao"
            )) {
                while (rst.next()) {
                    ContaPagarIMP imp = new ContaPagarIMP();
                    
                    imp.setId(rst.getString("id"));
                    imp.setIdFornecedor(rst.getString("fornecedor"));
                    imp.setNumeroDocumento(rst.getString("num_docto"));
                    imp.setDataEmissao(rst.getDate("dataemissao"));
                    imp.setDataEntrada(rst.getDate("dataemissao"));
                    imp.setDataHoraAlteracao(rst.getTimestamp("dataalteracao"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setObservacao(rst.getString("pagamento") + " - " + rst.getString("des_cc"));
                    imp.addVencimento(rst.getDate("datavencimento"), rst.getDouble("valor"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public Iterator<VendaIMP> getVendaIterator() throws Exception {
        return new VendaIterator(getLojaOrigem(), getVendasDataInicio());
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        return new VendaItemIterator(getLojaOrigem(), getVendasDataInicio());
    }

    public List<Entidade> getEntidades() throws SQLException {
        List<Entidade> result = new ArrayList<>();
        
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select cod_entidade, des_entidade from tab_entidade order by cod_entidade"
            )) {
                while (rst.next()) {
                    result.add(new Entidade(rst.getInt("cod_entidade"), rst.getString("des_entidade")));
                }
            }
        }
        
        return result;
    }

    public void setEntidadesCheques(List<Entidade> entidadesCheques) {
        this.entidadesCheques = entidadesCheques;
    }

    public void setEntidadesCreditoRotativo(List<Entidade> entidadesCreditoRotativo) {
        this.entidadesCreditoRotativo = entidadesCreditoRotativo;
    }

    private String implodeList(List<Entidade> entidades) {
        StringBuilder builder = new StringBuilder();
        
        for (Iterator<Entidade> iterator = entidades.iterator(); iterator.hasNext(); ) {
            builder.append(iterator.next().getId());            
            if (iterator.hasNext()) {
                builder.append(",");
            }
        }        
        
        return builder.toString();
    }
    
    private static class VendaIterator implements Iterator<VendaIMP> {
        
        private Statement stm;
        private ResultSet rst;
        private VendaIMP next;

        public VendaIterator(String idLojaCliente, Date dataInicio) {
            try {
                this.stm = ConexaoFirebird.getConexao().createStatement();
                this.rst = stm.executeQuery("select\n" +
                        "    v.num_ident id,\n" +
                        "    v.num_cupom_fiscal cupomfiscal,\n" +
                        "    v.num_pdv ecf,\n" +
                        "    cast(v.dta_saida as date) data,\n" +
                        "    v.cod_cliente id_cliente,\n" +
                        "    min(v.dta_saida) horaInicio,\n" +
                        "    max(v.dta_saida) horaTermino,\n" +
                        "    min(case when v.flg_cupom_cancelado = 'N' then 0 else 1 end) cancelado,\n" +
                        "    sum(v.val_total_produto) subtotalimpressora,\n" +
                        "    sum(v.val_desconto) desconto,\n" +
                        "    sum(v.val_acrescimo) acrescimo,\n" +
                        "    pdv.num_serie_fabr numeroserie,\n" +
                        "    pdv.des_modelo modeloimpressora,\n" +
                        "    c.num_cgc cpf,\n" +
                        "    c.des_cliente nomecliente\n" +
                        "from\n" +
                        "    tab_produto_pdv v\n" +
                        "    left join tab_pdv pdv on v.num_pdv = pdv.cod_pdvint and v.cod_loja = pdv.cod_loja\n" +
                        "    left join tab_cliente c on v.cod_cliente = c.cod_cliente\n" +
                        "where\n" +
                        "    v.cod_loja = " + idLojaCliente + "\n" +
                        "    and cast(v.dta_saida as date) >= '" + DATE_FORMAT.format(dataInicio) + "'\n" +
                        "    and v.num_ident != 0\n" +
                        "group by\n" +
                        "    v.num_ident,\n" +
                        "    v.num_cupom_fiscal,\n" +
                        "    v.num_pdv,\n" +
                        "    cast(v.dta_saida as date),\n" +
                        "    v.cod_cliente,\n" +
                        "    pdv.num_serie_fabr,\n" +
                        "    pdv.des_modelo,\n" +
                        "    c.num_cgc,\n" +
                        "    c.des_cliente"
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
                        
                        next.setId(rst.getInt("ecf") + "-" + rst.getString("id"));
                        next.setNumeroCupom(rst.getInt("cupomfiscal"));
                        next.setEcf(rst.getInt("ecf"));
                        next.setData(rst.getDate("data"));
                        next.setIdClientePreferencial(rst.getString("id_cliente"));
                        next.setHoraInicio(rst.getTimestamp("horaInicio"));
                        next.setHoraTermino(rst.getTimestamp("horaTermino"));
                        next.setCancelado(rst.getBoolean("cancelado"));
                        next.setSubTotalImpressora(rst.getDouble("subtotalimpressora"));
                        next.setValorDesconto(rst.getDouble("desconto"));
                        next.setValorAcrescimo(rst.getDouble("acrescimo"));
                        next.setNumeroSerie(rst.getString("numeroserie"));
                        next.setModeloImpressora(rst.getString("modeloimpressora"));
                        next.setCpf(rst.getString("cpf"));
                        next.setNomeCliente(rst.getString("nomecliente"));
                    }
                }
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Erro ao obter a venda", ex);
                throw new RuntimeException(ex);
            }
        }
        
    }
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");
    
    private static class Tributacao {
        
        int cst;
        double aliq;
        double reducao;

        public Tributacao(int cst, double aliq, double reducao) {
            this.cst = cst;
            this.aliq = aliq;
            this.reducao = reducao;
        }
        
    }
    
    private static class VendaItemIterator implements Iterator<VendaItemIMP> {
        
        private Statement stm;
        private ResultSet rst;
        private VendaItemIMP next;
        private Map<Integer, Tributacao> tributacao = new HashMap<>();

        public VendaItemIterator(String idLojaCliente, Date dataInicio) {
            try {                
                try (Statement st = ConexaoFirebird.getConexao().createStatement()) {
                    try (ResultSet rs = st.executeQuery(
                            "select\n" +
                            "    t.cod_tributacao,\n" +
                            "    cast(t.cod_sit_tributaria as integer) cst,\n" +
                            "    t.val_icms aliq,\n" +
                            "    t.val_reducao_base_calculo reducao\n" +
                            "from\n" +
                            "    tab_tributacao t\n" +
                            "order by\n" +
                            "    1"
                    )) {
                        while (rs.next()) {
                            tributacao.put(
                                    rs.getInt("cod_tributacao"), 
                                    new Tributacao(
                                            rs.getInt("cst"), 
                                            rs.getDouble("aliq"), 
                                            rs.getDouble("reducao")
                                    )
                            );
                        }
                    }
                }
                
                stm = ConexaoFirebird.getConexao().createStatement();
                rst = stm.executeQuery("select\n" +
                        "    v.num_registro id,\n" +
                        "    v.num_pdv ecf,\n" +
                        "    v.num_ident idvenda,\n" +
                        "    v.cod_produto idproduto,\n" +
                        "    p.des_reduzida descricaoreduzida,\n" +
                        "    v.qtd_total_produto quantidade,\n" +
                        "    v.val_preco_venda precovenda,\n" +
                        "    case when v.flg_cupom_cancelado = 'N' then 0 else 1 end cancelado,\n" +
                        "    v.val_desconto desconto,\n" +
                        "    v.val_acrescimo acrescimo,\n" +
                        "    v.cod_ean ean,\n" +
                        "    p.des_unidade_venda unidade,\n" +
                        "    v.cod_tributacao\n" +
                        "from\n" +
                        "    tab_produto_pdv v\n" +
                        "    join tab_produto p on v.cod_produto = p.cod_produto\n" +
                        "where\n" +
                        "    v.cod_loja = " + idLojaCliente + "\n" +
                        "    and cast(v.dta_saida as date) >= '" + DATE_FORMAT.format(dataInicio) + "'\n" +
                        "    and v.num_ident != 0\n" +
                        "order by\n" +
                        "    id"
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
                        next = new VendaItemIMP();
                        
                        next.setId(rst.getString("id"));
                        next.setVenda(rst.getInt("ecf") + "-" + rst.getString("idvenda"));
                        next.setProduto(rst.getString("idproduto"));
                        next.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                        next.setQuantidade(rst.getDouble("quantidade"));
                        next.setPrecoVenda(rst.getDouble("precovenda"));
                        next.setCancelado(rst.getBoolean("cancelado"));
                        next.setValorDesconto(rst.getDouble("desconto"));
                        next.setValorAcrescimo(rst.getDouble("acrescimo"));
                        next.setCodigoBarras(rst.getString("ean"));
                        next.setUnidadeMedida(rst.getString("unidade"));
                        Tributacao trib = this.tributacao.get(rst.getInt("cod_tributacao"));
                        if (trib != null) {
                            next.setIcmsCst(trib.cst);
                            next.setIcmsAliq(trib.aliq);
                            next.setIcmsReduzido(trib.reducao);
                        }
                    }
                }
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Erro ao obter a venda", ex);
                throw new RuntimeException(ex);
            }
        }
        
    }
    
}
