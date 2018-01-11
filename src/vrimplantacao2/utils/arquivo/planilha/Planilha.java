package vrimplantacao2.utils.arquivo.planilha;

import java.io.File;
import java.util.ArrayList;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import vrimplantacao2.utils.arquivo.AbstractArquivo;
import vrimplantacao2.utils.arquivo.DefaultLinha;

public class Planilha extends AbstractArquivo {

    public Planilha(String arquivo) throws Exception {
        WorkbookSettings settings = new WorkbookSettings();
        settings.setEncoding("CP1250");
        
        Workbook planilha = Workbook.getWorkbook(new File(arquivo), settings);            
        Sheet sheet = planilha.getSheet(0);
        
        cabecalho = new ArrayList<>();
        dados = new ArrayList<>();
        
        //Obtem os cabeçalhos.
        for (Cell column: sheet.getRow(0)) {
            cabecalho.add(column.getContents());
        }
        
        //Obtem os dados.
        for (int i = 1; i < sheet.getRows(); i++) {
            DefaultLinha linha = new DefaultLinha();
            Cell[] columns = sheet.getRow(i);
            for (int j = 0; j < columns.length; j++) {
                linha.putString(cabecalho.get(j), columns[j].getContents());
            }
            dados.add(linha);
        }         
    }

}
