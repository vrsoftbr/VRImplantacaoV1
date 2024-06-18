package vrimplantacao2_5.dao.sistema;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2_5.dao.conexao.ConexaoPostgres;

/**
 *
 * @author Bruno
 */
public class ProSuper2_5DAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "ProSuper";
    }

    /*
    Arquivos utilizados: 
    EAN = escodbar,
    Mercadologicos = esclassi,
    Produto e afins = estoque,
    Fornecedor = focadfor,
    Script Loja origem = ccconfig,empresa
    Cliente = ccclient,
    CredRotativo = cocadrec
    ProdutoFornecedor = prorelac
    
     */
    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.ATIVO,
                OpcaoProduto.ATUALIZAR_SOMAR_ESTOQUE,
                OpcaoProduto.CEST,
                OpcaoProduto.CUSTO,
                OpcaoProduto.CUSTO_COM_IMPOSTO,
                OpcaoProduto.CUSTO_SEM_IMPOSTO,
                OpcaoProduto.DATA_CADASTRO,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.FAMILIA,
                OpcaoProduto.FAMILIA_PRODUTO,
                OpcaoProduto.ICMS,
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                OpcaoProduto.MARGEM,
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                OpcaoProduto.NATUREZA_RECEITA,
                OpcaoProduto.NCM,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.PDV_VENDA,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.PRECO,
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.VALIDADE,
                OpcaoProduto.VENDA_PDV,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.ICMS_CONSUMIDOR,
                OpcaoProduto.ICMS_ENTRADA,
                OpcaoProduto.ICMS_ENTRADA_FORA_ESTADO,
                OpcaoProduto.ICMS_SAIDA,
                OpcaoProduto.ICMS_SAIDA_FORA_ESTADO,
                OpcaoProduto.VOLUME_TIPO_EMBALAGEM
        ));
    }

    @Override
    public Set<OpcaoFornecedor> getOpcoesDisponiveisFornecedor() {
        return new HashSet<>(Arrays.asList(
                OpcaoFornecedor.DADOS,
                OpcaoFornecedor.CONTATOS,
                OpcaoFornecedor.ENDERECO,
                OpcaoFornecedor.PRODUTO_FORNECEDOR,
                OpcaoFornecedor.RAZAO_SOCIAL,
                OpcaoFornecedor.NOME_FANTASIA,
                OpcaoFornecedor.PAGAR_FORNECEDOR,
                OpcaoFornecedor.SITUACAO_CADASTRO));
    }

    @Override
    public Set<OpcaoCliente> getOpcoesDisponiveisCliente() {
        return new HashSet<>(Arrays.asList(
                OpcaoCliente.CONTATOS,
                OpcaoCliente.SITUACAO_CADASTRO,
                OpcaoCliente.DADOS,
                OpcaoCliente.OBSERVACOES,
                OpcaoCliente.OBSERVACOES2,
                OpcaoCliente.ESTADO_CIVIL,
                OpcaoCliente.DATA_CADASTRO,
                OpcaoCliente.DATA_NASCIMENTO,
                OpcaoCliente.ENDERECO,
                OpcaoCliente.RECEBER_CREDITOROTATIVO,
                OpcaoCliente.VALOR_LIMITE));
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select distinct \n"
                    + "tqaliicm||tqsubsti::varchar||tqalipdv::varchar||tqtribut as id,\n"
                    + "case when tqsubsti <> 0 then tqaliicm::int||'% RDZ '||tqsubsti\n"
                    + "else tqaliicm::int||'%' end as descricao,\n"
                    + "tqaliicm  as aliq,\n"
                    + "tqsubsti as reducao,\n"
                    + "tqtribut::int  as cst \n"
                    + "from \n"
                    + "estoque \n"
                    + "union \n"
                    + "select distinct \n"
                    + "'c-'||tqalipdv id,\n"
                    + "'consumidor-'||tqalipdv descricao,\n"
                    + "tqalipdv  as aliq,\n"
                    + "0 as reducao,\n"
                    + "0  as cst \n"
                    + "from \n"
                    + "estoque "
            //                                        "select distinct \n"
            //                                        + "tqaliicm||tqsubsti::varchar||tqalipdv::varchar as id,\n"
            //                                        + "case when tqsubsti <> 0 then tqaliicm::int||'% RDZ '||tqsubsti\n"
            //                                        + "else tqaliicm::int||'%' end as descricao,\n"
            //                                        + "tqaliicm  as aliq,\n"
            //                                        + "tqsubsti as reducao,\n"
            //                                        + "tqalipdv  as cst \n"
            //                                        + "from \n"
            //                                        + "estoque "
            //            
            //                                        "select distinct \n"
            //                                        + "tqaliicm||tqsubsti::varchar||tqalipdv::varchar as id,\n"
            //                                        + "case when tqsubsti <> 0 then tqaliicm::int||'% RDZ '||tqsubsti\n"
            //                                        + "else tqaliicm::int||'%' end as descricao,\n"
            //                                        + "tqaliicm  as aliq,\n"
            //                                        + "tqsubsti as reducao,\n"
            //                                        + "tqalipdv  as cst \n"
            //                                        + "from \n"
            //                                        + "estoque "
            //                    " select distinct on (tqaliicm ||'-'|| tqsubsti::varchar)\n"
            //                    + "tqaliicm ||'-'|| tqsubsti::varchar as id,\n"
            //                    + "case when tqsubsti <> 0 then tqaliicm::int||'% RDZ '||tqsubsti\n"
            //                    + "else tqaliicm::int||'%' end as descricao,\n"
            //                    + "tqaliicm  as aliq,\n"
            //                    + "tqsubsti as reducao,\n"
            //                    + "tqalipdv as cst \n"
            //                    + "from \n"
            //                    + "estoque"
            )) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(
                            rs.getString("id"),
                            rs.getString("descricao"),
                            rs.getInt("cst"),
                            rs.getDouble("aliq"),
                            rs.getDouble("reducao")));
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
                    ""
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
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "with mercadologico1 as(\n"
                    + "select '1' merc1, 'GERAL' descricao, 0\n"
                    + "union\n"
                    + "select \n"
                    + " split_part(elcodigo,'.',1) merc1,\n"
                    + " eldescri descricao,\n"
                    + " length(replace(elcodigo,' ',''))\n"
                    + "from esclassi \n"
                    + "where \n"
                    + " length(replace(elcodigo,' ','')) between 3 and 4\n"
                    + " ) \n"
                    + ", mercadologico2 as(\n"
                    + "select '1' merc1, '001' merc2, 'GERAL' descricao, 0\n"
                    + "union\n"
                    + "select \n"
                    + " split_part(elcodigo,'.',1) merc1,\n"
                    + " split_part(elcodigo,'.',2) merc2,\n"
                    + " eldescri descricao,\n"
                    + " length(replace(elcodigo,' ',''))\n"
                    + "from esclassi \n"
                    + "where \n"
                    + " length(replace(elcodigo,' ','')) between 6 and 7 \n"
                    + " )\n"
                    + " , mercadologico3 as(\n"
                    + " select \n"
                    + " split_part(elcodigo,'.',1) merc1,\n"
                    + " split_part(elcodigo,'.',2) merc2,\n"
                    + " split_part(elcodigo,'.',3) merc3,\n"
                    + " eldescri descricao,\n"
                    + " length(replace(elcodigo,' ',''))\n"
                    + "from esclassi \n"
                    + "where \n"
                    + " length(replace(elcodigo,' ','')) > 7 \n"
                    + " )\n"
                    + "  select \n"
                    + "  m1.merc1,\n"
                    + "  m1.descricao as descricao1,\n"
                    + "  m2.merc2,\n"
                    + "  m2.descricao as descricao2,\n"
                    + "  m3.merc3,\n"
                    + "  m3.descricao as descricao3\n"
                    + " from mercadologico3 m3\n"
                    + " left join mercadologico2 m2 on m3.merc1 = m2.merc1 and m3.merc2 = m2.merc2\n"
                    + " left join mercadologico1 m1 on m2.merc1 = m1.merc1\n"
                    + "where m1.merc1 is not null \n"
                    + "  order by 1"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setMerc1ID(rst.getString("merc1"));
                    imp.setMerc1Descricao(rst.getString("descricao1"));
                    imp.setMerc2ID(rst.getString("merc2"));
                    imp.setMerc2Descricao(rst.getString("descricao2"));
                    imp.setMerc3ID(rst.getString("merc2"));
                    imp.setMerc3Descricao(rst.getString("descricao3"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    /*"select\n"
                    + "	distinct on (e.tqid) \n"
                    + "	e.tqcodigo as id_produto,\n"
                    + "	case when e.tqobserv = ''\n"
                    + "	then tqdescri else e.tqobserv end as descricaoreduzida,\n"
                    + "	tqdescri ||'-'||tqgrade as descricaocompleta,\n"
                    + "	e.tqncm as cod_ncm,\n"
                    + "	barra.cbcodbar as ean,\n"
                    + "	tqcest as cest,\n"
                    + "	case \n"
                    + "		when tqativo = 'S' then 1\n"
                    + "		when tqativo = 'N' then 0\n"
                    + "		else 1\n"
                    + "	end as ativo,\n"
                    + "	tqqtdmax as estoquemaximo,\n"
                    + "	tqqtdmin as estoqueminimo,\n"
                    + "	tqsaldo as estoque,\n"
                    + "	split_part(m.elcodigo,'.',1) as merc1,\n"
                    + "	split_part(m.elcodigo,'.',2) as merc2,\n"
                    + "	split_part(m.elcodigo,'.',3) as merc3,\n"
                    + "	tqpreven as preco,\n"
                    + "	tqprecus as custo,\n"
                    + "	tqlucro as margem,\n"
                    + "	tqnatise as nat_receita,\n"
                    + "	tqaliicm||tqsubsti::varchar||tqalipdv::varchar as icms,\n"
                    + "	tqcstcof as cst_cofins,\n"
                    + " e.tqalipdv as pdv, \n"
                    + "	tqcstpie as cst_pis_entrada,\n"
                    + "e.tqid as id \n"
                    + "from\n"
                    + "	estoque e\n"
                    + "left join escodbar barra on barra.cbcodigo = e.tqcodigo and e.tqgrade = barra.cbgrade\n"
                    + "left join esclassi m on e.tqclassi = m.elcodigo "*/
                    "with icms as(select distinct on (tqcodigo) \n"
                    + "tqid,\n"
                    + "tqcodigo,\n"
                    + "tqalipdv as cst \n"
                    + "from \n"
                    + "estoque )\n"
                    + "select\n"
                    + "	distinct on (e.tqid) \n"
                    + "	e.tqcodigo as id_produto,\n"
                    + "	case when e.tqobserv = ''\n"
                    + "	then tqdescri else e.tqobserv end as descricaoreduzida,\n"
                    + "	tqdescri ||'-'||tqgrade as descricaocompleta,\n"
                    + "	e.tqncm as cod_ncm,\n"
                    + "	barra.cbcodbar as ean,\n"
                    + "	tqcest as cest,\n"
                    + "	case \n"
                    + "		when tqativo = 'S' then 1\n"
                    + "		when tqativo = 'N' then 0\n"
                    + "		else 1\n"
                    + "	end as ativo,\n"
                    + "	tqqtdmax as estoquemaximo,\n"
                    + "	tqqtdmin as estoqueminimo,\n"
                    + "	tqsaldo as estoque,\n"
                    + "	split_part(m.elcodigo,'.',1) as merc1,\n"
                    + "	split_part(m.elcodigo,'.',2) as merc2,\n"
                    + "	split_part(m.elcodigo,'.',3) as merc3,\n"
                    + "	tqpreven as preco,\n"
                    + "	tqprecus as custo,\n"
                    + "	tqlucro as margem,\n"
                    + "	tqnatise as nat_receita,\n"
                    + "	tqaliicm||tqsubsti::varchar||tqalipdv::varchar||tqtribut as icms,\n"
                    + "	'c-'||tqalipdv as pdv,\n"
                    + "	tqcstcof as cst_cofins,\n"
                    + "	tqcstpie as cst_pis_entrada,\n"
                    + "e.tqid as id \n"
                    + "from\n"
                    + "	estoque e\n"
                    + "left join escodbar barra on barra.cbcodigo = e.tqcodigo and e.tqgrade = barra.cbgrade\n"
                    + "left join esclassi m on e.tqclassi = m.elcodigo \n"
                    + "join icms i on i.tqcodigo = e.tqcodigo"
            )) {
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().getProdutosBalanca();
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("id"));

                    int codigoProduto = Utils.stringToInt(rst.getString("ean"), -2);
                    ProdutoBalancaVO produtoBalanca = produtosBalanca.get(codigoProduto);

                    if (produtoBalanca != null) {
                        imp.setEan(String.valueOf(produtoBalanca.getCodigo()));
                        imp.seteBalanca(true);
                        imp.setTipoEmbalagem("U".equals(produtoBalanca.getPesavel()) ? "UN" : "KG");
                        imp.setValidade(produtoBalanca.getValidade());
                        imp.setQtdEmbalagem(1);
                    } else {
                        imp.seteBalanca(false);
                        imp.setValidade(0);
                        imp.setQtdEmbalagem(1);
                    }

                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida").isEmpty()
                            ? imp.getDescricaoCompleta()
                            : rst.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(imp.getDescricaoReduzida());

                    imp.setNcm(rst.getString("cod_ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setSituacaoCadastro(rst.getInt("ativo"));

                    imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
                    imp.setEstoqueMaximo(rst.getDouble("estoquemaximo"));
                    imp.setEstoque(rst.getDouble("estoque"));

                    imp.setCodMercadologico1(rst.getString("merc1"));
                    imp.setCodMercadologico2(rst.getString("merc2"));
                    imp.setCodMercadologico3(rst.getString("merc3"));

                    imp.setCustoComImposto(rst.getDouble("custo"));
                    imp.setCustoSemImposto(rst.getDouble("custo"));
                    imp.setPrecovenda(rst.getDouble("preco"));
                    imp.setMargem(rst.getDouble("margem"));

                    String idIcms = rst.getString("icms");

                    imp.setIcmsDebitoId(idIcms);
                    imp.setIcmsDebitoForaEstadoId(idIcms);
                    imp.setIcmsDebitoForaEstadoNfId(idIcms);
                    imp.setIcmsConsumidorId(rst.getString("pdv"));
                    imp.setIcmsCreditoId(idIcms);
                    imp.setIcmsCreditoForaEstadoId(idIcms);

                    imp.setPiscofinsCstDebito(rst.getString("cst_cofins"));
                    imp.setPiscofinsCstCredito(rst.getString("cst_pis_entrada"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("nat_receita"));

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
                    "select \n"
                    + "	distinct \n"
                    + "	e.tqid as id_produto,\n"
                    + "	p.spcodfor  as id_fornecedor, \n"
                    + "	p.spcodxml as codigoexterno\n"
                    + "	from prorelac p\n"
                    + "	left join estoque e on e.tqcodigo = p.spcodigo "
            )) {
                while (rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setIdProduto(rs.getString("id_produto"));
                    imp.setIdFornecedor(rs.getString("id_fornecedor"));
                    imp.setCodigoExterno(rs.getString("codigoexterno"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "  	e.tqcodigo ,\n"
                    + "  	e.tqdescri,\n"
                    + "  	e.tqgrade ,\n"
                    + "	e.tqid as id_produto,\n"
                    + "	cbcodbar as ean\n"
                    + "	from\n"
                    + "	escodbar b\n"
                    + "	join estoque e on e.tqcodigo = b.cbcodigo and e.tqgrade = b.cbgrade"
            )) {
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rs.getString("id_produto"));
                    imp.setEan(rs.getString("ean"));

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
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	focodigo as id_fornecedor,\n"
                    + "	forazao as razao,\n"
                    + "	fofantasia as fantasia,\n"
                    + "	focgc as cnpj,\n"
                    + "	foinsest as ie,\n"
                    + "	foendereco as endereco,\n"
                    + "	fonumero as numero,\n"
                    + "	fobairro as bairro,\n"
                    + "	focidade as municipio,\n"
                    + "	foativo as ativo,\n"
                    + "	foestado as uf,\n"
                    + "	focep as cep,\n"
                    + "	fodatcad as data_cadastro,\n"
                    + "	foobservac as observacoes,\n"
                    + "	fofone1 \n"
                    + "from\n"
                    + "	focadfor "
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("id_fornecedor"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    imp.setIe_rg(rst.getString("ie"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setAtivo(true);

                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setDatacadastro(rst.getDate("data_cadastro"));
                    imp.setObservacao(rst.getString("observacoes"));

                    imp.setTel_principal(rst.getString("fofone1"));

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
                    "select\n"
                    + "	conumdoc as id, \n"
                    + "	coclient as id_fornecedor,\n"
                    + "	conumdoc as documento,\n"
                    + "	codtdcto as emissao,\n"
                    + "	codtvcto as vencimento,\n"
                    + "	covalor as valor,\n"
                    + "	cocomple as observacao\n"
                    + "from\n"
                    + "	cocadcta\n"
                    + "where\n"
                    + "	costatus <> 'PAG'"
            )) {
                while (rst.next()) {
                    ContaPagarIMP imp = new ContaPagarIMP();

                    imp.setId(rst.getString("id"));
                    imp.setIdFornecedor(rst.getString("id_fornecedor"));
                    imp.setNumeroDocumento(rst.getString("documento"));
                    imp.setDataEmissao(rst.getDate("emissao"));
                    imp.setDataEntrada(rst.getDate("emissao"));
                    imp.addVencimento(rst.getDate("vencimento"), rst.getDouble("valor"), rst.getString("observacao"));

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
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	clcodigo as id,\n"
                    + "	clrazao as razao,\n"
                    + "	clnome as fantasia,\n"
                    + "	clnumero as numero,\n"
                    + "	clendereco as endereco,\n"
                    + "	clbairro as bairro,\n"
                    + "	clcep as cep,\n"
                    + "	clcidade as cidade,\n"
                    + "	clativo as status,\n"
                    + "	cltel1 as contato,\n"
                    + "	clestado as uf,\n"
                    + "	clobs1 as email,\n"
                    + "clcredito as limite,\n"
                    + "	case when clcpf  = '' then clcgc else clcpf end as cpf_cnpj,\n"
                    + "	cltel1 as fone,\n"
                    + "	clinsest as inscricaoest\n"
                    + "	from\n"
                    + "	ccclient "
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setCep(rst.getString("cep"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setAtivo(true);
                    imp.setObservacao(rst.getString("contato"));
                    imp.setValorLimite(rst.getDouble("limite"));

                    imp.setUf(rst.getString("uf"));
                    imp.setEmail(rst.getString("email"));
                    imp.setCnpj(rst.getString("cpf_cnpj"));
                    imp.setInscricaoestadual(rst.getString("inscricaoest"));
                    imp.setTelefone(rst.getString("fone"));

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
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	conumdoc as id, \n"
                    + "	coclient as id_cliente,\n"
                    + "	conumdoc as nota,\n"
                    + "	codtdcto as data_venda,\n"
                    + "	codtvcto as vencimento,\n"
                    + "	covalor as valor,\n"
                    + "	codtpgto \n"
                    + "from\n"
                    + "	cocadrec\n"
                    + "	where costatus = 'ABE'"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rst.getString("id"));
                    imp.setIdCliente(rst.getString("id_cliente"));
                    imp.setNumeroCupom(rst.getString("nota"));
//imp.setParcela(rst.getInt("parcela") == 0 ? 1 : rst.getInt("parcela"));
                    imp.setDataEmissao(rst.getDate("data_venda"));
                    imp.setDataVencimento(rst.getDate("vencimento"));
                    imp.setValor(rst.getDouble("valor"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

}
