/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.cadastro.pdv.operador;

import java.util.List;
import vrframework.classe.ProgressBar;
import vrimplantacao.utils.Utils;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.pdv.operador.OperadorVO;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.importacao.OperadorIMP;

/**
 * Repositório do Cliente para efetuar a importação de Operadores.
 *
 * @author lucasrafael
 */
public class OperadorRepository {

    private OperadorRepositoryProvider provider;

    public OperadorRepository(OperadorRepositoryProvider provider) throws Exception {
        this.provider = provider;
    }

    public void importarOperador(List<OperadorIMP> operadores) throws Exception {

        int iniciarEm = 1;
        boolean parametrosValidos = true;

        if (parametrosValidos) {

            System.gc();

            this.provider.begin();
            try {

                //<editor-fold defaultstate="collapsed" desc="Gerando as listagens necessárias para trabalhar com a importação">
                setNotificacao("Preparando para gravar operadores...", operadores.size());
                OperadorIDStack ids = provider.getOperadorIDStack(iniciarEm);
                MultiMap<String, OperadorVO> anteriores = provider.getOperadores();
                //</editor-fold>

                setNotificacao("Gravando operador...", operadores.size());

                for (OperadorIMP imp : operadores) {
                    OperadorVO anterior = anteriores.get(
                            String.valueOf(provider.getLojaVR()),
                            imp.getMatricula(),
                            imp.getNome()
                    );

                    OperadorVO operador = null;

                    // Se o operador não estiver cadastrado...executa
                    if (anterior == null) {
                        int id = ids.obterID("A");

                        // Converte os dados
                        operador = converterOperador(imp);
                        operador.setId(id);

                        // Grava os dados
                        gravarOperador(operador);
                        //gravarOperadorAnterior(anterior);
                    }
                    notificar();
                }
                this.provider.commit();
            } catch (Exception ex) {
                this.provider.rollback();
                throw ex;
            }
        } else {
            throw new Exception("Há valores incorretos nos parametros.");
        }
    }

    public OperadorVO converterOperador(OperadorIMP imp) throws Exception {
        OperadorVO vo = new OperadorVO();

        vo.setMatricula(Utils.stringToInt(imp.getMatricula()));
        vo.setNome(imp.getNome());
        vo.setSenha(Utils.stringToInt(imp.getSenha()));
        vo.setCodigo(Utils.stringToInt(imp.getCodigo()));
        vo.setId_tiponiveloperador(Utils.stringToInt(imp.getId_tiponiveloperador()));
        vo.setSituacaoCadastro(Utils.stringToInt(imp.getId_situacadastro()) == 1 ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
        return vo;
    }

    public void gravarOperador(OperadorVO operador) throws Exception {
        provider.salvar(operador);
    }

    public void setNotificacao(String mensagem, int qtd) throws Exception {
        ProgressBar.setStatus(mensagem);
        ProgressBar.setMaximum(qtd);
    }

    public void notificar() throws Exception {
        ProgressBar.next();
    }
}
