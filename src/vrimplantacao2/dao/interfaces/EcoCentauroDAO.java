package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import static vr.core.utils.StringUtils.LOG;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao.utils.Utils;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao.vo.vrimplantacao.ProdutoAutomacaoVO;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.parametro.Parametros;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoItemIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;

/**
 *
 * @author Desenvolvimento
 */
public class EcoCentauroDAO extends InterfaceDAO implements MapaTributoProvider {

    private boolean importarCodigoPrincipal;
    private boolean versao2Tributacao;

    public boolean isVersao2Tributacao() {
        return this.versao2Tributacao;
    }

    public void setVersao2Tributacao(boolean versao2Tributacao) {
        this.versao2Tributacao = versao2Tributacao;
    }

    public boolean isImportarCodigoPrincipal() {
        return this.importarCodigoPrincipal;
    }

    public void setImportarCodigoPrincipal(boolean importarCodigoPrincipal) {
        this.importarCodigoPrincipal = importarCodigoPrincipal;
    }

    @Override
    public String getSistema() {
        return "Eco Centauro";
    }

    private Date vendaDataIni;
    private Date vendaDataFim;

    public void setVendaDataIni(Date vendaDataIni) {
        this.vendaDataIni = vendaDataIni;
    }

    public void setVendaDataFim(Date vendaDataFim) {
        this.vendaDataFim = vendaDataFim;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {

            if (isVersao2Tributacao()) {
                try (ResultSet rs = stm.executeQuery(
                        "SELECT \n"
                        + "	codigoid,\n"
                        + "	DESCRICAO,\n"
                        + "	csf cst\n"
                        + "FROM \n"
                        + "	TESTGRUPOICMS \n"
                        + "WHERE codigoid in\n"
                        + "(SELECT DISTINCT grupoicms FROM TESTPRODUTOGERAL)")) {
                    while (rs.next()) {
                        result.add(new MapaTributoIMP(rs.getString("codigoid"),
                                rs.getString("descricao"),
                                rs.getInt("cst"),
                                0,
                                0));
                    }
                }
            } else {
                try (ResultSet rs = stm.executeQuery(
                        "SELECT DISTINCT\n"
                        + "    icm.vendacsf1 AS cst_saida,\n"
                        + "    icm.vendaicms1 AS aliquota_saida,\n"
                        + "    icm.vendareducao1 AS reducao_saida\n"
                        + "FROM TESTPRODUTOGERAL pg\n"
                        + "JOIN TESTICMS icm ON icm.produto = pg.codigo\n"
                        + "    AND icm.empresa = " + getLojaOrigem() + "\n"
                        + "    AND icm.estado = 'MT'"
                )) {
                    while (rs.next()) {
                        String id = rs.getString("cst_saida") + "-" + rs.getString("aliquota_saida") + "-" + rs.getString("reducao_saida");
                        result.add(new MapaTributoIMP(
                                id,
                                id,
                                rs.getInt("cst_saida"),
                                rs.getDouble("aliquota_saida"),
                                rs.getDouble("reducao_saida")
                        )
                        );
                    }
                }

                try (ResultSet rs = stm.executeQuery(
                        "SELECT DISTINCT\n"
                        + "    icm.compracsf AS cst_entrada,\n"
                        + "    icm.compraicms AS aliquota_entrada,\n"
                        + "    icm.comprareducao AS reducao_entrada\n"
                        + "FROM TESTPRODUTOGERAL pg\n"
                        + "JOIN TESTICMS icm ON icm.produto = pg.codigo\n"
                        + "    AND icm.empresa = " + getLojaOrigem() + "\n"
                        + "    AND icm.estado = 'MT'"
                )) {
                    while (rs.next()) {
                        String id = rs.getString("cst_entrada") + "-" + rs.getString("aliquota_entrada") + "-" + rs.getString("reducao_entrada");
                        result.add(new MapaTributoIMP(
                                id,
                                id,
                                rs.getInt("cst_entrada"),
                                rs.getDouble("aliquota_entrada"),
                                rs.getDouble("reducao_entrada")
                        )
                        );
                    }
                }
            }
        }

        return result;
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                OpcaoProduto.FAMILIA,
                OpcaoProduto.FAMILIA_PRODUTO,
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.VALIDADE,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.QTD_EMBALAGEM_COTACAO,
                OpcaoProduto.QTD_EMBALAGEM_EAN,
                OpcaoProduto.ATIVO,
                OpcaoProduto.PESO_BRUTO,
                OpcaoProduto.PESO_LIQUIDO,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.ESTOQUE_MINIMO,
                OpcaoProduto.MARGEM,
                OpcaoProduto.VENDA_PDV,
                OpcaoProduto.PRECO,
                OpcaoProduto.CUSTO,
                OpcaoProduto.CUSTO_COM_IMPOSTO,
                OpcaoProduto.CUSTO_SEM_IMPOSTO,
                OpcaoProduto.NCM,
                OpcaoProduto.EXCECAO,
                OpcaoProduto.CEST,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.NATUREZA_RECEITA,
                OpcaoProduto.ICMS,
                OpcaoProduto.DATA_CADASTRO
        ));
    }

    @Override
    public Set<OpcaoFornecedor> getOpcoesDisponiveisFornecedor() {
        return new HashSet<>(Arrays.asList(
                OpcaoFornecedor.DADOS,
                OpcaoFornecedor.RAZAO_SOCIAL,
                OpcaoFornecedor.NOME_FANTASIA,
                OpcaoFornecedor.CNPJ_CPF,
                OpcaoFornecedor.INSCRICAO_ESTADUAL,
                OpcaoFornecedor.INSCRICAO_MUNICIPAL,
                OpcaoFornecedor.PRODUTO_FORNECEDOR,
                OpcaoFornecedor.PAGAR_FORNECEDOR
        ));
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "    codigo,\n"
                    + "    nomefantasia,\n"
                    + "    cpfcnpj\n"
                    + "FROM TGEREMPRESA\n"
                    + "ORDER BY 1"
            )) {
                while (rst.next()) {
                    result.add(
                            new Estabelecimento(
                                    rst.getString("codigo"),
                                    rst.getString("nomefantasia") + "-" + rst.getString("cpfcnpj")
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
                    "SELECT\n"
                    + "    m1.codigo AS merc1,\n"
                    + "    m1.descricao AS desc_merc1,\n"
                    + "    m2.subgrupo AS merc2,\n"
                    + "    m2.descricao AS desc_merc2\n"
                    + "FROM TESTGRUPO m1\n"
                    + "LEFT JOIN TESTSUBGRUPO m2 ON m2.grupo = m1.codigo\n"
                    + "WHERE m1.empresa = '" + getLojaOrigem() + "'\n"
                    + "AND m2.empresa = '" + getLojaOrigem() + "'\n"
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
                    "SELECT \n"
                    + "    pg.produtoprincipal AS id,\n"
                    + "    pg.codigobarra AS ean,\n"
                    + "    pg.descricao AS descricaocompleta,\n"
                    + "    pg.referencia,\n"
                    + "    pg.descricaoreduzida AS descricaoreduzida,\n"
                    + "    pg.descricaograde AS descricaogondola,\n"
                    + "    pg.embalagem AS tipoembalagem,\n"
                    + "    pg.qtdeembalagem AS qtdembalagem,\n"
                    + "    pg.pesobruto AS pesobruto,\n"
                    + "    pg.pesoliquido AS pesoliquido,\n"
                    + "    pg.datacadastro AS datacadastro,\n"
                    + "    pg.classificacaofiscal AS ncm,\n"
                    + "    c.cest as cest,\n"
                    + "    p.custofabrica AS custosemimposto,\n"
                    + "    p.custofinal AS custocomimposto,\n"
                    + "    p.margemlucro AS margem,\n"
                    + "    p.prpraticado AS precovenda,\n"
                    + "    p.estoqueminimo AS estoqueminimo,\n"
                    + "    p.estoquemaximo AS estoquemaximo,\n"
                    + "    p.estdisponivel AS estoque,\n"
                    + "    p.grupo AS mercaologico1,\n"
                    + "    p.subgrupo AS mercadologico2,\n"
                    + "    '1' AS mercadologico3,\n"
                    + "    CASE p.ativo WHEN 'S' THEN 1 ELSE 0 END situacaocadastro,\n"
                    + "    CASE p.SETOR WHEN '001' THEN 1 ELSE 0 END ebalanca,\n"
                    + "    coalesce(pg.diasvalidade,0) as validade,\n"
                    + "    pg.grupoicms id_icms,\n"
                    + "    icm_s.vendacsf1 AS cst_saida,\n"
                    + "    icm_s.vendaicms1 AS aliquota_saida,\n"
                    + "    icm_s.vendareducao1 AS reducao_saida,\n"
                    + "    icm_e.compracsf AS cst_entrada,\n"
                    + "    icm_e.compraicms AS aliquota_entrada,\n"
                    + "    icm_e.comprareducao AS reducao_entrada\n"
                    + "FROM TESTPRODUTOGERAL pg\n"
                    + "LEFT JOIN TESTPRODUTO p ON p.produto = pg.codigo\n"
                    + "    AND p.empresa = '" + getLojaOrigem() + "'\n"
                    + "LEFT JOIN TESTGRUPOICMS gi ON gi.codigoid = pg.grupoicms\n"
                    + "LEFT JOIN TESTICMS icm_s on icm_s.produto = pg.codigo\n"
                    + "    AND icm_s.empresa = '" + getLojaOrigem() + "'\n"
                    + "    AND icm_s.estado = '" + Parametros.get().getUfPadraoV2().getSigla() + "'\n"
                    + "LEFT JOIN TESTICMS icm_e on icm_e.produto = pg.codigo\n"
                    + "    AND icm_e.empresa = '" + getLojaOrigem() + "'\n"
                    + "    AND icm_e.estado = '" + Parametros.get().getUfPadraoV2().getSigla() + "'\n"
                    + "LEFT JOIN TESTCEST c on c.idcest = pg.idcest\n"
                    + "WHERE pg.descricao NOT LIKE '%BASE CORROMPIDA%'\n"
//                    + "AND pg.codigo IN (SELECT produtoprincipal FROM TESTPRODUTOGERAL)"
//                    + (isVersao2Tributacao() ? " AND icm_s.produto IS NULL " : "")
                    + "ORDER BY 1"
            )) {
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().getProdutosBalanca();
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setEan(rst.getString("ean"));

                    long longEAN = Utils.stringToLong(imp.getEan(), -2);
                    String strEAN = String.valueOf(longEAN);

                    if (strEAN.startsWith("999000") && strEAN.length() == 13) {
                        final String eanBal = strEAN.substring(7, strEAN.length() - 1);
                        final int plu = Utils.stringToInt(eanBal, -1);
                        ProdutoBalancaVO bal = produtosBalanca.get(plu);
                        if (bal != null) {
                            imp.setEan(String.valueOf(bal.getCodigo()));
                            imp.seteBalanca(true);
                            imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
                            imp.setValidade(bal.getValidade());
                        } else {
                            imp.setEan(eanBal);
                            imp.seteBalanca(rst.getBoolean("ebalanca"));
                            imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
                            imp.setValidade(rst.getInt("validade"));
                        }
                    } else {
                        imp.seteBalanca(rst.getBoolean("ebalanca"));
                        imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
                        imp.setValidade(rst.getInt("validade"));
                    }

                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(rst.getString("descricaogondola"));
                    imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                    imp.setPesoBruto(rst.getDouble("pesobruto"));
                    imp.setPesoLiquido(rst.getDouble("pesoliquido"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setCodMercadologico1(rst.getString("mercaologico1"));
                    imp.setCodMercadologico2(rst.getString("mercadologico2"));
                    imp.setCodMercadologico3(rst.getString("mercadologico3"));
                    imp.setSituacaoCadastro(rst.getInt("situacaocadastro"));
                    imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
                    imp.setEstoqueMaximo(rst.getDouble("estoquemaximo"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));

                    if (isVersao2Tributacao()) {
                        imp.setIcmsDebitoId(rst.getString("id_icms"));
                        imp.setIcmsDebitoForaEstadoId(imp.getIcmsDebitoId());
                        imp.setIcmsDebitoForaEstadoNfId(imp.getIcmsDebitoId());
                        imp.setIcmsCreditoId(imp.getIcmsDebitoId());
                        imp.setIcmsCreditoForaEstadoId(imp.getIcmsDebitoId());
                        imp.setIcmsConsumidorId(imp.getIcmsDebitoId());
                    } else {
                        String idIcmsDebito, IdIcmsCredito;

                        idIcmsDebito = rst.getString("cst_saida") + "-"
                                + rst.getString("aliquota_saida") + "-"
                                + rst.getString("reducao_saida");

                        IdIcmsCredito = rst.getString("cst_entrada") + "-"
                                + rst.getString("aliquota_entrada") + "-"
                                + rst.getString("reducao_entrada");

                        imp.setIcmsDebitoId(idIcmsDebito);
                        imp.setIcmsDebitoForaEstadoId(idIcmsDebito);
                        imp.setIcmsDebitoForaEstadoNfId(idIcmsDebito);
                        imp.setIcmsCreditoId(IdIcmsCredito);
                        imp.setIcmsCreditoForaEstadoId(IdIcmsCredito);
                        imp.setIcmsConsumidorId(idIcmsDebito);
                    }

                    if (rst.getString("referencia") != null && !rst.getString("referencia").trim().isEmpty()) {
                        if (!rst.getString("descricaocompleta").contains(rst.getString("referencia").trim())) {
                            imp.setDescricaoCompleta(rst.getString("descricao") + " " + rst.getString("referencia"));
                        }
                    }
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
                    "SELECT \n"
                    + "    produtoprincipal AS idproduto,\n"
                    + "    codigobarra AS ean,\n"
                    + "    embalagem AS tipoembalagem,\n"
                    + "    qtdeembalagem AS qtdembalagem\n"
                    + "FROM TESTPRODUTOGERAL\n"
                    + "ORDER BY 1"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("idproduto"));
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
                    "SELECT\n"
                    + "    f.codigo AS id,\n"
                    + "    f.nome AS razao,\n"
                    + "    f.fantasia AS fantasia,\n"
                    + "    f.cpfcnpj AS cnpj,\n"
                    + "    f.rgie AS inscricaoestadual,\n"
                    + "    f.endereco,\n"
                    + "    f.numeroendereco AS numero,\n"
                    + "    f.complemento,\n"
                    + "    f.bairro,\n"
                    + "    cid.nome AS municipio,\n"
                    + "    cid.estado AS uf,\n"
                    + "    f.cep,\n"
                    + "    f.fone AS telefone,\n"
                    + "    f.fax,\n"
                    + "    f.homepage AS email2,\n"
                    + "    f.email,\n"
                    + "    f.fonecomervend,\n"
                    + "    f.foneresidvend,\n"
                    + "    f.fonetelevendas,\n"
                    + "    f.celularvend,\n"
                    + "    f.emailvend,\n"
                    + "    f.datacadastro,\n"
                    + "    f.observacao,\n"
                    + "    f.contribuinte,\n"
                    + "    CASE f.ativo WHEN 'S' THEN 1 ELSE 0 END ativo\n"
                    + "FROM tpagfornecedor f\n"
                    + "LEFT JOIN tgercidade cid ON cid.codigo = f.cidade\n"
                    + "ORDER BY 1"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    imp.setIe_rg(rst.getString("inscricaoestadual"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setAtivo(rst.getInt("ativo") == 1);
                    imp.setTel_principal(rst.getString("telefone"));
                    imp.setDatacadastro(rst.getDate("datacadastro"));
                    imp.setObservacao(rst.getString("observacao"));
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
                    "SELECT\n"
                    + "    fornecedor AS idfornecedor,\n"
                    + "    produto AS idproduto,\n"
                    + "    ultcompraqtde AS qtdembalagem,\n"
                    + "    ultcompradata AS dataalteracao,\n"
                    + "    codnofornecedor AS codigoexterno\n"
                    + "FROM testfornecproduto\n"
                    + "ORDER BY 1, 2"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rst.getString("idproduto"));
                    imp.setIdFornecedor(rst.getString("idfornecedor"));
                    imp.setCodigoExterno(rst.getString("codigoexterno"));
                    imp.setQtdEmbalagem(rst.getDouble("qtdembalagem"));
                    imp.setDataAlteracao(rst.getDate("dataalteracao"));
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
            try (ResultSet rs = stm.executeQuery(
                    "SELECT \n"
                    + " cp.IDSEQUENCIA id,\n"
                    + " cp.FORNECEDOR idfornecedor,\n"
                    + " cpfcnpj cnpj,\n"
                    + " cp.DOCUMENTO numerodocumento,\n"
                    + " d.EMISSAO dataemissao,\n"
                    + " d.DATADIGITACAO dataentrada,\n"
                    + " cp.VALOR, \n"
                    + " cp.VENCIMENTO datavencimento,\n"
                    + " COALESCE (cp.OBS,'')||cp.DOCUMENTO||'-'||cp.PARCELA AS OBS \n"
                    + "FROM \n"
                    + "	TPAGPARCELA cp\n"
                    + "	LEFT JOIN TPAGDOCUMENTO d ON d.DOCUMENTO = cp.DOCUMENTO \n"
                    + "	LEFT JOIN TPAGFORNECEDOR f ON cp.FORNECEDOR = f.CODIGO \n"
                    + "WHERE \n"
                    + "	cp.EMPRESA = " + getLojaOrigem() + "\n"
                    + "	AND cp.DATABAIXA IS NULL"
            )) {
                while (rs.next()) {
                    ContaPagarIMP imp = new ContaPagarIMP();

                    imp.setId(rs.getString("id"));
                    imp.setIdFornecedor(rs.getString("idfornecedor"));
                    imp.setCnpj(rs.getString("cnpj"));
                    imp.setNumeroDocumento(rs.getString("numerodocumento"));
                    imp.setDataEmissao(rs.getDate("dataemissao"));
                    imp.setDataEntrada(rs.getDate("dataentrada"));
                    imp.setValor(rs.getDouble("valor"));
                    imp.setVencimento(rs.getDate("datavencimento"));
                    imp.setObservacao(rs.getString("obs"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        DateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "    c.codigo id,\n"
                    + "    c.nome,\n"
                    + "    C.FANTASIA,\n"
                    + "    C.CPFCNPJ,\n"
                    + "    C.RGIE,\n"
                    + "    C.INSCRICAOMUNICIPAL im,\n"
                    + "    C.PESSOA tipopessoa,\n"
                    + "    C.ENDERECO,\n"
                    + "    C.NUMEROENDERECO numero,\n"
                    + "    C.BAIRRO,\n"
                    + "    C.COMPLEMENTO,\n"
                    + "    m.nome CIDADE,\n"
                    + "    m.ESTADO uf,\n"
                    + "    C.CEP,\n"
                    + "    coalesce(SUBSTRING(P.datanasc from 9 FOR 2)||'/'||SUBSTRING(P.datanasc from 6 FOR 2)||'/'||SUBSTRING(P.datanasc from 1 FOR 4), '') datanascimento,\n"
                    + "    C.datacadastro data_cadastro,\n"
                    + "    CASE WHEN C.bloqueado = 'S' THEN 1 ELSE 0 END BLOQUEADO,\n"
                    + "    p.localtrabalho empresa,\n"
                    + "    C.LIMITE,\n"
                    + "    p.nomepai,\n"
                    + "    p.nomemae,\n"
                    + "    C.FONE telefone,\n"
                    + "    C.FAX,\n"
                    + "    C.FONECELULAR celular,\n"
                    + "    C.EMAIL,\n"
                    + "    C.OBS\n"
                    + "FROM\n"
                    + "    TRECCLIENTEGERAL c\n"
                    + "LEFT JOIN TGERCIDADE m ON m.CODIGO = c.CIDADE\n"
                    + "left JOIN trecpfisica p ON  C.codigo = P.codigo\n"
                    + "ORDER BY 1"
            )) {
                while (rs.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rs.getString("id"));
                    imp.setRazao(rs.getString("nome"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setCnpj(rs.getString("CPFCNPJ"));
                    imp.setInscricaoestadual(rs.getString("RGIE"));
                    imp.setInscricaoMunicipal(rs.getString("im"));
                    imp.setBloqueado(rs.getBoolean("bloqueado"));

                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));

                    imp.setDataCadastro(rs.getDate("data_cadastro"));

                    if (rs.getString("datanascimento") != null && !rs.getString("datanascimento").trim().isEmpty()) {
                        imp.setDataNascimento(new java.sql.Date(fmt.parse(rs.getString("datanascimento")).getTime()));
                    }

                    imp.setBloqueado(rs.getBoolean("bloqueado"));
                    imp.setValorLimite(rs.getDouble("limite"));

                    imp.setTelefone(rs.getString("telefone"));
                    imp.setFax(rs.getString("fax"));
                    imp.setCelular(rs.getString("celular"));
                    imp.setEmail(rs.getString("email"));
                    imp.setObservacao(rs.getString("obs"));

                    imp.setBloqueado(rs.getInt("BLOQUEADO") == 1);
                    imp.setNomePai(rs.getString("nomepai"));
                    imp.setNomeMae(rs.getString("nomemae"));
                    imp.setEmpresa(rs.getString("empresa"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    /*@Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT    \n"
                    + "        cr.IDTRECPARCELA id,    \n"
                    + "        cr.DOCUMENTO numerocupom,    \n"
                    + "        cr.valorpendente valor,\n"
                    + "        cr.CLIENTE codcli,    \n"
                    + "        cpfcnpj cnpjcliente,    \n"
                    + "        1 AS ecf,    \n"
                    + "        t.EMISSAO emissao,    \n"
                    + "        cr.vencimento    \n"
                    + "FROM    \n"
                    + "       TRECPARCELA cr    \n"
                    + "LEFT JOIN TRECCLIENTEGERAL c ON c.CODIGO = cr.CLIENTE    \n"
                    + "LEFT JOIN TRECDOCUMENTO t ON cr.DOCUMENTO = t.DOCUMENTO     \n"
                    + "WHERE    \n"
                    + "       cr.EMPRESA = '" + getLojaOrigem() + "'\n"
                    + "AND cr.tipo = '01'\n"
                    + "AND cr.databaixa IS NULL\n"
                    + "AND cr.valorpendente >0"
            )) {
                while (rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rs.getString("id"));
                    imp.setNumeroCupom(Utils.formataNumero(rs.getString("numerocupom")));
                    imp.setValor(rs.getDouble("valor"));
                    imp.setIdCliente(rs.getString("codcli"));
                    imp.setCnpjCliente(Utils.formataNumero(rs.getString("cnpjcliente")));
                    imp.setEcf(rs.getString("ecf"));
                    imp.setDataEmissao(rs.getDate("emissao"));
                    imp.setDataVencimento(rs.getDate("vencimento"));

                    result.add(imp);
                }
            }
        }
        return result;
    }*/
    
    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();

        try (Statement st = ConexaoFirebird.getConexao().createStatement()) {
            Map<String, List<CreditoRotativoItemIMP>> pagamentos = new HashMap<>();
            try (ResultSet rs = st.executeQuery(
                    "SELECT\n"
                    + "	 cr.IDTRECPARCELA id,\n"
                    + "	 cr.PARCELA,\n"
                    + "	 cr.valor valor,\n"
                    + "	 cr.databaixa data_pag,\n"
                    + "	 cr.obs observacao\n"
                    + "FROM\n"
                    + "	 TRECPARCELA cr\n"
                    + "LEFT JOIN TRECCLIENTEGERAL c ON c.CODIGO = cr.CLIENTE\n"
                    + "LEFT JOIN TRECDOCUMENTO t ON cr.DOCUMENTO = t.DOCUMENTO\n"
                    + "WHERE\n"
                    + "	 cr.EMPRESA = '" + getLojaOrigem() + "'\n"
                    + "	 AND cr.tipo in ('01','12')\n"
                    + "  AND DATABAIXA IS NOT NULL\n"
                    + "ORDER BY 1,2"
            )) {
                while (rs.next()) {
                    final String id = rs.getString("id") + "-" + rs.getString("parcela");
                    List<CreditoRotativoItemIMP> pag = pagamentos.get(id);
                    if (pag == null) {
                        pag = new ArrayList<>();
                        pagamentos.put(id, pag);
                    }
                    CreditoRotativoItemIMP imp = new CreditoRotativoItemIMP();

                    imp.setId(id);
                    imp.setValor(rs.getDouble("valor"));
                    imp.setDataPagamento(rs.getDate("data_pag"));
                    imp.setObservacao(rs.getString("observacao"));

                    pag.add(imp);
                }
            }

            try (ResultSet rs = st.executeQuery(
                    "SELECT\n"
                    + "  cr.IDTRECPARCELA id,\n"
                    + "	 cr.PARCELA,\n"
                    + "  t.EMISSAO emissao,\n"
                    + "  cr.DOCUMENTO numerocupom,\n"
                    + "  1 AS ecf,\n"
                    + "  cr.valorpendente valor,\n"
                    + "  cr.CLIENTE codcli,\n"
                    + "  cpfcnpj cnpjcliente,\n"
                    + "  cr.vencimento,\n"
                    + "	 cr.obs observacao\n"
                    + "FROM \n"
                    + "  TRECPARCELA cr \n"
                    + "LEFT JOIN TRECCLIENTEGERAL c ON c.CODIGO = cr.CLIENTE\n"
                    + "LEFT JOIN TRECDOCUMENTO t ON cr.DOCUMENTO = t.DOCUMENTO\n"
                    + "WHERE\n"
                    + "  cr.EMPRESA = '" + getLojaOrigem() + "'\n"
                    + "	 AND cr.tipo in ('01','12')"
            )) {
                while (rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();

                    final String id = rs.getString("id") + "-" + rs.getString("parcela");

                    imp.setId(id);
                    imp.setParcela(rs.getInt("parcela"));
                    imp.setDataEmissao(rs.getDate("emissao"));
                    imp.setNumeroCupom(rs.getString("numerocupom"));
                    imp.setEcf(rs.getString("ecf"));
                    imp.setValor(rs.getDouble("valor"));
                    imp.setIdCliente(rs.getString("codcli"));
                    imp.setCnpjCliente(rs.getString("cnpjcliente"));
                    imp.setDataVencimento(rs.getDate("vencimento"));
                    imp.setObservacao(rs.getString("observacao"));
                    
                    List<CreditoRotativoItemIMP> pag = pagamentos.get(id);
                    if (pag != null) {
                        for (CreditoRotativoItemIMP pg : pag) {
                            pg.setCreditoRotativo(imp);
                            imp.getPagamentos().add(pg);
                        }
                    }

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
                    + "order by id"
            )) {
                while (rst.next()) {
                    ProdutoAutomacaoVO vo = new ProdutoAutomacaoVO();
                    vo.setIdproduto(rst.getInt("id"));
                    vo.setIdTipoEmbalagem(rst.getInt("id_tipoembalagem"));
                    vo.setCodigoBarras(gerarEan13(Long.parseLong(rst.getString("id")), true));
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

    @Override
    public Iterator<VendaIMP> getVendaIterator() throws Exception {
        return new EcoCentauroDAO.VendaIterator(getLojaOrigem(), this.vendaDataIni, this.vendaDataFim);
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        return new EcoCentauroDAO.VendaItemIterator(getLojaOrigem(), this.vendaDataIni, this.vendaDataFim);
    }

    private static class VendaIterator implements Iterator<VendaIMP> {

        public final static SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");

        private Statement stm = ConexaoFirebird.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaIMP next;
        private Set<String> uk = new HashSet<>();

        private void obterNext() {
            try {
                SimpleDateFormat timestampDate = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat timestamp = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                if (next == null) {
                    if (rst.next()) {
                        next = new VendaIMP();
                        String id = rst.getString("id_venda");
                        if (!uk.add(id)) {
                            LOG.warning("Venda " + id + " já existe na listagem");
                        }
                        next.setId(id);
                        next.setNumeroCupom(Utils.stringToInt(rst.getString("numerocupom")));
                        next.setIdClientePreferencial(rst.getString("id_cliente"));
                        next.setEcf(Utils.stringToInt(rst.getString("ecf")));
                        next.setData(rst.getDate("data"));

                        String horaInicio = timestampDate.format(rst.getDate("data")) + " " + rst.getString("hora");
                        String horaTermino = timestampDate.format(rst.getDate("data")) + " " + rst.getString("hora");
                        next.setHoraInicio(timestamp.parse(horaInicio));
                        next.setHoraTermino(timestamp.parse(horaTermino));
                        next.setValorDesconto(rst.getDouble("vlr_desc"));
                        next.setSubTotalImpressora(rst.getDouble("subtotalimpressora"));
                        next.setCancelado(rst.getBoolean("cancelado"));

                    }
                }
            } catch (SQLException | ParseException ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {

            String strDataInicio = new SimpleDateFormat("yyyy-MM-dd").format(dataInicio);
            String strDataTermino = new SimpleDateFormat("yyyy-MM-dd").format(dataTermino);
            this.sql
                    = "SELECT\n"
                    + "	CODIGO id_venda,\n"
                    + "	CASE \n"
                    + "	  WHEN NUMERONFCE IS NULL THEN CODIGO \n"
                    + "	  WHEN NUMERONFCE = '0' THEN CODIGO\n"
                    + "	  ELSE NUMERONFCE \n"
                    + " END numerocupom,\n"
                    + "	CLIENTE id_cliente,\n"
                    + "	DATA,\n"
                    + "	HORA,\n"
                    + "	VALORBRUTO,\n"
                    + "	VALORDESCONTO vlr_desc,\n"
                    + "	VALORLIQUIDO subtotalimpressora,\n"
                    + "	CASE\n"
                    + "	 WHEN STATUS = 'CAN' THEN 1\n"
                    + "	 ELSE 0\n"
                    + "	END cancelado,\n"
                    + "	ESTACAO ecf\n"
                    + "FROM\n"
                    + "	TVENPEDIDO\n"
                    + "WHERE\n"
                    + "	EMPRESA = '" + idLojaCliente + "'\n"
                    + "	AND DATA BETWEEN '" + strDataInicio + "' AND '" + strDataTermino + "'\n"
                    + " ORDER BY 1";
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
    }

    private static class VendaItemIterator implements Iterator<VendaItemIMP> {

        private Statement stm = ConexaoFirebird.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaItemIMP next;

        private void obterNext() {
            try {
                if (next == null) {
                    if (rst.next()) {
                        next = new VendaItemIMP();

                        next.setVenda(rst.getString("id_venda"));
                        next.setId(rst.getString("id_item"));
                        //next.setSequencia(rst.getInt("nroitem"));
                        next.setProduto(rst.getString("produto"));
                        next.setCodigoBarras(rst.getString("ean"));
                        next.setDescricaoReduzida(rst.getString("descricao"));
                        next.setUnidadeMedida(rst.getString("unidade"));
                        next.setQuantidade(rst.getDouble("quantidade"));
                        next.setPrecoVenda(rst.getDouble("precovenda"));
                        //next.setValorDesconto(rst.getDouble("desconto"));

                    }
                }
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaItemIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {
            this.sql
                    = /*"SELECT\n"
                    + "	REPLACE(v.EMPRESA||v.DATA||v.PDV||v.ECF||v.CUPOM,'-','') as id_venda,\n"
                    + "	REPLACE(vi.EMPRESA || vi.DATA || vi.PDV || vi.ECF || vi.CUPOM || vi.PRODUTO || vi.ITEM, '-', '') AS id_item,\n"
                    + "	vi.ITEM nroitem, \n"
                    + "	vi.PRODUTO,\n"
                    + "	p.PD_UNIDADE unidade,\n"
                    + " substring(vi.PRODUTO_BARRAS from 1 for 14) codigobarras,\n"
                    + "	p.DSC descricao,\n"
                    + "	vi.QUANTIDADE,\n"
                    + "	vi.VLR_UNT precovenda,\n"
                    + " (vi.vlr_ad*-1) desconto\n"
                    + "FROM\n"
                    + "	SM_MV_PDV_IT_CUP vi\n"
                    + "JOIN SM_MV_PDV_CB_CUP v\n"
                    + "	ON v.CUPOM = vi.CUPOM \n"
                    + "	  AND v.EMPRESA = vi.EMPRESA\n"
                    + "	  AND v.DATA = vi.DATA\n"
                    + "	  AND v.PDV = vi.PDV\n"
                    + "	  AND v.ECF = vi.ECF\n"
                    + "JOIN SM_CD_ES_PRODUTO p ON p.COD = vi.PRODUTO \n"
                    + "WHERE\n"
                    + "	v.EMPRESA = " + idLojaCliente + "\n"
                    + " and v.modelo is not null and vi.ic <> 1\n"
                    + "	AND v.DATA BETWEEN '" + VendaIterator.FORMAT.format(dataInicio) + "' and '" + VendaIterator.FORMAT.format(dataTermino) + "'\n"
                    + "ORDER BY 1,3"*/ "SELECT\n"
                    + "	PEDIDO id_venda,\n"
                    + "	IDENTIFICADOR id_item,\n"
                    + "	PRODUTO produto,\n"
                    + "	p.CODIGOBARRA ean,\n"
                    + "	p.DESCRICAOREDUZIDA descricao,\n"
                    + "	p.EMBALAGEM unidade,\n"
                    + "	QTDE quantidade,\n"
                    + "	PRVENDIDO precovenda\n"
                    + "FROM\n"
                    + "	TVENPRODUTO vi\n"
                    + "	LEFT JOIN TESTPRODUTOGERAL p ON p.CODIGO = vi.PRODUTO \n"
                    + "WHERE\n"
                    + "	EMPRESA = '" + idLojaCliente + "'\n"
                    + " AND DATA BETWEEN '" + VendaIterator.FORMAT.format(dataInicio) + "' and '" + VendaIterator.FORMAT.format(dataTermino) + "'\n"
                    + "ORDER BY 1,2";
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
    }
}
