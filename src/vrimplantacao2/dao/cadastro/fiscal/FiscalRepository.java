package vrimplantacao2.dao.cadastro.fiscal;

import vrimplantacao2.dao.cadastro.fiscal.pautafiscal.PautaFiscalRepository;
import vrimplantacao2.dao.cadastro.fiscal.pautafiscal.PautaFiscalRepositoryProvider;

/**
 *
 * @author Leandro
 */
public class FiscalRepository {

    private final PautaFiscalRepository pautaFiscalRep;
    
    public FiscalRepository(FiscalRepositoryProvider provider) throws Exception {
        this.pautaFiscalRep = new PautaFiscalRepository(new PautaFiscalRepositoryProvider(
                provider.getSistema(),
                provider.getLoja(),
                provider.getLojaVR()
        ));
    }
   
    public PautaFiscalRepository pautaFiscal() {
        return pautaFiscalRep;
    }
    
}
