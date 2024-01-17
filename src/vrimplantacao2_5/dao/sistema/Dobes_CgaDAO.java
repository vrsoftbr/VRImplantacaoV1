/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import vrframework.classe.ProgressBar;
import vrimplantacao.dao.cadastro.OfertaDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoEstadoCivil;
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao.vo.vrimplantacao.OfertaVO;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.vo.cadastro.financeiro.contareceber.OpcaoContaReceber;
import vrimplantacao2.vo.importacao.ChequeIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.ContaReceberIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.OfertaIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2_5.dao.conexao.ConexaoFirebird;

/**
 *
 * @author Desenvolvimento
 */
public class Dobes_CgaDAO extends InterfaceDAO implements MapaTributoProvider {

    private String lojaCliente;

    public String getLojaCliente() {
        return this.lojaCliente;
    }

    @Override
    public String getSistema() {
        return "Dobes CGA";
    }

    /*@Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "	ret016.\"ALIQCod\",\n"
                    + "	ret016.\"ALIQDesc\",\n"
                    + "	ret016.\"ALIQNFPerc\",\n"
                    + "	ret016.\"ALIQRedNF\",\n"
                    + "	ret016.\"ALIQPerc\",\n"
                    + "	CASE\n"
                    + "		WHEN ret016.\"ALIQRedNF\" > 0 THEN 20\n"
                    + "		ELSE 0\n"
                    + "	END cst\n"
                    + "FROM ret016\n"
                    + "ORDER BY ret016.\"ALIQCod\" ASC"
            )) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(
                            rs.getString("ALIQCod"),
                            rs.getString("ALIQDesc")
                            + " ALIQ. " + rs.getString("ALIQNFPerc")
                            + " RED. " + rs.getString("ALIQRedNF"),
                            rs.getInt("cst"),
                            rs.getFloat("ALIQNFPerc"),
                            rs.getFloat("ALIQRedNF")));
                }
            }
        }
        return result;
    }*/
    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "	ret016.\"ALIQCod\" id,\n"
                    + "	ret016.\"ALIQDesc\" descricao,\n"
                    + "	CASE \n"
                    + "	  WHEN ret016.\"ALIQDesc\" LIKE '%RED%' THEN 20\n"
                    + "	  WHEN ret016.\"ALIQDesc\" LIKE '%SUBST%' THEN 60\n"
                    + "	  WHEN ret016.\"ALIQDesc\" LIKE '%ISENTO%' THEN 40\n"
                    + "	  WHEN ret016.\"ALIQDesc\" LIKE '%NÃO TRIB%' THEN 41\n"
                    + "	  ELSE 0 END cst_saida,\n"
                    + "	ret016.\"ALIQNFPerc\" aliquota_saida,\n"
                    + "	ret016.\"ALIQRedNF\" reducao_saida\n"
                    + "FROM ret016\n"
                    + "ORDER BY ret016.\"ALIQCod\" ASC"
            )) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(
                            rs.getString("id"),
                            rs.getString("descricao"),
                            rs.getInt("cst_saida"),
                            rs.getDouble("aliquota_saida"),
                            rs.getDouble("reducao_saida"))
                    );
                }
            }
        }

        return result;
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.ASSOCIADO,
                OpcaoProduto.DATA_CADASTRO,
                OpcaoProduto.QTD_EMBALAGEM_COTACAO,
                OpcaoProduto.QTD_EMBALAGEM_EAN,
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.VALIDADE,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                //OpcaoProduto.MERCADOLOGICO_POR_NIVEL,
                OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                OpcaoProduto.FAMILIA,
                OpcaoProduto.FAMILIA_PRODUTO,
                OpcaoProduto.ATIVO,
                OpcaoProduto.PESO_BRUTO,
                OpcaoProduto.PESO_LIQUIDO,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.TROCA,
                OpcaoProduto.MARGEM,
                OpcaoProduto.VENDA_PDV,
                OpcaoProduto.PDV_VENDA,
                OpcaoProduto.PRECO,
                OpcaoProduto.CUSTO,
                OpcaoProduto.CUSTO_COM_IMPOSTO,
                OpcaoProduto.CUSTO_SEM_IMPOSTO,
                OpcaoProduto.NCM,
                OpcaoProduto.CEST,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.NATUREZA_RECEITA,
                OpcaoProduto.ICMS,
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.ATUALIZAR_SOMAR_ESTOQUE,
                OpcaoProduto.OFERTA,
                OpcaoProduto.DESCONTINUADO,
                OpcaoProduto.VOLUME_QTD,
                OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                OpcaoProduto.FABRICANTE,
                OpcaoProduto.NUTRICIONAL,
                OpcaoProduto.RECEITA,
                OpcaoProduto.RECEITA_BALANCA,
                OpcaoProduto.PAUTA_FISCAL,
                OpcaoProduto.PAUTA_FISCAL_PRODUTO,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.TIPO_PRODUTO
        ));
    }

    @Override
    public Set<OpcaoFornecedor> getOpcoesDisponiveisFornecedor() {
        return new HashSet<>(Arrays.asList(
                OpcaoFornecedor.DADOS,
                OpcaoFornecedor.ENDERECO,
                OpcaoFornecedor.CONTATOS,
                OpcaoFornecedor.SITUACAO_CADASTRO,
                OpcaoFornecedor.TIPO_EMPRESA,
                OpcaoFornecedor.PAGAR_FORNECEDOR,
                OpcaoFornecedor.PRODUTO_FORNECEDOR,
                OpcaoFornecedor.CONDICAO_PAGAMENTO,
                OpcaoFornecedor.OBSERVACAO,
                OpcaoFornecedor.PRAZO_FORNECEDOR
        ));
    }

    @Override
    public Set<OpcaoCliente> getOpcoesDisponiveisCliente() {
        return new HashSet<>(Arrays.asList(
                OpcaoCliente.DADOS,
                OpcaoCliente.ENDERECO,
                OpcaoCliente.ESTADO_CIVIL,
                OpcaoCliente.CONTATOS,
                OpcaoCliente.CLIENTE_EVENTUAL,
                OpcaoCliente.CONVENIO_EMPRESA,
                OpcaoCliente.CONVENIO_CONVENIADO,
                OpcaoCliente.CONVENIO_TRANSACAO,
                OpcaoCliente.DATA_CADASTRO,
                OpcaoCliente.DATA_NASCIMENTO,
                OpcaoCliente.VALOR_LIMITE,
                OpcaoCliente.VENCIMENTO_ROTATIVO,
                OpcaoCliente.RECEBER_CREDITOROTATIVO,
                OpcaoCliente.OUTRAS_RECEITAS));
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	ret011.\"SUBCod\",\n"
                    + "	ret011.\"SUBDesc\"\n"
                    + "FROM\n"
                    + "	ret011"
            )) {
                while (rst.next()) {
                    if ((rst.getString("SUBDesc") != null)
                            && (!rst.getString("SUBDesc").trim().isEmpty())) {
                        FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("SUBCod"));
                        imp.setDescricao(rst.getString("SUBDesc"));
                        vResult.add(imp);
                    }
                }
            }
        }
        return vResult;
    }

    @Override
    public List<OfertaIMP> getOfertas(Date dataTermino) throws Exception {
        List<OfertaIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "	ret051.\"PRODCod\",\n"
                    + "	ret051.\"PRODVendaPR\",\n"
                    + "	ret051.\"PRODVenda\",\n"
                    + "	ret051.\"PRODPromoIN\",\n"
                    + "	ret051.\"PRODPromoFM\"\n"
                    + "FROM\n"
                    + "	ret051\n"
                    + "WHERE\n"
                    + "	ret051.\"PRODPromoFM\" >= current_date")) {
                while (rs.next()) {
                    OfertaIMP imp = new OfertaIMP();

                    imp.setIdProduto(rs.getString("PRODCod"));
                    imp.setDataInicio(rs.getDate("PRODPromoIN"));
                    imp.setDataFim(rs.getDate("PRODPromoFM"));
                    imp.setPrecoNormal(rs.getDouble("PRODVenda"));
                    imp.setPrecoOferta(rs.getDouble("PRODVendaPR"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	ret018.\"SECCod\" AS merc1,\n"
                    + "	ret018.\"SECDesc\" AS desc1,\n"
                    + "	ret019.\"GRUCod\" AS merc2,\n"
                    + "	ret019.\"GRUDesc\" AS desc2,\n"
                    + "	ret020.\"SUBGCod\" AS merc3,\n"
                    + "	ret020.\"SUBGDesc\" AS desc3\n"
                    + "FROM\n"
                    + "	ret018\n"
                    + "LEFT JOIN RET019 ON RET018.\"SECCod\" = RET019.\"SECCod\"\n"
                    + "LEFT JOIN ret020 ON RET020.\"GRUCod\" = RET019.\"GRUCod\"\n"
                    + "ORDER BY\n"
                    + "	RET018.\"SECCod\",\n"
                    + "	RET020.\"GRUCod\",\n"
                    + "	ret020.\"SUBGCod\""
            /*
                    "SELECT DISTINCT \n"
                    + "	ret019.\"GRUCod\" merc1,\n"
                    + "	ret019.\"GRUDesc\" desc1\n"
                    + "FROM\n"
                    + "	ret018\n"
                    + "LEFT JOIN RET019 ON RET018.\"SECCod\" = RET019.\"SECCod\"\n"
                    + "LEFT JOIN ret020 ON RET020.\"GRUCod\" = RET019.\"GRUCod\"\n"
                    + "ORDER BY 1"*/
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setMerc1ID(rst.getString("merc1"));
                    imp.setMerc1Descricao(rst.getString("desc1"));
                    imp.setMerc2ID(rst.getString("merc2"));
                    imp.setMerc2Descricao(rst.getString("desc2"));
                    imp.setMerc3ID(rst.getString("merc3"));
                    imp.setMerc3Descricao(rst.getString("desc3"));

                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    //                    "SELECT\n" +
                    //                    "	ret051.\"PRODCod\",\n" +
                    //                    "	ret051.\"PRODNome\",\n" +
                    //                    "	ret051.\"PRODNomeRed\",\n" +
                    //                    "	ret051.\"PRODEtq\",\n" +
                    //                    "	ret051.\"PRODCadast\",\n" +
                    //                    "	ret051.\"PRODCusto\",\n" +
                    //                    "	ret051.prodcustofinalvenda,\n" +
                    //                    "	ret051.\"PRODVenda\",\n" +
                    //                    "	ret051.\"PRODMargem\",\n" +
                    //                    "	ret051.prodcustofinal custocomimposto,\n" +
                    //                    "	ret051.\"PRODCompra\" custosemimposto,\n" +
                    //                    "	ret051.\"GRUCod\" merc1,\n" +
                    //                    "	ret051.\"GRUCod\" merc2,\n" +
                    //                    "	ret051.\"GRUCod\" merc3,\n" +
                    //                    "	ret051.prodai,\n" +
                    //                    "	ret051.\"PRODBARCod\" ean,\n" +
                    //                    "	ret041.CLASFISCDESC ncm,\n" +
                    //                    "	ret041.clasfisccest cest,\n" +
                    //                    "	ret051.natreccod,\n" +
                    //                    "	ret051.prodstcofinsent,\n" +
                    //                    "	ret051.prodstcofins,\n" +
                    //                    "	ret051.\"SUBCod\",\n" +
                    //                    "	ret051.prodsdo,\n" +
                    //                    "	prodqtemb,\n" +
                    //                    "	ret051.\"ALIQCod\" id_icms_saida,\n" +
                    //                    "	ret051.\"TABBCod\" cstSaida,\n" +
                    //                    "	al1.\"ALIQNFPerc\" aliqDebito,\n" +
                    //                    "	al1.\"ALIQRedNF\" redDebito,\n" +
                    //                    "	ret051.aliqcred,\n" +
                    //                    "	ret051.tabbcred cstEntrada,\n" +
                    //                    "	al2.\"ALIQNFPerc\" aliqCredito,\n" +
                    //                    "	al2.\"ALIQRedNF\" redCredito,\n" +
                    //                    "	ret051.\"PRODUnid\",\n" +
                    //                    "	ret051.\"FORCod\" idfornecedor\n" +
                    //                    "FROM\n" +
                    //                    "	RET051\n" +
                    //                    "LEFT JOIN ret041 ON ret041.clasfisccod = ret051.clasfisccod\n" +
                    //                    "LEFT JOIN RET053 ON RET053.\"PRODCod\" = ret051.\"PRODCod\"\n" +
                    //                    "LEFT JOIN ret016 al1 ON al1.\"ALIQCod\" = ret051.\"ALIQCod\"\n" +
                    //                    "LEFT JOIN ret016 al2 ON al2.\"ALIQCod\" = ret051.aliqcred\n" +
                    //                    "ORDER BY\n" +
                    //                    "	ret051.\"PRODCod\""
                    // SCRIPT REALIZADO DEVIDO A OUTRA VERSÃO

                    "	SELECT\n"
                    + "	ret051.\"PRODCod\",\n"
                    + "	ret051.\"PRODNome\",\n"
                    + "	ret051.\"PRODNomeRed\",\n"
                    + "	ret051.\"PRODEtq\",\n"
                    + "	ret051.\"PRODCadast\",\n"
                    + "	ret051.\"PRODCusto\",\n"
                    + "	ret051.prodcustofinalvenda,\n"
                    + "	ret051.\"PRODVenda\",\n"
                    + "	ret051.\"PRODMargem\",\n"
                    + "	ret051.prodcustofinal custocomimposto,\n"
                    + "	ret051.\"PRODCompra\" custosemimposto,\n"
                    + "	ret051.\"SECCod\" merc1,\n"
                    + "	ret051.\"GRUCod\" merc2,\n"
                    + "	ret051.\"SUBGCod\" merc3,\n"
                    + "	ret051.\"SUBCod\" merc4,\n"
                    + "	ret051.prodai,\n"
                    + "	ret051.\"PRODBARCod\" ean,\n"
                    + "	ret041.CLASFISCDESC ncm,\n"
                    + "	ret041.clasfisccest cest,\n"
                    + "	ret051.natreccod,\n"
                    + "	ret051.prodstcofinsent,\n"
                    + "	ret051.prodstcofins,\n"
                    + "	ret051.\"SUBCod\",\n"
                    + "	ret051.prodsdo,\n"
                    + "	prodqtemb,\n"
                    + "	ret051.\"ALIQCod\" id_icms_saida,\n"
                    + "	ret051.\"TABBCod\" cstSaida,\n"
                    + "	al1.\"ALIQNFPerc\" aliqDebito,\n"
                    + "	al1.\"ALIQRedNF\" redDebito,\n"
                    + "	ret051.aliqcred,\n"
                    + "	ret051.tabbcred cstEntrada,\n"
                    + "	al2.\"ALIQNFPerc\" aliqCredito,\n"
                    + "	al2.\"ALIQRedNF\" redCredito,\n"
                    + "	ret051.\"PRODUnid\",\n"
                    + "	ret051.\"FORCod\"\n"
                    + "FROM\n"
                    + "	RET051\n"
                    + "LEFT JOIN ret041 ON ret041.clasfisccod = ret051.clasfisccod\n"
                    + "LEFT JOIN RET053 ON RET053.\"PRODCod\" = ret051.\"PRODCod\"\n"
                    + "LEFT JOIN ret016 al1 ON al1.\"ALIQCod\" = ret051.\"ALIQCod\"\n"
                    + "LEFT JOIN ret016 al2 ON al2.\"ALIQCod\" = ret051.aliqcred"
            )) {
                int contador = 1;
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().getProdutosBalanca();
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    ProdutoBalancaVO produtoBalanca;
                    imp.setImportId(rst.getString("PRODCod"));
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setEan(rst.getString("ean"));

                    long codigoProduto;

                    codigoProduto = Long.parseLong(imp.getImportId().equals("PRICMS") ? "999999" : imp.getImportId());
                    if (codigoProduto <= Integer.MAX_VALUE) {
                        produtoBalanca = produtosBalanca.get((int) codigoProduto);
                    } else {
                        produtoBalanca = null;
                    }

                    if (produtoBalanca != null) {
                        imp.seteBalanca(true);
                        imp.setValidade(produtoBalanca.getValidade() > 1 ? produtoBalanca.getValidade() : 0);
                        imp.setEan(imp.getImportId());
                    } else {
                        imp.setValidade(0);
                        imp.seteBalanca(false);
                    }

                    imp.setDescricaoCompleta(Utils.acertarTexto(rst.getString("PRODNome")));
                    imp.setDescricaoReduzida(Utils.acertarTexto(rst.getString("PRODNomeRed")));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setDataCadastro(rst.getDate("PRODCadast"));
                    imp.setMargem(rst.getDouble("PRODMargem"));
                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    imp.setPrecovenda(rst.getDouble("PRODVenda"));
                    imp.setEstoque(rst.getDouble("prodsdo"));
                    imp.setCodMercadologico1(rst.getString("merc1"));
                    imp.setCodMercadologico2(rst.getString("merc2"));
                    imp.setCodMercadologico3(rst.getString("merc3"));
                    //imp.setCodMercadologico4(rst.getString("merc4"));
                    imp.setIdFamiliaProduto(rst.getString("SUBCod"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setQtdEmbalagemCotacao(rst.getInt("prodqtemb") == 0 ? 1 : rst.getInt("prodqtemb"));
                    imp.setTipoEmbalagem(rst.getString("PRODUnid"));
                    imp.setTipoEmbalagemCotacao(rst.getString("PRODUnid"));
                    imp.setFornecedorFabricante(rst.getString("FORCod"));

                    if ((rst.getString("prodai") != null)
                            && (!rst.getString("prodai").trim().isEmpty())) {
                        imp.setSituacaoCadastro(rst.getString("prodai").contains("A") ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    } else {
                        imp.setSituacaoCadastro(SituacaoCadastro.EXCLUIDO);
                    }

                    imp.setPiscofinsCstDebito(Integer.parseInt(Utils.formataNumero(rst.getString("prodstcofins"))));
                    imp.setPiscofinsCstCredito(Integer.parseInt(Utils.formataNumero(rst.getString("prodstcofinsent"))));
                    imp.setPiscofinsNaturezaReceita(rst.getString("natreccod"));

                    imp.setIcmsDebitoId(rst.getString("id_icms_saida"));
                    imp.setIcmsConsumidorId(rst.getString("id_icms_saida"));
                    imp.setIcmsDebitoForaEstadoId(rst.getString("id_icms_saida"));
                    imp.setIcmsDebitoForaEstadoNfId(rst.getString("id_icms_saida"));
                    imp.setIcmsCreditoId(rst.getString("id_icms_saida"));
                    imp.setIcmsCreditoForaEstadoId(rst.getString("id_icms_saida"));

                    vResult.add(imp);
                    contador++;
                    ProgressBar.setStatus("Carregando dados..." + contador);
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
                    "SELECT\n"
                    + "	ret052.\"BARCod\",\n"
                    + "	ret052.\"PRODCod\",\n"
                    + "	ret052.barunbxa,\n"
                    + "	ret051.\"PRODUnid\"\n"
                    + "FROM\n"
                    + "	RET052\n"
                    + "JOIN ret051 ON ret051.\"PRODCod\" = ret052.\"PRODCod\""
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("PRODCod"));
                    imp.setEan(rst.getString("BARCod"));
                    imp.setQtdEmbalagem(rst.getInt("barunbxa"));
                    imp.setTipoEmbalagem(rst.getString("PRODUnid"));
                    vResult.add(imp);
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
                    "SELECT\n"
                    + "	ret154.forcod,\n"
                    + "	ret154.prodcod,\n"
                    + "	ret154.prodbarcod,\n"
                    + "	ret154.codfabricante\n"
                    + "FROM\n"
                    + "	RET154"
            )) {
                int contador = 1;
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdFornecedor(rst.getString("forcod"));
                    imp.setIdProduto(rst.getString("prodcod"));
                    imp.setCodigoExterno(rst.getString("codfabricante"));
                    vResult.add(imp);
                    ProgressBar.setStatus("Carregando dados..." + contador);
                    contador++;
                }
            }
        }
        return vResult;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "    select\n"
                    + "    ret007.\"FORCod\",\n"
                    + "    ret007.\"FORRazao\",\n"
                    + "    ret007.\"FORFant\",\n"
                    + "    ret007.\"FOREnd\",\n"
                    + "    ret007.\"FORCep\",\n"
                    + "    ret501.cidibge,\n"
                    + "    ret501.\"CIDNome\",\n"
                    + "    ret501.ciduf,\n"
                    + "    ret007.\"FORBairro\",\n"
                    + "    ret007.fornumero,\n"
                    + "    ret007.forcomplemento,\n"
                    + "    coalesce(nullif(coalesce(trim(ret007.forcnpj),''),''),\n"
                    + "    nullif(coalesce(trim(ret007.forcpf),''),'')) forcnpjcpf,\n"
                    + "    ret007.forie,\n"
                    + "    ret007.forativo,\n"
                    + "    ret007.\"FORFone1\",\n"
                    + "    ret007.\"FORFone2\",\n"
                    + "    ret007.\"FORFax\",\n"
                    + "    ret007.\"FORContato\",\n"
                    + "    ret007.\"FORBco\",\n"
                    + "    ret007.\"FORAg\",\n"
                    + "    ret007.\"FORCta\",\n"
                    + "    ret007.\"FOREmail\",\n"
                    + "    ret007.forobs,\n"
                    + "    ret007.forobsmemo,\n"
                    + "    ret007.forinclusao,\n"
                    + "    ret007.\"FORRep\",\n"
                    + "    ret007.\"FORRepF1\",\n"
                    + "    ret007.\"FORRepF2\",\n"
                    + "    ret007.forrepemail\n"
                    + "from\n"
                    + "    ret007\n"
                    + "    left join ret501 on ret501.\"CIDCod\" = ret007.\"CIDCod\""
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("FORCod"));
                    imp.setRazao(rst.getString("FORRazao"));
                    imp.setFantasia(rst.getString("FORFant"));
                    imp.setEndereco(rst.getString("FOREnd"));
                    imp.setBairro(rst.getString("FORBairro"));
                    imp.setCep(rst.getString("FORCep"));
                    imp.setMunicipio(rst.getString("CIDNome"));
                    imp.setIbge_municipio(rst.getInt("cidibge"));
                    imp.setUf(rst.getString("ciduf"));
                    imp.setNumero(rst.getString("fornumero"));
                    imp.setComplemento(rst.getString("forcomplemento"));
                    imp.setCnpj_cpf(rst.getString("forcnpjcpf"));
                    imp.setIe_rg(rst.getString("forie"));
                    imp.setAtivo(true);
                    imp.setTel_principal(rst.getString("FORFone1"));
                    imp.setDatacadastro(rst.getDate("forinclusao"));
                    imp.setObservacao(rst.getString("forobsmemo"));
                    if ((rst.getString("FORFone2") != null)
                            && (!rst.getString("FORFone2").trim().isEmpty())) {
                        imp.addContato(
                                "1",
                                "TELEFONE 2",
                                rst.getString("FORFone2"),
                                null,
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    if ((rst.getString("FORFax") != null)
                            && (!rst.getString("FORFax").trim().isEmpty())) {
                        imp.addContato(
                                "1",
                                "FAX",
                                rst.getString("FORFax"),
                                null,
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    if ((rst.getString("FOREmail") != null)
                            && (!rst.getString("FOREmail").trim().isEmpty())) {
                        imp.addContato(
                                "1",
                                "EMAIL",
                                null,
                                null,
                                TipoContato.COMERCIAL,
                                rst.getString("FOREmail")
                        );
                    }
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	ret028.\"CLICod\",\n"
                    + "	ret028.\"CLINome\",\n"
                    + "	ret028.\"CLIFantasia\",\n"
                    + "	ret028.\"CLIContato\",\n"
                    + "	ret028.\"CLIEnd\",\n"
                    + "	ret028.\"CLIBairro\",\n"
                    + "	ret028.\"CLICep\",\n"
                    + "	ret501.cidibge,\n"
                    + "	ret501.\"CIDNome\",\n"
                    + "	ret501.ciduf,\n"
                    + "	ret028.\"CLIFone1\",\n"
                    + "	ret028.\"CLIFone2\",\n"
                    + "	ret028.\"CLIFax\",\n"
                    + "	ret028.clicpf,\n"
                    + "	ret028.clirg,\n"
                    + "	ret028.clicnpj,\n"
                    + "	ret028.cliie,\n"
                    + "	ret028.\"CLIInclusao\",\n"
                    + "	ret028.\"CLICadastro\",\n"
                    + "	ret028.\"CLIEmail\",\n"
                    + "	ret028.\"CLINasc\",\n"
                    + "	ret028.clinumero,\n"
                    + "	ret028.clicomplemento,\n"
                    + "	ret028.\"CLICred\",\n"
                    + "	ret028.\"CLIEstCIV\",\n"
                    + "	ret028.clisexo,\n"
                    + "	ret028.\"CLIPai\",\n"
                    + "	ret028.\"CLIMae\",\n"
                    + "	ret028.clicj,\n"
                    + "	ret028.clicjcpf,\n"
                    + "	ret028.clicjrg,\n"
                    + "	ret028.\"CLICJNasc\",\n"
                    + "	ret028.\"CLIObs\",\n"
                    + "	ret028.\"CLIBco1\",\n"
                    + "	ret028.\"CLIAg1\",\n"
                    + "	ret028.\"CLICta1\",\n"
                    + "	ret028.\"CLILIMCred\",\n"
                    + "	ret028.\"CLICPTrab\",\n"
                    + "	ret028.\"CLITrab\",\n"
                    + "	ret028.\"CLICPRenda\",\n"
                    + "	ret028.\"CLITrabFone\",\n"
                    + "	ret028.cliativo,\n"
                    + "	ret028.clilimcc\n"
                    + "FROM\n"
                    + "	ret028\n"
                    + "LEFT JOIN RET501 ON	RET501.\"CIDCod\" = ret028.\"CIDCod\""
            )) {
                int contador = 1;
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("CLICod"));
                    imp.setRazao(rst.getString("CLINome"));
                    imp.setFantasia(rst.getString("CLIFantasia"));
                    imp.setEndereco(rst.getString("CLIEnd"));
                    imp.setBairro(rst.getString("CLIBairro"));
                    imp.setCep(rst.getString("CLICep"));
                    imp.setMunicipioIBGE(rst.getInt("cidibge"));
                    imp.setMunicipio(rst.getString("CIDNome"));
                    imp.setUf(rst.getString("ciduf"));
                    imp.setNumero(rst.getString("clinumero"));
                    imp.setComplemento(rst.getString("clicomplemento"));
                    imp.setValorLimite(rst.getDouble("CLILIMCred") > 0 ? rst.getDouble("CLILIMCred") : rst.getDouble("clilimcc"));
                    imp.setDataCadastro(rst.getDate("CLICadastro"));
                    imp.setDataNascimento(rst.getDate("CLINasc"));
                    imp.setTelefone(rst.getString("CLIFone1"));
                    imp.setFax(rst.getString("CLIFax"));
                    if ((rst.getString("clicpf") != null)
                            && (!rst.getString("clicpf").trim().isEmpty())) {
                        imp.setCnpj(rst.getString("clicpf"));
                    } else if ((rst.getString("clicnpj") != null)
                            && (!rst.getString("clicnpj").trim().isEmpty())) {
                        imp.setCnpj(rst.getString("clicnpj"));
                    } else {
                        imp.setCnpj("");
                    }
                    if ((rst.getString("clirg") != null)
                            && (!rst.getString("clirg").trim().isEmpty())) {
                        imp.setInscricaoestadual(rst.getString("clirg"));
                    } else if ((rst.getString("cliie") != null)
                            && (!rst.getString("cliie").trim().isEmpty())) {
                        imp.setInscricaoestadual(rst.getString("cliie"));
                    } else {
                        imp.setInscricaoestadual("ISENTO");
                    }
                    if ((rst.getString("CLIEstCIV") != null)
                            && (!rst.getString("CLIEstCIV").trim().isEmpty())) {
                        if (null != rst.getString("CLIEstCIV").trim()) {
                            switch (rst.getString("CLIEstCIV").trim()) {
                                case "O":
                                    imp.setEstadoCivil(TipoEstadoCivil.OUTROS);
                                    break;
                                case "C":
                                    imp.setEstadoCivil(TipoEstadoCivil.CASADO);
                                    break;
                                case "S":
                                    imp.setEstadoCivil(TipoEstadoCivil.SOLTEIRO);
                                    break;
                                case "V":
                                    imp.setEstadoCivil(TipoEstadoCivil.VIUVO);
                                    break;
                                case "D":
                                    imp.setEstadoCivil(TipoEstadoCivil.DIVORCIADO);
                                    break;
                                default:
                                    imp.setEstadoCivil(TipoEstadoCivil.NAO_INFORMADO);
                                    break;
                            }
                        }
                    } else {
                        imp.setEstadoCivil(TipoEstadoCivil.NAO_INFORMADO);
                    }
                    if ((rst.getString("clisexo") != null)
                            && (!rst.getString("clisexo").trim().isEmpty())) {
                        if ("F".equals(rst.getString("clisexo").trim())) {
                            imp.setSexo(TipoSexo.FEMININO);
                        } else {
                            imp.setSexo(TipoSexo.MASCULINO);
                        }
                    } else {
                        imp.setSexo(TipoSexo.MASCULINO);
                    }

                    if ((rst.getString("cliativo") != null)
                            && (!rst.getString("cliativo").trim().isEmpty())) {
                        imp.setAtivo("S".equals(rst.getString("cliativo")) ? true : false);
                    } else {
                        imp.setAtivo(true);
                    }

                    if ((rst.getString("CLICred") != null)
                            && (!rst.getString("CLICred").trim().isEmpty())) {
                        if ("S".equals(rst.getString("CLICred").trim())) {
                            imp.setBloqueado(false);
                            imp.setPermiteCreditoRotativo(true);
                            imp.setPermiteCheque(true);
                        } else {
                            imp.setBloqueado(true);
                            imp.setPermiteCreditoRotativo(false);
                            imp.setPermiteCheque(false);
                        }
                    } else {
                        imp.setBloqueado(true);
                        imp.setPermiteCreditoRotativo(false);
                        imp.setPermiteCheque(false);
                    }
                    imp.setNomePai(rst.getString("CLIPai"));
                    imp.setNomeMae(rst.getString("CLIMae"));
                    imp.setNomeConjuge(rst.getString("clicj"));
                    imp.setObservacao(rst.getString("CLIObs"));
                    imp.setEmpresa(rst.getString("CLITrab"));
                    imp.setEmpresaTelefone(rst.getString("CLITrabFone"));
                    imp.setSalario(rst.getDouble("CLICPRenda"));
                    imp.setEmail(rst.getString("CLIEmail"));
                    if ((rst.getString("CLIFone2") != null)
                            && (!rst.getString("CLIFone2").trim().isEmpty())) {
                        imp.addContato(
                                "1",
                                "TELEFONE 2",
                                rst.getString("CLIFone2"),
                                null,
                                null
                        );
                    }
                    vResult.add(imp);
                    ProgressBar.setStatus("Carregando dados..." + contador);
                    contador++;
                }
                return vResult;
            }
        }
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	ret010.\"CLICod\",\n"
                    + "	ret010.\"CCTCupom\",\n"
                    + "	ret010.cctecf,\n"
                    + "	ret010.\"CCTData\",\n"
                    + "	ret010.cctvcto,\n"
                    + "	ret010.\"CCTDebito\",\n"
                    + "	ret010.cctobs,\n"
                    + "	ret010.\"CCTPgto\",\n"
                    + "	ret010.\"CCTCod\"\n"
                    + "FROM\n"
                    + "	ret010\n"
                    + "WHERE\n"
                    + "	ret010.\"CCTPG\" = 'N'"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rst.getString("CCTCod"));
                    imp.setIdCliente(rst.getString("CLICod"));
                    imp.setNumeroCupom(rst.getString("CCTCupom"));
                    imp.setEcf(rst.getString("cctecf"));
                    imp.setDataEmissao(rst.getDate("CCTData"));
                    imp.setDataVencimento(rst.getDate("cctvcto"));
                    imp.setValor(rst.getDouble("CCTDebito"));
                    imp.setObservacao(rst.getString("cctobs"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<ContaPagarIMP> getContasPagar() throws Exception {
        List<ContaPagarIMP> vResult = new ArrayList<>();
        String observacao;
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "    p.\"FORCod\"||' - '||p.\"PAGDoc\"||' - '||p.\"PAGParc\" || '-'|| p.\"PAGDup\" id,\n"
                    + "    p.\"FORCod\",\n"
                    + "    f.\"FORRazao\", \n"
                    + "    f.forcnpj, \n"
                    + "    p.\"PAGDoc\", \n"
                    + "    p.\"PAGParc\", \n"
                    + "    p.\"PAGPgto\",\n"
                    + "    p.\"PAGDup\", \n"
                    + "    p.pagcmp, \n"
                    + "    p.\"PAGVcto\", \n"
                    + "    p.pagvlr, \n"
                    + "    p.\"PAGJuros\", \n"
                    + "    p.\"PAGDesc\", \n"
                    + "    p.pagobs\n"
                    + "FROM\n"
                    + "    RET091 p\n"
                    + "JOIN ret007 f on f.\"FORCod\" = p.\"FORCod\"\n"
                    + "WHERE\n"
                    + "p.\"PAGPgto\" is NULL AND DATAEXCLUSAO IS NULL  "
            )) {
                while (rst.next()) {
                    observacao = "";

                    if ((rst.getString("PAGDoc") != null)
                            && (!rst.getString("PAGDoc").trim().isEmpty())) {
                        observacao = "DOC. " + rst.getString("PAGDoc").trim() + " ";
                    }
                    if ((rst.getString("PAGDup") != null)
                            && (!rst.getString("PAGDup").trim().isEmpty())) {
                        observacao = observacao + "DUP. " + rst.getString("PAGDup") + " ";
                    }
                    if ((rst.getString("PAGParc") != null)
                            && (!rst.getString("PAGParc").trim().isEmpty())) {
                        observacao = observacao + "PARCELA. " + rst.getString("PAGParc") + " ";
                    }
                    if ((rst.getString("pagobs") != null)
                            && (!rst.getString("pagobs").trim().isEmpty())) {
                        observacao = observacao + "OBS." + rst.getString("pagobs") + " ";
                    }

                    ContaPagarIMP imp = new ContaPagarIMP();
                    imp.setId(rst.getString("id"));
                    imp.setIdFornecedor(rst.getString("FORCod"));
                    imp.setDataEmissao(rst.getDate("pagcmp"));
                    imp.setDataEntrada(rst.getDate("pagcmp"));
                    imp.setNumeroDocumento(rst.getString("PAGDoc"));
                    imp.setValor(rst.getDouble("pagvlr"));
                    imp.setObservacao(observacao);
                    imp.setDataHoraAlteracao(rst.getTimestamp("pagcmp"));
                    imp.addVencimento(rst.getDate("PAGVcto"), imp.getValor());
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<ContaReceberIMP> getContasReceber(Set<OpcaoContaReceber> opt) throws Exception {
        List<ContaReceberIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + "\"CLICod\" ||''||\"RECDoc\" AS id,\n"
                    + "\"CLICod\" AS id_fornecedor,\n"
                    + "RECVDA AS dataemissao,\n"
                    + "\"RECVcto\" AS vencimento,\n"
                    + "RECVLR AS valor,\n"
                    + "RECOBS AS observacao\n"
                    + "FROM RET092 \n"
                    + "WHERE \"RECDup\" = ''"
            )) {
                while (rst.next()) {
                    ContaReceberIMP imp = new ContaReceberIMP();

                    imp.setId(rst.getString("id"));
                    imp.setIdClienteEventual(rst.getString("id_fornecedor"));
                    imp.setDataEmissao(rst.getDate("dataemissao"));
                    imp.setDataVencimento(rst.getDate("vencimento"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setObservacao(rst.getString("observacao"));

                    result.add(imp);
                    System.out.println(imp.getIdFornecedor());
                }
            }
        }

        return result;
    }

    @Override
    public List<ChequeIMP> getCheques() throws Exception {
        List<ChequeIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	ch.\"CHQCod\",\n"
                    + "	ch.\"CHQBco\",\n"
                    + "	ch.\"CHQConta\",\n"
                    + "	ch.\"CHQAge\",\n"
                    + "	ch.\"CHQNum\",\n"
                    + "	ch.\"CHQVcto\",\n"
                    + "	ch.\"CHQLcto\",\n"
                    + "	ch.\"CHQValor\",\n"
                    + "	ch.\"CLICod\",\n"
                    + "	ch.\"CHQTitular\",\n"
                    + "	ch.\"CHQDoc\",\n"
                    + "	ch.\"CHQObs\",\n"
                    + "	cl.clirg,\n"
                    + "	cl.\"CLIFone1\"\n"
                    + "FROM\n"
                    + "	ret033 ch\n"
                    + "LEFT JOIN ret028 cl ON	cl.\"CLICod\" = ch.\"CLICod\"\n"
                    + "WHERE\n"
                    + "	ch.\"CHQBaixa\" IS NULL"
            )) {
                while (rst.next()) {
                    ChequeIMP imp = new ChequeIMP();
                    imp.setId(rst.getString("CHQCod"));
                    imp.setAgencia(rst.getString("CHQAge"));
                    imp.setConta(rst.getString("CHQConta"));
                    imp.setNumeroCheque(rst.getString("CHQNum"));
                    imp.setNumeroCupom("0");
                    imp.setValor(rst.getDouble("CHQValor"));
                    imp.setNome(rst.getString("CHQTitular"));
                    imp.setCpf(rst.getString("CHQDoc"));
                    imp.setRg(rst.getString("clirg"));
                    imp.setTelefone(rst.getString("CLIFone1"));
                    imp.setObservacao(rst.getString("CHQObs"));
                    imp.setDate(rst.getDate("CHQLcto"));
                    imp.setDataDeposito(rst.getDate("CHQVcto"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    public void importarOfertas(int idLojaVR, int idLojaCliente, String impLoja) throws Exception {
        ProgressBar.setStatus("Carregando dados das ofertas");
        List<OfertaVO> ofertas = carregarOfertas(idLojaVR, idLojaCliente);
        new OfertaDAO().salvar(ofertas, idLojaVR, impLoja);
    }

    public List<OfertaVO> carregarOfertas(int idLojaVR, int idLojaCliente) throws Exception {
        List<OfertaVO> ofertas = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	ret051.\"PRODCod\",\n"
                    + "	ret051.\"PRODVendaPR\",\n"
                    + "	ret051.\"PRODPromoIN\",\n"
                    + "	ret051.\"PRODPromoFM\"\n"
                    + "FROM\n"
                    + "	ret051\n"
                    + "WHERE\n"
                    + "	ret051.\"PRODPromoFM\" >= current_date"
            )) {
                while (rst.next()) {
                    OfertaVO vo = new OfertaVO();
                    vo.setId_loja(idLojaVR);
                    vo.setId_produto(rst.getInt("PRODCod"));
                    vo.setDatainicio(rst.getDate("PRODPromoIN"));
                    vo.setDatatermino(rst.getDate("PRODPromoFM"));
                    vo.setPrecooferta(rst.getDouble("PRODVendaPR"));
                    ofertas.add(vo);
                }
            }
        }
        return ofertas;
    }
}
