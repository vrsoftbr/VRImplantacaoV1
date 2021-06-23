package vrimplantacao2_5.service.migracao;

import java.util.List;
import vrimplantacao.classe.ConexaoMySQL;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2_5.dao.migracao.MigracaoSistemasDAO;

/**
 *
 * @author Desenvolvimento
 */
public class MigracaoSistemasService {
    
    private MigracaoSistemasDAO migracaoSistemasDAO;
    private ScriptsSistemasService scriptsSistemasService;
    
    public MigracaoSistemasService() {
        this.migracaoSistemasDAO = new MigracaoSistemasDAO();
        this.scriptsSistemasService = new ScriptsSistemasService();
    }
    
    public MigracaoSistemasService(
            MigracaoSistemasDAO migracaoSistemasDAO,
            ScriptsSistemasService scriptsSistemasService) {
        this.migracaoSistemasDAO = migracaoSistemasDAO;
        this.scriptsSistemasService = scriptsSistemasService;
    }

    public List<Estabelecimento> getLojasOrigem(int idSistema, int idBancoDados) throws Exception {
        String sql = scriptsSistemasService.getLojas(idSistema, idBancoDados);
        return migracaoSistemasDAO.getLojasOrigem(ConexaoMySQL.getConexao(), sql);        
    }
}
