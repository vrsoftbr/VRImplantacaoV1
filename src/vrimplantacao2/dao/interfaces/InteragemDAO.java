package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
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
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.ProdutoAutomacaoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto.ProdutoAnteriorDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.utils.MathUtils;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorContatoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

public class InteragemDAO extends InterfaceDAO implements MapaTributoProvider {

    public String i_arquivoXLS;
    private String complemento = "";

    public void setComplemento(String complemento) {
        this.complemento = complemento == null ? "" : complemento.trim();
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + "f.CODFIL, \n"
                    + "f.CNPJFIL, \n"
                    + "f.NOMFIL \n"
                    + "FROM TABFIL f \n"
                    + "ORDER BY f.CODFIL"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(
                            rst.getString("CODFIL") + " - " + rst.getString("CNPJFIL"),
                            rst.getString("NOMFIL")));
                }
            }
        }
        return result;
    }

    @Override
    public String getSistema() {
        if (complemento.trim().isEmpty()) {
            return "Interage";
        } else {
            return "Interage - " + complemento.trim();
        }
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
                    "select distinct\n"
                    + "    coalesce(p.cst, 0) as icms_cst,\n"
                    + "    coalesce(p.icms, 0) as icms_aliquota, \n"
                    + "    0 as icms_reduzido \n"
                    + "from tabpro p"
            )) {
                while (rst.next()) {
                    String id = getAliquotaKey(
                            "".equals(rst.getString("icms_cst")) ? "0" : rst.getString("icms_cst"),
                            rst.getDouble("icms_aliquota"),
                            rst.getDouble("icms_reduzido")
                    );

                    result.add(new MapaTributoIMP(
                            id,
                            id,
                            "".equals(rst.getString("icms_cst").trim()) ? 0 : rst.getInt("icms_cst"),
                            rst.getDouble("icms_aliquota"),
                            rst.getDouble("icms_reduzido")
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
                    OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                    OpcaoProduto.MERCADOLOGICO_PRODUTO,
                    OpcaoProduto.MERCADOLOGICO_POR_NIVEL,
                    OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                    OpcaoProduto.FAMILIA_PRODUTO,
                    OpcaoProduto.FAMILIA,
                    OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                    OpcaoProduto.MANTER_DESCRICAO_PRODUTO,
                    OpcaoProduto.PRODUTOS,
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
                    OpcaoProduto.TIPO_PRODUTO,
                    OpcaoProduto.ATACADO
                }
        ));
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> vResult = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "distinct p.codpro as id,\n"
                    + "p.codbarun as ean,\n"
                    + "1 as qtdembalagem,\n"
                    + "p.unidade as unidade,\n"
                    + "p.balanca as balanca,\n"
                    + "coalesce(p.descpro, '') as descricaocompleta,\n"
                    + "coalesce(p.descpro, '') as descricaoreduzida,\n"
                    + "coalesce(p.descpro, '') as descricaogondola,\n"
                    + "0 as cod_mercadologico1,\n"
                    + "'ACERTAR' as mercadologico1,\n"
                    + "0 as cod_mercadologico2,\n"
                    + "'ACERTAR' as mercadologico2,\n"
                    + "0 as cod_mercadologico3,\n"
                    + "'ACERTAR' as mercadologico3,\n"
                    + "'' as cod_mercadologico4,\n"
                    + "'' as mercadologico4,\n"
                    + "'' as cod_mercadologico5,\n"
                    + "'' as mercadologico5,\n"
                    + "'' as id_familiaproduto,\n"
                    + "'' as familiaproduto,\n"
                    + "p.pesobruto as pesobruto,\n"
                    + "p.pesoliquido as pesoliquido,\n"
                    + "p.rgdata as datacadastro,\n"
                    + "coalesce(p.diasvenc, 0) as validade,\n"
                    + "coalesce(f.marglucva, 0) as margem,\n"
                    + "0 as estoquemaximo,\n"
                    + "0 as estoqueminimo,\n"
                    + "coalesce(f.qtdpro, 0) as estoque,\n"
                    + "coalesce(f.prcusvar, 0) as custo,\n"
                    + "coalesce(f.prvapro, 0) as precovenda,\n"
                    + "case p.stprod when 'A' then 'S' else 'N' end ativo,\n"
                    + "coalesce(p.clasfiscal, '') as ncm,\n"
                    + "coalesce(p.cest, '') as cest,\n"
                    + "coalesce(i.piscst, 0) as piscofins_cst_debito,\n"
                    + "coalesce(i.piscst, 0) as piscofins_cst_credito,\n"
                    + "'' as piscofins_natureza_receita,\n"
                    + "coalesce(p.cst, 0) as icms_cst,\n"
                    + "coalesce(p.icms, 0) as icms_aliquota,\n"
                    + "0 as icms_reduzido\n"
                    + "from tabpro p\n"
                    + "left join tabproimp i on i.codpro = p.codpro\n"
                    + "left join TABPROFIL f on f.codpro = p.codpro and f.codfil = " + getLojaOrigem().substring(0, getLojaOrigem().indexOf("-")).trim() + " "
                    + "union all \n"
                    + "select\n"
                    + "distinct p.codpro as id,\n"
                    + "ean.codigo as ean,\n"
                    + "ean.qtdun as qtdembalagem,\n"
                    + "p.unidade as unidade,\n"
                    + "p.balanca as balanca,\n"
                    + "coalesce(p.descpro, '') as descricaocompleta,\n"
                    + "coalesce(p.descpro, '') as descricaoreduzida,\n"
                    + "coalesce(p.descpro, '') as descricaogondola,\n"
                    + "0 as cod_mercadologico1,\n"
                    + "'ACERTAR' as mercadologico1,\n"
                    + "0 as cod_mercadologico2,"
                    + "'ACERTAR' as mercadologico2,\n"
                    + "0 as cod_mercadologico3,\n"
                    + "'ACERTAR' as mercadologico3,\n"
                    + "'' as cod_mercadologico4,\n"
                    + "'' as mercadologico4,\n"
                    + "'' as cod_mercadologico5,\n"
                    + "'' as mercadologico5,\n"
                    + "'' as id_familiaproduto,\n"
                    + "'' as familiaproduto,\n"
                    + "p.pesobruto as pesobruto,\n"
                    + "p.pesoliquido as pesoliquido,\n"
                    + "p.rgdata as datacadastro,\n"
                    + "coalesce(p.diasvenc, 0) as validade,\n"
                    + "coalesce(f.marglucva, 0) as margem,\n"
                    + "0 as estoquemaximo,\n"
                    + "0 as estoqueminimo,\n"
                    + "coalesce(f.qtdpro, 0) as estoque,\n"
                    + "coalesce(f.prcusvar, 0) as custo,\n"
                    + "coalesce(f.prvapro, 0) as precovenda,\n"
                    + "case p.stprod when 'A' then 'S' else 'N' end ativo,\n"
                    + "coalesce(p.clasfiscal, '') as ncm,\n"
                    + "coalesce(p.cest, '') as cest,\n"
                    + "coalesce(i.piscst, 0) as piscofins_cst_debito,\n"
                    + "coalesce(i.piscst, 0) as piscofins_cst_credito,\n"
                    + "'' as piscofins_natureza_receita,\n"
                    + "coalesce(p.cst, 0) as icms_cst,\n"
                    + "coalesce(p.icms, 0) as icms_aliquota,\n"
                    + "0 as icms_reduzido\n"
                    + "from tabpro p\n"
                    + "join tabprocod ean on ean.codpro = p.codpro \n"
                    + "left join tabproimp i on i.codpro = p.codpro\n"
                    + "left join TABPROFIL f on f.codpro = p.codpro and f.codfil = " + getLojaOrigem().substring(0, getLojaOrigem().indexOf("-")).trim() + ""
            )) {
                int contador = 1;
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().carregarProdutosBalanca();
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(Utils.formataNumero(rst.getString("id")));
                    imp.setEan(Utils.formataNumero(rst.getString("ean")));

                    if ("7599".equals(imp.getImportId())) {
                        System.out.println(imp.getEan());
                    }

                    if ((imp.getEan() != null)
                            && (!imp.getEan().trim().isEmpty())) {
                        ProdutoBalancaVO produtoBalanca;
                        long codigoProduto;
                        codigoProduto = Long.parseLong(imp.getImportId());
                        if (codigoProduto <= Integer.MAX_VALUE) {
                            produtoBalanca = produtosBalanca.get((int) codigoProduto);
                        } else {
                            produtoBalanca = null;
                        }
                        if (produtoBalanca != null) {
                            imp.seteBalanca(true);
                            imp.setValidade(produtoBalanca.getValidade() > 1 ? produtoBalanca.getValidade() : rst.getInt("validade"));
                        } else {
                            imp.setValidade(rst.getInt("validade"));
                            imp.seteBalanca(false);
                            //imp.setManterEAN(true);
                        }
                    }

                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    imp.setDescricaoGondola(imp.getDescricaoGondola());
                    imp.setTipoEmbalagem(rst.getString("unidade"));
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                    imp.setMargem(MathUtils.trunc(rst.getDouble("margem"), 2));
                    imp.setPesoBruto(MathUtils.trunc(rst.getDouble("pesobruto"), 2));
                    imp.setPesoLiquido(MathUtils.trunc(rst.getDouble("pesoliquido"), 2));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(Integer.parseInt(Utils.formataNumero(rst.getString("piscofins_cst_debito"))));
                    imp.setPiscofinsCstCredito(Integer.parseInt(Utils.formataNumero(rst.getString("piscofins_cst_credito"))));
                    imp.setSituacaoCadastro(("S".equals(rst.getString("ativo")) ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO));
                    String venda = rst.getString("precovenda");

                    if (venda.length() > 11) {
                        imp.setPrecovenda(0);
                    } else {
                        imp.setPrecovenda(rst.getDouble("precovenda"));
                    }

                    imp.setCustoComImposto(MathUtils.trunc(rst.getDouble("custo"), 2));
                    imp.setCustoSemImposto(MathUtils.trunc(rst.getDouble("custo"), 2));
                    imp.setEstoque(MathUtils.trunc(rst.getDouble("estoque"), 2));

                    String aliqIcmsId = getAliquotaKey(
                            rst.getString("icms_cst"),
                            rst.getDouble("icms_aliquota"),
                            rst.getDouble("icms_reduzido"));

                    imp.setIcmsDebitoId(aliqIcmsId);
                    imp.setIcmsDebitoForaEstadoId(aliqIcmsId);
                    imp.setIcmsDebitoForaEstadoNfId(aliqIcmsId);
                    imp.setIcmsCreditoId(aliqIcmsId);
                    imp.setIcmsCreditoForaEstadoId(aliqIcmsId);
                    imp.setIcmsConsumidorId(aliqIcmsId);

                    vResult.add(imp);
                    ProgressBar.setStatus("Carregando dados...Produtos..." + contador);
                    contador++;
                }
            }
        }
        return vResult;
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "p.codpro id_produto,\n"
                    + "p.codigo codigobarras,\n"
                    + "p.qtdun qtdembalagem\n"
                    + "from tabprocod p"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id_produto"));
                    imp.setEan(rst.getString("codigobarras"));
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<ProdutoIMP> getProdutos(OpcaoProduto opt) throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        String codigoBarras;

        if (opt == OpcaoProduto.ATACADO) {
            try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select\n"
                        + "    pa.codpro as idproduto,\n"
                        + "    pa.qtddesco as quantidade,\n"
                        + "    pa.prvapro as precovenda,\n"
                        + "    pa.percdescco as percentualdesconto\n"
                        + "from TABPROFIL pa\n"
                        + "where pa.codfil = " + getLojaOrigem().substring(0, getLojaOrigem().indexOf("-")).trim() + "\n"
                        + "and pa.qtddesco > 1\n"
                        + "and pa.percdescco > 0"
                )) {
                    while (rst.next()) {
                        int codigoAtual = new ProdutoAnteriorDAO().getCodigoAnterior2(getSistema(), getLojaOrigem(), rst.getString("idproduto"));

                        codigoBarras = "999999" + String.valueOf(codigoAtual);

                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("idproduto"));
                        imp.setEan(codigoBarras);
                        imp.setQtdEmbalagem(rst.getInt("quantidade"));
                        imp.setPrecovenda(rst.getDouble("precovenda"));
                        imp.setAtacadoPorcentagem(rst.getDouble("percentualdesconto"));
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
        List<FornecedorIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select codfor, nomfor, fanfor, endfor, baifor, pontoref, cidade,\n"
                    + "        uf, cep, nrendfor, fone1, fone2, fax,  email, contato, cnpj, inscest,\n"
                    + "        tpfornec, situacao, obs, tppessoa, represent01, fonerepre01,\n"
                    + "        represent02, fonerepre02, represent03, fonerepre03,\n"
                    + "        represent04, fonerepre04\n"
                    + "  from tabfor"
            )) {
                int contador = 1;
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("codfor"));
                    imp.setRazao(rst.getString("nomfor"));
                    imp.setFantasia(rst.getString("fanfor"));
                    imp.setEndereco(rst.getString("endfor"));
                    imp.setBairro(rst.getString("baifor"));
                    imp.setComplemento(rst.getString("pontoref"));
                    imp.setMunicipio(rst.getString("cidade").toUpperCase());
                    imp.setUf(rst.getString("uf").toUpperCase());
                    imp.setCep(rst.getString("cep"));
                    imp.setNumero(rst.getString("nrendfor"));
                    imp.setAtivo(("A".equals(rst.getString("situacao"))));
                    imp.setCnpj_cpf(Utils.formataNumero(rst.getString("cnpj")));
                    imp.setIe_rg(rst.getString("inscest"));
                    imp.setTel_principal(rst.getString("fone1"));

                    if (Utils.stringToLong(rst.getString("fone2")) > 0) {
                        FornecedorContatoIMP cont = new FornecedorContatoIMP();
                        cont.setImportSistema(getSistema());
                        cont.setImportLoja(getLojaOrigem());
                        cont.setImportId("1");
                        cont.setNome("TELEFONE 2");
                        cont.setTelefone(Utils.stringLong(rst.getString("fone2")));
                    }
                    if (Utils.stringToLong(rst.getString("fax")) > 0) {
                        FornecedorContatoIMP cont = new FornecedorContatoIMP();
                        cont.setImportSistema(getSistema());
                        cont.setImportLoja(getLojaOrigem());
                        cont.setImportId("2");
                        cont.setNome("FAX");
                        cont.setTelefone(Utils.stringLong(rst.getString("fax")));
                    }
                    if ((rst.getString("email") != null)
                            && (!rst.getString("email").trim().isEmpty())) {
                        FornecedorContatoIMP cont = imp.getContatos().make("2");
                        cont.setImportSistema(getSistema());
                        cont.setImportLoja(getLojaOrigem());
                        cont.setImportId("3");
                        cont.setNome("EMAIL");
                        cont.setEmail(rst.getString("email"));
                    }
                    if ((rst.getString("contato") != null)
                            && (!rst.getString("contato").trim().isEmpty())) {
                        FornecedorContatoIMP cont = imp.getContatos().make("3");
                        cont.setImportSistema(getSistema());
                        cont.setImportLoja(getLojaOrigem());
                        cont.setImportId("3");
                        cont.setNome("CONTATO");
                        cont.setEmail(rst.getString("contato"));
                    }
                    vResult.add(imp);
                    ProgressBar.setStatus("Carregando dados...Fornecedores..." + contador);
                    contador++;
                }
            }
        }
        return vResult;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    distinct pf.codfor,\n"
                    + "    pf.codpro,\n"
                    + "    pf.codigo,\n"
                    + "    coalesce(fator.unidade, 'UN') unidade,\n"
                    + "    coalesce(fator.fator, 1) qtd\n"
                    + "from\n"
                    + "    tabprofor pf\n"
                    + "left join\n"
                    + "    (select\n"
                    + "        codpro,\n"
                    + "        codfor,\n"
                    + "        fator,\n"
                    + "        unidade\n"
                    + "    from\n"
                    + "        tabproforund\n"
                    + "    where\n"
                    + "        fator > 1) fator on (pf.codpro = fator.codpro) and\n"
                    + "        pf.codfor = fator.codfor\n"
                    + "order by\n"
                    + "    pf.codfor, pf.codpro"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rst.getString("codpro"));
                    imp.setIdFornecedor(rst.getString("codfor"));
                    imp.setCodigoExterno(rst.getString("codigo"));
                    imp.setQtdEmbalagem(rst.getDouble("qtd"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            
            try (ResultSet rst = stm.executeQuery(
                    " select\n"
                    + "    fun.codprof as id,\n"
                    + "    fun.nomprof as nome,\n"
                    + "    fun.endprof as endereco,\n"
                    + "    fun.cidprof as municipio,\n"
                    + "    fun.bairro as bairro,\n"
                    + "    fun.cpfprof as cpf,\n"
                    + "    fun.rgprof as rg,\n"
                    + "    fun.telprof as telefone,\n"
                    + "    fun.rgdata as datacadastro,\n"
                    + "    fun.cargo,\n"
                    + "    fun.funcao,\n"
                    + "    fun.dtadmissao as dataadmissao,\n"
                    + "    fun.salprof as salario\n"
                    + " from tabprof fun\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("id"));
                    imp.setRazao(rst.getString("nome"));
                    imp.setFantasia(imp.getRazao());
                    imp.setCnpj(rst.getString("cpf"));
                    imp.setInscricaoestadual(rst.getString("rg"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setTelefone(rst.getString("telefone"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setDataAdmissao(rst.getDate("dataadmissao"));
                    imp.setSalario(rst.getDouble("salario"));
                    
                    if ((rst.getString("cargo") != null)
                            && (!rst.getString("cargo").trim().isEmpty())) {
                        imp.setCargo(rst.getString("cargo"));
                    } else {
                        imp.setCargo(rst.getString("funcao"));
                    }
                    
                    result.add(imp);
                }
            }
            
            /*try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "    c.codcli id,\n"
                    + "    c.nomcli razao,\n"
                    + "    c.fancli fantasia,\n"
                    + "    c.dtcadastro,\n"
                    + "    c.dtnasc dtnascimento,\n"
                    + "    c.endcli endereco,\n"
                    + "    c.baicli bairro,\n"
                    + "    c.nrendcli numero,\n"
                    + "    c.cep,\n"
                    + "    c.cidade,\n"
                    + "    c.uf,\n"
                    + "    c.pontoref referencia,\n"
                    + "    c.fone1,\n"
                    + "    c.fone2,\n"
                    + "    c.fax,\n"
                    + "    c.email,\n"
                    + "    c.contato,\n"
                    + "    c.cgc cnpj,\n"
                    + "    c.inscest ie,\n"
                    + "    c.estcivil estadocivil,\n"
                    + "    cast((case sexo when '' then 0 else sexo end) as integer) sexo,\n"
                    + "    c.nmpai nomepai,\n"
                    + "    c.nmmae nomemae,\n"
                    + "    c.vlmtcli limite,\n"
                    + "    c.obs,\n"
                    + "    c.diaspag,\n"
                    + "    c.nmconjuge\n"
                    + "from\n"
                    + "    tabcli c\n"
                    + "order by\n"
                    + "    c.codcli")) {
                while (rs.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rs.getString("id"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setCnpj(rs.getString("cnpj"));
                    imp.setInscricaoestadual(rs.getString("ie"));
                    if (rs.getString("fantasia") == null && "".equals(rs.getString("fantasia"))) {
                        imp.setFantasia(rs.getString("razao"));
                    } else {
                        imp.setFantasia(rs.getString("fantasia"));
                    }
                    imp.setDataCadastro(rs.getDate("dtcadastro"));
                    imp.setDataNascimento(rs.getDate("dtnascimento"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setCep(rs.getString("cep"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("uf"));
                    imp.setTelefone(rs.getString("fone1"));
                    if (rs.getString("fone2") != null && !"".equals(rs.getString("fone2").trim())) {
                        imp.addContato("1", "TELEFONE 2", rs.getString("fone2"), "", "");
                    }
                    imp.setFax(rs.getString("fax"));
                    imp.setEmail(rs.getString("email"));
                    if (rs.getString("contato") != null && !"".equals(rs.getString("contato").trim())) {
                        imp.addContato("2", "CONTATO", rs.getString("contato"), "", "");
                    }
                    imp.setSexo(rs.getInt("sexo") == 0 ? TipoSexo.MASCULINO : TipoSexo.FEMININO);
                    imp.setNomePai(rs.getString("nomepai"));
                    imp.setNomeMae(rs.getString("nomemae"));
                    imp.setValorLimite(rs.getDouble("limite"));
                    if (rs.getString("obs") != null && !"".equals(rs.getString("obs").trim())) {
                        imp.setObservacao(rs.getString("obs"));
                    }
                    imp.copiarEnderecoParaCobranca();
                    imp.setPermiteCheque(true);
                    imp.setPermiteCreditoRotativo(true);
                    imp.setPrazoPagamento(rs.getInt("diaspag"));
                    imp.setNomeConjuge(rs.getString("nmconjuge"));

                    result.add(imp);
                }
            }*/
        }
        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    /*"select \n"
                    + "    r.codtit id,\n"
                    + "    r.codcli idcliente, \n"
                    + "    --c.cgc cnpj,\n"
                    + "    r.nrnota documento, \n"
                    + "    r.nomcli razao,\n"
                    + "    r.dtemitit emissao,\n"
                    + "    r.dtventit vencimento,\n"
                    + "    --r.dtpagtit pagamento,\n"
                    + "    r.vlduptit valor,\n"
                    + "    --r.vlabatit valorabatido,\n"
                    + "    --r.vlpagtit valorpago,\n"
                    + "    r.obstit observacao,\n"
                    + "    r.parcela as parcela\n"
                    + "    --mov.vlduptit as valorconta\n"
                    + " from MOVIINCR r\n"
                    + "where r.codfil = "+ getLojaOrigem().substring(0, getLojaOrigem().indexOf("-")).trim() +"\n"
                    + "and r.sttit = 'E'"*/
                    "select\n"
                    + "    r.codtit id,\n"
                    + "    r.codcli idcliente,\n"
                    + "    c.cgc cnpj,\n"
                    + "    r.nrnota documento,\n"
                    + "    r.nomcli razao,\n"
                    + "    r.dtemitit emissao,\n"
                    + "    r.dtventit vencimento,\n"
                    + "    r.dtpagtit pagamento,\n"
                    + "    r.vlduptit valor,\n"
                    + "    r.vlabatit valorabatido,\n"
                    + "    r.vlpagtit valorpago,\n"
                    + "    r.obstit observacao,\n"
                    + "    r.parcela as parcela\n"        
                    + "from\n"
                    + "    titulor r\n"
                    + "join tabcli c on r.codcli = c.codcli\n"
                    + "where\n"
                    + "    r.dtpagtit is null\n"
                    + "order by\n"
                    + "    r.dtemitit"
            )) {
                while (rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rs.getString("id"));
                    imp.setIdCliente(rs.getString("idcliente"));
                    //imp.setCnpjCliente(rs.getString("cnpj"));
                    imp.setDataEmissao(rs.getDate("emissao"));
                    imp.setDataVencimento(rs.getDate("vencimento"));
                    imp.setNumeroCupom(rs.getString("documento"));
                    imp.setValor(rs.getDouble("valor"));
                    imp.setParcela(Integer.parseInt(rs.getString("parcela").substring(0, rs.getString("parcela").indexOf("/"))));
                    imp.setObservacao(rs.getString("observacao"));

                    result.add(imp);
                }
            }
        }
        return result;
    }
    
    private List<ProdutoAutomacaoVO> getDigitoVerificador() throws Exception {
        List<ProdutoAutomacaoVO> result = new ArrayList<>();

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select id, id_tipoembalagem from produto \n"
                    + "where id_tipoembalagem = 0 \n"
                    + "and pesavel = false\n"
                    + "and id in (select id_produto from implantacao.bkp_produtoautomacao where codigobarras <= 999999)\n"
                    + "order by id"
            )) {
                while (rst.next()) {
                    ProdutoAutomacaoVO vo = new ProdutoAutomacaoVO();
                    vo.setIdproduto(rst.getInt("id"));
                    vo.setIdTipoEmbalagem(rst.getInt("id_tipoembalagem"));
                    vo.setCodigoBarras(gerarEan13(rst.getLong("id"), true));
                    result.add(vo);
                }
            }
        }

        return result;
    }
    
    public void importarDigitoVerificador() throws Exception {
        List<ProdutoAutomacaoVO> result = new ArrayList<>();
        ProgressBar.setStatus("Carregar Produtos...");
        try {
            result = getDigitoVerificador();
            
            if (!result.isEmpty()) {
                gravarCodigoBarrasDigitoVerificador(result);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    private void gravarCodigoBarrasDigitoVerificador(List<ProdutoAutomacaoVO> vo) throws Exception {
        
        Conexao.begin();
        Statement stm, stm2 = null;
        ResultSet rst = null;
        
        stm = Conexao.createStatement();
        stm2 = Conexao.createStatement();
        
        String sql = "";
        ProgressBar.setStatus("Gravando Código de Barras...");
        ProgressBar.setMaximum(vo.size());
        
        try {
            
            for (ProdutoAutomacaoVO i_vo : vo) {
                
                sql = "select codigobarras from produtoautomacao where codigobarras = " + i_vo.getCodigoBarras();
                rst = stm.executeQuery(sql);

                if (!rst.next()) {
                    sql = "insert into produtoautomacao ("
                            + "id_produto, "
                            + "codigobarras, "
                            + "id_tipoembalagem, "
                            + "qtdembalagem) "
                            + "values ("
                            + i_vo.getIdproduto() + ", "
                            + i_vo.getCodigoBarras() + ", "
                            + i_vo.getIdTipoEmbalagem() + ", 1);";
                    stm2.execute(sql);
                } else {                    
                    sql = "insert into implantacao.produtonaogerado ("
                            + "id_produto, "
                            + "codigobarras) "
                            + "values ("
                            + i_vo.getIdproduto() + ", "
                            + i_vo.getCodigoBarras() + ");";
                    stm2.execute(sql);
                }
                ProgressBar.next();
            }
            
            stm.close();
            stm2.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }
    
    public long gerarEan13(long i_codigo, boolean i_digito) throws Exception {
        String codigo = String.format("%012d", i_codigo);

        int somaPar = 0;
        int somaImpar = 0;

        for (int i = 0; i < 12; i += 2) {
            somaImpar += Integer.parseInt(String.valueOf(codigo.charAt(i)));
            somaPar += Integer.parseInt(String.valueOf(codigo.charAt(i + 1)));
        }

        int soma = somaImpar + (3 * somaPar);
        int digito = 0;
        boolean verifica = false;
        int calculo = 0;

        do {
            calculo = soma % 10;

            if (calculo != 0) {
                digito += 1;
                soma += 1;
            }
        } while (calculo != 0);

        if (i_digito) {
            return Long.parseLong(codigo + digito);
        } else {
            return Long.parseLong(codigo);
        }
    }        
}
