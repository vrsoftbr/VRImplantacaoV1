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
import java.util.Set;
import java.util.logging.Level;
import static vr.core.utils.StringUtils.LOG;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.cadastro.financeiro.contareceber.OpcaoContaReceber;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoFornecedor;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.ContaReceberIMP;
import vrimplantacao2.vo.importacao.ConveniadoIMP;
import vrimplantacao2.vo.importacao.ConvenioEmpresaIMP;
import vrimplantacao2.vo.importacao.ConvenioTransacaoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.OfertaIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;
import vrimplantacao2_5.dao.conexao.ConexaoFirebird;

/**
 *
 * @author Alan
 */
public class FenixMEDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "FenixME";
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
                OpcaoProduto.FAMILIA,
                OpcaoProduto.FAMILIA_PRODUTO,
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
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
                OpcaoProduto.FABRICANTE
        ));
    }

    @Override
    public Set<OpcaoFornecedor> getOpcoesDisponiveisFornecedor() {
        return new HashSet<>(Arrays.asList(
                OpcaoFornecedor.ENDERECO,
                OpcaoFornecedor.DADOS,
                OpcaoFornecedor.CONTATOS,
                OpcaoFornecedor.SITUACAO_CADASTRO,
                OpcaoFornecedor.TIPO_EMPRESA,
                OpcaoFornecedor.TIPO_FORNECEDOR,
                OpcaoFornecedor.PAGAR_FORNECEDOR,
                OpcaoFornecedor.PRODUTO_FORNECEDOR,
                OpcaoFornecedor.OUTRAS_RECEITAS
        ));
    }

    @Override
    public Set<OpcaoCliente> getOpcoesDisponiveisCliente() {
        return new HashSet<>(Arrays.asList(
                OpcaoCliente.DADOS,
                OpcaoCliente.ENDERECO,
                OpcaoCliente.CONTATOS,
                OpcaoCliente.DATA_CADASTRO,
                OpcaoCliente.DATA_NASCIMENTO,
                OpcaoCliente.VALOR_LIMITE,
                OpcaoCliente.CLIENTE_EVENTUAL,
                OpcaoCliente.CONVENIO_EMPRESA,
                OpcaoCliente.CONVENIO_CONVENIADO,
                OpcaoCliente.CONVENIO_TRANSACAO,
                OpcaoCliente.RECEBER_CREDITOROTATIVO,
                OpcaoCliente.VENCIMENTO_ROTATIVO,
                OpcaoCliente.OUTRAS_RECEITAS
        ));
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "	CODIGOICMS id,\n"
                    + "	OBSERVACAO descricao,\n"
                    + "	CAST(SITUACAOTRIBUTARIA AS int) cst,\n"
                    + "	CAST(REPLACE(\n"
                    + "		(CASE \n"
                    + "     	WHEN OBSERVACAO LIKE 'ICMS %' THEN substring(OBSERVACAO FROM 6 FOR 2)\n"
                    + "     	WHEN OBSERVACAO LIKE 'ALIQUOTA %' THEN substring(OBSERVACAO FROM 10 FOR 2)\n"
                    + "     	ELSE 0 END), '%', '') AS int) aliq,\n"
                    + "	CASE\n"
                    + "		WHEN OBSERVACAO LIKE '%RED%' THEN 1\n"
                    + "		ELSE 0\n"
                    + "	END red\n"
                    + "FROM\n"
                    + "	ICMS\n"
                    + "ORDER BY 1"
            )) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(
                            rs.getString("id"),
                            rs.getString("descricao"),
                            rs.getInt("cst"),
                            rs.getDouble("aliq"),
                            rs.getDouble("red"))
                    );
                }
            }
        }
        return result;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "WITH familia AS (\n"
                    + "	SELECT DISTINCT \n"
                    + "	f.CODIGOREFERENCIA id_familia\n"
                    + "FROM\n"
                    + "	PRODUTO f\n"
                    + "WHERE\n"
                    + "	f.CODIGOREFERENCIA IS NOT NULL\n"
                    + "	AND f.CODIGOREFERENCIA != 0)\n"
                    + "SELECT DISTINCT \n"
                    + "	f.CODIGOREFERENCIA id_familia,\n"
                    + "	f.DESCRICAOREDUZIDA familia\n"
                    + "FROM\n"
                    + "	PRODUTO f\n"
                    + "	JOIN familia ON id_familia = f.CODIGOPRODUTO \n"
                    + "WHERE\n"
                    + "	f.CODIGOREFERENCIA IS NOT NULL\n"
                    + "	AND f.CODIGOREFERENCIA != 0\n"
                    + " ORDER BY 1"
            )) {
                while (rs.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rs.getString("id_familia"));
                    imp.setDescricao(rs.getString("familia"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "	m1.CODIGODEPARTAMENTO merc1,\n"
                    + "	m1.DESCRICAO descmerc1,\n"
                    + "	m2.CODIGOSECAO merc2,\n"
                    + "	m2.DESCRICAO descmerc2,\n"
                    + "	m3.CODIGOGRUPO merc3,\n"
                    + "	m3.DESCRICAO descmerc3,\n"
                    + "	m4.CODIGOSUBGRUPO merc4,\n"
                    + "	m4.DESCRICAO descmerc4\n"
                    + "FROM\n"
                    + "	DEPARTAMENTO m1\n"
                    + "	JOIN SECAO m2 ON m1.CODIGODEPARTAMENTO = m2.CODIGODEPARTAMENTO\n"
                    + "	JOIN GRUPO m3 ON m3.CODIGOGRUPO = m2.CODIGOSECAO\n"
                    + "	JOIN SUBGRUPO m4 ON m4.CODIGOGRUPO = m3.CODIGOGRUPO\n"
                    + "ORDER BY 1,3,5,7"
            )) {
                while (rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());

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
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "	ean.CODIGOPRODUTO id_produto,\n"
                    + "	ean.CODIGOEAN ean,\n"
                    + "	p.UNIDADEVENDA tipo_embalagem\n"
                    + "FROM\n"
                    + "	EANPRODUTO ean\n"
                    + "	JOIN produto p ON p.CODIGOPRODUTO = EAN.CODIGOPRODUTO\n"
                    + "ORDER BY ean.CODIGOPRODUTO "
            )) {
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rs.getString("id_produto"));
                    imp.setEan(rs.getString("ean"));
                    imp.setQtdEmbalagem(1);
                    imp.setTipoEmbalagem(rs.getString("tipo_embalagem"));

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
                    "SELECT\n"
                    + "	p.CODIGOPRODUTO id_produto,\n"
                    + "	ean.CODIGOEAN ean,\n"
                    + "	DESCRICAOCOMPLETA desc_completa,\n"
                    + "	DESCRICAOREDUZIDA desc_reduzida,\n"
                    + "	UNIDADEVENDA tipo_embalagem,\n"
                    + " p.UNIDADECOMPRA emb_cotacao,\n"
                    + "	p.QTDUNIDADE qtde_cotacao,\n"
                    + "	p.DIASVALIDADE validade,\n"
                    + "	p.DATAINCLUSAO data_cadastro,\n"
                    + "	p.DATAALTERACAO data_alteracao,\n"
                    + " CASE WHEN e.EMLINHA = 'S' THEN 1 ELSE 0 END ativo,\n"
                    + "	e.PRECOVENDA1 precovenda,\n"
                    + "	e.CUSTO precocusto,\n"
                    + "	m.PORCENTPRECO1 margem,\n"
                    + " p.CODIGOREFERENCIA id_familia,\n"
                    + "	m1.CODIGODEPARTAMENTO merc1,\n"
                    + "	m1.DESCRICAO desc_merc1,\n"
                    + "	m2.CODIGOSECAO merc2,\n"
                    + "	m2.DESCRICAO desc_merc2,\n"
                    + "	p.CODIGOGRUPO merc3,\n"
                    + "	m3.DESCRICAO desc_merc3,\n"
                    + "	p.CODIGOSUBGRUPO merc4,\n"
                    + "	m4.DESCRICAO desc_merc4,\n"
                    + "	ICMSENTRADA icms_entrada,\n"
                    + "	ICMSSAIDA icms_saida,\n"
                    + "	CODIGOTRIBUTACAOPIS piscofins,\n"
                    + "	CLASSIFICACAO ncm,\n"
                    + " i.CEST,\n"
                    + "	i.NATUREZA natrec,\n"
                    + "	e.QTDMINIMA estmin,\n"
                    + "	e.QTDMAXIMA estmax,\n"
                    + "	e.QTDUNIDADE estoque\n"
                    + "FROM\n"
                    + "	PRODUTO p\n"
                    + "	LEFT JOIN ESTOQUE e ON e.CODIGOPRODUTO = p.CODIGOPRODUTO\n"
                    + " AND e.CODIGOFILIAL = '" + getLojaOrigem() + "'\n"
                    + "	LEFT JOIN MARGEM m ON m.CODIGOMARGEM = e.CODIGOMARGEM\n"
                    + "	LEFT JOIN EANPRODUTO ean ON p.CODIGOPRODUTO = ean.CODIGOPRODUTO\n"
                    + " LEFT JOIN IMPOSTO i ON i.CODIGOPRODUTO = p.CODIGOPRODUTO\n"
                    + "	LEFT JOIN GRUPO m3 ON p.CODIGOGRUPO = m3.CODIGOGRUPO\n"
                    + "	LEFT JOIN SUBGRUPO m4 ON m3.CODIGOGRUPO = m4.CODIGOGRUPO\n"
                    + "	LEFT JOIN SECAO m2 ON m2.CODIGOSECAO = m3.CODIGOSECAO\n"
                    + "	LEFT JOIN DEPARTAMENTO m1 ON m1.CODIGODEPARTAMENTO = m2.CODIGODEPARTAMENTO\n"
                    + "ORDER BY\n"
                    + "	p.CODIGOPRODUTO"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("id_produto"));
                    imp.setEan(rst.getString("ean"));

                    imp.setDescricaoCompleta(Utils.acertarTexto(rst.getString("desc_completa")));
                    imp.setDescricaoReduzida(Utils.acertarTexto(rst.getString("desc_reduzida")));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setTipoEmbalagem(rst.getString("tipo_embalagem"));
                    imp.setTipoEmbalagemCotacao(rst.getString("emb_cotacao"));
                    imp.setQtdEmbalagemCotacao(rst.getInt("qtde_cotacao"));
                    imp.setDataAlteracao(rst.getDate("data_alteracao"));
                    imp.setDataCadastro(rst.getDate("data_cadastro"));
                    imp.setValidade(rst.getInt("validade"));

                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setCustoComImposto(rst.getDouble("custo"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setMargem(rst.getDouble("margem"));

                    imp.setIdFamiliaProduto(rst.getString("id_familia"));
                    //imp.seteBalanca(rst.getBoolean("e_balanca"));
                    imp.setCodMercadologico1(rst.getString("merc1"));
                    imp.setCodMercadologico2(rst.getString("merc2"));
                    imp.setCodMercadologico3(rst.getString("merc3"));
                    imp.setCodMercadologico4(rst.getString("merc4"));

                    //imp.setSituacaoCadastro(rst.getInt("ativo"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));

                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setEstoqueMinimo(rst.getDouble("estmin"));
                    imp.setEstoqueMaximo(rst.getDouble("estmax"));

                    String idIcmsDebito = rst.getString("icms_saida");
                    String idIcmsCredito = rst.getString("icms_entrada");

                    imp.setIcmsDebitoId(idIcmsDebito);
                    imp.setIcmsConsumidorId(idIcmsDebito);
                    imp.setIcmsCreditoId(idIcmsCredito);
                    imp.setIcmsCreditoForaEstadoId(idIcmsCredito);
                    imp.setIcmsDebitoForaEstadoId(idIcmsDebito);
                    imp.setIcmsDebitoForaEstadoNfId(idIcmsDebito);

                    imp.setPiscofinsCstCredito(rst.getString("piscofins"));
                    imp.setPiscofinsCstDebito(rst.getString("piscofins"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("natrec"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<OfertaIMP> getOfertas(Date dataTermino) throws Exception {
        List<OfertaIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "	CODIGOPRODPROMOCAO id_item_oferta,\n"
                    + "	oi.CODIGOPRODUTO id_produto,\n"
                    + "	oi.DATAINICIAL data_ini,\n"
                    + "	oi.DATAFINAL data_fim,\n"
                    + "	e.PRECOVENDA1 preco_normal,\n"
                    + "	oi.PRECOVENDA preco_oferta\n"
                    + "FROM\n"
                    + "	PRODPROMOCAO oi \n"
                    + "	JOIN PROMOCAO o ON o.CODIGOPROMOCAO = oi.CODIGOPROMOCAO\n"
                    + "	JOIN ESTOQUE e ON e.CODIGOPRODUTO = oi.CODIGOPRODUTO AND e.CODIGOFILIAL = o.CODIGOFILIAL\n"
                    + "WHERE\n"
                    + "	o.CODIGOFILIAL = '" + getLojaOrigem() + "'\n"
                    + " AND o.DATAFINAL > 'now'"
            )) {
                while (rs.next()) {
                    OfertaIMP imp = new OfertaIMP();

                    imp.setIdProduto(rs.getString("id_produto"));
                    imp.setDataInicio(rs.getDate("data_ini"));
                    imp.setDataFim(rs.getDate("data_fim"));
                    imp.setPrecoNormal(rs.getDouble("preco_normal"));
                    imp.setPrecoOferta(rs.getDouble("preco_oferta"));

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
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "	CASE WHEN codigo IS NULL THEN CNPJCPF ELSE codigo END id,\n"
                    + "	CNPJCPF,\n"
                    + "	PESSOAINSCRICAO ie_rg,\n"
                    + "	PESSOADESCRICAO razao,\n"
                    + "	PESSOAFANTASIA fantasia,\n"
                    + "	PESSOACADASTRO data_cadastro,\n"
                    + " CASE TIPOFORNECEDOR\n"
                    + "     WHEN 'D' THEN 1\n"
                    + "     WHEN 'I' THEN 2\n"
                    + "     WHEN 'O' THEN 3\n"
                    + "     WHEN 'P' THEN 5\n"
                    + "     ELSE 0\n"
                    + "	END tipo_fornecedor,\n"
                    + "	ENDERECORUA endereco,\n"
                    + "	ENDERECONUMERO numero,\n"
                    + "	ENDERECOBAIRRO bairro,\n"
                    + "	ENDERECOCIDADE cidade,\n"
                    + "	ENDERECOESTADO uf,\n"
                    + "	ENDERECOCEP cep,\n"
                    + "	CASE WHEN ATIVO = 'S' THEN 1 ELSE 0 END ativo,\n"
                    + "	TELCOMERCIAL1 tel_principal,\n"
                    + "	PESSOAREPRESENTANTE contato1,\n"
                    + "	TELCOMERCIAL2 fone1,\n"
                    + "	TELCELULAR celular1,\n"
                    + "	EMAILCOMERCIAL email1,\n"
                    + "	'FAX' contato2,\n"
                    + "	EMAILPESSOAL email2,\n"
                    + "	TELFAX fax2,\n"
                    + "	PESSOAOBSERVACAO obs\n"
                    + "FROM\n"
                    + "	PESSOA f\n"
                    + "WHERE\n"
                    + "	PESSOAFORNECEDOR = 'S'\n"
                    + "ORDER BY 1"
            )) {
                while (rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rs.getString("id"));
                    imp.setCnpj_cpf(rs.getString("cnpjcpf"));
                    imp.setIe_rg(rs.getString("ie_rg"));

                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setDatacadastro(rs.getDate("data_cadastro"));

                    imp.setTipoFornecedor(TipoFornecedor.getById(rs.getInt("tipo_fornecedor")));

                    /*switch (rs.getInt("tipo_fornecedor")) {
                        case 1:
                            imp.setTipoFornecedor(TipoFornecedor.ATACADO);
                            break;
                        case 2:
                            imp.setTipoFornecedor(TipoFornecedor.DISTRIBUIDOR);
                            break;
                        case 3:
                            imp.setTipoFornecedor(TipoFornecedor.PRESTADOR);
                            break;
                        case 5:
                            imp.setTipoFornecedor(TipoFornecedor.PRODUTORRURAL);
                            break;
                        default:
                            imp.setTipoFornecedor(TipoFornecedor.INDUSTRIA);
                            break;
                    }*/
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));
                    imp.setAtivo(rs.getBoolean("ativo"));
                    imp.setObservacao(rs.getString("obs"));
                    imp.setTel_principal(rs.getString("tel_principal"));

                    if ((rs.getString("contato1") != null)
                            && (!rs.getString("contato1").trim().isEmpty())) {
                        imp.addContato(
                                rs.getString("contato1"),
                                rs.getString("fone1"),
                                rs.getString("celular1"),
                                TipoContato.COMERCIAL,
                                rs.getString("email1")
                        );
                    }

                    if ((rs.getString("contato2") != null)
                            && (!rs.getString("contato2").trim().isEmpty())) {
                        imp.addContato(
                                rs.getString("contato2"),
                                rs.getString("fax2"),
                                null,
                                TipoContato.COMERCIAL,
                                rs.getString("email2")
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

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "	CASE WHEN f.codigo is null then f.CNPJCPF else f.codigo end id_fornecedor,\n"
                    + "	pf.CODIGOPRODUTO id_produto,\n"
                    + " COALESCE (CODIGOFORNECEDOR, 'BENGALA') cod_externo,\n"
                    + "	p.QTDUNIDADE qtdembalagem\n"
                    + "FROM\n"
                    + "	FORNECIMENTO pf\n"
                    + "	JOIN PESSOA f ON f.CNPJCPF = pf.CNPJCPF AND f.PESSOAFORNECEDOR = 'S'\n"
                    + "	JOIN produto p ON p.CODIGOPRODUTO = pf.CODIGOPRODUTO"
            )) {
                while (rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setIdFornecedor(rs.getString("id_fornecedor"));
                    imp.setIdProduto(rs.getString("id_produto"));
                    imp.setCodigoExterno(rs.getString("cod_externo"));
                    imp.setQtdEmbalagem(rs.getDouble("qtdembalagem"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ContaPagarIMP> getContasPagar() throws Exception {
        List<ContaPagarIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	CODIGOTITULO id,\n"
                    + "	CASE WHEN f.CODIGO IS NULL THEN f.CNPJCPF ELSE f.CODIGO END id_fornecedor,\n"
                    + "	DOCUMENTO,\n"
                    + "	DATAEMISSAO emissao,\n"
                    + "	cp.DATAINCLUSAO entrada,\n"
                    + "	VENCIMENTO,\n"
                    + "	VALORTITULO valor,\n"
                    + " DESCONTODE desconto,\n"
                    + " CASE WHEN DESCONTODE > 0 THEN VALORTITULO - DESCONTODE ELSE VALORTITULO END valor_com_desc,\n"
                    + "	OBSERVACOES observacao\n"
                    + "FROM\n"
                    + "	DESPESAS cp\n"
                    + "	JOIN PESSOA f ON f.CNPJCPF = cp.CNPJCPF\n"
                    + "WHERE\n"
                    + "	cp.CODIGOFILIAL = '" + getLojaOrigem() + "'\n"
                    + "	AND DATABAIXA IS NULL"
            )) {
                while (rst.next()) {
                    ContaPagarIMP imp = new ContaPagarIMP();

                    imp.setId(rst.getString("id"));
                    imp.setIdFornecedor(rst.getString("id_fornecedor"));
                    imp.setNumeroDocumento(rst.getString("documento"));
                    imp.setDataEmissao(rst.getDate("emissao"));
                    imp.setDataEntrada(rst.getDate("entrada"));
                    imp.addVencimento(rst.getDate("vencimento"), rst.getDouble("valor_com_desc"), rst.getString("observacao"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    // Devoluçao de Fornecedores
    @Override
    public List<ContaReceberIMP> getContasReceber(Set<OpcaoContaReceber> opt) throws Exception {
        List<ContaReceberIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "	CODIGOTITULO id,\n"
                    + "	CASE WHEN f.codigo IS NULL THEN f.CNPJCPF ELSE codigo END id_fornecedor,\n"
                    + "	DATAEMISSAO emissao,\n"
                    + "	VENCIMENTO,\n"
                    + "	VALORTITULO valor,\n"
                    + "	HISTORICO observacao\n"
                    + "FROM\n"
                    + "	RECEITAS dev\n"
                    + "	JOIN PESSOA f ON f.CNPJCPF = dev.CNPJCPF AND f.PESSOAFORNECEDOR = 'S'\n"
                    + "WHERE\n"
                    + "	dev.CODIGOFILIAL = '" + getLojaOrigem() + "'\n"
                    + "	AND dev.CODIGOTIPOOPERACAO = '000048'\n"
                    + "	AND DATABAIXA IS NULL\n"
                    + "ORDER BY 1,3"
            )) {
                while (rs.next()) {
                    ContaReceberIMP imp = new ContaReceberIMP();

                    imp.setId(rs.getString("id"));
                    imp.setIdFornecedor(rs.getString("id_fornecedor"));
                    imp.setDataEmissao(rs.getDate("emissao"));
                    imp.setDataVencimento(rs.getDate("vencimento"));
                    imp.setValor(rs.getDouble("valor"));
                    imp.setObservacao(rs.getString("observacao"));

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
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "	CASE WHEN CODIGO IS NULL THEN CNPJCPF ELSE CODIGO END id,\n"
                    + "	CNPJCPF,\n"
                    + "	PESSOAINSCRICAO ie_rg,\n"
                    + "	PESSOADESCRICAO razao,\n"
                    + "	PESSOAFANTASIA fantasia,\n"
                    + "	PESSOANASCIMENTO dt_nasc,\n"
                    + "	PESSOACADASTRO dt_cad,\n"
                    + "	CASE WHEN ATIVO = 'S' THEN 1 ELSE 0 END ativo,\n"
                    + "	CLIENTELIMITE limite,\n"
                    + "	ENDERECORUA endereco,\n"
                    + "	ENDERECONUMERO numero,\n"
                    + "	ENDERECOBAIRRO bairro,\n"
                    + "	ENTREGACIDADE cidade,\n"
                    + "	ENDERECOESTADO uf,\n"
                    + "	ENDERECOCEP cep,\n"
                    + "	EMAILPESSOAL email,\n"
                    + "	TELCOMERCIAL1 fone,\n"
                    + "	PESSOAOBSERVACAO obs\n"
                    + "FROM\n"
                    + "	PESSOA c\n"
                    + "WHERE\n"
                    + "	PESSOACLIENTE = 'S'\n"
                    + "ORDER BY 1"
            )) {
                while (rs.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rs.getString("id"));
                    imp.setCnpj(rs.getString("cnpjcpf"));
                    imp.setInscricaoestadual(rs.getString("ie_rg"));

                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setDataNascimento(rs.getDate("dt_nasc"));
                    imp.setDataCadastro(rs.getDate("dt_cad"));
                    imp.setAtivo(rs.getBoolean("ativo"));
                    imp.setValorLimite(rs.getDouble("limite"));

                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));

                    imp.setTelefone(rs.getString("fone"));
                    imp.setEmail(rs.getString("email"));
                    imp.setObservacao(rs.getString("obs"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ConvenioEmpresaIMP> getConvenioEmpresa() throws Exception {
        List<ConvenioEmpresaIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	CODIGOFILIAL id,\n"
                    + "	CNPJ,\n"
                    + "	INSCRICAO ie,\n"
                    + "	NOME razao,\n"
                    + "	ENDERECO,\n"
                    + "	\"NUMERO\",\n"
                    + "	BAIRRO,\n"
                    + "	CIDADE,\n"
                    + "	ESTADO uf,\n"
                    + "	CEP,\n"
                    + "	TELEFONE\n"
                    + "FROM\n"
                    + "	FILIAL\n"
                    + "ORDER BY 1"
            )) {
                while (rst.next()) {
                    ConvenioEmpresaIMP imp = new ConvenioEmpresaIMP();

                    imp.setId(rst.getString("id"));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setInscricaoEstadual(rst.getString("ie"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setTelefone(rst.getString("telefone"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ConveniadoIMP> getConveniado() throws Exception {
        List<ConveniadoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "	CNPJCPF id_cliente,\n"
                    + "	FISICOJURIDICO,\n"
                    + "	PESSOADESCRICAO nome,\n"
                    + "	CODIGOFILIAL id_empresa,\n"
                    + "	CNPJCPF cpf_cnpj,\n"
                    + "	COALESCE (CLIENTELIMITE,0) limite,\n"
                    + "	CASE WHEN CODIGOSITUACAO = 0 THEN 1 ELSE 0 END status,\n"
                    + "	PESSOAOBSERVACAO observacao\n"
                    + "FROM\n"
                    + "	PESSOA p\n"
                    + "WHERE\n"
                    + "	PESSOACLIENTE = 'S'\n"
                    + "	AND FISICOJURIDICO = 'F'\n"
                  //+ "	AND CODIGOFILIAL = '" + getLojaOrigem() + "'\n"
                    + "ORDER BY\n"
                    + "	PESSOADESCRICAO"
            )) {
                while (rs.next()) {
                    ConveniadoIMP imp = new ConveniadoIMP();
                    imp.setId(rs.getString("id_cliente"));
                    imp.setNome(rs.getString("nome"));
                    imp.setIdEmpresa(rs.getString("id_empresa"));
                    imp.setCnpj(rs.getString("cpf_cnpj"));
                    imp.setConvenioLimite(rs.getDouble("limite"));
                    imp.setLojaCadastro(Integer.parseInt(getLojaOrigem()));
                    imp.setSituacaoCadastro(rs.getInt("status") == 1 ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    imp.setObservacao(rs.getString("observacao"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ConvenioTransacaoIMP> getConvenioTransacao() throws Exception {
        List<ConvenioTransacaoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	CODIGOTITULO id,\n"
                    + "	ct.CNPJCPF id_conveniado,\n"
                    + "	DOCUMENTO,\n"
                    + "	ct.DATAINCLUSAO emissao,\n"
                    + "	VALORTITULO valor,\n"
                    + "	OBSERVACOES observacao\n"
                    + "FROM\n"
                    + "	RECEITAS ct\n"
                    + "JOIN PESSOA p ON ct.CNPJCPF = p.CNPJCPF AND ct.CODIGOFILIAL = p.CODIGOFILIAL\n"
                    + "WHERE\n"
                    + "	ct.CODIGOFILIAL = '" + getLojaOrigem() + "'\n"
                    + "	AND p.PESSOACLIENTE = 'S'\n"
                    + "	AND FISICOJURIDICO = 'F'\n"
                    + "	AND ct.CODIGOTIPOOPERACAO IS NULL\n"
                    + "	AND DATABAIXA IS NULL\n"
                    + "ORDER BY 2"
            )) {
                while (rst.next()) {
                    ConvenioTransacaoIMP imp = new ConvenioTransacaoIMP();

                    imp.setId(rst.getString("id"));
                    imp.setIdConveniado(rst.getString("id_conveniado"));
                    imp.setNumeroCupom(rst.getString("documento"));
                    imp.setDataHora(rst.getTimestamp("emissao"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setObservacao(rst.getString("observacao"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    private Date dataInicioVenda;
    private Date dataTerminoVenda;

    @Override
    public Iterator<VendaIMP> getVendaIterator() throws Exception {
        return new FenixMEDAO.VendaIterator(getLojaOrigem(), this.dataInicioVenda, this.dataTerminoVenda);
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        return new FenixMEDAO.VendaItemIterator(getLojaOrigem(), this.dataInicioVenda, this.dataTerminoVenda);
    }

    public void setDataInicioVenda(Date dataInicioVenda) {
        this.dataInicioVenda = dataInicioVenda;
    }

    public void setDataTerminoVenda(Date dataTerminoVenda) {
        this.dataTerminoVenda = dataTerminoVenda;
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
                        String id = rst.getString("id_venda");
                        if (!uk.add(id)) {
                            LOG.warning("Venda " + id + " já existe na listagem");
                        }
                        next.setId(id);
                        next.setNumeroCupom(Utils.stringToInt(rst.getString("numerocupom")));
                        next.setEcf(Utils.stringToInt(rst.getString("ecf")));
                        next.setData(rst.getDate("data"));

                        String horaInicio = timestampDate.format(rst.getDate("data")) + " " + rst.getString("hora");
                        String horaTermino = timestampDate.format(rst.getDate("data")) + " " + rst.getString("hora");
                        next.setHoraInicio(timestamp.parse(horaInicio));
                        next.setHoraTermino(timestamp.parse(horaTermino));
                        next.setSubTotalImpressora(rst.getDouble("valor"));
                        next.setCancelado(rst.getBoolean("cancelado"));
                    }
                }
            } catch (SQLException | ParseException ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {

            String strDataInicio = new SimpleDateFormat("yyyy-MM-dd").format(dataInicio);
            String strDataTermino = new SimpleDateFormat("yyyy-MM-dd").format(dataTermino);
            this.sql
                    = "SELECT\n"
                    + "	NUMEROCUPOMFISCAL||CAIXA||DATACUPOM id_venda,\n"
                    + "	NUMEROCUPOMFISCAL numerocupom,\n"
                    + "	CAIXA AS ecf,\n"
                    + "	DATACUPOM data,\n"
                    + "	hora AS hora,\n"
                    + "	CASE WHEN CUPOMCANCELADO = 'S' THEN 1 ELSE 0 END cancelado, \n"
                    + "	VALORTOTALCUPOM valor\n"
                    + "FROM\n"
                    + "	ECF_MOVIMENTO m\n"
                    + "WHERE\n"
                    + "	LOJA = '1'\n"   // <--  ALTERAR A LOJA
                    + "	AND DATACUPOM between '" + strDataInicio + "' and '" + strDataTermino + "'\n"
                    + "	AND TIPOREGISTRO = 'PG'\n"
                    + "GROUP BY 1,2,3,4,5,6,7";
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

        private Statement stm = ConexaoFirebird.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaItemIMP next;

        private void obterNext() {
            try {
                if (next == null) {
                    if (rst.next()) {
                        next = new VendaItemIMP();

                        next.setVenda(rst.getString("id_venda"));
                        next.setId(rst.getString("id_item"));
                        next.setSequencia(rst.getInt("nroitem"));
                        next.setProduto(rst.getString("produto"));
                        next.setUnidadeMedida(rst.getString("unidade"));
                        next.setCodigoBarras(rst.getString("codigobarras"));
                        next.setDescricaoReduzida(rst.getString("descricao"));
                        next.setQuantidade(rst.getDouble("quantidade"));
                        next.setPrecoVenda(rst.getDouble("precovenda"));
                        next.setValorDesconto(rst.getDouble("desconto"));
                        next.setTotalBruto(rst.getDouble("total"));
                        next.setCancelado(rst.getBoolean("cancelado"));
                    }
                }
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaItemIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {
            this.sql
                    = "SELECT\n"
                    + "	NUMEROCUPOMFISCAL||CAIXA||DATACUPOM id_venda,\n"
                    + "	CODIGO id_item,\n"
                    + "	SEQUENCIA nroitem,\n"
                    + "	CODIGOPRODUTO produto,\n"
                    + "	UNIDADEVENDA unidade,\n"
                    + "	CODIGOBARRAS,\n"
                    + "	DESCRICAOPRODUTO descricao,\n"
                    + "	QUANTIDADE,\n"
                    + "	VALORUNITARIO precovenda,\n"
                    + "	VALORDESCONTO desconto,\n"
                    + "	VALORTOTALITEM total,\n"
                    + "	CASE WHEN ITEMCANCELADO = 'S' THEN 1 ELSE 0 END cancelado\n"
                    + "FROM\n"
                    + "	ECF_MOVIMENTO m\n"
                    + "WHERE\n"
                    + "	LOJA = '1'\n"
                    + "	AND TIPOREGISTRO = 'VI'\n"
                    + "	AND DATACUPOM BETWEEN '" + VendaIterator.FORMAT.format(dataInicio) + "' AND '" + VendaIterator.FORMAT.format(dataTermino) + "'\n"
                    + "ORDER BY 1,2,3";
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
