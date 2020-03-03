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
import java.util.Set;
import java.util.logging.Logger;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Lucas
 */
public class RKSoftwareDAO extends InterfaceDAO implements MapaTributoProvider {

    private static final Logger LOG = Logger.getLogger(WeberDAO.class.getName());

    @Override
    public String getSistema() {
        return "RK Software";
    }

    private String Encoding = "WIN1252";

    public void setEncoding(String Encoding) {
        this.Encoding = Encoding == null ? "WIN1252" : Encoding;
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                new OpcaoProduto[]{
                    OpcaoProduto.MERCADOLOGICO_PRODUTO,
                    OpcaoProduto.MERCADOLOGICO,
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
                    OpcaoProduto.MAPA_TRIBUTACAO
                }
        ));
    }

    public List<Estabelecimento> getLojaCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select fl_codigo as id, (fl_fantasia||' - '||fl_cgc) as fantasia from filiais order by fl_codigo"
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
                    + "    t.tr_codigo as id,\n"
                    + "    t.tr_nome as descricao,\n"
                    + "    tr.dt_tributo as situacaotributaria,\n"
                    + "    tr.dt_codstrib as csticms,\n"
                    + "    coalesce(tr.dt_percicm, 0) as aliqicms,\n"
                    + "    coalesce(tr.dt_reduicm, 0) as reducaoicms,\n"
                    + "    coalesce(tr.dt_cod_beneficio_fiscal, '') as codigobeneficio\n"
                    + "from tributacao t\n"
                    + "inner join detltrib tr on tr.dt_tributacao = t.tr_codigo\n"
                    + "order by t.tr_codigo"
            )) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(
                            rs.getString("id"),
                            rs.getString("descricao")
                            + rs.getString("csticms") + " "
                            + rs.getString("aliqicms") + " "
                            + rs.getString("reducaoicms")));
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
                    "select distinct\n"
                    + "    m1.gr_codigo as merc1,\n"
                    + "    m1.gr_nome as desc_merc1,\n"
                    + "    m2.sg_codigo as merc2,\n"
                    + "    m2.sg_nome as desc_merc2\n"
                    + "from produtos p\n"
                    + "inner join grupos m1 on m1.gr_codigo = p.pr_grupo\n"
                    + "inner join subgrupos m2 on m2.sg_codigo = p.pr_subgrupo\n"
                    + "order by 1, 3"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rst.getString("merc1"));
                    imp.setMerc1Descricao(rst.getString("desc_merc1"));
                    imp.setMerc2ID(rst.getString("merc2"));
                    imp.setMerc2Descricao(rst.getString("desc_merc2"));
                    imp.setMerc3ID("1");
                    imp.setMerc3Descricao(imp.getMerc2Descricao());
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
                    "select\n"
                    + "    p.pr_codigo as id,\n"
                    + "    p.pr_codbarra as ean,\n"
                    + "    p.pr_balvalid as validade,\n"
                    + "    p.pr_nome as descricao,\n"
                    + "    p.pr_grupo as merc1,\n"
                    + "    p.pr_subgrupo as merc2,\n"
                    + "    p.pr_unidade as tipoembalagem,\n"
                    + "    p.pr_pbruto as pesobruto,\n"
                    + "    p.pr_pliquid as pesoliquido,\n"
                    + "    case p.pr_inativo when 'N' then 1 else 0 end ativo,\n"
                    + "    p.pr_ncm as ncm,\n"
                    + "    p.pr_codgia as gia,\n"
                    + "    p.pr_cest as cest,\n"
                    + "    p.pr_tribut as tributacao,\n"
                    + "    p.pr_precocust as custo,\n"
                    + "    p.pr_precovend as precovenda,\n"
                    + "    p.pr_percaplic as margem,\n"
                    + "    e.pf_minimo as estoqueminimo,\n"
                    + "    e.pf_maximo as estoquemaximo,\n"
                    + "    e.pf_atual as estoque\n"
                    + "    p.pr_dtcadastro as datacadastro,\n"
                    + "    p.pr_tribut as tributacao, \n"
                    + "    t.tr_codigo as id_tributacao,\n"
                    + "    t.tr_nome as descricaotributacao,\n"
                    + "    tr.dt_tributo as situacaotributaria,\n"
                    + "    tr.dt_codstrib as csticms,\n"
                    + "    coalesce(tr.dt_percicm, 0) as aliqicms,\n"
                    + "    coalesce(tr.dt_reduicm, 0) as reducaoicms,\n"
                    + "    coalesce(tr.dt_cod_beneficio_fiscal, '') as codigobeneficio\n"
                    + "from produtos p\n"
                    + "left join prodfilial e on e.pf_produto = p.pr_codigo\n"
                    + "     and e.pf_filial = " + getLojaOrigem() + "\n"
                    + "left join tributacao t on t.tr_codigo = p.pr_tribut\n"
                    + "inner join detltrib tr on tr.dt_tributacao = t.tr_codigo\n"
                    + "order by p.pr_codigo"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setEan(rst.getString("ean"));
                    imp.setValidade(rst.getInt("validade"));
                    imp.setDescricaoCompleta(rst.getString("descricao"));
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setCodMercadologico1(rst.getString("merc1"));
                    imp.setCodMercadologico2(rst.getString("merc2"));
                    imp.setCodMercadologico3("1");
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setPesoBruto(rst.getDouble("pesobruto"));
                    imp.setPesoLiquido(rst.getDouble("pesoliquido"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setCustoComImposto(rst.getDouble("custo"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
                    imp.setEstoqueMaximo(rst.getDouble("estoquemaximo"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCodigoGIA(rst.getString("gia"));
                    imp.setCest(rst.getString("cest"));
                    imp.setIcmsDebitoId(rst.getString("id_tributacao"));
                    imp.setIcmsCreditoId(rst.getString("id_tributacao"));
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
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    ean.pb_produto as id,\n"
                    + "    ean.pb_codbarra as ean,\n"
                    + "    ean.pb_quantidade as qtdembalagem\n"
                    + "from prodbarr ean"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setEan(rst.getString("ean"));
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

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    c.cl_codigo as id,\n"
                    + "    c.cl_filial as idloja,\n"
                    + "    c.cl_nome as razao,\n"
                    + "    c.cl_nomfan as fantasia,\n"
                    + "    c.cl_endereco as endereco,\n"
                    + "    c.cl_nro as numero,\n"
                    + "    c.cl_compl as complemento,\n"
                    + "    c.cl_bairro as bairro,\n"
                    + "    c.cl_cidade as municipio,\n"
                    + "    c.cl_uf as uf,\n"
                    + "    c.cl_cep as cep,\n"
                    + "    c.cl_fone as telefone,\n"
                    + "    c.cl_fax as fax,\n"
                    + "    c.cl_contato as contato,\n"
                    + "    c.cl_cgc as cnpj,\n"
                    + "    c.cl_inscricao as ie_rg,\n"
                    + "    c.cl_cpf as cpf,\n"
                    + "    c.cl_situacao as situacao,\n"
                    + "    case c.cl_inativo when 'N' then 1 else 0 end ativo,\n"
                    + "    c.cl_email as email,\n"
                    + "    c.cl_emailnfe as email_nfe,\n"
                    + "    c.cl_site as site,\n"
                    + "    c.cl_obsgeral as observacao,\n"
                    + "    c.cl_celular as celular\n"
                    + "from clientes c\n"
                    + "where c.cl_tipo = 'F'\n"
                    + "order by c.cl_codigo"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));

                    if ((rst.getString("cnpj") != null)
                            && (!rst.getString("cnpj").trim().isEmpty())) {
                        imp.setCnpj_cpf(rst.getString("cnpj"));
                    } else {
                        imp.setCnpj_cpf(rst.getString("cpf"));
                    }

                    imp.setIe_rg(rst.getString("ie_rg"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setAtivo(rst.getInt("ativo") == 1);
                    imp.setObservacao(rst.getString("observacao"));
                    imp.setTel_principal(rst.getString("telefone"));

                    if ((rst.getString("contato") != null)
                            && (!rst.getString("contato").trim().isEmpty())) {

                        imp.addContato(
                                rst.getString("contato"),
                                rst.getString("fax"),
                                rst.getString("celular"),
                                TipoContato.COMERCIAL,
                                rst.getString("email") != null ? rst.getString("email").toLowerCase() : null
                        );
                    } else {

                        imp.addContato(
                                "CONTATO",
                                rst.getString("fax"),
                                rst.getString("celular"),
                                TipoContato.COMERCIAL,
                                rst.getString("email") != null ? rst.getString("email").toLowerCase() : null
                        );
                    }

                    if ((rst.getString("email_nfe") != null)
                            && (!rst.getString("email_nfe").trim().isEmpty())) {

                        imp.addEmail(
                                "EMAIL NFE",
                                rst.getString("email_nfe").toLowerCase(),
                                TipoContato.NFE
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

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    pf_produto as idproduto,\n"
                    + "    pf_fornec as idofornecedor,\n"
                    + "    pf_refforn as codigoexterno,\n"
                    + "    coalesce(pf_quantidade, 1) as qtdembalagem,\n"
                    + "    pf_dtatlz as dataalteracao\n"
                    + "from prodforn"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rst.getString("idproduto"));
                    imp.setIdFornecedor(rst.getString("idofornecedor"));
                    imp.setCodigoExterno(rst.getString("codigoexterno"));
                    imp.setQtdEmbalagem(rst.getDouble("qtdembalagem"));
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
                    + "    c.cl_codigo as id,\n"
                    + "    c.cl_filial as idloja,\n"
                    + "    c.cl_nome as razao,\n"
                    + "    c.cl_nomfan as fantasia,\n"
                    + "    c.cl_endereco as endereco,\n"
                    + "    c.cl_nro as numero,\n"
                    + "    c.cl_compl as complemento,\n"
                    + "    c.cl_bairro as bairro,\n"
                    + "    c.cl_cidade as municipio,\n"
                    + "    c.cl_uf as uf,\n"
                    + "    c.cl_cep as cep,\n"
                    + "    c.cl_fone as telefone,\n"
                    + "    c.cl_fax as fax,\n"
                    + "    c.cl_contato as contato,\n"
                    + "    c.cl_cgc as cnpj,\n"
                    + "    c.cl_inscricao as ie_rg,\n"
                    + "    c.cl_cpf as cpf,\n"
                    + "    c.cl_situacao as situacao,\n"
                    + "    case c.cl_inativo when 'N' then 1 else 0 end ativo,\n"
                    + "    c.cl_email as email,\n"
                    + "    c.cl_emailnfe as email_nfe,\n"
                    + "    c.cl_site as site,\n"
                    + "    c.cl_dtnasc as datanascimento,\n"
                    + "    c.cl_estcivil as estadocovil,\n"
                    + "    c.cl_natural as naturalidade,\n"
                    + "    c.cl_filiacao as filiacao,\n"
                    + "    c.cl_conjuge as nomeconjuge,\n"
                    + "    c.cl_empresa as emprsa,\n"
                    + "    c.cl_emprfone as telefoneempresa,\n"
                    + "    c.cl_empradm as dataadmissao,\n"
                    + "    c.cl_emprende as empresaendereco,\n"
                    + "    c.cl_emprcida as empresamunicipio,\n"
                    + "    c.cl_emprbair as empresabairro,\n"
                    + "    c.cl_empruf as empresauf,\n"
                    + "    c.cl_emprcep as empresacep,\n"
                    + "    c.cl_emprfunc as empresafuncao,\n"
                    + "    c.cl_emprnro as empresanumero,\n"
                    + "    c.cl_emprcompl as empresacomplemento,\n"
                    + "    c.cl_obsgeral as observacao,\n"
                    + "    c.cl_limitecred as valorlimite,\n"
                    + "    c.cl_credativo as crediario,\n"
                    + "    c.cl_remunerac as salario,\n"
                    + "    c.cl_remuextra as salarioextra,\n"
                    + "    c.cl_sexo as sexo,\n"
                    + "    c.cl_celular as celular\n"
                    + "from clientes c\n"
                    + "where c.cl_tipo = 'C'\n"
                    + "order by c.cl_codigo"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));

                    if ((rst.getString("cnpj") != null)
                            && (!rst.getString("cnpj").trim().isEmpty())) {
                        imp.setCnpj(rst.getString("cnpj"));
                    } else {
                        imp.setCnpj(rst.getString("cpf"));
                    }

                    imp.setInscricaoestadual(rst.getString("ie_rg"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setTelefone(rst.getString("telefone"));
                    imp.setFax(rst.getString("fax"));
                    imp.setCelular(rst.getString("celular"));
                    imp.setEmail(rst.getString("email"));
                    imp.setAtivo(rst.getInt("ativo") == 1);
                    imp.setPermiteCheque(true);
                    imp.setPermiteCreditoRotativo(true);
                    imp.setDataNascimento(rst.getDate("datanascimento"));
                    imp.setNomeConjuge(rst.getString("nomeconjuge"));

                    if ((rst.getString("sexo") != null)
                            && (!rst.getString("sexo").trim().isEmpty())) {
                        if ("F".equals(rst.getString("sexo").trim())) {
                            imp.setSexo(TipoSexo.FEMININO);
                        } else {
                            imp.setSexo(TipoSexo.MASCULINO);
                        }
                    }

                    imp.setValorLimite(rst.getDouble("valorlimite"));
                    imp.setSalario(rst.getDouble("salario"));

                    imp.setEmpresa(rst.getString("empresa"));
                    imp.setCargo(rst.getString("empresafuncao"));
                    imp.setDataAdmissao(rst.getDate("dataadmissao"));
                    imp.setEmpresaEndereco(rst.getString("empresaendereco"));
                    imp.setEmpresaNumero(rst.getString("empresanumero"));
                    imp.setEmpresaComplemento(rst.getString("empresacomplemento"));
                    imp.setEmpresaBairro(rst.getString("empresabairro"));
                    imp.setEmpresaMunicipio(rst.getString("empresamunicipio"));
                    imp.setEmpresaUf(rst.getString("empresauf"));
                    imp.setEmpresaCep(rst.getString("empresacep"));
                    imp.setObservacao(rst.getString("observacao"));
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
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    r.cr_controle as id,\n"
                    + "    r.cr_nronota as numerocupom,\n"
                    + "    r.cr_dtnota as emissao,\n"
                    + "    r.cr_vencto as vencimento,\n"
                    + "    r.cr_valor as valor,\n"
                    + "    r.cr_cliente as idcliente,\n"
                    + "    r.cr_observ as observacao,\n"
                    + "    r.cr_vljuros as juros,\n"
                    + "    r.cr_vldesc as desconto\n"
                    + "from ctareceb r\n"
                    + "where r.cr_dtpagto is null\n"
                    + "and r.cr_filial = " + getLojaOrigem()
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rst.getString("id"));
                    imp.setIdCliente(rst.getString("idcliente"));
                    imp.setNumeroCupom(rst.getString("numerocupom"));
                    imp.setDataEmissao(rst.getDate("emissao"));
                    imp.setDataVencimento(rst.getDate("vencimento"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setJuros(rst.getDouble("juros"));
                    imp.setObservacao(rst.getString("observacao"));
                    result.add(imp);
                }
            }
        }
        return result;
    }
}
