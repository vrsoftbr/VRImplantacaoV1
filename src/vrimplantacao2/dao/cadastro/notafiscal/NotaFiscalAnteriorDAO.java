package vrimplantacao2.dao.cadastro.notafiscal;

import java.sql.ResultSet;
import java.sql.Statement;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.multimap.MultiMap;
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
                        "	id_notasaida integer references notasaida(id) on update cascade on delete cascade,\n" +
                        "	id_notaentrada integer references notaentrada(id) on update cascade on delete cascade,\n" +
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
                    vo.setIdNotaSaida(rst.getObject("id_notasaida", Integer.class));
                    vo.setIdNotaEntrada(rst.getObject("id_notaentrada", Integer.class));
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

    public void atualizar(NotaFiscalAnteriorVO anterior) {
        throw new UnsupportedOperationException("Funcao ainda nao suportada.");
    }

    public void incluir(NotaFiscalAnteriorVO anterior) {
        throw new UnsupportedOperationException("Funcao ainda nao suportada.");
    }
    
}
