package vrimplantacao2_5.Financeiro.Repository;

import java.util.List;
import java.util.logging.Logger;
import vrimplantacao2_5.Financeiro.IMP.TipoSaidaContabilidadeIMP;
import vrimplantacao2_5.Financeiro.Provider.TipoSaidaContabilidadeRepositoryProvider;
import vrimplantacao2_5.Financeiro.VO.TipoSaidaContabilidadeVO;

public class TipoSaidaContabilidadeRepository {

    private static final Logger LOG = Logger.getLogger(TipoSaidaContabilidadeRepository.class.getName());

    private final TipoSaidaContabilidadeRepositoryProvider provider;

    public TipoSaidaContabilidadeRepository(TipoSaidaContabilidadeRepositoryProvider provider) {
        this.provider = provider;
    }

    public TipoSaidaContabilidadeVO converter(TipoSaidaContabilidadeIMP imp) {
        TipoSaidaContabilidadeVO vo = new TipoSaidaContabilidadeVO();

        vo.setId(imp.getId());
        vo.setId_tipoSaida(imp.getId_tipoSaida());
        vo.setId_tipoValorContabilidade(imp.getId_tipoValorContabilidade());
        vo.setId_contaContabilCredito(imp.getId_contaContabilCredito());

        return vo;

    }

    public void importarTipoSaidaContabil(List<TipoSaidaContabilidadeIMP> saidaContabil) throws Exception {

        provider.begin();
        try {
            provider.setStatus("Carregando Tipo Saida Contabilidade existentes...", saidaContabil.size());
            LOG.info("Iniciando gravação dos recebiveís");

            for (TipoSaidaContabilidadeIMP imp : saidaContabil) {
                TipoSaidaContabilidadeVO vo = new TipoSaidaContabilidadeVO();
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
