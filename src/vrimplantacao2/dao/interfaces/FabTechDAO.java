package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao.utils.Utils;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.vo.importacao.ClienteContatoIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorContatoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

public class FabTechDAO extends InterfaceDAO {

    @Override
    public String getSistema() {
        return "FabTech";
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select distinct\n" +
                    "	coalesce(ltrim(rtrim(p.Depto)),'') codMercadologico1,\n" +
                    "	c.descricao mercadologico1,\n" +
                    "	coalesce(ltrim(rtrim(p.Classe)),'') codMercadologico2,\n" +
                    "	d.descricao mercadologico2\n" +
                    "from\n" +
                    "	CPro_Produto p\n" +
                    "	LEFT OUTER JOIN CPro_Classe c ON\n" +
                    "		p.Classe = c.Classe\n" +
                    "	LEFT OUTER JOIN CPro_Depto d ON\n" +
                    "		p.Depto = d.Depto\n" +
                    "where\n" +
                    "	coalesce(ltrim(rtrim(p.Depto)),'') != '' and\n" +
                    "	coalesce(ltrim(rtrim(p.Classe)),'') != ''\n" +
                    "order by\n" +
                    "	codMercadologico1, codMercadologico2"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setMerc1ID(rst.getString("codMercadologico1"));
                    imp.setMerc1Descricao(rst.getString("mercadologico1"));
                    imp.setMerc2ID(rst.getString("codMercadologico2"));
                    imp.setMerc2Descricao(rst.getString("mercadologico2"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }
    
    

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            /*
            "SELECT \n" +
            "	dbo.CPro_Produto.Produto, \n" +
            "	dbo.CPro_Produto.Tipo, \n" +
            "	dbo.CPro_Produto.CodBarras, \n" +
            "	dbo.CPro_Produto.CodFabric, \n" +
            "	dbo.CPro_Produto.CodOriginal, \n" +
            "	dbo.CPro_Produto.Unidade, \n" +
            "	dbo.CPro_Produto.Unidade2, \n" +
            "   dbo.CPro_Produto.Unidade2TP, \n" +
            "	dbo.CPro_Produto.Unidade3, \n" +
            "	dbo.CPro_Produto.Unidade3TP, \n" +
            "	dbo.CPro_Produto.DescricaoCurta, \n" +
            "	dbo.CPro_Produto.Descricao, \n" +
            "	dbo.CPro_Produto.DescricaoCodBarras, \n" +
            "   dbo.CPro_Produto.DescricaoNF, \n" +
            "	dbo.CPro_Produto.NomeCientifico, \n" +
            "	dbo.CPro_Produto.DescricaoLonga, \n" +
            "	dbo.CPro_Produto.Descritivo, \n" +
            "	dbo.CPro_Produto.Fabricante, \n" +
            "	dbo.CPro_Fabricante.Descricao AS FabDesc, \n" +
            "   dbo.CPro_Produto.Depto, \n" +
            "	dbo.CPro_Produto.Validade, \n" +
            "	dbo.CPro_Depto.Descricao AS DepDesc, \n" +
            "	dbo.CPro_Produto.Classe, \n" +
            "	dbo.CPro_Classe.Descricao AS ClaDesc, \n" +
            "	dbo.CPro_Produto.DtInv, \n" +
            "   dbo.CPro_Produto.EstMin, \n" +
            "	dbo.CPro_Produto.EstMax, \n" +
            "	dbo.CPro_Produto.EstAtual, \n" +
            "	0 AS EstReservado, \n" +
            "	dbo.CPro_Produto.EstAtual AS EstDisponivel, \n" +
            "	dbo.CPro_Produto.EstInv, \n" +
            "	dbo.CPro_Produto.EstAlt, \n" +
            "   dbo.CPro_Produto.EstAdmDtInv, \n" +
            "	dbo.CPro_Produto.EstAdmInv, \n" +
            "	dbo.CPro_Produto.EstAdmAlt, \n" +
            "	dbo.CPro_Produto.EstAdmAtual, \n" +
            "	dbo.CPro_Produto.Moeda, \n" +
            "	dbo.CPro_Produto.ST_BaseCalculo, \n" +
            "   dbo.CPro_Produto.ST_ICMSEntrada, \n" +
            "	dbo.CPro_Produto.ST_ICMSSaida, \n" +
            "	dbo.CPro_Produto.ST_ICMSPagar, \n" +
            "	dbo.CPro_Produto.ST_ICMSDestino, \n" +
            "	dbo.CPro_Produto.ST_ICMSDestino_Red, \n" +
            "   dbo.CPro_Produto.ST_ICMSOrigem, \n" +
            "	dbo.CPro_Produto.ST_ICMSOrigem_Red, \n" +
            "	dbo.CPro_Produto.IVA, \n" +
            "	dbo.CPro_Produto.Pauta, \n" +
            "	dbo.CPro_Produto.ValorCusto, \n" +
            "   dbo.CPro_Produto.ValorCusto * dbo.CGe_MoedaDefault.Valor AS ValorCustoReais, \n" +
            "	dbo.CPro_Produto.Imp_FreteInternacional_P, \n" +
            "   dbo.CPro_Produto.ValorIPI, \n" +
            "	dbo.CPro_Produto.ValorIPI * dbo.CGe_MoedaDefault.Valor AS ValorIPIReais, \n" +
            "	dbo.CPro_Produto.ValorCusto_SubTotal_1, \n" +
            "	dbo.CPro_Produto.ST_ICMSPro, \n" +
            "   dbo.CPro_Produto.ST_ValorICMSPro, \n" +
            "	dbo.CPro_Produto.ValorCusto_SubTotal_2, \n" +
            "	dbo.CPro_Produto.Frete, \n" +
            "	dbo.CPro_Produto.ValorFrete, \n" +
            "	dbo.CPro_Produto.Imposto, \n" +
            "	dbo.CPro_Produto.ValorImposto, \n" +
            "   dbo.CPro_Produto.Outros, \n" +
            "	dbo.CPro_Produto.ValorOutros, \n" +
            "	dbo.CPro_Produto.ValorCusto_SubTotal_3, \n" +
            "	dbo.CPro_Produto.Desconto, \n" +
            "	dbo.CPro_Produto.ValorDesconto, \n" +
            "	dbo.CPro_Produto.ValorAnt, \n" +
            "   dbo.CPro_Produto.ValorMed, \n" +
            "	dbo.CPro_Produto.ValorAtual, \n" +
            "	dbo.CPro_Produto.ValorAtual * dbo.CGe_MoedaDefault.Valor AS ValorAtualReais, \n" +
            "	dbo.CPro_Produto.CalcVlMedio, \n" +
            "   dbo.CGe_MoedaDefault.Valor AS VlMoeda,\n" +
            "   (SELECT SUM(ValorCustoTotalReais) AS Expr1\n" +
            "       FROM dbo.VwCPro_ComposicaoCusto\n" +
            "       WHERE (ProdutoComp = dbo.CPro_Produto.Produto)) AS ValorCustoComposicao, \n" +
            "	dbo.CPro_Produto.Lucro, \n" +
            "	dbo.CPro_Produto.VlVenda, \n" +
            "	dbo.F_CriptoPrecoVenda(dbo.CPro_Produto.VlVenda) AS VlVendaCripto, \n" +
            "   dbo.CPro_Produto.VlVenda * dbo.CGe_MoedaDefault.Valor AS VlVendaReais, \n" +
            "	dbo.CPro_Produto.LucroMin, \n" +
            "	dbo.CPro_Produto.VlVendaMin, \n" +
            "   dbo.CPro_Produto.VlVendaMin * dbo.CGe_MoedaDefault.Valor AS VlVendaMinReais, \n" +
            "	dbo.CPro_Produto.IPI_Venda_ST, \n" +
            "	dbo.CPro_Produto.IPI_Venda_Aliquota, \n" +
            "   CASE WHEN (ISNULL(dbo.CPro_Produto.IPI_Venda_Aliquota, 0) / 100) = 0 THEN 0 ELSE ((dbo.CPro_Produto.VlVenda * dbo.CGe_MoedaDefault.Valor) * (ISNULL(dbo.CPro_Produto.IPI_Venda_Aliquota, 0) / 100)) \n" +
            "   END AS IPI_Venda_Valor, \n" +
            "	CASE WHEN (ISNULL(dbo.CPro_Produto.IPI_Venda_Aliquota, 0) / 100) = 0 THEN (dbo.CPro_Produto.VlVenda * dbo.CGe_MoedaDefault.Valor) \n" +
            "   ELSE ((dbo.CPro_Produto.VlVenda * dbo.CGe_MoedaDefault.Valor) * (1 + (ISNULL(dbo.CPro_Produto.IPI_Venda_Aliquota, 0) / 100))) END AS IPI_Venda_ValorVenda, \n" +
            "	dbo.CPro_Produto.Lucro02, \n" +
            "   dbo.CPro_Produto.VlVenda02, \n" +
            "	dbo.CPro_Produto.VlVenda02 * dbo.CGe_MoedaDefault.Valor AS VlVenda02Reais, \n" +
            "	dbo.CPro_Produto.LucroMin02, dbo.CPro_Produto.VlVendaMin02, \n" +
            "   dbo.CPro_Produto.VlVendaMin02 * dbo.CGe_MoedaDefault.Valor AS VlVendaMin02Reais, \n" +
            "	dbo.CPro_Produto.Promocao, \n" +
            "	dbo.CPro_Produto.PromVista, \n" +
            "	dbo.CPro_Produto.PromPrazo, \n" +
            "	dbo.CPro_Produto.LucroProm, \n" +
            "   dbo.CPro_Produto.VlVendaProm, \n" +
            "	dbo.CPro_Produto.VlVendaProm * dbo.CGe_MoedaDefault.Valor AS VlVendaPromReais, \n" +
            "	dbo.CPro_Produto.ComissaoTipo, \n" +
            "	dbo.CPro_Produto.ComissaoPorc, \n" +
            "   dbo.CPro_Produto.DetalhaPro, \n" +
            "	dbo.CPro_Produto.DescontoPro, \n" +
            "	dbo.CPro_Produto.ICMSTabela, \n" +
            "	dbo.CPro_TabICMS.Descricao AS ICMSDesc, \n" +
            "	dbo.CPro_ST.SubstituicaoTributaria AS ICMS_SubstTrib, \n" +
            "   dbo.CPro_Produto.ValorVendaST, \n" +
            "	dbo.CPro_Produto.ICMSTabela_CliFinal, \n" +
            "	CPro_TabICMS_CliFinal.Descricao AS ICMS_CliFinal_Desc, \n" +
            "	CPro_ST_CliFinal.SubstituicaoTributaria AS ICMS_CliFinal_SubstTrib, \n" +
            "   dbo.CPro_Produto.ICMSTabela_Dev_Cli, \n" +
            "	CPro_TabICMS_Dev_Cli.Descricao AS ICMS_Dev_Cli_Desc, \n" +
            "	CPro_ST_Dev_Cli.SubstituicaoTributaria AS ICMS_Dev_Cli_SubstTrib, \n" +
            "   dbo.CPro_Produto.ICMSTabela_Dev_CliFinal, \n" +
            "	CPro_TabICMS_Dev_CliFinal.Descricao AS ICMS_Dev_CliFinal_Desc, \n" +
            "	CPro_ST_Dev_CliFinal.SubstituicaoTributaria AS ICMS_Dev_CliFinal_SubstTrib, \n" +
            "   dbo.CPro_Produto.ICMSTabela_Dev_For, \n" +
            "	CPro_TabICMS_Dev_For.Descricao AS ICMS_Dev_For_Desc, \n" +
            "	CPro_ST_Dev_For.SubstituicaoTributaria AS ICMS_Dev_For_SubstTrib, \n" +
            "	dbo.CPro_Produto.PISTabela, \n" +
            "   dbo.CPro_TabPIS.Descricao AS PISDesc, \n" +
            "	dbo.CPro_TabPIS.Aliquota AS PISAliquota, \n" +
            "	dbo.CPro_TabPIS_ST.ST + ' - ' + dbo.CPro_TabPIS_ST.Descricao AS PIS_SitTrib, \n" +
            "	dbo.CPro_Produto.COFINSTabela, \n" +
            "   dbo.CPro_TabCOFINS.Descricao AS COFINSDesc, \n" +
            "	dbo.CPro_TabCOFINS.Aliquota AS COFINSAliquota, \n" +
            "	dbo.CPro_TabCOFINS_ST.ST + ' - ' + dbo.CPro_TabCOFINS_ST.Descricao AS COFINS_SitTrib, \n" +
            "   dbo.CPro_Produto.CESTTabela, \n" +
            "	dbo.CPro_TabCEST.CEST_Codigo, \n" +
            "	dbo.CPro_TabCEST.Item AS CEST_Item, \n" +
            "	dbo.CPro_TabCEST_Segmento.CEST_Segmento, \n" +
            "   dbo.CPro_TabCEST_Segmento.Descricao AS CEST_Seg_Desc, \n" +
            "	dbo.CPro_TabCEST_Segmento.CEST_Segmento + ' - ' + dbo.CPro_TabCEST_Segmento.Descricao AS CEST_Seg_Completa, \n" +
            "   dbo.CPro_TabCEST.NCM AS CEST_NCM, \n" +
            "	dbo.CPro_TabCEST.Descricao AS CEST_Desc, \n" +
            "	dbo.CPro_Produto.PesoUM, \n" +
            "	dbo.CPro_Produto.QtdeCX, \n" +
            "	dbo.CPro_Produto.PesoBruto, \n" +
            "	dbo.CPro_Produto.PAtivo, \n" +
            "   dbo.CPro_Produto.Local1, \n" +
            "	dbo.CPro_Produto.Local2, \n" +
            "	dbo.CPro_Produto.Local3, \n" +
            "	dbo.CPro_Produto.Local4, \n" +
            "	ISNULL(dbo.CPro_Produto.Local1, '') + ' ' + ISNULL(dbo.CPro_Produto.Local2, '') + ' ' + ISNULL(dbo.CPro_Produto.Local3, '') + ' ' + ISNULL(dbo.CPro_Produto.Local4, '') AS ProLoc, \n" +
            "	dbo.CPro_Produto.OT1, \n" +
            "	dbo.CPro_Produto.OT2, \n" +
            "	dbo.CPro_Produto.OT3, \n" +
            "	dbo.CPro_Produto.OF1_1, \n" +
            "   dbo.CPro_Produto.OF1_2, \n" +
            "	dbo.CPro_Produto.OF1_3, \n" +
            "	dbo.CPro_Produto.OF1_4, \n" +
            "	dbo.CPro_Produto.OF1_5, \n" +
            "	dbo.CPro_Produto.OF1_6, \n" +
            "	dbo.CPro_Produto.OF2_1, \n" +
            "	dbo.CPro_Produto.OF2_2, \n" +
            "	dbo.CPro_Produto.OF2_3, \n" +
            "   dbo.CPro_Produto.Grade, \n" +
            "	dbo.CPro_Produto.Modelo, \n" +
            "	dbo.CPro_Produto.Tamanho, \n" +
            "	dbo.CPro_Produto.FotoP, \n" +
            "	dbo.CPro_Produto.FotoG, \n" +
            "	dbo.CPro_Produto.DtReajuste, \n" +
            "	dbo.CPro_Produto.StatusFM, \n" +
            "   dbo.CPro_Produto.Formula, \n" +
            "	dbo.CPro_Produto.Imp, \n" +
            "	dbo.CPro_Produto.ConexaoImp, \n" +
            "	dbo.CPro_Produto.Inativo, \n" +
            "	dbo.CPro_Produto.SelCompra, \n" +
            "	dbo.CPro_Produto.ConexaoCompra, \n" +
            "	dbo.CPro_Produto.QtdeCompra, \n" +
            "   dbo.CPro_Produto.ClaFiscal, \n" +
            "	dbo.CPro_Produto.VendaManterValor, \n" +
            "	dbo.CPro_Produto.VendaAlteraDescr, \n" +
            "	dbo.CPro_Produto.Balanca, \n" +
            "	dbo.CPro_Produto.BalancaCheckout, \n" +
            "	dbo.CPro_Produto.Espessura, \n" +
            "   dbo.CPro_Produto.RegistroAlterado\n" +
            "FROM \n" +
            "	dbo.CPro_Produto \n" +
            "	LEFT OUTER JOIN dbo.CPro_TabICMS ON\n" +
            "		dbo.CPro_Produto.ICMSTabela = dbo.CPro_TabICMS.Codigo \n" +
            "	LEFT OUTER JOIN dbo.CPro_ST ON\n" +
            "		dbo.CPro_TabICMS.ST = dbo.CPro_ST.ST \n" +
            "	LEFT OUTER JOIN dbo.CPro_TabICMS AS CPro_TabICMS_CliFinal ON\n" +
            "		dbo.CPro_Produto.ICMSTabela_CliFinal = CPro_TabICMS_CliFinal.Codigo \n" +
            "	LEFT OUTER JOIN dbo.CPro_ST AS CPro_ST_CliFinal ON\n" +
            "		CPro_TabICMS_CliFinal.ST = CPro_ST_CliFinal.ST \n" +
            "	LEFT OUTER JOIN dbo.CPro_TabICMS AS CPro_TabICMS_Dev_Cli ON\n" +
            "		dbo.CPro_Produto.ICMSTabela_Dev_Cli = CPro_TabICMS_Dev_Cli.Codigo \n" +
            "	LEFT OUTER JOIN dbo.CPro_ST AS CPro_ST_Dev_Cli ON\n" +
            "		CPro_TabICMS_Dev_Cli.ST = CPro_ST_Dev_Cli.ST \n" +
            "	LEFT OUTER JOIN  dbo.CPro_TabICMS AS CPro_TabICMS_Dev_CliFinal ON\n" +
            "		dbo.CPro_Produto.ICMSTabela_Dev_CliFinal = CPro_TabICMS_Dev_CliFinal.Codigo \n" +
            "	LEFT OUTER JOIN dbo.CPro_ST AS CPro_ST_Dev_CliFinal ON\n" +
            "		CPro_TabICMS_Dev_CliFinal.ST = CPro_ST_Dev_CliFinal.ST \n" +
            "	LEFT OUTER JOIN dbo.CPro_TabICMS AS CPro_TabICMS_Dev_For ON\n" +
            "		dbo.CPro_Produto.ICMSTabela_Dev_For = CPro_TabICMS_Dev_For.Codigo \n" +
            "	LEFT OUTER JOIN dbo.CPro_ST AS CPro_ST_Dev_For ON\n" +
            "		CPro_TabICMS_Dev_For.ST = CPro_ST_Dev_For.ST \n" +
            "	LEFT OUTER JOIN dbo.CPro_TabPIS ON\n" +
            "		dbo.CPro_Produto.PISTabela = dbo.CPro_TabPIS.PISTabela \n" +
            "	LEFT OUTER JOIN dbo.CPro_TabPIS_ST ON\n" +
            "		dbo.CPro_TabPIS.ST = dbo.CPro_TabPIS_ST.ST \n" +
            "	LEFT OUTER JOIN dbo.CPro_TabCOFINS ON\n" +
            "		dbo.CPro_Produto.COFINSTabela = dbo.CPro_TabCOFINS.COFINSTabela\n" +
            "	LEFT OUTER JOIN dbo.CPro_TabCEST ON\n" +
            "		dbo.CPro_Produto.CESTTabela = dbo.CPro_TabCEST.CEST\n" +
            "	LEFT OUTER JOIN dbo.CPro_TabCEST_Segmento ON\n" +
            "		dbo.CPro_TabCEST.CEST_Segmento = dbo.CPro_TabCEST_Segmento.CEST_Segmento\n" +
            "	LEFT OUTER JOIN dbo.CPro_TabCOFINS_ST ON\n" +
            "		dbo.CPro_TabCOFINS.ST = dbo.CPro_TabCOFINS_ST.ST\n" +
            "	LEFT OUTER JOIN dbo.CPro_Classe ON\n" +
            "		dbo.CPro_Produto.Classe = dbo.CPro_Classe.Classe\n" +
            "	LEFT OUTER JOIN dbo.CPro_Depto ON\n" +
            "		dbo.CPro_Produto.Depto = dbo.CPro_Depto.Depto\n" +
            "	LEFT OUTER JOIN dbo.CPro_Fabricante ON\n" +
            "		dbo.CPro_Produto.Fabricante = dbo.CPro_Fabricante.Fabricante\n" +
            "	LEFT OUTER JOIN dbo.CGe_MoedaDefault ON\n" +
            "		dbo.CPro_Produto.Moeda = dbo.CGe_MoedaDefault.Moeda"
            */
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n" +
                    "	dbo.CPro_Produto.Produto id,\n" +
                    "	dbo.CPro_Produto.CodBarras ean,\n" +
                    "	dbo.CPro_Produto.Unidade unidade, \n" +
                    "	dbo.CPro_Produto.Balanca balanca,\n" +
                    "	dbo.CPro_Produto.Validade validade,\n" +
                    "	dbo.CPro_Produto.Descricao descricaocompleta,\n" +
                    "   coalesce(dbo.CPro_Produto.DescricaoNF, dbo.CPro_Produto.DescricaoCurta) descricaoreduzida,\n" +
                    "   dbo.CPro_Produto.Depto codMercadologico1, \n" +
                    "	dbo.CPro_Depto.Descricao AS mercadologico1, \n" +
                    "	dbo.CPro_Produto.Classe codMercadologico2, \n" +
                    "	dbo.CPro_Classe.Descricao AS mercadologico2,\n" +
                    "	dbo.CPro_Produto.PesoBruto pesobruto,\n" +
                    "   dbo.CPro_Produto.EstMin estoqueminimo, \n" +
                    "	dbo.CPro_Produto.EstMax estoquemaximo, \n" +
                    "	dbo.CPro_Produto.EstAtual estoque,\n" +
                    "	dbo.CPro_Produto.ValorCusto custosemimposto,\n" +
                    "	dbo.CPro_Produto.Lucro margem,\n" +
                    "	dbo.CPro_Produto.VlVenda precovenda,\n" +
                    "	case dbo.CPro_Produto.Inativo when 1 then 0 else 1 end as situacaocadastro,\n" +
                    "	dbo.CPro_Produto.ClaFiscal ncm,\n" +
                    "	dbo.CPro_TabCEST.CEST_Codigo cest,\n" +
                    "	dbo.CPro_TabPIS_ST.ST piscofins_cst,\n" +
                    "   dbo.CPro_Produto.ST_ICMSEntrada icms_cst_ent,\n" +
                    "	dbo.CPro_Produto.ST_ICMSSaida icms_cst_sai,\n" +
                    "	dbo.CPro_Produto.ST_ICMSDestino icms_aliq_sai,\n" +
                    "	dbo.CPro_Produto.ST_ICMSDestino_Red icms_red_sai,\n" +
                    "   dbo.CPro_Produto.ST_ICMSOrigem icms_aliq_ent,\n" +
                    "	dbo.CPro_Produto.ST_ICMSOrigem_Red icms_sai_ent\n" +
                    "FROM \n" +
                    "	dbo.CPro_Produto \n" +
                    "	LEFT OUTER JOIN dbo.CPro_TabICMS ON\n" +
                    "		dbo.CPro_Produto.ICMSTabela = dbo.CPro_TabICMS.Codigo \n" +
                    "	LEFT OUTER JOIN dbo.CPro_TabICMS AS CPro_TabICMS_CliFinal ON\n" +
                    "		dbo.CPro_Produto.ICMSTabela_CliFinal = CPro_TabICMS_CliFinal.Codigo \n" +
                    "	LEFT OUTER JOIN dbo.CPro_TabICMS AS CPro_TabICMS_Dev_Cli ON\n" +
                    "		dbo.CPro_Produto.ICMSTabela_Dev_Cli = CPro_TabICMS_Dev_Cli.Codigo \n" +
                    "	LEFT OUTER JOIN  dbo.CPro_TabICMS AS CPro_TabICMS_Dev_CliFinal ON\n" +
                    "		dbo.CPro_Produto.ICMSTabela_Dev_CliFinal = CPro_TabICMS_Dev_CliFinal.Codigo \n" +
                    "	LEFT OUTER JOIN dbo.CPro_TabICMS AS CPro_TabICMS_Dev_For ON\n" +
                    "		dbo.CPro_Produto.ICMSTabela_Dev_For = CPro_TabICMS_Dev_For.Codigo\n" +
                    "	LEFT OUTER JOIN dbo.CPro_TabPIS ON\n" +
                    "		dbo.CPro_Produto.PISTabela = dbo.CPro_TabPIS.PISTabela \n" +
                    "	LEFT OUTER JOIN dbo.CPro_TabPIS_ST ON\n" +
                    "		dbo.CPro_TabPIS.ST = dbo.CPro_TabPIS_ST.ST \n" +
                    "	LEFT OUTER JOIN dbo.CPro_TabCOFINS ON\n" +
                    "		dbo.CPro_Produto.COFINSTabela = dbo.CPro_TabCOFINS.COFINSTabela\n" +
                    "	LEFT OUTER JOIN dbo.CPro_TabCEST ON\n" +
                    "		dbo.CPro_Produto.CESTTabela = dbo.CPro_TabCEST.CEST\n" +
                    "	LEFT OUTER JOIN dbo.CPro_Classe ON\n" +
                    "		dbo.CPro_Produto.Classe = dbo.CPro_Classe.Classe\n" +
                    "	LEFT OUTER JOIN dbo.CPro_Depto ON\n" +
                    "		dbo.CPro_Produto.Depto = dbo.CPro_Depto.Depto\n" +
                    "order by dbo.CPro_Produto.Produto"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setEan(rst.getString("ean"));
                    imp.setTipoEmbalagem(rst.getString("unidade"));
                    imp.seteBalanca(rst.getBoolean("balanca"));
                    imp.setValidade(rst.getInt("validade"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setCodMercadologico1(rst.getString("codMercadologico1"));
                    imp.setCodMercadologico2(rst.getString("codMercadologico2"));
                    imp.setPesoBruto(rst.getDouble("pesobruto"));
                    imp.setPesoLiquido(rst.getDouble("pesobruto"));
                    imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
                    imp.setEstoqueMaximo(rst.getDouble("estoquemaximo"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setCustoComImposto(rst.getDouble("custosemimposto"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setSituacaoCadastro(SituacaoCadastro.getById(rst.getInt("situacaocadastro")));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstCredito(rst.getInt("piscofins_cst"));
                    imp.setIcmsCstEntrada(rst.getInt("icms_cst_ent"));
                    imp.setIcmsAliqEntrada(rst.getDouble("icms_aliq_ent"));
                    imp.setIcmsReducaoEntrada(rst.getDouble("icms_sai_ent"));
                    imp.setIcmsCstSaida(rst.getInt("icms_cst_sai"));
                    imp.setIcmsAliqSaida(rst.getDouble("icms_aliq_sai"));
                    imp.setIcmsReducaoSaida(rst.getDouble("icms_red_sai"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT dbo.CRH_Humano.Cadastro, dbo.CRH_Humano.Codigo, dbo.CRH_Humano.Sexo, dbo.CRH_Humano.Tipo, dbo.CRH_Humano.Desde, dbo.CRH_Humano.Nome, dbo.CRH_Humano.Nascimento, \n"
                    + "       dbo.CRH_Humano.RSocial, dbo.CRH_Humano.CPFCNPJ, dbo.CRH_Humano.RGIE, dbo.CRH_Humano.Endereco, dbo.CRH_Humano.Numero, ISNULL(dbo.CRH_Humano.Endereco, '') \n"
                    + "       + ', ' + ISNULL(dbo.CRH_Humano.Numero, '') AS Ender, dbo.CRH_Humano.Complemento, dbo.CRH_Humano.Bairro, dbo.CRH_Humano.Cidade, dbo.CRH_Humano.UF, ISNULL(dbo.CRH_Humano.Cidade, '') \n"
                    + "       + ' - ' + ISNULL(dbo.CRH_Humano.UF, '') AS Cid, dbo.CRH_Humano.CEP, dbo.CRH_Humano.Fone1, dbo.CRH_Humano.Fone2, dbo.CRH_Humano.Celular, dbo.CRH_Humano.Fax, dbo.CRH_Humano.Fone0800, \n"
                    + "       dbo.CRH_Humano.EMail, dbo.CRH_Humano.HomePage, dbo.CRH_Humano.CadCla, dbo.CRH_Humano.Classe, dbo.CRH_Classe.Descricao AS ClasseDesc, dbo.CRH_Humano.ContNome, \n"
                    + "       dbo.CRH_Humano.ContFone, dbo.CRH_Humano.ContRamal, dbo.CRH_Humano.ContFax, dbo.CRH_Humano.ContFaxRamal, dbo.CRH_Humano.ContCelular, dbo.CRH_Humano.ContEMail, \n"
                    + "       dbo.CRH_Humano.RamoAtiv, dbo.CRH_Humano.Inativo\n"
                    + "  FROM dbo.CRH_Humano LEFT OUTER JOIN\n"
                    + "       dbo.CRH_Classe ON dbo.CRH_Humano.CadCla = dbo.CRH_Classe.Cadastro AND dbo.CRH_Humano.Classe = dbo.CRH_Classe.Classe\n"
                    + " WHERE (dbo.CRH_Humano.Cadastro = 'For')\n"
                    + " ORDER BY dbo.CRH_Humano.Cadastro"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("Codigo"));
                    imp.setRazao(rst.getString("RSocial"));
                    imp.setFantasia(rst.getString("Nome"));
                    imp.setCnpj_cpf(Utils.formataNumero(rst.getString("CPFCNPJ")));
                    if ((rst.getString("RGIE") != null)
                            && (!rst.getString("RGIE").trim().isEmpty())) {
                        imp.setIe_rg(Utils.formataNumero(rst.getString("RGIE")));
                    } else {
                        imp.setIe_rg("ISENTO");
                    }
                    imp.setEndereco(rst.getString("Endereco"));
                    imp.setNumero(rst.getString("Numero"));
                    imp.setComplemento(rst.getString("Complemento"));
                    imp.setBairro(rst.getString("Bairro"));
                    imp.setMunicipio(rst.getString("Cidade"));
                    imp.setUf(rst.getString("UF"));
                    imp.setCep(Utils.formataNumero(rst.getString("CEP")));
                    imp.setAtivo((rst.getInt("Inativo") == 0 ? true : false));
                    imp.setTel_principal(Utils.formataNumero(rst.getString("Fone1")));

                    if ((rst.getString("Fone2") != null)
                            && (!rst.getString("Fone2").trim().isEmpty())) {
                        FornecedorContatoIMP contato = new FornecedorContatoIMP();
                        contato.setImportSistema(getSistema());
                        contato.setImportLoja(getLojaOrigem());
                        contato.setImportId("1");
                        contato.setNome("TELEFONE 2");
                        contato.setTelefone(Utils.formataNumero(rst.getString("Fone2").trim()));
                        imp.getContatos().put(contato, "1");
                    }

                    if ((rst.getString("Celular") != null)
                            && (!rst.getString("Celular").trim().isEmpty())) {
                        FornecedorContatoIMP contato = new FornecedorContatoIMP();
                        contato.setImportSistema(getSistema());
                        contato.setImportLoja(getLojaOrigem());
                        contato.setImportId("2");
                        contato.setNome("CELULAR");
                        contato.setCelular(Utils.formataNumero(rst.getString("Celular").trim()));
                        imp.getContatos().put(contato, "2");
                    }

                    if ((rst.getString("Fax") != null)
                            && (!rst.getString("Fax").trim().isEmpty())) {
                        FornecedorContatoIMP contato = new FornecedorContatoIMP();
                        contato.setImportSistema(getSistema());
                        contato.setImportLoja(getLojaOrigem());
                        contato.setImportId("3");
                        contato.setNome("FAX");
                        contato.setTelefone(Utils.formataNumero(rst.getString("Fax").trim()));
                        imp.getContatos().put(contato, "3");
                    }

                    if ((rst.getString("Fone0800") != null)
                            && (!rst.getString("Fone0800").trim().isEmpty())) {
                        FornecedorContatoIMP contato = new FornecedorContatoIMP();
                        contato.setImportSistema(getSistema());
                        contato.setImportLoja(getLojaOrigem());
                        contato.setImportId("4");
                        contato.setNome("FONE 0800");
                        contato.setTelefone(Utils.formataNumero(rst.getString("Fone0800")));
                        imp.getContatos().put(contato, "4");
                    }

                    if ((rst.getString("EMail") != null)
                            && (!rst.getString("EMAil").trim().isEmpty())) {
                        FornecedorContatoIMP contato = new FornecedorContatoIMP();
                        contato.setImportSistema(getSistema());
                        contato.setImportLoja(getLojaOrigem());
                        contato.setImportId("5");
                        contato.setNome("EMAIL");
                        contato.setEmail(rst.getString("EMail").trim());
                        imp.getContatos().put(contato, "5");
                    }

                    if ((rst.getString("HomePage") != null)
                            && (!rst.getString("HomePage").trim().isEmpty())) {
                        FornecedorContatoIMP contato = new FornecedorContatoIMP();
                        contato.setImportSistema(getSistema());
                        contato.setImportLoja(getLojaOrigem());
                        contato.setImportId("6");
                        contato.setNome("HOME PAGE");
                        contato.setEmail(rst.getString("HomePage"));
                        imp.getContatos().put(contato, "6");
                    }

                    if ((rst.getString("ContNome") != null)
                            && (!rst.getString("ContNome").trim().isEmpty())) {
                        FornecedorContatoIMP contato = new FornecedorContatoIMP();
                        contato.setImportSistema(getSistema());
                        contato.setImportLoja(getLojaOrigem());
                        contato.setImportId("7");
                        contato.setNome(rst.getString("ContNome"));
                        if ((rst.getString("ContFone") != null)
                                && (!rst.getString("ContFone").trim().isEmpty())) {
                            contato.setTelefone(Utils.formataNumero(rst.getString("ContFone")));
                        }
                        imp.getContatos().put(contato, "7");
                    }
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select Produto, Fornec, Docto, Qtde \n"
                    + "from dbo.VwCPro_ProdutoCompra"                    /*"SELECT dbo.CPro_Produto.Produto, dbo.CPro_Produto.Fabricante, dbo.CPro_Produto.Depto, dbo.CPro_Produto.Classe, dbo.MAC_Compra.CadFor, dbo.MAC_Compra.Fornec\n"
                    + "  FROM dbo.MAC_Compra \n"
                    + " INNER JOIN dbo.MAC_Recto ON dbo.MAC_Compra.Compra = dbo.MAC_Recto.Compra \n"
                    + " INNER JOIN dbo.CPro_Produto \n"
                    + " INNER JOIN dbo.MAC_RectoPro ON dbo.CPro_Produto.Produto = dbo.MAC_RectoPro.Produto ON dbo.MAC_Recto.Recto = dbo.MAC_RectoPro.Recto"*/
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setIdFornecedor(rst.getString("Fornec"));
                    imp.setIdProduto(rst.getString("Produto"));
                    imp.setCodigoExterno(rst.getString("Docto"));
                    imp.setQtdEmbalagem(rst.getInt("Qtde"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }
    
    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT dbo.CRH_Humano.Cadastro, dbo.CRH_Humano.Codigo, dbo.CRH_Humano.Tipo, dbo.CRH_Humano.Limite, dbo.CRH_Humano.Sexo, dbo.CRH_Humano.Desde, dbo.CRH_Humano.Nascimento, \n"
                    + "       dbo.CRH_Humano.RSocial, dbo.CRH_Humano.Nome, ISNULL(dbo.CRH_Humano.RSocial, '') AS NomeComp, dbo.CRH_Humano.CPFCNPJ, dbo.CRH_Humano.RGIE, \n"
                    + "       dbo.CRH_Humano.Endereco, dbo.CRH_Humano.Numero, dbo.CRH_Humano.Complemento, dbo.CRH_Humano.Bairro, dbo.CRH_Humano.Cidade, dbo.CRH_Humano.UF, dbo.CRH_Humano.CEP, \n"
                    + "       dbo.CRH_Humano.Fone1, dbo.CRH_Humano.Fone2, dbo.CRH_Humano.Celular, dbo.CRH_Humano.Fax, dbo.CRH_Humano.Fone0800, dbo.CRH_Humano.EMail, dbo.CRH_Humano.CadRep, dbo.CRH_Humano.Rep,\n"
                    + "       CRH_Rep.Nome AS RepNome, dbo.CRH_Humano.Cartao, dbo.CRH_Humano.CartaoGold, dbo.CRH_Humano.CartaoData, dbo.CRH_Humano.ConexaoImp, dbo.CRH_Humano.Imp, dbo.CRH_Humano.Inativo, \n"
                    + "       dbo.CRH_Humano.Conexao, dbo.CRH_Humano.CadCla, dbo.CRH_Humano.Classe, dbo.CRH_Classe.Descricao AS ClaDesc, dbo.CRH_Humano.CodBarras, dbo.CRH_Humano.OT1, dbo.CRH_Humano.OT2, \n"
                    + "       dbo.CRH_Humano.OT3, dbo.CRH_Humano.OF1_1, dbo.CRH_Humano.OF1_2, dbo.CRH_Humano.OF1_3, dbo.CRH_Humano.OF1_4, dbo.CRH_Humano.OF1_5, dbo.CRH_Humano.OF1_6, dbo.CRH_Humano.OF2_1, \n"
                    + "       dbo.CRH_Humano.OF2_2, dbo.CRH_Humano.OF2_3, dbo.CRH_Humano.OBS, dbo.CRH_Humano.ClassificacaoContribuinte, dbo.MFis_ClassificacaoContribuinte.Descricao AS ClasContDesc, \n"
                    + "       dbo.CRH_Humano.RegimeTributario, dbo.MFis_RegimeTributario.Descricao AS RegTribDesc, dbo.CRH_Humano.TabelaPreco\n"
                    + "  FROM dbo.CRH_Humano LEFT OUTER JOIN\n"
                    + "       dbo.CRH_Classe ON dbo.CRH_Humano.CadCla = dbo.CRH_Classe.Cadastro AND dbo.CRH_Humano.Classe = dbo.CRH_Classe.Classe LEFT OUTER JOIN\n"
                    + "       dbo.CRH_Humano AS CRH_Rep ON dbo.CRH_Humano.CadRep = CRH_Rep.Cadastro AND dbo.CRH_Humano.Rep = CRH_Rep.Codigo LEFT OUTER JOIN\n"
                    + "       dbo.MFis_ClassificacaoContribuinte ON dbo.CRH_Humano.ClassificacaoContribuinte = dbo.MFis_ClassificacaoContribuinte.Codigo LEFT OUTER JOIN\n"
                    + "       dbo.MFis_RegimeTributario ON dbo.CRH_Humano.RegimeTributario = dbo.MFis_RegimeTributario.Codigo\n"
                    + " WHERE (dbo.CRH_Humano.Cadastro = 'Cli')"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("Codigo"));
                    imp.setRazao(rst.getString("Nome"));
                    imp.setCnpj(Utils.formataNumero(rst.getString("CPFCNPJ")));
                    if ((rst.getString("RGIE") != null)
                            && (!rst.getString("RGIE").trim().isEmpty())) {
                        imp.setInscricaoestadual(Utils.formataNumero(rst.getString("RGIE")));
                    } else {
                        imp.setInscricaoestadual("ISENTO");
                    }
                    imp.setEndereco(rst.getString("Endereco"));
                    imp.setBairro(rst.getString("Bairro"));
                    imp.setNumero(rst.getString("Numero"));
                    imp.setComplemento(rst.getString("Complemento"));
                    imp.setMunicipio(rst.getString("Cidade"));
                    imp.setUf(rst.getString("UF"));
                    imp.setCep(Utils.formataNumero(rst.getString("Cep")));
                    imp.setAtivo((rst.getInt("Inativo") == 0 ? true : false));
                    imp.setObservacao(rst.getString("OBS"));
                    imp.setValorLimite(rst.getDouble("Limite"));
                    imp.setDataCadastro(rst.getDate("Desde"));
                    imp.setDataNascimento(rst.getDate("Nascimento"));
                    imp.setSexo("MASCULINO".equals(rst.getString("Sexo")) ? TipoSexo.MASCULINO : TipoSexo.FEMININO);
                    imp.setTelefone(Utils.formataNumero(rst.getString("Fone1")));

                    if ((rst.getString("Fone2") != null)
                            && (!rst.getString("Fone2").trim().isEmpty())) {
                        ClienteContatoIMP contato = new ClienteContatoIMP();
                        contato.setId("1");
                        contato.setNome("TELEFONE 2");
                        contato.setTelefone(Utils.formataNumero(rst.getString("Fone2").trim()));
                        imp.getContatos().add(contato);
                    }

                    if ((rst.getString("Celular") != null)
                            && (!rst.getString("Celular").trim().isEmpty())) {
                        ClienteContatoIMP contato = new ClienteContatoIMP();
                        contato.setId("2");
                        contato.setNome("CELULAR");
                        contato.setCelular(Utils.formataNumero(rst.getString("Celular").trim()));
                        imp.getContatos().add(contato);
                    }

                    if ((rst.getString("Fax") != null)
                            && (!rst.getString("Fax").trim().isEmpty())) {
                        ClienteContatoIMP contato = new ClienteContatoIMP();
                        contato.setId("3");
                        contato.setNome("FAX");
                        contato.setTelefone(Utils.formataNumero(rst.getString("Fax").trim()));
                        imp.getContatos().add(contato);
                    }

                    if ((rst.getString("Fone0800") != null)
                            && (!rst.getString("Fone0800").trim().isEmpty())) {
                        ClienteContatoIMP contato = new ClienteContatoIMP();
                        contato.setId("4");
                        contato.setNome("FONE 0800");
                        contato.setTelefone(Utils.formataNumero(rst.getString("Fone0800")));
                        imp.getContatos().add(contato);
                    }

                    if ((rst.getString("EMail") != null)
                            && (!rst.getString("EMail").trim().isEmpty())) {
                        ClienteContatoIMP contato = new ClienteContatoIMP();
                        contato.setId("5");
                        contato.setNome("EMAIL");
                        contato.setEmail(rst.getString("EMail").trim());
                        imp.getContatos().add(contato);
                    }
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> vResult = new ArrayList<>();
        String observacao;
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT dbo.MCR_Conta.Conta, dbo.MCR_Conta.CaixaVenda, dbo.MCR_Conta.Acerto, dbo.MCR_Conta.SitRec, dbo.MCR_Conta.SitVencto, dbo.MCR_Conta.Data, dbo.MCR_Conta.Status, dbo.MCR_Conta.DtStatus, \n"
                    + "       dbo.MCR_Conta.Situacao, dbo.MCR_Conta.Origem, dbo.MCR_Conta.DocOrigem, dbo.MCR_Conta.Descricao, dbo.MCR_Conta.Barras, dbo.MCR_Conta.PContas, dbo.CPC_Contas.Descricao AS PCDesc, \n"
                    + "       dbo.CPC_Contas.Tipo AS PCTipo, dbo.CPC_Contas.SubTipo AS PCSubTipo, dbo.MCR_Conta.Parcela, dbo.MCR_Conta.Docto, dbo.MCR_Conta.DoctoNF, dbo.MCR_Conta.DtPortador, dbo.MCR_Conta.Portador, \n"
                    + "       dbo.MCR_Conta.DtRestricao, dbo.MCR_Conta.Restricao, dbo.MCR_Conta.CadRepr, dbo.MCR_Conta.Repr, CRH_Repr.Nome AS ReprNome, dbo.MCR_Conta.CadCli, dbo.MCR_Conta.Cliente, \n"
                    + "       CRH_Cli.Tipo AS CliTipo, CRH_Cli.Nascimento AS CliNasc, CRH_Cli.Nome AS CliNome, CRH_Cli.RSocial AS CliRSocial, CRH_Cli.CPFCNPJ AS CliCPFCNPJ, CRH_Cli.RGIE AS CliRGIE, CRH_Cli.Endereco AS CliEnd, \n"
                    + "       CRH_Cli.Numero AS CliNum, CRH_Cli.Endereco + ', ' + CRH_Cli.Numero AS CliEndC, CRH_Cli.Complemento AS CliComp, CRH_Cli.Bairro AS CliBai, CRH_Cli.Cidade AS CliCid, CRH_Cli.UF AS CliUF, \n"
                    + "       CRH_Cli.Cidade + ' - ' + CRH_Cli.UF AS CliCidC, CRH_Cli.CEP AS CliCEP, CRH_Cli.Fone1 AS CliFone1, CRH_Cli.Fone2 AS CliFone2, CRH_Cli.Celular AS CliCel, CRH_Cli.Limite AS CliLimite, dbo.MCR_Conta.Vencto, \n"
                    + "       dbo.MCR_Conta.Valor, dbo.MCR_Conta.MultaP, dbo.MCR_Conta.MultaVl, dbo.MCR_Conta.MultaAplic, dbo.MCR_Conta.JurosPMes, dbo.MCR_Conta.JurosPDia, dbo.MCR_Conta.JurosVlDia, \n"
                    + "       dbo.MCR_Conta.JurosAplic, dbo.MCR_Conta.Total, dbo.MCR_Conta.Anotacoes, dbo.MCR_Conta.DtRecto, CASE WHEN dbo.MCR_Conta.DtRecto IS NULL THEN CASE WHEN CONVERT(INT, GETDATE() \n"
                    + "       - dbo.MCR_Conta.Vencto) < 0 THEN 0 ELSE CONVERT(INT, GETDATE() - dbo.MCR_Conta.Vencto) END WHEN (dbo.MCR_Conta.DtRecto - dbo.MCR_Conta.Vencto) \n"
                    + "       < 0 THEN 0 WHEN (dbo.MCR_Conta.DtRecto - dbo.MCR_Conta.Vencto) > 0 THEN CONVERT(INT, dbo.MCR_Conta.DtRecto - dbo.MCR_Conta.Vencto) ELSE 0 END AS DiasAtraso, dbo.MCR_Conta.Desconto, \n"
                    + "       dbo.MCR_Conta.VlRec, dbo.MCR_Conta.VlDev, dbo.MCR_Conta.RecValor, dbo.MCR_Conta.RecDesconto, dbo.MCR_Conta.RecBaixa, dbo.MCR_Conta.RecDevido, dbo.MCR_Conta.Retorno, dbo.MCR_Conta.Calc01,\n"
                    + "       dbo.MCR_Conta.Calc02, dbo.MCR_Conta.Calc03, dbo.MCR_Conta.Sel, dbo.MCR_Conta.SelAcerto, dbo.MCR_Conta.SelDev, dbo.MCR_Conta.SelME, dbo.MCR_Conta.SelCC, dbo.MCR_Conta.SelImp, \n"
                    + "       dbo.MCR_Conta.SelCob, dbo.MCR_Conta.Conexao, dbo.MCR_Conta.ConexaoAcerto, dbo.MCR_Conta.ConexaoDev, dbo.MCR_Conta.ConexaoME, dbo.MCR_Conta.ConexaoCC, dbo.MCR_Conta.ConexaoImp, \n"
                    + "       dbo.MCR_Conta.ConexaoCob, dbo.MCR_Conta.Contador, dbo.MCR_Conta.Financeira, dbo.MCR_Conta.CobConta, dbo.MCR_Conta.CobEmissao, dbo.MCR_Conta.CobArquivoDig, \n"
                    + "       dbo.MCR_Conta.CobLinhaDigitavel, dbo.MCR_Conta.CobNossoNumero\n"
                    + "  FROM dbo.MCR_Conta LEFT OUTER JOIN\n"
                    + "       dbo.CRH_Humano AS CRH_Repr ON dbo.MCR_Conta.CadRepr = CRH_Repr.Cadastro AND dbo.MCR_Conta.Repr = CRH_Repr.Codigo LEFT OUTER JOIN\n"
                    + "       dbo.CPC_Contas ON dbo.MCR_Conta.PContas = dbo.CPC_Contas.PContas LEFT OUTER JOIN\n"
                    + "       dbo.CRH_Humano AS CRH_Cli ON dbo.MCR_Conta.CadCli = CRH_Cli.Cadastro AND dbo.MCR_Conta.Cliente = CRH_Cli.Codigo\n"
                    + " WHERE dbo.MCR_Conta.Status like '%ANDAMENTO%'\n"
                    + "   AND dbo.MCR_Conta.CadCli = 'cli'"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rst.getString("Conta"));
                    imp.setDataEmissao(rst.getDate("Data"));
                    imp.setDataVencimento(rst.getDate("Vencto"));
                    imp.setNumeroCupom(Utils.formataNumero(rst.getString("DocOrigem")));
                    imp.setValor(rst.getDouble("VlDev"));
                    imp.setIdCliente(Utils.formataNumero(rst.getString("Cliente")));
                    imp.setCnpjCliente(Utils.formataNumero(rst.getString("CliCPFCNPJ")));
                    observacao = (rst.getString("Docto") == null ? "" : "DOCTO - " + rst.getString("Docto") + " PARCELA " + rst.getString("Parcela"));
                    imp.setObservacao(observacao + (rst.getString("Anotacoes") == null ? "" : rst.getString("Anotacoes")));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }
}
