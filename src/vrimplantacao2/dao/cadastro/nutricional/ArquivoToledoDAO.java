/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.cadastro.nutricional;

import java.sql.ResultSet;
import java.sql.Statement;
import vrframework.classe.Conexao;
import vrimplantacao.vo.vrimplantacao.NutricionalToledoVO;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.importacao.NutricionalToledoIMP;

/**
 *
 * @author Michael
 */
public class ArquivoToledoDAO {

    public void createTable() throws Exception {
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

    public void salvarNutricionalToledo(NutricionalToledoVO vo, NutricionalToledoVO prod) throws Exception {
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

    }

    public MultiMap<Integer, NutricionalToledoVO> getNutricionalProduto(String sistema, String loja) throws Exception {
        MultiMap<Integer, NutricionalToledoVO> result = new MultiMap<>();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	sistema,\n"
                    + "	loja,\n"
                    + "	nutricional,\n"
                    + "	descricao,\n"
                    + "	produto \n"
                    + "from \n"
                    + "	implantacao.codant_nutricionaltoledo where nutricional != 0 and\n"
                    + "   sistema = '" + sistema + "' and loja = '" + loja + "'\n"
                    + "order by \n"
                    + "	nutricional")) {
                while (rs.next()) {
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

    public void salvarToledoItem(NutricionalToledoVO vo, int idProduto) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            sql.setTableName("nutricionaltoledoitem");
            sql.put("id_nutricionaltoledo", vo.getId());
            sql.put("id_produto", idProduto);

            stm.execute(sql.getInsert());
        }
    }

    public void limparAnterior() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute("delete from implantacao.codant_nutricionaltoledo");
        }
    }

    void salvarAnterior(NutricionalToledoIMP vo, String sistema, String loja) throws Exception {
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
            }
            /*else {
                    System.out.println("Aqui 2");
                    sql.put("nutricional", vo.getCodigo());
                }*/

            stm.execute(sql.getInsert());
        }
    }
}
