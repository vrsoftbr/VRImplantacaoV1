/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.interfaces;

import java.io.File;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import vrframework.classe.ProgressBar;
import vrimplantacao.classe.ConexaoDBF;
import vrimplantacao.dao.cadastro.BancoDAO;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.cadastro.oferta.TipoOfertaVO;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ChequeIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.OfertaIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author lucasrafael
 */
public class SiaCriareDbfDAO extends InterfaceDAO implements MapaTributoProvider {

    public String i_arquivo;
    public String v_pahtFileXls;

    @Override
    public String getSistema() {
        return "SiaCriareDbf";
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList();
        ConexaoDBF.abrirConexao(i_arquivo);

        try (Statement stmt = ConexaoDBF.getConexao().createStatement()) {
            try (ResultSet rs = stmt.executeQuery(
                    "select "
                    + "CODALIQ, CST, ALIQUOTA "
                    + "from aliquotas "
                    + "order by CODALIQ"
            )) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(
                            rs.getString("CODALIQ"),
                            "CST. " + rs.getString("CST") + " ALIQ. " + rs.getString("ALIQUOTA")));
                }
            }
        }
        return result;
    }

    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        ConexaoDBF.abrirConexao(i_arquivo);

        try (Statement stm = ConexaoDBF.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "CODEMP, DESCRICAO, CNPJ "
                    + "from empresa "
                    + "order by CODEMP"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(
                            rst.getString("CODEMP"),
                            rst.getString("DESCRICAO") + " " + rst.getString("CNPJ")));
                }
            }
        }

        return result;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();
        ConexaoDBF.abrirConexao(i_arquivo);

        try (Statement stm = ConexaoDBF.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "CODFAM, DESCRICAO "
                    + "from familias "
                    + "order by CODFAM"
            )) {
                while (rst.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("CODFAM"));
                    imp.setDescricao(rst.getString("DESCRICAO"));
                    result.add(imp);
                }
            }
            return result;
        }
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        ConexaoDBF.abrirConexao(i_arquivo);

        try (Statement stm = ConexaoDBF.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    /*"select "
                     + "m1.CODGRUPO as merc1, m1.DESCRICAO as desc_merc1, "
                     + "m2.CODCAT as merc2, m2.DESCRICAO as desc_merc2, "
                     + "m3.CODFAM as merc3, m3.DESCRICAO as desc_merc3 "
                     + "from produtos p "
                     + "left join grupos m1 on m1.CODGRUPO = p.GRUPO "
                     + "left join categorias m2 on m2.CODCAT = p.CATEGORIA "
                     + "left join familias m3 on m3.CODFAM = p.FAMILIA "
                     + "order by m1.CODGRUPO, m2.CODCAT, m3.CODFAM"*/
                    "select CODGRUPO, DESCRICAO "
                    + "from grupos "
                    + "order by CODGRUPO "
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rst.getString("CODGRUPO"));
                    imp.setMerc1Descricao(rst.getString("DESCRICAO"));
                    imp.setMerc2ID("1");
                    imp.setMerc2Descricao(rst.getString("DESCRICAO"));
                    imp.setMerc3ID("1");
                    imp.setMerc3Descricao(rst.getString("DESCRICAO"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        ConexaoDBF.abrirConexao(i_arquivo);

        try (Statement stm = ConexaoDBF.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "p.CODITEM, "
                    + "p.GRUPO, "
                    + "p.DESCRICAO, "
                    + "p.ABREVIA, "
                    + "p.CUSTO, "
                    + "p.UNITARIO, "
                    + "p.BALANCA, "
                    + "p.ALIQUOTASA as ICMS, "
                    + "p.UNIDADE, "
                    + "p.FAMILIA, "
                    + "p.CODBARRA, "
                    + "p.NCM, "
                    + "p.CATEGORIA, "
                    + "p.ATIVO, "
                    + "p.PESO_LIQUI, "
                    + "p.PESO_BRUTO, "
                    + "(p.QTDEMBALAG / 1000) as QTDEMB, "
                    + "p.PIS, "
                    + "p.COFINS, "
                    + "p.MARKDOWN, "
                    + "p.CEST, "
                    + "p.ID_SIMILAR "
                    + "from produtos p"
            )) {
                int cont = 0;
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().carregarProdutosBalanca();
                while (rst.next()) {
                    if (rst.getString("CODITEM") != null) {

                        ProdutoIMP imp = new ProdutoIMP();
                        ProdutoBalancaVO produtoBalanca;
                        imp.setImportId(rst.getString("CODITEM"));

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
                        imp.setEan(rst.getString("CODBARRA"));
                        imp.setDescricaoCompleta(rst.getString("DESCRICAO"));
                        imp.setDescricaoReduzida(rst.getString("ABREVIA"));
                        imp.setDescricaoGondola(imp.getDescricaoCompleta());
                        imp.setTipoEmbalagem(rst.getString("UNIDADE"));
                        imp.setQtdEmbalagem(rst.getInt("QTDEMB"));
                        imp.setIdFamiliaProduto(rst.getString("ID_SIMILAR"));
                        imp.setCodMercadologico1(rst.getString("GRUPO"));
                        imp.setCodMercadologico2("1");
                        imp.setCodMercadologico3("1");
                        imp.setMargem(rst.getDouble("MARKDOWN"));
                        imp.setPrecovenda(rst.getDouble("UNITARIO") / 1000);
                        imp.setCustoComImposto(rst.getDouble("CUSTO") / 1000);
                        imp.setCustoSemImposto(imp.getCustoComImposto());
                        imp.setNcm(rst.getString("NCM"));
                        imp.setCest(rst.getString("CEST"));
                        imp.setPiscofinsCstDebito(rst.getString("PIS"));
                        imp.setPiscofinsCstCredito(rst.getString("COFINS"));
                        imp.setIcmsDebitoId(rst.getString("ICMS"));
                        imp.setIcmsCreditoId(rst.getString("ICMS"));
                        result.add(imp);

                        cont++;

                        ProgressBar.setStatus(String.valueOf(cont));
                    }
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos(OpcaoProduto opt) throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        ConexaoDBF.abrirConexao(i_arquivo);
        WorkbookSettings settings = new WorkbookSettings();
        Workbook arquivo = Workbook.getWorkbook(new File(v_pahtFileXls + "//PRODUTOS.xls"), settings);
        Sheet[] sheets = arquivo.getSheets();
        int linha;

        if (opt == OpcaoProduto.PRECO) {
            try (Statement stm = ConexaoDBF.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select "
                        + "ID_PRODUTO, "
                        + "UNITARIO "
                        + "from estoque "
                        + "where ID_EMPRESA = " + getLojaOrigem()
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("ID_PRODUTO"));
                        imp.setPrecovenda(rst.getDouble("UNITARIO") / 1000);
                        result.add(imp);
                    }
                }
            }
            return result;
        }

        if (opt == OpcaoProduto.CUSTO) {
            try (Statement stm = ConexaoDBF.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select "
                        + "ID_PRODUTO, "
                        + "CUSTO "
                        + "from estoque "
                        + "where ID_EMPRESA = " + getLojaOrigem()
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("ID_PRODUTO"));
                        imp.setCustoComImposto(rst.getDouble("CUSTO") / 1000);
                        imp.setCustoSemImposto(imp.getCustoComImposto());
                        result.add(imp);
                    }
                }
            }
            return result;
        }

        if (opt == OpcaoProduto.ESTOQUE) {
            try (Statement stm = ConexaoDBF.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select "
                        + "ID_PRODUTO, "
                        + "QTD "
                        + "from estoque "
                        + "where ID_EMPRESA = " + getLojaOrigem()
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("ID_PRODUTO"));
                        imp.setEstoque(rst.getDouble("QTD") / 1000);
                        result.add(imp);
                    }
                }
            }
            return result;
        }

        if (opt == OpcaoProduto.ATIVO) {

            try {

                for (int sh = 0; sh < sheets.length; sh++) {
                    Sheet sheet = arquivo.getSheet(sh);
                    linha = 0;

                    for (int i = 0; i < sheet.getRows(); i++) {
                        linha++;
                        if (linha == 1) {
                            continue;
                        }

                        Cell cellIdProduto = sheet.getCell(1, i);

                        if ((cellIdProduto.getContents() != null)
                                && (!cellIdProduto.getContents().trim().isEmpty())) {

                            if (!Utils.encontrouLetraCampoNumerico(cellIdProduto.getContents())) {
                                ProdutoIMP imp = new ProdutoIMP();
                                imp.setImportLoja(getLojaOrigem());
                                imp.setImportSistema(getSistema());
                                imp.setImportId(cellIdProduto.getContents());
                                imp.setSituacaoCadastro(SituacaoCadastro.ATIVO);
                                result.add(imp);
                            }
                        }
                    }
                }
                return result;
            } catch (Exception ex) {
                throw ex;
            }

        }

        return null;
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        ConexaoDBF.abrirConexao(i_arquivo);

        try (Statement stm = ConexaoDBF.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "CODEAN, "
                    + "CODPROD "
                    + "from codigos_ean"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("CODPROD"));
                    imp.setEan(rst.getString("CODEAN"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        ConexaoDBF.abrirConexao(i_arquivo);

        try (Statement stm = ConexaoDBF.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "CODIGOCLI, "
                    + "NOMECLI, "
                    + "BAIRROCLI, "
                    + "CIDADECLI, "
                    + "ESTADOCLI, "
                    + "CEPCLI, "
                    + "FONECLI, "
                    + "FAXCLI, "
                    + "CPFCGC, "
                    + "IDENINSC, "
                    + "EMAIL, "
                    + "ENDERCLI, "
                    + "BANCO, "
                    + "CONTA, "
                    + "AGENCIA, "
                    + "ATIVO, "
                    + "RAZAO, "
                    + "ID_CIDADE, "
                    + "NUMERO "
                    + "from clientes "
                    + "where TIPO = 'F'"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("CODIGOCLI"));
                    imp.setRazao(rst.getString("RAZAO"));
                    imp.setFantasia(rst.getString("NOMECLI"));
                    imp.setCnpj_cpf(rst.getString("CPFCGC"));
                    imp.setIe_rg(rst.getString("IDENINSC"));
                    imp.setAtivo("S".equals(rst.getString("ATIVO")));
                    imp.setEndereco(rst.getString("ENDERCLI"));
                    imp.setNumero(rst.getString("NUMERO"));
                    imp.setCep(rst.getString("CEPCLI"));
                    imp.setBairro(rst.getString("BAIRROCLI"));
                    imp.setMunicipio(rst.getString("CIDADECLI"));
                    imp.setUf(rst.getString("ESTADOCLI"));
                    imp.setTel_principal(rst.getString("FONECLI"));
                    if ((rst.getString("FAXCLI") != null)
                            && (!rst.getString("FAXCLI").trim().isEmpty())) {
                        imp.addContato(
                                "FAX",
                                rst.getString("FAXCLI"),
                                null,
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    if ((rst.getString("EMAIL") != null)
                            && (!rst.getString("EMAIL").trim().isEmpty())) {
                        imp.addContato(
                                "EMAIL",
                                null,
                                null,
                                TipoContato.NFE,
                                rst.getString("EMAIL").toLowerCase()
                        );
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
        ConexaoDBF.abrirConexao(i_arquivo);

        try (Statement stm = ConexaoDBF.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "ID_PRODUTO, "
                    + "ID_FORNECE, "
                    + "NOTA as CODEXTERNO, "
                    + "ULTIMACOMP as DATAALTERACAO "
                    + "from produtos_fornecedores"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rst.getString("ID_PRODUTO"));
                    imp.setIdFornecedor(rst.getString("ID_FORNECE"));
                    imp.setCodigoExterno(rst.getString("CODEXTERNO"));
                    imp.setDataAlteracao(rst.getDate("DATAALTERACAO"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        ConexaoDBF.abrirConexao(i_arquivo);

        try (Statement stm = ConexaoDBF.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "CODIGOCLI, "
                    + "NOMECLI, "
                    + "BAIRROCLI, "
                    + "CIDADECLI, "
                    + "ESTADOCLI, "
                    + "CEPCLI, "
                    + "FONECLI, "
                    + "FAXCLI, "
                    + "CPFCGC, "
                    + "IDENINSC, "
                    + "EMAIL, "
                    + "ENDERCLI, "
                    + "BANCO, "
                    + "CONTA, "
                    + "AGENCIA, "
                    + "ATIVO, "
                    + "RAZAO, "
                    + "ID_CIDADE, "
                    + "NUMERO,"
                    + "NOMEPAI, "
                    + "NOMEMAE,"
                    + "OBSERVACAO, "
                    + "CARGO, "
                    + "RENDA_TITU, "
                    + "LIMITE_CRE "
                    + "from clientes "
                    + "where TIPO = 'C'"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("CODIGOCLI"));
                    imp.setRazao(rst.getString("RAZAO"));
                    imp.setFantasia(rst.getString("NOMECLI"));
                    imp.setCnpj(rst.getString("CPFCGC"));
                    imp.setInscricaoestadual(rst.getString("IDENINSC"));
                    imp.setAtivo("S".equals(rst.getString("ATIVO")));
                    imp.setEndereco(rst.getString("ENDERCLI"));
                    imp.setNumero(rst.getString("NUMERO"));
                    imp.setCep(rst.getString("CEPCLI"));
                    imp.setBairro(rst.getString("BAIRROCLI"));
                    imp.setMunicipio(rst.getString("CIDADECLI"));
                    imp.setUf(rst.getString("ESTADOCLI"));
                    imp.setTelefone(rst.getString("FONECLI"));
                    imp.setFax(rst.getString("FAXCLI"));
                    imp.setEmail(rst.getString("EMAIL"));
                    imp.setObservacao(rst.getString("OBSERVACAO"));
                    imp.setNomePai(rst.getString("NOMEPAI"));
                    imp.setNomeMae(rst.getString("NOMEMAE"));
                    imp.setCargo(rst.getString("CARGO"));
                    imp.setSalario(rst.getDouble("RENDA_TITU"));
                    imp.setValorLimite(rst.getDouble("LIMITE_CRE"));
                    imp.setPermiteCheque(true);
                    imp.setPermiteCreditoRotativo(true);
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();
        java.sql.Date dataEmissao, dataVencimento;
        DateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
        WorkbookSettings settings = new WorkbookSettings();
        Workbook arquivo = Workbook.getWorkbook(new File(v_pahtFileXls + "//creceber.xls"), settings);
        Sheet[] sheets = arquivo.getSheets();
        int linha;

        try {

            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;

                for (int i = 0; i < sheet.getRows(); i++) {
                    linha++;
                    if (linha == 1) {
                        continue;
                    }

                    Cell cellIdVenda = sheet.getCell(0, i);
                    Cell cellCodCliente = sheet.getCell(2, i);
                    Cell cellEmissao = sheet.getCell(4, i);
                    Cell cellVencimento = sheet.getCell(5, i);
                    Cell cellValor = sheet.getCell(6, i);
                    Cell cellHistorico = sheet.getCell(7, i);
                    Cell cellJuros = sheet.getCell(14, i);
                    Cell cellCaixa = sheet.getCell(23, i);
                    Cell cellCupom = sheet.getCell(24, i);

                    System.out.println(linha + " " + cellEmissao.getContents() + " " + cellVencimento.getContents());

                    if ((cellEmissao.getContents() != null)
                            && (!cellEmissao.getContents().trim().isEmpty())) {
                        dataEmissao = new java.sql.Date(fmt.parse(cellEmissao.getContents()).getTime());
                    } else {
                        dataEmissao = new Date(new java.util.Date().getTime());
                    }

                    if ((cellVencimento.getContents() != null)
                            && (!cellVencimento.getContents().trim().isEmpty())) {
                        dataVencimento = new java.sql.Date(fmt.parse(cellVencimento.getContents()).getTime());
                    } else {
                        dataVencimento = new Date(new java.util.Date().getTime());
                    }

                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(cellIdVenda.getContents());
                    imp.setIdCliente(cellCodCliente.getContents());
                    imp.setDataEmissao(dataEmissao);
                    imp.setDataVencimento(dataVencimento);
                    imp.setValor(Double.parseDouble(cellValor.getContents()));
                    imp.setJuros(Double.parseDouble(cellJuros.getContents()));
                    imp.setNumeroCupom(cellCupom.getContents());
                    imp.setEcf(cellCaixa.getContents());
                    imp.setObservacao(cellHistorico.getContents());
                    result.add(imp);
                }
            }
            return result;
        } catch (Exception ex) {
            throw ex;
        }
    }

    @Override
    public List<ChequeIMP> getCheques() throws Exception {
        List<ChequeIMP> result = new ArrayList<>();
        java.sql.Date dataEmissao, dataVencimento;
        DateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
        WorkbookSettings settings = new WorkbookSettings();
        Workbook arquivo = Workbook.getWorkbook(new File(v_pahtFileXls + "//cheques.xls"), settings);
        Sheet[] sheets = arquivo.getSheets();
        int linha;

        try {

            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;

                for (int i = 0; i < sheet.getRows(); i++) {
                    linha++;
                    if (linha == 1) {
                        continue;
                    }

                    Cell cellIdCheque = sheet.getCell(0, i);
                    Cell cellNumCheque = sheet.getCell(1, i);
                    Cell cellCpf = sheet.getCell(2, i);
                    Cell cellNomeEmitente = sheet.getCell(3, i);
                    Cell cellIdBanco = sheet.getCell(4, i);
                    Cell cellValor = sheet.getCell(5, i);
                    Cell cellVencimento = sheet.getCell(6, i);
                    Cell cellDevolvido = sheet.getCell(7, i);
                    Cell cellCaixa = sheet.getCell(8, i);
                    Cell cellCupom = sheet.getCell(9, i);
                    Cell cellEmissao = sheet.getCell(10, i);
                    Cell cellObservacao = sheet.getCell(12, i);
                    Cell cellCmc7 = sheet.getCell(17, i);
                    Cell cellAgencia = sheet.getCell(19, i);
                    Cell cellConta = sheet.getCell(20, i);

                    System.out.println(linha + " " + cellEmissao.getContents() + " " + cellVencimento.getContents());

                    if ((cellEmissao.getContents() != null)
                            && (!cellEmissao.getContents().trim().isEmpty())) {
                        dataEmissao = new java.sql.Date(fmt.parse(cellEmissao.getContents()).getTime());
                    } else {
                        dataEmissao = new Date(new java.util.Date().getTime());
                    }

                    if ((cellVencimento.getContents() != null)
                            && (!cellVencimento.getContents().trim().isEmpty())) {
                        dataVencimento = new java.sql.Date(fmt.parse(cellVencimento.getContents()).getTime());
                    } else {
                        dataVencimento = new Date(new java.util.Date().getTime());
                    }

                    int idBanco = new BancoDAO().getId(Integer.parseInt(cellIdBanco.getContents()));
                    ChequeIMP imp = new ChequeIMP();
                    imp.setId(cellIdCheque.getContents());
                    imp.setDate(dataEmissao);
                    imp.setDataDeposito(dataVencimento);
                    imp.setNumeroCupom(cellCupom.getContents());
                    imp.setNumeroCheque(cellNumCheque.getContents());
                    imp.setAgencia(cellAgencia.getContents());
                    imp.setConta(cellConta.getContents());                    
                    imp.setCpf(cellCpf.getContents());
                    imp.setNome(cellNomeEmitente.getContents());
                    imp.setObservacao(cellObservacao.getContents());
                    imp.setValor(Double.parseDouble(cellValor.getContents()));
                    imp.setBanco(idBanco);
                    imp.setCmc7(cellCmc7.getContents());
                    imp.setEcf(cellCaixa.getContents());
                    
                    if ("N".equals(cellDevolvido.getContents().trim())) {
                        imp.setAlinea(0);
                    } else {
                        imp.setAlinea(1);
                    }
                    
                    result.add(imp);
                }
            }
            return result;
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    @Override
    public List<OfertaIMP> getOfertas(java.util.Date dataTermino) throws Exception {
        List<OfertaIMP> result = new ArrayList<>();
        java.sql.Date dataFimOferta, dataInicioOferta;
        DateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
        WorkbookSettings settings = new WorkbookSettings();
        Workbook arquivo = Workbook.getWorkbook(new File(v_pahtFileXls + "//estoques.xls"), settings);
        Sheet[] sheets = arquivo.getSheets();
        int linha;
        Calendar c = Calendar.getInstance();

        try {

            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;

                for (int i = 0; i < sheet.getRows(); i++) {
                    linha++;
                    if (linha == 1) {
                        continue;
                    }

                    Cell cellIdProduto = sheet.getCell(0, i);
                    Cell cellPrecoOferta = sheet.getCell(7, i);
                    Cell cellInicioOferta = sheet.getCell(20, i);
                    Cell cellFimOferta = sheet.getCell(21, i);

                    if ((cellInicioOferta.getContents() != null)
                            && (!cellInicioOferta.getContents().trim().isEmpty())
                            && (!cellInicioOferta.getContents().contains("-"))
                            && (cellFimOferta.getContents() != null)
                            && (!cellFimOferta.getContents().trim().isEmpty())
                            && (!cellFimOferta.getContents().contains("-"))) {

                        if ((cellFimOferta.getContents() != null)
                                && (!cellFimOferta.getContents().trim().isEmpty())) {
                            dataFimOferta = new java.sql.Date(fmt.parse(cellFimOferta.getContents()).getTime());
                        } else {
                            dataFimOferta = new Date(new java.util.Date().getTime());
                        }

                        dataInicioOferta = new Date(new java.util.Date().getTime());

                        if (dataFimOferta.after(dataInicioOferta)) {
                            OfertaIMP imp = new OfertaIMP();
                            imp.setTipoOferta(TipoOfertaVO.CAPA);
                            imp.setIdProduto(cellIdProduto.getContents());
                            imp.setPrecoOferta(Double.parseDouble(cellPrecoOferta.getContents()));
                            imp.setDataInicio(dataInicioOferta);
                            imp.setDataFim(dataFimOferta);
                            result.add(imp);
                        }
                    }
                }
            }
            return result;
        } catch (Exception ex) {
            throw ex;
        }
    }
}
