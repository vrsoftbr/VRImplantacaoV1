package vrimplantacao2.dao.cadastro.promocao;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.logging.Logger;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.utils.sql.SQLUtils;
import vrimplantacao2.vo.cadastro.pdv.promocao.PromocaoAnteriorVO;
import vrimplantacao2.vo.cadastro.pdv.promocao.PromocaoVO;

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
                    + "	impid varchar NULL,\n"
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
            sql.put("impid", anterior.getId_promocao());
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

    void copiarPromocaoItensFinalizadora(String sistema, String loja) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute(
                    "do $$\n"
                    + "	declare\n"
                    + "		v_id_loja integer;\n"
                    + "		v_id_promo integer;\n"
                    + "		rx record;\n"
                    + "	begin\n"
                    + "		v_id_loja = " + loja + "; --Código da loja\n"
                    + "	\n"
                    + "		--Insere a promoção e retorna o id\n"
                    + "		for rx in\n"
                    + "		(select\n"
                    + "			distinct\n"
                    + "			id_promocao::integer id,\n"
                    + "			" + loja + " id_loja,\n"
                    + "			descricao, \n"
                    + "			datainicio::date datainicio, \n"
                    + "			datatermino::date datatermino, \n"
                    + "			0 pontuacao, \n"
                    + "			quantidade::numeric(12,3) quantidade, \n"
                    + "			0::integer qtdcupom, \n"
                    + "			1::integer id_situacaocadastro, \n"
                    + "			1::integer id_tipopromocao, \n"
                    + "			0::numeric(11, 2) valor,\n"
                    + "			id_promocao::integer controle, \n"
                    + "			0::integer id_tipopercentualvalor, \n"
                    + "			1::integer id_tipoquantidade, \n"
                    + "			false::boolean aplicatodos, \n"
                    + "			null::varchar(2000) cupom, \n"
                    + "			0::numeric(11,2) valordesconto, \n"
                    + "			false::boolean valorreferenteitenslista, \n"
                    + "			false::boolean verificaprodutosauditados, \n"
                    + "			null::date datalimiteresgatecupom, \n"
                    + "			0::integer id_tipopercentualvalordesconto, \n"
                    + "			paga::numeric(11,2) valorpaga,\n"
                    + "			false::boolean utilizaquantidadeproporcional\n"
                    + "		FROM \n"
                    + "			implantacao.codant_promocao) loop\n"
                    + "		\n"
                    + "		INSERT INTO promocao(\n"
                    + "	            id, id_loja, descricao, datainicio, datatermino, pontuacao, quantidade, \n"
                    + "	            qtdcupom, id_situacaocadastro, id_tipopromocao, valor, controle, \n"
                    + "	            id_tipopercentualvalor, id_tipoquantidade, aplicatodos, cupom, \n"
                    + "	            valordesconto, valorreferenteitenslista, verificaprodutosauditados, \n"
                    + "	            datalimiteresgatecupom, id_tipopercentualvalordesconto, \n"
                    + "	            valorpaga, utilizaquantidadeproporcional)\n"
                    + "		VALUES (\n"
                    + "			rx.id, \n"
                    + "			rx.id_loja,\n"
                    + "			rx.descricao, \n"
                    + "			rx.datainicio, \n"
                    + "			rx.datatermino, \n"
                    + "			rx.pontuacao, \n"
                    + "			rx.quantidade, \n"
                    + "			rx.qtdcupom, \n"
                    + "			rx.id_situacaocadastro, \n"
                    + "			rx.id_tipopromocao, \n"
                    + "			rx.valor, \n"
                    + "			rx.controle, \n"
                    + "			rx.id_tipopercentualvalor, \n"
                    + "			rx.id_tipoquantidade, \n"
                    + "			rx.aplicatodos, \n"
                    + "			rx.cupom, \n"
                    + "			rx.valordesconto, \n"
                    + "			rx.valorreferenteitenslista, \n"
                    + "			rx.verificaprodutosauditados, \n"
                    + "			rx.datalimiteresgatecupom, \n"
                    + "			rx.id_tipopercentualvalordesconto, \n"
                    + "			rx.valorpaga,\n"
                    + "			rx.utilizaquantidadeproporcional) returning id into v_id_promo;\n"
                    + "			\n"
                    + "		--Insere o item\n"
                    + "		insert into promocaoitem(id_promocao, id_produto, precovenda)\n"
                    + "		select\n"
                    + "			distinct\n"
                    + "			v_id_promo,\n"
                    + "			pr.id,\n"
                    + "			0::numeric precovenda\n"
                    + "		from \n"
                    + "			implantacao.codant_promocao p \n"
                    + "		inner join implantacao.codant_produto imp on p.id_produto = imp.impid \n"
                    + "		inner join produto pr on imp.codigoatual = pr.id\n"
                    + "		where\n"
                    + "			p.id_promocao::integer = rx.id and \n"
                    + "			imp.impsistema = '" + sistema + "';\n"
                    + "			\n"
                    + "		--Insere a finalizadora\n"
                    + "		insert into promocaofinalizadora(id_promocao, id_finalizadora)\n"
                    + "		select \n"
                    + "			v_id_promo,\n"
                    + "			id \n"
                    + "		from \n"
                    + "			pdv.finalizadora;\n"
                    + "		end loop;\n"
                    + "	end;\n"
                    + "$$;");
        }
    }
    
    // precisa tratar id
int conta = 0;
    MultiMap<String, PromocaoAnteriorVO> getAnteriores(String sistema, String lojaOrigem) throws Exception {
        MultiMap<String, PromocaoAnteriorVO> result = new MultiMap<>();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	ant.sistema,\n"
                    + "	ant.loja,\n"
                    + "	ant.id_conexao,\n"
                    + "	ant.id_promocao,\n"
                    + "	ant.descricao,\n"
                    + "	ant.datainicio,\n"
                    + "	ant.datatermino,\n"
                    + "	ant.ean,\n"
                    + "	ant.id_produto,\n"
                    + "	ant.descricaocompleta,\n"
                    + "	ant.quantidade,\n"
                    + "	ant.paga\n"
                    + "from \n"
                    + "	implantacao.codant_promocao ant\n"
                    + "where\n"
                    + "	ant.sistema = " + SQLUtils.stringSQL(sistema) + " and\n"
                    + "	ant.loja = " + SQLUtils.stringSQL(lojaOrigem) + "\n"
                    + "order by\n"
                    + "	ant.sistema,\n"
                    + "	ant.loja,"
                    + "ant.impid"
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
                    result.put(
                            vo,
                            vo.getSistema(),
                            vo.getLoja()
                    );
                }
            }
        }
        return result;
    }
}