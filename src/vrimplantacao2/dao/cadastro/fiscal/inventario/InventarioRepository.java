package vrimplantacao2.dao.cadastro.fiscal.inventario;

import java.util.List;
import java.util.Map;
import vrframework.classe.ProgressBar;
import vrimplantacao2.dao.cadastro.produto.ProdutoAnteriorDAO;
import vrimplantacao2.vo.cadastro.fiscal.inventario.InventarioVO;
import vrimplantacao2.vo.cadastro.fiscal.inventario.InventarioAnteriorVO;
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
            setNotificacao("Preparando para gravar inventario...", inventario.size());
            Map<String, InventarioAnteriorVO> anteriores = provider.getAnteriores();
            //</editor-fold>

            setNotificacao("Gravando inventario...", inventario.size());

            for (InventarioIMP imp : inventario) {
                int idProduto = produtoAnteriorDAO.getCodigoAnterior2(provider.getSistema(), provider.getLojaOrigem(), imp.getIdProduto());

                if (idProduto > 0) {

                    InventarioAnteriorVO anterior = anteriores.get(imp.getId());
                    if (anterior == null) {
                        anterior = converterAnterior(imp, String.valueOf(idProduto));
                        InventarioVO vo = converterInventario(imp, idProduto);
                        provider.salvar(vo);
                        anterior.setIdAtual(vo);

                        provider.salvarAnterior(anterior);
                        //Inclui na listagem de anteriores.
                        anteriores.put(anterior.getId(), anterior);
                    } else {
                        anterior = converterAnterior(imp, String.valueOf(idProduto));
                        InventarioVO vo = converterInventario(imp, idProduto);
                        provider.atualizar(vo);
                        anterior.setIdAtual(vo);
                        provider.salvarAnterior(anterior);
                        anteriores.put(anterior.getId(), anterior);
                    }
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
        
        vo.setData(imp.getData());
        vo.setIdProduto(idProduto);
        vo.setIdLoja(provider.getLojaVR());
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

    public InventarioAnteriorVO converterAnterior(InventarioIMP imp, String idProduto) throws Exception {
        InventarioAnteriorVO vo = new InventarioAnteriorVO();
        vo.setSistema(provider.getSistema());
        vo.setIdLoja(provider.getLojaOrigem());
        vo.setData(imp.getData());
        vo.setId(imp.getId());
        vo.setCodigoAnteior(imp.getIdProduto());
        vo.setCodigoAtual(idProduto);
        vo.setDescricao(imp.getDescricao());
        vo.setPrecoVenda(imp.getPrecoVenda());
        vo.setQuantidade(imp.getQuantidade());
        vo.setCustoComImposto(imp.getCustoComImposto());
        vo.setCustoSemImposto(imp.getCustoSemImposto());
        vo.setCustoMedioComImposto(imp.getCustoMedioComImposto());
        vo.setCustoMedioSemImposto(imp.getCustoMedioSemImposto());
        vo.setPis(imp.getPis());
        vo.setCofins(imp.getCofins());
        vo.setIdAliquotaCredito(imp.getIdAliquotaCredito());
        vo.setIdAliquotadebito(imp.getIdAliquotaDebito());
        return vo;
    }

    public void gravarInventario(InventarioVO inventario) throws Exception {
        provider.salvar(inventario);
    }

    public void atualizarInventario(InventarioVO inventario) throws Exception {
        provider.atualizar(inventario);
    }
}
