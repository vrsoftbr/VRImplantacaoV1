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
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import vrframework.classe.ProgressBar;
import vrimplantacao.classe.ConexaoOracle;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.gui.component.mapatiporecebiveis.FinanceiroProvider;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoEstadoCivil;
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.vo.importacao.ChequeIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTipoRecebivelIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.RecebimentoCaixaIMP;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;

/**
 * CLASSE REFATORADA DA 2.0 E NÃO VALIDADA;
 *
 * @author Bruno
 */
public class Apollo2_5DAO extends InterfaceDAO implements FinanceiroProvider, MapaTributoProvider {

    private static final Logger LOG = Logger.getLogger(Apollo2_5DAO.class.getName());

    private String schema;

    public void setSchema(String schema) {
        this.schema = schema;
    }

    private String getSchema() {
        if (this.schema != null && !"".equals(this.schema.trim())) {
            return this.schema + ".";
        } else {
            return "";
        }
    }

    @Override
    public String getSistema() {
        return "APOLLO";
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();

        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "    codempresa,\n"
                    + "    fantasia\n"
                    + "FROM \n"
                    + "    " + getSchema() + "empresas \n"
                    + "order by \n"
                    + "    codempresa"
            )) {
                while (rst.next()) {
                    result.add(
                            new Estabelecimento(
                                    rst.getString("codempresa"),
                                    rst.getString("fantasia")
                            )
                    );
                }
            }
        }

        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select distinct\n"
                    + "    p.coddpto,\n"
                    + "    d.descricao departamento,\n"
                    + "    p.codgrupo,\n"
                    + "    g.descricao grupo,\n"
                    + "    p.codsubgrupo,\n"
                    + "    s.descricao subgrupo\n"
                    + "from \n"
                    + "    " + getSchema() + "produtos p\n"
                    + "    left join (select * from " + getSchema() + "familias_prod) g on p.codgrupo = g.codfamilia\n"
                    + "    left join (select * from " + getSchema() + "familias_prod) d on p.coddpto = d.codfamilia\n"
                    + "    left join (select * from " + getSchema() + "familias_prod) s on p.codsubgrupo = s.codfamilia\n"
                    + "where\n"
                    + "    not p.coddpto is null and\n"
                    + "    not p.codgrupo is null and\n"
                    + "    not p.codsubgrupo is null\n"
                    + "order by\n"
                    + "    p.coddpto,\n"
                    + "    p.codgrupo,\n"
                    + "    p.codsubgrupo"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setMerc1ID(rst.getString("coddpto"));
                    imp.setMerc1Descricao(rst.getString("departamento"));
                    imp.setMerc2ID(rst.getString("codgrupo"));
                    imp.setMerc2Descricao(rst.getString("grupo"));
                    imp.setMerc3ID(rst.getString("codsubgrupo"));
                    imp.setMerc3Descricao(rst.getString("subgrupo"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    codfamilia,\n"
                    + "    descricao\n"
                    + "from\n"
                    + "    " + getSchema() + "familias_prod\n"
                    + "where\n"
                    + "    tipo = 'P'\n"
                    + "order by\n"
                    + "    codfamilia"
            )) {
                while (rst.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("codfamilia"));
                    imp.setDescricao(rst.getString("descricao"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    p.idproduto id,\n"
                    + "    p.DTCADASTRO datacadastro,\n"
                    + "    ean.codbar ean,\n"
                    + "    ean.fatoremb qtdEmbalagem,\n"
                    + "    p.unidade tipoembalagem,\n"
                    + "    case p.balanca when 'S' then 1 else 0 end ebalanca,\n"
                    + "    coalesce(p.validade, 0) validade,\n"
                    + "    p.descricao descricaocompleta,\n"
                    + "    p.coddpto merc1,\n"
                    + "    p.codgrupo merc2,\n"
                    + "    p.codsubgrupo merc3,\n"
                    + "    p.codgrupopreco id_familiaproduto,\n"
                    + "    coalesce(p.PESOBRUTO_PRODUTO, 0) pesobruto,\n"
                    + "    coalesce(p.PESOLIQ_PRODUTO, 0) pesoliquido,\n"
                    + "    est.minimo estoqueminimo,\n"
                    + "    est.maximo estoquemaximo,\n"
                    + "    est.qtde estoque,\n"
                    + "    prec.markup margem,\n"
                    + "    COALESCE(prec.vrcustorep, prec.VRCUSTOCSI, prec.vrcusto) custocomimposto,\n"
                    + "    COALESCE(prec.VRCUSTOCSI, prec.VRCUSTOREP, prec.vrcusto) custosemimposto,\n"
                    + "    prec.vrvenda preco,\n"
                    + "    case when p.ativo = 'N' then 0 else 1 end ativo,\n"
                    + "    p.ncmsh ncm,\n"
                    + "    p.cest,\n"
                    + "    piscofins.cst_pis_ent piscofins_credito,\n"
                    + "    piscofins.cst_pis_sai piscofins_debito,\n"
                    + "    nat.idbasecreditoisento piscofins_natrec,\n"
                    + "    icms.cst icms_cst,\n"
                    + "    icms.icm icms_aliquota\n"
                    + "from\n"
                    + "    " + getSchema() + "produtos p\n"
                    + "    join " + getSchema() + "empresas emp on emp.codempresa = " + getLojaOrigem() + "\n"
                    + "    left join (\n"
                    + "        select\n"
                    + "            idproduto,\n"
                    + "            CODBARRA codbar,\n"
                    + "            1 fatoremb\n"
                    + "        from\n"
                    + "            " + getSchema() + "produtos\n"
                    + "        union\n"
                    + "        select\n"
                    + "            idproduto,\n"
                    + "            codauxiliar codbar,\n"
                    + "            case when coalesce(fatoremb, 1) < 1 then 1 else coalesce(fatoremb, 1) end fatoremb\n"
                    + "        from\n"
                    + "            " + getSchema() + "codauxiliar\n"
                    + "    ) ean on p.idproduto = ean.idproduto\n"
                    + "    left join " + getSchema() + "estoque est on est.idempresa = emp.codempresa and est.idproduto = p.idproduto\n"
                    + "    left join " + getSchema() + "multi_preco prec on prec.idempresa = emp.codempresa and prec.idproduto = p.idproduto\n"
                    + "    left join " + getSchema() + "piscofins piscofins on piscofins.idpiscofins = p.idpiscofins\n"
                    + "    left join " + getSchema() + "pc_tipocreditoisento nat on nat.idtabela = p.idtabela\n"
                    + "    left join " + getSchema() + "det_aliquota icms on icms.uf = emp.uf and icms.aliquota = p.aliquota\n"
                    + "order by\n"
                    + "    id"
            )) {
                int c1 = 0, c2 = 0;
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setDataCadastro(rst.getTimestamp("datacadastro"));
                    imp.setEan(rst.getString("ean"));
                    imp.setQtdEmbalagem(rst.getInt("qtdEmbalagem"));
                    imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
                    imp.seteBalanca(rst.getBoolean("ebalanca"));
                    imp.setValidade(rst.getInt("validade"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    imp.setCodMercadologico1(rst.getString("merc1"));
                    imp.setCodMercadologico2(rst.getString("merc2"));
                    imp.setCodMercadologico3(rst.getString("merc3"));
                    imp.setIdFamiliaProduto(rst.getString("id_familiaproduto"));
                    imp.setPesoBruto(rst.getDouble("pesobruto"));
                    imp.setPesoLiquido(rst.getDouble("pesoliquido"));
                    imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
                    imp.setEstoqueMaximo(rst.getDouble("estoquemaximo"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setPrecovenda(rst.getDouble("preco"));
                    imp.setSituacaoCadastro(SituacaoCadastro.getById(rst.getInt("ativo")));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstCredito(rst.getInt("piscofins_credito"));
                    imp.setPiscofinsCstDebito(rst.getInt("piscofins_debito"));
                    imp.setPiscofinsNaturezaReceita(rst.getInt("piscofins_natrec"));
                    imp.setIcmsCst(rst.getInt("icms_cst"));
                    imp.setIcmsAliq(rst.getDouble("icms_aliquota"));

                    result.add(imp);

                    c1++;
                    c2++;

                    if (c1 == 1000) {
                        c1 = 0;
                        ProgressBar.setStatus("Carregando produtos..." + c2);
                    }
                }
            }
        }

        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    f.codparceiro id,\n"
                    + "    f.razao,\n"
                    + "    f.fantasia,\n"
                    + "    e.cnpj_cpf,\n"
                    + "    e.rg_insc,\n"
                    + "    case f.ativado when 'N' then 0 else 1 end ativo,\n"
                    + "    case f.bloqued when 'S' then 1 else 0 end bloqueado,\n"
                    + "    e.endereco,\n"
                    + "    e.numero,\n"
                    + "    e.complemento,\n"
                    + "    e.bairro,\n"
                    + "    e.cidade,\n"
                    + "    e.uf,\n"
                    + "    e.idcidade,\n"
                    + "    e.cep,\n"
                    + "    f.telefoneempresa,\n"
                    + "    f.diavisita prazovisita,\n"
                    + "    f.prazo_entrega prazoentrega,\n"
                    + "    f.obs observacao,\n"
                    + "    f.dtcadastro\n"
                    + "from \n"
                    + "    " + getSchema() + "parceiros f\n"
                    + "    left join " + getSchema() + "parceiros_end e on f.codparceiro = e.codparceiro\n"
                    + "where\n"
                    + "    f.frn = 'S'\n"
                    + "order by\n"
                    + "    f.codparceiro"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("cnpj_cpf"));
                    imp.setIe_rg(rst.getString("rg_insc"));
                    imp.setAtivo(rst.getBoolean("ativo"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("uf"));
                    imp.setIbge_municipio(rst.getInt("idcidade"));
                    imp.setCep(rst.getString("cep"));
                    imp.setTel_principal(rst.getString("telefoneempresa"));
                    imp.setPrazoVisita(rst.getInt("prazovisita"));
                    imp.setPrazoEntrega(rst.getInt("prazoentrega"));
                    imp.setObservacao(rst.getString("observacao"));
                    imp.setDatacadastro(rst.getTimestamp("dtcadastro"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    f.codparceiro id,\n"
                    + "    e.cnpj_cpf,\n"
                    + "    e.rg_insc,\n"
                    + "    f.razao,\n"
                    + "    f.fantasia,\n"
                    + "    case f.ativado when 'N' then 0 else 1 end ativo,\n"
                    + "    case f.bloqued when 'S' then 1 else 0 end bloqueado,\n"
                    + "    e.endereco,\n"
                    + "    e.numero,\n"
                    + "    e.complemento,\n"
                    + "    e.bairro,\n"
                    + "    e.cidade,\n"
                    + "    e.uf,\n"
                    + "    e.idcidade,\n"
                    + "    e.cep,\n"
                    + "    f.estado_civil,\n"
                    + "    f.dtnascimento,\n"
                    + "    f.dtcadastro,\n"
                    + "    case f.sexo when 'F' then 0 else 1  end sexo,\n"
                    + "    f.empresatrabalha empresa,\n"
                    + "    f.telefoneempresa,\n"
                    + "    f.cargo,\n"
                    + "    f.renda salario,\n"
                    + "    f.limite_especial limite,\n"
                    + "    f.diasfinan diavencimento\n"
                    + "from \n"
                    + "    " + getSchema() + "parceiros f\n"
                    + "    left join " + getSchema() + "parceiros_end e on f.codparceiro = e.codparceiro\n"
                    + "where\n"
                    + "    f.cli = 'S'\n"
                    + "order by\n"
                    + "    f.codparceiro"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rst.getString("id"));
                    imp.setCnpj(rst.getString("cnpj_cpf"));
                    imp.setInscricaoestadual(rst.getString("rg_insc"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setAtivo(rst.getBoolean("ativo"));
                    imp.setBloqueado(rst.getBoolean("bloqueado"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("uf"));
                    imp.setMunicipioIBGE(rst.getInt("idcidade"));
                    imp.setCep(rst.getString("cep"));
                    imp.setEstadoCivil(TipoEstadoCivil.NAO_INFORMADO);
                    imp.setDataNascimento(rst.getTimestamp("dtnascimento"));
                    imp.setDataCadastro(rst.getTimestamp("dtcadastro"));
                    imp.setSexo("F".equals(rst.getString("sexo")) ? TipoSexo.FEMININO : TipoSexo.MASCULINO);
                    imp.setEmpresa(rst.getString("empresa"));
                    imp.setTelefone(rst.getString("telefoneempresa"));
                    imp.setCargo(rst.getString("cargo"));
                    imp.setSalario(rst.getDouble("salario"));
                    imp.setValorLimite(rst.getDouble("limite"));
                    imp.setDiaVencimento(rst.getInt("diavencimento"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    r.codrcb id,\n"
                    + "    r.dtcadastro datacadastro,\n"
                    + "    coalesce(r.nrocupom, r.docnf) cupom,\n"
                    + "    r.codpdv ecf,\n"
                    + "    r.valor,\n"
                    + "    r.obs,\n"
                    + "    r.codparceiro idcliente,\n"
                    + "    r.dtvenc datavencimento,\n"
                    + "    r.nrodup parcela,\n"
                    + "    r.txjuros juros\n"
                    + "from \n"
                    + "    " + getSchema() + "areceber r\n"
                    + "where \n"
                    + "    r.quitada = 'N' and\n"
                    + "    r.codempresa = " + getLojaOrigem() + "\n"
                    + "order by\n"
                    + "    r.codrcb"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();

                    imp.setId(rst.getString("id"));
                    imp.setDataEmissao(rst.getTimestamp("datacadastro"));
                    imp.setNumeroCupom(rst.getString("cupom"));
                    imp.setEcf(rst.getString("ecf"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setObservacao(rst.getString("obs"));
                    imp.setIdCliente(rst.getString("idcliente"));
                    imp.setDataVencimento(rst.getTimestamp("datavencimento"));
                    imp.setParcela(Utils.stringToInt(rst.getString("datavencimento")));
                    imp.setJuros(rst.getDouble("juros"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ChequeIMP> getCheques() throws Exception {
        List<ChequeIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    ch.codchq id,\n"
                    + "    e.cnpj_cpf,\n"
                    + "    ch.nrocheque cheque,\n"
                    + "    bc.agencia,\n"
                    + "    cc.nroconta,\n"
                    + "    ch.dtemissao,\n"
                    + "    ch.nropedido cupom,\n"
                    + "    ch.codpdv,\n"
                    + "    ch.valor,\n"
                    + "    cl.razao,\n"
                    + "    ch.observacao\n"
                    + "from\n"
                    + "    " + getSchema() + "cheque ch\n"
                    + "    left join " + getSchema() + "parceiros cl on ch.codparceiro = cl.codparceiro\n"
                    + "    left join " + getSchema() + "parceiros_end e on cl.codparceiro = e.codparceiro\n"
                    + "    left join " + getSchema() + "bancos bc on ch.codbco = bc.codbco\n"
                    + "    left join " + getSchema() + "contas_bancarias cc on ch.codconta = cc.codconta and ch.codbco = cc.codbco\n"
                    + "where\n"
                    + "    ch.baixado != 'S' and\n"
                    + "    ch.idempresa = " + getLojaOrigem()
            )) {
                while (rst.next()) {
                    ChequeIMP imp = new ChequeIMP();

                    imp.setId(rst.getString("id"));
                    imp.setCpf(rst.getString("cnpj_cpf"));
                    imp.setNumeroCheque(rst.getString("cheque"));
                    String aux = rst.getString("agencia");
                    if (aux != null && aux.contains(".") && aux.length() == 8) {
                        String banco = aux.substring(5);
                        String agencia = aux.substring(0, 4);

                        imp.setBanco(Utils.stringToInt(banco));
                        imp.setAgencia(agencia);
                    }
                    imp.setConta(rst.getString("nroconta"));
                    imp.setDate(rst.getTimestamp("dtemissao"));
                    imp.setNumeroCupom(rst.getString("cupom"));
                    imp.setEcf(rst.getString("codpdv"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setNome(rst.getString("razao"));
                    imp.setObservacao(rst.getString("observacao"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<RecebimentoCaixaIMP> getRecebimentosCaixa() throws Exception {
        List<RecebimentoCaixaIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    c.codvendcartao,\n"
                    + "    c.codoperadora,\n"
                    + "    c.dtvenda,\n"
                    + "    c.valor,\n"
                    + "    c.dtvenda + coalesce(op.diascomp, 0) vencimento,\n"
                    + "    c.obs,\n"
                    + "    c.operadora,\n"
                    + "    c.nrocupom,\n"
                    + "    c.nropedido\n"
                    + "from \n"
                    + "    " + getSchema() + "cartao c\n"
                    + "    join " + getSchema() + "operadoras op on\n"
                    + "        c.codoperadora = op.codoperadoras\n"
                    + "where\n"
                    + "    c.idempresa = " + getLojaOrigem() + " and\n"
                    + "    c.consiliado != 'S'"
            )) {
                while (rst.next()) {
                    RecebimentoCaixaIMP imp = new RecebimentoCaixaIMP();
                    imp.setId(rst.getString("codvendcartao"));
                    imp.setIdTipoRecebivel(rst.getString("codoperadora"));
                    imp.setDataEmissao(rst.getDate("dtvenda"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setDataVencimento(rst.getDate("vencimento"));

                    StringBuilder builder = new StringBuilder();
                    builder.append("OPERADORA=").append(rst.getString("operadora")).append(";\n");
                    builder.append("CUPOM=").append(rst.getString("nrocupom")).append(";\n");
                    builder.append("PERDIDO=").append(rst.getString("nropedido")).append(";\n");
                    builder.append("OBS=").append(rst.getString("obs")).append(";\n");

                    imp.setObservacao(builder.toString());

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<MapaTipoRecebivelIMP> getTipoRecebiveis() throws Exception {
        List<MapaTipoRecebivelIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    op.codoperadoras id,\n"
                    + "    op.operadora || ' | ' || pa.razao descricao\n"
                    + "from \n"
                    + "    " + getSchema() + "operadoras op\n"
                    + "    left join " + getSchema() + "parceiros pa on op.codadm = pa.codparceiro\n"
                    + "order by\n"
                    + "    op.operadora"
            )) {
                while (rst.next()) {
                    MapaTipoRecebivelIMP imp = new MapaTipoRecebivelIMP();

                    imp.setId(rst.getString("id"));
                    imp.setDescricao(rst.getString("descricao"));

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
        return new VendaIterator(getLojaOrigem(), dataInicioVenda, dataTerminoVenda);
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        return new VendaItemIterator(getLojaOrigem(), dataInicioVenda, dataTerminoVenda);
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private static class VendaIterator implements Iterator<VendaIMP> {

        public final static SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");

        private Statement stm = ConexaoOracle.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaIMP next;

        private void obterNext() {
            try {
                SimpleDateFormat timestamp = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                if (next == null) {
                    if (rst.next()) {
                        next = new VendaIMP();

                        String id = rst.getString("IDEMPRESA") + "-" + rst.getString("nropedido") + "-" + rst.getString("serie") + "-" + rst.getString("dtvenda") + "-" + rst.getString("coo");

                        next.setId(id);
                        next.setNumeroCupom(Utils.stringToInt(rst.getString("coo")));
                        next.setEcf(Utils.stringToInt(rst.getString("ecf")));
                        next.setIdClientePreferencial(rst.getString("idcliente"));
                        next.setData(rst.getDate("dtvenda"));
                        next.setHoraInicio(timestamp.parse(rst.getString("horainicio")));
                        next.setHoraTermino(timestamp.parse(rst.getString("horatermino")));
                        next.setSubTotalImpressora(rst.getDouble("TOTAL_VENDA"));
                        next.setValorAcrescimo(rst.getDouble("ACRESCIMO"));
                        next.setValorDesconto(rst.getDouble("DESC_PROMOCAO"));
                        next.setCancelado(rst.getBoolean("cancelada"));
                        next.setCpf(rst.getString("cnpj_cpf"));
                        next.setNomeCliente(rst.getString("razao"));
                        next.setNumeroSerie(rst.getString("nroserie"));
                        next.setModeloImpressora(rst.getString("modelo"));
                        next.setChaveNfCe(rst.getString("chavenfe"));
                    }
                }
            } catch (SQLException | ParseException ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            this.sql
                    = "SELECT\n"
                    + "        A.IDEMPRESA,\n"
                    + "	A.nropedido, \n"
                    + "	n.serie,\n"
                    + "	Trunc(A.DTVENDA) dtvenda,\n"
                    + "	n.nronf coo,\n"
                    + "        pdv.nropdv ecf,   \n"
                    + "	max(nullif(a.codparceiro, 0)) idcliente,\n"
                    + "	min(a.dtvenda) horainicio,\n"
                    + "	max(a.dtvenda) horatermino,\n"
                    + "	n.cancelada,   \n"
                    + "	(\n"
                    + "		SUM(\n"
                    + "			CASE \n"
                    + "				WHEN A.IAT = 'A' THEN CAST((A.QTDE * A.VRVENDA) AS NUMERIC(18,2)) \n"
                    + "				ELSE (CAST(TRUNC((A.QTDE * A.VRVENDA) * 100) AS NUMERIC(18,2)) / 100) \n"
                    + "			END\n"
                    + "		) \n"
                    + "	) TOTAL_VENDA,\n"
                    + "	Sum(((CASE WHEN COALESCE(A.DESC_ACRE_MEDIO,0) > 0 THEN (COALESCE(A.DESC_ACRE_MEDIO,0)) ELSE 0 END) + (CASE WHEN COALESCE(A.DESC_ACRE_ITEM,0) > 0 THEN (COALESCE(A.DESC_ACRE_ITEM,0)) ELSE 0 END)   )) ACRESCIMO,\n"
                    + "	Sum((COALESCE(A.DESC_PROMOCAO,0) + COALESCE(A.DESC_DEPARTAMENTO,0) + (CASE WHEN COALESCE(A.DESC_ACRE_MEDIO,0) < 0 THEN (COALESCE(A.DESC_ACRE_MEDIO,0)*-1) ELSE 0 END) + (CASE WHEN COALESCE(A.DESC_ACRE_ITEM,0) < 0 THEN (COALESCE(A.DESC_ACRE_ITEM,0)*-1) ELSE 0 END))) DESC_PROMOCAO,\n"
                    + "	a.cnpj_cpf,\n"
                    + "	a.razao,\n"
                    + "	pdv.nroserie,\n"
                    + "	pdv.modelo,\n"
                    + "	n.chavenfe,\n"
                    + "	n.protocolo_nfe\n"
                    + "FROM\n"
                    + "	VENDAS A                 \n"
                    + "	LEFT JOIN NFC N  ON A.NROPEDIDO = N.NROPEDIDO AND A.IDEMPRESA = N.IDEMPRESA AND A.NROSERIE = N.SERIE AND TRUNC(A.DTVENDA) = TRUNC(N.DTEMISSAO) \n"
                    + "	LEFT JOIN PRODUTOS P ON (P.IDPRODUTO = A.CODPRODUTO) \n"
                    + "	LEFT JOIN PISCOFINS CPC ON (CPC.IDPISCOFINS = COALESCE(A.IDPISCOFINS, P.IDPISCOFINS))\n"
                    + "        join empresas e on a.idempresa = e.codempresa \n"
                    + "        join det_aliquota aliq on a.aliquota = aliq.aliquota and aliq.uf = e.uf\n"
                    + "        left join pdv on pdv.nropdv = cast(n.serie as integer) and pdv.codempresa = a.idempresa\n"
                    + "WHERE\n"
                    + "	TRUNC(A.DTVENDA) BETWEEN '" + format.format(dataInicio) + "' AND '" + format.format(dataTermino) + "' AND\n"
                    + "	COALESCE(N.STATUSNFE,'P') = 'P' AND A.IDEMPRESA in (" + idLojaCliente + ")\n"
                    + "GROUP BY  \n"
                    + "        A.IDEMPRESA,\n"
                    + "	A.nropedido, \n"
                    + "	n.serie,\n"
                    + "	Trunc(A.DTVENDA),\n"
                    + "	n.nronf,\n"
                    + "        pdv.nropdv,\n"
                    + "	n.cancelada,\n"
                    + "        a.cnpj_cpf,\n"
                    + "        a.razao,\n"
                    + "	pdv.nroserie,\n"
                    + "	pdv.modelo,\n"
                    + "	n.chavenfe,\n"
                    + "	n.protocolo_nfe";
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

        private Statement stm = ConexaoOracle.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaItemIMP next;

        private void obterNext() {
            try {
                if (next == null) {
                    if (rst.next()) {
                        next = new VendaItemIMP();

                        String id = rst.getString("IDEMPRESA") + "-" + rst.getString("nropedido") + "-" + rst.getString("serie") + "-" + rst.getString("dtvenda") + "-" + rst.getString("nronf") + "-" + rst.getString("nroitem");
                        String idVenda = rst.getString("IDEMPRESA") + "-" + rst.getString("nropedido") + "-" + rst.getString("serie") + "-" + rst.getString("dtvenda") + "-" + rst.getString("nronf");

                        next.setId(id);
                        next.setVenda(idVenda);
                        next.setProduto(rst.getString("CODPRODUTO"));
                        next.setDescricaoReduzida(rst.getString("DESCRICAO"));
                        next.setQuantidade(rst.getDouble("QTDE"));
                        next.setTotalBruto(rst.getDouble("TOTAL_VENDA"));
                        next.setValorAcrescimo(rst.getDouble("ACRESCIMO"));
                        next.setValorDesconto(rst.getDouble("DESC_PROMOCAO"));
                        next.setCancelado(rst.getBoolean("CANCELADO"));
                        next.setCodigoBarras(rst.getString("CODBARRA"));
                        next.setUnidadeMedida(rst.getString("UNIDADE"));
                        next.setIcmsCst(rst.getInt("cst"));
                        next.setIcmsAliq(rst.getDouble("icm_efetivo"));
                    }
                }
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaItemIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            this.sql
                    = "SELECT\n"
                    + "	A.IDEMPRESA,\n"
                    + "	A.nropedido, \n"
                    + "	n.serie,\n"
                    + "	n.nronf,\n"
                    + "	Trunc(A.DTVENDA) dtvenda, \n"
                    + "	a.nroitem,  \n"
                    + "	A.CODPRODUTO,\n"
                    + "	P.DESCRICAO,  \n"
                    + "	SUM(A.QTDE) QTDE,  \n"
                    + "	(\n"
                    + "		SUM(\n"
                    + "			CASE \n"
                    + "				WHEN A.IAT = 'A' THEN CAST((A.QTDE * A.VRVENDA) AS NUMERIC(18,2)) \n"
                    + "				ELSE (CAST(TRUNC((A.QTDE * A.VRVENDA) * 100) AS NUMERIC(18,2)) / 100) \n"
                    + "			END\n"
                    + "		) \n"
                    + "	) TOTAL_VENDA,\n"
                    + "	case when A.CANCELADO = 'S' then 1 else 0 end as CANCELADO,\n"
                    + "        Sum(((CASE WHEN COALESCE(A.DESC_ACRE_MEDIO,0) > 0 THEN (COALESCE(A.DESC_ACRE_MEDIO,0)) ELSE 0 END) + (CASE WHEN COALESCE(A.DESC_ACRE_ITEM,0) > 0 THEN (COALESCE(A.DESC_ACRE_ITEM,0)) ELSE 0 END)   )) ACRESCIMO,\n"
                    + "	Sum((COALESCE(A.DESC_PROMOCAO,0) + COALESCE(A.DESC_DEPARTAMENTO,0) + (CASE WHEN COALESCE(A.DESC_ACRE_MEDIO,0) < 0 THEN (COALESCE(A.DESC_ACRE_MEDIO,0)*-1) ELSE 0 END) + (CASE WHEN COALESCE(A.DESC_ACRE_ITEM,0) < 0 THEN (COALESCE(A.DESC_ACRE_ITEM,0)*-1) ELSE 0 END))) DESC_PROMOCAO,\n"
                    + "	P.CODBARRA, \n"
                    + "	A.UNIDADE,\n"
                    + "	A.ALIQUOTA,\n"
                    + "	aliq.cst,\n"
                    + "	aliq.icm_efetivo\n"
                    + "FROM\n"
                    + "	VENDAS A\n"
                    + "	LEFT JOIN NFC N  ON A.NROPEDIDO = N.NROPEDIDO AND A.IDEMPRESA = N.IDEMPRESA AND A.NROSERIE = N.SERIE AND TRUNC(A.DTVENDA) = TRUNC(N.DTEMISSAO) \n"
                    + "	LEFT JOIN PRODUTOS P ON (P.IDPRODUTO = A.CODPRODUTO) \n"
                    + "	LEFT JOIN PISCOFINS CPC ON (CPC.IDPISCOFINS = COALESCE(A.IDPISCOFINS, P.IDPISCOFINS))\n"
                    + "        join empresas e on a.idempresa = e.codempresa \n"
                    + "        join det_aliquota aliq on a.aliquota = aliq.aliquota and aliq.uf = e.uf \n"
                    + "WHERE\n"
                    + "	TRUNC(A.DTVENDA) BETWEEN '" + format.format(dataInicio) + "' AND '" + format.format(dataTermino) + "' AND\n"
                    + "	COALESCE(N.STATUSNFE,'P') = 'P' AND A.IDEMPRESA in (" + idLojaCliente + ")\n"
                    + "GROUP BY  \n"
                    + "	A.IDEMPRESA,\n"
                    + "	A.nropedido, \n"
                    + "	n.serie,\n"
                    + "	n.nronf,\n"
                    + "	Trunc(A.DTVENDA), \n"
                    + "	a.nroitem,  \n"
                    + "	A.CODPRODUTO,\n"
                    + "	P.DESCRICAO,  \n"
                    + "	A.CANCELADO,\n"
                    + "	P.CODBARRA, \n"
                    + "	A.UNIDADE,\n"
                    + "	A.ALIQUOTA,\n"
                    + "	aliq.cst,\n"
                    + "	aliq.icm_efetivo";
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
