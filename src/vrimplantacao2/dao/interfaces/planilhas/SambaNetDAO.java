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
import vrimplantacao2.vo.importacao.ProdutoIMP;

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
            
            ProgressBar.setStatus("Analisando Planilha de Família de Produtos");
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
        String id;
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
            
            ProgressBar.setStatus("Analisando Planilha de Mercadológicos");
            ProgressBar.setMaximum(sheet.getRows());
            
            Mercadologico centroReceita = null;
            Mercadologico grupo = null;
            Mercadologico categoria;
            
            int linha = 0;
            try {
                for (int i = 1; i < sheet.getRows(); i++) {
                    linha++;
                    if (
                            sheet.getCell(0, i) != null &&
                            Utils.acertarTexto(sheet.getCell(0, i).getContents()).contains("CENTRO DE RECEITA")
                    ) {
                        String[] strs = Utils.acertarTexto(sheet.getCell(0, i).getContents()).split("\\-");
                        if (strs.length == 2) {
                            centroReceita = new Mercadologico();
                            centroReceita.id =  Utils.acertarTexto(strs[0].substring(17));
                            centroReceita.descricao =  Utils.acertarTexto(strs[1]);
                        }
                    } else if (
                            sheet.getCell(0, i) != null && Utils.acertarTexto(sheet.getCell(0, i).getContents()).contains("GRUPO")
                    ) {
                        String[] strs = Utils.acertarTexto(sheet.getCell(0, i).getContents()).split("\\-");
                        if (strs.length == 2) {
                            grupo = new Mercadologico();
                            grupo.id =  Utils.acertarTexto(strs[0].substring(5));
                            grupo.descricao =  Utils.acertarTexto(strs[1]);
                        }
                    } else if (
                            sheet.getCell(0, i) != null && Utils.acertarTexto(sheet.getCell(0, i).getContents()).contains("CATEGORIA")
                    ) {
                        String[] strs = Utils.acertarTexto(sheet.getCell(0, i).getContents()).split("\\-");
                        if (strs.length == 2) {
                            categoria = new Mercadologico();
                            categoria.id =  Utils.acertarTexto(strs[0].substring(9));
                            categoria.descricao =  Utils.acertarTexto(strs[1]);

                            MercadologicoIMP imp = new MercadologicoIMP();
                            imp.setImportSistema(getSistema());
                            imp.setImportLoja(getLojaOrigem());
                            imp.setMerc1ID(centroReceita.id);
                            imp.setMerc1Descricao(centroReceita.descricao);
                            imp.setMerc2ID(grupo.id);
                            imp.setMerc2Descricao(grupo.descricao);
                            imp.setMerc3ID(categoria.id);
                            imp.setMerc3Descricao(categoria.descricao);
                            result.add(imp);
                        }
                    }

                    ProgressBar.next();
                }
            } catch (Exception ex) {
                System.out.println(linha);
                throw ex;
            }

            return result;
        } else {
            throw new IOException("Planilha(s) não encontrada");
        }
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        File file = new File(this.planilhaProdutos);        
        if (file.exists()) {        
            List<ProdutoIMP> result = new ArrayList<>();

            WorkbookSettings settings = new WorkbookSettings();
            settings.setEncoding("CP1250");
            settings.setIgnoreBlanks(false);

            Workbook planilha = Workbook.getWorkbook(file, settings);            
            Sheet sheet = planilha.getSheet(0);
            
            ProgressBar.setStatus("Analisando Planilha de Produtos");
            ProgressBar.setMaximum(sheet.getRows());

            int linha = 0;
            try {
                for (int i = 1; i < sheet.getRows(); i++) {
                    linha++;
                    if (
                            sheet.getCell(0, i) != null &&
                            Utils.acertarTexto(sheet.getCell(0, i).getContents()).matches("[0-9]+")
                    ) {
                        
                    }

                    ProgressBar.next();
                }
            } catch (Exception ex) {
                System.out.println(linha);
                throw ex;
            }

            return result;
        } else {
            throw new IOException("Planilha(s) não encontrada");
        }
    }
    
    
    
}
