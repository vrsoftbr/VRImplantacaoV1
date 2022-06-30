/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao.dao.cadastro;

import java.sql.Statement;
import java.util.logging.Logger;
import vrframework.classe.Conexao;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.NutricionalFilizolaItemVO;
import vrimplantacao.vo.vrimplantacao.NutricionalFilizolaVO;
import vrimplantacao2.utils.sql.SQLBuilder;

public class NutricionalFilizolaDAO {

    private static final Logger LOG = Logger.getLogger(NutricionalFilizolaDAO.class.getName());
    Utils util = new Utils();

    public void gravar(NutricionalFilizolaVO i_nutricionalFilizola) throws Exception {
        SQLBuilder sql = null;
        try (Statement stm = Conexao.createStatement()) {

            sql = new SQLBuilder();
            sql.setTableName("nutricionalfilizola");
            sql.put("id", i_nutricionalFilizola.getId());
            sql.put("descricao", i_nutricionalFilizola.getDescricao());
            sql.put("id_situacaocadastro", i_nutricionalFilizola.getId_situacaocadastro());
            sql.put("caloria", i_nutricionalFilizola.getCaloria());
            sql.put("carboidrato", i_nutricionalFilizola.getCarboidrato());
            sql.put("carboidratoinferior", i_nutricionalFilizola.isCarboidratoinferior());
            sql.put("proteina", i_nutricionalFilizola.getProteina());
            sql.put("proteinainferior", i_nutricionalFilizola.isProteinainferior());
            sql.put("gordura", i_nutricionalFilizola.getGordura());
            sql.put("gordurasaturada", i_nutricionalFilizola.getGordurasaturada());
            sql.put("gorduratrans", i_nutricionalFilizola.getGorduratrans());
            sql.put("colesterolinferior", i_nutricionalFilizola.isColesterolinferior());
            sql.put("fibra", i_nutricionalFilizola.getFibra());
            sql.put("fibrainferior", i_nutricionalFilizola.isFibrainferior());
            sql.put("calcio", i_nutricionalFilizola.getCalcio());
            sql.put("ferro", i_nutricionalFilizola.getFerro());
            sql.put("sodio", i_nutricionalFilizola.getSodio());
            sql.put("percentualcaloria", i_nutricionalFilizola.getPercentualcaloria());
            sql.put("percentualcarboidrato", i_nutricionalFilizola.getPercentualcarboidrato());
            sql.put("percentualproteina", i_nutricionalFilizola.getPercentualproteina());
            sql.put("percentualgordura", i_nutricionalFilizola.getPercentualgordura());
            sql.put("percentualgordurasaturada", i_nutricionalFilizola.getPercentualgordurasaturada());
            sql.put("percentualfibra", i_nutricionalFilizola.getPercentualfibra());
            sql.put("percentualcalcio", i_nutricionalFilizola.getPercentualcalcio());
            sql.put("percentualferro", i_nutricionalFilizola.getPercentualferro());
            sql.put("percentualsodio", i_nutricionalFilizola.getPercentualsodio());
            sql.put("porcao", i_nutricionalFilizola.getPorcao());
            sql.put("mensagemalergico", i_nutricionalFilizola.getMensagemAlergico());

            stm.execute(sql.getInsert());
            LOG.finest(sql.toString());
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void gravarItem(NutricionalFilizolaItemVO i_nutricionalFilizolaItem) throws Exception {
        SQLBuilder sql = null;
        try (Statement stm = Conexao.createStatement()) {
            sql = new SQLBuilder();
            sql.setTableName("nutricionalfilizolaitem");
            sql.put("id_nutricionalfilizola", i_nutricionalFilizolaItem.getId_nutricionalfilizola());
            sql.put("id_produto", i_nutricionalFilizolaItem.getId_produto());
            stm.execute(sql.getInsert());
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void gravarItemArquivoRdc360(int id, int codigoAtual) throws Exception {
        SQLBuilder sql = null;
        try (Statement stm = Conexao.createStatement()) {
            sql = new SQLBuilder();
            sql.setTableName("nutricionalfilizolaitem");
            sql.put("id_nutricionalfilizola", id);
            sql.put("id_produto", codigoAtual);
            stm.execute(sql.getInsert());
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
