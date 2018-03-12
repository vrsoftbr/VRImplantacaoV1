package vrimplantacao2.dao.cadastro.nutricional;

import java.sql.ResultSet;
import java.sql.Statement;
import vrframework.classe.Conexao;
import vrimplantacao.vo.vrimplantacao.NutricionalToledoVO;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.utils.sql.SQLBuilder;

/**
 *
 * @author Leandro
 */
public class ToledoDAO {

    public void gravar(NutricionalToledoVO vo) throws Exception {        
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();  
            
            sql.setTableName("nutricionaltoledo");
            sql.getReturning().add("id");
            sql.put("descricao", vo.getDescricao());
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
            sql.put("id_tipounidadeporcao", 0);
            sql.put("medidainteira", 1);
            sql.put("id_tipomedidadecimal", 0);
            sql.put("id_tipomedida", 5);
            
            for (int i = 0; i < vo.getMensagemAlergico().size(); i++) {
                sql.put("mensagemalergico" + (i + 1), vo.getMensagemAlergico().get(i));
            }
            
            try (ResultSet rst = stm.executeQuery(
                    sql.getInsert()                    
            )) {
                if (rst.next()) {
                    vo.setId(rst.getInt("id"));
                }
            }
        }
    }

    public MultiMap<Integer, Void> getNutricionais() throws Exception {
        MultiMap<Integer, Void> result = new MultiMap<>();
        
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n" +
                    "	id_nutricionaltoledo,\n" +
                    "	id_produto\n" +
                    "from\n" +
                    "	nutricionaltoledoitem\n" +
                    "order by 1,2"
            )) {
                while (rst.next()) {
                    result.put(null, rst.getInt("id_nutricionaltoledo"), rst.getInt("id_produto"));
                }
            }
        }
        
        return result;
    }

    public void gravarItem(Integer idNutricional, Integer idProduto) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            sql.setTableName("nutricionaltoledoitem");
            sql.put("id_nutricionaltoledo", idNutricional);
            sql.put("id_produto", idProduto);            
            stm.executeQuery(sql.getInsert());
        }
    }
    
}
