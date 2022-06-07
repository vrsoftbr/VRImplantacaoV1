/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.cadastro.nutricional;

import java.util.List;
import vrframework.classe.ProgressBar;
import vrimplantacao.vo.vrimplantacao.NutricionalToledoVO;
import vrimplantacao2.dao.cadastro.produto.ProdutoAnteriorDAO;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.importacao.NutricionalToledoIMP;

/**
 *
 * @author Michael
 */
public class ArquivoToledoRepository {
        
    public ArquivoToledoDAO dao = new ArquivoToledoDAO();
    NutricionalRepositoryProvider provider;

        
    public void salvarNutricionalToledoINFNUTRI(List<NutricionalToledoVO> toledo) throws Exception {
        ProgressBar.setMaximum(toledo.size());
        ProgressBar.setStatus("Importando Info. Nutricional Toledo...");

        for (NutricionalToledoVO vo : toledo) {

            final MultiMap<Integer, NutricionalToledoVO> nutricionais = dao.getNutricionalProduto(provider.getSistema(), provider.getLoja());

            NutricionalToledoVO prod = nutricionais.get(vo.getId());

            if (prod == null) {
                continue;
            }
            dao.salvarNutricionalToledo(vo, prod);

            int idProduto = -1;

            if (provider.getOpcaoCodigo() == 2) {
                idProduto = new ProdutoAnteriorDAO().getCodigoAtualEANant(provider.getSistema(), provider.getLoja(), String.valueOf(vo.getId()));
            } else {

                if (provider.isIgnorarUltimoDigito()) {
                    idProduto = new ProdutoAnteriorDAO().getProdutoAnteriorSemUltimoDigito2(provider.getSistema(), provider.getLoja(), String.valueOf(vo.getId()));
                } else {
                    idProduto = new ProdutoAnteriorDAO().getCodigoAnterior2(provider.getSistema(), provider.getLoja(), String.valueOf(vo.getId()));
                }
            }

            if (idProduto == -1) {
                System.out.println("Produto Balança Não Encontrado: " + vo.getIdProduto() + "\n"
                        + "Desc: " + vo.getDescricao());
            } else {
                dao.salvarToledoItem(vo, idProduto);
            }
        }
    }
    
    public void salvarNutricionalProdutoITENSMGV(List<NutricionalToledoIMP> nutricional) throws Exception {
        ProgressBar.setMaximum(nutricional.size());
        ProgressBar.setStatus("Importando Nutricional Toledo...");
        dao.createTable();

        dao.limparAnterior();

        for (NutricionalToledoIMP vo : nutricional) {            
            dao.salvarAnterior(vo, provider.getSistema(), provider.getLoja());
        }
    }
}
