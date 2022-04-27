package vrimplantacao2_5.dao.sistema;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
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
import vrimplantacao2_5.dao.conexao.ConexaoSqlServer;

/**
 *
 * @author Guilherme
 */
public class ETradeDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "ETRADE";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                new OpcaoProduto[]{
                    OpcaoProduto.MERCADOLOGICO,
                    OpcaoProduto.MERCADOLOGICO_PRODUTO,
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
                    OpcaoProduto.DESC_COMPLETA,
                    OpcaoProduto.DESC_GONDOLA,
                    OpcaoProduto.DESC_REDUZIDA,
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
                    OpcaoProduto.MARGEM,
                    OpcaoProduto.VOLUME_QTD,
                    OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                    OpcaoProduto.IMPORTAR_MANTER_BALANCA
                }
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
                OpcaoCliente.RECEBER_CREDITOROTATIVO));
    }

    @Override
    public Set<OpcaoFornecedor> getOpcoesDisponiveisFornecedor() {
        return new HashSet<>(Arrays.asList(
                OpcaoFornecedor.ENDERECO,
                OpcaoFornecedor.DADOS,
                OpcaoFornecedor.CONTATOS,
                OpcaoFornecedor.SITUACAO_CADASTRO,
                OpcaoFornecedor.PRODUTO_FORNECEDOR,
                OpcaoFornecedor.PAGAR_FORNECEDOR));
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	codigo,\n"
                    + "	nome \n"
                    + "from \n"
                    + "	ClasseImposto \n"
                    + "where \n"
                    + "	ide in (select distinct pr.ClasseImposto__Ide from produto pr)\n"
                    + "order by \n"
                    + "	nome")) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(rs.getString("codigo"), rs.getString("nome")));
                }
            }
        }

        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    ""
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setMerc1ID(rst.getString(""));
                    imp.setMerc1Descricao(rst.getString(""));
                    imp.setMerc2ID(rst.getString(""));
                    imp.setMerc2Descricao(rst.getString(""));
                    imp.setMerc3ID(rst.getString(""));
                    imp.setMerc3Descricao(rst.getString(""));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	codigo,\n"
                    + "	nome\n"
                    + "from \n"
                    + "	Familias")) {
                while (rst.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("codigo"));
                    imp.setDescricao(rst.getString("nome"));
                    
                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        Map<Integer, vrimplantacao2.vo.cadastro.ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().getProdutosBalanca();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n" +
                    "	p.codigo,\n" +
                    "	p.nome,\n" +
                    "	p.codigo_ean ean,\n" +
                    "	p.Codigo_Fabricante1 id_balanca,\n" +
                    "	un.unidade,\n" +
                    "	p.Exportar_Balanca e_balanca,\n" +
                    "	p.Validade_Dias validade,\n" +
                    "	p.familia,\n" +
                    "	p.ncm,\n" +
                    "	ce.Codigo cest,\n" +
                    "	p.fracionado,\n" +
                    "	p.inativo,\n" +
                    "	p.Bloqueado,\n" +
                    "	p.BloqueadoParaVenda,\n" +
                    "	ea.Qtde estoque,\n" +
                    "	ea.Estoque_Ideal,\n" +
                    "	ea.Estoque_Minimo,\n" +
                    "	p.Custo1 custocomimposto,\n" +
                    "	p.preco1 precovenda,\n" +
                    "	pp.preco,\n" +
                    "	pp.margem,\n" +
                    "	pp.CustoBase,\n" +
                    "	pp.CustoOperacional,\n" +
                    "	p.data_cadastro,\n" +
                    "	p.margem margem_pro,\n" +
                    "	p.Peso_Liquido,\n" +
                    "	p.Peso_Bruto,\n" +
                    "	p.ClasseImposto__Ide id_icms\n" +
                    "FROM \n" +
                    "	produto p\n" +
                    "left join cest ce on p.cest = ce.ide\n" +
                    "left join ProdutoPreco pp on p.codigo = pp.Produto__Codigo\n" +
                    "left join unidades un on p.Unidade_Venda__Ide = un.Ide\n" +
                    "left join Estoque_Atual ea on p.codigo = ea.Produto\n" +
                    "where \n" +
                    "	pp.id = (select max(id) from produtopreco where p.codigo = Produto__Codigo) and \n" +
                    "	coalesce(ea.filial, 1) = " + getLojaOrigem()
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("codigo"));
                    imp.setDescricaoCompleta(rst.getString("nome"));
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setValidade(rst.getInt("validade"));

                    imp.setEan(rst.getString("ean"));

                    String eanBalanca = rst.getString("id_balanca"),
                            eanNormal = imp.getEan();

                    if (eanBalanca != null && 
                            !eanBalanca.isEmpty() && 
                                eanBalanca.length() < 7 && 
                                    eanNormal.trim().isEmpty()) {
                        
                        ProdutoBalancaVO balanca = produtosBalanca.get(Integer.valueOf(eanBalanca));
                        
                        imp.setEan(eanBalanca);
                        
                        if (balanca != null) {
                            imp.setEan(String.valueOf(balanca.getCodigo()));
                            imp.setValidade(balanca.getValidade() > 1 ? balanca.getValidade() : 0);
                            imp.setTipoEmbalagem("P".equals(balanca.getPesavel()) ? "KG" : "UN");
                        }
                        
                        imp.seteBalanca(true);

                    }

                    imp.setIdFamiliaProduto(rst.getString("familia"));
                    imp.setTipoEmbalagem(rst.getString("unidade"));
                    imp.setDataCadastro(rst.getDate("data_cadastro"));
                    imp.setSituacaoCadastro(rst.getInt("inativo") == 0 ? 1 : 0);

                    /*imp.setCodMercadologico1(rst.getString("merc1"));
                    imp.setCodMercadologico2(rst.getString("merc2"));
                    imp.setCodMercadologico3(rst.getString("merc3"));*/

                    imp.setPesoBruto(rst.getDouble("peso_bruto"));
                    imp.setPesoLiquido(rst.getDouble("peso_liquido"));

                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setPrecovenda(rst.getDouble("preco"));
                    imp.setMargem(rst.getDouble("margem"));

                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setEstoqueMinimo(rst.getDouble("estoque_minimo"));
                    
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    
                    //imp.setPiscofinsCstDebito(rst.getString("pisdebito"));
                    
                    imp.setIcmsConsumidorId(rst.getString("id_icms"));
                    imp.setIcmsDebitoId(imp.getIcmsConsumidorId());
                    imp.setIcmsDebitoForaEstadoId(imp.getIcmsConsumidorId());
                    imp.setIcmsDebitoForaEstadoNfId(imp.getIcmsConsumidorId());
                    
                    imp.setIcmsCreditoId(imp.getIcmsDebitoId());
                    imp.setIcmsCreditoForaEstadoId(imp.getIcmsDebitoId());
                    
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
            try (ResultSet rst = stm.executeQuery(
                    "select \n" +
                    "    p.Codigo,\n" +
                    "    p.Nome,\n" +
                    "    a.CodigoAdicional ean\n" +
                    "from \n" +
                    "    ProdutoCodigoAdicional a \n" +
                    "join Produto p on a.Produto__Ide = p.Ide\n" +
                    "union all \n" +
                    "select \n" +
                    "    codigo,\n" +
                    "    nome,\n" +
                    "    Codigo_Fabricante1 ean\n" +
                    "from \n" +
                    "    produto \n" +
                    "where \n" +
                    "    Codigo_Fabricante1 != '' and \n" +
                    "    len(Codigo_Fabricante1) > 6")) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("Codigo"));
                    imp.setEan(rst.getString("ean"));
                    
                    result.add(imp);
                }
            }
        }
            
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n" +
                    "	c.codigo,\n" +
                    "	c.nome,\n" +
                    "	c.cnpj,\n" +
                    "	c.inscricao ie,\n" +
                    "	c.InscricaoEstadualPf ief,\n" +
                    "	c.InscricaoMunicipal,\n" +
                    "	c.fantasia,\n" +
                    "	c.endereco,\n" +
                    "	c.cidade,\n" +
                    "	c.numero,\n" +
                    "	c.complemento,\n" +
                    "	c.bairro,\n" +
                    "	c.cep,\n" +
                    "	c.uf,\n" +
                    "	c.obs,\n" +
                    "	c.ObsFinanceiro,\n" +
                    "	c.fone1,\n" +
                    "	c.fone2,\n" +
                    "	c.fax,\n" +
                    "	c.Email,\n" +
                    "	c.EmailNFe,\n" +
                    "	c.Nascimento,\n" +
                    "	c.inativo,\n" +
                    "	c.bloqueado,\n" +
                    "	c.DataCadastro,\n" +
                    "	c.Limite_Credito,\n" +
                    "	c.Contato\n" +
                    "from \n" +
                    "	Cli_For c\n" +
                    "where	\n" +
                    "	c.tipo = 'F'"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("codigo"));
                    imp.setRazao(rst.getString("nome"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    
                    imp.setIe_rg(rst.getString("ie"));
                    
                    if (imp.getIe_rg() == null) {
                        imp.setIe_rg(rst.getString("ief"));
                    }
                    
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setAtivo(rst.getInt("inativo") == 0);
                    imp.setCep(rst.getString("cep"));
                    imp.setUf(rst.getString("uf"));
                    imp.setTel_principal(rst.getString("fone1"));
                    imp.setDatacadastro(rst.getDate("datacadastro"));

                    if ((rst.getString("email") != null)
                            && (!rst.getString("email").trim().isEmpty())) {
                        imp.addEmail("EMAIL", rst.getString("email").toLowerCase(), TipoContato.NFE);
                    }
                    if ((rst.getString("fone2") != null)
                            && (!rst.getString("fone2").trim().isEmpty())) {
                        imp.addTelefone("FONE2", rst.getString("fone2"));
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

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n" +
                    "	p.codigo idproduto,\n" +
                    "	cf.codigo idfornecedor,\n" +
                    "	pf.Codigo_Fornecedor externo,\n" +
                    "	pf.Caixa_Com qtde\n" +
                    "from\n" +
                    "	 Produto_Fornecedores pf \n" +
                    "join produto p on pf.Produto__Ide = p.Ide\n" +
                    "join Cli_For cf on pf.Cli_For__Ide = cf.Ide"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rst.getString("idproduto"));
                    imp.setIdFornecedor(rst.getString("idfornecedor"));
                    imp.setCodigoExterno(rst.getString("externo"));
                    imp.setQtdEmbalagem(rst.getDouble("qtde"));

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
            try (ResultSet rst = stm.executeQuery(
                    "select \n" +
                    "	c.codigo,\n" +
                    "	c.nome,\n" +
                    "	c.cnpj,\n" +
                    "	c.inscricao,\n" +
                    "	c.InscricaoEstadualPf,\n" +
                    "	c.InscricaoMunicipal,\n" +
                    "	c.fantasia,\n" +
                    "	c.endereco,\n" +
                    "	c.cidade,\n" +
                    "	c.numero,\n" +
                    "	c.complemento,\n" +
                    "	c.bairro,\n" +
                    "	c.cep,\n" +
                    "	c.uf,\n" +
                    "	c.obs,\n" +
                    "	c.ObsFinanceiro,\n" +
                    "	c.fone1,\n" +
                    "	c.fone2,\n" +
                    "	c.fax,\n" +
                    "	c.Email,\n" +
                    "	c.EmailNFe,\n" +
                    "	c.Nascimento,\n" +
                    "	c.inativo,\n" +
                    "	c.bloqueado,\n" +
                    "	c.DataCadastro,\n" +
                    "	c.Limite_Credito,\n" +
                    "	c.Sexo\n" +
                    "from \n" +
                    "	Cli_For c\n" +
                    "where	\n" +
                    "	c.tipo = 'C'"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rst.getString("codigo"));
                    imp.setRazao(rst.getString("nome"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj(rst.getString("cnpj"));
                    
                    imp.setInscricaoestadual(rst.getString("inscricao"));
                    
                    if (imp.getInscricaoestadual() == null) {
                        imp.setInscricaoestadual(rst.getString("inscricaoestadualpf"));
                    }
                    
                    imp.setAtivo(rst.getInt("inativo") == 0);
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setDataNascimento(rst.getDate("nascimento"));
                    imp.setTelefone(rst.getString("fone1"));
                    imp.setCelular(rst.getString("fone2"));
                    imp.setFax(rst.getString("fax"));
                    imp.setEmail(rst.getString("email"));
                    imp.setValorLimite(rst.getDouble("limite_credito"));

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
            try (ResultSet rst = stm.executeQuery(
                    "select \n" +
                    "	id,\n" +
                    "	emissao,\n" +
                    "	vencimento,\n" +
                    "	Caixa__Codigo ecf,\n" +
                    "	Cliente__Codigo idcliente,\n" +
                    "	NFCe,\n" +
                    "	Valor_Final,\n" +
                    "	Parcela_Numero parcela\n" +
                    "from \n" +
                    "	Financeiro_Conta	\n" +
                    "where \n" +
                    "	Situacao = 'A' and \n" +
                    "	Pagar_Receber = 'R' and \n" +
                    "	Filial__Codigo = " + getLojaOrigem()
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();

                    imp.setId(rst.getString("id"));
                    imp.setDataEmissao(rst.getDate("emissao"));
                    imp.setNumeroCupom(rst.getString("nfce"));
                    imp.setEcf(rst.getString("ecf"));
                    imp.setValor(rst.getDouble("valor_final"));
                    imp.setIdCliente(rst.getString("idcliente"));
                    imp.setDataVencimento(rst.getDate("datavencimento"));
                    imp.setParcela(rst.getInt("parcela"));

                    result.add(imp);
                }
            }
        }
        return result;
    }
}
