package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrframework.classe.Conexao;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Importacao
 */
public class HRTechDAO extends InterfaceDAO {

    @Override
    public String getSistema() {
        return "HRTech";
    }
    
    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try(Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "    codigoenti id,\n" +
                    "    apeltarefa razao\n" +
                    "from\n" +
                    "    fl060loj\n" +
                    "order by\n" +
                    "    1")) {
                while(rs.next()) {
                    result.add(new Estabelecimento(rs.getString("id"), rs.getString("razao")));
                }
            }
        }
        return result;
    }
    
    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        try(Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "	(select gruc03seto from fl100dpt m1 where gruc03grup = '' and gruc03subg = '' and gruc03fami = '' and gruc03subf = '' and m1.gruc03seto = m.gruc03seto) codmerc1,\n" +
                    "	(select m1.gruc35desc from fl100dpt m1 where gruc03grup = '' and gruc03subg = '' and gruc03fami = '' and gruc03subf = '' and m1.gruc03seto = m.gruc03seto) descmerc1,\n" +
                    "	(select gruc03grup from fl100dpt m1 where gruc03subg = '' and gruc03fami = '' and gruc03subf = '' and m1.gruc03seto = m.gruc03seto and m1.gruc03grup = m.gruc03grup) codmerc2,\n" +
                    "	(select m1.gruc35desc from fl100dpt m1 where gruc03subg = '' and gruc03fami = '' and gruc03subf = '' and m1.gruc03seto = m.gruc03seto and m1.gruc03grup = m.gruc03grup) descmerc2,\n" +
                    "	(select gruc03subg from fl100dpt m1 where gruc03fami = '' and gruc03subf = '' and m1.gruc03seto = m.gruc03seto and m1.gruc03grup = m.gruc03grup and m1.gruc03subg = m.gruc03subg) codmerc3,\n" +
                    "	(select m1.gruc35desc from fl100dpt m1 where gruc03fami = '' and gruc03subf = '' and m1.gruc03seto = m.gruc03seto and m1.gruc03grup = m.gruc03grup and m1.gruc03subg = m.gruc03subg) descmerc3,\n" +
                    "	(select gruc03fami from fl100dpt m1 where gruc03subf = '' and m1.gruc03seto = m.gruc03seto and m1.gruc03grup = m.gruc03grup and m1.gruc03subg = m.gruc03subg and m1.gruc03fami = m.gruc03fami) codmerc4,\n" +
                    "	(select m1.gruc35desc from fl100dpt m1 where gruc03subf = '' and m1.gruc03seto = m.gruc03seto and m1.gruc03grup = m.gruc03grup and m1.gruc03subg = m.gruc03subg and m1.gruc03fami = m.gruc03fami) descmerc4,\n" +
                    "	(select gruc03subf from fl100dpt m1 where m1.gruc03seto = m.gruc03seto and m1.gruc03grup = m.gruc03grup and m1.gruc03subg = m.gruc03subg and m1.gruc03fami = m.gruc03fami and m1.gruc03subf = m.gruc03subf) codmerc5,\n" +
                    "	(select m1.gruc35desc from fl100dpt m1 where m1.gruc03seto = m.gruc03seto and m1.gruc03grup = m.gruc03grup and m1.gruc03subg = m.gruc03subg and m1.gruc03fami = m.gruc03fami and m1.gruc03subf = m.gruc03subf) descmerc5\n" +
                    "from\n" +
                    "	fl100dpt m\n" +
                    "where\n" +
                    "	(select gruc03seto from fl100dpt m1 where gruc03grup = '' and gruc03subg = '' and gruc03fami = '' and gruc03subf = '' and m1.gruc03seto = m.gruc03seto) != '' and\n" +
                    "	(select gruc03grup from fl100dpt m1 where gruc03subg = '' and gruc03fami = '' and gruc03subf = '' and m1.gruc03seto = m.gruc03seto and m1.gruc03grup = m.gruc03grup) != '' and\n" +
                    "	(select gruc03subg from fl100dpt m1 where gruc03fami = '' and gruc03subf = '' and m1.gruc03seto = m.gruc03seto and m1.gruc03grup = m.gruc03grup and m1.gruc03subg = m.gruc03subg) != '' and\n" +
                    "	(select gruc03fami from fl100dpt m1 where gruc03subf = '' and m1.gruc03seto = m.gruc03seto and m1.gruc03grup = m.gruc03grup and m1.gruc03subg = m.gruc03subg and m1.gruc03fami = m.gruc03fami) != '' and\n" +
                    "	(select gruc03subf from fl100dpt m1 where m1.gruc03seto = m.gruc03seto and m1.gruc03grup = m.gruc03grup and m1.gruc03subg = m.gruc03subg and m1.gruc03fami = m.gruc03fami and m1.gruc03subf = m.gruc03subf) != ''\n" +
                    "order by\n" +
                    "	m.gruc03seto, \n" +
                    "	m.gruc03grup, \n" +
                    "	m.gruc03subg, \n" +
                    "	m.gruc03fami, \n" +
                    "	m.gruc03subf")) {
                while(rs.next()) {
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
                    imp.setMerc5ID(rs.getString("codmerc5"));
                    imp.setMerc5Descricao(rs.getString("descmerc5"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }
    
    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try(Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	distinct\n" +
                    "	p.codigoplu id,\n" +
                    "	case \n" +
                    "		when p.estc13codi = '' then \n" +
                    "		p.codigoplu \n" +
                    "	else p.estc13codi end ean,\n" +
                    "	p.estc35desc descricaocompleta,\n" +
                    "	p.descreduzi descricaoreduzida,\n" +
                    "	ps.situacao,\n" +
                    "	ps.dtcadastro,\n" +
                    "	p.estc03seto merc1,\n" +
                    "	p.estc03grup merc2,\n" +
                    "	p.estc03subg merc3,\n" +
                    "	p.estc03fami merc4,\n" +
                    "	p.estc03subf merc5,\n" +
                    "	p.estc01peso pesavel,\n" +
                    "	coalesce(bal.diasvalida, 0) validade,\n" +
                    "	coalesce(bal.peso_varia, '') peso,\n" +
                    "	cus.custoliqui custosemimposto,\n" +
                    "	cus.custoliqui custocomimposto,\n" +
                    "	p.vendaatua venda,\n" +
                    "	p.tip_emb_vd embalagem,\n" +
                    "	p.datreajatu datareajuste,\n" +
                    "	p.estoque,\n" +
                    "	p.cod_ncm ncm,\n" +
                    "	p.data_alte dataalteracao,\n" +
                    "	p.siglatribu,\n" +
                    "	p.tributouni tributo,\n" +
                    "	p.valoricm icms,\n" +
                    "	tr.situatribu cst,\n" +
                    "	tr.mrger icmsreducao,\n" +
                    "	p.cstpis,\n" +
                    "	p.cstcof cstcofins,\n" +
                    "	nat.NAT_REC_PIS naturezareceita,\n" +
                    "	p.codcest cest\n" +
                    "from \n" +
                    "	HRPDV_PREPARA_PRO p\n" +
                    "left join FLTRIBUT tr on (p.codigotrib = tr.codigotrib) and\n" +
                    "	p.codigoloja = tr.codigoloja\n" +
                    "left join FL303CUS cus on (p.codigoplu = cus.codigoplu) and\n" +
                    "	cus.codigoloja = p.codigoloja\n" +
                    "left join FL300EST ps on (p.codigoplu = ps.codigoplu)\n" +
                    "left join FL328BAL bal on (p.codigoplu = bal.codigoplu)\n" +
                    "left join FLTABNCM_PIS nat on (p.cod_ncm = nat.codigo) and\n" +
                    "	nat.cstpis = p.cstpis and\n" +
                    "	nat.cstcof = p.cstcof\n" +
                    "where\n" +
                    "	p.codigoloja = " + getLojaOrigem() + "\n" +
                    "order by\n" +
                    "	p.codigoplu")) {
                while(rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    if("S".equals(rs.getString("pesavel"))) {
                        String id = rs.getString("id");
                        id = id.substring(0, id.length() - 1);
                        imp.setImportId(id);
                        imp.setEan(id);
                    } else {
                        imp.setImportId(rs.getString("id"));
                        imp.setEan(rs.getString("ean"));
                    }
                    imp.setDescricaoCompleta(rs.getString("descricaocompleta"));
                    imp.setDescricaoGondola(rs.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rs.getString("descricaoreduzida"));
                    imp.setSituacaoCadastro(rs.getInt("situacao") == 0 ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    imp.setDataCadastro(rs.getDate("datacadastro"));
                    imp.setCodMercadologico1(rs.getString("merc1"));
                    imp.setCodMercadologico2(rs.getString("merc2"));
                    imp.setCodMercadologico3(rs.getString("merc3"));
                    imp.setCodMercadologico4(rs.getString("merc4"));
                    imp.setCodMercadologico5(rs.getString("merc5"));
                    imp.setCustoComImposto(rs.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(rs.getDouble("custosemimposto"));
                    imp.setPrecovenda(rs.getDouble("venda"));
                    imp.setTipoEmbalagem(rs.getString("embalagem"));
                    imp.setEstoque(rs.getDouble("estoque"));
                    imp.setNcm(rs.getString("ncm"));
                    imp.setPiscofinsNaturezaReceita(rs.getString("naturezareceita"));
                    imp.setDataAlteracao(rs.getDate("dataalteracao"));
                    imp.setIcmsAliq(rs.getDouble("icms"));
                    imp.setIcmsCst(rs.getString("cst"));
                    imp.setIcmsReducao(rs.getDouble("icmsreducao"));
                    imp.setIcmsAliqEntrada(rs.getDouble("icms"));
                    imp.setIcmsCstEntrada(rs.getInt("cst"));
                    imp.setIcmsAliqSaida(rs.getDouble("icms"));
                    imp.setIcmsCstSaida(rs.getInt("cst"));
                    imp.setPiscofinsCstCredito(rs.getString("cstcofins"));
                    imp.setCest(rs.getString("cest"));
                    imp.seteBalanca("S".equals(rs.getString("pesavel")));
                    imp.setValidade(rs.getInt("validade"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }
}
