package vrimplantacao2_5.dao.sistema;

import java.util.Map;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.FornecedorContatoIMP;
import vrimplantacao2_5.dao.conexao.ConexaoFirebird;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/*
 *
 * @author Guilherme
 *
 */
public class MegaSoftwareDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "MEGA SOFTWARE";
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
                //OpcaoProduto.VENDA_PDV,
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
                OpcaoProduto.MERCADOLOGICO_PRODUTO
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
                    ""
            )) {
                while (rs.next()) {
                    String id = rs.getString("cst") + "-" + rs.getString("aliquota") + "-" + rs.getString("reducao");
                    result.add(new MapaTributoIMP(
                            id,
                            id,
                            rs.getInt("cst"),
                            rs.getDouble("aliquota"),
                            rs.getDouble("reducao")));
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
                    "SELECT\n" +
                    "	p.PRO_CODI id,\n" +
                    "	p.PRO_DESC descricaocompleta,\n" +
                    "	p.DESC_PDV descricaoreduzida,\n" +
                    "	p.PRO_UNID unidade,\n" +
                    "	p.BALANCA,\n" +
                    "	p.EMBALAGEM,\n" +
                    "	p.PRO_BARR ean,\n" +
                    "	p.PRO_GRUP grupo,\n" +
                    "	p.PRO_SUBG subgrupo,\n" +
                    "	p.PRO_CUST custo,\n" +
                    "	p.PRO_VEND precovenda,\n" +
                    "	p.PRO_LUCR margem,\n" +
                    "	p.PRO_EATU estoque,\n" +
                    "	p.aliq aliquota,\n" +
                    "	p.cst,\n" +
                    "	p.CST_PIS,\n" +
                    "	p.CST_COFINS,\n" +
                    "	p.NCM,\n" +
                    "	p.NAT_RECEITA,\n" +
                    "	p.INATIVO,\n" +
                    "	p.CEST\n" +
                    "FROM \n" +
                    "	PRO001 p"
            )) {
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().getProdutosBalanca();
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());

                    imp.setImportId(rs.getString("Id"));
                    imp.setEan(rs.getString("ean"));
                    imp.setDescricaoCompleta(rs.getString("descricaoCompleta"));
                    imp.setDescricaoReduzida(rs.getString("descricaoReduzida"));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setTipoEmbalagem(rs.getString("tipoEmbalagem"));
                    imp.seteBalanca(rs.getBoolean("e_balanca"));
                    imp.setDataCadastro(rs.getDate("dataCadastro"));
                    imp.setDataAlteracao(rs.getDate("dataAlteracao"));

                    imp.setCodMercadologico1(rs.getString("codMercadologico1"));
                    imp.setCodMercadologico2(rs.getString("codMercadologico2"));
                    imp.setCodMercadologico3(rs.getString("codMercadologico3"));
                    imp.setEstoque(rs.getDouble("estoque"));
                    imp.setEstoqueMaximo(rs.getDouble("estoqueMaximo"));
                    imp.setEstoqueMaximo(rs.getDouble("estoqueMinimo"));
                    imp.setPesoBruto(rs.getDouble("pesoBruto"));
                    imp.setPesoLiquido(rs.getDouble("pesoLiquido"));

                    imp.setMargem(rs.getDouble("margem"));
                    imp.setCustoSemImposto(rs.getDouble("custoSemImposto"));
                    imp.setCustoComImposto(rs.getDouble("custoComImposto"));
                    imp.setPrecovenda(rs.getDouble("precovenda"));

                    imp.setSituacaoCadastro(rs.getInt("situacaoCadastro"));
                    imp.setNcm(rs.getString("ncm"));
                    imp.setCest(rs.getString("cest"));

                    imp.setPiscofinsCstDebito(rs.getInt("piscofinsCstDebito"));
                    imp.setPiscofinsCstCredito(rs.getInt("piscofinsCstCredito"));
                    imp.setPiscofinsNaturezaReceita(rs.getInt("piscofinsNaturezaReceita"));

                    String icmsId = rs.getString("icmsCstSaida") + "-" + rs.getString("icmsAliqSaida") + "-" + rs.getString("icmsReducaoSaida");

                    imp.setIcmsConsumidorId(icmsId);
                    imp.setIcmsDebitoId(icmsId);
                    imp.setIcmsCreditoId(icmsId);
                    imp.setIcmsCreditoForaEstadoId(icmsId);
                    imp.setIcmsDebitoForaEstadoId(icmsId);
                    imp.setIcmsDebitoForaEstadoNfId(icmsId);

                    int codigoProduto = Utils.stringToInt(rs.getString("id"), -2);
                    ProdutoBalancaVO produtoBalanca = produtosBalanca.get(codigoProduto);

                    if (produtoBalanca != null) {
                        imp.setEan(String.valueOf(produtoBalanca.getCodigo()));
                        imp.seteBalanca(true);
                        imp.setTipoEmbalagem("P".equals(produtoBalanca.getPesavel()) ? "KG" : "UN");
                        imp.setValidade(produtoBalanca.getValidade());
                        imp.setQtdEmbalagem(1);
                    } else {
                        imp.setEan(rs.getString("ean"));
                        imp.seteBalanca(false);
                        imp.setTipoEmbalagem(rs.getString("tipoEmbalagem"));
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
                    "SELECT \n" +
                    "	f.FOR_CODI id,\n" +
                    "	f.FOR_NOME razao,\n" +
                    "	f.fantasia,\n" +
                    "	f.for_num numero,\n" +
                    "	f.for_complemento complemento,\n" +
                    "	f.FOR_CIDA municipio,\n" +
                    "	f.for_ende endereco,\n" +
                    "	f.FOR_BAIR bairro,\n" +
                    "	f.FOR__CEP cep,\n" +
                    "	f.FOR_ESTA uf,\n" +
                    "	f.FOR_TELE fone,\n" +
                    "	f.FOR__CPF cnpj,\n" +
                    "	f.FOR_IEST ie,\n" +
                    "	f.FOR_OBS1 obs,\n" +
                    "	f.EMAIL,\n" +
                    "	f.DT_CADASTRO cadastro,\n" +
                    "	f.FOR__FAX fax\n" +
                    "FROM \n" +
                    "	FOR001 f"
            )) {
                while (rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());

                    imp.setImportId(rs.getString("id"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setCnpj_cpf(rs.getString("cnpj_cpf"));
                    imp.setIe_rg(rs.getString("ie_rg"));
                    imp.setInsc_municipal(rs.getString("insc_municipal"));
                    imp.setSuframa(rs.getString("suframa"));
                    imp.setAtivo(rs.getInt("ativo") == 1);

                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("municipio"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));
                    imp.setValor_minimo_pedido(rs.getFloat("valor_minimo_pedido"));
                    imp.setDatacadastro(rs.getDate("datacadastro"));
                    imp.setObservacao(rs.getString("observacao"));

                    imp.setTel_principal(rs.getString("fone1"));

                    String fax = (rs.getString("fax"));
                    if (!"".equals(fax)) {
                        FornecedorContatoIMP cont = imp.getContatos().make("1");
                        cont.setImportId("1");
                        cont.setNome("FAX");
                        cont.setTipoContato(TipoContato.COMERCIAL);
                        cont.setTelefone(fax);
                    }

                    String email = (rs.getString("email"));
                    if (!"".equals(email)) {
                        FornecedorContatoIMP cont = imp.getContatos().make("2");
                        cont.setImportId("2");
                        cont.setNome("EMAIL");
                        cont.setTipoContato(TipoContato.COMERCIAL);
                        cont.setTelefone(email);
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
                    "SELECT \n" +
                    "	g.GRU_CODI merc1,\n" +
                    "	g.GRU_DESC descmerc1,\n" +
                    "	sg.SUB_GRUPO merc2,\n" +
                    "	sg.DESCRICAO descmerc2\n" +
                    "FROM \n" +
                    "	SUB_GRUPO sg \n" +
                    "JOIN GRU001 g ON sg.GRUPO = g.GRU_CODI \n" +
                    "ORDER BY \n" +
                    "	2, 4"
            )) {
                while (rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setMerc1ID(rs.getString("merc1"));
                    imp.setMerc1Descricao(rs.getString("descmerc1"));
                    imp.setMerc2ID(rs.getString("merc2"));
                    imp.setMerc2Descricao(rs.getString("descmerc2"));
                    imp.setMerc3ID("1");
                    imp.setMerc3Descricao(rs.getString("descmerc2"));

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
                    "  SELECT\n"
                    + "	ID_FORCLI id_fornecedor,\n"
                    + "	ID_PRODUTO id_produto,\n"
                    + "	CODIGO cod_externo\n"
                    + "FROM\n"
                    + "	FOR_CLI_PRODUTOS \n"
                    + "ORDER BY 1,2"
            )) {
                while (rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());

                    imp.setIdFornecedor(rs.getString("id_fornecedor"));
                    imp.setIdProduto(rs.getString("id_produto"));
                    imp.setCodigoExterno(rs.getString("cod_externo"));

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
                    "SELECT \n" +
                    "	c.CLI_CODI id,\n" +
                    "	c.CLI_NOME razao,\n" +
                    "	c.cli_fant fantasia,\n" +
                    "	c.CLI__PAI pai,\n" +
                    "	c.CLI__MAE mae,\n" +
                    "	c.CLI_DTAN nascimento,\n" +
                    "	c.CLI_TELE fone,\n" +
                    "	c.cli_celular celular,\n" +
                    "	c.CLI__FAX fax,\n" +
                    "	c.CLI__CPF cpf,\n" +
                    "	c.CLI__IEST ie,\n" +
                    "	c.CLI___RG,\n" +
                    "	c.CLI_ENDE endereco,\n" +
                    "	c.cli_complemento complemento,\n" +
                    "	c.NUMERO,\n" +
                    "	c.CLI_BAIR bairro,\n" +
                    "	c.CLI_CIDA cidade,\n" +
                    "	c.CLI_ESTA uf,\n" +
                    "	c.CLI__CEP cep,\n" +
                    "	c.CLI_DT_C cadastro,\n" +
                    "	c.CLI_LIMI limite,\n" +
                    "	c.EMAIL email,\n" +
                    "	c.sexo\n" +
                    "FROM \n" +
                    "	CLI001 c"
            )) {
                while (rs.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rs.getString("id"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setCnpj(rs.getString("cnpj"));
                    imp.setInscricaoestadual(rs.getString("inscricaoestadual"));
                    imp.setDataNascimento(rs.getDate("dataNascimento"));
                    imp.setDataCadastro(rs.getDate("dataCadastro"));

                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("municipio"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));
                    imp.setTelefone(rs.getString("telefone"));
                    imp.setOrgaoemissor(rs.getString("orgaoemissor"));

                    imp.setEstadoCivil(rs.getString("estadoCivil"));
                    imp.setAtivo(rs.getBoolean("ativo"));
                    imp.setNomeConjuge(rs.getString("nomeConjuge"));
                    imp.setNomeMae(rs.getString("nomeMae"));
                    imp.setNomePai(rs.getString("nomePai"));
                    imp.setEmpresa(rs.getString("empresa"));
                    imp.setEmpresaEndereco(rs.getString("empresaEndereco"));
                    imp.setEmpresaComplemento(rs.getString("empresaComplemento"));
                    imp.setEmpresaBairro(rs.getString("empresaBairro"));
                    imp.setEmpresaMunicipio(rs.getString("empresaMunicipio"));
                    imp.setEmpresaUf(rs.getString("empresaUf"));
                    imp.setEmpresaCep(rs.getString("empresaCep"));
                    imp.setEmpresaTelefone(rs.getString("empresaTelefone"));
                    imp.setDataAdmissao(rs.getDate("dataAdmissao"));
                    imp.setCargo(rs.getString("cargo"));
                    imp.setEmpresaTelefone(rs.getString("empresaTelefone"));
                    imp.setSalario(rs.getDouble("salario"));
                    imp.setDiaVencimento(rs.getInt("diaVencimento"));

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
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT \n" +
                    "	SEQUENCIAL id,\n" +
                    "	CRE_CODI id_cliente,\n" +
                    "	nome_c razao,\n" +
                    "	CRE_NFIS nf,\n" +
                    "	coo,\n" +
                    "	caixa,\n" +
                    "	CRE_D_EM emissao,\n" +
                    "	CRE_D_VE vencimento,\n" +
                    "	CRE_CONT contador,\n" +
                    "	CRE_NOTA valor,\n" +
                    "	cre_rest restante,\n" +
                    "	CRE_PAGO pago,\n" +
                    "	(cre_nota - CRE_REST) total\n" +
                    "FROM \n" +
                    "	CRE001 c\n" +
                    "WHERE \n" +
                    "	(cre_nota - CRE_REST) > 0"
            )) {
                while (rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();

                    imp.setId(rs.getString("id"));
                    imp.setIdCliente(rs.getString("idCliente"));
                    imp.setCnpjCliente(rs.getString("CPFCnpj"));
                    imp.setNumeroCupom(rs.getString("numeroDocumento"));
                    imp.setDataEmissao(rs.getDate("dataEmissao"));
                    imp.setValor(rs.getDouble("valor"));

                    imp.setDataVencimento(rs.getDate("vencimento"));
                    imp.setObservacao(rs.getString("observacao"));

                    result.add(imp);
                }
            }
        }

        return result;
    }
}
