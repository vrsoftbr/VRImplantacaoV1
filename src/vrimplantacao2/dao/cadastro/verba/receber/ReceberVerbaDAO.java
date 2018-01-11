package vrimplantacao2.dao.cadastro.verba.receber;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.Stack;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao2.vo.cadastro.financeiro.ReceberVerbaVO;

public class ReceberVerbaDAO {

    private Stack<Integer> carregarIdsLivres(int limit) throws Exception {
        Stack<Integer> result = new Stack<>();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT id FROM generate_series(1, " + limit + ") "
                            + "AS s(id) except select id from "
                            + "receberverba order by id desc"
            )) {
                while (rst.next()) {
                    result.add(rst.getInt("id"));
                }
            }
        }
        return result;
    }
    
    public void salvar(List<ReceberVerbaVO> v_list, int idLoja) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        Stack<Integer> idsLivres = carregarIdsLivres(100000);
        int id;
        try {
            Conexao.begin();
            stm = Conexao.createStatement();
            ProgressBar.setMaximum(v_list.size());
            ProgressBar.setStatus("Importando ReceberVerba...");

            for (ReceberVerbaVO i_list : v_list) {
                id = idsLivres.pop();
                sql = new StringBuilder();
                sql.append("insert into receberverba ("
                        + "id, id_loja, id_tiporecebimento, dataemissao, datavencimento, \n"
                        + "id_fornecedor, id_divisaofornecedor, id_comprador, mercadologico1, \n"
                        + "id_tipoverba, id_situacaocadastro, id_situacaoreceberverba, representante, \n"
                        + "telefone, reciboimpresso, id_tipolocalcobranca, valor, observacao, \n"
                        + "cpfrepresentante, rgrepresentante, id_tipoorigemverba)"
                        + "values ("
                        + id + ", "
                        + idLoja + ", "
                        + i_list.getIdTipoRecebimento() + ", "
                        + "'" + i_list.getDataemissao() + "', "
                        + "'" + i_list.getDatavencimento() + "', "
                        + i_list.getIdFornecedor() + ", "
                        + i_list.getIdDivisaoFornecedor() + ", "
                        + i_list.getIdComprador() + ", "
                        + i_list.getMercadologico1() + ", "
                        + i_list.getIdTipoVerba() + ", "
                        + i_list.getIdSituacaoCadastro() + ", "
                        + i_list.getIdSituacaoReceberVerba() + ", "
                        + "'" + i_list.getRepresentante() + "', "
                        + "'" + i_list.getTelefone() + "', "
                        + i_list.isReciboImpresso() + ", "
                        + i_list.getIdTipoLocalCobranca() + ", "
                        + i_list.getValor() + ", "
                        + "'" + i_list.getObservacao() + "', "
                        + i_list.getCpfRepresentante() + ", "
                        + "'" + i_list.getRgRepresentante() + "',"
                        + i_list.getIdTipoOrigemVerba() + ");");
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
