package vrimplantacao2.dao.cadastro.notafiscal;

import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import vrframework.classe.Conexao;
import vrframework.classe.Util;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.cadastro.notafiscal.NotaEntrada;
import vrimplantacao2.vo.cadastro.notafiscal.NotaEntradaItem;
import vrimplantacao2.vo.importacao.NotaFiscalIMP;

/**
 * Classe responsável por gerenciar a manipulação dos dados das notas de entradas.
 * @author Leandro
 */
public class NotaEntradaDAO {
    
    private static final Logger LOG = Logger.getLogger(NotaEntradaDAO.class.getName());

    public int getTipoNotaEntrada() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select id from tipoentrada where descricao like 'IMPORTADO VR' limit 1"
            )) {
                if (rst.next()) {
                    return rst.getInt("id");
                }                
            }
            try (ResultSet rst = stm.executeQuery(
                    "insert into public.tipoentrada values (\n" +
                    "    (select coalesce(max(id) + 1, 1) from public.tipoentrada),\n" +
                    "    'IMPORTADO VR',\n" +
                    "    'N',\n" +
                    "    false,\n" +
                    "    false,\n" +
                    "    false,\n" +
                    "    false,\n" +
                    "    false,\n" +
                    "    false,\n" +
                    "    false,\n" +
                    "    false,\n" +
                    "    false,\n" +
                    "    false,\n" +
                    "    false,\n" +
                    "    false,\n" +
                    "    false,\n" +
                    "    false,\n" +
                    "    false,\n" +
                    "    false,\n" +
                    "    '1',\n" +
                    "    '1',\n" +
                    "    false,\n" +
                    "    null,\n" +
                    "    null,\n" +
                    "    null,\n" +
                    "    false,\n" +
                    "    false,\n" +
                    "    null,\n" +
                    "    1,\n" +
                    "    false,\n" +
                    "    null,\n" +
                    "    null,\n" +
                    "    false,\n" +
                    "    false,\n" +
                    "    false,\n" +
                    "	false,\n" +
                    "    null,\n" +
                    "    false,\n" +
                    "    false,\n" +
                    "    false,\n" +
                    "    null,\n" +
                    "    false,\n" +
                    "    null\n" +
                    ") returning id"
            )) {
                rst.next();
                return rst.getInt("id");
            }
        }
    }

    public void eliminarNota(int id) throws Exception {
        eliminarItens(id);
        try (Statement stm = Conexao.createStatement()) {
            stm.execute("delete from notaentrada where id = " + id);
        }
    }
    
    public void eliminarItens(int id) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute("delete from notaentradaitemimportacaoxml where id_notaentradaitem in (select id from notaentradaitem where id_notaentrada = " + id + ")");
            stm.execute("delete from notaentradaitem where id_notaentrada = " + id);
        }
    }

    public Integer getNota(NotaFiscalIMP imp, int idFornecedor, int idLojaVR) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            String sql = "select id from notaentrada n\n" +
                    "where\n" +
                    "	n.id_loja = " + idLojaVR + " and\n" +
                    "   n.id_fornecedor = " + idFornecedor + " and\n" +
                    "	n.numeronota = " + imp.getNumeroNota() + " and\n" +
                    "	n.dataentrada = '" + new SimpleDateFormat("yyyy-MM-dd").format(imp.getDataEntradaSaida()) + "'";
            
            LOG.fine(sql);
            
            try (ResultSet rst = stm.executeQuery(sql)) {
                if (rst.next()) {
                    return rst.getInt("id") > 0 ? rst.getInt("id") : null;
                }
            }
        }
        return null;
    }

    public void atualizar(NotaEntrada ne) throws Exception {
        
        SQLBuilder sql = new SQLBuilder();
        sql.setTableName("notaentrada");
        sql.put("valoripi", ne.getValorIpi());
        sql.put("valorfrete", ne.getValorFrete());
        sql.put("valordesconto", ne.getValorDesconto());
        sql.put("valoroutradespesa", ne.getValorOutraDespesa());
        sql.put("valordespesaadicional", ne.getValorDespesaAdicional());
        sql.put("valormercadoria", ne.getValorMercadoria());
        sql.put("valortotal", ne.getValorTotal());
        sql.put("valoricms", ne.getValorIcms());
        sql.put("valoricmssubstituicao", ne.getValorIcmsSubstituicao());
        sql.put("valorguiasubstituicao", ne.getValorGuiaSubstituicao());
        sql.put("valorbasecalculo", ne.getValorBaseCalculo());
        sql.put("valorbasesubstituicao", ne.getValorBaseSubstituicao());
        sql.put("valorfunrural", ne.getValorFunrural());
        sql.put("valordescontoboleto", ne.getValorDescontoBoleto());
        sql.put("valoricmssn", ne.getValorIcmsSN());
        sql.put("valordespesafrete", ne.getValorDespesaFrete());
        sql.put("valorfcp", ne.getValorFcp());
        sql.put("valorfcpst", ne.getValorFcpST());
        sql.put("valoricmsdesonerado", ne.getValorIcmsDesonerado());
        sql.put("valorsuframa", ne.getValorSuframa());        
        sql.setWhere("id = " + ne.getId());
                
        try (Statement stm = Conexao.createStatement()) {
            stm.execute(sql.getUpdate());
        }
    }

    public void salvar(NotaEntrada ne) throws Exception {
        SQLBuilder sql = new SQLBuilder();
        sql.setTableName("notaentrada");
        
        sql.put("id_loja", ne.getIdLoja());
        sql.put("numeronota", ne.getNumeroNota());
        sql.put("id_fornecedor", ne.getIdFornecedor());
        sql.put("dataentrada", ne.getDataEntrada());
        sql.put("id_tipoentrada", ne.getIdTipoEntrada());
        sql.put("dataemissao", ne.getDataEmissao());
        sql.put("datahoralancamento", ne.getDataHoraLancamento());
        sql.put("valoripi", ne.getValorIpi());
        sql.put("valorfrete", ne.getValorFrete());
        sql.put("valordesconto", ne.getValorDesconto());
        sql.put("valoroutradespesa", ne.getValorOutraDespesa());
        sql.put("valordespesaadicional", ne.getValorDespesaAdicional());
        sql.put("valormercadoria", ne.getValorMercadoria());
        sql.put("valortotal", ne.getValorTotal());
        sql.put("valoricms", ne.getValorIcms());
        sql.put("valoricmssubstituicao", ne.getValorIcmsSubstituicao());
        sql.put("id_usuario", ne.getIdUsuario());
        sql.put("impressao", ne.isImpressao());
        sql.put("produtorrural", ne.isProdutorRural());
        sql.put("aplicacustodesconto", ne.isAplicaCustoDesconto());
        sql.put("aplicaicmsdesconto", ne.isAplicaIcmsDesconto());
        sql.put("aplicacustoencargo", ne.isAplicaCustoEncargo());
        sql.put("aplicaicmsencargo", ne.isAplicaIcmsEncargo());
        sql.put("aplicadespesaadicional", ne.isAplicaDespesaAdicional());
        sql.put("id_situacaonotaentrada", ne.getSituacaoNotaEntrada().getId());
        sql.put("serie", ne.getSerie());
        sql.put("valorguiasubstituicao", ne.getValorGuiaSubstituicao());
        sql.put("valorbasecalculo", ne.getValorBaseCalculo());
        sql.put("aplicaaliquota", ne.getAplicaAliquota());
        sql.put("valorbasesubstituicao", ne.getValorBaseSubstituicao());
        sql.put("valorfunrural", ne.getValorFunrural());
        sql.put("valordescontoboleto", ne.getValorDescontoBoleto());
        sql.put("chavenfe", ne.getChaveNfe());
        sql.put("conferido", ne.isConferido());
        sql.put("id_tipofretenotafiscal", ne.getTipoFreteNotaFiscal().getIdVR());
        sql.put("observacao", ne.getObservacao());
        sql.put("id_notasaida", ne.getIdNotaSaida(), -1);
        sql.put("id_tiponota", ne.getTipoNota().getId());
        sql.put("modelo", ne.getModelo());
        sql.put("liberadopedido", ne.isLiberadoPedido());
        sql.put("dataHoraFinalizacao", ne.getDataHoraFinalizacao());
        sql.put("importadoXml", ne.isImportadoXml());
        sql.put("aplicaicmsipi", ne.isAplicaIcmsIpi());
        sql.put("liberadobonificacao", ne.getLiberadoBonificacao());
        sql.put("informacaocomplementar", ne.getInformacaoComplementar());
        sql.put("valoricmssn", ne.getValorIcmsSN());
        sql.put("datahoraalteracao", ne.getDataHoraAlteracao());
        sql.put("liberadovencimento", ne.getLiberadoVencimento(), -2);
        sql.put("justificativadivergencia", ne.getJustificativaDivergencia());
        sql.put("consistido", ne.isConsistido());
        sql.put("quantidadepaletes", ne.getQuantidadePaletes());
        sql.put("id_notadespesa", ne.getIdNotaDespesa(), -1);
        sql.put("valordespesafrete", ne.getValorDespesaFrete());
        sql.put("liberadovalidadeproduto", ne.isLiberadoValidadeProduto());
        sql.put("valorfcp", ne.getValorFcp());
        sql.put("valorfcpst", ne.getValorFcpST());
        sql.put("valoricmsdesonerado", ne.getValorIcmsDesonerado());
        sql.put("liberadodivergenciacoletor", ne.isLiberadoDivergenciaColetor());
        sql.put("valorsuframa", ne.getValorSuframa());
        
        sql.getReturning().add("id");
        
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    sql.getInsert()
            )) {
                if (rst.next()) {
                    ne.setId(rst.getInt("id"));
                }
            }
        }
    }

    public void salvarItem(NotaEntradaItem item) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            
            SQLBuilder sql = new SQLBuilder();  
            
            sql.setSchema("public");
            sql.setTableName("notaentradaitem");
            sql.put("id_notaentrada", item.getIdNotaEntrada());
            sql.put("id_produto", item.getIdProduto());
            sql.put("quantidade", item.getQuantidade());
            sql.put("qtdembalagem", item.getQtdEmbalagem());
            sql.put("valor", item.getValor());
            sql.put("valortotal", item.getValorTotal());
            sql.put("valoripi", item.getValorIpi());
            sql.put("id_aliquota", item.getIdAliquota());
            sql.put("custocomimposto", item.getCustoComImposto());
            sql.put("valortotalfinal", item.getValorTotalFinal());
            sql.put("valorbasecalculo", item.getValorBaseCalculo());
            sql.put("valoricms", item.getValorIcms());
            sql.put("valoricmssubstituicao", item.getValorIcmsSubstituicao());
            sql.put("custocomimpostoanterior", item.getCustoComImpostoAnterior());
            sql.put("valorbonificacao", item.getValorBonificacao());
            sql.put("valorverba", item.getValorVerba());
            sql.put("quantidadedevolvida", item.getQuantidadeDevolvida());
            sql.put("valorpiscofins", item.getValorPisCofins());
            sql.put("contabilizavalor", item.isContabilizaValor());
            sql.put("valorbasesubstituicao", item.getValorBaseSubstituicao());
            sql.put("valorembalagem", item.getValorEmbalagem());
            sql.put("cfop", item.getCfop());
            sql.put("valoricmssubstituicaoxml", item.getValorIcmsSubstituicaoXml());
            sql.put("valorisento", item.getValorIsento());
            sql.put("valoroutras", item.getValorOutras());
            sql.put("situacaotributaria", item.getSituacaoTributaria());
            sql.put("valorfrete", item.getValorFrete());
            sql.put("valoroutrasdespesas", item.getValorOutrasDespesas());
            sql.put("valordesconto", item.getValorDesconto());
            sql.put("id_tipopiscofins", item.getIdTipoPisCofins());
            sql.put("id_aliquotacreditoforaestado", item.getIdAliquotaCreditoForaEstado());
            sql.put("id_aliquotapautafiscal", item.getIdAliquotaPautaFiscal(), -1);
            sql.put("id_tipoentrada", item.getIdTipoEntrada(), -1);
            sql.put("valoroutrassubstituicao", item.getValorOutras());
            sql.put("quantidadebonificacao", item.getQuantidadeBonificacao());
            sql.put("valorsubstituicaoestadual", item.getValorSubstituicaoEstadual());
            sql.put("descricaoxml", item.getDescricaoXml());
            sql.put("valordespesafrete", item.getValorDespesaFrete());
            sql.put("cfopnota", item.getCfopNota());
            sql.put("valorbasefcp", item.getValorBaseFcp());
            sql.put("valorfcp", item.getValorFcp());
            sql.put("valorbasefcpst", item.getValorBaseFcpSt());
            sql.put("valorfcpst", item.getValorFcpSt());
            sql.put("valoricmsdesonerado", item.getValorIcmsDesonerado());
            sql.put("idmotivodesoneracao", item.getIdMotivoDesoneracao(), -1);
            sql.put("valorbasecalculoicmsdesonerado", item.getValorBaseCalculoIcmsDesonerado());
            sql.put("valoricmsdiferido", item.getValorIcmsDiferido());
            //sql.put("basecalculoicmsstretido");
            //sql.put("porcentagemicmsstretido");
            //sql.put("valoricmsstretido");
            //sql.put("basecalculofcpstretido");
            //sql.put("porcentagemfcpstretido");
            //sql.put("valorfcpstretido");
            //sql.put("porcentagemfcp");
            //sql.put("porcentagemfcpst");
            
            String sq = sql.getInsert();
            try {
                stm.execute(sq);
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, ex.getMessage() + " - '" + sq + "'", ex);
                throw ex;
            }
            
        }
    }
    
}
