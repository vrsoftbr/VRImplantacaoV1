package vrimplantacao.dao.interfaces;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import vrframework.classe.ProgressBar;
import vrimplantacao.dao.cadastro.ProdutoDAO;
import vrimplantacao.dao.fiscal.InventarioDAO;
import vrimplantacao.vo.fiscal.InventarioVO;

public class PlanilhaInventarioDAO {

    public List<InventarioVO> carregarInventarioLoja10(String i_arquivo, int idLojaVR, String pathLog, String data)
            throws Exception {
        int linha;
        List<InventarioVO> result = new ArrayList<>();
        WorkbookSettings settings = new WorkbookSettings();
        settings.setEncoding("CP1252");
        File f = new File(pathLog + "\\produtosNAOencontrado"+idLojaVR+".txt");
        FileWriter fw = new FileWriter(f);
        BufferedWriter bw = new BufferedWriter(fw);

        Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);
        Sheet[] sheets = arquivo.getSheets();

        try {
            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;

                for (int i = 0; i < sheet.getRows(); i++) {
                    linha++;

                    if (linha == 1) {
                        continue;
                    } else if (sheet.getCell(0, i).getContents().isEmpty()) { //ignora linha em branco
                        continue;
                    }

                    Cell cellCodigoBarras = sheet.getCell(0, i);
                    Cell cellCodigoProduto = sheet.getCell(1, i);
                    Cell cellDescricaoProduto = sheet.getCell(3, i);
                    Cell cellCustoMedio = sheet.getCell(7, i);
                    Cell cellQuantidade = sheet.getCell(8, i);
                    Cell cellValorTotal = sheet.getCell(9, i);

                    InventarioVO oInventario = new InventarioVO();
                    oInventario.setData(data);
                    oInventario.setDataGeracao(data);

                    
                    if (cellCodigoBarras.getContents().trim().length() < 7) {
                        oInventario.setIdProduto(new ProdutoDAO().getIdProdutoCodigoAnterior(Long.parseLong(cellCodigoProduto.getContents().trim())));

                        if (oInventario.getIdProduto() == -1) {
                            bw.write("CODIGO PRODUTO: " + cellCodigoProduto.getContents() + " - " + cellDescricaoProduto.getContents() + ";"
                                    +cellCustoMedio.getContents()+";"+ cellQuantidade.getContents() + ";" + cellValorTotal.getContents());
                            bw.newLine();
                        }
                    } else {
                        oInventario.setIdProduto(new ProdutoDAO().getIdProdutoCodigoBarras(Long.parseLong(cellCodigoBarras.getContents().trim())));

                        if (oInventario.getIdProduto() == -1) {
                            bw.write("CODIGO BARRAS: " + cellCodigoBarras.getContents() + " - " + cellDescricaoProduto.getContents() + ";"
                                    +cellCustoMedio.getContents()+";"+ cellQuantidade.getContents() + ";" + cellValorTotal.getContents());
                            bw.newLine();
                        }
                    }

                    oInventario.setQuantidade(Double.parseDouble(cellQuantidade.getContents().trim().replace(",", ".")));
                    oInventario.setCustoMedioComImposto(Double.parseDouble(cellCustoMedio.getContents().trim().replace(",", ".")));
                    oInventario.setCustoMedioSemImposto(Double.parseDouble(cellCustoMedio.getContents().trim().replace(",", ".")));
                    oInventario.setCustoSemImposto(oInventario.getCustoMedioSemImposto());
                    oInventario.setIdLoja(idLojaVR);
                    result.add(oInventario);
                }
            }
            bw.flush();
            bw.close();
            return result;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public List<InventarioVO> carregarInventarioLoja7(String i_arquivo, int idLojaVR, String pathLog, String data)
            throws Exception {
        int linha;
        List<InventarioVO> result = new ArrayList<>();
        WorkbookSettings settings = new WorkbookSettings();
        settings.setEncoding("CP1252");
        File f = new File(pathLog + "\\produtosNAOencontrado"+idLojaVR+".txt");
        FileWriter fw = new FileWriter(f);
        BufferedWriter bw = new BufferedWriter(fw);

        Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);
        Sheet[] sheets = arquivo.getSheets();

        try {
            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;

                for (int i = 0; i < sheet.getRows(); i++) {
                    linha++;

                    if (linha == 1) {
                        continue;
                    } else if (sheet.getCell(0, i).getContents().isEmpty()) { //ignora linha em branco
                        continue;
                    }

                    Cell cellCodigoProduto = sheet.getCell(0, i);
                    Cell cellCodigoBarras = sheet.getCell(1, i);
                    Cell cellDescricaoProduto = sheet.getCell(2, i);
                    Cell cellCustoMedio = sheet.getCell(5, i);
                    Cell cellQuantidade = sheet.getCell(6, i);
                    Cell cellValorTotal = sheet.getCell(7, i);

                    InventarioVO oInventario = new InventarioVO();
                    oInventario.setData(data);
                    oInventario.setDataGeracao(data);

                    if (cellCodigoBarras.getContents().trim().length() < 7) {
                        oInventario.setIdProduto(new ProdutoDAO().getIdProdutoCodigoAnterior(Long.parseLong(cellCodigoProduto.getContents().trim())));

                        if (oInventario.getIdProduto() == -1) {
                            bw.write("CODIGO PRODUTO: " + cellCodigoProduto.getContents() + " - " + cellDescricaoProduto.getContents() + ";"
                                    +cellCustoMedio.getContents()+";"+ cellQuantidade.getContents() + ";" + cellValorTotal.getContents());
                            bw.newLine();
                        }
                    } else {
                        oInventario.setIdProduto(new ProdutoDAO().getIdProdutoCodigoBarras(Long.parseLong(cellCodigoBarras.getContents().trim())));

                        if (oInventario.getIdProduto() == -1) {
                            bw.write("CODIGO BARRAS: " + cellCodigoBarras.getContents() + " - " + cellDescricaoProduto.getContents() + ";"
                                    +cellCustoMedio.getContents()+";"+ cellQuantidade.getContents() + ";" + cellValorTotal.getContents());
                            bw.newLine();
                        }
                    }
                    
                    oInventario.setQuantidade(Double.parseDouble(cellQuantidade.getContents().trim().replace(",", ".")));
                    oInventario.setCustoMedioComImposto(Double.parseDouble(cellCustoMedio.getContents().trim().replace(",", ".")));
                    oInventario.setCustoMedioSemImposto(Double.parseDouble(cellCustoMedio.getContents().trim().replace(",", ".")));
                    oInventario.setCustoSemImposto(oInventario.getCustoMedioSemImposto());
                    oInventario.setIdLoja(idLojaVR);
                    result.add(oInventario);
                }
            }
            bw.flush();
            bw.close();
            return result;
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    public List<InventarioVO> carregarInventarioComplementoLoja10(String i_arquivo, int idLojaVR, String pathLog, String data)
            throws Exception {
        int linha;
        long codigoAnterior;
        List<InventarioVO> result = new ArrayList<>();
        WorkbookSettings settings = new WorkbookSettings();
        settings.setEncoding("CP1252");
        File f = new File(pathLog + "\\produtosNAOencontradoComplemento"+idLojaVR+".txt");
        FileWriter fw = new FileWriter(f);
        BufferedWriter bw = new BufferedWriter(fw);

        Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);
        Sheet[] sheets = arquivo.getSheets();

        try {
            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;

                for (int i = 0; i < sheet.getRows(); i++) {
                    linha++;
                    codigoAnterior = 0;
                    
                    if (linha == 1) {
                        continue;
                    } else if (sheet.getCell(0, i).getContents().isEmpty()) { //ignora linha em branco
                        continue;
                    } else if (sheet.getCell(4, i).getContents().contains("INVENTARIO")) {
                        continue;
                    }

                    Cell cellProduto = sheet.getCell(0, i);                    
                    Cell cellCustoMedio = sheet.getCell(1, i);
                    Cell cellQuantidade = sheet.getCell(2, i);
                    Cell cellValorTotal = sheet.getCell(3, i);
                    Cell cellCodigoProduto = sheet.getCell(4, i);

                    if (cellProduto.getContents().contains("CODIGO PRODUTO:")) {
                        codigoAnterior = Long.parseLong(cellProduto.getContents().trim().substring(16, 
                                cellProduto.getContents().trim().indexOf(" - ")));
                    }
                    
                    InventarioVO oInventario = new InventarioVO();
                    oInventario.setData(data);
                    oInventario.setDataGeracao(data);
                    oInventario.setIdProduto(new ProdutoDAO().getId(Integer.parseInt(cellCodigoProduto.getContents().trim())));
                    oInventario.setCodigoAnterior(codigoAnterior);
                    oInventario.setQuantidade(Double.parseDouble(cellQuantidade.getContents().trim().replace(",", ".")));
                    oInventario.setCustoMedioComImposto(Double.parseDouble(cellCustoMedio.getContents().trim().replace(",", ".")));
                    oInventario.setCustoMedioSemImposto(Double.parseDouble(cellCustoMedio.getContents().trim().replace(",", ".")));
                    oInventario.setCustoSemImposto(oInventario.getCustoMedioSemImposto());
                    oInventario.setIdLoja(idLojaVR);
                    result.add(oInventario);
                }
            }
            bw.flush();
            bw.close();
            return result;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public List<InventarioVO> carregarInventarioComplementoLoja7(String i_arquivo, int idLojaVR, String pathLog, String data)
            throws Exception {
        int linha;
        long codigoAnterior;
        List<InventarioVO> result = new ArrayList<>();
        WorkbookSettings settings = new WorkbookSettings();
        settings.setEncoding("CP1252");
        File f = new File(pathLog + "\\produtosNAOencontradoComplemento"+idLojaVR+".txt");
        FileWriter fw = new FileWriter(f);
        BufferedWriter bw = new BufferedWriter(fw);

        Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);
        Sheet[] sheets = arquivo.getSheets();

        try {
            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;

                for (int i = 0; i < sheet.getRows(); i++) {
                    linha++;
                    codigoAnterior = 0;
                    
                    if (linha == 1) {
                        continue;
                    } else if (sheet.getCell(0, i).getContents().isEmpty()) { //ignora linha em branco
                        continue;
                    } else if (sheet.getCell(4, i).getContents().contains("INVENTARIO")) {
                        continue;
                    }

                    Cell cellProduto = sheet.getCell(0, i);                    
                    Cell cellCustoMedio = sheet.getCell(1, i);
                    Cell cellQuantidade = sheet.getCell(2, i);
                    Cell cellValorTotal = sheet.getCell(3, i);
                    Cell cellCodigoProduto = sheet.getCell(4, i);

                    if (cellProduto.getContents().contains("CODIGO PRODUTO:")) {
                        codigoAnterior = Long.parseLong(cellProduto.getContents().trim().substring(16, 
                                cellProduto.getContents().trim().indexOf(" - ")));
                    }
                    
                    InventarioVO oInventario = new InventarioVO();
                    oInventario.setData(data);
                    oInventario.setDataGeracao(data);
                    oInventario.setIdProduto(new ProdutoDAO().getId(Integer.parseInt(cellCodigoProduto.getContents().trim())));
                    oInventario.setCodigoAnterior(codigoAnterior);
                    oInventario.setQuantidade(Double.parseDouble(cellQuantidade.getContents().trim().replace(",", ".")));
                    oInventario.setCustoMedioComImposto(Double.parseDouble(cellCustoMedio.getContents().trim().replace(",", ".")));
                    oInventario.setCustoMedioSemImposto(Double.parseDouble(cellCustoMedio.getContents().trim().replace(",", ".")));
                    oInventario.setCustoSemImposto(oInventario.getCustoMedioSemImposto());
                    oInventario.setIdLoja(idLojaVR);
                    result.add(oInventario);
                }
            }
            bw.flush();
            bw.close();
            return result;
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    public void importarInventario(String i_arquivo, int idLojaVR, String pathLog, String data, int opcao) throws Exception {
        List<InventarioVO> vInventario = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Inventário Loja " + idLojaVR);
            
            if (opcao == 0) {
                if (idLojaVR == 10) {
                    vInventario = carregarInventarioLoja10(i_arquivo, idLojaVR, pathLog, data);
                } else if (idLojaVR == 7) {
                    vInventario = carregarInventarioLoja7(i_arquivo, idLojaVR, pathLog, data);
                }
            } else {
                if (idLojaVR == 10) {
                    vInventario = carregarInventarioComplementoLoja10(i_arquivo, idLojaVR, pathLog, data);
                } else if (idLojaVR == 7) {
                    vInventario = carregarInventarioComplementoLoja7(i_arquivo, idLojaVR, pathLog, data);
                }
            }            
            
            new InventarioDAO().salvar(vInventario, idLojaVR);
        } catch (Exception ex) {
            throw ex;
        }
    }
}
