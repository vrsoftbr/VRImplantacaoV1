package vrimplantacao2_5.service.migracao;

import java.sql.Connection;
import java.util.List;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2_5.dao.migracao.MigracaoSistemasDAO;
import vrimplantacao2_5.vo.enums.EBancoDados;

/**
 *
 * @author Desenvolvimento
 */
public class MigracaoSistemasService {
    
    private MigracaoSistemasDAO migracaoSistemasDAO;
    private ScriptsSistemasService scriptsSistemasService;
    private Connection conexaoBancoDados = null;
    private String sql;
    
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
        sql = scriptsSistemasService.getLojas(idSistema, idBancoDados);
        conexaoBancoDados = ConexaoBancoDadosFactory.getConexao(EBancoDados.getById(idBancoDados)); 
        
        return migracaoSistemasDAO.getLojasOrigem(conexaoBancoDados, sql);        
    }
}
