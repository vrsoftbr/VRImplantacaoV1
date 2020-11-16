/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao2.dao.cadastro.produto2.associado.OpcaoAssociado;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.AssociadoIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
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
public class DSoftDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "DSoft";
    }

    private String getAliquotaKey(String cst, double aliq, double red) throws Exception {
        return String.format(
                "%s-%.2f-%.2f",
                cst,
                aliq,
                red
        );
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    icm.codigo as id,\n"
                    + "    icm.nome as descricao,\n"
                    + "    icm.cst,\n"
                    + "    icm.aliquota,\n"
                    + "    0 as reducao\n"
                    + "from ecfaliquota icm\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(
                            rst.getString("id"),
                            rst.getString("descricao"),
                            rst.getInt("cst"),
                            rst.getDouble("aliquota"),
                            rst.getDouble("reducao")
                    )
                    );
                }
            }

            try (ResultSet rst = stm.executeQuery(
                    "select distinct\n"
                    + "    cst,\n"
                    + "    coalesce(icm, 0) as aliquota,\n"
                    + "    coalesce(perc_reducao_icms, 0) as reducao\n"
                    + "from estoque\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    String id = getAliquotaKey(
                            rst.getString("cst"),
                            rst.getDouble("aliquota"),
                            rst.getDouble("reducao")
                    );

                    result.add(new MapaTributoIMP(
                            id,
                            id,
                            rst.getInt("cst"),
                            rst.getDouble("aliquota"),
                            rst.getDouble("reducao")
                    )
                    );
                }
            }

            try (ResultSet rst = stm.executeQuery(
                    "select distinct\n"
                    + "    cst_entrada as cst_entrada,\n"
                    + "    coalesce(icms_entrada, 0) as aliquota_entrada,\n"
                    + "    coalesce(red_bc_entrada, 0) as reducao_entrada\n"
                    + "from estoque\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    String id = getAliquotaKey(
                            rst.getString("cst_entrada"),
                            rst.getDouble("aliquota_entrada"),
                            rst.getDouble("reducao_entrada")
                    );

                    result.add(new MapaTributoIMP(
                            id,
                            id,
                            rst.getInt("cst_entrada"),
                            rst.getDouble("aliquota_entrada"),
                            rst.getDouble("reducao_entrada")
                    )
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
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    m1.codigo as merc1,\n"
                    + "    m1.nome as desc_merc1,\n"
                    + "    m2.codigo as merc2,\n"
                    + "    m2.nome as desc_merc2,\n"
                    + "    m3.id as merc3,\n"
                    + "    m3.descricao as desc_merc3\n"
                    + "from grupo m1\n"
                    + "join subgrupo m2 on m2.codgrupo = m1.codigo\n"
                    + "join subcategorias m3 on m3.codgrupo = m1.codigo\n"
                    + "    and m3.codsubgrupo = m2.codigo\n"
                    + "order by 1, 3, 5"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rst.getString("merc1"));
                    imp.setMerc1Descricao(rst.getString("desc_merc1"));
                    imp.setMerc2ID(rst.getString("merc2"));
                    imp.setMerc2Descricao(rst.getString("desc_merc2"));
                    imp.setMerc3ID(rst.getString("merc3"));
                    imp.setMerc3Descricao(rst.getString("desc_merc3"));
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
                    + "    e.codigo as id,\n"
                    + "    e.codbarra as ean,\n"
                    + "    case e.balanca when 'S' then 1 else 0 end balanca,\n"
                    + "    e.validade,\n"
                    + "    e.descricao as descricaocompleta,\n"
                    + "    e.descrifiscal as descricaoreduzida,\n"
                    + "    e.med as tipoembalagem,\n"
                    + "    e.codgrupo as merc1,\n"
                    + "    grp.nome as desc_merc1,\n"
                    + "    e.codsubgrupo as merc2,\n"
                    + "    sgr.nome as desc_merc2,\n"
                    + "    e.codsubcategoria as merc3,\n"
                    + "    sub.codgrupo as grupo_subcategoria,\n"
                    + "    sub.codsubgrupo as subgrupo_subcategoria,\n"
                    + "    sub.descricao as descricao_subcategoria,\n"
                    + "    coalesce(e.precocusto, 0) as custo,\n"
                    + "    coalesce(e.precovenda, 0) as precovenda,\n"
                    + "    coalesce(e.qtde, 0) as qtdestoque,\n"
                    + "    coalesce(e.qtdeminima, 0) as estoqueminimo,\n"
                    + "    e.datacadastro,\n"
                    + "    case e.inativo when 'N' then 1 else 0 end situacaocadastro,\n"
                    + "    e.cod_ncm as ncm,\n"
                    + "    e.cod_cest as cest,\n"
                    + "    e.cst,\n"
                    + "    e.icm,\n"
                    + "    e.perc_reducao_icms,\n"
                    + "    e.cst_entrada,\n"
                    + "    e.icms_entrada,\n"
                    + "    e.red_bc_entrada as reducao_entrada,\n"
                    + "    e.codaliquota as icms_ecf,\n"
                    + "    ecf.nome as descricao_aliq_ecf,\n"
                    + "    ecf.cst as cst_ecf,\n"
                    + "    ecf.aliquota as aliquota_ecf,\n"
                    + "    e.cst_pis,\n"
                    + "    e.cst_cofins,\n"
                    + "    e.natureza_pis_cofins as naturezareceita\n"
                    + "from estoque e\n"
                    + "left join ecfaliquota ecf on ecf.codigo = e.codaliquota\n"
                    + "left join subcategorias sub on sub.id = e.codsubcategoria\n"
                    + "left join grupo grp on grp.codigo = e.codgrupo\n"
                    + "left join subgrupo sgr on sgr.codigo = e.codsubgrupo\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setEan(rst.getString("ean"));
                    imp.seteBalanca(rst.getInt("balanca") == 1);
                    imp.setValidade(rst.getInt("validade"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
                    imp.setCodMercadologico1(rst.getString("merc1"));
                    imp.setCodMercadologico2(rst.getString("merc2"));
                    imp.setCodMercadologico3(rst.getString("merc3"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setSituacaoCadastro(rst.getInt("situacaocadastro"));
                    imp.setCustoComImposto(rst.getDouble("custo"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setEstoque(rst.getDouble("qtdestoque"));
                    imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getString("cst_pis"));
                    imp.setPiscofinsCstCredito(rst.getString("cst_cofins"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("naturezareceita"));

                    String icmsDebitoId = getAliquotaKey(rst.getString("cst"), rst.getDouble("aliquota"), rst.getDouble("reducao"));
                    String icmsCreditoId = getAliquotaKey(rst.getString("cst_entrada"), rst.getDouble("icms_entrada"), rst.getDouble("reducao_entrada"));

                    imp.setIcmsDebitoId(icmsDebitoId);
                    imp.setIcmsDebitoForaEstadoId(icmsDebitoId);
                    imp.setIcmsDebitoForaEstadoNfId(icmsDebitoId);
                    imp.setIcmsCreditoId(icmsCreditoId);
                    imp.setIcmsCreditoForaEstadoId(icmsCreditoId);
                    imp.setIcmsConsumidorId(rst.getString("icms_ecf"));

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
                    + "    b.cod_produto as idproduto,\n"
                    + "    b.cod_barras as ean\n"
                    + "from estoque_codbarras b\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("idproduto"));
                    imp.setEan(rst.getString("ean"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<AssociadoIMP> getAssociados(Set<OpcaoAssociado> opt) throws Exception {
        List<AssociadoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    a.cod_produto_pai as idproduto_pai,\n"
                    + "    p1.descricao as desc_produtopai,\n"
                    + "    a.cod_produto_filho as idproduto_filho,\n"
                    + "    p2.descricao as desc_produto_filho,\n"
                    + "    a.qtde_indice as qtd\n"
                    + "from estoque_indices a\n"
                    + "join estoque p1 on p1.codigo = a.cod_produto_pai\n"
                    + "join estoque p2 on p2.codigo = a.cod_produto_filho\n"
                    + "order by 1, 3"
                    + /* TESTAR AMBOS SQL PARA MIGRAÇÃO DO ASSOCIADO */ "select\n"
                    + "    ren.cod_prod_mestre,\n"
                    + "    p1.descricao as idproduto_pai,\n"
                    + "    ren.cod_prod_filho,\n"
                    + "    p2.descricao as idproduto_filho,\n"
                    + "    ren.descricao, \n"
                    + "    1 as qtd\n"
                    + "from estoque_rentabilidade ren\n"
                    + "join estoque p1 on p1.codigo = ren.cod_prod_mestre\n"
                    + "join estoque p2 on p2.codigo = ren.cod_prod_filho\n"
                    + "order by 1, 3"
            )) {
                while (rst.next()) {
                    AssociadoIMP imp = new AssociadoIMP();
                    imp.setId(rst.getString("idproduto_pai"));
                    imp.setQtdEmbalagem(1);
                    imp.setProdutoAssociadoId(rst.getString("idproduto_filho"));
                    imp.setQtdEmbalagemItem(rst.getInt("qtd"));
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
                    + "    f.codigo as id,\n"
                    + "    f.razao,\n"
                    + "    f.nome as fantasia,\n"
                    + "    f.cgc as cnpj,\n"
                    + "    f.ie, \n"
                    + "    f.cpf,\n"
                    + "    f.rg, \n"
                    + "    f.contato,\n"
                    + "    f.endereco,\n"
                    + "    f.num_endereco,\n"
                    + "    f.bairro,\n"
                    + "    f.cidade as municipio,\n"
                    + "    f.uf,\n"
                    + "    f.cep,\n"
                    + "    f.telefone,\n"
                    + "    f.fax, \n"
                    + "    f.celular,\n"
                    + "    f.email,\n"
                    + "    f.email_secundario,\n"
                    + "    f.email_cotacao_web,\n"
                    + "    f.homepage,\n"
                    + "    case f.inativo when 'N' then 1 else 0 end situacaocadastro,\n"
                    + "    f.prazo_entrega,\n"
                    + "    f.obs as observacao,\n"
                    + "    f.vendedor,\n"
                    + "    f.fone_vend,\n"
                    + "    f.representante,\n"
                    + "    f.fone_rep\n"
                    + "from fornecedor f\n"
                    + "order by 1"
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

                    if ((rst.getString("ie") != null)
                            && (!rst.getString("ie").trim().isEmpty())) {
                        imp.setIe_rg(rst.getString("ie"));
                    } else {
                        imp.setIe_rg(rst.getString("ie"));
                    }

                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));

                    imp.setTel_principal(rst.getString("telefone"));

                    imp.setAtivo(rst.getInt("situacaocadastro") == 1);
                    imp.setPrazoEntrega(rst.getInt("prazo_entrega"));
                    imp.setObservacao(rst.getString("observacao"));

                    if ((rst.getString("fax") != null)
                            && (!rst.getString("fax").trim().isEmpty())) {
                        imp.addTelefone("FAX", rst.getString("fax"));
                    }

                    if ((rst.getString("celular") != null)
                            && (!rst.getString("celular").trim().isEmpty())) {
                        imp.addCelular("CELULAR", rst.getString("celular"));
                    }

                    if ((rst.getString("email") != null)
                            && (!rst.getString("email").trim().isEmpty())) {
                        imp.addEmail("EMAIL", rst.getString("email").toLowerCase(), TipoContato.NFE);
                    }

                    if ((rst.getString("email_secundario") != null)
                            && (!rst.getString("email_secundario").trim().isEmpty())) {
                        imp.addEmail("EMAIL 2", rst.getString("email_secundario").toLowerCase(), TipoContato.COMERCIAL);
                    }

                    if ((rst.getString("email_cotacao_web") != null)
                            && (!rst.getString("email_cotacao_web").trim().isEmpty())) {
                        imp.addEmail("EMAIL COTACAO WEB", rst.getString("email_cotacao_web").toLowerCase(), TipoContato.COMERCIAL);
                    }

                    imp.addTelefone(rst.getString("vendedor"), rst.getString("fone_vend"));
                    imp.addTelefone(rst.getString("representante"), rst.getString("fone_rep"));

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
                    + "    codigo_fornecedor as idfornecedor,\n"
                    + "    codigo_interno_sistema as idproduto,\n"
                    + "    codigo_fabricante as codigoexterno\n"
                    + "from estoque_codigo_fabric\n"
                    + "order by 1, 2"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdFornecedor(rst.getString("idfornecedor"));
                    imp.setIdProduto(rst.getString("idproduto"));
                    imp.setCodigoExterno(rst.getString("codigoexterno"));
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
                    + "    c.codigo as id,\n"
                    + "    c.nome as razao,\n"
                    + "    c.cpf,\n"
                    + "    c.rg,\n"
                    + "    c.cgc,\n"
                    + "    c.ie as inscricaoestadual,\n"
                    + "    c.endereco,\n"
                    + "    c.num_endereco,\n"
                    + "    c.complemento,\n"
                    + "    c.complemento_endereco,\n"
                    + "    c.bairro,\n"
                    + "    c.cidade as municipio,\n"
                    + "    c.uf,\n"
                    + "    c.cep,\n"
                    + "    c.endereco_cobranca,\n"
                    + "    c.complemento_cobranca,\n"
                    + "    c.bairro_cobranca,\n"
                    + "    c.cidade_cobranca,\n"
                    + "    c.uf_cobranca,\n"
                    + "    c.cep_cobranca,\n"
                    + "    c.endereco_entrega,\n"
                    + "    c.num_end_entrega,\n"
                    + "    c.bairro_entrega,\n"
                    + "    c.cidade_entrega,\n"
                    + "    c.uf_entrega,\n"
                    + "    c.cep_entrega,\n"
                    + "    c.telefone,\n"
                    + "    c.telefone_2,\n"
                    + "    c.telefone_conjuge,\n"
                    + "    c.telefone_entrega,\n"
                    + "    c.celular,\n"
                    + "    c.fax,\n"
                    + "    c.email,\n"
                    + "    c.email_secundario,\n"
                    + "    c.email_cobranca,\n"
                    + "    c.email_financeiro,\n"
                    + "    c.limitecredito as valorlimite,\n"
                    + "    c.datanascto as datanascimento,\n"
                    + "    c.datacadastro,\n"
                    + "    c.ondetrabalha as empresa,\n"
                    + "    c.fonetrabalho as telefoneempresa,\n"
                    + "    c.endtrabalho as enderecotrabalho,\n"
                    + "    c.funcao as cargo,\n"
                    + "    c.salario,\n"
                    + "    c.estadocivil,\n"
                    + "    c.conjugue,\n"
                    + "    c.conjugue_cpf,\n"
                    + "    c.conjugue_rg,\n"
                    + "    c.datanasctoconjugue,\n"
                    + "    c.nomepai,\n"
                    + "    c.nomemae,\n"
                    + "    case c.inativo when 'N' then 1 else 0 end situacaocadastro,\n"
                    + "    c.contato\n"
                    + "from cliente c\n"
                    + "order by 1"
            )) {
                while (rst.next()) {

                }
            }
        }
        return null;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    r.codigo as id,\n"
                    + "    r.codcliente as idcliente,\n"
                    + "    r.documento,\n"
                    + "    r.numparcela,\n"
                    + "    r.dataemissao,\n"
                    + "    r.datavencimento,\n"
                    + "    r.historico,\n"
                    + "    r.valorrecebido\n"
                    + "from receber r\n"
                    + "where r.codigo not in (select cod_receber from recebido)\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rst.getString("id"));
                    imp.setIdCliente(rst.getString("idcliente"));
                    imp.setNumeroCupom(rst.getString("documento"));
                    imp.setDataEmissao(rst.getDate("dataemissao"));
                    imp.setDataVencimento(rst.getDate("datavencimento"));
                    imp.setValor(rst.getDouble("valorrecebido"));
                    imp.setObservacao(rst.getString("historico") + " PARCELA - " + rst.getString("parcela"));
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
                    "select\n"
                    + "    pg.codigo as id,\n"
                    + "    pg.codfornecedor as idfornecedor,\n"
                    + "    pg.documento as numerodocumento,\n"
                    + "    pg.dataemissao,\n"
                    + "    pg.datavencimento,\n"
                    + "    pg.valor,\n"
                    + "    pg.parcela,\n"
                    + "    pg.historico as observacao\n"
                    + "from pagar pg\n"
                    + "where pg.datapagamento is null\n"
                    + "and pg.codfornecedor is not null"
            )) {
                while (rst.next()) {

                }
            }
        }
        return null;
    }
}
