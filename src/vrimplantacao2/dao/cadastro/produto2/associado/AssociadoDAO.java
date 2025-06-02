package vrimplantacao2.dao.cadastro.produto2.associado;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.cadastro.associado.AssociadoItemVO;
import vrimplantacao2.vo.cadastro.associado.AssociadoVO;

/**
 * Classe que gerencia as trasações nas tabelas associado e associadoitem.
 *
 * @author Leandro
 */
public class AssociadoDAO {

    /**
     * Cria a tabela associado no banco.
     *
     * @throws Exception
     */
    public void createAssociadoTable() throws Exception {
        Conexao.createStatement().execute(
                "do $$\n"
                + "declare\n"
                + "begin\n"
                + "	if not exists(select table_name from information_schema.tables where table_schema = 'implantacao' and table_name = 'codant_associado') then\n"
                + "		create table implantacao.codant_associado (\n"
                + "			impsistema varchar,\n"
                + "			imploja varchar,\n"
                + "			impid varchar,\n"
                + "			codigoatual integer,\n"
                + "                     importIdProduto varchar,\n"
                + "                     codigoAtualProdutoAssociado integer,\n"
                + "			qtdEmbalagem integer,\n"
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
     * Cria a tabela associado item no banco.
     *
     * @throws Exception
     */
    public void createAssociadoItemTable() throws Exception {
        Conexao.createStatement().execute(
                "do $$\n"
                + "declare\n"
                + "begin\n"
                + "	if not exists(select table_name from information_schema.tables where table_schema = 'implantacao' and table_name = 'codant_associadoitem') then\n"
                + "		create table implantacao.codant_associadoitem (\n"
                + "			impsistema varchar, \n"
                + "			imploja varchar, \n"
                + "			impid varchar,\n"
                + "			codigoatual integer,\n"
                + "			importIdAssociado varchar,\n"
                + "                     codigoAtualAssociado integer, \n"
                + "			importIdProdutoAssociadoItem varchar,\n"
                + "			codigoAtualProdutoAssociadoItem integer,\n"
                + "			qtdEmbalagemItem integer,\n"
                + "                     percentualPreco double precision,\n"
                + "                     aplicaPreco boolean,\n"
                + "                     aplicaCusto boolean,\n"
                + "                     aplicaEstoque boolean,\n"
                + "                     percentualcustoestoque double precision,\n"
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
     * Retorna uma {@link Map} com todos os associados cadastrados no sistema.
     *
     * @return
     * @throws Exception
     */
    public Map<Integer, AssociadoVO> getAssociadosExistentes() throws Exception {
        Map<Integer, AssociadoVO> result = new HashMap<>();

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	id,\n"
                    + "	id_produto\n"
                    + "from\n"
                    + "	associado\n"
                    + "order by\n"
                    + "	id_produto"
            )) {
                while (rst.next()) {
                    AssociadoVO vo = new AssociadoVO();
                    vo.setId(rst.getInt("id"));
                    vo.setIdProduto(rst.getInt("id_produto"));
                    result.put(vo.getIdProduto(), vo);
                }
            }

            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	i.id,\n"
                    + "	a.id_produto id_produto_pai,\n"
                    + "	i.id_produto,\n"
                    + "	i.percentualpreco,\n"
                    + "	i.aplicapreco,\n"
                    + "	i.aplicacusto,\n"
                    + "	i.aplicaestoque,\n"
                    + "	i.percentualcustoestoque\n"
                    + "from\n"
                    + "	associadoitem i\n"
                    + "	join associado a on\n"
                    + "		i.id_associado = a.id\n"
                    + "order by\n"
                    + "	id_produto"
            )) {
                while (rst.next()) {
                    AssociadoVO pai = result.get(rst.getInt("id_produto_pai"));
                    AssociadoItemVO item = new AssociadoItemVO();

                    item.setId(rst.getInt("id"));
                    item.setIdAssociado(pai.getId());
                    item.setIdProduto(rst.getInt("id_produto"));
                    item.setPercentualPreco(rst.getDouble("percentualpreco"));
                    item.setAplicaPreco(rst.getBoolean("aplicapreco"));
                    item.setAplicaCusto(rst.getBoolean("aplicacusto"));
                    item.setAplicaEstoque(rst.getBoolean("aplicaestoque"));
                    item.setPercentualCustoEstoque(rst.getDouble("percentualcustoestoque"));

                    pai.getItens().put(item.getIdProduto(), item);
                }
            }
        }

        return result;
    }

    public void gravar(AssociadoVO vo) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();

            sql.setTableName("associado");
            sql.put("id_produto", vo.getIdProduto());
            sql.put("qtdembalagem", vo.getQtdEmbalagem());
            sql.getReturning().add("id");
            try (ResultSet rst = stm.executeQuery(
                    sql.getInsert()
            )) {
                while (rst.next()) {
                    vo.setId(rst.getInt("id"));
                }
            }
        }
    }

    public void gravar(AssociadoItemVO vItem) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();

            sql.setTableName("associadoitem");
            sql.put("id_associado", vItem.getIdAssociado());
            sql.put("id_produto", vItem.getIdProduto());
            sql.put("qtdembalagem", vItem.getQtdEmbalagem());
            sql.put("percentualpreco", vItem.getPercentualPreco());
            sql.put("aplicapreco", vItem.isAplicaPreco());
            sql.put("aplicacusto", vItem.isAplicaCusto());
            sql.put("aplicaestoque", vItem.isAplicaEstoque());
            sql.put("percentualcustoestoque", vItem.getPercentualCustoEstoque());

            stm.execute(sql.getInsert());
        }
    }

    public Set<Integer> getProdutosAtivos(int lojaVR) throws Exception {
        Set<Integer> result = new HashSet<>();

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select id_produto from produtocomplemento where id_loja = " + lojaVR + " and id_situacaocadastro = 1"
            )) {
                while (rst.next()) {
                    result.add(rst.getInt("id_produto"));
                }
            }
        }

        return result;
    }

}
