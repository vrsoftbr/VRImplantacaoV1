package vrimplantacao2.dao.cadastro.kit;

import java.sql.ResultSet;
import java.sql.Statement;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.cadastro.kit.KitVO;
import vrimplantacao2.vo.cadastro.kit.KitItemVO;

/**
 * Classe que gerencia as trasações nas tabelas kit e kititem.
 *
 * @author Wesley
 */
public class KitDAO {

    /**
     * Cria a tabela de kits no banco.
     *
     * @throws Exception
     */
    public void createKitTable() throws Exception {
        Conexao.createStatement().execute(
                "do $$\n"
                + "declare\n"
                + "begin\n"
                + "	if not exists(select table_name from information_schema.tables where table_schema = 'implantacao' and table_name = 'codant_kit') then\n"
                + "		create table implantacao.codant_kit (\n"
                + "			impsistema varchar,\n"
                + "			imploja varchar,\n"
                + "			impid varchar,\n"
                + "			codigoatual integer,\n"
                + "                     importIdProduto varchar,\n"
                + "                     codigoAtualProdutoKit integer,\n"
                + "			observacaoImportacao varchar,\n"
                + "			primary key (impsistema, imploja, impid)"
                + "		);\n"
                + "		raise notice 'tabela criada';\n"
                + "	end if;\n"
                + "end;\n"
                + "$$;"
        );
    }

    /**
     * Cria a tabela kit item no banco.
     *
     * @throws Exception
     */
    public void createKitItemTable() throws Exception {
        Conexao.createStatement().execute(
                "do $$\n"
                + "declare\n"
                + "begin\n"
                + "	if not exists(select table_name from information_schema.tables where table_schema = 'implantacao' and table_name = 'codant_kititem') then\n"
                + "		create table implantacao.codant_kititem (\n"
                + "			impsistema varchar, \n"
                + "			imploja varchar, \n"
                + "			impid varchar,\n"
                + "			codigoatual integer,\n"
                + "			importIdKit varchar,\n"
                + "                     codigoAtualKit integer, \n"
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

    public void gravar(KitVO vo) throws Exception {
        try (Statement stm = Conexao.createStatement()) {

            SQLBuilder sql = new SQLBuilder();
            sql.setTableName("kit");

            sql.put("id_produto", vo.getIdProduto());
            sql.put("preconormal", vo.isPrecoNormal());

            String insert = sql.getInsert() + " RETURNING id";

            try (ResultSet rs = stm.executeQuery(insert)) {
                if (rs.next()) {
                    vo.setId(rs.getInt("id"));
                }
            }
        }
    }

    public void gravar(KitItemVO vo) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            sql.setTableName("kititem");

            sql.put("id_kit", vo.getKit().getId());
            sql.put("id_produto", vo.getIdProduto());
            sql.put("precovenda", vo.getPrecoVenda());
            sql.put("quantidade", vo.getQuantidade());

            String insert = sql.getInsert() + " RETURNING id";

            try (ResultSet rs = stm.executeQuery(insert)) {
                if (rs.next()) {
                    vo.setId(rs.getInt("id"));
                }
            }
        }
    }

    public void gravarKitLoja(KitVO vo) throws Exception {
        try (Statement stm = Conexao.createStatement()) {

            SQLBuilder sql = new SQLBuilder();
            sql.setTableName("kitloja");

            sql.put("id_kit", vo.getId());
            sql.put("id_loja", 1);
            sql.put("utiliza", true);

            stm.execute(sql.getInsert());
        }
    }
}
