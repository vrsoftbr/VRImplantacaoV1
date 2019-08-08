package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import vrframework.remote.ItemComboVO;
import vrimplantacao.classe.ConexaoPostgres;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Importacao
 */
public class ControlWareDAO extends InterfaceDAO {

    @Override
    public String getSistema() {
        return "ControlWare";
    }
    
    public List<Estabelecimento> getLojaCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select codestabelec id, razaosocial descricao "
                            + "from estabelecimento order by id"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("id"), rst.getInt("id") + " - " + rst.getString("descricao")));
                }
            }
        }
        return result;
    }
    
    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "	dep.coddepto id_merc1,\n" +
                    "	dep.nome merc1,\n" +
                    "	grp.codgrupo id_merc2,\n" +
                    "	grp.descricao merc2,\n" +
                    "	sgr.codsubgrupo id_merc3,\n" +
                    "	sgr.descricao merc3\n" +
                    "from\n" +
                    "	departamento dep\n" +
                    "	left join grupoprod grp on dep.coddepto = grp.codgrupo\n" +
                    "	left join subgrupo sgr on grp.codgrupo = sgr.codgrupo\n" +
                    "order by\n" +
                    "	id_merc1,\n" +
                    "	id_merc2,\n" +
                    "	id_merc3"
            )) {
                while (rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rs.getString("id_merc1"));
                    imp.setMerc1Descricao(rs.getString("merc1"));
                    imp.setMerc2ID(rs.getString("id_merc2"));
                    imp.setMerc2Descricao(rs.getString("merc2"));
                    imp.setMerc3ID(rs.getString("id_merc3"));
                    imp.setMerc3Descricao(rs.getString("merc3"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }
    
    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "	codfamilia,\n" +
                    "	descricao\n" +
                    "from \n" +
                    "	familia \n" +
                    "order by \n" +
                    "	codfamilia"
            )) {
                while (rs.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("codfamilia"));
                    imp.setDescricao(rs.getString("descricao"));
                    
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
            stm.executeUpdate("set client_encoding to 'WIN1252';");
            try (ResultSet rs = stm.executeQuery(
                "select\n" +
                "	p.codproduto id,\n" +
                "	p.descricaofiscal descricaocompleta,\n" +
                "	p.descricao descricaoreduzida,\n" +
                "	p.descricaofiscal descricaogondola,\n" +
                "	case p.foralinha\n" +
                "	when 'S' then 0\n" +
                "	else 1 end as id_Situacaocadastro,\n" +
                "	p.datainclusao dataCadastro,\n" +
                "	p.coddepto mercadologico1,\n" +
                "	p.codgrupo mercadologico2,\n" +
                "	p.codsubgrupo mercadologico3,\n" +
                "       case when p.pesado = 'S' then true else false end as e_balanca,\n" +
                "	replace(ncm.codigoncm,'.','') ncm,\n" +
                "	replace(cest.cest,'.','') cest,\n" +
                "	codfamilia id_familiaproduto,\n" +
                "	pe.margemvrj margem,\n" +
                "	emb.quantidade qtdEmbalagem,\n" +
                "	coalesce(ean.codean,'') codigobarras,\n" +
                "	case when pe.diasvalidade = 0 then p.diasvalidade else pe.diasvalidade end as validade,\n" +
                "	un.sigla id_tipoEmbalagem,\n" +
                "	piscofinsent.codcst idTipoPisCofinsCredito,\n" +
                "	piscofinssai.codcst idTipoPisCofinsDebito,\n" +
                "	natr.codigo naturezaReceita,\n" +
                "	pe.precovrj precovenda,\n" +
                "	pe.custorep custocomnota,\n" +
                "	pe.custorep custosemnota,\n" +
                "	pe.sldsaida estoque,\n" +
                "	pe.estminimo estoque_minimo,\n" +
                "	pe.estmaximo estoque_maximo,\n" +
                "	estab.uf idEstado,\n" +
                "	cast(icms_s.codcst as integer) AS icms_s_cst,\n" +
                "	icms_s.aliqicms icms_s_aliq,\n" +
                "	icms_s.aliqredicms icms_s_reducao,\n" +
                "	cast(icms_e.codcst as integer) AS icms_e_cst,\n" +
                "	icms_e.aliqicms icms_e_aliq,\n" +
                "	icms_e.aliqredicms icms_e_reducao,\n" +
                "	p.pesoliq pesoliquido,\n" +
                "	p.pesobruto\n" +
                "from \n" +
                "	produto p\n" +
                "	join produtoestab pe on pe.codproduto = p.codproduto\n" +
                "	join ncm on ncm.idncm = p.idncm\n" +
                "	left join cest on ncm.idcest = cest.idcest\n" +
                "	join embalagem emb on p.codembalvda = emb.codembal\n" +
                "	join unidade un on emb.codunidade = un.codunidade\n" +
                "	left join produtoean ean on ean.codproduto = p.codproduto\n" +
                "	join piscofins piscofinsent ON p.codpiscofinsent = piscofinsent.codpiscofins\n" +
                "	join piscofins piscofinssai ON p.codpiscofinssai = piscofinssai.codpiscofins	\n" +
                "	left join natreceita natr on natr.natreceita = p.natreceita\n" +
                "	join estabelecimento estab on pe.codestabelec = estab.codestabelec\n" +
                "	join classfiscal icms_s ON p.codcfpdv = icms_s.codcf\n" +
                "	join classfiscal icms_e ON p.codcfnfe = icms_e.codcf\n" +
                "where	estab.codestabelec = " + getLojaOrigem() + "\n" +
                "order by\n" +
                "	p.codproduto"
            )) {
                while (rs.next()) {      
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id"));
                    imp.setDescricaoCompleta(rs.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rs.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(rs.getString("descricaogondola"));
                    imp.setEan(rs.getString("codigobarras"));
                    imp.setPrecovenda(rs.getDouble("precovenda"));
                    imp.setCustoComImposto(rs.getDouble("custocomnota"));
                    imp.setCustoSemImposto(rs.getDouble("custosemnota"));
                    imp.setEstoque(rs.getDouble("estoque"));
                    imp.setEstoqueMaximo(rs.getDouble("estoque_maximo"));
                    imp.setEstoqueMinimo(rs.getDouble("estoque_minimo"));
                    imp.setPesoBruto(rs.getDouble("pesobruto"));
                    imp.setPesoLiquido(rs.getDouble("pesoliquido"));
                    imp.setSituacaoCadastro(rs.getInt("id_situacaocadastro"));
                    imp.setDataCadastro(rs.getDate("datacadastro"));
                    imp.setCodMercadologico1(rs.getString("mercadologico1"));
                    imp.setCodMercadologico2(rs.getString("mercadologico2"));
                    imp.setCodMercadologico3(rs.getString("mercadologico3"));
                    imp.seteBalanca(rs.getBoolean("e_balanca"));
                    imp.setValidade(rs.getInt("validade"));
                    imp.setNcm(rs.getString("ncm"));
                    imp.setCest(rs.getString("cest"));
                    imp.setIdFamiliaProduto(rs.getString("id_familiaproduto"));
                    imp.setMargem(rs.getDouble("margem"));
                    imp.setQtdEmbalagem(rs.getInt("qtdembalagem"));
                    imp.setTipoEmbalagem(rs.getString("id_tipoembalagem"));
                    imp.setPiscofinsCstCredito(rs.getString("idtipopiscofinscredito"));
                    imp.setPiscofinsCstDebito(rs.getString("idtipopiscofinsdebito"));
                    imp.setPiscofinsNaturezaReceita(rs.getString("naturezareceita"));
                    imp.setIcmsCstSaida(rs.getInt("icms_s_cst"));
                    imp.setIcmsAliqSaida(rs.getDouble("icms_s_aliq"));
                    imp.setIcmsReducaoSaida(rs.getDouble("icms_s_reducao"));
                    imp.setIcmsCstEntrada(rs.getInt("icms_e_cst"));
                    imp.setIcmsAliqEntrada(rs.getDouble("icms_e_aliq"));
                    imp.setIcmsReducaoEntrada(rs.getDouble("icms_e_reducao"));
                    
                    result.add(imp);
                }
                return result;
            }
        }
    }
    
    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        try(Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                "	f.codfornec id,\n" +
                "	f.razaosocial razao,\n" +
                "	f.nome fantasia,\n" +
                "	f.cpfcnpj cnpj,\n" +
                "	f.rgie inscricaoestadual,\n" +
                "	f.fone1,\n" +
                "	f.fone2,\n" +
                "	f.fone3,\n" +
                "	f.fax,\n" +
                "	f.site,\n" +
                "	f.endereco,\n" +
                "	f.numero,\n" +
                "	f.complemento,\n" +
                "	f.bairro,\n" +
                "	f.cep,\n" +
                "	c.codoficial id_municipio, \n" +
                "	e.codoficial id_estado,\n" +
                "	f.observacao,\n" +
                "	f.datainclusao datacadastro,\n" +
                "	f.email,\n" +
                "       case f.status when 'A' then 1 else 0 end as id_situacaocadastro\n" +
                "from \n" +
                "	fornecedor f\n" +
                "	left join cidade c on f.codcidade = c.codcidade\n" +
                "	left join estado e on c.uf = e.uf")) {
                while(rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setCnpj_cpf(rs.getString("cnpj"));
                    imp.setIe_rg(rs.getString("inscricaoestadual"));
                    imp.setAtivo(rs.getInt("id_situacaocadastro") == 1 ? true : false);
                    imp.setTel_principal(rs.getString("fone1"));
                    if(rs.getString("fone2") != null && !rs.getString("fone2").isEmpty()) {
                        imp.addContato("1", "TELEFONE 2", rs.getString("fone2"), "", TipoContato.COMERCIAL, "");
                    }
                    if(rs.getString("fone3") != null && !rs.getString("fone3").isEmpty()) {
                        imp.addContato("2", "TELEFONE 3", rs.getString("fone3"), "", TipoContato.COMERCIAL, "");
                    }
                    if(rs.getString("fax") != null && !rs.getString("fax").isEmpty()) {
                        imp.addContato("3", "FAX", rs.getString("fax"), "", TipoContato.COMERCIAL, "");
                    }
                    if(rs.getString("email") != null && !rs.getString("email").isEmpty()) {
                        imp.addContato("4", "EMAIL", "", "", TipoContato.COMERCIAL, rs.getString("email"));
                    }
                    if(rs.getString("site") != null && !rs.getString("site").isEmpty()) {
                        imp.addContato("5", rs.getString("site"), "", "", TipoContato.COMERCIAL, "");
                    }
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setCep(rs.getString("cep"));
                    imp.setIbge_municipio(rs.getInt("id_municipio"));
                    imp.setIbge_uf(rs.getInt("id_estado"));
                    imp.setObservacao(rs.getString("observacao"));
                    imp.setDatacadastro(rs.getDate("datacadastro"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }
}
