package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Desenvolvimento
 */
public class GestorDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "Gestor";
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
                    + "    icm.icms_reducao as reducao\n"
                    + " SM_CD_ES_PRODUTO_EF icm \n"
                    + "where icm.empresa = " + getLojaOrigem() + "\n"
                    + "and icm.uf = 'MS'"
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
                    + "    pr.estoque_fiscal as estque ,\n"
                    + "    icm.tributacao as csticms,\n"
                    + "    icm.icms as aliqicms,\n"
                    + "    icm.icms_reducao as redicms\n"
                    + "from SM_CD_ES_PRODUTO p\n"
                    + "left join SM_CD_ES_PRODUTO_BAR b on b.cod = p.cod\n"
                    + "left join SM_CD_ES_PRODUTO_DNM pr on pr.cod = p.cod\n"
                    + "    and pr.empresa = " + getLojaOrigem() + "\n"
                    + "left join SM_CD_ES_PRODUTO_EF_F pc on pc.cod = p.cod\n"
                    + "    and pc.empresa = " + getLojaOrigem() + "\n"
                    + "left join SM_CD_ES_PRODUTO_EF icm on icm.cod = p.cod\n"
                    + "    and icm.empresa = " + getLojaOrigem() + "\n"
                    + "    and icm.uf = 'MS'"
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

                    imp.setCnpj(rs.getString("cpfcnpj"));
                    imp.setInscricaoestadual(rs.getString("inscricao"));

                    imp.setTelefone(rs.getString("telefone1"));
                    //imp.setFax(rs.getString("telefone2"));
                    imp.setCelular(rs.getString("celular"));
                    imp.setEmail(rs.getString("email"));
                    imp.setDataCadastro(rs.getDate("datacadastro"));
                    imp.setBloqueado(rs.getBoolean("bloqueado"));
                    imp.setAtivo(rs.getBoolean("status"));
                    imp.setObservacao(rs.getString("obs"));

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
                    imp.setValorLimite(rs.getDouble("valorlimite"));

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
}
