package vrimplantacao2_5.Financeiro.Repository;

import java.util.List;
import java.util.logging.Logger;
import vrimplantacao2_5.Financeiro.IMP.TipoRecebivelFinalizadoraIMP;
import vrimplantacao2_5.Financeiro.Provider.TipoRecebivelFinalizadoraRepositoryProvider;
import vrimplantacao2_5.Financeiro.VO.TipoRecebivelFinalizadoraVO;

public class TipoRecebivelFinalizadoraRepository {

    private static final Logger LOG = Logger.getLogger(TipoRecebivelFinalizadoraRepository.class.getName());

    private final TipoRecebivelFinalizadoraRepositoryProvider provider;

    public TipoRecebivelFinalizadoraRepository(TipoRecebivelFinalizadoraRepositoryProvider provider) {
        this.provider = provider;
    }

    public TipoRecebivelFinalizadoraVO converter(TipoRecebivelFinalizadoraIMP imp) {
        TipoRecebivelFinalizadoraVO vo = new TipoRecebivelFinalizadoraVO();

        vo.setId(imp.getId());
        vo.setId_TipoRecebivel(imp.getId_tipoRecebivel());
        vo.setId_finalizadora(imp.getId_finalizadora());
        return vo;

    }

    public void importarTipoRecebivelFinalizadora(List<TipoRecebivelFinalizadoraIMP> tipoRecebivelFinalizadora) throws Exception {

        provider.begin();
        try {
            provider.setStatus("Carregando Tipo Recebivel Finalizadora existentes...", tipoRecebivelFinalizadora.size());
            LOG.info("Iniciando gravação dos recebiveís");

            for (TipoRecebivelFinalizadoraIMP imp : tipoRecebivelFinalizadora) {
                TipoRecebivelFinalizadoraVO vo = new TipoRecebivelFinalizadoraVO();
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
