package vrimplantacao2_5.dao.sistema;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.ProdutoAutomacaoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto.ProdutoAnteriorDAO;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
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
import vrimplantacao2_5.dao.conexao.ConexaoFirebird;

/**
 *
 * @author Michael-Oliveira
 */
public class Interage2_5DAO extends InterfaceDAO implements MapaTributoProvider {

    private String complemento = "";
    private String conexao = "";

    public void setComplemento(String complemento) {
        this.complemento = complemento == null ? "" : complemento.trim();
    }

    public void setConexao(String complemento) {
        this.conexao = complemento;
    }

    public String getConexao() {
        return conexao;
    }

    @Override
    public String getSistema() {
        if (complemento.trim().isEmpty()) {
            return "Interage";
        } else {
            return "Interage - " + complemento.trim();
        }
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    /*"SELECT DISTINCT\n"
                    + "    COALESCE(p.ICMSCST, 0) icms_cst,\n"
                    + "    COALESCE(p.ICMSPICMS, 0) icms_aliquota,\n"
                    + "    COALESCE(p.ICMSPREDBC, 0) icms_reduzido\n"
                    + "from tabproimp p"*/
                    "SELECT DISTINCT \n"
                    + "    ICMSCST || CAST(ICMSPICMS AS varchar(5)) || CAST(ICMSPREDBC AS varchar(5)) id,\n"
                    + "    CAST(ICMSPICMS AS varchar(5)) ||  ' RDZ ' || CAST(ICMSPREDBC AS varchar(5)) descricao,\n"
                    + "	ICMSCST cst, \n"
                    + "	ICMSPICMS aliquota, \n"
                    + "	ICMSPREDBC reducao\n"
                    + "FROM DETENTNFE c"
            )) {
                while (rst.next()) {
                    /*String id = getAliquotaKey(
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
                    ));*/
                    result.add(new MapaTributoIMP(rst.getString("id"), 
                            rst.getString("descricao"), 
                            rst.getInt("cst"), 
                            rst.getDouble("aliquota"), 
                            rst.getDouble("reducao")));
                }
            }
        }
        return result;
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
    public Set<OpcaoFornecedor> getOpcoesDisponiveisFornecedor() {
        return new HashSet<>(Arrays.asList(
                OpcaoFornecedor.ENDERECO,
                OpcaoFornecedor.DADOS,
                OpcaoFornecedor.CONTATOS,
                OpcaoFornecedor.SITUACAO_CADASTRO,
                OpcaoFornecedor.TIPO_EMPRESA,
                OpcaoFornecedor.PAGAR_FORNECEDOR,
                OpcaoFornecedor.PRODUTO_FORNECEDOR
        ));
    }

    @Override
    public Set<OpcaoCliente> getOpcoesDisponiveisCliente() {
        return new HashSet<>(Arrays.asList(
                OpcaoCliente.DADOS,
                OpcaoCliente.CNPJ,
                OpcaoCliente.INSCRICAO_ESTADUAL,
                OpcaoCliente.ENDERECO,
                OpcaoCliente.CONTATOS,
                OpcaoCliente.DATA_CADASTRO,
                OpcaoCliente.DATA_NASCIMENTO,
                OpcaoCliente.RECEBER_CREDITOROTATIVO,
                OpcaoCliente.CONVENIO_EMPRESA,
                OpcaoCliente.CONVENIO_TRANSACAO,
                OpcaoCliente.RECEBER_CHEQUE,
                OpcaoCliente.BLOQUEADO,
                OpcaoCliente.SITUACAO_CADASTRO,
                OpcaoCliente.CONVENIO_CONVENIADO));
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
                    "WITH filtro AS (\n"
                    + "SELECT \n"
                    + " CODPRO,\n"
                    + " max(KEYNFE) key\n"
                    + "FROM DETENTNFE \n"
                    + "GROUP BY 1\n"
                    + ")\n"
                    + "select\n"
                    + "distinct p.codpro as id,\n"
                    + "p.codbarun as ean,\n"
                    + "1 as qtdembalagem,\n"
                    + "p.unidade as unidade,\n"
                    + "p.balanca as balanca,\n"
                    + "COALESCE(p.descpro, null) as descricaocompleta,\n"
                    + "COALESCE(p.descpro, null) as descricaoreduzida,\n"
                    + "COALESCE(p.descpro, null) as descricaogondola,\n"
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
                    + "COALESCE(p.diasvenc, 0) as validade,\n"
                    + "COALESCE(f.marglucva, 0) as margem,\n"
                    + "0 as estoquemaximo,\n"
                    + "0 as estoqueminimo,\n"
                    + "COALESCE(f.qtdpro, 0) as estoque,\n"
                    + "COALESCE(f.PRCUSTOCOM, 0) as custosemimposto, \n"
                    + "COALESCE(f.prcusvar, 0) as custo,\n"
                    + "COALESCE(f.prvapro, 0) as precovenda,\n"
                    + "case p.stprod when 'A' then 'S' else 'N' end ativo,\n"
                    + "COALESCE(p.clasfiscal, '') as ncm,\n"
                    + "COALESCE(p.cest, null) as cest,\n"
                    + "COALESCE(i.piscst, 0) as piscofins_cst_debito,\n"
                    + "COALESCE(i.piscst, 0) as piscofins_cst_credito,\n"
                    + "'' as piscofins_natureza_receita,\n"
                    + "COALESCE(i.ICMSCST, 0) as icms_cst,\n"
                    + "COALESCE(i.ICMSPICMS, 0) as icms_aliquota,\n"
                    + "COALESCE(i.ICMSPREDBC, 0) as icms_reduzido,\n"
                    + "c.ICMSCST || CAST(c.ICMSPICMS AS varchar(5)) || CAST(c.ICMSPREDBC AS varchar(5)) id_aliquota\n"
                    + "from tabpro p\n"
                    + "JOIN DETENTNFE c on c.CODPRO = p.codpro\n"
                    + "JOIN filtro fi ON fi.KEY = c.KEYNFE AND fi.codpro = c.CODPRO\n"
                    + "left join tabproimp i on i.codpro = p.codpro and i.codfil = " + getLojaOrigem() + "\n"
                    + "left join TABPROFIL f on f.codpro = p.codpro and f.codfil = " + getLojaOrigem() + "\n"
                    + "WHERE  p.stprod = 'A'"
                    + "union all \n"
                    + "select\n"
                    + "distinct p.codpro as id,\n"
                    + "ean.codigo as ean,\n"
                    + "ean.qtdun as qtdembalagem,\n"
                    + "p.unidade as unidade,\n"
                    + "p.balanca as balanca,\n"
                    + "COALESCE(p.descpro, null) as descricaocompleta,\n"
                    + "COALESCE(p.descpro, null) as descricaoreduzida,\n"
                    + "COALESCE(p.descpro, null) as descricaogondola,\n"
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
                    + "COALESCE(p.diasvenc, 0) as validade,\n"
                    + "COALESCE(f.marglucva, 0) as margem,\n"
                    + "0 as estoquemaximo,\n"
                    + "0 as estoqueminimo,\n"
                    + "COALESCE(f.qtdpro, 0) as estoque,\n"
                    + "COALESCE(f.PRCUSTOCOM, 0) as custosemimposto, \n"
                    + "COALESCE(f.prcusvar, 0) as custo,\n"
                    + "COALESCE(f.prvapro, 0) as precovenda,\n"
                    + "case p.stprod when 'A' then 'S' else 'N' end ativo,\n"
                    + "COALESCE(p.clasfiscal, null) as ncm,\n"
                    + "COALESCE(p.cest, null) as cest,\n"
                    + "COALESCE(i.piscst, 0) as piscofins_cst_debito,\n"
                    + "COALESCE(i.piscst, 0) as piscofins_cst_credito,\n"
                    + "'' as piscofins_natureza_receita,\n"
                    + "COALESCE(i.ICMSCST, 0) as icms_cst,\n"
                    + "COALESCE(i.ICMSPICMS, 0) as icms_aliquota,\n"
                    + "COALESCE(i.ICMSPREDBC, 0) as icms_reduzido,\n"
                    + "c.ICMSCST || CAST(c.ICMSPICMS AS varchar(5)) || CAST(c.ICMSPREDBC AS varchar(5)) id_aliquota\n"
                    + "from tabpro p\n"
                    + "join tabprocod ean on ean.codpro = p.codpro \n"
                    + "JOIN DETENTNFE c on c.CODPRO = p.codpro\n"
                    + "JOIN filtro fi ON fi.KEY = c.KEYNFE AND fi.codpro = c.CODPRO\n"
                    + "left join tabproimp i on i.codpro = p.codpro and i.codfil = " + getLojaOrigem() + " \n"
                    + "left join TABPROFIL f on f.codpro = p.codpro and f.codfil = " + getLojaOrigem() + "\n"
                    + "where p.stprod = 'A'"
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
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
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
                    imp.setCustoSemImposto(MathUtils.trunc(rst.getDouble("custosemimposto"), 2));
                    imp.setEstoque(MathUtils.trunc(rst.getDouble("estoque"), 2));

                    /*String aliqIcmsId = getAliquotaKey(
                            rst.getString("icms_cst"),
                            rst.getDouble("icms_aliquota"),
                            rst.getDouble("icms_reduzido"));*/
                    imp.setIcmsDebitoId(rst.getString("id_aliquota"));
                    imp.setIcmsDebitoForaEstadoId(imp.getIcmsDebitoId());
                    imp.setIcmsDebitoForaEstadoNfId(imp.getIcmsDebitoId());
                    imp.setIcmsCreditoId(imp.getIcmsDebitoId());
                    imp.setIcmsCreditoForaEstadoId(imp.getIcmsDebitoId());
                    imp.setIcmsConsumidorId(imp.getIcmsDebitoId());

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
                    + "from tabprocod p \n"
                    + "JOIN TABPRO t ON p.CODPRO = t.CODPRO \n"
                    + "WHERE t.STPROD != 'I'"
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
                        + "    t.QTDEMB as quantidade,\n "
                        + "    t.UNEMB emb, \n"
                        + "    pa.prvapro as precovenda,\n"
                        + "    pa.PRATPRO as precoatacado\n"
                        + "from TABPROFIL pa\n"
                        + "JOIN TABPRO t ON pa.CODPRO = t.CODPRO \n"
                        + "where pa.codfil = " + getLojaOrigem() + "\n"
                        + "AND pa.PRATPRO > 0"
                )) {
                    int contador = 1;
                    while (rst.next()) {
                        int codigoAtual = new ProdutoAnteriorDAO().getCodigoAnterior2(getSistema() + this.conexao, getLojaOrigem(), rst.getString("idproduto"));

                        codigoBarras = "999999" + String.valueOf(codigoAtual);

                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("idproduto"));
                        imp.setEan(codigoBarras);
                        imp.setQtdEmbalagem(rst.getInt("quantidade"));
                        imp.setPrecovenda(rst.getDouble("precovenda"));
                        imp.setAtacadoPreco(rst.getDouble("precoatacado") / rst.getInt("quantidade"));
                        imp.setTipoEmbalagem(rst.getString("emb"));
                        result.add(imp);
                        ProgressBar.setStatus("Carregando dados...Produtos..." + contador);
                        contador++;
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
                    imp.setMunicipio(rst.getString("cidade") == null ? "BELEM" : rst.getString("cidade").toUpperCase());
                    imp.setUf(rst.getString("uf") == null ? "PA" : rst.getString("uf").toUpperCase());
                    imp.setCep(rst.getString("cep"));
                    imp.setNumero(rst.getString("nrendfor"));
                    imp.setAtivo(("A".equals(rst.getString("situacao").trim().toUpperCase())));
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
                    + "    pf.codigo\n"
                    + "from\n"
                    + "    tabprofor pf"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rst.getString("codpro"));
                    imp.setIdFornecedor(rst.getString("codfor"));
                    imp.setCodigoExterno(rst.getString("codigo"));
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
            /*
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    fun.CODCLI as id,\n"
                    + "    fun.NOMCLI as nome,\n"
                    + "    fun.ENDCLI as endereco,\n"
                    + "    fun.CIDADE as municipio,\n"
                    + "    fun.BAICLI as bairro,\n"
                    + "    fun.CGC as cpf,\n"
                    + "    fun.INSCEST as rg,\n"
                    + "    fun.FONE1 as telefone,\n"
                    + "    fun.FONE2 as telefone2,\n"
                    + "    fun.DTCADASTRO as datacadastro,\n"
                    + "    fun.DTNASC nascimento,\n"
                    + "    fun.CARGOTRAB cargo,\n"
                    + "    fun.LCTRAB empresa,\n"
                    + "    fun.VLMTMENSAL limite,\n"
                    + "    fun.NMPAI AS pai,\n"
                    + "    fun.NMMAE AS mae,\n"
                    + "    fun.CEP cep,\n"
                    + "    fun.UF uf,\n"
                    + "    fun.RESTRINGIR\n"
                    + " from TABCLI fun\n"
                    + "order by 2"
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
                    imp.setCelular(rst.getString("telefone2"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setDataNascimento(rst.getDate("nascimento"));
                    imp.setNomePai(rst.getString("pai"));
                    imp.setNomeMae(rst.getString("mae"));
                    imp.setValorLimite(rst.getDouble("limite"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setPermiteCreditoRotativo(true);

                    if ((rst.getString("cargo") != null)
                            && (!rst.getString("cargo").trim().isEmpty())) {
                        imp.setCargo(rst.getString("cargo"));
                    } else {
                        imp.setCargo(rst.getString("empresa"));
                    }
                    imp.setBloqueado(rst.getString("ativo").trim().toUpperCase().equals("S"));
                    imp.setAtivo(rst.getString("ativo").trim().toUpperCase().equals("A"));

                    result.add(imp);
                }
            }
             */
            try (ResultSet rs = stm.executeQuery(
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
            }
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
                    + "where r.codfil = " + getLojaOrigem() + "\n"
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
                    + "    r.STTIT = 'P'\n"
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
                    imp.setValor(rs.getDouble("valorabatido"));
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

                    String ean = "";

                    if (String.valueOf(rst.getInt("id")).length() == 1) {
                        ean = "9000" + rst.getString("id");
                    } else if (String.valueOf(rst.getInt("id")).length() == 2) {
                        ean = "900" + rst.getString("id");
                    } else if (String.valueOf(rst.getInt("id")).length() == 3) {
                        ean = "90" + rst.getString("id");
                    } else if (String.valueOf(rst.getInt("id")).length() == 4) {
                        ean = "9" + rst.getString("id");
                    } else {
                        ean = rst.getString("id");
                    }

                    ProdutoAutomacaoVO vo = new ProdutoAutomacaoVO();
                    vo.setIdproduto(rst.getInt("id"));
                    vo.setIdTipoEmbalagem(rst.getInt("id_tipoembalagem"));
                    vo.setCodigoBarras(gerarEan13(Long.parseLong(ean), true));
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

    private Date dataInicioVenda;
    private Date dataTerminoVenda;

    public void setDataInicioVenda(Date dataInicioVenda) {
        this.dataInicioVenda = dataInicioVenda;
    }

    public void setDataTerminoVenda(Date dataTerminoVenda) {
        this.dataTerminoVenda = dataTerminoVenda;
    }
}
