package vrimplantacao2_5.dao.sistema;

import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.Arrays;
import java.util.HashSet;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import vrimplantacao.utils.Utils;
import vrimplantacao2_5.dao.conexao.ConexaoSqlServer;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.cadastro.mercadologico.MercadologicoNivelIMP;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.ChequeIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;

/**
 *
 * @author Alan
 */
public class Jmaster2_5DAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "JMASTER";
    }

    public boolean apenasProdutoAtivo = false;

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.DATA_CADASTRO,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.ATIVO,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.ATUALIZAR_SOMAR_ESTOQUE,
                OpcaoProduto.CUSTO,
                OpcaoProduto.CUSTO_COM_IMPOSTO,
                OpcaoProduto.CUSTO_SEM_IMPOSTO,
                OpcaoProduto.MARGEM,
                OpcaoProduto.PRECO,
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
                OpcaoProduto.VENDA_PDV, // Libera produto para Venda no PDV
                OpcaoProduto.VOLUME_QTD
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
                OpcaoCliente.SITUACAO_CADASTRO,
                OpcaoCliente.RECEBER_CREDITOROTATIVO,
                OpcaoCliente.DATA_CADASTRO,
                OpcaoCliente.DATA_NASCIMENTO,
                OpcaoCliente.ENDERECO,
                OpcaoCliente.VALOR_LIMITE
        ));
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "with lj as (select LOJCODIGO id, LOJESTADO uf from CADLOJ l where l.LOJCODIGO = " + getLojaOrigem() + ")\n"
                    + "select distinct\n"
                    + "	'E' + aliq.NATCODIGO id,\n"
                    + "	aliq.NATDESCRICAO descricao,\n"
                    + "	aliq.NATMENSAGEM mensagem,\n"
                    + "	aliq.NATCST cst,\n"
                    + "	aliq.NATICMCOMPRA icms,\n"
                    + "	aliq.NATICMREDCMP reduzido\n"
                    + "from\n"
                    + "	cadnat aliq\n"
                    + "	join lj on\n"
                    + "		aliq.NATESTADO = lj.uf\n"
                    + "	join LOJITM li on\n"
                    + "		li.LITNATFISCAL = aliq.NATCODIGO\n"
                    + "where\n"
                    + "	NATTABNAT = 1\n"
                    + "union\n"
                    + "select distinct\n"
                    + "	'S' + aliq.NATCODIGO id,\n"
                    + "	aliq.NATDESCRICAO descricao,\n"
                    + "	aliq.NATMENSAGEM mensagem,\n"
                    + "	aliq.NATCST cst,\n"
                    + "	aliq.NATICM icms,\n"
                    + "	aliq.NATICMREDUZ reduzido\n"
                    + "from\n"
                    + "	cadnat aliq\n"
                    + "	join lj on\n"
                    + "		aliq.NATESTADO = lj.uf\n"
                    + "	join LOJITM li on\n"
                    + "		li.LITNATFISCAL = aliq.NATCODIGO\n"
                    + "where\n"
                    + "	aliq.NATTABNAT = 1\n"
                    + "order by\n"
                    + "	id"
            )) {
                while (rs.next()) {
                    String descricao = String.format(
                            "%s - %s",
                            rs.getString("descricao"),
                            rs.getString("mensagem")
                    );
                    int cst = Utils.stringToInt(rs.getString("cst"));
                    double aliq = rs.getDouble("icms");
                    double reduzido = rs.getDouble("reduzido");
                    result.add(new MapaTributoIMP(
                            rs.getString("id"),
                            descricao,
                            cst,
                            aliq,
                            reduzido
                    ));
                }
            }
        }

        return result;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT "
                    + "famcodigo, "
                    + "famdescricao "
                    + "FROM cadfam "
            )) {
                while (rst.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("famcodigo"));
                    imp.setDescricao(rst.getString("famdescricao"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<MercadologicoNivelIMP> getMercadologicoPorNivel() throws Exception {
        Map<String, MercadologicoNivelIMP> merc = new LinkedHashMap<>();
        try (Statement stm = vrimplantacao.classe.ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "secsecao, secdescri \n"
                    + "from cadsec \n"
                    + "where secsecao <> 0 "
                    + "and secgrupo = 0 "
                    + "and secsubgrupo = 0 "
                    + "order by secsecao"
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP imp = new MercadologicoNivelIMP();
                    imp.setId(rst.getString("secsecao"));
                    imp.setDescricao(rst.getString("secdescri"));
                    merc.put(imp.getId(), imp);
                }
            }

            try (ResultSet rst = stm.executeQuery(
                    "select secsecao, secgrupo, secdescri \n"
                    + "from cadsec \n"
                    + "where secsecao > 0 "
                    + "and secgrupo > 0 "
                    + "and secsubgrupo = 0 "
                    + "order by secsecao, secgrupo"
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP merc1 = merc.get(rst.getString("secsecao"));
                    if (merc1 != null) {
                        merc1.addFilho(
                                rst.getString("secgrupo"),
                                rst.getString("secdescri")
                        );
                    }
                }
            }

            try (ResultSet rst = stm.executeQuery(
                    "select secsecao, secgrupo, secsubgrupo, secdescri \n"
                    + "from cadsec \n"
                    + "where secsecao > 0 "
                    + "and secgrupo > 0 "
                    + "and secsubgrupo > 0 "
                    + "order by secsecao, secgrupo, secsubgrupo"
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP merc1 = merc.get(rst.getString("secsecao"));
                    if (merc1 != null) {
                        MercadologicoNivelIMP merc2 = merc1.getNiveis().get(rst.getString("secgrupo"));
                        if (merc2 != null) {
                            merc2.addFilho(
                                    rst.getString("secsubgrupo"),
                                    rst.getString("secdescri")
                            );
                        }
                    }
                }
            }
        }
        return new ArrayList<>(merc.values());
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "	select  \n"
                    + "	p.GERCODREDUZ id,\n"
                    + "	ean.EANCODIGO ean,\n"
                    + "	p.GERTIPVEN unidade\n"
                    + "	from\n"
                    + "	dbo.CADGER p\n"
                    + "	left join CADEAN ean on	p.GERCODREDUZ = ean.EANCODREDUZ"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("id"));
                    imp.setEan(rst.getString("ean"));
                    imp.setQtdEmbalagem(1);
                    imp.setTipoEmbalagem(rst.getString("unidade"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

   @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	p.GERCODREDUZ id,\n"
                    //+ "	CONVERT(DATE, CONVERT(VARCHAR, p.GERENTLIN)) datacadastro,\n"
                    + "	ean.EANCODIGO ean,\n"
                    + "	ean.EANQTDE qtdembalagem,\n"
                    + "	p.GERTIPVEN unidade,\n"
                    + "	p.gerembven qtdembalagemcotacao,\n"
                    + "	case \n"
                    + "	when p.GERPESOVARIAVEL  = 'S' then 1 else 0 end as e_balanca,\n"
                    + "	p.GERFRACAO,\n"
                    + "	p.GERVALIDADE validade,\n"
                    + "	p.GERDESCRICAO descricaocompleta,\n"
                    + "	p.GERDESCREDUZ descricaoreduzida,\n"
                    + "	case when p.GERVENDAPARC = 0 then 1 else p.GERVENDAPARC end parcelas,\n"
                    + "	p.GERSECAO merc1,\n"
                    + "	p.GERGRUPO merc2,\n"
                    + "	p.GERSUBGRUPO merc3,\n"
                    + "	p.GERFAMILIA id_familia,\n"
                    + "	p.GERPESOBRT pesobruto,\n"
                    + "	p.GERPESOLIQ pesoliquido,\n"
                    + "	est.LITESTQMIN estoqueminimo,\n"
                    + "	est.LITESTQD estoque,\n"
                    + "	est.LITMRGVEN1 margem,\n"
                    + "	est.LITCUSREP custocomimposto,\n"
                    + "	est.LITCUSREP custosemimposto,\n"
                    + "	est.LITCUSMED customedio,\n"
                    + "	est.LITPRCVEN1 precovenda,\n"
                    + "	p.GERTECLA teclassociada,\n"
                    + "	p.GERSAILIN saidadelinha,\n"
                    + " est.LITCUSMED customedio, \n"
                    + "	left (p.GERNBM,8) ncmtratado,\n"
                    + "	left(p.GERCEST,8) cesttratado,\n"
                    + "	p.GERNBM ncm,\n"
                    + "	p.GERCEST cest,\n"
                    + "	p.GERTIPOPIS piscofins_saida,\n"
                    + "	p.GERTIPOPIE piscofins_entrada,\n"
                    + "	'E' + est.LITNATFISCAL id_icms_entrada,\n"
                    + "	'S' + est.LITNATFISCAL id_icms_saida,\n"
                    + "	p.GERCODFOR fornecedorfabricante\n"
                    + "from\n"
                    + "	dbo.CADGER p\n"
                    + "	JOIN dbo.LOJITM est ON\n"
                    + "		p.GERCODREDUZ = est.LITCODREDUZ \n"
                    + "	JOIN dbo.LOJSEC ON\n"
                    + "		est.LITLOJA = dbo.LOJSEC.LSCLOJA AND \n"
                    + "		p.GERSECAO = dbo.LOJSEC.LSCSECAO\n"
                    + "	left join CADEAN ean on\n"
                    + "		p.GERCODREDUZ = ean.EANCODREDUZ\n"
                    + "where\n"
                    + "	est.LITLOJA = " + getLojaOrigem() + "\n"
                    + "order by id, ean"
            )) {
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().getProdutosBalanca();
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("id"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(rst.getString("descricaoreduzida"));
                    imp.setCodMercadologico1(rst.getString("merc1"));
                    imp.setCodMercadologico2(rst.getString("merc2"));
                    imp.setCodMercadologico3(rst.getString("merc3"));
                    imp.setTipoEmbalagem(rst.getString("unidade"));
                    imp.setTipoEmbalagemCotacao(rst.getString("unidade"));
                    imp.setCustoSemImposto(rst.getDouble("custocomimposto"));
                    imp.setCustoComImposto(rst.getDouble("custosemimposto"));
                    imp.setCustoMedioComImposto(rst.getDouble("customedio"));
                    imp.setCustoMedioSemImposto(rst.getDouble("customedio"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    if (rst.getString("ncm").length() > 8){
                        imp.setNcm(rst.getString("ncmtratado"));
                        imp.setCest(rst.getString("cesttratado"));
                    }
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setPiscofinsCstCredito(rst.getString("piscofins_saida"));
                    imp.setPiscofinsCstDebito(rst.getString("id_icms_saida"));

                    int codigoProduto = Utils.stringToInt(rst.getString("id"), -2);
                    ProdutoBalancaVO produtoBalanca = produtosBalanca.get(codigoProduto);

                    if (produtoBalanca != null) {
                        imp.setEan(String.valueOf(produtoBalanca.getCodigo()));
                        imp.seteBalanca(true);
                        imp.setTipoEmbalagem("U".equals(produtoBalanca.getPesavel()) ? "UN" : "KG");
                        imp.setValidade(produtoBalanca.getValidade());
                        imp.setQtdEmbalagem(1);
                    } else {
                        imp.setEan(rst.getString("ean"));
                        imp.seteBalanca(false);
                        imp.setValidade(0);
                        imp.setQtdEmbalagem(1);
                    }

                    imp.setIcmsDebitoId(rst.getString("id_icms_entrada"));
//                    imp.setPiscofinsNaturezaReceita(rst.getString("receita"));
                    imp.setIcmsDebitoForaEstadoId(rst.getString("id_icms_entrada"));
                    imp.setIcmsDebitoForaEstadoNfId(rst.getString("id_icms_saida"));
                    imp.setIcmsCreditoId(rst.getString("id_icms_saida"));
                    imp.setIcmsCreditoForaEstadoId(rst.getString("id_icms_saida"));
                    imp.setIcmsConsumidorId(rst.getString("id_icms_saida"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

//    @Override
//    public List<OfertaIMP> getOfertas(Date dataTermino) throws Exception {
//        List<OfertaIMP> result = new ArrayList<>();
//        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
//            try (ResultSet rst = stm.executeQuery(
//                    "select \n"
//                    + "cap.jocdataini, cap.jocdatafim, cap.jocobserv,\n"
//                    + "det.JODCODREDUZ, pro.GERDESCRICAO, det.JODPRCVEN, pro.litprcven1,\n"
//                    + "det.JODOBSERV\n"
//                    + "from JORCAP cap\n"
//                    + "inner join JORLOJ loj on loj.JOLNUMERO = cap.JOCNUMERO and loj.JOLLOJA = \n"
//                    + "inner join JORDET det on det.JODNUMERO = cap.JOCNUMERO\n"
//                    + "inner join VPRODLOJA pro on pro.GERCODREDUZ = det.JODCODREDUZ "
//
//            )) {
//                while (rst.next()) {
//                    OfertaIMP imp = new OfertaIMP();
//
//                    imp.setIdProduto(rst.getString("Produto"));
//                    imp.setDataInicio(rst.getDate("Validade_Inicial"));
//                    imp.setDataFim(rst.getDate("Validade_Final"));
//                    imp.setPrecoOferta(rst.getDouble("Preco_Promocao"));
//                    imp.setSituacaoOferta(SituacaoOferta.ATIVO);
//                    imp.setTipoOferta(TipoOfertaVO.CAPA);
//
//                    result.add(imp);
//                }
//            }
//        }
//        return result;
//    }
    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	f.forcodigo id,\n"
                    + "	f.FORRAZAO razao,\n"
                    + "	f.FORDESCRI nome,\n"
                    + "	f.FORCGC cnpj,\n"
                    + "	f.FORINSC ie_rg,\n"
                    + "	f.FORDTFLINHA dataforalinha,\n"
                    + "	f.FORENDERECO endereco,\n"
                    + "	f.FORNUMERO numero,\n"
                    + "	f.FORCOMPL complemento,\n"
                    + "	f.FORBAIRRO bairro,\n"
                    + "	f.FORCIDADE cidade,\n"
                    + "	f.FORESTADO uf,\n"
                    + "	f.FORCEP cep,\n"
                    + "	f.FORDDD ddd,\n"
                    + "	f.FORTELEFONE telefone,\n"
                    + "	CASE \n"
                    + "    WHEN f.FORDTCAD = 0 THEN null\n"
                    + "    ELSE CAST(CONVERT(VARCHAR, f.FORDTCAD) AS DATE)\n"
                    + "	END AS datacadastro,\n"
                    + "	f.FORENTREGA prazoentrega,\n"
                    + "	f.FORPRAZO prazopedido,\n"
                    + "	f.FORFAX fax,\n"
                    + "	f.FOREMAIL email,\n"
                    + "	coalesce(rtrim(ltrim(f.FORCONTATO)), '') contato,\n"
                    + "	f.FORDESCISS ,\n"
                    + "	f.forbanco banco,\n"
                    + "	f.FORAGENCIA agencia,\n"
                    + "	f.FORCONTA conta\n"
                    + "from\n"
                    + "	CADFOR f\n"
                    + "order by\n"
                    + "	forcodigo"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("id"));
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    imp.setIe_rg(rst.getString("ie_rg"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("razao"));

                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("complemento"));
                    imp.setCep(rst.getString("cep"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("uf"));
                    imp.setTel_principal(rst.getString("ddd") + rst.getString("telefone"));
                    imp.setDatacadastro(rst.getDate("datacadastro"));
                    imp.setPrazoEntrega(rst.getInt("prazoentrega"));
                    imp.setPrazoPedido(rst.getInt("prazopedido"));
                    imp.setTel_principal(rst.getString("telefone"));

                    if ((rst.getString("contato") != null)
                            && (!rst.getString("contato").trim().isEmpty())) {
                        imp.addContato(
                                "1",
                                "TELEFONE 2",
                                rst.getString("contato"),
                                null,
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    if ((rst.getString("fax") != null)
                            && (!rst.getString("fax").trim().isEmpty())) {
                        imp.addContato(
                                "2",
                                "FAX",
                                rst.getString("Fax"),
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
                    imp.setDatacadastro(rst.getDate("datacadastro"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "fitcodreduz, fitcodfor, fitreferencia, \n"
                    + "fitembfor, fittipfor \n"
                    + "from foritm "
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setIdFornecedor(rst.getString("fitcodreduz"));
                    imp.setIdProduto(rst.getString("fitcodreduz"));
                    imp.setCodigoExterno(rst.getString("fitreferencia"));
                    //imp.setQtdEmbalagem(rst.getDouble(""));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	c.CLICLIENTE id,\n"
                    + "	c.CLICGC cnpj,\n"
                    + "	c.CLIINSEST ie,\n"
                    + "	c.CLIRAZAO razao,\n"
                    + "	c.CLIFANTASIA fantasia,\n"
                    + "	case when c.CLISITUACAO = 'I' then 0 else 1 end ativo,\n"
                    + "	c.CLIENDERECO endereco,\n"
                    + "	c.CLINUMERO numero,\n"
                    + "	c.CLIBAIRRO bairro,\n"
                    + "	c.CLICIDADE cidade,\n"
                    + "	c.CLIESTADO uf,\n"
                    + "	c.CLICEP cep,\n"
                    + "	c.CLIESTCIVIL estadocivil,\n"
                    + "	CASE \n"
                    + "    WHEN c.CLIDTCADAS = 0 THEN CAST(CURRENT_TIMESTAMP AS DATE)\n"
                    + "    ELSE CAST(CONVERT(VARCHAR, c.CLIDTCADAS) AS DATE)\n"
                    + "	END AS dataaniversario,\n"
                    + "	CONVERT(DATE, CONVERT(VARCHAR, c.CLIDTCADAS)) datacadastro,\n"
                    + "	c.CLISEXO sexo,\n"
                    + "	c.CLIEMPRESA empresa,\n"
                    + "	c.CLIDDD ddd,\n"
                    + "	c.CLITELEFONE telefone,\n"
                    + "	c.CLIDDDCOM dddcomercial,\n"
                    + "	c.CLITELEFONECOM telefonecomercial,\n"
                    + "	c.CLIDDDCEL dddcelular,\n"
                    + "	c.CLINROCEL celular,\n"
                    + "	c.CLICARGO cargo,\n"
                    + "	c.CLIRENDAM salario,\n"
                    + "	c.CLILIMITE limite,\n"
                    + "	c.CLICPFCONJUGE cpfconjuge,\n"
                    + "	c.CLIPAI pai,\n"
                    + "	c.CLIMAE mae,\n"
                    + "	c.CLIOBSERV1,\n"
                    + "	c.CLIOBSERV2,\n"
                    + "	c.CLIOBSERV3,	\n"
                    + "	c.CLIOBSERVACAO observacao,\n"
                    + "	c.CLIDIAPGTO diavencimento,\n"
                    + "	c.CLIULTDIA prazopagamento,\n"
                    + "	c.CLIEMAIL email,\n"
                    + "	c.CLIFAX fax\n"
                    + "from\n"
                    + "	cadcli c\n"
                    + "order by\n"
                    + "	c.clicliente"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setUf(rst.getString("uf"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setCep(rst.getString("cep"));
                    imp.setTelefone(Utils.formataNumero(rst.getString("telefone")));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setInscricaoestadual(rst.getString("ie"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setEmail(rst.getString("email") == null ? "" : rst.getString("email").toLowerCase());
                    imp.setValorLimite(rst.getDouble("limite"));

                    imp.setObservacao(rst.getString("CLIOBSERV1"));
                    imp.setCargo(rst.getString("cargo"));

                    if ((rst.getString("telefonecomercial") != null)
                            && (!rst.getString("telefonecomercial").trim().isEmpty())) {
                        imp.addContato(
                                "1",
                                "TELEFONE 2",
                                rst.getString("dddcomercial") + rst.getString("telefonecomercial"),
                                null,
                                null
                        );
                    }
                    if ((rst.getString("Fax") != null)
                            && (!rst.getString("Fax").trim().isEmpty())) {
                        imp.addContato(
                                "2",
                                "FAX",
                                rst.getString("Fax"),
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
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	CONVERT(DATE, CONVERT(VARCHAR, DTTEMISSAO)) emissao,\n"
                    + "	CONVERT(DATE, CONVERT(VARCHAR, DTTVENCTO)) vencimento,\n"
                    + "	DTTNOTA,\n"
                    + "	DTTPARCELA,\n"
                    + "	DTTCLIENTE,\n"
                    + "	DTTVLRTIT,\n"
                    + "	DTTOBSERVACAO,\n"
                    + "	DTTPDV\n"
                    + "from\n"
                    + "	DETTIT\n"
                    + "where\n"
                    + "	DTTVLRPAGO = 0"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();

                    imp.setId(rst.getString("DTTPARCELA") + getSistema() + "-" + getLojaOrigem() + "-"
                            + rst.getString("DTTCLIENTE") + "-" + rst.getString("DTTNOTA"));
                    imp.setIdCliente(rst.getString("DTTCLIENTE"));
                    imp.setNumeroCupom(rst.getString("DTTNOTA"));
                    imp.setParcela(rst.getInt("DTTPARCELA"));
                    imp.setDataEmissao(rst.getDate("emissao"));
                    imp.setDataVencimento(rst.getDate("vencimento"));
                    imp.setValor(rst.getDouble("DTTVLRTIT"));
                    imp.setObservacao(rst.getString("DTTOBSERVACAO"));
                    imp.setEcf(rst.getString("DTTPDV"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ChequeIMP> getCheques() throws Exception {
        List<ChequeIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	c.CHRCGCCPF cpf,\n"
                    + "	c.CHRBANCO banco,\n"
                    + "	c.CHRAGENCIA agencia,\n"
                    + "	c.CHRCONTA conta,\n"
                    + "	c.CHRCHEQUE numerocheque,\n"
                    + "	c.CHREMISSAO dataemissao,\n"
                    + "	c.CHRVENCTO datadeposito,\n"
                    + "	c.CHRPDV pdv,\n"
                    + "	c.CHRINSCRG rg,\n"
                    + "	c.CHRDDD ddd,\n"
                    + "	c.CHRTELEFONE telefone,\n"
                    + "	c.CHRRAZAO nome,\n"
                    + "	c.CHROBSERV1,\n"
                    + "	c.CHROBSERV2,\n"
                    + "	c.CHRJURODIA juros,\n"
                    + "	c.CHRVALOR valor,\n"
                    + "	case when c.CHRVLRPAGO >= c.CHRVALOR then 1 else 0 end pago\n"
                    + "from\n"
                    + "	CHQREC c\n"
                    + "where\n"
                    + "	c.CHRLOJA = 1\n"
                    + "order by\n"
                    + "	chremissao"
            )) {
                while (rst.next()) {
                    ChequeIMP imp = new ChequeIMP();

                    imp.setId(rst.getString(String.format(
                            "%s-%s-%s-%s-%s",
                            rst.getString("cpf"),
                            rst.getString("banco"),
                            rst.getString("agencia"),
                            rst.getString("conta"),
                            rst.getString("numerocheque"))));

                    imp.setNome(rst.getString("nome"));
                    imp.setCpf(rst.getString("cpf"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setBanco(rst.getInt("banco"));
                    imp.setAgencia(rst.getString("agencia"));
                    imp.setConta(rst.getString("conta"));
                    imp.setNumeroCupom(rst.getString("valor"));
                    imp.setDataHoraAlteracao(rst.getTimestamp("dataemissao"));
                    imp.setAlinea(0);

                    result.add(imp);
                }
            }
        }
        return result;
    }

}