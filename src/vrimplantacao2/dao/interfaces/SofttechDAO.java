package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import vrimplantacao.classe.ConexaoPostgres;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.vo.cadastro.mercadologico.MercadologicoNivelIMP;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Leandro
 */
public class SofttechDAO extends InterfaceDAO {
    
    public static final String NOME_SISTEMA = "Softtech";
    private static final Logger LOG = Logger.getLogger(SofttechDAO.class.getName());
    public String auxiliar = "";

    public SofttechDAO() {
        ConexaoPostgres.CHARSET = "WIN1252";
    }

    @Override
    public String getSistema() {
        return NOME_SISTEMA + auxiliar;
    }

    public List<Estabelecimento> getLojas() {
        return Arrays.asList(new Estabelecimento("1", "SOFTTECH 01"));
    }

    @Override
    public List<MercadologicoNivelIMP> getMercadologicoPorNivel() throws Exception {
        List<MercadologicoNivelIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select codigo, descricao from secoes where secaonivel2 is null order by codigo"
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP imp = new MercadologicoNivelIMP();                    
                    imp.setId(rst.getString("codigo"));
                    imp.setDescricao(rst.getString("descricao"));
                    
                    try (Statement stm2 = ConexaoPostgres.getConexao().createStatement()) {
                        try (ResultSet rst2 = stm2.executeQuery(
                                "select codigo, descricao from secoes where secaonivel2 = " + imp.getId() + " order by codigo"
                        )) {
                            while (rst2.next()) {
                                imp.addFilho(rst2.getString("codigo"), rst2.getString("descricao"));
                            }
                        }
                    }
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select codigo, descricao from public.produtosfamilia where codigo != 1"
            )) {
                while (rst.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("codigo"));
                    imp.setDescricao(rst.getString("descricao"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    private static class MercadologicoAux {
        String merc1;
        String merc2;
        public MercadologicoAux(String merc1, String merc2) {
            this.merc1 = merc1;
            this.merc2 = merc2;
        }
    }
    
    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            Map<Integer, MercadologicoAux> mercs = new HashMap<>();
            try (ResultSet rst = stm.executeQuery(
                    "select codigo, secaonivel2, descricao from secoes"
            )) {
                while (rst.next()) {
                    if (rst.getString("secaonivel2") == null) {
                        mercs.put(rst.getInt("codigo"), new MercadologicoAux(rst.getString("codigo"), null));
                    } else {
                        mercs.put(rst.getInt("codigo"), new MercadologicoAux(rst.getString("secaonivel2"), rst.getString("codigo")));
                    }
                }
            }
            
            LOG.fine("Mercadológicos mapeados: " + mercs.size());
            
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	codigo id,\n" +
                    "	codigosweda ean,\n" +
                    "	datacriado datacadastro,\n" +
                    "	dataalterado dataalteracao,\n" +
                    "	unidade,\n" +
                    "	produtobalanca e_balanca,\n" +
                    "	produto_pai,\n" +
                    "	descricao descricaocompleta,\n" +
                    "	descricaoreduzida descricaoreduzida,\n" +
                    "	secao id_mercadologico,\n" +
                    "	familia id_familia,\n" +
                    "	estoqueminimo,\n" +
                    "	estoquemaximo,\n" +
                    "	estoquetotal estoque,\n" +
                    "	perclucro margem,\n" +
                    "	precocusto custocomimposto,\n" +
                    "	precocusto custosemimposto,\n" +
                    "	preco,\n" +
                    "	desativado = 0 ativo,\n" +
                    "	ncm,\n" +
                    "	cest,\n" +
                    "	pis_cst_saida,\n" +
                    "	pis_cst_entrada,\n" +
                    "	natureza_rec_pis,\n" +
                    "	situacaotrib icms_cst,\n" +
                    "	tipotributacao,\n" +
                    "	tributacao icms_aliq\n" +
                    "from\n" +
                    "	produtos\n" +
                    "order by\n" +
                    "	id"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setDataAlteracao(rst.getDate("dataalteracao"));
                    imp.setEan(rst.getString("ean"));
                    imp.setTipoEmbalagem(rst.getString("unidade"));
                    imp.seteBalanca(rst.getBoolean("e_balanca"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoGondola(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    MercadologicoAux merc = mercs.get(rst.getInt("id_mercadologico"));
                    if (merc != null) {
                        imp.setCodMercadologico1(merc.merc1);
                        imp.setCodMercadologico2(merc.merc2);
                    }                    
                    imp.setIdFamiliaProduto(rst.getString("id_familia"));
                    imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
                    imp.setEstoqueMaximo(rst.getDouble("estoquemaximo"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    imp.setPrecovenda(rst.getDouble("preco"));
                    imp.setSituacaoCadastro(rst.getBoolean("ativo") ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getString("pis_cst_saida"));
                    imp.setPiscofinsCstCredito(rst.getString("pis_cst_entrada"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("natureza_rec_pis"));
                    imp.setIcmsCst(Utils.stringToInt(rst.getString("icms_cst")));
                    imp.setIcmsAliq(rst.getDouble("icms_aliq"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }
    
    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try(Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "	codigo id_produto,\n" +
                    "	codigoean ean\n" +
                    "from\n" +
                    "	produtosean\n" +
                    "order by\n" +
                    "	1")) {
                while(rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id_produto"));
                    imp.setEan(rs.getString("ean"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	f.codigo id,\n" +
                    "	f.razaosocial,\n" +
                    "	f.nomefantasia,\n" +
                    "	f.cpfcgc cnpj,\n" +
                    "	f.inscricaoidentidade ie,\n" +
                    "	(f.desativado = 0) ativo,\n" +
                    "	f.endereco,\n" +
                    "	f.bairro,\n" +
                    "	f.cidade,\n" +
                    "	f.estado,\n" +
                    "	f.cep,\n" +
                    "	coalesce(f.ddd, '') || f.telefone1 telefone1,\n" +
                    "	coalesce(f.ddd, '') || f.telefone2 telefone2,\n" +
                    "	f.observacao\n" +
                    "from\n" +
                    "	fornecedores f\n" +
                    "order by\n" +
                    "	id"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razaosocial"));
                    imp.setFantasia(rst.getString("nomefantasia"));
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    imp.setIe_rg(rst.getString("ie"));
                    imp.setAtivo(rst.getBoolean("ativo"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("estado"));
                    imp.setCep(rst.getString("cep"));
                    imp.setTel_principal(rst.getString("telefone1"));
                    imp.addTelefone("TELEFONE", rst.getString("telefone2"));
                    imp.setObservacao(rst.getString("observacao"));
                    
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
                    "select\n" +
                    "	pf.fornecedor id_fornecedor,\n" +
                    "	pf.codigo id_produto,\n" +
                    "	pf.dataultcompra dataalteracao,\n" +
                    "	pf.valorultcompra custotabelado,\n" +
                    "	coalesce(fc.codigo_barras, pf.codigo::varchar) codigoexterno\n" +
                    "from\n" +
                    "	produtosfornecedores pf\n" +
                    "	left join fornecedores_codbarras fc on\n" +
                    "		pf.codigo = fc.pro_id and\n" +
                    "		pf.fornecedor = fc.for_id\n" +
                    "order by\n" +
                    "	id_fornecedor, id_produto"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setIdFornecedor(rst.getString("id_fornecedor"));
                    imp.setIdProduto(rst.getString("id_produto"));
                    imp.setCodigoExterno(rst.getString("codigoexterno"));
                    imp.setCustoTabela(rst.getDouble("custotabelado"));
                    imp.setDataAlteracao(rst.getDate("dataalteracao"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	c.codigo id,\n" +
                    "	c.cpfcgc cnpj,\n" +
                    "	c.inscricaoidentidade ie,\n" +
                    "	c.nome razao,\n" +
                    "	c.nomeempresa fantasia,\n" +
                    "	c.desativado = 0 ativo,\n" +
                    "	c.bloqueado,\n" +
                    "	c.endereco,\n" +
                    "	c.bairro,\n" +
                    "	c.cidade,\n" +
                    "	c.estado,\n" +
                    "	c.cep,\n" +
                    "	c.datanascimento,\n" +
                    "	c.datacadastro,\n" +
                    "	c.empresa,\n" +
                    "	c.cargo,\n" +
                    "	c.renda salario,\n" +
                    "	c.limitecredito,\n" +
                    "	c.observacao,\n" +
                    "	c.dialimite diavencimento,\n" +
                    "	coalesce(c.ddd,'') || c.telefone1 telefone1,\n" +
                    "	coalesce(c.ddd,'') || c.telefone2 telefone2,\n" +
                    "	coalesce(c.ddd,'') || c.celcontato celular,\n" +
                    "	c.email,\n" +
                    "	c.enderecocob,\n" +
                    "	c.bairrocob,\n" +
                    "	c.cidadecob,\n" +
                    "	c.estadocob,\n" +
                    "	c.cepcob,\n" +
                    "	coalesce(c.dddcob,'') || c.telefonecob telefonecob\n" +
                    "from\n" +
                    "	clientes c\n" +
                    "order by\n" +
                    "	c.codigo"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    
                    imp.setId(rst.getString("id"));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setInscricaoestadual(rst.getString("ie"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setAtivo(rst.getBoolean("ativo"));
                    imp.setBloqueado(rst.getBoolean("bloqueado"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("estado"));
                    imp.setCep(rst.getString("cep"));
                    imp.setDataNascimento(rst.getDate("datanascimento"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setEmpresa(rst.getString("empresa"));
                    imp.setCargo(rst.getString("cargo"));
                    imp.setSalario(rst.getDouble("salario"));
                    imp.setValorLimite(rst.getDouble("limitecredito"));
                    imp.setObservacao(rst.getString("observacao"));
                    imp.setDiaVencimento(rst.getInt("diavencimento"));
                    imp.setTelefone(rst.getString("telefone1"));
                    imp.addTelefone("TELEFONE 2", rst.getString("telefone2"));
                    imp.setCelular(rst.getString("celular"));
                    imp.setEmail(rst.getString("email"));
                    imp.setCobrancaEndereco(rst.getString("enderecocob"));
                    imp.setCobrancaBairro(rst.getString("bairrocob"));
                    imp.setCobrancaMunicipio(rst.getString("cidadecob"));
                    imp.setCobrancaUf(rst.getString("estadocob"));
                    imp.setCobrancaCep(rst.getString("cepcob"));
                    imp.setCobrancaTelefone(rst.getString("telefonecob"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    ""
            )) {
                while (rst.next()) {
                    
                }
            }
        }
        
        return result;
    }

    @Override
    public List<ContaPagarIMP> getContasPagar() throws Exception {
        List<ContaPagarIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	cp.codigo id,\n" +
                    "	cp.fornecedor id_fornecedor,\n" +
                    "	cp.documento,\n" +
                    "	cp.data,\n" +
                    "	cp.valor,\n" +
                    "	cp.datapg is null finalizado,\n" +
                    "	cp.observacoes,\n" +
                    "	cp.origem,\n" +
                    "	cp.descricao,\n" +
                    "	cp.vencimento,\n" +
                    "	cp.valor valorpg\n" +
                    "from\n" +
                    "	contaspagar cp\n" +
                    "order by\n" +
                    "	id"
            )) {
                while (rst.next()) {
                    ContaPagarIMP imp = new ContaPagarIMP();
                    
                    imp.setId(rst.getString("id"));
                    imp.setIdFornecedor(rst.getString("id_fornecedor"));
                    imp.setNumeroDocumento(rst.getString("documento"));
                    imp.setDataEmissao(rst.getDate("data"));
                    imp.setDataEntrada(rst.getDate("data"));
                    imp.setValor(rst.getDouble("valor"));
                    String obs = "";
                    if (rst.getString("descricao") != null) {
                        obs += "DESCR: " + rst.getString("descricao");
                    }
                    if (rst.getString("origem") != null) {
                        obs += "ORIGEM: " + rst.getString("origem");
                    }
                    if (rst.getString("observacoes") != null) {
                        obs += "OBS: " + rst.getString("observacoes");
                    }
                    imp.setObservacao(obs);
                    imp.addVencimento(rst.getDate("vencimento"), rst.getDouble("valorpg"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        Set<OpcaoProduto> opt = new HashSet<>();
        
        opt.addAll(Arrays.asList(
                OpcaoProduto.MERCADOLOGICO_POR_NIVEL,
                OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                OpcaoProduto.MERCADOLOGICO_PRODUTO
        ));
        opt.addAll(OpcaoProduto.getFamilia());
        opt.addAll(Arrays.asList(
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.ATIVO,
                OpcaoProduto.DATA_CADASTRO,
                OpcaoProduto.DATA_ALTERACAO,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.VALIDADE,
                OpcaoProduto.PRECO,
                OpcaoProduto.CUSTO,
                OpcaoProduto.CUSTO_COM_IMPOSTO,
                OpcaoProduto.CUSTO_SEM_IMPOSTO,
                OpcaoProduto.ESTOQUE
        ));
        opt.addAll(OpcaoProduto.getTributos());
        
        return opt;
    }
    
}
