package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import vrimplantacao2_5.dao.conexao.ConexaoPostgres;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author leandro
 */
public class InovaDAO extends InterfaceDAO implements MapaTributoProvider {

    private String complemento = "";

    public void setComplemento(String complemento) {
        this.complemento = complemento != null ? complemento.trim() : "";
    }

    @Override
    public String getSistema() {
        if ("".equals(complemento)) {
            return "Inova";
        } else {
            return "Inova - " + complemento;
        }
    }

    @Override
    public Set<OpcaoCliente> getOpcoesDisponiveisCliente() {
        return new HashSet<>(Arrays.asList(
                OpcaoCliente.DADOS,
                OpcaoCliente.ENDERECO,
                OpcaoCliente.CONTATOS,
                OpcaoCliente.DATA_CADASTRO,
                OpcaoCliente.DATA_NASCIMENTO,
                OpcaoCliente.TELEFONE,
                OpcaoCliente.ESTADO_CIVIL,
                OpcaoCliente.EMPRESA,
                OpcaoCliente.SALARIO,
                OpcaoCliente.PERMITE_CREDITOROTATIVO,
                OpcaoCliente.BLOQUEADO,
                OpcaoCliente.OBSERVACOES2,
                OpcaoCliente.OBSERVACOES,
                OpcaoCliente.NUMERO,
                OpcaoCliente.COMPLEMENTO,
                OpcaoCliente.SITUACAO_CADASTRO,
                OpcaoCliente.RECEBER_CREDITOROTATIVO));
    }

    @Override
    public Set<OpcaoFornecedor> getOpcoesDisponiveisFornecedor() {
        return new HashSet<>(Arrays.asList(
                OpcaoFornecedor.FORCAR_UNIFICACAO,
                OpcaoFornecedor.IMPORTAR_SOMENTE_ATIVOS,
                OpcaoFornecedor.DADOS,
                OpcaoFornecedor.RAZAO_SOCIAL,
                OpcaoFornecedor.NOME_FANTASIA,
                OpcaoFornecedor.CNPJ_CPF,
                OpcaoFornecedor.INSCRICAO_ESTADUAL,
                OpcaoFornecedor.INSCRICAO_MUNICIPAL,
                OpcaoFornecedor.ENDERECO,
                OpcaoFornecedor.NUMERO,
                OpcaoFornecedor.COMPLEMENTO,
                OpcaoFornecedor.BAIRRO,
                OpcaoFornecedor.MUNICIPIO,
                OpcaoFornecedor.SITUACAO_CADASTRO,
                OpcaoFornecedor.CEP,
                OpcaoFornecedor.UF,
                OpcaoFornecedor.TELEFONE,
                OpcaoFornecedor.DATA_CADASTRO,
                OpcaoFornecedor.OBSERVACAO,
                OpcaoFornecedor.EMAIL,
                OpcaoFornecedor.CELULAR,
                OpcaoFornecedor.CONTATOS,
                OpcaoFornecedor.PAGAR_FORNECEDOR,
                OpcaoFornecedor.OUTRAS_RECEITAS,
                OpcaoFornecedor.PRODUTO_FORNECEDOR
        ));
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                new OpcaoProduto[]{
                    OpcaoProduto.MERCADOLOGICO,
                    OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                    OpcaoProduto.MERCADOLOGICO_PRODUTO,
                    OpcaoProduto.FAMILIA,
                    OpcaoProduto.FAMILIA_PRODUTO,
                    OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                    OpcaoProduto.PRODUTOS,
                    OpcaoProduto.EAN,
                    OpcaoProduto.EAN_EM_BRANCO,
                    OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
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
                    OpcaoProduto.ESTOQUE,
                    OpcaoProduto.ATIVO,
                    OpcaoProduto.NCM,
                    OpcaoProduto.CEST,
                    OpcaoProduto.PIS_COFINS,
                    OpcaoProduto.NATUREZA_RECEITA,
                    OpcaoProduto.ICMS,
                    OpcaoProduto.PAUTA_FISCAL,
                    OpcaoProduto.PAUTA_FISCAL_PRODUTO,
                    OpcaoProduto.MARGEM,
                    OpcaoProduto.OFERTA,
                    OpcaoProduto.CUSTO,
                    OpcaoProduto.FORCAR_ATUALIZACAO,}
        ));
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	familiaid id,\n"
                    + "	familiacodigo descricao\n"
                    + "FROM\n"
                    + "	produtosfamilia"
            )) {
                while (rst.next()) {
                    if ((rst.getString("descricao") != null)
                            && (!rst.getString("descricao").trim().isEmpty())) {
                        FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("id"));
                        imp.setDescricao(rst.getString("descricao"));
                        vResult.add(imp);
                    }
                }
            }
        }
        return vResult;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	fornecedorid id,\n"
                    + "	fornecedorrazaosocial razao,\n"
                    + "	fornecedornomefantasia fantasia,\n"
                    + "	fornecedorendereco endereco,\n"
                    + "	fornecedorbairro bairro,\n"
                    + "	fornecedorcep cep,\n"
                    + "	fornecedorcidade municipioIBGE,\n"
                    + "	fornecedoruf ufIBGE,\n"
                    + "	fornecedornumero numero,\n"
                    + "	fornecedortelefone tel_principal,\n"
                    + "	fornecedorcnpj cpfcnpj,\n"
                    + "	fornecedorie inscestadual,\n"
                    + "	fornecedoremail email,\n"
                    + "	fornecedordatacadastro dtcadastro,\n"
                    + "	fornecedorstatus status,\n"
                    + "	fornecedorcomplemento complemento \n"
                    + "FROM\n"
                    + "	fornecedor\n"
                    + "ORDER BY 1"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("cpfcnpj"));
                    imp.setIe_rg(rst.getString("inscestadual"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setCep(rst.getString("cep"));
                    imp.setIbge_municipio(rst.getInt("municipioIBGE"));
                    imp.setIbge_uf(rst.getInt("ufIBGE"));
                    imp.setTel_principal(rst.getString("tel_principal"));
                    imp.setDatacadastro(rst.getDate("dtcadastro"));
                    imp.setComplemento(rst.getString("complemento"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	p.produtoid id,\n"
                    + "	p.produtodatacadastro datacadastro,\n"
                    + "	p.produtodataultimaalteracao dataalteracao,\n"
                    + "	p.produtocodigobarra ean,\n"
                    + "	p.produtopesavel pesavel,\n"
                    + "	p.produtotipo,\n"
                    + "	p.produtounitprodutoqtd qtdembalagem,\n"
                    + "	p.produtounidademedida unidade,\n"
                    + "	p.produtodiasvalidade validade,\n"
                    + "	coalesce(nullif(trim(p.produtodescricaodetalhada),''), p.produtodescricao) descricaocompleta,\n"
                    + "	p.produtodescricao descricaoreduzida,\n"
                    + "	p.produtocategoriaid mercadologico1,\n"
                    + "	p.produtofamiliaid id_familia,\n"
                    + "	p.produtoqtdestoquemax estoquemaximo,\n"
                    + "	p.produtoqtdestoquemin estoqueminimo,\n"
                    + "	p.produtoqtdestoque estoque,\n"
                    + "	p.produtolucroporcento margem,\n"
                    + "	p.produtovalorcompra custosemimposto,\n"
                    + "	p.produtovalorfinal custocomimposto,\n"
                    + "	p.produtovalorvenda precovenda,\n"
                    + "	p.produtostatus ativo,\n"
                    + "	p.produtoncm ncm,\n"
                    + "	p.produtocest cest,\n"
                    + "	p.produtopiscst pis_cst,\n"
                    + "	p.produtoimcscst icms_cst,\n"
                    + "	p.produtoicms icms_aliquota,\n"
                    + "	p.produtoicmsredbcalc icms_reduzido\n"
                    + "from\n"
                    + "	produtos p\n"
                    + "order by\n"
                    + "	1"
            )) {
                Map<Integer, ProdutoBalancaVO> balanca = new ProdutoBalancaDAO().getProdutosBalanca();
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setDataAlteracao(rst.getDate("dataalteracao"));

                    ProdutoBalancaVO bal = balanca.get(Utils.stringToInt(rst.getString("ean"), -2));
                    if (bal != null) {
                        imp.setEan(bal.getCodigo() + "");
                        imp.setQtdEmbalagem(1);
                        imp.seteBalanca(true);
                        imp.setValidade(imp.getValidade());
                        switch (bal.getPesavel()) {
                            case "U":
                                imp.setTipoEmbalagem("UN");
                                break;
                            default:
                                imp.setTipoEmbalagem("KG");
                                break;
                        }
                    } else {
                        imp.setEan(rst.getString("ean"));
                        imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                        imp.seteBalanca(rst.getBoolean("pesavel"));
                        imp.setValidade(rst.getInt("validade"));
                        imp.setTipoEmbalagem(rst.getString("unidade"));
                    }

                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoGondola(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setCodMercadologico1(rst.getString("mercadologico1"));
                    imp.setCodMercadologico2(rst.getString("mercadologico1"));
                    imp.setCodMercadologico3(rst.getString("mercadologico1"));
                    imp.setIdFamiliaProduto(rst.getString("id_familia"));
                    imp.setEstoqueMaximo(rst.getDouble("estoquemaximo"));
                    imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    imp.setCustoComImposto(imp.getCustoSemImposto());
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setSituacaoCadastro(rst.getBoolean("ativo") ? 1 : 0);
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstCredito(rst.getString("pis_cst"));
                    imp.setPiscofinsCstDebito(rst.getString("pis_cst"));
                    imp.setIcmsCst(rst.getString("icms_cst"));
                    imp.setIcmsAliq(rst.getDouble("icms_aliquota"));
                    imp.setIcmsReducao(rst.getDouble("icms_reduzido"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "	SELECT\n"
                    + "	produtofornecprodutoid produtoid,\n"
                    + "	produtofornecfornecedorid fornecedorid,\n"
                    + "	1 as qtdembalagem\n"
                    + " FROM \n"
                    + "	produtosfornecedor"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setIdFornecedor(rst.getString("fornecedorid"));
                    imp.setIdProduto(rst.getString("produtoid"));
                    imp.setCodigoExterno(rst.getString("qtdembalagem"));
                    imp.setQtdEmbalagem(rst.getDouble("qtdembalagem"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    " SELECT distinct\n"
                    + " coalesce(nullif(produtoimcscst, ''), '00') cst,\n"
                    + " produtoicms aliquota,\n"
                    + " coalesce(produtoicmsredbcalc, '0') reducao\n"
                    + "FROM produtos"
            )) {
                while (rst.next()) {
                    String id = rst.getString("cst") + "-" + rst.getString("aliquota") + "-" + rst.getString("reducao");
                    result.add(new MapaTributoIMP(
                            id,
                            id,
                            rst.getInt("cst"),
                            rst.getDouble("aliquota"),
                            rst.getDouble("reducao")));
                }
            }
        }
        return result;
    }

    public List<Estabelecimento> getLojas() {
        return Arrays.asList(new Estabelecimento("1", "LOJA"));
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery("select\n"
                    + "	categoriaid merc1,\n"
                    + "	categoriacodigo descmerc1,\n"
                    + "	categoriaid merc2,\n"
                    + "	categoriacodigo descmerc2,\n"
                    + "	categoriaid merc3,\n"
                    + "	categoriacodigo descmerc3\n"
                    + "from\n"
                    + "	categorias")) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setMerc1ID(rst.getString("merc1"));
                    imp.setMerc1Descricao(rst.getString("descmerc1"));
                    imp.setMerc2ID(rst.getString("merc2"));
                    imp.setMerc2Descricao(rst.getString("descmerc2"));
                    imp.setMerc3ID(rst.getString("merc3"));
                    imp.setMerc3Descricao(rst.getString("descmerc3"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ClienteIMP> getClientesPreferenciais() throws Exception {

        List<ClienteIMP> result = new ArrayList<>();

        try (Statement st = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = st.executeQuery(
                    "select\n"
                    + "	c.clienteid id,\n"
                    + "	c.clientecpf cnpj,\n"
                    + "	c.clienterg ierg,\n"
                    + "	coalesce(nullif(trim(c.clienterazaosocial),''), c.clientenome) razaosocial,\n"
                    + "	c.clientenome fantasia,\n"
                    + "	c.clientestatus ativo,\n"
                    + "	c.clienteendereco endereco,\n"
                    + "	c.clientenumero numero,\n"
                    + "	c.clientecomplemento complemento,\n"
                    + "	c.clientebairro bairro,\n"
                    + "	c.clientecidade cidade,\n"
                    + "	c.clienteuf uf,\n"
                    + "	c.clientecep cep,\n"
                    + "	c.clientedatanascimento datanascimento,\n"
                    + "	c.clientedatacriacao datacadastro,\n"
                    + "	c.clientesexo sexo,\n"
                    + "	c.clientedataultimaalteracao dataalteracao,\n"
                    + "	c.clientelimitecredito limite,\n"
                    + "	c.clienteobs observacao,\n"
                    + "	c.clienteobsfinanceira,\n"
                    + "	c.clienteobsnotafiscal,\n"
                    + "	c.clientediavencimento diavencimento,\n"
                    + "	c.clientetelefone,\n"
                    + "	c.clientetelcomercial,\n"
                    + "	c.clienteemail,\n"
                    + "	c.clienteemailsecundario,\n"
                    + " c.clientecontato\n"        
                    + "from\n"
                    + "	clientes c\n"
                    + "order by\n"
                    + "	id"
            )) {
                while (rs.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rs.getString("id"));
                    imp.setCnpj(rs.getString("cnpj"));
                    imp.setInscricaoestadual(rs.getString("ierg"));
                    imp.setRazao(rs.getString("razaosocial"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setAtivo(rs.getBoolean("ativo") == false);
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipioIBGE(rs.getString("cidade"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));
                    imp.setDataNascimento(rs.getDate("datanascimento"));
                    imp.setDataCadastro(rs.getDate("datacadastro"));
                    imp.setSexo(rs.getString("sexo"));
                    imp.setValorLimite(rs.getDouble("limite"));
                    imp.setDiaVencimento(Utils.stringToInt(rs.getString("diavencimento")));
                    imp.setTelefone(rs.getString("clientetelefone"));
                    imp.addTelefone("FONE COMERC.", rs.getString("clientetelcomercial"));
                    imp.setEmail(rs.getString("clienteemail"));
                    imp.addEmail(rs.getString("clienteemailsecundario"), TipoContato.COMERCIAL);
                    
                    if (imp.getValorLimite() > 0) {
                        imp.setPermiteCreditoRotativo(true);
                    }
                    
                    String contato = rs.getString("clientecontato");
                    imp.setObservacao2(rs.getString("observacao"));
                    
                    if (contato != null && !contato.isEmpty()) {
                        imp.addContato(null, contato, null, null, null);
                        imp.setObservacao(rs.getString("observacao") + " - Contato: " + contato);
                    }
                    

                    result.add(imp);
                }
            }
        }

        return result;

    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();

        try (Statement st = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = st.executeQuery(
                    "select\n"
                    + "	c.contasreceberid id,\n"
                    + "	c.contasreceberdataentrada emissao,\n"
                    + "	c.contasrecebervencimento vencimento,\n"
                    + "	c.contasrecebernumdoc cupom,\n"
                    + "	c.contasrecebervalor valor,\n"
                    + "	c.contasreceberobs observacao,\n"
                    + "	c.contasreceberclienteid id_cliente,\n"
                    + "	c.contasreceberparcela parcela,\n"
                    + "	c.contasreceberjuros juros,\n"
                    + "	c.contasrecebermulta multa,\n"
                    + "	pags.valorpago,\n"
                    + "	pags.datapago\n"
                    + "from\n"
                    + "	contasreceber c\n"
                    + "	left join (\n"
                    + "		select \n"
                    + "			contasreceberformarecreceberid id_contareceber,\n"
                    + "			sum(contasreceberformarecvalorrecebido) valorpago,\n"
                    + "			max(contasreceberformarecdatahora) datapago\n"
                    + "		from\n"
                    + "			contasreceberformapagto\n"
                    + "		group by\n"
                    + "			1\n"
                    + "	) pags on\n"
                    + "		c.contasreceberid = pags.id_contareceber\n"
                    + "where \n"
                    + "	pags.valorpago is null and\n"
                    + "	c.contasreceberclienteid is not null\n"        
                    + "order by\n"
                    + "	1"
            )) {
                while (rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();

                    imp.setId(rs.getString("id"));
                    imp.setDataEmissao(rs.getDate("emissao"));
                    imp.setDataVencimento(rs.getDate("vencimento"));
                    imp.setNumeroCupom(rs.getString("cupom"));
                    imp.setValor(rs.getDouble("valor"));
                    imp.setObservacao(rs.getString("observacao"));
                    imp.setIdCliente(rs.getString("id_cliente"));
                    imp.setParcela(rs.getInt("parcela"));
                    imp.setJuros(rs.getDouble("juros"));
                    imp.setMulta(rs.getDouble("multa"));

                    if (rs.getDouble("valorpago") > 0) {
                        imp.addPagamento(
                                rs.getString("id"),
                                rs.getDouble("valorpago"),
                                0,
                                0,
                                rs.getDate("datapago"),
                                rs.getString("observacao")
                        );
                    }

                    result.add(imp);
                }
            }
        }

        return result;
    }

    public void setDataInicioVenda(Date date) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void setDataTerminoVenda(Date date) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
