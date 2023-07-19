package vrimplantacao2.dao.cadastro.desmembramento;

import java.util.List;
import java.util.logging.Logger;
import vrimplantacao2.dao.cadastro.financeiro.contaspagar.HistoricoPadraoRepositoryProvider;
import vrimplantacao2.vo.cadastro.HistoricoPadraoVO;
import vrimplantacao2.vo.importacao.HistoricoPadraoIMP;

public class HistoricoPadraoRepository {

    private static final Logger LOG = Logger.getLogger(HistoricoPadraoRepository.class.getName());

    private final HistoricoPadraoRepositoryProvider provider;

    public HistoricoPadraoRepository(HistoricoPadraoRepositoryProvider provider) {
        this.provider = provider;
    }

    public HistoricoPadraoVO converter(HistoricoPadraoIMP imp) {
        HistoricoPadraoVO vo = new HistoricoPadraoVO();

        vo.setId(imp.getId());
        vo.setDescricao(imp.getDescricao());
        vo.setId_situacaoCadastro(imp.getId_situacaoCadastro());
        return vo;

    }

    public void importarHistoricoPadrao(List<HistoricoPadraoIMP> historico) throws Exception {

        provider.begin();
        try {
            provider.setStatus("Carregando Historico Padrão existentes...", historico.size());
            LOG.info("Iniciando gravação dos recebiveís");

            for (HistoricoPadraoIMP imp : historico) {
                HistoricoPadraoVO vo = new HistoricoPadraoVO();
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
