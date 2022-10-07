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
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.openide.util.Exceptions;
import vrimplantacao2_5.relatorios.relatoriosDAO.RelatorioCodigoBalancaAlteradoDAO;
import vrimplantacao2_5.relatorios.vo.CodBalAlteradoVO;

/**
 *
 * @author Michael
 */
public class GeradorBalanca {

    public List<CodBalAlteradoVO> carregarDadosBalanca() {
        List<CodBalAlteradoVO> bal = null;
        try {
            bal = new RelatorioCodigoBalancaAlteradoDAO().getCodBalFaltando();
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
        return bal;
    }

    public void geraPlanilhaBalanca(HSSFWorkbook workbook) throws Exception {
        HSSFSheet sheet = workbook.createSheet("COD_BAL");
        sheet.setDefaultColumnWidth(20);
        sheet.setDefaultRowHeight((short) 400);

        List<CodBalAlteradoVO> bal = null;
        try {
            bal = carregarDadosBalanca();
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
        cell.setCellValue("Código Antigo");

        cell = row.createCell(cellnum++);
        cell.setCellStyle(headerStyle);
        cell.setCellValue("Descrição");

        cell = row.createCell(cellnum++);
        cell.setCellStyle(headerStyle);
        cell.setCellValue("Código de Barras");

        cell = row.createCell(cellnum++);
        cell.setCellStyle(headerStyle);
        cell.setCellValue("Código Atual");

        for (CodBalAlteradoVO b : bal) {
            row = sheet.createRow(rownum++);
            cellnum = 0;

            cell = row.createCell(cellnum++);
            cell.setCellStyle(numberStyle);
            cell.setCellValue(b.getId());

            cell = row.createCell(cellnum++);
            cell.setCellStyle(textStyle);
            if(b.getDescricao().length() > 15) {
            cell.setCellValue(b.getDescricao().substring(0, 15));
            } else cell.setCellValue(b.getDescricao());

            cell = row.createCell(cellnum++);
            cell.setCellStyle(textStyle);
            cell.setCellValue(b.getEan());

            cell = row.createCell(cellnum++);
            cell.setCellStyle(numberStyle);
            cell.setCellValue(b.getCodigoAtual());

        }
        gerarBalancaTxt();
    }

    private void gerarBalancaTxt() {
        try {
            File f = new File("/vr/implantacao/planilhas/Cod_Bal.txt");
            PrintWriter printWriter = new PrintWriter(f);
            List<CodBalAlteradoVO> bal = null;
            try {
                bal = carregarDadosBalanca();
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
            printWriter.println("|  Código Antigo |      Descricao      |       EAN      |  Código Atual  |");
            printWriter.println("|----------------|---------------------|----------------|----------------|");
            String espacos = "----------------";
            String espacosD = "--------------------";
            for (CodBalAlteradoVO b : bal) {
                if (b.getDescricao().length() < 15){
                    printWriter.print(
                       "|" + b.getId().trim() + espacos.substring(b.getId().trim().length()).replace("-", " ") +
                       "| " + b.getDescricao().trim() + espacosD.substring(b.getDescricao().trim().length()).replace("-", " ") +
                       "|" + b.getEan().trim() + espacos.substring(b.getEan().trim().length()).replace("-", " ") + 
                       "|" + b.getCodigoAtual().trim() + espacos.substring(b.getCodigoAtual().trim().length()).replace("-", " "));
                    printWriter.println("|");
                } else {
               printWriter.print(
                       "|" + b.getId().trim() + espacos.substring(b.getId().trim().length()).replace("-", " ") +
                       "| " + b.getDescricao().trim().substring(0,15) + espacosD.substring(b.getDescricao().trim().substring(0,15).length()).replace("-", " ") +
                       "|" + b.getEan().trim() + espacos.substring(b.getEan().trim().length()).replace("-", " ") + 
                       "|" + b.getCodigoAtual().trim() + espacos.substring(b.getCodigoAtual().trim().length()).replace("-", " "));
                printWriter.println("|");
                }
            }
            printWriter.flush();
            printWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
