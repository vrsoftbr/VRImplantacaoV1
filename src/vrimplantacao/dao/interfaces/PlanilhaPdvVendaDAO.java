package vrimplantacao.dao.interfaces;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import vrframework.classe.ProgressBar;
import vrimplantacao.dao.cadastro.PdvVendaDAO;
import vrimplantacao.dao.cadastro.ProdutoDAO;
import vrimplantacao.vo.interfaces.ImportacaoLogVendaItemVO;
import vrimplantacao.vo.interfaces.ImportacaoLogVendaVO;

public class PlanilhaPdvVendaDAO {

    private List<ImportacaoLogVendaVO> carregarPdvVendaSttyllo(String i_arquivo) throws Exception {
        List<ImportacaoLogVendaVO> vPdvVenda = new ArrayList<>();
        WorkbookSettings settings = new WorkbookSettings();
        settings.setEncoding("CP1252");
        int linha = 0,
                idloja, numeroCupom, numeroEcf, idFinalizadora;
        double valTotal, valDesconto, valAcrescimo, valCancelado, valOpCaixa;
        String dataVenda, modeloEcf, numeroSerie, strQtd = "", itemNumeroSerie;
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
                    } else if (sheet.getCell(0, i).getContents().isEmpty()) { //ignora linha em branco
                        continue;
                    } else {

                        ImportacaoLogVendaVO oVenda = new ImportacaoLogVendaVO();
                        

                        if (sh == 0) {
                            Cell cellCodigoLoja = sheet.getCell(1, i);
                            Cell cellNumeroCupom = sheet.getCell(2, i);
                            Cell cellNumeroEcf = sheet.getCell(3, i);
                            Cell cellDataVenda = sheet.getCell(4, i);
                            Cell cellValorTotal = sheet.getCell(5, i);
                            Cell cellValorDesconto = sheet.getCell(6, i);
                            Cell cellValorAcrescimo = sheet.getCell(7, i);
                            Cell cellValorCancelado = sheet.getCell(8, i);
                            Cell cellNumeroSerieEcf = sheet.getCell(9, i);
                            Cell cellModeloEcf = sheet.getCell(10, i);
                            Cell cellFinalizadora = sheet.getCell(11, i);
                            Cell cellValOpCaixa = sheet.getCell(12, i);

                            idloja = Integer.parseInt(cellCodigoLoja.getContents());
                            numeroCupom = Integer.parseInt(cellNumeroCupom.getContents());
                            numeroEcf = Integer.parseInt(cellNumeroEcf.getContents());
                            dataVenda = cellDataVenda.getContents().substring(0, 10);
                            valTotal = Double.parseDouble(cellValorTotal.getContents());
                            valDesconto = Double.parseDouble(cellValorDesconto.getContents());
                            valAcrescimo = Double.parseDouble(cellValorAcrescimo.getContents());
                            valCancelado = Double.parseDouble(cellValorCancelado.getContents());
                            numeroSerie = cellNumeroSerieEcf.getContents();
                            modeloEcf = cellModeloEcf.getContents();
                            valOpCaixa = Double.parseDouble(cellValOpCaixa.getContents());

                            switch (cellFinalizadora.getContents()) {
                                case "DH":
                                    idFinalizadora = 1;
                                    break;
                                case "CT":
                                    idFinalizadora = 5;
                                    break;
                                case "CH":
                                    idFinalizadora = 2;
                                    break;
                                case "TK":
                                    idFinalizadora = 4;
                                    break;
                                default:
                                    idFinalizadora = 1;
                                    break;
                            }

                            oVenda.numeroCupom = numeroCupom;
                            oVenda.data = dataVenda.replace("-", "/");
                            oVenda.numeroSerie = numeroSerie;
                            oVenda.modeloImpressora = modeloEcf;
                            oVenda.valorDesconto = valDesconto;
                            oVenda.valorAcrescimo = valAcrescimo;
                            oVenda.ecf = numeroEcf;
                            itemNumeroSerie = oVenda.numeroSerie;
                            oVenda.pagina = sh;
                            oVenda.idFinalizadora = idFinalizadora;
                            oVenda.valorTotal = valTotal;

                        } else if (sh == 1) {   
                            ImportacaoLogVendaItemVO oItem = new ImportacaoLogVendaItemVO();
                            strQtd = "";
                            Cell cellCodigoLoja = sheet.getCell(0, i);
                            Cell cellNumeroEcf = sheet.getCell(1, i);
                            Cell cellNumeroCupom = sheet.getCell(2, i);
                            Cell cellNumeroItem = sheet.getCell(3, i);
                            Cell cellCodigoProduto = sheet.getCell(4, i);
                            Cell cellSiglaUnidade = sheet.getCell(5, i);
                            Cell cellQuantidade = sheet.getCell(6, i);
                            Cell cellPrecoProduto = sheet.getCell(7, i);
                            Cell cellValTotalProd = sheet.getCell(8, i);
                            Cell cellValCancItem = sheet.getCell(9, i);
                            Cell cellValorDesconto = sheet.getCell(10, i);
                            Cell cellCodigoBarras = sheet.getCell(11, i);
                            
                            oVenda.pagina = sh;
                            oItem.numeroCupom = Integer.parseInt(cellNumeroCupom.getContents());
                            oItem.numeroEcf = Integer.parseInt(cellNumeroEcf.getContents());
                            oItem.codigoAnterior = Long.parseLong(cellCodigoProduto.getContents());
                            oItem.codigoBarras = Long.parseLong(cellCodigoBarras.getContents());
                            oItem.sequencia = Integer.parseInt(cellNumeroItem.getContents());
                            oItem.numeroSerie = oVenda.numeroSerie;                            
                            
                            if (cellQuantidade.getContents().trim().length() == 9) {
                                for (int j = 0; j < cellQuantidade.getContents().trim().length(); j++) {
                                    if (j == 1) {
                                        strQtd = strQtd + "";
                                    } else {
                                        strQtd = strQtd + cellQuantidade.getContents().charAt(j);
                                    }
                                }
                                oItem.quantidade = Double.parseDouble(strQtd);
                            } else {
                                oItem.quantidade = Double.parseDouble(cellQuantidade.getContents());
                            }

                            oItem.precoVenda = Double.parseDouble(cellPrecoProduto.getContents());
                            oItem.valorDesconto = Double.parseDouble(cellValorDesconto.getContents());
                            oItem.valorTotal = (oItem.quantidade * Double.parseDouble(cellPrecoProduto.getContents()));
                            oItem.idProduto = new ProdutoDAO().getIdAnterior(oItem.codigoAnterior);
                            oItem.idAliquota = new ProdutoDAO().getAliquotaDebito(oItem.codigoAnterior);
                            oVenda.vLogVendaItem.add(oItem);
                        }
                        vPdvVenda.add(oVenda);
                    }
                }
            }

            return vPdvVenda;
        } catch (Exception ex) {
            //if (linha > 0) {
            //    throw new VRException("Linha " + linha + ": " + ex.getMessage());
            //} else {
                throw ex;
            //}
        }
    }

    public void importarPdvVendaSttyllo(String i_arquivo, int i_idLoja, 
            boolean i_exibeDivergenciaProduto, String numeroSerie) throws Exception {
        List<ImportacaoLogVendaVO> v_pdvVenda = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Pdv Venda...");
            v_pdvVenda = carregarPdvVendaSttyllo(i_arquivo);

            if (!v_pdvVenda.isEmpty()) {
                PdvVendaDAO pdvVendaDAO = new PdvVendaDAO();
                pdvVendaDAO.salvar(v_pdvVenda, i_idLoja, i_exibeDivergenciaProduto, numeroSerie);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }
}
