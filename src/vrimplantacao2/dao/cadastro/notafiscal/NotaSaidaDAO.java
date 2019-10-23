package vrimplantacao2.dao.cadastro.notafiscal;

import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.cadastro.notafiscal.NotaSaida;
import vrimplantacao2.vo.cadastro.notafiscal.NotaSaidaItem;
import vrimplantacao2.vo.importacao.NotaFiscalIMP;

/**
 * Classe responsável por gerenciar a manipulação dos dados das notas de saída.
 * @author Leandro
 */
public class NotaSaidaDAO {

    public int getTipoNotaSaida() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select id from tiposaida where descricao like 'IMPORTADO VR'"
            )) {
                if (rst.next()) {
                    return rst.getInt("id");
                }
            }
            try (ResultSet rst = stm.executeQuery(
                    "insert into tiposaida values (\n" +
                    "	(select coalesce(max(id) + 1, 1) from tiposaida),\n" +
                    "    'IMPORTADO VR',\n" +
                    "    false,\n" +
                    "    false,\n" +
                    "    '1',\n" +
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
                    "    null,\n" +
                    "    'S',\n" +
                    "    false,\n" +
                    "    false,\n" +
                    "    null,\n" +
                    "    null,\n" +
                    "    null,\n" +
                    "    false,\n" +
                    "    false,\n" +
                    "    null,\n" +
                    "    1,\n" +
                    "    false,\n" +
                    "    false,\n" +
                    "    false,\n" +
                    "    false,\n" +
                    "    false,\n" +
                    "    false,\n" +
                    "    null,\n" +
                    "    null,\n" +
                    "    false\n" +
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
            stm.execute("delete from notasaida where id = " + id);
        }
    }
    
    public void eliminarItens(int id) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute("delete from notasaidaitemimportacaoxml where id_notasaidaitem in (select id from notasaidaitem where id_notasaida = " + id + ")");
            stm.execute("delete from notasaidaitem where id_notasaida = " + id);
        }
    }

    public Integer getNota(NotaFiscalIMP imp, int idLojaVR) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select id from notasaida n\n" +
                    "where\n" +
                    "	n.id_loja = " + idLojaVR + " and\n" +
                    "	n.serie = '" + imp.getSerie() + "' and\n" +
                    "	n.numeronota = " + imp.getNumeroNota() + " and\n" +
                    "	n.datasaida = '" + new SimpleDateFormat("yyyy-MM-dd").format(imp.getDataEntradaSaida()) + "'"
            )) {
                if (rst.next()) {
                    return rst.getInt("id") > 0 ? rst.getInt("id") : null;
                }
            }
        }
        return null;
    }

    public void salvar(NotaSaida ns) throws Exception {
        SQLBuilder sql = new SQLBuilder();
        
        sql.setSchema("public");
        sql.setTableName("notasaida");
        sql.put("id_loja", ns.getIdLoja());
        sql.put("numeroNota", ns.getNumeroNota());
        sql.put("id_tiponota", ns.getTipoNota().getId());
        sql.put("id_fornecedordestinatario", ns.getIdFornecedor(), -1);
        sql.put("id_clienteeventualdestinatario", ns.getIdClienteEventual(), -1);
        sql.put("id_tiposaida", ns.getIdTipoSaida());
        sql.put("datahoraemissao", ns.getDataHoraEmissao());
        sql.put("datasaida", ns.getDataSaida());
        sql.put("valoripi", ns.getValorIpi());
        sql.put("valorfrete", ns.getValorFrete());
        sql.put("valoroutrasdespesas", ns.getValorOutrasDespesas());
        sql.put("valorproduto", ns.getValorProduto());
        sql.put("valortotal", ns.getValorTotal());
        sql.put("valorbasecalculo", ns.getValorBaseCalculo());
        sql.put("valoricms", ns.getValorIcms());
        sql.put("valorbasesubstituicao", ns.getValorBaseSubstituicao());
        sql.put("valoricmssubstituicao", ns.getValorIcmsSubstituicao());
        sql.put("valorseguro", ns.getValorSeguro());
        sql.put("valordesconto", ns.getValorDesconto());
        sql.put("impressao", ns.isImpressao());
        sql.put("id_situacaonotasaida", ns.getSituacaoNotaSaida().getId());
        sql.put("id_motoristatransportador", ns.getIdMotoristaTransportador(), -1);
        sql.put("id_fornecedortransportador", ns.getIdFornecedorTransportador(), -1);
        sql.put("id_clienteeventualtransportador", ns.getIdClienteEventualTransportador(), -1);
        sql.put("placa", ns.getPlaca());
        sql.put("id_tipodevolucao", ns.getIdTipoDevolucao(), -1);
        sql.put("informacaocomplementar", ns.getInformacaoComplementar());
        sql.put("senha", ns.getSenha());
        sql.put("tipolocalbaixa", ns.getTipoLocalBaixa());
        sql.put("valorbaseipi", ns.getValorBaseIpi());
        sql.put("volume", ns.getVolume());
        sql.put("pesoliquido", ns.getPesoLiquido());
        sql.put("id_situacaonfe", ns.getSituacaoNfe().getId());
        sql.put("chavenfe", ns.getChaveNfe());
        sql.put("recibonfe", ns.getReciboNfe());
        sql.put("motivorejeicaonfe", ns.getMotivoRejeicaoNfe());
        sql.put("protocolorecebimentonfe", ns.getProtocoloRecebimentoNfe());
        sql.put("datahorarecebimentonfe", ns.getDataHoraRecebimentoNfe());
        sql.put("justificativacancelamentonfe", ns.getJustificativaCancelamentoNfe());
        sql.put("protocolocancelamentonfe", ns.getProtocoloCancelamentoNfe());
        sql.put("datahoracancelamentonfe", ns.getDataHoraCancelamentoNfe());
        sql.put("id_tipofretenotafiscal", ns.getTipoFreteNotaFiscal().getIdVR());
        sql.put("id_notasaidacomplemento", ns.getIdNotaSaidaComplemento(), -1);
        sql.put("emailnfe", ns.isEmailNfe());
        sql.put("contingencianfe", ns.isContingenciaNfe());
        sql.put("id_notaentrada", ns.getIdNotaEntrada(), -1);
        sql.put("aplicaicmsdesconto", ns.isAplicaIcmsDesconto());
        sql.put("aplicaicmsencargo", ns.isAplicaIcmsEncargo());
        sql.put("pesobruto", ns.getPesoBruto());
        sql.put("datahoraalteracao", ns.getDataHoraAlteracao());
        sql.put("id_localentrega", ns.getIdLocalEntrega());
        sql.put("valoricmsusoconsumo", ns.getValorIcmsUsoConsumo());
        sql.put("aplicaicmsipi", ns.isAplicaIcmsIpi());
        sql.put("id_tipoviatransporteinternacional", ns.getIdTipoViaTransporteInteracional(), -1);
        sql.put("valorafrmm", ns.getValorafrmm());
        sql.put("id_tipoformaimportacao", ns.getIdTipoFormaImportacao(), -1);
        sql.put("aplicaIcmsStIpi", ns.isAplicaIcmsStIpi());
        sql.put("especie", ns.getEspecie());
        sql.put("marca", ns.getMarca());
        sql.put("numeracao", ns.getNumeracao());
        sql.put("aplicaPisCofinsDesconto", ns.isAplicaPisCofinsDesconto());
        sql.put("aplicaPisCofinsEncargo", ns.isAplicaPisCofinsEncargo());
        sql.put("serie", ns.getSerie());
        sql.put("id_EscritaSaldo", ns.getIdEscritaSaldo(), -1);
        sql.put("valorFcp", ns.getValorFcp());
        sql.put("valorFcpSt", ns.getValorFcpSt());
        sql.put("valorIcmsDesonerado", ns.getValorIcmsDesonerado());
        
        sql.getReturning().add("id");
        
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    sql.getInsert()
            )) {
                if (rst.next()) {
                    ns.setId(rst.getInt("id"));
                }
            }
        }
    }

    public void salvarItem(NotaSaidaItem item) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            
            SQLBuilder sql = new SQLBuilder();  
            
            sql.setSchema("public");
            sql.setTableName("notasaidaitem");
            sql.put("id_notasaida", item.getIdNotaSaida());//private long idNotaSaida;//id_notasaida;// bigint NOT NULL,
            sql.put("id_produto", item.getIdProduto());//id_produto;// integer NOT NULL,
            sql.put("quantidade", item.getQuantidade());// = 0;// numeric(12,3) NOT NULL,
            sql.put("qtdembalagem", item.getQtdEmbalagem());// = 1;// integer NOT NULL,
            sql.put("valor", item.getValor());// = 0;// numeric(13,4) NOT NULL,
            sql.put("valortotal", item.getValorTotal());// = 0;// numeric(11,2) NOT NULL,
            sql.put("valoripi", item.getValorIpi());// = 0;// numeric(13,4) NOT NULL,
            sql.put("id_aliquota", item.getIdAliquota());//;//id_aliquota;// integer NOT NULL,
            sql.put("valorbasecalculo", item.getValorBaseCalculo());// = 0;// numeric(11,2) NOT NULL,
            sql.put("valoricms", item.getValorIcms());// = 0;// numeric(13,4) NOT NULL,
            sql.put("valorbasesubstituicao", item.getValorBaseSubstituicao());// = 0;// numeric(11,2) NOT NULL,
            sql.put("valoricmssubstituicao", item.getValorIcmsSubstituicao());// = 0;// numeric(13,4) NOT NULL,
            sql.put("valorpiscofins", item.getValorPisCofins());// = 0;// numeric(11,2) NOT NULL,
            sql.put("valorbaseipi", item.getValorBaseIpi());// = 0;// numeric(11,2) NOT NULL,
            sql.put("cfop", item.getCfop());//;// character varying(5),
            sql.put("tipoiva", item.getTipoIva());// = 0;// integer NOT NULL DEFAULT 0,
            sql.put("id_aliquotapautafiscal", item.getIdAliquotaPautaFiscal(), -1);// = -1;//id_aliquotapautafiscal;// integer,
            sql.put("valordesconto", item.getValorDesconto());// = 0;// numeric(11,2) NOT NULL DEFAULT 0,
            sql.put("valorisento", item.getValorIsento());// = 0;// numeric(11,2) NOT NULL DEFAULT 0,
            sql.put("valoroutras", item.getValorOutras());// = 0;// numeric(11,2) NOT NULL DEFAULT 0,
            sql.put("situacaotributaria", item.getSituacaoTributaria());// = 0;// integer NOT NULL DEFAULT 0,
            sql.put("valoricmsdispensado", item.getValorIcmsDispensado());// = 0;// numeric(12,3) NOT NULL DEFAULT 0,
            sql.put("id_aliquotadispensado", item.getIdAliquotaDispensado(), -1);// = -1;//id_aliquotadispensado;// integer,
            sql.put("tiponaturezareceita", item.getTipoNaturezaReceita(), -1);// = -1;// integer,
            sql.put("datadesembaraco", item.getDataDesembaraco());//;// timestamp without time zone,
            sql.put("id_estadodesembaraco", item.getIdEstadoDesembaraco(), -1);// = -1;//id_estadodesembaraco;// integer,
            sql.put("numeroadicao", item.getNumeroAdicao());// = 0;// integer NOT NULL DEFAULT 0,
            sql.put("localdesembaraco", item.getLocalDesembaraco());// = "";// character varying(50) NOT NULL DEFAULT ''::character varying,
            sql.put("id_tiposaida", item.getIdTipoSaida(), -1);// = -1;//id_tiposaida;// integer,
            sql.put("id_aliquotainterestadual", item.getIdAliquotaInterestadual(), -1);// = -1;//id_aliquotainterestadual;// integer,
            sql.put("id_aliquotadestino", item.getIdAliquotaDestino(), -1);// = -1;//id_aliquotadestino;// integer,
            sql.put("id_tipoorigemapuracao", item.getIdTipoOrigemApuracao(), -1);// = -1;//id_tipoorigemapuracao;// integer,
            sql.put("valorbasefcp", item.getValorBaseFcp());// = 0;// numeric(11,2),
            sql.put("valorfcp", item.getValorFcp());// = 0;// numeric(11,2),
            sql.put("valorbasefcpst", item.getValorBaseFcpSt());// = 0;// numeric(11,2),
            sql.put("valorfcpst", item.getValorFcpSt());// = 0;// numeric(11,2),
            sql.put("valoricmsdesonerado", item.getValorIcmsDesonerado());// = 0;// numeric(11,2),
//            sql.put("id_motivodesoneracao", item.getIdMotivoDesoneracao(), -1);// = -1;// integer,
//            sql.put("valorbasecalculoicmsdesonerado", item.getValorBaseCalculoIcmsDesonerado());// = 0;// numeric(11,2),
//            sql.put("id_escritafundamento", item.getIdEscritaFundamento(), -1);// = -1;//id_escritafundamento;// integer,
//            sql.put("id_escritacodigoajuste", item.getIdEscritaCodigoAjuste(), -1);// = -1;//id_escritacodigoajuste;// integer,
//            sql.put("valoricmsdiferido", item.getValorIcmsDiferido());// = 0;// numeric(11,2)

            stm.execute(sql.getInsert());
            
        }
    }

    public void atualizar(NotaSaida ns) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
