package vrimplantacao2.dao.interfaces;

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
import vrimplantacao2.gui.component.mapatiporecebiveis.FinanceiroProvider;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoEstadoCivil;
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.vo.importacao.ChequeIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTipoRecebivelIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.RecebimentoCaixaIMP;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;

/**
 * Classe que gera os dados para importação do sistema Apollo.
 * @author Leandro
 */
public class ApolloDAO extends InterfaceDAO implements FinanceiroProvider {
    
    private static final Logger LOG = Logger.getLogger(ApolloDAO.class.getName());
    
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
                    "SELECT\n" +
                    "    codempresa,\n" +
                    "    fantasia\n" +
                    "FROM \n" +
                    "    " + getSchema() + "empresas \n" +
                    "order by \n" +
                    "    codempresa"
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
                    "select distinct\n" +
                    "    p.coddpto,\n" +
                    "    d.descricao departamento,\n" +
                    "    p.codgrupo,\n" +
                    "    g.descricao grupo,\n" +
                    "    p.codsubgrupo,\n" +
                    "    s.descricao subgrupo\n" +
                    "from \n" +
                    "    " + getSchema() + "produtos p\n" +
                    "    left join (select * from " + getSchema() + "familias_prod) g on p.codgrupo = g.codfamilia\n" +
                    "    left join (select * from " + getSchema() + "familias_prod) d on p.coddpto = d.codfamilia\n" +
                    "    left join (select * from " + getSchema() + "familias_prod) s on p.codsubgrupo = s.codfamilia\n" +
                    "where\n" +
                    "    not p.coddpto is null and\n" +
                    "    not p.codgrupo is null and\n" +
                    "    not p.codsubgrupo is null\n" +
                    "order by\n" +
                    "    p.coddpto,\n" +
                    "    p.codgrupo,\n" +
                    "    p.codsubgrupo"
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
                    "select\n" +
                    "    codfamilia,\n" +
                    "    descricao\n" +
                    "from\n" +
                    "    " + getSchema() + "familias_prod\n" +
                    "where\n" +
                    "    tipo = 'P'\n" +
                    "order by\n" +
                    "    codfamilia"
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
                    "select\n" +
                    "    p.idproduto id,\n" +
                    "    p.DTCADASTRO datacadastro,\n" +
                    "    ean.codbar ean,\n" +
                    "    ean.fatoremb qtdEmbalagem,\n" +
                    "    p.unidade tipoembalagem,\n" +
                    "    case p.balanca when 'S' then 1 else 0 end ebalanca,\n" +
                    "    coalesce(p.validade, 0) validade,\n" +
                    "    p.descricao descricaocompleta,\n" +
                    "    p.coddpto merc1,\n" +
                    "    p.codgrupo merc2,\n" +
                    "    p.codsubgrupo merc3,\n" +
                    "    p.codgrupopreco id_familiaproduto,\n" +
                    "    coalesce(p.PESOBRUTO_PRODUTO, 0) pesobruto,\n" +
                    "    coalesce(p.PESOLIQ_PRODUTO, 0) pesoliquido,\n" +
                    "    est.minimo estoqueminimo,\n" +
                    "    est.maximo estoquemaximo,\n" +
                    "    est.qtde estoque,\n" +
                    "    prec.markup margem,\n" +
                    "    prec.vrcustoreal custosemimposto,\n" +
                    "    prec.vrcusto custocomimposto,\n" +
                    "    prec.vrvenda preco,\n" +
                    "    case when p.ativo = 'N' then 0 else 1 end ativo,\n" +
                    "    p.ncmsh ncm,\n" +
                    "    p.cest,\n" +
                    "    piscofins.cst_pis_ent piscofins_credito,\n" +
                    "    piscofins.cst_pis_sai piscofins_debito,\n" +
                    "    nat.idbasecreditoisento piscofins_natrec,\n" +
                    "    icms.cst icms_cst,\n" +
                    "    icms.icm icms_aliquota\n" +
                    "from\n" +
                    "    " + getSchema() + "produtos p\n" +
                    "    join " + getSchema() + "empresas emp on emp.codempresa = " + getLojaOrigem() + "\n" +
                    "    left join (\n" +
                    "        select\n" +
                    "            idproduto,\n" +
                    "            CODBARRA codbar,\n" +
                    "            1 fatoremb\n" +
                    "        from\n" +
                    "            " + getSchema() + "produtos\n" +
                    "        union\n" +
                    "        select\n" +
                    "            idproduto,\n" +
                    "            codauxiliar codbar,\n" +
                    "            case when coalesce(fatoremb, 1) < 1 then 1 else coalesce(fatoremb, 1) end fatoremb\n" +
                    "        from\n" +
                    "            " + getSchema() + "codauxiliar\n" +
                    "    ) ean on p.idproduto = ean.idproduto\n" +
                    "    left join " + getSchema() + "estoque est on est.idempresa = emp.codempresa and est.idproduto = p.idproduto\n" +
                    "    left join " + getSchema() + "multi_preco prec on prec.idempresa = emp.codempresa and prec.idproduto = p.idproduto\n" +
                    "    left join " + getSchema() + "piscofins piscofins on piscofins.idpiscofins = p.idpiscofins\n" +
                    "    left join " + getSchema() + "pc_tipocreditoisento nat on nat.idtabela = p.idtabela\n" +
                    "    left join " + getSchema() + "det_aliquota icms on icms.uf = emp.uf and icms.aliquota = p.aliquota\n" +
                    "order by\n" +
                    "    id"
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
                    "select\n" +
                    "    f.codparceiro id,\n" +
                    "    f.razao,\n" +
                    "    f.fantasia,\n" +
                    "    e.cnpj_cpf,\n" +
                    "    e.rg_insc,\n" +
                    "    case f.ativado when 'N' then 0 else 1 end ativo,\n" +
                    "    case f.bloqued when 'S' then 1 else 0 end bloqueado,\n" +
                    "    e.endereco,\n" +
                    "    e.numero,\n" +
                    "    e.complemento,\n" +
                    "    e.bairro,\n" +
                    "    e.cidade,\n" +
                    "    e.uf,\n" +
                    "    e.idcidade,\n" +
                    "    e.cep,\n" +
                    "    f.telefoneempresa,\n" +
                    "    f.diavisita prazovisita,\n" +
                    "    f.prazo_entrega prazoentrega,\n" +
                    "    f.obs observacao,\n" +
                    "    f.dtcadastro\n" +
                    "from \n" +
                    "    " + getSchema() + "parceiros f\n" +
                    "    left join " + getSchema() + "parceiros_end e on f.codparceiro = e.codparceiro\n" +
                    "where\n" +
                    "    f.frn = 'S'\n" +
                    "order by\n" +
                    "    f.codparceiro"
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
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    idproduto,\n" +
                    "    codfor\n" +
                    "from\n" +
                    "    " + getSchema() + "produtos"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setIdFornecedor(rst.getString("codfor"));
                    imp.setIdProduto(rst.getString("idproduto"));
                    
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
                    "select\n" +
                    "    f.codparceiro id,\n" +
                    "    e.cnpj_cpf,\n" +
                    "    e.rg_insc,\n" +
                    "    f.razao,\n" +
                    "    f.fantasia,\n" +
                    "    case f.ativado when 'N' then 0 else 1 end ativo,\n" +
                    "    case f.bloqued when 'S' then 1 else 0 end bloqueado,\n" +
                    "    e.endereco,\n" +
                    "    e.numero,\n" +
                    "    e.complemento,\n" +
                    "    e.bairro,\n" +
                    "    e.cidade,\n" +
                    "    e.uf,\n" +
                    "    e.idcidade,\n" +
                    "    e.cep,\n" +
                    "    f.estado_civil,\n" +
                    "    f.dtnascimento,\n" +
                    "    f.dtcadastro,\n" +
                    "    case f.sexo when 'F' then 0 else 1  end sexo,\n" +
                    "    f.empresatrabalha empresa,\n" +
                    "    f.telefoneempresa,\n" +
                    "    f.cargo,\n" +
                    "    f.renda salario,\n" +
                    "    f.limite_especial limite,\n" +
                    "    f.diasfinan diavencimento\n" +
                    "from \n" +
                    "    " + getSchema() + "parceiros f\n" +
                    "    left join " + getSchema() + "parceiros_end e on f.codparceiro = e.codparceiro\n" +
                    "where\n" +
                    "    f.cli = 'S'\n" +
                    "order by\n" +
                    "    f.codparceiro"
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
                    "select\n" +
                    "    r.codrcb id,\n" +
                    "    r.dtcadastro datacadastro,\n" +
                    "    coalesce(r.nrocupom, r.docnf) cupom,\n" +
                    "    r.codpdv ecf,\n" +
                    "    r.valor,\n" +
                    "    r.obs,\n" +
                    "    r.codparceiro idcliente,\n" +
                    "    r.dtvenc datavencimento,\n" +
                    "    r.nrodup parcela,\n" +
                    "    r.txjuros juros\n" +
                    "from \n" +
                    "    " + getSchema() + "areceber r\n" +
                    "where \n" +
                    "    r.quitada = 'N' and\n" +
                    "    r.codempresa = " + getLojaOrigem() + "\n" +
                    "order by\n" +
                    "    r.codrcb"
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
                    "select\n" +
                    "    ch.codchq id,\n" +
                    "    e.cnpj_cpf,\n" +
                    "    ch.nrocheque cheque,\n" +
                    "    bc.agencia,\n" +
                    "    cc.nroconta,\n" +
                    "    ch.dtemissao,\n" +
                    "    ch.nropedido cupom,\n" +
                    "    ch.codpdv,\n" +
                    "    ch.valor,\n" +
                    "    cl.razao,\n" +
                    "    ch.observacao\n" +
                    "from\n" +
                    "    " + getSchema() + "cheque ch\n" +
                    "    left join " + getSchema() + "parceiros cl on ch.codparceiro = cl.codparceiro\n" +
                    "    left join " + getSchema() + "parceiros_end e on cl.codparceiro = e.codparceiro\n" +
                    "    left join " + getSchema() + "bancos bc on ch.codbco = bc.codbco\n" +
                    "    left join " + getSchema() + "contas_bancarias cc on ch.codconta = cc.codconta and ch.codbco = cc.codbco\n" +
                    "where\n" +
                    "    ch.baixado != 'S' and\n" +
                    "    ch.idempresa = " + getLojaOrigem()
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
                    "select\n" +
                    "    c.codvendcartao,\n" +
                    "    c.codoperadora,\n" +
                    "    c.dtvenda,\n" +
                    "    c.valor,\n" +
                    "    c.dtvenda + coalesce(op.diascomp, 0) vencimento,\n" +
                    "    c.obs,\n" +
                    "    c.operadora,\n" +
                    "    c.nrocupom,\n" +
                    "    c.nropedido\n" +
                    "from \n" +
                    "    " + getSchema() + "cartao c\n" +
                    "    join " + getSchema() + "operadoras op on\n" +
                    "        c.codoperadora = op.codoperadoras\n" +
                    "where\n" +
                    "    c.idempresa = " + getLojaOrigem() + " and\n" +
                    "    c.consiliado != 'S'"
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
                    "select\n" +
                    "    op.codoperadoras id,\n" +
                    "    op.operadora || ' | ' || pa.razao descricao\n" +
                    "from \n" +
                    "    " + getSchema() + "operadoras op\n" +
                    "    left join " + getSchema() + "parceiros pa on op.codadm = pa.codparceiro\n" +
                    "order by\n" +
                    "    op.operadora"
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
    
    private static class VendaIterator implements Iterator<VendaIMP> {

        public final static SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");

        private Statement stm = ConexaoOracle.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaIMP next;

        private void obterNext() {
            try {
                SimpleDateFormat timestampDate = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat timestamp = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                if (next == null) {
                    if (rst.next()) {
                        next = new VendaIMP();
                        String id = rst.getString("id");
                        next.setId(id);
                        next.setNumeroCupom(Utils.stringToInt(rst.getString("numerocupom")));
                        next.setEcf(Utils.stringToInt(rst.getString("ecf")));
                        next.setData(rst.getDate("data"));
                        String horaInicio = timestampDate.format(rst.getDate("data")) + " " + rst.getString("horainicio");
                        String horaTermino = timestampDate.format(rst.getDate("data")) + " " + rst.getString("horatermino");
                        next.setHoraInicio(timestamp.parse(horaInicio));
                        next.setHoraTermino(timestamp.parse(horaTermino));
                        next.setCancelado(rst.getBoolean("cancelado"));
                        next.setSubTotalImpressora(rst.getDouble("subtotalimpressora"));
                        next.setNumeroSerie(rst.getString("numeroserie"));
                        next.setChaveNfCe(rst.getString("chavenfe"));
                        next.setXml(rst.getString("xml"));
                    }
                }
            } catch (SQLException | ParseException ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {
            this.sql
                    = "select\n" +
                    "   v.nropedido id,\n" +
                    "   v.nronf numerocupom,\n" +
                    "   1 as ecf,\n" +
                    "   v.dtemissao data,\n" +
                    "   (select min(dtvenda) from vendas where nropedido = v.nropedido) horainicio,\n" +
                    "   (select max(dtvenda) from vendas where nropedido = v.nropedido) horatermino,\n" +
                    "   case when statusnfe = 'C' then 1 else 0 end cancelado,\n" +
                    "   coalesce(v.totalnf, 0) subtotalimpressora,\n" +
                    "   coalesce(v.totalprod, 0) totalprod,\n" +
                    "   coalesce(v.totalfrete, 0) frete,\n" +
                    "   coalesce(v.totaldesc, 0) totaldesconto,\n" +
                    "   coalesce(v.totaloutrasdesp, 0) totaloutrasdespesas,\n" +
                    "   v.serie numeroserie,\n" +
                    "   v.chavenfe,\n" +
                    "   v.arquivo_xml xml\n" +
                    "from nfc v where \n" +
                    "   v.dtemissao >= '" + FORMAT.format(dataInicio) + " 00:00:00' and\n" +
                    "   v.dtemissao <= '" + FORMAT.format(dataTermino) + " 23:59:59'\n" +
                    "order by v.nropedido";
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
                        String id = rst.getString("numerocupom") + "-" + rst.getString("ecf") + "-" + rst.getString("data");

                        next.setId(rst.getString("id"));
                        next.setVenda(id);
                        next.setProduto(rst.getString("produto"));
                        next.setDescricaoReduzida(rst.getString("descricao"));
                        next.setQuantidade(rst.getDouble("quantidade"));
                        next.setTotalBruto(rst.getDouble("total"));
                        next.setValorDesconto(rst.getDouble("desconto"));
                        next.setValorAcrescimo(rst.getDouble("acrescimo"));
                        next.setCancelado(rst.getBoolean("cancelado"));
                        next.setCodigoBarras(rst.getString("codigobarras"));
                        next.setUnidadeMedida(rst.getString("unidade"));
                        
                        String trib = rst.getString("codaliq_venda");
                        if (trib == null || "".equals(trib)) {
                            trib = rst.getString("codaliq_produto");
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
             TA	7.00	ALIQUOTA 07%
             TB	12.00	ALIQUOTA 12%
             TC	18.00	ALIQUOTA 18%
             TD	25.00	ALIQUOTA 25%
             TE	11.00	ALIQUOTA 11%
             I	0.00	ISENTO
             F	0.00	SUBST TRIBUTARIA
             N	0.00	NAO INCIDENTE
             */
            int cst;
            double aliq;
            switch (icms) {
                case "TA":
                    cst = 0;
                    aliq = 7;
                    break;
                case "TB":
                    cst = 0;
                    aliq = 12;
                    break;
                case "TC":
                    cst = 0;
                    aliq = 18;
                    break;
                case "TD":
                    cst = 0;
                    aliq = 25;
                    break;
                case "TE":
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
                    = "select\n"
                    + "    cx.id,\n"
                    + "    cx.coo as numerocupom,\n"
                    + "    cx.codcaixa as ecf,\n"
                    + "    cx.data as data,\n"
                    + "    cx.codprod as produto,\n"
                    + "    pr.DESC_PDV as descricao,    \n"
                    + "    isnull(cx.qtd, 0) as quantidade,\n"
                    + "    isnull(cx.totitem, 0) as total,\n"
                    + "    case when cx.cancelado = 'N' then 0 else 1 end as cancelado,\n"
                    + "    isnull(cx.descitem, 0) as desconto,\n"
                    + "    isnull(cx.acrescitem, 0) as acrescimo,\n"
                    + "    case\n"
                    + "     when LEN(cx.barra) > 14 \n"
                    + "     then SUBSTRING(cx.BARRA, 4, LEN(cx.barra))\n"
                    + "    else cx.BARRA end as codigobarras,\n"
                    + "    pr.unidade,\n"
                    + "    cx.codaliq codaliq_venda,\n"
                    + "    pr.codaliq codaliq_produto,\n"
                    + "    ic.DESCRICAO trib_desc\n"
                    + "from\n"
                    + "    caixageral as cx\n"
                    + "    join PRODUTOS pr on cx.codprod = pr.codprod\n"
                    + "    left join creceita c on pr.codcreceita = c.codcreceita\n"
                    + "    left join clientes cl on cx.cliente = cast(cl.codclie as varchar(20))\n"
                    + "    left join ALIQUOTA_ICMS ic on pr.codaliq = ic.codaliq\n"
                    + "where\n"
                    + "    cx.tipolancto = '' and\n"
                    + "    (cx.data between convert(date, '" + VendaIterator.FORMAT.format(dataInicio) + "', 23) and convert(date, '" + VendaIterator.FORMAT.format(dataTermino) + "', 23)) and\n"
                    + "    cx.codloja = " + idLojaCliente + " and\n"
                    + "    cx.atualizado = 'S' and\n"
                    + "    (cx.flgrupo = 'S' or cx.flgrupo = 'N')";
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
