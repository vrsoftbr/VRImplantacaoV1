package vrimplantacao2.dao.cadastro.cliente;

import java.util.List;
import vrimplantacao2.vo.importacao.PessoaImp;

/**
 *
 * @author Michael-Oliveira
 */
public class PessoaImpRepository {
    
    private PessoaImpDAO dao = new PessoaImpDAO();

    public void salvarPessoaImp(List<PessoaImp> clientes) throws Exception {
        for (PessoaImp cliente : clientes) {
            dao.salvar(cliente);
        }
    }
    
}
