package vrimplantacao2_5.dao.sistema;

import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.Arrays;
import java.util.HashSet;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import static vr.core.utils.StringUtils.LOG;
import vrimplantacao2_5.dao.conexao.ConexaoOracle;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.LocalDAO;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.nutricional.OpcaoNutricional;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.gui.interfaces.custom.solidus.Entidade;
import vrimplantacao2.utils.sql.SQLUtils;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.cadastro.notafiscal.SituacaoNfe;
import vrimplantacao2.vo.cadastro.notafiscal.TipoFreteNotaFiscal;
import vrimplantacao2.vo.cadastro.notafiscal.TipoNota;
import vrimplantacao2.vo.cadastro.receita.OpcaoReceitaBalanca;
import vrimplantacao2.vo.enums.OpcaoFiscal;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoDestinatario;
import vrimplantacao2.vo.enums.TipoEmpresa;
import vrimplantacao2.vo.enums.TipoIva;
import vrimplantacao2.vo.enums.TipoPagamento;
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.vo.importacao.ChequeIMP;
import vrimplantacao2.vo.importacao.ContaPagarVencimentoIMP;
import vrimplantacao2.vo.importacao.ConveniadoIMP;
import vrimplantacao2.vo.importacao.ConvenioEmpresaIMP;
import vrimplantacao2.vo.importacao.ConvenioTransacaoIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.DivisaoIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.NotaFiscalIMP;
import vrimplantacao2.vo.importacao.NotaFiscalItemIMP;
import vrimplantacao2.vo.importacao.NotaOperacao;
import vrimplantacao2.vo.importacao.NutricionalIMP;
import vrimplantacao2.vo.importacao.OfertaIMP;
import vrimplantacao2.vo.importacao.PautaFiscalIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ReceitaBalancaIMP;
import vrimplantacao2.vo.importacao.ReceitaIMP;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;

/**
 *
 * @author Alan
 */
public class SolidusOracle2_5DAO extends InterfaceDAO implements MapaTributoProvider {

    private Date vendasDataInicio = null;
    private Date vendasDataTermino = null;
    private Date notasDataInicio = null;
    private Date notasDataTermino = null;
    private List<Entidade> entidadesCheques;
    private List<Entidade> entidadesCreditoRotativo;
    private List<Entidade> entidadesConvenio;
    private String siglaEstadoPauta = "";
    private boolean removerDigitoProdutoBalanca = false;

    @Override
    public String getSistema() {
        return "SOLIDUS-ORACLE";
    }
    
    public void setSiglaEstadoPauta(String siglaEstadoPauta) {
        this.siglaEstadoPauta = siglaEstadoPauta == null ? "" : siglaEstadoPauta;
    }

    public void setRemoverDigitoProdutoBalanca(boolean removerDigitoProdutoBalanca) {
        this.removerDigitoProdutoBalanca = removerDigitoProdutoBalanca;
    }

    public Date getVendasDataInicio() {
        return vendasDataInicio;
    }

    public void setDataInicioVenda(Date vendasDataInicio) {
        this.vendasDataInicio = vendasDataInicio;
    }

    public void setDataTerminoVenda(Date vendasDataTermino) {
        this.vendasDataTermino = vendasDataTermino;
    }

    public Date getVendasDataTermino() {
        return vendasDataTermino;
    }

    public void setEntidadesConvenio(List<Entidade> entidadesConvenio) {
        this.entidadesConvenio = entidadesConvenio;
    }
    
    public void setEntidadesCheques(List<Entidade> entidadesCheques) {
        this.entidadesCheques = entidadesCheques;
    }

    public void setEntidadesCreditoRotativo(List<Entidade> entidadesCreditoRotativo) {
        this.entidadesCreditoRotativo = entidadesCreditoRotativo;
    }
    
    public void setNotasDataInicio(Date notasDataInicio) {
        this.notasDataInicio = notasDataInicio;
    }

    public void setNotasDataTermino(Date notasDataTermino) {
        this.notasDataTermino = notasDataTermino;
    }
    
    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.DATA_CADASTRO,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.ATIVO,
                OpcaoProduto.DESCONTINUADO,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.ATUALIZAR_SOMAR_ESTOQUE,
                OpcaoProduto.CUSTO,
                OpcaoProduto.CUSTO_COM_IMPOSTO,
                OpcaoProduto.CUSTO_SEM_IMPOSTO,
                OpcaoProduto.MARGEM,
                OpcaoProduto.PRECO,
                OpcaoProduto.FABRICANTE,
                OpcaoProduto.FAMILIA,
                OpcaoProduto.FAMILIA_PRODUTO,
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.ICMS,
                OpcaoProduto.ICMS_ENTRADA,
                OpcaoProduto.ICMS_SAIDA,
                OpcaoProduto.ICMS_CONSUMIDOR,
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                OpcaoProduto.NCM,
                OpcaoProduto.CEST,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.NATUREZA_RECEITA,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.PESO_BRUTO,
                OpcaoProduto.PESO_LIQUIDO,
                OpcaoProduto.QTD_EMBALAGEM_COTACAO,
                OpcaoProduto.QTD_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.TROCA,
                OpcaoProduto.VALIDADE,
                OpcaoProduto.VENDA_PDV,     // Libera produto para Venda no PDV
                OpcaoProduto.VOLUME_QTD,
                OpcaoProduto.ASSOCIADO,
                OpcaoProduto.RECEITA,
                OpcaoProduto.NUTRICIONAL,
                OpcaoProduto.PAUTA_FISCAL,
                OpcaoProduto.PAUTA_FISCAL_PRODUTO,
                OpcaoProduto.PDV_VENDA      // Habilita importacão de Vendas
        ));
    }

    @Override
    public Set<OpcaoFornecedor> getOpcoesDisponiveisFornecedor() {
        return new HashSet<>(Arrays.asList(
                OpcaoFornecedor.DADOS,
                OpcaoFornecedor.CONTATOS,
                OpcaoFornecedor.ENDERECO,
                OpcaoFornecedor.PAGAR_FORNECEDOR,
                OpcaoFornecedor.PRODUTO_FORNECEDOR,
                OpcaoFornecedor.SITUACAO_CADASTRO,
                OpcaoFornecedor.TIPO_EMPRESA
        ));
    }

    @Override
    public Set<OpcaoCliente> getOpcoesDisponiveisCliente() {
        return new HashSet<>(Arrays.asList(
                OpcaoCliente.DADOS,
                OpcaoCliente.CONTATOS,
                OpcaoCliente.CLIENTE_EVENTUAL,
                OpcaoCliente.DATA_CADASTRO,
                OpcaoCliente.DATA_NASCIMENTO,
                OpcaoCliente.ENDERECO,
                OpcaoCliente.VALOR_LIMITE,
                OpcaoCliente.CONVENIO_CONVENIADO,
                OpcaoCliente.CONVENIO_EMPRESA,
                OpcaoCliente.CONVENIO_TRANSACAO,
                OpcaoCliente.RECEBER_CREDITOROTATIVO,
                OpcaoCliente.RECEBER_CHEQUE,
                OpcaoCliente.VENCIMENTO_ROTATIVO
        ));
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        String tab_tributacao = "intersolid.tab_tributacao",
            tab_produto_loja = "intersolid.tab_produto_loja",
            tab_ncm_uf = "intersolid.tab_ncm_uf";

        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select distinct\n"
                    + "    t.cod_tributacao id,\n"
                    + "    t.des_tributacao descricao,\n"
                    + "    t.cod_sit_tributaria cst,\n"
                    + "    t.val_icms aliquota,\n"
                    + "    t.val_reducao_base_calculo reducao\n"
                    + "from\n"
                    + "    " + tab_tributacao + " t\n"
                    + "    left join (select distinct cod_tributacao from " + tab_produto_loja + ") pl on\n"
                    + "        pl.cod_tributacao = t.cod_tributacao\n"
                    + "    left join (select distinct cod_tributacao from " + tab_ncm_uf + ") nuf on\n"
                    + "        nuf.cod_tributacao = t.cod_tributacao\n"
                    + "    left join (select distinct cod_trib_entrada from " + tab_ncm_uf + ") nuf2 on\n"
                    + "        nuf2.cod_trib_entrada = t.cod_tributacao\n"
                    + "where\n"
                    + "    not pl.cod_tributacao is null or\n"
                    + "    not nuf.cod_tributacao is null or\n"
                    + "    not nuf2.cod_trib_entrada is null\n"
                    + "order by\n"
                    + "    t.cod_sit_tributaria,\n"
                    + "    t.val_icms,\n"
                    + "    t.val_reducao_base_calculo"
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(
                            rst.getString("id"),
                            rst.getString("descricao"),
                            rst.getInt("cst"),
                            rst.getDouble("aliquota"),
                            rst.getDouble("reducao")
                    ));
                }
            }
        }

        return result;
    }
    
    @Override
    public List<ConvenioEmpresaIMP> getConvenioEmpresa() throws Exception {
        List<ConvenioEmpresaIMP> result = new ArrayList<>();
        String 
            tab_cliente = "intersolid.tab_cliente",
            tab_cidade = "intersolid.tab_cidade";

        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    c.cod_cliente id,\n"
                    + "    c.des_cliente razao,\n"
                    + "    c.num_cgc cnpj,\n"
                    + "    c.num_insc_est ie,\n"
                    + "    c.des_endereco endereco,\n"
                    + "    c.num_endereco numero,\n"
                    + "    c.des_complemento complemento,\n"
                    + "    c.des_bairro bairro,\n"
                    + "    cd.des_cidade municipio,\n"
                    + "    cd.des_sigla uf,\n"
                    + "    c.num_cep cep,\n"
                    + "    c.num_fone telefone,\n"
                    + "    c.dta_cadastro datainicio,\n"
                    + "    coalesce(cast(c.dta_vencimento_cad as date), cast('01.01.2200' as date)) datatermino,\n"
                    + "    c.cod_status_pdv,\n"
                    + "    c.val_desconto desconto,\n"
                    + "    c.num_dia_fecha diapagamento,\n"
                    + "    case when c.dta_inicio_bloqueio < '01.01.2000' then null else c.dta_inicio_bloqueio end databloqueio,\n"
                    + "    c.des_observacao observacao\n"
                    + "from\n"
                    + "    " + tab_cliente + " c\n"
                    + "    left join " + tab_cidade + " cd on\n"
                    + "        c.cod_cidade = cd.cod_cidade\n"
                    + "where\n"
                    + "    c.cod_cliente in (select distinct cod_convenio from " + tab_cliente + ")\n"
                    + "order by\n"
                    + "    1"
            )) {
                while (rst.next()) {
                    ConvenioEmpresaIMP imp = new ConvenioEmpresaIMP();

                    imp.setId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setInscricaoEstadual(rst.getString("ie"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setTelefone(rst.getString("telefone"));
                    imp.setDataInicio(rst.getDate("datainicio"));
                    imp.setDataTermino(rst.getDate("datatermino"));
                    imp.setDesconto(rst.getDouble("desconto"));
                    imp.setDiaPagamento(Utils.stringToInt(rst.getString("diapagamento"), 1));
                    imp.setDataBloqueio(rst.getDate("databloqueio"));
                    imp.setObservacoes(rst.getString("observacao"));

                    result.add(imp);
                }
            }
        }

        return result;
    }
    
    @Override
    public List<ConveniadoIMP> getConveniado() throws Exception {
        List<ConveniadoIMP> result = new ArrayList<>();
        String tab_cliente = "intersolid.tab_cliente",
            tab_cidade = "intersolid.tab_cidade",
            tab_cliente_status_pdv = "intersolid.tab_cliente_status_pdv",
            tab_loja_cliente = "intersolid.tab_loja_cliente";

        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    c.cod_cliente id,\n"
                    + "    c.des_cliente nome,\n"
                    + "    coalesce(c.cod_convenio, 999) idempresa,\n"
                    + "    case s.negativar when 'S' then 1 else 0 end bloqueado,\n"
                    + "    c.num_cgc cnpj,\n"
                    + "    c.des_senha senha,\n"
                    + "    c.num_insc_est ie,\n"
                    + "    c.des_observacao observacao,\n"
                    + "    cast('01.01.2200' as date) validadecartao,\n"
                    + "    case c.flg_exibe_lim when 'S' then 1 else 0 end visualizasaldo,\n"
                    + "    coalesce(c.val_limite_conv, 0) limiteconvenio,\n"
                    + "    c.val_desconto desconto,\n"
                    + "    case lc.inativo when 'S' then 0 else 1 end ativo\n"
                    + "from\n"
                    + "    " + tab_cliente + " c\n"
                    + "    left join " + tab_cidade + " cd on\n"
                    + "        c.cod_cidade = cd.cod_cidade\n"
                    + "    left join " + tab_cliente_status_pdv + " s on\n"
                    + "        c.cod_status_pdv = s.cod_status_pdv\n"
                    + "    left join " + tab_loja_cliente + " lc on\n"
                    + "        lc.cod_cliente = c.cod_cliente and\n"
                    + "        lc.cod_loja = " + getLojaOrigem() + "\n"
                    + "where\n"
                    + "    not nullif(c.cod_convenio, 0) is null\n"
                    + "order by\n"
                    + "    1"
            )) {
                while (rst.next()) {
                    ConveniadoIMP imp = new ConveniadoIMP();

                    imp.setId(rst.getString("id"));
                    imp.setNome(rst.getString("nome"));
                    imp.setIdEmpresa(rst.getString("idempresa"));
                    imp.setCnpj(rst.getString("cnpj") == null ? imp.getId() : rst.getString("cnpj"));
                    imp.setObservacao(rst.getString("observacao"));
                    imp.setValidadeCartao(rst.getDate("validadecartao"));
                    imp.setVisualizaSaldo(rst.getBoolean("visualizasaldo"));
                    imp.setConvenioLimite(rst.getDouble("limiteconvenio"));
                    imp.setConvenioDesconto(rst.getDouble("desconto"));
                    imp.setBloqueado(rst.getBoolean("bloqueado"));
                    imp.setSituacaoCadastro(rst.getBoolean("ativo") ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);

                    if (imp.getConvenioLimite() > 9999999F) {
                        imp.setConvenioLimite(9999999);
                    }

                    result.add(imp);
                }
            }
        }

        return result;
    }
    
    @Override
    public List<ConvenioTransacaoIMP> getConvenioTransacao() throws Exception {
        List<ConvenioTransacaoIMP> result = new ArrayList<>();
        String tab_fluxo = "intersolid.tab_fluxo",
            tab_entidade = "intersolid.tab_entidade";

        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "    t.cod_loja,\n"
                    + "    t.tipo_conta, \n"
                    + "    t.tipo_parceiro,\n"
                    + "    t.cod_parceiro,\n"
                    + "    t.num_registro,\n"
                    + "    t.cod_parceiro idconveniado,\n"
                    + "    t.num_pdv ecf,\n"
                    + "    t.num_cupom_fiscal numerocupom,\n"
                    + "    t.dta_cadastro datacadastro,\n"
                    + "    t.val_parcela valor,\n"
                    + "    t.dta_emissao datamovimento,\n"
                    + "    t.des_observacao observacao\n"
                    + "from\n"
                    + "    " + tab_fluxo + " t\n"
                    + "    left join " + tab_entidade + " e on t.cod_entidade = e.cod_entidade\n"
                    + "where\n"
                    + "    t.cod_loja = " + getLojaOrigem() + "\n"
                    + "    and t.tipo_conta = 1\n"
                    + "    and t.tipo_parceiro = 0\n"
                    + "    and t.flg_quitado = 'N'\n"
                    + "    and t.cod_entidade in (" + implodeList(entidadesConvenio) + ")\n"
                    + "    and t.num_condicao = 30\n"
                    + "order by\n"
                    + "    1, 2, 3, 4, 5"
            )) {
                while (rst.next()) {
                    ConvenioTransacaoIMP imp = new ConvenioTransacaoIMP();

                    imp.setId(String.format(
                            "%d-%d-%d-%d-%d",
                            rst.getInt("cod_loja"),
                            rst.getInt("tipo_conta"),
                            rst.getInt("tipo_parceiro"),
                            rst.getInt("cod_parceiro"),
                            rst.getInt("num_registro")
                    ));
                    imp.setIdConveniado(rst.getString("idconveniado"));
                    imp.setEcf(rst.getString("ecf"));
                    imp.setNumeroCupom(rst.getString("numerocupom"));
                    imp.setDataHora(new Timestamp(rst.getDate("datacadastro").getTime()));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setDataMovimento(rst.getDate("datamovimento"));
                    imp.setObservacao(rst.getString("observacao"));

                    result.add(imp);
                }
            }
        }

        return result;
    }
    
    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();
        String tab_produto_similar = "intersolid.tab_produto_similar";

        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select cod_produto_similar, des_produto_similar from " + tab_produto_similar + " order by 1"
            )) {
                while (rst.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("cod_produto_similar"));
                    imp.setDescricao(rst.getString("des_produto_similar"));

                    result.add(imp);
                }
            }
        }

        return result;
    }
    
    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        String tab_secao = "intersolid.tab_secao",
            tab_grupo = "intersolid.tab_grupo",
            tab_subgrupo = "intersolid.tab_subgrupo";

        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    s.cod_secao merc1,\n"
                    + "    s.des_secao merc1_desc,\n"
                    + "    g.cod_grupo merc2,\n"
                    + "    g.des_grupo merc2_desc,\n"
                    + "    sg.cod_sub_grupo merc3,\n"
                    + "    sg.des_sub_grupo merc3_desc\n"
                    + "from\n"
                    + "    " + tab_secao + " s\n"
                    + "    left join " + tab_grupo + " g on g.cod_secao = s.cod_secao\n"
                    + "    left join " + tab_subgrupo + " sg on sg.cod_secao = g.cod_secao and sg.cod_grupo = g.cod_grupo\n"
                    + "order by\n"
                    + "    merc1, merc2, merc3"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setMerc1ID(rst.getString("merc1"));
                    imp.setMerc1Descricao(rst.getString("merc1_desc"));
                    imp.setMerc2ID(rst.getString("merc2"));
                    imp.setMerc2Descricao(rst.getString("merc2_desc"));
                    imp.setMerc3ID(rst.getString("merc3"));
                    imp.setMerc3Descricao(rst.getString("merc3_desc"));

                    result.add(imp);
                }
            }
        }

        return result;
    }
    
    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        String tab_produto_fornecedor = "intersolid.tab_produto_fornecedor",
            tab_produto = "intersolid.tab_produto",
            tab_loja = "intersolid.tab_loja",
            tab_codigo_barra = "intersolid.tab_codigo_barra",
            tab_divisao_fornecedor = "intersolid.tab_divisao_fornecedor",
            tab_produto_loja = "intersolid.tab_produto_loja",
            tab_ncm = "intersolid.tab_ncm",
            tab_cest = "intersolid.tab_cest",
            tab_tributacao = "intersolid.tab_tributacao";

        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    p.cod_produto id,\n"
                    + "    p.dta_cadastro datacadastro,\n"
                    + "    ean.ean,\n"
                    + "    p.qtd_embalagem_venda qtdembalagem,\n"
                    + "    p.qtd_embalagem_compra qtdembalagemcotacao,\n"
                    + "    p.des_unidade_venda unidade,\n"
                    + "    case p.flg_envia_balanca when 'S' then 1 else 0 end as ebalanca,\n"
                    + "    p.dias_validade validade,\n"
                    + "    p.des_produto decricaocompleta,\n"
                    + "    coalesce(p.des_reduzida, p.des_produto) descricaoreduzida,\n"
                    + "    (select \n"
                    + "        cod_fornecedor\n"
                    + "    from\n"
                    + "        " + tab_produto_fornecedor + "\n"
                    + "    where\n"
                    + "        flg_preferencial = 'S'\n"
                    + "        and cod_produto = p.cod_produto\n"
                    + "        and cod_loja = loja.cod_loja and rownum = 1 ) fabricante,\n"
                    + "    p.cod_secao,\n"
                    + "    p.cod_grupo, \n"
                    + "    p.cod_sub_grupo,\n"
                    + "    p.cod_produto_similar id_familia,\n"
                    + "    p.val_peso peso,\n"
                    + "    p.val_peso_emb pesoliquido,\n"
                    + "    p.val_vda_peso_bruto pesobruto,\n"
                    + "    pl.qtd_est_atual estoque,\n"
                    + "    pl.qtd_est_minimo estoqueminimo,\n"
                    + "    pl.val_margem margem,\n"
                    + "    case when pl.val_custo_tabela = 0 then pl.val_custo_cheio else pl.val_custo_tabela end custosemimposto,\n"
                    + "    case when pl.val_custo_cheio = 0 then pl.val_custo_tabela else pl.val_custo_cheio end custocomimposto,\n"
                    + "    pl.val_venda precovenda,\n"
                    + "    case when pl.inativo = 'S' then 0 else 1 end as situacaocadastro,\n"
                    + "    ncmcest.ncm,\n"
                    + "    ncmcest.cest,\n"
                    + "    p.cst_pis_cof_entrada,\n"
                    + "    p.cst_pis_cof_saida,\n"
                    + "    p.cod_tab_sped natreceita,\n"
                    + "    trib.cod_sit_tributaria icms_saida_cst,\n"
                    + "    trib.val_icms icms_saida_aliq,\n"
                    + "    trib.val_reducao_base_calculo icms_saida_reducao,\n"
                    + "    tribent.cod_sit_tributaria icms_entrada_cst,\n"
                    + "    tribent.val_icms icms_entrada_aliq,\n"
                    + "    tribent.val_reducao_base_calculo icms_entrada_reducao,\n"
                    + "    pl.cod_tributacao,\n"
                    + "    pl.cod_trib_entrada,\n"
                    + "    pl.per_pauta_iva,\n"
                    + "    pl.val_pauta_iva,\n"
                    + "    div.cod_divisao divisao\n"
                    + "from\n"
                    + "    " + tab_produto + " p\n"
                    + "    join " + tab_loja + " loja on loja.cod_loja = " + getLojaOrigem() + "\n"
                    + "    left join (\n"
                    + "        select\n"
                    + "            cod_produto,\n"
                    + "            cod_barra_principal ean\n"
                    + "        from\n"
                    + "            " + tab_produto + "\n"
                    + "        union\n"
                    + "        select\n"
                    + "            cod_produto,\n"
                    + "            cod_ean\n"
                    + "        from\n"
                    + "            " + tab_codigo_barra + "\n"
                    + "    ) ean on p.cod_produto = ean.cod_produto\n"
                    + "    left join (select distinct\n"
                    + "        pf.cod_produto,\n"
                    + "        max(df.cod_divisao) cod_divisao\n"
                    + "    from\n"
                    + "        " + tab_divisao_fornecedor + " df\n"
                    + "        join " + tab_produto_fornecedor + " pf on\n"
                    + "            df.cod_fornecedor = pf.cod_fornecedor\n"
                    + "    group by\n"
                    + "        pf.cod_produto) div on p.cod_produto = div.cod_produto\n"
                    + "    left join " + tab_produto_loja + " pl on\n"
                    + "        p.cod_produto = pl.cod_produto and\n"
                    + "        pl.cod_loja = loja.cod_loja\n"
                    + "    left join (\n"
                    + "        select\n"
                    + "            pl.*,\n"
                    + "            ncm.num_ncm ncm,\n"
                    + "            cest.num_cest cest\n"
                    + "        from\n"
                    + "            (select\n"
                    + "                cod_produto,\n"
                    + "                min(COD_NCM) cod_ncm\n"
                    + "            from\n"
                    + "                " + tab_produto_loja + "\n"
                    + "            group by\n"
                    + "                cod_produto) pl\n"
                    + "            left join " + tab_ncm + " ncm on\n"
                    + "                pl.cod_ncm = ncm.cod_ncm\n"
                    + "            left join " + tab_cest + " cest on\n"
                    + "                ncm.cod_cest = cest.cod_cest\n"
                    + "    ) ncmcest on p.cod_produto = ncmcest.cod_produto\n"
                    + "    left join " + tab_tributacao + " trib on\n"
                    + "        pl.cod_tributacao = trib.cod_tributacao\n"
                    + "    left join " + tab_tributacao + " tribent on\n"
                    + "        pl.cod_trib_entrada = tribent.cod_tributacao\n"
                    + "order by\n"
                    + "    id"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setEan(rst.getString("ean"));
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                    imp.setQtdEmbalagemCotacao(rst.getInt("qtdembalagemcotacao"));
                    imp.setTipoEmbalagem(rst.getString("unidade"));
                    imp.seteBalanca(rst.getBoolean("ebalanca"));
                    imp.setValidade(rst.getInt("validade"));

                    long ean = Utils.stringToLong(imp.getEan());
                    if (imp.isBalanca() && (ean <= 999999) && removerDigitoProdutoBalanca) {
                        String eanAux = String.valueOf(ean);
                        eanAux = eanAux.substring(0, eanAux.length() - 1);
                        imp.setEan(eanAux);
                    }

                    if (imp.isBalanca() && (String.valueOf(ean).length() == 7)) {
                        String eanAux = String.valueOf(ean);
                        eanAux = eanAux.substring(1, 7);
                        imp.setEan(eanAux);
                    }

                    imp.setDescricaoCompleta(rst.getString("decricaocompleta"));
                    imp.setDescricaoGondola(rst.getString("decricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setCodMercadologico1(rst.getString("cod_secao"));
                    imp.setCodMercadologico2(rst.getString("cod_grupo"));
                    imp.setCodMercadologico3(rst.getString("cod_sub_grupo"));
                    imp.setIdFamiliaProduto(rst.getString("id_familia"));
                    imp.setFornecedorFabricante(rst.getString("fabricante"));
                    imp.setPesoBruto(rst.getDouble("pesobruto"));
                    imp.setPesoLiquido(rst.getDouble("pesoliquido"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setSituacaoCadastro(SituacaoCadastro.getById(rst.getInt("situacaocadastro")));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstCredito(rst.getInt("cst_pis_cof_entrada"));
                    imp.setPiscofinsCstDebito(rst.getInt("cst_pis_cof_saida"));
                    imp.setPiscofinsNaturezaReceita(Utils.stringToInt(rst.getString("natreceita")));

                    imp.setIcmsCstSaida(rst.getInt("icms_saida_cst"));
                    imp.setIcmsAliqSaida(rst.getDouble("icms_saida_aliq"));
                    imp.setIcmsReducaoSaida(rst.getDouble("icms_saida_reducao"));

                    imp.setIcmsCstSaidaForaEstado(rst.getInt("icms_saida_cst"));
                    imp.setIcmsAliqSaidaForaEstado(rst.getDouble("icms_saida_aliq"));
                    imp.setIcmsReducaoSaidaForaEstado(rst.getDouble("icms_saida_reducao"));

                    imp.setIcmsCstSaidaForaEstadoNF(rst.getInt("icms_saida_cst"));
                    imp.setIcmsAliqSaidaForaEstadoNF(rst.getDouble("icms_saida_aliq"));
                    imp.setIcmsReducaoSaidaForaEstadoNF(rst.getDouble("icms_saida_reducao"));

                    imp.setIcmsCstEntrada(rst.getInt("icms_entrada_cst"));
                    imp.setIcmsAliqEntrada(rst.getDouble("icms_entrada_aliq"));
                    imp.setIcmsReducaoEntrada(rst.getDouble("icms_entrada_reducao"));

                    imp.setIcmsCstEntradaForaEstado(rst.getInt("icms_entrada_cst"));
                    imp.setIcmsAliqEntradaForaEstado(rst.getDouble("icms_entrada_aliq"));
                    imp.setIcmsReducaoEntradaForaEstado(rst.getDouble("icms_entrada_reducao"));

                    imp.setIcmsCstConsumidor(rst.getInt("icms_saida_cst"));
                    imp.setIcmsAliqConsumidor(rst.getDouble("icms_saida_aliq"));
                    imp.setIcmsReducaoConsumidor(rst.getDouble("icms_saida_reducao"));
                    
                    imp.setPautaFiscalId(rst.getString("ncm"));
                    imp.setDivisao(rst.getString("divisao"));

                    result.add(imp);
                }
            }
        }

        return result;
    }
    
    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        String tab_fornecedor = "intersolid.tab_fornecedor",
            tab_cidade = "intersolid.tab_cidade",
            tab_fornecedor_bloqueio = "intersolid.tab_fornecedor_bloqueio",
            tab_loja_fornecedor = "intersolid.tab_loja_fornecedor";

        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    f.cod_fornecedor id,\n"
                    + "    f.des_fornecedor razao,\n"
                    + "    f.des_fantasia fantasia,\n"
                    + "    f.num_cgc cnpj,\n"
                    + "    f.num_insc_est ie,\n"
                    + "    f.flg_simples,\n"
                    + "    case coalesce(bloq.flg_bloqueado,'N') when 'S' then 1 else 0 end bloqueado,\n"
                    + "    f.des_endereco endereco,\n"
                    + "    f.num_endereco numero,\n"
                    + "    f.des_bairro bairro,\n"
                    + "    cd.des_cidade municipio,\n"
                    + "    cd.des_sigla uf,\n"
                    + "    f.num_cep cep,\n"
                    + "    f.num_fone tel_principal,\n"
                    + "    f.ped_min_val valor_minimo_pedido,\n"
                    + "    f.dta_cadastro datacadastro,\n"
                    + "    f.des_observacao observacao,\n"
                    + "    f.num_prazo prazoEntrega,\n"
                    + "    f.num_freq_visita prazovisita,\n"
                    + "    f.qtd_dia_carencia prazoseguranca,\n"
                    + "    f.des_contato,\n"
                    + "    f.des_email,\n"
                    + "    f.num_fax,\n"
                    + "    case f.micro_empresa when 'S' then 1 else 0 end microempresa,\n"
                    + "    case f.flg_produtor_rural when 'S' then 1 else 0 end produtorrural,\n"
                    + "    bloq.des_motivo_bloq,\n"
                    + "    f.des_email_vend email_vendedor,\n"
                    + "    f.num_celular celular,\n"
                    + "    f.num_med_cpgto condicaopagamento,\n"
                    + "    lf.inativo as ativo\n"
                    + "from\n"
                    + "    " + tab_fornecedor + " f\n"
                    + "    left join " + tab_cidade + " cd on\n"
                    + "        f.cod_cidade = cd.cod_cidade \n"
                    + "    left join " + tab_fornecedor_bloqueio + " bloq on\n"
                    + "        f.cod_fornecedor = bloq.cod_fornecedor\n"
                    + "    left join " + tab_loja_fornecedor + " lf on\n"
                    + "        lf.cod_fornecedor = f.cod_fornecedor and\n"
                    + "        lf.cod_loja = " + getLojaOrigem() + "\n"
                    + "order by\n"
                    + "    id"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    imp.setIe_rg(rst.getString("ie"));
                    imp.setBloqueado(rst.getBoolean("bloqueado"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setTel_principal(rst.getString("tel_principal"));
                    imp.setValor_minimo_pedido(rst.getDouble("valor_minimo_pedido"));
                    imp.setDatacadastro(rst.getDate("datacadastro"));
                    imp.setPrazoEntrega(rst.getInt("prazoEntrega"));
                    imp.setPrazoVisita(rst.getInt("prazovisita"));
                    imp.setPrazoSeguranca(rst.getInt("prazoseguranca"));
                    imp.setAtivo("S".equals(rst.getString("ativo")));

                    imp.setObservacao(
                            new StringBuilder()
                                    .append("CONTATO ").append(rst.getString("des_contato"))
                                    .append(" FONE.: ").append(rst.getString("tel_principal"))
                                    .append(" CEL.: ").append(rst.getString("celular"))
                                    .append(" FAX.: ").append(rst.getString("num_fax"))
                                    .append(" EMAIL VEND.: ").append(rst.getString("email_vendedor"))
                                    .append(" OUTROS EMAIL: ").append(rst.getString("des_email"))
                                    .append(" BLOQ.: ").append(rst.getString("des_motivo_bloq"))
                                    .append(" OBS.: ").append(rst.getString("observacao"))
                                    .toString()
                    );

                    if (!"".equals(Utils.acertarTexto(rst.getString("des_contato")))
                            || !"".equals(Utils.acertarTexto(rst.getString("tel_principal")))
                            || !"".equals(Utils.acertarTexto(rst.getString("email_vendedor")))) {
                        imp.addContato("1", rst.getString("des_contato"), rst.getString("tel_principal"), rst.getString("celular"), TipoContato.COMERCIAL, "");
                    }
                    String fax = Utils.formataNumero(rst.getString("num_fax"));
                    if (!"0".equals(fax)) {
                        imp.addContato("2", "FAX", fax, "", TipoContato.COMERCIAL, "");
                    }
                    for (String email : Utils.formataEmail(rst.getString("email_vendedor"), 200).split(";")) {
                        if (email != null && !email.equals("")) {
                            imp.addEmail("E-MAIL COMERCIAL", email, TipoContato.COMERCIAL);
                        }
                    }

                    if ((rst.getString("des_email") != null)
                            && (!rst.getString("des_email").trim().isEmpty())) {

                        for (String email : rst.getString("des_email").split(";")) {
                            if (email != null && !email.equals("")) {
                                imp.addEmail("NFE", email, TipoContato.NFE);
                            }
                        }
                    }

                    if (rst.getBoolean("produtorrural")) {
                        long cnpj = Utils.stringToLong(rst.getString("cnpj"));
                        if (cnpj > 99999999999L) {
                            imp.setTipoEmpresa(TipoEmpresa.PRODUTOR_RURAL_JURIDICO);
                        } else {
                            imp.setTipoEmpresa(TipoEmpresa.PRODUTOR_RURAL_FISICA);
                        }
                    } else if (rst.getBoolean("microempresa")) {
                        imp.setTipoEmpresa(TipoEmpresa.ME_SIMPLES);
                    }
                    imp.setCondicaoPagamento(Utils.stringToInt(rst.getString("condicaopagamento")));

                    result.add(imp);
                }
            }
        }

        return result;
    }
    
    @Override
    public List<OfertaIMP> getOfertas(Date dataTermino) throws Exception {
        List<OfertaIMP> result = new ArrayList<>();
        String tab_produto_historico = "intersolid.tab_produto_historico",
            tab_produto_loja = "intersolid.tab_produto_loja";

        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select distinct\n"
                    + "    pl.cod_produto,\n"
                    + "    (select dta_ult_alt_preco_venda "
                    + "       from " + tab_produto_historico + " "
                    + "      where cod_loja = pl.cod_loja "
                    + "        and cod_produto = pl.cod_produto) dtinicio,\n"
                    + "    pl.dta_valida_oferta,\n"
                    + "    pl.val_oferta\n"
                    + "from\n"
                    + "    " + tab_produto_loja + " pl\n"
                    + "where\n"
                    + "    pl.cod_loja = " + getLojaOrigem() + "\n"
                    + "    and not pl.dta_valida_oferta is null\n"
                    + "    and pl.dta_valida_oferta >= current_date\n"
                    + "order by\n"
                    + "    1"
            )) {
                while (rst.next()) {
                    OfertaIMP imp = new OfertaIMP();

                    imp.setIdProduto(rst.getString("cod_produto"));
                    imp.setDataInicio(rst.getDate("dtinicio"));
                    imp.setDataFim(rst.getDate("dta_valida_oferta"));
                    imp.setPrecoOferta(rst.getDouble("val_oferta"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();
        String tab_produto_fornecedor = "intersolid.tab_produto_fornecedor",
            tab_divisao_fornecedor = "intersolid.tab_divisao_fornecedor";

        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    pf.cod_fornecedor,\n"
                    + "    pf.cod_produto,\n"
                    + "    pf.des_referencia,\n"
                    + "    max(div.cod_divisao) id_divisao,\n"
                    + "    max(pf.dta_alteracao) dta_alteracao,\n"
                    + "    coalesce(nullif(max(pf.qtd_embalagem_compra),0), 1) qtd_embalagem_compra,\n"
                    + "    max(pf.val_custo_embalagem) / coalesce(nullif(max(pf.qtd_embalagem_compra),0), 1) val_custo_embalagem\n"
                    + "from\n"
                    + "    " + tab_produto_fornecedor + " pf\n"
                    + "    left join " + tab_divisao_fornecedor + " div on\n"
                    + "        div.cod_fornecedor = pf.cod_fornecedor\n"
                    //+ "where trim(pf.des_referencia) != ''\n"
                    + "group by\n"
                    + "    pf.cod_fornecedor,\n"
                    + "    pf.cod_produto,\n"
                    + "    pf.des_referencia,\n"
                    + "    pf.val_custo_embalagem\n"
                    + "order by 1,2,3"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setIdFornecedor(rst.getString("cod_fornecedor"));
                    imp.setIdProduto(rst.getString("cod_produto"));
                    imp.setCodigoExterno(rst.getString("des_referencia"));
                    imp.setDataAlteracao(rst.getDate("dta_alteracao"));
                    imp.setQtdEmbalagem(rst.getInt("qtd_embalagem_compra"));
                    imp.setCustoTabela(rst.getDouble("val_custo_embalagem"));
                    imp.setIdDivisaoFornecedor(rst.getString("id_divisao"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        String tab_cliente = "intersolid.tab_cliente",
            tab_cidade = "intersolid.tab_cidade",
            tab_cliente_status_pdv = "intersolid.tab_cliente_status_pdv",
            tab_loja_cliente = "intersolid.tab_loja_cliente";

        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    c.cod_cliente id,\n"
                    + "    c.num_cgc cnpj,\n"
                    + "    coalesce(nullif(trim(c.num_insc_est), ''), c.num_rg) inscricaoestadual,\n"
                    + "    c.des_cliente razao,\n"
                    + "    coalesce(nullif(trim(c.des_fantasia), ''), c.des_cliente) fantasia,\n"
                    + "    c.des_endereco endereco,\n"
                    + "    c.num_endereco numero,\n"
                    + "    c.des_complemento complemento,\n"
                    + "    c.des_bairro bairro,\n"
                    + "    cd.des_cidade municipio,\n"
                    + "    cd.des_sigla estado,\n"
                    + "    c.num_cep cep,\n"
                    + "    c.flg_est_civil,\n"
                    + "    c.dta_nascimento datanascimento,\n"
                    + "    c.dta_cadastro datacadastro,\n"
                    + "    case c.flg_sexo when 1 then 0 else 1 end sexo,\n"
                    + "    c.des_empresa_trab empresa,\n"
                    + "    c.dta_admissao_trab dataadmissao,\n"
                    + "    c.des_cargo cargo,\n"
                    + "    cast(c.val_renda as numeric(11,2)) salario,\n"
                    + "    case when\n"
                    + "    length(cast(cast(c.val_limite_conv as number) as varchar(15))) = 10\n"
                    + "    then 0 else c.val_limite_conv end limitecredito,\n"
                    + "    c.des_conjuge nomeconjuge,\n"
                    + "    c.des_pai nomepai,\n"
                    + "    c.des_mae nomemae,\n"
                    + "    c.des_observacao observacao2,\n"
                    + "    c.num_dia_fecha diavencimento,\n"
                    + "    c.num_fone,\n"
                    + "    c.num_fax,\n"
                    + "    c.num_celular,\n"
                    + "    c.des_email,\n"
                    + "    case sta.negativar when 'S' then 1 else 0 end bloqueado,\n"
                    + "    case c.flg_envia_codigo when 'S' then 1 else 0 end permiterotativo,\n"
                    + "    sta.des_status_pdv observacao1,\n"
                    + "    case lc.inativo when 'S' then 0 else 1 end ativo\n"
                    + "from\n"
                    + "    " + tab_cliente + " c\n"
                    + "    left join " + tab_cidade + " cd on\n"
                    + "        c.cod_cidade = cd.cod_cidade\n"
                    + "    left join " + tab_cliente_status_pdv + " sta on\n"
                    + "        c.cod_status_pdv = sta.cod_status_pdv\n"
                    + "    left join " + tab_loja_cliente + " lc on\n"
                    + "        lc.cod_cliente = c.cod_cliente and\n"
                    + "        lc.cod_loja = " + getLojaOrigem() + "\n"
                    + "where\n"
                    + "   c.des_cliente <> 'CADASTRO AUTOMATICO'\n"
                    + "order by\n"
                    + "    1"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rst.getString("id"));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setInscricaoestadual(rst.getString("inscricaoestadual"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    //setSituacaoCadastro(imp, rst.getInt("ativobloq"));
                    imp.setBloqueado(rst.getBoolean("bloqueado"));
                    imp.setAtivo(rst.getBoolean("ativo"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setUf(rst.getString("estado"));
                    imp.setCep(rst.getString("cep"));
                    //imp.set(rst.getString("flg_est_civil"));
                    imp.setDataNascimento(rst.getDate("datanascimento"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setSexo(rst.getInt("sexo") == 0 ? TipoSexo.FEMININO : TipoSexo.MASCULINO);
                    imp.setEmpresa(rst.getString("empresa"));
                    imp.setDataAdmissao(rst.getDate("dataadmissao"));
                    imp.setCargo(rst.getString("cargo"));
                    imp.setSalario(rst.getDouble("salario"));
                    imp.setValorLimite(rst.getDouble("limitecredito"));
                    imp.setNomeConjuge(rst.getString("nomeconjuge"));
                    imp.setNomePai(rst.getString("nomepai"));
                    imp.setNomeMae(rst.getString("nomemae"));
                    imp.setDiaVencimento(rst.getInt("diavencimento"));
                    imp.setTelefone(rst.getString("num_fone"));
                    imp.setFax(rst.getString("num_fax"));
                    imp.setCelular(rst.getString("num_celular"));
                    imp.setEmail(rst.getString("des_email"));
                    imp.setPermiteCreditoRotativo(rst.getBoolean("permiterotativo"));

                    imp.setObservacao2(
                            new StringBuilder()
                                    .append(" FONE.: ").append(rst.getString("num_fone"))
                                    .append(" CEL.: ").append(rst.getString("num_celular"))
                                    .append(" FAX.: ").append(rst.getString("num_fax"))
                                    .append(" EMAIL.: ").append(rst.getString("des_email"))
                                    .append(" OBS.: ").append(rst.getString("observacao2"))
                                    .toString()
                    );

                    result.add(imp);
                }
            }
        }

        return result;
    }
    
    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();
        String tab_fluxo = "intersolid.tab_fluxo",
            tab_entidade = "intersolid.tab_entidade";

        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    f.cod_loja,\n"
                    + "    f.tipo_conta,\n"
                    + "    f.tipo_parceiro,\n"
                    + "    f.cod_parceiro,\n"
                    + "    f.num_registro,\n"
                    + "    f.dta_emissao dataemissao, \n"
                    + "    f.num_docto numerocupom,\n"
                    + "    f.num_pdv ecf,\n"
                    + "    f.val_parcela valor,\n"
                    + "    f.des_observacao observacao,\n"
                    + "    f.cod_parceiro idcliente, \n"
                    + "    f.dta_vencimento datavencimento,\n"
                    + "    f.num_parcela parcela, \n"
                    + "    f.val_juros juros,\n"
                    + "    f.num_cgc_cpf cpf,\n"
                    + "    f.cod_entidade || ' - ' || e.des_entidade pagamento\n"
                    + "from\n"
                    + "    " + tab_fluxo + " f\n"
                    + "    left join " + tab_entidade + " e on f.cod_entidade = e.cod_entidade\n"
                    + "where\n"
                    + "    f.cod_loja = " + getLojaOrigem() + "\n"
                    + "    and f.tipo_conta = 1\n"
                    + "    and tipo_parceiro = 0\n"
                    + "    and f.flg_quitado = 'N'\n"
                    + "    and not f.num_docto is null\n"
                    + "    and f.cod_entidade in (" + implodeList(entidadesCreditoRotativo) + ")\n\n"
                    + "order by\n"
                    + "    1, 2, 3, 4, 5"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();

                    imp.setId(String.format(
                            "%d-%d-%d-%d-%d",
                            rst.getInt("cod_loja"),
                            rst.getInt("tipo_conta"),
                            rst.getInt("tipo_parceiro"),
                            rst.getInt("cod_parceiro"),
                            rst.getInt("num_registro")
                    ));
                    imp.setDataEmissao(rst.getDate("dataemissao"));
                    imp.setNumeroCupom(rst.getString("numerocupom"));
                    imp.setEcf(rst.getString("ecf"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setObservacao(rst.getString("pagamento") + " - " + rst.getString("observacao"));
                    imp.setIdCliente(rst.getString("idcliente"));
                    imp.setDataVencimento(rst.getDate("datavencimento"));
                    imp.setParcela(rst.getInt("parcela"));
                    imp.setJuros(rst.getDouble("juros"));
                    imp.setCnpjCliente(rst.getString("cpf"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ChequeIMP> getCheques() throws Exception {
        List<ChequeIMP> result = new ArrayList<>();
        String tab_fluxo = "intersolid.tab_fluxo",
            tab_entidade = "intersolid.tab_entidade",
            tab_cliente = "intersolid.tab_cliente";

        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    f.cod_loja || '-' || f.tipo_conta || '-' || f.tipo_parceiro || '-' || f.cod_parceiro || '-' || f.num_registro id,\n"
                    + "    f.cod_entidade || ' - ' || e.des_entidade pagamento,\n"
                    + "    f.num_cgc_cpf cpf,\n"
                    + "    c.num_cgc cpf2,\n"
                    + "    f.dta_emissao dataemissao,\n"
                    + "    f.num_cupom_fiscal numerocupom,\n"
                    + "    f.num_pdv pdv,\n"
                    + "    f.val_parcela valor,\n"
                    + "    c.num_rg rg,\n"
                    + "    c.num_fone fone,\n"
                    + "    c.des_cliente nome,\n"
                    + "    f.des_observacao\n"
                    + "from\n"
                    + "    " + tab_fluxo + " f\n"
                    + "    left join " + tab_entidade + " e on f.cod_entidade = e.cod_entidade\n"
                    + "    left join " + tab_cliente + " c on f.cod_parceiro = c.cod_cliente\n"
                    + "where\n"
                    + "    f.cod_loja = " + getLojaOrigem() + "\n"
                    + "    and f.tipo_conta = 1\n"
                    + "    and tipo_parceiro = 0\n"
                    + "    and f.flg_quitado = 'N'\n"
                    + "    and f.cod_entidade in (" + implodeList(entidadesCheques) + ")\n"
                    + "order by\n"
                    + "    f.dta_emissao, id"
            )) {
                while (rst.next()) {
                    ChequeIMP imp = new ChequeIMP();

                    imp.setId(rst.getString("id"));
                    imp.setCpf(rst.getString("cpf"));
                    imp.setDate(rst.getDate("dataemissao"));
                    imp.setNumeroCupom(rst.getString("numerocupom"));
                    imp.setEcf(rst.getString("pdv"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setRg(rst.getString("rg"));
                    imp.setTelefone(rst.getString("fone"));
                    imp.setNome(rst.getString("nome"));
                    imp.setObservacao(rst.getString("pagamento") + " - " + rst.getString("des_observacao"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ContaPagarIMP> getContasPagar() throws Exception {
        List<ContaPagarIMP> result = new ArrayList<>();
        String tab_fluxo = "intersolid.tab_fluxo",
            tab_entidade = "intersolid.tab_entidade";

        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    f.cod_loja || '-' || f.tipo_conta || '-' || f.tipo_parceiro || '-' || f.cod_parceiro || '-' || f.num_registro id,\n"
                    + "    f.cod_parceiro fornecedor,\n"
                    + "    f.cod_entidade || ' - ' || e.des_entidade pagamento,\n"
                    + "    f.num_docto,\n"
                    + "    f.dta_emissao dataemissao,\n"
                    + "    f.dta_vencimento datavencimento,\n"
                    + "    f.dta_alteracao dataalteracao,\n"
                    + "    f.val_parcela valor,\n"
                    + "    f.des_cc,\n"
                    + "    f.num_parcela parcela,\n"
                    + "    f.val_juros juros,\n"
                    + "    f.num_cgc_cpf cpf,\n"
                    + "    f.cod_entidade\n"
                    + "from\n"
                    + "    " + tab_fluxo + " f\n"
                    + "    left join " + tab_entidade + " e on f.cod_entidade = e.cod_entidade\n"
                    + "where\n"
                    + "    f.cod_loja = " + getLojaOrigem() + "\n"
                    + "    and f.tipo_conta = 0\n"
                    + "    and tipo_parceiro = 1\n"
                    + "    and f.flg_quitado = 'N'\n"
                    + "    and not f.num_docto is null"
            )) {
                while (rst.next()) {
                    ContaPagarIMP imp = new ContaPagarIMP();

                    imp.setId(rst.getString("id"));
                    imp.setIdFornecedor(rst.getString("fornecedor"));
                    imp.setNumeroDocumento(rst.getString("num_docto"));
                    imp.setDataEmissao(rst.getDate("dataemissao"));
                    imp.setDataEntrada(rst.getDate("dataemissao"));
                    imp.setDataHoraAlteracao(rst.getTimestamp("dataalteracao"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setObservacao(rst.getString("pagamento") + " - " + rst.getString("des_cc"));
                    ContaPagarVencimentoIMP venc = imp.addVencimento(rst.getDate("datavencimento"), rst.getDouble("valor"));

                    switch (rst.getInt("cod_entidade")) {
                        case 1:
                            venc.setTipoPagamento(TipoPagamento.DINHEIRO);
                            break;//DINHEIRO                   
                        case 2:
                            venc.setTipoPagamento(TipoPagamento.CARTAO_CREDITO);
                            break;//CARTAO DE CREDITO          
                        case 3:
                            venc.setTipoPagamento(TipoPagamento.CHEQUE);
                            break;//CHEQUE A VISTA             
                        case 4:
                            venc.setTipoPagamento(TipoPagamento.CHEQUE);
                            break;//CHEQUE A PRAZO             
                        case 5:
                            venc.setTipoPagamento(TipoPagamento.S_BOLETO);
                            break;//VALE - CONVENIO            
                        case 6:
                            venc.setTipoPagamento(TipoPagamento.S_BOLETO);
                            break;//TICKET                     
                        case 7:
                            venc.setTipoPagamento(TipoPagamento.CHEQUE);
                            break;//CHEQUE SEM DADOS           
                        case 9:
                            venc.setTipoPagamento(TipoPagamento.BOLETO_BANCARIO);
                            break;//BOLETO                     
                        case 10:
                            venc.setTipoPagamento(TipoPagamento.CHEQUE);
                            break;//CHEQUE A PRAZO PG          
                        case 11:
                            venc.setTipoPagamento(TipoPagamento.S_BOLETO);
                            break;//BONIFICACAO DE MERCADORIA  
                        case 13:
                            venc.setTipoPagamento(TipoPagamento.CARTAO_DEBITO);
                            break;//VISA ELECTRON              
                        case 14:
                            venc.setTipoPagamento(TipoPagamento.CARTAO_CREDITO);
                            break;//VISA CREDITO               
                        case 15:
                            venc.setTipoPagamento(TipoPagamento.CARTAO_DEBITO);
                            break;//MAESTRO                    
                        case 16:
                            venc.setTipoPagamento(TipoPagamento.CARTAO_CREDITO);
                            break;//MASTERCARD                 
                        case 18:
                            venc.setTipoPagamento(TipoPagamento.CARTAO_DEBITO);
                            break;//SODEXO                     
                        case 20:
                            venc.setTipoPagamento(TipoPagamento.S_BOLETO);
                            break;//CONTRA VALE                
                        case 21:
                            venc.setTipoPagamento(TipoPagamento.CARTAO_DEBITO);
                            break;//CARTAO DEBITO              
                        case 22:
                            venc.setTipoPagamento(TipoPagamento.CARTAO_DEBITO);
                            break;//CARTAO BALCAO              
                        case 23:
                            venc.setTipoPagamento(TipoPagamento.CARTAO_CREDITO);
                            break;//CARTAO PARCELADO           
                        case 24:
                            venc.setTipoPagamento(TipoPagamento.S_BOLETO);
                            break;//QUEBRA DE CAIXA            
                        case 25:
                            venc.setTipoPagamento(TipoPagamento.S_BOLETO);
                            break;//CONVENIO WEB               
                        case 26:
                            venc.setTipoPagamento(TipoPagamento.CARTAO_DEBITO);
                            break;//KAPP CARD                  
                        case 27:
                            venc.setTipoPagamento(TipoPagamento.S_BOLETO);
                            break;//DESCONTO BOLETO            
                        case 28:
                            venc.setTipoPagamento(TipoPagamento.DEPOSITO);
                            break;//DEPOSITO BANCARIO          
                        case 29:
                            venc.setTipoPagamento(TipoPagamento.CARTAO_DEBITO);
                            break;//CARTAO KAPPCARD            
                        case 30:
                            venc.setTipoPagamento(TipoPagamento.S_BOLETO);
                            break;//CONVENIO NOVAMERIC  
                        default:
                            venc.setTipoPagamento(TipoPagamento.BOLETO_BANCARIO);
                            break;//BOLETO   
                    }

                    result.add(imp);
                }
            }
        }

        return result;
    }
    
    public List<Entidade> getEntidades() throws SQLException {
        List<Entidade> result = new ArrayList<>();
        String tabela = "intersolid.tab_entidade";

        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "cod_entidade, "
                    + "des_entidade "
                    + "from " + tabela + "\n"
                    + "order by cod_entidade"
            )) {
                while (rst.next()) {
                    result.add(new Entidade(rst.getInt("cod_entidade"), rst.getString("des_entidade")));
                }
            }
        }

        return result;
    }
        
    private String implodeList(List<Entidade> entidades) {
        StringBuilder builder = new StringBuilder();

        for (Iterator<Entidade> iterator = entidades.iterator(); iterator.hasNext();) {
            builder.append(iterator.next().getId());
            if (iterator.hasNext()) {
                builder.append(",");
            }
        }

        return builder.toString();
    }
    
    public String exibirMensagemComComboBox(String titulo, boolean isEstado) throws Exception {
        JComboBox comboBox = new JComboBox<>();
        comboBox.setModel(new DefaultComboBoxModel<>());
        
        List<String> dados = new LocalDAO().getSiglas();

        dados.forEach(dado -> comboBox.addItem(dado));
        JPanel panel = new JPanel();
        panel.setLayout(new java.awt.BorderLayout());
        panel.add(new JLabel(
                "<html>"
                + "<body>"
                + "<div style=\"width: 100%; border-bottom: 1px solid red; margin-bottom: 10px\">"
                + "<p style=\"display: inline-block;\"><font size=3 face=\"arial\">Escolha o estado para tributação da pauta:</font></p>"
                + "<br>"
                + "</div>"
                + "</body>"
                + "</html>"), java.awt.BorderLayout.NORTH);
        panel.add(comboBox, java.awt.BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(
                null,
                panel,
                titulo,
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            String escolha = (String) comboBox.getSelectedItem();
            String[] primeiraString= escolha.split(" ");
            return primeiraString[0];
        } else {
            throw new Exception("É obrigatório escolher um item!");
        }
    }
    
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");
    public static final SimpleDateFormat DATE_FORMAT_ORACLE = new SimpleDateFormat("dd/MM/yyyy");
    private String dataInicioVenda;
    private String dataTerminoVenda;
    
    public void setDataVendaInicio(Date dataVendaIncio) {
        this.dataInicioVenda = DATE_FORMAT_ORACLE.format(dataVendaIncio);
    }
    
    public void setDataVendaTermino(Date dataVendaTermino) {
        this.dataTerminoVenda = DATE_FORMAT_ORACLE.format(dataVendaTermino);
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

        private Statement stm;
        private ResultSet rst;
        private VendaIMP next;

        public VendaIterator(String idLojaCliente, String dataInicio, String dataTermino) {
            try {
                this.stm = ConexaoOracle.getConexao().createStatement();
                this.rst = stm.executeQuery(
                        "select\n"
                        + "    v.num_ident id,\n"
                        + "    v.num_cupom_fiscal cupomfiscal,\n"
                        + "    v.num_pdv ecf,\n"
                        + "    to_char(v.dta_saida, 'yyyy-MM-dd') data,\n"
                        + "    v.cod_cliente id_cliente,\n"
                        + " min(substr(des_hora, 1, 2)||':'||substr(des_hora, 3, 4)||':00') horaInicio, max(substr(des_hora, 1, 2)||':'||substr(des_hora, 3, 4)||':00') horaTermino,\n"
                        + "    min(case when v.flg_cupom_cancelado = 'N' then 0 else 1 end) cancelado,\n"
                        + "    sum(coalesce(v.val_total_produto, 0) + coalesce(v.val_desconto, 0)) subtotalimpressora,\n"
                        + "    sum(v.val_desconto) desconto,\n"
                        + "    sum(v.val_acrescimo) acrescimo,\n"
                        + "    pdv.num_serie_fabr numeroserie,\n"
                        + "    pdv.des_modelo modeloimpressora,\n"
                        + "    c.num_cgc cpf,\n"
                        + "    c.des_cliente nomecliente\n"
                        + "from\n"
                        + "intersolid.tab_produto_pdv v \n"
                        + " left join intersolid.tab_pdv pdv on v.num_pdv = pdv.cod_pdvint and v.cod_loja = pdv.cod_loja\n"
                        + " left join intersolid.tab_cliente c on v.cod_cliente = c.cod_cliente\n"
                        + "where\n"
                        + "    v.cod_loja = " + idLojaCliente + "\n"
                        + " and v.dta_saida >= to_date('" + dataInicio + "', 'DD/MM/YYYY') and v.dta_saida <= to_date('" + dataTermino + "', 'DD/MM/YYYY')\n"
                        + "    and v.num_ident != 0\n"
                        + "    and v.tipo_ind = 0\n"
                        + "group by\n"
                        + "    v.num_ident,\n"
                        + "    v.num_cupom_fiscal,\n"
                        + "    v.num_pdv,\n"
                        + "    v.cod_cliente,\n"
                        + "    pdv.num_serie_fabr,\n"
                        + "    pdv.des_modelo,\n"
                        + "    c.num_cgc,\n"
                        + "    c.des_cliente,\n"
                        + "    to_char(v.dta_saida, 'yyyy-MM-dd')"
                );
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Erro ao obter a venda", ex);
                throw new RuntimeException(ex);
            }
        }

        @Override
        public boolean hasNext() {
            processarNext();
            return next != null;
        }

        @Override
        public VendaIMP next() {
            processarNext();
            VendaIMP result = next;
            next = null;
            return result;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported.");
        }

        private void processarNext() {
            SimpleDateFormat timestampDate = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat timestamp = new SimpleDateFormat("yyyy-MM-dd hh:mm");
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            try {
                if (next == null) {
                    if (rst.next()) {
                        
                        String horaInicio = rst.getString("data") + " " + rst.getString("horaInicio");
                        String horaTermino = rst.getString("data") + " " + rst.getString("horaTermino");
                        
                        
                        next = new VendaIMP();

                        next.setId(rst.getInt("ecf") + "-" + rst.getString("id"));
                        next.setNumeroCupom(rst.getInt("cupomfiscal"));
                        next.setEcf(rst.getInt("ecf"));
                        next.setData(rst.getDate("data"));
                        next.setIdClientePreferencial(rst.getString("id_cliente"));
                        next.setHoraInicio(timestamp.parse(horaInicio));
                        next.setHoraTermino(timestamp.parse(horaTermino));
                        next.setCancelado(rst.getBoolean("cancelado"));
                        next.setSubTotalImpressora(rst.getDouble("subtotalimpressora"));
                        next.setValorDesconto(rst.getDouble("desconto"));
                        next.setValorAcrescimo(rst.getDouble("acrescimo"));
                        next.setNumeroSerie(rst.getString("numeroserie"));
                        next.setModeloImpressora(rst.getString("modeloimpressora"));
                        next.setCpf(rst.getString("cpf"));
                        next.setNomeCliente(rst.getString("nomecliente"));
                    }
                }
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Erro ao obter a venda", ex);
                throw new RuntimeException(ex);
            }
        }

    }

    private static class Tributacao {

        int cst;
        double aliq;
        double reducao;

        public Tributacao(int cst, double aliq, double reducao) {
            this.cst = cst;
            this.aliq = aliq;
            this.reducao = reducao;
        }

    }

    private static class VendaItemIterator implements Iterator<VendaItemIMP> {

        private Statement stm;
        private ResultSet rst;
        private VendaItemIMP next;
        private Map<Integer, Tributacao> tributacao = new HashMap<>();

        public VendaItemIterator(String idLojaCliente, String dataInicio, String dataTermino) {
            try {
                try (Statement st = ConexaoOracle.getConexao().createStatement()) {
                    try (ResultSet rs = st.executeQuery(
                            "select\n"
                            + "    t.cod_tributacao,\n"
                            + "    cast(t.cod_sit_tributaria as integer) cst,\n"
                            + "    t.val_icms aliq,\n"
                            + "    t.val_reducao_base_calculo reducao\n"
                            + "from\n"
                            + "intersolid.tab_tributacao t\n"
                            + "order by\n"
                            + "    1"
                    )) {
                        while (rs.next()) {
                            tributacao.put(
                                    rs.getInt("cod_tributacao"),
                                    new Tributacao(
                                            rs.getInt("cst"),
                                            rs.getDouble("aliq"),
                                            rs.getDouble("reducao")
                                    )
                            );
                        }
                    }
                }

                stm = ConexaoOracle.getConexao().createStatement();
                rst = stm.executeQuery(
                        "select\n"
                        + "    v.num_registro id,\n"
                        + "    v.num_pdv ecf,\n"
                        + "    v.num_ident idvenda,\n"
                        + "    v.cod_produto idproduto,\n"
                        + "    p.des_reduzida descricaoreduzida,\n"
                        + "    v.qtd_total_produto quantidade,\n"
                        + "    coalesce(v.val_total_produto, 0) + coalesce(v.val_desconto, 0) total,\n"
                        + "    case when v.flg_cupom_cancelado = 'N' then 0 else 1 end cancelado,\n"
                        + "    v.val_desconto desconto,\n"
                        + "    v.val_acrescimo acrescimo,\n"
                        + "    v.cod_ean ean,\n"
                        + "    p.des_unidade_venda unidade,\n"
                        + "    v.cod_tributacao\n"
                        + "from\n"
                        + "intersolid.tab_produto_pdv v\n"
                        + "join intersolid.tab_produto p on v.cod_produto = p.cod_produto\n"
                        + "where\n"
                        + "    v.cod_loja = " + idLojaCliente + "\n"
                        + "    and v.dta_saida >= to_date('" + dataInicio + "', 'DD/MM/YYYY') and v.dta_saida <= to_date('" + dataTermino + "', 'DD/MM/YYYY') \n"
                        + "    and v.num_ident != 0\n"
                        + "    and v.tipo_ind = 0\n"
                        + "order by\n"
                        + "    id"
                );

            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Erro ao obter a venda", ex);
                throw new RuntimeException(ex);
            }
        }

        @Override
        public boolean hasNext() {
            processarNext();
            return next != null;
        }

        @Override
        public VendaItemIMP next() {
            processarNext();
            VendaItemIMP result = next;
            next = null;
            return result;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported.");
        }

        private void processarNext() {
            try {
                if (next == null) {
                    if (rst.next()) {
                        next = new VendaItemIMP();

                        next.setId(rst.getString("id"));
                        next.setVenda(rst.getInt("ecf") + "-" + rst.getString("idvenda"));
                        next.setProduto(rst.getString("idproduto"));
                        next.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                        next.setQuantidade(rst.getDouble("quantidade"));
                        next.setTotalBruto(rst.getDouble("total"));
                        next.setCancelado(rst.getBoolean("cancelado"));
                        next.setValorDesconto(rst.getDouble("desconto"));
                        next.setValorAcrescimo(rst.getDouble("acrescimo"));
                        next.setCodigoBarras(rst.getString("ean"));
                        next.setUnidadeMedida(rst.getString("unidade"));
                        Tributacao trib = this.tributacao.get(rst.getInt("cod_tributacao"));
                        if (trib != null) {
                            next.setIcmsCst(trib.cst);
                            next.setIcmsAliq(trib.aliq);
                            next.setIcmsReduzido(trib.reducao);
                        }
                    }
                }
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Erro ao obter a venda", ex);
                throw new RuntimeException(ex);
            }
        }

    }

    @Override
    public List<PautaFiscalIMP> getPautasFiscais(Set<OpcaoFiscal> opcoes) throws Exception {
        exibirMensagemComComboBox("Escolha o estado", true);
        
        List<PautaFiscalIMP> result = new ArrayList<>();
        String tab_ncm_uf = "intersolid.tab_ncm_uf",
            tab_loja = "intersolid.tab_loja",
            tab_ncm = "intersolid.tab_ncm";

        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    nuf.cod_ncm,\n"
                    + "    ncm.num_ncm ncm,\n"
                    + "    nuf.des_sigla uf,\n"
                    + "    nuf.per_iva,\n"
                    + "    nuf.cod_trib_entrada,\n"
                    + "    nuf.cod_tributacao,\n"
                    + "    coalesce((select first 1 cod_trib_entrada from " + tab_ncm_uf + " where cod_ncm = nuf.cod_ncm and des_sigla = uf.uf), nuf.cod_trib_entrada) cod_tributacao_entrada_foraestado,\n"
                    + "    coalesce((select first 1 cod_tributacao from " + tab_ncm_uf + " where cod_ncm = nuf.cod_ncm and des_sigla = uf.uf), nuf.cod_tributacao) cod_tributacao_foraestado\n"
                    + "from\n"
                    + "    tab_ncm_uf nuf\n"
                    + "    join " + tab_loja + " lj on lj.cod_loja = " + getLojaOrigem() + "\n"
                    + "    join (select '" + siglaEstadoPauta + "' uf from rdb$database) uf on 1 = 1\n"
                    + "    join " + tab_ncm + " ncm on ncm.cod_ncm = nuf.cod_ncm\n"
                    + "where\n"
                    + "    nuf.des_sigla = lj.des_sigla\n"
                    + "order by\n"
                    + "    ncm, uf"
            )) {
                while (rst.next()) {
                    PautaFiscalIMP imp = new PautaFiscalIMP();

                    imp.setId(rst.getString("ncm"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setUf(rst.getString("uf"));
                    imp.setIva(rst.getDouble("per_iva"));
                    imp.setIvaAjustado(rst.getDouble("per_iva"));
                    imp.setTipoIva(TipoIva.PERCENTUAL);
                    imp.setAliquotaDebitoId(rst.getString("cod_tributacao"));
                    imp.setAliquotaCreditoId(rst.getString("cod_trib_entrada"));
                    imp.setAliquotaDebitoForaEstadoId(rst.getString("cod_tributacao_foraestado"));
                    imp.setAliquotaCreditoForaEstadoId(rst.getString("cod_tributacao_entrada_foraestado"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<NotaFiscalIMP> getNotasFiscais() throws Exception {
        List<NotaFiscalIMP> result = new ArrayList<>();
        String tab_nf = "intersolid.tab_nf";

        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    nf.cod_parceiro,\n"
                    + "    nf.tipo_parceiro,\n"
                    + "    nf.num_nf,\n"
                    + "    nf.num_serie_nf,\n"
                    + "    nf.tipo_ident,\n"
                    + "    nf.tipo_operacao,\n"
                    + "    nf.tipo_nf,\n"
                    + "    nf.num_serie_nf serie,\n"
                    + "    nf.num_nf numeronota,\n"
                    + "    nf.dta_emissao dataemissao,\n"
                    + "    nf.dta_entrada dataentradasaida,\n"
                    + "    nf.val_total_nf total_nota,\n"
                    + "    nf.cod_transportadora,\n"
                    + "    nf.cod_motorista,\n"
                    + "    nf.tipo_frete,\n"
                    + "    nf.tipo_pagamento,\n"
                    + "    nf.tipo_emitente,\n"
                    + "    nf.obs_fiscal,\n"
                    + "    nf.obs_livre,\n"
                    + "    nf.val_peso_cte,\n"
                    + "    nf.flg_cancelado,\n"
                    + "    nf.flg_denegada,\n"
                    + "    nf.flg_inutilizada,\n"
                    + "    nf.num_chave_acesso,\n"
                    + "    nf.dta_alteracao\n"
                    + "from\n"
                    + "    " + tab_nf + " nf\n"
                    + "where\n"
                    + "    nf.cod_loja = " + getLojaOrigem() + " and\n"
                    + "    nf.tipo_operacao in (0, 1) and\n"
                    + "    nf.tipo_nf in (0, 1) and\n"
                    + //"    nf.tipo_operacao = 0 and\n" + //TODO: Excluir esta linha quando incluir a nota de saida
                    "    nf.dta_emissao >= " + SQLUtils.stringSQL(DATE_FORMAT.format(notasDataInicio)) + " and\n"
                    + "    nf.dta_emissao <= " + SQLUtils.stringSQL(DATE_FORMAT.format(notasDataTermino)) + "\n"
                    + "order by\n"
                    + "    nf.dta_emissao"
            )) {
                while (rst.next()) {
                    NotaFiscalIMP imp = new NotaFiscalIMP();

                    imp.setId(
                            rst.getString("cod_parceiro"),
                            "-",
                            rst.getString("tipo_parceiro"),
                            "-",
                            rst.getString("num_nf"),
                            "-",
                            rst.getString("num_serie_nf"),
                            "-",
                            rst.getString("tipo_ident")
                    );
                    imp.setOperacao(NotaOperacao.get(rst.getInt("tipo_operacao")));
                    imp.setTipoNota(TipoNota.get(rst.getInt("tipo_nf")));
                    imp.setSerie(rst.getString("serie"));
                    imp.setNumeroNota(Utils.stringToInt(rst.getString("numeronota")));
                    imp.setDataEmissao(rst.getDate("dataemissao"));
                    imp.setDataEntradaSaida(rst.getDate("dataemissao"));
                    //imp.setDataEntradaSaida(rst.getDate("dataentradasaida"));
                    imp.setValorTotal(rst.getDouble("total_nota"));
                    imp.setIdDestinatario(rst.getString("cod_parceiro"));
                    imp.setTipoDestinatario(imp.getOperacao() == NotaOperacao.ENTRADA ? TipoDestinatario.FORNECEDOR : TipoDestinatario.CLIENTE_EVENTUAL);
                    /*int tipoNF = rst.getInt("tipo_nf");
                    if (tipoNF > 1) {
                        imp.setTipoDestinatario(TipoDestinatario.FORNECEDOR);
                        if (tipoNF == 4 && "1".equals(imp.getIdDestinatario().trim())) {
                            imp.setIdDestinatario("2347");
                        }
                    }*/
                    imp.setTipoFreteNotaFiscal(TipoFreteNotaFiscal.get(rst.getInt("tipo_frete")));
                    imp.setInformacaoComplementar(rst.getString("obs_fiscal") + " " + rst.getString("obs_livre"));
                    imp.setPesoBruto(rst.getDouble("val_peso_cte"));
                    imp.setPesoLiquido(rst.getDouble("val_peso_cte"));
                    if ("S".equals(rst.getString("flg_inutilizada"))) {
                        imp.setSituacaoNfe(SituacaoNfe.INUTILIZADA);
                    }
                    if ("S".equals(rst.getString("flg_denegada"))) {
                        imp.setSituacaoNfe(SituacaoNfe.DENEGADA);
                    }
                    if ("S".equals(rst.getString("flg_cancelado"))) {
                        imp.setSituacaoNfe(SituacaoNfe.CANCELADA);
                    }
                    imp.setChaveNfe(rst.getString("num_chave_acesso"));
                    imp.setDataHoraAlteracao(rst.getDate("dta_alteracao"));

                    getNotasItem(
                            rst.getString("cod_parceiro"),
                            //"2347".equals(imp.getIdDestinatario()) ? "1" : imp.getIdDestinatario(),
                            rst.getString("tipo_parceiro"),
                            rst.getString("num_nf"),
                            rst.getString("num_serie_nf"),
                            rst.getString("tipo_ident"),
                            imp
                    );

                    result.add(imp);
                }
            }
        }

        return result;
    }

    private void getNotasItem(String codParceiro, String tipoParceiro, String numNf, String numSerieNf, String tipoIdent, NotaFiscalIMP imp) throws Exception {
        String tab_nf_item = "intersolid.tab_nf_item";
        String sql = "select\n"
                + "    nfi.cod_parceiro,\n"
                + "    nfi.tipo_parceiro,\n"
                + "    nfi.num_nf,\n"
                + "    nfi.num_serie_nf,\n"
                + "    nfi.tipo_ident,\n"
                + "    nfi.cfop,\n"
                + "    nfi.cod_tributacao,\n"
                + "    nfi.num_item,\n"
                + "    nfi.cod_item id_produto,\n"
                + "    nfi.num_ncm ncm,\n"
                + "    nfi.num_cest cest,\n"
                + "    nfi.cfop,\n"
                + "    nfi.des_item descricao,\n"
                + "    nfi.des_unidade unidade,\n"
                + "    --incluir codigo de barras,\n"
                + "    nfi.qtd_embalagem qtdembalagem,\n"
                + "    nfi.qtd_total quantidade,\n"
                + "    nfi.val_total valor,\n"
                + "    nfi.val_desconto valor_desconto,\n"
                + "    nfi.val_frete valor_frete,\n"
                + "    nfi.val_isento valor_isento,\n"
                + "    --nfi.val_outras valor_outras,\n"
                + "    nfi.cod_sit_tributaria icms_cst,\n"
                + "    nfi.per_aliq_icms icms_aliq,\n"
                + "    nfi.per_red_bc_icms icms_red,\n"
                + "    nfi.val_icms icms_valor,\n"
                + "    nfi.val_bc_icms icms_bc,\n"
                + "    nfi.val_bc_st icms_bc_st,\n"
                + "    nfi.val_icms_st icms_st,\n"
                + "    nfi.val_base_ipi ipi_base,\n"
                + "    nfi.val_ipi ipi_valor,\n"
                + "    nfi.cst_pis pis_cst,\n"
                + "    nfi.per_iva iva_porcentagem,\n"
                + "    nfi.val_pauta_iva iva_pauta\n"
                + "from\n"
                + "    " + tab_nf_item + " nfi\n"
                + "where\n"
                + "    nfi.cod_parceiro = " + codParceiro + " and\n"
                + "    nfi.tipo_parceiro = " + tipoParceiro + " and\n"
                + "    nfi.num_nf = " + numNf + " and\n"
                + "    nfi.num_serie_nf = " + numSerieNf + " and\n"
                + "    nfi.tipo_ident = " + tipoIdent + "\n"
                + "order by\n"
                + "    nfi.cod_parceiro,\n"
                + "    nfi.tipo_parceiro,\n"
                + "    nfi.num_nf,\n"
                + "    nfi.num_serie_nf,\n"
                + "    nfi.tipo_ident,\n"
                + "    nfi.cfop,\n"
                + "    nfi.cod_tributacao,\n"
                + "    nfi.num_item,\n"
                + "    nfi.cod_item";
        try {
            try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select\n"
                        + "    nfi.cod_parceiro,\n"
                        + "    nfi.tipo_parceiro,\n"
                        + "    nfi.num_nf,\n"
                        + "    nfi.num_serie_nf,\n"
                        + "    nfi.tipo_ident,\n"
                        + "    nfi.cfop,\n"
                        + "    nfi.cod_tributacao,\n"
                        + "    nfi.num_item,\n"
                        + "    nfi.cod_item id_produto,\n"
                        + "    nfi.num_ncm ncm,\n"
                        + "    nfi.num_cest cest,\n"
                        + "    nfi.cfop,\n"
                        + "    nfi.des_item descricao,\n"
                        + "    nfi.des_unidade unidade,\n"
                        + "    --incluir codigo de barras,\n"
                        + "    nfi.qtd_embalagem qtdembalagem,\n"
                        + "    nfi.qtd_total quantidade,\n"
                        + "    nfi.val_total valor,\n"
                        + "    nfi.val_desconto valor_desconto,\n"
                        + "    nfi.val_frete valor_frete,\n"
                        + "    nfi.val_isento valor_isento,\n"
                        + "    --nfi.val_outras valor_outras,\n"
                        + "    nfi.cod_sit_tributaria icms_cst,\n"
                        + "    nfi.per_aliq_icms icms_aliq,\n"
                        + "    nfi.per_red_bc_icms icms_red,\n"
                        + "    nfi.val_icms icms_valor,\n"
                        + "    nfi.val_bc_icms icms_bc,\n"
                        + "    nfi.val_bc_st icms_bc_st,\n"
                        + "    nfi.val_icms_st icms_st,\n"
                        + "    nfi.val_base_ipi ipi_base,\n"
                        + "    nfi.val_ipi ipi_valor,\n"
                        + "    nfi.cst_pis pis_cst,\n"
                        + "    nfi.per_iva iva_porcentagem,\n"
                        + "    nfi.val_pauta_iva iva_pauta\n"
                        + "from\n"
                        + "    " + tab_nf_item + " nfi\n"
                        + "where\n"
                        + "    nfi.cod_parceiro = " + codParceiro + " and\n"
                        + "    nfi.tipo_parceiro = " + tipoParceiro + " and\n"
                        + "    nfi.num_nf = " + numNf + " and\n"
                        + "    nfi.num_serie_nf = " + numSerieNf + " and\n"
                        + "    nfi.tipo_ident = " + tipoIdent + "\n"
                        + "order by\n"
                        + "    nfi.cod_parceiro,\n"
                        + "    nfi.tipo_parceiro,\n"
                        + "    nfi.num_nf,\n"
                        + "    nfi.num_serie_nf,\n"
                        + "    nfi.tipo_ident,\n"
                        + "    nfi.cfop,\n"
                        + "    nfi.cod_tributacao,\n"
                        + "    nfi.num_item,\n"
                        + "    nfi.cod_item"
                )) {
                    while (rst.next()) {
                        NotaFiscalItemIMP item = imp.addItem();

                        item.setId(
                                rst.getString("cod_parceiro"), "-",
                                rst.getString("tipo_parceiro"), "-",
                                rst.getString("num_nf"), "-",
                                rst.getString("num_serie_nf"), "-",
                                rst.getString("tipo_ident"), "-",
                                rst.getString("cfop"), "-",
                                rst.getString("cod_tributacao"), "-",
                                rst.getString("num_item"), "-",
                                rst.getString("id_produto")
                        );
                        item.setNumeroItem(rst.getInt("num_item"));
                        item.setIdProduto(rst.getString("id_produto"));
                        item.setNcm(rst.getString("ncm"));
                        item.setCest(rst.getString("cest"));
                        item.setCfop(rst.getString("cfop"));
                        item.setDescricao(rst.getString("descricao"));
                        item.setUnidade(rst.getString("unidade"));
                        item.setQuantidadeEmbalagem(rst.getInt("qtdembalagem"));
                        item.setQuantidade(rst.getDouble("quantidade"));
                        item.setValorTotalProduto(rst.getDouble("valor"));
                        item.setValorDesconto(rst.getDouble("valor_desconto"));
                        item.setValorFrete(rst.getDouble("valor_frete"));
                        item.setValorIsento(rst.getDouble("valor_isento"));
                        //item.setValorOutras(rst.getDouble("valor_outras"));
                        item.setIcmsCst(rst.getInt("icms_cst"));
                        item.setIcmsAliquota(rst.getDouble("icms_aliq"));
                        item.setIcmsReduzido(rst.getDouble("icms_red"));
                        item.setIcmsValor(rst.getDouble("icms_valor"));
                        item.setIcmsBaseCalculoST(rst.getDouble("icms_bc_st"));
                        item.setIcmsValorST(rst.getDouble("icms_st"));
                        item.setIcmsBaseCalculo(rst.getDouble("icms_bc"));
                        item.setIpiValorBase(rst.getDouble("ipi_base"));
                        item.setIpiValor(rst.getDouble("ipi_valor"));
                        item.setPisCofinsCst(Utils.stringToInt(rst.getString("pis_cst")));
                        item.setIvaPorcentagem(rst.getDouble("iva_porcentagem"));
                        item.setIvaPauta(rst.getDouble("iva_pauta"));

                    }
                }
            }
        } catch (Exception ex) {
            System.out.println(sql);
            throw ex;
        }
    }

    @Override
    public List<NutricionalIMP> getNutricional(Set<OpcaoNutricional> opcoes) throws Exception {
        List<NutricionalIMP> result = new ArrayList<>();
        String tab_info_nutricional = "intersolid.tab_info_nutricional";

        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    n.cod_info_nutricional id,\n"
                    + "    n.des_info_nutricional descricao,\n"
                    + "    n.valor_calorico caloria,\n"
                    + "    n.carboidrato,\n"
                    + "    n.proteina,\n"
                    + "    n.gordura_total gordura,\n"
                    + "    n.gordura_saturada,\n"
                    + "    n.gordura_trans,\n"
                    + "    n.colesterol,\n"
                    + "    n.fibra_alimentar,\n"
                    + "    n.calcio,\n"
                    + "    n.ferro,\n"
                    + "    n.sodio,\n"
                    + "    n.vd_valor_calorico,\n"
                    + "    n.vd_carboidrato,\n"
                    + "    n.vd_proteina,\n"
                    + "    n.vd_gordura_total,\n"
                    + "    n.vd_gordura_saturada,\n"
                    + "    n.vd_gordura_trans,\n"
                    + "    n.vd_fibra_alimentar,\n"
                    + "    n.vd_calcio,\n"
                    + "    n.vd_ferro,\n"
                    + "    n.vd_sodio,\n"
                    + "    n.des_porcao,\n"
                    + "    n.unidade_porcao,\n"
                    + "    n.porcao\n"
                    + "from\n"
                    + "    " + tab_info_nutricional + " n\n"
                    + "order by\n"
                    + "    1"
            )) {
                while (rst.next()) {
                    NutricionalIMP imp = new NutricionalIMP();

                    imp.setId(rst.getString("id"));
                    imp.setDescricao(rst.getString("descricao"));
                    imp.setCaloria(rst.getInt("caloria"));
                    imp.setCarboidrato(rst.getDouble("carboidrato"));
                    imp.setProteina(rst.getDouble("proteina"));
                    imp.setGordura(rst.getDouble("gordura"));
                    imp.setGorduraSaturada(rst.getDouble("gordura_saturada"));
                    imp.setGorduraTrans(rst.getDouble("gordura_trans"));
                    imp.setFibra(rst.getDouble("fibra_alimentar"));
                    imp.setCalcio(rst.getDouble("calcio"));
                    imp.setFerro(rst.getDouble("ferro"));
                    imp.setSodio(rst.getDouble("sodio"));
                    imp.setPercentualCaloria(rst.getInt("vd_valor_calorico"));
                    imp.setPercentualCarboidrato(rst.getInt("vd_carboidrato"));
                    imp.setPercentualProteina(rst.getInt("vd_proteina"));
                    imp.setPercentualGordura(rst.getInt("vd_gordura_total"));
                    imp.setPercentualGorduraSaturada(rst.getInt("vd_gordura_saturada"));
                    imp.setPercentualFibra(rst.getInt("vd_fibra_alimentar"));
                    imp.setPercentualCalcio(rst.getInt("vd_calcio"));
                    imp.setPercentualFerro(rst.getInt("vd_ferro"));
                    imp.setPercentualSodio(rst.getInt("vd_sodio"));
                    imp.setPorcao(rst.getString("porcao"));

                    incluirProdutoNutricional(imp);

                    result.add(imp);
                }
            }
        }

        return result;
    }

    private void incluirProdutoNutricional(NutricionalIMP imp) throws Exception {
        String tab_produto = "intersolid.tab_produto";

        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    cod_produto\n"
                    + "from\n"
                    + "    " + tab_produto + "\n"
                    + "where\n"
                    + "    cod_info_nutricional = " + imp.getId()
            )) {
                while (rst.next()) {
                    imp.addProduto(rst.getString("cod_produto"));
                }
            }
        }
    }

    private void incluirProdutoReceitaBalanca(ReceitaBalancaIMP imp) throws Exception {
        String tab_produto = "intersolid.tab_produto";

        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    cod_produto\n"
                    + "from\n"
                    + "    " + tab_produto + "\n"
                    + "where\n"
                    + "    cod_info_receita = " + imp.getId()
            )) {
                while (rst.next()) {
                    imp.addProduto(rst.getString("cod_produto"));
                }
            }
        }
    }

    @Override
    public List<ReceitaBalancaIMP> getReceitaBalanca(Set<OpcaoReceitaBalanca> opt) throws Exception {
        List<ReceitaBalancaIMP> result = new ArrayList<>();
        String tab_info_receita = "intersolid.tab_info_receita";

        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    r.cod_info_receita id,\n"
                    + "    r.des_info_receita descricao,\n"
                    + "    r.detalhamento receita\n"
                    + "from\n"
                    + "    " + tab_info_receita + " r"
            )) {
                while (rst.next()) {
                    ReceitaBalancaIMP imp = new ReceitaBalancaIMP();

                    imp.setId(rst.getString("id"));
                    imp.setDescricao(rst.getString("descricao"));
                    imp.setReceita(rst.getString("receita"));

                    incluirProdutoReceitaBalanca(imp);

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ReceitaIMP> getReceitas() throws Exception {
        List<ReceitaIMP> result = new ArrayList<>();
        String tab_produto_producao = "intersolid.tab_produto_producao",
            tab_produto = "intersolid.tab_produto";

        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    pp.cod_produto id,\n"
                    + "    pp.cod_produto_producao id_produto,\n"
                    + "    p.des_produto descricao,\n"
                    + "    pp.qtd_rendimento rendimento,\n"
                    + "    case when pp.des_unidade in ('KG', 'LT') then 1000 else 1 end fator,\n"
                    + "    pp.qtd_receita,\n"
                    + "    pp.qtd_producao,\n"
                    + "    pp.qtd_receita + pp.qtd_producao qtd_total\n"
                    + "from\n"
                    + "    " + tab_produto_producao + " pp\n"
                    + "    join " + tab_produto + " p on\n"
                    + "        pp.cod_produto = p.cod_produto\n"
                    + "where\n"
                    + "    pp.cod_loja = " + getLojaOrigem() + "\n"
                    + "order by\n"
                    + "    1, 2"
            )) {
                while (rst.next()) {
                    ReceitaIMP imp = new ReceitaIMP();

                    imp.setImportsistema(getSistema());
                    imp.setImportloja(getLojaOrigem());
                    imp.setImportid(rst.getString("id"));
                    imp.setIdproduto(rst.getString("id"));
                    imp.setDescricao(rst.getString("descricao"));
                    imp.setRendimento(rst.getDouble("rendimento"));
                    imp.setQtdembalagemreceita(Math.round(rst.getFloat("qtd_receita") * 1000));
                    imp.setQtdembalagemproduto(1000);
                    imp.setFator(1);
                    imp.getProdutos().add(rst.getString("id_produto"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<DivisaoIMP> getDivisoes() throws Exception {
        List<DivisaoIMP> result = new ArrayList<>();
        String tab_divisao_fornecedor = "intersolid.tab_divisao_fornecedor";

        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select distinct\n"
                    + "   df.cod_divisao id,\n"
                    + "   df.des_divisao descricao\n"
                    + "from\n"
                    + "    " + tab_divisao_fornecedor + " df\n"
                    + "order by\n"
                    + "    1"
            )) {
                while (rst.next()) {
                    DivisaoIMP imp = new DivisaoIMP();

                    imp.setId(rst.getString("id"));
                    imp.setDescricao(rst.getString("id") + " - " + rst.getString("descricao"));

                    result.add(imp);
                }
            }
        }

        return result;
    }
}
