package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import vrimplantacao.classe.ConexaoAccess;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 * 
 * @author Alan
 */
public class StockDAO extends InterfaceDAO implements MapaTributoProvider {

    private static final Logger LOG = Logger.getLogger(StockDAO.class.getName());

    @Override
    public String getSistema() {
        return "Stock";
    }

    public List<Estabelecimento> getLojaCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();

        try (Statement stm = ConexaoAccess.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + "	empid as codigo,\n"
                    + "	empnome as descricao\n"
                    + "FROM\n"
                    + "	tbempresas"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("codigo"), rst.getString("descricao")));
                }
            }
        }

        return result;
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.FAMILIA,
                OpcaoProduto.FAMILIA_PRODUTO,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.QTD_EMBALAGEM_EAN,
                OpcaoProduto.ATIVO,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.ESTOQUE_MAXIMO,
                OpcaoProduto.ESTOQUE_MINIMO,
                OpcaoProduto.PRECO,
                OpcaoProduto.CUSTO,
                OpcaoProduto.MARGEM,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.VALIDADE,
                OpcaoProduto.DATA_CADASTRO,
                OpcaoProduto.PESO_BRUTO,
                OpcaoProduto.PESO_LIQUIDO,
                OpcaoProduto.NCM,
                OpcaoProduto.CEST,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.NATUREZA_RECEITA,
                OpcaoProduto.PAUTA_FISCAL,
                OpcaoProduto.ICMS,
                OpcaoProduto.PAUTA_FISCAL_PRODUTO,
                OpcaoProduto.OFERTA,
                OpcaoProduto.CODIGO_BENEFICIO,
                OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS
        ));
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoAccess.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	triid,\n"
                    + "	tridestributo,\n"
                    + "	imendes,\n"
                    + "	tricstcsosnsaida\n"
                    + "FROM\n"
                    + "	tbtributacoes"
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(
                            rst.getString("triid"),
                            rst.getString("tridestributo"),
                            rst.getInt("tricstcsosnsaida"),
                            rst.getDouble("imendes"),
                            0
                    ));
                }
            }
        }

        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoAccess.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	depid as merc1,\n"
                    + "	depdesdepartamento as desmerc1,\n"
                    + "	depid as merc2,\n"
                    + "	depdesdepartamento as desmerc2,\n"
                    + "	depid as merc3,\n"
                    + "	depdesdepartamento as desmerc3\n"
                    + "FROM\n"
                    + "	tbGrupos\n"
                    + "ORDER by 1,3,5"
            )) {
                while (rst.next()) {

                    MercadologicoIMP imp = new MercadologicoIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());

                    imp.setMerc1ID(rst.getString("merc1"));
                    imp.setMerc1Descricao(rst.getString("desmerc1"));
                    imp.setMerc2ID(rst.getString("merc2"));
                    imp.setMerc2Descricao(rst.getString("desmerc2"));
                    imp.setMerc3ID(rst.getString("merc3"));
                    imp.setMerc3Descricao(rst.getString("desmerc3"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoAccess.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	 proid as id,\n"
                    + "	 procodbarras as ean,\n"
                    + "	 pronomproduto as descricao,\n"
                    + "	 proabrproduto as reduzida,\n"
                    + "	 prodesunidade as unidade,\n"
                    + "	 provalprecovenda as precovenda,\n"
                    + "	 provalcusto as precocusto,\n"
                    + "	 prolucro as margem,\n"
                    + "	 proqntminima as estoque_min,\n"
                    + "	 proqntestoque as estoque,\n"
                    + "	 proCodDepartamento as merc1,\n"
                    + "	 proCodDepartamento as merc2,\n"
                    + "	 proCodDepartamento as merc3,\n"
                    + "	 propeso as pesobruto,\n"
                    + "	 prodataalterado as data_alteracao,\n"
                    + "	 proncm as ncm,\n"
                    + "  procest as cest,\n"
                    + "	 proflagbalanca as e_balanca,\n"
                    + "  proCodTributo as id_icms,\n"
                    + "  proCST_ENTRADA as pc_entrada,\n"
                    + "  proCST_SAIDA as pc_saida,\n"
                    + "	 procodnatreceita as nat_receita"
                    + "FROM\n"
                    + "	 tbprodutos p"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setEan(rst.getString("ean"));
                    imp.setDescricaoCompleta(Utils.acertarTexto(rst.getString("descricao")));
                    imp.setDescricaoReduzida(Utils.acertarTexto(rst.getString("reduzida")));
                    imp.setDescricaoGondola(Utils.acertarTexto(rst.getString("reduzida")));

                    imp.setTipoEmbalagem(rst.getString("unidade"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setCustoSemImposto(rst.getDouble("precocusto"));
                    imp.setCustoComImposto(rst.getDouble("precocusto"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setEstoqueMinimo(Utils.stringToDouble(rst.getString("estoque_min")));
                    imp.setEstoque(Utils.stringToDouble(rst.getString("estoque")));

                    imp.setCodMercadologico1(rst.getString("merc1"));
                    imp.setCodMercadologico2(rst.getString("merc2"));
                    imp.setCodMercadologico3(rst.getString("merc3"));

                    imp.setPesoBruto(rst.getDouble("pesobruto"));
                    imp.setDataAlteracao(rst.getDate("data_alteracao"));

                    imp.seteBalanca(rst.getBoolean("e_balanca"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));

                    imp.setCodMercadologico1(rst.getString("merc1"));
                    imp.setCodMercadologico2(imp.getCodMercadologico1());
                    imp.setCodMercadologico3(imp.getCodMercadologico1());

                    //imp.setIcmsCreditoId(rst.getString("id_icms"));
                    //imp.setIcmsDebitoId(rst.getString("id_icms"));
                    //imp.setIcmsConsumidorId(rst.getString("id_icms"));
                    
                    imp.setPiscofinsCstCredito(rst.getString("pc_entrada"));
                    imp.setPiscofinsCstDebito(rst.getString("pc_saida"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("nat_receita"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoAccess.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "	forId as id,\n"
                    + "	forDesFornecedor as razao,\n"
                    + "	forAbrFornecedor as fantasia,\n"
                    + "	forCGC as cnpj,\n"
                    + "	forInsc as ie,\n"
                    + "	forEndereco as endereco,\n"
                    + "	fornro as numero,\n"
                    + "	forcomplemento as complemento,\n"
                    + "	forBairro as bairro,\n"
                    + "	forCidade as cidade,\n"
                    + "	forEstado as uf,\n"
                    + "	forCEP as cep,\n"
                    + "	forEmail as email,\n"
                    + "	forTelefone as fone,\n"
                    + "	forObservacao as obs\n"
                    + "FROM\n"
                    + "	tbFornecedores\n"
                    + " ORDER BY 1")) {
                while (rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("id"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));

                    imp.setCnpj_cpf(rs.getString("cnpj"));
                    imp.setIe_rg(rs.getString("ie"));

                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));
                    imp.setObservacao(rs.getString("obs"));

                    imp.setTel_principal(rs.getString("fone"));

                    String email = rs.getString("email");
                    if ((email) != null && (!"".equals(email))) {
                        imp.addContato("1", "EMAIL", null, null, TipoContato.NFE, email);
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
        try (Statement stm = ConexaoAccess.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "	cliid as id,\n"
                    + "	clinomcliente as nome,\n"
                    + "	clifantasia as fantasia,\n"
                    + "	clidatanascimento as data_nasc,\n"
                    + "	clicpf_cgc as cpfcnpj,\n"
                    + "	clirg_ie as rg_ie,\n"
                    + "	cliendereco as endereco,\n"
                    + "	clinro as numero,\n"
                    + "	clibairro as bairro,\n"
                    + "	clicomplemento as complemento,\n"
                    + "	clicidade as cidade,\n"
                    + "	cliestado as uf,\n"
                    + "	clicep as cep,\n"
                    + "	clitelefone as telefone,\n"
                    + "	cliemail as email,\n"
                    + "	clifax as fax,\n"
                    + "	clinomeempresa as empresa,\n"
                    + "	clitelefonetrabalho as fone_empresa,\n"
                    + "	clidesde as data_cadastro,\n"
                    + "	cliflagbloqueado as bloqueado,\n"
                    + "	clilimitecompra as limite,\n"
                    + "	clidiavencconta as dia_vencimento,\n"
                    + "	clipai as nomepai,\n"
                    + "	climae as nomemae,\n"
                    + "	cliconjuge as nome_conju,\n"
                    + "	clicpfconjuge as cpf_conju,\n"
                    + "	clinascconjuge as nasc_conju\n"
                    + "FROM\n"
                    + "	tbclientes\n"
                    + "ORDER BY 1")) {
                while (rs.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rs.getString("id"));
                    imp.setRazao(rs.getString("nome"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setDataNascimento(rs.getDate("data_nasc"));
                    imp.setCnpj(rs.getString("cpfcnpj"));
                    imp.setInscricaoestadual(rs.getString("rg_ie"));

                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));

                    imp.setTelefone(rs.getString("telefone"));
                    imp.setEmail(rs.getString("email"));
                    imp.setFax(rs.getString("fax"));

                    imp.setEmpresa(rs.getString("empresa"));
                    imp.setEmpresaTelefone(rs.getString("fone_empresa"));
                    imp.setDataCadastro(rs.getDate("data_cadastro"));
                    imp.setBloqueado(rs.getBoolean("bloqueado"));

                    imp.setValorLimite(rs.getDouble("limite"));
                    imp.setDiaVencimento(rs.getInt("dia_vencimento"));

                    imp.setNomeMae(rs.getString("nomemae"));
                    imp.setNomePai(rs.getString("nomepai"));
                    imp.setNomeConjuge(rs.getString("nome_conju"));
                    imp.setCpfConjuge(rs.getString("cpf_conju"));
                    imp.setDataNascimentoConjuge(rs.getDate("nasc_conju"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoAccess.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "	crpNroLancamento as id,\n"
                    + "	crpDataLancamento as data_emissao,\n"
                    + "	crpNumDocumento as nrocupom,\n"
                    + " crpNroCaixa as ecf,"
                    + "	crpCodCliente as id_cliente,\n"
                    + "	crpVencimentoConta as data_vencimento,\n"
                    + "	crpValorLancamento as valor,\n"
                    + " crpJuros as juros,\n"
                    + "	crpDesLancamento as obs\n"
                    + "FROM \n"
                    + "	tbcontasreceberpagar\n"
                    + "WHERE\n"
                    + "	crpDataPagamento is NULL\n"
                    + "ORDER BY\n"
                    + "	crpNroLancamento")) {
                while (rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rs.getString("id"));
                    imp.setDataEmissao(rs.getDate("data_emissao"));
                    imp.setNumeroCupom(rs.getString("nrocupom"));
                    imp.setEcf(rs.getString("ecf"));
                    imp.setIdCliente(rs.getString("id_cliente"));
                    imp.setDataVencimento(rs.getDate("data_vencimento"));
                    imp.setValor(rs.getDouble("valor"));
                    imp.setJuros(rs.getDouble("juros"));
                    imp.setObservacao(rs.getString("obs"));

                    result.add(imp);
                }
            }
        }
        return result;
    }
}
