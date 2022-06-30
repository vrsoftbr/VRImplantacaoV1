/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao.dao.cadastro;

import java.util.List;
import java.util.Map;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao.dao.CodigoInternoDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.CodigoAnteriorVO;
import vrimplantacao.vo.vrimplantacao.NutricionalFilizolaItemVO;
import vrimplantacao.vo.vrimplantacao.NutricionalFilizolaVO;
import vrimplantacao2.dao.cadastro.produto.ProdutoAnteriorDAO;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.ProdutoAnteriorVO;

/**
 *
 * @author Michael
 */
public class NutricionalFilizolaRepository {

    NutricionalFilizolaDAO nutricionalFilizolaDao = new NutricionalFilizolaDAO();
    Utils util = new Utils();
    boolean eKairos = false;

    public void salvarClassesEspecificas(List<NutricionalFilizolaVO> v_nutricionalFilizola, String sistema, String loja) throws Exception {
        
        if (sistema.isEmpty() && loja.isEmpty() || "".equals(sistema) && "".equals(loja)) {
            eKairos = true;
        }
        
        try {
            Conexao.begin();

            ProgressBar.setStatus("Importando dados...Nutricional Filizola...");
            ProgressBar.setMaximum(v_nutricionalFilizola.size());
            ProdutoAnteriorDAO dao = new ProdutoAnteriorDAO();
            dao.setImportLoja(loja);
            dao.setImportSistema(sistema);
            Map<Double, CodigoAnteriorVO> anterioresKairos = new CodigoAnteriorDAO().carregarCodigoAnterior();
            MultiMap<String, ProdutoAnteriorVO> anterioresdemais = dao.getCodigoAnterior();

            for (NutricionalFilizolaVO i_nutricionalFilizola : v_nutricionalFilizola) {
                for (NutricionalFilizolaItemVO i_nutricionalFilizolaItem
                        : i_nutricionalFilizola.vNutricionalFilizolaItem) {

                    if (eKairos) {
                        CodigoAnteriorVO codigoAnterior = anterioresKairos.get(i_nutricionalFilizolaItem.getId_produtoDouble());
                        if (codigoAnterior != null) {
                            i_nutricionalFilizola.setId(new CodigoInternoDAO().get("nutricionalfilizola"));
                            nutricionalFilizolaDao.gravar(i_nutricionalFilizola);
                            i_nutricionalFilizolaItem.setId_nutricionalfilizola(i_nutricionalFilizola.getId());
                            i_nutricionalFilizolaItem.setId_produto((int) codigoAnterior.getCodigoatual());
                            nutricionalFilizolaDao.gravarItem(i_nutricionalFilizolaItem);
                            ProgressBar.next();
                        }
                    } else {
                        ProdutoAnteriorVO codigoAnterior = anterioresdemais.get(sistema, loja, i_nutricionalFilizolaItem.getStrID());
                        if (codigoAnterior != null && codigoAnterior.getCodigoAtual() != null) {
                            i_nutricionalFilizola.setId(new CodigoInternoDAO().get("nutricionalfilizola"));
                            if (i_nutricionalFilizola.getDescricao() != null && "".equals(i_nutricionalFilizola.getDescricao().trim())) {
                                i_nutricionalFilizola.setDescricao(codigoAnterior.getCodigoAtual().getDescricaoCompleta());
                            }
                            nutricionalFilizolaDao.gravar(i_nutricionalFilizola);
                            i_nutricionalFilizolaItem.setId_nutricionalfilizola(i_nutricionalFilizola.getId());
                            i_nutricionalFilizolaItem.setId_produto((int) codigoAnterior.getCodigoAtual().getId());
                            nutricionalFilizolaDao.gravarItem(i_nutricionalFilizolaItem);
                            ProgressBar.next();
                        }
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
