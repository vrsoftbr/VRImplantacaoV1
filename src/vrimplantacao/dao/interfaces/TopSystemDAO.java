package vrimplantacao.dao.interfaces;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import vrframework.classe.ProgressBar;
import vrframework.classe.Util;
import vrframework.remote.ItemComboVO;
import vrimplantacao.classe.ConexaoMySQL;
import vrimplantacao.classe.Global;
import vrimplantacao.dao.cadastro.CestDAO;
import vrimplantacao.dao.cadastro.ClientePreferencialDAO;
import vrimplantacao.dao.cadastro.FamiliaProdutoDAO;
import vrimplantacao.dao.cadastro.FornecedorDAO;
import vrimplantacao.dao.cadastro.LojaDAO;
import vrimplantacao.dao.cadastro.MercadologicoDAO;
import vrimplantacao.dao.cadastro.NcmDAO;
import vrimplantacao.dao.cadastro.PlanoDAO;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.dao.cadastro.ProdutoDAO;
import vrimplantacao.dao.cadastro.ProdutoFornecedorDAO;
import vrimplantacao.dao.cadastro.ReceberCreditoRotativoDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao.vo.vrimplantacao.CestVO;
import vrimplantacao.vo.vrimplantacao.ClientePreferencialVO;
import vrimplantacao.vo.vrimplantacao.CodigoAnteriorVO;
import vrimplantacao.vo.vrimplantacao.FamiliaProdutoVO;
import vrimplantacao.vo.vrimplantacao.FornecedorVO;
import vrimplantacao.vo.vrimplantacao.MercadologicoVO;
import vrimplantacao.vo.vrimplantacao.NcmVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAliquotaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAutomacaoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoComplementoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoFornecedorVO;
import vrimplantacao.vo.vrimplantacao.ProdutoVO;
import vrimplantacao.vo.vrimplantacao.ReceberCreditoRotativoVO;

/*






 * Aparentemente as informações principais do sistema ficam localizadas no schema "fiscal".

 * Produtos se localizam na tabela "fiscal.cad_produto"

 * Aparentemente o mercadológico é representado pela tabela "fiscal.cad_produto_estru" porém só há 3 itens nela.
 A tabela que possui o mercadológico dos produtos é a "fiscal.tab_familia_produto"
 Há um campo suspeito chamado grupo no cadastro de produto, verificar da onde vem essa informação. Verificar 
 se o cliente irá querer a estrutura de mercadológico do VR.

 * Código EAN possivelmente é um campo chamado "codigoa" na tabela "fiscal.cad_produto"

 * Pode ser que a definição de pesavel ou pesavel unitario seja o campo "pesavel" na "fiscal.cad_produto"
 S - Produto de kilo.
 U - Unitário pesável.
 N - Unitário.

 * Familia de Produto: "fiscal.cad_familia_produto" na tabela "fiscal.cad_produto.FamiliaProduto"

 * CEST: na tabela "fiscal.cad_produto.CEST"

 * Unificar a tributação com o produto
 select
 p.Codigo_operacaof,
 concat(trib.Codigo_cfop, trib.sequencia) trib,
 trib.Sit_Trib icms_cst,
 trib.Aliquota_icms icms_aliq,
 trib.Pct_Red_Calc_ICMS icms_reducao,
 trib.Pis_Cst piscofins_debito,
 trib.Codigo_ECF,
 trib.*
 from
 fiscal.cad_produto p
 join fiscal.cad_operacaof trib on p.Codigo_operacaof = concat(trib.Codigo_cfop, trib.sequencia)
 limit 1000
 */
public class TopSystemDAO extends AbstractIntefaceDao {

    @Override
    public List<FamiliaProdutoVO> carregarFamiliaProduto() throws SQLException {
        List<FamiliaProdutoVO> result = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select codigo, descricao "
                    + "from cad_familia_produto"
            )) {
                while (rst.next()) {
                    FamiliaProdutoVO oFamiliaProduto = new FamiliaProdutoVO();
                    oFamiliaProduto.setId(rst.getInt("codigo"));
                    oFamiliaProduto.setDescricao(rst.getString("descricao"));
                    oFamiliaProduto.setCodigoant(rst.getInt("codigo"));
                    result.add(oFamiliaProduto);
                }
            }
        }

        return result;
    }

    @Override
    public void importarFamiliaProduto() throws Exception {
        List<FamiliaProdutoVO> vFamiliaProduto;

        ProgressBar.setStatus("Carregando dados...Familia Produto...");
        vFamiliaProduto = carregarFamiliaProduto();
        FamiliaProdutoDAO dao = new FamiliaProdutoDAO();
        dao.salvar(vFamiliaProduto);
    }

    @Override
    public List<MercadologicoVO> carregarMercadologico(int nivel) throws Exception {
        List<MercadologicoVO> result = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select Codigo as id_merc1, 1 as id_merc2, 1 as id_merc3, "
                    + "Descricao "
                    + "from cad_setor "
                    + "order by Codigo"
            )) {
                while (rst.next()) {
                    MercadologicoVO mercadologico = new MercadologicoVO();
                    mercadologico.setMercadologico1(rst.getInt("id_merc1"));
                    mercadologico.setDescricao(rst.getString("Descricao"));
                    if (nivel > 1) {
                        mercadologico.setMercadologico2(rst.getInt("id_merc2"));
                    }
                    if (nivel > 2) {
                        mercadologico.setMercadologico3(rst.getInt("id_merc3"));
                    }
                    mercadologico.setNivel(nivel);

                    result.add(mercadologico);
                }
            }
        }
        return result;
    }

    @Override
    public void importarMercadologico() throws Exception {
        List<MercadologicoVO> vMercadologico;

        ProgressBar.setStatus("Carregando dados...Mercadologico...");
        MercadologicoDAO dao = new MercadologicoDAO();

        vMercadologico = carregarMercadologico(1);
        dao.salvar(vMercadologico, true);

        vMercadologico = carregarMercadologico(2);
        dao.salvar(vMercadologico, false);

        vMercadologico = carregarMercadologico(3);
        dao.salvar(vMercadologico, false);

        dao.salvarMax();
    }

    @Override
    public List<ProdutoVO> carregarListaDeProdutos(int idLojaVR, int idLojaCliente) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select Codigo, Descricao, Descricao_Complementar, Codigo_Interno, Unidade, "
                    + "Peso_Liquido_Embalagem, Peso_Bruto_Embalagem, Grupo, Class_Fiscal_Mercosul, "
                    + "preco_venda1, ST_Ecf, Preco_Custo, Margem_Lucro, Validade, Familia, Inativo, "
                    + "FamiliaProduto, CEST, Estoque_Minimo, TribContrib, ContrMonAliqDif, ContrMonAliqUnd, "
                    + "ContrSubstTrib, ContrAliqZero, pis.CstContrib_Cod "
                    + "from cad_produto "
                    + "inner join ger_tribcontribitem pis on pis.Cod = cad_produto.TribContrib "
            )) {
                //Obtem os produtos de balança
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().carregarProdutosBalanca();
                int contador = 1;
                while (rst.next()) {
                    //Instancia o produto
                    ProdutoVO oProduto = new ProdutoVO();
                    //Prepara as variáveis
                    ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();
                    CodigoAnteriorVO oCodigoAnterior = new CodigoAnteriorVO();
                    ProdutoAliquotaVO oAliquota = new ProdutoAliquotaVO();
                    ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                    //Inclui elas nas listas
                    oProduto.getvAutomacao().add(oAutomacao);
                    oProduto.getvCodigoAnterior().add(oCodigoAnterior);
                    oProduto.getvAliquota().add(oAliquota);
                    oProduto.getvComplemento().add(oComplemento);

                    oProduto.setIdDouble((Double.parseDouble(Utils.formataNumero(rst.getString("Codigo").trim())) > 99999999999999.0
                            ? Double.parseDouble(Utils.formataNumero(rst.getString("Codigo").trim().substring(0, 14)))
                            : Double.parseDouble(Utils.formataNumero(rst.getString("Codigo").trim()))));
                    oProduto.setDescricaoCompleta(rst.getString("Descricao"));
                    oProduto.setDescricaoReduzida(rst.getString("Descricao"));
                    oProduto.setDescricaoGondola(rst.getString("Descricao"));
                    oProduto.setIdSituacaoCadastro(rst.getInt("Inativo"));

                    oProduto.setDataCadastro(Util.formatDataGUI(new Date(new java.util.Date().getTime())));

                    oProduto.setMercadologico1(rst.getInt("Grupo"));
                    oProduto.setMercadologico2(1);
                    oProduto.setMercadologico3(1);
                    oProduto.setMercadologico4(0);
                    oProduto.setMercadologico5(0);

                    if ((rst.getString("Class_Fiscal_Mercosul") != null)
                            && (!rst.getString("Class_Fiscal_Mercosul").isEmpty())
                            && (rst.getString("Class_Fiscal_Mercosul").trim().length() > 5)) {
                        NcmVO oNcm = new NcmDAO().validar(rst.getString("Class_Fiscal_Mercosul").trim());

                        oProduto.setNcm1(oNcm.ncm1);
                        oProduto.setNcm2(oNcm.ncm2);
                        oProduto.setNcm3(oNcm.ncm3);
                    } else {
                        oProduto.setNcm1(402);
                        oProduto.setNcm2(99);
                        oProduto.setNcm3(0);
                    }

                    if ((rst.getString("CEST") != null)
                            && (!rst.getString("CEST").trim().isEmpty())) {
                        CestVO cest = CestDAO.parse(Utils.formataNumero(rst.getString("CEST")));
                        oProduto.setCest1(cest.getCest1());
                        oProduto.setCest2(cest.getCest2());
                        oProduto.setCest3(cest.getCest3());
                    } else {
                        oProduto.setCest1(-1);
                        oProduto.setCest2(-1);
                        oProduto.setCest3(-1);
                    }

                    oProduto.setIdFamiliaProduto(rst.getString("FamiliaProduto") != null ? rst.getInt("FamiliaProduto") : -1);
                    oProduto.setMargem(rst.getDouble("Margem_Lucro"));
                    oProduto.setQtdEmbalagem(1);
                    oProduto.setIdComprador(1);
                    oProduto.setIdFornecedorFabricante(1);

                    long codigoProduto;
                    codigoProduto = (long) oProduto.getIdDouble();

                    /**
                     * Aparentemente o sistema utiliza o próprio id para
                     * produtos de balança.
                     */
                    ProdutoBalancaVO produtoBalanca;
                    if (codigoProduto <= Integer.MAX_VALUE) {
                        produtoBalanca = produtosBalanca.get((int) codigoProduto);
                    } else {
                        produtoBalanca = null;
                    }
                    if (produtoBalanca != null) {
                        oAutomacao.setCodigoBarras((long) oProduto.getIdDouble());
                        oProduto.setValidade(produtoBalanca.getValidade() > 1 ? produtoBalanca.getValidade() : rst.getInt("Validade"));
                        oProduto.eBalanca = true;

                        if ("P".equals(produtoBalanca.getPesavel())) {
                            oAutomacao.setIdTipoEmbalagem(4);
                            oProduto.setPesavel(false);
                        } else {
                            oAutomacao.setIdTipoEmbalagem(0);
                            oProduto.setPesavel(true);
                        }

                        oCodigoAnterior.setCodigobalanca(produtoBalanca.getCodigo());
                        oCodigoAnterior.setE_balanca(true);
                    } else {
                        oProduto.setValidade(rst.getInt("Validade"));
                        oProduto.setPesavel(false);
                        oProduto.eBalanca = false;

                        if (Long.parseLong(Utils.formataNumero(rst.getString("Codigo"))) >= 1000000) {

                            if (Utils.formataNumero(rst.getString("Codigo").trim()).length() > 14) {
                                oAutomacao.setCodigoBarras(Long.parseLong(Utils.formataNumero(rst.getString("Codigo").substring(0, 14))));
                            } else {
                                oAutomacao.setCodigoBarras(Long.parseLong(Utils.formataNumero(rst.getString("Codigo"))));
                            }
                        } else {
                            oAutomacao.setCodigoBarras(-2);
                        }
                        oAutomacao.setIdTipoEmbalagem(Utils.converteTipoEmbalagem(rst.getString("Unidade").trim()));

                        oCodigoAnterior.setCodigobalanca(0);
                        oCodigoAnterior.setE_balanca(false);
                    }
                    oAutomacao.setQtdEmbalagem(1);

                    oProduto.setIdTipoEmbalagem(oAutomacao.getIdTipoEmbalagem());

                    oProduto.setSugestaoPedido(true);
                    oProduto.setAceitaMultiplicacaoPdv(true);
                    oProduto.setSazonal(false);
                    oProduto.setFabricacaoPropria(false);
                    oProduto.setConsignado(false);
                    oProduto.setDdv(0);
                    oProduto.setPermiteTroca(true);
                    oProduto.setVendaControlada(false);
                    oProduto.setVendaPdv(true);
                    oProduto.setConferido(true);
                    oProduto.setPermiteQuebra(true);
                    oProduto.setPesoBruto(rst.getDouble("Peso_Bruto_Embalagem"));
                    oProduto.setPesoLiquido(0);

                    oProduto.setIdTipoPisCofinsDebito(retornarPisCofinsDebito(rst.getInt("CstContrib_Cod")));
                    oProduto.setIdTipoPisCofinsCredito(retornarPisCofinsCredito(rst.getInt("CstContrib_Cod")));

                    int cstpis = rst.getInt("CstContrib_Cod");
                    String natrec = "";

                    if (cstpis == 2 || cstpis == 4) {
                        if (rst.getInt("ContrMonAliqDif") > 0) {
                            natrec = rst.getString("ContrMonAliqDif");
                        } else {
                            natrec = rst.getString("ContrMonAliqUnd");
                        }
                    } else if (cstpis == 3) {
                        natrec = rst.getString("ContrMonAliqUnd");
                    } else if (cstpis == 5) {
                        natrec = rst.getString("ContrSubstTrib");
                    } else if (cstpis == 6) {
                        natrec = rst.getString("ContrAliqZero");
                    }

                    oProduto.setTipoNaturezaReceita(Utils.retornarTipoNaturezaReceita(oProduto.idTipoPisCofinsDebito, natrec.trim()));

                    /*if ((cst == 2) && (rst.getInt("ContrMonAliqDif") > 0)) {
                     oProduto.setTipoNaturezaReceita(Utils.retornarTipoNaturezaReceita(oProduto.idTipoPisCofinsDebito, rst.getString("ContrMonAliqDif").trim()));
                     } else if ((rst.getInt("CstContrib_Cod") == 3) && (rst.getInt("ContrMonAliqUnd") > 0)) {
                     oProduto.setTipoNaturezaReceita(Utils.retornarTipoNaturezaReceita(oProduto.idTipoPisCofinsDebito, rst.getString("ContrMonAliqUnd").trim()));
                     } else if ((rst.getInt("CstContrib_Cod") == 4) && (rst.getInt("ContrMonAliqDif") > 0)) {
                     oProduto.setTipoNaturezaReceita(Utils.retornarTipoNaturezaReceita(oProduto.idTipoPisCofinsDebito, rst.getString("ContrMonAliqUnd").trim()));
                     } else if (rst.getInt("CstContrib_Cod") == 6) {
                     oProduto.setTipoNaturezaReceita(Utils.retornarTipoNaturezaReceita(oProduto.idTipoPisCofinsDebito, rst.getString("ContrAliqZero").trim()));
                     } else if ((rst.getInt("CstContrib_Cod") == 5) && (rst.getInt("ContrSubstTrib") > 0)) {
                     oProduto.setTipoNaturezaReceita(Utils.retornarTipoNaturezaReceita(oProduto.idTipoPisCofinsDebito, rst.getString("ContrSubstTrib").trim()));
                     }*/
                    oComplemento.setPrecoVenda(rst.getDouble("preco_venda1"));
                    oComplemento.setPrecoDiaSeguinte(rst.getDouble("preco_venda1"));
                    oComplemento.setCustoComImposto(rst.getDouble("Preco_Custo"));
                    oComplemento.setCustoSemImposto(rst.getDouble("Preco_Custo"));
                    oComplemento.setIdLoja(idLojaVR);

                    if (rst.getInt("Inativo") == 0) {
                        oComplemento.setIdSituacaoCadastro(1);
                    } else {
                        oComplemento.setIdSituacaoCadastro(0);
                    }

                    oComplemento.setEstoque(0);
                    oComplemento.setEstoqueMinimo(0);
                    oComplemento.setEstoqueMaximo(0);

                    oAliquota.setIdEstado(Utils.getEstadoPelaSigla("MG"));
                    if (oAliquota.getIdEstado() == 0) {
                        oAliquota.setIdEstado(35);
                    }
                    oAliquota.setIdAliquotaDebito(retornarICMS(rst.getString("ST_Ecf").trim()));
                    oAliquota.setIdAliquotaCredito(retornarICMS(rst.getString("ST_Ecf").trim()));
                    oAliquota.setIdAliquotaDebitoForaEstado(retornarICMS(rst.getString("ST_Ecf").trim()));
                    oAliquota.setIdAliquotaCreditoForaEstado(retornarICMS(rst.getString("ST_Ecf").trim()));
                    oAliquota.setIdAliquotaDebitoForaEstadoNF(retornarICMS(rst.getString("ST_Ecf").trim()));

                    oCodigoAnterior.setCodigoanterior(oProduto.getIdDouble());
                    oCodigoAnterior.setMargem(oProduto.getMargem());
                    oCodigoAnterior.setPrecovenda(oComplemento.getPrecoVenda());

                    if (Utils.formataNumero(rst.getString("Codigo").trim()).length() > 14) {
                        oCodigoAnterior.setBarras(Long.parseLong(Utils.formataNumero(rst.getString("Codigo").substring(0, 14))));
                    } else {
                        oCodigoAnterior.setBarras(Long.parseLong(Utils.formataNumero(rst.getString("Codigo"))));
                    }

                    oCodigoAnterior.setReferencia((int) oProduto.getId());
                    oCodigoAnterior.setNcm(rst.getString("Class_Fiscal_Mercosul"));
                    oCodigoAnterior.setId_loja(idLojaVR);
                    oCodigoAnterior.setPiscofinsdebito(rst.getInt("TribContrib"));
                    oCodigoAnterior.setPiscofinscredito(rst.getInt("TribContrib"));
                    oCodigoAnterior.setNaturezareceita(-1);
                    oCodigoAnterior.setRef_icmsdebito(rst.getString("ST_Ecf"));
                    oCodigoAnterior.setCodigoAuxiliar((rst.getString("Codigo") == null ? "" : rst.getString("Codigo").trim()));

                    //Encerramento produto
                    if (oProduto.getMargem() == 0) {
                        oProduto.recalcularMargem();
                    }

                    ProgressBar.setStatus("Carregando dados...Produtos..." + contador + "...");
                    contador++;
                    vProduto.add(oProduto);
                }
            }
        }

        return vProduto;
    }

    @Override
    public void importarProduto(int idLojaVR, int idLojaCliente) throws Exception {
        ProdutoDAO produto = new ProdutoDAO();

        ProgressBar.setStatus("Carregando dados...Produtos.....");
        List<ProdutoVO> vProdutos = carregarListaDeProdutos(idLojaVR, idLojaCliente);

        List<LojaVO> vLoja = new LojaDAO().carregar();

        ProgressBar.setMaximum(vProdutos.size());

        produto.implantacaoExterna = true;
        produto.salvar(vProdutos, idLojaVR, vLoja);
    }

    @Override
    public List<ItemComboVO> carregarLojasCliente() throws Exception {
        List<ItemComboVO> itens = new ArrayList<>();
        itens.add(new ItemComboVO(1, "LOJA 01"));
        return itens;
    }

    public List<ProdutoVO> carregarListaDePrecoProduto(int idLojaVR, int idLojaCliente) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select Codigo, Descricao, preco_venda1 "
                    + "from cad_produto"
            )) {

                while (rst.next()) {
                    ProdutoVO oProduto = new ProdutoVO();
                    ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                    oProduto.setIdDouble((Double.parseDouble(Utils.formataNumero(rst.getString("Codigo").trim())) > 99999999999999.0
                            ? Double.parseDouble(Utils.formataNumero(rst.getString("Codigo").trim().substring(0, 14)))
                            : Double.parseDouble(Utils.formataNumero(rst.getString("Codigo").trim()))));
                    oProduto.setDescricaoCompleta((rst.getString("Descricao") == null ? "" : rst.getString("Descricao").trim()));
                    oComplemento.setPrecoVenda(rst.getDouble("preco_venda1"));
                    oComplemento.setPrecoDiaSeguinte(rst.getDouble("preco_venda1"));
                    oComplemento.setIdLoja(idLojaVR);
                    oProduto.vComplemento.add(oComplemento);
                    vProduto.add(oProduto);
                }
            }
        }

        return vProduto;
    }

    public void importarListaPrecoProduto(int idLojaVR, int idLojaCliente) throws Exception {
        ProdutoDAO produto = new ProdutoDAO();
        List<ProdutoVO> vProduto = new ArrayList<>();
        ProgressBar.setStatus("Carregando dados...Preço Produto...Loja " + idLojaVR + "...");
        vProduto = carregarListaDePrecoProduto(idLojaVR, idLojaCliente);
        if (!vProduto.isEmpty()) {
            produto.alterarPrecoProduto(vProduto, idLojaVR);
        }
    }

    public List<ProdutoVO> carregarListaDeCustoProduto(int idLojaVR, int idLojaCliente) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select Codigo, Descricao, Preco_Custo "
                    + "from cad_produto"
            )) {

                while (rst.next()) {
                    ProdutoVO oProduto = new ProdutoVO();
                    ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                    oProduto.setIdDouble((Double.parseDouble(Utils.formataNumero(rst.getString("Codigo").trim())) > 99999999999999.0
                            ? Double.parseDouble(Utils.formataNumero(rst.getString("Codigo").trim().substring(0, 14)))
                            : Double.parseDouble(Utils.formataNumero(rst.getString("Codigo").trim()))));
                    oProduto.setDescricaoCompleta((rst.getString("Descricao") == null ? "" : rst.getString("Descricao").trim()));
                    oComplemento.setCustoComImposto(rst.getDouble("Preco_Custo"));
                    oComplemento.setCustoSemImposto(rst.getDouble("Preco_Custo"));
                    oComplemento.setIdLoja(idLojaVR);
                    oProduto.vComplemento.add(oComplemento);
                    vProduto.add(oProduto);
                }
            }
        }

        return vProduto;
    }

    public void importarListaCustoProduto(int idLojaVR, int idLojaCliente) throws Exception {
        ProdutoDAO produto = new ProdutoDAO();

        ProgressBar.setStatus("Carregando dados...Custo Produto...Loja " + idLojaVR + "...");
        List<ProdutoVO> vProduto = carregarListaDeCustoProduto(idLojaVR, idLojaCliente);
        if (!vProduto.isEmpty()) {
            produto.alterarCustoProduto(vProduto, idLojaVR);
        }
    }

    public List<ProdutoVO> carregarListaDeMargemProduto() throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select Codigo, Descricao, Margem_Lucro "
                    + "from cad_produto"
            )) {
                while (rst.next()) {
                    ProdutoVO oProduto = new ProdutoVO();
                    oProduto.setIdDouble((Double.parseDouble(Utils.formataNumero(rst.getString("Codigo").trim())) > 99999999999999.0
                            ? Double.parseDouble(Utils.formataNumero(rst.getString("Codigo").trim().substring(0, 14)))
                            : Double.parseDouble(Utils.formataNumero(rst.getString("Codigo").trim()))));
                    oProduto.setDescricaoCompleta((rst.getString("Descricao") == null ? "" : rst.getString("Descricao").trim()));
                    oProduto.setMargem(rst.getDouble("Margem_Lucro"));
                    vProduto.add(oProduto);
                }
            }
        }

        return vProduto;
    }
    
    public void importarListaDeMargemProduto(int idLojaVR) throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        ProgressBar.setStatus("Carregandado dados...Margem Produto...");
        try {
            vResult = carregarListaDeMargemProduto();
            if (!vResult.isEmpty()) {
                new ProdutoDAO().alterarMargemProduto(vResult, idLojaVR);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    public List<ProdutoVO> carregarListaDeSituacaoCadastroProduto(int idLojaVR, int idLojaCliente) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select Codigo, Descricao, Inativo "
                    + "from cad_produto"
            )) {

                while (rst.next()) {
                    ProdutoVO oProduto = new ProdutoVO();
                    ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                    oProduto.setIdDouble((Double.parseDouble(Utils.formataNumero(rst.getString("Codigo").trim())) > 99999999999999.0
                            ? Double.parseDouble(Utils.formataNumero(rst.getString("Codigo").trim().substring(0, 14)))
                            : Double.parseDouble(Utils.formataNumero(rst.getString("Codigo").trim()))));
                    oProduto.setDescricaoCompleta((rst.getString("Descricao") == null ? "" : rst.getString("Descricao").trim()));

                    if (rst.getInt("Inativo") == 0) {
                        oComplemento.setIdSituacaoCadastro(1);
                    } else {
                        oComplemento.setIdSituacaoCadastro(0);
                    }

                    oComplemento.setIdLoja(idLojaVR);
                    oProduto.vComplemento.add(oComplemento);
                    vProduto.add(oProduto);
                }
            }
        }

        return vProduto;
    }

    public void importarListaSituacaoCadastroProduto(int idLojaVR, int idLojaCliente) throws Exception {
        ProdutoDAO produto = new ProdutoDAO();

        ProgressBar.setStatus("Carregando dados...Situação Cadastro Produto...Loja " + idLojaVR + "...");
        List<ProdutoVO> vProduto = carregarListaDeSituacaoCadastroProduto(idLojaVR, idLojaCliente);
        if (!vProduto.isEmpty()) {
            produto.alterarSituacaoCadastroProduto(vProduto, idLojaVR);
        }
    }

    public List<ProdutoVO> carregarListaDeEstoqueProduto(int idLojaVR, int idLojaCliente) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select b.Codigo, b.Qtde_Atual, a.Descricao "
                    + "from cad_estoque b "
                    + "inner join cad_produto a on a.Codigo = b.Codigo "
                    //+ "where b.Qtde_Atual <> 0 "
                    + "order by b.Codigo, b.Qtde_Atual"
            )) {

                while (rst.next()) {
                    ProdutoVO oProduto = new ProdutoVO();
                    ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();

                    oProduto.setIdDouble(Double.parseDouble(Utils.formataNumero(rst.getString("Codigo"))));
                    oProduto.setDescricaoCompleta((rst.getString("Descricao") == null ? "" : rst.getString("Descricao").trim()));
                    oProduto.setCodigoBarras((long) oProduto.getIdDouble());
                    oComplemento.setEstoque(rst.getDouble("Qtde_Atual"));
                    oComplemento.setIdLoja(idLojaVR);
                    oProduto.vComplemento.add(oComplemento);
                    vProduto.add(oProduto);
                }
            }
        }

        return vProduto;
    }

    public void importarListaDeEstoqueProduto(int idLojaVR, int idLojaCliente) throws Exception {
        ProdutoDAO produto = new ProdutoDAO();

        ProgressBar.setStatus("Carregando dados...Estoque Produto...Loja " + idLojaVR + "...");
        List<ProdutoVO> vProduto = carregarListaDeEstoqueProduto(idLojaVR, idLojaCliente);
        if (!vProduto.isEmpty()) {
            produto.alterarEstoqueProduto(vProduto, idLojaVR);
        }
    }
    
    public void importarListaProdutoEstoqueIntegracao(int idLojaVR, int idLojaCliente) throws Exception {
        ProgressBar.setStatus("Carregando dados...Estoque Integração Loja..." + idLojaVR + "...");
        List<ProdutoVO> vProduto = carregarListaDeEstoqueProduto(idLojaVR, idLojaCliente);
        if (!vProduto.isEmpty()) {
            new ProdutoDAO().alterarEstoqueProdutoIntegracao(vProduto, idLojaVR);
        }
    }

    @Override
    public List<FornecedorVO> carregarFornecedor() throws Exception {
        List<FornecedorVO> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select Codigo, Nome, Razao_Social, Endereco, Numero, "
                    + "Complemento, Bairro,\n"
                    + "Cep, CPF_CNPJ, Insc_Estadual, DDD, Prefixo, "
                    + "Telefone, Fax, E_Mail, Site,\n"
                    + "Contato, municipio, uf, cod_oficial_mun, Celular \n"
                    + "from cad_fornecedor;"
            )) {
                while (rst.next()) {
                    FornecedorVO oFornecedor = new FornecedorVO();

                    Date datacadastro;
                    datacadastro = new Date(new java.util.Date().getTime());

                    oFornecedor.setId(rst.getInt("Codigo"));
                    oFornecedor.setCodigoanterior(rst.getInt("Codigo"));
                    oFornecedor.setDatacadastro(datacadastro);
                    oFornecedor.setRazaosocial(rst.getString("Razao_Social").trim());
                    oFornecedor.setNomefantasia(rst.getString("Nome").trim());

                    oFornecedor.setEndereco(rst.getString("Endereco").trim());
                    oFornecedor.setNumero(rst.getString("Numero").trim());
                    oFornecedor.setComplemento(rst.getString("Complemento").trim());
                    oFornecedor.setBairro(rst.getString("Bairro").trim());

                    if (rst.getInt("cod_oficial_mun") != 0) {
                        oFornecedor.setId_municipio(Utils.stringToInt(rst.getString("cod_oficial_mun")));
                    } else {
                        oFornecedor.setId_municipio(Utils.retornarMunicipioIBGEDescricao(
                                Utils.acertarTexto(rst.getString("municipio")), rst.getString("uf").toUpperCase()));
                    }

                    oFornecedor.setCep(Utils.stringToLong(rst.getString("Cep"), 0));
                    oFornecedor.setId_estado(Utils.getEstadoPelaSigla(rst.getString("uf").trim()));

                    oFornecedor.setEnderecocobranca("");
                    oFornecedor.setNumerocobranca("");
                    oFornecedor.setBairrocobranca("");
                    oFornecedor.setId_municipiocobranca(Utils.stringToInt("-1"));
                    oFornecedor.setCepcobranca(0);
                    oFornecedor.setId_estadocobranca(0);

                    oFornecedor.setTelefone(("0".equals(rst.getString("DDD").trim()) ? "" : rst.getString("DDD").trim())
                            + rst.getString("Telefone").trim());
                    oFornecedor.setInscricaoestadual(rst.getString("Insc_Estadual").trim());
                    oFornecedor.setCnpj(Utils.stringToLong(rst.getString("CPF_CNPJ"), 0));
                    oFornecedor.setId_tipoinscricao(String.valueOf(oFornecedor.getCnpj()).length() > 11 ? 0 : 1);

                    oFornecedor.setObservacao("");
                    oFornecedor.setTelefone2("");
                    oFornecedor.setCelular(rst.getString("Celular").trim());
                    oFornecedor.setFax(rst.getString("Fax"));
                    oFornecedor.setEmail(rst.getString("E_Mail"));
                    oFornecedor.setId_situacaocadastro(1);

                    result.add(oFornecedor);
                }
            }
        }

        return result;
    }

    @Override
    public List<ProdutoFornecedorVO> carregarProdutoFornecedor() throws Exception {
        List<ProdutoFornecedorVO> vResult = new ArrayList<>();
        java.sql.Date dataAlteracao = new Date(new java.util.Date().getTime());
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select CodForn, ProdForn, Produto "
                    + "from fiscal.cad_forn_prod;"
            )) {
                while (rst.next()) {
                    if ((rst.getString("ProdForn") != null)
                            && (!rst.getString("ProdForn").trim().isEmpty())) {
                        ProdutoFornecedorVO vo = new ProdutoFornecedorVO();
                        vo.setId_fornecedor(Integer.parseInt(Utils.formataNumero(rst.getString("CodForn"))));
                        vo.setCodigoexterno(rst.getString("ProdForn"));
                        vo.setId_produtoDouble((Double.parseDouble(Utils.formataNumero(rst.getString("Produto").trim())) > 99999999999999.0
                            ? Double.parseDouble(Utils.formataNumero(rst.getString("Produto").trim().substring(0, 14)))
                            : Double.parseDouble(Utils.formataNumero(rst.getString("Produto").trim()))));
                        vo.setDataalteracao(dataAlteracao);
                        vResult.add(vo);
                    }
                }
            }
        }
        return vResult;
    }
    
    @Override
    public void importarProdutoFornecedor() throws Exception {
        List<ProdutoFornecedorVO> vResult = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Produto Fornecedor...");
            vResult = carregarProdutoFornecedor();
            if (!vResult.isEmpty()) {
                new ProdutoFornecedorDAO().salvar2(vResult);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    @Override
    public void importarFornecedor() throws Exception {
        ProgressBar.setStatus("Carregando dados...Fornecedor...");
        List<FornecedorVO> vFornecedor = carregarFornecedor();

        if (Global.compararCnpj) {
            new FornecedorDAO().salvarCnpj(vFornecedor);
        } else {
            new FornecedorDAO().salvar(vFornecedor);
        }
    }

    public void importarAcertarEnderecoFornecedor() throws Exception {
        ProgressBar.setStatus("Carregando dados...Endereço Fornecedor...");
        List<FornecedorVO> vFornecedor = carregarFornecedor();

        if (Global.compararCnpj) {
            new FornecedorDAO().salvarCnpj(vFornecedor);
        } else {
            new FornecedorDAO().acertarEndereco(vFornecedor);
        }
    }

    public void importarAcertarTelefoneFornecedor() throws Exception {
        ProgressBar.setStatus("Carregando dados...Telefone Fornecedor...");
        List<FornecedorVO> vFornecedor = carregarFornecedor();

        if (Global.compararCnpj) {
            new FornecedorDAO().salvarCnpj(vFornecedor);
        } else {
            new FornecedorDAO().alterarTelefone(vFornecedor);
        }
    }

    @Override
    public List<ClientePreferencialVO> carregarCliente(int idLojaCliente) throws Exception {
        List<ClientePreferencialVO> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "Codigo, Nome_Reduzido, Razao_Social, Endereco, Numero, Complemento, "
                    + "Bairro, CEP, CPF_CNPJ, Data_Nascimento, Nome_Pai, Nome_Mae, Insc_Estadual_Rg, "
                    + "Insc_Municipal, DDD, Telefone, Ramal, Fax, E_Mail, Limite_Credito, Contato, "
                    + "Bloqueado, Celular, municipio, uf, cod_oficial_mun, Ativo, DDD_Celular "
                    + "from cad_cliente "
            )) {
                int contador = 1;
                while (rst.next()) {
                    ClientePreferencialVO oClientePreferencial = new ClientePreferencialVO();

                    oClientePreferencial.setId(rst.getInt("Codigo"));
                    oClientePreferencial.setCodigoanterior(rst.getInt("Codigo"));
                    oClientePreferencial.setNome(rst.getString("Razao_Social"));
                    oClientePreferencial.setEndereco(rst.getString("Endereco"));
                    oClientePreferencial.setNumero(rst.getString("Numero"));
                    oClientePreferencial.setComplemento(rst.getString("Complemento"));
                    oClientePreferencial.setBairro(rst.getString("Bairro"));
                    oClientePreferencial.setId_estado(Utils.acertarTexto(rst.getString("uf")).equals("") ? Global.idEstado : Utils.retornarEstadoDescricao(rst.getString("uf")));

                    if (rst.getInt("cod_oficial_mun") != 0) {
                        oClientePreferencial.setId_municipio(rst.getInt("cod_oficial_mun") == 0 ? Global.idMunicipio : rst.getInt("cod_oficial_mun"));
                    } else {
                        oClientePreferencial.setId_municipio((Utils.retornarMunicipioIBGEDescricao(
                                Utils.acertarTexto(rst.getString("municipio").trim()), rst.getString("uf").toUpperCase()) == 0 ? Global.idMunicipio
                                        : Utils.retornarMunicipioIBGEDescricao(
                                                Utils.acertarTexto(rst.getString("municipio").trim()), rst.getString("uf").toUpperCase())));
                    }

                    oClientePreferencial.setCep(Utils.stringToInt(rst.getString("CEP")));
                    oClientePreferencial.setTelefone(("0".equals(rst.getString("DDD").trim()) ? "" : rst.getString("DDD").trim()) + rst.getString("Telefone"));
                    oClientePreferencial.setCelular(("0".equals(rst.getString("DDD_Celular").trim()) ? "" : rst.getString("DDD_Celular").trim()) + rst.getString("Celular"));
                    oClientePreferencial.setInscricaoestadual(rst.getString("Insc_Municipal"));
                    
                    if ((rst.getString("CPF_CNPJ") != null) &&
                            (!rst.getString("CPF_CNPJ").trim().isEmpty())) {
                        if (Long.parseLong(Utils.formataNumero(rst.getString("CPF_CNPJ").trim())) >= 9) {
                            oClientePreferencial.setCnpj(Long.parseLong(Utils.formataNumero(rst.getString("CPF_CNPJ").trim())));
                        } else {
                            oClientePreferencial.setCnpj(-1);
                        }
                    } else {
                        oClientePreferencial.setCnpj(-1);
                    }
                    
                    oClientePreferencial.setId_tipoinscricao(String.valueOf(oClientePreferencial.getCnpj()).length() > 11 ? 0 : 1);
                    oClientePreferencial.setSexo(1);
                    oClientePreferencial.setDataresidencia("1990/01/01");
                    oClientePreferencial.setDatacadastro(Utils.formatDate(new java.util.Date()));
                    oClientePreferencial.setEmail(rst.getString("E_Mail"));
                    oClientePreferencial.setValorlimite(rst.getDouble("Limite_Credito"));
                    oClientePreferencial.setFax(rst.getString("Fax"));

                    if ("N".equals(rst.getString("Bloqueado").trim())) {
                        oClientePreferencial.setBloqueado(false);
                    } else {
                        oClientePreferencial.setBloqueado(true);
                    }

                    if ("S".equals(rst.getString("Ativo").trim())) {
                        oClientePreferencial.setId_situacaocadastro(1);
                    } else {
                        oClientePreferencial.setId_situacaocadastro(0);
                    }

                    oClientePreferencial.setTelefone2("");
                    oClientePreferencial.setObservacao("");
                    oClientePreferencial.setDatanascimento(rst.getString("Data_Nascimento"));
                    oClientePreferencial.setNomepai(rst.getString("Nome_Pai"));
                    oClientePreferencial.setNomemae(rst.getString("Nome_Mae"));
                    oClientePreferencial.setOrgaoemissor(null);

                    result.add(oClientePreferencial);

                    ProgressBar.setStatus("Carregando dados...Cliente Preferencial..." + contador);
                    contador++;
                }
            }
        }
        return result;
    }

    @Override
    public void importarClientePreferencial(int idLojaVR, int idLojaCliente) throws Exception {
        ProgressBar.setStatus("Carregando dados...Cliente Preferencial...");
        List<ClientePreferencialVO> vClientePreferencial = carregarCliente(idLojaCliente);
        new PlanoDAO().salvar(idLojaVR);

        new ClientePreferencialDAO().salvar(vClientePreferencial, idLojaVR, idLojaCliente);
    }

    public void importarClientePreferencialIntegracao(int idLojaVR, int idLojaCliente) throws Exception {
        ProgressBar.setStatus("Carregando dados...Cliente Preferencial Integracao...");
        List<ClientePreferencialVO> vClientePreferencial = carregarCliente(idLojaCliente);
        ClientePreferencialDAO dao = new ClientePreferencialDAO();
        new PlanoDAO().salvar(idLojaVR);
        dao.naoGravarCpfCnpjRepetidos = true;
        dao.salvar(vClientePreferencial, idLojaVR, idLojaCliente);
    }
    
    @Override
    public List<ReceberCreditoRotativoVO> carregarReceberCreditoRotativo(int idLojaCliente, int idLojaVR) throws Exception {
        List<ReceberCreditoRotativoVO> vResult = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select empresa, numero_doc, cliente, data_emissao,\n"
                    + "       data_vencimento, saldo, observacao\n"
                    + "  from fiscal.mov_car\n"
                    + " where empresa = " + idLojaCliente + " "
                    + " and data_liquidacao is null;"
            )) {
                while (rst.next()) {
                    ReceberCreditoRotativoVO vo = new ReceberCreditoRotativoVO();
                    vo.setDataemissao(rst.getString("data_emissao"));
                    vo.setDatavencimento(rst.getString("data_vencimento"));
                    vo.setNumerocupom(rst.getInt("numero_doc"));
                    vo.setId_clientepreferencial(rst.getInt("cliente"));
                    vo.setValor(rst.getDouble("saldo"));
                    
                    if ((rst.getString("observacao") != null) &&
                            (!rst.getString("observacao").trim().isEmpty())) {
                        vo.setObservacao(rst.getString("observacao").trim());
                    } else {
                        vo.setObservacao("IMPORTADO VR");
                    }
                    vResult.add(vo);
                }
            }
        }
        return vResult;
    }

    public List<ReceberCreditoRotativoVO> carregarReceberCreditoRotativoComCpf(int idLojaCliente, int idLojaVR) throws Exception {
        List<ReceberCreditoRotativoVO> vResult = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select a.empresa, a.numero_doc, a.cliente, a.data_emissao, \n"
                    + "     a.data_vencimento, a.saldo, a.observacao, b.cpf_cnpj \n"
                    + "  from fiscal.mov_car a \n"
                    + "  inner join cad_cliente b on b.Codigo = a.Cliente "
                    + " where a.empresa = " + idLojaCliente + " "
                    + " and a.data_liquidacao is null;"
            )) {
                while (rst.next()) {
                    if ((rst.getString("cpf_cnpj") != null)
                            && (!rst.getString("cpf_cnpj").trim().isEmpty())) {

                        if (Long.parseLong(Utils.formataNumero(rst.getString("cpf_cnpj").trim())) > 99999999) {

                            ReceberCreditoRotativoVO vo = new ReceberCreditoRotativoVO();
                            vo.setDataemissao(rst.getString("data_emissao"));
                            vo.setDatavencimento(rst.getString("data_vencimento"));
                            vo.setNumerocupom(rst.getInt("numero_doc"));
                            vo.setId_clientepreferencial(rst.getInt("cliente"));
                            vo.setValor(rst.getDouble("saldo"));
                            vo.setCnpjCliente(Long.parseLong(Utils.formataNumero(rst.getString("cpf_cnpj").trim())));

                            if ((rst.getString("observacao") != null)
                                    && (!rst.getString("observacao").trim().isEmpty())) {
                                vo.setObservacao(rst.getString("observacao").trim());
                            } else {
                                vo.setObservacao("IMPORTADO VR");
                            }
                            vResult.add(vo);
                        }
                    }
                }
            }
        }
        return vResult;
    }
    
    @Override
    public void importarReceberCreditoRotativo(int idLojaCliente, int idLojaVR) throws Exception {
        List<ReceberCreditoRotativoVO> vResult = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Receber Credito Rotativo Loja "+idLojaVR);
            vResult = carregarReceberCreditoRotativo(idLojaCliente, idLojaVR);
            if (!vResult.isEmpty()) {
                new  ReceberCreditoRotativoDAO().salvarComCodicao(vResult, idLojaVR);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    public void importarReceberCreditoRotativoComCpf(int idLojaCliente, int idLojaVR) throws Exception {
        List<ReceberCreditoRotativoVO> vResult = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Receber Credito Rotativo Cpf Loja "+idLojaVR);
            vResult = carregarReceberCreditoRotativoComCpf(idLojaCliente, idLojaVR);
            if (!vResult.isEmpty()) {
                new  ReceberCreditoRotativoDAO().salvarComCnpj(vResult, idLojaVR);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    public void importarAcertarSituacaoCadastroCliente(int idLojaVR) throws Exception {
        List<ClientePreferencialVO> vResult = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Acertar Situacao Cadastro Cliente...");
            vResult = carregarCliente(idLojaVR);
            if (!vResult.isEmpty()) {
                //new ClientePreferencialDAO().alterarSituacaoCliente(vResult, idLojaVR);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    /**
     * *****************************************
     */
    private int retornarPisCofinsDebito(int cst) {
        int retorno = 1;

        if ((cst == 1) || (cst == 50)) {
            retorno = 0;
        } else if ((cst == 2) || (cst == 60)) {
            retorno = 5;
        } else if ((cst == 3) || (cst == 51)) {
            retorno = 6;
        } else if ((cst == 4) || (cst == 70)) {
            retorno = 3;
        } else if ((cst == 5) || (cst == 75)) {
            retorno = 2;
        } else if ((cst == 6) || (cst == 73)) {
            retorno = 7;
        } else if ((cst == 7) || (cst == 71)) {
            retorno = 1;
        } else if ((cst == 8) || (cst == 74)) {
            retorno = 8;
        } else if ((cst == 49) || (cst == 98) || (cst == 99)) {
            retorno = 9;
        }

        return retorno;
    }

    private int retornarPisCofinsCredito(int cst) {
        int retorno = 13;

        if ((cst == 1) || (cst == 50)) {
            retorno = 12;
        } else if ((cst == 2) || (cst == 60)) {
            retorno = 17;
        } else if ((cst == 3) || (cst == 51)) {
            retorno = 18;
        } else if ((cst == 4) || (cst == 70)) {
            retorno = 15;
        } else if ((cst == 5) || (cst == 75)) {
            retorno = 14;
        } else if ((cst == 6) || (cst == 73)) {
            retorno = 19;
        } else if ((cst == 7) || (cst == 71)) {
            retorno = 13;
        } else if ((cst == 8) || (cst == 74)) {
            retorno = 20;
        } else if ((cst == 49) || (cst == 98) || (cst == 99)) {
            retorno = 21;
        }

        return retorno;
    }

    private int retornarICMS(String valorAliquota) {
        int retorno = 8;

        if (null != valorAliquota) {
            switch (valorAliquota) {
                case "FF":
                    retorno = 7;
                    break;
                case "II":
                    retorno = 6;
                    break;
                case "NN":
                    retorno = 17;
                    break;
                default:
                    if (Utils.truncar2(Double.parseDouble(valorAliquota.replace(",", ".")), 1) == 0.0) {
                        retorno = 6;
                    } else if (Utils.truncar2(Double.parseDouble(valorAliquota.replace(",", ".")), 1) == 7.0) {
                        retorno = 0;
                    } else if (Utils.truncar2(Double.parseDouble(valorAliquota.replace(",", ".")), 1) == 12.0) {
                        retorno = 1;
                    } else if (Utils.truncar2(Double.parseDouble(valorAliquota.replace(",", ".")), 1) == 18.0) {
                        retorno = 2;
                    } else if (Utils.truncar2(Double.parseDouble(valorAliquota.replace(",", ".")), 1) == 25.0) {
                        retorno = 3;
                    }
                    break;
            }
        }

        return retorno;
    }

    public void corrigirNaturezaReceita(int idLojaVR, int idLojaCliente) throws Exception {
        ProgressBar.setStatus("Importando os dados do tributo....Natureza da Receita");
        new ProdutoDAO().alterarNaturezaReceita(carregarListaDeProdutos(idLojaVR, idLojaCliente));
    }

}
