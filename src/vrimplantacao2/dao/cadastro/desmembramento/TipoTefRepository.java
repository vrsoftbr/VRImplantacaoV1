package vrimplantacao2.dao.cadastro.desmembramento;

import java.util.List;
import java.util.logging.Logger;
import vrimplantacao2.dao.cadastro.financeiro.contaspagar.TipoTefRepositoryProvider;
import vrimplantacao2.vo.cadastro.TipoTefVO;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.importacao.TipoTefIMP;

public class TipoTefRepository {

    private static final Logger LOG = Logger.getLogger(TipoTefRepository.class.getName());

    private final TipoTefRepositoryProvider provider;

    public TipoTefRepository(TipoTefRepositoryProvider provider) {
        this.provider = provider;
    }

    public TipoTefVO converterTipoTef(TipoTefIMP imp) {
        TipoTefVO vo = new TipoTefVO();
            vo.setId(imp.getId());
            vo.setDescricao(imp.getDescricao());
            vo.setTipocomunicao(imp.getTipocomunicao());
            vo.setBandeira(imp.getBandeira());
            vo.setImprimeCupom(imp.isImprimeCupom());
            vo.setId_situacaoCadastro(SituacaoCadastro.ATIVO);
            vo.setNumeroParcela(imp.getNumeroParcela());
            vo.setId_autorizadora(imp.getId_autorizadora());
            return vo;

    }

    public void importarTipoTef(List<TipoTefIMP> tef) throws Exception {

        provider.begin();
        try {
            provider.setStatus("Carregando Tipo Tef existentes...", tef.size());
            LOG.info("Iniciando gravação dos recebiveís");

            for (TipoTefIMP imp : tef) {
                TipoTefVO vo = new TipoTefVO();
                vo = converterTipoTef(imp);
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
