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
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ChequeIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.OfertaIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;

/**
 *
 * @author Importacao
 */
public class TecnosoftDAO extends InterfaceDAO implements MapaTributoProvider{

    private static final Logger LOG = Logger.getLogger(TecnosoftDAO.class.getName());
    
    @Override
    public String getSistema() {
        return "TECNOSOFT";
    }
    
    public List<Estabelecimento> getLojas() throws SQLException {
        List<Estabelecimento> result = new ArrayList<>();
        
        /*try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    ""
            )) {
                while (rst.next()) {
                    System.out.println("Entrou...");
                    result.add(new Estabelecimento(rst.getString("id"), rst.getString("razao")));
                }
            }
        }*/
        
        result.add(new Estabelecimento("1", "LOJA 01"));

        return result;
    }
    
    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                new OpcaoProduto[]{
                    OpcaoProduto.FAMILIA,
                    OpcaoProduto.FAMILIA_PRODUTO,
                    OpcaoProduto.MERCADOLOGICO_PRODUTO,
                    OpcaoProduto.MERCADOLOGICO,
                    OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                    OpcaoProduto.PRODUTOS,
                    OpcaoProduto.EAN,
                    OpcaoProduto.EAN_EM_BRANCO,
                    OpcaoProduto.DATA_CADASTRO,
                    OpcaoProduto.TIPO_EMBALAGEM_EAN,
                    OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                    OpcaoProduto.PESAVEL,
                    OpcaoProduto.VALIDADE,
                    OpcaoProduto.DESC_COMPLETA,
                    OpcaoProduto.DESC_GONDOLA,
                    OpcaoProduto.DESC_REDUZIDA,
                    OpcaoProduto.ESTOQUE_MAXIMO,
                    OpcaoProduto.ESTOQUE_MINIMO,
                    OpcaoProduto.PRECO,
                    OpcaoProduto.CUSTO,
                    OpcaoProduto.ESTOQUE,
                    OpcaoProduto.ATIVO,
                    OpcaoProduto.NCM,
                    OpcaoProduto.CEST,
                    OpcaoProduto.PIS_COFINS,
                    OpcaoProduto.NATUREZA_RECEITA,
                    OpcaoProduto.ICMS,
                    OpcaoProduto.ICMS_SAIDA,
                    OpcaoProduto.ICMS_SAIDA_FORA_ESTADO,
                    OpcaoProduto.ICMS_SAIDA_NF,
                    OpcaoProduto.ICMS_ENTRADA,
                    OpcaoProduto.ICMS_CONSUMIDOR,
                    OpcaoProduto.ICMS_ENTRADA_FORA_ESTADO,
                    OpcaoProduto.PAUTA_FISCAL,
                    OpcaoProduto.PAUTA_FISCAL_PRODUTO,
                    OpcaoProduto.MARGEM,
                    OpcaoProduto.OFERTA,
                    OpcaoProduto.MAPA_TRIBUTACAO
                }
        ));
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "    distinct\n" +
                    "    pt.icms_aliquota||pt.icms_st||pt.icms_basecalc id,\n" +
                    "    'ICMS: ' || round(pt.icms_aliquota, 2) || ' | CST: ' || pt.icms_st ||\n" +
                    "    ' | RED: ' || round(pt.icms_basecalc, 2) descricao,\n" +
                    "    pt.icms_st cst,\n" +
                    "    pt.icms_aliquota icms,\n" +
                    "    pt.icms_basecalc reducao\n" +
                    "from\n" +
                    "    prod_tributos pt\n" +
                    "where\n" +
                    "    pt.estado = 'SP' and\n" +
                    "    pt.icms_st is not null and\n" +
                    "    pt.icms_aliquota is not null and\n" +
                    "    pt.icms_basecalc is not null\n" +
                    "order by\n" +
                    "    pt.icms_st")) {
                while(rs.next()) {
                    result.add(new MapaTributoIMP(
                            rs.getString("id"), 
                            rs.getString("descricao"),
                            rs.getInt("cst"),
                            rs.getDouble("icms"),
                            rs.getDouble("reducao")));
                }
            }
        }
        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "    distinct\n" +
                    "    p.id_dep merc1,\n" +
                    "    d.descricao descmerc1,\n" +
                    "    p.id_set merc2,\n" +
                    "    s.descricao descmerc2,\n" +
                    "    p.id_sub merc3,\n" +
                    "    ss.descricao descmerc3\n" +
                    "from\n" +
                    "    produtos p\n" +
                    "join departamentos d on p.id_dep = d.id_dep\n" +
                    "join setores s on p.id_set = s.id_set\n" +
                    "join subsetor ss on p.id_sub = ss.id_sub\n" +
                    "order by\n" +
                    "    1, 3, 5")) {
                while(rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rs.getString("merc1"));
                    imp.setMerc1Descricao(rs.getString("descmerc1"));
                    imp.setMerc2ID(rs.getString("merc2"));
                    imp.setMerc2Descricao(rs.getString("descmerc2"));
                    imp.setMerc3ID(rs.getString("merc3"));
                    imp.setMerc3Descricao(rs.getString("descmerc3"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "    p.id_prod id,\n" +
                    "    p.codigo,\n" +
                    "    p.codigobar ean,\n" +
                    "    coalesce(p.balanca, 0) balanca,\n" +
                    "    coalesce(p.validade, 0) validade,\n" +
                    "    p.data_cadastro,\n" +
                    "    p.desativar,\n" +
                    "    un.descricao embalagem,\n" +
                    "    unc.descricao embalagemcompra,\n" +
                    "    p.descricao,\n" +
                    "    p.descricao_ecf,\n" +
                    "    p.descricao_nf,\n" +
                    "    p.estoque,\n" +
                    "    p.minimo estoqueminimo,\n" +
                    "    p.maximo estoquemaximo,\n" +
                    "    p.peso_bruto,\n" +
                    "    p.peso_liquido,\n" +
                    "    p.lucro margem,\n" +
                    "    p.margem_bruta,\n" +
                    "    p.precovenda,\n" +
                    "    p.custoinicial,\n" +
                    "    p.custofinal,\n" +
                    "    p.pis_st,\n" +
                    "    p.cofins_st,\n" +
                    "    p.id_dep merc1,\n" +
                    "    p.id_set merc2,\n" +
                    "    p.id_sub merc3,\n" +
                    "    p.id_ecf idicms_old,\n" +
                    "    coalesce(pt.icms_aliquota, 0)||\n" +
                    "    coalesce(pt.icms_st, 0)||\n" +
                    "    coalesce(pt.icms_basecalc, 0) idicms," + 
                    "    coalesce(pt.icms_aliquota, 0) icms_debito,\n" +
                    "    coalesce(pt.icms_st, 0) cst_debito,\n" +
                    "    coalesce(pt.icms_basecalc, 0) reducao_debito,\n" +       
                    "    p.ncm_sh ncm,\n" +
                    "    p.cest,\n" +
                    "    p.nat_receita_pis naturezareceita\n" +
                    "from\n" +
                    "    produtos p\n" +
                    "join prod_tributos pt on p.id_prod = pt.id_prod\n" +        
                    "left join unidades un on p.id_unid = un.id_unid\n" +
                    "left join unidades unc on p.id_unid_forn = unc.id_unid\n" +
                    "where pt.estado = 'SP'\n" +       
                    "order by\n" +
                    "    1")) {
                while(rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id"));
                    imp.setDescricaoCompleta(rs.getString("descricao"));
                    imp.setDescricaoReduzida(rs.getString("descricao_ecf"));
                    imp.setDescricaoGondola(rs.getString("descricao"));
                    imp.setEan(rs.getString("ean"));
                    imp.seteBalanca(rs.getInt("balanca") == 1);
                    imp.setValidade(rs.getInt("validade"));
                    imp.setDataCadastro(rs.getDate("data_cadastro"));
                    imp.setTipoEmbalagem(rs.getString("embalagem"));
                    imp.setSituacaoCadastro(rs.getInt("desativar") == 1 ? SituacaoCadastro.EXCLUIDO : SituacaoCadastro.ATIVO);
                    imp.setEstoqueMinimo(rs.getDouble("estoqueminimo"));
                    imp.setEstoqueMaximo(rs.getDouble("estoquemaximo"));
                    imp.setEstoque(rs.getDouble("estoque"));
                    imp.setPesoBruto(rs.getDouble("peso_bruto"));
                    imp.setPesoLiquido(rs.getDouble("peso_liquido"));
                    imp.setMargem(rs.getDouble("margem"));
                    imp.setCustoSemImposto(rs.getDouble("custoinicial"));
                    imp.setCustoComImposto(rs.getDouble("custofinal"));
                    imp.setPrecovenda(rs.getDouble("precovenda"));
                    imp.setPiscofinsCstDebito(rs.getString("pis_st"));
                    imp.setCodMercadologico1(rs.getString("merc1"));
                    imp.setCodMercadologico2(rs.getString("merc2"));
                    imp.setCodMercadologico3(rs.getString("merc3"));
                    imp.setIcmsDebitoId(rs.getString("idicms"));
                    imp.setIcmsConsumidorId(imp.getIcmsDebitoId());
                    imp.setIcmsCreditoId(imp.getIcmsDebitoId());
                    
                    /*imp.setIcmsAliqSaida(rs.getDouble("icms_debito"));
                    imp.setIcmsCstSaida(rs.getInt("cst_debito"));
                    imp.setIcmsReducaoSaida(rs.getDouble("reducao_debito"));
                    if(imp.getIcmsCstSaida() == 0) {
                        imp.setIcmsReducaoSaida(0);
                    }
                    
                    imp.setIcmsAliqEntrada(rs.getDouble("icms_debito"));
                    imp.setIcmsCstEntrada(rs.getInt("cst_debito"));
                    imp.setIcmsReducaoEntrada(rs.getDouble("reducao_debito"));
                    if(imp.getIcmsCstEntrada() == 0) {
                        imp.setIcmsReducaoEntrada(0);
                    }*/
                    
                    imp.setNcm(rs.getString("ncm"));
                    imp.setCest(rs.getString("cest"));
                    imp.setPiscofinsNaturezaReceita(rs.getString("naturezareceita"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "    pf.id_prod,\n" +
                    "    pf.id_forn\n" +
                    "from\n" +
                    "    prod_fornec pf")) {
                while(rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rs.getString("id_prod"));
                    imp.setIdFornecedor(rs.getString("id_forn"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<OfertaIMP> getOfertas(Date dataTermino) throws Exception {
        List<OfertaIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "    p.id_promocao,\n" +
                    "    p.data_inicio,\n" +
                    "    p.data_fim,\n" +
                    "    pi.id_prod,\n" +
                    "    pi.valor precopromocao,\n" +
                    "    pr.precovenda preconormal\n" +
                    "from\n" +
                    "    promocao p\n" +
                    "join promo_itens pi on p.id_promocao = pi.id_promocao\n" +
                    "join produtos pr on pi.id_prod = pr.id_prod\n" +
                    "where\n" +
                    "    p.data_fim > current_date and\n" +
                    "    p.id_loja = " + getLojaOrigem() + "\n" +        
                    "order by\n" +
                    "    p.data_inicio")) {
                while(rs.next()) {
                    OfertaIMP imp = new OfertaIMP();
                    
                    imp.setIdProduto(rs.getString("id_prod"));
                    imp.setDataInicio(rs.getDate("data_inicio"));
                    imp.setDataFim(rs.getDate("data_fim"));
                    imp.setPrecoNormal(rs.getDouble("preconormal"));
                    imp.setPrecoOferta(rs.getDouble("precopromocao"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "    f.id_forn,\n" +
                    "    f.codigo,\n" +
                    "    f.docum1 cnpj,\n" +
                    "    f.docum2 ie,\n" +
                    "    f.razaosocial,\n" +
                    "    f.nomefantasia fantasia,\n" +
                    "    f.endereco,\n" +
                    "    f.end_numero numero,\n" +
                    "    f.bairro,\n" +
                    "    f.cep,\n" +
                    "    f.complemento,\n" +
                    "    f.cidade,\n" +
                    "    f.estado,\n" +
                    "    f.telefone1,\n" +
                    "    f.telefone2,\n" +
                    "    f.telefone3,\n" +
                    "    f.telefone4,\n" +
                    "    f.email_compras,\n" +
                    "    f.email_financeiro,\n" +
                    "    f.data_cadastro,\n" +
                    "    f.desativar,\n" +
                    "    f.contato,\n" +
                    "    f.observacoes\n" +
                    "from\n" +
                    "    fornecedores f")) {
                while(rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id_forn"));
                    imp.setCnpj_cpf(rs.getString("cnpj"));
                    imp.setIe_rg(rs.getString("ie"));
                    imp.setRazao(rs.getString("razaosocial"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setCep(rs.getString("cep"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("estado"));
                    imp.setTel_principal(rs.getString("telefone1"));
                    
                    if(rs.getString("telefone2") != null && !"".equals(rs.getString("telefone2"))) {
                        imp.addContato("1", "TEL2", rs.getString("telefone2"), null, TipoContato.NFE, null);
                    }
                    
                    if(rs.getString("telefone3") != null && !"".equals(rs.getString("telefone3"))) {
                        imp.addContato("2", "TEL3", rs.getString("telefone3"), null, TipoContato.NFE, null);
                    }
                    
                    if(rs.getString("telefone4") != null && !"".equals(rs.getString("telefone4"))) {
                        imp.addContato("3", "TEL4", rs.getString("telefone4"), null, TipoContato.NFE, null);
                    }
                    
                    if(rs.getString("email_compras") != null && !"".equals(rs.getString("email_compras"))) {
                        imp.addContato("4", "EMAIL", null, null, TipoContato.NFE, rs.getString("email_compras"));
                    }
                    
                    if(rs.getString("email_financeiro") != null && !"".equals(rs.getString("email_financeiro"))) {
                        imp.addContato("5", "EMAIL2", null, null, TipoContato.NFE, rs.getString("email_financeiro"));
                    }
                    
                    imp.setDatacadastro(rs.getDate("data_cadastro"));
                    imp.setAtivo(rs.getInt("desativar") == 1 ? false : true);
                    imp.setObservacao(rs.getString("observacoes") == null ? "" : rs.getString("observacoes").trim());
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "    c.id_cli,\n" +
                    "    c.codigo,\n" +
                    "    c.docum1 cnpj,\n" +
                    "    c.docum2 ie,\n" +
                    "    c.docum3,\n" +
                    "    c.razaosocial,\n" +
                    "    c.nomefantasia fantasia,\n" +
                    "    c.endereco,\n" +
                    "    c.end_numero numero,\n" +
                    "    c.bairro bairro,\n" +
                    "    c.cep,\n" +
                    "    c.cidade,\n" +
                    "    c.estado,\n" +
                    "    c.complemento,\n" +
                    "    c.telefone1,\n" +
                    "    c.telefone2,\n" +
                    "    c.telefone3,\n" +
                    "    c.telefone4,\n" +
                    "    c.email_nfe,\n" +
                    "    c.email_contato,\n" +
                    "    c.email_cobranca,\n" +
                    "    c.data_cadastro,\n" +
                    "    c.data_nascimento,\n" +
                    "    c.desativar,\n" +
                    "    c.limite_credito,\n" +
                    "    coalesce(c.crediario, 0) crediario,\n" +        
                    "    c.contato,\n" +
                    "    c.observacoes,\n" +
                    "    c.pai,\n" +
                    "    c.mae\n" +
                    "from\n" +
                    "    clientes c")) {
                while(rs.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    
                    imp.setId(rs.getString("id_cli"));
                    imp.setCnpj(rs.getString("cnpj"));
                    imp.setInscricaoestadual(rs.getString("ie"));
                    imp.setRazao(rs.getString("razaosocial"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setCep(rs.getString("cep"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("estado"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setTelefone(rs.getString("telefone1"));
                    if(rs.getString("telefone2") != null && !"".equals(rs.getString("telefone2"))) {
                        imp.addContato("1", "TEL2", rs.getString("telefone2"), null, null);
                    }
                    
                    if(rs.getString("telefone3") != null && !"".equals(rs.getString("telefone3"))) {
                        imp.addContato("2", "TEL3", rs.getString("telefone3"), null, null);
                    }
                    
                    if(rs.getString("telefone4") != null && !"".equals(rs.getString("telefone4"))) {
                        imp.addContato("3", "TEL4", rs.getString("telefone4"), null, null);
                    }
                    
                    if(rs.getString("email_contato") != null && !"".equals(rs.getString("email_contato"))) {
                        imp.addContato("4", "EMAIL", null, null, rs.getString("email_contato"));
                    }
                    
                    if(rs.getString("email_cobranca") != null && !"".equals(rs.getString("email_cobranca"))) {
                        imp.addContato("5", "EMAIL2", null, null, rs.getString("email_cobranca"));
                    }
                    
                    imp.setDataCadastro(rs.getDate("data_cadastro"));
                    imp.setDataNascimento(rs.getDate("data_nascimento"));
                    
                    imp.setAtivo(rs.getInt("crediario") == 1 ? true : false);
                    imp.setValorLimite(rs.getDouble("limite_credito"));
                    imp.setNomeMae(rs.getString("mae"));
                    imp.setNomePai(rs.getString("pai"));
                    imp.setObservacao(rs.getString("observacoes") == null ? "" : rs.getString("observacoes"));
                    imp.setPermiteCreditoRotativo(rs.getInt("crediario") == 1);
                    
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

        public final static SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");

        private Statement stm = ConexaoFirebird.getConexao().createStatement();
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
                        next.setNumeroCupom(Utils.stringToInt(rst.getString("cupom")));
                        next.setEcf(Utils.stringToInt(rst.getString("caixa")));
                        next.setData(rst.getDate("data"));
                        next.setIdClientePreferencial(rst.getString("id_cli"));
                        String horaInicio = timestampDate.format(rst.getDate("data")) + " " + rst.getString("hora");
                        String horaTermino = timestampDate.format(rst.getDate("data")) + " " + rst.getString("hora");
                        next.setHoraInicio(timestamp.parse(horaInicio));
                        next.setHoraTermino(timestamp.parse(horaTermino));
                        next.setCancelado(rst.getBoolean("cancelada"));
                        next.setSubTotalImpressora(rst.getDouble("valtotal"));
                        next.setNumeroSerie(rst.getString("numero_sat"));
                        next.setNomeCliente(rst.getString("razao"));
                        String endereco
                                = Utils.acertarTexto(rst.getString("endereco")) + ","
                                + Utils.acertarTexto(rst.getString("numero")) + ","
                                + Utils.acertarTexto(rst.getString("complemento")) + ","
                                + Utils.acertarTexto(rst.getString("bairro")) + ","
                                + Utils.acertarTexto(rst.getString("cidade")) + "-"
                                + Utils.acertarTexto(rst.getString("uf"));
                        next.setEnderecoCliente(endereco);
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
                    "    v.id_ven id,\n" +
                    "    coalesce(sat.numero, v.orcamento) cupom,\n" +
                    "    v.orcamento,\n" +
                    "    v.terminal caixa,\n" +
                    "    v.data,\n" +
                    "    v.hora,\n" +
                    "    v.valtotal,\n" +
                    "    sat.chave_cfe,\n" +
                    "    sate.numero_sat,\n" +
                    "    sate.mac_adress,\n" +
                    "    v.id_cli,\n" +
                    "    sat.dest_nome razao,\n" +
                    "    sat.dest_endereco endereco,\n" +
                    "    sat.dest_end_numero numero,\n" +
                    "    sat.dest_bairro bairro,\n" +
                    "    sat.dest_complemento complemento,\n" +
                    "    sat.dest_cidade cidade,\n" +
                    "    sat.dest_estado uf,\n" +
                    "    v.cancelada,\n" +
                    "    v.observacao\n" +
                    "from\n" +
                    "    vendas v\n" +
                    "left join sat_cupom sat on v.id_cup = sat.id_cup\n" +
                    "left join sat_equipamento sate on sat.id_sat = sate.id_sat\n" +
                    "where\n" +
                    "    v.data between '" + FORMAT.format(dataInicio) + "' and '" + FORMAT.format(dataTermino) + "' and\n" +
                    "    v.tipo = 'VENDA' and\n" +
                    "    v.valtotal > 0 and\n" +
                    "    v.id_loja = " + idLojaCliente;
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

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "    p.id_parc id,\n" +
                    "    p.id_cli idcliente,\n" +
                    "    p.valrestante valor,\n" +
                    "    p.parcela,\n" +        
                    "    v.data emissao,\n" +
                    "    p.vencimento,\n" +
                    "    v.hora,\n" +
                    "    v.orcamento,\n" +
                    "    v.id_caixa,\n" +
                    "    c.terminal ecf,\n" +
                    "    c.operador\n" +
                    "from\n" +
                    "    parcelas p\n" +
                    "left join vendas v on p.id_ven = v.id_ven\n" +
                    "join caixa c on v.id_caixa = c.id_caixa\n" +
                    "where\n" +
                    "    p.valrestante > 0 and\n" +
                    "    p.id_loja = " + getLojaOrigem() + "\n" +
                    "order by\n" +
                    "    p.vencimento")) {
                while(rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    
                    imp.setId(rs.getString("id"));
                    imp.setIdCliente(rs.getString("idcliente"));
                    imp.setValor(rs.getDouble("valor"));
                    imp.setDataEmissao(rs.getDate("emissao"));
                    imp.setEcf(rs.getString("ecf"));
                    imp.setDataVencimento(rs.getDate("vencimento"));
                    imp.setNumeroCupom(rs.getString("orcamento"));
                    imp.setObservacao(rs.getString("operador"));
                    
                    String par[] = new String[2];
                    
                    if(rs.getString("parcela") != null) {
                        par = rs.getString("parcela").split("/");
                    
                        imp.setParcela(Utils.stringToInt(par[0]));
                    }
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ChequeIMP> getCheques() throws Exception {
        List<ChequeIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "    c.id_chq id,\n" +
                    "    em.nome razao,\n" +
                    "    em.documento cnpj,\n" +
                    "    em.endereco,\n" +
                    "    em.bairro,\n" +
                    "    em.cep,\n" +
                    "    em.cidade,\n" +
                    "    c.vencimento,\n" +
                    "    c.dia_util,\n" +
                    "    c.valor,\n" +
                    "    c.banco,\n" +
                    "    c.agencia,\n" +
                    "    c.conta,\n" +
                    "    c.num_chq cheque\n" +
                    "from\n" +
                    "    cheques c\n" +
                    "left join emitentes em on c.id_emit = em.id_emit\n" +
                    "where\n" +
                    "    c.status = 'EMPRES' and\n" +
                    "    c.id_loja = " + getLojaOrigem() + "\n" +
                    "order by\n" +
                    "    vencimento")) {
                while(rs.next()) {
                    ChequeIMP imp = new ChequeIMP();
                    
                    imp.setId(rs.getString("id"));
                    imp.setCpf(rs.getString("cnpj"));
                    imp.setNome(rs.getString("razao"));
                    imp.setDataDeposito(rs.getDate("vencimento"));
                    imp.setDate(rs.getDate("dia_util"));
                    imp.setValor(rs.getDouble("valor"));
                    imp.setBanco(rs.getInt("banco"));
                    imp.setAgencia(rs.getString("agencia"));
                    imp.setNumeroCheque(rs.getString("cheque"));
                    imp.setConta(rs.getString("conta"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ContaPagarIMP> getContasPagar() throws Exception {
        List<ContaPagarIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "SELECT * FROM (SELECT\n" +
                                    "    P.ID_PAR,\n" +
                                    "    P.ID_COM,\n" +
                                    "    P.VENCIMENTO,\n" +
                                    "    c.data emissao,\n" +
                                    "    P.VALTOTAL,\n" +
                                    "    P.VALPAGO,\n" +
                                    "    P.VALRESTANTE,\n" +
                                    "    P.JUR_DESC,\n" +
                                    "    P.PAGAMENTO,\n" +
                                    "    P.ID_FORN,\n" +
                                    "    P.ID_PAG,\n" +
                                    "    P.ID_LOJA,\n" +
                                    "    P.PARCELA,\n" +
                                    "    F.RAZAOSOCIAL,\n" +
                                    "    C.NOTAFISCAL,\n" +
                                    "    CAST(0 AS INTEGER) AS CE,\n" +
                                    "    P.CODIGO_BARRAS,\n" +
                                    "    C.OBSERVACAO\n" +
                                    "FROM\n" +
                                    "    COM_PARCELAS P\n" +
                                    "LEFT JOIN FORNECEDORES F ON\n" +
                                    "    F.ID_FORN = P.ID_FORN\n" +
                                    "LEFT JOIN COMPRAS C ON\n" +
                                    "    C.ID_COM = P.ID_COM\n" +
                                    "UNION\n" +
                                    "SELECT\n" +
                                    "    CAR.ID_CART_FAT AS ID_PAR,\n" +
                                    "    CAST(-1 AS INTEGER) AS ID_COM,\n" +
                                    "    CAR.VENCIMENTO,\n" +
                                    "    CAST(NULL AS DATE) emissao,\n" +
                                    "    CAR.VALTOTAL,\n" +
                                    "    CAR.VALPAGO,\n" +
                                    "    CAR.VALRESTANTE,\n" +
                                    "    CAST(0 AS FLOAT)AS JUR_DESC,\n" +
                                    "    CAST(NULL AS DATE) AS PAGAMENTOS,\n" +
                                    "    0 AS ID_FORN,\n" +
                                    "    CAR.ID_PAG,\n" +
                                    "    CAR.ID_LOJA,\n" +
                                    "    CAST('' AS VARCHAR(7)) AS PARCELA,\n" +
                                    "    CAST(CART.DESCRICAO AS VARCHAR(60)) AS FORNECEDORES,\n" +
                                    "    CAST('' AS VARCHAR(10))AS NOTAFISCAL,\n" +
                                    "    CAST(1 AS INTEGER) AS CE,\n" +
                                    "    CAST('' AS VARCHAR(50))AS CODIGO_BARRAS,\n" +
                                    "    CAST('' AS VARCHAR(80)) AS OBSERVACAO\n" +
                                    "FROM\n" +
                                    "    CARTAO_EMP_FATURA CAR\n" +
                                    "LEFT JOIN CARTAO_EMPRES CART ON\n" +
                                    "    CAR.ID_CART = CART.ID_CART\n" +
                                    "UNION\n" +
                                    "SELECT\n" +
                                    "    CP.ID_PAR,\n" +
                                    "    CP.ID_COM,\n" +
                                    "    CP.VENCIMENTO,\n" +
                                    "    PAG.data emissao,\n" +
                                    "    CP.VALTOTAL,\n" +
                                    "    CP.VALPAGO,\n" +
                                    "    CP.VALRESTANTE,\n" +
                                    "    CP.JUR_DESC,\n" +
                                    "    CP.PAGAMENTO,\n" +
                                    "    CP.ID_FORN,\n" +
                                    "    CP.ID_PAG,\n" +
                                    "    CP.ID_LOJA,\n" +
                                    "    CP.PARCELA,\n" +
                                    "    FORN.RAZAOSOCIAL,\n" +
                                    "    COM.NOTAFISCAL,\n" +
                                    "    CAST(3 AS INTEGER) AS CE,\n" +
                                    "    CP.CODIGO_BARRAS,\n" +
                                    "    COM.OBSERVACAO\n" +
                                    "FROM\n" +
                                    "    PAGAMENTOS PAG\n" +
                                    "LEFT JOIN PAG_TIPO PG ON\n" +
                                    "    PG.ID_PAG = PAG.ID_PAG\n" +
                                    "LEFT JOIN PAG_PARCELAS PP ON\n" +
                                    "    PP.ID_PAG = PG.ID_PAG\n" +
                                    "LEFT JOIN COM_PARCELAS CP ON\n" +
                                    "    CP.ID_PAR = PP.ID_PAR\n" +
                                    "LEFT JOIN COMPRAS COM ON\n" +
                                    "    COM.ID_COM = CP.ID_COM\n" +
                                    "LEFT JOIN FORNECEDORES FORN ON\n" +
                                    "    FORN.ID_FORN = COM.ID_FORN\n" +
                                    "WHERE\n" +
                                    "    PG.TIPO = 'Débito em Conta'\n" +
                                    "    AND CP.VENCIMENTO >= CURRENT_DATE) PAG\n" +
                                    "WHERE\n" +
                                    "    pag.VALRESTANTE > 0 and\n" +
                                    "    pag.id_loja = " + getLojaOrigem())) {
                while(rs.next()) {
                    ContaPagarIMP imp = new ContaPagarIMP();
                    
                    imp.setId(rs.getString("ID_PAR"));
                    imp.setIdFornecedor(rs.getString("ID_FORN"));
                    imp.addVencimento(rs.getDate("vencimento"), rs.getDouble("VALRESTANTE"));
                    imp.setDataEmissao(rs.getDate("emissao"));
                    imp.setDataEntrada(rs.getDate("emissao"));
                    imp.setNumeroDocumento(rs.getString("notafiscal"));
                    imp.setObservacao("PARCELA: " + rs.getString("PARCELA") + " - OBS.: " + rs.getString("OBSERVACAO") == null ? "" : rs.getString("OBSERVACAO"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }
    
    private static class VendaItemIterator implements Iterator<VendaItemIMP> {

        private Statement stm = ConexaoFirebird.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaItemIMP next;

        private void obterNext() {
            try {
                if (next == null) {
                    if (rst.next()) {
                        next = new VendaItemIMP();

                        next.setId(rst.getString("id"));
                        next.setVenda(rst.getString("idvenda"));
                        next.setProduto(rst.getString("idproduto"));
                        next.setDescricaoReduzida(rst.getString("descricao"));
                        next.setQuantidade(rst.getDouble("quantidade"));
                        next.setTotalBruto(rst.getDouble("total"));
                        next.setCodigoBarras(rst.getString("ean"));
                        next.setUnidadeMedida(rst.getString("unid"));
                        
                        switch(rst.getString("aliqecf").trim()) {
                            case "F":
                                next.setIcmsAliq(0);
                                next.setIcmsCst(60);
                                next.setIcmsReduzido(0);
                                break;
                            case "N":
                                next.setIcmsAliq(0);
                                next.setIcmsCst(41);
                                next.setIcmsReduzido(0);
                                break;
                            case "7":
                                next.setIcmsAliq(7);
                                next.setIcmsCst(0);
                                next.setIcmsReduzido(0);
                                break;
                            case "12":
                                next.setIcmsAliq(12);
                                next.setIcmsCst(0);
                                next.setIcmsReduzido(0);    
                                break;
                            case "18":
                                next.setIcmsAliq(18);
                                next.setIcmsCst(0);
                                next.setIcmsReduzido(0); 
                                break;
                            case "25":
                                next.setIcmsAliq(25);
                                next.setIcmsCst(0);
                                next.setIcmsReduzido(0);
                                break;
                            default:
                                next.setIcmsAliq(0);
                                next.setIcmsCst(40);
                                next.setIcmsReduzido(0);
                                break;
                        }
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
                    "    i.id_item id,\n" +
                    "    i.num_item seq,\n" +
                    "    i.id_ven idvenda,\n" +
                    "    i.id_prod idproduto,\n" +
                    "    p.codigobar ean,\n" +
                    "    i.descricao,\n" +
                    "    i.unid,\n" +
                    "    i.quantidade,\n" +
                    "    i.unitario,\n" +
                    "    i.total,\n" +
                    "    p.id_ecf idaliquota,\n" +
                    "    coalesce(trib.descricao, 'I') aliqecf,\n" +
                    "    coalesce(trib.aliquota, 0) aliquota\n" +
                    "from\n" +
                    "    itens i\n" +
                    "join vendas v on i.id_ven = v.id_ven\n" +
                    "join produtos p on i.id_prod = p.id_prod\n" +
                    "left join tribut_ecf trib on p.id_ecf = trib.id_ecf\n" +
                    "where\n" +
                    "    v.data between '" + VendaIterator.FORMAT.format(dataInicio) + "' and '" + VendaIterator.FORMAT.format(dataTermino) + "' and\n" +
                    "    i.id_loja = " + idLojaCliente + " and\n" +
                    "    i.total > 0 and\n" +
                    "    v.tipo = 'VENDA'";
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
