/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2_5.dao.sistema;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import static vr.core.utils.StringUtils.LOG;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto.ProdutoAnteriorDAO;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.vo.enums.TipoEmpresa;
import vrimplantacao2.vo.enums.TipoFornecedor;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.OfertaIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;
import vrimplantacao2_5.dao.conexao.ConexaoFirebird;

/**
 *
 * @author Wagner
 */
public class ManagerDAO extends InterfaceDAO implements MapaTributoProvider {

    private String lojaCliente;
    public boolean gerarCodigoAtacado = true;
    public boolean segundoEan = true;
    public boolean removeDigitoEAN = false;

    public String getLojaCliente() {
        return this.lojaCliente;
    }

    @Override
    public String getSistema() {
        return "Manager";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
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
                OpcaoProduto.ICMS,
                OpcaoProduto.DATA_CADASTRO,
                OpcaoProduto.ATACADO,
                OpcaoProduto.FAMILIA,
                OpcaoProduto.FAMILIA_PRODUTO,
                OpcaoProduto.VR_ATACADO,
                OpcaoProduto.ATACADO,
                OpcaoProduto.PDV_VENDA
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
                OpcaoFornecedor.PAGAR_FORNECEDOR,
                OpcaoFornecedor.TELEFONE
        ));
    }

    @Override
    public Set<OpcaoCliente> getOpcoesDisponiveisCliente() {
        return new HashSet<>(Arrays.asList(
                OpcaoCliente.DADOS,
                OpcaoCliente.ENDERECO,
                OpcaoCliente.CONTATOS,
                OpcaoCliente.DATA_CADASTRO,
                OpcaoCliente.DATA_NASCIMENTO,
                OpcaoCliente.TELEFONE,
                OpcaoCliente.ESTADO_CIVIL,
                OpcaoCliente.EMPRESA,
                OpcaoCliente.SALARIO,
                OpcaoCliente.BLOQUEADO,
                OpcaoCliente.OBSERVACOES2,
                OpcaoCliente.OBSERVACOES,
                OpcaoCliente.NUMERO,
                OpcaoCliente.COMPLEMENTO,
                OpcaoCliente.SITUACAO_CADASTRO,
                OpcaoCliente.RECEBER_CREDITOROTATIVO));
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	DISTINCT \n"
                    + "	CODIGOFISCAL ID,\n"
                    + "	CASE \n"
                    + "		WHEN CODIGOFISCAL = '000' AND TRIBEFETCPF = '12.00'\n"
                    + "		THEN '12'\n"
                    + "		WHEN CODIGOFISCAL = '000' AND TRIBEFETCPF = '17.00'\n"
                    + "		THEN '17'\n"
                    + "		WHEN CODIGOFISCAL = '000' AND TRIBEFETCPF = '18.00'\n"
                    + "		THEN '18'\n"
                    + "		WHEN CODIGOFISCAL = '040' AND TRIBEFETCPF = '17.00' OR CODIGOFISCAL = '300' AND TRIBEFETCPF = '17.00' \n"
                    + "		OR CODIGOFISCAL = '460' AND TRIBEFETCPF = '17.00' OR CODIGOFISCAL = '400' AND TRIBEFETCPF = '17.00'\n"
                    + "		THEN '17'\n"
                    + "		WHEN CODIGOFISCAL = '040' AND TRIBEFETCPF = '18.00'\n"
                    + "		THEN '18'\n"
                    + "		WHEN CODIGOFISCAL = '040' OR CODIGOFISCAL = '400' OR CODIGOFISCAL = '460' OR CODIGOFISCAL = '540' \n"
                    + "		OR CODIGOFISCAL = '000' AND TRIBEFETCPF = '1.00' OR CODIGOFISCAL = '000' AND TRIBEFETCPF = '0.00' OR CODIGOFISCAL = '000'\n"
                    + "		THEN 'ISENTO'\n"
                    + "		WHEN CODIGOFISCAL = '041' OR CODIGOFISCAL = '541' OR CODIGOFISCAL = '641' OR CODIGOFISCAL = '141'\n"
                    + "		THEN 'NAO TRIBUTADO'\n"
                    + "		WHEN CODIGOFISCAL = '051'\n"
                    + "		THEN 'DEFERIDO'\n"
                    + "		WHEN CODIGOFISCAL = '500'\n"
                    + "		THEN 'SUSPENSO'\n"
                    + "		WHEN CODIGOFISCAL = '060' OR CODIGOFISCAL = '240' OR CODIGOFISCAL = '241' OR CODIGOFISCAL = '260' \n"
                    + "			OR CODIGOFISCAL = '560' OR CODIGOFISCAL = '660' OR CODIGOFISCAL = '360' OR CODIGOFISCAL = '600'\n"
                    + "		THEN 'SUBSTITUIDO'\n"
                    + "		WHEN CODIGOFISCAL = '070' OR CODIGOFISCAL = '700'\n"
                    + "		THEN 'SUBSTITUIDO'\n"
                    + "		WHEN CODIGOFISCAL = '090'\n"
                    + "		THEN 'OUTROS'\n"
                    + "		WHEN CODIGOFISCAL = '100' OR CODIGOFISCAL = '160'\n"
                    + "		THEN 'TRIBUTADO SUSBS'\n"
                    + "		WHEN CODIGOFISCAL = '200' OR CODIGOFISCAL = '520' OR CODIGOFISCAL = '020'\n"
                    + "		THEN 'REDUZIDO'\n"
                    + "		WHEN CODIGOFISCAL = '300'\n"
                    + "		THEN 'ISENTO'\n"
                    + "		ELSE ''\n"
                    + "	END DESCRICAO,	\n"
                    + "	CODIGOFISCAL cst,\n"
                    + "	COALESCE(TRIBEFETCPF, NULL, '0.00') ICMSCREDITODEBITO,\n"
                    + "	CASE \n"
                    + "		WHEN CODIGOFISCAL != '000' OR CODIGOFISCAL != '020' OR CODIGOFISCAL != '100' OR CODIGOFISCAL != '200'\n"
                    + "		THEN '0.00'\n"
                    + "		ELSE PERCSUBSTITUICAO\n"
                    + "	END reduzido\n"
                    + "FROM\n"
                    + "	PRODUTOSFILIAIS"
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(rst.getString("id"),
                            rst.getString("descricao"),
                            rst.getInt("cst"),
                            rst.getDouble("ICMSCREDITODEBITO"),
                            rst.getDouble("reduzido")
                    ));
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos(OpcaoProduto opt) throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        System.out.println("To aqui");
        if (opt == OpcaoProduto.ATACADO) {
            try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select \n"
                        + "CODIGO id,\n"
                        + "PRECOVENDA pvenda,\n"
                        + "PRECOATACADO patacado,\n"
                        + "COALESCE(QUANTIDADEMINIMAATACADO, NULL, 0) qtd\n"
                        + "from PRODUTOS p  "
                )) {
                    while (rst.next()) {
                        int codigoAtual = new ProdutoAnteriorDAO().getCodigoAnterior2(getSistema() + " - " + 1, getLojaOrigem(), rst.getString("id"));

                        if (codigoAtual > 0) {

                            ProdutoIMP imp = new ProdutoIMP();
                            imp.setImportLoja(getLojaOrigem());
                            imp.setImportSistema(getSistema());
                            imp.setImportId(rst.getString("id"));
                            imp.setEan("99999" + String.valueOf(codigoAtual));
                            imp.setQtdEmbalagem(rst.getInt("qtd"));
                            imp.setPrecovenda(rst.getDouble("pvenda"));
                            imp.setAtacadoPreco(rst.getDouble("patacado"));
                            result.add(imp);
                        }
                    }
                }
            }
            return result;
        }
        return null;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	g.CODIGO mercid1,\n"
                    + "	g.GRUPO descri1,\n"
                    + "	g.CODIGO mercid2,\n"
                    + "	g.GRUPO descri2,\n"
                    + "	g.CODIGO mercid3,\n"
                    + "	g.GRUPO descri3\n"
                    + "FROM\n"
                    + "	GRUPOS g"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setMerc1ID(rst.getString("mercid1"));
                    imp.setMerc1Descricao(rst.getString("descri1"));
                    imp.setMerc2ID(rst.getString("mercid2"));
                    imp.setMerc2Descricao(rst.getString("descri2"));
                    imp.setMerc3ID(rst.getString("mercid3"));
                    imp.setMerc3Descricao(rst.getString("descri3"));
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
                    "SELECT\n"
                    + " p.CODIGO id,\n"
                    + " p.CODIGOBARRAS ean,\n"
                    + " p.DESCRICAO descricao,\n"
                    + " p.CODIGOGRUPO mercid1,\n"
                    + " p.CODIGOGRUPO mercid2,\n"
                    + " p.CODIGOGRUPO mercid3,\n"
                    + " p.UNIDADE unidade,\n"
                    + " p.PRECOVENDA precovenda,\n"
                    + " p.PNOTAFISCAL custosemimposto,\n"
                    + " p.PRECOCUSTO custocomimposto,\n"
                    + " p.PRECOCUSTO customedio,\n"
                    + " p.MARGEMLUCRO  margem,\n"
                    + " p.MARGEMATACADO,\n"
                    + " p.PRECOATACADO,\n"
                    + " p.CODIGOFORNECEDOR cod_fornec,\n"
                    + " p.PESO,\n"
                    + " p.ENVIABALANCA BALANCA,\n"
                    + " p.MEDIDABALANCA,\n"
                    + " p.INATIVO inativo,\n"
                    + " p.NCM ncm,\n"
                    + " p2.CODIGOPISCREDITO piscofinscredito,\n"
                    + " p2.CODIGOPISdebito piscofinsdebito,\n"
                    + " p2.TRIBEXT icmsforaestato,\n"
                    + " p.NATUREZARECEITA,\n"
                    + " p.CODIGOFAMILIA familia,\n"
                    + " p2.CODIGOFISCAL cst,\n"
                    + " p2.TRIBEFETCPF ICMSCREDITO,\n"
                    + " p2.TRIBEFETCPF ICMSDEBITO,\n"
                    + " p2.PERCSUBSTITUICAO reduzido,\n"
                    + " p.CEST cest,\n"
                    + " b.SALDOATUAL estoque,\n"
                    + " p.ESTOQUEMINIMO,\n"
                    + " p.ESTOQUEMAXIMO,\n"
                    + " p.OPERACIONAL operacional, \n"
                    + " p.EMBALAGEM qtdEmb\n"
                    + " FROM PRODUTOS p\n"
                    + " JOIN PRODUTOSFILIAIS p2 ON p2.CODIGOPRODUTO = p.CODIGO\n"
                    + " LEFT JOIN ESTOQUE b ON p.CODIGO = b.CODIGOPRODUTO\n"
                    + " WHERE p2.FILIAL = " + getLojaOrigem() + " AND b.FILIAL = " + getLojaOrigem() + ""
            )) {
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().getProdutosBalanca();
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("id"));
                    imp.setDescricaoCompleta(rst.getString("descricao"));
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setCodMercadologico1(rst.getString("mercid1"));
                    imp.setCodMercadologico2(rst.getString("mercid2"));
                    imp.setCodMercadologico3(rst.getString("mercid3"));
                    imp.setTipoEmbalagem(rst.getString("unidade"));
                    imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setPiscofinsCstCredito(rst.getString("piscofinscredito"));
                    imp.setPiscofinsCstDebito(rst.getString("piscofinsdebito"));
                    imp.setIdFamiliaProduto(rst.getString("familia"));
                    imp.setPesoBruto(rst.getDouble("peso"));
                    imp.setQtdEmbalagem(1);
                    imp.setQtdEmbalagemCotacao(rst.getInt("qtdEmb"));
                    imp.setOperacional(rst.getInt("operacional"));

                    long codigoProduto;
                    codigoProduto = Long.parseLong(Utils.stringLong(rst.getString("ean")));
                    String pBalanca = String.valueOf(codigoProduto);

                    ProdutoBalancaVO bal;
                    if (removeDigitoEAN) {
                        bal = produtosBalanca.get(Utils.stringToInt(pBalanca.substring(0, pBalanca.length() - 1), -2));
                    } else {
                        bal = produtosBalanca.get(Utils.stringToInt(pBalanca, -2));
                    }

                    if (bal != null) {
                        imp.setEan(String.valueOf(bal.getCodigo()));
                        imp.setQtdEmbalagem(1);
                        imp.setTipoEmbalagem("U".equals(bal.getPesavel()) ? "UN" : "KG");
                        imp.setValidade(bal.getValidade());
                    } else {
                        if (rst.getString("balanca").equals("1")) {
                            if (pBalanca.length() < 7) {
                                imp.seteBalanca(true);
                                imp.setEan(rst.getString("ean"));
                                if (removeDigitoEAN) {
                                    imp.setEan(pBalanca.substring(0, pBalanca.length() - 1));
                                }
                            } else {
                                imp.seteBalanca(false);
                                imp.setEan(rst.getString("ean"));
                            }
                        } else {
                            imp.seteBalanca(false);
                            imp.setEan(rst.getString("ean"));
                        }
                    }

//                    int codigoProduto = Utils.stringToInt(rst.getString("id"), -2);
//                    ProdutoBalancaVO produtoBalanca = produtosBalanca.get(codigoProduto);
//
//                    if (produtoBalanca != null) {
//                        imp.setEan(String.valueOf(produtoBalanca.getCodigo()));
//                        imp.seteBalanca(true);
//                        imp.setTipoEmbalagem("U".equals(produtoBalanca.getPesavel()) ? "UN" : "KG");
//                        imp.setValidade(produtoBalanca.getValidade());
//                        imp.setQtdEmbalagem(1);
//                    } else {
//                        imp.setEan(rst.getString("ean"));
//                        imp.seteBalanca(false);
//                        imp.setTipoEmbalagem(rst.getString("unidade"));
//                        imp.setValidade(0);
//                        imp.setQtdEmbalagem(0);
//                    }
                    imp.setIcmsDebitoId(rst.getString("cst"));
                    imp.setIcmsDebitoForaEstadoId(rst.getString("cst"));
                    imp.setIcmsDebitoForaEstadoNfId(rst.getString("cst"));
                    imp.setIcmsCreditoId(rst.getString("cst"));
                    imp.setIcmsCreditoForaEstadoId(rst.getString("cst"));
                    imp.setIcmsConsumidorId(rst.getString("cst"));

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
                    + "	p.CODIGOFORNECEDOR fornecedorid,\n"
                    + "	p.CODIGO produtoid,\n"
                    + "	b.CODIGOREGISTRO referencia\n"
                    + " FROM \n"
                    + "	PRODUTOS p\n"
                    + "	JOIN BALANCOLOTES b ON b.CODIGO = p.CODIGO"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdFornecedor(rst.getString("fornecedorid"));
                    imp.setIdProduto(rst.getString("produtoid"));
                    imp.setCodigoExterno(rst.getString("referencia"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        if (gerarCodigoAtacado) {
            List<ProdutoIMP> result = new ArrayList<>();
            try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        " select \n"
                        + "CODIGO  id_produto, \n"
                        + "PRECOATACADO precoatacado,\n"
                        + "PRECOVENDA  preconormal,\n"
                        + "COALESCE(QUANTIDADEMINIMAATACADO, NULL, 0) qtde \n"
                        + "from Produtos"
                )) {
                    while (rst.next()) {
                        int codigoAtual = new ProdutoAnteriorDAO().getCodigoAnterior2(getSistema(), getLojaOrigem(), rst.getString("id_produto"));
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("id_produto"));
                        imp.setEan("999999" + String.valueOf(codigoAtual));
                        imp.setPrecovenda(rst.getDouble("preconormal"));
                        imp.setAtacadoPreco(rst.getDouble("precoatacado"));
                        imp.setQtdEmbalagem(rst.getInt("qtde"));
                        result.add(imp);
                    }
                }
            }
            return result;
        }
        if (segundoEan) {
            List<ProdutoIMP> result = new ArrayList<>();
            try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "SELECT\n"
                        + "	p.CODIGOPRODUTO,\n"
                        + "	p.CODIGOBARRAS,\n"
                        + "	p2.UNIDADE,\n"
                        + "	p2.EMBALAGEM qtd,\n"
                        + "	p2.PESO\n"
                        + "FROM\n"
                        + "	PRODUTOSCODIGOBARRAS p\n"
                        + "JOIN PRODUTOS p2 ON\n"
                        + "	p.CODIGOPRODUTO = p2.CODIGO\n"
                        + "WHERE CHAR_LENGTH(p.CODIGOBARRAS) > 7"
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("CODIGOPRODUTO"));
                        imp.setEan(rst.getString("CODIGOBARRAS"));
                        imp.setQtdEmbalagem(1);
                        imp.setPesoBruto(rst.getDouble("peso"));
                        result.add(imp);
                    }
                }
            }
            return result;
        } else {
            return super.getEANs();
        }
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + " c.CODIGO id,\n"
                    + " c.NOME nome,\n"
                    + " c.ENDERECO endereco,\n"
                    + " c.NUMERO numero,\n"
                    + " c.BAIRRO bairro,\n"
                    + " c.CEP cep,\n"
                    + " cd.CIDADE municipio,\n"
                    + " cd.CODIGOMUNICIPIO cidadeibge,\n"
                    + " cd.ESTADO uf,\n"
                    + " c.PESSOA TIPO,\n"
                    + " CASE \n"
                    + " 	WHEN c.PESSOA = 'FISICA'\n"
                    + " 	THEN c.cpf\n"
                    + " 	WHEN c.PESSOA = 'JURIDICA'\n"
                    + " 	THEN c.cgc\n"
                    + " END cpfcnpj,\n"
                    + " c.CGC, \n"
                    + " c.CPF cpf,\n"
                    + " c.IDENTIDADE rg,\n"
                    + " CASE \n"
                    + " 	WHEN c.PESSOA = 'FISICA'\n"
                    + " 	THEN c.IDENTIDADE\n"
                    + " 	WHEN c.PESSOA = 'JURIDICA'\n"
                    + " 	THEN c.INSCRICAOESTADUAL\n"
                    + " END inscestrg,\n"
                    + " c.INSCRICAOESTADUAL,\n"
                    + " c.TELEFONE telefone,\n"
                    + " c.TELEFONE2 celular,\n"
                    + " c.OBSERVACOES obs,\n"
                    + " c.DATACADASTRO dtcadastro,\n"
                    + " c.NASCIMENTO dtnasc,\n"
                    + " c.ENDERECO_COMPL complemento,\n"
                    + " c.CONJUGE nomeconjuge,\n"
                    + " c.NASCTOCONJUGE nasconjuge,\n"
                    + " c.CPFCONJUGE CPFCONJUGE,\n"
                    + " c.PAI nomepai,\n"
                    + " c.MAE nomemae,\n"
                    + " c.EMPRESATRABALHO  empresa,\n"
                    + " c.SALARIOTRABALHO renda,\n"
                    + " c.LIMITECREDITO limite,\n"
                    + " c.INATIVO ativo,\n"
                    + " c.BLOQUEADO bloqueado\n"
                    + "FROM CLIENTES c\n"
                    + "LEFT JOIN CIDADES cd ON cd.CODIGO = c.CODIGOCIDADE\n"
                    + "WHERE c.FILIAL = " + getLojaOrigem()
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rst.getString("id"));
                    imp.setRazao(rst.getString("nome"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setDataNascimento(rst.getDate("dtnasc"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setCnpj(rst.getString("cpfcnpj"));
                    imp.setInscricaoestadual(rst.getString("inscestrg"));
                    imp.setComplemento(rst.getString("complemento"));

                    imp.setTelefone(rst.getString("telefone"));

                    imp.setEmpresa(rst.getString("empresa"));
                    imp.setSalario(rst.getDouble("renda"));
                    imp.setCelular(rst.getString("celular"));
                    imp.setDataCadastro(rst.getDate("dtcadastro"));
                    imp.setValorLimite(rst.getDouble("limite"));
                    imp.setNomePai(rst.getString("nomepai"));
                    imp.setNomeMae(rst.getString("nomemae"));
                    imp.setNomeConjuge(rst.getString("nomeconjuge"));
                    imp.setCpfConjuge(rst.getString("CPFCONJUGE"));
                    imp.setObservacao(rst.getString("obs"));
                    imp.setAtivo(rst.getString("ativo").equals("0") ? true : false);
                    imp.setBloqueado(rst.getString("bloqueado").equals("0") ? true : false);

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
                    + "	f.CODIGO id,\n"
                    + "	f.NOME razao,\n"
                    + "	f.ENDERECO endereco,\n"
                    + "	f.BAIRRO bairro,\n"
                    + "	f.CEP cep,\n"
                    + "	c.CIDADE municipio,\n"
                    + "	c.CODIGOMUNICIPIO municipioibge,\n"
                    + "	c.ESTADO uf,\n"
                    + "	f.NUMERO numero,\n"
                    + "	f.telefone tel_principal,\n"
                    + "	f.telefone2 tel_celular,\n"
                    + "	f.FAX,\n"
                    + "	f.CGC cpfcnpj,\n"
                    + "	f.inscricaoestadual inscestadual,\n"
                    + "	f.FANTASIA fantasia,\n"
                    + "	f.DATACADASTRO dtcadastro,\n"
                    + "	f.email,\n"
                    + "	f.OBSERVACOES obs,\n"
                    + "	f.CONTATO \n"
                    + "FROM\n"
                    + "	FORNECEDORES f\n"
                    + "LEFT JOIN CIDADES c ON c.CODIGO = f.CODIGOCIDADE \n"
                    + "	ORDER BY NOME"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("id"));
                    imp.setCnpj_cpf(rst.getString("cpfcnpj"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setIe_rg(rst.getString("inscestadual"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setCep(rst.getString("cep"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setUf(rst.getString("uf"));
                    imp.setIbge_municipio(rst.getInt("municipioibge"));
                    imp.setTipoEmpresa(TipoEmpresa.LUCRO_REAL);
                    if (rst.getString("tel_principal") == null) {
                        imp.setTel_principal(rst.getString("tel_celular"));
                    } else {
                        imp.setTel_principal(rst.getString("tel_principal"));
                    }
                    imp.setDatacadastro(rst.getDate("dtcadastro"));
                    imp.setObservacao(rst.getString("obs"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    codigo id,\n"
                    + "    familia descritivo\n"
                    + "from \n"
                    + "    familias"
            )) {
                while (rst.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setDescricao(rst.getString("descritivo"));
                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<OfertaIMP> getOfertas(Date dataTermino) throws Exception {
        List<OfertaIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	o.DATAINICIOPROMOCAO dataInicio,\n"
                    + "	o.VALIDADEPROMOCAO dataFim,\n"
                    + "	o.CODIGOPRODUTO idProduto,\n"
                    + "	p.PRECOVENDA  precoNormal,\n"
                    + "	o.PRECOPROMOCAO precoOferta\n"
                    + "FROM\n"
                    + "	PROMOCAOBALCAO o\n"
                    + "	JOIN PRODUTOS p ON p.CODIGO = o.CODIGOPRODUTO "
            )) {
                while (rst.next()) {
                    OfertaIMP imp = new OfertaIMP();
                    imp.setIdProduto(rst.getString("idProduto"));
                    imp.setDataInicio(rst.getDate("dataInicio"));
                    imp.setDataFim(rst.getDate("dataFim"));
                    imp.setPrecoNormal(rst.getDouble("precoNormal"));
                    imp.setPrecoOferta(rst.getDouble("precoOferta"));
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
                    " SELECT \n"
                    + " r.CODIGOREGISTRO id,\n"
                    + " r.CODCLI idcliente,\n"
                    + " CASE\n"
                    + " 	WHEN r.DUPL = 'VALE'\n"
                    + " 	THEN r.CODIGOREGISTRO\n"
                    + " 	ELSE r.DUPL 	\n"
                    + " END numerodocumento,\n"
                    + " r.EMISSAO dataemissao,\n"
                    + " r.VENCIMENTO datavencimento,\n"
                    + " r.VALOR valor,\n"
                    + " r.VALORPAGTO\n"
                    + "FROM RECEBER r\n"
                    + "JOIN TIPODOCTO t ON r.TIPODOCTO = t.TIPODOCTO\n"
                    + "  WHERE VALORPAGTO = 0\n"
                    + "  AND VALOR > 0\n"
                    + "  AND t.CODIGO IN (6, 4)\n"
                    + "  AND FILIAL = " + getLojaOrigem() + ""
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rst.getString("id"));
                    imp.setIdCliente(rst.getString("idcliente"));
                    imp.setDataEmissao(rst.getDate("dataemissao"));
                    imp.setDataVencimento(rst.getDate("datavencimento"));
                    imp.setNumeroCupom(rst.getString("numerodocumento").replace("-", "").replace("/", "").substring(3));
                    imp.setValor(rst.getDouble("valor"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    /* Não desenvolvido */
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
        return new ManagerDAO.VendaIterator(getLojaOrigem(), this.dataInicioVenda, this.dataTerminoVenda);
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        return new ManagerDAO.VendaItemIterator(getLojaOrigem(), this.dataInicioVenda, this.dataTerminoVenda);
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
                        next.setEcf(Utils.stringToInt(rst.getString("ecf")));
                        next.setData(rst.getDate("data"));

                        String horaInicio = timestampDate.format(rst.getDate("data")) + " " + rst.getString("hora");
                        String horaTermino = timestampDate.format(rst.getDate("data")) + " " + rst.getString("hora");
                        next.setHoraInicio(timestamp.parse(horaInicio));
                        next.setHoraTermino(timestamp.parse(horaTermino));
                        next.setSubTotalImpressora(rst.getDouble("valor"));
                        next.setIdClientePreferencial(rst.getString("id_cliente"));
                        next.setCpf(rst.getString("cpf"));
                        next.setNomeCliente(rst.getString("nomecliente"));
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
                    + "	null\n"
                    + "FROM\n"
                    + "	"
                    + "WHERE\n"
                    + "";
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
                        next.setSequencia(rst.getInt("nritem"));
                        next.setProduto(rst.getString("id_produto"));
                        next.setUnidadeMedida(rst.getString("unidade"));
                        next.setDescricaoReduzida(rst.getString("descricao"));
                        next.setQuantidade(rst.getDouble("quantidade"));
                        next.setPrecoVenda(rst.getDouble("valor"));
                        next.setValorDesconto(rst.getDouble("desconto"));

                    }
                }
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaItemIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {
            this.sql
                    = "SELECT  \n"
                    + " null\n"
                    + "FROM\n"
                    + "  \n"
                    + "WHERE ";
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
