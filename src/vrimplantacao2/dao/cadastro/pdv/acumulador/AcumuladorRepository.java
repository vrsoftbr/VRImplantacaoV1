/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.cadastro.pdv.acumulador;

import java.util.List;
import vrframework.classe.ProgressBar;
import vrimplantacao.utils.Utils;
import vrimplantacao2.vo.cadastro.pdv.acumulador.AcumuladorVO;
import vrimplantacao2.vo.importacao.AcumuladorIMP;

/**
 *
 * @author lucasrafael
 */
public class AcumuladorRepository {

    private AcumuladorRepositoryProvider provider;
    
    public AcumuladorRepository(AcumuladorRepositoryProvider provider) throws Exception {
        this.provider = provider;
    }
    
    public void importarAcumulador(List<AcumuladorIMP> acumuladores) throws Exception {

        this.provider.begin();
        try {
            
            setNotificacao("Preparando para gravar operadores...", acumuladores.size());
            setNotificacao("Gravando acumulador...", acumuladores.size());
            
            for (AcumuladorIMP imp : acumuladores) {
                
                AcumuladorVO acumulador = null;
                acumulador = converterAcumuador(imp);
                gravarAcumulador(acumulador);
                
                notificar();
            }
            this.provider.commit();
        } catch (Exception ex) {
            this.provider.rollback();
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
    
    public AcumuladorVO converterAcumuador(AcumuladorIMP imp) throws Exception {
        AcumuladorVO vo = new AcumuladorVO();
        vo.setId(Utils.stringToInt(imp.getId()));
        vo.setDescricao(imp.getDescricao());
        return vo;
    }
    
    public void gravarAcumulador(AcumuladorVO acumulador) throws Exception {
        provider.salvar(acumulador);
    }
}
