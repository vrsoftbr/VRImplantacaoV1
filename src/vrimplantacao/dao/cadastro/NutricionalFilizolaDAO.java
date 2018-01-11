/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao.dao.cadastro;

import java.sql.Statement;
import java.util.List;
import java.util.Map;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao.dao.CodigoInternoDAO;
import vrimplantacao.vo.vrimplantacao.CodigoAnteriorVO;
import vrimplantacao.vo.vrimplantacao.NutricionalFilizolaItemVO;
import vrimplantacao.vo.vrimplantacao.NutricionalFilizolaVO;
import vrimplantacao2.dao.cadastro.produto.ProdutoAnteriorDAO;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.ProdutoAnteriorVO;

public class NutricionalFilizolaDAO {

    public void salvar(List<NutricionalFilizolaVO> v_nutricionalFilizola) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;

        try {
            Conexao.begin();
            stm = Conexao.createStatement();

            ProgressBar.setStatus("Importando dados...Nutricional Filizola...");
            ProgressBar.setMaximum(v_nutricionalFilizola.size());
            Map<Double, CodigoAnteriorVO> anteriores = new CodigoAnteriorDAO().carregarCodigoAnterior();

            for (NutricionalFilizolaVO i_nutricionalFilizola : v_nutricionalFilizola) {

                for (NutricionalFilizolaItemVO i_nutricionalFilizolaItem
                        : i_nutricionalFilizola.vNutricionalFilizolaItem) {

                    CodigoAnteriorVO codigoAnterior = anteriores.get(i_nutricionalFilizolaItem.getId_produtoDouble());

                    if (codigoAnterior != null) {

                        i_nutricionalFilizola.setId(new CodigoInternoDAO().get("nutricionalfilizola"));

                        sql = new StringBuilder();
                        sql.append("INSERT INTO nutricionalfilizola( ");
                        sql.append("id, descricao, id_situacaocadastro, caloria, carboidrato, carboidratoinferior, ");
                        sql.append("proteina, proteinainferior, gordura, gordurasaturada, gorduratrans, ");
                        sql.append("colesterolinferior, fibra, fibrainferior, calcio, ferro, sodio, ");
                        sql.append("percentualcaloria, percentualcarboidrato, percentualproteina, ");
                        sql.append("percentualgordura, percentualgordurasaturada, percentualfibra, ");
                        sql.append("percentualcalcio, percentualferro, percentualsodio, porcao) ");
                        sql.append("VALUES ( ");
                        sql.append(i_nutricionalFilizola.getId() + ", ");
                        sql.append("'" + i_nutricionalFilizola.getDescricao() + "', ");
                        sql.append(i_nutricionalFilizola.getId_situacaocadastro() + ", ");
                        sql.append(i_nutricionalFilizola.getCaloria() + ", ");
                        sql.append(i_nutricionalFilizola.getCarboidrato() + ", ");
                        sql.append(i_nutricionalFilizola.isCarboidratoinferior() + ", ");
                        sql.append(i_nutricionalFilizola.getProteina() + ", ");
                        sql.append(i_nutricionalFilizola.isProteinainferior() + ", ");
                        sql.append(i_nutricionalFilizola.getGordura() + ", ");
                        sql.append(i_nutricionalFilizola.getGordurasaturada() + ", ");
                        sql.append(i_nutricionalFilizola.getGorduratrans() + ", ");
                        sql.append(i_nutricionalFilizola.isColesterolinferior() + ", ");
                        sql.append(i_nutricionalFilizola.getFibra() + ", ");
                        sql.append(i_nutricionalFilizola.isFibrainferior() + ", ");
                        sql.append(i_nutricionalFilizola.getCalcio() + ", ");
                        sql.append(i_nutricionalFilizola.getFerro() + ", ");
                        sql.append(i_nutricionalFilizola.getSodio() + ", ");
                        sql.append(i_nutricionalFilizola.getPercentualcaloria() + ", ");
                        sql.append(i_nutricionalFilizola.getPercentualcarboidrato() + ", ");
                        sql.append(i_nutricionalFilizola.getPercentualproteina() + ", ");
                        sql.append(i_nutricionalFilizola.getPercentualgordura() + ", ");
                        sql.append(i_nutricionalFilizola.getPercentualgordurasaturada() + ", ");
                        sql.append(i_nutricionalFilizola.getPercentualfibra() + ", ");
                        sql.append(i_nutricionalFilizola.getPercentualcalcio() + ", ");
                        sql.append(i_nutricionalFilizola.getPercentualferro() + ", ");
                        sql.append(i_nutricionalFilizola.getPercentualsodio() + ", ");
                        sql.append("'" + i_nutricionalFilizola.getPorcao() + "'");
                        sql.append(");");
                        stm.execute(sql.toString());

                        i_nutricionalFilizolaItem.setId_nutricionalfilizola(i_nutricionalFilizola.getId());
                        i_nutricionalFilizolaItem.setId_produto((int) codigoAnterior.getCodigoatual());
                        
                        sql = new StringBuilder();
                        sql.append("INSERT INTO nutricionalfilizolaitem( ");
                        sql.append("id_nutricionalfilizola, id_produto) ");
                        sql.append("VALUES ( ");
                        sql.append(i_nutricionalFilizolaItem.getId_nutricionalfilizola() + ", ");
                        sql.append(i_nutricionalFilizolaItem.getId_produto() + "");
                        sql.append(");");
                        stm.execute(sql.toString());

                        ProgressBar.next();
                    }
                }
            }

            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }
    
    public void salvarV2(List<NutricionalFilizolaVO> v_nutricionalFilizola, String sistema, String loja) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;

        try {
            Conexao.begin();
            stm = Conexao.createStatement();

            ProgressBar.setStatus("Importando dados...Nutricional Filizola...");
            ProgressBar.setMaximum(v_nutricionalFilizola.size());
            ProdutoAnteriorDAO dao = new ProdutoAnteriorDAO();
            dao.setImportLoja(loja);
            dao.setImportSistema(sistema);
            MultiMap<String, ProdutoAnteriorVO> anteriores = dao.getCodigoAnterior();

            for (NutricionalFilizolaVO i_nutricionalFilizola : v_nutricionalFilizola) {

                for (NutricionalFilizolaItemVO i_nutricionalFilizolaItem
                        : i_nutricionalFilizola.vNutricionalFilizolaItem) {

                    ProdutoAnteriorVO codigoAnterior = anteriores.get(sistema, loja, i_nutricionalFilizolaItem.getStrID());

                    if (codigoAnterior != null) {

                        i_nutricionalFilizola.setId(new CodigoInternoDAO().get("nutricionalfilizola"));

                        sql = new StringBuilder();
                        sql.append("INSERT INTO nutricionalfilizola( ");
                        sql.append("id, descricao, id_situacaocadastro, caloria, carboidrato, carboidratoinferior, ");
                        sql.append("proteina, proteinainferior, gordura, gordurasaturada, gorduratrans, ");
                        sql.append("colesterolinferior, fibra, fibrainferior, calcio, ferro, sodio, ");
                        sql.append("percentualcaloria, percentualcarboidrato, percentualproteina, ");
                        sql.append("percentualgordura, percentualgordurasaturada, percentualfibra, ");
                        sql.append("percentualcalcio, percentualferro, percentualsodio, porcao) ");
                        sql.append("VALUES ( ");
                        sql.append(i_nutricionalFilizola.getId() + ", ");
                        sql.append("'" + i_nutricionalFilizola.getDescricao() + "', ");
                        sql.append(i_nutricionalFilizola.getId_situacaocadastro() + ", ");
                        sql.append(i_nutricionalFilizola.getCaloria() + ", ");
                        sql.append(i_nutricionalFilizola.getCarboidrato() + ", ");
                        sql.append(i_nutricionalFilizola.isCarboidratoinferior() + ", ");
                        sql.append(i_nutricionalFilizola.getProteina() + ", ");
                        sql.append(i_nutricionalFilizola.isProteinainferior() + ", ");
                        sql.append(i_nutricionalFilizola.getGordura() + ", ");
                        sql.append(i_nutricionalFilizola.getGordurasaturada() + ", ");
                        sql.append(i_nutricionalFilizola.getGorduratrans() + ", ");
                        sql.append(i_nutricionalFilizola.isColesterolinferior() + ", ");
                        sql.append(i_nutricionalFilizola.getFibra() + ", ");
                        sql.append(i_nutricionalFilizola.isFibrainferior() + ", ");
                        sql.append(i_nutricionalFilizola.getCalcio() + ", ");
                        sql.append(i_nutricionalFilizola.getFerro() + ", ");
                        sql.append(i_nutricionalFilizola.getSodio() + ", ");
                        sql.append(i_nutricionalFilizola.getPercentualcaloria() + ", ");
                        sql.append(i_nutricionalFilizola.getPercentualcarboidrato() + ", ");
                        sql.append(i_nutricionalFilizola.getPercentualproteina() + ", ");
                        sql.append(i_nutricionalFilizola.getPercentualgordura() + ", ");
                        sql.append(i_nutricionalFilizola.getPercentualgordurasaturada() + ", ");
                        sql.append(i_nutricionalFilizola.getPercentualfibra() + ", ");
                        sql.append(i_nutricionalFilizola.getPercentualcalcio() + ", ");
                        sql.append(i_nutricionalFilizola.getPercentualferro() + ", ");
                        sql.append(i_nutricionalFilizola.getPercentualsodio() + ", ");
                        sql.append("'" + i_nutricionalFilizola.getPorcao() + "'");
                        sql.append(");");
                        stm.execute(sql.toString());

                        i_nutricionalFilizolaItem.setId_nutricionalfilizola(i_nutricionalFilizola.getId());
                        i_nutricionalFilizolaItem.setId_produto((int) codigoAnterior.getCodigoAtual().getId());
                        
                        sql = new StringBuilder();
                        sql.append("INSERT INTO nutricionalfilizolaitem( ");
                        sql.append("id_nutricionalfilizola, id_produto) ");
                        sql.append("VALUES ( ");
                        sql.append(i_nutricionalFilizolaItem.getId_nutricionalfilizola() + ", ");
                        sql.append(i_nutricionalFilizolaItem.getId_produto() + "");
                        sql.append(");");
                        stm.execute(sql.toString());

                        ProgressBar.next();
                    }
                }
            }

            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }
}