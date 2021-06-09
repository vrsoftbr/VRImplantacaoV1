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
import org.openide.util.Exceptions;
import vrimplantacao.classe.ConexaoMySQL;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.OfertaIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;

/**
 *
 * @author guilhermegomes
 */
public class EptusDAO extends InterfaceDAO implements MapaTributoProvider {

    private static final Logger LOG = Logger.getLogger(EptusDAO.class.getName());
    
    @Override
    public String getSistema() {
        return "Eptus";
    }
    
    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        
        try(Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	codemp,\n" +
                    "	descricao\n" +
                    "from empresa")) {
                while(rs.next()) {
                    result.add(new Estabelecimento(rs.getString("codemp"), rs.getString("descricao")));
                }
            }
        }
        return result;
    }
    
    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                new OpcaoProduto[] {
                    OpcaoProduto.MERCADOLOGICO,
                    OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                    OpcaoProduto.MERCADOLOGICO_PRODUTO,
                    OpcaoProduto.FAMILIA,
                    OpcaoProduto.FAMILIA_PRODUTO,
                    OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                    OpcaoProduto.OFERTA,
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
                    OpcaoProduto.PAUTA_FISCAL,
                    OpcaoProduto.PAUTA_FISCAL_PRODUTO,
                    OpcaoProduto.MARGEM,
                    OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS
                }
        ));
    }
    
    @Override
    public Set<OpcaoFornecedor> getOpcoesDisponiveisFornecedor() {
        return new HashSet<>(Arrays.asList(
                OpcaoFornecedor.DADOS,
                OpcaoFornecedor.RAZAO_SOCIAL,
                OpcaoFornecedor.NOME_FANTASIA,
                OpcaoFornecedor.CNPJ_CPF,
                OpcaoFornecedor.INSCRICAO_ESTADUAL,
                OpcaoFornecedor.INSCRICAO_MUNICIPAL,
                OpcaoFornecedor.PRODUTO_FORNECEDOR,
                OpcaoFornecedor.ENDERECO,
                OpcaoFornecedor.TELEFONE,
                OpcaoFornecedor.SITUACAO_CADASTRO
        ));        
    }
    
    @Override
    public Set<OpcaoCliente> getOpcoesDisponiveisCliente() {
        return new HashSet<>(Arrays.asList(
                OpcaoCliente.DADOS,
                OpcaoCliente.RAZAO,
                OpcaoCliente.CNPJ,
                OpcaoCliente.INSCRICAO_ESTADUAL,
                OpcaoCliente.ENDERECO,
                OpcaoCliente.NUMERO,
                OpcaoCliente.COMPLEMENTO,
                OpcaoCliente.BAIRRO,
                OpcaoCliente.MUNICIPIO,
                OpcaoCliente.UF,
                OpcaoCliente.CEP,
                OpcaoCliente.TELEFONE,
                OpcaoCliente.CELULAR,
                OpcaoCliente.EMAIL,
                OpcaoCliente.CONTATOS,
                OpcaoCliente.DATA_CADASTRO,
                OpcaoCliente.SITUACAO_CADASTRO,
                OpcaoCliente.BLOQUEADO,
                OpcaoCliente.PERMITE_CREDITOROTATIVO,
                OpcaoCliente.PERMITE_CHEQUE,
                OpcaoCliente.VALOR_LIMITE,
                OpcaoCliente.OBSERVACOES
        ));
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        
        System.out.println("Loja: " + getLojaOrigem());
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	distinct\n" +
                    "	concat(t.duff_cod_trib, t.duff_aliq_icms, t.duff_red_baseicm) id,\n" +
                    "	t.duff_cod_trib cst,\n" +
                    "	t.duff_aliq_icms icms,\n" +
                    "	t.duff_red_baseicm icms_reducao\n" +
                    "from\n" +
                    "	prodserv_dados p\n" +
                    "join prodserv_valor pc on\n" +
                    "	p.codigo = pc.cod_produto\n" +
                    "	and p.codemp = pc.codemp\n" +
                    "join tributacao t on\n" +
                    "	pc.cod_tributacao = t.codigo\n" +
                    "where\n" +
                    "	p.codemp = 1")) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(
                            rst.getString("id"),
                            rst.getString("id"),
                            rst.getInt("cst"),
                            rst.getDouble("icms"),
                            rst.getDouble("icms_reducao")
                    ));
                }
            }
            
            //Tributos de compra
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	distinct\n" +
                    "	concat('C', t.duff_cod_trib, t.duff_aliq_icms, t.duff_red_baseicm) id,\n" +
                    "	t.duff_cod_trib cst,\n" +
                    "	t.duff_aliq_icms icms,\n" +
                    "	t.duff_red_baseicm icms_reducao\n" +
                    "from\n" +
                    "	prodserv_dados p\n" +
                    "join prodserv_valor pc on\n" +
                    "	p.codigo = pc.cod_produto\n" +
                    "	and p.codemp = pc.codemp\n" +
                    "join tributacao t on\n" +
                    "	pc.cod_tribcompra = t.codigo\n" +
                    "where\n" +
                    "	p.codemp = 1")) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(
                            rst.getString("id"),
                            rst.getString("id"),
                            rst.getInt("cst"),
                            rst.getDouble("icms"),
                            rst.getDouble("icms_reducao")
                    ));
                }
            }
            
        }
        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
         
        try(Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	distinct\n" +
                    "	g.codigo merc1,\n" +
                    "	g.descricao descmerc1,\n" +
                    "	if(sg.codigo is null, 1, sg.codigo) merc2,\n" +
                    "	if(sg.descricao is null, g.descricao, sg.descricao) descmerc2,\n" +
                    "	d.codigo merc3,\n" +
                    "	d.descricao descmerc3,\n" +
                    "	s.codigo merc4,\n" +
                    "	s.descricao descmerc4\n" +
                    "from \n" +
                    "	prodserv_dados p\n" +
                    "left join grupo g on p.cod_grupo = g.codigo\n" +
                    "left join subgrupo sg on p.cod_subgrupo = sg.codigo\n" +
                    "left join dep_estoque d on p.cod_depto = d.codigo\n" +
                    "left join rp_garantia s on p.cod_tipgarantia = s.codigo\n" +
                    "where \n" +
                    "	g.descricao not like '%???????%' and g.descricao not like '%XXX%'")) {
                while(rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rs.getString("merc1"));
                    imp.setMerc1Descricao(rs.getString("descmerc1"));
                    imp.setMerc2ID(rs.getString("merc2"));
                    imp.setMerc2Descricao(rs.getString("descmerc2"));
                    
                    String merc3 = rs.getString("merc3"), 
                            descmerc3 = rs.getString("descmerc3"), 
                            merc4 = rs.getString("merc4"), 
                            descmerc4 = rs.getString("descmerc4");
                    
                    if (merc3 != null && !merc3.isEmpty()) {
                        imp.setMerc3ID(merc3);
                        imp.setMerc3Descricao(descmerc3);
                    } else {
                        imp.setMerc3ID("1");
                        imp.setMerc3Descricao(imp.getMerc2Descricao());
                    }
                    
                    if (merc4 != null && !merc4.isEmpty()) {
                        imp.setMerc4ID(merc4);
                        imp.setMerc4Descricao(descmerc4);
                    } else {
                        imp.setMerc4ID("1");
                        imp.setMerc4Descricao(imp.getMerc3Descricao());
                    }
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }
            
    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "	p.record_no,\n" +
                    "	p.codigo,\n" +
                    "	p.descricao,\n" +
                    "	p.dt_cadastro,\n" +
                    "	p.Sit_Desativado situacaocadastro,\n" +
                    "	p.desc_reduzida,\n" +
                    "	pa.cod_barras ean,\n" +
                    "	p.envia_balanca balanca,\n" +
                    "	p.dias_validade validade,\n" +
                    "	pc.qtd_atual estoque,\n" +
                    "	pc.qtd_minima,\n" +
                    "	pc.qtd_maxima,\n" +
                    "	pc.custo_compra custosemimposto,\n" +
                    "	pc.custo_medio,\n" +
                    "	pc.custo_anterior,\n" +
                    "	pc.custo_varejo custocomimposto,\n" +
                    "	pc.mlucro_varejo margem,\n" +
                    "	pc.gondola_venda precovenda,\n" +
                    "	pa.embalagem_venda qtdembalagem,\n" +
                    "	p.embalagem_compra qtdembalagemcompra,\n" +
                    "	pc.gondola_atacado precoatacado,\n" +
                    "	p.embalagem_venda qtdembalagemvenda,\n" +
                    "	p.embalagem_atacado qtdembalagematacado,\n" +
                    "	p.peso_bruto,\n" +
                    "	p.peso_liquido,\n" +
                    "	p.unidade,\n" +
                    "	p.cod_grupo merc1,\n" +
                    "	p.cod_subgrupo merc2,\n" +
                    "	if(p.cod_depto = 0, 1, p.cod_depto) merc3,\n" +
                    "	if(p.cod_tipgarantia = 0, 1, p.cod_tipgarantia) merc4,\n" +
                    "	p.cod_ncmercosul ncm,\n" +
                    "   p.Cod_CEST cest,\n" +        
                    "	pc.cod_tributacao id_icms_debito,\n" +
                    "	pc.cod_tribvendadev id_icmsdev_debito,\n" +
                    "	pc.cod_tribcompradev id_icmsdev_credito,\n" +
                    "	concat('C', pc.cod_tribcompra) id_icms_credito,\n" +
                    "	t.duff_cst_cofins cofins_debito,\n" +
                    "	substr(t.dUFf_CodCont_Pis, 5, 3) natureza_receita,\n" +
                    "	t.duff_cod_trib cst,\n" +
                    "	t.duff_aliq_icms icms,\n" +
                    "	t.duff_red_baseicm icms_reducao,\n" +
                    "	tc.duff_cod_trib cst_credito,\n" +
                    "	tc.duff_aliq_icms icms_credito,\n" +
                    "	tc.duff_red_baseicm icms_reducao_credito,\n" +
                    "   concat(t.duff_cod_trib, t.duff_aliq_icms, t.duff_red_baseicm) id_icms,\n" +
                    "	concat('C', tc.duff_cod_trib, tc.duff_aliq_icms, tc.duff_red_baseicm) id_icms_credito\n" +
                    "from\n" +
                    "	prodserv_dados p\n" +
                    "join prodserv_codbar pa on\n" +
                    "	p.codigo = pa.cod_produto\n" +
                    "	and p.codemp = pa.codemp\n" +
                    "join prodserv_valor pc on\n" +
                    "	p.codigo = pc.cod_produto\n" +
                    "	and p.codemp = pc.codemp\n" +
                    "join tributacao t on\n" +
                    "	pc.cod_tributacao = t.codigo\n" +
                    "join tributacao tc on\n" +
                    "	pc.cod_tribcompra = tc.codigo\n" +
                    "where\n" +
                    "	p.codemp = 1")) {
                while(rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("codigo"));
                    imp.setDescricaoCompleta(rs.getString("descricao"));
                    imp.setDescricaoReduzida(rs.getString("desc_reduzida"));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setDataCadastro(rs.getDate("dt_cadastro"));
                    imp.setSituacaoCadastro(rs.getInt("situacaocadastro") == 0 ? 1 : 0);
                    imp.setTipoEmbalagem(rs.getString("unidade"));
                    imp.setEan(rs.getString("ean"));
                    imp.seteBalanca(rs.getInt("balanca") == 1);
                    imp.setValidade(rs.getInt("validade"));
                    imp.setCodMercadologico1(rs.getString("merc1"));
                    imp.setCodMercadologico2(rs.getString("merc2"));
                    
                    imp.setCodMercadologico3(rs.getString("merc3"));
                    
                    if(imp.getCodMercadologico3() == null || imp.getCodMercadologico3().isEmpty()) {
                        imp.setCodMercadologico3("1");
                    }
                    
                    imp.setCodMercadologico4(rs.getString("merc4"));
                    
                    if(imp.getCodMercadologico4() == null || imp.getCodMercadologico4().isEmpty()) {
                        imp.setCodMercadologico4("1");
                    }
                    
                    imp.setEstoque(Utils.arredondar(rs.getDouble("estoque"), 2));
                    imp.setEstoqueMinimo(Utils.arredondar(rs.getDouble("qtd_minima"), 2));
                    imp.setEstoqueMaximo(Utils.arredondar(rs.getDouble("qtd_maxima"), 2));
                    
                    imp.setPesoBruto(rs.getDouble("peso_bruto"));
                    imp.setPesoLiquido(rs.getDouble("peso_liquido"));
                    
                    imp.setCustoSemImposto(Utils.arredondar(rs.getDouble("custosemimposto"), 2));
                    imp.setCustoComImposto(Utils.arredondar(rs.getDouble("custocomimposto"), 2));
                    imp.setCustoMedioSemImposto(imp.getCustoMedioComImposto());
                    imp.setCustoAnteriorComImposto(Utils.arredondar(rs.getDouble("custo_anterior"), 2));
                    imp.setCustoAnteriorSemImposto(imp.getCustoAnteriorComImposto());
                    
                    imp.setMargem(Utils.arredondar(rs.getDouble("margem"), 2));
                    imp.setPrecovenda(Utils.arredondar(rs.getDouble("precovenda"), 2));
                    
                    imp.setNcm(rs.getString("ncm"));
                    imp.setCest(rs.getString("cest"));
                    imp.setPiscofinsNaturezaReceita(rs.getString("natureza_receita"));
                    imp.setPiscofinsCstDebito(rs.getString("cofins_debito"));
                    
                    //tributacao saida
                    imp.setIcmsConsumidorId(rs.getString("id_icms"));
                    imp.setIcmsDebitoId(rs.getString("id_icms"));
                    imp.setIcmsDebitoForaEstadoId(imp.getIcmsDebitoId());
                    imp.setIcmsDebitoForaEstadoNfId(imp.getIcmsDebitoId());
                    
                    //tributacao entrada
                    imp.setIcmsCreditoId(rs.getString("id_icms_credito"));
                    imp.setIcmsCreditoForaEstadoId(imp.getIcmsCreditoId());
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	f.codigo,\n" +
                    "	f.razao,\n" +
                    "	f.fantasia,\n" +
                    "	f.cnpj,\n" +
                    "	f.ie,\n" +
                    "	f.insc_municipal im,\n" +
                    "	f.insc_prodrural ipr,\n" +
                    "	f.endereco,\n" +
                    "	f.cidade,\n" +
                    "	f.uf,\n" +
                    "	f.cep,\n" +
                    "	f.end_numero numero,\n" +
                    "	f.Complemento,\n" +
                    "	f.bairro,\n" +
                    "	f.fone1,\n" +
                    "	f.fone2,\n" +
                    "	f.fax,\n" +
                    "	f.celular,\n" +
                    "	f.contato1,\n" +
                    "	f.contato2,\n" +
                    "	f.Internet,\n" +
                    "	f.email,\n" +
                    "	f.Dias_Entrega,\n" +
                    "	f.Dt_Cadastro\n" +
                    "from \n" +
                    "	fornecedor f\n" +
                    "where \n" +
                    "	codemp = 1")) {
                while (rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("codigo"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setCnpj_cpf(rs.getString("cnpj"));
                    imp.setIe_rg(rs.getString("ie"));
                    imp.setInsc_municipal(rs.getString("im"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setTel_principal(rs.getString("fone1"));
                    
                    String fone2 = rs.getString("fone2");
                    
                    if(fone2 != null && !fone2.isEmpty()) {
                        imp.addContato("1", "TELEFONE2", fone2, null, TipoContato.NFE, null);
                    }
                    
                    String contato1 = rs.getString("contato1");
                    
                    if(contato1 != null && !contato1.isEmpty()) {
                        imp.addContato("2", contato1, null, null, TipoContato.NFE, null);
                    }
                    
                    String contato2 = rs.getString("contato2");
                    
                    if(contato2 != null && !contato2.isEmpty()) {
                        imp.addContato("3", contato2, null, null, TipoContato.NFE, null);
                    }
                    
                    String site = rs.getString("internet");
                    
                    if(site != null && !site.isEmpty()) {
                        imp.addContato("4", site, null, null, TipoContato.NFE, null);
                    }
                    
                    String email = rs.getString("email");
                    
                    if(email != null && !email.isEmpty()) {
                        imp.addContato("5", "EMAIL", null, null, TipoContato.NFE, email);
                    }
                    
                    imp.setDatacadastro(rs.getDate("dt_cadastro"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<OfertaIMP> getOfertas(Date dataTermino) throws Exception {
        List<OfertaIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "   cod_produto,\n" +        
                    "	promocao_dt_ini dtinicio,\n" +
                    "	promocao_dt_val dttermino,\n" +
                    "	promocao_valor,\n" +
                    "	gondola_venda\n" +
                    "from \n" +
                    "	prodserv_valor v\n" +
                    "where \n" +
                    "	promocao_dt_val is not null and \n" +
                    "	promocao_dt_ini is not null and \n" +
                    "	promocao_dt_val > cast(now() as date)\n" +
                    "order by\n" +
                    "	promocao_dt_ini")) {
                while(rs.next()) {
                    OfertaIMP imp = new OfertaIMP();
                    
                    imp.setIdProduto(rs.getString("cod_produto"));
                    imp.setDataInicio(rs.getDate("dtinicio"));
                    imp.setDataFim(rs.getDate("dttermino"));
                    imp.setPrecoOferta(rs.getDouble("promocao_valor"));
                    imp.setPrecoNormal(rs.getDouble("gondola_venda"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "	c.codigo,\n" +
                    "	c.razao,\n" +
                    "	c.fantasia,\n" +
                    "	c.nome_completo,\n" +
                    "	c.cpf_cnpj,\n" +
                    "	c.rg_ie,\n" +
                    "	c.bairro,\n" +
                    "	c.cidade,\n" +
                    "	c.uf,\n" +
                    "	c.complemento,\n" +
                    "	c.endereco,\n" +
                    "	c.end_numero,\n" +
                    "	c.cep,\n" +
                    "	c.celular,\n" +
                    "	c.dt_cadastro,\n" +
                    "	c.nascimento,\n" +
                    "	c.fone,\n" +
                    "	c.fone2,\n" +
                    "	c.email_1,\n" +
                    "	c.email_2,\n" +
                    "	c.limite_credito\n" +
                    "from\n" +
                    "	clientes c\n" +
                    "where \n" +
                    "	c.codemp = 1")) {
                while (rs.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    
                    imp.setId(rs.getString("codigo"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setCnpj(rs.getString("cpf_cnpj"));
                    imp.setInscricaoestadual(rs.getString("rg_ie"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("uf"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("end_numero"));
                    imp.setCep(rs.getString("cep"));
                    imp.setCelular(rs.getString("celular"));
                    imp.setDataCadastro(rs.getDate("dt_cadastro"));
                    imp.setDataNascimento(rs.getDate("nascimento"));
                    imp.setTelefone(rs.getString("fone"));
                    imp.setValorLimite(rs.getDouble("limite_credito"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	record_no id,\n" +
                    "	cod_cliente,\n" +
                    "	no_documento,\n" +
                    "	no_parcela,\n" +
                    "	tot_parcelas,\n" +
                    "	dt_lancamento,\n" +
                    "	dt_emissao,\n" +
                    "	dt_vencto,\n" +
                    "	vlr_tot_venda,\n" +
                    "	valor_bruto,\n" +
                    "	valor_pagto,\n" +
                    "	historico\n" +
                    "from \n" +
                    "	ctas_receber\n" +
                    "where \n" +
                    "	valor_pagto < valor_bruto and\n" +
                    "	codemp = 1")) {
                while(rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    
                    imp.setId(rs.getString("id"));
                    imp.setIdCliente(rs.getString("cod_cliente"));
                    imp.setNumeroCupom(rs.getString("no_documento"));
                    imp.setParcela(rs.getInt("no_parcela"));
                    imp.setDataEmissao(rs.getDate("dt_emissao"));
                    imp.setDataVencimento(rs.getDate("dt_vencto"));
                    imp.setObservacao(rs.getString("historico"));
                    imp.setValor(rs.getDouble("valor_bruto") - rs.getDouble("valor_pagto"));
                    
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
                        next.setNumeroCupom(Utils.stringToInt(rst.getString("documento")));
                        next.setEcf(Utils.stringToInt(rst.getString("ecf")));
                        next.setData(rst.getDate("data"));
                        next.setIdClientePreferencial(rst.getString("cod_cliente"));
                        
                        String horaInicio = timestampDate.format(rst.getDate("data")) + 
                                " " + rst.getString("hora");
                        String horaTermino = timestampDate.format(rst.getDate("data")) + 
                                " " + rst.getString("hora");
                        
                        next.setHoraInicio(timestamp.parse(horaInicio));
                        next.setHoraTermino(timestamp.parse(horaTermino));
                        next.setSubTotalImpressora(0d);
                    }
                }
            } catch (SQLException | ParseException ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {
            this.sql
                    = 
                    "select\n" +
                    "	v.cod_idregistro id,\n" +
                    "	v.dt_movto data,\n" +
                    "	v.hr_movto hora,\n" +
                    "	v.no_docto documento,\n" +
                    "	v.no_doctoserie ecf,\n" +
                    "	v.no_cooecf coo,\n" +
                    "	s.descricao caixa,\n" +
                    "	v.cod_cliente,\n" +
                    "	v.razao_cliente\n" +
                    "from\n" +
                    "	venda_cab v\n" +
                    "join 	serie_doc s on v.no_doctoserie = s.codigo\n" +
                    "where \n" +
                    "	v.codemp = 1 and \n" +
                    "	v.dt_movto between '" + FORMAT.format(dataInicio) + "' and '" + FORMAT.format(dataTermino) + "'";
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

        private Statement stm = ConexaoMySQL.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaItemIMP next;

        private void obterNext() {
            try {
                if (next == null) {
                    if (rst.next()) {
                        next = new VendaItemIMP();

                        next.setId(rst.getString("id"));
                        next.setVenda(rst.getString("id_venda"));
                        next.setProduto(rst.getString("id_produto"));
                        next.setSequencia(rst.getInt("seq"));
                        next.setDescricaoReduzida(rst.getString("descricao"));
                        next.setQuantidade(rst.getDouble("quantidade"));
                        next.setTotalBruto(rst.getDouble("total"));
                        next.setCodigoBarras(rst.getString("ean"));
                        next.setUnidadeMedida(rst.getString("unidade"));
                        next.setIcmsAliquotaId(rst.getString("cod_tributacao"));
                        
                    }
                }
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaItemIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {
            this.sql
                    = 
                    "select \n" +
                    "	vi.record_no id,\n" +
                    "	vi.cod_idregistro id_venda,\n" +
                    "	vi.cod_produto id_produto,\n" +
                    "	pd.Descricao,\n" +
                    "	pd.Unidade,\n" +
                    "	vi.no_item seq,\n" +
                    "	vi.cod_barras ean,\n" +
                    "	vi.quantidade,\n" +
                    "	vi.vlr_venda,\n" +
                    "	(vi.quantidade * vi.vlr_venda) total,\n" +
                    "	vi.cod_tributacao\n" +
                    "from \n" +
                    "	venda_pro vi \n" +
                    "join prodserv_dados pd on vi.cod_produto = pd.codigo and \n" +
                    "	pd.codemp = vi.codemp\n" +
                    "where \n" +
                    "	vi.codemp = 1 and \n" +
                    "	vi.dt_movto between '" + VendaIterator.FORMAT.format(dataInicio) + "' and '" + 
                                                                  VendaIterator.FORMAT.format(dataTermino) + "'";
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
