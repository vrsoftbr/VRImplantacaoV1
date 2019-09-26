package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import vrimplantacao.classe.ConexaoAccess;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.OpcaoFiscal;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoEmpresa;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.PautaFiscalIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Importacao
 */
public class OryonDAO extends InterfaceDAO implements MapaTributoProvider {

    private static final Logger LOG = Logger.getLogger(OryonDAO.class.getName());
    
    @Override
    public String getSistema() {
        return "Oryon";
    }
    
    public List<Estabelecimento> getLojaCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        
        try (Statement stm = ConexaoAccess.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select Codigo, Descricao from Tabela_Unidade_Negocio order by Codigo"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("Codigo"), rst.getString("Descricao")));
                }
            }
        }
        
        return result;
    }
    
    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.QTD_EMBALAGEM_EAN,
                OpcaoProduto.ATIVO,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.ESTOQUE_MAXIMO,
                OpcaoProduto.ESTOQUE_MINIMO,
                OpcaoProduto.PRECO,
                OpcaoProduto.CUSTO,
                OpcaoProduto.MARGEM,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.VALIDADE,
                OpcaoProduto.DATA_CADASTRO,
                OpcaoProduto.PESO_BRUTO,
                OpcaoProduto.PESO_LIQUIDO,
                OpcaoProduto.NCM,
                OpcaoProduto.CEST,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.NATUREZA_RECEITA,
                OpcaoProduto.PAUTA_FISCAL,
                OpcaoProduto.ICMS,
                OpcaoProduto.PAUTA_FISCAL_PRODUTO
        ));
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoAccess.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select aliquota from tabela_aliquotas"
            )) {
                while (rst.next()) {
                    int cst = 0;
                    double aliquota = rst.getDouble("aliquota");
                    double reducao = 0;
                    
                    result.add(new MapaTributoIMP(
                            String.format("%d-%.2f-%.2f", cst, aliquota, reducao),
                            String.format("%d-%.2f-%.2f", cst, aliquota, reducao),
                            cst,
                            aliquota,
                            reducao
                    ));
                }                
            }
            
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    p.situacao_tributaria_icm_entrada as cst_credito,\n" +
                    "    p.aliquota_icm_saida_ne as aliquota_credito,\n" +
                    "    p.base_icm_saida_ne as reducao_credito\n" +
                    "from\n" +
                    "	tabela_pro p\n" +
                    "group by\n" +
                    "	p.situacao_tributaria_icm_entrada,\n" +
                    "    p.aliquota_icm_saida_ne,\n" +
                    "    p.base_icm_saida_ne"
            )) {
                while (rst.next()) {
                    int cst = rst.getInt("cst_credito");
                    double aliquota = rst.getDouble("aliquota_credito");
                    double reducao = rst.getDouble("reducao_credito");
                    
                    result.add(new MapaTributoIMP(
                            String.format("%d-%.2f-%.2f", cst, aliquota, reducao),
                            String.format("%d-%.2f-%.2f", cst, aliquota, reducao),
                            cst,
                            aliquota,
                            reducao
                    ));
                }                
            }
            
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    p.situacao_tributaria_icm_saida_ne as cst_debito,\n" +
                    "    p.aliquota_icm_saida_ne as aliquota_debito,\n" +
                    "    p.base_icm_saida_ne as reducao_debito\n" +
                    "from\n" +
                    "	tabela_pro p\n" +
                    "group by\n" +
                    "	p.situacao_tributaria_icm_saida_ne,\n" +
                    "    p.aliquota_icm_saida_ne,\n" +
                    "    p.base_icm_saida_ne"
            )) {
                while (rst.next()) {
                    int cst = rst.getInt("cst_debito");
                    double aliquota = rst.getDouble("aliquota_debito");
                    double reducao = rst.getDouble("reducao_debito");
                    
                    result.add(new MapaTributoIMP(
                            String.format("%d-%.2f-%.2f", cst, aliquota, reducao),
                            String.format("%d-%.2f-%.2f", cst, aliquota, reducao),
                            cst,
                            aliquota,
                            reducao
                    ));
                }                
            }
            
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    p.situacao_tributaria_icm_saida_fe as cst_debito_fe,\n" +
                    "    p.aliquota_icm_saida_ne as aliquota_debito_fe,\n" +
                    "    p.base_icm_saida_fe as reducao_debito_fe\n" +
                    "from\n" +
                    "	tabela_pro p\n" +
                    "group by\n" +
                    "    p.situacao_tributaria_icm_saida_fe,\n" +
                    "    p.aliquota_icm_saida_ne,\n" +
                    "    p.base_icm_saida_fe"
            )) {
                while (rst.next()) {
                    int cst = rst.getInt("cst_debito_fe");
                    double aliquota = rst.getDouble("aliquota_debito_fe");
                    double reducao = rst.getDouble("reducao_debito_fe");
                    
                    result.add(new MapaTributoIMP(
                            String.format("%d-%.2f-%.2f", cst, aliquota, reducao),
                            String.format("%d-%.2f-%.2f", cst, aliquota, reducao),
                            cst,
                            aliquota,
                            reducao
                    ));
                }                
            }
        }
        
        return result;
    }
    
    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoAccess.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "  g.grupo as mercadologico1,\n" +
                    "  g.sub_grupo as mercadologico2,\n" +
                    "  g.Nome as mercadologico3\n" +
                    "from\n" +
                    "  tabela_categ g \n" +
                    "order by\n" +
                    "  1, 2, 3 "
            )) {
                while (rst.next()) {
                    
                    MercadologicoIMP imp = new MercadologicoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    
                    String g1 = Utils.acertarTexto(rst.getString("mercadologico1"));
                    String g2 = Utils.acertarTexto(rst.getString("mercadologico2"));
                    String g3 = Utils.acertarTexto(rst.getString("mercadologico3"));
                    
                    if ("".equals(g2)) {
                        g2 = g3;
                    }
                    if ("".equals(g1)) {
                        g1 = g2;
                    }                    
                    
                    imp.setMerc1ID(g1);
                    imp.setMerc1Descricao(g1);
                    imp.setMerc2ID(g2);
                    imp.setMerc2Descricao(g2);
                    imp.setMerc3ID(g3);
                    imp.setMerc3Descricao(g3);
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<PautaFiscalIMP> getPautasFiscais(Set<OpcaoFiscal> opcoes) throws Exception {
        List<PautaFiscalIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoAccess.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    p.codigo as id,\n" +
                    "    p.ncm,\n" +
                    "    p.margem_valor_agregado as mva,\n" +
                    "    p.aliquota_icm_saida_ne as aliquota_debito,\n" +
                    "    p.aliquota_st_ret as aliquota_credito\n" +
                    "from\n" +
                    "    tabela_pro p\n" +
                    "where\n" +
                    "    p.margem_valor_agregado > 0"
            )) {
                while (rst.next()) {
                    PautaFiscalIMP imp = new PautaFiscalIMP();
                    
                    imp.setId(rst.getString("id"));
                    imp.setIva(rst.getDouble("mva"));
                    imp.setIvaAjustado(imp.getIva());
                    imp.setNcm(rst.getString("ncm"));
                    String aliquotaDebito = formataIdTributacao(0, rst.getDouble("aliquota_debito"), 0);
                    imp.setAliquotaDebitoId(aliquotaDebito);
                    imp.setAliquotaDebitoForaEstadoId(aliquotaDebito);
                    imp.setAliquotaCreditoForaEstadoId(formataIdTributacao(0, rst.getDouble("aliquota_credito"), 0));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }    
    
    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoAccess.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    p.codigo as id,\n" +
                    "    p.codigo as codigobarras,\n" +
                    "    p.descricao as descricaocompleta,\n" +
                    "    p.descricao as descricaoreduzida,\n" +
                    "    p.descricao as descricaogondola,\n" +
                    "    g.grupo as mercadologico1,\n" +
                    "    g.sub_grupo as mercadologico2,\n" +
                    "    g.nome as mercadologico3,\n" +
                    "    p.familia,\n" +
                    "    p.unidade,\n" +
                    "    p.qt_embalagem as qtdembalagem,\n" +
                    "    p.situacao as ativo,\n" +
                    "    p.qt_estoque as estoque,\n" +
                    "    p.qt_minimo as estoqueminimo,\n" +
                    "    p.qt_maximo as estoquemaximo,\n" +
                    "    p.preco_venda as precovenda,   \n" +
                    "    p.preco_compra as custocomimposto,\n" +
                    "    p.preco_compra as custosemimposto,\n" +
                    "    p.margem_lucro as margem,\n" +
                    "    p.usa_balanca as balanca,\n" +
                    "    p.dias_validade as validade,\n" +
                    "    p.data_cadastro as datacadastro,\n" +
                    "    p.peso as pesobruto,\n" +
                    "    p.pesoliquido,\n" +
                    "    p.ncm,\n" +
                    "    p.cest,\n" +

                    "    p.situacao_tributaria_pis as pis_s,\n" +
                    "    p.situacao_tributaria_pis_entrada as pis_e,\n" +
                    "    p.situacao_tributaria_cofins as cofins_s,\n" +
                    "    p.situacao_tributaria_cofins_entrada as cofins_e,\n" +
                    "    p.codigo_natureza_receita_pis_cofins as natreceita,\n" +
                            
                    "    p.situacao_tributaria_icm_entrada as cst_credito,\n" +
                    "    p.aliquota_icm_saida_ne as aliquota_credito,\n" +
                    "    p.base_icm_saida_ne as reducao_credito,\n" +
                    "    p.situacao_tributaria_icm_saida_ne as cst_debito,\n" +
                    "    p.aliquota_icm_saida_ne as aliquota_debito,\n" +
                    "    p.base_icm_saida_ne as reducao_debito,\n" +
                    "    p.situacao_tributaria_icm_saida_fe as cst_debito_fe,\n" +
                    "    p.aliquota_icm_saida_ne as aliquota_debito_fe,\n" +
                    "    p.base_icm_saida_fe as reducao_debito_fe\n" +
                    "from\n" +
                    "    tabela_pro p\n" +
                    "    left join tabela_categ g on\n" +
                    "        p.categoria = g.codigo\n" +
                    "order by\n" +
                    "    p.codigo"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setEan(rst.getString("codigobarras"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(rst.getString("descricaogondola"));
                    
                    String g1 = Utils.acertarTexto(rst.getString("mercadologico1"));
                    String g2 = Utils.acertarTexto(rst.getString("mercadologico2"));
                    String g3 = Utils.acertarTexto(rst.getString("mercadologico3"));
                    
                    if ("".equals(g2)) {
                        g2 = g3;
                    }
                    if ("".equals(g1)) {
                        g1 = g2;
                    }                   
                    imp.setCodMercadologico1(g1);
                    imp.setCodMercadologico2(g2);
                    imp.setCodMercadologico3(g3);
                    imp.setIdFamiliaProduto(rst.getString("familia"));
                    imp.setTipoEmbalagem(rst.getString("unidade"));
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                    imp.setSituacaoCadastro(rst.getBoolean("ativo") ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
                    imp.setEstoqueMaximo(rst.getDouble("estoquemaximo"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.seteBalanca(rst.getBoolean("balanca"));
                    imp.setValidade(rst.getInt("validade"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setPesoBruto(rst.getDouble("pesobruto"));
                    imp.setPesoLiquido(rst.getDouble("pesoliquido"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    
                    imp.setPiscofinsCstCredito(rst.getString("pis_e"));
                    imp.setPiscofinsCstDebito(rst.getString("pis_s"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("natreceita"));
                    
                    int cst = rst.getInt("cst_credito");
                    double aliquota = rst.getDouble("aliquota_credito");
                    double reducao = rst.getDouble("reducao_credito");                    
                    imp.setIcmsCreditoId(formataIdTributacao(cst, aliquota, reducao));
                    imp.setIcmsCreditoForaEstadoId(formataIdTributacao(cst, aliquota, reducao));
                    
                    /*
                    imp.setIcmsCstEntrada(rst.getInt("cst_credito"));
                    imp.setIcmsAliqEntrada(rst.getDouble("aliquota_credito"));
                    imp.setIcmsReducaoEntrada(rst.getDouble("reducao_credito"));
                    imp.setIcmsCstEntradaForaEstado(imp.getIcmsCstEntrada());
                    imp.setIcmsAliqEntradaForaEstado(imp.getIcmsAliqEntrada());
                    imp.setIcmsReducaoEntradaForaEstado(imp.getIcmsReducaoEntrada());
                    */
                    
                    cst = rst.getInt("cst_debito");
                    aliquota = rst.getDouble("aliquota_debito");
                    reducao = rst.getDouble("reducao_debito");                    
                    imp.setIcmsDebitoId(formataIdTributacao(cst, aliquota, reducao));
                    
                    cst = rst.getInt("cst_debito_fe");
                    aliquota = rst.getDouble("aliquota_debito_fe");
                    reducao = rst.getDouble("reducao_debito_fe");                    
                    imp.setIcmsDebitoForaEstadoId(formataIdTributacao(cst, aliquota, reducao));
                    imp.setIcmsDebitoForaEstadoNfId(formataIdTributacao(cst, aliquota, reducao));
                    
                    /*                    
                    imp.setIcmsCstSaida(rst.getInt("cst_debito"));
                    imp.setIcmsAliqSaida(rst.getDouble("aliquota_debito"));
                    imp.setIcmsReducaoSaida(rst.getDouble("reducao_debito"));
                    imp.setIcmsCstSaidaForaEstado(rst.getInt("cst_debito_fe"));
                    imp.setIcmsAliqSaidaForaEstado(rst.getDouble("aliquota_debito_fe"));
                    imp.setIcmsReducaoSaidaForaEstado(rst.getDouble("reducao_debito_fe"));
                    */

                    
                    
                    imp.setPautaFiscalId(imp.getImportId());
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();
        try(Statement stm = ConexaoAccess.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "   codigo,\n" +
                    "   fornecedor,\n" +
                    "   codigo_forn \n" +
                    "from\n" +
                    "   tabela_pro\n" +
                    "where\n" +
                    "   fornecedor is not null and \n" +
                    "   fornecedor <> -1 and\n" +
                    "   codigo_forn is not null\n" +
                    "order by\n" +
                    "   1")) {
                while(rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rs.getString("codigo"));
                    imp.setIdFornecedor(rs.getString("fornecedor"));
                    imp.setCodigoExterno(rs.getString("codigo_forn"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        try(Statement stm = ConexaoAccess.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "   codigo as id,\n" +
                    "   nome as razao,\n" +
                    "   fantasia,\n" +
                    "   endereco_logradouro as rua,\n" +
                    "   endereco_numero as numero,\n" +
                    "   endereco_complemento as complemento,\n" +
                    "   bairro,\n" +
                    "   cidade,\n" +
                    "   codigo_cidade as ibgemunicipio,\n" +
                    "   cep,\n" +
                    "   uf,\n" +
                    "   telefone,\n" +
                    "   fax,\n" +
                    "   cnpj,\n" +
                    "   ie,\n" +
                    "   contato,\n" +
                    "   data_cadastro,\n" +
                    "   email,\n" +
                    "   inativo,\n" +
                    "   regime_tributario as tipoempresa \n" +
                    "from\n" +
                    "   tabela_for\n" +
                    "order by\n" +
                    "   1")) {
                while(rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("id"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setEndereco(rs.getString("rua"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setIbge_municipio(rs.getInt("ibgemunicipio"));
                    imp.setCep(rs.getString("cep"));
                    imp.setUf(rs.getString("uf"));
                    imp.setTel_principal(rs.getString("telefone"));
                    imp.setCnpj_cpf(rs.getString("cnpj"));
                    imp.setIe_rg(rs.getString("ie"));
                    imp.setDatacadastro(rs.getDate("data_cadastro"));
                    String email = rs.getString("email");
                    if((email) != null && (!"".equals(email))) {
                        imp.addContato("1", "EMAIL", null, null, TipoContato.NFE, email);
                    }
                    imp.setAtivo("0".equals(rs.getString("inativo")));
                    imp.setTipoEmpresa(rs.getInt("tipoempresa") == 0 ? TipoEmpresa.LUCRO_REAL : TipoEmpresa.EPP_SIMPLES);
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        try(Statement stm = ConexaoAccess.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "   codigo as id,\n" +
                    "   nome as razao,\n" +
                    "   fantasia,\n" +
                    "   endereco_logradouro as endereco,\n" +
                    "   endereco_numero as numero,\n" +
                    "   endereco_complemento as complemento,\n" +
                    "   bairro,\n" +
                    "   cidade,\n" +
                    "   codigo_cidade as ibgemunicipio,\n" +
                    "   cep,\n" +
                    "   uf,\n" +
                    "   telefone,\n" +
                    "   fax,\n" +
                    "   rg,\n" +
                    "   cic as cpf,\n" +
                    "   cnpj,\n" +
                    "   ie,\n" +
                    "   contato,\n" +
                    "   limite_credito,\n" +
                    "   data_cadastro,\n" +
                    "   pai,\n" +
                    "   mae,\n" +
                    "   email,\n" +
                    "   cancelado,\n" +
                    "   inativo,\n" +
                    "   sexomasc as sexo,\n" +
                    "   profissao \n" +
                    "from\n" +
                    "   tabela_cli\n" +
                    "order by\n" +
                    "   1")) {
                while(rs.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rs.getString("id"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setMunicipioIBGE(rs.getInt("ibgemunicipio"));
                    imp.setCep(rs.getString("cep"));
                    imp.setUf(rs.getString("uf"));
                    imp.setTelefone(rs.getString("telefone"));
                    imp.setFax(rs.getString("fax"));
                    String rg = rs.getString("rg");
                    String cpf = rs.getString("cpf");
                    if(rg == null && !"".equals(rg)) {
                        imp.setInscricaoestadual(rs.getString("ie"));
                    } else {
                        imp.setInscricaoestadual(rg);
                    }
                    if(cpf == null && !"".equals(cpf)) {
                        imp.setCnpj(rs.getString("cnpj"));
                    } else {
                        imp.setCnpj(cpf);
                    }
                    imp.setValorLimite(rs.getDouble("limite_credito"));
                    imp.setDataCadastro(rs.getDate("data_cadastro"));
                    imp.setNomeMae(rs.getString("mae"));
                    imp.setNomePai(rs.getString("pai"));
                    imp.setEmail(rs.getString("email"));
                    imp.setAtivo("0".equals(rs.getString("inativo")));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();
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
                    "   cliente,\n" +
                    "   descricao as observacao\n" +
                    "from\n" +
                    "   tabela_fluxo\n" +
                    "where\n" +
                    "   cliente is not null and\n" +
                    "   data_baixa is null and\n" +
                    "   duplicata is not null\n" +
                    "order by\n" +
                    "   vencimento")) {
                while(rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rs.getString("id"));
                    imp.setDataEmissao(rs.getDate("datalanc"));
                    imp.setDataVencimento(rs.getDate("vencimento"));
                    imp.setValor(rs.getDouble("valor"));
                    imp.setIdCliente(rs.getString("cliente"));
                    imp.setNumeroCupom(rs.getString("coo"));
                    imp.setEcf(rs.getString("ecf"));
                    imp.setObservacao(rs.getString("observacao"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
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
                    imp.setNumeroDocumento(rs.getString("coo"));
                    imp.setDataEmissao(rs.getDate("datalanc"));
                    imp.setObservacao(rs.getString("observacao"));
                    imp.addVencimento(rs.getDate("vencimento"), rs.getDouble("valor"));
                    
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

    private String formataIdTributacao(int cst, double aliquota, double reducao) {
        return String.format("%d-%.2f-%.2f", cst, aliquota, reducao);
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