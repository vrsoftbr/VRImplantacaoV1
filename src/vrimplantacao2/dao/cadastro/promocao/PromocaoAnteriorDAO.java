package vrimplantacao2.dao.cadastro.promocao;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.utils.sql.SQLUtils;
import vrimplantacao2.vo.cadastro.pdv.promocao.PromocaoAnteriorVO;
import vrimplantacao2.vo.cadastro.pdv.promocao.PromocaoVO;
import vrimplantacao2.vo.importacao.PromocaoIMP;

/**
 * DAO de promocao anterior.
 *
 * @author Michael
 */
public class PromocaoAnteriorDAO {

    private static final Logger LOG = Logger.getLogger(PromocaoAnteriorDAO.class.getName());

    public void createTable() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute(
                    "do $$\n"
                    + "declare\n"
                    + "begin\n"
                    + "	if not exists(select table_name from information_schema.tables where table_schema = 'implantacao' and table_name = 'codant_promocao') then\n"
                    + "CREATE TABLE implantacao.codant_promocao (\n"
                    + "	sistema varchar NOT NULL,\n"
                    + "	loja varchar NOT NULL,\n"
                    + "	codigoatual serial not NULL,\n"
                    + "	id_conexao int NOT NULL,\n"
                    + "	id_promocao varchar NULL,\n"
                    + "	descricao varchar NULL,\n"
                    + "	datainicio date NULL,\n"
                    + "	datatermino date NULL,\n"
                    + "	ean varchar NULL,\n"
                    + "	id_produto varchar NULL,\n"
                    + "	descricaocompleta varchar NULL,\n"
                    + "	quantidade varchar NULL,\n"
                    + "	paga varchar NULL\n"
                    + ");\n"
                    + "	end if;\n"
                    + "end;\n"
                    + "$$;"
            );
        }
    }

    public void salvar(PromocaoAnteriorVO anterior) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            sql.setTableName("codant_promocao");
            sql.setSchema("implantacao");
            sql.put("sistema", anterior.getSistema());
            sql.put("loja", anterior.getLoja());
            sql.put("id_promocao", anterior.getId_promocao());
            sql.put("id_produto", anterior.getId_produto());
            sql.put("descricao", anterior.getDescricao());
            sql.put("datainicio", anterior.getDataInicio());
            sql.put("datatermino", anterior.getDataTermino());
            sql.put("ean", anterior.getEan());
            sql.put("descricaocompleta", anterior.getDescricaoCompleta());
            sql.put("quantidade", anterior.getQuantidade());
            sql.put("paga", anterior.getPaga());
            sql.put("id_conexao", anterior.getIdConexao());

            try {
                stm.execute(sql.getInsert());
            } catch (Exception e) {
                System.out.println(sql.getInsert());
                e.printStackTrace();
                throw e;
            }
        }
    }

    public int getConexaoMigrada(int idConexao, String sistema) throws Exception {
        int conexao = 0;

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select id from implantacao2_5.conexao limit 1")) {
                if (rs.next()) {
                    conexao = rs.getInt("id");
                }
            }
        }

        return conexao;
    }

    public int verificaRegistro() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select count(*) qtd from implantacao.codant_promocao limit 100")) {
                if (rs.next()) {
                    return rs.getInt("qtd");
                }
            }
        }

        return 0;
    }

    public boolean verificaMigracaoMultiloja(String lojaOrigem, String sistema, int idConexao) throws Exception {
        boolean conexao = false;

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	id_conexao \n"
                    + "from \n"
                    + "	implantacao.codant_promocao\n"
                    + "where \n"
                    + "	sistema = " + SQLUtils.stringSQL(sistema) + " and\n"
                    + "   id_conexao = " + idConexao + " limit 1")) {
                if (rs.next()) {
                    conexao = true;
                }
            }
        }

        return conexao;
    }

    public String getLojaModelo(int idConexao, String sistema) throws Exception {
        String loja = "";

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	loja\n"
                    + "from \n"
                    + "	implantacao.codant_promocao \n"
                    + "where \n"
                    + "	sistema = '" + sistema + "' and \n"
                    + "	id_conexao = " + idConexao + "\n"
                    + "limit 1")) {
                if (rs.next()) {
                    loja = rs.getString("loja");
                }
            }
        }

        return loja;
    }

    public boolean verificaMultilojaMigrada(String lojaOrigem, String sistema, int idConexao) throws Exception {
        boolean lojaJaMigrada = false;

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	id_conexao \n"
                    + "from \n"
                    + "	implantacao.codant_promocao\n"
                    + "where \n"
                    + "	sistema = " + SQLUtils.stringSQL(sistema) + " and\n"
                    + "   loja = " + SQLUtils.stringSQL(lojaOrigem) + " and\n"
                    + "   id_conexao = " + idConexao + " limit 1")) {
                if (rs.next()) {
                    lojaJaMigrada = true;
                }
            }
        }

        return lojaJaMigrada;
    }

    MultiMap<String, PromocaoAnteriorVO> getAnteriores(String sistema, String lojaOrigem, int conexao) throws Exception {
        MultiMap<String, PromocaoAnteriorVO> result = new MultiMap<>();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	ant.sistema,\n"
                    + "	ant.loja,\n"
                    + "	ant.id_conexao,\n"
                    + "	ant.id_promocao,\n"
                    + " ant.codigoatual, \n"
                    + "	ant.descricao,\n"
                    + "	ant.datainicio,\n"
                    + "	ant.datatermino,\n"
                    + "	ant.ean,\n"
                    + "	p.codigoatual id_produto,\n"
                    + "	ant.descricaocompleta,\n"
                    + "	ant.quantidade,\n"
                    + "	ant.paga\n"
                    + "from \n"
                    + "	implantacao.codant_promocao ant\n"
                    + "join implantacao.codant_produto p on p.impid = ant.id_produto "
                    + "where\n"
                    + "	ant.sistema = " + SQLUtils.stringSQL(sistema) + " and\n"
                    + "	ant.loja = " + SQLUtils.stringSQL(lojaOrigem) + " and\n"
                    + " ant.id_conexao = " + conexao + "\n"
                    + "order by\n"
                    + "	ant.sistema,\n"
                    + "	ant.loja,"
                    + " ant.id_promocao"
            )) {
                while (rst.next()) {
                    PromocaoAnteriorVO vo = new PromocaoAnteriorVO();
                    vo.setSistema(rst.getString("sistema"));
                    vo.setLoja(rst.getString("loja"));
                    vo.setIdConexao(rst.getInt("id_conexao"));
                    vo.setId_promocao(rst.getString("id_promocao"));
                    vo.setDescricao(rst.getString("descricao"));
                    vo.setDataInicio(rst.getDate("datainicio"));
                    vo.setDataTermino(rst.getDate("datatermino"));
                    vo.setEan(rst.getString("ean"));
                    vo.setId_produto(rst.getString("id_produto"));
                    vo.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    vo.setQuantidade(rst.getDouble("quantidade"));
                    vo.setPaga(rst.getDouble("paga"));
                    if (rst.getString("codigoatual") != null) {
                        PromocaoVO atual = new PromocaoVO();
                        atual.setId(rst.getString("codigoatual"));
                        vo.setCodigoatual(atual);
                    }
                    result.put(
                            vo,
                            vo.getSistema(),
                            vo.getLoja(),
                            String.valueOf(vo.getCodigoAtual())
                    );
                }
            }
        }
        return result;
    }

    public List<PromocaoIMP> getPromocaoItens(String sistema) throws Exception {
        List<PromocaoIMP> Result = new ArrayList<>();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "distinct \n"
                    + "	p.id_promocao,\n"
                    + "	imp.codigoatual id_produto,\n"
                    + "	p.paga\n"
                    + "from\n"
                    + "	implantacao.codant_promocao p\n"
                    + "inner join implantacao.codant_produto imp on\n"
                    + "	p.id_produto = imp.impid\n"
                    + "where p.sistema = " + SQLUtils.stringSQL(sistema) + " \n"
                    + "order by 2"
            )) {
                while (rst.next()) {
                    PromocaoIMP imp = new PromocaoIMP();
                    imp.setId_promocao(rst.getString("id_promocao"));
                    imp.setId_produto(rst.getString("id_produto"));
                    imp.setPaga(rst.getDouble("paga"));
                    Result.add(imp);
                }
            }
        }
        return Result;
    }

    List<PromocaoIMP> getCodigoAtual(String sistema) throws Exception {
        List<PromocaoIMP> Result = new ArrayList<>();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select codigoatual from implantacao.codant_promocao \n"
                    + "where sistema = " + SQLUtils.stringSQL(sistema) + " \n"
                    + "order by 2"
            )) {
                while (rst.next()) {
                    PromocaoIMP imp = new PromocaoIMP();
                    imp.setId(rst.getString("codigoatual"));
                    Result.add(imp);
                }
            }
        }
        return Result;
    }

}
