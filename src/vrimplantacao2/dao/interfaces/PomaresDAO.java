package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.cadastro.mercadologico.MercadologicoNivelIMP;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Leandro
 */
public class PomaresDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "Pomares";
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	loj.lojnid_ent id,\n" +
                    "	ent.entcnoment nome\n" +
                    "from \n" +
                    "	asentloj loj\n" +
                    "	join asentent ent on loj.lojnid_ent = ent.entnid_ent\n" +
                    "order by\n" +
                    "	1"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("id"), rst.getString("nome")));
                }
            }
        }
        
        return result;
    }

    @Override
    public List<MercadologicoNivelIMP> getMercadologicoPorNivel() throws Exception {
        List<MercadologicoNivelIMP> result = new ArrayList<>();
        
        Map<String, MercadologicoNivelIMP> map = new LinkedHashMap<>();
        
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	depnid_dep id,\n" +
                    "	depcdescri descricao,\n" +
                    "	depnid_pai pai\n" +
                    "from\n" +
                    "	asprodep\n" +
                    "order by\n" +
                    "	pai, id"
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP imp = new MercadologicoNivelIMP();
                    
                    MercadologicoNivelIMP pai = map.get(rst.getString("pai"));
                    
                    if (pai == null) {
                        imp.setId(rst.getString("id"));
                        imp.setDescricao(rst.getString("descricao"));
                        map.put(imp.getId(), imp);
                        result.add(imp);
                    } else {
                        MercadologicoNivelIMP filho = pai.addFilho(rst.getString("id"), rst.getString("descricao"));
                        map.put(filho.getId(), filho);
                    }
                }
            }
        }
        
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        
        Map<String, MercadologicoNivelIMP> map = new LinkedHashMap<>();
        
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	depnid_dep id,\n" +
                    "	depcdescri descricao,\n" +
                    "	depnid_pai pai\n" +
                    "from\n" +
                    "	asprodep\n" +
                    "order by\n" +
                    "	pai, id"
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP imp = new MercadologicoNivelIMP();
                    
                    MercadologicoNivelIMP pai = map.get(rst.getString("pai"));
                    
                    if (pai == null) {
                        imp.setId(rst.getString("id"));
                        imp.setDescricao(rst.getString("descricao"));
                        map.put(imp.getId(), imp);
                    } else {
                        MercadologicoNivelIMP filho = pai.addFilho(rst.getString("id"), rst.getString("descricao"));
                        map.put(filho.getId(), filho);
                    }
                }
            }
        }
        
        List<ProdutoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n" +
                    "	p.PRONID_PRO id,\n" +
                    "	p.PRODDATCAD datacadastro,\n" +
                    "	ean.ean,\n" +
                    "	ean.qtd qtdembalagem,\n" +
                    "	un.unidade,\n" +
                    "	p.pronpesado e_balanca,\n" +
                    "	p.procdescri descricaocompleta,\n" +
                    "	p.procdesres descricaoresumida,\n" +
                    "	p.pronid_dep mercadologico1,\n" +
                    "	p.pronpesbru pesobruto,\n" +
                    "	p.pronpesliq pesoliq,\n" +
                    "	est.estnestatu estoque,\n" +
                    "	est.estnestmax estoquemaximo,\n" +
                    "	est.estnestmin estoqueminimo,\n" +
                    "	prc.prenvdamrg margem,\n" +
                    "	prc.prencusdig custosemimposto,\n" +
                    "	prc.prencusrep custocomimposto,\n" +
                    "	p.proccodncm ncm,\n" +
                    "	p.proccdcest cest,\n" +
                    "	prc.prenid_inc pis,\n" +
                    "	pis.pis pis_desc,\n" +
                    "	concat(mun.munccod_uf,prc.prenidfigu) icms,\n" +
                    "	icms.icms icms_desc\n" +
                    "from \n" +
                    "	aspropro p\n" +
                    "	join asentloj emp on emp.lojnid_ent = " + getLojaOrigem() + "\n" +
                    "	join asentent ent on emp.lojnid_ent = ent.entnid_ent\n" +
                    "	join ascepcep cep on ent.entnid_cep = cep.cepnid_cep\n" +
                    "	join ascepbai bai on cep.cepnid_bai = bai.bainid_bai\n" +
                    "	join ascepmun mun on bai.bainid_mun = mun.munnid_mun\n" +
                    "	left join (\n" +
                    "		select PRONID_PRO id, cast(PROCCODPRO as bigint) ean, 1 qtd from aspropro \n" +
                    "		union \n" +
                    "		select eannid_pro id, cast(eanccodean as bigint) ean, eannqtdemb from ASPROEAN\n" +
                    "	) ean on ean.id = p.PRONID_PRO\n" +
                    "	left join (\n" +
                    "		select gernid_ger id, gercdescri unidade from ASCADGER where gerctipcad = 'PROE'\n" +
                    "	) un on un.id = p.pronid_emb\n" +
                    "	left join asproest est on est.estnid_loj = emp.lojnid_ent and est.estnid_pro = p.pronid_pro\n" +
                    "	left join aspropre prc on prc.prenid_loj = emp.lojnid_ent and p.pronid_pro = prc.prenid_pro\n" +
                    "	left join (\n" +
                    "		select gernid_ger id, gercdescri pis from ASCADGER where gerctipcad = 'INCG'\n" +
                    "	) pis on pis.id = prc.prenid_inc\n" +
                    "	left join (\n" +
                    "		select gernid_ger id, gercdescri icms from ASCADGER where gerctipcad = 'FIGU'\n" +
                    "	) icms on icms.id = prc.prenidfigu"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    
                    List<MercadologicoNivelIMP> mercs = new ArrayList<>();
                    
                    MercadologicoNivelIMP merc = map.get(rst.getString("mercadologico1"));                    
                    mercs.add(merc);
                    do {
                        merc = merc.getMercadologicoPai();
                        mercs.add(merc);
                    } while (merc.getMercadologicoPai() != null);
                    Collections.reverse(mercs);
                    mercs.add(new MercadologicoNivelIMP());
                    mercs.add(new MercadologicoNivelIMP());
                    mercs.add(new MercadologicoNivelIMP());
                    mercs.add(new MercadologicoNivelIMP());
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setEan(rst.getString("ean"));
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                    imp.setTipoEmbalagem(rst.getString("unidade"));
                    imp.seteBalanca(rst.getBoolean("e_balanca"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoresumida"));
                    imp.setDescricaoGondola(rst.getString("descricaocompleta"));
                    imp.setCodMercadologico1(mercs.get(0).getId());
                    imp.setCodMercadologico2(mercs.get(1).getId());
                    imp.setCodMercadologico3(mercs.get(2).getId());
                    imp.setCodMercadologico4(mercs.get(3).getId());
                    imp.setCodMercadologico5(mercs.get(4).getId());
                    imp.setPesoBruto(rst.getDouble("pesobruto"));
                    imp.setPesoLiquido(rst.getDouble("pesoliq"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setEstoqueMaximo(rst.getDouble("estoquemaximo"));
                    imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(getPis(rst.getInt("pis")));
                    imp.setIcmsDebitoId(rst.getString("icms"));
                    imp.setIcmsCreditoId(rst.getString("icms"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }
    
    private int getPis(int cod) {
        switch (cod) {
            case 1013: return 1;
            case 1014: return 7;
            case 1015: return 1;
            case 1016: return 5;
            case 1017: return 4;
                    default: return 7;
        }
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select distinct\n" +
                    "	concat(fg.figcdestin,fg.fignidfigu) figura,\n" +
                    "	concat(\n" +
                    "		'ALQ: ', fg.figntriali, \n" +
                    "		'   RED: ', fg.figntrired,\n" +
                    "		'   DESC: ', cad.gercdescri\n" +
                    "	) descricao\n" +
                    "from\n" +
                    "	asprofig fg\n" +
                    "	join ascadger cad on fg.fignidfigu = cad.gernid_ger"
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(rst.getString("figura"), rst.getString("descricao")));
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
                    "	ent.ENTNID_ENT id,\n" +
                    "	ent.ENTCNOMENT razao,\n" +
                    "	ent.ENTCAPELID fantasia,\n" +
                    "	ent.ENTCCODCPF cnpj,\n" +
                    "	ent.ENTCCOD_RG rg,\n" +
                    "	cep.cepcdescri endereco,\n" +
                    "	ent.ENTNENDNUM numero,\n" +
                    "	ent.entcendcom complemento,\n" +
                    "	bai.BAICDESCRI bairro,\n" +
                    "	mun.muncdescri municipio,\n" +
                    "	mun.MUNCCOD_UF uf,\n" +
                    "	mun.MUNCCODIBG id_municipio,\n" +
                    "	cep.CEPCCODCEP cep,\n" +
                    "	ent.entddatcad datacadastro,\n" +
                    "	ent.entce_mail email\n" +
                    "from\n" +
                    "	asentent ent\n" +
                    "	join asentfnc fnc on ent.ENTNID_ENT = fnc.FNCNID_ENT\n" +
                    "	left join ascepcep cep on cep.CEPNID_CEP = ent.ENTNID_CEP\n" +
                    "	left join ascepbai bai on bai.bainid_bai = cep.CEPNID_BAI\n" +
                    "	left join ascepmun mun on mun.munnid_mun = bai.BAINID_MUN\n" +
                    "order by\n" +
                    "	ent.entnid_ent"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    imp.setIe_rg(rst.getString("rg"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setUf(rst.getString("uf"));
                    imp.setIbge_municipio(rst.getInt("id_municipio"));
                    imp.setCep(rst.getString("cep"));
                    imp.setDatacadastro(rst.getDate("datacadastro"));
                    
                    if (!"".equals(Utils.acertarTexto(rst.getString("email")))) {
                        imp.addContato("A", "EMAIL", "", "", TipoContato.COMERCIAL, rst.getString("email"));
                    }
                    
                    try (Statement stm2 = ConexaoSqlServer.getConexao().createStatement()) {
                        try (ResultSet rst2 = stm2.executeQuery(
                            "select\n" +
                            "	t.telnid_tel id,\n" +
                            "	concat(t.telncodddd,t.telnnumtel) tel,\n" +
                            "	coalesce(nullif(rtrim(ltrim(t.telmobserv)),''), 'telefone') descricao\n" +
                            "from\n" +
                            "	asenttel t\n" +
                            "where\n" +
                            "	t.telnid_ent = " + imp.getImportId()
                        )) {
                            while (rst2.next()) {
                                imp.addContato(
                                        rst2.getString("id"), 
                                        rst2.getString("descricao"), 
                                        rst2.getString("tel"), 
                                        "", 
                                        TipoContato.COMERCIAL,
                                        ""
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
                    "	pf.refnid_fnc id_fornecedor,\n" +
                    "	pf.refnid_pro id_produto,\n" +
                    "	pf.refccodref codigoexterno,\n" +
                    "	pf.refnqtdemb qtd\n" +
                    "from \n" +
                    "	asproref pf"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setIdFornecedor(rst.getString("id_fornecedor"));
                    imp.setIdProduto(rst.getString("id_produto"));
                    imp.setCodigoExterno(rst.getString("codigoexterno"));
                    imp.setQtdEmbalagem(rst.getDouble("qtd"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }
    
}
