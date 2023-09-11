/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2_5.mercadologicopadrao.service;

import javax.swing.JOptionPane;
import org.openide.util.Exceptions;
import vrframework.classe.ProgressBar;
import vrframework.classe.Util;
import vrimplantacao2_5.mercadologicopadrao.dao.MercadologicoPadraoDAO;

/**
 *
 * @author Michael
 */
public class MercadologicoPadraoService {

    private MercadologicoPadraoDAO dao = new MercadologicoPadraoDAO();
    private String msg = "";

    public void executaPasso1() {
        msg = "Criado Mercadológico temporário.\nAtualizado mercadológico dos produtos para \"TEMP\".";
        executaPasso(1, dao, msg);
    }

    public void executaPasso2() {
        msg = "Deletado mercadológico atual.";
        executaPasso(2, dao, msg);
    }

    public void executaPasso3() {
        msg = "Inserido Mercadológico Padrão VR.";
        executaPasso(3, dao, msg);
    }

    public void executaPasso4() {
        msg = "Mercadológico Padrão VR executado com sucesso!";
        executaPasso(4, dao, msg);
    }

    public void executaPasso(int passoAtual, MercadologicoPadraoDAO dao, String mensagem) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    ProgressBar.show();
                    ProgressBar.setCancel(false);
                    ProgressBar.setStatus("Executando passo " + passoAtual + ", por favor aguarde.");
                    dao.abrirConexao();
                    switch (passoAtual) {
                        case 1:
                            dao.insereMercadologicoTemporario();
                            dao.atualizaMercadologicoAtualParaTEMP();
                            break;
                        case 2:
                            dao.mercadologicoPadraoPasso2();
                            break;
                        case 3:
                            dao.mercadologicoPadraoPasso3();
                            break;
                        case 4:
                            dao.mercadologicoPadraoPasso4();
                            break;
                        default:
                            throw new Exception("Nenhum passo foi escolhido, erro na classe MercadologicoPadraoService linha 44");
                    }
                    dao.commit();
                    dao.fecharConexao();
                    ProgressBar.dispose();
                    JOptionPane.showMessageDialog(null, mensagem);
                } catch (Exception ex) {
                    try {
                        dao.rollback();
                        dao.fecharConexao();
                    } catch (Exception ex1) {
                        Exceptions.printStackTrace(ex1);
                        ProgressBar.dispose();
                        Util.exibirMensagemErro(ex, "Erro ao executar Passo " + passoAtual + ":");
                        Exceptions.printStackTrace(ex);
                    }
                    ProgressBar.dispose();
                    Util.exibirMensagemErro(ex, "Erro ao executar Passo " + passoAtual + ":");
                    Exceptions.printStackTrace(ex);
                }

            }
        };
        thread.start();
    }
}
