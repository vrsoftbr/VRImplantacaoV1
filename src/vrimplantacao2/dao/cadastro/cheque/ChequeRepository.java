package vrimplantacao2.dao.cadastro.cheque;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import vrimplantacao.utils.Utils;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.cliente.cheque.ChequeAnteriorVO;
import vrimplantacao2.vo.cadastro.cliente.cheque.ChequeVO;
import vrimplantacao2.vo.enums.TipoAlinea;
import vrimplantacao2.vo.enums.TipoInscricao;
import vrimplantacao2.vo.importacao.ChequeIMP;

/**
 *
 * @author Leandro
 */
public class ChequeRepository {
    private final ChequeRepositoryProvider provider;

    public ChequeRepository(ChequeRepositoryProvider provider) {
        this.provider = provider;        
    }

    public void salvar(List<ChequeIMP> cheques) throws Exception {        
        Map<String, ChequeIMP> filtrados = filtrar(cheques);
        cheques.clear();
        System.gc();
        
        provider.begin();
        try {
            MultiMap<String, ChequeAnteriorVO> anteriores = provider.getAnteriores();
            Set<Integer> bancosExistentes = provider.getBancosExistentes();
            provider.setStatus("Gravando cheques...");
            provider.setMaximo(filtrados.size());
            
            for (ChequeIMP imp: filtrados.values()) {
                ChequeAnteriorVO anterior = anteriores.get(
                        provider.getSistema(),
                        provider.getLojaOrigem(),
                        imp.getId()
                );
                
                if (anterior == null) {                        
                    ChequeVO ch = converterCheque(imp);
                    if (!bancosExistentes.contains(ch.getId_banco())) {
                        ch.setId_banco(804);
                    }
                    anterior = converterChequeAnteriorVO(imp);
                    anterior.setCodigoatual(ch);
                    
                    gravarCheque(ch);
                    gravarChequeAnterior(anterior);
                    anteriores.put(
                            anterior,
                            provider.getSistema(),
                            provider.getLojaOrigem(),
                            imp.getId()
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

    public Map<String, ChequeIMP> filtrar(List<ChequeIMP> cheques) {
        Map<String, ChequeIMP> result = new LinkedHashMap<>();
        for (ChequeIMP imp: cheques) {
            result.put(imp.getId(), imp);
        }
        return result;
    }

    public ChequeVO converterCheque(ChequeIMP imp) {
        ChequeVO vo = new ChequeVO();
        vo.setId_loja(provider.getLojaVR());
        vo.setCpf(Utils.stringToLong(imp.getCpf()));
        vo.setNumeroCheque(Utils.stringToInt(imp.getNumeroCheque()));
        vo.setId_banco(imp.getBanco());
        vo.setAgencia(imp.getAgencia());
        vo.setConta(imp.getConta());
        vo.setData(imp.getDate());
        vo.setId_plano(0);
        vo.setNumeroCupom(Utils.stringToInt(imp.getNumeroCupom()));
        vo.setEcf(Utils.stringToInt(imp.getEcf()));
        vo.setValor(imp.getValor());
        vo.setDataDeposito((imp.getDataDeposito() == null ? imp.getDate() : imp.getDataDeposito()));
        vo.setLancamentoManual(false);
        vo.setRg(imp.getRg());
        vo.setTelefone(imp.getTelefone());
        vo.setNome(imp.getNome());
        vo.setObservacao("IMPORTADO VR " + Utils.acertarObservacao(imp.getObservacao()));
        vo.setSituacaoCheque(imp.getSituacaoCheque());
        vo.setTipoLocalCobranca(imp.getIdLocalCobranca());
        vo.setCmc7(imp.getCmc7());
        vo.setDataDevolucao(null);
        vo.setTipoAlinea(TipoAlinea.getAlinea(imp.getAlinea()));
        vo.setTipoInscricao(TipoInscricao.analisarCnpjCpf(vo.getCpf()));
        vo.setDataEnvioCobranca(null);
        vo.setValorPagarFornecedor(0);
        vo.setId_boleto(0);
        vo.setOperadorClienteBloqueado(null);
        vo.setOperadorExcedeLimite(null);
        vo.setOperadorProblemaCheque(null);
        vo.setOperadorChequeBloqueado(null);
        vo.setValorJuros(imp.getValorJuros());
        vo.setTipoVistaPrazo(imp.getVistaPrazo());
        vo.setJustificativa("");
        vo.setValorAcrescimo(imp.getValorAcrescimo());
        vo.setValorInicial(imp.getValor());
        vo.setDataHoraAlteracao(imp.getDataHoraAlteracao());
        vo.setOperadorClienteNaoCadastrado(null);
        vo.setDataDevolucao(imp.getDataDevolucao());
        return vo;
    }

    public ChequeAnteriorVO converterChequeAnteriorVO(ChequeIMP imp) {
        ChequeAnteriorVO vo = new ChequeAnteriorVO();
        vo.setSistema(provider.getSistema());
        vo.setLoja(provider.getLojaOrigem());
        vo.setId(imp.getId());
        vo.setData(imp.getDate());
        vo.setBanco(imp.getBanco());
        vo.setAgencia(imp.getAgencia());
        vo.setConta(imp.getConta());
        vo.setCheque(imp.getNumeroCheque());
        vo.setValor(imp.getValor());
        return vo;
    }

    public void gravarCheque(ChequeVO ch) throws Exception {
        provider.gravarCheque(ch);
    }

    public void gravarChequeAnterior(ChequeAnteriorVO anterior) throws Exception {
        provider.gravarChequeAnterior(anterior);
    }
    
}
