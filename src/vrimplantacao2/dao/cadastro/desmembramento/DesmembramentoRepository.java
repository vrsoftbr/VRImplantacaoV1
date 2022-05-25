package vrimplantacao2.dao.cadastro.desmembramento;

import java.text.SimpleDateFormat;
import java.util.List;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.desmembramento.DesmembramentoAnteriorVO;
import vrimplantacao2.vo.importacao.DesmembramentoIMP;
import vrimplantacao2_5.controller.migracao.LogController;
import vrimplantacao2.vo.cadastro.desmembramento.DesmembramentoVO;

public class DesmembramentoRepository {

    private final DesmembramentoProvider provider;
    private final LogController logController;

    public DesmembramentoRepository(DesmembramentoProvider provider) {
        this.provider = provider;
        this.logController = new LogController();
    }

    public void salvar(List<DesmembramentoIMP> desmembramento) throws Exception {

        MultiMap<String, DesmembramentoIMP> organizados = organizar(desmembramento);
        provider.notificar("Desmembramento - Preparando Importação...");
        MultiMap<Long, DesmembramentoVO> desmem = provider.getId();
        MultiMap<Long, DesmembramentoAnteriorDAO> desmem_old = provider.getImpId();
        
        MultiMap<String, DesmembramentoAnteriorVO> anteriores = provider.getAnteriores();
        
    }

    private MultiMap<String, DesmembramentoIMP> organizar(List<DesmembramentoIMP> desmembramento) throws Exception {
        MultiMap<String, DesmembramentoIMP> result = new MultiMap<>();

        for (DesmembramentoIMP imp : desmembramento) {
            result.put(imp, imp.getImpId());
        }

        desmembramento.clear();
        System.gc();

        result = result.getSortedMap();
        System.gc();

        return result;        
    }

}
