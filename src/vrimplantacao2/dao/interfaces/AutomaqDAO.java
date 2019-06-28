package vrimplantacao2.dao.interfaces;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Leandro
 */
public class AutomaqDAO extends InterfaceDAO implements MapaTributoProvider {

    private String complemento = "";
    private Connection conexaoProduto;
    private Connection conexaoFornecedor;
    private Connection conexaoCliente;

    public void setConexaoProduto(Connection conexaoProduto) {
        this.conexaoProduto = conexaoProduto;
    }

    public void setConexaoFornecedor(Connection conexaoFornecedor) {
        this.conexaoFornecedor = conexaoFornecedor;
    }

    public void setConexaoCliente(Connection conexaoCliente) {
        this.conexaoCliente = conexaoCliente;
    }
    
    @Override
    public String getSistema() {
        if ("".equals(complemento)) {
            return "Automaq";
        } else {
            return "Automaq(" + complemento + ")";
        }
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento == null ? "" : complemento.trim();
    }

    public List<Estabelecimento> getLojas() {
        return Arrays.asList(new Estabelecimento("1", "AUTOMAQ LJ01"));
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.ATUALIZAR_SOMAR_ESTOQUE,
                OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.FAMILIA,
                OpcaoProduto.FAMILIA_PRODUTO,
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.DATA_CADASTRO,
                OpcaoProduto.DATA_ALTERACAO,
                OpcaoProduto.QTD_EMBALAGEM_COTACAO,
                OpcaoProduto.QTD_EMBALAGEM_EAN,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.VALIDADE,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.PESO_BRUTO,
                OpcaoProduto.PESO_LIQUIDO,
                OpcaoProduto.ESTOQUE_MAXIMO,
                OpcaoProduto.ESTOQUE_MINIMO,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.MARGEM,
                OpcaoProduto.CUSTO_COM_IMPOSTO,
                OpcaoProduto.CUSTO_SEM_IMPOSTO,
                OpcaoProduto.PRECO,
                OpcaoProduto.ATIVO,
                OpcaoProduto.NCM,
                OpcaoProduto.CEST,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.NATUREZA_RECEITA,
                OpcaoProduto.ICMS,
                OpcaoProduto.EMITE_ETIQUETA,
                OpcaoProduto.FABRICANTE,
                OpcaoProduto.ACEITA_MULTIPLICACAO_PDV
        ));
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        
        try (Statement stm = conexaoProduto.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    d.codinterno cod_merc1,\n" +
                    "    d.nomedepartamento merc1,\n" +
                    "    g.codinterno cod_marc2,\n" +
                    "    g.nomegrupo merc2,\n" +
                    "    sg.codinterno cod_merc3,\n" +
                    "    sg.nomesubgrupo merc3\n" +
                    "from\n" +
                    "    tbldepartamento d\n" +
                    "    left join tblgrupo g on\n" +
                    "        d.codinterno = g.coddepartamento\n" +
                    "    left join tblsubgrupo sg on\n" +
                    "        g.coddepartamento = sg.coddepartamento and\n" +
                    "        g.codinterno = sg.codgrupo\n" +
                    "order by\n" +
                    "    d.codinterno,\n" +
                    "    g.codinterno,\n" +
                    "    sg.codinterno"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setMerc1ID(rst.getString("cod_merc1"));
                    imp.setMerc1Descricao(rst.getString("merc1"));
                    imp.setMerc2ID(rst.getString("cod_marc2"));
                    imp.setMerc2Descricao(rst.getString("merc2"));
                    imp.setMerc3ID(rst.getString("cod_merc3"));
                    imp.setMerc3Descricao(rst.getString("merc3"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();
        
        try (Statement stm = conexaoProduto.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    f.codinterno id,\n" +
                    "    f.nomeagrupamento descricao\n" +
                    "from\n" +
                    "    tblagrupamento f\n" +
                    "order by\n" +
                    "    codinterno"
            )) {
                while (rst.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setDescricao(rst.getString("descricao"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        
        try (Statement stm = conexaoProduto.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    p.codinterno id,\n" +
                    "    p.datacadastro,\n" +
                    "    p.dataalteracao,\n" +
                    "    p.codbarras ean,\n" +
                    "    1 qtdembalagem,\n" +
                    "    coalesce(nullif(p.unidadecx,0),1) qtdembalagemcotacao,\n" +
                    "    case p.pesado\n" +
                    "    when 'S' then 1\n" +
                    "    when 'U' then 1\n" +
                    "    else 0\n" +
                    "    end pesavel,\n" +
                    "    case p.pesado\n" +
                    "    when 'S' then 'KG'\n" +
                    "    when 'U' then 'UN'\n" +
                    "    else p.unidademedida\n" +
                    "    end tipoembalagem,\n" +
                    "    p.validade,\n" +
                    "    p.descricao descricaocompleta,\n" +
                    "    coalesce(p.abreviacao, p.descricao) descricaoreduzida,\n" +
                    "    p.descricao descricaogondola,\n" +
                    "    p.coddepartamento merc1,\n" +
                    "    p.codgrupo merc2,\n" +
                    "    p.codsubgrupo merc3,\n" +
                    "    p.codagrupamento id_familia,\n" +
                    "    p.pesobruto,\n" +
                    "    p.pesoliquido,\n" +
                    "    p.saldominimo estoqueminimo,\n" +
                    "    p.saldomaximo estoquemaximo,\n" +
                    "    p.saldoatual estoque,\n" +
                    "    coalesce(p.saldotroca, 0) troca,\n" +
                    "    p.margem1pv1 markup,\n" +
                    "    p.margem2pv1 markdown,\n" +
                    "    p.precocusto custocomimposto,\n" +
                    "    p.precocusto custosemimposto,\n" +
                    "    p.precovenda1 prevenda,\n" +
                    "    case p.produtoativo when 'N' then 0 else 1 end situacaocadastro,\n" +
                    "    p.codncm ncm,\n" +
                    "    cest.cest,\n" +
                    "    p.cstpis_entrada piscofins_entrada,\n" +
                    "    p.cstpis piscofins_saida,\n" +
                    "    p.natrecpis piscofins_naturezareceita,\n" +
                    "    p.codtributacao icms_debito_id,\n" +
                    "    p.codtributacaoentrada icms_credito_id,\n" +
                    "    case p.imprimeetiqueta when 'N' then 0 else 1 end imprimeetiqueta,\n" +
                    "    p.codfornecedor fabricante,\n" +
                    "    case p.permitemultiplicacao when 'N' then 0 else 1 end permitemultiplicacao\n" +
                    "from\n" +
                    "    tblprodutos p\n" +
                    "    left join tblcest cest on\n" +
                    "        p.codcest = cest.codinterno\n" +
                    "order by\n" +
                    "    p.codinterno"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setDataAlteracao(rst.getDate("dataalteracao"));
                    imp.setEan(rst.getString("ean"));
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                    imp.setQtdEmbalagemCotacao(rst.getInt("qtdembalagemcotacao"));
                    imp.seteBalanca(rst.getBoolean("pesavel"));
                    imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
                    imp.setValidade(rst.getInt("validade"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(rst.getString("descricaogondola"));
                    imp.setCodMercadologico1(rst.getString("merc1"));
                    imp.setCodMercadologico2(rst.getString("merc2"));
                    imp.setCodMercadologico3(rst.getString("merc3"));
                    imp.setIdFamiliaProduto(rst.getString("id_familia"));
                    imp.setPesoBruto(rst.getDouble("pesobruto"));
                    imp.setPesoLiquido(rst.getDouble("pesoliquido"));
                    imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
                    imp.setEstoqueMaximo(rst.getDouble("estoquemaximo"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setTroca(rst.getDouble("troca"));
                    imp.setMargem(rst.getDouble("markup"));
                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    imp.setPrecovenda(rst.getDouble("prevenda"));
                    imp.setSituacaoCadastro(rst.getInt("situacaocadastro"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstCredito(rst.getString("piscofins_entrada"));
                    imp.setPiscofinsCstDebito(rst.getString("piscofins_saida"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("piscofins_naturezareceita"));
                    imp.setIcmsDebitoId(rst.getString("icms_debito_id"));
                    imp.setIcmsCreditoId(rst.getString("icms_credito_id"));
                    imp.setEmiteEtiqueta(rst.getBoolean("imprimeetiqueta"));
                    imp.setFornecedorFabricante(rst.getString("fabricante"));
                    imp.setAceitaMultiplicacaoPDV(rst.getBoolean("permitemultiplicacao"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        
        try (Statement stm = conexaoProduto.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    t.codinterno id,\n" +
                    "    t.nometributacao descricao,\n" +
                    "    t.cst,\n" +
                    "    t.valoraliquota aliquota\n" +
                    "from\n" +
                    "    tbltributacao t\n" +
                    "order by\n" +
                    "    t.codinterno"
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(
                            rst.getString("id"),
                            rst.getString("descricao"),
                            Utils.stringToInt(rst.getString("cst")),
                            rst.getDouble("aliquota"),
                            0
                    ));
                }
            }
        }
        
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        
        try (Statement stm = conexaoFornecedor.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    f.codinterno id,\n" +
                    "    f.razaosocial,\n" +
                    "    f.nomefantasia,\n" +
                    "    f.cnpj,\n" +
                    "    f.ie,\n" +
                    "    case f.ativo when 'N' then 0 else 1 end ativo,\n" +
                    "    f.suframa,\n" +
                    "    f.endereco,\n" +
                    "    f.numero,\n" +
                    "    f.complemento,\n" +
                    "    f.bairro,\n" +
                    "    f.cidade,\n" +
                    "    f.codmun cidadeibge,\n" +
                    "    f.estado,\n" +
                    "    f.cep,\n" +
                    "    f.contato,\n" +
                    "    f.celular,\n" +
                    "    f.fone1,\n" +
                    "    f.fone2,\n" +
                    "    f.email,\n" +
                    "    f.dataalteracao,\n" +
                    "    f.observacao1,\n" +
                    "    f.prazoentrega,\n" +
                    "    f.prazopagamento\n" +
                    "from\n" +
                    "    tblfornecedor f\n" +
                    "order by\n" +
                    "    f.codinterno"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razaosocial"));
                    imp.setFantasia(rst.getString("nomefantasia"));
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    imp.setIe_rg(rst.getString("ie"));
                    imp.setAtivo(rst.getBoolean("ativo"));
                    imp.setSuframa(rst.getString("suframa"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("estado"));
                    imp.setCep(rst.getString("cep"));
                    imp.addContato("1", rst.getString("contato"), rst.getString("fone2"), rst.getString("celular"), TipoContato.COMERCIAL, rst.getString("email"));
                    imp.setTel_principal(rst.getString("fone1"));
                    imp.setIbge_municipio(rst.getInt("cidadeibge"));
                    imp.setObservacao(rst.getString("observacao1"));
                    imp.setPrazoEntrega(rst.getInt("prazoentrega"));
                    if (rst.getInt("prazopagamento") > 0) {
                        imp.addCondicaoPagamento(rst.getInt("prazopagamento"));
                    }
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }
    
    
}
