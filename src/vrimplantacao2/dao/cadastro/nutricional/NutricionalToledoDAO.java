package vrimplantacao2.dao.cadastro.nutricional;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.NutricionalToledoVO;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.importacao.NutricionalToledoIMP;

/**
 *
 * @author Importacao
 */
public class NutricionalToledoDAO {

    Utils util = new Utils();

    public List<NutricionalToledoIMP> getNutricionalToledoProduto(String arquivo) throws Exception {
        List<NutricionalToledoIMP> result = new ArrayList<>();
        List<String> vToledo = util.lerArquivoBalanca(arquivo);

        for (int i = 0; i < vToledo.size(); i++) {
            NutricionalToledoIMP toledo = new NutricionalToledoIMP();
            if (!vToledo.get(i).trim().isEmpty()) {
                if ("0".equals(vToledo.get(i).substring(2, 3))) {
                    toledo.setCodigo(Integer.parseInt(vToledo.get(i).substring(3, 9)));
                    toledo.setPesavel("P");
                    toledo.setDescricao(util.acertarTexto(vToledo.get(i).substring(18, 67).replace("'", "").trim()));
                    toledo.setValidade(Integer.parseInt(vToledo.get(i).substring(15, 18)));
                    toledo.setNutricional(Integer.parseInt(vToledo.get(i).substring(78, 84)));
                } else {
                    toledo.setCodigo(Integer.parseInt(vToledo.get(i).substring(3, 9)));
                    toledo.setPesavel("U");
                    toledo.setDescricao(util.acertarTexto(vToledo.get(i).substring(18, 67).replace("'", "").trim()));
                    toledo.setValidade(Integer.parseInt(vToledo.get(i).substring(15, 18)));
                    toledo.setNutricional(Integer.parseInt(vToledo.get(i).substring(78, 84)));
                }
            }
            result.add(toledo);
        }
        return result;
    }

    public List<NutricionalToledoVO> getNutricionalToledo(String arquivo) throws Exception {
        List<NutricionalToledoVO> result = new ArrayList<>();
        List<String> vToledo = util.lerArquivoBalanca(arquivo);

        for (int i = 0; i < vToledo.size(); i++) {
            NutricionalToledoVO vo = new NutricionalToledoVO();
            if (!vToledo.get(i).trim().isEmpty()) {
                vo.setId(Utils.stringToInt(vToledo.get(i).substring(2, 7)));
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
            if (vo.getNutricional() != 0) {
                try (Statement stm = Conexao.createStatement()) {
                    SQLBuilder sql = new SQLBuilder();
                    sql.setTableName("implantacao.codant_nutricionaltoledo");
                    sql.put("sistema", sistema);
                    sql.put("loja", loja);
                    sql.put("produto", vo.getCodigo());
                    sql.put("pesavel", vo.getPesavel());
                    sql.put("descricao", vo.getDescricao());
                    sql.put("validade", vo.getValidade());
                    sql.put("nutricional", vo.getNutricional());

                    stm.execute(sql.getInsert());
                }
            }
        }
    }

    private void salvarNutricionalToledo(List<NutricionalToledoVO> toledo) throws Exception {
        ProgressBar.setMaximum(toledo.size());
        ProgressBar.setStatus("Importando Info. Nutricional Toledo...");

        for (NutricionalToledoVO vo : toledo) {
            
            NutricionalToledoVO prod = getNutricionalProduto().get(vo.getId());
            
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
            
            try(Statement stm = Conexao.createStatement()) {
                SQLBuilder sql = new SQLBuilder();
                sql.setTableName("nutricionaltoledoitem");
                sql.put("id_nutricionaltoledo", vo.getId());
                sql.put("id_produto", prod.getIdProduto());
                
                stm.execute(sql.getInsert());
            }
        }
    }
    
    private MultiMap<Integer, NutricionalToledoVO> getNutricionalProduto() throws Exception {
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
                    "	implantacao.codant_nutricionaltoledo\n" +
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

    public static void importarNutricionalToledoProduto(String arquivo, String sistema, String loja) throws Exception {
        ProgressBar.setStatus("Carregando dados...Nutricional Toledo Produto...");
        List<NutricionalToledoIMP> nutricionalToledo = new NutricionalToledoDAO().getNutricionalToledoProduto(arquivo);
        new NutricionalToledoDAO().salvarNutricionalProduto(nutricionalToledo, sistema, loja);
    }
    
    public static void importarNutricionalToledo(String arquivo) throws Exception {
        ProgressBar.setStatus("Importando dados...Nutricional Toledo...");
        List<NutricionalToledoVO> nutri = new NutricionalToledoDAO().getNutricionalToledo(arquivo);
        new NutricionalToledoDAO().salvarNutricionalToledo(nutri);
    }
}
