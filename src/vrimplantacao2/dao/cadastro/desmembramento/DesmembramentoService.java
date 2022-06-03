package vrimplantacao2.dao.cadastro.desmembramento;

public class DesmembramentoService {

    private DesmembramentoAnteriorDAO desmembramentoAnteriorDAO;

    public DesmembramentoService() {
        this.desmembramentoAnteriorDAO = new DesmembramentoAnteriorDAO();
    }

    public DesmembramentoService(DesmembramentoAnteriorDAO desmembramentoAnteriorDAO) {
        this.desmembramentoAnteriorDAO = desmembramentoAnteriorDAO;
    }
}