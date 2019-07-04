package vrimplantacao2.dao.cadastro.convenio.receber;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import vrimplantacao.utils.Utils;
import vrimplantacao2.vo.cadastro.convenio.conveniado.ConveniadoAnteriorVO;
import vrimplantacao2.vo.cadastro.convenio.conveniado.TipoServicoConvenio;
import vrimplantacao2.vo.cadastro.convenio.transacao.ConvenioTransacaoAnteriorVO;
import vrimplantacao2.vo.cadastro.convenio.transacao.ConvenioTransacaoVO;
import vrimplantacao2.vo.importacao.ConvenioTransacaoIMP;

/**
 *
 * @author Leandro
 */
public class ConvenioReceberRepository {
    private final ConvenioReceberRepositoryProvider provider;

    public ConvenioReceberRepository(ConvenioReceberRepositoryProvider provider) throws Exception {
        this.provider = provider;
    }

    public void salvar(List<ConvenioTransacaoIMP> recebimentos) throws Exception {
        provider.setStatus("Gravando as transações(Convênio)...");
        try {
            provider.begin();
            
            Map<String, ConvenioTransacaoIMP> filtrados = filtrar(recebimentos);
            recebimentos.clear();
            
            System.gc();
                
            Map<String, ConvenioTransacaoAnteriorVO> anteriores = provider.getAnteriores();
            Map<String, ConveniadoAnteriorVO> conveniados = provider.getConveniados();            
            
            provider.setMaximum(filtrados.size());
            for (ConvenioTransacaoIMP imp: filtrados.values()) {
                
                ConvenioTransacaoAnteriorVO anterior = anteriores.get(imp.getId());
                ConveniadoAnteriorVO conveniado = conveniados.get(imp.getIdConveniado());
                
                if (anterior == null && conveniado != null && conveniado.getCodigoAtual() < 1) {
                    
                    ConvenioTransacaoVO vo = converterTransacao(imp);
                    vo.setId_conveniado(conveniado.getCodigoAtual());
                    anterior = converterTransacaoAnterior(imp);
                    anterior.setCodigoAtual(vo);
                    
                    gravarTransacao(vo);
                    gravarTransacaoAnterior(anterior);
                    
                } else {
                    System.out.println(String.format("ID: %s VALOR: %s (anterior == null: %s) (conveniado != null: %s)", 
                            imp.getId(),
                            String.valueOf(imp.getValor()),
                            String.valueOf(anterior == null),
                            String.valueOf(conveniado != null)
                    ));
                }
                        
                provider.next();
            }
            
            provider.commit();
        } catch (Exception e) {
            provider.rollback();
            throw e;
        }
    }

    public Map<String, ConvenioTransacaoIMP> filtrar(List<ConvenioTransacaoIMP> recebimentos) {
        Map<String, ConvenioTransacaoIMP> result = new LinkedHashMap<>();
        for (ConvenioTransacaoIMP imp: recebimentos) {
            result.put(imp.getId(), imp);
        }
        return result;
    }

    public ConvenioTransacaoVO converterTransacao(ConvenioTransacaoIMP imp) throws Exception {
        ConvenioTransacaoVO vo = new ConvenioTransacaoVO();
        
        vo.setEcf(Utils.stringToInt(imp.getEcf()));
        vo.setNumeroCupom(Utils.stringToInt(imp.getNumeroCupom()));
        vo.setDataHora(imp.getDataHora());
        vo.setId_loja(provider.getLojaVR());
        vo.setValor(imp.getValor());
        vo.setSituacaoTransacaoConveniado(imp.getSituacaoTransacaoConveniado());
        vo.setLancamentoManual(false);
        vo.setMatricula(500001);
        vo.setDataMovimento(imp.getDataMovimento() != null ? imp.getDataMovimento() : imp.getDataHora());
        vo.setFinalizado(imp.isFinalizado());
        vo.setTipoServicoConvenio(TipoServicoConvenio.CONVENIO);
        vo.setObservacao("IMPORTADO VR" + Utils.acertarTexto(imp.getObservacao()));
        
        return vo;
    }

    public ConvenioTransacaoAnteriorVO converterTransacaoAnterior(ConvenioTransacaoIMP imp) {
        ConvenioTransacaoAnteriorVO vo = new ConvenioTransacaoAnteriorVO();
        vo.setSistema(provider.getSistema());
        vo.setLoja(provider.getLojaOrigem());
        vo.setId(imp.getId());
        vo.setData(imp.getDataHora());
        vo.setPago(imp.isFinalizado());
        return vo;
    }

    public void gravarTransacao(ConvenioTransacaoVO vo) throws Exception {
        provider.gravarTransacao(vo);
    }

    public void gravarTransacaoAnterior(ConvenioTransacaoAnteriorVO anterior) throws Exception {
        provider.gravarTransacaoAnterior(anterior);
    }
    
}
