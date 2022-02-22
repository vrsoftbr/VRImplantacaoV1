package vrimplantacao2.dao.cadastro.produto;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import vrframework.classe.Conexao;
import vrframework.classe.Util;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.utils.sql.SQLUtils;
import vrimplantacao2.vo.cadastro.ProdutoAnteriorVO;
import vrimplantacao2.vo.cadastro.ProdutoVO;

public class ProdutoAnteriorDAO {

    private MultiMap<String, ProdutoAnteriorVO> codigoAnterior;
    private MultiMap<String, ProdutoAnteriorVO> forcarNovo;
    private final ProdutoAnteriorEanDAO eanAnteriorDAO = new ProdutoAnteriorEanDAO();
    private String importSistema = "";
    private String importLoja = "";
    private boolean carregarTodosOsAnteriores = false;

    public ProdutoAnteriorDAO() {
    }

    public ProdutoAnteriorDAO(boolean carregarTodosOsAnteriores) {
        this.carregarTodosOsAnteriores = carregarTodosOsAnteriores;
    }

    public void setImportSistema(String importSistema) {
        this.importSistema = importSistema;
    }

    public String getImportSistema() {
        return importSistema;
    }

    public String getImportLoja() {
        return importLoja;
    }

    public void setImportLoja(String importLoja) {
        this.importLoja = importLoja;
    }

    public void clearAnteriores() {
        codigoAnterior = null;
    }

    public MultiMap<String, ProdutoAnteriorVO> getCodigoAnterior() throws Exception {
        if (codigoAnterior == null) {
            atualizarCodigoAnterior();
        }
        return codigoAnterior;
    }

    public MultiMap<String, ProdutoAnteriorVO> getCodigoAnteriorLoja() throws Exception {
        if (codigoAnterior == null) {
            atualizarCodigoAnteriorLoja();
        }
        return codigoAnterior;
    }
    
    public MultiMap<String, ProdutoAnteriorVO> getForcarNovo() throws Exception {
        if (forcarNovo == null) {
            atualizarForcarNovo();
        }
        return forcarNovo;
    }
    
    private void createTable() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute(
                    "do $$\n"
                    + "declare\n"
                    + "begin\n"
                    + "	if not exists(select table_name from information_schema.tables where table_schema = 'implantacao' and table_name = 'codant_produto') then\n"
                    + "		CREATE TABLE implantacao.codant_produto\n"
                    + "                    (\n"
                    + "                      impsistema character varying NOT NULL,\n"
                    + "                      imploja character varying NOT NULL,\n"
                    + "                      impid character varying NOT NULL,\n"
                    + "                      descricao varchar,\n"
                    + "                      codigoatual integer,\n"
                    + "                      piscofinscredito integer,\n"
                    + "                      piscofinsdebito integer,\n"
                    + "                      piscofinsnaturezareceita integer,\n"
                    + "                      icmscst integer,\n"
                    + "                      icmsaliq numeric(14,4),\n"
                    + "                      icmsreducao numeric(14,4),\n"
                    + "                      estoque numeric(14,4),\n"
                    + "                      e_balanca boolean,\n"
                    + "                      custosemimposto numeric(13,4),\n"
                    + "                      custocomimposto numeric(13,4),\n"
                    + "                      margem numeric(11,2),\n"
                    + "                      precovenda numeric(11,2),\n"
                    + "                      ncm character varying(15),\n"
                    + "                      cest varchar(15),\n"
                    + "                      contadorimportacao integer not null default 0,\n"
                    + "                      novo boolean default false not null,\n"
                    + "                      codigosped varchar,\n"
                    + "                      situacaocadastro integer,\n"
                    + "                      dataimportacao timestamp,\n"
                    + "                      obsimportacao character varying,\n"
                    + "                      icmscstsaida integer,\n"
                    + "                      icmsaliqsaida numeric(14,4),\n"
                    + "                      icmsreducaosaida numeric(14,4),\n"
                    + "                      icmscstsaidaforaestado integer,\n"
                    + "                      icmsaliqsaidaforaestado numeric(14,4),\n"
                    + "                      icmsreducaosaidaforaestado numeric(14,4),\n"
                    + "                      icmscstsaidaforaestadonf integer,\n"
                    + "                      icmsaliqsaidaforaestadonf numeric(14,4),\n"
                    + "                      icmsreducaosaidaforaestadonf numeric(14,4),\n"
                    + "                      icmscstentrada integer,\n"
                    + "                      icmsaliqentrada numeric(14,4),\n"
                    + "                      icmsreducaoentrada numeric(14,4),\n"
                    + "                      icmscstentradaforaestado integer,\n"
                    + "                      icmsaliqentradaforaestado numeric(14,4),\n"
                    + "                      icmsreducaoentradaforaestado numeric(14,4),\n"
                    + "                      icmscstconsumidor integer,\n"
                    + "                      icmsaliqconsumidor numeric(14,4),\n"
                    + "                      icmsreducaoconsumidor numeric(14,4),\n"
                    + "                      icmsdebitoid character varying,\n"
                    + "                      icmsdebitoforaestadoid character varying,\n"
                    + "                      icmsdebitoforaestadonfid character varying,\n"
                    + "                      icmscreditoid character varying,\n"
                    + "                      icmscreditoforaestadoid character varying,\n"
                    + "                      icmsconsumidorid character varying,\n"
                    + "                      dataalteracao timestamp, \n"        
                    + "                      datacadastro timestamp, \n"        
                    + "                      primary key (impsistema, imploja, impid),\n"
                    + "                      unique (impsistema, imploja, codigosped)\n"
                    + "                );\n"
                    + "		raise notice 'tabela criada';\n"
                    + "	end if;\n"
                    + "end;\n"
                    + "$$;"
            );
        }
    }

    public int getCodigoAnterior2(String sistema, String loja, String id) throws Exception {
        int retorno = -1;
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + "	ant.impsistema, \n"
                    + "	ant.imploja, \n"
                    + "	ant.impid, \n"
                    + "	ant.descricao, \n"
                    + "	p.descricaocompleta, \n"
                    + "	p.descricaoreduzida, \n"
                    + "	p.descricaogondola, \n"
                    + "	ant.codigoatual, \n"
                    + "	ant.piscofinscredito, \n"
                    + "	ant.piscofinsdebito, \n"
                    + "	ant.piscofinsnaturezareceita, \n"
                    + "	ant.icmscst, \n"
                    + "	ant.icmsaliq, \n"
                    + "	ant.icmsreducao, \n"
                    + "	ant.estoque, \n"
                    + "	ant.e_balanca, \n"
                    + "	ant.custosemimposto, \n"
                    + "	ant.custocomimposto, \n"
                    + "	ant.margem, \n"
                    + "	ant.precovenda, \n"
                    + "	ant.ncm, \n"
                    + "	ant.cest,\n"
                    + "	ant.contadorimportacao,\n"
                    + "	ant.novo\n"
                    + "FROM \n"
                    + "	implantacao.codant_produto ant\n"
                    + "	left join produto p on ant.codigoatual = p.id\n"
                    + "where \n"
                    + " ant.impsistema = " + SQLUtils.stringSQL(sistema) + " "
                    + " and ant.imploja = " + SQLUtils.stringSQL(loja) + " "
                    + "and ant.impid = " + SQLUtils.stringSQL(id)
            )) {
                if (rst.next()) {
                    retorno = rst.getInt("codigoatual");
                } else {
                    retorno = -1;
                }
            }
        }
        return retorno;
    }

    public int getProdutoAnteriorSemUltimoDigito2(String sistema, String loja, String id) throws Exception {
        int retorno = -1;
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + "	ant.codigoatual \n"
                    + "FROM \n"
                    + "	implantacao.codant_produto ant\n"
                    + "where \n"
                    + " ant.impsistema = " + SQLUtils.stringSQL(sistema) + " "
                    + " and ant.imploja = " + SQLUtils.stringSQL(loja) + " "
                    + " and substring(ant.impid, 1, char_length(ant.impid) -1) = " + SQLUtils.stringSQL(id) + "\n"
            )) {
                if (rst.next()) {
                    retorno = rst.getInt("codigoatual");
                } else {
                    retorno = -1;
                }
            }
        }
        return retorno;
    }
    
    public int getCodigoAnterior2ByEAN(String sistema, String loja, String id) throws Exception {
        int retorno = -1;
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + "	ant.impsistema, \n"
                    + "	ant.imploja, \n"
                    + "	ant.impid, \n"
                    + "	ant.descricao, \n"
                    + "	p.descricaocompleta, \n"
                    + "	p.descricaoreduzida, \n"
                    + "	p.descricaogondola, \n"
                    + "	ant.codigoatual, \n"
                    + "	ant.piscofinscredito, \n"
                    + "	ant.piscofinsdebito, \n"
                    + "	ant.piscofinsnaturezareceita, \n"
                    + "	ant.icmscst, \n"
                    + "	ant.icmsaliq, \n"
                    + "	ant.icmsreducao, \n"
                    + "	ant.estoque, \n"
                    + "	ant.e_balanca, \n"
                    + "	ant.custosemimposto, \n"
                    + "	ant.custocomimposto, \n"
                    + "	ant.margem, \n"
                    + "	ant.precovenda, \n"
                    + "	ant.ncm, \n"
                    + "	ant.cest,\n"
                    + "	ant.contadorimportacao,\n"
                    + "	ant.novo\n"
                    + "FROM \n"
                    + "	implantacao.codant_produto ant\n"
                    + "	left join produto p on ant.codigoatual = p.id\n"
                    + "where \n"
                    + " ant.impsistema = " + SQLUtils.stringSQL(sistema) + " "
                    + " and ant.imploja = " + SQLUtils.stringSQL(loja) + " "
                    + "and ant.impid = lpad(" + SQLUtils.stringSQL(id) + ", 14, '0')"
            )) {
                if (rst.next()) {
                    retorno = rst.getInt("codigoatual");
                } else {
                    retorno = -1;
                }
            }
        }
        return retorno;
    }

    public String getCodigoAnterior3(String sistema, String loja, String id) throws Exception {
        String retorno = "-1";
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT "
                    + "impid "
                    + "FROM \n"
                    + "	implantacao.codant_produto ant\n"
                    + "where \n"
                    + " ant.impsistema = " + SQLUtils.stringSQL(sistema) + " "
                    + " and ant.imploja = " + SQLUtils.stringSQL(loja) + " "
                    + "and ant.impid = " + SQLUtils.stringSQL(id)
            )) {
                if (rst.next()) {
                    retorno = rst.getString("impid");
                } else {
                    retorno = "-1";
                }
            }
        }
        return retorno;
    }

    public String getCodigoAnteriorEAN(String sistema, String loja, String ean) throws Exception {
        String retorno = "-1";
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT "
                    + "importid "
                    + "FROM \n"
                    + "	implantacao.codant_ean ant\n"
                    + "where \n"
                    + " ant.importsistema = " + SQLUtils.stringSQL(sistema) + " "
                    + " and ant.importloja = " + SQLUtils.stringSQL(loja) + " "
                    + "and ant.ean = " + SQLUtils.stringSQL(ean)
            )) {
                if (rst.next()) {
                    retorno = rst.getString("importid");
                } else {
                    retorno = "-1";
                }
            }
        }
        return retorno;
    }

    public int getCodigoAtualEANant(String sistema, String loja, String ean) throws Exception {
        int retorno = -1;
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	cp.codigoatual\n" +
                    "from\n" +
                    "	implantacao.codant_ean ce\n" +
                    "	join implantacao.codant_produto cp on\n" +
                    "		ce.importsistema = cp.impsistema and\n" +
                    "		ce.importloja = cp.imploja and\n" +
                    "		ce.importid = cp.impid\n" +
                    "where\n" +
                    "	ce.ean::bigint = " + ean + " and\n" +
                    "	ce.importsistema = '" + sistema + "' and\n" +
                    "	ce.importloja = '" + loja + "'\n" +
                    "limit 1\n"
            )) {

                if (rst.next()) {
                    retorno = rst.getInt("codigoatual");
                } else {
                    retorno = -1;
                }
            }
        }
        return retorno;
    }

    public int getCodigoAtualEANantCPGestor(String sistema, String loja, String ean) throws Exception {
        int retorno = -1;
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	cp.codigoatual\n" +
                    "from\n" +
                    "	implantacao.codant_ean ce\n" +
                    "	join implantacao.codant_produto cp on\n" +
                    "		ce.importsistema = cp.impsistema and\n" +
                    "		ce.importloja = cp.imploja and\n" +
                    "		ce.importid = cp.impid\n" +
                    "where\n" +
                    "	ce.ean::bigint = " + ean + " and\n" +
                    "	ce.importsistema = '" + sistema + "' and\n" +
                    "	ce.importloja = '" + loja + "'\n" +
                    "limit 1\n"
            )) {

                if (rst.next()) {
                    retorno = rst.getInt("codigoatual");
                } else {
                    retorno = -1;
                }
            }
        }
        return retorno;
    }
    
    public String getCodigoAnteriorCpGestor(String ean) throws Exception {
        String retorno = "-1";
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select pr_codint "
                    + "from implantacao.produtos_tokleve "
                    + "where pr_cbarra = lpad('" + ean + "', 14, '0')"
            )) {
                if (rst.next()) {
                    retorno = rst.getString("pr_codint");
                } else {
                    retorno = "-1";
                }
            }
        }
        return retorno;
    }

    public void atualizarCodigoAnterior() throws Exception {
        codigoAnterior = new MultiMap<>(3);
        createTable();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + "	ant.impsistema, \n"
                    + "	ant.imploja, \n"
                    + "	ant.impid, \n"
                    + "	ant.descricao, \n"
                    + "	p.descricaocompleta, \n"
                    + "	p.descricaoreduzida, \n"
                    + "	p.descricaogondola, \n"
                    + "	ant.codigoatual, \n"
                    + "	ant.piscofinscredito, \n"
                    + "	ant.piscofinsdebito, \n"
                    + "	ant.piscofinsnaturezareceita, \n"
                    + "	ant.icmscst, \n"
                    + "	ant.icmsaliq, \n"
                    + "	ant.icmsreducao, \n"
                    + "	ant.estoque, \n"
                    + "	ant.e_balanca, \n"
                    + "	ant.custosemimposto, \n"
                    + "	ant.custocomimposto, \n"
                    + "	ant.margem, \n"
                    + "	ant.precovenda, \n"
                    + "	ant.ncm, \n"
                    + "	ant.cest,\n"
                    + "	ant.contadorimportacao,\n"
                    + "	ant.novo,\n"
                    + " ant.id_conexao\n"        
                    + "FROM \n"
                    + "	implantacao.codant_produto ant\n"
                    + "	left join produto p on ant.codigoatual = p.id\n"
                    + (!carregarTodosOsAnteriores
                            ? "where \n"
                            + "       ant.impsistema = " + SQLUtils.stringSQL(getImportSistema()) + " and\n"
                            + "       ant.imploja = " + SQLUtils.stringSQL(getImportLoja()) + "\n"
                            : "")
                    + "order by\n"
                    + "	ant.impsistema, \n"
                    + "	ant.imploja, \n"
                    + "	ant.impid"
            )) {
                int cont = 1;
                while (rst.next()) {
                    ProdutoAnteriorVO vo = new ProdutoAnteriorVO();

                    vo.setImportSistema(rst.getString("impsistema"));
                    vo.setImportLoja(rst.getString("imploja"));
                    vo.setImportId(rst.getString("impid"));
                    vo.setDescricao(rst.getString("descricao"));
                    ProdutoVO produto = null;
                    if (rst.getString("codigoatual") != null) {
                        produto = new ProdutoVO();
                        produto.setId(rst.getInt("codigoatual"));
                        produto.setDescricaoCompleta("descricaocompleta");
                        produto.setDescricaoGondola("descricaogondola");
                        produto.setDescricaoReduzida("descricaoreduzida");
                    }
                    vo.setCodigoAtual(produto);
                    vo.setPisCofinsCredito(rst.getInt("piscofinscredito"));
                    vo.setPisCofinsDebito(rst.getInt("piscofinsdebito"));
                    vo.setPisCofinsNaturezaReceita(rst.getInt("piscofinsnaturezareceita"));
                    vo.setIcmsCst(rst.getInt("icmscst"));
                    vo.setIcmsAliq(rst.getDouble("icmsaliq"));
                    vo.setIcmsReducao(rst.getDouble("icmsreducao"));
                    vo.setEstoque(rst.getDouble("estoque"));
                    vo.seteBalanca(rst.getBoolean("e_balanca"));
                    vo.setCustosemimposto(rst.getDouble("custosemimposto"));
                    vo.setCustocomimposto(rst.getDouble("custocomimposto"));
                    vo.setPrecovenda(rst.getDouble("precovenda"));
                    vo.setNcm(rst.getString("ncm"));
                    vo.setCest(rst.getString("cest"));
                    vo.setNovo(rst.getBoolean("novo"));
                    vo.setContadorImportacao(rst.getInt("contadorimportacao"));
                    vo.setIdConexao(rst.getInt("id_conexao"));
                    eanAnteriorDAO.addEans(vo);
                    codigoAnterior.put(vo, vo.getChave());
                    cont++;
                }
            }
        }
    }

    public void atualizarCodigoAnteriorLoja() throws Exception {
        codigoAnterior = new MultiMap<>(3);
        createTable();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n" +
                    "	ant.impsistema, \n" +
                    "	ant.imploja, \n" +
                    "	ant.impid, \n" +
                    "	ant.descricao, \n" +
                    "	p.descricaocompleta, \n" +
                    "	p.descricaoreduzida, \n" +
                    "	p.descricaogondola, \n" +
                    "	ant.codigoatual, \n" +
                    "	ant.piscofinscredito, \n" +
                    "	ant.piscofinsdebito, \n" +
                    "	ant.piscofinsnaturezareceita, \n" +
                    "	ant.icmscst, \n" +
                    "	ant.icmsaliq, \n" +
                    "	ant.icmsreducao, \n" +
                    "	ant.estoque, \n" +
                    "	ant.e_balanca, \n" +
                    "	ant.custosemimposto, \n" +
                    "	ant.custocomimposto, \n" +
                    "	ant.margem, \n" +
                    "	ant.precovenda, \n" +
                    "	ant.ncm, \n" +
                    "	ant.cest,\n" +
                    "	ant.contadorimportacao,\n" +
                    "	ant.novo\n" +
                    "FROM \n" +
                    "	implantacao.codant_produto ant\n" +
                    "	left join produto p on\n" +
                    "		ant.codigoatual = p.id\n" +
                    "where\n" +
                    "    ant.impsistema = " + SQLUtils.stringSQL(getImportSistema()) + " and\n" +
                    "    ant.imploja = " + SQLUtils.stringSQL(getImportLoja()) + " and    \n" +
                    "	ant.obsimportacao in (\n" +
                    "		'PRODUTO NOVO - INSERIDO PELO MAPEAMENTO (FORCAR NOVO)',\n" +
                    "		'PRODUTO NOVO - INSERIDO PELO METODO unificar DA CLASSE vrimplantacao2.dao.cadastro.produto2.ProdutoRepository'\n" +
                    "	)\n" +
                    "order by\n" +
                    "	ant.impsistema, \n" +
                    "	ant.imploja, \n" +
                    "	ant.impid"
            )) {
                int cont = 1;
                while (rst.next()) {
                    ProdutoAnteriorVO vo = new ProdutoAnteriorVO();

                    vo.setImportSistema(rst.getString("impsistema"));
                    vo.setImportLoja(rst.getString("imploja"));
                    vo.setImportId(rst.getString("impid"));
                    vo.setDescricao(rst.getString("descricao"));
                    ProdutoVO produto = null;
                    if (rst.getString("codigoatual") != null) {
                        produto = new ProdutoVO();
                        produto.setId(rst.getInt("codigoatual"));
                        produto.setDescricaoCompleta("descricaocompleta");
                        produto.setDescricaoCompleta("descricaogondola");
                        produto.setDescricaoCompleta("descricaoreduzida");
                    }
                    vo.setCodigoAtual(produto);
                    vo.setPisCofinsCredito(rst.getInt("piscofinscredito"));
                    vo.setPisCofinsDebito(rst.getInt("piscofinsdebito"));
                    vo.setPisCofinsNaturezaReceita(rst.getInt("piscofinsnaturezareceita"));
                    vo.setIcmsCst(rst.getInt("icmscst"));
                    vo.setIcmsAliq(rst.getDouble("icmsaliq"));
                    vo.setIcmsReducao(rst.getDouble("icmsreducao"));
                    vo.setEstoque(rst.getDouble("estoque"));
                    vo.seteBalanca(rst.getBoolean("e_balanca"));
                    vo.setCustosemimposto(rst.getDouble("custosemimposto"));
                    vo.setCustocomimposto(rst.getDouble("custocomimposto"));
                    vo.setPrecovenda(rst.getDouble("precovenda"));
                    vo.setNcm(rst.getString("ncm"));
                    vo.setCest(rst.getString("cest"));
                    vo.setNovo(rst.getBoolean("novo"));
                    vo.setContadorImportacao(rst.getInt("contadorimportacao"));
                    eanAnteriorDAO.addEans(vo);
                    codigoAnterior.put(vo, vo.getChave());
                    cont++;
                }
            }
        }
    }
    
    private int contador = -1;

    private void obtemContador() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select coalesce(max(contadorimportacao) + 1,1) contador from implantacao.codant_produto"
            )) {
                rst.next();
                contador = rst.getInt("contador");
            }
        }
    }

    public void salvar(Collection<ProdutoAnteriorVO> values) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            for (ProdutoAnteriorVO vo : values) {

                if (!getCodigoAnterior().containsKey(
                        vo.getImportSistema(),
                        vo.getImportLoja(),
                        vo.getImportId()
                )) {
                    SQLBuilder sql = new SQLBuilder();
                    sql.setTableName("codant_produto");
                    sql.setSchema("implantacao");
                    sql.put("impsistema", vo.getImportSistema());
                    sql.put("imploja", vo.getImportLoja());
                    sql.put("impid", vo.getImportId());
                    sql.put("descricao", vo.getDescricao());
                    sql.put("codigoatual", (vo.getCodigoAtual() != null ? vo.getCodigoAtual().getId() : null));
                    sql.put("piscofinscredito", vo.getPisCofinsCredito());
                    sql.put("piscofinsdebito", vo.getPisCofinsDebito());
                    sql.put("piscofinsnaturezareceita", vo.getPisCofinsNaturezaReceita(), -1);
                    sql.put("icmscst", vo.getIcmsCst());
                    sql.put("icmsaliq", vo.getIcmsAliq());
                    sql.put("icmsreducao", vo.getIcmsReducao());
                    sql.put("estoque", vo.getEstoque());
                    sql.put("e_balanca", vo.iseBalanca());
                    sql.put("custosemimposto", vo.getCustosemimposto());
                    sql.put("custocomimposto", vo.getCustocomimposto());
                    sql.put("margem", vo.getMargem());
                    sql.put("precovenda", vo.getPrecovenda());
                    sql.put("ncm", vo.getNcm());
                    sql.put("cest", vo.getCest());
                    sql.put("novo", vo.isNovo());
                    sql.put("codigosped", vo.getCodigoSped());
                    if (contador == -1) {
                        obtemContador();
                    }
                    sql.put("contadorimportacao", contador);
                    
                    sql.put("situacaocadastro", vo.getSituacaoCadastro().getId());
                    sql.put("dataimportacao", vo.getDataHora());
                    sql.put("obsimportacao", vo.getObsImportacao());                    

                    sql.put("icmscstsaida", vo.getIcmsCstSaida());
                    sql.put("icmsaliqsaida", vo.getIcmsAliqSaida());
                    sql.put("icmsreducaosaida", vo.getIcmsReducaoSaida());

                    sql.put("icmscstsaidaforaestado", vo.getIcmsCstSaidaForaEstado());
                    sql.put("icmsaliqsaidaforaestado", vo.getIcmsAliqSaidaForaEstado());
                    sql.put("icmsreducaosaidaforaestado", vo.getIcmsReducaoSaidaForaEstado());

                    sql.put("icmscstsaidaforaestadonf", vo.getIcmsCstSaidaForaEstadoNf());
                    sql.put("icmsaliqsaidaforaestadonf", vo.getIcmsAliqSaidaForaEstadoNf());
                    sql.put("icmsreducaosaidaforaestadonf", vo.getIcmsReducaoSaidaForaEstadoNf());

                    sql.put("icmscstentrada", vo.getIcmsCstEntrada());
                    sql.put("icmsaliqentrada", vo.getIcmsAliqEntrada());
                    sql.put("icmsreducaoentrada", vo.getIcmsReducaoEntrada());

                    sql.put("icmscstentradaforaestado", vo.getIcmsCstEntradaForaEstado());
                    sql.put("icmsaliqentradaforaestado", vo.getIcmsAliqEntradaForaEstado());
                    sql.put("icmsreducaoentradaforaestado", vo.getIcmsReducaoEntradaForaEstado());

                    sql.put("icmscstconsumidor", vo.getIcmsCstConsumidor());
                    sql.put("icmsaliqconsumidor", vo.getIcmsAliqConsumidor());
                    sql.put("icmsreducaoconsumidor", vo.getIcmsReducaoConsumidor());

                    sql.put("icmsdebitoid", vo.getIcmsDebitoId());
                    sql.put("icmsdebitoforaestadoid", vo.getIcmsDebitoForaEstadoId());
                    sql.put("icmsdebitoforaestadonfid", vo.getIcmsDebitoForaEstadoIdNf());

                    sql.put("icmscreditoid", vo.getIcmsCreditoId());
                    sql.put("icmscreditoforaestadoid", vo.getIcmsCreditoForaEstadoId());

                    sql.put("icmsconsumidorid", vo.getIcmsConsumidorId());
                    
                    try {
                        stm.execute(sql.getInsert());
                    } catch (Exception e) {
                        Util.exibirMensagem(sql.getInsert(), "");
                    }
                    eanAnteriorDAO.salvar(vo.getEans().values());

                    codigoAnterior.put(
                            vo,
                            vo.getImportSistema(),
                            vo.getImportLoja(),
                            vo.getImportId()
                    );
                } else {
                    eanAnteriorDAO.salvar(vo.getEans().values());
                }
            }
        }
    }

    public void salvar(ProdutoAnteriorVO anterior) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            
            //<editor-fold defaultstate="collapsed" desc="Conflito de Insert - Melhoria">
            /*INSERT INTO cliente VALUES (2, 'MARIAA')
            ON CONFLICT (id)
            DO
              UPDATE SET nome = 'MARIAA';*/
            //</editor-fold>

            sql.setTableName("codant_produto");
            sql.setSchema("implantacao");
            sql.put("impsistema", anterior.getImportSistema());
            sql.put("imploja", anterior.getImportLoja());
            sql.put("impid", anterior.getImportId());
            sql.put("descricao", anterior.getDescricao());
            sql.put("codigoatual", (anterior.getCodigoAtual() != null ? anterior.getCodigoAtual().getId() : null));
            sql.put("piscofinscredito", anterior.getPisCofinsCredito());
            sql.put("piscofinsdebito", anterior.getPisCofinsDebito());
            sql.put("piscofinsnaturezareceita", anterior.getPisCofinsNaturezaReceita(), -1);
            sql.put("icmscst", anterior.getIcmsCst());
            sql.put("icmsaliq", anterior.getIcmsAliq());
            sql.put("icmsreducao", anterior.getIcmsReducao());
            sql.put("estoque", anterior.getEstoque());
            sql.put("e_balanca", anterior.iseBalanca());
            sql.put("custosemimposto", anterior.getCustosemimposto());
            sql.put("custocomimposto", anterior.getCustocomimposto());
            sql.put("margem", anterior.getMargem());
            sql.put("precovenda", anterior.getPrecovenda());
            sql.put("ncm", anterior.getNcm());
            sql.put("cest", anterior.getCest());
            sql.put("novo", anterior.isNovo());
            sql.put("codigosped", anterior.getCodigoSped());
            if (contador == -1) {
                obtemContador();
            }
            sql.put("contadorimportacao", contador);
            sql.put("situacaocadastro", anterior.getSituacaoCadastro().getId());
            sql.put("dataimportacao", "'" + anterior.getDataHora() + "'");
            sql.put("obsimportacao", anterior.getObsImportacao());
            
            sql.put("icmscstsaida", anterior.getIcmsCstSaida());
            sql.put("icmsaliqsaida", anterior.getIcmsAliqSaida());
            sql.put("icmsreducaosaida", anterior.getIcmsReducaoSaida());
            
            sql.put("icmscstsaidaforaestado", anterior.getIcmsCstSaidaForaEstado());
            sql.put("icmsaliqsaidaforaestado", anterior.getIcmsAliqSaidaForaEstado());
            sql.put("icmsreducaosaidaforaestado", anterior.getIcmsReducaoSaidaForaEstado());
            
            sql.put("icmscstsaidaforaestadonf", anterior.getIcmsCstSaidaForaEstadoNf());
            sql.put("icmsaliqsaidaforaestadonf", anterior.getIcmsAliqSaidaForaEstadoNf());
            sql.put("icmsreducaosaidaforaestadonf", anterior.getIcmsReducaoSaidaForaEstadoNf());
            
            sql.put("icmscstentrada", anterior.getIcmsCstEntrada());
            sql.put("icmsaliqentrada", anterior.getIcmsAliqEntrada());
            sql.put("icmsreducaoentrada", anterior.getIcmsReducaoEntrada());
            
            sql.put("icmscstentradaforaestado", anterior.getIcmsCstEntradaForaEstado());
            sql.put("icmsaliqentradaforaestado", anterior.getIcmsAliqEntradaForaEstado());
            sql.put("icmsreducaoentradaforaestado", anterior.getIcmsReducaoEntradaForaEstado());
            
            sql.put("icmscstconsumidor", anterior.getIcmsCstConsumidor());
            sql.put("icmsaliqconsumidor", anterior.getIcmsAliqConsumidor());
            sql.put("icmsreducaoconsumidor", anterior.getIcmsReducaoConsumidor());
            
            sql.put("icmsdebitoid", anterior.getIcmsDebitoId());
            sql.put("icmsdebitoforaestadoid", anterior.getIcmsDebitoForaEstadoId());
            sql.put("icmsdebitoforaestadonfid", anterior.getIcmsDebitoForaEstadoIdNf());
            
            sql.put("icmscreditoid", anterior.getIcmsCreditoId());
            sql.put("icmscreditoforaestadoid", anterior.getIcmsCreditoForaEstadoId());
            
            sql.put("icmsconsumidorid", anterior.getIcmsConsumidorId());
            sql.put("datacadastro", anterior.getDataCadastro());

            sql.put("id_conexao", anterior.getIdConexao());
            
            try {
                stm.execute(sql.getInsert());
                getCodigoAnterior().put(
                        anterior,
                        anterior.getImportSistema(),
                        anterior.getImportLoja(),
                        anterior.getImportId());
            } catch (Exception e) {
                sql.setFormatarSQL(true);
                System.out.println(sql.getInsert());
                Util.exibirMensagem(sql.getInsert(), "");
                throw e;
            }
        }
    }

    public void alterar(ProdutoAnteriorVO anterior) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            sql.setSchema("implantacao");
            sql.setTableName("codant_produto");
            sql.put("codigoatual", (anterior.getCodigoAtual() != null ? anterior.getCodigoAtual().getId() : null));
            
            sql.setWhere(
                    "impsistema = '" + anterior.getImportSistema() + "'"
                    + " and imploja = '" + anterior.getImportLoja() + "'"
                    + " and impid = '" + anterior.getImportId() + "'");
            
            if (!sql.isEmpty()) {
                stm.execute(sql.getUpdate());
            }
        }
    }

    public void atualizarIcmsAnterior(ProdutoAnteriorVO anterior) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            sql.setSchema("implantacao");
            sql.setTableName("codant_produto");

            sql.put("icmscst", anterior.getIcmsCst());
            sql.put("icmsaliq", anterior.getIcmsAliq());
            sql.put("icmsreducao", anterior.getIcmsReducao());
            
            sql.put("icmscstsaida", anterior.getIcmsCstSaida());
            sql.put("icmsaliqsaida", anterior.getIcmsAliqSaida());
            sql.put("icmsreducaosaida", anterior.getIcmsReducaoSaida());
            
            sql.put("icmscstsaidaforaestado", anterior.getIcmsCstSaidaForaEstado());
            sql.put("icmsaliqsaidaforaestado", anterior.getIcmsAliqSaidaForaEstado());
            sql.put("icmsreducaosaidaforaestado", anterior.getIcmsReducaoSaidaForaEstado());
            
            sql.put("icmscstsaidaforaestadonf", anterior.getIcmsCstSaidaForaEstadoNf());
            sql.put("icmsaliqsaidaforaestadonf", anterior.getIcmsAliqSaidaForaEstadoNf());
            sql.put("icmsreducaosaidaforaestadonf", anterior.getIcmsReducaoSaidaForaEstadoNf());
            
            sql.put("icmscstentrada", anterior.getIcmsCstEntrada());
            sql.put("icmsaliqentrada", anterior.getIcmsAliqEntrada());
            sql.put("icmsreducaoentrada", anterior.getIcmsReducaoEntrada());
            
            sql.put("icmscstentradaforaestado", anterior.getIcmsCstEntradaForaEstado());
            sql.put("icmsaliqentradaforaestado", anterior.getIcmsAliqEntradaForaEstado());
            sql.put("icmsreducaoentradaforaestado", anterior.getIcmsReducaoEntradaForaEstado());
            
            sql.put("icmscstconsumidor", anterior.getIcmsCstConsumidor());
            sql.put("icmsaliqconsumidor", anterior.getIcmsAliqConsumidor());
            sql.put("icmsreducaoconsumidor", anterior.getIcmsReducaoConsumidor());
            
            sql.put("icmsdebitoid", anterior.getIcmsDebitoId());
            sql.put("icmsdebitoforaestadoid", anterior.getIcmsDebitoForaEstadoId());
            sql.put("icmsdebitoforaestadonfid", anterior.getIcmsDebitoForaEstadoIdNf());
            
            sql.put("icmscreditoid", anterior.getIcmsCreditoId());
            sql.put("icmscreditoforaestadoid", anterior.getIcmsCreditoForaEstadoId());
            
            sql.put("icmsconsumidorid", anterior.getIcmsConsumidorId());
            
            sql.put("dataalteracao", anterior.getDataHoraAlteracao());
            
            sql.setWhere(
                    "impsistema = '" + anterior.getImportSistema() + "'"
                    + " and imploja = '" + anterior.getImportLoja() + "'"
                    + " and impid = '" + anterior.getImportId() + "'");
            
            if (!sql.isEmpty()) {
                stm.execute(sql.getUpdate());
            }
        }
    }

    public void atualizarIcmsAnteriorLoja(ProdutoAnteriorVO anterior, boolean primeiraLojaMigrada) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            sql.setSchema("implantacao");
            sql.setTableName("codant_produto");

            sql.put("icmscst", anterior.getIcmsCst());
            sql.put("icmsaliq", anterior.getIcmsAliq());
            sql.put("icmsreducao", anterior.getIcmsReducao());
            
            sql.put("icmscstsaida", anterior.getIcmsCstSaida());
            sql.put("icmsaliqsaida", anterior.getIcmsAliqSaida());
            sql.put("icmsreducaosaida", anterior.getIcmsReducaoSaida());
            
            sql.put("icmscstsaidaforaestado", anterior.getIcmsCstSaidaForaEstado());
            sql.put("icmsaliqsaidaforaestado", anterior.getIcmsAliqSaidaForaEstado());
            sql.put("icmsreducaosaidaforaestado", anterior.getIcmsReducaoSaidaForaEstado());
            
            sql.put("icmscstsaidaforaestadonf", anterior.getIcmsCstSaidaForaEstadoNf());
            sql.put("icmsaliqsaidaforaestadonf", anterior.getIcmsAliqSaidaForaEstadoNf());
            sql.put("icmsreducaosaidaforaestadonf", anterior.getIcmsReducaoSaidaForaEstadoNf());
            
            sql.put("icmscstentrada", anterior.getIcmsCstEntrada());
            sql.put("icmsaliqentrada", anterior.getIcmsAliqEntrada());
            sql.put("icmsreducaoentrada", anterior.getIcmsReducaoEntrada());
            
            sql.put("icmscstentradaforaestado", anterior.getIcmsCstEntradaForaEstado());
            sql.put("icmsaliqentradaforaestado", anterior.getIcmsAliqEntradaForaEstado());
            sql.put("icmsreducaoentradaforaestado", anterior.getIcmsReducaoEntradaForaEstado());
            
            sql.put("icmscstconsumidor", anterior.getIcmsCstConsumidor());
            sql.put("icmsaliqconsumidor", anterior.getIcmsAliqConsumidor());
            sql.put("icmsreducaoconsumidor", anterior.getIcmsReducaoConsumidor());
            
            sql.put("icmsdebitoid", anterior.getIcmsDebitoId());
            sql.put("icmsdebitoforaestadoid", anterior.getIcmsDebitoForaEstadoId());
            sql.put("icmsdebitoforaestadonfid", anterior.getIcmsDebitoForaEstadoIdNf());
            
            sql.put("icmscreditoid", anterior.getIcmsCreditoId());
            sql.put("icmscreditoforaestadoid", anterior.getIcmsCreditoForaEstadoId());
            
            sql.put("icmsconsumidorid", anterior.getIcmsConsumidorId());
            
            sql.put("dataalteracao", anterior.getDataHoraAlteracao());
            
            sql.setWhere(
                    "impsistema = '" + anterior.getImportSistema() + "'"
                    + " and imploja = '" + anterior.getImportLoja() + "'"
                    + " and impid = '" + anterior.getImportId() + "'");
                
            if (!sql.isEmpty()) {
                stm.execute(sql.getUpdate());
            }
            
        }
    }
    
    public Map<String, ProdutoAnteriorVO> getCodigoAnterior(String sistema, String loja) throws Exception {
        Map<String, ProdutoAnteriorVO> result = new LinkedHashMap<>();
        createTable();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + "	ant.impsistema, \n"
                    + "	ant.imploja, \n"
                    + "	ant.impid, \n"
                    + "	ant.descricao, \n"
                    + "	p.descricaocompleta, \n"
                    + "	p.descricaoreduzida, \n"
                    + "	p.descricaogondola, \n"
                    + "	ant.codigoatual, \n"
                    + "	ant.piscofinscredito, \n"
                    + "	ant.piscofinsdebito, \n"
                    + "	ant.piscofinsnaturezareceita, \n"
                    + "	ant.icmscst, \n"
                    + "	ant.icmsaliq, \n"
                    + "	ant.icmsreducao, \n"
                    + "	ant.estoque, \n"
                    + "	ant.e_balanca, \n"
                    + "	ant.custosemimposto, \n"
                    + "	ant.custocomimposto, \n"
                    + "	ant.margem, \n"
                    + "	ant.precovenda, \n"
                    + "	ant.ncm, \n"
                    + "	ant.cest,\n"
                    + "	ant.contadorimportacao,\n"
                    + "	ant.novo\n"
                    + "FROM \n"
                    + "	implantacao.codant_produto ant\n"
                    + "	left join produto p on ant.codigoatual = p.id\n"
                    + "where \n"
                    + "       ant.impsistema = " + SQLUtils.stringSQL(getImportSistema()) + " and\n"
                    + "       ant.imploja = " + SQLUtils.stringSQL(getImportLoja()) + "\n"
                    + "order by\n"
                    + "	ant.impsistema, \n"
                    + "	ant.imploja, \n"
                    + "	ant.impid"
            )) {
                while (rst.next()) {
                    ProdutoAnteriorVO vo = new ProdutoAnteriorVO();

                    vo.setImportSistema(rst.getString("impsistema"));
                    vo.setImportLoja(rst.getString("imploja"));
                    vo.setImportId(rst.getString("impid"));
                    vo.setDescricao(rst.getString("descricao"));
                    ProdutoVO produto = null;
                    if (rst.getString("codigoatual") != null) {
                        produto = new ProdutoVO();
                        produto.setId(rst.getInt("codigoatual"));
                        produto.setDescricaoCompleta("descricaocompleta");
                        produto.setDescricaoCompleta("descricaogondola");
                        produto.setDescricaoCompleta("descricaoreduzida");
                    }
                    vo.setCodigoAtual(produto);
                    vo.setPisCofinsCredito(rst.getInt("piscofinscredito"));
                    vo.setPisCofinsDebito(rst.getInt("piscofinsdebito"));
                    vo.setPisCofinsNaturezaReceita(rst.getInt("piscofinsnaturezareceita"));
                    vo.setIcmsCst(rst.getInt("icmscst"));
                    vo.setIcmsAliq(rst.getDouble("icmsaliq"));
                    vo.setIcmsReducao(rst.getDouble("icmsreducao"));
                    vo.setEstoque(rst.getDouble("estoque"));
                    vo.seteBalanca(rst.getBoolean("e_balanca"));
                    vo.setCustosemimposto(rst.getDouble("custosemimposto"));
                    vo.setCustocomimposto(rst.getDouble("custocomimposto"));
                    vo.setPrecovenda(rst.getDouble("precovenda"));
                    vo.setNcm(rst.getString("ncm"));
                    vo.setCest(rst.getString("cest"));
                    vo.setNovo(rst.getBoolean("novo"));
                    vo.setContadorImportacao(rst.getInt("contadorimportacao"));

                    result.put(vo.getImportId(), vo);
                }
            }
        }

        return result;
    }

    /**
     * Retorna o código atual dos produtos através do código de importação.
     *
     * @param sistema Código do sistema importado.
     * @param loja Código da loja importada.
     * @return {@link Map} com os códigos dos produtos mapeados com os códigos
     * anteriores.
     * @throws Exception
     */
    public Map<String, Integer> getAnteriores(String sistema, String loja) throws Exception {
        Map<String, Integer> result = new HashMap<>();

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	ant.impid,\n"
                    + "	ant.codigoatual\n"
                    + "from\n"
                    + "	implantacao.codant_produto ant\n"
                    + "	join produto p on ant.codigoatual = p.id\n"
                    + "where\n"
                    + "	ant.impsistema = " + SQLUtils.stringSQL(sistema) + " and\n"
                    + "	ant.imploja = " + SQLUtils.stringSQL(loja) + " and	\n"
                    + "	not ant.codigoatual is null\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    result.put(rst.getString("impid"), rst.getInt("codigoatual"));
                }
            }
        }

        return result;
    }
    
    /**
     * Retorna o código atual dos produtos através do código de importação.
     *
     * @param sistema Código do sistema importado.
     * @param loja Código da loja importada.
     * @return {@link Map} com os códigos dos produtos mapeados com os códigos
     * anteriores.
     * @throws Exception
     */
    public Map<String, Integer> getAnterioresIncluindoComCodigoAtualNull(String sistema, String loja) throws Exception {
        Map<String, Integer> result = new HashMap<>();

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	ant.impid,\n"
                    + "	ant.codigoatual\n"
                    + "from\n"
                    + "	implantacao.codant_produto ant\n"
                    + "where\n"
                    + "	ant.impsistema = " + SQLUtils.stringSQL(sistema) + " and\n"
                    + "	ant.imploja = " + SQLUtils.stringSQL(loja) + "\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    result.put(rst.getString("impid"), rst.getInt("codigoatual"));
                }
            }
        }

        return result;
    }

    public Map<String, ProdutoAnteriorVO> getAnterior(String sistema) throws Exception {
        Map<String, ProdutoAnteriorVO> result = new HashMap<>();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "impsistema, \n"
                    + "impid, \n"
                    + "imploja \n"
                    + "from implantacao.codant_produto \n"
                    + "where impsistema = " + SQLUtils.stringSQL(sistema) + "\n"
                    + "and codigoatual not in \n"
                    + "(select codigoatual from implantacao.codant_produto where impsistema <> " + SQLUtils.stringSQL(sistema) + ")"
            )) {
                while (rst.next()) {
                    ProdutoAnteriorVO vo = new ProdutoAnteriorVO();
                    vo.setImportSistema(rst.getString("impsistema"));
                    vo.setImportLoja(rst.getString("imploja"));
                    vo.setImportId(rst.getString("impid"));
                    result.put(rst.getString("impid"), vo);
                }
            }
        }
        return result;
    }
    
    public ProdutoAnteriorVO getProdutoAnterior(String sistema, String loja, String id) throws Exception {
        createTable();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + "	ant.impsistema, \n"
                    + "	ant.imploja, \n"
                    + "	ant.impid, \n"
                    + "	ant.descricao, \n"
                    + "	p.descricaocompleta, \n"
                    + "	p.descricaoreduzida, \n"
                    + "	p.descricaogondola, \n"
                    + "	ant.codigoatual, \n"
                    + "	ant.piscofinscredito, \n"
                    + "	ant.piscofinsdebito, \n"
                    + "	ant.piscofinsnaturezareceita, \n"
                    + "	ant.icmscst, \n"
                    + "	ant.icmsaliq, \n"
                    + "	ant.icmsreducao, \n"
                    + "	ant.estoque, \n"
                    + "	ant.e_balanca, \n"
                    + "	ant.custosemimposto, \n"
                    + "	ant.custocomimposto, \n"
                    + "	ant.margem, \n"
                    + "	ant.precovenda, \n"
                    + "	ant.ncm, \n"
                    + "	ant.cest,\n"
                    + "	ant.contadorimportacao,\n"
                    + "	ant.novo\n"
                    + "FROM \n"
                    + "	implantacao.codant_produto ant\n"
                    + "	left join produto p on ant.codigoatual = p.id\n"
                    + "where \n"
                    + "       ant.impsistema = " + SQLUtils.stringSQL(sistema) + " and\n"
                    + "       ant.imploja = " + SQLUtils.stringSQL(loja) + " and\n"
                    + "       ant.impid = " + SQLUtils.stringSQL(id) + "\n"
                    + "order by\n"
                    + "	ant.impsistema, \n"
                    + "	ant.imploja, \n"
                    + "	ant.impid"
            )) {
                if (rst.next()) {
                    ProdutoAnteriorVO vo = new ProdutoAnteriorVO();

                    vo.setImportSistema(rst.getString("impsistema"));
                    vo.setImportLoja(rst.getString("imploja"));
                    vo.setImportId(rst.getString("impid"));
                    vo.setDescricao(rst.getString("descricao"));
                    ProdutoVO produto = null;
                    if (rst.getString("codigoatual") != null) {
                        produto = new ProdutoVO();
                        produto.setId(rst.getInt("codigoatual"));
                        produto.setDescricaoCompleta("descricaocompleta");
                        produto.setDescricaoCompleta("descricaogondola");
                        produto.setDescricaoCompleta("descricaoreduzida");
                    }
                    vo.setCodigoAtual(produto);
                    vo.setPisCofinsCredito(rst.getInt("piscofinscredito"));
                    vo.setPisCofinsDebito(rst.getInt("piscofinsdebito"));
                    vo.setPisCofinsNaturezaReceita(rst.getInt("piscofinsnaturezareceita"));
                    vo.setIcmsCst(rst.getInt("icmscst"));
                    vo.setIcmsAliq(rst.getDouble("icmsaliq"));
                    vo.setIcmsReducao(rst.getDouble("icmsreducao"));
                    vo.setEstoque(rst.getDouble("estoque"));
                    vo.seteBalanca(rst.getBoolean("e_balanca"));
                    vo.setCustosemimposto(rst.getDouble("custosemimposto"));
                    vo.setCustocomimposto(rst.getDouble("custocomimposto"));
                    vo.setPrecovenda(rst.getDouble("precovenda"));
                    vo.setNcm(rst.getString("ncm"));
                    vo.setCest(rst.getString("cest"));
                    vo.setNovo(rst.getBoolean("novo"));
                    vo.setContadorImportacao(rst.getInt("contadorimportacao"));

                    return vo;
                }
            }
        }

        return null;
    }

    public ProdutoAnteriorVO getProdutoAnteriorSemUltimoDigito(String sistema, String loja, String id) throws Exception {
        createTable();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + "	ant.impsistema, \n"
                    + "	ant.imploja, \n"
                    + "	ant.impid, \n"
                    + "	ant.descricao, \n"
                    + "	p.descricaocompleta, \n"
                    + "	p.descricaoreduzida, \n"
                    + "	p.descricaogondola, \n"
                    + "	ant.codigoatual, \n"
                    + "	ant.piscofinscredito, \n"
                    + "	ant.piscofinsdebito, \n"
                    + "	ant.piscofinsnaturezareceita, \n"
                    + "	ant.icmscst, \n"
                    + "	ant.icmsaliq, \n"
                    + "	ant.icmsreducao, \n"
                    + "	ant.estoque, \n"
                    + "	ant.e_balanca, \n"
                    + "	ant.custosemimposto, \n"
                    + "	ant.custocomimposto, \n"
                    + "	ant.margem, \n"
                    + "	ant.precovenda, \n"
                    + "	ant.ncm, \n"
                    + "	ant.cest,\n"
                    + "	ant.contadorimportacao,\n"
                    + "	ant.novo\n"
                    + "FROM \n"
                    + "	implantacao.codant_produto ant\n"
                    + "	left join produto p on ant.codigoatual = p.id\n"
                    + "where \n"
                    + "       ant.impsistema = " + SQLUtils.stringSQL(sistema) + " and\n"
                    + "       ant.imploja = " + SQLUtils.stringSQL(loja) + " and\n"
                    + "       substring(ant.impid, 1, char_length(ant.impid) -1) = " + SQLUtils.stringSQL(id) + "\n"
                    + "order by\n"
                    + "	ant.impsistema, \n"
                    + "	ant.imploja, \n"
                    + "	ant.impid"
            )) {
                if (rst.next()) {
                    ProdutoAnteriorVO vo = new ProdutoAnteriorVO();

                    vo.setImportSistema(rst.getString("impsistema"));
                    vo.setImportLoja(rst.getString("imploja"));
                    vo.setImportId(rst.getString("impid"));
                    vo.setDescricao(rst.getString("descricao"));
                    ProdutoVO produto = null;
                    if (rst.getString("codigoatual") != null) {
                        produto = new ProdutoVO();
                        produto.setId(rst.getInt("codigoatual"));
                        produto.setDescricaoCompleta("descricaocompleta");
                        produto.setDescricaoCompleta("descricaogondola");
                        produto.setDescricaoCompleta("descricaoreduzida");
                    }
                    vo.setCodigoAtual(produto);
                    vo.setPisCofinsCredito(rst.getInt("piscofinscredito"));
                    vo.setPisCofinsDebito(rst.getInt("piscofinsdebito"));
                    vo.setPisCofinsNaturezaReceita(rst.getInt("piscofinsnaturezareceita"));
                    vo.setIcmsCst(rst.getInt("icmscst"));
                    vo.setIcmsAliq(rst.getDouble("icmsaliq"));
                    vo.setIcmsReducao(rst.getDouble("icmsreducao"));
                    vo.setEstoque(rst.getDouble("estoque"));
                    vo.seteBalanca(rst.getBoolean("e_balanca"));
                    vo.setCustosemimposto(rst.getDouble("custosemimposto"));
                    vo.setCustocomimposto(rst.getDouble("custocomimposto"));
                    vo.setPrecovenda(rst.getDouble("precovenda"));
                    vo.setNcm(rst.getString("ncm"));
                    vo.setCest(rst.getString("cest"));
                    vo.setNovo(rst.getBoolean("novo"));
                    vo.setContadorImportacao(rst.getInt("contadorimportacao"));

                    return vo;
                }
            }
        }

        return null;
    }

    public ProdutoAnteriorVO getProdutoAnteriorUnificado(String sistema, String id) throws Exception {
        createTable();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT distinct\n"
                    + "	ant.impsistema, \n"
                    + "	ant.impid, \n"
                    + "	ant.codigoatual \n"
                    + "FROM \n"
                    + "	implantacao.codant_produto ant\n"
                    + "where \n"
                    + "       ant.impsistema = " + SQLUtils.stringSQL(sistema) + " and\n"
                    + "       ant.impid = " + SQLUtils.stringSQL(id) + "\n and\n"
                    + "       ant.codigoatual is not null\n"        
                    + "order by\n"
                    + "	ant.impsistema, \n"
                    + "	ant.impid"
            )) {
                if (rst.next()) {
                    ProdutoAnteriorVO vo = new ProdutoAnteriorVO();
                    vo.setImportSistema(rst.getString("impsistema"));
                    vo.setImportId(rst.getString("impid"));
                    ProdutoVO produto = null;
                    if (rst.getString("codigoatual") != null) {
                        produto = new ProdutoVO();
                        produto.setId(rst.getInt("codigoatual"));
                    }
                    vo.setCodigoAtual(produto);

                    return vo;
                }
            }
        }

        return null;
    }
    
    public void atualizarForcarNovo() throws Exception {
        forcarNovo = new MultiMap<>(3);
        criarColunaCodAnt();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n" +
                    "	impsistema,\n" +
                    "	imploja,\n" +
                    "	impid\n" +
                    "from \n" +
                    "	implantacao.codant_produto\n" +
                    "where \n" +
                    "	impsistema = '" + getImportSistema() + "' and \n" +
                    "	imploja = '" + getImportLoja() + "' and \n" +
                    "	forcarnovo is true"
            )) {
                while (rst.next()) {
                    ProdutoAnteriorVO vo = new ProdutoAnteriorVO();
                    
                    vo.setImportSistema(rst.getString("impsistema"));
                    vo.setImportLoja(rst.getString("imploja"));
                    vo.setImportId(rst.getString("impid"));
                    
                    forcarNovo.put(vo, vo.getChave());
                }
            }
        }
    }
    
    private void criarColunaCodAnt() throws Exception {
        try(Statement stm = Conexao.createStatement()) {
            stm.execute(
                    "do $$\n" +
                    "begin\n" +
                    "	if not exists (\n" +
                    "       select \n" +
                    "		* \n" +
                    "	    from \n" +
                    "		information_schema.columns\n" +
                    "       where \n" +
                    "		table_schema = 'implantacao' and \n" +
                    "		column_name = 'forcarnovo') then \n" +
                    "	execute 'alter table implantacao.codant_produto add column forcarnovo boolean default false'; \n" +
                    "end if;\n" +
                    "end\n" +
                    "$$");
        }
    }

    public MultiMap<String, Integer> getAnterioresPorIdEan(String sistema, String loja) throws Exception {
        MultiMap<String, Integer> result = new MultiMap<>();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	antean.importid,\n" +
                    "	antean.ean,\n" +
                    "	ant.codigoatual\n" +
                    "from\n" +
                    "	implantacao.codant_produto ant\n" +
                    "	join implantacao.codant_ean antean on\n" +
                    "		ant.impsistema = antean.importsistema and\n" +
                    "		ant.imploja = antean.importloja and\n" +
                    "		ant.impid = antean.importid \n" +
                    "where\n" +
                    "	ant.impsistema = '" + sistema + "' and\n" +
                    "	ant.imploja = '" + loja + "'\n" +
                    "order by\n" +
                    "	1, 2"
            )) {
                while (rst.next()) {
                    result.put(
                            rst.getInt("codigoatual") == 0 ? null : rst.getInt("codigoatual"),
                            rst.getString("importid"),
                            rst.getString("ean")
                    );
                }
            }
        }
        return result;
    }
    
    public int verificaRegistro() throws Exception {
        try(Statement stm = Conexao.createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    /*"select \n" +
                    "	count(*) qtd \n" +
                    "from \n" +
                    "	implantacao.codant_produto \n" +
                    "where \n" +
                    "	codigoatual in (select codigoatual from implantacao.codant_produto limit 100)"*/
                    "select\n" +
                    "	count(*) qtd\n" +
                    "	from\n" +
                    "implantacao.codant_produto\n" +
                    "where codigoatual is not null"
            )) {
                if (rs.next()) {
                    return rs.getInt("qtd");
                } else {
                    return 0;
                }
            }
        }
    }
    
    public int getConexaoMigrada(int idConexao, String sistema) throws Exception {
        int conexao = 0;
        
        try(Statement stm = Conexao.createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	id_conexao \n" +
                    "from \n" +
                    "	implantacao.codant_produto\n" +
                    "where \n" +
                    "	impsistema = " + SQLUtils.stringSQL(sistema) + " and\n" +
                    "   id_conexao = " + idConexao + " limit 1")) {
                if (rs.next()) {
                    conexao = rs.getInt("id_conexao");
                }
            }
        }
        
        return conexao;
    }
    
    public boolean verificaMigracaoMultiloja(String lojaOrigem, String sistema, int idConexao) throws Exception {
        boolean conexao = false;
        
        try(Statement stm = Conexao.createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	id_conexao \n" +
                    "from \n" +
                    "	implantacao.codant_produto\n" +
                    "where \n" +
                    "	impsistema = " + SQLUtils.stringSQL(sistema) + " and\n" +
                    "   id_conexao = " + idConexao + " limit 1")) {
                if (rs.next()) {
                    conexao = true;
                }
            }
        }
        
        return conexao;
    }
    
    public boolean verificaMultilojaMigrada(String lojaOrigem, String sistema, int idConexao) throws Exception {
        boolean lojaJaMigrada = false;
        
        try(Statement stm = Conexao.createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	id_conexao \n" +
                    "from \n" +
                    "	implantacao.codant_produto\n" +
                    "where \n" +
                    "	impsistema = " + SQLUtils.stringSQL(sistema) + " and\n" +
                    "   imploja = " + SQLUtils.stringSQL(lojaOrigem) + "and\n" +        
                    "   id_conexao = " + idConexao + " limit 1")) {
                if (rs.next()) {
                    lojaJaMigrada = true;
                }
            }
        }
        
        return lojaJaMigrada;
    }
    
    public String getLojaModelo(int idConexao, String sistema) throws Exception {
        String loja = "";
        
        try(Statement stm = Conexao.createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	imploja\n" +
                    "from \n" +
                    "	implantacao.codant_produto \n" +
                    "where \n" +
                    "	impsistema = '" + sistema + "' and \n" +
                    "	id_conexao = " + idConexao + "\n" +
                    "limit 1")) {
                if(rs.next()) {
                    loja = rs.getString("imploja");
                }
            }
        }
        
        return loja;
    }
    
    public void copiarCodantProduto(String sistema, String lojaModelo, String lojaNova) throws Exception {
        
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
                "				false,\n" +
                "                               id_conexao\n" +
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
                "				false,\n" +
                "                               id_conexao\n" +
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
                "			novo,\n" +
                "                       codigosped,\n" +
                "                       situacaocadastro,\n" +
                "                       dataimportacao,\n" +
                "                       obsimportacao,\n" +
                "                       icmscstsaida,\n" +
                "                       icmsaliqsaida,\n" +
                "                       icmsreducaosaida,\n" +
                "                       icmscstsaidaforaestado,\n" +
                "                       icmsaliqsaidaforaestado,\n" +
                "                       icmsreducaosaidaforaestado,\n" +
                "                       icmscstsaidaforaestadonf,\n" +
                "                       icmsaliqsaidaforaestadonf,\n" +
                "                       icmsreducaosaidaforaestadonf,\n" +
                "                       icmscstentrada,\n" +
                "                       icmsaliqentrada,\n" +
                "                       icmsreducaoentrada,\n" +
                "                       icmscstentradaforaestado,\n" +
                "                       icmsaliqentradaforaestado,\n" +
                "                       icmsreducaoentradaforaestado,\n" +
                "                       icmscstconsumidor,\n" +
                "                       icmsaliqconsumidor,\n" +
                "                       icmsreducaoconsumidor,\n" +
                "                       icmsdebitoid,\n" +
                "                       icmsdebitoforaestadoid,\n" +
                "                       icmsdebitoforaestadonfid,\n" +
                "                       icmscreditoid,\n" +
                "                       icmscreditoforaestadoid,\n" +
                "                       icmsconsumidorid,\n" +
                "                       datacadastro,\n" +
                "                       id_conexao\n" +
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
                "				fantasia,\n" +
                "                               id_conexao\n" +
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
                "				orig_reduzido,\n" +
                "                               orig_fcp,\n" +
                "                               orig_desonerado,\n" +
                "                               orig_porcentagemdesonerado\n" +
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
    
    public String getImpSistema() throws Exception {
        String loja = "";
        
        try(Statement stm = Conexao.createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	impsistema\n" +
                    "from \n" +
                    "	implantacao.codant_produto\n" +
                    "where \n" +
                    "	id_conexao = (select min(id_conexao) from implantacao.codant_produto)\n" +
                    "limit 1")) {
                if(rs.next()) {
                    loja = rs.getString("impsistema");
                }
            }
        }
        
        return loja;
    }
}
