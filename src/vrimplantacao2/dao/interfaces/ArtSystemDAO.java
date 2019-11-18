package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import vrimplantacao.classe.ConexaoMySQL;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;

/**
 *
 * @author lucasrafael
 */
public class ArtSystemDAO extends InterfaceDAO {

    private static final Logger LOG = Logger.getLogger(ArtSystemDAO.class.getName());

    @Override
    public String getSistema() {
        return "ArtSystem";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet(Arrays.asList(new OpcaoProduto[]{
            OpcaoProduto.IMPORTAR_MANTER_BALANCA,
            OpcaoProduto.PRODUTOS,
            OpcaoProduto.ATIVO,
            OpcaoProduto.DESC_COMPLETA,
            OpcaoProduto.DESC_GONDOLA,
            OpcaoProduto.DESC_REDUZIDA,
            OpcaoProduto.DATA_CADASTRO,
            OpcaoProduto.EAN,
            OpcaoProduto.EAN_EM_BRANCO,
            OpcaoProduto.TIPO_EMBALAGEM_EAN,
            OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
            OpcaoProduto.CUSTO,
            OpcaoProduto.MARGEM,
            OpcaoProduto.PRECO,
            OpcaoProduto.ESTOQUE,
            OpcaoProduto.PESAVEL,
            OpcaoProduto.NCM,
            OpcaoProduto.CEST,
            OpcaoProduto.ICMS,
            OpcaoProduto.PIS_COFINS,
            OpcaoProduto.NATUREZA_RECEITA,
            OpcaoProduto.ATACADO,
            OpcaoProduto.VALIDADE,
            OpcaoProduto.MERCADOLOGICO,
            OpcaoProduto.MERCADOLOGICO_PRODUTO,}));
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "LOJNID_ENT as codigo, \n"
                    + "LOJCCODLOJ as codloja,  \n"
                    + "LOJCEMLASU as nome\n"
                    + "from dbo.ASENTLOJ"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(
                            rst.getString("codigo"),
                            rst.getString("codloja") + " - " + rst.getString("nome")
                    ));
                }
            }
        }
        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	DEPNID_DEP as merc1, \n"
                    + "	DEPCDESCRI as desc_merc1\n"
                    + "from dbo.ASPRODEP\n"
                    + "where DEPNID_PAI is null\n"
                    + "order by DEPNID_DEP"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rst.getString("merc1"));
                    imp.setMerc1Descricao(rst.getString("desc_merc1"));
                    imp.setMerc2ID("1");
                    imp.setMerc2Descricao(imp.getMerc1Descricao());
                    imp.setMerc3ID("1");
                    imp.setMerc3Descricao(imp.getMerc1Descricao());
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select  \n"
                    + "	p.PRONID_PRO,\n"
                    + "	p.PROCCODPRO as id,\n"
                    + "	ean.EANCCODEAN as ean,\n"
                    + "	ean.EANNQTDEMB as qtdemb_ean,\n"
                    + "	p.PROCDESCRI as descricaocompleta,\n"
                    + "	p.PROCDESRES as descricaoresumida,\n"
                    + "	p.PROCCODNCM as ncm,\n"
                    + "	p.PROCCDCEST as cest,\n"
                    + "	inc.INCCCSTSAI as pis_saida,\n"
                    + "	inc.INCCCSTENT as pis_entrada,\n"
                    + "	icm_e.FIGCCODCST as cst_entrada,\n"
                    + "	icm_e.FIGNTRIALI as aliquota_entrada, \n"
                    + "	icm_e.FIGNTRIRED as reducao_entrada,\n"
                    + "	icm_s.FIGCCODCST as cst_saida,\n"
                    + "	icm_s.FIGNTRIALI as aliquota_saida, \n"
                    + "	icm_s.FIGNTRIRED as reducao_saida,\n"
                    + "	p.PRODDATCAD as datacadastro,\n"
                    + "	p.PRONPESBRU as peso_bruto,\n"
                    + "	p.PRONPESLIQ as peso_liquido,\n"
                    + "	est.ESTNESTATU as estoque,\n"
                    + "	est.ESTNESTMAX as estoque_maximo,\n"
                    + "	est.ESTNESTMIN as estoque_minimo,\n"
                    + "	pre.PRENCUSREP as custo,\n"
                    + "	pre.PRENVDAMRG as margem,\n"
                    + "	pre.PRENVDAATU as precovenda\n"
                    + "from dbo.ASPROPRO p\n"
                    + "left join dbo.ASPROEAN ean on ean.EANNID_PRO = p.PRONID_PRO\n"
                    + "left join dbo.ASPROEST est on est.ESTNID_PRO = p.PRONID_PRO and est.ESTNID_LOJ = " + getLojaOrigem() + "\n"
                    + "left join dbo.ASPROPRE pre on pre.PRENID_PRO = p.PRONID_PRO and pre.PRENID_LOJ = " + getLojaOrigem() + "\n"
                    + "left join dbo.ASPROINC inc on inc.INCNID_ING = pre.PRENID_INC and INCNID_INT = 121\n"
                    + "left join dbo.ASPROFIG icm_e on icm_e.FIGNIDFIGU = pre.PRENIDFIGU and icm_e.FIGCCODCFO is null and icm_e.FIGNTIPCFO = 1\n"
                    + "	and icm_e.FIGCTIPCAD is null and icm_e.FIGCORIGEM = 'SP' and icm_e.FIGCDESTIN = 'SP'\n"
                    + "left join dbo.ASPROFIG icm_s on icm_s.FIGNIDFIGU = pre.PRENIDFIGU and icm_s.FIGCCODCFO is null and icm_s.FIGNTIPCFO = 2\n"
                    + "	and icm_s.FIGCTIPCAD is null and icm_s.FIGCORIGEM = 'SP' and icm_s.FIGCDESTIN = 'SP'"
            )) {
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().carregarProdutosBalanca();
                while (rst.next()) {
                    if ((rst.getString("id") != null)
                            && (!rst.getString("id").trim().isEmpty())) {

                        ProdutoIMP imp = new ProdutoIMP();
                        ProdutoBalancaVO produtoBalanca;

                        String codigoBalanca = rst.getString("id").substring(0, rst.getString("id").trim().length() - 1);

                        long codigoProduto;
                        codigoProduto = Long.parseLong(codigoBalanca);
                        if (codigoProduto <= Integer.MAX_VALUE) {
                            produtoBalanca = produtosBalanca.get((int) codigoProduto);
                        } else {
                            produtoBalanca = null;
                        }

                        if (produtoBalanca != null) {
                            imp.seteBalanca(true);
                            imp.setValidade(produtoBalanca.getValidade() > 1 ? produtoBalanca.getValidade() : 0);
                        } else {
                            imp.seteBalanca(false);
                        }

                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("id"));

                        if (imp.isBalanca()) {
                            imp.setEan(codigoBalanca);
                        } else {
                            imp.setEan(rst.getString("ean"));
                        }

                        imp.setQtdEmbalagem(rst.getInt("qtdemb_ean"));
                        imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                        imp.setDescricaoReduzida(rst.getString("descricaoresumida"));
                        imp.setDescricaoGondola(imp.getDescricaoCompleta());
                        imp.setDataCadastro(rst.getDate("datacadastro"));
                        imp.setPesoBruto(rst.getDouble("peso_bruto"));
                        imp.setPesoLiquido(rst.getDouble("peso_liquido"));
                        imp.setMargem(rst.getDouble("margem"));
                        imp.setPrecovenda(rst.getDouble("precovenda"));
                        imp.setCustoComImposto(rst.getDouble("custo"));
                        imp.setCustoSemImposto(rst.getDouble("custo"));
                        imp.setEstoque(rst.getDouble("estoque"));
                        imp.setEstoqueMinimo(rst.getDouble("estoque_minimo"));
                        imp.setEstoqueMaximo(rst.getDouble("estoque_maximo"));
                        imp.setNcm(rst.getString("ncm"));
                        imp.setCest(rst.getString("cest"));
                        imp.setPiscofinsCstDebito(rst.getString("pis_saida"));
                        imp.setPiscofinsCstCredito(rst.getString("pis_entrada"));
                        imp.setIcmsCstSaida(rst.getInt("cst_saida"));
                        imp.setIcmsAliqSaida(rst.getDouble("aliquota_saida"));
                        imp.setIcmsReducaoSaida(rst.getDouble("reducao_saida"));
                        imp.setIcmsCstEntrada(rst.getInt("cst_entrada"));
                        imp.setIcmsAliqEntrada(rst.getDouble("aliquota_entrada"));
                        imp.setIcmsReducaoEntrada(rst.getDouble("reducao_entrada"));
                        result.add(imp);
                    }
                }
            }
            return result;
        }
    }

    @Override
    public List<ProdutoIMP> getProdutos(OpcaoProduto opt) throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        if (opt == OpcaoProduto.MERCADOLOGICO) {
            try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select \n"
                        + " p.PROCCODPRO as idproduto,\n"
                        + " (p.PRONID_DEP - 4) as merc\n"
                        + "from dbo.ASPROPRO p \n"
                        + "inner join dbo.ASPRODEP d on d.DEPNID_DEP = (p.PRONID_DEP - 4)"
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("idproduto"));
                        imp.setCodMercadologico1(rst.getString("merc"));
                        imp.setCodMercadologico2("1");
                        imp.setCodMercadologico3("1");
                        result.add(imp);
                    }
                }
                return result;
            }
        }
        return null;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	f.ENTNID_ENT as id,\n"
                    + "	f.ENTCCODCPF as cnpj,\n"
                    + "	f.ENTCCOD_RG as ie_rg,\n"
                    + "	f.ENTCNOMENT as razao,\n"
                    + "	f.ENTCAPELID as fantasia,\n"
                    + " cep.CEPCCODCEP as cep,\n"
                    + "	cep.CEPCDESCRI as endereco,\n"
                    + "	f.ENTNENDNUM as numero,\n"
                    + "	f.ENTCENDCOM as complemento,\n"
                    + "	f.ENTCE_MAIL as email,\n"
                    + " f2.FNCCCONPAG as condicao_pagamento,\n"
                    + "	bai.BAICDESCRI as bairro,\n"
                    + "	mun.MUNCDESCRI as municipio,\n"
                    + "	mun.MUNCCODIBG as municipio_ibge,\n"
                    + "	mun.MUNCCOD_UF as uf,\n"
                    + "	tel.TELNCODDDD as ddd,\n"
                    + "	tel.TELNNUMTEL as telefone,\n"
                    + "	tel.TELMOBSERV as tel_contato,\n"
                    + "	obs.OBSMOBSERV as observacao\n"
                    + "from dbo.ASENTENT f\n"
                    + "left join dbo.ASENTFNC f2 on f2.FNCNID_ENT = f.ENTNID_ENT\n"
                    + "left join dbo.ASCEPCEP cep on cep.CEPNID_CEP = f.ENTNID_CEP\n"
                    + "left join dbo.ASCEPBAI bai on bai.BAINID_BAI = cep.CEPNID_BAI\n"
                    + "left join dbo.ASCEPMUN mun on mun.MUNNID_MUN = bai.BAINID_MUN\n"
                    + "left join dbo.ASENTTEL tel on tel.TELNID_ENT = f.ENTNID_ENT\n"
                    + "left join dbo.ASENTOBS obs on obs.OBSNID_ENT = f.ENTNID_ENT and obs.OBSCTIPCAD = 'FNC'\n"
                    + "where f.ENTNID_ENT not in (select CLINID_ENT from dbo.ASENTCLI)"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    imp.setIe_rg(rst.getString("ie_rg"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setEndereco("RUA " + rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setCep(rst.getString("cep"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setIbge_municipio(rst.getInt("municipio_ibge"));
                    imp.setUf(rst.getString("uf"));
                    imp.setTel_principal(rst.getString("ddd") + rst.getString("telefone"));
                    imp.setObservacao(rst.getString("observacao"));

                    if ((rst.getString("condicao_pagamento") != null)
                            && (!rst.getString("condicao_pagamento").trim().isEmpty())) {

                        if ("/AV".equals(rst.getString("condicao_pagamento").trim())) {
                            imp.setCondicaoPagamento(0);
                        } else if ("0".equals(rst.getString("condicao_pagamento").trim())) {
                            imp.setCondicaoPagamento(0);
                        } else if ("0/AV".equals(rst.getString("condicao_pagamento").trim())) {
                            imp.setCondicaoPagamento(0);
                        } else if ("0/DD".equals(rst.getString("condicao_pagamento").trim())) {
                            imp.setCondicaoPagamento(0);
                        } else if ("1/AV".equals(rst.getString("condicao_pagamento").trim())) {
                            imp.setCondicaoPagamento(1);
                        } else if ("10/DD".equals(rst.getString("condicao_pagamento").trim())) {
                            imp.setCondicaoPagamento(10);
                        } else if ("13/DD".equals(rst.getString("condicao_pagamento").trim())) {
                            imp.setCondicaoPagamento(13);
                        } else if ("14/D".equals(rst.getString("condicao_pagamento").trim())) {
                            imp.setCondicaoPagamento(14);
                        } else if ("14/DD".equals(rst.getString("condicao_pagamento").trim())) {
                            imp.setCondicaoPagamento(14);
                        } else if ("15/DD".equals(rst.getString("condicao_pagamento").trim())) {
                            imp.setCondicaoPagamento(15);
                        } else if ("17/DD".equals(rst.getString("condicao_pagamento").trim())) {
                            imp.setCondicaoPagamento(17);
                        } else if ("18/DD".equals(rst.getString("condicao_pagamento").trim())) {
                            imp.setCondicaoPagamento(18);
                        } else if ("19/DD".equals(rst.getString("condicao_pagamento").trim())) {
                            imp.setCondicaoPagamento(19);
                        } else if ("20/DD".equals(rst.getString("condicao_pagamento").trim())) {
                            imp.setCondicaoPagamento(20);
                        } else if ("21/DD".equals(rst.getString("condicao_pagamento").trim())) {
                            imp.setCondicaoPagamento(21);
                        } else if ("22/DD".equals(rst.getString("condicao_pagamento").trim())) {
                            imp.setCondicaoPagamento(22);
                        } else if ("27/DD".equals(rst.getString("condicao_pagamento").trim())) {
                            imp.setCondicaoPagamento(27);
                        } else if ("28/DD".equals(rst.getString("condicao_pagamento").trim())) {
                            imp.setCondicaoPagamento(28);
                        } else if ("29/DD".equals(rst.getString("condicao_pagamento").trim())) {
                            imp.setCondicaoPagamento(29);
                        } else if ("3/DD".equals(rst.getString("condicao_pagamento").trim())) {
                            imp.setCondicaoPagamento(3);
                        } else if ("30/DD".equals(rst.getString("condicao_pagamento").trim())) {
                            imp.setCondicaoPagamento(30);
                        } else if ("32/DD".equals(rst.getString("condicao_pagamento").trim())) {
                            imp.setCondicaoPagamento(32);
                        } else if ("4/DD".equals(rst.getString("condicao_pagamento").trim())) {
                            imp.setCondicaoPagamento(4);
                        } else if ("5/DD".equals(rst.getString("condicao_pagamento").trim())) {
                            imp.setCondicaoPagamento(5);
                        } else if ("6/DD".equals(rst.getString("condicao_pagamento").trim())) {
                            imp.setCondicaoPagamento(6);
                        } else if ("60/DD".equals(rst.getString("condicao_pagamento").trim())) {
                            imp.setCondicaoPagamento(60);
                        } else if ("7/AV".equals(rst.getString("condicao_pagamento").trim())) {
                            imp.setCondicaoPagamento(7);
                        } else if ("7/DD".equals(rst.getString("condicao_pagamento").trim())) {
                            imp.setCondicaoPagamento(7);
                        } else if ("8/DD".equals(rst.getString("condicao_pagamento").trim())) {
                            imp.setCondicaoPagamento(8);
                        } else if ("9/DD".equals(rst.getString("condicao_pagamento").trim())) {
                            imp.setCondicaoPagamento(9);
                        } else {
                            imp.setCondicaoPagamento(0);
                        }
                    }

                    try (Statement stm2 = ConexaoSqlServer.getConexao().createStatement()) {
                        try (ResultSet rst2 = stm2.executeQuery(
                                "select \n"
                                + " TELNCODDDD as ddd, "
                                + " TELNNUMTEL as telefone, "
                                + " TELMOBSERV as tel_contato \n"
                                + "  from dbo.ASENTTEL \n"
                                + " where TELNID_ENT not in(select CLINID_ENT from dbo.ASENTCLI)\n"
                                + "   and TELNID_ENT = " + imp.getImportId()
                        )) {
                            while (rst2.next()) {
                                if ((rst2.getString("telefone") != null)
                                        && (!rst2.getString("telefone").trim().isEmpty())) {
                                    imp.addTelefone(
                                            rst2.getString("tel_contato") != null ? rst2.getString("tel_contato") : "TELEFONE",
                                            rst2.getString("ddd") + rst2.getString("telefone")
                                    );
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

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	pro.PROCCODPRO as idproduto,\n"
                    + "	pf.REFNID_FNC as idfornecedor,\n"
                    + "	pf.REFCCODREF as codigoexterno,\n"
                    + "	pf.REFNQTDEMB as qtdembalagem,\n"
                    + "	pf.REFDDATALT as datalateracao\n"
                    + "from dbo.ASPROREF pf\n"
                    + "inner join dbo.ASPROPRO pro on pf.REFNID_PRO = pro.PRONID_PRO"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rst.getString("idproduto"));
                    imp.setIdFornecedor(rst.getString("idfornecedor"));
                    imp.setCodigoExterno(rst.getString("codigoexterno"));
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                    imp.setDataAlteracao(rst.getDate("datalateracao"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	f.ENTNID_ENT as id,\n"
                    + "	f.ENTCCODCPF as cnpj,\n"
                    + "	f.ENTCCOD_RG as ie_rg,\n"
                    + "	f.ENTCNOMENT as razao,\n"
                    + "	f.ENTCAPELID as fantasia,\n"
                    + "	cep.CEPCCODCEP as cep,\n"
                    + "	cep.CEPCDESCRI as endereco,\n"
                    + "	cepCob.CEPCCODCEP as cep_cob,\n"
                    + "	cepCob.CEPCDESCRI as endereco_cob,\n"
                    + "	cepEnt.CEPCCODCEP as cep_ent,\n"
                    + "	cepEnt.CEPCDESCRI as endereco_ent,\n"
                    + "	cli.CLINCOBNUM as numero_cob,\n"
                    + "	cli.CLICCOBCOM as complemento_cob,\n"
                    + "	cli.CLINETRNUM as numero_ent,\n"
                    + "	cli.CLICETRCOM as complemento_ent,\n"
                    + "	f.ENTNENDNUM as numero,\n"
                    + "	f.ENTCENDCOM as complemento,\n"
                    + "	f.ENTCE_MAIL as email,\n"
                    + "	bai.BAICDESCRI as bairro,\n"
                    + "	baiCob.BAICDESCRI as bairro_cob,\n"
                    + "	baiEnt.BAICDESCRI as bairro_ent,\n"
                    + "	mun.MUNCDESCRI as municipio,\n"
                    + "	mun.MUNCCODIBG as municipio_ibge,\n"
                    + "	mun.MUNCCOD_UF as uf,\n"
                    + "	munCob.MUNCDESCRI as municipio_cob,\n"
                    + "	munCob.MUNCCODIBG as municipio_ibge_cob,\n"
                    + "	munCob.MUNCCOD_UF as uf_cob,\n"
                    + "	munEnt.MUNCDESCRI as municipio_ent,\n"
                    + "	munEnt.MUNCCODIBG as municipio_ibge_ent,\n"
                    + "	munEnt.MUNCCOD_UF as uf_ent,	\n"
                    + "	tel.TELNCODDDD as ddd,\n"
                    + "	tel.TELNNUMTEL as telefone,\n"
                    + "	tel.TELMOBSERV as tel_contato,\n"
                    + "	obs.OBSMOBSERV as observacao,\n"
                    + "	cli.CLIDNASABE as nascimento,\n"
                    + "	cli.CLIDDATCAD as cadastro,\n"
                    + "	cli.CLINLIMCPR as valor_limite,\n"
                    + "	cli.CLINCVNSAL as saldo,\n"
                    + "	cli.CLINCVNLIM as limite_convenio,\n"
                    + "	cli.CLICSEXTIP as sexo,\n"
                    + " cli.CLINID_STA as status\n"
                    + "from dbo.ASENTENT f\n"
                    + "left join dbo.ASCEPCEP cep on cep.CEPNID_CEP = f.ENTNID_CEP\n"
                    + "left join dbo.ASCEPBAI bai on bai.BAINID_BAI = cep.CEPNID_BAI\n"
                    + "left join dbo.ASCEPMUN mun on mun.MUNNID_MUN = bai.BAINID_MUN\n"
                    + "left join dbo.ASENTTEL tel on tel.TELNID_ENT = f.ENTNID_ENT\n"
                    + "left join dbo.ASENTOBS obs on obs.OBSNID_ENT = f.ENTNID_ENT and obs.OBSCTIPCAD = 'CLI'\n"
                    + "inner join dbo.ASENTCLI cli on cli.CLINID_ENT = f.ENTNID_ENT\n"
                    + "left join dbo.ASCEPCEP cepCob on cepCob.CEPNID_CEP = cli.CLINID_COB\n"
                    + "left join dbo.ASCEPBAI baiCob on baiCob.BAINID_BAI = cepCob.CEPNID_BAI\n"
                    + "left join dbo.ASCEPMUN munCob on munCob.MUNNID_MUN = baiCob.BAINID_MUN\n"
                    + "left join dbo.ASCEPCEP cepEnt on cepEnt.CEPNID_CEP = cli.CLINID_ETR\n"
                    + "left join dbo.ASCEPBAI baiEnt on baiEnt.BAINID_BAI = cepEnt.CEPNID_BAI\n"
                    + "left join dbo.ASCEPMUN munEnt on munEnt.MUNNID_MUN = baiEnt.BAINID_MUN"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setInscricaoestadual(rst.getString("ie_rg"));
                    imp.setEndereco("RUA " + rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setCep(rst.getString("cep"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setMunicipioIBGE(rst.getInt("municipio_ibge"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCobrancaEndereco(rst.getString("endereco_cob"));
                    imp.setCobrancaNumero(rst.getString("numero_cob"));
                    imp.setCobrancaCep(rst.getString("cep_cob"));
                    imp.setCobrancaBairro(rst.getString("bairro_cob"));
                    imp.setCobrancaMunicipio(rst.getString("municipio_cob"));
                    imp.setCobrancaMunicipioIBGE(rst.getInt("municipio_ibge_cob"));
                    imp.setCobrancaUf(rst.getString("uf_cob"));
                    imp.setTelefone(rst.getString("ddd") + rst.getString("telefone"));
                    imp.setEmail(rst.getString("email") == null ? "" : rst.getString("email").toLowerCase());
                    imp.setObservacao(rst.getString("observacao"));
                    imp.setObservacao2(rst.getString("observacao"));
                    imp.setDataNascimento(rst.getDate("nascimento"));
                    imp.setDataCadastro(rst.getDate("cadastro"));
                    imp.setSexo("M".equals(rst.getString("sexo")) ? TipoSexo.MASCULINO : TipoSexo.FEMININO);
                    imp.setValorLimite(rst.getDouble("saldo"));
                    imp.setPermiteCreditoRotativo(true);
                    imp.setPermiteCheque(true);

                    if (null != rst.getString("status")) {
                        switch (rst.getString("status")) {
                            case "232":
                                imp.setBloqueado(true);
                                break;
                            case "233":
                                imp.setBloqueado(false);
                                break;
                            case "263":
                                imp.setBloqueado(true);
                                break;
                            default:
                                imp.setBloqueado(false);
                                break;
                        }
                    }

                    try (Statement stm2 = ConexaoSqlServer.getConexao().createStatement()) {
                        try (ResultSet rst2 = stm2.executeQuery(
                                "select \n"
                                + " TELNCODDDD as ddd, "
                                + " TELNNUMTEL as telefone, "
                                + " TELMOBSERV as tel_contato \n"
                                + "  from dbo.ASENTTEL \n"
                                + " where TELNID_ENT in (select CLINID_ENT from dbo.ASENTCLI)\n"
                                + "   and TELNID_ENT = " + imp.getId()
                        )) {
                            while (rst2.next()) {
                                if ((rst2.getString("telefone") != null)
                                        && (!rst2.getString("telefone").trim().isEmpty())) {
                                    imp.addTelefone(
                                            rst2.getString("tel_contato") != null ? rst2.getString("tel_contato") : "TELEFONE",
                                            rst2.getString("ddd") + rst2.getString("telefone")
                                    );
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
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	f.FINNID_FIN as id,\n"
                    + "	f.FINNID_ENT as idcliente,\n"
                    + "	f.FINC_NFNUM as numerocupom,\n"
                    + "	f.FINDDATEMI as dataemissao,\n"
                    + "	i.FVLDVENVIG as datavencimento,\n"
                    + "	i.FVLNVAlBRU as valor,\n"
                    + "	i.FVLCDESCRI as observacao\n"
                    + "from [As_FIN].dbo.ASFINFIN f\n"
                    + "inner join [As_FIN].dbo.ASFINFVL i on i.FVLNID_FIN = f.FINNID_FIN\n"
                    + "where f.FINCTIPENT = 'CLI'\n"
                    + "and i.FVLDDATPAG is null\n"
                    + "and i.FVLNID_PFI = 2\n"
                    + "and f.FINNID_LOJ = " + getLojaOrigem() + "\n"
                    + "order by f.FINDDATEMI desc"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rst.getString("id"));
                    imp.setIdCliente(rst.getString("idcliente"));
                    imp.setDataEmissao(rst.getDate("dataemissao"));
                    imp.setDataVencimento(rst.getDate("datavencimento"));
                    imp.setNumeroCupom(rst.getString("numerocupom"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setObservacao(rst.getString("observacao"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    private String bancoDadosProd;
    private String bancoDadosVenda;

    public void setBancoDadosProd(String bancoDadosProd) {
        this.bancoDadosProd = bancoDadosProd;
    }

    public String getBancoDadosProd() {
        return this.bancoDadosProd;
    }

    public void setBancoDadosVenda(String bancoDadosVenda) {
        this.bancoDadosVenda = bancoDadosVenda;
    }

    public String getBancoDadosVenda() {
        return this.bancoDadosVenda;
    }

    @Override
    public Iterator<VendaIMP> getVendaIterator() throws Exception {
        return new VendaIterator(getLojaOrigem(), getBancoDadosVenda());
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        return new VendaItemIterator(getLojaOrigem(), getBancoDadosVenda(), getBancoDadosProd());
    }

    private static class VendaIterator implements Iterator<VendaIMP> {

        private final static SimpleDateFormat FORMAT = new SimpleDateFormat("dd/MM/yyyy");
        private Statement stm = ConexaoSqlServer.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaIMP next;
        private Set<String> uk = new HashSet<>();

        public VendaIterator(String idLojaCliente, String bancoDadosVenda) throws Exception {
            this.sql
                    = "select distinct \n"
                    + "	cast(ven.CUPDDATCUP as date) as datavenda,\n"
                    + "	convert(varchar(8), cast(ven.CUPDDATCUP as time), 108) as horavenda,\n"
                    + "	ven.CUPNNUMPDV as ecf,\n"
                    + "	ven.CUPNNUMCUP as numerocupom,\n"
                    + "	ven.CUPNVLRCUP as valortotal,\n"
                    + "	ven.CUPNVLRCAN as valorcancelado,\n"
                    + "	ven.CUPNVLRACR as valoracrescimo,\n"
                    + "	ven.CUPNVLRDES as valordesconto,\n"
                    + "	ven.CUPNVLROUT as valoroutro,\n"
                    + "	ven.CUPNCUPCAN as cancelado,\n"
                    + "	xm.CPXCNOMCUP as chaveCfe\n"
                    + "from [" + bancoDadosVenda + "].dbo.ASCUPCUP ven\n"
                    + "left join [" + bancoDadosVenda + "].dbo.ASCUPXML xm on xm.CPXNNUMPDV = ven.CUPNNUMPDV \n"
                    + "     and xm.CPXNNUMCUP = ven.CUPNNUMCUP\n"
                    + "     and cast(xm.CPXDDATCUP as date) = cast(ven.CUPDDATCUP as date)\n"
                    + "     and xm.CPXNID_LOJ = " + idLojaCliente + "\n"
                    + "     and xm.CPXNID_CPX = (select MIN(CPXNID_CPX) \n"
                    + "	                           from [" + bancoDadosVenda + "].dbo.ASCUPXML \n"
                    + "	                          where CPXNNUMCUP = ven.CUPNNUMCUP\n"
                    + "	                            and CPXNNUMPDV = ven.CUPNNUMPDV\n"
                    + "	                            and cast(CPXDDATCUP as date) = cast(ven.CUPDDATCUP as date)\n"
                    + "	                            and CPXNID_LOJ = " + idLojaCliente + "\n"
                    + "	                        )\n"
                    + "where CUPNID_LOJ = " + idLojaCliente + "\n"
                    + "and ven.CUPCCONCUP not like '%DATAFISCAL%'\n"
                    + "and ven.CUPNVLRCUP > 0";

            LOG.log(Level.FINE, "SQL da venda: " + sql);
            rst = stm.executeQuery(sql);
        }

        @Override
        public boolean hasNext() {
            obterNext();
            return next != null;
        }

        @Override
        public VendaIMP next() {
            obterNext();
            VendaIMP result = next;
            next = null;
            return result;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported.");
        }

        private void obterNext() {
            try {
                SimpleDateFormat timestampDate = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat timestamp = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                if (next == null) {
                    if (rst.next()) {
                        next = new VendaIMP();
                        String id = rst.getString("datavenda") + "-" + rst.getString("numerocupom") + "-" + rst.getString("ecf");
                        if (!uk.add(id)) {
                            LOG.warning("Venda " + id + " já existe na listagem");
                        }
                        next.setId(id);
                        String numeroCupom = rst.getString("numerocupom");
                        next.setNumeroCupom(Utils.stringToInt(numeroCupom));
                        next.setEcf(Utils.stringToInt(rst.getString("ecf")));
                        next.setData(rst.getDate("datavenda"));

                        String horavenda = rst.getString("horavenda");
                        if ((horavenda).contains("::")) {
                            horavenda = "00:00:00";
                        }

                        String horaInicio = timestampDate.format(rst.getDate("datavenda")) + " " + horavenda;
                        String horaTermino = timestampDate.format(rst.getDate("datavenda")) + " " + horavenda;

                        if ((rst.getString("cancelado") != null)
                                && (!rst.getString("cancelado").trim().isEmpty())) {

                            if (rst.getInt("cancelado") == 1) {
                                next.setCancelado(true);
                            } else {
                                next.setCancelado(false);
                            }
                        } else {
                            next.setCancelado(false);
                        }

                        next.setHoraInicio(timestamp.parse(horaInicio));
                        next.setHoraTermino(timestamp.parse(horaTermino));
                        next.setSubTotalImpressora(rst.getDouble("valortotal"));
                        next.setValorDesconto(rst.getDouble("valordesconto"));

                        if ((rst.getString("chaveCfe") != null)
                                && (!rst.getString("chaveCfe").trim().isEmpty())) {
                            next.setChaveCfe(rst.getString("chaveCfe").replace(".XML", "").replace("CFE", ""));
                        } else {
                            next.setChaveCfe("");
                        }
                    }
                }
            } catch (SQLException | ParseException ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }
    }

    private static class VendaItemIterator implements Iterator<VendaItemIMP> {

        private final static SimpleDateFormat FORMAT = new SimpleDateFormat("dd/MM/yyyy");

        private Statement stm = ConexaoSqlServer.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaItemIMP next;

        /**
         * Método temporario, desenvolver um mapeamento eficiente da tributação.
         *
         * @param item
         * @throws SQLException
         */
        public void obterAliquota(VendaItemIMP item, String icms) throws SQLException {
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
            double aliq;
            switch (icms) {
                case "0700":
                    cst = 0;
                    aliq = 7;
                    break;
                case "1200":
                    cst = 0;
                    aliq = 12;
                    break;
                case "1800":
                    cst = 0;
                    aliq = 18;
                    break;
                case "2500":
                    cst = 0;
                    aliq = 25;
                    break;
                case "1100":
                    cst = 0;
                    aliq = 11;
                    break;
                case "F":
                    cst = 60;
                    aliq = 0;
                    break;
                case "N":
                    cst = 41;
                    aliq = 0;
                    break;
                default:
                    cst = 40;
                    aliq = 0;
                    break;
            }
            item.setIcmsCst(cst);
            item.setIcmsAliq(aliq);
        }

        public VendaItemIterator(String idLojaCliente, String bancoDadosVenda, String bancoDadosProd) throws Exception {
            this.sql
                    = "select distinct \n"
                    + "	pro.MVPNID_MVP as id,\n"
                    + "	cast(pro.MVPDDATCAD as date) as datavenda,\n"
                    + "	pro.MVPNID_PRO as id_produto,\n"
                    + " p.PROCCODPRO as codigo_produto,\n"
                    + "	(pro.MVPNQTDMOV * -1) as qtdproduto,\n"
                    + "	(pro.MVPNVLRVDA * -1) as valortotal,\n"
                    + "	pro.MVPN_NFNUM as numerocupom,\n"
                    + " cup.CUPNNUMPDV as ecf,"
                    + "	pre.PRENCUSREP as custo,\n"
                    + "	pre.PRENVDAATU as precovenda,\n"
                    + "	inc.MVICID_INT as tribproduto,\n"
                    + "	inc.MVINPERINC as aliquota,\n"
                    + "	case p.PRONID_EMB when 965 then 'KG' else 'UN' end tipoembalagem\n"
                    + "from [" + bancoDadosVenda + "].dbo.ASPROMVP pro \n"
                    + "inner join [" + bancoDadosVenda + "].dbo.ASPROMVP_INC inc on inc.MVINID_MVP = pro.MVPNID_MVP "
                    + "     and inc.MVICID_INT not in ('121', '122')\n"
                    + "inner join [" + bancoDadosVenda + "].dbo.ASCUPCUP cup on cup.CUPNNUMCUP = pro.MVPN_NFNUM \n"
                    + "     and cast(cup.CUPDDATCUP as date) = cast(pro.MVPDDATCAD as date)\n"
                    + "     and cup.CUPNVLRCUP > 0 \n"
                    + "left join [" + bancoDadosProd + "].dbo.ASPROPRE pre on pre.PRENID_PRO = pro.MVPNID_PRO and pre.PRENID_LOJ = " + idLojaCliente + "\n"
                    + "inner join [" + bancoDadosProd + "].dbo.ASPROPRO p on p.PRONID_PRO = pro.MVPNID_PRO\n"
                    + "where pro.MVPCTIPMVP = 'VDACX' -- possível tabelas itens da venda\n"
                    + "and pro.MVPNID_LOJ = " + idLojaCliente + "";

            LOG.log(Level.FINE, "SQL da venda: " + sql);
            rst = stm.executeQuery(sql);
        }

        @Override
        public boolean hasNext() {
            obterNext();
            return next != null;
        }

        @Override
        public VendaItemIMP next() {
            obterNext();
            VendaItemIMP result = next;
            next = null;
            return result;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported.");
        }

        private void obterNext() {
            try {
                if (next == null) {
                    if (rst.next()) {
                        next = new VendaItemIMP();
                        String idVenda = rst.getString("datavenda") + "-" + rst.getString("numerocupom") + "-" + rst.getString("ecf");
                        String id = rst.getString("id");

                        next.setId(id);
                        next.setVenda(idVenda);
                        next.setProduto(rst.getString("codigo_produto"));
                        next.setQuantidade(rst.getDouble("qtdproduto"));
                        next.setTotalBruto(rst.getDouble("valortotal"));
                        next.setPrecoVenda(rst.getDouble("precovenda"));

                        boolean cancelado = false;

                        next.setCancelado(cancelado);
                        next.setCodigoBarras(rst.getString("codigo_produto"));

                        String strTrib = "";

                        if ((rst.getString("tribproduto") != null)
                                && (!rst.getString("tribproduto").trim().isEmpty())) {
                            if ("F".equals(rst.getString("tribproduto").trim())) {

                                strTrib = "F";

                            } else if ("I".equals(rst.getString("tribproduto").trim())) {

                                strTrib = "I";

                            } else if ("N".equals(rst.getString("tribproduto").trim())) {

                                strTrib = "N";

                            } else if ("T".equals(rst.getString("tribproduto"))) {

                                if (rst.getDouble("aliquota") == 7) {
                                    strTrib = "0700";
                                } else if (rst.getDouble("aliquota") == 11) {
                                    strTrib = "1100";
                                } else if (rst.getDouble("aliquota") == 12) {
                                    strTrib = "1200";
                                } else if (rst.getDouble("aliquota") == 18) {
                                    strTrib = "1800";
                                } else if (rst.getDouble("aliquota") == 25) {
                                    strTrib = "2500";
                                } else {
                                    strTrib = "I";
                                }

                            }

                        } else {
                            strTrib = "I";
                        }

                        String trib = strTrib;

                        obterAliquota(next, trib);
                    }
                }
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }
    }
}
