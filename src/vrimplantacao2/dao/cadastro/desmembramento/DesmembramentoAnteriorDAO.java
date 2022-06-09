package vrimplantacao2.dao.cadastro.desmembramento;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.openide.util.Exceptions;
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
                    + "	impid varchar not null,\n"
                    + "	codigoatual serial not null,\n"
                    + "	produtopai varchar not null,\n"
                    + "	produtofilho varchar null,\n"
                    + " percentual varchar not null,\n"
                    + "	id_conexao int not null,\n"
                    + " primary key (sistema, loja, produtopai,produtofilho) \n"
                    + ");\n"
                    + "	end if;\n"
                    + "end;\n"
                    + "$$;"
            );
        }
    }

    List<DesmembramentoIMP> getCodigoAtual(String sistema) throws Exception {
        List<DesmembramentoIMP> Result = new ArrayList<>();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select codigoatual from implantacao.codant_desmembramento \n"
                    + "where sistema = " + SQLUtils.stringSQL(sistema) + " \n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    DesmembramentoIMP imp = new DesmembramentoIMP();
                    imp.setProdutoPai(rst.getString("codigoatual"));

                    Result.add(imp);
                }
            }
        }
        return Result;
    }

    public void salvar(DesmembramentoAnteriorVO anterior) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            sql.setTableName("codant_desmembramento");
            sql.setSchema("implantacao");
            sql.put("sistema", anterior.getSistema());
            sql.put("loja", anterior.getLoja());
            sql.put("impid", anterior.getId());
            sql.put("produtopai", anterior.getProdutoPai());
            sql.put("produtofilho", anterior.getProdutoFilho());
            sql.put("percentual", anterior.getPercentual());
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
}
