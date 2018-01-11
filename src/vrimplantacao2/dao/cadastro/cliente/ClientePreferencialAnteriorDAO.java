package vrimplantacao2.dao.cadastro.cliente;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;
import vrframework.classe.Conexao;
import vrimplantacao.utils.Utils;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.utils.sql.SQLUtils;
import vrimplantacao2.vo.cadastro.cliente.ClientePreferencialAnteriorVO;
import vrimplantacao2.vo.cadastro.cliente.ClientePreferencialVO;

/**
 * DAO do cliente preferencial anterior.
 * @author Leandro
 */
public class ClientePreferencialAnteriorDAO {
    
    public void createTable() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute(
                    "do $$\n" +
                    "declare\n" +
                    "begin\n" +
                    "	if not exists(select table_name from information_schema.tables where table_schema = 'implantacao' and table_name = 'codant_clientepreferencial') then\n" +
                    "		create table implantacao.codant_clientepreferencial (\n" +
                    "			sistema varchar not null,\n" +
                    "			loja varchar not null,\n" +
                    "			id varchar not null,\n" +
                    "			codigoatual integer,\n" +
                    "			cnpj varchar,\n" +
                    "			ie varchar,\n" +
                    "			nome varchar,\n" +
                    "			forcargravacao boolean not null default false,\n" +
                    "			primary key (sistema, loja, id)\n" +
                    "		);\n" +
                    "	end if;\n" +
                    "end;\n" +
                    "$$;"
            );
        }
    }

    public void salvar(ClientePreferencialAnteriorVO anterior) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            sql.setTableName("codant_clientepreferencial");
            sql.setSchema("implantacao");            
            sql.put("sistema", anterior.getSistema());
            sql.put("loja", anterior.getLoja());
            sql.put("id", anterior.getId());
            if (anterior.getCodigoAtual() != null) {
                sql.put("codigoAtual", anterior.getCodigoAtual().getId());
            }
            sql.put("cnpj", anterior.getCnpj());
            sql.put("ie", anterior.getIe());
            sql.put("nome", anterior.getNome());
            sql.put("forcarGravacao", anterior.isForcarGravacao());
            
            try {
                stm.execute(sql.getInsert());
            } catch (Exception e) {
                System.out.println(sql.getInsert());
                e.printStackTrace();
                throw e;
            }
        }
    }
    
    public MultiMap<String, ClientePreferencialAnteriorVO> getAnteriores(String sistema, String loja) throws Exception {
        MultiMap<String, ClientePreferencialAnteriorVO> result = new MultiMap<>();
        
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	sistema,\n" +
                    "	loja,\n" +
                    "	id,\n" +
                    "	codigoatual,\n" +
                    "	cnpj,\n" +
                    "	ie,\n" +
                    "	nome,\n" +
                    "	forcargravacao\n" +
                    "from \n" +
                    "	implantacao.codant_clientepreferencial\n" +
                    "where\n" +
                    "	sistema = " + Utils.quoteSQL(sistema) + " and\n" +
                    "	loja = " + Utils.quoteSQL(loja) + "\n" +
                    "order by\n" +
                    "	sistema,\n" +
                    "	loja,\n" +
                    "	id"
            )) {
                while (rst.next()) {
                    ClientePreferencialAnteriorVO vo = new ClientePreferencialAnteriorVO();
                    
                    vo.setSistema(rst.getString("sistema"));
                    vo.setLoja(rst.getString("loja"));
                    vo.setId(rst.getString("id"));
                    if (rst.getString("codigoatual") != null) {
                        ClientePreferencialVO cli = new ClientePreferencialVO();
                        cli.setId(rst.getInt("codigoatual"));
                        vo.setCodigoAtual(cli);
                    }
                    vo.setCnpj(rst.getString("cnpj"));
                    vo.setIe(rst.getString("ie"));
                    vo.setNome(rst.getString("nome"));
                    vo.setForcarGravacao(rst.getBoolean("forcargravacao"));

                    result.put(vo, vo.getSistema(), vo.getLoja(), vo.getId());
                }
            }
        }
        
        return result;
    }

    public Map<String, ClientePreferencialVO> getClientesAnteriores(String sistema, String loja) throws Exception {
        Map<String, ClientePreferencialVO> result = new LinkedHashMap<>();
        
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	ant.id,\n" +
                    "	ant.codigoatual,\n" +
                    "	c.nome,\n" +
                    "	c.cnpj,\n" +
                    "	c.endereco,\n" +
                    "	c.numero,\n" +
                    "	c.complemento,\n" +
                    "	c.bairro,\n" +
                    "	c.id_municipio,\n" +
                    "	c.id_estado,\n" +
                    "	c.cep\n" +
                    "from\n" +
                    "	implantacao.codant_clientepreferencial ant\n" +
                    "	join clientepreferencial c on ant.codigoatual = c.id\n" +
                    "where\n" +
                    "	sistema = " + SQLUtils.stringSQL(sistema) + " and\n" +
                    "	loja = " + SQLUtils.stringSQL(loja) + "\n" +
                    "order by\n" +
                    "	ant.id"
            )) {
                while (rst.next()) {
                    ClientePreferencialVO vo = new ClientePreferencialVO();
                    
                    vo.setId(rst.getInt("codigoatual"));
                    vo.setNome(rst.getString("nome"));
                    vo.setCnpj(rst.getLong("cnpj"));
                    vo.setEndereco(rst.getString("endereco"));
                    vo.setNumero(rst.getString("numero"));
                    vo.setComplemento(rst.getString("complemento"));
                    vo.setBairro(rst.getString("bairro"));
                    vo.setId_municipio(rst.getInt("id_municipio"));
                    vo.setId_estado(rst.getInt("id_estado"));
                    vo.setCep(rst.getInt("cep"));
                    
                    result.put(rst.getString("id"), vo);
                }
            }
        }
        
        return result;
    }
    
}
