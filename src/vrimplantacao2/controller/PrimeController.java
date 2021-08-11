package vrimplantacao2.controller;

import vrimplantacao2.services.PrimeService;

public class PrimeController {
    
    private PrimeService service;
    
    public PrimeController() {
        service = new PrimeService();
    }
    
    public PrimeController(PrimeService service) {
        this.service = service;
    }
    
    public void deletarPagarFornecedorDuplicado(String sistema, String idLojaOrigem, int idLojaVR) throws Exception {
        service.deletarPagarFornecedorDuplicado(sistema, idLojaOrigem, idLojaVR);
    }
}
