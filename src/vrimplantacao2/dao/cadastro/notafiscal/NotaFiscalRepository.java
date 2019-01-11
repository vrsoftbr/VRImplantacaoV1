package vrimplantacao2.dao.cadastro.notafiscal;

import java.util.HashSet;

/**
 * Repositório de operações com a nota fiscal.
 * @author Leandro
 */
public class NotaFiscalRepository {
    
    private final NotaFiscalRepositoryProvider provider;

    public NotaFiscalRepository(NotaFiscalRepositoryProvider provider) {
        this.provider = provider;
    }

    public void importar(HashSet<OpcaoNotaFiscal> opt) throws Exception {
        throw new UnsupportedOperationException("Funcao ainda nao suportada.");
    }
    
}
