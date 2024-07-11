package vrimplantacao2_5.dao.sistema;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import vrimplantacao2_5.dao.conexao.ConexaoMySQL;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Bruno
 */
public class ImperiumMarket2_5DAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "IMPERIUMMARKET";
    }

    @Override
    public Set<OpcaoFornecedor> getOpcoesDisponiveisFornecedor() {
        return new HashSet<>(Arrays.asList(
                OpcaoFornecedor.ENDERECO,
                OpcaoFornecedor.DADOS,
                OpcaoFornecedor.CONTATOS,
                OpcaoFornecedor.SITUACAO_CADASTRO,
                OpcaoFornecedor.TIPO_EMPRESA,
                OpcaoFornecedor.PAGAR_FORNECEDOR,
                OpcaoFornecedor.PRODUTO_FORNECEDOR
        ));
    }

    @Override
    public Set<OpcaoCliente> getOpcoesDisponiveisCliente() {
        return new HashSet<>(Arrays.asList(
                OpcaoCliente.DADOS,
                OpcaoCliente.CNPJ,
                OpcaoCliente.INSCRICAO_ESTADUAL,
                OpcaoCliente.ENDERECO,
                OpcaoCliente.CONTATOS,
                OpcaoCliente.DATA_CADASTRO,
                OpcaoCliente.DATA_NASCIMENTO,
                OpcaoCliente.RECEBER_CREDITOROTATIVO,
                OpcaoCliente.CONVENIO_EMPRESA,
                OpcaoCliente.CONVENIO_TRANSACAO,
                OpcaoCliente.RECEBER_CHEQUE,
                OpcaoCliente.CONVENIO_CONVENIADO));
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                new OpcaoProduto[]{
                    OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                    OpcaoProduto.MERCADOLOGICO_PRODUTO,
                    OpcaoProduto.IMPORTAR_GERAR_SUBNIVEL_MERC,
                    OpcaoProduto.MERCADOLOGICO,
                    OpcaoProduto.FAMILIA_PRODUTO,
                    OpcaoProduto.FAMILIA,
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
                    OpcaoProduto.CUSTO_COM_IMPOSTO,
                    OpcaoProduto.CUSTO_SEM_IMPOSTO,
                    OpcaoProduto.ESTOQUE,
                    OpcaoProduto.ATIVO,
                    OpcaoProduto.NCM,
                    OpcaoProduto.CEST,
                    OpcaoProduto.PIS_COFINS,
                    OpcaoProduto.NATUREZA_RECEITA,
                    OpcaoProduto.ICMS,
                    OpcaoProduto.ICMS_CONSUMIDOR,
                    OpcaoProduto.ICMS_SAIDA,
                    OpcaoProduto.ICMS_SAIDA_FORA_ESTADO,
                    OpcaoProduto.ICMS_SAIDA_NF,
                    OpcaoProduto.ICMS_ENTRADA,
                    OpcaoProduto.ICMS_ENTRADA_FORA_ESTADO,
                    OpcaoProduto.PAUTA_FISCAL,
                    OpcaoProduto.PAUTA_FISCAL_PRODUTO,
                    OpcaoProduto.MARGEM,
                    OpcaoProduto.MARGEM_MAXIMA,
                    OpcaoProduto.MARGEM_MINIMA,
                    OpcaoProduto.OFERTA,
                    OpcaoProduto.PAUTA_FISCAL,
                    OpcaoProduto.PAUTA_FISCAL_PRODUTO
                }
        ));
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	distinct \n"
                    + "	concat(icms_alqt_e,'.',icms_alqt_s)as id,\n"
                    + "	concat(icms_alqt_e,' RDZ ',icms_rbc_e,'%') as descricao,\n"
                    + "	icms_cst_e as cst,\n"
                    + "	icms_alqt_e as aliq,\n"
                    + "	icms_rbc_e as red\n"
                    + "from\n"
                    + "	mxf_vw_produtos"
            )) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(
                            rs.getString("id"),
                            rs.getString("descricao"),
                            rs.getInt("cst"),
                            rs.getDouble("aliq"),
                            rs.getDouble("red"))
                    );
                }
            }
        }

        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "  distinct \n"
                    + "	g.IDGRUPO as merc1,\n"
                    + "	g.NOME as desc1,\n"
                    + "	s2.id as merc2,\n"
                    + "	s2.Nome desc2,\n"
                    + "	s.id as merc3,\n"
                    + "	s.nome as desc3\n"
                    + "from\n"
                    + "	subgrupo1 s\n"
                    + "	left join grupo g on g.IDGRUPO = s.idgrupo \n"
                    + "	left join subgrupo s2 on s2.idSubGrupo = s.idsubgrupo \n"
                    + "  where g.IDGRUPO is not null\n"
                    + "  order by 1,3,5"
            )) {
                while (rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rs.getString("merc1"));
                    imp.setMerc1Descricao(rs.getString("desc1"));
                    imp.setMerc2ID(rs.getString("merc2") == null ? rs.getString("merc1") : rs.getString("merc2"));
                    imp.setMerc2Descricao(rs.getString("desc2") == null ? rs.getString("desc1") : rs.getString("merc2"));
                    imp.setMerc3ID(rs.getString("merc3"));
                    imp.setMerc3Descricao(rs.getString("desc3"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "idProduto as id_produto,\n"
                    + "CodigoEan as ean,\n"
                    + "qtde_emb as embalagem\n"
                    + "from\n"
                    + "	produto_ean pe "
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id_produto"));
                    imp.setEan(rst.getString("ean"));
                    imp.setQtdEmbalagem(rst.getInt("embalagem"));

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
                    + "	p.idProduto as id, \n"
                    + "	PesoVariavel as ebalanca,\n"
                    + "	p.Ean,\n"
                    + "	ean1 ,\n"
                    + "	Descricao as descricaocompleta,\n"
                    + "	DescrRed as descricaoreduzida,\n"
                    + "	descricaoetq as descricaogondola,\n"
                    + "	UnidEntra as tipoembalagem,\n"
                    + "	EmbSaida as qtdembalagem,\n"
                    + "	ClassFiscal as ncm,\n"
                    + "	cest as cest,\n"
                    + "	idSituacao as ativo,\n"
                    + "	e.estoque_atual as estoque,\n"
                    + "	p.idGrupo as merc1,\n"
                    + "	p.idSubGrupo as merc2,\n"
                    + "	p.idSubGrupo1  as merc3,\n"
                    + "	pp.MARGEM as margem,\n"
                    + "	pp.CUSTO as custo,\n"
                    + "	pp.VENDA1 as precovenda,\n"
                    + "	concat(icms_alqt_e,'.',icms_alqt_s)as idicms\n"
                    + "from\n"
                    + "	produto p \n"
                    + "	left join produto_tributacao pt on p.idProduto = pt.idproduto \n"
                    + "	left join produto_estoque e on e.idProduto  = p.idProduto \n"
                    + "	left join produto_preco pp on pp.IDPRODUTO = p.idProduto\n"
                    + "	left join mxf_vw_produtos  m on m.codigo_produto = p.idProduto "
            )) {

                Map<Integer, vrimplantacao2.vo.cadastro.ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().getProdutosBalanca();
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rs.getString("id"));
                    imp.seteBalanca(rs.getBoolean("ebalanca"));
                    imp.setEan(rs.getString("ean"));

                    ProdutoBalancaVO bal = produtosBalanca.get(Utils.stringToInt(rs.getString("id"), -2));

                    if (bal != null) {
                        imp.seteBalanca(true);
                        imp.setTipoEmbalagem("P".equals(bal.getPesavel()) ? "KG" : "UN");
                        imp.setValidade(bal.getValidade());
                        imp.setEan(String.valueOf(bal.getCodigo()));
                    }

                    imp.setDescricaoCompleta(rs.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rs.getString("descricaocompleta"));
                    imp.setDescricaoGondola(rs.getString("descricaogondola") == null
                            ? imp.getDescricaoReduzida()
                            : rs.getString("descricaogondola"));

                    imp.setTipoEmbalagem(rs.getString("tipoembalagem"));
                    imp.setTipoEmbalagemCotacao(rs.getString("tipoembalagem"));
                    imp.setQtdEmbalagem(rs.getInt("qtdembalagem"));
                    imp.setTipoEmbalagemVolume(imp.getTipoEmbalagemCotacao());

                    imp.setNcm(rs.getString("ncm"));
                    imp.setCest(rs.getString("cest"));
                    imp.setSituacaoCadastro(rs.getInt("ativo") == 2 ? SituacaoCadastro.EXCLUIDO : SituacaoCadastro.ATIVO);

                    imp.setEstoque(rs.getDouble("estoque"));

                    imp.setCodMercadologico1(rs.getString("merc1"));
                    imp.setCodMercadologico2(rs.getString("merc2"));
                    imp.setCodMercadologico3(rs.getString("merc3"));

                    imp.setMargem(rs.getDouble("margem"));
                    imp.setCustoComImposto(rs.getDouble("custo"));
                    imp.setPrecovenda(rs.getDouble("precovenda"));

                    String idIcms;

                    idIcms = rs.getString("idicms");

                    imp.setIcmsDebitoId(idIcms);
                    imp.setIcmsDebitoForaEstadoId(idIcms);
                    imp.setIcmsDebitoForaEstadoNfId(idIcms);
                    imp.setIcmsConsumidorId(idIcms);
                    imp.setIcmsCreditoId(idIcms);
                    imp.setIcmsCreditoForaEstadoId(idIcms);

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	idProduto as produtoid,\n"
                    + "	idFornecedor as fornecedorid,\n"
                    + "	Referencia as codigoexterno\n"
                    + "from\n"
                    + "	itensfornecedor i "
            )) {
                while (rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rs.getString("produtoid"));
                    imp.setIdFornecedor(rs.getString("fornecedorid"));
                    imp.setCodigoExterno(rs.getString("codigoexterno"));

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
                    "select\n"
                    + "	IDFORNECEDOR as id, \n"
                    + "	NOME as razao,\n"
                    + "	FANTASIA as fantasia,\n"
                    + "	RG_IE as ie,\n"
                    + "	CPF_CGC as cnpj_cnpf,\n"
                    + "	ENDERECO ,\n"
                    + "	NUMERO ,\n"
                    + "	COMPLEMENTO ,\n"
                    + "	BAIRRO ,\n"
                    + "	CIDADE as municipio,\n"
                    + "	CEP ,\n"
                    + "	TELEFONE \n"
                    + "from\n"
                    + "	fornecedor f"
            )) {
                while (rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());

                    imp.setImportId(rs.getString("id"));
                    imp.setRazao(Utils.acertarTexto(rs.getString("razao")));
                    imp.setFantasia(Utils.acertarTexto(rs.getString("fantasia")));
                    imp.setIe_rg(rs.getString("ie"));
                    imp.setCnpj_cpf(rs.getString("cnpj_cnpf"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("COMPLEMENTO"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("municipio"));
                    imp.setCep(rs.getString("cep"));
                    imp.setTel_principal(rs.getString("telefone"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	idCliente as id,\n"
                    + "	trim(cpf) as cpf,\n"
                    + "	rg ,\n"
                    + "	nome ,\n"
                    + "	endereco ,\n"
                    + "	numero ,\n"
                    + "	complemento ,\n"
                    + "	bairro ,\n"
                    + "	cidade ,\n"
                    + "	uf ,\n"
                    + "	cep ,\n"
                    + "	celular ,\n"
                    + "	status_cadastro \n"
                    + "from\n"
                    + "	cliente c"
            )) {
                while (rs.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rs.getString("id"));
                    imp.setCnpj(rs.getString("cpf"));
                    imp.setInscricaoestadual(rs.getString("rg"));
                    imp.setRazao(rs.getString("nome"));
                    imp.setFantasia(rs.getString("nome"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));
                    imp.setTelefone(rs.getString("celular"));

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
                    "  select\n"
                    + "  IDDEBITO as id,\n"
                    + "  IDCLIENTE  as id_cliente,\n"
                    + "  DT_VENDA as emissao,\n"
                    + "  DT_VENC as vencto,\n"
                    + "  VL_VISTA as valor \n"
                    + "  from debito d \n"
                    + "  where situacao != 'P'"
            )) {
                while (rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();

                    imp.setId(rs.getString("id"));
                    imp.setParcela(1);
                    imp.setIdCliente(rs.getString("id_cliente"));
                    imp.setDataEmissao(rs.getDate("emissao"));
                    imp.setDataVencimento(rs.getDate("vencto"));
                    imp.setValor(rs.getDouble("valor"));

                    result.add(imp);
                }
            }
        }
        return result;
    }
}
