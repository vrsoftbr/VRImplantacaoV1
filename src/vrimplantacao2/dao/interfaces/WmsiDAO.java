/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import vrframework.classe.Conexao;
import vrframework.remote.ItemComboVO;
import vrimplantacao.classe.ConexaoOracle;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoEmpresa;
import vrimplantacao2.vo.enums.TipoFornecedor;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

public class WmsiDAO extends InterfaceDAO implements MapaTributoProvider {

    public String v_tipoDocumentoRotativo;
    public String v_tipoDocumentoCheque;

    @Override
    public String getSistema() {
        return "Wmsi";
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "pes_codigo codempresa,\n"
                    + "loj_fantasia nomeempresa\n"
                    + "from tab_lojas"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("codempresa"), rst.getString("nomeempresa")));
                }
            }
        }
        return result;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "distinct\n"
                    + "tri.TRI_CODIGO,\n"
                    + "tri.TRI_CST as csticms,\n"
                    + "('CST: '||tri.TRI_CST||' TRIBUTACAO: '||tri.tri_nome) as nometribut\n"
                    + "from TAB_TRIBUTACAO tri\n"
                    + "inner join TAB_FAMILIA fam on fam.fam_cod_tribut_entrada = tri.TRI_CODIGO\n"
                    + "order by tri.TRI_CODIGO"
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(rst.getString("TRI_CODIGO"), rst.getString("nometribut")));
                }
            }
        }
        return result;
    }

    public List<ItemComboVO> getTipoDocumento() throws Exception {
        List<ItemComboVO> result = new ArrayList<>();
        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "tipd_codigo, \n"
                    + "tipd_descricao \n"
                    + "from V_CONTAS_RECEBER\n"
                    + "order by tipd_codigo"
            )) {
                while (rst.next()) {
                    result.add(new ItemComboVO(rst.getInt("tipd_codigo"),
                            rst.getString("tipd_codigo") + " - "
                            + rst.getString("tipd_descricao")));
                }
            }
        }
        return result;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "fam_codigo,\n"
                    + "fam_nome\n"
                    + "from tab_familia\n"
                    + "order by fam_codigo"
            )) {
                while (rst.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("fam_codigo"));
                    imp.setDescricao(rst.getString("fam_nome"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "m1.cat_codigo cod_merc1, m1.cat_nome desc_merc1,\n"
                    + "m2.cat_codigo cod_merc2, m2.cat_nome desc_merc2,\n"
                    + "m3.cat_codigo cod_merc3, m3.cat_nome desc_merc3\n"
                    + "from TAB_CATEGORIA m1\n"
                    + "inner join TAB_CATEGORIA m2 on m2.CAT_MAE = m1.CAT_CODIGO\n"
                    + "inner join TAB_CATEGORIA m3 on m3.CAT_MAE = m2.CAT_CODIGO\n"
                    + "order by m1.cat_codigo"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rst.getString("cod_merc1"));
                    imp.setMerc1Descricao(rst.getString("desc_merc1"));
                    imp.setMerc2ID(rst.getString("cod_merc2"));
                    imp.setMerc2Descricao(rst.getString("desc_merc2"));
                    imp.setMerc3ID(rst.getString("cod_merc3"));
                    imp.setMerc3Descricao(rst.getString("desc_merc3"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "pro.pro_codigo,\n"
                    + "bar.CODP_CODIGO,\n"
                    + "bar.CODP_VALIDADE,\n"
                    + "bal.CODBALANCA,\n"
                    + "bal.DESCRICAO descricaobalanca,\n"
                    + "bal.PRECOBALANCA,\n"
                    + "bal.VALIDADE,\n"
                    + "pro.fam_codigo,\n"
                    + "pro.pro_nome,\n"
                    + "pro.pro_desc_reduzida,\n"
                    + "pro.pro_ref_fabricante,\n"
                    + "pro.pro_cod_fabricante,\n"
                    + "pro.pro_dias_validade,\n"
                    + "pro.pro_exclusao,\n"
                    + "pro.nbm_codigo,\n"
                    + "fam.CEST,\n"
                    + "to_char(pro.pro_datacad, 'YYYY-MM-DD') pro_datacad,\n"
                    + "pro.pro_cod_associado,\n"
                    + "pro.pro_unidade_uso,\n"
                    + "pro.pro_unid_medida,\n"
                    + "pre.PRE_PRECO_BASE precovenda,\n"
                    + "cus.cusl_ult_custo_aquisicao custo,\n"
                    + "fam.fam_cod_tribut_entrada as codtribut,\n"
                    + "tri.TRI_CST csticms,\n"
                    + "tri.tri_nome as nometribut\n"
                    + "from TAB_PRODUTO pro\n"
                    + "left join tab_preco_produto pre on pre.pro_codigo = pro.pro_codigo\n"
                    + "left join TAB_CODPROD bar on bar.pro_codigo = pro.PRO_CODIGO\n"
                    + "left join TAB_CUSLOJAS cus on cus.PRO_CODIGO = pro.PRO_CODIGO\n"
                    + "left join TAB_FAMILIA fam on fam.fam_codigo = pro.fam_codigo\n"
                    + "left join TAB_TRIBUTACAO tri on tri.TRI_CODIGO = fam.fam_cod_tribut_entrada\n"
                    + "left join V_GERA_PLUTOLEDO bal on bal.pro_codigo = pro.pro_codigo "
            )) {
                while (rst.next()) {

                    ProdutoIMP imp = new ProdutoIMP();

                    if ((rst.getString("CODBALANCA") != null)
                            && (!rst.getString("CODBALANCA").trim().isEmpty())) {

                        if ((rst.getString("CODP_CODIGO") != null)
                                && (!rst.getString("CODP_CODIGO").trim().isEmpty())) {

                            if (rst.getString("CODBALANCA").equals(rst.getString("CODP_CODIGO"))) {
                                imp.seteBalanca(true);
                            } else {
                                imp.seteBalanca(false);
                            }
                        } else {
                            imp.seteBalanca(false);
                        }
                    } else {
                        imp.seteBalanca(false);
                    }

                    if ((rst.getString("pro_unidade_uso") != null)
                            && (!rst.getString("pro_unidade_uso").trim().isEmpty())) {
                        if ((rst.getString("pro_unidade_uso").contains("QUIL"))
                                || (rst.getString("pro_unidade_uso").contains("KG"))) {
                            imp.setTipoEmbalagem("KG");
                        } else {
                            imp.setTipoEmbalagem("UN");
                        }
                    } else {
                        imp.setTipoEmbalagem("UN");
                    }

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("pro_codigo"));
                    imp.setEan(Utils.formataNumero(rst.getString("CODP_CODIGO")));
                    imp.setDescricaoCompleta(rst.getString("pro_nome"));
                    imp.setDescricaoReduzida(rst.getString("pro_desc_reduzida"));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setNcm(rst.getString("nbm_codigo"));
                    imp.setCest(rst.getString("CEST"));
                    imp.setDataCadastro(rst.getDate("pro_datacad"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setCustoComImposto(rst.getDouble("custo"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setValidade(rst.getInt("VALIDADE"));
                    imp.setIdFamiliaProduto(rst.getString("fam_codigo"));
                    imp.setIcmsDebitoId(rst.getString("codtribut"));
                    imp.setIcmsCreditoId(rst.getString("codtribut"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<ProdutoIMP> getProdutos(OpcaoProduto opcao) throws Exception {
        if (opcao == OpcaoProduto.ESTOQUE) {
            List<ProdutoIMP> vResult = new ArrayList<>();
            try (Statement stm = ConexaoOracle.createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "SELECT \n"
                        + "procodigo, \n"
                        + "quantidade \n"
                        + "FROM TAB_SALDOESTOQUE\n"
                        + "JOIN (SELECT procodigo, MAX(DATA) DATA \n"
                        + "        FROM TAB_SALDOESTOQUE\n"
                        + "      GROUP BY procodigo) A USING (procodigo, DATA) \n"
                        + "ORDER BY procodigo"
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("procodigo"));
                        imp.setEstoque(rst.getDouble("quantidade"));
                        vResult.add(imp);
                    }
                    return vResult;
                }
            }
        } else if (opcao == OpcaoProduto.MERCADOLOGICO) {
            List<ProdutoIMP> vResult = new ArrayList<>();
            try (Statement stm = ConexaoOracle.createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select\n"
                        + "pro.pro_codigo, pro.pro_nome,\n"
                        + "fam.fam_codigo, fam.fam_nome,\n"
                        + "m1.cat_codigo cod_merc1, m1.cat_nome desc_merc1,\n"
                        + "m2.cat_codigo cod_merc2, m2.cat_nome desc_merc2,\n"
                        + "m3.cat_codigo cod_merc3, m3.cat_nome desc_merc3\n"
                        + "from TAB_CATEGORIA m1\n"
                        + "inner join TAB_CATEGORIA m2 on m2.CAT_MAE = m1.CAT_CODIGO\n"
                        + "inner join TAB_CATEGORIA m3 on m3.CAT_MAE = m2.CAT_CODIGO\n"
                        + "inner join tab_familia fam on fam.cat_codigo = m3.cat_codigo\n"
                        + "inner join tab_produto pro on pro.FAM_CODIGO = fam.FAM_CODIGO\n"
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("pro_codigo"));
                        imp.setCodMercadologico1(rst.getString("cod_merc1"));
                        imp.setCodMercadologico2(rst.getString("cod_merc2"));
                        imp.setCodMercadologico3(rst.getString("cod_merc3"));
                        vResult.add(imp);
                    }
                    return vResult;
                }
            }
        } else if (opcao == OpcaoProduto.ATIVO) {
            List<ProdutoIMP> vResult = new ArrayList<>();
            try (Statement stm = ConexaoOracle.createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select PRO_CODIGO, DESCSITUACAO, sitpis \n"
                        + "from V_SITUACAOPRODUTO "
                        + "where CODLOJA = " + getLojaOrigem()
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("PRO_CODIGO"));
                        imp.setSituacaoCadastro(rst.getString("DESCSITUACAO").contains("Ativo") ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                        vResult.add(imp);
                    }
                    return vResult;
                }
            }
        } else if (opcao == OpcaoProduto.PIS_COFINS) {
            List<ProdutoIMP> vResult = new ArrayList<>();
            try (Statement stm = ConexaoOracle.createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select PRO_CODIGO, DESCSITUACAO, sitpis \n"
                        + "from V_SITUACAOPRODUTO "
                        + "where CODLOJA = " + getLojaOrigem()
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("PRO_CODIGO"));
                        if (rst.getString("sitpis").contains("Isento")) {
                            imp.setPiscofinsCstDebito(7);
                            imp.setPiscofinsCstCredito(71);
                        } else if (rst.getString("sitpis").contains("Trib")) {
                            imp.setPiscofinsCstDebito(1);
                            imp.setPiscofinsCstCredito(50);
                        } else {
                            imp.setPiscofinsCstDebito(7);
                            imp.setPiscofinsCstCredito(71);
                        }
                        vResult.add(imp);
                    }
                    return vResult;
                }
            }
        }
        return null;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "distinct\n"
                    + "f.pes_codigo,\n"
                    + "f.for_ativo,\n"
                    + "f.for_exclusao,\n"
                    + "f.for_periodo_visita,\n"
                    + "f.for_prazo_entrega,\n"
                    + "f.for_fantasia,\n"
                    + "f.for_indprodrural,\n"
                    + "f.for_supersimples,\n"
                    + "f.FOR_DISTRIBUIDOR,\n"
                    + "f.FOR_MICRO_ST,\n"
                    + "p.pes_nome,\n"
                    + "p.pes_complemento,\n"
                    + "p.pes_ddd_celular,\n"
                    + "p.pes_telefone,\n"
                    + "p.pes_email,\n"
                    + "p.pes_celular,\n"
                    + "p.pes_rua,\n"
                    + "r.rua_codigo,\n"
                    + "r.rua_nome,\n"
                    + "p.pes_cep,\n"
                    + "r.rua_cep,\n"
                    + "p.pes_cidade,\n"
                    + "c.CID_CODIGO,\n"
                    + "c.CID_NOME,\n"
                    + "c.COD_IBGE,\n"
                    + "p.pes_bairro,\n"
                    + "b.BAI_CODIGO,\n"
                    + "b.BAI_NOME,\n"
                    + "p.pes_uf,\n"
                    + "p.pes_renda,\n"
                    + "p.pes_ddd,\n"
                    + "p.pes_ddd2,\n"
                    + "p.pes_telefone2,\n"
                    + "p.pes_ddd_fax,\n"
                    + "p.pes_nro_fax,\n"
                    + "p.pes_numero,\n"
                    + "p.pes_cgc_cpf, \n"
                    + "d.CGCS_INSCRICAO_ESTADUAL,\n"
                    + "d.CGCS_INSCRICAO_MUNICIPAL,\n"
                    + "d.CGCS_INSC_RURAL,\n"
                    + "p.pes_ddd3,\n"
                    + "p.pes_telefone3,\n"
                    + "p.pes_ddd4,\n"
                    + "p.pes_telefone4,\n"
                    + "p.PES_OBSERVACAO,\n"
                    + "p.PES_OBSERVACOES\n"
                    + "from TAB_FORNECEDOR f \n"
                    + "inner join tab_pessoa p on p.pes_codigo = f.pes_codigo\n"
                    + "left join tab_cgcs d on d.PES_CODIGO = p.PES_CODIGO\n"
                    + "left join tab_rua r on r.RUA_CODIGO = p.PES_RUA\n"
                    + "left join tab_bairro b on b.BAI_CODIGO = p.PES_BAIRRO\n"
                    + "left join tab_cidade c on c.CID_CODIGO = p.PES_CIDADE \n"
                    + "where b.BAI_CODIGO = r.BAI_CODIGO \n"
                    + "and c.CID_CODIGO = r.CID_CODIGO\n"
                    + "and r.BAI_CODIGO = b.BAI_CODIGO\n"
                    + "and r.CID_CODIGO = c.CID_CODIGO\n"
                    + "order by pes_codigo"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("pes_codigo"));
                    imp.setRazao(rst.getString("pes_nome"));
                    imp.setFantasia(rst.getString("for_fantasia"));
                    imp.setCnpj_cpf(rst.getString("pes_cgc_cpf"));
                    imp.setIe_rg(rst.getString("CGCS_INSCRICAO_ESTADUAL"));
                    imp.setInsc_municipal(rst.getString("CGCS_INSCRICAO_MUNICIPAL"));
                    imp.setAtivo("A".equals(rst.getString("for_ativo")));
                    imp.setEndereco(rst.getString("rua_nome"));
                    imp.setNumero(rst.getString("pes_numero"));
                    imp.setComplemento(rst.getString("pes_complemento"));
                    imp.setCep(rst.getString("rua_cep"));
                    imp.setBairro(rst.getString("bai_nome"));
                    imp.setMunicipio(rst.getString("cid_nome"));
                    imp.setUf(rst.getString("pes_uf"));
                    imp.setTel_principal((rst.getString("pes_ddd") == null ? "" : rst.getString("pes_ddd").trim()) + rst.getString("pes_telefone"));
                    imp.setObservacao(rst.getString("PES_OBSERVACAO"));

                    if ((rst.getString("for_periodo_visita") != null)
                            && (!rst.getString("for_periodo_visita").trim().isEmpty())) {
                        imp.setObservacao(imp.getObservacao() + " PERIODO VISITA " + rst.getString("for_periodo_visita"));
                    }

                    if ((rst.getString("for_indprodrural") != null)
                            && (!rst.getString("for_indprodrural").trim().isEmpty())
                            && ("1".equals(rst.getString("for_indprodrural").trim()))) {
                        imp.setTipoEmpresa(TipoEmpresa.PRODUTOR_RURAL_JURIDICO);
                    } else if ((rst.getString("for_supersimples") != null)
                            && (!rst.getString("for_supersimples").trim().isEmpty())
                            && ("1".equals(rst.getString("for_supersimples").trim()))) {
                        imp.setTipoEmpresa(TipoEmpresa.EPP_SIMPLES);
                    } else if ((rst.getString("FOR_MICRO_ST") != null)
                            && (!rst.getString("FOR_MICRO_ST").trim().isEmpty())
                            && ("1".equals(rst.getString("FOR_MICRO_ST").trim()))) {
                        imp.setTipoEmpresa(TipoEmpresa.ME_SIMPLES);
                    } else {
                        imp.setTipoEmpresa(TipoEmpresa.LUCRO_REAL);
                    }

                    if ((rst.getString("FOR_DISTRIBUIDOR") != null)
                            && (!rst.getString("FOR_DISTRIBUIDOR").trim().isEmpty())
                            && ("1".equals(rst.getString("FOR_DISTRIBUIDOR").trim()))) {
                        imp.setTipoFornecedor(TipoFornecedor.DISTRIBUIDOR);
                    } else {
                        imp.setTipoFornecedor(TipoFornecedor.INDUSTRIA);
                    }

                    if ((rst.getString("pes_celular") != null)
                            && (!rst.getString("pes_celular").trim().isEmpty())) {
                        imp.addContato(
                                "1",
                                "CELULAR",
                                null,
                                rst.getString("pes_celular").trim(),
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    if ((rst.getString("pes_telefone2") != null)
                            && (!rst.getString("pes_telefone2").trim().isEmpty())) {
                        imp.addContato(
                                "2",
                                "TELEFONE 2",
                                (rst.getString("pes_ddd2") == null ? "" : rst.getString("pes_ddd2").trim()) + rst.getString("pes_telefone2"),
                                null,
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    if ((rst.getString("pes_nro_fax") != null)
                            && (!rst.getString("pes_nro_fax").trim().isEmpty())) {
                        imp.addContato(
                                "3",
                                "FAX",
                                (rst.getString("pes_ddd_fax") == null ? "" : rst.getString("pes_ddd_fax")) + rst.getString("pes_nro_fax"),
                                null,
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    if ((rst.getString("pes_telefone3") != null)
                            && (!rst.getString("pes_telefone3").trim().isEmpty())) {
                        imp.addContato(
                                "4",
                                "TELEFONE 3",
                                (rst.getString("pes_ddd3") == null ? "" : rst.getString("pes_ddd3")) + rst.getString("pes_telefone3").trim(),
                                null,
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    if ((rst.getString("pes_telefone4") != null)
                            && (!rst.getString("pes_telefone4").trim().isEmpty())) {
                        imp.addContato(
                                "5",
                                "TELEFONE 4",
                                (rst.getString("pes_ddd4") == null ? "" : rst.getString("pes_ddd4")) + rst.getString("pes_telefone4").trim(),
                                null,
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "codigo as prod_cod,\n"
                    + "nome as prod_nome,\n"
                    + "codfornec as forn_cod,\n"
                    + "nomefornec forn_nome\n"
                    + "from V_PRODUTOFORNECEDOR"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdFornecedor(rst.getString("forn_cod"));
                    imp.setIdProduto(rst.getString("prod_cod"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "c.PES_CODIGO,\n"
                    + "c.CLI_CPF,\n"
                    + "c.cli_rg,\n"
                    + "c.CLI_CID_NATURALIDADE,\n"
                    + "c.CLI_UF_NATURALIDADE,\n"
                    + "c.cli_pai,\n"
                    + "c.cli_mae,\n"
                    + "c.cli_sexo,\n"
                    + "c.CLI_ESTADO_CIVIL,\n"
                    + "c.CLI_CONJUGE,\n"
                    + "c.CLI_RENDA_MENSAL,\n"
                    + "c.PROF_CODIGO,\n"
                    + "c.cli_ativo,\n"
                    + "c.cli_contato,\n"
                    + "c.CLI_OBSERVACAO,\n"
                    + "c.CLI_INS_MUNICIPAL,\n"
                    + "c.CLI_LIMITE_CREDITO,\n"
                    + "c.CLI_FANTASIA,\n"
                    + "p.pes_complemento,\n"
                    + "p.pes_ddd_celular,\n"
                    + "p.pes_telefone,\n"
                    + "p.pes_email,\n"
                    + "p.pes_celular,\n"
                    + "p.pes_rua,\n"
                    + "r.rua_codigo,\n"
                    + "r.rua_nome,\n"
                    + "p.pes_cep,\n"
                    + "r.rua_cep,\n"
                    + "p.pes_cidade,\n"
                    + "cid.CID_CODIGO,\n"
                    + "cid.CID_NOME,\n"
                    + "cid.COD_IBGE,\n"
                    + "p.pes_bairro,\n"
                    + "b.BAI_CODIGO,\n"
                    + "b.BAI_NOME,\n"
                    + "p.pes_uf,\n"
                    + "p.pes_ddd,\n"
                    + "p.pes_ddd2,\n"
                    + "p.pes_telefone2,\n"
                    + "p.pes_ddd_fax,\n"
                    + "p.pes_nro_fax,\n"
                    + "p.pes_numero,\n"
                    + "p.pes_ddd3,\n"
                    + "p.pes_telefone3,\n"
                    + "p.pes_ddd4,\n"
                    + "p.pes_telefone4,\n"
                    + "p.PES_OBSERVACAO,\n"
                    + "p.PES_OBSERVACOES,\n"
                    + "p.pes_nome "
                    + "from TAB_CLIENTE c\n"
                    + "inner join tab_pessoa p on p.pes_codigo = c.pes_codigo\n"
                    + "left join tab_rua r on r.RUA_CODIGO = p.PES_RUA\n"
                    + "left join tab_bairro b on b.BAI_CODIGO = p.PES_BAIRRO\n"
                    + "left join tab_cidade cid on cid.CID_CODIGO = p.PES_CIDADE \n"
                    + "where b.BAI_CODIGO = r.BAI_CODIGO \n"
                    + "and cid.CID_CODIGO = r.CID_CODIGO\n"
                    + "and r.BAI_CODIGO = b.BAI_CODIGO\n"
                    + "and r.CID_CODIGO = cid.CID_CODIGO\n"
                    + "order by p.PES_CODIGO"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("PES_CODIGO"));
                    imp.setCnpj(rst.getString("CLI_CPF"));
                    imp.setInscricaoestadual(rst.getString("cli_rg"));
                    imp.setInscricaoMunicipal(rst.getString("CLI_INS_MUNICIPAL"));
                    imp.setRazao(rst.getString("pes_nome"));
                    imp.setFantasia(rst.getString("CLI_FANTASIA"));
                    imp.setEndereco(rst.getString("rua_nome"));
                    imp.setNumero(rst.getString("pes_numero"));
                    imp.setComplemento(rst.getString("pes_complemento"));
                    imp.setCep(rst.getString("rua_cep"));
                    imp.setBairro(rst.getString("BAI_NOME"));
                    imp.setMunicipio(rst.getString("CID_NOME"));
                    imp.setUf(rst.getString("pes_uf"));

                    imp.setEmail(rst.getString("pes_email") == null ? "" : rst.getString("pes_email").toLowerCase());
                    imp.setTelefone((rst.getString("pes_ddd") == null ? "" : rst.getString("pes_ddd"))
                            + (rst.getString("pes_telefone") == null ? "" : rst.getString("pes_telefone").trim()));
                    imp.setCelular((rst.getString("pes_ddd_celular") == null ? "" : rst.getString("pes_ddd_celular").trim())
                            + (rst.getString("pes_celular") == null ? "" : rst.getString("pes_celular").trim()));

                    imp.setNomePai(rst.getString("cli_pai"));
                    imp.setNomeMae(rst.getString("cli_mae"));
                    imp.setNomeConjuge(rst.getString("CLI_CONJUGE"));
                    imp.setSalario(rst.getDouble("CLI_RENDA_MENSAL"));
                    imp.setAtivo("A".equals(rst.getString("cli_ativo")));
                    imp.setObservacao(rst.getString("CLI_OBSERVACAO"));
                    imp.setValorLimite(rst.getDouble("CLI_LIMITE_CREDITO"));
                    imp.setObservacao(rst.getString("PES_OBSERVACAO"));
                    imp.setObservacao2(rst.getString("PES_OBSERVACOES")
                            + (rst.getString("cli_contato") == null ? "" : "CONTATO " + rst.getString("cli_contato").trim()));

                    if ((rst.getString("pes_nro_fax") != null)
                            && (!rst.getString("pes_nro_fax").trim().isEmpty())) {
                        imp.addContato(
                                "1",
                                "FAX",
                                (rst.getString("pes_ddd_fax") == null ? "" : rst.getString("pes_ddd_fax"))
                                + rst.getString("pes_nro_fax").trim(),
                                null,
                                null
                        );
                    }
                    if ((rst.getString("pes_telefone2") != null)
                            && (!rst.getString("pes_telefone2").trim().isEmpty())) {
                        imp.addContato(
                                "2",
                                "TELEFONE 2",
                                (rst.getString("pes_ddd2") == null ? "" : rst.getString("pes_ddd2"))
                                + rst.getString("pes_telefone2").trim(),
                                null,
                                null
                        );
                    }
                    if ((rst.getString("pes_telefone3") != null)
                            && (!rst.getString("pes_telefone3").trim().isEmpty())) {
                        imp.addContato(
                                "3",
                                "TELEFONE 3",
                                (rst.getString("pes_ddd3") == null ? "" : rst.getString("pes_ddd3"))
                                + rst.getString("pes_telefone3").trim(),
                                null,
                                null
                        );
                    }
                    if ((rst.getString("pes_telefone4") != null)
                            && (!rst.getString("pes_telefone4").trim().isEmpty())) {
                        imp.addContato(
                                "4",
                                "TELEFONE 4",
                                (rst.getString("pes_ddd4") == null ? "" : rst.getString("pes_ddd4"))
                                + rst.getString("pes_telefone4").trim(),
                                null,
                                null
                        );
                    }
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "SEQ_RECTITULO, TIPD_CODIGO, REC_NUMERO,\n"
                    + "REC_DATA as DATAEMISSAO,\n"
                    + "REC_DATA_VENCIMENTO as DATAVENCIMENTO,\n"
                    + "REC_PES_CODIGO,\n"
                    + "REC_VALOR, \n"
                    + "REC_DESCONTO, \n"
                    + "REC_NOTA\n"
                    + "from TAB_RECTITULO \n"
                    + "where TIPD_CODIGO in (" + v_tipoDocumentoRotativo + ")\n"
                    + "and REC_LOJA_CODIGO = '" + getLojaOrigem() + "'\n"
                    + "and rec_valor_pago is null"
            )) {
                SimpleDateFormat format = new SimpleDateFormat("yy/MM/dd");
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rst.getString("SEQ_RECTITULO"));
                    imp.setNumeroCupom(rst.getString("REC_NOTA"));
                    imp.setDataEmissao(format.parse(rst.getString("DATAEMISSAO")));
                    imp.setDataVencimento(format.parse(rst.getString("DATAVENCIMENTO")));
                    imp.setIdCliente(rst.getString("REC_PES_CODIGO"));
                    imp.setValor(rst.getDouble("REC_VALOR"));
                    vResult.add(imp);
                    System.out.println(
                            "ID" + rst.getString("SEQ_RECTITULO") + "\n"
                            + "NOTA " + rst.getString("REC_NOTA") + "\n"
                            + "DATAEMISSAO " + rst.getString("DATAEMISSAO") + "\n"
                            + "DATAVENCIMENTO " + rst.getString("DATAVENCIMENTO") + "\n"
                            + "PESSOA " + rst.getString("REC_PES_CODIGO") + "\n"
                            + "VALOR " + rst.getString("REC_VALOR"));
                }
            }
        }
        return vResult;
    }

    public void importarProdutosBalanca() throws Exception {
        Conexao.begin();
        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery("select \n"
                    + "CODBALANCA, \n"
                    + "descricao,\n"
                    + "validade,\n"
                    + "case tipoproduto when '0' then 'P' else 'U' end tipoproduto \n"
                    + "from V_GERA_PLUTOLEDO \n"
                    + "where cod_loja = '" + getLojaOrigem() + "'")) {
                while (rst.next()) {
                    try (Statement stm2 = Conexao.createStatement()) {
                        stm2.execute("insert into implantacao.produtobalanca ("
                                + "codigo, descricao, validade, pesavel) "
                                + "values ("
                                + rst.getInt("CODBALANCA") + ", "
                                + "'" + Utils.acertarTexto(rst.getString("descricao")) + "', "
                                + +rst.getInt("validade") + ", "
                                + "'" + rst.getString("tipoproduto") + "')");
                    } catch (Exception ex) {
                        Conexao.rollback();
                        throw ex;
                    }
                }
                Conexao.commit();
            }
        }
    }
}
