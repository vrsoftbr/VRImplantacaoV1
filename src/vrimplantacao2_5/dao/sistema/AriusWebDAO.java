package vrimplantacao2_5.dao.sistema;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2_5.dao.conexao.ConexaoMySQL;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Alan
 */
public class AriusWebDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "ARIUS_WEB";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(new OpcaoProduto[]{
            OpcaoProduto.MERCADOLOGICO_PRODUTO,
            OpcaoProduto.MERCADOLOGICO,
            OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
            OpcaoProduto.IMPORTAR_MANTER_BALANCA,
            OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
            OpcaoProduto.PRODUTOS,
            OpcaoProduto.EAN,
            OpcaoProduto.EAN_EM_BRANCO,
            OpcaoProduto.ATIVO,
            OpcaoProduto.CEST,
            OpcaoProduto.CUSTO,
            OpcaoProduto.PRECO,
            OpcaoProduto.ESTOQUE_MINIMO,
            OpcaoProduto.ESTOQUE_MAXIMO,
            OpcaoProduto.ESTOQUE,
            OpcaoProduto.DATA_ALTERACAO,
            OpcaoProduto.DATA_CADASTRO,
            OpcaoProduto.DESC_COMPLETA,
            OpcaoProduto.DESC_GONDOLA,
            OpcaoProduto.DESC_REDUZIDA,
            OpcaoProduto.MARGEM,
            OpcaoProduto.QTD_EMBALAGEM_EAN,
            OpcaoProduto.QTD_EMBALAGEM_COTACAO,
            OpcaoProduto.TIPO_EMBALAGEM_EAN,
            OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
            OpcaoProduto.NCM,
            OpcaoProduto.PESO_BRUTO,
            OpcaoProduto.PESO_LIQUIDO,
            OpcaoProduto.DESCONTINUADO,
            OpcaoProduto.PESAVEL,
            OpcaoProduto.ICMS,
            OpcaoProduto.PIS_COFINS,
            OpcaoProduto.NATUREZA_RECEITA,
            OpcaoProduto.ATACADO,
            OpcaoProduto.OFERTA,
            OpcaoProduto.FABRICANTE
        }));
    }

    @Override
    public Set<OpcaoFornecedor> getOpcoesDisponiveisFornecedor() {
        return new HashSet<>(Arrays.asList(
                OpcaoFornecedor.DADOS,
                OpcaoFornecedor.RAZAO_SOCIAL,
                OpcaoFornecedor.NOME_FANTASIA,
                OpcaoFornecedor.CNPJ_CPF,
                OpcaoFornecedor.INSCRICAO_ESTADUAL,
                OpcaoFornecedor.INSCRICAO_MUNICIPAL,
                OpcaoFornecedor.PRODUTO_FORNECEDOR,
                OpcaoFornecedor.PAGAR_FORNECEDOR
        ));
    }

    @Override
    public Set<OpcaoCliente> getOpcoesDisponiveisCliente() {
        return new HashSet<>(Arrays.asList(
                OpcaoCliente.DADOS,
                OpcaoCliente.CNPJ,
                OpcaoCliente.INSCRICAO_ESTADUAL,
                OpcaoCliente.RAZAO,
                OpcaoCliente.FANTASIA,
                OpcaoCliente.SITUACAO_CADASTRO,
                OpcaoCliente.CONTATOS,
                OpcaoCliente.DATA_NASCIMENTO,
                OpcaoCliente.TELEFONE,
                OpcaoCliente.CELULAR,
                OpcaoCliente.EMAIL));
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	codigo id,\n"
                    + "	descricao,\n"
                    + "	case\n"
                    + "	  when codigo = 'F' then '60'\n"
                    + "	  when codigo = 'I' then '40'\n"
                    + "	  when codigo = 'Z' then '00'\n"
                    + "	  when codigo = 'Y' then '00'\n"
                    + "	  when codigo = 'X' then '00'\n"
                    + "	  when codigo = 'D' then '51'\n"
                    + "   when codigo = 'N' then '41'\n"
                    + "	  else '00'\n"
                    + "	end cst,\n"
                    + "	percentual aliq,\n"
                    + "	0 red\n"
                    + "from\n"
                    + "	controle.tributacoes t\n"
                    + "order by 1"
            )) {
                while (rs.next()) {
//                    String id = rs.getString("cst") + "-" + rs.getString("aliq") + "-" + rs.getString("red");
                    result.add(new MapaTributoIMP(
                            rs.getString("id"),
                            rs.getString("descricao"),
                            rs.getInt("cst"),
                            rs.getDouble("aliq"),
                            rs.getDouble("red")));
                }
            }
        }

        return result;
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> Result = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	codigoean id_produto,\n"
                    + "	codigoean codigobarras,\n"
                    + "	unidade\n"
                    + "from\n"
                    + "	mercador m\n"
                    + "where\n"
                    + "	nroloja = 1\n"
                    + "	and codigoint = 0"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("id_produto"));
                    imp.setEan(rst.getString("codigobarras"));
                    imp.setTipoEmbalagem(rst.getString("unidade"));
                    imp.setQtdEmbalagem(1);

                    Result.add(imp);
                }
            }
        }
        return Result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	m1.codigo merc1,\n"
                    + "	m1.descricao descmerc1,\n"
                    + "	COALESCE (m2.codigo, m1.codigo) merc2,\n"
                    + "	COALESCE (m2.descricao, m1.descricao) descmerc2\n"
                    + "from\n"
                    + "	controle.secoes m1\n"
                    + "	left join controle.grupos m2 on m1.codigo = m2.codsecao\n"
                    + "order by 1, 3")) {
                while (rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rs.getString("merc1"));
                    imp.setMerc1Descricao(rs.getString("descmerc1"));
                    imp.setMerc2ID(rs.getString("merc2"));
                    imp.setMerc2Descricao(rs.getString("descmerc2"));
                    imp.setMerc3ID(rs.getString("merc2"));
                    imp.setMerc3Descricao(rs.getString("descmerc2"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	case when codigoint = 0 then codigoean else codigoint end id,\n"
                    + " case when flg_eanvalido = 0 then 1 else 0 end pesavel,\n"
                    + "	codigoean ean,\n"
                    + "	descricao_completa,\n"
                    + "	descricao desc_reduzida,\n"
                    + "	custo precocusto,\n"
                    + "	valor precovenda,\n"
                    + "	unidade,\n"
                    + "	estoque_atual estoque,\n"
                    + "	depto merc1,\n"
                    + "	case when Grupo = 0 then depto end merc2,\n"
                    + "	case when Grupo = 0 then depto end merc3,\n"
                    + "	tributacao id_icms,\n"
                    + "	ncm,\n"
                    + "	cest,\n"
                    + "	cst_pis_cofins pis_cofins\n"
                    + "from\n"
                    + "	mercador p\n"
                    + "where\n"
                    + "	nroloja = " + getLojaOrigem() + "\n"
                    + "order by 1"
            )) {
                Map<Integer, vrimplantacao2.vo.cadastro.ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().getProdutosBalanca();
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rs.getString("id"));
                    imp.setEan(rs.getString("ean"));
                    imp.seteBalanca(rs.getBoolean("pesavel"));

                    ProdutoBalancaVO bal = produtosBalanca.get(Utils.stringToInt(imp.getImportId(), -2));

                    if (bal != null) {
                        imp.seteBalanca(true);
                        imp.setTipoEmbalagem("P".equals(bal.getPesavel()) ? "KG" : "UN");
                        imp.setEan(imp.getImportId());
                    }
                    
                    imp.setDescricaoCompleta(rs.getString("descricao_completa"));
                    imp.setDescricaoReduzida(rs.getString("desc_reduzida"));
                    imp.setDescricaoGondola(imp.getDescricaoReduzida());

                    imp.setTipoEmbalagem(rs.getString("unidade"));

                    imp.setCodMercadologico1("merc1");
                    imp.setCodMercadologico2("merc2");
                    imp.setCodMercadologico3("merc3");
                    
                    imp.setPrecovenda(rs.getDouble("precovenda"));
                    imp.setCustoComImposto(rs.getDouble("precocusto"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    
                    imp.setEstoque(rs.getDouble("estoque"));

                    imp.setNcm(rs.getString("ncm"));
                    imp.setCest(rs.getString("cest"));
//                    imp.setSituacaoCadastro(rs.getInt("situacao") == 0 ? 1 : 0);

                    imp.setIcmsDebitoId(rs.getString("id_icms"));
                    imp.setIcmsDebitoForaEstadoId(imp.getIcmsDebitoId());
                    imp.setIcmsDebitoForaEstadoNfId(imp.getIcmsDebitoId());
                    imp.setIcmsConsumidorId(imp.getIcmsDebitoId());
                    imp.setIcmsCreditoId(imp.getIcmsDebitoId());
                    imp.setIcmsCreditoForaEstadoId(imp.getIcmsCreditoId());

                    imp.setPiscofinsCstDebito(rs.getString("pis_cofins"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    /*@Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	FORNEC id_fornecedor,\n"
                    + "	e.ID id_produto,\n"
                    + "	pf.CODPLU codigoexterno,\n"
                    + "	pf.EMB embalagem,\n"
                    + "	pf.QTEMB qtdembalagem\n"
                    + "from 	\n"
                    + "	plu pf \n"
                    + "join estoque e on pf.codigo = e.CODIGO")) {
                while (rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdFornecedor(rs.getString("id_fornecedor"));
                    imp.setIdProduto(rs.getString("id_produto"));
                    imp.setCodigoExterno(rs.getString("codigoexterno"));
                    imp.setQtdEmbalagem(rs.getDouble("qtdembalagem"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	codigo,\n"
                    + "	cliente razao,\n"
                    + "	fantasia,\n"
                    + "	cnpj,\n"
                    + "	insc ie,\n"
                    + "	endereco,\n"
                    + "	bairro,\n"
                    + "	cidade,\n"
                    + "	cep,\n"
                    + "	estado,\n"
                    + "	numero,\n"
                    + "	codmunicip municipio_ibge,\n"
                    + "	telefone,\n"
                    + "	telefone2,\n"
                    + "	telefone3,\n"
                    + "	contato,\n"
                    + "	contato2,\n"
                    + "	contato3,\n"
                    + "	prazo,\n"
                    + "	vendedor,\n"
                    + "	regime,\n"
                    + "	tipocontribuinte,\n"
                    + "	emailxml,\n"
                    + "	datacad cadastro\n"
                    + "from \n"
                    + "	fornec")) {
                while (rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("codigo"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setCnpj_cpf(rs.getString("cnpj"));
                    imp.setIe_rg(rs.getString("ie"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setCep(rs.getString("cep"));
                    imp.setUf(rs.getString("estado"));
                    imp.setIbge_municipio(rs.getInt("municipio_ibge"));
                    imp.setTel_principal(rs.getString("telefone"));

                    String email = rs.getString("emailxml");

                    if (email != null && !email.trim().isEmpty()) {
                        imp.addContato("1", "EMAIL", null, null, TipoContato.NFE, email);
                    }

                    String telfone3 = rs.getString("telefone3");

                    if (telfone3 != null && !telfone3.trim().isEmpty()) {
                        imp.addContato("2", "TELEFONE2", telfone3, null, TipoContato.NFE, null);
                    }

                    String contato = rs.getString("vendedor");

                    if (contato != null && !contato.trim().isEmpty()) {
                        imp.addContato("3", contato, null, null, TipoContato.NFE, null);
                    }

                    result.add(imp);
                }
            }
        }

        return result;
    }*/

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	Convenio id,\n"
                    + "	CodCgcCpf cpf_cnpj,\n"
                    + "	IE ie_rg,\n"
                    + "	Nome,\n"
                    + "	Endereco,\n"
                    + "	numero_predio numero,\n"
                    + "	complemento,\n"
                    + "	Bairro,\n"
                    + "	Cidade,\n"
                    + "	uf,\n"
                    + "	Cep,\n"
                    + "	Fone,\n"
                    + "	email,\n"
                    + "	observacao,\n"
                    + "	case when status_cli = 'A' then 1 else 0 end situacao\n"
                    + "from\n"
                    + "	clientes c")) {
                while (rs.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rs.getString("id"));
                    imp.setCnpj(rs.getString("cpf_cnpj"));
                    imp.setInscricaoestadual(rs.getString("ie_rg"));
                    imp.setRazao(rs.getString("nome"));
                    imp.setFantasia(rs.getString("nome"));

                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setCep(rs.getString("cep"));
                    imp.setUf(rs.getString("uf"));
                    imp.setTelefone(rs.getString("fone"));
                    imp.setEmail(rs.getString("email"));
                    imp.setObservacao(rs.getString("observacao"));
                    imp.setAtivo(rs.getBoolean("situacao"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	id,\n"
                    + "	codcli idcliente,\n"
                    + "	cliente,\n"
                    + "	pedido,\n"
                    + "	pdv,\n"
                    + "	duplicata,\n"
                    + "	valor,\n"
                    + "	dtvencimento,\n"
                    + "	dtemissao\n"
                    + "from \n"
                    + "	dupreceber d\n"
                    + "where \n"
                    + "	ValorPago is null")) {
                while (rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();

                    imp.setId(rs.getString("id"));
                    imp.setIdCliente(rs.getString("idcliente"));
                    imp.setDataEmissao(rs.getDate("dtemissao"));
                    imp.setDataVencimento(rs.getDate("dtvencimento"));
                    imp.setValor(rs.getDouble("valor"));
                    imp.setEcf(rs.getString("pdv"));
                    imp.setNumeroCupom(rs.getString("pedido"));

                    result.add(imp);
                }
            }
        }

        return result;
    }
}
