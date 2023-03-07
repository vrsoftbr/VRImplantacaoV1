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
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
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
                OpcaoFornecedor.PAGAR_FORNECEDOR,
                OpcaoFornecedor.PRODUTO_FORNECEDOR
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
                OpcaoCliente.VENCIMENTO_ROTATIVO,
                OpcaoCliente.VALOR_LIMITE,
                OpcaoCliente.CLIENTE_EVENTUAL,
                OpcaoCliente.RECEBER_CREDITOROTATIVO
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
                    + "ORDER BY 1,3,5,7	"
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
                    + "	p.DATAINCLUSAO data_cadastro,\n"
                    + "	p.DATAALTERACAO data_alteracao,\n"
                    + "	e.PRECOVENDA1 precovenda,\n"
                    + "	e.CUSTO precocusto,\n"
                    + "	m.PORCENTPRECO1 margem,\n"
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

                    imp.setDescricaoCompleta(rst.getString("desc_completa"));
                    imp.setDescricaoReduzida(rst.getString("desc_reduzida"));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setTipoEmbalagem(rst.getString("tipo_embalagem"));
                    imp.setDataAlteracao(rst.getDate("data_alteracao"));
                    imp.setDataCadastro(rst.getDate("data_cadastro"));

                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setCustoComImposto(rst.getDouble("custo"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setMargem(rst.getDouble("margem"));

                    //imp.seteBalanca(rst.getBoolean("e_balanca"));
                    imp.setCodMercadologico1(rst.getString("merc1"));
                    imp.setCodMercadologico2(rst.getString("merc2"));
                    imp.setCodMercadologico3(rst.getString("merc3"));
                    imp.setCodMercadologico4(rst.getString("merc4"));

                    //imp.setSituacaoCadastro(rst.getInt("ativo"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("natrec"));

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
                    + "	CASE WHEN codigo is null then CNPJCPF else codigo end id,\n"
                    + "	CNPJCPF,\n"
                    + "	PESSOAINSCRICAO ie_rg,\n"
                    + "	PESSOADESCRICAO razao,\n"
                    + "	PESSOAFANTASIA fantasia,\n"
                    + "	PESSOACADASTRO data_cadastro,\n"
                    + "	ENDERECORUA endereco,\n"
                    + "	ENDERECONUMERO numero,\n"
                    + "	ENDERECOBAIRRO bairro,\n"
                    + "	ENTREGACIDADE cidade,\n"
                    + "	ENDERECOESTADO uf,\n"
                    + "	ENDERECOCEP cep,\n"
                    + "	CASE WHEN ATIVO = 'S' THEN 1 ELSE 0 END ativo,\n"
                    + "	PESSOAREPRESENTANTE contato1,\n"
                    + "	TELCOMERCIAL1 fone1,\n"
                    + "	TELCELULAR celular1,\n"
                    + "	EMAILCOMERCIAL email1,\n"
                    + "	PESSOAREPRESENTANTE contato2,\n"
                    + "	TELCOMERCIAL2 fone2,\n"
                    + "	EMAILPESSOAL email2,\n"
                    + "	TELFAX fax,\n"
                    + " PESSOAOBSERVACAO obs\n"
                    + "FROM\n"
                    + "	PESSOA f\n"
                    + "WHERE\n"
                    + "	PESSOAFORNECEDOR = 'S'\n"
                    + "	AND CODIGOFILIAL = '" + getLojaOrigem() + "'\n"
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

                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));
                    imp.setAtivo(rs.getBoolean("ativo"));
                    imp.setObservacao(rs.getString("obs"));

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
                                rs.getString("fone2"),
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
                    ""
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
                    + "	OBSERVACOES observacao\n"
                    + "FROM\n"
                    + "	DESPESAS cp\n"
                    + "	JOIN PESSOA f ON f.CNPJCPF = cp.CNPJCPF\n"
                    + "WHERE\n"
                    + "	cp.CODIGOFILIAL = '" + getLojaOrigem() + "'\n"
                    + "	AND VALORBAIXA IS NULL"
            )) {
                while (rst.next()) {
                    ContaPagarIMP imp = new ContaPagarIMP();

                    imp.setId(rst.getString("id"));
                    imp.setIdFornecedor(rst.getString("id_fornecedor"));
                    imp.setNumeroDocumento(rst.getString("documento"));
                    imp.setDataEmissao(rst.getDate("emissao"));
                    imp.setDataEntrada(rst.getDate("entrada"));
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
                    + "	AND CODIGOFILIAL = '" + getLojaOrigem() + "'\n"
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
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "	CODIGOTITULO id,\n"
                    + "	CASE WHEN p.CODIGO IS NULL THEN p.CNPJCPF ELSE p.CODIGO END id_cliente,\n"
                    + " p.CNPJCPF,\n"
                    + "	DOCUMENTO,\n"
                    + "	DATAEMISSAO emissao,\n"
                    + "	VENCIMENTO,\n"
                    + "	VALORTITULO valor,\n"
                    + "	HISTORICO obs\n"
                    + "FROM\n"
                    + "	RECEITAS cr\n"
                    + "	JOIN PESSOA p ON p.CNPJCPF = cr.CNPJCPF\n"
                    + "WHERE\n"
                    + "	cr.CODIGOFILIAL = '" + getLojaOrigem() + "'\n"
                    + "	AND VALORBAIXA IS NULL"
            )) {
                while (rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();

                    imp.setId(rs.getString("id"));
                    imp.setNumeroCupom(Utils.formataNumero(rs.getString("documento")));
                    imp.setIdCliente(rs.getString("id_cliente"));
                    imp.setCnpjCliente(rs.getString("cnpjcpf"));
                    imp.setValor(rs.getDouble("valor"));
                    imp.setDataEmissao(rs.getDate("emissao"));
                    imp.setDataVencimento(rs.getDate("vencimento"));
                    imp.setObservacao(rs.getString("obs"));

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
                        next.setSubTotalImpressora(rst.getDouble("total"));
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
                    + "	CODIGO_VENDA id_venda,\n"
                    + "	COO_ECF_NF numerocupom,\n"
                    + "	SERIE_ECF ecf,\n"
                    + "	CAST(DATA_VENDA AS DATE) data,\n"
                    + "	SUBSTRING(DATA_VENDA FROM 12 FOR 8) hora,\n"
                    + "	VALOR_LIQUIDO_VENDA total,\n"
                    + "	CASE WHEN VENDAATIVA = 'N' THEN 1 ELSE 0 END cancelado\n"
                    + "FROM\n"
                    + "	TB_VENDA v\n"
                    + "WHERE\n"
                    + "	NUMERO_LOJA = " + idLojaCliente + "\n"
                    + " AND COD_TIPOMOVIMENTO in (5,-5)\n"
                    + " AND VENDAATIVA = 'S'\n"
                    + "	AND CAST(DATA_VENDA AS DATE) BETWEEN '" + strDataInicio + "' AND '" + strDataTermino + "'";
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
                    + "	COD_VENDA id_venda,\n"
                    + "	CODIGO_VENDAITEM id_item,\n"
                    + "	SEQUENCIAL_ITEM_CUPOM nroitem,\n"
                    + "	COD_PRODUTO produto,\n"
                    + "	p.UNIDADE_REFERENCIA unidade,\n"
                    + "	COD_BARRA codigobarras,\n"
                    + "	p.DESCRICAO_PDV descricao,\n"
                    + "	QUANTIDADE,\n"
                    + "	VALOR_UNITARIO precovenda,\n"
                    + " (vi.valor_total + vi.valor_rat_acrescimo + vi.valor_acrescimo) - (vi.valor_rat_desconto + vi.valor_desconto) AS total,\n"
                    + "	CASE\n"
                    + "	  WHEN CANCELADO != 'N' THEN 1 ELSE 0\n"
                    + "	  END CANCELADO\n"
                    + "FROM\n"
                    + "	TB_VENDA_ITEM vi\n"
                    + "	JOIN TB_VENDA v ON v.CODIGO_VENDA = vi.COD_VENDA\n"
                    + "	JOIN TB_PRODUTOS p ON p.CODIGO_PRODUTO = vi.COD_PRODUTO \n"
                    + "WHERE\n"
                    + "	v.NUMERO_LOJA = " + idLojaCliente + "\n"
                    + "	AND CAST(DATA_VENDA AS DATE) BETWEEN '" + VendaIterator.FORMAT.format(dataInicio) + "' AND '" + VendaIterator.FORMAT.format(dataTermino) + "'\n"
                    + "	ORDER BY 1,3";
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
