package vrimplantacao2.dao.interfaces;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import org.apache.commons.lang3.StringUtils;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto.ProdutoAnteriorDAO;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.vo.cadastro.convenio.transacao.SituacaoTransacaoConveniado;
import vrimplantacao2.vo.cadastro.oferta.SituacaoOferta;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoEstadoCivil;
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ConveniadoIMP;
import vrimplantacao2.vo.importacao.ConvenioEmpresaIMP;
import vrimplantacao2.vo.importacao.ConvenioTransacaoIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.OfertaIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Leandro
 */
public class SysPdvDAO extends InterfaceDAO {

    private TipoConexao tipoConexao;
    private String complementoSistema = "";
    public String FZDCOD = "";
    public String v_pahtFileXls;

    public void setTipoConexao(TipoConexao tipoConexao) {
        this.tipoConexao = tipoConexao;
    }

    public void setComplementoSistema(String complementoSistema) {
        this.complementoSistema = complementoSistema == null ? "" : complementoSistema.trim();
    }

    @Override
    public String getSistema() {
        return (!"".equals(complementoSistema) ? this.complementoSistema + "-" : "") + this.tipoConexao.getSistema();
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();

        try (Statement stm = tipoConexao.getConnection().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	m1.seccod, \n"
                    + "	m1.secdes, \n"
                    + "	m2.grpcod, \n"
                    + "	m2.grpdes, \n"
                    + "	m3.sgrcod, \n"
                    + "	m3.sgrdes \n"
                    + "from \n"
                    + "	secao as m1 \n"
                    + "	left join grupo as m2 on \n"
                    + "		m2.seccod = m1.seccod \n"
                    + "	left join subgrupo as m3 on \n"
                    + "		m3.seccod = m1.seccod and\n"
                    + "		m3.grpcod = m2.grpcod \n"
                    + "order by \n"
                    + "	m1.seccod,\n"
                    + "	m2.grpcod,\n"
                    + "	m3.sgrcod"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setMerc1ID(rst.getString("seccod"));
                    imp.setMerc1Descricao(rst.getString("secdes"));
                    imp.setMerc2ID(rst.getString("grpcod"));
                    imp.setMerc2Descricao(rst.getString("grpdes"));
                    imp.setMerc3ID(rst.getString("sgrcod"));
                    imp.setMerc3Descricao(rst.getString("sgrdes"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();

        try (Statement stm = tipoConexao.getConnection().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	procodsim,\n"
                    + "	similaresdes\n"
                    + "from\n"
                    + "	similares"
            )) {
                while (rst.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("procodsim"));
                    imp.setDescricao(rst.getString("similaresdes"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    private static class Ean {

        public String idProduto;
        public String ean;
        public int qtdEmbalagem;

        public Ean(String idProduto, String ean, int qtdEmbalagem) {
            this.idProduto = idProduto;
            this.ean = ean;
            this.qtdEmbalagem = qtdEmbalagem;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 97 * hash + Objects.hashCode(this.idProduto);
            hash = 97 * hash + Objects.hashCode(this.ean);
            hash = 97 * hash + (int) (Double.doubleToLongBits(this.qtdEmbalagem) ^ (Double.doubleToLongBits(this.qtdEmbalagem) >>> 32));
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Ean other = (Ean) obj;
            if (!Objects.equals(this.idProduto, other.idProduto)) {
                return false;
            }
            if (!Objects.equals(this.ean, other.ean)) {
                return false;
            }
            return Double.doubleToLongBits(this.qtdEmbalagem) == Double.doubleToLongBits(other.qtdEmbalagem);
        }

    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = tipoConexao.getConnection().createStatement()) {
            Map<String, int[]> piscofins = new HashMap<>();
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    p.procod id_produto,\n"
                    + "    pis.impfedst piscofins_entrada,\n"
                    + "    pis.impfedstsai piscofins_saida\n"
                    + "from\n"
                    + "    (select\n"
                    + "         pispro.procod,\n"
                    + "         min(pispro.impfedsim) impfedsim\n"
                    + "     from\n"
                    + "         impostos_federais pis\n"
                    + "         join impostos_federais_produto pispro on\n"
                    + "            pis.impfedsim = pispro.impfedsim\n"
                    + "            and pis.impfedtip = 'P'\n"
                    + "     group by\n"
                    + "         pispro.procod) p\n"
                    + "     join impostos_federais pis on\n"
                    + "        p.impfedsim = pis.impfedsim"
            )) {
                while (rst.next()) {
                    piscofins.put(
                            rst.getString("id_produto"),
                            new int[]{
                                Utils.stringToInt(rst.getString("piscofins_entrada")),
                                Utils.stringToInt(rst.getString("piscofins_saida"))
                            }
                    );
                }
            }

            Map<String, Set<Ean>> eans = new HashMap<>();
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    procod id_produto,\n"
                    + "    procod ean,\n"
                    + "    1 qtdembalagem\n"
                    + "from\n"
                    + "    produto\n"
                    + "union\n"
                    + "select\n"
                    + "    procod id_produto,\n"
                    + "    procodaux ean,\n"
                    + "    coalesce(profatormult, 1) qtdembalagem\n"
                    + "from\n"
                    + "    produtoaux\n"
                    + "order by\n"
                    + "    1"
            )) {
                while (rst.next()) {
                    long ean = Utils.stringToLong(rst.getString("ean"), -2);
                    if (ean > 999999 || !String.valueOf(rst.getString("ean")).equals(rst.getString("id_produto"))) {

                        Set<Ean> ea = eans.get(rst.getString("id_produto"));

                        if (ea == null) {
                            ea = new HashSet<>();
                        }

                        ea.add(
                                new Ean(
                                        rst.getString("id_produto"),
                                        rst.getString("ean"),
                                        Math.round(rst.getFloat("qtdembalagem"))
                                )
                        );

                        eans.put(
                                rst.getString("id_produto"),
                                ea
                        );
                    }
                }
            }

            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "    p.procod id,\n"
                    + "    p.prodes descricaocompleta,\n"
                    + "    p.prodesrdz descricaoreduzida,\n"
                    + "    p.seccod merc1,\n"
                    + "    nullif(p.grpcod, '000') merc2,\n"
                    + "    nullif(p.sgrcod, '000') merc3,\n"
                    + "    p.proestmin estoqueminimo,\n"
                    + "    p.proestmax estoquemaximo,\n"
                    + "    est.estatu estoque,\n"
                    + "    p.proncm ncm,\n"
                    + "    case when p.proforlin = 'S' then 0 else 1 end situacaocadastro,\n"
                    + "    p.proprccst custocomimposto,\n"
                    + "    p.proprccst custosemimposto,\n"
                    + "    p.prodatcadinc datacadastro,\n"
                    + "    p.proiteemb qtdembalagem,\n"
                    + "    round(((proprcvdavar / case when p.proprccst = 0.00 then 1 else p.proprccst end) - 1) * (100),2) margem,\n"
                    + "    p.promrg1 as margem2, \n"
                    + "    proprcvdavar precovenda,\n"
                    + "    items.procodsim id_familiaproduto,\n"
                    + "    p.propesbrt pesobruto,\n"
                    + "    p.propesliq pesoliquido,\n"
                    + "    case p.propesvar\n"
                    + "    when 'S' then 'KG'\n"
                    + "    when 'P' then 'KG'\n"
                    + "    else 'UN' end as tipoembalagem,\n"
                    + "    p.prounid, \n"
                    + "    case when p.proenvbal = 'S' then 1 else 0 end e_balanca,\n"
                    + "    coalesce(p.provld, 0) validade,\n"
                    + "    i.trbtabb icms_cst, \n"
                    + "    i.trbalq icms_aliquota, \n"
                    + "    i.trbred icms_reducao, \n"
                    + "    p.procest cest,\n"
                    + "    p.natcodigo piscofins_natrec\n"
                    + "FROM \n"
                    + "    produto p\n"
                    + "    LEFT JOIN item_similares items ON \n"
                    + "        items.procod = p.procod\n"
                    + "    left join tributacao i on \n"
                    + "        i.trbid = p.trbid\n"
                    + "    left join estoque est on\n"
                    + "        est.PROCOD = p.PROCOD\n"
                    + "ORDER BY \n"
                    + "    p.procod"
            )) {
                Map<Integer, ProdutoBalancaVO> balanca = new ProdutoBalancaDAO().getProdutosBalanca();
                while (rst.next()) {

                    for (Ean ean : eans.get(rst.getString("id"))) {

                        ProdutoIMP imp = new ProdutoIMP();

                        imp.setImportSistema(getSistema());
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportId(rst.getString("id"));
                        imp.setEan(ean.ean);
                        imp.setQtdEmbalagem(ean.qtdEmbalagem);
                        imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                        imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                        imp.setDescricaoGondola(imp.getDescricaoCompleta());
                        imp.setCodMercadologico1(rst.getString("merc1"));
                        imp.setCodMercadologico2(rst.getString("merc2"));
                        imp.setCodMercadologico3(rst.getString("merc3"));

                        ProdutoBalancaVO bal = balanca.get(Utils.stringToInt(rst.getString("id")));
                        if (bal != null) {
                            imp.seteBalanca(true);
                            if (null != bal.getPesavel()) {
                                switch (bal.getPesavel()) {
                                    case "P":
                                        imp.setTipoEmbalagem("KG");
                                        break;
                                    case "U":
                                        imp.setTipoEmbalagem("UN");
                                        break;
                                }
                            }
                            imp.setValidade(bal.getValidade());
                        } else {
                            if (balanca.isEmpty()) {
                                imp.seteBalanca(rst.getBoolean("e_balanca"));
                                imp.setTipoEmbalagem(rst.getString("prounid"));
                            } else {
                                imp.seteBalanca(false);
                                imp.setTipoEmbalagem(rst.getString("prounid"));
                            }
                            imp.setValidade(Utils.stringToInt(rst.getString("validade")));
                        }

                        imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
                        imp.setEstoqueMaximo(rst.getDouble("estoquemaximo"));
                        imp.setEstoque(rst.getDouble("estoque"));
                        imp.setNcm(rst.getString("ncm"));
                        imp.setSituacaoCadastro(rst.getInt("situacaocadastro"));
                        imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                        imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                        imp.setDataCadastro(rst.getDate("datacadastro"));
                        imp.setQtdEmbalagemCotacao(rst.getInt("qtdembalagem"));
                        imp.setMargem(rst.getDouble("margem2"));
                        imp.setPrecovenda(rst.getDouble("precovenda"));
                        imp.setIdFamiliaProduto(rst.getString("id_familiaproduto"));
                        imp.setPesoBruto(rst.getDouble("pesobruto"));
                        imp.setPesoLiquido(rst.getDouble("pesoliquido"));
                        imp.setIcmsCstSaida(Utils.stringToInt(rst.getString("icms_cst")));
                        imp.setIcmsAliqSaida(rst.getDouble("icms_aliquota"));
                        imp.setIcmsReducaoSaida(rst.getDouble("icms_reducao"));
                        imp.setIcmsCstEntrada(Utils.stringToInt(rst.getString("icms_cst")));
                        imp.setIcmsAliqEntrada(rst.getDouble("icms_aliquota"));
                        imp.setIcmsReducaoEntrada(rst.getDouble("icms_reducao"));
                        imp.setCest(rst.getString("cest"));

                        int[] pis = piscofins.get(rst.getString("id"));

                        if (pis != null) {
                            imp.setPiscofinsCstCredito(pis[0]);
                            imp.setPiscofinsCstDebito(pis[1]);
                            imp.setPiscofinsNaturezaReceita(rst.getString("piscofins_natrec"));
                        }

                        result.add(imp);

                    }
                }
            }
        }

        return result;
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = tipoConexao.getConnection().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "procod, "
                    + "proprc1, "
                    + "proprc2, "
                    + "proqtdminprc2 "
                    + "from produto\n"
                    + "where proqtdminprc2 > 1"
            )) {
                while (rst.next()) {

                    int codigoAtual = new ProdutoAnteriorDAO().getCodigoAnterior2(getSistema(), getLojaOrigem(), rst.getString("procod"));

                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("procod"));
                    imp.setEan("99999" + String.valueOf(codigoAtual));
                    imp.setQtdEmbalagem(rst.getInt("proqtdminprc2"));
                    result.add(imp);
                }
            }
            return result;
        }
    }

    @Override
    public List<ProdutoIMP> getProdutos(OpcaoProduto opt) throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        WorkbookSettings settings = new WorkbookSettings();
        Workbook arquivo = Workbook.getWorkbook(new File(v_pahtFileXls), settings);
        Sheet[] sheets = arquivo.getSheets();
        int linha;

        double desconto = 0;

        if (opt == OpcaoProduto.ATACADO) {
            try (Statement stm = tipoConexao.getConnection().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select "
                        + "procod, "
                        + "proprc1, "
                        + "proprc2, "
                        + "proqtdminprc2 "
                        + "from produto\n"
                        + "where proqtdminprc2 > 1"
                )) {
                    while (rst.next()) {

                        int codigoAtual = new ProdutoAnteriorDAO().getCodigoAnterior2(getSistema(), getLojaOrigem(), rst.getString("procod"));

                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("procod"));
                        imp.setEan("99999" + String.valueOf(codigoAtual));
                        imp.setPrecovenda(rst.getDouble("proprc1"));
                        imp.setAtacadoPreco(rst.getDouble("proprc2"));
                        imp.setQtdEmbalagem(rst.getInt("proqtdminprc2"));
                        result.add(imp);
                    }
                }
                return result;
            }
        }

        if (opt == OpcaoProduto.PIS_COFINS) {

            try {

                for (int sh = 0; sh < sheets.length; sh++) {
                    Sheet sheet = arquivo.getSheet(sh);
                    linha = 0;

                    for (int i = 0; i < sheet.getRows(); i++) {
                        linha++;
                        if (linha == 1) {
                            continue;
                        }

                        Cell cellIdProduto = sheet.getCell(0, i);
                        Cell cellCstPisEntrada = sheet.getCell(4, i);
                        Cell cellCstPisSaida = sheet.getCell(6, i);

                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(cellIdProduto.getContents().trim());
                        imp.setPiscofinsCstDebito(cellCstPisSaida.getContents().trim());
                        imp.setPiscofinsCstCredito(cellCstPisEntrada.getContents().trim());
                        result.add(imp);
                    }
                }
                return result;
            } catch (Exception ex) {
                throw ex;
            }

        }
        return null;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();

        try (Statement stm = tipoConexao.getConnection().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	procod as id_produto,\n"
                    + "	forcod as id_fornecedor,\n"
                    + "	prfreffor as ref,\n"
                    + "	prfqtd as qtdemb\n"
                    + "from\n"
                    + "	produto_fornecedor")) {
                while (rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setIdProduto(rs.getString("id_produto"));
                    imp.setIdFornecedor(rs.getString("id_fornecedor"));
                    imp.setCodigoExterno(rs.getString("ref"));
                    imp.setQtdEmbalagem(rs.getDouble("qtdemb"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    public List<FornecedorIMP> getFornecedores2() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();

        try (Statement stm = tipoConexao.getConnection().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    f.forcod id,\n"
                    + "    f.fordes razao,\n"
                    + "    f.forfan fantasia,\n"
                    + "    f.forcgc cnpj,\n"
                    + "    f.forcgf inscricaoestadual,\n"
                    + "    f.forend endereco,\n"
                    + "    f.fornum numero,\n"
                    + "    f.forcmp complemento,\n"
                    + "    f.forbai bairro,\n"
                    + "    f.forcodibge ibge_municipio,\n"
                    + "    f.forcep cep,\n"
                    + "    f.fortel telefone,\n"
                    + "    " + (tipoConexao == TipoConexao.FIREBIRD ? "current_date" : "getdate()") + " datacadastro,\n"
                    + "    f.forobs observacao,\n"
                    + "    f.forprz prazoentrega,\n"
                    + "    f.forcon contato,\n"
                    + "    f.forfax fax\n"
                    + "from\n"
                    + "    fornecedor f\n"
                    + "where\n"
                    + "    f.forcod != '0000'\n"
                    + "order by\n"
                    + "    1"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    imp.setIe_rg(rst.getString("inscricaoestadual"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setIbge_municipio(Utils.stringToInt(rst.getString("ibge_municipio")));
                    imp.setCep(rst.getString("cep"));
                    imp.setTel_principal(rst.getString("telefone"));
                    imp.setDatacadastro(rst.getDate("datacadastro"));
                    imp.setObservacao(rst.getString("observacao"));
                    imp.setPrazoEntrega(rst.getInt("prazoentrega"));
                    String contato = Utils.acertarTexto(rst.getString("contato"));
                    if (!"".equals(contato)) {
                        imp.addContato(contato, rst.getString("telefone"), null, TipoContato.COMERCIAL, "");
                    }
                    imp.addTelefone("FAX", rst.getString("fax"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();

        try (Statement stm = tipoConexao.getConnection().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    c.clicod id,\n"
                    + "    c.clicpfcgc cnpj,\n"
                    + "    c.clirgcgf inscricaoestadual,\n"
                    + "    c.clirgexp emissor,\n"
                    + "    c.clides razao,\n"
                    + "    c.clifan fantasia,\n"
                    + "    case when st.stablq = 'S' then 1 else 0 end bloqueado,\n"
                    + "    c.clidtblo databloqueio,\n"
                    + "    c.cliend endereco,\n"
                    + "    c.clinum numero,\n"
                    + "    c.clicmp complemento,\n"
                    + "    c.clibai bairro,\n"
                    + "    c.clicodigoibge ibge_municipio,\n"
                    + "    c.clicid cidade,\n"
                    + "    c.cliest estado,\n"
                    + "    c.clicep cep,\n"
                    + "    c.cliestciv estadocivil,\n"
                    + "    c.clidtcad datacadastro,\n"
                    + "    c.clidtnas datanascimento,\n"
                    + "    c.clisex sexo,\n"
                    + "    c.cliemptrb empresa,\n"
                    + "    c.cliempend empresa_endereco,\n"
                    + "    c.cliemptel empresa_telefone,\n"
                    + "    c.cliempcar empresa_cargo,\n"
                    + "    c.clisal empresa_salario,\n"
                    + "    c.clilimcre valorlimite,\n"
                    + "    c.clipai nomepai,\n"
                    + "    c.climae nomemae,\n"
                    + "    c.cliobs observacao,\n"
                    + "    c.clidiafec diavencimento,\n"
                    + "    c.clitel telefone,\n"
                    + "    c.clitel2 telefone2,\n"
                    + "    c.cliemail email,\n"
                    + "    c.clifax fax,\n"
                    + "    c.cliendcob cob_endereco,\n"
                    + "    c.clinumcob cob_numero,\n"
                    + "    c.clicmp cob_complemento,\n"
                    + "    c.clibai cob_bairro,\n"
                    + "    c.clicidcob cob_cidade,\n"
                    + "    c.cliestcob cob_estado,\n"
                    + "    c.clicepcob cob_cep,\n"
                    + "    c.cliprz prazopagamento,\n"
                    + "    c.cliinscmun inscricaomunicipal,\n"
                    + "    c.clilimcre2 limitecompra\n"
                    + "from\n"
                    + "    cliente c\n"
                    + "    left join status st on\n"
                    + "        c.stacod = st.stacod\n"
                    + "where\n"
                    + "    c.clicod != '000000000000000'\n"
                    + "order by\n"
                    + "    c.clicod"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    imp.setIe_rg(rst.getString("inscricaoestadual"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setIbge_municipio(Integer.parseInt(Utils.formataNumero(rst.getString("ibge_municipio"))));
                    imp.setCep(rst.getString("cep"));
                    imp.setTel_principal(rst.getString("telefone"));
                    imp.setDatacadastro(rst.getDate("datacadastro"));
                    imp.setObservacao(rst.getString("observacao"));

                    if ((rst.getString("telefone2") != null)
                            && (!rst.getString("telefone2").trim().isEmpty())) {
                        imp.addContato(
                                "TELEFONE 2",
                                rst.getString("telefone2"),
                                null,
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    if ((rst.getString("email") != null)
                            && (!rst.getString("email").trim().isEmpty())) {
                        imp.addContato(
                                "EMAIL",
                                null,
                                null,
                                TipoContato.NFE,
                                rst.getString("email").toLowerCase()
                        );
                    }
                    if ((rst.getString("fax") != null)
                            && (!rst.getString("fax").trim().isEmpty())) {
                        imp.addContato(
                                "FAX",
                                rst.getString("fax"),
                                null,
                                TipoContato.COMERCIAL,
                                null
                        );
                    }

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();

        try (Statement stm = tipoConexao.getConnection().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    c.clicod id,\n"
                    + "    c.clicpfcgc cnpj,\n"
                    + "    c.clirgcgf inscricaoestadual,\n"
                    + "    c.clirgexp emissor,\n"
                    + "    c.clides razao,\n"
                    + "    c.clifan fantasia,\n"
                    + "    case when st.stablq = 'S' then 1 else 0 end bloqueado,\n"
                    + "    c.clidtblo databloqueio,\n"
                    + "    c.cliend endereco,\n"
                    + "    c.clinum numero,\n"
                    + "    c.clicmp complemento,\n"
                    + "    c.clibai bairro,\n"
                    + "    c.clicodigoibge ibge_municipio,\n"
                    + "    c.clicid cidade,\n"
                    + "    c.cliest estado,\n"
                    + "    c.clicep cep,\n"
                    + "    c.cliestciv estadocivil,\n"
                    + "    c.clidtcad datacadastro,\n"
                    + "    c.clidtnas datanascimento,\n"
                    + "    c.clisex sexo,\n"
                    + "    c.cliemptrb empresa,\n"
                    + "    c.cliempend empresa_endereco,\n"
                    + "    c.cliemptel empresa_telefone,\n"
                    + "    c.cliempcar empresa_cargo,\n"
                    + "    c.clisal empresa_salario,\n"
                    + "    c.clilimcre valorlimite,\n"
                    + "    c.clipai nomepai,\n"
                    + "    c.climae nomemae,\n"
                    + "    c.cliobs observacao2,\n"
                    + "    c.clidiafec diavencimento,\n"
                    + "    c.clitel telefone,\n"
                    + "    c.clitel2 telefone2,\n"
                    + "    c.cliemail email,\n"
                    + "    c.clifax fax,\n"
                    + "    c.cliendcob cob_endereco,\n"
                    + "    c.clinumcob cob_numero,\n"
                    + "    c.clicmp cob_complemento,\n"
                    + "    c.clibai cob_bairro,\n"
                    + "    c.clicidcob cob_cidade,\n"
                    + "    c.cliestcob cob_estado,\n"
                    + "    c.clicepcob cob_cep,\n"
                    + "    c.cliprz prazopagamento,\n"
                    + "    c.cliinscmun inscricaomunicipal,\n"
                    + "    c.clilimcre2 limitecompra\n"
                    + "from\n"
                    + "    cliente c\n"
                    + "    left join status st on\n"
                    + "        c.stacod = st.stacod\n"
                    + "where\n"
                    + "    c.clicod != '000000000000000'\n"
                    + "order by\n"
                    + "    c.clicod"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rst.getString("id"));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setInscricaoestadual(rst.getString("inscricaoestadual"));
                    imp.setOrgaoemissor(rst.getString("emissor"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setBloqueado(rst.getBoolean("bloqueado"));
                    imp.setDataBloqueio(rst.getDate("databloqueio"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipioIBGE(Integer.parseInt(Utils.formataNumero(rst.getString("ibge_municipio"))));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("estado"));
                    imp.setCep(rst.getString("cep"));
                    switch (Utils.acertarTexto(rst.getString("estadocivil"))) {
                        case "S":
                            imp.setEstadoCivil(TipoEstadoCivil.SOLTEIRO);
                            break;
                        case "O":
                            imp.setEstadoCivil(TipoEstadoCivil.CASADO);
                            break;
                        default:
                            imp.setEstadoCivil(TipoEstadoCivil.OUTROS);
                            break;
                    }
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setDataNascimento(rst.getDate("datanascimento"));
                    switch (Utils.acertarTexto(rst.getString("sexo"))) {
                        case "F":
                            imp.setSexo(TipoSexo.FEMININO);
                            break;
                        default:
                            imp.setSexo(TipoSexo.MASCULINO);
                            break;
                    }
                    imp.setEmpresa(rst.getString("empresa"));
                    imp.setEmpresaEndereco(rst.getString("empresa_endereco"));
                    imp.setEmpresaTelefone(rst.getString("empresa_telefone"));
                    imp.setCargo(rst.getString("empresa_cargo"));
                    imp.setSalario(rst.getDouble("empresa_salario"));
                    imp.setValorLimite(rst.getDouble("valorlimite"));
                    imp.setNomePai(rst.getString("nomepai"));
                    imp.setNomeMae(rst.getString("nomemae"));
                    imp.setObservacao2(rst.getString("observacao2"));
                    imp.setDiaVencimento(Utils.stringToInt(rst.getString("diavencimento")));
                    imp.setTelefone(rst.getString("telefone"));
                    imp.setCelular(rst.getString("telefone2"));
                    imp.setEmail(rst.getString("email"));
                    imp.setFax(rst.getString("fax"));
                    imp.setCobrancaEndereco(rst.getString("cob_endereco"));
                    imp.setCobrancaNumero(rst.getString("cob_numero"));
                    imp.setCobrancaComplemento(rst.getString("cob_complemento"));
                    imp.setCobrancaBairro(rst.getString("cob_bairro"));
                    imp.setCobrancaMunicipio(rst.getString("cob_cidade"));
                    imp.setCobrancaUf(rst.getString("cob_estado"));
                    imp.setCobrancaCep(rst.getString("cob_cep"));
                    imp.setPrazoPagamento(rst.getInt("prazopagamento"));
                    imp.setInscricaoMunicipal(rst.getString("inscricaomunicipal"));
                    imp.setLimiteCompra(rst.getDouble("limitecompra"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    public List<String> getFinalizadora() throws Exception {
        List<String> result = new ArrayList<>();

        try (Statement stm = tipoConexao.getConnection().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select FZDCOD, FZDDES from finalizadora order by FZDCOD"
            )) {
                while (rst.next()) {
                    result.add(rst.getString("FZDCOD") + " - " + rst.getString("FZDDES"));
                }
            }
            return result;
        }
    }

    public List<CreditoRotativoIMP> getCreditoRotativo2() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();

        try (Statement stm = tipoConexao.getConnection().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "    CTRID,\n"
                    + "    ctrnum,\n"
                    + "    clicod,\n"
                    + "    cxanum,\n"
                    + "    ctrdatemi,\n"
                    + "    ctrdatvnc,\n"
                    + "    ctrvlrdev,\n"
                    + "    ctrobs\n"
                    + "FROM CONTARECEBER\n"
                    + "WHERE \n" //(COALESCE(CTRVLRPAG,0) < CTRVLRNOM)
                    + "COALESCE(ctrvlrdev,0) > 0 "
                    + "AND FZDCOD IN (" + FZDCOD + ") " //+ "    (COALESCE(FZDCOD,'005') IN ('005'))"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();

                    imp.setId(rst.getString("CTRID"));
                    imp.setNumeroCupom(rst.getString("ctrnum"));
                    imp.setIdCliente(rst.getString("clicod"));
                    imp.setEcf(rst.getString("cxanum"));
                    imp.setDataEmissao(rst.getDate("ctrdatemi"));
                    imp.setDataVencimento(rst.getDate("ctrdatvnc"));
                    imp.setValor(rst.getDouble("ctrvlrdev"));
                    imp.setObservacao(rst.getString("ctrobs"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();
        java.sql.Date dataEmissao, dataVencimento;
        DateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
        WorkbookSettings settings = new WorkbookSettings();
        Workbook arquivo = Workbook.getWorkbook(new File(v_pahtFileXls), settings);
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
                    } else if (Utils.encontrouLetraCampoNumerico(sheet.getCell(0, i).getContents())) {
                        continue;
                    } else if ((sheet.getCell(0, i).getContents().trim().isEmpty())
                            || (sheet.getCell(0, i).getContents() == null)) {
                        continue;
                    }

                    Cell cellIdVenda = sheet.getCell(0, i);
                    Cell cellCodCliente = sheet.getCell(2, i);
                    Cell cellCupom = sheet.getCell(4, i);
                    Cell cellEmissao = sheet.getCell(5, i);
                    Cell cellVencimento = sheet.getCell(6, i);
                    Cell cellValor = sheet.getCell(15, i);
                    Cell cellHistorico = sheet.getCell(8, i);
                    Cell cellJuros = sheet.getCell(11, i);
                    Cell cellDesconto = sheet.getCell(13, i);
                    Cell cellMulta = sheet.getCell(12, i);

                    if ((cellEmissao.getContents() != null)
                            && (!cellEmissao.getContents().trim().isEmpty())) {
                        dataEmissao = new java.sql.Date(fmt.parse(cellEmissao.getContents()).getTime());
                    } else {
                        dataEmissao = new java.sql.Date(new java.util.Date().getTime());
                    }

                    if ((cellVencimento.getContents() != null)
                            && (!cellVencimento.getContents().trim().isEmpty())) {
                        dataVencimento = new java.sql.Date(fmt.parse(cellVencimento.getContents()).getTime());
                    } else {
                        dataVencimento = new java.sql.Date(new java.util.Date().getTime());
                    }

                    String id_cliente = cellCodCliente.getContents().substring(0, cellCodCliente.getContents().indexOf("-"));

                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(cellIdVenda.getContents());
                    imp.setIdCliente(id_cliente.trim());
                    imp.setDataEmissao(dataEmissao);
                    imp.setDataVencimento(dataVencimento);
                    imp.setValor(Double.parseDouble(cellValor.getContents().replace(",", ".")));
                    imp.setJuros(Double.parseDouble(cellJuros.getContents().replace(",", ".")));
                    imp.setMulta(Double.parseDouble(cellMulta.getContents().replace(",", ".")));
                    imp.setNumeroCupom(cellCupom.getContents());
                    imp.setObservacao(cellHistorico.getContents());

                    System.out.println(imp.getIdCliente());

                    result.add(imp);
                }
            }
            return result;
        } catch (Exception ex) {
            throw ex;
        }
    }

    @Override
    public List<OfertaIMP> getOfertas(Date dataTermino) throws Exception {
        List<OfertaIMP> result = new ArrayList<>();

        try (Statement stm = tipoConexao.getConnection().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select distinct\n"
                    + "    oft.procod id_produto,\n"
                    + "    oft.pprdatini datainicial,\n"
                    + "    oft.pprdatfim datafinal,\n"
                    + "    oft.pprprcprog precooferta\n"
                    + "from\n"
                    + "    preco_programado oft\n"
                    + "where\n"
                    + "    oft.pprdatfim >= '" + new SimpleDateFormat("yyyy-MM-dd").format(dataTermino) + "'\n"
                    + "order by\n"
                    + "    id_produto"
            )) {
                while (rst.next()) {
                    OfertaIMP imp = new OfertaIMP();

                    imp.setIdProduto(rst.getString("id_produto"));
                    imp.setDataInicio(rst.getDate("datainicial"));
                    imp.setDataFim(rst.getDate("datafinal"));
                    imp.setPrecoOferta(rst.getDouble("precooferta"));
                    imp.setSituacaoOferta(SituacaoOferta.ATIVO);

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ConvenioEmpresaIMP> getConvenioEmpresa() throws Exception {
        List<ConvenioEmpresaIMP> result = new ArrayList<>();

        try (Statement stm = tipoConexao.getConnection().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    c.clicod id, \n"
                    + "    c.clides razao,\n"
                    + "    c.clicpfcgc cnpj,\n"
                    + "    c.clirgcgf inscricaoestadual,\n"
                    + "    c.cliend endereco,\n"
                    + "    c.clinum numero,\n"
                    + "    c.clicmp complemento,\n"
                    + "    c.clibai bairro,\n"
                    + "    c.clicodigoibge ibge_municipio,\n"
                    + "    c.clicid cidade,\n"
                    + "    c.cliest estado,\n"
                    + "    c.clicep cep,\n"
                    + "    c.clitel telefone,\n"
                    + "    co.cnvdatini datainicio,\n"
                    + "    co.cnvdatvnc datatermino,\n"
                    + "    case when co.cnvsta = 'N' then 0 else 1 end situacaocadastro,\n"
                    + "    co.cnvdiafec diapagamento,\n"
                    + "    co.cnvdes observacao\n"
                    + "from\n"
                    + "    convenio co\n"
                    + "    join cliente c on\n"
                    + "        co.clicod = c.clicod\n"
                    + "order by\n"
                    + "    1"
            )) {
                while (rst.next()) {
                    ConvenioEmpresaIMP imp = new ConvenioEmpresaIMP();

                    imp.setId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setInscricaoEstadual(rst.getString("inscricaoestadual"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setIbgeMunicipio(rst.getInt("ibge_municipio"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("estado"));
                    imp.setCep(rst.getString("cep"));
                    imp.setTelefone(rst.getString("telefone"));
                    imp.setDataInicio(rst.getDate("datainicio"));
                    imp.setDataTermino(rst.getDate("datatermino"));
                    imp.setSituacaoCadastro(rst.getInt("situacaocadastro") == 0 ? SituacaoCadastro.EXCLUIDO : SituacaoCadastro.ATIVO);
                    imp.setDiaPagamento(rst.getInt("diapagamento"));
                    imp.setObservacoes(rst.getString("observacao"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ConveniadoIMP> getConveniado() throws Exception {
        List<ConveniadoIMP> result = new ArrayList<>();

        try (Statement stm = tipoConexao.getConnection().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    c.clicod id, \n"
                    + "    c.clides razao,\n"
                    + "    co.cnvcod id_empresa,\n"
                    + "    c.clicpfcgc cnpj,\n"
                    + "    co.cncsta status,\n"
                    + "    co.cnclimcre limitedecredito\n"
                    + "from\n"
                    + "    convenio_cliente co\n"
                    + "    join cliente c on\n"
                    + "        co.clicod = c.clicod\n"
                    + "order by\n"
                    + "    1"
            )) {
                while (rst.next()) {
                    ConveniadoIMP imp = new ConveniadoIMP();

                    imp.setId(rst.getString("id"));
                    imp.setNome(rst.getString("razao"));
                    imp.setIdEmpresa(rst.getString("id_empresa"));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setSituacaoCadastro("N".equals(rst.getString("status")) ? SituacaoCadastro.EXCLUIDO : SituacaoCadastro.ATIVO);
                    imp.setConvenioLimite(rst.getDouble("limitedecredito"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ConvenioTransacaoIMP> getConvenioTransacao() throws Exception {
        List<ConvenioTransacaoIMP> result = new ArrayList<>();

        try (Statement stm = tipoConexao.getConnection().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    ""
            )) {
                while (rst.next()) {
                    ConvenioTransacaoIMP imp = new ConvenioTransacaoIMP();

                    imp.setId(rst.getString(""));
                    imp.setDataHora(rst.getTimestamp(""));
                    imp.setDataMovimento(rst.getDate(""));
                    imp.setEcf(rst.getString(""));
                    imp.setFinalizado(rst.getBoolean(""));
                    imp.setIdConveniado(rst.getString(""));
                    imp.setNumeroCupom(rst.getString(""));
                    imp.setObservacao(rst.getString(""));
                    imp.setSituacaoTransacaoConveniado(SituacaoTransacaoConveniado.PENDENTE);
                    imp.setValor(rst.getDouble(""));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    public List<Estabelecimento> getLojasCliente() throws SQLException {
        List<Estabelecimento> result = new ArrayList<>();

        try (Statement stm = tipoConexao.getConnection().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT prpcod, prpfan FROM PROPRIO"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("prpcod"), rst.getString("prpfan")));
                }
            }
        }

        return result;
    }

    public static enum TipoConexao {

        FIREBIRD {
                    @Override
                    public Connection getConnection() {
                        return ConexaoFirebird.getConexao();
                    }

                    @Override
                    public String getSistema() {
                        return "SysPdv(FIREBIRD)";
                    }
                },
        SQL_SERVER {
                    @Override
                    public Connection getConnection() {
                        return ConexaoSqlServer.getConexao();
                    }

                    @Override
                    public String getSistema() {
                        return "SysPdv(SQLSERVER)";
                    }
                };

        public abstract Connection getConnection();

        public abstract String getSistema();

        public String getLojasClienteSQL() {
            return "";
        }

    }

}
