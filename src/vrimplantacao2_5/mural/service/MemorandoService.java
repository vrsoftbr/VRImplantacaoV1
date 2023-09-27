/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2_5.mural.service;

import org.openide.util.Exceptions;
import vrframework.classe.Util;
import vrimplantacao2_5.mural.dao.MemorandoDAO;
import vrimplantacao2_5.mural.gui.MemorandoGUI;
import vrimplantacao2_5.mural.vo.MemorandoVO;

/**
 *
 * @author Michael
 */
public class MemorandoService {
    
    private MemorandoDAO dao;
    private int lembretesCadastrados = 0;

    public MemorandoService() throws Exception {
        this.dao = new MemorandoDAO();        
    }
    
    public int inicializaMemorando() throws Exception {
        lembretesCadastrados = dao.retornaUltimoId();
        if (lembretesCadastrados != 0) {
            MemorandoGUI.setNumeroLembrete(String.valueOf(lembretesCadastrados));
        }
        return lembretesCadastrados;
    }

    public void deletarLembretes() {
        try {
            dao.deletarLembretes();
            lembretesCadastrados = 0;
        } catch (Exception e) {
            Util.exibirMensagemErro(e, "Erro ao deletar lembretes");
        }
    }

    public MemorandoVO carregarUltimoLembrete() {
        try {
           return dao.retornaMensagem(lembretesCadastrados);
        } catch (Exception e) {
            Util.exibirMensagemErro(e, "Erro ao carregar lembretes");
        }
        return new MemorandoVO();
    }

    public void incluir(String lembrete) {
        try {
            lembretesCadastrados = dao.inserirMemorando(lembrete);
        } catch (Exception e) {
            Util.exibirMensagemErro(e, "Erro ao gravar lembretes");
        }
    }

    public MemorandoVO carregarLembretePorId(int idPossivel) {
        try {
            MemorandoVO memoVO = dao.retornaMensagem(idPossivel);
            if (memoVO.getId() != 0) {
                return memoVO;
            }
        } catch (Exception e) {
            Util.exibirMensagemErro(e, "Erro ao carregar lembretes");
        }
        return null;
    }

    public int ultimoIdLembrete() {
        try {
            return dao.retornaUltimoId();
        } catch (Exception e) {
            Util.exibirMensagemErro(e, "Erro ao carregar ultimo id do lembrete");
        }
        return 0;
    }
}
