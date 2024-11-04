package vrimplantacao2.dao.cadastro.venda;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import vr.core.parametro.versao.Versao;
import vrframework.classe.Conexao;
import vrimplantacao.utils.Utils;
import vrimplantacao2.utils.MathUtils;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.utils.sql.SQLUtils;
import vrimplantacao2.vo.cadastro.venda.PdvVendaPromocaoPontuacaoVO;
import vrimplantacao2.vo.cadastro.venda.PdvVendaVO;

/**
 *
 * @author Leandro
 */
public class PdvVendaDAO {

    private static final Logger LOG = Logger.getLogger(PdvVendaDAO.class.getName());

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("hh:mm:ss");

    private final Versao versao = Versao.createFromConnectionInterface(Conexao.getConexao());

    public PdvVendaDAO() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute(
                    "create table if not exists implantacao.vendasimportadas(\n"
                    + "	id_venda integer not null primary key\n"
                    + ");"
            );
        }
    }

    public boolean validaColunaBaixaEstoque() throws Exception {
        String query = "SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS "
                + "WHERE TABLE_SCHEMA = 'pdv' AND TABLE_NAME = 'venda' AND COLUMN_NAME = 'baixaestoque'";

        try (PreparedStatement pst = Conexao.prepareStatement(query)) {
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            return false;
        }
        return false;
    }

    public void gravar(PdvVendaVO vo) throws Exception {

        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();

            sql.setSchema("pdv");
            sql.setTableName("venda");
            sql.getReturning().add("id");

            sql.put("id_loja", vo.getId_loja());
            sql.put("numerocupom", vo.getNumeroCupom());
            sql.put("ecf", vo.getEcf());
            sql.put("data", vo.getData());
            sql.put("id_clientepreferencial", vo.getId_clientePreferencial(), 0);
            sql.put("matricula", vo.getMatricula(), 0);
            sql.put("horainicio", vo.getHoraInicio());
            sql.put("horatermino", vo.getHoraTermino());
            sql.put("cancelado", vo.isCancelado());
            sql.put("subtotalimpressora", vo.getSubTotalImpressora());
            sql.put("matriculacancelamento", vo.getMatriculaCancelamento(), 0);
            if (vo.getTipoCancelamento() != null) {
                sql.put("id_tipocancelamento", vo.getTipoCancelamento().getId());
            }
            sql.put("cpf", vo.getCpf());
            sql.put("contadordoc", vo.getContadorDoc());
            sql.put("valordesconto", vo.getValorDesconto());
            sql.put("valoracrescimo", vo.getValorAcrescimo());
            sql.put("canceladoemvenda", vo.isCanceladoEmVenda());
            sql.put("numeroserie", vo.getNumeroSerie());
            sql.put("mfadicional", vo.getMfAdicional());
            sql.put("modeloimpressora", vo.getModeloImpressora());
            sql.put("numerousuario", vo.getNumeroUsuario());
            sql.put("nomecliente", vo.getNomeCliente());
            sql.put("enderecocliente", vo.getEnderecoCliente());
            sql.put("id_clienteeventual", vo.getId_clienteEventual(), 0);
            sql.put("chavecfe", vo.getChaveCfe());
            sql.put("cpfcrm", vo.getCpfCrm(), 0);
            sql.put("cpfcnpjentidade", vo.getCpfCnpjEntidade(), 0);
            sql.put("razaosocialentidade", vo.getRazaoSocialEntidade());
            sql.put("chavenfce", vo.getChaveNfce());
            if (vo.getTipoDesconto() != null) {
                sql.put("id_tipodesconto", vo.getTipoDesconto().getId());
            }

            if (versao.igualOuMaiorQue(4, 1, 0)) {
                sql.put("vendaecommercemercafacil", false);
                sql.put("vendaecommercesitemercado", false);
                sql.put("vendaecommerceapi", false);
                sql.put("cupomverde", false);
                //sql.put("baixaestoque", false);
                //sql.put("chavenfcecontingencia", vo.getChaveNfceContingencia());
            }
            if (validaColunaBaixaEstoque()) {
                sql.put("baixaestoque", false);
            };

            LOG.finer(
                    "Incluindo a Venda { "
                    + "ecf:" + vo.getEcf() + ","
                    + "cupom: " + vo.getNumeroCupom() + ","
                    + "data:" + SQLUtils.stringSQL(DATE_FORMAT.format(vo.getData())) + ","
                    + "horainicio:" + SQLUtils.stringSQL(TIME_FORMAT.format(vo.getHoraInicio())) + ","
                    + "subtotalimpressora: " + String.format("%.2f", vo.getSubTotalImpressora())
                    + "}"
            );
            String sqlInsert = sql.getInsert();

            try (ResultSet rst = stm.executeQuery(sqlInsert)) {
                while (rst.next()) {
                    vo.setId(rst.getLong("id"));
                }
                LOG.finest(sqlInsert);
            }
        }

    }

    public void eliminarVenda(long idVenda) throws Exception {

        try (Statement stm = Conexao.createStatement()) {
            StringBuilder builder = new StringBuilder();

            builder.append("DELETE FROM pdv.vendakititem WHERE id_vendakit IN (SELECT id FROM pdv.vendakit WHERE id_venda = ").append(idVenda).append(");");
            builder.append("DELETE FROM pdv.vendakit WHERE id_venda = ").append(idVenda).append(";");
            builder.append("DELETE FROM pdv.vendapromocao WHERE id_venda = ").append(idVenda).append(";");
            builder.append("DELETE FROM pdv.vendapromocaocupom WHERE id_venda = ").append(idVenda).append(";");
            builder.append("DELETE FROM pdv.vendafinalizadora WHERE id_venda = ").append(idVenda).append(";");
            builder.append("DELETE FROM pdv.vendaitem WHERE id_venda = ").append(idVenda).append(";");
            builder.append("DELETE FROM pdv.venda WHERE id = ").append(idVenda).append(";");

            stm.execute(builder.toString());
        }

    }

    public long vendaExistente(int id_loja, int ecf, int numeroCupom, Date data, double subTotalImpressora) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select id from pdv.venda\n"
                    + "where \n"
                    + "	id_loja = " + id_loja + " and\n"
                    + "	numerocupom = " + numeroCupom + " and\n"
                    + "	ecf = " + ecf + " and\n"
                    + "	data = " + SQLUtils.stringSQL(DATE_FORMAT.format(data)) + " and\n"
                    + "	trunc(subtotalimpressora, 2) = " + MathUtils.trunc(subTotalImpressora, 2)
            )) {
                if (rst.next()) {
                    return rst.getLong("id");
                }
            }
        }
        return -1;
    }

    public int getMatricula(int lojaVR) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT matricula FROM pdv.operador WHERE id_loja = " + lojaVR + " and upper(nome) like '%ADMIN%' and id_situacaocadastro = 1\n"
                    + "union\n"
                    + "SELECT matricula FROM pdv.operador WHERE id_loja = " + lojaVR + " and upper(nome) like '%ADMIN%'\n"
                    + "limit 1"
            )) {
                if (rst.next()) {
                    return rst.getInt("matricula");
                }
            }
        }
        return 50001;
    }

    public void logarVendaImportadas(long id_venda) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute("insert into implantacao.vendasimportadas values (" + id_venda + ")");
        }
    }

    public void gerarMapaResumo(int idLojaVR) throws Exception {
        LOG.info("Gerando o mapa resumo das vendas importadas");
        try (Statement stm = Conexao.createStatement()) {
            stm.execute(
                    "insert into maparesumo (\n"
                    + "	id_loja,\n"
                    + "	data,\n"
                    + "	ecf,\n"
                    + "	reducao,\n"
                    + "	contadorinicial,\n"
                    + "	contadorfinal,\n"
                    + "	gtinicial,\n"
                    + "	gtfinal,\n"
                    + "	cancelamento,\n"
                    + "	desconto,\n"
                    + "	lancamentomanual,\n"
                    + "	contadorreinicio,\n"
                    + "	datahoraemissaorz,\n"
                    + "	contadorgerencial,\n"
                    + "	contadorcdc,\n"
                    + "	totalnaofiscal,\n"
                    + "	acrescimo,\n"
                    + "	lancamentorfd\n"
                    + ")\n"
                    + "select\n"
                    + "	mapa.*\n"
                    + "from\n"
                    + "	(select\n"
                    + "		v.id_loja,\n"
                    + "		v.data,\n"
                    + "		v.ecf,\n"
                    + "		1 reducao,\n"
                    + "		min(vi.contadordoc) contadorinicial,\n"
                    + "		max(vi.contadordoc) contadorfinal,\n"
                    + "		0 gtinicial,\n"
                    + "		coalesce(nullif(sum(vi.valortotal),0) + sum(vi.valoracrescimo), 1) gtfinal,\n"
                    + "		sum(vi.valorcancelado) cancelamento,\n"
                    + "		sum(vi.valordescontopromocao + vi.valordesconto) desconto,\n"
                    + "		true as lancamentomanual,\n"
                    + "		max(vi.contadordoc) contadorreinicio,\n"
                    + "		max(v.data + v.horatermino) as datahoraemissaorz,\n"
                    + "		0 contadorgerencial,\n"
                    + "		0 contadorcdc,\n"
                    + "		0 totalnaofiscal,\n"
                    + "		sum(vi.valoracrescimo) acrescimo,\n"
                    + "		false lancamentorfd\n"
                    + "	from \n"
                    + "		pdv.venda v\n"
                    + "		join pdv.vendaitem vi on\n"
                    + "			vi.id_venda = v.id\n"
                    + "	where\n"
                    + "		id_loja = " + idLojaVR + "\n"
                    + "		and v.id in (select id_venda from implantacao.vendasimportadas)\n"
                    + "	group by\n"
                    + "		v.id_loja, v.data, v.ecf) mapa\n"
                    + "	left join maparesumo mp on\n"
                    + "		mapa.id_loja = mp.id_loja\n"
                    + "		and mapa.ecf = mp.ecf\n"
                    + "		and mapa.data = mp.data\n"
                    + "where\n"
                    + "	mp.id is null\n"
                    + "order by\n"
                    + "	mapa.id_loja, mapa.data, mapa.ecf"
            );
            LOG.fine("Mapas resumos gerados");
            stm.execute(
                    "insert into maparesumoitem (id_maparesumo, id_aliquota, valor)\n"
                    + "select\n"
                    + "	mp.id id_maparesumo,	\n"
                    + "	aliq.id_aliquota,\n"
                    + "	aliq.valor\n"
                    + "from\n"
                    + "	(select\n"
                    + "		v.id_loja,\n"
                    + "		v.data,\n"
                    + "		v.ecf,\n"
                    + "		pdvaliq.id_aliquota,\n"
                    + "		sum(vi.valortotal - vi.valorcancelado - (vi.valordesconto + vi.valordescontopromocao) + vi.valoracrescimo) valor\n"
                    + "	from \n"
                    + "		pdv.venda v\n"
                    + "		join pdv.vendaitem vi on\n"
                    + "			vi.id_venda = v.id\n"
                    + "		left join aliquota al on\n"
                    + "			vi.id_aliquota = al.id\n"
                    + "		join pdv.aliquota pdvaliq on\n"
                    + "			pdvaliq.id = al.id_aliquotapdv\n"
                    + "	where\n"
                    + "		id_loja = " + idLojaVR + "\n"
                    + "		and v.id in (select id_venda from implantacao.vendasimportadas)\n"
                    + "	group by\n"
                    + "		v.id_loja,v.data,v.ecf,pdvaliq.id_aliquota\n"
                    + "	order by 1,2,3,4) aliq\n"
                    + "	join maparesumo mp on\n"
                    + "		aliq.id_loja = mp.id_loja\n"
                    + "		and aliq.data = mp.data\n"
                    + "		and aliq.ecf = mp.ecf	\n"
                    + "	left join maparesumoitem mpi on\n"
                    + "		mpi.id_maparesumo = mp.id\n"
                    + "		and mpi.id_aliquota = aliq.id_aliquota\n"
                    + "where\n"
                    + "	mpi.id is null\n"
                    + "order by\n"
                    + "	mp.id, aliq.id_aliquota"
            );
            LOG.fine("Itens do mapa resumo gerados");
        }
        LOG.info("Mapa resumo gerado com sucesso");
    }

    public void gerarECFs(int idLojaVR) throws Exception {
        LOG.info("Criando ECFs");
        try (Statement stm = Conexao.createStatement()) {
            stm.execute(
                    "insert into pdv.ecf (\n"
                    + "	id_loja,\n"
                    + "	ecf,\n"
                    + "	descricao,\n"
                    + "	id_tipomarca,\n"
                    + "	id_tipomodelo,\n"
                    + "	id_situacaocadastro,\n"
                    + "	numeroserie,\n"
                    + "	mfadicional,\n"
                    + "	numerousuario,\n"
                    + "	tipoecf,\n"
                    + "	versaosb,\n"
                    + "	datahoragravacaosb,\n"
                    + "	datahoracadastro,\n"
                    + "	incidenciadesconto,\n"
                    + "	versaobiblioteca,\n"
                    + "	geranfpaulista,\n"
                    + "	id_tipoestado,\n"
                    + "	versao,\n"
                    + "	datamovimento,\n"
                    + "	cargagdata,\n"
                    + "	cargaparam,\n"
                    + "	cargalayout,\n"
                    + "	cargaimagem,\n"
                    + "	id_tipolayoutnotapaulista\n"
                    + ")\n"
                    + "select\n"
                    + "	v.id_loja,\n"
                    + "	v.ecf,\n"
                    + "	'IMPORTADO VR ' || v.ecf as descricao,\n"
                    + "	999 as id_tipomarca,\n"
                    + "	999 as id_tipomodelo,\n"
                    + "	1 as id_situacaocadastro,\n"
                    + "	v.ecf numeroserie,\n"
                    + "	'N' as mfadicional,\n"
                    + "	1 as numerousuario,\n"
                    + "	'ECF-IF' tipoecf,\n"
                    + "	'00.00.00' versaosb,\n"
                    + "	current_timestamp as datahoragravacaosb,\n"
                    + "	current_timestamp as datahoracadastro,\n"
                    + "	false as incidenciadesconto,\n"
                    + "	0 as versaobiblioteca,\n"
                    + "	false as geranfpaulista,\n"
                    + "	0 as id_tipoestado,\n"
                    + "	'' as versao,\n"
                    + "	null as datamovimento,\n"
                    + "	false as cargagdata,\n"
                    + "	false as cargaparam,\n"
                    + "	false as cargalayout,\n"
                    + "	false as cargaimagem,\n"
                    + "	0 as id_tipolayoutnotapaulista\n"
                    + "from\n"
                    + "	(select\n"
                    + "		v.id_loja,\n"
                    + "		v.ecf	\n"
                    + "	from\n"
                    + "		pdv.venda v\n"
                    + "		join implantacao.vendasimportadas impv on\n"
                    + "			v.id = impv.id_venda\n"
                    + "	where v.id_loja = " + idLojaVR + "\n"
                    + "	group by\n"
                    + "		id_loja, ecf) v\n"
                    + "	left join pdv.ecf ecf on\n"
                    + "		v.id_loja = ecf.id_loja\n"
                    + "		and v.ecf = ecf.ecf\n"
                    + "where\n"
                    + "	ecf.id is null\n"
                    + "order by\n"
                    + "	id_loja, ecf;"
            );
        }
        LOG.info("ECFs criadas");
    }

    public void gerarRegistrosGenericos() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute(
                    "do $$\n"
                    + "declare\n"
                    + "begin\n"
                    + "	if (not exists(select id from pdv.tipomarca where id = 999)) then\n"
                    + "		insert into pdv.tipomarca values (999, 'IMPORTACAO', 'IM');\n"
                    + "	end if;\n"
                    + "	if (not exists(select id from pdv.tipomodelo where id = 999)) then\n"
                    + "		insert into pdv.tipomodelo values (999, 999, 'IMPORTACAO VR', 0, false);\n"
                    + "	end if;\n"
                    + "end;\n"
                    + "$$;"
            );
        }
    }

    public void eliminarVenda(int id_loja, int ecf, int numeroCupom, Date data, double subTotalImpressora) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            String sql
                    = "do $$\n"
                    + "declare\n"
                    + "	v_id integer;\n"
                    + "begin\n"
                    + "	select id from pdv.venda \n"
                    + "	where  \n"
                    + "		id_loja = " + id_loja + " and \n"
                    + "		numerocupom = " + numeroCupom + " and \n"
                    + "		ecf = " + ecf + " and \n"
                    + "		data = " + SQLUtils.stringSQL(DATE_FORMAT.format(data)) + "\n"
                    + "	into v_id;\n"
                    + "	if ( not v_id is null ) then\n"
                    + "       DELETE FROM pdv.vendacfe WHERE id_venda = v_id;\n"
                    + "       DELETE FROM pdv.vendakititem WHERE id_vendakit IN (SELECT id FROM pdv.vendakit WHERE id_venda = v_id);\n"
                    + "       DELETE FROM pdv.vendakit WHERE id_venda = v_id;\n"
                    + "       DELETE FROM pdv.vendapromocao WHERE id_venda = v_id;\n"
                    + "       DELETE FROM pdv.vendapromocaocupom WHERE id_venda = v_id;\n"
                    + "       DELETE FROM pdv.vendafinalizadora WHERE id_venda = v_id;\n"
                    + "       DELETE FROM pdv.vendaitem WHERE id_venda = v_id;\n"
                    + "       DELETE FROM pdv.vendanfce WHERE id_venda = v_id;\n"
                    + "       DELETE FROM pdv.venda WHERE id = v_id;\n"
                    + "   end if;"
                    + "end;\n"
                    + "$$;";

            LOG.finer(sql);
            stm.execute(sql);
        }
    }

    public Integer encontrarVenda(int id_loja, int ecf, int numeroCupom, Date data, double subTotalImpressora) throws Exception {
        try (
                Statement st = Conexao.createStatement();
                ResultSet rs = st.executeQuery(
                        "	select id from pdv.venda \n"
                        + "	where\n"
                        + "		id_loja = " + id_loja + " and \n"
                        + "		numerocupom = " + numeroCupom + " and \n"
                        + "		ecf = " + ecf + " and \n"
                        + "		data = " + SQLUtils.stringSQL(DATE_FORMAT.format(data)) + "\n"
                        + "limit 1"
                )) {
            if (rs.next()) {
                return rs.getInt("id");
            }
        }
        return null;
    }

    public List<Integer> getIdsPorData(int idLoja, Date dt) throws Exception {
        List<Integer> result = new ArrayList<>();

        try (Statement st = Conexao.createStatement()) {
            try (ResultSet rs = st.executeQuery(
                    "select\n"
                    + "	id\n"
                    + "from\n"
                    + "	pdv.venda v\n"
                    + "where\n"
                    + "	v.data = " + Utils.dateSQL(dt) + " and\n"
                    + "	v.id_loja = " + idLoja + "\n"
                    + "order by\n"
                    + "	id desc"
            )) {
                while (rs.next()) {
                    result.add(rs.getInt("id"));
                }
            }
        }

        return result;
    }

    public void cleanerVenda(int id) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            String sql
                    = "do $$\n"
                    + "	declare\n"
                    + "		v_id integer = " + id + ";\n"
                    + "		v_iditem integer;\n"
                    + "	begin\n"
                    + "	   select id from pdv.vendaitem where id_venda = v_id into v_iditem;\n"
                    + "	   delete from scanntech.vendaitem where id_venda = v_id;\n"
                    + "      delete from scanntech.venda where id_venda = v_id;\n"
                    + "	   delete from balanca.vendaoperadorbalanca where id_vendaitem = v_iditem;\n"
                    + "	   delete from pdv.perguntanota where id_venda = v_id;\n"
                    + "	   delete from crescevendas.vendacrescevendas where id_venda = v_id;\n"
                    + "	   delete from crescevendas.vendaitem where id_vendaitem = v_iditem;	\n"
                    + "	   delete from public.clientepreferencialpromocao where id_venda = v_id;\n"
                    + "	   delete from comissao.venda where id_venda = v_id;\n"
                    + "	   delete from connect.vendaefetuada where id_venda = v_id;\n"
                    + "	   delete from pdv.vendaenviocupomcfe where id_venda = v_id;\n"
                    + "	   delete from pdv.vendapromocao where id_venda = v_id;\n"
                    + "	   delete from pdv.vendapontuacao where id_venda = v_id;\n"
                    + "	   delete from pdv.vendapromocaopontuacao where id_venda = v_id;\n"
                    + "	   delete from pdv.vendapromocaoproduto where id_venda = v_id;\n"
                    + "	   delete from pdv.vendacfe where id_venda = v_id;        \n"
                    + "	   delete from pdv.vendakititem where id_vendakit in (select id from pdv.vendakit where id_venda = v_id);\n"
                    + "	   delete from pdv.vendakit where id_venda = v_id;\n"
                    + "	   delete from pdv.vendapromocao where id_venda = v_id;\n"
                    + "	   delete from pdv.vendapromocaocupom where id_venda = v_id;\n"
                    + "	   delete from pdv.vendapdvvendatef where id_venda = v_id;\n"
                    + "	   delete from pdv.vendafinalizadora where id_venda = v_id;\n"
                    + "	   delete from pdv.vendaitem where id_venda = v_id;\n"
                    + "	   delete from pdv.vendanfce where id_venda = v_id;\n"
                    + "	   delete from pdv.vendamfepagamento where id_venda = v_id;\n"
                    + "	   delete from pdv.vendamfe where id_venda = v_id;\n"
                    + "       delete from pdv.vendacfe where id_venda = v_id;\n"
                    + "	   delete from pdv.venda where id = v_id;\n"
                    + "	end;\n"
                    + "$$;";

            LOG.finer(sql);
            stm.execute(sql);
        }
    }

    public void vincularMapaDivergenciaComAnteriores(String sistema, String loja) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute(
                    "update implantacao.mapavenda a set\n"
                    + "	codigoatual = b.codigoatual,\n"
                    + "	novo = b.novo\n"
                    + "from\n"
                    + "(select\n"
                    + "	ant.impsistema,\n"
                    + "	ant.imploja,\n"
                    + "	coalesce(ant.impid, mv.codigo) impid,\n"
                    + "	coalesce(ant.descricao, mv.descricao) descricao,\n"
                    + "	coalesce(ant.codigoatual, mv.codigoatual) codigoatual,\n"
                    + "	coalesce(ant.novo, mv.novo) novo\n"
                    + "from\n"
                    + "	implantacao.mapavenda mv \n"
                    + "	join implantacao.codant_produto ant on \n"
                    + "		mv.sistema = ant.impsistema and \n"
                    + "		mv.loja = ant.imploja and \n"
                    + "		mv.codigo = ant.impid\n"
                    + "where	\n"
                    + "	ant.impsistema = '" + sistema + "' and\n"
                    + "	ant.imploja = '" + loja + "'\n"
                    + ") b\n"
                    + "where	 \n"
                    + "	a.sistema = b.impsistema and \n"
                    + "	a.loja = b.imploja and \n"
                    + "	a.codigo = b.impid and \n"
                    + "	a.codigoatual is null"
            );
        }
    }

    public void gerarConsistencia(int lojaVR) throws Exception {
        try (Statement st = Conexao.createStatement()) {
            st.execute(
                    "insert into pdv.consistencia (data, id_loja)\n"
                    + "select\n"
                    + "	distinct data,\n"
                    + "	id_loja\n"
                    + "from\n"
                    + "	pdv.venda v\n"
                    + "	join implantacao.vendasimportadas imp on\n"
                    + "		imp.id_venda = v.id\n"
                    + "where\n"
                    + "	v.id_loja = " + lojaVR + "\n"
                    + "except\n"
                    + "select\n"
                    + "	data,\n"
                    + "	id_loja\n"
                    + "from\n"
                    + "	pdv.consistencia c\n"
                    + "order by\n"
                    + "	1"
            );
        }
    }

    public void gerarVendaPontuacao(PdvVendaVO vo) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();

            sql.setSchema("pdv");
            sql.setTableName("venda");

            sql.put("id_loja", vo.getId_loja());
            sql.put("numerocupom", vo.getNumeroCupom());
            sql.put("ecf", vo.getEcf());
            sql.put("data", vo.getData());
            sql.put("id_clientepreferencial", vo.getId_clientePreferencial(), 0);
            sql.put("horainicio", vo.getHoraInicio());
            sql.put("horatermino", vo.getHoraTermino());
            sql.put("cancelado", vo.isCancelado());
            sql.put("subtotalimpressora", vo.getSubTotalImpressora());
            sql.put("cpf", vo.getCpf());
            sql.put("contadordoc", vo.getContadorDoc());
            sql.put("valordesconto", vo.getValorDesconto());
            sql.put("valoracrescimo", vo.getValorAcrescimo());
            sql.put("canceladoemvenda", vo.isCanceladoEmVenda());
            sql.put("numeroserie", vo.getNumeroSerie());
            sql.put("mfadicional", vo.getMfAdicional());
            sql.put("modeloimpressora", vo.getModeloImpressora());
            sql.put("numerousuario", vo.getNumeroUsuario());
            sql.put("nomecliente", vo.getNomeCliente());
            sql.put("enderecocliente", vo.getEnderecoCliente());

            LOG.finer(
                    "Incluindo a Venda { "
                    + "ecf:" + vo.getEcf() + ","
                    + "cupom: " + vo.getNumeroCupom() + ","
                    + "data:" + SQLUtils.stringSQL(DATE_FORMAT.format(vo.getData())) + ","
                    + "horainicio:" + SQLUtils.stringSQL(TIME_FORMAT.format(vo.getHoraInicio())) + ","
                    + "subtotalimpressora: " + String.format("%.2f", vo.getSubTotalImpressora())
                    + "}"
            );

            sql.getReturning().add("id");

            String sqlInsert = sql.getInsert();

            try (ResultSet rst = stm.executeQuery(sqlInsert)) {
                if (rst.next()) {
                    vo.setId(rst.getLong("id"));
                }
                LOG.finest(sqlInsert);
            }
        }
    }

    public void gerarVendaPromocaoPontuacao(PdvVendaPromocaoPontuacaoVO vo) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();

            sql.setSchema("pdv");
            sql.setTableName("vendapromocaopontuacao");

            sql.put("id_venda", vo.getIdVenda());
            sql.put("id_promocao", vo.getIdPromocao());
            sql.put("pontos", vo.getPonto());
            sql.put("cnpj", vo.getCnpj());
            sql.put("id_situacaopromocaopontuacao", vo.getIdSituacaoPromocaoPontuacao());
            sql.put("lancamentomanual", vo.isLancamentoManual());
            sql.put("id_loja", vo.getIdLoja());
            sql.put("datacompra", vo.getDataCompra());
            sql.put("dataexpiracao", vo.getDataExpiracao());

            sql.getReturning().add("id");

            String sqlInsert = sql.getInsert();

            try (ResultSet rst = stm.executeQuery(sqlInsert)) {
                if (rst.next()) {
                    vo.setId(rst.getLong("id"));
                }
                LOG.finest(sqlInsert);
            }
        }
    }
}
