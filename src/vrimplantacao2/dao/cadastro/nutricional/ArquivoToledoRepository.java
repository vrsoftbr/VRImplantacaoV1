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
    public String sistema = ToledoService.sistema;
    public String loja = ToledoService.loja;
    public boolean ignorarUltimoDigito = ToledoService.ignorarUltimoDigito;
    public int opcaoCodigo = ToledoService.opcaoCodigo;
        
    public void salvarNutricionalToledoINFNUTRI(List<NutricionalToledoVO> toledo) throws Exception {
        ProgressBar.setStatus("Importando Info. Nutricional Toledo...");   
        ProgressBar.setMaximum(toledo.size());

        for (NutricionalToledoVO vo : toledo) {
            final MultiMap<Integer, NutricionalToledoVO> nutricionais = dao.getNutricionalProduto(sistema, loja);

            NutricionalToledoVO prod = nutricionais.get(vo.getId());

            if (prod == null) {
                continue;
            }
            dao.salvarNutricionalToledo(vo, prod);

            int idProduto = -1;

            if (opcaoCodigo == 2) {
                idProduto = new ProdutoAnteriorDAO().getCodigoAtualEANant(sistema + " - 1", loja, String.valueOf(vo.getId()));
                System.out.println(idProduto);
            } else {

                if (ignorarUltimoDigito) {
                    idProduto = new ProdutoAnteriorDAO().getProdutoAnteriorSemUltimoDigito2(sistema + " - 1", loja, String.valueOf(vo.getId()));
                    System.out.println(idProduto);
                } else {
                    idProduto = new ProdutoAnteriorDAO().getCodigoAnterior2(sistema + " - 1", loja, String.valueOf(vo.getId()));
                    System.out.println(vo.getId());
                    System.out.println(idProduto);
                }
            }

            if (idProduto == -1) {
                System.out.println("Produto Balança Não Encontrado: " + vo.getIdProduto() + "\n"
                        + "Desc: " + vo.getDescricao());
            } else {
                dao.salvarToledoItem(vo, idProduto);
            }
            
            ProgressBar.next();
        }
    }
    
    public void salvarNutricionalProdutoITENSMGV(List<NutricionalToledoIMP> nutricional) throws Exception {
        ProgressBar.setMaximum(nutricional.size());
        ProgressBar.setStatus("Importando Nutricional Toledo...");
        dao.createTable();

        dao.limparAnterior();
        
        for (NutricionalToledoIMP vo : nutricional) { 
            dao.salvarAnterior(vo, sistema, loja);
        }
    }
}
