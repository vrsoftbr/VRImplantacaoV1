package vrimplantacao2_5.dao.sistema;

import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.Arrays;
import java.util.HashSet;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import static vr.core.utils.StringUtils.LOG;
import vrimplantacao2_5.dao.conexao.ConexaoSqlServer;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.vo.cadastro.financeiro.contareceber.OpcaoContaReceber;
import vrimplantacao2.vo.importacao.ContaReceberIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;

/**
 *
 * @author Alan
 */
public class UPSoftware2_5DAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "UPSOFTWARE";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.DATA_CADASTRO,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.ATIVO,
                OpcaoProduto.DESCONTINUADO,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.ATUALIZAR_SOMAR_ESTOQUE,
                OpcaoProduto.CUSTO,
                OpcaoProduto.CUSTO_COM_IMPOSTO,
                OpcaoProduto.CUSTO_SEM_IMPOSTO,
                OpcaoProduto.MARGEM,
                OpcaoProduto.PRECO,
                OpcaoProduto.FABRICANTE,
                OpcaoProduto.FAMILIA,
                OpcaoProduto.FAMILIA_PRODUTO,
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.ICMS,
                OpcaoProduto.ICMS_ENTRADA,
                OpcaoProduto.ICMS_SAIDA,
                OpcaoProduto.ICMS_CONSUMIDOR,
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                OpcaoProduto.NCM,
                OpcaoProduto.CEST,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.NATUREZA_RECEITA,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.PESO_BRUTO,
                OpcaoProduto.PESO_LIQUIDO,
                OpcaoProduto.QTD_EMBALAGEM_COTACAO,
                OpcaoProduto.QTD_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.TROCA,
                OpcaoProduto.VALIDADE,
                OpcaoProduto.VOLUME_QTD,
                OpcaoProduto.RECEITA
        ));
    }

    @Override
    public Set<OpcaoFornecedor> getOpcoesDisponiveisFornecedor() {
        return new HashSet<>(Arrays.asList(
                OpcaoFornecedor.DADOS,
                OpcaoFornecedor.CONTATOS,
                OpcaoFornecedor.ENDERECO,
                OpcaoFornecedor.SITUACAO_CADASTRO,
                OpcaoFornecedor.TIPO_EMPRESA
        ));
    }

    @Override
    public Set<OpcaoCliente> getOpcoesDisponiveisCliente() {
        return new HashSet<>(Arrays.asList(
                OpcaoCliente.DADOS,
                OpcaoCliente.CONTATOS,
                OpcaoCliente.CLIENTE_EVENTUAL,
                OpcaoCliente.DATA_CADASTRO,
                OpcaoCliente.DATA_NASCIMENTO,
                OpcaoCliente.ENDERECO,
                OpcaoCliente.VALOR_LIMITE,
                OpcaoCliente.VENCIMENTO_ROTATIVO
        ));
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	DISTINCT \n"
                    + "	concat(trim(cf.TRIB_ICMS), '-', cpt.ALIQ_ICMS, '-', cpt.RED_BC_ICMS) id,\n"
                    + "	concat(trim(cf.TRIB_ICMS), '-', cpt.ALIQ_ICMS, '-', cpt.RED_BC_ICMS) descricao,\n"
                    + "	cf.TRIB_ICMS cst,\n"
                    + "	cpt.ALIQ_ICMS icms,\n"
                    + "	cpt.RED_BC_ICMS reducao\n"
                    + "from\n"
                    + "	CE_PRODUTOS_TRIB cpt\n"
                    + "join CE_FISCAL cf on\n"
                    + "	cpt.COD_FISCAL = cf.COD_FISCAL"
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(
                            rst.getString("id"),
                            rst.getString("descricao"),
                            rst.getInt("cst"),
                            rst.getDouble("icms"),
                            rst.getDouble("reducao"))
                    );
                }
            }
        }
        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	DISTINCT p.GRUPO merc1,\n"
                    + "	cg.DESCRICAO desc1,\n"
                    + "	p.CLASSE merc2,\n"
                    + "	cc.DESCRICAO desc2,\n"
                    + "	p.CLASSE merc3,\n"
                    + "	cc.DESCRICAO desc3\n"
                    + "from\n"
                    + "	CE_PRODUTOS p\n"
                    + "left join CE_GRUPOS cg on\n"
                    + "	p.GRUPO = cg.COD_GRUPO\n"
                    + "left join CE_MARCAS cm on\n"
                    + "	p.MARCA = cm.COD_MARCAS\n"
                    + "left join CE_CLASSES cc on\n"
                    + "	p.CLASSE = cc.COD_CLASSES"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());

                    imp.setMerc1ID(rst.getString("merc1"));
                    imp.setMerc1Descricao(rst.getString("desc1"));
                    imp.setMerc2ID(rst.getString("merc2"));
                    imp.setMerc2Descricao(rst.getString("desc2"));
                    imp.setMerc3ID(rst.getString("merc3"));
                    imp.setMerc3Descricao(rst.getString("desc3"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	DISTINCT\n"
                    + "	p.MARCA id,\n"
                    + "	cm.DESCRICAO familia\n"
                    + "from\n"
                    + "	CE_PRODUTOS p\n"
                    + "left join CE_MARCAS cm on\n"
                    + "	p.MARCA = cm.COD_MARCAS"
            )) {
                while (rst.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("id"));
                    imp.setDescricao(rst.getString("familia"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	COD_PROD id,\n"
                    + "	ProCodBar ean,\n"
                    + "	FATOR qtdembalagem,\n"
                    + "	FATORUN embalagem\n"
                    + "from\n"
                    + "	CE_PRODUTOS"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("id"));
                    imp.setEan(rst.getString("ean"));
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                    imp.setTipoEmbalagem(rst.getString("embalagem"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        System.out.println("Na data de desenvolvimento do sistema, não foi encontrado dado de balança");
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT distinct\n"
                    + " p.COD_PROD id,\n"
                    + " p.ProCodBar ean,\n"
                    + " p.descricao descricaocompleta,\n"
                    + " p.ABREV descricaoreduzida,\n"
                    + " p.GRUPO mercid1,\n"
                    + " p.CLASSE mercid2,\n"
                    + " p.CLASSE mercid3,\n"
                    + " p.MARCA familia,\n"
                    + " p.FATORUN unidade,\n"
                    + " p.VR_VENDA precovenda,\n"
                    + " p.VR_COMPRA custosemimposto,\n"
                    + " p.VR_COMPRA custocomimposto,\n"
                    + " p.FABRICANTE codfornecedor,\n"
                    + " p.PESO_BRUTO pesobruto,\n"
                    + " --não encontrado dados de BALANCA,\n"
                    + " CASE WHEN p.DT_DESATIVACAO is null THEN 1\n"
                    + " ELSE 0 END AS situacao,\n"
                    + " p.NBM ncm,\n"
                    + " p.ESTOQUE estoque,\n"
                    + " p.CST_PIS pis,\n"
                    + " p.CST_COFINS cofins,\n"
                    + " concat(trim(f.TRIB_ICMS), '-', pt.ALIQ_ICMS, '-', pt.RED_BC_ICMS) idaliquota,\n"
                    + " p.DT_CADASTRO dtcadastro,\n"
                    + " p.CEST cest\n"
                    + " FROM CE_PRODUTOS p\n"
                    + "LEFT JOIN CE_PRODUTOS_TRIB pt on p.COD_PROD = pt.COD_PROD and pt.COD_FISCAL not in (65, 66, 67)\n"
                    + "left JOIN CE_FISCAL f on f.COD_FISCAL = pt.COD_FISCAL and f.COD_FISCAL not in (65, 66, 67)\n"
                    + "where p.COD_EMP = " + getLojaOrigem()
            )) {
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().getProdutosBalanca();
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("id"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(rst.getString("descricaocompleta"));
                    imp.setCodMercadologico1(rst.getString("mercid1"));
                    imp.setCodMercadologico2(rst.getString("mercid2"));
                    imp.setCodMercadologico3(rst.getString("mercid3"));
                    imp.setDataCadastro(rst.getDate("dtcadastro"));
                    imp.setTipoEmbalagem(rst.getString("unidade"));
                    imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setPiscofinsCstCredito(rst.getString("pis"));
                    imp.setPiscofinsCstDebito(rst.getString("cofins"));
                    imp.setIdFamiliaProduto(rst.getString("familia"));

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
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	c.cod_forn id,\n"
                    + "	c.razao_social razao,\n"
                    + "	c.ENDERECO endereco,\n"
                    + "	c.BAIRRO bairro,\n"
                    + "	c.CEP cep,\n"
                    + "	t.DESCRICAO municipio,\n"
                    + "	c.UF uf,\n"
                    + "	c.NUMERO numero,\n"
                    + "	c.TELEFONES tel_principal,\n"
                    + "	c.FAX,\n"
                    + "	c.CNPJ cpfcnpj,\n"
                    + "	c.IE inscestadual,\n"
                    + "	c.fantasia fantasia,\n"
                    + "	c.EMAIL,\n"
                    + "	c.DT_CADASTRO dtcadastro,\n"
                    + "	c.ATIVO\n"
                    + "FROM\n"
                    + "	TFornecedor c\n"
                    + "join TCIDADES t on c.cidade = t.COD_CID "
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
                    imp.setDatacadastro(getData(rst.getString("dtcadastro")));
                    imp.setAtivo("S".equals(rst.getString("ativo").trim()));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + " c.COD_CLI id,\n"
                    + " c.RAZAO_SOCIAL nome,\n"
                    + " c.ENDERECO endereco,\n"
                    + " c.NUMERO numero,\n"
                    + " c.BAIRRO bairro,\n"
                    + " c.CEP cep,\n"
                    + " t.DESCRICAO municipio,\n"
                    + " c.UF uf,\n"
                    + " c.PESSOA_FJ tipo,\n"
                    + " c.CNPJ cpfcnpj,\n"
                    + " c.IE inscestrg,\n"
                    + " c.TELEFONES telefone,\n"
                    + " c.CELULAR celular,\n"
                    + " c.OBS obs,\n"
                    + " c.DT_CADASTRO dtcadastro,\n"
                    + " c.CliDatNas dtnasc,\n"
                    + " c.COMPLEMENTO complemento,\n"
                    + " CASE \n"
                    + " WHEN c.ATIVO LIKE 'S'\n"
                    + " THEN 1\n"
                    + " ELSE 0\n"
                    + " END ativo,\n"
                    + " c.DT_ALTERACAO DTATUALIZACAO\n"
                    + "FROM\n"
                    + "	TCLIENTES c\n"
                    + "join TCIDADES t on c.CIDADE = t.COD_CID "
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

                    imp.setTelefone(rst.getString("telefone"));

                    imp.setCelular(rst.getString("celular"));
                    imp.setDataCadastro( getData(rst.getString("dtcadastro")));
                    imp.setObservacao(rst.getString("obs"));
                    imp.setAtivo(rst.getBoolean("ativo"));

                    result.add(imp);

                }
            }
        }
        return result;
    }
        
    private Date getData(String format) {
        if (format != null && !"".equals(format.trim())) {
            try {
                if (format.contains("/")) {

                    String[] dataAjustada = format.split("/");
                    if (dataAjustada.length == 3) {
                        String ano = dataAjustada[0];
                        if (ano.length() == 4) {
                            int anoConvert = Integer.parseInt(ano);

                            if (anoConvert > 1000) {
                                SimpleDateFormat ajustarAno = new SimpleDateFormat("yyyy/MM/dd");
                                return ajustarAno.parse(format);
                            }
                        }
                    }

                    SimpleDateFormat ajusteData = new SimpleDateFormat("dd/MM/yyyy");
                    SimpleDateFormat converteData = new SimpleDateFormat("yyyy/MM/dd");
                    return converteData.parse(converteData.format(ajusteData.parse(format)));

                } else if (format.contains("-")) {

                    String[] dataAjustada = format.split("-");
                    if (dataAjustada.length == 3) {
                        String ano = dataAjustada[0];
                        if (ano.length() == 4) {
                            int anoConvert = Integer.parseInt(ano);

                            if (anoConvert > 1000) {
                                SimpleDateFormat ajustarAno = new SimpleDateFormat("yyyy-MM-dd");
                                return ajustarAno.parse(format);
                            }
                        }
                    }

                    SimpleDateFormat ajusteData = new SimpleDateFormat("dd-MM-yyyy");
                    SimpleDateFormat converteData = new SimpleDateFormat("yyyy/MM/dd");
                    return converteData.parse(converteData.format(ajusteData.parse(format.replace("-", "/"))));
                }
                return null;

            } catch (ParseException | NumberFormatException ex) {
                System.out.println("Erro ao analisar a data: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
        return null;
    }

    private Date dataInicioVenda;
    private Date dataTerminoVenda;

    @Override
    public Iterator<VendaIMP> getVendaIterator() throws Exception {
        return new VendaIterator(getLojaOrigem(), this.dataInicioVenda, this.dataTerminoVenda);
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        return new VendaItemIterator(getLojaOrigem(), this.dataInicioVenda, this.dataTerminoVenda);
    }

    public void setDataInicioVenda(Date dataInicioVenda) {
        this.dataInicioVenda = dataInicioVenda;
    }

    public void setDataTerminoVenda(Date dataTerminoVenda) {
        this.dataTerminoVenda = dataTerminoVenda;
    }

    private static class VendaIterator implements Iterator<VendaIMP> {

        public final static SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");

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
                        String id = rst.getString("id_venda");
                        if (!uk.add(id)) {
                            LOG.warning("Venda " + id + " já existe na listagem");
                        }
                        next.setId(id);
                        next.setNumeroCupom(Utils.stringToInt(rst.getString("")));
                        next.setEcf(Utils.stringToInt(rst.getString("")));
                        next.setData(rst.getDate("data"));

                        String horaInicio = timestampDate.format(rst.getDate("data")) + " " + rst.getString("hora");
                        String horaTermino = timestampDate.format(rst.getDate("data")) + " " + rst.getString("hora");
                        next.setHoraInicio(timestamp.parse(horaInicio));
                        next.setHoraTermino(timestamp.parse(horaTermino));
                        next.setSubTotalImpressora(rst.getDouble(""));
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
                    = "";
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

        private Statement stm = ConexaoSqlServer.getConexao().createStatement();
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
                        next.setSequencia(rst.getInt(""));
                        next.setProduto(rst.getString(""));
                        next.setUnidadeMedida(rst.getString(""));
                        next.setCodigoBarras(rst.getString(""));
                        next.setDescricaoReduzida(rst.getString(""));
                        next.setQuantidade(rst.getDouble(""));
                        next.setPrecoVenda(rst.getDouble(""));
                        next.setTotalBruto(rst.getDouble(""));
                        next.setCancelado(rst.getBoolean(""));
                    }
                }
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaItemIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {
            this.sql
                    = "";
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
