package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Importacao
 */
public class SysERPDAO extends InterfaceDAO {

    @Override
    public String getSistema() {
        return "SYSERP";
    }

    public List<Estabelecimento> getLojasCliente() throws SQLException {
        List<Estabelecimento> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	emp_codigo id,\n"
                    + "	emp_nome_fantasia fantasia\n"
                    + "from\n"
                    + "	tb_empresa"
            )) {
                while (rs.next()) {
                    result.add(new Estabelecimento(rs.getString("id"), rs.getString("fantasia")));
                }
            }
        }
        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT	\n"
                    + "	*\n"
                    + " FROM \n"
                    + "	tb_grupo\n"
                    + "order by\n"
                    + "	2"
            )) {
                while (rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rs.getString("grp_codigo"));
                    imp.setMerc1Descricao(rs.getString("grp_descricao"));
                    imp.setMerc2ID("1");
                    imp.setMerc2Descricao(rs.getString("grp_descricao"));
                    imp.setMerc3ID("1");
                    imp.setMerc3Descricao(rs.getString("grp_descricao"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	p.prd_codigo id,\n"
                    + "	p.prd_descricao descricaocompleta,\n"
                    + "	p.grp_codigo mercadologico1,\n"
                    + "	p.prd_unidade_venda unidade,\n"
                    + "	P.prd_granel pesavel,\n"
                    + "	p.prd_codigo_barras ean,\n"
                    + "	p.prd_valor_venda_vista precovenda,\n"
                    + "	p.prd_data_cadastramento datacadastro,\n"
                    + "	p.prd_ativo situacaocadastro,\n"
                    + "	p.prd_ultimo_custo custoanterior,\n"
                    + "	p.prd_qtde_estoque estoque,\n"
                    + "	p.prd_estoque_maximo estoquemaximo,\n"
                    + "	p.prd_aliquota_icms icms_debito,\n"
                    + "	p.prd_situacao_tributaria icms_cst_debito,\n"
                    + "	p.prd_icms tipoicms,\n"
                    + "	p.prd_perc_av margem,\n"
                    + "	p.prd_codigoncm ncm,\n"
                    + "	p.prd_cest cest,\n"
                    + "	p.prd_qtde_emb qtdembalagem,\n"
                    + "	p.prd_pis_cofins_entrada piscofinscredito,\n"
                    + "	p.prd_pis_cofins piscofinsdebito,\n"
                    + "	p.prd_cod_nat_receita natureza,\n"
                    + "	p.prd_mva mva,\n"
                    + "	p.prd_tipo\n"
                    + "from\n"
                    + "	tb_produto p\n"
                    + "order by\n"
                    + "	p.prd_codigo"
            )) {
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id"));
                    imp.setDescricaoCompleta(rs.getString("descricaocompleta"));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    imp.setCodMercadologico1(rs.getString("mercadologico1"));
                    imp.setCodMercadologico2("1");
                    imp.setCodMercadologico3("1");
                    imp.setTipoEmbalagem(rs.getString("unidade"));
                    imp.setEan(rs.getString("ean"));

                    if (rs.getInt("pesavel") == 1) {
                        if (rs.getString("ean") != null && !"".equals(rs.getString("ean"))) {
                            String eanSTR = rs.getString("ean"), novoEAN;

                            novoEAN = eanSTR.substring(2, eanSTR.length() - 3);

                            imp.setEan(novoEAN);
                        }
                        imp.seteBalanca(true);
                    }
                    
                    imp.setPrecovenda(rs.getDouble("precovenda"));
                    imp.setDataCadastro(rs.getDate("datacadastro"));
                    imp.setSituacaoCadastro(rs.getInt("situacaocadastro") == 1
                            ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    imp.setCustoComImposto(rs.getDouble("custoanterior"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setEstoque(rs.getDouble("estoque"));
                    imp.setEstoqueMaximo(rs.getDouble("estoquemaximo"));
                    imp.setMargem(rs.getDouble("margem"));
                    imp.setNcm(rs.getString("ncm"));
                    imp.setCest(rs.getString("cest"));
                    imp.setQtdEmbalagem(rs.getInt("qtdembalagem"));
                    
                    imp.setPiscofinsCstDebito(rs.getString("piscofinsdebito"));
                    imp.setPiscofinsCstCredito(rs.getString("piscofinscredito"));
                    imp.setPiscofinsNaturezaReceita(rs.getString("natureza"));

                    imp.setIcmsAliqSaida(rs.getDouble("icms_debito"));
                    imp.setIcmsCstSaida(rs.getInt("icms_cst_debito"));

                    imp.setIcmsAliqEntrada(rs.getDouble("icms_debito"));
                    imp.setIcmsCstEntrada(rs.getInt("icms_cst_debito"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        try(Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "	f.for_codigo id,\n" +
                    "	f.for_razao_social razao,\n" +
                    "	f.for_cnpj_cpf cnpj,\n" +
                    "	f.for_inscricao ie,\n" +
                    "	f.for_data_cadastramento datacadastro,\n" +
                    "	f.for_nome_fantasia fantasia,\n" +
                    "	f.for_endereco endereco,\n" +
                    "	f.for_numero numero,\n" +
                    "	f.for_complemento complemento,\n" +
                    "	f.for_bairro bairro,\n" +
                    "	f.for_cidade cidade,\n" +
                    "	f.est_codigo uf,\n" +
                    "	f.for_cep cep,\n" +
                    "	f.for_telefone telefone,\n" +
                    "	f.for_fax fax,\n" +
                    "	f.for_celular celular,\n" +
                    "	f.for_email email,\n" +
                    "	f.for_observacao obs,\n" +
                    "	f.for_ativo situacaocadastro,\n" +
                    "	f.for_contato contato\n" +
                    "from\n" +
                    "	tb_fornecedor f\n" +
                    "order by\n" +
                    "	f.for_codigo"
            )) {
                while(rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setCnpj_cpf(rs.getString("cnpj"));
                    imp.setIe_rg(rs.getString("ie"));
                    imp.setDatacadastro(rs.getDate("datacadastro"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));
                    imp.setTel_principal(rs.getString("telefone"));
                    imp.setAtivo(rs.getInt("situacaocadastro") == 1);
                    
                    if(rs.getString("email") != null && !"".equals(rs.getString("email"))) {
                        String email = rs.getString("email");
                        if(rs.getString("email").length() > 50) {
                            email = rs.getString("email").substring(0, 50);
                        }
                        imp.addContato("1", "EMAIL", null, null, TipoContato.COMERCIAL, email);
                    }
                    
                    if(rs.getString("obs") != null && !"".equals(rs.getString("obs"))) {
                        imp.setObservacao(rs.getString("obs"));
                    }
                    
                    if(rs.getString("contato") != null && !"".equals(rs.getString("contato"))) {
                        imp.addContato("2", rs.getString("contato"), null, null, TipoContato.COMERCIAL, null);
                    }
                    
                    if(rs.getString("celular") != null && !"".equals(rs.getString("celular"))) {
                        imp.addContato("3", "CELULAR", null, rs.getString("celular"), TipoContato.COMERCIAL, null);
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
        try(Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "	prd_codigo id_produto,\n" +
                    "	for_codigo id_fornecedor,\n" +
                    "	prf_codigo_produto codigoexterno\n" +
                    "from\n" +
                    "	tb_produto_fornecedor\n" +
                    "order by\n" +
                    "	2, 1"
            )) {
                while(rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rs.getString("id_produto"));
                    imp.setIdFornecedor(rs.getString("id_fornecedor"));
                    imp.setCodigoExterno(rs.getString("codigoexterno"));
                    
                    result.add(imp);
                    
                }
            }
        }
        return result;
    }
}
