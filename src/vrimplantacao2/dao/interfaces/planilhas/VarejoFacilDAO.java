
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.interfaces.planilhas;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import vrframework.classe.ProgressBar;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.vo.enums.TipoEmpresa;
import vrimplantacao2.vo.enums.TipoFornecedor;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;

/**
 *
 * @author Alan
 */
public class VarejoFacilDAO extends InterfaceDAO {

    private String planilhaCreditoRotativo;
    private String planilhaFornecedor;

    @Override
    public String getSistema() {
        return "VarejoFacil";
    }

    public void setPlanilhaCreditoRotativo(String planilhaCreditoRotativo) {
        this.planilhaCreditoRotativo = planilhaCreditoRotativo;
    }
    
    public void setPlanilhaFornecedor(String planilhaFornecedor) {
        this.planilhaFornecedor = planilhaFornecedor;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        File file = new File(this.planilhaFornecedor);
        List<FornecedorIMP> result = new ArrayList<>();
        
        if (file.exists()) {
            WorkbookSettings settings = new WorkbookSettings();
            settings.setEncoding("CP1250");
            settings.setIgnoreBlanks(false);

            Workbook planilha = Workbook.getWorkbook(file, settings);
            Sheet sheet = planilha.getSheet(0);

            ProgressBar.setStatus("Analisando Planilha de Fornecedores");
            ProgressBar.setMaximum(sheet.getRows());
            
            int linha = 0;
            try {
                for (int i = 0; i < sheet.getRows(); i++) {
                    Cell[] cells = sheet.getRow(i);

                    linha++;

                    if (linha == 1) {
                        continue;
                    }
                    
                    Cell cellId = sheet.getCell(0, i);
                    Cell cellRazao = sheet.getCell(1, i);
                    Cell cellCnpj = sheet.getCell(2, i);
                    Cell cellTipo = sheet.getCell(3, i);
                    Cell cellReigime = sheet.getCell(4, i);
                    
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(cellId.getContents());
                    imp.setRazao(cellRazao.getContents());
                    imp.setFantasia(imp.getRazao());
                    imp.setCnpj_cpf(cellCnpj.getContents());
                    
                    if (cellReigime.getContents().startsWith("1")) {
                        imp.setTipoEmpresa(TipoEmpresa.LUCRO_REAL);
                    } else if (cellReigime.getContents().startsWith("4")) {
                        imp.setTipoEmpresa(TipoEmpresa.ME_SIMPLES);
                    } else {
                        imp.setTipoEmpresa(TipoEmpresa.LUCRO_REAL);
                    }
                    
                    if ("DISTRIBUIDORA".equals(cellTipo.getContents())) {
                        imp.setTipoFornecedor(TipoFornecedor.DISTRIBUIDOR);
                    } else if ("INDUSTRIA".equals(cellTipo.getContents())) {
                        imp.setTipoFornecedor(TipoFornecedor.INDUSTRIA);
                    } else if ("VAREJO".equals(cellTipo.getContents())) {
                        imp.setTipoFornecedor(TipoFornecedor.VAREJO);
                    } else {
                        imp.setTipoFornecedor(TipoFornecedor.INDUSTRIA);
                    }
                    
                    result.add(imp);
                    
                }
            } catch(Exception ex) {
                System.out.println(linha);
                throw ex;
            }
            return result;
        } else {
            throw new IOException("Planilha(s) não encontrada");
        }
    }
    
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        File file = new File(this.planilhaCreditoRotativo);
        List<CreditoRotativoIMP> result = new ArrayList<>();
        DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        java.sql.Date dataEmissao, dataVencimento;

        if (file.exists()) {
            WorkbookSettings settings = new WorkbookSettings();
            settings.setEncoding("CP1250");
            settings.setIgnoreBlanks(false);

            Workbook planilha = Workbook.getWorkbook(file, settings);
            Sheet sheet = planilha.getSheet(0);

            ProgressBar.setStatus("Analisando Planilha de Crédito Rotativo");
            ProgressBar.setMaximum(sheet.getRows());

            int linha = 0;
            try {
                for (int i = 0; i < sheet.getRows(); i++) {
                    Cell[] cells = sheet.getRow(i);

                    linha++;

                    if (linha == 1) {
                        continue;
                    }

                    Cell cellNumeroCupom = sheet.getCell(8, i);
                    Cell cellDataEmissao = sheet.getCell(10, i);
                    Cell cellDataVencimento = sheet.getCell(11, i);
                    Cell cellIdCliente = sheet.getCell(15, i);
                    Cell cellValor = sheet.getCell(18, i);
                    Cell cellSituacao = sheet.getCell(19, i);
                    Cell cellObs = sheet.getCell(21, i);
                    Cell cellJuros = sheet.getCell(25, i);

                    dataEmissao = new java.sql.Date(fmt.parse(cellDataEmissao.getContents()).getTime());
                    dataVencimento = new java.sql.Date(fmt.parse(cellDataVencimento.getContents()).getTime());

                    if ("A".equals(cellSituacao.getContents().trim())) {
                        CreditoRotativoIMP imp = new CreditoRotativoIMP();
                        
                        imp.setId(
                                cellNumeroCupom.getContents() + "-"
                                + cellDataEmissao.getContents().replace("/", "") + "-"
                                + cellDataVencimento.getContents().replace("/", "") + "-"
                                + cellIdCliente.getContents().trim());
                        
                        imp.setIdCliente(cellIdCliente.getContents().trim());
                        imp.setNumeroCupom(cellNumeroCupom.getContents());
                        imp.setDataEmissao(dataEmissao);
                        imp.setDataVencimento(dataVencimento);
                        imp.setValor(Double.parseDouble(cellValor.getContents().replace(".", "").replace(",", ".")));
                        imp.setJuros(Double.parseDouble(cellJuros.getContents().replace(".", "").replace(",", ".")));
                        imp.setObservacao(cellObs.getContents());
                        result.add(imp);
                    }
                }
            } catch (Exception ex) {
                System.out.println(linha);
                throw ex;
            }
            return result;
        } else {
            throw new IOException("Planilha(s) não encontrada");
        }
    }
}
