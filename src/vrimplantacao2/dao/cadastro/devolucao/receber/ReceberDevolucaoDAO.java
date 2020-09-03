package vrimplantacao2.dao.cadastro.devolucao.receber;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao2.vo.cadastro.financeiro.ReceberDevolucaoVO;

public class ReceberDevolucaoDAO {

    public void salvar(List<ReceberDevolucaoVO> v_list, int idLoja) throws Exception {
        Statement stm = null;
        StringBuilder sql = null;
        ResultSet rst = null;
        try {
            Conexao.begin();
            stm = Conexao.createStatement();
            ProgressBar.setMaximum(v_list.size());
            ProgressBar.setStatus("Importando Receber Devolucao...");
            for (ReceberDevolucaoVO i_list : v_list) {

                rst = stm.executeQuery(
                        "select id from receberdevolucao "
                        + "where dataemissao = '" + i_list.getDataemissao() + "' "
                        + "and numeronota = " + i_list.getNumeroNota() + " "
                        + "and valor = " + i_list.getValor() + " "
                        + "and id_fornecedor = " + i_list.getIdFornecedor() + " "
                        + "and id_loja = " + idLoja
                );

                if (!rst.next()) {

                    sql = new StringBuilder();
                    sql.append("insert into receberdevolucao ("
                            + "id_loja, id_fornecedor, numeronota, dataemissao, datavencimento, \n"
                            + "valor, valorliquido, observacao, id_situacaoreceberdevolucao, id_tipolocalcobranca, \n"
                            + "id_tipodevolucao, lancamentomanual, valorpagarfornecedor, \n"
                            + "justificativa, numeroparcela, exportado) "
                            + "values ("
                            + idLoja + ", "
                            + i_list.getIdFornecedor() + ", "
                            + i_list.getNumeroNota() + ", "
                            + "'" + i_list.getDataemissao() + "', "
                            + "'" + i_list.getDatavencimento() + "', "
                            + i_list.getValor() + ", "
                            + i_list.getValor() + ", "
                            + "'" + i_list.getObservacao() + "', "
                            + i_list.getIdSituacaoReceberDevolucao() + ", "
                            + i_list.getIdTipolocalCobranca() + ", "
                            + i_list.getIdTipoDevolucao() + ", "
                            + i_list.isLancamentoManual() + ", "
                            + i_list.getValorPagarFornecedor() + ", "
                            + "'" + i_list.getJustificativa() + "', "
                            + i_list.getNumeroParcela() + ", "
                            + i_list.isExportado() + ");"
                    );
                    stm.execute(sql.toString());
                }
                ProgressBar.next();
            }
            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void gravarIdReceberDevolucaoDuplicado(List<ReceberDevolucaoVO> v_list, int idLoja) throws Exception {
        File f = new File("C:\\vr\\Implantacao\\scripts_devolucao_duplicado.txt");
        FileWriter fw = new FileWriter(f);
        BufferedWriter bw = new BufferedWriter(fw);
        Statement stm = null;
        StringBuilder sql = null;
        ResultSet rst = null;
        try {
            Conexao.begin();
            stm = Conexao.createStatement();
            ProgressBar.setMaximum(v_list.size());
            ProgressBar.setStatus("Gravando Id Receber Devolucao Duplicado...");
            for (ReceberDevolucaoVO i_list : v_list) {

                rst = stm.executeQuery(
                        "select * from receberdevolucao\n"
                        + "where numeronota = " + i_list.getNumeroNota() + "\n"
                        + "and id_fornecedor = " + i_list.getIdFornecedor() + "\n"
                        + "and dataemissao = '" + i_list.getDataemissao() + "' \n"
                        + "and datavencimento = '" + i_list.getDatavencimento() + "' \n"
                        + "and id_loja = " + i_list.getIdLoja() + " \n"
                        + "and valor = " + i_list.getValor() + " "
                        + "and observacao like '%IMPORTADO VR%'\n" 
                        + "and id_situacaoreceberdevolucao = 0");
                
                if (rst.next()) {
                        stm.execute("insert into implantacao.id_receberdevolucao(id, id_loja) "
                                + "values ("
                                + rst.getInt("id") + ", "
                                + rst.getInt("id_loja") + ");");
                }
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
