package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
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
import vrframework.classe.Util;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.parametro.Parametros;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;

/**
 *
 * @author Desenvolvimento
 */
public class GestorDAO extends InterfaceDAO implements MapaTributoProvider {

    private String complemento = "";

    public void setComplemento(String complemento) {
        this.complemento = complemento == null ? "" : complemento.trim();
    }

    @Override
    public String getSistema() {
        return "Gestor" + (!"".equals(complemento) ? " - " + complemento : "");
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                OpcaoProduto.FAMILIA,
                OpcaoProduto.FAMILIA_PRODUTO,
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.VALIDADE,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.QTD_EMBALAGEM_COTACAO,
                OpcaoProduto.QTD_EMBALAGEM_EAN,
                OpcaoProduto.ATIVO,
                OpcaoProduto.PESO_BRUTO,
                OpcaoProduto.PESO_LIQUIDO,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.ESTOQUE_MINIMO,
                OpcaoProduto.MARGEM,
                OpcaoProduto.VENDA_PDV,
                OpcaoProduto.PRECO,
                OpcaoProduto.CUSTO,
                OpcaoProduto.CUSTO_COM_IMPOSTO,
                OpcaoProduto.CUSTO_SEM_IMPOSTO,
                OpcaoProduto.NCM,
                OpcaoProduto.EXCECAO,
                OpcaoProduto.CEST,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.NATUREZA_RECEITA,
                OpcaoProduto.ICMS,
                OpcaoProduto.DATA_CADASTRO,
                OpcaoProduto.RECEITA
        ));
    }

    @Override
    public Set<OpcaoCliente> getOpcoesDisponiveisCliente() {
        return new HashSet<>(Arrays.asList(
                OpcaoCliente.DADOS,
                OpcaoCliente.ENDERECO,
                OpcaoCliente.VALOR_LIMITE,
                OpcaoCliente.CONTATOS,
                OpcaoCliente.RECEBER_CREDITOROTATIVO));
    }

    @Override
    public Set<OpcaoFornecedor> getOpcoesDisponiveisFornecedor() {
        return new HashSet<>(Arrays.asList(
                            OpcaoFornecedor.DADOS,
                            OpcaoFornecedor.ENDERECO,
                            OpcaoFornecedor.CONTATOS,
                            OpcaoFornecedor.PAGAR_FORNECEDOR));
    }
    
    

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + " codigo AS codigo,\n"
                    + " nome AS nome,\n"
                    + " fantasia AS nomefantasia,\n"
                    + " CNPJ_CPF  AS cpfcnpj\n"
                    + "FROM ST_CD_EMPRESAS \n"
                    + "ORDER BY 1"
            )) {
                while (rst.next()) {
                    result.add(
                            new Estabelecimento(
                                    rst.getString("codigo"),
                                    rst.getString("nomefantasia") + "-" + rst.getString("cpfcnpj")
                            )
                    );
                }
            }
        }
        return result;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select distinct "
                    + "    icm.tributacao as cst,\n"
                    + "    icm.icms as aliquota,\n"
                    + "    icm.icms_base_reducao as reducao\n"
                    + "from SM_CD_ES_PRODUTO_EF icm \n"
                    + "where icm.empresa = " + getLojaOrigem() + "\n"
                    + "and icm.uf = '" + Parametros.get().getUfPadraoV2().getSigla() + "'"
            )) {
                while (rst.next()) {
                    String id = rst.getString("cst") + "-" + rst.getString("aliquota") + rst.getString("reducao");
                    String descricao = id;
                    result.add(new MapaTributoIMP(id,
                            descricao,
                            rst.getInt("cst"),
                            rst.getDouble("aliquota"),
                            rst.getDouble("reducao")));
                }
            }
        }
        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    m1.cod as merc1,\n"
                    + "    m1.dsc as descricao_merc1,\n"
                    + "    m2.cod as merc2,\n"
                    + "    m2.dsc as descricao_merc2\n"
                    + "from sm_cd_es_departamento m1\n"
                    + "join sm_cd_es_grupo m2 on m2.dep = m1.cod"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rst.getString("merc1"));
                    imp.setMerc1Descricao(rst.getString("descricao_merc1"));
                    imp.setMerc2ID(rst.getString("merc2"));
                    imp.setMerc2Descricao(rst.getString("descricao_merc2"));
                    imp.setMerc3ID("1");
                    imp.setMerc3Descricao(imp.getMerc2Descricao());
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
                    "select\n"
                    + "    p.cod as id,\n"
                    + "    p.pd_balanca as balanca,\n"
                    + "    p.da_validade as validade,\n"
                    + "    coalesce(b.barras, p.cod) as codigobarras,\n"
                    + "    b.pdv_quantidade as qtdembalagem,\n"
                    + "    p.dsc as descricaocompleta,\n"
                    + "    p.rdz as descricaoreduzida,\n"
                    + "    p.pd_departamento as mercadologico1,\n"
                    + "    p.pd_grupo as mercadologico2,\n"
                    + "    '1' as mercadologico3,\n"
                    + "    p.pd_data,\n"
                    + "    p.data_c, \n"
                    + "    p.pd_unidade as tipoembalagem,\n"
                    + "    p.pd_cest as cest,\n"
                    + "    p.pd_ncm as ncm,\n"
                    + "    pc.pis_credito_tributacao as pisconfinsentrada,\n"
                    + "    pc.cofins_tributacao as piscofinssaida,\n"
                    + "    pc.pis_nat_receita_tab,\n"
                    + "    pc.pis_nat_receita_it,\n"
                    + "    pc.pis_nat_receita_it_var,\n"
                    + "    pr.custo_s_imp as custosemimposto,\n"
                    + "    pr.custo_c_imp as custocomimposto,\n"
                    + "    pr.preco as precovenda,\n"
                    + "    pr.margem_atual as margem,\n"
                    + "    pr.margem_minima as margemminima,\n"
                    + "    pr.margem_maxima as margemmaxima,\n"
                    + "    pr.estoque_minimo as estoqueminimo,\n"
                    + "    pr.estoque_maximo as estoquemaximo,\n"
                    + "    pr.estoque_fiscal as estoque ,\n"
                    + "    icm.tributacao as csticms,\n"
                    + "    icm.icms as aliqicms,\n"
                    + "    icm.icms_base_reducao as redicms\n"
                    + "from SM_CD_ES_PRODUTO p\n"
                    + "left join SM_CD_ES_PRODUTO_BAR b on b.cod = p.cod\n"
                    + "left join SM_CD_ES_PRODUTO_DNM pr on pr.cod = p.cod\n"
                    + "    and pr.empresa = " + getLojaOrigem() + "\n"
                    + "left join SM_CD_ES_PRODUTO_EF_F pc on pc.cod = p.cod\n"
                    + "    and pc.empresa = " + getLojaOrigem() + "\n"
                    + "left join SM_CD_ES_PRODUTO_EF icm on icm.cod = p.cod\n"
                    + "    and icm.empresa = " + getLojaOrigem() + "\n"
                    + "    and icm.uf = '" + Parametros.get().getUfPadraoV2().getSigla() + "'"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setEan(rst.getString("codigobarras"));
                    imp.seteBalanca(rst.getInt("balanca") == 1);
                    imp.setValidade(rst.getInt("validade"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
                    imp.setDataCadastro(rst.getDate("data_c"));
                    imp.setCodMercadologico1(rst.getString("mercadologico1"));
                    imp.setCodMercadologico2(rst.getString("mercadologico2"));
                    imp.setCodMercadologico3("1");
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setMargemMinima(rst.getDouble("margemminima"));
                    imp.setMargemMaxima(rst.getDouble("margemmaxima"));
                    imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
                    imp.setEstoqueMaximo(rst.getDouble("estoquemaximo"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getString("piscofinssaida"));
                    imp.setPiscofinsCstCredito(rst.getString("pisconfinsentrada"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("pis_nat_receita_tab"));

                    String idIcms = rst.getString("csticms") + "-" + rst.getString("aliqicms") + rst.getString("redicms");

                    imp.setIcmsDebitoId(idIcms);
                    imp.setIcmsDebitoForaEstadoId(idIcms);
                    imp.setIcmsDebitoForaEstadoNfId(idIcms);
                    imp.setIcmsCreditoId(idIcms);
                    imp.setIcmsCreditoForaEstadoId(idIcms);
                    imp.setIcmsConsumidorId(idIcms);

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        DateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT \n"
                    + " f.COD AS id,\n"
                    + " f.PD_NOME AS razao,\n"
                    + " f.PD_FANTASIA AS fantasia,\n"
                    + " f.PD_CNPJ_CPF AS cnpj_cpf,\n"
                    + " f.PD_IE AS ie_rg,\n"
                    + " f.PD_ENDERECO AS endereco,\n"
                    + " f.PD_NUMERO AS numero,\n"
                    + " scc.NOME AS cidade,\n"
                    + " scu.UF AS uf,\n"
                    + " f.PD_CEP AS cep,\n"
                    + " f.PD_BAIRRO AS bairro,\n"
                    + " f.PD_COMPLEMENTO AS complemento,\n"
                    + " f.PD_EMAIL AS email,\n"
                    + " f.PD_MOVEL AS celular,\n"
                    + " f.PD_FONE AS telefone1,\n"
                    + " f.EC_ENDERECO AS c_endereco,\n"
                    + " f.EC_BAIRRO AS c_bairro,\n"
                    + " scc.NOME AS c_cidade,\n"
                    + " f.EC_COMPLEMENTO AS c_complemento,\n"
                    + " f.EC_CEP AS c_cep,\n"
                    + " scu.UF AS c_uf,\n"
                    + " f.EC_NUMERO AS c_numero,\n"
                    + " f.OB_OBSERVACAO AS obs,\n"
                    + " f.DATA_C AS dtcadastro,\n"
                    + " f.PD_DTANASCCONST AS dtnascimento,\n"
                    + " scmmc.IP_PAI AS pai,\n"
                    + " scmmc.IP_MAE AS mae,\n"
                    + " scmmc.IP_CONJ_NOME AS conjuge,\n"
                    + " scmmc.IC_CO_LIMITE AS limite,\n"
                    + " scmmc.CO_SALARIO AS salario,\n"
                    + " scmmc.CO_EMPRESA AS empresa,\n"
                    + " scmmc.CO_CARGO AS profissao,\n"
                    + " CASE WHEN scms.DSC <> 'ATIVO' THEN 0\n"
                    + " ELSE 1\n"
                    + " END AS status,\n"
                    + " CASE WHEN scms.BLOQ <> 0 THEN 0\n"
                    + " ELSE 1 \n"
                    + " END AS bloqueado\n"
                    + "FROM SM_CD_MO_MOVIMENTADOR f\n"
                    + "LEFT JOIN ST_CD_CIDADES scc ON scc.CODIGO = f.PD_CIDADE \n"
                    + "LEFT JOIN ST_CD_UF scu ON scu.UF = scc.UF \n"
                    + "LEFT JOIN SM_CD_MO_SITUACAO scms ON scms.COD = f.PD_SITUACAO\n"
                    + "LEFT JOIN SM_CD_MO_MOVIMENTADOR_CL scmmc ON scmmc.COD = f.COD\n"
                    + "WHERE PD_CNPJ_CPF_TIPO = 1\n"
                    + "ORDER BY 1"
            )) {
                while (rs.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rs.getString("id"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));

                    imp.setDataNascimento(rs.getDate("dtnascimento"));

                    imp.setCnpj(rs.getString("cnpj_cpf"));
                    imp.setInscricaoestadual(rs.getString("ie_rg"));

                    imp.setTelefone(rs.getString("telefone1"));
                    //imp.setFax(rs.getString("telefone2"));
                    imp.setCelular(rs.getString("celular"));
                    imp.setEmail(rs.getString("email"));
                    imp.setDataCadastro(rs.getDate("dtcadastro"));
                    imp.setBloqueado(rs.getBoolean("bloqueado"));
                    imp.setAtivo(rs.getBoolean("status"));
                    imp.setObservacao("CLIENTE LOJA " + complemento + "..." + rs.getString("obs"));

                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setCep(rs.getString("cep"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("uf"));

                    imp.setCobrancaEndereco(rs.getString("c_endereco"));
                    imp.setCobrancaNumero(rs.getString("c_numero"));
                    imp.setCobrancaComplemento(rs.getString("c_complemento"));
                    imp.setCobrancaBairro(rs.getString("c_bairro"));
                    imp.setCobrancaCep(rs.getString("c_cep"));
                    imp.setCobrancaMunicipio(rs.getString("c_cidade"));
                    imp.setCobrancaUf(rs.getString("c_uf"));

                    imp.setNomePai(rs.getString("pai"));
                    imp.setNomeMae(rs.getString("mae"));
                    imp.setNomeConjuge(rs.getString("conjuge"));
                    imp.setCargo(rs.getString("profissao"));
                    imp.setSalario(rs.getDouble("salario"));
                    imp.setValorLimite(rs.getDouble("limite"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "	cb.lancto id,\n" +
                    "	cb.empresa idempresa,\n" +
                    "	em.nome as empresa,\n" +
                    "	cb.movimentador idcliente,\n" +
                    "	fo.pd_nome as razao,\n" +
                    "	fo.pd_cnpj_cpf_tipo as tipocliente,\n" +
                    "	fo.pd_cnpj_cpf as cnpj_cpf,\n" +
                    "	pa.parcela,\n" +
                    "	cb.ref,\n" +
                    "	tc.dsc as cobranca,\n" +
                    "	cb.emissao,\n" +
                    "	pa.vencimento,\n" +
                    "	pa.documento,\n" +
                    "	cb.historico,\n" +
                    "	cb.valor_total,\n" +
                    "	pa.valor as valor_parcela,\n" +
                    "	cb.tipo_moeda,\n" +
                    "	pa.valor_moeda as valor_parcela_moeda,\n" +
                    "	pa.baixa,\n" +
                    "	pa.valor_baixa,\n" +
                    "	pa.valor_baixa_corrigido,\n" +
                    "	pa.multa,\n" +
                    "	pa.juro,\n" +
                    "	pa.desconto,\n" +
                    "	pa.variacao_moeda,\n" +
                    "	pa.valor_baixa_quita,\n" +
                    "	cb.observacao\n" +
                    "from\n" +
                    "	sm_mv_fi_tl_pa_titulo pa\n" +
                    "join st_cd_empresas em on\n" +
                    "	pa.empresa = em.codigo\n" +
                    "join sm_mv_fi_tl_cb_titulo cb on\n" +
                    "	pa.empresa = cb.empresa\n" +
                    "	and pa.lancto = cb.lancto\n" +
                    "join sm_cd_mo_movimentador fo on\n" +
                    "	cb.movimentador = fo.cod\n" +
                    "join sm_cd_mo_movimentador_fo_a fa on\n" +
                    "	cb.empresa = fa.empresa\n" +
                    "	and cb.movimentador = fa.cod\n" +
                    "join sm_cd_mo_movimentador_fo_e fe on\n" +
                    "	cb.empresa = fe.empresa\n" +
                    "	and cb.movimentador = fe.cod\n" +
                    "join sm_cd_fi_tpcobranca tc on\n" +
                    "	cb.empresa = tc.empresa\n" +
                    "	and cb.tipo_cobranca = tc.cod\n" +
                    "left join sm_cd_fi_operacoes_lancto op on\n" +
                    "	cb.empresa = op.empresa\n" +
                    "	and cb.operacao = op.cod\n" +
                    "left join sm_cd_mo_tipo tf on\n" +
                    "	fo.pd_tipo = tf.cod\n" +
                    "left join sm_cd_fi_banco bc on\n" +
                    "	fa.ad_banco = bc.cod\n" +
                    "left join sm_cd_fi_contrato cn on\n" +
                    "	cb.empresa = cn.empresa\n" +
                    "	and cb.contrato = cn.cod\n" +
                    "left join sm_mv_fi_tl_pa_at_titulo ap on\n" +
                    "	pa.empresa = ap.empresa\n" +
                    "	and pa.lancto = ap.lancto\n" +
                    "	and pa.parcela = ap.parcela\n" +
                    "	and pa.origem = ap.origem\n" +
                    "left join sm_cd_fi_formapgto fp on\n" +
                    "	cb.empresa = fp.empresa\n" +
                    "	and cb.formapgto = fp.cod\n" +
                    "left join sm_cd_fi_cb_condicaopgto cg on\n" +
                    "	cb.empresa = cg.empresa\n" +
                    "	and cb.condicaopgto = cg.cod\n" +
                    "where fo.pd_cnpj_cpf_tipo = 1 and \n" +
                    "	cb.empresa = " + getLojaOrigem() + " and\n" + 
                    "	pa.valor_baixa < pa.valor")) {
                while(rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    
                    imp.setId(rs.getString("id"));
                    imp.setIdCliente(rs.getString("idcliente"));
                    imp.setCnpjCliente(rs.getString("cnpj_cpf"));
                    imp.setDataEmissao(rs.getDate("emissao"));
                    imp.setDataVencimento(rs.getDate("vencimento"));
                    imp.setValor(Util.round(rs.getDouble("valor_parcela") - rs.getDouble("valor_baixa"), 2));
                    imp.setParcela(rs.getInt("parcela"));
                    imp.setObservacao(rs.getString("historico"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<ContaPagarIMP> getContasPagar() throws Exception {
        List<ContaPagarIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "	cb.lancto id,\n" +
                    "	cb.empresa idempresa,\n" +
                    "	em.nome as empresa,\n" +
                    "	cb.movimentador idfornecedor,\n" +
                    "	fo.pd_nome as razao,\n" +
                    "	fo.pd_cnpj_cpf_tipo as tipo,\n" +
                    "	fo.pd_cnpj_cpf as cnpj_cpf,\n" +
                    "	pa.parcela,\n" +
                    "	cb.ref,\n" +
                    "	tc.dsc as cobranca,\n" +
                    "	cb.emissao,\n" +
                    "	pa.vencimento,\n" +
                    "	pa.documento,\n" +
                    "	cb.historico,\n" +
                    "	cb.valor_total,\n" +
                    "	pa.valor as valor_parcela,\n" +
                    "	cb.tipo_moeda,\n" +
                    "	pa.valor_moeda as valor_parcela_moeda,\n" +
                    "	pa.baixa,\n" +
                    "	pa.valor_baixa,\n" +
                    "	pa.valor_baixa_corrigido,\n" +
                    "	pa.multa,\n" +
                    "	pa.juro,\n" +
                    "	pa.desconto,\n" +
                    "	pa.variacao_moeda,\n" +
                    "	pa.valor_baixa_quita,\n" +
                    "	cb.observacao\n" +
                    "from\n" +
                    "	sm_mv_fi_tl_pa_titulo pa\n" +
                    "join st_cd_empresas em on\n" +
                    "	pa.empresa = em.codigo\n" +
                    "join sm_mv_fi_tl_cb_titulo cb on\n" +
                    "	pa.empresa = cb.empresa\n" +
                    "	and pa.lancto = cb.lancto\n" +
                    "join sm_cd_mo_movimentador fo on\n" +
                    "	cb.movimentador = fo.cod\n" +
                    "join sm_cd_mo_movimentador_fo_a fa on\n" +
                    "	cb.empresa = fa.empresa\n" +
                    "	and cb.movimentador = fa.cod\n" +
                    "join sm_cd_mo_movimentador_fo_e fe on\n" +
                    "	cb.empresa = fe.empresa\n" +
                    "	and cb.movimentador = fe.cod\n" +
                    "join sm_cd_fi_tpcobranca tc on\n" +
                    "	cb.empresa = tc.empresa\n" +
                    "	and cb.tipo_cobranca = tc.cod\n" +
                    "left join sm_cd_fi_operacoes_lancto op on\n" +
                    "	cb.empresa = op.empresa\n" +
                    "	and cb.operacao = op.cod\n" +
                    "left join sm_cd_mo_tipo tf on\n" +
                    "	fo.pd_tipo = tf.cod\n" +
                    "left join sm_cd_fi_banco bc on\n" +
                    "	fa.ad_banco = bc.cod\n" +
                    "left join sm_cd_fi_contrato cn on\n" +
                    "	cb.empresa = cn.empresa\n" +
                    "	and cb.contrato = cn.cod\n" +
                    "left join sm_mv_fi_tl_pa_at_titulo ap on\n" +
                    "	pa.empresa = ap.empresa\n" +
                    "	and pa.lancto = ap.lancto\n" +
                    "	and pa.parcela = ap.parcela\n" +
                    "	and pa.origem = ap.origem\n" +
                    "left join sm_cd_fi_formapgto fp on\n" +
                    "	cb.empresa = fp.empresa\n" +
                    "	and cb.formapgto = fp.cod\n" +
                    "left join sm_cd_fi_cb_condicaopgto cg on\n" +
                    "	cb.empresa = cg.empresa\n" +
                    "	and cb.condicaopgto = cg.cod\n" +
                    "where fo.pd_cnpj_cpf_tipo = 0 and \n" +
                    "	cb.empresa = " + getLojaOrigem() + " AND \n" +
                    "	pa.valor_baixa < pa.valor")) {
                while(rs.next()) {
                    ContaPagarIMP imp = new ContaPagarIMP();
                    
                    imp.setId(rs.getString("id"));
                    imp.setIdFornecedor(rs.getString("idfornecedor"));
                    imp.setDataEmissao(rs.getDate("emissao"));
                    imp.addVencimento(rs.getDate("vencimento"), rs.getDouble("valor_parcela"), rs.getInt("parcela"));
                    imp.setCnpj(rs.getString("cnpj_cpf"));
                    imp.setNumeroDocumento(rs.getString("documento"));
                    imp.setObservacao(rs.getString("historico"));
                    
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
                    "SELECT \n"
                    + " f.COD AS fornecedorid,\n"
                    + " f.PD_NOME AS razao,\n"
                    + " f.PD_FANTASIA AS fantasia,\n"
                    + " f.PD_CNPJ_CPF AS cnpj_cpf,\n"
                    + " f.PD_IE AS ie_rg,\n"
                    + " f.PD_ENDERECO AS endereco,\n"
                    + " f.PD_NUMERO AS numero,\n"
                    + " scc.NOME AS municipio,\n"
                    + " scu.UF AS uf,\n"
                    + " f.PD_CEP AS cep,\n"
                    + " f.PD_BAIRRO AS bairro,\n"
                    + " f.PD_COMPLEMENTO AS complemento,\n"
                    + " f.PD_EMAIL AS email,\n"
                    + " f.PD_MOVEL AS cel,\n"
                    + " f.PD_FONE AS tel,\n"
                    + " f.OB_OBSERVACAO as observacao,\n"
                    + " f.PD_DATA,\n"
                    + " f.DATA_C as dtcadastro,\n"
                    + " CASE WHEN scms.DSC <> 'ATIVO' THEN 0\n"
                    + " ELSE 1\n"
                    + " END AS status,\n"
                    + " CASE WHEN scms.BLOQ <> 0 THEN 0\n"
                    + " ELSE 1 \n"
                    + " END AS bloqueado\n"
                    + "FROM SM_CD_MO_MOVIMENTADOR f\n"
                    + "LEFT JOIN ST_CD_CIDADES scc ON scc.CODIGO = f.PD_CIDADE \n"
                    + "LEFT JOIN ST_CD_UF scu ON scu.UF = scc.UF \n"
                    + "LEFT JOIN SM_CD_MO_SITUACAO scms ON scms.COD = f.PD_SITUACAO\n"
                    + "WHERE PD_CNPJ_CPF_TIPO = 0\n"
                    + "ORDER BY 1"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("fornecedorid"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("cnpj_cpf"));
                    imp.setIe_rg(rst.getString("ie_rg"));
                    imp.setAtivo(rst.getBoolean("status"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setCep(rst.getString("cep"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setUf(rst.getString("uf"));
                    imp.setTel_principal(rst.getString("tel"));
                    imp.setDatacadastro(rst.getDate("dtcadastro"));
                    imp.setObservacao(rst.getString("observacao"));
                    imp.setBloqueado(rst.getBoolean("bloqueado"));

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
                    "select\n"
                    + "    cod as idproduto,\n"
                    + "    fornecedor as idfornecedor,\n"
                    + "    unidade_quantidade as qtd,\n"
                    + "    ref as codigoexterno\n"
                    + "from SM_CD_ES_PRODUTO_REF\n"
                    + "order by 2, 1"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rst.getString("idproduto"));
                    imp.setIdFornecedor(rst.getString("idfornecedor"));
                    imp.setCodigoExterno(rst.getString("codigoexterno"));
                    imp.setQtdEmbalagem(rst.getDouble("qtd") <= 0 ? 1 : rst.getDouble("qtd"));
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
        return new GestorDAO.VendaIterator(getLojaOrigem(), this.dataInicioVenda, this.dataTerminoVenda);
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        return new GestorDAO.VendaItemIterator(getLojaOrigem(), this.dataInicioVenda, this.dataTerminoVenda);
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
                        
                        String horaInicio = timestampDate.format(rst.getDate("data")) + " " + rst.getString("horainicio");
                        String horaTermino = timestampDate.format(rst.getDate("data")) + " " + rst.getString("horatermino");
                        next.setHoraInicio(timestamp.parse(horaInicio));
                        next.setHoraTermino(timestamp.parse(horaTermino));
                        
                        next.setSubTotalImpressora(rst.getDouble("subtotalimpressora"));
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
                    + "	REPLACE(v.EMPRESA||v.DATA||v.PDV||v.ECF||v.CUPOM,'-','') as id_venda,\n"
                    + "	v.cupom numerocupom,\n"
                    + "	v.ecf,\n"
                    + "	v.data data,\n"
                    + "	v.hora_gravacao horainicio,\n"
                    + "	v.hora horatermino,\n"
                    + "	v.vlr_liquido subtotalimpressora\n"
                    + "FROM\n"
                    + "	SM_MV_PDV_CB_CUP v\n"
                    + "WHERE\n"
                    + "	v.empresa = " + idLojaCliente + "\n"
                    + "	AND v.data BETWEEN '" + strDataInicio + "' and '" + strDataTermino + "'\n"
                    + "ORDER BY 1";
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
                    + "	REPLACE(v.EMPRESA||v.DATA||v.PDV||v.ECF||v.CUPOM,'-','') as id_venda,\n"
                    + "	REPLACE(vi.EMPRESA || vi.DATA || vi.PDV || vi.ECF || vi.CUPOM || vi.PRODUTO || vi.ITEM, '-', '') AS id_item,\n"
                    + "	vi.ITEM nroitem, \n"
                    + "	vi.PRODUTO,\n"
                    + "	p.PD_UNIDADE,\n"
                    + "	vi.PRODUTO_BARRAS codigobarras,\n"
                    + "	p.DSC descricao,\n"
                    + "	vi.QUANTIDADE,\n"
                    + "	vi.VLR_UNT precovenda,\n"
                    + "	vi.VALOR_REAL total\n"
                    + "FROM\n"
                    + "	SM_MV_PDV_IT_CUP vi\n"
                    + "JOIN SM_MV_PDV_CB_CUP v\n"
                    + "	ON v.CUPOM = vi.CUPOM \n"
                    + "	  AND v.EMPRESA = vi.EMPRESA\n"
                    + "	  AND v.DATA = vi.DATA\n"
                    + "	  AND v.PDV = vi.PDV\n"
                    + "	  AND v.ECF = vi.ECF\n"
                    + "JOIN SM_CD_ES_PRODUTO p ON p.COD = vi.PRODUTO \n"
                    + "WHERE\n"
                    + "	v.EMPRESA = " + idLojaCliente + "\n"
                    + "	AND v.DATA BETWEEN '" + VendaIterator.FORMAT.format(dataInicio) + "' and '" + VendaIterator.FORMAT.format(dataTermino) + "'\n"
                    + "ORDER BY 1,3";
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
