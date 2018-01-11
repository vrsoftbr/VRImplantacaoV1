package vrimplantacao.dao.interfaces;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrframework.classe.Util;
import vrframework.classe.VRException;
import vrimplantacao.classe.Global;
import vrimplantacao.dao.cadastro.CestDAO;
import vrimplantacao.dao.cadastro.ClientePreferencialDAO;
import vrimplantacao.dao.cadastro.CompradorDAO;
import vrimplantacao.dao.cadastro.IcmsDAO;
import vrimplantacao.dao.cadastro.LojaDAO;
import vrimplantacao.dao.cadastro.MercadologicoDAO;
import vrimplantacao.dao.cadastro.NcmDAO;
import vrimplantacao.dao.cadastro.OfertaDAO;
import vrimplantacao.dao.cadastro.PisCofinsDAO;
import vrimplantacao.dao.cadastro.PlanoDAO;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.dao.cadastro.ProdutoDAO;
import vrimplantacao.dao.cadastro.ProdutoFornecedorDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao.vo.loja.SituacaoCadastro;
import vrimplantacao.vo.vrimplantacao.CestVO;
import vrimplantacao.vo.vrimplantacao.ClientePreferencialVO;
import vrimplantacao.vo.vrimplantacao.CodigoAnteriorVO;
import vrimplantacao.vo.vrimplantacao.ProdutosUnificacaoVO;
import vrimplantacao.vo.vrimplantacao.CompradorVO;
import vrimplantacao.vo.vrimplantacao.MercadologicoVO;
import vrimplantacao.vo.vrimplantacao.NcmVO;
import vrimplantacao.vo.vrimplantacao.OfertaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAliquotaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAutomacaoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoComplementoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoFornecedorVO;
import vrimplantacao.vo.vrimplantacao.ProdutoVO;
import vrimplantacao.vo.vrimplantacao.TipoPisCofinsVO;

public class PlanilhaPadraoDAO {

    public void importarCodigoBarraEmBranco() throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();

        try {

            ProgressBar.setStatus("Carregando dados...Produtos...Codigo Barras...");
            Map<Long, ProdutoVO> vCodigoBarra = carregarCodigoBarrasEmBranco();

            ProgressBar.setMaximum(vCodigoBarra.size());

            for (Long keyId : vCodigoBarra.keySet()) {

                ProdutoVO oProduto = vCodigoBarra.get(keyId);

                vProdutoNovo.add(oProduto);

                ProgressBar.next();
            }

            produto.addCodigoBarrasEmBranco(vProdutoNovo);

        } catch (Exception ex) {

            throw ex;
        }
    }

    public void migrarProdutoLoja(String i_arquivo, int i_idLojaDestino) throws Exception {
        try {
            ProgressBar.setStatus("Carregando dados para comparação...");

            List<ProdutoVO> vProdutoOrigem = carregarProdutoOrigem(i_arquivo);

            List<LojaVO> vLoja = new LojaDAO().carregar();

            ProgressBar.setStatus("Comparando produtos Arquivo Origem/Loja Destino...");
            ProgressBar.setMaximum(vProdutoOrigem.size());

            if (!vProdutoOrigem.isEmpty()) {
                ProdutoDAO produtoDAO = new ProdutoDAO();
                produtoDAO.gerarCodigoBarrasSeRepetido = true;
                produtoDAO.salvar(vProdutoOrigem, i_idLojaDestino, vLoja);
            }
        } catch (Exception e) {
            throw e;
        }
    }

    public void migrarPrecoMargemCustoCarnauba(String i_arquivo, int i_idLojaDestino) throws Exception {
        try {
            ProgressBar.setStatus("Carregando dados ... Preço Loja " + i_idLojaDestino + "...");

            List<ProdutoVO> vProdutoOrigem = carregarPrecoMargemCustoCarnauba(i_arquivo, i_idLojaDestino);

            ProgressBar.setMaximum(vProdutoOrigem.size());

            if (!vProdutoOrigem.isEmpty()) {
                ProdutoDAO produtoDAO = new ProdutoDAO();
                produtoDAO.acertarPrecoMargemCustoCarnauba(vProdutoOrigem, i_idLojaDestino);
            }
        } catch (Exception e) {
            throw e;
        }
    }

    public void migrarDataProdutoCarnauba(String i_arquivo, int i_idLojaDestino) throws Exception {
        try {
            ProgressBar.setStatus("Carregando dados ... Data Cadastro...");

            List<ProdutoVO> vProdutoOrigem = carregarDataProdutosCarnauba(i_arquivo, i_idLojaDestino);

            ProgressBar.setMaximum(vProdutoOrigem.size());

            if (!vProdutoOrigem.isEmpty()) {
                ProdutoDAO produtoDAO = new ProdutoDAO();
                produtoDAO.p_idLoja = i_idLojaDestino;
                produtoDAO.verificarLoja = true;
                produtoDAO.altertarDataCadastroProdutoGdoor(vProdutoOrigem);
            }
        } catch (Exception e) {
            throw e;
        }
    }

    public void migrarProdutoLojaPaiva(String i_arquivo, int i_idLojaDestino) throws Exception {
        try {
            ProgressBar.setStatus("Carregando dados para importação...");

            List<ProdutoVO> vProdutoOrigem = carregarProdutoPaiva(i_arquivo);

            List<LojaVO> vLoja = new LojaDAO().carregar();

            ProgressBar.setMaximum(vProdutoOrigem.size());

            if (!vProdutoOrigem.isEmpty()) {
                ProdutoDAO produtoDAO = new ProdutoDAO();
                produtoDAO.salvar(vProdutoOrigem, i_idLojaDestino, vLoja);
            }
        } catch (Exception e) {
            throw e;
        }
    }

    public void migrarProdutoICMSPaiva(String i_arquivo) throws Exception {
        try {
            ProgressBar.setStatus("Carregando dados para importação...");

            List<ProdutoVO> vProdutoOrigem = carregarProdutoICMSPaiva(i_arquivo);

            List<LojaVO> vLoja = new LojaDAO().carregar();

            ProgressBar.setMaximum(vProdutoOrigem.size());

            if (!vProdutoOrigem.isEmpty()) {
                acertarICMS(vProdutoOrigem);
            }
        } catch (Exception e) {
            throw e;
        }
    }

    public void migrarProdutoICMSSaoFrancisco(String i_arquivo) throws Exception {
        try {
            ProgressBar.setStatus("Carregando dados para importação...");

            List<ProdutoVO> vProdutoOrigem = carregarProdutoICMSSaoFrancisco(i_arquivo);

            List<LojaVO> vLoja = new LojaDAO().carregar();

            ProgressBar.setMaximum(vProdutoOrigem.size());

            if (!vProdutoOrigem.isEmpty()) {
                new ProdutoDAO().alterarIcmsProdutoPCSistemas(vProdutoOrigem);
            }
        } catch (Exception e) {
            throw e;
        }
    }

    public void migrarProdutoICMSSysPdv(String i_arquivo) throws Exception {
        try {
            ProgressBar.setStatus("Carregando dados para importação...");

            List<ProdutoVO> vProdutoOrigem = carregarProdutoICMSSysPdv(i_arquivo);

            List<LojaVO> vLoja = new LojaDAO().carregar();

            ProgressBar.setMaximum(vProdutoOrigem.size());

            if (!vProdutoOrigem.isEmpty()) {
                ProdutoDAO produtoDAO = new ProdutoDAO();
                produtoDAO.eBarras = true;
                produtoDAO.alterarICMSProduto(vProdutoOrigem);
            }
        } catch (Exception e) {
            throw e;
        }
    }

    public void migrarProdutoLojaPrecoProdutosPaiva(String i_arquivo, int i_idLojaDestino) throws Exception {
        try {
            ProgressBar.setStatus("Carregando dados para importação...");

            List<ProdutoVO> vProdutoOrigem = carregarProdutoPrecoCustoMargemPaiva(i_arquivo);

            List<LojaVO> vLoja = new LojaDAO().carregar();

            ProgressBar.setMaximum(vProdutoOrigem.size());

            if (!vProdutoOrigem.isEmpty()) {
                ProdutoDAO produtoDAO = new ProdutoDAO();
                produtoDAO.alterarPrecoCustoMargemProdutosPaiva(vProdutoOrigem, i_idLojaDestino);
            }
        } catch (Exception e) {
            throw e;
        }
    }

    public void migrarProdutoPisCofins(String i_arquivo, int i_idLojaDestino) throws Exception {
        try {
            ProgressBar.setStatus("Carregando dados para comparação...");

            List<ProdutoVO> vProdutoPisCofins = carregarPisCofins(i_arquivo);

            ProgressBar.setStatus("Comparando produtos Arquivo Origem/Loja Destino...");
            ProgressBar.setMaximum(vProdutoPisCofins.size());

            if (!vProdutoPisCofins.isEmpty()) {
                ProdutoDAO produtoDAO = new ProdutoDAO();
                produtoDAO.alterarPisCofinsProdutoSysPdv(vProdutoPisCofins, i_idLojaDestino);
            }
        } catch (Exception e) {
            throw e;
        }
    }

    public void migrarProdutoLojaCarnauba(String i_arquivo, int i_idLojaDestino) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        List<ProdutoVO> vProdutoAlterado = new ArrayList<>();

        try {
            ProgressBar.setStatus("Carregando dados para comparação...");

            Map<Long, ProdutoVO> vProdutoOrigem = carregarProdutosCarnauba(i_arquivo, i_idLojaDestino);
            Map<Long, Long> vProdutoDestino = new ProdutoDAO().carregarCodigoBarras();

            List<LojaVO> vLoja = new LojaDAO().carregar();

            ProgressBar.setStatus("Comparando produtos Arquivo Origem/Loja Destino Carnaúba...Loja " + i_idLojaDestino);
            ProgressBar.setMaximum(vProdutoOrigem.size() + vProdutoDestino.size());

            for (Long keyCodigoBarra : vProdutoOrigem.keySet()) {

                if (vProdutoDestino.containsKey(keyCodigoBarra)) {
                    long codigoProduto = vProdutoDestino.get(keyCodigoBarra);
                    ProdutoVO oProduto = vProdutoOrigem.get(keyCodigoBarra);

                    oProduto.id = (int) codigoProduto;

                    vProdutoAlterado.add(oProduto);
                } else {

                    ProdutoVO oProduto = vProdutoOrigem.get(keyCodigoBarra);

                    vProdutoNovo.add(oProduto);
                }

                ProgressBar.next();
            }

            if (!vProdutoAlterado.isEmpty()) {
                ProdutoDAO produtoDAO = new ProdutoDAO();
                produtoDAO.implantacaoExterna = true;
                //produtoDAO.implantacaoUnificacao = true;
                produtoDAO.salvar(vProdutoAlterado, i_idLojaDestino, true, vLoja, false, 0);
            }

            if (!vProdutoNovo.isEmpty()) {
                ProdutoDAO produtoDAO = new ProdutoDAO();
                produtoDAO.implantacaoExterna = true;
                produtoDAO.verificarLoja = true;
                //produtoDAO.implantacaoUnificacao = true;
                produtoDAO.salvar(vProdutoNovo, i_idLojaDestino, vLoja);
            }
        } catch (Exception e) {
            throw e;
        }
    }

    public void migrarProdutoEstoqueCarnauba(String i_arquivo, int i_idLojaDestino) throws Exception {
        try {
            ProgressBar.setStatus("Carregando dados para comparação...");

            List<ProdutoVO> vProdutoEstoque = carregarEstoqueProdutosCarnauba(i_arquivo);

            ProgressBar.setStatus("Comparando produtos Arquivo Origem/Loja Destino...");
            ProgressBar.setMaximum(vProdutoEstoque.size());

            if (!vProdutoEstoque.isEmpty()) {
                ProdutoDAO produtoDAO = new ProdutoDAO();
                produtoDAO.alterarEstoqueSysPdvCarnauba(vProdutoEstoque, i_idLojaDestino);
            }
        } catch (Exception e) {
            throw e;
        }
    }

    public void migrarProdutoReceitaFreitas(String i_arquivo, int i_idLojaDestino) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados para comparação...");

            Map<Integer, ProdutoVO> vProdutoOrigem = carregarProdutosReceitaFreitas(i_arquivo, i_idLojaDestino);

            List<LojaVO> vLoja = new LojaDAO().carregar();

            ProgressBar.setStatus("Comparando Produtos Receita " + i_idLojaDestino);
            ProgressBar.setMaximum(vProdutoOrigem.size());

            for (Integer keyCodigo : vProdutoOrigem.keySet()) {
                ProdutoVO oProduto = vProdutoOrigem.get(keyCodigo);
                vProdutoNovo.add(oProduto);
                ProgressBar.next();
            }

            if (!vProdutoNovo.isEmpty()) {
                ProdutoDAO produtoDAO = new ProdutoDAO();
                produtoDAO.salvar(vProdutoNovo, i_idLojaDestino, vLoja, true);
            }
        } catch (Exception e) {
            throw e;
        }
    }

    public Map<Long, ProdutoVO> carregarCodigoBarrasEmBranco() throws SQLException, Exception {
        StringBuilder sql = null;
        Statement stmPostgres = null;
        ResultSet rst;
        Map<Long, ProdutoVO> vProduto = new HashMap<>();
        int qtdeEmbalagem;
        double idProduto;
        long codigobarras;
        Utils util = new Utils();

        try {

            stmPostgres = Conexao.createStatement();

            sql = new StringBuilder();
            sql.append("select id, id_tipoembalagem ");
            sql.append("  from produto p ");
            sql.append(" where id not in (select id_produto from produtoautomacao) ");

            rst = stmPostgres.executeQuery(sql.toString());

            while (rst.next()) {

                idProduto = Double.parseDouble(rst.getString("ID"));

                if ((rst.getInt("id_tipoembalagem") == 4)
                        || (rst.getInt("id") < 10000)) {
                    codigobarras = util.gerarEan13((int) idProduto, false);
                } else {
                    codigobarras = util.gerarEan13((int) idProduto, true);
                }

                qtdeEmbalagem = 1;

                ProdutoVO oProduto = new ProdutoVO();
                oProduto.id = (int) idProduto;
                ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();
                oAutomacao.codigoBarras = codigobarras;
                oAutomacao.qtdEmbalagem = qtdeEmbalagem;
                oProduto.vAutomacao.add(oAutomacao);
                vProduto.put(codigobarras, oProduto);
            }

            return vProduto;

        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }

    private List<ProdutoVO> carregarPisCofins(String i_arquivo) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        Utils util = new Utils();

        try {

            int idTipoPisCofins, idTipoPisCofinsCredito, tipoNaturezaReceita, linha;
            double idProduto;

            WorkbookSettings settings = new WorkbookSettings();
            settings.setEncoding("CP1250");

            Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);

            Sheet[] sheets = arquivo.getSheets();

            try {

                for (int sh = 0; sh < sheets.length; sh++) {
                    Sheet sheet = arquivo.getSheet(sh);
                    linha = 0;

                    for (int i = 0; i < sheet.getRows(); i++) {
                        linha++;

                        //ignora o cabeçalho
                        if (linha == 1) {
                            continue;

                        } else if (sheet.getCell(1, i).getContents().isEmpty()) { //ignora linha em branco
                            continue;
                        } else {

                            Cell cellIdProduto = sheet.getCell(1, i);
                            Cell cellPisCofinsDebito = sheet.getCell(6, i);
                            Cell cellPisCofinsCredito = sheet.getCell(5, i);
                            Cell cellNaturezaReceita = sheet.getCell(7, i);

                            idProduto = Double.parseDouble(cellIdProduto.getContents().trim().substring(0,
                                    cellIdProduto.getContents().trim().length() - 3));

                            if ((cellPisCofinsDebito.getContents() != null)
                                    && (!cellPisCofinsDebito.getContents().trim().isEmpty())
                                    && (!"NULL".equals(cellPisCofinsDebito.getContents().trim()))) {

                                idTipoPisCofins = util.retornarPisCofinsDebito(Integer.parseInt(cellPisCofinsDebito.getContents().trim()));
                            } else {
                                idTipoPisCofins = 0;
                            }

                            if ((cellPisCofinsCredito.getContents() != null)
                                    && (!cellPisCofinsCredito.getContents().trim().isEmpty())
                                    && (!"NULL".equals(cellPisCofinsCredito.getContents().trim()))) {

                                idTipoPisCofinsCredito = util.retornarPisCofinsCredito(Integer.parseInt(cellPisCofinsCredito.getContents().trim()));
                            } else {
                                idTipoPisCofinsCredito = 12;
                            }

                            if ((cellNaturezaReceita.getContents() != null)
                                    && (!cellNaturezaReceita.getContents().trim().isEmpty())
                                    && (!"NULL".equals(cellNaturezaReceita.getContents().trim()))
                                    && (!"0".equals(cellNaturezaReceita.getContents().trim()))) {

                                tipoNaturezaReceita = util.retornarTipoNaturezaReceita(idTipoPisCofins,
                                        cellNaturezaReceita.getContents().trim());
                            } else {
                                tipoNaturezaReceita = -1;
                            }

                            ProdutoVO oProduto = new ProdutoVO();

                            oProduto.idDouble = idProduto;
                            oProduto.idTipoPisCofinsDebito = idTipoPisCofins;
                            oProduto.idTipoPisCofinsCredito = idTipoPisCofinsCredito;
                            oProduto.tipoNaturezaReceita = tipoNaturezaReceita;

                            vProduto.add(oProduto);
                        }
                    }
                }
            } catch (Exception ex) {
                throw ex;
            }

            return vProduto;
        } catch (Exception ex) {
            throw ex;
        }
    }

    private Map<Long, ProdutoVO> carregarProdutosCarnauba(String i_arquivo, int idLoja) throws Exception {
        Map<Long, ProdutoVO> vProdutoCarnauba = new HashMap<>();
        Utils util = new Utils();

        try {

            int linha = 0, qtdEmbalagem = 1, idTipoEmbalagem = 0, ncm1, ncm2, ncm3, contI = -1,
                    idAliquotaICMS;
            double idProduto = 0, custoComImposto, custoSemImposto, margem, precoVenda;
            long codigoBarras = 0;
            String codAliquotaICMS = "", descricaoCompleta = "", descricaoReduzida = "", ncm = "",
                    dataCadastro = "", strQtd = "", strNovoQtd = "", strIdProduto = "";

            WorkbookSettings settings = new WorkbookSettings();
            settings.setEncoding("CP1250");

            Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);

            Sheet[] sheets = arquivo.getSheets();

            try {

                for (int sh = 0; sh < sheets.length; sh++) {
                    Sheet sheet = arquivo.getSheet(sh);
                    linha = 0;

                    for (int i = 0; i < sheet.getRows(); i++) {
                        linha++;

                        //ignora o cabeçalho
                        if (linha == 1) {
                            continue;

                        } else if (sheet.getCell(1, i).getContents().isEmpty()) { //ignora linha em branco
                            continue;
                        } else if (/*(!"7".equals(sheet.getCell(4, i).getContents().trim())) &&
                                 (!"8".equals(sheet.getCell(4, i).getContents().trim())) &&
                                 (!"9".equals(sheet.getCell(4, i).getContents().trim())) &&
                                 (!"10".equals(sheet.getCell(4, i).getContents().trim())) &&
                                 (!"14".equals(sheet.getCell(4, i).getContents().trim())) &&
                                 (!"34".equals(sheet.getCell(4, i).getContents().trim()))*/(!"KG".equals(sheet.getCell(6, i).getContents().trim()))) {

                            //if (linha == 6380) {
                            //    JOptionPane.showMessageDialog(null, "aqui");
                            //}
                            if (linha == 10088) {
                                System.out.println("aqui");
                            }

                            Cell cellIdProduto = sheet.getCell(0, i);
                            Cell cellDescricaoCompleta = sheet.getCell(2, i);
                            Cell cellDescricaoReduzida = sheet.getCell(3, i);
                            Cell cellAliquotaICMS = sheet.getCell(5, i);
                            Cell cellNcm = sheet.getCell(11, i);
                            Cell cellCodigoBarras = sheet.getCell(13, i);
                            Cell cellCusto = sheet.getCell(15, i);
                            Cell cellQtdEmbalagem = sheet.getCell(17, i);
                            Cell cellMargem = sheet.getCell(18, i);
                            Cell cellPrecoVenda = sheet.getCell(19, i);
                            Cell cellDataCadastro = sheet.getCell(16, i);
                            Cell cellIdTipoEmbalagem = sheet.getCell(6, i);

                            if (!"NULL".equals(cellDescricaoCompleta.getContents().trim())) {

                                if ((cellIdTipoEmbalagem.getContents() != null)
                                        && (!cellIdTipoEmbalagem.getContents().trim().isEmpty())
                                        && (!"NULL".equals(cellIdTipoEmbalagem.getContents().trim()))) {

                                    if ("CX".equals(cellIdTipoEmbalagem.getContents().trim())) {
                                        idTipoEmbalagem = 1;
                                    } else {
                                        idTipoEmbalagem = 0;
                                    }
                                } else {
                                    idTipoEmbalagem = 0;
                                }

                                if ((cellAliquotaICMS.getContents() != null)
                                        && (!cellAliquotaICMS.getContents().trim().isEmpty())
                                        && (!"NULL".equals(cellAliquotaICMS.getContents().trim()))) {

                                    codAliquotaICMS = cellAliquotaICMS.getContents().trim();

                                    idAliquotaICMS = new IcmsDAO().carregarIcmsCeara(codAliquotaICMS);

                                } else {
                                    idAliquotaICMS = 8;
                                }

                                strIdProduto = util.formataNumero(cellIdProduto.getContents().trim());
                                strIdProduto = strIdProduto.substring(0, strIdProduto.length() - 2);
                                idProduto = Double.parseDouble(strIdProduto);
                                //idProduto = Double.parseDouble(cellIdProduto.getContents().trim().substring(0,
                                //        cellIdProduto.getContents().trim().length() - 3));

                                if ((cellDescricaoCompleta.getContents() != null)
                                        && (!cellDescricaoCompleta.getContents().trim().isEmpty())
                                        && (!"NULL".equals(cellDescricaoCompleta.getContents().trim()))) {
                                    descricaoCompleta = util.acertarTexto(cellDescricaoCompleta.getContents().trim().replace("'", ""));
                                } else {
                                    descricaoCompleta = "";
                                }

                                if ((cellDescricaoReduzida.getContents() != null)
                                        && (!cellDescricaoReduzida.getContents().trim().isEmpty())
                                        && (!"NULL".equals(cellDescricaoReduzida.getContents().trim()))) {
                                    descricaoReduzida = util.acertarTexto(cellDescricaoReduzida.getContents().trim().replace("'", ""));
                                } else {
                                    descricaoReduzida = "";
                                }

                                if ((cellCodigoBarras.getContents() != null)
                                        && (!cellCodigoBarras.getContents().trim().isEmpty())
                                        && (!"NULL".equals(cellCodigoBarras.getContents().trim()))) {

                                    if (util.encontrouLetraCampoNumerico(cellCodigoBarras.getContents())) {
                                        codigoBarras = contI;
                                    } else {

                                        if (cellCodigoBarras.getContents().trim().substring(0,
                                                cellCodigoBarras.getContents().trim().length() - 3).length() < 7) {
                                            codigoBarras = contI;
                                        } else {

                                            if (cellCodigoBarras.getContents().contains(",")) {
                                                codigoBarras = Long.parseLong(cellCodigoBarras.getContents().trim().substring(0,
                                                        cellCodigoBarras.getContents().trim().length() - 3));
                                            } else {

                                                codigoBarras = Long.parseLong(util.formataNumero(cellCodigoBarras.getContents().trim()));
                                            }
                                        }
                                    }
                                } else {
                                    codigoBarras = -1;
                                }

                                if ((cellDataCadastro.getContents() != null)
                                        && (!cellDataCadastro.getContents().trim().isEmpty())
                                        && (cellDataCadastro.getContents().trim().length() >= 10)) {
                                    dataCadastro = cellDataCadastro.getContents().substring(0, 10).trim();
                                    dataCadastro = dataCadastro.replace("-", "/");
                                } else {
                                    dataCadastro = "";
                                }

                                if ((cellCusto.getContents() != null)
                                        && (!cellCusto.getContents().trim().isEmpty())
                                        && (!"NULL".equals(cellCusto.getContents().trim()))) {
                                    custoComImposto = Double.parseDouble(cellCusto.getContents().trim());
                                    custoSemImposto = Double.parseDouble(cellCusto.getContents().trim());
                                } else {
                                    custoComImposto = 0;
                                    custoSemImposto = 0;
                                }

                                if ((cellMargem.getContents() != null)
                                        && (!cellMargem.getContents().trim().isEmpty())
                                        && (!"NULL".equals(cellMargem.getContents().trim()))) {
                                    margem = Double.parseDouble(cellMargem.getContents().trim());
                                } else {
                                    margem = 0;
                                }

                                if ((cellPrecoVenda.getContents() != null)
                                        && (!cellPrecoVenda.getContents().trim().isEmpty())
                                        && (!"NULL".equals(cellPrecoVenda.getContents().trim()))) {
                                    precoVenda = Double.parseDouble(cellPrecoVenda.getContents().trim());
                                } else {
                                    precoVenda = 0;
                                }

                                /*if ((cellQtdEmbalagem.getContents() != null) &&
                                 (!cellQtdEmbalagem.getContents().trim().isEmpty()) &&
                                 (!"NULL".equals(cellQtdEmbalagem.getContents().trim()))) {
                            
                                 strQtd = cellQtdEmbalagem.getContents().trim();
                            
                                 if (strQtd.length() == 9) {
                                 for (int j = 0; j < strQtd.length(); j++) {
                                 if (j == 1) {
                                 strNovoQtd = strNovoQtd + "";
                                 } else {
                                 strNovoQtd = strNovoQtd + strQtd.charAt(j);
                                 }
                                 }
                                
                                 qtdEmbalagem = (int) Double.parseDouble(strNovoQtd);
                                
                                 } else if (strQtd.length() == 10) {
                                 for (int j = 0; j < strQtd.length(); j++) {
                                 if (j == 2) {
                                 strNovoQtd = strNovoQtd + "";
                                 } else {
                                 strNovoQtd = strNovoQtd + strQtd.charAt(j);
                                 }
                                 }
                                
                                 qtdEmbalagem = (int) Double.parseDouble(strNovoQtd);
                                
                                 } else if (strQtd.length() == 11) {
                                 for (int j = 0; j < strQtd.length(); j++) {
                                 if (j == 3) {
                                 strNovoQtd = strNovoQtd + "";
                                 } else {
                                 strNovoQtd = strNovoQtd + strQtd.charAt(j);
                                 }
                                 }
                                
                                 qtdEmbalagem = (int) Double.parseDouble(strNovoQtd);
                                
                                 } else {
                                 qtdEmbalagem = (int) Double.parseDouble(strQtd);
                                 }                            
                            
                                 } else {
                                 qtdEmbalagem = 1;
                                 }*/
                                if ((cellQtdEmbalagem.getContents() != null)
                                        && (!cellQtdEmbalagem.getContents().trim().isEmpty())
                                        && (!"NULL".equals(cellQtdEmbalagem.getContents().trim()))) {

                                    strQtd = cellQtdEmbalagem.getContents().trim().substring(0,
                                            cellQtdEmbalagem.getContents().trim().length() - 4);

                                    strQtd = strQtd.replace(".", "");

                                    qtdEmbalagem = Integer.parseInt(strQtd);
                                } else {
                                    qtdEmbalagem = 1;
                                }

                                if ((cellNcm.getContents() != null)
                                        && (cellNcm.getContents().trim().isEmpty())
                                        && (!"NULL".equals(cellNcm.getContents().trim()))) {
                                    ncm = cellNcm.getContents().isEmpty() ? "" : cellNcm.getContents();
                                } else {
                                    ncm = "";
                                }

                                NcmVO oNcm = null;

                                if (ncm.equals("")) {
                                    oNcm = new NcmDAO().getPadrao();

                                } else {

                                    if (util.encontrouLetraCampoNumerico(ncm)) {
                                        oNcm = new NcmDAO().getPadrao();
                                    } else {
                                        oNcm = new NcmDAO().validar(ncm);
                                    }
                                }

                                ncm1 = oNcm.ncm1;
                                ncm2 = oNcm.ncm2;
                                ncm3 = oNcm.ncm3;

                                if (descricaoCompleta.length() > 60) {
                                    descricaoCompleta = descricaoCompleta.substring(0, 60);
                                }

                                if (descricaoReduzida.length() > 22) {
                                    descricaoReduzida = descricaoReduzida.substring(0, 22);
                                }

                                ProdutoVO oProduto = new ProdutoVO();

                                oProduto.codigoAnterior = idProduto;
                                oProduto.idDouble = idProduto;
                                oProduto.dataCadastro = dataCadastro;
                                oProduto.descricaoCompleta = descricaoCompleta;
                                oProduto.descricaoReduzida = descricaoReduzida;
                                oProduto.descricaoGondola = descricaoCompleta;
                                oProduto.idTipoEmbalagem = idTipoEmbalagem;
                                oProduto.qtdEmbalagem = qtdEmbalagem;
                                oProduto.idTipoPisCofinsDebito = 1;
                                oProduto.idTipoPisCofinsCredito = 13;
                                oProduto.tipoNaturezaReceita = 999;
                                oProduto.margem = margem;
                                oProduto.ncm1 = ncm1;
                                oProduto.ncm2 = ncm2;
                                oProduto.ncm3 = ncm3;
                                oProduto.mercadologico1 = 103;
                                oProduto.mercadologico2 = 1;
                                oProduto.mercadologico3 = 1;
                                oProduto.vendaPdv = true;
                                oProduto.sugestaoPedido = true;
                                oProduto.aceitaMultiplicacaoPdv = true;
                                oProduto.sazonal = false;
                                oProduto.fabricacaoPropria = false;
                                oProduto.consignado = false;
                                oProduto.ddv = 0;
                                oProduto.permiteTroca = true;
                                oProduto.vendaControlada = false;
                                oProduto.vendaPdv = true;
                                oProduto.conferido = true;
                                oProduto.permiteQuebra = true;
                                oProduto.permitePerda = true;
                                oProduto.utilizaTabelaSubstituicaoTributaria = false;
                                oProduto.utilizaValidadeEntrada = false;
                                oProduto.eBalanca = false;

                                ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();

                                oComplemento.idLoja = idLoja;
                                oComplemento.idSituacaoCadastro = SituacaoCadastro.ATIVO.getId();
                                oComplemento.precoVenda = precoVenda;
                                oComplemento.precoDiaSeguinte = precoVenda;
                                oComplemento.custoComImposto = custoComImposto;
                                oComplemento.custoSemImposto = custoSemImposto;

                                ProdutoAliquotaVO oProdutoAliquota = new ProdutoAliquotaVO();

                                oProdutoAliquota.idAliquotaCredito = idAliquotaICMS;
                                oProdutoAliquota.idAliquotaCreditoForaEstado = idAliquotaICMS;
                                oProdutoAliquota.idAliquotaDebito = idAliquotaICMS;
                                oProdutoAliquota.idEstado = Global.idEstado;
                                oProdutoAliquota.idAliquotaDebitoForaEstado = idAliquotaICMS;
                                oProdutoAliquota.idAliquotaDebitoForaEstadoNF = idAliquotaICMS;

                                oProduto.vAliquota.add(oProdutoAliquota);

                                for (ProdutoComplementoVO oProdutoComplemento : oProduto.vComplemento) {
                                    oProdutoComplemento.idAliquotaCredito = idAliquotaICMS;
                                }

                                oProduto.vComplemento.add(oComplemento);

                                ProdutoAutomacaoVO oProdutoAutomacao = new ProdutoAutomacaoVO();

                                oProdutoAutomacao.codigoBarras = codigoBarras;
                                oProdutoAutomacao.idTipoEmbalagem = idTipoEmbalagem;
                                oProdutoAutomacao.qtdEmbalagem = 1;
                                oProdutoAutomacao.precoVenda = precoVenda;

                                oProduto.vAutomacao.add(oProdutoAutomacao);

                                CodigoAnteriorVO oAnterior = new CodigoAnteriorVO();

                                oAnterior.codigoanterior = idProduto;
                                oAnterior.ref_icmsdebito = cellAliquotaICMS.getContents().trim();
                                oAnterior.ncm = cellNcm.getContents().trim();
                                oAnterior.id_loja = idLoja;
                                oAnterior.precovenda = precoVenda;
                                oAnterior.custocomimposto = custoComImposto;
                                oAnterior.custosemimposto = custoSemImposto;
                                oAnterior.e_balanca = false;

                                oProduto.vCodigoAnterior.add(oAnterior);

                                vProdutoCarnauba.put(codigoBarras, oProduto);
                            }
                        }
                    }
                }

                return vProdutoCarnauba;
            } catch (Exception ex) {
                if (linha > 0) {
                    throw new VRException("Linha " + linha + ": " + ex.getMessage());
                } else {
                    throw ex;
                }
            }

        } catch (Exception ex) {
            throw ex;
        }
    }

    private List<MercadologicoVO> carregarChampMercadologico(String i_arquivo, int nivel) throws Exception {

        WorkbookSettings settings = new WorkbookSettings();
        settings.setEncoding("CP1250");

        Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);

        Sheet[] sheets = arquivo.getSheets();
        Sheet sheet = arquivo.getSheet(0);

        Set<String> departamentos = new LinkedHashSet<>();
        for (int i = 1; i < sheet.getRows(); i++) {
            Cell cellDepart = sheet.getCell(7, i);
            departamentos.add(Utils.acertarTexto(cellDepart.getContents(), "SEM MERCADOLOGICO"));
        }

        List<MercadologicoVO> result = new ArrayList<>();
        int cont = 0;
        for (String departamento : departamentos) {
            cont++;
            if (nivel == 1) {
                MercadologicoVO merc = new MercadologicoVO();
                merc.setMercadologico1(cont);
                merc.setDescricao(departamento);
                merc.setNivel(nivel);
                result.add(merc);
            } else if (nivel == 2) {
                MercadologicoVO merc = new MercadologicoVO();
                merc.setMercadologico1(cont);
                merc.setMercadologico2(1);
                merc.setDescricao(departamento);
                merc.setNivel(nivel);
                result.add(merc);
            } else if (nivel == 3) {
                MercadologicoVO merc = new MercadologicoVO();
                merc.setMercadologico1(cont);
                merc.setMercadologico2(1);
                merc.setMercadologico3(1);
                merc.setDescricao(departamento);
                merc.setNivel(nivel);
                result.add(merc);
            }
        }
        return result;
    }

    public Map<Long, ProdutoVO> carregarChampEAN(String i_arquivo, int idLojaVR) throws Exception {
        Map<Long, ProdutoVO> result = new HashMap<>();

        WorkbookSettings settings = new WorkbookSettings();
        settings.setEncoding("CP1250");

        Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);

        Sheet[] sheets = arquivo.getSheets();

        /*for (int sh = 0; sh < sheets.length; sh++)*/ {
            Sheet sheet = arquivo.getSheet(0);
            for (int i = 1; i < sheet.getRows(); i++) {

                //Instancia o produto
                ProdutoVO oProduto = new ProdutoVO();
                CodigoAnteriorVO oCodigoAnterior = new CodigoAnteriorVO();
                //Inclui elas nas listas                
                oProduto.getvCodigoAnterior().add(oCodigoAnterior);

                Cell cellIdProduto = sheet.getCell(0, i);
                Cell cellEAN = sheet.getCell(1, i);
                Cell cellEANs = sheet.getCell(2, i);
                Cell cellTipoEmbalagem = sheet.getCell(6, i);

                Set<Long> eans = new LinkedHashSet<>();
                if (!Utils.formataNumero(cellEAN.getContents()).equals("0")) {
                    String strEan = cellEAN.getContents().trim();
                    if ((strEan.length() >= 7)
                            && (strEan.length() <= 14)) {
                        eans.add(Utils.stringToLong(strEan));
                    }
                }
                if (cellEANs.getContents() != null && !cellEANs.getContents().trim().equals("")) {
                    String[] strEans = cellEANs.getContents().trim().split(";");
                    for (int j = 0; j < strEans.length; j++) {
                        if ((strEans[j].length() >= 7)
                                && (strEans[j].length() <= 14)) {
                            eans.add(Utils.stringToLong(strEans[j]));
                        }
                    }
                }

                oProduto.setId(Utils.stringToInt(cellIdProduto.getContents()));
                boolean isPrimeiro = true;
                for (Long ean : eans) {
                    ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();
                    oAutomacao.setCodigoBarras(ean);
                    oAutomacao.setIdTipoEmbalagem(Utils.converteTipoEmbalagem(cellTipoEmbalagem.getContents()));
                    oAutomacao.setQtdEmbalagem(1);
                    oProduto.getvAutomacao().add(oAutomacao);
                    if (isPrimeiro) {
                        isPrimeiro = false;
                        oCodigoAnterior.setBarras(ean);
                        result.put(ean, oProduto);
                    }
                }
            }
        }

        return result;
    }

    private Map<Long, ProdutoVO> carregarChampProdutos(String i_arquivo, int idLojaVR) throws Exception {
        Map<Long, ProdutoVO> vProduto = new HashMap<>();

        WorkbookSettings settings = new WorkbookSettings();
        settings.setEncoding("CP1250");

        Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);

        Sheet[] sheets = arquivo.getSheets();

        MercadologicoVO aAcertar = MercadologicoDAO.getMaxMercadologico();

        Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().carregarProdutosBalanca();
        /*for (int sh = 0; sh < sheets.length; sh++)*/ {
            Sheet sheet = arquivo.getSheet(0);
            for (int i = 1; i < sheet.getRows(); i++) {

                //Instancia o produto
                ProdutoVO oProduto = new ProdutoVO();
                //Prepara as variáveis
                ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();
                CodigoAnteriorVO oCodigoAnterior = new CodigoAnteriorVO();
                ProdutoAliquotaVO oAliquota = new ProdutoAliquotaVO();
                ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                //Inclui elas nas listas
                oProduto.getvAutomacao().add(oAutomacao);
                oProduto.getvCodigoAnterior().add(oCodigoAnterior);
                oProduto.getvAliquota().add(oAliquota);
                oProduto.getvComplemento().add(oComplemento);

                Cell cellIdProduto = sheet.getCell(0, i);
                Cell cellDescricaoCompleta = sheet.getCell(3, i);
                Cell cellDescricaoReduzida = sheet.getCell(3, i);
                Cell cellIdSituacaocadastro = sheet.getCell(5, i);
                Cell cellDepart = sheet.getCell(7, i);
                Cell cellNcm = sheet.getCell(10, i);
                Cell cellCest = sheet.getCell(11, i);
                Cell cellTipoEmbalagem = sheet.getCell(6, i);
                Cell cellPisCredito = sheet.getCell(13, i);
                Cell cellPisDebito = sheet.getCell(14, i);
                Cell cellNatReceita = sheet.getCell(12, i);
                Cell cellPrecoVenda = sheet.getCell(9, i);
                Cell cellCusto = sheet.getCell(8, i);
                Cell cellIcmsCST = sheet.getCell(19, i);
                Cell cellIcmsAliq = sheet.getCell(20, i);
                Cell cellIcmsRed = sheet.getCell(21, i);

                oProduto.setId(Utils.stringToInt(cellIdProduto.getContents()));
                oProduto.setDescricaoCompleta(cellDescricaoCompleta.getContents());
                oProduto.setDescricaoReduzida(cellDescricaoReduzida.getContents());
                oProduto.setDescricaoGondola(cellDescricaoCompleta.getContents());
                switch (Utils.acertarTexto(cellIdSituacaocadastro.getContents())) {
                    case "ATIVO":
                        oProduto.setIdSituacaoCadastro(1);
                        break;
                    default:
                        oProduto.setIdSituacaoCadastro(0);
                        break;
                }
                oProduto.setDataCadastro(Util.formatDataGUI(new Date(new java.util.Date().getTime())));

                try (Statement stm = Conexao.createStatement()) {
                    try (ResultSet rst = stm.executeQuery(
                            "select\n"
                            + "	ant1,\n"
                            + "	ant2,\n"
                            + "	ant3,\n"
                            + "	ant4,\n"
                            + "	ant5\n"
                            + "from \n"
                            + "	implantacao.codigoanteriormercadologico\n"
                            + "where\n"
                            + "	descricao = " + Utils.quoteSQL(Utils.acertarTexto(cellDepart.getContents())) + " and\n"
                            + "	nivel = 3"
                    )) {
                        if (rst.next()) {
                            oProduto.setMercadologico1(rst.getInt("ant1"));
                            oProduto.setMercadologico2(rst.getInt("ant2"));
                            oProduto.setMercadologico3(rst.getInt("ant3"));
                            oProduto.setMercadologico4(rst.getInt("ant4"));
                            oProduto.setMercadologico5(rst.getInt("ant5"));
                        } else {
                            oProduto.setMercadologico1(aAcertar.getMercadologico1());
                            oProduto.setMercadologico2(aAcertar.getMercadologico2());
                            oProduto.setMercadologico3(aAcertar.getMercadologico3());
                            oProduto.setMercadologico4(aAcertar.getMercadologico4());
                            oProduto.setMercadologico5(aAcertar.getMercadologico5());
                        }
                    }
                }

                if ((cellNcm.getContents() != null)
                        && (!cellNcm.getContents().isEmpty())
                        && (cellNcm.getContents().trim().length() > 5)) {
                    NcmVO oNcm = new NcmDAO().validar(cellNcm.getContents().trim());

                    oProduto.setNcm1(oNcm.ncm1);
                    oProduto.setNcm2(oNcm.ncm2);
                    oProduto.setNcm3(oNcm.ncm3);
                } else {
                    oProduto.setNcm1(9701);
                    oProduto.setNcm2(90);
                    oProduto.setNcm3(0);
                }

                CestVO cest = CestDAO.parse(cellCest.getContents());
                oProduto.setCest1(cest.getCest1());
                oProduto.setCest2(cest.getCest2());
                oProduto.setCest3(cest.getCest3());

                oProduto.setIdFamiliaProduto(-1);
                oProduto.setMargem(0);
                oProduto.setQtdEmbalagem(1);
                oProduto.setIdComprador(1);
                oProduto.setIdFornecedorFabricante(1);

                long codigoBarra;
                if (!produtosBalanca.containsKey((int) oProduto.getId())) {
                    codigoBarra = -1;
                } else {
                    codigoBarra = (long) oProduto.getId();
                }

                /**
                 * Aparentemente o sistema utiliza o próprio id para produtos de
                 * balança.
                 */
                ProdutoBalancaVO produtoBalanca;
                if (codigoBarra <= Integer.MAX_VALUE) {
                    produtoBalanca = produtosBalanca.get((int) codigoBarra);
                } else {
                    produtoBalanca = null;
                }
                if (produtoBalanca != null) {
                    oAutomacao.setCodigoBarras((long) oProduto.getId());
                    oProduto.setValidade(produtoBalanca.getValidade() > 1 ? produtoBalanca.getValidade() : 0);

                    if ("P".equals(produtoBalanca.getPesavel())) {
                        oAutomacao.setIdTipoEmbalagem(4);
                        oProduto.setPesavel(false);
                    } else {
                        oAutomacao.setIdTipoEmbalagem(0);
                        oProduto.setPesavel(true);
                    }

                    oCodigoAnterior.setCodigobalanca(produtoBalanca.getCodigo());
                    oCodigoAnterior.setE_balanca(true);
                } else {
                    oProduto.setValidade(0);
                    oProduto.setPesavel(false);

                    oAutomacao.setCodigoBarras(-2);
                    oAutomacao.setIdTipoEmbalagem(Utils.converteTipoEmbalagem(cellTipoEmbalagem.getContents()));

                    oCodigoAnterior.setCodigobalanca(0);
                    oCodigoAnterior.setE_balanca(false);
                }
                oAutomacao.setQtdEmbalagem(1);

                oProduto.setIdTipoEmbalagem(oAutomacao.getIdTipoEmbalagem());

                oProduto.setSugestaoPedido(true);
                oProduto.setAceitaMultiplicacaoPdv(true);
                oProduto.setSazonal(false);
                oProduto.setFabricacaoPropria(false);
                oProduto.setConsignado(false);
                oProduto.setDdv(0);
                oProduto.setPermiteTroca(true);
                oProduto.setVendaControlada(false);
                oProduto.setVendaPdv(true);
                oProduto.setConferido(true);
                oProduto.setPermiteQuebra(true);
                oProduto.setPesoBruto(0);
                oProduto.setPesoLiquido(0);

                oProduto.setIdTipoPisCofinsDebito(Utils.retornarPisCofinsDebito(Utils.stringToInt(cellPisDebito.getContents())));
                oProduto.setIdTipoPisCofinsCredito(Utils.retornarPisCofinsCredito(Utils.stringToInt(cellPisCredito.getContents())));
                oProduto.setTipoNaturezaReceita(Utils.retornarTipoNaturezaReceita(oProduto.idTipoPisCofinsDebito, cellNatReceita.getContents()));

                oComplemento.setPrecoVenda(Utils.stringToDouble(cellPrecoVenda.getContents()));
                oComplemento.setPrecoDiaSeguinte(Utils.stringToDouble(cellPrecoVenda.getContents()));
                oComplemento.setCustoComImposto(Utils.stringToDouble(cellCusto.getContents()));
                oComplemento.setCustoSemImposto(Utils.stringToDouble(cellCusto.getContents()));
                oComplemento.setIdLoja(idLojaVR);
                oComplemento.setIdSituacaoCadastro(oProduto.getIdSituacaoCadastro());
                oComplemento.setEstoque(0);
                oComplemento.setEstoqueMinimo(0);
                oComplemento.setEstoqueMaximo(0);

                oAliquota.setIdEstado(35);
                if (oAliquota.getIdEstado() == 0) {
                    oAliquota.setIdEstado(35);
                }
                double icmsAliq = Utils.stringToDouble(cellIcmsAliq.getContents()) / 1000;
                double icmsRed = Utils.stringToDouble(cellIcmsRed.getContents()) / 1000;
                int cst = Utils.stringToInt(cellIcmsCST.getContents());

                oAliquota.setIdAliquotaDebito(Utils.getAliquotaICMS("SP", cst, icmsAliq, icmsRed));
                oAliquota.setIdAliquotaCredito(Utils.getAliquotaICMS("SP", cst, icmsAliq, icmsRed));
                oAliquota.setIdAliquotaDebitoForaEstado(Utils.getAliquotaICMS("SP", cst, icmsAliq, icmsRed));
                oAliquota.setIdAliquotaCreditoForaEstado(Utils.getAliquotaICMS("SP", cst, icmsAliq, icmsRed));
                oAliquota.setIdAliquotaDebitoForaEstadoNF(Utils.getAliquotaICMS("SP", cst, icmsAliq, icmsRed));

                oCodigoAnterior.setCodigoanterior(oProduto.getId());
                oCodigoAnterior.setMargem(oProduto.getMargem());
                oCodigoAnterior.setPrecovenda(oComplemento.getPrecoVenda());
                oCodigoAnterior.setBarras(-2);
                oCodigoAnterior.setReferencia((int) oProduto.getId());
                oCodigoAnterior.setNcm(cellNcm.getContents());
                oCodigoAnterior.setId_loja(idLojaVR);
                oCodigoAnterior.setPiscofinsdebito(Utils.stringToInt(cellPisDebito.getContents()));
                oCodigoAnterior.setPiscofinscredito(Utils.stringToInt(cellPisCredito.getContents()));
                oCodigoAnterior.setNaturezareceita(Utils.stringToInt(cellNatReceita.getContents()));
                oCodigoAnterior.setRef_icmsdebito(cellIcmsCST.getContents());

                //Encerramento produto
                if (oProduto.getMargem() == 0) {
                    oProduto.recalcularMargem();
                }

                vProduto.put((long) oProduto.getId(), oProduto);

            }
        }

        return vProduto;
    }

    private List<ClientePreferencialVO> carregarChampCliente(String i_arquivo) throws Exception {

        List<ClientePreferencialVO> vClientePreferencial = new ArrayList<>();

        WorkbookSettings settings = new WorkbookSettings();
        settings.setEncoding("CP1250");

        Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);

        Sheet[] sheets = arquivo.getSheets();

        Sheet sheet = arquivo.getSheet(0);
        Utils utils = new Utils();
        for (int i = 1; i < sheet.getRows(); i++) {

            Cell cellId = sheet.getCell(0, i);
            Cell cellNome = sheet.getCell(3, i);
            Cell cellFantasia = sheet.getCell(10, i);
            Cell cellTelefone = sheet.getCell(8, i);
            Cell cellTelefone2 = sheet.getCell(9, i);
            Cell cellCnpjCpf = sheet.getCell(4, i);
            Cell cellCidade = sheet.getCell(5, i);
            Cell cellUF = sheet.getCell(6, i);
            Cell cellEmail = sheet.getCell(11, i);
            Cell cellDataNasc = sheet.getCell(12, i);
            Cell cellBloqueado = sheet.getCell(2, i);

            ClientePreferencialVO oClientePreferencial = new ClientePreferencialVO();
            oClientePreferencial.setId(Utils.stringToInt(cellId.getContents()));
            oClientePreferencial.setCodigoanterior(Utils.stringToInt(cellId.getContents()));
            oClientePreferencial.setNome(Utils.acertarTexto(cellNome.getContents()));
            //oClientePreferencial.setEndereco(rst.getString("res_endereco"));
            //oClientePreferencial.setNumero(rst.getString("res_numero"));
            //oClientePreferencial.setComplemento(rst.getString("res_complemento"));
            //oClientePreferencial.setBairro(rst.getString("res_bairro"));
            oClientePreferencial.setId_estado(Utils.getEstadoPelaSigla(cellUF.getContents()));
            if (oClientePreferencial.getId_estado() == 0) {
                oClientePreferencial.setId_estado(35);
            }
            oClientePreferencial.setId_municipio(utils.retornarMunicipioIBGEDescricao(Utils.acertarTexto(cellCidade.getContents()), Utils.acertarTexto(cellUF.getContents())));
            if (oClientePreferencial.getId_municipio() == 0) {
                oClientePreferencial.setId_municipio(3504107);
            }
            //oClientePreferencial.setCep(rst.getInt("res_cep"));
            oClientePreferencial.setTelefone(cellTelefone.getContents());
            //oClientePreferencial.setInscricaoestadual(rst.getString("inscricaoestadual"));
            oClientePreferencial.setCnpj(cellCnpjCpf.getContents());
            //oClientePreferencial.setSexo(rst.getInt("sexo"));
            oClientePreferencial.setDataresidencia("1990/01/01");
            oClientePreferencial.setDatacadastro(Utils.formatDate(new java.util.Date()));
            oClientePreferencial.setEmail(cellEmail.getContents());

            oClientePreferencial.setDatanascimento(Utils.formatDate(new SimpleDateFormat("d/M/yyyy").parse(cellDataNasc.getContents())));
            //oClientePreferencial.setValorlimite(rst.getDouble("limite"));
            //oClientePreferencial.setFax(rst.getString("fax"));
            int idSituacaoCadastro = 1;
            boolean bloqueado = false;
            String status = Utils.acertarTexto(cellBloqueado.getContents());
            if (status.contains("BLOQ")) {
                idSituacaoCadastro = 0;
                bloqueado = true;
            } else if (!status.equals("")) {
                idSituacaoCadastro = 0;
            }

            oClientePreferencial.setBloqueado(bloqueado);
            oClientePreferencial.setId_situacaocadastro(idSituacaoCadastro);
            //oClientePreferencial.setTelefone2(rst.getString("telefone2"));
            oClientePreferencial.setObservacao("IMPORTADO VR: " + Utils.acertarTexto(cellFantasia.getContents()));
            //oClientePreferencial.setNomepai(rst.getString("nomePai"));
            //oClientePreferencial.setNomemae(rst.getString("nomeMae"));
            //oClientePreferencial.setEmpresa(rst.getString("empresa"));
            //oClientePreferencial.setTelefoneempresa(rst.getString("telEmpresa"));
            //oClientePreferencial.setCargo(rst.getString("cargo"));
            //oClientePreferencial.setEnderecoempresa(rst.getString("enderecoEmpresa"));
            oClientePreferencial.setId_tipoinscricao(String.valueOf(oClientePreferencial.getCnpj()).length() > 11 ? 0 : 1);
            //oClientePreferencial.setSalario(rst.getDouble("salario"));
            //oClientePreferencial.setId_tipoestadocivil(rst.getInt("estadoCivil"));
            //oClientePreferencial.setNomeconjuge(rst.getString("conjuge"));
            //oClientePreferencial.setOrgaoemissor(rst.getString("orgaoExp"));                  

            if (oClientePreferencial.getCnpj() > 0) {
                vClientePreferencial.add(oClientePreferencial);
            }
        }

        return vClientePreferencial;
    }

    private List<ProdutoVO> carregarDataProdutosCarnauba(String i_arquivo, int idLoja) throws Exception {
        List<ProdutoVO> vProdutoCarnauba = new ArrayList<>();
        Utils util = new Utils();

        try {

            int linha = 0;
            double idProduto = 0;

            String dataCadastro = "", strIdProduto = "";

            WorkbookSettings settings = new WorkbookSettings();
            settings.setEncoding("CP1250");

            Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);

            Sheet[] sheets = arquivo.getSheets();

            try {

                for (int sh = 0; sh < sheets.length; sh++) {
                    Sheet sheet = arquivo.getSheet(sh);
                    linha = 0;

                    for (int i = 0; i < sheet.getRows(); i++) {
                        linha++;

                        //ignora o cabeçalho
                        if (linha == 1) {
                            continue;

                        } else if (sheet.getCell(1, i).getContents().isEmpty()) { //ignora linha em branco
                            continue;
                        } else if (/*(!"7".equals(sheet.getCell(4, i).getContents().trim())) &&
                                 (!"8".equals(sheet.getCell(4, i).getContents().trim())) &&
                                 (!"9".equals(sheet.getCell(4, i).getContents().trim())) &&
                                 (!"10".equals(sheet.getCell(4, i).getContents().trim())) &&
                                 (!"14".equals(sheet.getCell(4, i).getContents().trim())) &&
                                 (!"34".equals(sheet.getCell(4, i).getContents().trim()))*/(!"KG".equals(sheet.getCell(6, i).getContents().trim()))) {

                            //if (linha == 6380) {
                            //    JOptionPane.showMessageDialog(null, "aqui");
                            //}
                            if (linha == 10088) {
                                System.out.println("aqui");
                            }

                            Cell cellIdProduto = sheet.getCell(0, i);
                            Cell cellDescricaoCompleta = sheet.getCell(2, i);
                            Cell cellDataCadastro = sheet.getCell(16, i);

                            if (!"NULL".equals(cellDescricaoCompleta.getContents().trim())) {

                                strIdProduto = util.formataNumero(cellIdProduto.getContents().trim());
                                strIdProduto = strIdProduto.substring(0, strIdProduto.length() - 2);
                                idProduto = Double.parseDouble(strIdProduto);

                                if ((cellDataCadastro.getContents() != null)
                                        && (!cellDataCadastro.getContents().trim().isEmpty())
                                        && (cellDataCadastro.getContents().trim().length() >= 10)) {
                                    dataCadastro = cellDataCadastro.getContents().substring(0, 10).trim();
                                    dataCadastro = dataCadastro.replace("-", "/");
                                } else {
                                    dataCadastro = "";
                                }

                                ProdutoVO oProduto = new ProdutoVO();

                                oProduto.codigoAnterior = idProduto;
                                oProduto.idDouble = idProduto;
                                oProduto.dataCadastro = dataCadastro;

                                vProdutoCarnauba.add(oProduto);
                            }
                        }
                    }
                }

                return vProdutoCarnauba;
            } catch (Exception ex) {
                if (linha > 0) {
                    throw new VRException("Linha " + linha + ": " + ex.getMessage());
                } else {
                    throw ex;
                }
            }

        } catch (Exception ex) {
            throw ex;
        }
    }

    private List<ProdutoVO> carregarCodigoBarras(String i_arquivo) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        try {
            int linha = 0;
            double idProduto = 0;
            long codigoBarras = -2;
            WorkbookSettings settings = new WorkbookSettings();
            settings.setEncoding("CP1250");
            Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);
            Sheet[] sheets = arquivo.getSheets();

            try {
                for (int sh = 0; sh < sheets.length; sh++) {
                    Sheet sheet = arquivo.getSheet(sh);
                    linha = 0;
                    for (int i = 0; i < sheet.getRows(); i++) {
                        linha++;
                        //ignora o cabeçalho
                        if (sheet.getCell(1, i).getContents().isEmpty()) { //ignora linha em branco
                            continue;
                        } else if (sheet.getCell(0, i).getContents().isEmpty()) {
                            continue;
                        } else {
                            Cell cellIdProduto = sheet.getCell(0, i);
                            Cell cellCodigoBarras = sheet.getCell(1, i);

                            if (cellCodigoBarras.getContents().trim().length() >= 7) {
                                idProduto = Double.parseDouble(cellIdProduto.getContents().trim());
                                codigoBarras = Long.parseLong(Utils.formataNumero(cellCodigoBarras.getContents().trim()));

                                ProdutoVO oProduto = new ProdutoVO();
                                ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();
                                oProduto.setIdDouble(idProduto);
                                oAutomacao.setCodigoBarras(codigoBarras);
                                oAutomacao.setQtdEmbalagem(1);
                                oProduto.vAutomacao.add(oAutomacao);
                                vProduto.add(oProduto);
                            }
                        }
                    }
                }

                return vProduto;
            } catch (Exception ex) {
                if (linha > 0) {
                    throw new VRException("Linha " + linha + ": " + ex.getMessage());
                } else {
                    throw ex;
                }
            }

        } catch (Exception ex) {
            throw ex;
        }
    }

    public void importarCodigoBarras(String i_arquivo) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...CodigoBarras...");
            vProduto = carregarCodigoBarras(i_arquivo);
            if (!vProduto.isEmpty()) {
                new ProdutoDAO().addCodigoBarras(vProduto);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    private List<ProdutoVO> carregarPrecoMargemCustoCarnauba(String i_arquivo, int idLoja) throws Exception {
        List<ProdutoVO> vProdutoCarnauba = new ArrayList<>();
        Utils util = new Utils();

        try {

            int linha = 0, qtdEmbalagem = 1, idTipoEmbalagem = 0, ncm1, ncm2, ncm3, contI = -1,
                    idAliquotaICMS;
            double idProduto = 0, custoComImposto, custoSemImposto, margem, precoVenda;
            long codigoBarras = 0;
            String codAliquotaICMS = "", descricaoCompleta = "", descricaoReduzida = "", ncm = "",
                    dataCadastro = "", strQtd = "", strNovoQtd = "", strIdProduto = "";

            WorkbookSettings settings = new WorkbookSettings();
            settings.setEncoding("CP1250");

            Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);

            Sheet[] sheets = arquivo.getSheets();

            try {

                for (int sh = 0; sh < sheets.length; sh++) {
                    Sheet sheet = arquivo.getSheet(sh);
                    linha = 0;

                    for (int i = 0; i < sheet.getRows(); i++) {
                        linha++;

                        //ignora o cabeçalho
                        if (linha == 1) {
                            continue;

                        } else if (sheet.getCell(1, i).getContents().isEmpty()) { //ignora linha em branco
                            continue;
                        } else if (/*(!"7".equals(sheet.getCell(4, i).getContents().trim())) &&
                                 (!"8".equals(sheet.getCell(4, i).getContents().trim())) &&
                                 (!"9".equals(sheet.getCell(4, i).getContents().trim())) &&
                                 (!"10".equals(sheet.getCell(4, i).getContents().trim())) &&
                                 (!"14".equals(sheet.getCell(4, i).getContents().trim())) &&
                                 (!"34".equals(sheet.getCell(4, i).getContents().trim()))*/(!"KG".equals(sheet.getCell(6, i).getContents().trim()))) {

                            //if (linha == 6380) {
                            //    JOptionPane.showMessageDialog(null, "aqui");
                            //}
                            if (linha == 10088) {
                                System.out.println("aqui");
                            }

                            Cell cellIdProduto = sheet.getCell(0, i);
                            Cell cellDescricaoCompleta = sheet.getCell(2, i);
                            Cell cellDescricaoReduzida = sheet.getCell(3, i);
                            Cell cellAliquotaICMS = sheet.getCell(5, i);
                            Cell cellNcm = sheet.getCell(11, i);
                            Cell cellCodigoBarras = sheet.getCell(13, i);
                            Cell cellCusto = sheet.getCell(15, i);
                            Cell cellQtdEmbalagem = sheet.getCell(17, i);
                            Cell cellMargem = sheet.getCell(18, i);
                            Cell cellPrecoVenda = sheet.getCell(19, i);
                            Cell cellDataCadastro = sheet.getCell(16, i);
                            Cell cellIdTipoEmbalagem = sheet.getCell(6, i);

                            if (!"NULL".equals(cellDescricaoCompleta.getContents().trim())) {

                                if ((cellIdTipoEmbalagem.getContents() != null)
                                        && (!cellIdTipoEmbalagem.getContents().trim().isEmpty())
                                        && (!"NULL".equals(cellIdTipoEmbalagem.getContents().trim()))) {

                                    if ("CX".equals(cellIdTipoEmbalagem.getContents().trim())) {
                                        idTipoEmbalagem = 1;
                                    } else {
                                        idTipoEmbalagem = 0;
                                    }
                                } else {
                                    idTipoEmbalagem = 0;
                                }

                                if ((cellAliquotaICMS.getContents() != null)
                                        && (!cellAliquotaICMS.getContents().trim().isEmpty())
                                        && (!"NULL".equals(cellAliquotaICMS.getContents().trim()))) {

                                    codAliquotaICMS = cellAliquotaICMS.getContents().trim();

                                    idAliquotaICMS = new IcmsDAO().carregarIcmsCeara(codAliquotaICMS);

                                } else {
                                    idAliquotaICMS = 8;
                                }

                                strIdProduto = util.formataNumero(cellIdProduto.getContents().trim());
                                strIdProduto = strIdProduto.substring(0, strIdProduto.length() - 2);
                                idProduto = Double.parseDouble(strIdProduto);
                                //idProduto = Double.parseDouble(cellIdProduto.getContents().trim().substring(0,
                                //        cellIdProduto.getContents().trim().length() - 3));

                                if ((cellDescricaoCompleta.getContents() != null)
                                        && (!cellDescricaoCompleta.getContents().trim().isEmpty())
                                        && (!"NULL".equals(cellDescricaoCompleta.getContents().trim()))) {
                                    descricaoCompleta = util.acertarTexto(cellDescricaoCompleta.getContents().trim().replace("'", ""));
                                } else {
                                    descricaoCompleta = "";
                                }

                                if ((cellDescricaoReduzida.getContents() != null)
                                        && (!cellDescricaoReduzida.getContents().trim().isEmpty())
                                        && (!"NULL".equals(cellDescricaoReduzida.getContents().trim()))) {
                                    descricaoReduzida = util.acertarTexto(cellDescricaoReduzida.getContents().trim().replace("'", ""));
                                } else {
                                    descricaoReduzida = "";
                                }

                                if ((cellCodigoBarras.getContents() != null)
                                        && (!cellCodigoBarras.getContents().trim().isEmpty())
                                        && (!"NULL".equals(cellCodigoBarras.getContents().trim()))) {

                                    if (util.encontrouLetraCampoNumerico(cellCodigoBarras.getContents())) {
                                        codigoBarras = -1;
                                    } else {

                                        if (cellCodigoBarras.getContents().trim().substring(0,
                                                cellCodigoBarras.getContents().trim().length() - 3).length() < 7) {
                                            codigoBarras = -1;
                                        } else {

                                            if (cellCodigoBarras.getContents().contains(",")) {
                                                codigoBarras = Long.parseLong(cellCodigoBarras.getContents().trim().substring(0,
                                                        cellCodigoBarras.getContents().trim().length() - 3));
                                            } else {

                                                codigoBarras = Long.parseLong(util.formataNumero(cellCodigoBarras.getContents().trim()));
                                            }
                                        }
                                    }
                                } else {
                                    codigoBarras = -1;
                                }

                                if ((cellDataCadastro.getContents() != null)
                                        && (!cellDataCadastro.getContents().trim().isEmpty())
                                        && (cellDataCadastro.getContents().trim().length() >= 10)) {
                                    dataCadastro = cellDataCadastro.getContents().substring(0, 10).trim();
                                    dataCadastro = dataCadastro.replace("-", "/");
                                } else {
                                    dataCadastro = "";
                                }

                                if ((cellCusto.getContents() != null)
                                        && (!cellCusto.getContents().trim().isEmpty())
                                        && (!"NULL".equals(cellCusto.getContents().trim()))) {
                                    custoComImposto = Double.parseDouble(cellCusto.getContents().trim());
                                    custoSemImposto = Double.parseDouble(cellCusto.getContents().trim());
                                } else {
                                    custoComImposto = 0;
                                    custoSemImposto = 0;
                                }

                                if ((cellMargem.getContents() != null)
                                        && (!cellMargem.getContents().trim().isEmpty())
                                        && (!"NULL".equals(cellMargem.getContents().trim()))) {
                                    margem = Double.parseDouble(cellMargem.getContents().trim());
                                } else {
                                    margem = 0;
                                }

                                if ((cellPrecoVenda.getContents() != null)
                                        && (!cellPrecoVenda.getContents().trim().isEmpty())
                                        && (!"NULL".equals(cellPrecoVenda.getContents().trim()))) {
                                    precoVenda = Double.parseDouble(cellPrecoVenda.getContents().trim());
                                } else {
                                    precoVenda = 0;
                                }

                                /*if ((cellQtdEmbalagem.getContents() != null) &&
                                 (!cellQtdEmbalagem.getContents().trim().isEmpty()) &&
                                 (!"NULL".equals(cellQtdEmbalagem.getContents().trim()))) {
                            
                                 strQtd = cellQtdEmbalagem.getContents().trim();
                            
                                 if (strQtd.length() == 9) {
                                 for (int j = 0; j < strQtd.length(); j++) {
                                 if (j == 1) {
                                 strNovoQtd = strNovoQtd + "";
                                 } else {
                                 strNovoQtd = strNovoQtd + strQtd.charAt(j);
                                 }
                                 }
                                
                                 qtdEmbalagem = (int) Double.parseDouble(strNovoQtd);
                                
                                 } else if (strQtd.length() == 10) {
                                 for (int j = 0; j < strQtd.length(); j++) {
                                 if (j == 2) {
                                 strNovoQtd = strNovoQtd + "";
                                 } else {
                                 strNovoQtd = strNovoQtd + strQtd.charAt(j);
                                 }
                                 }
                                
                                 qtdEmbalagem = (int) Double.parseDouble(strNovoQtd);
                                
                                 } else if (strQtd.length() == 11) {
                                 for (int j = 0; j < strQtd.length(); j++) {
                                 if (j == 3) {
                                 strNovoQtd = strNovoQtd + "";
                                 } else {
                                 strNovoQtd = strNovoQtd + strQtd.charAt(j);
                                 }
                                 }
                                
                                 qtdEmbalagem = (int) Double.parseDouble(strNovoQtd);
                                
                                 } else {
                                 qtdEmbalagem = (int) Double.parseDouble(strQtd);
                                 }                            
                            
                                 } else {
                                 qtdEmbalagem = 1;
                                 }*/
                                if ((cellQtdEmbalagem.getContents() != null)
                                        && (!cellQtdEmbalagem.getContents().trim().isEmpty())
                                        && (!"NULL".equals(cellQtdEmbalagem.getContents().trim()))) {

                                    strQtd = cellQtdEmbalagem.getContents().trim().substring(0,
                                            cellQtdEmbalagem.getContents().trim().length() - 4);

                                    strQtd = strQtd.replace(".", "");

                                    qtdEmbalagem = Integer.parseInt(strQtd);
                                } else {
                                    qtdEmbalagem = 1;
                                }

                                if ((cellNcm.getContents() != null)
                                        && (cellNcm.getContents().trim().isEmpty())
                                        && (!"NULL".equals(cellNcm.getContents().trim()))) {
                                    ncm = cellNcm.getContents().isEmpty() ? "" : cellNcm.getContents();
                                } else {
                                    ncm = "";
                                }

                                NcmVO oNcm = null;

                                if (ncm.equals("")) {
                                    oNcm = new NcmDAO().getPadrao();

                                } else {

                                    if (util.encontrouLetraCampoNumerico(ncm)) {
                                        oNcm = new NcmDAO().getPadrao();
                                    } else {
                                        oNcm = new NcmDAO().validar(ncm);
                                    }
                                }

                                ncm1 = oNcm.ncm1;
                                ncm2 = oNcm.ncm2;
                                ncm3 = oNcm.ncm3;

                                if (descricaoCompleta.length() > 60) {
                                    descricaoCompleta = descricaoCompleta.substring(0, 60);
                                }

                                if (descricaoReduzida.length() > 22) {
                                    descricaoReduzida = descricaoReduzida.substring(0, 22);
                                }

                                ProdutoVO oProduto = new ProdutoVO();

                                oProduto.codigoAnterior = idProduto;
                                oProduto.codigoBarras = codigoBarras;
                                oProduto.idDouble = idProduto;
                                oProduto.dataCadastro = dataCadastro;
                                oProduto.descricaoCompleta = descricaoCompleta;
                                oProduto.descricaoReduzida = descricaoReduzida;
                                oProduto.descricaoGondola = descricaoCompleta;
                                oProduto.idTipoEmbalagem = idTipoEmbalagem;
                                oProduto.qtdEmbalagem = qtdEmbalagem;
                                oProduto.idTipoPisCofinsDebito = 1;
                                oProduto.idTipoPisCofinsCredito = 13;
                                oProduto.tipoNaturezaReceita = 999;
                                oProduto.margem = margem;
                                oProduto.ncm1 = ncm1;
                                oProduto.ncm2 = ncm2;
                                oProduto.ncm3 = ncm3;
                                oProduto.mercadologico1 = 103;
                                oProduto.mercadologico2 = 1;
                                oProduto.mercadologico3 = 1;
                                oProduto.vendaPdv = true;
                                oProduto.sugestaoPedido = true;
                                oProduto.aceitaMultiplicacaoPdv = true;
                                oProduto.sazonal = false;
                                oProduto.fabricacaoPropria = false;
                                oProduto.consignado = false;
                                oProduto.ddv = 0;
                                oProduto.permiteTroca = true;
                                oProduto.vendaControlada = false;
                                oProduto.vendaPdv = true;
                                oProduto.conferido = true;
                                oProduto.permiteQuebra = true;
                                oProduto.permitePerda = true;
                                oProduto.utilizaTabelaSubstituicaoTributaria = false;
                                oProduto.utilizaValidadeEntrada = false;
                                oProduto.eBalanca = false;

                                ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();

                                oComplemento.idLoja = idLoja;
                                oComplemento.idSituacaoCadastro = SituacaoCadastro.ATIVO.getId();
                                oComplemento.precoVenda = precoVenda;
                                oComplemento.precoDiaSeguinte = precoVenda;
                                oComplemento.custoComImposto = custoComImposto;
                                oComplemento.custoSemImposto = custoSemImposto;

                                ProdutoAliquotaVO oProdutoAliquota = new ProdutoAliquotaVO();

                                oProdutoAliquota.idAliquotaCredito = idAliquotaICMS;
                                oProdutoAliquota.idAliquotaCreditoForaEstado = idAliquotaICMS;
                                oProdutoAliquota.idAliquotaDebito = idAliquotaICMS;
                                oProdutoAliquota.idEstado = Global.idEstado;
                                oProdutoAliquota.idAliquotaDebitoForaEstado = idAliquotaICMS;
                                oProdutoAliquota.idAliquotaDebitoForaEstadoNF = idAliquotaICMS;

                                oProduto.vAliquota.add(oProdutoAliquota);

                                for (ProdutoComplementoVO oProdutoComplemento : oProduto.vComplemento) {
                                    oProdutoComplemento.idAliquotaCredito = idAliquotaICMS;
                                }

                                oProduto.vComplemento.add(oComplemento);

                                ProdutoAutomacaoVO oProdutoAutomacao = new ProdutoAutomacaoVO();

                                oProdutoAutomacao.codigoBarras = codigoBarras;
                                oProdutoAutomacao.idTipoEmbalagem = idTipoEmbalagem;
                                oProdutoAutomacao.qtdEmbalagem = 1;
                                oProdutoAutomacao.precoVenda = precoVenda;

                                oProduto.vAutomacao.add(oProdutoAutomacao);

                                CodigoAnteriorVO oAnterior = new CodigoAnteriorVO();

                                oAnterior.codigoanterior = idProduto;
                                oAnterior.ref_icmsdebito = cellAliquotaICMS.getContents().trim();
                                oAnterior.ncm = cellNcm.getContents().trim();
                                oAnterior.id_loja = idLoja;
                                oAnterior.precovenda = precoVenda;
                                oAnterior.custocomimposto = custoComImposto;
                                oAnterior.custosemimposto = custoSemImposto;
                                oAnterior.e_balanca = false;

                                oProduto.vCodigoAnterior.add(oAnterior);

                                vProdutoCarnauba.add(oProduto);
                            }
                        }
                    }
                }

                return vProdutoCarnauba;
            } catch (Exception ex) {
                if (linha > 0) {
                    throw new VRException("Linha " + linha + ": " + ex.getMessage());
                } else {
                    throw ex;
                }
            }

        } catch (Exception ex) {
            throw ex;
        }
    }

    private Map<Integer, ProdutoVO> carregarProdutosReceitaFreitas(String i_arquivo, int idLoja) throws Exception {
        Map<Integer, ProdutoVO> vProdutoCarnauba = new HashMap<>();
        Utils util = new Utils();
        try {

            int linha = 0, ncm1, ncm2, ncm3, contI = -1, idAliquotaICMS = 0, idProduto = 0;
            long codigoBarras = 0;
            String descricaoCompleta = "", descricaoReduzida = "";

            WorkbookSettings settings = new WorkbookSettings();
            settings.setEncoding("CP1250");
            Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);

            Sheet[] sheets = arquivo.getSheets();

            try {

                for (int sh = 0; sh < sheets.length; sh++) {
                    Sheet sheet = arquivo.getSheet(sh);
                    linha = 0;

                    for (int i = 0; i < sheet.getRows(); i++) {
                        linha++;

                        //ignora o cabeçalho
                        if (linha == 1) {
                            continue;
                        } else {

                            Cell cellIdProduto = sheet.getCell(0, i);
                            Cell cellDescricaoCompleta = sheet.getCell(1, i);
                            Cell cellDescricaoReduzida = sheet.getCell(1, i);
                            Cell cellCodigoBarras = sheet.getCell(2, i);

                            idProduto = Integer.parseInt(cellIdProduto.getContents().trim());
                            descricaoCompleta = util.acertarTexto(cellDescricaoCompleta.getContents().trim());
                            descricaoReduzida = util.acertarTexto(cellDescricaoReduzida.getContents().trim());
                            codigoBarras = Long.parseLong(cellCodigoBarras.getContents().trim());
                            ncm1 = 402;
                            ncm2 = 99;
                            ncm3 = 0;

                            if (descricaoCompleta.length() > 60) {
                                descricaoCompleta = descricaoCompleta.substring(0, 60);
                            }

                            if (descricaoReduzida.length() > 22) {
                                descricaoReduzida = descricaoReduzida.substring(0, 22);
                            }
                            idAliquotaICMS = 7;
                            ProdutoVO oProduto = new ProdutoVO();

                            oProduto.id = idProduto;
                            oProduto.codigoAnterior = idProduto;
                            oProduto.idDouble = idProduto;
                            oProduto.descricaoCompleta = descricaoCompleta;
                            oProduto.descricaoReduzida = descricaoReduzida;
                            oProduto.descricaoGondola = descricaoCompleta;
                            oProduto.idTipoPisCofinsDebito = 0;
                            oProduto.idTipoPisCofinsCredito = 12;
                            oProduto.tipoNaturezaReceita = -1;
                            oProduto.ncm1 = ncm1;
                            oProduto.ncm2 = ncm2;
                            oProduto.ncm3 = ncm3;
                            oProduto.tipoProduto = "1";
                            oProduto.mercadologico1 = 15;
                            oProduto.mercadologico2 = 0;
                            oProduto.mercadologico3 = 0;
                            oProduto.vendaPdv = true;
                            oProduto.sugestaoPedido = true;
                            oProduto.aceitaMultiplicacaoPdv = true;
                            oProduto.sazonal = false;
                            oProduto.fabricacaoPropria = false;
                            oProduto.consignado = false;
                            oProduto.ddv = 0;
                            oProduto.permiteTroca = true;
                            oProduto.vendaControlada = false;
                            oProduto.vendaPdv = true;
                            oProduto.conferido = true;
                            oProduto.permiteQuebra = true;
                            oProduto.permitePerda = true;
                            oProduto.utilizaTabelaSubstituicaoTributaria = false;
                            oProduto.utilizaValidadeEntrada = false;
                            oProduto.eBalanca = false;

                            ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();

                            oComplemento.idLoja = idLoja;
                            oComplemento.idSituacaoCadastro = SituacaoCadastro.ATIVO.getId();

                            ProdutoAliquotaVO oProdutoAliquota = new ProdutoAliquotaVO();

                            oProdutoAliquota.idAliquotaCredito = idAliquotaICMS;
                            oProdutoAliquota.idAliquotaCreditoForaEstado = idAliquotaICMS;
                            oProdutoAliquota.idAliquotaDebito = idAliquotaICMS;
                            oProdutoAliquota.idEstado = Global.idEstado;
                            oProdutoAliquota.idAliquotaDebitoForaEstado = idAliquotaICMS;
                            oProdutoAliquota.idAliquotaDebitoForaEstadoNF = idAliquotaICMS;

                            oProduto.vAliquota.add(oProdutoAliquota);

                            for (ProdutoComplementoVO oProdutoComplemento : oProduto.vComplemento) {
                                oProdutoComplemento.idAliquotaCredito = idAliquotaICMS;
                            }

                            oProduto.vComplemento.add(oComplemento);

                            ProdutoAutomacaoVO oProdutoAutomacao = new ProdutoAutomacaoVO();

                            oProdutoAutomacao.codigoBarras = codigoBarras;

                            oProdutoAutomacao.qtdEmbalagem = 1;

                            oProduto.vAutomacao.add(oProdutoAutomacao);

                            CodigoAnteriorVO oAnterior = new CodigoAnteriorVO();

                            oAnterior.codigoanterior = idProduto;
                            oAnterior.id_loja = idLoja;
                            oAnterior.e_balanca = false;

                            oProduto.vCodigoAnterior.add(oAnterior);

                            ProdutosUnificacaoVO oProdutoUnificado = new ProdutosUnificacaoVO();

                            oProdutoUnificado.codigoanterior = idProduto;
                            oProdutoUnificado.codigoatual = idProduto;
                            oProdutoUnificado.barras = codigoBarras;
                            oProdutoUnificado.descricao = descricaoCompleta;

                            oProduto.vProdutosUnificacao.add(oProdutoUnificado);

                            vProdutoCarnauba.put(idProduto, oProduto);
                        }

                        contI = contI - 1;
                    }
                }

                return vProdutoCarnauba;
            } catch (Exception ex) {
                if (linha > 0) {
                    throw new VRException("Linha " + linha + ": " + ex.getMessage());
                } else {
                    throw ex;
                }
            }

        } catch (Exception ex) {
            throw ex;
        }
    }

    private List<ProdutoVO> carregarEstoqueProdutosCarnauba(String i_arquivo) throws Exception {
        List<ProdutoVO> vProdutoEstoque = new ArrayList<>();
        long codigoBarras = 0;
        double idProduto, estoque = 0;
        int linha = 0;
        String strEstoque = "", strNovoEstoque = "", descricao = "";
        Utils util = new Utils();

        try {

            WorkbookSettings settings = new WorkbookSettings();
            settings.setEncoding("CP1250");

            Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);

            Sheet[] sheets = arquivo.getSheets();

            try {

                for (int sh = 0; sh < sheets.length; sh++) {
                    Sheet sheet = arquivo.getSheet(sh);
                    linha = 0;

                    for (int i = 0; i < sheet.getRows(); i++) {
                        linha++;

                        //ignora o cabeçalho
                        if (linha == 1) {
                            continue;

                        } else if (sheet.getCell(1, i).getContents().isEmpty()) { //ignora linha em branco
                            continue;
                        }

                        //if (linha == 1012) {
                        //    JOptionPane.showMessageDialog(null, "aqui");
                        //}
                        strEstoque = "";
                        strNovoEstoque = "";
                        descricao = "";

                        Cell cellIdProduto = sheet.getCell(0, i);
                        Cell cellEstoque = sheet.getCell(1, i);
                        Cell cellCodigoBarras = sheet.getCell(2, i);
                        Cell cellDescricaoProduto = sheet.getCell(3, i);

                        idProduto = Double.parseDouble(cellIdProduto.getContents().trim().substring(0,
                                cellIdProduto.getContents().trim().length() - 3));

                        if ((cellEstoque.getContents() != null)
                                && (!cellEstoque.getContents().trim().isEmpty())
                                && (!"NULL".equals(cellEstoque.getContents().trim()))) {

                            strEstoque = cellEstoque.getContents().trim();
                            strEstoque = strEstoque.substring(0, strEstoque.length() - 4);
                            strEstoque = strEstoque.replace(".", "");

                            estoque = Double.parseDouble(strEstoque);

                        } else {
                            estoque = 0;
                        }

                        if ((cellCodigoBarras.getContents() != null)
                                && (!cellCodigoBarras.getContents().trim().isEmpty())
                                && (!"NULL".equals(cellCodigoBarras.getContents().trim()))
                                && (cellCodigoBarras.getContents().trim().length() >= 10)) {

                            codigoBarras = Long.parseLong(util.formataNumero(cellCodigoBarras.getContents().trim().substring(0,
                                    cellCodigoBarras.getContents().trim().length() - 3)));
                        } else {
                            codigoBarras = -1;
                        }

                        if ((cellDescricaoProduto.getContents() != null)
                                && (!cellDescricaoProduto.getContents().trim().isEmpty())
                                && (!"NULL".equals(cellDescricaoProduto.getContents().trim()))) {
                            descricao = util.acertarTexto(cellDescricaoProduto.getContents().trim().replace("'", ""));
                        } else {
                            descricao = "";
                        }

                        ProdutoVO oProduto = new ProdutoVO();

                        oProduto.idDouble = idProduto;
                        oProduto.descricaoCompleta = descricao;
                        oProduto.codigoBarras = codigoBarras;

                        ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();

                        oComplemento.estoque = estoque;

                        oProduto.vComplemento.add(oComplemento);

                        vProdutoEstoque.add(oProduto);
                    }
                }

            } catch (Exception ex) {
                if (linha > 0) {
                    throw new VRException("Linha " + linha + ": " + ex.getMessage());
                } else {
                    throw ex;
                }

            }

            return vProdutoEstoque;
        } catch (Exception ex) {
            throw ex;
        }
    }

    private List<ProdutoVO> carregarProdutoOrigem(String i_arquivo) throws Exception {
        List<ProdutoVO> vProdutoOrigem = new ArrayList<>();

        try {
            int linha = 0;

            WorkbookSettings settings = new WorkbookSettings();
            settings.setEncoding("CP1250");

            Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);

            Sheet[] sheets = arquivo.getSheets();

            try {

                for (int sh = 0; sh < sheets.length; sh++) {
                    Sheet sheet = arquivo.getSheet(sh);
                    linha = 0;

                    for (int i = 0; i < sheet.getRows(); i++) {
                        linha++;

                        //ignora o cabeçalho
                        if (linha == 1) {
                            continue;

                        } else if (sheet.getCell(1, i).getContents().isEmpty()) { //ignora linha em branco
                            continue;
                        }

                        Cell cellCodBarras = sheet.getCell(0, i);
                        Cell cellDescricao = sheet.getCell(1, i);
                        Cell cellPrecoVenda = sheet.getCell(2, i);
                        Cell cellNCM = sheet.getCell(3, i);

                        String testeBarras = Util.getNumero(cellCodBarras.getContents().isEmpty() ? "" : cellCodBarras.getContents());

                        if (testeBarras.length() > 13) {
                            testeBarras = testeBarras.substring(0, 13);
                        }

                        if (!testeBarras.equals("")) {
                            testeBarras = String.valueOf(Long.parseLong(testeBarras));
                        }

                        Long codBarras = (long) -1;

                        if (testeBarras.length() > 7) {
                            codBarras = Long.parseLong(testeBarras);
                        }

                        String testePrecoVenda = cellPrecoVenda.getContents().isEmpty() ? "0" : cellPrecoVenda.getContents().replace(".", "").replace(",", ".").replace("$", "").replace("  ", " ");

                        Double precoVenda = Double.parseDouble("0");

                        if (testePrecoVenda.length() < 11) {
                            precoVenda = Double.parseDouble(testePrecoVenda);
                        }

                        String ncm = cellNCM.getContents().isEmpty() ? "" : cellNCM.getContents();
                        String descricao = cellDescricao.getContents().isEmpty() ? "" : acertarTexto(Util.converteCaracter(cellDescricao.getContents())).toUpperCase();

                        int idTipoEmbalagem = 0;

                        ProdutoVO oProduto = new ProdutoVO();
                        oProduto.eBalanca = (codBarras == -1);

                        if (oProduto.eBalanca) {
                            if (descricao.contains("KG")) {
                                idTipoEmbalagem = 4;
                            }
                        }

                        if (descricao.length() <= 60) {
                            oProduto.descricaoCompleta = descricao;
                        } else {
                            oProduto.descricaoCompleta = descricao.substring(0, 60);
                        }

                        if (descricao.length() <= 60) {
                            oProduto.descricaoCompletaAnterior = descricao;
                        } else {
                            oProduto.descricaoCompletaAnterior = descricao.substring(0, 60);
                        }

                        if (descricao.length() <= 60) {
                            oProduto.descricaoGondola = descricao;
                        } else {
                            oProduto.descricaoGondola = descricao.substring(0, 60);
                        }

                        if (descricao.length() <= 22) {
                            oProduto.descricaoReduzida = descricao;
                        } else {
                            oProduto.descricaoReduzida = descricao.substring(0, 22);
                        }

                        oProduto.dataCadastro = Util.getDataAtual();

                        // Dados padrão
                        Map<String, TipoPisCofinsVO> vTipoCofins = new PisCofinsDAO().carregarTributado();
                        MercadologicoVO oMercadologico = new MercadologicoDAO().carregar();
                        CompradorVO oComprador = new CompradorDAO().carregar();
                        int idAliquotaICMS = new IcmsDAO().carregarOutras();

                        oProduto.idTipoPisCofinsDebito = vTipoCofins.get("saida").id;
                        oProduto.idTipoPisCofinsCredito = vTipoCofins.get("entrada").id;

                        oProduto.mercadologico1 = oMercadologico.mercadologico1;
                        oProduto.mercadologico2 = oMercadologico.mercadologico2;
                        oProduto.mercadologico3 = oMercadologico.mercadologico3;
                        oProduto.mercadologico4 = oMercadologico.mercadologico4;
                        oProduto.mercadologico5 = oMercadologico.mercadologico5;

                        NcmVO oNcm = null;

                        if (ncm.equals("")) {
                            oNcm = new NcmDAO().getPadrao();

                        } else {
                            oNcm = new NcmDAO().validar(ncm);
                        }

                        oProduto.ncm1 = oNcm.ncm1;
                        oProduto.ncm2 = oNcm.ncm2;
                        oProduto.ncm3 = oNcm.ncm3;

                        oProduto.idTipoEmbalagem = idTipoEmbalagem;

                        oProduto.idProdutoVasilhame = -1;
                        oProduto.idFamiliaProduto = -1;
                        oProduto.idFornecedorFabricante = Global.idFornecedor;
                        oProduto.excecao = -1;
                        oProduto.idTipoMercadoria = -1;
                        oProduto.tipoNaturezaReceita = -1;
                        oProduto.qtdEmbalagem = 1;
                        oProduto.idTipoMercadoria = 2;
                        oProduto.vendaPdv = true;
                        oProduto.idComprador = oComprador.id;
                        oProduto.sugestaoPedido = true;
                        oProduto.aceitaMultiplicacaoPdv = true;
                        oProduto.sazonal = false;
                        oProduto.fabricacaoPropria = false;
                        oProduto.consignado = false;
                        oProduto.ddv = 0;
                        oProduto.permiteTroca = true;
                        oProduto.vendaControlada = false;
                        oProduto.vendaPdv = true;
                        oProduto.conferido = true;
                        oProduto.permiteQuebra = true;
                        oProduto.permitePerda = true;
                        oProduto.utilizaTabelaSubstituicaoTributaria = false;
                        oProduto.utilizaValidadeEntrada = false;

                        ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();

                        oComplemento.idSituacaoCadastro = SituacaoCadastro.ATIVO.getId();
                        oComplemento.precoVenda = precoVenda;
                        oComplemento.precoVendaAnterior = precoVenda;
                        oComplemento.precoDiaSeguinte = precoVenda;
                        oComplemento.dataUltimoPreco = oProduto.dataCadastro;

                        ProdutoAliquotaVO oProdutoAliquota = new ProdutoAliquotaVO();

                        oProdutoAliquota.idAliquotaCredito = idAliquotaICMS;
                        oProdutoAliquota.idAliquotaCreditoForaEstado = idAliquotaICMS;
                        oProdutoAliquota.idAliquotaDebito = idAliquotaICMS;
                        oProdutoAliquota.idEstado = Global.idEstado;
                        oProdutoAliquota.idAliquotaDebitoForaEstado = idAliquotaICMS;
                        oProdutoAliquota.idAliquotaDebitoForaEstadoNF = idAliquotaICMS;

                        oProduto.vAliquota.add(oProdutoAliquota);

                        for (ProdutoComplementoVO oProdutoComplemento : oProduto.vComplemento) {
                            oProdutoComplemento.idAliquotaCredito = idAliquotaICMS;
                        }

                        oProduto.vComplemento.add(oComplemento);

                        ProdutoAutomacaoVO oProdutoAutomacao = new ProdutoAutomacaoVO();

                        oProdutoAutomacao.codigoBarras = codBarras;
                        oProdutoAutomacao.idTipoEmbalagem = idTipoEmbalagem;
                        oProdutoAutomacao.qtdEmbalagem = 1;
                        oProdutoAutomacao.precoVenda = precoVenda;

                        oProduto.vAutomacao.add(oProdutoAutomacao);

                        vProdutoOrigem.add(oProduto);
                    }
                }

                return vProdutoOrigem;

            } catch (Exception ex) {
                if (linha > 0) {
                    throw new VRException("Linha " + linha + ": " + ex.getMessage());
                } else {
                    throw ex;
                }
            }

        } catch (Exception e) {
            throw e;
        }
    }

    private List<ProdutoVO> carregarProdutoPaiva(String i_arquivo) throws Exception {
        List<ProdutoVO> vProdutoOrigem = new ArrayList<>();

        try {
            int linha = 0, idProduto = 0, idAliquota, idTipoPisCofinsDebito,
                    idTipoPisCofinsCredito, tipoNaturezaReceita, ncm1, ncm2, ncm3,
                    codigoBalanca, referencia, validade, idTipoEmbalagem, qtdEmbalagem = 1;
            String strCodigoBarras = "", descricaoCompleta, descricaoReduzida, descricaoGondola,
                    strNcm = "", strCodICMS = "", strPisCofins = "", strTipoNaturezaReceita = "";
            boolean eBalanca = false, pesavel = false;
            long codigoBarras = 0;
            Utils util = new Utils();

            StringBuilder sql = null;
            Statement stm = null;
            ResultSet rst = null;

            WorkbookSettings settings = new WorkbookSettings();
            settings.setEncoding("CP1250");

            Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);

            Sheet[] sheets = arquivo.getSheets();

            try {

                Conexao.begin();
                stm = Conexao.createStatement();

                for (int sh = 0; sh < sheets.length; sh++) {
                    Sheet sheet = arquivo.getSheet(sh);
                    linha = 0;

                    for (int i = 0; i < sheet.getRows(); i++) {
                        linha++;

                        //ignora o cabeçalho
                        if (linha == 1) {
                            continue;

                        } else if (sheet.getCell(1, i).getContents().isEmpty()) { //ignora linha em branco
                            continue;
                        } else if (("PAIVA Acougue".equals(sheet.getCell(0, i).getContents().trim()))
                                || ("SSCARLTP  GIL".equals(sheet.getCell(0, i).getContents().trim()))
                                || ("=============".equals(sheet.getCell(0, i).getContents().trim()))
                                || ("Cod.".equals(sheet.getCell(0, i).getContents().trim()))
                                || ("=============".equals(sheet.getCell(0, i).getContents().trim()))) {
                            continue;
                        } else {

                            if (linha == 1414) {
                                System.out.println("aqui");
                            }

                            Cell cellCodigo = sheet.getCell(0, i);
                            Cell cellCodigoBarra = sheet.getCell(1, i);
                            Cell cellDescricao = sheet.getCell(2, i);
                            Cell cellTamanho = sheet.getCell(3, i);
                            Cell cellCodICMS = sheet.getCell(7, i);
                            Cell cellPisCofins = sheet.getCell(8, i);
                            Cell cellNCM = sheet.getCell(9, i);

                            eBalanca = false;
                            codigoBalanca = 0;
                            referencia = -1;
                            pesavel = false;

                            idProduto = Integer.parseInt(cellCodigo.getContents().trim());

                            if (cellCodigoBarra.getContents().contains("X")) {
                                strCodigoBarras = cellCodigoBarra.getContents().replace("X", "").trim();
                            } else {
                                strCodigoBarras = cellCodigoBarra.getContents().substring(0,
                                        cellCodigoBarra.getContents().trim().length() - 3);
                            }

                            if (!strCodigoBarras.isEmpty()) {

                                sql = new StringBuilder();
                                sql.append("select codigo, descricao, pesavel, validade ");
                                sql.append("from implantacao.produtobalanca ");
                                sql.append("where codigo = " + strCodigoBarras.substring(1,
                                        strCodigoBarras.length()));

                                rst = stm.executeQuery(sql.toString());

                                if (rst.next()) {
                                    eBalanca = true;
                                    codigoBalanca = rst.getInt("codigo");
                                    validade = rst.getInt("validade");

                                    if ("P".equals(rst.getString("pesavel"))) {
                                        idTipoEmbalagem = 4;
                                        pesavel = false;
                                    } else {
                                        idTipoEmbalagem = 0;
                                        pesavel = true;
                                    }
                                } else {
                                    pesavel = false;
                                    validade = 0;
                                    idTipoEmbalagem = 0;
                                }

                            } else {
                                pesavel = false;
                                validade = 0;
                                idTipoEmbalagem = 0;
                            }

                            descricaoCompleta = util.acertarTexto(cellDescricao.getContents().trim().replace("'", ""));
                            descricaoCompleta = descricaoCompleta + " " + util.acertarTexto(cellTamanho.getContents().trim());

                            descricaoReduzida = descricaoCompleta;
                            descricaoGondola = descricaoCompleta;

                            strNcm = cellNCM.getContents().replace(" ", "").trim();

                            strCodICMS = cellCodICMS.getContents().trim();

                            idAliquota = retornarIcmsPaiva(strCodICMS);

                            strPisCofins = cellPisCofins.getContents().trim();

                            if (strPisCofins.trim().isEmpty()) {
                                idTipoPisCofinsDebito = 1;
                                idTipoPisCofinsCredito = 13;
                                tipoNaturezaReceita = 999;
                            } else {

                                idTipoPisCofinsDebito = retornarPisCofinsPaivaDebito(strPisCofins);
                                idTipoPisCofinsCredito = retornarPisCofinsPaivaCredito(strPisCofins);
                                strTipoNaturezaReceita = String.valueOf(retornarNaturezaReceitaPaiva(strPisCofins));
                                tipoNaturezaReceita = util.retornarTipoNaturezaReceita(idTipoPisCofinsDebito, strTipoNaturezaReceita);
                            }

                            if (strNcm.length() > 5) {
                                NcmVO oNcm = new NcmDAO().validar(strNcm);

                                ncm1 = oNcm.ncm1;
                                ncm2 = oNcm.ncm2;
                                ncm3 = oNcm.ncm3;
                            } else {
                                ncm1 = 402;
                                ncm2 = 99;
                                ncm3 = 0;
                            }

                            // codigobarras
                            if (eBalanca) {
                                codigoBarras = idProduto;
                            } else {

                                if (!strCodigoBarras.isEmpty()) {

                                    if (idProduto >= 10000) {

                                        if (strCodigoBarras.length() < 7) {

                                            codigoBarras = util.gerarEan13(idProduto, true);

                                        } else {

                                            codigoBarras = Long.parseLong(strCodigoBarras);
                                        }
                                    } else {

                                        if (strCodigoBarras.length() < 7) {

                                            codigoBarras = util.gerarEan13(idProduto, false);
                                        } else {
                                            codigoBarras = Long.parseLong(strCodigoBarras);
                                        }
                                    }
                                } else {
                                    codigoBarras = -1;
                                }
                            }

                            if (descricaoCompleta.length() > 60) {
                                descricaoCompleta = descricaoCompleta.substring(0, 60);
                            }

                            if (descricaoReduzida.length() > 22) {
                                descricaoReduzida = descricaoReduzida.substring(0, 22);
                            }

                            if (descricaoGondola.length() > 60) {
                                descricaoGondola = descricaoGondola.substring(0, 60);
                            }

                            ProdutoVO oProduto = new ProdutoVO();
                            oProduto.id = idProduto;
                            oProduto.descricaoCompleta = descricaoCompleta;
                            oProduto.descricaoReduzida = descricaoReduzida;
                            oProduto.descricaoGondola = descricaoGondola;
                            oProduto.idTipoEmbalagem = idTipoEmbalagem;
                            oProduto.qtdEmbalagem = qtdEmbalagem;
                            oProduto.pesavel = pesavel;
                            oProduto.mercadologico1 = 14;
                            oProduto.mercadologico2 = 1;
                            oProduto.mercadologico3 = 1;
                            oProduto.validade = validade;
                            oProduto.ncm1 = ncm1;
                            oProduto.ncm2 = ncm2;
                            oProduto.ncm3 = ncm3;
                            oProduto.idFamiliaProduto = -1;
                            oProduto.sugestaoPedido = true;
                            oProduto.aceitaMultiplicacaoPdv = true;
                            oProduto.sazonal = false;
                            oProduto.fabricacaoPropria = false;
                            oProduto.consignado = false;
                            oProduto.ddv = 0;
                            oProduto.permiteTroca = true;
                            oProduto.vendaControlada = false;
                            oProduto.vendaPdv = true;
                            oProduto.conferido = true;
                            oProduto.permiteQuebra = true;
                            oProduto.idComprador = 1;
                            oProduto.idFornecedorFabricante = 1;
                            oProduto.idTipoPisCofinsDebito = idTipoPisCofinsDebito;
                            oProduto.idTipoPisCofinsCredito = idTipoPisCofinsCredito;
                            oProduto.tipoNaturezaReceita = tipoNaturezaReceita;

                            ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                            oComplemento.idSituacaoCadastro = 1;
                            oProduto.vComplemento.add(oComplemento);

                            ProdutoAliquotaVO oAliquota = new ProdutoAliquotaVO();
                            oAliquota.idEstado = 33;
                            oAliquota.idAliquotaDebito = idAliquota;
                            oAliquota.idAliquotaCredito = idAliquota;
                            oAliquota.idAliquotaDebitoForaEstado = idAliquota;
                            oAliquota.idAliquotaCreditoForaEstado = idAliquota;
                            oAliquota.idAliquotaDebitoForaEstadoNF = idAliquota;
                            oProduto.vAliquota.add(oAliquota);

                            ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();
                            oAutomacao.codigoBarras = codigoBarras;
                            oAutomacao.idTipoEmbalagem = idTipoEmbalagem;
                            oAutomacao.qtdEmbalagem = 1;
                            oProduto.vAutomacao.add(oAutomacao);

                            CodigoAnteriorVO oCodigoAnterior = new CodigoAnteriorVO();
                            oCodigoAnterior.codigoanterior = idProduto;

                            if (!strCodigoBarras.isEmpty()) {
                                oCodigoAnterior.barras = Long.parseLong(strCodigoBarras);
                            } else {
                                oCodigoAnterior.barras = -1;
                            }

                            oCodigoAnterior.piscofinsdebito = idTipoPisCofinsDebito;
                            oCodigoAnterior.piscofinscredito = idTipoPisCofinsCredito;
                            oCodigoAnterior.naturezareceita = tipoNaturezaReceita;
                            oCodigoAnterior.ref_icmsdebito = strCodICMS;
                            oCodigoAnterior.e_balanca = eBalanca;
                            oCodigoAnterior.codigobalanca = codigoBalanca;
                            oCodigoAnterior.ncm = strNcm;
                            oCodigoAnterior.referencia = referencia;
                            oProduto.vCodigoAnterior.add(oCodigoAnterior);

                            vProdutoOrigem.add(oProduto);

                        }
                    }
                }

                Conexao.commit();
                return vProdutoOrigem;

            } catch (Exception ex) {
                if (linha > 0) {
                    throw new VRException("Linha " + linha + ": " + ex.getMessage());
                } else {
                    throw ex;
                }
            }

        } catch (Exception e) {
            throw e;
        }
    }

    private List<ProdutoVO> carregarProdutoPrecoCustoMargemPaiva(String i_arquivo) throws Exception {
        List<ProdutoVO> vProdutoOrigem = new ArrayList<>();

        try {
            int linha = 0, idProduto = 0;
            double precoVenda, custoComImposto, custoSemImposto, margem;
            Utils util = new Utils();

            WorkbookSettings settings = new WorkbookSettings();
            settings.setEncoding("CP1250");

            Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);

            Sheet[] sheets = arquivo.getSheets();

            try {

                for (int sh = 0; sh < sheets.length; sh++) {
                    Sheet sheet = arquivo.getSheet(sh);
                    linha = 0;

                    for (int i = 0; i < sheet.getRows(); i++) {
                        linha++;

                        //ignora o cabeçalho
                        if (linha == 1) {
                            continue;

                        } else if (sheet.getCell(1, i).getContents().isEmpty()) { //ignora linha em branco
                            continue;
                        } else if (("PAIVA Ac".equals(sheet.getCell(0, i).getContents().trim()))
                                || ("SIPRCOCV".equals(sheet.getCell(0, i).getContents().trim()))
                                || ("Secao.:".equals(sheet.getCell(0, i).getContents().trim()))
                                || ("Fabr..:".equals(sheet.getCell(0, i).getContents().trim()))
                                || ("========".equals(sheet.getCell(0, i).getContents().trim()))
                                || ("".equals(sheet.getCell(0, i).getContents().trim()))) {
                            continue;
                        } else {

                            Cell cellCodigo = sheet.getCell(0, i);
                            Cell cellCusto = sheet.getCell(2, i);
                            Cell cellMargem = sheet.getCell(5, i);
                            Cell cellPreco = sheet.getCell(7, i);

                            idProduto = Integer.parseInt(cellCodigo.getContents().trim());
                            precoVenda = Double.parseDouble(cellPreco.getContents().trim().replace(",", "."));
                            custoComImposto = Double.parseDouble(cellCusto.getContents().trim().replace(",", "."));
                            custoSemImposto = Double.parseDouble(cellCusto.getContents().trim().replace(",", "."));
                            margem = Double.parseDouble(cellMargem.getContents().trim().replace(",", "."));

                            ProdutoVO oProduto = new ProdutoVO();
                            oProduto.id = idProduto;
                            oProduto.margem = margem;

                            ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                            oComplemento.precoVenda = precoVenda;
                            oComplemento.precoDiaSeguinte = precoVenda;
                            oComplemento.custoComImposto = custoComImposto;
                            oComplemento.custoSemImposto = custoSemImposto;
                            oProduto.vComplemento.add(oComplemento);

                            vProdutoOrigem.add(oProduto);

                        }
                    }
                }

                Conexao.commit();
                return vProdutoOrigem;

            } catch (Exception ex) {
                if (linha > 0) {
                    throw new VRException("Linha " + linha + ": " + ex.getMessage());
                } else {
                    throw ex;
                }
            }

        } catch (Exception e) {
            throw e;
        }
    }

    private List<ProdutoVO> carregarProdutoICMSPaiva(String i_arquivo) throws Exception {
        List<ProdutoVO> vProdutoOrigem = new ArrayList<>();

        try {
            int linha = 0, idProduto = 0, idAliquota, idTipoPisCofinsDebito,
                    idTipoPisCofinsCredito, tipoNaturezaReceita, ncm1, ncm2, ncm3,
                    codigoBalanca, referencia, validade, idTipoEmbalagem, qtdEmbalagem = 1;
            String strCodigoBarras = "", descricaoCompleta, descricaoReduzida, descricaoGondola,
                    strNcm = "", strCodICMS = "", strPisCofins = "", strTipoNaturezaReceita = "";
            boolean eBalanca = false, pesavel = false;
            long codigoBarras = 0;
            Utils util = new Utils();

            StringBuilder sql = null;
            Statement stm = null;
            ResultSet rst = null;

            WorkbookSettings settings = new WorkbookSettings();
            settings.setEncoding("CP1250");

            Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);

            Sheet[] sheets = arquivo.getSheets();

            try {

                Conexao.begin();
                stm = Conexao.createStatement();

                for (int sh = 0; sh < sheets.length; sh++) {
                    Sheet sheet = arquivo.getSheet(sh);
                    linha = 0;

                    for (int i = 0; i < sheet.getRows(); i++) {
                        linha++;

                        //ignora o cabeçalho
                        if (linha == 1) {
                            continue;

                        } else if (sheet.getCell(1, i).getContents().isEmpty()) { //ignora linha em branco
                            continue;
                        } else if (("PAIVA Acougue".equals(sheet.getCell(0, i).getContents().trim()))
                                || ("SSCARLTP  GIL".equals(sheet.getCell(0, i).getContents().trim()))
                                || ("=============".equals(sheet.getCell(0, i).getContents().trim()))
                                || ("Cod.".equals(sheet.getCell(0, i).getContents().trim()))
                                || ("=============".equals(sheet.getCell(0, i).getContents().trim()))) {
                            continue;
                        } else {

                            if (linha == 1414) {
                                System.out.println("aqui");
                            }

                            Cell cellCodigo = sheet.getCell(0, i);
                            Cell cellCodICMS = sheet.getCell(7, i);

                            eBalanca = false;
                            codigoBalanca = 0;
                            referencia = -1;
                            pesavel = false;

                            idProduto = Integer.parseInt(cellCodigo.getContents().trim());

                            strCodICMS = cellCodICMS.getContents().trim();

                            idAliquota = retornarIcmsPaiva(strCodICMS);

                            ProdutoVO oProduto = new ProdutoVO();
                            oProduto.id = idProduto;

                            ProdutoAliquotaVO oAliquota = new ProdutoAliquotaVO();
                            oAliquota.idEstado = 33;
                            oAliquota.idAliquotaDebito = idAliquota;
                            oAliquota.idAliquotaCredito = idAliquota;
                            oAliquota.idAliquotaDebitoForaEstado = idAliquota;
                            oAliquota.idAliquotaCreditoForaEstado = idAliquota;
                            oAliquota.idAliquotaDebitoForaEstadoNF = idAliquota;
                            oProduto.vAliquota.add(oAliquota);

                            vProdutoOrigem.add(oProduto);

                        }
                    }
                }

                return vProdutoOrigem;
            } catch (Exception ex) {
                throw ex;
            }
        } catch (Exception e) {
            throw e;
        }
    }

    private List<ProdutoVO> carregarProdutoICMSSaoFrancisco(String i_arquivo) throws Exception {
        List<ProdutoVO> vProdutoOrigem = new ArrayList<>();

        try {
            int linha = 0, idProduto = 0, idAliquota, idTipoPisCofinsDebito,
                    idTipoPisCofinsCredito, tipoNaturezaReceita, ncm1, ncm2, ncm3,
                    codigoBalanca, referencia, validade, idTipoEmbalagem, qtdEmbalagem = 1;
            String strCodigoBarras = "", descricaoCompleta, descricaoReduzida, descricaoGondola,
                    strNcm = "", strCodICMS = "", strPisCofins = "", strTipoNaturezaReceita = "";
            boolean eBalanca = false, pesavel = false;
            long codigoBarras = 0;
            Utils util = new Utils();

            StringBuilder sql = null;
            Statement stm = null;
            ResultSet rst = null;

            WorkbookSettings settings = new WorkbookSettings();
            settings.setEncoding("CP1250");

            Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);

            Sheet[] sheets = arquivo.getSheets();

            try {

                //Conexao.begin();
                //stm = Conexao.createStatement();
                for (int sh = 0; sh < sheets.length; sh++) {
                    Sheet sheet = arquivo.getSheet(sh);
                    linha = 0;

                    for (int i = 0; i < sheet.getRows(); i++) {
                        linha++;

                        //ignora o cabeçalho
                        if (linha == 1) {
                            continue;

                        } else {

                            Cell cellCodigo = sheet.getCell(0, i);
                            Cell cellCodICMS = sheet.getCell(3, i);

                            idProduto = Integer.parseInt(cellCodigo.getContents().trim());

                            strCodICMS = cellCodICMS.getContents().trim();

                            if ("0".equals(strCodICMS)) {
                                idAliquota = 8;
                            } else if ("1".equals(strCodICMS)) { // 18
                                idAliquota = 2;
                            } else if ("2".equals(strCodICMS)) { // 27
                                idAliquota = 19;
                            } else if ("3".equals(strCodICMS)) {
                                idAliquota = 7;
                            } else if ("4".equals(strCodICMS)) {
                                idAliquota = 8;
                            } else if ("5".equals(strCodICMS)) {
                                idAliquota = 3;
                            } else if ("6".equals(strCodICMS)) {
                                idAliquota = 1;
                            } else if ("7".equals(strCodICMS)) {
                                idAliquota = 0;
                            } else if ("8".equals(strCodICMS)) {
                                idAliquota = 6;
                            } else if ("9".equals(strCodICMS)) {
                                idAliquota = 2;
                            } else if ("10".equals(strCodICMS)) {
                                idAliquota = 19;
                            } else if ("11".equals(strCodICMS)) {
                                idAliquota = 7;
                            } else if ("12".equals(strCodICMS)) {
                                idAliquota = 8;
                            } else if ("13".equals(strCodICMS)) {
                                idAliquota = 3;
                            } else if ("14".equals(strCodICMS)) {
                                idAliquota = 1;
                            } else if ("15".equals(strCodICMS)) {
                                idAliquota = 0;
                            } else if ("16".equals(strCodICMS)) {
                                idAliquota = 6;
                            } else if ("17".equals(strCodICMS)) {
                                idAliquota = 7;
                            } else if ("18".equals(strCodICMS)) {
                                idAliquota = 7;
                            } else if ("19".equals(strCodICMS)) {
                                idAliquota = 8;
                            } else if ("20".equals(strCodICMS)) {
                                idAliquota = 8;
                            } else {
                                idAliquota = 8;
                            }

                            ProdutoVO oProduto = new ProdutoVO();
                            oProduto.id = idProduto;

                            ProdutoAliquotaVO oAliquota = new ProdutoAliquotaVO();
                            //oAliquota.idEstado = 33;
                            oAliquota.idAliquotaDebito = idAliquota;
                            oAliquota.idAliquotaCredito = idAliquota;
                            oAliquota.idAliquotaDebitoForaEstado = idAliquota;
                            oAliquota.idAliquotaCreditoForaEstado = idAliquota;
                            oAliquota.idAliquotaDebitoForaEstadoNF = idAliquota;
                            oProduto.vAliquota.add(oAliquota);

                            vProdutoOrigem.add(oProduto);

                        }
                    }
                }

                //Conexao.commit();
                return vProdutoOrigem;
            } catch (Exception ex) {
                throw ex;
            }
        } catch (Exception e) {
            throw e;
        }
    }

    private List<ProdutoVO> carregarProdutoICMSSysPdv(String i_arquivo) throws Exception {
        List<ProdutoVO> vProdutoOrigem = new ArrayList<>();

        try {
            int linha = 0, idAliquota = 0;
            double idProduto = 0;
            String strCodICMS = "";
            Utils util = new Utils();

            StringBuilder sql = null;
            Statement stm = null;
            ResultSet rst = null;

            WorkbookSettings settings = new WorkbookSettings();
            settings.setEncoding("CP1250");

            Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);

            Sheet[] sheets = arquivo.getSheets();

            try {

                for (int sh = 0; sh < sheets.length; sh++) {
                    Sheet sheet = arquivo.getSheet(sh);
                    linha = 0;

                    for (int i = 0; i < sheet.getRows(); i++) {
                        linha++;

                        //ignora o cabeçalho
                        if (linha == 1) {
                            continue;

                        } else if (util.encontrouLetraCampoNumerico(sheet.getCell(0, i).getContents())) {
                            continue;
                        } else {

                            Cell cellCodigo = sheet.getCell(0, i);
                            Cell cellCodICMS = sheet.getCell(1, i);

                            idProduto = Double.parseDouble(cellCodigo.getContents().trim().substring(0,
                                    cellCodigo.getContents().trim().length() - 3));

                            strCodICMS = cellCodICMS.getContents().trim();

                            if ((strCodICMS != null)
                                    && (!strCodICMS.trim().isEmpty())) {
                                idAliquota = retornarAliquotaICMSSysPdv(strCodICMS.trim().toUpperCase(), "");
                            } else {
                                idAliquota = 8;
                            }

                            ProdutoVO oProduto = new ProdutoVO();
                            oProduto.idDouble = idProduto;

                            ProdutoAliquotaVO oAliquota = new ProdutoAliquotaVO();
                            oAliquota.idAliquotaDebito = idAliquota;
                            oAliquota.idAliquotaCredito = idAliquota;
                            oAliquota.idAliquotaDebitoForaEstado = idAliquota;
                            oAliquota.idAliquotaCreditoForaEstado = idAliquota;
                            oAliquota.idAliquotaDebitoForaEstadoNF = idAliquota;
                            oProduto.vAliquota.add(oAliquota);

                            CodigoAnteriorVO oAnterior = new CodigoAnteriorVO();

                            oAnterior.ref_icmsdebito = strCodICMS.trim();
                            oProduto.vCodigoAnterior.add(oAnterior);

                            vProdutoOrigem.add(oProduto);

                        }
                    }
                }

                return vProdutoOrigem;
            } catch (Exception ex) {
                throw ex;
            }
        } catch (Exception e) {
            throw e;
        }
    }

    public List<ProdutoVO> carregarEstoqueCentralEconomia(String i_arquivo) throws Exception {
        List<ProdutoVO> result = new ArrayList<>();
        int linha = 0;
        WorkbookSettings settings = new WorkbookSettings();
        settings.setEncoding("CP1250");
        Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);
        Sheet[] sheets = arquivo.getSheets();
        String strQtd = "";
        double estoque = 0;

        try {

            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;

                for (int i = 0; i < sheet.getRows(); i++) {
                    linha++;

                    if (linha == 1) {
                        continue;
                    } else if (sheet.getCell(0, i).getContents().isEmpty()) {
                        continue;
                    } else if (sheet.getCell(0, i).getContents().contains("Estoque Id")) {
                        continue;
                    } else if (Utils.encontrouLetraCampoNumerico(sheet.getCell(0, i).getContents())) {
                        continue;
                    } else {

                        Cell cellIdProduto = sheet.getCell(0, i);
                        Cell cellEstoque = sheet.getCell(10, i);

                        /*if ((cellEstoque.getContents() != null)
                         && (!cellEstoque.getContents().trim().isEmpty())) {
                         if (cellEstoque.getContents().trim().length() == 9) {
                         for (int j = 0; j < cellEstoque.getContents().trim().length(); j++) {
                         if (j == 1) {
                         strQtd = strQtd + "";
                         } else {
                         strQtd = strQtd + cellEstoque.getContents().charAt(j);
                         }
                         }
                         estoque = Double.parseDouble(strQtd);
                         } else if (cellEstoque.getContents().trim().length() == 10) {
                         for (int j = 0; j < cellEstoque.getContents().trim().length(); j++) {
                         if (j == 2) {
                         strQtd = strQtd + "";
                         } else {
                         strQtd = strQtd + cellEstoque.getContents().charAt(j);
                         }
                         }
                         estoque = Double.parseDouble(strQtd);
                         } else if (cellEstoque.getContents().trim().length() == 11) {
                         for (int j = 0; j < cellEstoque.getContents().trim().length(); j++) {
                         if (j == 3) {
                         strQtd = strQtd + "";
                         } else {
                         strQtd = strQtd + cellEstoque.getContents().charAt(j);
                         }
                         }
                         estoque = Double.parseDouble(strQtd);
                         } else {
                         estoque = Double.parseDouble(cellEstoque.getContents().trim());
                         }
                         } else {
                         estoque = 0;
                         }*/
                        ProdutoVO oProduto = new ProdutoVO();
                        ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                        oProduto.setId(Integer.parseInt(cellIdProduto.getContents().trim()));

                        if ((cellEstoque.getContents() != null)
                                && (!cellEstoque.getContents().trim().isEmpty())) {
                            oComplemento.setEstoque(Double.parseDouble(cellEstoque.getContents().replace(".", "").replace(",", ".")));
                        } else {
                            oComplemento.setEstoque(0);
                        }
                        oProduto.vComplemento.add(oComplemento);
                        result.add(oProduto);
                    }
                }
            }
        } catch (Exception ex) {
            throw ex;
        }
        return result;
    }

    public List<ProdutoVO> carregarEstoqueCentralEconomiaCodBarras(String i_arquivo) throws Exception {
        List<ProdutoVO> result = new ArrayList<>();
        int linha = 0;
        WorkbookSettings settings = new WorkbookSettings();
        settings.setEncoding("CP1250");
        Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);
        Sheet[] sheets = arquivo.getSheets();
        String strQtd = "";
        double estoque = 0;

        try {

            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;

                for (int i = 0; i < sheet.getRows(); i++) {
                    linha++;

                    if (linha == 1) {
                        continue;
                    } else if (sheet.getCell(0, i).getContents().isEmpty()) {
                        continue;
                    } else if (sheet.getCell(0, i).getContents().contains("Estoque Id")) {
                        continue;
                    } else if (Utils.encontrouLetraCampoNumerico(sheet.getCell(0, i).getContents())) {
                        continue;
                    } else if (sheet.getCell(3, i).getContents().trim().isEmpty()) {
                        continue;
                    } else {

                        Cell cellIdProduto = sheet.getCell(3, i);
                        Cell cellEstoque = sheet.getCell(10, i);

                        ProdutoVO oProduto = new ProdutoVO();
                        ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                        oProduto.setCodigoBarras(Long.parseLong(cellIdProduto.getContents().trim()));

                        if ((cellEstoque.getContents() != null)
                                && (!cellEstoque.getContents().trim().isEmpty())) {
                            oComplemento.setEstoque(Double.parseDouble(cellEstoque.getContents().replace(".", "").replace(",", ".")));
                        } else {
                            oComplemento.setEstoque(0);
                        }
                        oProduto.vComplemento.add(oComplemento);
                        result.add(oProduto);
                    }
                }
            }
        } catch (Exception ex) {
            throw ex;
        }
        return result;
    }

    private String acertarTexto(String texto) {
        String vRetorno = "", textoAcertado = "",
                strPode = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789 /?!@#$%&*()-_=+[{]}:;.>,<\'|'";

        if (!"".equals(texto)) {
            texto = texto.replace("a", "A");
            texto = texto.replace("á", "A");
            texto = texto.replace("à", "A");
            texto = texto.replace("ã", "A");
            texto = texto.replace("â", "A");
            texto = texto.replace("ŕ", "A");
            texto = texto.replace("ă", "A");
            texto = texto.replace("Á", "A");
            texto = texto.replace("À", "A");
            texto = texto.replace("Ã", "A");
            texto = texto.replace("Â", "A");
            texto = texto.replace("Ă", "A");
            texto = texto.replace("Ŕ", "A");
            texto = texto.replace("b", "B");
            texto = texto.replace("c", "C");
            texto = texto.replace("ç", "C");
            texto = texto.replace("Ç", "C");
            texto = texto.replace("d", "D");
            texto = texto.replace("e", "E");
            texto = texto.replace("é", "A");
            texto = texto.replace("É", "E");
            texto = texto.replace("Č", "E");
            texto = texto.replace("Ę", "E");
            texto = texto.replace("č", "E");
            texto = texto.replace("ę", "E");
            texto = texto.replace("f", "F");
            texto = texto.replace("g", "G");
            texto = texto.replace("h", "H");
            texto = texto.replace("i", "I");
            texto = texto.replace("í", "I");
            texto = texto.replace("Í", "I");
            texto = texto.replace("Ě", "I");
            texto = texto.replace("ě", "I");
            texto = texto.replace("j", "J");
            texto = texto.replace("k", "K");
            texto = texto.replace("l", "L");
            texto = texto.replace("m", "M");
            texto = texto.replace("n", "N");
            texto = texto.replace("o", "O");
            texto = texto.replace("ó", "O");
            texto = texto.replace("õ", "O");
            texto = texto.replace("ô", "O");
            texto = texto.replace("Ó", "O");
            texto = texto.replace("Õ", "O");
            texto = texto.replace("Ô", "O");
            texto = texto.replace("Ň", "O");
            texto = texto.replace("Ő", "O");
            texto = texto.replace("ň", "O");
            texto = texto.replace("ő", "O");
            texto = texto.replace("p", "P");
            texto = texto.replace("q", "Q");
            texto = texto.replace("r", "R");
            texto = texto.replace("s", "S");
            texto = texto.replace("t", "T");
            texto = texto.replace("u", "U");
            texto = texto.replace("ú", "U");
            texto = texto.replace("Ú", "U");
            texto = texto.replace("Ů", "U");
            texto = texto.replace("Ű", "U");
            texto = texto.replace("ů", "U");
            texto = texto.replace("ű", "U");
            texto = texto.replace("v", "V");
            texto = texto.replace("x", "X");
            texto = texto.replace("y", "Y");
            texto = texto.replace("w", "W");
            texto = texto.replace("z", "Z");
            texto = texto.replace("´", " ");
            texto = texto.replace("º", "R");
            texto = texto.replace("¦", "R");
            texto = texto.replace("°", " ");
            texto = texto.replace("ª", " ");
            texto = texto.replace("Ñ", "N");
            texto = texto.replace("§", "");
            texto = texto.replace("\"", "");
            texto = texto.replace("\\", " ");
            texto = texto.replace(", ", " ");
            texto = texto.replace(" ,", " ");
            texto = texto.replace(",", " ");
            texto = texto.replace(";", " ");
            texto = texto.replace(".", " ");
            texto = texto.replace("[", "");
            texto = texto.replace("]", " ");
            texto = texto.replace("'", "");
            texto = texto.replace("+", "");
            texto = texto.replace("  ", " ");
            texto = texto.replace("  ", " ");
            texto = texto.trim();

            for (int i = 0; i < texto.length(); i++) {
                if (strPode.indexOf(texto.charAt(i)) != -1) {
                    textoAcertado = textoAcertado + texto.charAt(i);
                } else {
                    textoAcertado = textoAcertado + "?";
                }
            }
        }
        vRetorno = textoAcertado;
        return vRetorno;
    }

    private int retornarIcmsPaiva(String codigo) {

        int retorno = 8;

        if ("F".equals(codigo)) {
            retorno = 7;
        } else if ("I".equals(codigo)) {
            retorno = 6;
        } else if ("0".equals(codigo)) {
            retorno = 18;
        } else if ("1".equals(codigo)) {
            retorno = 21;
        } else if ("2".equals(codigo)) {
            retorno = 22;
        } else if ("3".equals(codigo)) {
            retorno = 23;
        } else {
            retorno = 8;
        }

        return retorno;
    }

    private int retornarPisCofinsPaivaDebito(String codigo) {
        int retorno = 1;

        if (("1".equals(codigo)) || ("6".equals(codigo)) || ("7".equals(codigo))) {
            retorno = 0;
        } else if (("A".equals(codigo)) || ("B".equals(codigo)) || ("C".equals(codigo))
                || ("D".equals(codigo)) || ("E".equals(codigo)) || ("F".equals(codigo))
                || ("G".equals(codigo)) || ("H".equals(codigo)) || ("I".equals(codigo))
                || ("K".equals(codigo)) || ("L".equals(codigo))
                || ("M".equals(codigo)) || ("N".equals(codigo)) || ("O".equals(codigo))
                || ("P".equals(codigo)) || ("Q".equals(codigo)) || ("R".equals(codigo))
                || ("S".equals(codigo)) || ("T".equals(codigo)) || ("Z".equals(codigo))) {
            retorno = 7;
        } else if (("a".equals(codigo)) || ("b".equals(codigo) || ("f".equals(codigo)))
                || ("g".equals(codigo)) || ("h".equals(codigo) || ("i".equals(codigo)))
                || ("j".equals(codigo))) {
            retorno = 3;
        } else if ("q".equals(codigo)) {
            retorno = 2;
        } else if (("V".equals(codigo))) {
            retorno = 9;
        } else {
            retorno = 1;
        }

        return retorno;
    }

    private int retornarPisCofinsPaivaCredito(String codigo) {
        int retorno = 1;

        if (("1".equals(codigo)) || ("6".equals(codigo)) || ("7".equals(codigo))) {
            retorno = 12;
        } else if (("A".equals(codigo)) || ("B".equals(codigo)) || ("C".equals(codigo))
                || ("D".equals(codigo)) || ("E".equals(codigo)) || ("F".equals(codigo))
                || ("G".equals(codigo)) || ("H".equals(codigo)) || ("I".equals(codigo))
                || ("K".equals(codigo)) || ("L".equals(codigo))
                || ("M".equals(codigo)) || ("N".equals(codigo)) || ("O".equals(codigo))
                || ("P".equals(codigo)) || ("Q".equals(codigo)) || ("R".equals(codigo))
                || ("S".equals(codigo)) || ("T".equals(codigo)) || ("Z".equals(codigo))) {
            retorno = 19;
        } else if (("a".equals(codigo)) || ("b".equals(codigo) || ("f".equals(codigo)))
                || ("g".equals(codigo)) || ("h".equals(codigo) || ("i".equals(codigo)))
                || ("j".equals(codigo))) {
            retorno = 15;
        } else if ("q".equals(codigo)) {
            retorno = 14;
        } else if (("V".equals(codigo))) {
            retorno = 21;
        } else {
            retorno = 13;
        }

        return retorno;
    }

    private int retornarNaturezaReceitaPaiva(String codigo) {
        int retorno = 999;

        if (("1".equals(codigo)) || ("6".equals(codigo)) || ("7".equals(codigo))) {
            retorno = -1;
        } else if ("A".equals(codigo)) {
            retorno = 105;
        } else if ("B".equals(codigo)) {
            retorno = 108;
        } else if ("C".equals(codigo)) {
            retorno = 110;
        } else if ("D".equals(codigo)) {
            retorno = 111;
        } else if ("E".equals(codigo)) {
            retorno = 113;
        } else if ("F".equals(codigo)) {
            retorno = 115;
        } else if ("G".equals(codigo)) {
            retorno = 116;
        } else if ("H".equals(codigo)) {
            retorno = 117;
        } else if ("I".equals(codigo)) {
            retorno = 119;
        } else if ("K".equals(codigo)) {
            retorno = 121;
        } else if ("L".equals(codigo)) {
            retorno = 122;
        } else if ("M".equals(codigo)) {
            retorno = 123;
        } else if ("N".equals(codigo)) {
            retorno = 124;
        } else if ("O".equals(codigo)) {
            retorno = 125;
        } else if ("P".equals(codigo)) {
            retorno = 126;
        } else if ("Q".equals(codigo)) {
            retorno = 127;
        } else if ("R".equals(codigo)) {
            retorno = 128;
        } else if ("S".equals(codigo)) {
            retorno = 129;
        } else if ("T".equals(codigo)) {
            retorno = 130;
        } else if ("Z".equals(codigo)) {
            retorno = 900;
        } else if ("a".equals(codigo)) {
            retorno = 201;
        } else if ("b".equals(codigo)) {
            retorno = 202;
        } else if ("f".equals(codigo)) {
            retorno = 401;
        } else if ("g".equals(codigo)) {
            retorno = 402;
        } else if ("h".equals(codigo)) {
            retorno = 403;
        } else if ("i".equals(codigo)) {
            retorno = 405;
        } else if ("j".equals(codigo)) {
            retorno = 406;
        } else if ("q".equals(codigo)) {
            retorno = 101;
        } else if ("V".equals(codigo)) {
            retorno = -1;
        } else {
            retorno = 999;
        }

        return retorno;
    }

    private void acertarICMS(List<ProdutoVO> v_produto) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        File f = new File("C:\\svn\\repositorioImplantacao\\Importacoes_OFFICIAL\\Paiva\\update_produtoaliquota.txt");
        FileWriter fw = new FileWriter(f);
        BufferedWriter bw = new BufferedWriter(fw);

        try {
            Conexao.begin();

            stm = Conexao.createStatement();

            ProgressBar.setMaximum(v_produto.size());
            ProgressBar.setStatus("Acertando icms...");

            for (ProdutoVO i_produto : v_produto) {
                for (ProdutoAliquotaVO oAliquota : i_produto.vAliquota) {

                    sql = new StringBuilder();
                    sql.append("select p.id from produto p ");
                    sql.append("inner join implantacao.codigoanterior ant on ant.codigoatual = p.id ");
                    sql.append("where ant.codigoanterior = " + i_produto.id);

                    rst = stm.executeQuery(sql.toString());

                    if (rst.next()) {
                        sql = new StringBuilder();
                        sql.append("update produtoaliquota set ");
                        sql.append("id_aliquotadebito = " + oAliquota.idAliquotaDebito + ", ");
                        sql.append("id_aliquotacredito = " + oAliquota.idAliquotaCredito + ", ");
                        sql.append("id_aliquotadebitoforaestado = " + oAliquota.idAliquotaDebitoForaEstado + ", ");
                        sql.append("id_aliquotacreditoforaestado = " + oAliquota.idAliquotaCreditoForaEstado + ", ");
                        sql.append("id_aliquotadebitoforaestadoNF = " + oAliquota.idAliquotaDebitoForaEstadoNF + " ");
                        sql.append("where id_produto = " + rst.getInt("id") + ";");

                        bw.write(sql.toString());
                        bw.newLine();
                    }

                    ProgressBar.next();
                }
            }

            bw.flush();
            bw.close();
            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    private int retornarAliquotaICMSSysPdv(String codTrib, String descTrib) {

        int retorno;

        if ("F00".equals(codTrib)) {
            retorno = 7;
        } else if ("F07".equals(codTrib)) {
            retorno = 7;
        } else if ("F17".equals(codTrib)) {
            retorno = 7;
        } else if ("F19".equals(codTrib)) {
            retorno = 7;
        } else if ("F58".equals(codTrib)) {
            retorno = 7;
        } else if ("I00".equals(codTrib)) {
            retorno = 6;
        } else if ("N00".equals(codTrib)) {
            retorno = 21;
        } else if ("T07".equals(codTrib)) {
            retorno = 25;
        } else if ("T12".equals(codTrib)) {
            retorno = 24;
        } else if ("T17".equals(codTrib)) {
            retorno = 20;
        } else if ("T18".equals(codTrib)) {
            retorno = 8;
        } else if ("T25".equals(codTrib)) {
            retorno = 3;
        } else if ("T27".equals(codTrib)) {
            retorno = 27;
        } else if ("T29".equals(codTrib)) {
            retorno = 7;
        } else if ("T58".equals(codTrib)) {
            retorno = 26;
        } else {
            retorno = 8;
        }

        return retorno;
    }

    public void migrarProdutoLojaChamp(String arquivo, int idLojaVR) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();

        ProgressBar.setStatus("Carregando dados...Produtos.....");
        Map<Long, ProdutoVO> vProdutos = carregarChampProdutos(arquivo, idLojaVR);

        List<LojaVO> vLoja = new LojaDAO().carregar();

        ProgressBar.setMaximum(vProdutos.size());

        for (Long keyId : vProdutos.keySet()) {

            ProdutoVO oProduto = vProdutos.get(keyId);

            oProduto.idProdutoVasilhame = -1;
            oProduto.excecao = -1;
            oProduto.idTipoMercadoria = -1;

            vProdutoNovo.add(oProduto);

            ProgressBar.next();
        }

        produto.implantacaoExterna = true;
        produto.salvar(vProdutoNovo, idLojaVR, vLoja);
    }

    public void migrarEANChamp(String arquivo, int idLojaVR) throws Exception {
        ProdutoDAO produto = new ProdutoDAO();

        ProgressBar.setStatus("Carregando dados...Produtos...Código de Barra...");
        Map<Long, ProdutoVO> vEanProdutos = carregarChampEAN(arquivo, idLojaVR);

        ProgressBar.setMaximum(vEanProdutos.size());

        produto.addCodigoBarras(new ArrayList<>(vEanProdutos.values()));

        new AbstractIntefaceDao() {
        }.importarCodigoBarraEmBranco();
    }

    public void migrarMercadologicoChamp(String arquivo, int idLojaVR) throws Exception {
        List<MercadologicoVO> vMercadologico;

        ProgressBar.setStatus("Carregando dados...Mercadologico...");
        MercadologicoDAO dao = new MercadologicoDAO();

        vMercadologico = carregarChampMercadologico(arquivo, 1);
        dao.salvar(vMercadologico, true);

        vMercadologico = carregarChampMercadologico(arquivo, 2);
        dao.salvar(vMercadologico, false);

        vMercadologico = carregarChampMercadologico(arquivo, 3);
        dao.salvar(vMercadologico, false);

        dao.salvarMax();
    }

    public void migrarClientePreferencial(String arquivo, int idLojaVR) throws Exception {
        ProgressBar.setStatus("Carregando dados...Cliente Preferencial...");
        List<ClientePreferencialVO> vClientePreferencial = carregarChampCliente(arquivo);
        new PlanoDAO().salvar(idLojaVR);

        new ClientePreferencialDAO().salvar(vClientePreferencial, idLojaVR, 1);
    }

    public void atualizarPrecoCusto(String arquivo, int idLojaVR) throws Exception {
        ProgressBar.setStatus("Carregando dados...Produtos...Preço e Custo...");

        Map<Double, ProdutoVO> aux = new LinkedHashMap<>();
        for (ProdutoVO vo : carregarChampProdutos(arquivo, idLojaVR).values()) {
            Double id = vo.getIdDouble() > 0 ? vo.getIdDouble() : vo.getId();
            aux.put(id, vo);
        }

        ProgressBar.setMaximum(aux.size());

        ProdutoDAO produto = new ProdutoDAO();
        produto.implantacaoExterna = true;
        produto.alterarPrecoProdutoRapido(new ArrayList(aux.values()), idLojaVR);
        produto.alterarCustoProdutoRapido(new ArrayList(aux.values()), idLojaVR);
    }

    public void atualizarEstoqueCentralEconomia(String arquivo, int idLojaVR) throws Exception {
        ProgressBar.setStatus("Carregando dados...Estoque Central Economia...");
        List<ProdutoVO> vProduto = carregarEstoqueCentralEconomia(arquivo);
        new ProdutoDAO().alterarEstoqueSomaProduto(vProduto, idLojaVR);
    }

    public void atualizarEstoqueCentralEconomiaCodBarras(String arquivo, int idLojaVR) throws Exception {
        ProgressBar.setStatus("Carregando dados CodBarras Estoque Central Economia...");
        List<ProdutoVO> vProduto = carregarEstoqueCentralEconomiaCodBarras(arquivo);
        new ProdutoDAO().alterarEstoqueSomaProdutoCodBarras(vProduto, idLojaVR);
    }

    private List<OfertaVO> carregarProdutoOfertas(String i_arquivo, int idLojaVR) throws Exception {
        List<OfertaVO> vResult = new ArrayList<>();

        try {
            double idProduto = 0;
            int linha;
            WorkbookSettings settings = new WorkbookSettings();
            settings.setEncoding("CP1250");
            Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);
            Sheet[] sheets = arquivo.getSheets();

            try {

                for (int sh = 0; sh < sheets.length; sh++) {
                    Sheet sheet = arquivo.getSheet(sh);
                    linha = 0;
                    for (int i = 0; i < sheet.getRows(); i++) {
                        linha++;

                        //ignora o cabeçalho
                        if (linha == 1) {
                            continue;
                        } else if ("000002".equals(sheet.getCell(1, i).getContents().trim())
                                || ("000001".equals(sheet.getCell(1, i).getContents().trim()))) {

                            Cell cellIdLoja = sheet.getCell(1, i);
                            Cell cellCodigo = sheet.getCell(4, i);
                            //Cell cellDataInicio     = sheet.getCell(1, i);
                            //Cell cellDataFim        = sheet.getCell(2, i);
                            Cell cellPrecoOferta = sheet.getCell(10, i);

                            idProduto = Double.parseDouble(Utils.formataNumero(cellCodigo.getContents().trim().substring(0, 10)));
                            OfertaVO vo = new OfertaVO();
                            vo.setId_loja(Integer.parseInt(cellIdLoja.getContents().trim()));
                            vo.setId_produtoDouble(idProduto);
                            vo.setDatainicio("2017/03/14");
                            vo.setDatatermino("2017/12/31");
                            vo.setPrecooferta(Double.parseDouble(cellPrecoOferta.getContents().replace(".", "").replace(",", ".").trim()));
                            vResult.add(vo);
                        } else {
                            continue;
                        }
                    }
                }
                return vResult;
            } catch (Exception ex) {
                throw ex;
            }
        } catch (Exception e) {
            throw e;
        }
    }

    public void importarOfertas(int idLojaVR, String i_arquivo) throws Exception {
        ProgressBar.setStatus("Carregando dados das ofertas");
        List<OfertaVO> ofertas = carregarProdutoOfertas(i_arquivo, idLojaVR);
        new OfertaDAO().salvar(ofertas, idLojaVR);
    }

    private List<ProdutoFornecedorVO> carregarProdutoFornecedor(String i_arquivo) throws Exception {
        List<ProdutoFornecedorVO> vResult = new ArrayList<>();

        try {
            double idProduto = 0;
            int linha;
            WorkbookSettings settings = new WorkbookSettings();
            settings.setEncoding("CP1250");
            Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);
            Sheet[] sheets = arquivo.getSheets();
            String strDataAlteracao = "";
            java.sql.Date dataAlteracao;
            DateFormat fmt = new SimpleDateFormat("yyyy/MM/dd");

            try {

                for (int sh = 0; sh < sheets.length; sh++) {
                    Sheet sheet = arquivo.getSheet(sh);
                    linha = 0;
                    for (int i = 0; i < sheet.getRows(); i++) {
                        linha++;

                        //ignora o cabeçalho
                        if (linha == 1) {
                            continue;
                        }

                        Cell cellIdFornecedor = sheet.getCell(0, i);
                        Cell cellIdProduto = sheet.getCell(1, i);
                        Cell cellCodigoExterno = sheet.getCell(2, i);

                        dataAlteracao = new Date(new java.util.Date().getTime());

                        ProdutoFornecedorVO vo = new ProdutoFornecedorVO();
                        vo.setId_fornecedor(Integer.parseInt(cellIdFornecedor.getContents().trim()));
                        vo.setId_produto(Integer.parseInt(cellIdProduto.getContents().trim()));
                        vo.setDataalteracao(dataAlteracao);

                        if ((cellCodigoExterno.getContents() != null)
                                && (!cellCodigoExterno.getContents().trim().isEmpty())) {
                            if (!"0".equals(cellCodigoExterno.getContents().trim())) {
                                vo.setCodigoexterno(Utils.acertarTexto(cellCodigoExterno.getContents().trim()));
                            } else {
                                vo.setCodigoexterno("");
                            }
                        } else {
                            vo.setCodigoexterno("");
                        }

                        vResult.add(vo);
                    }
                }
                return vResult;
            } catch (Exception ex) {
                throw ex;
            }
        } catch (Exception e) {
            throw e;
        }
    }

    public void importarProdutoForencedor(String i_arquivo) throws Exception {
        List<ProdutoFornecedorVO> vResult = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Produto Fornecedor...");
            vResult = carregarProdutoFornecedor(i_arquivo);

            if (!vResult.isEmpty()) {
                new ProdutoFornecedorDAO().salvar2(vResult);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    private List<ProdutoVO> carregarEstoqueProduto(String i_arquivo) throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();

        try {
            int linha;
            WorkbookSettings settings = new WorkbookSettings();
            settings.setEncoding("CP1250");
            Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);
            Sheet[] sheets = arquivo.getSheets();

            try {

                for (int sh = 0; sh < sheets.length; sh++) {
                    Sheet sheet = arquivo.getSheet(sh);
                    linha = 0;
                    for (int i = 0; i < sheet.getRows(); i++) {
                        linha++;

                        //ignora o cabeçalho
                        if (linha == 1) {
                            continue;
                        }

                        Cell cellIdProduto = sheet.getCell(0, i);
                        Cell cellEstoque = sheet.getCell(4, i);

                        ProdutoVO oProduto = new ProdutoVO();
                        ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                        oProduto.setIdDouble(Double.parseDouble(cellIdProduto.getContents().trim()));
                        oComplemento.setEstoque(Double.parseDouble(cellEstoque.getContents().replace(",", ".").trim()));
                        oProduto.vComplemento.add(oComplemento);
                        vResult.add(oProduto);
                    }
                }
                return vResult;
            } catch (Exception ex) {
                throw ex;
            }
        } catch (Exception e) {
            throw e;
        }
    }

    public void importarEstoqueProduto(String i_arquivo, int idLojaVR) throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Estoque Loja " + idLojaVR + "...");
            vResult = carregarEstoqueProduto(i_arquivo);
            if (!vResult.isEmpty()) {
                new ProdutoDAO().alterarEstoqueSomaProduto(vResult, idLojaVR);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void gerarScriptIdSituacaoCadastro(String i_arquivo) throws Exception {
        WorkbookSettings settings = new WorkbookSettings();
        settings.setEncoding("CP1250");
        Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);
        Sheet[] sheets = arquivo.getSheets();
        int linha;
        File f = new File("C:\\svn\\repositorioImplantacao\\Importacoes_OFFICIAL\\Miranda\\update_situacaocadastro.txt");
        FileWriter fw = new FileWriter(f);
        BufferedWriter bw = new BufferedWriter(fw);
        StringBuilder sql = null, texto = null;
        Statement stm = null;
        ResultSet rst = null;
        long codigoBarras;

        try {
            int contador = 1;
            stm = Conexao.createStatement();
            texto = new StringBuilder();
            bw.write("update produtocomplemento set id_situacaocadastro = 0 where id_produto not in (");
            bw.newLine();
            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;
                for (int i = 0; i < sheet.getRows(); i++) {
                    linha++;
                    if (linha == 1) {
                        continue;
                    }

                    Cell cellCodigoBarras = sheet.getCell(1, i);
                    codigoBarras = Long.parseLong(Utils.formataNumero(cellCodigoBarras.getContents().trim()));

                    if (codigoBarras > 999999) {
                        sql = new StringBuilder();
                        sql.append("select pa.id_produto "
                                + "from produtoautomacao pa "
                                + "where pa.codigobarras = " + codigoBarras);
                        rst = stm.executeQuery(sql.toString());
                        if (rst.next()) {
                            //texto.append(rst.getInt("id_produto")).append(", ");
                            bw.write(rst.getString("id_produto") + ", ");
                        }
                    }

                    ProgressBar.setStatus("Carregando dados..." + contador);
                    contador++;
                }
            }
            bw.newLine();
            bw.write(");");
            bw.flush();
            bw.close();
        } catch (Exception ex) {
            throw ex;
        }
    }
}
