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
import vrimplantacao.utils.Utils;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2_5.vo.cadastro.LogAtualizacaoVO;

/**
 *
 * @author Michael
 */
public class LogAtualizacaoDAO {

    public LogAtualizacaoDAO() {
        try {
            criarTabelaLogAtualizacao();
        } catch (Exception ex) {
            System.out.println("Erro no Construtor da classe LogAtualizacaoDAO");
            Exceptions.printStackTrace(ex);
        }
    }

    public void criarTabelaLogAtualizacao() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute(
                    "CREATE TABLE IF NOT EXISTS implantacao.log_atualizacao (\n"
                    + "	impid varchar(100),\n"
                    + " impsistema varchar(100),\n"
                    + " imploja varchar(6),\n"
                    + "	descricao varchar(250),\n"
                    + "	codigoatual integer,\n"
                    + "	preco numeric(11,2),\n"
                    + "	estoque numeric(11,2),\n"
                    + "	custocomimposto numeric(11,2),\n"
                    + "	custosemimposto numeric(11,2),\n"
                    + "	dataalteracao timestamp\n"
                    + ")"
            );
        } catch (PSQLException e) {
            System.out.println("erro ao criar tabela implantacao.log_atualizacao: \n\n" + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("erro ao criar tabela implantacao.log_atualizacao: \n\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    public void salvarLogAtualizacao(List<LogAtualizacaoVO> logPrecos) throws Exception {
        ProgressBar.setStatus("Salvando Log Atualização: " + logPrecos.size());
        ProgressBar.setMaximum(logPrecos.size());
        for (LogAtualizacaoVO logPrecoVO : logPrecos) {
            try (Statement stm = Conexao.createStatement()) {
                SQLBuilder sql = new SQLBuilder();
                sql.setTableName("log_atualizacao");
                sql.setSchema("implantacao");
                sql.put("impid", logPrecoVO.getImpId());
                sql.put("impsistema", logPrecoVO.getImpSistema());
                sql.put("imploja", logPrecoVO.getImpLoja());
                sql.put("descricao", Utils.acertarTexto(logPrecoVO.getDescricao()));
                sql.put("codigoatual", logPrecoVO.getCoigoatual());
                sql.put("preco", logPrecoVO.getPreco());
                sql.put("estoque", logPrecoVO.getEstoque());
                sql.put("custocomimposto", logPrecoVO.getCustoComImposto());
                sql.put("custosemimposto", logPrecoVO.getCustoSemImposto());
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

    public void deletarLogAtualizacao(String sistema, String loja) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute("delete from implantacao.log_atualizacao "
                    + "where impsistema = '" + sistema + "' and imploja = '" + loja + "'");
        }
    }

}
