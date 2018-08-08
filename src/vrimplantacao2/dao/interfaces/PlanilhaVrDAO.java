/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.interfaces;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import org.apache.commons.lang3.StringUtils;
import vrframework.classe.ProgressBar;
import vrimplantacao.dao.cadastro.ProdutoDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.ProdutoVO;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author lucasrafael
 */
public class PlanilhaVrDAO extends InterfaceDAO {

    public String v_arquivoXls;
    public String v_arquivoXlsProdAliq;

    @Override
    public String getSistema() {
        return "PLANILHA";
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        WorkbookSettings settings = new WorkbookSettings();
        Workbook arquivo = Workbook.getWorkbook(new File(v_arquivoXls), settings);
        Sheet[] sheets = arquivo.getSheets();
        int linha;

        for (int sh = 0; sh < sheets.length; sh++) {
            Sheet sheet = arquivo.getSheet(sh);
            linha = 0;

            for (int i = 0; i < sheet.getRows(); i++) {
                linha++;
                if (linha == 1) {
                    continue;
                }

                Cell cellCodigo = sheet.getCell(0, i);
                Cell cellCstIcmsSaida = sheet.getCell(2, i);
                Cell cellValorIcmsSaida = sheet.getCell(3, i);
                Cell cellReduIcmsSaida = sheet.getCell(4, i);
                Cell cellCstIcmsEntrada = sheet.getCell(5, i);
                Cell cellValorIcmsEntrada = sheet.getCell(6, i);
                Cell cellRedIcmsEntrada = sheet.getCell(7, i);
                Cell cellCstPisDebito = sheet.getCell(8, i);
                Cell cellCstPisCredito = sheet.getCell(9, i);
                Cell cellNaturezaReceita = sheet.getCell(10, i);
                Cell cellNcm1 = sheet.getCell(11, i);
                Cell cellNcm2 = sheet.getCell(12, i);
                Cell cellNcm3 = sheet.getCell(13, i);
                Cell cellCest1 = sheet.getCell(14, i);
                Cell cellCest2 = sheet.getCell(15, i);
                Cell cellCest3 = sheet.getCell(16, i);

                ProdutoIMP imp = new ProdutoIMP();
                imp.setImportLoja(getLojaOrigem());
                imp.setImportSistema(getSistema());
                imp.setImportId(cellCodigo.getContents());
                imp.setNcm(StringUtils.leftPad(cellNcm1.getContents(), 4, "0") + StringUtils.leftPad(cellNcm2.getContents(), 2, "0") + StringUtils.leftPad(cellNcm3.getContents(), 2, "0"));
                imp.setCest(StringUtils.leftPad(cellCest1.getContents(), 2, "0") + StringUtils.leftPad(cellCest2.getContents(), 3, "0") + StringUtils.leftPad(cellCest3.getContents(), 2, "0"));
                imp.setPiscofinsCstDebito(cellCstPisDebito.getContents());
                imp.setPiscofinsCstCredito(cellCstPisCredito.getContents());
                imp.setPiscofinsNaturezaReceita(cellNaturezaReceita.getContents());
                imp.setIcmsCstSaida(Utils.stringToInt(cellCstIcmsSaida.getContents()));
                imp.setIcmsAliqSaida(Utils.stringToDouble(cellValorIcmsSaida.getContents()));
                imp.setIcmsReducaoSaida(Utils.stringToDouble(cellReduIcmsSaida.getContents()));
                imp.setIcmsCstEntrada(Utils.stringToInt(cellCstIcmsEntrada.getContents()));
                imp.setIcmsAliqEntrada(Utils.stringToDouble(cellValorIcmsEntrada.getContents()));
                imp.setIcmsReducaoEntrada(Utils.stringToDouble(cellRedIcmsEntrada.getContents()));

                result.add(imp);
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos(OpcaoProduto opcao) throws Exception {

        if (opcao == OpcaoProduto.PIS_COFINS) {
            List<ProdutoIMP> result = new ArrayList<>();
            WorkbookSettings settings = new WorkbookSettings();
            Workbook arquivo = Workbook.getWorkbook(new File(v_arquivoXls), settings);
            Sheet[] sheets = arquivo.getSheets();
            int linha;

            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;

                for (int i = 0; i < sheet.getRows(); i++) {
                    linha++;
                    if (linha == 1) {
                        continue;
                    }

                    Cell cellCodigo = sheet.getCell(0, i);
                    Cell cellCstPisDebito = sheet.getCell(8, i);
                    Cell cellCstPisCredito = sheet.getCell(9, i);

                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(cellCodigo.getContents());
                    imp.setPiscofinsCstDebito(cellCstPisDebito.getContents());
                    imp.setPiscofinsCstCredito(cellCstPisCredito.getContents());
                    result.add(imp);
                }
            }
            return result;
        } else if (opcao == OpcaoProduto.NATUREZA_RECEITA) {
            List<ProdutoIMP> result = new ArrayList<>();
            WorkbookSettings settings = new WorkbookSettings();
            Workbook arquivo = Workbook.getWorkbook(new File(v_arquivoXls), settings);
            Sheet[] sheets = arquivo.getSheets();
            int linha;

            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;

                for (int i = 0; i < sheet.getRows(); i++) {
                    linha++;
                    if (linha == 1) {
                        continue;
                    }

                    Cell cellCodigo = sheet.getCell(0, i);
                    Cell cellNaturezaReceita = sheet.getCell(10, i);

                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(cellCodigo.getContents());
                    imp.setPiscofinsNaturezaReceita(cellNaturezaReceita.getContents());
                    result.add(imp);
                }
            }
            return result;
        } else if (opcao == OpcaoProduto.NCM) {
            List<ProdutoIMP> result = new ArrayList<>();
            WorkbookSettings settings = new WorkbookSettings();
            Workbook arquivo = Workbook.getWorkbook(new File(v_arquivoXls), settings);
            Sheet[] sheets = arquivo.getSheets();
            int linha;

            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;

                for (int i = 0; i < sheet.getRows(); i++) {
                    linha++;
                    if (linha == 1) {
                        continue;
                    }

                    Cell cellCodigo = sheet.getCell(0, i);
                    Cell cellNcm1 = sheet.getCell(11, i);
                    Cell cellNcm2 = sheet.getCell(12, i);
                    Cell cellNcm3 = sheet.getCell(13, i);

                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(cellCodigo.getContents());
                    imp.setNcm(cellNcm1.getContents() + cellNcm2.getContents() + cellNcm3.getContents());
                    result.add(imp);
                }
            }
            return result;
        } else if (opcao == OpcaoProduto.CEST) {
            List<ProdutoIMP> result = new ArrayList<>();
            WorkbookSettings settings = new WorkbookSettings();
            Workbook arquivo = Workbook.getWorkbook(new File(v_arquivoXls), settings);
            Sheet[] sheets = arquivo.getSheets();
            int linha;

            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;

                for (int i = 0; i < sheet.getRows(); i++) {
                    linha++;
                    if (linha == 1) {
                        continue;
                    }

                    Cell cellCodigo = sheet.getCell(0, i);
                    Cell cellCest1 = sheet.getCell(14, i);
                    Cell cellCest2 = sheet.getCell(15, i);
                    Cell cellCest3 = sheet.getCell(16, i);

                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(cellCodigo.getContents());
                    imp.setCest(cellCest1.getContents() + cellCest2.getContents() + cellCest3.getContents());
                    result.add(imp);
                }
            }
            return result;
        } else if (opcao == OpcaoProduto.ICMS_SAIDA) {
            List<ProdutoIMP> result = new ArrayList<>();
            WorkbookSettings settings = new WorkbookSettings();
            Workbook arquivo = Workbook.getWorkbook(new File(v_arquivoXls), settings);
            Sheet[] sheets = arquivo.getSheets();
            int linha;

            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;

                for (int i = 0; i < sheet.getRows(); i++) {
                    linha++;
                    if (linha == 1) {
                        continue;
                    }

                    Cell cellCodigo = sheet.getCell(0, i);
                    Cell cellCstIcmsSaida = sheet.getCell(2, i);
                    Cell cellValorIcmsSaida = sheet.getCell(3, i);
                    Cell cellReduIcmsSaida = sheet.getCell(4, i);

                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(cellCodigo.getContents());
                    imp.setIcmsCstSaida(Utils.stringToInt(cellCstIcmsSaida.getContents()));
                    imp.setIcmsAliqSaida(Utils.stringToDouble(cellValorIcmsSaida.getContents()));
                    imp.setIcmsReducaoSaida(Utils.stringToDouble(cellReduIcmsSaida.getContents()));
                    result.add(imp);
                }
            }
            return result;
        } else if (opcao == OpcaoProduto.ICMS_ENTRADA) {
            List<ProdutoIMP> result = new ArrayList<>();
            WorkbookSettings settings = new WorkbookSettings();
            Workbook arquivo = Workbook.getWorkbook(new File(v_arquivoXls), settings);
            Sheet[] sheets = arquivo.getSheets();
            int linha;

            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;

                for (int i = 0; i < sheet.getRows(); i++) {
                    linha++;
                    if (linha == 1) {
                        continue;
                    }

                    Cell cellCodigo = sheet.getCell(0, i);
                    Cell cellCstIcmsEntrada = sheet.getCell(5, i);
                    Cell cellValorIcmsEntrada = sheet.getCell(6, i);
                    Cell cellRedIcmsEntrada = sheet.getCell(7, i);

                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(cellCodigo.getContents());
                    imp.setIcmsCstEntrada(Utils.stringToInt(cellCstIcmsEntrada.getContents()));
                    imp.setIcmsAliqEntrada(Utils.stringToDouble(cellValorIcmsEntrada.getContents()));
                    imp.setIcmsReducaoEntrada(Utils.stringToDouble(cellRedIcmsEntrada.getContents()));
                    result.add(imp);
                }
            }
            return result;
        } else if (opcao == OpcaoProduto.ICMS) {
            List<ProdutoIMP> result = new ArrayList<>();
            WorkbookSettings settings = new WorkbookSettings();
            Workbook arquivo = Workbook.getWorkbook(new File(v_arquivoXlsProdAliq), settings);
            Sheet[] sheets = arquivo.getSheets();
            int linha;

            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;

                for (int i = 0; i < sheet.getRows(); i++) {
                    linha++;
                    if (linha == 1) {
                        continue;
                    }

                    Cell cellCodigo = sheet.getCell(0, i);
                    Cell cellCstIcmsSaida = sheet.getCell(2, i);
                    Cell cellValorIcmsSaida = sheet.getCell(3, i);
                    Cell cellReduIcmsSaida = sheet.getCell(4, i);
                    Cell cellCstIcmsEntrada = sheet.getCell(5, i);
                    Cell cellValorIcmsEntrada = sheet.getCell(6, i);
                    Cell cellRedIcmsEntrada = sheet.getCell(7, i);
                    Cell cellCstIcmsSaidaForaEstado = sheet.getCell(8, i);
                    Cell cellValorIcmsSaidaForaEstado = sheet.getCell(9, i);
                    Cell cellReduIcmsSaidaForaEstado = sheet.getCell(10, i);
                    Cell cellCstIcmsEntradaForaEstado = sheet.getCell(11, i);
                    Cell cellValorIcmsEntradaForaEstado = sheet.getCell(12, i);
                    Cell cellRedIcmsEntradaForaEstado = sheet.getCell(13, i);
                    Cell cellCstIcmsSaidaForaEstadoNF = sheet.getCell(14, i);
                    Cell cellValorIcmsSaidaForaEstadoNF = sheet.getCell(15, i);
                    Cell cellReduIcmsSaidaForaEstadoNF = sheet.getCell(16, i);

                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(cellCodigo.getContents());
                    imp.setIcmsCstSaida(Utils.stringToInt(cellCstIcmsSaida.getContents()));
                    imp.setIcmsAliqSaida(Utils.stringToDouble(cellValorIcmsSaida.getContents()));
                    imp.setIcmsReducaoSaida(Utils.stringToDouble(cellReduIcmsSaida.getContents()));                    
                    imp.setIcmsCstEntrada(Utils.stringToInt(cellCstIcmsEntrada.getContents()));
                    imp.setIcmsAliqEntrada(Utils.stringToDouble(cellValorIcmsEntrada.getContents()));
                    imp.setIcmsReducaoEntrada(Utils.stringToDouble(cellRedIcmsEntrada.getContents()));
                    imp.setIcmsCstSaidaForaEstado(Utils.stringToInt(cellCstIcmsSaidaForaEstado.getContents()));
                    imp.setIcmsAliqSaidaForaEstado(Utils.stringToDouble(cellValorIcmsSaidaForaEstado.getContents()));
                    imp.setIcmsReducaoSaidaForaEstado(Utils.stringToDouble(cellReduIcmsSaidaForaEstado.getContents()));                    
                    imp.setIcmsCstEntradaForaEstado(Utils.stringToInt(cellCstIcmsEntradaForaEstado.getContents()));
                    imp.setIcmsAliqEntradaForaEstado(Utils.stringToDouble(cellValorIcmsEntradaForaEstado.getContents()));
                    imp.setIcmsReducaoEntradaForaEstado(Utils.stringToDouble(cellRedIcmsEntradaForaEstado.getContents()));
                    imp.setIcmsCstSaidaForaEstadoNF(Utils.stringToInt(cellCstIcmsSaidaForaEstadoNF.getContents()));
                    imp.setIcmsAliqSaidaForaEstadoNF(Utils.stringToDouble(cellValorIcmsSaidaForaEstadoNF.getContents()));
                    imp.setIcmsReducaoSaidaForaEstadoNF(Utils.stringToDouble(cellReduIcmsSaidaForaEstadoNF.getContents()));                    
                    
                    result.add(imp);
                }
            }
            return result;
        }
        return null;
    }
    
    private List<ProdutoVO> getProdutoNcmCest() throws Exception {
        List<ProdutoVO> result = new ArrayList();
        WorkbookSettings settings = new WorkbookSettings();
        Workbook arquivo = Workbook.getWorkbook(new File(v_arquivoXls), settings);
        Sheet[] sheets = arquivo.getSheets();
        int linha;

        for (int sh = 0; sh < sheets.length; sh++) {
            Sheet sheet = arquivo.getSheet(sh);
            linha = 0;

            for (int i = 0; i < sheet.getRows(); i++) {
                linha++;
                if (linha == 1) {
                    continue;
                }

                Cell cellCodigo = sheet.getCell(0, i);
                Cell cellNcm1 = sheet.getCell(11, i);
                Cell cellNcm2 = sheet.getCell(12, i);
                Cell cellNcm3 = sheet.getCell(13, i);
                Cell cellCest1 = sheet.getCell(14, i);
                Cell cellCest2 = sheet.getCell(15, i);
                Cell cellCest3 = sheet.getCell(16, i);

                if ((cellNcm1.getContents() != null) && (!cellNcm1.getContents().trim().isEmpty())
                        && (cellNcm2.getContents() != null) && (!cellNcm2.getContents().trim().isEmpty())
                        && (cellNcm3.getContents() != null) && (!cellNcm3.getContents().trim().isEmpty())
                        && (cellCest1.getContents() != null) && (!cellCest1.getContents().trim().isEmpty())
                        && (cellCest2.getContents() != null) && (!cellCest2.getContents().trim().isEmpty())
                        && (cellCest3.getContents() != null) && (!cellCest3.getContents().trim().isEmpty())) {

                    ProdutoVO vo = new ProdutoVO();
                    vo.id = Integer.parseInt(cellCodigo.getContents());
                    vo.ncm1 = Integer.parseInt(cellNcm1.getContents());
                    vo.ncm2 = Integer.parseInt(cellNcm2.getContents());
                    vo.ncm3 = Integer.parseInt(cellNcm3.getContents());
                    vo.cest1 = Integer.parseInt(cellCest1.getContents());
                    vo.cest2 = Integer.parseInt(cellCest2.getContents());
                    vo.cest3 = Integer.parseInt(cellCest3.getContents());
                    result.add(vo);
                }
            }
        }
        return result;        
    }
    
    public void importarNCMCestProdutoVR() throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Código NCM x CEST...");
            vProduto = getProdutoNcmCest();
            if (!vProduto.isEmpty()) {
                new ProdutoDAO().adicionarNcmCestProdutoVR(vProduto);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }
}
