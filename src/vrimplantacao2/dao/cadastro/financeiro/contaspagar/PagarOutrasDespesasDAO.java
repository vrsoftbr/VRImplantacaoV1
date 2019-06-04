package vrimplantacao2.dao.cadastro.financeiro.contaspagar;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import vrframework.classe.Conexao;
import vrframework.classe.Util;
import vrimplantacao2.vo.cadastro.fornecedor.FornecedorVO;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.cadastro.financeiro.ContaPagarVO;
import vrimplantacao2.vo.cadastro.financeiro.PagarOutrasDespesasVO;

/**
 *
 * @author Leandro
 */
public class PagarOutrasDespesasDAO {

    public void gravar(PagarOutrasDespesasVO vo) throws Exception {
        SQLBuilder sql = new SQLBuilder();
        sql.setTableName("pagaroutrasdespesas");
        sql.put("id_fornecedor", vo.getIdFornecedor());
        sql.put("numerodocumento", vo.getNumeroDocumento());
        sql.put("id_tipoentrada", vo.getIdTipoEntrada());
        sql.put("dataemissao", vo.getDataEmissao());
        sql.put("dataentrada", vo.getDataEntrada());
        sql.put("valor", vo.getValor());
        sql.put("id_situacaopagaroutrasdespesas", vo.getSituacaoPagarOutrasDespesas().getId());
        sql.put("id_loja", vo.getId_loja());
        sql.put("observacao", vo.getObservacao());
        sql.put("id_tipopiscofins", vo.getId_tipopiscofins(), -1);
        sql.put("datahoraalteracao", vo.getDataHoraAlteracao());
        sql.getReturning().add("id");
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(sql.getInsert())) {
                rst.next();
                vo.setId(rst.getInt("id"));
            }
        }
    }

    public void atualizar(PagarOutrasDespesasVO vo, Set<OpcaoContaPagar> opt) throws Exception {
        /*
         SQLBuilder sql = new SQLBuilder();
         sql.setTableName("pagaroutrasdespesas");
         sql.setWhere("id = " + vo.getId());        
         sql.put("id_fornecedor", vo.getIdFornecedor());
         sql.put("numerodocumento", vo.getNumeroDocumento());
         sql.put("id_tipoentrada", vo.getTipoEntrada().getId());        
         sql.put("dataemissao", vo.getDataEmissao());
         sql.put("dataentrada", vo.getDataEntrada());
         sql.put("valor", vo.getValor());
         sql.put("id_situacaopagaroutrasdespesas", vo.getSituacaoPagarOutrasDespesas().getId());
         sql.put("id_loja", vo.getId_loja());
         sql.put("observacao", vo.getObservacao());
         sql.put("id_tipopiscofins", vo.getId_tipopiscofins());
         sql.put("datahoraalteracao", vo.getDataHoraAlteracao());
         try (Statement stm = Conexao.createStatement()) {
         stm.execute(sql.getUpdate());
         }
         */
    }

    public int getFornecedorLoja(int lojaVR) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select id_fornecedor from loja where id = " + lojaVR
            )) {
                if (rst.next()) {
                    return rst.getInt("id_fornecedor");
                }
            }
        }
        return 1;
    }

    public List<ContaPagarVO> getOutrasDespesas(int idLoja, int idFornecedor, String razaoSocial) throws Exception {
        List<ContaPagarVO> result = new ArrayList<>();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	cp.numerodocumento,\n"
                    + " cp.id,\n"
                    + "	cp.id_fornecedor,\n"
                    + "	f.razaosocial,\n"
                    + "	cp.dataentrada,\n"
                    + "	cp.dataemissao,\n"
                    + " pv.datavencimento,\n"        
                    + " te.id id_tipoentrada,\n"
                    + "	te.descricao,\n"
                    + "	cp.valor,\n"
                    + "	cp.observacao \n"
                    + "from \n"
                    + "	pagaroutrasdespesas cp\n"
                    + "join fornecedor f on (cp.id_fornecedor = f.id)\n"
                    + "join tipoentrada te on (cp.id_tipoentrada = te.id)\n"
                    + "join pagaroutrasdespesasvencimento pv on (cp.id = pv.id_pagaroutrasdespesas)"        
                    + "where \n"
                    + "	cp.observacao like '% - FLAG%' and\n"
                    + "	cp.id_loja = " + idLoja + " and\n"
                    + "	id_situacaopagaroutrasdespesas = 0\n"
                    + (idFornecedor != 0 ? " and f.id = " + idFornecedor + "\n" : "")
                    + (!"".equals(razaoSocial) ? " and f.razaosocial like '%" + razaoSocial + "%'\n" : "")
                    + "order by\n"
                    + "	cp.dataemissao")) {
                while (rs.next()) {
                    ContaPagarVO vo = new ContaPagarVO();
                    vo.setIdOutrasDespesas(rs.getInt("id"));
                    vo.setNumeroDocumento(rs.getInt("numerodocumento"));
                    FornecedorVO forn = new FornecedorVO();
                    forn.setId(rs.getInt("id_fornecedor"));
                    forn.setRazaoSocial(rs.getString("razaosocial"));
                    vo.setFornecedor(forn);
                    vo.setDataEntrada(rs.getDate("dataentrada"));
                    vo.setDataEmissao(rs.getDate("dataemissao"));
                    vo.setVencimento(rs.getDate("datavencimento"));
                    vo.setIdTipoEntrada(rs.getInt("id_tipoentrada"));
                    vo.setValor(rs.getDouble("valor"));
                    vo.setObservacao(rs.getString("observacao"));

                    result.add(vo);
                }
            }
        }
        return result;
    }

    public void finalizar(List<ContaPagarVO> vContaPagar, int idLoja) throws Exception {
        for (ContaPagarVO vo : vContaPagar) {
            try (Statement stm = Conexao.createStatement()) {
                SQLBuilder sql = new SQLBuilder();
                sql.setTableName("pagarfornecedor");
                sql.put("id_loja", idLoja);
                sql.put("id_fornecedor", vo.getFornecedor().getId());
                sql.put("id_tipoentrada", vo.getIdTipoEntrada());
                sql.put("numerodocumento", vo.getNumeroDocumento());
                sql.put("dataentrada", vo.getDataEntrada());
                sql.put("dataemissao", vo.getDataEmissao());
                sql.put("valor", vo.getValor());
                sql.putNull("id_notadespesa");
                sql.putNull("id_notaentrada");
                sql.put("id_pagaroutrasdespesas", vo.getIdOutrasDespesas());
                sql.putNull("id_transferenciaentrada");
                sql.putNull("id_geracaoretencaotributo");
                sql.putNull("id_escritasaldo");
                sql.getReturning().add("id");
                try (ResultSet rst = stm.executeQuery(sql.getInsert())) {
                    rst.next();
                    vo.setId(rst.getInt("id"));
                    atualizaSituacaoOutrasDespesas(vo.getIdOutrasDespesas());
                    salvarParcela(vo);
                } catch (Exception e) {
                    sql.setFormatarSQL(true);
                    Util.exibirMensagem(sql.getInsert(), "");
                    throw e;
                }
            }
        }
    }

    private void atualizaSituacaoOutrasDespesas(int id) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            sql.setTableName("pagaroutrasdespesas");
            sql.put("id_situacaopagaroutrasdespesas", SituacaoPagarOutrasDespesas.FINALIZADO.id);
            sql.setWhere(" id = " + id);
            try {
                stm.execute(sql.getUpdate());
            } catch (Exception e) {
                sql.setFormatarSQL(true);
                Util.exibirMensagem(sql.getUpdate(), "");
                throw e;
            }
        }
    }

    private void salvarParcela(ContaPagarVO contaPagar) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            sql.setTableName("pagarfornecedorparcela");
            sql.put("id_pagarfornecedor", contaPagar.getId());
            sql.put("numeroparcela", 1);
            sql.put("datavencimento", contaPagar.getVencimento());
            sql.put("valor", contaPagar.getValor());
            sql.put("observacao", contaPagar.getObservacao());
            sql.put("id_situacaopagarfornecedorparcela", SituacaoPagarOutrasDespesas.NAO_FINALIZADO.id);
            sql.put("id_tipopagamento", 5);
            sql.putNull("datapagamentocontabil");
            sql.put("id_banco", 804);
            sql.put("agencia", "VERIFICAR");
            sql.put("conta", "VERIFICAR");
            sql.put("numerocheque", 0);
            sql.put("conferido", false);
            sql.put("valoracrescimo", 0);
            sql.put("datahoraalteracao", Util.getDataAtual());
            try {
                stm.execute(sql.getInsert());
            } catch (Exception e) {
                sql.setFormatarSQL(true);
                Util.exibirMensagem(sql.getInsert(), "");
                throw e;
            }
        }
    }
}
