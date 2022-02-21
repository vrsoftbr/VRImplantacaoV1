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
import vrimplantacao2.utils.sql.SQLUtils;
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
            
            sql.put("id_conexao", anterior.getIdConexao());
            
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
    
    public int getConexaoMigrada(int idConexao, String sistema) throws Exception {
        int conexao = 0;
        
        try(Statement stm = Conexao.createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	id_conexao \n" +
                    "from \n" +
                    "	implantacao.codant_clienteeventual\n" +
                    "where \n" +
                    "	sistema = " + SQLUtils.stringSQL(sistema) + " and\n" +
                    "   id_conexao = " + idConexao + " limit 1")) {
                if (rs.next()) {
                    conexao = rs.getInt("id_conexao");
                }
            }
        }
        
        return conexao;
    }
    
    public int verificaRegistro() throws Exception {
        try(Statement stm = Conexao.createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	count(*) qtd \n" +
                    "from \n" +
                    "	implantacao.codant_clienteeventual \n" +
                    "where \n" +
                    "	codigoatual in (select codigoatual from implantacao.codant_clientepreferencial limit 100)")) {
                if (rs.next()) {
                    return rs.getInt("qtd");
                }
            }
        }
        
        return 0;
    }
    
    public boolean verificaMigracaoMultiloja(String lojaOrigem, String sistema, int idConexao) throws Exception {
        boolean conexao = false;
        
        try(Statement stm = Conexao.createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	id_conexao \n" +
                    "from \n" +
                    "	implantacao.codant_clienteeventual\n" +
                    "where \n" +
                    "	sistema = " + SQLUtils.stringSQL(sistema) + " and\n" +
                    "   id_conexao = " + idConexao + " limit 1")) {
                if (rs.next()) {
                    conexao = true;
                }
            }
        }
        
        return conexao;
    }
    
    public void copiarCodantClienteEventual(String sistema, String lojaModelo, String lojaNova) throws Exception {
        
        String sql = 
                "create temp table implantacao_lojas (sistema varchar, loja_modelo varchar, loja_nova varchar) on commit drop;\n" +
                "insert into implantacao_lojas values ('" + sistema + "', '" + lojaModelo + "', '" + lojaNova + "');\n" +
                "\n" +
                "do $$\n" +
                "declare\n" +
                "	r record;\n" +
                "begin\n" +
                "	for r in select * from implantacao_lojas\n" +
                "	loop\n" +
                "		if (exists(select table_name from information_schema.tables t where t.table_name = 'codant_clientepreferencial' and t.table_schema = 'implantacao')) then\n" +
                "			insert into implantacao.codant_clientepreferencial\n" +
                "			select\n" +
                "				r.sistema,\n" +
                "				r.loja_nova,\n" +
                "				id,\n" +
                "				codigoatual,\n" +
                "				cnpj,\n" +
                "				ie,\n" +
                "				nome,\n" +
                "				false\n" +
                "			from\n" +
                "				implantacao.codant_clientepreferencial\n" +
                "			where\n" +
                "				sistema = r.sistema and\n" +
                "				loja = r.loja_modelo and\n" +
                "				id in (\n" +
                "					select\n" +
                "						id\n" +
                "					from\n" +
                "						implantacao.codant_clientepreferencial\n" +
                "					where\n" +
                "						sistema = r.sistema and \n" +
                "						loja = r.loja_modelo\n" +
                "					except\n" +
                "					select\n" +
                "						id\n" +
                "					from\n" +
                "						implantacao.codant_clientepreferencial	\n" +
                "					where\n" +
                "						sistema = r.sistema and \n" +
                "						loja = r.loja_nova\n" +
                "				);\n" +
                "		end if;\n" +
                "\n" +
                "		--Clientes eventuais\n" +
                "		if (exists(select table_name from information_schema.tables t where t.table_name = 'codant_clienteeventual' and t.table_schema = 'implantacao')) then\n" +
                "			insert into implantacao.codant_clienteeventual\n" +
                "			select\n" +
                "				r.sistema,\n" +
                "				r.loja_nova,\n" +
                "				id,\n" +
                "				codigoatual,\n" +
                "				cnpj,\n" +
                "				ie,\n" +
                "				nome,\n" +
                "				false\n" +
                "			from\n" +
                "				implantacao.codant_clienteeventual\n" +
                "			where\n" +
                "				sistema = r.sistema and\n" +
                "				loja = r.loja_modelo and\n" +
                "				id in (\n" +
                "					select\n" +
                "						id\n" +
                "					from\n" +
                "						implantacao.codant_clienteeventual\n" +
                "					where\n" +
                "						sistema = r.sistema and \n" +
                "						loja = r.loja_modelo\n" +
                "					except\n" +
                "					select\n" +
                "						id\n" +
                "					from\n" +
                "						implantacao.codant_clienteeventual	\n" +
                "					where\n" +
                "						sistema = r.sistema and \n" +
                "						loja = r.loja_nova\n" +
                "				);\n" +
                "		end if;\n" +
                "\n" +
                "		--Produtos\n" +
                "\n" +
                "		insert into implantacao.codant_produto\n" +
                "		SELECT \n" +
                "			impsistema, \n" +
                "			r.loja_nova imploja,\n" +
                "			impid, \n" +
                "			descricao, \n" +
                "			codigoatual, \n" +
                "			piscofinscredito, \n" +
                "			piscofinsdebito, \n" +
                "			piscofinsnaturezareceita, \n" +
                "			icmscst, \n" +
                "			icmsaliq, \n" +
                "			icmsreducao, \n" +
                "			estoque, \n" +
                "			e_balanca, \n" +
                "			custosemimposto, \n" +
                "			custocomimposto, \n" +
                "			margem, \n" +
                "			precovenda, \n" +
                "			ncm, \n" +
                "			cest, \n" +
                "			contadorimportacao, \n" +
                "			novo\n" +
                "		FROM \n" +
                "			implantacao.codant_produto\n" +
                "		where\n" +
                "			impsistema = r.sistema and \n" +
                "			imploja = r.loja_modelo and\n" +
                "			impid in (\n" +
                "				select\n" +
                "					impid\n" +
                "				from\n" +
                "					implantacao.codant_produto\n" +
                "				where\n" +
                "					impsistema = r.sistema and \n" +
                "					imploja = r.loja_modelo\n" +
                "				except\n" +
                "				select\n" +
                "					impid\n" +
                "				from\n" +
                "					implantacao.codant_produto	\n" +
                "				where\n" +
                "					impsistema = r.sistema and \n" +
                "					imploja = r.loja_nova\n" +
                "			)\n" +
                "		order by impid;\n" +
                "\n" +
                "\n" +
                "		insert into implantacao.codant_ean \n" +
                "		SELECT \n" +
                "			ant.importsistema,\n" +
                "			r.loja_nova imploja,\n" +
                "			ant.importid, \n" +
                "			ant.ean,\n" +
                "			ant.qtdembalagem,\n" +
                "			ant.valor,\n" +
                "			ant.tipoembalagem\n" +
                "		FROM \n" +
                "			implantacao.codant_ean ant\n" +
                "			join (\n" +
                "			select\n" +
                "				importid,\n" +
                "				ean\n" +
                "			from\n" +
                "				implantacao.codant_ean\n" +
                "			where\n" +
                "				importsistema = r.sistema and \n" +
                "				importloja = r.loja_modelo\n" +
                "			except\n" +
                "			select\n" +
                "				importid,\n" +
                "				ean\n" +
                "			from\n" +
                "				implantacao.codant_ean	\n" +
                "			where\n" +
                "				importsistema = r.sistema and \n" +
                "				importloja = r.loja_nova\n" +
                "			) a on\n" +
                "			ant.importid = a.importid and\n" +
                "			ant.ean = a.ean\n" +
                "		where   \n" +
                "			ant.importsistema = r.sistema and \n" +
                "			ant.importloja = r.loja_modelo\n" +
                "			order by ant.importid;\n" +
                "\n" +
                "		if (exists(select table_name from information_schema.tables t where t.table_name = 'codant_fornecedor' and t.table_schema = 'implantacao')) then\n" +
                "			insert into implantacao.codant_fornecedor\n" +
                "			select\n" +
                "				importsistema,\n" +
                "				r.loja_nova,\n" +
                "				importid,\n" +
                "				codigoatual,\n" +
                "				cnpj,\n" +
                "				razao,\n" +
                "				fantasia\n" +
                "			from \n" +
                "				implantacao.codant_fornecedor\n" +
                "			where\n" +
                "				importsistema = r.sistema and \n" +
                "				importloja = r.loja_modelo and\n" +
                "				importid in (\n" +
                "					select\n" +
                "						importid\n" +
                "					from\n" +
                "						implantacao.codant_fornecedor\n" +
                "					where\n" +
                "						importsistema = r.sistema and \n" +
                "						importloja = r.loja_modelo\n" +
                "					except\n" +
                "					select\n" +
                "						importid\n" +
                "					from\n" +
                "						implantacao.codant_fornecedor	\n" +
                "					where\n" +
                "						importsistema = r.sistema and \n" +
                "						importloja = r.loja_nova\n" +
                "				);\n" +
                "		end if;\n" +
                "\n" +
                "		if (exists(select table_name from information_schema.tables t where t.table_name = 'codant_convenioempresa' and t.table_schema = 'implantacao')) then\n" +
                "			insert into implantacao.codant_convenioempresa\n" +
                "			select\n" +
                "				sistema,\n" +
                "				r.loja_nova loja,\n" +
                "				id,\n" +
                "				codigoatual,\n" +
                "				cnpj,\n" +
                "				razao\n" +
                "			from \n" +
                "				implantacao.codant_convenioempresa a\n" +
                "			where\n" +
                "				a.sistema = r.sistema and\n" +
                "				a.loja = r.loja_modelo and\n" +
                "				a.id in (\n" +
                "					select\n" +
                "						id\n" +
                "					from\n" +
                "						implantacao.codant_convenioempresa\n" +
                "					where\n" +
                "						sistema = r.sistema and \n" +
                "						loja = r.loja_modelo\n" +
                "					except\n" +
                "					select\n" +
                "						id\n" +
                "					from\n" +
                "						implantacao.codant_convenioempresa	\n" +
                "					where\n" +
                "						sistema = r.sistema and \n" +
                "						loja = r.loja_nova\n" +
                "				);\n" +
                "		end if;\n" +
                "\n" +
                "		if (exists(select table_name from information_schema.tables t where t.table_name = 'codant_conveniado' and t.table_schema = 'implantacao')) then\n" +
                "			insert into implantacao.codant_conveniado\n" +
                "			select\n" +
                "				sistema,\n" +
                "				r.loja_nova loja,\n" +
                "				id,\n" +
                "				codigoatual,\n" +
                "				cnpj,\n" +
                "				razao\n" +
                "			from \n" +
                "				implantacao.codant_conveniado a\n" +
                "			where\n" +
                "				a.sistema = r.sistema and\n" +
                "				a.loja = r.loja_modelo and\n" +
                "				a.id in (\n" +
                "					select\n" +
                "						id\n" +
                "					from\n" +
                "						implantacao.codant_conveniado\n" +
                "					where\n" +
                "						sistema = r.sistema and \n" +
                "						loja = r.loja_modelo\n" +
                "					except\n" +
                "					select\n" +
                "						id\n" +
                "					from\n" +
                "						implantacao.codant_conveniado	\n" +
                "					where\n" +
                "						sistema = r.sistema and \n" +
                "						loja = r.loja_nova\n" +
                "				);\n" +
                "		end if;\n" +
                "\n" +
                "		if (exists(select table_name from information_schema.tables t where t.table_name = 'mapatributacao' and t.table_schema = 'implantacao')) then\n" +
                "			insert into implantacao.mapatributacao\n" +
                "			select\n" +
                "				sistema,\n" +
                "				r.loja_nova loja,\n" +
                "				orig_id,\n" +
                "				orig_descricao,\n" +
                "				id_aliquota,\n" +
                "				orig_cst,\n" +
                "				orig_aliquota,\n" +
                "				orig_reduzido\n" +
                "			from \n" +
                "				implantacao.mapatributacao a\n" +
                "			where\n" +
                "				a.sistema = r.sistema and\n" +
                "				a.agrupador = r.loja_modelo and\n" +
                "				a.orig_id in (\n" +
                "					select\n" +
                "						orig_id\n" +
                "					from\n" +
                "						implantacao.mapatributacao\n" +
                "					where\n" +
                "						sistema = r.sistema and \n" +
                "						agrupador = r.loja_modelo\n" +
                "					except\n" +
                "					select\n" +
                "						orig_id\n" +
                "					from\n" +
                "						implantacao.mapatributacao	\n" +
                "					where\n" +
                "						sistema = r.sistema and \n" +
                "						agrupador = r.loja_nova\n" +
                "				);\n" +
                "		end if;\n" +
                "	end loop;     \n" +
                "end;\n" +
                "$$;\n";
        
        try(Statement stm = Conexao.createStatement()) {
            stm.execute(sql);
        }
    }
    
    public String getLojaModelo(int idConexao, String sistema) throws Exception {
        String loja = "";
        
        try(Statement stm = Conexao.createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	loja\n" +
                    "from \n" +
                    "	implantacao.codant_clienteeventual \n" +
                    "where \n" +
                    "	sistema = '" + sistema + "' and \n" +
                    "	id_conexao = " + idConexao + "\n" +
                    "limit 1")) {
                if(rs.next()) {
                    loja = rs.getString("loja");
                }
            }
        }
        
        return loja;
    }
    
    public boolean verificaMultilojaMigrada(String lojaOrigem, String sistema, int idConexao) throws Exception {
        boolean lojaJaMigrada = false;
        
        try(Statement stm = Conexao.createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	id_conexao \n" +
                    "from \n" +
                    "	implantacao.codant_clienteeventual\n" +
                    "where \n" +
                    "	sistema = " + SQLUtils.stringSQL(sistema) + " and\n" +
                    "   loja = " + SQLUtils.stringSQL(lojaOrigem) + " and\n" +        
                    "   id_conexao = " + idConexao + " limit 1")) {
                if (rs.next()) {
                    lojaJaMigrada = true;
                }
            }
        }
        
        return lojaJaMigrada;
    }
    
    public String getImpSistema() throws Exception {
        String loja = "";
        
        try(Statement stm = Conexao.createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	sistema\n" +
                    "from \n" +
                    "	implantacao.codant_clienteeventual\n" +
                    "where \n" +
                    "	id_conexao = (select min(id_conexao) from implantacao.codant_clienteeventual)\n" +
                    "limit 1")) {
                if(rs.next()) {
                    loja = rs.getString("sistema");
                }
            }
        }
        
        return loja;
    }
}
