package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author guilhermegomes
 */
public class PlenusDAO extends InterfaceDAO {

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
                OpcaoProduto.CUSTO_COM_IMPOSTO,
                OpcaoProduto.CUSTO_SEM_IMPOSTO,
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
                    "        id_empresa = 1 and\n" +
                    "        id_produto = a.id_produto), 0) estoque,\n" +
                    "    A.PRECOVENDAVAREJO precovenda,\n" +
                    "    B.PERCMARGEMVAREJO margem,\n" +
                    "    A.CUSTOREPOSICAO custo,\n" +
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
                    
                    if(rs.getString("balanca") != null && "T".equals(rs.getString("balanca").trim())) {
                        imp.seteBalanca(true);
                        
                        if(imp.getEan() != null && !"".equals(imp.getEan())) {
                            imp.setEan(imp.getEan().substring(0, imp.getEan().length() - 1));
                        }
                    }
                    
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
