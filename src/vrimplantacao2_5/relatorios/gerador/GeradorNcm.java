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
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.openide.util.Exceptions;
import vrimplantacao2_5.relatorios.relatoriosDAO.RelatorioNcmFaltandoDAO;
import vrimplantacao2_5.relatorios.vo.NCMFaltandoVO;

/**
 *
 * @author Michael
 */
public class GeradorNcm {

    public List<NCMFaltandoVO> carregarDadosNcm() {
        List<NCMFaltandoVO> ncm = null;
        try {
            ncm = new RelatorioNcmFaltandoDAO().getNcmFaltando();
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
        return ncm;
    }

    public void geraPlanilhaNcm(HSSFWorkbook workbook) throws Exception {
        HSSFSheet sheet = workbook.createSheet("NCM");
        sheet.setDefaultColumnWidth(15);
        sheet.setDefaultRowHeight((short) 400);

        List<NCMFaltandoVO> ncm = null;
        try {
            ncm = carregarDadosNcm();
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
        cell.setCellValue("NCM");

        cell = row.createCell(cellnum++);
        cell.setCellStyle(headerStyle);
        cell.setCellValue("Quantidade");

        for (NCMFaltandoVO n : ncm) {
            row = sheet.createRow(rownum++);
            cellnum = 0;

            cell = row.createCell(cellnum++);
            cell.setCellStyle(textStyle);
            cell.setCellValue(n.getNcm());

            cell = row.createCell(cellnum++);
            cell.setCellStyle(numberStyle);
            cell.setCellValue(n.getQtd());
        }
        gerarNcmTxt();
    }

    public void gerarNcmTxt() {
        try {
            File f = new File("/vr/implantacao/planilhas/Ncm.txt");
            PrintWriter printWriter = new PrintWriter(f);
            List<NCMFaltandoVO> ncm = null;
            try {
                ncm = carregarDadosNcm();
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
            printWriter.println("|       NCM       |      QTD      |");
            printWriter.println("|-----------------|---------------|");
            String espacos = "----------------";
            String espacosQ = "--------------";
            for (NCMFaltandoVO n : ncm) {
                String ncmDesign = n.getNcm().trim() + espacos.substring(n.getNcm().trim().length()).replace("-", " ");
                String qtdDesign = n.getQtd().trim() + espacosQ.substring(n.getQtd().trim().length()).replace("-", " ");
                    printWriter.print("| " + ncmDesign +
                                      "| " + qtdDesign);
                    printWriter.println("|");

            }
            printWriter.flush();
            printWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,"Erro em GeradorNcm\n" 
                    + "Entre em contato com o setor de migração e reporte esse erro\n\n"
                    + e, "Relatórios", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
