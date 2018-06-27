/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.cadastro.pdv.acumulador;

import java.util.List;
import vrframework.classe.ProgressBar;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.pdv.acumulador.AcumuladorLayoutRetornoVO;
import vrimplantacao2.vo.cadastro.pdv.acumulador.AcumuladorLayoutVO;
import vrimplantacao2.vo.cadastro.pdv.acumulador.AcumuladorVO;
import vrimplantacao2.vo.importacao.AcumuladorIMP;
import vrimplantacao2.vo.importacao.AcumuladorLayoutIMP;
import vrimplantacao2.vo.importacao.AcumuladorLayoutRetornoIMP;

/**
 *
 * @author lucasrafael
 */
public class AcumuladorRepository {

    private AcumuladorRepositoryProvider provider;

    public AcumuladorRepository(AcumuladorRepositoryProvider provider) throws Exception {
        this.provider = provider;
    }

    public void importarAcumulador(List<AcumuladorIMP> acumuladores, List<AcumuladorLayoutIMP> acumuladoresLayout, List<AcumuladorLayoutRetornoIMP> acumuladoresLayoutRetorno) throws Exception {

        this.provider.begin();
        try {

            setNotificacao("Preparando para gravar acumuladores...", acumuladores.size());
            setNotificacao("Gravando acumulador...", acumuladores.size());
            MultiMap<Integer, AcumuladorVO> gravados = provider.getAcumuladores();

            for (AcumuladorIMP imp : acumuladores) {

                AcumuladorVO acumulador = null;

                acumulador = converterAcumulador(imp);
                gravarAcumulador(acumulador);

                gravados.put(acumulador, imp.getId());

                notificar();
            }

            setNotificacao("Preparando para gravar acumuladores layout...", acumuladoresLayout.size());
            setNotificacao("Gravando acumulador layout...", acumuladoresLayout.size());

            for (AcumuladorLayoutIMP impL : acumuladoresLayout) {

                AcumuladorLayoutVO acumuladorLayout = null;
                acumuladorLayout = converterAcumuladorLayout(impL);
                gravarAcumuladorLayout(acumuladorLayout);
            }

            setNotificacao("Preparando para gravar acumuladores layout retorno...", acumuladoresLayoutRetorno.size());
            setNotificacao("Gravando acumulador layout retorno...", acumuladoresLayoutRetorno.size());

            for (AcumuladorLayoutRetornoIMP impR : acumuladoresLayoutRetorno) {

                AcumuladorLayoutRetornoVO acumuladorLayoutRetorno = new AcumuladorLayoutRetornoVO();
                acumuladorLayoutRetorno = converterAcumuadorLayoutRetorno(impR);
                gravarAcumuladorLayoutRetorno(acumuladorLayoutRetorno);
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

    public AcumuladorVO converterAcumulador(AcumuladorIMP imp) throws Exception {
        AcumuladorVO vo = new AcumuladorVO();
        vo.setId(imp.getId());
        vo.setDescricao(imp.getDescricao());
        return vo;
    }

    public AcumuladorLayoutVO converterAcumuladorLayout(AcumuladorLayoutIMP imp) throws Exception {
        AcumuladorLayoutVO vo = new AcumuladorLayoutVO();
        vo.setId(Integer.parseInt(imp.getId()));
        vo.setIdLoja(provider.getLojaVR());
        vo.setDescricao(imp.getDescricao());
        return vo;
    }

    public AcumuladorLayoutRetornoVO converterAcumuadorLayoutRetorno(AcumuladorLayoutRetornoIMP imp) throws Exception {
        AcumuladorLayoutRetornoVO vo = new AcumuladorLayoutRetornoVO();
        vo.setIdAcumuladorLayout(Integer.parseInt(imp.getIdAcumuladorLayout()));
        vo.setIdAcumulador(Integer.parseInt(imp.getIdAcumulador()));
        vo.setRetorno(Integer.parseInt(imp.getRetorno()));
        vo.setTitulo(Integer.parseInt(imp.getTitulo()));
        return vo;
    }

    public void deletar() throws Exception {
        provider.delete();
    }
    
    public void gravarAcumulador(AcumuladorVO acumulador) throws Exception {
        provider.salvar(acumulador);
    }

    public void gravarAcumuladorLayout(AcumuladorLayoutVO acumuladorLayout) throws Exception {
        provider.salvar(acumuladorLayout);
    }
    
    public void gravarAcumuladorLayoutRetorno(AcumuladorLayoutRetornoVO acumuladorLayoutRetorno) throws Exception {
        provider.salvar(acumuladorLayoutRetorno);
    }
}
