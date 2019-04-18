package vrimplantacao2.dao.cadastro.notafiscal;

import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.cadastro.notafiscal.NotaEntrada;
import vrimplantacao2.vo.importacao.NotaFiscalIMP;

/**
 * Classe responsável por gerenciar a manipulação dos dados das notas de entradas.
 * @author Leandro
 */
public class NotaEntradaDAO {

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

    public void salvarItens(NotaEntrada ne) throws Exception {
        //TODO: Incluir rotina de importação dos itens da nota.
    }

    public void eliminarNota(int id) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.executeQuery("delete from notaentradaitem where id_notaentrada = " + id);
            stm.executeQuery("delete from notaentrada where id = " + id);
        }
    }

    public Integer getNota(NotaFiscalIMP imp, int idLojaVR) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select id from notaentrada n\n" +
                    "where\n" +
                    "	n.id_loja = " + idLojaVR + " and\n" +
                    "	n.modelo = '" + imp.getModelo() + "' and\n" +
                    "	n.serie = '" + imp.getSerie() + "' and\n" +
                    "	n.numeronota = " + imp.getNumeroNota() + " and\n" +
                    "	n.dataentrada = '" + new SimpleDateFormat("yyyy-MM-dd").format(imp.getDataEntradaSaida()) + "'"
            )) {
                if (rst.next()) {
                    return rst.getObject("id", Integer.class);
                }
            }
        }
        return null;
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
        sql.put("liberadovencimento", ne.getLiberadoVencimento(), -1);
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
    
}
