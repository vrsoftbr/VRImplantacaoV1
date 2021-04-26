package vrimplantacao.dao.interfaces;

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
import vrimplantacao.classe.ConexaoMySQL;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ChequeIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

public class FMDAO extends InterfaceDAO {
    
    public String lojaID = "";
    private static final Logger LOG = Logger.getLogger(FMDAO.class.getName());

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n" +
                    "	codigo, \n" +
                    "	secao\n" +
                    "from \n" +
                    "	fm.secoes\n" +
                    "where \n" +
                    "	secao <> ''\n" +
                    "	and char_length(secao) > 1\n" +
                    "order by \n" +
                    "	codigo"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setMerc1ID(rst.getString("codigo"));
                    imp.setMerc1Descricao(rst.getString("secao"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	distinct(familia) familia\n" +
                    "from\n" +
                    "	fm.mercadorias\n" +
                    "where \n" +
                    "	familia <> '' and\n" +
                    "    char_length(secao) > 4\n" +
                    "order by\n" +
                    "	familia"
            )) {
                while (rst.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("familia"));
                    imp.setDescricao(rst.getString("familia"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            Map<Integer, Double> estoque = new HashMap<>();
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	e.CodMercadoria id_produto,\n" +
                    "	coalesce(e.estoque, 0) estoque\n" +
                    "from\n" +
                    "	fm.estoque e    \n" +
                    "	join (select\n" +
                    "		max(es.Codigo) Codigo,\n" +
                    "		es.codMercadoria\n" +
                    "	from\n" +
                    "		fm.estoque es\n" +
                    "		join (select \n" +
                    "			CodMercadoria, \n" +
                    "			max(data) data\n" +
                    "		from \n" +
                    "			fm.estoque \n" +
                    "		group by \n" +
                    "			codMercadoria) ids on\n" +
                    "			es.codmercadoria = ids.codmercadoria and\n" +
                    "			es.data = ids.data\n" +
                    "	group by\n" +
                    "		es.codMercadoria) est on\n" +
                    "		e.Codigo = est.Codigo"
            )) {
                while (rst.next()) {
                    estoque.put(rst.getInt("id_produto"), rst.getDouble("estoque"));
                }
            }
            
            LOG.fine("Número de estoque encontrado: " + estoque.size());
            
            Map<String, String> mercadologicos = new HashMap<>();
            for (MercadologicoIMP mp: getMercadologicos()) {
                mercadologicos.put(mp.getMerc1Descricao(), mp.getMerc1ID());
            }
            
            LOG.fine("Número de mercadológicos mapeados: " + mercadologicos.size());
            
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	 p.Codigo id,\n" +
                    "    p.DataCadastro datacadastro,\n" +
                    "    p.CodBarras ean,\n" +
                    "    p.UM unidade,\n" +
                    "    p.Validade,\n" +
                    "    p.Nome descricaocompleta,\n" +
                    "    p.Secao merc1,\n" +
                    "    p.Familia id_familia,\n" +
                    "    p.Peso,\n" +
                    "    p.EstoqueMin,\n" +
                    "    p.Custo,\n" +
                    "    p.Venda preco,\n" +
                    "    p.NCM,\n" +
                    "    p.CEST,\n" +
                    "    substring(p.cstPIS, 1, 2) piscofins_saida,\n" +
                    "    substring(p.cstICMS, 1, 2) icms_cst,\n" +
                    "    p.AliICMS icms_aliquota,\n" +
                    "    p.AliRedBaseCalcICMS icms_reduzido\n" +
                    "from\n" +
                    "	fm.mercadorias p\n" +
                    "order by\n" +
                    "	p.Codigo"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    
                    Double est = estoque.get(rst.getInt("id"));
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setEan(rst.getString("ean"));
                    imp.setTipoEmbalagem(rst.getString("unidade"));
                    imp.setValidade(rst.getInt("Validade"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoGondola(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaocompleta"));
                    imp.setCodMercadologico1(mercadologicos.get(rst.getString("merc1")));
                    imp.setIdFamiliaProduto(rst.getString("id_familia"));
                    imp.setPesoBruto(rst.getDouble("Peso"));
                    imp.setPesoLiquido(rst.getDouble("Peso"));
                    imp.setEstoqueMinimo(rst.getDouble("EstoqueMin"));
                    imp.setEstoque(est != null ? est : 0D);
                    imp.setCustoComImposto(rst.getDouble("Custo"));
                    imp.setCustoSemImposto(rst.getDouble("Custo"));
                    imp.setPrecovenda(rst.getDouble("preco"));
                    imp.setNcm(rst.getString("NCM"));
                    imp.setCest(rst.getString("CEST"));
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
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	 f.Codigo id,\n" +
                    "    f.Nome razao,\n" +
                    "    f.CNPJ cnpj,\n" +
                    "    f.IE,\n" +
                    "    f.Endereco,\n" +
                    "    f.Numero,\n" +
                    "    f.Complemento,\n" +
                    "    f.Bairro,\n" +
                    "    f.CodMunicipio,\n" +
                    "    f.CEP,\n" +
                    "    f.Fone1,\n" +
                    "    f.Fone2,\n" +
                    "    f.DataCadastro,\n" +
                    "    f.Observacoes,\n" +
                    "    f.Contato\n" +
                    "from\n" +
                    "	fm.fornecedores f\n" +
                    "order by\n" +
                    "	f.Codigo"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("razao"));
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    imp.setIe_rg(rst.getString("IE"));
                    imp.setEndereco(rst.getString("Endereco"));
                    imp.setNumero(rst.getString("Numero"));
                    imp.setComplemento(rst.getString("Complemento"));
                    imp.setBairro(rst.getString("Bairro"));
                    imp.setIbge_municipio(rst.getInt("CodMunicipio"));
                    imp.setCep(rst.getString("CEP"));
                    imp.setTel_principal(rst.getString("Fone1"));
                    imp.setDatacadastro(rst.getDate("DataCadastro"));
                    imp.setObservacao(rst.getString("Observacoes"));
                    imp.addContato(rst.getString("Contato"), rst.getString("Fone2"), "", TipoContato.COMERCIAL, "");
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	 c.Codigo id,\n" +
                    "    c.CPF cnpj,\n" +
                    "    c.RG ie_rg,\n" +
                    "    c.IM insc_municipal,\n" +
                    "    c.Nome razao,\n" +
                    "    c.Fantasia fantasia,\n" +
                    "    lower(coalesce(c.Bloqueado, '')) Bloqueado,\n" +
                    "    c.Endereco,\n" +
                    "    c.Numero,\n" +
                    "    c.Complemento,\n" +
                    "    c.Bairro,\n" +
                    "    c.CodMunicipio,\n" +
                    "    c.CEP,\n" +
                    "    c.DataNascimento,\n" +
                    "    c.DataCadastro,\n" +
                    "    c.Profissao cargo,\n" +
                    "    c.Valor salario,\n" +
                    "    c.LimiteCredito,\n" +
                    "    c.Observacoes,\n" +
                    "    c.DiaVencimento,\n" +
                    "    c.senha,\n" +
                    "    c.Fone1,\n" +
                    "    c.Fone2,\n" +
                    "    c.Celular,\n" +
                    "    c.Email\n" +
                    "from\n" +
                    "	fm.clientes c\n" +
                    "order by\n" +
                    "	c.Codigo"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    
                    imp.setId(rst.getString("id"));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setInscricaoestadual(rst.getString("ie_rg"));
                    imp.setInscricaoMunicipal(rst.getString("insc_municipal"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setBloqueado("sim".equals(rst.getString("Bloqueado")));
                    imp.setEndereco(rst.getString("Endereco"));
                    imp.setNumero(rst.getString("Numero"));
                    imp.setComplemento(rst.getString("Complemento"));
                    imp.setBairro(rst.getString("Bairro"));
                    imp.setMunicipioIBGE(rst.getInt("CodMunicipio"));
                    imp.setCep(rst.getString("CEP"));
                    imp.setDataNascimento(rst.getDate("DataNascimento"));
                    imp.setDataCadastro(rst.getDate("DataCadastro"));
                    imp.setCargo(rst.getString("cargo"));
                    imp.setSalario(rst.getDouble("salario"));
                    imp.setValorLimite(rst.getDouble("LimiteCredito"));
                    imp.setObservacao2(rst.getString("Observacoes"));
                    imp.setDiaVencimento(rst.getInt("DiaVencimento"));
                    imp.setSenha(rst.getInt("senha"));
                    imp.setTelefone(rst.getString("Fone1"));
                    imp.addTelefone("FONE 2", rst.getString("Fone2"));
                    imp.addCelular("CELULAR", rst.getString("Celular"));
                    imp.addEmail(rst.getString("Email"), TipoContato.COMERCIAL);
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	 c.Codigo id,\n" +
                    "    c.Data dataemissao,\n" +
                    "    c.ecf,\n" +
                    "    c.Valor,\n" +
                    "    c.Descricao observacao,\n" +
                    "    c.CodCliente id_cliente,\n" +
                    "    c.Vencimento,\n" +
                    "    c.Juros,\n" +
                    "    c.CodCupom\n" +
                    "from\n" +
                    "	fm.contasreceber c\n" +
                    "where lower(c.pago) != 'sim'\n" +
                    "order by\n" +
                    "	id"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    
                    imp.setId(rst.getString("id"));
                    imp.setDataEmissao(rst.getDate("dataemissao"));
                    imp.setEcf(rst.getString("ecf"));
                    imp.setValor(rst.getDouble("Valor"));
                    imp.setObservacao(rst.getString("observacao"));
                    imp.setIdCliente(rst.getString("id_cliente"));
                    imp.setDataVencimento(rst.getDate("Vencimento"));
                    imp.setJuros(rst.getDouble("Juros"));
                    imp.setNumeroCupom(rst.getString("CodCupom"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<ChequeIMP> getCheques() throws Exception {
        List<ChequeIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	 c.Codigo id,\n" +
                    "    cl.CPF,\n" +
                    "    c.Cheque,\n" +
                    "    c.Banco,\n" +
                    "    c.Agencia,\n" +
                    "    c.Conta,\n" +
                    "    c.Data,\n" +
                    "    c.Valor,\n" +
                    "    cl.RG,\n" +
                    "    cl.Fone1,\n" +
                    "    cl.Nome,\n" +
                    "    c.Observacoes,\n" +
                    "    c.Vencimento\n" +
                    "from\n" +
                    "	fm.cheques c\n" +
                    "    left join fm.clientes cl on\n" +
                    "		c.CodCliente = cl.Codigo\n" +
                    "order by\n" +
                    "	c.Codigo"
            )) {
                while (rst.next()) {
                    ChequeIMP imp = new ChequeIMP();
                    
                    imp.setId(rst.getString("id"));
                    imp.setCpf(rst.getString("CPF"));
                    imp.setNumeroCheque(rst.getString("Cheque"));
                    imp.setBanco(Utils.stringToInt(rst.getString("Banco")));
                    imp.setAgencia(rst.getString("Agencia"));
                    imp.setConta(rst.getString("Conta"));
                    imp.setDate(rst.getDate("Data"));
                    imp.setValor(rst.getDouble("Valor"));
                    imp.setRg(rst.getString("RG"));
                    imp.setTelefone(rst.getString("Fone1"));
                    imp.setNome(rst.getString("Nome"));
                    imp.setObservacao(rst.getString("Observacoes"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        Set<OpcaoProduto> opt = new HashSet<>();
        opt.addAll(OpcaoProduto.getMercadologico());
        opt.addAll(OpcaoProduto.getFamilia());
        opt.addAll(OpcaoProduto.getProduto());
        opt.addAll(OpcaoProduto.getComplementos());
        opt.addAll(Arrays.asList(
                OpcaoProduto.NCM,
                OpcaoProduto.CEST,
                OpcaoProduto.ICMS,
                OpcaoProduto.PIS_COFINS
        ));
        opt.addAll(Arrays.asList(
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.DATA_CADASTRO,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.VALIDADE
        ));
        opt.remove(OpcaoProduto.MARGEM);
        opt.remove(OpcaoProduto.NATUREZA_RECEITA);
        return opt;
    }

    @Override
    public String getSistema() {
        if(lojaID != null && !"".equals(lojaID)) {
            return "FM" + lojaID;
        } else {
            return "FM";
        }
    }
    
}
