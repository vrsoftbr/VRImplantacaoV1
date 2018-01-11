/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao.dao.interfaces;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import vrframework.classe.ProgressBar;
import vrframework.classe.VRException;
import vrimplantacao.vo.vrimplantacao.NcmVO;
import vrimplantacao.utils.Utils;
import vrimplantacao.dao.cadastro.NcmDAO;

/**
 *
 * @author lucasrafael
 */
public class PlanilhaNCMDAO {
    
    private List<NcmVO> carregarNCM(String i_arquivo) throws Exception {
        List<NcmVO> vNcm = new ArrayList<>();
        WorkbookSettings settings = new WorkbookSettings();
        settings.setEncoding("CP1252");
        int linha = 0, ncm1 = 0, ncm2 = 0, ncm3 = 0, nivel = 0;
        String descricao = "", strNcm1 = "", strNcm2 = "", strNcm3 = "",
               strNcm = "";
        
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
                    } else if (sheet.getCell(0, i).getContents().isEmpty()) { //ignora linha em branco
                        continue;
                    }
                    
                    if (linha == 14) {
                        System.out.println("aqui");
                    }
                    
                    ncm1 = -1;
                    ncm2 = -1;
                    ncm3 = -1;
                    nivel = 0;
                    
                    Cell cellNcm = sheet.getCell(3, i);
                    Cell cellDescricao = sheet.getCell(4, i);

                    if (!cellNcm.getContents().contains("Ex")) {

                        if (cellNcm.getContents().trim().length() <= 5) {
                            nivel = 1;
                        } else if ((cellNcm.getContents().trim().length() > 5)
                                && (cellNcm.getContents().trim().length() < 8)) {
                            nivel = 2;
                        } else if (cellNcm.getContents().trim().length() >= 8) {
                            nivel = 3;
                        }

                        if (nivel == 1) {
                            ncm1 = Integer.parseInt(Utils.formataNumero(cellNcm.getContents().trim()));
                            ncm2 = -1;
                            ncm3 = -1;
                            descricao = Utils.acertarTexto(cellDescricao.getContents().replace("-", "").trim());
                        } else if (nivel == 2) {
                            strNcm1 = cellNcm.getContents().trim().substring(0, 4);

                            strNcm2 = cellNcm.getContents().trim().substring(cellNcm.getContents().trim().indexOf(".") + 1,
                                    cellNcm.getContents().trim().length());

                            ncm1 = Integer.parseInt(strNcm1);
                            ncm2 = Integer.parseInt(strNcm2);
                            ncm3 = -1;

                            descricao = Utils.acertarTexto(cellDescricao.getContents().replace("-", "").trim());
                        } else if (nivel == 3) {

                            if (cellNcm.getContents().trim().length() > 10) {
                                strNcm = cellNcm.getContents().trim().substring(0, 10);
                            } else {
                                strNcm = cellNcm.getContents().trim();
                            }

                            strNcm1 = strNcm.trim().substring(0, 4);
                            strNcm2 = strNcm.trim().substring(5, 7);

                            strNcm3 = strNcm.trim().substring(
                                    strNcm.trim().length() - 2);

                            ncm1 = Integer.parseInt(strNcm1);
                            ncm2 = Integer.parseInt(Utils.formataNumero(strNcm2));
                            ncm3 = Integer.parseInt(Utils.formataNumero(strNcm3));

                            descricao = Utils.acertarTexto(cellDescricao.getContents().replace("-", "").trim());
                        }

                        descricao = descricao.trim();

                        if (descricao.length() > 150) {
                            descricao = descricao.substring(0, 150);
                        }

                        if ((nivel == 1) || (nivel == 2) || (nivel == 3)) {
                            NcmVO oNcm = new NcmVO();
                            oNcm.ncm1 = ncm1;
                            oNcm.ncm2 = ncm2;
                            oNcm.ncm3 = ncm3;
                            oNcm.descricao = descricao;
                            oNcm.nivel = nivel;
                            oNcm.strNcm = strNcm;
                            vNcm.add(oNcm);
                        }
                    }
                }
            }
           
            return vNcm;
        } catch (Exception ex) {
            if (linha > 0) {
                throw new VRException("Linha " + linha + ": " + ex.getMessage());
            } else {
                throw ex;
            }
        }
    }
    
    public void importarNCM(String i_arquivo) throws Exception {
        List<NcmVO> v_ncm = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Tabela de NCM...");
            v_ncm = carregarNCM(i_arquivo);
            
            if (!v_ncm.isEmpty()) {
                NcmDAO ncmDAO = new NcmDAO();
                ncmDAO.salvar(v_ncm);
            }
        } catch(Exception ex) {
            throw ex;
        }
    }
}