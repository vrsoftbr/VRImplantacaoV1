package vrimplantacao2.dao.cadastro.mercadologico;

import java.sql.Statement;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.cadastro.mercadologico.MercadologicoAnteriorVO;

/**
 *
 * @author Leandro
 */
public class MercadologicoAnteriorDAO {
    
    /**
     * Cria a tabela no banco de dados caso ela não exista.
     * @throws Exception 
     */
    public void createTable() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute(
                "do $$\n" +
                "declare\n" +
                "begin\n" +
                "	if not exists(select table_name from information_schema.tables where table_schema = 'implantacao' and table_name = 'codant_mercadologico') then\n" +
                "		create table implantacao.codant_mercadologico (\n" +
                "			imp_sistema varchar not null,\n" +
                "			imp_loja varchar not null,\n" +
                "			ant_merc1 varchar not null,\n" +
                "			ant_merc2 varchar not null,\n" +
                "			ant_merc3 varchar not null,\n" +
                "			ant_merc4 varchar not null,\n" +
                "			ant_merc5 varchar not null,\n" +
                "			merc1 integer,\n" +
                "			merc2 integer,\n" +
                "			merc3 integer,\n" +
                "			merc4 integer,\n" +
                "			merc5 integer,\n" +
                "			descricao varchar,\n" +
                "		        nivel integer,\n" +
                "			primary key (imp_sistema, imp_loja, ant_merc1, ant_merc2, ant_merc3, ant_merc4, ant_merc5)\n" +
                "		);\n" +
                "		raise notice 'tabela criada';\n" +
                "	end if;\n" +
                "end;\n" +
                "$$;"
            );                    
        }
    }

    public void salvar(MercadologicoAnteriorVO vo) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            sql.setTableName("codant_mercadologico");
            sql.setSchema("implantacao");
            sql.put("imp_sistema", vo.getSistema());
            sql.put("imp_loja", vo.getLoja());
            sql.put("ant_merc1", vo.getAntMerc1());
            sql.put("ant_merc2", vo.getAntMerc2());
            sql.put("ant_merc3", vo.getAntMerc3());
            sql.put("ant_merc4", vo.getAntMerc4());
            sql.put("ant_merc5", vo.getAntMerc5());
            sql.put("merc1", vo.getMerc1());
            sql.put("merc2", vo.getMerc2());
            sql.put("merc3", vo.getMerc3());
            sql.put("merc4", vo.getMerc4());
            sql.put("merc5", vo.getMerc5());
            sql.put("descricao", vo.getDescricao());
            sql.put("nivel", vo.getNivel());
            
            stm.execute(sql.getInsert());
        }
    }
    
}
