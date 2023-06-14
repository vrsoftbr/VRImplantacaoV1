/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2_5.dao.sistema;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoEmpresa;
import vrimplantacao2.vo.enums.TipoEstadoCivil;
import vrimplantacao2.vo.enums.TipoFornecedor;
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.vo.importacao.ChequeIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;

/**
 *
 * @author Lucas
 * 
 * SISTEMA REFATORADO DA 2.0 PARA 2.5 E AINDA NÃO TESTADO FAVOR VALIDAR OS METODOS
 */
public class AutoAdm2_5DAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "AutoADM";
    }

    private String getAliquotaForaEstadoKey(String cst, double aliq, double red) throws SQLException {
        return String.format(
                "%s-%.2f-%.2f",
                cst,
                aliq,
                red
        );
    }

    private String getAliquotaDentroEstadoKey(String cst, double aliq, double red) throws SQLException {
        return String.format(
                "%s-%.2f-%.2f",
                cst,
                aliq,
                red
        );
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    l.nr_loja as id,\n"
                    + "    p.nm_fantasia as nome\n"
                    + "from tb_loja l\n"
                    + "join tb_pessoa p on p.cd_pessoa = l.cd_pessoa_loja\n"
                    + "order by l.nr_loja"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("id"), rst.getString("nome")));
                }
            }
        }
        return result;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    distinct\n"
                    + "    icms.ds_tributacao_icms descricao,\n"
                    + "    icms.cd_governo icmscst,\n"
                    + "    g_imp.pe_icms_fora_estado icmsaliq,\n"
                    + "    coalesce(g_imp.pe_icms_reducao, 0) icmsreducao\n"
                    + "from tb_produto pro\n"
                    + "join tb_grupo_imposto g_imp on g_imp.cd_grupo_imposto = pro.cd_grupo_imposto\n"
                    + "join tb_tributacao_icms icms on icms.cd_tributacao_icms = g_imp.cd_tributacao_icms"
            )) {
                while (rst.next()) {
                    String id = getAliquotaForaEstadoKey(
                            rst.getString("icmscst"),
                            rst.getDouble("icmsaliq"),
                            rst.getDouble("icmsreducao"));
                    result.add(new MapaTributoIMP(
                            id,
                            rst.getString("descricao"),
                            rst.getInt("icmscst"),
                            rst.getDouble("icmsaliq"),
                            rst.getDouble("icmsreducao")
                    ));
                }
            }

            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    distinct\n"
                    + "    icms.ds_tributacao_icms descricao,\n"
                    + "    icms.cd_governo icmscst,\n"
                    + "    g_imp.pe_icms_dentro_estado icmsaliq,\n"
                    + "    coalesce(g_imp.pe_icms_reducao, 0) icmsreducao\n"
                    + "from tb_produto pro\n"
                    + "join tb_grupo_imposto g_imp on g_imp.cd_grupo_imposto = pro.cd_grupo_imposto\n"
                    + "join tb_tributacao_icms icms on icms.cd_tributacao_icms = g_imp.cd_tributacao_icms"
            )) {
                while (rst.next()) {
                    String id = getAliquotaDentroEstadoKey(
                            rst.getString("icmscst"),
                            rst.getDouble("icmsaliq"),
                            rst.getDouble("icmsreducao"));
                    result.add(new MapaTributoIMP(
                            id,
                            rst.getString("descricao"),
                            rst.getInt("icmscst"),
                            rst.getDouble("icmsaliq"),
                            rst.getDouble("icmsreducao")
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
                    OpcaoProduto.MERCADOLOGICO_PRODUTO,
                    OpcaoProduto.MERCADOLOGICO_POR_NIVEL,
                    OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                    OpcaoProduto.FAMILIA_PRODUTO,
                    OpcaoProduto.FAMILIA,
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
                    OpcaoProduto.MAPA_TRIBUTACAO,
                    OpcaoProduto.EXCECAO,
                    OpcaoProduto.TIPO_PRODUTO
                }
        ));
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery("  select\n"
                    + "    fam.cd_produto_grupo, fam.nm_produto_grupo\n"
                    + "from tb_produto_grupo fam\n"
                    + "order by 1")) {
                while (rst.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("cd_produto_grupo"));
                    imp.setDescricao(rst.getString("nm_produto_grupo"));
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
                    + "    p.cd_produto as id,\n"
                    + "    b.cd_barra as ean,\n"
                    + "    um.sg_unidade_medida as tipoembalagem,\n"
                    + "    pm.qt_multiplicador as qtdembalagem,\n"
                    + "    um.ds_unidade_medida,\n"
                    + "    p.fg_produto_balanca as balanca,\n"
                    + "    p.fg_unidade_peso as pesavel,\n"
                    + "    p.vl_validade as validade,\n"
                    + "    p.nr_dias_validade,\n"
                    + "    p.cd_produto_grupo as familiaproduto,\n"
                    + "    p.nm_produto as descricaocompleta,\n"
                    + "    p.nm_produto_reduzido as descricaoreduzida,\n"
                    + "    p.vl_venda as precovenda,\n"
                    + "    p.pe_margem as margem,\n"
                    + "    p.vl_custo as custo,\n"
                    + "    p.dt_inclusao as datacadastro,\n"
                    + "    p.dt_alteracao as dataalteracao,\n"
                    + "    p.fg_situacao as situacaocadastro,\n"
                    + "    p.nr_ncm as ncm,\n"
                    + "    cest.nr_cest as cest,\n"
                    + "    tp.cd_governo as tipoproduto,\n"
                    + "    tp.ds_tipo_produto as descricaotipoproduto,\n"
                    + "    pis_e.cd_governo as pisentrada,\n"
                    + "    pis_s.cd_governo as pissaida,\n"
                    + "    cof_e.cd_governo as cofinsentrada,\n"
                    + "    cof_s.cd_governo as cofinssaida,\n"
                    + "    nat.nr_natureza_receita as naturezareita,\n"
                    + "    icms.ds_tributacao_icms,\n"
                    + "    icms.cd_governo as icmscst,\n"
                    + "    g_imp.pe_icms_dentro_estado as aliqdentroestado,\n"
                    + "    g_imp.pe_icms_fora_estado as aliqforaestado,\n"
                    + "    coalesce(g_imp.pe_icms_reducao, 0) as reducao,\n"
                    + "    coalesce(g_imp.pe_reducao_base_icms, 0) pe_reducao_base_icms\n"
                    + "from tb_produto p\n"
                    + "left join tb_produto_tipo tp on\n"
                    + "    tp.cd_produto_tipo = p.cd_produto_tipo\n"
                    + "left join tb_produto_unidade_medida pm\n"
                    + "    on pm.cd_produto = p.cd_produto\n"
                    + "left join tb_produto_codigo_barra b\n"
                    + "    on b.cd_produto_unidade_medida = pm.cd_produto_unidade_medida\n"
                    + "left join tb_unidade_medida um\n"
                    + "    on um.cd_unidade_medida = pm.cd_unidade_medida\n"
                    + "left join tb_cest cest\n"
                    + "    on cest.cd_cest = p.cd_cest\n"
                    + "left join tb_tributacao_pis_cofins pis_e\n"
                    + "    on pis_e.cd_tributacao_pis_cofins = p.cd_tributacao_pis_e\n"
                    + "left join tb_tributacao_pis_cofins pis_s\n"
                    + "    on pis_s.cd_tributacao_pis_cofins = p.cd_tributacao_pis_s\n"
                    + "left join tb_tributacao_pis_cofins cof_e\n"
                    + "    on cof_e.cd_tributacao_pis_cofins = p.cd_tributacao_cofins_e\n"
                    + "left join tb_tributacao_pis_cofins cof_s\n"
                    + "    on cof_s.cd_tributacao_pis_cofins = p.cd_tributacao_cofins_s\n"
                    + "left join tb_natureza_receita nat\n"
                    + "    on nat.cd_natureza_receita = p.cd_natureza_receita\n"
                    + "left join tb_grupo_imposto g_imp\n"
                    + "    on g_imp.cd_grupo_imposto = p.cd_grupo_imposto\n"
                    + "left join tb_tributacao_icms icms\n"
                    + "    on icms.cd_tributacao_icms = g_imp.cd_tributacao_icms"
            )) {
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().carregarProdutosBalanca();
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    ProdutoBalancaVO produtoBalanca;
                    imp.setImportId(rst.getString("id"));

                    long codigoProduto;
                    codigoProduto = Long.parseLong(imp.getImportId());
                    if (codigoProduto <= Integer.MAX_VALUE) {
                        produtoBalanca = produtosBalanca.get((int) codigoProduto);
                    } else {
                        produtoBalanca = null;
                    }

                    if (produtoBalanca != null) {
                        imp.seteBalanca(true);
                        imp.setValidade(produtoBalanca.getValidade() > 1 ? produtoBalanca.getValidade() : 0);
                    } else {
                        imp.setValidade(0);
                        imp.seteBalanca(false);
                    }

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setTipoProduto(rst.getInt("tipoproduto"));
                    imp.setEan(rst.getString("ean"));
                    imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setDataAlteracao(rst.getDate("dataalteracao"));
                    imp.setIdFamiliaProduto(rst.getString("familiaproduto"));
                    imp.setValidade(rst.getInt("validade"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setCustoComImposto(rst.getDouble("custo"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getInt("pissaida"));
                    imp.setPiscofinsCstCredito(rst.getInt("cofinsentrada"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("naturezareita"));

                    String aliqDentroEstado = getAliquotaDentroEstadoKey(
                            rst.getString("icmscst"),
                            rst.getDouble("aliqdentroestado"),
                            rst.getDouble("reducao"));

                    String aliqForaEstado = getAliquotaForaEstadoKey(
                            rst.getString("icmscst"),
                            rst.getDouble("aliqforaestado"),
                            rst.getDouble("reducao"));

                    imp.setIcmsDebitoId(aliqDentroEstado);
                    imp.setIcmsDebitoForaEstadoId(aliqForaEstado);
                    imp.setIcmsDebitoForaEstadoNfId(aliqForaEstado);
                    imp.setIcmsCreditoId(aliqDentroEstado);
                    imp.setIcmsCreditoForaEstadoId(aliqForaEstado);
                    imp.setIcmsConsumidorId(aliqDentroEstado);

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
                    + "    p.cd_produto as id,\n"
                    + "    b.cd_barra as ean,\n"
                    + "    um.sg_unidade_medida as tipoembalagem,\n"
                    + "    pm.qt_multiplicador as qtdembalagem,\n"
                    + "    um.ds_unidade_medida\n"
                    + "from tb_produto p\n"
                    + "join tb_produto_tipo tp on\n"
                    + "    tp.cd_produto_tipo = p.cd_produto_tipo\n"
                    + "join tb_produto_unidade_medida pm\n"
                    + "    on pm.cd_produto = p.cd_produto\n"
                    + "join tb_produto_codigo_barra b\n"
                    + "    on b.cd_produto_unidade_medida = pm.cd_produto_unidade_medida\n"
                    + "join tb_unidade_medida um\n"
                    + "    on um.cd_unidade_medida = pm.cd_unidade_medida\n"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setEan(rst.getString("ean"));
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

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    pe.cd_pessoa as id,\n"
                    + "    pe.nm_pessoa as razao,\n"
                    + "    pe.nm_fantasia as fantasia,\n"
                    + "    pe.nr_cpf as cpf,\n"
                    + "    pe.nr_rg as rg,\n"
                    + "    pe.nr_cnpj as cnpj,\n"
                    + "    pe.nr_cpf_cnpj as cpfcnpj,\n"
                    + "    pe.fg_fisica_juridica,\n"
                    + "    pe.nr_ie as inscricaoestadual,\n"
                    + "    pe.nr_im as inscricaomunicipal,\n"
                    + "    pe.dt_inclusao as datacadastro,\n"
                    + "    f.dt_inclusao,\n"
                    + "    pe.nr_cep as cep,\n"
                    + "    (lt.ds_logradouro_tipo||' '||l.nm_logradouro) as endereco,\n"
                    + "    pe.nr_endereco as numero,\n"
                    + "    pe.ds_complemento as complemento,\n"
                    + "    ba.nm_bairro as bairro,\n"
                    + "    cid.nm_cidade as municipio,\n"
                    + "    cid.cd_municipio_ibge as municipioibge,\n"
                    + "    uf.sg_estado as uf,\n"
                    + "    pe.ds_observacao as observacao,\n"
                    + "    pe.nm_pai as pai,\n"
                    + "    pe.nm_mae as mae,\n"
                    + "    pe.dt_nascimento as datanascimento,\n"
                    + "    f.nr_dias_entrega as prazoentrega,\n"
                    + "    cp.ds_condicao_pagamento as condicaopagamento,\n"
                    + "    f.fg_situacao as situacaocadastro,\n"
                    + "    f.fg_produtor_rural,\n"
                    + "    f.fg_atacado,\n"
                    + "    f.fg_optante_simples\n"
                    + "from tb_pessoa pe\n"
                    + "join tb_fornecedor f\n"
                    + "    on f.cd_pessoa_fornecedor = pe.cd_pessoa\n"
                    + "left join tb_condicao_pagamento cp\n"
                    + "    on cp.cd_condicao_pagamento = f.cd_condicao_pagamento\n"
                    + "left join tb_logradouro_bairro lb\n"
                    + "    on lb.cd_logradouro_bairro = pe.cd_logradouro_bairro\n"
                    + "left join tb_logradouro l\n"
                    + "    on l.cd_logradouro = lb.cd_logradouro\n"
                    + "left join tb_logradouro_tipo lt\n"
                    + "    on lt.cd_logradouro_tipo = l.cd_logradouro_tipo\n"
                    + "left join tb_bairro ba\n"
                    + "    on ba.cd_bairro = lb.cd_bairro\n"
                    + "left join tb_cidade cid\n"
                    + "    on cid.cd_cidade = ba.cd_cidade\n"
                    + "left join tb_estado uf\n"
                    + "    on uf.cd_estado = cid.cd_estado"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setCnpj_cpf(rst.getString("cpfcnpj"));

                    if ((rst.getString("rg") != null)
                            && (!rst.getString("rg").trim().isEmpty())) {
                        imp.setIe_rg(rst.getString("rg"));
                    } else {
                        imp.setIe_rg(rst.getString("inscricaoestadual"));
                    }

                    imp.setInsc_municipal(rst.getString("inscricaomunicipal"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setCep(rst.getString("cep"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setIbge_municipio(rst.getInt("municipioibge"));
                    imp.setUf(rst.getString("uf"));
                    imp.setDatacadastro(rst.getDate("dt_inclusao"));
                    imp.setPrazoEntrega(rst.getInt("prazoentrega"));
                    imp.setObservacao("COND PAGTO - " + rst.getString("condicaopagamento"));
                    imp.setAtivo("A".equals(rst.getString("situacaocadastro").trim()));

                    if ("S".equals(rst.getString("fg_produtor_rural").trim())) {

                        if ("F".equals(rst.getString("fg_fisica_juridica").trim())) {
                            imp.setTipoEmpresa(TipoEmpresa.PRODUTOR_RURAL_FISICA);
                        } else {
                            imp.setTipoEmpresa(TipoEmpresa.PRODUTOR_RURAL_JURIDICO);
                        }
                    }

                    if ((imp.getTipoEmpresa() != TipoEmpresa.PRODUTOR_RURAL_FISICA)
                            && (imp.getTipoEmpresa() != TipoEmpresa.PRODUTOR_RURAL_JURIDICO)) {

                        if ("S".equals(rst.getString("fg_optante_simples").trim())) {
                            imp.setTipoEmpresa(TipoEmpresa.ME_SIMPLES);
                        }
                    }

                    if ("S".equals(rst.getString("fg_atacado").trim())) {
                        imp.setTipoFornecedor(TipoFornecedor.ATACADO);
                    }

                    try (ResultSet rst2 = stm.executeQuery(
                            "select\n"
                            + "    pe.cd_pessoa as id,\n"
                            + "    ct.ds_contato_tipo as tipocontato,\n"
                            + "    pc.ds_pessoa_contato as contato,\n"
                            + "    pc.nm_contato as nome,\n"
                            + "    pc.fg_principal as principal\n"
                            + "from tb_pessoa pe\n"
                            + "join tb_fornecedor f\n"
                            + "    on f.cd_pessoa_fornecedor = pe.cd_pessoa\n"
                            + "join tb_pessoa_contato pc\n"
                            + "    on pc.cd_pessoa = pe.cd_pessoa\n"
                            + "join tb_contato_tipo ct\n"
                            + "    on ct.cd_contato_tipo = pc.cd_contato_tipo\n"
                            + "where pe.cd_pessoa = " + imp.getImportId()
                    )) {
                        while (rst2.next()) {

                            String nome = rst2.getString("nome");

                            if ("Fone Comercial".equals(rst2.getString("tipocontato"))) {
                                imp.setTel_principal(rst2.getString("contato"));
                            }

                            if ("Fone Comercial".equals(rst2.getString("tipocontato"))) {

                                if (nome != null && !nome.trim().isEmpty()) {
                                    imp.addTelefone(nome, rst2.getString("contato"));
                                } else {
                                    imp.addTelefone("TELEFONE", rst2.getString("contato"));
                                }
                            }

                            if ("Celular".equals(rst2.getString("tipocontato"))) {

                                if (nome != null && !nome.trim().isEmpty()) {
                                    imp.addCelular(nome, rst2.getString("contato"));
                                } else {
                                    imp.addCelular("CELULAR", rst2.getString("contato"));
                                }
                            }

                            if ("Email".equals(rst2.getString("tipocontato"))) {

                                if (nome != null && !nome.trim().isEmpty()) {
                                    imp.addEmail(nome, rst2.getString("contato"), TipoContato.COMERCIAL);
                                } else {
                                    imp.addEmail("EMAIL", rst2.getString("contato"), TipoContato.COMERCIAL);
                                }
                            }

                            if ("Email NFe".equals(rst2.getString("tipocontato"))) {

                                if (nome != null && !nome.trim().isEmpty()) {
                                    imp.addEmail(nome, rst2.getString("contato"), TipoContato.NFE);
                                } else {
                                    imp.addEmail("EMAIL NFE", rst2.getString("contato"), TipoContato.NFE);
                                }
                            }

                            if ("Fax".equals(rst2.getString("tipocontato"))) {

                                if (nome != null && !nome.trim().isEmpty()) {
                                    imp.addTelefone(nome, rst2.getString("contato"));
                                } else {
                                    imp.addTelefone("FAX", rst2.getString("contato"));
                                }
                            }

                            if ("Fone Residencial".equals(rst2.getString("tipocontato"))) {

                                if (nome != null && !nome.trim().isEmpty()) {
                                    imp.addTelefone(nome, rst2.getString("contato"));
                                } else {
                                    imp.addTelefone("TEL RESIDENCIAL", rst2.getString("contato"));
                                }
                            }
                        }
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
                    + "    pf.cd_pessoa_fornecedor as idfornecedor,\n"
                    + "    pf.cd_produto as idproduto,\n"
                    + "    pf.dt_alteracao as dataalteracao,\n"
                    + "    pf.cd_codigo_fornecedor as codigoexterno\n"
                    + "from tb_produto_fornecedor pf\n"
                    + "order by 1, 2"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdFornecedor(rst.getString("idfornecedor"));
                    imp.setIdProduto(rst.getString("idproduto"));
                    imp.setDataAlteracao(rst.getDate("dataalteracao"));
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
                    + "    pe.cd_pessoa as id,\n"
                    + "    pe.nm_pessoa as razao,\n"
                    + "    pe.nm_fantasia as fantasia,\n"
                    + "    pe.nr_cpf as cpf,\n"
                    + "    pe.nr_rg as rg,\n"
                    + "    pe.nr_cnpj as cnpj,\n"
                    + "    pe.nr_cpf_cnpj as cpfcnpj,\n"
                    + "    pe.nr_ie as inscricaoestadual,\n"
                    + "    pe.nr_im as inscricaomunicipal,\n"
                    + "    pe.dt_inclusao,\n"
                    + "    c.dt_inclusao as datacadastro,\n"
                    + "    pe.nr_cep as cep,\n"
                    + "    (lt.ds_logradouro_tipo||' '||l.nm_logradouro) as endereco,\n"
                    + "    pe.nr_endereco as numero,\n"
                    + "    pe.ds_complemento as complemento,\n"
                    + "    ba.nm_bairro as bairro,\n"
                    + "    cid.nm_cidade as municipio,\n"
                    + "    cid.cd_municipio_ibge as municipioibge,\n"
                    + "    uf.sg_estado as uf,\n"
                    + "    pe.ds_observacao as observacao,\n"
                    + "    pe.nm_pai as pai,\n"
                    + "    pe.nm_mae as mae,\n"
                    + "    pe.dt_nascimento as datanascimento,\n"
                    + "    c.fg_situacao as situacaocadastro,\n"
                    + "    c.ds_obs,\n"
                    + "    c.fg_estado_civil as estadocivil,\n"
                    + "    c.tp_sexo as sexo,\n"
                    + "    c.nm_empresa as empresa,\n"
                    + "    c.dt_admissao as dataadmissao,\n"
                    + "    c.ds_profissao as cargo,\n"
                    + "    c.vl_renda_principal as salario,\n"
                    + "    c.nr_cep_cobranca as cepcobranca,\n"
                    + "    (lt_cob.ds_logradouro_tipo||' '||l_cob.nm_logradouro) as enderecocobranca,\n"
                    + "    c.nr_endereco_cobranca as numerocobranca,\n"
                    + "    c.ds_complemento_cobranca as complementocobranca,\n"
                    + "    ba_cob.nm_bairro as bairrocobranca,\n"
                    + "    cid_cob.nm_cidade as municipiocobranca,\n"
                    + "    cid_cob.cd_municipio_ibge as municipioibgecobranca,\n"
                    + "    uf_cob.sg_estado as ufcobranca\n"
                    + "from tb_pessoa pe\n"
                    + "join tb_cliente c\n"
                    + "    on c.cd_pessoa_cliente = pe.cd_pessoa\n"
                    + "left join tb_logradouro_bairro lb\n"
                    + "    on lb.cd_logradouro_bairro = pe.cd_logradouro_bairro\n"
                    + "left join tb_logradouro l\n"
                    + "    on l.cd_logradouro = lb.cd_logradouro\n"
                    + "left join tb_logradouro_tipo lt\n"
                    + "    on lt.cd_logradouro_tipo = l.cd_logradouro_tipo\n"
                    + "left join tb_bairro ba\n"
                    + "    on ba.cd_bairro = lb.cd_bairro\n"
                    + "left join tb_cidade cid\n"
                    + "    on cid.cd_cidade = ba.cd_cidade\n"
                    + "left join tb_estado uf\n"
                    + "    on uf.cd_estado = cid.cd_estado\n"
                    + "left join tb_logradouro_bairro lb_cob\n"
                    + "    on lb_cob.cd_logradouro_bairro = c.cd_logradouro_bairro_cobranca\n"
                    + "left join tb_logradouro l_cob\n"
                    + "    on l_cob.cd_logradouro = lb_cob.cd_logradouro\n"
                    + "left join tb_logradouro_tipo lt_cob\n"
                    + "    on lt_cob.cd_logradouro_tipo = l_cob.cd_logradouro_tipo\n"
                    + "left join tb_bairro ba_cob\n"
                    + "    on ba_cob.cd_bairro = lb_cob.cd_bairro\n"
                    + "left join tb_cidade cid_cob\n"
                    + "    on cid_cob.cd_cidade = ba_cob.cd_cidade\n"
                    + "left join tb_estado uf_cob\n"
                    + "    on uf_cob.cd_estado = cid_cob.cd_estado"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("id"));
                    imp.setCnpj(rst.getString("cpfcnpj"));

                    if ((rst.getString("rg") != null)
                            && (!rst.getString("rg").trim().isEmpty())) {
                        imp.setInscricaoestadual(rst.getString("rg"));
                    } else {
                        imp.setInscricaoestadual(rst.getString("inscricaoestadual"));
                    }

                    imp.setInscricaoMunicipal(rst.getString("inscricaomunicipal"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setCep(rst.getString("cep"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setMunicipioIBGE(rst.getInt("municipioibge"));
                    imp.setUf(rst.getString("uf"));

                    imp.setCobrancaEndereco(rst.getString("enderecocobranca"));
                    imp.setCobrancaNumero(rst.getString("numerocobranca"));
                    imp.setCobrancaComplemento(rst.getString("complementocobranca"));
                    imp.setCobrancaBairro(rst.getString("bairrocobranca"));
                    imp.setCobrancaCep(rst.getString("cepcobranca"));
                    imp.setCobrancaMunicipio(rst.getString("municipiocobranca"));
                    imp.setCobrancaMunicipioIBGE(rst.getInt("municipioibgecobranca"));
                    imp.setCobrancaUf(rst.getString("ufcobranca"));

                    imp.setDataCadastro(rst.getDate("dt_inclusao"));
                    imp.setDataNascimento(rst.getDate("datanascimento"));
                    imp.setObservacao(rst.getString("ds_obs") + " " + rst.getString("observacao"));
                    imp.setAtivo("A".equals(rst.getString("situacaocadastro").trim()));

                    try (ResultSet rst3 = stm.executeQuery(
                            "select\n"
                            + "    car.nm_carteira as tipovalorlimite,\n"
                            + "    coalesce(cc.vl_limite, 0) as valorlimite\n"
                            + "from tb_pessoa pe\n"
                            + "join tb_cliente c\n"
                            + "    on c.cd_pessoa_cliente = pe.cd_pessoa\n"
                            + "left join tb_cliente_carteira cc\n"
                            + "    on cc.cd_pessoa_cliente = c.cd_pessoa_cliente\n"
                            + "left join tb_carteira car\n"
                            + "    on car.cd_carteira = cc.cd_carteira\n"
                            + "where pe.cd_pessoa = " + imp.getId() + "\n"
                            + "and car.cd_carteira = 3"
                    )) {
                        if (rst3.next()) {
                            imp.setValorLimite(rst3.getDouble("valorlimite"));
                        }
                    }

                    imp.setPermiteCheque(true);
                    imp.setPermiteCreditoRotativo(true);

                    imp.setEmpresa(rst.getString("empresa"));
                    imp.setDataAdmissao(rst.getDate("dataadmissao"));
                    imp.setCargo(rst.getString("cargo"));
                    imp.setSalario(rst.getDouble("salario"));

                    imp.setNomeMae(rst.getString("mae"));
                    imp.setNomePai(rst.getString("pai"));

                    if ("C".equals(rst.getString("estadocivil").trim())) {
                        imp.setEstadoCivil(TipoEstadoCivil.CASADO);
                    } else if ("D".equals(rst.getString("estadocivil").trim())) {
                        imp.setEstadoCivil(TipoEstadoCivil.DIVORCIADO);
                    } else if ("O".equals(rst.getString("estadocivil").trim())) {
                        imp.setEstadoCivil(TipoEstadoCivil.OUTROS);
                    } else if ("S".equals(rst.getString("estadocivil").trim())) {
                        imp.setEstadoCivil(TipoEstadoCivil.SOLTEIRO);
                    } else {
                        imp.setEstadoCivil(TipoEstadoCivil.VIUVO);
                    }

                    if ((rst.getString("sexo") != null)
                            && (!rst.getString("sexo").trim().isEmpty())) {

                        if ("F".equals(rst.getString("sexo").trim())) {
                            imp.setSexo(TipoSexo.FEMININO);
                        } else {
                            imp.setSexo(TipoSexo.MASCULINO);
                        }
                    }

                    try (ResultSet rst2 = stm.executeQuery(
                            "select\n"
                            + "    pe.cd_pessoa as id,\n"
                            + "    ct.ds_contato_tipo as tipocontato,\n"
                            + "    pc.ds_pessoa_contato as contato\n"
                            + "from tb_pessoa pe\n"
                            + "join tb_cliente c\n"
                            + "    on c.cd_pessoa_cliente = pe.cd_pessoa\n"
                            + "join tb_pessoa_contato pc\n"
                            + "    on pc.cd_pessoa = pe.cd_pessoa\n"
                            + "join tb_contato_tipo ct\n"
                            + "    on ct.cd_contato_tipo = pc.cd_contato_tipo \n"
                            + "where pe.cd_pessoa = " + imp.getId()
                    )) {
                        while (rst2.next()) {

                            if ("Fone Residencial".equals(rst2.getString("tipocontato"))) {
                                imp.setTelefone(rst2.getString("contato"));
                            }
                            if ("Celular".equals(rst2.getString("tipocontato"))) {
                                imp.setCelular(rst2.getString("contato"));
                            }
                            if ("Email".equals(rst2.getString("tipocontato"))) {
                                imp.setEmail(rst2.getString("contato"));
                            }
                            if ("Email NFe".equals(rst2.getString("tipocontato"))) {
                                imp.addEmail(rst2.getString("contato"), TipoContato.NFE);
                            }
                            if ("Fone Comercial".equals(rst2.getString("tipocontato"))) {
                                imp.addTelefone("TEL COMERCIAL", rst2.getString("contato"));
                            }
                        }
                    }

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
                    + "    r.cd_receber as id,\n"
                    + "    r.cd_pessoa as idcliente,\n"
                    + "    r.nr_documento as numerocupom,\n"
                    + "    r.dt_emissao as dataemissao,\n"
                    + "    i.dt_vencimento as datavencimento,\n"
                    + "    r.vl_receber as valortotal,\n"
                    + "    i.vl_parcela as valorparcela,\n"
                    + "    i.vl_juros as valorjuros,\n"
                    + "    i.vl_saldo,\n"
                    + "    i.nr_parcela as numeroparcela,\n"
                    + "    r.fg_situacao as situacao_r,\n"
                    + "    i.fg_situacao as situacao_i,\n"
                    + "    r.ds_historico as historico,\n"
                    + "    r.ds_observacao as observacao,\n"
                    + "    r.cd_carteira,\n"
                    + "    r.cd_tipo_documento,\n"
                    + "    doc.ds_tipo_documento as documento\n"
                    + "from tb_receber r\n"
                    + "join tb_receber_item i\n"
                    + "    on i.cd_receber = r.cd_receber\n"
                    + "join tb_tipo_documento doc\n"
                    + "    on doc.cd_tipo_documento = r.cd_tipo_documento\n"
                    + "where r.fg_situacao = 'A' \n"
                    + "and i.fg_situacao in ('A', 'P')\n"
                    + "and r.cd_carteira in (2, 3, 15)"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rst.getString("idcliente")
                            + rst.getString("numerocupom")
                            + rst.getString("datavencimento")
                            + rst.getString("numeroparcela")
                            + rst.getString("valorparcela"));
                    imp.setIdCliente(rst.getString("idcliente"));
                    imp.setNumeroCupom(rst.getString("numerocupom"));
                    imp.setDataEmissao(rst.getDate("dataemissao"));
                    imp.setDataVencimento(rst.getDate("datavencimento"));
                    imp.setValor(rst.getDouble("vl_saldo"));
                    imp.setJuros(rst.getDouble("valorjuros"));
                    imp.setParcela(rst.getInt("numeroparcela"));
                    imp.setObservacao("VALOR TOTAL CONTA " + rst.getString("valortotal") + " PARCELA " + rst.getString("numeroparcela")
                            + "..." + rst.getString("historico") + " " + rst.getString("observacao") + " - SITUACAO " + rst.getString("situacao_i"));
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
                    "select\n"
                    + "    ch.cd_cheque_terceiro as id,\n"
                    + "    ch.nr_cheque as numerocheque,\n"
                    + "    ch.vl_cheque as valor,\n"
                    + "    ch.dt_emissao as dataemissao,\n"
                    + "    ch.dt_deposito as datadeposito,\n"
                    + "    ch.dt_alteracao as dataalteracao,\n"
                    + "    ch.ds_cmc7 as cmc7,\n"
                    + "    ch.ds_observacao as observacao,\n"
                    + "    ch.cd_alinea as alinea,\n"
                    + "    ch.cd_pessoa_conta_terceiro,\n"
                    + "    pct.cd_banco as banco,\n"
                    + "    bc.nr_banco as idbanco,\n"
                    + "    bc.nm_banco as nomebanco,\n"
                    + "    pct.nr_conta as numeroconta,\n"
                    + "    pct.dg_conta as digitoconta,\n"
                    + "    pct.nr_agencia as numeroagencia,\n"
                    + "    pct.dg_agencia as digitoagencia,\n"
                    + "    p.nm_pessoa as nome,\n"
                    + "    p.nr_cpf_cnpj as cnpjcpf,\n"
                    + "    p.nr_rg as rg,\n"
                    + "    p.nr_ie as inscricaoestadual,\n"
                    + "    ch.fg_baixado,\n"
                    + "    ch.fg_situacao\n"
                    + "from tb_cheque_terceiro ch\n"
                    + "left join tb_pessoa_conta_terceiro pct\n"
                    + "    on pct.cd_pessoa_conta_terceiro = ch.cd_pessoa_conta_terceiro\n"
                    + "left join tb_pessoa p\n"
                    + "    on p.cd_pessoa = pct.cd_pessoa\n"
                    + "left join tb_banco bc\n"
                    + "    on bc.cd_banco = pct.cd_banco\n"
                    + "where ch.fg_baixado = 'N'\n"
                    + "and ch.fg_situacao = 'A'"
            )) {
                while (rst.next()) {
                    ChequeIMP imp = new ChequeIMP();
                    imp.setId(rst.getString("id"));
                    imp.setNumeroCheque(rst.getString("numerocheque"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setDate(rst.getDate("dataemissao"));
                    imp.setDataDeposito(rst.getDate("datadeposito"));
                    imp.setDataHoraAlteracao(rst.getTimestamp("dataalteracao"));
                    imp.setCmc7(rst.getString("cmc7"));
                    imp.setObservacao(rst.getString("observacao"));
                    imp.setBanco(rst.getInt("idbanco"));
                    imp.setAgencia(rst.getString("numeroagencia") + " " + rst.getString("digitoagencia"));
                    imp.setConta(rst.getString("numeroconta") + " " + rst.getString("digitoconta"));
                    imp.setNome(rst.getString("nome"));
                    imp.setCpf(rst.getString("cnpjcpf"));

                    if ((rst.getString("rg") != null)
                            && (!rst.getString("rg").trim().isEmpty())) {
                        imp.setRg(rst.getString("rg"));
                    } else {
                        imp.setRg(rst.getString("inscricaoestadual"));
                    }

                    result.add(imp);
                }
            }
        }
        return result;
    }

    public void importarIcmsCodAntProduto() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        result = getProdutos();

        if (!result.isEmpty()) {
            gravarIcmsCodAntProduto(result);
        }
    }

    public void gravarIcmsCodAntProduto(List<ProdutoIMP> imp) throws Exception {

        Conexao.begin();
        Statement stm;
        String sql = "";

        ProgressBar.setStatus("Gravando Icms implantacao.codant_produto...");
        ProgressBar.setMaximum(imp.size());

        try {
            stm = Conexao.createStatement();

            for (ProdutoIMP i_imp : imp) {

                sql = "update implantacao.codant_produto set \n"
                        + "icmsdebitoid = '" + i_imp.getIcmsDebitoId() + "', \n"
                        + "icmsdebitoforaestadoid = '" + i_imp.getIcmsDebitoForaEstadoId() + "', \n"
                        + "icmsdebitoforaestadonfid = '" + i_imp.getIcmsDebitoForaEstadoNfId() + "', \n"
                        + "icmscreditoid = '" + i_imp.getIcmsCreditoId() + "', \n"
                        + "icmscreditoforaestadoid = '" + i_imp.getIcmsCreditoForaEstadoId() + "',\n"
                        + "icmsconsumidorid = '" + i_imp.getIcmsConsumidorId() + "' \n"
                        + "where impid = '" + i_imp.getImportId() + "' \n"
                        + "and impsistema = '" + getSistema() + "' \n"
                        + "and imploja = '" + getLojaOrigem() + "';";

                stm.execute(sql);

                ProgressBar.next();

            }

            stm.close();
            Conexao.commit();

        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }
}
