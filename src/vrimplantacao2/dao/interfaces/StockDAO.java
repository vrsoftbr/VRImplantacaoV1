package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao.classe.ConexaoAccess;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.cadastro.oferta.SituacaoOferta;
import vrimplantacao2.vo.cadastro.oferta.TipoOfertaVO;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoEmpresa;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.OfertaIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/*
@author Alan
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
                    "select trivalortributacao from tbtributacoes"
            )) {
                while (rst.next()) {
                    int cst = 0;
                    double aliquota = rst.getDouble("aliquota");
                    double reducao = 0;

                    result.add(new MapaTributoIMP(
                            String.format("%d-%.2f-%.2f", cst, aliquota, reducao),
                            String.format("%d-%.2f-%.2f", cst, aliquota, reducao),
                            cst,
                            aliquota,
                            reducao
                    ));
                }
            }

            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    p.tricstcsosnentrada as cst_credito,\n"
                    + "    p.trivalortributacao as aliquota_credito,\n"
                    + "    0 as reducao_credito\n"
                    + "from\n"
                    + "	tbtributacoes p"
            )) {
                while (rst.next()) {
                    int cst = rst.getInt("cst_credito");
                    double aliquota = rst.getDouble("aliquota_credito");
                    double reducao = rst.getDouble("reducao_credito");

                    result.add(new MapaTributoIMP(
                            String.format("%d-%.2f-%.2f", cst, aliquota, reducao),
                            String.format("%d-%.2f-%.2f", cst, aliquota, reducao),
                            cst,
                            aliquota,
                            reducao
                    ));
                }
            }

            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    p.tricstcsosnentrada as cst_debito,\n"
                    + "    p.tricstcsosnsaida as aliquota_debito,\n"
                    + "    0 as reducao_debito\n"
                    + "from\n"
                    + "	tbtributacoes p"
            )) {
                while (rst.next()) {
                    int cst = rst.getInt("cst_debito");
                    double aliquota = rst.getDouble("aliquota_debito");
                    double reducao = rst.getDouble("reducao_debito");

                    result.add(new MapaTributoIMP(
                            String.format("%d-%.2f-%.2f", cst, aliquota, reducao),
                            String.format("%d-%.2f-%.2f", cst, aliquota, reducao),
                            cst,
                            aliquota,
                            reducao
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
                    + "ORDER 1,3,5"
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
                    + "	proid as id,\n"
                    + "	procodbarras as ean,\n"
                    + "	pronomproduto as descricao,\n"
                    + "	proabrproduto as reduzida,\n"
                    + "	prodesunidade as unidade,\n"
                    + "	provalprecovenda as precovenda,\n"
                    + "	provalcusto as precocusto,\n"
                    + "	prolucro as margem,\n"
                    + "	proqntminima as estoque_min,\n"
                    + "	proqntestoque as estoque,\n"
                    + "	proCodDepartamento as merc1,\n"
                    + "	proCodDepartamento as merc2,\n"
                    + "	proCodDepartamento as merc3,\n"
                    + "	propeso as pesobruto,\n"
                    + "	prodataalterado as data_alteracao,\n"
                    + "	proncm as ncm,\n"
                    + " procest as cest,\n"
                    + "	proflagbalanca as e_balanca,\n"
                    + "	trivalortributacao as aliquota_saida,\n"
                    + "	tricstcsosnsaida as cst_saida,\n"
                    + "	0 as red_saida,\n"
                    + "	trivalortributacao as aliquota_entrada,\n"
                    + "	tricstcsosnentrada as cst_entrada,\n"
                    + " 0 as red_entrada\n"
                    + "FROM\n"
                    + "	  tbprodutos p,\n"
                    + "   tbGrupos m,\n"
                    + "   tbtributacoes t\n"
                    + "WHERE\n"
                    + "   p.procodtributo = t.triid\n"
                    + "   and m.depid = p.proCodDepartamento \n"
                    + "ORDER BY 1"
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
                    imp.setEstoqueMinimo(rst.getDouble("estoque_min"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    
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

                    imp.setValidade(rst.getInt("validade"));

                    imp.setPiscofinsCstCredito(rst.getString("pis_e"));
                    imp.setPiscofinsCstDebito(rst.getString("pis_s"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("natreceita"));

                    int cst = rst.getInt("cst_credito");
                    double aliquota = rst.getDouble("aliquota_credito");
                    double reducao = rst.getDouble("reducao_credito");
                    imp.setIcmsCreditoId(formataIdTributacao(cst, aliquota, reducao));
                    imp.setIcmsCreditoForaEstadoId(formataIdTributacao(cst, aliquota, reducao));

                    cst = rst.getInt("cst_debito");
                    aliquota = rst.getDouble("aliquota_debito");
                    reducao = rst.getDouble("reducao_debito");
                    imp.setIcmsDebitoId(formataIdTributacao(cst, aliquota, reducao));
                    imp.setIcmsConsumidorId(formataIdTributacao(cst, aliquota, reducao));

                    cst = rst.getInt("cst_debito_fe");
                    aliquota = rst.getDouble("aliquota_debito_fe");
                    reducao = rst.getDouble("reducao_debito_fe");
                    imp.setIcmsDebitoForaEstadoId(formataIdTributacao(cst, aliquota, reducao));
                    imp.setIcmsDebitoForaEstadoNfId(formataIdTributacao(cst, aliquota, reducao));

                    imp.setPautaFiscalId(imp.getImportId());
                    imp.setBeneficio(rst.getString("beneficio"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    public void alterarCodAnt_produto() throws Exception {

        Statement stm2 = null;
        stm2 = Conexao.createStatement();

        try (Statement stm = ConexaoAccess.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "p.codigo,"
                    + "    p.situacao_tributaria_icm_saida_ne as cst_debito,\n"
                    + "    p.aliquota_icm_saida_ne as aliquota_debito,\n"
                    + "    p.base_icm_saida_ne as reducao_debito\n"
                    + "from tabela_pro p"
            )) {
                while (rst.next()) {

                    String sql = "update implantacao.codant_produto "
                            + "set "
                            + "icmscst = " + rst.getInt("cst_debito") + ", "
                            + "icmsaliq = " + rst.getDouble("aliquota_debito") + ", "
                            + "icmsreducao = " + rst.getDouble("reducao_debito") + " "
                            + "where impid = '" + rst.getString("codigo") + "';";

                    stm2.execute(sql);

                    ProgressBar.next();

                }
            }
        }
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

    /*@Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoAccess.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "  r.Duplicata as id,\n"
                    + "  r.Dia as emissao,\n"
                    + "  r.Num_NF as numerocupom,\n"
                    + "  r.Num_maquina as ecf,\n"
                    + "  r.Valor_total as valor,\n"
                    + "  r.Duplicata,\n"
                    + "  r.Unidade_origem,\n"
                    + "  r.Observacoes,\n"
                    + "  r.Cliente as id_cliente,\n"
                    + "  r.Vencimento,\n"
                    + "  r.Data_pgto,\n"
                    + "  r.Valor_pago,\n"
                    + "  r.valor_adicional as juros\n"
                    + "from\n"
                    + "  Tabela_Caderno r\n"
                    + "where\n"
                    + "  Status = 0\n"
                    + "order by\n"
                    + "  r.Vencimento")) {
                while (rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rs.getString("id"));
                    imp.setDataEmissao(rs.getDate("emissao"));
                    imp.setNumeroCupom(rs.getString("numerocupom"));
                    imp.setEcf(rs.getString("ecf"));
                    imp.setValor(rs.getDouble("valor"));
                    imp.setJuros(rs.getDouble("juros"));
                    imp.setObservacao(
                            String.format(
                                    "LOJA ORIG %s DUPLIC %s OBS %s",
                                    rs.getString("Duplicata"),
                                    rs.getString("Unidade_origem"),
                                    rs.getString("Observacoes")
                            )
                    );
                    imp.setIdCliente(rs.getString("id_cliente"));
                    imp.setDataVencimento(rs.getDate("Vencimento"));
                    double valorPago = rs.getDouble("Valor_pago");
                    if (valorPago > 0) {
                        imp.addPagamento(imp.getId(), valorPago,
                                0,
                                0,
                                rs.getDate("Data_pgto"),
                                ""
                        );
                    }

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<OfertaIMP> getOfertas(Date dataTermino) throws Exception {
        List<OfertaIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoAccess.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "  codigo, \n"
                    + "  inicio_promocao, \n"
                    + "  final_promocao,\n"
                    + "  preco_venda, \n"
                    + "  preco_promocao \n"
                    + " from tabela_pro \n"
                    + "where final_promocao > Date()\n"
                    + "order by final_promocao"
            )) {
                while (rs.next()) {
                    OfertaIMP imp = new OfertaIMP();

                    imp.setIdProduto(rs.getString("codigo"));
                    imp.setDataInicio(rs.getDate("inicio_promocao"));
                    imp.setDataFim(rs.getDate("final_promocao"));
                    imp.setPrecoOferta(rs.getDouble("preco_promocao"));
                    imp.setPrecoNormal(rs.getDouble("preco_venda"));
                    imp.setSituacaoOferta(SituacaoOferta.ATIVO);
                    imp.setTipoOferta(TipoOfertaVO.CAPA);

                    result.add(imp);
                }
            }
        }
        return result;
    }*/
    private Date dataInicioVenda;
    private Date dataTerminoVenda;

    public void setDataInicioVenda(Date dataInicioVenda) {
        this.dataInicioVenda = dataInicioVenda;
    }

    public void setDataTerminoVenda(Date dataTerminoVenda) {
        this.dataTerminoVenda = dataTerminoVenda;
    }

    @Override
    public Iterator<VendaIMP> getVendaIterator() throws Exception {
        return new VendaIterator(getLojaOrigem(), dataInicioVenda, dataTerminoVenda);
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        return new VendaItemIterator(getLojaOrigem(), dataInicioVenda, dataTerminoVenda);
    }

    private String formataIdTributacao(int cst, double aliquota, double reducao) {
        return String.format("%d-%.2f-%.2f", cst, aliquota, reducao);
    }

    private static class VendaIterator implements Iterator<VendaIMP> {

        public final static SimpleDateFormat FORMAT = new SimpleDateFormat("dd/MM/yyyy");

        private Statement stm = ConexaoAccess.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaIMP next;
        private Set<String> uk = new HashSet<>();

        private void obterNext() {
            try {
                SimpleDateFormat timestampDate = new SimpleDateFormat("dd/MM/yyyy");
                SimpleDateFormat timestamp = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                if (next == null) {
                    if (rst.next()) {
                        next = new VendaIMP();
                        String id = rst.getString("id");
                        if (!uk.add(id)) {
                            LOG.warning("Venda " + id + " já existe na listagem");
                        }
                        next.setId(id);
                        next.setNumeroCupom(Utils.stringToInt(rst.getString("numerocupom")));
                        next.setEcf(rst.getInt("ecf"));
                        next.setData(rst.getDate("data"));
                        String cliente = rst.getString("cliente");
                        next.setIdClientePreferencial(cliente);
                        next.setHoraInicio(timestamp.parse(rst.getString("horainicio")));
                        next.setHoraTermino(timestamp.parse(rst.getString("horatermino")));
                        //next.setCancelado(rst.getBoolean("cancelado"));
                        next.setSubTotalImpressora(rst.getDouble("subtotalimpressora"));
                        if (rst.getString("cnpj") == null) {
                            next.setCpf(rst.getString("cpf"));
                        } else {
                            next.setCpf(rst.getString("cnpj"));
                        }
                        //next.setValorDesconto(rst.getDouble("desconto"));
                        next.setNumeroSerie(rst.getString("numeroserie"));
                        next.setModeloImpressora(rst.getString("modelo"));
                        next.setNomeCliente(rst.getString("nome"));
                        String endereco
                                = Utils.acertarTexto(rst.getString("endereco")) + ","
                                + Utils.acertarTexto(rst.getString("numero")) + ","
                                + Utils.acertarTexto(rst.getString("complemento")) + ","
                                + Utils.acertarTexto(rst.getString("bairro")) + ","
                                + Utils.acertarTexto(rst.getString("cidade")) + "-"
                                + Utils.acertarTexto(rst.getString("estado")) + ","
                                + Utils.acertarTexto(rst.getString("cep"));
                        next.setEnderecoCliente(endereco);
                        next.setChaveNfCe(rst.getString("chavenfce"));
                    }
                }
            } catch (SQLException | ParseException ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {
            this.sql
                    = "select\n"
                    + "    n.link as id,\n"
                    + "    n.dia as data,\n"
                    + "    n.dia as horainicio,\n"
                    + "    n.dia as horatermino,\n"
                    + "    n.cliente,\n"
                    + "    c.nome,\n"
                    + "    c.cnpj,\n"
                    + "    c.cic as cpf,\n"
                    + "    c.endereco_logradouro as endereco,\n"
                    + "    c.endereco_numero as numero,\n"
                    + "    c.endereco_complemento as complemento,\n"
                    + "    c.bairro,\n"
                    + "    c.cidade,\n"
                    + "    c.uf as estado,\n"
                    + "    c.cep,\n"
                    + "    n.numero as numerocupom,\n"
                    + "    n.desconto as desconto,\n"
                    + "    n.valor as subtotalimpressora,\n"
                    + "    n.num_maquina as ecf,\n"
                    + "    ecf.marcaimpressora,\n"
                    + "    ecf.modeloimpressora as modelo,\n"
                    + "    ecf.numserie as numeroserie,\n"
                    + "    n.situacao as cancelado,\n"
                    + "    chavenfe as chavenfce\n"
                    + "from\n"
                    + "    tabela_nota1 n,\n"
                    + "    tabela_cli c,\n"
                    + "    tabela_sped_ecf ecf\n"
                    + "where\n"
                    + "    n.cliente = c.codigo and\n"
                    + "    n.num_maquina = ecf.codigo and\n"
                    + "    n.data_inclusao between #" + FORMAT.format(dataInicio) + "# and #" + FORMAT.format(dataTermino) + "# and\n"
                    + "    n.cupom = True and\n"
                    + "    n.tipo = 1\n"
                    + "order by\n"
                    + "    dia";
            LOG.log(Level.FINE, "SQL da venda: " + sql);
            rst = stm.executeQuery(sql);
        }

        @Override
        public boolean hasNext() {
            obterNext();
            return next != null;
        }

        @Override
        public VendaIMP next() {
            obterNext();
            VendaIMP result = next;
            next = null;
            return result;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported.");
        }
    }

    private static class VendaItemIterator implements Iterator<VendaItemIMP> {

        private Statement stm = ConexaoAccess.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaItemIMP next;

        private void obterNext() {
            try {
                if (next == null) {
                    if (rst.next()) {
                        next = new VendaItemIMP();
                        String data = rst.getString("dia"),
                                numero = rst.getString("numero"),
                                ecf = rst.getString("ecf"),
                                sequencia = rst.getString("sequencia");
                        String id = data + "-" + numero + "-" + ecf + "-" + sequencia;

                        next.setId(id);
                        next.setVenda(rst.getString("link"));
                        next.setProduto(rst.getString("cod_produto"));
                        next.setDescricaoReduzida(rst.getString("descricaocompleta"));
                        next.setQuantidade(rst.getDouble("quantidade"));
                        next.setTotalBruto(rst.getDouble("valor"));
                        next.setValorDesconto(rst.getDouble("desconto"));
                        String cancelado = rst.getString("situacao");
                        next.setCancelado("1".equals(cancelado));
                        next.setCodigoBarras(rst.getString("codigo"));
                        next.setUnidadeMedida(rst.getString("unidade"));
                        next.setIcmsAliq(rst.getDouble("icms_aliq"));
                        next.setIcmsCst(rst.getInt("icms_cst"));
                    }
                }
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaItemIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {
            this.sql
                    = "select\n"
                    + "   vi.link,\n"
                    + "   vi.dia,\n"
                    + "   vi.numero,\n"
                    + "   v.num_maquina as ecf,\n"
                    + "   vi.item as sequencia,\n"
                    + "   vi.codigo as cod_produto,\n"
                    + "   vi.codigo,\n"
                    + "   p.descricao as descricaocompleta,\n"
                    + "   p.unidade,\n"
                    + "   vi.quantidade,\n"
                    + "   vi.valor,\n"
                    + "   vi.desconto,\n"
                    + "   vi.situacao,\n"
                    + "   vi.situacao_tributaria_icm as icms_cst,\n"
                    + "   vi.aliquota_icm as icms_aliq\n"
                    + "from \n"
                    + "   tabela_nota2 vi,\n"
                    + "   tabela_nota1 v,\n"
                    + "   tabela_pro p\n"
                    + "where\n"
                    + "   vi.link = v.link and\n"
                    + "   vi.codigo = p.codigo and\n"
                    + "   v.data_inclusao between #" + VendaIterator.FORMAT.format(dataInicio) + "# and #" + VendaIterator.FORMAT.format(dataTermino) + "# and\n"
                    + "   v.tipo = 1\n"
                    + "order by\n"
                    + "   2, 3, 5";
            LOG.log(Level.FINE, "SQL da venda: " + sql);
            rst = stm.executeQuery(sql);
        }

        @Override
        public boolean hasNext() {
            obterNext();
            return next != null;
        }

        @Override
        public VendaItemIMP next() {
            obterNext();
            VendaItemIMP result = next;
            next = null;
            return result;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported.");
        }
    }
}
