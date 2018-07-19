package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import vrimplantacao.classe.ConexaoPostgres;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Leandro
 */
public class WShopDAO extends InterfaceDAO {

    private static final Logger LOG = Logger.getLogger(WShopDAO.class.getName());
    
    @Override
    public String getSistema() {
        return "WShop";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        Set<OpcaoProduto> opt = new HashSet<>(OpcaoProduto.getMercadologico());
        opt.addAll(OpcaoProduto.getPadrao());
        opt.addAll(OpcaoProduto.getFamilia());
        opt.addAll(OpcaoProduto.getComplementos());
        opt.addAll(OpcaoProduto.getTributos());
        return opt;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	idgrupo merc1,\n" +
                    "	nmgrupo merc1_desc\n" +
                    "from\n" +
                    "	wshop.grupo\n" +
                    "order by \n" +
                    "	nmgrupo"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setMerc1ID(rst.getString("merc1"));
                    imp.setMerc1Descricao(rst.getString("merc1_desc"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }
    
    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select cdempresa, nrcgc || '-' || nmempresa razao from wshop.empshop order by cdempresa"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("cdempresa"), rst.getString("razao")));
                }
            }
        }
        
        return result;
    }
    
    private Map<String, String> mapaFamilia;

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();
        
        Set<String> fam = mapearFamilia();
        
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            for (String key: fam) {
                try (ResultSet rst = stm.executeQuery(
                        "select iddetalhe, dsdetalhe from wshop.detalhe where iddetalhe = '" + key + "'"
                )) {
                    while (rst.next()) {
                        FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                        imp.setImportSistema(getSistema());
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportId(rst.getString("iddetalhe"));
                        imp.setDescricao(rst.getString("dsdetalhe"));
                        result.add(imp);
                    }
                }
            }
        }   
        
        
        return result;
    }

    private Set<String> mapearFamilia() throws SQLException {
        Set<List<String>> itens = new LinkedHashSet<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select distinct\n" +
                            "	iddetalhe,\n" +
                            "	iddetalhe iddetalheequivalente\n" +
                            "from\n" +
                            "	wshop.prodequivalente\n" +
                            "union\n" +
                            "select \n" +
                            "	iddetalhe, \n" +
                            "	iddetalheequivalente \n" +
                            "from \n" +
                            "	wshop.ProdEquivalente \n" +
                            "order by \n" +
                            "	1, 2"
            )) {
                String lastKey = null;           
                
                List<String> listaAtual = null;
                while (rst.next()) {
                    String key = rst.getString("iddetalhe");
                    String value = rst.getString("iddetalheequivalente");
                    if (!key.equals(lastKey)) {
                        if (listaAtual != null && !itens.add(listaAtual)) {
                            LOG.finer("lista " + listaAtual.get(0) + " já existe!");
                        }
                        listaAtual = new ArrayList<>();
                        lastKey = key;
                    }                    
                    listaAtual.add(value);
                }
                System.out.println("Lista: " + itens.size());
            }
        }
        mapaFamilia = new HashMap<>();
        Set<String> fam = new HashSet<>();
        for (List<String> lista: itens) {
            fam.add(lista.get(0));
            for (String item: lista) {
                mapaFamilia.put(item, lista.get(0));
            }
        }
        return fam;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	ids.dscodigo id,\n" +
                    "	dt.dtcadastro datacadastro,\n" +
                    "	dt.dtaltvlprecovenda dataalteracao,\n" +
                    "	coalesce(ean.dscodigo, ids.dscodigo) ean,\n" +
                    "	coalesce(nullif(dt.qtembalagem, 0), 1) qtdembalagemcotacao,\n" +
                    "	substring(un.dssigla, 1,2) unidade,\n" +
                    "	dt.stbalanca ebalanca,\n" +
                    "	coalesce(bal.nrdiasvalidade, 0) validade,\n" +
                    "	p.nmproduto descricaocompleta,\n" +
                    "	dt.dsdetalhe descricaoreduzida,\n" +
                    "	p.idgrupo merc1,\n" +
                    "	dt.idproduto,\n" +
                    "	dt.iddetalhe,\n" +
                    "	dt.pesoliquido,\n" +
                    "	dt.pesobruto,\n" +
                    "	est.qtestoque estoque,\n" +
                    "	dt.allucro margem,\n" +
                    "	dt.vlprecocusto custocomimposto,\n" +
                    "	dt.vlprecovenda precovenda,\n" +
                    "	dt.stdetalheativo ativo,\n" +
                    "	p.cdipi ncm,\n" +
                    "	p.cest,\n" +
                    "	dt.cdsittribpisentrada piscofins_entrada,\n" +
                    "	dt.cdsittribpis piscofins_saida,\n" +
                    "	dt.cdnaturezareceita piscofins_naturezareceita,	\n" +
                    "	icms.cdsituacaotributaria icms_cst,\n" +
                    "	icms.alicms icms_aliquota,\n" +
                    "	0 as icms_reduzido\n" +
                    "from\n" +
                    "	wshop.produto p\n" +
                    "	join wshop.empshop emp on emp.cdempresa = '" + getLojaOrigem() + "'\n" +
                    "	join wshop.detalhe dt on p.idproduto = dt.idproduto\n" +
                    "	join wshop.codigos ids on dt.iddetalhe = ids.iddetalhe and dt.idproduto = ids.idproduto and ids.tpcodigo = 'Chamada'\n" +
                    "	left join (\n" +
                    "		select \n" +
                    "			ean.*\n" +
                    "		from\n" +
                    "			wshop.codigos ean\n" +
                    "			join wshop.detalhe dt on ean.iddetalhe = dt.iddetalhe and ean.idproduto = dt.idproduto\n" +
                    "		where\n" +
                    "			(dt.stbalanca and ean.tpcodigo = 'Chamada') or\n" +
                    "			(not dt.stbalanca and ean.tpcodigo != 'Chamada')\n" +
                    "	) ean on dt.iddetalhe = ean.iddetalhe and dt.idproduto = ean.idproduto	\n" +
                    "	left join wshop.unidade un on un.idunidade = p.idunidade\n" +
                    "	left join wshop.produto_balanca bal on bal.iddetalhe = dt.iddetalhe\n" +
                    "	left join (\n" +
                    "		select\n" +
                    "			est.iddetalhe,\n" +
                    "			est.cdempresa,\n" +
                    "			est.qtestoque\n" +
                    "		from\n" +
                    "			wshop.estoque est\n" +
                    "			join (\n" +
                    "				select \n" +
                    "					iddetalhe, \n" +
                    "					cdempresa,\n" +
                    "					max(dtreferencia) dtreferencia \n" +
                    "				from\n" +
                    "					wshop.estoque\n" +
                    "				group by \n" +
                    "					iddetalhe, cdempresa\n" +
                    "			) a on est.iddetalhe = a.iddetalhe and est.dtreferencia = a.dtreferencia\n" +
                    "	) est on est.iddetalhe = dt.iddetalhe and est.cdempresa = emp.cdempresa\n" +
                    "	left join wshop.icms_uf icms on p.idcalculoicms = icms.idcalculoicms and iduf = emp.cduf and idufdestino = emp.cduf and stvendaconsumidorfinal\n" +
                    "order by\n" +
                    "	id"
            )) {
                mapearFamilia();
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setDataAlteracao(rst.getDate("dataalteracao"));
                    imp.setEan(rst.getString("ean"));
                    imp.setQtdEmbalagemCotacao(rst.getInt("qtdembalagemcotacao"));
                    imp.setTipoEmbalagem(rst.getString("unidade"));
                    imp.seteBalanca(rst.getBoolean("ebalanca"));
                    imp.setValidade(rst.getInt("validade"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(rst.getString("descricaocompleta"));
                    imp.setCodMercadologico1(rst.getString("merc1"));
                    String familia = mapaFamilia.get(rst.getString("iddetalhe"));
                    if (familia != null) LOG.finer("Familia " + familia + " encontrada para " + imp.getImportId() + " - " + imp.getDescricaoCompleta());
                    imp.setIdFamiliaProduto(familia);
                    imp.setPesoLiquido(rst.getDouble("pesoliquido"));
                    imp.setPesoBruto(rst.getDouble("pesobruto"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(rst.getDouble("custocomimposto"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setSituacaoCadastro(rst.getBoolean("ativo") ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstCredito(rst.getString("piscofins_entrada"));
                    imp.setPiscofinsCstDebito(rst.getString("piscofins_saida"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("piscofins_naturezareceita"));
                    imp.setIcmsCst(Utils.stringToInt(rst.getString("icms_cst")));
                    imp.setIcmsAliq(rst.getDouble("icms_aliquota"));
                    imp.setIcmsReducao(rst.getDouble("icms_reduzido"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }
    
    
    
}
