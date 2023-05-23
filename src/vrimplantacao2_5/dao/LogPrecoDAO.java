/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2_5.dao;

import java.sql.Statement;
import java.util.List;
import org.openide.util.Exceptions;
import org.postgresql.util.PSQLException;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2_5.vo.cadastro.LogPrecoVO;

/**
 *
 * @author Michael
 */
public class LogPrecoDAO {

    public LogPrecoDAO() {
        try {
            criarTabelaLogAtualizaPreco();
        } catch (Exception ex) {
            System.out.println("Erro no Construtor da classe LogPrecoDAO");
            Exceptions.printStackTrace(ex);
        }
    }

    public void criarTabelaLogAtualizaPreco() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute(
                    "CREATE TABLE IF NOT EXISTS implantacao.logatualizapreco (\n"
                    + "	impid varchar(100) NOT NULL PRIMARY key,\n"
                    + " impsistema varchar(100),\n"
                    + " imploja varchar(6),\n"
                    + "	descricao varchar(250),\n"
                    + "	codigoatual integer,\n"
                    + "	preco numeric(11,2),\n"
                    + "	dataalteracao timestamp\n"
                    + ")"
            );
        } catch (PSQLException e) {
            System.out.println("erro ao criar tabela implantacao.logatualizapreco: \n\n" + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("erro ao criar tabela implantacao.logatualizapreco: \n\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    public void salvarLogPreco(List<LogPrecoVO> logPrecos) throws Exception {
        ProgressBar.setStatus("Salvando Log preço: " + logPrecos.size());
        ProgressBar.setMaximum(logPrecos.size());
        for (LogPrecoVO logPrecoVO : logPrecos) {
            try (Statement stm = Conexao.createStatement()) {
                SQLBuilder sql = new SQLBuilder();
                sql.setTableName("logatualizapreco");
                sql.setSchema("implantacao");
                sql.put("impid", logPrecoVO.getImpId());
                sql.put("impsistema", logPrecoVO.getImpSistema());
                sql.put("imploja", logPrecoVO.getImpLoja());
                sql.put("descricao", logPrecoVO.getDescricao());
                sql.put("codigoatual", logPrecoVO.getCoigoatual());
                sql.put("preco", logPrecoVO.getPreco());
                sql.put("dataalteracao", logPrecoVO.getDataAlteracao());

                try {
                    stm.execute(sql.getInsert());
                    ProgressBar.next();
                } catch (Exception a) {
                    System.out.println(sql.getInsert());
                    a.printStackTrace();
                }
            }
        }
    }

    public void deletarLogAtualizaPreco(String sistema, String loja) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute("delete from implantacao.logatualizapreco "
                    + "where impsistema = '" + sistema + "' and imploja = '" + loja + "'");
        }
    }

}
