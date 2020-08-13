/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.interfaces.planilhas;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import vrframework.classe.ProgressBar;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.vo.importacao.ClienteIMP;
/**
 *
 * @author Lucas
 */
public class GenericDAO extends InterfaceDAO {

    private String planilhaCliente;
    
    public void setPlanilhaCliente(String planilhaCliente) {
        this.planilhaCliente = planilhaCliente;
    }
    
    @Override
    public String getSistema() {
        return "Generic";
    }
    
    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        File file = new File(this.planilhaCliente);
        List<ClienteIMP> result = new ArrayList<>();
        
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
                    
                    Cell cellNome = sheet.getCell(0, i);
                    Cell cellCpnj = sheet.getCell(1, i);
                    Cell cellObs = sheet.getCell(2, i);
                    
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(cellNome.getContents());
                    imp.setRazao(cellNome.getContents());
                    imp.setFantasia(imp.getRazao());
                    imp.setCnpj(cellCpnj.getContents());
                    imp.setObservacao(cellObs.getContents());
                    
                    
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
    
}
