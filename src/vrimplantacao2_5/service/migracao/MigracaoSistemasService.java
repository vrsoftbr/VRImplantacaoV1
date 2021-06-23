package vrimplantacao2_5.service.migracao;

import java.util.List;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2_5.dao.migracao.MigracaoSistemasDAO;

/**
 *
 * @author Desenvolvimento
 */
public class MigracaoSistemasService {
    
    private MigracaoSistemasDAO migracaoSistemasDAO;
    
    MigracaoSistemasService() {
        this.migracaoSistemasDAO = new MigracaoSistemasDAO();
    }
    
    MigracaoSistemasService(MigracaoSistemasDAO migracaoSistemasDAO) {
        this.migracaoSistemasDAO = migracaoSistemasDAO;
    }
    
    public List<Estabelecimento> getLojasOrigem() throws Exception {
        return migracaoSistemasDAO.getLojasOrigem(null, "");
    }
}
