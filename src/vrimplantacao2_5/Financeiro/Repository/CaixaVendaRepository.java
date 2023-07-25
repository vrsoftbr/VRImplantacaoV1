package vrimplantacao2_5.Financeiro.Repository;

import java.util.List;
import java.util.logging.Logger;
import vrimplantacao2_5.Financeiro.IMP.CaixaVendaIMP;
import vrimplantacao2_5.Financeiro.Provider.CaixaVendaRepositoryProvider;
import vrimplantacao2_5.Financeiro.VO.CaixaVendaVO;

public class CaixaVendaRepository {

    private static final Logger LOG = Logger.getLogger(CaixaVendaRepository.class.getName());

    private final CaixaVendaRepositoryProvider provider;

    public CaixaVendaRepository(CaixaVendaRepositoryProvider provider) {
        this.provider = provider;
    }

    public CaixaVendaVO converter(CaixaVendaIMP imp) {
        CaixaVendaVO vo = new CaixaVendaVO();

        vo.setId(imp.getId());
        vo.setId_tipoValor((imp.getId_tipoValor()));
        vo.setId_contaContabilFiscalDebito(imp.getId_contaContabilFiscalDebito());
        vo.setId_contaContabilFiscalCredito(imp.getId_contaContabilFiscalCredito());
        vo.setId_historicoPadrao(imp.getId_historicoPadrao());
        vo.setId_centroCusto(imp.getId_centroCusto());

        return vo;

    }

    public void importarCaixaVenda(List<CaixaVendaIMP> caixa) throws Exception {

        provider.begin();
        try {
            provider.setStatus("Carregando Caixa Venda existentes...", caixa.size());
            LOG.info("Iniciando gravação dos recebiveís");

            for (CaixaVendaIMP imp : caixa) {
                CaixaVendaVO vo = new CaixaVendaVO();
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
