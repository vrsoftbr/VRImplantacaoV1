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
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.OpcaoFiscal;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.PautaFiscalIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;

/**
 *
 * @author Importacao
 */
public class WeberDAO extends InterfaceDAO implements MapaTributoProvider {

    private static final Logger LOG = Logger.getLogger(WeberDAO.class.getName());

    @Override
    public String getSistema() {
        return "Weber";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                new OpcaoProduto[]{
                    OpcaoProduto.MERCADOLOGICO,
                    OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                    OpcaoProduto.MERCADOLOGICO_PRODUTO,
                    OpcaoProduto.FAMILIA,
                    OpcaoProduto.FAMILIA_PRODUTO,
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
                    OpcaoProduto.PAUTA_FISCAL,
                    OpcaoProduto.PAUTA_FISCAL_PRODUTO,
                    OpcaoProduto.MARGEM
                }
        ));
    }

    public List<Estabelecimento> getLojaCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "    id_loja,\n"
                    + "    fantasia\n"
                    + "from\n"
                    + "    loja"
            )) {
                while (rs.next()) {
                    result.add(new Estabelecimento(rs.getString("id_loja"), rs.getString("fantasia")));
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
                    "select\n"
                    + "    p.id_produto as importid,\n"
                    + "    p.data_cadastro as datacadastro,\n"
                    + "    p.data_alteracao as dataalteracao,\n"
                    + "    e.est_max estmaximo,\n"
                    + "    e.est_min estminimo,\n"
                    + "    e.est_atual estoque,\n"
                    + "    p.balanca,\n"
                    + "    replace(p.situacao, '*', 1) situacao,\n"
                    + "    p.unm_emb_qtd as qtdembalagem,\n"
                    + "    p.unm as tipoembalagem,\n"
                    + "    p.dias_validade as validade,\n"
                    + "    nome_produto as descricaocompleta,\n"
                    + "    nome_reduzido as descricaoreduzida,\n"
                    + "    nome_reduzido as descricaogondola,\n"
                    + "    peso_bruto as pesobruto,\n"
                    + "    peso_liquido as pesoliquido,\n"
                    + "    perc_margem as margem,\n"
                    + "    preco_custo as custosemimposto,\n"
                    + "    preco_venda as precovenda,\n"
                    + "    ncm,\n"
                    + "    cest,\n"
                    + "    cofinse_aliq as piscofinscstdebito,\n"
                    + "    pis_cofins_entrada as piscofinscstcredito,\n"
                    + "    pis_nat_rec as piscofinsnaturezareceita,\n"
                    + "    icm_cst as icms_cst_credito,\n"
                    + "    icm_aliq as icms_credito,\n"
                    + "    icm_pbc icms_reducao_credito,\n"
                    + "    icm.tabicm_st icms_cst_debito,\n"
                    + "    icm.tabicm_aliq icms_debito,\n"
                    + "    icm.tabicm_pbc icms_reducao_debito,\n"
                    + "    tipo_prod as tipoproduto,\n"
                    + "    p.icm_mva\n"
                    + "from\n"
                    + "    est_produtos p\n"
                    + "left join est_atual e on p.id_produto = e.id_produto and\n"
                    + "    e.id_loja = " + getLojaOrigem() + "\n"
                    + "left join tab_icm icm on p.tabicm = icm.id_tabicm\n"
                    + "order by\n"
                    + "    p.id_produto"
            )) {
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("importid"));
                    imp.setEan(imp.getImportId());
                    imp.setDescricaoCompleta(rs.getString("descricaocompleta"));
                    if (rs.getString("descricaoreduzida") == null && "".equals(rs.getString("descricaoreduzida"))) {
                        imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    } else {
                        imp.setDescricaoReduzida(rs.getString("descricaoreduzida"));
                    }
                    imp.setDescricaoGondola(rs.getString("descricaocompleta"));
                    imp.setEstoque(rs.getDouble("estoque"));
                    imp.setEstoqueMaximo(rs.getDouble("estmaximo"));
                    imp.setEstoqueMinimo(rs.getDouble("estminimo"));
                    imp.setDataCadastro(rs.getDate("datacadastro"));
                    imp.setDataAlteracao(rs.getDate("dataalteracao"));
                    imp.seteBalanca("S".equals(rs.getString("balanca")));
                    imp.setValidade(rs.getInt("validade"));
                    imp.setSituacaoCadastro(rs.getInt("situacao") == 1 ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    imp.setQtdEmbalagem(rs.getInt("qtdembalagem"));
                    imp.setTipoEmbalagem(rs.getString("tipoembalagem"));
                    imp.setPesoBruto(rs.getDouble("pesobruto"));
                    imp.setPesoLiquido(rs.getDouble("pesoliquido"));
                    imp.setMargem(rs.getDouble("margem"));
                    imp.setCustoComImposto(rs.getDouble("custosemimposto"));
                    imp.setCustoSemImposto(rs.getDouble("custosemimposto"));
                    imp.setPrecovenda(rs.getDouble("precovenda"));
                    imp.setNcm(rs.getString("ncm"));
                    imp.setCest(rs.getString("cest"));
                    imp.setPiscofinsCstCredito(rs.getString("piscofinscstcredito"));
                    imp.setPiscofinsCstDebito(rs.getString("piscofinscstdebito"));
                    imp.setPiscofinsNaturezaReceita(rs.getString("piscofinsnaturezareceita"));

                    //Aliquota de saída
                    imp.setIcmsAliqSaida(rs.getDouble("icms_debito"));
                    imp.setIcmsCstSaida(rs.getInt("icms_cst_debito"));

                    double reducao = rs.getDouble("icms_reducao_debito");
                    imp.setIcmsReducaoSaida(reducao == 100 ? 0 : reducao);

                    //Alíquota de saída fora estado
                    imp.setIcmsAliqSaidaForaEstado(rs.getDouble("icms_debito"));
                    imp.setIcmsCstSaidaForaEstado(rs.getInt("icms_cst_debito"));
                    imp.setIcmsReducaoSaidaForaEstado(reducao == 100 ? 0 : reducao);

                    //Aliquota de entrada
                    imp.setIcmsAliqEntrada(rs.getDouble("icms_credito"));
                    imp.setIcmsCstEntrada(Integer.parseInt(Utils.formataNumero(rs.getString("icms_cst_credito"))));

                    reducao = rs.getDouble("icms_reducao_credito");
                    imp.setIcmsReducaoEntrada(reducao == 100 ? 0 : reducao);

                    //Aliquota de entrada fora estado
                    imp.setIcmsAliqEntradaForaEstado(rs.getDouble("icms_credito"));
                    imp.setIcmsCstEntradaForaEstado(Integer.parseInt(Utils.formataNumero(rs.getString("icms_cst_credito"))));
                    imp.setIcmsReducaoEntradaForaEstado(reducao == 100 ? 0 : reducao);

                    //Pauta Fiscal
                    imp.setPautaFiscalId(imp.getImportId());

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
                    imp.setAliquotaDebito(rst.getInt("cst_debito"), rst.getDouble("icms_aliquota_debito"), 0.0);
                    imp.setAliquotaDebitoForaEstado(rst.getInt("cst_debito"), rst.getDouble("icms_aliquota_debito"), 0.0);
                    double reducao = 0;
                    if (rst.getDouble("aliquota_reducao_credito") == 100) {
                        reducao = 0;
                    } else {
                        reducao = rst.getDouble("aliquota_reducao_credito");
                    }
                    imp.setAliquotaCredito(0, rst.getDouble("aliquota_credito"), reducao);
                    imp.setAliquotaCreditoForaEstado(0, rst.getDouble("aliquota_credito"), reducao);

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
                    "select\n"
                    + "    c.id_cliente as id,\n"
                    + "    c.cnpj_cpf as cnpj,\n"
                    + "    c.ie_rg as inscricaoestadual,\n"
                    + "    c.nome_razao as razao,\n"
                    + "    c.fantasia,\n"
                    + "    c.bloqueado,\n"
                    + "    c.endereco,\n"
                    + "    c.numero,\n"
                    + "    c.endcomplmto as complemento,\n"
                    + "    c.bairro,\n"
                    + "    c.cod_cidade as municipioibge,\n"
                    + "    c.cidade as municipio,\n"
                    + "    c.uf,\n"
                    + "    c.cep,\n"
                    + "    c.data_nascimento as datanascimento,\n"
                    + "    c.data_cadastro as datacadastro,\n"
                    + "    c.sexo as tiposexo,\n"
                    + "    c2.empr_atual as empresa,\n"
                    + "    c2.empr_endereco as empresaendereco,\n"
                    + "    c2.empr_bairro as empresabairro,\n"
                    + "    c2.empr_cidade as empresamunicipio,\n"
                    + "    c2.empr_fone as empresatelefone,\n"
                    + "    c2.empr_data_admissao as dataadmissao,\n"
                    + "    c2.empr_funcao as cargo,\n"
                    + "    c2.empr_rendimentos as salario,\n"
                    + "    c.vlr_limite as valorlimite,\n"
                    + "    c2.conj_nome as nomeconjuge,\n"
                    + "    c2.nome_pai as nomepai,\n"
                    + "    c2.nome_mae as nomemae,\n"
                    + "    c.obs_memo as observacao,\n"
                    + "    c.dia_vcto as diavencimento,\n"
                    + "    c.cred_rotativo as permitecreditorotativo,\n"
                    + "    c.fone1 as telefone,\n"
                    + "    c.fone2 as celular,\n"
                    + "    c.email as email,\n"
                    + "    c.vlr_limite as limitecompra,\n"
                    + "    c.inscr_municipal as inscricaomunicipal,\n"
                    + "    c.situacao indicadorie\n"
                    + "from\n"
                    + "    clie_dados c\n"
                    + "    left join clie_compl1 as c2\n"
                    + "        on c.id_cliente = c2.id_cliente\n"
                    + "where\n"
                    + "    upper(tipo_forn) = 'S'\n"
                    + "order by 1"
            )) {
                while (rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setCnpj_cpf(Utils.formataNumero(rs.getString("cnpj")));
                    imp.setIe_rg(rs.getString("inscricaoestadual"));
                    //imp.setAtivo(rs.getInt("situacao") == 1);
                    imp.setAtivo("N".equals(rs.getString("bloqueado")));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("municipio"));
                    imp.setIbge_municipio(rs.getInt("municipioibge"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));
                    imp.setDatacadastro(rs.getDate("datacadastro"));
                    imp.setCondicaoPagamento(rs.getInt("diavencimento"));
                    imp.setTel_principal(rs.getString("telefone"));
                    if (rs.getString("celular") != null && !"".equals(rs.getString("celular"))) {
                        imp.addContato("1", "CELULAR", null, rs.getString("celular"), TipoContato.NFE, null);
                    }
                    if (rs.getString("email") != null && !"".equals(rs.getString("email"))) {
                        imp.addContato("2", "EMAIL", null, null, TipoContato.NFE, rs.getString("email"));
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
                    "select\n"
                    + "    c.id_cliente as id,\n"
                    + "    c.cnpj_cpf as cnpj,\n"
                    + "    c.ie_rg as inscricaoestadual,\n"
                    + "    c.nome_razao as razao,\n"
                    + "    c.fantasia,\n"
                    + "    c.bloqueado,\n"
                    + "    c.endereco,\n"
                    + "    c.numero,\n"
                    + "    c.endcomplmto as complemento,\n"
                    + "    c.bairro,\n"
                    + "    c.cod_cidade as municipioibge,\n"
                    + "    c.cidade as municipio,\n"
                    + "    c.uf,\n"
                    + "    c.cep,\n"
                    + "    c.data_nascimento as datanascimento,\n"
                    + "    c.data_cadastro as datacadastro,\n"
                    + "    c.sexo as tiposexo,\n"
                    + "    c2.empr_atual as empresa,\n"
                    + "    c2.empr_endereco as empresaendereco,\n"
                    + "    c2.empr_bairro as empresabairro,\n"
                    + "    c2.empr_cidade as empresamunicipio,\n"
                    + "    c2.empr_fone as empresatelefone,\n"
                    + "    c2.empr_data_admissao as dataadmissao,\n"
                    + "    c2.empr_funcao as cargo,\n"
                    + "    c2.empr_rendimentos as salario,\n"
                    + "    c.vlr_limite as valorlimite,\n"
                    + "    c2.conj_nome as nomeconjuge,\n"
                    + "    c2.nome_pai as nomepai,\n"
                    + "    c2.nome_mae as nomemae,\n"
                    + "    c.obs_memo as observacao,\n"
                    + "    c.dia_vcto as diavencimento,\n"
                    + "    c.cred_rotativo as permitecreditorotativo,\n"
                    + "    c.fone1 as telefone,\n"
                    + "    c.fone2 as celular,\n"
                    + "    c.email as email,\n"
                    + "    c.vlr_limite as limitecompra,\n"
                    + "    c.dia_vcto vencimentorotativo,\n"
                    + "    c.inscr_municipal as inscricaomunicipal,\n"
                    + "    c.situacao as tipoindicadorie\n"
                    + "from\n"
                    + "    clie_dados c\n"
                    + "    left join clie_compl1 as c2\n"
                    + "        on c.id_cliente = c2.id_cliente\n"
                    + "where\n"
                    + "    upper(tipo_clie) = 'S'\n"
                    + "order by 1"
            )) {
                while (rs.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rs.getString("id"));
                    imp.setCnpj(Utils.formataNumero(rs.getString("cnpj")));
                    imp.setInscricaoestadual(rs.getString("inscricaoestadual"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setAtivo("N".equals(rs.getString("bloqueado")));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("municipio"));
                    imp.setMunicipioIBGE(rs.getInt("municipioibge"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));
                    imp.setDataNascimento(rs.getDate("datanascimento"));
                    imp.setDataCadastro(rs.getDate("datacadastro"));
                    imp.setValorLimite(rs.getDouble("valorlimite"));
                    imp.setTelefone(rs.getString("telefone"));
                    imp.setCelular(rs.getString("celular"));
                    imp.setEmail(rs.getString("email"));
                    imp.setDiaVencimento(rs.getInt("vencimentorotativo"));

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
                    "select\n"
                    + "    c.id_cliente as idFornecedor,\n"
                    + "    cf.cod_prod as idProduto,\n"
                    + "    fator as qtdEmbalagem,\n"
                    + "    cf.id_cod codigoexterno\n"
                    + "from\n"
                    + "    codigo_fornec cf\n"
                    + "        left join clie_dados c\n"
                    + "            on cf.id_cnpj = replace(replace(replace(c.cnpj_cpf,'.',''), '/', ''), '-', '')\n"
                    + "where\n"
                    + "    cf.cod_prod is not null\n"
                    + "order by\n"
                    + "    idFornecedor"
            )) {
                while (rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdFornecedor(rs.getString("idFornecedor"));
                    imp.setIdProduto(rs.getString("idProduto"));
                    imp.setQtdEmbalagem(rs.getDouble("qtdEmbalagem"));
                    imp.setCodigoExterno(rs.getString("codigoexterno"));

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
                    + "    loja = " + getLojaOrigem() + "\n"
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

    @Override
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
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "    id_tabicm id,\n"
                    + "    tabicm_descricao descricao\n"
                    + "from\n"
                    + "    tab_icm"
            )) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(rs.getString("id"), rs.getString("descricao")));
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
                    + "    b.id_final || '' ||\n"
                    + "    id_seq || '' ||\n"
                    + "    c.total) as id,\n"
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
