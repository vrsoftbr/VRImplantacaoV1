/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2_5.relatorios.gerador;

import java.io.File;
import java.io.PrintWriter;
import vrimplantacao2_5.relatorios.utils.Formatador;
import java.util.List;
import javax.swing.JOptionPane;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.openide.util.Exceptions;
import vrimplantacao2_5.relatorios.relatoriosDAO.RelatorioCestFaltandoDAO;
import vrimplantacao2_5.relatorios.vo.CestFaltandoVO;

/**
 *
 * @author Michael
 */
public class GeradorCest {

    public List<CestFaltandoVO> carregarDadosCest() {
        List<CestFaltandoVO> cest = null;
        try {
            cest = new RelatorioCestFaltandoDAO().getCestFaltando();
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
        return cest;
    }

    public void geraPlanilhaCest(HSSFWorkbook workbook) throws Exception {
        HSSFSheet sheet = workbook.createSheet("CEST");
        sheet.setDefaultColumnWidth(15);
        sheet.setDefaultRowHeight((short) 400);

        List<CestFaltandoVO> cest = null;
        try {
            cest = carregarDadosCest();
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }

        int rownum = 0;
        int cellnum = 0;
        Cell cell;
        Row row;

        CellStyle headerStyle = new Formatador().formatarCelulaHeader(workbook);
        CellStyle textStyle = new Formatador().formatarCelulaText(workbook);
        CellStyle numberStyle = new Formatador().formatarCelulaNumber(workbook);

        row = sheet.createRow(rownum++);
        cell = (Cell) row.createCell(cellnum++);
        cell.setCellStyle(headerStyle);
        cell.setCellValue("Cest");

        cell = row.createCell(cellnum++);
        cell.setCellStyle(headerStyle);
        cell.setCellValue("Quantidade");

        for (CestFaltandoVO c : cest) {
            row = sheet.createRow(rownum++);
            cellnum = 0;

            cell = row.createCell(cellnum++);
            cell.setCellStyle(textStyle);
            cell.setCellValue(c.getCest());

            cell = row.createCell(cellnum++);
            cell.setCellStyle(numberStyle);
            cell.setCellValue(c.getQtd());
        }
        gerarCestTxt();
    }

    public void gerarCestTxt() {
        try {
            File f = new File("/vr/implantacao/planilhas/Cest.txt");
            PrintWriter printWriter = new PrintWriter(f);
            List<CestFaltandoVO> cest = null;
            try {
                cest = carregarDadosCest();
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
            printWriter.println("|        Cest       |      QTD      |");
            printWriter.println("|-------------------|---------------|");
            String espacos = "----------------";
            String espacosQ = "--------------";
            for (CestFaltandoVO c : cest) {
                String cestDesign = c.getCest().trim() + espacos.substring(c.getCest().trim().length()).replace("-", " ");
                String qtdDesign = c.getQtd().trim() + espacosQ.substring(c.getQtd().trim().length()).replace("-", " ");
                printWriter.print("| " + cestDesign +
                                    "  | " + qtdDesign);
                    printWriter.println("|");

            }
            printWriter.flush();
            printWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,"Erro em GeradorCest\n" 
                    + "Entre em contato com o setor de migração e reporte esse erro\n\n"
                    + e, "Relatórios", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
