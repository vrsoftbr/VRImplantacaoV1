package vrimplantacao2.dao.cadastro.nutricional;

import java.sql.ResultSet;
import java.sql.Statement;
import vrframework.classe.Conexao;
import vrimplantacao.vo.vrimplantacao.NutricionalFilizolaVO;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.utils.sql.SQLBuilder;

/**
 *
 * @author Leandro
 */
public class FilizolaDAO {

    public void gravar(NutricionalFilizolaVO vo) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();  
            
            sql.setTableName("nutricionalfilizola");
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
            sql.put("porcao", vo.getPorcao());
            sql.put("mensagemalergico", vo.getMensagemAlergico());
            
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
                    "	id_nutricionalfilizola,\n" +
                    "	id_produto\n" +
                    "from\n" +
                    "	nutricionalfilizolaitem\n" +
                    "order by 1,2"
            )) {
                while (rst.next()) {
                    result.put(null, rst.getInt("id_nutricionalfilizola"), rst.getInt("id_produto"));
                }
            }
        }
        
        return result;
    }

    public void gravarItem(Integer idNutricional, Integer idProduto) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            sql.setTableName("nutricionalfilizolaitem");
            sql.put("id_nutricionalfilizola", idNutricional);
            sql.put("id_produto", idProduto);            
            stm.executeQuery(sql.getInsert());
        }
    }
    
}
