package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import vr.core.parametro.versao.Versao;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.ProdutoAnteriorDAO;
import vrimplantacao2.dao.cadastro.produto.ProdutoAutomacaoDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.utils.MathUtils;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.mercadologico.MercadologicoNivelIMP;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Leandro
 */
public class InventerDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "Inventer";
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select ChvEmp, NomeRazao, Cnpj from tbEmp order by 1"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("ChvEmp"), rst.getString("NomeRazao") + " - " + rst.getString("Cnpj")));
                }
            }
        }
        
        return result;
    }

    @Override
    public List<MercadologicoNivelIMP> getMercadologicoPorNivel() throws Exception {
        List<MercadologicoNivelIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            
            MultiMap<String, MercadologicoNivelIMP> nivel = new MultiMap<>();
            
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	ChvGrp1 id,\n" +
                    "	Descricao\n" +
                    "from\n" +
                    "	tbTab_Grp1\n" +
                    "where\n" +
                    "	ChvGrp1 != 1\n" +
                    "order by 1"
            )) {
                while (rst.next()) {
                    if (!nivel.containsKey(rst.getString("id"))) {
                        MercadologicoNivelIMP merc = new MercadologicoNivelIMP(
                                rst.getString("id"),
                                rst.getString("descricao")
                        );
                        nivel.put(merc, 
                                rst.getString("id"));
                        result.add(merc);
                    }
                }
            }
            
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	ChvGrp1 id,\n" +
                    "	ChvGrp2 id_pai1,\n" +
                    "	Descricao\n" +
                    "from\n" +
                    "	tbTab_Grp2\n" +
                    "where\n" +
                    "	ChvGrp2 != 1\n" +
                    "order by 1"
            )) {
                while (rst.next()) {
                    if (nivel.containsKey(rst.getString("id_pai1"))) {
                        MercadologicoNivelIMP pai = nivel.get(rst.getString("id_pai1"));
                        
                        nivel.put(
                                pai.addFilho(rst.getString("id"), rst.getString("descricao")),
                                pai.getId(),
                                rst.getString("id")
                        );
                    }                    
                }
            }
            
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	g2.ChvGrp1 id_pai1,\n" +
                    "	g3.ChvGrp2 id_pai2,\n" +
                    "	g3.ChvGrp3 id,\n" +
                    "	g3.Descricao\n" +
                    "from\n" +
                    "	tbTab_Grp3 g3\n" +
                    "	join tbTab_Grp2 g2 on\n" +
                    "		g3.ChvGrp2 = g2.ChvGrp2\n" +
                    "where\n" +
                    "	g3.ChvGrp3 != 1\n" +
                    "order by 1,2,3"
            )) {
                while (rst.next()) {
                    if (nivel.containsKey(rst.getString("id_pai1"), rst.getString("id_pai2"))) {
                        MercadologicoNivelIMP pai = nivel.get(rst.getString("id_pai1"), rst.getString("id_pai2"));
                        
                        nivel.put(
                                pai.addFilho(rst.getString("id"), rst.getString("descricao")),
                                pai.getMercadologicoPai().getId(),
                                pai.getId(),
                                rst.getString("id")
                        );
                    }                    
                }
            }
            
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	g2.ChvGrp1 id_pai1,\n" +
                    "	g3.ChvGrp2 id_pai2,\n" +
                    "	g4.ChvGrp3 id_pai3,\n" +
                    "	g4.ChvGrp4 id,\n" +
                    "	g4.Descricao\n" +
                    "from\n" +
                    "	tbTab_Grp4 g4\n" +
                    "	join tbTab_Grp3 g3 on\n" +
                    "		g4.ChvGrp3 = g3.ChvGrp3\n" +
                    "	join tbTab_Grp2 g2 on\n" +
                    "		g3.ChvGrp2 = g2.ChvGrp2\n" +
                    "where\n" +
                    "	g3.ChvGrp3 != 1\n" +
                    "order by 1,2,3,4"
            )) {
                while (rst.next()) {
                    if (nivel.containsKey(rst.getString("id_pai1"), rst.getString("id_pai2"), rst.getString("id_pai3"))) {
                        MercadologicoNivelIMP pai = nivel.get(rst.getString("id_pai1"), rst.getString("id_pai2"), rst.getString("id_pai3"));
                        
                        nivel.put(
                                pai.addFilho(rst.getString("id"), rst.getString("descricao")),
                                pai.getMercadologicoPai().getMercadologicoPai().getId(),
                                pai.getMercadologicoPai().getId(),
                                pai.getId(),
                                rst.getString("id")
                        );
                    }                    
                }
            }
                        
        }
        
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	p.ChvProd id,\n" +
                    "	p.DtCadastro datacadastro,\n" +
                    "	p.Barra ean,\n" +
                    "	p.QtdSai qtdembalagem,\n" +
                    "	p.QTDENT,\n" +
                    "	case when un.fraciona = 'S' then 'KG' else un.Apelido end tipoembalagem,\n" +
                    "	p.Fracionado e_balanca,\n" +
                    "	coalesce(p.Val, 0) validade,\n" +
                    "	p.Descricao descricaocompleta,\n" +
                    "	p.Descricao descricaogondola,\n" +
                    "	p.DescricaoEcf descricaoreduzida,\n" +
                    "	p.ChvGrp1 merc1,\n" +
                    "	nullif(p.ChvGrp2, 1) merc2,\n" +
                    "	nullif(p.ChvGrp3, 1) merc3,\n" +
                    "	nullif(p.ChvGrp4, 1) merc4,\n" +
                    "	p.EstoqueMinimo,\n" +
                    "	p.EstoqueMaximo,\n" +
                    "	p.EstoqueLoja estoque,\n" +
                    "	pr.MargemLucro margem,\n" +
                    "	pr.PCompra custocomimposto,\n" +
                    "	pr.PCusto custosemimposto,\n" +
                    "	pr.PVenda precovenda,\n" +
                    "	case p.Inativo when 1 then 0 else 1 end ativo,\n" +
                    "	p.NCM_Genero ncm,\n" +
                    "	p.ChvST_PIS_Saida piscofins_saida,\n" +
                    "	p.ChvST_PIS_Entrada piscofins_entrada,\n" +
                    "	p.ChvST icms_cst,\n" +
                    "	p.ICMS_Aliq icms_aliq,\n" +
                    "	p.ICMS_Red_Aliq icms_reducao,\n" +
                    "	coalesce(nullif(pr.PVenda2, 0), pr.PVenda) atacado,\n" +
                    "	pr.ApartirDe qtdAtacado\n" +
                    "from\n" +
                    "	tbProd p\n" +
                    "	left join tbTab_Embalagem_Sai un on\n" +
                    "		p.ChvUnidComercial = un.ChvEmbalagem\n" +
                    "	left join tbProd_CustoPreco pr on\n" +
                    "		p.ChvProd = pr.ChvProd\n" +
                    "order by\n" +
                    "	id"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setEan(rst.getString("ean"));
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                    imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
                    imp.seteBalanca(rst.getBoolean("e_balanca"));
                    imp.setValidade(rst.getInt("validade"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoGondola(rst.getString("descricaogondola"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setCodMercadologico1(rst.getString("merc1"));
                    imp.setCodMercadologico2(rst.getString("merc2"));
                    imp.setCodMercadologico3(rst.getString("merc3"));
                    imp.setCodMercadologico4(rst.getString("merc4"));
                    imp.setEstoqueMinimo(rst.getDouble("EstoqueMinimo"));
                    imp.setEstoqueMaximo(rst.getDouble("EstoqueMaximo"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setSituacaoCadastro(rst.getInt("ativo"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setPiscofinsCstDebito(rst.getString("piscofins_saida"));
                    imp.setPiscofinsCstCredito(rst.getString("piscofins_entrada"));
                    imp.setIcmsCst(rst.getInt("icms_cst"));
                    imp.setIcmsAliq(rst.getDouble("icms_aliq"));
                    imp.setIcmsReducao(rst.getDouble("icms_reducao"));
                    imp.setAtacadoPreco(rst.getDouble("atacado"));
                    
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
                    "select\n" +
                    "	f.ChvCad id,\n" +
                    "	f.NomeRazao razao,\n" +
                    "	f.CnpjCpf cnpj,\n" +
                    "	f.IeRg ie,\n" +
                    "	f.SimAtivo ativo,\n" +
                    "	e.Endereco,\n" +
                    "	e.Compl,\n" +
                    "	e.Bairro,\n" +
                    "	cid.CMN_IBGE cidadeibge,\n" +
                    "	cid.CUF_IBGE ufibge,\n" +
                    "	e.Cep,\n" +
                    "	e.Telefone1,\n" +
                    "	e.Telefone2,\n" +
                    "	e.Celular,\n" +
                    "	f.DtCad datacadastro,\n" +
                    "	f.Entrega,\n" +
                    "	f.EMail\n" +
                    "from\n" +
                    "	tbForn_Cadastro f\n" +
                    "	join tbForn_Endereco e on\n" +
                    "		f.ChvCad = e.ChvCad\n" +
                    "	join tbTab_Cid cid on\n" +
                    "		e.ChvTabCid = cid.ChvTabCid\n" +
                    "order by\n" +
                    "	1"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("razao"));
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    imp.setIe_rg(rst.getString("ie"));
                    imp.setAtivo(rst.getBoolean("ativo"));
                    imp.setEndereco(rst.getString("Endereco"));
                    imp.setComplemento(rst.getString("Compl"));
                    imp.setBairro(rst.getString("Bairro"));
                    imp.setIbge_municipio(rst.getInt("cidadeibge"));
                    imp.setIbge_uf(rst.getInt("ufibge"));
                    imp.setCep(rst.getString("Cep"));
                    imp.setTel_principal(rst.getString("Telefone1"));
                    imp.addTelefone("TELEFONE 2", rst.getString("Telefone2"));
                    imp.addCelular("CELULAR", rst.getString("Celular"));
                    imp.setDatacadastro(rst.getDate("datacadastro"));
                    imp.setPrazoEntrega(Utils.stringToInt(rst.getString("Entrega")));
                    imp.addEmail("E-MAIL", rst.getString("EMail"), TipoContato.COMERCIAL);
                    
                    try (Statement st = ConexaoSqlServer.getConexao().createStatement()) {
                        try (ResultSet rs = st.executeQuery(
                                "select\n" +
                                "	cd.ChvContato id,\n" +
                                "	ed.ChvCad,\n" +
                                "	coalesce(cd.Nome, '') + ( case when nullif(cd.Funcao,'*') is null then '' else ' (' + coalesce(nullif(cd.Funcao,'*'), '') + ')' end) nome,\n" +
                                "	nullif(cd.Telefone, '*') telefone,\n" +
                                "	nullif(cd.Celular, '*') celular,\n" +
                                "	nullif(cd.EMail, '*') email\n" +
                                "from \n" +
                                "	tbForn_Endereco_Contato cd\n" +
                                "	join tbForn_Endereco ed on\n" +
                                "		cd.ChvEnd = ed.ChvEnd\n" +
                                "where ed.ChvCad = " + imp.getImportId()
                        )) {
                            while (rs.next()) {
                                imp.addContato(
                                        rs.getString("id"),
                                        rs.getString("nome"),
                                        rs.getString("telefone"),
                                        rs.getString("celular"),
                                        TipoContato.COMERCIAL,
                                        rs.getString("email")
                                );
                            }
                        }
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
        
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	ChvProd id_produto,\n" +
                    "	ChvForn id_fornecedor,\n" +
                    "	CodigoForn codigoexterno\n" +
                    "from\n" +
                    "	tbProd_Forn\n" +
                    "order by\n" +
                    "	1,2,3"
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
    public List<ClienteIMP> getClientesPreferenciais() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	c.ChvCad id,\n" +
                    "	c.CnpjCpf cnpj,\n" +
                    "	c.IeRg,\n" +
                    "	c.NomeRazao razao,\n" +
                    "	c.SimAtivo ativo,\n" +
                    "	ed.Endereco,\n" +
                    "	ed.Numero,\n" +
                    "	ed.Compl,\n" +
                    "	ed.Bairro,\n" +
                    "	cid.CMN_IBGE municipio_ibge,\n" +
                    "	cid.CUF_IBGE uf_ibge,\n" +
                    "	ed.Cep,\n" +
                    "	c.EstadoCivil,\n" +
                    "	c.DtCad datacadastro,\n" +
                    "	c.DtNasc datanascimento,\n" +
                    "	cr.Empresa,\n" +
                    "	cr.Telefone,\n" +
                    "	cr.DtAdm dataadmissao,\n" +
                    "	cr.Funcao cargo,\n" +
                    "	cr.Renda salario,\n" +
                    "	cr.Credito valorlimite,\n" +
                    "	cr.NomeConjuge conjuge,\n" +
                    "	cr.NomePai,\n" +
                    "	cr.NomeMae,\n" +
                    "	c.Obs observacao,\n" +
                    "	vc.Vencimento,\n" +
                    "	nullif(cr.TelefoneConjuge, '*') telefoneConjuge,\n" +
                    "	nullif(c.EMail, '*') email,\n" +
                    "	nullif(ed.DDD1, '*') ddd1,\n" +
                    "	nullif(ed.DDD2, '*') ddd2,\n" +
                    "	nullif(ed.Telefone1, '*') telefone1,\n" +
                    "	nullif(ed.Telefone2, '*') telefone2,\n" +
                    "	nullif(cr.Telefone, '*') telefone3,\n" +
                    "	nullif(ed.DDDCelular, '*') dddc1,\n" +
                    "	nullif(ed.DDDCelular2, '*') dddc2,\n" +
                    "	nullif(ed.DDDCelular3, '*') dddc3,\n" +
                    "	nullif(ed.Celular, '*') celular1,\n" +
                    "	nullif(ed.Celular2, '*') celular2,\n" +
                    "	nullif(ed.Celular3, '*') celular3\n" +
                    "from\n" +
                    "	tbCli_Cadastro c\n" +
                    "	left join tbCli_Endereco ed on\n" +
                    "		ed.ChvCad = c.ChvCad\n" +
                    "	left join tbTab_Cid cid on\n" +
                    "		ed.ChvTabCid = cid.ChvTabCid\n" +
                    "	left join tbCli_Crediario cr on\n" +
                    "		cr.ChvCad = c.ChvCad\n" +
                    "	left join tbTab_Vencimentos vc on\n" +
                    "		cr.ChvTabVencimento = vc.ChvTabVencimento\n" +
                    "order by\n" +
                    "	1"
            )) {
                Set<String> uk = new HashSet<>();
                while (rst.next()) {
                    
                    if (uk.contains(rst.getString("id"))) {
                        continue;
                    }                    
                    
                    ClienteIMP imp = new ClienteIMP();
                    
                    imp.setId(rst.getString("id"));
                    uk.add(imp.getId());
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setInscricaoestadual(rst.getString("IeRg"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("razao"));
                    imp.setAtivo(rst.getBoolean("ativo"));
                    imp.setEndereco(rst.getString("Endereco"));
                    imp.setNumero(rst.getString("Numero"));
                    imp.setComplemento(rst.getString("Compl"));
                    imp.setBairro(rst.getString("Bairro"));
                    imp.setMunicipioIBGE(rst.getInt("municipio_ibge"));
                    imp.setUfIBGE(rst.getInt("uf_ibge"));
                    imp.setCep(rst.getString("Cep"));
                    imp.setEstadoCivil(rst.getInt("EstadoCivil"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setDataNascimento(rst.getDate("datanascimento"));
                    imp.setEmpresa(rst.getString("Empresa"));
                    imp.setTelefone(rst.getString("Telefone"));
                    imp.setDataAdmissao(rst.getDate("dataadmissao"));
                    imp.setCargo(rst.getString("cargo"));
                    imp.setSalario(rst.getDouble("salario"));
                    imp.setValorLimite(rst.getDouble("valorlimite"));
                    imp.setNomeConjuge(rst.getString("conjuge"));
                    imp.setNomePai(rst.getString("NomePai"));
                    imp.setNomeMae(rst.getString("NomeMae"));
                    imp.setObservacao2(rst.getString("observacao"));
                    imp.setDiaVencimento(rst.getInt("Vencimento"));
                    imp.addTelefone("CONJUGE", rst.getString("telefoneConjuge"));
                    imp.setEmail(rst.getString("email"));
                    
                    String tel1 = Utils.stringLong(rst.getString("ddd1") + '-' + rst.getString("telefone1"));
                    String tel2 = Utils.stringLong(rst.getString("ddd2") + '-' + rst.getString("telefone2"));
                    String tel3 = Utils.stringLong(rst.getString("telefone3"));
                    
                    tel1 = tel1.length() > 5 ? tel1 : "0";
                    tel2 = tel2.length() > 5 ? tel2 : "0";
                    tel3 = tel3.length() > 5 ? tel3 : "0";
                    
                    String cel1 = Utils.stringLong(rst.getString("dddc1") + '-' + rst.getString("celular1"));
                    String cel2 = Utils.stringLong(rst.getString("dddc2") + '-' + rst.getString("celular2"));
                    String cel3 = Utils.stringLong(rst.getString("dddc3") + '-' + rst.getString("celular3"));
                    
                    cel1 = cel1.length() > 5 ? cel1 : "0";
                    cel2 = cel2.length() > 5 ? cel2 : "0";
                    cel3 = cel3.length() > 5 ? cel3 : "0";
                    
                    imp.addTelefone("TELEFONE 1", tel1);
                    imp.addTelefone("TELEFONE 2", tel2);
                    imp.addTelefone("TELEFONE 3", tel3);
                    imp.addCelular("CELULAR 1", cel1);
                    imp.addCelular("CELULAR 2", cel2);
                    imp.addCelular("CELULAR 3", cel3);
                    
                    try (Statement st = ConexaoSqlServer.getConexao().createStatement()) {
                        try (ResultSet rs = st.executeQuery(
                                "select\n" +
                                "	cc.ChvContato id,\n" +
                                "	ed.ChvCad id_cliente,\n" +
                                "	coalesce(cc.Nome, '') + ( case when nullif(cc.Funcao,'*') is null then '' else ' (' + coalesce(nullif(cc.Funcao,'*'), '') + ')' end) nome,\n" +
                                "	cc.Telefone,\n" +
                                "	cc.Celular,\n" +
                                "	cc.EMail\n" +
                                "from\n" +
                                "	tbCli_Endereco_Contato cc\n" +
                                "	join tbCli_Endereco ed on\n" +
                                "		cc.ChvEnd = ed.ChvEnd\n" +
                                "where\n" +
                                "	ed.ChvCad = " + imp.getId()
                        )) {
                            while (rs.next()) {
                                imp.addContato(
                                        rs.getString("id"),
                                        rs.getString("nome"),
                                        rs.getString("telefone"),
                                        rs.getString("celular"),                                        
                                        rs.getString("email")
                                );
                            }
                        }
                    }
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    public void importarAtacadoPorEAN(int lojaVR) throws Exception {
        ProgressBar.setStatus("Preparando para gravar atacado...");
        Map<String, Integer> anteriores = new ProdutoAnteriorDAO().getAnteriores(getSistema(), getLojaOrigem());        
        Map<Long, Integer> eans = new ProdutoAutomacaoDAO().getEansCadastrados();
        Set<Long> atac = new ProdutoAutomacaoDAO().getEansCadastradosAtacado(lojaVR);
        Versao versao = Versao.createFromConnectionInterface(Conexao.getConexao());
        
        Conexao.begin();
        try {            
            List<ProdutoIMP> prods = getProdutos();
            ProgressBar.setStatus("Gravando atacado...");
            ProgressBar.setMaximum(prods.size());
            for (ProdutoIMP imp: prods) {            
                Integer id = anteriores.get(imp.getImportId());

                if (id != null) {                
                    if (!atac.contains(id.longValue())) {
                        double precoAtacado = imp.getAtacadoPreco();
                        double precoVenda;
                        int qtd = 10;//imp.getQtdEmbalagem();
                        long ean = id.longValue();

                        try (Statement stm = Conexao.createStatement()) {

                            try (ResultSet rst = stm.executeQuery(
                                    "select precovenda from produtocomplemento where id_loja = " + lojaVR + " and id_produto = " + id
                            )) {
                                rst.next();
                                precoVenda = rst.getDouble("precovenda");
                            }

                            if (!eans.containsKey(ean)) {
                                stm.execute("insert into produtoautomacao (id_produto, codigobarras, qtdembalagem, id_tipoembalagem, pesobruto, dun14) values (" + id + ", " + ean + ", " + qtd + ", 0, 0, false)");
                                eans.put(ean, id);
                            }
                            if (precoVenda != precoAtacado) {
                                double desconto = MathUtils.round(100 - ((imp.getAtacadoPreco() / (imp.getPrecovenda() == 0 ? 1 : imp.getPrecovenda())) * 100), 2);
                                if (versao.igualOuMenorQue(3, 18)) {
                                    stm.execute("insert into produtoautomacaoloja (codigobarras, precovenda, id_loja) values (" + ean + ", " + precoAtacado + ", " + lojaVR + ")");
                                    stm.execute("insert into produtoautomacaodesconto (codigobarras, id_loja, desconto) values (" + ean + ", " + lojaVR + ", " + desconto + ")");
                                } else {
                                    stm.execute("insert into produtoautomacaodesconto (codigobarras, id_loja, desconto, descontodiaanterior, descontodiaseguinte, dataultimodesconto) values (" + ean + ", " + lojaVR + ", " + String.format("%.2f", desconto) + ", 0, " + String.format("%.2f", desconto) + ", now())");
                                }
                                atac.add(ean);
                            }
                        }
                    }
                }
                
                ProgressBar.next();
            }
            
            Conexao.commit();
        } catch (Exception e) {
            Conexao.rollback();
            throw e;
        }
    }
    
}
