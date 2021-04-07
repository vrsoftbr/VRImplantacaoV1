package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import vrimplantacao.classe.ConexaoPostgres;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.OpcaoFiscal;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoIva;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.PautaFiscalIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Alan
 */
public class ResultMaisDAO extends InterfaceDAO implements MapaTributoProvider {

    public String complemento = "";

    private static final Logger LOG = Logger.getLogger(ResultMaisDAO.class.getName());

    @Override
    public String getSistema() {
        return "RMSistemas" + complemento;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select distinct\n"
                    + "	st || '-' || valor_taxa || '-' || valor_reducao codigo,\n"
                    + "	valor_taxa || '%' ||\n"
                    + "	case\n"
                    + "	  when st = '20' then ' RED'\n"
                    + "	  when st = '60' then ' SUBS'\n"
                    + "	  else ''\n"
                    + "	end descricao,\n"
                    + "	st cst,\n"
                    + "	valor_taxa aliquota,\n"
                    + "	valor_reducao reducao\n"
                    + "from\n"
                    + "	produto_tributo pt\n"
                    + "order by 1,2"
            )) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(rs.getString("codigo"),
                            rs.getString("descricao"),
                            rs.getInt("cst"),
                            rs.getInt("aliquota"),
                            rs.getInt("reducao")));
                }
            }
        }
        return result;
    }

    public List<Estabelecimento> getLojaCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	cd_empresa id,\n"
                    + " razao_social nome\n"
                    + "from empresa\n"
                    + "	order by 1"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("id"), rst.getInt("id") + " - " + rst.getString("nome")));
                }
            }
        }
        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	cd_grupo_produto m1,\n"
                    + "	descricao merc1,\n"
                    + "	cd_grupo_produto m2,\n"
                    + "	descricao merc2,\n"
                    + "	cd_grupo_produto m3,\n"
                    + "	descricao merc3\n"
                    + "from grupo_produto\n"
                    + " order by 1,3,5"
            )) {
                while (rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rs.getString("m1"));
                    imp.setMerc1Descricao(rs.getString("merc1"));
                    imp.setMerc2ID(rs.getString("m2"));
                    imp.setMerc2Descricao(rs.getString("merc2"));
                    imp.setMerc3ID(rs.getString("m3"));
                    imp.setMerc3Descricao(rs.getString("merc3"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	 cd_produto idproduto,\n"
                    + "	 ean codigobarras,\n"
                    + "	 1 qtdembalagem\n"
                    + "from\n"
                    + "	 produto p1\n"
                    + "where ean != 'SEM GTIN'\n"
                    + "	 union\n"
                    + "select\n"
                    + "	 cd_produto idproduto,\n"
                    + "	 ean_trib codigobarras,\n"
                    + "	 1 qtdembalagem\n"
                    + "from\n"
                    + "	 produto p2\n"
                    + "where ean_trib != 'SEM GTIN'\n"
                    + "  order by 1"
            )) {
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("idproduto"));
                    imp.setEan(rs.getString("codigobarras"));
                    imp.setQtdEmbalagem(rs.getInt("qtdembalagem"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    public List<PautaFiscalIMP> getPautasFiscais(Set<OpcaoFiscal> opcoes) throws Exception {
        List<PautaFiscalIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	p.cd_produto idproduto,\n"
                    + "	ncm,\n"
                    + "	p_mva_st per_mva,\n"
                    + "	pt.st cst_debito,\n"
                    + "	pt.valor_taxa aliquota_debito,\n"
                    + "	pt.valor_reducao reducao_debito,\n"
                    + "	case\n"
                    + "		when pt.valor_reducao > 0 then 20\n"
                    + "		else 0\n"
                    + "	end cst_credito,\n"
                    + "	p_st_ret aliquota_credito,\n"
                    + "	p_red_bc_efet reducao_credito\n"
                    + "from\n"
                    + "	produto p\n"
                    + "left join tributo t on p.cd_tributo = t.cd_tributo\n"
                    + "left join produto_tributo pt on p.cd_tributo = pt.cd_produto\n"
                    + "where\n"
                    + "	p_mva_st > 0\n"
                    + "order by\n"
                    + "	p.cd_produto"
            )) {
                while (rst.next()) {
                    PautaFiscalIMP imp = new PautaFiscalIMP();

                    imp.setId(rst.getString("idproduto"));
                    imp.setTipoIva(TipoIva.PERCENTUAL);
                    imp.setIva(rst.getDouble("per_mva"));
                    imp.setIvaAjustado(imp.getIva());
                    imp.setNcm(rst.getString("ncm"));

                    // DÉBITO
                    if ((rst.getDouble("aliquota_debito") > 0) && (rst.getDouble("reducao_debito") == 0)) {

                        imp.setAliquotaDebito(0, rst.getDouble("aliquota_debito"), rst.getDouble("reducao_debito"));
                        imp.setAliquotaDebitoForaEstado(0, rst.getDouble("aliquota_debito"), rst.getDouble("reducao_debito"));

                    } else if ((rst.getDouble("aliquota_debito") > 0) && (rst.getDouble("reducao_debito") > 0)) {

                        imp.setAliquotaDebito(20, rst.getDouble("aliquota_debito"), rst.getDouble("reducao_debito"));
                        imp.setAliquotaDebitoForaEstado(20, rst.getDouble("aliquota_debito"), rst.getDouble("reducao_debito"));

                    } else {

                        imp.setAliquotaDebito(rst.getInt("cst_debito"), rst.getDouble("aliquota_debito"), rst.getDouble("reducao_debito"));
                        imp.setAliquotaDebitoForaEstado(rst.getInt("cst_debito"), rst.getDouble("aliquota_debito"), rst.getDouble("reducao_debito"));
                    }

                    // CRÉDITO
                    if ((rst.getDouble("aliquota_credito") > 0) && (rst.getDouble("reducao_credito") == 0)) {

                        imp.setAliquotaCredito(0, rst.getDouble("aliquota_credito"), rst.getDouble("reducao_credito"));
                        imp.setAliquotaCreditoForaEstado(0, rst.getDouble("aliquota_credito"), rst.getDouble("reducao_credito"));

                    } else if ((rst.getDouble("aliquota_credito") > 0) && (rst.getDouble("reducao_credito") > 0)) {

                        imp.setAliquotaCredito(20, rst.getDouble("aliquota_credito"), rst.getDouble("reducao_credito"));
                        imp.setAliquotaCreditoForaEstado(20, rst.getDouble("aliquota_credito"), rst.getDouble("reducao_credito"));

                    } else {

                        imp.setAliquotaCredito(rst.getInt("cst_credito"), rst.getDouble("aliquota_credito"), rst.getDouble("reducao_credito"));
                        imp.setAliquotaCreditoForaEstado(rst.getInt("cst_credito"), rst.getDouble("aliquota_credito"), rst.getDouble("reducao_credito"));
                    }

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	p.cd_produto idproduto,\n"
                    + "	codigo codigobarras,\n"
                    + "	upper(p.descricao) descricao,\n"
                    + "	u.simbolo embalagem,\n"
                    + "	case\n"
                    + "		when length(codigo) <= 6 then 1\n"
                    + "		else 0\n"
                    + "	end e_balanca,\n"
                    + "	cd_grupo merc1,\n"
                    + "	cd_grupo merc2,\n"
                    + "	cd_grupo merc3,\n"
                    + "	round(perc_lucro, 2) margem,\n"
                    + "	pr_compra custosemimposto,\n"
                    + "	pr_custo custocomimposto,\n"
                    + "	pr_venda precovenda,\n"
                    + "	situacao situacaocadastro,\n"
                    + "	dt_cadastro datacadastro,\n"
                    + "	p.dh_ult_alteracao dataalteracao,\n"
                    + "	saldo_fisico estoque,\n"
                    + "	est_minimo estoquemin,\n"
                    + "	est_maximo estoquemax,\n"
                    + "	peso,\n"
                    + "	pt.valor_taxa aliqicms,\n"
                    + "	pt.st cst,\n"
                    + "	pt.valor_reducao reducao,\n"
                    + "	p.cod_pis pc_saida,\n"
                    + "	p.cod_pis_ent pc_entrada,\n"
                    + "	ncm,\n"
                    + "	t.cest\n"
                    + "from\n"
                    + "	produto p\n"
                    + "left join saldo_prod sp on sp.cd_produto = p.cd_produto\n"
                    + "left join unidade u on u.cd_unidade = p.cd_unidade\n"
                    + "left join tributo t on p.cd_tributo = t.cd_tributo\n"
                    + "left join produto_tributo pt on p.cd_tributo = pt.cd_produto\n"
                    + "where \n"
                    + "	ano_mes = (select max(ano_mes) from saldo_prod)\n"
                    + "order by\n"
                    + "	p.cd_produto"
            )) {
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("idproduto"));
                    imp.setEan(rs.getString("codigobarras"));
                    imp.setDescricaoCompleta(rs.getString("descricao"));
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setTipoEmbalagem(rs.getString("embalagem"));
                    imp.seteBalanca(rs.getBoolean("e_balanca"));
                    /*
                     if (imp.isBalanca()) {
                     imp.setEan(imp.getImportId());
                     }
                     */
                    imp.setCodMercadologico1(rs.getString("merc1"));
                    imp.setCodMercadologico2(rs.getString("merc2"));
                    imp.setCodMercadologico3(rs.getString("merc3"));

                    imp.setMargem(rs.getDouble("margem"));
                    imp.setCustoComImposto(rs.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(rs.getDouble("custosemimposto"));
                    imp.setPrecovenda(rs.getDouble("precovenda"));

                    imp.setSituacaoCadastro(rs.getInt("situacaocadastro") == 1 ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    imp.setDataCadastro(rs.getDate("datacadastro"));
                    imp.setDataAlteracao(rs.getDate("dataalteracao"));

                    imp.setEstoque(rs.getDouble("estoque"));
                    imp.setEstoqueMinimo(rs.getDouble("estoquemin"));
                    imp.setEstoqueMaximo(rs.getDouble("estoquemax"));
                    imp.setPesoBruto(rs.getDouble("peso"));

                    imp.setNcm(rs.getString("ncm"));
                    imp.setCest(rs.getString("cest"));

                    imp.setPiscofinsCstCredito(rs.getString("pc_entrada"));
                    imp.setPiscofinsCstDebito(rs.getString("pc_saida"));

                    imp.setIcmsCst(rs.getInt("cst"));
                    imp.setIcmsAliqSaida(rs.getInt("aliqicms"));
                    imp.setIcmsReducao(rs.getDouble("reducao"));

                    result.add(imp);
                }
                return result;
            }
        }
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	cd_pessoa id,\n"
                    + "	case when cnpj = '' then cpf else cnpj end cnpj_cpf,\n"
                    + "	inscricao_e inscricaoestadual,\n"
                    + "	inscricao_m inscricaomunicipal,\n"
                    + "	nome razao,\n"
                    + "	n_fantasia fantasia,\n"
                    + "	endereco,\n"
                    + "	numero,\n"
                    + "	complemento,\n"
                    + "	bairro,\n"
                    + "	upper(c.descricao) cidade,\n"
                    + "	c.cod_ibge cidade_ibge,\n"
                    + "	c.estado,\n"
                    + "	cep,\n"
                    + "	dt_cadastro datacadastro,\n"
                    + "	situacao,\n"
                    + "	telefone,\n"
                    + "	fax,\n"
                    + "	email,\n"
                    + "	observacao\n"
                    + "from\n"
                    + "	pessoa p\n"
                    + "	join cidade c on c.cd_cidade = p.cd_cidade\n"
                    + "where\n"
                    + "	fornecedor = true\n"
                    + "order by cd_pessoa"
            )) {
                while (rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id"));
                    imp.setCnpj_cpf(rs.getString("cnpj_cpf"));
                    imp.setIe_rg(rs.getString("inscricaoestadual"));
                    imp.setInsc_municipal(rs.getString("inscricaomunicipal"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));

                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setIbge_municipio(rs.getInt("cidade_ibge"));
                    imp.setUf(rs.getString("estado"));
                    imp.setCep(rs.getString("cep"));

                    imp.setDatacadastro(rs.getDate("datacadastro"));
                    imp.setAtivo(rs.getBoolean("situacao"));
                    imp.setObservacao(rs.getString("observacao"));

                    imp.setTel_principal(Utils.formataNumero(rs.getString("telefone")));
                    if (rs.getString("fax") != null && !rs.getString("fax").isEmpty()) {
                        imp.addContato("1", "FAX", rs.getString("fax"), "", TipoContato.COMERCIAL, "");
                    }
                    if (rs.getString("email") != null && !rs.getString("email").isEmpty()) {
                        imp.addContato("2", "EMAIL", "", "", TipoContato.COMERCIAL, rs.getString("email"));
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
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	pf.cd_pessoa fornecedor,\n"
                    + "	cd_produto idproduto,\n"
                    + "	cd_fornec_prod codexterno\n"
                    + "from\n"
                    + "	fornec_prod pf\n"
                    + "	join pessoa f on f.cd_pessoa = pf.cd_pessoa\n"
                    + "order by pf.cd_pessoa, cd_produto")) {
                while (rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdFornecedor(rs.getString("fornecedor"));
                    imp.setIdProduto(rs.getString("idproduto"));
                    imp.setCodigoExterno(rs.getString("codexterno"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	cd_pessoa id,\n"
                    + "	case when cnpj = '' then cpf else cnpj end cnpj_cpf,\n"
                    + "	inscricao_e inscricaoestadual,\n"
                    + "	inscricao_m inscricaomunicipal,\n"
                    + "	nome razao,\n"
                    + "	n_fantasia fantasia,\n"
                    + "	endereco,\n"
                    + "	numero,\n"
                    + "	complemento,\n"
                    + "	bairro,\n"
                    + "	upper(c.descricao) cidade,\n"
                    + " c.cod_ibge cidade_ibge,\n"
                    + "	c.estado,\n"
                    + "	cep,\n"
                    + "	dt_cadastro datacadastro,\n"
                    + "	dt_nasc datanascimento,\n"
                    + "	situacao,\n"
                    + "	telefone,\n"
                    + "	fax,\n"
                    + "	email,\n"
                    + "	nome_mae nomemae,\n"
                    + "	nome_pai nomepai,\n"
                    + " emp_trab empresa,\n"
                    + " fone_emp empresa_tel,\n"
                    + " cargo,\n"
                    + " remuneracao salario,\n"
                    + " vl_limite limite,\n"
                    + " conjuge,\n"
                    + "	observacao\n"
                    + "from\n"
                    + "	pessoa p\n"
                    + "	join cidade c on c.cd_cidade = p.cd_cidade\n"
                    + "where\n"
                    + "	cliente = true\n"
                    + "order by cd_pessoa")) {
                while (rs.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rs.getString("id"));
                    imp.setCnpj(rs.getString("cnpj_cpf"));
                    imp.setInscricaoestadual(rs.getString("inscricaoestadual"));
                    imp.setInscricaoMunicipal(rs.getString("inscricaomunicipal"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));

                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setMunicipioIBGE(rs.getInt("cidade_ibge"));
                    imp.setUf(rs.getString("estado"));
                    imp.setCep(rs.getString("cep"));

                    imp.setDataCadastro(rs.getDate("datacadastro"));
                    imp.setDataNascimento(rs.getDate("datanascimento"));

                    imp.setAtivo(rs.getInt("situacao") == 1);
                    imp.setTelefone(rs.getString("telefone"));
                    imp.setFax(rs.getString("fax"));
                    imp.setEmail(rs.getString("email"));

                    imp.setNomeMae(rs.getString("nomemae"));
                    imp.setNomePai(rs.getString("nomepai"));
                    imp.setEmpresa(rs.getString("empresa"));
                    imp.setEmpresaTelefone(rs.getString("empresa_tel"));
                    imp.setCargo(rs.getString("cargo"));
                    imp.setSalario(rs.getDouble("salario"));
                    imp.setValorLimite(rs.getDouble("limite"));
                    imp.setNomeConjuge(rs.getString("conjuge"));
                    imp.setObservacao(rs.getString("observacao"));

                    result.add(imp);
                }
            }
        }
        return result;
    }
}
