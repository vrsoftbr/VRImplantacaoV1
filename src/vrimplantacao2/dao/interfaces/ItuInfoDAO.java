package vrimplantacao2.dao.interfaces;

import vrimplantacao2.dao.cadastro.*;
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
import java.util.Set;
import java.util.logging.Level;
import static vr.core.utils.StringUtils.LOG;
import vrimplantacao.classe.ConexaoPostgres;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ChequeIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;

public class ItuInfoDAO extends InterfaceDAO implements MapaTributoProvider {

    private String complemento = "";

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    @Override
    public String getSistema() {
        return "ItuInfo" + (!complemento.equals("") ? " - " + complemento : "");
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                new OpcaoProduto[]{
                    OpcaoProduto.FAMILIA,
                    OpcaoProduto.FAMILIA_PRODUTO,
                    OpcaoProduto.MERCADOLOGICO_PRODUTO,
                    OpcaoProduto.MERCADOLOGICO,
                    OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                    OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                    OpcaoProduto.PRODUTOS,
                    OpcaoProduto.EAN,
                    OpcaoProduto.EAN_EM_BRANCO,
                    OpcaoProduto.DATA_CADASTRO,
                    OpcaoProduto.TIPO_EMBALAGEM_EAN,
                    OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                    OpcaoProduto.PESAVEL,
                    OpcaoProduto.VALIDADE,
                    OpcaoProduto.DESC_COMPLETA,
                    OpcaoProduto.DESC_GONDOLA,
                    OpcaoProduto.DESC_REDUZIDA,
                    OpcaoProduto.ESTOQUE_MAXIMO,
                    OpcaoProduto.ESTOQUE_MINIMO,
                    OpcaoProduto.PRECO,
                    OpcaoProduto.CUSTO,
                    OpcaoProduto.ESTOQUE,
                    OpcaoProduto.ATIVO,
                    OpcaoProduto.NCM,
                    OpcaoProduto.CEST,
                    OpcaoProduto.PIS_COFINS,
                    OpcaoProduto.NATUREZA_RECEITA,
                    OpcaoProduto.ICMS,
                    OpcaoProduto.ICMS_SAIDA,
                    OpcaoProduto.ICMS_SAIDA_FORA_ESTADO,
                    OpcaoProduto.ICMS_SAIDA_NF,
                    OpcaoProduto.ICMS_ENTRADA,
                    OpcaoProduto.ICMS_CONSUMIDOR,
                    OpcaoProduto.ICMS_ENTRADA_FORA_ESTADO,
                    OpcaoProduto.PAUTA_FISCAL,
                    OpcaoProduto.PAUTA_FISCAL_PRODUTO,
                    OpcaoProduto.EXCECAO,
                    OpcaoProduto.MARGEM,
                    OpcaoProduto.MAPA_TRIBUTACAO,
                    OpcaoProduto.OFERTA
                }
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
                OpcaoFornecedor.TELEFONE,
                OpcaoFornecedor.CEP,
                OpcaoFornecedor.PRODUTO_FORNECEDOR
        ));
    }

    @Override
    public Set<OpcaoCliente> getOpcoesDisponiveisCliente() {
        return new HashSet<>(Arrays.asList(
                OpcaoCliente.DADOS,
                OpcaoCliente.RAZAO,
                OpcaoCliente.TELEFONE,
                OpcaoCliente.CEP,
                OpcaoCliente.RECEBER_CREDITOROTATIVO
        ));
    }

    private String getAliquotaKey(String cst, double aliq, double red) throws SQLException {
        return String.format(
                "%s-%.2f-%.2f",
                cst,
                aliq,
                red
        );
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	distinct\n"
                    + "	case  \n"
                    + "		when indiceicms = '' then 0\n"
                    + "		when indiceicms like '%0%' then 0\n"
                    + "		when indiceicms like 'FF-%' then 60\n"
                    + "		when indiceicms like 'NN-%' then 41\n"
                    + "		else 40\n"
                    + "	end cst_icms,\n"
                    + "	tributoe aliq_icms,\n"
                    + " 0 as red_icms,"
                    + "	case\n"
                    + "		when indiceicms = '' then 0\n"
                    + "		when indiceicms like '%0%' then 0\n"
                    + "		when indiceicms like 'FF-%' then 60\n"
                    + "		when indiceicms like 'NN-%' then 41\n"
                    + "		else 40\n"
                    + "	end || '-' || tributoe || '-' || '0' as descricao\n"
                    + "from\n"
                    + "	produtos p\n"
                    + "where\n"
                    + "	tributoe is not null\n"
                    + "order by 3"
            )) {
                while (rs.next()) {
                    String id = getAliquotaKey(
                            rs.getString("cst_icms"),
                            rs.getDouble("aliq_icms"),
                            rs.getDouble("red_icms")
                    );
                    result.add(new MapaTributoIMP(
                            id,
                            id,
                            Utils.stringToInt(rs.getString("cst_icms")),
                            rs.getDouble("aliq_icms"),
                            rs.getDouble("red_icms")
                    ));
                }
            }
        }
        return result;
    }

    public List<Estabelecimento> getLojaCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	código as id,\n"
                    + "	fantasia descricao,\n"
                    + "	cnpj\n"
                    + "from\n"
                    + "	sistema"
            )) {
                while (rs.next()) {
                    result.add(new Estabelecimento(rs.getString("id"), rs.getString("descricao")));
                }
            }
        }
        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	codigo as codmerc1,\n"
                    + "	setor as descmerc1,\n"
                    + "	codigo as codmerc2,\n"
                    + "	setor as descmerc2,\n"
                    + "	codigo as codmerc3,\n"
                    + "	setor as descmerc3\n"
                    + "from\n"
                    + "	setor\n"
                    + "order by 1"
            )) {
                while (rst.next()) {

                    MercadologicoIMP imp = new MercadologicoIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());

                    imp.setMerc1ID(rst.getString("codmerc1"));
                    imp.setMerc1Descricao(rst.getString("descmerc1"));
                    imp.setMerc2ID(rst.getString("codmerc2"));
                    imp.setMerc2Descricao(rst.getString("descmerc2"));
                    imp.setMerc3ID(rst.getString("codmerc3"));
                    imp.setMerc3Descricao(rst.getString("descmerc3"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	id,\n"
                    + "	codigobarra,\n"
                    + "	produto as descricao,\n"
                    + "	case\n"
                    + "	  when mgv5 = 1 then 'KG'\n"
                    + "	  else 'UN'\n"
                    + "	end unidade,\n"
                    + "	vrcompra as custosemimposto,\n"
                    + "	vrcompra as custocomimposto,\n"
                    + "	vrvenda as precovenda,\n"
                    + " lucro margem,"
                    + "	codigosetor as mercadologico1,\n"
                    + "	codigosetor as mercadologico2,\n"
                    + "	codigosetor as mercadologico3,\n"
                    + "	estoqueminimo,\n"
                    + "	estoqueatual as estoque,\n"
                    + "	mgv5 as e_balanca,\n"
                    + "	ncm,\n"
                    + "	cest,\n"
                    + "	case \n"
                    + "	  when indiceicms = '' then 0\n"
                    + "	  when indiceicms like '%0%' then 0\n"
                    + "	  when indiceicms like 'FF-%' then 60\n"
                    + "	  when indiceicms like 'NN-%' then 41\n"
                    + "	  else 40\n"
                    + "	end cst_icms,\n"
                    + "	tributoe aliq_icms,\n"
                    + "	0 red_icms\n"
                    + "from\n"
                    + "	produtos\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("id"));
                    imp.setEan(rst.getString("codigobarra"));
                    imp.setDescricaoCompleta(rst.getString("descricao"));
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setTipoEmbalagem(rst.getString("unidade"));

                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setMargem(rst.getDouble("margem"));

                    imp.setCodMercadologico1(rst.getString("mercadologico1"));
                    imp.setCodMercadologico2(rst.getString("mercadologico2"));
                    imp.setCodMercadologico3(rst.getString("mercadologico3"));
                    imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
                    imp.setEstoque(Utils.stringToDouble(rst.getString("estoque")));

                    imp.seteBalanca(rst.getBoolean("e_balanca"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));

                    String idIcms = getAliquotaKey(
                            rst.getString("cst_icms"),
                            rst.getDouble("aliq_icms"),
                            rst.getDouble("red_icms")
                    );

                    imp.setIcmsDebitoId(idIcms);
                    imp.setIcmsDebitoForaEstadoId(idIcms);
                    imp.setIcmsDebitoForaEstadoNfId(idIcms);
                    imp.setIcmsCreditoId(idIcms);
                    imp.setIcmsCreditoForaEstadoId(idIcms);
                    imp.setIcmsConsumidorId(idIcms);

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	id idproduto,\n"
                    + "	codigobarra ean,\n"
                    + "	1 as qtdeembalagem\n"
                    + "from\n"
                    + "	produtos p\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("idproduto"));
                    imp.setEan(rst.getString("ean"));
                    imp.setQtdEmbalagem(rst.getInt("qtdeembalagem"));

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
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	codigo as id,\n"
                    + "	razao,\n"
                    + "	cnpj as cnpj_cpf,\n"
                    + "	ie as ie_rg,\n"
                    + "	endereco,\n"
                    + "	bairro,\n"
                    + "	municipio,\n"
                    + "	uf,\n"
                    + "	cep,\n"
                    + "	telefone,\n"
                    + "	fax,\n"
                    + "	email,\n"
                    + "	obs as observacao\n"
                    + "from\n"
                    + "	fornecedor\n"
                    + "order by 1")) {
                while (rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());

                    imp.setImportId(rs.getString("id"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setCnpj_cpf(rs.getString("cnpj_cpf"));
                    imp.setIe_rg(rs.getString("ie_rg"));

                    imp.setEndereco(rs.getString("endereco"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("municipio"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));

                    imp.setTel_principal(rs.getString("telefone"));
                    imp.setObservacao(rs.getString("observacao"));

                    String email = rs.getString("email");
                    if ((email) != null && (!"".equals(email))) {
                        imp.addContato("1", "EMAIL", null, null, TipoContato.NFE, email);
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
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	pag.id,\n"
                    + "	pag.nf as numerodocumento,\n"
                    + "	pag.vencimento as datavencimento,\n"
                    + "	pag.codigofornecedor as idfornecedor,\n"
                    + "	pag.valor\n"
                    + "from\n"
                    + "	apagar pag\n"
                    + "join fornecedor forn on	forn.codigo = pag.codigofornecedor\n"
                    + "where\n"
                    + "	pag.valorpago = 0\n"
                    + "order by 1"
            )) {
                while (rs.next()) {
                    ContaPagarIMP imp = new ContaPagarIMP();
                    imp.setId(rs.getString("id"));
                    imp.setNumeroDocumento(rs.getString("numerodocumento"));
                    imp.setDataEmissao(rs.getDate("datavencimento"));
                    imp.setVencimento(rs.getDate("datavencimento"));
                    imp.setIdFornecedor(rs.getString("idfornecedor"));
                    imp.setValor(rs.getDouble("valor"));

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
                    "select \n"
                    + "	codigo as id,\n"
                    + "	nome as razao,\n"
                    + "	cnpj as cpfcnpj,\n"
                    + "	endereco,\n"
                    + "	bairro,\n"
                    + "	cidade,\n"
                    + "	uf,\n"
                    + "	cep,\n"
                    + "	telefone as telefone1,\n"
                    + "	celular,\n"
                    + "	bloqueado,\n"
                    + "	obs\n"
                    + "from\n"
                    + "	clientecheque\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("razao"));
                    imp.setCnpj(rst.getString("cpfcnpj"));

                    imp.setEndereco(rst.getString("endereco"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));

                    imp.setTelefone(rst.getString("telefone1"));
                    imp.setCelular(rst.getString("celular"));
                    imp.setBloqueado(rst.getBoolean("bloqueado"));
                    imp.setObservacao(rst.getString("obs"));

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
                    "select\n"
                    + "	codcup id,\n"
                    + "	data::date emissao,\n"
                    + "	codcup numerocupom,\n"
                    + "	substring(terminal,7,2) ecf,\n"
                    + "	vrtotal valor,\n"
                    + "	codcli idcliente,\n"
                    + "	data::date vencimento\n"
                    + "from\n"
                    + "	fiado\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rst.getString("id"));
                    imp.setDataEmissao(rst.getDate("emissao"));
                    imp.setNumeroCupom(rst.getString("numerocupom"));
                    imp.setEcf(rst.getString("ecf"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setIdCliente(rst.getString("idcliente"));
                    imp.setDataVencimento(rst.getDate("vencimento"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    public List<ChequeIMP> getCheque() throws Exception {
        List<ChequeIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	id,\n"
                    + "	emissao,\n"
                    + "	vencimento datadeposito,\n"
                    + "	nvenda num_cupom,\n"
                    + "	ncheque num_cheque,\n"
                    + "	banco,\n"
                    + "	agencia,\n"
                    + "	conta,\n"
                    + "	telefone,\n"
                    + "	cpf,\n"
                    + "	cliente nome,\n"
                    + "	rg,\n"
                    + "	valorcheque,\n"
                    + "	celular observacao\n"
                    + "from\n"
                    + "	chequepre \n"
                    + "order by 1")) {
                while (rs.next()) {
                    ChequeIMP imp = new ChequeIMP();

                    imp.setId(rs.getString("id"));
                    imp.setDate(rs.getDate("emissao"));
                    imp.setDataDeposito(rs.getDate("datadeposito"));
                    imp.setNumeroCupom(rs.getString("num_cupom"));
                    imp.setNumeroCheque(rs.getString("num_cheque"));
                    imp.setBanco(rs.getInt("banco"));
                    imp.setAgencia(rs.getString("agencia"));
                    imp.setConta(rs.getString("conta"));
                    imp.setTelefone(rs.getString("telefone"));
                    imp.setCpf(rs.getString("cpf"));
                    imp.setNome(rs.getString("nome"));
                    imp.setRg(rs.getString("rg"));
                    imp.setValor(rs.getDouble("valorcheque"));
                    imp.setObservacao(rs.getString("observacao"));

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
        return new ItuInfoDAO.VendaIterator(getLojaOrigem(), this.dataInicioVenda, this.dataTerminoVenda);
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        return new ItuInfoDAO.VendaItemIterator(getLojaOrigem(), this.dataInicioVenda, this.dataTerminoVenda);
    }

    private static class VendaIterator implements Iterator<VendaIMP> {

        public final static SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");

        private Statement stm = ConexaoPostgres.getConexao().createStatement();
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
                        next.setNumeroCupom(Utils.stringToInt(rst.getString("numcupom")));
                        next.setEcf(Utils.stringToInt(rst.getString("ecf")));

                        next.setData(rst.getDate("data"));
                        String horaInicio = timestampDate.format(rst.getDate("data")) + " " + rst.getString("horainicio");
                        String horaTermino = timestampDate.format(rst.getDate("data")) + " " + rst.getString("horatermino");
                        next.setHoraInicio(timestamp.parse(horaInicio));
                        next.setHoraTermino(timestamp.parse(horaTermino));
                        next.setSubTotalImpressora(rst.getDouble("subtotalimpressora"));
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
                    = "select\n"
                    + "  max(id) id_venda,\n"
                    + "	 descricao numcupom,\n"
                    + "	 trim(replace (terminal,'CAIXA','')) ecf,\n"
                    + "	 data,\n"
                    + "	 min(hora) horainicio,\n"
                    + "	 max(hora) horatermino,\n"
                    + "	 sum(credito) subtotalimpressora\n"
                    + "from\n"
                    + "	 caixa\n"
                    + "where\n"
                    + "	 codigoloja = " + idLojaCliente + "\n"
                    + "	 and descricao not like 'SANG%'\n"
                    + "	 and situacao = 1\n"
                    + "	 and data between '" + strDataInicio + "' and '" + strDataTermino + "'\n"
                    + " group by descricao,terminal,data";
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

        private Statement stm = ConexaoPostgres.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaItemIMP next;
        private Set<String> uk = new HashSet<>();

        private void obterNext() {
            try {
                if (next == null) {
                    if (rst.next()) {
                        next = new VendaItemIMP();
                        String id = rst.getString("id_item") + "-" + rst.getString("nroitem") + "-" + rst.getString("produto");
                        if (!uk.add(id)) {
                            LOG.warning("Venda " + id + " já existe na listagem");
                        }
                        next.setVenda(rst.getString("id_venda"));
                        next.setId(id);
                        next.setSequencia(rst.getInt("nroitem"));
                        next.setProduto(rst.getString("produto"));
                        next.setCodigoBarras(rst.getString("codigobarra"));
                        next.setDescricaoReduzida(rst.getString("descricao"));
                        next.setUnidadeMedida(rst.getString("unidade"));
                        next.setQuantidade(rst.getDouble("quantidade"));
                        next.setPrecoVenda(rst.getDouble("precovenda"));
                        next.setTotalBruto(rst.getDouble("total"));
                        //next.setCancelado(rst.getBoolean("cancelado"));
                    }
                }
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaItemIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {
            this.sql
                    = "select\n"
                    + "	max(v.id) id_venda,\n"
                    + "	max(i.id) id_item,\n"
                    + "	max(i.item) nroitem,\n"
                    + "	max(p.id) produto,\n"
                    + "	p.codigobarra,\n"
                    + "	max(p.produto) descricao,\n"
                    + "	case when p.mgv5 = 0 then 'UN' else 'KG' end unidade,\n"
                    + "	max(i.qtde) quantidade,\n"
                    + "	i.vrunit precovenda,\n"
                    + "	i.vrtotal total\n"
                    + "from\n"
                    + "	cupom i\n"
                    + "	left join caixa v on v.descricao = i.codigo::varchar and v.codigoloja = i.loja\n"
                    + "	left join produtos p on i.produto = p.codigobarra\n"
                    + "where\n"
                    + "	v.codigoloja = " + idLojaCliente + "\n"
                    + "	and v.data between '" + VendaIterator.FORMAT.format(dataInicio) + "' and '" + VendaIterator.FORMAT.format(dataTermino) + "'\n"
                    + "group by \n"
                    + "	p.codigobarra ,i.vrunit, i.vrtotal, p.mgv5 \n"
                    + "order by 1,3";
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
