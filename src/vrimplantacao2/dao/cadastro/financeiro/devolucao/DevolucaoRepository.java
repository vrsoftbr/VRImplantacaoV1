package vrimplantacao2.dao.cadastro.financeiro.devolucao;

import java.util.Map;
import java.util.List;
import static vr.core.utils.StringUtils.LOG;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.importacao.DevolucaoIMP;
import vrimplantacao2.vo.cadastro.fornecedor.FornecedorAnteriorVO;
import vrimplantacao2.dao.cadastro.financeiro.contasreceber.OpcaoContasReceber;
import vrimplantacao2.vo.cadastro.financeiro.contareceber.devolucao.DevolucaoVO;
import vrimplantacao2.vo.cadastro.financeiro.contareceber.devolucao.DevolucaoAnteriorVO;

/**
 *
 * @author Wesley
 */
public class DevolucaoRepository {
    
    private final DevolucaoProvider provider;
    
    public DevolucaoRepository(DevolucaoProvider provider) {
        this.provider = provider;
    }
    
    public void salvar(List<DevolucaoIMP> devolucoes, OpcaoContasReceber... opcoes) throws Exception {
        provider.setStatus("Importando devoluções...Carregando dados");
        provider.begin();
        try {

            Map<String, DevolucaoAnteriorVO> anteriores = provider.getAnteriores();
            MultiMap<String, FornecedorAnteriorVO> fornecedores = provider.getFornecedoresAnteriores();

            provider.setStatus("Importando devoluções.....Gravando", devolucoes.size());
            for (DevolucaoIMP imp : devolucoes) {

                DevolucaoAnteriorVO anterior = anteriores.get(imp.getId());

                FornecedorAnteriorVO fornecedor = fornecedores.get(
                        provider.getSistema(),
                        provider.getLoja(),
                        imp.getIdFornecedor()
                );

                if (anterior == null) {

                    anterior = this.converterDevolucaoAnterior(imp);

                    if (fornecedor != null && fornecedor.getCodigoAtual() != null) {

                        DevolucaoVO devolucao = this.converterDevolucao(imp);
                        devolucao.setId_fornecedor(fornecedor.getCodigoAtual().getId());

                        //Gravou Devolucao no banco VR
                        provider.gravarDevolucao(devolucao);
                        anterior.setCodigoAtual(devolucao);

                    } else {
                        LOG.warning("Fornecedor '" + imp.getIdFornecedor() + "' não encontrado!");
                    }

                    //Gravou Devolucao na codant e adicionou na lista de anteriores.
                    provider.gravarDevolucaoAnterior(anterior);
                    anteriores.put(imp.getId(), anterior);
                }

                provider.setStatus();
            }

            provider.commit();

        } catch (Exception e) {
            provider.rollback();
            throw e;
        }
    }
    

    public DevolucaoVO converterDevolucao(DevolucaoIMP imp) {
        DevolucaoVO vo = new DevolucaoVO();

        vo.setId_loja(provider.getLojaVR());
        vo.setDataEmissao(imp.getDataEmissao());
        vo.setDataVencimento(imp.getDataVencimento());
        vo.setNumeroNota(imp.getNumeroNota());
        vo.setValor(imp.getValor());
        vo.setObservacao("IMPORTADO VR - " + imp.getObservacao());

        return vo;
    }

    public DevolucaoAnteriorVO converterDevolucaoAnterior(DevolucaoIMP imp) {
        DevolucaoAnteriorVO ant = new DevolucaoAnteriorVO();
        ant.setSistema(provider.getSistema());
        ant.setLoja(provider.getLoja());
        ant.setId(imp.getId());
        ant.setNumeroNota(imp.getNumeroNota().toString());
        ant.setIdFornecedor(imp.getIdFornecedor());
        ant.setEmissao(imp.getDataEmissao());
        ant.setVencimento(imp.getDataVencimento());
        ant.setValor(imp.getValor());
        return ant;
    }
}
