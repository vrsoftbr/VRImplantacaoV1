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
        throw new UnsupportedOperationException("Funcao ainda nao suportada.");
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
                    "	n.dataentrada = " + new SimpleDateFormat("yyyy-MM-dd").format(imp.getDataEntradaSaida())
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
        /*        
        private SituacaoNotaEntrada situacaoNotaEntrada = SituacaoNotaEntrada.FINALIZADO;//id_situacaonotaentrada; integer NOT NULL,
        private String serie;// character varying(4) NOT NULL,
        private double valorGuiaSubstituicao = 0;// numeric(11,2),
        private double valorBaseCalculo = 0;// numeric(11,2),
        private int aplicaAliquota = -1;// integer NOT NULL,
        private double valorBaseSubstituicao = 0;// numeric(11,2) NOT NULL,
        private double valorFunrural = 0;// numeric(11,2) NOT NULL,
        private double valorDescontoBoleto = 0;// numeric(11,2) NOT NULL,
        private String chaveNfe;// character varying(44) NOT NULL,
        private boolean conferido = false;// boolean NOT NULL,
        private TipoFreteNotaFiscal tipoFreteNotaFiscal = TipoFreteNotaFiscal.CONTRATADO_DESTINATARIO;//id_tipofretenotafiscal integer NOT NULL,
        private String observacao = "";// text NOT NULL DEFAULT ''::character varying,
        private long idNotaSaida = -1;//id_notasaida bigint,
        private TipoNota tipoNota = TipoNota.NORMAL;//id_tiponota integer NOT NULL DEFAULT 0,
        private String modelo;// character varying(2) NOT NULL DEFAULT ''::character varying,
        private boolean liberadoPedido = false;// boolean NOT NULL DEFAULT false,
        private Timestamp dataHoraFinalizacao;// timestamp without time zone DEFAULT '1900-01-01'::date,
        private boolean importadoXml = false;// boolean NOT NULL DEFAULT false,
        private boolean aplicaIcmsIpi = false;// boolean NOT NULL DEFAULT false,
        private int liberadoBonificacao = 1;// integer NOT NULL DEFAULT '-1'::integer,
        private String informacaoComplementar = "";// character varying(1000),
        private double valorIcmsSN = 0;// numeric(13,4),
        private Timestamp dataHoraAlteracao;// timestamp without time zone NOT NULL DEFAULT now(),
        private int liberadoVencimento = -1;// integer NOT NULL DEFAULT '-1'::integer,
        private String justificativaDivergencia;// character varying(50),
        private boolean consistido = false;// boolean DEFAULT false,
        private int quantidadePaletes = 0;// integer NOT NULL DEFAULT 0,
        private long idNotaDespesa = -1;//id_notadespesa bigint,
        private double valorDespesaFrete = 0;// numeric(11,2),
        private boolean liberadoValidadeProduto = false;// boolean NOT NULL DEFAULT false,
        private double valorFcp = 0;// numeric(11,2),
        private double valorFcpST = 0;// numeric(11,2),
        private double valorIcmsDesonerado = 0;// numeric(11,2),
        private boolean liberadoDivergenciaColetor = false;// boolean DEFAULT false,
        private double valorSuframa = -1;// numeric(11,2),
        */
        
        sql.getReturning().add("id");
    }
    
}
