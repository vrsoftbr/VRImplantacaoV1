package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author guilhermegomes
 */
public class SGMasterDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "SG Master";
    }
    
    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.FAMILIA,
                OpcaoProduto.FAMILIA_PRODUTO,
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.QTD_EMBALAGEM_COTACAO,
                OpcaoProduto.QTD_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.VALIDADE,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.PESO_BRUTO,
                OpcaoProduto.PESO_LIQUIDO,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.ESTOQUE_MINIMO,
                OpcaoProduto.ESTOQUE_MAXIMO,
                OpcaoProduto.TROCA,
                OpcaoProduto.MARGEM,
                OpcaoProduto.CUSTO,
                OpcaoProduto.CUSTO_COM_IMPOSTO,
                OpcaoProduto.CUSTO_SEM_IMPOSTO,
                OpcaoProduto.PRECO,
                OpcaoProduto.ATIVO,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.NATUREZA_RECEITA,
                OpcaoProduto.ICMS,
                OpcaoProduto.ICMS_CONSUMIDOR,
                OpcaoProduto.ATACADO,
                OpcaoProduto.PAUTA_FISCAL,
                OpcaoProduto.PAUTA_FISCAL_PRODUTO,
                OpcaoProduto.SUGESTAO_COTACAO,
                OpcaoProduto.COMPRADOR,
                OpcaoProduto.COMPRADOR_PRODUTO,
                OpcaoProduto.OFERTA,
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                OpcaoProduto.NCM,
                OpcaoProduto.CEST,
                OpcaoProduto.RECEITA_BALANCA,
                OpcaoProduto.MAPA_TRIBUTACAO
        ));
    }
    
    public List<Estabelecimento> getLojaCliente() throws SQLException {
        return Arrays.asList(new Estabelecimento("1", "LOJA 01"));
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT DISTINCT \n"
                    + "	CSOSN cst,\n"
                    + "	COALESCE(ALIQUOTAICMSECF, 0) icms,\n"
                    + "	COALESCE(PERCREDUCAOBC, 0) reducao\n"
                    + "FROM TESTOQUE\n"
                    + "ORDER BY 1, 2, 3"
            )) {
                while (rs.next()) {
                    String id = rs.getString("cst") + "-" + rs.getString("icms") + "-" + rs.getString("reducao");
                    result.add(new MapaTributoIMP(
                            id,
                            id,
                            rs.getInt("cst"),
                            rs.getDouble("icms"),
                            rs.getDouble("reducao")
                        )
                    );
                }
            }
        }
        return result;
    }
    
    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "SELECT \n" +
                    "	pr.controle id,\n" +
                    "	pr.produto descricao,\n" +
                    "	pr.pesado,\n" +
                    "	pr.diasvalidadeproduto validade,\n" +
                    "	pr.status,\n" +
                    "	pr.TIPOBARRA,\n" +
                    "	pr.CODBARRAS ean,\n" +
                    "	pr.UNIDADE,\n" +
                    "	pr.PRECOCUSTO,\n" +
                    "	pr.PERCLUCRO,\n" +
                    "	pr.PRECOVENDA,\n" +
                    "	pr.DATAHORACADASTRO cadastro,\n" +
                    "	pr.QTDE estoque,\n" +
                    "	pr.qtdereal estoquereal,\n" +
                    "	pr.QTDEMINIMA,\n" +
                    "	pr.QTDEMAXIMA,\n" +
                    "	pr.ativo,\n" +
                    "	pr.ncm,\n" +
                    "	pr.CEST,\n" +
                    "	pr.CODTRIBUTACAOCOFINS cofins,\n" +
                    "	pr.CODTRIBUTACAOPIS pis,\n" +
                    "	pr.tributado,\n" +
                    "	pr.CSOSN cst,\n" +
                    "   COALESCE(pr.ALIQUOTAICMSECF, 0) icms, \n" +
                    "	COALESCE(PERCREDUCAOBC, 0) reducao \n" +
                    "FROM \n" +
                    "	testoque pr")) {
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().carregarProdutosBalanca();
                while(rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    ProdutoBalancaVO produtoBalanca;
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("id"));
                    imp.setDescricaoCompleta(rs.getString("descricao"));
                    imp.setDataCadastro(rs.getDate("cadastro"));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    imp.setTipoEmbalagem(rs.getString("UNIDADE"));
                    imp.setEan(rs.getString("ean"));
                    
                    String pesado = Utils.acertarTexto(rs.getString("pesado"));
                    
                    if (produtosBalanca.isEmpty()) {
                        if (pesado != null && "SIM".equals(pesado.trim())) {
                            imp.seteBalanca(true);
                        }                        
                    } else {
                        long codigoProduto;
                        
                        if (imp.getEan() != null && !imp.getEan().trim().isEmpty()) {

                            codigoProduto = Long.parseLong(imp.getEan());
                            if (codigoProduto <= Integer.MAX_VALUE) {
                                produtoBalanca = produtosBalanca.get((int) codigoProduto);
                            } else {
                                produtoBalanca = null;
                            }

                            if (produtoBalanca != null) {
                                imp.seteBalanca(true);
                                imp.setValidade(produtoBalanca.getValidade() > 1 ? produtoBalanca.getValidade() : rs.getInt("validade"));
                            } else {
                                imp.setValidade(rs.getInt("validade"));
                                imp.seteBalanca(false);
                            }
                        } else {
                            imp.seteBalanca(false);
                            imp.setValidade(rs.getInt("validade"));
                        }

                    }                    
                    
                    imp.setSituacaoCadastro("SIM".equals(rs.getString("ativo")) ? 1 : 0);
                    imp.setEstoque(rs.getDouble("estoque"));
                    imp.setEstoqueMaximo(rs.getDouble("QTDEMAXIMA"));
                    imp.setEstoqueMinimo(rs.getDouble("QTDEMINIMA"));
                    imp.setPrecovenda(rs.getDouble("PRECOVENDA"));
                    imp.setMargem(rs.getDouble("PERCLUCRO"));
                    imp.setCustoComImposto(rs.getDouble("PRECOCUSTO"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setNcm(rs.getString("ncm"));
                    imp.setCest(rs.getString("cest"));
                    imp.setPiscofinsCstDebito(rs.getString("pis"));
                    
                    String idIcms = rs.getString("cst") + "-" + rs.getString("icms") + "-" + rs.getString("reducao");
                    
                    imp.setIcmsDebitoId(idIcms);
                    imp.setIcmsDebitoForaEstadoId(idIcms);
                    imp.setIcmsDebitoForaEstadoNfId(idIcms);
                    imp.setIcmsCreditoId(idIcms);
                    imp.setIcmsCreditoForaEstadoId(idIcms);
                    //imp.setIcmsConsumidorId(idIcms);
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos(OpcaoProduto opt) throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        if (opt == OpcaoProduto.ICMS_CONSUMIDOR) {
            try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "SELECT \n"
                        + "	pr.controle id,\n"
                        + "	pr.ALIQUOTAICMSECF icms\n"
                        + "FROM \n"
                        + "	testoque pr"
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("id"));
                        
                        imp.setIcmsDebitoId(null);
                        
                        imp.setIcmsCstSaida(0);
                        imp.setIcmsAliqSaida(rst.getDouble("icms"));
                        imp.setIcmsReducaoSaida(0);
                        imp.setIcmsCstConsumidor(0);
                        imp.setIcmsAliqConsumidor(rst.getDouble("icms"));
                        imp.setIcmsReducaoConsumidor(0);
                        
                        result.add(imp);
                        
                    }
                }
            }
            return result;
        }
        return null;
    }
    
    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "SELECT \n" +
                    "	f.controle id,\n" +
                    "	f.datahoracadastro cadastro,\n" +
                    "	f.razaosocial,\n" +
                    "	f.nomefantasia,\n" +
                    "	f.rg,\n" +
                    "	f.cpf,\n" +
                    "	f.cnpj,\n" +
                    "	f.ie,\n" +
                    "	f.im,\n" +
                    "	f.endereco,\n" +
                    "	f.bairro,\n" +
                    "	f.cidade,\n" +
                    "	f.uf,\n" +
                    "	f.cep,\n" +
                    "	f.complemento,\n" +
                    "	f.telefone,\n" +
                    "	f.celular,\n" +
                    "	f.email,\n" +
                    "	f.numero,\n" +
                    "	f.ativo\n" +
                    "FROM 	\n" +
                    "	TFORNECEDOr f")) {
                while(rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id"));
                    imp.setDatacadastro(rs.getDate("cadastro"));
                    imp.setRazao(rs.getString("razaosocial"));
                    imp.setFantasia(rs.getString("nomefantasia"));
                    imp.setCnpj_cpf(rs.getString("cnpj"));
                    imp.setIe_rg(rs.getString("ie"));
                    imp.setInsc_municipal(rs.getString("im"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setTel_principal(rs.getString("telefone"));
                    imp.setNumero(rs.getString("numero"));
                    
                    String celular = rs.getString("celular"), 
                            email = rs.getString("email");
                    
                    if(celular != null 
                               && !"".equals(celular)) {
                        imp.addContato("1", "CELULAR", null, celular, TipoContato.NFE, null);
                    }
                    
                    if(email != null 
                               && !"".equals(email)) {
                        imp.addContato("2", "EMAIL", null, null, TipoContato.NFE, email);
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
        
        try(Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "SELECT \n" +
                    "	controle id, \n" +
                    "	cliente razao,\n" +
                    "	FANTASIA,\n" +
                    "	rg,\n" +
                    "	cnpj,\n" +
                    "	cpf,\n" +
                    "	ie,\n" +
                    "	im,\n" +
                    "	endereco,\n" +
                    "	complemento,\n" +
                    "	numero,\n" +
                    "	bairro,\n" +
                    "	cidade,\n" +
                    "	uf,\n" +
                    "	cep,\n" +
                    "	sexo,\n" +
                    "	DATANASCIMENTO,\n" +
                    "	DATAHORACADASTRO cadastro,\n" +
                    "	telefone,\n" +
                    "	celular,\n" +
                    "	email,\n" +
                    "	ESTADOCIVIL,\n" +
                    "	ativo,\n" +
                    "	LIMITECREDITO,\n" +
                    "	OBS\n" +
                    "FROM \n" +
                    "	TCLIENTE")) {
                while(rs.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    
                    imp.setId(rs.getString("id"));
                    imp.setAtivo(rs.getString("ativo").equals("T"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("FANTASIA"));
                    imp.setCnpj(rs.getString("cnpj"));
                    imp.setInscricaoestadual(rs.getString("ie"));
                    imp.setTelefone(rs.getString("telefone"));
                    imp.setCelular(rs.getString("celular"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setMunicipio(rs.getString("CIDADE"));
                    imp.setUf(rs.getString("UF"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setCep(rs.getString("cep"));
                    imp.setDataCadastro(rs.getDate("cadastro"));
                    imp.setDataNascimento(rs.getDate("DATANASCIMENTO"));
                    imp.setEmail(rs.getString("email"));
                    imp.setInscricaoMunicipal(rs.getString("im"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }
}
