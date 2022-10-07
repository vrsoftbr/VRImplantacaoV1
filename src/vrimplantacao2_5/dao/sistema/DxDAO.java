package vrimplantacao2_5.dao.sistema;

import java.util.Map;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2_5.dao.conexao.ConexaoFirebird;
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
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoInscricao;
import vrimplantacao2.vo.enums.TipoPagamento;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;

/*
 *
 * @author Wagner
 *
 */
public class DxDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "DX";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.DATA_CADASTRO,
                OpcaoProduto.QTD_EMBALAGEM_EAN,
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.ATIVO,
                OpcaoProduto.PESO_BRUTO,
                OpcaoProduto.PESO_LIQUIDO,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.MARGEM,
                OpcaoProduto.VENDA_CONTROLADA,
                OpcaoProduto.PDV_VENDA,
                OpcaoProduto.PRECO,
                OpcaoProduto.CUSTO,
                OpcaoProduto.CUSTO_COM_IMPOSTO,
                OpcaoProduto.CUSTO_SEM_IMPOSTO,
                OpcaoProduto.NCM,
                OpcaoProduto.CEST,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.ICMS,
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                OpcaoProduto.ATUALIZAR_SOMAR_ESTOQUE,
                OpcaoProduto.OFERTA,
                OpcaoProduto.VOLUME_QTD,
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.DESMEMBRAMENTO
        ));
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
                OpcaoFornecedor.SITUACAO_CADASTRO,
                OpcaoFornecedor.ENDERECO,
                OpcaoFornecedor.NUMERO,
                OpcaoFornecedor.COMPLEMENTO,
                OpcaoFornecedor.BAIRRO,
                OpcaoFornecedor.MUNICIPIO,
                OpcaoFornecedor.UF,
                OpcaoFornecedor.CEP,
                OpcaoFornecedor.DATA_CADASTRO,
                OpcaoFornecedor.PAGAR_FORNECEDOR,
                OpcaoFornecedor.PRODUTO_FORNECEDOR,
                OpcaoFornecedor.OBSERVACAO));
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
                OpcaoCliente.ENDERECO,
                OpcaoCliente.NUMERO,
                OpcaoCliente.COMPLEMENTO,
                OpcaoCliente.BAIRRO,
                OpcaoCliente.MUNICIPIO,
                OpcaoCliente.UF,
                OpcaoCliente.CEP,
                OpcaoCliente.ESTADO_CIVIL,
                OpcaoCliente.CONTATOS,
                OpcaoCliente.DATA_CADASTRO,
                OpcaoCliente.DATA_NASCIMENTO,
                OpcaoCliente.EMPRESA,
                OpcaoCliente.ENDERECO_EMPRESA,
                OpcaoCliente.BAIRRO_EMPRESA,
                OpcaoCliente.COMPLEMENTO_EMPRESA,
                OpcaoCliente.MUNICIPIO_EMPRESA,
                OpcaoCliente.UF_EMPRESA,
                OpcaoCliente.CEP_EMPRESA,
                OpcaoCliente.TELEFONE_EMPRESA,
                OpcaoCliente.DATA_ADMISSAO,
                OpcaoCliente.CARGO,
                OpcaoCliente.SALARIO,
                OpcaoCliente.NOME_CONJUGE,
                OpcaoCliente.DATA_NASCIMENTO_CONJUGE,
                OpcaoCliente.NOME_PAI,
                OpcaoCliente.NOME_MAE,
                OpcaoCliente.TELEFONE,
                OpcaoCliente.CELULAR,
                OpcaoCliente.EMAIL,
                OpcaoCliente.RECEBER_CREDITOROTATIVO));
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + " c_codtrib id,\n"
                    + " c_nometrib descricao,\n"
                    + " c_percent aliquota\n"
                    + "from tributacao;"
            )) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(
                            rs.getString("id"),
                            rs.getString("descricao"),
                            0,
                            rs.getDouble("aliquota"),
                            0));
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
                    ""
            )) {
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString(""));
                    imp.setEan(rs.getString(""));
                    imp.setQtdEmbalagem(1);

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
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "	p.c_codprod AS id,\n"
                    + "	p.c_codbarra AS ean,\n"
                    + "	p.c_nomeprod AS descricaocompleta,\n"
                    + "	p.c_unidade AS unidade,\n"
                    + "	p.c_grupo AS mercid1,\n"
                    + "	p.c_subgrupo AS mercid2,\n"
                    + "	p.c_subgrupo AS mercid3,\n"
                    + "	p.c_tributaecf AS id_tributacao,\n"
                    + "	p.c_stributaria,\n"
                    + "	p.c_dtcad AS datacadastro,\n"
                    + "	p.c_status AS situacao,\n"
                    + "	p.c_empresa,\n"
                    + "	p.c_prodbalanca AS e_balanca,\n"
                    + "	p.c_basereducao,\n"
                    + "	p.c_classefiscal AS ncm,\n"
                    + "	p.c_st_pis AS pis,\n"
                    + "	p.c_st_cofins AS cofins,\n"
                    + "	p.c_nat_rec_pc AS naturezareceita,\n"
                    + "	p.c_cest AS cest,\n"
                    + "	e.c_qtdatu AS estoque,\n"
                    + "	e.c_prvenda AS precovenda,\n"
                    + "	e.c_prcusto AS precocusto\n"
                    + "FROM\n"
                    + "	produtos p\n"
                    + "JOIN estoque e ON\n"
                    + "	e.c_codprod = p.c_codprod"
            )) {
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().getProdutosBalanca();
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());

                    imp.setImportId(rs.getString("id"));
                    //imp.setEan(rs.getString("ean"));
                    imp.setDescricaoCompleta(rs.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setTipoEmbalagem(rs.getString("unidade"));
                    imp.seteBalanca(rs.getBoolean("e_balanca"));
                    imp.setDataCadastro(rs.getDate("datacadastro"));

                    imp.setCodMercadologico1(rs.getString("mercid1"));
                    imp.setCodMercadologico2(rs.getString("mercid2"));
                    imp.setCodMercadologico3(rs.getString("mercid3"));
                    imp.setEstoque(rs.getDouble("estoque"));

                    imp.setCustoSemImposto(rs.getDouble("precocusto"));
                    imp.setCustoComImposto(imp.getCustoMedioSemImposto());
                    imp.setPrecovenda(rs.getDouble("precovenda"));

                    imp.setSituacaoCadastro(rs.getInt("situacao"));
                    imp.setNcm(rs.getString("ncm"));
                    imp.setCest(rs.getString("cest"));

                    imp.setPiscofinsCstDebito(rs.getString("pis"));
                    imp.setPiscofinsCstCredito(rs.getString("pis"));

                    imp.setIcmsConsumidorId(rs.getString("id_tributacao"));
                    imp.setIcmsDebitoId(imp.getIcmsConsumidorId());
                    imp.setIcmsCreditoId(imp.getIcmsConsumidorId());
                    imp.setIcmsCreditoForaEstadoId(imp.getIcmsConsumidorId());
                    imp.setIcmsDebitoForaEstadoId(imp.getIcmsConsumidorId());
                    imp.setIcmsDebitoForaEstadoNfId(imp.getIcmsConsumidorId());

                    int codigoProduto = Utils.stringToInt(rs.getString("id"), -2);
                    ProdutoBalancaVO produtoBalanca = produtosBalanca.get(codigoProduto);

                    if (produtoBalanca != null) {
                        imp.setEan(String.valueOf(produtoBalanca.getCodigo()));
                        imp.seteBalanca(true);
                        imp.setTipoEmbalagem("P".equals(produtoBalanca.getPesavel()) ? "KG" : "UN");
                        imp.setValidade(produtoBalanca.getValidade());
                        imp.setQtdEmbalagem(1);
                    } 
                    else {
                        imp.setEan(rs.getString("ean"));
                        imp.seteBalanca(rs.getBoolean("e_balanca"));
                        imp.setTipoEmbalagem(rs.getString("unidade"));
                        imp.setValidade(0);
                        imp.setQtdEmbalagem(0);
                    }
                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "	f.c_codforn AS id,\n"
                    + "	f.c_nomefor AS nome,\n"
                    + "	f.c_fantasia AS fantasia,\n"
                    + "	f.c_endereco AS endereco,\n"
                    + "	f.c_bairro AS bairro,\n"
                    + "	f.c_estado AS uf,\n"
                    + "	f.c_cep AS cep,\n"
                    + "	f.c_numero AS numero,\n"
                    + "	f.c_cgc AS cnpj,\n"
                    + "	f.c_insc AS inscricao,\n"
                    + "	f.c_fone AS telefone,\n"
                    + "	f.c_fax AS fax,\n"
                    + "	f.c_datacad AS datacadastro,\n"
                    + "	f.c_contato AS contato,\n"
                    + "	f.c_telconta AS telefone2,\n"
                    + "	f.c_email AS email,\n"
                    + "	f.c_empresa,\n"
                    + "	c.C_NOMECIDADE AS cidade,\n"
                    + "	f.c_tipo AS pessoa\n"
                    + "FROM\n"
                    + "	fornecedores f\n"
                    + "JOIN cad_cidade c ON c.C_CODCIDADE = f.C_CODCIDADE;"
            )) {
                while (rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());

                    imp.setImportId(rs.getString("id"));
                    imp.setRazao(rs.getString("nome"));
                    imp.setFantasia(rs.getString("fantasia"));

                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));

                    imp.setDatacadastro(rs.getDate("datacadastro"));
                    imp.setTel_principal(rs.getString("telefone"));
                    imp.setIe_rg(rs.getString("inscricao"));

                    String pessoa = (rs.getString("pessoa"));
                    if ("F".equals(pessoa)) {
                        imp.setTipo_inscricao(TipoInscricao.FISICA);
                        imp.setCnpj_cpf(rs.getString("cnpj"));
                    } else {
                        imp.setTipo_inscricao(TipoInscricao.JURIDICA);
                        imp.setCnpj_cpf(rs.getString("cnpj"));
                    }

                    imp.addContato(
                            rs.getString("contato"),
                            rs.getString("telefone2"),
                            "",
                            TipoContato.COMERCIAL,
                            rs.getString("email")
                    );

                    result.add(imp);
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
                    "SELECT DISTINCT\n"
                    + " p.c_grupo id,\n"
                    + " g.c_nomegrupo descri,\n"
                    + " p.c_subgrupo sub_id,\n"
                    + " s.c_nomegrupo descri2\n"
                    + "FROM PRODUTOS p\n"
                    + " JOIN GR_PRODUTOS g ON g.c_codgrupo = p.c_grupo\n"
                    + " JOIN CAD_SUB_GRUPO s ON s.c_codgrupo = p.c_subgrupo\n"
                    + "WHERE \n"
                    + " s.C_DTALTERACAO IS NOT NULL "
            )) {
                while (rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setMerc1ID(rs.getString("id"));
                    imp.setMerc1Descricao(rs.getString("descri"));
                    imp.setMerc2ID(rs.getString("sub_id"));
                    imp.setMerc2Descricao(rs.getString("descri2"));
                    imp.setMerc3ID(imp.getMerc2ID());
                    imp.setMerc3Descricao(imp.getMerc2Descricao());

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
                    + "	c_codprod AS produtoid,\n"
                    + "	c_referencia AS referencia,\n"
                    + "	c_fornecedor AS fornecedorid\n"
                    + "FROM\n"
                    + "	produtos\n"
                    + "WHERE\n"
                    + "	c_referencia <> '';"
            )) {
                while (rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());

                    imp.setIdFornecedor(rs.getString("fornecedorid"));
                    imp.setIdProduto(rs.getString("produtoid"));
                    imp.setCodigoExterno(rs.getString("referencia"));

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
                    + "	cli.c_codcli AS id,\n"
                    + "	cli.c_nomecli AS nome,\n"
                    + "	c.C_NOMECIDADE AS cidade,\n"
                    + "	cli.c_endereco AS endereco,\n"
                    + "	cli.c_numero AS numero,\n"
                    + "	cli.c_bairro AS bairro,\n"
                    + "	cli.c_estado AS uf,\n"
                    + "	cli.c_cep AS cep,\n"
                    + "	cli.c_cgc_cpf AS cnpjcpf,\n"
                    + "	cli.c_ins_rg AS inscrg,\n"
                    + "	cli.c_fone AS telefone,\n"
                    + "	cli.c_dtcad AS datacadastro,\n"
                    + "	cli.c_aniver AS datanascimento,\n"
                    + "	cli.c_pai AS pai,\n"
                    + "	cli.c_mae AS mae,\n"
                    + "	cli.c_convenio,\n"
                    + "	cli.c_grupo,\n"
                    + "	cli.c_email AS email,\n"
                    + "	cli.c_limite AS limite,\n"
                    + "	cli.c_celular AS celular,\n"
                    + "	cli.c_fantasia,\n"
                    + "	cli.c_empresa,\n"
                    + "	cli.c_status AS situacao,\n"
                    + "	cli.c_tipo as pessoa\n"
                    + "FROM\n"
                    + "	cad_clientes cli\n"
                    + "JOIN cad_cidade c ON c.C_CODCIDADE = cli.C_CODCIDADE;"
            )) {
                while (rs.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rs.getString("id"));
                    imp.setRazao(rs.getString("nome"));
                    imp.setFantasia(rs.getString("nome"));
                    imp.setCnpj(rs.getString("cnpjcpf"));
                    imp.setInscricaoestadual(rs.getString("inscrg"));

                    String pessoa = (rs.getString("pessoa"));
                    if ("F".equals(pessoa)) {
                        imp.setCnpj(rs.getString("cnpjcpf"));
                        imp.setInscricaoestadual(rs.getString("inscrg"));
                    }

                    imp.setDataNascimento(rs.getDate("datanascimento"));
                    imp.setDataCadastro(rs.getDate("datacadastro"));

                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));
                    imp.setTelefone(rs.getString("telefone"));
                    imp.setOrgaoemissor(rs.getString("uf"));
                    imp.setLimiteCompra(rs.getDouble("limite"));

                    imp.setAtivo(rs.getBoolean("situacao"));
                    imp.setNomeMae(rs.getString("mae"));
                    imp.setNomePai(rs.getString("pai"));

                    imp.setEmail(rs.getString("email"));
                    imp.setTelefone(rs.getString("telefone"));
                    imp.setCelular(rs.getString("celular"));

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
                    "select \n"
                    + " c_documento as id,\n"
                    + " c_fatura as numerodocumento,\n"
                    + " c_codfor as fornecedorid,\n"
                    + " c_parcela as parcela,\n"
                    + " c_valor as valor,\n"
                    + " c_dataemiss as dataemissao,\n"
                    + " c_datvenc as datavencimento,\n"
                    + " c_obs as obs\n"
                    + "from pagar\n"
                    + "where \n"
                    + "c_valorpgto = 0\n"
                    + "and\n"
                    + "c_datapgto is null;"
            )) {
                while (rs.next()) {
                    ContaPagarIMP imp = new ContaPagarIMP();
                    imp.setId(rs.getString("id"));
                    imp.setIdFornecedor(rs.getString("fornecedorid"));
                    imp.setNumeroDocumento(rs.getString("numerodocumento"));
                    imp.setDataEmissao(rs.getDate("dataemissao"));
                    imp.setDataEntrada(rs.getTimestamp("dataemissao"));
                    imp.setValor(rs.getDouble("valor"));
                    imp.setVencimento(rs.getDate("datavencimento"));
                    imp.setObservacao(rs.getString("obs"));

                    imp.addVencimento(
                            rs.getDate("datavencimento"),
                            rs.getDouble("valor"),
                            TipoPagamento.BOLETO_BANCARIO,
                            rs.getInt("parcela"));

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
                    "select \n"
                    + " c_documento as id,\n"
                    + " c_fatura as numerodocumento,\n"
                    + " c_codclie as clienteid,\n"
                    + " c_convenio,\n"
                    + " c_parcela as parcela,\n"
                    + " c_valor as valor,\n"
                    + " c_datemiss as dataemissao,\n"
                    + " c_datvenc as datavencimento,\n"
                    + " c_obs as obs\n"
                    + "from receber\n"
                    + "where \n"
                    + "c_valorpgto = 0\n"
                    + "and\n"
                    + "c_datapgto is null;"
            )) {
                while (rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();

                    imp.setId(rs.getString("id"));
                    imp.setIdCliente(rs.getString("clienteid"));
                    imp.setNumeroCupom(rs.getString("numerodocumento"));
                    imp.setDataEmissao(rs.getDate("dataemissao"));
                    imp.setValor(rs.getDouble("valor"));

                    imp.setDataVencimento(rs.getDate("datavencimento"));
                    imp.setObservacao(rs.getString("obs"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

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
        return new DxDAO.VendaIterator(getLojaOrigem(), this.dataInicioVenda, this.dataTerminoVenda);
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        return new DxDAO.VendaItemIterator(getLojaOrigem(), this.dataInicioVenda, this.dataTerminoVenda);
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
                        next.setSubTotalImpressora(rst.getDouble("valorliquido"));
                        next.setCancelado(rst.getBoolean("cancelado"));
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
                    = "";

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
                        next.setSequencia(rst.getInt("sequencia"));
                        next.setProduto(rst.getString("produtoid"));
                        next.setUnidadeMedida(rst.getString("unidade"));
                        next.setCodigoBarras(rst.getString("codigobarras"));
                        next.setDescricaoReduzida(rst.getString("descricao"));
                        next.setQuantidade(rst.getDouble("quantidade"));
                        next.setPrecoVenda(rst.getDouble("valor"));
                        next.setValorDesconto(rst.getDouble("desconto"));
                        next.setCancelado(rst.getBoolean("cancelado"));

                    }
                }
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaItemIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {
            this.sql
                    = "";
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
