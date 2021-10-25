package vrimplantacao2.dao.cadastro.cliente;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;
import vrframework.classe.Conexao;
import vrimplantacao.utils.Utils;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.cadastro.cliente.ClienteEventualAnteriorVO;
import vrimplantacao2.vo.cadastro.cliente.ClienteEventualVO;

/**
 * DAO do cliente preferencial anterior.
 * @author Leandro
 */
public class ClienteEventualAnteriorDAO {
    
    private static final Logger LOG = Logger.getLogger(ClienteEventualAnteriorDAO.class.getName());
    
    public void createTable() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute(
                    "do $$\n" +
                    "declare\n" +
                    "begin\n" +
                    "	if not exists(select table_name from information_schema.tables where table_schema = 'implantacao' and table_name = 'codant_clienteeventual') then\n" +
                    "		create table implantacao.codant_clienteeventual(\n" +
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

    public void salvar(ClienteEventualAnteriorVO anterior) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            sql.setTableName("codant_clienteeventual");
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
            
            stm.execute(sql.getInsert());
        }
    }
    
    public Map<String, ClienteEventualVO> getAnterior(String sistema, String loja) throws Exception {
        Map<String, ClienteEventualVO> result = new LinkedHashMap<>();
        
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	ant.id,\n" +
                    "	ant.codigoatual,\n" +
                    "	c.nome, \n" +
                    "	c.cnpj, \n" +
                    "	c.endereco, \n" +
                    "	c.numero, \n" +
                    "	c.complemento, \n" +
                    "	c.bairro, \n" +
                    "	c.id_municipio, \n" +
                    "	c.id_estado, \n" +
                    "	c.cep\n" +
                    "from\n" +
                    "	implantacao.codant_clienteeventual ant\n" +
                    "	join clienteeventual c on\n" +
                    "		ant.codigoatual = c.id\n" +
                    "where\n" +
                    "	ant.sistema = " + Utils.quoteSQL(sistema) + "\n" +
                    "	and ant.loja = " + Utils.quoteSQL(loja) + "\n" +
                    "order by\n" +
                    "	ant.id"                  
            )) {
                while (rst.next()) {
                    
                    ClienteEventualVO vo = new ClienteEventualVO();
                    
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

    @Deprecated
    public MultiMap<String, ClienteEventualAnteriorVO> getAnteriores(String sistema, String loja) throws Exception {
        MultiMap<String, ClienteEventualAnteriorVO> result = new MultiMap<>();
        
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
                    "	implantacao.codant_clienteeventual\n" +
                    "where\n" +
                    "	sistema = " + Utils.quoteSQL(sistema) + " and\n" +
                    "	loja = " + Utils.quoteSQL(loja) + "\n" +
                    "order by\n" +
                    "	sistema,\n" +
                    "	loja,\n" +
                    "	id"
            )) {
                while (rst.next()) {
                    ClienteEventualAnteriorVO vo = new ClienteEventualAnteriorVO();
                    
                    vo.setSistema(rst.getString("sistema"));
                    vo.setLoja(rst.getString("loja"));
                    vo.setId(rst.getString("id"));
                    if (rst.getString("codigoatual") != null) {
                        ClienteEventualVO cli = new ClienteEventualVO();
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

    public Map<String, Integer> getClientesImportador(String sistema, String loja) throws Exception {
        Map<String, Integer> result = new HashMap<>();
        
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	ant.id,\n" +
                    "	ant.codigoatual\n" +
                    "from\n" +
                    "	implantacao.codant_contareceber ant\n" +
                    "	join receberoutrasreceitas r on\n" +
                    "		ant.codigoatual = r.id\n" +
                    "order by\n" +
                    "	1,2"
            )) {
                while (rst.next()) {
                    result.put(rst.getString("id"), rst.getInt("codigoatual"));
                }
            }
        }
        
        return result;
    }

    public Integer getByIdAnterior(String sistema, String loja, String id) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select codigoatual from implantacao.codant_clienteeventual where \n" +
                    "	sistema = '" + sistema + "' and\n" +
                    "	loja = '" + loja + "' and\n" +
                    "	id = '" + id + "'"
            )) {
                if (rst.next()) {
                    return rst.getInt("codigoatual");
                }
            }
        }
        return null;
    }

    public Map<String, Integer> getClientesEventuaisImportados(String sistema, String lojaOrigem) throws Exception {
        Map<String, Integer> result = new HashMap<>();
        
        try (Statement stm = Conexao.createStatement()) {
            String sql = "select id, codigoatual from implantacao.codant_clienteeventual where \n" +
                    "	sistema = '" + sistema + "' and\n" +
                    "	loja = '" + lojaOrigem + "' and\n" +
                    "	not codigoatual is null";
            LOG.fine(sql);
            try (ResultSet rst = stm.executeQuery(sql)) {
                while (rst.next()) {
                    result.put(rst.getString("id"), rst.getInt("codigoatual"));
                }
            }
        }
        LOG.fine("Qte cliente eventual:" + result.size());
        return result;
    }
    
    
    
}
