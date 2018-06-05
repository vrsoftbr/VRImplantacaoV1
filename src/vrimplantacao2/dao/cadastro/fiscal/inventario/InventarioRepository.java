/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.cadastro.fiscal.inventario;

import java.util.List;
import vrframework.classe.ProgressBar;
import vrimplantacao2.vo.importacao.InventarioIMP;

/**
 *
 * @author lucasrafael
 */
public class InventarioRepository {

    private InventarioRepositoryProvider provider;

    public InventarioRepository(InventarioRepositoryProvider provider) throws Exception {
        this.provider = provider;
    }

    public void importarInventario(List<InventarioIMP> inventario) throws Exception {

        System.gc();

        this.provider.begin();
        try {

            //<editor-fold defaultstate="collapsed" desc="Gerando as listagens necessárias para trabalhar com a importação">
            setNotificacao("Preparando para gravar operadores...", inventario.size());
            //</editor-fold>
            
            setNotificacao("Gravando operador...", inventario.size());

        } catch (Exception ex) {
            throw ex;
        }
    }

    public void setNotificacao(String mensagem, int qtd) throws Exception {
        ProgressBar.setStatus(mensagem);
        ProgressBar.setMaximum(qtd);
    }

    public void notificar() throws Exception {
        ProgressBar.next();
    }
}
