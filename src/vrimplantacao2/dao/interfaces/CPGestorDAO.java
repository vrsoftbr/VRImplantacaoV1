package vrimplantacao2.dao.interfaces;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao.classe.ConexaoOracle;
import vrimplantacao.dao.cadastro.FornecedorDAO;
import vrimplantacao.dao.cadastro.OfertaDAO;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.OfertaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao2.dao.cadastro.devolucao.receber.ReceberDevolucaoDAO;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto.ProdutoAnteriorDAO;
import vrimplantacao2.vo.cadastro.financeiro.ReceberDevolucaoVO;
import vrimplantacao2.vo.cadastro.mercadologico.MercadologicoNivelIMP;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoEstadoCivil;
import vrimplantacao2.vo.enums.TipoFornecedor;
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

public class CPGestorDAO extends InterfaceDAO {

    public String i_arquivo;

    @Override
    public String getSistema() {
        return "CPGestor";
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT PR_CODINT, \n"
                    + "PR_NOME \n"
                    + "FROM GERAL.PRODUT \n"
                    + "WHERE PR_MASTER = 'M' \n"
                    + "AND PR_ATIVO = 'S'\n"
                    + "AND LJ_ASSOCIACAO = " + getLojaOrigem() + "\n"
                    + "ORDER BY PR_CODINT"
            )) {
                while (rst.next()) {
                    while (rst.next()) {
                        FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("PR_CODINT"));
                        imp.setDescricao(rst.getString("PR_NOME"));
                        vResult.add(imp);
                    }
                }
            }
        }
        return vResult;
    }

    @Override
    public List<MercadologicoNivelIMP> getMercadologicoPorNivel() throws Exception {
        Map<String, MercadologicoNivelIMP> merc = new LinkedHashMap<>();
        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT "
                    + "SE_CODIG, "
                    + "SE_NOME, "
                    + "SE_TIPO \n"
                    + "FROM GERAL.setores \n"
                    + "WHERE LJ_ASSOCIACAO=" + getLojaOrigem() + " \n"
                    + "AND LENGTH(SE_CODIG) > 1\n"
                    + "AND SE_TIPO = 1 "
                    + "ORDER BY SE_CODIG, SE_TIPO"
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP imp = new MercadologicoNivelIMP();
                    imp.setId(rst.getString("SE_CODIG").substring(0, 2));
                    imp.setDescricao(rst.getString("SE_NOME"));
                    merc.put(imp.getId(), imp);
                }
            }

            try (ResultSet rst = stm.executeQuery(
                    "SELECT "
                    + "SE_CODIG, "
                    + "SE_NOME, "
                    + "SE_TIPO \n"
                    + "FROM GERAL.setores \n"
                    + "WHERE LJ_ASSOCIACAO=" + getLojaOrigem() + " \n"
                    + "AND LENGTH(SE_CODIG) > 1\n"
                    + "AND SE_TIPO = 2 "
                    + "ORDER BY SE_CODIG, SE_TIPO"
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP merc1 = merc.get(rst.getString("SE_CODIG").substring(0, 2));
                    if (merc1 != null) {
                        merc1.addFilho(
                                rst.getString("SE_CODIG").substring(2, 4),
                                rst.getString("SE_NOME")
                        );
                    }
                }
            }

            try (ResultSet rst = stm.executeQuery(
                    "SELECT "
                    + "SE_CODIG, "
                    + "SE_NOME, "
                    + "SE_TIPO \n"
                    + "FROM GERAL.setores \n"
                    + "WHERE LJ_ASSOCIACAO=" + getLojaOrigem() + " \n"
                    + "AND LENGTH(SE_CODIG) > 1\n"
                    + "AND SE_TIPO = 3 "
                    + "ORDER BY SE_CODIG, SE_TIPO"
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP merc1 = merc.get(rst.getString("SE_CODIG").substring(0, 2));
                    if (merc1 != null) {
                        MercadologicoNivelIMP merc2 = merc1.getNiveis().get(rst.getString("SE_CODIG").substring(2, 4));
                        if (merc2 != null) {
                            merc2.addFilho(
                                    rst.getString("SE_CODIG").substring(4, 6),
                                    rst.getString("SE_NOME")
                            );
                        }
                    }
                }
            }

            try (ResultSet rst = stm.executeQuery(
                    "SELECT "
                    + "SE_CODIG, "
                    + "SE_NOME, "
                    + "SE_TIPO \n"
                    + "FROM GERAL.setores \n"
                    + "WHERE LJ_ASSOCIACAO=" + getLojaOrigem() + " \n"
                    + "AND LENGTH(SE_CODIG) > 1\n"
                    + "AND SE_TIPO = 4 "
                    + "ORDER BY SE_CODIG, SE_TIPO"
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP merc1 = merc.get(rst.getString("SE_CODIG").substring(0, 2));
                    if (merc1 != null) {
                        MercadologicoNivelIMP merc2 = merc1.getNiveis().get(rst.getString("SE_CODIG").substring(2, 4));
                        if (merc2 != null) {
                            MercadologicoNivelIMP merc3 = merc2.getNiveis().get(rst.getString("SE_CODIG").substring(4, 6));
                            if (merc3 != null) {
                                merc3.addFilho(
                                        rst.getString("SE_CODIG").substring(6, 8),
                                        rst.getString("SE_NOME")
                                );
                            }
                        }
                    }
                }
            }
        }
        return new ArrayList<>(merc.values());
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        String codProduto = "";
        List<ProdutoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + "P.PR_CODINT, "
                    + "P.PR_CODIGO_MASTER, "
                    + "P2.PR_CBARRA, "
                    + "P2.PR_QTDE, "
                    + "P.PR_NOME, "
                    + "P.PR_NOMEABREVIADO, "
                    + "P.PR_NOMEGONDOLA, "
                    + "P2.PR_NOME_BAL, "
                    + "P.TC_CODIG, "
                    + "P.SE_CODIG, \n"
                    + "P.PR_ATIVO, "
                    + "PR_PRECOVENDA_ATUAL, "
                    + "P.PR_ULT_PRECOCUSTO, "
                    + "P.PR_DIAS_VALIDADE, "
                    + "P.PR_MARGEM_BRUTA_SCUSTO, \n"
                    + "P.PR_QTDE_CAIXA, "
                    + "P.DATA_INC, "
                    + "P.PR_CUSTO_SEM_ICMS, "
                    + "P.PLU, "
                    + "P.PR_CODIGOCSTENT, "
                    + "P.PR_CODIGOCSTSAI, \n"
                    + "P.PR_ALIQ_NF, "
                    + "P.PR_REDUCAO_BCALCULO_ICMS, "
                    + "P.UNI_VENDA, "
                    + "ESTOQUE_MINIMO, "
                    + "P.PR_MULT_PDV, \n"
                    + "P.NATUREZA_RECEITA, "
                    + "P.NCM, "
                    + "P.PR_PESO_LIQUIDO, "
                    + "P.PR_PESO_BRUTO, "
                    + "((PR_PRECOVENDA_ATUAL * (PR_PER_DESCONTO)) / 100) PER_DESCONTO, \n"
                    + "PR_PER_DESCONTO "
                    + "FROM GERAL.PRODUT P\n"
                    + "LEFT JOIN GERAL.PRODUT2 P2 ON P2.PR_CODINT = P.PR_CODINT\n"
                    + "WHERE P.LJ_ASSOCIACAO = " + getLojaOrigem() + " "
                    + "AND P2.LJ_ASSOCIACAO = " + getLojaOrigem()
            )) {
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().carregarProdutosBalanca();
                int cont = 0;
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setEan(rst.getString("PR_CBARRA"));

                    codProduto = new ProdutoAnteriorDAO().getCodigoAnterior3(getSistema(), getLojaOrigem(), rst.getString("PR_CODINT"));
                    if (!"-1".equals(codProduto)) {
                        imp.setImportId(codProduto);
                        imp.setPrecovenda(rst.getDouble("PR_PRECOVENDA_ATUAL"));
                    } else {
                        imp.setImportId(imp.getEan());
                        if (rst.getDouble("PR_PER_DESCONTO") < 0) {
                            imp.setPrecovenda(rst.getDouble("PR_PRECOVENDA_ATUAL") - rst.getDouble("PER_DESCONTO"));
                        } else if (rst.getDouble("PR_PER_DESCONTO") > 0) {
                            imp.setPrecovenda(rst.getDouble("PR_PRECOVENDA_ATUAL") - rst.getDouble("PER_DESCONTO"));
                        } else {
                            imp.setPrecovenda(rst.getDouble("PR_PRECOVENDA_ATUAL"));
                        }
                    }

                    /*if (Long.parseLong(imp.getEan()) <= 999999) {
                     imp.setImportId(rst.getString("PR_CBARRA"));
                     if ((rst.getString("PR_NOME_BAL") != null) && (!rst.getString("PR_NOME_BAL").trim().isEmpty())) {
                     imp.setDescricaoCompleta(rst.getString("PR_NOME_BAL"));
                     imp.setDescricaoReduzida(rst.getString("PR_NOME_BAL"));
                     imp.setDescricaoGondola(rst.getString("PR_NOME_BAL"));
                     } else {
                     imp.setDescricaoCompleta(rst.getString("PR_NOME"));
                     imp.setDescricaoReduzida(rst.getString("PR_NOMEABREVIADO"));
                     imp.setDescricaoGondola(rst.getString("PR_NOMEGONDOLA"));
                     }

                     if (rst.getDouble("PR_PER_DESCONTO") < 0) {
                     imp.setPrecovenda(rst.getDouble("PR_PRECOVENDA_ATUAL") - rst.getDouble("PER_DESCONTO"));
                     } else if (rst.getDouble("PR_PER_DESCONTO") > 0) {
                     imp.setPrecovenda(rst.getDouble("PR_PRECOVENDA_ATUAL") - rst.getDouble("PER_DESCONTO"));
                     } else {
                     imp.setPrecovenda(rst.getDouble("PR_PRECOVENDA_ATUAL"));
                     }

                     } else {
                     imp.setImportId(rst.getString("PR_CODINT"));
                     imp.setDescricaoCompleta(rst.getString("PR_NOME"));
                     imp.setDescricaoReduzida(rst.getString("PR_NOMEABREVIADO"));
                     imp.setDescricaoGondola(rst.getString("PR_NOMEGONDOLA"));
                     imp.setPrecovenda(rst.getDouble("PR_PRECOVENDA_ATUAL"));
                     }*/
                    if ((rst.getString("PR_CODIGO_MASTER") != null)
                            && (!rst.getString("PR_CODIGO_MASTER").trim().isEmpty())) {
                        imp.setIdFamiliaProduto(rst.getString("PR_CODIGO_MASTER"));
                    }
                    imp.setTipoEmbalagem(rst.getString("UNI_VENDA"));
                    imp.setQtdEmbalagem(rst.getInt("PR_QTDE"));
                    imp.setSituacaoCadastro("S".equals(rst.getString("PR_ATIVO")) ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    imp.setMargem(rst.getDouble("PR_MARGEM_BRUTA_SCUSTO"));
                    imp.setCustoComImposto(rst.getDouble("PR_ULT_PRECOCUSTO"));
                    imp.setCustoSemImposto(rst.getDouble("PR_CUSTO_SEM_ICMS"));
                    imp.setPesoBruto(rst.getDouble("PR_PESO_BRUTO"));
                    imp.setPesoLiquido(rst.getDouble("PR_PESO_LIQUIDO"));
                    imp.setNcm(rst.getString("NCM"));
                    if ((rst.getString("SE_CODIG") != null)
                            && (!rst.getString("SE_CODIG").trim().isEmpty())
                            && (rst.getString("SE_CODIG").length() == 8)) {
                        imp.setCodMercadologico1(rst.getString("SE_CODIG").substring(0, 2));
                        imp.setCodMercadologico2(rst.getString("SE_CODIG").substring(2, 4));
                        imp.setCodMercadologico3(rst.getString("SE_CODIG").substring(4, 6));
                        imp.setCodMercadologico4(rst.getString("SE_CODIG").substring(6, 8));
                    } else {
                        imp.setCodMercadologico1("0");
                        imp.setCodMercadologico2("0");
                        imp.setCodMercadologico3("0");
                        imp.setCodMercadologico4("0");
                    }

                    ProdutoBalancaVO produtoBalanca;
                    long codigoProduto;
                    if ((imp.getEan() != null)
                            && (!imp.getEan().trim().isEmpty())) {
                        if (Long.parseLong(imp.getEan()) <= 999999) {
                            codigoProduto = Long.parseLong(imp.getEan());
                            if (codigoProduto <= Integer.MAX_VALUE) {
                                produtoBalanca = produtosBalanca.get((int) codigoProduto);
                            } else {
                                produtoBalanca = null;
                            }

                            if (produtoBalanca != null) {
                                imp.seteBalanca(true);
                                imp.setValidade(produtoBalanca.getValidade() > 1 ? produtoBalanca.getValidade() : rst.getInt("PR_DIAS_VALIDADE"));
                            } else {
                                imp.setValidade(0);
                                imp.seteBalanca(false);
                            }
                        }
                    } else {
                        imp.setValidade(0);
                        imp.seteBalanca(false);
                    }

                    vResult.add(imp);
                    cont++;
                    System.out.println("carregando dados Produtos: " + cont);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<ProdutoIMP> getProdutos(OpcaoProduto opcao) throws Exception {
        if (opcao == OpcaoProduto.FAMILIA) {
            List<ProdutoIMP> vResult = new ArrayList<>();
            try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "SELECT PR_CODINT, PR_NOME, PR_CODIGO_MASTER\n"
                        + "FROM GERAL.PRODUT \n"
                        + "WHERE PR_MASTER = 'A' \n"
                        + "AND LJ_ASSOCIACAO = " + getLojaOrigem() + "\n"
                        + "ORDER BY PR_CODINT"
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("PR_CODINT"));
                        imp.setIdFamiliaProduto(rst.getString("PR_CODIGO_MASTER"));
                        vResult.add(imp);
                    }
                }
            }
            return vResult;
        }
        if (opcao == OpcaoProduto.ESTOQUE) {
            List<ProdutoIMP> vResult = new ArrayList<>();
            String codigoProduto = "";
            File f = new File("C:\\svn\\repositorioImplantacao\\Importacoes_OFFICIAL\\Importações Andamento\\TokLeve\\update_estoque.txt");
            FileWriter fw = new FileWriter(f);
            BufferedWriter bw = new BufferedWriter(fw);

            try {
                int linha = 0, cont = 0;
                WorkbookSettings settings = new WorkbookSettings();
                settings.setEncoding("CP1250");
                Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);
                Sheet[] sheets = arquivo.getSheets();
                Conexao.begin();
                for (int sh = 0; sh < sheets.length; sh++) {
                    Sheet sheet = arquivo.getSheet(sh);
                    linha = 0;

                    for (int i = 0; i < sheet.getRows(); i++) {
                        linha++;
                        if (linha == 1) {
                            continue;

                        }

                        if (sheet.getCell(0, i).getContents().contains("")
                                || (sheet.getCell(0, i).getContents().contains("DATA"))
                                || (sheet.getCell(0, i).getContents().contains("-------"))
                                || (sheet.getCell(0, i).getContents().contains("SETOR"))
                                || (sheet.getCell(0, i).getContents().contains("CODIGO")
                                || (sheet.getCell(0, i).getContents().contains("TOTAL")))
                                || ("".equals(sheet.getCell(0, i).getContents()))) {
                            continue;
                        }

                        Cell codEan = sheet.getCell(1, i);
                        Cell estoque = sheet.getCell(4, i);

                        ProdutoIMP imp = new ProdutoIMP();
                        try (Statement stm = Conexao.createStatement()) {
                            try (ResultSet rst = stm.executeQuery(
                                    "SELECT PR_CODINT, PR_CBARRA "
                                    + "FROM implantacao.produtos_tokleve "
                                    + "WHERE PR_CBARRA = lpad('" + codEan.getContents() + "', 14, '0')"
                            )) {
                                if (rst.next()) {
                                    codigoProduto = new ProdutoAnteriorDAO().getCodigoAnterior3(getSistema(), getLojaOrigem(), rst.getString("PR_CODINT"));
                                    if (!"-1".equals(codigoProduto)) {
                                        imp.setImportId(codigoProduto);

                                        bw.write("update produtocomplemento set estoque = "
                                                + Double.parseDouble(estoque.getContents().replace(".", "").replace(",", "."))
                                                + "where id_produto in (select codigoatual from implantacao.codant_produto "
                                                + "where impid = '" + codigoProduto + "');");
                                    } else {
                                        imp.setImportId(codEan.getContents().trim());
                                        bw.write("update produtocomplemento set estoque = "
                                                + Double.parseDouble(estoque.getContents().replace(".", "").replace(",", "."))
                                                + "where id_produto in (select codigoatual from implantacao.codant_produto "
                                                + "where impid = '" + codEan.getContents().trim() + "');");

                                    }
                                    bw.newLine();
                                    imp.setImportLoja(getLojaOrigem());
                                    imp.setImportSistema(getSistema());
                                    imp.setEstoque(Double.parseDouble(estoque.getContents().replace(".", "").replace(",", ".")));
                                    vResult.add(imp);
                                    cont++;
                                    System.out.println("carregando dados Estoque: " + cont);
                                }
                            }
                        }
                    }
                }
                bw.flush();
                bw.close();
                return vResult;
            } catch (Exception ex) {
                throw ex;
            }
        }
        if (opcao == OpcaoProduto.PIS_COFINS) {
            List<ProdutoIMP> vResult = new ArrayList<>();
            String codigoProduto = "";
            try {
                int linha = 0;
                WorkbookSettings settings = new WorkbookSettings();
                settings.setEncoding("CP1250");
                Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);
                Sheet[] sheets = arquivo.getSheets();

                int cont = 0;

                for (int sh = 0; sh < sheets.length; sh++) {
                    Sheet sheet = arquivo.getSheet(sh);
                    linha = 0;

                    for (int i = 0; i < sheet.getRows(); i++) {
                        linha++;
                        if (linha == 1) {
                            continue;
                        }

                        Cell codEan = sheet.getCell(1, i);
                        Cell cstPisEnt = sheet.getCell(13, i);
                        Cell cstPisSai = sheet.getCell(17, i);
                        Cell natReceita = sheet.getCell(22, i);

                        ProdutoIMP imp = new ProdutoIMP();

                        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
                            try (ResultSet rst = stm.executeQuery(
                                    "SELECT PR_CODINT, PR_CBARRA "
                                    + "FROM GERAL.PRODUT2 "
                                    + "WHERE PR_CBARRA = '" + codEan.getContents() + "'"
                            )) {
                                if (rst.next()) {
                                    codigoProduto = new ProdutoAnteriorDAO().getCodigoAnterior3(getSistema(), getLojaOrigem(), rst.getString("PR_CODINT"));
                                    if (!"-1".equals(codigoProduto)) {
                                        imp.setImportId(codigoProduto);
                                    } else {
                                        imp.setImportId(codEan.getContents().trim());
                                    }

                                    imp.setImportLoja(getLojaOrigem());
                                    imp.setImportSistema(getSistema());

                                    if ((cstPisSai.getContents() != null) && (!cstPisSai.getContents().trim().isEmpty())) {
                                        imp.setPiscofinsCstDebito(Integer.parseInt(cstPisSai.getContents().trim()));
                                    }
                                    if ((cstPisEnt.getContents() != null) && (!cstPisEnt.getContents().trim().isEmpty())) {
                                        imp.setPiscofinsCstCredito(Integer.parseInt(cstPisEnt.getContents().trim()));
                                    }
                                    if ((natReceita.getContents() != null) && (!natReceita.getContents().trim().isEmpty())) {
                                        imp.setPiscofinsNaturezaReceita(Integer.parseInt(natReceita.getContents()));
                                    }

                                    vResult.add(imp);
                                    cont++;
                                    System.out.println("carregando dados PisCofins: " + cont);
                                }
                            }
                        }
                    }
                }
                return vResult;
            } catch (Exception ex) {
                throw ex;
            }
        }
        if (opcao == OpcaoProduto.NATUREZA_RECEITA) {
            List<ProdutoIMP> vResult = new ArrayList<>();
            String codigoProduto = "";
            try {
                int linha = 0;
                WorkbookSettings settings = new WorkbookSettings();
                settings.setEncoding("CP1250");
                Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);
                Sheet[] sheets = arquivo.getSheets();
                int cont = 0;
                for (int sh = 0; sh < sheets.length; sh++) {
                    Sheet sheet = arquivo.getSheet(sh);
                    linha = 0;

                    for (int i = 0; i < sheet.getRows(); i++) {
                        linha++;
                        if (linha == 1) {
                            continue;
                        }

                        Cell codEan = sheet.getCell(1, i);
                        Cell cstPisEnt = sheet.getCell(13, i);
                        Cell cstPisSai = sheet.getCell(17, i);
                        Cell natReceita = sheet.getCell(22, i);

                        ProdutoIMP imp = new ProdutoIMP();

                        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
                            try (ResultSet rst = stm.executeQuery(
                                    "SELECT PR_CODINT, PR_CBARRA "
                                    + "FROM GERAL.PRODUT2 "
                                    + "WHERE PR_CBARRA = '" + codEan.getContents() + "'"
                            )) {
                                if (rst.next()) {
                                    codigoProduto = new ProdutoAnteriorDAO().getCodigoAnterior3(getSistema(), getLojaOrigem(), rst.getString("PR_CODINT"));
                                    if (!"-1".equals(codigoProduto)) {
                                        imp.setImportId(codigoProduto);
                                    } else {
                                        imp.setImportId(codEan.getContents().trim());
                                    }

                                    imp.setImportLoja(getLojaOrigem());
                                    imp.setImportSistema(getSistema());

                                    if ((cstPisSai.getContents() != null) && (!cstPisSai.getContents().trim().isEmpty())) {
                                        imp.setPiscofinsCstDebito(Integer.parseInt(cstPisSai.getContents().trim()));
                                    }
                                    if ((cstPisEnt.getContents() != null) && (!cstPisEnt.getContents().trim().isEmpty())) {
                                        imp.setPiscofinsCstCredito(Integer.parseInt(cstPisEnt.getContents().trim()));
                                    }
                                    if ((natReceita.getContents() != null) && (!natReceita.getContents().trim().isEmpty())) {
                                        imp.setPiscofinsNaturezaReceita(Integer.parseInt(natReceita.getContents()));
                                    }

                                    vResult.add(imp);
                                    cont++;
                                    System.out.println("carregando dados NaturezaReceita: " + cont);
                                }
                            }
                        }
                    }
                }
                return vResult;
            } catch (Exception ex) {
                throw ex;
            }
        }
        if (opcao == OpcaoProduto.ICMS) {
            List<ProdutoIMP> vResult = new ArrayList<>();
            String codigoProduto = "";
            try {
                int linha = 0;
                WorkbookSettings settings = new WorkbookSettings();
                settings.setEncoding("CP1250");
                Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);
                Sheet[] sheets = arquivo.getSheets();
                int cont = 0;

                for (int sh = 0; sh < sheets.length; sh++) {
                    Sheet sheet = arquivo.getSheet(sh);
                    linha = 0;

                    for (int i = 0; i < sheet.getRows(); i++) {
                        linha++;
                        if (linha == 1) {
                            continue;
                        }

                        Cell codEan = sheet.getCell(1, i);
                        Cell cstIcmsEnt = sheet.getCell(3, i);
                        Cell aliqIcmsEnt = sheet.getCell(4, i);
                        Cell reduIcmsEnt = sheet.getCell(5, i);
                        Cell cstIcmsSai = sheet.getCell(8, i);
                        Cell aliqIcmsSai = sheet.getCell(9, i);
                        Cell reduIcmsSai = sheet.getCell(10, i);

                        ProdutoIMP imp = new ProdutoIMP();

                        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
                            try (ResultSet rst = stm.executeQuery(
                                    "SELECT PR_CODINT, PR_CBARRA "
                                    + "FROM GERAL.PRODUT2 "
                                    + "WHERE PR_CBARRA = '" + codEan.getContents() + "'"
                            )) {
                                if (rst.next()) {
                                    codigoProduto = new ProdutoAnteriorDAO().getCodigoAnterior3(getSistema(), getLojaOrigem(), rst.getString("PR_CODINT"));
                                    if (!"-1".equals(codigoProduto)) {
                                        imp.setImportId(codigoProduto);
                                    } else {
                                        imp.setImportId(codEan.getContents().trim());
                                    }

                                    imp.setImportLoja(getLojaOrigem());
                                    imp.setImportSistema(getSistema());

                                    if ((cstIcmsSai.getContents() != null) && (!cstIcmsSai.getContents().trim().isEmpty())) {
                                        imp.setIcmsCstSaida(Integer.parseInt(cstIcmsSai.getContents()));
                                    }
                                    if ((aliqIcmsSai.getContents() != null) && (!aliqIcmsSai.getContents().trim().isEmpty())) {
                                        imp.setIcmsAliqSaida(Double.parseDouble(aliqIcmsSai.getContents().replace(",", ".")));
                                    }
                                    if ((reduIcmsSai.getContents() != null) && (!reduIcmsSai.getContents().trim().isEmpty())) {
                                        imp.setIcmsReducaoSaida(Double.parseDouble(reduIcmsSai.getContents().replace(",", ".")));
                                    }
                                    if ((cstIcmsEnt.getContents() != null) && (!cstIcmsEnt.getContents().trim().isEmpty())) {
                                        imp.setIcmsCstEntrada(Integer.parseInt(cstIcmsEnt.getContents()));
                                    }
                                    if ((aliqIcmsEnt.getContents() != null) && (!aliqIcmsEnt.getContents().trim().isEmpty())) {
                                        imp.setIcmsAliqEntrada(Double.parseDouble(aliqIcmsEnt.getContents().replace(",", ".")));
                                    }
                                    if ((reduIcmsEnt.getContents() != null) && (!reduIcmsEnt.getContents().trim().isEmpty())) {
                                        imp.setIcmsReducaoEntrada(Double.parseDouble(reduIcmsEnt.getContents().replace(",", ".")));
                                    }
                                    vResult.add(imp);
                                    cont++;
                                    System.out.println("carregando dados Icms: " + cont);
                                }
                            }
                        }
                    }
                }
                return vResult;
            } catch (Exception ex) {
                throw ex;
            }
        }
        return null;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT "
                    + "CF_CODIG, "
                    + "CF_RAZAO, "
                    + "CF_FANTA, "
                    + "CF_ENDER, "
                    + "CF_BAIRR,\n"
                    + "CF_CIDAD, "
                    + "CF_UF, "
                    + "CF_CEP, "
                    + "CF_TELEF1, "
                    + "CF_RAMAL1, "
                    + "CF_TELEF2,\n"
                    + "CF_RAMAL2, "
                    + "CF_FAX, "
                    + "CF_CGC, "
                    + "CF_INSCR, "
                    + "CF_CONTATO, "
                    + "TCF_DESCR,\n"
                    + "CF_OBSERV, "
                    + "CF_INATIVO, "
                    + "CF_CTELEF, "
                    + "CF_RNOME, "
                    + "CF_RTELEF, "
                    + "CF_SNOME,\n"
                    + "CF_PRAZO_PGTO, "
                    + "CF_SEMAIL, "
                    + "CF_REMAIL, "
                    + "CF_GREMAIL, "
                    + "CF_GNEMAIL, "
                    + "CF_ATACADISTA\n"
                    + "FROM GERAL.RC_CF\n"
                    + "WHERE LJ_ASSOCIACAO = " + getLojaOrigem() + "\n"
                    + "AND CF_TIPO = 'F'\n"
                    + "ORDER BY CF_CODIG"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("CF_CODIG"));
                    imp.setRazao(rst.getString("CF_RAZAO"));
                    imp.setFantasia(rst.getString("CF_FANTA"));
                    imp.setEndereco(rst.getString("CF_ENDER"));
                    imp.setBairro(rst.getString("CF_BAIRR"));
                    imp.setMunicipio(rst.getString("CF_CIDAD"));
                    imp.setUf(rst.getString("CF_UF"));
                    imp.setCep(rst.getString("CF_CEP"));
                    imp.setTel_principal(rst.getString("CF_TELEF1"));
                    imp.setCnpj_cpf(rst.getString("CF_CGC"));
                    imp.setIe_rg(rst.getString("CF_INSCR"));
                    imp.setObservacao(rst.getString("CF_OBSERV"));
                    imp.setAtivo("F".equals(rst.getString("CF_INATIVO")));
                    if ((rst.getString("TCF_DESCR") != null)
                            && (!rst.getString("TCF_DESCR").trim().isEmpty())) {
                        if (rst.getString("TCF_DESCR").contains("ATAC")) {
                            imp.setTipoFornecedor(TipoFornecedor.ATACADO);
                        } else if (rst.getString("TCF_DESCR").contains("DISTR")) {
                            imp.setTipoFornecedor(TipoFornecedor.DISTRIBUIDOR);
                        } else if (rst.getString("TCF_DESCR").contains("INDUS")) {
                            imp.setTipoFornecedor(TipoFornecedor.INDUSTRIA);
                        } else {
                            imp.setTipoFornecedor(TipoFornecedor.INDUSTRIA);
                        }
                    } else {
                        imp.setTipoFornecedor(TipoFornecedor.INDUSTRIA);
                    }

                    if ((rst.getString("CF_TELEF2") != null)
                            && (!rst.getString("CF_TELEF2").trim().isEmpty())) {
                        imp.addContato(
                                "1",
                                "TELEFONE 2",
                                Utils.formataNumero(rst.getString("CF_TELEF2")),
                                null,
                                TipoContato.COMERCIAL,
                                null);
                    }
                    if ((rst.getString("CF_FAX") != null)
                            && (!rst.getString("CF_FAX").trim().isEmpty())) {
                        imp.addContato(
                                "2",
                                "FAX",
                                Utils.formataNumero(rst.getString("CF_FAX")),
                                null,
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    if ((rst.getString("CF_CONTATO") != null)
                            && (!rst.getString("CF_CONTATO").trim().isEmpty())) {
                        imp.addContato(
                                "3",
                                rst.getString("CF_CONTATO"),
                                null,
                                null,
                                TipoContato.COMERCIAL,
                                null
                        );
                    }

                    if ((rst.getString("CF_CTELEF") != null)
                            && (!rst.getString("CF_CTELEF").trim().isEmpty())
                            && (!rst.getString("CF_CTELEF").contains("/"))) {
                        imp.addContato(
                                "4",
                                "TELEFONE",
                                Utils.formataNumero(rst.getString("CF_CTELEF")),
                                null,
                                TipoContato.COMERCIAL,
                                null
                        );
                    }

                    if ((rst.getString("CF_REMAIL") != null)
                            && (!rst.getString("CF_REMAIL").trim().isEmpty())) {
                        imp.addContato(
                                "5",
                                "EMAIL",
                                Utils.formataNumero(rst.getString("CF_REMAIL")),
                                null,
                                TipoContato.COMERCIAL,
                                rst.getString("CF_REMAIL") == null ? "" : rst.getString("CF_REMAIL").toLowerCase()
                        );
                    }

                    String contato = rst.getString("CF_CTELEF") != null ? rst.getString("CF_CTELEF") : "";
                    String[] cods = contato.split("/");
                    int cod = 6;

                    if ((rst.getString("CF_CTELEF") != null)
                            && (!rst.getString("CF_CTELEF").trim().isEmpty())
                            && (rst.getString("CF_CTELEF").contains("/"))) {

                        for (int i = 0; i < cods.length; i++) {
                            cod = 6 + i;
                            imp.addContato(
                                    String.valueOf(cod),
                                    "TELEFONE",
                                    Utils.formataNumero(cods[i]),
                                    null,
                                    TipoContato.COMERCIAL,
                                    null
                            );
                        }
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
        String codigoProduto = "";
        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    /*"SELECT "
                     + "PF.CF_CODIG, "
                     + "PF.PR_CODINT, "
                     + "PF.CODIGO_PRD_FOR, "
                     + "P.PR_CBARRA\n"
                     + "FROM GERAL.PRODUT3 PF\n"
                     + "INNER JOIN GERAL.PRODUT2 P ON P.PR_CODINT = PF.PR_CODINT\n"
                     + "WHERE PF.LJ_ASSOCIACAO = " + getLojaOrigem()
                     + "ORDER BY PF.CF_CODIG, PF.PR_CODINT"*/
                    "SELECT DISTINCT\n"
                    + "P.PR_CODINT, P2.PR_CBARRA, P.PR_NOME,\n"
                    + "F.CF_CODIG, F.CF_RAZAO, P3.CODIGO_PRD_FOR, PR_QTDE_CAIXA\n"
                    + "FROM GERAL.PRODUT P\n"
                    + "INNER JOIN GERAL.PRODUT2 P2 ON P2.PR_CODINT = P.PR_CODINT\n"
                    + "LEFT JOIN GERAL.PRODUT3 P3 ON P3.PR_CODINT = P.PR_CODINT\n"
                    + "INNER JOIN GERAL.RC_CF F ON F.CF_CODIG = P.FO_CODIG\n"
                    + "WHERE P.LJ_ASSOCIACAO = " + getLojaOrigem() + "\n"
                    + "AND P2.LJ_ASSOCIACAO = " + getLojaOrigem() + "\n"
                    + "AND F.LJ_ASSOCIACAO = " + getLojaOrigem() + "\n"
                    + "ORDER BY P.PR_NOME"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setIdFornecedor(rst.getString("CF_CODIG"));

                    codigoProduto = new ProdutoAnteriorDAO().getCodigoAnterior3(getSistema(), getLojaOrigem(), rst.getString("PR_CODINT"));
                    if (!"-1".equals(codigoProduto)) {
                        imp.setIdProduto(codigoProduto);
                    } else {
                        imp.setIdProduto(rst.getString("PR_CBARRA"));
                    }
                    /*if ((rst.getString("PR_CBARRA") != null)
                     && (!rst.getString("PR_CBARRA").trim().isEmpty())) {
                     if (Long.parseLong(rst.getString("PR_CBARRA")) <= 99999) {
                     imp.setIdProduto(rst.getString("PR_CBARRA"));
                     } else {
                     imp.setIdProduto(rst.getString("PR_CODINT"));
                     }
                     } else {
                     imp.setIdProduto(rst.getString("PR_CODINT"));
                     }*/
                    imp.setCodigoExterno(rst.getString("CODIGO_PRD_FOR"));
                    imp.setQtdEmbalagem(rst.getDouble("PR_QTDE_CAIXA"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> vResult = new ArrayList<>();
        String obs2;
        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + "CLI_CODIG, CLI_NOME, CLI_DTANASCIMENTO, CLI_SEXO,\n"
                    + "CLI_CPF, CLI_RG, CLI_EMISSORRG, CLI_DTAEMISSAO, CLI_ESTADOCIVIL,\n"
                    + "CLI_EMAIL, CLI_PROFISSAO, CLI_MAENOME, CLI_NOME_PAI, CLI_ENDERECO,\n"
                    + "CLI_NUMERO, CLI_COMPLEMENTO, CLI_BAIRRO, CLI_CIDADE, CLI_UF, CLI_CEP,\n"
                    + "CLI_RESIDEDESDE, CLI_SITUACAOCASA, CLI_FONERESIDENCIAL, CLI_FONECOMERCIAL,\n"
                    + "CLI_FONECELULAR, CLI_RAMALCOMERCIAL, CLI_DATAINC, CLI_NATURALIDADE, \n"
                    + "CLI_LIMITE, CLI_SITUACAO, CLI_EMPRESA, CLI_EMP_ENDERECO, CLI_EMP_BAIRRO,\n"
                    + "CLI_EMP_CIDADE, CLI_EMP_UF, CLI_EMP_CEP, CLI_BCO, CLI_BCO_AGE, CLI_BCO_TEL,\n"
                    + "CLI_BCO_CC, CLI_BCO_OBS, CLI_PESSOAIS_NOME, CLI_PESSOAIS_TEL, CLI_OBS, \n"
                    + "CLI_EMP_SALARIO, CLI_STATUS, CLI_TIPO, CLI_PESSOA, CLI_INATIVO, MNC_CODIG\n"
                    + "FROM GERAL.CRM_CLIENTES "
                    + "WHERE LJ_ASSOCIACAO = " + getLojaOrigem()
            )) {
                while (rst.next()) {
                    obs2 = "";
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("CLI_CODIG"));
                    imp.setRazao(rst.getString("CLI_NOME"));
                    imp.setFantasia(rst.getString("CLI_NOME"));
                    imp.setCnpj(rst.getString("CLI_CPF"));
                    imp.setInscricaoestadual(rst.getString("CLI_RG"));
                    imp.setOrgaoemissor(rst.getString("CLI_EMISSORRG"));
                    imp.setEndereco(rst.getString("CLI_ENDERECO"));
                    imp.setNumero(rst.getString("CLI_NUMERO"));
                    imp.setComplemento(rst.getString("CLI_COMPLEMENTO"));
                    imp.setBairro(rst.getString("CLI_BAIRRO"));
                    imp.setMunicipio(rst.getString("CLI_CIDADE"));
                    imp.setUf(rst.getString("CLI_CEP"));
                    imp.setCep(rst.getString("CLI_CEP"));
                    if ((rst.getString("CLI_FONERESIDENCIAL") != null)
                            && (!rst.getString("CLI_FONERESIDENCIAL").trim().isEmpty())) {
                        imp.setTelefone(Utils.formataNumero(rst.getString("CLI_FONERESIDENCIAL")));
                    }
                    if ((rst.getString("CLI_FONECELULAR") != null)
                            && (!rst.getString("CLI_FONECELULAR").trim().isEmpty())) {
                        imp.setCelular(Utils.formataNumero(rst.getString("CLI_FONECELULAR")));
                    }
                    if ((rst.getString("CLI_EMAIL") != null)
                            && (!rst.getString("CLI_EMAIL").trim().isEmpty())) {
                        imp.setEmail(rst.getString("CLI_EMAIL").toLowerCase());
                    }

                    if ((rst.getString("CLI_BCO") != null)
                            && (!rst.getString("CLI_BCO").trim().isEmpty())) {
                        obs2 = "CODBANCO " + rst.getString("CLI_BCO");
                    }
                    if ((rst.getString("CLI_BCO_AGE") != null)
                            && (!rst.getString("CLI_BCO_AGE").trim().isEmpty())) {
                        obs2 = obs2 + " AGENCIA " + rst.getString("CLI_BCO_AGE");
                    }
                    if ((rst.getString("CLI_BCO_CC") != null)
                            && (!rst.getString("CLI_BCO_CC").trim().isEmpty())) {
                        obs2 = obs2 + " CONTA " + rst.getString("CLI_BCO_CC");
                    }
                    if ((rst.getString("CLI_BCO_TEL") != null)
                            && (!rst.getString("CLI_BCO_TEL").trim().isEmpty())) {
                        obs2 = obs2 + " TELEFONE AGENCIA" + rst.getString("CLI_BCO_TEL");
                    }
                    if ((rst.getString("CLI_BCO_OBS") != null)
                            && (!rst.getString("CLI_BCO_OBS").trim().isEmpty())) {
                        obs2 = obs2 + " OBS " + rst.getString("CLI_BCO_OBS");
                    }

                    if ((rst.getString("CLI_ESTADOCIVIL") != null)
                            && (!rst.getString("CLI_ESTADOCIVIL").trim().isEmpty())) {
                        if (rst.getString("CLI_ESTADOCIVIL").contains("Casado")) {
                            imp.setEstadoCivil(TipoEstadoCivil.CASADO);
                        } else if (rst.getString("CLI_ESTADOCIVIL").contains("Solteiro")) {
                            imp.setEstadoCivil(TipoEstadoCivil.SOLTEIRO);
                        } else if (rst.getString("CLI_ESTADOCIVIL").contains("Viuvo")) {
                            imp.setEstadoCivil(TipoEstadoCivil.VIUVO);
                        } else if (rst.getString("CLI_ESTADOCIVIL").contains("Desquitado")) {
                            imp.setEstadoCivil(TipoEstadoCivil.DIVORCIADO);
                        } else {
                            imp.setEstadoCivil(TipoEstadoCivil.OUTROS);
                        }
                    } else {
                        imp.setEstadoCivil(TipoEstadoCivil.OUTROS);
                    }

                    if ((rst.getString("CLI_SEXO") != null)
                            && (!rst.getString("CLI_SEXO").trim().isEmpty())) {
                        if ("F".equals(rst.getString("CLI_SEXO"))) {
                            imp.setSexo(TipoSexo.FEMININO);
                        } else {
                            imp.setSexo(TipoSexo.MASCULINO);
                        }
                    } else {
                        imp.setSexo(TipoSexo.MASCULINO);
                    }

                    imp.setObservacao(rst.getString("CLI_OBS"));
                    imp.setObservacao2(obs2);
                    imp.setValorLimite(rst.getDouble("CLI_LIMITE"));
                    imp.setPermiteCreditoRotativo(true);
                    imp.setPermiteCheque(true);
                    imp.setAtivo("F".equals(rst.getString("CLI_INATIVO")));
                    imp.setNomePai(rst.getString("CLI_NOME_PAI"));
                    imp.setNomeMae(rst.getString("CLI_MAENOME"));
                    imp.setEmpresa(rst.getString("CLI_EMPRESA"));
                    imp.setEmpresaEndereco(rst.getString("CLI_EMP_ENDERECO"));
                    imp.setEmpresaBairro(rst.getString("CLI_EMP_BAIRRO"));
                    imp.setEmpresaMunicipio(rst.getString("CLI_EMP_CIDADE"));
                    imp.setEmpresaUf(rst.getString("CLI_EMP_UF"));
                    imp.setEmpresaCep(rst.getString("CLI_EMP_CEP"));
                    imp.setCargo(rst.getString("CLI_PROFISSAO"));
                    imp.setSalario(rst.getDouble("CLI_EMP_SALARIO"));

                    if ((rst.getString("CLI_FONECOMERCIAL") != null)
                            && (!rst.getString("CLI_FONECOMERCIAL").trim().isEmpty())) {
                        imp.addContato(
                                "1",
                                "COMERCIAL",
                                Utils.formataNumero(rst.getString("CLI_FONECOMERCIAL")),
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
        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT (R.CR_LANCAMENTO||'-'||R.CF_CODIG||'-'||R.LJ_ASSOCIACAO) ID,\n"
                    + "R.CF_TIPO, R.CF_CODIG, R.CR_DOCUMEN, \n"
                    + "TO_CHAR(R.CR_DTEMISS, 'YYYY-MM-DD') DTEMISSAO, \n"
                    + "TO_CHAR(R.CR_DTVECTO, 'YYYY-MM-DD') DTVENCIMENTO,\n"
                    + "R.CR_VLTITUL, R.CR_OBSERVA, R.CR_OBSERV2, R.LJ_ASSOCIACAO, \n"
                    + "R.NF, R.CR_PARCELA\n"
                    + "FROM GERAL.RCRECEB R\n"
                    + "WHERE R.CR_DTPGTO IS NULL\n"
                    + "AND R.CF_TIPO = 'C'\n"
                    + "AND R.LJ_ASSOCIACAO = " + getLojaOrigem()
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rst.getString("ID"));
                    imp.setIdCliente(rst.getString("CF_CODIG"));
                    imp.setNumeroCupom(rst.getString("CR_DOCUMEN"));
                    imp.setDataEmissao(rst.getDate("DTEMISSAO"));
                    imp.setDataVencimento(rst.getDate("DTVENCIMENTO"));
                    imp.setValor(rst.getDouble("CR_VLTITUL"));
                    imp.setObservacao(rst.getString("CR_OBSERVA") + " " + rst.getString("CR_OBSERV2"));
                    imp.setParcela(rst.getInt("CR_PARCELA"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    public List<ReceberDevolucaoVO> getReceberDevolucao() throws Exception {
        List<ReceberDevolucaoVO> vResult = new ArrayList<>();
        int idFornecedor;
        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT (R.CR_LANCAMENTO||'-'||R.CF_CODIG||'-'||R.LJ_ASSOCIACAO) ID,\n"
                    + "R.CF_TIPO, R.CF_CODIG, R.CR_DOCUMEN, \n"
                    + "TO_CHAR(R.CR_DTEMISS, 'YYYY-MM-DD') DTEMISSAO, \n"
                    + "TO_CHAR(R.CR_DTVECTO, 'YYYY-MM-DD') DTVENCIMENTO,\n"
                    + "R.CR_VLTITUL, R.CR_OBSERVA, R.CR_OBSERV2, R.LJ_ASSOCIACAO, \n"
                    + "R.NF, R.CR_PARCELA\n"
                    + "FROM GERAL.RCRECEB R\n"
                    + "WHERE R.CR_DTPGTO IS NULL\n"
                    + "AND R.CF_TIPO = 'F'\n"
                    + "AND R.LJ_ASSOCIACAO = " + getLojaOrigem()
            )) {
                while (rst.next()) {
                    String obs = "";

                    idFornecedor = new FornecedorDAO().getIdByCodAnt_Fornecedor(getSistema(), getLojaOrigem(), rst.getString("CF_CODIG"));
                    if (idFornecedor != -1) {
                        ReceberDevolucaoVO imp = new ReceberDevolucaoVO();
                        imp.setIdFornecedor(idFornecedor);
                        if ((rst.getString("CR_DOCUMEN") != null)
                                && (!rst.getString("CR_DOCUMEN").trim().isEmpty())) {
                            if (rst.getString("CR_DOCUMEN").trim().length() > 9) {
                                obs = "DOCUMENTO " + rst.getString("CR_DOCUMEN");
                            } else {
                                imp.setNumeroNota(Integer.parseInt(Utils.formataNumero(rst.getString("CR_DOCUMEN"))));
                            }
                        } else {
                            imp.setNumeroNota(0);
                        }
                        imp.setDataemissao(rst.getDate("DTEMISSAO"));
                        imp.setDatavencimento(rst.getDate("DTVENCIMENTO"));
                        imp.setValor(rst.getDouble("CR_VLTITUL"));
                        imp.setObservacao("IMPORTADO VR " + (rst.getString("CR_OBSERVA") == null ? "" : rst.getString("CR_OBSERVA").trim())
                                + " " + (rst.getString("NF") == null ? "" : rst.getString("NF").trim()) + " " + obs);
                        vResult.add(imp);
                    }
                }
            }
        }
        return vResult;
    }

    public void importarReceberDevolucao(int idLojaVR) throws Exception {
        List<ReceberDevolucaoVO> vResult = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados ReceberDevolucao...");
            vResult = getReceberDevolucao();
            if (!vResult.isEmpty()) {
                new ReceberDevolucaoDAO().salvar(vResult, idLojaVR);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void importarOfertas(int idLojaVR, int idLojaCliente, String impLoja) throws Exception {
        ProgressBar.setStatus("Carregando dados das ofertas");
        List<OfertaVO> ofertas = carregarOfertas(idLojaVR);
        new OfertaDAO().salvar2(ofertas, idLojaVR, impLoja);
    }

    public List<OfertaVO> carregarOfertas(int idLojaVR) throws Exception {
        List<OfertaVO> ofertas = new ArrayList<>();
        String codigoProduto = "";

        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + "PR_CODINT PRODUTO, \n"
                    + "PR_CBARRA BARRA,\n"
                    + "TO_CHAR(OFERTA_INICIO, 'YYYY-MM-DD') INICIO,\n"
                    + "TO_CHAR(OFERTA_FIM, 'YYYY-MM-DD') FIM,\n"
                    + "OFERTA_VALOR,\n"
                    + "GO_CODIG\n"
                    + "FROM GERAL.RC_GESTAO_OFERTA \n"
                    + "WHERE TO_CHAR(OFERTA_FIM, 'YYYY-MM-DD') > TO_CHAR(SYSDATE, 'YYYY-MM-DD') \n"
                    + "AND LJ_ASSOCIACAO = " + getLojaOrigem() + "\n"
                    + "AND PR_CODINT <> 0\n"
                    + "UNION ALL\n"
                    + "SELECT \n"
                    + "P.PR_CODINT PRODUTO,\n"
                    + "O.PR_CBARRA,\n"
                    + "TO_CHAR(O.OFERTA_INICIO, 'YYYY-MM-DD') INICIO,\n"
                    + "TO_CHAR(O.OFERTA_FIM, 'YYYY-MM-DD') FIM,\n"
                    + "O.OFERTA_VALOR,\n"
                    + "O.GO_CODIG\n"
                    + "FROM GERAL.RC_GESTAO_OFERTA O\n"
                    + "INNER JOIN GERAL.PRODUT2 P ON O.PR_CBARRA = P.PR_CBARRA\n"
                    + "WHERE TO_CHAR(OFERTA_FIM, 'YYYY-MM-DD') > TO_CHAR(SYSDATE, 'YYYY-MM-DD') \n"
                    + "AND O.LJ_ASSOCIACAO = " + getLojaOrigem() + "\n"
                    + "AND P.LJ_ASSOCIACAO = " + getLojaOrigem()
            )) {
                while (rst.next()) {
                    OfertaVO vo = new OfertaVO();
                    vo.setId_loja(idLojaVR);
                    codigoProduto = new ProdutoAnteriorDAO().getCodigoAnterior3(getSistema(), getLojaOrigem(), rst.getString("PRODUTO"));
                    if (!"-1".equals(codigoProduto)) {
                        vo.setIdProduto(codigoProduto);
                    } else {
                        vo.setIdProduto(rst.getString("BARRA"));
                    }
                    vo.setDatainicio(rst.getDate("INICIO"));
                    vo.setDatatermino(rst.getDate("FIM"));
                    vo.setPrecooferta(rst.getDouble("OFERTA_VALOR"));
                    ofertas.add(vo);
                }
            }
        }
        return ofertas;
    }

    public void importarOfertasFamilia(int idLojaVR, int idLojaCliente, String impLoja) throws Exception {
        ProgressBar.setStatus("Carregando dados das ofertas");
        List<OfertaVO> ofertas = carregarOfertasFamilia(idLojaVR);
        new OfertaDAO().salvar2(ofertas, idLojaVR, impLoja);
    }

    public List<OfertaVO> carregarOfertasFamilia(int idLojaVR) throws Exception {
        List<OfertaVO> ofertas = new ArrayList<>();
        String codigoProduto = "";
        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT DISTINCT PR_CODINT, \n"
                    + "TO_CHAR(OFERTA_INICIO, 'YYYY-MM-DD') INICIO, \n"
                    + "TO_CHAR(OFERTA_FIM, 'YYYY-MM-DD') FIM, \n"
                    + "PR_PRECOOFERTA\n"
                    + "FROM GERAL.PRODUTOS_EM_OFERTA \n"
                    + "WHERE LJ_ASSOCIACAO = " + getLojaOrigem() + "\n"
                    + "AND TO_CHAR(OFERTA_FIM, 'YYYY-MM-DD') > TO_CHAR(SYSDATE, 'YYYY-MM-DD')\n"
                    + "ORDER BY PR_CODINT"
            )) {
                while (rst.next()) {
                    OfertaVO vo = new OfertaVO();
                    vo.setId_loja(idLojaVR);
                    codigoProduto = new ProdutoAnteriorDAO().getCodigoAnterior3(getSistema(), getLojaOrigem(), rst.getString("PR_CODINT"));
                    if (!"-1".equals(codigoProduto)) {
                        vo.setIdProduto(codigoProduto);
                        vo.setDatainicio(rst.getDate("INICIO"));
                        vo.setDatatermino(rst.getDate("FIM"));
                        vo.setPrecooferta(rst.getDouble("PR_PRECOOFERTA"));
                        ofertas.add(vo);
                    }
                }
            }
        }
        return ofertas;
    }

    public void importarProdutosCPGestor(String i_arquio) throws Exception {
        int linha = 0;
        Statement stm = null;
        StringBuilder sql = null;
        try {
            Conexao.begin();
            stm = Conexao.createStatement();
            WorkbookSettings settings = new WorkbookSettings();
            settings.setEncoding("CP1250");
            Workbook arquivo = Workbook.getWorkbook(new File(i_arquio), settings);
            Sheet[] sheets = arquivo.getSheets();

            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;
                for (int i = 0; i < sheet.getRows(); i++) {
                    linha++;
                    if (linha == 1) {
                        continue;
                    }

                    Cell cellPr_codInt = sheet.getCell(0, i);
                    Cell cellPr_cBarra = sheet.getCell(1, i);
                    Cell cell_Pr_nome = sheet.getCell(2, i);

                    sql = new StringBuilder();
                    sql.append("insert into implantacao.produtos_getway ("
                            + "codprod, "
                            + "barras, "
                            + "descricao) "
                            + "values ("
                            + "'" + cellPr_codInt.getContents().trim() + "' ,"
                            + "'" + cellPr_cBarra.getContents().trim() + "', "
                            + "'" + Utils.acertarTexto(cell_Pr_nome.getContents().trim()) + "')");
                    stm.execute(sql.toString());
                    System.out.println(i);
                }
            }
            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }
}
