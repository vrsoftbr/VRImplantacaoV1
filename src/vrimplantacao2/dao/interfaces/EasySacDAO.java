package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoEstadoCivil;
import vrimplantacao2.vo.importacao.ChequeIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Guilherme
 */
public class EasySacDAO extends InterfaceDAO implements MapaTributoProvider {

    public String complemento = "";
    
    @Override
    public String getSistema() {
        return "EasySac" + complemento;
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                new OpcaoProduto[]{
                    OpcaoProduto.MERCADOLOGICO_PRODUTO,
                    OpcaoProduto.MERCADOLOGICO,
                    OpcaoProduto.MANTER_CODIGO_MERCADOLOGICO,
                    OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                    OpcaoProduto.FAMILIA_PRODUTO,
                    OpcaoProduto.FAMILIA,
                    OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                    OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                    OpcaoProduto.PRODUTOS,
                    OpcaoProduto.EAN,
                    OpcaoProduto.EAN_EM_BRANCO,
                    OpcaoProduto.ATACADO,
                    OpcaoProduto.DATA_CADASTRO,
                    OpcaoProduto.TIPO_EMBALAGEM_EAN,
                    OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                    OpcaoProduto.QTD_EMBALAGEM_COTACAO,
                    OpcaoProduto.QTD_EMBALAGEM_EAN,
                    OpcaoProduto.PESAVEL,
                    OpcaoProduto.VALIDADE,
                    OpcaoProduto.DESC_COMPLETA,
                    OpcaoProduto.DESC_GONDOLA,
                    OpcaoProduto.DESC_REDUZIDA,
                    OpcaoProduto.ESTOQUE_MAXIMO,
                    OpcaoProduto.ESTOQUE_MINIMO,
                    OpcaoProduto.PRECO,
                    OpcaoProduto.CUSTO,
                    OpcaoProduto.CUSTO_COM_IMPOSTO,
                    OpcaoProduto.CUSTO_SEM_IMPOSTO,
                    OpcaoProduto.ESTOQUE,
                    OpcaoProduto.ATIVO,
                    OpcaoProduto.NCM,
                    OpcaoProduto.CEST,
                    OpcaoProduto.PIS_COFINS,
                    OpcaoProduto.NATUREZA_RECEITA,
                    OpcaoProduto.ICMS,
                    OpcaoProduto.MARGEM,
                    OpcaoProduto.OFERTA
                }
        ));
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n" +
                    "	cdloja id,\n" +
                    "	fantas razao\n" +
                    "from \n" +
                    "	sac999"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("id"), rst.getString("razao")));
                }
            }
        }

        return result;
    }
    
    @Override
    /*
    select * from sac427 --localidade 
    select * from sac434 --subgrupo
    select * from sac433 --grupo
    select * from sac432 --departamento
    */
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	distinct \n" +
                    "	l.cdloca merc1,\n" +
                    "	l.locali desc1,\n" +
                    "	s.cdsgru merc2,\n" +
                    "	s.subgru desc2\n" +
                    "from \n" +
                    "	sac441 p \n" +
                    "inner join sac427 l on p.cdloca = l.cdloca\n" +
                    "inner join sac434 s on p.cdsgru = s.cdsgru \n" +
                    "order by\n" +
                    "	1, 3")) {
                while (rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rs.getString("merc1"));
                    imp.setMerc1Descricao(rs.getString("desc1"));
                    imp.setMerc2ID(rs.getString("merc2"));
                    imp.setMerc2Descricao(rs.getString("desc2"));
                    imp.setMerc3ID("1");
                    imp.setMerc3Descricao(imp.getMerc2Descricao());

                    result.add(imp);
                }
            }
        }
        return result;
    }
    
    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n" +
                    "	cdimpo id,\n" +
                    "	impost descricao,\n" +
                    "	enticm icms, \n" +
                    "	cstest cst,\n" +
                    "	entred reducao\n" +
                    "from \n" +
                    "	sac422"
            )) {
                while (rst.next()) {

                    result.add(new MapaTributoIMP(
                            rst.getString("id"),
                            rst.getString("descricao"),
                            rst.getInt("cst"),
                            rst.getDouble("icms"),
                            rst.getDouble("reducao")
                    ));
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
                    "select \n" +
                    "	p.cdprod id, \n" +
                    "	p.nomcur descricaoreduzida,\n" +
                    "	p.nompro descricaocompleta,\n" +
                    "	p.barras ean,\n" +
                    "	p.datcad datacadastro,\n" +
                    "	p.unidad unidade,\n" +
                    "	p.qtdemb qtdunidade,\n" +
                    "	p.pesbru pesobruto,\n" +
                    "	p.pesliq pesoliquido,\n" +
                    "	p.estmin estoqueminimo,\n" +
                    "	em.saldos estoque,\n" +
                    "	p.cdcate categoria,\n" +
                    "	p.cdloca localidade,\n" +
                    "	p.cdsgru grupo,\n" +
                    "	p.cdmarc marca,\n" +
                    "	p.ativos situacao,\n" +
                    "	p.balanc balanca,\n" +
                    "	p.classi ncm,\n" +
                    "	p.precom custo,\n" +
                    "	p.pcusto custocomimposto,\n" +
                    "	p.lucros margem,\n" +
                    "	p.pvenda precovenda,\n" +
                    "	p.cdimpo aliquota,        \n" +
                    "	p.cdcest cest,\n" +
                    "	p.codpis pis,\n" +
                    "	p.codcof cofins\n" +
                    "from\n" +
                    "	sac441 p\n" +
                    "left join sac719 em on p.CDPROD = em.cdprod \n" +
                    "where\n" +
                    "	em.cdloja = " + getLojaOrigem() + " and \n" +
                    "	em.lancam = (select max(lancam) from sac719 where cdprod = p.cdprod and cdloja = em.cdloja)\n" +
                    "order by p.cdprod")) 
            {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setEan(rst.getString("ean"));
                    imp.seteBalanca(rst.getInt("balanca") == 1);
                    
                    if(imp.isBalanca() && 
                            imp.getEan() != null &&
                                !"".equals(imp.getEan().trim()) &&
                                        imp.getEan().length() <= 6) {
                        imp.setEan(imp.getEan().substring(0, imp.getEan().length() - 2));
                    }
                    
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setCodMercadologico1(rst.getString("localidade"));
                    imp.setCodMercadologico2(rst.getString("grupo"));
                    imp.setCodMercadologico3("1");
                    imp.setTipoEmbalagem(rst.getString("unidade"));
                    imp.setQtdEmbalagem(rst.getInt("qtdunidade"));
                    imp.setPesoBruto(rst.getDouble("pesobruto"));
                    imp.setPesoLiquido(rst.getDouble("pesoliquido"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getString("pis"));
                    
                    imp.setIcmsDebitoId(rst.getString("aliquota"));
                    imp.setIcmsDebitoForaEstadoId(imp.getIcmsDebitoId());
                    imp.setIcmsDebitoForaEstadoNfId(imp.getIcmsDebitoId());
                    imp.setIcmsCreditoId(imp.getIcmsDebitoId());
                    imp.setIcmsCreditoForaEstadoId(imp.getIcmsDebitoId());
                    imp.setIcmsConsumidorId(imp.getIcmsDebitoId());
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }
    
    @Override
    public List<ProdutoIMP> getProdutos(OpcaoProduto opt) throws Exception {

        if (opt == OpcaoProduto.ATACADO) {
            List<ProdutoIMP> vResult = new ArrayList<>();
            try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select \n" +
                        "	ean.cdprod idproduto,\n" +
                        "	ean.barras ean,\n" +
                        "	ean.conver qtdembalagem,\n" +
                        "	ean.pvenda precovendaemb,\n" +
                        "	pr.pvenda precovenda,\n" +
                        "	round((ean.pvenda / ean.conver), 2) precoatacado,\n" +
                        "	ean.cdunid unidade\n" +
                        "from \n" +
                        "	sac459 ean\n" +
                        "inner join sac441 pr on ean.cdprod = pr.cdprod"
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();

                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("idproduto"));
                        
                        String ean = rst.getString("ean");
                    
                        imp.setEan(ean);
                        if(ean != null && !"".equals(ean) && ean.length() < 7) {
                            imp.setEan("99999" + ean);
                        }
                        
                        imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                        imp.setAtacadoPreco(rst.getDouble("precoatacado"));
                        imp.setPrecovenda(rst.getDouble("precovenda"));

                        vResult.add(imp);
                    }
                }
            }
            return vResult;
        }

        return null;
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        //Tabela atacado SAC459
        List<ProdutoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	ean.cdprod idproduto,\n" +
                    "	ean.barras ean,\n" +
                    "	ean.conver qtdembalagem,\n" +
                    "	ean.pvenda precovendaemb,\n" +
                    "	pr.pvenda precovenda,\n" +
                    "	round((ean.pvenda / ean.conver), 2) precoatacado,\n" +
                    "	ean.cdunid unidade\n" +
                    "from \n" +
                    "	sac459 ean\n" +
                    "inner join sac441 pr on ean.cdprod = pr.cdprod")) {
                while(rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("idproduto"));
                    
                    String ean = rs.getString("ean");
                    
                    imp.setEan(ean);
                    if(ean != null && !"".equals(ean) && ean.length() < 7) {
                        imp.setEan("99999" + ean);
                    }
                    imp.setQtdEmbalagem(rs.getInt("qtdembalagem"));
                    imp.setTipoEmbalagem(rs.getString("unidade"));
                    
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
                    "select \n" +
                    "	f.cdforn id,\n" +
                    "	f.nomfor fantasia,\n" +
                    "	f.razsoc razao,\n" +
                    "	f.endere endereco,\n" +
                    "	f.insest ie,\n" +
                    "	f.nrcnpj cnpj,\n" +
                    "	f.insmun im,\n" +
                    "	f.bairro, \n" +
                    "	f.numero,\n" +
                    "	f.numcep cep,\n" +
                    "	f.cdcida cidadeibge,\n" +
                    "	f.cidade,\n" +
                    "	f.estado,\n" +
                    "	f.telef1 tel1,\n" +
                    "	f.telef2 tel2,\n" +
                    "	f.numfax fax,\n" +
                    "	f.confor contato,\n" +
                    "	f.emafor email,\n" +
                    "	f.ativos\n" +
                    "from\n" +
                    "	sac412 f"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setAtivo(rst.getInt("ativos") == 1);
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    imp.setIe_rg(rst.getString("ie"));
                    imp.setInsc_municipal(rst.getString("im"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setCep(rst.getString("cep"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setIbge_municipio(rst.getInt("cidadeibge"));
                    imp.setUf(rst.getString("estado"));
                    imp.setTel_principal(rst.getString("tel1"));

                    if ((rst.getString("email") != null)
                            && (!rst.getString("email").trim().isEmpty())) {
                        imp.addEmail("EMAIL", rst.getString("email").toLowerCase(), TipoContato.NFE);
                    }
                    if ((rst.getString("tel2") != null)
                            && (!rst.getString("tel2").trim().isEmpty())) {
                        imp.addTelefone("TELEFONE 2", rst.getString("tel2"));
                    }
                    if ((rst.getString("fax") != null)
                            && (!rst.getString("fax").trim().isEmpty())) {
                        imp.addTelefone("FAX", rst.getString("fax"));
                    }
                    if ((rst.getString("contato") != null)
                            && (!rst.getString("contato").trim().isEmpty())) {
                        imp.addContato(rst.getString("contato"), null, null, TipoContato.NFE, null);
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
                    "select \n" +
                    "	cdforn idforn,\n" +
                    "	cdprod idprod,\n" +
                    "	barras,\n" +
                    "	cdorig codigoexterno \n" +
                    "from \n" +
                    "	sac790"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rst.getString("idprod"));
                    imp.setIdFornecedor(rst.getString("idforn"));
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

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n" +
                    "	c.cdclie id,\n" +
                    "	c.nomcli razao,\n" +
                    "	c.cpfcgc cnpj,\n" +
                    "	c.numdrg ie,\n" +
                    "	c.insmun im,\n" +
                    "	c.ativos,\n" +
                    "	c.bloque bloq,\n" +
                    "	c.fantas,\n" +
                    "	c.endere endereco,\n" +
                    "	c.refere,\n" +
                    "	c.numero,\n" +
                    "	c.bairro,\n" +
                    "	c.numcep cep,\n" +
                    "	cida.cidade,\n" +
                    "	cida.estado,\n" +
                    "	cida.cdibge,\n" +
                    "	c.dtnasc,\n" +
                    "	c.dtcada datacadastro,\n" +
                    "	c.telefo tel,\n" +
                    "	c.numfax fax,\n" +
                    "	c.celula cel,\n" +
                    "	c.emails,\n" +
                    "	c.estciv estcivil,\n" +
                    "	c.limcre limite,\n" +
                    "	c.diaspz prazo,\n" +
                    "	c.diaven vencimento,\n" +
                    "	c.cdconv\n" +
                    "from \n" +
                    "	sac311 c\n" +
                    "left outer join sac215 cida on c.cdcida = cida.cdcida"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    
                    imp.setId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setAtivo(rst.getInt("ativos") == 1);
                    imp.setBloqueado("S".equals(rst.getString("bloq")));
                    imp.setFantasia(rst.getString("fantas"));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setInscricaoestadual(rst.getString("ie"));
                    imp.setInscricaoMunicipal(rst.getString("im"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("refere"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setMunicipioIBGE(rst.getInt("cdibge"));
                    imp.setUf(rst.getString("estado"));
                    imp.setCep(rst.getString("cep"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setDataNascimento(rst.getDate("dtnasc"));
                    imp.setTelefone(rst.getString("tel"));
                    imp.setCelular(rst.getString("cel"));
                    imp.setFax(rst.getString("fax"));
                    imp.setEmail(rst.getString("emails"));
                    imp.setValorLimite(rst.getDouble("limite"));
                    
                    String estcivil = rst.getString("estcivil").trim();
                    
                    if(estcivil != null && !"".equals(estcivil)) {
                        switch(estcivil) {
                            case "A": imp.setEstadoCivil(TipoEstadoCivil.AMAZIADO);
                                break;
                            case "C": imp.setEstadoCivil(TipoEstadoCivil.CASADO);
                                break;    
                            case "D": imp.setEstadoCivil(TipoEstadoCivil.DIVORCIADO);
                                break;
                            case "S": imp.setEstadoCivil(TipoEstadoCivil.SOLTEIRO);
                                break; 
                            case "V": imp.setEstadoCivil(TipoEstadoCivil.VIUVO);
                                break;
                            default: imp.setEstadoCivil(TipoEstadoCivil.NAO_INFORMADO);
                                break;
                        }
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

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	rece.cdloja,\n" +
                    "	rece.lancam,\n" +
                    "	rece.cdclie,\n" +
                    "	rece.docume,\n" +
                    "	rece.cdfunc,\n" +
                    "	rece.cdform,\n" +
                    "	rece.dtdigi,\n" +
                    "	convert(char(8), rece.dtdigi, 8) hora,\n" +
                    "	rece.dtemis,\n" +
                    "	rece.prazos,\n" +
                    "	rece.dtvenc,\n" +
                    "	rece.vendas,\n" +
                    "	rece.vlrdoc,\n" +
                    "	case\n" +
                    "		when datediff(day, rece.dtvenc, para.dtmovi) > 0 \n" +
                    "			then round((rece.vlrdoc * (para.jurmes * datediff (day, rece.dtvenc, para.dtmovi)) / 100 / 30), 2)\n" +
                    "		else 0.0\n" +
                    "	end as juros,\n" +
                    "	case\n" +
                    "		when datediff (day, rece.dtvenc, para.dtmovi) > 0 \n" +
                    "			then round((rece.vlrdoc + (rece.vlrdoc * (para.jurmes * datediff (day, rece.dtvenc, para.dtmovi)) / 100 / 30)), 2)\n" +
                    "		else rece.vlrdoc\n" +
                    "	end as total\n" +
                    "from\n" +
                    "	sac511 rece with (nolock)\n" +
                    "left outer join sac311 clie on\n" +
                    "	rece.cdclie = clie.cdclie\n" +
                    "left outer join sac215 cida on\n" +
                    "	clie.cdcida = cida.cdcida\n" +
                    "left outer join sac216 form on\n" +
                    "	rece.cdform = form.cdform\n" +
                    "left outer join sac219 func on\n" +
                    "	rece.cdfunc = func.cdfunc\n" +
                    "left outer join sac251 loja on\n" +
                    "	rece.cdloja = loja.cdloja\n" +
                    "left outer join sac999 para on\n" +
                    "	" + getLojaOrigem() + " = para.cdloja\n" +
                    "where\n" +
                    "	rece.dtrcto is null and \n" +
                    "	rece.status = 0 and \n" +
                    "	rece.cdloja = " + getLojaOrigem()
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    
                    imp.setId(rst.getString("lancam"));
                    imp.setDataEmissao(rst.getDate("dtemis"));
                    imp.setNumeroCupom(rst.getString("vendas"));
                    imp.setValor(rst.getDouble("vlrdoc"));
                    imp.setIdCliente(rst.getString("cdclie"));
                    imp.setDataVencimento(rst.getDate("dtvenc"));
                    imp.setJuros(rst.getDouble("juros"));
                    imp.setObservacao("Func.: " + rst.getString("cdfunc") + " Forma:" + rst.getString("cdform"));
                    
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
                    "	rece.cdloja,\n" +
                    "	rece.cdclie,\n" +
                    "   rece.lancam,\n" +        
                    "	rece.docume,\n" +
                    "	rece.bancos,\n" +
                    "   rece.dtemis,\n" +
                    "	rece.dtvenc,\n" +        
                    "	rece.agenci,\n" +
                    "	rece.contas,\n" +
                    "	rece.cheque,\n" +
                    "	rece.dtdepo,\n" +
                    "	rece.vendas,\n" +
                    "	round(coalesce(rece.vlrchq, 0), 2) as vlrrec,\n" +
                    "	clie.nomcli,\n" +
                    "   clie.cpfcgc,\n" +
                    "   clie.telefo,\n" +        
                    "	clie.numdrg,\n" +        
                    "	form.formas\n" +
                    "from\n" +
                    "	sac511 rece with (nolock)\n" +
                    "left outer join sac216 form on\n" +
                    "	rece.cdform = form.cdform\n" +
                    "left outer join sac311 clie on\n" +
                    "	rece.cdclie = clie.cdclie\n" +
                    "where\n" +
                    "	rece.dtrcto is not null\n" +
                    "	and rece.status = 0 and \n" +
                    "	form.cdform in (2, 3) and \n" +
                    "	rece.cdloja = " + getLojaOrigem())) {
                while(rs.next()) {
                    ChequeIMP imp = new ChequeIMP();
                    
                    imp.setId(rs.getString("lancam"));
                    imp.setDataDeposito(rs.getDate("dtvenc"));
                    imp.setNumeroCheque(rs.getString("docume"));
                    imp.setDate(rs.getDate("dtemis"));
                    imp.setBanco(rs.getInt("bancos"));
                    imp.setAgencia(rs.getString("agenci"));
                    imp.setConta(rs.getString("contas"));
                    imp.setNome(rs.getString("nomcli"));
                    imp.setTelefone(rs.getString("telefo"));
                    imp.setValor(rs.getDouble("vlrrec"));
                    imp.setNumeroCupom(rs.getString("vendas"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }
}
