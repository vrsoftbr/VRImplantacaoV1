package vrimplantacao2.dao.cadastro.notafiscal;

import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.cadastro.notafiscal.NotaSaida;
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
        try (Statement stm = Conexao.createStatement()) {
            stm.executeQuery("delete from notasaidaitem where id_notasaida = " + id);
            stm.executeQuery("delete from notasaida where id = " + id);
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
                    return rst.getObject("id", Integer.class);
                }
            }
        }
        return null;
    }

    public void salvar(NotaSaida ns) throws Exception {
        SQLBuilder sql = new SQLBuilder();
        
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
        sql.put("idlocalentrega", ns.getIdLocalEntrega());
        sql.put("valoricmsusoconsumo", ns.getValorIcmsUsoConsumo());
        sql.put("aplicaicmsipi", ns.isAplicaIcmsIpi());
        sql.put("id_tipoviatransporteinternacional", ns.getIdTipoViaTransporteInteracional());
        sql.put("valorafrmm", ns.getValorafrmm());
        sql.put("id_tipoformaimportacao", ns.getIdTipoFormaImportacao());
        sql.put("aplicaIcmsStIpi", ns.isAplicaIcmsStIpi());
        sql.put("especie", ns.getEspecie());
        sql.put("marca", ns.getMarca());
        sql.put("numeracao", ns.getNumeracao());
        sql.put("aplicaPisCofinsDesconto", ns.isAplicaPisCofinsDesconto());
        sql.put("aplicaPisCofinsEncargo", ns.isAplicaPisCofinsEncargo());
        sql.put("serie", ns.getSerie());
        sql.put("id_EscritaSaldo", ns.getIdEscritaSaldo());
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

    public void salvarItens(NotaSaida ns) throws Exception {
        //TODO: Incluir rotina de importação dos itens da nota.
    }
    
}
