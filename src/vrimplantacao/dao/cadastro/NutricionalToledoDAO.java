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
import vrimplantacao.vo.vrimplantacao.NutricionalToledoVO;
import vrimplantacao.vo.vrimplantacao.NutricionalToledoItemVO;
import vrimplantacao2.dao.cadastro.produto.ProdutoAnteriorDAO;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.ProdutoAnteriorVO;

public class NutricionalToledoDAO {

    public void salvar(List<NutricionalToledoVO> v_nutricionalToledo) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;

        try {
            Conexao.begin();
            stm = Conexao.createStatement();

            ProgressBar.setStatus("Importando dados...Nutricional Toledo...");
            ProgressBar.setMaximum(v_nutricionalToledo.size());
            Map<Double, CodigoAnteriorVO> anteriores = new CodigoAnteriorDAO().carregarCodigoAnterior();

            for (NutricionalToledoVO i_nutricionalToledo : v_nutricionalToledo) {

                for (NutricionalToledoItemVO i_nutricionalToledoItem
                        : i_nutricionalToledo.vNutricionalToledoItem) {
                    i_nutricionalToledo.setId(new CodigoInternoDAO().get("nutricionaltoledo"));

                    CodigoAnteriorVO codigoAnterior = anteriores.get(i_nutricionalToledoItem.getId_produtoDouble());

                    if (codigoAnterior != null) {

                        i_nutricionalToledo.setId(new CodigoInternoDAO().get("nutricionaltoledo"));
                        
                        sql = new StringBuilder();
                        sql.append("INSERT INTO nutricionaltoledo( ");
                        sql.append("id, descricao, id_situacaocadastro, caloria, carboidrato, carboidratoinferior, ");
                        sql.append("proteina, proteinainferior, gordura, gordurasaturada, gorduratrans, ");
                        sql.append("colesterolinferior, fibra, fibrainferior, calcio, ferro, sodio, ");
                        sql.append("percentualcaloria, percentualcarboidrato, percentualproteina, ");
                        sql.append("percentualgordura, percentualgordurasaturada, percentualfibra, ");
                        sql.append("percentualcalcio, percentualferro, percentualsodio, quantidade, ");
                        sql.append("id_tipounidadeporcao, medidainteira, id_tipomedidadecimal, id_tipomedida) ");
                        sql.append("VALUES ( ");
                        sql.append(i_nutricionalToledo.getId() + ", ");
                        sql.append("'" + i_nutricionalToledo.getDescricao() + "', ");
                        sql.append(i_nutricionalToledo.getId_situacaocadastro() + ", ");
                        sql.append(i_nutricionalToledo.getCaloria() + ", ");
                        sql.append(i_nutricionalToledo.getCarboidrato() + ", ");
                        sql.append(i_nutricionalToledo.isCarboidratoinferior() + ", ");
                        sql.append(i_nutricionalToledo.getProteina() + ", ");
                        sql.append(i_nutricionalToledo.isProteinainferior() + ", ");
                        sql.append(i_nutricionalToledo.getGordura() + ", ");
                        sql.append(i_nutricionalToledo.getGordurasaturada() + ", ");
                        sql.append(i_nutricionalToledo.getGorduratrans() + ", ");
                        sql.append(i_nutricionalToledo.isColesterolinferior() + ", ");
                        sql.append(i_nutricionalToledo.getFibra() + ", ");
                        sql.append(i_nutricionalToledo.isFibrainferior() + ", ");
                        sql.append(i_nutricionalToledo.getCalcio() + ", ");
                        sql.append(i_nutricionalToledo.getFerro() + ", ");
                        sql.append(i_nutricionalToledo.getSodio() + ", ");
                        sql.append(i_nutricionalToledo.getPercentualcaloria() + ", ");
                        sql.append(i_nutricionalToledo.getPercentualcarboidrato() + ", ");
                        sql.append(i_nutricionalToledo.getPercentualproteina() + ", ");
                        sql.append(i_nutricionalToledo.getPercentualgordura() + ", ");
                        sql.append(i_nutricionalToledo.getPercentualgordurasaturada() + ", ");
                        sql.append(i_nutricionalToledo.getPercentualfibra() + ", ");
                        sql.append(i_nutricionalToledo.getPercentualcalcio() + ", ");
                        sql.append(i_nutricionalToledo.getPercentualferro() + ", ");
                        sql.append(i_nutricionalToledo.getPercentualsodio() + ", ");
                        sql.append(i_nutricionalToledo.getQuantidade() + ", ");
                        sql.append(i_nutricionalToledo.getId_tipounidadeporcao() + ", ");
                        sql.append(i_nutricionalToledo.getMedidainteira() + ", ");
                        sql.append(i_nutricionalToledo.getId_tipomedidadecimal() + ", ");
                        sql.append(i_nutricionalToledo.getId_tipomedida() + "");
                        sql.append(");");
                        stm.execute(sql.toString());

                        i_nutricionalToledoItem.setId_nutricionaltoledo(i_nutricionalToledo.getId());
                        i_nutricionalToledoItem.setId_produto((int) codigoAnterior.getCodigoatual());

                        sql = new StringBuilder();
                        sql.append("INSERT INTO nutricionaltoledoitem( ");
                        sql.append("id_nutricionaltoledo, id_produto) ");
                        sql.append("VALUES ( ");
                        sql.append(i_nutricionalToledoItem.getId_nutricionaltoledo() + ", ");
                        sql.append(i_nutricionalToledoItem.getId_produto() + "");
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

    public void salvarV2(List<NutricionalToledoVO> v_nutricionalToledo, String sistema, String loja) throws Exception {
        Conexao.begin();
        try (Statement stm = Conexao.createStatement()) {            

            ProgressBar.setStatus("Importando dados...Nutricional Toledo...");
            ProgressBar.setMaximum(v_nutricionalToledo.size());
            ProdutoAnteriorDAO dao = new ProdutoAnteriorDAO();
            dao.setImportSistema(sistema);
            dao.setImportLoja(loja);
            MultiMap<String, ProdutoAnteriorVO> anteriores = dao.getCodigoAnterior();

            for (NutricionalToledoVO i_nutricionalToledo : v_nutricionalToledo) {

                for (NutricionalToledoItemVO i_nutricionalToledoItem
                        : i_nutricionalToledo.vNutricionalToledoItem) {

                    ProdutoAnteriorVO codigoAnterior = anteriores.get(sistema, loja, i_nutricionalToledoItem.getStrID());

                    if (codigoAnterior != null) {

                        i_nutricionalToledo.setId(new CodigoInternoDAO().get("nutricionaltoledo"));
                        
                        {
                            StringBuilder sql = new StringBuilder();
                            sql.append("INSERT INTO nutricionaltoledo( ");
                            sql.append("id, descricao, id_situacaocadastro, caloria, carboidrato, carboidratoinferior, ");
                            sql.append("proteina, proteinainferior, gordura, gordurasaturada, gorduratrans, ");
                            sql.append("colesterolinferior, fibra, fibrainferior, calcio, ferro, sodio, ");
                            sql.append("percentualcaloria, percentualcarboidrato, percentualproteina, ");
                            sql.append("percentualgordura, percentualgordurasaturada, percentualfibra, ");
                            sql.append("percentualcalcio, percentualferro, percentualsodio, quantidade, ");
                            sql.append("id_tipounidadeporcao, medidainteira, id_tipomedidadecimal, id_tipomedida) ");
                            sql.append("VALUES ( ");
                            sql.append(i_nutricionalToledo.getId() + ", ");
                            sql.append("'" + i_nutricionalToledo.getDescricao() + "', ");
                            sql.append(i_nutricionalToledo.getId_situacaocadastro() + ", ");
                            sql.append(i_nutricionalToledo.getCaloria() + ", ");
                            sql.append(i_nutricionalToledo.getCarboidrato() + ", ");
                            sql.append(i_nutricionalToledo.isCarboidratoinferior() + ", ");
                            sql.append(i_nutricionalToledo.getProteina() + ", ");
                            sql.append(i_nutricionalToledo.isProteinainferior() + ", ");
                            sql.append(i_nutricionalToledo.getGordura() + ", ");
                            sql.append(i_nutricionalToledo.getGordurasaturada() + ", ");
                            sql.append(i_nutricionalToledo.getGorduratrans() + ", ");
                            sql.append(i_nutricionalToledo.isColesterolinferior() + ", ");
                            sql.append(i_nutricionalToledo.getFibra() + ", ");
                            sql.append(i_nutricionalToledo.isFibrainferior() + ", ");
                            sql.append(i_nutricionalToledo.getCalcio() + ", ");
                            sql.append(i_nutricionalToledo.getFerro() + ", ");
                            sql.append(i_nutricionalToledo.getSodio() + ", ");
                            sql.append(i_nutricionalToledo.getPercentualcaloria() + ", ");
                            sql.append(i_nutricionalToledo.getPercentualcarboidrato() + ", ");
                            sql.append(i_nutricionalToledo.getPercentualproteina() + ", ");
                            sql.append(i_nutricionalToledo.getPercentualgordura() + ", ");
                            sql.append(i_nutricionalToledo.getPercentualgordurasaturada() + ", ");
                            sql.append(i_nutricionalToledo.getPercentualfibra() + ", ");
                            sql.append(i_nutricionalToledo.getPercentualcalcio() + ", ");
                            sql.append(i_nutricionalToledo.getPercentualferro() + ", ");
                            sql.append(i_nutricionalToledo.getPercentualsodio() + ", ");
                            sql.append(i_nutricionalToledo.getQuantidade() + ", ");
                            sql.append(i_nutricionalToledo.getId_tipounidadeporcao() + ", ");
                            sql.append(i_nutricionalToledo.getMedidainteira() + ", ");
                            sql.append(i_nutricionalToledo.getId_tipomedidadecimal() + ", ");
                            sql.append(i_nutricionalToledo.getId_tipomedida() + "");
                            sql.append(");");
                            stm.execute(sql.toString());
                        }

                        i_nutricionalToledoItem.setId_nutricionaltoledo(i_nutricionalToledo.getId());
                        i_nutricionalToledoItem.setId_produto((int) codigoAnterior.getCodigoAtual().getId());

                        {
                            StringBuilder sql = new StringBuilder();                        
                            sql.append("INSERT INTO nutricionaltoledoitem( ");
                            sql.append("id_nutricionaltoledo, id_produto) ");
                            sql.append("VALUES ( ");
                            sql.append(i_nutricionalToledoItem.getId_nutricionaltoledo() + ", ");
                            sql.append(i_nutricionalToledoItem.getId_produto() + "");
                            sql.append(");");
                            stm.execute(sql.toString());
                        }

                        ProgressBar.next();

                    }
                }
            }
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }
}
