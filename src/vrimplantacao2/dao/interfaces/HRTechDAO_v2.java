package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import vr.core.utils.StringUtils;
import vrframework.classe.ProgressBar;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.nutricional.OpcaoNutricional;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.dao.cadastro.produto2.associado.OpcaoAssociado;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.vo.cadastro.financeiro.contareceber.OpcaoContaReceber;
import vrimplantacao2.vo.cadastro.tributacao.AliquotaVO;
import vrimplantacao2.vo.enums.OpcaoFiscal;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoEmpresa;
import vrimplantacao2.vo.enums.TipoEstadoCivil;
import vrimplantacao2.vo.enums.TipoIva;
import vrimplantacao2.vo.enums.TipoProduto;
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.vo.importacao.AssociadoIMP;
import vrimplantacao2.vo.importacao.ChequeIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CompradorIMP;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.ContaReceberIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoPagamentoAgrupadoIMP;
import vrimplantacao2.vo.importacao.DivisaoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.NutricionalIMP;
import vrimplantacao2.vo.importacao.OfertaIMP;
import vrimplantacao2.vo.importacao.PautaFiscalIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;

/**
 *
 * @author Importacao
 */
public class HRTechDAO_v2 extends InterfaceDAO implements MapaTributoProvider {

    private static final Logger LOG = Logger.getLogger(HRTechDAO_v2.class.getName());
    
    private String complemento;
    private String codigoConvenio = "000001";

    public void setCodigoConvenio(String codigoConvenio) {
        this.codigoConvenio = codigoConvenio == null || codigoConvenio.trim().equals("") ? "000001" : codigoConvenio;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }    
    
    @Override
    public String getSistema() {
        if (complemento == null || complemento.trim().equals("")) {
            return "HRTech";
        } else {
            return "HRTech - " + complemento;
        }
    }

    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "    lj.codigoenti id,\n" +
                    "    rtrim(ltrim(cpf.NOMAPELIDO)) nome,\n" +
                    "    rtrim(ltrim(cpf.NUMCGC_CPF)) cnpj\n" +
                    "from\n" +
                    "    fl060loj lj\n" +
                    "    join FLCGCCPF cpf on\n" +
                    "    	lj.CODCGCCPFS = cpf.CODIGOENTI\n" +
                    "order by\n" +
                    "    1"
            )) {
                while (rs.next()) {
                    result.add(new Estabelecimento(rs.getString("id"), rs.getString("nome") + " - " + rs.getString("cnpj")));
                }
            }
        }
        return result;
    }

    /*@Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	m1.codmerc1,\n"
                    + "	m1.descmerc1,\n"
                    + "	m2.codmerc2,\n"
                    + "	m2.descmerc2,\n"
                    + "	m3.codmerc3,\n"
                    + "	m3.descmerc3,\n"
                    + "	coalesce(m4.codmerc4, 1) codmerc4,\n"
                    + "	coalesce(m4.descmerc4, m3.descmerc3) descmerc4\n" 
                    + "from\n"
                    + "	(\n"
                    + "		select\n"
                    + "			gruc03seto codmerc1,\n"
                    + "			gruc35desc descmerc1\n"
                    + "		from\n"
                    + "			fl100dpt s\n"
                    + "		where\n"
                    + "			gruc03seto != '' and gruc03grup = '' and gruc03subg = '' and gruc03fami = '' and gruc03subf = ''\n"
                    + "	) m1\n"
                    + "	join (\n"
                    + "		select\n"
                    + "			gruc03seto codmerc1,\n"
                    + "			gruc03grup codmerc2,\n"
                    + "			gruc35desc descmerc2\n"
                    + "		from\n"
                    + "			fl100dpt s\n"
                    + "		where\n"
                    + "			gruc03seto != '' and gruc03grup != '' and gruc03subg = '' and gruc03fami = '' and gruc03subf = ''\n"
                    + "	) m2 on\n"
                    + "		m1.codmerc1 = m2.codmerc1\n"
                    + "	join (\n"
                    + "			select\n"
                    + "			gruc03seto codmerc1,\n"
                    + "			gruc03grup codmerc2,\n"
                    + "			gruc03subg codmerc3,\n"
                    + "			gruc35desc descmerc3\n"
                    + "		from\n"
                    + "			fl100dpt s\n"
                    + "		where\n"
                    + "			gruc03seto != '' and gruc03grup != '' and gruc03subg != '' and gruc03fami = '' and gruc03subf = ''\n"
                    + "	) m3 on\n"
                    + "		m2.codmerc1 = m3.codmerc1 and m2.codmerc2 = m3.codmerc2\n"
                    + "	left join (\n"
                    + "			select\n"
                    + "			gruc03seto codmerc1,\n"
                    + "			gruc03grup codmerc2,\n"
                    + "			gruc03subg codmerc3,\n"
                    + "			gruc03fami codmerc4,\n"
                    + "			gruc35desc descmerc4\n"
                    + "		from\n"
                    + "			fl100dpt s\n"
                    + "		where\n"
                    + "			gruc03seto != '' and gruc03grup != '' and gruc03subg != '' and gruc03fami != '' and gruc03subf = ''\n"
                    + "	) m4 on\n"
                    + "		m3.codmerc1 = m4.codmerc1 and m3.codmerc2 = m4.codmerc2 and m3.codmerc3 = m4.codmerc3\n"
                    //+ "where m1.codmerc1 >= 200 \n"
                    + "	order by 1,3,5,7")) {
                while (rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rs.getString("codmerc1"));
                    imp.setMerc1Descricao(rs.getString("descmerc1"));
                    imp.setMerc2ID(rs.getString("codmerc2"));
                    imp.setMerc2Descricao(rs.getString("descmerc2"));
                    imp.setMerc3ID(rs.getString("codmerc3"));
                    imp.setMerc3Descricao(rs.getString("descmerc3"));
                    imp.setMerc4ID(rs.getString("codmerc4"));
                    imp.setMerc4Descricao(rs.getString("descmerc4"));

                    result.add(imp);
                }
            }
        }
        return result;
    }*/
    
    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "	m1.codmerc1,\n" +
                    "	m1.descmerc1,\n" +
                    "	m2.codmerc2,\n" +
                    "	m2.descmerc2 \n" +
                    "from\n" +
                    "	(\n" +
                    "		select\n" +
                    "			gruc03seto codmerc1,\n" +
                    "			gruc35desc descmerc1\n" +
                    "		from\n" +
                    "			fl100dpt s\n" +
                    "		where\n" +
                    "			gruc03seto != '' and gruc03grup = '' and gruc03subg = '' and gruc03fami = '' and gruc03subf = ''\n" +
                    "	) m1\n" +
                    "	join (\n" +
                    "		select\n" +
                    "			gruc03seto codmerc1,\n" +
                    "			gruc03grup codmerc2,\n" +
                    "			gruc35desc descmerc2\n" +
                    "		from\n" +
                    "			fl100dpt s\n" +
                    "		where\n" +
                    "			gruc03seto != '' and gruc03grup != '' and gruc03subg = '' and gruc03fami = '' and gruc03subf = ''\n" +
                    "	) m2 on\n" +
                    "		m1.codmerc1 = m2.codmerc1\n" +
                    "	order by 1,3")) {
                while (rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rs.getString("codmerc1"));
                    imp.setMerc1Descricao(rs.getString("descmerc1"));
                    imp.setMerc2ID(rs.getString("codmerc2"));
                    imp.setMerc2Descricao(rs.getString("descmerc2"));
                    imp.setMerc3ID("1");
                    imp.setMerc3Descricao(rs.getString("descmerc2"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();
        
        try (
                Statement st = ConexaoSqlServer.getConexao().createStatement();
                ResultSet rs = st.executeQuery(
                        "select\n" +
                        "	CODIGOPRE id,\n" +
                        "	DESCRICAO\n" +
                        "from\n" +
                        "	flfilpre\n" +
                        "order by\n" +
                        "	CODIGOPRE"
                )) {
            while (rs.next()) {
                FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                
                imp.setImportSistema(getSistema());
                imp.setImportLoja(getLojaOrigem());
                imp.setImportId(rs.getString("id"));
                imp.setDescricao(rs.getString("descricao"));
                
                result.add(imp);
            }
        }
        
        return result;
    }
    
    @Override
    public List<AssociadoIMP> getAssociados(Set<OpcaoAssociado> opt) throws Exception {
        List<AssociadoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	pf.CODIGOPLU idprodutopai,\n" +
                    "	pf.ESTC35DESC descricaoprodutopai,\n" +
                    "	pr.CODIGOPLU idprodutofilho,\n" +
                    "	pr.ESTC35DESC descricaoprodutofilho,\n" +
                    "	p.qtde qtdfilho\n" +
                    "from \n" +
                    "	FL310INS p\n" +
                    "join fl300est pr on p.CODIGOPLU = pr.CODIGOPLU\n" +
                    "join fl300est pf on p.CODIASSO = pf.CODIGOPLU\n" +
                    "order by \n" +
                    "	1, 4")) {
                while(rs.next()) {
                    AssociadoIMP imp = new AssociadoIMP();
                    
                    imp.setImpIdProduto(rs.getString("idprodutopai"));
                    imp.setDescricaoAssociado(rs.getString("descricaoprodutopai"));
                    imp.setImpIdProdutoItem(rs.getString("idprodutofilho"));
                    imp.setDescricaoAssociadoItem(rs.getString("descricaoprodutofilho"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	codigoplu idproduto,\n"
                    + "	estc13codi ean,\n"
                    + "	qtd_emb_vd quantidade,\n"
                    + "	por_des_vd desconto\n"
                    + "from\n"
                    + "	FL322EAN")) {
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    String id = rs.getString("idproduto");
                    //id = id.substring(0, id.length() - 1);
                    imp.setImportId(id);
                    imp.setEan(rs.getString("ean"));
                    imp.setQtdEmbalagem(rs.getInt("quantidade"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos(OpcaoProduto opt) throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        if (opt == OpcaoProduto.ATACADO) {
            try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
                try (ResultSet rs = stm.executeQuery(
                        "select\n"
                        + "	e.codigoplu idproduto,\n"
                        + "	e.estc13codi ean,\n"
                        + "	e.qtd_emb_vd quantidade,\n"
                        + "	e.por_des_vd porcentagematacado,\n"
                        + "	cast(p.vendaatua as numeric(10,4)) precovenda,\n"
                        + "	cast(round((p.vendaatua - (p.vendaatua * e.por_des_vd / 100)), 2) as numeric(10,4)) precovendaatacado\n"
                        + "from\n"
                        + "	FL322EAN e\n"
                        + "join\n"
                        + "	HRPDV_PREPARA_PRO p on (e.codigoplu = p.codigoplu)\n"
                        + "where\n"
                        + "	e.por_des_vd > 0 and\n"
                        + "	e.qtd_emb_vd > 1 and\n"
                        + "	p.codigoloja = " + getLojaOrigem())) {
                    while (rs.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        String id = rs.getString("idproduto");
                        id = id.substring(0, id.length() - 1);
                        imp.setImportId(id);
                        imp.setEan(rs.getString("ean"));
                        imp.setPrecovenda(rs.getDouble("precovenda"));
                        imp.setAtacadoPorcentagem(rs.getDouble("porcentagematacado"));
                        imp.setQtdEmbalagem(rs.getInt("quantidade"));

                        result.add(imp);
                    }
                }
            }
            return result;
        }
        if (opt == OpcaoProduto.DESCONTINUADO) {
            try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
                try (ResultSet rs = stm.executeQuery(
                        "select\n" +
                        "	CODIGOPLU,\n" +
                        "	MIXPRODUTO\n" +
                        "from\n" +
                        "	FL302MIX"
                )) {
                    while (rs.next()) {
                        int index = Utils.stringToInt(getLojaOrigem(), -2);
                        String a = rs.getString("MIXPRODUTO");
                        boolean descontinuado = a.charAt(index - 1) == '0';
                        
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rs.getString("CODIGOPLU"));
                        imp.setDescontinuado(descontinuado);
                        
                        if ("052061".equals(rs.getString("CODIGOPLU"))) 
                            System.out.println("OK");
                        
                        result.add(imp);
                    }
                }                
            }
            return result;
        }
        if (opt == OpcaoProduto.TROCA) {
            try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
                try (ResultSet rs = stm.executeQuery(
                        "select\n" +
                        "		codigoplu,\n" +
                        "		sum(QTDEPENDEN) troca\n" +
                        "	from\n" +
                        "		FL345TRO\n" +
                        "	where\n" +
                        "		ltrim(rtrim(CODIGOPLU)) != ''\n" +
                        "		and CODIGOLOJA = " + getLojaOrigem() + "\n" +
                        "	group by\n" +
                        "		codigoplu"
                )) {
                    while (rs.next()) {                        
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rs.getString("CODIGOPLU"));
                        imp.setTroca(rs.getDouble("troca"));
                        
                        result.add(imp);
                    }
                }                
            }
            return result;
        }
        return null;
    }

    @Override
    public List<DivisaoIMP> getDivisoes() throws Exception {
        List<DivisaoIMP> result = new ArrayList<>();
        
        try (
                Statement st = ConexaoSqlServer.getConexao().createStatement();
                ResultSet rs = st.executeQuery(
                        "select distinct\n" +
                        "	div.CODIGOENTI + '-' + div.NUMCGC_CPF id,\n" +
                        "	div.NOMEENTIDA descricao\n" +
                        "from\n" +
                        "	fl812div div\n" +
                        "order by\n" +
                        "	div.CODIGOENTI + '-' + div.NUMCGC_CPF"
                )) {
            while (rs.next()) {
                result.add(new DivisaoIMP(
                        rs.getString("id"),
                        rs.getString("descricao")
                ));
            }
        }
        
        return result;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        try (Statement st = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = st.executeQuery(
                    "select distinct\n" +
                    "	ts.CODIGOTRIB id,\n" +
                    "	ts.DESCRICAO,\n" +
                    "	ts.situatribu icms_cst,\n" +
                    "	ts.VALORICM icms_aliq,\n" +
                    "	ts.mrger icms_red\n" +
                    "from\n" +
                    "	fl301est est\n" +
                    "	join fltribut ts on \n" +
                    "		est.codtribsai = ts.codigotrib and\n" +
                    "		ts.codigoloja = " + getLojaOrigem() + "\n" +
                    "union\n" +
                    "select distinct\n" +
                    "	te.CODIGOTRIB id,\n" +
                    "	te.descricao,\n" +
                    "	te.situatribu icms_cst,\n" +
                    "	te.VALORICM icms_aliq,\n" +
                    "	te.mrger icms_red\n" +
                    "from\n" +
                    "	fl301est est\n" +
                    "	join fltribut te on \n" +
                    "		est.codtribent = te.codigotrib and\n" +
                    "		te.codigoloja = " + getLojaOrigem()
            )) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(
                            rs.getString("id"),
                            String.format(
                                "%s - %03d - %.2f - %.2f",
                                rs.getString("descricao"),
                                rs.getInt("icms_cst"),
                                rs.getDouble("icms_aliq"),
                                rs.getDouble("icms_red")
                            ),
                            rs.getInt("icms_cst"),
                            rs.getDouble("icms_aliq"),
                            rs.getDouble("icms_red")
                    ));
                }
            }
        }
        
        return result;
    }

    @Override
    public List<NutricionalIMP> getNutricional(Set<OpcaoNutricional> opcoes) throws Exception {
        List<NutricionalIMP> result = new ArrayList<>();
        
        try (
                Statement st = ConexaoSqlServer.getConexao().createStatement();
                ResultSet rs = st.executeQuery(
                        "select\n" +
                        "	nut.CODIGO id,\n" +
                        "	coalesce(nullif(rtrim(ltrim(nut.DESCINFO)),''), p.ESTC35DESC) descricao,\n" +
                        "	p.CODIGOPLU id_produto,\n" +
                        "	nut.VALOR_CALORICO caloria,\n" +
                        "	nut.CARBOIDRATOS,\n" +
                        "	nut.PROTEINAS,\n" +
                        "	nut.GORDURAS_TOTAIS,\n" +
                        "	nut.GORDURAS_SATURADAS,\n" +
                        "	nut.GORDURA_TRANS,\n" +
                        "	nut.COLESTEROL,\n" +
                        "	nut.FIBRA_ALIMENTAR,\n" +
                        "	nut.CALCIO,\n" +
                        "	nut.FERRO,\n" +
                        "	nut.SODIO,\n" +
                        "	nut.VDCALORICO,\n" +
                        "	nut.VDCARBOIDRATOS,\n" +
                        "	nut.VDPROTEINAS,\n" +
                        "	nut.VDGORDURAS_TOTAIS,\n" +
                        "	nut.VDGORDURAS_SATURADAS,\n" +
                        "	nut.VDFIBRA_ALIMENTAR,\n" +
                        "	nut.VDCALCIO,\n" +
                        "	nut.VDFERRO,\n" +
                        "	nut.VDSODIO,\n" +
                        "	nut.PORCAO,\n" +
                        "	nut.PORCAO_DESC,\n" +
                        "	nut.UNID_PORC\n" +
                        "from\n" +
                        "	FL328NUT n\n" +
                        "	join FLINFNUT nut on\n" +
                        "		n.CODIGO = nut.CODIGO\n" +
                        "	join FL300EST p on\n" +
                        "		n.CODIGOPLU = p.CODIGOPLU\n" +
                        "order by\n" +
                        "	nut.CODIGO"
                )
                ) {
            while (rs.next()) {
                NutricionalIMP imp = new NutricionalIMP();
                
                imp.setId(rs.getString("id"));
                imp.setDescricao(rs.getString("descricao"));
                imp.addProduto(rs.getString("id_produto"));
                imp.setCaloria(rs.getInt("caloria"));
                imp.setCarboidrato(rs.getInt("CARBOIDRATOS"));
                imp.setProteina(rs.getInt("PROTEINAS"));
                imp.setGordura(rs.getInt("GORDURAS_TOTAIS"));
                imp.setGorduraSaturada(rs.getInt("GORDURAS_SATURADAS"));
                imp.setGorduraTrans(rs.getInt("GORDURA_TRANS"));
                imp.setFibra(rs.getInt("FIBRA_ALIMENTAR"));
                imp.setCalcio(rs.getInt("CALCIO"));
                imp.setFerro(rs.getInt("FERRO"));
                imp.setSodio(rs.getInt("SODIO"));
                imp.setPercentualCaloria(rs.getInt("VDCALORICO"));
                imp.setPercentualCarboidrato(rs.getInt("VDCARBOIDRATOS"));
                imp.setPercentualProteina(rs.getInt("VDPROTEINAS"));
                imp.setPercentualGordura(rs.getInt("VDGORDURAS_TOTAIS"));
                imp.setPercentualGorduraSaturada(rs.getInt("VDGORDURAS_SATURADAS"));
                imp.setPercentualFibra(rs.getInt("VDFIBRA_ALIMENTAR"));
                imp.setPercentualCalcio(rs.getInt("VDCALCIO"));
                imp.setPercentualFerro(rs.getInt("VDFERRO"));
                imp.setPercentualSodio(rs.getInt("VDSODIO"));
                /*String un;
                switch (Utils.acertarTexto(rs.getString("PORCAO"))) {
                    case "40": un = "ft"
                }*/
                imp.setPorcao(rs.getString("PORCAO_DESC"));
                
                result.add(imp);
            }
        }
        
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            Map<String, String> familia = new HashMap<>();
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n" +
                    "	CODIGOPRE id_familia,\n" +
                    "	CODIGOPLU id_produto\n" +
                    "FROM\n" +
                    "	fl329pre\n" +
                    "order by\n" +
                    "	CODIGOPRE,\n" +
                    "	CODIGOPLU"
            )) {
                while (rs.next()) {
                    familia.put(rs.getString("id_produto"), rs.getString("id_familia"));
                }
            }
            try (ResultSet rs = stm.executeQuery(
                    "declare @loja integer = '" + getLojaOrigem() + "';\n" +
                    "declare @crt integer = 0;\n" +
                    "with div as (\n" +
                    "	select\n" +
                    "		distinct CODIGOPLU,\n" +
                    "		CODIGOENTI,\n" +
                    "		CODIGOENTI + '-' + NUMCGC_CPF id_divisao\n" +
                    "	from\n" +
                    "		fl300div div\n" +
                    "	where\n" +
                    "		ltrim(rtrim(NUMCGC_CPF)) != ''\n" +
                    ")\n" +
                    "select\n" +
                    "	v.codigoloja,\n" +
                    "	p.codigoplu id, \n" +
                    "	case  \n" +
                    "		when p.estc13codi = '' then p.codigoplu  \n" +
                    "		else p.estc13codi \n" +
                    "	end ean,\n" +
                    "   p.estc35desc + ext.COMPDESCRI descricaocompleta,\n" +
                    "	p.estc35desc descricaogondola,\n" +
                    "	p.descreduzi descricaoreduzida,\n" +
                    "	p.dtcadastro,\n" +
                    "	p.ESTC01LINH ativo,\n" +
                    "	p.estc01peso pesavel,\n" +
                    "	coalesce(bal.validade, 0) validade,\n" +
                    "	p.estc03seto merc1, \n" +
                    "	p.estc03grup merc2, \n" +
                    "	p.estc03subg merc3, \n" +
                    "	p.estc03fami merc4, \n" +
                    "	p.estc03subf merc5,\n" +
                    "	est.estn05mrge margem,\n" +
                    "	est.qtd_emb_co qtdembalagemcotacao,\n" +
                    "	est.qtd_emb_vd qtdembalagem,\n" +
                    "	est.tip_emb_vd embalagem,\n" +
                    "	v.vendaatua venda,\n" +
                    "	c.custoliqui custocomimposto,\n" +
                    "	c.CUSTCSIATU custosemimposto,\n" +
                    "	e.estoqueatu estoque,\n" +
                    "	est.estn10maxi estoquemaximo,\n" +
                    "	est.estn10mini estoqueminimo,\n" +
                    "	ncm.cod_ncm ncm,\n" +
                    "	ncm.id_cest cest,\n" +
                    "	est.codtribsai,\n" +
                    "	est.codtribent,\n" +
                    "	pis_s.cstpis pis_cst_s,\n" +
                    "	pis_e.cstpis pis_cst_e,\n" +
                    "	pis_s.nat_rec_pis pis_natrec,\n" +
                    "	coalesce(ext.tipo_item,'') tipo_item,\n" +
                    "	p.CODIGOENTI fabricante,\n" +
                    "	p.comprador,\n" +
                    "	div.id_divisao\n" +
                    "from\n" +
                    "	fl300est p\n" +
                    "	join fl304ven v on \n" +
                    "		p.codigoplu = v.codigoplu and\n" +
                    "		v.CODIGOLOJA = @loja\n" +
                    "	join fl309est e on \n" +
                    "		p.codigoplu = e.codigoplu and \n" +
                    "		v.codigoloja = e.codigoloja\n" +
                    "	join fl303cus c on \n" +
                    "		p.codigoplu = c.codigoplu and\n" +
                    "		v.codigoloja = c.codigoloja\n" +
                    "	join fltabncm_pro ncm on \n" +
                    "		p.codigoplu = ncm.codigoplu\n" +
                    "	left join fltabncm_pis pis_e on\n" +
                    "		ncm.codigoplu = p.codigoplu and\n" +
                    "		ncm.cod_ncm = pis_e.codigo and\n" +
                    "		pis_e.id_opera = 1 and\n" +
                    "		pis_e.ID_CRT = @crt and\n" +
                    "		pis_e.ID_VALI = 0 and\n" +
                    "		pis_e.ID_EXCE = 0\n" +
                    "	left join fltabncm_pis pis_s on\n" +
                    "		ncm.codigoplu = p.codigoplu and\n" +
                    "		ncm.cod_ncm = pis_s.codigo and\n" +
                    "		pis_s.id_opera = 21 and \n" +
                    "		pis_s.ID_CRT = @crt and\n" +
                    "		pis_s.ID_VALI = 0 and\n" +
                    "		pis_s.ID_EXCE = 0\n" +
                    "	left join fl301est est on \n" +
                    "		p.codigoplu = est.codigoplu and\n" +
                    "		est.codigoloja = v.codigoloja\n" +
                    "	left join (\n" +
                    "		select\n" +
                    "			codigoplu,\n" +
                    "			max(diasvalida) validade\n" +
                    "		from\n" +
                    "			fl328bal\n" +
                    "		group by\n" +
                    "			codigoplu\n" +
                    "	) bal on \n" +
                    "		p.codigoplu = bal.codigoplu\n" +
                    "	left join FL300EXT ext on\n" +
                    "           ext.codigoplu = p.codigoplu\n" +
                    "    left join div on\n" +
                    "    	p.CODIGOPLU = div.CODIGOPLU and\n" +
                    "    	p.CODIGOENTI = div.CODIGOENTI\n" +
                    "order by\n" +
                    "	id"
            )) {
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().getProdutosBalanca();
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("id"));
                    
                    String ean = rs.getString("ean");
                    
                    ProdutoBalancaVO bal = produtosBalanca.get(Utils.stringToInt(ean, -2));
                    
                    if (bal != null) {
                        imp.setEan(String.valueOf(bal.getCodigo()));
                        imp.setTipoEmbalagem("U".equals(bal.getPesavel()) ? "UN" : "KG");
                        imp.setQtdEmbalagem(1);
                        imp.seteBalanca(true);
                        imp.setValidade(bal.getValidade());
                    } else {
                        imp.setEan(ean);
                        imp.setTipoEmbalagem(rs.getString("embalagem"));
                        imp.setQtdEmbalagem(1);
                        imp.seteBalanca("S".equals(rs.getString("pesavel")));
                        imp.setValidade(rs.getInt("validade"));                        
                    }
                    
                    imp.setIdFamiliaProduto(familia.get(imp.getImportId()));                    
                    imp.setQtdEmbalagemCotacao(rs.getInt("qtdembalagemcotacao"));
                    imp.setDescricaoCompleta(Utils.acertarTexto(rs.getString("descricaocompleta")));
                    imp.setDescricaoGondola(Utils.acertarTexto(rs.getString("descricaogondola")));
                    imp.setDescricaoReduzida(Utils.acertarTexto(rs.getString("descricaoreduzida")));
                    imp.setSituacaoCadastro("N".equals(rs.getString("ativo")) ? SituacaoCadastro.EXCLUIDO : SituacaoCadastro.ATIVO);
                    imp.setDataCadastro(rs.getDate("dtcadastro"));
                    imp.setCodMercadologico1(rs.getString("merc1"));
                    imp.setCodMercadologico2(rs.getString("merc2"));
                    imp.setCodMercadologico3("1");
                    //imp.setCodMercadologico4(rs.getString("merc4"));
                    //poimp.setCodMercadologico5(rs.getString("merc5"));
                    imp.setCustoComImposto(rs.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(rs.getDouble("custosemimposto"));
                    imp.setMargem(rs.getDouble("margem"));
                    imp.setPrecovenda(rs.getDouble("venda"));
                    imp.setEstoque(rs.getDouble("estoque"));
                    imp.setEstoqueMaximo(rs.getDouble("estoquemaximo"));
                    imp.setEstoqueMinimo(rs.getDouble("estoqueminimo"));
                    imp.setNcm(rs.getString("ncm"));
                    
                    imp.setIcmsDebitoId(rs.getString("codtribsai"));
                    imp.setIcmsDebitoForaEstadoId(rs.getString("codtribsai"));
                    imp.setIcmsDebitoForaEstadoNfId(rs.getString("codtribsai"));
                    imp.setIcmsConsumidorId(rs.getString("codtribsai"));
                    imp.setIcmsCreditoId(rs.getString("codtribent"));
                    imp.setIcmsCreditoForaEstadoId(rs.getString("codtribent"));
                    
                    switch (rs.getString("tipo_item")) {
                        case "02": imp.setTipoProduto(TipoProduto.EMBALAGEM); break;
                        case "07": imp.setTipoProduto(TipoProduto.MATERIAL_USO_E_CONSUMO); break;
                        case "09": imp.setTipoProduto(TipoProduto.SERVICOS); break;
                        case "08": imp.setTipoProduto(TipoProduto.ATIVO_IMOBILIZADO); break;
                        case "99": imp.setTipoProduto(TipoProduto.OUTROS); break;
                    }
                                        
                    imp.setPiscofinsCstCredito(rs.getString("pis_cst_e"));
                    imp.setPiscofinsCstDebito(rs.getString("pis_cst_s"));
                    imp.setPiscofinsNaturezaReceita(rs.getString("pis_natrec"));
                    imp.setCest(rs.getString("cest"));
                    
                    imp.setIdComprador(rs.getString("comprador"));
                    imp.setFornecedorFabricante(rs.getString("fabricante"));
                    imp.setDivisao(rs.getString("id_divisao"));
                    
                    imp.setPautaFiscalId(rs.getString("id"));

                    result.add(imp);
                }
            }
            return result;
        }
    }

    @Override
    public List<PautaFiscalIMP> getPautasFiscais(Set<OpcaoFiscal> opcoes) throws Exception {
        List<PautaFiscalIMP> result = new ArrayList<>();
        
        try (
                Statement st = ConexaoSqlServer.getConexao().createStatement();
                ResultSet rs = st.executeQuery(
                        "declare @loja integer = " + getLojaOrigem() + ";\n" +
                        "with trib as (\n" +
                        "	select distinct\n" +
                        "		ts.CODIGOTRIB id,\n" +
                        "		ts.DESCRICAO,\n" +
                        "		ts.situatribu icms_cst,\n" +
                        "		ts.VALORICM icms_aliq,\n" +
                        "		ts.mrger icms_red\n" +
                        "	from\n" +
                        "		fl301est est\n" +
                        "		join fltribut ts on \n" +
                        "			est.codtribsai = ts.codigotrib and\n" +
                        "			ts.codigoloja = @loja\n" +
                        "	where\n" +
                        "		ts.situatribu = '60'\n" +
                        "	union\n" +
                        "	select distinct\n" +
                        "		te.CODIGOTRIB id,\n" +
                        "		te.descricao,\n" +
                        "		te.situatribu icms_cst,\n" +
                        "		te.VALORICM icms_aliq,\n" +
                        "		te.mrger icms_red\n" +
                        "	from\n" +
                        "		fl301est est\n" +
                        "		join fltribut te on \n" +
                        "			est.codtribent = te.codigotrib and\n" +
                        "			te.codigoloja = @loja\n" +
                        "	where\n" +
                        "		te.situatribu = '60'\n" +
                        ")\n" +
                        "SELECT \n" +
                        "	prod.Codigoplu id,\n" +
                        "	ncm.COD_NCM ncm,\n" +
                        "	ESTA.origem uf,\n" +
                        "	ESTA.iva,\n" +
                        "	esta.pauta,\n" +
                        "	est.codtribent,\n" +
                        "	case trib.icms_red \n" +
                        "		when 0 then 0 \n" +
                        "		else 20 \n" +
                        "	end as icms_cst,\n" +
                        "	trib.icms_aliq,\n" +
                        "	trib.icms_red\n" +
                        "FROM\n" +
                        "	FLGRUSUB_ESTA ESTA\n" +
                        "	INNER JOIN FLTRIBUT TRIB1 ON\n" +
                        "		ESTA.CODTRIBENT=TRIB1.CODIGOTRIB AND\n" +
                        "		TRIB1.CODIGOLOJA=@loja\n" +
                        "	INNER JOIN FLTRIBUT TRIB2 ON\n" +
                        "		ESTA.CODTRIBSAI=TRIB2.CODIGOTRIB AND\n" +
                        "		TRIB2.CODIGOLOJA=@loja\n" +
                        "	join flgrusub_prod prod on\n" +
                        "		esta.id_Grupo = prod.Id_Grupo and\n" +
                        "		prod.Id_Estado = 0\n" +
                        "	join fl301est est on\n" +
                        "		est.CODIGOLOJA = @loja and\n" +
                        "		est.CODIGOPLU = prod.Codigoplu\n" +
                        "	join fltabncm_pro ncm on\n" +
                        "		est.codigoplu = ncm.codigoplu\n" +
                        "	join trib on\n" +
                        "		trib.id = est.codtribent\n" +
                        "where\n" +
                        "	ESTA.origem = 'PB' and\n" +
                        "	ESTA.destino = 'PB' and\n" +
                        "	(esta.iva != 0 or esta.pauta != 0)\n" +
                        "ORDER BY\n" +
                        "	ESTA.ORIGEM,ESTA.DESTINO"
                )
                ) {
            while (rs.next()) {
                PautaFiscalIMP imp = new PautaFiscalIMP();
                
                imp.setId(rs.getString("id"));
                imp.setNcm(rs.getString("ncm"));
                imp.setUf(rs.getString("uf"));
                if (rs.getDouble("iva") > 0) {
                    imp.setIva(rs.getDouble("iva"));
                    imp.setTipoIva(TipoIva.PERCENTUAL);
                } else {
                    imp.setIva(rs.getDouble("pauta"));
                    imp.setTipoIva(TipoIva.VALOR);
                }
                
                AliquotaVO a = new AliquotaVO(rs.getInt("icms_cst"), rs.getDouble("icms_aliq"), rs.getDouble("icms_red"));
                imp.setAliquotaCredito(a);
                imp.setAliquotaCreditoForaEstado(a);
                imp.setAliquotaDebito(a);
                imp.setAliquotaDebitoForaEstado(a);
                
                result.add(imp);
            }
        }
        
        return result;
    }
    
    @Override
    public List<OfertaIMP> getOfertas(Date dataTermino) throws Exception {
        List<OfertaIMP> result = new ArrayList<>();
        
        try (
                Statement st = ConexaoSqlServer.getConexao().createStatement();
                ResultSet rs = st.executeQuery(
                        "select\n" +
                        "	o.CODIGOPLU id_produto,\n" +
                        "	o.DATINICOFE datainicio,\n" +
                        "	o.DATFINAOFE datatermino,\n" +
                        "	o.VENDAOFERT precooferta,\n" +
                        "	o.VENDAATUA preconormal\n" +
                        "from\n" +
                        "	HRTECH.dbo.fl304ven o\n" +
                        "where\n" +
                        "	codigoloja=" + getLojaOrigem() + " and DATFINAOFE >= cast(current_timestamp as date)"
                )
                ) {
            while (rs.next()) {
                OfertaIMP imp = new OfertaIMP();
                
                imp.setIdProduto(rs.getString("id_produto"));
                imp.setDataInicio(rs.getDate("datainicio"));
                imp.setDataFim(rs.getDate("datatermino"));
                imp.setPrecoOferta(rs.getDouble("precooferta"));
                imp.setPrecoNormal(rs.getDouble("preconormal"));
                
                result.add(imp);
            }
        }
        
        return result;
    }

    @Override
    public List<CompradorIMP> getCompradores() throws Exception {
        List<CompradorIMP> result= new ArrayList<>();
        
        try (
                Statement st = ConexaoSqlServer.getConexao().createStatement();
                ResultSet rs = st.executeQuery(
                        "select\n" +
                        "	cp.CODIGOENTI id,\n" +
                        "	ent.NOMEENTIDA nome,\n" +
                        "	ent.NOMAPELIDO apelido\n" +
                        "from\n" +
                        "	FL010COM cp\n" +
                        "	join FLCGCCPF ent on\n" +
                        "		cp.ID_ENTIDADE = ent.ID_ENTIDADE\n" +
                        "order by\n" +
                        "	cp.CODIGOENTI"
                )
        ) {
            while (rs.next()) {
                result.add(new CompradorIMP(rs.getString("id"), rs.getString("nome")));
            }
        }
        
        return result;
    }
    
    

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.MANTER_CODIGO_MERCADOLOGICO,
                OpcaoProduto.FAMILIA,
                OpcaoProduto.TROCA,
                OpcaoProduto.FAMILIA_PRODUTO,
                OpcaoProduto.DIVISAO,
                OpcaoProduto.DIVISAO_PRODUTO,
                OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.DATA_CADASTRO,
                OpcaoProduto.ATIVO,
                OpcaoProduto.DESCONTINUADO,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.VALIDADE,
                OpcaoProduto.MARGEM,
                OpcaoProduto.QTD_EMBALAGEM_COTACAO,
                OpcaoProduto.QTD_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.PRECO,
                OpcaoProduto.CUSTO,
                OpcaoProduto.OFERTA,
                OpcaoProduto.ESTOQUE_MINIMO,
                OpcaoProduto.ESTOQUE_MAXIMO,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.NCM,
                OpcaoProduto.CEST,
                OpcaoProduto.ICMS,
                OpcaoProduto.ICMS_CONSUMIDOR,
                OpcaoProduto.ICMS_SAIDA,
                OpcaoProduto.ICMS_SAIDA_FORA_ESTADO,
                OpcaoProduto.ICMS_SAIDA_NF,
                OpcaoProduto.ICMS_ENTRADA,
                OpcaoProduto.ICMS_ENTRADA_FORA_ESTADO,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.NATUREZA_RECEITA,
                OpcaoProduto.TIPO_PRODUTO,
                OpcaoProduto.COMPRADOR,
                OpcaoProduto.COMPRADOR_PRODUTO,
                OpcaoProduto.FABRICANTE,
                OpcaoProduto.NUTRICIONAL,
                OpcaoProduto.PAUTA_FISCAL,
                OpcaoProduto.PAUTA_FISCAL_PRODUTO,
                OpcaoProduto.ASSOCIADO
        ));
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        enderecosFOR = null;
        enderecosFCO = null;
        contatosFornecedor = null;
        
        List<FornecedorIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "	f.codigoenti id_fornecedor,\n" +
                    "	f.datusucada datacadastro,\n" +
                    "	cpf.nomeentida razao,\n" +
                    "	cpf.nomapelido fantasia,\n" +
                    "	cpf.numcgc_cpf cnpj,\n" +
                    "	cpf.codinsc_rg rgie,\n" +
                    "   f.FORC01TIOP ativo,\n" +
                    "	cpf.tipempresa tipo,\n" +
                    "	coalesce(cpf.microempre, '') microempre,\n" +
                    "	cpf.datanascim datanascimento,\n" +
                    "	coalesce(cpf.codcepcome,'') cep,\n" +
                    "	coalesce(cpf.codcepcobr,'') cep_cob,\n" +
                    "	cpf.compcomerc numero,\n" +
                    "	f.forn02visi prazovisita,\n" +
                    "	f.forn02pent prazoentrega,	\n" +
                    "	f.diasemanas prazoseguranca,\n" +
                    "	f.prod_rural produtorrural,\n" +
                    "	f.possui_nfe,\n" +
                    "	coalesce(pg.NOMCONDPGT,'') condicaopagamento,\n" +
                    "	coalesce(f.RECEB_S_PED, 1) recebe_nfe_s_ped,\n" +
                    "	tel.TELEFONE01 fone1,\n" +
                    "	tel.TELEFONE02 fone2,\n" +
                    "	tel.TELEFAX01 fax,\n" +
                    "	tel.TELECELU celular,\n" +
                    "   (select top 1 email from FL821EMA where codigoenti = f.codigoenti) email\n" +
                    "from \n" +
                    "	FL800FOR f\n" +
                    "	left join flcgccpf cpf on \n" +
                    "		f.id_entidade = cpf.id_entidade\n" +
                    "	left join flcondpg pg on \n" +
                    "		f.codcondpgt = pg.codcondpgt\n" +
                    "	left join fltelefo_cad tel on \n" +
                    "		f.codigoenti = tel.id_cadastro and\n" +
                    "		tel.tp_cadastro = 'FOR'\n" +
                    "where\n" +
                    "	1 = 1 or\n" +
                    "	f.codigoenti in (select distinct CODIGOENTI from FL700FIN where tipolancam = 'P')\n" +
                    "order by\n" +
                    "	f.codigoenti"
            )) {                
                String msg = "Gerando lista de importação de fornecedores...";
                int cont = 1;                
                while (rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("id_fornecedor"));
                    imp.setDatacadastro(rs.getDate("datacadastro"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setIe_rg(rs.getString("rgie"));
                    imp.setCnpj_cpf(rs.getString("cnpj"));
                    imp.setPrazoVisita(StringUtils.toInt(rs.getString("prazovisita")));
                    imp.setPrazoEntrega(StringUtils.toInt(rs.getString("prazoentrega")));
                    imp.setPrazoSeguranca(StringUtils.toInt(rs.getString("prazoseguranca")));
                    switch (rs.getString("microempre")) {
                        case "S": imp.setTipoEmpresa(TipoEmpresa.ME_SIMPLES); break;
                        //case "N": imp.setTipoEmpresa(TipoEmpresa.LUCRO_REAL); break;
                        default: imp.setTipoEmpresa(TipoEmpresa.LUCRO_REAL); break;
                    }
                    if (rs.getInt("produtorrural") == 1) {
                        imp.setProdutorRural();
                    }
                    imp.setEmiteNfe(rs.getInt("possui_nfe") == 1);

                    String pagamento[] = rs.getString("condicaopagamento").split("/");
                    for (String pag : pagamento) {
                        imp.addCondicaoPagamento(Utils.stringToInt(pag.trim()));
                    }
                    
                    Endereco end = obterEnderecoFOR(rs.getString("id_fornecedor"), rs.getString("cep"));
                    if (end == null) {
                        //end = obterEnderecoHRCep(rs.getString("cep"));
                        if (end != null) {
                            end.numero = rs.getString("numero");
                        } else {
                            LOG.log(Level.WARNING, "Fornecedor sem endereço: {0} - {1}", new Object[]{imp.getImportId(), imp.getRazao()});
                        }
                    }                    
                    
                    if (end != null) {
                        imp.setEndereco(end.logradouro);
                        imp.setNumero(end.numero);
                        imp.setBairro(end.bairro);
                        imp.setMunicipio(end.cidade);
                        imp.setUf(end.estado);
                        imp.setCep(end.cep);
                    }
                    
                    {
                        Endereco end2 = obterEnderecoFCO(rs.getString("id_fornecedor"), rs.getString("cep_cob"));
                        if (end2 != null) {
                            end = end2;
                        }
                    }
                    
                    if (end != null) {
                        imp.setCob_endereco(end.logradouro);
                        imp.setCob_numero(end.numero);
                        imp.setCob_bairro(end.bairro);
                        imp.setCob_municipio(end.cidade);
                        imp.setCob_uf(end.estado);
                        imp.setCob_cep(end.cep);
                    }
                    
                    imp.setPermiteNfSemPedido(rs.getInt("recebe_nfe_s_ped") == 1);
                    
                    imp.setTel_principal(rs.getString("fone1"));
                    imp.addTelefone("FONE2", rs.getString("fone2"));
                    imp.addTelefone("FAX", rs.getString("fax"));
                    imp.addCelular("CELULAR", rs.getString("celular"));
                    imp.addEmail("EMAIL", rs.getString("email"), TipoContato.NFE);
                    
                    List<ContatoFornecedor> cts = getContatosFornecedor(imp.getImportId());
                    if (cts != null) {
                        for (ContatoFornecedor cf: cts) {
                            imp.addContato(cf.nome, cf.telefone, "", TipoContato.COMERCIAL, cf.email);
                        }
                    }

                    result.add(imp);
                    
                    ProgressBar.setStatus(msg + cont);
                    cont++;
                }
            }
        }
        
        if (enderecosFOR != null) enderecosFOR.clear();
        enderecosFOR = null;
        if (enderecosFCO != null) enderecosFCO.clear();
        enderecosFCO = null;
        if (contatosFornecedor != null) contatosFornecedor.clear();
        contatosFornecedor = null;
        
        return result;
    }
    
    private Map<String, List<ContatoFornecedor>> contatosFornecedor;
    private List<ContatoFornecedor> getContatosFornecedor(String idFornecedor) throws Exception {
        if (contatosFornecedor == null) {
            try (Statement st = ConexaoSqlServer.getConexao().createStatement()) {
                try (ResultSet rs = st.executeQuery(
                        "select distinct\n" +
                        "    codigoenti id_cliente,\n" +
                        "    depto,\n" +
                        "    funcao,\n" +
                        "    nome,\n" +
                        "    telefone,\n" +
                        "    email\n" +
                        "from\n" +
                        "    FL809FOR\n" +
                        "order by\n" +
                        "    id_cliente"
                )) {
                    contatosFornecedor = new HashMap<>();
                    while (rs.next()) {
                        List<ContatoFornecedor> cont = contatosFornecedor.get(rs.getString("id_cliente"));
                        if (cont == null) {
                            cont = new ArrayList<>();
                            contatosFornecedor.put(rs.getString("id_cliente"), cont);
                        }
                        ContatoFornecedor ct = new ContatoFornecedor();
                        ct.id_cliente = rs.getString("id_cliente");
                        ct.depto = rs.getString("depto");
                        ct.funcao = rs.getString("funcao");
                        ct.nome = rs.getString("nome");
                        ct.telefone = rs.getString("telefone");
                        ct.email = rs.getString("email");
                        cont.add(ct);
                    }
                }
            }
        }
        return contatosFornecedor.get(idFornecedor);
    }
    
    private class ContatoFornecedor {
        String id_cliente;
        String depto;
        String funcao;
        String nome;
        String telefone;
        String email;
    }
    
    private MultiMap<String, Endereco> enderecosFOR;
    private Endereco obterEnderecoFOR(String codigoEntidade, String codigoCep) throws Exception {
        if (enderecosFOR == null) {
            try (Statement st = ConexaoSqlServer.getConexao().createStatement()) {
                try (ResultSet rs = st.executeQuery(
                    "select\n" +
                    "    cp.codigoenti,\n" +
                    "    cp.titulo,\n" +
                    "    cp.logradouro,\n" +
                    "    cpf.compcomerc numero,\n" +
                    "    cp.bairro,\n" +
                    "    cp.cidade,\n" +
                    "    cp.estado,\n" +
                    "    cpf.codcepcome cep\n" +
                    "from\n" +
                    "    fl423cep cp\n" +
                    "    join fl800for f on\n" +
                    "        f.codigoenti = cp.codigoenti\n" +
                    "    join flcgccpf cpf on \n" +
                    "        f.id_entidade = cpf.id_entidade\n" +
                    "where\n" +
                    "    cp.tipocadast = ('FOR')"
                )) {
                    enderecosFOR = new MultiMap<>();
                    while (rs.next()) {
                        Endereco e = new Endereco();
                        e.logradouro = rs.getString("titulo") + " " + rs.getString("logradouro");
                        e.numero = rs.getString("numero");
                        e.bairro = rs.getString("bairro");
                        e.cidade = rs.getString("cidade");
                        e.estado = rs.getString("estado");
                        e.cep = rs.getString("cep");
                        enderecosFOR.put(e, rs.getString("codigoenti"), e.cep);
                    }
                }
            }            
        }
        return enderecosFOR.get(codigoEntidade, codigoCep);
    }
    
    private MultiMap<String, Endereco> enderecosFCO;
    private Endereco obterEnderecoFCO(String codigoEntidade, String codigoCep) throws Exception {
        if (enderecosFCO == null) {
            try (Statement st = ConexaoSqlServer.getConexao().createStatement()) {
                try (ResultSet rs = st.executeQuery(
                    "select\n" +
                    "    cp.codigoenti,\n" +
                    "    cp.titulo,\n" +
                    "    cp.logradouro,\n" +
                    "    cpf.compcomerc numero,\n" +
                    "    cp.bairro,\n" +
                    "    cp.cidade,\n" +
                    "    cp.estado,\n" +
                    "    cpf.codcepcome cep\n" +
                    "from\n" +
                    "    fl423cep cp\n" +
                    "    join fl800for f on\n" +
                    "        f.codigoenti = cp.codigoenti\n" +
                    "    join flcgccpf cpf on \n" +
                    "        f.id_entidade = cpf.id_entidade\n" +
                    "where\n" +
                    "    cp.tipocadast = ('FCO')"
                )) {
                    enderecosFCO = new MultiMap<>();
                    while (rs.next()) {
                        Endereco e = new Endereco();
                        e.logradouro = rs.getString("titulo") + " " + rs.getString("logradouro");
                        e.numero = rs.getString("numero");
                        e.bairro = rs.getString("bairro");
                        e.cidade = rs.getString("cidade");
                        e.estado = rs.getString("estado");
                        e.cep = rs.getString("cep");
                        enderecosFCO.put(e, rs.getString("codigoenti"), e.cep);
                    }
                }
            }
        }
        return enderecosFCO.get(codigoEntidade, codigoCep);
    }
    
    private Endereco obterEnderecoHRCep(String codigoCep) throws Exception {
        try (Statement st = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = st.executeQuery(
                "select top 1\n" +
                "    titulo,\n" +
                "    LOGRADOURO logradouro,\n" +
                "    bairro,\n" +
                "    cidade,\n" +
                "    estado,\n" +
                "    CODIGOCEP cep\n" +
                "from\n" +
                "    dbo.FL423CEP\n" +
                "    where\n" +
                "        ltrim(rtrim(coalesce(codigocep,''))) != '' and\n" +
                "        codigocep = '" + codigoCep + "'"
            )) {
                if (rs.next()) {
                    Endereco e = new Endereco();
                    e.logradouro = rs.getString("titulo") + " " + rs.getString("logradouro");
                    e.numero = rs.getString("titulo");
                    e.complemento = rs.getString("complemento");
                    e.bairro = rs.getString("bairro");
                    e.cidade = rs.getString("cidade");
                    e.estado = rs.getString("estado");
                    e.cep = rs.getString("cep");
                    return e;
                }
            }
        }
        return null;
    }

    /*
     Este método foi copiado para trazer a importação de funcionários
     que está em uma tabela especifíca no HRTech. Pois é necessário
     para a importação de contas a pagar dos funcionários. Após usar o mesmo,
     comentar este método e descomentar o método principal getFornecedores()
     @Override
     public List<FornecedorIMP> getFornecedores() throws Exception {
     List<FornecedorIMP> result = new ArrayList<>();
     try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
     try (ResultSet rs = stm.executeQuery(
     "select \n" +
     "	'U' + fun.codigoenti id_fornecedor,\n" +
     "	getdate() datacadastro,\n" +
     "	cpf.nomeentida razao,\n" +
     "	cpf.nomapelido fantasia,\n" +
     "	cpf.codinsc_rg rgie,\n" +
     "	cpf.numcgc_cpf cnpj,\n" +
     "	cpf.tipempresa tipo,\n" +
     "	cpf.datanascim datanascimento,\n" +
     "	cpf.codceplent cep,\n" +
     "	cpf.complocent numero,\n" +
     "	0 prazovisita,\n" +
     "	0 prazoentrega,\n" +
     "	0 condicaopagamento,\n" +
     "	0 diasemanas,\n" +
     "	0 produtorural,\n" +
     "	ltrim(cep.titulo + ' ' + cep.logradouro) endereco,\n" +
     "	cep.bairro,\n" +
     "	cep.cidade,\n" +
     "	cep.estado,\n" +
     "	0 telefone\n" +
     "from \n" +
     "	FL040FUN fun\n" +
     "join \n" +
     "	flcgccpf cpf on (fun.codcgccpfs = cpf.codigoenti)\n" +
     "left join fl423cep cep on (fun.codigoenti = cep.codigoenti) and\n" +
     "	cep.tipocadast = 'FUN'")) {
     while (rs.next()) {
     FornecedorIMP imp = new FornecedorIMP();
     imp.setImportSistema(getSistema());
     imp.setImportLoja(getLojaOrigem());
     imp.setImportId(rs.getString("id_fornecedor"));
     imp.setDatacadastro(rs.getDate("datacadastro"));
     imp.setRazao(Utils.acertarTexto(rs.getString("razao")));
     imp.setFantasia(Utils.acertarTexto(rs.getString("fantasia")));
     imp.setIe_rg(rs.getString("rgie"));
     imp.setCnpj_cpf(rs.getString("cnpj"));
     imp.setTipo_inscricao("J".equals(rs.getString("tipo")) ? TipoInscricao.JURIDICA : TipoInscricao.FISICA);
     imp.setCep(rs.getString("cep"));
     imp.setNumero(rs.getString("numero"));
     imp.setPrazoVisita(rs.getInt("prazovisita"));
     imp.setPrazoEntrega(rs.getInt("prazoentrega"));

     String pagamento[] = rs.getString("condicaopagamento").split("/");
     for(String pag : pagamento) {
     imp.setCondicaoPagamento(Utils.stringToInt(pag.trim()));
     }
     if (rs.getInt("produtorural") == 1) {
     imp.setProdutorRural();
     }
     imp.setEndereco(rs.getString("endereco"));
     imp.setBairro(rs.getString("bairro"));
     imp.setMunicipio(rs.getString("cidade"));
     imp.setUf(rs.getString("estado"));
     imp.setTel_principal(rs.getString("telefone"));
     imp.copiarEnderecoParaCobranca();

     result.add(imp);
     }
     }
     }
     return result;
     }*/
    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                        "with div as (\n" +
                        "	select\n" +
                        "		distinct CODIGOPLU,\n" +
                        "		CODIGOENTI,\n" +
                        "		CODIGOENTI + '-' + NUMCGC_CPF id_divisao\n" +
                        "	from\n" +
                        "		fl300div div\n" +
                        "	where\n" +
                        "		ltrim(rtrim(NUMCGC_CPF)) != ''\n" +
                        ")\n" +
                        "select \n" +
                        "	f.codigoenti id_fornecedor,\n" +
                        "	f.codigoplu id_produto,\n" +
                        "	coalesce(f.dataaltera, '') dataalteracao,\n" +
                        "	coalesce(f.qtd_emb_co, 1) qtdcotacao,\n" +
                        "	coalesce(f.referencia, '') referencia,\n" +
                        "	div.id_divisao\n" +
                        "from \n" +
                        "	FL324FOR f\n" +
                        "	left join div on\n" +
                        "		f.CODIGOENTI = div.codigoenti and\n" +
                        "		f.CODIGOPLU = div.codigoplu\n" +
                        "where\n" +
                        "	f.codigoenti != '' and\n" +
                        "	f.codigoenti not in ('000001') and\n" +
                        "	rtrim(ltrim(f.referencia)) != ''\n" +
                        "order by\n" +
                        "	f.codigoenti, f.codigoplu"
            )) {
                while (rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdFornecedor(rs.getString("id_fornecedor"));
                    imp.setIdProduto(rs.getString("id_produto"));
                    imp.setQtdEmbalagem(rs.getDouble("qtdcotacao"));
                    imp.setDataAlteracao(rs.getDate("dataalteracao"));
                    imp.setCodigoExterno(rs.getString("referencia"));
                    imp.setIdDivisaoFornecedor(rs.getString("id_divisao"));

                    result.add(imp);
                }
            }
        }
        return result;
    }
    
    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "with endcom as (\n" +
                    "select\n" +
                    "	a.*\n" +
                    "from\n" +
                    "	fl423cep a\n" +
                    "	join (\n" +
                    "		select\n" +
                    "			codigoenti,\n" +
                    "			min(codigocep) codigocep\n" +
                    "		from\n" +
                    "			fl423cep\n" +
                    "		where\n" +
                    "			tipocadast = ('CLI')\n" +
                    "		group by\n" +
                    "			codigoenti\n" +
                    "	) b on\n" +
                    "		a.codigoenti = b.codigoenti and\n" +
                    "		a.codigocep = b.codigocep\n" +
                    "where\n" +
                    "	tipocadast = ('CLI')\n" +
                    ")\n" +
                    "select \n" +
                    "    c.id_cliente id,\n" +
                    "    cpf.nomeentida razao,\n" +
                    "    cpf.nomapelido fantasia,\n" +
                    "    cpf.codinsc_rg rgie,\n" +
                    "    cpf.numcgc_cpf cnpj,\n" +
                    "    cpf.tipempresa tipo,\n" +
                    "    case cpf.microempre when 'S' then 1 else 0 end microempresa,\n" +
                    "    cpf.datanascim datanascimento,\n" +
                    "    c.clin12limi limite,\n" +
                    "    c.clic01stat situacao,\n" +
                    "    c.codigosexo sexo,\n" +
                    "    c.estadocivi estadocivil,\n" +
                    "    c.datacadast datacadastro,\n" +
                    "    ec.TITULO,\n" +
                    "    ec.LOGRADOURO endereco,\n" +
                    "    cpf.compreside numero,\n" +
                    "    ec.BAIRRO,\n" +
                    "    ec.CIDADE,\n" +
                    "    ec.ESTADO,\n" +
                    "    ec.codigocep cep,\n" +
                    "    tel.TELEFONE01 fone1,\n" +
                    "    tel.TELEFONE02 fone2,\n" +
                    "    tel.TELEFAX01 fax,\n" +
                    "    tel.TELECELU celular\n" +
                    "from\n" +
                    "    FL400CLI c \n" +
                    "    left join flcgccpf cpf on\n" +
                    "        c.ID_ENTIDADE = cpf.ID_ENTIDADE\n" +
                    "    left join endcom ec on\n" +
                    "        c.codigoenti = ec.CODIGOENTI\n" +
                    "    left join\n" +
                    "        fltelefo_cad tel on \n" +
                    "            c.id_cliente = tel.id_cadastro and\n" +
                    "            tel.tp_cadastro = 'CLI'\n" +
                    "order by\n" +
                    "    c.codigoenti")) {
                
                while (rs.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    String id;
                    try {
                        id = String.valueOf(Integer.parseInt(rs.getString("id")));
                    } catch (NumberFormatException ex) {
                        id = rs.getString("id");
                    }
                    imp.setId(id);
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setInscricaoestadual(rs.getString("rgie"));
                    imp.setCnpj(rs.getString("cnpj"));
                    imp.setDataNascimento(rs.getDate("datanascimento"));
                    imp.setValorLimite(rs.getDouble("limite"));
                    imp.setAtivo(Utils.stringToInt(rs.getString("situacao")) <= 1);
                    imp.setSexo("F".equals(rs.getString("sexo")) ? TipoSexo.FEMININO : TipoSexo.MASCULINO);
                    switch (Utils.stringToInt(rs.getString("estadocivil"))) {
                        case 0: imp.setEstadoCivil(TipoEstadoCivil.CASADO); break;
                        case 1: imp.setEstadoCivil(TipoEstadoCivil.SOLTEIRO); break;
                        case 2: imp.setEstadoCivil(TipoEstadoCivil.DIVORCIADO); break;
                        case 3: imp.setEstadoCivil(TipoEstadoCivil.DIVORCIADO); break;
                        case 4: imp.setEstadoCivil(TipoEstadoCivil.VIUVO); break;
                        case 5: imp.setEstadoCivil(TipoEstadoCivil.OUTROS); break;
                        default: imp.setEstadoCivil(TipoEstadoCivil.NAO_INFORMADO); break;
                    }
                    imp.setDataCadastro(rs.getDate("datacadastro"));
                    imp.setEndereco((rs.getString("TITULO") + " " + rs.getString("endereco")).trim());
                    imp.setNumero(rs.getString("numero"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("estado"));
                    imp.setCep(rs.getString("cep"));
                    imp.setTelefone(rs.getString("fone1"));
                    imp.setFax(rs.getString("fax"));
                    imp.setCelular(rs.getString("celular"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "	cr.CODIGOLOJA id_loja,\n" +
                    "	cr.NUMEROLANC id,\n" +
                    "	cr.DATEMISSAO,\n" +
                    "	cr.NOTAFISCAL numerocupom,\n" +
                    "	cr.VLRTOTALNF valor,\n" +
                    "	cr.HISTORICO observacao,\n" +
                    "	c.ID_CLIENTE,\n" +
                    "	cr.DATVENCIME vencimento,\n" +
                    "	cpf.NUMCGC_CPF cpf\n" +
                    "from\n" +
                    "	FL700FIN cr\n" +
                    "	join FL400CLI c on\n" +
                    "		cr.ID_CLIENTE = c.ID_CLIENTE \n" +
                    "	join FLCGCCPF cpf on\n" +
                    "		c.ID_ENTIDADE = cpf.ID_ENTIDADE\n" +
                    "where\n" +
                    "	cr.CODIGOLOJA = '" + getLojaOrigem() + "' and\n" +
                    "   cr.TIPO_PAGTO in ('VLE','BOL','A','   ','DH') and\n" +
                    "	cr.FORMAPAGTO in ('A', 'C') and\n" +
                    "	cr.TIPOLANCAM = 'R' and\n" +
                    "	cr.TIPOCADAST = 'C' and\n" +
                    "	cr.DATPAGTO <= '2000-01-01' AND \n" +
                    "	cr.DATENTRADA >= '2019-01-01'\n" +
                    "order by\n" +
                    "	cr.NUMEROLANC")) {
                
                while (rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    
                    imp.setId(rs.getString("id"));
                    imp.setDataEmissao(rs.getDate("DATEMISSAO"));
                    imp.setNumeroCupom(rs.getString("numerocupom"));
                    imp.setValor(rs.getDouble("valor"));
                    imp.setObservacao(rs.getString("observacao"));
                    imp.setIdCliente(rs.getString("ID_CLIENTE"));
                    imp.setDataVencimento(rs.getDate("vencimento"));
                    imp.setCnpjCliente(rs.getString("cpf"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<CreditoRotativoPagamentoAgrupadoIMP> getCreditoRotativoPagamentoAgrupado() throws Exception {
        List<CreditoRotativoPagamentoAgrupadoIMP> result = new ArrayList<>();
        
        try (Statement st = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = st.executeQuery(
                    "SELECT\n" +
                    "	FL404CON.id_cliente,\n" +
                    "	sum(FL404CON.VALORVENDA * (case when FL404CON.operacao = 'J' then -1 else 1 end)) total\n" +
                    "FROM\n" +
                    "	fl404con\n" +
                    //"where id_cliente = 165\n" +
                    "group by\n" +
                    "	id_cliente"
            )) {
                while (rs.next()) {
                    CreditoRotativoPagamentoAgrupadoIMP imp = new CreditoRotativoPagamentoAgrupadoIMP();
                    String idCliente;
                    try {
                        idCliente = String.valueOf(Integer.parseInt(rs.getString("id_cliente")));
                    } catch (NumberFormatException ex) {
                        idCliente = rs.getString("id_cliente");
                    }                  
                    imp.setIdCliente(idCliente);
                    imp.setValor(rs.getDouble("total"));
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<ContaReceberIMP> getContasReceber(Set<OpcaoContaReceber> opt) throws Exception {
        List<ContaReceberIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "	numerolanc id,\n" +
                    "	codigoenti idfornecedor,\n" +
                    "	notafiscal,\n" +
                    "	parcela,\n" +
                    "	datlancame,\n" +
                    "	datemissao dtemissao,\n" +
                    "   datvencime vencimento,\n" +        
                    "	datentrada,\n" +
                    "	datefetiva,\n" +
                    "	vlrtotalnf valor,\n" +
                    "	historico\n" +
                    "from\n" +
                    "	fl700fin\n" +
                    "where\n" +
                    "	tipo_pagto in ('CHQ', 'VLE', 'BOL', 'A', '   ', 'DH') \n" +
                    "	and codigoloja = '" + getLojaOrigem() + "'\n" +
                    "	and tipolancam in ('A', 'R') and tipocadast = 'F'\n" +        
                    "	and datpagto = '1900-01-01 00:00:00'")) {
                while(rs.next()) {
                    ContaReceberIMP imp = new ContaReceberIMP();
                    
                    imp.setId(rs.getString("id"));
                    imp.setDataEmissao(rs.getDate("dtemissao"));
                    imp.setDataVencimento(rs.getDate("vencimento"));
                    imp.setValor(rs.getDouble("valor"));
                    imp.setObservacao(rs.getString("historico"));
                    imp.setIdFornecedor(rs.getString("idfornecedor"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<ChequeIMP> getCheques() throws Exception {
        List<ChequeIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "	numerolanc id,\n" +
                    "	codigoenti idcliente,\n" +
                    "	notafiscal,\n" +
                    "	parcela,\n" +
                    "	datlancame,\n" +
                    "	datemissao dtemissao,\n" +
                    "	datentrada,\n" +
                    "	datefetiva,\n" +
                    "	vlrtotalnf,\n" +
                    "	historico\n" +
                    "from\n" +
                    "	fl700fin\n" +
                    "where\n" +
                    "	tipo_pagto = 'CHQ'\n" +
                    "	and codigoloja = '" + getLojaOrigem() + "'\n" +
                    "	and tipolancam = 'R'\n" +
                    "	and datpagto = '1900-01-01 00:00:00'")) {
                while (rs.next()) {
                    ChequeIMP imp = new ChequeIMP();
                    
                    imp.setId(rs.getString("id"));
                    imp.setNumeroCheque(rs.getString("notafiscal"));
                    imp.setValor(rs.getDouble("vlrtotalnf"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    /*
     O código do fornecedor foi alterado para trazer os títulos de funcionários
     que está em uma tabela especifica para funcionários. Antes, é necessário importar
     os funcionários como fornecedor.
     */
    @Override
    public List<ContaPagarIMP> getContasPagar() throws Exception {
        List<ContaPagarIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "	numerolanc id,\n" +
                    "	case \n" +
                    "		when tipocadast = 'U' \n" +
                    "		then 'U' + codigoenti \n" +
                    "		else codigoenti\n" +
                    "	end idfornecedor,\n" +
                    "	notafiscal documento,\n" +
                    "	parcela,\n" +
                    "	datemissao emissao,\n" +
                    "	datvencime vencimento,\n" +
                    "	vlrtotalnf valor,\n" +
                    "	historico observacao,\n" +
                    "	cast(datpagto as date) pagamento\n" +
                    "from\n" +
                    "	FL700FIN\n" +
                    "where\n" +
                    "	FL700FIN.numerolanc > 0\n" +
                    "	AND fl700FIN.CODIGOLOJA = " + getLojaOrigem() + "\n" +
                    "	AND FL700FIN.TIPOLANCAM = 'P'\n" +
                    "	AND FL700FIN.DATPAGTO <= '19000101'\n" +
                    "	AND ISNULL(fl700fin.tipo_pagto,'') >= ' '\n" +
                    "order by\n" +
                    "	emissao")) {
                while (rs.next()) {
                    ContaPagarIMP imp = new ContaPagarIMP();
                    imp.setId(rs.getString("id"));
                    imp.setNumeroDocumento(rs.getString("documento"));
                    imp.setDataEmissao(rs.getDate("emissao"));
                    imp.setDataEntrada(rs.getDate("emissao"));
                    imp.setIdFornecedor(rs.getString("idfornecedor"));
                    imp.setValor(rs.getDouble("valor"));                  
                    imp.setObservacao(rs.getString("observacao"));
                    imp.addVencimento(rs.getDate("vencimento"), imp.getValor());
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public Iterator<VendaIMP> getVendaIterator() throws Exception {
        return new VendaIterator(getLojaOrigem(), dataInicioVenda, dataTerminoVenda);
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        return new VendaItemIterator(getLojaOrigem(), dataInicioVenda, dataTerminoVenda);
    }

    private Date dataInicioVenda;
    private Date dataTerminoVenda;

    public void setDataInicioVenda(Date dataInicioVenda) {
        this.dataInicioVenda = dataInicioVenda;
    }

    public void setDataTerminoVenda(Date dataTerminoVenda) {
        this.dataTerminoVenda = dataTerminoVenda;
    }

    private String getlojaorigem() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private static class VendaIterator implements Iterator<VendaIMP> {

        public final static SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");

        private final Statement stm = ConexaoSqlServer.getConexao().createStatement();
        private final ResultSet rst;
        private final String sql;
        private VendaIMP next;
        private final Set<String> uk = new HashSet<>();

        private void obterNext() {
            try {
                SimpleDateFormat timestampDate = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat timestamp = new SimpleDateFormat("HHmm");
                if (next == null) {
                    if (rst.next()) {
                        next = new VendaIMP();
                        String id = rst.getString("id");
                        if (!uk.add(id)) {
                            LOG.log(Level.WARNING, "Venda {0} já existe na listagem", id);
                        }
                        next.setId(id);
                        next.setNumeroCupom(Utils.stringToInt(rst.getString("coo")));
                        next.setEcf(Utils.stringToInt(rst.getString("ecf")));
                        next.setData(rst.getDate("data"));
                        next.setIdClientePreferencial(rst.getString("idcliente"));
                        String horaInicio = "".equals(rst.getString("horainicio").trim()) ? "0000" : rst.getString("horainicio");
                        String horaTermino = "".equals(rst.getString("horafim").trim()) ? "0000" : rst.getString("horafim");
                        next.setHoraInicio(timestamp.parse(horaInicio));
                        next.setHoraTermino(timestamp.parse(horaTermino));
                        next.setSubTotalImpressora(rst.getDouble("subtotalimpressora"));
                        next.setCpf(rst.getString("cnpj"));
                        next.setNomeCliente(rst.getString("razao"));
                        String endereco
                                = Utils.acertarTexto(rst.getString("endereco")) + ","
                                + Utils.acertarTexto(rst.getString("numero")) + ","
                                + Utils.acertarTexto(rst.getString("complemento")) + ","
                                + Utils.acertarTexto(rst.getString("bairro")) + ","
                                + Utils.acertarTexto(rst.getString("cidade")) + "-"
                                + Utils.acertarTexto(rst.getString("estado")) + ","
                                + Utils.acertarTexto(rst.getString("cep"));
                        next.setEnderecoCliente(endereco);
                        next.setChaveCfe(rst.getString("chavenfe"));
                    }
                }
            } catch (SQLException | ParseException ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {
            this.sql
                    = 
                    "declare @loja integer = " + idLojaCliente + ";\n" +
                    "declare @dataini date = '" + FORMAT.format(dataInicio) + "';\n" +
                    "declare @datafim date = '" + FORMAT.format(dataTermino) + "';\n" +
                    "with vend as (\n" +
                    "select\n" +
                    "	c.codi_relacio  id,\n" +
                    "	coalesce(cl.codigoenti, '') idcliente,\n" +
                    "	case c.vdl_dia when 0.00 then 1 else 0 end cancelado,\n" +
                    "	c.numerocaix ecf,\n" +
                    "	c.numerocupo coo,\n" +
                    "	c.datamovime data,\n" +
                    "	c.vdg_dia subtotalimpressora,\n" +
                    "	c.numcgc_cpf cnpj,\n" +
                    "	c.hora_ini horainicio,\n" +
                    "	c.hora_fin horafim,\n" +
                    "	c.chave_nfe chavenfe,\n" +
                    "	coalesce(cpf.nomeentida, '') razao,\n" +
                    "	coalesce(cep.logradouro, '') endereco,\n" +
                    "	coalesce(cpf.complocent, '') complemento,\n" +
                    "	coalesce(cpf.compreside, '') numero,\n" +
                    "	coalesce(cep.bairro, '') bairro,\n" +
                    "	coalesce(cep.cidade, '') cidade,\n" +
                    "	coalesce(cep.estado, '') estado,\n" +
                    "	coalesce(cpf.codcepresi, '') cep\n" +
                    "from\n" +
                    "	FL305CUP c\n" +
                    "left join flcgccpf cpf on \n" +
                    "	(case when (cast(c.numcgc_cpf as bigint)) = 0 then 1 \n" +
                    "		else (cast(c.numcgc_cpf as bigint)) end = cast(cpf.numcgc_cpf as bigint))\n" +
                    "left join FL400CLI cl on (cl.id_entidade = cpf.id_entidade)\n" +
                    "left join fl423cep cep on (cl.id_cliente = cep.id_cliente) and\n" +
                    "	cep.codigocep = cpf.codcepcobr and\n" +
                    "	cep.tipocadast = 'CLI'\n" +
                    "where\n" +
                    "	c.codigoloja = @loja and\n" +
                    "	cast(c.datamovime as date) between @dataini and @datafim\n" +
                    "),\n" +
                    "dupl_id as (\n" +
                    "	select id, count(*) cont from vend group by id having count(*) > 1\n" +
                    "),\n" +
                    "dupl_cupom as (\n" +
                    "	select coo, ecf, data, count(*) cont from vend group by coo, ecf, data having count(*) > 1\n" +
                    "),\n" +
                    "dupl as (\n" +
                    "	select\n" +
                    "		id\n" +
                    "	from\n" +
                    "		dupl_id\n" +
                    "	union\n" +
                    "	select\n" +
                    "		v.id\n" +
                    "	from\n" +
                    "		vend v\n" +
                    "		join dupl_cupom c on\n" +
                    "			v.coo = c.coo and\n" +
                    "			v.ecf = c.ecf and\n" +
                    "			v.data = c.data\n" +
                    ")\n" +
                    "select distinct\n" +
                    "	v.*\n" +
                    "from\n" +
                    "	vend v\n" +
                    "where\n" +
                    "	not v.id in (select id from dupl)\n" +
                    "order by v.data, v.ecf, v.coo";
            LOG.log(Level.FINE, "SQL da venda: {0}", sql);
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

        private final Statement stm = ConexaoSqlServer.getConexao().createStatement();
        private final ResultSet rst;
        private final String sql;
        private VendaItemIMP next;

        private void obterNext() {
            try {
                if (next == null) {
                    if (rst.next()) {
                        next = new VendaItemIMP();

                        next.setId(rst.getString("id"));
                        next.setVenda(rst.getString("id_venda"));
                        String id = rst.getString("id_produto");
                        next.setCancelado(rst.getInt("cancelado") == 1);
                        //id = id.substring(0, id.length() - 1);
                        next.setProduto(id);
                        if (rst.getString("id_produto").equals(rst.getString("codigobarras"))) {
                            next.setCodigoBarras(next.getProduto());
                        } else {
                            next.setCodigoBarras(rst.getString("codigobarras"));
                        }
                        next.setDescricaoReduzida(rst.getString("descricao"));
                        next.setQuantidade(rst.getDouble("quantidade"));
                        next.setTotalBruto(rst.getDouble("total"));
                        next.setUnidadeMedida(rst.getString("unidade"));
                        next.setIcmsAliq(rst.getDouble("icms"));
                        next.setIcmsCst(rst.getInt("cst"));
                        next.setIcmsReduzido(rst.getDouble("icmsreducao"));
                    }
                }
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaItemIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {                    
            this.sql = 
                    "declare @loja integer = " + idLojaCliente + ";\n" +
                    "declare @dataini date = '" + VendaIterator.FORMAT.format(dataInicio) + "';\n" +
                    "declare @datafim date = '" + VendaIterator.FORMAT.format(dataTermino) + "';\n" +
                    "with vend as (\n" +
                    "	select\n" +
                    "	 	it.codi_relacio + '-' + cast(coalesce(it.id_item, 1) as varchar) + '-' + cast(it.vdg_dia as varchar) + '-' + cast(it.codigoplu as varchar) id,\n" +
                    "		it.codi_relacio id_venda,\n" +
                    "		it.codigoplu id_produto,\n" +
                    "		pr.estc35desc descricao,\n" +
                    "	 case upper(it.origem) when 'C' then 1 else 0 end cancelado,\n" +
                    "	   case\n" +
                    "			pr.estc13codi when '' then pr.codigoplu else\n" +
                    "			pr.estc13codi end as codigobarras,\n" +
                    "		pr.tip_emb_vd unidade,\n" +
                    "		it.datamovime data,\n" +
                    "		it.id_item sequencia,\n" +
                    "		it.vdg_dia total,\n" +
                    "		it.qtd_dia quantidade,\n" +
                    "		tr.valoricm icms,\n" +
                    "		tr.situatribu cst,\n" +
                    "		tr.mrger icmsreducao\n" +
                    "	from\n" +
                    "		FL305DIA it\n" +
                    "	join FLTRIBUT tr on (it.codigoloja = tr.codigoloja) and\n" +
                    "		it.codtribsai = tr.codigotrib\n" +
                    "	join HRPDV_PREPARA_PRO pr on (it.codigoplu = pr.codigoplu) and\n" +
                    "		it.codigoloja = pr.codigoloja\n" +
                    "	where \n" +
                    "		it.codigoloja = @loja and\n" +
                    "		(cast(it.datamovime as date) between @dataini and @datafim)\n" +
                    "),\n" +
                    "dupl as (\n" +
                    "	select id from vend group by id having count(*) > 1\n" +
                    ")\n" +
                    "select v.* from vend v left join dupl d on v.id = d.id where d.id is null\n" +
                    "order by\n" +
                    "	id, sequencia";
            LOG.log(Level.FINE, "SQL da venda: {0}", sql);
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
    
    private class Endereco {
        String logradouro;
        String numero;
        String complemento;
        String bairro;
        String cidade;
        String estado;
        String cep;
    }
    
    private static class Telefone {
        int id;
        String fone1;
        String fone2;
        String fax;
        String celular;
        String foneEmpresa;
        String faxEmpresa;
    }

}