package vrimplantacao2.dao.interfaces;

import java.io.File;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao.dao.cadastro.NcmDAO;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.NcmVO;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.financeiro.creditorotativo.CreditoRotativoDAO;
import vrimplantacao2.dao.cadastro.financeiro.creditorotativo.CreditoRotativoItemAnteriorDAO;
import vrimplantacao2.dao.cadastro.financeiro.creditorotativo.CreditoRotativoItemDAO;
import vrimplantacao2.utils.MathUtils;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.cliente.rotativo.CreditoRotativoItemAnteriorVO;
import vrimplantacao2.vo.cadastro.cliente.rotativo.CreditoRotativoItemVO;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Leandro
 */
public class RepleisDAO extends InterfaceDAO {

    public String i_arquivoProdutoFornecedor;

    @Override
    public String getSistema() {
        return "Repleis";
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select codigo, setor from cadastrosetores"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setMerc1ID(rst.getString("codigo"));
                    imp.setMerc1Descricao(rst.getString("setor"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        long codigoBalanca;
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    p.codigo,\n"
                    + "    p.data,\n"
                    + "    p.codigobarras,\n"
                    + "    p.un,\n"
                    + "    p.descricao,\n"
                    + "    p.linksetor,\n"
                    + "    p.idgrade,\n"
                    + "    p.pesobruto,\n"
                    + "    p.peseliquido,\n"
                    + "    p.estoqueminimo,\n"
                    + "    p.estoque,\n"
                    + "    p.margemlucro,\n"
                    + "    p.precocompra,\n"
                    + "    p.preco,\n"
                    + "    case lower(p.participacao) when 'indisponível' then 0 else 1 end ativo\n"
                    + "from\n"
                    + "    cadastrodescricao p\n"
                    + "order by\n"
                    + "    p.codigo"
            )) {
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().carregarProdutosBalanca();
                while (rst.next()) {

                    if ("2312300".equals(rst.getString("codigobarras"))) {
                        System.out.println("aqui");
                    }

                    ProdutoIMP imp = new ProdutoIMP();

                    if ((rst.getString("codigobarras") != null)
                            && (!rst.getString("codigobarras").trim().isEmpty())) {
                        codigoBalanca = Long.parseLong(Utils.formataNumero(rst.getString("codigobarras").substring(1, rst.getString("codigobarras").trim().length())));
                        codigoBalanca = Long.parseLong(String.valueOf(codigoBalanca).substring(0, String.valueOf(codigoBalanca).length() - 2));
                    } else {
                        codigoBalanca = 0;
                    }

                    long codigoProduto;
                    codigoProduto = codigoBalanca;
                    ProdutoBalancaVO produtoBalanca;
                    if (codigoProduto <= Integer.MAX_VALUE) {
                        produtoBalanca = produtosBalanca.get((int) codigoProduto);
                    } else {
                        produtoBalanca = null;
                    }
                    if (produtoBalanca != null) {
                        imp.seteBalanca(true);
                        imp.setValidade(produtoBalanca.getValidade() > 1 ? produtoBalanca.getValidade() : 0);
                        imp.setEan(String.valueOf(codigoBalanca));
                        if ("P".equals(produtoBalanca.getPesavel())) {
                            imp.setTipoEmbalagem("KG");
                        } else {
                            imp.setTipoEmbalagem("UN");
                        }
                    } else {
                        imp.setEan(rst.getString("codigobarras"));
                        imp.seteBalanca(false);
                        imp.setTipoEmbalagem(Utils.acertarTexto(rst.getString("un")));
                    }

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("codigo"));
                    imp.setDataCadastro(rst.getDate("data"));

                    /*String strEAN = Utils.stringLong(rst.getString("codigobarras"));
                     imp.seteBalanca("KG".equals(imp.getTipoEmbalagem()));
                     if (!"".equals(strEAN)) {
                     imp.setEan(imp.isBalanca() ? strEAN.substring(1) :  strEAN);
                     }*/
                    imp.setDescricaoCompleta(rst.getString("descricao"));
                    imp.setDescricaoReduzida(rst.getString("descricao"));
                    imp.setDescricaoGondola(rst.getString("descricao"));
                    imp.setCodMercadologico1(rst.getString("linksetor"));
                    imp.setIdFamiliaProduto(rst.getString("idgrade"));
                    imp.setPesoBruto(rst.getDouble("pesobruto"));
                    imp.setPesoLiquido(rst.getDouble("peseliquido"));
                    imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setMargem(rst.getDouble("margemlucro"));
                    imp.setCustoComImposto(rst.getDouble("precocompra"));
                    imp.setCustoSemImposto(rst.getDouble("precocompra"));
                    imp.setPrecovenda(rst.getDouble("preco"));
                    imp.setSituacaoCadastro(SituacaoCadastro.getById(rst.getInt("ativo")));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    //@Override
    public List<FornecedorIMP> getFornecedore() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    f.codigo,\n"
                    + "    f.nomefornecedor,\n"
                    + "    case trim(coalesce(f.cnpj,'')) when '' then trim(coalesce(f.cpf, '')) else trim(coalesce(f.cnpj, '')) end cnpj,\n"
                    + "    f.ie,\n"
                    + "    f.endereco,\n"
                    + "    f.numero,\n"
                    + "    f.bairro,\n"
                    + "    f.cidade,\n"
                    + "    f.estado,\n"
                    + "    f.cep,\n"
                    + "    f.pontoreferencia,\n"
                    + "    f.representante,\n"
                    + "    f.fone1,\n"
                    + "    f.fone2,\n"
                    + "    f.fax,\n"
                    + "    f.email,\n"
                    + "    f.fonerepresen, \n"
                    + "    f.celrepresentante, \n"
                    + "    f.emailrepresentante,\n"
                    + "    f.datacad,  \n"
                    + "    f.obs,\n"
                    + "    f.prazopagamento,\n"
                    + "\n"
                    + "    f.site,\n"
                    + "    f.credito,\n"
                    + "    f.formaentrega,\n"
                    + "    f.aceitaparcelamento,\n"
                    + "    f.aceitacartao,\n"
                    + "    f.aceitaboleto\n"
                    + "from\n"
                    + "    cadastrofornecedor f\n"
                    + "order by\n"
                    + "    f.codigo"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("codigo"));
                    imp.setRazao(rst.getString("nomefornecedor"));
                    imp.setFantasia(rst.getString("nomefornecedor"));
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    imp.setIe_rg(rst.getString("ie"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("estado"));
                    imp.setCep(rst.getString("cep"));
                    String observacao = "";
                    String pontoReferencia = Utils.acertarTexto(rst.getString("pontoreferencia"));
                    if (!"".equals(pontoReferencia)) {
                        observacao += "ponto de referencia: " + pontoReferencia + "\n";
                    }
                    imp.setTel_principal(rst.getString("fone1"));
                    String fone2 = Utils.stringLong(rst.getString("fone2"));
                    if (!"".equals(fone2) && !"0".equals(fone2)) {
                        imp.addContato("1", "TELEFONE 2", fone2, "", TipoContato.COMERCIAL, "");
                    }
                    String fax = Utils.stringLong(rst.getString("fax"));
                    if (!"".equals(fax) && !"0".equals(fax)) {
                        imp.addContato("2", "FAX", fax, "", TipoContato.COMERCIAL, "");
                    }
                    String email = Utils.acertarTexto(rst.getString("email"));
                    if (!"".equals(email)) {
                        imp.addContato("3", "E-MAIL", "", "", TipoContato.COMERCIAL, email);
                    }
                    String representante = Utils.acertarTexto(rst.getString("representante"));
                    if (!"".equals(representante)) {
                        imp.addContato("4", representante, rst.getString("fonerepresen"), rst.getString("celrepresentante"), TipoContato.COMERCIAL, rst.getString("emailrepresentante"));
                    }
                    imp.setDatacadastro(rst.getDate("datacad"));
                    observacao += Utils.acertarTexto(rst.getString("obs"));
                    imp.setObservacao(observacao);
                    imp.setCondicaoPagamento(rst.getInt("prazopagamento"));

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
                    "select\n"
                    + "    c.codigo id,\n"
                    + "    coalesce(c.cnpj, c.cpf) cnpj,\n"
                    + "    coalesce(c.inscestadual, c.rg) inscricaoestadual,\n"
                    + "    c.orgaoemissor,\n"
                    + "    c.nomecliente razao,\n"
                    + "    c.nomecliente fantasia,\n"
                    + "    case upper(c.ativoinativo) when 'ATIVO' then 1 else 0 end ativo,\n"
                    + "    c.endereco,\n"
                    + "    c.numero,\n"
                    + "    c.proximoa complemento,\n"
                    + "    c.bairro,\n"
                    + "    c.cidade,\n"
                    + "    c.estado,\n"
                    + "    c.cep,\n"
                    + "    c.datanasci datanascimento,\n"
                    + "    c.datacad datacadastro,\n"
                    + "    c.creditomax valorlimite,\n"
                    + "    c.pai,\n"
                    + "    c.mae,\n"
                    + "    c.obs,\n"
                    + "    c.apelido,\n"
                    + "    c.fiador,\n"
                    + "    c.telefonefiador,\n"
                    + "    c.prazo diavencimento,\n"
                    + "    c.telefoneres,\n"
                    + "    c.telefonecom,\n"
                    + "    c.celular,\n"
                    + "    c.fax,\n"
                    + "    c.email,\n"
                    + "    c.inscmunicipal inscricaoMunicipal\n"
                    + "from\n"
                    + "    cadastrocliente c\n"
                    + "order by\n"
                    + "    c.codigo"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("id"));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setInscricaoestadual(rst.getString("inscricaoestadual"));
                    imp.setOrgaoemissor(rst.getString("orgaoemissor"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setAtivo(rst.getBoolean("ativo"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("estado"));
                    imp.setCep(rst.getString("cep"));
                    imp.setDataNascimento(rst.getDate("datanascimento"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setValorLimite(rst.getDouble("valorlimite"));
                    imp.setNomePai(rst.getString("pai"));
                    imp.setNomeMae(rst.getString("mae"));
                    String apelido = Utils.acertarTexto(rst.getString("apelido"));
                    String fiador = Utils.acertarTexto(rst.getString("fiador"));
                    String telFiador = Utils.formataNumero(rst.getString("telefonefiador"));
                    String obs = Utils.acertarTexto(rst.getString("obs"));
                    imp.setObservacao(
                            (!"".equals(apelido) ? "apelido: " + apelido + " | " : "")
                            + (!"".equals(fiador) ? "fiador: " + fiador + " | " : "")
                            + (!"".equals(telFiador) ? "telefone fiador: " + telFiador + " | " : "")
                            + (!"".equals(obs) ? "obs: " + obs : "")
                    );
                    imp.setDiaVencimento(rst.getInt("diavencimento"));
                    imp.setTelefone(rst.getString("telefoneres"));
                    imp.setCelular(rst.getString("celular"));
                    imp.setFax(rst.getString("fax"));
                    imp.setEmail(rst.getString("email"));
                    imp.setInscricaoMunicipal(rst.getString("inscricaoMunicipal"));
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
                    "select\n"
                    + "    coalesce(p.numerovenda, '') || '-' ||\n"
                    + "    coalesce(p.codigo, '') || '-' ||\n"
                    + "    coalesce(p.datacompra, '') || '-' ||\n"
                    + "    coalesce(p.datavencimento, '') || '-' ||\n"
                    + "    coalesce(sum(p.totallinha), '') id,\n"
                    + "    p.numerovenda numeroCupom,\n"
                    + "    p.codigo idCliente,\n"
                    + "    p.cliente,\n"
                    + "    p.responsavel,\n"
                    + "    p.datacompra dataEmissao,\n"
                    + "    p.datavencimento,\n"
                    + "    sum(p.totallinha) valor\n"
                    + "from\n"
                    + "    conta p\n"
                    + "group by\n"
                    + "    numerovenda,\n"
                    + "    codigo,\n"
                    + "    cliente,\n"
                    + "    responsavel,\n"
                    + "    datacompra,\n"
                    + "    datavencimento"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();

                    imp.setId(rst.getString("id"));
                    imp.setNumeroCupom(rst.getString("numerocupom"));
                    imp.setIdCliente(rst.getString("idCliente"));
                    imp.setObservacao("RESPONSAVEL: " + rst.getString("responsavel"));
                    imp.setDataEmissao(rst.getDate("dataemissao"));
                    imp.setDataVencimento(rst.getDate("datavencimento"));
                    imp.setValor(rst.getDouble("valor"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select cnpj, nomeempresa from cadastroempresa"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("cnpj"), rst.getString("nomeempresa")));
                }
            }
        }
        return result;
    }

    public void importarPagamentoRotativo() throws Exception {
        Conexao.begin();
        try {
            Map<String, Double> pagamentos = new HashMap<>();

            ProgressBar.setStatus("Importando pagamentos...");
            try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select\n"
                        + "    r.codigo,\n"
                        + "    r.cliente,\n"
                        + "    sum(r.valor) valor\n"
                        + "from\n"
                        + "    recebimento r\n"
                        + "group by\n"
                        + "    codigo,\n"
                        + "    cliente"
                )) {
                    while (rst.next()) {
                        double valor = rst.getDouble("valor");
                        if (valor < 0) {
                            valor *= -1;
                        }
                        pagamentos.put(rst.getString("codigo"), MathUtils.trunc(valor, 2));
                    }
                }
            }

            System.out.println("Pagamentos: " + pagamentos.size() + " (209015) = " + pagamentos.get("209015"));

            CreditoRotativoDAO rotDao = new CreditoRotativoDAO();
            CreditoRotativoItemDAO dao = new CreditoRotativoItemDAO();
            CreditoRotativoItemAnteriorDAO antDao = new CreditoRotativoItemAnteriorDAO();
            MultiMap<String, CreditoRotativoItemAnteriorVO> baixasAnteriores = antDao.getBaixasAnteriores(null, null);

            try (Statement stm = Conexao.createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select\n"
                        + "	 ant.sistema,\n"
                        + "    ant.loja,\n"
                        + "    ant.id_cliente,\n"
                        + "    ant.id,\n"
                        + "    ant.codigoatual,\n"
                        + "    r.id_loja,\n"
                        + "    r.valor,\n"
                        + "    r.datavencimento\n"
                        + "from \n"
                        + "	implantacao.codant_recebercreditorotativo ant\n"
                        + "    join recebercreditorotativo r on\n"
                        + "    	ant.codigoatual = r.id\n"
                        + "order by\n"
                        + "	ant.id_cliente, r.datavencimento"
                )) {
                    int cont1 = 0, cont2 = 0;
                    while (rst.next()) {
                        String sistema = rst.getString("sistema");
                        String loja = rst.getString("loja");
                        String idCliente = rst.getString("id_cliente");
                        String idRotativo = rst.getString("id");
                        int codigoAtual = rst.getInt("codigoatual");
                        int id_loja = rst.getInt("id_loja");
                        double valor = rst.getDouble("valor");
                        Date vencimento = rst.getDate("datavencimento");

                        if (!baixasAnteriores.containsKey(sistema, loja, idRotativo, idRotativo)) {
                            if (pagamentos.containsKey(idCliente)) {
                                double valorPagoTotal = pagamentos.get(idCliente);
                                if (valorPagoTotal > 0) {
                                    double valorParc;
                                    if (valorPagoTotal >= valor) {
                                        valorPagoTotal -= valor;
                                        valorParc = valor;
                                    } else {
                                        valorParc = valorPagoTotal;
                                        valorPagoTotal = 0;
                                    }

                                    CreditoRotativoItemVO pag = new CreditoRotativoItemVO();
                                    pag.setId_receberCreditoRotativo(codigoAtual);
                                    pag.setValor(valorParc);
                                    pag.setValorTotal(valorParc);
                                    pag.setDatabaixa(vencimento);
                                    pag.setDataPagamento(vencimento);
                                    pag.setObservacao("IMPORTADO VR");
                                    pag.setId_loja(id_loja);

                                    dao.gravarRotativoItem(pag);

                                    CreditoRotativoItemAnteriorVO ant = new CreditoRotativoItemAnteriorVO();
                                    ant.setSistema(sistema);
                                    ant.setLoja(loja);
                                    ant.setIdCreditoRotativo(idRotativo);
                                    ant.setId(idRotativo);
                                    ant.setCodigoAtual(pag.getId());
                                    ant.setDataPagamento(vencimento);
                                    ant.setValor(pag.getValor());

                                    antDao.gravarRotativoItemAnterior(ant);

                                    rotDao.verificarBaixado(codigoAtual);

                                    pagamentos.put(idCliente, valorPagoTotal);
                                    baixasAnteriores.put(ant,
                                            ant.getSistema(),
                                            ant.getLoja(),
                                            ant.getIdCreditoRotativo(),
                                            ant.getId()
                                    );
                                }
                            }
                        }
                        cont1++;
                        cont2++;

                        if (cont1 == 1000) {
                            cont1 = 0;
                            ProgressBar.setStatus("Importando pagamentos..." + cont2);
                        }
                    }
                }
            }

            Conexao.commit();
        } catch (Exception e) {
            Conexao.rollback();
            throw e;
        }
    }

    /*adapatação do sistema alterdata provisório*/
    public void getProdutoTributacao(String i_arquivoProduto) throws Exception {
        WorkbookSettings settings = new WorkbookSettings();
        settings.setEncoding("CP1250");
        Workbook arquivo = Workbook.getWorkbook(new File(i_arquivoProduto), settings);
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        Sheet[] sheets = arquivo.getSheets();
        int linha, idAliquota, idTipoPisCofins,
                idTipoPisCofinsCredito, ncm1, ncm2, ncm3, naturezaReceita;
        long codigoBarras;
        String ncmAtual;
        try {
            Conexao.begin();
            stm = Conexao.createStatement();
            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;
                for (int i = 0; i < sheet.getRows(); i++) {
                    linha++;

                    if (linha == 1) {
                        continue;
                    }

                    Cell cellCodigoBarras = sheet.getCell(0, i);
                    Cell cellDescIcms = sheet.getCell(2, i);
                    Cell cellNcm = sheet.getCell(20, i);
                    Cell cellCstPis = sheet.getCell(39, i);
                    Cell cellCstCofins = sheet.getCell(40, i);
                    Cell cellCest = sheet.getCell(52, i);
                    codigoBarras = Long.parseLong(Utils.formataNumero(cellCodigoBarras.getContents().trim()));
                    if (cellDescIcms.getContents().contains("CALCULOTRIBUTADO 18%")) {
                        idAliquota = 2;
                    } else if (cellDescIcms.getContents().contains("CALCULO TRIBUTADO 12%")) {
                        idAliquota = 1;
                    } else if (cellDescIcms.getContents().contains("CALCULO TRIBUTADO 7%")) {
                        idAliquota = 0;
                    } else if (cellDescIcms.getContents().contains("ISENTO")) {
                        idAliquota = 6;
                    } else if (cellDescIcms.getContents().contains("NÃO TRIBUTADO")) {
                        idAliquota = 17;
                    } else if (cellDescIcms.getContents().contains("SUBS")) {
                        idAliquota = 7;
                    } else {
                        idAliquota = 8;
                    }

                    idTipoPisCofins = Utils.retornarPisCofinsDebito(Integer.parseInt(cellCstPis.getContents().trim()));
                    idTipoPisCofinsCredito = Utils.retornarPisCofinsCredito(Integer.parseInt(cellCstCofins.getContents().trim()));
                    naturezaReceita = Utils.retornarTipoNaturezaReceita(idTipoPisCofins, "");

                    ncmAtual = "";
                    if ((cellNcm.getContents() != null)
                            && (!cellNcm.getContents().trim().isEmpty())
                            && (cellNcm.getContents().trim().length() > 5)) {
                        ncmAtual = Utils.formataNumero(cellNcm.getContents().trim());
                        NcmVO oNcm = new NcmDAO().validar(ncmAtual);
                        ncm1 = oNcm.ncm1;
                        ncm2 = oNcm.ncm2;
                        ncm3 = oNcm.ncm3;
                    } else {
                        ncm1 = 402;
                        ncm2 = 99;
                        ncm3 = 0;
                    }

                    sql = new StringBuilder();
                    sql.append("select id_produto "
                            + "from produtoautomacao "
                            + "where codigobarras = " + codigoBarras);
                    rst = stm.executeQuery(sql.toString());
                    if (rst.next()) {
                        sql = new StringBuilder();
                        sql.append("update produtoaliquota "
                                + "set "
                                + "id_aliquotadebito = " + idAliquota + ", "
                                + "id_aliquotacredito = " + idAliquota + ", "
                                + "id_aliquotadebitoforaestado = " + idAliquota + ", "
                                + "id_aliquotacreditoforaestado = " + idAliquota + ", "
                                + "id_aliquotadebitoforaestadoNF = " + idAliquota + ", "
                                + "id_aliquotaconsumidor = " + idAliquota + " "
                                + "where id_produto = " + rst.getInt("id_produto") + "; "
                                + "update produto "
                                + "set "
                                + "id_tipopiscofins = " + idTipoPisCofins + ", "
                                + "id_tipopiscofinscredito = " + idTipoPisCofinsCredito + ", "
                                + "tiponaturezareceita = " + (naturezaReceita == -1 ? null : naturezaReceita) + ","
                                + "ncm1 = " + ncm1 + " ,"
                                + "ncm2 = " + ncm2 + ", "
                                + "ncm3 = " + ncm3 + " "
                                + "where id = " + rst.getInt("id_produto") + "; ");
                        stm.execute(sql.toString());
                    }
                    ProgressBar.next();
                }
            }
            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void getCodigoAlterdataProduto(String i_arquivoProduto) throws Exception {
        WorkbookSettings settings = new WorkbookSettings();
        settings.setEncoding("CP1250");
        Workbook arquivo = Workbook.getWorkbook(new File(i_arquivoProduto), settings);
        StringBuilder sql = null;
        Statement stm = null, stm2 = null;
        ResultSet rst = null, rst2 = null;
        Sheet[] sheets = arquivo.getSheets();
        int linha;
        long codigoEAN;
        try {
            Conexao.begin();
            stm = Conexao.createStatement();
            stm2 = Conexao.createStatement();
            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;
                for (int i = 0; i < sheet.getRows(); i++) {
                    linha++;
                    if (linha == 1) {
                        continue;
                    }

                    Cell cellIdProduto = sheet.getCell(1, i);
                    Cell cellCodigoEAN = sheet.getCell(0, i);
                    if ((cellCodigoEAN.getContents() != null)
                            && (!cellCodigoEAN.getContents().trim().isEmpty())) {
                        codigoEAN = Long.parseLong(Utils.formataNumero(cellCodigoEAN.getContents().trim()));

                        sql = new StringBuilder();
                        sql.append("select id_produto "
                                + "from produtoautomacao "
                                + "where codigobarras = " + codigoEAN);
                        rst = stm.executeQuery(sql.toString());
                        if (rst.next()) {
                            sql = new StringBuilder();
                            sql.append("select codigoatual "
                                    + "from implantacao.codant_produto "
                                    + "where codigoatual = " + rst.getString("id_produto"));
                            rst2 = stm2.executeQuery(sql.toString());
                            if (rst2.next()) {
                                sql = new StringBuilder();
                                sql.append("update implantacao.codant_produto "
                                        + "set codigoAlterdata = '" + cellIdProduto.getContents().trim() + "' "
                                        + "where codigoatual = " + rst.getInt("id_produto") + ";");
                                stm2.execute(sql.toString());
                            }
                        }
                    }
                    ProgressBar.next();
                }
            }
            stm.close();
            stm2.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void getCodigoAlterdataFornecedor(String i_arquivoProduto) throws Exception {
        WorkbookSettings settings = new WorkbookSettings();
        settings.setEncoding("CP1250");
        Workbook arquivo = Workbook.getWorkbook(new File(i_arquivoProduto), settings);
        StringBuilder sql = null;
        Statement stm = null, stm2 = null;
        ResultSet rst = null, rst2 = null;
        Sheet[] sheets = arquivo.getSheets();
        int linha;
        long cnpj;
        try {
            Conexao.begin();
            stm = Conexao.createStatement();
            stm2 = Conexao.createStatement();
            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;
                for (int i = 0; i < sheet.getRows(); i++) {
                    linha++;
                    if (linha == 1) {
                        continue;
                    }

                    Cell cellIdPessoa = sheet.getCell(0, i);
                    Cell cellCnpj = sheet.getCell(1, i);
                    if ((cellCnpj.getContents() != null)
                            && (!cellCnpj.getContents().trim().isEmpty())) {
                        cnpj = Long.parseLong(Utils.formataNumero(cellCnpj.getContents().trim()));

                        sql = new StringBuilder();
                        sql.append("select id "
                                + "from fornecedor "
                                + "where cnpj = " + cnpj);
                        rst = stm.executeQuery(sql.toString());
                        if (rst.next()) {
                            sql = new StringBuilder();
                            sql.append("select codigoatual "
                                    + "from implantacao.codant_fornecedor "
                                    + "where codigoatual = " + rst.getString("id"));
                            rst2 = stm2.executeQuery(sql.toString());
                            if (rst2.next()) {
                                sql = new StringBuilder();
                                sql.append("update implantacao.codant_fornecedor "
                                        + "set codigoAlterdata = '" + cellIdPessoa.getContents().trim() + "' "
                                        + "where codigoatual = " + rst.getInt("id") + ";");
                                stm2.execute(sql.toString());
                            }
                        }
                    }
                    ProgressBar.next();
                }
            }
            stm.close();
            stm2.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> vResult = new ArrayList<>();
        WorkbookSettings settings = new WorkbookSettings();
        settings.setEncoding("CP1250");
        Workbook arquivo = Workbook.getWorkbook(new File(i_arquivoProdutoFornecedor), settings);
        StringBuilder sql = null;
        Statement stm = null, stm2 = null;
        ResultSet rst = null, rst2 = null;
        Sheet[] sheets = arquivo.getSheets();
        int linha;
        String codigoProduto, codigoFornecedor;
        String strDataAlteracao = "", codigoExterno;
        java.sql.Date dataAlteracao;
        DateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Conexao.begin();
            stm = Conexao.createStatement();
            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;
                for (int i = 0; i < sheet.getRows(); i++) {
                    linha++;
                    if (linha == 1) {
                        continue;
                    }

                    Cell cellIdPessoa = sheet.getCell(1, i);
                    Cell cellIdProduto = sheet.getCell(2, i);
                    Cell cellDataAlteracao = sheet.getCell(3, i);
                    Cell cellCusto = sheet.getCell(4, i);
                    Cell cellCodigoExterno = sheet.getCell(5, i);
                    Cell cellQtdEmbalagem = sheet.getCell(8, i);

                    codigoProduto = "";
                    codigoFornecedor = "";

                    sql = new StringBuilder();
                    sql.append("select impid "
                            + "from implantacao.codant_produto "
                            + "where codigoAlterdata = '" + cellIdProduto.getContents().trim() + "'");
                    rst = stm.executeQuery(sql.toString());
                    if (rst.next()) {
                        codigoProduto = rst.getString("impid");
                    }

                    if ((cellDataAlteracao.getContents() != null)
                            && (!cellDataAlteracao.getContents().trim().isEmpty())) {
                        strDataAlteracao = cellDataAlteracao.getContents().trim();
                        dataAlteracao = new java.sql.Date(fmt.parse(strDataAlteracao).getTime());
                    } else {
                        dataAlteracao = new java.sql.Date(new java.util.Date().getTime());
                    }
                    if ((cellCodigoExterno.getContents() != null)
                            && (!cellCodigoExterno.getContents().trim().isEmpty())) {
                        codigoExterno = cellCodigoExterno.getContents().trim();
                    } else {
                        codigoExterno = "";
                    }

                    if ((!codigoProduto.isEmpty())) {
                        ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setIdProduto(codigoProduto);
                        imp.setIdFornecedor(cellIdPessoa.getContents());
                        if ((cellQtdEmbalagem.getContents() != null) &&
                                (!cellQtdEmbalagem.getContents().trim().isEmpty())) {
                            if (Double.parseDouble(cellQtdEmbalagem.getContents().replace(",", ".")) < 1) {
                                imp.setQtdEmbalagem(1);
                            } else {
                                imp.setQtdEmbalagem(Math.round(Double.parseDouble(cellQtdEmbalagem.getContents().replace(",", ".").trim())));
                            }
                        } else {
                            imp.setQtdEmbalagem(1);
                        }
                        if ((cellCusto.getContents() != null)
                                && (!cellCusto.getContents().trim().isEmpty())) {
                            imp.setCustoTabela(Double.parseDouble(cellCusto.getContents().replace(",", ".").trim()));
                        } else {
                            imp.setCustoTabela(0);
                        }
                        imp.setDataAlteracao(dataAlteracao);
                        imp.setCodigoExterno(codigoExterno);
                        vResult.add(imp);
                    }
                }
            }
            stm.close();
            Conexao.commit();
            return vResult;
        } catch (Exception ex) {
            throw ex;
        }
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> vResult = new ArrayList<>();
        WorkbookSettings settings = new WorkbookSettings();
        settings.setEncoding("CP1250");
        Workbook arquivo = Workbook.getWorkbook(new File(i_arquivoProdutoFornecedor), settings);
        StringBuilder sql = null;
        Sheet[] sheets = arquivo.getSheets();
        int linha;
        try {
            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;
                for (int i = 0; i < sheet.getRows(); i++) {
                    linha++;
                    if (linha == 1) {
                        continue;
                    }

                    Cell cellIdPessoa = sheet.getCell(0, i);
                    Cell cellNome = sheet.getCell(2, i);
                    Cell cellFantasia = sheet.getCell(3, i);
                    Cell cellEndereco = sheet.getCell(4, i);
                    Cell cellBairro = sheet.getCell(5, i);
                    Cell cellCep = sheet.getCell(6, i);
                    Cell cellCidade = sheet.getCell(7, i);
                    Cell cellUF = sheet.getCell(8, i);
                    Cell cellEmail = sheet.getCell(9, i);
                    Cell cellTelefone = sheet.getCell(10, i);
                    Cell cellTelefoneComercial = sheet.getCell(11, i);
                    Cell cellFax = sheet.getCell(12, i);
                    Cell cellCnpj = sheet.getCell(13, i);
                    Cell cellInsricaoEstadual = sheet.getCell(14, i);
                    Cell cellAtivo = sheet.getCell(15, i);
                    Cell cellObs = sheet.getCell(16, i);
                    Cell cellNumero = sheet.getCell(17, i);
                    Cell cellComplemento = sheet.getCell(18, i);
                    Cell cellEmail2 = sheet.getCell(19, i);
                    Cell cellSite = sheet.getCell(20, i);
                    Cell cellEmail3 = sheet.getCell(21, i);

                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(cellIdPessoa.getContents().trim());
                    imp.setRazao(cellNome.getContents());
                    imp.setFantasia(cellFantasia.getContents());
                    imp.setEndereco(cellEndereco.getContents());
                    imp.setNumero(cellNumero.getContents());
                    imp.setBairro(cellBairro.getContents());
                    imp.setComplemento(cellComplemento.getContents());
                    imp.setCep(Utils.formataNumero(cellCep.getContents()));
                    imp.setTel_principal(cellTelefone.getContents());
                    imp.setCnpj_cpf(Utils.formataNumero(cellCnpj.getContents()));
                    imp.setIe_rg(cellInsricaoEstadual.getContents());
                    imp.setAtivo("S".equals(cellAtivo.getContents()));
                    imp.setObservacao(cellObs.getContents());
                    imp.setMunicipio(Utils.acertarTexto(cellCidade.getContents()));
                    imp.setUf(cellUF.getContents());
                    if ((cellEmail.getContents() != null)
                            && (!cellEmail.getContents().trim().isEmpty())) {
                        imp.addContato("1",
                                "EMAIL",
                                "",
                                "",
                                TipoContato.COMERCIAL,
                                cellEmail.getContents().trim()
                        );
                    }
                    if ((cellEmail2.getContents() != null)
                            && (!cellEmail2.getContents().trim().isEmpty())) {
                        imp.addContato("2",
                                "EMAIL 2",
                                "",
                                "",
                                TipoContato.COMERCIAL,
                                cellEmail2.getContents().trim()
                        );
                    }
                    if ((cellEmail3.getContents() != null)
                            && (!cellEmail3.getContents().trim().isEmpty())) {
                        imp.addContato("3",
                                "EMAIL 3",
                                "",
                                "",
                                TipoContato.COMERCIAL,
                                cellEmail3.getContents().trim()
                        );
                    }
                    if ((cellSite.getContents() != null)
                            && (!cellSite.getContents().trim().isEmpty())) {
                        imp.addContato("4",
                                "SITE",
                                "",
                                "",
                                TipoContato.COMERCIAL,
                                cellSite.getContents().trim()
                        );
                    }
                    if ((cellFax.getContents() != null)
                            && (!cellFax.getContents().trim().isEmpty())) {
                        imp.addContato("5",
                                "FAX",
                                Utils.formataNumero(cellFax.getContents()),
                                "",
                                TipoContato.COMERCIAL,
                                ""
                        );
                    }
                    if ((cellTelefoneComercial.getContents() != null)
                            && (!cellTelefoneComercial.getContents().trim().isEmpty())) {
                        imp.addContato("5",
                                "TELEFONE COMERCIAL",
                                Utils.formataNumero(cellTelefoneComercial.getContents()),
                                "",
                                TipoContato.COMERCIAL,
                                ""
                        );
                    }
                    vResult.add(imp);
                }
            }
            return vResult;
        } catch (Exception ex) {
            throw ex;
        }
    }
}
