package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import vrimplantacao.classe.ConexaoPostgres;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoEstadoCivil;
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Guilherme
 */
public class RMSAutomaHelpDAO extends InterfaceDAO implements MapaTributoProvider {

    public boolean v_usar_arquivoBalanca;

    @Override
    public String getSistema() {
        return "RMS AutomaHelp";
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "     codigo,\n"
                    + "     nome\n"
                    + "from icms\n"
                    + "order by 2")) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(rs.getString("codigo"), rs.getString("nome")));
                }
            }
        }
        return result;
    }

    public List<Estabelecimento> getLojas() throws SQLException {
        List<Estabelecimento> lojas = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "loja::integer as id,\n"
                    + "cnpj,\n"
                    + "nome_fantasia as descricao\n"
                    + "from "
                    + "parametros"
            )) {
                while (rst.next()) {
                    lojas.add(new Estabelecimento(rst.getString("id"), rst.getString("descricao")));
                }
            }
        }
        return lojas;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	case\n"
                    + "	  when codigo_cliente = '' then codigo::character varying\n"
                    + "	else codigo_cliente end as id,\n"
                    + "	(case when c.inscricao_estadual = '' then null else c.inscricao_estadual end) as ie,\n"
                    + "	c.rg,\n"
                    + "	c.nome as razao,\n"
                    + "	case \n"
                    + "	  when inativo = 'N' then 1 else 0 end as ativo,\n"
                    + "	c.endereco,\n"
                    + "	c.numero,\n"
                    + "	c.complemento,\n"
                    + "	c.bairro,\n"
                    + "	c.codigo_municipio::integer as municipioIBGE,\n"
                    + "	c.cidade as municipio,\n"
                    + "	c.estado as uf,\n"
                    + "	c.cep,\n"
                    + "	(case \n"
                    + "	  when estado_civil = 'Solteiro(a)' then 1\n"
                    + "	  when estado_civil = 'Casado(a)' then 2\n"
                    + "	  when estado_civil = 'Viúvo(a)' then 3\n"
                    + "	  when estado_civil = 'Desquitado' then 6\n"
                    + "	  when estado_civil = 'Divorciado' then 6\n"
                    + "	  when estado_civil = 'Outros...' then 5\n"
                    + "	end) as estadocivil,\n"
                    + "	c.datanas::date as datanascimento,\n"
                    + "	c.datacadastro::date as datacadastro,\n"
                    + "	case sexo when 'Masculino' then 1 else 0 end as sexo,\n"
                    + "	c.nome_empresa as empresa,\n"
                    + "	c.endereco_empresa as empresaendereco,\n"
                    + "	c.numero_empresa as empresanumero,\n"
                    + "	c.bairro_empresa as empresabairro,\n"
                    + "	c.cidade_empresa as empresamunicipio,\n"
                    + "	c.estado_empresa as empresaestado,\n"
                    + "	c.cep_empresa as empresacep,\n"
                    + "	c.telefone_empresa as empresatelefone,\n"
                    + "	c.cargo_empresa as cargo,\n"
                    + "	c.telefone,\n"
                    + "	c.celular,\n"
                    + "	c.email\n"
                    + "from clientes c\n"
                   + "where length(c.codigo_cliente) <= 6\n"
                    + "order by 1")) {
                while (rs.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rs.getString("id"));
                    imp.setRazao(rs.getString("razao"));
                    if ((rs.getString("rg") != null)
                            && (!rs.getString("rg").trim().isEmpty())) {
                        imp.setInscricaoestadual(rs.getString("rg"));
                    } else if ((rs.getString("ie") != null)
                            && (!rs.getString("ie").trim().isEmpty())) {
                        imp.setInscricaoestadual(rs.getString("ie"));
                    } else {
                        imp.setInscricaoestadual("isento");
                    }
                    imp.setAtivo(rs.getBoolean("ativo"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipioIBGE(rs.getInt("municipioIBGE"));
                    imp.setMunicipio(rs.getString("municipio"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));
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
                    imp.setDataNascimento(rs.getDate("datanascimento"));
                    imp.setDataCadastro(rs.getDate("datacadastro"));
                    imp.setSexo("1".equals(rs.getString("sexo")) ? TipoSexo.MASCULINO : TipoSexo.FEMININO);
                    imp.setEmpresa(rs.getString("empresa"));
                    imp.setEmpresaEndereco(rs.getString("empresaendereco"));
                    imp.setEmpresaNumero(rs.getString("empresanumero"));
                    imp.setEmpresaBairro(rs.getString("empresabairro"));
                    imp.setEmpresaMunicipio(rs.getString("empresamunicipio"));
                    imp.setEmpresaUf(rs.getString("empresaestado"));
                    imp.setEmpresaCep(rs.getString("empresacep"));
                    imp.setEmpresaTelefone(rs.getString("empresatelefone"));
                    imp.setCargo(rs.getString("cargo"));
                    imp.setTelefone(rs.getString("telefone"));
                    imp.setCelular(rs.getString("celular"));
                    imp.setEmail(rs.getString("email"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws SQLException {
        List<FornecedorIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	codigo as id,\n"
                    + "	trim(razao_social) as razao,\n"
                    + "	trim(nome_fantasia) as fantasia,\n"
                    + "	telefone as telefoneprincipal,\n"
                    + "	endereco,\n"
                    + "	numero,\n"
                    + "	bairro,\n"
                    + "	cidade,\n"
                    + "	codigo_municipio as ibge_municipio,\n"
                    + "	estado,\n"
                    + "	cep,\n"
                    + "	complemento,\n"
                    + "	observacao,\n"
                    + "	trim(cpf) as cnpj_cpf,\n"
                    + "	trim(rg) as ie_rg,\n"
                    + "	fax,\n"
                    + "	email_nfe,\n"
                    + "	celular,\n"
                    + "	data_alteracao as datacadastro,\n"
                    + "	case\n"
                    + "	when status = 'A' then 1 else 0 end as ativo\n"
                    + "from fornecedores\n"
                    + "order by codigo")) {
                while (rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setTel_principal(rs.getString("telefoneprincipal"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setIbge_municipio(rs.getInt("ibge_municipio"));
                    imp.setUf(rs.getString("estado"));
                    imp.setCep(rs.getString("cep"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setObservacao(rs.getString("observacao"));
                    imp.setCnpj_cpf(rs.getString("cnpj_cpf"));
                    imp.setIe_rg(rs.getString("ie_rg"));
                    imp.setDatacadastro(rs.getDate("datacadastro"));
                    imp.setAtivo(rs.getBoolean("ativo"));
                    if ((rs.getString("fax") != null)
                            && (!rs.getString("fax").trim().isEmpty())) {
                        imp.addContato(
                                "1",
                                "fax",
                                rs.getString("fax"),
                                null,
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    String email = Utils.acertarTexto(rs.getString("email_nfe")).toLowerCase();
                    if (!"".equals(email)) {
                        imp.addContato("2", "email nfe", "", "", TipoContato.COMERCIAL, email);
                    }
                    if ((rs.getString("celular") != null)
                            && (!rs.getString("celular").trim().isEmpty())) {
                        imp.addContato(
                                "3",
                                "Celular",
                                null,
                                rs.getString("celular"),
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select * from familias order by 1"
            )) {
                while (rs.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("codigo"));
                    imp.setDescricao(rs.getString("nome"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws SQLException {
        List<MercadologicoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select "
                    + " g.codigo as codmerc1,\n"
                    + " g.nome as descmerc1,\n"
                    + " f.codigo as codmerc2,\n"
                    + " f.nome as descmerc2,\n"
                    + " coalesce(s.codigo,1) as codmerc3,\n"
                    + " coalesce(s.nome, f.nome) as descmerc3\n"
                    + "from familias f\n"
                    + "join sub_familias s on s.familia = f.codigo\n"
                    + "join grupos g on f.grupo = g.codigo"
            )) {
                while (rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setMerc1ID(rs.getString("codmerc1"));
                    imp.setMerc1Descricao(rs.getString("descmerc1"));
                    imp.setMerc2ID(rs.getString("codmerc2"));
                    imp.setMerc2Descricao(rs.getString("descmerc2"));
                    imp.setMerc3ID(rs.getString("codmerc3"));
                    imp.setMerc3Descricao(rs.getString("descmerc3"));

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
                    "select	\n"
                    + "	distinct\n"
                    + "	p.codigo,\n"
                    + "	f.codigo as codfamilia,\n"
                    + "	p.descricao,\n"
                    + "	p.descricao_reduzida,\n"
                    + "	coalesce(g.codigo, 1) as merc1,\n"
                    + "	coalesce(f.codigo, 1) as merc2,\n"
                    + "	coalesce(sf.codigo, 1) as merc3,\n"
                    + "	p.icms as icmsdebito,\n"
                    + "	p.embalagem1 as qtdembalagem,\n"
                    + "	p.codigo_barras::bigint,\n"
                    + "	p.embalagem2,\n"
                    + "	p.estoque,\n"
                    + "	p.estoquemin,\n"
                    + "	coalesce(p.preco_custo_un_nf, p.preco_custo_un_total) as precocusto,\n"
                    + " p.preco_custo_un_total as precocustoimposto,\n"
                    + "	p.precovenda,\n"
                    + "	p.margem,\n"
                    + "	p.data_alteracao,\n"
                    + "	case\n"
                    + "	  when setorbalanca = 'NÃO' then 0\n"
                    + "	else 1 end as ebalanca,\n"
                    + "	p.validade,\n"
                    + " upper(p.produto_inativo) as ativo,\n"
                    + "	p.ncm,\n"
                    + "	p.cest,\n"
                    + "	p.cst_pis_entrada,\n"
                    + "	p.cst_pis_saida,\n"
                    + "	p.cst_cofins_entrada,\n"
                    + "	p.cst_cofins_saida,\n"
                    + "	p.icms_entrada icmscredito,\n"
                    + "	p.percentual_pis_cofins_entrada as piscofinsCstCredito\n"
                    + "from produtos p\n"
                    + "join familias f on p.familia = f.codigo\n"
                    + "join grupos g on p.grupo = g.codigo\n"
                    + "join sub_familias sf on f.codigo = sf.familia\n"
                    + "and sf.codigo = p.sub_familia\n"
                    + "and sf.familia = f.codigo\n"
                    + "order by codigo")) {
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().carregarProdutosBalanca();
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("codigo"));
                    imp.setIdFamiliaProduto(rs.getString("codfamilia"));
                    imp.setDescricaoCompleta(rs.getString("descricao"));
                    imp.setDescricaoReduzida(rs.getString("descricao_reduzida"));
                    imp.setCodMercadologico1(rs.getString("merc1"));
                    imp.setCodMercadologico2(rs.getString("merc2"));
                    imp.setCodMercadologico3(rs.getString("merc3"));
                    imp.setIcmsAliq(rs.getDouble("icmsdebito"));
                    imp.setQtdEmbalagem(rs.getInt("qtdembalagem") == 0 ? 1 : rs.getInt("qtdembalagem"));
                    imp.setEan(rs.getString("codigo_barras"));
                    imp.setTipoEmbalagem(rs.getString("embalagem2"));
                    imp.setEstoque(rs.getDouble("estoque"));
                    imp.setEstoqueMinimo(rs.getDouble("estoquemin"));
                    imp.setCustoComImposto(rs.getDouble("precocustoimposto"));
                    imp.setCustoSemImposto(rs.getDouble("precocusto"));
                    imp.setPrecovenda(rs.getDouble("precovenda"));
                    imp.setMargem(rs.getDouble("margem"));
                    imp.seteBalanca(rs.getBoolean("ebalanca"));
                    imp.setValidade(rs.getInt("validade"));
                    imp.setSituacaoCadastro(("N".equals(rs.getString("ativo")) ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO));
                    imp.setNcm(rs.getString("ncm"));
                    imp.setCest(rs.getString("cest"));
                    imp.setIcmsCreditoId(rs.getString("icmscredito"));
                    imp.setIcmsDebitoId(rs.getString("icmsdebito"));
                    imp.setPiscofinsCstCredito(rs.getString("cst_pis_entrada"));
                    imp.setPiscofinsCstDebito(rs.getString("cst_pis_saida"));
                    if ((rs.getString("codigo_barras") != null)
                            && (!rs.getString("codigo_barras").trim().isEmpty())
                            && (rs.getString("codigo_barras").trim().length() >= 4)
                            && (rs.getString("codigo_barras").trim().length() <= 6)) {

                        if (v_usar_arquivoBalanca) {
                            ProdutoBalancaVO produtoBalanca;
                            long codigoProduto;
                            codigoProduto = Long.parseLong(imp.getEan().trim());
                            if (codigoProduto <= Integer.MAX_VALUE) {
                                produtoBalanca = produtosBalanca.get((int) codigoProduto);
                            } else {
                                produtoBalanca = null;
                            }
                            if (produtoBalanca != null) {
                                imp.seteBalanca(true);
                                imp.setValidade(produtoBalanca.getValidade() > 1 ? produtoBalanca.getValidade() : rs.getInt("validade"));
                            } else {
                                imp.setValidade(0);
                                imp.seteBalanca(false);
                            }
                        } else {
                            imp.seteBalanca(rs.getString("embalagem2").contains("KG") ? true : false);
                            imp.setValidade(rs.getInt("validade"));
                        }
                    }
                    result.add(imp);
                }
            }
            return result;
        }
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws SQLException {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + " fornecedor,\n"
                    + " produto,\n"
                    + " quantidade_estabelecimento as qtdemb,\n"
                    + " referencia\n"
                    + "from referencias")) {
                while (rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdFornecedor(rs.getString("fornecedor"));
                    imp.setIdProduto(rs.getString("produto"));
                    imp.setQtdEmbalagem(rs.getDouble("qtdemb"));
                    imp.setCodigoExterno("referencia");

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws SQLException {
        List<CreditoRotativoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	cp.lancamento as id,\n"
                    + "	cp.cliente::integer as idcliente,\n"
                    + "	c.cpf as cpfcnpj,\n"
                    + "	cp.data::date as dtemissao,\n"
                    + "	cp.caixa as ecf,\n"
                    + "	cp.cupom as nrcupom,\n"
                    + "	cp.tipopagamento,\n"
                    + "	coalesce(cp.valorcompra,0) as valorcompra,\n"
                    + "	coalesce(cp.valordebito,0) as valordebito,\n"
                    + "	cp.datadeposito::date as dtvencimento,\n"
                    + "	cp.loja as idloja,\n"
                    + "	cp.horario,\n"
                    + "	cp.desconto\n"
                    + "from comprascliente cp\n"
                    + "left join clientes c on cp.cliente = c.codigo_cliente\n"
                    + "where statuscompra = 'D'\n"
                    + "  and loja::integer = " + getLojaOrigem() + "\n"
                    + "  and length(cp.cliente) <= 6\n"
                    + "order by data")) {
                while (rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rs.getString("id"));
                    imp.setIdCliente(rs.getString("idcliente"));
                    imp.setCnpjCliente(rs.getString("cpfcnpj"));
                    imp.setDataEmissao(rs.getDate("dtemissao"));
                    imp.setEcf(rs.getString("ecf"));
                    imp.setNumeroCupom(rs.getString("nrcupom"));
                    imp.setValor(rs.getDouble("valordebito"));
                    imp.setDataVencimento(rs.getDate("dtvencimento"));

                    result.add(imp);
                }
            }
        }
        return result;
    }
}
