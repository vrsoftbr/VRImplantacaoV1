package vrimplantacao2.dao.cadastro.convenio.conveniado;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import vrimplantacao.utils.Utils;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.convenio.conveniado.ConveniadoAnteriorVO;
import vrimplantacao2.vo.cadastro.convenio.conveniado.ConveniadoServicoVO;
import vrimplantacao2.vo.cadastro.convenio.conveniado.ConveniadoVO;
import vrimplantacao2.vo.cadastro.convenio.conveniado.TipoServicoConvenio;
import vrimplantacao2.vo.cadastro.convenio.empresa.ConvenioEmpresaAnteriorVO;
import vrimplantacao2.vo.importacao.ConveniadoIMP;

/**
 *
 * @author Leandro
 */
public class ConvenioConveniadoRepository {
    
    private static final Logger LOG = Logger.getLogger(ConvenioConveniadoRepository.class.getName());
    
    private final ConvenioConveniadoRepositoryProvider provider;

    public ConvenioConveniadoRepository(ConvenioConveniadoRepositoryProvider provider) throws Exception {        
        this.provider = provider;
    }

    public void salvar(List<ConveniadoIMP> conveniados) throws Exception {
        provider.setStatus("Gravando conveniados (Convênio)...");
        provider.begin();
        try {
            MultiMap<Long, Integer> cnpjCadastrados = provider.getCnpjCadastrado();
            ConveniadoIDStack ids = provider.getIds();
            Map<String, ConveniadoAnteriorVO> anteriores = provider.getAnteriores();
            Map<String, ConvenioEmpresaAnteriorVO> empresas = provider.getEmpresas(); 
            
            Map<String, ConveniadoIMP> filtrados = filtrar(conveniados, ids.obterIdsExistentes());
            System.gc();
            
            provider.setMaximum(filtrados.size());
            for (ConveniadoIMP imp: filtrados.values()) {
                ConveniadoAnteriorVO anterior = anteriores.get(imp.getId());
                ConvenioEmpresaAnteriorVO empresa = empresas.get(imp.getIdEmpresa());
                
                if ( empresa == null || empresa.getCodigoAtual() < 1 ) {
                    LOG.warning("Código de empresa não existe '" + imp.getIdEmpresa() + "'");
                    provider.next();
                    continue;
                }
                
                if (anterior == null) {
                    
                    int id = ids.obterID(imp.getId());
                    long cnpj = Utils.stringToLong(imp.getCnpj());
                    if (cnpj > 99999999999999L || cnpj < 0L) {
                        cnpj = id;
                    }
                    long id_empresa = (long) empresa.getCodigoAtual();
                    
                    Integer idByCnpj = cnpjCadastrados.get(id_empresa, cnpj);
                    
                    anterior = converterConveniadoAnterior(imp);
                    
                    if (idByCnpj != null) {
                        anterior.setCodigoAtual(id);
                    } else {                    
                        ConveniadoVO vo = converterConveniado(imp);
                        vo.setId(id);
                        vo.setCnpj(cnpj);
                        vo.setId_empresa((int) id_empresa);
                        ConveniadoServicoVO servico = converterServicoConvenio(imp);
                        servico.setId_conveniado(vo.getId());                    

                        gravarConveniado(vo);
                        gravarConveniadoServico(servico);
                        
                        anterior.setCodigoAtual(vo.getId());

                        cnpjCadastrados.put(vo.getId(), (long) vo.getId_empresa(), (long) vo.getCnpj());
                    }
                    gravarConveniadoAnterior(anterior);
                    anteriores.put(
                            imp.getId(),
                            anterior
                    );
                }
                
                provider.next();
            }
            
            provider.commit();
        } catch (Exception e) {
            provider.rollback();
            throw e;
        }
    }

    public Map<String, ConveniadoIMP> filtrar(List<ConveniadoIMP> conveniados, Set<Integer> existentes) throws Exception {
        Map<String, ConveniadoIMP> result = new LinkedHashMap<>();
        MultiMap<String, ConveniadoIMP> validos = new MultiMap<>(1);
        MultiMap<String, ConveniadoIMP> invalidos = new MultiMap<>(1);
        
        for (ConveniadoIMP imp: conveniados) {
            try {
                int id = Integer.parseInt(imp.getId());
                if (existentes.contains(id)) {
                    invalidos.put(imp, imp.getId());
                } else {
                    validos.put(imp, imp.getId());
                }                
            } catch (NumberFormatException e) {
                invalidos.put(imp, imp.getId());
            }
        }
        
        validos = validos.getSortedMap();
        invalidos = invalidos.getSortedMap();
        
        for (ConveniadoIMP imp: validos.values()) {
            result.put(imp.getId(), imp);
        }
        for (ConveniadoIMP imp: invalidos.values()) {
            result.put(imp.getId(), imp);
        }
        
        return result;
    }

    public ConveniadoVO converterConveniado(ConveniadoIMP imp) throws Exception {
        ConveniadoVO vo = new ConveniadoVO();
        vo.setNome(imp.getNome());
        vo.setBloqueado(imp.isBloqueado());
        vo.setSituacaoCadastro(imp.getSituacaoCadastro());
        vo.setId_loja(imp.getLojaCadastro() > 0 ? imp.getLojaCadastro() : provider.getLojaVR());
        vo.setObservacao("IMPORTADO VR " + Utils.acertarTexto(imp.getObservacao()));
        vo.setDataValidadeCartao(imp.getValidadeCartao());
        vo.setDataDesbloqueio(imp.getDataDesbloqueio());
        vo.setVisualizaSaldo(true);
        vo.setDataBloqueio(imp.getDataBloqueio());
        return vo;
    }

    public ConveniadoAnteriorVO converterConveniadoAnterior(ConveniadoIMP imp) throws Exception {
        ConveniadoAnteriorVO vo = new ConveniadoAnteriorVO();
        vo.setSistema(provider.getSistema());
        vo.setLoja(provider.getLojaOrigem());
        vo.setId(imp.getId());
        vo.setCnpj(imp.getCnpj());
        vo.setRazao(imp.getNome());
        vo.setLojaCadastro(String.valueOf(imp.getLojaCadastro()));
        return vo;
    }

    public void gravarConveniado(ConveniadoVO vo) throws Exception {
        provider.gravarConveniado(vo);
    }

    public void gravarConveniadoAnterior(ConveniadoAnteriorVO ant) throws Exception {
        provider.gravarConveniadoAnterior(ant);
    }

    public ConveniadoServicoVO converterServicoConvenio(ConveniadoIMP imp) {
        ConveniadoServicoVO vo = new ConveniadoServicoVO();
        
        vo.setTipoServicoConvenio(TipoServicoConvenio.CONVENIO);
        vo.setValor(imp.getConvenioLimite());
        vo.setValorDesconto(imp.getConvenioDesconto());
        
        return vo;
    }

    public void gravarConveniadoServico(ConveniadoServicoVO servico) throws Exception {
        provider.gravarConveniadoServico(servico);
    }
    
}
