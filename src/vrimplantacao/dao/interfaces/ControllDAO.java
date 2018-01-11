package vrimplantacao.dao.interfaces;

import com.sun.tools.jdi.LinkedHashMap;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import vrframework.remote.ItemComboVO;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.ClientePreferencialVO;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.FornecedorContatoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Leandro
 */
public class ControllDAO extends AbstractIntefaceDao {
    
    private final InterfaceDAO produtoDao = new InterfaceDAO() {

        @Override
        public String getSistema() {
            return "CONTROLL";
        }

        @Override
        public List<MercadologicoIMP> getMercadologicos() throws Exception {
            List<MercadologicoIMP> result = new ArrayList<>();
            
            try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select\n" +
                        "    codsec, \n" +
                        "    descricao\n" +
                        "from\n" +
                        "    secao\n" +
                        "order by\n" +
                        "    codsec"
                )) {
                    while (rst.next()) {
                        MercadologicoIMP merc = new MercadologicoIMP();
                        merc.setImportSistema(getSistema());
                        merc.setImportLoja(getLojaOrigem());
                        merc.setMerc1ID(rst.getString("codsec"));
                        merc.setMerc1Descricao(rst.getString("descricao"));
                        result.add(merc);
                    }
                }
            }
            
            return result;
        }
        
        @Override
        public List<ProdutoIMP> getProdutos() throws Exception {
            List<ProdutoIMP> result = new ArrayList<>();
            
            try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
                Map<Integer, Set<String>> eans = new LinkedHashMap();
                try (ResultSet rst = stm.executeQuery(
                        "select\n" +
                        "    codpro id,\n" +
                        "    barra ean\n" +
                        "from\n" +
                        "    codbarra\n" +
                        "order by\n" +
                        "    id,\n" +
                        "    ean"
                )) {
                    while (rst.next()) {
                        Set<String> list = eans.get(rst.getInt("id"));
                        if (list == null) {
                            list = new LinkedHashSet<>();
                            eans.put(rst.getInt("id"), list);
                        }                        
                        list.add(rst.getString("ean"));
                    }
                }
                
                
                try (ResultSet rst = stm.executeQuery(
                        "select\n" +
                        "    p.codpro id,\n" +
                        "    case when p.inipro = '01.01.1900' then p.dataultimopreco else p.inipro end datacadastro,\n" +
                        "    p.embpro unidade,\n" +
                        "    1 qtdembalagem,\n" +
                        "    case upper(replace(replace(p.pespro,'ã','A'),'Ã','A')) when 'SIM' then 1 else 0 end ebalanca,\n" +
                        "    p.valpro validade,\n" +
                        "    p.despro descricaoCompleta,\n" +
                        "    p.apepro descricaoReduzida,\n" +
                        "    p.apepro descricaoGondola,\n" +
                        "    p.secpro mercadologico1,\n" +
                        "    p.pesobruto,\n" +
                        "    p.pesoliquido,\n" +
                        "    p.maxpro estoqueMaximo,\n" +
                        "    p.minpro estoqueMinimo,\n" +
                        "    p.salpro estoque,\n" +
                        "    p.marpro margem,\n" +
                        "    p.cuspro custosemimposto,\n" +
                        "    p.realpro custocomimposto,\n" +
                        "    p.prepro precovenda,\n" +
                        "    p.nbmpro ncm,\n" +
                        "    p.cest,\n" +
                        "    p.cstsaida piscofinsCstDebito,\n" +
                        "    p.cstentrada piscofinsCstCredito,\n" +
                        "    p.codigopis piscofinsNaturezaReceita,\n" +
                        "    coalesce(p.natpro, '') natpro,\n" +
                        "    nat.codimpressora icmsCst,\n" +
                        "    nat.aliquota icmsAliq,\n" +
                        "    p.quaemb qtdemp_compra,\n" +
                        "    p.tipemb embalagem_compra,\n" +
                        "    coalesce(p.balpro, 0) plu\n" +
                        "from\n" +
                        "    produto p\n" +
                        "    left join natureza nat on\n" +
                        "        p.natpro = nat.codfiscal and\n" +
                        "        p.codemp = nat.codemp\n" +
                        "order by\n" +
                        "    p.codpro"
                )) {
                    while (rst.next()) {
                        Set<String> list = eans.get(rst.getInt("id"));
                        if (list != null && !rst.getBoolean("eBalanca")) {
                            for (String ean: list) {
                                ProdutoIMP imp = gerarProdutoIMP(rst);
                                imp.setEan(ean);
                                result.add(imp);
                            }                        
                        } else {
                            ProdutoIMP imp = gerarProdutoIMP(rst);
                            result.add(imp);
                        }                        
                    }
                }
            }
            
            return result;
        }

        @Override
        public List<FornecedorIMP> getFornecedores() throws Exception {
            List<FornecedorIMP> result = new ArrayList<>();
            try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select\n" +
                        "    f.CODFOR id,\n" +
                        "    f.nomfor razaoSocial,\n" +
                        "    f.fantasia nomeFantasia,\n" +
                        "    f.cnpjfor cnpj,\n" +
                        "    f.insfor inscricaoEstadual,\n" +
                        "    f.insmunic inscricaoMunicipal,\n" +
                        "    coalesce(trim(f.tipoend) || ' ','') || trim(f.endfor) endereco,\n" +
                        "    f.numero numero,\n" +
                        "    f.baifor bairro,\n" +
                        "    f.cidfor cidade,\n" +
                        "    f.estfor estado,\n" +
                        "    f.cepfor cep,\n" +
                        "    f.telfor telefone,\n" +
                        "    coalesce(f.faxfor,'') fax,\n" +
                        "    coalesce(f.celfor,'') celular,\n" +
                        "    coalesce(f.sitefor,'') site,\n" +
                        "    coalesce(f.emailfor,'') email\n" +
                        "from\n" +
                        "    fornece f\n" +
                        "where\n" +
                        "    not f.nomfor is null\n" +
                        "order by\n" +
                        "    f.codfor"
                )) {
                    while (rst.next()) {
                        FornecedorIMP imp = new FornecedorIMP();
                        
                        imp.setImportSistema(getSistema());
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportId(rst.getString("id"));
                        imp.setRazao(rst.getString("razaoSocial"));
                        imp.setFantasia(rst.getString("nomeFantasia"));
                        imp.setCnpj_cpf(rst.getString("cnpj"));
                        imp.setIe_rg(rst.getString("inscricaoEstadual"));
                        imp.setInsc_municipal(rst.getString("inscricaoMunicipal"));
                        imp.setEndereco(rst.getString("endereco"));
                        imp.setNumero(rst.getString("numero"));
                        imp.setBairro(rst.getString("bairro"));
                        imp.setMunicipio(rst.getString("cidade"));
                        imp.setUf(rst.getString("estado"));
                        imp.setCep(rst.getString("cep"));
                        imp.setTel_principal(rst.getString("telefone"));
                        
                        FornecedorContatoIMP cont;
                        if (!"".equals(rst.getString("fax").trim())) {
                            cont = imp.getContatos().make("1");
                            cont.setImportId("1");
                            cont.setNome("FAX");
                            cont.setTelefone(rst.getString("fax").trim());
                            cont.setTipoContato(TipoContato.COMERCIAL);
                        }
                        if (!"".equals(rst.getString("celular").trim())) {
                            cont = imp.getContatos().make("2");
                            cont.setImportId("2");
                            cont.setNome("CELULAR");
                            cont.setTelefone(rst.getString("celular").trim());
                            cont.setTipoContato(TipoContato.COMERCIAL);
                        }
                        if (!"".equals(rst.getString("site").trim())) {
                            cont = imp.getContatos().make("3");
                            cont.setImportId("3");
                            cont.setNome("SITE");
                            cont.setEmail(rst.getString("site").trim());
                            cont.setTipoContato(TipoContato.COMERCIAL);
                        }
                        if (!"".equals(rst.getString("email").trim())) {
                            cont = imp.getContatos().make("4");
                            cont.setImportId("4");
                            cont.setNome("EMAIL");
                            cont.setEmail(rst.getString("email").trim());
                            cont.setTipoContato(TipoContato.COMERCIAL);
                        }
                        
                        result.add(imp);
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
                        "    cf.codfor idFornecedor,\n" +
                        "    cf.codpro idProduto,\n" +
                        "    cf.codigo codigoExterno\n" +
                        "from\n" +
                        "    codfor cf"
                )) {
                    while (rst.next()) {
                        ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                        
                        imp.setImportSistema(getSistema());
                        imp.setImportLoja(getLojaOrigem());
                        imp.setIdFornecedor(rst.getString("idFornecedor"));
                        imp.setIdProduto(rst.getString("idProduto"));
                        imp.setCodigoExterno(rst.getString("codigoExterno"));
                        
                        result.add(imp);
                    }
                }
            }
            return result;
        }
        
        
        

        private ProdutoIMP gerarProdutoIMP(final ResultSet rst) throws SQLException {
            ProdutoIMP imp = new ProdutoIMP();
            imp.setImportSistema(getSistema());
            imp.setImportLoja(getLojaOrigem());
            imp.setImportId(rst.getString("id"));
            imp.setDataCadastro(rst.getDate("datacadastro"));
            imp.setTipoEmbalagem(rst.getString("unidade"));
            imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
            imp.seteBalanca(rst.getBoolean("ebalanca"));
            if (imp.isBalanca()) {
                imp.setEan(rst.getString("plu"));
            }
            imp.setValidade(rst.getInt("validade"));
            imp.setDescricaoCompleta(rst.getString("descricaoCompleta"));
            imp.setDescricaoReduzida(rst.getString("descricaoReduzida"));
            imp.setDescricaoGondola(rst.getString("descricaoGondola"));
            imp.setCodMercadologico1(rst.getString("mercadologico1"));
            imp.setPesoBruto(rst.getDouble("pesobruto"));
            imp.setPesoLiquido(rst.getDouble("pesoliquido"));
            imp.setEstoqueMaximo(rst.getDouble("estoqueMaximo"));
            imp.setEstoqueMinimo(rst.getDouble("estoqueMinimo"));
            imp.setEstoque(rst.getDouble("estoque"));
            imp.setMargem(rst.getDouble("margem"));
            imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
            imp.setCustoComImposto(rst.getDouble("custocomimposto"));
            imp.setPrecovenda(rst.getDouble("precovenda"));
            imp.setNcm(rst.getString("ncm"));
            imp.setCest(rst.getString("cest"));
            imp.setPiscofinsCstDebito(rst.getInt("piscofinsCstDebito"));
            imp.setPiscofinsCstCredito(rst.getInt("piscofinsCstCredito"));
            imp.setPiscofinsNaturezaReceita(Utils.stringToInt(rst.getString("piscofinsNaturezaReceita")));
            int cst = 0;
            double aliq = 0;
            switch (rst.getString("natpro")) {
                case "07": {
                    cst = 0;
                    aliq = 7;
                }; break;
                case "12": {
                    cst = 0;
                    aliq = 12;
                }; break;
                case "18": {
                    cst = 0;
                    aliq = 18;
                }; break;
                case "25": {
                    cst = 0;
                    aliq = 25;
                }; break;
                case "I": {
                    cst = 40;
                    aliq = 0;
                }; break;
                case "N": {
                    cst = 41;
                    aliq = 0;
                }; break;
                default: {
                    cst = 60;
                    aliq = 0;
                }; break;
            }
            imp.setIcmsCst(cst);
            imp.setIcmsAliq(aliq);
            imp.setIcmsReducao(0);
            return imp;
        }
        
        
    
    };
    
    public List<ItemComboVO> getLojasCliente() throws Exception {
        List<ItemComboVO> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                "select\n" +
                "    codemp,\n" +
                "    nome\n" +
                "from\n" +
                "    empresa\n" +
                "order by\n" +
                "    codemp"
            )) {
                while (rst.next()) {
                    result.add(new ItemComboVO(rst.getInt("codemp"), rst.getString("nome")));
                }
            }
        }
        return result;
    }

    public InterfaceDAO getProdutoDao() {
        return produtoDao;
    }

    @Override
    public List<ClientePreferencialVO> carregarCliente(int idLojaCliente) throws Exception {
        List<ClientePreferencialVO> vClientePreferencial = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {                    
            try (ResultSet rst = stm.executeQuery(               
                "select\n" +
                "    c.codcli id,\n" +
                "    c.nomcli nome,\n" +
                "    trim(c.logcli) tipo_endereco,\n" +
                "    trim(c.endcli) res_endereco,\n" +
                "    c.numcli res_numero,\n" +
                "    null res_complemento,\n" +
                "    c.baicli res_bairro,\n" +
                "    c.cidcli res_cidade,\n" +
                "    c.estcli res_uf,\n" +
                "    c.cepcli res_cep,\n" +
                "    c.foncli fone1,\n" +
                "    null fone2,\n" +
                "    c.celcli celular,\n" +
                "    c.inscli inscricaoestadual,\n" +
                "    c.cgccli cnpj,\n" +
                "    case when upper(substring(trim(c.sexcli) from 1 for 1)) = 'F' then 0 else 1 end sexo,\n" +
                "    c.vencli prazodias,\n" +
                "    c.emailcli email,\n" +
                "    c.cadcli datacadastro,\n" +
                "    c.limcli limitepreferencial,\n" +
                "    case when upper(trim(c.blocli)) = 'SIM' then 1 else 0 end bloqueado,\n" +
                "    c.obscli observacao,\n" +
                "    c.paicli nomepai,\n" +
                "    c.maecli nomemae,\n" +
                "    null empresa,\n" +
                "    null telEmpresa,\n" +
                "    null cargo,\n" +
                "    c.salcli salario,\n" +
                "    c.nascli datanascimento,\n" +
                "    0 as estadocivil,\n" +
                "    null conjuge,\n" +
                "    null orgaoemissor\n" +
                "from\n" +
                "    cliente c\n" +
                "order by\n" +
                "    c.codcli"
            )) {
                while (rst.next()) {                    
                    ClientePreferencialVO oClientePreferencial = new ClientePreferencialVO();

                    oClientePreferencial.setId(rst.getInt("id"));
                    oClientePreferencial.setCodigoanterior(rst.getInt("id"));
                    oClientePreferencial.setNome(rst.getString("nome"));                    
                    
                    String endereco = rst.getString("res_endereco");
                    if (!"".equals(endereco)) {
                        endereco = rst.getString("tipo_endereco") + " " + endereco;
                    }
                    oClientePreferencial.setEndereco(endereco);
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
                    oClientePreferencial.setSexo(rst.getInt("sexo"));
                    oClientePreferencial.setVencimentocreditorotativo(rst.getInt("PRAZODIAS"));
                    oClientePreferencial.setEmail(rst.getString("email"));
                    oClientePreferencial.setDataresidencia("1990/01/01");
                    oClientePreferencial.setDatacadastro(rst.getDate("datacadastro"));
                    oClientePreferencial.setValorlimite(rst.getDouble("limitepreferencial"));
                    oClientePreferencial.setBloqueado(rst.getBoolean("bloqueado"));
                    oClientePreferencial.setId_situacaocadastro(1);
                    oClientePreferencial.setObservacao("IMPORTADO VR: " + rst.getString("observacao"));
                    oClientePreferencial.setDatanascimento(rst.getDate("datanascimento"));
                    oClientePreferencial.setNomepai(rst.getString("nomePai"));
                    oClientePreferencial.setNomemae(rst.getString("nomeMae"));
                    oClientePreferencial.setEmpresa(rst.getString("empresa"));
                    oClientePreferencial.setTelefoneempresa(rst.getString("telEmpresa"));
                    oClientePreferencial.setCargo(rst.getString("cargo"));
                    oClientePreferencial.setId_tipoinscricao(String.valueOf(oClientePreferencial.getCnpj()).length() > 11 ? 0 : 1);
                    oClientePreferencial.setSalario(rst.getDouble("salario"));
                    oClientePreferencial.setId_tipoestadocivil(rst.getInt("estadoCivil"));
                    oClientePreferencial.setNomeconjuge(rst.getString("conjuge"));
                    oClientePreferencial.setOrgaoemissor(rst.getString("ORGAOEMISSOR"));                   

                    vClientePreferencial.add(oClientePreferencial);
                }                
            }
        }
        
        return vClientePreferencial;
    }
    
}
