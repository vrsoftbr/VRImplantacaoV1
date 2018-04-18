/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.cadastro.pdv.operador;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import vrframework.classe.ProgressBar;
import vrimplantacao.utils.Utils;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.pdv.operador.OperadorAnteriorVO;
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

            operadores = organizarListagem(operadores);
            System.gc();

            this.provider.begin();
            try {

                //<editor-fold defaultstate="collapsed" desc="Gerando as listagens necessárias para trabalhar com a importação">
                setNotificacao("Preparando para gravar operadores...", operadores.size());
                OperadorIDStack ids = provider.getOperadorIDStack(iniciarEm);
                MultiMap<String, OperadorAnteriorVO> anteriores = provider.getAnteriores();
                //</editor-fold>

                setNotificacao("Gravando operador...", operadores.size());

                for (OperadorIMP imp : operadores) {
                    OperadorAnteriorVO anterior = anteriores.get(
                            provider.getSistema(),
                            provider.getLojaOrigem(),
                            imp.getMatricula()
                    );

                    OperadorVO operador = null;

                    // Se o operador não estiver cadastrado...executa
                    if (anterior == null) {
                        int id = ids.obterID("A");

                        // Converte os dados
                        operador = converterOperador(imp);
                        operador.setId(id);
                        anterior = converterOperadorAnterior(imp);
                        anterior.setMatriculaatual(operador);

                        // Grava os dados
                        gravarOperador(operador);
                        gravarOperadorAnterior(anterior);

                        anteriores.put(
                                anterior,
                                provider.getSistema(),
                                provider.getLojaOrigem(),
                                imp.getMatricula()
                        );
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

    public OperadorAnteriorVO converterOperadorAnterior(OperadorIMP imp) throws Exception {
        OperadorAnteriorVO vo = new OperadorAnteriorVO();
        vo.setSistema(provider.getSistema());
        vo.setLoja(provider.getLojaOrigem());
        vo.setMatricula(imp.getMatricula());
        vo.setNome(imp.getNome());
        vo.setSenha(imp.getSenha());
        vo.setId_tiponiveloperador(imp.getId_tiponiveloperador());
        vo.setId_situacaocadastro(imp.getId_situacadastro());
        return vo;
    }

    public List<OperadorIMP> organizarListagem(List<OperadorIMP> operadores) {
        List<OperadorIMP> result = new ArrayList<>();
        Map<String, OperadorIMP> validos = new LinkedHashMap<>();
        Map<String, OperadorIMP> invalidos = new LinkedHashMap<>();

        for (OperadorIMP imp : operadores) {
            //Verifica se o ID é válido para organizar a listagem;
            try {
                long id = Long.parseLong(imp.getId());

                if (id <= 999999) {
                    validos.put(imp.getId(), imp);
                } else {
                    invalidos.put(imp.getId(), imp);
                }
            } catch (NumberFormatException e) {
                invalidos.put(imp.getId(), imp);
            }
        }

        /**
         * Unifica os resultados, colocando primeiro os operadores com IDs
         * válidos e depois os inválidos que receberão um novo id
         * posteriormente.
         */
        result.addAll(validos.values());
        result.addAll(invalidos.values());

        //Liberar memória
        validos.clear();
        invalidos.clear();

        return result;
    }

    public void gravarOperador(OperadorVO operador) throws Exception {
        provider.salvar(operador);
    }

    public void gravarOperadorAnterior(OperadorAnteriorVO anterior) throws Exception {
        provider.salvar(anterior);
    }

    public void setNotificacao(String mensagem, int qtd) throws Exception {
        ProgressBar.setStatus(mensagem);
        ProgressBar.setMaximum(qtd);
    }

    public void notificar() throws Exception {
        ProgressBar.next();
    }
}
