package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto.ProdutoAnteriorDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.cadastro.ProdutoAnteriorVO;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoEstadoCivil;
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Guilherme
 */
public class ICommerceDAO extends InterfaceDAO implements MapaTributoProvider {

    public boolean v_usar_arquivoBalanca;
    public String id_loja;

    @Override
    public String getSistema() {
        if (id_loja == null) {
            id_loja = "";
        }   
        return "ICommerce" + id_loja;        
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	alq_codigo,\n"
                    + "	alq_descri\n"
                    + "from\n"
                    + "	aliquotas_ecf")) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(rs.getString("alq_codigo"), rs.getString("alq_descri")));
                }
            }
        }
        return result;
    }

    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> lojas = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	loj_codigo id,\n"
                    + "	loj_fantasia nome,\n"
                    + "	loj_cnpj cnpj\n"
                    + "from \n"
                    + "	lojas\n"
                    + "order by\n"
                    + "	loj_codigo"
            )) {
                while (rs.next()) {
                    lojas.add(new Estabelecimento(rs.getString("id"), rs.getString("nome")));
                }
            }
        }
        return lojas;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select  \n"
                    + "	p.pro_codigo id, \n"
                    + "	p.pro_barras ean, \n"
                    + "	p.pro_descri descricaocompleta, \n"
                    + "	p.pro_descri_fiscal descricaoreduzida, \n"
                    + "	p.pro_un unidade, \n"
                    + "	p.pro_qtde_embalagem qtdembalagem, \n"
                    + "	p.pro_peso peso, \n"
                    + "	p.pro_peso_liquido pesoliquido, \n"
                    + "	p.pro_preco1 preco, \n"
                    + "	p.pro_custo_unitario custo, \n"
                    + " p.pro_lucro_esperado_p margem, \n"
                    + "	pe.pro_saldo estoque, \n"
                    + "	p.pro_est_min estoquemin, \n"
                    + "	p.pro_est_max estoquemax, \n"
                    + "	p.pro_cadastro datacadastro, \n"
                    + "	p.pro_data_alt dataalteracao, \n"
                    + "	p.pro_status ativo,\n"
                    + "	aliq.alq_codigo as icmsid, \n"
                    + "	p.pro_st cstdebito, \n"
                    + "	p.pro_usa_balanca ebalanca, \n"
                    + "	p.pro_validade validade, \n"
                    + "	p.pro_st_ipi ipidebito, \n"
                    + "	p.pro_st_pis pisdebito, \n"
                    + "	p.pro_st_cofins stcofins, \n"
                    + "	p.pro_aliq_pis aliqpis, \n"
                    + "	p.pro_aliq_cofins aliqcofins, \n"
                    + "	p.pro_ncm ncm, \n"
                    + "	p.pro_st_pis_ent piscredito, \n"
                    + "	p.pro_st_cofins_ent cofinscredito, \n"
                    + "	p.pro_aliq_pis_ent aliqpiscredito, \n"
                    + "	p.pro_aliq_cofins_ent aliqcofinscredito, \n"
                    + " p.pro_ntr as naturezareceita \n"
                    + "from \n"
                    + "	produtos as p \n"
                    + "	join produtos_estoque as pe on p.pro_codigo = pe.pro_codigo \n"
                    + "	join lojas l on pe.pro_loja = l.loj_codigo\n"
                    + "left join aliquotas_ecf  aliq on p.pro_aliquota = aliq.alq_codigo\n"
                    + "where \n"
                    + "	l.loj_codigo = " + getLojaOrigem() + "\n"
                    + "order by \n"
                    + "	p.pro_codigo")) {
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().carregarProdutosBalanca();
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id"));
                    imp.setEan(rs.getString("ean"));
                    imp.setDescricaoCompleta(rs.getString("descricaocompleta"));
                    
                    if ((rs.getString("descricaoreduzida") != null) && (!rs.getString("descricaoreduzida").trim().isEmpty())) {
                        imp.setDescricaoReduzida(rs.getString("descricaoreduzida"));
                    } else {
                        imp.setDescricaoReduzida(rs.getString("descricaocompleta"));
                    }
                    
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setTipoEmbalagem(rs.getString("unidade"));
                    
                    if (rs.getInt("qtdembalagem") == 0) {
                        imp.setQtdEmbalagem(1);
                    } else {
                        imp.setQtdEmbalagem(rs.getInt("qtdembalagem"));
                    }
                    
                    imp.setPesoBruto(rs.getDouble("peso"));
                    imp.setPesoLiquido(rs.getDouble("pesoliquido"));
                    imp.setPrecovenda(rs.getDouble("preco"));
                    imp.setMargem(rs.getDouble("margem"));
                    imp.setCustoComImposto(rs.getDouble("custo"));
                    imp.setCustoSemImposto(rs.getDouble("custo"));
                    imp.setEstoque(rs.getDouble("estoque"));
                    imp.setEstoqueMinimo(rs.getDouble("estoquemin"));
                    imp.setEstoqueMaximo(rs.getDouble("estoquemax"));
                    imp.setDataCadastro(rs.getDate("datacadastro"));
                    imp.setDataAlteracao(rs.getDate("dataalteracao"));
                    imp.setNcm(rs.getString("ncm"));
                    imp.setSituacaoCadastro("A".equals(rs.getString("ativo")) ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);

                    ProdutoBalancaVO produtoBalanca;
                    long codigoProduto;
                    codigoProduto = Long.parseLong(imp.getImportId().trim());
                    if (codigoProduto <= Integer.MAX_VALUE) {
                        produtoBalanca = produtosBalanca.get((int) codigoProduto);
                    } else {
                        produtoBalanca = null;
                    }
                    if (produtoBalanca != null) {
                        imp.seteBalanca(true);
                        imp.setValidade(produtoBalanca.getValidade() > 1 ? produtoBalanca.getValidade() : 1);
                    } else {
                        imp.setValidade(0);
                        imp.seteBalanca(false);
                    }

                    imp.setIcmsDebitoId(rs.getString("icmsid"));
                    imp.setIcmsCreditoId(rs.getString("icmsid"));
                    imp.setPiscofinsCstCredito(rs.getString("piscredito"));
                    imp.setPiscofinsCstDebito(rs.getString("pisdebito"));
                    imp.setPiscofinsNaturezaReceita(rs.getString("naturezareceita"));

                    result.add(imp);
                }
            }
        }
        return result;
    }
    
    @Override
    public List<ProdutoIMP> getProdutos(OpcaoProduto opt) throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        if (opt == OpcaoProduto.ICMS_INDIVIDUAL) {
            try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select  \n"
                        + "	p.pro_codigo id, \n"
                        + "	aliq.alq_codigo as icmsid \n"
                        + "from \n"
                        + "	produtos as p \n"
                        + "	join produtos_estoque as pe on p.pro_codigo = pe.pro_codigo \n"
                        + "	join lojas l on pe.pro_loja = l.loj_codigo\n"
                        + "left join aliquotas_ecf  aliq on p.pro_aliquota = aliq.alq_codigo\n"
                        + "where \n"
                        + "	l.loj_codigo = " + getLojaOrigem() + "\n"
                        + "order by \n"
                        + "	p.pro_codigo"
                )) {
                    Map<String, ProdutoAnteriorVO> anteriores = new ProdutoAnteriorDAO().getAnterior(getSistema());
                    while (rst.next()) {

                        ProdutoAnteriorVO anterior;
                        anterior = anteriores.get(rst.getString("id"));

                        if (anterior != null) {
                            ProdutoIMP imp = new ProdutoIMP();
                            imp.setImportLoja(getLojaOrigem());
                            imp.setImportSistema(getSistema());
                            imp.setImportId(rst.getString("id"));
                            imp.setIcmsDebitoId(rst.getString("icmsid"));
                            imp.setIcmsCreditoId(rst.getString("icmsid"));
                            result.add(imp);
                        }
                    }
                }
                return result;
            }
        }
        return null;
    }
    
    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	for_codigo id,\n"
                    + "	for_razao razao,\n"
                    + "	for_fantasia fantasia,\n"
                    + "	for_cpfcnpj cnpj_cpf,\n"
                    + "	for_inscri ie_rg,\n"
                    + "	for_endereco endereco,\n"
                    + "	for_bairro bairro,\n"
                    + "	for_cidade cidade,\n"
                    + "	for_numero as numero,\n"
                    + "	for_uf uf,\n"
                    + "	for_cep cep,\n"
                    + "	for_fone telefone,\n"
                    + "	for_fax fax,\n"
                    + "	for_celular celular,\n"
                    + "	for_email email,\n"
                    + "	for_observ observacao,\n"
                    + "	for_tipo_pessoa pessoa,\n"
                    + "	for_status situacao,\n"
                    + "	for_data_alt dataalteracao,\n"
                    + "	for_ultima dataultimaentrada\n"
                    + "from\n"
                    + "	fornecedores\n"
                    + "order by\n"
                    + "	for_codigo")) {
                while (rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setCnpj_cpf(rs.getString("cnpj_cpf"));
                    imp.setIe_rg(rs.getString("ie_rg"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));
                    imp.setTel_principal(rs.getString("telefone"));

                    if ((rs.getString("fax") != null) && (!rs.getString("fax").trim().isEmpty())) {
                        imp.addContato("FAX",
                                rs.getString("fax"),
                                null,
                                TipoContato.COMERCIAL,
                                null);
                    }
                    if ((rs.getString("celular") != null) && (!rs.getString("celular").trim().isEmpty())) {
                        imp.addContato("Celular",
                                null,
                                rs.getString("celular"),
                                TipoContato.COMERCIAL,
                                null);
                    }
                    if ((rs.getString("email") != null) && (!rs.getString("email").isEmpty())) {
                        String email = rs.getString("email").toLowerCase();
                        imp.addContato("Email",
                                null,
                                null,
                                TipoContato.COMERCIAL,
                                email);
                    }
                    imp.setObservacao(rs.getString("observacao"));
                    boolean situacao = "A".equals(rs.getString("situacao")) ? true : false;
                    imp.setAtivo(situacao);

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	prf_produto idproduto,\n"
                    + "	prf_fornecedor idfornecedor,\n"
                    + "	prf_data datacadastro,\n"
                    + "	prf_qtde_embalagem qtdembalagem\n"
                    + "from\n"
                    + "	produtos_fornecedor")) {
                while (rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rs.getString("idproduto"));
                    imp.setIdFornecedor(rs.getString("idfornecedor"));
                    imp.setQtdEmbalagem(rs.getDouble("qtdembalagem"));
                    imp.setDataAlteracao(rs.getDate("datacadastro"));

                    result.add(imp);
                }

            }
        }
        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	cli_codigo id,\n"
                    + "	cli_tipo_pessoa pessoa,\n"
                    + "	cli_cpfcnpj cpfcnpj,\n"
                    + "	cli_rg rg,\n"
                    + "	cli_nome nome,\n"
                    + "	cli_fantasia fantasia,\n"
                    + "	case when cli_sexo = 'M' then 1 \n"
                    + "      when cli_sexo = 'F' then 0\n"
                    + "	else null end as sexo,\n"
                    + "	cli_nascimento dtnascimento,\n"
                    + "	cli_endereco endereco,\n"
                    + "	cli_numero numero,\n"
                    + "	cli_bairro bairro,\n"
                    + "	cli_cidade cidade,\n"
                    + "	cli_estado uf,\n"
                    + "	cli_cep cep,\n"
                    + "	cli_celular celular,\n"
                    + "	cli_email email,\n"
                    + "	cli_pai nomepai,\n"
                    + "	cli_mae nomemae,\n"
                    + "	case cli_civil when 'S' then 1\n"
                    + "	when 'C' then 2\n"
                    + "	when 'V' then 3\n"
                    + "	when 'A' then 4\n"
                    + "	when 'O' then 5\n"
                    + "	else 0 end as estadocivil,\n"
                    + "	cli_limite limite,\n"
                    + "	cli_obs observacao,\n"
                    + "	cli_cadastro datacadastro,\n"
                    + "	cli_ult_alteracao dataalteracao,\n"
                    + "	cli_situacao situacao,\n"
                    + "	cli_status ativo,\n"
                    + "	cli_saldo saldo\n"
                    + "from\n"
                    + "	clientes \n"
                    + "order by\n"
                    + "	cli_codigo")) {
                while (rs.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rs.getString("id"));
                    imp.setCnpj(rs.getString("cpfcnpj"));
                    if ((rs.getString("rg") != null)
                            && (!rs.getString("rg").trim().isEmpty()) && (rs.getString("rg").length() == 9)) {
                        imp.setInscricaoestadual(rs.getString("rg"));
                    } else if ((rs.getString("rg") != null) && (!rs.getString("rg").isEmpty()) && (rs.getString("rg").length() < 9)) {
                        imp.setInscricaoestadual(rs.getString("rg"));
                    } else {
                        imp.setInscricaoMunicipal("ISENTO");
                    }
                    boolean ativo = "A".equals(rs.getString("ativo")) ? true : false;
                    imp.setAtivo(ativo);
                    imp.setRazao(rs.getString("nome"));
                    imp.setFantasia(rs.getString("fantasia"));
                    if (rs.getString("sexo") != null) {
                        imp.setSexo("1".equals(rs.getString("sexo")) ? TipoSexo.MASCULINO : TipoSexo.FEMININO);
                    }
                    imp.setDataNascimento(rs.getDate("dtnascimento"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));
                    if ((rs.getString("celular") != null) && (!rs.getString("celular").isEmpty())) {
                        imp.addContato("1",
                                "Celular",
                                null,
                                rs.getString("celular"),
                                null);
                    }
                    if ((rs.getString("email") != null) && (!rs.getString("email").isEmpty())) {
                        String email = rs.getString("email").toLowerCase();
                        imp.addContato("2",
                                "Email",
                                null,
                                null,
                                email);
                    }
                    imp.setNomePai(rs.getString("nomepai"));
                    imp.setNomeMae(rs.getString("nomemae"));
                    if ((rs.getString("estadocivil") != null)
                            && (!rs.getString("estadocivil").trim().isEmpty())) {
                        if (null != rs.getString("estadocivil")) {
                            switch (rs.getString("estadocivil")) {
                                case "1":
                                    imp.setEstadoCivil(TipoEstadoCivil.SOLTEIRO);
                                    break;
                                case "2":
                                    imp.setEstadoCivil(TipoEstadoCivil.CASADO);
                                    break;
                                case "3":
                                    imp.setEstadoCivil(TipoEstadoCivil.VIUVO);
                                    break;
                                case "4":
                                    imp.setEstadoCivil(TipoEstadoCivil.AMAZIADO);
                                    break;
                                case "5":
                                    imp.setEstadoCivil(TipoEstadoCivil.OUTROS);
                                    break;
                                default:
                                    imp.setEstadoCivil(TipoEstadoCivil.OUTROS);
                                    break;
                            }
                        }
                    } else {
                        imp.setEstadoCivil(TipoEstadoCivil.OUTROS);
                    }
                    imp.setLimiteCompra(rs.getDouble("limite"));
                    imp.setObservacao(rs.getString("observacao"));
                    imp.setDataCadastro(rs.getDate("datacadastro"));
                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	cr.con_vendas id,\n"
                    + "	cr.con_cliente idcliente,\n"
                    + "	c.cli_cpfcnpj cnpj,\n"
                    + "	cr.con_emissao dataemissao,\n"
                    + "	cr.con_desconto desconto,\n"
                    + "	cr.con_acrescimo acrescimo,\n"
                    + "	cr.con_cupom_fiscal cco,\n"
                    + "	crd.crd_valor valor,\n"
                    + "	crd.crd_parcela parcela,\n"
                    + "	crd.crd_vencimento vencimento,\n"
                    + "	crd.crd_juros juros\n"
                    + "from\n"
                    + "	contas_receber cr\n"
                    + "join contas_receber_dados crd on cr.con_vendas = crd.crd_codigo\n"
                    + "join clientes c on cr.con_cliente = c.cli_codigo\n"
                    + "where\n"
                    + "	cr.con_loja = " + getLojaOrigem() + " and\n"
                    + "	crd.crd_data_pgto is null\n"
                    + "order by\n"
                    + "	cr.con_emissao")) {
                while (rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rs.getString("id"));
                    imp.setCnpjCliente(rs.getString("cnpj"));
                    imp.setDataEmissao(rs.getDate("dataemissao"));
                    imp.setDataVencimento(rs.getDate("vencimento"));
                    imp.setIdCliente(rs.getString("idcliente"));
                    imp.setNumeroCupom(rs.getString("cco"));
                    imp.setParcela(rs.getInt("parcela"));
                    imp.setValor(rs.getDouble("valor"));
                    imp.setJuros(rs.getDouble("juros"));

                    result.add(imp);
                }
            }
        }
        return result;
    }
}
