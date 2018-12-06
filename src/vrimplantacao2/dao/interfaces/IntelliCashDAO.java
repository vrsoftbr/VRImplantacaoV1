/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ChequeIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorContatoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;

/**
 *
 * @author lucasrafael
 */
public class IntelliCashDAO extends InterfaceDAO {

    public boolean i_importarCodigoCliente;
    private static final Logger LOG = Logger.getLogger(IntelliCashDAO.class.getName());

    @Override
    public String getSistema() {
        return "IntelliCash";
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select distinct\n"
                    + "p.grupo merc1, m1.descricao merc1_desc,\n"
                    + "p.secao merc2, m2.descricao merc2_desc,\n"
                    + "p.subgrupo merc3, m3.descricao merc3_desc\n"
                    + "from\n"
                    + "produtos p\n"
                    + "join grupos m1 on m1.id = p.grupo\n"
                    + "join secoes m2 on m2.id = p.secao\n"
                    + "join subgrupos m3 on m3.id = p.subgrupo\n"
                    + "order by\n"
                    + "merc1, merc2, merc3"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setMerc1ID(rst.getString("merc1"));
                    imp.setMerc1Descricao(rst.getString("merc1_desc"));
                    imp.setMerc2ID(rst.getString("merc2"));
                    imp.setMerc2Descricao(rst.getString("merc2_desc"));
                    imp.setMerc3ID(rst.getString("merc3"));
                    imp.setMerc3Descricao(rst.getString("merc3_desc"));
                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    id,\n"
                    + "    descricao\n"
                    + "from\n"
                    + "    semelhancas\n"
                    + "order by\n"
                    + "    id"
            )) {
                while (rst.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setDescricao(rst.getString("descricao"));
                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        double margem = 0;
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            String loja = getLojaOrigem().split("-")[0];
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    p.id,\n"
                    + "    p.datacadastro,\n"
                    + "    ob1.descricao as tipoEmbalagem,\n"
                    + "    case when not bal.codigo is null then bal.codigo else e.ean end ean,\n"
                    + "    case when not bal.codigo is null then 1 else 0 end eBalanca,\n"
                    + "    coalesce(bal.validade, 0) validade,\n"
                    + "    p.descricao descricaoCompleta,\n"
                    + "    p.ref descricaoReduzida,\n"
                    + "    p.descricao descricaoGondola,\n"
                    + "    p.grupo codMercadologico1,\n"
                    + "    ob2.descricao as mercadologico1,\n"
                    + "    p.secao codMercadologico2,\n"
                    + "    ob3.descricao as mercadologico2, \n"
                    + "    p.subgrupo codMercadologico3,\n"
                    + "    subg.descricao as mercadologico3,\n"
                    + "    f.id as idFamiliaProduto,\n"
                    + "    f.descricao as desc_familia,\n"
                    + "    p.estqmin estoqueMinimo,\n"
                    + "    p.estqmax estoqueMaximo,\n"
                    + "    (select qtde from getestqprod(p.id, emp.id)) estoque, \n"
                    //+ "    p.mkp as margem,\n"
                    + "    c.custoatual custoSemImposto,\n"
                    + "    c.custoatual custoComImposto,\n"
                    + "    prc.vpreco precoVenda,\n"
                    + "    p.ativo,\n"
                    + "    fisco.ncm,\n"
                    + "    pst.cod_cest cest,    \n"
                    + "    coalesce(fisco.pis_cst_s, 13) pis_cst_e,\n"
                    + "    coalesce(fisco.pis_cst_s, 1) pis_cst_s,\n"
                    + "    fisco.cod_natureza_receita pis_natureza_receita,\n"
                    + "    case substring(icms.descricao from 1 for 1)\n"
                    + "    when 'F' then 60\n"
                    + "    when 'T' then 0\n"
                    + "    when 'I' then 40\n"
                    + "    when 'N' then 41\n"
                    + "    end icms_cst,\n"
                    + "    icms.valor icms_aliq\n"
                    + "from\n"
                    + "    produtos p\n"
                    + "    left join empresas emp on emp.id = " + loja + "\n"
                    + "    left join pesaveis bal on p.id = bal.id\n"
                    + "    left join estoque est on p.id = est.idprod\n"
                    + "    left join prodst pst on p.id = pst.id\n"
                    + "    left join mxf_vw_pis_cofins fisco on fisco.codigo_produto = p.id\n"
                    + "    left join eans e on e.produto = p.id\n"
                    + "    left join semelhantes fd on fd.idprod = p.id\n"
                    + "    left join semelhancas f on f.id = fd.idclasse\n"
                    + "    left join objetos ob1 on ob1.id = p.unidade\n"
                    + "    left join objetos ob2 on ob2.id = p.secao\n"
                    + "    left join objetos ob3 on ob3.id = p.grupo\n"
                    + "    left join objetos icms on icms.id = p.trib\n"
                    + "    left join objetos subg on subg.id = p.subgrupo\n"
                    + "    left join custoanterior c on c.id = p.id\n"
                    + "    left join VW_EC_EXPT_PRODUTOS prc on prc.idproduto = p.id\n"
                    + "order by\n"
                    + "    p.id"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setTipoEmbalagem(rst.getString("tipoEmbalagem"));
                    imp.setEan(rst.getString("ean"));
                    imp.seteBalanca(rst.getBoolean("eBalanca"));
                    imp.setValidade(rst.getInt("validade"));
                    imp.setDescricaoCompleta(rst.getString("descricaoCompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoReduzida"));
                    imp.setDescricaoGondola(rst.getString("descricaoGondola"));
                    imp.setCodMercadologico1(rst.getString("codMercadologico1"));
                    imp.setCodMercadologico2(rst.getString("codMercadologico2"));
                    imp.setCodMercadologico3(rst.getString("codMercadologico3"));
                    imp.setIdFamiliaProduto(rst.getString("idFamiliaProduto"));
                    imp.setEstoqueMinimo(rst.getInt("estoqueMinimo"));
                    imp.setEstoqueMaximo(rst.getInt("estoqueMaximo"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    if(rst.getDouble("custocomimposto") != 0) {
                        margem = (((rst.getDouble("precovenda") / rst.getDouble("custocomimposto")) - 1) * 100);
                    }
                    imp.setMargem(Utils.arredondar(margem, 2));
                    imp.setCustoSemImposto(rst.getDouble("custoSemImposto"));
                    imp.setCustoComImposto(rst.getDouble("custoComImposto"));
                    imp.setPrecovenda(rst.getDouble("precoVenda"));
                    if (rst.getInt("ativo") == 0) {
                        imp.setSituacaoCadastro(SituacaoCadastro.EXCLUIDO);
                    } else {
                        imp.setSituacaoCadastro(SituacaoCadastro.ATIVO);
                    }
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstCredito(rst.getInt("pis_cst_e"));
                    imp.setPiscofinsCstDebito(rst.getInt("pis_cst_s"));
                    imp.setPiscofinsNaturezaReceita(rst.getInt("pis_natureza_receita"));
                    imp.setIcmsCst(rst.getInt("icms_cst"));
                    imp.setIcmsAliq(rst.getDouble("icms_aliq"));

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
            try (Statement stm2 = ConexaoFirebird.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select\n"
                        + "    a.id,\n"
                        + "    a.nome razao,\n"
                        + "    a.fantasia,\n"
                        + "    a.doc cnpj_cpf,\n"
                        + "    coalesce(dcie.doc, dcrg.doc) ie_rg,\n"
                        + "    f.ativo,\n"
                        + "\n"
                        + "    en.logradouro endereco,\n"
                        + "    en.numero,\n"
                        + "    en.complemento,\n"
                        + "    en.bairro,\n"
                        + "    cid.cidade,   \n"
                        + "    cidibge.id cidade_ibge,\n"
                        + "    cid.uf,\n"
                        + "    en.cep,\n"
                        + "    (select first 1 coalesce('('||ddd||')','')||telefone tel from telefones where agente = a.id ) tel_principal\n"
                        + "from\n"
                        + "    agentes a\n"
                        + "    join forns f on f.id = a.id\n"
                        + "    left join enderecos en on en.agente = a.id\n"
                        + "    left join cidades cid on cid.id = en.cidade\n"
                        + "    left join cidadesibge cidibge on cidibge.id2 = cid.id\n"
                        + "    left join docs dcie on dcie.codag = a.id and dcie.tipo = 66\n"
                        + "    left join docs dcrg on dcrg.codag = a.id and dcrg.tipo = 67\n"
                        + "order by\n"
                        + "    a.id"
                )) {
                    while (rst.next()) {
                        FornecedorIMP imp = new FornecedorIMP();

                        imp.setImportSistema(getSistema());
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportId(rst.getString("id"));
                        imp.setRazao(rst.getString("razao"));
                        imp.setFantasia(rst.getString("fantasia"));
                        imp.setCnpj_cpf(rst.getString("cnpj_cpf"));
                        imp.setIe_rg(rst.getString("ie_rg"));
                        imp.setAtivo(rst.getBoolean("ativo"));

                        imp.setEndereco(rst.getString("endereco"));
                        imp.setNumero(rst.getString("numero"));
                        imp.setComplemento(rst.getString("complemento"));
                        imp.setBairro(rst.getString("bairro"));
                        imp.setIbge_municipio(rst.getInt("cidade_ibge"));
                        imp.setMunicipio(rst.getString("cidade"));
                        imp.setUf(rst.getString("uf"));
                        imp.setCep(rst.getString("cep"));

                        imp.setCob_endereco(rst.getString("endereco"));
                        imp.setCob_numero(rst.getString("numero"));
                        imp.setCob_complemento(rst.getString("complemento"));
                        imp.setCob_bairro(rst.getString("bairro"));
                        imp.setCob_ibge_municipio(rst.getInt("cidade_ibge"));
                        imp.setCob_municipio(rst.getString("cidade"));
                        imp.setCob_uf(rst.getString("uf"));
                        imp.setCob_cep(rst.getString("cep"));

                        imp.setTel_principal(rst.getString("tel_principal"));

                        int cont = 0;
                        try (ResultSet rst2 = stm2.executeQuery(
                                "select\n"
                                + "    id,\n"
                                + "    agente,\n"
                                + "    email valor,\n"
                                + "    'EMAIL' tipo,\n"
                                + "    '' contato\n"
                                + "from\n"
                                + "    emails\n"
                                + "where\n"
                                + "    agente = " + Utils.quoteSQL(imp.getImportId()) + "\n"
                                + "union\n"
                                + "select\n"
                                + "    id,\n"
                                + "    agente,\n"
                                + "    coalesce(coalesce('('||ddd||')','')||telefone,'') valor,\n"
                                + "    'TELEFONE' tipo,\n"
                                + "    coalesce(contato,'') contato\n"
                                + "from\n"
                                + "    telefones\n"
                                + "where\n"
                                + "    agente = " + Utils.quoteSQL(imp.getImportId())
                        )) {
                            while (rst2.next()) {
                                cont++;
                                FornecedorContatoIMP contato = new FornecedorContatoIMP();
                                contato.setImportSistema(imp.getImportSistema());
                                contato.setImportLoja(imp.getImportLoja());
                                contato.setImportFornecedorId(imp.getImportId());
                                contato.setImportId(rst2.getString("id"));
                                contato.setNome((rst2.getString("tipo") + " " + rst2.getString("contato")).trim());
                                if ("TELEFONE".equals(rst2.getString("tipo"))) {
                                    if (!rst2.getString("valor").equals(imp.getTel_principal())) {
                                        contato.setTelefone(rst2.getString("valor"));
                                        imp.getContatos().put(contato, String.valueOf(cont));
                                    }
                                }
                                if ((rst2.getString("tipo").contains("EMAIL"))) {
                                    contato.setEmail(rst2.getString("valor").toLowerCase());
                                    imp.getContatos().put(contato, String.valueOf(cont));
                                }
                            }
                        }
                        result.add(imp);
                    }
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
                    "select\n"
                    + "    prod id_produto,\n"
                    + "    forn id_fornecedor,\n"
                    + "    codigo as codigoexterno\n"
                    + "from\n"
                    + "    fornxcodprod"
            )) {
                while (rst.next()) { 
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setIdProduto(rst.getString("id_produto"));
                    imp.setIdFornecedor(rst.getString("id_fornecedor"));
                    imp.setCodigoExterno(rst.getString("codigoexterno"));

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
                    "select\n" +
                    "    a.id id_agente,\n" +
                    "    c.codigo id_cliente,\n" +
                    "    a.nome,\n" +
                    "    en.logradouro res_endereco,\n" +
                    "    en.numero res_numero,\n" +
                    "    en.complemento res_complemento,\n" +
                    "    en.bairro res_bairro,\n" +
                    "    cidibge.id res_cidade_ibge,\n" +
                    "    cid.cidade res_cidade,\n" +
                    "    cid.uf res_uf,\n" +
                    "    en.cep res_cep,   \n" +
                    "    a.doc cnpj,\n" +
                    "    dcie.doc as inscricaoestadual,\n" +
                    "    (select first 1 coalesce('('||ddd||')', '') || telefone from telefones where agente = a.id order by id desc) fone1,\n" +
                    "    (select first 1 skip 1 coalesce('('||ddd||')', '') || telefone from telefones where agente = a.id order by id desc) fone2,\n" +
                    "    (select first 1 skip 2 coalesce('('||ddd||')', '') || telefone from telefones where agente = a.id order by id desc) celular,\n" +
                    "    c.diavenc prazodias,\n" +
                    "    coalesce(c.cadastro, current_date) datacadastro,\n" +
                    "    (select first 1 email from emails where agente = a.id) email,\n" +
                    "    c.limite limitepreferencial,\n" +
                    "    c.renda salario,\n" +
                    "    c.situacao,\n" +
                    "    tpcl.id tipo_cliente,\n" +
                    "    tpcl.descricao,\n" +
                    "    c.cadastro,\n" +
                    "    cidibge.id,\n" +
                    "    dcrg.doc as rg,\n" +
                    "    f.pai,\n" +
                    "    f.mae,\n" +
                    "    ec.nascimento\n" +
                    "from\n" +
                    "    agentes a\n" +
                    "join clientes c on c.id = a.id\n" +
                    "left join filiacao f on f.id = a.id\n" +
                    "left join enderecos en on en.agente = a.id\n" +
                    "left join cidades cid on cid.id = en.cidade\n" +
                    "left join tiposclientes tpcl on tpcl.id = c.tipocliente\n" +
                    "left join cidadesibge cidibge on cidibge.id2 = cid.id\n" +
                    "left join docs dcie on dcie.codag = a.id and dcie.tipo = 66\n" +
                    "left join docs dcrg on dcrg.codag = a.id and dcrg.tipo = 67\n" +
                    "left join EC_EXPT_AGENTE ec on ec.id = a.id\n" +
                    "union all\n" +
                    "select\n" +
                    "    fu.id id_agente,\n" +
                    "    ec.id id_cliente,\n" +
                    "    ec.nome,\n" +
                    "    '' res_endereco,\n" +
                    "    '' res_numero,\n" +
                    "    '' res_complemento,\n" +
                    "    '' res_bairro,\n" +
                    "    '' res_cidade_ibge,\n" +
                    "    '' res_cidade,\n" +
                    "    '' res_uf,\n" +
                    "    '' res_cep,\n" +
                    "    ec.doc cnpj,\n" +
                    "    '' as inscricaoestadual,\n" +
                    "    '' fone1,\n" +
                    "    '' fone2,\n" +
                    "    '' celular,\n" +
                    "    fu.diapg prazodias,\n" +
                    "    coalesce(fu.admissao, current_date) datacadastro,\n" +
                    "    '' email,\n" +
                    "    fu.limitevales limitepreferencial,\n" +
                    "    fu.salario salario,\n" +
                    "    2 situacao,\n" +
                    "    cast('' as varchar(20)) tipo_cliente,\n" +
                    "    cast('' as varchar(20)) descricao,\n" +
                    "    current_date cadastro,\n" +
                    "    0 id,\n" +
                    "    cast('' as varchar(30)) as rg,\n" +
                    "    cast('' as varchar(200)) pai,\n" +
                    "    cast('' as varchar(200)) mae,\n" +
                    "    fu.datanasc nascimento\n" +
                    "from\n" +
                    "    funcionarios fu\n" +
                    "left join ec_expt_agente ec on fu.id = ec.id\n" +
                    "order by 1")) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    if (i_importarCodigoCliente) {
                        imp.setId(rst.getString("id_cliente"));
                    } else {
                        imp.setId(rst.getString("id_agente"));
                    }

                    imp.setRazao(rst.getString("nome"));
                    imp.setEndereco(rst.getString("res_endereco"));
                    imp.setNumero(rst.getString("res_numero"));
                    imp.setComplemento(rst.getString("res_complemento"));
                    imp.setBairro(rst.getString("res_bairro"));
                    imp.setMunicipio(rst.getString("res_cidade"));
                    imp.setUf(rst.getString("res_uf"));
                    imp.setCep(rst.getString("res_cep"));
                    imp.setTelefone(rst.getString("fone1"));
                    if (Utils.stringToLong(rst.getString("fone2")) != 0) {
                        imp.addContato("FONE2", "FONE2", rst.getString("fone2"), "", "");
                    }
                    imp.setCelular(rst.getString("celular"));
                    imp.setInscricaoestadual(rst.getString("inscricaoestadual"));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setDiaVencimento(rst.getInt("prazodias"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setEmail(rst.getString("email"));
                    imp.setValorLimite(rst.getDouble("limitepreferencial"));
                    imp.setSalario(rst.getDouble("salario"));
                    imp.setPermiteCreditoRotativo(true);
                    imp.setPermiteCheque(true);
                    imp.setNomePai(rst.getString("pai"));
                    imp.setNomeMae(rst.getString("mae"));
                    imp.setDataNascimento(rst.getDate("nascimento"));

                    int cont = 0;
                    try (Statement stm2 = ConexaoFirebird.getConexao().createStatement()) {
                        try (ResultSet rst2 = stm2.executeQuery(
                                "select\n"
                                + "    id,\n"
                                + "    agente,\n"
                                + "    email valor,\n"
                                + "    'EMAIL' tipo,\n"
                                + "    '' contato\n"
                                + "from\n"
                                + "    emails\n"
                                + "where\n"
                                + "    agente = " + Utils.quoteSQL(imp.getId()) + "\n"
                                + "union\n"
                                + "select\n"
                                + "    id,\n"
                                + "    agente,\n"
                                + "    coalesce(coalesce('('||ddd||')','')||telefone,'') valor,\n"
                                + "    'TELEFONE' tipo,\n"
                                + "    coalesce(contato,'') contato\n"
                                + "from\n"
                                + "    telefones\n"
                                + "where\n"
                                + "    agente = " + Utils.quoteSQL(imp.getId())
                        )) {
                            while (rst2.next()) {
                                if ((rst2.getString("tipo").contains("EMAIL"))) {
                                    imp.setEmail(rst2.getString("valor").toLowerCase());
                                }

                                if ("TELEFONE".equals(rst2.getString("tipo"))) {
                                    if (!rst2.getString("valor").equals(imp.getTelefone())) {
                                        imp.addContato(
                                                rst2.getString("id"),
                                                rst2.getString("tipo"),
                                                rst2.getString("valor"),
                                                null,
                                                null);
                                    }
                                }
                            }
                        }
                    }
                    
                    if(rst.getInt("situacao") == 2) {
                        imp.setBloqueado(true);
                    } else {
                        imp.setBloqueado(false);
                    }
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
                    + "    af.id,\n"
                    + "    af.data,\n"
                    + "    af.vencimento,\n"
                    + "    af.doc,\n"
                    + "    af.codag as id_agente,\n"
                    + "    cli.codigo as id_cliente,\n"
                    + "    af.valor,\n"
                    + "    af.descricao,\n"
                    + "    af.juros\n"
                    + "from agendafin af\n"
                    + "inner join agentes ag on ag.id = af.codag\n"
                    + "inner join clientes cli on cli.id = ag.id\n"
                    + "where af.pg is null\n"
                    + "and af.empresa = " + getLojaOrigem()
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rst.getString("id"));

                    if (i_importarCodigoCliente) {
                        imp.setIdCliente(rst.getString("id_cliente"));
                    } else {
                        imp.setIdCliente(rst.getString("id_agente"));
                    }

                    imp.setDataEmissao(rst.getDate("data"));
                    imp.setDataVencimento(rst.getDate("vencimento"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setJuros(rst.getDouble("juros"));
                    imp.setObservacao(rst.getString("descricao") + " NUMERO DOC " + rst.getString("doc"));

                    if ((rst.getString("doc") != null)
                            && (!rst.getString("doc").trim().isEmpty())
                            && (rst.getString("doc").trim().length() <= 14)) {

                        if (Long.parseLong(Utils.formataNumero(rst.getString("doc").trim())) <= Integer.MAX_VALUE) {
                            imp.setNumeroCupom(Utils.formataNumero(rst.getString("doc")));
                        }
                    }

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ChequeIMP> getCheques() throws Exception {
        List<ChequeIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                   "select\n"
                    + "id,\n"
                    + "data,\n"
                    + "datadeposito,\n"
                    + "valor,\n"
                    + "banco,\n"
                    + "agencia,\n"
                    + "conta,\n"
                    + "numchq,\n"
                    + "emitente,\n"
                    + "cmc7\n"
                 + "from cheques\n"
                 + "where data >= '25.06.2018'\n"
                 + "order by id"
            )) {
                while (rst.next()) {
                    ChequeIMP imp = new ChequeIMP();
                    imp.setId(rst.getString("id"));
                    imp.setDate(rst.getDate("data"));
                    imp.setDataDeposito(rst.getDate("datadeposito"));
                    imp.setNome(rst.getString("emitente"));
                    imp.setNumeroCheque(rst.getString("numchq"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setBanco(rst.getInt("banco"));
                    imp.setAgencia(rst.getString("agencia"));
                    imp.setConta(rst.getString("numchq"));
                    imp.setCmc7(rst.getString("cmc7"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> lojas = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "    emp.id,\n"
                    + "    a.nome\n"
                    + "from\n"
                    + "    empresas emp\n"
                    + "    join agentes a on emp.codag = a.id\n"
                    + "order by\n"
                    + "    emp.id"
            )) {
                while (rs.next()) {
                    lojas.add(new Estabelecimento(rs.getString("id"), rs.getString("nome")));
                }
            }
        }
        return lojas;
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
        return new IntelliCashDAO.VendaIterator(getLojaOrigem(), dataInicioVenda, dataTerminoVenda);
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        return new IntelliCashDAO.VendaItemIterator(getLojaOrigem(), dataInicioVenda, dataTerminoVenda);
    }
    
    private static class VendaIterator implements Iterator<VendaIMP> {

        public final static SimpleDateFormat FORMAT = new SimpleDateFormat("dd.MM.yyyy");

        private Statement stm = ConexaoFirebird.getConexao().createStatement();
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
                        String id = rst.getString("coo") + "-" + rst.getString("ecf") + "-" + rst.getString("emissao");
                        if (!uk.add(id)) {
                            LOG.warning("Venda " + id + " já existe na listagem");
                        }
                        next.setId(id);
                        next.setNumeroCupom(Utils.stringToInt(rst.getString("coo")));
                        next.setEcf(Utils.stringToInt(rst.getString("ecf")));
                        next.setData(rst.getDate("emissao"));
                        next.setIdClientePreferencial(rst.getString("idcliente"));
                        String horaInicio = rst.getString("horainicio");
                        String horaTermino = rst.getString("horatermino");
                        next.setHoraInicio(timestamp.parse(horaInicio));
                        next.setHoraTermino(timestamp.parse(horaTermino));
                        next.setSubTotalImpressora(rst.getDouble("valor"));
                        next.setCpf(rst.getString("cnpj"));
                        next.setModeloImpressora(rst.getString("modelo"));
                        next.setNomeCliente(rst.getString("nome"));
                        String endereco
                                = Utils.acertarTexto(rst.getString("endereco")) + ","
                                + Utils.acertarTexto(rst.getString("numero")) + ","
                                + Utils.acertarTexto(rst.getString("bairro")) + ","
                                + Utils.acertarTexto(rst.getString("cidade")) + "-"
                                + Utils.acertarTexto(rst.getString("estado")) + ","
                                + Utils.acertarTexto(rst.getString("cep"));
                        next.setEnderecoCliente(endereco);
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
                    "    c.cupom coo,\n" +
                    "    c.emissao,\n" +
                    "    c.pdv ecf,\n" +
                    "    min(c.hora) horainicio,\n" +
                    "    max(c.hora) horatermino,\n" +
                    "    sum(c.valor) valor,\n" +
                    "    sum(c.desconto) desconto,\n" +
                    "    c.motivocanc,\n" +
                    "    iif(c.codag is null, '', c.codag) idcliente,\n" +
                    "    iif(a.nome is null, '', a.nome) nome,\n" +
                    "    iif(a.doc is null, '', a.doc) cnpj,\n" +
                    "    iif(e.cep is null, '', e.cep) cep,\n" +
                    "    iif(e.logradouro is null, '', e.logradouro) endereco,\n" +
                    "    iif(e.numero is null, '', e.numero) numero,\n" +
                    "    iif(e.bairro is null, '', e.bairro) bairro,\n" +
                    "    iif(ci.cidade is null, '', ci.cidade) cidade,\n" +
                    "    iif(ci.uf is null, '', ci.uf) estado,\n" +
                    "    c.modelo,\n" +
                    "    c.consumidor\n" +
                    "from\n" +
                    "    TMP60I c\n" +
                    "left join agentes a on (c.codag = a.id)\n" +
                    "left join enderecos e on (a.id = e.agente)\n" +
                    "left join cidades ci on (e.cidade = ci.id)\n" +
                    "where\n" +
                    "    emissao between '" + FORMAT.format(dataInicio) + "' and '" +  FORMAT.format(dataTermino) + "' and\n" +
                    "    c.empresa = " + idLojaCliente + "\n" +
                    "group by\n" +
                    "    c.cupom,\n" +
                    "    c.emissao,\n" +
                    "    c.pdv,\n" +
                    "    c.motivocanc,\n" +
                    "    c.codag,\n" +
                    "    a.nome,\n" +
                    "    a.doc,\n" +
                    "    e.cep,\n" +
                    "    e.logradouro,\n" +
                    "    e.numero,\n" +
                    "    e.bairro,\n" +
                    "    ci.cidade,\n" +
                    "    ci.uf,\n" +
                    "    c.modelo,\n" +
                    "    c.consumidor\n" +
                    "order by\n" +
                    "    c.emissao, c.cupom";
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

        private Statement stm = ConexaoFirebird.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaItemIMP next;

        private void obterNext() {
            try {
                if (next == null) {
                    if (rst.next()) {
                        next = new VendaItemIMP();
                        String id = rst.getString("coo") + "-" + rst.getString("ecf") + "-" + rst.getString("emissao") + "-" + rst.getDouble("sequencia");
                        String idVenda = rst.getString("coo") + "-" + rst.getString("ecf") + "-" + rst.getString("emissao");

                        next.setId(id);
                        next.setVenda(idVenda);
                        next.setProduto(rst.getString("idproduto"));
                        next.setDescricaoReduzida(rst.getString("descricao"));
                        next.setQuantidade(rst.getDouble("qtde"));
                        next.setTotalBruto(rst.getDouble("valor"));
                        next.setValorDesconto(rst.getDouble("desconto"));
                        next.setCancelado(rst.getBoolean("cancelado"));
                        next.setCodigoBarras(rst.getString("ean"));
                        next.setUnidadeMedida(rst.getString("unidade"));
                        next.setIcmsAliq(rst.getDouble("aliqicms"));
                        int cst;
                        switch(rst.getString("idicms")) {
                            case "F": cst = 60; break;
                            case "I": cst = 40; break;    
                            case "N": cst = 41; break;
                            case "T07": cst = 0; break;
                            case "T12": cst = 0; break;
                            case "T18": cst = 0; break;
                            case "T25": cst = 0; break;
                            default: cst = 40;
                        }
                        next.setIcmsCst(cst);
                        next.setSequencia(rst.getInt("sequencia"));
                    }
                }
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaItemIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {
            this.sql
                    = 
                    "select\n" +
                    "    i.cupom coo,\n" +
                    "    i.pdv ecf,\n" +
                    "    i.emissao,\n" +
                    "    e.produto idproduto,\n" +
                    "    p.descricao,\n" +
                    "    un.descricao unidade,\n" +
                    "    i.ean,\n" +
                    "    i.qtde,\n" +
                    "    i.trib,\n" +
                    "    i.custo,\n" +
                    "    i.valor,\n" +
                    "    i.numitem sequencia,\n" +
                    "    i.emissao,\n" +
                    "    i.cancelado,\n" +
                    "    i.datahorasistema,\n" +
                    "    i.desconto,\n" +
                    "    i.descontocupom,\n" +
                    "    i.acrescimoitem,\n" +
                    "    icms.descricao idicms,\n" +
                    "    icms.valor aliqicms\n" +
                    "from\n" +
                    "    tmp60i i\n" +
                    "join eans e on (i.idean = e.id)\n" +
                    "join produtos p on (e.produto = p.id)\n" +
                    "left join objetos un on (p.unidade = un.id)\n" +
                    "left join objetos icms on (p.trib = icms.id)\n" +
                    "where\n" +
                    "    i.emissao between '" + VendaIterator.FORMAT.format(dataInicio) + "' and '" + VendaIterator.FORMAT.format(dataTermino) + "' and \n" +
                    "    i.empresa = " + idLojaCliente + "\n" +
                    "order by\n" +
                    "    i.cupom, i.emissao";
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
