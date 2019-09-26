package vrimplantacao2.dao.cadastro.fiscal.inventario;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joda.time.DateTime;
import vrframework.classe.ProgressBar;
import vrimplantacao.utils.Utils;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoVO;
import vrimplantacao2.utils.MathUtils;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.fiscal.inventario.InventarioVO;
import vrimplantacao2.vo.cadastro.fiscal.inventario.InventarioAnteriorVO;
import vrimplantacao2.vo.cadastro.fiscal.inventario.ProdutoInventario;
import vrimplantacao2.vo.importacao.InventarioIMP;

/**
 *
 * @author lucasrafael
 */
public class InventarioRepository {

    private static final Logger LOG = Logger.getLogger(InventarioRepository.class.getName());
    
    private InventarioRepositoryProvider provider;

    public InventarioRepository(InventarioRepositoryProvider provider) throws Exception {
        this.provider = provider;
    }

    public void importarInventario(List<InventarioIMP> inventario) throws Exception {
        
        LOG.fine("Iniciando a importação de inventário");
        setNotificacao("Inventário - Carregando dados...", 0);
        Map<String, ProdutoInventario> produtos = provider.getProdutosInventario();
        LOG.finer("Produtos carregados: " + produtos.size());
        Map<String, InventarioAnteriorVO> anteriores = provider.getAnteriores();
        LOG.finer("Inventários anteriores carregados: " + anteriores.size());
        MultiMap<Comparable, Integer> aliquotas = provider.getAliquotas();
        LOG.finer("Aliquotas por cst, aliquota e reduzido carregados: " + aliquotas.size());
        Map<String, MapaTributoVO> mapaAliquotas = provider.getMapaAliquotas();
        LOG.finer("Mapa de aliquotas carregado: " + mapaAliquotas.size());
        
        provider.begin();
        try {
            
            setNotificacao("Inventário - Gravando...", inventario.size());
            
            for (InventarioIMP imp: inventario) {
                InventarioAnteriorVO anterior = anteriores.get(imp.getId());
                
                if (anterior == null) {
                    ProdutoInventario prod = produtos.get(imp.getIdProduto());
                    
                    if (prod != null) {                        
                        InventarioVO vo = converterInventario(imp, prod);
                        
                        //Aliquota crédito
                        {
                            if (imp.getIdAliquotaCredito() != null) {
                                MapaTributoVO map = mapaAliquotas.get(imp.getIdAliquotaCredito());                                
                                if (map != null && map.getAliquota() != null) {
                                    vo.setIdAliquotaCredito(map.getAliquota().getId());
                                }
                            } else {
                                Integer aliq = aliquotas.get(
                                        Utils.stringToInt(imp.getCstCredito()),
                                        MathUtils.trunc(imp.getAliquotaCredito(), 1), 
                                        MathUtils.trunc(imp.getReduzidoCredito(), 1)
                                );
                                if (aliq != null) {
                                    vo.setIdAliquotaCredito(aliq);
                                }
                            }
                        }
                        
                        //Aliquota débito
                        {
                            if (imp.getIdAliquotaDebito()!= null) {
                                MapaTributoVO map = mapaAliquotas.get(imp.getIdAliquotaDebito());                                
                                if (map != null && map.getAliquota() != null) {
                                    vo.setIdAliquotaDebito(map.getAliquota().getId());
                                }
                            } else {
                                Integer aliq = aliquotas.get(
                                        Utils.stringToInt(imp.getCstDebito()),
                                        MathUtils.trunc(imp.getAliquotaDebito(), 1), 
                                        MathUtils.trunc(imp.getReduzidoDebito(), 1)
                                );
                                if (aliq != null) {
                                    vo.setIdAliquotaDebito(aliq);
                                }
                            }
                        }
                        
                        provider.salvar(vo);
                        
                        anterior = converterAnterior(imp);
                        anterior.setIdAtual(vo);
                        
                        provider.salvarAnterior(anterior);
                        
                        anteriores.put(anterior.getId(), anterior);
                        
                    } else {
                        LOG.warning("Produto '" + imp.getIdProduto() + "' - '" + imp.getDescricao() + "' não encontrado!");
                    }
                    
                } else {
                    LOG.warning("Inventário '" + imp.getId() + "' - '" + imp.getIdProduto() + "' - '" + imp.getDescricao() + "' já importado ");
                }
                
                notificar();
            }
            
            provider.commit();
            LOG.fine("Rotina de importação de inventário concluída");
        } catch (Exception ex) {
            provider.rollback();
            LOG.log(Level.SEVERE, "Erro ao importar os inventários", ex);
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

    public InventarioVO converterInventario(InventarioIMP imp, ProdutoInventario pi) throws Exception {
        InventarioVO vo = new InventarioVO();
        
        vo.setIdProduto(pi.getIdProduto());
        Date dt = imp.getData() != null ? imp.getData() : new Date();
        DateTime data = new DateTime(dt.getTime());
        vo.setData(data.dayOfMonth().withMaximumValue().toDate());
        vo.setDatageracao(dt);
        
        vo.setIdLoja(provider.getLojaVR());
        vo.setDescricao(
                (imp.getDescricao() == null || "".equals(imp.getDescricao().trim())) ?
                pi.getDescricao() :
                imp.getDescricao()
        );
        vo.setCustoComImposto(
                imp.getCustoComImposto() <= 0 ?
                pi.getCustoComImposto() :
                imp.getCustoComImposto()
        );
        vo.setCustoSemImposto(
                imp.getCustoSemImposto() <= 0 ?
                pi.getCustoComImposto() :
                imp.getCustoSemImposto()
        );
        vo.setCustoMedioComImposto(
                imp.getCustoMedioComImposto() <= 0 ?
                pi.getCustoMedioComImposto() :
                imp.getCustoMedioComImposto()
        );
        vo.setCustoMedioSemImposto(
                imp.getCustoMedioSemImposto() <= 0 ?
                pi.getCustoMedioSemImposto() :
                imp.getCustoMedioSemImposto()
        );
        vo.setPrecoVenda(
                imp.getPrecoVenda() <= 0 ?
                pi.getPrecoVenda() :
                imp.getPrecoVenda()
        );
        if (vo.getCustoSemImposto() == 0 && vo.getCustoComImposto() > 0) {
            vo.setCustoSemImposto(vo.getCustoComImposto());
        } else if (vo.getCustoComImposto() == 0 && vo.getCustoSemImposto() > 0) {
            vo.setCustoComImposto(vo.getCustoSemImposto());
        }
        vo.setQuantidade(imp.getQuantidade());
        vo.setPis(imp.getPis());
        vo.setCofins(imp.getCofins());
        vo.setIdAliquotaCredito(pi.getIdAliquotaCredito());
        vo.setIdAliquotaDebito(pi.getIdAliquotaDebito());
        
        return vo;
    }

    public InventarioAnteriorVO converterAnterior(InventarioIMP imp) throws Exception {
        InventarioAnteriorVO vo = new InventarioAnteriorVO();
        vo.setSistema(provider.getSistema());
        vo.setIdLoja(provider.getLojaOrigem());
        vo.setData(imp.getData());
        vo.setId(imp.getId());
        vo.setCodigoAnteior(imp.getIdProduto());
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
