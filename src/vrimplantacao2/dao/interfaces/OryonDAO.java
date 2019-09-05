package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import vrimplantacao.classe.ConexaoAccess;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;

/**
 *
 * @author Importacao
 */
public class OryonDAO extends InterfaceDAO {

    private static final Logger LOG = Logger.getLogger(OryonDAO.class.getName());
    
    @Override
    public String getSistema() {
        return "Oryon";
    }
    
    public List<Estabelecimento> getLojaCliente() {
        return new ArrayList<>(Arrays.asList(new Estabelecimento("2", "SUPERMERCADO ANDREA")));
    }

    @Override
    public List<ContaPagarIMP> getContasPagar() throws Exception {
        List<ContaPagarIMP> result = new ArrayList<>();
        try(Statement stm = ConexaoAccess.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "   codigo_fluxo as id,\n" +
                    "   duplicata,\n" +
                    "   num_maquina as ecf,\n" +
                    "   dia as datalanc,\n" +
                    "   vencimento,\n" +
                    "   numero as coo,\n" +
                    "   prazo as valor,\n" +
                    "   fornecedor,\n" +
                    "   descricao as observacao\n" +
                    "from\n" +
                    "   tabela_fluxo\n" +
                    "where\n" +
                    "   fornecedor is not null and\n" +
                    "   data_baixa is null and\n" +
                    "   duplicata is not null\n" +
                    "order by\n" +
                    "   vencimento")) {
                while(rs.next()) {
                    ContaPagarIMP imp = new ContaPagarIMP();
                    imp.setId(rs.getString("id"));
                    imp.setIdFornecedor(rs.getString("fornecedor"));
                    imp.setVencimento(rs.getDate("vencimento"));
                    imp.setValor(rs.getDouble("valor"));
                    imp.setNumeroDocumento(rs.getString("coo"));
                    imp.setDataEmissao(rs.getDate("datalanc"));
                    imp.setObservacao(rs.getString("observacao"));
                    
                    result.add(imp);
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
        return new VendaIterator(getLojaOrigem(), dataInicioVenda, dataTerminoVenda);
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        return new VendaItemIterator(getLojaOrigem(), dataInicioVenda, dataTerminoVenda);
    }
    
    private static class VendaIterator implements Iterator<VendaIMP> {

        public final static SimpleDateFormat FORMAT = new SimpleDateFormat("dd/MM/yyyy");

        private Statement stm = ConexaoAccess.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaIMP next;
        private Set<String> uk = new HashSet<>();

        private void obterNext() {
            try {
                SimpleDateFormat timestampDate = new SimpleDateFormat("dd/MM/yyyy");
                SimpleDateFormat timestamp = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                if (next == null) {
                    if (rst.next()) {
                        next = new VendaIMP();
                        String id = rst.getString("id");
                        if (!uk.add(id)) {
                            LOG.warning("Venda " + id + " já existe na listagem");
                        }
                        next.setId(id);
                        next.setNumeroCupom(Utils.stringToInt(rst.getString("numerocupom")));
                        next.setEcf(rst.getInt("ecf"));
                        next.setData(rst.getDate("data"));
                        String cliente = rst.getString("cliente");
                        next.setIdClientePreferencial(cliente);
                        next.setHoraInicio(timestamp.parse(rst.getString("horainicio")));
                        next.setHoraTermino(timestamp.parse(rst.getString("horatermino")));
                        //next.setCancelado(rst.getBoolean("cancelado"));
                        next.setSubTotalImpressora(rst.getDouble("subtotalimpressora"));
                        if(rst.getString("cnpj") == null) {
                            next.setCpf(rst.getString("cpf"));
                        } else {
                            next.setCpf(rst.getString("cnpj"));
                        }
                        //next.setValorDesconto(rst.getDouble("desconto"));
                        next.setNumeroSerie(rst.getString("numeroserie"));
                        next.setModeloImpressora(rst.getString("modelo"));
                        next.setNomeCliente(rst.getString("nome"));
                        String endereco
                                = Utils.acertarTexto(rst.getString("endereco")) + ","
                                + Utils.acertarTexto(rst.getString("numero")) + ","
                                + Utils.acertarTexto(rst.getString("complemento")) + ","
                                + Utils.acertarTexto(rst.getString("bairro")) + ","
                                + Utils.acertarTexto(rst.getString("cidade")) + "-"
                                + Utils.acertarTexto(rst.getString("estado")) + ","
                                + Utils.acertarTexto(rst.getString("cep"));
                        next.setEnderecoCliente(endereco);
                        next.setChaveNfCe(rst.getString("chavenfce"));
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
                    "    n.link as id,\n" +
                    "    n.dia as data,\n" +
                    "    n.dia as horainicio,\n" +
                    "    n.dia as horatermino,\n" +
                    "    n.cliente,\n" +
                    "    c.nome,\n" +
                    "    c.cnpj,\n" +
                    "    c.cic as cpf,\n" +
                    "    c.endereco_logradouro as endereco,\n" +
                    "    c.endereco_numero as numero,\n" +
                    "    c.endereco_complemento as complemento,\n" +
                    "    c.bairro,\n" +
                    "    c.cidade,\n" +
                    "    c.uf as estado,\n" +
                    "    c.cep,\n" +
                    "    n.numero as numerocupom,\n" +
                    "    n.desconto as desconto,\n" +
                    "    n.valor as subtotalimpressora,\n" +
                    "    n.num_maquina as ecf,\n" +
                    "    ecf.marcaimpressora,\n" +
                    "    ecf.modeloimpressora as modelo,\n" +
                    "    ecf.numserie as numeroserie,\n" +
                    "    n.situacao as cancelado,\n" +
                    "    chavenfe as chavenfce\n" +
                    "from\n" +
                    "    tabela_nota1 n,\n" +
                    "    tabela_cli c,\n" +
                    "    tabela_sped_ecf ecf\n" +
                    "where\n" +
                    "    n.cliente = c.codigo and\n" +
                    "    n.num_maquina = ecf.codigo and\n" +
                    "    n.data_inclusao between #" + FORMAT.format(dataInicio) + "# and #" + FORMAT.format(dataTermino) + "# and\n" +
                    "    n.cupom = True and\n" +
                    "    n.tipo = 1\n" +
                    "order by\n" +
                    "    dia";
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

        private Statement stm = ConexaoAccess.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaItemIMP next;

        private void obterNext() {
            try {
                if (next == null) {
                    if (rst.next()) {
                        next = new VendaItemIMP();
                        String data = rst.getString("dia"), 
                                numero = rst.getString("numero"), 
                                ecf = rst.getString("ecf"), 
                                sequencia = rst.getString("sequencia");
                        String id = data + "-" + numero + "-" + ecf + "-" + sequencia;

                        next.setId(id);
                        next.setVenda(rst.getString("link"));
                        next.setProduto(rst.getString("cod_produto"));
                        next.setDescricaoReduzida(rst.getString("descricaocompleta"));
                        next.setQuantidade(rst.getDouble("quantidade"));
                        next.setTotalBruto(rst.getDouble("valor"));
                        next.setValorDesconto(rst.getDouble("desconto"));
                        String cancelado = rst.getString("situacao");
                        next.setCancelado("1".equals(cancelado));
                        next.setCodigoBarras(rst.getString("codigo"));
                        next.setUnidadeMedida(rst.getString("unidade"));
                        next.setIcmsAliq(rst.getDouble("icms_aliq"));
                        next.setIcmsCst(rst.getInt("icms_cst"));
                    }
                }
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaItemIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {
            this.sql
                    = "select\n" +
                    "   vi.link,\n" +
                    "   vi.dia,\n" +
                    "   vi.numero,\n" +
                    "   v.num_maquina as ecf,\n" +
                    "   vi.item as sequencia,\n" +
                    "   vi.codigo as cod_produto,\n" +
                    "   vi.codigo,\n" +
                    "   p.descricao as descricaocompleta,\n" +
                    "   p.unidade,\n" +
                    "   vi.quantidade,\n" +
                    "   vi.valor,\n" +
                    "   vi.desconto,\n" +
                    "   vi.situacao,\n" +
                    "   vi.situacao_tributaria_icm as icms_cst,\n" +
                    "   vi.aliquota_icm as icms_aliq\n" +
                    "from \n" +
                    "   tabela_nota2 vi,\n" +
                    "   tabela_nota1 v,\n" +
                    "   tabela_pro p\n" +
                    "where\n" +
                    "   vi.link = v.link and\n" +
                    "   vi.codigo = p.codigo and\n" +
                    "   v.data_inclusao between #" + VendaIterator.FORMAT.format(dataInicio) + "# and #" + VendaIterator.FORMAT.format(dataTermino) + "# and\n" +
                    "   v.tipo = 1\n" +
                    "order by\n" +
                    "   2, 3, 5";
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

/*
--- Script Loja
select
   codigo,
   descricao
from
   tabela_unidade_negocio
*/

/*
--- Script de Produto
select
  p.codigo as id,
  p.codigo as codigobarras,
  descricao as descricaocompleta,
  descricao as descricaoreduzida,
  descricao as descricaogondola,
  categoria as cod_mercadologico1,
  g.grupo as mercadologico1,
  1 as cod_mercadologico2,
  g.sub_grupo as mercadologico2,
  1 as cod_mercadologico3,
  g.sub_grupo as mercadologico3,
  familia,
  unidade,
  qt_embalagem as qtdembalagem,
  situacao as ativo,
  qt_estoque as estoque,
  qt_minimo as estoqueminimo,
  qt_maximo as estoquemaximo,
  preco_venda as precovenda,
  preco_compra as custocomimposto,
  preco_compra as custosemimposto,
  margem_lucro as margem,
  usa_balanca as balanca,
  dias_validade as validade,
  data_cadastro as datacadastro,
  peso as pesobruto,
  pesoliquido,
  ncm,
  cest,
  situacao_tributaria_icm_entrada as cst_e,
  situacao_tributaria_icm_saida_ne as cst_ne_s,
  aliquota_icm_saida_ne as icms_s,
  aliquota_st_ret as icmsretencao,
  situacao_tributaria_icm_saida_fe as cst_fe_s,
  situacao_tributaria_pis as pis_s,
  situacao_tributaria_pis_entrada as pis_e,
  situacao_tributaria_cofins as cofins_s,
  situacao_tributaria_cofins_entrada as cofins_e,
  codigo_natureza_receita_pis_cofins as natreceita,
  margem_valor_agregado_fe as mva_fe,
  margem_valor_agregado as mva
from
  tabela_pro p,
  tabela_categ g
where
  p.categoria = g.codigo
order by
  1
*/

/*
-- Script de Mercadologico
select 
   codigo as merc1,
   grupo as descmerc1,
   1 as merc2,
   sub_grupo as descmerc2,
   1 as merc3,
   nome as descmerc3 
from
   tabela_categ
order by
   1, 2
*/

/*
-- Script Produto Fornecedor
select
  codpro as idproduto,
  codfor as idfornecedor,
  codigo_forn as codigoexterno
from
  tabela_profor
order by
  codpro, codfor
  
 -----------------------------
 
 select 
   codigo as idproduto,
   fornecedor as idfornecedor,
   codigo_forn as codigoexterno 
from
   tabela_pro
where
   fornecedor is not null and 
   fornecedor <> -1 and
   codigo_forn is not null
order by
   1
*/

/*
-- Script de Cliente
select 
   codigo as id,
   nome as razao,
   fantasia,
   endereco_logradouro as endereco,
   endereco_numero as numero,
   endereco_complemento as complemento,
   bairro,
   cidade,
   codigo_cidade as ibgemunicipio,
   cep,
   uf,
   telefone,
   fax,
   rg,
   cic as cpf,
   cnpj,
   ie,
   contato,
   limite_credito,
   data_cadastro,
   pai,
   mae,
   email,
   cancelado,
   inativo,
   sexomasc as sexo,
   profissao 
from
   tabela_cli
order by
   1
*/

/*
-- Script de Fornecedor
select 
   codigo as id,
   nome as razao,
   fantasia,
   endereco_logradouro as rua,
   endereco_numero as numero,
   endereco_complemento as complemento,
   bairro,
   cidade,
   codigo_cidade as ibgemunicipio,
   cep,
   uf,
   telefone,
   fax,
   cnpj,
   ie,
   contato,
   data_cadastro,
   email,
   inativo,
   regime_tributario as tipoempresa 
from
   tabela_for
order by
   1
*/

/*
-- Script Rotativo
select
   codigo_fluxo as id,
   duplicata,
   num_maquina as ecf,
   dia as datalanc,
   vencimento,
   numero as coo,
   prazo as valor,
   cliente,
   descricao as observacao
from
   tabela_fluxo
where
   cliente is not null and
   data_baixa is null and
   duplicata is not null
order by
   vencimento
*/

/*
-- Script Conta Pagar
select
   codigo_fluxo as id,
   duplicata,
   num_maquina as ecf,
   dia as datalanc,
   vencimento,
   numero as coo,
   prazo as valor,
   fornecedor,
   descricao as observacao
from
   tabela_fluxo
where
   fornecedor is not null and
   data_baixa is null and
   duplicata is not null
order by
   vencimento
*/

/*
-- Script Venda
select
    n.link as id,
    n.dia as data,
    n.cliente as clientepreferencial,
    c.nome as razao,
    c.cnpj,
    c.cic,
    n.numero as numerocupom,
    n.desconto as valordesconto,
    n.valor as subtotalimpressora,
    n.num_maquina as ecf,
    n.situacao as cancelado
from
    tabela_nota1 n,
    tabela_cli c
where
    n.cliente = c.codigo and
    n.data_inclusao between #01/01/2018# and #21/08/2019# and
    n.cupom = True
order by
    dia
*/

/*
-- Script Venda Item
select 
   vi.link as cod_venda,
   vi.dia as data,
   vi.numero as numerocupom,
   vi.item as sequencia,
   vi.codigo as cod_produto,
   p.descricao as descricaocompleta,
   p.unidade as unidademedida,
   vi.quantidade,
   vi.valor,
   vi.desconto,
   vi.situacao,
   vi.situacao_tributaria_icm as icms_cst,
   vi.aliquota_icm as icms_aliq
from 
   tabela_nota2 vi,
   tabela_nota1 v,
   tabela_pro p
where
   vi.link = v.link and
   vi.codigo = p.codigo and
   v.data_inclusao between #01/01/2018# and #21/08/2019#
order by
   1, 2, 4
*/