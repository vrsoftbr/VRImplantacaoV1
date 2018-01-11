package vrimplantacao.dao.interfaces;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;

public class PlanilhaProdutosLanchoneteDAO {

    public List<ProdutoBalancaVO> carregarProdutosLanchonete(String i_arquivo) throws Exception {
        int linha;
        List<ProdutoBalancaVO> v_balanca = new ArrayList<>();
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
                    
                    if (linha == 1) {
                        continue;
                    }
                    
                    Cell cellCodigo = sheet.getCell(0, i);
                    Cell cellDescricao = sheet.getCell(2, i);
                    
                    ProdutoBalancaVO oProdutoBalanca = new ProdutoBalancaVO();
                    oProdutoBalanca.setCodigo(Integer.parseInt(cellCodigo.getContents().trim()));
                    oProdutoBalanca.setDescricao(Utils.acertarTexto(cellDescricao.getContents().trim()));
                    oProdutoBalanca.setValidade(0);
                    oProdutoBalanca.setPesavel("");
                    v_balanca.add(oProdutoBalanca);
                }
            }
            return v_balanca;
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    public void importarProdutosLanchonete(String i_arquivo) throws Exception {
        try {
            List<ProdutoBalancaVO> vBalanca = new ArrayList<>();
            vBalanca = carregarProdutosLanchonete(i_arquivo);
            new ProdutoBalancaDAO().salvarSemDelete(vBalanca);
        } catch (Exception ex) {
            throw ex;
        }
    }
}