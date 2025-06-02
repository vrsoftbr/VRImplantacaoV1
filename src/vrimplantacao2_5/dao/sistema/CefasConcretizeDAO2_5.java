package vrimplantacao2_5.dao.sistema;

import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
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
import vrimplantacao2.dao.cadastro.produto2.associado.OpcaoAssociado;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.AssociadoIMP;
import vrimplantacao2.vo.importacao.ChequeIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.ReceitaIMP;
import vrimplantacao2_5.dao.conexao.ConexaoOracle;

/**
 *
 * @author2_5 Bruno
 */
public class CefasConcretizeDAO2_5 extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "CEFAS";
    }

    public boolean vBalanca = false;
    public String vPlanoContas;

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.ASSOCIADO,
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
                OpcaoProduto.CODIGO_BENEFICIO,
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
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
                OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                OpcaoProduto.FABRICANTE,
                OpcaoProduto.NUTRICIONAL,
                OpcaoProduto.RECEITA,
                OpcaoProduto.RECEITA_BALANCA,
                OpcaoProduto.PAUTA_FISCAL,
                OpcaoProduto.PAUTA_FISCAL_PRODUTO,
                OpcaoProduto.TIPO_PRODUTO
        ));
    }

    @Override
    public Set<OpcaoFornecedor> getOpcoesDisponiveisFornecedor() {
        return new HashSet<>(Arrays.asList(
                OpcaoFornecedor.DADOS,
                OpcaoFornecedor.ENDERECO,
                OpcaoFornecedor.CONTATOS,
                OpcaoFornecedor.SITUACAO_CADASTRO,
                OpcaoFornecedor.TIPO_EMPRESA,
                OpcaoFornecedor.PAGAR_FORNECEDOR,
                OpcaoFornecedor.PRODUTO_FORNECEDOR,
                OpcaoFornecedor.CONDICAO_PAGAMENTO,
                OpcaoFornecedor.OBSERVACAO,
                OpcaoFornecedor.PRAZO_FORNECEDOR
        ));
    }

    @Override
    public Set<OpcaoCliente> getOpcoesDisponiveisCliente() {
        return new HashSet<>(Arrays.asList(
                OpcaoCliente.DADOS,
                OpcaoCliente.ENDERECO,
                OpcaoCliente.ESTADO_CIVIL,
                OpcaoCliente.CONTATOS,
                OpcaoCliente.CLIENTE_EVENTUAL,
                OpcaoCliente.CONVENIO_EMPRESA,
                OpcaoCliente.CONVENIO_CONVENIADO,
                OpcaoCliente.CONVENIO_TRANSACAO,
                OpcaoCliente.DATA_CADASTRO,
                OpcaoCliente.DATA_NASCIMENTO,
                OpcaoCliente.VALOR_LIMITE,
                OpcaoCliente.VENCIMENTO_ROTATIVO,
                OpcaoCliente.RECEBER_CREDITOROTATIVO,
                OpcaoCliente.OUTRAS_RECEITAS));
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT \n"
                    + "	CODTRIBUT id,\n"
                    + "	obs descricao,\n"
                    + "	ALIQICMS icms, \n"
                    + "	SITTRIBUT cst,\n"
                    + "	PERBASERED reducao\n"
                    + "FROM 	\n"
                    + "	tributacao\n"
                    + "ORDER BY \n"
                    + "	1")) {
                while (rs.next()) {
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
        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "    d.codepto merc1,\n"
                    + "    d.departamento descmerc1,\n"
                    + "    coalesce(cast(s.codsec as integer), 1) merc2,\n"
                    + "    s.secao descmerc2,\n"
                    + "    coalesce(cast(c.codcateg as integer), 1) merc3,\n"
                    + "    decode(c.categoria, '', s.secao, c.categoria) descmerc3,\n"
                    + "    coalesce(cast(sc.codsubcateg as integer), 1) merc4,\n"
                    + "    decode(sc.subcategoria, '', decode(c.categoria, '', s.secao, c.categoria), sc.subcategoria) descmerc4\n"
                    + "from\n"
                    + "    depto d\n"
                    + "left join\n"
                    + "    secao s on s.codepto = d.codepto\n"
                    + "left join\n"
                    + "    categ c on c.codsec = s.codsec and\n"
                    + "    c.codepto = d.codepto\n"
                    + "left join\n"
                    + "    subcateg sc on sc.codcateg = c.codcateg and\n"
                    + "    sc.codsec = s.codsec and\n"
                    + "    sc.codepto = d.codepto\n"
                    + "order by\n"
                    + "    d.departamento, s.secao, c.categoria, sc.subcategoria")) {
                while (rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setMerc1ID(rs.getString("merc1"));
                    imp.setMerc1Descricao(rs.getString("descmerc1"));
                    imp.setMerc2ID(rs.getString("merc2"));
                    imp.setMerc2Descricao(rs.getString("descmerc2"));
                    imp.setMerc3ID(rs.getString("merc3"));
                    imp.setMerc3Descricao(rs.getString("descmerc3"));
                    imp.setMerc4ID(rs.getString("merc4"));
                    imp.setMerc4Descricao(rs.getString("descmerc4"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "    p.codprod id,\n"
                    + "    p.descricao descricaocompleta,\n"
                    + "    p.abreviacao descricaoreduzida,\n"
                    + "    p.embalagem,\n"
                    + "    p.codbarra codigobarras,\n"
                    + "    p.codepto merc1,\n"
                    + "    decode(p.codsec, '', '1', p.codsec) merc2,\n"
                    + "    decode(p.codcat, '', '1', p.codcat) merc3,\n"
                    + "    decode(p.codsubcat, '', '1', p.codsubcat) merc4,\n"
                    + "    p.dtcadastro,\n"
                    + "    p.unidade,\n"
                    + "    p.qtunitcx qtdcaixa,\n"
                    + "    p.qtunit qtdunidade,\n"
                    + "    p.peso,\n"
                    + "    p.clafiscal ncm,\n"
                    + "    p.prazovalid validade,\n"
                    + "    e.custoreal custo,\n"
                    + "    e.PTABELAULTENT custocomimposto,\n"
                    + "    em.margem,\n"
                    + "    pre.pvenda venda,\n"
                    + "    em.PVENDA precovenda,\n"
                    + "    pisentrada.cstpis pisentrada,\n"
                    + "    pissaida.cstpis pissaida,\n"
                    + "    pissaida.cest,\n"
                    + "    pissaida.codnatpis naturezareceita,\n"
                    + "    t.codtribut idaliquota,\n"
                    + "    t.aliqicms icmsdebito,\n"
                    + "    t.sittribut cst,\n"
                    + "    t.perbasered redicms,\n"
                    + "    e.qtest estoque,\n"
                    + "    e.qtestmin estoqueminimo,\n"
                    + "    dtexclusao excluido, --campo null nao excluido, campo not null excluido,\n"
                    + "     t.CBENEF, \n"
                    + "    p.codfornecprinc fornprincipal\n"
                    + "from\n"
                    + "    produto p \n"
                    + "left join\n"
                    + "    preco pre on pre.codprod = p.codprod\n"
                    + "join\n"
                    + "    tributacao t on t.codtribut = pre.codtribut\n"
                    + "left join\n"
                    + "    embalagem em on em.codprod = p.codprod and\n"
                    + "    em.codbarra = p.codbarra\n"
                    + "left join\n"
                    + "    estoque e on e.codprod = p.codprod\n"
                    + "left join\n"
                    + "    (select\n"
                    + "        pis.cstpis,\n"
                    + "        pis.cest,\n"
                    + "        pis.codprod\n"
                    + "    from\n"
                    + "        cadncmpiscofins pis\n"
                    + "    where\n"
                    + "        pis.operacao = 'E') pisentrada on p.codprod = pisentrada.codprod\n"
                    + "left join\n"
                    + "    (select\n"
                    + "        pis.cstpis,\n"
                    + "        pis.cest,\n"
                    + "        pis.codprod,\n"
                    + "        pis.codnatpis\n"
                    + "    from\n"
                    + "        cadncmpiscofins pis\n"
                    + "    where\n"
                    + "        pis.operacao = 'S') pissaida on p.codprod = pissaida.codprod\n"
                    + "where \n"
                    + "    pre.numregiao = 1 and\n"
                    + "    em.codfilial = " + getLojaOrigem() + " \n"
                    + "order by\n"
                    + "    p.codprod")) {

                while (rs.next()) {
                    Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().getProdutosBalanca();
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("id"));
                    imp.setDescricaoCompleta(rs.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rs.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(rs.getString("descricaocompleta"));
                    imp.setTipoEmbalagem(rs.getString("embalagem"));
                    imp.setEan(rs.getString("codigobarras"));
                    imp.setCodMercadologico1(rs.getString("merc1"));
                    imp.setCodMercadologico2(rs.getString("merc2"));
                    imp.setCodMercadologico3(rs.getString("merc3"));
                    imp.setCodMercadologico4(rs.getString("merc4"));
                    imp.setDataCadastro(rs.getDate("dtcadastro"));
                    imp.setQtdEmbalagem(rs.getInt("qtdunidade"));
                    imp.setPesoLiquido(rs.getDouble("peso"));
                    imp.setNcm(rs.getString("ncm"));
                    imp.setValidade(rs.getInt("validade"));
                    imp.setCustoComImposto(rs.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(rs.getDouble("custo"));
                    imp.setMargem(rs.getDouble("margem"));
                    imp.setPrecovenda(rs.getDouble("precovenda"));
                    imp.setCest(rs.getString("cest"));
                    imp.setPiscofinsCstCredito(rs.getString("pisentrada"));
                    imp.setPiscofinsCstDebito(rs.getString("pissaida"));
                    imp.setPiscofinsNaturezaReceita(rs.getString("naturezareceita"));
                    imp.setBeneficio(rs.getString("CBENEF"));

                    imp.setIcmsDebitoId(rs.getString("idaliquota"));
                    imp.setIcmsCreditoId(imp.getIcmsDebitoId());
                    imp.setIcmsCreditoForaEstadoId(imp.getIcmsDebitoId());
                    imp.setIcmsDebitoForaEstadoId(imp.getIcmsDebitoId());
                    imp.setIcmsDebitoForaEstadoNfId(imp.getIcmsDebitoId());
                    imp.setIcmsConsumidorId(imp.getIcmsDebitoId());

                    imp.setEstoque(rs.getDouble("estoque"));
                    imp.setEstoqueMinimo(rs.getDouble("estoqueminimo"));
                    if (rs.getDate("excluido") == null) {
                        imp.setSituacaoCadastro(SituacaoCadastro.ATIVO);
                    } else {
                        imp.setSituacaoCadastro(SituacaoCadastro.EXCLUIDO);
                    }
                    if ((rs.getString("codigobarras") != null) && (rs.getString("codigobarras").length() <= 6)) {
                        if (vBalanca) {
                            ProdutoBalancaVO produtoBalanca;
                            long codigoProduto;
                            codigoProduto = Long.parseLong(imp.getEan().trim());
                            if (codigoProduto <= Integer.MAX_VALUE) {
                                produtoBalanca = produtosBalanca.get((int) codigoProduto);
                            } else {
                                produtoBalanca = null;
                            }
                            if (produtoBalanca != null) {
                                imp.seteBalanca(true);
                                imp.setValidade(produtoBalanca.getValidade() > 1 ? produtoBalanca.getValidade() : rs.getInt("validade"));
                            } else {
                                imp.setValidade(0);
                                imp.seteBalanca(false);
                            }
                        } else {
                            imp.setValidade(rs.getInt("validade"));
                        }
                    }
                    imp.setFornecedorFabricante(rs.getString("fornprincipal"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<AssociadoIMP> getAssociados(Set<OpcaoAssociado> opt) throws Exception {
        List<AssociadoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    ""
            )) {
                while (rs.next()) {
                    AssociadoIMP imp = new AssociadoIMP();
                    imp.setImpIdProduto(rs.getString(""));
                    imp.setQtdEmbalagem(rs.getInt(""));
                    imp.setImpIdProdutoItem(rs.getString(""));
                    imp.setQtdEmbalagemItem(rs.getInt(""));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ContaPagarIMP> getContasPagar() throws Exception {
        List<ContaPagarIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "	SELECT\n"
                    + "	NUMLANC AS id,\n"
                    + "	CODFORNEC AS id_fornecedor,\n"
                    + "	NUMNOTA AS numerodocumento,\n"
                    + "	DTEMISSAO AS dataemissao,\n"
                    + "	DTLANC AS dataentrada,\n"
                    + "	DTULTALTER AS dataalteracao,\n"
                    + "	VALOR AS valor,\n"
                    + "	OBS AS observacao,\n"
                    + "	DTVENC AS vencimento,\n"
                    + "	PREST AS parcela\n"
                    + "FROM\n"
                    + "	CPAGAR\n"
                    + "WHERE DTPAGO IS NULL"
            )) {
                while (rst.next()) {
                    ContaPagarIMP imp = new ContaPagarIMP();

                    imp.setId(rst.getString("id"));
                    imp.setIdFornecedor(rst.getString("id_fornecedor"));
                    imp.setNumeroDocumento(rst.getString("numerodocumento"));
                    imp.setDataEmissao(rst.getDate("dataemissao"));
                    imp.setDataEntrada(rst.getDate("dataentrada"));
                    imp.setVencimento(rst.getDate("vencimento"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setDataHoraAlteracao(rst.getTimestamp("dataalteracao"));
                    imp.setObservacao(rst.getString("observacao"));
                    
                    imp.addVencimento(rst.getDate("vencimento"), rst.getDouble("valor"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ReceitaIMP> getReceitas() throws Exception {
        List<ReceitaIMP> result = new ArrayList<>();
        try (Statement stm = vrimplantacao.classe.ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    ""
            )) {
                while (rs.next()) {
                    ReceitaIMP imp = new ReceitaIMP();

                    double qtdEmbUtilizado = 0;
                    qtdEmbUtilizado = rs.getDouble("");

                    imp.setImportsistema(getSistema());
                    imp.setImportloja(getLojaOrigem());
                    imp.setImportid(rs.getString(""));
                    imp.setIdproduto(rs.getString(""));
                    imp.setDescricao(rs.getString(""));
                    imp.setRendimento(rs.getDouble(""));
                    imp.setQtdembalagemreceita((int) qtdEmbUtilizado);
                    imp.setQtdembalagemproduto(rs.getInt(""));
                    imp.setFator(1);
                    imp.getProdutos().add(rs.getString(""));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "    codfornec id,\n"
                    + "    fornecedor razaosocial,\n"
                    + "    fantasia,\n"
                    + "    cpfcnpj,\n"
                    + "    ie,\n"
                    + "    endereco,\n"
                    + "    bairro,\n"
                    + "    cidade,\n"
                    + "    codmunicipio,\n"
                    + "    estado,\n"
                    + "    cep,\n"
                    + "    telefone,\n"
                    + "    fax,\n"
                    + "    email,\n"
                    + "    email2,\n"
                    + "    contato,\n"
                    + "    dtcadastro,\n"
                    + "    bloqueio,\n"
                    + "    obs,\n"
                    + "    prazoent,\n"
                    + "    repres,\n"
                    + "    telefone2,\n"
                    + "    endercob,\n"
                    + "    bairrocob,\n"
                    + "    cidadecob,\n"
                    + "    estcob,\n"
                    + "    cepcob,\n"
                    + "    telcob,\n"
                    + "    prazo1,\n"
                    + "    prazo2,\n"
                    + "    prazo3\n"
                    + "from\n"
                    + "    fornecedor\n"
                    + "order by\n"
                    + "    codfornec"
            )) {
                while (rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("id"));
                    imp.setRazao(rs.getString("razaosocial"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setCnpj_cpf(rs.getString("cpfcnpj"));
                    imp.setIe_rg(rs.getString("ie"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setIbge_municipio(rs.getInt("codmunicipio"));
                    imp.setUf(rs.getString("estado"));
                    imp.setCep(rs.getString("cep"));
                    imp.setTel_principal(rs.getString("telefone"));
                    if ((rs.getString("fax") != null) && (!"".equals(rs.getString("fax")))) {
                        imp.addContato("1", "FAX", rs.getString("fax"), null, TipoContato.FINANCEIRO, null);
                    }
                    if ((rs.getString("email") != null) && (!"".equals(rs.getString("email")))) {
                        imp.addContato("2", "EMAIL", null, null, TipoContato.FINANCEIRO, rs.getString("email"));
                    }
                    if ((rs.getString("telefone2") != null) && (!"".equals(rs.getString("telefone2")))) {
                        imp.addContato("3", "TELEFONE2", rs.getString("telefone2"), null, TipoContato.COMERCIAL, null);
                    }
                    if ((rs.getString("email2") != null) && (!"".equals(rs.getString("email2")))) {
                        imp.addContato("2", "EMAIL2", null, null, TipoContato.FINANCEIRO, rs.getString("email2"));
                    }
                    imp.setDatacadastro(rs.getDate("dtcadastro"));
                    imp.setAtivo("S".equals(rs.getString("bloqueio")) ? false : true);
                    if ((rs.getString("obs") != null) && (!"".equals(rs.getString("obs")))) {
                        imp.setObservacao(rs.getString("obs"));
                    }

                    imp.setPrazoEntrega(rs.getInt("prazoent"));
                    if ((rs.getString("repres") != null) && (!"".equals(rs.getString("repres")))) {
                        imp.addContato("REPRESENTANTE", null, null, TipoContato.COMERCIAL, null);
                    }

                    if ((rs.getString("endercob") != null) && (!"".equals(rs.getString("endercob")))) {
                        imp.setCob_endereco(rs.getString("endercob"));
                    }
                    if ((rs.getString("bairrocob") != null) && (!"".equals(rs.getString("bairrocob")))) {
                        imp.setCob_bairro(rs.getString("bairrocob"));
                    }
                    if ((rs.getString("cidadecob") != null) && (!"".equals(rs.getString("cidadecob")))) {
                        imp.setCob_municipio(rs.getString("cidadecob"));
                    }
                    if ((rs.getString("estcob") != null) && (!"".equals(rs.getString("estcob")))) {
                        imp.setCob_uf(rs.getString("estcob"));
                    }
                    if ((rs.getString("cepcob") != null) && (!"".equals(rs.getString("cepcob")))) {
                        imp.setCob_cep(rs.getString("cepcob"));
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
        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "	CODFORNEC AS idfornecedor,\n"
                    + "	CODPROD AS idproduto,	\n"
                    + "	CODPRODFOR AS codigoexterno,\n"
                    + "	QTUNITCX AS qtd\n"
                    +   "FROM\n"
                    + "	PRODFORNEC p"
            )) {
                while (rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdFornecedor(rs.getString("idfornecedor"));
                    imp.setIdProduto(rs.getString("idproduto"));
                    imp.setCodigoExterno(rs.getString("codigoexterno"));
                    imp.setQtdEmbalagem(rs.getDouble("qtd"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "    codcli id,\n"
                    + "    cliente razaosocial,\n"
                    + "    fantasia,\n"
                    + "    cpfcnpj,\n"
                    + "    ie,\n"
                    + "    bloq,\n"
                    + "    dtcadastro,\n"
                    + "    limcred,\n"
                    + "    obs,\n"
                    + "    endereco,\n"
                    + "    bairro,\n"
                    + "    cidade,\n"
                    + "    codmunicipio,\n"
                    + "    estado,\n"
                    + "    cep,\n"
                    + "    telefone,\n"
                    + "    email,\n"
                    + "    dtnasc,\n"
                    + "    contato,\n"
                    + "    telcontato2,\n"
                    + "    enderent,\n"
                    + "    bairroent,\n"
                    + "    cidadeent,\n"
                    + "    estadoent,\n"
                    + "    cepent,\n"
                    + "    telent\n"
                    + "from\n"
                    + "    cliente\n"
                    + "order by\n"
                    + "    codcli")) {
                while (rs.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rs.getString("id"));
                    imp.setRazao(rs.getString("razaosocial"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setCnpj(rs.getString("cpfcnpj"));
                    imp.setInscricaoestadual(rs.getString("ie"));
                    imp.setAtivo("S".equals(rs.getString("bloq")) ? false : true);
                    imp.setDataCadastro(rs.getDate("dtcadastro"));
                    imp.setValorLimite(rs.getDouble("limcred"));
                    imp.setObservacao(rs.getString("obs"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setMunicipioIBGE(rs.getInt("codmunicipio"));
                    imp.setUf(rs.getString("estado"));
                    imp.setCep(rs.getString("cep"));
                    imp.setTelefone(rs.getString("telefone"));
                    imp.setEmail(rs.getString("email"));
                    imp.setDataNascimento(rs.getDate("dtnasc"));
                    if ((rs.getString("contato") != null) && (!"".equals(rs.getString("contato")))) {
                        imp.addContato("1", rs.getString("contato"), null, null, null);
                    }
                    if ((rs.getString("telcontato2") != null) && (!"".equals(rs.getString("telcontato2")))) {
                        imp.addContato("2", "TEL2", rs.getString("telcontato2"), null, null);
                    }
                    imp.setPermiteCheque(true);
                    imp.setPermiteCreditoRotativo(true);

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "    c.numvenda id,\n"
                    + "    c.numnota coo,\n"
                    + "    c.prest parcela,\n"
                    + "    c.codcli idcliente,\n"
                    + "    cli.cpfcnpj,\n"
                    + "    coalesce(cli.fantasia, cli.cliente) razao,\n"
                    + "    c.numcx ecf,\n"
                    + "    to_char(c.dtemissao, 'yyyy-MM-dd') dtemissao,\n"
                    + "    to_char(c.dtvenc, 'yyyy-MM-dd') dtvencimento,\n"
                    + "    c.valor,\n"
                    + "    c.vljuro,\n"
                    + "    cob.codcob,\n"
                    + "    cob.descricao\n"
                    + "from\n"
                    + "    creceber c, cobranca cob, cliente cli\n"
                    + "where\n"
                    + "    c.codcob = cob.codcob and\n"
                    + "    c.codcli = cli.codcli and\n"
                    + "    status = 'A' and\n"
                    + "    c.vpago = 0 and\n"
                    + "    cob.codcob = '" + vPlanoContas + "' \n"
                    + "order by\n"
                    + "    c.dtvenc")) {
                SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy");
                while (rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();

                    imp.setId(rs.getString("id"));
                    imp.setNumeroCupom(rs.getString("coo"));
                    imp.setParcela(rs.getInt("parcela"));
                    imp.setIdCliente(rs.getString("idcliente"));
                    imp.setCnpjCliente(rs.getString("cpfcnpj"));
                    imp.setEcf(rs.getString("ecf"));
                    imp.setDataEmissao(rs.getDate("dtemissao"));
                    imp.setDataVencimento(rs.getDate("dtvencimento"));
                    imp.setValor(rs.getDouble("valor"));
                    imp.setJuros(rs.getDouble("vljuro"));

                    result.add(imp);

                }
            }
        }

        return result;
    }

    @Override
    public List<ChequeIMP> getCheques() throws Exception {
        List<ChequeIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT \n"
                    + "	c.numvenda id, \n"
                    + "	c.numnota coo, \n"
                    + "	c.prest parcela, \n"
                    + "	c.codcli idcliente, \n"
                    + "	c.vljuro,\n"
                    + "	cli.cpfcnpj, \n"
                    + "	COALESCE (cli.fantasia, cli.cliente) razao, \n"
                    + "   cli.telefone,\n"
                    + "	c.numcx ecf,\n"
                    + "	TO_CHAR (c.dtemissao, 'yyyy-MM-dd') dtemissao, \n"
                    + "	TO_CHAR (c.dtvenc, 'yyyy-MM-dd') dtvencimento, \n"
                    + "	c.valor, \n"
                    + "	cob.codcob, \n"
                    + "	cob.descricao, \n"
                    + "	c.obs, \n"
                    + "	c.obs2, \n"
                    + "	c.numch cheque, \n"
                    + "	c.numag agencia, \n"
                    + "	c.numbco banco, \n"
                    + "	c.numconta conta \n"
                    + "FROM \n"
                    + "	creceber c, \n"
                    + "	cobranca cob, \n"
                    + "	cliente cli\n"
                    + "WHERE \n"
                    + "	c.codcob = cob.codcob AND \n"
                    + "	c.codcli = cli.codcli AND \n"
                    + "	status = 'A' AND \n"
                    + "	c.vpago = 0 AND \n"
                    + "	cob.codcob in ('CH', 'CHP', 'CHT') AND \n"
                    + "	c.valor > 0\n"
                    + "ORDER BY \n"
                    + "	c.dtvenc")) {

                while (rs.next()) {

                    while (rs.next()) {
                        ChequeIMP imp = new ChequeIMP();
                        imp.setId(rs.getString("id"));
                        imp.setCpf(rs.getString("cpfcnpj"));
                        imp.setNumeroCheque(rs.getString("cheque"));
                        imp.setNome(rs.getString("razao"));
                        imp.setTelefone(rs.getString("telefone"));
                        imp.setAgencia(rs.getString("agencia"));
                        imp.setConta(rs.getString("conta"));
                        imp.setBanco(Utils.stringToInt(rs.getString("banco")));
                        imp.setNumeroCupom(rs.getString("coo"));
                        imp.setValorJuros(rs.getDouble("vljuro"));
                        imp.setValor(rs.getDouble("valor"));
                        imp.setDate(rs.getDate("dtemissao"));
                        imp.setDataDeposito(rs.getDate("dtvencimento"));
                        imp.setEcf(rs.getString("ecf"));

                        result.add(imp);
                    }
                }
            }

            return result;
        }
    }
}
