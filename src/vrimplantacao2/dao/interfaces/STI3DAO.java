package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import vrimplantacao.classe.ConexaoMySQL;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;

/**
 *
 * @author Importacao
 */
public class STI3DAO extends InterfaceDAO implements MapaTributoProvider {

    private static final Logger LOG = Logger.getLogger(STI3DAO.class.getName());
    
    @Override
    public String getSistema() {
        return "STI3";
    }
    
    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n" +
                    "	 codigo,\n" +
                    "    aliquota\n" +
                    "from \n" +
                    "	powerstock.aliquotas_ecf"
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(
                            rst.getString("codigo"),
                            rst.getString("aliquota")
                    ));
                }
            }
        }
        return result;
    }
    
    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(new OpcaoProduto[]{
            OpcaoProduto.MERCADOLOGICO,
            OpcaoProduto.MAPA_TRIBUTACAO,
            OpcaoProduto.FAMILIA_PRODUTO,
            OpcaoProduto.FAMILIA,
            OpcaoProduto.PRODUTOS,
            OpcaoProduto.IMPORTAR_RESETAR_BALANCA,
            OpcaoProduto.IMPORTAR_MANTER_BALANCA,
            OpcaoProduto.DATA_CADASTRO,
            OpcaoProduto.DATA_ALTERACAO,
            OpcaoProduto.EAN,
            OpcaoProduto.EAN_EM_BRANCO,
            OpcaoProduto.TIPO_EMBALAGEM_EAN,
            OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
            OpcaoProduto.PESAVEL,
            OpcaoProduto.VALIDADE,
            OpcaoProduto.DESC_COMPLETA,
            OpcaoProduto.DESC_GONDOLA,
            OpcaoProduto.ATIVO,
            OpcaoProduto.DESC_REDUZIDA,
            OpcaoProduto.MERCADOLOGICO_PRODUTO,
            OpcaoProduto.PESO_BRUTO,
            OpcaoProduto.PESO_LIQUIDO,
            OpcaoProduto.ESTOQUE_MINIMO,
            OpcaoProduto.ESTOQUE_MAXIMO,
            OpcaoProduto.ESTOQUE,
            OpcaoProduto.CUSTO,
            OpcaoProduto.PRECO,
            OpcaoProduto.NCM,
            OpcaoProduto.CEST,
            OpcaoProduto.PIS_COFINS,
            OpcaoProduto.NATUREZA_RECEITA,
            OpcaoProduto.ICMS,
            OpcaoProduto.ICMS_SAIDA,
            OpcaoProduto.ICMS_SAIDA_FORA_ESTADO,
            OpcaoProduto.ICMS_ENTRADA,
            OpcaoProduto.ICMS_ENTRADA_FORA_ESTADO,
            OpcaoProduto.USAR_CONVERSAO_ALIQUOTA_COMPLETA,
            OpcaoProduto.MARGEM
        }));
    }
    
    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try(Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "	 codigo,\n" +
                    "    razao,\n" +
                    "    cnpj\n" +
                    "from\n" +
                    "	powerstock.empresas")) {
                while(rs.next()) {
                    result.add(new Estabelecimento(rs.getString("codigo"), rs.getString("razao")));
                }
            }
        }
        return result;
    }
    
    private Date dataInicioVenda;
    private Date dataTerminoVenda;

    public void setDataInicioVenda(Date dataInicioVenda) {
        this.dataInicioVenda = dataInicioVenda;
    }

    public void setDataTerminoVenda(Date dataTerminoVenda) {
        this.dataTerminoVenda = dataTerminoVenda;
    }
    
    @Override
    public Iterator<VendaIMP> getVendaIterator() throws Exception {
        return new STI3DAO.VendaIterator(getLojaOrigem(), dataInicioVenda, dataTerminoVenda);
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        return new STI3DAO.VendaItemIterator(getLojaOrigem(), dataInicioVenda, dataTerminoVenda);
    }
    
    private static class VendaIterator implements Iterator<VendaIMP> {

        private final static SimpleDateFormat FORMAT = new SimpleDateFormat("dd/MM/yyyy");

        private Statement stm = ConexaoMySQL.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaIMP next;
        private Set<String> uk = new HashSet<>();

        private void obterNext() {
            try {
                SimpleDateFormat timestampDate = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat timestamp = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                if (next == null) {
                    if (rst.next()) {
                        next = new VendaIMP();
                        String id = rst.getString("id");
                        if (!uk.add(id)) {
                            LOG.warning("Venda " + id + " já existe na listagem");
                        }
                        next.setId(id);
                        next.setNumeroCupom(Utils.stringToInt(rst.getString("id")));
                        next.setEcf(Utils.stringToInt(rst.getString("ecf")));
                        next.setData(rst.getDate("emissao"));
                        next.setIdClientePreferencial(rst.getString("cliente"));
                        next.setNomeCliente(rst.getString("nome"));
                        next.setCpf(rst.getString("cpf_cnpj"));
                        String endereco = "";
                        endereco = rst.getString("logradouro") + ", " + rst.getString("bairro") + ", " + 
                                rst.getString("numero") + ", " + rst.getString("cidade") + ", " + 
                                rst.getString("uf") + " - " + rst.getString("cep");
                        next.setEnderecoCliente(endereco);
                        String horaInicio = timestampDate.format(rst.getDate("emissao")) + " " + rst.getString("horainicio");
                        String horaTermino = timestampDate.format(rst.getDate("emissao")) + " " + rst.getString("horainicio");
                        next.setHoraInicio(timestamp.parse(horaInicio));
                        next.setHoraTermino(timestamp.parse(horaTermino));
                        next.setSubTotalImpressora(rst.getDouble("valortotal"));
                    }
                }
            } catch (SQLException | ParseException ex) {                
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {
            this.sql
                    = "select\n" +
                    "	 codigo id,\n" +
                    "    numcaixa ecf,\n" +
                    "    dataemissao emissao,\n" +
                    "    codcli cliente,\n" +
                    "    c.nome,\n" +
                    "    c.logradouro,\n" +
                    "    c.numero,\n" +
                    "    c.bairro,\n" +
                    "    c.cidade,\n" +
                    "    c.uf,\n" +
                    "    c.cep,\n" +
                    "    date_format(data_hora, \"%H:%i:%s\") horainicio,\n" +
                    "    valortotal,\n" +
                    "    replace(upper(status), 'Í', 'I') cancelado,\n" +
                    "    cpf_cnpj\n" +
                    "from\n" +
                    "	powerstock.vendas v\n" +
                    "left join powerstock.clientes c on v.codcli = c.codigo_cliente\n" +
                    "where\n" +
                    "	 v.dataemissao between '" + dataInicio + "' and '" + dataTermino + "' and\n" +
                    "    v.codemp = " + idLojaCliente + "\n" +
                    "order by\n" +
                    "	dataemissao";

            LOG.log(Level.FINE, "SQL da venda: " + sql);
            rst = stm.executeQuery(sql);
        }

        @Override
        public boolean hasNext() {
            obterNext();
            return next != null;
        }

        @Override
        public VendaIMP next() {
            obterNext();
            VendaIMP result = next;
            next = null;
            return result;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported.");
        }
    }

    private static class VendaItemIterator implements Iterator<VendaItemIMP> {

        private final static SimpleDateFormat FORMAT = new SimpleDateFormat("dd/MM/yyyy");

        private Statement stm = ConexaoMySQL.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaItemIMP next;

        private void obterNext() {
            try {
                if (next == null) {
                    if (rst.next()) {
                        next = new VendaItemIMP();
                        String idVenda = rst.getString("idvenda");
                        String id = rst.getString("id");
                        next.setId(id);
                        next.setVenda(idVenda);
                        next.setProduto(rst.getString("idproduto"));
                        next.setDescricaoReduzida(rst.getString("descricao"));
                        next.setQuantidade(rst.getDouble("quantidade"));
                        next.setTotalBruto(rst.getDouble("valor"));
                        next.setValorDesconto(rst.getDouble("desconto"));
                        boolean cancelado = false;                           
                        if (rst.getInt("cancelado") == 1) {
                            cancelado = true;
                        }
                        next.setCancelado(cancelado);
                        if((rst.getString("ean")) != null &&
                                (!"".equals(rst.getString("ean"))) &&
                                (!"SEM GTIN".equals(rst.getString("ean").trim()))) {
                            next.setCodigoBarras(rst.getString("ean"));
                        } else {
                            next.setCodigoBarras(rst.getString("idproduto"));
                        }
                        next.setUnidadeMedida(rst.getString("unidade"));
                        String icms = rst.getString("icms");
                        if(rst.getString("icms") == null) {
                            icms = "II";
                        }
                        obterAliquota(next, icms);
                    }
                }
            } catch (Exception ex) {
                //ex.printStackTrace();
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        /**
         * Método temporario, desenvolver um mapeamento eficiente da tributação.
         *
         * @param item
         * @throws SQLException
         */
        public void obterAliquota(VendaItemIMP item, String icms) throws SQLException {
            /*
             07   7.00    ALIQUOTA 07%
             12   12.00   ALIQUOTA 12%
             18   18.00   ALIQUOTA 18%
             25   25.00   ALIQUOTA 25%
             II   0.00    ISENTO
             FF   0.00    SUBST TRIBUTARIA
             NN   0.00    NAO INCIDENTE
             */
            int cst;
            double aliq;
            switch (icms) {
                case "07":
                    cst = 0;
                    aliq = 7;
                    break;
                case "12":
                    cst = 0;
                    aliq = 12;
                    break;
                case "18":
                    cst = 0;
                    aliq = 18;
                    break;
                case "25":
                    cst = 0;
                    aliq = 25;
                    break;
                case "FF":
                    cst = 60;
                    aliq = 0;
                    break;
                case "NN":
                    cst = 41;
                    aliq = 0;
                    break;
                default:
                    cst = 40;
                    aliq = 0;
                    break;
            }
            item.setIcmsCst(cst);
            item.setIcmsAliq(aliq);
        }

        public VendaItemIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {
            this.sql
                    = "select\n" +
                    "	 i.codigo id,\n" +
                    "    i.codvenda idvenda,\n" +
                    "    i.codprod idproduto,\n" +
                    "    e.codigobarrasfornecedor ean,\n" +
                    "    p.descricao,\n" +
                    "    p.unidade,\n" +
                    "    i.quantidade,\n" +
                    "    i.item sequencia,\n" +
                    "    i.cancelado,\n" +
                    "    i.valortotalitem valor,\n" +
                    "    i.descontoinformado desconto,\n" +
                    "	 a.aliquota icms\n" +
                    "from\n" +
                    "	powerstock.itens_vendas i\n" +
                    "join powerstock.produtos p on (i.codprod = p.codpro)\n" +
                    "join powerstock.estoque e on (p.codpro = e.codprod)\n" +
                    "join powerstock.vendas v on (i.codvenda = v.codigo)\n" +
                    "left join powerstock.aliquotas_ecf a on (i.codigoaliquotaecf = a.codigo)\n" +
                    "where\n" +
                    "	 v.dataemissao between '" + dataInicio + "' and '" + dataTermino + "' and\n" +
                    "    i.codempresa = " + idLojaCliente +"\n" +
                    "order by\n" +
                    "	 i.codvenda, i.item";

            LOG.log(Level.FINE, "SQL da venda: " + sql);
            rst = stm.executeQuery(sql);
        }

        @Override
        public boolean hasNext() {
            obterNext();
            return next != null;
        }

        @Override
        public VendaItemIMP next() {
            obterNext();
            VendaItemIMP result = next;
            next = null;
            return result;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported.");
        }
    }
}
