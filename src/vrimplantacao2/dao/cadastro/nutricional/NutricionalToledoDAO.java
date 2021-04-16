package vrimplantacao2.dao.cadastro.nutricional;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.NutricionalToledoVO;
import vrimplantacao2.dao.cadastro.produto.ProdutoAnteriorDAO;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.importacao.NutricionalToledoIMP;

/**
 *
 * @author Importacao
 */
public class NutricionalToledoDAO {

    Utils util = new Utils();
    public static String sistema;
    public static String loja;
    public boolean ignorarUltimoDigito = false;
    public int opcaoCodigo = 1;

    public List<NutricionalToledoIMP> getNutricionalToledoProduto(String arquivo) throws Exception {
        List<NutricionalToledoIMP> result = new ArrayList<>();
        List<String> vToledo = util.lerArquivoBalanca(arquivo);

        for (int i = 0; i < vToledo.size(); i++) {
            NutricionalToledoIMP toledo = new NutricionalToledoIMP();
            StringLine ln = new StringLine(vToledo.get(i));
            if (!vToledo.get(i).trim().isEmpty()) {
                /*ln.jump(4);
                switch (ln.sbi(1)) {
                    case 1: toledo.setPesavel("U"); break;
                    case 5: toledo.setPesavel("U"); break;
                    default: toledo.setPesavel("P"); break;
                }
                toledo.setCodigo(ln.sbi(6));
                ln.jump(6);
                toledo.setValidade(ln.sbi(3));
                toledo.setDescricao(ln.sb(25));
                ln.sb(25);*/
                if ("0".equals(vToledo.get(i).substring(2, 3))) {
                    //toledo.setCodigo(Integer.parseInt(vToledo.get(i).substring(2, 9)));
                    //toledo.setCodigo(Integer.parseInt(vToledo.get(i).substring(4, 9)));
                    toledo.setPesavel("P");
                    toledo.setDescricao(util.acertarTexto(vToledo.get(i).substring(18, 67).replace("'", "").trim()));
                    toledo.setValidade(Integer.parseInt(vToledo.get(i).substring(15, 18)));
                    toledo.setNutricional(Integer.parseInt(vToledo.get(i).substring(78, 81)));
                    //toledo.setNutricional(Integer.parseInt(vToledo.get(i).substring(78, 84)));
                    //toledo.setCodigo(toledo.getNutricional());
                    toledo.setCodigo(Integer.parseInt(vToledo.get(i).substring(3, 9)));
                } else {
                    //toledo.setCodigo(Integer.parseInt(vToledo.get(i).substring(2, 9)));
                    //toledo.setCodigo(Integer.parseInt(vToledo.get(i).substring(4, 9)));
                    toledo.setPesavel("U");
                    toledo.setDescricao(util.acertarTexto(vToledo.get(i).substring(18, 67).replace("'", "").trim()));
                    toledo.setValidade(Integer.parseInt(vToledo.get(i).substring(15, 18)));
                    toledo.setNutricional(Integer.parseInt(vToledo.get(i).substring(78, 81)));
                    //toledo.setNutricional(Integer.parseInt(vToledo.get(i).substring(78, 84)));
                    //toledo.setCodigo(toledo.getNutricional());
                    toledo.setCodigo(Integer.parseInt(vToledo.get(i).substring(3, 9)));
                }
            }
            result.add(toledo);
        }
        return result;
    }

    public List<NutricionalToledoVO> getNutricionalToledo(String arquivo) throws Exception {
        List<NutricionalToledoVO> result = new ArrayList<>();
        List<String> vToledo = util.lerArquivoBalanca(arquivo);
        
        boolean isLayoutMgv6 = !vToledo.isEmpty() && vToledo.get(0).startsWith("N");

        if (isLayoutMgv6) {
            for (int i = 0; i < vToledo.size(); i++) {
                NutricionalToledoVO vo = new NutricionalToledoVO();
                if (!vToledo.get(i).trim().isEmpty()) {
                    
                    if (opcaoCodigo == 1) {
                        vo.setId(Utils.stringToInt(vToledo.get(i).substring(1, 7)));
                    } else {
                        vo.setId(Utils.stringToInt(vToledo.get(i).substring(2, 7)));
                    }
                    
                    System.out.println("ID NUTRI: " + vo.getId());
                    vo.setQuantidade(Utils.stringToInt(vToledo.get(i).substring(8, 11)));
                    vo.setId_tipounidadeporcao(Utils.stringToInt(vToledo.get(i).substring(11, 12)));
                    vo.setMedidainteira(Utils.stringToInt(vToledo.get(i).substring(13, 14)));
                    vo.setId_tipomedidadecimal(Utils.stringToInt(vToledo.get(i).substring(14, 15)));
                    vo.setId_tipomedida(Utils.stringToInt(vToledo.get(i).substring(15, 17)));
                    vo.setCaloria(Utils.stringToInt(vToledo.get(i).substring(17, 21)));
                    vo.setCarboidrato(Utils.stringToLong(vToledo.get(i).substring(21, 25)) / 10);
                    vo.setProteina(Utils.stringToDouble(vToledo.get(i).substring(25, 28)) / 10);
                    vo.setGordura(Utils.stringToDouble(vToledo.get(i).substring(28, 31)) / 10);
                    vo.setGordurasaturada(Utils.stringToDouble(vToledo.get(i).substring(31, 34)) / 10);
                    vo.setGorduratrans(Utils.stringToDouble(vToledo.get(i).substring(34, 37)) / 10);
                    vo.setFibra(Utils.stringToDouble(vToledo.get(i).substring(38, 40)) / 10);
                    vo.setSodio(Utils.stringToDouble(vToledo.get(i).substring(41, 45)) / 10);
                    vo.setId_situacaocadastro(1);

                    result.add(vo);
                }
            }
        } else {
            //0371 0 100 0 00 0 00 0202 000 1 19 0 100 040 000 00 1 000 0000 0581 10 00 38 13 16 00 00 00 00 24
            //CCCC A BBB D EE F GG HHHH III J LL M NNN OOO PPP QQ R SSS TTTT UUUU VV XX ZZ WW YY KK && ## ** $$
            
            for (int i = 0; i < vToledo.size(); i++) {
                NutricionalToledoVO vo = new NutricionalToledoVO();
                StringLine ln = new StringLine(vToledo.get(i));
                if (!ln.isEmpty()) {
                    vo.setId(ln.sbi(4));//CCCC
                    vo.setIdProduto(vo.getId());
                    ln.jump(1);//A - RESERVADO
                    vo.setQuantidade(ln.sbi(3));//BBB
                    vo.setId_tipounidadeporcao(ln.sbi(1));//D
                    vo.setMedidainteira(ln.sbi(2));//EE
                    vo.setId_tipomedidadecimal(ln.sbi(1));//F
                    switch (ln.sbi(2)) {//GG
                        case 0: vo.setId_tipomedida(0); break;//0	"Colher(es) de Sopa"
                        case 1: vo.setId_tipomedida(1); break;//1	"Colher(es) de Café"
                        case 2: vo.setId_tipomedida(2); break;//2	"Colher(es) de Chá"
                        case 3: vo.setId_tipomedida(3); break;//3	"Xícara(s)"
                        case 4: vo.setId_tipomedida(4); break;//4	"De Xícaras"
                        case 5: vo.setId_tipomedida(5); break;//5	"Unidades(s)"
                        case 6: vo.setId_tipomedida(6); break;//6	"Pacote(s)"
                        case 7: vo.setId_tipomedida(7); break;//7	"Fatia(s)"
                        case 8: vo.setId_tipomedida(8); break;//8	"Fatia(s) Fina(s)"
                        case 9: vo.setId_tipomedida(9); break;//9	"Pedaço(s)"
                        case 10: vo.setId_tipomedida(10); break;//10	"Folha(s)"
                        case 11: vo.setId_tipomedida(11); break;//11	"Pão(es)"
                        case 12: vo.setId_tipomedida(12); break;//12	"Biscoito(s)"
                        case 13: vo.setId_tipomedida(13); break;//13	"Bisnaguinha(s)"
                        case 14: vo.setId_tipomedida(14); break;//14	"Disco(s)"
                        case 15: vo.setId_tipomedida(15); break;//15	"Copo(s)"
                        case 16: vo.setId_tipomedida(16); break;//16	"Porção(ões)"
                        case 17: vo.setId_tipomedida(17); break;//17	"Tablete(s)"
                        case 18: vo.setId_tipomedida(18); break;//18	"Sachê(S)"
                        case 19: vo.setId_tipomedida(19); break;//19	"Almôndega(s)"
                        case 20: vo.setId_tipomedida(20); break;//20	"Bife(s)"
                        case 21: vo.setId_tipomedida(21); break;//21	"Filé(s)"
                        case 22: vo.setId_tipomedida(22); break;//22	"Concha(s)"
                        case 23: vo.setId_tipomedida(23); break;//23	"Bala(s)"
                        case 24: vo.setId_tipomedida(24); break;//24	"Prato(s) Fundo(s)"
                        case 25: vo.setId_tipomedida(25); break;//25	"Pitada(s)"
                        case 26: vo.setId_tipomedida(26); break;//26	"Lata(s)"
                    }
                    
                    vo.setCaloria(ln.sbi(4));//HHHH
                    vo.setCarboidrato(ln.sbd(3));//III
                    vo.setCarboidratoinferior(ln.sbb(1));//J
                    vo.setProteina(ln.sbd(2));//LL
                    vo.setProteinainferior(ln.sbb(1));//M
                    vo.setGordura(ln.sbd(3, 1));//NNN
                    vo.setGordurasaturada(ln.sbd(3, 1));//OOO
                    ln.jump(3);//Colesterou::PPPUtils.stringToDouble(vToledo.get(i).substring(40, 43)) / 10
                    vo.setFibra(ln.sbd(2));//QQ
                    vo.setFibrainferior(ln.sbb(1));//R
                    vo.setCalcio(ln.sbd(3, 1));//SSS
                    vo.setFerro(ln.sbd(4, 2));//TTTT
                    vo.setSodio(ln.sbd(4));//UUUU
                    vo.setPercentualcaloria(ln.sbi(2));//VV
                    vo.setPercentualcarboidrato(ln.sbi(2));//XX
                    vo.setPercentualproteina(ln.sbi(2));//ZZ
                    vo.setPercentualgordura(ln.sbi(2));//WW
                    vo.setPercentualgordurasaturada(ln.sbi(2));//YY
                    ln.jump(2);//Percentual Colesterou: KK                    
                    vo.setPercentualfibra(ln.sbi(2));//&&
                    vo.setPercentualcalcio(ln.sbi(2));//##
                    vo.setPercentualferro(ln.sbi(2));//**
                    vo.setPercentualsodio(ln.sbi(2));//$$

                    result.add(vo);
                }
            }
        }
        return result;
    }

    private void salvarNutricionalProduto(List<NutricionalToledoIMP> nutricional, String sistema, String loja) throws Exception {
        ProgressBar.setMaximum(nutricional.size());
        ProgressBar.setStatus("Importando Nutricional Toledo...");
        createTable();

        try (Statement stm = Conexao.createStatement()) {
            stm.execute("delete from implantacao.codant_nutricionaltoledo");
        }

        for (NutricionalToledoIMP vo : nutricional) {            
            try (Statement stm = Conexao.createStatement()) {
                SQLBuilder sql = new SQLBuilder();
                sql.setTableName("implantacao.codant_nutricionaltoledo");
                sql.put("sistema", sistema);
                sql.put("loja", loja);
                sql.put("produto", vo.getCodigo());
                sql.put("pesavel", vo.getPesavel());
                sql.put("descricao", vo.getDescricao());
                sql.put("validade", vo.getValidade());
                if (vo.getNutricional() != 0) {
                    System.out.println("Aqui");
                    sql.put("nutricional", vo.getNutricional());
                } /*else {
                    System.out.println("Aqui 2");
                    sql.put("nutricional", vo.getCodigo());
                }*/

                stm.execute(sql.getInsert());
            }
        }
    }

    private void salvarNutricionalToledo(List<NutricionalToledoVO> toledo) throws Exception {
        ProgressBar.setMaximum(toledo.size());
        ProgressBar.setStatus("Importando Info. Nutricional Toledo...");

        for (NutricionalToledoVO vo : toledo) {
            
            final MultiMap<Integer, NutricionalToledoVO> nutricionais = getNutricionalProduto(sistema, loja);
            
            NutricionalToledoVO prod = nutricionais.get(vo.getId());
            
            if (prod == null) {
                continue;
            }
            
            try (Statement stm = Conexao.createStatement()) {
                SQLBuilder sql = new SQLBuilder();
                sql.setTableName("nutricionaltoledo");
                sql.put("id", vo.getId());
                sql.put("descricao", prod.getDescricao());
                sql.put("id_situacaocadastro", vo.getId_situacaocadastro());
                sql.put("caloria", vo.getCaloria());
                sql.put("carboidrato", vo.getCarboidrato());
                sql.put("carboidratoinferior", vo.isCarboidratoinferior());
                sql.put("proteina", vo.getProteina());
                sql.put("proteinainferior", vo.isProteinainferior());
                sql.put("gordura", vo.getGordura());
                sql.put("gordurasaturada", vo.getGordurasaturada());
                sql.put("gorduratrans", vo.getGorduratrans());
                sql.put("colesterolinferior", vo.isColesterolinferior());
                sql.put("fibra", vo.getFibra());
                sql.put("fibrainferior", vo.isFibrainferior());
                sql.put("calcio", vo.getCalcio());
                sql.put("ferro", vo.getFerro());
                sql.put("sodio", vo.getSodio());
                sql.put("percentualcaloria", vo.getPercentualcaloria());
                sql.put("percentualcarboidrato", vo.getPercentualcarboidrato());
                sql.put("percentualproteina", vo.getPercentualproteina());
                sql.put("percentualgordura", vo.getPercentualgordura());
                sql.put("percentualgordurasaturada", vo.getPercentualgordurasaturada());
                sql.put("percentualfibra", vo.getPercentualfibra());
                sql.put("percentualcalcio", vo.getPercentualcalcio());
                sql.put("percentualferro", vo.getPercentualferro());
                sql.put("percentualsodio", vo.getPercentualsodio());
                sql.put("quantidade", vo.getQuantidade());
                sql.put("id_tipounidadeporcao", vo.getId_tipounidadeporcao());
                sql.put("medidainteira", vo.getMedidainteira());
                sql.put("id_tipomedidadecimal", vo.getId_tipomedidadecimal());
                sql.put("id_tipomedida", vo.getId_tipomedida());

                stm.execute(sql.getInsert());
            }
            
            int idProduto = -1;
            
            if (this.opcaoCodigo == 2) {
                idProduto = new ProdutoAnteriorDAO().getCodigoAtualEANant(sistema, loja, String.valueOf(vo.getId()));
            } else {
                
                if (ignorarUltimoDigito) {
                    idProduto = new ProdutoAnteriorDAO().getProdutoAnteriorSemUltimoDigito2(sistema, loja, String.valueOf(vo.getId()));
                } else {
                    idProduto = new ProdutoAnteriorDAO().getCodigoAnterior2(sistema, loja, String.valueOf(vo.getId()));
                }
            }
            
            if (idProduto == -1) {
                System.out.println("Produto Balança Não Encontrado: " + vo.getIdProduto() + "\n"
                        + "Desc: " + vo.getDescricao());
            } else {
                try (Statement stm = Conexao.createStatement()) {
                    SQLBuilder sql = new SQLBuilder();
                    sql.setTableName("nutricionaltoledoitem");
                    sql.put("id_nutricionaltoledo", vo.getId());
                    sql.put("id_produto", idProduto);

                    stm.execute(sql.getInsert());
                }
            }
        }
    }
    
    private MultiMap<Integer, NutricionalToledoVO> getNutricionalProduto(String sistema, String loja) throws Exception {
        MultiMap<Integer, NutricionalToledoVO> result = new MultiMap<>();
        try(Statement stm = Conexao.createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	sistema,\n" +
                    "	loja,\n" +
                    "	nutricional,\n" +
                    "	descricao,\n" +
                    "	produto \n" +
                    "from \n" +
                    "	implantacao.codant_nutricionaltoledo where nutricional != 0 and\n" +
                    "   sistema = '" + sistema + "' and loja = '" + loja + "'\n" +       
                    "order by \n" +
                    "	nutricional")) {
                while(rs.next()) {
                    NutricionalToledoVO vo = new NutricionalToledoVO();
                    vo.setDescricao(rs.getString("descricao"));
                    vo.setId(rs.getInt("nutricional"));
                    vo.setIdProduto(rs.getInt("produto"));
                    
                    result.put(vo, vo.getId());
                }
            }
        }
        return result;
    }

    private void createTable() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute(
                    "create table if not exists implantacao.codant_nutricionaltoledo(\n"
                    + "	sistema varchar not null,\n"
                    + "	loja varchar not null,\n"
                    + "	nutricional integer,\n"
                    + "	produto integer,\n"
                    + "	pesavel character(1),\n"
                    + "	descricao character varying,\n"
                    + "	validade integer\n"
                    + ");");
        }
    }

    public static void importarNutricionalToledoProduto(String arquivo) throws Exception {
        ProgressBar.setStatus("Carregando dados...Nutricional Toledo Produto...");
        List<NutricionalToledoIMP> nutricionalToledo = new NutricionalToledoDAO().getNutricionalToledoProduto(arquivo);
        new NutricionalToledoDAO().salvarNutricionalProduto(nutricionalToledo, sistema, loja);
    }
    
    public static void importarNutricionalToledo(String arquivo) throws Exception {
        ProgressBar.setStatus("Importando dados...Nutricional Toledo...");
        List<NutricionalToledoVO> nutri = new NutricionalToledoDAO().getNutricionalToledo(arquivo);
        new NutricionalToledoDAO().salvarNutricionalToledo(nutri);
    }
    
    public static void importarNutricionalToledo(String arquivo, int opcaoCodigo, boolean ignorarUltimoDigito) throws Exception {
        ProgressBar.setStatus("Importando dados...Nutricional Toledo...");
        List<NutricionalToledoVO> nutri = new NutricionalToledoDAO().getNutricionalToledo(arquivo);
        NutricionalToledoDAO dao = new NutricionalToledoDAO();
        dao.ignorarUltimoDigito = ignorarUltimoDigito;
        dao.opcaoCodigo = opcaoCodigo;
        dao.salvarNutricionalToledo(nutri);
    }
}

class StringLine {
    
    private final String ln;
    private int index = 0;

    StringLine(String ln) {
        this.ln = ln == null ? "" : ln.trim();
    }
    
    boolean isEmpty() {
        return ln.isEmpty();
    }
    
    String sb(int length) {
        int i;
        if (this.ln.length() >= index + length) {
            i = length;
        } else {
            i = ln.length() - index;
        }
        
        if (i == 0) {
            return "";
        }
        
        int iniPos = index;
        jump(i);
        return ln.substring(iniPos, index);
    }
    
    int sbi(int length) {
        return Utils.stringToInt(sb(length));
    }
    
    double sbd(int length) {
        return Utils.stringToDouble(sb(length));
    }
    
    double sbd(int length, int decimal) {
        double fator = Math.pow(10, decimal);
        return Utils.stringToDouble(sb(length)) / fator;
    }
    
    boolean sbb(int length) {
        return Utils.stringToBool(sb(length));
    }

    void jump(int i) {
        index += i;
    }
    
}