package vrimplantacao2_5.dao.sistema;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.parametro.Parametros;
import vrimplantacao2.vo.cadastro.oferta.SituacaoOferta;
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
import vrimplantacao2_5.dao.conexao.ConexaoFirebird;

/**
 *
 * @author Alan @
 */
public class SygmaDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "Sygma";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.DATA_CADASTRO,
                OpcaoProduto.QTD_EMBALAGEM_EAN,
                OpcaoProduto.QTD_EMBALAGEM_COTACAO,
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.ATIVO,
                OpcaoProduto.PESO_BRUTO,
                OpcaoProduto.PESO_LIQUIDO,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.MARGEM,
                OpcaoProduto.VENDA_PDV,
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
                OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                OpcaoProduto.ATUALIZAR_SOMAR_ESTOQUE,
                OpcaoProduto.OFERTA,
                OpcaoProduto.VOLUME_QTD
        ));
    }

    @Override
    public Set<OpcaoFornecedor> getOpcoesDisponiveisFornecedor() {
        return new HashSet<>(Arrays.asList(
                OpcaoFornecedor.RAZAO_SOCIAL,
                OpcaoFornecedor.NOME_FANTASIA,
                OpcaoFornecedor.CNPJ_CPF,
                OpcaoFornecedor.INSCRICAO_ESTADUAL,
                OpcaoFornecedor.ENDERECO,
                OpcaoFornecedor.DADOS,
                OpcaoFornecedor.CONTATOS,
                OpcaoFornecedor.SITUACAO_CADASTRO,
                OpcaoFornecedor.PRODUTO_FORNECEDOR));
    }

    @Override
    public Set<OpcaoCliente> getOpcoesDisponiveisCliente() {
        return new HashSet<>(Arrays.asList(
                OpcaoCliente.DADOS,
                OpcaoCliente.ENDERECO,
                OpcaoCliente.BAIRRO,
                OpcaoCliente.COMPLEMENTO,
                OpcaoCliente.NUMERO,
                OpcaoCliente.MUNICIPIO,
                OpcaoCliente.UF,
                OpcaoCliente.CONTATOS,
                OpcaoCliente.DATA_CADASTRO,
                OpcaoCliente.DATA_NASCIMENTO,
                OpcaoCliente.RECEBER_CREDITOROTATIVO));
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "	te.CODTABTRIBUT id,\n"
                    + " ALQCONSU aliquota,\n"
                    + " ((BASECONSU-100)*-1) reducao,\n"
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
                            rs.getString("descricao")));
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
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "	pd.CODDER id,\n"
                    + "	p.DESCRICAO descricao,\n"
                    + "	pd.DESCRICAOCOMPLETA descricao_completa,\n"
                    + "	pd.REFERENCIA ean,\n"
                    + "	pd.UNIDMED unidade,\n"
                    + " pd.FRACEMB emb_compra,\n"
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
                    + " pd.CODCEST cest,\n"
                    + " p.CODTABTRIBUT id_icms,\n"
                    + "	SUBSTRING(t2.CSTCONSU FROM 2 FOR 3) cst_debito,\n"
                    + "	t2.ALQCONSU aliq_debito,\n"
                    + "	CASE WHEN t2.BASECONSU <> 0 THEN ((t2.BASECONSU-100)*-1) ELSE t2.BASECONSU END red_debito,\n"
                    + "	SUBSTRING(t2.CSTENTRADA FROM 2 FOR 3) cst_credito,\n"
                    + "	t2.ALQENTRADA aliq_credito,\n"
                    + "	t2.BASEENTRADA red_credito,\n"
                    + "	pc.CSTPISSAI piscofins_debito,\n"
                    + "	pc.CSTPISENT piscofins_credito,\n"
                    + " pc.CODNATREC natrec\n"
                    + "FROM\n"
                    + "	TPRODUTO p\n"
                    + "	JOIN TDERIVACAO pd ON pd.CODPRO = p.CODPRO\n"
                    + "	JOIN TABPISCOF pc ON pc.CODTABPISCOF = p.CODTABPISCOF\n"
                    + "	JOIN TABTRIBUT t1 ON t1.CODTABTRIBUT = p.CODTABTRIBUT \n"
                    + "	JOIN TABTRIBUTUF t2 ON t2.CODTABTRIBUT = t1.CODTABTRIBUT \n"
                    + "WHERE\n"
                    + "	t2.TABUF = '" + Parametros.get().getUfPadraoV2().getSigla() + "'"
            )) {
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());

                    imp.setImportId(rs.getString("id"));
                    imp.setEan(rs.getString("ean"));
                    imp.setDescricaoCompleta(rs.getString("descricao_completa"));
                    imp.setDescricaoReduzida(rs.getString("descricao"));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setTipoEmbalagem(rs.getString("unidade"));
                    imp.setQtdEmbalagemCotacao(rs.getInt("emb_compra"));
                    imp.seteBalanca(rs.getBoolean("e_balanca"));

                    imp.setEstoque(rs.getDouble("estoque"));
                    imp.setEstoqueMaximo(rs.getDouble("estmax"));
                    imp.setPesoBruto(rs.getDouble("pesobruto"));
                    imp.setPesoLiquido(rs.getDouble("pesoliquido"));

                    imp.setMargem(rs.getDouble("margem"));
                    imp.setCustoSemImposto(rs.getDouble("custo"));
                    imp.setCustoComImposto(rs.getDouble("custo"));
                    imp.setPrecovenda(rs.getDouble("precovenda"));

                    imp.setSituacaoCadastro(rs.getInt("situacaocadastro"));
                    imp.setNcm(rs.getString("ncm"));
                    imp.setCest(rs.getString("cest"));

                    imp.setIcmsDebitoId(rs.getString("id_icms"));
                    imp.setIcmsCreditoId(rs.getString("id_icms"));
                    imp.setIcmsDebitoForaEstadoNfId(rs.getString("id_icms"));
                    imp.setIcmsDebitoForaEstadoId(rs.getString("id_icms"));
                    imp.setIcmsCreditoForaEstadoId(rs.getString("id_icms"));
                    imp.setIcmsConsumidorId(rs.getString("id_icms"));

                    imp.setPiscofinsCstDebito(rs.getInt("piscofins_debito"));
                    imp.setPiscofinsCstCredito(rs.getInt("piscofins_credito"));
                    imp.setPiscofinsNaturezaReceita(rs.getString("natrec"));

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
                while (rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());

                    imp.setImportId(rs.getString("id"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setCnpj_cpf(rs.getString("cnpj"));
                    imp.setIe_rg(rs.getString("ie_rg"));

                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));
                    imp.setDatacadastro(rs.getDate("data_cadastro"));
                    imp.setObservacao(rs.getString("observacao"));

                    imp.setTel_principal(rs.getString("fone1"));

                    String fone2 = (rs.getString("fone2"));
                    if (!"".equals(fone2)) {
                        FornecedorContatoIMP cont = imp.getContatos().make("1");
                        cont.setImportId("1");
                        cont.setNome("FONE 2");
                        cont.setTipoContato(TipoContato.COMERCIAL);
                        cont.setTelefone(fone2);
                    }

                    String fax = (rs.getString("fax"));
                    if (!"".equals(fax)) {
                        FornecedorContatoIMP cont = imp.getContatos().make("2");
                        cont.setImportId("2");
                        cont.setNome("FAX");
                        cont.setTipoContato(TipoContato.COMERCIAL);
                        cont.setTelefone(fax);
                    }

                    String email = (rs.getString("fone2"));
                    if (!"".equals(email)) {
                        FornecedorContatoIMP cont = imp.getContatos().make("3");
                        cont.setImportId("3");
                        cont.setNome("EMAIL");
                        cont.setTipoContato(TipoContato.COMERCIAL);
                        cont.setTelefone(email);
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
                    + "	pf.COD_FORNECEDOR id_fornecedor,\n"
                    + "	d.CODDER id_produto,\n"
                    + " d.FRACEMB qtdembalagem,\n"
                    + "	pf.REFFORNEC cod_externo\n"
                    + "FROM\n"
                    + "	FORNECPROD pf\n"
                    + "	JOIN TDERIVACAO d ON d.CODDER = pf.CODDER\n"
                    + "WHERE\n"
                    + "	pf.REFFORNEC != '' AND pf.REFFORNEC IS NOT NULL\n"
                    + "ORDER BY 1,2"
            )) {
                while (rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());

                    imp.setIdFornecedor(rs.getString("id_fornecedor"));
                    imp.setIdProduto(rs.getString("id_produto"));
                    imp.setQtdEmbalagem(rs.getDouble("qtdembalagem"));
                    imp.setCodigoExterno(rs.getString("cod_externo"));

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
    public List<OfertaIMP> getOfertas(Date datatermino) throws Exception {
        List<OfertaIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
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
                while (rs.next()) {
                    OfertaIMP imp = new OfertaIMP();
                    imp.setIdProduto(rs.getString("id_produto"));
                    imp.setDataInicio(rs.getDate("data_inicial"));
                    imp.setDataFim(rs.getDate("data_final"));
                    imp.setPrecoNormal(rs.getDouble("preco_venda"));
                    imp.setPrecoOferta(rs.getDouble("preco_oferta"));
                    imp.setSituacaoOferta(SituacaoOferta.ATIVO);

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
                    /*"SELECT\n"
                    + "	 CODPESSOA id,\n"
                    + "	 NOME razao,\n"
                    + "	 nome fantasia,\n"
                    + "	 CPF cpf_cnpj,\n"
                    + "	 RG rg_ie,\n"
                    + "	 DATA_NASC dt_nascimento,\n"
                    + "	 SUBSTRING(INCLUSAOHORARIO FROM 1 FOR 10) dt_cadastro,\n"
                    + "	 LOGRA_COM endereco,\n"
                    + "	 NUM_COM numero,\n"
                    + "	 COMPL_COM complemento,\n"
                    + "	 BAIRRO bairro,\n"
                    + "	 UPPER(c.NOM_CIDADE) cidade,\n"
                    + "	 c.SGL_ESTADO uf,\n"
                    + "	 CEP,\n"
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
                    + "ORDER BY 1"*/
                    "SELECT \n"
                    + "	 CODPESSOA id,\n"
                    + "	 CASE WHEN tipo = 'F' THEN NOME ELSE razaosocial END razao,\n"
                    + "	 nome fantasia,\n"
                    + "	 CASE WHEN tipo = 'F' THEN CPF ELSE CNPJ END cpf_cnpj,\n"
                    + "	 CASE WHEN tipo = 'F' THEN RG ELSE INSCEST END rg_ie,\n"
                    + "	 DATA_NASC dt_nascimento,\n"
                    + "	 SUBSTRING(INCLUSAOHORARIO FROM 1 FOR 10) dt_cadastro,\n"
                    + "	 CASE WHEN LOGRA_COM IS NOT NULL OR LOGRA_COM = '' THEN LOGRA_COM ELSE logra_DOM END endereco,\n"
                    + "	 CASE WHEN NUM_COM IS NOT NULL OR num_COM = '' THEN NUM_COM ELSE NUM_DOM END numero,\n"
                    + "	 CASE WHEN COMPL_COM IS NOT NULL OR COMPL_COM = '' THEN COMPL_COM ELSE COMPL_DOM END complemento,\n"
                    + "	 CASE WHEN BAIRRO IS NOT NULL OR bairro = '' THEN bairro ELSE bairrodom END bairro,\n"
                    + "	 UPPER(c.NOM_CIDADE) cidade,\n"
                    + "	 c.SGL_ESTADO uf,\n"
                    + "	 CEP,\n"
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
                    + "ORDER BY 1"
            )) {
                while (rs.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rs.getString("id"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setCnpj(rs.getString("cpf_cnpj"));
                    imp.setInscricaoestadual(rs.getString("rg_ie"));
                    imp.setDataNascimento(rs.getDate("dt_nascimento"));
                    imp.setDataCadastro(rs.getDate("dt_cadastro"));

                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));

                    imp.setAtivo(rs.getBoolean("situacaocadastro"));
                    imp.setValorLimite(rs.getDouble("valorlimite"));
                    imp.setNomeConjuge(rs.getString("conjuge"));
                    imp.setNomeMae(rs.getString("nomemae"));
                    imp.setNomePai(rs.getString("nomepai"));
                    imp.setEmpresa(rs.getString("empresa"));
                    imp.setEmpresaTelefone(rs.getString("empresa_fone"));
                    imp.setCargo(rs.getString("cargo"));
                    imp.setSalario(rs.getDouble("salario"));

                    imp.setEmail(rs.getString("email"));
                    imp.setTelefone(rs.getString("telefone"));
                    imp.setCelular(rs.getString("celular"));
                    imp.setObservacao(rs.getString("observacao"));

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
                while (rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();

                    imp.setId(rs.getString("id"));
                    imp.setIdCliente(rs.getString("id_cliente"));
                    imp.setCnpjCliente(rs.getString("cpf_cnpj"));
                    imp.setDataEmissao(rs.getDate("emissao"));
                    imp.setNumeroCupom(rs.getString("cupom"));
                    imp.setEcf(rs.getString("ecf"));
                    imp.setValor(rs.getDouble("valor"));

                    imp.setDataVencimento(rs.getDate("vencimento"));
                    imp.setObservacao(rs.getString("observacao"));

                    result.add(imp);
                }
            }
        }

        return result;
    }
}
