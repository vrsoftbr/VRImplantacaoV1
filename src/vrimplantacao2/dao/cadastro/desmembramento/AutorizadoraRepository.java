package vrimplantacao2.dao.cadastro.desmembramento;

import java.util.List;
import java.util.logging.Logger;
import vrimplantacao2.dao.cadastro.financeiro.contaspagar.AutorizadoraRepositoryProvider;
import vrimplantacao2.vo.cadastro.AutorizadoraVO;
import vrimplantacao2.vo.importacao.AutorizadoraIMP;

public class AutorizadoraRepository {

    private static final Logger LOG = Logger.getLogger(AutorizadoraRepository.class.getName());

    private final AutorizadoraRepositoryProvider provider;

    public AutorizadoraRepository(AutorizadoraRepositoryProvider provider) {
        this.provider = provider;
    }

    public AutorizadoraVO converterAutorizadora(AutorizadoraIMP imp) {
        AutorizadoraVO vo = new AutorizadoraVO();
        vo.setId(imp.getId());
        vo.setDescricao(imp.getDescricao());
        vo.setUtilizado(imp.isUtilizado());
        return vo;
    }

    public void importarAutorizadora(List<AutorizadoraIMP> autorizadoras) throws Exception {

        provider.begin();
        try {
            provider.setStatus("Carregando Tipo Tef existentes...", autorizadoras.size());
            LOG.info("Iniciando gravação dos recebiveís");

            for (AutorizadoraIMP imp : autorizadoras) {
                AutorizadoraVO vo = new AutorizadoraVO();
                vo = converterAutorizadora(imp);
                
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
