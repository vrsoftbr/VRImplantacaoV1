package vrimplantacao2_5.dao.sistema;

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
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import static vr.core.utils.StringUtils.LOG;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.dao.cadastro.produto2.associado.OpcaoAssociado;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.vo.cadastro.oferta.SituacaoOferta;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.importacao.AssociadoIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.OfertaIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;
import vrimplantacao2_5.dao.conexao.ConexaoPostgres;

/**
 *
 * @author guilhermegomes
 */

/* OBSERVAÇÃO: Banco de dados do sistema é DBF convertido para POSTGRES, utilizando Linux!
    
    Arquivos DBF necessários:
        arqbar.dbf
        arqfab.dbf
        cadcli.dbf
        cadfil.dbf
        cadforn.dbf
        cadpro.dbf
        conrec.dbf
        contpag.dbf
        medpro.dbf
        profer.dbf
        profor.dbf
        tabagr.dbf
        tabmun.dbf
        tpiscof.dbf
        tabgru.dbf
        tabdep.dbf
 */
public class SGDAO extends InterfaceDAO implements MapaTributoProvider {

    public boolean digitobalanca = false;

    public void setDigitoBalanca(boolean digitoBalanca) {
        this.digitobalanca = digitobalanca;
    }

    @Override
    public String getSistema() {
        return "SG";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.DATA_CADASTRO,
                OpcaoProduto.QTD_EMBALAGEM_COTACAO,
                OpcaoProduto.QTD_EMBALAGEM_EAN,
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.VALIDADE,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                OpcaoProduto.FABRICANTE,
                OpcaoProduto.FAMILIA,
                OpcaoProduto.FAMILIA_PRODUTO,
                OpcaoProduto.ATIVO,
                OpcaoProduto.PESO_BRUTO,
                OpcaoProduto.PESO_LIQUIDO,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.TROCA,
                OpcaoProduto.MARGEM,
                OpcaoProduto.VENDA_PDV,
                OpcaoProduto.PDV_VENDA,
                OpcaoProduto.PRECO,
                OpcaoProduto.CUSTO,
                OpcaoProduto.CUSTO_COM_IMPOSTO,
                OpcaoProduto.CUSTO_SEM_IMPOSTO,
                OpcaoProduto.NCM,
                OpcaoProduto.CEST,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.NATUREZA_RECEITA,
                OpcaoProduto.ICMS,
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.ATUALIZAR_SOMAR_ESTOQUE,
                OpcaoProduto.OFERTA,
                OpcaoProduto.DESCONTINUADO,
                OpcaoProduto.VOLUME_QTD,
                OpcaoProduto.VOLUME_TIPO_EMBALAGEM,
                OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                OpcaoProduto.ASSOCIADO
        ));
    }

    @Override
    public Set<OpcaoFornecedor> getOpcoesDisponiveisFornecedor() {
        return new HashSet<>(Arrays.asList(
                OpcaoFornecedor.ENDERECO,
                OpcaoFornecedor.DADOS,
                OpcaoFornecedor.CONTATOS,
                OpcaoFornecedor.SITUACAO_CADASTRO,
                OpcaoFornecedor.PAGAR_FORNECEDOR,
                OpcaoFornecedor.PRODUTO_FORNECEDOR));
    }

    @Override
    public Set<OpcaoCliente> getOpcoesDisponiveisCliente() {
        return new HashSet<>(Arrays.asList(
                OpcaoCliente.DADOS,
                OpcaoCliente.ENDERECO,
                OpcaoCliente.CONTATOS,
                OpcaoCliente.DATA_CADASTRO,
                OpcaoCliente.DATA_NASCIMENTO,
                OpcaoCliente.RECEBER_CREDITOROTATIVO));
    }

    @Override
    public List<AssociadoIMP> getAssociados(Set<OpcaoAssociado> opt) throws Exception {
        List<AssociadoIMP> result = new ArrayList<>();

        String sqlAssociado = "select distinct  \n"
                + " a.codpro id_produtopai,\n"
                + " p.descpro01 descricao_pai,\n"
                + " a.codassoc id_produtofilho,\n"
                + " p2.descpro01 descricao_filho,\n"
                + " a.qtembassoc qtde \n"
                + "from proass a\n"
                + "join cadpro p on p.codpro01 = a.codpro\n"
                + "join cadpro p2 on p2.codpro01 = a.codassoc \n"
                + "order by 1";

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(sqlAssociado)) {
                while (rs.next()) {
                    AssociadoIMP imp = new AssociadoIMP();

                    imp.setId(rs.getString("id_produtopai"));
                    imp.setDescricao(rs.getString("descricao_pai"));
                    imp.setQtdEmbalagem(rs.getInt("qtde"));
                    imp.setProdutoAssociadoId(rs.getString("id_produtofilho"));
                    imp.setDescricaoProdutoAssociado(rs.getString("descricao_filho"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	distinct \n"
                    + "	alikicm01 || '-' || coalesce(cdsitrib01, 0) id,\n"
                    + "	alikicm01 descricao,\n"
                    + "	cdsitrib01 cst\n"
                    + "from \n"
                    + "	cadpro")) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(rs.getString("id"), rs.getString("descricao")));
                }
            }
        }

        return result;
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + " codpro produtoid,\n"
                    + " codbarra codigobarras,\n"
                    + " qtdeembal embalagem,\n"
                    + " case when qtdeembal > 1 then 'CX'\n"
                    + " else 'UN' end as unidade\n"
                    + "from arqbar;"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("produtoid"));
                    imp.setEan(rst.getString("codigobarras"));
                    imp.setQtdEmbalagem(rst.getInt("embalagem"));
                    imp.setTipoEmbalagem(rst.getString("unidade"));

                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        Map<Integer, vrimplantacao2.vo.cadastro.ProdutoBalancaVO> produtosBalanca
                = new ProdutoBalancaDAO().getProdutosBalanca();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select distinct on (p.codpro01) \n"
                    + "	p.codpro01 id,\n"
                    + "	p.balanca01,\n"
                    + " p.agrupa01 familia_id,\n"
                    //+ "	ean.codbarra codigobarras,\n"
                    + (digitobalanca == true ? "case when p.balanca01 is not null then left(ean.codbarra::varchar,-1) "
                            + "else ean.codbarra::varchar end codigobarras,\n" : "ean.codbarra codigobarras,\n")
                    + "	ean.qtdeembal qtdembalagemvenda,\n"
                    + "(p.descpro01||''||p.desccomp01) descricaocompleta,\n"
                    + "	p.descpro01 descricaogondola,\n"
                    + "	p.descabr01 descricaoreduzida,\n"
                    + "	p.datacad01 datacadastro,\n"
                    + "	p.unidpro01 unidade,\n"
                    + "	p.cusreal01 custocomimposto,\n"
                    + "	p.custfis01 custosemimposto,\n"
                    + "	p.prevend01 precovenda,\n"
                    + "	p.precopdv01 precopdv,\n"
                    + "	p.margtra01 margem,\n"
                    + "	p.qtdeemb01 qtdembalagem,\n"
                    + "	p.estatu01 estoque,\n"
                    + "	p.estmin01 estoqueminimo,\n"
                    + "	p.estmax01 estoquemaximo,\n"
                    + " f.codforn02 fabricante,\n"
                    + "	p.pesopro01 pesobruto,\n"
                    + "	p.pesoliq01 pesoliquido,\n"
                    + "	p.alikicm01 icmsaliquota,\n"
                    + "	p.cdsitrib01 cst,\n"
                    + "	p.classfis01,\n"
                    + "	p.pbcreduz01 icmsreducao,\n"
                    + "	p.icmcompr01 icmsaliquotacredito,\n"
                    + "	p.alikicm01 || '-' || coalesce(p.cdsitrib01, 0) id_aliquotadebito, \n"
                    + "	p.cdobsicm01 idicms,\n"
                    + "	p.aicmstef01 icmstef,\n"
                    + "case when upper(p.piscofin01) = 'M' then '04'\n"
                    + "	     when upper(p.piscofin01) = 'C' then '08'\n"
                    + "	     when upper(p.piscofin01) = 'I' then '07'\n"
                    + "	     when upper(p.piscofin01) = 'P' then '09'\n"
                    + "	     when upper(p.piscofin01) = 'S' then '05'\n"
                    + "	     when upper(p.piscofin01) = 'Z' then '06'\n"
                    + "	     when upper(p.piscofin01) = 'E' then '01'\n"
                    + " else 01 end cstpis,\n"
                    + "	pis.cstcofins,\n"
                    + "	p.codncm01 ncm,\n"
                    + "	p.codcest01 cest,\n"
                    + "	p.natpisco01 naturezareceita,\n"
                    + " case when sitpro01 = 'I' then 0\n"
                    + "    else 1 end situacao,\n"
                    + " case \n"
                    + "		when medida = 'M' then 'MT'\n"
                    + "		when medida = 'U' then 'UN'\n"
                    + "		when medida = 'G' then 'KG'\n"
                    + "		when medida = 'L' then 'LT'\n"
                    + " else 'UN'\n"
                    + "	end tipo_volume,\n"
                    + "	m.quantidade volume, \n"
                    + " s.coddepto merc1, \n"
                    + " g.codgrupo merc2 \n"
                    + "from \n"
                    + "	cadpro p \n"
                    + "left join arqbar ean on p.codpro01 = ean.codpro\n"
                    + "left join tpiscof pis on p.codpro01 = pis.codpro and \n"
                    + "	pis.entsai = 'S' and \n"
                    + "	pis.filial = p.codfil01\n"
                    + "left join cadforn f on substring(p.fornec01,1,15) = substring(f.nomeabr02,1,15)\n"
                    + "left join medpro m on m.filial = p.codfil01 and m.codpro = p.codpro01\n"
                    + "left join tabgru g on g.codgrupo = p.codgrupo01\n"
                    + "left join tabdep s on s.coddepto = g.coddepto\n"
                    + "where \n"
                    + "	p.codfil01 = " + getLojaOrigem())) {
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id"));
                    imp.setEan(rs.getString("codigobarras"));
                    imp.setDescricaoCompleta(rs.getString("descricaocompleta"));
                    imp.setDescricaoGondola(rs.getString("descricaogondola"));
                    imp.setDescricaoReduzida(rs.getString("descricaoreduzida"));
                    imp.setDataCadastro(rs.getDate("datacadastro"));
                    imp.setTipoEmbalagem(rs.getString("unidade"));

                    imp.setCodMercadologico1(rs.getString("merc1"));
                    imp.setCodMercadologico2(rs.getString("merc2"));
                    imp.setCodMercadologico3(imp.getCodMercadologico2());

                    if (rs.getDouble("custocomimposto") < rs.getDouble("custosemimposto")) {
                        imp.setCustoComImposto(rs.getDouble("custosemimposto"));
                        imp.setCustoSemImposto(rs.getDouble("custocomimposto"));
                    } else {
                        imp.setCustoComImposto(rs.getDouble("custocomimposto"));
                        imp.setCustoSemImposto(rs.getDouble("custosemimposto"));
                    }

                    imp.setPrecovenda(rs.getDouble("precovenda"));
                    imp.setMargem(rs.getDouble("margem"));
                    imp.setQtdEmbalagemCotacao(rs.getInt("qtdembalagem"));
                    imp.setEstoque(rs.getDouble("estoque"));
                    imp.setEstoqueMinimo(rs.getDouble("estoqueminimo"));
                    imp.setEstoqueMaximo(rs.getDouble("estoquemaximo"));
                    imp.setPesoBruto(rs.getDouble("pesobruto"));
                    imp.setPesoLiquido(rs.getDouble("pesoliquido"));
                    imp.setIdFamiliaProduto(rs.getString("familia_id"));
                    imp.setSituacaoCadastro(rs.getInt("situacao") == 1 ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    imp.setFornecedorFabricante(rs.getString("fabricante"));
                    imp.setTipoEmbalagemVolume(rs.getString("tipo_volume"));
                    imp.setVolume(rs.getDouble("volume"));

                    imp.setIcmsConsumidorId(rs.getString("id_aliquotadebito"));
                    imp.setIcmsDebitoId(imp.getIcmsConsumidorId());
                    imp.setIcmsDebitoForaEstadoId(imp.getIcmsConsumidorId());
                    imp.setIcmsDebitoForaEstadoNfId(imp.getIcmsConsumidorId());
                    imp.setIcmsCreditoId(imp.getIcmsConsumidorId());
                    imp.setIcmsCreditoForaEstadoId(imp.getIcmsConsumidorId());
                    imp.setPiscofinsNaturezaReceita(rs.getInt("naturezareceita"));
                    imp.setNcm(rs.getString("ncm"));
                    imp.setCest(rs.getString("cest"));
                    imp.setPiscofinsCstDebito(rs.getString("cstpis"));

                    ProdutoBalancaVO balanca = produtosBalanca.get(Utils.stringToInt(imp.getEan(), -2));

                    if (balanca != null) {
                        imp.seteBalanca(true);
                        imp.setTipoEmbalagem("P".equals(balanca.getPesavel()) ? "KG" : "UN");
                        imp.setValidade(balanca.getValidade() > 1 ? balanca.getValidade() : 0);
                    } else {
                        imp.setValidade(0);
                        imp.seteBalanca(false);
                    }

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select distinct\n"
                    + " s.coddepto merc1,\n"
                    + " s.nomedepto desc1,\n"
                    + " g.codgrupo merc2,\n"
                    + " g.descgrupo desc2\n"
                    + "from tabgru g\n"
                    + "join tabdep s on s.coddepto = g.coddepto\n"
                    + "order by 1,2"
            )) {
                while (rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rs.getString("merc1"));
                    imp.setMerc1Descricao(rs.getString("desc1"));
                    imp.setMerc2ID(rs.getString("merc2"));
                    imp.setMerc2Descricao(rs.getString("desc2"));
                    imp.setMerc3ID(imp.getMerc2ID());
                    imp.setMerc3Descricao(imp.getMerc2Descricao());

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + " codagru03 id,\n"
                    + " desagru03 descricao\n"
                    + "from tabagr;"
            )) {
                while (rst.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setDescricao(rst.getString("descricao"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<OfertaIMP> getOfertas(Date datatermino) throws Exception {
        List<OfertaIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + " o.codpro produtoid,\n"
                    + " o.dataini,\n"
                    + " o.datafim,\n"
                    + " o.precofer oferta,\n"
                    + " p.prevend01 precovenda,\n"
                    + " o.codfil loja\n"
                    + "from profer o\n"
                    + "join cadpro p on p.codpro01 = o.codpro \n"
                    + "where\n"
                    + " o.datafim >= now()\n"
                    + " and\n"
                    + " o.codfil = " + getLojaOrigem() + " "
            )) {
                while (rs.next()) {
                    OfertaIMP imp = new OfertaIMP();
                    imp.setIdProduto(rs.getString("produtoid"));
                    imp.setDataInicio(rs.getDate("dataini"));
                    imp.setDataFim(rs.getDate("datafim"));
                    imp.setPrecoNormal(rs.getDouble("precovenda"));
                    imp.setPrecoOferta(rs.getDouble("oferta"));
                    imp.setSituacaoOferta(SituacaoOferta.ATIVO);

                    result.add(imp);

                }
            }
        }
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	f.codforn02 id,\n"
                    + "	f.razforn02 razao,\n"
                    + "	f.nomeabr02 fantasia,\n"
                    + "	f.endforn02 endereco,\n"
                    + "	f.bairro02 bairro,\n"
                    + "	f.cgcforn02 cnpj,\n"
                    + "	f.insforn02 ie,\n"
                    + "	f.insmunic02 im,\n"
                    + "	f.cepforn02 cep,\n"
                    + "	f.fonefor02 telefone,\n"
                    + "	f.nomecon02 nomecontato,\n"
                    + "	m.codibge ibge_municipio,\n"
                    + "	m.nome municipio,\n"
                    + "	m.uf,\n"
                    + "	f.numender02 numero,\n"
                    + "	f.compleme02 complemento,\n"
                    + "	f.datacad02 cadastro,\n"
                    + "	f.datanasc02 nascimento\n"
                    + "from \n"
                    + "	cadforn f \n"
                    + "left join tabmun m on f.codmunic02 = m.codigo")) {
                while (rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("id"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setCnpj_cpf(rs.getString("cnpj"));
                    imp.setIe_rg(rs.getString("ie"));
                    imp.setCep(rs.getString("cep"));
                    imp.setTel_principal(rs.getString("telefone"));
                    imp.setIbge_municipio(rs.getInt("ibge_municipio"));
                    imp.setUf(rs.getString("uf"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setDatacadastro(rs.getDate("cadastro"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	codforn13 id_fornecedor,\n"
                    + "	codpro13 id_produto,\n"
                    + "	p.unidpro01 unidade,\n"
                    + "	ce.cdfabric codexterno\n"
                    + "from\n"
                    + "	profor pf\n"
                    + "join cadpro p on pf.codpro13 = p.codpro01 and p.codfil01 = " + getLojaOrigem() + "\n"
                    + "join cadforn f on pf.codforn13 = f.codforn02\n"
                    + "join arqfab ce on ce.codpro = pf.codpro13 and ce.codforn = pf.codforn13\n"
                    + "order by codforn13, codpro13")) {
                while (rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());

                    imp.setIdFornecedor(rs.getString("id_fornecedor"));
                    imp.setIdProduto(rs.getString("id_produto"));
                    imp.setCodigoExterno(rs.getString("codexterno"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	c.codcli10 id,\n"
                    + "	c.razsoc10 razao,\n"
                    + "	c.nomefan10,\n"
                    + "	c.cgccpf10 cnpj,\n"
                    + "	c.insccli10 ie,\n"
                    + "	c.endcli10 endereco,\n"
                    + "	c.numendcl10 numero,\n"
                    + "	c.bairro10 bairro,\n"
                    + "	c.cepcli10 cep,\n"
                    + "	m.codibge ibge_municipio,\n"
                    + "	m.nome municipio,\n"
                    + "	m.uf,\n"
                    + "	c.fonecli10 telefone,\n"
                    + "	c.celcli10 celular,\n"
                    + "	c.datacad10 cadastro,\n"
                    + "	c.datanasc10 nascimento,\n"
                    + "	c.limcompr10 valorlimite,\n"
                    + "	c.situacao10 situacao,\n"
                    + "	c.emailcli10 email\n"
                    + "from \n"
                    + "	cadcli c\n"
                    + "left join tabmun m on c.codmunic10 = m.codigo")) {
                while (rs.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rs.getString("id"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("nomefan10"));
                    imp.setCnpj(rs.getString("cnpj"));
                    imp.setInscricaoestadual(rs.getString("ie"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setCep(rs.getString("cep"));
                    imp.setMunicipioIBGE(rs.getInt("ibge_municipio"));
                    imp.setUf(rs.getString("uf"));
                    imp.setTelefone(rs.getString("telefone"));
                    imp.setCelular(rs.getString("celular"));
                    imp.setDataCadastro(rs.getDate("cadastro"));
                    imp.setDataNascimento(rs.getDate("nascimento"));
                    imp.setValorLimite(rs.getDouble("valorlimite"));
                    imp.setEmail(rs.getString("email"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ContaPagarIMP> getContasPagar() throws Exception {
        List<ContaPagarIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + " c.lanent15::varchar||ordpag15::varchar id,\n"
                    + " f.codforn02 idfornecedor,\n"
                    + " c.codfil15 loja,\n"
                    + " c.datemis15 dtemissao,\n"
                    + " c.datvenc15 dtvencimento,\n"
                    + " c.numdoc15 numerodocumento,\n"
                    + " c.valpag15 valor,\n"
                    + " c.desconto15 desconto,\n"
                    + " c.ttparcel15 parcela,\n"
                    + " c.observ15 obs\n"
                    + "from contpag c\n"
                    + "join cadforn f on f.codforn02 = c.codforn15\n"
                    + "where \n"
                    + " c.datpag15 is null \n"
                    + " and\n"
                    + " c.codfil15 = " + getLojaOrigem() + ";"
            )) {
                while (rst.next()) {
                    ContaPagarIMP imp = new ContaPagarIMP();

                    imp.setId(rst.getString("id"));
                    imp.setIdFornecedor(rst.getString("idfornecedor"));
                    imp.setDataEmissao(rst.getDate("dtemissao"));
                    imp.setVencimento(rst.getDate("dtvencimento"));
                    imp.setNumeroDocumento(rst.getString("numeroDocumento"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setObservacao(rst.getString("obs"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	c.lansai60 id,\n"
                    + "	cp.codcli10 id_cliente,\n"
                    + "	cp.cgccpf10 cnpj,\n"
                    + "	cp.razsoc10 razao,\n"
                    + "	c.numdoc60 documento,\n"
                    + "	c.datemis60 emissao,\n"
                    + "	c.datvenc60 vencimento,\n"
                    + "	c.valor60 valor,\n"
                    + "	c.valrec60 valorecebido,\n"
                    + "	c.datrec60 recebimento,\n"
                    + "	c.jurosrec60 juros,\n"
                    + "	c.observ60 observacao,\n"
                    + "	c.clifor60 tipo\n"
                    + "from \n"
                    + "	conrec c\n"
                    + "inner join cadcli cp on c.codcli60 = cp.codcli10\n"
                    + "where \n"
                    + "	c.codfil60 = " + getLojaOrigem() + " and\n"
                    + "   c.datrec60 is null")) {
                while (rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();

                    imp.setId(rs.getString("id"));
                    imp.setIdCliente(rs.getString("id_cliente"));
                    imp.setValor(rs.getDouble("valor"));
                    imp.setDataEmissao(rs.getDate("emissao"));
                    imp.setDataVencimento(rs.getDate("vencimento"));
                    imp.setCnpjCliente(rs.getString("cnpj"));
                    imp.setNumeroCupom(rs.getString("documento"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    private Date dataInicioVenda;
    private Date dataTerminoVenda;
    private String tabelaVenda;

    public void setDataInicioVenda(Date dataInicioVenda) {
        this.dataInicioVenda = dataInicioVenda;
    }

    public void setDataTerminoVenda(Date dataTerminoVenda) {
        this.dataTerminoVenda = dataTerminoVenda;
    }

    public void setTabelaVenda(String tabelaVenda) {
        this.tabelaVenda = tabelaVenda;
    }

    public String getTabelaVenda() {
        return this.tabelaVenda;
    }

    @Override
    public Iterator<VendaIMP> getVendaIterator() throws Exception {
        return new SGDAO.VendaIterator(getLojaOrigem(), dataInicioVenda, dataTerminoVenda, getTabelaVenda());
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        return new SGDAO.VendaItemIterator(getLojaOrigem(), dataInicioVenda, dataTerminoVenda, getTabelaVenda());
    }

    private static class VendaIterator implements Iterator<VendaIMP> {

        public final static SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");

        private Statement stm = ConexaoPostgres.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaIMP next;
        private Set<String> uk = new HashSet<>();

        private void obterNext() {
            try {
                SimpleDateFormat timestampDate = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                if (next == null) {
                    if (rst.next()) {
                        next = new VendaIMP();
                        String id = rst.getString("id_venda");

                        if (!uk.add(id)) {
                            LOG.warning("Venda " + id + " já existe na listagem");
                        }

                        next.setId(id);
                        next.setNumeroCupom(rst.getInt("cupom"));
                        next.setEcf(rst.getInt("ecf"));
                        next.setData(rst.getDate("datavenda"));
                        String horaInicio = timestampDate.format(rst.getDate("datavenda")) + " 00:00:00";
                        String horaTermino = timestampDate.format(rst.getDate("datavenda")) + " 00:00:00";
                        next.setHoraInicio(timestamp.parse(horaInicio));
                        next.setHoraTermino(timestamp.parse(horaTermino));
                        next.setSubTotalImpressora(rst.getDouble("valor"));
                    }
                }
            } catch (SQLException | ParseException ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaIterator(String idLojaCliente, Date dataInicio, Date dataTermino, String tabelaVenda) throws Exception {
            this.sql
                    = "select\n"
                    + "	FILIAL::varchar || numeropdv || v.operador ||COO || v.data ||HORAFINAL id_venda,\n"
                    + "	coo cupom,\n"
                    + "	NUMEROPDV ecf,\n"
                    + "	v.DATA datavenda,\n"
                    + "	valor\n"
                    + "from\n"
                    + "	cupom v\n"
                    + "	join lp" + tabelaVenda + " vi on v.coo = vi.ncupom and v.data = vi.data and v.numeropdv = vi.numecr \n"
                    + "where\n"
                    + "	FILIAL = " + idLojaCliente + "\n"
                    + "group by 1,2,3,4,5\n"
                    + "order by v.data, v.coo";
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

        private Statement stm = ConexaoPostgres.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaItemIMP next;

        private void obterNext() {
            try {
                if (next == null) {
                    if (rst.next()) {
                        next = new VendaItemIMP();

                        next.setId(rst.getString("id_item"));
                        next.setVenda(rst.getString("id_venda"));
                        next.setSequencia(rst.getInt("sequencial"));
                        next.setProduto(rst.getString("id_produto"));
                        next.setDescricaoReduzida(rst.getString("produto"));
                        next.setUnidadeMedida(rst.getString("unidade"));
                        next.setCodigoBarras(rst.getString("ean"));
                        next.setQuantidade(rst.getDouble("quantidade"));
                        next.setPrecoVenda(rst.getDouble("precovenda"));
                        next.setTotalBruto(rst.getDouble("total"));
                        next.setValorDesconto(rst.getDouble("desconto"));
                        next.setCancelado(rst.getBoolean("cancelado"));
                    }
                }
            } catch (SQLException ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaItemIterator(String idLojaCliente, Date dataInicio, Date dataTermino, String tabelaVenda) throws Exception {
            this.sql
                    = "select\n"
                    + "	FILIAL::varchar || numeropdv || v.operador ||COO || v.data ||HORAFINAL id_venda,\n"
                    + " FILIAL::varchar || numeropdv || v.operador ||COO || v.data ||HORAFINAL || ordem::varchar || codpro || qtdevend || codbarra || ncupom || horario id_item,\n"
                    + "	ORDEM sequencial,\n"
                    + "	CODPRO id_produto,\n"
                    + "	p.descpro01 produto,\n"
                    + "	p.UNIDPRO01 unidade,\n"
                    + "	CODBARRA ean,\n"
                    + "	QTDEVEND quantidade,\n"
                    + "	round(PREVEND,2) precovenda,\n"
                    + "	round(QTDEVEND * PREVEND,2) total,\n"
                    + " desconto,\n"
                    + " case when cancelado = '' then 0 else 1 end cancelado\n"
                    + "from\n"
                    + "	lp" + tabelaVenda + " vi\n"
                    + "	join cupom v on v.coo = vi.ncupom and v.numeropdv = vi.numecr and v.filial = vi.codfil\n"
                    + "	join cadpro p on vi.CODPRO = p.CODPRO01 and vi.codfil = p.codfil01 \n"
                    + "where\n"
                    + "	vi.codfil = " + idLojaCliente + "\n"
                    + "order by\n"
                    + "	v.data, v.coo, vi.ORDEM";
            LOG.log(Level.FINE, "SQL da venda: " + sql);
            System.out.println(sql);
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
