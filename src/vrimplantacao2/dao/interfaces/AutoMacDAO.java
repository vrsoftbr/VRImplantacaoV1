package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author guilhermegomes
 */
public class AutoMacDAO 
        extends InterfaceDAO 
        implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "AutoMac";
    }

    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	1 as \"id\",\n"
                    + "	nome_fantasia\n"
                    + "from \n"
                    + "	paramet")) {
                while (rs.next()) {
                    result.add(new Estabelecimento(rs.getString("id"), rs.getString("nome_fantasia")));
                }
            }
        }

        return result;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	codigo,\n"
                    + "	descricao,\n"
                    + "	taxa\n"
                    + "from \n"
                    + "	taxaicms")) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(rs.getString("codigo"),
                            rs.getString("descricao"),
                            0,
                            rs.getDouble("taxa"),
                            0));
                }
            }
        }

        return result;
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                new OpcaoProduto[]{
                    OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                    OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                    OpcaoProduto.MERCADOLOGICO_PRODUTO,
                    OpcaoProduto.MERCADOLOGICO_POR_NIVEL,
                    OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                    OpcaoProduto.FAMILIA_PRODUTO,
                    OpcaoProduto.FAMILIA,
                    OpcaoProduto.MANTER_DESCRICAO_PRODUTO,
                    OpcaoProduto.PRODUTOS,
                    OpcaoProduto.PRODUTOS_BALANCA,
                    OpcaoProduto.EAN,
                    OpcaoProduto.EAN_EM_BRANCO,
                    OpcaoProduto.DATA_CADASTRO,
                    OpcaoProduto.TIPO_EMBALAGEM_EAN,
                    OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                    OpcaoProduto.QTD_EMBALAGEM_COTACAO,
                    OpcaoProduto.QTD_EMBALAGEM_EAN,
                    OpcaoProduto.PESAVEL,
                    OpcaoProduto.VALIDADE,
                    OpcaoProduto.DESC_COMPLETA,
                    OpcaoProduto.DESC_GONDOLA,
                    OpcaoProduto.DESC_REDUZIDA,
                    OpcaoProduto.ESTOQUE_MAXIMO,
                    OpcaoProduto.ESTOQUE_MINIMO,
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
                    OpcaoProduto.PAUTA_FISCAL,
                    OpcaoProduto.PAUTA_FISCAL_PRODUTO,
                    OpcaoProduto.MARGEM,
                    OpcaoProduto.OFERTA,
                    OpcaoProduto.MAPA_TRIBUTACAO,
                    OpcaoProduto.EXCECAO,
                    OpcaoProduto.TIPO_PRODUTO
                }
        ));
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	p.codigo,\n" +
                    "	p.id,\n" +
                    "	p.\"DATA\" cadastro,\n" +
                    "	p.cod_barra ean,\n" +
                    "	p.grupo,\n" +
                    "	p.setor,\n" +
                    "	p.balanca,\n" +
                    "	p.validade,\n" +
                    "	p.tipo_unid,\n" +
                    "	p.nome descricaocompleta,\n" +
                    "	p.unidade,\n" +
                    "	p.unid_compra,\n" +
                    "	p.custo,\n" +
                    "	p.preco_compra,\n" +
                    "	p.margem,\n" +
                    "	p.margem2,\n" +
                    "	p.preco_venda,\n" +
                    "	p.preco_venda2,\n" +
                    "	p.est_minimo estoqueminimo,\n" +
                    "	p.pos_estoque estoque,\n" +
                    "	p.txa_icms id_icmsdebito,\n" +
                    "	p.sit_trib_icm,\n" +
                    "	fi.cod_fis ncm,\n" +
                    "	fi.cst_pis,\n" +
                    "	fi.cest\n" +
                    "from \n" +
                    "	produtos p\n" +
                    "left join clafsipi fi on p.clas_fis_ipi = fi.codigo")) {
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("codigo"));
                    imp.setDataCadastro(rs.getDate("cadastro"));
                    imp.setEan(rs.getString("ean"));
                    imp.seteBalanca(rs.getString("balanca") != null && 
                                        !rs.getString("balanca").isEmpty() &&
                                            rs.getString("balanca").equals("T"));
                    
                    long ean = Utils.stringToLong(imp.getEan(), -2);
                    
                    if(imp.isBalanca() &&
                                ean != 0 &&
                                    ean <= 999999) {
                        imp.setEan(String.valueOf(ean));
                    }
                    
                    imp.setValidade(rs.getInt("validade"));
                    imp.setDescricaoCompleta(rs.getString("descricaocompleta"));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    imp.setTipoEmbalagem(rs.getString("unidade"));
                    imp.setCustoComImposto(rs.getDouble("custo"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setMargem(rs.getDouble("margem"));
                    imp.setPrecovenda(rs.getDouble("preco_venda"));
                    imp.setEstoque(rs.getDouble("estoque"));
                    imp.setEstoqueMinimo(rs.getDouble("estoqueminimo"));
                    imp.setNcm(rs.getString("ncm"));
                    imp.setCest(rs.getString("cest"));
                    
                    String[] pis;
                    
                    if(rs.getString("cst_pis") != null && !rs.getString("cst_pis").trim().isEmpty()) {
                       pis = rs.getString("cst_pis").split(" ");
                       imp.setPiscofinsCstDebito(pis[0]);
                    }
                    
                    imp.setIcmsDebitoId(rs.getString("id_icmsdebito"));
                    imp.setIcmsConsumidorId(imp.getIcmsDebitoId());
                    imp.setIcmsDebitoForaEstadoId(imp.getIcmsDebitoId());
                    imp.setIcmsDebitoForaEstadoNfId(imp.getIcmsDebitoId());
                    imp.setIcmsCreditoId(imp.getIcmsDebitoId());
                    imp.setIcmsCreditoForaEstadoId(imp.getIcmsDebitoId());
                    
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
                    "	f.codigo,\n" +
                    "	f.razao_social,\n" +
                    "	f.nome_fantasia,\n" +
                    "	f.cnpjcpf,\n" +
                    "	f.endereco,\n" +
                    "	f.end_numero,\n" +
                    "	f.complemento,\n" +
                    "	f.bairro,\n" +
                    "	f.cidade,\n" +
                    "	c.NOME municipio,\n" +
                    "	c.estado,\n" +
                    "	f.cep,\n" +
                    "	f.inscest ie,\n" +
                    "	f.fone,\n" +
                    "	f.celular,\n" +
                    "	f.fax,\n" +
                    "	f.nome_contato,\n" +
                    "	f.fone_contato,\n" +
                    "	f.email,\n" +
                    "	f.obs\n" +
                    "FROM 	\n" +
                    "	FORNECED f\n" +
                    "LEFT JOIN CIDADES c ON f.CIDADE = c.codigo ")) {
                while (rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("codigo"));
                    imp.setRazao(rs.getString("razao_social"));
                    imp.setFantasia(rs.getString("nome_fantasia"));
                    imp.setCnpj_cpf(rs.getString("cnpjcpf"));
                    imp.setIe_rg(rs.getString("ie"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("end_numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("municipio"));
                    imp.setUf(rs.getString("estado"));
                    imp.setCep(rs.getString("cep"));
                    imp.setTel_principal(rs.getString("fone"));
                    
                    String foneContato = rs.getString("fone_contato"),
                            nomeContato = rs.getString("nome_contato");
                    
                    if (foneContato != null && !foneContato.trim().isEmpty()) {
                        imp.addContato("1", 
                                nomeContato, 
                                foneContato, 
                                null, 
                                TipoContato.NFE, 
                                null);
                    }
                    
                    String celular = rs.getString("celular");
                    
                    if (celular != null && !celular.trim().isEmpty()) {
                        imp.addContato("2", 
                                "CEL. CONTATO", 
                                null, 
                                celular, 
                                TipoContato.NFE, 
                                null);
                    }
                    
                    String email = rs.getString("email");
                    
                    if (email != null && !email.trim().isEmpty()) {
                        imp.addContato("3", 
                                "EMAIL", 
                                null, 
                                null, 
                                TipoContato.NFE, 
                                email);
                    }
                    
                    imp.setObservacao(rs.getString("obs"));
                    
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
                    "SELECT \n" +
                    "	pf.codigo,\n" +
                    "	pf.produto,\n" +
                    "	pf.nome,\n" +
                    "	pf.fornecedor,\n" +
                    "	pf.codigo_barras\n" +
                    "FROM \n" +
                    "	produtos_fornecedor pf \n" +
                    "ORDER BY \n" +
                    "	pf.fornecedor, pf.produto")) {
                while (rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setIdProduto(rs.getString("produto"));
                    imp.setIdFornecedor(rs.getString("fornecedor"));
                    imp.setCodigoExterno(rs.getString("codigo_barras"));
                    
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
                    "	c.codigo, \n" +
                    "	c.nome,\n" +
                    "	c.apelido,\n" +
                    "	c.rg,\n" +
                    "	c.cnpjcpf,\n" +
                    "	c.inscest ie,\n" +
                    "	c.endereco,\n" +
                    "	c.end_numero,\n" +
                    "	c.bairro,\n" +
                    "	ci.nome municipio,\n" +
                    "	ci.estado,\n" +
                    "	c.cidade,\n" +
                    "	c.complemento,\n" +
                    "	c.cep,\n" +
                    "	c.estado_civil,\n" +
                    "	c.dt_nasc,\n" +
                    "	c.sexo,\n" +
                    "	c.fone,\n" +
                    "	c.fax,\n" +
                    "	c.celular,\n" +
                    "	c.email,\n" +
                    "	c.dt_cadastro,\n" +
                    "	c.limite_cred,\n" +
                    "	c.obs,\n" +
                    "	c.ativo,\n" +
                    "	c.bloqueado\n" +
                    "FROM \n" +
                    "	clientes c\n" +
                    "LEFT JOIN cidades ci ON c.cidade = ci.codigo")) {
                while (rs.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    
                    imp.setId(rs.getString("codigo"));
                    imp.setRazao(rs.getString("nome"));
                    imp.setFantasia(rs.getString("apelido"));
                    imp.setCnpj(rs.getString("cnpjcpf"));
                    imp.setInscricaoestadual(rs.getString("ie"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("end_numero"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("municipio"));
                    imp.setUf(rs.getString("estado"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setCep(rs.getString("cep"));
                    imp.setTelefone(rs.getString("fone"));
                    imp.setCelular(rs.getString("celular"));
                    imp.setDataCadastro(rs.getDate("dt_cadastro"));
                    imp.setValorLimite(rs.getDouble("limite_cred"));
                    
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
                    "	seq,\n" +
                    "	referente cupom, \n" +
                    "	cliente,\n" +
                    "	documento,\n" +
                    "	dt_entrada,\n" +
                    "	dt_venc,\n" +
                    "	valor,\n" +
                    "   pendente\n" +        
                    "FROM\n" +
                    "	contarcb\n" +
                    "ORDER BY \n" +
                    "	dt_venc")) {
                while (rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    
                    imp.setId(rs.getString("seq"));
                    imp.setIdCliente(rs.getString("cliente"));
                    imp.setNumeroCupom(rs.getString("cupom"));
                    imp.setDataEmissao(rs.getDate("dt_entrada"));
                    imp.setDataVencimento(rs.getDate("dt_venc"));
                    imp.setValor(rs.getDouble("pendente"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }
}
