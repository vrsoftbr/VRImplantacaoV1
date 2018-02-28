package vrimplantacao.dao.interfaces;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrframework.classe.VRException;
import vrframework.remote.ItemComboVO;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao.dao.cadastro.ClientePreferencialDAO;
import vrimplantacao.dao.cadastro.FamiliaProdutoDAO;
import vrimplantacao.dao.cadastro.FornecedorDAO;
import vrimplantacao.dao.cadastro.LojaDAO;
import vrimplantacao.dao.cadastro.MercadologicoDAO;
import vrimplantacao.dao.cadastro.NcmDAO;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.dao.cadastro.ProdutoDAO;
import vrimplantacao.dao.cadastro.ProdutoFornecedorDAO;
import vrimplantacao.dao.cadastro.ReceberChequeDAO;
import vrimplantacao.dao.cadastro.ReceberCreditoRotativoDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao.vo.vrimplantacao.ClientePreferencialVO;
import vrimplantacao.vo.vrimplantacao.CodigoAnteriorVO;
import vrimplantacao.vo.vrimplantacao.FamiliaProdutoVO;
import vrimplantacao.vo.vrimplantacao.FornecedorVO;
import vrimplantacao.vo.vrimplantacao.MercadologicoVO;
import vrimplantacao.vo.vrimplantacao.NcmVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAliquotaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAutomacaoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoComplementoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoFornecedorVO;
import vrimplantacao.vo.vrimplantacao.ProdutoVO;
import vrimplantacao.vo.vrimplantacao.ReceberChequeVO;
import vrimplantacao.vo.vrimplantacao.ReceberCreditoRotativoVO;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorContatoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

public class IntelliCashDAO extends AbstractIntefaceDao {
    
    private final InterfaceDAO produtoDao = new InterfaceDAO() {
        @Override
        public String getSistema() {
            return "IntelliCash";
        }

        @Override
        public List<MercadologicoIMP> getMercadologicos() throws Exception {
            List<MercadologicoIMP> result = new ArrayList<>();
            
            try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select distinct\n" +
                        "    p.secao merc1,\n" +
                        "    m1.descricao merc1_desc,\n" +
                        "    p.grupo merc2,\n" +
                        "    m2.descricao merc2_desc,\n" +
                        "    p.subgrupo merc3,\n" +
                        "    m3.descricao merc3_desc\n" +
                        "from\n" +
                        "    produtos p\n" +
                        "    join secoes m1 on m1.id = p.secao\n" +
                        "    join grupos m2 on m2.id = p.grupo\n" +
                        "    join subgrupos m3 on m3.id = p.subgrupo\n" +
                        "order by\n" +
                        "    merc1, merc2, merc3"
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
                        "select\n" +
                        "    id,\n" +
                        "    descricao\n" +
                        "from\n" +
                        "    semelhancas\n" +
                        "order by\n" +
                        "    id"
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
            List<ProdutoIMP> result = new ArrayList<>();           
            
            
            try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
                String loja = getLojaOrigem().split("-")[0];
                try (ResultSet rst = stm.executeQuery(
                        "select\n" +
                        "    p.id,\n" +
                        "    p.datacadastro,\n" +
                        "    ob1.descricao as tipoEmbalagem,\n" +
                        "    case when not bal.codigo is null then bal.codigo else e.ean end ean,\n" +
                        "    case when not bal.codigo is null then 1 else 0 end eBalanca,\n" +
                        "    coalesce(bal.validade, 0) validade,\n" +
                        "    p.descricao descricaoCompleta,\n" +
                        "    p.ref descricaoReduzida,\n" +
                        "    p.descricao descricaoGondola,\n" +
                        "    p.secao codMercadologico1,\n" +
                        "    ob2.descricao as mercadologico1,\n" +
                        "    p.grupo codMercadologico2,\n" +
                        "    ob3.descricao as mercadologico2, \n" +
                        "    p.subgrupo codMercadologico3,\n" +
                        "    subg.descricao as mercadologico3,\n" +
                        "    f.id as idFamiliaProduto,\n" +
                        "    f.descricao as desc_familia,\n" +
                        "    p.estqmin estoqueMinimo,\n" +
                        "    p.estqmax estoqueMaximo,\n" +
                        "    (select qtde from getestqprod(p.id, emp.id)) estoque, \n" +
                        "    p.mkp as margem,      \n" +
                        "    p.custo custoSemImposto,\n" +
                        "    p.custo custoComImposto,\n" +
                        "    p.preco precoVenda,\n" +
                        "    p.ativo,\n" +
                        "    fisco.ncm,\n" +
                        "    pst.cod_cest cest,    \n" +
                        "    coalesce(fisco.pis_cst_e, 13) pis_cst_e,\n" +
                        "    coalesce(fisco.pis_cst_s, 1) pis_cst_s,\n" +
                        "    fisco.cod_natureza_receita pis_natureza_receita,\n" +
                        "    case substring(icms.descricao from 1 for 1)\n" +
                        "    when 'F' then 60\n" +
                        "    when 'T' then 0\n" +
                        "    when 'I' then 40\n" +
                        "    when 'N' then 41\n" +
                        "    end icms_cst,\n" +
                        "    icms.valor icms_aliq\n" +
                        "from\n" +
                        "    produtos p\n" +
                        "    left join empresas emp on emp.id = " + loja + "\n" +
                        "    left join pesaveis bal on p.id = bal.id\n" +
                        "    left join estoque est on p.id = est.idprod\n" +
                        "    left join prodst pst on p.id = pst.id\n" +
                        "    left join mxf_vw_pis_cofins fisco on fisco.codigo_produto = p.id\n" +
                        "    left join eans e on e.produto = p.id\n" +
                        "    left join semelhantes fd on fd.idprod = p.id\n" +
                        "    left join semelhancas f on f.id = fd.idclasse\n" +
                        "    left join objetos ob1 on ob1.id = p.unidade\n" +
                        "    left join objetos ob2 on ob2.id = p.secao\n" +
                        "    left join objetos ob3 on ob3.id = p.grupo\n" +
                        "    left join objetos icms on icms.id = p.trib\n" +
                        "    left join objetos subg on subg.id = p.subgrupo\n" +
                        "order by\n" +
                        "    p.id"
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
                        imp.setEstoqueMinimo(rst.getInt("estoqueMaximo"));
                        imp.setEstoque(rst.getDouble("estoque"));
                        imp.setMargem(rst.getDouble("margem"));
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
                            "select\n" +
                            "    a.id,\n" +
                            "    a.nome razao,\n" +
                            "    a.fantasia,\n" +
                            "    a.doc cnpj_cpf,\n" +
                            "    coalesce(dcie.doc, dcrg.doc) ie_rg,\n" +
                            "    f.ativo,\n" +
                            "\n" +
                            "    en.logradouro endereco,\n" +
                            "    en.numero,\n" +
                            "    en.complemento,\n" +
                            "    en.bairro,\n" +
                            "    cid.cidade,   \n" +
                            "    cidibge.id cidade_ibge,\n" +
                            "    cid.uf,\n" +
                            "    en.cep,\n" +
                            "    (select first 1 coalesce('('||ddd||')','')||telefone tel from telefones where agente = a.id ) tel_principal\n" +
                            "from\n" +
                            "    agentes a\n" +
                            "    join forns f on f.id = a.id\n" +
                            "    left join enderecos en on en.agente = a.id\n" +
                            "    left join cidades cid on cid.id = en.cidade\n" +
                            "    left join cidadesibge cidibge on cidibge.id2 = cid.id\n" +
                            "    left join docs dcie on dcie.codag = a.id and dcie.tipo = 66\n" +
                            "    left join docs dcrg on dcrg.codag = a.id and dcrg.tipo = 67\n" +
                            "order by\n" +
                            "    a.id"
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
                                    "select\n" +
                                    "    id,\n" +
                                    "    agente,\n" +
                                    "    email valor,\n" +
                                    "    'EMAIL' tipo,\n" +
                                    "    '' contato\n" +
                                    "from\n" +
                                    "    emails\n" +
                                    "where\n" +
                                    "    agente = " + Utils.quoteSQL(imp.getImportId()) + "\n" +
                                    "union\n" +
                                    "select\n" +
                                    "    id,\n" +
                                    "    agente,\n" +
                                    "    coalesce(coalesce('('||ddd||')','')||telefone,'') valor,\n" +
                                    "    'TELEFONE' tipo,\n" +
                                    "    coalesce(contato,'') contato\n" +
                                    "from\n" +
                                    "    telefones\n" +
                                    "where\n" +
                                    "    agente = " + Utils.quoteSQL(imp.getImportId())
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
                                    if ("EMAIL".equals(rst2.getString("tipo"))) {
                                        contato.setEmail(rst2.getString("valor"));
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
                        "select\n" +
                        "    prod id_produto,\n" +
                        "    forn id_fornecedor,\n" +
                        "    codigo as codigoexterno\n" +
                        "from\n" +
                        "    fornxcodprod"
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
                    "    a.id,\n" +
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
                    "    dcrg.doc as rg\n" +
                    "from\n" +
                    "    agentes a\n" +
                    "    join clientes c on c.id = a.id\n" +
                    "    left join enderecos en on en.agente = a.id\n" +
                    "    left join cidades cid on cid.id = en.cidade\n" +
                    "    left join tiposclientes tpcl on tpcl.id = c.tipocliente\n" +
                    "    left join cidadesibge cidibge on cidibge.id2 = cid.id\n" +
                    "    left join docs dcie on dcie.codag = a.id and dcie.tipo = 66\n" +
                    "    left join docs dcrg on dcrg.codag = a.id and dcrg.tipo = 67\n" +
                    "order by\n" +
                    "    a.id"
                )) {
                    while (rst.next()) {                    
                        ClienteIMP imp = new ClienteIMP();

                        imp.setId(rst.getString("id"));
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
                        imp.setObservacao("IMPORTADO VR");
                        imp.setSalario(rst.getDouble("salario"));                 

                        result.add(imp);
                    }                
                }
            }
            return result;
        }
        
        
        
    };
    
    @Override
    public List<ClientePreferencialVO> carregarCliente(int idLojaCliente) throws Exception {
        List<ClientePreferencialVO> vClientePreferencial = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {                    
            try (ResultSet rst = stm.executeQuery(               
                "select\n" +
                "    a.id,\n" +
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
                "    dcrg.doc as rg\n" +
                "from\n" +
                "    agentes a\n" +
                "    join clientes c on c.id = a.id\n" +
                "    left join enderecos en on en.agente = a.id\n" +
                "    left join cidades cid on cid.id = en.cidade\n" +
                "    left join tiposclientes tpcl on tpcl.id = c.tipocliente\n" +
                "    left join cidadesibge cidibge on cidibge.id2 = cid.id\n" +
                "    left join docs dcie on dcie.codag = a.id and dcie.tipo = 66\n" +
                "    left join docs dcrg on dcrg.codag = a.id and dcrg.tipo = 67\n" +
                "order by\n" +
                "    a.id"
            )) {
                while (rst.next()) {                    
                    ClientePreferencialVO oClientePreferencial = new ClientePreferencialVO();

                    oClientePreferencial.setIdLong(rst.getLong("id"));
                    oClientePreferencial.setCodigoanterior(rst.getLong("id"));
                    oClientePreferencial.setNome(rst.getString("nome"));
                    oClientePreferencial.setEndereco(rst.getString("res_endereco"));
                    oClientePreferencial.setNumero(rst.getString("res_numero"));
                    oClientePreferencial.setComplemento(rst.getString("res_complemento"));
                    oClientePreferencial.setBairro(rst.getString("res_bairro"));
                    oClientePreferencial.setId_municipio(Utils.retornarMunicipioIBGEDescricao(rst.getString("res_cidade"), rst.getString("res_uf")));                     
                    oClientePreferencial.setId_estado(Utils.getEstadoPelaSigla(rst.getString("res_uf")));
                    oClientePreferencial.setCep(rst.getString("res_cep"));
                    oClientePreferencial.setTelefone(rst.getString("fone1"));
                    oClientePreferencial.setTelefone2(rst.getString("fone2"));
                    oClientePreferencial.setCelular(rst.getString("celular"));
                    oClientePreferencial.setInscricaoestadual(rst.getString("inscricaoestadual"));
                    oClientePreferencial.setCnpj(rst.getString("cnpj"));
                    if (String.valueOf(oClientePreferencial.getCnpj()).length() < 8) {
                        oClientePreferencial.setCnpj(-1);
                    }
                    oClientePreferencial.setVencimentocreditorotativo(rst.getInt("prazodias"));
                    oClientePreferencial.setDataresidencia("1990/01/01");
                    oClientePreferencial.setDatacadastro(rst.getDate("datacadastro"));
                    oClientePreferencial.setEmail(rst.getString("email"));
                    oClientePreferencial.setValorlimite(rst.getDouble("limitepreferencial"));
                    oClientePreferencial.setId_situacaocadastro(1);
                    oClientePreferencial.setObservacao("IMPORTADO VR");
                    oClientePreferencial.setId_tipoinscricao(String.valueOf(oClientePreferencial.getCnpj()).length() > 11 ? 0 : 1);
                    oClientePreferencial.setSalario(rst.getDouble("salario"));                 

                    vClientePreferencial.add(oClientePreferencial);
                }                
            }
        }
        return vClientePreferencial;
    } 
    
    public List<ItemComboVO> getLojasCliente() throws Exception {
        List<ItemComboVO> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                "select\n" +
                "    emp.id,\n" +
                "    a.fantasia nome\n" +
                "from\n" +
                "    empresas emp\n" +
                "    join agentes a on emp.codag = a.id\n" +
                "order by\n" +
                "    emp.id"
            )) {
                while (rst.next()) {
                    result.add(new ItemComboVO(rst.getInt("id"), rst.getString("nome")));
                }
            }
        }
        return result;
    }
    
    public InterfaceDAO getProdutoDao() {
        return produtoDao;
    }

    //CARREGAMENTOS
    
    private List<ProdutoVO> carregarMargemProduto() throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        List<ProdutoVO> vProduto = new ArrayList<>();
        double idProduto = 0, margem = 0;        
        
        try {
            stm = ConexaoFirebird.getConexao().createStatement();
            
            sql = new StringBuilder();
            sql.append("select id, mkp from produtos ");
            
            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                
                idProduto = Double.parseDouble(Utils.formataNumero(rst.getString("id").trim()));
                
                if ((rst.getString("mkp") != null) &&
                        (!rst.getString("mkp").trim().isEmpty())) {
                    margem = Double.parseDouble(rst.getString("mkp").trim());
                } else {
                    margem = 0;
                }
                
                ProdutoVO oProduto = new ProdutoVO();
                oProduto.idDouble = idProduto;
                oProduto.margem = margem;
                
                vProduto.add(oProduto);
            }
            
            stm.close();
            return vProduto;
        } catch(Exception ex) {
            throw ex;
        }
    }
    
    public List<ProdutoVO> carregarProdutoFamiliaProdutoIntelliCash() throws Exception {

        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        List<ProdutoVO> vProduto = new ArrayList<>();

        try {

            stm = ConexaoFirebird.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append(" SELECT IDCLASSE,         ");
            sql.append("        IDPROD ");
            sql.append(" FROM SEMELHANTES      ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                ProdutoVO oProduto = new ProdutoVO();
                
                oProduto.idDouble = Double.parseDouble(Utils.formataNumero(rst.getString("IDPROD").trim()));
                oProduto.codigoAnterior = Double.parseDouble(Utils.formataNumero(rst.getString("IDCLASSE").trim()));
                
                vProduto.add(oProduto);
                
                
            }

            return vProduto;

        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }
    
    public List<FamiliaProdutoVO> carregarFamiliaProdutoIntelliCash() throws Exception {

        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        List<FamiliaProdutoVO> vFamiliaProduto = new ArrayList<>();

        try {

            stm = ConexaoFirebird.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append(" SELECT ID,         ");
            sql.append("        DESCRICAO ");
            sql.append(" FROM SEMELHANCAS      ");
            sql.append(" ORDER BY ID        ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                FamiliaProdutoVO oFamiliaProduto = new FamiliaProdutoVO();

                oFamiliaProduto.idLong = Long.parseLong(Utils.formataNumero(rst.getString("ID")));
                oFamiliaProduto.descricao = Utils.acertarTexto(rst.getString("DESCRICAO").replace("'", "").trim());
                oFamiliaProduto.id_situacaocadastro = 1;                
                oFamiliaProduto.codigoant = 0;

                vFamiliaProduto.add(oFamiliaProduto);

            }

            return vFamiliaProduto;

        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }

    public List<MercadologicoVO> carregarMercadologicoIntelliCash(int nivel) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        Utils util = new Utils();
        List<MercadologicoVO> vMercadologico = new ArrayList<>();
        String descricao = "";
        int mercadologico1, mercadologico2, mercadologico3;

        try {

            stm = ConexaoFirebird.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append(" SELECT DISTINCT P.GRUPO, M1.S_DESCRICAO AS MERC1, ");
            sql.append("        P.DEPARTAMENTO, M2.S_DESCRICAO AS MERC2,   ");
            sql.append("        P.SESSAO, M3.S_DESCRICAO AS MERC3          ");
            sql.append("   FROM PRODUTOS P                        ");
            sql.append("  INNER JOIN GRUPOS M1 ON M1.ID = P.GRUPO ");
            sql.append("  INNER JOIN DEPARTAMENTOS M2 ON M2.ID = P.DEPARTAMENTO ");
            sql.append("  INNER JOIN SESSOES M3 ON M3.ID = P.SESSAO             ");
            sql.append("  ORDER BY P.GRUPO,  P.DEPARTAMENTO, P.SESSAO;          ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                mercadologico1 = 0;
                mercadologico2 = 0;
                mercadologico3 = 0;

                MercadologicoVO oMercadologico = new MercadologicoVO();

                if (nivel == 1) {
                    mercadologico1 = Integer.parseInt(rst.getString("GRUPO"));
                    descricao = util.acertarTexto(rst.getString("MERC1").replace("'", "").trim());
                    if (descricao.length() > 35) {
                        descricao = descricao.substring(0, 35);
                    }
                    oMercadologico.mercadologico1 = mercadologico1;
                    oMercadologico.mercadologico2 = 0;
                    oMercadologico.mercadologico3 = 0;
                    oMercadologico.mercadologico4 = 0;
                    oMercadologico.mercadologico5 = 0;
                    oMercadologico.descricao = descricao;
                    oMercadologico.nivel = nivel;

                } else if (nivel == 2) {

                    mercadologico1 = Integer.parseInt(rst.getString("GRUPO"));
                    mercadologico2 = Integer.parseInt(rst.getString("DEPARTAMENTO"));
                    descricao = util.acertarTexto(rst.getString("MERC2").replace("'", "").trim());
                    if (descricao.length() > 35) {
                        descricao = descricao.substring(0, 35);
                    }
                    oMercadologico.mercadologico1 = mercadologico1;
                    oMercadologico.mercadologico2 = mercadologico2;
                    oMercadologico.mercadologico3 = 0;
                    oMercadologico.mercadologico4 = 0;
                    oMercadologico.mercadologico5 = 0;
                    oMercadologico.descricao = descricao;
                    oMercadologico.nivel = nivel;
                } else if (nivel == 3) {
                    mercadologico1 = Integer.parseInt(rst.getString("GRUPO"));
                    mercadologico2 = Integer.parseInt(rst.getString("DEPARTAMENTO"));
                    mercadologico3 = Integer.parseInt(rst.getString("SESSAO"));
                    descricao = util.acertarTexto(rst.getString("MERC3").replace("'", "").trim());
                    if (descricao.length() > 35) {
                        descricao = descricao.substring(0, 35);
                    }
                    oMercadologico.mercadologico1 = mercadologico1;
                    oMercadologico.mercadologico2 = mercadologico2;
                    oMercadologico.mercadologico3 = mercadologico3;
                    oMercadologico.mercadologico4 = 0;
                    oMercadologico.mercadologico5 = 0;
                    oMercadologico.descricao = descricao;
                    oMercadologico.nivel = nivel;
                }

                vMercadologico.add(oMercadologico);
            }

            return vMercadologico;

        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }

    public List<ProdutoVO> carregarPisCofinsWisa() throws SQLException, Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst;
        List<ProdutoVO> vProdutoPisCofins = new ArrayList<>();
        double idProduto = 0;
        int idTipoPisCofins, idTipoPisCofinsCredito, tipoNaturezaReceita,
                idTipoPisCofinsCodigoAnterior, idTipoPisCofinsCreditoCodigoAnterior, tipoNaturezaReceitaCodigoAnterior;
        Utils util = new Utils();

        try {

            stm = ConexaoFirebird.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append(" SELECT S.SUP001,       ");
            sql.append("        CASE S.SUP108   ");
            sql.append("             WHEN 1 THEN 9 ");
            sql.append("             WHEN 2 THEN 3 ");
            sql.append("             WHEN 3 THEN 0 ");
            sql.append("             WHEN 4 THEN 1 ");
            sql.append("             WHEN 5 THEN 2 ");
            sql.append("        END AS PISCOFINSDEBITO, ");
            sql.append("        CASE S.SUP108 ");
            sql.append("             WHEN 1 THEN 21 ");
            sql.append("             WHEN 2 THEN 15 ");
            sql.append("             WHEN 3 THEN 12 ");
            sql.append("             WHEN 4 THEN 13 ");
            sql.append("             WHEN 5 THEN 14 ");
            sql.append("        END AS PISCOFINSCREDITO, ");
            sql.append("        S90.codigonatrec AS NATCODIGO ");
            sql.append(" FROM SUP001 S ");
            sql.append(" INNER JOIN SUP090 S90 ON ");
            sql.append(" S90.sup090 = S.sup090    ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                ProdutoVO oProduto = new ProdutoVO();

                if ((rst.getString("PISCOFINSDEBITO") != null)
                        && (!rst.getString("PISCOFINSDEBITO").trim().isEmpty())) {
                    idTipoPisCofins = Integer.parseInt(rst.getString("PISCOFINSDEBITO").trim());
                    idTipoPisCofinsCodigoAnterior = Integer.parseInt(rst.getString("PISCOFINSDEBITO").trim());
                } else {
                    idTipoPisCofins = 0;
                    idTipoPisCofinsCodigoAnterior = -1;
                }

                if ((rst.getString("PISCOFINSCREDITO") != null)
                        && (!rst.getString("PISCOFINSCREDITO").trim().isEmpty())) {
                    idTipoPisCofinsCredito = Integer.parseInt(rst.getString("PISCOFINSCREDITO").trim());
                    idTipoPisCofinsCreditoCodigoAnterior = Integer.parseInt(rst.getString("PISCOFINSCREDITO").trim());
                } else {
                    idTipoPisCofinsCredito = 12;
                    idTipoPisCofinsCreditoCodigoAnterior = -1;
                }

                if ((rst.getString("NATCODIGO") != null)
                        && (!rst.getString("NATCODIGO").trim().isEmpty())) {
                    tipoNaturezaReceita = util.retornarTipoNaturezaReceita(idTipoPisCofins,
                            util.acertarTexto(rst.getString("NATCODIGO").trim()));
                    tipoNaturezaReceitaCodigoAnterior = util.retornarTipoNaturezaReceita(idTipoPisCofins,
                            util.acertarTexto(rst.getString("NATCODIGO").trim()));
                } else {
                    tipoNaturezaReceita = util.retornarTipoNaturezaReceita(idTipoPisCofins, "");
                    tipoNaturezaReceitaCodigoAnterior = -1;
                }

                idProduto = Double.parseDouble(rst.getString("SUP001"));

                oProduto.idDouble = idProduto;
                oProduto.idTipoPisCofinsDebito = idTipoPisCofins;
                oProduto.idTipoPisCofinsCredito = idTipoPisCofinsCredito;
                oProduto.tipoNaturezaReceita = tipoNaturezaReceita;

                CodigoAnteriorVO oCodigoAnterior = new CodigoAnteriorVO();
                oCodigoAnterior.codigoatual = idProduto;
                oCodigoAnterior.piscofinscredito = idTipoPisCofinsCreditoCodigoAnterior;
                oCodigoAnterior.piscofinsdebito = idTipoPisCofinsCodigoAnterior;
                oCodigoAnterior.naturezareceita = tipoNaturezaReceitaCodigoAnterior;
                oProduto.vCodigoAnterior.add(oCodigoAnterior);

                vProdutoPisCofins.add(oProduto);
            }

            return vProdutoPisCofins;
        } catch (Exception ex) {
            throw ex;
        }
    }

    private List<ProdutoVO> carregarDataCadastroProduto() throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        double idProduto = 0;
        String dataCadastro = "";
        
        try {
            stm = ConexaoFirebird.getConexao().createStatement();
            
            sql = new StringBuilder();
            sql.append("select ID, datacadastro ");
            sql.append("from produtos ");
            
            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                
                idProduto = Double.parseDouble(Utils.formataNumero(rst.getString("id").trim()));
                
                if ((rst.getString("datacadastro") != null) &&
                        (!rst.getString("datacadastro").trim().isEmpty())) {
                    
                    dataCadastro = rst.getString("datacadastro").trim().substring(0, 10).replace(".", "/").replace("-", "/");
                    
                } else {
                    dataCadastro = "";
                }
                
                ProdutoVO oProduto = new ProdutoVO();
                oProduto.idDouble = idProduto;
                oProduto.dataCadastro = dataCadastro;
                
                vProduto.add(oProduto);
            }
            
            return vProduto;
        } catch(Exception ex) {
            throw ex;
        }
    }
    
    public Map<Double, ProdutoVO> carregarProdutoIntelliCash() throws Exception {
        Map<Double, ProdutoVO> vProduto = new HashMap<>();
        StringBuilder sql = null;
        Statement stm = null, stmPostgres = null;
        ResultSet rst = null, rstPostgres = null;
        Utils util = new Utils();
        int idTipoEmbalagem = 0, qtdEmbalagem, idTipoPisCofins, idTipoPisCofinsCredito, tipoNaturezaReceita,
                idAliquota, idFamilia, mercadologico1, mercadologico2, mercadologico3, idSituacaoCadastro,
                ncm1, ncm2, ncm3, codigoBalanca, referencia = -1, validade = 0;
        String descricaoCompleta, descricaoReduzida, descricaoGondola, ncmAtual, strCodigoBarras, dataCadastro = "";
        boolean eBalanca, pesavel;
        long codigoBarras = 0;
        double margem, precoVenda, custo, idProduto, idFamiliaDouble = -1;
        
        try {

            stmPostgres = Conexao.createStatement();

            stm = ConexaoFirebird.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("SELECT E.EAN, P.ID, OB1.descricao AS UNIDADE, OB2.descricao AS SECAO, ");
            sql.append("       OB3.descricao AS GRUPO, OB4.descricao AS MARCA,P.TRIB, ");
            sql.append("       OB5.descricao ICMS, OB6.descricao AS TAMANHO, ");
            sql.append("       OB7.descricao AS FABRICANTE, OB8.descricao AS COLECAO, ");
            sql.append("       P.descricao, p.ref as DESCRICAO_REDUZIDA, p.custo, p.preco, p.datacadastro, ");
            sql.append("       p.ativo, p.subgrupo, p.bcpiscofins, p.mkp AS MARGEM, ");
            sql.append("       FISCO.ncm, fisco.cod_natureza_receita, COALESCE(fisco.pis_cst_E,13) pis_cst_E, ");
            sql.append("       COALESCE(fisco.pis_cst_s,1) pis_cst_s, F.id AS ID_FAMILIA, F.descricao AS DESC_FAMILIA ");            
            sql.append("FROM PRODUTOS P                                                             ");
            sql.append("    LEFT OUTER JOIN mxf_VW_pis_cofins FISCO ON FISCO.CODIGO_PRODUTO = P.ID ");           
            sql.append("    LEFT OUTER JOIN EANS E ON E.produto = P.ID      ");      
            sql.append("    LEFT OUTER JOIN SEMELHANTES FD ON FD.IDPROD = P.ID ");
            sql.append("    LEFT OUTER JOIN SEMELHANCAS F ON F.ID = FD.IDCLASSE ");
            sql.append("    INNER JOIN OBJETOS OB1 ON OB1.ID = P.UNIDADE    ");
            sql.append("    INNER JOIN OBJETOS OB2 ON OB2.ID = P.SECAO      ");
            sql.append("    INNER JOIN OBJETOS OB3 ON OB3.ID = P.GRUPO      ");
            sql.append("    INNER JOIN OBJETOS OB4 ON OB4.ID = P.MARCA      ");
            sql.append("    INNER JOIN OBJETOS OB5 ON OB5.ID = P.TRIB       ");
            sql.append("    INNER JOIN OBJETOS OB6 ON OB6.ID = P.TAMANHO    ");
            sql.append("    INNER JOIN OBJETOS OB7 ON OB7.ID = P.FABRICANTE ");
            sql.append("    INNER JOIN OBJETOS OB8 ON OB8.ID = P.COLECAO    ");
            sql.append("WHERE P.ID <= 999999                                ");
            sql.append("ORDER BY P.ID                                       ");
            

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                ProdutoVO oProduto = new ProdutoVO();

                if (rst.getInt("ATIVO")==1) {
                    idSituacaoCadastro = 1;
                } else {
                    idSituacaoCadastro = 0;
                }

                dataCadastro = "";
                
                eBalanca = false;
                codigoBalanca = -1;
                pesavel = false;
                idTipoEmbalagem = 0;

                sql = new StringBuilder();
                sql.append("select codigo, descricao, pesavel, validade ");
                sql.append("from implantacao.produtobalanca ");
                sql.append("where cast(codigo as numeric (14,0))  = " + Double.parseDouble(Utils.formataNumero(rst.getString("EAN").replace(".", ""))));

                rstPostgres = stmPostgres.executeQuery(sql.toString());
                idProduto = Double.parseDouble(Utils.formataNumero(rst.getString("id").trim()));
                if (rstPostgres.next()) {
                    eBalanca = true;
                    codigoBalanca = rstPostgres.getInt("codigo");
                    validade = rstPostgres.getInt("validade");
                    
                    if ("P".equals(rstPostgres.getString("pesavel").trim())) {
                        pesavel = false;
                        idTipoEmbalagem = 4;
                    } else if ("U".equals(rstPostgres.getString("pesavel").trim())) {
                        pesavel = true;
                        idTipoEmbalagem = 0;
                    } else {
                        pesavel = false;
                        idTipoEmbalagem = 4;
                    }
                    
                } else {
                    eBalanca = false;
                    pesavel = false;
                    validade = 0;
                    if ("CX".equals(rst.getString("UNIDADE").trim())) {
                        idTipoEmbalagem = 1;
                    } else if ("KG".equals(rst.getString("UNIDADE").trim())) {
                        idTipoEmbalagem = 4;
                    } else if ("UN".equals(rst.getString("UNIDADE").trim())) {
                        idTipoEmbalagem = 0;
                    } else {
                        idTipoEmbalagem = 0;
                    }
                }

                if ((rst.getString("DESCRICAO") != null)
                        && (!rst.getString("DESCRICAO").trim().isEmpty())) {
                    byte[] bytes = rst.getBytes("DESCRICAO");
                    String textoAcertado = new String(bytes, "ISO-8859-1");
                    descricaoCompleta = util.acertarTexto(textoAcertado.replace("'", "").trim());
                } else {
                    descricaoCompleta = "";
                }

                if ((rst.getString("DESCRICAO_REDUZIDA") != null)
                        && (!rst.getString("DESCRICAO_REDUZIDA").trim().isEmpty())) {
                    byte[] bytes = rst.getBytes("DESCRICAO_REDUZIDA");
                    String textoAcertado = new String(bytes, "ISO-8859-1");
                    descricaoReduzida = util.acertarTexto(textoAcertado.replace("'", "").trim());
                } else {
                    descricaoReduzida = "";
                }

                descricaoGondola = descricaoCompleta;
                qtdEmbalagem = 1;
                
                if ((rst.getString("ID_FAMILIA") != null) &&
                        (!rst.getString("ID_FAMILIA").trim().isEmpty())) {
                    
                    idFamiliaDouble = Double.parseDouble(Utils.formataNumero(rst.getString("ID_FAMILIA").trim()));
                    
                    sql = new StringBuilder();
                    sql.append("select f.id from familiaproduto f ");
                    sql.append("inner join implantacao.codigoanterior_familiaproduto ant ");
                    sql.append("on ant.codigoatual = f.id ");
                    sql.append("where ant.codigoanterior = " + idFamiliaDouble);
                    
                    rstPostgres = stmPostgres.executeQuery(sql.toString());
                    
                    if (rstPostgres.next()) {
                        idFamilia = rstPostgres.getInt("id");
                    } else {
                        idFamilia = -1;
                    }

                } else {
                    idFamilia = -1;
                }

                // MERCADOLOGICO MODELO VR SOFTWARE
                mercadologico1 = 14;
                mercadologico2 = 1;
                mercadologico3 = 1;

                if ((rst.getString("NCM") != null)
                        && (!rst.getString("NCM").trim().isEmpty())) {
                    ncmAtual = util.formataNumero(rst.getString("NCM"));
                    if ((ncmAtual != null)
                            && (!ncmAtual.isEmpty())
                            && (ncmAtual.length() > 5)) {
                        try {
                            NcmVO oNcm = new NcmDAO().validar(ncmAtual);
                            ncm1 = oNcm.ncm1;
                            ncm2 = oNcm.ncm2;
                            ncm3 = oNcm.ncm3;
                        } catch (Exception ex) {
                            ncm1 = 402;
                            ncm2 = 99;
                            ncm3 = 0;
                        }
                    } else {
                        ncm1 = 402;
                        ncm2 = 99;
                        ncm3 = 0;
                    }
                } else {
                    ncm1 = 402;
                    ncm2 = 99;
                    ncm3 = 0;
                }

                if (eBalanca == true) {
                    codigoBarras = (long) idProduto;
                } else {

                    if ((rst.getString("EAN") != null)
                            && (!rst.getString("EAN").trim().isEmpty())) {

                        strCodigoBarras = Utils.formataNumero(rst.getString("EAN").replace(".", "").trim());

                        if (String.valueOf(Long.parseLong(strCodigoBarras)).length() < 7) {
                            codigoBarras = -1;
                        } else {
                            
                            if (strCodigoBarras.length() > 14) {
                                codigoBarras = Long.parseLong(strCodigoBarras.substring(0, 14));
                            }
                            
                            codigoBarras = Long.parseLong(strCodigoBarras);
                        }
                    }
                }
               
                if ((rst.getString("PIS_CST_S") != null) &&
                 (!rst.getString("PIS_CST_S").trim().isEmpty())) {
                    idTipoPisCofins = util.retornarPisCofinsDebito(rst.getInt("PIS_CST_S"));
                }else{
                    idTipoPisCofins = 1;                    
                }
                
                if ((rst.getString("PIS_CST_E") != null) &&
                  (!rst.getString("PIS_CST_E").trim().isEmpty())) {                
                     idTipoPisCofinsCredito = util.retornarPisCofinsCredito(rst.getInt("PIS_CST_E"));
                }else{
                     idTipoPisCofinsCredito = 13;                   
                }

                if ((rst.getString("COD_NATUREZA_RECEITA") != null) &&
                 (!rst.getString("COD_NATUREZA_RECEITA").trim().isEmpty())) {
                    tipoNaturezaReceita = Utils.retornarTipoNaturezaReceita(idTipoPisCofins, 
                    rst.getString("COD_NATUREZA_RECEITA").trim());
                } else {
                    tipoNaturezaReceita = Utils.retornarTipoNaturezaReceita(idTipoPisCofins, "");
                }

                if ((rst.getString("ICMS") != null)
                        && (!rst.getString("ICMS").trim().isEmpty())) {
                    idAliquota = retornarAliquotaICMSIntelliCash(rst.getString("ICMS").trim().toUpperCase(), "");
                } else {
                    idAliquota = 8;
                }

                if ((rst.getString("MARGEM") != null)
                        && (!rst.getString("MARGEM").trim().isEmpty())) {
                    margem = Double.parseDouble(rst.getString("MARGEM").replace(",", "."));
                } else {
                    margem = 0;
                }
                
                if ((rst.getString("PRECO") != null)
                        && (!rst.getString("PRECO").trim().isEmpty())) {
                    precoVenda = Double.parseDouble(rst.getString("PRECO").replace(",", "."));
                } else {
                    precoVenda = 0;
                }
                if ((rst.getString("custo") != null)
                        && (!rst.getString("custo").trim().isEmpty())) {
                    custo = Double.parseDouble(rst.getString("custo").replace(",", "."));
                } else {
                    custo = 0;
                }

                if (descricaoCompleta.length() > 60) {
                    descricaoCompleta = descricaoCompleta.substring(0, 60);
                }

                if (descricaoReduzida.length() > 22) {
                    descricaoReduzida = descricaoReduzida.substring(0, 22);
                }

                if (descricaoGondola.length() > 60) {
                    descricaoGondola = descricaoGondola.substring(0, 60);
                }

                oProduto.idDouble = idProduto;
                oProduto.descricaoCompleta = descricaoCompleta;
                oProduto.descricaoReduzida = descricaoReduzida;
                oProduto.descricaoGondola = descricaoGondola;
                oProduto.idTipoEmbalagem = idTipoEmbalagem;
                oProduto.qtdEmbalagem = qtdEmbalagem;
                oProduto.idTipoPisCofinsDebito = idTipoPisCofins;
                oProduto.idTipoPisCofinsCredito = idTipoPisCofinsCredito;
                oProduto.tipoNaturezaReceita = tipoNaturezaReceita;
                oProduto.pesavel = pesavel;
                oProduto.mercadologico1 = mercadologico1;
                oProduto.mercadologico2 = mercadologico2;
                oProduto.mercadologico3 = mercadologico3;
                oProduto.ncm1 = ncm1;
                oProduto.ncm2 = ncm2;
                oProduto.ncm3 = ncm3;
                oProduto.idFamiliaProduto = idFamilia;
                oProduto.idFornecedorFabricante = 1;
                oProduto.sugestaoPedido = true;
                oProduto.aceitaMultiplicacaoPdv = true;
                oProduto.sazonal = false;
                oProduto.fabricacaoPropria = false;
                oProduto.consignado = false;
                oProduto.ddv = 0;
                oProduto.permiteTroca = true;
                oProduto.vendaControlada = false;
                oProduto.vendaPdv = true;
                oProduto.conferido = true;
                oProduto.permiteQuebra = true;
                oProduto.permitePerda = true;
                oProduto.utilizaTabelaSubstituicaoTributaria = false;
                oProduto.utilizaValidadeEntrada = false;
                oProduto.validade = validade;
                oProduto.margem = margem;
                oProduto.dataCadastro = dataCadastro;

                ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();

                oComplemento.idSituacaoCadastro = idSituacaoCadastro;

                oProduto.vComplemento.add(oComplemento);
                oComplemento.precoVenda = precoVenda;
                oComplemento.precoDiaSeguinte = precoVenda;
                oComplemento.custoComImposto = custo;
                oComplemento.custoSemImposto = custo;

                ProdutoAliquotaVO oAliquota = new ProdutoAliquotaVO();

                oAliquota.idEstado = 31;
                oAliquota.idAliquotaDebito = idAliquota;
                oAliquota.idAliquotaCredito = idAliquota;
                oAliquota.idAliquotaDebitoForaEstado = idAliquota;
                oAliquota.idAliquotaCreditoForaEstado = idAliquota;
                oAliquota.idAliquotaDebitoForaEstadoNF = idAliquota;

                oProduto.vAliquota.add(oAliquota);

                ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();

                oAutomacao.codigoBarras = codigoBarras;
                oAutomacao.qtdEmbalagem = qtdEmbalagem;
                oAutomacao.idTipoEmbalagem = idTipoEmbalagem;

                oProduto.vAutomacao.add(oAutomacao);

                CodigoAnteriorVO oCodigoAnterior = new CodigoAnteriorVO();

                oCodigoAnterior.codigoanterior = idProduto;
                
                if ((rst.getString("EAN") != null)
                        && (!rst.getString("EAN").trim().isEmpty())) {
                    oCodigoAnterior.barras = Long.parseLong(Utils.formataNumero(rst.getString("EAN").trim()));
                } else {
                    oCodigoAnterior.barras = -1;
                }
                
                if ((rst.getString("PIS_CST_S") != null) &&
                 (!rst.getString("PIS_CST_S").trim().isEmpty())) {
                    oCodigoAnterior.piscofinsdebito = Integer.parseInt(rst.getString("PIS_CST_S").trim());
                } else {
                    oCodigoAnterior.piscofinsdebito = -1;
                }
                
                if ((rst.getString("PIS_CST_E") != null) &&
                  (!rst.getString("PIS_CST_E").trim().isEmpty())) {                
                     oCodigoAnterior.piscofinscredito = Integer.parseInt(rst.getString("PIS_CST_E").trim());
                }else{
                     oCodigoAnterior.piscofinscredito = -1;
                }

                if ((rst.getString("COD_NATUREZA_RECEITA") != null) &&
                 (!rst.getString("COD_NATUREZA_RECEITA").trim().isEmpty())) {                    
                    oCodigoAnterior.naturezareceita = Integer.parseInt(rst.getString("COD_NATUREZA_RECEITA").trim());                    
                } else {
                    oCodigoAnterior.naturezareceita = -1;
                }                
                
                if ((rst.getString("ICMS") != null)
                        && (!rst.getString("ICMS").trim().isEmpty())) {
                    oCodigoAnterior.ref_icmsdebito = rst.getString("ICMS").trim();
                } else {
                    oCodigoAnterior.ref_icmsdebito = "";
                }
                
                oCodigoAnterior.estoque = -1;
                oCodigoAnterior.e_balanca = eBalanca;
                oCodigoAnterior.codigobalanca = codigoBalanca;
                oCodigoAnterior.custosemimposto = custo;
                oCodigoAnterior.custocomimposto = custo;
                oCodigoAnterior.margem = margem;
                oCodigoAnterior.precovenda = precoVenda;
                oCodigoAnterior.referencia = -1;
                
                if ((rst.getString("NCM") != null)
                        && (!rst.getString("NCM").trim().isEmpty())) {
                    ncmAtual = Utils.formataNumero(rst.getString("NCM"));
                    oCodigoAnterior.ncm = ncmAtual;
                } else {
                    oCodigoAnterior.ncm = "";
                }
                oProduto.vCodigoAnterior.add(oCodigoAnterior);

                vProduto.put(idProduto, oProduto);
            }

            stmPostgres.close();
            return vProduto;

        } catch (Exception ex) {
            throw ex;
        }
    }

    public Map<Double, ProdutoVO> carregarProdutoMaior6DigitosIntelliCash() throws Exception {
        Map<Double, ProdutoVO> vProduto = new HashMap<>();
        StringBuilder sql = null;
        Statement stm = null, stmPostgres = null;
        ResultSet rst = null, rstPostgres = null;
        Utils util = new Utils();
        int idTipoEmbalagem = 0, qtdEmbalagem, idTipoPisCofins, idTipoPisCofinsCredito, tipoNaturezaReceita,
                idAliquota, idFamilia, mercadologico1, mercadologico2, mercadologico3, idSituacaoCadastro,
                ncm1, ncm2, ncm3, codigoBalanca, referencia = -1, validade = 0;
        String descricaoCompleta, descricaoReduzida, descricaoGondola, ncmAtual, strCodigoBarras, dataCadastro = "";
        boolean eBalanca, pesavel;
        long codigoBarras = 0;
        double margem, precoVenda, custo, idProduto, idFamiliaDouble;
        
        try {

            stmPostgres = Conexao.createStatement();

            stm = ConexaoFirebird.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("SELECT E.EAN, P.ID, OB1.descricao AS UNIDADE, OB2.descricao AS SECAO, ");
            sql.append("       OB3.descricao AS GRUPO, OB4.descricao AS MARCA,P.TRIB, ");
            sql.append("       OB5.descricao ICMS, OB6.descricao AS TAMANHO, ");
            sql.append("       OB7.descricao AS FABRICANTE, OB8.descricao AS COLECAO, ");
            sql.append("       P.descricao, p.ref as DESCRICAO_REDUZIDA, p.custo, p.preco, p.datacadastro, ");
            sql.append("       p.ativo, p.subgrupo, p.bcpiscofins, p.mkp AS MARGEM,            ");
            sql.append("       FISCO.ncm, fisco.cod_natureza_receita, COALESCE(fisco.pis_cst_E,13) pis_cst_E, ");
            sql.append("       COALESCE(fisco.pis_cst_s,1) pis_cst_s, F.id AS ID_FAMILIA, F.descricao AS DESC_FAMILIA ");          
            sql.append("FROM PRODUTOS P                                                             ");
            sql.append("    LEFT OUTER JOIN mxf_VW_pis_cofins FISCO ON FISCO.CODIGO_PRODUTO = P.ID ");           
            sql.append("    LEFT OUTER JOIN EANS E ON E.produto = P.ID      ");                  
            sql.append("    LEFT OUTER JOIN SEMELHANTES FD ON FD.IDPROD = P.ID ");
            sql.append("    LEFT OUTER JOIN SEMELHANCAS F ON F.ID = FD.IDCLASSE ");
            sql.append("    INNER JOIN OBJETOS OB1 ON OB1.ID = P.UNIDADE    ");
            sql.append("    INNER JOIN OBJETOS OB2 ON OB2.ID = P.SECAO      ");
            sql.append("    INNER JOIN OBJETOS OB3 ON OB3.ID = P.GRUPO      ");
            sql.append("    INNER JOIN OBJETOS OB4 ON OB4.ID = P.MARCA      ");
            sql.append("    INNER JOIN OBJETOS OB5 ON OB5.ID = P.TRIB       ");
            sql.append("    INNER JOIN OBJETOS OB6 ON OB6.ID = P.TAMANHO    ");
            sql.append("    INNER JOIN OBJETOS OB7 ON OB7.ID = P.FABRICANTE ");
            sql.append("    INNER JOIN OBJETOS OB8 ON OB8.ID = P.COLECAO    ");
            sql.append("WHERE P.ID > 999999                                ");
            sql.append("ORDER BY ID                                         ");
            

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                ProdutoVO oProduto = new ProdutoVO();

                if (rst.getInt("ATIVO")==1) {
                    idSituacaoCadastro = 1;
                } else {
                    idSituacaoCadastro = 0;
                }

                if ((rst.getString("datacadastro") != null) &&
                        (!rst.getString("datacadastro").trim().isEmpty())) {
                    
                    dataCadastro = rst.getString("datacadastro").trim().substring(0, 10).replace(".", "/").replace("-", "/");
                    
                } else {
                    dataCadastro = "";
                }
                
                eBalanca = false;
                codigoBalanca = -1;
                pesavel = false;
                idTipoEmbalagem = 0;

                sql = new StringBuilder();
                sql.append("select codigo, descricao, pesavel, validade ");
                sql.append("from implantacao.produtobalanca ");
                sql.append("where cast(codigo as numeric (14,0))  = " + Double.parseDouble(Utils.formataNumero(rst.getString("EAN").replace(".", ""))));

                rstPostgres = stmPostgres.executeQuery(sql.toString());
                idProduto = Double.parseDouble(Utils.formataNumero(rst.getString("id").trim()));
                if (rstPostgres.next()) {
                    eBalanca = true;
                    codigoBalanca = rstPostgres.getInt("codigo");
                    validade = rstPostgres.getInt("validade");
                    
                    if ("P".equals(rstPostgres.getString("pesavel").trim())) {
                        pesavel = false;
                        idTipoEmbalagem = 4;
                    } else if ("U".equals(rstPostgres.getString("pesavel").trim())) {
                        pesavel = true;
                        idTipoEmbalagem = 0;
                    } else {
                        pesavel = false;
                        idTipoEmbalagem = 4;
                    }
                    
                } else {
                    eBalanca = false;
                    pesavel = false;
                    validade = 0;
                    if ("CX".equals(rst.getString("UNIDADE").trim())) {
                        idTipoEmbalagem = 1;
                    } else if ("KG".equals(rst.getString("UNIDADE").trim())) {
                        idTipoEmbalagem = 4;
                    } else if ("UN".equals(rst.getString("UNIDADE").trim())) {
                        idTipoEmbalagem = 0;
                    } else {
                        idTipoEmbalagem = 0;
                    }
                }

                if ((rst.getString("DESCRICAO") != null)
                        && (!rst.getString("DESCRICAO").trim().isEmpty())) {
                    byte[] bytes = rst.getBytes("DESCRICAO");
                    String textoAcertado = new String(bytes, "ISO-8859-1");
                    descricaoCompleta = util.acertarTexto(textoAcertado.replace("'", "").trim());
                } else {
                    descricaoCompleta = "";
                }

                if ((rst.getString("DESCRICAO_REDUZIDA") != null)
                        && (!rst.getString("DESCRICAO_REDUZIDA").trim().isEmpty())) {
                    byte[] bytes = rst.getBytes("DESCRICAO_REDUZIDA");
                    String textoAcertado = new String(bytes, "ISO-8859-1");
                    descricaoReduzida = util.acertarTexto(textoAcertado.replace("'", "").trim());
                } else {
                    descricaoReduzida = "";
                }

                descricaoGondola = descricaoCompleta;
                qtdEmbalagem = 1;

                if ((rst.getString("ID_FAMILIA") != null) &&
                        (!rst.getString("ID_FAMILIA").trim().isEmpty())) {
                    
                    idFamiliaDouble = Double.parseDouble(Utils.formataNumero(rst.getString("ID_FAMILIA").trim()));
                    
                    sql = new StringBuilder();
                    sql.append("select f.id from familiaproduto f ");
                    sql.append("inner join implantacao.codigoanterior_familiaproduto ant ");
                    sql.append("on ant.codigoatual = f.id ");
                    sql.append("where ant.codigoanterior = " + idFamiliaDouble);
                    
                    rstPostgres = stmPostgres.executeQuery(sql.toString());
                    
                    if (rstPostgres.next()) {
                        idFamilia = rstPostgres.getInt("id");
                    } else {
                        idFamilia = -1;
                    }

                } else {
                    idFamilia = -1;
                }

                // MERCADOLOGICO MODELO VR SOFTWARE
                mercadologico1 = 14;
                mercadologico2 = 1;
                mercadologico3 = 1;

                if ((rst.getString("NCM") != null)
                        && (!rst.getString("NCM").trim().isEmpty())) {
                    ncmAtual = util.formataNumero(rst.getString("NCM"));
                    if ((ncmAtual != null)
                            && (!ncmAtual.isEmpty())
                            && (ncmAtual.length() > 5)) {
                        try {
                            NcmVO oNcm = new NcmDAO().validar(ncmAtual);
                            ncm1 = oNcm.ncm1;
                            ncm2 = oNcm.ncm2;
                            ncm3 = oNcm.ncm3;
                        } catch (Exception ex) {
                            ncm1 = 402;
                            ncm2 = 99;
                            ncm3 = 0;
                        }
                    } else {
                        ncm1 = 402;
                        ncm2 = 99;
                        ncm3 = 0;
                    }
                } else {
                    ncm1 = 402;
                    ncm2 = 99;
                    ncm3 = 0;
                }

                if (eBalanca == true) {
                    codigoBarras = (long) idProduto;
                } else {

                    if ((rst.getString("EAN") != null)
                            && (!rst.getString("EAN").trim().isEmpty())) {

                        strCodigoBarras = Utils.formataNumero(rst.getString("EAN").replace(".", "").trim());

                        if (String.valueOf(Long.parseLong(strCodigoBarras)).length() < 7) {
                            codigoBarras = -1;
                        } else {
                            
                            if (strCodigoBarras.length() > 14) {
                                codigoBarras = Long.parseLong(strCodigoBarras.substring(0, 14));
                            }
                            
                            codigoBarras = Long.parseLong(strCodigoBarras);
                        }
                    }
                }
               
                if ((rst.getString("PIS_CST_S") != null) &&
                 (!rst.getString("PIS_CST_S").trim().isEmpty())) {
                    idTipoPisCofins = util.retornarPisCofinsDebito(rst.getInt("PIS_CST_S"));
                }else{
                    idTipoPisCofins = 1;                    
                }
                
                if ((rst.getString("PIS_CST_E") != null) &&
                  (!rst.getString("PIS_CST_E").trim().isEmpty())) {                
                     idTipoPisCofinsCredito = util.retornarPisCofinsCredito(rst.getInt("PIS_CST_E"));
                }else{
                     idTipoPisCofinsCredito = 13;                   
                }

                if ((rst.getString("COD_NATUREZA_RECEITA") != null) &&
                 (!rst.getString("COD_NATUREZA_RECEITA").trim().isEmpty())) {
                    tipoNaturezaReceita = util.retornarTipoNaturezaReceita(idTipoPisCofins, 
                    rst.getString("COD_NATUREZA_RECEITA").trim());
                } else {
                    tipoNaturezaReceita = util.retornarTipoNaturezaReceita(idTipoPisCofins, "");
                }

                if ((rst.getString("ICMS") != null)
                        && (!rst.getString("ICMS").trim().isEmpty())) {
                    idAliquota = retornarAliquotaICMSIntelliCash(rst.getString("ICMS").trim().toUpperCase(), "");
                } else {
                    idAliquota = 8;
                }

                /*if ((rst.getString("MARGEM") != null)
                        && (!rst.getString("MARGEM").trim().isEmpty())) {
                    margem = Double.parseDouble(rst.getString("MARGEM").replace(",", "."));
                } else {*/
                    margem = 0;
                //}
                
                if ((rst.getString("PRECO") != null)
                        && (!rst.getString("PRECO").trim().isEmpty())) {
                    precoVenda = Double.parseDouble(rst.getString("PRECO").replace(",", "."));
                } else {
                    precoVenda = 0;
                }
                if ((rst.getString("custo") != null)
                        && (!rst.getString("custo").trim().isEmpty())) {
                    custo = Double.parseDouble(rst.getString("custo").replace(",", "."));
                } else {
                    custo = 0;
                }

                if (descricaoCompleta.length() > 60) {
                    descricaoCompleta = descricaoCompleta.substring(0, 60);
                }

                if (descricaoReduzida.length() > 22) {
                    descricaoReduzida = descricaoReduzida.substring(0, 22);
                }

                if (descricaoGondola.length() > 60) {
                    descricaoGondola = descricaoGondola.substring(0, 60);
                }

                oProduto.idDouble = idProduto;
                oProduto.descricaoCompleta = descricaoCompleta;
                oProduto.descricaoReduzida = descricaoReduzida;
                oProduto.descricaoGondola = descricaoGondola;
                oProduto.idTipoEmbalagem = idTipoEmbalagem;
                oProduto.qtdEmbalagem = qtdEmbalagem;
                oProduto.idTipoPisCofinsDebito = idTipoPisCofins;
                oProduto.idTipoPisCofinsCredito = idTipoPisCofinsCredito;
                oProduto.tipoNaturezaReceita = tipoNaturezaReceita;
                oProduto.pesavel = pesavel;
                oProduto.mercadologico1 = mercadologico1;
                oProduto.mercadologico2 = mercadologico2;
                oProduto.mercadologico3 = mercadologico3;
                oProduto.ncm1 = ncm1;
                oProduto.ncm2 = ncm2;
                oProduto.ncm3 = ncm3;
                oProduto.idFamiliaProduto = idFamilia;
                oProduto.idFornecedorFabricante = 1;
                oProduto.sugestaoPedido = true;
                oProduto.aceitaMultiplicacaoPdv = true;
                oProduto.sazonal = false;
                oProduto.fabricacaoPropria = false;
                oProduto.consignado = false;
                oProduto.ddv = 0;
                oProduto.permiteTroca = true;
                oProduto.vendaControlada = false;
                oProduto.vendaPdv = true;
                oProduto.conferido = true;
                oProduto.permiteQuebra = true;
                oProduto.permitePerda = true;
                oProduto.utilizaTabelaSubstituicaoTributaria = false;
                oProduto.utilizaValidadeEntrada = false;
                oProduto.validade = validade;
                oProduto.margem = margem;
                oProduto.dataCadastro = dataCadastro;

                ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();

                oComplemento.idSituacaoCadastro = idSituacaoCadastro;

                oProduto.vComplemento.add(oComplemento);
                oComplemento.precoVenda = precoVenda;
                oComplemento.precoDiaSeguinte = precoVenda;
                oComplemento.custoComImposto = custo;
                oComplemento.custoSemImposto = custo;

                ProdutoAliquotaVO oAliquota = new ProdutoAliquotaVO();

                oAliquota.idEstado = 31;
                oAliquota.idAliquotaDebito = idAliquota;
                oAliquota.idAliquotaCredito = idAliquota;
                oAliquota.idAliquotaDebitoForaEstado = idAliquota;
                oAliquota.idAliquotaCreditoForaEstado = idAliquota;
                oAliquota.idAliquotaDebitoForaEstadoNF = idAliquota;

                oProduto.vAliquota.add(oAliquota);

                ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();

                oAutomacao.codigoBarras = codigoBarras;
                oAutomacao.qtdEmbalagem = qtdEmbalagem;
                oAutomacao.idTipoEmbalagem = idTipoEmbalagem;

                oProduto.vAutomacao.add(oAutomacao);

                CodigoAnteriorVO oCodigoAnterior = new CodigoAnteriorVO();

                oCodigoAnterior.codigoanterior = idProduto;
                
                if ((rst.getString("EAN") != null)
                        && (!rst.getString("EAN").trim().isEmpty())) {
                    oCodigoAnterior.barras = Long.parseLong(Utils.formataNumero(rst.getString("EAN").trim()));
                } else {
                    oCodigoAnterior.barras = -1;
                }
                
                if ((rst.getString("PIS_CST_S") != null) &&
                 (!rst.getString("PIS_CST_S").trim().isEmpty())) {
                    oCodigoAnterior.piscofinsdebito = Integer.parseInt(rst.getString("PIS_CST_S").trim());
                } else {
                    oCodigoAnterior.piscofinsdebito = -1;
                }
                
                if ((rst.getString("PIS_CST_E") != null) &&
                  (!rst.getString("PIS_CST_E").trim().isEmpty())) {                
                     oCodigoAnterior.piscofinscredito = Integer.parseInt(rst.getString("PIS_CST_E").trim());
                }else{
                     oCodigoAnterior.piscofinscredito = -1;
                }

                if ((rst.getString("COD_NATUREZA_RECEITA") != null) &&
                 (!rst.getString("COD_NATUREZA_RECEITA").trim().isEmpty())) {                    
                    oCodigoAnterior.naturezareceita = Integer.parseInt(rst.getString("COD_NATUREZA_RECEITA").trim());                    
                } else {
                    oCodigoAnterior.naturezareceita = -1;
                }                
                
                if ((rst.getString("ICMS") != null)
                        && (!rst.getString("ICMS").trim().isEmpty())) {
                    oCodigoAnterior.ref_icmsdebito = rst.getString("ICMS").trim();
                } else {
                    oCodigoAnterior.ref_icmsdebito = "";
                }
                
                oCodigoAnterior.estoque = -1;
                oCodigoAnterior.e_balanca = eBalanca;
                oCodigoAnterior.codigobalanca = codigoBalanca;
                oCodigoAnterior.custosemimposto = -1;
                oCodigoAnterior.custocomimposto = -1;
                oCodigoAnterior.margem = -1;
                oCodigoAnterior.precovenda = -1;
                oCodigoAnterior.referencia = -1;
                
                if ((rst.getString("NCM") != null)
                        && (!rst.getString("NCM").trim().isEmpty())) {
                    ncmAtual = util.formataNumero(rst.getString("NCM"));
                    oCodigoAnterior.ncm = ncmAtual;
                } else {
                    oCodigoAnterior.ncm = "";
                }
                oProduto.vCodigoAnterior.add(oCodigoAnterior);

                vProduto.put(idProduto, oProduto);
            }

            stmPostgres.close();
            return vProduto;

        } catch (Exception ex) {
            throw ex;
        }
    }
    
    public List<ClientePreferencialVO> carregarCliente(int idLoja, int idLojaCliente) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        Utils util = new Utils();
        List<ClientePreferencialVO> vClientePreferencial = new ArrayList<>();
        String nome, endereco, bairro, telefone, inscricaoestadual, email,
                dataResidencia, dataCadastro, numero, dataAniversario, nomePai, nomeMae, conjuge, 
                complemento, ddd, obs = "";
        int id_municipio = 0, id_estado, id_sexo, id_tipoinscricao = 0, id, agente, id_situacaocadastro, Linha = 0;
        long cnpj, cep, idLong;
        double limite, renda;
        

        try {
            stm = ConexaoFirebird.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("SELECT A.ID codigo, A.NOME, A.FANTASIA, A.TIPO, A.DOC, c.limite, c.renda, ");
            sql.append("en.cep, en.logradouro, en.numero, en.complemento, en.bairro, cid.cep ceppadrao, ");
            sql.append("cid.cidade, cid.uf, tel.ddd, tel.telefone, tel.contato, c.situacao, ");
            sql.append("tpcl.id tipo_cliente,  tpcl.descricao, em.email, c.cadastro, cidibge.id, ");
            sql.append("dcIE.doc as inscEst,  dcRG.doc as RG ");
            sql.append("FROM AGENTES A ");
            sql.append("inner join clientes c on c.id = a.id ");
            sql.append("left join enderecos en on en.agente = a.id ");
            sql.append("left join cidades cid on cid.id = en.cidade ");
            sql.append("left join telefones tel on tel.agente = a.id ");
            sql.append("inner join tiposclientes tpcl on tpcl.id = c.tipocliente ");
            sql.append("left join emails em on em.agente = a.id ");
            sql.append("left join cidadesibge cidibge on cidibge.id2 = cid.id ");
            sql.append("left join docs dcIE on dcIE.codag = a.id and dcIE.tipo = 66 ");
            sql.append("left join docs dcRG on dcRG.codag = a.id and dcRG.tipo = 67 ");            
            sql.append("order by a.id ");

            rst = stm.executeQuery(sql.toString());
            Linha = 1;
            try {
                while (rst.next()) {
                    
                    ClientePreferencialVO oClientePreferencial = new ClientePreferencialVO();

                    idLong = Long.parseLong(rst.getString("codigo").replace(".", "").trim());
                    
                    if ((rst.getString("situacao") != null) &&
                            (!rst.getString("situacao").trim().isEmpty())) {
                        
                        if ("1".equals(rst.getString("situacao").trim())) {
                            id_situacaocadastro = 1;
                        } else {
                            id_situacaocadastro = 0;
                        }
                    } else {
                        id_situacaocadastro = 0;
                    }
                    
                    if ((rst.getString("NOME") != null)
                            && (!rst.getString("NOME").isEmpty())) {
                        byte[] bytes = rst.getBytes("NOME");
                        String textoAcertado = new String(bytes, "ISO-8859-1");
                        nome = util.acertarTexto(textoAcertado.replace("'", "").trim());
                    } else {
                        nome = "SEM NOME VR " + idLong;
                    }
                    
                    if ((rst.getString("logradouro") != null)
                            && (!rst.getString("logradouro").isEmpty())) {
                        endereco = util.acertarTexto(rst.getString("logradouro").replace("'", "").trim());
                    } else {
                        endereco = "";
                    }
                    if (endereco.length() > 50) {
                        endereco = endereco.substring(0, 50);
                    }

                    if (rst.getString("bairro") != null) {
                        bairro = util.acertarTexto(rst.getString("bairro").replace("'", "").trim());
                    } else {
                        bairro = "";
                    }
                    
                    if ((rst.getString("numero") != null) &&
                            (!rst.getString("numero").trim().isEmpty())) {
                        numero = Utils.acertarTexto(rst.getString("numero").trim().replace("'", ""));
                    } else {
                        numero = "0";
                    }

                    if ((rst.getString("complemento") != null) &&
                            (!rst.getString("complemento").trim().isEmpty())) {
                        complemento = Utils.acertarTexto(rst.getString("complemento").trim().replace("'", ""));
                    } else {
                        complemento = "";
                    }
                    
                    if ((rst.getString("cidade") != null) && 
                            (rst.getString("UF") != null) &&
                            (rst.getString("cidade").trim().isEmpty()) &&
                            (rst.getString("UF").trim().isEmpty())) {
                        
                        id_estado = util.retornarEstadoDescricao(rst.getString("UF"));
                        
                        if (id_estado == 0) {
                            id_estado = 31; // ESTADO ESTADO DO CLIENTE
                        }
                        
                        id_municipio = util.retornarMunicipioIBGEDescricao(
                                rst.getString("CIDADE").toString(), rst.getString("UF").toString());
                        
                        if (id_municipio == 0) {
                            id_municipio = 3107604;// CIDADE DO CLIENTE;
                        }
                        
                    } else {
                        id_estado = 31; // ESTADO ESTADO DO CLIENTE
                        id_municipio = 3107604; // CIDADE DO CLIENTE;                   
                    }

                    if (rst.getString("CEP") != null) {
                        cep = Long.parseLong(util.formataNumero(util.acertarTexto(rst.getString("CEP").replace("'", ""))));
                    } else {
                        
                        if ((rst.getString("ceppadrao") != null) &&
                                (!rst.getString("ceppadrao").trim().isEmpty())) {
                            cep = Long.parseLong(util.formataNumero(util.acertarTexto(rst.getString("ceppadrao").replace("'", ""))));
                        } else {
                            
                            cep = Long.parseLong("0");
                        }
                    }

                    if ((rst.getString("DDD") != null) &&
                            (!rst.getString("DDD").trim().isEmpty())) {
                        ddd = Utils.acertarTexto(rst.getString("DDD").trim().replace("'", ""));
                    } else {
                        ddd = "";
                    }
                    
                    if ((rst.getString("TELEFONE") != null) &&
                            (!rst.getString("TELEFONE").trim().isEmpty())) {
                        telefone = ddd + util.formataNumero(rst.getString("TELEFONE"));
                    } else {
                        telefone = "";
                    }

                    if (rst.getString("NUMERO") != null) {
                        numero = util.acertarTexto(rst.getString("NUMERO"));
                        if (numero.length() > 6) {
                            numero = numero.substring(0, 6);
                        }
                    } else {
                        numero = "";
                    }

                    if (rst.getString("EMAIL") != null) {
                        email = util.acertarTexto(rst.getString("EMAIL"));
                        if (email.length() > 50) {
                            email = email.substring(0, 50);
                        }
                    } else {
                        email = "";
                    }

                    if ((rst.getString("DOC") != null)
                            && (!rst.getString("DOC").trim().isEmpty())) {
                        
                        cnpj = Long.parseLong(util.formataNumero(util.acertarTexto(rst.getString("DOC").trim())));
                        
                    } else {
                                                
                        cnpj = -1;
                    }

                    id_sexo = 1;

                    if ((rst.getString("cadastro") != null)
                            && (!rst.getString("cadastro").isEmpty())) {
                        dataCadastro = rst.getString("cadastro").replace(".", "/");
                    } else {
                        dataCadastro = "";
                    }
                                        
                    dataAniversario = null;

                    nomePai = "";

                    nomeMae = "";

                    conjuge = "";

                    dataResidencia = "1990/01/01";

                    if ((rst.getString("TIPO") != null) &&
                            (!rst.getString("TIPO").trim().isEmpty())) {
                        
                        if ("F".equals(rst.getString("TIPO").trim())) {
                            id_tipoinscricao = 1;
                        } else {
                            id_tipoinscricao = 0;
                        }
                    } else {
                        id_tipoinscricao = 1;
                    }
                    
                    if (id_tipoinscricao == 0) {

                        if ((rst.getString("inscEst") != null)
                                && (!rst.getString("inscEst").isEmpty())) {
                            inscricaoestadual = util.acertarTexto(rst.getString("inscEst").replace("'", "").trim());
                        } else {
                            inscricaoestadual = "ISENTO";
                        }
                        
                    } else {
                        if ((rst.getString("RG") != null)
                                && (!rst.getString("RG").isEmpty())) {
                            inscricaoestadual = util.acertarTexto(rst.getString("RG").replace("'", "").trim());
                        } else {
                            inscricaoestadual = "ISENTO";
                        }                        
                    }
                    
                    if ((rst.getString("RENDA") != null) &&
                            (!rst.getString("RENDA").trim().isEmpty())) {

                        renda = Double.parseDouble(rst.getString("RENDA").trim());
                        
                    } else {
                        renda = 0;
                    }
                    
                    if ((rst.getString("LIMITE") != null) &&
                            (!rst.getString("LIMITE").trim().isEmpty())) {
                        limite = Double.parseDouble(rst.getString("LIMITE").trim());
                        
                        if (limite > 99999999999.0) {
                            limite = 0;
                            obs = "CLIENTE "+nome+", LIMITE: "+rst.getString("LIMITE")+", VR NÃO SUPORTE ESSE VALOR. VERIFICAR.";
                        } else {
                            limite = limite;
                        }
                        
                    } else {
                        limite = 0;
                    }

                    if (nome.length() > 40) {
                        nome = nome.substring(0, 40);
                    }

                    if (endereco.length() > 50) {
                        endereco = endereco.substring(0, 50);
                    }
                    
                    if (String.valueOf(cep).length() > 8) {
                        cep = Long.parseLong(String.valueOf(cep).substring(0, 8).trim());
                    }

                    oClientePreferencial.idLong = idLong;
                    oClientePreferencial.nome = nome;
                    oClientePreferencial.endereco = endereco;
                    oClientePreferencial.bairro = bairro;
                    oClientePreferencial.numero = numero;
                    oClientePreferencial.id_estado = id_estado;
                    oClientePreferencial.id_municipio = id_municipio;
                    oClientePreferencial.complemento = complemento;
                    oClientePreferencial.id_tipoinscricao = id_tipoinscricao;
                    oClientePreferencial.cep = cep;
                    oClientePreferencial.nomepai = nomePai;
                    oClientePreferencial.nomemae = nomeMae;
                    oClientePreferencial.nomeconjuge = conjuge;
                    oClientePreferencial.telefone = telefone;
                    oClientePreferencial.telefone2 = "";
                    oClientePreferencial.inscricaoestadual = inscricaoestadual;
                    oClientePreferencial.cnpj = cnpj;
                    oClientePreferencial.sexo = id_sexo;
                    oClientePreferencial.dataresidencia = dataResidencia;
                    oClientePreferencial.datanascimento = dataAniversario;
                    oClientePreferencial.datacadastro = dataCadastro;
                    oClientePreferencial.email = email;
                    oClientePreferencial.valorlimite = limite;
                    oClientePreferencial.salario = renda;
                    oClientePreferencial.codigoanterior = idLong;
                    oClientePreferencial.observacao2 = obs;
                    vClientePreferencial.add(oClientePreferencial);
                }
                stm.close();
            } catch (Exception ex) {
                if (Linha > 0) {
                    throw new VRException("Linha " + Linha + ": " + ex.getMessage());
                } else {
                    throw ex;
                }
            }
            return vClientePreferencial;
        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }

    public List<FornecedorVO> carregarFornecedorIntelliCash() throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        Utils util = new Utils();
        List<FornecedorVO> vFornecedor = new ArrayList<>();

        String razaosocial, nomefantasia, obs = "", inscricaoestadual, endereco,
                bairro, datacadastro, numero, telefone, telefone2 = "", email, 
                complemento, ddd;
        int id, id_municipio = 0, id_estado, id_tipoinscricao, Linha = 0;
        long cnpj, cep, idLong;
        double pedidoMin;
        boolean ativo = true;

        try {
            stm = ConexaoFirebird.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("SELECT A.ID codigo, A.NOME, A.FANTASIA, A.TIPO, A.DOC, ");
            sql.append("en.cep, en.logradouro, en.numero, en.complemento, en.bairro, ");
            sql.append("cid.cidade, cid.uf, tel.ddd, tel.telefone, tel.contato, ");
            sql.append("em.email, cidibge.id, cid.cep ceppadrao, dcIE.doc as inscEst,  dcRG.doc as RG ");
            sql.append("FROM AGENTES A ");
            sql.append("inner join forns f on f.id = a.id ");
            sql.append("left join enderecos en on en.agente = a.id ");
            sql.append("left join cidades cid on cid.id = en.cidade ");
            sql.append("left join telefones tel on tel.agente = a.id ");
            sql.append("left join emails em on em.agente = a.id ");
            sql.append("left join cidadesibge cidibge on cidibge.id2 = cid.id ");
            sql.append("left join docs dcIE on dcIE.codag = a.id and dcIE.tipo = 66 ");
            sql.append("left join docs dcRG on dcRG.codag = a.id and dcRG.tipo = 67 ");
            sql.append("order by a.NOME ");

            rst = stm.executeQuery(sql.toString());
            Linha = 0;
            try {
                while (rst.next()) {
                    FornecedorVO oFornecedor = new FornecedorVO();

                    idLong = Long.parseLong(rst.getString("codigo").trim().replace(".", ""));

                    Linha++;

                    if ((rst.getString("NOME") != null)
                            && (!rst.getString("NOME").isEmpty())) {
                        byte[] bytes = rst.getBytes("NOME");
                        String textoAcertado = new String(bytes, "ISO-8859-1");
                        razaosocial = util.acertarTexto(textoAcertado.replace("'", "").trim());
                    } else {
                        razaosocial = "RAZAO SOCIAL VR";
                    }

                    if ((rst.getString("FANTASIA") != null)
                            && (!rst.getString("FANTASIA").isEmpty())) {
                        byte[] bytes = rst.getBytes("FANTASIA");
                        String textoAcertado = new String(bytes, "ISO-8859-1");
                        nomefantasia = util.acertarTexto(textoAcertado.replace("'", "").trim());
                    } else {
                        nomefantasia = "NOME FANTASIA VR";
                    }

                    if ((rst.getString("TIPO") != null) &&
                            (!rst.getString("TIPO").trim().isEmpty())) {
                    
                        if ("J".equals(rst.getString("TIPO").trim())) {
                            id_tipoinscricao = 0;
                        } else {
                            id_tipoinscricao = 1;
                        }                    
                    } else {
                        id_tipoinscricao = 0;
                    }

                    if ((rst.getString("DOC") != null)
                            && (!rst.getString("DOC").isEmpty())) {
                        cnpj = Long.parseLong(util.formataNumero(rst.getString("DOC").trim()));
                    } else {
                        cnpj = -1;
                    }

                    
                    if (id_tipoinscricao == 0) {

                        if ((rst.getString("inscEst") != null)
                                && (!rst.getString("inscEst").isEmpty())) {
                            inscricaoestadual = util.acertarTexto(rst.getString("inscEst").replace("'", "").trim());
                        } else {
                            inscricaoestadual = "ISENTO";
                        }
                        
                    } else {
                        if ((rst.getString("RG") != null)
                                && (!rst.getString("RG").isEmpty())) {
                            inscricaoestadual = util.acertarTexto(rst.getString("RG").replace("'", "").trim());
                        } else {
                            inscricaoestadual = "ISENTO";
                        }                        
                    }

                    if ((rst.getString("logradouro") != null)
                            && (!rst.getString("logradouro").isEmpty())) {
                        endereco = util.acertarTexto(rst.getString("logradouro").replace("'", "").trim());
                    } else {
                        endereco = "";
                    }

                    if ((rst.getString("bairro") != null)
                            && (!rst.getString("bairro").isEmpty())) {
                        bairro = util.acertarTexto(rst.getString("bairro").replace("'", "").trim());
                    } else {
                        bairro = "";
                    }

                    if ((rst.getString("cep") != null)
                            && (!rst.getString("cep").isEmpty())) {
                        cep = Long.parseLong(util.formataNumero(rst.getString("cep").trim()));
                    } else {
                        
                        if ((rst.getString("ceppadrao") != null) &&
                                (!rst.getString("ceppadrao").trim().isEmpty())) {
                            cep = Long.parseLong(util.formataNumero(rst.getString("ceppadrao").trim()));
                        } else {
                            cep = Long.parseLong("0");
                        }
                    }

                    if ((rst.getString("NUMERO") != null)
                            && (!rst.getString("NUMERO").isEmpty())) {
                        numero = rst.getString("NUMERO").trim();
                        if (numero.length() > 6) {
                            numero = numero.substring(0, 6);
                        }
                    } else {
                        numero = "0";
                    }

                    if ((rst.getString("cidade") != null)
                            && (!rst.getString("cidade").isEmpty())) {

                        if ((rst.getString("uf") != null)
                                && (!rst.getString("uf").isEmpty())) {

                            id_municipio = util.retornarMunicipioIBGEDescricao(util.acertarTexto(rst.getString("cidade").replace("'", "").trim()),
                                    util.acertarTexto(rst.getString("uf").replace("'", "").trim()));

                            if (id_municipio == 0) {
                                id_municipio = 3107604;
                            }
                        }
                    } else {
                        id_municipio = 3107604;
                    }

                    if ((rst.getString("uf") != null)
                            && (!rst.getString("uf").isEmpty())) {
                        id_estado = util.retornarEstadoDescricao(util.acertarTexto(rst.getString("uf").replace("'", "").trim()));

                        if (id_estado == 0) {
                            id_estado = 31;
                        }
                    } else {
                        id_estado = 31;
                    }

                    if ((rst.getString("DDD") != null) &&
                            (!rst.getString("DDD").trim().isEmpty())) {
                        ddd = Utils.acertarTexto(rst.getString("DDD").trim());
                    } else {
                        ddd = "";
                    }
                    
                    if (rst.getString("TELEFONE") != null) {
                        telefone = ddd + Utils.formataNumero(rst.getString("TELEFONE").trim());
                    } else {
                        telefone = "";
                    }
                    
                    if ((rst.getString("complemento") != null) &&
                            (!rst.getString("complemento").trim().isEmpty())) {
                        
                        complemento = Utils.acertarTexto(rst.getString("complemento").replace("'", "").trim());
                    } else {
                        complemento = "";
                    }
                    
                    /*if (rst.getString("FAX") != null) {
                        telefone2 = rst.getString("FAX").trim();
                    } else {
                        telefone2 = "";
                    }*/

                    if ((rst.getString("EMAIL") != null) &&
                            (!rst.getString("EMAIL").trim().isEmpty())) {
                    
                        if ((rst.getString("EMAIL").contains("@"))
                                || (rst.getString("EMAIL").contains("www"))) {
                            
                            email = Utils.acertarTexto(rst.getString("EMAIL").trim().replace("'", ""));
                            
                        } else {
                            
                            email = "";
                        }
                    } else {
                        email = "";
                    }

                    /*if (rst.getString("OBS") != null) {
                        obs = rst.getString("OBS").trim();
                    } else {
                        obs = "";
                    }*/

                    /*if (rst.getString("DTCADASTRO") != null) {
                        datacadastro = rst.getString("DTCADASTRO");
                    } else {*/
                        datacadastro = "";
                    //}

                    /*if (rst.getString("ATIVO") != null) {
                        if ("S".equals(rst.getString("ATIVO").trim())) {
                            ativo = true;
                        } else {
                            ativo = false;
                        }
                    } else {
                        ativo = true;
                    }*/

                    ativo = true;                        
                        
                    if (razaosocial.length() > 40) {
                        razaosocial = razaosocial.substring(0, 40);
                    }

                    if (nomefantasia.length() > 30) {
                        nomefantasia = nomefantasia.substring(0, 30);
                    }

                    if (endereco.length() > 40) {
                        endereco = endereco.substring(0, 40);
                    }

                    if (bairro.length() > 30) {
                        bairro = bairro.substring(0, 30);
                    }

                    if (String.valueOf(cep).length() > 8) {
                        cep = Long.parseLong(String.valueOf(cep).substring(0, 8));
                    }

                    if (String.valueOf(cnpj).length() > 14) {
                        cnpj = Long.parseLong(String.valueOf(cnpj).substring(0, 14));
                    }

                    if (inscricaoestadual.length() > 20) {
                        inscricaoestadual = inscricaoestadual.substring(0, 20);
                    }

                    oFornecedor.codigoanterior = idLong;
                    oFornecedor.razaosocial = razaosocial;
                    oFornecedor.nomefantasia = nomefantasia;
                    oFornecedor.endereco = endereco;
                    oFornecedor.numero = numero;
                    oFornecedor.bairro = bairro;
                    oFornecedor.complemento = complemento;
                    oFornecedor.telefone = telefone;
                    oFornecedor.telefone2 = telefone2;
                    oFornecedor.email = email;
                    oFornecedor.id_municipio = id_municipio;
                    oFornecedor.cep = cep;
                    oFornecedor.id_estado = id_estado;
                    oFornecedor.id_tipoinscricao = id_tipoinscricao;
                    oFornecedor.inscricaoestadual = inscricaoestadual;
                    oFornecedor.cnpj = cnpj;
                    oFornecedor.id_situacaocadastro = (ativo == true ? 1 : 0);
                    oFornecedor.observacao = obs;

                    vFornecedor.add(oFornecedor);
                }
            } catch (Exception ex) {
                /*if (Linha > 0) {
                    throw new VRException("Linha " + Linha + ": " + ex.getMessage());
                } else {*/
                    throw ex;
                //}
            }

            return vFornecedor;

        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }

    public List<ProdutoFornecedorVO> carregarProdutoFornecedorIntelliCash() throws Exception {

        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        Utils util = new Utils();
        List<ProdutoFornecedorVO> vProdutoFornecedor = new ArrayList<>();
        long idFornecedor, idProduto;
        String codigoExterno;
        java.sql.Date dataAlteracao = new Date(new java.util.Date().getTime());

        try {

            stm = ConexaoFirebird.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("select prod, forn, codigo as codigo_externo ");
            sql.append("from fornxcodprod ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                idFornecedor = Long.parseLong(rst.getString("forn").trim());
                idProduto = Long.parseLong(rst.getString("prod").trim());

                if ((rst.getString("codigo_externo") != null)
                        && (!rst.getString("codigo_externo").isEmpty())) {
                    codigoExterno = util.acertarTexto(rst.getString("codigo_externo").replace("'", "").trim());
                } else {
                    codigoExterno = "";
                }

                ProdutoFornecedorVO oProdutoFornecedor = new ProdutoFornecedorVO();

                oProdutoFornecedor.idFornecedorLong = idFornecedor;
                oProdutoFornecedor.idProdutoLong = idProduto;
                oProdutoFornecedor.dataalteracao = dataAlteracao;
                oProdutoFornecedor.codigoexterno = codigoExterno;

                vProdutoFornecedor.add(oProdutoFornecedor);
            }

            return vProdutoFornecedor;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public Map<Integer, ProdutoVO> carregarCustoProdutoIntelliCash(int idLoja, int idLojaCliente) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst;
        Map<Integer, ProdutoVO> vProduto = new HashMap<>();
        int idProduto;
        double custo = 0;

        try {

            stm = ConexaoFirebird.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("SELECT P.ID, p.custo ");
            sql.append("FROM PRODUTOS P      ");      

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                idProduto = Integer.parseInt(rst.getString("ID").replace(".", ""));
                custo = Double.parseDouble(rst.getString("CUSTO").replace(",", "."));

                ProdutoVO oProduto = new ProdutoVO();
                oProduto.id = idProduto;

                ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();

                oComplemento.idLoja = idLoja;
                oComplemento.custoComImposto = custo;
                oComplemento.custoSemImposto = custo;

                oProduto.vComplemento.add(oComplemento);

                CodigoAnteriorVO oCodigoAnterior = new CodigoAnteriorVO();

                oCodigoAnterior.custocomimposto = custo;
                oCodigoAnterior.custosemimposto = custo;
                oCodigoAnterior.id_loja = idLoja;

                oProduto.vCodigoAnterior.add(oCodigoAnterior);

                vProduto.put(idProduto, oProduto);
            }

            return vProduto;

        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }

    public List<ReceberChequeVO> carregarReceberChequeIntelliCash(int id_loja, int id_lojaCliente) throws Exception {

        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null, rst2 = null;
        Utils util = new Utils();
        List<ReceberChequeVO> vReceberCheque = new ArrayList<>();

        int numerocupom, idBanco, cheque, idTipoInscricao, id_tipoalinea;
        double valor, juros;
        long cpfCnpj;
        String observacao = "", dataemissao = "", datavencimento = "",
                agencia, conta, nome, rg, telefone;

        try {

            stm = ConexaoFirebird.getConexao().createStatement();

            sql = new StringBuilder();

            sql.append("SELECT CHQ.CPF_CGC, CHQ.SUP029 AS BANCO, CHQ.AGENCIA, CHQ.CONTA, ");
            sql.append("       CHQ.NUM_CHEQUE, CHQ.DATA_EMISSAO, CHQ.DATA_VENCIMENTO, CHQ.OBS, ");
            sql.append("       CHQ.VALOR, CHQ.NOME ");
            sql.append("FROM SUP034 CHQ ");
            sql.append("WHERE CHQ.DATA_BAIXA IS NULL ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                ReceberChequeVO oReceberCheque = new ReceberChequeVO();

                cpfCnpj = Long.parseLong(rst.getString("CPF_CGC").trim());

                if (String.valueOf(cpfCnpj).length() > 11) {
                    idTipoInscricao = 0;
                } else {
                    idTipoInscricao = 1;
                }

                idBanco = util.retornarBanco(Integer.parseInt(rst.getString("BANCO").trim()));

                if ((rst.getString("AGENCIA") != null)
                        && (!rst.getString("AGENCIA").trim().isEmpty())) {
                    agencia = util.acertarTexto(rst.getString("AGENCIA").trim().replace("'", ""));
                } else {
                    agencia = "";
                }

                if ((rst.getString("CONTA") != null)
                        && (!rst.getString("CONTA").trim().isEmpty())) {
                    conta = util.acertarTexto(rst.getString("CONTA").trim().replace("'", ""));
                } else {
                    conta = "";
                }

                if ((rst.getString("NUM_CHEQUE") != null)
                        && (!rst.getString("NUM_CHEQUE").trim().isEmpty())) {

                    cheque = Integer.parseInt(util.formataNumero(rst.getString("NUM_CHEQUE")));

                    if (String.valueOf(cheque).length() > 10) {
                        cheque = Integer.parseInt(String.valueOf(cheque).substring(0, 10));
                    }
                } else {
                    cheque = 0;
                }

                if ((rst.getString("DATA_EMISSAO") != null)
                        && (!rst.getString("DATA_EMISSAO").trim().isEmpty())) {
                    dataemissao = rst.getString("DATA_EMISSAO").trim();
                } else {
                    dataemissao = "2016/02/01";
                }

                if ((rst.getString("DATA_VENCIMENTO") != null)
                        && (!rst.getString("DATA_VENCIMENTO").trim().isEmpty())) {

                    datavencimento = rst.getString("DATA_VENCIMENTO").trim();
                } else {
                    datavencimento = "2016/02/12";
                }

                if ((rst.getString("NOME") != null)
                        && (!rst.getString("NOME").isEmpty())) {
                    nome = util.acertarTexto(rst.getString("NOME").replace("'", "").trim());
                } else {
                    nome = "IMPORTADO VR";
                }

                rg = "";

                valor = Double.parseDouble(rst.getString("VALOR"));
                numerocupom = 0;
                juros = 0;

                /*if ((rst.getString("chrobserv1") != null)
                 && (!rst.getString("chrobserv1").isEmpty())) {
                 observacao = util.acertarTexto(rst.getString("chrobserv1").replace("'", "").trim());
                 } else {*/
                observacao = "IMPORTADO VR";
                //}

                /*if ((rst.getString("chrtelefone") != null) &&
                 (!rst.getString("chrtelefone").isEmpty()) &&
                 (!"0".equals(rst.getString("chrtelefone").trim()))) {
                 telefone = util.formataNumero(rst.getString("chrtelefone"));
                 } else {*/
                telefone = "";
                //}

                //if (rst.getInt("status")==1){
                id_tipoalinea = 0;
                /*} else if (rst.getInt("status")==2){
                 id_tipoalinea = 15;                    
                 } else {
                 id_tipoalinea = 0;
                 }*/

                oReceberCheque.id_loja = id_loja;
                oReceberCheque.id_tipoalinea = id_tipoalinea;
                oReceberCheque.data = dataemissao;
                oReceberCheque.datadeposito = datavencimento;
                oReceberCheque.cpf = cpfCnpj;
                oReceberCheque.numerocheque = cheque;
                oReceberCheque.id_banco = idBanco;
                oReceberCheque.agencia = agencia;
                oReceberCheque.conta = conta;
                oReceberCheque.numerocupom = numerocupom;
                oReceberCheque.valor = valor;
                oReceberCheque.observacao = observacao;
                oReceberCheque.rg = rg;
                oReceberCheque.telefone = telefone;
                oReceberCheque.nome = nome;
                oReceberCheque.id_tipoinscricao = idTipoInscricao;
                oReceberCheque.datadeposito = datavencimento;
                oReceberCheque.valorjuros = juros;
                oReceberCheque.valorinicial = valor;

                vReceberCheque.add(oReceberCheque);

            }

            return vReceberCheque;

        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }

    public List<ReceberCreditoRotativoVO> carregarReceberClienteIntelliCash(int id_loja, int id_lojaCliente) throws Exception {

        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        Utils util = new Utils();
        List<ReceberCreditoRotativoVO> vReceberCreditoRotativo = new ArrayList<>();

        int numerocupom = 0;
        long idCliente = 0;
        double valor, juros;
        String observacao, dataemissao, datavencimento, strNumeroCupom,
                strNumeroCupomAux = "";
        long cnpj;

        try {

            stm = ConexaoFirebird.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("select data, vencimento, codag, ");
            sql.append("descricao, doc, valor ");
            sql.append("from agendafin ");
            sql.append("inner join clientes c on c.id = codag ");
            sql.append("where empresa = " + id_lojaCliente+" ");
            sql.append("and pg is null ");
            
            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                ReceberCreditoRotativoVO oReceberCreditoRotativo = new ReceberCreditoRotativoVO();

                idCliente = Long.parseLong(Utils.formataNumero(rst.getString("codag").trim()));
                dataemissao = rst.getString("data").replace(".", "/");
                datavencimento = rst.getString("vencimento").replace(".", "/");
                
                if ((rst.getString("doc") != null) &&
                        (!rst.getString("doc").trim().isEmpty())) {
                    
                   strNumeroCupom = Utils.formataNumero(rst.getString("doc").trim());
                   strNumeroCupomAux = Utils.acertarTexto(rst.getString("doc").trim());
                   
                   if (strNumeroCupom.length() > 9) {
                       strNumeroCupom = strNumeroCupom.substring(0, 9);
                   }
                   
                   numerocupom = Integer.parseInt(strNumeroCupom);
                } else {
                    
                    numerocupom = 0;
                }
                
                valor = Double.parseDouble(rst.getString("valor"));
                
                juros = 0;

                if ((rst.getString("descricao") != null) &&
                        (!rst.getString("descricao").trim().isEmpty())) {
                    
                    observacao = "IMPORTACAO VR => " + Utils.acertarTexto(rst.getString("descricao").trim().replace("'", ""));
                    
                    if (!strNumeroCupomAux.trim().isEmpty()) {
                        observacao = observacao + " => NUMERO CUPOM " + strNumeroCupomAux;
                    }
                } else {
                    observacao = "IMPORTADO VR";
                }
                
                oReceberCreditoRotativo.id_loja = id_loja;
                oReceberCreditoRotativo.dataemissao = dataemissao;
                oReceberCreditoRotativo.numerocupom = numerocupom;
                oReceberCreditoRotativo.valor = valor;
                oReceberCreditoRotativo.observacao = observacao;
                oReceberCreditoRotativo.idClientePreferencialLong = idCliente;
                oReceberCreditoRotativo.datavencimento = datavencimento;
                oReceberCreditoRotativo.valorjuros = juros;

                vReceberCreditoRotativo.add(oReceberCreditoRotativo);

            }

            return vReceberCreditoRotativo;

        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }

    public Map<Integer, ProdutoVO> carregarPrecoProdutoIntelliCash(int idLoja, int id_lojaCliente) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst;
        Map<Integer, ProdutoVO> vProduto = new HashMap<>();
        int idProduto;
        double preco = 0, margem = 0;

        try {

            stm = ConexaoFirebird.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("SELECT P.ID, p.preco, mkp AS MARGEM ");
            sql.append("FROM PRODUTOS P            ");
            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                idProduto = Integer.parseInt(rst.getString("ID"));
                preco = rst.getDouble("PRECO");

                if ((rst.getString("MARGEM") != null)
                        && !rst.getString("MARGEM").trim().isEmpty()) {
                    margem = Double.parseDouble(rst.getString("MARGEM"));
                } else {
                    margem = 0;
                }

                ProdutoVO oProduto = new ProdutoVO();
                oProduto.id = idProduto;
                oProduto.margem = margem;

                ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();

                oComplemento.idLoja = idLoja;
                oComplemento.precoVenda = preco;
                oComplemento.precoDiaSeguinte = preco;

                oProduto.vComplemento.add(oComplemento);

                CodigoAnteriorVO oCodigoAnterior = new CodigoAnteriorVO();

                oCodigoAnterior.precovenda = preco;
                oCodigoAnterior.id_loja = idLoja;

                oProduto.vCodigoAnterior.add(oCodigoAnterior);

                vProduto.put(idProduto, oProduto);

            }

            return vProduto;

        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }

    public Map<Integer, ProdutoVO> carregarEstoqueProdutoIntelliCash(int idLoja, int id_lojaCliente) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst;
        Map<Integer, ProdutoVO> vProduto = new HashMap<>();
        int idProduto;
        double saldo = 0;

        try {
            stm = ConexaoFirebird.getConexao().createStatement();
            sql = new StringBuilder();
            sql.append("SELECT IDPROD, QTDE FROM ESTOQUE E WHERE EMPRESA = "+idLoja);            
            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                idProduto = Integer.parseInt(rst.getString("IDPROD"));
                
                if ((rst.getString("QTDE") != null) &&
                        (!rst.getString("QTDE").trim().isEmpty())) {
                    saldo = Double.parseDouble(rst.getString("QTDE").trim());
                } else {
                    saldo = 0;
                }
                

                ProdutoVO oProduto = new ProdutoVO();
                oProduto.id = idProduto;

                ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();

                oComplemento.idLoja = idLoja;
                oComplemento.estoque = saldo;
                oProduto.vComplemento.add(oComplemento);
                CodigoAnteriorVO oCodigoAnterior = new CodigoAnteriorVO();

                oCodigoAnterior.estoque = saldo;
                oCodigoAnterior.id_loja = idLoja;

                oProduto.vCodigoAnterior.add(oCodigoAnterior);

                vProduto.put(idProduto, oProduto);

            }

            return vProduto;

        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }

    public List<ProdutoVO> carregarCodigoBarras() throws SQLException {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst;
        List<ProdutoVO> vProduto = new ArrayList<>();
        double idProduto;
        long codigobarras;

        try {
            stm = ConexaoFirebird.getConexao().createStatement();
            sql = new StringBuilder();
            sql.append(" select produto, ean from eans ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                idProduto = Double.parseDouble(Utils.formataNumero(rst.getString("produto")));

                if ((rst.getString("ean") != null)
                        && (!rst.getString("ean").trim().isEmpty())) {
                    codigobarras = Long.parseLong(Utils.formataNumero(rst.getString("ean").replace(".", "")));
                } else {
                    codigobarras = -1;
                }

                if (String.valueOf(codigobarras).length() >= 7) {

                    ProdutoVO oProduto = new ProdutoVO();

                    oProduto.idDouble = idProduto;

                    ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();

                    oAutomacao.codigoBarras = codigobarras;

                    oProduto.vAutomacao.add(oAutomacao);

                    vProduto.add(oProduto);
                }

            }

            return vProduto;

        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }

    //IMPORTAÇÕES    
    public void importarFamiliaProdutoIntelliCash() throws Exception {

        try {

            ProgressBar.setStatus("Carregando dados...Familia Produto...");
            List<FamiliaProdutoVO> vFamiliaProduto = carregarFamiliaProdutoIntelliCash();

            new FamiliaProdutoDAO().salvar(vFamiliaProduto);

        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarMercadologicoIntelliCash() throws Exception {

        List<MercadologicoVO> vMercadologico = new ArrayList<>();

        try {

            ProgressBar.setStatus("Carregando dados...Mercadologico...");
            vMercadologico = carregarMercadologicoIntelliCash(1);
            new MercadologicoDAO().salvar(vMercadologico, true);

            vMercadologico = carregarMercadologicoIntelliCash(2);
            new MercadologicoDAO().salvar(vMercadologico, false);

            vMercadologico = carregarMercadologicoIntelliCash(3);
            new MercadologicoDAO().salvar(vMercadologico, false);

        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarFornecedorIntelliCash() throws Exception {

        try {

            ProgressBar.setStatus("Carregando dados...Fornecedor...");
            List<FornecedorVO> vFornecedor = carregarFornecedorIntelliCash();

            new FornecedorDAO().salvar(vFornecedor);

        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarProdutoIntelliCash(int id_loja) throws Exception {

        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();

        try {

            ProgressBar.setStatus("Carregando dados...Produtos...");
            Map<Double, ProdutoVO> vProdutoMilenio = carregarProdutoIntelliCash();

            List<LojaVO> vLoja = new LojaDAO().carregar();

            ProgressBar.setMaximum(vProdutoMilenio.size());

            for (Double keyId : vProdutoMilenio.keySet()) {

                ProdutoVO oProduto = vProdutoMilenio.get(keyId);

                oProduto.idProdutoVasilhame = -1;
                oProduto.excecao = -1;
                oProduto.idTipoMercadoria = -1;

                vProdutoNovo.add(oProduto);

                ProgressBar.next();
            }

            produto.implantacaoExterna = true;
            produto.usarMercadoligicoProduto = true;
            produto.salvar(vProdutoNovo, id_loja, vLoja);

        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarProdutoMaior6DigitosIntelliCash(int id_loja) throws Exception {

        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();

        try {

            ProgressBar.setStatus("Carregando dados...Produtos Maior 6 Digitos...");
            Map<Double, ProdutoVO> vProdutoMilenio = carregarProdutoMaior6DigitosIntelliCash();

            List<LojaVO> vLoja = new LojaDAO().carregar();

            ProgressBar.setMaximum(vProdutoMilenio.size());

            for (Double keyId : vProdutoMilenio.keySet()) {

                ProdutoVO oProduto = vProdutoMilenio.get(keyId);

                oProduto.idProdutoVasilhame = -1;
                oProduto.excecao = -1;
                oProduto.idTipoMercadoria = -1;

                vProdutoNovo.add(oProduto);

                ProgressBar.next();
            }

            produto.implantacaoExterna = true;
            produto.usarMercadoligicoProduto = true;
            produto.salvar(vProdutoNovo, id_loja, vLoja);

        } catch (Exception ex) {

            throw ex;
        }
    }
    
    public void importarCustoProdutoIntelliCash(int id_loja, int id_lojaCliente) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();

        try {

            ProgressBar.setStatus("Carregando dados...Produtos...Custo...");
            Map<Integer, ProdutoVO> vCustoProduto = carregarCustoProdutoIntelliCash(id_loja, id_lojaCliente);

            List<LojaVO> vLoja = new LojaDAO().carregar();

            ProgressBar.setMaximum(vCustoProduto.size());

            for (Integer keyId : vCustoProduto.keySet()) {

                ProdutoVO oProduto = vCustoProduto.get(keyId);

                vProdutoNovo.add(oProduto);

                ProgressBar.next();
            }

            produto.alterarCustoProduto(vProdutoNovo, id_loja);

        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarPrecoProdutoIntelliCash(int id_loja, int id_lojaCliente) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();

        try {

            ProgressBar.setStatus("Carregando dados...Produtos...Preço...");
            Map<Integer, ProdutoVO> vPrecoProduto = carregarPrecoProdutoIntelliCash(id_loja, id_lojaCliente);

            List<LojaVO> vLoja = new LojaDAO().carregar();

            ProgressBar.setMaximum(vPrecoProduto.size());

            for (Integer keyId : vPrecoProduto.keySet()) {

                ProdutoVO oProduto = vPrecoProduto.get(keyId);

                vProdutoNovo.add(oProduto);

                ProgressBar.next();
            }

            produto.alterarPrecoProduto(vProdutoNovo, id_loja);

        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarEstoqueProdutoIntelliCash(int id_loja, int id_lojaCliente) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();

        try {

            ProgressBar.setStatus("Carregando dados...Produtos...Estoque...");
            Map<Integer, ProdutoVO> vEstoqueProduto = carregarEstoqueProdutoIntelliCash(id_loja, id_lojaCliente);

            List<LojaVO> vLoja = new LojaDAO().carregar();

            ProgressBar.setMaximum(vEstoqueProduto.size());

            for (Integer keyId : vEstoqueProduto.keySet()) {

                ProdutoVO oProduto = vEstoqueProduto.get(keyId);

                vProdutoNovo.add(oProduto);

                ProgressBar.next();
            }

            produto.alterarEstoqueProduto(vProdutoNovo, id_loja);

        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarCodigoBarra() throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();

        try {

            ProgressBar.setStatus("Carregando dados...Produtos...CodigoBarra...");
            vProdutoNovo = carregarCodigoBarras();

            List<LojaVO> vLoja = new LojaDAO().carregar();

            ProgressBar.setMaximum(vProdutoNovo.size());

            /*for (Double keyId : vEstoqueProduto.keySet()) {

                ProdutoVO oProduto = vEstoqueProduto.get(keyId);

                vProdutoNovo.add(oProduto);

                ProgressBar.next();
            }*/

            produto.addCodigoBarras(vProdutoNovo);

        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarChequeReceberIntelliCash(int id_loja, int id_lojaCliente) throws Exception {

        try {

            ProgressBar.setStatus("Carregando dados...Cheque Receber...");
            List<ReceberChequeVO> vReceberCheque = carregarReceberChequeIntelliCash(id_loja, id_lojaCliente);

            new ReceberChequeDAO().salvar(vReceberCheque, id_loja);

        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarProdutoFornecedorIntelliCash() throws Exception {

        try {

            ProgressBar.setStatus("Carregando dados...Produto Fornecedor...");
            List<ProdutoFornecedorVO> vProdutoFornecedor = carregarProdutoFornecedorIntelliCash();

            new ProdutoFornecedorDAO().salvar(vProdutoFornecedor);

        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarReceberClienteIntelliCash(int idLoja, int idLojaCliente) throws Exception {

        try {

            ProgressBar.setStatus("Carregando dados...Receber Cliente...");
            List<ReceberCreditoRotativoVO> vReceberCliente = carregarReceberClienteIntelliCash(idLoja, idLojaCliente);

            new ReceberCreditoRotativoDAO().salvar(vReceberCliente, idLoja);

        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarProdutoBalanca(String arquivo, int opcao) throws Exception {

        try {

            ProgressBar.setStatus("Carregando dados...Produtos de Balanca...");
            List<ProdutoBalancaVO> vProdutoBalanca = new ProdutoBalancaDAO().carregar(arquivo, opcao);

            new ProdutoBalancaDAO().salvar(vProdutoBalanca);
        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarPisCofinsProdutoWisa() throws Exception {
        try {

            ProgressBar.setStatus("Carregando dados...Pis Cofins...Natureza Receita...");
            List<ProdutoVO> vProduto = carregarPisCofinsWisa();

            new ProdutoDAO().alterarPisCofinsProduto(vProduto);
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    public void importarDataCadastroProduto() throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        try {
            
            ProgressBar.setStatus("Carregando dados...Data Cadastro Produto...");
            vProduto = carregarDataCadastroProduto();
            
            if (!vProduto.isEmpty()) {
                new ProdutoDAO().altertarDataCadastroProdutoGdoor(vProduto);
            }
        } catch(Exception ex) {
            throw ex;
        }
    }
    
    public void importarMargemProduto() throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Margem Produtos...");
            vProduto = carregarMargemProduto();
            
            if (!vProduto.isEmpty()) {
                new ProdutoDAO().alterarMargemProduto(vProduto, 1);
            }
            
        } catch(Exception ex) {
            throw ex;
        }
    }

    // FUNÇÕES

    private int retornarAliquotaICMSIntelliCash(String codTrib, String descTrib) {

        int retorno;

        if ("F".equals(codTrib)) {
            retorno = 7;
        } else if (("I".equals(codTrib))||("N".equals(codTrib))) {
            retorno = 6;
        } else if ("T07".equals(codTrib)) {
            retorno = 0;
        } else if ("T12".equals(codTrib)) {
            retorno = 1;
        } else if ("T18".equals(codTrib)) {
            retorno = 2;
        } else if ("T25".equals(codTrib)) {
            retorno = 3;
        } else {
            retorno = 8;
        }

        return retorno;
    }

    public void corrigirClienteDuplicado() throws Exception {

        try {
            ProgressBar.setStatus("Corrigindo dados...Cliente Duplicados...");
            new ClientePreferencialDAO().corrigirClienteDuplicado();
        } catch (Exception ex) {

            throw ex;
        }
    }
}
