/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2_5.dao.sistema;

import java.sql.ResultSet;
import java.sql.Statement;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2_5.dao.conexao.ConexaoFirebird;

/**
 *
 * @author Wagner
 */
public class MbdDAO extends InterfaceDAO implements MapaTributoProvider {

    private String lojaCliente;

    public String getLojaCliente() {
        return this.lojaCliente;
    }

    @Override
    public String getSistema() {
        return "MBD";
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
                OpcaoProduto.FAMILIA,
                OpcaoProduto.FAMILIA_PRODUTO,
                OpcaoProduto.DATA_CADASTRO,
                OpcaoProduto.PDV_VENDA
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
                OpcaoFornecedor.PRODUTO_FORNECEDOR,
                OpcaoFornecedor.PAGAR_FORNECEDOR,
                OpcaoFornecedor.TELEFONE
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
                OpcaoCliente.TELEFONE,
                OpcaoCliente.ESTADO_CIVIL,
                OpcaoCliente.EMPRESA,
                OpcaoCliente.SALARIO,
                OpcaoCliente.BLOQUEADO,
                OpcaoCliente.OBSERVACOES2,
                OpcaoCliente.OBSERVACOES,
                OpcaoCliente.NUMERO,
                OpcaoCliente.COMPLEMENTO,
                OpcaoCliente.SITUACAO_CADASTRO,
                OpcaoCliente.RECEBER_CREDITOROTATIVO));
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT distinct\n"
                    + " t.CODIGO_ID id,\n"
                    + " t.ALIQUOTA aliquota,\n"
                    + " t.DESCRICAO descricao\n"
                    + "FROM TRIBUTAR t "
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(
                            rst.getString("id"),
                            rst.getString("descricao")
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
                    "SELECT\n"
                    + "	DISTINCT \n"
                    + "	CODIGO_ID mercid1,\n"
                    + "	DESCRICAO descri1,\n"
                    + "	CODIGO_ID mercid2,\n"
                    + "	DESCRICAO descri2,\n"
                    + "	CODIGO_ID mercid3,\n"
                    + "	DESCRICAO descri3\n"
                    + "FROM\n"
                    + "	GRUPO p"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setMerc1ID(rst.getString("mercid1"));
                    imp.setMerc1Descricao(rst.getString("descri1"));
                    imp.setMerc2ID(rst.getString("mercid2"));
                    imp.setMerc2Descricao(rst.getString("descri2"));
                    imp.setMerc3ID(rst.getString("mercid3"));
                    imp.setMerc3Descricao(rst.getString("descri3"));
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
                    + " p.CODIGO_ID id,\n"
                    + " p.PRODUTO ean,\n"
                    + " p.DESCRICAO descricao,\n"
                    + " p.GRUPO mercid1,\n"
                    + " p.GRUPO mercid2,\n"
                    + " p.GRUPO mercid3,\n"
                    + " u.SIGLA unidade,\n"
                    + " p.PRECO precovenda,\n"
                    + " p.PRECOCUSTO custosemimposto,\n"
                    + " p.PRECOCUSTO custocomimposto,\n"
                    + " p.PRECOCUSTO customedio,\n"
                    + " p.MARGEM_IDEAL margem,\n"
                    + " p.FORNECEDOR cod_for,\n"
                    + " p.PESOLIQ peso,\n"
                    + " CASE WHEN p.BALANCA = 'S' THEN 1\n"
                    + " ELSE 0 END AS BALANCA,\n"
                    + " CASE WHEN p.ATIVO = 'S' THEN 1\n"
                    + " ELSE 0 END AS situacao,\n"
                    + " p.NCM ncm,\n"
                    + " p.ESTOQUE_FISICO estoque,\n"
                    + " p.ST_PIS piscofins,\n"
                    + " p.ST_COFINS piscofins2,\n"
                    + " p.TRIBUTAR idaliquota,\n"
                    + " p.CODIGOCEST cest\n"
                    + " FROM PRODUTOS p\n"
                    + " LEFT JOIN UNIDADE u ON p.UNIDADE = u.CODIGO_ID "
            )) {
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().getProdutosBalanca();
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("id"));
                    imp.setDescricaoCompleta(removerAcentos(rst.getString("descricao")));
                    imp.setDescricaoReduzida(removerAcentos(rst.getString("descricao")));
                    imp.setDescricaoGondola(removerAcentos(rst.getString("descricao")));
                    imp.setCodMercadologico1(rst.getString("mercid1"));
                    imp.setCodMercadologico2(rst.getString("mercid2"));
                    imp.setCodMercadologico3(rst.getString("mercid3"));
                    imp.setTipoEmbalagem(rst.getString("unidade"));
                    imp.setTipoEmbalagemVolume(rst.getString("unidade"));
                    imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setPiscofinsCstCredito(rst.getString("piscofins"));
                    imp.setPiscofinsCstDebito(rst.getString("piscofins"));
                    imp.setIdFamiliaProduto(rst.getString("mercid1"));

                    int codigoProduto = Utils.stringToInt(rst.getString("id"), -2);
                    ProdutoBalancaVO produtoBalanca = produtosBalanca.get(codigoProduto);

                    if (produtoBalanca != null) {
                        imp.setEan(String.valueOf(produtoBalanca.getCodigo()));
                        imp.seteBalanca(true);
                        imp.setTipoEmbalagem("U".equals(produtoBalanca.getPesavel()) ? "UN" : "KG");
                        imp.setValidade(produtoBalanca.getValidade());
                        imp.setQtdEmbalagem(1);
                    } else {
                        imp.setEan(rst.getString("ean"));
                        imp.seteBalanca(false);
                        imp.setTipoEmbalagem(rst.getString("unidade"));
                        imp.setValidade(0);
                        imp.setQtdEmbalagem(0);
                    }

                    imp.setIcmsDebitoId(rst.getString("idaliquota"));
                    imp.setIcmsDebitoForaEstadoId(rst.getString("idaliquota"));
                    imp.setIcmsDebitoForaEstadoNfId(rst.getString("idaliquota"));
                    imp.setIcmsCreditoId(rst.getString("idaliquota"));
                    imp.setIcmsCreditoForaEstadoId(rst.getString("idaliquota"));
                    imp.setIcmsConsumidorId(rst.getString("idaliquota"));

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
                    "SELECT\n"
                    + "	CODIGO_ID id,\n"
                    + "	RAZAOSOCIAL razao,\n"
                    + "	ENDERECO endereco,\n"
                    + "	BAIRRO bairro,\n"
                    + "	CEP cep,\n"
                    + "	CIDADE  municipio,\n"
                    + "	UF uf,\n"
                    + "	NUMEROCASA numero,\n"
                    + "	TELEFONE tel_principal,\n"
                    + "	CNPJ cpfcnpj,\n"
                    + "	IE inscestadual,\n"
                    + "	NOMEFANTASIA fantasia,\n"
                    + "	EMAIL,\n"
                    + "	DTCADASTRO dtcadastro,\n"
                    + "	ATIVO \n"
                    + "FROM\n"
                    + "	CLIFOR \n"
                    + "WHERE TIPOCLIFOR != 'C'"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(removerAcentos(rst.getString("razao")));
                    imp.setFantasia(removerAcentos(rst.getString("fantasia")));
                    imp.setCnpj_cpf(rst.getString("cpfcnpj"));
                    imp.setIe_rg(rst.getString("inscestadual"));
                    imp.setEndereco(removerAcentos(rst.getString("endereco")));
                    imp.setNumero(rst.getString("numero"));
                    imp.setBairro(removerAcentos(rst.getString("bairro")));
                    imp.setCep(rst.getString("cep"));
                    imp.setMunicipio(removerAcentos(rst.getString("municipio")));
                    imp.setUf(rst.getString("uf"));
                    imp.setTel_principal(rst.getString("tel_principal"));
                    imp.setDatacadastro(rst.getDate("dtcadastro"));

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
            try (ResultSet rst = stm.executeQuery(
                    ""
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdFornecedor(rst.getString("fornecedorid"));
                    imp.setIdProduto(rst.getString("produtoid"));
                    imp.setCodigoExterno(rst.getString("referencia"));

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
                    " SELECT\n"
                    + "	p.CODIGO_ID id,\n"
                    + "	p.PRODUTO ean,\n"
                    + "	u.SIGLA un\n"
                    + "FROM\n"
                    + "	PRODUTOS p\n"
                    + "LEFT JOIN UNIDADE u ON\n"
                    + "	p.UNIDADE = u.CODIGO_ID"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setEan(rst.getString("ean"));
                    imp.setTipoEmbalagem(rst.getString("un"));

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
                    + " CODIGO_Id id,\n"
                    + " NOMEFANTASIA nome,\n"
                    + " ENDERECO endereco,\n"
                    + " NUMEROCASA numero,\n"
                    + " BAIRRO bairro,\n"
                    + " CEP cep,\n"
                    + " CIDADE municipio,\n"
                    + " UF uf,\n"
                    + " CPF cpfcnpj,\n"
                    + " RG inscestrg,\n"
                    + " TELEFONE telefone,\n"
                    + " CELULAR celular,\n"
                    + " OBSERVACAO obs,\n"
                    + " DTCADASTRO dtcadastro,\n"
                    + " DATA_NASC dtnasc,\n"
                    + " SALDO_LIMITE limite,\n"
                    + " CASE \n"
                    + " WHEN ATIVO LIKE 'N'\n"
                    + " THEN 0\n"
                    + " ELSE 1\n"
                    + " END ativo,\n"
                    + " DATA_MODIF DTATUALIZACAO\n"
                    + "FROM CLIFOR\n"
                    + "WHERE TIPOCLIFOR = 'C'"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rst.getString("id"));
                    imp.setRazao(removerAcentos(rst.getString("nome")));
                    imp.setEndereco(removerAcentos(rst.getString("endereco")));
                    imp.setBairro(removerAcentos(rst.getString("bairro")));
                    imp.setMunicipio(removerAcentos(rst.getString("cidade")));
                    imp.setDataNascimento(rst.getDate("dtnasc"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setCnpj(rst.getString("cpfcnpj"));
                    imp.setInscricaoestadual(rst.getString("inscestrg"));

                    if (rst.getString("telefone") == null) {
                        imp.setTelefone(rst.getString("telefone"));
                    } else if (rst.getString("telefone").startsWith("0")) {
                        imp.setTelefone(rst.getString("telefone").substring(1));
                    } else {
                        imp.setTelefone(rst.getString("telefone"));
                    }

                    imp.setCelular(rst.getString("celular"));
                    imp.setDataCadastro(rst.getDate("dtcadastro"));
                    imp.setValorLimite(rst.getDouble("limite"));
                    imp.setObservacao(rst.getString("obs"));
                    imp.setAtivo(rst.getBoolean("ativo"));

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
                    + " SEQ id,\n"
                    + " COD_CLI idcliente,\n"
                    + " COD_FILIAL,\n"
                    + " NUM_OPER numerodocumento,\n"
                    + " DT_VENDA dataemissao,\n"
                    + " DTATUALIZACAO,\n"
                    + " CASE \n"
                    + " WHEN BAIXADO LIKE 'S'\n"
                    + " THEN VALOR\n"
                    + " ELSE VR_ATUAL \n"
                    + " END valor,\n"
                    + " N_FISCAL,\n"
                    + " PARCELA,\n"
                    + " VENCIMENTO datavencimento,\n"
                    + " STATUS\n"
                    + "FROM RECEBER \n"
                    + "WHERE \n"
                    + " BAIXADO <> 'S'"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rst.getString("id"));
                    imp.setIdCliente(rst.getString("idcliente"));
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
    
    private Date dataInicioVenda;
    private Date dataTerminoVenda;

    public void setDataInicioVenda(Date dataInicioVenda) {
        this.dataInicioVenda = dataInicioVenda;
    }

    public void setDataTerminoVenda(Date dataTerminoVenda) {
        this.dataTerminoVenda = dataTerminoVenda;
    }

    private String removerAcentos(String texto) {
        texto = texto != null ? Normalizer.normalize(texto, Normalizer.Form.NFD) : "";
        texto = texto != null ? texto.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "") : "";
        texto = texto != null ? texto.replaceAll("�", "C") : "";
        texto = texto != null ? texto.replaceAll("[^\\p{ASCII}]", "") : "";

        return texto;
    }
}
