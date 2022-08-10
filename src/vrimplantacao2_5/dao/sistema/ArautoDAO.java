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
import vrimplantacao2.vo.enums.TipoSexo;
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
public class ArautoDAO extends InterfaceDAO implements MapaTributoProvider {

    private ConexaoFirebird stmPessoa;
    
    @Override
    public String getSistema() {
        return "ARAUTO";
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
                OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR
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
    
    public void setStatementPessoa(ConexaoFirebird stm) {
        this.stmPessoa = stm;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT \n" +
                    "	id,\n" +
                    "	IMPOSTO,\n" +
                    "	ALIQUOTA,\n" +
                    "	cst\n" +
                    "FROM \n" +
                    "	IMPOSTO"
            )) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(
                            rs.getString("id"),
                            rs.getString("imposto"),
                            rs.getInt("cst"),
                            rs.getDouble("aliquota"),
                            0));
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
                    "	p.id,\n" +
                    "	p.PRODUTO descricaocompleta,\n" +
                    "	p.NOME_COMPLEMENTAR complemento,\n" +
                    "	p.unidade,\n" +
                    "	p.UNIDADE_COMPRA,\n" +
                    "	p.QUATDE_FTD qtdembcompra,\n" +
                    "	p.cod1 ean,\n" +
                    "	p.CODIGO_CAIXA,\n" +
                    "	p.datacadastro,\n" +
                    "	p.cod2,\n" +
                    "	p.PESANOCAIXA,\n" +
                    "	p.EMPRESA,\n" +
                    "	p.ESTOQUEFISICO estoque,\n" +
                    "	p.ESTOQUEMAXIMO,\n" +
                    "	p.ESTOQUEMINIMO,\n" +
                    "	p.COMPRA custo,\n" +
                    "	p.MARGEM,\n" +
                    "	p.MARKUP,\n" +
                    "	p.unitario,\n" +
                    "	p.VENDA precovenda,\n" +
                    "	p.valor,\n" +
                    "	p.ativo,\n" +
                    "	p.IMPOSTO idicms,\n" +
                    "	p.ICMS,\n" +
                    "	p.ICMSE,\n" +
                    "	p.ICMSS,\n" +
                    "	p.SITTR cst,\n" +
                    "	p.SAT_CST,\n" +
                    "	p.NCM,\n" +
                    "	p.CEST,\n" +
                    "	p.CST_PIS_COFINS\n" +
                    "FROM \n" +
                    "	PRODUTO p"
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
                    imp.setTipoEmbalagem(rs.getString("unidade"));
                    
                    int qtdEmbalagem = Utils.stringToInt(rs.getString("embalagem"), 1); 
                    
                    imp.setQtdEmbalagemCotacao(qtdEmbalagem);
                    imp.seteBalanca(rs.getString("balanca").equals("S"));
                    imp.setCodMercadologico1(rs.getString("merc1"));
                    imp.setCodMercadologico2(rs.getString("merc2"));
                    imp.setCodMercadologico3("1");
                    imp.setEstoque(rs.getDouble("estoque"));
                    imp.setMargem(rs.getDouble("margem"));
                    imp.setCustoSemImposto(rs.getDouble("custo"));
                    imp.setCustoComImposto(imp.getCustoSemImposto());
                    imp.setPrecovenda(rs.getDouble("precovenda"));
                    imp.setSituacaoCadastro(rs.getString("inativo").equals("") ? 1 : 0);
                    imp.setNcm(rs.getString("ncm"));
                    imp.setCest(rs.getString("cest"));
                    imp.setPiscofinsCstDebito(rs.getInt("cst_cofins"));
                    imp.setPiscofinsNaturezaReceita(rs.getString("nat_receita"));

                    String icmsId = rs.getString("aliquota");
                    
                    imp.setIcmsConsumidorId(icmsId);
                    imp.setIcmsDebitoId(icmsId);
                    imp.setIcmsCreditoId(icmsId);
                    imp.setIcmsCreditoForaEstadoId(icmsId);
                    imp.setIcmsDebitoForaEstadoId(icmsId);
                    imp.setIcmsDebitoForaEstadoNfId(icmsId);
                    
                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();

        try (Statement stm = stmPessoa.createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT \n" +
                    "	f.FOR_CODI id,\n" +
                    "	f.FOR_NOME razao,\n" +
                    "   f.FOR__CGC cnpj,\n" +
                    "	f.FOR__CPF cpf,\n" +
                    "	f.fantasia,\n" +
                    "	f.for_num numero,\n" +
                    "	f.for_complemento complemento,\n" +
                    "	f.FOR_CIDA municipio,\n" +
                    "	f.for_ende endereco,\n" +
                    "	f.FOR_BAIR bairro,\n" +
                    "	f.FOR__CEP cep,\n" +
                    "	f.FOR_ESTA uf,\n" +
                    "	f.FOR_TELE fone,\n" +
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
                    imp.setCnpj_cpf(rs.getString("cnpj"));
                    
                    if (imp.getCnpj_cpf() == null && imp.getCnpj_cpf().isEmpty()) {
                        imp.setCnpj_cpf(rs.getString("cpf"));
                    }
                    
                    imp.setIe_rg(rs.getString("ie"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("municipio"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));
                    imp.setDatacadastro(rs.getDate("cadastro"));
                    imp.setObservacao(rs.getString("obs"));

                    imp.setTel_principal(rs.getString("fone"));

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
                    "	COALESCE(sg.SUB_GRUPO, g.GRU_CODI) merc2,\n" +
                    "	COALESCE(sg.DESCRICAO, g.GRU_DESC) descmerc2\n" +
                    "FROM \n" +
                    "	GRU001 g \n" +
                    "LEFT JOIN SUB_GRUPO sg ON sg.GRUPO = g.GRU_CODI \n" +
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
                    ""
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
                    "   c.CLI__CGC cnpj,\n" +        
                    "	c.CLI__CPF cpf,\n" +
                    "	c.CLI__IEST ie,\n" +
                    "	c.CLI___RG rg,\n" +
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
                    
                    if(imp.getCnpj().isEmpty()) {
                        imp.setCnpj(rs.getString("cpf"));
                    }
                    
                    imp.setInscricaoestadual(rs.getString("ie"));
                    
                    if (imp.getInscricaoestadual().isEmpty()) {
                        imp.setInscricaoestadual(rs.getString("rg"));
                    }
                    imp.setDataCadastro(rs.getDate("cadastro"));
                    imp.setDataNascimento(rs.getDate("nascimento"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));
                    imp.setTelefone(rs.getString("fone"));
                    imp.setFax(rs.getString("fax"));
                    imp.setNomeMae(rs.getString("mae"));
                    imp.setNomePai(rs.getString("pai"));
                    imp.setEmail(rs.getString("email"));
                    imp.setCelular(rs.getString("celular"));
                    imp.setSexo(rs.getString("sexo") != null && rs.getString("sexo").equals("M") ? TipoSexo.MASCULINO : TipoSexo.FEMININO);
                    imp.setValorLimite(rs.getDouble("limite"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();

        try (Statement stm = stmPessoa.createStatement()) {
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
                    "	CRE_REST > 0")) {
                while (rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();

                    imp.setId(rs.getString("id"));
                    imp.setIdCliente(rs.getString("id_cliente"));
                    imp.setEcf(rs.getString("caixa"));
                    imp.setNumeroCupom(rs.getString("coo"));
                    imp.setDataEmissao(rs.getDate("emissao"));
                    imp.setValor(rs.getDouble("restante"));

                    imp.setDataVencimento(rs.getDate("vencimento"));

                    result.add(imp);
                }
            }
        }

        return result;
    }
}
