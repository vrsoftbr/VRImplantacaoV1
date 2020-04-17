package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import vrimplantacao.classe.ConexaoInformix;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.OfertaIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Importacao
 */
public class LogusDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "Logus";
    }
    
    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoInformix.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "	distinct\n" +
                    "	icms.cdg_icms id,\n" +
                    "	icms.dcr_icms descricao,\n" +
                    "	cst.cdg_situacaotributariaicms cst,\n" +
                    "	icms.pct_aliq_icms aliquota,\n" +
                    "	icms.pct_reducao_bc reducao\n" +
                    "from \n" +
                    "	informix.cadicms icms  \n" +
                    "join informix.cadsituacoestributariasicms cst \n" +
                    "	on icms.idcadsituacaotributariaicms = cst.idcadsituacaotributariaicms\n" +
                    "where \n" +
                    " 	icms.cdg_icms in \n" +
                    " 		(select cdg_icms from cadassoc)\n" +
                    "order by\n" +
                    "	1")) {
                while(rs.next()) {
                    result.add(new MapaTributoIMP(
                            rs.getString("id"), 
                            rs.getString("descricao"),
                            rs.getInt("cst"),
                            rs.getDouble("aliquota"),
                            rs.getDouble("reducao")));
                }
            }
        }
        return result;
    }
    
    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                new OpcaoProduto[]{
                    OpcaoProduto.FAMILIA,
                    OpcaoProduto.FAMILIA_PRODUTO,
                    OpcaoProduto.MERCADOLOGICO_PRODUTO,
                    OpcaoProduto.MERCADOLOGICO,
                    OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                    OpcaoProduto.PRODUTOS,
                    OpcaoProduto.EAN,
                    OpcaoProduto.EAN_EM_BRANCO,
                    OpcaoProduto.DATA_CADASTRO,
                    OpcaoProduto.TIPO_EMBALAGEM_EAN,
                    OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                    OpcaoProduto.PESAVEL,
                    OpcaoProduto.VALIDADE,
                    OpcaoProduto.DESC_COMPLETA,
                    OpcaoProduto.DESC_GONDOLA,
                    OpcaoProduto.DESC_REDUZIDA,
                    OpcaoProduto.ESTOQUE_MAXIMO,
                    OpcaoProduto.ESTOQUE_MINIMO,
                    OpcaoProduto.PRECO,
                    OpcaoProduto.CUSTO,
                    OpcaoProduto.ESTOQUE,
                    OpcaoProduto.ATIVO,
                    OpcaoProduto.NCM,
                    OpcaoProduto.CEST,
                    OpcaoProduto.PIS_COFINS,
                    OpcaoProduto.NATUREZA_RECEITA,
                    OpcaoProduto.ICMS,
                    OpcaoProduto.ICMS_SAIDA,
                    OpcaoProduto.ICMS_SAIDA_FORA_ESTADO,
                    OpcaoProduto.ICMS_SAIDA_NF,
                    OpcaoProduto.ICMS_ENTRADA,
                    OpcaoProduto.ICMS_CONSUMIDOR,
                    OpcaoProduto.ICMS_ENTRADA_FORA_ESTADO,
                    OpcaoProduto.PAUTA_FISCAL,
                    OpcaoProduto.PAUTA_FISCAL_PRODUTO,
                    OpcaoProduto.MARGEM,
                    OpcaoProduto.OFERTA,
                    OpcaoProduto.MAPA_TRIBUTACAO,
                    OpcaoProduto.USAR_CONVERSAO_ALIQUOTA_COMPLETA
                }
        ));
    }
    
    public List<Estabelecimento> getLojas() throws SQLException {
        List<Estabelecimento> result = new ArrayList<>();
        
        try(Statement stm = ConexaoInformix.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	cdg_filial id,\n" +
                    "	dcr_fantasia fantasia\n" +
                    "from\n" +
                    "	informix.cadfil")) {
                while(rs.next()) {
                    result.add(new Estabelecimento(rs.getString("id"), rs.getString("fantasia")));
                }
            }
        }
        return result;
    }
    
    @Override
    public List<MercadologicoIMP> getMercadologicos() throws SQLException {
        List<MercadologicoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoInformix.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "	distinct\n" +
                    "	x5.cdg_depto merc1,\n" +
                    "	x5.dcr_depto descmerc1,\n" +
                    "	x4.cdg_secao merc2,\n" +
                    "	x4.dcr_secao descmerc2,\n" +
                    "	x3.cdg_grupo merc3,\n" +
                    "	x3.dcr_grupo descmerc3,\n" +
                    "	x6.cdg_subgrupo merc4,\n" +
                    "	x6.dcr_subgrupo descmerc4\n" +
                    "from\n" +
                    "	cadassoc x0,\n" +
                    "   cadprod x1,\n" +
                    "	cadgrupo x3,\n" +
                    "	cadsecao x4,\n" +
                    "	caddepto x5,\n" +
                    "	cadsubgr x6\n" +
                    "where\n" +
                    "	x0.cdg_interno = x1.cdg_interno and \n" +
                    "	x0.cdg_estoque = x1.cdg_produto and \n" +
                    "	x0.cdg_grupo = x3.cdg_grupo and \n" +
                    "	x3.cdg_secao = x4.cdg_secao and \n" +
                    "	x4.cdg_depto = x5.cdg_depto and \n" +
                    "	x0.cdg_subgrupo = x6.cdg_subgrupo\n" +
                    "order by\n" +
                    "	1, 2, 3, 4")) {
                while(rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rs.getString("merc1"));
                    imp.setMerc1Descricao(rs.getString("descmerc1"));
                    imp.setMerc2ID(rs.getString("merc2"));
                    imp.setMerc2Descricao(rs.getString("descmerc2"));
                    imp.setMerc3ID(rs.getString("merc3"));
                    imp.setMerc3Descricao(rs.getString("descmerc3"));
                    imp.setMerc4ID(rs.getString("merc4"));
                    imp.setMerc4Descricao(rs.getString("descmerc4"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoInformix.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	p.cdg_produto id,\n" +
                    "	p.cdg_interno id_interno,\n" +
                    "	p.dcr_etiq_gondola descricaogondola,\n" +
                    "	p.dcr_reduzida descricaoreduzida,\n" +
                    "	pa.dcr_produto || ' ' || pa.dcr_variedade descricaologus,\n" +
                    "	p.cdg_barra ean,\n" +
                    "	nullif (pa.flb_tipo_peso, 'F') pesavel,\n" +
                    "	pa.flb_habilita_checagem_peso_pdv pesopdv,\n" +
                    "	est.val_custo custosemimposto,\n" +
                    "	est.val_custo_tot custocomimposto,\n" +
                    "	pa.pct_mg_lucro margem,\n" +
                    "	est.val_preco precovenda,\n" +
                    "	est.qtd_estoque estoque,\n" +
                    "	p.dat_cadastro cadastro,\n" +
                    "	p.dat_desativacao desativacao,\n" +
                    "	un.sgl_unidade_medida unidade,\n" +
                    "	p.qtd_por_emb qtdembalagem,\n" +
                    "	ncm.cdg_ncm ncm,\n" +
                    "	st.cdg_especificador_st cest,\n" +
                    "	se.cdg_depto merc1,\n" +
                    "	se.cdg_secao merc2,\n" +
                    "	gr.cdg_grupo merc3,\n" +
                    "	pa.cdg_subgrupo merc4,\n" +
                    "	pa.cdg_icms idicms,\n" +
                    "	est.pct_icms_ent icms_credito,\n" +
                    "	pis.flg_cst_piscofinse pis_credito,\n" +
                    "	pis.flg_cst_piscofinss pis_debito,\n" +
                    "	pis.cdg_natrecpiscof naturezareceita\n" +
                    "from \n" +
                    "	informix.cadprod p\n" +
                    "left join informix.cadunidadesmedida un on p.idcadunidademedida = un.idcadunidademedida\n" +
                    "left join informix.cadassoc pa on p.cdg_interno = pa.cdg_interno and \n" +
                    "	pa.cdg_estoque = p.cdg_produto\n" +
                    "left join informix.estprfil est on p.cdg_produto = est.cdg_produto\n" +
                    "left join informix.cadgrupo gr on pa.cdg_grupo = gr.cdg_grupo\n" +
                    "left join informix.cadsecao se on gr.cdg_secao = se.cdg_secao\n" +
                    "left join informix.caddepto dp on se.cdg_depto = dp.cdg_depto\n" +
                    "left join cadassocpiscofins pis on pa.cdg_interno = pis.cdg_interno\n" +
                    "	and pis.dat_ini_vigencia = (select\n" +
                    "                                   max(x.dat_ini_vigencia)\n" +
                    "                               from\n" +
                    "                                   cadassocpiscofins x\n" +
                    "                               where\n" +
                    "                                   x.cdg_interno = pis.cdg_interno and \n" +
                    "                                   x.dat_ini_vigencia <= current year to fraction(3))\n" +
                    "left join cadcodigosespecificstproduto cest on pa.cdg_interno = cest.cdg_interno and \n" +
                    "	cest.dat_inicio_vigencia = (select \n" +
                    "					min(x.dat_inicio_vigencia)\n" +
                    "                               from\n" +
                    "					cadcodigosespecificstproduto x\n" +
                    "                               where \n" +
                    "					x.cdg_interno = cest.cdg_interno)\n" +
                    "left join cadcodigosespecificadoresst st on cest.idcadcodigoespecificadorst = st.idcadcodigoespecificadorst\n" +
                    "left join cadncmproduto ncm on pa.cdg_interno = ncm.cdg_interno and \n" +
                    "	ncm.dat_ini_vigencia = (select \n" +
                    "					max(x.dat_ini_vigencia)\n" +
                    "				from \n" +
                    "					cadncmproduto x \n" +
                    "				where \n" +
                    "					x.cdg_interno = ncm.cdg_interno and \n" +
                    "					x.dat_ini_vigencia <= current year to fraction(3))\n" +
                    "where \n" +
                    "	est.cdg_filial = " + getLojaOrigem())) {
                while(rs.next()) {
                   ProdutoIMP imp = new ProdutoIMP();
                   
                   imp.setImportLoja(getLojaOrigem());
                   imp.setImportSistema(getSistema());
                   imp.setImportId(rs.getString("id_interno"));
                   imp.setDescricaoCompleta(rs.getString("descricaogondola") == null ? rs.getString("descricaoreduzida")
                           :rs.getString("descricaogondola"));
                   imp.setDescricaoReduzida(rs.getString("descricaoreduzida"));
                   imp.setDescricaoGondola(imp.getDescricaoCompleta());
                   if(rs.getString("pesavel") != null && "V".equals(rs.getString("pesavel").trim().toUpperCase())) {
                       imp.seteBalanca(true);
                   }
                   if(rs.getString("desativacao") != null) {
                       imp.setSituacaoCadastro(0);
                   }
                   imp.setEan(rs.getString("ean"));
                   imp.setCustoComImposto(rs.getDouble("custocomimposto"));
                   imp.setCustoSemImposto(rs.getDouble("custosemimposto"));
                   imp.setPrecovenda(rs.getDouble("precovenda"));
                   imp.setMargem(rs.getDouble("margem"));
                   imp.setEstoque(rs.getDouble("estoque"));
                   imp.setDataCadastro(rs.getDate("cadastro"));
                   imp.setTipoEmbalagem(rs.getString("unidade"));
                   imp.setQtdEmbalagem(rs.getInt("qtdembalagem"));
                   imp.setNcm(rs.getString("ncm"));
                   imp.setCest(rs.getString("cest"));
                   imp.setCodMercadologico1(rs.getString("merc1"));
                   imp.setCodMercadologico2(rs.getString("merc2"));
                   imp.setCodMercadologico3(rs.getString("merc3"));
                   imp.setCodMercadologico4(rs.getString("merc4"));
                   imp.setIcmsDebitoId(rs.getString("idicms"));
                   imp.setIcmsCstEntrada(00);
                   imp.setIcmsAliqEntrada(rs.getDouble("icms_credito"));
                   imp.setIcmsReducaoEntrada(0.0);
                   imp.setPiscofinsCstCredito(rs.getString("pis_credito"));
                   imp.setPiscofinsNaturezaReceita(rs.getString("naturezareceita"));
                   
                   result.add(imp);
                }
            }
        }
        return result;
    } 

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoInformix.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	pf.cdg_fornecedor idfornecedor,\n" +
                    "	p.cdg_interno idproduto,\n" +
                    "	pf.cdg_prod_forn codigoexterno\n" +
                    "from \n" +
                    "	cadcodfor pf \n" +
                    "inner join cadforn f on pf.cdg_fornecedor = f.cdg_fornecedor \n" +
                    "inner join cadprod p on pf.cdg_produto = p.cdg_produto")) {
                while(rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rs.getString("idproduto"));
                    imp.setIdFornecedor(rs.getString("idfornecedor"));
                    imp.setCodigoExterno(rs.getString("codigoexterno"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoInformix.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	f.cdg_fornecedor id,\n" +
                    "	f.dcr_fornecedor razao,\n" +
                    "	f.dcr_fantasia fantasia,\n" +
                    "	f.nmr_ie ie,\n" +
                    "	f.nmr_im inscricaomunicipal,\n" +
                    "	f.dat_cadastro cadastro,\n" +
                    "	f.cdg_municipio ibgemunicipio,\n" +
                    "	f.dcr_cidade_old cidadeold,\n" +
                    "	f.dcr_endereco endereco,\n" +
                    "	f.nmr_endereco numero,\n" +
                    "	f.dcr_bairro bairro,\n" +
                    "	f.sgl_estado uf,\n" +
                    "	f.nmr_cep cep, \n" +
                    "	f.nmr_fone telefone,\n" +
                    "	f.nmr_fax fax,\n" +
                    "	f.dcr_vendedor vendedor,\n" +
                    "	f.nmr_fone_vend fonevendedor,\n" +
                    "	f.nmr_fax_vend faxvendedor,\n" +
                    "	f.cdg_cpag condicao,\n" +
                    "	f.dat_desativacao desativado,\n" +
                    "	f.dcr_email_vend emailvendedor,\n" +
                    "	f.dcr_email_pedido emailpedido,\n" +
                    "	f.dcr_assunto assunto\n" +
                    "from \n" +
                    "	cadforn f")) {
                while(rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setIe_rg(rs.getString("ie"));
                    imp.setInsc_municipal(rs.getString("inscricaomunicipal"));
                    imp.setDatacadastro(rs.getDate("cadastro"));
                    imp.setIbge_municipio(rs.getInt("ibgemunicipio"));
                    imp.setMunicipio(rs.getString("cidadeold"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));
                    imp.setTel_principal(rs.getString("telefone"));
                    
                    if(rs.getString("fax") != null && !"".equals(rs.getString("fax"))) {
                        imp.addContato("1", "FAX", rs.getString("fax"), null, TipoContato.NFE, null);
                    }
                    
                    if(rs.getString("vendedor") != null && !"".equals(rs.getString("vendedor"))) {
                        imp.addContato("2", rs.getString("vendedor"), rs.getString("fonevendedor"), null, TipoContato.COMERCIAL, rs.getString("emailvendedor"));
                    }
                    
                    if(rs.getString("faxvendedor") != null && !"".equals(rs.getString("faxvendedor"))) {
                        imp.addContato("3", "FAX VEND", rs.getString("faxvendedor"), null, TipoContato.COMERCIAL, null);
                    }
                    
                    imp.setCondicaoPagamento(Integer.valueOf(Utils.formataNumero(rs.getString("condicao"))));
                    
                    if(rs.getString("desativado") != null) {
                        imp.setAtivo(false);
                    }
                    
                    if(rs.getString("emailpedido") != null && !"".equals(rs.getString("emailpedido"))) {
                        imp.addContato("4", "PEDIDO", null, null, TipoContato.COMERCIAL, rs.getString("emailpedido"));
                    }
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoInformix.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	c.nmr_cliente id,\n" +
                    "	c.cdg_cliente codigo,\n" +
                    "	c.dcr_cliente razao,\n" +
                    "	c.nmr_rg_ie ie,\n" +
                    "	c.nmr_rg rg,\n" +
                    "	c.dcr_endereco endereco,\n" +
                    "	c.dcr_compl_end complemento,\n" +
                    "	c.nmr_endereco numero,\n" +
                    "	c.cdg_municipio ibgemunicipio,\n" +
                    "	c.dcr_cidade_old municipio,\n" +
                    "	c.cdg_cidade idcidade,\n" +
                    "	c.dcr_bairro bairro,\n" +
                    "	c.sgl_estado uf,\n" +
                    "	c.nmr_fone fone,\n" +
                    "	c.nmr_fone2 fone2,\n" +
                    "	c.nmr_fone3 fone3,\n" +
                    "	c.nmr_celular celular,\n" +
                    "	c.dcr_funcao funcao,\n" +
                    "	c.dat_bloqueio bloqueio,\n" +
                    "	c.cdg_status,\n" +
                    "	c.dat_cadastro cadastro,\n" +
                    "	c.dat_nascto nascimento,\n" +
                    "	c.nmr_fone_emp fonempresa,\n" +
                    "	c.dcr_email email,\n" +
                    "	c.dcr_email_xml emailxml,\n" +
                    "	c.flb_sexo sexo,\n" +
                    "	c.flb_estado_civil estadocivil,\n" +
                    "	c.val_limite_total_convenio limite\n" +
                    "from \n" +
                    "	cadcli c")) {
                while(rs.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    
                    imp.setId(rs.getString("id"));
                    imp.setRazao(rs.getString("razao"));
                    if(rs.getString("ie") != null && "ISENTO".equals(rs.getString("ie"))) {
                        imp.setInscricaoestadual(rs.getString("rg"));
                    } else {
                        imp.setInscricaoestadual(rs.getString("ie"));
                    }
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setMunicipioIBGE(rs.getString("ibgemunicipio"));
                    imp.setMunicipio(rs.getString("municipio"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setUf(rs.getString("uf"));
                    imp.setTelefone(rs.getString("fone"));
                    imp.setCelular(rs.getString("celular"));
                    imp.setDataCadastro(rs.getDate("cadastro"));
                    imp.setDataNascimento(rs.getDate("nascimento"));
                    imp.setEmail(rs.getString("email"));
                    imp.setValorLimite(rs.getDouble("limite"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoInformix.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	r.nmr_lancto id,\n" +
                    "	r.dat_venda emissao,\n" +
                    "	r.nmr_cliente idcliente,\n" +
                    "	r.val_docto_orig,\n" +
                    "	(r.val_docto - (case when r.val_recebido is null then 0 else r.val_recebido end)) valor,\n" +
                    "	r.dat_vecto vencimento,\n" +
                    "	r.nmr_docto documento,\n" +
                    "	r.nmr_docto_vnd docvenda\n" +
                    "from \n" +
                    "	recconta r\n" +
                    "where \n" +
                    "	r.nmr_cliente is not null and \n" +
                    "	r.val_recebido < r.val_docto or r.dat_recebto is null and \n" +
                    "	r.cdg_filial = " + getLojaOrigem() + "\n" +
                    "order by \n" +
                    "	r.dat_vecto")) {
                while(rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    
                    imp.setId(rs.getString("id"));
                    imp.setDataEmissao(rs.getDate("emissao"));
                    imp.setDataVencimento(rs.getDate("vencimento"));
                    imp.setIdCliente(rs.getString("idcliente"));
                    imp.setValor(rs.getDouble("valor"));
                    imp.setNumeroCupom(rs.getString("documento"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<OfertaIMP> getOfertas(Date dataTermino) throws Exception {
        List<OfertaIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoInformix.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	 o.nmr_lancto id,\n" +
                    "	 p.cdg_interno idproduto,\n" +
                    "	 o.dat_preco_de datainicio,\n" +
                    "	 o.dat_preco_ate datatermino,\n" +
                    "	 o.val_preco_de preconormal,\n" +
                    "	 o.val_preco_ate precooferta\n" +
                    "from \n" +
                    "	bdoprpre o\n" +
                    "join cadprod p on o.cdg_produto = p.cdg_produto \n" +
                    "where \n" +
                    "	o.dat_preco_de is not null and \n" +
                    "	o.dat_preco_ate is not null and\n" +
                    "	o.dat_preco_ate > current\n" +
                    "order by \n" +
                    "	o.dat_proc_ate")) {
                while(rs.next()) {
                    OfertaIMP imp = new OfertaIMP();
                    
                    imp.setIdProduto(rs.getString("idproduto"));
                    imp.setDataInicio(rs.getDate("datainicio"));
                    imp.setDataFim(rs.getDate("datatermino"));
                    imp.setPrecoNormal(rs.getDouble("preconormal"));
                    imp.setPrecoOferta(rs.getDouble("precooferta"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }
}
