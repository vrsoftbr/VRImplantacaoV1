package vrimplantacao2.dao.cadastro.desmembramento;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import vrimplantacao2.dao.cadastro.financeiro.contaspagar.TipoRecebivelRepositoryProvider;
import vrimplantacao2.vo.cadastro.TipoRecebivelVO;
import vrimplantacao2.vo.importacao.TipoRecebivelIMP;

public class TipoRecebivelRepository {

    private static final Logger LOG = Logger.getLogger(TipoRecebivelRepository.class.getName());

    private final TipoRecebivelRepositoryProvider provider;

    public TipoRecebivelRepository(TipoRecebivelRepositoryProvider provider) {
        this.provider = provider;
    }

    public TipoRecebivelVO converterRecebivel(TipoRecebivelIMP imp) {
        TipoRecebivelVO vo = new TipoRecebivelVO();
        vo.setId(imp.getId());
        vo.setDescricao(imp.getDescricao());
        vo.setPercentual(imp.getPercentual());
        vo.setId_TipoTef(imp.getId_tipoTef());
        vo.setId_TipoTicket(imp.getId_tipotiket());
        vo.setGeraRecebimento(imp.isGeraRecebimento());
        vo.setId_contaContabilFiscalDebito(imp.getId_ContaContabilFiscalDebito());
        vo.setId_ContaContabilFiscalCredito(imp.getId_ContaContabilFiscalCredito());
        vo.setId_HistoricoPadrao(imp.getId_HistoricoPadrao());
        vo.setId_situacaoCadastro(imp.getSituacaoCadastro());
        vo.setId_TipoPrazo(imp.getId_TipoVistaPrazo());
        vo.setId_TipoCartaoTef(imp.getId_TipoCartaoTef());
        vo.setId_Fornecedor(imp.getId_Fornecedor());
        vo.setTef(imp.isTef());
        vo.setId_tiporecebimento(imp.getId_TipoRecebimento());
        vo.setContabiliza(imp.isContabiliza());
        vo.setId_ContaContabilFinanceiro(imp.getId_contaContabilFinanceiro());

        return vo;
    }

    public void importarRecebivel(List<TipoRecebivelIMP> recebiveis) throws Exception {

        provider.begin();
        try {
            provider.setStatus("Carregando Recebiveis existentes...", recebiveis.size());
            LOG.info("Iniciando gravação dos recebiveís");

            for (TipoRecebivelIMP imp : recebiveis) {
                TipoRecebivelVO vo = new TipoRecebivelVO();
                vo = converterRecebivel(imp);
                provider.gravar(vo);
            }

            //       provider.gravar(vo);
            LOG.finest("");

            provider.setStatus();

            provider.commit();
        } catch (Exception ex) {
            provider.rollback();
            throw ex;
        }
    }
}
