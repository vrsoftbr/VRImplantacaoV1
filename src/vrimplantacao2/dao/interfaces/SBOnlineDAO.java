package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao.dao.cadastro.ProdutoDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoSexo;
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
public class SBOnlineDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "SBOnline";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                new OpcaoProduto[]{
                    OpcaoProduto.MERCADOLOGICO_PRODUTO,
                    OpcaoProduto.MERCADOLOGICO_POR_NIVEL,
                    OpcaoProduto.MANTER_CODIGO_MERCADOLOGICO,
                    OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                    OpcaoProduto.FAMILIA_PRODUTO,
                    OpcaoProduto.FAMILIA,
                    OpcaoProduto.PRODUTOS,
                    OpcaoProduto.EAN,
                    OpcaoProduto.EAN_EM_BRANCO,
                    OpcaoProduto.DATA_CADASTRO,
                    OpcaoProduto.TIPO_EMBALAGEM_EAN,
                    OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                    OpcaoProduto.QTD_EMBALAGEM_COTACAO,
                    OpcaoProduto.QTD_EMBALAGEM_EAN,
                    OpcaoProduto.PESAVEL,
                    OpcaoProduto.VALIDADE,
                    OpcaoProduto.DESC_COMPLETA,
                    OpcaoProduto.DESC_GONDOLA,
                    OpcaoProduto.DESC_REDUZIDA,
                    OpcaoProduto.ESTOQUE_MAXIMO,
                    OpcaoProduto.ESTOQUE_MINIMO,
                    OpcaoProduto.PRECO,
                    OpcaoProduto.CUSTO,
                    OpcaoProduto.CUSTO_COM_IMPOSTO,
                    OpcaoProduto.CUSTO_SEM_IMPOSTO,
                    OpcaoProduto.ESTOQUE_MAXIMO,
                    OpcaoProduto.ESTOQUE_MINIMO,
                    OpcaoProduto.ESTOQUE,
                    OpcaoProduto.ATIVO,
                    OpcaoProduto.NCM,
                    OpcaoProduto.CEST,
                    OpcaoProduto.PIS_COFINS,
                    OpcaoProduto.NATUREZA_RECEITA,
                    OpcaoProduto.ICMS,
                    OpcaoProduto.MARGEM,
                    OpcaoProduto.OFERTA,
                    OpcaoProduto.VOLUME_QTD
                }
        ));
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select cnpj, razao from firma"
            )) {
                while (rs.next()) {
                    result.add(new Estabelecimento(rs.getString("cnpj"), rs.getString("razao")));
                }
            }
        }

        return result;
    }

    public List<String> getNomeLojaCliente() throws Exception {
        List<String> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select fantasia from firma"
            )) {
                while (rst.next()) {
                    result.add(rst.getString("fantasia"));
                }
            }
        }

        return result;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    ""
            )) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(
                            rs.getString(""),
                            rs.getString(""),
                            0,
                            rs.getDouble(""),
                            rs.getDouble("")
                    ));
                }
            }
        }
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	f.CODIGO id,\n"
                    + "	f.RAZAO razao,\n"
                    + "	f.FANTASIA fantasia,\n"
                    + "	CURRENT_TIMESTAMP as datacadastro,\n"
                    + "	f.ENDERECO,\n"
                    + "	'0' as numero,\n"
                    + "	f.COMPLEMENTO,\n"
                    + "	f.BAIRRO,\n"
                    + "	f.CIDADE,\n"
                    + "	f.UF,\n"
                    + "	f.CEP,\n"
                    + "	isnull(f.DDD,'') + f.FONE fone1,\n"
                    + "	isnull(f.DDD,'') + f.CELULAR celular,\n"
                    + "	isnull(f.DDD,'') + f.FAX fax,\n"
                    + "	f.CNPJ,\n"
                    + "	f.INSC_ESTA inscricaoestadual,\n"
                    + "	f.EMAIL as observacao,\n"
                    + "	1 as id_situacaocadastro\n"
                    + "from\n"
                    + "	FORNECE f\n"
                    + "order by codigo"
            )) {
                while (rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    
                    imp.setImportId(rs.getString("id"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString(""));
                    imp.setDatacadastro(rs.getDate("datacadastro"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));
                    imp.setTel_principal(rs.getString("fone1"));
                    imp.setCnpj_cpf(rs.getString("cnpj"));
                    imp.setIe_rg(rs.getString("inscricaoestadual"));
                    imp.setObservacao(rs.getString("observacao"));
                    imp.setAtivo(rs.getBoolean("id_situacaocadastro"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    public List<ProdutoFornecedorIMP> getProdutosFornecedor() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    ""
            )) {
                while (rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
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
                    + "	c.CODIGO id,\n"
                    + "	c.NOME,\n"
                    + "	c.ENDERE,\n"
                    + "	c.NumLogr,\n"
                    + "	c.COMPLE,\n"
                    + "	c.BAIRRO,\n"
                    + "	c.ESTADO,\n"
                    + "	c.CIDADE,\n"
                    + "	c.CEP,\n"
                    + "	c.FONE,\n"
                    + "	c.INSCRG,\n"
                    + " case when c.CPF is null then c.CGC else c.CPF end cpf_cnpj,\n"
                    + "	c.SEXO,\n"
                    + "	c.EMAIL,\n"
                    + "	c.LIMITE,\n"
                    + "	c.FONECOMP1,\n"
                    + "	c.FONECOMP2,\n"
                    + "	c.FONECOMP3,\n"
                    + "	c.CELULA,\n"
                    + "	c.BloqueiaCheque,\n"
                    + "	c.BloqueiaFatura,\n"
                    + "	c.OBS,\n"
                    + "	c.EMAIL,\n"
                    + "	c.DTNASCI,\n"
                    + "	c.DTCADAS,\n"
                    + "	c.ATIVIDADE cargo,\n"
                    + "	c.CONJUGE\n"
                    + "from\n"
                    + "	Cliente c\n"
                    + "order by\n"
                    + "	c.CODIGO"
            )) {
                while (rs.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rs.getString("id"));
                    imp.setRazao(rs.getString("nome"));
                    imp.setEndereco(rs.getString("endere"));
                    imp.setNumero(rs.getString("numlogr"));
                    imp.setComplemento(rs.getString("comple"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setUf(rs.getString("estado"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setCep(rs.getString("cep"));
                    imp.setTelefone(rs.getString("fone"));
                    imp.setInscricaoestadual(rs.getString("inscrg"));
                    imp.setCnpj(rs.getString("cpf_cnpj"));
                    imp.setSexo("F".equals(rs.getString("SEXO")) ? TipoSexo.FEMININO : TipoSexo.MASCULINO);
                    imp.setEmail(rs.getString("email"));
                    imp.setLimiteCompra(rs.getDouble("limite"));
                    imp.setCelular(rs.getString("celula"));
                    imp.setPermiteCheque(rs.getBoolean("bloqueiacheque"));
                    imp.setPermiteCreditoRotativo(rs.getBoolean("bloqueiafatura"));
                    imp.setObservacao(rs.getString("obs"));
                    imp.setDataCadastro(rs.getDate("dtcadas"));
                    imp.setDataNascimento(rs.getDate("dtnasci"));
                    imp.setCargo(rs.getString("cargo"));
                    imp.setNomeConjuge(rs.getString("conjuge"));

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
                    ""
            )) {
                while (rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();

                    result.add(imp);
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
                    "select\n"
                    + "	distinct\n"
                    + "	g.CODGRUPO merc1,\n"
                    + "	g.NOMEGRUPO descmerc1,\n"
                    + "	sg.CODSUB merc2,\n"
                    + "	sg.NOMESUB descmerc2,\n"
                    + "	sg.CODSUB merc3,\n"
                    + "	sg.NOMESUB descmerc3\n"
                    + "from\n"
                    + "	Grupo g\n"
                    + "left join SubGrupo sg on g.CODGRUPO = sg.CODGRUPO"
            )) {
                while (rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();

                    imp.setMerc1ID(rs.getString("merc1"));
                    imp.setMerc1Descricao(rs.getString("descmerc1"));
                    imp.setMerc2ID(rs.getString("merc2"));
                    imp.setMerc2Descricao(rs.getString("descmerc2"));
                    imp.setMerc3ID(rs.getString("merc3"));
                    imp.setMerc3Descricao(rs.getString("descmerc3"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	CodigoBar ean,\n"
                    + "	Codigo id_produto,\n"
                    + "	Unidade id_tipoembalagem\n"
                    + "from\n"
                    + "	Produto p\n"
                    + "union\n"
                    + "select\n"
                    + "	ca.CodBar ean,\n"
                    + "	ca.CodProd id_produto,\n"
                    + "	p.Unidade id_tipoembalagem\n"
                    + "from\n"
                    + "	CodAdicional ca\n"
                    + "join Produto p on\n"
                    + "	ca.CodProd = p.Codigo"
            )) {
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id_produto"));
                    imp.setEan(rs.getString("ean"));
                    imp.setTipoEmbalagem(rs.getString("id_tipoembalagem"));

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
                    ""
            )) {
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().getProdutosBalanca();
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    result.add(imp);
                }
            }
        }
        return result;
    }
}
