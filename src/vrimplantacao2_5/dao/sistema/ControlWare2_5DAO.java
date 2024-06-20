/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2_5.dao.sistema;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import static vr.core.utils.StringUtils.LOG;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.NutricionalToledoVO;
import vrimplantacao.vo.vrimplantacao.NutricionalToledoItemVO;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoInscricao;
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;
import vrimplantacao2_5.dao.conexao.ConexaoPostgres;

/**
 *
 * @author Wagner
 */
public class ControlWare2_5DAO extends InterfaceDAO implements MapaTributoProvider {

    private String lojaCliente;
    
    public boolean importarFamiliaDeSimilar = false;
    public int tipoPlanoContaReceber;
    public int tipoPlanoContaPagar;

    public String getLojaCliente() {
        return this.lojaCliente;
    }

    @Override
    public String getSistema() {
        return "ControlWare";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.VALIDADE,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.QTD_EMBALAGEM_COTACAO,
                OpcaoProduto.QTD_EMBALAGEM_EAN,
                OpcaoProduto.ATIVO,
                OpcaoProduto.PESO_BRUTO,
                OpcaoProduto.PESO_LIQUIDO,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.ESTOQUE_MINIMO,
                OpcaoProduto.MARGEM,
                OpcaoProduto.VENDA_PDV,
                OpcaoProduto.PRECO,
                OpcaoProduto.CUSTO,
                OpcaoProduto.CUSTO_COM_IMPOSTO,
                OpcaoProduto.CUSTO_SEM_IMPOSTO,
                OpcaoProduto.NCM,
                OpcaoProduto.EXCECAO,
                OpcaoProduto.CEST,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.ICMS,
                OpcaoProduto.DATA_CADASTRO,
                OpcaoProduto.PDV_VENDA
        ));
    }

    @Override
    public Set<OpcaoFornecedor> getOpcoesDisponiveisFornecedor() {
        return new HashSet<>(Arrays.asList(
                OpcaoFornecedor.DADOS,
                OpcaoFornecedor.RAZAO_SOCIAL,
                OpcaoFornecedor.NOME_FANTASIA,
                OpcaoFornecedor.CNPJ_CPF,
                OpcaoFornecedor.INSCRICAO_ESTADUAL,
                OpcaoFornecedor.INSCRICAO_MUNICIPAL,
                OpcaoFornecedor.PRODUTO_FORNECEDOR,
                OpcaoFornecedor.PAGAR_FORNECEDOR,
                OpcaoFornecedor.TELEFONE
        ));
    }

    @Override
    public Set<OpcaoCliente> getOpcoesDisponiveisCliente() {
        return new HashSet<>(Arrays.asList(
                OpcaoCliente.DADOS,
                OpcaoCliente.ENDERECO,
                OpcaoCliente.CONTATOS,
                OpcaoCliente.DATA_CADASTRO,
                OpcaoCliente.DATA_NASCIMENTO,
                OpcaoCliente.TELEFONE,
                OpcaoCliente.ESTADO_CIVIL,
                OpcaoCliente.EMPRESA,
                OpcaoCliente.SALARIO,
                OpcaoCliente.BLOQUEADO,
                OpcaoCliente.OBSERVACOES2,
                OpcaoCliente.OBSERVACOES,
                OpcaoCliente.NUMERO,
                OpcaoCliente.COMPLEMENTO,
                OpcaoCliente.SITUACAO_CADASTRO,
                OpcaoCliente.RECEBER_CREDITOROTATIVO));
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                     "select \n"
                    + "	codcf id,\n"
                    + "	descricao\n"
                    + "from\n"
                    + "	classfiscal\n"
                    + "order by\n"
                    + "	id"
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(
                            rst.getString("id"),
                            rst.getString("descricao")
                    ));
                }
            }
        }
        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	dep.coddepto id_merc1,\n"
                    + "	dep.nome merc1,\n"
                    + "	grp.codgrupo id_merc2,\n"
                    + "	grp.descricao merc2,\n"
                    + "	sgr.codsubgrupo id_merc3,\n"
                    + "	sgr.descricao merc3\n"
                    + "from\n"
                    + "	departamento dep\n"
                    + "	left join grupoprod grp on dep.coddepto = grp.coddepto\n"
                    + "	left join subgrupo sgr on grp.codgrupo = sgr.codgrupo\n"
                    + "order by\n"
                    + "	id_merc1,\n"
                    + "	id_merc2,\n"
                    + "	id_merc3"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setMerc1ID(rst.getString("id_merc1"));
                    imp.setMerc1Descricao(rst.getString("merc1"));
                    imp.setMerc2ID(rst.getString("id_merc2"));
                    imp.setMerc2Descricao(rst.getString("merc2"));
                    imp.setMerc3ID(rst.getString("id_merc3"));
                    imp.setMerc3Descricao(rst.getString("merc3"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }
    
    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();
        if (importarFamiliaDeSimilar) {
            try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
                try (ResultSet rs = stm.executeQuery(
                        "select\n"
                        + "	codsimilar,\n"
                        + "	descricao\n"
                        + "from\n"
                        + "	simprod\n"
                        + "order by\n"
                        + "	2"
                )) {
                    while (rs.next()) {
                        FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rs.getString("codsimilar"));
                        imp.setDescricao(rs.getString("descricao"));

                        result.add(imp);
                    }
                }
            }
        } else {
            try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
                try (ResultSet rs = stm.executeQuery(
                        "select\n"
                        + "	codfamilia,\n"
                        + "	descricao\n"
                        + "from \n"
                        + "	familia \n"
                        + "order by \n"
                        + "	codfamilia"
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
        }

        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            stm.executeUpdate("set client_encoding to 'WIN1252';");
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	p.codproduto id,\n"
                    + "	p.descricaofiscal descricaocompleta,\n"
                    + "	p.descricao descricaoreduzida,\n"
                    + "	p.descricaofiscal descricaogondola,\n"
                    + "	case p.foralinha\n"
                    + "	when 'S' then 0\n"
                    + "	else 1 end as id_Situacaocadastro,\n"
                    + "	p.datainclusao dataCadastro,\n"
                    + "	p.coddepto mercadologico1,\n"
                    + "	p.codgrupo mercadologico2,\n"
                    + "	p.codsubgrupo mercadologico3,\n"
                    + " case when p.pesado = 'S' then true else false end as e_balanca,\n"
                    + "	replace(ncm.codigoncm,'.','') ncm,\n"
                    + "	replace(cest.cest,'.','') cest,\n"
                    + "	codfamilia id_familiaproduto,\n"
                    + " codsimilar id_familiasimilar,\n"
                    + "	pe.margemvrj margem,\n"
                    + "	emb.quantidade qtdEmbalagem,\n"
                    + "	coalesce(ean.codean,'') codigobarras,\n"
                    + "	case when pe.diasvalidade = 0 then p.diasvalidade else pe.diasvalidade end as validade,\n"
                    + "	un.sigla id_tipoEmbalagem,\n"
                    + "	piscofinsent.codcst idTipoPisCofinsCredito,\n"
                    + "	piscofinssai.codcst idTipoPisCofinsDebito,\n"
                    + "	coalesce(natr.codigo, p.natreceita::integer) naturezaReceita,\n"
                    + "	pe.precovrj precovenda,\n"
                    + "	pe.custorep custocomnota,\n"
                    + "	pe.custorep custosemnota,\n"
                    + "	pe.sldsaida estoque,\n"
                    + " pe.sldatual estoqueatual,\n"
                    + "	pe.estminimo estoque_minimo,\n"
                    + "	pe.estmaximo estoque_maximo,\n"
                    + "	estab.uf idEstado,\n"
                    + "	cast(icms_s.codcst as integer) AS icms_s_cst,\n"
                    + "	icms_s.aliqicms icms_s_aliq,\n"
                    + "	icms_s.aliqredicms icms_s_reducao,\n"
                    + "	cast(icms_e.codcst as integer) AS icms_e_cst,\n"
                    + "	icms_e.aliqicms icms_e_aliq,\n"
                    + "	icms_e.aliqredicms icms_e_reducao,\n"
                    + " p.codcfpdv idaliquotadebito,\n"
                    + " p.codcfnfe idaliquotacredito,\n"
                    + "	p.pesoliq pesoliquido,\n"
                    + "	p.pesobruto\n"
                    + "from \n"
                    + "	produto p\n"
                    + "	join produtoestab pe on pe.codproduto = p.codproduto\n"
                    + "	left join ncm on ncm.idncm = p.idncm\n"
                    + "	left join cest on ncm.idcest = cest.idcest\n"
                    + "	join embalagem emb on p.codembalvda = emb.codembal\n"
                    + "	join unidade un on emb.codunidade = un.codunidade\n"
                    + "	left join produtoean ean on ean.codproduto = p.codproduto\n"
                    + "	join piscofins piscofinsent ON p.codpiscofinsent = piscofinsent.codpiscofins\n"
                    + "	join piscofins piscofinssai ON p.codpiscofinssai = piscofinssai.codpiscofins	\n"
                    + "	left join natreceita natr on natr.natreceita = p.natreceita\n"
                    + "	join estabelecimento estab on pe.codestabelec = estab.codestabelec\n"
                    + "	join classfiscal icms_s ON p.codcfpdv = icms_s.codcf\n"
                    + "	join classfiscal icms_e ON p.codcfnfe = icms_e.codcf\n"
                    + "where estab.codestabelec = " + getLojaOrigem() + "\n"
                    + "order by\n"
                    + "	p.codproduto"
            )) {
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id"));
                    imp.setDescricaoCompleta(rs.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rs.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(rs.getString("descricaogondola"));
                    imp.seteBalanca(rs.getBoolean("e_balanca"));
                    imp.setEan(rs.getString("codigobarras"));
                    if (imp.isBalanca()) {
                        imp.setEan(imp.getImportId());
                    }
                    imp.setPrecovenda(rs.getDouble("precovenda"));
                    imp.setCustoComImposto(rs.getDouble("custocomnota"));
                    imp.setCustoSemImposto(rs.getDouble("custosemnota"));
                    imp.setEstoque(rs.getDouble("estoqueatual"));
                    imp.setEstoqueMaximo(rs.getDouble("estoque_maximo"));
                    imp.setEstoqueMinimo(rs.getDouble("estoque_minimo"));
                    imp.setPesoBruto(rs.getDouble("pesobruto"));
                    imp.setPesoLiquido(rs.getDouble("pesoliquido"));
                    imp.setSituacaoCadastro(rs.getInt("id_situacaocadastro"));
                    imp.setDataCadastro(rs.getDate("datacadastro"));
                    imp.setCodMercadologico1(rs.getString("mercadologico1"));
                    imp.setCodMercadologico2(rs.getString("mercadologico2"));
                    imp.setCodMercadologico3(rs.getString("mercadologico3"));

                    imp.setValidade(rs.getInt("validade"));
                    imp.setNcm(rs.getString("ncm"));
                    imp.setCest(rs.getString("cest"));
                    if (importarFamiliaDeSimilar) {
                        imp.setIdFamiliaProduto(rs.getString("id_familiasimilar"));
                    } else {
                        imp.setIdFamiliaProduto(rs.getString("id_familiaproduto"));
                    }
                    
                    /*
                        Calcular Margem sobre custo se necessário no campo margem
                        margem = round((precovenda / (custocomimposto / 100)) - 100, 2)
                    */
                    
                    imp.setMargem(rs.getDouble("margem"));
                    imp.setQtdEmbalagem(rs.getInt("qtdembalagem"));
                    imp.setTipoEmbalagem(rs.getString("id_tipoembalagem"));
                    imp.setPiscofinsCstCredito(rs.getString("idtipopiscofinscredito"));
                    imp.setPiscofinsCstDebito(rs.getString("idtipopiscofinsdebito"));
                    imp.setPiscofinsNaturezaReceita(rs.getString("naturezareceita"));

                    imp.setIcmsCreditoId(rs.getString("idaliquotacredito"));
                    imp.setIcmsCreditoForaEstadoId(imp.getIcmsDebitoId());
                    imp.setIcmsDebitoId(rs.getString("idaliquotadebito"));
                    imp.setIcmsConsumidorId(imp.getIcmsDebitoId());
                    imp.setIcmsDebitoForaEstadoId(imp.getIcmsDebitoId());
                    imp.setIcmsDebitoForaEstadoNfId(imp.getIcmsDebitoId());

                    result.add(imp);
                }
                return result;
            }
        }
    }
    
    @Override
    public List<NutricionalToledoVO> getNutricionalToledo() throws Exception {
        List<NutricionalToledoVO> result = new ArrayList<>();
        
        try(Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            stm.executeUpdate("set client_encoding to 'WIN1252';");
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	codnutricional id,\n" +
                    "	descricao,\n" +
                    "	qtdeporcao qtdporcao,\n" +
                    "	qtdecal qtdcaloria,\n" +
                    "	perccal,\n" +
                    "	qtdecarbo qtdcarboidrato,\n" +
                    "	perccarbo,\n" +
                    "	qtdeprot qtdproteina,\n" +
                    "	percprot,\n" +
                    "	qtdegord qtdgordura,\n" +
                    "	percgord,\n" +
                    "	qtdegordsat qtdgordurasaturada,\n" +
                    "	percgordsat,\n" +
                    "	qtdecolest qtdcolesterol,\n" +
                    "	perccolest,\n" +
                    "	qtdefibra qtdfibra,\n" +
                    "	percfibra,\n" +
                    "	qtdeferro qtdferro,\n" +
                    "	percferro,\n" +
                    "	qtdecal qtdcalcio,\n" +
                    "	perccalcio,\n" +
                    "	qtdesodio qtdsodio,\n" +
                    "	percsodio,\n" +
                    "	qtdegordtrans qtdgordtrans,\n" +
                    "	percgordtrans,\n" +
                    "	unidporcao unidade,\n" +
                    "	intmedcas medidaint,\n" +
                    "	decmedcas decimalmedidacase,\n" +
                    "	medcaseira\n" +
                    "from \n" +
                    "	nutricional")) {
                while(rs.next()) {
                    NutricionalToledoVO vo = new NutricionalToledoVO();
                    
                    vo.setId(rs.getInt("id"));
                    vo.setDescricao(rs.getString("descricao"));
                    vo.setQuantidade(rs.getInt("qtdporcao"));
                    vo.setCaloria(rs.getInt("qtdcaloria"));
                    vo.setPercentualcaloria(rs.getInt("perccal"));
                    vo.setCarboidrato(rs.getDouble("qtdcarboidrato"));
                    vo.setPercentualcarboidrato(rs.getInt("perccarbo"));
                    vo.setProteina(rs.getDouble("qtdproteina"));
                    vo.setPercentualproteina(rs.getInt("percprot"));
                    vo.setGordura(rs.getDouble("qtdgordura"));
                    vo.setPercentualgordura(rs.getInt("percgord"));
                    vo.setGordurasaturada(rs.getDouble("qtdgordurasaturada"));
                    vo.setPercentualgordurasaturada(rs.getInt("percgordsat"));
                    vo.setFibra(rs.getDouble("qtdfibra"));
                    vo.setPercentualfibra(rs.getInt("percfibra"));
                    vo.setFerro(rs.getDouble("qtdferro"));
                    vo.setPercentualferro(rs.getInt("percferro"));
                    vo.setCalcio(rs.getDouble("qtdcalcio"));
                    vo.setPercentualcalcio(rs.getInt("perccalcio"));
                    vo.setSodio(rs.getDouble("qtdsodio"));
                    vo.setPercentualsodio(rs.getInt("percsodio"));
                    vo.setGorduratrans(rs.getDouble("qtdgordtrans"));
                    vo.setMedidainteira(rs.getInt("medidaint"));
                    vo.setId_tipounidadeporcao(rs.getInt("unidade"));
                    vo.setId_tipomedida(rs.getInt("medcaseira"));
                    vo.setId_tipomedidadecimal(rs.getInt("decimalmedidacase"));
                    
                    addItemNutricional(vo);
                    
                    result.add(vo);
                }
            }
        }
        
        return result;
    }
    
    private void addItemNutricional(NutricionalToledoVO vo) throws SQLException {
        try(Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            stm.executeUpdate("set client_encoding to 'WIN1252';");
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	codproduto,\n" +
                    "	codnutricional\n" +
                    "from \n" +
                    "	produto\n" +
                    "where \n" +
                    "	codnutricional is not null and \n" +
                    "	codnutricional = " + vo.getId())) {
                while(rs.next()) {
                    NutricionalToledoItemVO voItem = new NutricionalToledoItemVO();
                    
                    voItem.setId_nutricionaltoledo(rs.getInt("codnutricional"));
                    voItem.setStrID(rs.getString("codproduto"));
                    
                    vo.vNutricionalToledoItem.add(voItem);
                }
            }
        }
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            stm.executeUpdate("set client_encoding to 'WIN1252';");
            try (ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "	f.codfornec id,\n" +
                    "	f.razaosocial razao,\n" +
                    "	f.nome fantasia,\n" +
                    "	f.cpfcnpj cnpj,\n" +
                    "	f.rgie inscricaoestadual,\n" +
                    "	f.fone1,\n" +
                    "	f.fone telefone,\n" +
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
                    "	case f.status when 'A' then 1 else 0 end as id_situacaocadastro,\n" +
                    "	pg.descricao condpagto,\n" +
                    "	pg.dia1,\n" +
                    "	pg.dia2,\n" +
                    "	pg.dia3,\n" +
                    "	pg.dia4,\n" +
                    "	fe.diasentrega,\n" +
                    "	fe.freqvisita\n" +
                    "from \n" +
                    "	fornecedor f\n" +
                    "left join cidade c on f.codcidade = c.codcidade\n" +
                    "left join estado e on c.uf = e.uf\n" +
                    "left join condpagto pg on f.codcondpagto = pg.codcondpagto\n" +
                    "left join fornecestab fe on f.codfornec = fe.codfornec\n" +
                    "where	\n" +
                    "	fe.codestabelec = " + getLojaOrigem()
            )) {
                while (rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setCnpj_cpf(rs.getString("cnpj"));
                    imp.setIe_rg(rs.getString("inscricaoestadual"));
                    imp.setAtivo(rs.getInt("id_situacaocadastro") == 1 ? true : false);
                    imp.setTel_principal(Utils.formataNumero(rs.getString("telefone")));
                    if (rs.getString("fone1") != null && !rs.getString("fone1").isEmpty()) {
                        imp.addContato("1", "TELEFONE 1", rs.getString("fone1"), "", TipoContato.COMERCIAL, "");
                    }
                    if (rs.getString("fone2") != null && !rs.getString("fone2").isEmpty()) {
                        imp.addContato("2", "TELEFONE 2", rs.getString("fone2"), "", TipoContato.COMERCIAL, "");
                    }
                    if (rs.getString("fone3") != null && !rs.getString("fone3").isEmpty()) {
                        imp.addContato("3", "TELEFONE 3", rs.getString("fone3"), "", TipoContato.COMERCIAL, "");
                    }
                    if (rs.getString("fax") != null && !rs.getString("fax").isEmpty()) {
                        imp.addContato("4", "FAX", rs.getString("fax"), "", TipoContato.COMERCIAL, "");
                    }
                    if (rs.getString("email") != null && !rs.getString("email").isEmpty()) {
                        imp.addContato("5", "EMAIL", "", "", TipoContato.COMERCIAL, rs.getString("email"));
                    }
                    if (rs.getString("site") != null && !rs.getString("site").isEmpty()) {
                        imp.addContato("6", rs.getString("site"), "", "", TipoContato.COMERCIAL, "");
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
                    imp.setPrazoEntrega(rs.getInt("diasentrega"));
                    imp.setPrazoVisita(rs.getInt("freqvisita"));
                    
                    if((rs.getString("dia1") != null) && (!"".equals(rs.getString("dia1")))) {
                        imp.addCondicaoPagamento(rs.getInt("dia1"));
                    }
                    
                    if((rs.getString("dia2") != null) && (!"".equals(rs.getString("dia2")))) {
                        imp.addCondicaoPagamento(rs.getInt("dia2"));
                    }
                    
                    if((rs.getString("dia3") != null) && (!"".equals(rs.getString("dia3")))) {
                        imp.addCondicaoPagamento(rs.getInt("dia3"));
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
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            stm.executeUpdate("set client_encoding to 'WIN1252';");
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	codprodfornec id,\n"
                    + "	codproduto idproduto,\n"
                    + "	codfornec idfornecedor,\n"
                    + "	reffornec codigoexterno\n"
                    + "from\n"
                    + "	prodfornec\n"
                    + "order by\n"
                    + "	2, 1")) {
                while (rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdFornecedor(rs.getString("idfornecedor"));
                    imp.setIdProduto(rs.getString("idproduto"));
                    imp.setCodigoExterno(rs.getString("codigoexterno"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            stm.executeUpdate("set client_encoding to 'WIN1252';");
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	c.codcliente id,\n"
                    + "	case c.codstatus when 1 then 1 when 2 then 1 else 0 end id_situacaocadastro,\n"
                    + "	case c.tppessoa when 'F' then 1 else 0 end id_tipoinscricao,\n"
                    + "	coalesce(c.razaosocial, c.nome) nome,\n"
                    + "	c.enderres res_end,\n"
                    + "	c.numerores res_num,\n"
                    + "	c.complementores res_compl,\n"
                    + "	c.bairrores res_bairro,\n"
                    + "	c.cepres res_cep,\n"
                    + "	res_cid.codoficial res_id_municipio,\n"
                    + "	res_uf.codoficial res_id_estado,\n"
                    + "	c.enderfat cob_end,\n"
                    + "	c.numerofat cob_num,\n"
                    + "	c.complementofat cob_compl,\n"
                    + "	c.bairrofat cob_bairro,\n"
                    + "	c.cepfat cob_cep,\n"
                    + "	cob_cid.codoficial cob_id_municipio,\n"
                    + "	cob_uf.codoficial cob_id_estado,\n"
                    + "	c.enderent ent_end,\n"
                    + "	c.numeroent ent_num,\n"
                    + "	c.complementoent ent_compl,\n"
                    + "	c.bairroent ent_bairro,\n"
                    + "	c.cepent ent_cep,\n"
                    + "	ent_cid.codoficial ent_id_municipio,\n"
                    + "	ent_uf.codoficial ent_id_estado,\n"
                    + "	c.cpfcnpj cnpj,\n"
                    + "	c.foneres fone1,\n"
                    + "	c.codempresa id_conveniado_com_cliente,\n"
                    + "	case c.limite1 when 0 then c.limite2 else c.limite1 end as limite,\n"
                    + "	c.rgie,\n"
                    + "	c.dtinclusao datacadastro,\n"
                    + "	c.dtnascto dataNascimento,\n"
                    + "	case c.codstatus when 1 then false when 2 then true else true end bloqueado,\n"
                    + "	c.fonefat fone2,\n"
                    + "	c.faxfat fax,\n"
                    + "	c.celular,\n"
                    + "	c.observacao,\n"
                    + "	c.email,\n"
                    + "	case c.sexo when 'F' then 0 else 1 end sexo,\n"
                    + "	(select razaosocial  from cliente where c.codempresa = codcliente) empresa,\n"
                    + "	c.respcargo1 cargo,\n"
                    + "	c.salario,\n"
                    + "	0 as estadoCivil,\n"
                    + "	nomeconj conjuge	\n"
                    + "from\n"
                    + "	cliente c\n"
                    + "	left join cidade cob_cid on cob_cid.codcidade = c.codcidadefat\n"
                    + "	left join estado cob_uf on cob_uf.uf = c.uffat\n"
                    + "	left join cidade ent_cid on ent_cid.codcidade = c.codcidadeent\n"
                    + "	left join estado ent_uf on ent_uf.uf = c.ufent\n"
                    + "	left join cidade res_cid on res_cid.codcidade = c.codcidaderes\n"
                    + "	left join estado res_uf on res_uf.uf = c.ufres\n"
                    + "order by\n"
                    + "	c.codcliente")) {
                while (rs.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rs.getString("id"));
                    imp.setCnpj(rs.getString("cnpj"));
                    imp.setInscricaoestadual(rs.getString("rgie"));
                    imp.setAtivo(rs.getInt("id_situacaocadastro") == 1);
                    imp.setTipoInscricao(rs.getInt("id_tipoinscricao") == 0 ? TipoInscricao.JURIDICA : TipoInscricao.FISICA);
                    imp.setRazao(rs.getString("nome"));
                    imp.setEndereco(rs.getString("res_end"));
                    imp.setNumero(rs.getString("res_num"));
                    imp.setComplemento(rs.getString("res_compl"));
                    imp.setBairro(rs.getString("res_bairro"));
                    imp.setCep(rs.getString("res_cep"));
                    imp.setMunicipioIBGE(rs.getInt("res_id_municipio"));
                    imp.setUfIBGE(rs.getInt("res_id_estado"));
                    imp.setTelefone(rs.getString("fone1"));
                    imp.setValorLimite(rs.getDouble("limite"));
                    imp.setDataCadastro(rs.getDate("datacadastro"));
                    imp.setDataNascimento(rs.getDate("datanascimento"));
                    imp.setBloqueado(rs.getBoolean("bloqueado"));
                    imp.setCelular(rs.getString("celular"));
                    imp.setObservacao(rs.getString("observacao"));
                    imp.setEmail(rs.getString("email"));
                    imp.setSexo(rs.getInt("sexo") == 1 ? TipoSexo.MASCULINO : TipoSexo.FEMININO);

                    result.add(imp);
                }
            }
        }
        return result;
    }
    
    public class PlanoConta {
        private int id;
        private String descricao;
        
        public PlanoConta(int id, String descricao) {
            this.id = id;
            this.descricao = descricao;
        }
        
        public void setId(int id) {
            this.id = id;
        }
        
        public int getId() {
            return id;
        }
        
        public void setDescricao(String descricao) {
            this.descricao = descricao;
        }
        
        public String getDescricao() {
            return descricao;
        }
        
        @Override
        public String toString() {
            return descricao;
        }
    }
    
    public List<PlanoConta> getEspecie() throws Exception {
        List<PlanoConta> result = new ArrayList<>();
        try(Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	codespecie,\n" +
                    "	descricao \n" +
                    "from \n" +
                    "	public.especie\n" +
                    "order by\n" +
                    "	descricao")) {
                while(rs.next()) {
                    result.add(new PlanoConta(rs.getInt("codespecie"), rs.getString("descricao")));
                }
            }
        }
        return result;
    } 

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();
        try(Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	codlancto id,\n" +
                    "	codestabelec id_loja,\n" +
                    "   numnotafis documento,\n" +
                    "   (SELECT "
                    +        "cupom.numeroecf\n" +
                    "    FROM "
                    +        "cupomlancto, cupom\n" +
                    "    WHERE "
                    +        "cupomlancto.codlancto = l.codlancto AND\n" +
                             "cupomlancto.idcupom = cupom.idcupom\n" +
                    "   LIMIT 1) AS ecf,\n" +
                    "	dtemissao emissao,\n" +
                    "	dtvencto vencimento,\n" +
                    "	parcela,\n" +
                    "	codparceiro idcliente,\n" +
                    "	valorliquido,\n" +
                    "	valordescto desconto,\n" +
                    "	valorjuros\n" +
                    "from \n" +
                    "	lancamento l \n" +
                    "where \n" +
                    "	pagrec = 'R' and\n" +
                    "	status = 'A' and\n" +
                    "	codestabelec = " + getLojaOrigem() + " and\n" +
                    "	codespecie = " + tipoPlanoContaReceber + "\n" +
                    "order by\n" +
                    "	dtlancto"
            )) {
                while(rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rs.getString("id"));
                    imp.setIdCliente(rs.getString("idcliente"));
                    imp.setNumeroCupom(rs.getString("documento"));
                    imp.setEcf(rs.getString("ecf"));
                    imp.setDataEmissao(rs.getDate("emissao"));
                    imp.setDataVencimento(rs.getDate("vencimento"));
                    imp.setParcela(rs.getInt("parcela"));
                    imp.setValor(rs.getDouble("valorliquido"));
                    imp.setJuros(rs.getDouble("valorjuros"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ContaPagarIMP> getContasPagar() throws Exception {
        List<ContaPagarIMP> result = new ArrayList<>();
        try(Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	codlancto id,\n" +
                    "	codestabelec id_loja,\n" +
                    "	numnotafis documento,\n" +
                    "	dtemissao emissao,\n" +
                    "	dtvencto vencimento,\n" +
                    "	parcela,\n" +
                    "	codparceiro idfornecedor,\n" +
                    "	valorliquido,\n" +
                    "	valordescto desconto,\n" +
                    "	valorjuros\n" +
                    "from \n" +
                    "	lancamento l \n" +
                    "where \n" +
                    "	pagrec = 'P' and\n" +
                    "	status = 'A' and\n" +
                    "	codestabelec = " + getLojaOrigem() + " and\n" +
                    "	codespecie = " + tipoPlanoContaPagar + " and\n" +
                    "   tipoparceiro = 'F'\n" +        
                    "order by\n" +
                    "	dtlancto")) {
                while(rs.next()) {
                    ContaPagarIMP imp = new ContaPagarIMP();
                    imp.setId(rs.getString("id"));
                    imp.setNumeroDocumento(rs.getString("documento"));
                    imp.setDataEmissao(rs.getDate("emissao"));
                    imp.addVencimento(rs.getDate("vencimento"), rs.getDouble("valorliquido"));
                    imp.setIdFornecedor(rs.getString("idfornecedor"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    private Date dataInicioVenda;
    private Date dataFimVenda;
    
    public void setDataInicioVenda(Date dataInicioVenda) {
        this.dataInicioVenda = dataInicioVenda;
    }
    
    public void setDataFimVenda(Date dataFimVenda) {
        this.dataFimVenda = dataFimVenda;
    }
    
    @Override
    public Iterator<VendaIMP> getVendaIterator() throws Exception {
        return new VendaIterator(getLojaOrigem(), dataInicioVenda, dataFimVenda);
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        return new VendaItemIterator(getLojaOrigem(), dataInicioVenda, dataFimVenda);
    }
    
    private static class VendaIterator implements Iterator<VendaIMP> {

        public final static SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");

        private Statement stm = ConexaoPostgres.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaIMP next;
        private Set<String> uk = new HashSet<>();

        private void obterNext() {
            try {
                SimpleDateFormat timestampDate = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat timestamp = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                if (next == null) {
                    if (rst.next()) {
                        next = new VendaIMP();
                        String id = rst.getString("idcupom");
                        if (!uk.add(id)) {
                            LOG.warning("Venda " + id + " já existe na listagem");
                        }
                        next.setId(id);
                        next.setNumeroCupom(Utils.stringToInt(rst.getString("cupom")));
                        next.setEcf(Utils.stringToInt(rst.getString("ecf")));
                        next.setData(rst.getDate("data"));
                        next.setIdClientePreferencial(rst.getString("codcliente"));
                        String horaInicio = timestampDate.format(rst.getDate("data")) + " " + rst.getString("hora");
                        String horaTermino = timestampDate.format(rst.getDate("data")) + " " + rst.getString("hora");
                        next.setHoraInicio(timestamp.parse(horaInicio));
                        next.setHoraTermino(timestamp.parse(horaTermino));
                        next.setCancelado("C".equals(rst.getString("status")));
                        next.setSubTotalImpressora(rst.getDouble("totalliquido"));
                        //next.setValorAcrescimo(rst.getDouble("totalacrescimo"));
                        next.setCpf(rst.getString("cpfcnpj"));
                        next.setNomeCliente(rst.getString("nome"));
                        String endereco
                                = Utils.acertarTexto(rst.getString("endereco")) + ","
                                + Utils.acertarTexto(rst.getString("numero")) + ","
                                + Utils.acertarTexto(rst.getString("complemento")) + ","
                                + Utils.acertarTexto(rst.getString("bairro")) + ","
                                + Utils.acertarTexto(rst.getString("cidade")) + "-"
                                + Utils.acertarTexto(rst.getString("estado")) + ","
                                + Utils.acertarTexto(rst.getString("cep"));
                        next.setEnderecoCliente(endereco);
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
                    "select\n" +
                    "	idcupom,\n" +
                    "	dtmovto as data,\n" +
                    "	hrmovto hora,\n" +
                    "	caixa ecf,\n" +
                    "	totalliquido,\n" +
                    "	totaldesconto,\n" +
                    "	cupom,\n" +
                    "	seqecf,\n" +
                    "	c.codcliente,\n" +
                    "	c.nome,\n" +
                    "	cupom.cpfcnpj,\n" +
                    "   totalacrescimo,\n" +
                    "   status,\n" +
                    "	c.enderres endereco,\n" +
                    "	c.numerores numero,\n" +
                    "	c.complementores complemento,\n" +
                    "	c.bairrores bairro,\n" +
                    "	cid.nome cidade,\n" +
                    "   uf.uf estado,\n" +
                    "	c.cepres cep,\n" +
                    "	status,\n" +
                    "	chavecfe\n" +
                    "from\n" +
                    "	cupom\n" +
                    "left join cliente c on cupom.codcliente = c.codcliente\n" +
                    "left join cidade cid on cid.codcidade = c.codcidadeent\n" +
                    "left join estado uf on uf.uf = c.ufres\n" +
                    "where\n" +
                    "	dtmovto between '" + dataInicio + "' and '" + dataTermino + "' and\n" +
                    "	codestabelec = " + idLojaCliente + "\n" +
                    "order by\n" +
                    "	dtmovto";
            LOG.log(Level.FINE, "SQL da venda: " + sql);
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

        private Statement stm = ConexaoPostgres.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaItemIMP next;

        private void obterNext() {
            try {
                if (next == null) {
                    if (rst.next()) {
                        next = new VendaItemIMP();
                        next.setId(rst.getString("id"));
                        next.setVenda(rst.getString("idcupom"));
                        next.setProduto(rst.getString("codproduto"));
                        next.setDescricaoReduzida(rst.getString("descricao"));
                        next.setQuantidade(rst.getDouble("quantidade"));
                        next.setPrecoVenda(rst.getDouble("preco"));
                        next.setTotalBruto(rst.getDouble("valortotal"));
                        //next.setValorAcrescimo(rst.getDouble("acrescimo"));
                        //next.setValorDesconto(rst.getDouble("desconto"));
                        next.setCancelado("C".equals(rst.getString("status")));
                        if("S".equals(rst.getString("pesado"))) {
                            next.setCodigoBarras(rst.getString("codproduto"));
                        } else {
                            String eanStr = rst.getString("ean");
                            if(eanStr != null && eanStr.length() > 14) {
                                next.setCodigoBarras(eanStr.substring(1, 14));
                            } else {
                                next.setCodigoBarras(eanStr);
                            }
                        }
                        
                        next.setUnidadeMedida(rst.getString("embalagem"));
                        
                        String trib = rst.getString("tptribicms");
                        if (trib == null || "".equals(trib)) {
                            trib = "I";
                        }

                        obterAliquota(next, trib, rst.getDouble("aliqicms"));
                    }
                }
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        /**
         * Método temporario, desenvolver um mapeamento eficiente da tributação.
         *
         * @param item
         * @throws SQLException
         */
        public void obterAliquota(VendaItemIMP item, String icms, double aliquota) throws SQLException {
            /*
             TA	7.00	ALIQUOTA 07%
             TB	12.00	ALIQUOTA 12%
             TC	18.00	ALIQUOTA 18%
             TD	25.00	ALIQUOTA 25%
             TE	11.00	ALIQUOTA 11%
             I	0.00	ISENTO
             F	0.00	SUBST TRIBUTARIA
             N	0.00	NAO INCIDENTE
             */
            int cst;
            double aliq;
            switch (icms) {
                case "I":
                    cst = 40;
                    aliq = 0;
                    break;
                case "F":
                    cst = 0;
                    aliq = 60;
                    break;
                case "T":
                    cst = 0;
                    aliq = aliquota;
                    break;
                case "N":
                    cst = 41;
                    aliq = 0;
                    break;     
                default:
                    cst = 40;
                    aliq = 0;
                    break;
            }
            item.setIcmsCst(cst);
            item.setIcmsAliq(aliq);
        }

        public VendaItemIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {
            this.sql
                    = "select\n" +
                    "	(c.caixa || '' || \n" +
                    "	i.idcupom || '' || \n" +
                    "	i.codproduto || '' || \n" +
                    "	i.codprodutopai || '' || \n" +
                    "	i.status) as id,\n" +
                    "	i.codmovimento,\n" +
                    "	i.codproduto,\n" +
                    "	(select\n" +
                    "		codean\n" +
                    "	from\n" +
                    "		produtoean ean\n" +
                    "	where\n" +
                    "		ean.codproduto = p.codproduto\n" +
                    "	limit 1) ean,\n" +
                    "	p.pesado,\n" +
                    "	p.descricao,\n" +
                    "	un.sigla embalagem,\n" +
                    "	i.idcupom,\n" +
                    "	i.quantidade,\n" +
                    "	i.preco,\n" +
                    "	i.desconto,\n" +
                    "   i.acrescimo,\n" +
                    "	i.valortotal,\n" +
                    "	i.aliqicms,\n" +
                    "	i.tptribicms,\n" +
                    "	i.status\n" +
                    "from\n" +
                    "	itcupom i\n" +
                    "join cupom c on i.idcupom = c.idcupom\n" +
                    "join produto p on p.codproduto = i.codproduto\n" +
                    "join embalagem emb on p.codembalvda = emb.codembal\n" +
                    "join unidade un on emb.codunidade = un.codunidade\n" +
                    "where\n" +
                    "	c.dtmovto between '" + dataInicio + "' and '" + dataTermino + "' and\n" +
                    "   c.codestabelec = " + idLojaCliente + " and\n" +
                    "   i.composicao in ('P', 'N')\n" +
                    "order by\n" +
                    "	c.dtmovto, idcupom";
            LOG.log(Level.FINE, "SQL da venda: " + sql);
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
}
