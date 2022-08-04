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
import vrframework.classe.ProgressBar;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.cadastro.oferta.TipoOfertaVO;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ClienteIMP;
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
 * @author Alan
 */
public class Orion_PostgresDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "Orion_Postgres";
    }

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
                OpcaoFornecedor.SITUACAO_CADASTRO));
    }

    @Override
    public Set<OpcaoCliente> getOpcoesDisponiveisCliente() {
        return new HashSet<>(Arrays.asList(
                OpcaoCliente.CONTATOS,
                OpcaoCliente.DADOS,
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
                    "select distinct\n"
                    + "	replace(sittribut,'','0')||'-'||icms||'-'||reducao id,\n"
                    + "	case\n"
                    + "	  when sittribut like '%00%' then 'TRIB '||icms||'%'\n"
                    + "	  when sittribut like '%10%' then 'SUBS'\n"
                    + "	  when sittribut like '%20%' then icms||' RED '||reducao||'%'\n"
                    + "	  when sittribut like '%40%' then 'ISENTO'\n"
                    + "	  when sittribut like '%41%' then 'NAO TRIBUTADO'\n"
                    + "	  when sittribut like '%51%' then 'DIFERIMENTO'\n"
                    + "	  when sittribut like '%60%' then 'SUBS'\n"
                    + "	  when sittribut like '%70%' then 'SUBS'\n"
                    + "	  when sittribut like '%90%' then 'OUTRAS'\n"
                    + "   else 'ISENTO'\n"
                    + "	end descricao,\n"
                    + "	cast (case \n"
                    + "	  when length(sittribut) = 3 then substring(sittribut from 2 for 3)\n"
                    + "	  when sittribut = '' then '0'\n"
                    + "	  else sittribut\n"
                    + "	end as int) cst,\n"
                    + "	icms aliq,\n"
                    + "	reducao\n"
                    + "from\n"
                    + "	estoque\n"
                    + "order by 1,2")) {
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
                    "select\n"
                    + "	distinct codsub as id,\n"
                    + "	titulograd as descricao\n"
                    + "from\n"
                    + "	ESTOQUE\n"
                    + "where\n"
                    + "	codsub is not null or trim(codsub) <> ''"
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
                    "select distinct \n"
                    + "	m1.codsetor cod_m1,\n"
                    + "	m1.setor desc_m1,\n"
                    + "	m2.codgrupo cod_m2,\n"
                    + "	m2.grupo desc_m2,\n"
                    + "	m3.codigo cod_m3,\n"
                    + "	m3.subgrupo desc_m3\n"
                    + "from\n"
                    + "	setor m1\n"
                    + "left join grupo m2 on m2.codsetor = m1.codsetor\n"
                    + "left join subgrupo m3 on m3.codgrupo = m2.codgrupo\n"
                    + "order by m1.codsetor, m2.codgrupo, m3.codigo"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setMerc1ID(rst.getString("cod_m1"));
                    imp.setMerc1Descricao(rst.getString("desc_m1"));
                    imp.setMerc2ID(rst.getString("cod_m2"));
                    imp.setMerc2Descricao(rst.getString("desc_m2"));
                    imp.setMerc3ID(rst.getString("cod_m3"));
                    imp.setMerc3Descricao(rst.getString("desc_m3"));

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
                    "select\n"
                    + "	e.plu id_produto,\n"
                    + "	case when l.codigo = 'SEM GTIN' then e.plu else l.codigo end ean,\n"
                    + "	e.codsetor mercadologico1,\n"
                    + "	e.codgru mercadologico2,\n"
                    + "	e.codsubgru mercadologico3,\n"
                    + "	e.nome descricaocompleta,\n"
                    + "	e.descricao descricaoreduzida,\n"
                    + "	e.gondola descricaogondola,\n"
                    + "	e.custo,\n"
                    + "	e.classfis ncm,\n"
                    + "	e.cest,\n"
                    + "	cast (case when e.sittribut = '' then '40' else e.sittribut end as int)sittribut,\n"
                    + "	e.icms,\n"
                    + "	e.reducao,\n"
                    + "	e.unidade,\n"
                    + "	e.inclusao,\n"
                    + "	e.piscst,\n"
                    + "	e.cofinscst,\n"
                    + "	e.vendavare,\n"
                    + "	e.lucrovare margem,\n"
                    + "	l.qtde,\n"
                    + "	e.quantfisc,\n"
                    + "	e.custobase,\n"
                    + "	e.gradeum,\n"
                    + "	e.gradedois,\n"
                    + "	e.codsub,\n"
                    + "	e.custobase custosemimposto,\n"
                    + "	e.custobase,\n"
                    + "	((e.custobase - e.descontos) + e.icmssubstr + e.encargos + e.frete + e.outrasdesp) as custocomimposto\n"
                    + "from\n"
                    + "	estoque e\n"
                    + "left join ligplu l on e.plu = l.plu\n"
                    + "where\n"
                    + "	e.plu is not null"
            )) {
                Map<Integer, vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO> produtosBalanca = new vrimplantacao.dao.cadastro.ProdutoBalancaDAO().carregarProdutosBalanca();
                int cont = 0;
                while (rst.next()) {

                    System.out.println(getLojaOrigem() + " - " + getSistema() + " - " + rst.getString("id_produto"));

                    ProdutoIMP imp = new ProdutoIMP();
                    vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO produtoBalanca;

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id_produto"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(rst.getString("descricaogondola"));
                    imp.setIdFamiliaProduto(rst.getString("codsub"));
                    imp.setCodMercadologico1(rst.getString("mercadologico1"));
                    imp.setCodMercadologico2(rst.getString("mercadologico2"));
                    imp.setCodMercadologico3(rst.getString("mercadologico3"));
                    imp.setTipoEmbalagem(rst.getString("unidade"));
                    imp.setTipoEmbalagemVolume(rst.getString("unidade"));
                    imp.setQtdEmbalagem(rst.getInt("qtde"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setPrecovenda(rst.getDouble("vendavare"));
                    imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setEstoque(rst.getDouble("quantfisc"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getString("piscst"));
                    imp.setPiscofinsCstCredito(rst.getString("cofinscst"));

                    imp.setIcmsCstSaida(rst.getInt("sittribut"));
                    imp.setIcmsAliqSaida(rst.getDouble("icms"));
                    imp.setIcmsReducaoSaida(rst.getDouble("reducao"));

                    imp.setIcmsCstConsumidor(imp.getIcmsCstSaida());
                    imp.setIcmsAliqConsumidor(imp.getIcmsAliqSaida());
                    imp.setIcmsReducaoConsumidor(imp.getIcmsReducaoSaida());

                    imp.setIcmsCstSaidaForaEstado(rst.getInt("sittribut"));
                    imp.setIcmsAliqSaidaForaEstado(rst.getDouble("icms"));
                    imp.setIcmsReducaoSaidaForaEstado(rst.getDouble("reducao"));

                    imp.setIcmsCstSaidaForaEstadoNF(rst.getInt("sittribut"));
                    imp.setIcmsAliqSaidaForaEstadoNF(rst.getDouble("icms"));
                    imp.setIcmsReducaoSaidaForaEstadoNF(rst.getDouble("reducao"));

                    imp.setIcmsCstEntrada(rst.getInt("sittribut"));
                    imp.setIcmsAliqEntrada(rst.getDouble("icms"));
                    imp.setIcmsReducaoEntrada(rst.getDouble("reducao"));

                    imp.setIcmsCstEntradaForaEstado(rst.getInt("sittribut"));
                    imp.setIcmsAliqEntradaForaEstado(rst.getDouble("icms"));
                    imp.setIcmsReducaoEntradaForaEstado(rst.getDouble("reducao"));

                    imp.setDataCadastro(rst.getDate("inclusao"));

                    long codigoProduto;
                    if ((rst.getString("ean") != null)
                            && (!rst.getString("ean").trim().isEmpty())) {

                        if (Long.parseLong(Utils.formataNumero(rst.getString("ean").trim())) <= 999999) {

                            codigoProduto = Long.parseLong(rst.getString("ean").trim());
                            if (codigoProduto <= Integer.MAX_VALUE) {
                                produtoBalanca = produtosBalanca.get((int) codigoProduto);
                            } else {
                                produtoBalanca = null;
                            }

                            if (produtoBalanca != null) {
                                imp.seteBalanca(true);
                                imp.setValidade(produtoBalanca.getValidade() > 1 ? produtoBalanca.getValidade() : 0);
                            } else {
                                imp.setValidade(0);
                                imp.seteBalanca(false);
                            }

                            imp.setEan(rst.getString("ean"));

                        } else {
                            imp.seteBalanca(false);
                            imp.setEan(rst.getString("ean"));
                        }
                    } else {
                        imp.seteBalanca(false);
                        imp.setEan(rst.getString("ean"));
                    }

                    result.add(imp);

                    cont++;
                    ProgressBar.setStatus("Carregando produtos..." + cont);
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
                    + "	e.plu id,\n"
                    + "	case\n"
                    + "	  when l.codigo = 'SEM GTIN' \n"
                    + "   then e.plu\n"
                    + "	  else l.codigo\n"
                    + "	end ean,\n"
                    + "	l.qtde quantidade\n"
                    + "from\n"
                    + "	estoque e\n"
                    + "left join ligplu l on e.plu = l.plu\n"
                    + "where\n"
                    + "	e.plu is not null"
            )) {
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rs.getString("id"));
                    imp.setEan(rs.getString("ean"));
                    imp.setQtdEmbalagem(rs.getInt("quantidade"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<OfertaIMP> getOfertas(Date dataTermino) throws Exception {
        List<OfertaIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	e.plu as idproduto,\n"
                    + "	e.proinivare as datainicio,\n"
                    + "	e.profimvare as datatermino,\n"
                    + "	e.vendavare as precovenda,\n"
                    + "	e.promovare as precooferta\n"
                    + "from\n"
                    + "	estoque e\n"
                    + "	where e.profimvare >= now() \n"
                    + "order by e.proinivare"
            )) {
                while (rst.next()) {
                    OfertaIMP imp = new OfertaIMP();
                    imp.setIdProduto(rst.getString("idproduto"));
                    imp.setDataInicio(rst.getDate("datainicio"));
                    imp.setDataFim(rst.getDate("datatermino"));
                    imp.setPrecoNormal(rst.getDouble("precovenda"));
                    imp.setPrecoOferta(rst.getDouble("precooferta"));
                    imp.setTipoOferta(TipoOfertaVO.CAPA);

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
                    + "	codigo,\n"
                    + "	nome,\n"
                    + "	razao,\n"
                    + "	inscest,\n"
                    + "	cgc,\n"
                    + "	rua,\n"
                    + "	casa,\n"
                    + "	edificio,\n"
                    + "	sala,\n"
                    + "	cidade,\n"
                    + "	bairro,\n"
                    + "	cep,\n"
                    + "	estado,\n"
                    + "	inclusao,\n"
                    + "	email,\n"
                    + "	contato,\n"
                    + "	contatcom,\n"
                    + "	telefone1,\n"
                    + "	telefone2,\n"
                    + "	telefone3,\n"
                    + "	obs\n"
                    + "from\n"
                    + "	FORNECE\n"
                    + "order by\n"
                    + "	codigo"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    if ((rst.getString("codigo") != null)
                            && (!rst.getString("codigo").trim().isEmpty())) {
                        imp.setImportId(rst.getString("codigo"));
                    } else {
                        imp.setImportId(rst.getString("cgc"));
                    }

                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("nome"));
                    imp.setCnpj_cpf(rst.getString("cgc"));
                    imp.setIe_rg(rst.getString("inscest"));
                    imp.setEndereco(rst.getString("rua"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("estado"));
                    imp.setCep(rst.getString("cep"));
                    imp.setComplemento(rst.getString("casa") + " " + rst.getString("edificio") + " " + rst.getString("sala"));
                    imp.setDatacadastro(rst.getDate("inclusao"));
                    imp.setObservacao(rst.getString("obs"));

                    if ((rst.getString("contato") != null)
                            && (!rst.getString("contato").trim().isEmpty())) {
                        imp.setObservacao(imp.getObservacao() + " CONTATO " + rst.getString("contato"));
                    }
                    if ((rst.getString("contatcom") != null)
                            && (!rst.getString("contatcom").trim().isEmpty())) {
                        imp.setObservacao(imp.getObservacao() + " CONTATOCOM " + rst.getString("contatcom"));
                    }

                    imp.setTel_principal(rst.getString("telefone1"));

                    if ((rst.getString("telefone2") != null)
                            && (!rst.getString("telefone2").trim().isEmpty())) {
                        imp.addContato(
                                "1",
                                "TELEFONE2",
                                rst.getString("telefone2"),
                                null,
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    if ((rst.getString("telefone3") != null)
                            && (!rst.getString("telefone3").trim().isEmpty())) {
                        imp.addContato(
                                "2",
                                "TELEFONE 3",
                                rst.getString("telefone3"),
                                null,
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    if ((rst.getString("email") != null)
                            && (!rst.getString("email").trim().isEmpty())) {
                        imp.addContato(
                                "3",
                                "EMAIL",
                                null,
                                null,
                                TipoContato.COMERCIAL,
                                rst.getString("email").toLowerCase()
                        );
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

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	CODFOR,\n"
                    + "	CODINT,\n"
                    + "	PLU,\n"
                    + "	QTDE\n"
                    + "from\n"
                    + "	LIGFAB"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setIdFornecedor(rst.getString("CODFOR"));
                    imp.setIdProduto(rst.getString("PLU"));
                    imp.setCodigoExterno(rst.getString("CODINT"));
                    imp.setQtdEmbalagem(rst.getInt("QTDE"));

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
                    + "	codigo,\n"
                    + "	nome,\n"
                    + "	razao,\n"
                    + "	nascimento,\n"
                    + "	inscest,\n"
                    + "	cgc,\n"
                    + "	cic,\n"
                    + "	firma,\n"
                    + "	cargo,\n"
                    + "	salario,\n"
                    + "	compramax,\n"
                    + "	pai,\n"
                    + "	mae,\n"
                    + "	rua,\n"
                    + "	casa,\n"
                    + "	edificio,\n"
                    + "	apto,\n"
                    + "	cidade,\n"
                    + "	bairro,\n"
                    + "	cep,\n"
                    + "	estado,\n"
                    + "	email,\n"
                    + "	abertura,\n"
                    + "	contato,\n"
                    + "	telefone1,\n"
                    + "	telefone2,\n"
                    + "	telefone3,\n"
                    + "	contatcom,\n"
                    + "	rg\n"
                    + "from\n"
                    + "	cliente"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("codigo"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("nome"));
                    imp.setDataNascimento(rst.getDate("nascimento"));
                    imp.setEmpresa(rst.getString("firma"));
                    imp.setCargo(rst.getString("cargo"));
                    imp.setSalario(rst.getDouble("salario"));
                    imp.setValorLimite(rst.getDouble("compramax"));
                    imp.setNomeMae(rst.getString("mae"));
                    imp.setNomePai(rst.getString("pai"));
                    imp.setEndereco(rst.getString("rua"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setCep(rst.getString("cep"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("estado"));
                    imp.setTelefone(rst.getString("telefone1"));
                    imp.setEmail(rst.getString("email"));

                    if ((rst.getString("contato") != null)
                            && (!rst.getString("contato").trim().isEmpty())) {
                        imp.setObservacao("CONTATO " + rst.getString("contato"));
                    }
                    if ((rst.getString("contatcom") != null)
                            && (!rst.getString("contatcom").trim().isEmpty())) {
                        imp.setObservacao(imp.getObservacao() + " CONTATOCOM " + rst.getString("contatcom"));
                    }

                    if ((rst.getString("cic") != null)
                            && (!rst.getString("cic").trim().isEmpty())) {
                        imp.setCnpj(rst.getString("cic"));
                    } else if ((rst.getString("cgc") != null)
                            && (!rst.getString("cgc").trim().isEmpty())) {
                        imp.setCnpj(rst.getString("cgc"));
                    } else {
                        imp.setCnpj("");
                    }

                    if ((rst.getString("rg") != null)
                            && (!rst.getString("rg").trim().isEmpty())) {
                        imp.setInscricaoestadual(rst.getString("rg"));
                    } else if ((rst.getString("inscest") != null)
                            && (!rst.getString("inscest").trim().isEmpty())) {
                        imp.setInscricaoestadual(rst.getString("inscest"));
                    } else {
                        imp.setInscricaoestadual("");
                    }

                    if ((rst.getString("telefone2") != null)
                            && (!rst.getString("telefone2").trim().isEmpty())) {
                        imp.addContato(
                                "1",
                                "TELEFONE 2",
                                rst.getString("telefone2"),
                                null,
                                null
                        );
                    }
                    if ((rst.getString("telefone3") != null)
                            && (!rst.getString("telefone3").trim().isEmpty())) {
                        imp.addContato(
                                "3",
                                "TELEFONE 3",
                                rst.getString("telefone3"),
                                null,
                                null
                        );
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

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	codigo,\n"
                    + "	vencimento,\n"
                    + "	dlanca,\n"
                    + "	valorreceb,\n"
                    + "	codigocli,\n"
                    + "	terminal\n"
                    + "from\n"
                    + "	receber\n"
                    + "where\n"
                    + "	pagamento is null\n"
                    + "	and codigocli is not null\n"
                    + "	and codigocli != ''"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rst.getString("codigo"));
                    imp.setIdCliente(rst.getString("codigocli"));
                    imp.setNumeroCupom(rst.getString("codigo"));
                    imp.setEcf(rst.getString("terminal"));
                    imp.setDataEmissao(rst.getDate("dlanca"));
                    imp.setDataVencimento(rst.getDate("vencimento"));
                    imp.setValor(rst.getDouble("valorreceb"));

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
        return new VendaIterator(dataInicioVenda, dataTerminoVenda);
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        return new VendaItemIterator(dataInicioVenda, dataTerminoVenda);
    }

    private static class VendaIterator implements Iterator<VendaIMP> {

        private final static SimpleDateFormat FORMAT = new SimpleDateFormat("dd/MM/yyyy");

        private Statement stm = ConexaoPostgres.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaIMP next;
        private Set<String> uk = new HashSet<>();

        private void obterNext() {
            try {
                SimpleDateFormat timestampDate = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat timestamp = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                if (next == null) {
                    if (rst.next()) {
                        next = new VendaIMP();
                        String i_id = rst.getString("id");
                        String i_numerocupom = rst.getString("numerocupom");
                        String i_ecf = rst.getString("ecf");
                        Date i_datavenda = rst.getDate("datavenda");

                        String id = i_id + i_ecf + String.valueOf(i_datavenda);
                        if (!uk.add(id)) {
                            LOG.warning("Venda " + id + " já existe na listagem");
                        }

                        next.setId(id);

                        next.setNumeroCupom(i_numerocupom == null
                                ? Utils.stringToInt(i_id)
                                : Utils.stringToInt(i_numerocupom));

                        next.setEcf(Utils.stringToInt(i_ecf));
                        next.setData(i_datavenda);
                        next.setIdClientePreferencial(rst.getString("idcliente"));

                        String horaInicio = timestampDate.format(i_datavenda) + " 00:00:00";
                        String horaTermino = timestampDate.format(i_datavenda) + " 00:00:00";

                        next.setCancelado("Cancelado".equals(rst.getString("status")));
                        next.setHoraInicio(timestamp.parse(horaInicio));
                        next.setHoraTermino(timestamp.parse(horaTermino));
                        next.setSubTotalImpressora(rst.getDouble("totalvenda"));
                        next.setValorDesconto(rst.getDouble("desconto"));
                        next.setValorAcrescimo(rst.getDouble("acrescimo"));
                        next.setNumeroSerie(rst.getString("seriesat"));
                        next.setChaveCfe(rst.getString("chavesat"));
                    }
                }
            } catch (SQLException | ParseException ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaIterator(Date dataInicio, Date dataTermino) throws Exception {
            this.sql
                    = "select distinct \n"
                    + "	v.codigo as id,\n"
                    + "	v.codcli as idcliente,\n"
                    + "	v.cupom as numerocupom,\n"
                    + "	v.terminal as ecf,\n"
                    + "	v.operador,\n"
                    + "	v.data as datavenda,\n"
                    + "	v.horainicio,\n"
                    + "	v.horafim,\n"
                    + "	v.estado as status,\n"
                    + "	v.desconto,\n"
                    + "	v.acrescimo,\n"
                    + "	v.total,\n"
                    + "	v.totalvenda,\n"
                    + "	v.chavesat,\n"
                    + "	v.chasatcanc,\n"
                    + "	v.seriesat,\n"
                    + "	v.numcfe\n"
                    + "from vendas v\n"
                    + "where v.data between '" + dataInicio + "' and '" + dataTermino + "'\n"
                    + "order by v.data";

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

        private Statement stm = ConexaoPostgres.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaItemIMP next;
        private String i_idvenda, i_ecf, i_datavenda, i_idproduto, i_sequencia;
        private Double i_qtdembalagem, i_aliquota;
        private Integer i_cst;

        private void obterNext() {
            try {
                if (next == null) {
                    if (rst.next()) {
                        next = new VendaItemIMP();

                        i_idvenda = rst.getString("idvenda");
                        i_ecf = rst.getString("ecf");
                        i_datavenda = rst.getString("datavenda");
                        i_idproduto = rst.getString("idproduto");
                        i_sequencia = rst.getString("sequencia");
                        i_qtdembalagem = rst.getDouble("qtdembalagem");
                        i_cst = rst.getInt("cst");
                        i_aliquota = rst.getDouble("aliquota");

                        String idVenda = i_idvenda + i_ecf + i_datavenda;
                        String id = i_idvenda
                                + i_ecf
                                + i_datavenda
                                + i_idproduto
                                + i_sequencia
                                + String.valueOf(i_qtdembalagem);

                        next.setId(id);
                        next.setVenda(idVenda);
                        next.setProduto(i_idproduto);
                        next.setSequencia(Integer.parseInt(i_sequencia));
                        next.setDescricaoReduzida(rst.getString("descricaoproduto"));
                        next.setQuantidade(i_qtdembalagem);
                        next.setTotalBruto(rst.getDouble("valortotal"));
                        next.setValorDesconto(rst.getDouble("desconto"));
                        next.setCancelado("Cancelado".equals(rst.getString("status")));
                        next.setCodigoBarras(rst.getString("codigobarras"));
                        next.setUnidadeMedida(rst.getString("tipoembalagem"));

                        String trib = "";

                        if (i_cst == 40) {
                            trib = "F";
                        } else if (i_cst == 41) {
                            trib = "N";
                        } else if (i_cst == 60) {
                            trib = "F";
                        } else if (i_cst == 0) {

                            if (i_aliquota == 7) {
                                trib = "0700";
                            } else if (i_aliquota == 11) {
                                trib = "1100";
                            } else if (i_aliquota == 4.5) {
                                trib = "0450";
                            } else if (i_aliquota == 12) {
                                trib = "1200";
                            } else if (i_aliquota == 18) {
                                trib = "1800";
                            } else if (i_aliquota == 25) {
                                trib = "2500";
                            } else if (i_aliquota == 27) {
                                trib = "2700";
                            } else if (i_aliquota == 17) {
                                trib = "1700";
                            } else {
                                trib = "0";
                            }
                        }

                        obterAliquota(next, trib);
                    }
                }
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public void obterAliquota(VendaItemIMP item, String icms) throws SQLException {

            int cst;
            double aliq;
            switch (icms) {
                case "0450":
                    cst = 0;
                    aliq = 4.5;
                    break;
                case "0700":
                    cst = 0;
                    aliq = 7;
                    break;
                case "1100":
                    cst = 0;
                    aliq = 11;
                    break;
                case "1200":
                    cst = 0;
                    aliq = 12;
                    break;
                case "1800":
                    cst = 0;
                    aliq = 18;
                    break;
                case "2500":
                    cst = 0;
                    aliq = 25;
                    break;
                case "F":
                    cst = 60;
                    aliq = 0;
                    break;
                case "N":
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

        public VendaItemIterator(Date dataInicio, Date dataTermino) throws Exception {
            this.sql
                    = "select distinct\n"
                    + "	i.codvenda as idvenda,\n"
                    + "	i.terminal as ecf,\n"
                    + "	i.item as sequencia,\n"
                    + "	i.codplu as idproduto,\n"
                    + "	i.codestoque as codigobarras,\n"
                    + "	i.descricao as descricaoproduto,\n"
                    + "	i.unidade as tipoembalagem,\n"
                    + "	i.quantpeso as qtdembalagem,\n"
                    + "	i.custo,\n"
                    + "	i.venda as precovenda,\n"
                    + "	i.desconto,\n"
                    + "	i.total as valortotal,\n"
                    + "	i.datavenda,\n"
                    + "	i.icms as aliquota,\n"
                    + "	i.sittribut as cst,\n"
                    + "	i.estado as status\n"
                    + "from detaven i\n"
                    + "where i.datavenda between '" + dataInicio + "' and '" + dataTermino + "'\n"
                    + "and i.codplu is not null\n"
                    + "order by i.codvenda, i.terminal, i.item";

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
