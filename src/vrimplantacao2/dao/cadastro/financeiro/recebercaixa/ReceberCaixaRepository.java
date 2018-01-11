package vrimplantacao2.dao.cadastro.financeiro.recebercaixa;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import vrimplantacao2.vo.cadastro.financeiro.recebimentocaixa.RecebimentoCaixaAnteriorVO;
import vrimplantacao2.vo.cadastro.financeiro.recebimentocaixa.RecebimentoCaixaVO;
import vrimplantacao2.vo.enums.SituacaoReceberCaixa;
import vrimplantacao2.vo.importacao.RecebimentoCaixaIMP;

/**
 * Gerencia a importação do recebimento de caixa.
 * @author Leandro
 */
public class ReceberCaixaRepository {
    private final ReceberCaixaRepositoryProvider provider;

    public ReceberCaixaRepository(ReceberCaixaRepositoryProvider provider) {        
        this.provider = provider;
    }

    public void salvar(List<RecebimentoCaixaIMP> recebimentos, OpcaoRecebimentoCaixa... opcoes) throws Exception {
        Set<OpcaoRecebimentoCaixa> opt = new HashSet<>(Arrays.asList(opcoes));
        try {
            provider.begin();
            
            provider.notificar("Organizando e carregando informações para importação...");
            //Converter a lista de informações passadas importação.
            Map<String, RecebimentoCaixaIMP> organizados = organizar(recebimentos);
            recebimentos.clear();
            System.gc();

            //Carrega as listagens necessárias para a importação.
            Map<String, RecebimentoCaixaAnteriorVO> anteriores = provider.getAnteriores();
            Map<String, Integer> mapaTipoRecebivel = provider.getMapaTipoRecebivel();

            provider.notificar("Importando recebimentos...", organizados.size());
            for (RecebimentoCaixaIMP imp: organizados.values()) {
                
                RecebimentoCaixaAnteriorVO anterior = anteriores.get(imp.getId());
                Integer idTipoRecebivel = mapaTipoRecebivel.get(imp.getIdTipoRecebivel());
                
                /**
                 * Se for um recebimento novo e se o TipoRecebivel estiver mapeado.
                 */                
                if (anterior == null) {
                    if (idTipoRecebivel != null) {
                        if (opt.contains(OpcaoRecebimentoCaixa.NOVOS)) {
                            RecebimentoCaixaVO vo = gravarNovoRecebimento(imp, idTipoRecebivel);
                            gravarAnterior(imp, vo, anteriores);
                        }
                    }
                } else {
                    if (anterior.getCodigoAtual() != null) {
                        RecebimentoCaixaVO vo = atualizarRecebimentoCaixa(anterior.getCodigoAtual(), imp, idTipoRecebivel, opt);
                        atualizarAnterior(imp, vo, anteriores);
                    } else {
                        if (idTipoRecebivel != null) {
                            if (opt.contains(OpcaoRecebimentoCaixa.NOVOS)) {
                                RecebimentoCaixaVO vo = gravarNovoRecebimento(imp, idTipoRecebivel);
                                atualizarAnterior(imp, vo, anteriores);
                            }
                        }
                    }
                }
                
                provider.notificar();
            }
            
            provider.commit();
        } catch (Exception e) {
            provider.rollback();
            throw e;
        }
    }

    /**
     * Grava um novo código anterior.
     * @param imp
     * @param vo
     * @param anteriores
     * @throws Exception 
     */
    public void gravarAnterior(RecebimentoCaixaIMP imp, RecebimentoCaixaVO vo, Map<String, RecebimentoCaixaAnteriorVO> anteriores) throws Exception {
        RecebimentoCaixaAnteriorVO anterior;
        //Converte e grava o código anterior.
        anterior = converterRecebimentoAnterior(imp);
        anterior.setCodigoAtual(vo);
        provider.gravarRecebimentoCaixaAnterior(anterior);
        anteriores.put(imp.getId(), anterior);
    }
    
    public void atualizarAnterior(RecebimentoCaixaIMP imp, RecebimentoCaixaVO vo, Map<String, RecebimentoCaixaAnteriorVO> anteriores) throws Exception {
        RecebimentoCaixaAnteriorVO anterior;
        //grava o código anterior.
        anterior = converterRecebimentoAnterior(imp);
        anterior.setCodigoAtual(vo);
        provider.atualizaRecebimentoCaixaAnterior(anterior);
        anteriores.put(imp.getId(), anterior);
    }
    
    public RecebimentoCaixaVO gravarNovoRecebimento(RecebimentoCaixaIMP imp, Integer idTipoRecebivel) throws Exception {
        // Converte e grava o RecebimentoCaixa.
        RecebimentoCaixaVO vo = converterRecebimento(imp);
        vo.setIdTipoRecebivel(idTipoRecebivel);
        provider.gravarRecebimentoCaixa(vo);
        return vo;
    }

    public RecebimentoCaixaVO atualizarRecebimentoCaixa(RecebimentoCaixaVO codigoAtual, RecebimentoCaixaIMP imp, Integer idTipoRecebivel, Set<OpcaoRecebimentoCaixa> opt) throws Exception {
        RecebimentoCaixaVO vo = converterRecebimento(imp);
        vo.setId(codigoAtual.getId());
        vo.setIdTipoRecebivel(idTipoRecebivel);
        provider.atualizarRecebimentoCaixa(vo, opt);
        return vo;
    }

    public Map<String, RecebimentoCaixaIMP> organizar(List<RecebimentoCaixaIMP> recebimentos) {
        Map<String, RecebimentoCaixaIMP> result = new LinkedHashMap<>();
        for (RecebimentoCaixaIMP imp: recebimentos) {
            result.put(imp.getId(), imp);
        }
        return result;
    }

    public RecebimentoCaixaVO converterRecebimento(RecebimentoCaixaIMP imp) {
        RecebimentoCaixaVO vo = new RecebimentoCaixaVO();
        vo.setDataEmissao(imp.getDataEmissao());
        vo.setDataVencimento(imp.getDataVencimento());
        vo.setIdLoja(provider.getLojaVR());
        vo.setIdTipoLocalCobranca(0);
        vo.setIdTipoRecebimento(0);
        vo.setObservacao("IMPORTADO VR " + imp.getObservacao());
        vo.setSituacaoReceberCaixa(SituacaoReceberCaixa.ABERTO);
        vo.setValor(imp.getValor());
        return vo;
    }

    public RecebimentoCaixaAnteriorVO converterRecebimentoAnterior(RecebimentoCaixaIMP imp) {
        RecebimentoCaixaAnteriorVO ant = new RecebimentoCaixaAnteriorVO();
        ant.setSistema(provider.getSistema());
        ant.setAgrupador(provider.getAgrupador());
        ant.setId(imp.getId());
        ant.setDataEmissao(imp.getDataEmissao());
        ant.setVencimento(imp.getDataVencimento());
        ant.setIdTipoRecebivel(imp.getIdTipoRecebivel());
        ant.setValor(imp.getValor());
        return ant;
    }
    
}
