/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2_5.relatorios.utils;

import java.text.Normalizer;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;

/**
 *
 * @author Michael
 */
public class Formatador {

    public Formatador() {
    }

    public CellStyle formatarCelulaHeader(HSSFWorkbook workbook) {
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
        headerStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        headerStyle.setAlignment(CellStyle.ALIGN_CENTER);
        headerStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);

        return headerStyle;
    }

    public CellStyle formatarCelulaText(HSSFWorkbook workbook) {

        CellStyle textStyle = workbook.createCellStyle();
        textStyle.setAlignment(CellStyle.ALIGN_CENTER);
        textStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);

        return textStyle;
    }

    public CellStyle formatarCelulaNumber(HSSFWorkbook workbook) {
        HSSFDataFormat numberFormat = workbook.createDataFormat();

        CellStyle numberStyle = workbook.createCellStyle();
        numberStyle.setDataFormat(numberFormat.getFormat("“#,##0.00"));
        numberStyle.setAlignment(CellStyle.ALIGN_CENTER);
        numberStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);

        return numberStyle;
    }

    public static String removerAcentos(String texto) {
        texto = texto != null ? Normalizer.normalize(texto, Normalizer.Form.NFD) : "";
        texto = texto != null ? texto.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "") : "";
        texto = texto != null ? texto.replaceAll("�", "C") : "";
        texto = texto != null ? texto.replaceAll("[^\\p{ASCII}]", "") : "";

        return texto;
    }
}
