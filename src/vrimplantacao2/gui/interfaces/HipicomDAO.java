package vrimplantacao2.gui.interfaces;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import org.mozilla.javascript.edu.emory.mathcs.backport.java.util.Arrays;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.vo.cadastro.mercadologico.MercadologicoNivelIMP;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Leandro
 */
public class HipicomDAO extends InterfaceDAO implements MapaTributoProvider {
    
    private String arquivo;

    @Override
    public String getSistema() {
        return "Hipicom";
    }
    
    private static interface LineHandler {

        public void handleLine(Cell[] row) throws Exception;
    
    }
    
    private void processarArquivo(LineHandler handler) throws Exception {
        WorkbookSettings settings = new WorkbookSettings();
        settings.setEncoding("CP1250");
        
        Workbook planilha = Workbook.getWorkbook(new File(arquivo), settings);            
        Sheet sheet = planilha.getSheet(0);
        
        //Obtem os dados.
        for (int i = 1; i < sheet.getRows(); i++) {       
            Cell[] row = sheet.getRow(i);
            if (row.length > 0) {
                handler.handleLine(row);
            }
        }
    }

    @Override
    public List<MercadologicoNivelIMP> getMercadologicoPorNivel() throws Exception {
        final MultiMap<String, MercadologicoNivelIMP> auxiliar = new MultiMap<>();
        final List<MercadologicoNivelIMP> result = new ArrayList<>();
        processarArquivo(new LineHandler() {
            @Override
            public void handleLine(Cell[] row) throws Exception {
                String value = row[0].getContents();
                if (value.matches("Depto/.*")) {
                    value = value.replace("Depto/Seçăo/Grupo/Sub-Grupo: ", "");
                    
                    String[] mercs = new String[4];
                    mercs[0] = "";
                    mercs[1] = "";
                    mercs[2] = "";
                    mercs[3] = "";
                    String descricao = "";
                    boolean gerandoDescricao = false;
                    int barCount = 0;
                    
                    char[] v = value.toCharArray();
                    for (int i = 0; i < v.length; i++) {
                        char ch = v[i];
                        if (String.valueOf(ch).matches("[0-9]") && !gerandoDescricao) {
                             mercs[barCount] += ch;
                        } else if ('/' == ch && !gerandoDescricao) {
                            barCount++;
                        } else if (' ' == ch && !gerandoDescricao) {
                            gerandoDescricao = true;
                        } else {
                            descricao += ch;
                        }
                    }
                    
                    descricao = descricao.trim();
                    
                    System.out.println(Arrays.toString(mercs) + " - " + descricao);
                    
                    if (!"".equals(mercs[1])) {                        
                        try {
                            if ("".equals(mercs[2])) {
                                MercadologicoNivelIMP imp = new MercadologicoNivelIMP();
                                imp.setId(mercs[0] + "-" + mercs[1]);
                                imp.setDescricao(descricao);
                                auxiliar.put(imp, imp.getId());
                                result.add(imp);
                            } else if ("".equals(mercs[3])) {
                                MercadologicoNivelIMP pai = auxiliar.get(mercs[0] + "-" + mercs[1]);
                                if (pai == null) {
                                    pai = new MercadologicoNivelIMP();
                                    pai.setId(mercs[0] + "-" + mercs[1]);
                                    pai.setDescricao(descricao);
                                    auxiliar.put(pai, pai.getId());
                                    result.add(pai);
                                }
                                MercadologicoNivelIMP imp = pai.addFilho(mercs[2], descricao);
                                auxiliar.put(imp, mercs[0] + "-" + mercs[1], mercs[2]);
                            } else if (!"".equals(mercs[3])) {
                                MercadologicoNivelIMP pai = auxiliar.get(mercs[0] + "-" + mercs[1], mercs[2]);
                                if (pai == null) {
                                    pai = auxiliar.get(mercs[0] + "-" + mercs[1]);
                                    if (pai == null) {
                                        pai = new MercadologicoNivelIMP();
                                        pai.setId(mercs[0] + "-" + mercs[1]);
                                        pai.setDescricao(descricao);
                                        auxiliar.put(pai, pai.getId());
                                        result.add(pai);
                                    }
                                    pai = pai.addFilho(mercs[2], pai.getDescricao());
                                }
                                MercadologicoNivelIMP imp = pai.addFilho(mercs[3], descricao);
                                auxiliar.put(imp, mercs[0] + "-" + mercs[1], mercs[2], mercs[3]);
                            }
                        } catch (Exception e) {
                            System.out.println(Arrays.toString(mercs) + " - " + descricao);
                            throw e;
                        }
                    }
                    
                }
            }
        });
        return result; 
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        final List<ProdutoIMP> result = new ArrayList<>();
        final Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().getProdutosBalanca();
        
        processarArquivo(
            new LineHandler() {
                @Override
                public void handleLine(Cell[] row) throws Exception {                    
                    if (row.length > 7) {
                        ProdutoIMP imp = new ProdutoIMP();
                        
                        try {
                            imp.setImportSistema(getSistema());
                            imp.setImportLoja(getLojaOrigem());
                            
                            String id = row[15].getContents().split("-")[0];
                            
                            ProdutoBalancaVO bal = produtosBalanca.get(Utils.stringToInt(id));
                            if (bal != null) {
                                imp.seteBalanca(true);
                                if ("U".equals(bal.getPesavel())) {
                                    imp.setTipoEmbalagem("UN");
                                } else {
                                    imp.setTipoEmbalagem("KG");
                                }
                                imp.setValidade(bal.getValidade());
                                imp.setDescricaoReduzida(bal.getDescricao());
                            } else {
                                imp.setDescricaoReduzida(row[16].getContents());
                            }
                            
                            imp.setImportId(id);
                            imp.setDescricaoCompleta(row[16].getContents());
                            imp.setDescricaoGondola(row[16].getContents());
                            if (row.length == 21) {
                                imp.setEan(row[20].getContents());
                            }

                            imp.setPrecovenda(Utils.stringToDouble(row[1].getContents()));
                            imp.setCustoSemImposto(Utils.stringToDouble(row[2].getContents()));
                            imp.setCustoComImposto(Utils.stringToDouble(row[6].getContents()));

                            if ("S".equals(row[8].getContents())) {
                                imp.setSituacaoCadastro(SituacaoCadastro.EXCLUIDO);
                            } else {
                                imp.setSituacaoCadastro(SituacaoCadastro.ATIVO);
                            }

                            imp.setNcm(row[8].getContents());
                            imp.setMargem(Utils.stringToDouble(row[12].getContents()));



                            imp.setPiscofinsCstCredito(pis_e(row[5].getContents()));
                            imp.setPiscofinsCstDebito(pis_s(row[11].getContents()));

                            imp.setIcmsDebitoId(row[4].getContents() != null ? row[4].getContents() : "");
                            imp.setIcmsCreditoId(row[3].getContents() != null ? row[3].getContents() : "");
                            /*double[] icm = icms(row[4].getContents());
                            imp.setIcmsCstSaida((int) icm[0]);
                            imp.setIcmsAliqSaida(icm[1]);
                            imp.setIcmsReducaoSaida(icm[2]);*/

                            /*icm = icms(row[3].getContents());
                            imp.setIcmsCstEntrada((int) icm[0]);
                            imp.setIcmsAliqEntrada(icm[1]);
                            imp.setIcmsReducaoEntrada(icm[2]);*/
                        } catch (Exception e) {
                            throw e;
                        }
                        
                        result.add(imp);
                    }                    
                }
            }
        );
        
        return result;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        final Set<MapaTributoIMP> result = new LinkedHashSet<>();
        processarArquivo(
            new LineHandler() {
                @Override
                public void handleLine(Cell[] row) throws Exception {
                    if (row.length >= 7) {
                        result.add(new MapaTributoIMP(row[4].getContents(), row[4].getContents()));
                        result.add(new MapaTributoIMP(row[3].getContents(), row[3].getContents()));
                    }
                }
            }
        );
        
        return new ArrayList<>(result);
    }
    
    private int pis_s(String pis) {
        if (pis.contains("01) Trib.9.25%")) {return 1;}
        if (pis.contains("04) Monofásico")) {return 4;}
        if (pis.contains("05) Subst.Trib")) {return 5;}
        if (pis.contains("06) Alíq.Zero")) {return 6;}
        if (pis.contains("07) Isento")) {return 7;}
        if (pis.contains("09) Suspensão")) {return 9;}
        return 99;
    }
    
    private int pis_e(String pis) {
        if (pis.contains("50) Créd.9.25%")) {return 50;}
        if (pis.contains("70) Sem Créd.")) {return 70;}
        if (pis.contains("71) Isento")) {return 71;}
        if (pis.contains("72) Suspensão")) {return 72;}
        if (pis.contains("73) Alíq.Zero")) {return 73;}
        if (pis.contains("75) Subst.Trib")) {return 75;}
        return 99;
    }
    
    private double[] icms(String icms) {
        switch (icms) {
            case "01) ST.19%": {return new double[] {90,0,0};}
            case "02) Isento": {return new double[] {40,0,0};}
            case "03) 19%": {return new double[] {0,19,0};}
            case "04) 8%": {return new double[] {0,8,0};}
            case "05) 13%": {return new double[] {0,13,0};}
            case "06) 26%": {return new double[] {0,26,0};}
            case "11) Não trib.": {return new double[] {41,0,0};}
            case "12) ST.8%": {return new double[] {90,0,0};}
            case "13) ST.13%": {return new double[] {90,0,0};}
            case "14) ST.26%": {return new double[] {90,0,0};}
            case "15) 12%": {return new double[] {0,12,0};}
            case "20) 18 p/ 7%": {return new double[] {20,18,61.11};}
            case "21) 18 p/ 12%": {return new double[] {20,18,33.33};}
            case "23) 19 p/ 8%": {return new double[] {90,0,0};}
            case "24) 7%": {return new double[] {0,7,0};}
            case "25) ST.7%": {return new double[] {90,0,0};}
            case "26) 12 p/ 7%": {return new double[] {20,12,41.67};}
            case "31) 19 p/ 13%": {return new double[] {90,0,0};}
            case "33) ST.18-7%": {return new double[] {90,0,0};}
            case "38) ST.19-13%": {return new double[] {90,0,0};}
            case "40) 19 p/ 7%": {return new double[] {20,19,63.15};}
            case "41) 19 p/ 12%": {return new double[] {90,0,0};}
            case "45) ST.19-12%": {return new double[] {90,0,0};}
            case "46) STna.26-1": {return new double[] {90,0,0};}
            case "47) ST.17%": {return new double[] {90,0,0};}
            case "48) ST.30,08-": {return new double[] {90,0,0};}
            case "49) 19 p/ 12%": {return new double[] {90,0,0};}
            case "50) ST.12%": {return new double[] {90,0,0};}
            case "51) ST.18%": {return new double[] {90,0,0};}
            case "53) 3,1%": {return new double[] {90,0,0};}
            case "56) Diferido": {return new double[] {51,0,0};}
            case "57) 18%": {return new double[] {0,18,0};}
            case "59) ST.12-8,8": {return new double[] {90,0,0};}
            case "60) ST.19-13,": {return new double[] {90,0,0};}
            case "62) 3,48%": {return new double[] {90,0,0};}
            case "64) 4%": {return new double[] {0,4,0};}
            case "65) ST.4%": {return new double[] {90,0,0};}
            case "66) 12 p/ 8,8": {return new double[] {90,0,0};}
            case "68) 2%": {return new double[] {90,0,0};}
            case "69) 5%": {return new double[] {0,5,0};}
            case "70) 29%": {return new double[] {0,29,0};}
            case "71) 20%": {return new double[] {0,20,0};}
            case "72) 9%": {return new double[] {0,9,0};}
            case "73) 14%": {return new double[] {0,14,0};}
            case "74) 27%": {return new double[] {0,27,0};}
            case "75) ST.9%": {return new double[] {90,0,0};}
            case "76) ST.14%": {return new double[] {90,0,0};}
            case "77) ST.20%": {return new double[] {90,0,0};}
            case "78) ST.27%": {return new double[] {90,0,0};}
            case "79) 20 p/ 1%": {return new double[] {90,0,0};}
            case "81) 14 p/ 9%": {return new double[] {90,0,0};}
            case "82) 20 p/ 9%": {return new double[] {20,20,55};}
            case "83) 20 p/ 14%": {return new double[] {20,20,30};}
            case "84) 27 p/ 14%": {return new double[] {90,0,0};}
            case "86) ST.14-9%": {return new double[] {90,0,0};}
            case "88) ST.20-14%": {return new double[] {90,0,0};}
            case "89) ST.26-14%": {return new double[] {90,0,0};}
            case "90) ST.27-20%": {return new double[] {90,0,0};}
            case "91) ST.20-15%": {return new double[] {90,0,0};}
            default : {return new double[] {90,0,0};}
        }
    }
    
    

    public void setArquivo(String arquivo) {
        this.arquivo = arquivo;
    }

}
