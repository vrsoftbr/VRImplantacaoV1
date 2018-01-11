package vrimplantacao2.dao.cadastro.fiscal.pautafiscal;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import vrimplantacao2.vo.cadastro.fiscal.pautafiscal.PautaFiscalAnteriorVO;
import vrimplantacao2.vo.cadastro.fiscal.pautafiscal.PautaFiscalVO;
import vrimplantacao2.vo.cadastro.local.EstadoVO;
import vrimplantacao2.vo.enums.NcmVO;
import vrimplantacao2.vo.enums.OpcaoFiscal;
import vrimplantacao2.vo.importacao.PautaFiscalIMP;

/**
 * Repositório para tratar a importação das pautas fiscais.
 * @author Leandro
 */
public class PautaFiscalRepository {
    private final PautaFiscalRepositoryProvider provider;
    
    private Map<String, EstadoVO> ufs;
    private EstadoVO padrao;

    public PautaFiscalRepository(PautaFiscalRepositoryProvider provider) throws Exception {
        this.provider = provider;
        this.ufs = provider.getEstados();
        this.padrao = provider.getUfPadrao();
    }

    public void importar(List<PautaFiscalIMP> pautas, Set<OpcaoFiscal> opt) throws Exception {
        try {
            provider.begin();
            
            provider.notificar("Pauta Fiscal...Carregando informações...");
            
            Map<String, PautaFiscalIMP> organizados = organizar(pautas);
            pautas.clear();
            System.gc();
            
            Map<String, PautaFiscalAnteriorVO> anteriores = provider.getAnteriores();
            
            provider.notificar("Pauta Fiscal...Gravando...", organizados.size());
            for (PautaFiscalIMP imp: organizados.values()) {
                PautaFiscalAnteriorVO anterior = anteriores.get(imp.getId());
                
                NcmVO ncm = provider.getNcm(imp.getNcm());
                
                /**
                 * Se for um NCM válido executa.
                 */
                if (ncm != null) {
                    /**
                     * Se a pauta não estava cadastrada no sistema.
                     */
                    if (anterior == null) {                       
                        anterior = converterPautaAnterior(imp);  

                        if (opt.contains(OpcaoFiscal.NOVOS)) { 
                            PautaFiscalVO vo = converterPauta(imp, ncm);
                            provider.gravar(vo, opt);                      
                            anterior.setCodigoAtual(vo);
                        }

                        provider.gravarAnterior(anterior);
                        //Inclui na listagem de anteriores.
                        anteriores.put(anterior.getId(), anterior);
                    } else {
                        /**
                         * Se a tabela código anterior possuir o registro, mas por 
                         * alguma razão ele não foi incluido na tabela pautafiscal
                         * executa esta opção para incluir os novo
                         */                    
                        if (anterior.getCodigoAtual() == null) {
                            if (opt.contains(OpcaoFiscal.NOVOS)) {
                                PautaFiscalVO vo = converterPauta(imp, ncm);
                                provider.gravar(vo, opt);
                                anterior.setCodigoAtual(vo);
                            }
                            provider.atualizar(anterior);
                        } 
                        /**
                         * Senão atualiza as informações.
                         */
                        else {
                            PautaFiscalVO vo = converterPauta(imp, ncm);
                            provider.atualizar(vo, opt);
                            provider.atualizar(anterior);
                        }
                    }
                } else {
                    anterior = converterPautaAnterior(imp);
                    provider.gravarAnterior(anterior);
                    anteriores.put(anterior.getId(), anterior);
                }
                
                provider.notificar();
            }
            
            
            provider.commit();
        } catch (Exception e) {
            provider.rollback();
            throw e;
        } finally {
            if (this.ufs != null) {
                this.ufs.clear();
                this.ufs = null;
            }
            if (this.padrao != null) {
                this.padrao = null;
            }
        }
    }

    public Map<String, PautaFiscalIMP> organizar(List<PautaFiscalIMP> pautas) {
        Map<String, PautaFiscalIMP> result = new LinkedHashMap<>();
        for (PautaFiscalIMP imp: pautas) {
            result.put(imp.getId(), imp);
        }
        return result;
    }

    public PautaFiscalVO converterPauta(PautaFiscalIMP imp, NcmVO ncm) throws Exception {
        PautaFiscalVO vo = new PautaFiscalVO();
        
        String idIcmsCredito = imp.getAliquotaCreditoId();
        String idIcmsDebito = imp.getAliquotaDebitoId();
        String idIcmsDebitoForaEstado = imp.getAliquotaDebitoForaEstadoId();
        
        if (idIcmsDebito != null || idIcmsCredito != null) {
            if (idIcmsDebito != null && idIcmsCredito == null) {
                idIcmsCredito = idIcmsDebito;
            } else if (idIcmsDebito == null && idIcmsCredito != null) {
                idIcmsDebito = idIcmsCredito;
            }
            if (idIcmsDebitoForaEstado == null) {
                idIcmsDebitoForaEstado = idIcmsDebito;
            }  
            
            vo.setId_aliquotaCredito(provider.getAliquotaByMapaId(idIcmsCredito).getId());
            vo.setId_aliquotaDebito(provider.getAliquotaByMapaId(idIcmsDebito).getId());
            vo.setId_aliquotaDebitoForaEstado(provider.getAliquotaByMapaId(idIcmsDebitoForaEstado).getId());
        } else {
            vo.setId_aliquotaCredito(provider.getAliquota(
                    imp.getAliquotaCredito().getCst(),
                    imp.getAliquotaCredito().getAliquota(),
                    imp.getAliquotaCredito().getReduzido()
            ));
            vo.setId_aliquotaDebito(provider.getAliquota(
                    imp.getAliquotaDebito().getCst(),
                    imp.getAliquotaDebito().getAliquota(),
                    imp.getAliquotaDebito().getReduzido()
            ));
            vo.setId_aliquotaDebitoForaEstado(provider.getAliquota(
                    imp.getAliquotaDebitoForaEstado().getCst(),
                    imp.getAliquotaDebitoForaEstado().getAliquota(),
                    imp.getAliquotaDebitoForaEstado().getReduzido()
            ));
        }
        
        EstadoVO uf = this.ufs.get(imp.getUf());
        if (uf == null) {
            uf = this.padrao;
        }
        vo.setId_estado(uf.getId());
        vo.setIva(imp.getIva());
        vo.setIvaAjustado(imp.getIvaAjustado());
        vo.setNcm1(ncm.getNcm1());
        vo.setNcm2(ncm.getNcm2());
        vo.setNcm3(ncm.getNcm3());
        vo.setTipoIva(imp.getTipoIva());
        
        return vo;        
    }

    public PautaFiscalAnteriorVO converterPautaAnterior(PautaFiscalIMP imp) {
        PautaFiscalAnteriorVO ant = new PautaFiscalAnteriorVO();
        
        ant.setSistema(provider.getSistema());
        ant.setLoja(provider.getLoja());
        ant.setId(imp.getId());
        
        return ant;
    }
    
}
