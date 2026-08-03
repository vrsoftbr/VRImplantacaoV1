package vrimplantacao2.dao.cadastro.produtosimilar;

import java.sql.ResultSet;
import java.sql.Statement;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.cadastro.produtosimilar.ProdutoSimilarVO;
import vrimplantacao2.vo.cadastro.produtosimilar.ProdutoSimilarItemVO;

/**
 * Classe que gerencia as trasações nas tabelas produtosimilar e
 * produtosimilaritem.
 *
 * @author Wesley
 */
public class ProdutoSimilarDAO {

    /**
     * Cria a tabela produto similar no banco.
     *
     * @throws Exception
     */
    public void createProdutoSimilarTable() throws Exception {
        Conexao.createStatement().execute(
                "do $$\n"
                + "declare\n"
                + "begin\n"
                + "	if not exists(select table_name from information_schema.tables where table_schema = 'implantacao' and table_name = 'codant_produtosimilar') then\n"
                + "		create table implantacao.codant_produtosimilar (\n"
                + "			impsistema varchar,\n"
                + "			imploja varchar,\n"
                + "			impid varchar,\n"
                + "			codigoatual integer,\n"
                + "                     descricao varchar, \n"
                + "			observacaoImportacao varchar,\n"
                + "			primary key (impsistema, imploja, impid)\n"
                + "		);\n"
                + "		raise notice 'tabela criada';\n"
                + "	end if;\n"
                + "end;\n"
                + "$$;"
        );
    }

    /**
     * Cria a tabela produto similar item no banco.
     *
     * @throws Exception
     */
    public void createProdutoSimilarItemTable() throws Exception {
        Conexao.createStatement().execute(
                "do $$\n"
                + "declare\n"
                + "begin\n"
                + "	if not exists(select table_name from information_schema.tables where table_schema = 'implantacao' and table_name = 'codant_produtosimilaritem') then\n"
                + "		create table implantacao.codant_produtosimilaritem (\n"
                + "			impsistema varchar, \n"
                + "			imploja varchar, \n"
                + "			impid varchar,\n"
                + "			codigoatual integer,\n"
                + "			importIdProdutoSimilar varchar,\n"
                + "                     codigoAtualProdutoSimilar integer, \n"
                + "			importIdProduto varchar,\n"
                + "			codigoAtualProduto integer,\n"
                + "			observacaoImportacao varchar,\n"
                + "			primary key (impsistema, imploja, impid)\n"
                + "		);\n"
                + "		raise notice 'tabela criada';\n"
                + "	end if;\n"
                + "end;\n"
                + "$$;"
        );
    }

    public void gravar(ProdutoSimilarVO vo) throws Exception {
        try (Statement stm = Conexao.createStatement()) {

            try (ResultSet rst = stm.executeQuery(
                    "SELECT COALESCE(MAX(id), 0) + 1 AS id FROM similarproduto")) {

                if (rst.next()) {
                    vo.setId(rst.getInt("id"));
                }
            }

            SQLBuilder sql = new SQLBuilder();
            sql.setTableName("similarproduto");
            sql.put("id", vo.getId());
            sql.put("descricao", vo.getDescricao());
            sql.put("id_situacaocadastro", vo.getIdSituacaoCadastro());

            stm.execute(sql.getInsert());
        }
    }

    public void gravar(ProdutoSimilarItemVO vItem) throws Exception {
        try (Statement stm = Conexao.createStatement()) {

            SQLBuilder sql = new SQLBuilder();
            sql.setTableName("similarprodutoitem");
            sql.put("id_similarproduto", vItem.getIdProdutoSimilar().getId());
            sql.put("id_produto", vItem.getIdProduto());
            sql.getReturning().add("id");

            try (ResultSet rst = stm.executeQuery(sql.getInsert())) {
                if (rst.next()) {
                    vItem.setId(rst.getInt("id"));
                }
            }
        }
    }
}
