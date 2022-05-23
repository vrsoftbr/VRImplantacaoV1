package vrimplantacao2.dao.cadastro.desmembramento;

public class DesmembramentoService {

    private DesmembramentoAnteriorDAO desmembramentoAnteriorDAO;

    public DesmembramentoService() {
        this.desmembramentoAnteriorDAO = new DesmembramentoAnteriorDAO();
    }

    public DesmembramentoService(DesmembramentoAnteriorDAO desmembramentoAnteriorDAO) {
        this.desmembramentoAnteriorDAO = desmembramentoAnteriorDAO;
    }

    public int existeConexaoMigrada(int idConexao, String sistema) throws Exception {
        return this.desmembramentoAnteriorDAO.getConexaoMigrada(idConexao, sistema);
    }

    public int verificaRegistro() throws Exception {
        return this.desmembramentoAnteriorDAO.verificaRegistro();
    }

    public boolean verificaMigracaoMultiloja(String lojaOrigem, String sistema, int idConexao) throws Exception {
        return this.desmembramentoAnteriorDAO.verificaMigracaoMultiloja(lojaOrigem, sistema, idConexao);
    }

    public String getLojaModelo(int idConexao, String sistema) throws Exception {
        return this.desmembramentoAnteriorDAO.getLojaModelo(idConexao, sistema);
    }

    public boolean verificaMultilojaMigrada(String lojaOrigem, String sistema, int idConexao) throws Exception {
        return this.desmembramentoAnteriorDAO.verificaMultilojaMigrada(lojaOrigem, sistema, idConexao);
    }
}
