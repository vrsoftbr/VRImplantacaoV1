package vrimplantacao2_5.Financeiro.Repository;

import java.util.List;
import java.util.logging.Logger;
import vrimplantacao2_5.Financeiro.IMP.EcfLayoutIMP;
import vrimplantacao2_5.Financeiro.Provider.EcfLayoutRepositoryProvider;
import vrimplantacao2_5.Financeiro.VO.EcfLayoutVO;

public class EcfLayoutRepository {

    private static final Logger LOG = Logger.getLogger(EcfLayoutRepository.class.getName());

    private final EcfLayoutRepositoryProvider provider;

    public EcfLayoutRepository(EcfLayoutRepositoryProvider provider) {
        this.provider = provider;
    }

    public EcfLayoutVO converter(EcfLayoutIMP imp) {
        EcfLayoutVO vo = new EcfLayoutVO();

        vo.setId(imp.getId());
        vo.setId_ecf(imp.getId_ecf());
        vo.setId_tecladoLayout(imp.getId_tecladoLayout());
        vo.setId_finalizadoraLayout(imp.getId_tecladoLayout());
        vo.setId_acumuladorLayout(imp.getId_aliquotaLayout());
        vo.setId_aliquotaLayout(imp.getId_aliquotaLayout());
        vo.setRegraCalculo(imp.getRegraCalculo());
        vo.setArredondamentoAbnt(imp.isArredondamentoAbnt());
        
        return vo;

    }

    public void importarEcfLayout(List<EcfLayoutIMP> ecf) throws Exception {

        provider.begin();
        try {
            provider.setStatus("Carregando Layout ECF existentes...", ecf.size());
            LOG.info("Iniciando gravação dos recebiveís");

            for (EcfLayoutIMP imp : ecf) {
                EcfLayoutVO vo = new EcfLayoutVO();
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
