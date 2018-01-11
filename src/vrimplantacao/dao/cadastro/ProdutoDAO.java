package vrimplantacao.dao.cadastro;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import javax.swing.JOptionPane;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrframework.classe.Util;
import vrframework.classe.VRException;
import vrimplantacao.dao.CodigoInternoDAO;
import vrimplantacao.dao.DataProcessamentoDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao.vo.vrimplantacao.CestVO;
import vrimplantacao.vo.vrimplantacao.CodigoAnteriorVO;
import vrimplantacao.vo.vrimplantacao.EstoqueTerceiroVO;
import vrimplantacao.vo.vrimplantacao.FamiliaProdutoVO;
import vrimplantacao.vo.vrimplantacao.MercadologicoVO;
import vrimplantacao.vo.vrimplantacao.ProdutosUnificacaoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAliquotaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAutomacaoLojaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAutomacaoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoComplementoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoVO;
import vrimplantacao2.utils.MathUtils;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.utils.sql.SQLBuilder;

public class ProdutoDAO {

    /**
     * Esta constante define a quantidade de comandos sql que serão acumulados
     * antes de executar em um Statement.
     */
    private static final int QTD_PARA_EXECUTAR_SQL = 1000;

    public boolean implantacaoExterna = false;
    public boolean gerarCodigoBarrasSeRepetido = false;
    public double IdProduto = 0;
    public double codigoAnterior = 0;
    public boolean Inserir = false;
    public boolean verificarLoja = false;
    public int p_idLoja = 0;
    public boolean alterarQtdEmbalagem = false;
    public boolean eBarras = false;
    public boolean usarMercadoligicoProduto = false;
    public boolean gerarCodigo = false;
    public boolean usarCodigoAnterior = false;
    public boolean vrTovr = false;
    public int id_loja = 0;
    public int qtdeCodigoInterno = 150000;
    public boolean automacaoLoja = false;
    public boolean usarCodigoBalancaComoID = false;
    public boolean usarMercadologicoAcertar = false;
    public boolean alterarBarraAnterio = true;
    public boolean usarCodAnteriorIntegracao = false;
    public boolean implantacaoCodAntProduto = false;
    /**
     * Se estiver marcado como true, ao executar o método salvar, não é
     * verificado se o código interno já existe
     */
    public boolean naoVerificarCodigoAnterior = false;

    //<editor-fold defaultstate="collapsed" desc="IMPORTACAO V2">
    private String importSistema;
    private String importLoja;

    public String getImportSistema() {
        return importSistema;
    }

    public void setImportSistema(String importSistema) {
        this.importSistema = importSistema;
    }

    public String getImportLoja() {
        return importLoja;
    }

    public void setImportLoja(String importLoja) {
        this.importLoja = importLoja;
    }
    //</editor-fold>

    public void corrigirBalanca(List<ProdutoVO> carregarProdutos) throws Exception {
        Map<Double, ProdutoVO> auxiliar = new LinkedHashMap<>();
        for (ProdutoVO produto : carregarProdutos) {
            if (produto.getvCodigoAnterior().get(0).isE_balanca()) {
                auxiliar.put(produto.idDouble, produto);
            }
        }

        Map<Double, CodigoAnteriorVO> codigoAnterior = new CodigoAnteriorDAO().carregarCodigoAnterior();
        Conexao.begin();
        try {
            Statement stm = Conexao.createStatement();
            stm.execute("create table if not exists implantacao.correcaobalanca(comando text);");
            stm.execute("delete from implantacao.correcaobalanca;");

            ProgressBar.setStatus("Importando dados...Corrigindo produtos de balança...");
            ProgressBar.setMaximum(auxiliar.size());

            for (ProdutoVO produto : auxiliar.values()) {
                double idProduto;
                if (produto.idDouble > 0) {
                    idProduto = produto.idDouble;
                } else {
                    idProduto = produto.id;
                }
                CodigoAnteriorVO anterior = codigoAnterior.get(idProduto);

                if (anterior != null) {

                    CodigoAnteriorVO oCodigoAnterior = produto.getvCodigoAnterior().get(0);

                    StringBuilder sql = new StringBuilder();

                    sql.append("delete from produtoautomacao where id_produto = ")
                            .append((long) anterior.getCodigoatual())
                            .append(";");

                    sql.append("update produto set ")
                            .append("id_tipoembalagem = ").append(produto.getIdTipoEmbalagem()).append(", ")
                            .append("pesavel = ").append(produto.isPesavel()).append(", ")
                            .append("validade = ").append(produto.getValidade()).append(" ")
                            .append("where id = ").append((long) anterior.getCodigoatual())
                            .append(";");

                    sql.append("insert into produtoautomacao (id_produto, ")
                            .append("codigobarras, qtdembalagem, id_tipoembalagem ")
                            .append(") values (")
                            .append((long) anterior.getCodigoatual()).append(", ")
                            .append((long) anterior.getCodigoatual()).append(", ")
                            .append("1, ")
                            .append(produto.getIdTipoEmbalagem())
                            .append(");");

                    sql.append(
                            "update implantacao.codigoanterior set "
                            + "codigobalanca = " + (long) oCodigoAnterior.getCodigobalanca() + ", "
                            + "e_balanca = " + oCodigoAnterior.isE_balanca() + ", "
                            + "barras = " + (long) oCodigoAnterior.getBarras() + " "
                            + "where codigoatual = " + (long) anterior.getCodigoatual() + ";"
                    );

                    sql.append("insert into implantacao.correcaobalanca(comando) values (")
                            .append(Utils.quoteSQL(sql.toString()))
                            .append(");");

                    stm.execute(sql.toString());

                    ProgressBar.next();

                }
            }
            Conexao.commit();
        } catch (Exception e) {
            Conexao.rollback();
            throw e;
        }
    }

    public void alterarPrecoPorEAN(List<ProdutoVO> produtos, int idLojaVR) throws Exception {
        int x = 0, cont = 0;
        try {

            Conexao.begin();

            Map<Long, ProdutoVO> v_produto = new LinkedHashMap<>();
            for (ProdutoVO produto : produtos) {
                long codigobarras;

                if (produto.getvAutomacao() != null && produto.getvAutomacao().size() == 1) {
                    codigobarras = produto.getvAutomacao().get(0).getCodigoBarras();
                } else {
                    codigobarras = produto.getCodigoBarras();
                }
                if (String.valueOf(codigobarras).length() <= 14 && String.valueOf(codigobarras).length() >= 7) {
                    v_produto.put(codigobarras, produto);
                }
            }

            ProgressBar.setMaximum(v_produto.size());

            ProgressBar.setStatus("Atualizando dados produto...Preço...");
            StringBuilder sql = new StringBuilder();

            //Zera os custos
            //stm.execute("update produtocomplemento set custosemimposto = 0, custocomimposto = 0 where id_loja = " + id_loja + " and dataultimaentrada is null");
            //stm.execute("update implantacao.codigoanterior set custosemimposto = 0, custocomimposto = 0");
            try (Statement stm = Conexao.createStatement()) {
                Map<Long, Long> idPorEan = carregarCodigoBarras();
                for (ProdutoVO i_produto : v_produto.values()) {
                    for (ProdutoComplementoVO oComplemento : i_produto.vComplemento) {

                        long codigobarras;

                        if (i_produto.getvAutomacao() != null && i_produto.getvAutomacao().size() == 1) {
                            codigobarras = i_produto.getvAutomacao().get(0).getCodigoBarras();
                        } else {
                            codigobarras = i_produto.getCodigoBarras();
                        }

                        Long idAtualProduto = idPorEan.get(codigobarras);

                        if (idAtualProduto != null && idAtualProduto != 0) {
                            try (Statement st = Conexao.createStatement()) {
                                try (ResultSet rs = st.executeQuery(
                                        "select * from oferta where id_produto = " + idAtualProduto + " and id_loja = " + idLojaVR
                                )) {
                                    if (!rs.next()) {
                                        sql.append("UPDATE produtocomplemento SET ");
                                        sql.append("precovenda = " + oComplemento.precoVenda + ", ");
                                        sql.append("precodiaseguinte = " + oComplemento.precoDiaSeguinte + " ");
                                        sql.append("where id_produto = " + idAtualProduto + " ");
                                        sql.append("and id_loja = " + idLojaVR + "; ");

                                        sql.append(" UPDATE implantacao.codigoanterior SET ");
                                        sql.append(" precovenda = " + oComplemento.precoVenda + " ");
                                        sql.append(" WHERE codigoatual = " + idAtualProduto + " ");
                                        sql.append(" AND id_loja = " + idLojaVR + "; ");
                                        x++;
                                    }
                                }
                            }
                        }
                    }
                    cont++;
                    if (x == QTD_PARA_EXECUTAR_SQL || cont >= v_produto.size()) {
                        stm.execute(sql.toString());
                        sql = new StringBuilder();
                        x = 0;
                    }
                    ProgressBar.next();
                }
            }
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void acertarDescricao(List<ProdutoVO> produtos) throws Exception {
        ProgressBar.setStatus("Ajustando as descrições....");
        Conexao.begin();
        try {
            try (PreparedStatement stm = Conexao.prepareStatement(
                    "update produto set descricaocompleta = ?, descricaoreduzida = ?, descricaogondola = ? where id = ?"
            )) {
                ProgressBar.setMaximum(produtos.size());

                Map<Double, CodigoAnteriorVO> anteriores = new CodigoAnteriorDAO().carregarCodigoAnterior();

                for (ProdutoVO produto : produtos) {
                    CodigoAnteriorVO anterior = anteriores.get((double) produto.getId());
                    if (anterior != null) {
                        stm.setString(1, produto.getDescricaoCompleta());
                        stm.setString(2, produto.getDescricaoReduzida());
                        stm.setString(3, produto.getDescricaoGondola());
                        stm.setInt(4, (int) anterior.getCodigoatual());
                        stm.execute();
                    }
                    ProgressBar.next();
                }
            }
            Conexao.commit();
        } catch (Exception e) {
            Conexao.rollback();
            throw e;
        }
    }

    public void acertarDescricaoSemCodigoAnterior(List<ProdutoVO> produtos) throws Exception {
        ProgressBar.setStatus("Ajustando as descrições....");
        Conexao.begin();
        try {
            try (PreparedStatement stm = Conexao.prepareStatement(
                    "update produto set descricaocompleta = ?, descricaoreduzida = ?, descricaogondola = ? where id = ?"
                    + "and descricaocompleta like '%?%'"
            )) {
                ProgressBar.setMaximum(produtos.size());

                for (ProdutoVO produto : produtos) {

                    stm.setString(1, produto.getDescricaoCompleta());
                    stm.setString(2, produto.getDescricaoReduzida());
                    stm.setString(3, produto.getDescricaoGondola());
                    stm.setInt(4, (int) produto.getId());
                    stm.execute();
                    ProgressBar.next();
                }
            }
            Conexao.commit();
        } catch (Exception e) {
            Conexao.rollback();
            throw e;
        }
    }

    public void incluirICMSLoja(List<ProdutoVO> v_produto) throws Exception {
        try {
            Conexao.begin();
            try (Statement stm = Conexao.createStatement()) {
                ProgressBar.setMaximum(v_produto.size());
                ProgressBar.setStatus("Atualizando dados produto...ICMS...");
                Map<Double, CodigoAnteriorVO> anteriores = new CodigoAnteriorDAO().carregarCodigoAnterior();
                MultiMap<Integer, Void> prodAliq = new MultiMap<>();

                try (ResultSet rst = stm.executeQuery(
                        "select id_produto, id_estado from produtoaliquota"
                )) {
                    while (rst.next()) {
                        prodAliq.put(null, rst.getInt("id_produto"), rst.getInt("id_estado"));
                    }
                }

                for (ProdutoVO i_produto : v_produto) {
                    if (i_produto.idDouble > 0) {
                        IdProduto = i_produto.idDouble;
                    } else {
                        IdProduto = i_produto.id;
                    }

                    CodigoAnteriorVO vCodigoAnterior = anteriores.get(IdProduto);

                    if (vCodigoAnterior != null) {
                        for (ProdutoAliquotaVO oAliquota : i_produto.vAliquota) {
                            SQLBuilder builder = new SQLBuilder();
                            builder.setTableName("produtoaliquota");
                            builder.put("id_produto", vCodigoAnterior.getCodigoatual());
                            builder.put("id_estado", oAliquota.idEstado);
                            builder.put("id_aliquotadebito", oAliquota.idAliquotaDebito);
                            builder.put("id_aliquotacredito", oAliquota.idAliquotaCredito);
                            builder.put("id_aliquotadebitoforaestado", oAliquota.idAliquotaDebitoForaEstado);
                            builder.put("id_aliquotacreditoforaestado", oAliquota.idAliquotaCreditoForaEstado);
                            builder.put("id_aliquotadebitoforaestadonf", oAliquota.idAliquotaDebitoForaEstadoNF);
                            builder.put("id_aliquotaconsumidor", oAliquota.getIdAliquotaConsumidor());
                            builder.setWhere("id_produto = " + vCodigoAnterior.getCodigoatual() + " and id_estado = " + oAliquota.idEstado);

                            boolean existe = prodAliq.containsKey((int) vCodigoAnterior.getCodigoatual(), oAliquota.idEstado);

                            if (existe) {
                                stm.execute(builder.getUpdate());
                            } else {
                                stm.execute(builder.getInsert());
                                prodAliq.put(null, (int) vCodigoAnterior.getCodigoatual(), oAliquota.idEstado);
                            }
                        }
                    }

                    ProgressBar.next();
                }
            }

            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    // INICIO METODOS PADRÕES 
    private class BalancaMap {

        int codigoProduto = (int) 0f;

        public BalancaMap(int codigoProduto) {
            this.codigoProduto = codigoProduto;
        }

        public BalancaMap() {
        }
    }

    private Map<Integer, BalancaMap> carregarProdutoBalanca() throws Exception {
        Conexao.begin();
        try {
            Map<Integer, BalancaMap> temp = new HashMap<>();

            Statement stm = Conexao.createStatement();
            try (ResultSet rst = stm.executeQuery(
                    "select codigo from implantacao.produtobalanca"
            )) {
                while (rst.next()) {
                    BalancaMap map = new BalancaMap();
                    map.codigoProduto = rst.getInt("codigo");
                    temp.put(map.codigoProduto, map);
                }
            } finally {
                stm.close();
            }
            Conexao.commit();

            return temp;

        } catch (Exception e) {
            Conexao.rollback();
            throw e;
        }
    }

    /*     CASO PRECISE ALTERAR ALGUM METODO POR UMA PARTICULARIDADE 
     DE UM CLIENTE, CRIAR O METODO IGUAL E COM O NOME DELE.       */
    public Map<Double, Integer> carregarCodigoAnterior() throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        Map<Double, Integer> vProduto = new HashMap<>();

        try {

            stm = Conexao.createStatement();

            sql = new StringBuilder();
            sql.append("select codigoanterior, codigoatual from implantacao.codigoanterior ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {
                double codigoAnterior = rst.getDouble("codigoanterior");
                int codigoAtual = rst.getInt("codigoatual");

                vProduto.put(codigoAnterior, codigoAtual);
            }

            return vProduto;
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        } finally {
            Conexao.destruir(null, stm, rst);
        }
    }

    public Map<Long, Long> carregarCodigoBarras() throws Exception {

        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;

        try {
            stm = Conexao.createStatement();

            Map<Long, Long> vProdutoDestino = new HashMap<>();

            sql = new StringBuilder();

            sql.append("SELECT codigobarras, id_produto FROM produtoautomacao");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {
                long codBarrasDestino = rst.getLong("codigobarras");
                long id_produto = rst.getLong("id_produto");

                vProdutoDestino.put(codBarrasDestino, id_produto);
            }

            return vProdutoDestino;

        } catch (Exception e) {
            Conexao.rollback();
            throw e;
        } finally {
            Conexao.destruir(null, stm, rst);
        }

    }

    public void salvarCodigoBarrasAnterior(List<ProdutoVO> v_produto) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;

        try {
            Conexao.begin();
            stm = Conexao.createStatement();
            ProgressBar.setStatus("Importando dados...Codigo Barras Anterior...");
            ProgressBar.setMaximum(v_produto.size());

            for (ProdutoVO i_produto : v_produto) {
                sql = new StringBuilder();
                sql.append("INSERT INTO implantacao.codigoanterior_barras( ");
                sql.append("codigobarras, codigoanterior) ");
                sql.append("VALUES (");
                sql.append(i_produto.codigoBarras + ", ");
                sql.append(i_produto.idDouble + " ");
                sql.append(");");

                stm.execute(sql.toString());
                ProgressBar.next();
            }

            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void salvar(List<ProdutoVO> v_produto, int i_idLojaDestino, List<LojaVO> vLoja) throws Exception {
        salvar(v_produto, i_idLojaDestino, false, vLoja, false, 60000);
    }

    public void salvar(List<ProdutoVO> v_produto, int i_idLojaDestino, List<LojaVO> vLoja, boolean unificacaoProduto) throws Exception {
        salvar(v_produto, i_idLojaDestino, false, vLoja, unificacaoProduto, 60000);
    }

    public void salvar(List<ProdutoVO> v_produto, int i_idLojaDestino, List<LojaVO> vLoja, boolean unificacaoProduto, int qtdeProdutoCodigoInterno) throws Exception {
        salvar(v_produto, i_idLojaDestino, false, vLoja, unificacaoProduto, qtdeProdutoCodigoInterno);
    }

    // SALVAR PRINCIPAL
    public void salvar(List<ProdutoVO> produtos, int i_idLojaDestino, boolean atualizarCadastro, List<LojaVO> vLoja, boolean unificacaoProduto, int qtdeProdutoCodigoInterno) throws Exception {
        Statement stm = null, stm2 = null, stm3 = null;
        ResultSet rst = null, rst2 = null;
        StringBuilder sql = null;
        CestDAO cestDAO = new CestDAO();

        String dadosProduto = "";

        MercadologicoAnteriorDAO mercadologicoAnteriorDao = new MercadologicoAnteriorDAO();

        try {
            Conexao.begin();

            stm = Conexao.createStatement();
            stm2 = Conexao.createStatement();
            stm3 = Conexao.createStatement();

            Stack<Integer> idsBalanca;
            Set<Integer> idsExistentes;
            Stack<Integer> idsNormais;

            {
                CodigoInternoDAO dao = new CodigoInternoDAO();
                idsBalanca = dao.getIdsVagosBalanca();
                idsNormais = dao.getIdsVagosNormais();
                idsExistentes = dao.getIdsExistentes();
            }

            //<editor-fold defaultstate="collapsed" desc="ORDENANDO A LISTA DE INCLUSAO">
            List<ProdutoVO> menorQue6dig = new ArrayList<>();
            List<ProdutoVO> maiorQue6dig = new ArrayList<>();
            for (ProdutoVO produto : produtos) {
                double id;
                if (produto.idDouble > 0) {
                    id = produto.idDouble;
                } else {
                    id = produto.id;
                }

                if (id <= 999999) {
                    menorQue6dig.add(produto);
                } else {
                    maiorQue6dig.add(produto);
                }
            }

            List<ProdutoVO> v_produto = new ArrayList<>(menorQue6dig);
            v_produto.addAll(maiorQue6dig);

            menorQue6dig = null;
            maiorQue6dig = null;
            //</editor-fold>

            ProgressBar.setMaximum(v_produto.size());
            int cont = 0;
            //Obtem o código de família anterior
            Map<Long, FamiliaProdutoVO> familiaProdutoAnteriores = new FamiliaProdutoDAO().carregarAnteriores();
            MultiMap<String, FamiliaProdutoVO> familiaProdutoAnterioresV2 = new FamiliaProdutoDAO().carregarAnteriores2();

            if (atualizarCadastro) {

                ProgressBar.setStatus("Atualizando dados produto complemento...");

                for (ProdutoVO i_produto : v_produto) {
                    for (ProdutoComplementoVO oComplemento : i_produto.vComplemento) {

                        sql = new StringBuilder();
                        sql.append("UPDATE produtocomplemento SET ");
                        sql.append("prateleira='" + oComplemento.prateleira + "',");
                        sql.append("secao='" + oComplemento.secao + "',");
                        sql.append("estoqueminimo=" + oComplemento.estoqueMinimo + ",");
                        sql.append("estoquemaximo=" + oComplemento.estoqueMaximo + ",");
                        sql.append("valoripi=" + oComplemento.valorIpi + ",");
                        sql.append("dataultimopreco=" + (oComplemento.dataUltimoPreco.isEmpty() ? null : "'" + Util.formatDataBanco(oComplemento.dataUltimoPreco) + "'") + ",");
                        sql.append("dataultimaentrada=" + (oComplemento.dataUltimaEntrada.isEmpty() ? null : "'" + Util.formatDataBanco(oComplemento.dataUltimaEntrada) + "'") + ",");
                        sql.append("custosemimposto=" + oComplemento.custoSemImposto + ",");
                        sql.append("custocomimposto=" + oComplemento.custoComImposto + ",");
                        sql.append("custosemimpostoanterior=" + oComplemento.custoSemImpostoAnterior + ",");
                        sql.append("custocomimpostoanterior=" + oComplemento.custoComImpostoAnterior + ",");
                        sql.append("precovenda=" + oComplemento.precoVenda + ",");
                        sql.append("precovendaanterior=" + oComplemento.precoVendaAnterior + ",");
                        sql.append("precodiaseguinte=" + oComplemento.precoDiaSeguinte + ",");
                        sql.append("estoque=" + oComplemento.estoque + ",");
                        sql.append("troca=" + oComplemento.troca + ",");
                        sql.append("emiteetiqueta=" + oComplemento.emiteEtiqueta + ",");
                        sql.append("custosemperdasemimposto=" + oComplemento.custoSemPerdaSemImposto + ",");
                        sql.append("custosemperdasemimpostoanterior=" + oComplemento.custoMedioSemImpostoAnterior + ",");
                        sql.append("customediocomimposto=" + oComplemento.custoMedioComImposto + ",");
                        sql.append("customediosemimposto=" + oComplemento.custoMedioSemImposto + ",");
                        sql.append("id_aliquotacredito=" + oComplemento.idAliquotaCredito + ",");
                        sql.append("dataultimavenda=" + (oComplemento.dataUltimaVenda.isEmpty() ? null : "'" + Util.formatDataBanco(oComplemento.dataUltimaVenda) + "'") + ",");
                        sql.append("teclaassociada=" + oComplemento.teclaAssociada + ",");
                        sql.append("id_situacaocadastro=" + oComplemento.idSituacaoCadastro + ",");
                        sql.append("descontinuado=" + oComplemento.descontinuado + ",");
                        sql.append("quantidadeultimaentrada=" + oComplemento.quantidadeUltimaEntrada + ",");
                        sql.append("centralizado=" + oComplemento.centralizado + ",");
                        sql.append("operacional=" + oComplemento.operacional + ",");
                        sql.append("valoricmssubstituicao=" + oComplemento.valorIcmsSubstituicao + ",");
                        sql.append("dataultimaentradaanterior=" + (oComplemento.dataUltimaEntradaAnterior.isEmpty() ? null : "'" + Util.formatDataBanco(oComplemento.dataUltimaEntradaAnterior) + "'") + ",");
                        sql.append("cestabasica=" + oComplemento.cestaBasica + ",");
                        sql.append("customediocomimpostoanterior=" + oComplemento.custoMedioComImpostoAnterior + ",");
                        sql.append("customediosemimpostoanterior=" + oComplemento.custoMedioSemImpostoAnterior + ",");
                        sql.append("id_tipopiscofinscredito=" + oComplemento.idTipoPisCofinsCredito + ",");
                        sql.append("valoroutrassubstituicao=" + oComplemento.valorOutrasSubstituicao + "");
                        sql.append(" WHERE id_produto = " + i_produto.id + " ");
                        sql.append(" AND id_loja = " + i_idLojaDestino + ";");
                    }

                    if (unificacaoProduto) {
                        for (ProdutosUnificacaoVO oProdutoUnificado : i_produto.vProdutosUnificacao) {
                            oProdutoUnificado.codigoatual = i_produto.id;

                            if (String.valueOf(oProdutoUnificado.barras).length() > 14) {
                                oProdutoUnificado.barras = Long.parseLong(String.valueOf(oProdutoUnificado.barras).substring(0, 14));
                            }

                            sql.append("INSERT INTO implantacao.produtos_unificacao( ");
                            sql.append("codigoanterior, codigoatual, barras, descricaoantiga,existe) ");
                            sql.append("VALUES ( ");
                            sql.append(oProdutoUnificado.codigoanterior + ", ");
                            sql.append(i_produto.id + ", ");
                            sql.append(oProdutoUnificado.barras + ", ");
                            sql.append("'" + oProdutoUnificado.descricao + "',");
                            sql.append("True);");
                        }
                    }
                    stm.execute(sql.toString());
                    ProgressBar.next();
                }

                stm.executeBatch();

            } else {

                ProgressBar.setStatus("Cadastrando novos produtos...");
                for (ProdutoVO i_produto : v_produto) {
                    Inserir = false;
                    if (!unificacaoProduto && !naoVerificarCodigoAnterior) {

                        sql = new StringBuilder();
                        sql.append("select p.id from produto p ");
                        sql.append("inner join implantacao.codigoanterior ant on ant.codigoatual = p.id ");

                        if (!usarCodigoAnterior) {
                            String codigoAnteriorStr = null;
                            if (i_produto.getvCodigoAnterior().get(0) != null) {
                                codigoAnteriorStr = i_produto.getvCodigoAnterior().get(0).getCodigoAnteriorStr();
                            }
                            //Se houver um código string informado entra no if
                            if (codigoAnteriorStr != null && !"".equals(codigoAnteriorStr.trim())) {
                                if (i_produto.idDouble > 0) {
                                    IdProduto = i_produto.idDouble;
                                } else {
                                    IdProduto = i_produto.id;
                                }
                                sql.append("where ant.codigoanterior::varchar = " + Utils.quoteSQL(codigoAnteriorStr));
                            } else if (i_produto.idDouble > 0) {
                                //Senão se o ID informado for double executa aqui.
                                IdProduto = i_produto.idDouble;
                                //sql.append("where CAST(ant.codigoanterior AS NUMERIC(14,0)) = CAST(" + IdProduto + " AS NUMERIC(14,0)) ");
                                sql.append("where ant.codigoanterior::varchar = '" + String.format("%.0f", IdProduto) + "'");
                            } else {
                                //Senão usa o id comum.
                                IdProduto = i_produto.id;
                                sql.append("where ant.codigoanterior = " + IdProduto);
                            }
                        } else {
                            CodigoAnteriorVO anterior = i_produto.getvCodigoAnterior().get(0);
                            codigoAnterior = i_produto.codigoAnterior;
                            String codigo;
                            if (anterior != null && anterior.getCodigoAnteriorStr() != null) {
                                codigo = anterior.getCodigoAnteriorStr();
                            } else {
                                codigo = String.format("%.0f", codigoAnterior);
                            }
                            sql.append("where ant.codigoanterior::varchar = " + Utils.quoteSQL(codigo));
                        }

                        if (verificarLoja) {
                            sql.append(" and id_loja = " + i_idLojaDestino);
                        }

                        dadosProduto = i_produto.getIdDouble() + " - " + i_produto.getDescricaoCompleta() + "\n";
                        dadosProduto += sql.toString();

                        rst2 = stm.executeQuery(sql.toString());
                        if (!rst2.next()) {
                            Inserir = true;
                            if (i_produto.id > 0) {
                                IdProduto = i_produto.id;
                            } else {
                                IdProduto = i_produto.idDouble;
                            }
                        } else {
                            Inserir = false;
                            IdProduto = Double.parseDouble(rst2.getString("id"));
                        }
                    } else if (usarCodigoBalancaComoID && i_produto.eBalanca) {
                        IdProduto = i_produto.getvCodigoAnterior().get(0).getCodigobalanca();

                        rst2 = stm.executeQuery("select p.id from produto p where id = " + IdProduto);
                        if (!rst2.next()) {
                            Inserir = true;
                        } else {
                            Inserir = false;
                            IdProduto = Double.parseDouble(rst2.getString("id"));
                        }
                    } else {
                        Inserir = true;

                        if (i_produto.id > 0) {
                            IdProduto = i_produto.id;
                        } else {
                            IdProduto = i_produto.idDouble;
                        }
                    }

                    if (Inserir) {
                        sql = new StringBuilder();

                        sql.append("INSERT INTO produto (id, descricaocompleta, qtdembalagem, id_tipoembalagem, mercadologico1, mercadologico2, mercadologico3,");
                        sql.append(" mercadologico4, mercadologico5, id_comprador, id_familiaproduto, descricaoreduzida, pesoliquido, datacadastro,");
                        sql.append(" validade, pesobruto, tara, comprimentoembalagem, larguraembalagem, alturaembalagem, perda, margem, verificacustotabela,");
                        sql.append(" descricaogondola, dataalteracao, id_produtovasilhame, ncm1, ncm2, ncm3, excecao, id_tipomercadoria, fabricacaopropria,");
                        sql.append(" sugestaopedido, sugestaocotacao, aceitamultiplicacaopdv, id_fornecedorfabricante, id_divisaofornecedor, id_tipoproduto, id_tipopiscofins,");
                        sql.append(" id_tipopiscofinscredito, custofinal, percentualipi, percentualfrete, percentualencargo, percentualperda, percentualsubstituicao, pesavel,");
                        sql.append(" sazonal, consignado, ddv, permitetroca, temperatura, id_tipoorigemmercadoria, ipi, vendacontrolada, tiponaturezareceita,");
                        sql.append(" vendapdv, permitequebra, permiteperda, impostomedioimportado, impostomedionacional, impostomedioestadual, utilizatabelasubstituicaotributaria,");
                        sql.append(" utilizavalidadeentrada, id_tipolocaltroca, id_tipocompra, codigoanp, numeroparcela, qtddiasminimovalidade, id_cest, id_normareposicao)");

                        sql.append(" VALUES ");
                        sql.append(" (");

                        if (usarCodigoBalancaComoID && i_produto.eBalanca) {
                            IdProduto = i_produto.getvCodigoAnterior().get(0).getCodigobalanca();
                        }

                        //<editor-fold defaultstate="collapsed" desc="TRATAMENTO DO ID DO PRODUTO">
                        {
                            boolean gerarId = false;
                            //Se o valor do id for inválido gera um novo automaticamente, senão
                            if (IdProduto < 1 || IdProduto > 999999) {
                                gerarId = true;
                            } else {
                                //Se já existir um produto com esse id, gera um novo id, senão
                                if (idsExistentes.contains((Integer) (int) IdProduto)) {
                                    gerarId = true;
                                } else {
                                    //Se for implantação externa e se for para gerar um código automaticamente executa
                                    if (implantacaoExterna && gerarCodigo) {
                                        gerarId = true;
                                    }
                                    //Senão mantem o código
                                }
                            }

                            //Se true gera um novo id para o produto
                            if (gerarId) {
                                if (i_produto.eBalanca) {
                                    IdProduto = idsBalanca.pop();
                                } else {
                                    IdProduto = idsNormais.pop();
                                }
                            } else {
                                //Caso não seja necessário gerar um novo id,
                                //é necessário remover o id das pilhas de ids disponíveis para uso
                                idsBalanca.remove((Integer) (int) IdProduto);
                                idsNormais.remove((Integer) (int) IdProduto);
                            }
                            //Inclui o id na lista dos existentes
                            idsExistentes.add((Integer) (int) IdProduto);
                        }
                        //</editor-fold>

                        if ("".equals(i_produto.dataCadastro.trim())) {
                            i_produto.dataCadastro = new DataProcessamentoDAO().get();
                        }

                        sql.append(IdProduto + ",");
                        sql.append("'" + i_produto.descricaoCompleta + "',");
                        sql.append(i_produto.qtdEmbalagem + ",");
                        sql.append(i_produto.idTipoEmbalagem + ",");

                        if (!usarMercadoligicoProduto) {

                            if (usarMercadologicoAcertar) {
                                MercadologicoVO mercadologico
                                        = mercadologicoAnteriorDao.makeMercadologicoIntegracao(
                                                i_produto.mercadologico1,
                                                i_produto.mercadologico2,
                                                i_produto.mercadologico3,
                                                i_produto.mercadologico4,
                                                i_produto.mercadologico5);
                                if (mercadologico.mercadologico1 == 0
                                        || mercadologico.mercadologico2 == 0
                                        || mercadologico.mercadologico3 == 0) {
                                    mercadologico = MercadologicoDAO.getMaxMercadologico();
                                }

                                sql.append(mercadologico.mercadologico1 + ",");
                                sql.append(mercadologico.mercadologico2 + ",");
                                sql.append(mercadologico.mercadologico3 + ",");
                                sql.append(mercadologico.mercadologico4 + ",");
                                sql.append(mercadologico.mercadologico5 + ",");

                            } else {
                                MercadologicoVO mercadologico
                                        = mercadologicoAnteriorDao.makeMercadologico(
                                                i_produto.mercadologico1,
                                                i_produto.mercadologico2,
                                                i_produto.mercadologico3,
                                                i_produto.mercadologico4,
                                                i_produto.mercadologico5);

                                if (mercadologico.mercadologico1 == 0
                                        || mercadologico.mercadologico2 == 0
                                        || mercadologico.mercadologico3 == 0) {
                                    mercadologico = MercadologicoDAO.getMaxMercadologico();
                                }

                                sql.append(mercadologico.mercadologico1 + ",");
                                sql.append(mercadologico.mercadologico2 + ",");
                                sql.append(mercadologico.mercadologico3 + ",");
                                sql.append(mercadologico.mercadologico4 + ",");
                                sql.append(mercadologico.mercadologico5 + ",");

                            }
                        } else {

                            if (MercadologicoDAO.existeMercadologico(
                                    i_produto.mercadologico1, i_produto.mercadologico2,
                                    i_produto.mercadologico3, i_produto.mercadologico4, i_produto.mercadologico5)) {

                                sql.append(i_produto.mercadologico1 + ",");
                                sql.append(i_produto.mercadologico2 + ",");
                                sql.append(i_produto.mercadologico3 + ",");
                                sql.append(i_produto.mercadologico4 + ",");
                                sql.append(i_produto.mercadologico5 + ",");
                            } else {
                                MercadologicoVO mercadologico;
                                mercadologico = MercadologicoDAO.getMaxMercadologico();
                                sql.append(mercadologico.mercadologico1 + ",");
                                sql.append(mercadologico.mercadologico2 + ",");
                                sql.append(mercadologico.mercadologico3 + ",");
                                sql.append(mercadologico.mercadologico4 + ",");
                                sql.append(mercadologico.mercadologico5 + ",");
                            }
                        }

                        sql.append(i_produto.idComprador + ",");

                        if (i_produto.getFamiliaProduto() != null && !"".equals(i_produto.getFamiliaProduto().trim())) {
                            /*
                             Em alguns casos pode ser que não haja como utilizar 
                             um inteiro como chave nestes casos pode-se usar o
                             campo familiaproduto que é String.
                             */
                            FamiliaProdutoVO vo = familiaProdutoAnterioresV2.get(getImportSistema(), getImportLoja(), i_produto.getFamiliaProduto());
                            if (vo != null) {
                                sql.append(vo.getId() + ",");
                            } else {
                                sql.append("null,");
                            }
                        } else if (i_produto.getIdFamiliaProduto() != -1) {
                            /*
                             Se o id da família for maior que 0, utiliza ao produto
                             familia através de id inteiro.
                             */
                            FamiliaProdutoVO vo = familiaProdutoAnteriores.get((long) i_produto.getIdFamiliaProduto());
                            if (vo != null) {
                                sql.append(vo.getId() + ",");
                            } else {
                                sql.append("null,");
                            }
                        } else {
                            /*
                             Caso contrário envia null.
                             */
                            sql.append("null,");
                        }

                        sql.append("'" + i_produto.descricaoReduzida + "',");
                        sql.append(MathUtils.trunc(i_produto.pesoLiquido, 3, 9999) + ",");

                        sql.append("'" + Util.formatDataBanco(i_produto.dataCadastro) + "',");

                        sql.append(i_produto.validade + ",");
                        sql.append(MathUtils.trunc(i_produto.pesoBruto, 3, 9999) + ",");
                        sql.append(i_produto.tara + ",");
                        sql.append(i_produto.comprimentoEmbalagem + ",");
                        sql.append(i_produto.larguraEmbalagem + ",");
                        sql.append(i_produto.alturaEmbalagem + ",");
                        sql.append(i_produto.perda + ",");
                        sql.append(i_produto.margem + ",");
                        sql.append(i_produto.verificaCustoTabela + ",");
                        sql.append("'" + i_produto.descricaoGondola + "',");
                        sql.append((i_produto.dataAlteracao.isEmpty() ? null : "'" + Util.formatDataBanco(i_produto.dataAlteracao) + "'") + ",");
                        sql.append((i_produto.idProdutoVasilhame == -1 ? null : i_produto.idProdutoVasilhame) + ",");
                        sql.append((i_produto.ncm1 == -1 ? null : i_produto.ncm1) + ",");
                        sql.append((i_produto.ncm2 == -1 ? null : i_produto.ncm2) + ",");
                        sql.append((i_produto.ncm3 == -1 ? null : i_produto.ncm3) + ",");
                        sql.append((i_produto.excecao == -1 ? null : i_produto.excecao) + ",");
                        sql.append((i_produto.idTipoMercadoria == -1 ? null : i_produto.idTipoMercadoria) + ",");
                        sql.append(i_produto.fabricacaoPropria + ",");
                        sql.append(i_produto.sugestaoPedido + ",");
                        sql.append(i_produto.sugestaoCotacao + ",");
                        sql.append(i_produto.aceitaMultiplicacaoPdv + ",");
                        sql.append(i_produto.idFornecedorFabricante + ",");
                        sql.append(i_produto.idDivisaoFornecedor + ",");
                        sql.append(i_produto.idTipoProduto + ",");
                        sql.append(i_produto.idTipoPisCofinsDebito + ",");
                        sql.append(i_produto.idTipoPisCofinsCredito + ",");
                        sql.append(i_produto.custoFinal + ",");
                        sql.append(i_produto.percentualIpi + ",");
                        sql.append(i_produto.percentualFrete + ",");
                        sql.append(i_produto.percentualEncargo + ",");
                        sql.append(i_produto.percentualPerda + ",");
                        sql.append(i_produto.percentualSubstituicao + ",");
                        sql.append(i_produto.pesavel + ",");
                        sql.append(i_produto.sazonal + ",");
                        sql.append(i_produto.consignado + ",");
                        sql.append(i_produto.ddv + ",");
                        sql.append(i_produto.permiteTroca + ",");
                        sql.append(i_produto.temperatura + ",");
                        sql.append(i_produto.idTipoOrigemMercadoria + ",");
                        sql.append(i_produto.ipi + ",");
                        sql.append(i_produto.vendaControlada + ",");
                        sql.append((i_produto.tipoNaturezaReceita == -1 ? null : i_produto.tipoNaturezaReceita) + ",");
                        sql.append(i_produto.vendaPdv + ",");
                        sql.append(i_produto.permiteQuebra + ",");
                        sql.append(i_produto.permitePerda + ",");
                        sql.append(i_produto.impostoMedioImportado + ",");
                        sql.append(i_produto.impostoMedioNacional + ",");
                        sql.append(i_produto.impostoMedioEstadual + ",");
                        sql.append(i_produto.utilizaTabelaSubstituicaoTributaria + ",");
                        sql.append(i_produto.utilizaValidadeEntrada + ",");
                        sql.append(i_produto.idTipoLocalTroca + ",");
                        sql.append(i_produto.idTipoCompra + ",");
                        sql.append("'" + i_produto.codigoAnp + "',");
                        sql.append(i_produto.numeroParcela + ",");
                        sql.append(i_produto.qtdDiasMinimoValidade + ",");

                        if (i_produto.getCest1() != -1
                                && i_produto.getCest2() != -1
                                && i_produto.getCest3() != -1) {
                            CestVO cest = cestDAO.getCestValido(i_produto.getCest1(), i_produto.getCest2(), i_produto.getCest3());
                            sql.append(Utils.longIntSQL(cest.getId(), 0) + ",");
                        } else {
                            sql.append("null,");
                        }

                        sql.append(Utils.longIntSQL(i_produto.getIdNormaReposicao(), -1) + ");");

                        try {
                            stm.execute(sql.toString());
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(null, "Erro " + IdProduto + " - " + sql.toString() + ex);
                            throw ex;
                        }

                        for (ProdutoComplementoVO oComplemento : i_produto.vComplemento) {
                            for (LojaVO oLoja : vLoja) {
                                sql = new StringBuilder();
                                sql.append("INSERT INTO produtocomplemento (id_produto, prateleira, secao,estoqueminimo,");
                                sql.append(" estoquemaximo,valoripi,dataultimopreco,dataultimaentrada,");
                                sql.append(" custosemimposto,custocomimposto,custosemimpostoanterior,");
                                sql.append(" custocomimpostoanterior,precovenda,precovendaanterior,");
                                sql.append(" precodiaseguinte,estoque,troca,emiteetiqueta,custosemperdasemimposto,");
                                sql.append(" custosemperdasemimpostoanterior,customediocomimposto,customediosemimposto,");
                                sql.append(" id_aliquotacredito,dataultimavenda,teclaassociada,id_situacaocadastro,");
                                sql.append(" id_loja,descontinuado,quantidadeultimaentrada,centralizado,operacional,valoricmssubstituicao,");
                                sql.append(" dataultimaentradaanterior,cestabasica,customediocomimpostoanterior,customediosemimpostoanterior,id_tipopiscofinscredito,valoroutrassubstituicao)");

                                sql.append(" VALUES (");
                                sql.append(IdProduto + ",");
                                sql.append("'" + oComplemento.prateleira + "',");
                                sql.append("'" + oComplemento.secao + "',");
                                sql.append(oComplemento.estoqueMinimo + ",");
                                sql.append(oComplemento.estoqueMaximo + ",");
                                sql.append(oComplemento.valorIpi + ",");
                                sql.append((oComplemento.dataUltimoPreco.isEmpty() ? null : "'" + Util.formatDataBanco(oComplemento.dataUltimoPreco) + "'") + ",");
                                sql.append((oComplemento.dataUltimaEntrada.isEmpty() ? null : "'" + Util.formatDataBanco(oComplemento.dataUltimaEntrada) + "'") + ",");

                                sql.append(oComplemento.custoSemImposto + ",");
                                sql.append(oComplemento.custoComImposto + ",");
                                sql.append(oComplemento.custoSemImpostoAnterior + ",");
                                sql.append(oComplemento.custoComImpostoAnterior + ",");
                                sql.append(oComplemento.precoVenda + ",");
                                sql.append(oComplemento.precoVendaAnterior + ",");
                                sql.append(oComplemento.precoDiaSeguinte + ",");
                                if (unificacaoProduto) {
                                    if (oLoja.id == i_idLojaDestino) {
                                        sql.append(oComplemento.estoque + ",");
                                    } else {
                                        sql.append("0, ");
                                    }
                                } else {
                                    sql.append(oComplemento.estoque + ",");
                                }
                                sql.append(oComplemento.troca + ",");
                                sql.append(oComplemento.emiteEtiqueta + ",");
                                sql.append(oComplemento.custoSemPerdaSemImposto + ",");
                                sql.append(oComplemento.custoSemPerdaSemImpostoAnterior + ",");
                                sql.append(oComplemento.custoMedioComImposto + ",");
                                sql.append(oComplemento.custoMedioSemImposto + ",");
                                sql.append(oComplemento.idAliquotaCredito + ",");
                                sql.append((oComplemento.dataUltimaVenda.isEmpty() ? null : "'" + Util.formatDataBanco(oComplemento.dataUltimaVenda) + "'") + ",");
                                sql.append(oComplemento.teclaAssociada + ",");
                                sql.append(oComplemento.idSituacaoCadastro + ",");
                                sql.append(oLoja.id + ",");
                                sql.append(oComplemento.descontinuado + ",");
                                sql.append(oComplemento.quantidadeUltimaEntrada + ",");
                                sql.append(oComplemento.centralizado + ",");
                                sql.append(oComplemento.operacional + ",");
                                sql.append(oComplemento.valorIcmsSubstituicao + ",");
                                sql.append((oComplemento.dataUltimaEntradaAnterior.isEmpty() ? null : "'" + Util.formatDataBanco(oComplemento.dataUltimaEntradaAnterior) + "'") + ",");
                                sql.append(oComplemento.cestaBasica + ",");
                                sql.append(oComplemento.custoMedioComImpostoAnterior + ",");
                                sql.append(oComplemento.custoMedioSemImpostoAnterior + ",");
                                sql.append(oComplemento.idTipoPisCofinsCredito + ",");
                                sql.append(oComplemento.valorOutrasSubstituicao + ");");

                                stm.execute(sql.toString());

                            }

                        }
                        for (ProdutoAutomacaoVO oAutomacao : i_produto.vAutomacao) {

                            if (oAutomacao.codigoBarras == -1) {

                                if ((IdProduto <= 9999)
                                        && (i_produto.idTipoEmbalagem != 4)) {

                                    oAutomacao.codigoBarras = Utils.gerarEan13((int) IdProduto, false);

                                } else if ((IdProduto > 9999)
                                        && (i_produto.idTipoEmbalagem != 4)
                                        && (!i_produto.pesavel)) {

                                    oAutomacao.codigoBarras = Utils.gerarEan13((int) IdProduto, true);

                                } else if ((IdProduto > 9999)
                                        && (i_produto.idTipoEmbalagem != 4)
                                        && (i_produto.pesavel)) {

                                    oAutomacao.codigoBarras = Utils.gerarEan13((int) IdProduto, false);

                                } else if ((i_produto.idTipoEmbalagem != 4)
                                        && (i_produto.pesavel)) {

                                    oAutomacao.codigoBarras = Utils.gerarEan13((int) IdProduto, false);

                                } else if (i_produto.idTipoEmbalagem == 4) {

                                    oAutomacao.codigoBarras = Utils.gerarEan13((int) IdProduto, false);

                                } else {

                                    oAutomacao.codigoBarras = Utils.gerarEan13((int) IdProduto, true);

                                }
                            }

                            if (String.valueOf(oAutomacao.codigoBarras).length() > 14) {
                                oAutomacao.codigoBarras = Long.parseLong(String.valueOf(oAutomacao.codigoBarras).substring(0, 14));
                            }

                            if (oAutomacao.codigoBarras > -2) {

                                sql = new StringBuilder();
                                sql.append("SELECT id_produto FROM produtoautomacao");
                                if (usarCodigoBalancaComoID && i_produto.eBalanca) {
                                    sql.append(" WHERE cast(codigobarras as bigint) = cast(" + i_produto.getvCodigoAnterior().get(0).getCodigobalanca() + " as bigint) ");
                                } else {
                                    sql.append(" WHERE cast(codigobarras as bigint) = cast(" + oAutomacao.codigoBarras + " as bigint) ");
                                }

                                rst = stm.executeQuery(sql.toString());

                                if (!rst.next()) {
                                    sql = new StringBuilder();
                                    sql.append("INSERT INTO produtoautomacao (id_produto, codigobarras, qtdembalagem, id_tipoembalagem)");
                                    sql.append("VALUES (");
                                    sql.append(IdProduto + ",");
                                    if (usarCodigoBalancaComoID && i_produto.eBalanca) {
                                        //sql.append(i_produto.getvCodigoAnterior().get(0).getCodigobalanca() + ",");
                                        sql.append(IdProduto + ",");
                                    } else {
                                        sql.append(oAutomacao.codigoBarras + ",");
                                    }
                                    sql.append(oAutomacao.qtdEmbalagem + ",");
                                    sql.append(oAutomacao.idTipoEmbalagem);
                                    sql.append(")");

                                    stm.execute(sql.toString());
                                }
                            }
                        }

                        for (ProdutoAliquotaVO oAliquota : i_produto.vAliquota) {
                            sql = new StringBuilder();

                            sql.append("INSERT INTO produtoaliquota (id_produto, id_estado, id_aliquotadebito, id_aliquotacredito, id_aliquotadebitoforaestado,");
                            sql.append(" id_aliquotacreditoforaestado, id_aliquotadebitoforaestadonf, id_aliquotaconsumidor)");
                            sql.append(" VALUES(");

                            sql.append(IdProduto + ",");
                            sql.append(oAliquota.idEstado + ",");
                            sql.append(oAliquota.idAliquotaDebito + ",");
                            sql.append(oAliquota.idAliquotaCredito + ",");
                            sql.append(oAliquota.idAliquotaDebitoForaEstado + ",");
                            sql.append(oAliquota.idAliquotaCreditoForaEstado + ",");
                            sql.append(oAliquota.idAliquotaDebitoForaEstadoNF + ",");
                            sql.append(oAliquota.getIdAliquotaConsumidor());

                            sql.append(")");

                            stm.execute(sql.toString());

                        }

                        if (!unificacaoProduto) {
                            for (CodigoAnteriorVO oCodigoInterno : i_produto.vCodigoAnterior) {
                                oCodigoInterno.codigoatual = IdProduto;

                                if (String.valueOf(oCodigoInterno.barras).length() > 14) {
                                    oCodigoInterno.barras = Long.parseLong(String.valueOf(oCodigoInterno.barras).substring(0, 14));
                                }

                                sql = new StringBuilder();
                                sql.append("INSERT INTO implantacao.codigoanterior( ");
                                sql.append("codigoanterior, codigoatual, barras, naturezareceita, ");
                                sql.append("piscofinscredito, piscofinsdebito, ref_icmsdebito, estoque, e_balanca, ");
                                sql.append("codigobalanca, custosemimposto, custocomimposto, margem, precovenda, referencia, ncm, id_loja, produto_novo, codigoauxiliar, "
                                        + "codanterior) ");
                                sql.append("VALUES ( ");
                                if (oCodigoInterno.getCodigoAnteriorStr() != null) {
                                    sql.append(Utils.quoteSQL(oCodigoInterno.getCodigoAnteriorStr()) + ", ");
                                } else {
                                    sql.append(String.format("%.0f", oCodigoInterno.codigoanterior) + ", ");
                                }

                                sql.append(IdProduto + ", ");
                                sql.append((oCodigoInterno.barras == -1 ? null : oCodigoInterno.barras) + ", ");
                                sql.append((oCodigoInterno.naturezareceita == -1 ? null : oCodigoInterno.naturezareceita) + ", ");
                                sql.append((oCodigoInterno.piscofinscredito == -1 ? null : oCodigoInterno.piscofinscredito) + ", ");
                                sql.append((oCodigoInterno.piscofinsdebito == -1 ? null : oCodigoInterno.piscofinsdebito) + ", ");
                                sql.append((oCodigoInterno.ref_icmsdebito.isEmpty() ? null : "'" + oCodigoInterno.ref_icmsdebito + "'") + ", ");
                                sql.append((oCodigoInterno.estoque == -1 ? null : oCodigoInterno.estoque) + ", ");
                                sql.append(oCodigoInterno.e_balanca + ", ");
                                sql.append((oCodigoInterno.codigobalanca == -1 ? null : oCodigoInterno.codigobalanca) + ", ");
                                sql.append((oCodigoInterno.custosemimposto == -1 ? null : oCodigoInterno.custosemimposto) + ", ");
                                sql.append((oCodigoInterno.custocomimposto == -1 ? null : oCodigoInterno.custocomimposto) + ", ");
                                sql.append((oCodigoInterno.margem == -1 ? null : oCodigoInterno.margem) + ", ");
                                sql.append((oCodigoInterno.precovenda == -1 ? null : oCodigoInterno.precovenda) + ", ");
                                sql.append((oCodigoInterno.referencia == -1 ? null : oCodigoInterno.referencia) + ", ");
                                sql.append((oCodigoInterno.ncm.isEmpty() ? null : "'" + oCodigoInterno.ncm + "'") + ", ");
                                sql.append(i_idLojaDestino + ", true, ");
                                sql.append(Utils.quoteSQL(oCodigoInterno.getCodigoAuxiliar()) + ", "
                                        + "'" + oCodigoInterno.getCodAnterior() + "'");

                                sql.append(");");

                                stm.execute(sql.toString());
                            }
                        } else {
                            for (ProdutosUnificacaoVO oProdutoUnificado : i_produto.vProdutosUnificacao) {

                                oProdutoUnificado.codigoatual = IdProduto;

                                if (String.valueOf(oProdutoUnificado.barras).length() > 14) {
                                    oProdutoUnificado.barras = Long.parseLong(String.valueOf(oProdutoUnificado.barras).substring(0, 14));
                                }

                                sql = new StringBuilder();

                                sql.append("INSERT INTO implantacao.produtos_unificacao( ");
                                sql.append("codigoanterior, codigoatual, barras, descricaoantiga,existe ) ");
                                sql.append("VALUES ( ");
                                sql.append(oProdutoUnificado.codigoanterior + ", ");
                                sql.append(IdProduto + ", ");
                                sql.append(oProdutoUnificado.barras + ", ");
                                sql.append("'" + oProdutoUnificado.descricao + "',");
                                sql.append("False);");

                                stm.execute(sql.toString());
                            }
                        }
                    } else {
                        for (ProdutoAutomacaoVO oAutomacao : i_produto.vAutomacao) {
                            if ((oAutomacao.codigoBarras != -1)
                                    && (oAutomacao.codigoBarras != -2)) {

                                if (String.valueOf(oAutomacao.codigoBarras).length() > 14) {
                                    oAutomacao.codigoBarras = Long.parseLong(String.valueOf(oAutomacao.codigoBarras).substring(0, 14));
                                }

                                sql = new StringBuilder();
                                sql.append("SELECT id_produto FROM produtoautomacao");
                                if (usarCodigoBalancaComoID && i_produto.eBalanca) {
                                    sql.append(" WHERE cast(codigobarras as numeric(14,0)) = cast(" + i_produto.getvCodigoAnterior().get(0).codigobalanca + " as numeric(14,0)) ");
                                } else {
                                    sql.append(" WHERE cast(codigobarras as numeric(14,0)) = cast(" + oAutomacao.codigoBarras + " as numeric(14,0)) ");
                                }
                                rst = stm.executeQuery(sql.toString());

                                if (!rst.next()) {
                                    sql = new StringBuilder();
                                    sql.append("INSERT INTO produtoautomacao (id_produto, codigobarras, qtdembalagem, id_tipoembalagem)");
                                    sql.append("VALUES (");
                                    sql.append(IdProduto + ",");
                                    if (usarCodigoBalancaComoID && i_produto.eBalanca) {
                                        sql.append(i_produto.getvCodigoAnterior().get(0).codigobalanca + ",");
                                    } else {
                                        sql.append(oAutomacao.codigoBarras + ",");
                                    }

                                    sql.append(oAutomacao.qtdEmbalagem + ",");
                                    sql.append(oAutomacao.idTipoEmbalagem);
                                    sql.append(")");

                                    stm.execute(sql.toString());

                                    for (CodigoAnteriorVO oCodigoInterno : i_produto.vCodigoAnterior) {
                                        oCodigoInterno.codigoatual = IdProduto;

                                        if (String.valueOf(oCodigoInterno.barras).length() > 14) {
                                            oCodigoInterno.barras = Long.parseLong(String.valueOf(oCodigoInterno.barras).substring(0, 14));
                                        }

                                        sql = new StringBuilder();
                                        sql.append("select * from implantacao.codigoanterior ");
                                        if (oCodigoInterno.getCodigoAnteriorStr() != null) {
                                            sql.append("where codigoanterior = " + Utils.quoteSQL(oCodigoInterno.getCodigoAnteriorStr()) + " ");
                                        } else {
                                            sql.append("where codigoanterior = " + oCodigoInterno.codigoanterior + " ");
                                        }
                                        sql.append("and barras = " + oCodigoInterno.barras);

                                        rst2 = stm.executeQuery(sql.toString());

                                        if (!rst2.next()) {

                                            sql = new StringBuilder();
                                            sql.append("INSERT INTO implantacao.codigoanterior( ");
                                            sql.append("codigoanterior, codigoatual, barras, naturezareceita, ");
                                            sql.append("piscofinscredito, piscofinsdebito, ref_icmsdebito, estoque, e_balanca, ");
                                            sql.append("codigobalanca, custosemimposto, custocomimposto, margem, precovenda, referencia, ncm, id_loja, produto_novo) ");
                                            sql.append("VALUES ( ");
                                            if (oCodigoInterno.getCodigoAnteriorStr() != null) {
                                                sql.append(Utils.quoteSQL(oCodigoInterno.getCodigoAnteriorStr()) + ", ");
                                            } else {
                                                sql.append(oCodigoInterno.codigoanterior + ", ");
                                            }
                                            sql.append(IdProduto + ", ");
                                            sql.append((oCodigoInterno.barras < 0 ? null : oCodigoInterno.barras) + ", ");
                                            sql.append((oCodigoInterno.naturezareceita == -1 ? null : oCodigoInterno.naturezareceita) + ", ");
                                            sql.append((oCodigoInterno.piscofinscredito == -1 ? null : oCodigoInterno.piscofinscredito) + ", ");
                                            sql.append((oCodigoInterno.piscofinsdebito == -1 ? null : oCodigoInterno.piscofinsdebito) + ", ");
                                            sql.append((oCodigoInterno.ref_icmsdebito.isEmpty() ? null : "'" + oCodigoInterno.ref_icmsdebito + "'") + ", ");
                                            sql.append((oCodigoInterno.estoque == -1 ? null : oCodigoInterno.estoque) + ", ");
                                            sql.append(oCodigoInterno.e_balanca + ", ");
                                            sql.append((oCodigoInterno.codigobalanca == -1 ? null : oCodigoInterno.codigobalanca) + ", ");
                                            sql.append((oCodigoInterno.custosemimposto == -1 ? null : oCodigoInterno.custosemimposto) + ", ");
                                            sql.append((oCodigoInterno.custocomimposto == -1 ? null : oCodigoInterno.custocomimposto) + ", ");
                                            sql.append((oCodigoInterno.margem == -1 ? null : oCodigoInterno.margem) + ", ");
                                            sql.append((oCodigoInterno.precovenda == -1 ? null : oCodigoInterno.precovenda) + ", ");
                                            sql.append((oCodigoInterno.referencia == -1 ? null : oCodigoInterno.referencia) + ", ");
                                            sql.append("'" + (oCodigoInterno.ncm.isEmpty() ? null : oCodigoInterno.ncm) + "', ");
                                            sql.append(i_idLojaDestino + ", true ");

                                            sql.append(");");

                                        }

                                        stm.execute(sql.toString());
                                    }
                                }
                            }
                        }
                    }

                    ProgressBar.next();
                }
            }

            sql = new StringBuilder();
            sql.append("update produtoaliquota set id_aliquotaconsumidor = id_aliquotadebito; "
                    + "update produtoaliquota aaa set id_aliquotaconsumidor = aliq.id_aliquotaconsumidor "
                    + "from\n"
                    + "	(select\n"
                    + "		pa.*,\n"
                    + "		al_con.descricao\n"
                    + "	from\n"
                    + "		(select \n"
                    + "			pa.id,\n"
                    + "			pa.id_produto,\n"
                    + "			pa.id_estado,\n"
                    + "			pa.id_aliquotadebito,\n"
                    + "			al_deb.descricao,\n"
                    + "			(select id from aliquota where situacaotributaria != 20 and reduzido = 0 and porcentagem = al_deb.porcentagemfinal limit 1) id_aliquotaconsumidor\n"
                    + "		from \n"
                    + "			produtoaliquota pa\n"
                    + "			left join aliquota al_deb on pa.id_aliquotadebito = al_deb.id\n"
                    + "		where\n"
                    + "			al_deb.situacaotributaria = 20) pa\n"
                    + "		left join aliquota al_con on pa.id_aliquotaconsumidor = al_con.id) aliq\n"
                    + "where\n"
                    + "	aaa.id = aliq.id;");
            stm.execute(sql.toString());
            stm.close();
            stm2.close();
            stm3.close();
            Conexao.commit();

        } catch (Exception ex) {
            Conexao.rollback();
            JOptionPane.showMessageDialog(null, "Produto=" + IdProduto + " Erro=" + ex + "\n" + dadosProduto);
            throw ex;
        } finally {
            //Conexao.destruir(null, stm, rst);
        }
    }
    // SALVAR PRINCIPAL

    public void salvarIntegracao(List<ProdutoVO> produtos, int i_idLojaDestino, List<LojaVO> vLoja) throws Exception {
        Statement stm = null, stm2 = null, stm3 = null, stm4 = null;
        ResultSet rst = null, rst2 = null, rst3 = null, rst4 = null;
        StringBuilder sql = null, sql2 = null;
        CestDAO cestDAO = new CestDAO();

        MercadologicoAnteriorDAO mercadologicoAnteriorDao = new MercadologicoAnteriorDAO();

        Utils util = new Utils();

        try {
            Conexao.begin();

            stm = Conexao.createStatement();
            stm2 = Conexao.createStatement();
            stm3 = Conexao.createStatement();
            stm4 = Conexao.createStatement();

            //<editor-fold defaultstate="collapsed" desc="ORDENANDO A LISTA DE INCLUSAO">
            List<ProdutoVO> menorQue6dig = new ArrayList<>();
            List<ProdutoVO> maiorQue6dig = new ArrayList<>();
            for (ProdutoVO produto : produtos) {
                double id;
                if (produto.idDouble > 0) {
                    id = produto.idDouble;
                } else {
                    id = produto.id;
                }

                if (id <= 999999) {
                    menorQue6dig.add(produto);
                } else {
                    maiorQue6dig.add(produto);
                }
            }

            List<ProdutoVO> v_produto = new ArrayList<>(menorQue6dig);
            v_produto.addAll(maiorQue6dig);

            menorQue6dig = null;
            maiorQue6dig = null;
            //</editor-fold>

            ProgressBar.setMaximum(v_produto.size());

            //Obtem o código de família anterior
            //Map<Long, FamiliaProdutoVO> familiaProdutoAnteriores = new FamiliaProdutoDAO().carregarAnteriores();
            ProgressBar.setStatus("Cadastrando novos produtos Integração...Loja " + i_idLojaDestino + "...");
            for (ProdutoVO i_produto : v_produto) {
                Inserir = true;

                if (usarCodAnteriorIntegracao) {

                    if (String.valueOf(i_produto.codigoBarras).length() >= 7) {

                        sql = new StringBuilder();
                        sql.append("select p.id from produto p "
                                + "inner join implantacao.codigoanterior ant on ant.codigoatual = p.id "
                                + "where id_loja = " + i_idLojaDestino);

                        if (i_produto.id > 0) {
                            sql.append(" and ant.codigoanterior = " + i_produto.id);
                        } else {
                            sql.append(" and ant.codigoanterior = " + i_produto.idDouble);
                        }

                        rst4 = stm4.executeQuery(sql.toString());

                        if (!rst4.next()) {
                            sql = new StringBuilder();
                            sql.append("select * from produtoautomacao ");
                            sql.append("where codigobarras = " + i_produto.codigoBarras + " ");

                            rst = stm.executeQuery(sql.toString());

                            if (rst.next()) {
                                Inserir = false;
                            }

                            if (Inserir) {

                                sql = new StringBuilder();

                                sql.append("INSERT INTO produto (id, descricaocompleta, qtdembalagem, id_tipoembalagem, mercadologico1, mercadologico2, mercadologico3,");
                                sql.append(" mercadologico4, mercadologico5, id_comprador, id_familiaproduto, descricaoreduzida, pesoliquido, datacadastro,");
                                sql.append(" validade, pesobruto, tara, comprimentoembalagem, larguraembalagem, alturaembalagem, perda, margem, verificacustotabela,");
                                sql.append(" descricaogondola, dataalteracao, id_produtovasilhame, ncm1, ncm2, ncm3, excecao, id_tipomercadoria, fabricacaopropria,");
                                sql.append(" sugestaopedido, sugestaocotacao, aceitamultiplicacaopdv, id_fornecedorfabricante, id_divisaofornecedor, id_tipoproduto, id_tipopiscofins,");
                                sql.append(" id_tipopiscofinscredito, custofinal, percentualipi, percentualfrete, percentualencargo, percentualperda, percentualsubstituicao, pesavel,");
                                sql.append(" sazonal, consignado, ddv, permitetroca, temperatura, id_tipoorigemmercadoria, ipi, vendacontrolada, tiponaturezareceita,");
                                sql.append(" vendapdv, permitequebra, permiteperda, impostomedioimportado, impostomedionacional, impostomedioestadual, utilizatabelasubstituicaotributaria,");
                                sql.append(" utilizavalidadeentrada, id_tipolocaltroca, id_tipocompra, codigoanp, numeroparcela, qtddiasminimovalidade, id_cest, id_normareposicao)");

                                sql.append(" VALUES ");
                                sql.append(" (");

                                if (!implantacaoExterna) {

                                    if (i_produto.id > 0) {
                                        IdProduto = i_produto.id;
                                    } else {
                                        IdProduto = i_produto.idDouble;
                                    }

                                    if ((IdProduto <= 0) || (IdProduto >= 1000000)) {

                                        if (i_produto.eBalanca) {
                                            IdProduto = new CodigoInternoDAO().getIdProdutoBalanca();
                                        } else {
                                            IdProduto = new CodigoInternoDAO().getIdProduto(qtdeCodigoInterno);
                                        }
                                    }
                                } else {

                                    if (i_produto.eBalanca) {
                                        if (!gerarCodigo) {
                                            if ((IdProduto >= 1000000) || (IdProduto <= 0)) {
                                                IdProduto = new CodigoInternoDAO().getIdProdutoBalanca();
                                            }
                                        } else {
                                            IdProduto = new CodigoInternoDAO().getIdProdutoBalanca();
                                        }
                                    } else {
                                        if (!gerarCodigo) {
                                            if ((IdProduto >= 1000000) || (IdProduto <= 0)) {
                                                IdProduto = new CodigoInternoDAO().getIdProduto(qtdeCodigoInterno);
                                            }
                                        } else {
                                            IdProduto = new CodigoInternoDAO().getIdProduto(qtdeCodigoInterno);
                                        }
                                    }
                                }

                                if ("".equals(i_produto.dataCadastro.trim())) {
                                    i_produto.dataCadastro = new DataProcessamentoDAO().get();
                                }

                                if (implantacaoExterna) {

                                    sql2 = new StringBuilder();
                                    sql2.append("select id from produto ");
                                    sql2.append("where id = " + IdProduto);

                                    rst3 = stm3.executeQuery(sql2.toString());

                                    if (rst3.next()) {
                                        if (i_produto.eBalanca) {
                                            IdProduto = new CodigoInternoDAO().getIdProdutoBalanca();
                                        } else {
                                            IdProduto = new CodigoInternoDAO().getIdProduto(qtdeCodigoInterno);
                                        }
                                    }
                                }

                                sql.append(IdProduto + ",");
                                sql.append("'" + i_produto.descricaoCompleta + "',");
                                sql.append(i_produto.qtdEmbalagem + ",");
                                sql.append(i_produto.idTipoEmbalagem + ",");

                                MercadologicoVO mercadologico
                                        = mercadologicoAnteriorDao.makeMercadologicoIntegracao(
                                                i_produto.mercadologico1,
                                                i_produto.mercadologico2,
                                                i_produto.mercadologico3,
                                                i_produto.mercadologico4,
                                                i_produto.mercadologico5);

                                if (mercadologico.mercadologico1 == 0
                                        || mercadologico.mercadologico2 == 0
                                        || mercadologico.mercadologico3 == 0) {
                                    mercadologico = MercadologicoDAO.getMaxMercadologico();
                                }

                                sql.append(mercadologico.mercadologico1 + ",");
                                sql.append(mercadologico.mercadologico2 + ",");
                                sql.append(mercadologico.mercadologico3 + ",");
                                sql.append(mercadologico.mercadologico4 + ",");
                                sql.append(mercadologico.mercadologico5 + ",");
                                sql.append(i_produto.idComprador + ",");
                                sql.append("null,");
                                sql.append("'" + i_produto.descricaoReduzida + "',");
                                sql.append(Utils.truncar(i_produto.pesoLiquido, 3) + ",");
                                sql.append("'" + Util.formatDataBanco(i_produto.dataCadastro) + "',");
                                sql.append(i_produto.validade + ",");
                                sql.append(Utils.truncar(i_produto.pesoBruto, 3) + ",");
                                sql.append(i_produto.tara + ",");
                                sql.append(i_produto.comprimentoEmbalagem + ",");
                                sql.append(i_produto.larguraEmbalagem + ",");
                                sql.append(i_produto.alturaEmbalagem + ",");
                                sql.append(i_produto.perda + ",");
                                sql.append(i_produto.margem + ",");
                                sql.append(i_produto.verificaCustoTabela + ",");
                                sql.append("'" + i_produto.descricaoGondola + "',");
                                sql.append((i_produto.dataAlteracao.isEmpty() ? null : "'" + Util.formatDataBanco(i_produto.dataAlteracao) + "'") + ",");
                                sql.append((i_produto.idProdutoVasilhame == -1 ? null : i_produto.idProdutoVasilhame) + ",");
                                sql.append((i_produto.ncm1 == -1 ? null : i_produto.ncm1) + ",");
                                sql.append((i_produto.ncm2 == -1 ? null : i_produto.ncm2) + ",");
                                sql.append((i_produto.ncm3 == -1 ? null : i_produto.ncm3) + ",");
                                sql.append((i_produto.excecao == -1 ? null : i_produto.excecao) + ",");
                                sql.append((i_produto.idTipoMercadoria == -1 ? null : i_produto.idTipoMercadoria) + ",");
                                sql.append(i_produto.fabricacaoPropria + ",");
                                sql.append(i_produto.sugestaoPedido + ",");
                                sql.append(i_produto.sugestaoCotacao + ",");
                                sql.append(i_produto.aceitaMultiplicacaoPdv + ",");
                                sql.append(i_produto.idFornecedorFabricante + ",");
                                sql.append(i_produto.idDivisaoFornecedor + ",");
                                sql.append(i_produto.idTipoProduto + ",");
                                sql.append(i_produto.idTipoPisCofinsDebito + ",");
                                sql.append(i_produto.idTipoPisCofinsCredito + ",");
                                sql.append(i_produto.custoFinal + ",");
                                sql.append(i_produto.percentualIpi + ",");
                                sql.append(i_produto.percentualFrete + ",");
                                sql.append(i_produto.percentualEncargo + ",");
                                sql.append(i_produto.percentualPerda + ",");
                                sql.append(i_produto.percentualSubstituicao + ",");
                                sql.append(i_produto.pesavel + ",");
                                sql.append(i_produto.sazonal + ",");
                                sql.append(i_produto.consignado + ",");
                                sql.append(i_produto.ddv + ",");
                                sql.append(i_produto.permiteTroca + ",");
                                sql.append(i_produto.temperatura + ",");
                                sql.append(i_produto.idTipoOrigemMercadoria + ",");
                                sql.append(i_produto.ipi + ",");
                                sql.append(i_produto.vendaControlada + ",");
                                sql.append((i_produto.tipoNaturezaReceita == -1 ? null : i_produto.tipoNaturezaReceita) + ",");
                                sql.append(i_produto.vendaPdv + ",");
                                sql.append(i_produto.permiteQuebra + ",");
                                sql.append(i_produto.permitePerda + ",");
                                sql.append(i_produto.impostoMedioImportado + ",");
                                sql.append(i_produto.impostoMedioNacional + ",");
                                sql.append(i_produto.impostoMedioEstadual + ",");
                                sql.append(i_produto.utilizaTabelaSubstituicaoTributaria + ",");
                                sql.append(i_produto.utilizaValidadeEntrada + ",");
                                sql.append(i_produto.idTipoLocalTroca + ",");
                                sql.append(i_produto.idTipoCompra + ",");
                                sql.append("'" + i_produto.codigoAnp + "',");
                                sql.append(i_produto.numeroParcela + ",");
                                sql.append(i_produto.qtdDiasMinimoValidade + ",");

                                if (i_produto.getCest1() != -1
                                        && i_produto.getCest2() != -1
                                        && i_produto.getCest3() != -1) {
                                    CestVO cest = cestDAO.getCestValido(i_produto.getCest1(), i_produto.getCest2(), i_produto.getCest3());
                                    sql.append(Utils.longIntSQL(cest.getId(), 0) + ",");
                                } else {
                                    sql.append("null,");
                                }

                                sql.append(Utils.longIntSQL(i_produto.getIdNormaReposicao(), -1) + ");");

                                try {
                                    stm.execute(sql.toString());
                                } catch (Exception ex) {
                                    JOptionPane.showMessageDialog(null, "Erro " + IdProduto + " - " + sql.toString() + ex);
                                    throw ex;
                                }

                                for (ProdutoComplementoVO oComplemento : i_produto.vComplemento) {
                                    for (LojaVO oLoja : vLoja) {
                                        sql = new StringBuilder();
                                        sql.append("INSERT INTO produtocomplemento (id_produto, prateleira, secao,estoqueminimo,");
                                        sql.append(" estoquemaximo,valoripi,dataultimopreco,dataultimaentrada,");
                                        sql.append(" custosemimposto,custocomimposto,custosemimpostoanterior,");
                                        sql.append(" custocomimpostoanterior,precovenda,precovendaanterior,");
                                        sql.append(" precodiaseguinte,estoque,troca,emiteetiqueta,custosemperdasemimposto,");
                                        sql.append(" custosemperdasemimpostoanterior,customediocomimposto,customediosemimposto,");
                                        sql.append(" id_aliquotacredito,dataultimavenda,teclaassociada,id_situacaocadastro,");
                                        sql.append(" id_loja,descontinuado,quantidadeultimaentrada,centralizado,operacional,valoricmssubstituicao,");
                                        sql.append(" dataultimaentradaanterior,cestabasica,customediocomimpostoanterior,customediosemimpostoanterior,id_tipopiscofinscredito,valoroutrassubstituicao)");

                                        sql.append(" VALUES (");
                                        sql.append(IdProduto + ",");
                                        sql.append("'" + oComplemento.prateleira + "',");
                                        sql.append("'" + oComplemento.secao + "',");
                                        sql.append(oComplemento.estoqueMinimo + ",");
                                        sql.append(oComplemento.estoqueMaximo + ",");
                                        sql.append(oComplemento.valorIpi + ",");
                                        sql.append((oComplemento.dataUltimoPreco.isEmpty() ? null : "'" + Util.formatDataBanco(oComplemento.dataUltimoPreco) + "'") + ",");
                                        sql.append((oComplemento.dataUltimaEntrada.isEmpty() ? null : "'" + Util.formatDataBanco(oComplemento.dataUltimaEntrada) + "'") + ",");
                                        if (IdProduto == 72365) {
                                            Util.exibirMensagem(oComplemento.custoComImposto + ", " + oComplemento.custoSemImposto, "");
                                            Util.exibirMensagem(Utils.arredondar(oComplemento.custoComImposto, 3) + ", " + Utils.arredondar(oComplemento.custoSemImposto, 3), "");
                                        }
                                        sql.append(oComplemento.custoSemImposto + ",");
                                        sql.append(oComplemento.custoComImposto + ",");
                                        sql.append(oComplemento.custoSemImpostoAnterior + ",");
                                        sql.append(oComplemento.custoComImpostoAnterior + ",");
                                        sql.append(oComplemento.precoVenda + ",");
                                        sql.append(oComplemento.precoVendaAnterior + ",");
                                        sql.append(oComplemento.precoDiaSeguinte + ",");
                                        sql.append(oComplemento.estoque + ",");
                                        sql.append(oComplemento.troca + ",");
                                        sql.append(oComplemento.emiteEtiqueta + ",");
                                        sql.append(oComplemento.custoSemPerdaSemImposto + ",");
                                        sql.append(oComplemento.custoSemPerdaSemImpostoAnterior + ",");
                                        sql.append(oComplemento.custoMedioComImposto + ",");
                                        sql.append(oComplemento.custoMedioSemImposto + ",");
                                        sql.append(oComplemento.idAliquotaCredito + ",");
                                        sql.append((oComplemento.dataUltimaVenda.isEmpty() ? null : "'" + Util.formatDataBanco(oComplemento.dataUltimaVenda) + "'") + ",");
                                        sql.append(oComplemento.teclaAssociada + ",");
                                        sql.append(oComplemento.idSituacaoCadastro + ",");
                                        sql.append(oLoja.id + ",");
                                        sql.append(oComplemento.descontinuado + ",");
                                        sql.append(oComplemento.quantidadeUltimaEntrada + ",");
                                        sql.append(oComplemento.centralizado + ",");
                                        sql.append(oComplemento.operacional + ",");
                                        sql.append(oComplemento.valorIcmsSubstituicao + ",");
                                        sql.append((oComplemento.dataUltimaEntradaAnterior.isEmpty() ? null : "'" + Util.formatDataBanco(oComplemento.dataUltimaEntradaAnterior) + "'") + ",");
                                        sql.append(oComplemento.cestaBasica + ",");
                                        sql.append(oComplemento.custoMedioComImpostoAnterior + ",");
                                        sql.append(oComplemento.custoMedioSemImpostoAnterior + ",");
                                        sql.append(oComplemento.idTipoPisCofinsCredito + ",");
                                        sql.append(oComplemento.valorOutrasSubstituicao + ");");
                                        stm.execute(sql.toString());
                                    }
                                }
                                for (ProdutoAutomacaoVO oAutomacao : i_produto.vAutomacao) {
                                    if (String.valueOf(oAutomacao.codigoBarras).length() > 14) {
                                        oAutomacao.codigoBarras = Long.parseLong(String.valueOf(oAutomacao.codigoBarras).substring(0, 14));
                                    }

                                    if (oAutomacao.codigoBarras > -2) {

                                        sql = new StringBuilder();
                                        sql.append("SELECT id_produto FROM produtoautomacao");
                                        sql.append(" WHERE cast(codigobarras as numeric(14,0)) = cast(" + oAutomacao.codigoBarras + " as numeric(14,0)) ");
                                        rst = stm.executeQuery(sql.toString());

                                        if (!rst.next()) {
                                            sql = new StringBuilder();
                                            sql.append("INSERT INTO produtoautomacao (id_produto, codigobarras, qtdembalagem, id_tipoembalagem)");
                                            sql.append("VALUES (");
                                            sql.append(IdProduto + ",");
                                            sql.append(oAutomacao.codigoBarras + ",");
                                            sql.append(oAutomacao.qtdEmbalagem + ",");
                                            sql.append(oAutomacao.idTipoEmbalagem);
                                            sql.append(")");

                                            stm.execute(sql.toString());
                                        }
                                    }
                                }

                                for (ProdutoAliquotaVO oAliquota : i_produto.vAliquota) {
                                    sql = new StringBuilder();

                                    sql.append("INSERT INTO produtoaliquota (id_produto, id_estado, id_aliquotadebito, id_aliquotacredito, id_aliquotadebitoforaestado,");
                                    sql.append(" id_aliquotacreditoforaestado, id_aliquotadebitoforaestadonf, id_aliquotaconsumidor)");
                                    sql.append(" VALUES(");

                                    sql.append(IdProduto + ",");
                                    sql.append(oAliquota.idEstado + ",");
                                    sql.append(oAliquota.idAliquotaDebito + ",");
                                    sql.append(oAliquota.idAliquotaCredito + ",");
                                    sql.append(oAliquota.idAliquotaDebitoForaEstado + ",");
                                    sql.append(oAliquota.idAliquotaCreditoForaEstado + ",");
                                    sql.append(oAliquota.idAliquotaDebitoForaEstadoNF + ",");
                                    sql.append(oAliquota.getIdAliquotaConsumidor());

                                    sql.append(")");

                                    stm.execute(sql.toString());

                                }

                                for (CodigoAnteriorVO oCodigoInterno : i_produto.vCodigoAnterior) {
                                    oCodigoInterno.codigoatual = IdProduto;

                                    if (String.valueOf(oCodigoInterno.barras).length() > 14) {
                                        oCodigoInterno.barras = Long.parseLong(String.valueOf(oCodigoInterno.barras).substring(0, 14));
                                    }

                                    sql = new StringBuilder();
                                    sql.append("INSERT INTO implantacao.codigoanterior( ");
                                    sql.append("codigoanterior, codigoatual, barras, naturezareceita, ");
                                    sql.append("piscofinscredito, piscofinsdebito, ref_icmsdebito, estoque, e_balanca, ");
                                    sql.append("codigobalanca, custosemimposto, custocomimposto, margem, precovenda, referencia, ncm, id_loja, produto_novo, codigoauxiliar) ");
                                    sql.append("VALUES ( ");
                                    sql.append(oCodigoInterno.codigoanterior + ", ");
                                    sql.append(IdProduto + ", ");
                                    sql.append((oCodigoInterno.barras == -1 ? null : oCodigoInterno.barras) + ", ");
                                    sql.append((oCodigoInterno.naturezareceita == -1 ? null : oCodigoInterno.naturezareceita) + ", ");
                                    sql.append((oCodigoInterno.piscofinscredito == -1 ? null : oCodigoInterno.piscofinscredito) + ", ");
                                    sql.append((oCodigoInterno.piscofinsdebito == -1 ? null : oCodigoInterno.piscofinsdebito) + ", ");
                                    sql.append((oCodigoInterno.ref_icmsdebito.isEmpty() ? null : "'" + oCodigoInterno.ref_icmsdebito + "'") + ", ");
                                    sql.append((oCodigoInterno.estoque == -1 ? null : oCodigoInterno.estoque) + ", ");
                                    sql.append(oCodigoInterno.e_balanca + ", ");
                                    sql.append((oCodigoInterno.codigobalanca == -1 ? null : oCodigoInterno.codigobalanca) + ", ");
                                    sql.append((oCodigoInterno.custosemimposto == -1 ? null : oCodigoInterno.custosemimposto) + ", ");
                                    sql.append((oCodigoInterno.custocomimposto == -1 ? null : oCodigoInterno.custocomimposto) + ", ");
                                    sql.append((oCodigoInterno.margem == -1 ? null : oCodigoInterno.margem) + ", ");
                                    sql.append((oCodigoInterno.precovenda == -1 ? null : oCodigoInterno.precovenda) + ", ");
                                    sql.append((oCodigoInterno.referencia == -1 ? null : oCodigoInterno.referencia) + ", ");
                                    sql.append((oCodigoInterno.ncm.isEmpty() ? null : "'" + oCodigoInterno.ncm + "'") + ", ");
                                    sql.append(i_idLojaDestino + ", true, ");
                                    sql.append(Utils.quoteSQL(oCodigoInterno.getCodigoAuxiliar()));

                                    sql.append(");");

                                    stm.execute(sql.toString());
                                }

                            }

                        } else {
                            for (ProdutoAutomacaoVO oAutomacao : i_produto.vAutomacao) {
                                if (String.valueOf(oAutomacao.codigoBarras).length() > 14) {
                                    oAutomacao.codigoBarras = Long.parseLong(String.valueOf(oAutomacao.codigoBarras).substring(0, 14));
                                }

                                if (oAutomacao.codigoBarras > -2) {
                                    sql = new StringBuilder();
                                    sql.append("SELECT id_produto FROM produtoautomacao");
                                    sql.append(" WHERE cast(codigobarras as numeric(14,0)) = cast(" + oAutomacao.codigoBarras + " as numeric(14,0)) ");
                                    rst = stm.executeQuery(sql.toString());

                                    if (!rst.next()) {
                                        sql = new StringBuilder();
                                        sql.append("INSERT INTO produtoautomacao (id_produto, codigobarras, qtdembalagem, id_tipoembalagem)");
                                        sql.append("VALUES (");
                                        sql.append(rst4.getInt("id") + ",");
                                        sql.append(oAutomacao.codigoBarras + ",");
                                        sql.append(oAutomacao.qtdEmbalagem + ",");
                                        sql.append(oAutomacao.idTipoEmbalagem);
                                        sql.append(");");
                                        stm.execute(sql.toString());
                                    }
                                }
                            }
                        }
                    }
                } else {

                    if (String.valueOf(i_produto.codigoBarras).length() >= 7) {

                        sql = new StringBuilder();
                        sql.append("select * from produtoautomacao ");
                        sql.append("where codigobarras = " + i_produto.codigoBarras + " ");

                        rst = stm.executeQuery(sql.toString());

                        if (rst.next()) {
                            Inserir = false;
                        }

                        if (Inserir) {
                            sql = new StringBuilder();

                            sql.append("INSERT INTO produto (id, descricaocompleta, qtdembalagem, id_tipoembalagem, mercadologico1, mercadologico2, mercadologico3,");
                            sql.append(" mercadologico4, mercadologico5, id_comprador, id_familiaproduto, descricaoreduzida, pesoliquido, datacadastro,");
                            sql.append(" validade, pesobruto, tara, comprimentoembalagem, larguraembalagem, alturaembalagem, perda, margem, verificacustotabela,");
                            sql.append(" descricaogondola, dataalteracao, id_produtovasilhame, ncm1, ncm2, ncm3, excecao, id_tipomercadoria, fabricacaopropria,");
                            sql.append(" sugestaopedido, sugestaocotacao, aceitamultiplicacaopdv, id_fornecedorfabricante, id_divisaofornecedor, id_tipoproduto, id_tipopiscofins,");
                            sql.append(" id_tipopiscofinscredito, custofinal, percentualipi, percentualfrete, percentualencargo, percentualperda, percentualsubstituicao, pesavel,");
                            sql.append(" sazonal, consignado, ddv, permitetroca, temperatura, id_tipoorigemmercadoria, ipi, vendacontrolada, tiponaturezareceita,");
                            sql.append(" vendapdv, permitequebra, permiteperda, impostomedioimportado, impostomedionacional, impostomedioestadual, utilizatabelasubstituicaotributaria,");
                            sql.append(" utilizavalidadeentrada, id_tipolocaltroca, id_tipocompra, codigoanp, numeroparcela, qtddiasminimovalidade, id_cest, id_normareposicao)");

                            sql.append(" VALUES ");
                            sql.append(" (");

                            if (!implantacaoExterna) {

                                if (i_produto.id > 0) {
                                    IdProduto = i_produto.id;
                                } else {
                                    IdProduto = i_produto.idDouble;
                                }

                                if ((IdProduto <= 0) || (IdProduto >= 1000000)) {

                                    if (i_produto.eBalanca) {
                                        IdProduto = new CodigoInternoDAO().getIdProdutoBalanca();
                                    } else {
                                        IdProduto = new CodigoInternoDAO().getIdProduto(qtdeCodigoInterno);
                                    }
                                }
                            } else {

                                if (i_produto.eBalanca) {
                                    if (!gerarCodigo) {
                                        if ((IdProduto >= 1000000) || (IdProduto <= 0)) {
                                            IdProduto = new CodigoInternoDAO().getIdProdutoBalanca();
                                        }
                                    } else {
                                        IdProduto = new CodigoInternoDAO().getIdProdutoBalanca();
                                    }
                                } else {
                                    if (!gerarCodigo) {
                                        if ((IdProduto >= 1000000) || (IdProduto <= 0)) {
                                            IdProduto = new CodigoInternoDAO().getIdProduto(qtdeCodigoInterno);
                                        }
                                    } else {
                                        IdProduto = new CodigoInternoDAO().getIdProduto(qtdeCodigoInterno);
                                    }
                                }
                            }

                            if ("".equals(i_produto.dataCadastro.trim())) {
                                i_produto.dataCadastro = new DataProcessamentoDAO().get();
                            }

                            if (implantacaoExterna) {

                                sql2 = new StringBuilder();
                                sql2.append("select id from produto ");
                                sql2.append("where id = " + IdProduto);

                                rst3 = stm3.executeQuery(sql2.toString());

                                if (rst3.next()) {
                                    if (i_produto.eBalanca) {
                                        IdProduto = new CodigoInternoDAO().getIdProdutoBalanca();
                                    } else {
                                        IdProduto = new CodigoInternoDAO().getIdProduto(qtdeCodigoInterno);
                                    }
                                }
                            }

                            sql.append(IdProduto + ",");
                            sql.append("'" + i_produto.descricaoCompleta + "',");
                            sql.append(i_produto.qtdEmbalagem + ",");
                            sql.append(i_produto.idTipoEmbalagem + ",");

                            MercadologicoVO mercadologico
                                    = mercadologicoAnteriorDao.makeMercadologicoIntegracao(
                                            i_produto.mercadologico1,
                                            i_produto.mercadologico2,
                                            i_produto.mercadologico3,
                                            i_produto.mercadologico4,
                                            i_produto.mercadologico5);

                            if (mercadologico.mercadologico1 == 0
                                    || mercadologico.mercadologico2 == 0
                                    || mercadologico.mercadologico3 == 0) {
                                mercadologico = MercadologicoDAO.getMaxMercadologico();
                            }

                            sql.append(mercadologico.mercadologico1 + ",");
                            sql.append(mercadologico.mercadologico2 + ",");
                            sql.append(mercadologico.mercadologico3 + ",");
                            sql.append(mercadologico.mercadologico4 + ",");
                            sql.append(mercadologico.mercadologico5 + ",");
                            sql.append(i_produto.idComprador + ",");
                            sql.append("null,");
                            sql.append("'" + i_produto.descricaoReduzida + "',");
                            sql.append(Utils.truncar(i_produto.pesoLiquido, 3) + ",");
                            sql.append("'" + Util.formatDataBanco(i_produto.dataCadastro) + "',");
                            sql.append(i_produto.validade + ",");
                            sql.append(Utils.truncar(i_produto.pesoBruto, 3) + ",");
                            sql.append(i_produto.tara + ",");
                            sql.append(i_produto.comprimentoEmbalagem + ",");
                            sql.append(i_produto.larguraEmbalagem + ",");
                            sql.append(i_produto.alturaEmbalagem + ",");
                            sql.append(i_produto.perda + ",");
                            sql.append(i_produto.margem + ",");
                            sql.append(i_produto.verificaCustoTabela + ",");
                            sql.append("'" + i_produto.descricaoGondola + "',");
                            sql.append((i_produto.dataAlteracao.isEmpty() ? null : "'" + Util.formatDataBanco(i_produto.dataAlteracao) + "'") + ",");
                            sql.append((i_produto.idProdutoVasilhame == -1 ? null : i_produto.idProdutoVasilhame) + ",");
                            sql.append((i_produto.ncm1 == -1 ? null : i_produto.ncm1) + ",");
                            sql.append((i_produto.ncm2 == -1 ? null : i_produto.ncm2) + ",");
                            sql.append((i_produto.ncm3 == -1 ? null : i_produto.ncm3) + ",");
                            sql.append((i_produto.excecao == -1 ? null : i_produto.excecao) + ",");
                            sql.append((i_produto.idTipoMercadoria == -1 ? null : i_produto.idTipoMercadoria) + ",");
                            sql.append(i_produto.fabricacaoPropria + ",");
                            sql.append(i_produto.sugestaoPedido + ",");
                            sql.append(i_produto.sugestaoCotacao + ",");
                            sql.append(i_produto.aceitaMultiplicacaoPdv + ",");
                            sql.append(i_produto.idFornecedorFabricante + ",");
                            sql.append(i_produto.idDivisaoFornecedor + ",");
                            sql.append(i_produto.idTipoProduto + ",");
                            sql.append(i_produto.idTipoPisCofinsDebito + ",");
                            sql.append(i_produto.idTipoPisCofinsCredito + ",");
                            sql.append(i_produto.custoFinal + ",");
                            sql.append(i_produto.percentualIpi + ",");
                            sql.append(i_produto.percentualFrete + ",");
                            sql.append(i_produto.percentualEncargo + ",");
                            sql.append(i_produto.percentualPerda + ",");
                            sql.append(i_produto.percentualSubstituicao + ",");
                            sql.append(i_produto.pesavel + ",");
                            sql.append(i_produto.sazonal + ",");
                            sql.append(i_produto.consignado + ",");
                            sql.append(i_produto.ddv + ",");
                            sql.append(i_produto.permiteTroca + ",");
                            sql.append(i_produto.temperatura + ",");
                            sql.append(i_produto.idTipoOrigemMercadoria + ",");
                            sql.append(i_produto.ipi + ",");
                            sql.append(i_produto.vendaControlada + ",");
                            sql.append((i_produto.tipoNaturezaReceita == -1 ? null : i_produto.tipoNaturezaReceita) + ",");
                            sql.append(i_produto.vendaPdv + ",");
                            sql.append(i_produto.permiteQuebra + ",");
                            sql.append(i_produto.permitePerda + ",");
                            sql.append(i_produto.impostoMedioImportado + ",");
                            sql.append(i_produto.impostoMedioNacional + ",");
                            sql.append(i_produto.impostoMedioEstadual + ",");
                            sql.append(i_produto.utilizaTabelaSubstituicaoTributaria + ",");
                            sql.append(i_produto.utilizaValidadeEntrada + ",");
                            sql.append(i_produto.idTipoLocalTroca + ",");
                            sql.append(i_produto.idTipoCompra + ",");
                            sql.append("'" + i_produto.codigoAnp + "',");
                            sql.append(i_produto.numeroParcela + ",");
                            sql.append(i_produto.qtdDiasMinimoValidade + ",");

                            if (i_produto.getCest1() != -1
                                    && i_produto.getCest2() != -1
                                    && i_produto.getCest3() != -1) {
                                CestVO cest = cestDAO.getCestValido(i_produto.getCest1(), i_produto.getCest2(), i_produto.getCest3());
                                sql.append(Utils.longIntSQL(cest.getId(), 0) + ",");
                            } else {
                                sql.append("null,");
                            }

                            sql.append(Utils.longIntSQL(i_produto.getIdNormaReposicao(), -1) + ");");

                            try {
                                stm.execute(sql.toString());
                            } catch (Exception ex) {
                                JOptionPane.showMessageDialog(null, "Erro " + IdProduto + " - " + sql.toString() + ex);
                                throw ex;
                            }

                            for (ProdutoComplementoVO oComplemento : i_produto.vComplemento) {
                                for (LojaVO oLoja : vLoja) {
                                    sql = new StringBuilder();
                                    sql.append("INSERT INTO produtocomplemento (id_produto, prateleira, secao,estoqueminimo,");
                                    sql.append(" estoquemaximo,valoripi,dataultimopreco,dataultimaentrada,");
                                    sql.append(" custosemimposto,custocomimposto,custosemimpostoanterior,");
                                    sql.append(" custocomimpostoanterior,precovenda,precovendaanterior,");
                                    sql.append(" precodiaseguinte,estoque,troca,emiteetiqueta,custosemperdasemimposto,");
                                    sql.append(" custosemperdasemimpostoanterior,customediocomimposto,customediosemimposto,");
                                    sql.append(" id_aliquotacredito,dataultimavenda,teclaassociada,id_situacaocadastro,");
                                    sql.append(" id_loja,descontinuado,quantidadeultimaentrada,centralizado,operacional,valoricmssubstituicao,");
                                    sql.append(" dataultimaentradaanterior,cestabasica,customediocomimpostoanterior,customediosemimpostoanterior,id_tipopiscofinscredito,valoroutrassubstituicao)");

                                    sql.append(" VALUES (");
                                    sql.append(IdProduto + ",");
                                    sql.append("'" + oComplemento.prateleira + "',");
                                    sql.append("'" + oComplemento.secao + "',");
                                    sql.append(oComplemento.estoqueMinimo + ",");
                                    sql.append(oComplemento.estoqueMaximo + ",");
                                    sql.append(oComplemento.valorIpi + ",");
                                    sql.append((oComplemento.dataUltimoPreco.isEmpty() ? null : "'" + Util.formatDataBanco(oComplemento.dataUltimoPreco) + "'") + ",");
                                    sql.append((oComplemento.dataUltimaEntrada.isEmpty() ? null : "'" + Util.formatDataBanco(oComplemento.dataUltimaEntrada) + "'") + ",");
                                    if (IdProduto == 72365) {
                                        Util.exibirMensagem(oComplemento.custoComImposto + ", " + oComplemento.custoSemImposto, "");
                                        Util.exibirMensagem(Utils.arredondar(oComplemento.custoComImposto, 3) + ", " + Utils.arredondar(oComplemento.custoSemImposto, 3), "");
                                    }
                                    sql.append(oComplemento.custoSemImposto + ",");
                                    sql.append(oComplemento.custoComImposto + ",");
                                    sql.append(oComplemento.custoSemImpostoAnterior + ",");
                                    sql.append(oComplemento.custoComImpostoAnterior + ",");
                                    sql.append(oComplemento.precoVenda + ",");
                                    sql.append(oComplemento.precoVendaAnterior + ",");
                                    sql.append(oComplemento.precoDiaSeguinte + ",");
                                    sql.append(oComplemento.estoque + ",");
                                    sql.append(oComplemento.troca + ",");
                                    sql.append(oComplemento.emiteEtiqueta + ",");
                                    sql.append(oComplemento.custoSemPerdaSemImposto + ",");
                                    sql.append(oComplemento.custoSemPerdaSemImpostoAnterior + ",");
                                    sql.append(oComplemento.custoMedioComImposto + ",");
                                    sql.append(oComplemento.custoMedioSemImposto + ",");
                                    sql.append(oComplemento.idAliquotaCredito + ",");
                                    sql.append((oComplemento.dataUltimaVenda.isEmpty() ? null : "'" + Util.formatDataBanco(oComplemento.dataUltimaVenda) + "'") + ",");
                                    sql.append(oComplemento.teclaAssociada + ",");
                                    sql.append(oComplemento.idSituacaoCadastro + ",");
                                    sql.append(oLoja.id + ",");
                                    sql.append(oComplemento.descontinuado + ",");
                                    sql.append(oComplemento.quantidadeUltimaEntrada + ",");
                                    sql.append(oComplemento.centralizado + ",");
                                    sql.append(oComplemento.operacional + ",");
                                    sql.append(oComplemento.valorIcmsSubstituicao + ",");
                                    sql.append((oComplemento.dataUltimaEntradaAnterior.isEmpty() ? null : "'" + Util.formatDataBanco(oComplemento.dataUltimaEntradaAnterior) + "'") + ",");
                                    sql.append(oComplemento.cestaBasica + ",");
                                    sql.append(oComplemento.custoMedioComImpostoAnterior + ",");
                                    sql.append(oComplemento.custoMedioSemImpostoAnterior + ",");
                                    sql.append(oComplemento.idTipoPisCofinsCredito + ",");
                                    sql.append(oComplemento.valorOutrasSubstituicao + ");");
                                    stm.execute(sql.toString());
                                }
                            }
                            for (ProdutoAutomacaoVO oAutomacao : i_produto.vAutomacao) {
                                if (oAutomacao.codigoBarras == -1) {
                                    if ((IdProduto <= 9999)
                                            && (i_produto.idTipoEmbalagem != 4)) {
                                        oAutomacao.codigoBarras = util.gerarEan13((int) IdProduto, false);
                                    } else if ((IdProduto > 9999)
                                            && (i_produto.idTipoEmbalagem != 4)
                                            && (!i_produto.pesavel)) {
                                        oAutomacao.codigoBarras = util.gerarEan13((int) IdProduto, true);
                                    } else if ((IdProduto > 9999)
                                            && (i_produto.idTipoEmbalagem != 4)
                                            && (i_produto.pesavel)) {
                                        oAutomacao.codigoBarras = util.gerarEan13((int) IdProduto, false);
                                    } else if ((i_produto.idTipoEmbalagem != 4)
                                            && (i_produto.pesavel)) {
                                        oAutomacao.codigoBarras = util.gerarEan13((int) IdProduto, false);
                                    } else if (i_produto.idTipoEmbalagem == 4) {
                                        oAutomacao.codigoBarras = util.gerarEan13((int) IdProduto, false);
                                    } else {
                                        oAutomacao.codigoBarras = util.gerarEan13((int) IdProduto, true);
                                    }
                                }

                                if (String.valueOf(oAutomacao.codigoBarras).length() > 14) {
                                    oAutomacao.codigoBarras = Long.parseLong(String.valueOf(oAutomacao.codigoBarras).substring(0, 14));
                                }

                                if (oAutomacao.codigoBarras > -2) {

                                    sql = new StringBuilder();
                                    sql.append("SELECT id_produto FROM produtoautomacao");
                                    sql.append(" WHERE cast(codigobarras as numeric(14,0)) = cast(" + oAutomacao.codigoBarras + " as numeric(14,0)) ");
                                    rst = stm.executeQuery(sql.toString());

                                    if (!rst.next()) {
                                        sql = new StringBuilder();
                                        sql.append("INSERT INTO produtoautomacao (id_produto, codigobarras, qtdembalagem, id_tipoembalagem)");
                                        sql.append("VALUES (");
                                        sql.append(IdProduto + ",");
                                        sql.append(oAutomacao.codigoBarras + ",");
                                        sql.append(oAutomacao.qtdEmbalagem + ",");
                                        sql.append(oAutomacao.idTipoEmbalagem);
                                        sql.append(")");

                                        stm.execute(sql.toString());
                                    }
                                }
                            }

                            for (ProdutoAliquotaVO oAliquota : i_produto.vAliquota) {
                                sql = new StringBuilder();

                                sql.append("INSERT INTO produtoaliquota (id_produto, id_estado, id_aliquotadebito, id_aliquotacredito, id_aliquotadebitoforaestado,");
                                sql.append(" id_aliquotacreditoforaestado, id_aliquotadebitoforaestadonf, id_aliquotaconsumidor)");
                                sql.append(" VALUES(");

                                sql.append(IdProduto + ",");
                                sql.append(oAliquota.idEstado + ",");
                                sql.append(oAliquota.idAliquotaDebito + ",");
                                sql.append(oAliquota.idAliquotaCredito + ",");
                                sql.append(oAliquota.idAliquotaDebitoForaEstado + ",");
                                sql.append(oAliquota.idAliquotaCreditoForaEstado + ",");
                                sql.append(oAliquota.idAliquotaDebitoForaEstadoNF + ",");
                                sql.append(oAliquota.getIdAliquotaConsumidor());

                                sql.append(")");

                                stm.execute(sql.toString());

                            }

                            for (CodigoAnteriorVO oCodigoInterno : i_produto.vCodigoAnterior) {
                                oCodigoInterno.codigoatual = IdProduto;

                                if (String.valueOf(oCodigoInterno.barras).length() > 14) {
                                    oCodigoInterno.barras = Long.parseLong(String.valueOf(oCodigoInterno.barras).substring(0, 14));
                                }

                                sql = new StringBuilder();
                                sql.append("INSERT INTO implantacao.codigoanterior( ");
                                sql.append("codigoanterior, codigoatual, barras, naturezareceita, ");
                                sql.append("piscofinscredito, piscofinsdebito, ref_icmsdebito, estoque, e_balanca, ");
                                sql.append("codigobalanca, custosemimposto, custocomimposto, margem, precovenda, referencia, ncm, id_loja, produto_novo, codigoauxiliar) ");
                                sql.append("VALUES ( ");
                                sql.append(oCodigoInterno.codigoanterior + ", ");
                                sql.append(IdProduto + ", ");
                                sql.append((oCodigoInterno.barras == -1 ? null : oCodigoInterno.barras) + ", ");
                                sql.append((oCodigoInterno.naturezareceita == -1 ? null : oCodigoInterno.naturezareceita) + ", ");
                                sql.append((oCodigoInterno.piscofinscredito == -1 ? null : oCodigoInterno.piscofinscredito) + ", ");
                                sql.append((oCodigoInterno.piscofinsdebito == -1 ? null : oCodigoInterno.piscofinsdebito) + ", ");
                                sql.append((oCodigoInterno.ref_icmsdebito.isEmpty() ? null : "'" + oCodigoInterno.ref_icmsdebito + "'") + ", ");
                                sql.append((oCodigoInterno.estoque == -1 ? null : oCodigoInterno.estoque) + ", ");
                                sql.append(oCodigoInterno.e_balanca + ", ");
                                sql.append((oCodigoInterno.codigobalanca == -1 ? null : oCodigoInterno.codigobalanca) + ", ");
                                sql.append((oCodigoInterno.custosemimposto == -1 ? null : oCodigoInterno.custosemimposto) + ", ");
                                sql.append((oCodigoInterno.custocomimposto == -1 ? null : oCodigoInterno.custocomimposto) + ", ");
                                sql.append((oCodigoInterno.margem == -1 ? null : oCodigoInterno.margem) + ", ");
                                sql.append((oCodigoInterno.precovenda == -1 ? null : oCodigoInterno.precovenda) + ", ");
                                sql.append((oCodigoInterno.referencia == -1 ? null : oCodigoInterno.referencia) + ", ");
                                sql.append((oCodigoInterno.ncm.isEmpty() ? null : "'" + oCodigoInterno.ncm + "'") + ", ");
                                sql.append(i_idLojaDestino + ", true, ");
                                sql.append(Utils.quoteSQL(oCodigoInterno.getCodigoAuxiliar()));

                                sql.append(");");

                                stm.execute(sql.toString());
                            }
                        } else {

                            for (ProdutoAutomacaoVO oAutomacao : i_produto.vAutomacao) {
                                if ((oAutomacao.codigoBarras != -1)
                                        && (oAutomacao.codigoBarras != -2)) {

                                    /*if (IdProduto > 9999) {
                                     oAutomacao.codigoBarras = util.gerarEan13(i_produto.id, true);
                                     } else {
                                     oAutomacao.codigoBarras = util.gerarEan13(i_produto.id, false);
                                     }*/
                                    if (String.valueOf(oAutomacao.codigoBarras).length() > 14) {
                                        oAutomacao.codigoBarras = Long.parseLong(String.valueOf(oAutomacao.codigoBarras).substring(0, 14));
                                    }

                                    sql = new StringBuilder();
                                    sql.append("SELECT id_produto FROM produtoautomacao");
                                    sql.append(" WHERE cast(codigobarras as numeric(14,0)) = cast(" + oAutomacao.codigoBarras + " as numeric(14,0)) ");

                                    rst = stm.executeQuery(sql.toString());

                                    if (!rst.next()) {
                                        sql = new StringBuilder();
                                        sql.append("INSERT INTO produtoautomacao (id_produto, codigobarras, qtdembalagem, id_tipoembalagem)");
                                        sql.append("VALUES (");
                                        sql.append(IdProduto + ",");
                                        sql.append(oAutomacao.codigoBarras + ",");
                                        sql.append(oAutomacao.qtdEmbalagem + ",");
                                        sql.append(oAutomacao.idTipoEmbalagem);

                                        sql.append(")");

                                        stm.execute(sql.toString());

                                        for (CodigoAnteriorVO oCodigoInterno : i_produto.vCodigoAnterior) {
                                            oCodigoInterno.codigoatual = IdProduto;

                                            if (String.valueOf(oCodigoInterno.barras).length() > 14) {
                                                oCodigoInterno.barras = Long.parseLong(String.valueOf(oCodigoInterno.barras).substring(0, 14));
                                            }

                                            sql = new StringBuilder();
                                            sql.append("select * from implantacao.codigoanterior ");
                                            sql.append("where codigoanterior = " + oCodigoInterno.codigoanterior + " ");
                                            sql.append("and barras = " + oCodigoInterno.barras);

                                            rst2 = stm.executeQuery(sql.toString());

                                            if (!rst2.next()) {

                                                sql = new StringBuilder();
                                                sql.append("INSERT INTO implantacao.codigoanterior( ");
                                                sql.append("codigoanterior, codigoatual, barras, naturezareceita, ");
                                                sql.append("piscofinscredito, piscofinsdebito, ref_icmsdebito, estoque, e_balanca, ");
                                                sql.append("codigobalanca, custosemimposto, custocomimposto, margem, precovenda, referencia, ncm, id_loja, produto_novo) ");
                                                sql.append("VALUES ( ");
                                                sql.append(oCodigoInterno.codigoanterior + ", ");
                                                sql.append(IdProduto + ", ");
                                                sql.append((oCodigoInterno.barras < 0 ? null : oCodigoInterno.barras) + ", ");
                                                sql.append((oCodigoInterno.naturezareceita == -1 ? null : oCodigoInterno.naturezareceita) + ", ");
                                                sql.append((oCodigoInterno.piscofinscredito == -1 ? null : oCodigoInterno.piscofinscredito) + ", ");
                                                sql.append((oCodigoInterno.piscofinsdebito == -1 ? null : oCodigoInterno.piscofinsdebito) + ", ");
                                                sql.append((oCodigoInterno.ref_icmsdebito.isEmpty() ? null : "'" + oCodigoInterno.ref_icmsdebito + "'") + ", ");
                                                sql.append((oCodigoInterno.estoque == -1 ? null : oCodigoInterno.estoque) + ", ");
                                                sql.append(oCodigoInterno.e_balanca + ", ");
                                                sql.append((oCodigoInterno.codigobalanca == -1 ? null : oCodigoInterno.codigobalanca) + ", ");
                                                sql.append((oCodigoInterno.custosemimposto == -1 ? null : oCodigoInterno.custosemimposto) + ", ");
                                                sql.append((oCodigoInterno.custocomimposto == -1 ? null : oCodigoInterno.custocomimposto) + ", ");
                                                sql.append((oCodigoInterno.margem == -1 ? null : oCodigoInterno.margem) + ", ");
                                                sql.append((oCodigoInterno.precovenda == -1 ? null : oCodigoInterno.precovenda) + ", ");
                                                sql.append((oCodigoInterno.referencia == -1 ? null : oCodigoInterno.referencia) + ", ");
                                                sql.append("'" + (oCodigoInterno.ncm.isEmpty() ? null : oCodigoInterno.ncm) + "', ");
                                                sql.append(i_idLojaDestino + ", true ");

                                                sql.append(");");
                                            }
                                            stm.execute(sql.toString());
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                ProgressBar.next();
            }

            sql = new StringBuilder();
            sql.append("update produtoaliquota set id_aliquotaconsumidor = id_aliquotadebito; "
                    + "update produtoaliquota aaa set id_aliquotaconsumidor = aliq.id_aliquotaconsumidor "
                    + "from\n"
                    + "	(select\n"
                    + "		pa.*,\n"
                    + "		al_con.descricao\n"
                    + "	from\n"
                    + "		(select \n"
                    + "			pa.id,\n"
                    + "			pa.id_produto,\n"
                    + "			pa.id_estado,\n"
                    + "			pa.id_aliquotadebito,\n"
                    + "			al_deb.descricao,\n"
                    + "			(select id from aliquota where situacaotributaria != 20 and reduzido = 0 and porcentagem = al_deb.porcentagemfinal limit 1) id_aliquotaconsumidor\n"
                    + "		from \n"
                    + "			produtoaliquota pa\n"
                    + "			left join aliquota al_deb on pa.id_aliquotadebito = al_deb.id\n"
                    + "		where\n"
                    + "			al_deb.situacaotributaria = 20) pa\n"
                    + "		left join aliquota al_con on pa.id_aliquotaconsumidor = al_con.id) aliq\n"
                    + "where\n"
                    + "	aaa.id = aliq.id;");
            stm.execute(sql.toString());

            stm.close();
            stm2.close();
            stm3.close();
            stm4.close();
            Conexao.commit();

        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void verificarProdutoIntegracao(List<ProdutoVO> produtos, int i_idLojaDestino, List<LojaVO> vLoja,
            String pathLog) throws Exception {
        Statement stm = null, stm2 = null, stm3 = null;
        ResultSet rst = null;
        StringBuilder sql = null;
        File f = new File(pathLog + "\\produtos.txt");
        FileWriter fw = new FileWriter(f);
        BufferedWriter bw = new BufferedWriter(fw);

        try {
            Conexao.begin();

            stm = Conexao.createStatement();
            stm2 = Conexao.createStatement();
            stm3 = Conexao.createStatement();

            //<editor-fold defaultstate="collapsed" desc="ORDENANDO A LISTA DE INCLUSAO">
            List<ProdutoVO> menorQue6dig = new ArrayList<>();
            List<ProdutoVO> maiorQue6dig = new ArrayList<>();
            for (ProdutoVO produto : produtos) {
                double id;
                if (produto.idDouble > 0) {
                    id = produto.idDouble;
                } else {
                    id = produto.id;
                }

                if (id <= 999999) {
                    menorQue6dig.add(produto);
                } else {
                    maiorQue6dig.add(produto);
                }
            }

            List<ProdutoVO> v_produto = new ArrayList<>(menorQue6dig);
            v_produto.addAll(maiorQue6dig);

            menorQue6dig = null;
            maiorQue6dig = null;
            //</editor-fold>
            ProgressBar.setMaximum(v_produto.size());

            ProgressBar.setStatus("Verificando novos produtos Integração...Loja " + i_idLojaDestino + "...");
            for (ProdutoVO i_produto : v_produto) {
                Inserir = true;

                if (String.valueOf(i_produto.codigoBarras).length() >= 7) {

                    sql = new StringBuilder();
                    sql.append("select * from produtoautomacao ");
                    sql.append("where codigobarras = " + i_produto.codigoBarras + " ");

                    rst = stm.executeQuery(sql.toString());

                    if (rst.next()) {
                        Inserir = false;
                    }

                    if (Inserir) {
                        bw.write("CODIGO PRODUTO: " + i_produto.idDouble
                                + "CODIGO BARRAS: " + i_produto.codigoBarras + " "
                                + "DESCRICAO: " + i_produto.descricaoCompleta + ";");
                        bw.newLine();
                    }
                }
                ProgressBar.next();
            }

            bw.flush();
            bw.close();
            stm.close();
            stm2.close();
            stm3.close();
            Conexao.commit();

        } catch (Exception ex) {
            Conexao.rollback();
            JOptionPane.showMessageDialog(null, "Produto=" + IdProduto + " Erro=" + ex + "\n" + sql.toString());
            throw ex;
        } finally {
            //Conexao.destruir(null, stm, rst);
        }
    }

    public void alterarDescricaoProduto(Collection<ProdutoVO> values) throws Exception {
        Conexao.begin();
        try {
            //Localizo os códigos anteriores
            Map<Double, CodigoAnteriorVO> anteriores = new CodigoAnteriorDAO().carregarCodigoAnterior();

            ProgressBar.setMaximum(values.size());
            for (ProdutoVO produto : values) {
                CodigoAnteriorVO anterior = anteriores.get(produto.getId());

                if (anterior != null) {

                    String descricaoCompleta = Utils.quoteSQL(produto.getDescricaoCompleta());
                    String descricaoReduzida = Utils.quoteSQL(produto.getDescricaoReduzida());
                    String descricaoGondola = Utils.quoteSQL(produto.getDescricaoGondola());

                    Conexao.createStatement().execute(
                            "update produto set descricaocompleta = " + descricaoCompleta
                            + " , descricaoreduzida = " + descricaoReduzida
                            + " , descricaogondola = " + descricaoGondola
                            + " where id = " + (int) anterior.getCodigoatual()
                    );
                }

                ProgressBar.next();
            }

            Conexao.commit();
        } catch (Exception e) {
            Conexao.rollback();
            throw e;
        }
    }

    public void alterarQtdEmbalagem(List<ProdutoVO> v_produto) throws Exception {
        Statement stm = null;

        try {
            Conexao.begin();

            stm = Conexao.createStatement();

            ProgressBar.setMaximum(v_produto.size());

            ProgressBar.setStatus("Atualizando dados produto...Tipo Embalagem...");

            Map<Long, Long> idPorEan = carregarCodigoBarras();
            for (ProdutoVO i_produto : v_produto) {
                Long barras = i_produto.getvAutomacao().get(0).getCodigoBarras();
                if (barras > 0) {
                    if (idPorEan.containsKey(barras)) {
                        String sql = "update produtoautomacao set \n"
                                + "qtdembalagem = " + i_produto.getvAutomacao().get(0).getQtdEmbalagem() + "\n"
                                + "where codigobarras = " + barras + ";";

                        stm.execute(sql);
                    }
                }

                ProgressBar.next();
            }

            stm.close();
            Conexao.commit();

        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void alterarTipoEmbalagem(List<ProdutoVO> v_produto) throws Exception {
        Statement stm = null;

        try {
            Conexao.begin();

            stm = Conexao.createStatement();

            ProgressBar.setMaximum(v_produto.size());

            ProgressBar.setStatus("Atualizando dados produto...Tipo Embalagem...");

            Map<Double, CodigoAnteriorVO> anteriores = new CodigoAnteriorDAO().carregarCodigoAnterior();
            for (ProdutoVO i_produto : v_produto) {

                CodigoAnteriorVO anterior = anteriores.get(i_produto.getId());
                if (anterior != null) {
                    boolean pesavel;
                    if (i_produto.getIdTipoEmbalagem() == 4) {
                        pesavel = false;
                    } else {
                        pesavel = i_produto.isPesavel();
                    }

                    String sql = "update produto set\n"
                            + "id_tipoembalagem = " + i_produto.idTipoEmbalagem + ",\n"
                            + "pesavel = " + pesavel + "\n"
                            + "where id = " + anterior.getCodigoatual() + ";\n";

                    sql += "update produtoautomacao set \n"
                            + "id_tipoembalagem = " + i_produto.idTipoEmbalagem + "\n"
                            + "where id_produto = " + anterior.getCodigoatual() + ";";

                    stm.execute(sql);
                }

                ProgressBar.next();
            }

            stm.close();
            Conexao.commit();

        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void alterarPrecoProduto(List<ProdutoVO> v_produto, int id_loja) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;
        double IdProduto;
        //File f = new File("C:/vr/Implantacao/scripts/update_preco.txt");
        //FileWriter fw = new FileWriter(f);
        //BufferedWriter bw = new BufferedWriter(fw);

        try {

            Conexao.begin();

            stm = Conexao.createStatement();

            ProgressBar.setMaximum(v_produto.size());

            ProgressBar.setStatus("Atualizando dados produto...Preço...");

            Map<Double, CodigoAnteriorVO> anteriores = new CodigoAnteriorDAO().carregarCodigoAnterior();

            for (ProdutoVO i_produto : v_produto) {
                for (ProdutoComplementoVO oComplemento : i_produto.vComplemento) {
                    /*sql = new StringBuilder();
                     sql.append("select p.id from produto p ");
                     sql.append("inner join implantacao.codigoanterior ant on ant.codigoatual = p.id ");
                     if (i_produto.idDouble > 0) {
                     IdProduto = i_produto.idDouble;
                     sql.append("where CAST(ant.codigoanterior AS NUMERIC(14,0)) = CAST(" + IdProduto + " AS NUMERIC(14,0)) ");
                     } else {
                     IdProduto = i_produto.id;
                     sql.append("where ant.codigoanterior = " + IdProduto);
                     }

                     rst = stm.executeQuery(sql.toString());

                     if (rst.next()) {*/

                    if (i_produto.idDouble > 0) {
                        IdProduto = i_produto.idDouble;
                    } else {
                        IdProduto = i_produto.id;
                    }

                    CodigoAnteriorVO codigoAnterior = anteriores.get(IdProduto);
                    if (codigoAnterior != null) {

                        sql = new StringBuilder();
                        sql.append("select * from oferta ");
                        sql.append("where id_produto = " + (int) codigoAnterior.getCodigoatual() + " ");
                        sql.append("and id_situacaooferta = 1 ");
                        sql.append("and id_loja = " + id_loja);

                        rst = stm.executeQuery(sql.toString());

                        if (!rst.next()) {

                            sql = new StringBuilder();
                            sql.append("UPDATE produtocomplemento SET ");
                            sql.append("precovenda = " + oComplemento.precoVenda + ", ");
                            sql.append("precodiaseguinte = " + oComplemento.precoDiaSeguinte + " ");
                            sql.append("where id_produto = " + (int) codigoAnterior.getCodigoatual() + " ");
                            sql.append("and id_loja = " + id_loja + " ");
                            sql.append("and dataultimopreco is null; ");

                            sql.append(" UPDATE implantacao.codigoanterior SET ");
                            sql.append(" precovenda = " + oComplemento.precoVenda + " ");
                            sql.append(" WHERE codigoatual = " + (int) codigoAnterior.getCodigoatual() + " ");
                            sql.append(" AND id_loja = " + id_loja + "; ");

                            stm.execute(sql.toString());
                            //bw.write(sql.toString());
                            //bw.newLine();

                        }
                    }
                }

                ProgressBar.next();
            }

            //bw.flush();
            //bw.close();
            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void alterarMargemProduto(List<ProdutoVO> v_produto, int id_loja) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;
        double IdProduto;

        try {

            Conexao.begin();

            stm = Conexao.createStatement();

            ProgressBar.setMaximum(v_produto.size());

            ProgressBar.setStatus("Atualizando dados produto...Margem...");

            Map<Double, CodigoAnteriorVO> anteriores = new CodigoAnteriorDAO().carregarCodigoAnterior();

            for (ProdutoVO i_produto : v_produto) {
                /*sql = new StringBuilder();
                 sql.append("select p.id from produto p ");
                 sql.append("inner join implantacao.codigoanterior ant on ant.codigoatual = p.id ");
                 if (i_produto.idDouble > 0) {
                 IdProduto = i_produto.idDouble;
                 sql.append("where CAST(ant.codigoanterior AS NUMERIC(14,0)) = CAST(" + IdProduto + " AS NUMERIC(14,0)) ");
                 } else {
                 IdProduto = i_produto.id;
                 sql.append("where ant.codigoanterior = " + IdProduto);
                 }

                 rst = stm.executeQuery(sql.toString());

                 if (rst.next()) {*/

                if (i_produto.idDouble > 0) {
                    IdProduto = i_produto.idDouble;
                } else {
                    IdProduto = i_produto.id;
                }

                CodigoAnteriorVO codigoAnterior = anteriores.get(IdProduto);
                if (codigoAnterior != null) {
                    sql = new StringBuilder();

                    sql.append(" UPDATE produto SET ");
                    sql.append(" margem = round(" + i_produto.margem + ",2) ");
                    sql.append(" WHERE id = " + (int) codigoAnterior.getCodigoatual() + " "
                            + "and margem = 0;");

                    stm.execute(sql.toString());
                }

                ProgressBar.next();
            }

            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void alterarPrecoProdutoConcretize(List<ProdutoVO> v_produto, int id_loja) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;
        double IdProduto;

        try {

            Conexao.begin();

            stm = Conexao.createStatement();

            ProgressBar.setMaximum(v_produto.size());

            ProgressBar.setStatus("Atualizando dados produto...Preço...");

            for (ProdutoVO i_produto : v_produto) {
                /*if (i_produto.id == 120114) {
                 Util.exibirMensagem(i_produto.vComplemento.isEmpty() ? "VAZIO" : "COM VALOR: " + i_produto.vComplemento.get(0).precoVenda,"");
                 }*/
                for (ProdutoComplementoVO oComplemento : i_produto.vComplemento) {

                    IdProduto = i_produto.id;

                    sql = new StringBuilder();
                    sql.append("UPDATE produtocomplemento SET ");
                    sql.append("precovenda = " + oComplemento.precoVenda + ", ");
                    sql.append("precodiaseguinte = " + oComplemento.precoDiaSeguinte + " ");
                    sql.append("where id_produto = " + IdProduto + " ");
                    sql.append("and id_loja = " + id_loja + "; ");

                    /*if (i_produto.margem>0){
                     sql.append(" UPDATE produto SET ");
                     sql.append(" margem = round(" + i_produto.margem + ",2) ");
                     sql.append(" WHERE id = " + IdProduto + "; ");
                     }*/
                    sql.append(" UPDATE implantacao.codigoanterior SET ");
                    sql.append(" precovenda = " + oComplemento.precoVenda + " ");
                    sql.append(" WHERE codigoatual = " + IdProduto + " ");
                    sql.append(" AND id_loja = " + id_loja + "; ");

                    stm.execute(sql.toString());

                    /*if (IdProduto == 120114) {
                     Util.exibirMensagem(sql.toString(),"");
                     }*/
                }

                ProgressBar.next();
            }

            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void alterarNaturezaReceita(List<ProdutoVO> v_produto) throws Exception {
        StringBuilder sql = null;

        try {
            Conexao.begin();
            try (Statement stm = Conexao.createStatement()) {
                ProgressBar.setMaximum(v_produto.size());
                ProgressBar.setStatus("Atualizando dados produto...PisCofins e Natureza Receita...");
                Map<Double, CodigoAnteriorVO> anteriores = new CodigoAnteriorDAO().carregarCodigoAnterior();
                for (ProdutoVO i_produto : v_produto) {
                    /*sql = new StringBuilder();
                     sql.append("SELECT p.id FROM produto p ");
                     sql.append("INNER JOIN implantacao.codigoanterior ant ");
                     sql.append("ON ant.codigoatual = p.id ");
                     if (i_produto.idDouble > 0) {
                     IdProduto = i_produto.idDouble;
                     sql.append("where CAST(ant.codigoanterior AS NUMERIC(14,0)) = CAST(" + IdProduto + " AS NUMERIC(14,0)) ");
                     } else {
                     IdProduto = i_produto.id;
                     sql.append("where ant.codigoanterior = " + IdProduto);
                     }

                     rst = stm.executeQuery(sql.toString());

                     if (rst.next()) {*/
                    double IdProduto = 0;
                    if (i_produto.idDouble > 0) {
                        IdProduto = i_produto.idDouble;
                    } else {
                        IdProduto = i_produto.id;
                    }

                    CodigoAnteriorVO anterior = anteriores.get(IdProduto);
                    if (anterior != null) {
                        sql = new StringBuilder();
                        sql.append("update produto set ");
                        sql.append("tiponaturezareceita = " + i_produto.tipoNaturezaReceita + " ");
                        sql.append("where id = " + anterior.getCodigoatual() + ";");

                        sql.append("UPDATE implantacao.codigoanterior SET ");
                        sql.append("naturezareceita = " + i_produto.tipoNaturezaReceita + " ");
                        sql.append("WHERE codigoatual = " + anterior.getCodigoatual() + ";");
                        stm.execute(sql.toString());
                    }
                    ProgressBar.next();
                }
            }

            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void alterarPrecoProdutoRapido(List<ProdutoVO> v_produto, int id_loja) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql, sql2 = null;
        double IdProduto;
        int x = 0, cont = 0;

        try {

            Conexao.begin();

            stm = Conexao.createStatement();

            ProgressBar.setMaximum(v_produto.size());

            ProgressBar.setStatus("Atualizando dados produto...Preço...");
            sql2 = new StringBuilder();

            Map<String, CodigoAnteriorVO> anteriores = new CodigoAnteriorDAO().carregarCodigoAnteriorV2();

            //Zera o estoque
            //stm.execute("update produtocomplemento set precovenda = 0, precodiaseguinte = 0 where id_loja = " + id_loja);
            //stm.execute("update implantacao.codigoanterior set precovenda = 0");
            for (ProdutoVO i_produto : v_produto) {
                for (ProdutoComplementoVO oComplemento : i_produto.vComplemento) {

                    String strAnterior;
                    CodigoAnteriorVO get = i_produto.getvCodigoAnterior().get(0);
                    if (get != null && get.getCodigoAnteriorStr() != null && "".equals(get.getCodigoAnteriorStr().trim())) {
                        strAnterior = get.getCodigoAnteriorStr();
                    } else if (i_produto.getIdDouble() > 0) {
                        strAnterior = String.valueOf((long) i_produto.getIdDouble());
                    } else {
                        strAnterior = String.valueOf((int) i_produto.getId());
                    }

                    CodigoAnteriorVO vCodigoAnterior = anteriores.get(strAnterior);

                    if (vCodigoAnterior != null) {
                        sql = new StringBuilder();
                        sql.append("select * from oferta ");
                        sql.append("where id_produto = " + (int) vCodigoAnterior.getCodigoatual() + " ");
                        sql.append("and id_situacaooferta = 1 ");
                        sql.append("and id_loja = " + id_loja);

                        rst = stm.executeQuery(sql.toString());

                        if (!rst.next()) {
                            sql2.append("UPDATE produtocomplemento SET ");
                            sql2.append("precovenda = " + oComplemento.precoVenda + ", ");
                            sql2.append("precodiaseguinte = " + oComplemento.precoDiaSeguinte + " ");
                            sql2.append("where id_produto = " + (int) vCodigoAnterior.getCodigoatual() + " ");
                            sql2.append("and id_loja = " + id_loja + "; ");

                            sql2.append(" UPDATE implantacao.codigoanterior SET ");
                            sql2.append(" precovenda = " + oComplemento.precoVenda + " ");
                            sql2.append(" WHERE codigoatual = " + (int) vCodigoAnterior.getCodigoatual() + " ");
                            sql2.append(" AND id_loja = " + id_loja + "; ");

                            x++;
                        }
                    }
                }

                cont++;
                if (x == QTD_PARA_EXECUTAR_SQL || cont >= v_produto.size()) {
                    try {
                        stm.execute(sql2.toString());
                        sql2 = new StringBuilder();
                        x = 0;
                    } catch (Exception ex) {
                        throw new VRException("Produto: " + ex.getMessage() + " - " + sql2.toString());
                    }
                }
                ProgressBar.next();
            }

            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void alterarCustoProduto(List<ProdutoVO> v_produto, int id_loja) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;
        double IdProduto;
        try {

            Conexao.begin();

            stm = Conexao.createStatement();

            ProgressBar.setMaximum(v_produto.size());

            ProgressBar.setStatus("Atualizando dados produto...Custo...");
            Map<Double, CodigoAnteriorVO> anteriores = new CodigoAnteriorDAO().carregarCodigoAnterior();
            for (ProdutoVO i_produto : v_produto) {
                for (ProdutoComplementoVO oComplemento : i_produto.vComplemento) {
                    if (i_produto.idDouble > 0) {
                        IdProduto = i_produto.idDouble;
                    } else {
                        IdProduto = i_produto.id;
                    }

                    CodigoAnteriorVO codigoAnterior = anteriores.get(IdProduto);
                    if (codigoAnterior != null) {

                        sql = new StringBuilder();
                        sql.append("UPDATE produtocomplemento SET ");
                        sql.append("custosemimposto = " + oComplemento.custoSemImposto + ", ");
                        sql.append("custocomimposto = " + oComplemento.custoComImposto + " ");
                        sql.append("where id_produto = " + (int) codigoAnterior.getCodigoatual() + " ");
                        sql.append("and id_loja = " + id_loja + " ");
                        sql.append("and dataultimaentrada is null; ");

                        sql.append("UPDATE implantacao.codigoanterior SET ");
                        sql.append("custosemimposto = " + oComplemento.custoSemImposto + ", ");
                        sql.append("custocomimposto = " + oComplemento.custoComImposto + " ");
                        sql.append("WHERE codigoatual = " + (int) codigoAnterior.getCodigoatual() + " ");
                        sql.append("AND id_loja = " + id_loja + ";");

                        stm.execute(sql.toString());
                    }
                }

                ProgressBar.next();
            }

            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void alterarIdFabricanteProduto(List<ProdutoVO> v_produto) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;
        double IdProduto;
        try {
            Conexao.begin();
            stm = Conexao.createStatement();
            ProgressBar.setMaximum(v_produto.size());
            ProgressBar.setStatus("Atualizando Id Fornecedor Fabricante...");
            Map<Double, CodigoAnteriorVO> anteriores = new CodigoAnteriorDAO().carregarCodigoAnterior();
            for (ProdutoVO i_produto : v_produto) {

                if (i_produto.idDouble > 0) {
                    IdProduto = i_produto.idDouble;
                } else {
                    IdProduto = i_produto.id;
                }

                CodigoAnteriorVO codigoAnterior = anteriores.get(IdProduto);
                if (codigoAnterior != null) {

                    sql = new StringBuilder();
                    sql.append("UPDATE produto SET ");
                    sql.append("id_fornecedorfabricante = " + i_produto.getIdFornecedorFabricante() + " "
                            + "where id = " + (int) codigoAnterior.getCodigoatual());
                    stm.execute(sql.toString());
                }
                ProgressBar.next();
            }
            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void alterarCustoPorEAN(Collection<ProdutoVO> values, int idLojaVR) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;
        double IdProduto;
        int x = 0, cont = 0;
        try {

            Conexao.begin();

            stm = Conexao.createStatement();

            Map<Long, ProdutoVO> v_produto = new LinkedHashMap<>();
            for (ProdutoVO produto : values) {
                long codigobarras;

                if (produto.getvAutomacao() != null && produto.getvAutomacao().size() == 1) {
                    codigobarras = produto.getvAutomacao().get(0).getCodigoBarras();
                } else {
                    codigobarras = produto.getCodigoBarras();
                }
                if (String.valueOf(codigobarras).length() <= 14 && String.valueOf(codigobarras).length() >= 7) {
                    v_produto.put(codigobarras, produto);
                }
            }

            ProgressBar.setMaximum(v_produto.size());

            ProgressBar.setStatus("Atualizando dados produto...Custo...");
            sql = new StringBuilder();

            //Zera os custos
            //stm.execute("update produtocomplemento set custosemimposto = 0, custocomimposto = 0 where id_loja = " + id_loja + " and dataultimaentrada is null");
            //stm.execute("update implantacao.codigoanterior set custosemimposto = 0, custocomimposto = 0");
            Map<Long, Long> idPorEan = carregarCodigoBarras();
            for (ProdutoVO i_produto : v_produto.values()) {
                for (ProdutoComplementoVO oComplemento : i_produto.vComplemento) {

                    long codigobarras;

                    if (i_produto.getvAutomacao() != null && i_produto.getvAutomacao().size() == 1) {
                        codigobarras = i_produto.getvAutomacao().get(0).getCodigoBarras();
                    } else {
                        codigobarras = i_produto.getCodigoBarras();
                    }

                    Long idAtualProduto = idPorEan.get(codigobarras);

                    if (idAtualProduto != null && idAtualProduto != 0) {
                        sql.append("UPDATE produtocomplemento SET ");
                        sql.append("custosemimposto = " + oComplemento.custoSemImposto + ", ");
                        sql.append("custocomimposto = " + oComplemento.custoComImposto + " ");
                        sql.append("where id_produto = " + idAtualProduto + " ");
                        sql.append("and id_loja = " + idLojaVR + " ");
                        sql.append("and dataultimaentrada is null; ");

                        sql.append("UPDATE implantacao.codigoanterior SET ");
                        sql.append("custosemimposto = " + oComplemento.custoSemImposto + ", ");
                        sql.append("custocomimposto = " + oComplemento.custoComImposto + " ");
                        sql.append("WHERE codigoatual = " + idAtualProduto + " ");
                        sql.append("AND id_loja = " + idLojaVR + ";");
                        x++;
                    }
                }
                cont++;
                if (x == QTD_PARA_EXECUTAR_SQL || cont >= v_produto.size()) {
                    stm.execute(sql.toString());
                    sql = new StringBuilder();
                    x = 0;
                }
                ProgressBar.next();
            }

            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void alterarCustoProdutoRapido(List<ProdutoVO> v_produto, int id_loja) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql, sql2 = null;
        double IdProduto;
        int x = 0, cont = 0;
        try {

            Conexao.begin();

            stm = Conexao.createStatement();

            ProgressBar.setMaximum(v_produto.size());

            ProgressBar.setStatus("Atualizando dados produto...Custo...");
            sql2 = new StringBuilder();

            //Zera os custos
            //stm.execute("update produtocomplemento set custosemimposto = 0, custocomimposto = 0 where id_loja = " + id_loja + " and dataultimaentrada is null");
            //stm.execute("update implantacao.codigoanterior set custosemimposto = 0, custocomimposto = 0");
            Map<String, CodigoAnteriorVO> anteriores = new CodigoAnteriorDAO().carregarCodigoAnteriorV2();
            for (ProdutoVO i_produto : v_produto) {
                for (ProdutoComplementoVO oComplemento : i_produto.vComplemento) {

                    String strAnterior;
                    CodigoAnteriorVO get = i_produto.getvCodigoAnterior().get(0);
                    if (get != null && get.getCodigoAnteriorStr() != null && "".equals(get.getCodigoAnteriorStr().trim())) {
                        strAnterior = get.getCodigoAnteriorStr();
                    } else if (i_produto.getIdDouble() > 0) {
                        strAnterior = String.valueOf((long) i_produto.getIdDouble());
                    } else {
                        strAnterior = String.valueOf((int) i_produto.getId());
                    }

                    CodigoAnteriorVO vCodigoAnterior = anteriores.get(strAnterior);

                    if (vCodigoAnterior != null) {
                        sql2.append("UPDATE produtocomplemento SET ");
                        sql2.append("custosemimposto = " + oComplemento.custoSemImposto + ", ");
                        sql2.append("custocomimposto = " + oComplemento.custoComImposto + " ");
                        sql2.append("where id_produto = " + (int) vCodigoAnterior.getCodigoatual() + " ");
                        sql2.append("and id_loja = " + id_loja + " ");
                        sql2.append("and dataultimaentrada is null; ");

                        sql2.append("UPDATE implantacao.codigoanterior SET ");
                        sql2.append("custosemimposto = " + oComplemento.custoSemImposto + ", ");
                        sql2.append("custocomimposto = " + oComplemento.custoComImposto + " ");
                        sql2.append("WHERE codigoatual = " + (int) vCodigoAnterior.getCodigoatual() + " ");
                        sql2.append("AND id_loja = " + id_loja + ";");
                        x++;
                    }
                }
                cont++;
                if (x == QTD_PARA_EXECUTAR_SQL || cont >= v_produto.size()) {
                    stm.execute(sql2.toString());
                    sql2 = new StringBuilder();
                    x = 0;
                }
                ProgressBar.next();
            }

            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void alterarEstoqueProduto(List<ProdutoVO> v_produto, int id_loja) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;
        Map<Double, CodigoAnteriorVO> codigoAnterior = new CodigoAnteriorDAO().carregarCodigoAnterior();
        //File f = new File("C:/vr/Implantacao/scripts/update_estoque"+id_loja+".txt");
        //FileWriter fw = new FileWriter(f);
        //BufferedWriter bw = new BufferedWriter(fw);

        try {
            Conexao.begin();
            stm = Conexao.createStatement();
            ProgressBar.setMaximum(v_produto.size());
            ProgressBar.setStatus("Atualizando dados produto...Estoque...");
            for (ProdutoVO i_produto : v_produto) {
                for (ProdutoComplementoVO oComplemento : i_produto.vComplemento) {

                    if (i_produto.idDouble > 0) {
                        IdProduto = i_produto.idDouble;
                    } else {
                        IdProduto = i_produto.id;
                    }

                    /*sql = new StringBuilder();
                     sql.append("select p.id from produto p ");
                     sql.append("inner join implantacao.codigoanterior ant on ant.codigoatual = p.id ");
                     if (i_produto.idDouble > 0) {
                     IdProduto = i_produto.idDouble;
                     sql.append("where CAST(ant.codigoanterior AS NUMERIC(14,0)) = CAST(" + IdProduto + " AS NUMERIC(14,0)) ");
                     } else {
                     IdProduto = i_produto.id;
                     sql.append("where ant.codigoanterior = " + IdProduto);
                     }

                     rst = stm.executeQuery(sql.toString());

                     if (rst.next()) {*/
                    CodigoAnteriorVO anterior = codigoAnterior.get(IdProduto);

                    if (anterior != null) {
                        sql = new StringBuilder();
                        sql.append("UPDATE produtocomplemento SET ");
                        sql.append("estoque = " + oComplemento.estoque + ", ");
                        sql.append("estoqueminimo = " + oComplemento.estoqueMinimo + ", ");
                        sql.append("estoquemaximo = " + oComplemento.estoqueMaximo + " ");
                        sql.append("where id_produto = " + ((int) anterior.getCodigoatual()) + " ");
                        sql.append("and id_loja = " + id_loja + "; ");

                        sql.append("UPDATE implantacao.codigoanterior SET ");
                        sql.append("estoque = " + oComplemento.estoque + " ");
                        sql.append("WHERE codigoatual = " + ((int) anterior.getCodigoatual()) + ";");
                        stm.execute(sql.toString());

                        //bw.write(sql.toString());
                        //bw.newLine();
                    }
                }

                ProgressBar.next();
            }

            //bw.flush();
            //bw.close();
            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void alterarEstoqueSomaProduto(List<ProdutoVO> v_produto, int id_loja) throws Exception {
        Statement stm = null;
        StringBuilder sql = null;
        Map<Double, CodigoAnteriorVO> codigoAnterior = new CodigoAnteriorDAO().carregarCodigoAnterior();

        try {
            Conexao.begin();
            stm = Conexao.createStatement();
            ProgressBar.setMaximum(v_produto.size());
            ProgressBar.setStatus("Atualizando dados produto...Estoque Soma...");
            for (ProdutoVO i_produto : v_produto) {
                for (ProdutoComplementoVO oComplemento : i_produto.vComplemento) {

                    if (i_produto.idDouble > 0) {
                        IdProduto = i_produto.idDouble;
                    } else {
                        IdProduto = i_produto.id;
                    }

                    CodigoAnteriorVO anterior = codigoAnterior.get(IdProduto);

                    if (anterior != null) {
                        sql = new StringBuilder();
                        sql.append("UPDATE produtocomplemento SET ");
                        sql.append("estoque = estoque + (" + oComplemento.estoque + ") ");
                        sql.append("where id_produto = " + ((int) anterior.getCodigoatual()) + " ");
                        sql.append("and id_loja = " + id_loja + "; ");
                        stm.execute(sql.toString());
                    }
                }

                ProgressBar.next();
            }
            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void alterarEstoqueSomaProdutoCodBarras(List<ProdutoVO> v_produto, int id_loja) throws Exception {
        Statement stm = null;
        StringBuilder sql = null;
        ResultSet rst = null;

        try {
            Conexao.begin();
            stm = Conexao.createStatement();
            ProgressBar.setMaximum(v_produto.size());
            ProgressBar.setStatus("Atualizando dados produto Estoque Soma Cod.Barras...");
            for (ProdutoVO i_produto : v_produto) {
                for (ProdutoComplementoVO oComplemento : i_produto.vComplemento) {
                    sql = new StringBuilder();
                    sql.append("select p.id from produto p "
                            + "inner join produtoautomacao pa on pa.id_produto = p.id "
                            + "where char_length(cast(pa.codigobarras as varchar)) >= 7 "
                            + "and pa.codigobarras = " + i_produto.codigoBarras);
                    rst = stm.executeQuery(sql.toString());

                    if (rst.next()) {
                        sql = new StringBuilder();
                        sql.append("UPDATE produtocomplemento SET ");
                        sql.append("estoque = estoque + (" + oComplemento.estoque + ") ");
                        sql.append("where id_produto = " + rst.getInt("id") + " ");
                        sql.append("and id_loja = " + id_loja + "; ");
                        stm.execute(sql.toString());
                    }
                }

                ProgressBar.next();
            }
            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void alterarEstoquePorEAN(List<ProdutoVO> produtos, int idLojaVR) throws Exception {
        int x = 0, cont = 0;
        try {

            Conexao.begin();

            Map<Long, ProdutoVO> v_produto = new LinkedHashMap<>();
            for (ProdutoVO produto : produtos) {
                long codigobarras;

                if (produto.getvAutomacao() != null && produto.getvAutomacao().size() == 1) {
                    codigobarras = produto.getvAutomacao().get(0).getCodigoBarras();
                } else {
                    codigobarras = produto.getCodigoBarras();
                }
                if (String.valueOf(codigobarras).length() <= 14 && String.valueOf(codigobarras).length() >= 7) {
                    v_produto.put(codigobarras, produto);
                }
            }

            ProgressBar.setMaximum(v_produto.size());

            ProgressBar.setStatus("Atualizando dados produto...Estoque...");
            StringBuilder sql = new StringBuilder();

            //Zera os custos
            //stm.execute("update produtocomplemento set custosemimposto = 0, custocomimposto = 0 where id_loja = " + id_loja + " and dataultimaentrada is null");
            //stm.execute("update implantacao.codigoanterior set custosemimposto = 0, custocomimposto = 0");
            try (Statement stm = Conexao.createStatement()) {
                Map<Long, Long> idPorEan = carregarCodigoBarras();
                for (ProdutoVO i_produto : v_produto.values()) {
                    for (ProdutoComplementoVO oComplemento : i_produto.vComplemento) {

                        long codigobarras;

                        if (i_produto.getvAutomacao() != null && i_produto.getvAutomacao().size() == 1) {
                            codigobarras = i_produto.getvAutomacao().get(0).getCodigoBarras();
                        } else {
                            codigobarras = i_produto.getCodigoBarras();
                        }

                        Long idAtualProduto = idPorEan.get(codigobarras);

                        if (idAtualProduto != null && idAtualProduto != 0) {
                            sql.append("UPDATE produtocomplemento SET ");
                            sql.append("estoque = " + oComplemento.estoque + " ");
                            sql.append("where id_produto = " + idAtualProduto + " ");
                            sql.append("and id_loja = " + idLojaVR + "; ");

                            sql.append("UPDATE implantacao.codigoanterior SET ");
                            sql.append("estoque = " + oComplemento.estoque + " ");
                            sql.append("WHERE codigoatual = " + idAtualProduto + " ");
                            sql.append("AND id_loja = " + idLojaVR + ";");
                            x++;
                        }
                    }
                    cont++;
                    if (x == QTD_PARA_EXECUTAR_SQL || cont >= v_produto.size()) {
                        stm.execute(sql.toString());
                        sql = new StringBuilder();
                        x = 0;
                    }
                    ProgressBar.next();
                }
            }
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void alterarEstoqueProdutoIntegrado(List<ProdutoVO> v_produto, int id_loja) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;

        Map<Double, ProdutosUnificacaoVO> codigoUnif = new ProdutoUnificacaoDAO().carregarUnificados();

        try {
            Conexao.begin();
            stm = Conexao.createStatement();
            ProgressBar.setMaximum(v_produto.size());
            ProgressBar.setStatus("Atualizando dados produto...Estoque Integrado...");
            for (ProdutoVO i_produto : v_produto) {
                for (ProdutoComplementoVO oComplemento : i_produto.vComplemento) {

                    if (i_produto.idDouble > 0) {
                        IdProduto = i_produto.idDouble;
                    } else {
                        IdProduto = i_produto.id;
                    }
                    //Localiza o produto unificado
                    ProdutosUnificacaoVO unif = codigoUnif.get(IdProduto);

                    if (unif != null) {
                        try (Statement stm2 = Conexao.createStatement()) {
                            try (ResultSet rst2 = stm2.executeQuery(
                                    "select \n"
                                    + "	pa.id_produto \n"
                                    + "from \n"
                                    + "	produtoautomacao pa\n"
                                    + "	join (select distinct codigoanterior, barras from implantacao.produtos_unificacao) pu on\n"
                                    + "		pa.codigobarras = pu.barras\n"
                                    + "where \n"
                                    + "	pa.codigobarras = " + unif.barras
                            )) {
                                //Se localizar o código de barras executa
                                if (rst2.next()) {
                                    sql = new StringBuilder();
                                    /*sql.append("UPDATE produtocomplemento SET ");
                                     sql.append("estoque = " + oComplemento.estoque + ", ");
                                     sql.append("estoqueminimo = " + oComplemento.estoqueMinimo + ", ");
                                     sql.append("estoquemaximo = " + oComplemento.estoqueMaximo + " ");
                                     sql.append("where id_produto = " + rst2.getInt("id_produto") + " ");
                                     sql.append("and id_loja = " + id_loja + " ");
                                     sql.append("and dataultimaentrada is null; ");*/

                                    sql.append("UPDATE produtocomplemento SET ");
                                    sql.append("estoque = estoque + " + oComplemento.estoque + " ");
                                    sql.append("where id_produto = " + rst2.getInt("id_produto") + " ");
                                    sql.append("and id_loja = " + id_loja + " ");
                                    sql.append("and dataultimaentrada is not null ");

                                    stm.execute(sql.toString());
                                }
                            }
                        }
                    }
                }

                ProgressBar.next();
            }
            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void alterarEstoqueProdutoSomando(List<ProdutoVO> v_produto, int idLojaVR) throws Exception {
        StringBuilder sql = null;

        try {

            Conexao.begin();

            ProgressBar.setMaximum(v_produto.size());

            ProgressBar.setStatus("Atualizando dados produto...Estoque...Somando...");

            Map<Double, CodigoAnteriorVO> anteriores = new CodigoAnteriorDAO().carregarCodigoAnterior();
            try (Statement stm = Conexao.createStatement()) {
                for (ProdutoVO i_produto : v_produto) {
                    for (ProdutoComplementoVO oComplemento : i_produto.vComplemento) {
                        double idProduto;
                        if (i_produto.getIdDouble() > 0) {
                            idProduto = i_produto.getIdDouble();
                        } else {
                            idProduto = i_produto.getId();
                        }
                        CodigoAnteriorVO anterior = anteriores.get(idProduto);

                        if (anterior != null) {
                            sql = new StringBuilder();
                            sql.append(" UPDATE produtocomplemento SET ");
                            sql.append(" estoque = estoque + (" + oComplemento.estoque + "), ");
                            sql.append(" estoqueminimo = " + oComplemento.estoqueMinimo + ", ");
                            sql.append(" estoquemaximo = " + oComplemento.estoqueMaximo + " ");
                            sql.append(" where id_produto = " + (int) anterior.getCodigoatual() + " ");
                            sql.append(" and id_loja = " + idLojaVR + ";");

                            stm.execute(sql.toString());
                        }
                    }

                    ProgressBar.next();
                }
            }

            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void alterarEstoqueProdutoRapido(List<ProdutoVO> v_produto, int id_loja) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql, sql2 = null;
        int x = 0, cont = 0;
        try {
            Conexao.begin();
            stm = Conexao.createStatement();
            ProgressBar.setMaximum(v_produto.size());
            ProgressBar.setStatus("Atualizando dados produto...Estoque...");
            sql2 = new StringBuilder();
            //Zera o estoque
            //stm.execute("update produtocomplemento set estoque = 0 where id_loja = " + id_loja);
            //stm.execute("update implantacao.codigoanterior set estoque = 0");
            Map<String, CodigoAnteriorVO> anteriores = new CodigoAnteriorDAO().carregarCodigoAnteriorV2();
            for (ProdutoVO i_produto : v_produto) {
                for (ProdutoComplementoVO oComplemento : i_produto.vComplemento) {

                    String strAnterior;
                    CodigoAnteriorVO get = i_produto.getvCodigoAnterior().get(0);
                    if (get != null && get.getCodigoAnteriorStr() != null && "".equals(get.getCodigoAnteriorStr().trim())) {
                        strAnterior = get.getCodigoAnteriorStr();
                    } else if (i_produto.getIdDouble() > 0) {
                        strAnterior = String.valueOf((long) i_produto.getIdDouble());
                    } else {
                        strAnterior = String.valueOf((int) i_produto.getId());
                    }

                    CodigoAnteriorVO vCodigoAnterior = anteriores.get(strAnterior);

                    if (vCodigoAnterior != null) {
                        sql2.append("UPDATE produtocomplemento SET ");
                        sql2.append("estoque = " + oComplemento.estoque + ", ");
                        sql2.append("estoqueminimo = " + oComplemento.estoqueMinimo + ", ");
                        sql2.append("estoquemaximo = " + oComplemento.estoqueMaximo + " ");
                        sql2.append("where id_produto = " + vCodigoAnterior.getCodigoatual() + " ");
                        sql2.append("and id_loja = " + id_loja + ";");

                        sql2.append("UPDATE implantacao.codigoanterior SET ");
                        sql2.append("estoque = " + oComplemento.estoque + " ");
                        sql2.append("WHERE codigoatual = " + vCodigoAnterior.getCodigoatual() + ";");
                        x++;
                    }
                }
                cont++;
                if (x == QTD_PARA_EXECUTAR_SQL || cont >= v_produto.size()) {
                    stm.execute(sql2.toString());
                    sql2 = new StringBuilder();
                    x = 0;
                }
                ProgressBar.next();
            }
            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void addCodigoBarras(List<ProdutoVO> v_produto) throws Exception {
        Statement stm = null, stm2 = null, stm3 = null;
        ResultSet rst = null, rst2 = null, rst3 = null;
        StringBuilder sql = null;

        try {
            Conexao.begin();
            stm = Conexao.createStatement();
            stm2 = Conexao.createStatement();
            stm3 = Conexao.createStatement();

            ProgressBar.setMaximum(v_produto.size());
            ProgressBar.setStatus("Atualizando dados produto...Codigo Barra...");

            for (ProdutoVO i_produto : v_produto) {
                for (ProdutoAutomacaoVO oAutomacao : i_produto.vAutomacao) {

                    if (String.valueOf(oAutomacao.getCodigoBarras()).length() >= 7
                            && String.valueOf(oAutomacao.getCodigoBarras()).length() <= 14) {

                        sql = new StringBuilder();
                        sql.append("select p.id, id_tipoembalagem, e_balanca from produto p ");
                        sql.append("inner join implantacao.codigoanterior ant on ant.codigoatual = p.id ");
                        if (i_produto.idDouble > 0) {
                            IdProduto = i_produto.idDouble;
                            sql.append("where ant.codigoanterior::varchar = '" + String.format("%.0f", IdProduto) + "'");
                        } else {
                            IdProduto = i_produto.id;
                            sql.append("where ant.codigoanterior = " + IdProduto + " ");
                        }

                        if (verificarLoja) {
                            sql.append("and id_loja = " + id_loja + " ");
                        }

                        rst = stm.executeQuery(sql.toString());

                        if (rst.next()) {

                            //if (!rst.getBoolean("e_balanca")) {
                            sql = new StringBuilder();
                            sql.append("select * from produtoautomacao ");
                            sql.append("where codigobarras = " + oAutomacao.codigoBarras);

                            rst2 = stm2.executeQuery(sql.toString());

                            if (!rst2.next()) {
                                int qtdEmbalagem = oAutomacao.qtdEmbalagem;
                                if (qtdEmbalagem <= 0) {
                                    qtdEmbalagem = 1;
                                }

                                sql = new StringBuilder();
                                sql.append("insert into produtoautomacao (");
                                sql.append("id_produto, codigobarras, qtdembalagem, id_tipoembalagem) ");
                                sql.append("values (");
                                sql.append(rst.getInt("id") + ",");
                                sql.append(oAutomacao.codigoBarras + ",");
                                sql.append(qtdEmbalagem + ",");
                                sql.append((oAutomacao.idTipoEmbalagem == -1 ? rst.getInt("id_tipoembalagem") : oAutomacao.idTipoEmbalagem) + ");");

                                if (alterarBarraAnterio) {
                                    sql.append("update implantacao.codigoanterior set "
                                            + "barras = " + (oAutomacao.codigoBarras > 0
                                                    ? Utils.quoteSQL(oAutomacao.codigoBarras + "")
                                                    : null) + " where codigoatual = " + rst.getInt("id") + ";");
                                }
                                stm.execute(sql.toString());
                            }
                            //}
                        }
                    }
                }

                if (automacaoLoja) {
                    for (ProdutoAutomacaoLojaVO oAutomacaoLoja : i_produto.vAutomacaoLoja) {
                        sql = new StringBuilder();
                        sql.append("select * from produtoautomacaoloja ");
                        sql.append("where codigobarras = " + oAutomacaoLoja.codigobarras);
                        rst3 = stm3.executeQuery(sql.toString());

                        if (rst3.next()) {
                            sql = new StringBuilder();
                            sql.append("update produtoautomacaoloja set ");
                            sql.append("precovenda = " + oAutomacaoLoja.precovenda + " ");
                            sql.append("where codigobarras = " + oAutomacaoLoja.codigobarras + " ");
                            sql.append("and id_loja = " + oAutomacaoLoja.id_loja);
                            stm3.execute(sql.toString());
                        } else {
                            sql = new StringBuilder();
                            sql.append("insert into produtoautomacaoloja (");
                            sql.append("codigobarras, precovenda, id_loja) ");
                            sql.append("values (");
                            sql.append(oAutomacaoLoja.codigobarras + ", ");
                            sql.append(oAutomacaoLoja.precovenda + ", ");
                            sql.append(oAutomacaoLoja.id_loja + ");");
                            stm3.execute(sql.toString());
                        }

                        sql = new StringBuilder();
                        sql.append("update produtoautomacao set qtdembalagem = " + oAutomacaoLoja.qtdEmbalagem + " ");
                        sql.append("where codigobarras = " + oAutomacaoLoja.codigobarras + ";");
                        stm3.execute(sql.toString());
                    }
                }
                ProgressBar.next();
            }
            stm.close();
            stm2.close();
            stm3.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void addCodigoBarrasUnificacao(List<ProdutoVO> v_produto) throws Exception {
        Statement stm = null, stm2 = null;
        ResultSet rst = null, rst2 = null;
        StringBuilder sql = null;

        try {
            Conexao.begin();
            stm = Conexao.createStatement();
            stm2 = Conexao.createStatement();

            ProgressBar.setMaximum(v_produto.size());
            ProgressBar.setStatus("Atualizando dados produto...Codigo Barra Unificaçao...");

            for (ProdutoVO i_produto : v_produto) {
                for (ProdutoAutomacaoVO oAutomacao : i_produto.vAutomacao) {

                    if (String.valueOf(oAutomacao.getCodigoBarras()).length() >= 7
                            && String.valueOf(oAutomacao.getCodigoBarras()).length() <= 14) {

                        sql = new StringBuilder();
                        sql.append("select * from produtoautomacao ");
                        sql.append("where codigobarras = " + oAutomacao.codigoBarras);
                        rst = stm.executeQuery(sql.toString());

                        if (!rst.next()) {
                            int qtdEmbalagem = oAutomacao.qtdEmbalagem;
                            if (qtdEmbalagem <= 0) {
                                qtdEmbalagem = 1;
                            }

                            sql = new StringBuilder();
                            sql.append("insert into produtoautomacao (");
                            sql.append("id_produto, codigobarras, qtdembalagem, id_tipoembalagem) ");
                            sql.append("values (");
                            sql.append(i_produto.id + ",");
                            sql.append(oAutomacao.codigoBarras + ",");
                            sql.append(qtdEmbalagem + ",");
                            sql.append(oAutomacao.idTipoEmbalagem + ");");
                            stm.execute(sql.toString());

                            if (automacaoLoja) {
                                for (ProdutoAutomacaoLojaVO oAutomacaoLoja : i_produto.vAutomacaoLoja) {
                                    sql = new StringBuilder();
                                    sql.append("select * from produtoautomacaoloja ");
                                    sql.append("where codigobarras = " + oAutomacaoLoja.codigobarras);
                                    rst2 = stm2.executeQuery(sql.toString());

                                    if (rst2.next()) {
                                        sql = new StringBuilder();
                                        sql.append("update produtoautomacaoloja set ");
                                        sql.append("precovenda = " + oAutomacaoLoja.precovenda + " ");
                                        sql.append("where codigobarras = " + oAutomacaoLoja.codigobarras + " ");
                                        sql.append("and id_loja = " + oAutomacaoLoja.id_loja);
                                        stm2.execute(sql.toString());
                                    } else {
                                        sql = new StringBuilder();
                                        sql.append("insert into produtoautomacaoloja (");
                                        sql.append("codigobarras, precovenda, id_loja) ");
                                        sql.append("values (");
                                        sql.append(oAutomacaoLoja.codigobarras + ", ");
                                        sql.append(oAutomacaoLoja.precovenda + ", ");
                                        sql.append(oAutomacaoLoja.id_loja + ");");
                                        stm2.execute(sql.toString());
                                    }

                                    sql = new StringBuilder();
                                    sql.append("update produtoautomacao set qtdembalagem = " + oAutomacaoLoja.qtdEmbalagem + " ");
                                    sql.append("where codigobarras = " + oAutomacaoLoja.codigobarras + ";");
                                    stm2.execute(sql.toString());
                                }
                            }
                        }
                    }
                    ProgressBar.next();
                }
            }

            stm.close();
            stm2.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void addCodigoBarrasAtacado(List<ProdutoVO> v_produto) throws Exception {
        Statement stm = null, stm2 = null, stm3 = null;
        ResultSet rst = null, rst2 = null, rst3 = null;
        StringBuilder sql = null;

        try {

            Conexao.begin();
            stm = Conexao.createStatement();
            stm2 = Conexao.createStatement();
            stm3 = Conexao.createStatement();

            ProgressBar.setMaximum(v_produto.size());
            ProgressBar.setStatus("Atualizando dados produto...Codigo Barra Atacado...");

            for (ProdutoVO i_produto : v_produto) {
                for (ProdutoAutomacaoLojaVO oAutomacaoLoja : i_produto.vAutomacaoLoja) {

                    sql = new StringBuilder();
                    sql.append("select p.id, id_tipoembalagem, e_balanca from produto p ");
                    sql.append("inner join implantacao.codigoanterior ant on ant.codigoatual = p.id ");
                    if (i_produto.idDouble > 0) {
                        IdProduto = i_produto.idDouble;
                        sql.append("where CAST(ant.codigoanterior AS NUMERIC(14,0)) = CAST(" + IdProduto + " AS NUMERIC(14,0)) ");
                    } else {
                        IdProduto = i_produto.id;
                        sql.append("where ant.codigoanterior = " + IdProduto + " ");
                    }

                    if (verificarLoja) {
                        sql.append("and id_loja = " + id_loja + " ");
                    }

                    rst = stm.executeQuery(sql.toString());

                    if (rst.next()) {

                        if (oAutomacaoLoja.codigobarras == -1) {
                            if ((rst.getInt("id_tipoembalagem") == 4) || (rst.getInt("id") <= 999)) {
                                oAutomacaoLoja.codigobarras = Utils.gerarEan13((int) rst.getInt("id"), false);
                            } else {
                                oAutomacaoLoja.codigobarras = Utils.gerarEan13((int) rst.getInt("id"), true);
                            }
                        }

                        sql = new StringBuilder();
                        sql.append("select * from produtoautomacao ");
                        sql.append("where codigobarras = " + oAutomacaoLoja.codigobarras);
                        rst2 = stm2.executeQuery(sql.toString());

                        if (!rst2.next()) {
                            sql = new StringBuilder();
                            sql.append("insert into produtoautomacao (");
                            sql.append("codigobarras, id_produto, qtdembalagem, id_tipoembalagem) ");
                            sql.append("values (");
                            sql.append(oAutomacaoLoja.codigobarras + ",");
                            sql.append(rst.getInt("id") + ",");
                            sql.append(oAutomacaoLoja.qtdEmbalagem + ",");
                            sql.append((oAutomacaoLoja.idTipoEmbalagem == -1 ? rst.getInt("id_tipoembalagem") : oAutomacaoLoja.idTipoEmbalagem) + ");");
                            stm.execute(sql.toString());
                        } else {
                            sql = new StringBuilder();
                            sql.append("update produtoautomacao set ");
                            sql.append("qtdembalagem = " + oAutomacaoLoja.qtdEmbalagem + " ");
                            sql.append("where id = " + rst2.getString("id"));
                            stm.execute(sql.toString());
                        }

                        sql = new StringBuilder();
                        sql.append("select * from produtoautomacaoloja ");
                        sql.append("where codigobarras = " + oAutomacaoLoja.codigobarras + " ");
                        sql.append("and id_loja = " + oAutomacaoLoja.id_loja);
                        rst3 = stm3.executeQuery(sql.toString());

                        if (rst3.next()) {
                            sql = new StringBuilder();
                            sql.append("update produtoautomacaoloja set ");
                            sql.append("precovenda = " + oAutomacaoLoja.precovenda + " ");
                            sql.append("where codigobarras = " + oAutomacaoLoja.codigobarras + " ");
                            sql.append("and id_loja = " + oAutomacaoLoja.id_loja);
                            stm3.execute(sql.toString());
                        } else {
                            sql = new StringBuilder();
                            sql.append("insert into produtoautomacaoloja (");
                            sql.append("codigobarras, precovenda, id_loja) ");
                            sql.append("values (");
                            sql.append(oAutomacaoLoja.codigobarras + ", ");
                            sql.append(oAutomacaoLoja.precovenda + ", ");
                            sql.append(oAutomacaoLoja.id_loja + ");");
                            stm3.execute(sql.toString());
                        }
                    }
                }

                ProgressBar.next();
            }

            stm.close();
            stm2.close();
            stm3.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void addCodigoBarrasAtacado2(List<ProdutoVO> v_produto) throws Exception {
        Statement stm = null, stm2 = null, stm3 = null;
        ResultSet rst = null, rst2 = null, rst3 = null;
        StringBuilder sql = null;

        try {

            Conexao.begin();
            stm = Conexao.createStatement();
            stm2 = Conexao.createStatement();
            stm3 = Conexao.createStatement();

            ProgressBar.setMaximum(v_produto.size());
            ProgressBar.setStatus("Atualizando dados produto...Codigo Barra Atacado...");

            for (ProdutoVO i_produto : v_produto) {
                for (ProdutoAutomacaoLojaVO oAutomacaoLoja : i_produto.vAutomacaoLoja) {

                    sql = new StringBuilder();
                    sql.append("select p.id, id_tipoembalagem, e_balanca from produto p ");
                    sql.append("inner join implantacao.codigoanterior ant on ant.codigoatual = p.id ");
                    if (i_produto.idDouble > 0) {
                        IdProduto = i_produto.idDouble;
                        sql.append("where CAST(ant.codigoanterior AS NUMERIC(14,0)) = CAST(" + IdProduto + " AS NUMERIC(14,0)) ");
                    } else {
                        IdProduto = i_produto.id;
                        sql.append("where ant.codigoanterior = " + IdProduto + " ");
                    }

                    if (verificarLoja) {
                        sql.append("and id_loja = " + id_loja + " ");
                    }

                    rst = stm.executeQuery(sql.toString());

                    if (rst.next()) {

                        sql = new StringBuilder();
                        sql.append("select * from produtoautomacao ");
                        sql.append("where codigobarras = " + oAutomacaoLoja.codigobarras);
                        rst2 = stm2.executeQuery(sql.toString());

                        if (!rst2.next()) {
                            sql = new StringBuilder();
                            sql.append("insert into produtoautomacao (");
                            sql.append("codigobarras, id_produto, qtdembalagem, id_tipoembalagem) ");
                            sql.append("values (");
                            sql.append(oAutomacaoLoja.codigobarras + ",");
                            sql.append(rst.getInt("id") + ",");
                            sql.append(oAutomacaoLoja.qtdEmbalagem + ",");
                            sql.append((oAutomacaoLoja.idTipoEmbalagem == -1 ? rst.getInt("id_tipoembalagem") : oAutomacaoLoja.idTipoEmbalagem) + ");");
                            stm.execute(sql.toString());

                            sql = new StringBuilder();
                            sql.append("select * from produtoautomacaoloja ");
                            sql.append("where codigobarras = " + oAutomacaoLoja.codigobarras + " ");
                            sql.append("and id_loja = " + oAutomacaoLoja.id_loja);
                            rst3 = stm3.executeQuery(sql.toString());

                            if (rst3.next()) {
                                sql = new StringBuilder();
                                sql.append("update produtoautomacaoloja set ");
                                sql.append("precovenda = " + oAutomacaoLoja.precovenda + " ");
                                sql.append("where codigobarras = " + oAutomacaoLoja.codigobarras + " ");
                                sql.append("and id_loja = " + oAutomacaoLoja.id_loja);
                                stm3.execute(sql.toString());
                            } else {
                                sql = new StringBuilder();
                                sql.append("insert into produtoautomacaoloja (");
                                sql.append("codigobarras, precovenda, id_loja) ");
                                sql.append("values (");
                                sql.append(oAutomacaoLoja.codigobarras + ", ");
                                sql.append(oAutomacaoLoja.precovenda + ", ");
                                sql.append(oAutomacaoLoja.id_loja + ");");
                                stm3.execute(sql.toString());
                            }
                        }
                    }
                }

                ProgressBar.next();
            }

            stm.close();
            stm2.close();
            stm3.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void addCodigoBarrasAtacadoSemCodigoAnterior(List<ProdutoVO> v_produto, int i_idLoja) throws Exception {
        Statement stm = null, stm2 = null, stm3 = null;
        ResultSet rst = null, rst2 = null, rst3 = null;
        StringBuilder sql = null;

        try {

            Conexao.begin();
            stm = Conexao.createStatement();
            stm2 = Conexao.createStatement();
            stm3 = Conexao.createStatement();

            ProgressBar.setMaximum(v_produto.size());
            ProgressBar.setStatus("Atualizando dados produto...Codigo Barra Atacado...Loja " + i_idLoja);

            for (ProdutoVO i_produto : v_produto) {
                for (ProdutoAutomacaoLojaVO oAutomacaoLoja : i_produto.vAutomacaoLoja) {
                    sql = new StringBuilder();
                    sql.append("select p.id, p.id_tipoembalagem from produto p "
                            + "inner join produtoautomacao pa on pa.id_produto = p.id "
                            + "where pa.codigobarras = " + i_produto.codigoBarras);
                    rst = stm.executeQuery(sql.toString());

                    if (rst.next()) {
                        sql = new StringBuilder();
                        sql.append("select * from produtoautomacao ");
                        sql.append("where codigobarras = " + oAutomacaoLoja.codigobarras);
                        rst2 = stm2.executeQuery(sql.toString());

                        if (!rst2.next()) {
                            sql = new StringBuilder();
                            sql.append("insert into produtoautomacao (");
                            sql.append("codigobarras, id_produto, qtdembalagem, id_tipoembalagem) ");
                            sql.append("values (");
                            sql.append(oAutomacaoLoja.codigobarras + ",");
                            sql.append(rst.getInt("id") + ",");
                            sql.append(oAutomacaoLoja.qtdEmbalagem + ",");
                            sql.append((oAutomacaoLoja.idTipoEmbalagem == -1 ? rst.getInt("id_tipoembalagem") : oAutomacaoLoja.idTipoEmbalagem) + ");");
                            stm.execute(sql.toString());
                        }

                        sql = new StringBuilder();
                        sql.append("select * from produtoautomacaoloja ");
                        sql.append("where codigobarras = " + oAutomacaoLoja.codigobarras);
                        sql.append(" and id_loja = " + i_idLoja);
                        rst3 = stm3.executeQuery(sql.toString());

                        if (!rst3.next()) {
                            sql = new StringBuilder();
                            sql.append("insert into produtoautomacaoloja (");
                            sql.append("codigobarras, precovenda, id_loja) ");
                            sql.append("values (");
                            sql.append(oAutomacaoLoja.codigobarras + ", ");
                            sql.append(oAutomacaoLoja.precovenda + ", ");
                            sql.append(oAutomacaoLoja.id_loja + ");");
                            stm3.execute(sql.toString());
                        }
                    }
                }

                ProgressBar.next();
            }

            stm.close();
            stm2.close();
            stm3.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void addCodigoBarrasEmBranco(List<ProdutoVO> v_produto) throws Exception {
        Statement stm = null, stm2 = null;
        ResultSet rst = null, rst2 = null;
        StringBuilder sql = null;
        try {

            Conexao.begin();

            stm = Conexao.createStatement();
            stm2 = Conexao.createStatement();

            ProgressBar.setMaximum(v_produto.size());

            ProgressBar.setStatus("Atualizando dados produto...Codigo Barra...");

            for (ProdutoVO i_produto : v_produto) {
                for (ProdutoAutomacaoVO oAutomacao : i_produto.vAutomacao) {

                    if (i_produto.idDouble > 0) {
                        IdProduto = i_produto.idDouble;
                    } else {
                        IdProduto = i_produto.id;
                    }

                    sql = new StringBuilder();
                    sql.append("select * from produtoautomacao ");
                    sql.append("where codigobarras = " + oAutomacao.codigoBarras);

                    rst2 = stm2.executeQuery(sql.toString());

                    if (!rst2.next()) {
                        sql = new StringBuilder();
                        sql.append("insert into produtoautomacao (");
                        sql.append("id_produto, codigobarras, qtdembalagem, id_tipoembalagem) ");
                        sql.append("values (");
                        sql.append(IdProduto + ",");
                        sql.append(oAutomacao.codigoBarras + ",");
                        sql.append((oAutomacao.qtdEmbalagem == -1 ? "1," : oAutomacao.qtdEmbalagem) + ",");
                        sql.append(oAutomacao.idTipoEmbalagem + ");");
                        stm.execute(sql.toString());
                    }
                }

                ProgressBar.next();
            }

            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void alterarSituacaoCadastroProduto(List<ProdutoVO> v_produto, int idLoja) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;

        try {

            Conexao.begin();

            ProgressBar.setMaximum(v_produto.size());
            ProgressBar.setStatus("Importando Situação Cadastro dos Produtos (Inativo)...");

            stm = Conexao.createStatement();

            for (ProdutoVO i_produto : v_produto) {
                for (ProdutoComplementoVO oComplemento : i_produto.vComplemento) {

                    sql = new StringBuilder();
                    sql.append("select p.id from produto p ");
                    sql.append("inner join implantacao.codigoanterior ant on ");
                    sql.append("ant.codigoatual = p.id ");

                    if (i_produto.id > 0) {
                        sql.append("where ant.codigoanterior = " + i_produto.id);
                    } else {
                        sql.append("where ant.codigoanterior = " + i_produto.idDouble);
                    }

                    rst = stm.executeQuery(sql.toString());

                    if (rst.next()) {

                        sql = new StringBuilder();
                        sql.append("update produtocomplemento set ");
                        sql.append("id_situacaocadastro = " + oComplemento.idSituacaoCadastro + " ");
                        sql.append("where id_produto = " + rst.getInt("id") + " ");
                        sql.append("and id_loja = " + idLoja);

                        stm.execute(sql.toString());

                    }
                }

                ProgressBar.next();
            }

            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void alterarSituacaoCadastroProdutoIntegracao(List<ProdutoVO> v_produto, int idLoja) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;

        try {
            Conexao.begin();

            ProgressBar.setMaximum(v_produto.size());
            ProgressBar.setStatus("Alterando Situação Produtos, Integração, Loja " + idLoja);

            stm = Conexao.createStatement();
            for (ProdutoVO i_produto : v_produto) {
                for (ProdutoComplementoVO oComplemento : i_produto.vComplemento) {

                    sql = new StringBuilder();
                    sql.append("update produtocomplemento set ");
                    sql.append("id_situacaocadastro = " + oComplemento.idSituacaoCadastro + " ");
                    sql.append("where id_produto = " + i_produto.id + " ");
                    sql.append("and id_loja = " + idLoja);
                    stm.execute(sql.toString());

                }
                ProgressBar.next();
            }

            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void alterarNcm(List<ProdutoVO> v_produto, int idLoja) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;

        try {
            Conexao.begin();

            stm = Conexao.createStatement();

            ProgressBar.setMaximum(v_produto.size());
            ProgressBar.setStatus("Importando...Ncm Produto...");

            for (ProdutoVO i_produto : v_produto) {
                sql = new StringBuilder();
                sql.append("select p.id from produto p ");
                sql.append("inner join implantacao.codigoanterior ant on ant.codigoatual = p.id ");
                if (i_produto.id > 0) {
                    sql.append("where ant.codigoanterior = " + i_produto.id + " ");
                } else {
                    sql.append("where ant.codigoanterior = " + i_produto.idDouble + " ");
                }
                sql.append("and p.ncm1 = 402 and p.ncm2 = 99 and p.ncm3 = 0 ");
                sql.append("and ant.id_loja = " + idLoja);
                rst = stm.executeQuery(sql.toString());
                if (rst.next()) {
                    sql = new StringBuilder();
                    sql.append("update produto set ");
                    sql.append("ncm1 = " + i_produto.ncm1 + ", ");
                    sql.append("ncm2 = " + i_produto.ncm2 + ", ");
                    sql.append("ncm3 = " + i_produto.ncm3 + " ");
                    sql.append("where id = " + rst.getInt("id") + "; ");
                    for (CodigoAnteriorVO i_codigoAnterior : i_produto.vCodigoAnterior) {
                        sql.append("update implantacao.codigoanterior ");
                        sql.append("set ncm = '" + i_codigoAnterior.ncm + "' ");
                        sql.append("where codigoanterior = " + i_codigoAnterior.codigoanterior + ";");
                    }
                    stm.execute(sql.toString());
                }
                ProgressBar.next();
            }
            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }


    /*public void alterarMargemProduto(List<ProdutoVO> v_produto, int idLoja) throws Exception {
     Statement stm = null;
     ResultSet rst = null;
     StringBuilder sql = null;
        
     double IdProduto;
        
     try {

     Conexao.begin();

     stm = Conexao.createStatement();

     ProgressBar.setMaximum(v_produto.size());

     ProgressBar.setStatus("Atualizando dados produto...Margem...");

     for (ProdutoVO i_produto : v_produto) {
     sql = new StringBuilder();
     sql.append("select p.id from produto p ");
     sql.append("inner join implantacao.codigoanterior ant on ant.codigoatual = p.id ");
     if (i_produto.idDouble > 0) {
     IdProduto = i_produto.idDouble;
     sql.append("where CAST(ant.codigoanterior AS NUMERIC(14,0)) = CAST(" + IdProduto + " AS NUMERIC(14,0)) ");
     } else {
     IdProduto = i_produto.id;
     sql.append("where ant.codigoanterior = " + IdProduto);
     }

     rst = stm.executeQuery(sql.toString());

     if (rst.next()) {

     sql = new StringBuilder();

     sql.append("UPDATE produto SET ");
     sql.append("margem = " + i_produto.margem + " ");
     sql.append("WHERE id = " + rst.getInt("id") + " ");

     stm.execute(sql.toString());
     }
     ProgressBar.next();
     }

     stm.close();
     Conexao.commit();
     } catch (Exception ex) {
     Conexao.rollback();
     throw ex;
     }
     }*/
    public void alterarCodigoAnterior(Collection<ProdutoVO> v_produto) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;

        try {
            Conexao.begin();
            stm = Conexao.createStatement();
            ProgressBar.setMaximum(v_produto.size());
            ProgressBar.setStatus("Atualizando dados produto...Código anterior PIS/COFINS, Natureza Receita, ICMS, NCM...");

            int cont = 0;

            for (ProdutoVO i_produto : v_produto) {

                sql = new StringBuilder();
                sql.append("SELECT p.id FROM produto p ");
                sql.append("INNER JOIN implantacao.codigoanterior ant ");
                sql.append("ON ant.codigoatual = p.id ");

                if (i_produto.idDouble > 0) {
                    IdProduto = i_produto.idDouble;
                    sql.append("where CAST(ant.codigoanterior AS NUMERIC(14,0)) = CAST(" + IdProduto + " AS NUMERIC(14,0)) ");
                } else {
                    IdProduto = i_produto.id;
                    sql.append("where ant.codigoanterior = " + IdProduto);
                }

                rst = stm.executeQuery(sql.toString());

                if (rst.next()) {

                    String sql2 = "";

                    for (CodigoAnteriorVO i_codigoAnterior : i_produto.vCodigoAnterior) {

                        sql2 += "    ref_icmsdebito = '" + (i_codigoAnterior.ref_icmsdebito != null ? i_codigoAnterior.ref_icmsdebito.trim() : "") + "',";
                        sql2 += "    piscofinsdebito = " + i_codigoAnterior.piscofinsdebito + ",";
                        sql2 += "    piscofinscredito = " + i_codigoAnterior.piscofinscredito + ",";
                        sql2 += "    naturezareceita = " + i_codigoAnterior.naturezareceita + ",";
                        sql2 += "    ncm = '" + (i_codigoAnterior.ncm != null ? i_codigoAnterior.ncm.trim() : "") + "'";

                        sql2 = "UPDATE implantacao.codigoanterior SET " + sql2 + " WHERE codigoatual = " + rst.getInt("id") + ";";
                    }

                    stm.execute(sql2);

                }
                ProgressBar.next();
            }

            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void alterarCodigoAnterior(List<ProdutoVO> v_produto) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;

        try {
            Conexao.begin();
            stm = Conexao.createStatement();
            ProgressBar.setMaximum(v_produto.size());
            ProgressBar.setStatus("Atualizando dados produto...Código anterior PIS/COFINS, Natureza Receita, ICMS, NCM...");

            int cont = 0;

            for (ProdutoVO i_produto : v_produto) {

                sql = new StringBuilder();
                sql.append("SELECT p.id FROM produto p ");
                sql.append("INNER JOIN implantacao.codigoanterior ant ");
                sql.append("ON ant.codigoatual = p.id ");

                if (i_produto.idDouble > 0) {
                    IdProduto = i_produto.idDouble;
                    sql.append("where CAST(ant.codigoanterior AS NUMERIC(14,0)) = CAST(" + IdProduto + " AS NUMERIC(14,0)) ");
                } else {
                    IdProduto = i_produto.id;
                    sql.append("where ant.codigoanterior = " + IdProduto);
                }

                rst = stm.executeQuery(sql.toString());

                if (rst.next()) {

                    String sql2 = "";

                    for (CodigoAnteriorVO i_codigoAnterior : i_produto.vCodigoAnterior) {

                        sql2 += "    ref_icmsdebito = '" + (i_codigoAnterior.ref_icmsdebito != null ? i_codigoAnterior.ref_icmsdebito.trim() : "") + "',";
                        sql2 += "    piscofinsdebito = " + i_codigoAnterior.piscofinsdebito + ",";
                        sql2 += "    piscofinscredito = " + i_codigoAnterior.piscofinscredito + ",";
                        sql2 += "    naturezareceita = " + i_codigoAnterior.naturezareceita + ",";
                        sql2 += "    ncm = '" + (i_codigoAnterior.ncm != null ? i_codigoAnterior.ncm.trim() : "") + "'";

                        sql2 = "UPDATE implantacao.codigoanterior SET " + sql2 + " WHERE codigoatual = " + rst.getInt("id") + ";";
                    }

                    stm.execute(sql2);

                }
                ProgressBar.next();
            }

            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void alterarPisCofinsProduto(List<ProdutoVO> v_produto) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;
        //File f = new File("C:\\vr\\Implantacao\\scripts\\update_piscofins_produto.txt");
        //FileWriter fw = new FileWriter(f);
        //BufferedWriter bw = new BufferedWriter(fw);

        try {
            Conexao.begin();
            stm = Conexao.createStatement();
            ProgressBar.setMaximum(v_produto.size());
            ProgressBar.setStatus("Atualizando dados produto...PisCofins e Natureza Receita...");

            Map<Double, CodigoAnteriorVO> anteriores = new CodigoAnteriorDAO().carregarCodigoAnterior();

            for (ProdutoVO i_produto : v_produto) {

                if (i_produto.idDouble > 0) {
                    IdProduto = i_produto.idDouble;
                } else {
                    IdProduto = i_produto.id;
                }

                CodigoAnteriorVO codigoAnterior = anteriores.get(IdProduto);

                if (codigoAnterior != null) {
                    sql = new StringBuilder();
                    sql.append("update produto set ");
                    sql.append("    id_tipopiscofins = " + i_produto.idTipoPisCofinsDebito + ", ");
                    sql.append("    id_tipopiscofinscredito = " + i_produto.idTipoPisCofinsCredito + ", ");
                    sql.append("    tiponaturezareceita = " + (i_produto.tipoNaturezaReceita == -1 ? null : i_produto.tipoNaturezaReceita) + " ");
                    sql.append("where id = " + (int) codigoAnterior.getCodigoatual() + ";");
                    for (CodigoAnteriorVO i_codigoAnterior : i_produto.vCodigoAnterior) {
                        sql.append("UPDATE implantacao.codigoanterior SET ");
                        sql.append("    piscofinsdebito = " + i_codigoAnterior.piscofinsdebito + ", ");
                        sql.append("    piscofinscredito = " + i_codigoAnterior.piscofinscredito + ", ");
                        sql.append("    naturezareceita = " + i_codigoAnterior.naturezareceita + " ");
                        sql.append("WHERE codigoatual = " + (int) codigoAnterior.getCodigoatual() + ";");
                    }

                    for (ProdutoAliquotaVO i_aliquota : i_produto.vAliquota) {
                        sql.append("UPDATE produtoaliquota SET ");
                        sql.append("id_aliquotadebito = " + i_aliquota.idAliquotaDebito + ", ");
                        sql.append("id_aliquotacredito = " + i_aliquota.idAliquotaCredito + ", ");
                        sql.append("id_aliquotadebitoforaestado = " + i_aliquota.idAliquotaDebitoForaEstado + ", ");
                        sql.append("id_aliquotacreditoforaestado = " + i_aliquota.idAliquotaCreditoForaEstado + ", ");
                        sql.append("id_aliquotadebitoforaestadonf = " + i_aliquota.idAliquotaDebitoForaEstado + ", ");
                        sql.append("id_aliquotaconsumidor = " + i_aliquota.getIdAliquotaConsumidor() + " ");
                        sql.append("WHERE id_produto = " + (int) codigoAnterior.getCodigoatual() + ";");

                        for (CodigoAnteriorVO i_codigoAnterior : i_produto.vCodigoAnterior) {
                            sql.append("UPDATE implantacao.codigoanterior SET ");
                            sql.append("    ref_icmsdebito = '" + i_codigoAnterior.ref_icmsdebito + "' ");
                            sql.append("WHERE codigoatual = " + (int) codigoAnterior.getCodigoatual() + ";");
                        }
                    }
                    //bw.write(sql.toString());
                    //bw.newLine();
                    stm.execute(sql.toString());
                }
                ProgressBar.next();
            }

            //bw.flush();
            //bw.close();
            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void alterarPrecoCustoTributacaoEstoque(List<ProdutoVO> v_produto, int idLoja, int Tipo) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;

        try {
            Conexao.begin();
            stm = Conexao.createStatement();
            ProgressBar.setMaximum(v_produto.size());

            if (Tipo == 1) {
                ProgressBar.setStatus("Atualizando dados produto...Custo...");
            } else if (Tipo == 2) {
                ProgressBar.setStatus("Atualizando dados produto...PisCofins ICMS...");
            } else if (Tipo == 3) {
                ProgressBar.setStatus("Atualizando dados produto...Estoque...");
            } else if (Tipo == 4) {
                ProgressBar.setStatus("Atualizando dados produto...Preço...");
            } else if (Tipo == 5) {
                ProgressBar.setStatus("Atualizando dados produto...Margem...");
            }

            for (ProdutoVO i_produto : v_produto) {

                sql = new StringBuilder();
                sql.append("SELECT p.id FROM produto p ");
                sql.append("INNER JOIN implantacao.codigoanterior ant ");
                sql.append("ON ant.codigoatual = p.id ");
                if (i_produto.idDouble > 0) {
                    IdProduto = i_produto.idDouble;
                    sql.append("where CAST(ant.codigoanterior AS NUMERIC(14,0)) = CAST(" + IdProduto + " AS NUMERIC(14,0)) ");
                } else {
                    IdProduto = i_produto.id;
                    sql.append("where ant.codigoanterior = " + IdProduto);
                }

                rst = stm.executeQuery(sql.toString());

                if (rst.next()) {
                    sql = new StringBuilder();

                    if (Tipo == 1) {
                        if (i_produto.margem > 0) {
                            sql.append(" UPDATE produto SET ");
                            sql.append(" margem = round(" + i_produto.margem + ",2) ");
                            sql.append(" WHERE id = " + IdProduto + "; ");
                        }
                        for (ProdutoComplementoVO i_preco : i_produto.vComplemento) {
                            sql.append("UPDATE produtocomplemento SET ");
                            sql.append("custocomimposto = " + i_preco.custoComImposto + ", ");
                            sql.append("custosemimposto = " + i_preco.custoSemImposto + " ");
                            sql.append("WHERE id_loja = " + idLoja);
                            sql.append("  and id_produto = " + rst.getInt("id") + ";");
                        }
                        for (CodigoAnteriorVO i_codigoAnterior : i_produto.vCodigoAnterior) {
                            sql.append("UPDATE implantacao.codigoanterior SET ");
                            sql.append("custocomimposto = " + i_codigoAnterior.custocomimposto + ", ");
                            sql.append("custosemimposto = " + i_codigoAnterior.custosemimposto + " ");
                            sql.append("WHERE codigoatual = " + rst.getInt("id") + ";");
                        }
                    } else if (Tipo == 2) {
                        sql.append("update produto set ");
                        sql.append("    id_tipopiscofins = " + i_produto.idTipoPisCofinsDebito + ", ");
                        sql.append("    id_tipopiscofinscredito = " + i_produto.idTipoPisCofinsCredito + ", ");
                        sql.append("    tiponaturezareceita = " + (i_produto.tipoNaturezaReceita == -1 ? null : i_produto.tipoNaturezaReceita) + " ");
                        sql.append("where id = " + rst.getInt("id") + ";");
                        for (ProdutoAliquotaVO i_aliquota : i_produto.vAliquota) {
                            sql.append("UPDATE produtoaliquota SET ");
                            sql.append("id_aliquotadebito = " + i_aliquota.idAliquotaDebito + ", ");
                            sql.append("id_aliquotacredito = " + i_aliquota.idAliquotaCredito + ", ");
                            sql.append("id_aliquotacreditoforaestado = " + i_aliquota.idAliquotaCreditoForaEstado + ", ");
                            sql.append("id_aliquotadebitoforaestadonf = " + i_aliquota.idAliquotaDebitoForaEstado + " ");
                            sql.append("WHERE id_produto = " + rst.getInt("id") + ";");
                        }
                        for (CodigoAnteriorVO i_codigoAnterior : i_produto.vCodigoAnterior) {
                            sql.append("UPDATE implantacao.codigoanterior SET ");
                            sql.append("    piscofinsdebito = " + i_codigoAnterior.piscofinsdebito + ", ");
                            sql.append("    piscofinscredito = " + i_codigoAnterior.piscofinscredito + ", ");
                            sql.append("    naturezareceita = " + i_codigoAnterior.naturezareceita + " ");
                            sql.append("WHERE codigoatual = " + rst.getInt("id") + ";");
                        }
                    } else if (Tipo == 3) {
                        for (ProdutoComplementoVO i_estoque : i_produto.vComplemento) {
                            sql.append("UPDATE produtocomplemento SET ");
                            sql.append("estoque = " + i_estoque.estoque + " ");
                            sql.append("WHERE id_loja = " + idLoja);
                            sql.append("  and id_produto = " + rst.getInt("id") + ";");
                        }
                        for (CodigoAnteriorVO i_codigoAnterior : i_produto.vCodigoAnterior) {
                            sql.append("UPDATE implantacao.codigoanterior SET ");
                            sql.append("estoque = " + i_codigoAnterior.estoque);
                            sql.append("WHERE codigoatual = " + rst.getInt("id") + ";");
                        }
                    } else if (Tipo == 4) {
                        for (ProdutoComplementoVO i_preco : i_produto.vComplemento) {
                            sql.append("UPDATE produtocomplemento SET ");
                            sql.append("precovenda = " + i_preco.precoVenda + ", ");
                            sql.append("precodiaseguinte = " + i_preco.precoVenda + " ");
                            sql.append("WHERE id_loja = " + idLoja);
                            sql.append("  and id_produto = " + rst.getInt("id") + ";");
                        }
                        for (CodigoAnteriorVO i_codigoAnterior : i_produto.vCodigoAnterior) {
                            sql.append("UPDATE implantacao.codigoanterior SET ");
                            sql.append("precovenda = " + i_codigoAnterior.precovenda + " ");
                            sql.append("WHERE codigoatual = " + rst.getInt("id") + ";");
                        }
                    } else if (Tipo == 5) {
                        sql.append("UPDATE produto set ");
                        sql.append("margem = " + i_produto.margem + " ");
                        sql.append("WHERE id = " + rst.getInt("id") + ";");
                    }
                    stm.execute(sql.toString());
                }
                ProgressBar.next();
            }

            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void alterarICMSProduto(List<ProdutoVO> v_produto) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;
        //File f = new File("C:/vr/Implantacao/scripts/update_produtoAliquota.txt");
        //FileWriter fw = new FileWriter(f);
        //BufferedWriter bw = new BufferedWriter(fw);

        try {
            Conexao.begin();
            stm = Conexao.createStatement();
            ProgressBar.setMaximum(v_produto.size());
            ProgressBar.setStatus("Atualizando dados produto...ICMS...");
            Map<Double, CodigoAnteriorVO> anteriores = new CodigoAnteriorDAO().carregarCodigoAnterior();
            for (ProdutoVO i_produto : v_produto) {
                if (i_produto.idDouble > 0) {
                    IdProduto = i_produto.idDouble;
                } else {
                    IdProduto = i_produto.id;
                }

                CodigoAnteriorVO vCodigoAnterior = anteriores.get(IdProduto);

                if (vCodigoAnterior != null) {
                    for (ProdutoAliquotaVO i_aliquota : i_produto.vAliquota) {
                        sql = new StringBuilder();
                        sql.append("update produtoaliquota set ");
                        sql.append("id_aliquotadebito = " + i_aliquota.idAliquotaDebito + ", ");
                        sql.append("id_aliquotacredito = " + i_aliquota.idAliquotaCredito + ", ");
                        sql.append("id_aliquotadebitoforaestado = " + i_aliquota.idAliquotaDebitoForaEstado + ", ");
                        sql.append("id_aliquotacreditoforaestado = " + i_aliquota.idAliquotaCreditoForaEstado + ", ");
                        sql.append("id_aliquotadebitoforaestadoNF = " + i_aliquota.idAliquotaDebitoForaEstadoNF + ", ");
                        sql.append("id_aliquotaconsumidor = " + i_aliquota.idAliquotaConsumidor + " ");
                        sql.append("where id_produto = " + vCodigoAnterior.getCodigoatual() + "; ");
                        stm.executeUpdate(sql.toString());
                    }

                    for (CodigoAnteriorVO i_codigoAnterior : i_produto.vCodigoAnterior) {
                        sql = new StringBuilder();
                        sql.append("UPDATE implantacao.codigoanterior SET ");
                        sql.append("ref_icmsdebito = '" + i_codigoAnterior.ref_icmsdebito + "' ");
                        sql.append("WHERE codigoatual = " + IdProduto + ";");
                        stm.executeUpdate(sql.toString());
                    }

                    //bw.write(sql.toString());
                    //bw.newLine();
                }

                //}
                ProgressBar.next();
            }

            //bw.flush();
            //bw.close();
            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void adicionarIcmsProduto(List<ProdutoVO> v_produto) throws Exception {
        StringBuilder sql = null;
        Statement stm = null, stm2 = null;
        ResultSet rst = null, rst2 = null;

        try {
            Conexao.begin();

            stm = Conexao.createStatement();
            stm2 = Conexao.createStatement();

            ProgressBar.setStatus("Importando dados...Icms Produtos...");
            ProgressBar.setMaximum(v_produto.size());

            for (ProdutoVO i_produto : v_produto) {

                sql = new StringBuilder();
                sql.append("select p.id from produto p ");
                sql.append("inner join implantacao.codigoanterior ant on ant.codigoatual = p.id ");

                if (i_produto.id > 0) {
                    sql.append("where ant.codigoanterior = " + i_produto.id + " ");
                } else {
                    sql.append("where ant.codigoanterior = " + i_produto.idDouble + " ");
                }

                rst = stm.executeQuery(sql.toString());

                if (rst.next()) {

                    for (ProdutoAliquotaVO oAliquota : i_produto.vAliquota) {

                        sql = new StringBuilder();
                        sql.append("select id_produto, id_estado ");
                        sql.append("from produtoaliquota ");
                        sql.append("where id_produto = " + rst.getString("id") + " ");
                        sql.append("and id_estado = " + oAliquota.idEstado + " ");

                        rst2 = stm2.executeQuery(sql.toString());

                        if (!rst2.next()) {

                            sql = new StringBuilder();
                            sql.append("INSERT INTO produtoaliquota( ");
                            sql.append("id_produto, id_estado, id_aliquotadebito, id_aliquotacredito, ");
                            sql.append("id_aliquotadebitoforaestado, id_aliquotacreditoforaestado, id_aliquotadebitoforaestadonf) ");
                            sql.append("VALUES ( ");
                            sql.append(rst.getInt("id") + ", ");
                            sql.append(oAliquota.idEstado + ", ");
                            sql.append(oAliquota.idAliquotaDebito + ", ");
                            sql.append(oAliquota.idAliquotaCredito + ", ");
                            sql.append(oAliquota.idAliquotaDebitoForaEstado + ", ");
                            sql.append(oAliquota.idAliquotaCreditoForaEstado + ", ");
                            sql.append(oAliquota.idAliquotaDebitoForaEstadoNF + ");");

                            stm.execute(sql.toString());

                        }
                    }
                }

                ProgressBar.next();
            }

            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void alterarCestProduto(List<ProdutoVO> v_produto) throws Exception {

        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        double idProduto = 0;
        int idCest = -1;

        try {

            Conexao.begin();

            stm = Conexao.createStatement();

            ProgressBar.setMaximum(v_produto.size());

            ProgressBar.setStatus("Atualizando dados produto...Código CEST...");

            Map<Double, CodigoAnteriorVO> anteriores = new CodigoAnteriorDAO().carregarCodigoAnterior();

            for (ProdutoVO i_produto : v_produto) {

                if (i_produto.idDouble > 0) {
                    idProduto = i_produto.idDouble;
                } else {
                    idProduto = i_produto.id;
                }

                CodigoAnteriorVO codigoAnterior = anteriores.get(idProduto);
                if (codigoAnterior != null) {

                    sql = new StringBuilder();
                    sql.append("select id from cest ");
                    sql.append("where cest1 = " + i_produto.cest1 + " ");
                    sql.append("  and cest2 = " + i_produto.cest2 + " ");
                    sql.append("  and cest3 = " + i_produto.cest3 + ";");

                    rst = stm.executeQuery(sql.toString());

                    if (rst.next()) {
                        idCest = rst.getInt("id");
                    } else {
                        idCest = -1;
                    }

                    sql = new StringBuilder();
                    sql.append(" UPDATE produto SET ");
                    sql.append(" id_cest = " + (idCest == -1 ? null : idCest) + " ");
                    sql.append(" WHERE id = " + (int) idProduto + " ");
                    sql.append(" AND ncm1 = " + i_produto.ncm1 + " ");
                    sql.append(" AND ncm2 = " + i_produto.ncm2 + " ");
                    sql.append(" AND ncm3 = " + i_produto.ncm3 + "; ");

                    for (CodigoAnteriorVO ant : i_produto.getvCodigoAnterior()) {
                        sql.append(" UPDATE implantacao.codigoanterior SET ");
                        sql.append(" cest = " + Utils.quoteSQL(ant.getCest()) + ", ");
                        sql.append(" ncm = " + Utils.quoteSQL(ant.getNcm()) + " ");
                        sql.append(" WHERE codigoatual = " + (int) idProduto + ";");
                    }

                    stm.execute(sql.toString());
                }

                ProgressBar.next();

            }

            stm.close();
            Conexao.commit();

        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void adicionarNcmCestProduto(List<ProdutoVO> v_produto) throws Exception {

        StringBuilder sql = null;
        Statement stm = null, stm2 = null;
        ResultSet rst = null, rst2 = null;
        double idProduto = 0;
        int idCest = -1;

        try {

            Conexao.begin();

            stm = Conexao.createStatement();
            stm2 = Conexao.createStatement();

            ProgressBar.setMaximum(v_produto.size());

            ProgressBar.setStatus("Atualizando dados produto...Código CEST...");

            Map<Double, CodigoAnteriorVO> anteriores = new CodigoAnteriorDAO().carregarCodigoAnterior();

            for (ProdutoVO i_produto : v_produto) {

                if (i_produto.idDouble > 0) {
                    idProduto = i_produto.idDouble;
                } else {
                    idProduto = i_produto.id;
                }

                CodigoAnteriorVO codigoAnterior = anteriores.get(idProduto);
                if (codigoAnterior != null) {

                    sql = new StringBuilder();
                    sql.append("select p.id, p.descricaocompleta, n.id id_ncm, p.id_cest ");
                    sql.append("from produto p ");
                    sql.append("inner join ncm n on n.ncm1 = p.ncm1 and n.ncm2 = p.ncm2 and n.ncm3 = p.ncm3 ");
                    sql.append("inner join cest c on c.id = p.id_cest ");
                    sql.append("where n.ncm1 = " + i_produto.ncm1 + " ");
                    sql.append("  and n.ncm2 = " + i_produto.ncm2 + " ");
                    sql.append("  and n.ncm3 = " + i_produto.ncm3 + " ");
                    sql.append("  and c.cest1 = " + i_produto.cest1 + " ");
                    sql.append("  and c.cest2 = " + i_produto.cest2 + " ");
                    sql.append("  and c.cest3 = " + i_produto.cest3 + " ");
                    sql.append("  and p.id_cest is not null; ");
                    rst = stm.executeQuery(sql.toString());

                    if (rst.next()) {

                        sql = new StringBuilder();
                        sql.append("select * from ncmcest ");
                        sql.append(" where id_ncm = " + rst.getInt("id_ncm") + " ");
                        sql.append("   and id_cest = " + rst.getInt("id_cest") + ";");

                        rst2 = stm2.executeQuery(sql.toString());

                        if (!rst2.next()) {
                            sql = new StringBuilder();
                            sql.append("insert into ncmcest (id_ncm, id_cest) ");
                            sql.append("values ( ");
                            sql.append(rst.getInt("id_ncm") + ", ");
                            sql.append(rst.getInt("id_cest") + ");");
                            stm2.execute(sql.toString());
                        }
                    }
                }

                ProgressBar.next();

            }

            stm.close();
            stm2.close();
            Conexao.commit();

        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void alterarMercadologicoProdutoRapido(List<ProdutoVO> v_produto) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        double idProduto = 0;

        try {
            Conexao.begin();

            stm = Conexao.createStatement();

            ProgressBar.setMaximum(v_produto.size());

            ProgressBar.setStatus("Atualizando dados produto...Produto Mercadologico...");

            Map<Double, CodigoAnteriorVO> anteriores = new CodigoAnteriorDAO().carregarCodigoAnterior();

            for (ProdutoVO i_produto : v_produto) {

                if (i_produto.idDouble > 0) {
                    idProduto = i_produto.idDouble;
                } else {
                    idProduto = i_produto.id;
                }

                CodigoAnteriorVO codigoAnterior = anteriores.get(idProduto);
                if (codigoAnterior != null) {

                    sql = new StringBuilder();
                    sql.append("update produto set ");
                    sql.append("mercadologico1 = " + i_produto.mercadologico1 + ", ");
                    sql.append("mercadologico2 = " + i_produto.mercadologico2 + ", ");
                    sql.append("mercadologico3 = " + i_produto.mercadologico3 + " ");
                    sql.append("where id = " + (int) codigoAnterior.getCodigoatual() + " ");
                    sql.append("and mercadologico1 = 38; ");
                    stm.execute(sql.toString());
                }

                ProgressBar.next();

            }

            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void inserirEstoqueTerceiro(List<EstoqueTerceiroVO> v_estoqueTerceiro) throws Exception {
        Statement stm = null;
        StringBuilder sql = null;
        ResultSet rst = null;
        try {
            Map<Double, CodigoAnteriorVO> codigoAnterior = new CodigoAnteriorDAO().carregarCodigoAnterior();
            Conexao.begin();
            stm = Conexao.createStatement();
            ProgressBar.setMaximum(v_estoqueTerceiro.size());
            ProgressBar.setStatus("Inserindo dados...Estoque Terceiro...");

            for (EstoqueTerceiroVO i_estoqueTerceiro : v_estoqueTerceiro) {
                CodigoAnteriorVO anterior = codigoAnterior.get(i_estoqueTerceiro.getId_produto());

                if (anterior != null) {
                    sql = new StringBuilder();
                    sql.append("select * from estoqueterceiro "
                            + "where id_produto = " + anterior.getCodigoatual()
                            + " and id_loja = " + i_estoqueTerceiro.getId_loja()
                            + " and id_lojaterceiro = " + i_estoqueTerceiro.getId_lojaterceiro());
                    rst = stm.executeQuery(sql.toString());

                    if (!rst.next()) {
                        sql = new StringBuilder();
                        sql.append("insert into estoqueterceiro "
                                + "(id_produto, quantidade, id_loja, id_lojaterceiro) "
                                + "values ("
                                + anterior.getCodigoatual() + ","
                                + i_estoqueTerceiro.getQuantidade() + ", "
                                + i_estoqueTerceiro.getId_loja() + ", "
                                + i_estoqueTerceiro.getId_lojaterceiro() + ");");
                        stm.execute(sql.toString());
                    }
                } else {

                    sql = new StringBuilder();
                    sql.append("select * from estoqueterceiro "
                            + "where id_produto = " + this.getIdCodigoBarrasAnterior(i_estoqueTerceiro.getCodigoBarras(),
                                    i_estoqueTerceiro.getDescProduto())
                            + " and id_loja = " + i_estoqueTerceiro.getId_loja()
                            + " and id_lojaterceiro = " + i_estoqueTerceiro.getId_lojaterceiro());
                    rst = stm.executeQuery(sql.toString());

                    if (!rst.next()) {

                        if (this.getIdCodigoBarrasAnterior(i_estoqueTerceiro.getCodigoBarras(),
                                i_estoqueTerceiro.getDescProduto()) != -1) {
                            sql = new StringBuilder();
                            sql.append("insert into estoqueterceiro "
                                    + "(id_produto, quantidade, id_loja, id_lojaterceiro) "
                                    + "values ("
                                    + this.getIdCodigoBarrasAnterior(i_estoqueTerceiro.getCodigoBarras(),
                                            i_estoqueTerceiro.getDescProduto()) + ","
                                    + i_estoqueTerceiro.getQuantidade() + ", "
                                    + i_estoqueTerceiro.getId_loja() + ", "
                                    + i_estoqueTerceiro.getId_lojaterceiro() + ");");
                            stm.execute(sql.toString());
                        }
                    }
                }
                ProgressBar.next();
            }
            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    //* FIM METODOS PADRÕES 
//* METODOS ESPECIFICAS PARA CLIENTES
    //********** AQUI SÃO ALTERAÇÕES DO SISTEMA MILENIO
    public void alterarPisCofinsNaturezaReceitaMilenio(List<ProdutoVO> v_produto) throws Exception {
        Statement stm = null;
        //ResultSet rst = null;
        StringBuilder sql = null;

        try {
            Conexao.begin();

            stm = Conexao.createStatement();

            ProgressBar.setMaximum(v_produto.size());

            ProgressBar.setStatus("Atualizando dados produto...piscofins, natureza receita");

            Map<Double, CodigoAnteriorVO> anteriores = new CodigoAnteriorDAO().carregarCodigoAnterior();

            for (ProdutoVO i_produto : v_produto) {

                for (CodigoAnteriorVO i_codigoAnterior : i_produto.vCodigoAnterior) {
                    double idProduto;
                    if (i_produto.idDouble > 0) {
                        idProduto = i_produto.idDouble;
                    } else {
                        idProduto = i_produto.id;
                    }

                    CodigoAnteriorVO vCodigoAnterior = anteriores.get(idProduto);

                    if (vCodigoAnterior != null) {
                        sql = new StringBuilder();
                        sql.append("UPDATE produto SET ");
                        sql.append("id_tipopiscofins = " + i_produto.idTipoPisCofinsDebito + ", ");
                        sql.append("id_tipopiscofinscredito = " + i_produto.idTipoPisCofinsCredito + ", ");
                        sql.append("tiponaturezareceita = " + i_produto.tipoNaturezaReceita + " ");
                        sql.append("where id = " + vCodigoAnterior.getCodigoatual()/*rst.getInt("id")*/ + ";");

                        sql.append("UPDATE implantacao.codigoanterior SET ");
                        sql.append("piscofinsdebito = " + i_codigoAnterior.piscofinsdebito + ", ");
                        sql.append("piscofinscredito = " + i_codigoAnterior.piscofinscredito + ", ");
                        sql.append("naturezareceita = " + i_codigoAnterior.naturezareceita + " ");
                        sql.append("WHERE codigoatual = " + vCodigoAnterior.getCodigoatual()/*rst.getInt("id")*/ + ";");

                        stm.execute(sql.toString());

                    }
                }

                ProgressBar.next();
            }

            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void alterarCustoProdutoMilenio(List<ProdutoVO> v_produto, int id_loja) throws Exception {
        Statement stm = null, stm2 = null;
        ResultSet rst = null;
        StringBuilder sql = null;

        try {

            Conexao.begin();

            stm = Conexao.createStatement();
            stm2 = Conexao.createStatement();

            ProgressBar.setMaximum(v_produto.size());

            ProgressBar.setStatus("Atualizando dados produto...custo...");

            for (ProdutoVO i_produto : v_produto) {
                for (ProdutoComplementoVO oComplemento : i_produto.vComplemento) {

                    sql = new StringBuilder();
                    sql.append("SELECT p.id as id_produto FROM produto p ");
                    sql.append("INNER JOIN implantacao.codigoanterior ant ");
                    sql.append("ON ant.codigoatual = p.id ");
                    sql.append("WHERE ant.codigoanterior = " + i_produto.id + " ");

                    rst = stm2.executeQuery(sql.toString());

                    while (rst.next()) {

                        sql = new StringBuilder();
                        sql.append("UPDATE produtocomplemento SET ");
                        sql.append("custosemimposto = " + oComplemento.custoSemImposto + ", ");
                        sql.append("custocomimposto = " + oComplemento.custoComImposto + " ");
                        sql.append("where id_produto = " + rst.getInt("id_produto") + " ");
                        sql.append("and id_loja = " + id_loja + ";");

                        sql.append("UPDATE implantacao.codigoanterior SET ");
                        sql.append("custosemimposto = " + oComplemento.custoSemImposto + ", ");
                        sql.append("custocomimposto = " + oComplemento.custoComImposto + " ");
                        sql.append("WHERE codigoatual = " + rst.getInt("id_produto") + " ");
                        sql.append("AND id_loja = " + id_loja + ";");

                        stm.execute(sql.toString());
                    }
                }

                ProgressBar.next();
            }

            stm2.close();
            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void alterarEstoqueProdutoMilenio(List<ProdutoVO> v_produto, int id_loja) throws Exception {
        Statement stm = null, stm2 = null;
        ResultSet rst = null;
        StringBuilder sql = null;

        try {

            Conexao.begin();

            stm = Conexao.createStatement();
            stm2 = Conexao.createStatement();

            ProgressBar.setMaximum(v_produto.size());

            ProgressBar.setStatus("Atualizando dados produto...Estoque...");

            for (ProdutoVO i_produto : v_produto) {
                for (ProdutoComplementoVO oComplemento : i_produto.vComplemento) {

                    sql = new StringBuilder();
                    sql.append("SELECT p.id FROM produto p ");
                    sql.append("INNER JOIN implantacao.codigoanterior ant ");
                    sql.append("ON ant.codigoatual = p.id ");
                    sql.append("WHERE ant.codigoanterior = " + i_produto.id + " ");

                    rst = stm.executeQuery(sql.toString());

                    while (rst.next()) {

                        sql = new StringBuilder();
                        sql.append("UPDATE produtocomplemento SET ");
                        sql.append("estoque = " + oComplemento.estoque + ", ");
                        sql.append("estoqueminimo = " + oComplemento.estoqueMinimo + ", ");
                        sql.append("estoquemaximo = " + oComplemento.estoqueMaximo + " ");
                        sql.append("where id_produto = " + rst.getInt("id") + " ");
                        sql.append("and id_loja = " + id_loja + ";");

                        sql.append("UPDATE implantacao.codigoanterior SET ");
                        sql.append("estoque = " + oComplemento.estoque + " ");
                        sql.append("WHERE codigoatual = " + rst.getInt("id") + ";");

                        stm2.execute(sql.toString());
                    }
                }

                ProgressBar.next();
            }

            stm.close();
            stm2.close();;
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void alterarSituacoCadastroProdutoMilenio(List<ProdutoVO> v_produto, int id_loja) throws Exception {
        Statement stm = null, stm2 = null;
        ResultSet rst = null;
        StringBuilder sql = null;

        try {

            Conexao.begin();

            stm = Conexao.createStatement();
            stm2 = Conexao.createStatement();

            ProgressBar.setMaximum(v_produto.size());

            ProgressBar.setStatus("Atualizando dados produto...situação cadastro...");

            for (ProdutoVO i_produto : v_produto) {
                for (ProdutoComplementoVO oComplemento : i_produto.vComplemento) {

                    sql = new StringBuilder();
                    sql.append("SELECT p.id as id_produto FROM produto p ");
                    sql.append("INNER JOIN implantacao.codigoanterior ant ");
                    sql.append("ON ant.codigoatual = p.id ");
                    sql.append("WHERE ant.codigoanterior = " + i_produto.id + " ");

                    rst = stm2.executeQuery(sql.toString());

                    while (rst.next()) {

                        sql = new StringBuilder();
                        sql.append("UPDATE produtocomplemento SET ");
                        sql.append("id_situacaocadastro = " + oComplemento.idSituacaoCadastro + " ");
                        sql.append("where id_produto = " + rst.getInt("id_produto") + " ");
                        sql.append("and id_loja = " + id_loja + ";");

                        stm.execute(sql.toString());
                    }
                }

                ProgressBar.next();
            }

            stm2.close();
            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void adicionarEanProdutoMilenio(List<ProdutoVO> v_produto) throws Exception {
        Statement stm = null;
        ResultSet rst = null, rst2 = null, rst3 = null;
        StringBuilder sql, sqlVerif = null;
        Utils util = new Utils();
        try {
            Conexao.begin();

            stm = Conexao.createStatement();
            boolean continua = false;
            long idProduto;
            int tipoEmbalagem;
            long CodigoBarras = 0;
            ProgressBar.setMaximum(v_produto.size());

            ProgressBar.setStatus("Atualizando dados produto...Código Barras...");

            for (ProdutoVO i_produto : v_produto) {

                if (i_produto.idDouble > 0) {
                    idProduto = (long) i_produto.idDouble;
                } else {
                    idProduto = i_produto.id;
                }

                for (ProdutoAutomacaoVO i_produtoAutomacao : i_produto.getvAutomacao()) {
                    continua = false;
                    sql = new StringBuilder();
                    sql.append("SELECT * FROM produtoautomacao ");
                    sql.append("WHERE codigobarras = " + i_produtoAutomacao.getCodigoBarras() + ";");

                    rst = stm.executeQuery(sql.toString());

                    if (!rst.next()) {

                        sql = new StringBuilder();
                        sql.append("SELECT p.id, p.id_tipoembalagem, p.pesavel FROM produto p ");
                        sql.append("inner join implantacao.codigoanterior ant on ");
                        sql.append("    ant.codigoatual=p.id ");
                        sql.append("WHERE ant.codigoanterior = " + i_produto.id + " ");

                        rst2 = stm.executeQuery(sql.toString());

                        if (rst2.next()) {
                            idProduto = rst2.getInt("id");
                            tipoEmbalagem = rst2.getInt("id_tipoembalagem");
                            if ((rst2.getInt("id_tipoembalagem") == 4)
                                    || (rst2.getBoolean("pesavel"))) {
                                sqlVerif = new StringBuilder();
                                sqlVerif.append("SELECT * FROM produtoautomacao ");
                                sqlVerif.append("WHERE codigobarras = " + idProduto + ";");
                                rst3 = stm.executeQuery(sqlVerif.toString());
                                if (!rst3.next()) {
                                    continua = true;
                                    CodigoBarras = (long) idProduto;
                                }
                            } else if (i_produtoAutomacao.codigoBarras == -1) {
                                CodigoBarras = util.gerarEan13(idProduto, true);
                                sqlVerif = new StringBuilder();
                                sqlVerif.append("SELECT * FROM produtoautomacao ");
                                sqlVerif.append("WHERE codigobarras = " + CodigoBarras + ";");
                                rst3 = stm.executeQuery(sqlVerif.toString());
                                if (!rst3.next()) {
                                    continua = true;
                                }
                            } else {
                                continua = true;
                                CodigoBarras = i_produtoAutomacao.codigoBarras;
                            }

                            if (continua) {
                                sql = new StringBuilder();
                                sql.append("INSERT INTO produtoautomacao ( ");
                                sql.append("id_produto, codigobarras, qtdembalagem, id_tipoembalagem) ");
                                sql.append("VALUES ( ");
                                sql.append(idProduto + ", ");
                                sql.append(CodigoBarras + ", ");
                                sql.append(i_produtoAutomacao.qtdEmbalagem + ", ");
                                sql.append(tipoEmbalagem + " ); ");
                                stm.execute(sql.toString());
                            }
                        }
                    }
                }
                ProgressBar.next();
            }

            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }
    //************** FIM SISTEMA MILENIO *****************************

    //************** SISTEMA JMASTER******************************  
    public void adicionarEanProdutoJMaster(List<ProdutoVO> v_produto) throws Exception {
        Statement stm = null, stm2 = null;
        ResultSet rst = null, rst2 = null;
        StringBuilder sql = null;
        Utils util = new Utils();
        long codigoBarras;

        try {
            Conexao.begin();

            stm = Conexao.createStatement();
            stm2 = Conexao.createStatement();

            ProgressBar.setMaximum(v_produto.size());

            ProgressBar.setStatus("Atualizando dados produto...Código Barras...");

            for (ProdutoVO i_produto : v_produto) {
                for (ProdutoAutomacaoVO i_produtoAutomacao : i_produto.vAutomacao) {

                    sql = new StringBuilder();
                    sql.append("select p.id, p.id_tipoembalagem, ant.e_balanca ");
                    sql.append("from produto p ");
                    sql.append("inner join implantacao.codigoanterior ant ");
                    sql.append("on ant.codigoatual = p.id ");
                    sql.append("where ant.codigoanterior = " + i_produto.id);

                    rst = stm.executeQuery(sql.toString());

                    if (rst.next()) {

                        if (rst.getBoolean("e_balanca") == true) {
                            sql = new StringBuilder();
                            sql.append("INSERT INTO produtoautomacao ( ");
                            sql.append("id_produto, codigobarras, qtdembalagem, id_tipoembalagem) ");
                            sql.append("VALUES ( ");
                            sql.append(rst.getInt("id") + ", ");
                            sql.append(rst.getInt("id") + ", ");
                            sql.append("1, ");
                            sql.append(rst.getInt("id_tipoembalagem") + " ");

                            sql.append(");");

                            stm.execute(sql.toString());
                        } else {

                            if ("73905".equals(rst.getString("id"))) {
                                JOptionPane.showMessageDialog(null, "aqui");
                            }

                            if (String.valueOf(i_produtoAutomacao.codigoBarras).length() >= 7) {

                                sql = new StringBuilder();
                                sql.append("select codigobarras from produtoautomacao ");
                                sql.append("where codigobarras = " + i_produtoAutomacao.codigoBarras);

                                rst2 = stm2.executeQuery(sql.toString());

                                if (!rst2.next()) {
                                    sql = new StringBuilder();
                                    sql.append("INSERT INTO produtoautomacao ( ");
                                    sql.append("id_produto, codigobarras, qtdembalagem, id_tipoembalagem) ");
                                    sql.append("VALUES ( ");
                                    sql.append(rst.getInt("id") + ", ");
                                    sql.append(i_produtoAutomacao.codigoBarras + ", ");
                                    sql.append(i_produtoAutomacao.qtdEmbalagem + ", ");
                                    sql.append(rst.getInt("id_tipoembalagem") + " ");

                                    sql.append(");");

                                    stm2.execute(sql.toString());
                                }
                            }
                        }
                    }
                }
                ProgressBar.next();
            }

            ProgressBar.setStatus("Inserindo código de barras para produtos que não tem...");

            sql = new StringBuilder();
            sql.append("select id, id_tipoembalagem from produto ");
            sql.append("where id not in (select id_produto from produtoautomacao) ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                if (rst.getInt("id") >= 10000) {
                    codigoBarras = util.gerarEan13(rst.getInt("id"), true);
                } else {
                    codigoBarras = util.gerarEan13(rst.getInt("id"), false);
                }

                sql = new StringBuilder();
                sql.append("INSERT INTO produtoautomacao ( ");
                sql.append("id_produto, codigobarras, qtdembalagem, id_tipoembalagem) ");
                sql.append("VALUES ( ");
                sql.append(rst.getInt("id") + ", ");
                sql.append(codigoBarras + ", ");
                sql.append("1, ");
                sql.append(rst.getInt("id_tipoembalagem") + " ");

                sql.append(");");

                stm2.execute(sql.toString());

                ProgressBar.next();
            }

            stm.close();
            stm2.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void alterarCustoProdutoJMaster(List<ProdutoVO> v_produto, int id_loja) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;

        try {

            Conexao.begin();

            stm = Conexao.createStatement();

            ProgressBar.setMaximum(v_produto.size());

            ProgressBar.setStatus("Atualizando dados produto...Custo...");

            for (ProdutoVO i_produto : v_produto) {
                for (ProdutoComplementoVO oComplemento : i_produto.vComplemento) {

                    sql = new StringBuilder();
                    sql.append("SELECT p.id FROM produto p ");
                    sql.append("INNER JOIN implantacao.codigoanterior ant ");
                    sql.append("ON ant.codigoatual = p.id ");
                    sql.append("WHERE ant.codigoanterior = " + i_produto.id + " ");

                    rst = stm.executeQuery(sql.toString());

                    if (rst.next()) {

                        sql = new StringBuilder();
                        sql.append("UPDATE produtocomplemento SET ");
                        sql.append("custosemimposto = " + oComplemento.custoSemImposto + ", ");
                        sql.append("custocomimposto = " + oComplemento.custoComImposto + " ");
                        sql.append("where id_produto = " + rst.getInt("id") + " ");
                        sql.append("and id_loja = " + id_loja + ";");

                        sql.append("UPDATE implantacao.codigoanterior SET ");
                        sql.append("custosemimposto = " + oComplemento.custoSemImposto + ", ");
                        sql.append("custocomimposto = " + oComplemento.custoComImposto + " ");
                        sql.append("WHERE codigoatual = " + rst.getInt("id") + " ");
                        sql.append("AND id_loja = " + id_loja + ";");

                        stm.execute(sql.toString());
                    }
                }

                ProgressBar.next();
            }

            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void alterarPrecoProdutoJMaster(List<ProdutoVO> v_produto, int id_loja) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;

        try {

            Conexao.begin();

            stm = Conexao.createStatement();

            ProgressBar.setMaximum(v_produto.size());

            ProgressBar.setStatus("Atualizando dados produto...Preço...");

            for (ProdutoVO i_produto : v_produto) {
                for (ProdutoComplementoVO oComplemento : i_produto.vComplemento) {

                    sql = new StringBuilder();
                    sql.append("SELECT p.id FROM produto p ");
                    sql.append("INNER JOIN implantacao.codigoanterior ant ");
                    sql.append("ON ant.codigoatual = p.id ");
                    sql.append("WHERE ant.codigoanterior = " + i_produto.id + " ");

                    rst = stm.executeQuery(sql.toString());

                    if (rst.next()) {

                        sql = new StringBuilder();
                        sql.append("UPDATE produtocomplemento SET ");
                        sql.append("precovenda = " + oComplemento.precoVenda + ", ");
                        sql.append("precodiaseguinte = " + oComplemento.precoDiaSeguinte + " ");
                        sql.append("where id_produto = " + rst.getInt("id") + " ");
                        sql.append("and id_loja = " + id_loja + ";");

                        sql.append("UPDATE implantacao.codigoanterior SET ");
                        sql.append("precovenda = " + oComplemento.precoVenda + " ");
                        sql.append("WHERE codigoatual = " + rst.getInt("id") + " ");
                        sql.append("AND id_loja = " + id_loja + ";");

                        stm.execute(sql.toString());
                    }
                }

                ProgressBar.next();
            }

            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void alterarSituacoCadastroProdutoJMaster(List<ProdutoVO> v_produto, int id_loja) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;

        try {

            Conexao.begin();

            stm = Conexao.createStatement();

            ProgressBar.setMaximum(v_produto.size());

            ProgressBar.setStatus("Atualizando dados produto...situação cadastro...");

            for (ProdutoVO i_produto : v_produto) {
                for (ProdutoComplementoVO oComplemento : i_produto.vComplemento) {

                    sql = new StringBuilder();
                    sql.append("SELECT p.id FROM produto p ");
                    sql.append("INNER JOIN implantacao.codigoanterior ant ");
                    sql.append("ON ant.codigoatual = p.id ");
                    sql.append("WHERE ant.codigoanterior = " + i_produto.id + ";");

                    rst = stm.executeQuery(sql.toString());

                    if (rst.next()) {

                        sql = new StringBuilder();
                        sql.append("UPDATE produtocomplemento SET ");
                        sql.append("id_situacaocadastro = " + oComplemento.idSituacaoCadastro + " ");
                        sql.append("where id_produto = " + rst.getInt("id") + " ");
                        sql.append("and id_loja = " + id_loja + ";");

                        stm.execute(sql.toString());
                    }
                }

                ProgressBar.next();
            }

            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    /**
     * ************ SISTEMA JMASTER*****************************
     */
    //************** SISTEMA FORTEMIX*****************************
    public void acertarPrecoCustoForteMix(List<ProdutoVO> v_produto) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;

        File f = new File("C:\\svn\\repositorioImplantacao\\Importacoes_OFFICIAL\\Meio a Meio - Comprebem\\scripts\\update_preco_loja2.txt");
        FileWriter fw = new FileWriter(f);
        BufferedWriter bw = new BufferedWriter(fw);

        try {
            Conexao.begin();

            stm = Conexao.createStatement();

            ProgressBar.setMaximum(v_produto.size());

            ProgressBar.setStatus("Atualizando dados produto...Preço...");

            for (ProdutoVO i_produto : v_produto) {
                for (ProdutoComplementoVO oComplemento : i_produto.vComplemento) {

                    sql = new StringBuilder();
                    //sql.append("select id_produto, codigobarras from produtoautomacao ");
                    //sql.append("where codigobarras = " + i_produto.codigoBarras);
                    sql.append("select p.id from produto p ");
                    sql.append("inner join implantacao.codigoanterior ant ");
                    sql.append("on ant.codigoatual = p.id ");
                    sql.append("where ant.codigoanterior = " + i_produto.id);

                    rst = stm.executeQuery(sql.toString());

                    if (rst.next()) {

                        sql = new StringBuilder();
                        sql.append("update produtocomplemento set ");
                        sql.append("precovenda = " + oComplemento.precoVenda + ",");
                        sql.append("precodiaseguinte = " + oComplemento.precoVenda + " ");
                        //sql.append("custosemimposto = "+oComplemento.custoSemImposto+",");
                        //sql.append("custocomimposto = "+oComplemento.custoComImposto+" ");
                        sql.append("where id_produto = " + rst.getInt("id") + " ");
                        sql.append("and id_loja = " + oComplemento.idLoja + ";");

                        bw.write(sql.toString());
                        bw.newLine();

                        //stm.execute(sql.toString());
                    }
                }

                ProgressBar.next();
            }

            bw.flush();
            bw.close();
            Conexao.commit();
            stm.close();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void acertarEstoqueForteMix(List<ProdutoVO> v_produto) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;

        File f = new File("C:\\svn\\repositorioImplantacao\\Importacoes_OFFICIAL\\Meio a Meio - Comprebem\\scripts\\update_estoque.txt");
        FileWriter fw = new FileWriter(f);
        BufferedWriter bw = new BufferedWriter(fw);

        try {
            Conexao.begin();

            stm = Conexao.createStatement();

            ProgressBar.setMaximum(v_produto.size());

            ProgressBar.setStatus("Atualizando dados produto...Estoque...");

            for (ProdutoVO i_produto : v_produto) {
                for (ProdutoComplementoVO oComplemento : i_produto.vComplemento) {

                    sql = new StringBuilder();
                    //sql.append("select id_produto, codigobarras from produtoautomacao ");
                    //sql.append("where codigobarras = " + i_produto.codigoBarras);
                    sql.append("select p.id from produto p ");
                    sql.append("inner join implantacao.codigoanterior ant ");
                    sql.append("on ant.codigoatual = p.id ");
                    sql.append("where ant.codigoanterior = " + i_produto.id);

                    rst = stm.executeQuery(sql.toString());

                    if (rst.next()) {

                        sql = new StringBuilder();
                        sql.append("update produtocomplemento set ");
                        sql.append("estoque = " + oComplemento.estoque + ", ");
                        sql.append("custocomimposto = " + oComplemento.custoComImposto + ", ");
                        sql.append("custosemimposto = " + oComplemento.custoSemImposto + " ");
                        //sql.append("estoqueminimo = "+oComplemento.estoqueMinimo+" ");
                        sql.append("where id_produto = " + rst.getInt("id") + " ");
                        sql.append("and id_loja = " + oComplemento.idLoja + ";");

                        //stm.execute(sql.toString());
                        bw.write(sql.toString());
                        bw.newLine();
                    }
                }

                ProgressBar.next();
            }

            bw.flush();
            bw.close();
            Conexao.commit();
            stm.close();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    /**
     * ************ SISTEMA FORTEMIX*****************************
     */
    /**
     * ************ SISTEMA GCF (LOJÃO DO CABELEREIRO) *********
     */
    public void alterarCustoLojaoCabelereiro(List<ProdutoVO> v_produto, int idLoja) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        File f = new File("C:\\vr\\importacao\\04042016\\update_custo_lojaoCabelereiro" + idLoja + ".txt");
        FileWriter fw = new FileWriter(f);
        BufferedWriter bw = new BufferedWriter(fw);

        try {

            Conexao.begin();

            stm = Conexao.createStatement();

            ProgressBar.setStatus("Atualizando Custo na Loja " + idLoja + "...");
            ProgressBar.setMaximum(v_produto.size());

            for (ProdutoVO i_produto : v_produto) {
                for (ProdutoComplementoVO oComplemento : i_produto.vComplemento) {

                    sql = new StringBuilder();
                    sql.append("select p.id from produto p ");
                    sql.append("inner join implantacao.codigoanterior ant on ant.codigoatual = p.id ");
                    sql.append("inner join produtocomplemento pc on pc.id_produto = p.id ");
                    sql.append("where ant.codigoanterior = " + i_produto.id + " ");
                    sql.append("and pc.id_loja = " + idLoja + " ");
                    sql.append("and pc.dataultimaentrada is null ");

                    rst = stm.executeQuery(sql.toString());

                    if (rst.next()) {
                        sql = new StringBuilder();
                        sql.append("update produtocomplemento set ");
                        sql.append("custocomimposto = " + oComplemento.custoComImposto + ", ");
                        sql.append("custosemimposto = " + oComplemento.custoSemImposto + " ");
                        sql.append("where id_produto = " + rst.getInt("id") + " ");
                        sql.append("and id_loja = " + idLoja + ";");

                        bw.write(sql.toString());
                        bw.newLine();
                    }

                    ProgressBar.next();
                }
            }

            bw.flush();
            bw.close();
            stm.close();
            Conexao.commit();

        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    /**
     * ************ SISTEMA SYSPDV *****************************
     */
    public void alterarEstoqueSysPdvCarnauba(List<ProdutoVO> v_produto, int idLoja) throws Exception {
        StringBuilder sql = null;
        Statement stm = null, stm2 = null, stm3 = null;
        ResultSet rst = null, rst2 = null, rst3 = null;
        //File f = new File("C:\\Users\\Administrador\\Desktop\\vr\\dist\\dist\\importacao\\loja5\\update_estoque_loja"+idLoja+".txt");
        //File f = new File("C:\\svn\\repositorioImplantacao\\Importacoes_OFFICIAL\\Carnauba (Milenio)\\Loja 5\\030516\\xls\\update_estoque_loja"+idLoja+".txt");
        //FileWriter fw = new FileWriter(f);
        //BufferedWriter bw = new BufferedWriter(fw);
        try {

            Conexao.begin();

            stm = Conexao.createStatement();
            stm2 = Conexao.createStatement();
            stm3 = Conexao.createStatement();

            ProgressBar.setStatus("Atualizando Estoque Loja " + idLoja + "...");
            ProgressBar.setMaximum(v_produto.size());

            for (ProdutoVO i_produto : v_produto) {
                for (ProdutoComplementoVO oComplemento : i_produto.vComplemento) {

                    sql = new StringBuilder();
                    sql.append("select p.id from produto p ");
                    sql.append("inner join implantacao.codigoanterior ant on ant.codigoatual = p.id ");

                    if (i_produto.idDouble > 0) {
                        sql.append("where ant.codigoanterior = " + i_produto.idDouble + " ");
                    } else {
                        sql.append("where ant.codigoanterior = " + i_produto.id + " ");
                    }

                    sql.append("and ant.id_loja = " + idLoja);

                    rst = stm.executeQuery(sql.toString());

                    if (rst.next()) {
                        sql = new StringBuilder();
                        sql.append("update produtocomplemento set ");
                        sql.append("estoque = " + oComplemento.estoque + " ");
                        sql.append("where id_produto = " + rst.getInt("id") + " ");
                        sql.append("and id_loja = " + idLoja + ";");

                        stm.execute(sql.toString());

                        //bw.write(sql.toString());
                        //bw.newLine();
                    } else {

                        if (i_produto.codigoBarras != -1) {

                            sql = new StringBuilder();
                            sql.append("select id_produto from produtoautomacao ");
                            sql.append("where codigobarras = " + i_produto.codigoBarras);

                            rst2 = stm2.executeQuery(sql.toString());

                            if (rst2.next()) {
                                sql = new StringBuilder();
                                sql.append("update produtocomplemento set ");
                                sql.append("estoque = " + oComplemento.estoque + " ");
                                sql.append("where id_produto = " + rst2.getInt("id_produto") + " ");
                                sql.append("and id_loja = " + idLoja + ";");

                                stm2.execute(sql.toString());
                                //bw.write(sql.toString());
                                //bw.newLine();

                            } else {

                                if (!i_produto.descricaoCompleta.trim().isEmpty()) {
                                    sql = new StringBuilder();
                                    sql.append("select id from produto ");
                                    sql.append("where descricaocompleta = '" + i_produto.descricaoCompleta + "'");

                                    rst3 = stm3.executeQuery(sql.toString());

                                    if (rst3.next()) {

                                        sql = new StringBuilder();
                                        sql.append("update produtocomplemento set ");
                                        sql.append("estoque = " + oComplemento.estoque + " ");
                                        sql.append("where id_produto = " + rst3.getInt("id") + " ");
                                        sql.append("and id_loja = " + idLoja + ";");

                                        stm3.execute(sql.toString());
                                        //bw.write(sql.toString());
                                        //bw.newLine();
                                    }
                                }
                            }
                        }
                    }
                }
                ProgressBar.next();
            }

            //bw.flush();
            //bw.close();
            stm.close();
            stm2.close();
            stm3.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void alterarPisCofinsProdutoSysPdv(List<ProdutoVO> v_produto, int idLoja) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;

        try {
            Conexao.begin();
            stm = Conexao.createStatement();
            ProgressBar.setMaximum(v_produto.size());
            ProgressBar.setStatus("Atualizando dados produto...PisCofins e Natureza Receita...");
            for (ProdutoVO i_produto : v_produto) {
                //for (CodigoAnteriorVO i_codigoAnterior : i_produto.vCodigoAnterior) {
                sql = new StringBuilder();
                sql.append("SELECT p.id FROM produto p ");
                sql.append("INNER JOIN implantacao.codigoanterior ant ");
                sql.append("ON ant.codigoatual = p.id ");
                if (i_produto.idDouble > 0) {
                    IdProduto = i_produto.idDouble;
                    sql.append("where CAST(ant.codigoanterior AS NUMERIC(14,0)) = CAST(" + IdProduto + " AS NUMERIC(14,0)) ");
                } else {
                    IdProduto = i_produto.id;
                    sql.append("where ant.codigoanterior = " + IdProduto);
                }

                sql.append(" and ant.id_loja = " + idLoja);

                rst = stm.executeQuery(sql.toString());

                if (rst.next()) {
                    sql = new StringBuilder();
                    sql.append("update produto set ");
                    sql.append("id_tipopiscofins = " + i_produto.idTipoPisCofinsDebito + ", ");
                    sql.append("id_tipopiscofinscredito = " + i_produto.idTipoPisCofinsCredito + ", ");
                    sql.append("tiponaturezareceita = " + (i_produto.tipoNaturezaReceita == -1 ? null : i_produto.tipoNaturezaReceita) + " ");
                    sql.append("where id = " + rst.getInt("id") + ";");

                    sql.append("UPDATE implantacao.codigoanterior SET ");
                    sql.append("piscofinsdebito = " + i_produto.idTipoPisCofinsDebito + ", ");
                    sql.append("piscofinscredito = " + i_produto.idTipoPisCofinsCredito + ", ");
                    sql.append("naturezareceita = " + (i_produto.tipoNaturezaReceita == -1 ? null : i_produto.tipoNaturezaReceita) + " ");
                    sql.append("WHERE codigoatual = " + rst.getInt("id") + " ");
                    sql.append("and id_loja = " + idLoja + ";");
                    stm.execute(sql.toString());
                    //}
                }
                ProgressBar.next();
            }

            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void addCodigoBarrasSysPdv(List<ProdutoVO> v_produto) throws Exception {
        Statement stm = null, stm2 = null;
        ResultSet rst = null, rst2 = null;
        StringBuilder sql = null;

        try {

            Conexao.begin();

            stm = Conexao.createStatement();
            stm2 = Conexao.createStatement();

            ProgressBar.setMaximum(v_produto.size());

            ProgressBar.setStatus("Atualizando dados produto...Codigo Barra...");

            for (ProdutoVO i_produto : v_produto) {
                for (ProdutoAutomacaoVO oAutomacao : i_produto.vAutomacao) {

                    sql = new StringBuilder();
                    sql.append("SELECT p.id, p.id_tipoembalagem FROM produto p ");
                    sql.append("INNER JOIN implantacao.codigoanterior ant ");
                    sql.append("ON ant.codigoatual = p.id ");
                    sql.append("WHERE ant.codigoanterior = " + i_produto.idDouble + ";");

                    rst = stm.executeQuery(sql.toString());

                    if (rst.next()) {

                        sql = new StringBuilder();
                        sql.append("select * from produtoautomacao ");
                        sql.append("where codigobarras = " + oAutomacao.codigoBarras);

                        rst2 = stm2.executeQuery(sql.toString());

                        if (!rst2.next()) {
                            sql = new StringBuilder();
                            sql.append("insert into produtoautomacao (");
                            sql.append("id_produto, codigobarras, qtdembalagem, id_tipoembalagem) ");
                            sql.append("values (");
                            sql.append(rst.getInt("id") + ",");
                            sql.append(oAutomacao.codigoBarras + ",");
                            sql.append("1, ");
                            sql.append(rst.getInt("id_tipoembalagem") + ");");

                            stm.execute(sql.toString());
                        }
                    }
                }

                ProgressBar.next();
            }

            stm.close();
            stm2.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void alterarCustoProdutoSysPdv(List<ProdutoVO> v_produto, int id_loja) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;

        //File f = new File("C:\\vr\\implantacao\\scripts\\update_custo.txt");
        //FileWriter fw = new FileWriter(f);
        //BufferedWriter bw = new BufferedWriter(fw);
        try {

            Conexao.begin();

            stm = Conexao.createStatement();

            ProgressBar.setMaximum(v_produto.size());

            ProgressBar.setStatus("Atualizando dados produto...Custo...");

            for (ProdutoVO i_produto : v_produto) {
                for (ProdutoComplementoVO oComplemento : i_produto.vComplemento) {

                    sql = new StringBuilder();
                    sql.append("SELECT p.id FROM produto p ");
                    sql.append("INNER JOIN implantacao.codigoanterior ant ");
                    sql.append("ON ant.codigoatual = p.id ");
                    sql.append("WHERE cast(ant.codigoanterior as numeric(14,0)) = cast(" + i_produto.idDouble + " as numeric(14,0)) ");

                    rst = stm.executeQuery(sql.toString());

                    if (rst.next()) {

                        sql = new StringBuilder();
                        sql.append("UPDATE produtocomplemento SET ");
                        sql.append("custosemimposto = " + oComplemento.custoSemImposto + ", ");
                        sql.append("custocomimposto = " + oComplemento.custoComImposto + " ");
                        sql.append("where id_produto = " + rst.getInt("id") + " ");
                        sql.append("and id_loja = " + id_loja + ";");

                        sql.append("UPDATE implantacao.codigoanterior SET ");
                        sql.append("custosemimposto = " + oComplemento.custoSemImposto + ", ");
                        sql.append("custocomimposto = " + oComplemento.custoComImposto + " ");
                        sql.append("WHERE codigoatual = " + rst.getInt("id") + " ");
                        sql.append("AND id_loja = " + id_loja + ";");

                        //bw.write(sql.toString());
                        ///bw.newLine();
                        stm.execute(sql.toString());
                    }
                }

                ProgressBar.next();
            }

            //bw.flush();
            //bw.close();
            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void alterarPrecoProdutoSysPdv(List<ProdutoVO> v_produto, int id_loja) throws Exception {
        Statement stm = null, stm2 = null;
        ResultSet rst = null, rst2 = null;
        StringBuilder sql = null;

        /*File f = new File("C:\\vr\\implantacao\\scripts\\update_preco.txt");
         FileWriter fw = new FileWriter(f);
         BufferedWriter bw = new BufferedWriter(fw);*/
        try {

            Conexao.begin();

            stm = Conexao.createStatement();
            stm2 = Conexao.createStatement();

            ProgressBar.setMaximum(v_produto.size());

            ProgressBar.setStatus("Atualizando dados produto...Preço...");

            for (ProdutoVO i_produto : v_produto) {
                for (ProdutoComplementoVO oComplemento : i_produto.vComplemento) {

                    sql = new StringBuilder();
                    sql.append("SELECT p.id FROM produto p ");
                    sql.append("INNER JOIN implantacao.codigoanterior ant ");
                    sql.append("ON ant.codigoatual = p.id ");
                    sql.append("WHERE cast(ant.codigoanterior as numeric(14,0)) = cast(" + i_produto.idDouble + " as numeric(14,0)) ");

                    rst = stm.executeQuery(sql.toString());

                    if (rst.next()) {

                        sql = new StringBuilder();
                        sql.append("select id_produto from oferta ");
                        sql.append("where id_produto = " + rst.getInt("id") + ";");

                        rst2 = stm2.executeQuery(sql.toString());

                        if (!rst2.next()) {
                            sql = new StringBuilder();
                            sql.append("UPDATE produtocomplemento SET ");
                            sql.append("precovenda = " + oComplemento.precoVenda + ", ");
                            sql.append("precodiaseguinte = " + oComplemento.precoDiaSeguinte + " ");
                            sql.append("where id_produto = " + rst.getInt("id") + " ");
                            sql.append("and id_loja = " + id_loja + ";");

                            sql.append("UPDATE implantacao.codigoanterior SET ");
                            sql.append("precovenda = " + oComplemento.precoVenda + " ");
                            sql.append("WHERE codigoatual = " + rst.getInt("id") + " ");
                            sql.append("AND id_loja = " + id_loja + ";");

                            stm.execute(sql.toString());
                            /*bw.write(sql.toString());
                             bw.newLine();*/
                        }
                    }
                }

                ProgressBar.next();
            }

            /*bw.flush();
             bw.newLine();*/
            stm2.close();
            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void alterarEstoqueProdutoSysPdv(List<ProdutoVO> v_produto, int id_loja) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;

        //File f = new File("C:\\vr\\implantacao\\scripts\\update_estoque.txt");
        //FileWriter fw = new FileWriter(f);
        //BufferedWriter bw = new BufferedWriter(fw);
        try {

            Conexao.begin();

            stm = Conexao.createStatement();

            ProgressBar.setMaximum(v_produto.size());

            ProgressBar.setStatus("Atualizando dados produto...Estoque...");

            for (ProdutoVO i_produto : v_produto) {
                for (ProdutoComplementoVO oComplemento : i_produto.vComplemento) {

                    sql = new StringBuilder();
                    sql.append("SELECT p.id FROM produto p ");
                    sql.append("INNER JOIN implantacao.codigoanterior ant ");
                    sql.append("ON ant.codigoatual = p.id ");
                    sql.append("WHERE ant.codigoanterior = " + i_produto.idDouble + ";");

                    rst = stm.executeQuery(sql.toString());

                    if (rst.next()) {

                        sql = new StringBuilder();
                        sql.append("UPDATE produtocomplemento SET ");
                        sql.append("estoque = " + oComplemento.estoque + ", ");
                        sql.append("estoqueminimo = " + oComplemento.estoqueMinimo + ", ");
                        sql.append("estoquemaximo = " + oComplemento.estoqueMaximo + " ");
                        sql.append("where id_produto = " + rst.getInt("id") + " ");
                        sql.append("and id_loja = " + id_loja + ";");

                        sql.append("UPDATE implantacao.codigoanterior SET ");
                        sql.append("estoque = " + oComplemento.estoque + " ");
                        sql.append("WHERE codigoatual = " + rst.getInt("id") + ";");

                        stm.execute(sql.toString());
                        //bw.write(sql.toString());
                        //bw.newLine();
                    }
                }

                ProgressBar.next();
            }

            //bw.flush();
            //bw.close();
            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void acertarFamiliaProdutoSysPdv(List<ProdutoVO> v_produto) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;

        try {

            Conexao.begin();

            stm = Conexao.createStatement();

            ProgressBar.setMaximum(v_produto.size());

            ProgressBar.setStatus("Atualizando dados produto...Familia Produto...");

            for (ProdutoVO i_produto : v_produto) {

                sql = new StringBuilder();
                sql.append("SELECT p.id FROM produto p ");
                sql.append("INNER JOIN implantacao.codigoanterior ant ");
                sql.append("ON ant.codigoatual = p.id ");
                sql.append("WHERE ant.codigoanterior = " + ((long) i_produto.idDouble) + ";");

                rst = stm.executeQuery(sql.toString());

                if (rst.next()) {

                    sql = new StringBuilder();
                    sql.append("update produto set ");
                    sql.append("id_familiaproduto = " + (i_produto.idFamiliaProduto == -1 ? null : i_produto.idFamiliaProduto) + " ");
                    sql.append("where id = " + rst.getInt("id") + ";");

                    stm.execute(sql.toString());

                }

                ProgressBar.next();
            }

            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void acertarProdutoSysPdvCARNAUBA(List<ProdutoVO> v_produto, int idLoja, List<LojaVO> vLoja) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;

        try {

            Conexao.begin();

            stm = Conexao.createStatement();

            ProgressBar.setStatus("Importando dados...Produtos...");
            ProgressBar.setMaximum(v_produto.size());

            for (ProdutoVO i_produto : v_produto) {
                for (ProdutoAutomacaoVO i_automacao : i_produto.vAutomacao) {

                    if (String.valueOf(i_automacao.codigoBarras).length() >= 7) {

                        sql = new StringBuilder();
                        sql.append("select * from produtoautomacao ");
                        sql.append("where codigobarras = " + i_automacao.codigoBarras);

                        rst = stm.executeQuery(sql.toString());

                        if (rst.next()) {
                            salvar(v_produto, idLoja, true, vLoja, false, 0);
                        } else {
                            salvar(v_produto, idLoja, false, vLoja, false, 0);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * ****************ALTERAÇÕES SYSPDV ***********************
     */
    public void alterarTipoEmbalagemSysPdv(List<ProdutoVO> v_produto) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;

        try {
            Conexao.begin();

            stm = Conexao.createStatement();

            ProgressBar.setMaximum(v_produto.size());

            ProgressBar.setStatus("Atualizando dados produto...Tipo Embalagem...");

            for (ProdutoVO i_produto : v_produto) {

                sql = new StringBuilder();
                sql.append("select p.id from produto p ");
                sql.append("inner join implantacao.codigoanterior ant ");
                sql.append("on ant.codigoatual = p.id ");

                if (i_produto.id > 0) {
                    sql.append("where ant.codigoanterior = " + i_produto.id);
                } else {
                    sql.append("where ant.codigoanterior = " + i_produto.idDouble);
                }

                rst = stm.executeQuery(sql.toString());

                if (rst.next()) {
                    sql = new StringBuilder();
                    sql.append("update produto set ");

                    if (alterarQtdEmbalagem) {
                        sql.append("id_tipoembalagem = " + i_produto.idTipoEmbalagem + ", ");
                        sql.append("qtdembalagem = " + i_produto.qtdEmbalagem + ", ");
                        sql.append("pesavel = " + i_produto.pesavel + ", ");
                        sql.append("aceitamultiplicacaopdv = " + i_produto.aceitaMultiplicacaoPdv + " ");
                    } else {
                        sql.append("id_tipoembalagem = " + i_produto.idTipoEmbalagem + " ");
                    }

                    sql.append("where id = " + rst.getInt("id") + ";");

                    sql.append("update produtoautomacao set ");

                    if (alterarQtdEmbalagem) {
                        sql.append("id_tipoembalagem = " + i_produto.idTipoEmbalagem + ", ");
                        sql.append("qtdembalagem = " + i_produto.qtdEmbalagem + " ");
                    } else {
                        sql.append("id_tipoembalagem = " + i_produto.idTipoEmbalagem + " ");
                    }

                    //sql.append("id_tipoembalagem = " + i_produto.idTipoEmbalagem + " ");
                    sql.append("where id_produto = " + rst.getString("id") + ";");

                    stm.execute(sql.toString());
                }

                ProgressBar.next();
            }

            stm.close();
            Conexao.commit();

        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    /**
     * ************ MÉTODOS COPIADOS DO VRADM
     *
     *******************
     * @param i_codigo
     * @return
     * @throws java.lang.Exception
     */
    /**
     * *
     * Sistema Interagem o codigo interno tem LETRA
     *
     * @param i_codigo
     * @return
     * @throws java.lang.Exception
     *
     */
    public int getIdByCodigoAnterior(String i_codigo) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        stm = Conexao.createStatement();
        rst = stm.executeQuery("select p.id from produto p "
                + "inner join implantacao.codigoanterior ant "
                + "on ant.codigoatual = p.id "
                + "where ant.codanterior = '" + i_codigo + "'");
        if (rst.next()) {
            return rst.getInt("id");
        } else {
            return -1;
        }
    }

    public long getBarrasByCodigoAnterior(long i_codigo) throws Exception {
        Statement stm = null;
        ResultSet rst = null;

        stm = Conexao.createStatement();
        rst = stm.executeQuery("select pa.codigobarras from produtoautomacao "
                + "inner join implantacao.codigoanterior ant "
                + "on ant.codigoatual = pa.id_produto "
                + "where ant.codigoanterior = " + i_codigo);
        if (rst.next()) {
            return rst.getLong("codigobarras");
        } else {
            return 0;
        }
    }

    public int getIdProduto(long i_codigo) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        int retorno = -1;

        stm = Conexao.createStatement();

        rst = stm.executeQuery("select p.id from produto p "
                + "inner join implantacao.codigoanterior ant on ant.codigoatual = p.id"
                + "where ant.codigoanterior = " + i_codigo);
        if (rst.next()) {
            retorno = rst.getInt("id");
        } else {
            retorno = -1;
        }

        if (retorno == -1) {
            rst = stm.executeQuery("select id_produto from produtoautomacao "
                    + "where codigobarras = " + i_codigo);
            if (rst.next()) {
                retorno = rst.getInt("id_produto");
            } else {
                retorno = -1;
            }
        }

        if (retorno == -1) {
            rst = stm.executeQuery("select id from produto "
                    + "where id = " + i_codigo);
            if (rst.next()) {
                retorno = rst.getInt("id");
            } else {
                retorno = -1;
            }
        }

        return retorno;
    }

    public int getIdProdutoCodigoBarras(long i_codigo) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        int retorno = -1;

        stm = Conexao.createStatement();

        rst = stm.executeQuery("select id_produto from produtoautomacao "
                + "where codigobarras = " + i_codigo);
        if (rst.next()) {
            retorno = rst.getInt("id_produto");
        } else {
            retorno = -1;
        }

        return retorno;
    }

    public int getIdProdutoCodigoAnterior(long i_codigo) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        int retorno = -1;

        stm = Conexao.createStatement();
        rst = stm.executeQuery("select p.id from produto p "
                + "inner join implantacao.codigoanterior ant on ant.codigoatual = p.id "
                + "where ant.codigoanterior = " + i_codigo);
        if (rst.next()) {
            retorno = rst.getInt("id");
        } else {
            retorno = -1;
        }

        /*if (retorno == -1) {
         rst = stm.executeQuery("select p.id from produto p "
         + "inner join implantacao.codigoanterior ant on ant.codigoatual = p.id "
         + "where ant.id = " + i_codigo);
         if (rst.next()) {
         retorno = rst.getInt("id");
         } else {
         retorno = -1;
         }
         }*/
        return retorno;
    }

    public int getIdCodigoBarrasAnterior(long i_codigobarras, String i_descricao) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        int retorno = -1;

        stm = Conexao.createStatement();

        rst = stm.executeQuery("select id_produto from produtoautomacao "
                + "where codigobarras = " + i_codigobarras);

        if (rst.next()) {
            retorno = rst.getInt("id_produto");
        } else {
            retorno = -1;
        }

        if (retorno == -1) {

            if (i_descricao.contains("     ")) {
                i_descricao = i_descricao.substring(0,
                        i_descricao.indexOf("     "));
            }

            rst = stm.executeQuery("select p.id from produto p "
                    + "where p.descricaocompleta = '" + i_descricao.trim() + "' ");

            if (rst.next()) {
                retorno = rst.getInt("id");
            } else {
                retorno = -1;
            }
        }

        if (retorno == -1) {
            if (i_descricao.contains("     ")) {
                i_descricao = i_descricao.substring(0,
                        i_descricao.indexOf("     "));
            }

            rst = stm.executeQuery("select p.id from produto p "
                    + "where p.descricaoreduzida = '" + i_descricao.trim() + "' ");

            if (rst.next()) {
                retorno = rst.getInt("id");
            } else {
                retorno = -1;
            }
        }

        if (retorno == -1) {
            rst = stm.executeQuery("select id from produto "
                    + "where id = " + i_codigobarras);

            if (rst.next()) {
                retorno = rst.getInt("id");
            } else {
                retorno = -1;
            }
        }

        if (retorno == -1) {
            rst = stm.executeQuery("select id_produto from produtoautomacao "
                    + "where substring(codigobarras::varchar, 1, length(codigobarras::varchar) -1) = '" + String.valueOf(i_codigobarras) + "'");
            if (rst.next()) {
                retorno = rst.getInt("id_produto");
            } else {
                retorno = -1;
            }
        }

        return retorno;
    }

    public long getIdCodigoBarrasAnterior2(long i_codigobarras, String i_descricao) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        long retorno = -1;

        stm = Conexao.createStatement();

        rst = stm.executeQuery("select codigobarras from produtoautomacao "
                + "where codigobarras = " + i_codigobarras);

        if (rst.next()) {
            retorno = rst.getLong("codigobarras");
        } else {
            retorno = -1;
        }

        if (retorno == -1) {
            if (i_descricao.contains("     ")) {
                i_descricao = i_descricao.substring(0,
                        i_descricao.indexOf("     "));
            }

            rst = stm.executeQuery("select pa.codigobarras from produto p "
                    + "inner join produtoautomacao pa on pa.id_produto = p.id "
                    + "where p.descricaocompleta = '" + i_descricao.trim() + "' ");

            if (rst.next()) {
                retorno = rst.getLong("codigobarras");
            } else {
                retorno = -1;
            }
        }

        if (retorno == -1) {
            if (i_descricao.contains("     ")) {
                i_descricao = i_descricao.substring(0,
                        i_descricao.indexOf("     "));
            }

            rst = stm.executeQuery("select pa.codigobarras from produto p "
                    + "inner join produtoautomacao pa on pa.id_produto = p.id "
                    + "where p.descricaoreduzida = '" + i_descricao.trim() + "' ");

            if (rst.next()) {
                retorno = rst.getLong("codigobarras");
            } else {
                retorno = -1;
            }
        }

        if (retorno == -1) {
            rst = stm.executeQuery("select codigobarras from produtoautomacao "
                    + "where substring(codigobarras::varchar, 1, length(codigobarras::varchar) -1) = '" + String.valueOf(i_codigobarras) + "'");
            if (rst.next()) {
                retorno = rst.getLong("codigobarras");
            } else {
                retorno = -1;
            }
        }

        return retorno;
    }

    public int getId(int i_id) throws Exception {
        Statement stm = null;
        ResultSet rst = null;

        stm = Conexao.createStatement();

        rst = stm.executeQuery("SELECT id FROM produto WHERE id = " + i_id);

        if (rst.next()) {
            return rst.getInt("id");
        } else {
            return -1;
        }
    }

    public int getId(long i_codigobarras) throws Exception {
        Statement stm = null;
        ResultSet rst = null;

        stm = Conexao.createStatement();

        rst = stm.executeQuery("SELECT id_produto FROM produtoautomacao WHERE codigobarras = " + i_codigobarras);

        if (rst.next()) {
            return rst.getInt("id_produto");
        } else {
            return -1;
        }
    }

    public int getIdAnterior(long i_codigoAnterior) throws Exception {
        Statement stm = null;
        ResultSet rst = null;

        stm = Conexao.createStatement();

        rst = stm.executeQuery("SELECT codigoatual FROM implantacao.codigoanterior WHERE codigoanterior = " + i_codigoAnterior);

        if (rst.next()) {
            return rst.getInt("codigoatual");
        } else {
            return -1;
        }
    }

    public int getIdAnterior(long i_codigoAnterior, int idLoja) throws Exception {
        Statement stm = null;
        ResultSet rst = null;

        stm = Conexao.createStatement();

        rst = stm.executeQuery("SELECT codigoatual FROM implantacao.codigoanterior WHERE codigoanterior = " + i_codigoAnterior + " "
                + "and id_loja = " + idLoja);

        if (rst.next()) {
            return rst.getInt("codigoatual");
        } else {
            return -1;
        }
    }

    public int getIdAnterior2(long i_codigobarras) throws Exception {
        Statement stm = null;
        ResultSet rst = null;

        stm = Conexao.createStatement();

        rst = stm.executeQuery("SELECT codigoatual FROM implantacao.codigoanterior "
                + "WHERE codigobalanca = cast(substring('" + i_codigobarras + "', 2, 5) as numeric)");

        if (rst.next()) {
            return rst.getInt("codigoatual");
        } else {
            return -1;
        }
    }

    public int getIdAnterior3(long i_codigobarras) throws Exception {
        Statement stm = null;
        ResultSet rst = null;

        stm = Conexao.createStatement();

        rst = stm.executeQuery("SELECT codigoatual FROM implantacao.codigoanterior2 "
                + "WHERE barras = " + i_codigobarras);

        if (rst.next()) {
            return rst.getInt("codigoatual");
        } else {
            return -1;
        }
    }

    public int getIdAnterior4(long i_codigobarras) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        int retorno = -1;

        stm = Conexao.createStatement();

        rst = stm.executeQuery("select * from implantacao.codigoanterior  "
                + "where cast(substring(cast(codigoanterior as varchar), 1, 5) as numeric) = cast(substring('" + i_codigobarras + "', 2, 5) as numeric)"
                + " and char_length(cast(codigoanterior as varchar)) = 6");

        if (rst.next()) {
            retorno = rst.getInt("codigoatual");
        } else {
            retorno = -1;
        }

        if (retorno == -1) {

            rst = stm.executeQuery("select * from implantacao.codigoanterior  "
                    + "where cast(substring(cast(codigoanterior as varchar), 1, 4) as numeric) = cast(substring('" + i_codigobarras + "', 2, 5) as numeric)"
                    + " and char_length(cast(codigoanterior as varchar)) = 5");

            if (rst.next()) {
                retorno = rst.getInt("codigoatual");
            } else {
                retorno = -1;
            }
        }

        if (retorno == -1) {
            rst = stm.executeQuery("select * from implantacao.codigoanterior  "
                    + "where cast(substring(cast(codigoanterior as varchar), 1, 3) as numeric) = cast(substring('" + i_codigobarras + "', 2, 5) as numeric)"
                    + " and char_length(cast(codigoanterior as varchar)) = 4");

            if (rst.next()) {
                retorno = rst.getInt("codigoatual");
            } else {
                retorno = -1;
            }
        }

        if (retorno == -1) {
            rst = stm.executeQuery("select * from implantacao.codigoanterior  "
                    + "where cast(substring(cast(codigoanterior as varchar), 1, 2) as numeric) = cast(substring('" + i_codigobarras + "', 2, 5) as numeric)"
                    + " and char_length(cast(codigoanterior as varchar)) = 3");

            if (rst.next()) {
                retorno = rst.getInt("codigoatual");
            } else {
                retorno = -1;
            }
        }

        return retorno;
    }
    
    public int getIdByCodAntEan(String i_codigobarras) throws Exception {
        Statement stm = null;
        ResultSet rst = null;

        stm = Conexao.createStatement();
        rst = stm.executeQuery(
                "select p.id "
                + "from produto p "
                + "inner join implantacao.codant_produto ant on ant.codigoatual = p.id "
                + "inner join implantacao.codant_ean ean on ean.importid = ant.impid "
                + "where ean.ean = '" + i_codigobarras + "'");
        if (rst.next()) {
            return rst.getInt("id");
        } else {
            return -1;
        }
    }

    /**
     * ******************* SISTEMA GDOOR******************************
     */
    public void alterarAliquotaProdutoGdoor(List<ProdutoVO> v_produto) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;

        try {

            Conexao.begin();

            stm = Conexao.createStatement();

            ProgressBar.setMaximum(v_produto.size());
            ProgressBar.setStatus("Importando Aliquota Icms Produto...");

            for (ProdutoVO i_produto : v_produto) {
                for (ProdutoAliquotaVO i_aliquota : i_produto.vAliquota) {
                    sql = new StringBuilder();
                    sql.append("select p.id from produto p ");
                    sql.append("inner join implantacao.codigoanterior ant ");
                    sql.append("on ant.codigoatual = p.id ");
                    sql.append("where ant.codigoanterior = " + i_produto.id);

                    rst = stm.executeQuery(sql.toString());

                    if (rst.next()) {

                        sql = new StringBuilder();
                        sql.append("update produtoaliquota set ");
                        sql.append("id_aliquotadebito = " + i_aliquota.idAliquotaDebito + ",");
                        sql.append("id_aliquotacredito = " + i_aliquota.idAliquotaCredito + ",");
                        sql.append("id_aliquotadebitoforaestado = " + i_aliquota.idAliquotaDebitoForaEstado + ",");
                        sql.append("id_aliquotacreditoforaestado = " + i_aliquota.idAliquotaCreditoForaEstado + ",");
                        sql.append("id_aliquotadebitoforaestadonf = " + i_aliquota.idAliquotaDebitoForaEstadoNF + " ");
                        sql.append("where id_produto = " + rst.getInt("id") + ";");
                        stm.execute(sql.toString());
                    }
                }

                ProgressBar.next();
            }

            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void altertarDataCadastroProdutoGdoor(List<ProdutoVO> v_produto) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        java.sql.Date datacadastro;

        try {
            Conexao.begin();

            stm = Conexao.createStatement();

            ProgressBar.setMaximum(v_produto.size());
            ProgressBar.setStatus("Importando Data Cadastro Produto...");

            for (ProdutoVO i_produto : v_produto) {

                sql = new StringBuilder();
                sql.append("select p.id from produto p ");
                sql.append("inner join implantacao.codigoanterior ant ");
                sql.append("on ant.codigoatual = p.id ");

                if (i_produto.id > 0) {
                    sql.append("where ant.codigoanterior = " + i_produto.id);
                } else {
                    sql.append("where ant.codigoanterior = " + i_produto.idDouble);
                }

                if (verificarLoja) {
                    sql.append(" and id_loja = " + p_idLoja);
                }

                rst = stm.executeQuery(sql.toString());

                if (rst.next()) {

                    sql = new StringBuilder();
                    sql.append("update produto set ");

                    if (i_produto.dataCadastro.isEmpty()) {
                        datacadastro = new java.sql.Date(new java.util.Date().getTime());
                        sql.append("datacadastro = '" + datacadastro + "'");
                    } else {
                        sql.append("datacadastro = '" + i_produto.dataCadastro + "'");
                    }

                    sql.append("where id = " + rst.getInt("id"));

                    stm.execute(sql.toString());
                }

                ProgressBar.next();
            }

            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    /**
     * ************ SISTEMA CONTECH **********************************
     */
    public void acertarProdutosContech(List<ProdutoVO> v_produto) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;

        try {
            Conexao.begin();

            stm = Conexao.createStatement();

            for (ProdutoVO i_produto : v_produto) {
                for (ProdutoAutomacaoVO oAutomacao : i_produto.vAutomacao) {

                    sql = new StringBuilder();
                    sql.append("select p.id from produto ");
                    sql.append("inner join implantacao.codigoanterior ant ");
                    sql.append("on ant.codigoatual = p.id ");
                    sql.append("where ant.codigoanterior = " + i_produto.id + " ");
                    sql.append("and p.mercadologico1 in (6, 178) ");

                }
            }

            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;

        }
    }

    public void alterarPrecoVendaMercadologicoContech(List<ProdutoVO> v_produto, int idLoja) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;

        try {

            Conexao.begin();

            stm = Conexao.createStatement();

            ProgressBar.setMaximum(v_produto.size());
            ProgressBar.setStatus("Importando Mercadoalogico e Preço Venda...");

            for (ProdutoVO i_produto : v_produto) {
                for (ProdutoComplementoVO oComplemento : i_produto.vComplemento) {

                    sql = new StringBuilder();
                    sql.append("select p.id from produto p ");
                    sql.append("inner join implantacao.codigoanterior ant ");
                    sql.append("on ant.codigoatual = p.id ");
                    sql.append("where ant.codigoanterior = " + i_produto.id);

                    rst = stm.executeQuery(sql.toString());

                    if (rst.next()) {

                        sql = new StringBuilder();
                        sql.append("update produto set ");
                        sql.append("mercadologico1 = " + i_produto.mercadologico1 + ", ");
                        sql.append("mercadologico2 = " + i_produto.mercadologico2 + ", ");
                        sql.append("mercadologico3 = " + i_produto.mercadologico3 + " ");
                        sql.append("where id = " + rst.getInt("id") + ";");
                        sql.append("update produtocomplemento set ");
                        sql.append("precovenda = " + oComplemento.precoVenda + ", ");
                        sql.append("precodiaseguinte = " + oComplemento.precoVenda + " ");
                        sql.append("where id_produto = " + rst.getInt("id") + " ");
                        sql.append("and id_loja = " + idLoja + ";");

                        stm.execute(sql.toString());

                    }
                }

                ProgressBar.next();
            }

            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void alterarNcmPisCofinsContech(List<ProdutoVO> v_produto) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;

        try {
            Conexao.begin();

            stm = Conexao.createStatement();

            ProgressBar.setMaximum(v_produto.size());
            ProgressBar.setStatus("Importando Ncm, Pis Cofins Produto...");

            for (ProdutoVO i_produto : v_produto) {
                sql = new StringBuilder();
                sql.append("select p.id from produto p ");
                sql.append("inner join implantacao.codigoanterior ant on ant.codigoatual = p.id ");
                sql.append("where ant.codigoanterior = " + i_produto.id);

                rst = stm.executeQuery(sql.toString());

                if (rst.next()) {
                    sql = new StringBuilder();
                    sql.append("update produto set ");
                    sql.append("id_tipopiscofins = " + i_produto.idTipoPisCofinsDebito + ", ");
                    sql.append("id_tipopiscofinscredito = " + i_produto.idTipoPisCofinsCredito + ", ");
                    sql.append("tiponaturezareceita = " + (i_produto.tipoNaturezaReceita == -1 ? null : i_produto.tipoNaturezaReceita) + ", ");
                    sql.append("ncm1 = " + (i_produto.ncm1 == -1 ? null : i_produto.ncm1) + ", ");
                    sql.append("ncm2 = " + (i_produto.ncm2 == -1 ? null : i_produto.ncm2) + ", ");
                    sql.append("ncm3 = " + (i_produto.ncm3 == -1 ? null : i_produto.ncm3) + " ");
                    sql.append("where id = " + rst.getInt("id"));
                    sql.append(";");

                    sql.append("update implantacao.codigoanterior set ");

                    if (i_produto.ncm1 == -1) {
                        sql.append("ncm = NULL, ");
                    } else {
                        sql.append("ncm = '" + i_produto.ncm1 + i_produto.ncm2 + i_produto.ncm3 + "', ");
                    }

                    sql.append("piscofinsdebito = " + (i_produto.pisCofinsDebitoAnt == -1 ? null : i_produto.pisCofinsDebitoAnt) + ", ");
                    sql.append("piscofinscredito = " + (i_produto.pisCofinsCreditoAnt == -1 ? null : i_produto.pisCofinsCreditoAnt) + " ");

                    sql.append("where codigoanterior = " + i_produto.id + ";");

                    stm.execute(sql.toString());
                }

                ProgressBar.next();
            }

            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void alterarNcmContech(List<ProdutoVO> v_produto) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;

        try {
            Conexao.begin();

            stm = Conexao.createStatement();

            ProgressBar.setMaximum(v_produto.size());
            ProgressBar.setStatus("Importando Ncm Produto...");

            for (ProdutoVO i_produto : v_produto) {
                sql = new StringBuilder();
                sql.append("select p.id from produto p ");
                sql.append("inner join implantacao.codigoanterior ant on ant.codigoatual = p.id ");
                sql.append("where ant.codigoanterior = " + i_produto.id);

                rst = stm.executeQuery(sql.toString());

                if (rst.next()) {
                    sql = new StringBuilder();
                    sql.append("update produto set ");
                    sql.append("ncm1 = " + (i_produto.ncm1 == -1 ? null : i_produto.ncm1) + ", ");
                    sql.append("ncm2 = " + (i_produto.ncm2 == -1 ? null : i_produto.ncm2) + ", ");
                    sql.append("ncm3 = " + (i_produto.ncm3 == -1 ? null : i_produto.ncm3) + " ");
                    sql.append("where id = " + rst.getInt("id"));
                    sql.append(";");

                    sql.append("update implantacao.codigoanterior set ");

                    if (i_produto.ncm1 == -1) {
                        sql.append("ncm = NULL ");
                    } else {
                        sql.append("ncm = '" + i_produto.ncm1 + i_produto.ncm2 + i_produto.ncm3 + "' ");
                    }

                    sql.append("where codigoanterior = " + i_produto.id + ";");

                    stm.execute(sql.toString());
                }

                ProgressBar.next();
            }

            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void alterarProdutoMercadologicoContech(List<ProdutoVO> v_produto) throws Exception {
        StringBuilder sql = null;
        Statement stm = null, stm2 = null, stm3 = null;
        ResultSet rst = null, rst2 = null;

        File f = new File("C:\\svn\\repositorioImplantacao\\Importacoes_OFFICIAL\\Recanto (Contech)\\errorlog.txt");
        FileWriter fw = new FileWriter(f);
        BufferedWriter bw = new BufferedWriter(fw);

        try {

            Conexao.begin();

            stm = Conexao.createStatement();
            stm2 = Conexao.createStatement();
            stm3 = Conexao.createStatement();

            ProgressBar.setStatus("Acertando Mercadologico Produto...");
            ProgressBar.setMaximum(v_produto.size());

            for (ProdutoVO i_produto : v_produto) {

                ProgressBar.next();
            }

            stm.close();
            stm2.close();
            stm3.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    /* SISTEMA IDEAL */
    public void alterarEstoqueProdutoIdeal(List<ProdutoVO> v_produto, int id_loja) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;

        try {

            Conexao.begin();

            stm = Conexao.createStatement();

            ProgressBar.setMaximum(v_produto.size());

            ProgressBar.setStatus("Atualizando dados produto...Estoque...");

            for (ProdutoVO i_produto : v_produto) {
                for (ProdutoComplementoVO oComplemento : i_produto.vComplemento) {

                    sql = new StringBuilder();
                    sql.append("select p.id from produto p ");
                    sql.append("inner join implantacao.codigoanterior ant on ant.codigoatual = p.id ");
                    if (i_produto.idDouble > 0) {
                        IdProduto = i_produto.idDouble;
                        sql.append("where CAST(ant.codigoanterior AS NUMERIC(14,0)) = CAST(" + IdProduto + " AS NUMERIC(14,0)) ");
                    } else {
                        IdProduto = i_produto.id;
                        sql.append("where ant.codigoanterior = " + IdProduto);
                    }

                    rst = stm.executeQuery(sql.toString());

                    if (rst.next()) {

                        sql = new StringBuilder();
                        sql.append(" UPDATE produtocomplemento SET ");
                        sql.append(" estoque = estoque + " + oComplemento.estoque + ", ");
                        sql.append(" estoqueminimo = " + oComplemento.estoqueMinimo + ", ");
                        sql.append(" estoquemaximo = " + oComplemento.estoqueMaximo + " ");
                        sql.append(" where id_produto = " + rst.getInt("id") + " ");
                        sql.append(" and id_loja = " + id_loja + ";");

                        sql.append("UPDATE implantacao.codigoanterior SET ");
                        sql.append("estoque = estoque + " + oComplemento.estoque + " ");
                        sql.append("WHERE codigoatual = " + rst.getInt("id") + ";");

                        stm.execute(sql.toString());
                    }
                }

                ProgressBar.next();
            }

            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    /**
     * ******* ALTERAR PRECO, CUSTO, MARGEM PAIVA
     *
     */
    public void alterarPrecoCustoMargemProdutosPaiva(List<ProdutoVO> v_produto, int idLoja) throws Exception {

        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;

        ProgressBar.setStatus("Alterando Preço, Custo, Margem...Produtos...");
        ProgressBar.setMaximum(v_produto.size());

        Conexao.begin();
        stm = Conexao.createStatement();

        try {

            for (ProdutoVO i_produto : v_produto) {
                for (ProdutoComplementoVO oComplemento : i_produto.vComplemento) {

                    sql = new StringBuilder();
                    sql.append("select p.id from produto p ");
                    sql.append("inner join implantacao.codigoanterior ant on ant.codigoatual = p.id ");
                    sql.append("where ant.codigoanterior = " + i_produto.id);

                    rst = stm.executeQuery(sql.toString());

                    if (rst.next()) {

                        sql = new StringBuilder();
                        sql.append("update produto set margem = " + i_produto.margem + " ");
                        sql.append("where id = " + rst.getInt("id") + ";");
                        sql.append("update produtocomplemento set ");
                        sql.append("precovenda = " + oComplemento.precoVenda + ", ");
                        sql.append("precodiaseguinte = " + oComplemento.precoDiaSeguinte + ", ");
                        sql.append("custocomimposto = " + oComplemento.custoComImposto + ", ");
                        sql.append("custosemimposto = " + oComplemento.custoSemImposto + " ");
                        sql.append("where id_produto = " + rst.getInt("id") + " ");
                        sql.append("and id_loja = " + idLoja + ";");

                        stm.execute(sql.toString());
                    }

                    ProgressBar.next();
                }
            }

            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    /**
     * SISTEMA PCSISTEMAS ********************************
     */
    public void alterarPrecoProdutoPCSistemas(List<ProdutoVO> v_produto, int id_loja) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null, sqlAux = null;
        double IdProduto = 0;
        int cont = 0;

        try {

            Conexao.begin();

            stm = Conexao.createStatement();

            ProgressBar.setMaximum(v_produto.size());

            ProgressBar.setStatus("Atualizando dados produto...Preço...");

            sql = new StringBuilder();

            for (ProdutoVO i_produto : v_produto) {

                cont = cont + 1;

                for (ProdutoComplementoVO oComplemento : i_produto.vComplemento) {
                    //sql = new StringBuilder();
                    sql.append("UPDATE produtocomplemento SET ");
                    sql.append("precovenda = " + oComplemento.precoVenda + ", ");
                    sql.append("precodiaseguinte = " + oComplemento.precoDiaSeguinte + " ");

                    if (i_produto.id > 0) {
                        sql.append("where id_produto = " + i_produto.id + " ");
                    } else {
                        sql.append("where id_produto = " + i_produto.idDouble + " ");
                    }

                    sql.append("and id_loja = " + id_loja + "; ");

                    if (i_produto.margem > 0) {
                        sql.append(" UPDATE produto SET ");
                        sql.append(" margem = " + i_produto.margem + " ");

                        if (i_produto.id > 0) {
                            sql.append("where id = " + i_produto.id + "; ");
                        } else {
                            sql.append("where id = " + i_produto.idDouble + "; ");
                        }
                    }

                    sql.append("UPDATE implantacao.codigoanterior SET ");
                    sql.append("precovenda = " + oComplemento.precoVenda + " ");

                    if (i_produto.id > 0) {
                        sql.append("where codigoatual = " + i_produto.id + " ");
                    } else {
                        sql.append("where codigoatual = " + i_produto.idDouble + " ");
                    }

                    sql.append("AND id_loja = " + id_loja + "; ");

                    //stm.execute(sql.toString());
                }

                if (cont == 1000) {
                    stm.execute(sql.toString());
                    sql = new StringBuilder();
                    cont = 0;
                }

                ProgressBar.next();
            }

            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void alterarCustoProdutoPCSistemas(List<ProdutoVO> v_produto, int id_loja) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null, sqlAux = null;
        double IdProduto = 0;
        int cont = 0;

        try {

            Conexao.begin();

            stm = Conexao.createStatement();

            ProgressBar.setMaximum(v_produto.size());

            ProgressBar.setStatus("Atualizando dados produto...Custo...");

            sql = new StringBuilder();

            for (ProdutoVO i_produto : v_produto) {

                cont = cont + 1;

                for (ProdutoComplementoVO oComplemento : i_produto.vComplemento) {

                    sql.append("UPDATE produtocomplemento SET ");
                    sql.append("custocomimposto = " + oComplemento.custoComImposto + ", ");
                    sql.append("custosemimposto = " + oComplemento.custoSemImposto + " ");
                    sql.append("where id_produto = " + i_produto.id + " ");
                    sql.append("and id_loja = " + id_loja + "; ");
                }

                if (cont == 1000) {
                    stm.execute(sql.toString());
                    sql = new StringBuilder();
                    cont = 0;
                }

                ProgressBar.next();
            }

            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void alterarPisCofinsProdutoPCSistemas(List<ProdutoVO> v_produto) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;
        int cont = 0;

        try {
            Conexao.begin();

            stm = Conexao.createStatement();

            ProgressBar.setMaximum(v_produto.size());

            ProgressBar.setStatus("Atualizando dados produto...PisCofins e Natureza Receita...");

            sql = new StringBuilder();

            for (ProdutoVO i_produto : v_produto) {

                cont = cont + 1;

                for (CodigoAnteriorVO i_codigoAnterior : i_produto.vCodigoAnterior) {

                    //sql = new StringBuilder();
                    sql.append("update produto set ");
                    sql.append("id_tipopiscofins = " + i_produto.idTipoPisCofinsDebito + ", ");
                    sql.append("id_tipopiscofinscredito = " + i_produto.idTipoPisCofinsCredito + ", ");
                    sql.append("tiponaturezareceita = " + i_produto.tipoNaturezaReceita + " ");
                    sql.append("where id = " + i_produto.id + ";");

                    sql.append("UPDATE implantacao.codigoanterior SET ");
                    sql.append("piscofinsdebito = " + i_codigoAnterior.piscofinsdebito + ", ");
                    sql.append("piscofinscredito = " + i_codigoAnterior.piscofinscredito + ", ");
                    sql.append("naturezareceita = " + i_codigoAnterior.naturezareceita + " ");
                    sql.append("WHERE codigoatual = " + i_produto.id + ";");
                    //stm.execute(sql.toString());
                }

                if (cont == 1000) {
                    stm.execute(sql.toString());
                    sql = new StringBuilder();
                    cont = 0;
                }

                ProgressBar.next();
            }

            stm.close();
            Conexao.commit();

        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void alterarIcmsProdutoPCSistemas(List<ProdutoVO> v_produto) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;
        int cont = 0;

        try {
            Conexao.begin();

            stm = Conexao.createStatement();

            ProgressBar.setMaximum(v_produto.size());

            ProgressBar.setStatus("Atualizando dados produto...Icms...");

            sql = new StringBuilder();

            for (ProdutoVO i_produto : v_produto) {

                cont = cont + 1;

                for (ProdutoAliquotaVO i_aliquota : i_produto.vAliquota) {

                    sql.append("update produtoaliquota set ");
                    sql.append("id_aliquotadebito = " + i_aliquota.idAliquotaDebito + ", ");
                    sql.append("id_aliquotacredito = " + i_aliquota.idAliquotaCredito + ", ");
                    sql.append("id_aliquotadebitoforaestado = " + i_aliquota.idAliquotaDebitoForaEstado + ", ");
                    sql.append("id_aliquotacreditoforaestado = " + i_aliquota.idAliquotaCreditoForaEstado + ", ");
                    sql.append("id_aliquotadebitoforaestadoNF = " + i_aliquota.idAliquotaDebitoForaEstadoNF + " ");
                    sql.append("where id_produto = " + i_produto.id + "; ");
                }

                for (CodigoAnteriorVO i_codigoAnterior : i_produto.vCodigoAnterior) {
                    sql.append("UPDATE implantacao.codigoanterior SET ");
                    sql.append("ref_icmsdebito = '" + i_codigoAnterior.ref_icmsdebito + "' ");
                    sql.append("WHERE codigoanterior = " + i_produto.id + ";");
                }

                if (cont == 1000) {
                    stm.execute(sql.toString());
                    sql = new StringBuilder();
                    cont = 0;
                }

                ProgressBar.next();
            }

            stm.close();
            Conexao.commit();

        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void alterarEstoqueProdutoPCSistemas(List<ProdutoVO> v_produto) throws Exception {
        Statement stm = null;
        StringBuilder sql = null;
        int cont = 0;

        try {
            Conexao.begin();

            stm = Conexao.createStatement();

            ProgressBar.setMaximum(v_produto.size());

            ProgressBar.setStatus("Atualizando dados produto...Estoque...");

            sql = new StringBuilder();

            for (ProdutoVO i_produto : v_produto) {

                cont = cont + 1;

                for (ProdutoComplementoVO i_complemento : i_produto.vComplemento) {

                    sql.append("update produtocomplemento set ");
                    sql.append("estoque = " + i_complemento.estoque + " ");
                    sql.append("where id_produto = " + i_produto.id + " ");
                    sql.append("and id_loja = " + i_complemento.idLoja + "; ");

                }

                if (cont == 1000) {
                    stm.execute(sql.toString());
                    sql = new StringBuilder();
                    cont = 0;
                }

                ProgressBar.next();
            }

            stm.close();
            Conexao.commit();

        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void addCodigoBarrasPCSistemas(List<ProdutoVO> v_produto) throws Exception {
        Statement stm = null, stm2 = null;
        ResultSet rst = null, rst2 = null;
        StringBuilder sql = null;

        try {

            Conexao.begin();

            stm = Conexao.createStatement();
            stm2 = Conexao.createStatement();

            ProgressBar.setMaximum(v_produto.size());

            ProgressBar.setStatus("Atualizando dados produto...Codigo Barra...");

            for (ProdutoVO i_produto : v_produto) {
                for (ProdutoAutomacaoVO oAutomacao : i_produto.vAutomacao) {

                    sql = new StringBuilder();
                    sql.append("select p.id, id_tipoembalagem from produto p ");
                    sql.append("inner join implantacao.codigoanterior ant on ant.codigoatual = p.id ");
                    if (i_produto.idDouble > 0) {
                        IdProduto = i_produto.idDouble;
                        sql.append("where CAST(ant.codigoanterior AS NUMERIC(14,0)) = CAST(" + IdProduto + " AS NUMERIC(14,0)) ");
                    } else {
                        IdProduto = i_produto.id;
                        sql.append("where ant.codigoanterior = " + IdProduto);
                    }

                    rst = stm.executeQuery(sql.toString());

                    if (rst.next()) {

                        sql = new StringBuilder();
                        sql.append("select * from produtoautomacao ");
                        sql.append("where codigobarras = " + oAutomacao.codigoBarras);

                        rst2 = stm2.executeQuery(sql.toString());

                        if (!rst2.next()) {
                            sql = new StringBuilder();
                            sql.append("insert into produtoautomacao (");
                            sql.append("id_produto, codigobarras, qtdembalagem, id_tipoembalagem) ");
                            sql.append("values (");
                            sql.append(rst.getInt("id") + ",");
                            sql.append(oAutomacao.codigoBarras + ",");
                            sql.append((oAutomacao.qtdEmbalagem == -1 ? "1," : oAutomacao.qtdEmbalagem) + ",");
                            sql.append(rst.getInt("id_tipoembalagem") + ")");
                            stm.execute(sql.toString());
                        }
                    }
                }

                ProgressBar.next();
            }

            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    /**
     * **** CARNAUBA *****
     */
    public void acertarPrecoMargemCustoCarnauba(List<ProdutoVO> v_produto, int idLoja) throws Exception {
        StringBuilder sql = null;
        Statement stm = null,
                stm2 = null;
        ResultSet rst = null,
                rst2 = null,
                rst3 = null;

        try {

            Conexao.begin();
            stm = Conexao.createStatement();
            stm2 = Conexao.createStatement();

            ProgressBar.setMaximum(v_produto.size());

            ProgressBar.setStatus("Atualizando dados produto...Preço Loja " + idLoja + "...");

            for (ProdutoVO i_produto : v_produto) {
                for (ProdutoComplementoVO i_complemento : i_produto.vComplemento) {

                    sql = new StringBuilder();
                    sql.append("select p.id from produto p ");
                    sql.append("inner join implantacao.codigoanterior ant on ant.codigoatual = p.id ");
                    sql.append("where ant.codigoanterior = " + i_produto.codigoAnterior + " ");
                    sql.append("and ant.id_loja = " + idLoja);

                    rst = stm.executeQuery(sql.toString());

                    if (rst.next()) {

                        sql = new StringBuilder();
                        sql.append("select id_produto from oferta ");
                        sql.append("where id_loja = " + idLoja + " ");
                        sql.append("and id_produto = " + rst.getInt("id") + ";");

                        rst3 = stm2.executeQuery(sql.toString());

                        if (!rst3.next()) {

                            sql = new StringBuilder();
                            sql.append("update produto set margem = " + i_produto.margem + " ");
                            sql.append("where id = " + rst.getInt("id") + ";");

                            sql.append("update produtocomplemento set ");
                            sql.append("precovenda = " + i_complemento.precoVenda + ", ");
                            sql.append("precodiaseguinte = " + i_complemento.precoDiaSeguinte + " ");
                            sql.append("where id_produto = " + rst.getInt("id") + " ");
                            sql.append("and id_loja = " + idLoja + ";");

                            stm.execute(sql.toString());

                        }

                    } else {

                        if (i_produto.codigoBarras != -1) {

                            sql = new StringBuilder();
                            sql.append("select p.id from produto p ");
                            sql.append("inner join produtoautomacao pa on pa.id_produto = p.id ");
                            sql.append("where pa.codigobarras = " + i_produto.codigoBarras);

                            rst2 = stm.executeQuery(sql.toString());

                            if (rst2.next()) {

                                sql = new StringBuilder();
                                sql.append("select id_produto from oferta ");
                                sql.append("where id_loja = " + idLoja + " ");
                                sql.append("and id_produto = " + rst2.getInt("id") + ";");

                                rst3 = stm2.executeQuery(sql.toString());

                                if (!rst3.next()) {

                                    sql = new StringBuilder();
                                    sql.append("update produtocomplemento set ");
                                    sql.append("precovenda = " + i_complemento.precoVenda + ", ");
                                    sql.append("precodiaseguinte = " + i_complemento.precoDiaSeguinte + " ");
                                    sql.append("where id_produto = " + rst2.getInt("id") + " ");
                                    sql.append("and id_loja = " + idLoja + ";");

                                    stm.execute(sql.toString());
                                }
                            }
                        }
                    }

                    ProgressBar.next();
                }
            }

            stm.close();
            stm2.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void acertarProduto(List<ProdutoVO> vProduto) throws Exception {
        StringBuilder sql = null;
        Statement stm = null, stm2 = null;
        ResultSet rst = null, rst2 = null;
        Utils util = new Utils();
        File f = new File("C:\\svn\\repositorioImplantacao\\Importacoes_OFFICIAL\\Bailo Morada Sol (BoechatSoft)\\script\\acertar_produtos.txt");
        FileWriter fw = new FileWriter(f);
        BufferedWriter bw = new BufferedWriter(fw);

        try {
            Conexao.begin();
            stm = Conexao.createStatement();
            stm2 = Conexao.createStatement();

            ProgressBar.setStatus("Importando dados...Acertando cadastro de produto...");
            ProgressBar.setMaximum(vProduto.size());

            for (ProdutoVO i_produto : vProduto) {

                sql = new StringBuilder();
                sql.append("select p.id from produto p ");
                sql.append("inner join implantacao.codigoanterior ant on ant.codigoatual = p.id ");
                sql.append("where ant.codigoanterior = " + i_produto.id + ";");

                rst = stm.executeQuery(sql.toString());

                if (rst.next()) {

                    sql = new StringBuilder();
                    sql.append("update produto set ");
                    sql.append("id_tipoembalagem = " + i_produto.idTipoEmbalagem + ", ");
                    sql.append("qtdembalagem = " + i_produto.qtdEmbalagem + ", ");
                    sql.append("pesavel = " + i_produto.pesavel + " ");
                    sql.append("where id = " + rst.getInt("id") + ";");

                    bw.write(sql.toString());
                    bw.newLine();

                    for (ProdutoAutomacaoVO oAutomacao : i_produto.vAutomacao) {

                        if (oAutomacao.codigoBarras == -1) {
                            if ((rst.getInt("id") <= 9999) && (i_produto.idTipoEmbalagem != 4)) {

                                oAutomacao.codigoBarras = util.gerarEan13(rst.getInt("id"), false);

                            } else if ((rst.getInt("id") > 9999) && (i_produto.idTipoEmbalagem != 4)) {

                                oAutomacao.codigoBarras = util.gerarEan13(rst.getInt("id"), true);

                            } else if (i_produto.idTipoEmbalagem == 4) {

                                oAutomacao.codigoBarras = util.gerarEan13(rst.getInt("id"), false);
                            } else {

                                oAutomacao.codigoBarras = util.gerarEan13(rst.getInt("id"), true);

                            }
                        }

                        if (String.valueOf(oAutomacao.codigoBarras).length() > 14) {
                            oAutomacao.codigoBarras = Long.parseLong(String.valueOf(oAutomacao.codigoBarras).substring(0, 14));
                        }

                        sql = new StringBuilder();
                        sql.append("select * from produtoautomacao ");
                        sql.append("where codigobarras = " + oAutomacao.codigoBarras + "; ");

                        rst2 = stm2.executeQuery(sql.toString());

                        if (!rst2.next()) {

                            sql = new StringBuilder();
                            sql.append("insert into produtoautomacao (");
                            sql.append("id_produto, codigobarras, id_tipoembalagem, qtdembalagem) ");
                            sql.append("values(");
                            sql.append(rst.getInt("id") + ",");
                            sql.append(oAutomacao.codigoBarras + ", ");
                            sql.append(oAutomacao.idTipoEmbalagem + ", ");
                            sql.append(oAutomacao.qtdEmbalagem + ");");

                            bw.write(sql.toString());
                            bw.newLine();
                        }
                    }
                }

                for (CodigoAnteriorVO oAnterior : i_produto.vCodigoAnterior) {

                    sql = new StringBuilder();
                    sql.append("update implantacao.codigoanterior set ");
                    sql.append("codigobalanca = " + oAnterior.codigobalanca + ", ");
                    sql.append("e_balanca = " + oAnterior.e_balanca + ", ");
                    sql.append("barras = " + oAnterior.barras + " ");
                    sql.append("where codigoanterior = " + rst.getInt("id") + ";");

                    bw.write(sql.toString());
                    bw.newLine();

                }

                ProgressBar.next();
            }

            bw.flush();
            bw.close();
            stm.close();
            stm2.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void alterarMercadologicoProduto(List<ProdutoVO> v_produto) throws Exception {
        StringBuilder sql = null;
        Statement stm = null, stm2 = null;
        ResultSet rst = null, rst2 = null;

        try {
            Conexao.begin();
            stm = Conexao.createStatement();
            stm2 = Conexao.createStatement();

            ProgressBar.setStatus("Acertar Mercadologico Produtos...");
            ProgressBar.setMaximum(v_produto.size());

            for (ProdutoVO i_produto : v_produto) {
                sql = new StringBuilder();
                sql.append("select p.id from produto p ");
                sql.append("inner join implantacao.codigoanterior ant on ant.codigoatual = p.id ");

                if (i_produto.id > 0) {
                    sql.append(" where ant.codigoanterior = " + i_produto.id + " ");
                } else {
                    sql.append(" where ant.codigoanterior = " + i_produto.idDouble + " ");
                }

                rst = stm.executeQuery(sql.toString());

                if (rst.next()) {

                    sql = new StringBuilder();
                    sql.append("select m.mercadologico1, m.mercadologico2, m.mercadologico3, ");
                    sql.append("m.mercadologico4, m.mercadologico5 ");
                    sql.append("from mercadologico m ");
                    sql.append("inner join implantacao.codigoanteriormercadologico ant ");
                    sql.append("on ant.merc1 = m.mercadologico1 and ant.merc2 = m.mercadologico2 and ant.merc3 = m.mercadologico3 ");
                    sql.append("and ant.merc4 = m.mercadologico4 and ant.merc5 = m.mercadologico5 ");
                    sql.append("and ant.ant1 = " + i_produto.mercadologico1 + " ");
                    sql.append("and ant.ant2 = " + i_produto.mercadologico2 + " ");
                    sql.append("and ant.ant3 = " + i_produto.mercadologico3 + " ");
                    sql.append("and ant.ant4 = " + i_produto.mercadologico4 + " ");
                    sql.append("and ant.ant5 = " + i_produto.mercadologico5 + " ");

                    rst2 = stm2.executeQuery(sql.toString());

                    if (rst2.next()) {

                        sql = new StringBuilder();
                        sql.append("update produto set ");
                        sql.append("mercadologico1 = " + rst2.getInt("mercadologico1") + ", ");
                        sql.append("mercadologico2 = " + rst2.getInt("mercadologico2") + ", ");
                        sql.append("mercadologico3 = " + rst2.getInt("mercadologico3") + ", ");
                        sql.append("mercadologico4 = " + rst2.getInt("mercadologico4") + ", ");
                        sql.append("mercadologico5 = " + rst2.getInt("mercadologico5") + " ");
                        sql.append("where id = " + rst.getInt("id") + ";");

                        stm.execute(sql.toString());
                    }
                }

                ProgressBar.next();
            }

            stm.close();
            stm2.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void alterarMercadologicoProdutoSemCodigoAnteriorMerc(List<ProdutoVO> v_produto) throws Exception {
        StringBuilder sql = null;
        Statement stm = null, stm2 = null;
        ResultSet rst = null, rst2 = null;

        try {
            Conexao.begin();
            stm = Conexao.createStatement();
            stm2 = Conexao.createStatement();

            ProgressBar.setStatus("Acertar Mercadologico Produtos...");
            ProgressBar.setMaximum(v_produto.size());

            for (ProdutoVO i_produto : v_produto) {
                sql = new StringBuilder();
                sql.append("select p.id from produto p ");
                sql.append("inner join implantacao.codigoanterior ant on ant.codigoatual = p.id ");

                if (i_produto.id > 0) {
                    sql.append(" where ant.codigoanterior = " + i_produto.id + " ");
                } else {
                    sql.append(" where ant.codigoanterior = " + i_produto.idDouble + " ");
                }

                rst = stm.executeQuery(sql.toString());

                if (rst.next()) {

                    sql = new StringBuilder();
                    sql.append("select m.mercadologico1, m.mercadologico2, m.mercadologico3, ");
                    sql.append("m.mercadologico4, m.mercadologico5 ");
                    sql.append("from mercadologico m ");
                    sql.append("where m.mercadologico1 = " + i_produto.mercadologico1 + " ");
                    sql.append("and m.mercadologico2 = " + i_produto.mercadologico2 + " ");
                    sql.append("and m.mercadologico3 = " + i_produto.mercadologico3 + " ");
                    sql.append("and m.mercadologico4 = " + i_produto.mercadologico4 + " ");
                    sql.append("and m.mercadologico5 = " + i_produto.mercadologico5 + " ");

                    rst2 = stm2.executeQuery(sql.toString());

                    if (rst2.next()) {

                        sql = new StringBuilder();
                        sql.append("update produto set ");
                        sql.append("mercadologico1 = " + rst2.getInt("mercadologico1") + ", ");
                        sql.append("mercadologico2 = " + rst2.getInt("mercadologico2") + ", ");
                        sql.append("mercadologico3 = " + rst2.getInt("mercadologico3") + ", ");
                        sql.append("mercadologico4 = " + rst2.getInt("mercadologico4") + ", ");
                        sql.append("mercadologico5 = " + rst2.getInt("mercadologico5") + " ");
                        sql.append("where id = " + rst.getInt("id") + ";");

                        stm.execute(sql.toString());
                    }
                }

                ProgressBar.next();
            }

            stm.close();
            stm2.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void alterarMercadologicoProdutoSemCodigoAnterior(List<ProdutoVO> v_produto) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;

        try {
            Conexao.begin();
            stm = Conexao.createStatement();

            ProgressBar.setStatus("Acertar Mercadologico Produtos...");
            ProgressBar.setMaximum(v_produto.size());

            for (ProdutoVO i_produto : v_produto) {
                sql = new StringBuilder();
                sql.append("select m.mercadologico1, m.mercadologico2, m.mercadologico3, ");
                sql.append("m.mercadologico4, m.mercadologico5 ");
                sql.append("from mercadologico m ");
                sql.append("where m.mercadologico1 = " + i_produto.getMercadologico1() + " ");
                sql.append("and m.mercadologico2 = " + i_produto.getMercadologico2() + " ");
                sql.append("and m.mercadologico3 = " + i_produto.getMercadologico3() + " ");
                sql.append("and m.mercadologico4 = " + i_produto.getMercadologico4() + " ");
                sql.append("and m.mercadologico5 = " + i_produto.getMercadologico5() + " ");
                rst = stm.executeQuery(sql.toString());
                if (rst.next()) {
                    sql = new StringBuilder();
                    sql.append("update produto set ");
                    sql.append("mercadologico1 = " + rst.getInt("mercadologico1") + ", ");
                    sql.append("mercadologico2 = " + rst.getInt("mercadologico2") + ", ");
                    sql.append("mercadologico3 = " + rst.getInt("mercadologico3") + ", ");
                    sql.append("mercadologico4 = " + rst.getInt("mercadologico4") + ", ");
                    sql.append("mercadologico5 = " + rst.getInt("mercadologico5") + " ");
                    sql.append("where id = " + i_produto.getId() + ";");
                    stm.execute(sql.toString());
                } else {
                    MercadologicoVO mercadologico;
                    mercadologico = MercadologicoDAO.getMercadologicoAAcertar();
                    sql = new StringBuilder();
                    sql.append("update produto set ");
                    sql.append("mercadologico1 = " + mercadologico.mercadologico1 + ", ");
                    sql.append("mercadologico2 = " + mercadologico.mercadologico2 + ", ");
                    sql.append("mercadologico3 = " + mercadologico.mercadologico3 + ", ");
                    sql.append("mercadologico4 = " + mercadologico.mercadologico4 + ", ");
                    sql.append("mercadologico5 = " + mercadologico.mercadologico5 + " ");
                    sql.append("where id = " + i_produto.getId() + ";");
                }
                ProgressBar.next();
            }

            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    /**
     * **** RMS *****
     */
    public void alterarNcmRMS(List<ProdutoVO> v_produto) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;

        try {
            Conexao.begin();

            stm = Conexao.createStatement();

            ProgressBar.setMaximum(v_produto.size());
            ProgressBar.setStatus("Importando...Ncm Produto...");

            for (ProdutoVO i_produto : v_produto) {
                sql = new StringBuilder();
                sql.append("select p.id from produto p ");
                sql.append("inner join implantacao.codigoanterior ant on ant.codigoatual = p.id ");
                sql.append("where ant.codigoanterior = " + i_produto.id);

                rst = stm.executeQuery(sql.toString());

                if (rst.next()) {
                    sql = new StringBuilder();
                    sql.append("update produto set ");
                    sql.append("ncm1 = " + (i_produto.ncm1 == -1 ? null : i_produto.ncm1) + ", ");
                    sql.append("ncm2 = " + (i_produto.ncm2 == -1 ? null : i_produto.ncm2) + ", ");
                    sql.append("ncm3 = " + (i_produto.ncm3 == -1 ? null : i_produto.ncm3) + " ");
                    sql.append("where id = " + rst.getInt("id"));
                    sql.append(";");

                    stm.execute(sql.toString());
                }

                ProgressBar.next();
            }

            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void acertarNcmProdutoRMS(List<ProdutoVO> v_produto) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;

        try {
            Conexao.begin();

            stm = Conexao.createStatement();

            ProgressBar.setMaximum(v_produto.size());
            ProgressBar.setStatus("Importando...Acertar Ncm Produto...");

            for (ProdutoVO i_produto : v_produto) {
                sql = new StringBuilder();
                sql.append("select p.id from produto p ");
                sql.append("inner join implantacao.codigoanterior ant on ant.codigoatual = p.id ");

                if (i_produto.id > 0) {
                    sql.append("where ant.codigoanterior = " + i_produto.id);
                } else {
                    sql.append("where ant.codigoanterior = " + i_produto.idDouble);
                }

                rst = stm.executeQuery(sql.toString());

                if (rst.next()) {
                    sql = new StringBuilder();
                    sql.append("update produto set ");
                    sql.append("ncm1 = " + (i_produto.ncm1 == -1 ? null : i_produto.ncm1) + ", ");
                    sql.append("ncm2 = " + (i_produto.ncm2 == -1 ? null : i_produto.ncm2) + ", ");
                    sql.append("ncm3 = " + (i_produto.ncm3 == -1 ? null : i_produto.ncm3) + " ");
                    sql.append("where id = " + rst.getInt("id") + " ");
                    sql.append("and ncm1 = 402 ");
                    sql.append("and ncm2 = 99 ");
                    sql.append("and ncm3 = 0 ");
                    sql.append(";");

                    stm.execute(sql.toString());
                }

                ProgressBar.next();
            }

            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    protected Map<Long, ProdutoVO> carregarCodigoBarrasEmBranco() throws Exception {
        StringBuilder sql = null;
        Statement stmPostgres = null;
        ResultSet rst;
        Map<Long, ProdutoVO> vProduto = new HashMap<>();
        int qtdeEmbalagem;
        double idProduto;
        long codigobarras;
        Utils util = new Utils();

        try {

            stmPostgres = Conexao.createStatement();

            sql = new StringBuilder();
            sql.append("select id, id_tipoembalagem ");
            sql.append(" from produto p ");
            sql.append(" where not exists(select pa.id from produtoautomacao pa where pa.id_produto = p.id) ");

            rst = stmPostgres.executeQuery(sql.toString());

            while (rst.next()) {

                idProduto = Double.parseDouble(rst.getString("id"));

                if (rst.getInt("id_tipoembalagem") == 4) {
                    codigobarras = util.gerarEan13((int) idProduto, false);
                } else {
                    codigobarras = util.gerarEan13((int) idProduto, true);
                }

                qtdeEmbalagem = 1;

                ProdutoVO oProduto = new ProdutoVO();
                oProduto.id = (int) idProduto;
                ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();
                oAutomacao.idTipoEmbalagem = rst.getInt("id_tipoembalagem");
                oAutomacao.codigoBarras = codigobarras;
                oAutomacao.qtdEmbalagem = qtdeEmbalagem;
                oProduto.vAutomacao.add(oAutomacao);
                vProduto.put(codigobarras, oProduto);
            }

            return vProduto;

        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }

    /**
     * Verifica se os produtos apresentam código de barras em branco e gera um
     * novo código de barras
     *
     * @throws Exception
     */
    public void importarCodigoBarraEmBranco() throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();

        ProgressBar.setStatus("Carregando dados...Produtos...Codigo Barras...");
        Map<Long, ProdutoVO> vCodigoBarra = carregarCodigoBarrasEmBranco();

        ProgressBar.setMaximum(vCodigoBarra.size());

        for (Long keyId : vCodigoBarra.keySet()) {

            ProdutoVO oProduto = vCodigoBarra.get(keyId);

            vProdutoNovo.add(oProduto);

            ProgressBar.next();
        }

        produto.addCodigoBarrasEmBranco(vProdutoNovo);
    }

    public void gravarAtacado(List<ProdutoAutomacaoLojaVO> vProdutos, int idLojaVR, List<LojaVO> vLoja) throws Exception {
        Conexao.begin();
        try {
            ProgressBar.setStatus("Carregando dados...Atacado...");

            ProgressBar.setMaximum(vProdutos.size());

            for (ProdutoAutomacaoLojaVO vo : vProdutos) {
                //Verifica se existe o registro,se existir atualiza preço e 
                //qtdcaixa senão inclui

                int id_produtoAtual = 0;
                long codigoBarrasAtual = 0;

                try (ResultSet rst = Conexao.createStatement().executeQuery(
                        "select ca.codigoatual, cb.codigobarras_atual from "
                        + "implantacao.codigodebarrasalterado cb join "
                        + "implantacao.codigoanterior ca on "
                        + "cb.id_produto = ca.codigoatual "
                        + "where ca.codigoanterior = " + vo.id_produto + " and "
                        + "cb.codigobarras_anterior = " + vo.codigobarras
                )) {
                    if (rst.next()) {
                        codigoBarrasAtual = rst.getLong("codigobarras_atual");
                        id_produtoAtual = rst.getInt("codigoatual");
                    }
                }

                String sql = "", sql2 = "";
                try (Statement stm = Conexao.createStatement()) {
                    try (ResultSet rst = stm.executeQuery(
                            "select\n"
                            + "	*\n"
                            + "from\n"
                            + "	produtoautomacaoloja\n"
                            + "where\n"
                            + "	codigobarras = " + codigoBarrasAtual/*vo.codigobarras*/ + " and\n"
                            + "	id_loja = " + vo.id_loja + ";"
                    )) {
                        if (rst.next()) {
                            sql = "update produtoautomacaoloja set\n"
                                    + "precovenda = " + vo.precovenda + "\n"
                                    + "where\n"
                                    + "	codigobarras = " + codigoBarrasAtual/*vo.codigobarras*/ + " and\n"
                                    + "	id_loja = " + vo.id_loja;
                            Conexao.createStatement().execute(sql);

                            sql2 = "update produtoautomacao \n"
                                    + "set qtdembalagem = " + vo.qtdEmbalagem + " \n"
                                    + "where codigobarras = " + codigoBarrasAtual/*vo.codigobarras*/ + ";";

                            Conexao.createStatement().execute(sql2);
                        } else {

                            if (vo.gravarAutomacao) {
                                sql = "insert into produtoautomacaoloja (\n"
                                        + "codigobarras, precovenda, id_loja) values (\n"
                                        + codigoBarrasAtual/*vo.codigobarras*/ + ", "
                                        + vo.precovenda + ", "
                                        + vo.id_loja + "\n"
                                        + ")";
                                Conexao.createStatement().execute(sql);
                            }

                            sql2 = "update produtoautomacao \n"
                                    + "set qtdembalagem = " + vo.qtdEmbalagem + " \n"
                                    + "where codigobarras = " + codigoBarrasAtual/*vo.codigobarras*/ + ";";

                            Conexao.createStatement().execute(sql2);
                        }
                    }
                }

                ProgressBar.next();
            }
            Conexao.commit();
        } catch (Exception e) {
            Conexao.rollback();
            throw e;
        }
    }

    /**
     * *** EVEREST *****
     */
    public void acertarPisCofinsEverest(List<ProdutoVO> v_produto) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        double idProduto = 0;

        try {
            Conexao.begin();
            stm = Conexao.createStatement();

            ProgressBar.setStatus("Importando dados...Acertando Piscofins...");
            ProgressBar.setMaximum(v_produto.size());

            for (ProdutoVO i_produto : v_produto) {
                if (i_produto.id > 0) {
                    idProduto = i_produto.id;
                } else {
                    idProduto = i_produto.idDouble;
                }

                sql = new StringBuilder();
                sql.append("select p.id from produto p ");
                sql.append("inner join implantacao.produtos_unificacao ant ");
                sql.append("on ant.codigoatual = p.id ");
                sql.append("where ant.codigoanterior = " + idProduto + " ");
                sql.append("and ant.existe = false ");
                rst = stm.executeQuery(sql.toString());

                if (rst.next()) {
                    sql = new StringBuilder();
                    sql.append("update produto set ");
                    sql.append("id_tipopiscofins = " + i_produto.idTipoPisCofinsDebito + ", ");
                    sql.append("id_tipopiscofinscredito = " + i_produto.idTipoPisCofinsCredito + ", ");
                    sql.append("tiponaturezareceita = " + (i_produto.tipoNaturezaReceita == -1 ? null : i_produto.tipoNaturezaReceita) + " ");
                    sql.append("where id = " + rst.getInt("id") + ";");
                    stm.execute(sql.toString());
                }

                ProgressBar.next();
            }

            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void acertarICMSEverest(List<ProdutoVO> v_produto) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        double idProduto = 0;

        try {
            Conexao.begin();
            stm = Conexao.createStatement();

            ProgressBar.setStatus("Importando dados...Icms...");
            ProgressBar.setMaximum(v_produto.size());

            for (ProdutoVO i_produto : v_produto) {
                for (ProdutoAliquotaVO i_produtoAliquota : i_produto.vAliquota) {
                    if (i_produto.id > 0) {
                        idProduto = i_produto.id;
                    } else {
                        idProduto = i_produto.idDouble;
                    }

                    sql = new StringBuilder();
                    sql.append("select p.id from produto p ");
                    sql.append("inner join implantacao.produtos_unificacao ant ");
                    sql.append("on ant.codigoatual = p.id ");
                    sql.append("where ant.codigoanterior = " + idProduto + " ");
                    sql.append("and ant.existe = false ");
                    rst = stm.executeQuery(sql.toString());

                    if (rst.next()) {
                        sql = new StringBuilder();
                        sql.append("update produtoaliquota set ");
                        //sql.append("id_aliquotadebito = " + i_produtoAliquota.idAliquotaDebito + ", ");
                        sql.append("id_aliquotacredito = " + i_produtoAliquota.idAliquotaCredito + ", ");
                        sql.append("id_aliquotadebitoforaestado = " + i_produtoAliquota.idAliquotaDebitoForaEstado + ", ");
                        sql.append("id_aliquotacreditoforaestado = " + i_produtoAliquota.idAliquotaCreditoForaEstado + ", ");
                        sql.append("id_aliquotadebitoforaestadoNF = " + i_produtoAliquota.idAliquotaDebitoForaEstadoNF + " ");
                        sql.append("where id_produto = " + rst.getInt("id") + ";");
                        stm.execute(sql.toString());
                    }
                }

                ProgressBar.next();
            }

            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public boolean isProduto(int i_id) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        boolean achou = false;

        stm = Conexao.createStatement();

        rst = stm.executeQuery("SELECT id FROM produto WHERE id = " + i_id);

        if (rst.next()) {
            achou = true;
        }

        return achou;

    }

    public void alterarFamiliaProdutoIntegracao(List<ProdutoVO> v_produto) throws Exception {
        Statement stm = null, stm2 = null;
        ResultSet rst = null, rst2 = null;
        StringBuilder sql = null;
        double IdProduto;

        try {
            Conexao.begin();
            stm = Conexao.createStatement();
            stm2 = Conexao.createStatement();
            ProgressBar.setMaximum(v_produto.size());
            ProgressBar.setStatus("Atualizando dados produto...Familia Produto Integraçao...");

            for (ProdutoVO i_produto : v_produto) {

                sql = new StringBuilder();
                sql.append("select p.id from produto p ");
                sql.append("inner join produtoautomacao pa on pa.id_produto = p.id ");
                sql.append("where pa.codigobarras = " + i_produto.codigoBarras + " ");
                sql.append("and char_length(cast(pa.codigobarras as varchar)) >= 7 ");
                rst = stm.executeQuery(sql.toString());

                if (rst.next()) {
                    sql = new StringBuilder();
                    sql.append("select f.id from implantacao.codigoanterior_familiaproduto ant ");
                    sql.append("inner join familiaproduto f on f.id = ant.codigoatual ");
                    sql.append("where ant.codigoanterior = " + i_produto.idFamiliaProduto + " ");
                    sql.append("and ant.codigoanterior <> ant.codigoatual");
                    rst2 = stm2.executeQuery(sql.toString());
                    if (rst2.next()) {
                        sql = new StringBuilder();
                        sql.append("update produto set id_familiaproduto = " + rst2.getInt("id") + " ");
                        sql.append("where id = " + rst.getInt("id") + ";");
                        stm2.execute(sql.toString());
                    }
                }

                ProgressBar.next();
            }

            stm.close();
            stm2.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    /**
     * *******************************
     */
    public void alterarPrecoProdutoIntegracao(List<ProdutoVO> v_produto, int id_loja) throws Exception {
        Statement stm = null, stm2 = null;
        ResultSet rst = null, rst2 = null;
        StringBuilder sql = null;
        double IdProduto;

        try {

            Conexao.begin();

            stm = Conexao.createStatement();
            stm2 = Conexao.createStatement();

            ProgressBar.setMaximum(v_produto.size());

            ProgressBar.setStatus("Atualizando dados produto...Preço Integraçao Loja " + id_loja + "...");

            for (ProdutoVO i_produto : v_produto) {
                for (ProdutoComplementoVO oComplemento : i_produto.vComplemento) {
                    sql = new StringBuilder();
                    sql.append("select p.id from produto p ");
                    sql.append("inner join produtoautomacao pa on pa.id_produto = p.id ");
                    sql.append("where pa.codigobarras = " + i_produto.codigoBarras + " ");
                    sql.append("and char_length(cast(pa.codigobarras as varchar)) >= 7 ");

                    rst2 = stm2.executeQuery(sql.toString());

                    if (rst2.next()) {

                        sql = new StringBuilder();
                        sql.append("select * from oferta ");
                        sql.append("where id_produto = " + rst2.getInt("id") + " ");
                        sql.append("and id_situacaooferta = 1 ");
                        sql.append("and id_loja = " + id_loja);

                        rst = stm.executeQuery(sql.toString());

                        if (!rst.next()) {

                            sql = new StringBuilder();
                            sql.append("UPDATE produtocomplemento SET ");
                            sql.append("precovenda = " + oComplemento.precoVenda + ", ");
                            sql.append("precodiaseguinte = " + oComplemento.precoDiaSeguinte + " ");
                            sql.append("where id_produto = " + rst2.getInt("id") + " ");
                            sql.append("and id_loja = " + id_loja + " ");
                            sql.append("and dataultimopreco is null ");

                            stm.execute(sql.toString());

                        }
                    }
                }

                ProgressBar.next();
            }

            stm.close();
            stm2.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void alterarCustoProdutoIntegracao(List<ProdutoVO> v_produto, int id_loja) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;
        double IdProduto;
        try {

            Conexao.begin();

            stm = Conexao.createStatement();

            ProgressBar.setMaximum(v_produto.size());

            ProgressBar.setStatus("Atualizando dados produto...Custo Integração Loja " + id_loja + "...");
            for (ProdutoVO i_produto : v_produto) {
                for (ProdutoComplementoVO oComplemento : i_produto.vComplemento) {

                    sql = new StringBuilder();
                    sql.append("select p.id from produto p ");
                    sql.append("inner join produtoautomacao pa on pa.id_produto = p.id ");
                    sql.append("where pa.codigobarras = " + i_produto.codigoBarras + " ");
                    sql.append("and char_length(cast(pa.codigobarras as varchar)) >= 7 ");

                    rst = stm.executeQuery(sql.toString());

                    if (rst.next()) {

                        sql = new StringBuilder();
                        sql.append("UPDATE produtocomplemento SET ");
                        sql.append("custosemimposto = " + oComplemento.custoSemImposto + ", ");
                        sql.append("custocomimposto = " + oComplemento.custoComImposto + " ");
                        sql.append("where id_produto = " + rst.getInt("id") + " ");
                        sql.append("and id_loja = " + id_loja + " ");
                        sql.append("and dataultimaentrada is null ");

                        stm.execute(sql.toString());

                    }
                }

                ProgressBar.next();
            }

            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void alterarEstoqueProdutoIntegracao(List<ProdutoVO> v_produto, int id_loja) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;

        try {
            Conexao.begin();
            stm = Conexao.createStatement();
            ProgressBar.setMaximum(v_produto.size());
            ProgressBar.setStatus("Atualizando dados produto...Estoque Integração Loja " + id_loja + "...");
            for (ProdutoVO i_produto : v_produto) {
                for (ProdutoComplementoVO oComplemento : i_produto.vComplemento) {

                    sql = new StringBuilder();
                    sql.append("select p.id from produto p ");
                    sql.append("inner join produtoautomacao pa on pa.id_produto = p.id ");
                    sql.append("where pa.codigobarras = " + i_produto.codigoBarras + " ");
                    sql.append("and char_length(cast(pa.codigobarras as varchar)) >= 7 ");

                    rst = stm.executeQuery(sql.toString());

                    if (rst.next()) {
                        sql = new StringBuilder();
                        sql.append("UPDATE produtocomplemento SET ");
                        sql.append("estoque = " + oComplemento.estoque + ", ");
                        sql.append("estoqueminimo = " + oComplemento.estoqueMinimo + ", ");
                        sql.append("estoquemaximo = " + oComplemento.estoqueMaximo + " ");
                        sql.append("where id_produto = " + rst.getInt("id") + " ");
                        sql.append("and id_loja = " + id_loja + "; ");
                        stm.execute(sql.toString());
                    }
                }

                ProgressBar.next();
            }
            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void alterarSomaEstoqueProdutoIntegracao(List<ProdutoVO> v_produto, int id_loja) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;

        try {
            Conexao.begin();
            stm = Conexao.createStatement();
            ProgressBar.setMaximum(v_produto.size());
            ProgressBar.setStatus("Atualizando dados produto...Soma Estoque Integração Loja " + id_loja + "...");
            for (ProdutoVO i_produto : v_produto) {
                for (ProdutoComplementoVO oComplemento : i_produto.vComplemento) {

                    sql = new StringBuilder();
                    sql.append("select p.id from produto p ");
                    sql.append("inner join produtoautomacao pa on pa.id_produto = p.id ");
                    sql.append("where pa.codigobarras = " + i_produto.codigoBarras + " ");
                    sql.append("and char_length(cast(pa.codigobarras as varchar)) >= 7 ");

                    rst = stm.executeQuery(sql.toString());

                    if (rst.next()) {
                        sql = new StringBuilder();
                        sql.append("UPDATE produtocomplemento SET ");
                        sql.append("estoque = estoque + (" + oComplemento.estoque + "), ");
                        sql.append("estoqueminimo = " + oComplemento.estoqueMinimo + ", ");
                        sql.append("estoquemaximo = " + oComplemento.estoqueMaximo + " ");
                        sql.append("where id_produto = " + rst.getInt("id") + " ");
                        sql.append("and id_loja = " + id_loja + "; ");
                        stm.execute(sql.toString());
                    }
                }

                ProgressBar.next();
            }
            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void corrigirInformacoes(List<ProdutoVO> vProduto, int idLojaVR, int idLojaCliente, OpcaoProduto... opcoes) throws Exception {
        Set<OpcaoProduto> opts = new LinkedHashSet<>();
        for (int i = 0; i < opcoes.length; i++) {
            opts.add(opcoes[i]);
        }

        Map<Long, FamiliaProdutoVO> familiaProdutoAnteriores = new LinkedHashMap<>();
        if (opts.contains(OpcaoProduto.FAMILIA_PRODUTO)) {
            familiaProdutoAnteriores = new FamiliaProdutoDAO().carregarAnteriores();
        }

        Map<Double, CodigoAnteriorVO> anteriores = new CodigoAnteriorDAO().carregarCodigoAnterior();
        Conexao.begin();
        try {
            try (Statement stm = Conexao.createStatement()) {
                ProgressBar.setStatus("Executando correções nos produtos.... ");
                ProgressBar.setMaximum(vProduto.size());
                for (ProdutoVO produto : vProduto) {
                    double idProduto;
                    if (produto.getIdDouble() > 0) {
                        idProduto = produto.getIdDouble();
                    } else {
                        idProduto = produto.getId();
                    }
                    int opcoesrestantes = opts.size();
                    CodigoAnteriorVO codigoAnterior = anteriores.get(idProduto);
                    if (codigoAnterior != null && !opts.isEmpty()) {
                        StringBuilder sql = new StringBuilder();
                        if (opts.contains(OpcaoProduto.FAMILIA_PRODUTO)) {
                            FamiliaProdutoVO familia = familiaProdutoAnteriores.get((long) produto.getIdFamiliaProduto());
                            if (familia != null) {
                                sql.append("id_familiaproduto = " + familia.getId() + (opcoesrestantes > 1 ? "," : ""));
                            }
                            opcoesrestantes--;
                        }
                        if (!"".equals(sql.toString())) {
                            sql = new StringBuilder(
                                    "update produto set " + sql.toString() + " where id = " + (long) codigoAnterior.getCodigoatual() + ";"
                            );
                            stm.execute(sql.toString());
                        }
                    }
                    ProgressBar.next();
                }
            }
            Conexao.commit();
        } catch (Exception e) {
            Conexao.rollback();
            throw e;
        }
    }

    public static enum OpcaoProduto {

        FAMILIA_PRODUTO
    }

    public int getAliquotaDebito(long i_idProduto) throws Exception {
        Statement stm = null;
        ResultSet rst = null;

        stm = Conexao.createStatement();
        rst = stm.executeQuery("SELECT pa.id_aliquotadebito "
                + "FROM produtoaliquota pa "
                + "inner join implantacao.codigoanterior ant on ant.codigoanterior <> pa.id_produto "
                + "WHERE ant.codigoanterior = " + i_idProduto);

        if (rst.next()) {
            return rst.getInt("id_aliquotadebito");
        } else {
            return -1;
        }
    }

    /* MÉTODO USADO PARA QUANDO FOR IMPORTAR DE UMA BASE VR PARA OUTRA*/
    public void alterarProdutoPrecoVR(List<ProdutoVO> v_produto, int idLoja) throws Exception {

        StringBuilder sql = null;
        Statement stm = null;
        String status = "";

        try {
            Conexao.begin();
            stm = Conexao.createStatement();

            ProgressBar.setMaximum(v_produto.size());
            status = "Preço...";
            status = status + "Loja " + idLoja;

            ProgressBar.setStatus("Importando dados..." + status);

            for (ProdutoVO i_produto : v_produto) {
                for (ProdutoComplementoVO oComplemento : i_produto.vComplemento) {
                    sql = new StringBuilder();
                    sql.append("update produtocomplemento set ");
                    sql.append("precovenda = " + oComplemento.getPrecoVenda() + ", "
                            + "precodiaseguinte = " + oComplemento.getPrecoDiaSeguinte());
                    sql.append(" ");
                    sql.append("where id_loja = " + idLoja + " "
                            + "and id_produto = " + (i_produto.getId() == 0 ? i_produto.getIdDouble() : i_produto.getId()));

                    stm.execute(sql.toString());
                }
                ProgressBar.next();
            }

            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void alterarProdutoCustoVR(List<ProdutoVO> v_produto, int idLoja) throws Exception {

        StringBuilder sql = null;
        Statement stm = null;
        String status = "";

        try {
            Conexao.begin();
            stm = Conexao.createStatement();

            ProgressBar.setMaximum(v_produto.size());
            status = "Custo...";
            status = status + "Loja " + idLoja;

            ProgressBar.setStatus("Importando dados..." + status);

            for (ProdutoVO i_produto : v_produto) {
                for (ProdutoComplementoVO oComplemento : i_produto.vComplemento) {
                    sql = new StringBuilder();
                    sql.append("update produtocomplemento set ");
                    sql.append("custocomimposto = " + oComplemento.getCustoComImposto() + ", "
                            + "custosemimposto = " + oComplemento.getCustoSemImposto());
                    sql.append(" ");
                    sql.append("where id_loja = " + idLoja + " "
                            + "and id_produto = " + (i_produto.getId() == 0 ? i_produto.getIdDouble() : i_produto.getId()));

                    stm.execute(sql.toString());
                }
                ProgressBar.next();
            }

            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void alterarProdutoEstoqueVR(List<ProdutoVO> v_produto, int idLoja) throws Exception {

        StringBuilder sql = null;
        Statement stm = null;
        String status = "";

        try {
            Conexao.begin();
            stm = Conexao.createStatement();

            ProgressBar.setMaximum(v_produto.size());
            status = "Estoque...";
            status = status + "Loja " + idLoja;

            ProgressBar.setStatus("Importando dados..." + status);

            for (ProdutoVO i_produto : v_produto) {
                for (ProdutoComplementoVO oComplemento : i_produto.vComplemento) {
                    sql = new StringBuilder();
                    sql.append("update produtocomplemento set ");
                    sql.append("estoque = " + oComplemento.getEstoque() + ", "
                            + "estoqueminimo = " + oComplemento.getEstoqueMinimo() + ", "
                            + "estoquemaximo = " + oComplemento.getEstoqueMaximo());
                    sql.append(" ");
                    sql.append("where id_loja = " + idLoja + " "
                            + "and id_produto = " + (i_produto.getId() == 0 ? i_produto.getIdDouble() : i_produto.getId()));

                    stm.execute(sql.toString());
                }
                ProgressBar.next();
            }

            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void alterarProdutoEstoqueSomaVR(List<ProdutoVO> v_produto, int idLoja) throws Exception {

        StringBuilder sql = null;
        Statement stm = null;
        String status = "";

        try {
            Conexao.begin();
            stm = Conexao.createStatement();

            ProgressBar.setMaximum(v_produto.size());
            status = "Soma Estoque...";
            status = status + "Loja " + idLoja;

            ProgressBar.setStatus("Importando dados..." + status);

            for (ProdutoVO i_produto : v_produto) {
                for (ProdutoComplementoVO oComplemento : i_produto.vComplemento) {
                    sql = new StringBuilder();
                    sql.append("update produtocomplemento set ");
                    sql.append("estoque = estoque + (" + oComplemento.getEstoque() + "), "
                            + "estoqueminimo = " + oComplemento.getEstoqueMinimo() + ", "
                            + "estoquemaximo = " + oComplemento.getEstoqueMaximo());
                    sql.append(" ");
                    sql.append("where id_loja = " + idLoja + " "
                            + "and id_produto = " + (i_produto.getId() == 0 ? i_produto.getIdDouble() : i_produto.getId()));

                    stm.execute(sql.toString());
                }
                ProgressBar.next();
            }

            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void gerarArquivoAnalise(String i_arquivo, List<ProdutoVO> v_produto, int idLoja, int opcao) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        String nomeArquivo;

        if (opcao == 0) {
            nomeArquivo = "precoDiferente.csv";
        } else {
            nomeArquivo = "produtosNaoCadastros.csv";
        }

        File f = new File(i_arquivo + "\\" + nomeArquivo);
        FileWriter fw = new FileWriter(f);
        BufferedWriter bw = new BufferedWriter(fw);
        try {
            Conexao.begin();
            stm = Conexao.createStatement();

            ProgressBar.setMaximum(v_produto.size());
            ProgressBar.setStatus("Gerando arquivo de análise...");

            if (opcao == 0) {
                bw.write("CODIGOBARRAS  DESCRICAO   PRECO_MRS   PRECO_VR");
            } else {
                bw.write("CODIGOBARRAS  DESCRICAO");
            }

            bw.newLine();

            for (ProdutoVO i_produto : v_produto) {

                if (opcao == 0) {
                    sql = new StringBuilder();
                    sql.append("select pa.codigobarras, p.descricaocompleta, pc.precovenda "
                            + "from produto p "
                            + "inner join produtocomplemento pc on pc.id_produto = p.id "
                            + "inner join produtoautomacao pa on pa.id_produto = p.id "
                            + "where pc.id_loja = " + idLoja + " "
                            + "and pa.codigobarras = " + i_produto.codigoBarras);
                    rst = stm.executeQuery(sql.toString());
                    if (rst.next()) {

                        if (i_produto.precoVenda != rst.getDouble("precovenda")) {

                            bw.write(rst.getLong("codigobarras") + "  " + rst.getString("descricaocompleta")
                                    + "   " + i_produto.precoVenda + "    " + rst.getDouble("precovenda"));
                            bw.newLine();
                        }
                    }
                } else {
                    sql = new StringBuilder();
                    sql.append("select pa.codigobarras, p.descricaocompleta "
                            + "from produto p "
                            + "inner join produtoautomacao pa on pa.id_produto = p.id "
                            + "where pa.codigobarras = " + i_produto.codigoBarras);
                    rst = stm.executeQuery(sql.toString());

                    if (!rst.next()) {
                        bw.write(i_produto.codigoBarras + ";" + i_produto.descricaoCompleta);
                        bw.newLine();
                    }
                }

                ProgressBar.next();
            }
            bw.flush();
            bw.close();
            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void importarProdutoRestaurante(List<ProdutoVO> v_result, int idLojaVR, List<LojaVO> vLoja) throws Exception {
        StringBuilder sql = null, sql2 = null;
        Statement stm = null, stm2 = null, stm3 = null;
        ResultSet rst = null, rst2 = null, rst3 = null;
        CestDAO cestDAO = new CestDAO();
        MercadologicoAnteriorDAO mercadologicoAnteriorDao = new MercadologicoAnteriorDAO();
        try {
            Conexao.begin();
            stm = Conexao.createStatement();
            stm2 = Conexao.createStatement();
            stm3 = Conexao.createStatement();
            ProgressBar.setMaximum(v_result.size());
            ProgressBar.setStatus("Importando dados...Produtos Restaurante..." + idLojaVR + "...");

            for (ProdutoVO i_produto : v_result) {
                for (ProdutoAutomacaoVO oAutomacao2 : i_produto.vAutomacao) {
                    if (oAutomacao2.getCodigoBarras() != -2) {

                        sql = new StringBuilder();
                        sql.append("select id_produto from produtoautomacao "
                                + "where codigobarras = " + oAutomacao2.getCodigoBarras());
                        rst = stm.executeQuery(sql.toString());

                        if (!rst.next()) {
                            sql = new StringBuilder();
                            sql.append("select p.id, p.id_tipoembalagem from produto p "
                                    + "inner join implantacao.codigoanterior ant "
                                    + "on ant.codigoatual = p.id "
                                    + "where ant.codigoanterior = " + i_produto.getId() + " ");

                            if (verificarLoja) {
                                sql.append(" and ant.id_loja = " + idLojaVR);
                            }

                            rst2 = stm.executeQuery(sql.toString());

                            if (rst2.next()) {
                                sql = new StringBuilder();
                                sql.append("insert into produtoautomacao (id_produto, codigobarras, qtdembalagem, id_tipoembalagem) "
                                        + "values ("
                                        + i_produto.getId() + ", "
                                        + oAutomacao2.getCodigoBarras() + ", "
                                        + "1, "
                                        + rst2.getInt("id_tipoembalagem") + ");");
                                stm2.execute(sql.toString());
                            } else {

                                sql = new StringBuilder();

                                sql.append("INSERT INTO produto (id, descricaocompleta, qtdembalagem, id_tipoembalagem, mercadologico1, mercadologico2, mercadologico3,");
                                sql.append(" mercadologico4, mercadologico5, id_comprador, id_familiaproduto, descricaoreduzida, pesoliquido, datacadastro,");
                                sql.append(" validade, pesobruto, tara, comprimentoembalagem, larguraembalagem, alturaembalagem, perda, margem, verificacustotabela,");
                                sql.append(" descricaogondola, dataalteracao, id_produtovasilhame, ncm1, ncm2, ncm3, excecao, id_tipomercadoria, fabricacaopropria,");
                                sql.append(" sugestaopedido, sugestaocotacao, aceitamultiplicacaopdv, id_fornecedorfabricante, id_divisaofornecedor, id_tipoproduto, id_tipopiscofins,");
                                sql.append(" id_tipopiscofinscredito, custofinal, percentualipi, percentualfrete, percentualencargo, percentualperda, percentualsubstituicao, pesavel,");
                                sql.append(" sazonal, consignado, ddv, permitetroca, temperatura, id_tipoorigemmercadoria, ipi, vendacontrolada, tiponaturezareceita,");
                                sql.append(" vendapdv, permitequebra, permiteperda, impostomedioimportado, impostomedionacional, impostomedioestadual, utilizatabelasubstituicaotributaria,");
                                sql.append(" utilizavalidadeentrada, id_tipolocaltroca, id_tipocompra, codigoanp, numeroparcela, qtddiasminimovalidade, id_cest, id_normareposicao)");

                                sql.append(" VALUES ");
                                sql.append(" (");

                                if (!implantacaoExterna) {

                                    if (i_produto.id > 0) {
                                        IdProduto = i_produto.id;
                                    } else {
                                        IdProduto = i_produto.idDouble;
                                    }

                                    if ((IdProduto <= 0) || (IdProduto >= 1000000)) {

                                        if (i_produto.eBalanca) {
                                            IdProduto = new CodigoInternoDAO().getIdProdutoBalanca();
                                        } else {
                                            IdProduto = new CodigoInternoDAO().getIdProduto(qtdeCodigoInterno);
                                        }
                                    }
                                } else {

                                    if (i_produto.eBalanca) {
                                        if (!gerarCodigo) {
                                            if ((IdProduto >= 1000000) || (IdProduto <= 0)) {
                                                IdProduto = new CodigoInternoDAO().getIdProdutoBalanca();
                                            }
                                        } else {
                                            IdProduto = new CodigoInternoDAO().getIdProdutoBalanca();
                                        }
                                    } else {
                                        if (!gerarCodigo) {
                                            if ((IdProduto >= 1000000) || (IdProduto <= 0)) {
                                                IdProduto = new CodigoInternoDAO().getIdProduto(qtdeCodigoInterno);
                                            }
                                        } else {
                                            IdProduto = new CodigoInternoDAO().getIdProduto(qtdeCodigoInterno);
                                        }
                                    }
                                }

                                if ("".equals(i_produto.dataCadastro.trim())) {
                                    i_produto.dataCadastro = new DataProcessamentoDAO().get();
                                }

                                if (implantacaoExterna) {

                                    sql2 = new StringBuilder();
                                    sql2.append("select id from produto ");
                                    sql2.append("where id = " + IdProduto);

                                    rst3 = stm3.executeQuery(sql2.toString());

                                    if (rst3.next()) {
                                        if (i_produto.eBalanca) {
                                            IdProduto = new CodigoInternoDAO().getIdProdutoBalanca();
                                        } else {
                                            IdProduto = new CodigoInternoDAO().getIdProduto(qtdeCodigoInterno);
                                        }
                                    }
                                }

                                sql.append(IdProduto + ",");
                                sql.append("'" + i_produto.descricaoCompleta + "',");
                                sql.append(i_produto.qtdEmbalagem + ",");
                                sql.append(i_produto.idTipoEmbalagem + ",");

                                MercadologicoVO mercadologico
                                        = mercadologicoAnteriorDao.makeMercadologicoIntegracao(
                                                i_produto.mercadologico1,
                                                i_produto.mercadologico2,
                                                i_produto.mercadologico3,
                                                i_produto.mercadologico4,
                                                i_produto.mercadologico5);

                                if (mercadologico.mercadologico1 == 0
                                        || mercadologico.mercadologico2 == 0
                                        || mercadologico.mercadologico3 == 0) {
                                    mercadologico = MercadologicoDAO.getMaxMercadologico();
                                }

                                sql.append(mercadologico.mercadologico1 + ",");
                                sql.append(mercadologico.mercadologico2 + ",");
                                sql.append(mercadologico.mercadologico3 + ",");
                                sql.append(mercadologico.mercadologico4 + ",");
                                sql.append(mercadologico.mercadologico5 + ",");
                                sql.append(i_produto.idComprador + ",");
                                sql.append("null,");
                                sql.append("'" + i_produto.descricaoReduzida + "',");
                                sql.append(Utils.truncar(i_produto.pesoLiquido, 3) + ",");
                                sql.append("'" + Util.formatDataBanco(i_produto.dataCadastro) + "',");
                                sql.append(i_produto.validade + ",");
                                sql.append(Utils.truncar(i_produto.pesoBruto, 3) + ",");
                                sql.append(i_produto.tara + ",");
                                sql.append(i_produto.comprimentoEmbalagem + ",");
                                sql.append(i_produto.larguraEmbalagem + ",");
                                sql.append(i_produto.alturaEmbalagem + ",");
                                sql.append(i_produto.perda + ",");
                                sql.append(i_produto.margem + ",");
                                sql.append(i_produto.verificaCustoTabela + ",");
                                sql.append("'" + i_produto.descricaoGondola + "',");
                                sql.append((i_produto.dataAlteracao.isEmpty() ? null : "'" + Util.formatDataBanco(i_produto.dataAlteracao) + "'") + ",");
                                sql.append((i_produto.idProdutoVasilhame == -1 ? null : i_produto.idProdutoVasilhame) + ",");
                                sql.append((i_produto.ncm1 == -1 ? null : i_produto.ncm1) + ",");
                                sql.append((i_produto.ncm2 == -1 ? null : i_produto.ncm2) + ",");
                                sql.append((i_produto.ncm3 == -1 ? null : i_produto.ncm3) + ",");
                                sql.append((i_produto.excecao == -1 ? null : i_produto.excecao) + ",");
                                sql.append((i_produto.idTipoMercadoria == -1 ? null : i_produto.idTipoMercadoria) + ",");
                                sql.append(i_produto.fabricacaoPropria + ",");
                                sql.append(i_produto.sugestaoPedido + ",");
                                sql.append(i_produto.sugestaoCotacao + ",");
                                sql.append(i_produto.aceitaMultiplicacaoPdv + ",");
                                sql.append(i_produto.idFornecedorFabricante + ",");
                                sql.append(i_produto.idDivisaoFornecedor + ",");
                                sql.append(i_produto.idTipoProduto + ",");
                                sql.append(i_produto.idTipoPisCofinsDebito + ",");
                                sql.append(i_produto.idTipoPisCofinsCredito + ",");
                                sql.append(i_produto.custoFinal + ",");
                                sql.append(i_produto.percentualIpi + ",");
                                sql.append(i_produto.percentualFrete + ",");
                                sql.append(i_produto.percentualEncargo + ",");
                                sql.append(i_produto.percentualPerda + ",");
                                sql.append(i_produto.percentualSubstituicao + ",");
                                sql.append(i_produto.pesavel + ",");
                                sql.append(i_produto.sazonal + ",");
                                sql.append(i_produto.consignado + ",");
                                sql.append(i_produto.ddv + ",");
                                sql.append(i_produto.permiteTroca + ",");
                                sql.append(i_produto.temperatura + ",");
                                sql.append(i_produto.idTipoOrigemMercadoria + ",");
                                sql.append(i_produto.ipi + ",");
                                sql.append(i_produto.vendaControlada + ",");
                                sql.append((i_produto.tipoNaturezaReceita == -1 ? null : i_produto.tipoNaturezaReceita) + ",");
                                sql.append(i_produto.vendaPdv + ",");
                                sql.append(i_produto.permiteQuebra + ",");
                                sql.append(i_produto.permitePerda + ",");
                                sql.append(i_produto.impostoMedioImportado + ",");
                                sql.append(i_produto.impostoMedioNacional + ",");
                                sql.append(i_produto.impostoMedioEstadual + ",");
                                sql.append(i_produto.utilizaTabelaSubstituicaoTributaria + ",");
                                sql.append(i_produto.utilizaValidadeEntrada + ",");
                                sql.append(i_produto.idTipoLocalTroca + ",");
                                sql.append(i_produto.idTipoCompra + ",");
                                sql.append("'" + i_produto.codigoAnp + "',");
                                sql.append(i_produto.numeroParcela + ",");
                                sql.append(i_produto.qtdDiasMinimoValidade + ",");

                                if (i_produto.getCest1() != -1
                                        && i_produto.getCest2() != -1
                                        && i_produto.getCest3() != -1) {
                                    CestVO cest = cestDAO.getCestValido(i_produto.getCest1(), i_produto.getCest2(), i_produto.getCest3());
                                    sql.append(Utils.longIntSQL(cest.getId(), 0) + ",");
                                } else {
                                    sql.append("null,");
                                }

                                sql.append(Utils.longIntSQL(i_produto.getIdNormaReposicao(), -1) + ");");

                                try {
                                    stm.execute(sql.toString());
                                } catch (Exception ex) {
                                    JOptionPane.showMessageDialog(null, "Erro " + IdProduto + " - " + sql.toString() + ex);
                                    throw ex;
                                }

                                for (ProdutoComplementoVO oComplemento : i_produto.vComplemento) {
                                    for (LojaVO oLoja : vLoja) {
                                        sql = new StringBuilder();
                                        sql.append("INSERT INTO produtocomplemento (id_produto, prateleira, secao,estoqueminimo,");
                                        sql.append(" estoquemaximo,valoripi,dataultimopreco,dataultimaentrada,");
                                        sql.append(" custosemimposto,custocomimposto,custosemimpostoanterior,");
                                        sql.append(" custocomimpostoanterior,precovenda,precovendaanterior,");
                                        sql.append(" precodiaseguinte,estoque,troca,emiteetiqueta,custosemperdasemimposto,");
                                        sql.append(" custosemperdasemimpostoanterior,customediocomimposto,customediosemimposto,");
                                        sql.append(" id_aliquotacredito,dataultimavenda,teclaassociada,id_situacaocadastro,");
                                        sql.append(" id_loja,descontinuado,quantidadeultimaentrada,centralizado,operacional,valoricmssubstituicao,");
                                        sql.append(" dataultimaentradaanterior,cestabasica,customediocomimpostoanterior,customediosemimpostoanterior,id_tipopiscofinscredito,valoroutrassubstituicao)");

                                        sql.append(" VALUES (");
                                        sql.append(IdProduto + ",");
                                        sql.append("'" + oComplemento.prateleira + "',");
                                        sql.append("'" + oComplemento.secao + "',");
                                        sql.append(oComplemento.estoqueMinimo + ",");
                                        sql.append(oComplemento.estoqueMaximo + ",");
                                        sql.append(oComplemento.valorIpi + ",");
                                        sql.append((oComplemento.dataUltimoPreco.isEmpty() ? null : "'" + Util.formatDataBanco(oComplemento.dataUltimoPreco) + "'") + ",");
                                        sql.append((oComplemento.dataUltimaEntrada.isEmpty() ? null : "'" + Util.formatDataBanco(oComplemento.dataUltimaEntrada) + "'") + ",");
                                        if (IdProduto == 72365) {
                                            Util.exibirMensagem(oComplemento.custoComImposto + ", " + oComplemento.custoSemImposto, "");
                                            Util.exibirMensagem(Utils.arredondar(oComplemento.custoComImposto, 3) + ", " + Utils.arredondar(oComplemento.custoSemImposto, 3), "");
                                        }
                                        sql.append(oComplemento.custoSemImposto + ",");
                                        sql.append(oComplemento.custoComImposto + ",");
                                        sql.append(oComplemento.custoSemImpostoAnterior + ",");
                                        sql.append(oComplemento.custoComImpostoAnterior + ",");
                                        sql.append(oComplemento.precoVenda + ",");
                                        sql.append(oComplemento.precoVendaAnterior + ",");
                                        sql.append(oComplemento.precoDiaSeguinte + ",");
                                        sql.append(oComplemento.estoque + ",");
                                        sql.append(oComplemento.troca + ",");
                                        sql.append(oComplemento.emiteEtiqueta + ",");
                                        sql.append(oComplemento.custoSemPerdaSemImposto + ",");
                                        sql.append(oComplemento.custoSemPerdaSemImpostoAnterior + ",");
                                        sql.append(oComplemento.custoMedioComImposto + ",");
                                        sql.append(oComplemento.custoMedioSemImposto + ",");
                                        sql.append(oComplemento.idAliquotaCredito + ",");
                                        sql.append((oComplemento.dataUltimaVenda.isEmpty() ? null : "'" + Util.formatDataBanco(oComplemento.dataUltimaVenda) + "'") + ",");
                                        sql.append(oComplemento.teclaAssociada + ",");
                                        sql.append(oComplemento.idSituacaoCadastro + ",");
                                        sql.append(oLoja.id + ",");
                                        sql.append(oComplemento.descontinuado + ",");
                                        sql.append(oComplemento.quantidadeUltimaEntrada + ",");
                                        sql.append(oComplemento.centralizado + ",");
                                        sql.append(oComplemento.operacional + ",");
                                        sql.append(oComplemento.valorIcmsSubstituicao + ",");
                                        sql.append((oComplemento.dataUltimaEntradaAnterior.isEmpty() ? null : "'" + Util.formatDataBanco(oComplemento.dataUltimaEntradaAnterior) + "'") + ",");
                                        sql.append(oComplemento.cestaBasica + ",");
                                        sql.append(oComplemento.custoMedioComImpostoAnterior + ",");
                                        sql.append(oComplemento.custoMedioSemImpostoAnterior + ",");
                                        sql.append(oComplemento.idTipoPisCofinsCredito + ",");
                                        sql.append(oComplemento.valorOutrasSubstituicao + ");");
                                        stm.execute(sql.toString());
                                    }
                                }
                                for (ProdutoAutomacaoVO oAutomacao : i_produto.vAutomacao) {
                                    if (oAutomacao.codigoBarras == -1) {
                                        if ((IdProduto <= 9999)
                                                && (i_produto.idTipoEmbalagem != 4)) {
                                            oAutomacao.codigoBarras = Utils.gerarEan13((int) IdProduto, false);
                                        } else if ((IdProduto > 9999)
                                                && (i_produto.idTipoEmbalagem != 4)
                                                && (!i_produto.pesavel)) {
                                            oAutomacao.codigoBarras = Utils.gerarEan13((int) IdProduto, true);
                                        } else if ((IdProduto > 9999)
                                                && (i_produto.idTipoEmbalagem != 4)
                                                && (i_produto.pesavel)) {
                                            oAutomacao.codigoBarras = Utils.gerarEan13((int) IdProduto, false);
                                        } else if ((i_produto.idTipoEmbalagem != 4)
                                                && (i_produto.pesavel)) {
                                            oAutomacao.codigoBarras = Utils.gerarEan13((int) IdProduto, false);
                                        } else if (i_produto.idTipoEmbalagem == 4) {
                                            oAutomacao.codigoBarras = Utils.gerarEan13((int) IdProduto, false);
                                        } else {
                                            oAutomacao.codigoBarras = Utils.gerarEan13((int) IdProduto, true);
                                        }
                                    }

                                    if (String.valueOf(oAutomacao.codigoBarras).length() > 14) {
                                        oAutomacao.codigoBarras = Long.parseLong(String.valueOf(oAutomacao.codigoBarras).substring(0, 14));
                                    }

                                    if (oAutomacao.codigoBarras > -2) {

                                        sql = new StringBuilder();
                                        sql.append("SELECT id_produto FROM produtoautomacao");
                                        sql.append(" WHERE cast(codigobarras as numeric(14,0)) = cast(" + oAutomacao.codigoBarras + " as numeric(14,0)) ");
                                        rst = stm.executeQuery(sql.toString());

                                        if (!rst.next()) {
                                            sql = new StringBuilder();
                                            sql.append("INSERT INTO produtoautomacao (id_produto, codigobarras, qtdembalagem, id_tipoembalagem)");
                                            sql.append("VALUES (");
                                            sql.append(IdProduto + ",");
                                            sql.append(oAutomacao.codigoBarras + ",");
                                            sql.append(oAutomacao.qtdEmbalagem + ",");
                                            sql.append(oAutomacao.idTipoEmbalagem);
                                            sql.append(")");

                                            stm.execute(sql.toString());
                                        }
                                    }
                                }

                                for (ProdutoAliquotaVO oAliquota : i_produto.vAliquota) {
                                    sql = new StringBuilder();

                                    sql.append("INSERT INTO produtoaliquota (id_produto, id_estado, id_aliquotadebito, id_aliquotacredito, id_aliquotadebitoforaestado,");
                                    sql.append(" id_aliquotacreditoforaestado, id_aliquotadebitoforaestadonf, id_aliquotaconsumidor)");
                                    sql.append(" VALUES(");

                                    sql.append(IdProduto + ",");
                                    sql.append(oAliquota.idEstado + ",");
                                    sql.append(oAliquota.idAliquotaDebito + ",");
                                    sql.append(oAliquota.idAliquotaCredito + ",");
                                    sql.append(oAliquota.idAliquotaDebitoForaEstado + ",");
                                    sql.append(oAliquota.idAliquotaCreditoForaEstado + ",");
                                    sql.append(oAliquota.idAliquotaDebitoForaEstadoNF + ",");
                                    sql.append(oAliquota.getIdAliquotaConsumidor());

                                    sql.append(")");

                                    stm.execute(sql.toString());

                                }

                                for (CodigoAnteriorVO oCodigoInterno : i_produto.vCodigoAnterior) {
                                    oCodigoInterno.codigoatual = IdProduto;

                                    if (String.valueOf(oCodigoInterno.barras).length() > 14) {
                                        oCodigoInterno.barras = Long.parseLong(String.valueOf(oCodigoInterno.barras).substring(0, 14));
                                    }

                                    sql = new StringBuilder();
                                    sql.append("INSERT INTO implantacao.codigoanterior( ");
                                    sql.append("codigoanterior, codigoatual, barras, naturezareceita, ");
                                    sql.append("piscofinscredito, piscofinsdebito, ref_icmsdebito, estoque, e_balanca, ");
                                    sql.append("codigobalanca, custosemimposto, custocomimposto, margem, precovenda, referencia, ncm, id_loja, produto_novo, codigoauxiliar) ");
                                    sql.append("VALUES ( ");
                                    sql.append(oCodigoInterno.codigoanterior + ", ");
                                    sql.append(IdProduto + ", ");
                                    sql.append((oCodigoInterno.barras == -1 ? null : oCodigoInterno.barras) + ", ");
                                    sql.append((oCodigoInterno.naturezareceita == -1 ? null : oCodigoInterno.naturezareceita) + ", ");
                                    sql.append((oCodigoInterno.piscofinscredito == -1 ? null : oCodigoInterno.piscofinscredito) + ", ");
                                    sql.append((oCodigoInterno.piscofinsdebito == -1 ? null : oCodigoInterno.piscofinsdebito) + ", ");
                                    sql.append((oCodigoInterno.ref_icmsdebito.isEmpty() ? null : "'" + oCodigoInterno.ref_icmsdebito + "'") + ", ");
                                    sql.append((oCodigoInterno.estoque == -1 ? null : oCodigoInterno.estoque) + ", ");
                                    sql.append(oCodigoInterno.e_balanca + ", ");
                                    sql.append((oCodigoInterno.codigobalanca == -1 ? null : oCodigoInterno.codigobalanca) + ", ");
                                    sql.append((oCodigoInterno.custosemimposto == -1 ? null : oCodigoInterno.custosemimposto) + ", ");
                                    sql.append((oCodigoInterno.custocomimposto == -1 ? null : oCodigoInterno.custocomimposto) + ", ");
                                    sql.append((oCodigoInterno.margem == -1 ? null : oCodigoInterno.margem) + ", ");
                                    sql.append((oCodigoInterno.precovenda == -1 ? null : oCodigoInterno.precovenda) + ", ");
                                    sql.append((oCodigoInterno.referencia == -1 ? null : oCodigoInterno.referencia) + ", ");
                                    sql.append((oCodigoInterno.ncm.isEmpty() ? null : "'" + oCodigoInterno.ncm + "'") + ", ");
                                    sql.append(idLojaVR + ", true, ");
                                    sql.append(Utils.quoteSQL(oCodigoInterno.getCodigoAuxiliar()));

                                    sql.append(");");

                                    stm.execute(sql.toString());
                                }
                            }
                        } else {
                            sql = new StringBuilder();
                            sql.append("update produtocomplemento "
                                    + "set precovenda = " + i_produto.precoVenda + ", "
                                    + "precodiaseguinte = " + i_produto.precoVenda + " "
                                    + "where id_produto = " + rst.getInt("id_produto") + " "
                                    + "and id_loja = " + idLojaVR);
                            stm.execute(sql.toString());
                        }
                    } else {
                        sql = new StringBuilder();
                        sql.append("select p.id from produto p "
                                + "inner join implantacao.codigoanterior ant "
                                + "on ant.codigoatual = p.id "
                                + "where ant.codigoanterior = " + i_produto.getId() + " ");

                        if (verificarLoja) {
                            sql.append(" and ant.id_loja = " + idLojaVR);
                        }

                        rst2 = stm.executeQuery(sql.toString());

                        if (!rst2.next()) {

                            sql = new StringBuilder();

                            sql.append("INSERT INTO produto (id, descricaocompleta, qtdembalagem, id_tipoembalagem, mercadologico1, mercadologico2, mercadologico3,");
                            sql.append(" mercadologico4, mercadologico5, id_comprador, id_familiaproduto, descricaoreduzida, pesoliquido, datacadastro,");
                            sql.append(" validade, pesobruto, tara, comprimentoembalagem, larguraembalagem, alturaembalagem, perda, margem, verificacustotabela,");
                            sql.append(" descricaogondola, dataalteracao, id_produtovasilhame, ncm1, ncm2, ncm3, excecao, id_tipomercadoria, fabricacaopropria,");
                            sql.append(" sugestaopedido, sugestaocotacao, aceitamultiplicacaopdv, id_fornecedorfabricante, id_divisaofornecedor, id_tipoproduto, id_tipopiscofins,");
                            sql.append(" id_tipopiscofinscredito, custofinal, percentualipi, percentualfrete, percentualencargo, percentualperda, percentualsubstituicao, pesavel,");
                            sql.append(" sazonal, consignado, ddv, permitetroca, temperatura, id_tipoorigemmercadoria, ipi, vendacontrolada, tiponaturezareceita,");
                            sql.append(" vendapdv, permitequebra, permiteperda, impostomedioimportado, impostomedionacional, impostomedioestadual, utilizatabelasubstituicaotributaria,");
                            sql.append(" utilizavalidadeentrada, id_tipolocaltroca, id_tipocompra, codigoanp, numeroparcela, qtddiasminimovalidade, id_cest, id_normareposicao)");

                            sql.append(" VALUES ");
                            sql.append(" (");

                            if (!implantacaoExterna) {

                                if (i_produto.id > 0) {
                                    IdProduto = i_produto.id;
                                } else {
                                    IdProduto = i_produto.idDouble;
                                }

                                if ((IdProduto <= 0) || (IdProduto >= 1000000)) {

                                    if (i_produto.eBalanca) {
                                        IdProduto = new CodigoInternoDAO().getIdProdutoBalanca();
                                    } else {
                                        IdProduto = new CodigoInternoDAO().getIdProduto(qtdeCodigoInterno);
                                    }
                                }
                            } else {

                                if (i_produto.eBalanca) {
                                    if (!gerarCodigo) {
                                        if ((IdProduto >= 1000000) || (IdProduto <= 0)) {
                                            IdProduto = new CodigoInternoDAO().getIdProdutoBalanca();
                                        }
                                    } else {
                                        IdProduto = new CodigoInternoDAO().getIdProdutoBalanca();
                                    }
                                } else {
                                    if (!gerarCodigo) {
                                        if ((IdProduto >= 1000000) || (IdProduto <= 0)) {
                                            IdProduto = new CodigoInternoDAO().getIdProduto(qtdeCodigoInterno);
                                        }
                                    } else {
                                        IdProduto = new CodigoInternoDAO().getIdProduto(qtdeCodigoInterno);
                                    }
                                }
                            }

                            if ("".equals(i_produto.dataCadastro.trim())) {
                                i_produto.dataCadastro = new DataProcessamentoDAO().get();
                            }

                            if (implantacaoExterna) {

                                sql2 = new StringBuilder();
                                sql2.append("select id from produto ");
                                sql2.append("where id = " + IdProduto);

                                rst3 = stm3.executeQuery(sql2.toString());

                                if (rst3.next()) {
                                    if (i_produto.eBalanca) {
                                        IdProduto = new CodigoInternoDAO().getIdProdutoBalanca();
                                    } else {
                                        IdProduto = new CodigoInternoDAO().getIdProduto(qtdeCodigoInterno);
                                    }
                                }
                            }

                            sql.append(IdProduto + ",");
                            sql.append("'" + i_produto.descricaoCompleta + "',");
                            sql.append(i_produto.qtdEmbalagem + ",");
                            sql.append(i_produto.idTipoEmbalagem + ",");

                            MercadologicoVO mercadologico
                                    = mercadologicoAnteriorDao.makeMercadologicoIntegracao(
                                            i_produto.mercadologico1,
                                            i_produto.mercadologico2,
                                            i_produto.mercadologico3,
                                            i_produto.mercadologico4,
                                            i_produto.mercadologico5);

                            if (mercadologico.mercadologico1 == 0
                                    || mercadologico.mercadologico2 == 0
                                    || mercadologico.mercadologico3 == 0) {
                                mercadologico = MercadologicoDAO.getMaxMercadologico();
                            }

                            sql.append(mercadologico.mercadologico1 + ",");
                            sql.append(mercadologico.mercadologico2 + ",");
                            sql.append(mercadologico.mercadologico3 + ",");
                            sql.append(mercadologico.mercadologico4 + ",");
                            sql.append(mercadologico.mercadologico5 + ",");
                            sql.append(i_produto.idComprador + ",");
                            sql.append("null,");
                            sql.append("'" + i_produto.descricaoReduzida + "',");
                            sql.append(Utils.truncar(i_produto.pesoLiquido, 3) + ",");
                            sql.append("'" + Util.formatDataBanco(i_produto.dataCadastro) + "',");
                            sql.append(i_produto.validade + ",");
                            sql.append(Utils.truncar(i_produto.pesoBruto, 3) + ",");
                            sql.append(i_produto.tara + ",");
                            sql.append(i_produto.comprimentoEmbalagem + ",");
                            sql.append(i_produto.larguraEmbalagem + ",");
                            sql.append(i_produto.alturaEmbalagem + ",");
                            sql.append(i_produto.perda + ",");
                            sql.append(i_produto.margem + ",");
                            sql.append(i_produto.verificaCustoTabela + ",");
                            sql.append("'" + i_produto.descricaoGondola + "',");
                            sql.append((i_produto.dataAlteracao.isEmpty() ? null : "'" + Util.formatDataBanco(i_produto.dataAlteracao) + "'") + ",");
                            sql.append((i_produto.idProdutoVasilhame == -1 ? null : i_produto.idProdutoVasilhame) + ",");
                            sql.append((i_produto.ncm1 == -1 ? null : i_produto.ncm1) + ",");
                            sql.append((i_produto.ncm2 == -1 ? null : i_produto.ncm2) + ",");
                            sql.append((i_produto.ncm3 == -1 ? null : i_produto.ncm3) + ",");
                            sql.append((i_produto.excecao == -1 ? null : i_produto.excecao) + ",");
                            sql.append((i_produto.idTipoMercadoria == -1 ? null : i_produto.idTipoMercadoria) + ",");
                            sql.append(i_produto.fabricacaoPropria + ",");
                            sql.append(i_produto.sugestaoPedido + ",");
                            sql.append(i_produto.sugestaoCotacao + ",");
                            sql.append(i_produto.aceitaMultiplicacaoPdv + ",");
                            sql.append(i_produto.idFornecedorFabricante + ",");
                            sql.append(i_produto.idDivisaoFornecedor + ",");
                            sql.append(i_produto.idTipoProduto + ",");
                            sql.append(i_produto.idTipoPisCofinsDebito + ",");
                            sql.append(i_produto.idTipoPisCofinsCredito + ",");
                            sql.append(i_produto.custoFinal + ",");
                            sql.append(i_produto.percentualIpi + ",");
                            sql.append(i_produto.percentualFrete + ",");
                            sql.append(i_produto.percentualEncargo + ",");
                            sql.append(i_produto.percentualPerda + ",");
                            sql.append(i_produto.percentualSubstituicao + ",");
                            sql.append(i_produto.pesavel + ",");
                            sql.append(i_produto.sazonal + ",");
                            sql.append(i_produto.consignado + ",");
                            sql.append(i_produto.ddv + ",");
                            sql.append(i_produto.permiteTroca + ",");
                            sql.append(i_produto.temperatura + ",");
                            sql.append(i_produto.idTipoOrigemMercadoria + ",");
                            sql.append(i_produto.ipi + ",");
                            sql.append(i_produto.vendaControlada + ",");
                            sql.append((i_produto.tipoNaturezaReceita == -1 ? null : i_produto.tipoNaturezaReceita) + ",");
                            sql.append(i_produto.vendaPdv + ",");
                            sql.append(i_produto.permiteQuebra + ",");
                            sql.append(i_produto.permitePerda + ",");
                            sql.append(i_produto.impostoMedioImportado + ",");
                            sql.append(i_produto.impostoMedioNacional + ",");
                            sql.append(i_produto.impostoMedioEstadual + ",");
                            sql.append(i_produto.utilizaTabelaSubstituicaoTributaria + ",");
                            sql.append(i_produto.utilizaValidadeEntrada + ",");
                            sql.append(i_produto.idTipoLocalTroca + ",");
                            sql.append(i_produto.idTipoCompra + ",");
                            sql.append("'" + i_produto.codigoAnp + "',");
                            sql.append(i_produto.numeroParcela + ",");
                            sql.append(i_produto.qtdDiasMinimoValidade + ",");

                            if (i_produto.getCest1() != -1
                                    && i_produto.getCest2() != -1
                                    && i_produto.getCest3() != -1) {
                                CestVO cest = cestDAO.getCestValido(i_produto.getCest1(), i_produto.getCest2(), i_produto.getCest3());
                                sql.append(Utils.longIntSQL(cest.getId(), 0) + ",");
                            } else {
                                sql.append("null,");
                            }

                            sql.append(Utils.longIntSQL(i_produto.getIdNormaReposicao(), -1) + ");");

                            try {
                                stm.execute(sql.toString());
                            } catch (Exception ex) {
                                JOptionPane.showMessageDialog(null, "Erro " + IdProduto + " - " + sql.toString() + ex);
                                throw ex;
                            }

                            for (ProdutoComplementoVO oComplemento : i_produto.vComplemento) {
                                for (LojaVO oLoja : vLoja) {
                                    sql = new StringBuilder();
                                    sql.append("INSERT INTO produtocomplemento (id_produto, prateleira, secao,estoqueminimo,");
                                    sql.append(" estoquemaximo,valoripi,dataultimopreco,dataultimaentrada,");
                                    sql.append(" custosemimposto,custocomimposto,custosemimpostoanterior,");
                                    sql.append(" custocomimpostoanterior,precovenda,precovendaanterior,");
                                    sql.append(" precodiaseguinte,estoque,troca,emiteetiqueta,custosemperdasemimposto,");
                                    sql.append(" custosemperdasemimpostoanterior,customediocomimposto,customediosemimposto,");
                                    sql.append(" id_aliquotacredito,dataultimavenda,teclaassociada,id_situacaocadastro,");
                                    sql.append(" id_loja,descontinuado,quantidadeultimaentrada,centralizado,operacional,valoricmssubstituicao,");
                                    sql.append(" dataultimaentradaanterior,cestabasica,customediocomimpostoanterior,customediosemimpostoanterior,id_tipopiscofinscredito,valoroutrassubstituicao)");

                                    sql.append(" VALUES (");
                                    sql.append(IdProduto + ",");
                                    sql.append("'" + oComplemento.prateleira + "',");
                                    sql.append("'" + oComplemento.secao + "',");
                                    sql.append(oComplemento.estoqueMinimo + ",");
                                    sql.append(oComplemento.estoqueMaximo + ",");
                                    sql.append(oComplemento.valorIpi + ",");
                                    sql.append((oComplemento.dataUltimoPreco.isEmpty() ? null : "'" + Util.formatDataBanco(oComplemento.dataUltimoPreco) + "'") + ",");
                                    sql.append((oComplemento.dataUltimaEntrada.isEmpty() ? null : "'" + Util.formatDataBanco(oComplemento.dataUltimaEntrada) + "'") + ",");
                                    if (IdProduto == 72365) {
                                        Util.exibirMensagem(oComplemento.custoComImposto + ", " + oComplemento.custoSemImposto, "");
                                        Util.exibirMensagem(Utils.arredondar(oComplemento.custoComImposto, 3) + ", " + Utils.arredondar(oComplemento.custoSemImposto, 3), "");
                                    }
                                    sql.append(oComplemento.custoSemImposto + ",");
                                    sql.append(oComplemento.custoComImposto + ",");
                                    sql.append(oComplemento.custoSemImpostoAnterior + ",");
                                    sql.append(oComplemento.custoComImpostoAnterior + ",");
                                    sql.append(oComplemento.precoVenda + ",");
                                    sql.append(oComplemento.precoVendaAnterior + ",");
                                    sql.append(oComplemento.precoDiaSeguinte + ",");
                                    sql.append(oComplemento.estoque + ",");
                                    sql.append(oComplemento.troca + ",");
                                    sql.append(oComplemento.emiteEtiqueta + ",");
                                    sql.append(oComplemento.custoSemPerdaSemImposto + ",");
                                    sql.append(oComplemento.custoSemPerdaSemImpostoAnterior + ",");
                                    sql.append(oComplemento.custoMedioComImposto + ",");
                                    sql.append(oComplemento.custoMedioSemImposto + ",");
                                    sql.append(oComplemento.idAliquotaCredito + ",");
                                    sql.append((oComplemento.dataUltimaVenda.isEmpty() ? null : "'" + Util.formatDataBanco(oComplemento.dataUltimaVenda) + "'") + ",");
                                    sql.append(oComplemento.teclaAssociada + ",");
                                    sql.append(oComplemento.idSituacaoCadastro + ",");
                                    sql.append(oLoja.id + ",");
                                    sql.append(oComplemento.descontinuado + ",");
                                    sql.append(oComplemento.quantidadeUltimaEntrada + ",");
                                    sql.append(oComplemento.centralizado + ",");
                                    sql.append(oComplemento.operacional + ",");
                                    sql.append(oComplemento.valorIcmsSubstituicao + ",");
                                    sql.append((oComplemento.dataUltimaEntradaAnterior.isEmpty() ? null : "'" + Util.formatDataBanco(oComplemento.dataUltimaEntradaAnterior) + "'") + ",");
                                    sql.append(oComplemento.cestaBasica + ",");
                                    sql.append(oComplemento.custoMedioComImpostoAnterior + ",");
                                    sql.append(oComplemento.custoMedioSemImpostoAnterior + ",");
                                    sql.append(oComplemento.idTipoPisCofinsCredito + ",");
                                    sql.append(oComplemento.valorOutrasSubstituicao + ");");
                                    stm.execute(sql.toString());
                                }
                            }

                            for (ProdutoAutomacaoVO oAutomacao : i_produto.vAutomacao) {
                                if (oAutomacao.codigoBarras == -1) {
                                    if ((IdProduto <= 9999)
                                            && (i_produto.idTipoEmbalagem != 4)) {
                                        oAutomacao.codigoBarras = Utils.gerarEan13((int) IdProduto, false);
                                    } else if ((IdProduto > 9999)
                                            && (i_produto.idTipoEmbalagem != 4)
                                            && (!i_produto.pesavel)) {
                                        oAutomacao.codigoBarras = Utils.gerarEan13((int) IdProduto, true);
                                    } else if ((IdProduto > 9999)
                                            && (i_produto.idTipoEmbalagem != 4)
                                            && (i_produto.pesavel)) {
                                        oAutomacao.codigoBarras = Utils.gerarEan13((int) IdProduto, false);
                                    } else if ((i_produto.idTipoEmbalagem != 4)
                                            && (i_produto.pesavel)) {
                                        oAutomacao.codigoBarras = Utils.gerarEan13((int) IdProduto, false);
                                    } else if (i_produto.idTipoEmbalagem == 4) {
                                        oAutomacao.codigoBarras = Utils.gerarEan13((int) IdProduto, false);
                                    } else {
                                        oAutomacao.codigoBarras = Utils.gerarEan13((int) IdProduto, true);
                                    }
                                }

                                if (String.valueOf(oAutomacao.codigoBarras).length() > 14) {
                                    oAutomacao.codigoBarras = Long.parseLong(String.valueOf(oAutomacao.codigoBarras).substring(0, 14));
                                }

                                if (oAutomacao.codigoBarras > -2) {

                                    sql = new StringBuilder();
                                    sql.append("SELECT id_produto FROM produtoautomacao");
                                    sql.append(" WHERE cast(codigobarras as numeric(14,0)) = cast(" + oAutomacao.codigoBarras + " as numeric(14,0)) ");
                                    rst = stm.executeQuery(sql.toString());

                                    if (!rst.next()) {
                                        sql = new StringBuilder();
                                        sql.append("INSERT INTO produtoautomacao (id_produto, codigobarras, qtdembalagem, id_tipoembalagem)");
                                        sql.append("VALUES (");
                                        sql.append(IdProduto + ",");
                                        sql.append(oAutomacao.codigoBarras + ",");
                                        sql.append(oAutomacao.qtdEmbalagem + ",");
                                        sql.append(oAutomacao.idTipoEmbalagem);
                                        sql.append(")");

                                        stm.execute(sql.toString());
                                    }
                                }
                            }

                            for (ProdutoAliquotaVO oAliquota : i_produto.vAliquota) {
                                sql = new StringBuilder();

                                sql.append("INSERT INTO produtoaliquota (id_produto, id_estado, id_aliquotadebito, id_aliquotacredito, id_aliquotadebitoforaestado,");
                                sql.append(" id_aliquotacreditoforaestado, id_aliquotadebitoforaestadonf, id_aliquotaconsumidor)");
                                sql.append(" VALUES(");

                                sql.append(IdProduto + ",");
                                sql.append(oAliquota.idEstado + ",");
                                sql.append(oAliquota.idAliquotaDebito + ",");
                                sql.append(oAliquota.idAliquotaCredito + ",");
                                sql.append(oAliquota.idAliquotaDebitoForaEstado + ",");
                                sql.append(oAliquota.idAliquotaCreditoForaEstado + ",");
                                sql.append(oAliquota.idAliquotaDebitoForaEstadoNF + ",");
                                sql.append(oAliquota.getIdAliquotaConsumidor());

                                sql.append(")");

                                stm.execute(sql.toString());

                            }

                            for (CodigoAnteriorVO oCodigoInterno : i_produto.vCodigoAnterior) {
                                oCodigoInterno.codigoatual = IdProduto;

                                if (String.valueOf(oCodigoInterno.barras).length() > 14) {
                                    oCodigoInterno.barras = Long.parseLong(String.valueOf(oCodigoInterno.barras).substring(0, 14));
                                }

                                sql = new StringBuilder();
                                sql.append("INSERT INTO implantacao.codigoanterior( ");
                                sql.append("codigoanterior, codigoatual, barras, naturezareceita, ");
                                sql.append("piscofinscredito, piscofinsdebito, ref_icmsdebito, estoque, e_balanca, ");
                                sql.append("codigobalanca, custosemimposto, custocomimposto, margem, precovenda, referencia, ncm, id_loja, produto_novo, codigoauxiliar) ");
                                sql.append("VALUES ( ");
                                sql.append(oCodigoInterno.codigoanterior + ", ");
                                sql.append(IdProduto + ", ");
                                sql.append((oCodigoInterno.barras == -1 ? null : oCodigoInterno.barras) + ", ");
                                sql.append((oCodigoInterno.naturezareceita == -1 ? null : oCodigoInterno.naturezareceita) + ", ");
                                sql.append((oCodigoInterno.piscofinscredito == -1 ? null : oCodigoInterno.piscofinscredito) + ", ");
                                sql.append((oCodigoInterno.piscofinsdebito == -1 ? null : oCodigoInterno.piscofinsdebito) + ", ");
                                sql.append((oCodigoInterno.ref_icmsdebito.isEmpty() ? null : "'" + oCodigoInterno.ref_icmsdebito + "'") + ", ");
                                sql.append((oCodigoInterno.estoque == -1 ? null : oCodigoInterno.estoque) + ", ");
                                sql.append(oCodigoInterno.e_balanca + ", ");
                                sql.append((oCodigoInterno.codigobalanca == -1 ? null : oCodigoInterno.codigobalanca) + ", ");
                                sql.append((oCodigoInterno.custosemimposto == -1 ? null : oCodigoInterno.custosemimposto) + ", ");
                                sql.append((oCodigoInterno.custocomimposto == -1 ? null : oCodigoInterno.custocomimposto) + ", ");
                                sql.append((oCodigoInterno.margem == -1 ? null : oCodigoInterno.margem) + ", ");
                                sql.append((oCodigoInterno.precovenda == -1 ? null : oCodigoInterno.precovenda) + ", ");
                                sql.append((oCodigoInterno.referencia == -1 ? null : oCodigoInterno.referencia) + ", ");
                                sql.append((oCodigoInterno.ncm.isEmpty() ? null : "'" + oCodigoInterno.ncm + "'") + ", ");
                                sql.append(idLojaVR + ", true, ");
                                sql.append(Utils.quoteSQL(oCodigoInterno.getCodigoAuxiliar()));

                                sql.append(");");

                                stm.execute(sql.toString());
                            }
                        }
                    }
                }
                ProgressBar.next();
            }

            stm.close();
            stm2.close();
            stm3.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void importarCustoProdutoSemCodigoAnterior(List<ProdutoVO> v_result, int idLojaVR) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;

        try {
            Conexao.begin();
            stm = Conexao.createStatement();
            ProgressBar.setMaximum(v_result.size());
            ProgressBar.setStatus("Importando dados...Atualizar Custo Loja " + idLojaVR + "...");

            for (ProdutoVO i_result : v_result) {
                for (ProdutoComplementoVO oComplemento : i_result.vComplemento) {
                    sql = new StringBuilder();
                    sql.append("update produtocomplemento set "
                            + "custosemimposto = " + oComplemento.getCustoSemImposto() + ", "
                            + "custocomimposto = " + oComplemento.getCustoComImposto() + " "
                            + "where id_produto = " + i_result.getId() + " "
                            + "and id_loja = " + idLojaVR + ";");
                    stm.execute(sql.toString());
                }

                for (CodigoAnteriorVO oAnterior : i_result.vCodigoAnterior) {
                    sql = new StringBuilder();
                    sql.append("update implantacao.codigoanterior set "
                            + "custosemimposto = " + oAnterior.getCustosemimposto() + ", "
                            + "custocomimposto = " + oAnterior.getCustocomimposto() + " "
                            + "where codigoatual = " + i_result.getId() + " "
                            + "and id_loja = " + idLojaVR + ";");
                    stm.execute(sql.toString());
                }

                ProgressBar.next();
            }

            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void importarPrecoProdutoSemCodigoAnterior(List<ProdutoVO> v_result, int idLojaVR) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;

        try {
            Conexao.begin();
            stm = Conexao.createStatement();
            ProgressBar.setMaximum(v_result.size());
            ProgressBar.setStatus("Importando dados...Atualizar Preco Loja " + idLojaVR + "...");

            for (ProdutoVO i_result : v_result) {
                for (ProdutoComplementoVO oComplemento : i_result.vComplemento) {
                    sql = new StringBuilder();
                    sql.append("update produtocomplemento set "
                            + "precovenda = " + oComplemento.getPrecoVenda() + ", "
                            + "precodiaseguinte = " + oComplemento.getPrecoDiaSeguinte() + " "
                            + "where id_produto = " + i_result.getId() + " "
                            + "and id_loja = " + idLojaVR + ";");
                    stm.execute(sql.toString());
                }

                for (CodigoAnteriorVO oAnterior : i_result.vCodigoAnterior) {
                    sql = new StringBuilder();
                    sql.append("update implantacao.codigoanterior set "
                            + "precovenda = " + oAnterior.getPrecovenda() + " "
                            + "where codigoatual = " + i_result.getId() + " "
                            + "and id_loja = " + idLojaVR + ";");
                    stm.execute(sql.toString());
                }

                ProgressBar.next();
            }

            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void importarEstoqueProdutoSemCodigoAnterior(List<ProdutoVO> v_result, int idLojaVR) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;

        try {
            Conexao.begin();
            stm = Conexao.createStatement();
            ProgressBar.setMaximum(v_result.size());
            ProgressBar.setStatus("Importando dados...Atualizar Estoque Loja " + idLojaVR + "...");

            for (ProdutoVO i_result : v_result) {
                for (ProdutoComplementoVO oComplemento : i_result.vComplemento) {
                    sql = new StringBuilder();
                    sql.append("update produtocomplemento set "
                            + "estoque = " + oComplemento.getEstoque() + " "
                            + "where id_produto = " + i_result.getId() + " "
                            + "and id_loja = " + idLojaVR + ";");
                    stm.execute(sql.toString());
                }

                for (CodigoAnteriorVO oAnterior : i_result.vCodigoAnterior) {
                    sql = new StringBuilder();
                    sql.append("update implantacao.codigoanterior set "
                            + "estoque = " + oAnterior.getEstoque() + " "
                            + "where codigoatual = " + i_result.getId() + " "
                            + "and id_loja = " + idLojaVR + ";");
                    stm.execute(sql.toString());
                }

                ProgressBar.next();
            }

            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void alterarFamiliaProduto_Produto(List<ProdutoVO> v_produto) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;

        try {

            Conexao.begin();

            stm = Conexao.createStatement();

            ProgressBar.setMaximum(v_produto.size());

            ProgressBar.setStatus("Atualizando dados produto...Familia Produto...");

            for (ProdutoVO i_produto : v_produto) {

                sql = new StringBuilder();
                sql.append("SELECT p.id FROM produto p ");
                sql.append("INNER JOIN implantacao.codigoanterior ant ");
                sql.append("ON ant.codigoatual = p.id ");
                sql.append("WHERE ant.codigoanterior = " + (i_produto.idDouble > 0 ? i_produto.idDouble : i_produto.id) + ";");

                rst = stm.executeQuery(sql.toString());

                if (rst.next()) {

                    sql = new StringBuilder();
                    sql.append("update produto set ");
                    sql.append("id_familiaproduto = " + (i_produto.idFamiliaProduto == -1 ? null : i_produto.idFamiliaProduto) + " ");
                    sql.append("where id = " + rst.getInt("id") + ";");

                    stm.execute(sql.toString());

                }

                ProgressBar.next();
            }

            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void adicionarCodigoBarrasAtacado(List<ProdutoVO> v_produto, int qtdEmbalagem) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;
        try {

            Conexao.begin();
            stm = Conexao.createStatement();
            ProgressBar.setMaximum(v_produto.size());
            ProgressBar.setStatus("Atualizando dados produto...Adicionar Codigo Barra Atacado...");

            for (ProdutoVO i_produto : v_produto) {
                for (ProdutoAutomacaoVO oAutomacao : i_produto.vAutomacao) {
                    if (i_produto.getIdDouble() > 0) {
                        IdProduto = i_produto.getIdDouble();
                    } else {
                        IdProduto = i_produto.getId();
                    }

                    sql = new StringBuilder();
                    sql.append("select * from produtoautomacao ");
                    sql.append("where codigobarras = " + oAutomacao.getCodigoBarras());

                    rst = stm.executeQuery(sql.toString());

                    if (!rst.next()) {
                        sql = new StringBuilder();
                        sql.append("insert into produtoautomacao (");
                        sql.append("id_produto, codigobarras, qtdembalagem, id_tipoembalagem) ");
                        sql.append("values (");
                        sql.append(IdProduto + ",");
                        sql.append(oAutomacao.getCodigoBarras() + ",");
                        sql.append(qtdEmbalagem + ", ");
                        sql.append(oAutomacao.getIdTipoEmbalagem() + ");");
                        stm.execute(sql.toString());
                        /*sql = new StringBuilder();
                        sql.append("insert into produtoautomacaodesconto ( "
                                + "codigobarras, id_loja, desconto) "
                                + "values ("
                                + oAutomacao.getCodigoBarras() + ", "
                                + idLoja + ", "
                                + desconto + ");"
                                );
                        stm.execute(sql.toString());*/
                    }
                }
                ProgressBar.next();
            }
            
            //sql = new StringBuilder();
            //sql.append("update produtoautomacaodesconto set desconto = " + desconto + ";");
            //stm.execute(sql.toString());
            
            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }    
}
