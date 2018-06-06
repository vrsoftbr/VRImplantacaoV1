/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.cadastro.fiscal.inventario;

import com.sun.tools.javac.jvm.ByteCodes;
import java.util.List;
import vrframework.classe.ProgressBar;
import vrimplantacao2.dao.cadastro.produto.ProdutoAnteriorDAO;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.fiscal.inventario.InventarioVO;
import vrimplantacao2.vo.cadastro.fiscal.inventario.InventarioVOIMP;
import vrimplantacao2.vo.enums.Icms;
import vrimplantacao2.vo.importacao.InventarioIMP;

/**
 *
 * @author lucasrafael
 */
public class InventarioRepository {

    private InventarioRepositoryProvider provider;

    public InventarioRepository(InventarioRepositoryProvider provider) throws Exception {
        this.provider = provider;
    }

    public void importarInventario(List<InventarioIMP> inventario) throws Exception {
        ProdutoAnteriorDAO produtoAnteriorDAO = new ProdutoAnteriorDAO();
        System.gc();

        this.provider.begin();
        try {

            //<editor-fold defaultstate="collapsed" desc="Gerando as listagens necessárias para trabalhar com a importação">
            setNotificacao("Preparando para gravar operadores...", inventario.size());
            MultiMap<String, InventarioVOIMP> inventarios = provider.getInventarios();
            //</editor-fold>
            
            setNotificacao("Gravando operador...", inventario.size());
            
            for (InventarioIMP imp : inventario) {
                
                int idProduto = produtoAnteriorDAO.getCodigoAnterior2(provider.getSistema(), provider.getLojaOrigem(), imp.getIdProduto());
                InventarioVOIMP inv = inventarios.get(
                        String.valueOf(provider.getLojaVR()),
                        String.valueOf(idProduto),
                        String.valueOf(imp.getData())
                );
                
                InventarioVO vo = null;                
                vo = converterInventario(imp, idProduto);
                
                if (inv == null) {
                    gravarInventario(vo);
                    inventarios.put(
                            inv,
                            String.valueOf(provider.getLojaVR()),
                            String.valueOf(idProduto),
                            String.valueOf(imp.getData())
                    );
                } else {
                    atualizarInventario(vo);
                }
                notificar();
            }
            this.provider.commit();
        } catch (Exception ex) {
            this.provider.rollback();
            throw ex;
        }
    }

    public void setNotificacao(String mensagem, int qtd) throws Exception {
        ProgressBar.setStatus(mensagem);
        ProgressBar.setMaximum(qtd);
    }

    public void notificar() throws Exception {
        ProgressBar.next();
    }
    
    public InventarioVO converterInventario(InventarioIMP imp, int idProduto) throws Exception {
        Icms aliqCredito;
        Icms aliqDebito;

        String idIcmsDebito = imp.getIdAliquotaDebito();
        String idIcmsCredito = imp.getIdAliquotaCredito();
        aliqCredito = provider.tributo().getAliquotaByMapaId(idIcmsCredito);
        aliqDebito = provider.tributo().getAliquotaByMapaId(idIcmsDebito);

        InventarioVO vo = new InventarioVO();
        vo.setIdProduto(idProduto);
        vo.setDescricao(imp.getDescricao());
        vo.setCustoComImposto(imp.getCustoComImposto());
        vo.setCustoSemImposto(imp.getCustoSemImposto());
        vo.setCustoMedioComImposto(imp.getCustoMedioComImposto());
        vo.setCustoMedioSemImposto(imp.getCustoMedioSemImposto());
        vo.setPrecoVenda(imp.getPrecoVenda());
        vo.setQuantidade(imp.getQuantidade());
        vo.setPis(imp.getPis());
        vo.setCofins(imp.getCofins());
        vo.setIdAliquotadebito(aliqDebito.getId());
        vo.setIdAliquotaCredito(aliqCredito.getId());        
        return vo;
    }
    
    public void gravarInventario(InventarioVO inventario) throws Exception {
        provider.salvar(inventario);
    }
    
    public void atualizarInventario(InventarioVO inventario) throws Exception {
        provider.atualizar(inventario);
    }
}
