package vrimplantacao2.utils.arquivo.planilha;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import vrimplantacao2.utils.arquivo.AbstractArquivo;
import vrimplantacao2.utils.arquivo.DefaultLinha;

public class Planilha extends AbstractArquivo {

    public Planilha(String arquivo, String dateFormat, String timeFormat) throws Exception {
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
        
        SimpleDateFormat sDate = new SimpleDateFormat(dateFormat);
        SimpleDateFormat sTime = new SimpleDateFormat(timeFormat);
        
        //Obtem os dados.
        for (int i = 1; i < sheet.getRows(); i++) {
            DefaultLinha linha = new DefaultLinha(sDate, sTime);
            Cell[] columns = sheet.getRow(i);
            for (int j = 0; j < columns.length; j++) {
                String contents = columns[j].getContents();
                if (columns[j] instanceof jxl.ErrorCell) {
                    linha.putString(cabecalho.get(j), "");
                } else {
                    linha.putString(cabecalho.get(j), contents);
                }
            }
            for (int j = columns.length; j < cabecalho.size(); j++) {
                linha.putString(cabecalho.get(j), "");
            }
            dados.add(linha);
        }         
    }

}
