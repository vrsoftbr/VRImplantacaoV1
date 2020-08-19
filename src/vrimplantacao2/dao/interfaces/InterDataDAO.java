package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.TipoPagamento;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Importacao
 */
public class InterDataDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "InterData";
    }
    
    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                new OpcaoProduto[]{
                    OpcaoProduto.FAMILIA,
                    OpcaoProduto.FAMILIA_PRODUTO,
                    OpcaoProduto.MERCADOLOGICO_PRODUTO,
                    OpcaoProduto.MERCADOLOGICO_POR_NIVEL,
                    OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                    OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
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
    
    public List<Estabelecimento> getLojaCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "    f.i_contador id,\n" +
                    "    f.a_fantasia fantasia\n" +
                    "from\n" +
                    "    filial f"
            )) {
                while (rs.next()) {
                    result.add(new Estabelecimento(rs.getString("id"), rs.getString("fantasia")));
                }
            }
        }
        return result;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "    i.i_contador id,\n"
                    + "    i.n_icms descricao\n"
                    + "from\n"
                    + "    ICMS I"
            )) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(rs.getString("id"), rs.getString("descricao")));
                }
            }
        }
        return result;
    }
    
    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "    distinct\n" +
                    "    lo.i_contador merc1,\n" +
                    "    LO.SLOCALIZA descmerc1,\n" +
                    "    L.i_contador merc2,\n" +
                    "    L.A_LINHAPRO descmerc2,\n" +
                    "    s.i_contador merc3,\n" +
                    "    S.A_SUBLINHA descmerc3\n" +
                    "from\n" +
                    "    PRODUTO P\n" +
                    "left join LINHA L on (L.I_CONTADOR = P.I_LINHAPRO)\n" +
                    "left join SUBLINHA S on (S.I_CONTADOR = P.I_SUBLINHA)\n" +
                    "left join LOCALIZA LO on (LO.I_CONTADOR = P.ID_LOCALIZA)\n" +
                    "order by\n" +
                    "    1, 3"
            )) {
                while (rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());                 
                    imp.setMerc1ID(rs.getString("merc1"));
                    imp.setMerc1Descricao(rs.getString("descmerc1"));
                    imp.setMerc2ID(rs.getString("merc2"));
                    imp.setMerc2Descricao(rs.getString("descmerc2"));
                    imp.setMerc3ID(rs.getString("merc3"));
                    imp.setMerc3Descricao(rs.getString("descmerc3"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "    P.I_CONTADOR id,\n" +
                    "    P.d_casdadtr datacadastro,\n" +
                    "    p.a_validade isvalidade,\n" +
                    "    p.a_balanca pesavel,\n" +
                    "    p.i_qtdiasbal validade,\n" +
                    "    (select\n" +
                    "        CODBARRA\n" +
                    "    from\n" +
                    "        SP_GET_MFCODBARRAS(P.A_CODBARRA, P.I_CONTADOR)) ean,\n" +
                    "    p.a_unidadep embalagem,\n" +
                    "    P.A_NPRODUTO descricaocompleta,\n" +
                    "    e.n_quantatu estoque,\n" +
                    "    p.n_maxprodu estoquemax,\n" +
                    "    p.n_minprodu estoquemin,\n" +
                    "    p.n_pesoliq pesoliquido,\n" +
                    "    p.n_pesobru pesobruto,\n" +
                    "    p.n_margemlu margem,\n" +
                    "    p.c_prodcust custocomimposto,\n" +
                    "    p.c_precopro custosemimposto,\n" +
                    "    p.c_precovar precovenda,\n" +
                    "    p.c_precoata,\n" +
                    "    P.A_NCM ncm,\n" +
                    "    p.a_cest cest,\n" +
                    "    P.A_NATREC natureza_receita,\n" +
                    "    P.A_CLASSIPISENTRA PIS_CST_E,\n" +
                    "    P.A_CLASSIPIS PIS_CST_S,\n" +
                    "    P.N_PISENTRADA PIS_ALQ_E,\n" +
                    "    P.N_PIS PIS_ALQ_S,\n" +
                    "    P.A_CLASSICOFINSENTRA COFINS_CST_E,\n" +
                    "    P.A_CLASSICOFINS COFINS_CST_S,\n" +
                    "    P.N_COFINSENTRADA COFINS_ALQ_E,\n" +
                    "    P.N_COFINS COFINS_ALQ_S,\n" +
                    "    lo.i_contador merc1,\n" +
                    "    LO.SLOCALIZA descmerc1,\n" +
                    "    L.i_contador merc2,\n" +
                    "    L.A_LINHAPRO descmerc2,\n" +
                    "    s.i_contador merc3,\n" +
                    "    S.A_SUBLINHA descmerc3,\n" +
                    "    lo.i_contador codsetor,\n" +
                    "    LO.slocaliza setor,\n" +
                    "    case(LO.I_TPSETOR)\n" +
                    "         when 1 then 'Alimentos'\n" +
                    "         when 2 then 'Não Alimentos'\n" +
                    "         when 3 then 'Apropriações'\n" +
                    "    end SUBGRUPO,\n" +
                    "    iif(P.A_DESCONTI = 'SIM', 0, 1) situacao,\n" +
                    "    p.I_CLASIFIS idicms\n" +
                    "from\n" +
                    "    PRODUTO P\n" +
                    "left join estoque e on e.i_idlojaes = " + getLojaOrigem() + " and\n" +
                    "    (p.i_contador = e.i_idprodut)\n" +
                    "left join LINHA L on (L.I_CONTADOR = P.I_LINHAPRO)\n" +
                    "left join SUBLINHA S on (S.I_CONTADOR = P.I_SUBLINHA)\n" +
                    "left join LOCALIZA LO on (LO.I_CONTADOR = P.ID_LOCALIZA)\n" +
                    "left join ICMS I ON (I.I_CONTADOR = P.I_CLASIFIS) \n" +
                    "order by\n" +
                    "    p.i_contador")) {
                while(rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    
                    imp.setImportId(rs.getString("id"));
                    imp.seteBalanca("SIM".equals(rs.getString("pesavel")));
                    imp.setDataCadastro(rs.getDate("datacadastro"));
                    imp.setValidade(rs.getInt("validade"));
                    imp.setEan(rs.getString("ean"));
                    imp.setTipoEmbalagem(rs.getString("embalagem"));
                    imp.setDescricaoCompleta(rs.getString("descricaocompleta"));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    imp.setEstoque(rs.getDouble("estoque"));
                    imp.setEstoqueMaximo(rs.getDouble("estoquemax"));
                    imp.setEstoqueMinimo(rs.getDouble("estoquemin"));
                    imp.setPesoBruto(rs.getDouble("pesobruto"));
                    imp.setPesoLiquido(rs.getDouble("pesoliquido"));
                    imp.setMargem(rs.getDouble("margem"));
                    imp.setCustoComImposto(rs.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(rs.getDouble("custosemimposto"));
                    imp.setPrecovenda(rs.getDouble("precovenda"));
                    imp.setNcm(rs.getString("ncm"));
                    imp.setCest(rs.getString("cest"));
                    imp.setPiscofinsNaturezaReceita(rs.getString("natureza_receita"));
                    imp.setPiscofinsCstCredito(rs.getString("pis_cst_e"));
                    imp.setPiscofinsCstDebito(rs.getString("pis_cst_s"));
                    imp.setCodMercadologico1(rs.getString("merc1"));
                    imp.setCodMercadologico2(rs.getString("merc2"));
                    imp.setCodMercadologico3(rs.getString("merc3"));
                    imp.setSituacaoCadastro(rs.getInt("situacao"));
                    
                    imp.setIcmsDebitoId(rs.getString("idicms"));
                    imp.setIcmsDebitoForaEstadoId(rs.getString("idicms"));
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
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "    c.i_contador id,\n" +
                    "    c.a_nomecli razao,\n" +
                    "    c.a_conjcli fantasia,\n" +
                    "    c.a_cpfcli cnpj,\n" +
                    "    c.a_rgcli rg,\n" +
                    "    c.a_ruacli rua,\n" +
                    "    c.a_numerocli numero,\n" +
                    "    c.a_cepcli cep,\n" +
                    "    c.a_bairrocli bairro,\n" +
                    "    c.a_cidadecli cidade,\n" +
                    "    c.a_estresicli uf,\n" +
                    "    c.a_telecli telefone,\n" +
                    "    c.a_telefaxcli fax,\n" +
                    "    c.a_situacaocli situacao,\n" +
                    "    c.d_datacadcli datacadastro\n" +
                    "from\n" +
                    "    cliente c\n" +
                    "where\n" +
                    "    c.a_tipocliente like '%Fornecedor%'")) {
                while(rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    
                    imp.setImportId(rs.getString("id"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setCnpj_cpf(rs.getString("cnpj"));
                    imp.setIe_rg(rs.getString("rg"));
                    imp.setEndereco(rs.getString("rua"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setCep(rs.getString("cep"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("uf"));
                    imp.setTel_principal(rs.getString("telefone"));
                    imp.setDatacadastro(rs.getDate("datacadastro"));
                    imp.setAtivo("ATIVO".equals(rs.getString("situacao")) ? true : false);
                    
                    result.add(imp);
                }
            }
        }
         return result;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "    pf.id_produto,\n" +
                    "    pf.id_fornecedor,\n" +
                    "    pf.a_codigo codigoexterno,\n" +
                    "    pf.n_qtembalagem qtdembalagem\n" +
                    " from\n" +
                    "    codfornref pf")) {
                while(rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setIdProduto(rs.getString("id_produto"));
                    imp.setIdFornecedor(rs.getString("id_fornecedor"));
                    imp.setCodigoExterno(rs.getString("codigoexterno"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ContaPagarIMP> getContasPagar() throws Exception {
        List<ContaPagarIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "    p.i_contador id,\n" +
                    "    p.i_codiclipag idfornecedor,\n" +
                    "    p.d_emisaocont emissao,\n" +
                    "    p.d_venciconta vencimento,\n" +
                    "    p.c_valorparec valor,\n" +
                    "    p.a_numerodoc documento,\n" +
                    "    p.a_obsconpag obs,\n" +
                    "    p.a_parcelapag parcela\n" +
                    "from\n" +
                    "    pagar p\n" +
                    "where\n" +
                    "    p.a_pagueicont = 'NÃO'\n" +
                    "order by\n" +
                    "    p.d_venciconta")) {
                while(rs.next()) {
                    ContaPagarIMP imp = new ContaPagarIMP();
                    
                    imp.setId(rs.getString("id"));
                    imp.setIdFornecedor(rs.getString("idfornecedor"));
                    imp.setDataEmissao(rs.getDate("emissao"));
                    imp.setNumeroDocumento(rs.getString("documento"));
                    imp.setObservacao(rs.getString("obs"));
                    
                    String parcela = rs.getString("parcela");
                    
                    String parc[] = parcela.split("/");
                    
                    imp.addVencimento(
                            rs.getDate("vencimento"), 
                            rs.getDouble("valor"),
                            TipoPagamento.BOLETO_BANCARIO,
                            Integer.parseInt(parc[0]));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }
}
