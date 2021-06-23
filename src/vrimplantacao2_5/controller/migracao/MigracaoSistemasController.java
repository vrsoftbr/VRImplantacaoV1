/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2_5.controller.migracao;

import java.util.List;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2_5.service.migracao.MigracaoSistemasService;

/**
 *
 * @author Desenvolvimento
 */
public class MigracaoSistemasController {

    private static int idSistema;
    private static int idBancoDados;
    
    public static int getIdSistema() {
        return idSistema;
    }
    
    public static int getIdBancoDados() {
        return idBancoDados;
    }
    
    public void setIdSistema(int idSistema) {
        this.idSistema = idSistema;
    }
    
    public void setIdBancoDados(int idBancoDados) {
        this.idBancoDados = idBancoDados;
    }
    
    private MigracaoSistemasService migracaoSistemasService;
    
    public MigracaoSistemasController() {
        this.migracaoSistemasService = new MigracaoSistemasService();
    }
    
    public MigracaoSistemasController(MigracaoSistemasService migracaoSistemasService) {
        this.migracaoSistemasService = migracaoSistemasService;
    }
    
    public List<Estabelecimento> getLojasOrigem(int idSistema, int idBancoDados) throws Exception {
        return this.migracaoSistemasService.getLojasOrigem(idSistema, idBancoDados);
    }
}
