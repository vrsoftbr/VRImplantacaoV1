package vrimplantacao2.dao.cadastro.desmembramento;

import java.util.List;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.desmembramento.DesmembramentoAnteriorVO;
import vrimplantacao2.vo.cadastro.desmembramento.DesmembramentoVO;
import vrimplantacao2.vo.importacao.DesmembramentoIMP;
import vrimplantacao2_5.controller.migracao.LogController;

public class DesmembramentoRepository {

    private final DesmembramentoProvider provider;
    private final LogController logController;

    public DesmembramentoRepository(DesmembramentoProvider provider) {
        this.provider = provider;
        this.logController = new LogController();
    }

    public void salvar(List<DesmembramentoIMP> desmembramento) throws Exception {
        MultiMap<String, DesmembramentoAnteriorVO> anteriores = provider.getAnteriores();
        try {
            provider.setStatus("Gravando desmembramentos...");
            provider.setMaximo(desmembramento.size());
            for (DesmembramentoIMP imp : desmembramento) {
                DesmembramentoAnteriorVO anterior = anteriores.get(
                        provider.getSistema(),
                        provider.getLojaOrigem(),
                        imp.getImpId()
                );

                if (anterior == null) {
                    anterior = converterDesmembramentoAnteriorVO(imp);
                    gravar(anterior);
                    anteriores.put(
                            anterior,
                            provider.getSistema(),
                            provider.getLojaOrigem(),
                            imp.getImpId()
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

    private DesmembramentoAnteriorVO converterDesmembramentoAnteriorVO(DesmembramentoIMP imp) {
        
        //int idConexao = promocaoService.existeConexaoMigrada(this.provider.getIdConexao(), this.provider.getSistema());
        DesmembramentoAnteriorVO vo = new DesmembramentoAnteriorVO();
        vo.setSistema(provider.getSistema());
        vo.setLoja(provider.getLojaOrigem());
        vo.setIdConexao(provider.getIdConexao());
        vo.setImpId(imp.getImpId());
        vo.setProdutoPai(imp.getProdutoPai());
        vo.setProdutoFilho(imp.getProdutoFilho());
        vo.setPercentual(imp.getPercentual());

        return vo;
    }

    public void gravarDesmembramento(DesmembramentoVO desmem) throws Exception {
        provider.gravarDesmembramento(desmem);
    }

    public void gravar(DesmembramentoAnteriorVO anterior) throws Exception {
        provider.gravarAnterior(anterior);
    }

    public void getDesmembramentoItens() throws Exception {
        provider.getDesmembramentoItens();
    }

    public void gravarDesmembramentoItens(DesmembramentoAnteriorVO itens) throws Exception {
        provider.gravarDesmembramentoItens(itens);
    }

}
