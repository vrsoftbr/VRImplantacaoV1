/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.cadastro.desmembramento;

import vrframework.classe.ProgressBar;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.desmembramento.DesmembramentoAnteriorVO;
import vrimplantacao2.vo.cadastro.desmembramento.DesmembramentoVO;

public class DesmembramentoProvider {

    public DesmembramentoProvider(String sistema, String lojaOrigem, int lojaVR, int idConexao) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void notificar(String mensagem) throws Exception {
        ProgressBar.setStatus(mensagem);
    }

    public void notificar(String mensagem, int size) throws Exception {
        notificar(mensagem);
        ProgressBar.setMaximum(size);
    }

    public void notificar() throws Exception {
        ProgressBar.next();
    }

    MultiMap<Long, DesmembramentoAnteriorDAO> getImpId() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    MultiMap<Long, DesmembramentoVO> getId() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    MultiMap<String, DesmembramentoAnteriorVO> getAnteriores() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
