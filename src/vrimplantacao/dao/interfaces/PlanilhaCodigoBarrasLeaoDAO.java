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
import vrimplantacao.dao.cadastro.CodigoAnterior2DAO;
import vrimplantacao.dao.cadastro.LojaDAO;
import vrimplantacao.dao.cadastro.PagarOutrasDespesasDAO;
import vrimplantacao.dao.cadastro.ReceberCreditoRotativoDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao.vo.vrimplantacao.CodigoAnterior2VO;
import vrimplantacao.vo.vrimplantacao.PagarOutrasDespesasVO;
import vrimplantacao.vo.vrimplantacao.PagarOutrasDespesasVencimentoVO;
import vrimplantacao.vo.vrimplantacao.ReceberCreditoRotativoVO;

/**
 *
 * @author lucasrafael
 */
public class PlanilhaCodigoBarrasLeaoDAO {
    public void migrarCodigoAnterior2(String i_arquivo, int i_idLojaDestino) throws Exception {
        try {
            ProgressBar.setStatus("Carregando dados para comparação...");

            List<CodigoAnterior2VO> vCodigoAnterior2 = carregarCodigoAnterior2(i_arquivo);

            ProgressBar.setMaximum(vCodigoAnterior2.size());

            CodigoAnterior2DAO codigoAnterior2DAO = new CodigoAnterior2DAO();
            codigoAnterior2DAO.salvar(vCodigoAnterior2);
        } catch (Exception e) {
            throw e;
        }
    }
    
    private List<CodigoAnterior2VO> carregarCodigoAnterior2(String i_arquivo) throws Exception {
        List<CodigoAnterior2VO> vCodigoAnterior = new ArrayList<>();
        try {
            int linha = 0;

            WorkbookSettings settings = new WorkbookSettings();
            settings.setEncoding("CP1250");

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
                        }
                        
                        CodigoAnterior2VO oCodigoAnterior = new CodigoAnterior2VO();
                        
                        Cell cellCodProduto = sheet.getCell(0, i);
                        Cell cellCodigoBarra = sheet.getCell(9, i);
                        
                        oCodigoAnterior.codigoAnterior = Double.parseDouble(cellCodProduto.getContents());
                        oCodigoAnterior.barras = Long.parseLong(cellCodigoBarra.getContents());

                        
                        vCodigoAnterior.add(oCodigoAnterior);
                    }
                }

                return vCodigoAnterior;

            } catch (Exception ex) {
                if (linha > 0) {
                    throw new VRException("Linha " + linha + ": " + ex.getMessage());
                } else {
                    throw ex;
                }
            }

        } catch (Exception e) {
            throw e;
        }
    }
}
