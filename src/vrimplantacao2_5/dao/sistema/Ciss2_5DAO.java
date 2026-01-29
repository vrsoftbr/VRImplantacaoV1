package vrimplantacao2_5.dao.sistema;

import java.util.Map;
import java.util.Set;
import java.util.Date;
import java.util.List;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.sql.SQLException;
import java.util.logging.Level;
import java.text.ParseException;
import vrimplantacao.utils.Utils;
import java.text.SimpleDateFormat;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoEmpresa;
import vrimplantacao2.vo.enums.TipoReceita;
import vrimplantacao2.vo.enums.TipoInscricao;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VerbaIMP;
import vrimplantacao2.vo.enums.TipoFornecedor;
import vrimplantacao2.vo.importacao.ChequeIMP;
import vrimplantacao2.vo.importacao.OfertaIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.ReceitaIMP;
import vrimplantacao2_5.dao.conexao.ConexaoDB2;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.importacao.AssociadoIMP;
import vrimplantacao2.vo.importacao.DevolucaoIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;
import vrimplantacao2.vo.importacao.ConveniadoIMP;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.ContaReceberIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.ConvenioEmpresaIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.vo.importacao.ConvenioTransacaoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.dao.cadastro.produto2.associado.OpcaoAssociado;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.cadastro.financeiro.contareceber.OpcaoContaReceber;

import static vr.core.utils.StringUtils.LOG;
import static vrimplantacao2.dao.interfaces.IntelliconDAO.FORMAT;

/**
 *
 * @author Wesley
 */
public class Ciss2_5DAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "Sistema-Ciss";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.DATA_CADASTRO,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.ATIVO,
                OpcaoProduto.DESCONTINUADO,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.ATUALIZAR_SOMAR_ESTOQUE,
                OpcaoProduto.CUSTO,
                OpcaoProduto.CUSTO_COM_IMPOSTO,
                OpcaoProduto.CUSTO_SEM_IMPOSTO,
                OpcaoProduto.MARGEM,
                OpcaoProduto.PRECO,
                OpcaoProduto.FABRICANTE,
                OpcaoProduto.FAMILIA,
                OpcaoProduto.FAMILIA_PRODUTO,
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.ICMS,
                OpcaoProduto.ICMS_ENTRADA,
                OpcaoProduto.ICMS_SAIDA,
                OpcaoProduto.ICMS_CONSUMIDOR,
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                OpcaoProduto.NCM,
                OpcaoProduto.CEST,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.NATUREZA_RECEITA,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.PESO_BRUTO,
                OpcaoProduto.PESO_LIQUIDO,
                OpcaoProduto.QTD_EMBALAGEM_COTACAO,
                OpcaoProduto.QTD_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.TROCA,
                OpcaoProduto.VALIDADE,
                OpcaoProduto.VENDA_PDV, // Libera produto para Venda no PDV
                OpcaoProduto.VOLUME_QTD,
                OpcaoProduto.ASSOCIADO,
                OpcaoProduto.RECEITA,
                OpcaoProduto.PDV_VENDA // Habilita importacão de Vendas
        ));
    }

    @Override
    public Set<OpcaoFornecedor> getOpcoesDisponiveisFornecedor() {
        return new HashSet<>(Arrays.asList(
                OpcaoFornecedor.DADOS,
                OpcaoFornecedor.CONTATOS,
                OpcaoFornecedor.ENDERECO,
                OpcaoFornecedor.PAGAR_FORNECEDOR,
                OpcaoFornecedor.PRODUTO_FORNECEDOR,
                OpcaoFornecedor.SITUACAO_CADASTRO,
                OpcaoFornecedor.TIPO_EMPRESA
        ));
    }

    @Override
    public Set<OpcaoCliente> getOpcoesDisponiveisCliente() {
        return new HashSet<>(Arrays.asList(
                OpcaoCliente.DADOS,
                OpcaoCliente.RECEBER_CREDITOROTATIVO,
                OpcaoCliente.OUTRAS_RECEITAS,
                OpcaoCliente.CONVENIO_CONVENIADO,
                OpcaoCliente.CONVENIO_EMPRESA,
                OpcaoCliente.CONVENIO_TRANSACAO,
                OpcaoCliente.CONTATOS,
                OpcaoCliente.CLIENTE_EVENTUAL,
                OpcaoCliente.DATA_CADASTRO,
                OpcaoCliente.DATA_NASCIMENTO,
                OpcaoCliente.ENDERECO,
                OpcaoCliente.UF,
                OpcaoCliente.MUNICIPIO,
                OpcaoCliente.VALOR_LIMITE,
                OpcaoCliente.VENCIMENTO_ROTATIVO,
                OpcaoCliente.RECEBER_CHEQUE
        ));
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoDB2.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + "DISTINCT \n"
                    + "	IDSITTRIBSAI || '-' || VARCHAR_FORMAT(PERICMSAI) || '-' || VARCHAR_FORMAT(PERREDTRIBSAI) as id,\n"
                    + "	CASE\n"
                    + "		WHEN IDSITTRIBSAI = 0 THEN 'CST 00 ' || VARCHAR_FORMAT(PERICMSAI) || '%'\n"
                    + "		WHEN IDSITTRIBSAI = 10 THEN 'CST 10 ' || VARCHAR_FORMAT(PERICMSAI) || '%'\n"
                    + "    WHEN IDSITTRIBSAI = 20 AND PERICMSAI <> 0 THEN\n"
                    + "        'CST 20 ' ||\n"
                    + "        VARCHAR_FORMAT(PERICMSAI) || '%' ||\n"
                    + "        CASE\n"
                    + "            WHEN PERREDTRIBSAI <> 0 THEN\n"
                    + "                ' RDZ ' || RTRIM(CHAR(DECIMAL(PERREDTRIBSAI,5,3))) || '%'\n"
                    + "            ELSE\n"
                    + "                ''\n"
                    + "        END\n"
                    + "  		WHEN IDSITTRIBSAI = 40 THEN 'ISENTO'\n"
                    + "  		WHEN IDSITTRIBSAI = 41 THEN 'NÃO TRIBUTADO'\n"
                    + "			WHEN IDSITTRIBSAI = 60 THEN 'SUBSTITUIDO'\n"
                    + "			WHEN IDSITTRIBSAI = 90 THEN 'CST 90 ' || \n"
                    + "        CASE\n"
                    + "            WHEN PERICMSAI <> 0 THEN\n"
                    + "                RTRIM(CHAR(DECIMAL(PERICMSAI,5,2))) || '%'\n"
                    + "            ELSE\n"
                    + "                ''\n"
                    + "        END\n"
                    + "  		WHEN IDSITTRIBSAI = 500 THEN 'CST 00 ' || VARCHAR_FORMAT(PERICMSAI) || '%'\n"
                    + "	END AS descricao,\n"
                    + "	IDSITTRIBSAI cst,\n"
                    + "	PERICMSAI aliq,\n"
                    + "	PERREDTRIBSAI red\n"
                    + "FROM DBA.PRODUTO_TRIBUTACAO_VW\n"
                    + "ORDER BY IDSITTRIBSAI, PERICMSAI, PERREDTRIBSAI"
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(
                            rst.getString("id"),
                            rst.getString("descricao"),
                            rst.getInt("cst"),
                            rst.getDouble("aliq"),
                            rst.getDouble("red"))
                    );
                }
            }
        }
        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoDB2.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    " SELECT DISTINCT \n"
                    + " 	 d.IDDIVISAO id_merc1,\n"
                    + "    d.DESCRDIVISAO merc1,\n"
                    + "    s.idsecao id_merc2,\n"
                    + "    s.descrsecao merc2,\n"
                    + "    g.idgrupo id_merc3,\n"
                    + "    g.descrgrupo merc3,\n"
                    + "    sub.idsubgrupo id_merc4,\n"
                    + "    sub.descrsubgrupo merc4\n"
                    + "FROM\n"
                    + "	DBA.PRODUTO p\n"
                    + "		LEFT JOIN DBA.DIVISAO d ON d.IDDIVISAO = p.IDDIVISAO \n"
                    + "    left join DBA.SECAO as s on s.idsecao = p.idsecao\n"
                    + "    left join DBA.GRUPO as g on g.idgrupo = p.idgrupo\n"
                    + "    left join DBA.SUBGRUPO as sub on sub.idsubgrupo = p.idsubgrupo\n"
                    + "    WHERE d.IDDIVISAO IS NOT NULL \n"
                    + "ORDER BY id_merc1, id_merc2, id_merc3, id_merc4"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());

                    imp.setMerc1ID(rst.getString("id_merc1"));
                    imp.setMerc1Descricao(rst.getString("merc1"));
                    imp.setMerc2ID(rst.getString("id_merc2"));
                    imp.setMerc2Descricao(rst.getString("merc2"));
                    imp.setMerc3ID(rst.getString("id_merc3"));
                    imp.setMerc3Descricao(rst.getString("merc3"));
                    imp.setMerc4ID(rst.getString("id_merc4"));
                    imp.setMerc4Descricao(rst.getString("merc4"));
//                    imp.setMerc5ID(rst.getString(""));
//                    imp.setMerc5Descricao(rst.getString(""));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoDB2.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT DISTINCT \n"
                    + "	pg.IDCADEIAPRECO id_familia, \n"
                    + "	pcp.DESCRCADEIAPRECO descricao \n"
                    + "FROM DBA.PRODUTO p \n"
                    + "JOIN DBA.PRODUTO_GRADE pg ON p.IDPRODUTO = pg.IDPRODUTO AND pg.IDPRODUTO = pg.IDSUBPRODUTO \n"
                    + "JOIN DBA.PRODUTO_CADEIA_PRECO pcp ON pcp.IDCADEIAPRECO = pg.IDCADEIAPRECO "
            )) {
                while (rst.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("id_familia"));
                    imp.setDescricao(rst.getString("descricao"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoDB2.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT DISTINCT \n"
                    + "	pg.IDSUBPRODUTO id,    \n"
                    + "	pg.IDCODBARPROD ean, \n"
                    + "	CASE \n"
                    + "		WHEN pg.EMBALAGEMPRODUCAO IS NULL AND pg.VALMULTIVENDAS = 1 THEN 'UN' \n"
                    + "	 	WHEN pg.EMBALAGEMPRODUCAO IS NULL AND pg.VALMULTIVENDAS > 1 THEN 'CX'\n"
                    + "	 	ELSE pg.EMBALAGEMPRODUCAO END tipo_embalagem,\n"
                    + "--	pg.VALMULTIVENDAS qtd_embalagem, \n"
                    + "	1 qtd_embalagem \n"
                    + "FROM    \n"
                    + "	DBA.PRODUTO_GRADE pg  \n"
                    + "UNION \n"
                    + "SELECT    \n"
                    + "	cx.IDSUBPRODUTO id,    \n"
                    + "	cx.IDCODBARCX ean,\n"
                    + "	cx.EMBALAGEMSAIDA tipo_embalagem,\n"
                    + "--	cx.QTDMULTIPLA qtd_embalagem, \n"
                    + "	1 qtd_embalagem \n"
                    + "FROM \n"
                    + "DBA.PRODUTO_GRADE_CODBARCX cx "
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("id"));
                    imp.setEan(rst.getString("ean"));
                    imp.setQtdEmbalagem(rst.getInt("qtd_embalagem"));
                    imp.setTipoEmbalagem(rst.getString("tipo_embalagem"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoDB2.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + "	p.IDPRODUTO ,\n"
                    + "    	pg.IDSUBPRODUTO id, \n"
                    + "    	pg.IDSUBPRODUTO , \n"
                    + "    	p.descrcomproduto || coalesce(' ' || pg.subdescricao, '') descricaocompleta, \n"
                    + "    	pg.descrresproduto descricaoreduzida, \n"
                    + "    	p.descrcomproduto || coalesce(' ' || pg.subdescricao, '') descricaogondola, \n"
                    + "    	pg.DTCADASTRO datacadastro,  \n"
                    + "    	pg.DTALTERACAO dataalteracao, \n"
                    + "    	pg.idcodbarprod ean, \n"
                    + "    	1 qtd,\n"
                    + "    	p.embalagemsaida tipoembalagem, \n"
                    + "    	p.VALGRAMAENTRADA AS qtdembalagemcontacao, \n"
                    + "    	p.EMBALAGEMENTRADA AS tipoembalagemcotacao, \n"
                    + "    	CASE p.flagexpbalanca WHEN 'F' THEN 0 ELSE 1 END ebalanca, \n"
                    + "    	CASE WHEN pg.FLAGINATIVOCOMPRA = 'T' THEN 1 ELSE 0 END descontinuado,  \n"
                    + "			CASE WHEN pg.TIPOITEM = '04' THEN 1 ELSE 0 END fabricacao_propria, \n"
                    + "    	p.diasvalidade validade, \n"
                    + "    	p.IDDIVISAO AS mercadologico1, \n"
                    + "    	p.idsecao as mercadologico2,    \n"
                    + "    	p.idgrupo as mercadologico3, \n"
                    + "    	p.idsubgrupo as mercadologico4, \n"
                    + "    	pg.IDCADEIAPRECO as id_familiaproduto, \n"
                    + "    	CASE pg.FLAGINATIVO WHEN 'F' THEN 1 ELSE 0 END AS situacaocadastro, \n"
                    + "    	pg.pesobruto,  \n"
                    + "    	pg.pesoliquido, \n"
                    + "    	pc.qtdestminimo estoqueminimo,    \n"
                    + "    	pc.qtdestmaximo estoquemaximo, \n"
                    + "    	est.qtdatualestoque estoque, \n"
                    + "    	pg.ncm, \n"
                    + "    	pg.codcest cest, \n"
                    + "    	preco.permargemvarejo margem, \n"
                    + "    	preco.valprecovarejo * pg.VALMULTIVENDAS preco, \n"
                    + "    	custo.CUSTOGERENCIAL custocomimposto,  \n"
                    + "    	custo.CUSTOULTIMACOMPRA custosemimposto, \n"
                    + "    	coalesce(g.IDCSTPISCOFINSSAIDA, p.idcstpiscofinssaida) piscofinssaida, \n"
                    + "    	coalesce(g.IDCSTPISCOFINSENTRADA, p.IDCSTPISCOFINSENTRADA)piscofinsentrada, \n"
                    + "    	nat.idcodnatureza as tipoNaturezaReceita, \n"
                    + "    	trib.idsittribsai as icmsCstDebito,   \n"
                    + "    	trib.idsittribent as icmsCstCredito,     \n"
                    + "    	trib.pericment as icmsAliqCredito,     \n"
                    + "    	trib.pericmsai as icmsAliqDebito, \n"
                    + "    	trib.perredtribsai as icmsPercReducaoSaida,  \n"
                    + "    	trib.perredtribent as icmsPercReducaoEntrada, \n"
                    + "    	trib.permargemsubsti as percSubst \n"
                    + "FROM DBA.PRODUTO_GRADE pg \n"
                    + "JOIN DBA.PRODUTO p ON p.IDPRODUTO = pg.IDPRODUTO \n"
                    + "JOIN DBA.EMPRESA e ON e.IDEMPRESA = " + getLojaOrigem() + " \n"
                    + "LEFT JOIN DBA.PRODUTO_COMPRAS pc ON \n"
                    + "pg.IDSUBPRODUTO = pc.IDSUBPRODUTO AND \n"
                    + "pc.IDEMPRESA = e.IDEMPRESA \n"
                    + "LEFT JOIN DBA.ESTOQUE_SALDO_ATUAL est ON \n"
                    + "est.IDPRODUTO = pg.IDPRODUTO AND \n"
                    + "est.IDSUBPRODUTO = pg.IDSUBPRODUTO AND \n"
                    + "est.IDEMPRESA = e.IDEMPRESA AND \n"
                    + "est.IDLOCALESTOQUE = e.IDLOCALESTVENDAPADRAO \n"
                    + "LEFT JOIN DBA.POLITICA_PRECO_PRODUTO preco ON \n"
                    + "preco.IDSUBPRODUTO = pg.IDSUBPRODUTO  AND \n"
                    + "preco.IDEMPRESA = e.IDEMPRESA \n"
                    + "LEFT JOIN DBA.PRODUTO_GRADE_CUSTO_VIEW custo ON \n"
                    + "custo.IDSUBPRODUTO = pg.IDSUBPRODUTO AND \n"
                    + "custo.IDEMPRESA = e.IDEMPRESA \n"
                    + "LEFT JOIN dba.GRUPO g ON p.IDGRUPO = g.IDGRUPO \n"
                    + "LEFT JOIN DBA.PRODUTO_TRIBUTACAO_VW trib ON \n"
                    + "trib.IDSUBPRODUTO = pg.IDSUBPRODUTO AND \n"
                    + "trib.UF = e.UF \n"
                    + "LEFT JOIN DBA.PISCOFINS_CODIGO_NATUREZA_RECEITA nat ON \n"
                    + "nat.IDNATUREZAPISCOFINS = p.IDNATUREZAPISCOFINS "
            )) {
                Map<Integer, vrimplantacao2.vo.cadastro.ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().getProdutosBalanca();
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("id"));
                    imp.setEan(rst.getString("ean"));

                    ProdutoBalancaVO bal = produtosBalanca.get(Utils.stringToInt(rst.getString("ean"), -2));

                    if (bal != null) {
                        imp.seteBalanca(true);
                        imp.setTipoEmbalagem("P".equals(bal.getPesavel()) ? "KG" : "UN");
                        imp.setEan(String.valueOf(bal.getCodigo()));
                    }

                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(rst.getString("descricaogondola"));
                    imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
                    imp.setQtdEmbalagem(rst.getInt("qtd"));
                    imp.seteBalanca(rst.getBoolean("ebalanca"));
                    imp.setValidade(rst.getInt("validade"));

                    imp.setDescontinuado(rst.getBoolean("descontinuado"));
                    imp.setFabricacaoPropria(rst.getBoolean("fabricacao_propria"));

                    imp.setQtdEmbalagemCotacao(rst.getInt("qtdembalagemcontacao"));
                    imp.setTipoEmbalagemCotacao(rst.getString("tipoembalagemcotacao"));

                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    imp.setPrecovenda(rst.getDouble("preco"));
                    imp.setMargem(rst.getDouble("margem"));

                    imp.setCodMercadologico1(rst.getString("mercadologico1"));
                    imp.setCodMercadologico2(rst.getString("mercadologico2"));
                    imp.setCodMercadologico3(rst.getString("mercadologico3"));
                    imp.setCodMercadologico4(rst.getString("mercadologico4"));

                    imp.setSituacaoCadastro(rst.getInt("situacaocadastro"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setDataAlteracao(rst.getDate("dataalteracao"));
                    imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
                    imp.setEstoqueMaximo(rst.getDouble("estoquemaximo"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setPesoBruto(rst.getDouble("pesobruto"));
                    imp.setPesoLiquido(rst.getDouble("pesoliquido"));

                    imp.setIdFamiliaProduto(rst.getString("id_familiaproduto"));

                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));

                    //ENTRADA
                    imp.setIcmsCstEntrada(rst.getInt("icmsCstCredito"));
                    imp.setIcmsAliqEntrada(rst.getDouble("icmsAliqCredito"));
                    imp.setIcmsReducaoEntrada(rst.getDouble("icmsPercReducaoEntrada"));

                    imp.setIcmsCstEntradaForaEstado(rst.getInt("icmsCstCredito"));
                    imp.setIcmsAliqEntradaForaEstado(rst.getDouble("icmsAliqCredito"));
                    imp.setIcmsReducaoEntradaForaEstado(rst.getDouble("icmsPercReducaoEntrada"));

                    //SAIDA
                    imp.setIcmsCstSaida(rst.getInt("icmsCstDebito"));
                    imp.setIcmsAliqSaida(rst.getDouble("icmsAliqDebito"));
                    imp.setIcmsReducaoSaida(rst.getDouble("icmsPercReducaoSaida"));

                    imp.setIcmsCstSaidaForaEstado(rst.getInt("icmsCstDebito"));
                    imp.setIcmsAliqSaidaForaEstado(rst.getDouble("icmsAliqDebito"));
                    imp.setIcmsReducaoSaidaForaEstado(rst.getDouble("icmsPercReducaoSaida"));

                    imp.setIcmsCstSaidaForaEstadoNF(rst.getInt("icmsCstDebito"));
                    imp.setIcmsAliqSaidaForaEstadoNF(rst.getDouble("icmsAliqDebito"));
                    imp.setIcmsReducaoSaidaForaEstadoNF(rst.getDouble("icmsPercReducaoSaida"));

                    imp.setIcmsCstConsumidor(rst.getInt("icmsCstDebito"));
                    imp.setIcmsAliqConsumidor(rst.getDouble("icmsAliqDebito"));
                    imp.setIcmsReducaoConsumidor(rst.getDouble("icmsPercReducaoSaida"));

                    imp.setPiscofinsCstDebito(rst.getString("piscofinssaida"));
                    imp.setPiscofinsCstCredito(rst.getString("piscofinsentrada"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("tipoNaturezaReceita"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<OfertaIMP> getOfertas(Date dataTermino) throws Exception {
        List<OfertaIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoDB2.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    ""
            )) {
                while (rst.next()) {
                    OfertaIMP imp = new OfertaIMP();

                    imp.setIdProduto(rst.getString(""));
                    imp.setDataInicio(rst.getDate(""));
                    imp.setDataFim(rst.getDate(""));
                    imp.setPrecoNormal(rst.getDouble(""));
                    imp.setPrecoOferta(rst.getDouble(""));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<AssociadoIMP> getAssociados(Set<OpcaoAssociado> opt) throws Exception {
        List<AssociadoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoDB2.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + "pg.IDPRODUTO || '-' ||pg.IDSUBPRODUTO id_associado, \n"
                    + "pg.IDSUBPRODUTO id_produto, \n"
                    + "pg.DESCRRESPRODUTO descricao_associado, \n"
                    + "pg.VALMULTIVENDAS qtdembalagem, \n"
                    + "pg.IDSUBPRODUTO || '-' ||pg.IDPRODUTO id_associado_item, \n"
                    + "pg.IDPRODUTO id_produto_item, \n"
                    + "p.DESCRCOMPRODUTO descricao_associado_item, \n"
                    + "1 qtdembalagem_item, \n"
                    + "FALSE aplica_custo, \n"
                    + "TRUE aplica_estoque \n"
                    + "FROM DBA.PRODUTO_GRADE pg \n"
                    + "JOIN DBA.PRODUTO p ON p.IDPRODUTO = pg.IDPRODUTO \n"
                    + "WHERE pg.IDPRODUTO <> pg.IDSUBPRODUTO \n"
                    + "UNION \n"
                    + "SELECT \n"
                    + "CAST(pg.IDPRODUTO AS VARCHAR(20)) id_associado, \n"
                    + "pg.IDPRODUTO id_produto, \n"
                    + "p.DESCRCOMPRODUTO descricao_associado, \n"
                    + "1 qtdembalagem, \n"
                    + "CAST(pg.IDSUBPRODUTO AS VARCHAR(20)) id_associado_item, \n"
                    + "pg.IDSUBPRODUTO id_produto_item, \n"
                    + "pg.DESCRRESPRODUTO descricao_associado_item, \n"
                    + "pg.VALMULTIVENDAS qtdembalagem_item, \n"
                    + "TRUE aplica_custo, \n"
                    + "FALSE aplica_estoque \n"
                    + "FROM DBA.PRODUTO_GRADE pg \n"
                    + "JOIN DBA.PRODUTO p ON p.IDPRODUTO = pg.IDPRODUTO \n"
                    + "WHERE pg.IDPRODUTO <> pg.IDSUBPRODUTO "
            )) {
                while (rst.next()) {
                    AssociadoIMP imp = new AssociadoIMP();

                    imp.setImpIdAssociado(rst.getString("id_associado"));
                    imp.setImpIdProduto(rst.getString("id_produto"));
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                    imp.setImpIdAssociadoItem(rst.getString("id_associado_item"));
                    imp.setImpIdProdutoItem(rst.getString("id_produto_item"));
                    imp.setQtdEmbalagemItem(rst.getInt("qtdembalagem_item"));
                    imp.setAplicaCusto(rst.getBoolean("aplica_custo"));
                    imp.setAplicaEstoque(rst.getBoolean("aplica_estoque"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ReceitaIMP> getReceitas() throws Exception {
        List<ReceitaIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoDB2.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "WITH ultima_formula AS (\n"
                    + "  SELECT\n"
                    + "    IDFORMULA,\n"
                    + "    IDPRODUTOACABADO,\n"
                    + "    QTDRENDIMENTOPREVISTO,\n"
                    + "    DTALTERACAO,\n"
                    + "    ROW_NUMBER() OVER (\n"
                    + "      PARTITION BY IDPRODUTOACABADO\n"
                    + "      ORDER BY DTALTERACAO DESC, IDFORMULA DESC\n"
                    + "    ) AS rn\n"
                    + "  FROM dba.PRODUCAO_FORMULA\n"
                    + ")\n"
                    + "SELECT DISTINCT\n"
                    + "  fc.IDFORMULA          AS id_receita,\n"
                    + "  pga.IDPRODUTO         AS id_produto_acabado,\n"
                    + "  pga.DESCRCOMPRODUTO   AS descricao_produto_acabado,\n"
                    + "  pf.QTDRENDIMENTOPREVISTO AS rendimento,\n"
                    + "  fc.QTDCOMPONENTE * 1000    AS quantidade_componente,\n"
                    + "  pg.IDPRODUTO          AS id_produto_componente,\n"
                    + "  pg.DESCRCOMPRODUTO    AS descricao_produto_componente\n"
                    + "FROM dba.PRODUCAO_FORMULA_COMPONENTE fc\n"
                    + "JOIN ultima_formula uf\n"
                    + "  ON fc.IDFORMULA = uf.IDFORMULA\n"
                    + "  AND uf.rn = 1\n"
                    + "JOIN dba.PRODUCAO_FORMULA pf\n"
                    + "  ON pf.IDFORMULA = uf.IDFORMULA\n"
                    + "JOIN dba.PRODUTO pga\n"
                    + "  ON pga.IDPRODUTO = pf.IDPRODUTOACABADO\n"
                    + "JOIN dba.PRODUCAO_COMPONENTE pc\n"
                    + "  ON fc.IDCOMPONENTE = pc.IDCOMPONENTE\n"
                    + "JOIN dba.PRODUTO pg\n"
                    + "  ON pg.IDPRODUTO = pc.IDPRODUTOBAIXAEST"
            )) {
                while (rst.next()) {
                    ReceitaIMP imp = new ReceitaIMP();
                    imp.setImportsistema(getSistema());
                    imp.setImportloja(getLojaOrigem());

                    imp.setImportid(rst.getString("id_receita"));
                    imp.setIdproduto(rst.getString("id_produto_acabado"));
                    imp.setDescricao(rst.getString("descricao_produto_acabado"));
                    imp.setRendimento(rst.getDouble("rendimento"));
                    imp.setQtdembalagemreceita(rst.getInt("quantidade_componente"));
                    imp.setQtdembalagemproduto(1000);
                    imp.setFator(1);
                    imp.setFichatecnica("");
                    imp.getProdutos().add(rst.getString("id_produto_componente"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoDB2.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "	WITH contatos AS (\n"
                    + "    SELECT \n"
                    + "        cc.IDCLIFOR,\n"
                    + "        cc.IDCONTATO,\n"
                    + "        cc.NOMECONTATO,\n"
                    + "        cc.FONE,\n"
                    + "        cc.EMAIL,\n"
                    + "        ccc.IDCARGOCOMPLEMENTAR,\n"
                    + "        ROW_NUMBER() OVER (\n"
                    + "            PARTITION BY cc.IDCLIFOR \n"
                    + "            ORDER BY cc.IDCONTATO\n"
                    + "        ) AS rn\n"
                    + "    FROM DBA.CLIENTE_FORNECEDOR_CONTATO cc\n"
                    + "    LEFT JOIN DBA.CARGO_COMPLEMENTAR ccc ON ccc.IDCARGOCOMPLEMENTAR = cc.IDCARGOCOMPLEMENTAR\n"
                    + "    WHERE cc.NOMECONTATO IS NOT NULL\n"
                    + "      AND cc.NOMECONTATO <> '' \n"
                    + ")\n"
                    + "SELECT \n"
                    + "    f.idclifor id,\n"
                    + "    f.NOME razao,\n"
                    + "    f.NOMEFANTASIA fantasia,\n"
                    + "    f.CNPJCPF cnpj,\n"
                    + "    f.INSCRESTADUAL inscricaoestadual,\n"
                    + "    f.FONE1 fone,\n"
                    + "    f.ENDERECO endereco,\n"
                    + "    f.BAIRRO bairro,\n"
                    + "    f.NUMERO numero,\n"
                    + "    f.COMPLEMENTO complento,\n"
                    + "    f.IDCEP cep,\n"
                    + "    f.OBSGERAL observacao,\n"
                    + "    c.codigoibge id_municipio,\n"
                    + "    c.descrcidade,\n"
                    + "    e.CODIGOESTADO id_estado,\n"
                    + "    e.UF uf,\n"
                    + "    f.dtcadastro datacadastro,\n"
                    + "    CASE f.idsituacao WHEN 4 THEN 0 ELSE 1 END id_situacaoCadastro,\n"
                    + "    f.FONE2,\n"
                    + "    f.FONEFAX fax,\n"
                    + "    f.EMAIL,\n"
                    + "    f.tipofisicajuridica, \n"
                    + "    CASE \n"
                    + "        WHEN r.TIPOREGIMETRIBFEDERAL IN ('I', 'R') THEN 3\n"
                    + "        WHEN r.TIPOREGIMETRIBFEDERAL = 'P' THEN 1\n"
                    + "        WHEN r.TIPOREGIMETRIBFEDERAL = 'S' THEN 8\n"
                    + "        ELSE 20\n"
                    + "    END tipo_empresa ,\n"
                    + "    CASE\n"
                    + "        WHEN a.IDATIVIDADE = 11 THEN 0\n"
                    + "        WHEN a.IDATIVIDADE = 2 THEN 1\n"
                    + "        WHEN a.IDATIVIDADE = 7 THEN 2\n"
                    + "        WHEN a.IDATIVIDADE = 15 THEN 3\n"
                    + "        WHEN a.IDATIVIDADE = 16 THEN 4\n"
                    + "        WHEN a.IDATIVIDADE = 17 THEN 5\n"
                    + "        ELSE 0 \n"
                    + "    END tipo_fornecedor,\n"
                    + "    p.DIASPRAZO cond_pag,\n"
                    + "    c1.IDCONTATO AS id_contato1,\n"
                    + "    c1.NOMECONTATO AS nome_contato1,\n"
                    + "    c1.FONE AS tel_contato1,\n"
                    + "    LOWER(c1.EMAIL) AS email_contato1,\n"
                    + "    c1.IDCARGOCOMPLEMENTAR AS cargo1,\n"
                    + "    c2.IDCONTATO AS id_contato2,\n"
                    + "    c2.NOMECONTATO AS nome_contato2,\n"
                    + "    c2.FONE AS tel_contato2,\n"
                    + "    LOWER(c2.EMAIL) AS email_contato2,\n"
                    + "    c2.IDCARGOCOMPLEMENTAR AS cargo2,\n"
                    + "    c3.IDCONTATO AS id_contato3,\n"
                    + "    c3.NOMECONTATO AS nome_contato3,\n"
                    + "    c3.FONE AS tel_contato3,\n"
                    + "    LOWER(c3.EMAIL) AS email_contato3,\n"
                    + "    c3.IDCARGOCOMPLEMENTAR AS cargo3,\n"
                    + "    c4.IDCONTATO AS id_contato4,\n"
                    + "    c4.NOMECONTATO AS nome_contato4,\n"
                    + "    c4.FONE AS tel_contato4,\n"
                    + "    LOWER(c4.EMAIL) AS email_contato4,\n"
                    + "    c4.IDCARGOCOMPLEMENTAR AS cargo4,\n"
                    + "    c5.IDCONTATO AS id_contato5,\n"
                    + "    c5.NOMECONTATO AS nome_contato5,\n"
                    + "    c5.FONE AS tel_contato5,\n"
                    + "    LOWER(c5.EMAIL) AS email_contato5,\n"
                    + "    c5.IDCARGOCOMPLEMENTAR AS cargo5,\n"
                    + "    c6.IDCONTATO AS id_contato6,\n"
                    + "    c6.NOMECONTATO AS nome_contato6,\n"
                    + "    c6.FONE AS tel_contato6,\n"
                    + "    LOWER(c6.EMAIL) AS email_contato6,\n"
                    + "    c6.IDCARGOCOMPLEMENTAR AS cargo6,\n"
                    + "    c7.IDCONTATO AS id_contato7,\n"
                    + "    c7.NOMECONTATO AS nome_contato7,\n"
                    + "    c7.FONE AS tel_contato7,\n"
                    + "    LOWER(c7.EMAIL) AS email_contato7,\n"
                    + "    c7.IDCARGOCOMPLEMENTAR AS cargo7,\n"
                    + "    c8.IDCONTATO AS id_contato8,\n"
                    + "    c8.NOMECONTATO AS nome_contato8,\n"
                    + "    c8.FONE AS tel_contato8,\n"
                    + "    LOWER(c8.EMAIL) AS email_contato8,\n"
                    + "    c8.IDCARGOCOMPLEMENTAR AS cargo8,\n"
                    + "    c9.IDCONTATO AS id_contato9,\n"
                    + "    c9.NOMECONTATO AS nome_contato9,\n"
                    + "    c9.FONE AS tel_contato9,\n"
                    + "    LOWER(c9.EMAIL) AS email_contato9,\n"
                    + "    c9.IDCARGOCOMPLEMENTAR AS cargo9,\n"
                    + "    c10.IDCONTATO AS id_contato10,\n"
                    + "    c10.NOMECONTATO AS nome_contato10,\n"
                    + "    c10.FONE AS tel_contato10,\n"
                    + "    LOWER(c10.EMAIL) AS email_contato10,\n"
                    + "    c10.IDCARGOCOMPLEMENTAR AS cargo10 \n"
                    + "FROM DBA.CLIENTE_FORNECEDOR f\n"
                    + "LEFT JOIN DBA.CIDADES_IBGE c ON f.idcidade = c.idcidade\n"
                    + "LEFT JOIN DBA.ESTADO e ON c.UF = e.uf\n"
                    + "LEFT JOIN DBA.ATIVIDADE a ON f.IDATIVIDADE = a.IDATIVIDADE \n"
                    + "LEFT JOIN DBA.REGIME_TRIBUTARIO_FEDERAL r ON f.TIPOREGIMETRIBFEDERAL = r.TIPOREGIMETRIBFEDERAL \n"
                    + "LEFT JOIN DBA.CLIENTE_FORNECEDOR_PRAZO p  ON p.IDCLIFOR = f.IDCLIFOR AND p.NUMPARCELA = 1\n"
                    + "LEFT JOIN contatos c1 ON c1.IDCLIFOR = f.IDCLIFOR AND c1.rn = 1 AND c1.NOMECONTATO <> ''	AND c1.FONE IS NOT NULL \n"
                    + "LEFT JOIN contatos c2 ON c2.IDCLIFOR = f.IDCLIFOR AND c2.rn = 2 AND c2.NOMECONTATO <> ''	AND c2.FONE IS NOT NULL \n"
                    + "LEFT JOIN contatos c3 ON c3.IDCLIFOR = f.IDCLIFOR AND c3.rn = 3 AND c3.NOMECONTATO <> ''	AND c3.FONE IS NOT NULL \n"
                    + "LEFT JOIN contatos c4 ON c4.IDCLIFOR = f.IDCLIFOR AND c4.rn = 4 AND c4.NOMECONTATO <> ''	AND c4.FONE IS NOT NULL \n"
                    + "LEFT JOIN contatos c5 ON c5.IDCLIFOR = f.IDCLIFOR AND c5.rn = 5 AND c5.NOMECONTATO <> ''	AND c5.FONE IS NOT NULL \n"
                    + "LEFT JOIN contatos c6 ON c6.IDCLIFOR = f.IDCLIFOR AND c6.rn = 6 AND c6.NOMECONTATO <> ''	AND c6.FONE IS NOT NULL \n"
                    + "LEFT JOIN contatos c7 ON c7.IDCLIFOR = f.IDCLIFOR AND c7.rn = 7 AND c7.NOMECONTATO <> ''	AND c7.FONE IS NOT NULL \n"
                    + "LEFT JOIN contatos c8 ON c8.IDCLIFOR = f.IDCLIFOR AND c8.rn = 8 AND c8.NOMECONTATO <> ''	AND c8.FONE IS NOT NULL \n"
                    + "LEFT JOIN contatos c9 ON c9.IDCLIFOR = f.IDCLIFOR AND c9.rn = 9 AND c9.NOMECONTATO <> ''	AND c9.FONE IS NOT NULL \n"
                    + "LEFT JOIN contatos c10 ON c10.IDCLIFOR = f.IDCLIFOR AND c10.rn = 10 AND c10.NOMECONTATO <> ''	AND c10.FONE IS NOT NULL \n"
                    + "WHERE (f.TIPOCADASTRO IN ('A','F') OR f.IDCLIFOR IN (10006, 1000036, 1000037, 10007))\n"
                    + "ORDER BY f.IDCLIFOR "
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
                    imp.setBairro(rst.getString("bairro"));
                    imp.setIbge_municipio(rst.getInt("id_municipio"));
                    imp.setComplemento("complento");
                    imp.setUf(rst.getString("id_estado"));
                    imp.setCep(rst.getString("cep"));

                    imp.setAtivo(rst.getBoolean("id_situacaoCadastro"));
                    imp.setObservacao(rst.getString("observacao"));
                    imp.setDatacadastro(rst.getDate("datacadastro"));
                    imp.setTel_principal(rst.getString("fone"));

                    imp.setValor_minimo_pedido(0);
                    imp.setCondicaoPagamento(rst.getObject("cond_pag") == null ? 0 : rst.getInt("cond_pag"));

                    imp.setTipoEmpresa(TipoEmpresa.getById(rst.getInt("tipo_empresa")));
                    imp.setTipoFornecedor(TipoFornecedor.getById(rst.getInt("tipo_fornecedor")));

                    for (int i = 1; i <= 10; i++) {
                        if (rst.getString("id_contato" + i) != null) {
                            int cargo = rst.getInt("cargo" + i);

                            TipoContato tipo = cargo == 1 ? TipoContato.COMERCIAL
                                    : cargo == 2 ? TipoContato.FINANCEIRO
                                            : cargo == 3 ? TipoContato.FISCAL
                                                    : cargo == 4 ? TipoContato.NFE
                                                            : TipoContato.COMERCIAL;

                            imp.addContato(
                                    rst.getString("id_contato" + i),
                                    rst.getString("nome_contato" + i),
                                    rst.getString("tel_contato" + i),
                                    "",
                                    tipo,
                                    rst.getString("email_contato" + i) == null ? "" : rst.getString("email_contato" + i)
                            );
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
        try (Statement stm = ConexaoDB2.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	pf.idclifor id_fornecedor,\n"
                    + "	pf.idsubproduto id_produto,\n"
                    + "	pf.CODIGOINTERNOFORN codigoexterno,"
                    + "   pf.VALGRAMAENTRADA qntd_embalagem\n"
                    + "from\n"
                    + "	dba.PRODUTO_FORNECEDOR pf\n"
                    + "order by\n"
                    + "	id_fornecedor,\n"
                    + "	id_produto"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setIdFornecedor(rst.getString("id_fornecedor"));
                    imp.setIdProduto(rst.getString("id_produto"));
                    imp.setCodigoExterno(rst.getString("codigoexterno"));
                    imp.setQtdEmbalagem(rst.getDouble("qntd_embalagem"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ContaPagarIMP> getContasPagar() throws Exception {
        List<ContaPagarIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoDB2.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "        trim(cp.IDEMPRESA||'-'||cp.IDCLIFOR||'-'||cp.DIGITOTITULO||'-'||cp.SERIENOTA||'-'||cp.IDTITULO) id,\n"
                    + "        cp.idclifor idfornecedor,\n"
                    + "        cp.idtitulo doc,\n"
                    + "        cp.dtmovimento dataemissao,\n"
                    + "        cp.valtitulo valor,\n"
                    + "        cp.dtvencimento vencimento,\n"
                    + "        ' - ' || cp.obstitulo observacao,\n"
                    + "        CASE WHEN cp.SERIENOTA IN ('AVU', 'F') THEN 210 ELSE 0 END tipo_entrada\n"
                    + "from\n"
                    + "        dba.contas_pagar cp\n"
                    + "where\n"
                    + "        cp.idempresa = " + getLojaOrigem() + "\n"
                    + "        and cp.flagbaixada = 'F'\n"
                    + "order by\n"
                    + "        cp.dtmovimento"
            )) {
                while (rst.next()) {
                    ContaPagarIMP imp = new ContaPagarIMP();

                    imp.setId(rst.getString("id"));
                    imp.setIdFornecedor(rst.getString("idfornecedor"));
                    imp.setNumeroDocumento(rst.getString("doc"));
                    imp.setDataEmissao(rst.getDate("dataemissao"));
                    imp.setDataEntrada(imp.getDataEmissao());
                    imp.setIdTipoEntradaVR(rst.getInt("tipo_entrada"));
                    imp.addVencimento(rst.getDate("vencimento"), rst.getDouble("valor"), rst.getString("observacao"));
                    imp.setObservacao(rst.getString("observacao"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ContaReceberIMP> getContasReceber(Set<OpcaoContaReceber> opt) throws Exception {
        List<ContaReceberIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoDB2.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	DISTINCT \n"
                    + "  ROW_NUMBER() OVER(\n"
                    + "	ORDER BY\n"
                    + "		cr.IDPLANILHA\n"
                    + "	) || '-' || cr.IDPLANILHA AS id_conta,\n"
                    + "	a.IDCLIFOR AS fornecedor,\n"
                    + "	cr.IDTITULO ,\n"
                    + "	cf.NOMEFANTASIA ,\n"
                    + "	cr.IDTITULO AS nota,\n"
                    + "	cr.DTMOVIMENTO AS dataemissao,\n"
                    + "	cr.DTVENCIMENTO AS datavencimento,\n"
                    + "	cr.VALTITULO AS valor,\n"
                    + "	ab.DESCRINSTITUICAO tipo,\n"
                    + "	CASE\n"
                    + " WHEN REPLACE(REPLACE(REPLACE(UPPER(ab.DESCRINSTITUICAO), '.', '_'),'É','E'),'ÇÃ', 'CA') = 'ALELO ALIMENTACAO' THEN 101\n"
                    + " WHEN REPLACE(REPLACE(REPLACE(UPPER(ab.DESCRINSTITUICAO), '.', '_'),'É','E'),'ÇÃ', 'CA') = 'ALELO BENEFICIO' THEN 102\n"
                    + " WHEN REPLACE(REPLACE(REPLACE(UPPER(ab.DESCRINSTITUICAO), '.', '_'),'É','E'),'ÇÃ', 'CA') = 'AMEX' THEN 103\n"
                    + " WHEN REPLACE(REPLACE(REPLACE(UPPER(ab.DESCRINSTITUICAO), '.', '_'),'É','E'),'ÇÃ', 'CA') = 'BIQ VOUCHER' THEN 104\n"
                    + " WHEN REPLACE(REPLACE(REPLACE(UPPER(ab.DESCRINSTITUICAO), '.', '_'),'É','E'),'ÇÃ', 'CA') = 'ELO CREDITO' THEN 105\n"
                    + " WHEN REPLACE(REPLACE(REPLACE(UPPER(ab.DESCRINSTITUICAO), '.', '_'),'É','E'),'ÇÃ', 'CA') = 'ELO DEBITO' THEN 107\n"
                    + " WHEN REPLACE(REPLACE(REPLACE(UPPER(ab.DESCRINSTITUICAO), '.', '_'),'É','E'),'ÇÃ', 'CA') = 'GOODCARD CREDITO' THEN 108\n"
                    + " WHEN REPLACE(REPLACE(REPLACE(UPPER(ab.DESCRINSTITUICAO), '.', '_'),'É','E'),'ÇÃ', 'CA') = 'GREENCARD' THEN 109\n"
                    + " WHEN REPLACE(REPLACE(REPLACE(UPPER(ab.DESCRINSTITUICAO), '.', '_'),'É','E'),'ÇÃ', 'CA') = 'IFOOD' THEN 110\n"
                    + " WHEN REPLACE(REPLACE(REPLACE(UPPER(ab.DESCRINSTITUICAO), '.', '_'),'É','E'),'ÇÃ', 'CA') = 'MAESTRO' THEN 111\n"
                    + " WHEN REPLACE(REPLACE(REPLACE(UPPER(ab.DESCRINSTITUICAO), '.', '_'),'É','E'),'ÇÃ', 'CA') = 'MASTERCARD' THEN 112\n"
                    + " WHEN REPLACE(REPLACE(REPLACE(UPPER(ab.DESCRINSTITUICAO), '.', '_'),'É','E'),'ÇÃ', 'CA') = 'MASTERCARD DEBIT' THEN 113\n"
                    + " WHEN REPLACE(REPLACE(REPLACE(UPPER(ab.DESCRINSTITUICAO), '.', '_'),'É','E'),'ÇÃ', 'CA') = 'MEGAVALECARD' THEN 114\n"
                    + " WHEN REPLACE(REPLACE(REPLACE(UPPER(ab.DESCRINSTITUICAO), '.', '_'),'É','E'),'ÇÃ', 'CA') = 'PICPAY CD' THEN 115\n"
                    + " WHEN REPLACE(REPLACE(REPLACE(UPPER(ab.DESCRINSTITUICAO), '.', '_'),'É','E'),'ÇÃ', 'CA') = 'PIX CIELO' THEN 116\n"
                    + " WHEN REPLACE(REPLACE(REPLACE(UPPER(ab.DESCRINSTITUICAO), '.', '_'),'É','E'),'ÇÃ', 'CA') = 'POLICARD VOCUHER' THEN 117\n"
                    + " WHEN REPLACE(REPLACE(REPLACE(UPPER(ab.DESCRINSTITUICAO), '.', '_'),'É','E'),'ÇÃ', 'CA') = 'ROMCARD CREDITO' THEN 118\n"
                    + " WHEN REPLACE(REPLACE(REPLACE(UPPER(ab.DESCRINSTITUICAO), '.', '_'),'É','E'),'ÇÃ', 'CA') = 'SODEXO ALIMENTACAO' THEN 119\n"
                    + " WHEN REPLACE(REPLACE(REPLACE(UPPER(ab.DESCRINSTITUICAO), '.', '_'),'É','E'),'ÇÃ', 'CA') = 'SODEXO GIFT' THEN 120\n"
                    + " WHEN REPLACE(REPLACE(REPLACE(UPPER(ab.DESCRINSTITUICAO), '.', '_'),'É','E'),'ÇÃ', 'CA') = 'SODEXO REFEICAO' THEN 121\n"
                    + " WHEN REPLACE(REPLACE(REPLACE(UPPER(ab.DESCRINSTITUICAO), '.', '_'),'É','E'),'ÇÃ', 'CA') = 'SODEXO VOUCHER' THEN 122\n"
                    + " WHEN REPLACE(REPLACE(REPLACE(UPPER(ab.DESCRINSTITUICAO), '.', '_'),'É','E'),'ÇÃ', 'CA') = 'TICKET ALIMENTACAO' THEN 123\n"
                    + " WHEN REPLACE(REPLACE(REPLACE(UPPER(ab.DESCRINSTITUICAO), '.', '_'),'É','E'),'ÇÃ', 'CA') = 'TICKET FLEX' THEN 124\n"
                    + " WHEN REPLACE(REPLACE(REPLACE(UPPER(ab.DESCRINSTITUICAO), '.', '_'),'É','E'),'ÇÃ', 'CA') = 'TICKET RESTAURANTE' THEN 125\n"
                    + " WHEN REPLACE(REPLACE(REPLACE(UPPER(ab.DESCRINSTITUICAO), '.', '_'),'É','E'),'ÇÃ', 'CA') = 'TRICARDMAIS' THEN 126\n"
                    + " WHEN REPLACE(REPLACE(REPLACE(UPPER(ab.DESCRINSTITUICAO), '.', '_'),'É','E'),'ÇÃ', 'CA') = 'VEGASCARD ALIMENTACAO' THEN 127\n"
                    + " WHEN REPLACE(REPLACE(REPLACE(UPPER(ab.DESCRINSTITUICAO), '.', '_'),'É','E'),'ÇÃ', 'CA') = 'VEGASCARD CREDITO' THEN 128\n"
                    + " WHEN REPLACE(REPLACE(REPLACE(UPPER(ab.DESCRINSTITUICAO), '.', '_'),'É','E'),'ÇÃ', 'CA') = 'VEROCARD' THEN 129\n"
                    + " WHEN REPLACE(REPLACE(REPLACE(UPPER(ab.DESCRINSTITUICAO), '.', '_'),'É','E'),'ÇÃ', 'CA') = 'VISA CREDITO' THEN 130\n"
                    + " WHEN REPLACE(REPLACE(REPLACE(UPPER(ab.DESCRINSTITUICAO), '.', '_'),'É','E'),'ÇÃ', 'CA') = 'VISA ELECTRON' THEN 131\n"
                    + " WHEN REPLACE(REPLACE(REPLACE(UPPER(ab.DESCRINSTITUICAO), '.', '_'),'É','E'),'ÇÃ', 'CA') = 'VR ALIMENTACAO' THEN 132\n"
                    + " WHEN REPLACE(REPLACE(REPLACE(UPPER(ab.DESCRINSTITUICAO), '.', '_'),'É','E'),'ÇÃ', 'CA') = 'VR REFEICAO' THEN 133\n"
                    + " WHEN REPLACE(REPLACE(REPLACE(UPPER(ab.DESCRINSTITUICAO), '.', '_'),'É','E'),'ÇÃ', 'CA') = 'LECARD' THEN 134\n"
                    + " WHEN REPLACE(REPLACE(REPLACE(UPPER(ab.DESCRINSTITUICAO), '.', '_'),'É','E'),'ÇÃ', 'CA') = 'SERVICARD' THEN 135\n"
                    + " WHEN REPLACE(REPLACE(REPLACE(UPPER(ab.DESCRINSTITUICAO), '.', '_'),'É','E'),'ÇÃ', 'CA') = 'VALE CARD' THEN 136\n"
                    + " WHEN REPLACE(REPLACE(REPLACE(UPPER(ab.DESCRINSTITUICAO), '.', '_'),'É','E'),'ÇÃ', 'CA') = 'VALECARD' THEN 137\n"
                    + "	ELSE 99\n"
                    + "	END tipo_id,\n"
                    + "	' - ' || OBSTITULO AS obs\n"
                    + "FROM\n"
                    + "	DBA.CONTAS_RECEBER cr\n"
                    + "JOIN DBA.ADMINISTRADORAS a ON\n"
                    + "	a.IDADMINISTRADORA = cr.IDADMINISTRADORA\n"
                    + "JOIN DBA.ADMINISTRADORAS_BANDEIRA ab ON\n"
                    + "	ab.IDADMINISTRADORA = cr.IDADMINISTRADORA\n"
                    + "	AND ab.IDBANDEIRA = cr.IDBANDEIRA\n"
                    + "	AND ab.IDEMPRESA = cr.IDEMPRESA\n"
                    + "LEFT JOIN DBA.CLIENTE_FORNECEDOR cf ON\n"
                    + "	cf.IDCLIFOR = a.IDCLIFOR\n"
                    + "WHERE cr.IDEMPRESA = " + getLojaOrigem() + "\n"
                    + "	AND cr.flagbaixada = 'F'\n"
                    + "	AND cr.IDCTACONTABIL = '1120102'\n"
                    + "	AND DTMOVIMENTO >= '01/12/2025'\n"
                    + "	AND CR.ORIGEMMOVIMENTO <> 'FRE'"
                    + " AND CR.IDCLIFOR <> 1000000000000007"
            )) {
                while (rst.next()) {
                    ContaReceberIMP imp = new ContaReceberIMP();

                    imp.setId(rst.getString("id_conta"));
                    imp.setIdFornecedor(rst.getString("fornecedor"));
                    imp.setDataEmissao(rst.getDate("dataemissao"));
                    imp.setDataVencimento(rst.getDate("datavencimento"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setTipoReceita(TipoReceita.getById(rst.getInt("tipo_id")));
                    imp.setObservacao(rst.getString("obs"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoDB2.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "        c.idclifor id, \n"
                    + "        c.NOME razao, \n"
                    + "        c.NOMEFANTASIA fantasia, \n"
                    + "        c.CNPJCPF cnpj, \n"
                    + "        c.INSCRESTADUAL inscricaoestadual, \n"
                    + "        c.FONE1 fone, \n"
                    + "        c.FONECELULAR celular, \n"
                    + "        c.EMAIL email, \n"
                    + "        c.FONEFAX fax, \n"
                    + "        c.ENDERECO endereco, \n"
                    + "        c.BAIRRO bairro, \n"
                    + "        c.NUMERO numero, \n"
                    + "        c.COMPLEMENTO complemento, \n"
                    + "        c.IDCEP cep, \n"
                    + "        c.OBSGERAL observacao, \n"
                    + "        cid.codigoibge id_municipio, \n"
                    + "        e.CODIGOESTADO id_estado, \n"
                    + "        cid.descrcidade, \n"
                    + "        cid.uf id_estado, \n"
                    + "        c.dtcadastro datacadastro, \n"
                    + "        c.VALLIMITECREDITO valor_limit, \n"
                    + "        coalesce(conv.diavencimento,0) diavencimento, \n"
                    + "        c.tipofisicajuridica tipo_pessoa, \n"
                    + "        CASE c.FLAGINATIVO WHEN 'T' THEN 0 ELSE 1 END id_situacaoCadastro, \n"
                    + "        c.FONE2, \n"
                    + "        c.FONEFAX fax, \n"
                    + "        c.EMAIL, \n"
                    + "        cc.IDCONTATO id_contato, \n"
                    + "        cc.NOMECONTATO nome_contato, \n"
                    + "        cc.FONE tel_contato, \n"
                    + "        cc.EMAIL email_contato \n"
                    + "FROM \n"
                    + "        DBA.CLIENTE_FORNECEDOR c \n"
                    + "        LEFT JOIN DBA.CIDADES_IBGE cid ON cid.IDCIDADE = c.IDCIDADE \n"
                    + "        LEFT JOIN DBA.ESTADO e ON cid.UF = e.UF \n"
                    + "        LEFT JOIN DBA.CLIENTE_CONVENIO conv ON c.IDCONVENIO = conv.IDCONVENIO \n"
                    + "        LEFT JOIN DBA.CLIENTE_FORNECEDOR_CONTATO cc ON cc.IDCLIFOR = c.IDCLIFOR \n"
                    + "        AND cc.FONE IS NOT NULL \n"
                    + "        AND cc.NOMECONTATO != ''"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setInscricaoestadual(rst.getString("inscricaoestadual"));

                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipioIBGE(rst.getInt("id_municipio"));
                    imp.setUfIBGE(rst.getInt("id_estado"));
                    imp.setCep(rst.getString("cep"));

                    imp.setTelefone(rst.getString("fone"));
                    imp.setCelular(rst.getString("celular"));
                    imp.setEmail(rst.getString("email"));
                    imp.setFax(rst.getString("fax"));

                    imp.setTipoInscricao(
                            "J".equals(rst.getString("tipo_pessoa"))
                            ? TipoInscricao.JURIDICA
                            : TipoInscricao.FISICA
                    );

                    imp.setValorLimite(rst.getDouble("valor_limit"));
                    imp.setDiaVencimento(rst.getInt("diavencimento"));

                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setAtivo(rst.getBoolean("id_situacaoCadastro"));
                    imp.setObservacao(rst.getString("observacao"));

//                    imp.addContato(
//                            rst.getString("id_contato1"),
//                            rst.getString("nome_contato"),
//                            rst.getString("tel_contato"),
//                            null,
//                            rst.getString("email_contato")
//                    );
                    result.add(imp);
                }
            }
        }
        return result;
    }

    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoDB2.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "    cr.idplanilha AS id,\n"
                    + "    cr.idtitulo cupom,\n"
                    + "    cr.dtmovimento AS dataemissao,\n"
                    + "    cr.numcupomfiscal AS ccf,\n"
                    + "    cr.idcaixa AS ecf,\n"
                    + "    cr.idclifor AS idcliente,\n"
                    + "    cf.cnpjcpf AS cnpj,\n"
                    + "    cr.valtitulo AS valor,\n"
                    + "    cr.dtvencimento AS vencimento,\n"
                    + "    cr.obstitulo AS observacao,\n"
                    + "    cr.sumvaljuroscobrado AS juros\n"
                    + "FROM\n"
                    + "    DBA.CONTAS_RECEBER cr\n"
                    + "JOIN DBA.CLIENTE_FORNECEDOR cf ON cr.IDCLIFOR = cf.IDCLIFOR\n"
                    + "WHERE\n"
                    + "    cr.IDEMPRESA = " + getLojaOrigem() + " AND \n"
                    + "    cr.FLAGBAIXADA = 'F' \n"
                    + "    AND cr.IDRECEBIMENTO = 2 \n"
                    + "    AND cr.IDPLANILHA > 0 \n"
                    + "ORDER BY\n"
                    + "    cr.dtmovimento"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();

                    imp.setId(rst.getString("id"));
                    imp.setNumeroCupom(Utils.formataNumero(rst.getString("ccf")));
                    imp.setIdCliente(rst.getString("idcliente"));
                    imp.setCnpjCliente(rst.getString("cnpj"));
                    imp.setEcf(rst.getString("ecf"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setDataEmissao(rst.getDate("dataemissao"));
                    imp.setDataVencimento(rst.getDate("vencimento"));
                    imp.setObservacao(rst.getString("observacao"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ChequeIMP> getCheques() throws Exception {
        List<ChequeIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoDB2.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "        ch.idplanilha as id,\n"
                    + "        cf.nome,\n"
                    + "        ch.cnpjcpfdono as cpf,\n"
                    + "        cf.inscrestadual as rg,\n"
                    + "        ch.idnumcheque as idcheque,\n"
                    + "        ch.idclifor as idcliente,\n"
                    + "        ch.idbanco,\n"
                    + "        ch.idagencia,\n"
                    + "        ch.numconta as numeroconta,\n"
                    + "        ch.valor,\n"
                    + "        ch.dtvencimento,\n"
                    + "        cf.FONE1 fone \n"
                    + "from\n"
                    + "        dba.cheques ch\n"
                    + "join\n"
                    + "        dba.cliente_fornecedor cf on ch.idclifor = cf.idclifor\n"
                    + "where\n"
                    + "        ch.idempresa = " + getLojaOrigem() + " and\n"
                    + "        not upper(cf.nome) like '%CONSUMIDOR%FINAL%' and\n"
                    + "        ch.codcompensacao = 18\n"
                    + "order by\n"
                    + "        ch.dtvencimento"
            )) {
                while (rst.next()) {
                    ChequeIMP imp = new ChequeIMP();

                    imp.setId(rst.getString("id"));
                    imp.setDataDeposito(rst.getDate("dtvencimento"));
                    imp.setNumeroCheque(rst.getString("idcheque"));
                    imp.setDate(rst.getDate("dtvencimento"));
                    imp.setBanco(rst.getInt("idbanco"));
                    imp.setAgencia(rst.getString("idagencia"));
                    imp.setConta(rst.getString("numeroconta"));
                    imp.setNome(rst.getString("nome"));
                    imp.setTelefone(rst.getString("fone"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setNumeroCupom(rst.getString("idcheque"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<VerbaIMP> getVerbas() throws Exception {
        List<VerbaIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoDB2.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + "	DIGITOTITULO || '-' || IDPLANILHA AS id, \n"
                    + "	IDCLIFOR AS fornecedor, \n"
                    + "	IDTITULO AS titulo, \n"
                    + "	DTMOVIMENTO AS dataemissao, \n"
                    + "	DTVENCIMENTO AS datavencimento, \n"
                    + "	VALTITULO - SUMVALPAGAMENTOTITULO AS valor, \n"
                    + "	'REFERENTE AO TITULO - '  || DIGITOTITULO || '-' ||IDTITULO || ' - OBS: ' || OBSTITULO AS obs,"
                    + " 15 mercadologico \n"
                    + "FROM DBA.CONTAS_RECEBER cr \n"
                    + "WHERE cr.IDCTACONTABIL = 1120111 \n"
                    + "        AND cr.idempresa = " + getLojaOrigem() + " \n"
                    + "        AND cr.flagbaixada = 'F' "
            )) {
                while (rst.next()) {
                    VerbaIMP imp = new VerbaIMP();

                    imp.setId(rst.getString("id"));
                    imp.setIdFornecedor(rst.getString("fornecedor"));
                    imp.setDocumento(rst.getString("titulo"));
                    imp.setDataEmissao(rst.getDate("dataemissao"));
                    imp.setDataVencimento(rst.getDate("datavencimento"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setObservacao(rst.getString("obs"));
                    imp.setMercadologico1(rst.getInt("mercadologico"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<DevolucaoIMP> getDevolucao() throws Exception {
        List<DevolucaoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoDB2.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + "	DIGITOTITULO || '-' || IDPLANILHA AS id, \n"
                    + "	IDCLIFOR AS fornecedor,\n"
                    + "	IDTITULO AS nota,\n"
                    + "	DTMOVIMENTO AS dataemissao,\n"
                    + "	DTVENCIMENTO AS datavencimento,\n"
                    + "	VALTITULO AS valor,\n"
                    + "	OBSTITULO AS obs \n"
                    + "FROM DBA.CONTAS_RECEBER cr \n"
                    + "WHERE cr.IDCTACONTABIL = 1120107\n"
                    + "        AND cr.idempresa = " + getLojaOrigem() + " \n"
                    + "        AND cr.flagbaixada = 'F'"
            )) {
                while (rst.next()) {
                    DevolucaoIMP imp = new DevolucaoIMP();

                    imp.setId(rst.getString("id"));
                    imp.setIdFornecedor(rst.getString("fornecedor"));
                    imp.setNumeroNota(rst.getInt("nota"));
                    imp.setDataEmissao(rst.getDate("dataemissao"));
                    imp.setDataVencimento(rst.getDate("datavencimento"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setObservacao(rst.getString("obs"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ConvenioEmpresaIMP> getConvenioEmpresa() throws Exception {
        List<ConvenioEmpresaIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoDB2.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + "	IDCONVENIO id_empresa_conv, \n"
                    + "	IDCONVENIO cnpj, \n"
                    + "	'ISENTO' inscricao_estadual, \n"
                    + "	DESCRCONVENIO razao \n"
                    + "FROM DBA.CLIENTE_CONVENIO"
            )) {
                while (rst.next()) {
                    ConvenioEmpresaIMP imp = new ConvenioEmpresaIMP();

                    imp.setId(rst.getString("id_empresa_conv"));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setInscricaoEstadual(rst.getString("inscricao_estadual"));
                    imp.setRazao(rst.getString("razao"));
//                    imp.setEndereco(rst.getString(""));
//                    imp.setNumero(rst.getString(""));
//                    imp.setBairro(rst.getString(""));
//                    imp.setMunicipio(rst.getString(""));
//                    imp.setUf(rst.getString(""));
//                    imp.setCep(rst.getString(""));
//                    imp.setTelefone(rst.getString(""));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ConveniadoIMP> getConveniado() throws Exception {
        List<ConveniadoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoDB2.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + " c.IDCLIFOR id_conveniado, \n"
                    + " c.NOME nome, \n"
                    + " c.IDCONVENIO id_empresa, \n"
                    + " c.CNPJCPF cnpj, \n"
                    + " c.VALLIMITECONVENIO limite, \n"
                    + " CASE WHEN c.FLAGINATIVO = 'F' THEN 1 ELSE 0 END ativo, \n"
                    + " CAST(c.OBSGERAL AS VARCHAR(5000)) AS obs \n"
                    + "FROM DBA.CLIENTE_FORNECEDOR c \n"
                    + "LEFT JOIN DBA.cidades_ibge cid ON cid.idcidade = c.idcidade \n"
                    + "LEFT JOIN DBA.ESTADO e ON cid.UF = e.uf \n"
                    + "JOIN DBA.CLIENTE_CONVENIO conv ON c.IDCONVENIO = conv.IDCONVENIO	\n"
                    + "UNION \n"
                    + "SELECT \n"
                    + " c.IDCLIFOR id_conveniado, \n"
                    + " c.NOME nome, \n"
                    + " 1 id_empresa, \n"
                    + " c.CNPJCPF cnpj, \n"
                    + " c.VALLIMITECONVENIO limite, \n"
                    + " CASE WHEN c.FLAGINATIVO = 'F' THEN 1 ELSE 0 END ativo, \n"
                    + "CAST(c.OBSGERAL AS VARCHAR(5000)) AS obs \n"
                    + "FROM DBA.CLIENTE_FORNECEDOR c \n"
                    + "LEFT JOIN DBA.cidades_ibge cid ON cid.idcidade = c.idcidade \n"
                    + "LEFT JOIN DBA.ESTADO e ON cid.UF = e.uf \n"
                    + "LEFT JOIN DBA.CLIENTE_CONVENIO conv ON c.IDCONVENIO = conv.IDCONVENIO	\n"
                    + "WHERE c.IDCLIFOR = 201024"
            )) {
                while (rst.next()) {
                    ConveniadoIMP imp = new ConveniadoIMP();

                    imp.setId(rst.getString("id_conveniado"));
                    imp.setNome(rst.getString("nome"));
                    imp.setIdEmpresa(rst.getString("id_empresa"));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setConvenioLimite(rst.getDouble("limite"));
                    imp.setLojaCadastro(Integer.parseInt(getLojaOrigem()));
                    imp.setSituacaoCadastro(rst.getInt("ativo") == 1 ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    imp.setObservacao(rst.getString("obs"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ConvenioTransacaoIMP> getConvenioTransacao() throws Exception {
        List<ConvenioTransacaoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoDB2.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	cr.IDPLANILHA id_transacao, \n"
                    + " 	c.IDCLIFOR id_conveniado, \n"
                    + " 	cr.IDTITULO cupom, \n"
                    + " 	cr.VALTITULO valor, \n"
                    + " 	cr.DTMOVIMENTO data_movimento, \n"
                    + " 	cr.OBSTITULO obs \n"
                    + "FROM DBA.CONTAS_RECEBER cr \n"
                    + "JOIN DBA.CLIENTE_FORNECEDOR c ON c.IDCLIFOR = cr.IDCLIFOR \n"
                    + "JOIN DBA.CLIENTE_CONVENIO conv ON cr.IDCONVENIO = conv.IDCONVENIO \n"
                    + "WHERE cr.FLAGBAIXADA = 'F' \n"
                    + "AND cr.IDEMPRESA = " + getLojaOrigem()
            )) {
                while (rst.next()) {
                    ConvenioTransacaoIMP imp = new ConvenioTransacaoIMP();

                    imp.setId(rst.getString("id_transacao"));
                    imp.setIdConveniado(rst.getString("id_conveniado"));
                    imp.setNumeroCupom(rst.getString("cupom"));
                    imp.setDataHora(rst.getTimestamp("data_movimento"));
                    imp.setValor(rst.getDouble("valor"));
//                    imp.setObservacao(rst.getString("obs"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    private Date dataInicioVenda;
    private Date dataTerminoVenda;

    @Override
    public Iterator<VendaIMP> getVendaIterator() throws Exception {
        return new VendaIterator(getLojaOrigem(), this.dataInicioVenda, this.dataTerminoVenda);
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        return new VendaItemIterator(getLojaOrigem(), this.dataInicioVenda, this.dataTerminoVenda);
    }

    public void setDataInicioVenda(Date dataInicioVenda) {
        this.dataInicioVenda = dataInicioVenda;
    }

    public void setDataTerminoVenda(Date dataTerminoVenda) {
        this.dataTerminoVenda = dataTerminoVenda;

    }

    private static class VendaIterator implements Iterator<VendaIMP> {

        public final static SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");

        private Statement stm = ConexaoDB2.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaIMP next;
        private Set<String> uk = new HashSet<>();

        private void obterNext() {
            try {
                SimpleDateFormat timestampDate = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat timestamp = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                SimpleDateFormat formaterHora = new SimpleDateFormat("hh:mm");
                if (next == null) {
                    if (rst.next()) {
                        next = new VendaIMP();
                        String id = rst.getString("id_venda");
                        if (!uk.add(id)) {
                            LOG.warning("Venda " + id + " já existe na listagem");
                        }
                        next.setId(id);
                        next.setNumeroCupom(rst.getInt("numerocupom"));
//                        next.setNumeroCupom(Utils.stringToInt(rst.getString("numerocupom")));
                        next.setEcf(Utils.stringToInt(rst.getString("ecf")));
                        next.setData(rst.getDate("data"));

                        String horaInicio = timestamp.format(rst.getDate("data"));
                        String horaTermino = timestamp.format(rst.getDate("data"));
                        next.setHoraInicio(timestamp.parse(horaInicio));
                        next.setHoraTermino(timestamp.parse(horaTermino));
                        next.setSubTotalImpressora(rst.getDouble("subtotalimpressora"));
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
                    + "	cr.idplanilha || '-' || cr.DIGITOTITULO id_venda,\n"
                    + "	cr.IDTITULO ||  cr.DIGITOTITULO numerocupom,\n"
                    + "	n.idcaixa ecf,\n"
                    + "	n.dtmovimento data,\n"
                    + "	n.IDCLIFOR idcliente,\n"
                    + "	case when n.FLAGNOTACANCEL= 'T' then 1 else 0 end cancelado,\n"
                    + "	sum(cr.valtitulo) subtotalimpressora,\n"
                    + "	v.CNPJCPF cpf,\n"
                    + "	v.NOME nomecliente,\n"
                    + "	v.ENDERECO,\n"
                    + "	v.NUMERo,\n"
                    + "	v.COMPLEMENTO,\n"
                    + "	v.BAIRRO,\n"
                    + "	v.IDCIDADE,\n"
                    + "	v.idcep cep\n"
                    + "FROM \n"
                    + "	dba.CONTAS_RECEBER cr\n"
                    + "	join dba.NOTAS n on\n"
                    + "		cr.IDEMPRESA = n.IDEMPRESA AND\n"
                    + "		cr.IDPLANILHA = n.IDPLANILHA\n"
                    + "	join dba.NOTAS_ENTRADA_SAIDA v on\n"
                    + "		cr.IDEMPRESA = v.IDEMPRESA AND\n"
                    + "		cr.IDPLANILHA = v.IDPLANILHA\n"
                    + "	join dba.FRCAIXA_RECEBIMENTO fr on\n"
                    + "		cr.IDEMPRESA = fr.IDEMPRESA AND\n"
                    + "		cr.IDPLANILHA = fr.IDPLANILHA AND\n"
                    + "		cr.IDRECEBIMENTO = fr.IDRECEBIMENTO	\n"
                    + "WHERE\n"
                    + "	v.IDOPERACAO = 1300 AND\n"
                    + "	cr.IDEMPRESA IN ( " + idLojaCliente + ") AND\n"
                    + "	cr.DTMOVIMENTO BETWEEN '" + strDataInicio + "' AND '" + strDataTermino + "'\n"
                    + "group by\n"
                    + "	cr.idplanilha,\n"
                    + "   cr.DIGITOTITULO, \n"
                    + "	cr.IDTITULO,\n"
                    + "	cr.DIGITOTITULO,\n"
                    + "	n.idcaixa,\n"
                    + "	n.dtmovimento,\n"
                    + "	n.IDCLIFOR,\n"
                    + "	case when n.FLAGNOTACANCEL= 'T' then 1 else 0 end,\n"
                    + "	v.CNPJCPF,\n"
                    + "	v.NOME,\n"
                    + "	v.ENDERECO,\n"
                    + "	v.NUMERo,\n"
                    + "	v.COMPLEMENTO,\n"
                    + "	v.BAIRRO,\n"
                    + "	v.IDCIDADE,\n"
                    + "	v.idcep\n";
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

        private Statement stm = ConexaoDB2.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaItemIMP next;

        private void obterNext() {
            try {
                if (next == null) {
                    if (rst.next()) {
                        next = new VendaItemIMP();

                        next.setVenda(rst.getString("id_venda"));
                        next.setId(rst.getString("id"));
                        next.setSequencia(rst.getInt("sequencia"));
                        next.setProduto(rst.getString("idproduto"));
                        next.setUnidadeMedida(rst.getString("embalagem"));
                        next.setCodigoBarras(rst.getString("codigobarras"));
                        next.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                        next.setQuantidade(rst.getDouble("quantidade"));
//                        next.setPrecoVenda(rst.getDouble(""));
                        next.setTotalBruto(rst.getDouble("totalbruto"));
                        next.setCancelado(rst.getBoolean("cancelado"));
                    }
                }
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaItemIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {
            this.sql
                    = "select\n"
                    + "	cr.idplanilha || '-' || cr.DIGITOTITULO || '-' || e.numsequencia id,\n"
                    + "	e.NUMSEQUENCIA sequencia,\n"
                    + "	cr.idplanilha || '-' || cr.DIGITOTITULO id_venda,\n"
                    + "	e.IDSUBPRODUTO idproduto,\n"
                    + "	e.VALTOTLIQUIDO totalbruto,\n"
                    + "	e.QTDPRODUTO quantidade,\n"
                    + "	(e.VALTOTLIQUIDO / NULLIF(e.QTDPRODUTO, 0)) AS valor_unitario,\n"
                    + "	case when n.FLAGNOTACANCEL= 'T' then 1 else 0 end cancelado,\n"
                    + "	0 desconto,\n"
                    + "	0 acrescimo,\n"
                    + "	ean.descrresproduto descricaoreduzida,\n"
                    + "	ean.CODBAR codigobarras,\n"
                    + "	p.embalagemsaida embalagem,\n"
                    + "	e.IDSITTRIB icms_cst,\n"
                    + "	e.PERICM icms_aliq,\n"
                    + "	e.PERREDTRIB icms_reducao\n"
                    + "from\n"
                    + "	dba.notas n\n"
                    + "	join dba.estoque_analitico e on\n"
                    + "		e.idempresa = n.idempresa and\n"
                    + "		e.idplanilha = n.idplanilha and\n"
                    + "		(e.numsequenciakit is null or e.numsequenciakit <= 0)\n"
                    + "	join dba.produto_grade ean on\n"
                    + "		ean.idsubproduto = e.idsubproduto\n"
                    + "	join dba.produto p on\n"
                    + "		p.idproduto = e.idproduto\n"
                    + "	join dba.CONTAS_RECEBER cr \n"
                    + "	on cr.IDPLANILHA = n.IDPLANILHA AND\n"
                    + "	cr.IDEMPRESA  = n.IDEMPRESA \n"
                    + "where\n"
                    + "	e.idoperacao = 1300 and\n"
                    + "	e.idempresa in (" + idLojaCliente + ") and\n"
                    + "	e.DTMOVIMENTO BETWEEN '" + FORMAT.format(dataInicio) + "' AND '" + FORMAT.format(dataTermino) + "'\n"
                    + "order by 1";
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
