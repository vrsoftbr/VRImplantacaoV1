package vrimplantacao.dao.interfaces;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import org.jsoup.Jsoup;
import vrimplantacao.dao.cadastro.NcmDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.NcmVO;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.mozilla.javascript.edu.emory.mathcs.backport.java.util.Arrays;
import org.sonar.runner.commonsio.FilenameUtils;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao.dao.cadastro.CestDAO;
import vrimplantacao.vo.vrimplantacao.CestVO;

/**
 * Esta classe é utilizada para importar o ncm da legislação.
 * @author Leandro
 */
public class ImportacoesDiversasDAO {

    public int origem = 1; // 1 = pagina web, 2 = arquivo xls
    private final NcmDAO dao = new NcmDAO();
    
    public void importarNCM(String i_arquivo) throws Exception {
        
        List<NcmVO> ncms = new ArrayList<>();
        
        WorkbookSettings settings = new WorkbookSettings();
        settings.setEncoding("CP1250");

        Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);

        Sheet[] sheets = arquivo.getSheets();

        for (int sh = 0; sh < sheets.length; sh++) {
            Sheet sheet = arquivo.getSheet(sh);
            for (int i = 0; i < sheet.getRows(); i++) {

                String cellB = sheet.getCell(0,i).getContents();
                String cellC = sheet.getCell(1,i).getContents();

                if (cellB.matches("^([0-9]{1,2})\\.([0-9]{1,2})([^.])")) {
                    //Grava nível 1
                    String descricao = Utils.acertarTexto(cellC.replace("-", "").trim(), 150);
                    ncms.add(gravarNcm(descricao, cellB.replace(".", "")));
                } else if (cellB.matches("([0-9]{4})\\.([0-9]{1,2})\\.([0-9]{1,2})")) {
                    //Grava nível 3
                    String descricao = Utils.acertarTexto(cellC.replace("-", "").trim(), 150);

                    String[] ncm = cellB.split("\\.");
                    ncms.add(gravarNcm(descricao, ncm[0], ncm[1], ncm[2])); 
                } else if (cellB.matches("([0-9]{4})\\.([0-9]{1,2})")) {
                    //Grava nível 2
                    if (!cellB.matches("([0-9]{4})\\.([0-9]{1,2})\\.")) {
                        String descricao = Utils.acertarTexto(cellC.replace("-", "").trim(), 150);
                        String[] ncm = cellB.split("\\.");
                        ncms.add(gravarNcm(descricao, ncm[0], ncm[1]));
                    }
                } 
            }
        }
        
        dao.salvar(ncms);
    }

    private NcmVO gravarNcm(String descricao, String... ncm) {
        NcmVO vo = new NcmVO();
        
        vo.setNcm1(Integer.parseInt(ncm[0]));
        if (ncm.length >= 2) {        
            if (ncm[1].length() == 1) {
                ncm[1] += "0";
            }
            vo.setNcm2(Integer.parseInt(ncm[1]));
        }
        if (ncm.length >= 3) {
            if (ncm[2].length() == 1) {
                ncm[2] += "0";
            }
            vo.setNcm3(Integer.parseInt(ncm[2]));
        }
        vo.setDescricao(descricao);
        vo.setNivel(ncm.length);
        return vo;
    }
    
    private List<String> processaCapitulos(String cellC) throws NumberFormatException {
        List<String> result = new ArrayList<>();
        String str = cellC.replaceAll("(Capítulo(s)*) *", "").replace(" ", "").trim();
        
        String ncm = "";
        boolean eIntervalo = false;
        while (!str.isEmpty()) {
            String letra = str.substring(0, 1);
            str = str.substring(1, str.length());
            //Se for número
            if (letra.matches("[0-9]")) {
                ncm += letra;
            } else if (",".equals(letra) || "e".equals(letra)) {
                result.add(ncm);
                ncm = "";
            } else if ("a".equals(letra)) {
                eIntervalo = true;
                result.add(ncm);
                ncm = "";
            }
        }
        
        if (!"".equals(ncm)) {
            if (eIntervalo) {
                eIntervalo = false;
                int capInicial = Integer.parseInt(result.get(result.size() - 1)) + 1;
                int capFinal = Integer.parseInt(ncm);
                for (int j = capInicial; j <= capFinal; j++) {
                    result.add(String.valueOf(j));
                }
                ncm = "";
            } else {
                result.add(ncm);
                ncm = "";
            }
        }
        
        return result;
    }

    public void importarCEST(String arquivo) throws IOException, Exception {
        String extFile = FilenameUtils.getExtension(arquivo);
        if ("xls".equals(extFile)) {
            List<CestVO> cestMapeadosXls = parseXls(arquivo);
            ProgressBar.setStatus("Carregando dados...Cest arquivo Xls...");
            ProgressBar.setMaximum(cestMapeadosXls.size());
            new CestDAO().salvar(cestMapeadosXls);
        } else {
            List<CestVO> cestMapeados = parseHtml(arquivo);
            ProgressBar.setStatus("Importando CEST....Gravando CESTs....");
            new CestDAO().salvar(cestMapeados);
        }
    }

    private List<CestVO> parseXls(String i_arquivo) throws Exception {
        List<CestVO> result = new ArrayList<>();
        WorkbookSettings settings = new WorkbookSettings();
        settings.setEncoding("CP1250");
        Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);
        Sheet[] sheets = arquivo.getSheets();
        int linha;
        
        try {
            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;
                
                for (int i = 0; i < sheet.getRows(); i++) {
                    linha ++;
                    if (linha == 1) {
                        continue;
                    }
                    
                    Cell cellCest = sheet.getCell(0, i);
                    Cell cellNcm = sheet.getCell(1, i);
                    Cell cellDescCest = sheet.getCell(4, i);
                    
                    List<String> ncms = null;
                    String strNcms = cellNcm.getContents().trim();
                    if (strNcms.length() == 2) {
                        ncms = processaCapitulos(strNcms);
                    } else {
                        ncms = breakNcms(strNcms);
                    }
                    
                    CestVO vo = new CestVO();
                    String[] codCest = cellCest.getContents().split("\\.");
                    vo.setCest1(Integer.parseInt(codCest[0]));
                    vo.setCest2(Integer.parseInt(codCest[1]));
                    vo.setCest3(Integer.parseInt(codCest[2]));
                    vo.setDescricao(cellDescCest.getContents());
                    //Para cada informação de ncm encontrada, cria uma entrada na tabela
                    for (String nc : ncms) {
                        for (NcmVO ncm : ncmsIniciadosPor(nc)) {
                            vo.getNcms().add(ncm);
                        }
                    }
                    result.add(vo);                    
                }
            }
            return result;
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    private List<CestVO> parseHtml(String arquivo) throws Exception, IOException, NumberFormatException {
        ProgressBar.setStatus("Importando CEST....Convertendo tabelas HTML....");
        List<CestVO> result = new ArrayList<>();
        Document doc = Jsoup.parse(new File(arquivo), "UTF-8");
        Elements tables = doc.select("table[class*=plain]");
        ProgressBar.setMaximum(tables.size());
        //Obtem as tabelas do HTML
        for (Element table: tables) {
            //Obtem as linhas da tabela
            for (Element linha: table.select("tr")) {
                Elements celulas = linha.select("td");
                //FILTRO: Toda tabela com 4 células é de tributação
                if (celulas.size() == 4) {
                    String strCest = celulas.get(1).text();
                    //Testa o campo 1 da para ver se é um número CEST válido.
                    if (strCest.matches("[0-9]{2}\\.[0-9]{3}\\.[0-9]{2}")) {
                        //Quebra a String com os ncms em uma Lista
                        List<String> ncms;
                        String strNcms = celulas.get(2).child(0).html();                        
                        if (strNcms.contains("Capítulo")) {
                            ncms = processaCapitulos(strNcms);                        
                        } else {
                            ncms = breakNcms(strNcms);
                        }
                        CestVO cest = new CestVO();
                        String[] codCest = strCest.split("\\.");                            
                        cest.setCest1(Integer.parseInt(codCest[0]));                            
                        cest.setCest2(Integer.parseInt(codCest[1]));
                        cest.setCest3(Integer.parseInt(codCest[2]));
                        cest.setDescricao(celulas.get(3).text());
                        //Para cada informação de ncm encontrada, cria uma entrada na tabela
                        for (String nc: ncms) {                            
                            for(NcmVO ncm: ncmsIniciadosPor(nc)) {
                                cest.getNcms().add(ncm);
                            }
                        }
                        result.add(cest);
                    }
                }                
            }
            ProgressBar.next();
        }        
        return result;
    }

    private List<String> breakNcms(String strNcms) {
        List<String> result = new ArrayList<>();
        String[] ncms = strNcms.replace(" ", "").replaceAll("<( *)br( *)>|( +)e( +)|&nbsp;", ",").split(",");
        
        result.addAll(Arrays.asList(ncms));
        
        return result;
    }

    private List<NcmVO> ncmsIniciadosPor(String strNcm) throws Exception {
        strNcm = strNcm.replace(".", "");
        List<NcmVO> result = new ArrayList<>();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	id, \n" +
                    "	ncm,\n" +
                    "	ncm1,\n" +
                    "	ncm2,\n" +
                    "	ncm3,\n" +
                    "	nivel,\n" +
                    "	descricao\n" +
                    "from\n" +
                    "	(select \n" +
                    "		id, \n" +
                    "		lpad(ncm1::varchar,4,'0') || rpad(ncm2::varchar,2,'0') || rpad(ncm3::varchar,2,'0') ncm,\n" +
                    "		ncm1,\n" +
                    "		ncm2,\n" +
                    "		ncm3,\n" +
                    "		nivel,\n" +
                    "		descricao\n" +
                    "	from \n" +
                    "		ncm \n" +
                    "	where \n" +
                    "		nivel = 3) ncm\n" +
                    "where\n" +
                    "	ncm like '" + strNcm + "%'"
            )) {
                while (rst.next()) {
                    NcmVO ncm = new NcmVO();
                    ncm.setId(rst.getInt("id"));
                    ncm.setDescricao(rst.getString("descricao"));
                    ncm.setNivel(rst.getInt("nivel"));
                    ncm.setNcm1(rst.getInt("ncm1"));
                    ncm.setNcm2(rst.getInt("ncm2"));
                    ncm.setNcm3(rst.getInt("ncm3"));
                    
                    result.add(ncm);
                }
            }
        }
        return result;
    }
    
}
