package vrimplantacao2.dao.cadastro.financeiro.verbas;

import java.util.Map;
import java.util.List;
import java.text.SimpleDateFormat;
import static vr.core.utils.StringUtils.LOG;
import vrimplantacao2.vo.importacao.VerbaIMP;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.fornecedor.FornecedorAnteriorVO;
import vrimplantacao2.vo.cadastro.financeiro.contareceber.verbas.VerbasVO;
import vrimplantacao2.dao.cadastro.financeiro.contasreceber.OpcaoContasReceber;
import vrimplantacao2.vo.cadastro.financeiro.contareceber.verbas.VerbasAnteriorVO;

/**
 *
 * @author Wesley
 */
public class VerbasRepository {

    private static final SimpleDateFormat FORMATER = new SimpleDateFormat("yyyy-MM-dd");

    private final VerbasProvider provider;

    public VerbasRepository(VerbasProvider provider) {
        this.provider = provider;
    }

    public void salvar(List<VerbaIMP> verbas, OpcaoContasReceber... opcoes) throws Exception {
        provider.setStatus("Importando verbas...Carregando dados");
        provider.begin();
        try {

            Map<String, VerbasAnteriorVO> anteriores = provider.getAnteriores();
            MultiMap<String, FornecedorAnteriorVO> fornecedores = provider.getFornecedoresAnteriores();

            provider.setStatus("Importando verbas.....Gravando", verbas.size());
            for (VerbaIMP imp : verbas) {

                VerbasAnteriorVO anterior = anteriores.get(imp.getId());

                FornecedorAnteriorVO fornecedor = fornecedores.get(
                        provider.getSistema(),
                        provider.getLoja(),
                        imp.getIdFornecedor()
                );

                if (anterior == null) {

                    anterior = this.converterVerbaAnterior(imp);

                    if (fornecedor != null && fornecedor.getCodigoAtual() != null) {

                        VerbasVO verba = this.converterVerba(imp);
                        verba.setId_fornecedor(fornecedor.getCodigoAtual().getId());

                        //Gravou VERBAS no banco VR
                        provider.gravarVerbas(verba);
                        anterior.setCodigoAtual(verba);
                        
                        //Gravar VENCIMENTO de verbas no VR
                        provider.gravarVencimento(verba);
                        anterior.setCodigoAtualVencimento(verba.getCodigoAtualVencimento());


                    } else {
                        LOG.warning("Fornecedor '" + imp.getIdFornecedor() + "' não encontrado!");
                    }

                    //Gravou VERBAS na codant e adicionou na lista de anteriores.
                    provider.gravarVerbasAnterior(anterior);
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

    public VerbasVO converterVerba(VerbaIMP imp) {
        VerbasVO vo = new VerbasVO();

        vo.setId_loja(provider.getLojaVR());
        vo.setDataemissao(imp.getDataEmissao());
        vo.setDataVencimento(imp.getDataVencimento());
        vo.setValor(imp.getValor());
        vo.setMercadologico1(imp.getMercadologico1());
        vo.setObservacao("IMPORTADO VR - " + imp.getObservacao());

        return vo;
    }

    public VerbasAnteriorVO converterVerbaAnterior(VerbaIMP imp) {
        VerbasAnteriorVO ant = new VerbasAnteriorVO();
        ant.setSistema(provider.getSistema());
        ant.setLoja(provider.getLoja());
        ant.setId(imp.getId());
        ant.setIdFornecedor(imp.getIdFornecedor());
        ant.setEmissao(imp.getDataEmissao());
        ant.setVencimento(imp.getDataVencimento());
        ant.setValor(imp.getValor());
        return ant;
    }
}
