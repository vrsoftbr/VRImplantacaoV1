/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2_5.dao.sistema;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2_5.dao.conexao.ConexaoFirebird;

/**
 *
 * @author Wagner
 */
public class DevSisDAO extends InterfaceDAO implements MapaTributoProvider {

    private String lojaCliente;

    public String getLojaCliente() {
        return this.lojaCliente;
    }

    @Override
    public String getSistema() {
        return "DevSis";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.VALIDADE,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.QTD_EMBALAGEM_COTACAO,
                OpcaoProduto.QTD_EMBALAGEM_EAN,
                OpcaoProduto.ATIVO,
                OpcaoProduto.PESO_BRUTO,
                OpcaoProduto.PESO_LIQUIDO,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.ESTOQUE_MINIMO,
                OpcaoProduto.MARGEM,
                OpcaoProduto.VENDA_PDV,
                OpcaoProduto.PRECO,
                OpcaoProduto.CUSTO,
                OpcaoProduto.CUSTO_COM_IMPOSTO,
                OpcaoProduto.CUSTO_SEM_IMPOSTO,
                OpcaoProduto.NCM,
                OpcaoProduto.EXCECAO,
                OpcaoProduto.CEST,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.ICMS,
                OpcaoProduto.DATA_CADASTRO
        ));
    }

    @Override
    public Set<OpcaoFornecedor> getOpcoesDisponiveisFornecedor() {
        return new HashSet<>(Arrays.asList(
                OpcaoFornecedor.DADOS,
                OpcaoFornecedor.RAZAO_SOCIAL,
                OpcaoFornecedor.NOME_FANTASIA,
                OpcaoFornecedor.CNPJ_CPF
        ));
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT DISTINCT\n"
                    + " ALIQUOTA id, \n"
                    + " CST\n"
                    + "FROM EST_PRODUTO\n"
                    + "WHERE \n"
                    + " ALIQUOTA IS NOT NULL \n"
                    + " AND \n"
                    + " CST IS NOT NULL"
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(
                            rst.getString("id"),
                            rst.getString("id"),
                            rst.getInt("cst"),
                            0,
                            0
                    ));
                }
            }
        }
        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + " m1.REFERENCIAL mercid1,\n"
                    + " m1.NOME desc1,\n"
                    + " m2.REFERENCIAL mercid2,\n"
                    + " m2.NOME desc2\n"
                    + "FROM EST_SETOR m1\n"
                    + "LEFT JOIN EST_GRUPO m2 ON m2.REF_SETOR = m1.REFERENCIAL"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setMerc1ID(rst.getString("mercid1"));
                    imp.setMerc1Descricao(rst.getString("desc1"));
                    imp.setMerc2ID(rst.getString("mercid2"));
                    imp.setMerc2Descricao(rst.getString("desc2"));
                    imp.setMerc3ID(imp.getMerc2ID());
                    imp.setMerc3Descricao(imp.getMerc2Descricao());
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + " p.REFERENCIAL id,\n"
                    + " p.CODIGO,\n"
                    + " p.NOME descricaocompleta,\n"
                    + " p.APELIDO descricaoreduzida,\n"
                    + " p.PRECO_CUSTO custo,\n"
                    + " p.MARGEM margem,\n"
                    + " p.PRECO_VENDA precovenda,\n"
                    + " p.REF_SETOR idmerc1,\n"
                    + " p.REF_GRUPO idmerc2,\n"
                    + " p.REF_GRUPO2,\n"
                    + " p.REF_GRUPO3,\n"
                    + " p.REF_FORNECEDOR fonecedorid,\n"
                    + " p.ALIQUOTA ID_ALIQUOTA,\n"
                    + " p.UNITARIO embalagem,\n"
                    + " p.NCM,\n"
                    + " p.CEST,\n"
                    + " p.STATUS situacaocadastro,\n"
                    + " CASE WHEN p.KG = 'F' THEN 0 ELSE 1 END ebalanca,\n"
                    + " p.COD_PIS pis,\n"
                    + " p.COD_COFINS cofins,\n"
                    + " e.ESTOQUE_ATUAL estoque,\n"
                    + " e.ESTOQUE_MINIMO estoqueminimo\n"
                    + "FROM EST_PRODUTO p\n"
                    + "LEFT JOIN EST_ESTOQUE e ON e.REF_PRODUTO = p.REFERENCIAL\n"
                   
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("id"));
                    imp.seteBalanca(rst.getBoolean("ebalanca"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    imp.setTipoEmbalagem(rst.getString("embalagem"));
                    imp.setCodMercadologico1(rst.getString("idmerc1"));
                    imp.setCodMercadologico2(rst.getString("idmerc2"));
                    imp.setCodMercadologico3(imp.getCodMercadologico2());
                    //imp.setSituacaoCadastro(rst.getInt("situacaocadastro"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setCustoComImposto(rst.getDouble("custo"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));

                    imp.setIcmsDebitoId(rst.getString("ID_ALIQUOTA"));
                    imp.setIcmsDebitoForaEstadoId(rst.getString("ID_ALIQUOTA"));
                    imp.setIcmsDebitoForaEstadoNfId(rst.getString("ID_ALIQUOTA"));
                    imp.setIcmsCreditoId(rst.getString("ID_ALIQUOTA"));
                    imp.setIcmsCreditoForaEstadoId(rst.getString("ID_ALIQUOTA"));
                    imp.setIcmsConsumidorId(rst.getString("ID_ALIQUOTA"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + " REF_PRODUTO produtoid,\n"
                    + " CODIGO ean \n"
                    + "FROM EST_CODIGO_EXTRA"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("produtoid"));
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

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + " REFERENCIAL id,\n"
                    + " NOME fantasia,\n"
                    + " RAZAO_SOCIAL razao,\n"
                    + " CNPJ cpfcnpj,\n"
                    + " IE,\n"
                    + " ENDERECO,\n"
                    + " BAIRRO,\n"
                    + " CIDADE,\n"
                    + " ESTADO,\n"
                    + " CEP,\n"
                    + " NUMERO,\n"
                    + " CONTATO,\n"
                    + " FONE1,\n"
                    + " FONE2\n"
                    + "FROM EST_FORNECEDOR"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("cpfcnpj"));
                    imp.setIe_rg(rst.getString("ie"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setCep(rst.getString("cep"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("estado"));
                    imp.setTel_principal(rst.getString("fone1"));

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
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + " REFERENCIAL id,\n"
                    + " NOME, \n"
                    + " RAZAO_SOCIAL,\n"
                    + " TIPO_PESSOA,\n"
                    + " CPF_CNPJ cpfcnpj,\n"
                    + " RG_IE,\n"
                    + " ENDERECO,\n"
                    + " NUMERO,\n"
                    + " BAIRRO,\n"
                    + " CIDADE,\n"
                    + " CEP,\n"
                    + " ESTADO uf,\n"
                    + " EMAIL,\n"
                    + " OBS,\n"
                    + " FONE1,\n"
                    + " FONE2,\n"
                    + " DT_NASCIMENTO,\n"
                    + " DT_CADASTRO,\n"
                    + " VALOR_PERMITIDO,\n"
                    + " ATIVO\n"
                    + "FROM FIN_CLIENTES"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rst.getString("id"));
                    imp.setRazao(rst.getString("nome"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setCnpj(rst.getString("cpfcnpj"));
                    imp.setInscricaoestadual(rst.getString("RG_IE"));
                    imp.setTelefone(rst.getString("FONE1"));
                    imp.setCelular(rst.getString("FONE2"));
                    imp.setDataCadastro(rst.getDate("DT_CADASTRO"));
                    imp.setValorLimite(rst.getDouble("VALOR_PERMITIDO"));
                    imp.setEmail(rst.getString("email"));

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
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + " REFERENCIAL id,\n"
                    + " REF_CLIENTE clienteid,\n"
                    + " VALOR,\n"
                    + " DATA dataemissao,\n"
                    + " DATA_VENCIMENTO datavencimento,\n"
                    + " HISTORICO,\n"
                    + " REF_SAIDA numerodocumento\n"
                    + "FROM FIN_RECEBER \n"
                    + "WHERE\n"
                    + " RECEBIDO = 'N'\n"
                    + " AND \n"
                    + " DATA_RECEBIMENTO IS NULL \n"
                    + " AND \n"
                    + " VALOR > 0"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rst.getString("id"));
                    imp.setIdCliente(rst.getString("clienteid"));
                    imp.setDataEmissao(rst.getDate("dataemissao"));
                    imp.setDataVencimento(rst.getDate("datavencimento"));
                    imp.setNumeroCupom(rst.getString("numerodocumento"));
                    imp.setValor(rst.getDouble("valor"));

                    result.add(imp);
                }
            }
        }
        return result;
    }
}
