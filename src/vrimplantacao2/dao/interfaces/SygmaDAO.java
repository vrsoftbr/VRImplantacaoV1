package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.parametro.Parametros;
import vrimplantacao2.vo.cadastro.oferta.SituacaoOferta;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ChequeIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorContatoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.OfertaIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Alan
 */
public class SygmaDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                new OpcaoProduto[]{
                    OpcaoProduto.PRODUTOS,
                    OpcaoProduto.DATA_CADASTRO,
                    OpcaoProduto.EAN,
                    OpcaoProduto.EAN_EM_BRANCO,
                    OpcaoProduto.TIPO_EMBALAGEM_EAN,
                    OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                    OpcaoProduto.VALIDADE,
                    OpcaoProduto.DESC_COMPLETA,
                    OpcaoProduto.DESC_GONDOLA,
                    OpcaoProduto.DESC_REDUZIDA,
                    OpcaoProduto.PESO_BRUTO,
                    OpcaoProduto.PESO_LIQUIDO,
                    OpcaoProduto.ESTOQUE_MAXIMO,
                    OpcaoProduto.ESTOQUE_MINIMO,
                    OpcaoProduto.ESTOQUE,
                    OpcaoProduto.MARGEM,
                    OpcaoProduto.CUSTO_SEM_IMPOSTO,
                    OpcaoProduto.CUSTO_COM_IMPOSTO,
                    OpcaoProduto.PRECO,
                    OpcaoProduto.ATIVO,
                    OpcaoProduto.NCM,
                    OpcaoProduto.CEST,
                    OpcaoProduto.PIS_COFINS,
                    OpcaoProduto.ICMS,
                    OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                    OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                    OpcaoProduto.OFERTA,
                    OpcaoProduto.MAPA_TRIBUTACAO
                }
        ));
    }

    @Override
    public String getSistema() {
        return "Sygma";
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT DISTINCT \n"
                    + "	1 AS id,\n"
                    + "	VAL_PARAMETRO fantasia,\n"
                    + "	(SELECT VAL_PARAMETRO FROM TPARAMETRO t WHERE ORDEM_PARAMETRO = 1003) cnpj\n"
                    + "FROM\n"
                    + "	TPARAMETRO t\n"
                    + "	WHERE ORDEM_PARAMETRO = 1002"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("id"), rst.getString("fantasia")));
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
                    "SELECT \n"
                    + "	p.codpro id_produto,\n"
                    + "	pd.REFERENCIA ean,\n"
                    + "	1 AS qtdembalagem\n"
                    + "FROM\n"
                    + "	TPRODUTO p\n"
                    + "	JOIN TDERIVACAO pd ON pd.CODPRO = p.CODPRO\n"
                    + "ORDER BY 1"
            )) {
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id_produto"));
                    imp.setEan(rs.getString("ean"));
                    imp.setQtdEmbalagem(rs.getInt("qtdembalagem"));

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
                    + "	p.CODPRO id,\n"
                    + "	p.DESCRICAO descricao,\n"
                    + "	pd.DESCRICAOCOMPLETA descricao_completa,\n"
                    + "	pd.REFERENCIA ean,\n"
                    + "	pd.UNIDMED unidade,\n"
                    + "	CASE\n"
                    + "	  WHEN CODSUBGRUPO = 2 THEN 1\n"
                    + "	  ELSE 0\n"
                    + "	END e_balanca,\n"
                    + "	qd_est estoque,\n"
                    + "	qd_max estmax,\n"
                    + "	pesobruto,\n"
                    + "	pd.PESOLIQ pesoliquido,\n"
                    + "	pd.CUSTOCOMPRA custo,\n"
                    + "	pd.MARGEM,\n"
                    + "	pd.PRECO_VIST precovenda,\n"
                    + "	CASE\n"
                    + "	  WHEN pd.FLAGINATIVO = 'N' THEN 1\n"
                    + "	  ELSE 0\n"
                    + "	END situacaocadastro,\n"
                    + "	pd.CODNCM ncm,\n"
                    + " p.CODTABTRIBUT id_icms,\n"
                    + "	SUBSTRING(t2.CSTCONSU FROM 2 FOR 3) cst_debito,\n"
                    + "	t2.ALQCONSU aliq_debito,\n"
                    + "	CASE WHEN t2.BASECONSU <> 0 THEN ((t2.BASECONSU-100)*-1) ELSE t2.BASECONSU END red_debito,\n"
                    + "	SUBSTRING(t2.CSTENTRADA FROM 2 FOR 3) cst_credito,\n"
                    + "	t2.ALQENTRADA aliq_credito,\n"
                    + "	t2.BASEENTRADA red_credito,\n"
                    + "	pc.CSTPISSAI piscofins_debito,\n"
                    + "	pc.CSTPISENT piscofins_credito\n"
                    + "FROM\n"
                    + "	TPRODUTO p\n"
                    + "	JOIN TDERIVACAO pd ON pd.CODPRO = p.CODPRO\n"
                    + "	JOIN TABPISCOF pc ON pc.CODTABPISCOF = p.CODTABPISCOF\n"
                    + "	JOIN TABTRIBUT t1 ON t1.CODTABTRIBUT = p.CODTABTRIBUT \n"
                    + "	JOIN TABTRIBUTUF t2 ON t2.CODTABTRIBUT = t1.CODTABTRIBUT \n"
                    + "WHERE\n"
                    + "	t2.TABUF = '" + Parametros.get().getUfPadraoV2().getSigla() + "'"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());

                    imp.setImportId(rst.getString("id"));
                    imp.setEan(rst.getString("ean"));
                    imp.setDescricaoCompleta(rst.getString("descricao_completa"));
                    imp.setDescricaoReduzida(rst.getString("descricao"));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setTipoEmbalagem(rst.getString("unidade"));
                    imp.seteBalanca(rst.getBoolean("e_balanca"));
                    
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setEstoqueMaximo(rst.getDouble("estmax"));
                    imp.setPesoBruto(rst.getDouble("pesobruto"));
                    imp.setPesoLiquido(rst.getDouble("pesoliquido"));
                    
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setCustoSemImposto(rst.getDouble("custo"));
                    imp.setCustoComImposto(rst.getDouble("custo"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    
                    imp.setSituacaoCadastro(rst.getInt("situacaoCadastro"));
                    imp.setNcm(rst.getString("ncm"));
                    
                    imp.setIcmsDebitoId(rst.getString("id_icms"));
                    imp.setIcmsCreditoId(rst.getString("id_icms"));
                    imp.setIcmsDebitoForaEstadoNfId(rst.getString("id_icms"));
                    imp.setIcmsDebitoForaEstadoId(rst.getString("id_icms"));
                    imp.setIcmsCreditoForaEstadoId(rst.getString("id_icms"));
                    imp.setIcmsConsumidorId(rst.getString("id_icms"));
                    
                    imp.setPiscofinsCstDebito(rst.getInt("piscofins_debito"));
                    imp.setPiscofinsCstCredito(rst.getInt("piscofins_credito"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<OfertaIMP> getOfertas(Date datatermino) throws Exception {
        List<OfertaIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	d.CODPRO id_produto,\n"
                    + "	o.DTAINI data_inicial,\n"
                    + "	o.DTAFIM data_final,\n"
                    + "	d.PRECO_VIST preco_venda,\n"
                    + "	i.PRECOPROMOCIONAL preco_oferta\n"
                    + "FROM\n"
                    + "	TPROMOCAO o\n"
                    + "	JOIN TITPROMOCAO i ON i.CODPROMOCAO = o.SEQ\n"
                    + "	JOIN TDERIVACAO d ON i.CODDER = d.CODDER \n"
                    + "WHERE\n"
                    + "	DTAFIM >= 'now'"
            )) {
                while (rst.next()) {
                    OfertaIMP imp = new OfertaIMP();
                    imp.setIdProduto(rst.getString("id_produto"));
                    imp.setDataInicio(rst.getDate("data_inicial"));
                    imp.setDataFim(rst.getDate("data_final"));
                    imp.setPrecoNormal(rst.getDouble("preco_venda"));
                    imp.setPrecoOferta(rst.getDouble("preco_oferta"));
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

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	COD_FORNECEDOR id,\n"
                    + "	DES_RAZAO razao,\n"
                    + "	DES_FANTASIA fantasia,\n"
                    + "	NUM_CNPJ_CPF cnpj,\n"
                    + "	NUM_INSCRICAO_RG ie_rg,\n"
                    + "	LOGRADOURO endereco,\n"
                    + "	LOGRA_NUM numero,\n"
                    + "	LOGRA_COMPL complemento,\n"
                    + "	NOM_BAIRRO bairro,\n"
                    + "	c.NOM_CIDADE cidade,\n"
                    + "	c.SGL_ESTADO uf,\n"
                    + "	NUM_CEP cep,\n"
                    + "	NUM_TELEFONE fone1,\n"
                    + "	NUM_TELEFONE2 fone2,\n"
                    + "	NUM_FAX fax,\n"
                    + "	E_MAIL email,\n"
                    + "	LIMITECRED limite,\n"
                    + "	DTA_INCLUSAO data_cadastro,\n"
                    + "	DES_OBSERVACAO observacao\n"
                    + "FROM\n"
                    + "	TFORNECEDOR f\n"
                    + "	JOIN TCIDADE c ON f.COD_CIDADE = c.COD_CIDADE\n"
                    + "ORDER BY 1"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());

                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    imp.setIe_rg(rst.getString("ie_rg"));

                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setDatacadastro(rst.getDate("data_cadastro"));
                    imp.setObservacao(rst.getString("observacao"));

                    imp.setTel_principal(rst.getString("fone1"));

                    String fone2 = rst.getString("fone2");
                    if (!"".equals(fone2)) {
                        FornecedorContatoIMP cont = imp.getContatos().make("1");
                        cont.setImportId("1");
                        cont.setNome("FONE 2");
                        cont.setTipoContato(TipoContato.COMERCIAL);
                        cont.setTelefone(fone2);
                    }

                    String fax = rst.getString("fax");
                    if (!"".equals(fax)) {
                        FornecedorContatoIMP cont = imp.getContatos().make("2");
                        cont.setImportId("2");
                        cont.setNome("FAX");
                        cont.setTipoContato(TipoContato.COMERCIAL);
                        cont.setTelefone(fax);
                    }

                    String email = rst.getString("email");
                    if (!"".equals(email)) {
                        FornecedorContatoIMP cont = imp.getContatos().make("3");
                        cont.setImportId("3");
                        cont.setNome("EMAIL");
                        cont.setTipoContato(TipoContato.COMERCIAL);
                        cont.setEmail(email);
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
                    "SELECT\n"
                    + "	pf.COD_FORNECEDOR id_fornecedor,\n"
                    + "	d.CODPRO id_produto,\n"
                    + "	pf.REFFORNEC cod_externo\n"
                    + "FROM\n"
                    + "	FORNECPROD pf\n"
                    + "	JOIN TDERIVACAO d ON d.CODDER = pf.CODDER\n"
                    + "WHERE\n"
                    + "	pf.REFFORNEC != '' AND pf.REFFORNEC IS NOT NULL\n"
                    + "ORDER BY 1,2"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setIdFornecedor(rst.getString("id_fornecedor"));
                    imp.setIdProduto(rst.getString("id_produto"));
                    imp.setCodigoExterno(rst.getString("codigoexterno"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ChequeIMP> getCheques() throws Exception {
        List<ChequeIMP> Result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "	SEQ_CHEQUE id,\n"
                    + "	DTA_ENTRADA DATA,\n"
                    + "	DTA_DEPOSITO deposito,\n"
                    + "	NUM_CHEQUE numero_cheque,\n"
                    + "	COD_BANCO banco,\n"
                    + "	COD_AGENCIA agencia,\n"
                    + "	NUM_CONTA_CHEQUE conta,\n"
                    + "	CNPJCPF,\n"
                    + "	NOM_EMITENTE nome,\n"
                    + "	VAL_CHEQUE valor,\n"
                    + "	CODLAN observacao\n"
                    + "FROM\n"
                    + "	TCHEQUE_PREDATADO"
            )) {
                while (rs.next()) {
                    ChequeIMP imp = new ChequeIMP();

                    imp.setId(rs.getString("id"));
                    imp.setDate(rs.getDate("data"));
                    imp.setDataDeposito(rs.getDate("deposito"));
                    imp.setNumeroCheque(rs.getString("numero_cheque"));
                    imp.setBanco(rs.getInt("banco"));
                    imp.setAgencia(rs.getString("agencia"));
                    imp.setConta(rs.getString("conta"));
                    imp.setCpf(rs.getString("cnpjcpf"));
                    imp.setNome(rs.getString("nome"));
                    imp.setValor(rs.getDouble("valor"));
                    imp.setObservacao(rs.getString("observacao"));

                    Result.add(imp);
                }
            }
        }

        return Result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	 CODPESSOA id,\n"
                    + "	 NOME razao,\n"
                    + "	 nome fantasia,\n"
                    + "	 CPF cpf_cnpj,\n"
                    + "	 RG rg_ie,\n"
                    + "	 DATA_NASC dt_nascimento,\n"
                    + "	 SUBSTRING(INCLUSAOHORARIO FROM 1 FOR 10) dt_cadastro,\n"
                    + "	 LOGRA_DOM endereco,\n"
                    + "	 NUM_DOM numero,\n"
                    + "	 COMPL_DOM complemento,\n"
                    + "	 BAIRRODOM bairro,\n"
                    + "	 UPPER(c.NOM_CIDADE) cidade,\n"
                    + "	 c.SGL_ESTADO uf,\n"
                    + "	 CEPDOM cep,\n"
                    + "	 CASE WHEN ATIVO = 'S' THEN 1 ELSE 0 END situacaocadastro,\n"
                    + "	 LIMITECRED valorlimite,\n"
                    + "	 CONJUGE,\n"
                    + "	 FILIACAO_MAE nomemae,\n"
                    + "	 FILIACAO_PAI nomepai,\n"
                    + "	 LOCALTRABALHO empresa,\n"
                    + "	 TELEFONETRABALHO empresa_fone,\n"
                    + "	 PROFTRABALHO cargo,\n"
                    + "	 RENDA salario,\n"
                    + "	 EMAIL,\n"
                    + "	 TELEFONE,\n"
                    + "	 CELULAR,\n"
                    + "  OBSERVACAO\n"
                    + "FROM\n"
                    + "	 TPESSOA p\n"
                    + "	 LEFT JOIN TCIDADE c ON p.CODCIDADEDOM = c.COD_CIDADE\n"
                    + "WHERE\n"
                    + "	 TIPO = 'F'\n"
                    + "UNION \n"
                    + "SELECT\n"
                    + "	 CODPESSOA id,\n"
                    + "	 razaosocial razao,\n"
                    + "	 nome fantasia,\n"
                    + "	 cnpj cpf_cnpj,\n"
                    + "	 inscest rg_ie,\n"
                    + "	 DATA_NASC dt_nascimento,\n"
                    + "	 SUBSTRING(INCLUSAOHORARIO FROM 1 FOR 10) dt_cadastro,\n"
                    + "	 LOGRA_COM endereco,\n"
                    + "	 NUM_COM numero,\n"
                    + "	 COMPL_COM complemento,\n"
                    + "	 BAIRRO bairro,\n"
                    + "	 UPPER(c.NOM_CIDADE) cidade,\n"
                    + "	 c.SGL_ESTADO uf,\n"
                    + "	 CEP cep,\n"
                    + "	 CASE WHEN ATIVO = 'S' THEN 1 ELSE 0 END situacaocadastro,\n"
                    + "	 LIMITECRED valorlimite,\n"
                    + "	 CONJUGE,\n"
                    + "	 FILIACAO_MAE nomemae,\n"
                    + "	 FILIACAO_PAI nomepai,\n"
                    + "	 LOCALTRABALHO empresa,\n"
                    + "	 TELEFONETRABALHO empresa_fone,\n"
                    + "	 PROFTRABALHO cargo,\n"
                    + "	 RENDA salario,\n"
                    + "	 EMAIL,\n"
                    + "	 TELEFONE,\n"
                    + "	 CELULAR,\n"
                    + "	 OBSERVACAO\n"
                    + "FROM\n"
                    + "	 TPESSOA p\n"
                    + "	 LEFT JOIN TCIDADE c ON p.CODCIDADE = c.COD_CIDADE\n"
                    + "WHERE\n"
                    + "	 TIPO = 'J'\n"
                    + "ORDER BY 1"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj(rst.getString("cpf_cnpj"));
                    imp.setInscricaoestadual(rst.getString("rg_ie"));
                    imp.setDataNascimento(rst.getDate("dt_nascimento"));
                    imp.setDataCadastro(rst.getDate("dt_cadastro"));

                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));

                    imp.setAtivo(rst.getBoolean("situacaocadastro"));
                    imp.setValorLimite(rst.getDouble("valorlimite"));
                    imp.setNomeConjuge(rst.getString("conjuge"));
                    imp.setNomeMae(rst.getString("nomemae"));
                    imp.setNomePai(rst.getString("nomepai"));
                    imp.setEmpresa(rst.getString("empresa"));
                    imp.setEmpresaTelefone(rst.getString("empresa_fone"));
                    imp.setCargo(rst.getString("cargo"));
                    imp.setSalario(rst.getDouble("salario"));

                    imp.setEmail(rst.getString("email"));
                    imp.setTelefone(rst.getString("telefone"));
                    imp.setCelular(rst.getString("celular"));
                    imp.setObservacao(rst.getString("observacao"));

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
                    "SELECT\n"
                    + "	SEQ_TITULO id,\n"
                    + "	COD_CLIENTE id_cliente,\n"
                    + "	c.CPF cpf_cnpj,\n"
                    + "	DTA_EMISSAO emissao,\n"
                    + "	NVENDA cupom,\n"
                    + "	NCAIXA ecf,\n"
                    + "	VAL_TITULO valor,\n"
                    + "	DTA_VENCIMENTO vencimento,\n"
                    + "	DES_HISTORICO observacao\n"
                    + "FROM\n"
                    + "	TTITULO_RECEBER cr\n"
                    + "	LEFT JOIN TPESSOA c ON c.CODPESSOA = cr.COD_CLIENTE \n"
                    + "WHERE\n"
                    + "	DTA_QUITACAO IS NULL"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();

                    imp.setId(rst.getString("id"));
                    imp.setIdCliente(rst.getString("id_cliente"));
                    imp.setCnpjCliente(rst.getString("cpf_cnpj"));
                    imp.setDataEmissao(rst.getDate("emissao"));
                    imp.setNumeroCupom(rst.getString("cupom"));
                    imp.setEcf(rst.getString("ecf"));
                    imp.setValor(rst.getDouble("valor"));

                    imp.setDataVencimento(rst.getDate("vencimento"));
                    imp.setObservacao(rst.getString("observacao"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "	te.CODTABTRIBUT id,\n"
                    + "ALQCONSU aliquota,\n"
                    + "((BASECONSU-100)*-1) reducao,\n"
                    + "	SUBSTRING(CSTCONSU FROM 2 FOR 3)||'-'||ALQCONSU||'-'||((BASECONSU-100)*-1) AS descricao\n"
                    + "FROM\n"
                    + "	TABTRIBUTUF te\n"
                    + "	JOIN TABTRIBUT t ON t.CODTABTRIBUT = te.CODTABTRIBUT \n"
                    + "	WHERE t.DESTABTRIBUT NOT LIKE 'Cte%'\n"
                    + "	AND TABUF = '" + Parametros.get().getUfPadraoV2().getSigla() + "'\n"
                    + "ORDER BY 1"
            )) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(
                            rs.getString("id"),
                            rs.getString("descricao"),
                            rs.getInt("id"),
                            rs.getDouble("aliquota"),
                            rs.getDouble("reducao")));
                }
            }
        }

        return result;
    }

}
