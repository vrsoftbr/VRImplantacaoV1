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
import vrimplantacao.vo.vrimplantacao.NutricionalToledoItemVO;
import vrimplantacao.vo.vrimplantacao.NutricionalToledoVO;
import vrimplantacao2.dao.cadastro.nutricional.ToledoDAO;
import vrimplantacao2.dao.cadastro.produto.ProdutoAnteriorDAO;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.ProdutoAnteriorVO;

/**
 *
 * @author Michael
 */
public class NutricionalToledoRepository {

    ToledoDAO daoToledo = new ToledoDAO();

    public void salvarClassesEspecificas(List<NutricionalToledoVO> v_nutricionalToledo, String sistema, String lojaOrigem) throws Exception {
        if (sistema.isEmpty() && lojaOrigem.isEmpty() || "".equals(sistema) && "".equals(lojaOrigem)) {
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

                            daoToledo.gravar(i_nutricionalToledo);

                            i_nutricionalToledoItem.setId_nutricionaltoledo(i_nutricionalToledo.getId());
                            i_nutricionalToledoItem.setId_produto((int) codigoAnterior.getCodigoatual());

                            daoToledo.gravarItem(i_nutricionalToledoItem.getId_nutricionaltoledo(), i_nutricionalToledoItem.getId_produto());

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
        } else {
            Conexao.begin();
            try (Statement stm = Conexao.createStatement()) {

                ProgressBar.setStatus("Importando dados...Nutricional Toledo...");
                ProgressBar.setMaximum(v_nutricionalToledo.size());
                ProdutoAnteriorDAO dao = new ProdutoAnteriorDAO();
                dao.setImportSistema(sistema);
                dao.setImportLoja(lojaOrigem);
                MultiMap<String, ProdutoAnteriorVO> anteriores = dao.getCodigoAnterior();

                for (NutricionalToledoVO i_nutricionalToledo : v_nutricionalToledo) {

                    for (NutricionalToledoItemVO i_nutricionalToledoItem
                            : i_nutricionalToledo.vNutricionalToledoItem) {

                        ProdutoAnteriorVO codigoAnterior = anteriores.get(sistema, lojaOrigem, i_nutricionalToledoItem.getStrID());

                        if (codigoAnterior != null) {

                            i_nutricionalToledo.setId(new CodigoInternoDAO().get("nutricionaltoledo"));

                            {
                                daoToledo.gravar(i_nutricionalToledo);
                            }

                            i_nutricionalToledoItem.setId_nutricionaltoledo(i_nutricionalToledo.getId());
                            i_nutricionalToledoItem.setId_produto((int) codigoAnterior.getCodigoAtual().getId());

                            {
                                daoToledo.gravarItem(i_nutricionalToledoItem.getId_nutricionaltoledo(), i_nutricionalToledoItem.getId_produto());
                            }

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

}
