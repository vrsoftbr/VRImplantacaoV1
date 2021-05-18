package vrimplantacao2.dao.interfaces;

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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.cadastro.mercadologico.MercadologicoNivelIMP;
import vrimplantacao2.vo.enums.OpcaoFiscal;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.OfertaIMP;
import vrimplantacao2.vo.importacao.PautaFiscalIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;

/**
 * @author Alan
 */
public class LogicboxDAO extends InterfaceDAO implements MapaTributoProvider {

    private static final Logger LOG = Logger.getLogger(LogicboxDAO.class.getName());

    @Override
    public String getSistema() {
        return "LogicBox";
    }

    private String Encoding = "WIN1252";

    public void setEncoding(String Encoding) {
        this.Encoding = Encoding == null ? "WIN1252" : Encoding;
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                new OpcaoProduto[]{
                    OpcaoProduto.MERCADOLOGICO_PRODUTO,
                    OpcaoProduto.MERCADOLOGICO,
                    OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                    OpcaoProduto.PRODUTOS,
                    OpcaoProduto.EAN,
                    OpcaoProduto.EAN_EM_BRANCO,
                    OpcaoProduto.DATA_CADASTRO,
                    OpcaoProduto.TIPO_EMBALAGEM_EAN,
                    OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                    OpcaoProduto.PESAVEL,
                    OpcaoProduto.VALIDADE,
                    OpcaoProduto.DESC_COMPLETA,
                    OpcaoProduto.DESC_GONDOLA,
                    OpcaoProduto.DESC_REDUZIDA,
                    OpcaoProduto.ESTOQUE_MAXIMO,
                    OpcaoProduto.ESTOQUE_MINIMO,
                    OpcaoProduto.PRECO,
                    OpcaoProduto.CUSTO,
                    OpcaoProduto.ESTOQUE,
                    OpcaoProduto.ATIVO,
                    OpcaoProduto.NCM,
                    OpcaoProduto.CEST,
                    OpcaoProduto.PIS_COFINS,
                    OpcaoProduto.NATUREZA_RECEITA,
                    OpcaoProduto.ICMS,
                    OpcaoProduto.ICMS_SAIDA,
                    OpcaoProduto.ICMS_SAIDA_FORA_ESTADO,
                    OpcaoProduto.ICMS_SAIDA_NF,
                    OpcaoProduto.ICMS_ENTRADA,
                    OpcaoProduto.ICMS_CONSUMIDOR,
                    OpcaoProduto.ICMS_ENTRADA_FORA_ESTADO,
                    OpcaoProduto.PAUTA_FISCAL,
                    OpcaoProduto.PAUTA_FISCAL_PRODUTO,
                    OpcaoProduto.MARGEM,
                    OpcaoProduto.OFERTA,
                    OpcaoProduto.MAPA_TRIBUTACAO,
                    OpcaoProduto.USAR_CONVERSAO_ALIQUOTA_COMPLETA,
                    OpcaoProduto.CODIGO_BENEFICIO
                }
        ));
    }

    public List<Estabelecimento> getLojaCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT \n"
                    + "	EMPRESA id_loja,\n"
                    + "	RAZAOSOCIAL empresa\n"
                    + "fROM CONFIG c  "
            )) {
                while (rs.next()) {
                    result.add(new Estabelecimento(rs.getString("id_loja"), rs.getString("empresa")));
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
                    + "	codigo m1,\n"
                    + "	descricao m1desc,\n"
                    + "	codigo m2,\n"
                    + "	descricao m2desc,\n"
                    + "	codigo m3,\n"
                    + "	descricao m3desc\n"
                    + "FROM TBLSECAO\n"
                    + "ORDER BY 1,3,5"
            )) {
                while (rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rs.getString("m1"));
                    imp.setMerc1Descricao(Utils.acertarTexto(rs.getString("m1desc")));
                    imp.setMerc2ID(rs.getString("m2"));
                    imp.setMerc2Descricao(Utils.acertarTexto(rs.getString("m2desc")));
                    imp.setMerc3ID(rs.getString("m3"));
                    imp.setMerc3Descricao(Utils.acertarTexto(rs.getString("m3desc")));

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
                    + "	ID_PRO id,\n"
                    + "	DESC_PRO descricaocompleta,\n"
                    + "	cod_pro codigobarras,\n"
                    + "	UNID_PRO tipoembalagem,\n"
                    + "	CLAFIS_PRO ncm,\n"
                    + "	CEST_PRO cest,\n"
                    + "	a.ALIQUOTA icms_aliq,\n"
                    + "	a.SIT_TRIBUTARIA_NOTA icms_cst,\n"
                    + "	STATUS_PRO situacao,\n"
                    + "	CASE WHEN PESAVE_PRO = 'F' THEN 'N' ELSE 'S' end balanca,\n"
                    + "	preco precovenda,\n"
                    + "	custo custosemimposto,\n"
                    + "	margem,\n"
                    + "	est_min,\n"
                    + "	est_max,\n"
                    + "	DATA_ALTERACAO_PROD dataalteracao\n"
                    + "FROM SRQPRO p\n"
                    + "	LEFT JOIN ALIQUOTAS a ON p.ID_ALIQUOTA = a.ID_ALIQUOTAS\n"
                    + "	LEFT JOIN TBLDADOSPRO d ON d.FK_ID_PRO = p.ID_PRO\n"
                    + "	LEFT JOIN TBLCODIGOPRO ean ON p.ID_PRO = ean.FK_ID_PRO\n"
                    + "WHERE p.COD_FILIAL = " + getLojaOrigem() + ""
            )) {
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id"));
                    imp.setDescricaoCompleta(rs.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setEan(rs.getString("codigobarras"));
                    imp.setTipoEmbalagem(rs.getString("tipoembalagem"));
                    
                    imp.setNcm(rs.getString("ncm"));
                    imp.setCest(rs.getString("cest"));
                    imp.setIcmsCstSaida(rs.getInt("icms_cst"));
                    imp.setIcmsAliqSaida(rs.getDouble("icms_aliq"));
                    
                    //imp.setSituacaoCadastro(Utils.stringToInt(rs.getString("situacao")) == 1 ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    imp.seteBalanca("S".equals(rs.getString("balanca")));
                   
                    imp.setPrecovenda(rs.getDouble("precovenda"));
                    imp.setCustoComImposto(rs.getDouble("custosemimposto"));
                    imp.setCustoSemImposto(rs.getDouble("custosemimposto"));
                    imp.setMargem(rs.getDouble("margem"));
                    
                    //imp.setEstoque(rs.getDouble("estoque"));
                    imp.setEstoqueMaximo(rs.getDouble("est_max"));
                    imp.setEstoqueMinimo(rs.getDouble("est_min"));
                    //imp.setDataCadastro(rs.getDate("datacadastro"));
                    imp.setDataAlteracao(rs.getDate("dataalteracao"));
                    
                    //imp.setPautaFiscalId(imp.getImportId());
                    //imp.setBeneficio(rs.getString("beneficio"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    private String getAliquotaCreditoKey(String cst, double aliq, double red) throws SQLException {
        return String.format(
                "%s-%.2f-%.2f",
                cst,
                aliq,
                red
        );
    }

    @Override
    public List<OfertaIMP> getOfertas(Date dataTermino) throws Exception {
        List<OfertaIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	id_produto idProduto,\n"
                    + "	pre_promocao_datai dataInicio,\n"
                    + "	pre_promocao_dataf dataFim,\n"
                    + "	pre_promocao_preco precooferta,\n"
                    + "	preco_venda precoNormal\n"
                    + "from est_produtos\n"
                    + "where\n"
                    + "	pre_promocao_datai is not null\n"
                    + "	and pre_promocao_dataf is not null\n"
                    + "	and pre_promocao_datai <> '1899-12-30'\n"
                    + "	and pre_promocao_dataf >= current_date"
            )) {

                while (rs.next()) {
                    OfertaIMP imp = new OfertaIMP();
                    imp.setIdProduto(rs.getString("idproduto"));
                    imp.setDataInicio(rs.getDate("datainicio"));
                    imp.setDataFim(rs.getDate("datafim"));
                    imp.setPrecoNormal(rs.getDouble("preconormal"));
                    imp.setPrecoOferta(rs.getDouble("precooferta"));

                    result.add(imp);

                }
            }
        }
        return result;
    }

    @Override
    public List<PautaFiscalIMP> getPautasFiscais(Set<OpcaoFiscal> opcoes) throws Exception {
        List<PautaFiscalIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    id_produto,\n"
                    + "    nome_produto,\n"
                    + "    ncm,\n"
                    + "    tabicm aliquota_debito_id,\n"
                    + "    icm.tabicm_st cst_debito,\n"
                    + "    icm.tabicm_aliq icms_aliquota_debito,\n"
                    + "    icm.tabicm_pbc icms_aliquota_debito_reducao,\n"
                    + "    icm_aliq aliquota_credito,\n"
                    + "    icm_cst cst_credito,\n"
                    + "    icm_pbc aliquota_reducao_credito,\n"
                    + "    icm_stperc aliquota_final_credito,\n"
                    + "    icm_mva\n"
                    + "from\n"
                    + "    est_produtos p\n"
                    + "join tab_icm icm on p.tabicm = icm.id_tabicm\n"
                    + "where\n"
                    + "    icm_mva > 0\n"
                    + "order by\n"
                    + "    2"
            )) {
                while (rst.next()) {
                    PautaFiscalIMP imp = new PautaFiscalIMP();

                    imp.setId(rst.getString("id_produto"));
                    imp.setIva(rst.getDouble("icm_mva"));
                    imp.setIvaAjustado(imp.getIva());
                    imp.setNcm(rst.getString("ncm"));

                    int cst;
                    double aliquota = rst.getDouble("aliquota_credito");
                    double reduzido = 0;

                    if (rst.getDouble("aliquota_reducao_credito") == 100) {
                        reduzido = 0;
                    } else {
                        reduzido = rst.getDouble("aliquota_reducao_credito");
                    }

                    if (reduzido > 0) {
                        cst = 20;
                    } else if (aliquota == 0) {
                        cst = rst.getInt("cst_debito");
                    } else {
                        cst = 0;
                    }

                    imp.setAliquotaDebito(cst, aliquota, reduzido);
                    imp.setAliquotaDebitoForaEstado(cst, aliquota, reduzido);
                    imp.setAliquotaCredito(cst, aliquota, reduzido);
                    imp.setAliquotaCreditoForaEstado(cst, aliquota, reduzido);

                    /* imp.setAliquotaDebito(rst.getInt("icms_aliquota_debito") > 0 ? 0 : rst.getInt("cst_debito"), rst.getDouble("icms_aliquota_debito"), 0.0);
                     imp.setAliquotaDebitoForaEstado(rst.getInt("cst_debito"), rst.getDouble("icms_aliquota_debito"), 0.0);

                     if (rst.getDouble("aliquota_reducao_credito") == 100) {
                     reducao = 0;
                     } else {
                     reducao = rst.getDouble("aliquota_reducao_credito");
                     }
                     imp.setAliquotaCredito(0, rst.getDouble("aliquota_credito"), reducao);
                     imp.setAliquotaCreditoForaEstado(reducao > 0 ? 20 : 0, rst.getDouble("aliquota_credito"), reducao);
                     */
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
                    + "	COD_FOR id,\n"
                    + "	NOME_FOR razao,\n"
                    + "	FANTAS_FOR fantasia,\n"
                    + "	cgc_for cnpj,\n"
                    + "	INSCRI_FOR ie,\n"
                    + "	ENDERE_FOR endereco,\n"
                    + "	NRO_ENDERECO_FOR numero,\n"
                    + "	COMPLEMENTO_ENDER_FOR complemento,\n"
                    + "	BAIRRO_FOR bairro,\n"
                    + "	CIDADE_FOR cidade,\n"
                    + "	UF_FOR uf,\n"
                    + "	CEP_FOR cep,\n"
                    //+ " STATUS_ATIVO ativo,\n"
                    + "	FONE1_FOR telefone,\n"
                    + "	email_for email,\n"
                    + "	PRZENT_FOR prazoentrega\n"
                    + "FROM SRQFOR f"
            )) {
                while (rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id"));
                    imp.setRazao(Utils.acertarTexto(rs.getString("razao")));
                    imp.setFantasia(Utils.acertarTexto(rs.getString("fantasia")));
                    imp.setCnpj_cpf(Utils.formataNumero(rs.getString("cnpj")));
                    imp.setIe_rg(rs.getString("ie"));
                    //imp.setAtivo(rs.getInt("situacao") == 1);
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(Utils.acertarTexto(rs.getString("bairro")));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));

                    imp.setPrazoEntrega(rs.getInt("prazoentrega"));
                    imp.setTel_principal(rs.getString("telefone"));
                    if (rs.getString("email") != null && !"".equals(rs.getString("email"))) {
                        imp.addContato("1", "EMAIL", null, null, TipoContato.NFE, rs.getString("email"));
                    }

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
                    + "	COD_CLI id,\n"
                    + "	NOME_CLI razao,\n"
                    + "	FANTAS_CLI fantasia,\n"
                    + "	CGC_CLI cnpj,\n"
                    + "	CASE WHEN RG_CLI IS NULL THEN INSCRI_CLI ELSE RG_CLI END ie_rg,\n"
                    + "	ENDERE_CLI endereco,\n"
                    + "	NRO_ENDERECO_CLI numero,\n"
                    + "	COMPLEMENTO_ENDER_CLI complemento,\n"
                    + " BAIRRO_CLI bairro,\n"
                    + "	CIDADE_CLI cidade,\n"
                    + "	CEP_CLI cep,\n"
                    + "	UF_CLI uf,\n"
                  //+ "	STATUS_ATIVO ativo,\n"
                    + "	CASE WHEN SITUACAO = 'B' THEN 1 ELSE 0 END bloqueado,\n"
                    + "	FONE_CLI telefone,\n"
                    + "	CELULAR,\n"
                    + "	EMAIL,\n"
                    + "	DATA_CLI datacadastro,\n"
                    + "	DATA_NASC datanascimento,\n"
                    + "	NOME_MAE nomemae,\n"
                    + "	NOME_PAI nomepai,\n"
                    + "	SEXO,\n"
                    + "	CARGO_CLI cargo,\n"
                    + "	RENDA_CLI salario,\n"
                    + "	CONJUGE_CLI nomeconjuge\n"
                    + "FROM SRQCLI c"
            )) {
                while (rs.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rs.getString("id"));
                    imp.setRazao(Utils.acertarTexto(rs.getString("razao")));
                    imp.setFantasia(Utils.acertarTexto(rs.getString("fantasia")));
                    imp.setCnpj(Utils.formataNumero(rs.getString("cnpj")));
                    imp.setInscricaoestadual(rs.getString("ie_rg"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(Utils.acertarTexto(rs.getString("bairro")));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));

                    //imp.setAtivo("N".equals(rs.getString("bloqueado")));
                    imp.setBloqueado(rs.getBoolean("bloqueado"));
                    imp.setTelefone(rs.getString("telefone"));
                    imp.setCelular(rs.getString("celular"));
                    imp.setEmail(rs.getString("email"));
                    imp.setDataCadastro(rs.getDate("datacadastro"));
                    imp.setDataNascimento(rs.getDate("datanascimento"));
                    imp.setNomeMae(rs.getString("nomemae"));
                    imp.setNomePai(rs.getString("nomepai"));
                    imp.setNomeConjuge(rs.getString("nomeconjuge"));
                    if ("F".equals(rs.getString("sexo").trim())) {
                        imp.setSexo(TipoSexo.FEMININO);
                    } else {
                        imp.setSexo(TipoSexo.MASCULINO);
                    }
                    imp.setCargo(rs.getString("cargo"));
                    imp.setSalario(rs.getDouble("salario"));

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
                    + "	cod_for idfornecedor,\n"
                    + "	fk_id_pro idproduto,\n"
                    + "	cod_fabricante codexterno\n"
                    + "FROM TBLFORNECPRO\n"
                    + "	WHERE cod_fabricante IS NOT NULL \n"
                    + "ORDER BY 1"
            )) {
                while (rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdFornecedor(rs.getString("idFornecedor"));
                    imp.setIdProduto(rs.getString("idProduto"));
                    imp.setCodigoExterno(rs.getString("codexterno"));

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
                    "select\n"
                    + "    id_lcto as id,\n"
                    + "    dt_lcto as dataemissao,\n"
                    + "    documento as numerocupom,\n"
                    + "    term as ecf,\n"
                    + "    vlr_doc valorliquido,\n"
                    + "    vlr_doc - coalesce(tot_pago, 0) as valor,\n"
                    + "    vlr_juros as juros,\n"
                    + "    cr.origem_lcto || ' ' || cr.obs observacao,\n"
                    + "    cliente as idcliente,\n"
                    + "    dt_vcto as vencimento,\n"
                    + "    c.cnpj_cpf as cnpjcliente,\n"
                    + "    c.id_cliente\n"
                    + "from\n"
                    + "    cr_nota cr\n"
                    + "    left join clie_dados c\n"
                    + "        on c.id_cliente = cr.cliente\n"
                    + "where\n"
                    + "    cr.quitado = 'F' and\n"
                    + "    cr.loja = " + getLojaOrigem() + "\n"
                    + "order by dt_vcto"
            )) {
                while (rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rs.getString("id"));
                    imp.setDataEmissao(rs.getDate("dataemissao"));
                    imp.setNumeroCupom(Utils.formataNumero(rs.getString("numerocupom")));
                    imp.setEcf(rs.getString("ecf"));
                    imp.setValor(rs.getDouble("valor"));
                    imp.setObservacao(rs.getString("observacao"));
                    imp.setIdCliente(rs.getString("idcliente"));
                    imp.setDataVencimento(rs.getDate("vencimento"));
                    imp.setJuros(rs.getDouble("juros"));
                    imp.setCnpjCliente(Utils.formataNumero(rs.getString("cnpjcliente")));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    /*@Override
     public List<ContaPagarIMP> getContasPagar() throws Exception {
     List<ContaPagarIMP> result = new ArrayList<>();
     try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
     try (ResultSet rs = stm.executeQuery(
     "select\n"
     + "    id_lcto as id,\n"
     + "    cedente as idFornecedor,\n"
     + "    documento as numeroDocumento,\n"
     + "    dt_emissao as dataEmissao,\n"
     + "    dt_vcto vencimento,\n"
     + "    dt_lcto as dataEntrada,\n"
     + "    vlr_doc as valor,\n"
     + "    vlr_juros juros,\n"
     + "    obs_memo as observacao\n"
     + "from\n"
     + "    cp_nota\n"
     + "where\n"
     + "    quitado = 'F' and\n"
     + "    cedente is not null and\n"
     + "    cedente in (select id_cliente from clie_dados where upper(tipo_forn) = 'S') and\n"
     + "    loja = " + getLojaOrigem() + "\n"
     + "order by\n"
     + "    dt_lcto"
     )) {
     while (rs.next()) {
     ContaPagarIMP imp = new ContaPagarIMP();
     imp.setId(rs.getString("id"));
     imp.setIdFornecedor(rs.getString("idFornecedor"));
     imp.setNumeroDocumento(Utils.formataNumero(rs.getString("numeroDocumento")));
     imp.setDataEmissao(rs.getDate("dataEmissao"));
     imp.setDataEntrada(rs.getDate("dataEntrada"));
     imp.addVencimento(rs.getDate("vencimento"), rs.getDouble("valor"));
     imp.setObservacao(rs.getString("observacao"));

     result.add(imp);
     }
     }
     }
     return result;
     }*/
    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "	ID_ALIQUOTAS id,\n"
                    + " DESCRICAO,\n"
                    + "	SIT_TRIBUTARIA_NOTA cst,\n"
                    + "	ALIQUOTA,\n"
                    + "	BASE_CALCULO reducao\n"
                    + "FROM ALIQUOTAS a\n"
                    + "order by 1"
            )) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(rs.getString("id"),
                            rs.getString("descricao"),
                            rs.getInt("cst"),
                            rs.getInt("aliquota"),
                            rs.getInt("reducao")));
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
        return new VendaIterator(getLojaOrigem(), dataInicioVenda, dataTerminoVenda);
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        return new VendaItemIterator(getLojaOrigem(), dataInicioVenda, dataTerminoVenda);
    }

    private static class VendaIterator implements Iterator<VendaIMP> {

        public final static SimpleDateFormat FORMAT = new SimpleDateFormat("dd.MM.yyyy");
        public final static SimpleDateFormat TIMESTAMP = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");

        private Statement stm = ConexaoFirebird.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaIMP next;
        private Set<String> uk = new HashSet<>();

        private void obterNext() {
            try {

                if (next == null) {
                    if (rst.next()) {
                        next = new VendaIMP();
                        String id = rst.getString("id");
                        if (!uk.add(id)) {
                            LOG.warning("Venda " + id + " já existe na listagem");
                        }
                        next.setId(id);
                        next.setNumeroCupom(Utils.stringToInt(rst.getString("cupom")));
                        next.setEcf(Utils.stringToInt(rst.getString("ecf")));
                        next.setData(rst.getDate("data"));
                        next.setIdClientePreferencial(rst.getString("id_cliente"));
                        String horaInicio = FORMAT.format(rst.getDate("data")) + " " + rst.getString("hora");
                        String horaTermino = FORMAT.format(rst.getDate("data")) + " " + rst.getString("hora");
                        next.setHoraInicio(TIMESTAMP.parse(horaInicio));
                        next.setHoraTermino(TIMESTAMP.parse(horaTermino));
                        next.setCancelado(!"V".equals(rst.getString("situacao")));
                        next.setSubTotalImpressora(rst.getDouble("subtotalimpressora"));
                        next.setCpf(rst.getString("cnpj"));
                        next.setValorDesconto(rst.getDouble("desconto"));
                        next.setValorAcrescimo(rst.getDouble("acrescimo"));
                        next.setNumeroSerie(rst.getString("numeroserie"));
                        next.setModeloImpressora(rst.getString("modelo"));
                        //next.setNomeCliente(rst.getString("nomecliente"));
                    }
                }
            } catch (SQLException | ParseException ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {
            this.sql
                    = "select\n"
                    + "    (c.id_ecf || '' || c.id_data || '' || c.id_cupom || '' || c.id_final) as id,\n"
                    + "    c.id_data data,\n"
                    + "    c.fin_hora hora,\n"
                    + "    c.fin_pdv ecf,\n"
                    + "    e.num_serie serie,\n"
                    + "    e.num_mfd mfd,\n"
                    + "    e.fab_serie numeroserie,\n"
                    + "    e.fab_modelo modelo,\n"
                    + "    e.fab_marca marca,\n"
                    + "    c.id_cupom cupom,\n"
                    + "    c.fin_hora hora,\n"
                    + "    c.fin_obs obs,\n"
                    + "    c.fin_valor subtotalimpressora,\n"
                    + "    c.fin_desconto desconto,\n"
                    + "    c.fin_acresc acrescimo,\n"
                    + "    c.fin_situacao situacao,\n"
                    + "    c.fin_cpf_cnpj cnpj,\n"
                    + "    c.fin_clie id_cliente\n"
                    + "from\n"
                    + "    pdv_mapaf c\n"
                    + "join pdv_ecfs e on c.id_ecf = e.id_ecf\n"
                    + "where\n"
                    + "    c.id_data between '" + FORMAT.format(dataInicio) + "' and '" + FORMAT.format(dataTermino) + "' and\n"
                    + "    c.id_final = 995 and\n"
                    + "    c.fin_loja = " + idLojaCliente + "\n"
                    + "order by\n"
                    + "    c.id_data, c.id_ecf";
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

                        next.setId(rst.getString("id"));
                        next.setVenda(rst.getString("id_venda"));
                        next.setProduto(rst.getString("id_produto"));
                        next.setDescricaoReduzida(rst.getString("descricao"));
                        next.setQuantidade(rst.getDouble("quantidade"));
                        next.setTotalBruto(rst.getDouble("total"));
                        next.setValorDesconto(rst.getDouble("desconto"));
                        next.setValorAcrescimo(rst.getDouble("acrescimo"));
                        next.setCancelado(rst.getBoolean("cancelado"));
                        next.setCodigoBarras(rst.getString("ean"));
                        next.setUnidadeMedida(rst.getString("embalagem"));

                        if (rst.getString("icm") != null && !"".equals(rst.getString("icm"))) {
                            switch (rst.getString("icm")) {
                                case "FF":
                                    next.setIcmsAliq(0);
                                    next.setIcmsCst(60);
                                    next.setIcmsReduzido(0);
                                    break;

                                case "II":
                                    next.setIcmsAliq(0);
                                    next.setIcmsCst(40);
                                    next.setIcmsReduzido(0);
                                    break;

                                case "0700":
                                    next.setIcmsAliq(7);
                                    next.setIcmsCst(0);
                                    next.setIcmsReduzido(0);
                                    break;

                                case "1700":
                                    next.setIcmsAliq(17);
                                    next.setIcmsCst(0);
                                    next.setIcmsReduzido(0);
                                    break;

                                case "1200":
                                    next.setIcmsAliq(12);
                                    next.setIcmsCst(0);
                                    next.setIcmsReduzido(0);
                                    break;

                                case "2500":
                                    next.setIcmsAliq(25);
                                    next.setIcmsCst(0);
                                    next.setIcmsReduzido(0);
                                    break;

                                case "NN":
                                    next.setIcmsAliq(0);
                                    next.setIcmsCst(41);
                                    next.setIcmsReduzido(0);
                                    break;

                                case "0720":
                                    next.setIcmsAliq(7.20);
                                    next.setIcmsCst(0);
                                    next.setIcmsReduzido(0);
                                    break;

                                case "1800":
                                    next.setIcmsAliq(18);
                                    next.setIcmsCst(0);
                                    next.setIcmsReduzido(0);
                                    break;

                                case "2700":
                                    next.setIcmsAliq(27);
                                    next.setIcmsCst(0);
                                    next.setIcmsReduzido(0);
                                    break;

                                default:
                                    next.setIcmsAliq(0);
                                    next.setIcmsCst(40);
                                    next.setIcmsReduzido(0);
                                    break;
                            }
                        }

                    }
                }
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaItemIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {
            this.sql
                    = "select\n"
                    + "    (b.id_ecf || '' ||\n"
                    + "    b.id_data || '' ||\n"
                    + "    b.id_cupom || '' ||\n"
                    + "    b.id_final) as id_venda,\n"
                    + "    (b.id_ecf || '' || \n"
                    + "    b.id_data || '' || \n"
                    + "    b.id_cupom || '' || \n"
                    + "    icm || '' ||\n"
                    + "    id_seq || '' ||\n"
                    + "    c.total || '' ||\n"
                    + "    id_seq ) as id,\n"
                    + "    c.id_seq sequencia,\n"
                    + "    c.cod_prod id_produto,\n"
                    + "    c.cod_prod ean,\n"
                    + "    p.nome_produto descricao,\n"
                    + "    p.unm_emb embalagem,\n"
                    + "    c.qtd quantidade,\n"
                    + "    c.custo,\n"
                    + "    c.preco,\n"
                    + "    c.desconto,\n"
                    + "    c.acresc acrescimo,\n"
                    + "    c.total,\n"
                    + "    c.icm,\n"
                    + "    c.cst,\n"
                    + "    c.situacao cancelado,\n"
                    + "    i.tabicm_aliq aliquota,\n"
                    + "    i.tabicm_pbc reducao\n"
                    + "from\n"
                    + "    pdv_ecfs a,\n"
                    + "    pdv_mapaf b,\n"
                    + "    pdv_mapai c,\n"
                    + "    est_produtos p,\n"
                    + "    tab_icm i\n"
                    + "where\n"
                    + "    a.id_ecf = b.id_ecf and\n"
                    + "    a.id_ecf = c.id_ecf and\n"
                    + "    b.id_data = c.id_data and\n"
                    + "    b.id_cupom = c.id_cupom and\n"
                    + "    c.cod_prod = p.id_produto and\n"
                    + "    c.icm = i.tabicm_ecf and\n"
                    + "    b.id_final = 995 and\n"
                    + "    b.fin_loja = " + idLojaCliente + " and\n"
                    + "    b.id_data between '" + VendaIterator.FORMAT.format(dataInicio) + "' and '" + VendaIterator.FORMAT.format(dataTermino) + "'\n"
                    + "order by\n"
                    + "    c.id_data,\n"
                    + "    c.id_cupom,\n"
                    + "    c.id_seq";
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
