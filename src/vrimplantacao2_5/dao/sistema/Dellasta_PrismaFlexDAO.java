package vrimplantacao2_5.dao.sistema;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import static vr.core.utils.StringUtils.LOG;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoEmpresa;
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.OfertaIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;
import vrimplantacao2_5.dao.conexao.ConexaoFirebird;

/**
 *
 * @author Alan
 */
public class Dellasta_PrismaFlexDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "PrismaFlex";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.ASSOCIADO,
                OpcaoProduto.DATA_CADASTRO,
                OpcaoProduto.QTD_EMBALAGEM_COTACAO,
                OpcaoProduto.QTD_EMBALAGEM_EAN,
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.VALIDADE,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                OpcaoProduto.FAMILIA,
                OpcaoProduto.FAMILIA_PRODUTO,
                OpcaoProduto.ATIVO,
                OpcaoProduto.PESO_BRUTO,
                OpcaoProduto.PESO_LIQUIDO,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.TROCA,
                OpcaoProduto.MARGEM,
                OpcaoProduto.VENDA_PDV,
                OpcaoProduto.PDV_VENDA,
                OpcaoProduto.PRECO,
                OpcaoProduto.CUSTO,
                OpcaoProduto.CUSTO_COM_IMPOSTO,
                OpcaoProduto.CUSTO_SEM_IMPOSTO,
                OpcaoProduto.NCM,
                OpcaoProduto.CEST,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.NATUREZA_RECEITA,
                OpcaoProduto.ICMS,
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.ATUALIZAR_SOMAR_ESTOQUE,
                OpcaoProduto.OFERTA,
                OpcaoProduto.DESCONTINUADO,
                OpcaoProduto.VOLUME_QTD,
                OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                OpcaoProduto.FABRICANTE
        ));
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
                OpcaoCliente.ENDERECO,
                OpcaoCliente.CONTATOS,
                OpcaoCliente.DATA_CADASTRO,
                OpcaoCliente.DATA_NASCIMENTO,
                OpcaoCliente.VENCIMENTO_ROTATIVO,
                OpcaoCliente.CLIENTE_EVENTUAL,
                OpcaoCliente.RECEBER_CREDITOROTATIVO));
                //OpcaoCliente.OUTRAS_RECEITAS));
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT DISTINCT\n"
                    + "	i.SITCODIGO id,\n"
                    + "	i.SITCODTRI||'-'||p.PROICMS||'-'||p.PROBASEREDUCAO descricao,\n"
                    + "	i.SITCODTRI cst,\n"
                    + "	p.PROICMS aliquota,\n"
                    + "	p.PROBASEREDUCAO reducao\n"
                    + "FROM\n"
                    + "	SITITRIB i\n"
                    + "	JOIN PRODUTOS p ON i.SITCODIGO = p.SITCODIGO"
            )) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(
                            rs.getString("id"),
                            rs.getString("descricao"),
                            rs.getInt("cst"),
                            rs.getDouble("aliquota"),
                            rs.getDouble("reducao"))
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
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "	LFGCODIGO merc1,\n"
                    + "	LFGDESCRI desc_merc1,\n"
                    + "	LFGCODIGO merc2,\n"
                    + "	LFGDESCRI desc_merc2,\n"
                    + "	LFGCODIGO merc3,\n"
                    + "	LFGDESCRI desc_merc3\n"
                    + "FROM\n"
                    + "	LINFAMGRU\n"
                    + "WHERE\n"
                    + "	LFGTIPOAV = 'G'\n"
                    + "ORDER BY 1"
            )) {
                while (rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());

                    imp.setMerc1ID(rs.getString("merc1"));
                    imp.setMerc1Descricao(rs.getString("desc_merc1"));
                    imp.setMerc2ID(rs.getString("merc2"));
                    imp.setMerc2Descricao(rs.getString("desc_merc2"));
                    imp.setMerc3ID(rs.getString("merc3"));
                    imp.setMerc3Descricao(rs.getString("desc_merc3"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "	LFGCODIGO id_familia,\n"
                    + "	LFGDESCRI familia\n"
                    + "FROM\n"
                    + "	LINFAMGRU\n"
                    + "WHERE\n"
                    + "	LFGTIPOAV = 'F'\n"
                    + "ORDER BY 1"
            )) {
                while (rs.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rs.getString("id_familia"));
                    imp.setDescricao(rs.getString("familia"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "	PROCODIGO id_produto,\n"
                    + "	BARCBARRA ean,\n"
                    + "	BARFATOR qtdembalagem,\n"
                    + "	PROUNIDME tipo_embalagem\n"
                    + "FROM\n"
                    + "	CODBARRA\n"
                    + "ORDER BY 1"
            )) {
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rs.getString("id_produto"));
                    imp.setEan(rs.getString("ean"));
                    imp.setQtdEmbalagem(rs.getInt("qtdembalagem"));
                    imp.setTipoEmbalagem(rs.getString("tipo_embalagem"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + "	p.PROCODIGO id,\n"
                    + "	CASE WHEN barcbarra IS NULL THEN p.PROCODIGO ELSE barcbarra END ean,\n"
                    + "	PRODESCRI desc_completa,\n"
                    + "	PRODESRES desc_reduzida,\n"
                    + "	p.PROUNIDME tipoembalagem,\n"
                    + "	ean.BARFATOR qtdembalagem,\n"
                    + "	FABCODIGO fabricante,\n"
                    + "	CASE WHEN PROPESADO = 'S' THEN 1 ELSE 0 END e_balanca,\n"
                    + "	(e.ESTQTDENT - e.ESTQTDSAI) estoque,\n"
                    + "	COALESCE(PROESTMIN, 0) estoquemin,\n"
                    + "	COALESCE(PROESTMAX, 0) estoquemax,\n"
                    + "	PROINCLUS data_cadastro,\n"
                    + "	PROALTERA data_alteracao,\n"
                    + "	p2.PRECUSTO custosemimposto,\n"
                    + "	p2.PRECUSTOR custocomimposto,\n"
                    + "	p2.PROMARGEM margem,\n"
                    + "	p2.PREPVENDA precovenda,\n"
                    + "	FAMCODIGO familia,\n"
                    + "	GRUCODIGO merc1,\n"
                    + "	GRUCODIGO merc2,\n"
                    + "	SITCODIGO id_icms_saida,\n"
                    + "	PRONCM ncm,\n"
                    + "	PROCEST cest,\n"
                    + "	PROCSTPIS piscofins_debito,\n"
                    + "	PROCSTPISE piscofins_credito\n"
                    + "FROM\n"
                    + "	PRODUTOS p\n"
                    + "	LEFT JOIN CODBARRA ean ON p.PROCODIGO = ean.procodigo\n"
                    + "	JOIN PRECO p2 ON p.PROCODIGO = p2.PROCODIGO\n"
                    + "	JOIN ESTOQUE e ON p.PROCODIGO = e.PROCODIGO\n"
                    + "ORDER BY 1 "
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("id"));
                    imp.setEan(rst.getString("ean"));

                    imp.setDescricaoCompleta(rst.getString("desc_completa"));
                    imp.setDescricaoReduzida(rst.getString("desc_reduzida"));
                    imp.setDescricaoGondola(imp.getDescricaoReduzida());
                    imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                    imp.setFornecedorFabricante(rst.getString("fabricante"));
                    imp.seteBalanca(rst.getBoolean("e_balanca"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setEstoqueMinimo(rst.getDouble("estoquemin"));
                    imp.setEstoqueMaximo(rst.getDouble("estoquemax"));
                    imp.setDataCadastro(rst.getDate("data_cadastro"));
                    imp.setDataAlteracao(rst.getDate("data_alteracao"));

                    imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));

                    imp.setIdFamiliaProduto(rst.getString("familia"));
                    imp.setCodMercadologico1(rst.getString("merc1"));
                    imp.setCodMercadologico2(rst.getString("merc2"));
                    imp.setCodMercadologico3(imp.getCodMercadologico2());

                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));

                    String idIcmsDebito, IdIcmsCredito, IdIcmsForaEstado;

                    idIcmsDebito = rst.getString("id_icms_saida");
                    //IdIcmsCredito = rst.getString("id_credito");

                    imp.setIcmsDebitoId(idIcmsDebito);
                    //imp.setIcmsDebitoForaEstadoId(IdIcmsForaEstado);
                    //imp.setIcmsDebitoForaEstadoNfId(IdIcmsForaEstado);
                    imp.setIcmsConsumidorId(idIcmsDebito);
                    imp.setIcmsCreditoId(idIcmsDebito);
                    //imp.setIcmsCreditoForaEstadoId(IdIcmsCredito);

                    imp.setPiscofinsCstCredito(rst.getString("piscofins_credito"));
                    imp.setPiscofinsCstDebito(rst.getString("piscofins_debito"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<OfertaIMP> getOfertas(Date dataTermino) throws Exception {
        List<OfertaIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "	o.CODIPRO id_produto,\n"
                    + "	DTINPRO data_ini,\n"
                    + "	DTFIPRO data_fim,\n"
                    + "	PRECO_PRO precooferta,\n"
                    + "	pr.PREVE preconormal\n"
                    + "FROM\n"
                    + "	PROMOCOES o\n"
                    + "	LEFT JOIN PRODUTOS p ON p.CODIPRO = o.CODIPRO \n"
                    + "	LEFT JOIN PRECOS_LOJAS pr ON pr.CODIPRO = p.CODIPRO AND pr.AGP_CODIGO = o.EMP_CODIGO \n"
                    + "WHERE\n"
                    + "	o.EMP_CODIGO = " + getLojaOrigem() + "\n"
                    + "	AND DTFIPRO >= 'now'"
            )) {
                while (rs.next()) {
                    OfertaIMP imp = new OfertaIMP();

                    imp.setIdProduto(rs.getString("id_produto"));
                    imp.setDataInicio(rs.getDate("data_ini"));
                    imp.setDataFim(rs.getDate("data_fim"));
                    imp.setPrecoOferta(rs.getDouble("precooferta"));
                    imp.setPrecoNormal(rs.getDouble("preconormal"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    /*@Override
    public List<AssociadoIMP> getAssociados(Set<OpcaoAssociado> opt) throws Exception {
        List<AssociadoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "	pa.CODIPRO produto_pai,\n"
                    + "	p.DESCRICAO descricao_pai,\n"
                    + "	pa.QTD_RELA qtdembalagem,\n"
                    + "	pa.COD_RELA produto_filho,\n"
                    + "	p2.DESCRICAO descricao_filho\n"
                    + "FROM\n"
                    + "	PRO_RELA pa\n"
                    + "	JOIN PRODUTOS p ON p.CODIPRO = pa.CODIPRO\n"
                    + "	JOIN PRODUTOS p2 ON pa.COD_RELA = p2.CODIPRO \n"
                    + "ORDER BY\n"
                    + "	produto_pai, produto_filho"
            )) {
                while (rs.next()) {
                    AssociadoIMP imp = new AssociadoIMP();

                    imp.setId(rs.getString("produto_pai"));
                    imp.setDescricao(rs.getString("descricao_pai"));
                    imp.setQtdEmbalagem(rs.getInt("qtdembalagem"));
                    imp.setProdutoAssociadoId(rs.getString("produto_filho"));
                    imp.setDescricaoProdutoAssociado(rs.getString("descricao_filho"));

                    result.add(imp);
                }
            }
        }

        return result;
    }*/
    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "	FORCODIGO id,\n"
                    + "	FORRAZAOS razao,\n"
                    + "	FORFANTAS fantasia,\n"
                    + "	FORDOCUME cnpj,\n"
                    + "	FORINSEST ie,\n"
                    + "	FORENDERE endereco,\n"
                    + "	FORNUMERO numero,\n"
                    + "	FORCOMPLE complemento,\n"
                    + "	FORBAIRRO bairro,\n"
                    + "	FORCIDADE cidade,\n"
                    + "	FORESTADO uf,\n"
                    + "	FORCEP cep,\n"
                    + "	FORDDDFO1||' '||FORTELFO1 telefone1,\n"
                    + "	FORDDDFO2||' '||FORTELFO2 telefone2,\n"
                    + "	FOREMAIL email,\n"
                    + "	CAST(FORINCLUS AS date) data_cadastro,\n"
                    + "	FOROBSERV observacao,\n"
                    + "	FORSIMPLE simples\n"
                    + "FROM\n"
                    + "	FORNECEDOR f\n"
                    + "ORDER BY 1"
            )) {
                while (rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

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
                    imp.setObservacao(rs.getString("observacao"));

                    String email = Utils.acertarTexto(rs.getString("email")).toLowerCase();
                    if (!"".equals(email)) {
                        imp.addContato("1", "Email", "", "", TipoContato.COMERCIAL,
                                (email.length() > 50 ? email.substring(0, 50) : email));
                    }

                    imp.setTel_principal(Utils.acertarTexto(rs.getString("telefone1")));

                    if ((rs.getString("telefone2") != null)
                            && (!rs.getString("telefone2").trim().isEmpty())) {
                        imp.addContato("2", "Telefone 2", rs.getString("telefone2"), null, TipoContato.COMERCIAL, null);
                    }

                    if ("S".equals(rs.getString("simples"))) {
                        imp.setTipoEmpresa(TipoEmpresa.EPP_SIMPLES);
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

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "	FORCODIGO id_fornecedor,\n"
                    + "	PROCODIGO id_produto,\n"
                    + "	FPRCODPRO cod_externo\n"
                    + "FROM\n"
                    + "	FORNECPRODUTO\n"
                    + "WHERE FORCODIGO != 0\n"
                    + "ORDER BY 1"
            )) {
                while (rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setIdProduto(rs.getString("id_produto"));
                    imp.setIdFornecedor(rs.getString("id_fornecedor"));
                    imp.setCodigoExterno(rs.getString("cod_externo"));
                    imp.setQtdEmbalagem(1);

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ContaPagarIMP> getContasPagar() throws Exception {
        List<ContaPagarIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "	CPGNUMERONF||CPGDUPLIC id,\n"
                    + "	CLICODIGO id_fornecedor,\n"
                    + "	f.FORDOCUME cnpj_cpf,\n"
                    + " CPGNUMERONF documento,\n"
                    + "	CAST(CPGDATAMV AS date) emissao,\n"
                    + "	CPGVALLIQ valor,\n"
                    + "	COALESCE (cast(CPGVENCIM as date), '1900-01-01') vencimento,\n"
                    + "	CPGOBSERV observacao\n"
                    + "FROM\n"
                    + "	CONTASPG cp\n"
                    + "	JOIN FORNECEDOR f ON f.FORCODIGO = cp.CLICODIGO\n"
                    + "WHERE\n"
                    + "	EMPCODIGO = " + getLojaOrigem() + "\n"
                    + "	AND CPGBAIXAD = 'N'\n"
                    + "	AND CLICODIGO NOT IN (1)\n"
                    + "ORDER BY CPGDATAMV"
            )) {
                while (rs.next()) {
                    ContaPagarIMP imp = new ContaPagarIMP();
                    imp.setId(Utils.acertarTexto((rs.getString("id"))));
                    imp.setIdFornecedor(rs.getString("id_fornecedor"));
                    imp.setCnpj(rs.getString("cnpj_cpf"));
                    imp.setNumeroDocumento(rs.getString("documento"));

                    SimpleDateFormat formatar = new SimpleDateFormat("yyyy-MM-dd");
                    Date data = formatar.parse(rs.getString("emissao"));
                    imp.setDataEmissao(data);

                    imp.setDataEntrada(imp.getDataEmissao());
                    imp.setValor(rs.getDouble("valor"));
                    imp.setVencimento(rs.getDate("vencimento"));
                    imp.setObservacao(rs.getString("observacao"));

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
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "	CLICODIGO id,\n"
                    + "	CLIRAZAOS razao,\n"
                    + "	CLIFANTAS fantasia,\n"
                    + "	CLIDOCUME cnpj_cpf,\n"
                    + "	CLIIDENTI rg_ie,\n"
                    + "	CLIENDERE endereco,\n"
                    + "	CLINUMERO numero,\n"
                    + "	CLICOMPLE complemento,\n"
                    + "	CLIBAIRRO bairro,\n"
                    + "	CLICIDADE cidade,\n"
                    + "	CLIESTADO uf,\n"
                    + "	CLICEP cep,\n"
                    + "	CLISEXO sexo,\n"
                    + "	CLINASCIM data_nasc,\n"
                    + "	CAST(CLIINCLUS as date) data_cadastro,\n"
                    + "	CASE WHEN CLIATIVO = 'S' THEN 1 ELSE 0 END ativo,\n"
                    + "	CASE WHEN CLIBLOQUE = 'N' THEN 0 ELSE 1 END bloqueado,\n"
                    + "	CLIDDDFO1||' '||CLITELFO1 telefone,\n"
                    + "	CLIDDDFO2||' '||CLITELFO2 telefone2,\n"
                    + "	CLIDDDCEL||' '||CLITELCEL celular,\n"
                    + "	CLIEMAIL email,\n"
                    + "	CLILIMITEP limite,\n"
                    + "	CLINOMEMAE nomemae,\n"
                    + "	CLINOMEPAI nomepai,\n"
                    + "	CLIOBSERV observacao\n"
                    + "FROM\n"
                    + "	CLIENTES\n"
                    + "ORDER BY 1"
            )) {
                while (rs.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rs.getString("id"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setCnpj(rs.getString("cnpj_cpf"));
                    imp.setInscricaoestadual(rs.getString("rg_ie"));

                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));

                    if ("F".equals(rs.getString("sexo"))) {
                        imp.setSexo(TipoSexo.FEMININO);
                    } else {
                        imp.setSexo(TipoSexo.MASCULINO);
                    }

                    imp.setDataNascimento(rs.getDate("data_nasc"));
                    imp.setDataCadastro(rs.getDate("data_cadastro"));
                    imp.setAtivo(rs.getBoolean("ativo"));
                    imp.setBloqueado(rs.getBoolean("bloqueado"));
                    imp.setValorLimite(rs.getDouble("limite"));

                    imp.setTelefone(rs.getString("telefone"));
                    imp.setCelular(rs.getString("celular"));
                    imp.setEmail(rs.getString("email"));
                    imp.setNomeMae(rs.getString("nomemae"));
                    imp.setNomePai(rs.getString("nomepai"));

                    imp.setObservacao(rs.getString("observacao"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "	REPLACE(EMPCODIGO||CRCCODIGO||CRCDUPLIC||CRCDATAMV||CRCPARCTOT,'-','') id,\n"
                    + "	CRCNUMNF numerocupom,\n"
                    + "	cr.CLICODIGO codcli,\n"
                    + "	c.CLIDOCUME cpf_cnpj,\n"
                    + "	CRCECFREC ecf,\n"
                    + "	CRCVALORD valor,\n"
                    + "	CRCDATAMV emissao,\n"
                    + "	COALESCE (CRCVENCIM,'1900-01-01') vencimento\n"
                    + "FROM\n"
                    + "	CONTASRC cr\n"
                    + "	LEFT JOIN CLIENTES c ON c.CLICODIGO = cr.CLICODIGO \n"
                    + "WHERE\n"
                    + "	EMPCODIGO = " + getLojaOrigem() + "\n"
                    + "	AND CRCDATAREC IS NULL\n"
                    + "ORDER BY CRCDATAMV"
            )) {
                while (rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();

                    imp.setId(rs.getString("id"));
                    imp.setNumeroCupom(Utils.formataNumero(rs.getString("numerocupom")));
                    imp.setIdCliente(rs.getString("codcli"));
                    imp.setCnpjCliente(rs.getString("cpf_cnpj"));
                    imp.setEcf(rs.getString("ecf"));
                    imp.setValor(rs.getDouble("valor"));
                    imp.setDataEmissao(rs.getDate("emissao"));
                    imp.setDataVencimento(rs.getDate("vencimento"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    private Date dataInicioVenda;
    private Date dataTerminoVenda;

    @Override
    public Iterator<VendaIMP> getVendaIterator() throws Exception {
        return new Dellasta_PrismaFlexDAO.VendaIterator(getLojaOrigem(), this.dataInicioVenda, this.dataTerminoVenda);
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        return new Dellasta_PrismaFlexDAO.VendaItemIterator(getLojaOrigem(), this.dataInicioVenda, this.dataTerminoVenda);
    }

    public void setDataInicioVenda(Date date) {
        this.dataInicioVenda = dataInicioVenda;
    }

    public void setDataTerminoVenda(Date date) {
        this.dataTerminoVenda = dataTerminoVenda;
    }

    private static class VendaIterator implements Iterator<VendaIMP> {

        public final static SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");

        private Statement stm = ConexaoFirebird.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaIMP next;
        private Set<String> uk = new HashSet<>();

        private void obterNext() {
            try {
                SimpleDateFormat timestampDate = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat timestamp = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                if (next == null) {
                    if (rst.next()) {
                        next = new VendaIMP();
                        String id = rst.getString("id_venda");
                        if (!uk.add(id)) {
                            LOG.warning("Venda " + id + " já existe na listagem");
                        }
                        next.setId(id);
                        next.setNumeroCupom(Utils.stringToInt(rst.getString("numerocupom")));
                        next.setEcf(Utils.stringToInt(rst.getString("ecf")));
                        next.setData(rst.getDate("data"));

                        String horaInicio = timestampDate.format(rst.getDate("data")) + " " + rst.getString("hora");
                        String horaTermino = timestampDate.format(rst.getDate("data")) + " " + rst.getString("hora");
                        next.setHoraInicio(timestamp.parse(horaInicio));
                        next.setHoraTermino(timestamp.parse(horaTermino));
                        next.setValorDesconto(rst.getDouble("desconto"));
                        next.setValorAcrescimo(rst.getDouble("acrescimo"));
                        next.setSubTotalImpressora(rst.getDouble("total"));
                    }
                }
            } catch (SQLException | ParseException ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {

            String strDataInicio = new SimpleDateFormat("yyyy-MM-dd").format(dataInicio);
            String strDataTermino = new SimpleDateFormat("yyyy-MM-dd").format(dataTermino);
            this.sql
                    = "SELECT\n"
                    + "	EMPCODIGO || VENNUMECF || VENNUMPDV || VENNCUPOM || VENNUITEM || VENREFCX || VENMODELO AS id_venda,\n"
                    + "	VENNUMPDV pdv,\n"
                    + "	VENNUMECF ecf,\n"
                    + "	VENNCUPOM numerocupom,\n"
                    + "	VENDATA DATA,\n"
                    + "	VENHORA hora\n"
                    + "FROM\n"
                    + "	ITENS01032022\n"
                    + "WHERE\n"
                    + "	a.EMPCODIGO = 1\n"
                    + "	AND VENDATA BETWEEN '2022-03-01' AND '2022-03-01'";
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

        private Statement stm = ConexaoFirebird.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaItemIMP next;

        private void obterNext() {
            try {
                if (next == null) {
                    if (rst.next()) {
                        next = new VendaItemIMP();

                        next.setVenda(rst.getString("id_venda"));
                        next.setId(rst.getString("id_item"));
                        next.setSequencia(rst.getInt("nroitem"));
                        next.setProduto(rst.getString("produto"));
                        next.setUnidadeMedida(rst.getString("unidade"));
                        next.setCodigoBarras(rst.getString("codigobarras"));
                        next.setDescricaoReduzida(rst.getString("descricao"));
                        next.setQuantidade(rst.getDouble("quantidade"));
                        next.setPrecoVenda(rst.getDouble("precovenda"));
                        next.setTotalBruto(rst.getDouble("total"));

                    }
                }
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaItemIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {
            this.sql
                    = "SELECT\n"
                    + "	REPLACE((pdv.MOV_LOJA || pdv.MOV_COO || pdv.MOV_PDV || pdv.MOV_DT_MOVIMENTO), '-', '') AS id_venda,\n"
                    + "	REPLACE((pdv.MOV_LOJA || pdv.MOV_COO || pdv.MOV_PDV || pdv.MOV_DT_MOVIMENTO || pdv.PRO_ID || pdv.MOV_SEQ_COO), '-', '') AS id_item,\n"
                    + "	SUBSTRING(pdv.PRO_COD_BARRA FROM 1 FOR CHAR_LENGTH(pdv.PRO_COD_BARRA)-1) ean,\n"
                    + "	p.PRO_DESCRICAO produto,\n"
                    + "	pdv.MOV_LOJA AS loja,\n"
                    + "	pdv.MOV_PDV AS pdv,\n"
                    + "	pdv.MOV_ECF AS ecf,\n"
                    + "	pdv.MOV_COO AS numerocupom,\n"
                    + "	pdv.MOV_SEQ_COO AS sequencia,\n"
                    + "	pdv.MOV_DT_MOVIMENTO AS DATA,\n"
                    + " '00:00:00' hora,\n"
                    + "	CASE\n"
                    + "	 WHEN pdv.MOV_TPO_REGISTRO = 10 THEN 1\n"
                    + "	 ELSE 0\n"
                    + "	END cancelado,\n"
                    + "	p.PRO_UN_REFERENCIA AS unidade,\n"
                    + "	pdv.MOV_QTD_ITEM AS qtd,\n"
                    + "	pdv.MOV_VLR_UNIT AS valorunitario,\n"
                    + "	pdv.MOV_VLR_TOTAL AS valortotal,\n"
                    + "	pdv.MOV_DESCONTO_ITEM AS desconto\n"
                    + "FROM\n"
                    + "	TB_PDV_MOVOUTRA pdv\n"
                    + "JOIN TB_PRODUTOS p ON p.pro_id = pdv.PRO_ID\n"
                    + "WHERE\n"
                    + "	pdv.PRO_ID IS NOT NULL\n"
                    + "	AND pdv.MOV_LOJA = " + idLojaCliente + "\n"
                    + "	AND pdv.MOV_DT_MOVIMENTO BETWEEN '" + dataInicio + "' AND '" + dataTermino + "'";
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
