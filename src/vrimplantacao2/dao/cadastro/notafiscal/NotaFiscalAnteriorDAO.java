package vrimplantacao2.dao.cadastro.notafiscal;

import java.sql.ResultSet;
import java.sql.Statement;
import vrframework.classe.Conexao;
import vrimplantacao.utils.Utils;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.cadastro.notafiscal.TipoNota;
import vrimplantacao2.vo.importacao.NotaOperacao;
import vrimplantacao2.vo.enums.TipoDestinatario;

/**
 *
 * @author Leandro
 */
public class NotaFiscalAnteriorDAO {
    
    private static final String SCHEMA = "implantacao";
    private static final String TABLE = "codant_notafiscal";
    
    
    public NotaFiscalAnteriorDAO() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            boolean existente;
            try (ResultSet rst = stm.executeQuery(
                    "select * from information_schema.tables where table_schema = '" + SCHEMA + "' and table_name = '" + TABLE + "'"
            )) {
                existente = rst.next();
            }
            if (!existente) {
                stm.execute(
                        "create table implantacao.codant_notafiscal (\n" +
                        "	sistema varchar not null,\n" +
                        "	loja varchar not null,\n" +
                        "	operacao integer not null,\n" +
                        "	id varchar not null,\n" +
                        "	id_notasaida integer references notasaida(id) on update cascade on delete set null,\n" +
                        "	id_notaentrada integer references notaentrada(id) on update cascade on delete set null,\n" +
                        "	tiponota integer,\n" +
                        "	modelo varchar(4),\n" +
                        "	serie varchar(5),\n" +
                        "	numeronota integer,\n" +
                        "	dataemissao date,\n" +
                        "	valorproduto numeric,\n" +
                        "	valortotal numeric,\n" +
                        "	tipodestinatario integer,\n" +
                        "	iddestinatario varchar,\n" +
                        "	primary key (sistema, loja, operacao, id)\n" +
                        ")"
                );
            }
        }
    }

    public MultiMap<String, NotaFiscalAnteriorVO> getAnteriores(String sistema, String lojaOrigem) throws Exception {
        MultiMap<String, NotaFiscalAnteriorVO> result = new MultiMap<>();
        
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n" +
                    "	sistema,\n" +
                    "	loja,\n" +
                    "	operacao,\n" +
                    "	id,\n" +
                    "	id_notasaida,\n" +
                    "	id_notaentrada,\n" +
                    "	tiponota,\n" +
                    "	modelo,\n" +
                    "	serie,\n" +
                    "	numeronota,\n" +
                    "	dataemissao,\n" +
                    "	valorproduto,\n" +
                    "	valortotal,\n" +
                    "	tipodestinatario,\n" +
                    "	iddestinatario\n" +
                    "FROM\n" +
                    "	implantacao.codant_notafiscal\n" +
                    "where\n" +
                    "	sistema = '" + sistema + "' and\n" +
                    "	loja = '" + lojaOrigem + "'\n" +
                    "order by\n" +
                    "	operacao, id"
            )) {
                while (rst.next()) {
                    NotaFiscalAnteriorVO vo = new NotaFiscalAnteriorVO();
                    
                    vo.setSistema(rst.getString("sistema"));
                    vo.setLoja(rst.getString("loja"));
                    vo.setOperacao(NotaOperacao.get(rst.getInt("operacao")));
                    vo.setId(rst.getString("id"));
                    vo.setIdNotaSaida(Utils.toInteger(rst.getString("id_notasaida")));
                    vo.setIdNotaEntrada(Utils.toInteger(rst.getString("id_notaentrada")));
                    vo.setTipoNota(TipoNota.get(rst.getInt("tiponota")));
                    vo.setModelo(rst.getString("modelo"));
                    vo.setSerie(rst.getString("serie"));
                    vo.setNumeroNota(rst.getInt("numeronota"));
                    vo.setDataEmissao(rst.getDate("dataemissao"));
                    vo.setValorProduto(rst.getDouble("valorproduto"));
                    vo.setValorTotal(rst.getDouble("valortotal"));
                    vo.setTipoDestinatario(TipoDestinatario.get(rst.getInt("tipodestinatario")));
                    vo.setIdDestinatario(rst.getString("iddestinatario"));
                    
                    result.put(vo, String.valueOf(vo.getOperacao().getId()), vo.getId());
                }
            }
        }
        
        return result;
    }

    public void atualizar(NotaFiscalAnteriorVO anterior) throws Exception {
        SQLBuilder sql = new SQLBuilder();
        sql.setSchema("implantacao");
        sql.setTableName("codant_notafiscal");
        sql.put("id_notasaida", anterior.getIdNotaSaida());
        sql.put("id_notaentrada", anterior.getIdNotaEntrada());
        sql.put("tiponota", anterior.getTipoNota().getId());
        sql.put("modelo", anterior.getModelo());
        sql.put("serie", anterior.getSerie());
        sql.put("numeronota", anterior.getNumeroNota());
        sql.put("dataemissao", anterior.getDataEmissao());
        sql.put("valorproduto", anterior.getValorProduto());
        sql.put("valortotal", anterior.getValorTotal());
        sql.put("tipodestinatario", anterior.getTipoDestinatario().getId());
        sql.put("iddestinatario", anterior.getIdDestinatario());
        sql.setWhere(String.format(
                "sistema = '%s' and loja = '%s' and operacao = %d and id = '%s'",
                anterior.getSistema(),
                anterior.getLoja(),
                anterior.getOperacao().getId(),
                anterior.getId()
        ));
        try (Statement stm = Conexao.createStatement()) {
            stm.execute(sql.getUpdate());
        }
    }

    public void incluir(NotaFiscalAnteriorVO anterior) throws Exception {
        SQLBuilder sql = new SQLBuilder();
        sql.setSchema("implantacao");
        sql.setTableName("codant_notafiscal");
        sql.put("sistema", anterior.getSistema());
        sql.put("loja", anterior.getLoja());
        sql.put("operacao", anterior.getOperacao().getId());
        sql.put("id", anterior.getId());
        sql.put("id_notasaida", anterior.getIdNotaSaida());
        sql.put("id_notaentrada", anterior.getIdNotaEntrada());
        sql.put("tiponota", anterior.getTipoNota().getId());
        sql.put("modelo", anterior.getModelo());
        sql.put("serie", anterior.getSerie());
        sql.put("numeronota", anterior.getNumeroNota());
        sql.put("dataemissao", anterior.getDataEmissao());
        sql.put("valorproduto", anterior.getValorProduto());
        sql.put("valortotal", anterior.getValorTotal());
        sql.put("tipodestinatario", anterior.getTipoDestinatario().getId());
        sql.put("iddestinatario", anterior.getIdDestinatario());
        try (Statement stm = Conexao.createStatement()) {
            stm.execute(sql.getInsert());
        }
    }
    
}
