package vrimplantacao.dao.interfaces;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import vrframework.classe.ProgressBar;
import vrframework.classe.VRException;
import vrimplantacao.dao.cadastro.ProdutoDAO;
import vrimplantacao.dao.cadastro.VendaDAO;
import vrimplantacao.vo.vrimplantacao.VendasVO;

public class PlanilhaVendasDAO {
    
    private static final Logger LOG = Logger.getLogger(PlanilhaVendasDAO.class.getName());

    public void migrarVendas(String i_arquivo, int i_idLojaDestino)
            throws Exception {
        List<VendasVO> vVenda = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados para importação...Vendas...");

            vVenda = carregarVendas(i_arquivo, i_idLojaDestino);

            ProgressBar.setMaximum(vVenda.size());
            VendaDAO vendaDAO = new VendaDAO();
            vendaDAO.salvar(vVenda, i_idLojaDestino);
        } catch (Exception e) {
            throw e;
        }
    }

    public void migrarVendasJJR(String i_arquivo, int i_idLojaDestino, String data)
            throws Exception {
        List<VendasVO> vVenda = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados para importação...Vendas JJR...");
            vVenda = carregarVendasJJR(i_arquivo, i_idLojaDestino, data);
            ProgressBar.setMaximum(vVenda.size());
            VendaDAO vendaDAO = new VendaDAO();
            vendaDAO.salvarJJR(vVenda, i_idLojaDestino);
        } catch (Exception e) {
            throw e;
        }
    }
    
    private List<VendasVO> carregarVendas(String i_arquivo, int idLoja) throws Exception {
        List<VendasVO> vVendas = new ArrayList<>();
        String data = "";
        double idProduto = 0, precoVenda = 0, custo = 0, quantidade = 0, valorTotal = 0;

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
                        }

                        Cell cellData = sheet.getCell(0, i);
                        Cell cellQuantidade = sheet.getCell(2, i);
                        Cell cellValorTotal = sheet.getCell(3, i);
                        Cell cellPrecoProduto = sheet.getCell(4, i);
                        Cell cellCustoProduto = sheet.getCell(5, i);
                        Cell cellIdProduto = sheet.getCell(6, i);

                        idProduto = Double.parseDouble(cellIdProduto.getContents().trim());
                        data = cellData.getContents().substring(6, 10);
                        data = data + "/" + cellData.getContents().substring(3, 5);
                        data = data + "/" + cellData.getContents().substring(0, 2);

                        if ((cellQuantidade.getContents() != null)
                                && (!cellQuantidade.getContents().trim().isEmpty())
                                && (!cellQuantidade.getContents().contains("NULL"))) {
                            quantidade = Double.parseDouble(cellQuantidade.getContents().replace(".", "").replace(",", "."));
                        } else {
                            quantidade = 0;
                        }

                        if ((cellPrecoProduto.getContents() != null)
                                && (!cellPrecoProduto.getContents().trim().isEmpty())
                                && (!cellPrecoProduto.getContents().contains("NULL"))) {
                            precoVenda = Math.round(Double.parseDouble(cellPrecoProduto.getContents().replace(".", "").replace(",", ".")));
                        } else {
                            precoVenda = 0;
                        }

                        if ((cellCustoProduto.getContents() != null)
                                && (!cellCustoProduto.getContents().trim().isEmpty())
                                && (!cellCustoProduto.getContents().contains("NULL"))) {
                            custo = Double.parseDouble(cellCustoProduto.getContents().replace(".", "").replace(",", "."));
                        } else {
                            custo = 0;
                        }

                        if ((cellValorTotal.getContents() != null)
                                && (!cellValorTotal.getContents().trim().isEmpty())
                                && (!cellValorTotal.getContents().contains("NULL"))) {
                            valorTotal = Double.parseDouble(cellValorTotal.getContents().replace(".", "").replace(",", "."));
                        } else {
                            valorTotal = 0;
                        }

                        VendasVO oVenda = new VendasVO();
                        oVenda.id_loja = idLoja;
                        oVenda.id_produto = idProduto;
                        oVenda.data = data;
                        oVenda.precovenda = precoVenda;
                        oVenda.quantidade = quantidade;
                        oVenda.valortotal = valorTotal;
                        oVenda.custocomimposto = custo;
                        oVenda.custosemimposto = custo;
                        vVendas.add(oVenda);
                    }
                }
                return vVendas;
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

    private List<VendasVO> carregarVendasJJR(String i_arquivo, int idLoja, String data) throws Exception {
        List<VendasVO> vVendas = new ArrayList<>();
        double custo = 0, quantidade = 0, valorTotal = 0;
        int idProduto;
        try {
            int linha = 0;

            WorkbookSettings settings = new WorkbookSettings();
            settings.setEncoding("CP1250");
            Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);
            Sheet[] sheets = arquivo.getSheets();

            try {
                SimpleDateFormat format = new SimpleDateFormat(data);
                for (int sh = 0; sh < sheets.length; sh++) {
                    Sheet sheet = arquivo.getSheet(sh);
                    linha = 0;

                    for (int i = 0; i < sheet.getRows(); i++) {
                        linha++;
                        //ignora o cabeçalho
                        if (linha == 1) {
                            continue;
                        }

                        Cell cellData = sheet.getCell(0, i);
                        Cell cellCodBarras = sheet.getCell(1, i);
                        Cell cellQuantidade = sheet.getCell(2, i);
                        Cell cellValorTotal = sheet.getCell(3, i);
                        Cell cellCustoProduto = sheet.getCell(5, i);

                        if ((cellQuantidade.getContents() != null)
                                && (!cellQuantidade.getContents().trim().isEmpty())
                                && (!cellQuantidade.getContents().contains("NULL"))) {
                            quantidade = Double.parseDouble(cellQuantidade.getContents().replace(".", "").replace(",", "."));
                        } else {
                            quantidade = 0;
                        }

                        if ((cellCustoProduto.getContents() != null)
                                && (!cellCustoProduto.getContents().trim().isEmpty())
                                && (!cellCustoProduto.getContents().contains("NULL"))) {
                            custo = Double.parseDouble(cellCustoProduto.getContents().replace(".", "").replace(",", "."));
                        } else {
                            custo = 0;
                        }

                        if ((cellValorTotal.getContents() != null)
                                && (!cellValorTotal.getContents().trim().isEmpty())
                                && (!cellValorTotal.getContents().contains("NULL"))) {
                            valorTotal = Double.parseDouble(cellValorTotal.getContents().replace(".", "").replace(",", "."));
                        } else {
                            valorTotal = 0;
                        }
                        
                        Date dt;
                        if ((cellData.getContents() != null)
                                && (!cellData.getContents().trim().isEmpty())
                                && (!cellData.getContents().contains("NULL"))) {
                            dt = format.parse(cellData.getContents());
                        } else {
                            dt = format.getCalendar().getTime();
                        }

                        idProduto = new ProdutoDAO().getIdByCodAntEan(cellCodBarras.getContents());
                        if (idProduto != -1) {
                            VendasVO oVenda = new VendasVO();
                            oVenda.id_loja = idLoja;
                            oVenda.id_produto = idProduto;
                            oVenda.data = format.format(dt);
                            oVenda.precovenda = (valorTotal / quantidade);
                            oVenda.quantidade = quantidade;
                            oVenda.valortotal = valorTotal;
                            oVenda.custocomimposto = (custo / quantidade);
                            oVenda.custosemimposto = (custo / quantidade);
                            vVendas.add(oVenda);
                        } else {
                            LOG.warning("Produto não localizado (" +
                                    cellCodBarras.getContents() + "," +
                                    cellData.getContents() + "," +
                                    cellQuantidade.getContents() + "," +
                                    cellValorTotal.getContents() + "," +
                                    cellCustoProduto.getContents()                                    
                                    + ")");
                        }
                    }
                }
                return vVendas;
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
}
