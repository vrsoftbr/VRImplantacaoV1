package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.vo.cadastro.oferta.SituacaoOferta;
import vrimplantacao2.vo.cadastro.oferta.TipoOfertaVO;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.importacao.ChequeIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.OfertaIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;

public class AsefeDAO extends InterfaceDAO {

    private static final Logger LOG = Logger.getLogger(AsefeDAO.class.getName());

    @Override
    public String getSistema() {
        return "Asefe";
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT DISTINCT\n"
                    + "M1.CODIGO MERC1, M1.DESCRICAO DESC_MERC1,\n"
                    + "M2.CODIGO_GRUPOS MERC2, M2.DESCRICAO_GRUPOS DESC_MERC2,\n"
                    + "coalesce(M3.Codigo, '1') MERC3,\n"
                    + "coalesce(M3.Descricao, M2.DESCRICAO_GRUPOS) DESC_MERC3\n"
                    + "FROM\n"
                    + "CE_PRODUTOS P\n"
                    + "INNER JOIN CE_SETORES M1 ON M1.CODIGO = P.CODIGOSETOR\n"
                    + "INNER JOIN CE_GRUPOS M2 ON M2.CODIGO_GRUPOS = P.CODGRU_PRODUTOS\n"
                    + "left join SubGrupos M3 on M3.Codigo = p.SubGrupo\n"
                    + "ORDER BY M1.CODIGO, M2.CODIGO_GRUPOS, coalesce(M3.Codigo, '1')"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rst.getString("MERC1"));
                    imp.setMerc1Descricao(rst.getString("DESC_MERC1"));
                    imp.setMerc2ID(rst.getString("MERC2"));
                    imp.setMerc2Descricao(rst.getString("DESC_MERC2"));
                    imp.setMerc3ID(rst.getString("MERC3"));
                    imp.setMerc3Descricao(rst.getString("DESC_MERC3"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + "P.CODBARRA_PRODUTOS, \n"
                    + "P.CODPROD_PRODUTOS, \n"
                    + "P.DESCRICAO_PRODUTOS,\n"
                    + "P.UNIDADE_PRODUTOS, \n"
                    + "P.CODGRU_PRODUTOS, \n"
                    + "P.CUSTO_PRODUTOS, \n"
                    + "P.VENDA_PRODUTOS,\n"
                    + "P.NCM_PRODUTOS, \n"
                    + "P.STPIS, \n"
                    + "P.STCOFINS, \n"
                    + "P.CEST, \n"
                    + "P.CODIGOSETOR, \n"
                    + "P.CODGRU_PRODUTOS,\n"
                    + "P.CODMOD_PRODUTOS, \n"
                    + "coalesce(P.SubGrupo, '1') SubGrupo,\n"
                    + "P.STICMS,\n"
                    + "M.DESCRICAO_MODELOS,\n"
                    + "M.CODTRIB_MODELOS,\n"
                    + "R.VALORREDUCAO,\n"
                    + "P.PRODUTOPESAVEL,\n"
                    + "p.Inutilizado\n"
                    + "FROM CE_PRODUTOS P\n"
                    + "LEFT JOIN CE_MODELOS M ON M.CODIGO_MODELOS = P.CODMOD_PRODUTOS\n"
                    + "LEFT JOIN VW_GERALPRODUTOS P2 ON P2.CodInterno = P.CODPROD_PRODUTOS\n"
                    + "LEFT JOIN CE_REDUCAOICMS R ON R.CODIGO = P2.CodReducao\n"
                    + "ORDER BY CODPROD_PRODUTOS"
            )) {
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().carregarProdutosBalanca();
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("CODPROD_PRODUTOS"));
                    imp.setEan(rst.getString("CODBARRA_PRODUTOS"));
                    imp.setTipoEmbalagem(rst.getString("UNIDADE_PRODUTOS"));
                    imp.setSituacaoCadastro(rst.getInt("Inutilizado") == 1 ? SituacaoCadastro.EXCLUIDO : SituacaoCadastro.ATIVO);
                    imp.setDescricaoCompleta(rst.getString("DESCRICAO_PRODUTOS"));
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setCodMercadologico1(rst.getString("CODIGOSETOR"));
                    imp.setCodMercadologico2(rst.getString("CODGRU_PRODUTOS"));
                    imp.setCodMercadologico3(rst.getString("SubGrupo"));
                    imp.setNcm(rst.getString("NCM_PRODUTOS"));
                    imp.setCest(rst.getString("CEST"));
                    imp.setPiscofinsCstDebito(Integer.parseInt(Utils.formataNumero(rst.getString("STPIS"))));
                    imp.setPiscofinsCstCredito(Integer.parseInt(Utils.formataNumero(rst.getString("STCOFINS"))));
                    imp.setIcmsCst(Integer.parseInt(Utils.formataNumero(rst.getString("STICMS"))));
                    imp.setIcmsAliq(rst.getDouble("CODTRIB_MODELOS"));
                    imp.setIcmsReducao(rst.getDouble("VALORREDUCAO"));

                    ProdutoBalancaVO produtoBalanca;
                    long codigoProduto;
                    codigoProduto = Long.parseLong(imp.getImportId());
                    if (codigoProduto <= Integer.MAX_VALUE) {
                        produtoBalanca = produtosBalanca.get((int) codigoProduto);
                    } else {
                        produtoBalanca = null;
                    }

                    if (produtoBalanca != null) {
                        imp.seteBalanca(true);
                        imp.setValidade(produtoBalanca.getValidade() > 1 ? produtoBalanca.getValidade() : 0);
                    } else {
                        imp.setValidade(0);
                        imp.seteBalanca(false);
                    }

                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<ProdutoIMP> getProdutos(OpcaoProduto opcao) throws Exception {
        if (opcao == OpcaoProduto.CUSTO) {
            List<ProdutoIMP> vResult = new ArrayList<>();
            try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select \n"
                        + "p.CODPROD_PRODUTOS,\n"
                        + "p.DESCRICAO_PRODUTOS,\n"
                        + "pe.Barras, \n"
                        + "pe.Quantidade,\n"
                        + "pe.CustoReal, \n"
                        + "pe.Venda, \n"
                        + "pe.Margem\n"
                        + "from ProdutosEmpresa pe\n"
                        + "inner join CE_PRODUTOS p on p.CODBARRA_PRODUTOS = pe.Barras\n"
                        + "where pe.CodEmpresa = " + getLojaOrigem()
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportSistema(getSistema());
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportId(rst.getString("CODPROD_PRODUTOS"));
                        imp.setCustoComImposto(rst.getDouble("CustoReal"));
                        imp.setCustoSemImposto(imp.getCustoComImposto());
                        vResult.add(imp);
                    }
                }
                return vResult;
            }
        } else if (opcao == OpcaoProduto.PRECO) {
            List<ProdutoIMP> vResult = new ArrayList<>();
            try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select \n"
                        + "p.CODPROD_PRODUTOS,\n"
                        + "p.DESCRICAO_PRODUTOS,\n"
                        + "pe.Barras, \n"
                        + "pe.Quantidade,\n"
                        + "pe.CustoReal, \n"
                        + "pe.Venda, \n"
                        + "pe.Margem\n"
                        + "from ProdutosEmpresa pe\n"
                        + "inner join CE_PRODUTOS p on p.CODBARRA_PRODUTOS = pe.Barras\n"
                        + "where pe.CodEmpresa = " + getLojaOrigem()
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportSistema(getSistema());
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportId(rst.getString("CODPROD_PRODUTOS"));
                        imp.setPrecovenda(rst.getDouble("Venda"));
                        vResult.add(imp);
                    }
                }
                return vResult;
            }
        } else if (opcao == OpcaoProduto.MARGEM) {
            List<ProdutoIMP> vResult = new ArrayList<>();
            try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select \n"
                        + "p.CODPROD_PRODUTOS,\n"
                        + "p.DESCRICAO_PRODUTOS,\n"
                        + "pe.Barras, \n"
                        + "pe.Quantidade,\n"
                        + "pe.CustoReal, \n"
                        + "pe.Venda, \n"
                        + "pe.Margem\n"
                        + "from ProdutosEmpresa pe\n"
                        + "inner join CE_PRODUTOS p on p.CODBARRA_PRODUTOS = pe.Barras\n"
                        + "where pe.CodEmpresa = " + getLojaOrigem()
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportSistema(getSistema());
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportId(rst.getString("CODPROD_PRODUTOS"));
                        imp.setMargem(rst.getDouble("Margem"));
                        vResult.add(imp);
                    }
                }
                return vResult;
            }
        } else if (opcao == OpcaoProduto.ESTOQUE) {
            List<ProdutoIMP> vResult = new ArrayList<>();
            try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select \n"
                        + "p.CODPROD_PRODUTOS,\n"
                        + "p.DESCRICAO_PRODUTOS,\n"
                        + "pe.Barras, \n"
                        + "pe.Quantidade,\n"
                        + "pe.CustoReal, \n"
                        + "pe.Venda, \n"
                        + "pe.Margem\n"
                        + "from ProdutosEmpresa pe\n"
                        + "inner join CE_PRODUTOS p on p.CODBARRA_PRODUTOS = pe.Barras\n"
                        + "where pe.CodEmpresa = " + getLojaOrigem()
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("CODPROD_PRODUTOS"));
                        imp.setEstoque(rst.getDouble("Quantidade"));
                        vResult.add(imp);
                    }
                }
                return vResult;
            }
        }
        return null;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "CodCliente, "
                    + "Carteira, "
                    + "NomeCliente, "
                    + "EnderecoCliente, "
                    + "BairroCliente, \n"
                    + "CidadeCliente, "
                    + "CepCliente, "
                    + "CpfCliente, "
                    + "RgCliente, "
                    + "LimiteCheque, "
                    + "LimiteCliente,\n"
                    + "TelCliente, "
                    + "CelCliente, "
                    + "Situacao, "
                    + "Datanascimento, "
                    + "Pessoas_autorizadas,\n"
                    + "Saldo, "
                    + "Cheque, "
                    + "Fiado, "
                    + "CodigoConvenio, "
                    + "Matricula, "
                    + "SENHA, "
                    + "UF, "
                    + "NUMERO, "
                    + "CodUf,\n"
                    + "email, "
                    + "CodMunicipio, "
                    + "DiasVencimento, "
                    + "Sexo, "
                    + "DiasVencimentoCheque, "
                    + "SituacaoCheque,\n"
                    + "DiasCarenciaCheque, "
                    + "ChaveAcesso, "
                    + "OrgaoPublico, "
                    + "Telefone1, "
                    + "Telefone2, "
                    + "Fax, "
                    + "Obs\n"
                    + "from CC_Clientes\n"
                    + "order by CodCliente"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("CodCliente"));
                    imp.setRazao(rst.getString("NomeCliente"));
                    imp.setFantasia(rst.getString("NomeCliente"));
                    imp.setEndereco(rst.getString("EnderecoCliente"));
                    imp.setNumero(rst.getString("NUMERO"));
                    imp.setBairro(rst.getString("BairroCliente"));
                    imp.setMunicipio(rst.getString("CidadeCliente"));
                    imp.setMunicipioIBGE(rst.getInt("CodMunicipio"));
                    imp.setUf(rst.getString("UF"));
                    imp.setUfIBGE(rst.getInt("CodUf"));
                    imp.setCep(rst.getString("CepCliente"));
                    imp.setCnpj(rst.getString("CpfCliente"));
                    imp.setInscricaoestadual(rst.getString("RgCliente"));
                    imp.setValorLimite(rst.getDouble("LimiteCliente"));
                    imp.setTelefone(rst.getString("TelCliente"));
                    imp.setCelular(rst.getString("CelCliente"));
                    imp.setEmail(rst.getString("email"));
                    imp.setPermiteCheque((rst.getInt("Cheque") != 0));
                    imp.setPermiteCreditoRotativo((rst.getInt("Fiado") != 0));
                    imp.setObservacao(rst.getString("Obs"));
                    imp.setDataNascimento(rst.getDate("Datanascimento"));

                    if ((rst.getString("Telefone1") != null)
                            && (!rst.getString("Telefone1").trim().isEmpty())) {
                        imp.addContato(
                                "1",
                                "TELEFONE 1",
                                rst.getString("Telefone1").trim(),
                                null,
                                null
                        );
                    }
                    if ((rst.getString("Telefone2") != null)
                            && (!rst.getString("Telefone2").trim().isEmpty())) {
                        imp.addContato(
                                "2",
                                "TELEFONE 2",
                                rst.getString("Telefone2").trim(),
                                null,
                                null
                        );
                    }
                    if ((rst.getString("Fax") != null)
                            && (!rst.getString("Fax").trim().isEmpty())) {
                        imp.addContato(
                                "2",
                                "FAX",
                                rst.getString("Fax").trim(),
                                null,
                                null
                        );
                    }
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + "C.Codigo, \n"
                    + "C.Data, \n"
                    + "C.ValorTotal, \n"
                    + "C.CodCliente,\n"
                    + "C.Coo, \n"
                    + "C.NumImpFiscal, \n"
                    + "C.NumeroCaixa,\n"
                    + "P.Numero,\n"
                    + "P.Juros,\n"
                    + "P.Valor,\n"
                    + "P.ValorRestante,\n"
                    + "ValorSemJuros,\n"
                    + "p.ValorPagamento,\n"
                    + "(p.ValorSemJuros - p.ValorPagamento) valorConta,\n"
                    + "P.Data,\n"
                    + "P.DataVencimento\n"
                    + "FROM VendasCrediario C\n"
                    + "INNER JOIN ParcelasCrediario P ON P.CodVenda = C.Codigo  \n"
                    + "WHERE P.ValorRestante <= Valor\n"
                    + "and p.ValorRestante > 0\n"
                    + "AND C.CodEmpresa = " + getLojaOrigem() + "\n"
                    + "ORDER BY C.Data"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rst.getString("Codigo"));
                    imp.setIdCliente(rst.getString("CodCliente"));
                    imp.setNumeroCupom(rst.getString("Coo"));
                    imp.setEcf(rst.getString("NumeroCaixa"));
                    imp.setValor(rst.getDouble("valorConta"));
                    imp.setParcela(rst.getInt("Numero"));
                    imp.setDataEmissao(rst.getDate("Data"));
                    imp.setDataVencimento(rst.getDate("DataVencimento"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<ChequeIMP> getCheques() throws Exception {
        List<ChequeIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "ch.Codigo_Cheque, ch.Codcli_Cheque,\n"
                    + "ch.Numero_Cheque, ch.Agencia_Cheque,\n"
                    + "ch.Banco_Cheque, ch.DataEmissao_Cheque,\n"
                    + "ch.DataVencimento_Cheque, ch.Valor_Cheque,\n"
                    + "ch.Nomerepasse_Cheque, ch.Cpfrepasse_Cheque,\n"
                    + "ch.Telrepasse_Cheque, ch.Conta_Cheque, ch.CpfCheque,\n"
                    + "ch.CpfCliente, ch.NomeCliente, ch.Compensado, ch.CMC7,\n"
                    + "ch.DataCadastro_Cheque, ch.DataUltAlteracao, ch.Telefone,\n"
                    + "cli.NomeCliente cliente, cli.CpfCliente cpf, cli.RgCliente rg,\n"
                    + "case ch.codhistorico_Cheque \n"
                    + "when 1 then 'CHEQUE OK'\n"
                    + "when 2 then 'CHEQUE BLOQUEADO' end historico\n"
                    + "from CC_Cheques ch \n"
                    + "left join CC_Clientes cli on cli.CodCliente = ch.Codcli_Cheque\n"
                    + "where ch.CodEmpresa = " + getLojaOrigem() + "\n"
                    + "order by Codigo_Cheque"
            )) {
                while (rst.next()) {
                    ChequeIMP imp = new ChequeIMP();
                    imp.setId(rst.getString("Codigo_Cheque"));
                    imp.setNumeroCheque(rst.getString("Numero_Cheque"));
                    imp.setBanco(Integer.parseInt(Utils.formataNumero(rst.getString("Banco_Cheque"))));
                    imp.setAgencia(rst.getString("Agencia_Cheque"));
                    imp.setConta(rst.getString("Conta_Cheque"));
                    imp.setNome(rst.getString("cliente"));
                    imp.setCpf(rst.getString("cpf"));
                    imp.setRg(rst.getString("rg"));
                    imp.setTelefone(rst.getString("Telefone"));
                    imp.setCmc7(rst.getString("CMC7"));
                    imp.setDataHoraAlteracao(rst.getTimestamp("DataUltAlteracao"));
                    imp.setDate(rst.getDate("DataEmissao_Cheque"));
                    imp.setDataDeposito(rst.getDate("DataVencimento_Cheque"));
                    imp.setValor(rst.getDouble("Valor_Cheque"));
                    imp.setAlinea(0);
                    vResult.add(imp);
                }
            }
            return vResult;
        }
    }

    @Override
    public List<OfertaIMP> getOfertas(Date dataTermino) throws Exception {
        if (dataTermino == null) {
            dataTermino = new Date();
        }
        List<OfertaIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "o.CODIGO, o.DataInicial, o.DataFinal,\n"
                    + "op.PRECOPROMOCAO_PRODUTOS,\n"
                    + "p.CODPROD_PRODUTOS, op.DESCRICAO\n"
                    + "from vwPromocao o \n"
                    + "inner join vwPromocaoProdutos op on op.CODIGO_PROMOCAO = o.CODIGO\n"
                    + "inner join CE_PRODUTOS p on p.CODBARRA_PRODUTOS = op.CODIGOBARRAS_PRODUTOS\n"
                    + "where o.DataFinal > GETDATE()"
            )) {
                while (rst.next()) {
                    OfertaIMP imp = new OfertaIMP();

                    imp.setIdProduto(rst.getString("CODPROD_PRODUTOS"));
                    imp.setDataInicio(rst.getDate("DataInicial"));
                    imp.setDataFim(rst.getDate("DataFinal"));
                    imp.setPrecoOferta(rst.getDouble("PRECOPROMOCAO_PRODUTOS"));
                    imp.setSituacaoOferta(SituacaoOferta.ATIVO);
                    imp.setTipoOferta(TipoOfertaVO.CAPA);

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
        return new AsefeDAO.VendaIterator(getLojaOrigem(), dataInicioVenda, dataTerminoVenda);
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        return new AsefeDAO.VendaItemIterator(getLojaOrigem(), dataInicioVenda, dataTerminoVenda);
    }

    private static class VendaIterator implements Iterator<VendaIMP> {

        private final static SimpleDateFormat FORMAT = new SimpleDateFormat("dd/MM/yyyy");

        private Statement stm = ConexaoSqlServer.getConexao().createStatement();
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
                        String id = rst.getString("id") + "-" + rst.getString("numerocupom") + "-" + rst.getString("ecf") + "-" + rst.getString("data");
                        if (!uk.add(id)) {
                            LOG.warning("Venda " + id + " já existe na listagem");
                        }
                        next.setId(id);
                        next.setNumeroCupom(Utils.stringToInt(rst.getString("numerocupom")));
                        next.setEcf(Utils.stringToInt(rst.getString("ecf")));
                        next.setData(rst.getDate("data"));                        

                        String endereco;

                        if ((rst.getString("vc_clientepreferencial") != null)
                                && (!rst.getString("vc_clientepreferencial").trim().isEmpty())) {

                            if (!"0".equals(rst.getString("vc_clientepreferencial").trim())) {

                                next.setCpf(rst.getString("cli_cpf"));
                                next.setNomeCliente(rst.getString("cli_nomecliente"));
                                next.setIdClientePreferencial(rst.getString("vc_clientepreferencial"));

                                endereco
                                        = Utils.acertarTexto(rst.getString("cli_endereco")) + ","
                                        + Utils.acertarTexto(rst.getString("cli_numero")) + ","
                                        + Utils.acertarTexto(rst.getString("complemento")) + ","
                                        + Utils.acertarTexto(rst.getString("cli_bairro")) + ","
                                        + Utils.acertarTexto(rst.getString("cli_cidade")) + "-"
                                        + Utils.acertarTexto(rst.getString("cli_estado")) + ","
                                        + Utils.acertarTexto(rst.getString("cli_cep"));
                                next.setEnderecoCliente(endereco);
                            } else {

                                next.setCpf(rst.getString("vc_cpf"));
                                next.setNomeCliente(rst.getString("vc_nomecliente"));
                                next.setIdClientePreferencial(null);

                                endereco
                                        = Utils.acertarTexto(rst.getString("vc_endereco")) + ","
                                        + Utils.acertarTexto(rst.getString("vc_numero")) + ","
                                        + Utils.acertarTexto(rst.getString("complemento")) + ","
                                        + Utils.acertarTexto(rst.getString("vc_bairro")) + ","
                                        + Utils.acertarTexto(rst.getString("vc_cidade")) + "-"
                                        + Utils.acertarTexto(rst.getString("vc_estado")) + ","
                                        + Utils.acertarTexto(rst.getString("vc_cep"));
                                next.setEnderecoCliente(endereco);
                            }
                        } else {

                            next.setCpf(rst.getString("vc_cpf"));
                            next.setNomeCliente(rst.getString("vc_nomecliente"));
                            next.setIdClientePreferencial(null);

                            endereco
                                    = Utils.acertarTexto(rst.getString("vc_endereco")) + ","
                                    + Utils.acertarTexto(rst.getString("vc_numero")) + ","
                                    + Utils.acertarTexto(rst.getString("complemento")) + ","
                                    + Utils.acertarTexto(rst.getString("vc_bairro")) + ","
                                    + Utils.acertarTexto(rst.getString("vc_cidade")) + "-"
                                    + Utils.acertarTexto(rst.getString("vc_estado")) + ","
                                    + Utils.acertarTexto(rst.getString("vc_cep"));
                            next.setEnderecoCliente(endereco);
                        }

                        String horaInicio = timestampDate.format(rst.getDate("data")) + " " + rst.getString("horainicio");
                        String horaTermino = timestampDate.format(rst.getDate("data")) + " " + rst.getString("horatermino");
                        next.setHoraInicio(timestamp.parse(horaInicio));
                        next.setHoraTermino(timestamp.parse(horaTermino));
                        next.setCancelado(rst.getBoolean("cancelado"));
                        next.setSubTotalImpressora(rst.getDouble("subtotalimpressora"));
                        next.setValorDesconto(rst.getDouble("desconto"));
                        next.setValorAcrescimo(rst.getDouble("acrescimo"));
                        next.setNumeroSerie(rst.getString("numeroserie"));
                        next.setModeloImpressora(rst.getString("modelo"));
                    }
                }
            } catch (SQLException | ParseException ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {
            this.sql
                    = "select \n"
                    + "vc.Codigo as id,\n"
                    + "vc.COO as numerocupom,\n"
                    + "vc.NumeroCaixa as ecf,\n"
                    + "vc.Data as data,\n"
                    + "vc.CodCliente as vc_clientepreferencial,\n"
                    + "cli.CodCliente as cli_clientepreferencial,\n"
                    + "MIN(CONVERT(nvarchar(5), Data, 108)) as horainicio,\n"
                    + "MAX(CONVERT(nvarchar(5), Data, 108)) as horatermino,\n"
                    + "'N' as cancelado,\n"
                    + "vc.ValorTotal as subtotalimpressora,\n"
                    + "vc.CPFConsumidor as vc_cpf,\n"
                    + "cli.CpfCliente as cli_cpf,\n"
                    + "ISNULL(vc.DescAcr, 0) as desconto,\n"
                    + "0 as acrescimo,\n"
                    + "vc.NomeConsumidor as vc_nomecliente,\n"
                    + "cli.NomeCliente as cli_nomecliente,\n"
                    + "vc.Endereco as vc_endereco,\n"
                    + "cli.EnderecoCliente as cli_endereco,\n"
                    + "'' as vc_numero,\n"
                    + "cli.NUMERO as cli_numero,\n"
                    + "'' as complemento,\n"
                    + "vc.Bairro as vc_bairro,\n"
                    + "cli.BairroCliente as cli_bairro,\n"
                    + "vc.Cidade as vc_cidade,\n"
                    + "cli.CidadeCliente as cli_cidade,\n"
                    + "'' as vc_estado,\n"
                    + "cli.UF as cli_estado,\n"
                    + "vc.Cep as vc_cep,\n"
                    + "cli.CepCliente as cli_cep,\n"
                    + "(SELECT \n"
                    + "	DISTINCT\n"
                    + "	SerieECF\n"
                    + "	FROM dbo.ExpCucaPrincipal\n"
                    + "	WHERE \n"
                    + "	(Data = (SELECT MAX(Data) AS Expr1\n"
                    + "					FROM dbo.ExpCucaPrincipal AS AUX\n"
                    + "                   WHERE (SerieECF = dbo.ExpCucaPrincipal.SerieECF)))\n"
                    + "                   and OrdemECF = vc.OrdemECF\n"
                    + "                   and CodEmpresa = " + idLojaCliente + "\n"
                    + ") as numeroserie,\n"
                    + "(SELECT \n"
                    + "	DISTINCT\n"
                    + "	ECF_Mod\n"
                    + "	FROM dbo.ExpCucaPrincipal\n"
                    + "	WHERE \n"
                    + "	(Data = (SELECT MAX(Data) AS Expr1\n"
                    + "					FROM dbo.ExpCucaPrincipal AS AUX\n"
                    + "                   WHERE (SerieECF = dbo.ExpCucaPrincipal.SerieECF)))\n"
                    + "                   and OrdemECF = vc.OrdemECF\n"
                    + "                   and CodEmpresa = " + idLojaCliente + "\n"
                    + ") as modelo,\n"
                    + "'' as marca\n"
                    + "from CE_VendasCaixa vc \n"
                    + "left join CONTROLE_CLIENTES.dbo.CC_Clientes cli on cli.CodCliente = vc.CodCliente\n"
                    + "where (vc.Data between CONVERT(datetime, '" + FORMAT.format(dataInicio) + "', 103)\n"
                    + "		and CONVERT(datetime, '" + FORMAT.format(dataTermino) + "', 103))\n"
                    + "and vc.CodEmpresa = " + idLojaCliente + "\n"
                    + "group by \n"
                    + "vc.Codigo,\n"
                    + "vc.COO, \n"
                    + "vc.NumeroCaixa, \n"
                    + "vc.Data, \n"
                    + "vc.CodCliente,\n"
                    + "cli.CodCliente,\n"
                    + "vc.ValorTotal,\n"
                    + "vc.CPFConsumidor,\n"
                    + "cli.CpfCliente,\n"
                    + "vc.DescAcr,\n"
                    + "vc.NomeConsumidor,\n"
                    + "cli.NomeCliente,\n"
                    + "vc.Endereco,\n"
                    + "cli.EnderecoCliente,\n"
                    + "cli.NUMERO,\n"
                    + "vc.Bairro,\n"
                    + "cli.BairroCliente,\n"
                    + "vc.Cidade,\n"
                    + "cli.CidadeCliente,\n"
                    + "cli.UF,\n"
                    + "vc.Cep,\n"
                    + "cli.CepCliente,\n"
                    + "vc.OrdemECF";
            
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

        private final static SimpleDateFormat FORMAT = new SimpleDateFormat("dd/MM/yyyy");

        private Statement stm = ConexaoSqlServer.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaItemIMP next;

        private void obterNext() {
            try {
                if (next == null) {
                    if (rst.next()) {
                        next = new VendaItemIMP();
                        String idVenda = rst.getString("id_venda") + "-" + rst.getString("numerocupom") + "-" + rst.getString("ecf") + "-" + rst.getString("data");
                        String id = rst.getString("id") + "-" + rst.getString("id_venda") + "-" + rst.getString("numerocupom") + "-" + rst.getString("ecf") + "-" + rst.getString("data");
                                
                        next.setId(id);
                        next.setVenda(idVenda);
                        next.setProduto(rst.getString("produto"));
                        next.setDescricaoReduzida(rst.getString("descricao"));
                        next.setQuantidade(rst.getDouble("quantidade"));
                        next.setTotalBruto(rst.getDouble("total"));
                        next.setValorDesconto(rst.getDouble("desconto"));
                        next.setValorAcrescimo(rst.getDouble("acrescimo"));
                        next.setCancelado(rst.getBoolean("cancelado"));
                        next.setCodigoBarras(rst.getString("codigobarras"));
                        next.setUnidadeMedida(rst.getString("unidade"));

                        String trib = Utils.acertarTexto(rst.getString("codaliq_venda"));
                        if (trib == null || "".equals(trib)) {
                            trib = Utils.acertarTexto(rst.getString("codaliq_produto"));
                        }

                        obterAliquota(next, trib);
                    }
                }
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        /**
         * Método temporario, desenvolver um mapeamento eficiente da tributação.
         *
         * @param item
         * @throws SQLException
         */
        public void obterAliquota(VendaItemIMP item, String icms) throws SQLException {
            /*
             0700   7.00    ALIQUOTA 07%
             1200   12.00   ALIQUOTA 12%
             1800   18.00   ALIQUOTA 18%
             2500   25.00   ALIQUOTA 25%
             1100   11.00   ALIQUOTA 11%
             I      0.00    ISENTO
             F      0.00    SUBST TRIBUTARIA
             N      0.00    NAO INCIDENTE
             */
            int cst;
            double aliq;
            switch (icms) {
                case "0700":
                    cst = 0;
                    aliq = 7;
                    break;
                case "1200":
                    cst = 0;
                    aliq = 12;
                    break;
                case "1800":
                    cst = 0;
                    aliq = 18;
                    break;
                case "2500":
                    cst = 0;
                    aliq = 25;
                    break;
                case "1100":
                    cst = 0;
                    aliq = 11;
                    break;
                case "F":
                    cst = 60;
                    aliq = 0;
                    break;
                case "N":
                    cst = 41;
                    aliq = 0;
                    break;
                default:
                    cst = 40;
                    aliq = 0;
                    break;
            }
            item.setIcmsCst(cst);
            item.setIcmsAliq(aliq);
        }

        public VendaItemIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {
            this.sql
                    = "select \n"
                    + "mov.cod_mov as id,\n"
                    + "vc.Codigo as id_venda,\n"
                    + "vc.COO as numerocupom,\n"
                    + "vc.NumeroCaixa as ecf,\n"
                    + "vc.Data as data,\n"
                    + "pro.CODPROD_PRODUTOS as produto,\n"
                    + "pro.DescricaoCompleta as descricao,\n"
                    + "ISNULL(mov.qtd_mov, 0) quantidade,\n"
                    + "ISNULL(mov.venda_mov, 0) as total,\n"
                    + "mov.Cancelada as cancelado,\n"
                    + "0 as desconto,\n"
                    + "0 as acrescimo,\n"
                    + "mov.codbarra_mov as codigobarras,\n"
                    + "pro.UNIDADE_PRODUTOS as unidade,\n"
                    + "mov.S_Trib_Aliquota codaliq_venda,\n"
                    + "'I' as codaliq_produto,\n"
                    + "'' as trib_desc\n"
                    + "from CE_VendasCaixa vc\n"
                    + "inner join CE_MOVIMENTACAO mov on mov.caixa_mov = vc.NumeroCaixa and vc.COO = mov.coo\n"
                    + "inner join CE_PRODUTOS pro on pro.CODBARRA_PRODUTOS = mov.codbarra_mov \n"
                    + "where vc.CodEmpresa = " + idLojaCliente + "\n"
                    + "and (vc.Data between CONVERT(datetime, '" + FORMAT.format(dataInicio) + "', 103)\n"
                    + "		and CONVERT(datetime, '" + FORMAT.format(dataTermino) + "', 103))";

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
