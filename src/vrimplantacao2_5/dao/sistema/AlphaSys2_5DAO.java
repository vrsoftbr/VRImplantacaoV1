package vrimplantacao2_5.dao.sistema;

import java.util.Map;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.vo.importacao.ChequeIMP;
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
import vrimplantacao2.vo.enums.TipoInscricao;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.DesmembramentoIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;

/*
 *
 * @author Brunos
 *
 */
public class AlphaSys2_5DAO extends InterfaceDAO implements MapaTributoProvider {

    // SISTEMA REFATORADO DA 2.0 E NÃO VALIDADO, FAVOR REVER TODOS OS CAMPOS INCLUSIVE ESCRIPTLOJAORIGEM -- SELECT LOJA.
    private String lojaCliente;

    public String getLojaCliente() {
        return this.lojaCliente;
    }

    @Override
    public String getSistema() {
        return "AlphaSys";
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
                OpcaoCliente.RECEBER_CHEQUE,
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
                OpcaoCliente.VALOR_LIMITE,
                OpcaoCliente.RECEBER_CREDITOROTATIVO));
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "------"
            )) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(
                            rs.getString("codigo"),
                            rs.getString("descricao"),
                            0,
                            rs.getDouble("cst"),
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
                    imp.setImportId(rs.getString("id"));
                    imp.setEan(rs.getString("ean"));
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
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    p.cod_produto id,\n"
                    + "    p.dt_atualizacao_preco datacadastro,\n"
                    + "    p.dt_atualizacao_preco dataalteracao,\n"
                    + "    p.cod_barras ean,\n"
                    + "    p.cod_busca_preco,\n"
                    + "    p.fator_conversao qtdemb_cotacao,\n"
                    + "    p.cod_unidade_saida unidade,\n"
                    + "    pc.validade,\n"
                    + "    case upper(p.produto_balanca) when 'TRUE' then 1 else 0 end e_balanca,\n"
                    + "    p.nome descricaocompleta,\n"
                    + "    p.nome_pdv descricaoreduzida,\n"
                    + "    p.cod_grupo mercadologico1,\n"
                    + "    p.cod_subgrupo mercadologico2,\n"
                    + "    pc.peso_bruto,\n"
                    + "    pc.peso_liquido,\n"
                    + "    pc.estoque_minimo,\n"
                    + "    pc.estoque_maximo,\n"
                    + "    pc.custo_lucro margem,\n"
                    + "    pc.preco_compra custocomimposto,\n"
                    + "    pc.preco_compra - coalesce(pc.custo_imposto, 0) custosemimposto,\n"
                    + "    p.preco_vista precovenda,\n"
                    + "    p.ncm,\n"
                    + "    pc.situacao_tributaria_pis piscofins_saida,\n"
                    + "    pc.situacao_tributaria_cfe icms_cst,\n"
                    + "    pc.cfe_cst_percentual icms_aliquota,\n"
                    + "    pc.reducao_bc_icms_cfe icms_reduzido\n"
                    + "from\n"
                    + "    produto p\n"
                    + "    join produto_complemento pc on\n"
                    + "        pc.cod_produto = p.cod_produto and\n"
                    + "        pc.cod_empresa = p.cod_empresa\n"
                    + "order by\n"
                    + "    1"
            )) {
                int cont = 0;
                Map<Integer, vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO> produtosBalanca = new vrimplantacao.dao.cadastro.ProdutoBalancaDAO().carregarProdutosBalanca();
                                
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    
                    vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO bal = produtosBalanca.get(Utils.stringToInt(rst.getString("ean")));
                    if (bal != null) {
                        imp.setEan(bal.getCodigo() + "");
                        imp.setQtdEmbalagem(1);
                        imp.setValidade(bal.getValidade());
                        imp.seteBalanca(true);
                        imp.setTipoEmbalagem("U".equals(bal.getPesavel()) ? "UN" : "KG");
                    } else {
                        imp.setEan(rst.getString("ean"));
                        imp.setQtdEmbalagem(1);
                        imp.setValidade(rst.getInt("validade"));
                        imp.seteBalanca("S".equals(rst.getString("e_balanca")));
                        imp.setTipoEmbalagem(rst.getString("UNIDADE"));
                    }
                    
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setDataAlteracao(rst.getDate("dataalteracao"));
                    //imp.setEan(rst.getString("ean"));
                    imp.setQtdEmbalagemCotacao(rst.getInt("qtdemb_cotacao"));
                    //imp.setTipoEmbalagem(rst.getString("unidade"));
                    //imp.setValidade(rst.getInt("validade"));
                    //imp.seteBalanca(rst.getBoolean("e_balanca"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoGondola(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setCodMercadologico1(rst.getString("mercadologico1"));
                    imp.setCodMercadologico2(rst.getString("mercadologico2"));
                    imp.setPesoBruto(rst.getDouble("peso_bruto"));
                    imp.setPesoLiquido(rst.getDouble("peso_liquido"));
                    imp.setEstoqueMinimo(rst.getDouble("estoque_minimo"));
                    imp.setEstoqueMaximo(rst.getDouble("estoque_maximo"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setPiscofinsCstDebito(rst.getString("piscofins_saida"));
                    imp.setIcmsCst(rst.getInt("icms_cst"));
                    imp.setIcmsAliq(rst.getDouble("icms_aliquota"));
                    imp.setIcmsReducao(rst.getDouble("icms_reduzido"));

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
                    ""
            )) {
                while (rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());

                    imp.setImportId(rs.getString("codigo"));
                    imp.setRazao(rs.getString("nome"));
                    imp.setFantasia(rs.getString("nome"));

                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));

                    imp.setDatacadastro(rs.getDate("data_cadastro"));
                    imp.setTel_principal(rs.getString("telefone"));
                    imp.setAtivo(rs.getBoolean("status"));
                    imp.setIe_rg(rs.getString("insc_estadual"));

                    String pessoa = (rs.getString("pessoa"));
                    if ("F".equals(pessoa)) {
                        imp.setTipo_inscricao(TipoInscricao.FISICA);
                        imp.setCnpj_cpf(rs.getString("cpfcnpj"));
                    } else {
                        imp.setTipo_inscricao(TipoInscricao.JURIDICA);
                        imp.setCnpj_cpf(rs.getString("cpfcnpj"));
                    }

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
                    "select\n"
                    + "    cod_grupo merc1,\n"
                    + "    nome merc1_desc\n"
                    + "from\n"
                    + "    grupo g\n"
                    + "where\n"
                    + "    nivel = 0\n"
                    + "order by\n"
                    + "    1"
            )) {
                while (rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setMerc1ID(rs.getString("merc1"));
                    imp.setMerc1Descricao(rs.getString("desc1"));
                    imp.setMerc2ID(rs.getString("merc2"));
                    imp.setMerc2Descricao(rs.getString("desc2"));
                    imp.setMerc3ID(rs.getString("merc3"));
                    imp.setMerc3Descricao(rs.getString("desc3"));

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
                    "select \n"
                    + "	cod_colaborador	as idFornecedor,\n"
                    + "	cod_produto	as idProduto,\n"
                    + "	cod_produto_fornecedor as codigoexterno,\n"
                    + "	dt_compra as dataalteracao,\n"
                    + "	preco_compra custotabela\n"
                    + "from produto_fornecedor pf\n"
                    + "	where cod_produto_fornecedor is not null"
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
    public List<ChequeIMP> getCheques() throws Exception {
        List<ChequeIMP> Result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "")){
                while (rs.next()) {
                    ChequeIMP imp = new ChequeIMP();

                    imp.setId(rs.getString("codigo"));
                    imp.setDate(rs.getDate("data_aquisicao"));
                    imp.setDataDeposito(rs.getDate("data_vendimento"));
                    imp.setNumeroCheque(rs.getString("numero_cheque"));
                    imp.setBanco(rs.getInt("banco"));
                    imp.setAgencia(rs.getString("agencia"));
                    imp.setConta(rs.getString("codigo_conta"));
                    imp.setEcf(rs.getString("numero_pdv"));

                    String pessoa = (rs.getString("pessoa"));
                    if ("F".equals(pessoa)) {
                        imp.setCpf(rs.getString("cpf"));
                    }

                    imp.setCpf(rs.getString("cnpj"));
                    imp.setRg(rs.getString("rg"));
                    imp.setNome(rs.getString("nome"));
                    imp.setValor(rs.getDouble("valor"));

                    String contato = (rs.getString("telefone"));
                    if (!"".equals(contato)) {
                        imp.setTelefone(rs.getString("celular"));
                    }
                    imp.setTelefone(rs.getString("telefone"));

                    Result.add(imp);
                }
            }
        }
        return Result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	c.cod_colaborador as id,\n" +
                    "	c.cgc as cnpj,\n" +
                    "	c.ies as inscricaoestadual,\n" +
                    "	c.razao,\n" +
                    "	c.fantasia,\n" +
                    "	tl.nome||' '||l.nome as endereco,\n" +
                    "	c.numero,\n" +
                    "	c.complemento,\n" +
                    "	b.nome as bairro,\n" +
                    "	m.nome as municipio,\n" +
                    "	c.cod_estado as uf,\n" +
                    "	c.cep,\n" +
                    "	c.dt_cadastro as dataCadastro,\n" +
                    "	c.fone as telefone,\n" +
                    "	c.celular,\n" +
                    "	c.email,\n" +
                    "	c.fax,\n" +
                    "	cc.limite_credito,\n" +
                    "	cc.dia_pagto_1,\n" +
                    "	cc.dt_nascimento,\n" +
                    "	cc.sexo,\n" +
                    "	cc.numero_cartao,\n" +
                    "	cc.salario\n" +
                    "from colaborador C\n" +
                    "	join logradouro l\n" +
                    "		on c.cod_logradouro = l.cod_logradouro\n" +
                    "	join logradouro_tipo tl\n" +
                    "		on c.cod_logradouro_tipo = tl.cod_logradouro_tipo\n" +
                    "	join bairro b\n" +
                    "		on c.cod_bairro = b.cod_bairro\n" +
                    "	join municipio m\n" +
                    "		on c.cod_municipio = m.cod_municipio\n" +
                    "	left join CLIENTE_COMPLEMENTO cc on\n" +
                    "       cc.cod_empresa = c.cod_empresa and\n" +
                    "       cc.cod_colaborador = c.cod_colaborador\n" +
                    "where c.tipo = 1\n" +
                    "	order by c.cod_colaborador, c.tipo"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("id"));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setInscricaoestadual(rst.getString("inscricaoestadual"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setTelefone(rst.getString("telefone"));
                    imp.setCelular(rst.getString("celular"));
                    imp.setEmail(rst.getString("email"));
                    imp.setFax(rst.getString("fax"));
                    imp.setValorLimite(rst.getDouble("limite_credito"));
                    imp.setDiaVencimento(rst.getInt("dia_pagto_1"));
                    imp.setDataNascimento(rst.getDate("dt_nascimento"));
                    imp.setSexo(rst.getString("sexo"));
                    imp.setObservacao("NUMERO CARTAO " + rst.getString("numero_cartao"));
                    imp.setSalario(rst.getDouble("salario"));
                    imp.setPermiteCreditoRotativo(true);

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
                    "SELECT \n"
                    + " cp.CODIGO,\n"
                    + " cp.CODIGO_FORNECEDOR fornecedorid,\n"
                    + " cp.NUMERO_DOC,\n"
                    + " cp.NUMERO_PARCELA,\n"
                    + " cp.DATA_EMISSAO,\n"
                    + " CASE WHEN cp.DATA_VENCIMENTO IS NULL THEN cp.DATA_LANCAMENTO ELSE cp.DATA_VENCIMENTO END DATA_VENCIMENTO,\n"
                    + " cp.DATA_LANCAMENTO,\n"
                    + " cp.VALOR_NOMINAL,\n"
                    + " cp.VALOR_ABERTO,\n"
                    + " cp.MULTA,\n"
                    + " cp.JUROS_DIA,\n"
                    + " cp.OBSERVACAO,\n"
                    + " cp.CODIGO_FILIAL\n"
                    + "FROM CONTAS_PAGAR cp\n"
                    + "LEFT JOIN CONTAS_PAGAR_PAGAMENTO cpp ON cpp.CODIGO_CP = cp.CODIGO\n"
                    + "WHERE \n"
                    + " cpp.CODIGO IS NULL "
            )) {
                while (rs.next()) {
                    ContaPagarIMP imp = new ContaPagarIMP();
                    imp.setId(rs.getString("codigo"));
                    imp.setIdFornecedor(rs.getString("fornecedorid"));
                    imp.setNumeroDocumento(rs.getString("numero_doc"));
                    imp.setDataEmissao(rs.getDate("data_emissao"));
                    imp.setDataEntrada(rs.getTimestamp("data_lancamento"));
                    imp.setValor(rs.getDouble("valor_aberto"));
                    imp.setVencimento(rs.getDate("data_vencimento"));
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
                    "SELECT \n"
                    + " cr.CODIGO,\n"
                    + " cr.CODIGO_CLIENTE clienteid,\n"
                    + " cr.NUMERO_PARCELA,\n"
                    + " cr.DATA_EMISSAO,\n"
                    + " cr.HORA_EMISSAO,\n"
                    + " cr.DATA_VENCIMENTO,\n"
                    + " cr.STATUS,\n"
                    + " cr.VALOR_NOMINAL,\n"
                    + " cr.VALOR_ABERTO,\n"
                    + " cr.OBSERVACAO,\n"
                    + " cr.CODIGO_FILIAL,\n"
                    + " cr.CODIGO_OS,\n"
                    + " cr.CODIGO_VENDA,\n"
                    + " cr.CODIGO_VENDA_ECF\n"
                    + "FROM CONTAS_RECEBER cr\n"
                    + "LEFT JOIN CONTAS_RECEBER_PAGAMENTO crp ON crp.CODIGO_CR = cr.CODIGO \n"
                    + "WHERE \n"
                    + " crp.CODIGO IS NULL"
            )) {
                while (rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();

                    imp.setId(rs.getString("codigo"));
                    imp.setIdCliente(rs.getString("clienteid"));
                    imp.setNumeroCupom(rs.getString("codigo_venda_ecf"));
                    imp.setDataEmissao(rs.getDate("data_emissao"));
                    imp.setEcf(rs.getString("codigo_venda_ecf"));
                    imp.setValor(rs.getDouble("valor_aberto"));

                    imp.setDataVencimento(rs.getDate("data_vencimento"));
                    imp.setObservacao(rs.getString("observacao"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<DesmembramentoIMP> getDesmembramentos() throws Exception {
        List<DesmembramentoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "	i.codigo id_desmem,\n"
                    + "	d.CODIGO_PRODUTO_FRACIONAVEL prod_pai,\n"
                    + "	i.CODIGO_PRODUTO_FRACIONADO prod_filho,\n"
                    + "	p.DESCRICAO produto,\n"
                    + "	i.QUANTIDADE percentual\n"
                    + "FROM\n"
                    + "	PRODUTO_FRACIONAMENTO d\n"
                    + "	JOIN PRODUTO_FRACIONAMENTO_ITEM i ON d.CODIGO = i.CODIGO_FRACIONAMENTO\n"
                    + "	JOIN produto p ON p.CODIGO = i.CODIGO_PRODUTO_FRACIONADO\n"
                    + "WHERE\n"
                    + "	d.CODIGO_FILIAL = " + getLojaOrigem() + "\n"
                    + " ORDER by 1"
            )) {
                while (rs.next()) {
                    DesmembramentoIMP imp = new DesmembramentoIMP();

                    imp.setId(rs.getString("id_desmem"));
                    imp.setProdutoPai(rs.getString("prod_pai"));
                    imp.setProdutoFilho(rs.getString("prod_filho"));
                    imp.setPercentual(rs.getDouble("percentual"));

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
        return new AlphaSys2_5DAO.VendaIterator(getLojaOrigem(), this.dataInicioVenda, this.dataTerminoVenda);
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        return new AlphaSys2_5DAO.VendaItemIterator(getLojaOrigem(), this.dataInicioVenda, this.dataTerminoVenda);
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
                    = "SELECT \n"
                    + " CODIGO id_venda,\n"
                    + " TIPO_VENDA,\n"
                    + " DATA_VENDA data,\n"
                    + " HORA_VENDA hora,\n"
                    + " VENDA_BRUTA valorbruto,\n"
                    + " VENDA_LIQUIDA valorliquido,\n"
                    + " DESCONTO_VALOR desconto,\n"
                    + " CODIGO numerocupom,\n"
                    + " CODIGO_ECF ecf,\n"
                    + " CASE WHEN ESTORNO = 'S' THEN 1 ELSE 0 END cancelado,\n"
                    + " CODIGO_FILIAL\n"
                    + "FROM VENDA\n"
                    + "WHERE \n"
                    + " DATA_VENDA BETWEEN '" + strDataInicio + "' and '" + strDataTermino + "'";

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
                    = "SELECT \n"
                    + "vi.CODIGO_ITEM id_item,\n"
                    + "vi.CODIGO_VENDA id_venda,\n"
                    + "vi.CODIGO_PRODUTO produtoid,\n"
                    + "vi.PAF_DESCRICAO_PRODUTO descricao,\n"
                    + "vi.CODIGO_BARRA codigobarras,\n"
                    + "CASE WHEN vi.PAF_UNIDADE_MEDIDA NOT IN ('UN','KG','CX') THEN 'UN'\n"
                    + "     ELSE vi.PAF_UNIDADE_MEDIDA END unidade,\n"
                    + "vi.SEQUENCIA,\n"
                    + "vi.QUANTIDADE,\n"
                    + "vi.PRECO_UNITARIO valor,\n"
                    + "vi.DESCONTO_VALOR desconto,\n"
                    + "CASE WHEN vi.ESTORNO = 'S' THEN 1 ELSE 0 END cancelado\n"
                    + "FROM VENDA_ITEM vi\n"
                    + "JOIN VENDA v ON v.CODIGO = vi.CODIGO_VENDA \n"
                    + "WHERE \n"
                    + "  v.DATA_VENDA BETWEEN '" + VendaIterator.FORMAT.format(dataInicio) + "' and '" + VendaIterator.FORMAT.format(dataTermino) + "';";
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
