package vrimplantacao2.dao.interfaces.planilhas;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import vrframework.classe.ProgressBar;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Leandro
 */
public class SambaNetDAO extends InterfaceDAO implements MapaTributoProvider {
    
    private static final Logger LOG = Logger.getLogger(SambaNetDAO.class.getName());
    
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
            OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
            OpcaoProduto.IMPORTAR_GERAR_SUBNIVEL_MERC,
            OpcaoProduto.IMPORTAR_MANTER_BALANCA,
            OpcaoProduto.PRODUTOS,
            OpcaoProduto.EAN,
            OpcaoProduto.EAN_EM_BRANCO,
            OpcaoProduto.DESC_COMPLETA,
            OpcaoProduto.DESC_GONDOLA,
            OpcaoProduto.DESC_REDUZIDA,
            OpcaoProduto.QTD_EMBALAGEM_COTACAO,
            OpcaoProduto.ICMS,
            OpcaoProduto.PIS_COFINS,
            OpcaoProduto.NATUREZA_RECEITA,
            OpcaoProduto.ESTOQUE,
            OpcaoProduto.CUSTO,
            OpcaoProduto.PRECO,
            OpcaoProduto.MERCADOLOGICO_PRODUTO,
            OpcaoProduto.FAMILIA
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

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        
        File file = new File(this.planilhaProdutosContador);        
        if (file.exists()) {

            WorkbookSettings settings = new WorkbookSettings();
            settings.setEncoding("CP1250");
            settings.setIgnoreBlanks(false);

            Workbook planilha = Workbook.getWorkbook(file, settings);            
            Sheet sheet = planilha.getSheet(0);
            
            int linha = 0;
            try {
                Set<String> tributos = new HashSet<>();
                for (int i = 1; i < sheet.getRows(); i++) {
                    if (
                            val(sheet, 0, i).matches("[0-9]+") &&
                            val(sheet, 1, i).matches("[0-9]+") &&
                            val(sheet, 5, i).matches("[a-zA-Z0-9 ]+")
                    ) {
                        if (tributos.add(val(sheet, 10, i))) {
                            LOG.fine("Tributo '" + val(sheet, 10, i) + "' incluso!");
                        }
                    }
                }
                
                List<MapaTributoIMP> result = new ArrayList<>();
                
                for (String trib: tributos) {
                    result.add(new MapaTributoIMP(trib, trib));
                }
                
                return result;
            } catch (Exception ex) {
                System.out.println(linha);
                throw ex;
            }
        }
        return null;
    }
    
    private String val(Sheet sheet, int col, int row) {
        String str = sheet.getCell(col, row).getContents();
        str = str == null ? "" : str.trim();
        return str;
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
            
            
            MultiMap<String, ProdutoIMP> produtos = new MultiMap<>();
            
            try {

                String centroReceita = "", grupo = "", categoria = "";
                
                
                for (int i = 1; i < sheet.getRows(); i++) {
                    linha++;
                    if (
                            sheet.getCell(0, i) != null &&
                            Utils.acertarTexto(sheet.getCell(0, i).getContents()).matches("[0-9]+") &&
                            Utils.acertarTexto(sheet.getCell(1, i).getContents()).matches("[0-9]")
                    ) {
                        
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportSistema(getSistema());
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportId(sheet.getCell(0, i).getContents());
                        imp.setEan(sheet.getCell(3, i).getContents());
                        imp.setDescricaoCompleta(sheet.getCell(6, i).getContents());
                        imp.setDescricaoReduzida(sheet.getCell(6, i).getContents());
                        imp.setDescricaoGondola(sheet.getCell(6, i).getContents());
                        imp.setQtdEmbalagemCotacao(Utils.stringToInt(sheet.getCell(11, i).getContents()));
                        imp.setIcmsDebitoId(sheet.getCell(13, i).getContents());
                        imp.setIcmsCreditoId(sheet.getCell(13, i).getContents());
                        imp.setEstoque(Utils.stringToDouble(sheet.getCell(14, i).getContents()));
                        imp.setCustoSemImposto(Utils.stringToDouble(sheet.getCell(17, i).getContents()));
                        imp.setCustoComImposto(Utils.stringToDouble(sheet.getCell(17, i).getContents()));
                        imp.setPrecovenda(Utils.stringToDouble(sheet.getCell(21, i).getContents()));
                        imp.setCodMercadologico1(centroReceita);
                        imp.setCodMercadologico2(grupo);
                        imp.setCodMercadologico3(categoria);
                        produtos.put(imp, imp.getImportId(), imp.getEan());
                        
                    } else if (
                            sheet.getCell(0, i) != null &&
                            Utils.acertarTexto(sheet.getCell(0, i).getContents()).contains("CENTRO DE RECEITA")
                    ) {
                        String[] strs = Utils.acertarTexto(sheet.getCell(0, i).getContents()).split("\\-");
                        if (strs.length == 2) {
                            centroReceita = Utils.acertarTexto(strs[0].substring(17));
                        }
                    } else if (
                            sheet.getCell(0, i) != null && Utils.acertarTexto(sheet.getCell(0, i).getContents()).contains("GRUPO")
                    ) {
                        String[] strs = Utils.acertarTexto(sheet.getCell(0, i).getContents()).split("\\-");
                        if (strs.length == 2) {
                            grupo = Utils.acertarTexto(strs[0].substring(5));
                        }
                    } else if (
                            sheet.getCell(0, i) != null && Utils.acertarTexto(sheet.getCell(0, i).getContents()).contains("CATEGORIA")
                    ) {
                        String[] strs = Utils.acertarTexto(sheet.getCell(0, i).getContents()).split("\\-");
                        if (strs.length == 2) {
                            categoria = Utils.acertarTexto(strs[0].substring(9));
                        }                    
                    }

                    ProgressBar.next();
                }
            } catch (Exception ex) {
                System.out.println(linha);
                throw ex;
            }
            
            vincularFamilia(produtos);
            
            vincularTributos(produtos);

            return result;
        } else {
            throw new IOException("Planilha(s) não encontrada");
        }
    }

    private void vincularFamilia(MultiMap<String, ProdutoIMP> produtos) throws Exception {
        File file = new File(this.planilhaFamilia);        
        if (file.exists()) {

            WorkbookSettings settings = new WorkbookSettings();
            settings.setEncoding("CP1250");
            settings.setIgnoreBlanks(false);

            Workbook planilha = Workbook.getWorkbook(file, settings);            
            Sheet sheet = planilha.getSheet(0);

            ProgressBar.setStatus("Analisando Planilha de Produtos (Família de Produtos)");
            ProgressBar.setMaximum(sheet.getRows());

            int linha = 0;
            
            try {
                String familia = null;
                for (int i = 1; i < sheet.getRows(); i++) {
                    //Se a coluna 2 for um número e a coluna 3 for texto, então é um produto.
                    if (
                            Utils.acertarTexto(sheet.getCell(1, i).getContents()).matches("[0-9]+") &&
                            Utils.acertarTexto(sheet.getCell(2, i).getContents()).matches("[a-zA-Z0-9 ]+")
                    ) {
                        String id = sheet.getCell(1, i).getContents();
                        produtos.get(id).setIdFamiliaProduto(familia);
                        LOG.finer(String.format("Família '%s' vinculado ao produto '%s'-'%s'",
                                familia,
                                sheet.getCell(1, i),
                                sheet.getCell(2, i)
                        ));
                    } else if (
                            //Se a primeira coluna é numérica, se a segunda é vazia e se a terceira for um texto
                            //então é uma família.
                            Utils.acertarTexto(sheet.getCell(0, i).getContents()).matches("[0-9]+") &&
                            Utils.acertarTexto(sheet.getCell(1, i).getContents()).equals("") &&
                            Utils.acertarTexto(sheet.getCell(2, i).getContents()).matches("[a-zA-Z0-9 ]+")
                    ) {
                        familia = Utils.acertarTexto(sheet.getCell(0, i).getContents());
                    }
                    
                    ProgressBar.next();
                }
                
            } catch (Exception ex) {
                System.out.println(linha);
                throw ex;
            }
        }
    }

    private void vincularTributos(MultiMap<String, ProdutoIMP> produtos) throws Exception {
        throw new UnsupportedOperationException("Funcao ainda nao suportada.");
    }
    
}
