/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2_5.dao.sistema;

/**
 *
 * @author Implantação
 * 
 * SISTEMA REFATORADO DA 2.0 PARA 2.5 E AINDA NÃO TESTADO FAVOR VALIDAR OS METODOS
 */
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

public class Autocom2_5DAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "Autocom";
    }

    private String getAliquotaKey(String cst, double aliq, double red) throws Exception {
        return String.format(
                "%s-%.2f-%.2f",
                cst,
                aliq,
                red
        );
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    codigo,\n"
                    + "    nome,\n"
                    + "    cgc as cnpj\n"
                    + "from empresa\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(
                            rst.getString("codigo"), rst.getString("nome") + " - " + rst.getString("cnpj")));
                }
            }
        }
        return result;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select distinct\n"
                    + "    p.cst as cst,\n"
                    + "    coalesce(p.aliqinterna, 0) as aliquota,\n"
                    + "    coalesce(p.redbcicms, 0) as reducao\n"
                    + "from produto p"
            )) {
                while (rst.next()) {
                    String id = getAliquotaKey(
                            rst.getString("cst"),
                            rst.getDouble("aliquota"),
                            rst.getDouble("reducao")
                    );

                    result.add(new MapaTributoIMP(
                            id,
                            id,
                            rst.getInt("cst"),
                            rst.getDouble("aliquota"),
                            rst.getDouble("reducao")
                    ));
                }
            }
            
            try (ResultSet rst = stm.executeQuery(
                    "select distinct\n"
                    + "    coalesce(p.cstent, '000') as cstentrada,\n"
                    + "    coalesce(p.aliqinterna, 0) as aliquota,\n"
                    + "    coalesce(p.redbcicms, 0) as reducao\n"
                    + "from produto p"
            )) {
                while (rst.next()) {
                    String id = getAliquotaKey(
                            rst.getString("cstentrada"),
                            rst.getDouble("aliquota"),
                            rst.getDouble("reducao")
                    );

                    result.add(new MapaTributoIMP(
                            id,
                            id,
                            rst.getInt("cstentrada"),
                            rst.getDouble("aliquota"),
                            rst.getDouble("reducao")
                    ));
                }
            }
        }
        return result;
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                new OpcaoProduto[]{
                    OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                    OpcaoProduto.MERCADOLOGICO_PRODUTO,
                    OpcaoProduto.MERCADOLOGICO,
                    OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                    OpcaoProduto.FAMILIA_PRODUTO,
                    OpcaoProduto.FAMILIA,
                    OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                    OpcaoProduto.MANTER_DESCRICAO_PRODUTO,
                    OpcaoProduto.PRODUTOS,
                    OpcaoProduto.EAN,
                    OpcaoProduto.EAN_EM_BRANCO,
                    OpcaoProduto.DATA_CADASTRO,
                    OpcaoProduto.TIPO_EMBALAGEM_EAN,
                    OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                    OpcaoProduto.QTD_EMBALAGEM_COTACAO,
                    OpcaoProduto.QTD_EMBALAGEM_EAN,
                    OpcaoProduto.PESAVEL,
                    OpcaoProduto.VALIDADE,
                    OpcaoProduto.DESC_COMPLETA,
                    OpcaoProduto.DESC_GONDOLA,
                    OpcaoProduto.DESC_REDUZIDA,
                    OpcaoProduto.ESTOQUE_MAXIMO,
                    OpcaoProduto.ESTOQUE_MINIMO,
                    OpcaoProduto.PRECO,
                    OpcaoProduto.CUSTO,
                    OpcaoProduto.CUSTO_COM_IMPOSTO,
                    OpcaoProduto.CUSTO_SEM_IMPOSTO,
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
                    OpcaoProduto.OFERTA,
                    OpcaoProduto.MAPA_TRIBUTACAO,
                    OpcaoProduto.EXCECAO,
                    OpcaoProduto.TIPO_PRODUTO,
                    OpcaoProduto.ATACADO
                }
        ));
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    codigo as merc1,\n"
                    + "    nome as desc_merc1\n"
                    + "from secao\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rst.getString("merc1"));
                    imp.setMerc1Descricao(rst.getString("desc_merc1"));
                    imp.setMerc2ID("1");
                    imp.setMerc2Descricao(imp.getMerc1Descricao());
                    imp.setMerc3ID("1");
                    imp.setMerc3Descricao(imp.getMerc1Descricao());
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    p.numero as id,\n"
                    + "    p.codigo as ean,\n"
                    + "    case p.balanca when 'S' then 1 else 0 end balanca,\n"
                    + "    p.diasval as validade,\n"
                    + "    p.descricao as descricaocompleta,\n"
                    + "    case p.status when 'ATIVO' then 1 else 0 end situacaocadastro,\n"
                    + "    p.unidade as tipoembalagem,\n"
                    + "    p.embalagem as qtdembalagem,\n"
                    + "    p.embalagemc as qtdembalagemcotacao,\n"
                    + "    p.dtcadastro as datacadastro,\n"
                    + "    p.pesob as pesobruto,\n"
                    + "    p.pesol as pesoliquido,\n"
                    + "    p.codsec as merc1,\n"
                    + "    p.codgru as merc2,\n"
                    + "    p.precus as custo,\n"
                    + "    p.preven as precovenda,\n"
                    + "    p.mlucro as margem,\n"
                    + "    p.estoque,\n"
                    + "    p.maximo as estoquemaximo,\n"
                    + "    p.minimo as estoqueminimo,\n"
                    + "    p.ncm,\n"
                    + "    p.cest,\n"
                    + "    p.cstpis,\n"
                    + "    p.cstpisc,\n"
                    + "    p.cstcofins,\n"
                    + "    p.cstcofinsc,\n"
                    + "    p.codnat as naturezareceita,\n"
                    + "    p.tributado as sittrib,\n"
                    + "    coalesce(p.cstent, '000') as cstentrada,\n"                            
                    + "    coalesce(p.cst, '000') as cst,\n"
                    + "    coalesce(p.aliqinterna, 0) as aliquota,\n"
                    + "    coalesce(p.redbcicms, 0) as reducao,\n"
                    + "    p.cstent as cst_credito,\n"
                    + "    p.fcp\n"
                    + "from produto p\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setEan(rst.getString("ean"));
                    imp.seteBalanca(rst.getInt("balanca") == 1);
                    imp.setValidade(rst.getInt("validade"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
                    imp.setQtdEmbalagemCotacao(rst.getInt("qtdembalagemcotacao"));
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setPesoBruto(rst.getDouble("pesobruto"));
                    imp.setPesoLiquido(rst.getDouble("pesoliquido"));
                    imp.setSituacaoCadastro(rst.getInt("situacaocadastro"));
                    imp.setCodMercadologico1(rst.getString("merc1"));
                    imp.setCodMercadologico2("1");
                    imp.setCodMercadologico3("1");
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setCustoComImposto(rst.getDouble("custo"));
                    imp.setCustoSemImposto(rst.getDouble("custo"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
                    imp.setEstoqueMaximo(rst.getDouble("estoquemaximo"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getString("cstpis"));
                    imp.setPiscofinsCstCredito(rst.getString("cstcofinsc"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("naturezareceita"));

                    String icmsDebitoId = getAliquotaKey(rst.getString("cst"), rst.getDouble("aliquota"), rst.getDouble("reducao"));
                    String icmsCreditoId = getAliquotaKey(rst.getString("cstentrada"), rst.getDouble("aliquota"), rst.getDouble("reducao"));

                    imp.setIcmsDebitoId(icmsDebitoId);
                    imp.setIcmsDebitoForaEstadoId(icmsDebitoId);
                    imp.setIcmsDebitoForaEstadoNfId(icmsDebitoId);
                    imp.setIcmsCreditoId(icmsCreditoId);
                    imp.setIcmsCreditoForaEstadoId(icmsCreditoId);
                    imp.setIcmsConsumidorId(icmsDebitoId);

                    result.add(imp);
                }
            }
        }
        return result;
    }
    
    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                            + " p.numero as id, \n"
                            + " p.ean as ean, \n"
                            + " p.unidade as tipoembalagem, \n"
                            + " p.embalagem as qtdembalagem \n"
                            + "from produto p \n"
                            + "order by 1"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setEan(rst.getString("ean"));
                    imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                    result.add(imp);
                }
            }
        }
        return result;
    }
    
    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    f.codigo as id,\n"
                    + "    f.nome as razao,\n"
                    + "    f.fantasia,\n"
                    + "    f.cgc as cnpj,\n"
                    + "    f.insc as ie_rg,\n"
                    + "    f.endereco,\n"
                    + "    f.numero,\n"
                    + "    f.complemento,\n"
                    + "    f.bairro,\n"
                    + "    f.cidade as municipio,\n"
                    + "    f.uf,\n"
                    + "    f.cep,\n"
                    + "    f.telefone,\n"
                    + "    f.fax,\n"
                    + "    f.contato,\n"
                    + "    f.telvend,\n"
                    + "    f.email,\n"
                    + "    case f.status when 'ATIVO' then 1 else 0 end situacaocadastro,\n"
                    + "    f.observacao\n"
                    + "from forneced f\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    imp.setIe_rg(rst.getString("ie_rg"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setTel_principal(rst.getString("telefone"));
                    imp.setAtivo(rst.getInt("situacaocadastro") == 1);
                    imp.setObservacao(rst.getString("observacao"));

                    if ((rst.getString("fax") != null)
                            && (!rst.getString("fax").trim().isEmpty())) {
                        imp.addTelefone("FAX", rst.getString("fax"));
                    }
                    if ((rst.getString("telvend") != null)
                            && (!rst.getString("telvend").trim().isEmpty())) {
                        imp.addTelefone("TELEFONE VENDA", rst.getString("telvend"));
                    }
                    if ((rst.getString("email") != null)
                            && (!rst.getString("email").trim().isEmpty())) {
                        imp.addTelefone("EMAIL", rst.getString("email").toLowerCase());
                    }

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    pf.numpro as idproduto,\n"
                    + "    pf.fornec as idfornecedor,\n"
                    + "    pf.codfor as codigoexterno,\n"
                    + "    pf.embalagem as qtdembalagem\n"
                    + "from codigofor pf\n"
                    + "order by 2, 1"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rst.getString("idproduto"));
                    imp.setIdFornecedor(rst.getString("idfornecedor"));
                    imp.setCodigoExterno(rst.getString("codigoexterno"));
                    imp.setQtdEmbalagem(rst.getDouble("qtdembalagem"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    c.codigo as id,\n"
                    + "    c.nome as razao,\n"
                    + "    c.fantasia,\n"
                    + "    c.cpf,\n"
                    + "    c.ie as inscricaoestadual,\n"
                    + "    c.endres as endereco,\n"
                    + "    c.endcom as complemento,\n"
                    + "    c.baires as bairro,\n"
                    + "    c.numres as numero,\n"
                    + "    c.cidade as municipio,\n"
                    + "    c.uf,\n"
                    + "    c.cep,\n"
                    + "    c.telres as telefone,\n"
                    + "    c.telcom as telefone_comercial,\n"
                    + "    c.celular,\n"
                    + "    c.email,\n"
                    + "    case c.status when 'ATIVO' then 1 else 0 end situacaocadastro,\n"
                    + "    c.nascimento as datanascimento,\n"
                    + "    c.dtcadastro as datacadastro,\n"
                    + "    c.pai as nomepai,\n"
                    + "    c.mae as nomemae,\n"
                    + "    c.conjuge as nomeconjuge,\n"
                    + "    c.estcivil as estadocivil,\n"
                    + "    c.contato,\n"
                    + "    c.observacao,\n"
                    + "    c.endcob as endereco_cobranca,\n"
                    + "    c.numcob as numero_cobranca,\n"
                    + "    c.baicob as bairro_cobranca,\n"
                    + "    c.cidcob as municipio_cobranca,\n"
                    + "    c.ufcob as uf_cobranca,\n"
                    + "    c.cepcob as cep_cobranca,\n"
                    + "    c.renda as salario,\n"
                    + "    c.profissao as cargo,\n"
                    + "    c.limite as valor_limite\n"
                    + "from cliente c\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj(rst.getString("cpf"));
                    imp.setInscricaoestadual(Utils.acertarTexto(rst.getString("inscricaoestadual")));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setTelefone(rst.getString("telefone"));
                    imp.setCelular(rst.getString("celular"));
                    imp.setEmail(rst.getString("email"));
                    imp.setAtivo(rst.getInt("situacaocadastro") == 1);
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setDataNascimento(rst.getDate("datanascimento"));
                    imp.setNomePai(rst.getString("nomepai"));
                    imp.setNomeMae(rst.getString("nomemae"));
                    imp.setNomeConjuge(rst.getString("nomeconjuge"));
                    imp.setCargo(rst.getString("cargo"));
                    imp.setSalario(rst.getDouble("salario"));
                    imp.setValorLimite(rst.getDouble("valor_limite"));
                    imp.setObservacao(rst.getString("observacao"));
                    imp.setCobrancaEndereco(rst.getString("endereco_cobranca"));
                    imp.setCobrancaNumero(rst.getString("numero_cobranca"));
                    imp.setCobrancaBairro(rst.getString("bairro_cobranca"));
                    imp.setCobrancaMunicipio(rst.getString("municipio_cobranca"));
                    imp.setCobrancaUf(rst.getString("uf_cobranca"));
                    imp.setCobrancaCep(rst.getString("cep_cobranca"));

                    if ((rst.getString("telefone_comercial") != null)
                            && (!rst.getString("telefone_comercial").trim().isEmpty())) {
                        imp.addTelefone("TEL COMERCIAL", rst.getString("telefone_comercial"));
                    }

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    r.numero as id,\n"
                    + "    r.parcela,\n"
                    + "    r.cliente as idcliente,\n"
                    + "    r.compra as dataemissao,\n"
                    + "    r.vencimento as datavencimento,\n"
                    + "    r.valor,\n"
                    + "    r.acrescimo,\n"
                    + "    r.desconto,\n"
                    + "    r.numeronf as numerocupom\n"
                    + "from areceber r\n"
                    + "where r.dtbaixa is null"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rst.getString("id"));
                    imp.setIdCliente(rst.getString("idcliente"));
                    imp.setDataEmissao(rst.getDate("dataemissao"));
                    imp.setDataVencimento(rst.getDate("datavencimento"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setNumeroCupom(rst.getString("numerocupom"));
                    imp.setParcela(rst.getInt("parcela"));
                    result.add(imp);
                }
            }
        }
        return result;
    }
}
