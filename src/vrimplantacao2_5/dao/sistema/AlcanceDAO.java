/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2_5.dao.sistema;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import static vr.core.utils.StringUtils.LOG;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.vo.cadastro.financeiro.contareceber.OpcaoContaReceber;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.importacao.ChequeIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.ContaReceberIMP;
import vrimplantacao2.vo.importacao.ConveniadoIMP;
import vrimplantacao2.vo.importacao.ConvenioEmpresaIMP;
import vrimplantacao2.vo.importacao.ConvenioTransacaoIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;
import vrimplantacao2_5.dao.conexao.ConexaoMySQL;

/**
 *
 * @author Wagner
 */
public class AlcanceDAO extends InterfaceDAO implements MapaTributoProvider {

    private String lojaCliente;

    public String getLojaCliente() {
        return this.lojaCliente;
    }

    @Override
    public String getSistema() {
        return "ALCANCE";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.FAMILIA,
                OpcaoProduto.FAMILIA_PRODUTO,
                OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.PESO_BRUTO,
                OpcaoProduto.PESO_LIQUIDO,
                OpcaoProduto.VALIDADE,
                OpcaoProduto.MARGEM,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.ESTOQUE_MINIMO,
                OpcaoProduto.CUSTO_COM_IMPOSTO,
                OpcaoProduto.CUSTO_SEM_IMPOSTO,
                OpcaoProduto.PRECO,
                OpcaoProduto.ATIVO,
                OpcaoProduto.NCM,
                OpcaoProduto.CEST,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.NATUREZA_RECEITA,
                OpcaoProduto.ICMS,
                OpcaoProduto.ICMS_ENTRADA,
                OpcaoProduto.ICMS_ENTRADA_FORA_ESTADO,
                OpcaoProduto.ICMS_SAIDA,
                OpcaoProduto.CUSTO,
                OpcaoProduto.ICMS_SAIDA_FORA_ESTADO,
                OpcaoProduto.ICMS_SAIDA_NF,
                OpcaoProduto.NUTRICIONAL,
                OpcaoProduto.ICMS_CONSUMIDOR,
                OpcaoProduto.ICMS_LOJA
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
                OpcaoCliente.RECEBER_CHEQUE,
                OpcaoCliente.CONVENIO_EMPRESA,
                OpcaoCliente.CONVENIO_CONVENIADO,
                OpcaoCliente.CONVENIO_TRANSACAO,
                OpcaoCliente.RECEBER_CREDITOROTATIVO));
    }

    @Override
    public Set<OpcaoFornecedor> getOpcoesDisponiveisFornecedor() {
        return new HashSet<>(Arrays.asList(
                OpcaoFornecedor.DADOS,
                OpcaoFornecedor.ENDERECO,
                OpcaoFornecedor.CONTATOS,
                OpcaoFornecedor.SITUACAO_CADASTRO,
                OpcaoFornecedor.TIPO_EMPRESA,
                OpcaoFornecedor.PAGAR_FORNECEDOR,
                OpcaoFornecedor.PRODUTO_FORNECEDOR,
                OpcaoFornecedor.CONDICAO_PAGAMENTO,
                OpcaoFornecedor.OUTRAS_RECEITAS,
                OpcaoFornecedor.OBSERVACAO,
                OpcaoFornecedor.UTILIZAIVA,
                OpcaoFornecedor.PRAZO_FORNECEDOR
        ));
    }

//    @Override
//    public List<ChequeIMP> getCheques() throws Exception {
//        List<ChequeIMP> Result = new ArrayList<>();
//        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
//            try (ResultSet rs = stm.executeQuery(
//                    ""
//            )) {
//                while (rs.next()) {
//                    ChequeIMP imp = new ChequeIMP();
//
//                    imp.setId(rs.getString("id"));
//                    imp.setDate(rs.getDate("DATA"));
//                    imp.setDataDeposito(rs.getDate("dataDeposito"));
//                    imp.setNumeroCheque(rs.getString("numeroCheque"));
//                    imp.setBanco(rs.getInt("banco"));
//                    imp.setAgencia(rs.getString("agencia"));
//                    imp.setConta(rs.getString("conta"));
//                    imp.setCpf(rs.getString("cpfCnpj"));
//                    imp.setNome(rs.getString("nome"));
//                    imp.setValor(rs.getDouble("valor"));
//                    imp.setTelefone(rs.getString("telefone"));
//
//                    Result.add(imp);
//                }
//            }
//        }
//        return Result;
//    }
    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "trim(Familia) id, \n"
                    + "Familia nomeFamilia \n"
                    + "from familias f"
            )) {
                while (rst.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setDescricao(rst.getString("nomeFamilia"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    int cont = 0;
    @Override
    public List<ConveniadoIMP> getConveniado() throws Exception {
        List<ConveniadoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    " SELECT \n"
                    + " idCliente id,\n"
                    + " NomeCli nome,\n"
                    + " EndCli endereco,\n"
                    + " NumEndCli numero,\n"
                    + " BaiCli bairro,\n"
                    + " CepCli cep,\n"
                    + " CidCli municipio,\n"
                    + " SiglaUFCli uf,\n"
                    + " TipPessoa,\n"
                    + " CnpjCpf cpfcnpj,\n"
                    + " IERG inscestrg,\n"
                    + " FoneCel telefone,\n"
                    + " FoneRes telefone2,\n"
                    + " ObsCli obs,\n"
                    + " DatCad dtcadastro,\n"
                    + " DatNasc dtnasc,\n"
                    + " ComplEndCli complemento,\n"
                    + " Saldo saldo,\n"
                    + " LimCred limite,\n"
                    + " Status ativo\n"
                    + "FROM clientes\n"
                    + "where TipPessoa != 'j'"
            )) {
                while (rs.next()) {
                    ConveniadoIMP imp = new ConveniadoIMP();
                    imp.setId(rs.getString("id"));
                    imp.setNome(rs.getString("nome"));
                    imp.setIdEmpresa("1");
                    imp.setCnpj(String.valueOf(cont++));
                    imp.setConvenioLimite(rs.getDouble("limite"));
                    imp.setSituacaoCadastro(rs.getInt("ativo") == 0 ? SituacaoCadastro.EXCLUIDO : SituacaoCadastro.ATIVO);

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ConvenioEmpresaIMP> getConvenioEmpresa() throws Exception {
        List<ConvenioEmpresaIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	c.NumComanda id,\n"
                    + "	c.MascComanda cnpj_cpf,\n"
                    + "	c.Titulo descritivo,\n"
                    + "	c.Titulo fantasia,\n"
                    + "	1 ativo,\n"
                    + "	c.LograCli endereco,\n"
                    + "	c.NumCli numero,\n"
                    + "	c.BairroCli bairro,\n"
                    + "	c.MuniCli cidade,\n"
                    + "	c.UFCli estado,\n"
                    + "	c.CEPCli cep,\n"
                    + "	c.FoneCli telefone1\n"
                    + "from\n"
                    + "	ppcx.paramh c"
            )) {
                while (rst.next()) {

                    ConvenioEmpresaIMP imp = new ConvenioEmpresaIMP();

                    imp.setId("1");
                    imp.setCnpj(rst.getString("cnpj_cpf"));
                    imp.setRazao(rst.getString("descritivo"));
                    imp.setSituacaoCadastro(rst.getBoolean("ativo") ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("estado"));
                    imp.setCep(rst.getString("cep"));
                    imp.setTelefone(rst.getString("telefone1"));
                    imp.setDataTermino(Utils.getDataAtual());

                    result.add(imp);

                }
            }
        }

        return result;
    }

    @Override
    public List<ConvenioTransacaoIMP> getConvenioTransacao() throws Exception {
        List<ConvenioTransacaoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "r.fkVenda as id,\n"
                    + "r.fkEntidade as cliente,\n"
                    + "dtVenda as emissao,\n"
                    + "dateadd(DAY, 30, r.dtVenda) as vencimento,\n"
                    + "(r.valorVenda - r.valorPago) as valor,\n"
                    + "r.valorJuros as juros,\n"
                    + "r.fkPDV as ecf,\n"
                    + "v.coo numerocupom\n"
                    + "from Comercial.VendaPrazo r\n"
                    + "left join Comercial.Venda v on v.id = r.fkVenda\n"
                    + "join Cadastro.Entidade e on e.id = r.fkEntidade\n"
                    + "where coalesce(r.valorPago, 0) < coalesce(r.valorVenda,0)\n"
                    + "and r.fkCliente = " + getLojaOrigem() + "\n"
                    + "and e.isFuncionario = 1\n"
                    + "ORDER BY r.dtVenda desc"
            )) {
                while (rst.next()) {

                    ConvenioTransacaoIMP imp = new ConvenioTransacaoIMP();
                    imp.setId(rst.getString("id"));
                    imp.setIdConveniado(rst.getString("cliente"));
                    imp.setEcf(rst.getString("ecf"));
                    imp.setNumeroCupom(rst.getString("numerocupom"));
                    imp.setDataHora(rst.getTimestamp("emissao"));
                    imp.setValor(rst.getDouble("valor"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ContaReceberIMP> getContasReceber(Set<OpcaoContaReceber> opt) throws Exception {
        List<ContaReceberIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	 cr.ID,\n"
                    + "	 cr.ID_CADASTRO+2000 id_fornecedor,\n"
                    + "	 tc.REDUZIDO tipo,\n"
                    + "	 EMISSAO dataemissao,\n"
                    + "	 VENCIMENTO,\n"
                    + "	 VALOR,\n"
                    + "	 OBSERVACAO\n"
                    + "FROM\n"
                    + "	 VW_CONTAS cr\n"
                    + "	 JOIN VW_TIPO_CADASTRO tc ON tc.id = cr.TIPO_CADASTRO \n"
                    + "WHERE\n"
                    + "	 EMPRESA =  " + getLojaOrigem() + "\n"
                    + "	 AND TIPO_CADASTRO = 4\n"
                    + "	 AND PAGAMENTO IS NULL\n"
                    + "  AND PARCELA <> 0\n"
                    + "  AND cr.ID_CADASTRO <> 0\n"
                    + "  AND DESC_FORMA_PAGTO <> 'TAXA CARTÕES'\n"
                    + " UNION \n"
                    + "  SELECT\n"
                    + "	 cr.ID,\n"
                    + "	 cr.ID_CADASTRO id_fornecedor,\n"
                    + "	 tc.REDUZIDO tipo,\n"
                    + "	 EMISSAO dataemissao,\n"
                    + "	 VENCIMENTO,\n"
                    + "	 VALOR,\n"
                    + "	 OBSERVACAO\n"
                    + "FROM\n"
                    + "	 VW_CONTAS cr\n"
                    + "	 JOIN VW_TIPO_CADASTRO tc ON tc.id = cr.TIPO_CADASTRO\n"
                    + "WHERE\n"
                    + "	 EMPRESA =  " + getLojaOrigem() + "\n"
                    + "	 AND TIPO_CADASTRO = 2\n"
                    + "	 AND PAGAMENTO IS NULL\n"
                    + "  AND PARCELA <> 0\n"
                    + "  AND TIPO_CONTA = 1\n"
                    + "ORDER BY 1"
            )) {
                while (rst.next()) {
                    ContaReceberIMP imp = new ContaReceberIMP();

                    imp.setId(rst.getString("id"));
                    imp.setIdFornecedor(rst.getString("id_fornecedor"));
                    imp.setDataEmissao(rst.getDate("dataemissao"));
                    imp.setDataVencimento(rst.getDate("vencimento"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setObservacao(rst.getString("observacao"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	distinct \n"
                    + "	c.clasTrib cst,\n"
                    + "	ValAliq aliquota,\n"
                    + "	case\n"
                    + "		when CoefRedu is null then 0\n"
                    + "		else CoefRedu\n"
                    + "	end reducao\n"
                    + "from\n"
                    + "	produtos p\n"
                    + "left join clastributaria c on\n"
                    + "	p.clasTrib = c.clasTrib"
            )) {
                while (rs.next()) {
                    String id = rs.getString("cst") + "-" + rs.getString("aliquota") + "-" + rs.getString("reducao");
                    result.add(new MapaTributoIMP(
                            id,
                            id,
                            rs.getInt("cst"),
                            rs.getDouble("aliquota"),
                            rs.getDouble("reducao")));
                }
            }
        }

        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	DISTINCT \n"
                    + "	trim(Departamento) mercid1,\n"
                    + "	Departamento descri1,\n"
                    + "	trim(Departamento) mercid2,\n"
                    + "	Departamento descri2,\n"
                    + "	trim(Departamento) mercid3,\n"
                    + "	Departamento descri3\n"
                    + "FROM\n"
                    + "	departamentos d "
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
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + " p.idProduto id,\n"
                    + " p.CodigoBarras ean,\n"
                    + " p.NomeProd descricao,\n"
                    + " trim(p.Departamento) mercid1,\n"
                    + " trim(p.Departamento) mercid2,\n"
                    + " trim(p.Departamento) mercid3,\n"
                    + " trim(p.Familia) idFamilia,\n"
                    + " p.UniVenda unidade,\n"
                    + " p.PrcVenda precovenda,\n"
                    + " p.CustoLimpo custosemimposto,\n"
                    + " p.CustoAtual custocomimposto,\n"
                    + " p.Margem margem,\n"
                    + " p.idUltFor COD_FORNEC,\n"
                    + " p.Pesavel BALANCA,\n"
                    + " p.StatusAtv situacao,\n"
                    + " p.CodNCM ncm,\n"
                    + " p.QtdEstoq estoque,\n"
                    + " p.CSTPIS piscofins,\n"
                    + " p.CSTCOFINS piscofins2,\n"
                    + " p.clasTrib cst,\n"
                    + " p.ValAliq aliquota,\n"
                    + " ifnull(p.CoefRedu, 0) reducao,\n"
                    + " p.DatCadProd dtcadastro,\n"
                    + " p.CodCEST cest\n"
                    + " FROM PRODUTOS p"
            )) {
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().getProdutosBalanca();
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("id"));
                    imp.setDescricaoCompleta(rst.getString("descricao"));
                    imp.setDescricaoReduzida(rst.getString("descricao"));
                    imp.setDescricaoGondola(rst.getString("descricao"));
                    imp.setCodMercadologico1(rst.getString("mercid1"));
                    imp.setCodMercadologico2(rst.getString("mercid2"));
                    imp.setCodMercadologico3(rst.getString("mercid3"));
                    imp.setIdFamiliaProduto(rst.getString("idFamilia"));
                    imp.setDataCadastro(rst.getDate("dtcadastro"));
                    imp.setTipoEmbalagem(rst.getString("unidade"));
                    imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setPiscofinsCstCredito(rst.getString("piscofins"));
                    imp.setPiscofinsCstDebito(rst.getString("piscofins"));

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

                    String idAliquota = rst.getString("cst") + "-" + rst.getString("aliquota") + "-" + rst.getString("reducao");

                    imp.setIcmsDebitoId(idAliquota);
                    imp.setIcmsDebitoForaEstadoId(idAliquota);
                    imp.setIcmsDebitoForaEstadoNfId(idAliquota);
                    imp.setIcmsCreditoId(idAliquota);
                    imp.setIcmsCreditoForaEstadoId(idAliquota);
                    imp.setIcmsConsumidorId(idAliquota);

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
                    "SELECT\n"
                    + "	idFornecedor id,\n"
                    + "	NomeFor razao,\n"
                    + "	EndFor endereco,\n"
                    + "	BaiFor bairro,\n"
                    + "	CepFor cep,\n"
                    + "	CidFor municipio,\n"
                    + "	SiglaUFFor uf,\n"
                    + "	NumEndFor numero,\n"
                    + "	FoneCml tel_principal,\n"
                    + "	CnpjCpf cpfcnpj,\n"
                    + "	IERG inscestadual,\n"
                    + "	Apelido fantasia,\n"
                    + "	EmailFor EMAIL,\n"
                    + "	DatCad dtcadastro,\n"
                    + "	Status \n"
                    + "FROM\n"
                    + "	fornecedores"
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
                    imp.setMunicipio(rst.getString("municipio"));
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

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select idFornecedor fornecedorid, idProduto produtoid, idReferencia referencia from tabreferencia"
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

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + "	idProduto id,\n"
                    + "	CodigoBarras ean\n"
                    + "FROM\n"
                    + "	produtos p \n"
                    + "union \n"
                    + "SELECT\n"
                    + "	idProduto id,\n"
                    + "	CodigoAux ean\n"
                    + "FROM\n"
                    + "	produtos p "
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setEan(rst.getString("ean"));

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
                    "SELECT \n"
                    + " idCliente id,\n"
                    + " NomeCli nome,\n"
                    + " EndCli endereco,\n"
                    + " NumEndCli numero,\n"
                    + " BaiCli bairro,\n"
                    + " CepCli cep,\n"
                    + " CidCli municipio,\n"
                    + " SiglaUFCli uf,\n"
                    + " TipPessoa,\n"
                    + " CnpjCpf cpfcnpj,\n"
                    + " IERG inscestrg,\n"
                    + " FoneCel telefone,\n"
                    + " FoneRes telefone2,\n"
                    + " ObsCli obs,\n"
                    + " DatCad dtcadastro,\n"
                    + " DatNasc dtnasc,\n"
                    + " ComplEndCli complemento,\n"
                    + " Saldo saldo,\n"
                    + " LimCred limite,\n"
                    + " Status ativo\n"
                    + "FROM clientes"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rst.getString("id"));
                    imp.setRazao(rst.getString("nome"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setDataNascimento(rst.getDate("dtnasc"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setCnpj(rst.getString("cpfcnpj"));
                    imp.setInscricaoestadual(rst.getString("inscestrg"));
                    imp.setComplemento(rst.getString("complemento"));

                    imp.setTelefone(rst.getString("telefone2"));
                    imp.setCelular(rst.getString("telefone"));
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

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + " idNumConta id,\n"
                    + " IdCliente idcliente,\n"
                    + " NomCli nome,\n"
                    + " case\n"
                    + " 	when NumDoc is null \n"
                    + " 	then idPedVen \n"
                    + " 	else NumDoc\n"
                    + " end numerodocumento,\n"
                    + " DatAtu dataemissao,\n"
                    + " ValARec valor,\n"
                    + " TefNSU N_FISCAL,\n"
                    + " Parcela ,\n"
                    + " DatVenc datavencimento\n"
                    + "FROM conrec where ValRec = 0\n"
                    + "and DatCancela is null"
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

    @Override
    public List<ContaPagarIMP> getContasPagar() throws Exception {
        List<ContaPagarIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT  \n"
                    + " idNumConta id,\n"
                    + " DatVenc dtvencimento,\n"
                    + " idFornec idfornecedor,\n"
                    + " EmissaoDoc dataemissao,\n"
                    + " ValAPagar valor,\n"
                    + " OBSPag obs,\n"
                    + " NumDoc numeroDocumento\n"
                    + "FROM conpag\n"
                    + "where DatPagto is null"
            )) {
                while (rst.next()) {
                    ContaPagarIMP imp = new ContaPagarIMP();

                    imp.setId(rst.getString("id"));
                    imp.setIdFornecedor(rst.getString("idfornecedor"));
                    imp.setNumeroDocumento(rst.getString("numeroDocumento"));
                    imp.setDataEmissao(rst.getDate("dataemissao"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setVencimento(rst.getDate("dtvencimento"));
                    imp.setObservacao(rst.getString("obs"));

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

    @Override
    public Iterator<VendaIMP> getVendaIterator() throws Exception {
        return new AlcanceDAO.VendaIterator(getLojaOrigem(), this.dataInicioVenda, this.dataTerminoVenda);
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        return new AlcanceDAO.VendaItemIterator(getLojaOrigem(), this.dataInicioVenda, this.dataTerminoVenda);
    }

    private static class VendaIterator implements Iterator<VendaIMP> {

        public final static SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");

        private Statement stm = ConexaoMySQL.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaIMP next;
        private Set<String> uk = new HashSet<>();

        private void obterNext() {
            try {
                SimpleDateFormat timestampDate = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat timestamp = new SimpleDateFormat("yyyy-MM-dd hh:mm");

                if (next == null) {
                    if (rst.next()) {
                        next = new VendaIMP();
                        String id = rst.getString("id_venda");
                        if (!uk.add(id)) {
                            LOG.warning("Venda " + id + " já existe na listagem");
                        }
                        next.setId(id);
                        next.setNumeroCupom(Utils.stringToInt(rst.getString("numerocupom")));
                        next.setEcf(Utils.stringToInt(rst.getString("ecf")));
                        next.setData(rst.getDate("data"));

                        String horaInicio = timestampDate.format(rst.getDate("data")) + " " + rst.getString("hora");
                        String horaTermino = timestampDate.format(rst.getDate("data")) + " " + rst.getString("hora");
                        next.setHoraInicio(timestamp.parse(horaInicio));
                        next.setHoraTermino(timestamp.parse(horaTermino));
                        next.setSubTotalImpressora(rst.getDouble("valor"));
                        next.setIdClientePreferencial(rst.getString("id_cliente"));
                        next.setCpf(rst.getString("cpf"));
                        next.setNomeCliente(rst.getString("nomecliente"));
                        next.setCancelado(rst.getBoolean("cancelado"));
                    }
                }
            } catch (SQLException | ParseException ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {

            String strDataInicio = new SimpleDateFormat("yyyy-MM-dd").format(dataInicio);
            String strDataTermino = new SimpleDateFormat("yyyy-MM-dd").format(dataTermino);
            this.sql
                    = "SELECT\n"
                    + "	c.seq id_venda,\n"
                    + "	CASE WHEN c.SEQ IS NULL THEN c.SEQ||'-'||c.NUM_OPER ELSE c.SEQ END numerocupom,\n"
                    + "	c.COD_CAIXA ecf,\n"
                    + "	c.DATA data,\n"
                    + "	c.HORA hora,\n"
                    + "	c.VALORTOTAL valor,\n"
                    + "	c.DESCONTO desconto,\n"
                    + "	c.COD_CLI id_cliente,\n"
                    + "	cc.CGC cpf,\n"
                    + "	cc.NOME nomecliente,\n"
                    + "	CASE WHEN c.SITUACAO = 'A' THEN 0 ELSE 1 END cancelado\n"
                    + "FROM\n"
                    + "	CAIXA c\n"
                    + "	LEFT JOIN CADCLI cc ON c.COD_CLI = cc.CODIGO \n"
                    + "WHERE\n"
                    + "	c.DATA BETWEEN '" + strDataInicio + "' AND '" + strDataTermino + "'\n"
                    + "	AND c.NATUREZA = 500";
            LOG.log(Level.FINE, "SQL da venda: " + sql);
            rst = stm.executeQuery(sql);
        }

        @Override
        public boolean hasNext() {
            obterNext();
            return next != null;
        }

        @Override
        public VendaIMP next() {
            obterNext();
            VendaIMP result = next;
            next = null;
            return result;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported.");
        }
    }

    private static class VendaItemIterator implements Iterator<VendaItemIMP> {

        private Statement stm = ConexaoMySQL.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaItemIMP next;

        private void obterNext() {
            try {
                if (next == null) {
                    if (rst.next()) {
                        next = new VendaItemIMP();

                        next.setVenda(rst.getString("id_venda"));
                        next.setId(rst.getString("id_item"));
                        next.setSequencia(rst.getInt("nritem"));
                        next.setProduto(rst.getString("id_produto"));
                        next.setUnidadeMedida(rst.getString("unidade"));
                        next.setDescricaoReduzida(rst.getString("descricao"));
                        next.setQuantidade(rst.getDouble("quantidade"));
                        next.setPrecoVenda(rst.getDouble("valor"));
                        next.setValorDesconto(rst.getDouble("desconto"));

                    }
                }
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaItemIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {
            this.sql
                    = "WITH teste AS (SELECT NUM_OPER, max(NUMEROITEM) n FROM venda GROUP BY 1)\n"
                    + "SELECT  \n"
                    + " v.NUMEROITEM nritem,\n"
                    + "	c.SEQ id_venda,\n"
                    + "	v.SEQ id_item,\n"
                    + "	v.COD_PROD id_produto,\n"
                    + "	v.NOME_PROD descricao,\n"
                    + "	v.QUANTIDADE quantidade,\n"
                    + "	v.PRECO_VEND valor,\n"
                    + "	v.UNIDADE unidade,\n"
                    + "	(c.DESCONTO/t.n) desconto\n"
                    + "FROM\n"
                    + " VENDA v \n"
                    + " JOIN CAIXA c ON v.NUM_OPER = c.NUM_OPER \n"
                    + " JOIN teste t ON t.num_oper = v.NUM_OPER \n"
                    + "WHERE v.DATA BETWEEN '" + VendaIterator.FORMAT.format(dataInicio) + "' AND '" + VendaIterator.FORMAT.format(dataTermino) + "'\n"
                    + "	AND NATUREZA = 500\n"
                    + "GROUP BY 1, 2, 3, 4, 5, 6, 7 ,8 , 9";
            LOG.log(Level.FINE, "SQL da venda: " + sql);
            rst = stm.executeQuery(sql);
        }

        @Override
        public boolean hasNext() {
            obterNext();
            return next != null;
        }

        @Override
        public VendaItemIMP next() {
            obterNext();
            VendaItemIMP result = next;
            next = null;
            return result;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported.");
        }
    }
}
