package vrimplantacao2.dao.cadastro.notafiscal;

/**
 * Provedor de dados para a classe {@link NotaFiscalRepository}.
 * @author Leandro
 */
public class NotaFiscalRepositoryProvider {
    private final String sistema;
    private final String lojaOrigem;
    private final int lojaVR;

    public NotaFiscalRepositoryProvider(String sistema, String lojaOrigem, int lojaVR) {
        this.sistema = sistema;
        this.lojaOrigem = lojaOrigem;
        this.lojaVR = lojaVR;
    }
    
}
