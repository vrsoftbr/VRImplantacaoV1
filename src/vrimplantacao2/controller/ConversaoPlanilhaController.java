package vrimplantacao2.controller;

import vrimplantacao2.services.ConversaoPlanilhaService;

/**
 *
 * @author guilhermegomes
 */
public class ConversaoPlanilhaController {
    
   private final String arquivo;
   private String nameTable;
    
    ConversaoPlanilhaService service;

    public ConversaoPlanilhaController(String arquivo, String nameTable) throws Exception {
        this.arquivo = arquivo;
        this.nameTable = nameTable;
        this.service = new ConversaoPlanilhaService(arquivo, ";".charAt(0), false, "\"".charAt(0));
    }
    
    public void converter() throws Exception {
        service.setNameTable(nameTable);
        service.converter();
    }
}
