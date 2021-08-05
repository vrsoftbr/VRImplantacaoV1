package vrimplantacao2.services;

import vrimplantacao2.dao.interfaces.PrimeDAO;

public class PrimeService {

    private PrimeDAO dao;
    
    public PrimeService() {
        this.dao = new PrimeDAO();
    }

    public PrimeService(PrimeDAO dao) {
        this.dao = dao;
    }
    
    public void deletarPagarFornecedorDuplicado(String sistema, String idLojaOrigem, int idLojaVR) throws Exception {
        dao.deletarPagarFornecedorDuplicado(sistema, idLojaOrigem, idLojaVR);
    }
}
