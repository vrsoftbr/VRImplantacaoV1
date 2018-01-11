package vrimplantacao2.dao.cadastro.devolucao.receber;

import java.sql.Statement;
import java.util.List;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao2.vo.cadastro.financeiro.ReceberDevolucaoVO;

public class ReceberDevolucaoDAO {

    public void salvar(List<ReceberDevolucaoVO> v_list, int idLoja) throws Exception {
        Statement stm = null;
        StringBuilder sql = null;
        try {
            Conexao.begin();
            stm = Conexao.createStatement();
            ProgressBar.setMaximum(v_list.size());
            ProgressBar.setStatus("Importando ReceberDevolucao...");
            for (ReceberDevolucaoVO i_list : v_list) {

                sql = new StringBuilder();
                sql.append("insert into receberdevolucao ("
                        + "id_loja, id_fornecedor, numeronota, dataemissao, datavencimento, \n"
                        + "valor, observacao, id_situacaoreceberdevolucao, id_tipolocalcobranca, \n"
                        + "id_tipodevolucao, lancamentomanual, valorpagarfornecedor, \n"
                        + "justificativa, numeroparcela, exportado) "
                        + "values ("
                        + idLoja + ", "
                        + i_list.getIdFornecedor() + ", "
                        + i_list.getNumeroNota() + ", "
                        + "'" + i_list.getDataemissao() + "', "
                        + "'" + i_list.getDatavencimento() + "', "
                        + i_list.getValor() + ", "
                        + "'" + i_list.getObservacao() + "', "
                        + i_list.getIdSituacaoReceberDevolucao() + ", "
                        + i_list.getIdTipolocalCobranca() + ", "
                        + i_list.getIdTipoDevolucao() + ", "
                        + i_list.isLancamentoManual() + ", "
                        + i_list.getValorPagarFornecedor() + ", "
                        + "'" + i_list.getJustificativa() + "', "
                        + i_list.getNumeroParcela() + ", "
                        + i_list.isExportado() + ");");
                stm.execute(sql.toString());
                ProgressBar.next();
            }
            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }
}