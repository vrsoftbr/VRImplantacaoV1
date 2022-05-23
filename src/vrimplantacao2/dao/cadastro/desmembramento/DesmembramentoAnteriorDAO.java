package vrimplantacao2.dao.cadastro.desmembramento;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.utils.sql.SQLUtils;
import vrimplantacao2.vo.cadastro.desmembramento.DesmembramentoAnteriorVO;
import vrimplantacao2.vo.cadastro.desmembramento.DesmembramentoVO;
import vrimplantacao2.vo.importacao.DesmembramentoIMP;

public class DesmembramentoAnteriorDAO {

    private static final Logger LOG = Logger.getLogger(DesmembramentoAnteriorDAO.class.getName());

    public void createTable() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute(
                    "do $$\n"
                    + "declare\n"
                    + "begin\n"
                    + "	if not exists(select table_name from information_schema.tables where table_schema = 'implantacao' and table_name = 'codant_desmembramento') then\n"
                    + "CREATE TABLE implantacao.codant_desmembramento (\n"
                    + "	sistema varchar not null,\n"
                    + "	loja varchar not null,\n"
                    + "	importId varchar not null,\n"
                    + "	codigoatual serial not null,\n"
                    + "	produto varchar not null,\n"
                    + "	descricao varchar null,\n"
                    + "	id_conexao int not null \n"
                    + ");\n"
                    + "	end if;\n"
                    + "end;\n"
                    + "$$;"
            );
        }
    }

    public void salvar(DesmembramentoAnteriorVO anterior) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            sql.setTableName("codant_desmembramento");
            sql.setSchema("implantacao");
            sql.put("sistema", anterior.getSistema());
            sql.put("loja", anterior.getLoja());
            sql.put("importid", anterior.getImportId());
            sql.put("produto", anterior.getProduto());
            sql.put("descricao", anterior.getDescricao());
            sql.put("quantidade", anterior.getQuantidade());
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
                    "select count(*) qtd from implantacao.codant_desmembramento limit 100")) {
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
                    + "	implantacao.codant_desmembramento \n"
                    + "where \n"
                    + "	sistema = " + SQLUtils.stringSQL(sistema) + " and \n"
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
                    + "	loja \n"
                    + "from \n"
                    + "	implantacao.codant_desmembramento \n"
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
    
    MultiMap<String, DesmembramentoAnteriorVO> getAnteriores(String sistema, String lojaOrigem, int conexao) throws Exception {
        MultiMap<String, DesmembramentoAnteriorVO> result = new MultiMap<>();
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
                    DesmembramentoAnteriorVO vo = new DesmembramentoAnteriorVO();
                    vo.setSistema(rst.getString("sistema"));
                    vo.setLoja(rst.getString("loja"));
                    vo.setIdConexao(rst.getInt("id_conexao"));
                    vo.setDescricao(rst.getString("descricao"));
                    vo.setProduto(rst.getString("id_produto"));
                    vo.setDescricao(rst.getString("descricaocompleta"));
                    vo.setQuantidade(rst.getDouble("quantidade"));

                    if (rst.getString("codigoatual") != null) {
                        DesmembramentoVO atual = new DesmembramentoVO();
                        atual.setId(rst.getString("codigoatual"));
                        vo.setCodigoAtual(atual);
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

    public List<DesmembramentoIMP> getDesmembramentoItens(String sistema) throws Exception {
        List<DesmembramentoIMP> Result = new ArrayList<>();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "distinct \n"
                    + "	p.id_promocao,\n"
                    + "	imp.codigoatual id_produto,\n"
                    + "	p.paga\n"
                    + "from\n"
                    + "	implantacao.codant_desmembramento d\n"
                    + "inner join implantacao.codant_produto imp on\n"
                    + "	d.id_produto = imp.impid\n"
                    + "where p.sistema = " + SQLUtils.stringSQL(sistema) + " \n"
                    + "order by 2"
            )) {
                while (rst.next()) {
                    DesmembramentoIMP imp = new DesmembramentoIMP();
                    imp.setId(rst.getString("id_promocao"));
                    imp.setId(rst.getString("id_produto"));

                    Result.add(imp);
                }
            }
        }
        return Result;
    }

    List<DesmembramentoIMP> getCodigoAtual(String sistema) throws Exception {
        List<DesmembramentoIMP> Result = new ArrayList<>();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select codigoatual from implantacao.codant_promocao \n"
                    + "where sistema = " + SQLUtils.stringSQL(sistema) + " \n"
                    + "order by 2"
            )) {
                while (rst.next()) {
                    DesmembramentoIMP imp = new DesmembramentoIMP();
                    imp.setId(rst.getString("codigoatual"));
                    
                    Result.add(imp);
                }
            }
        }
        return Result;
    }

}
