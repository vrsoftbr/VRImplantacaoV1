package vrimplantacao2_5.Financeiro.Repository;

import java.util.List;
import java.util.logging.Logger;
import vrimplantacao2_5.Financeiro.IMP.FinalizadoraLayoutRetornoIMP;
import vrimplantacao2_5.Financeiro.Provider.FinalizadoraLayoutRetornoRepositoryProvider;
import vrimplantacao2_5.Financeiro.VO.FinalizadoraLayoutRetornoVO;

public class FinalizadoraLayoutRetornoRepository {

    private static final Logger LOG = Logger.getLogger(FinalizadoraLayoutRetornoRepository.class.getName());

    private final FinalizadoraLayoutRetornoRepositoryProvider provider;

    public FinalizadoraLayoutRetornoRepository(FinalizadoraLayoutRetornoRepositoryProvider provider) {
        this.provider = provider;
    }

    public FinalizadoraLayoutRetornoVO converter(FinalizadoraLayoutRetornoIMP imp) {
        FinalizadoraLayoutRetornoVO vo = new FinalizadoraLayoutRetornoVO();

        vo.setId(imp.getId());
        vo.setId_finalizadoraLayout(imp.getId_finalizadoraLayout());
        vo.setId_finalizadora(imp.getId_finalizadora());
        vo.setRetorno(imp.getRetorno());
        vo.setUtilizado(imp.isUtilizado());

        return vo;

    }

    public void importarFinalizadoraLayout(List<FinalizadoraLayoutRetornoIMP> retorno) throws Exception {

        provider.begin();
        try {
            provider.setStatus("Carregando Finalizadora Layout existentes...", retorno.size());
            LOG.info("Iniciando gravação dos recebiveís");

            for (FinalizadoraLayoutRetornoIMP imp : retorno) {
                FinalizadoraLayoutRetornoVO vo = new FinalizadoraLayoutRetornoVO();
                vo = converter(imp);
                provider.gravar(vo);
            }

            LOG.finest("");

            provider.setStatus();

            provider.commit();
        } catch (Exception ex) {
            provider.rollback();
            throw ex;
        }
    }
}
