package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Desenvolvimento
 */
public class GComDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "GCom";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.MAPA_TRIBUTACAO,
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.PRODUTOS_BALANCA,
                OpcaoProduto.DATA_CADASTRO,
                OpcaoProduto.DATA_ALTERACAO,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.QTD_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.VALIDADE,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.PESO_BRUTO,
                OpcaoProduto.PESO_LIQUIDO,
                OpcaoProduto.ESTOQUE_MINIMO,
                OpcaoProduto.ESTOQUE_MAXIMO,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.MARGEM,
                OpcaoProduto.PRECO,
                OpcaoProduto.ATIVO,
                OpcaoProduto.NCM,
                OpcaoProduto.CEST,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.ICMS,
                OpcaoProduto.PAUTA_FISCAL,
                OpcaoProduto.PAUTA_FISCAL_PRODUTO,
                OpcaoProduto.EXCECAO,
                OpcaoProduto.CODIGO_BENEFICIO
        ));
    }
    
    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "    t.tr_codigo||'-'||t.tr_descricao||'-'||t.tr_aliquota||'-'||coalesce(t.tr_identificador, '') AS id,\n"
                    + "    t.tr_descricao AS descricao\n"
                    + "FROM CR_TRIBUTACAOVENDA t\n"
                    + "UNION ALL\n"
                    + "SELECT\n"
                    + "    t.tr_codigo||'-'||t.tr_descricao||'-'||t.tr_aliquota AS id,\n"
                    + "    t.tr_descricao AS descricao\n"
                    + "FROM CR_TRIBUTACAOCOMPRA t\n"
                    + "ORDER BY 1"
            )) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(rs.getString("id"),
                            rs.getString("descricao")
                        )
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
            try (ResultSet rst = stm.executeQuery(
                    "SELECT DISTINCT\n"
                    + "    p.PR_SETOR AS merc1,\n"
                    + "    p.PR_GRUPO AS merc2\n"
                    + "FROM CR_PRODUTOS p\n"
                    + "WHERE p.PR_SETOR != ''\n"
                    + "ORDER BY 1, 2"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setMerc1ID(rst.getString("merc1"));
                    imp.setMerc1Descricao(rst.getString("merc1"));

                    if (rst.getString("merc2") == null || rst.getString("merc2").trim().isEmpty()) {
                        imp.setMerc2ID(rst.getString("merc1"));
                        imp.setMerc2Descricao(rst.getString("merc1"));
                    } else {
                        imp.setMerc2ID(rst.getString("merc2"));
                        imp.setMerc2Descricao(rst.getString("merc2"));
                    }

                    imp.setMerc3ID("1");
                    imp.setMerc3Descricao(rst.getString("merc2"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        String icmsCreditoId, icmsDeditoId;
        
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "    p.pr_idl AS id,\n"
                    + "    p.pr_codebar AS codigobarras,\n"
                    + "    CASE p.pr_balanca WHEN 'S' THEN 1 ELSE 0 END ebalanca,\n"
                    + "    p.pr_descricao AS descricaocompleta,\n"
                    + "    p.pr_descrred AS descricaoreduzida,\n"
                    + "    p.PR_SETOR AS merc1,\n"
                    + "    p.PR_GRUPO AS merc2,\n"        
                    + "    p.pr_precovenda AS precovenda,\n"
                    + "    p.pr_precocusto AS custo,\n"
                    + "    p.pr_margemlucro AS margem,\n"
                    + "    p.pr_unidade AS tipoembalagem,\n"
                    + "    p.pr_estoqueminimo AS estoqueminimo,\n"
                    + "    p.pr_estoquemaximo AS estoquemaximo,\n"
                    + "    p.pr_estoqueatual AS estoque,\n"
                    + "    p.pr_validade AS validade,\n"
                    + "    p.pr_cf_ncm AS ncm,\n"
                    + "    p.pr_cest AS cest,\n"
                    + "    CASE p.pr_ativo WHEN 'S' THEN 1 ELSE 0 END situacaocadastro,\n"
                    + "    p.pr_stpis AS piscofinssaida,\n"
                    + "    p.pr_stpis_entrada AS piscofinsentrada,\n"
                    + "    p.pr_nat_rec AS naturezareceita,\n"
                    + "    p.pr_item52 AS codigobeneficio,\n"
                    + "    icm_s.tr_codigo AS codicms_s,\n"
                    + "    icm_s.tr_descricao AS descicms_s,\n"
                    + "    icm_s.tr_aliquota AS aliqicms_s,\n"
                    + "    coalesce(t.tr_identificador, '') AS identicms_s, \n"        
                    + "    icm_e.tr_codigo AS codicms_e,\n"
                    + "    icm_e.tr_descricao AS descicms_e,\n"
                    + "    icm_e.tr_aliquota AS aliqicms_e,\n"
                    + "    p.pr_datacadastro AS datacadastro\n"
                    + "FROM CR_PRODUTOS p\n"
                    + "LEFT JOIN CR_TRIBUTACAOVENDA icm_s ON p.pr_codigotributacao = icm_s.tr_codigo\n"
                    + "LEFT JOIN cr_tributacaocompra icm_e ON p.pr_cod_icmscompra = icm_e.tr_codigo\n"
                    + "ORDER BY 1"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    
                    icmsCreditoId = rst.getString("codicms_e") + "-" + rst.getString("descicms_e") + "-" + rst.getString("aliqicms_e");
                    icmsDeditoId = rst.getString("codicms_s") + "-" + rst.getString("descicms_s") + "-" + rst.getString("aliqicms_s") + "-" + rst.getString("identicms_s");
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setEan(rst.getString("codigobarras"));
                    imp.seteBalanca(rst.getInt("ebalanca") == 1);
                    imp.setValidade(rst.getInt("validade"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
                    imp.setCodMercadologico1(rst.getString("merc1"));
                    imp.setCodMercadologico2(rst.getString("merc2"));
                    imp.setCodMercadologico3("1");
                    
                    imp.setSituacaoCadastro(rst.getInt("situacaocadastro"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setCustoComImposto(rst.getDouble("custo"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
                    imp.setEstoqueMaximo(rst.getDouble("estoquemaximo"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getInt("piscofinssaida"));
                    imp.setPiscofinsCstCredito(rst.getInt("piscofinsentrada"));
                    imp.setPiscofinsNaturezaReceita(rst.getInt("naturezareceita"));
                    imp.setBeneficio(rst.getString("codigobeneficio"));
                    imp.setIcmsDebitoId(icmsDeditoId);
                    imp.setIcmsDebitoForaEstadoId(icmsDeditoId);
                    imp.setIcmsDebitoForaEstadoNfId(icmsDeditoId);
                    imp.setIcmsCreditoId(icmsCreditoId);
                    imp.setIcmsCreditoForaEstadoId(icmsCreditoId);
                    imp.setIcmsConsumidorId(icmsDeditoId);
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
                    + "    f.fo_codigo AS id,\n"
                    + "    f.fo_nome AS razao,\n"
                    + "    f.fr_fantasia AS fantasia,\n"
                    + "    f.fr_cnpj AS cnpj,\n"
                    + "    f.fr_cpf AS cpf,\n"
                    + "    CASE f.fr_ativo WHEN 'S' THEN 1 ELSE 0 END ativo,\n"
                    + "    f.fr_im AS inscricaomunicipal,\n"
                    + "    f.fr_ie AS inscricaoestadual,\n"
                    + "    f.fr_endereco AS endereco,\n"
                    + "    f.fr_numero AS numero,\n"
                    + "    f.fr_bairro AS bairro,\n"
                    + "    f.fr_cidade AS municipio,\n"
                    + "    f.fr_codmunicipio AS municipioibge,\n"
                    + "    f.fr_cep AS cep,\n"
                    + "    f.fr_estado AS uf,\n"
                    + "    f.fr_contato AS contato,\n"
                    + "    f.fr_contato_email AS contatoemail,\n"
                    + "    f.fr_contato_fone AS contatotelefone,\n"
                    + "    f.fr_obs AS observacao,\n"
                    + "    f.fr_telefone AS telefone,\n"
                    + "    f.fr_telefone2 AS telefone2,\n"
                    + "    f.fr_email AS email,\n"
                    + "    f.fr_celular2 AS celular\n"
                    + "FROM cr_fornecedor f\n"
                    + "ORDER BY 1"
            )) {
                while (rst.next()) {

                }
            }
        }
        return null;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "    pf.pf_codigofornecedor AS idfornecedor,\n"
                    + "    p.pr_idl AS idproduto,\n"
                    + "    pf.pf_codigoproduto AS codigoexterno,\n"
                    + "    pf.pf_valor AS custo,\n"
                    + "    pf.pf_quantidade AS qtdembalagem,\n"
                    + "    pf.pf_ultimacompra AS dataalteracao\n"
                    + "FROM CR_PRODUTOSFORNECEDOR pf\n"
                    + "JOIN CR_PRODUTOS p ON p.pr_codebar = pf.pf_codebar\n"
                    + "ORDER BY 1, 2"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rst.getString("idproduto"));
                    imp.setIdFornecedor(rst.getString("idfornecedor"));
                    imp.setCodigoExterno(rst.getString("codigoexterno"));
                    imp.setCustoTabela(rst.getDouble("custo"));
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                    imp.setDataAlteracao(rst.getDate("dataalteracao"));
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
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "    c.cl_codigo AS id,\n"
                    + "    c.cl_nome AS razao,\n"
                    + "    c.cl_fantasia As fantasia,\n"
                    + "    c.cl_cnpj AS cnpj,\n"
                    + "    c.cl_cpf As cpf,\n"
                    + "    c.cl_ie As inscricaoestadual,\n"
                    + "    c.cl_identidade As identidade,\n"
                    + "    CASE c.cl_ativo WHEN 'S' THEN 1 ELSE 0 END ativo,\n"
                    + "    c.cl_endereco AS endereco,\n"
                    + "    c.cl_numero AS numero,\n"
                    + "    c.cl_bairro AS bairro,\n"
                    + "    c.cl_cep AS cep,\n"
                    + "    c.cl_cidade AS cidade,\n"
                    + "    c.cl_estado AS uf,\n"
                    + "    c.cl_codmunicipio AS municipioibge,\n"
                    + "    c.cl_telefone AS telefone,\n"
                    + "    c.cl_email AS email,\n"
                    + "    c.cl_celular AS celular,\n"
                    + "    c.cl_celular2 AS celular2,\n"
                    + "    c.cl_obs AS observacao,\n"
                    + "    c.cl_limite AS valorlimite,\n"
                    + "    c.cl_saldo AS saldo,\n"
                    + "    c.cl_nomepai AS nomepai,\n"
                    + "    c.cl_nomemae AS nomemae,\n"
                    + "    c.cl_conjuge AS nomeconjuge,\n"
                    + "    c.cl_estadocivil AS estadocivil,\n"
                    + "    c.cl_empresatrabalho AS empresa,\n"
                    + "    c.cl_cargo AS cargo,\n"
                    + "    c.cl_renda AS salario,\n"
                    + "    c.cl_fonetrabalho as telefonetrabalho,\n"
                    + "    c.cl_datacadastro AS datacadastro,\n"
                    + "    c.cl_datanascimento AS datanascimento,\n"
                    + "    c.cl_enderecocobranca AS enderecocobranca,\n"
                    + "    c.cl_sexo AS sexo\n"
                    + "FROM CR_CLIENTES c\n"
                    + "ORDER by 1"
            )) {
                while (rst.next()) {

                }
            }
        }
        return null;
    }
}
