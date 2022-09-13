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
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author guilhermegomes
 */
public class PlenusDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "Plenus";
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
                OpcaoProduto.PRECO,
                OpcaoProduto.ATIVO,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.NATUREZA_RECEITA,
                OpcaoProduto.ICMS,
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
        List<Estabelecimento> result = new ArrayList<>();
        
        try(Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "    id_empresa,\n" +
                    "    nome_fantasia\n" +
                    "from\n" +
                    "    empresa")) {
                while(rs.next()) {
                    result.add(new Estabelecimento(
                                        rs.getString("id_empresa"), 
                                        rs.getString("nome_fantasia")));
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
                    "SELECT DISTINCT \n"
                    + "    su.flagsubstituicaotributaria AS codTrib,\n"
                    + "    A.trib_aprox_estadual AS aliquota\n"
                    + "from\n"
                    + "    PRODUTOS A\n"
                    + "    left join PRODUTO_BASE B on A.ID_PRODUTO_BASE = B.ID_PRODUTO_BASE\n"
                    + "    left join SUBGRUPO SU on SU.ID_SUBGRUPO = B.ID_SUBGRUPO\n"
                    + "    left join PRODUTO_ADICIONAIS PA on A.ID_PRODUTO = PA.ID_PRODUTO \n"
                    + "where\n"
                    + "    A.ID_PRODUTO > 0\n"
                    + "ORDER BY 1, 2   "
            )) {
                while (rs.next()) {
                    String idTrib = rs.getString("codTrib") + "-" + rs.getString("aliquota");
                    result.add(new MapaTributoIMP(
                            idTrib,
                            idTrib));
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
                    "select\n" +
                    "    A.ID_PRODUTO,\n" +
                    "    A.ID_PRODUTO_BASE,\n" +
                    "    a.dtcadastro,\n" +
                    "    A.DESCRICAO,\n" +
                    "    A.UN_SAID,\n" +
                    "    A.UN_ENTR,\n" +
                    "    A.UN_TRIB,\n" +
                    "    A.IDCODBARPROD EAN,\n" +
                    "    M.DESCRICAO marca,\n" +
                    "    FLAGATIVO ATIVO,\n" +
                    "    coalesce((select\n" +
                    "         saldoestoque\n" +
                    "    from\n" +
                    "        produto_saldoestoque\n" +
                    "    where\n" +
                    "        id_empresa = " + getLojaOrigem() + " and\n" +
                    "        id_produto = a.id_produto), 0) estoque,\n" +
                    "    A.PRECOVENDAVAREJO precovenda,\n" +
                    "    B.PERCMARGEMVAREJO margem,\n" +
                    "    PA.CUSTOMEDIO custo, \n" +
                    "    S.DESCRICAO secao,\n" +
                    "    G.DESCRICAO grupo,\n" +
                    "    SU.DESCRICAO subgrupo,\n" +
                    "    A.CODNCM ncm,\n" +
                    "    B.ID_MARCA,\n" +
                    "    B.ID_SECAO,\n" +
                    "    B.ID_GRUPO,\n" +
                    "    B.ID_SUBGRUPO,\n" +
                    "    A.PESOBRUTO,\n" +
                    "    A.FLAGEXPORTABALANCA balanca,\n" +
                    "    A.ESTOQUEMINIMO,\n" +
                    "    A.ESTOQUEMAXIMO,\n" +
                    "    A.PESOLIQUIDO,\n" +
                    "    A.ISIGUALBASE,\n" +
                    "    A.QUANT_TRIB,\n" +
                    "    A.QUANT_ENTR,\n" +
                    "    A.CEST,\n" +
                    "    su.cst_pis_venda pisdebito,\n" +
                    "    su.cst_cofins_venda cofinsdebito,\n" + 
                    "    su.flagsubstituicaotributaria esubstituido,\n" +        
                    "    A.trib_aprox_municipal,\n" +
                    "    A.trib_aprox_estadual,\n" +
                    "    A.trib_aprox_importado\n" +        
                    "from\n" +
                    "    PRODUTOS A\n" +
                    "    left join PRODUTO_BASE B on A.ID_PRODUTO_BASE = B.ID_PRODUTO_BASE\n" +
                    "    left join MARCA M on B.ID_MARCA = M.ID_MARCA\n" +
                    "    left join SECAO S on S.ID_SECAO = B.ID_SECAO\n" +
                    "    left join GRUPO G on G.ID_GRUPO = B.ID_GRUPO\n" +
                    "    left join SUBGRUPO SU on SU.ID_SUBGRUPO = B.ID_SUBGRUPO\n" +
                    "    left join PRODUTO_ADICIONAIS PA on A.ID_PRODUTO = PA.ID_PRODUTO and ID_EMPRESA = " + getLojaOrigem() + " \n" +
                    "where\n" +
                    "    A.ID_PRODUTO > 0")) {
                while(rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("ID_PRODUTO"));
                    imp.setDescricaoCompleta(rs.getString("DESCRICAO"));
                    imp.setDataCadastro(rs.getDate("dtcadastro"));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    imp.setTipoEmbalagem(rs.getString("UN_SAID"));
                    imp.setTipoEmbalagemCotacao(rs.getString("UN_ENTR"));
                    imp.setEan(rs.getString("EAN"));
                    
                    imp.setSituacaoCadastro("T".equals(rs.getString("ATIVO")) ? 1 : 0);
                    imp.setEstoque(rs.getDouble("estoque"));
                    imp.setEstoqueMaximo(rs.getDouble("estoquemaximo"));
                    imp.setEstoqueMinimo(rs.getDouble("estoqueminimo"));
                    imp.setPrecovenda(rs.getDouble("precovenda"));
                    imp.setMargem(rs.getDouble("margem"));
                    imp.setCustoComImposto(rs.getDouble("custo"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setPesoBruto(rs.getDouble("pesobruto"));
                    imp.setPesoLiquido(rs.getDouble("pesoliquido"));
                    imp.setNcm(rs.getString("ncm"));
                    imp.setCest(rs.getString("cest"));
                    imp.setPiscofinsCstDebito(rs.getString("pisdebito"));
                    
                    String idTrib = rs.getString("esubstituido") + "-" + rs.getString("trib_aprox_estadual");
                    
                    imp.setIcmsDebitoId(idTrib);
                    imp.setIcmsDebitoForaEstadoId(idTrib);
                    imp.setIcmsDebitoForaEstadoNfId(idTrib);
                    imp.setIcmsCreditoId(idTrib);
                    imp.setIcmsCreditoForaEstadoId(idTrib);
                    imp.setIcmsConsumidorId(idTrib);                    
                    
                    /*if(rs.getString("esubstituido") != null && rs.getString("esubstituido").equals("T")) {
                        imp.setIcmsAliq(0);
                        imp.setIcmsCst(60);
                        imp.setIcmsReducao(0);
                    }*/
                    
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
                    "select\n" +
                    "    ID_CLIENTE_FORNECEDOR id,\n" +
                    "    A.ATIVO,\n" +
                    "    PESSOA,\n" +
                    "    FANTASIA_APELIDO,\n" +
                    "    CPF_CNPJ,\n" +
                    "    RG_INSCR,\n" +
                    "    FONE1,\n" +
                    "    CELULAR,\n" +
                    "    RAZSOCIAL_NOME,\n" +
                    "    LOGRADOURO,\n" +
                    "    C.NOME CIDADE,\n" +
                    "    UF,\n" +
                    "    D.ID_ESTADO,\n" +
                    "    E.DESCRICAO BAIRRO,\n" +
                    "    A.CEP,\n" +
                    "    A.ID_CIDADE,\n" +
                    "    A.ID_CIDADE_NASCIMENTO,\n" +
                    "    A.ID_CIDADE_COBRANCA,\n" +
                    "    A.DT_CADASTRO,\n" +
                    "    A.EMAIL,\n" +
                    "    C.CODMUN_IBGE,\n" +
                    "    A.INSCR_MUNICIPAL,\n" +
                    "    A.CONSUMIDORFINAL,\n" +
                    "    A.FONE2,\n" +
                    "    A.NUMERO,\n" +
                    "    A.COMPLEMENTO\n" +
                    "from\n" +
                    "    CLIENTES_FORNECEDORES A,\n" +
                    "    CIDADES C,\n" +
                    "    ESTADOS D,\n" +
                    "    BAIRRO E\n" +
                    "where\n" +
                    "    A.ID_CIDADE = C.ID_CIDADE\n" +
                    "    and C.ID_ESTADO = D.ID_ESTADO\n" +
                    "    and A.ID_BAIRRO = E.ID_BAIRRO\n" +
                    "    and a.flag_cliente = 'T'")) {
                while(rs.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    
                    imp.setId(rs.getString("id"));
                    imp.setAtivo(rs.getString("ativo").equals("T"));
                    imp.setRazao(rs.getString("RAZSOCIAL_NOME"));
                    imp.setFantasia(rs.getString("FANTASIA_APELIDO"));
                    imp.setCnpj(rs.getString("CPF_CNPJ"));
                    imp.setInscricaoestadual(rs.getString("RG_INSCR"));
                    imp.setTelefone(rs.getString("FONE1"));
                    imp.setCelular(rs.getString("CELULAR"));
                    imp.setEndereco(rs.getString("LOGRADOURO"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setMunicipio(rs.getString("CIDADE"));
                    imp.setUf(rs.getString("UF"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setCep(rs.getString("cep"));
                    imp.setDataCadastro(rs.getDate("DT_CADASTRO"));
                    imp.setEmail(rs.getString("email"));
                    imp.setInscricaoMunicipal(rs.getString("INSCR_MUNICIPAL"));
                    
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
                    "select\n" +
                    "    ID_CLIENTE_FORNECEDOR id,\n" +
                    "    A.ATIVO,\n" +
                    "    PESSOA,\n" +
                    "    FANTASIA_APELIDO,\n" +
                    "    CPF_CNPJ,\n" +
                    "    RG_INSCR,\n" +
                    "    FONE1,\n" +
                    "    CELULAR,\n" +
                    "    RAZSOCIAL_NOME,\n" +
                    "    LOGRADOURO,\n" +
                    "    C.NOME CIDADE,\n" +
                    "    UF,\n" +
                    "    D.ID_ESTADO,\n" +
                    "    E.DESCRICAO BAIRRO,\n" +
                    "    A.CEP,\n" +
                    "    A.ID_CIDADE,\n" +
                    "    A.ID_CIDADE_NASCIMENTO,\n" +
                    "    A.ID_CIDADE_COBRANCA,\n" +
                    "    A.DT_CADASTRO,\n" +
                    "    A.EMAIL,\n" +
                    "    C.CODMUN_IBGE,\n" +
                    "    A.INSCR_MUNICIPAL,\n" +
                    "    A.CONSUMIDORFINAL,\n" +
                    "    A.FONE2,\n" +
                    "    A.NUMERO,\n" +
                    "    A.COMPLEMENTO\n" +
                    "from\n" +
                    "    CLIENTES_FORNECEDORES A,\n" +
                    "    CIDADES C,\n" +
                    "    ESTADOS D,\n" +
                    "    BAIRRO E\n" +
                    "where\n" +
                    "    A.ID_CIDADE = C.ID_CIDADE\n" +
                    "    and C.ID_ESTADO = D.ID_ESTADO\n" +
                    "    and A.ID_BAIRRO = E.ID_BAIRRO\n" +
                    "    and a.flag_fornecedor = 'T'")) {
                while (rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("id"));
                    imp.setAtivo(rs.getString("ativo").equals("T"));
                    imp.setRazao(rs.getString("razsocial_nome"));
                    imp.setFantasia(rs.getString("fantasia_apelido"));
                    imp.setCnpj_cpf(rs.getString("cpf_cnpj"));
                    imp.setIe_rg(rs.getString("rg_inscr"));
                    imp.setTel_principal(rs.getString("fone1"));
                    
                    String cel = rs.getString("celular");
                    
                    if (cel != null && !cel.isEmpty()) {
                        imp.addCelular("CELULAR", cel);
                    }
                    
                    imp.setEndereco(rs.getString("logradouro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("uf"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setCep(rs.getString("cep"));
                    imp.setDatacadastro(rs.getDate("dt_cadastro"));
                    
                    String email = rs.getString("email");
                    
                    if (email != null && !email.isEmpty()) {
                        imp.addEmail("EMAIL", email, TipoContato.NFE);
                    }
                    
                    imp.setInsc_municipal(rs.getString("inscr_municipal"));
                    
                    String fone2 = rs.getString("fone2");
                    
                    if (fone2 != null && !fone2.isEmpty()) {
                        imp.addTelefone("FONE2", fone2);
                    }
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<ContaPagarIMP> getContasPagar() throws Exception {
        List<ContaPagarIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "SELECT \n" +
                    "	ID_CONTAS_PAGAR id,\n" +
                    "	ID_FORNECEDOR,\n" +
                    "	titulo,\n" +
                    "	digito parcela,\n" +
                    "	DATA_CADASTRAMENTO emissao,\n" +
                    "	DATA_VENCIMENTO vencimento,\n" +
                    "	VALOR,\n" +
                    "	TOT_PARC,\n" +
                    "	juros,\n" +
                    "	multa,\n" +
                    "	DESCONTO,\n" +
                    "	OBSERVACAO\n" +
                    "FROM \n" +
                    "	CONTAS_PAGAR cp\n" +
                    "WHERE \n" +
                    "	 LIQUIDADA = 'F' AND \n" +
                    "	 ID_EMPRESA = " + getLojaOrigem())) {
                while (rs.next()) {
                    ContaPagarIMP imp = new ContaPagarIMP();
                    
                    imp.setId(rs.getString("id"));
                    imp.setDataEmissao(rs.getDate("emissao"));
                    imp.setNumeroDocumento(rs.getString("titulo"));
                    imp.setIdFornecedor(rs.getString("id_fornecedor"));
                    imp.setObservacao(rs.getString("observacao"));
                    
                    imp.addVencimento(rs.getDate("vencimento"), rs.getDouble("valor"), rs.getInt("parcela"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }
    
    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "    id_contas_receber id,\n" +
                    "    id_cliente,\n" +
                    "    titulo,\n" +
                    "    id_venda,\n" +
                    "    vend,\n" +
                    "    valor,\n" +
                    "    juros,\n" +
                    "    multa,\n" +
                    "    digito,\n" +        
                    "    desconto,\n" +
                    "    data_cadastramento,\n" +
                    "    data_vencimento\n" +
                    "from    \n" +
                    "    contas_receber\n" +
                    "where\n" +
                    "    liquidada = 'F' and\n" +
                    "    id_empresa = " + getLojaOrigem())) {
                while(rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    
                    imp.setId(rs.getString("id"));
                    imp.setIdCliente(rs.getString("id_cliente"));
                    imp.setNumeroCupom(rs.getString("vend"));
                    imp.setParcela(rs.getInt("digito"));
                    imp.setValor(rs.getDouble("valor") - rs.getDouble("desconto"));
                    imp.setJuros(rs.getDouble("juros"));
                    imp.setDataEmissao(rs.getDate("data_cadastramento"));
                    imp.setDataVencimento(rs.getDate("data_vencimento"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }
}
