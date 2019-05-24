package vrimplantacao2.dao.interfaces.planilhas;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import vrframework.classe.ProgressBar;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;

/**
 *
 * @author Leandro
 */
public class SambaNetDAO extends InterfaceDAO {
    
    private String planilhaFamilia;
    private String planilhaProdutos;
    private String planilhaProdutosContador;

    @Override
    public String getSistema() {
        return "SambaNet";
    }

    public void setPlanilhaFamiliaProduto(String planilhaFamilia) {
        this.planilhaFamilia = planilhaFamilia;
    }

    public void setPlanilhaProdutos(String planilhaProdutos) {
        this.planilhaProdutos = planilhaProdutos;
    }

    public void setPlanilhaProdutosContator(String planilhaProdutosContador) {
        this.planilhaProdutosContador = planilhaProdutosContador;
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet(Arrays.asList(new OpcaoProduto[]{
            OpcaoProduto.FAMILIA_PRODUTO,
            OpcaoProduto.MERCADOLOGICO,
            OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR
        }));
    }
    
    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        File file = new File(this.planilhaFamilia);
        
        if (file.exists()) {        
            List<FamiliaProdutoIMP> result = new ArrayList<>();

            WorkbookSettings settings = new WorkbookSettings();
            settings.setEncoding("CP1250");
            settings.setIgnoreBlanks(false);

            Workbook planilha = Workbook.getWorkbook(file, settings);            
            Sheet sheet = planilha.getSheet(0);
            
            ProgressBar.setStatus("Analisando planilha de Família de Produtos");
            ProgressBar.setMaximum(sheet.getRows());
            for (int i = 1; i < sheet.getRows(); i++) {
                Cell[] cells = sheet.getRow(i);
                //Se for uma linha vazia ou não numérica, pula
                if (
                        sheet.getCell(0, i) == null || !Utils.acertarTexto(sheet.getCell(0, i).getContents()).matches("[0-9]+")
                ) {
                    ProgressBar.next();
                    continue;
                }
                
                //Caso contrário verifica se a coluna C é uma descrição
                if (!"".equals(Utils.acertarTexto(cells[2].getContents()))) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(cells[0].getContents());
                    imp.setDescricao(cells[2].getContents());
                    
                    result.add(imp);
                }                
                
                ProgressBar.next();
            }

            return result;
        } else {
            throw new IOException("Planilha de Família de Produtos não encontrada");
        }
    }

    
    private static class Mercadologico {
        int id;
        String descricao;
    }
    
    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        File file = new File(this.planilhaProdutos);        
        if (file.exists()) {        
            List<MercadologicoIMP> result = new ArrayList<>();

            WorkbookSettings settings = new WorkbookSettings();
            settings.setEncoding("CP1250");
            settings.setIgnoreBlanks(false);

            Workbook planilha = Workbook.getWorkbook(file, settings);            
            Sheet sheet = planilha.getSheet(0);
            
            ProgressBar.setStatus("Analisando planilha de Mercadológicos");
            ProgressBar.setMaximum(sheet.getRows());
            
            Mercadologico centroReceita;
            Mercadologico grupo;
            Mercadologico categoria;
            
            for (int i = 1; i < sheet.getRows(); i++) {                
                if (
                        sheet.getCell(0, i) == null || !Utils.acertarTexto(sheet.getCell(0, i).getContents()).contains("CENTRO DE RECEITA")
                ) {
                    
                    ProgressBar.next();
                }
                
                
                
                ProgressBar.next();
            }

            return result;
        } else {
            throw new IOException("Planilha(s) não encontrada");
        }
    }
    
}
