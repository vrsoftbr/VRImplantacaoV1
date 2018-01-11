/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao.dao.cadastro;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JOptionPane;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrframework.classe.Util;
import vrframework.classe.VRException;
import vrimplantacao.classe.Global;
import vrimplantacao.dao.DataProcessamentoDAO;
import vrimplantacao.dao.LogTransacaoDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.Formulario;
import vrimplantacao.vo.TipoTransacao;
import vrimplantacao.vo.vrimplantacao.CodigoAnteriorFornecedorVO;
import vrimplantacao.vo.vrimplantacao.CodigoAnteriorVO;
import vrimplantacao.vo.vrimplantacao.ProdutoFornecedorVO;
import vrimplantacao.vo.vrimplantacao.ProdutosUnificacaoVO;

/**
 *
 * @author lucasrafael
 */
public class ProdutoFornecedorDAO {

    public long verificar(int i_idProduto, int i_idFornecedor, int i_idEstado) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;

        stm = Conexao.createStatement();

        sql = new StringBuilder();
        sql.append("SELECT id FROM produtofornecedor");
        sql.append(" WHERE id_produto = " + i_idProduto);
        sql.append(" AND id_fornecedor = " + i_idFornecedor);
        sql.append(" AND id_estado = " + i_idEstado);

        rst = stm.executeQuery(sql.toString());

        if (rst.next()) {
            return rst.getLong("id");
        } else {
            return 0;
        }
    }

    public ProdutoFornecedorVO carregar(long i_id, long i_idLoja) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;

        stm = Conexao.createStatement();

        sql = new StringBuilder();
        sql.append("SELECT produtofornecedor.*, produto.id_tipoembalagem, COALESCE(pauta.iva, 0) AS iva, pc.precovenda");
        sql.append(" FROM produtofornecedor");
        sql.append(" INNER JOIN produto ON produto.id = produtofornecedor.id_produto");
        sql.append(" LEFT JOIN pautafiscal AS pauta ON pauta.ncm1 = produto.ncm1 AND pauta.ncm2 = produto.ncm2 AND pauta.ncm3 = produto.ncm3");
        sql.append(" AND pauta.excecao = produto.excecao AND pauta.id_estado = " + Global.idEstado);
        sql.append(" INNER JOIN produtocomplemento AS pc ON pc.id_produto = produto.id AND pc.id_loja = " + i_idLoja);
        sql.append(" WHERE produtofornecedor.id = " + i_id);

        rst = stm.executeQuery(sql.toString());

        if (!rst.next()) {
            throw new VRException("Produto não encontrado!");
        }

        ProdutoFornecedorVO oProdutoFornecedor = new ProdutoFornecedorVO();
        oProdutoFornecedor.id = rst.getInt("id");
        oProdutoFornecedor.id_produto = rst.getInt("id_produto");
        oProdutoFornecedor.codigoexterno = rst.getString("codigoexterno");
        oProdutoFornecedor.id_fornecedor = rst.getInt("id_fornecedor");
        oProdutoFornecedor.id_estado = rst.getInt("id_estado");
        oProdutoFornecedor.qtdembalagem = rst.getInt("qtdembalagem");
        oProdutoFornecedor.id_divisaofornecedor = rst.getInt("id_divisaofornecedor");
        oProdutoFornecedor.custoinicial = rst.getDouble("custoinicial");
        oProdutoFornecedor.custotabela = rst.getDouble("custotabela");
        oProdutoFornecedor.tipodesconto = rst.getInt("tipodesconto");
        oProdutoFornecedor.desconto = rst.getDouble("desconto");
        oProdutoFornecedor.dataalteracao = rst.getDate("dataalteracao");
        oProdutoFornecedor.tipobonificacao = rst.getInt("tipobonificacao");
        oProdutoFornecedor.bonificacao = rst.getDouble("bonificacao");
        oProdutoFornecedor.tipoipi = rst.getInt("tipoipi");
        oProdutoFornecedor.ipi = rst.getDouble("ipi");
        oProdutoFornecedor.tipoverba = rst.getInt("tipoverba");
        oProdutoFornecedor.verba = rst.getDouble("verba");
        oProdutoFornecedor.idTipoEmbalagem = rst.getInt("id_tipoembalagem");
        oProdutoFornecedor.iva = rst.getDouble("iva");
        oProdutoFornecedor.pesoEmbalagem = rst.getDouble("pesoembalagem");
        oProdutoFornecedor.idTipoPisCofins = rst.getInt("id_tipopiscofins");
        oProdutoFornecedor.precoVenda = rst.getDouble("precovenda");

        stm.close();

        return oProdutoFornecedor;
    }
    
    public void salvar(ProdutoFornecedorVO i_produtoFornecedor) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;

        try {
            Conexao.begin();
            stm = Conexao.createStatement();

            sql = new StringBuilder();
            sql.append("SELECT id FROM produtofornecedor WHERE id = " + i_produtoFornecedor.id);

            rst = stm.executeQuery(sql.toString());

            i_produtoFornecedor.dataAlteracao = new DataProcessamentoDAO().get();

            if (rst.next()) {
                sql = new StringBuilder();
                sql.append("UPDATE produtofornecedor SET");
                sql.append(" codigoexterno = '" + i_produtoFornecedor.codigoexterno + "',");
                sql.append(" id_estado = " + i_produtoFornecedor.id_estado + ",");
                sql.append(" qtdembalagem = " + i_produtoFornecedor.qtdembalagem + ",");
                sql.append(" id_divisaofornecedor = " + i_produtoFornecedor.id_divisaofornecedor + ",");
                sql.append(" dataalteracao = '" + Util.formatDataBanco(i_produtoFornecedor.dataAlteracao) + "',");
                sql.append(" custoinicial = " + i_produtoFornecedor.custoinicial + ",");
                sql.append(" custotabela = " + i_produtoFornecedor.custotabela + ",");
                sql.append(" tipodesconto = " + i_produtoFornecedor.tipodesconto + ",");
                sql.append(" desconto = " + i_produtoFornecedor.desconto + ",");
                sql.append(" tipobonificacao = " + i_produtoFornecedor.tipobonificacao + ",");
                sql.append(" bonificacao = " + i_produtoFornecedor.bonificacao + ",");
                sql.append(" tipoipi = " + i_produtoFornecedor.tipoipi + ",");
                sql.append(" ipi = " + i_produtoFornecedor.ipi + ",");
                sql.append(" tipoverba = " + i_produtoFornecedor.tipoverba + ",");
                sql.append(" verba = " + i_produtoFornecedor.verba + ",");
                sql.append(" pesoembalagem = " + i_produtoFornecedor.pesoEmbalagem + ",");
                sql.append(" id_tipopiscofins = " + i_produtoFornecedor.idTipoPisCofins);
                sql.append(" WHERE id = " + i_produtoFornecedor.id);

                stm.execute(sql.toString());

                String observacao = "FORNECEDOR: " + i_produtoFornecedor.id_fornecedor + ", ESTADO: " + i_produtoFornecedor.id_estado;
                new LogTransacaoDAO().gerar(Formulario.CADASTRO_PRODUTO_FORNECEDOR, TipoTransacao.ALTERACAO, i_produtoFornecedor.id_produto, observacao, i_produtoFornecedor.id);

            } else {
                sql = new StringBuilder();
                sql.append("INSERT INTO produtofornecedor (id_produto, id_fornecedor, id_estado, custoinicial, custotabela, codigoexterno, qtdembalagem,");
                sql.append(" id_divisaofornecedor, dataalteracao, tipoipi, ipi, tipodesconto, desconto, tipobonificacao, bonificacao, tipoverba,");
                sql.append(" verba, pesoembalagem, id_tipopiscofins) VALUES (");
                sql.append(i_produtoFornecedor.id_produto + ", ");
                sql.append(i_produtoFornecedor.id_fornecedor + ", ");
                sql.append(i_produtoFornecedor.id_estado + ", ");
                sql.append(i_produtoFornecedor.custoinicial + ", ");
                sql.append(i_produtoFornecedor.custotabela + ", ");
                sql.append("'" + i_produtoFornecedor.codigoexterno + "', ");
                sql.append(i_produtoFornecedor.qtdembalagem + ", ");
                sql.append(i_produtoFornecedor.id_divisaofornecedor + ", ");
                sql.append("'" + Util.formatDataBanco(i_produtoFornecedor.dataAlteracao) + "',");
                sql.append(i_produtoFornecedor.tipoipi + ",");
                sql.append(i_produtoFornecedor.ipi + ", ");
                sql.append(i_produtoFornecedor.tipodesconto + ", ");
                sql.append(i_produtoFornecedor.desconto + ", ");
                sql.append(i_produtoFornecedor.tipobonificacao + ", ");
                sql.append(i_produtoFornecedor.bonificacao + ", ");
                sql.append(i_produtoFornecedor.tipoverba + ", ");
                sql.append(i_produtoFornecedor.verba + ", ");
                sql.append(i_produtoFornecedor.pesoEmbalagem + ", ");
                sql.append(i_produtoFornecedor.idTipoPisCofins + ")");

                stm.execute(sql.toString());

                rst = stm.executeQuery("SELECT CURRVAL('produtofornecedor_id_seq') AS id");
                rst.next();

                i_produtoFornecedor.id = rst.getInt("id");

                String observacao = "FORNECEDOR: " + i_produtoFornecedor.id_fornecedor + ", ESTADO: " + i_produtoFornecedor.id_estado;
                new LogTransacaoDAO().gerar(Formulario.CADASTRO_PRODUTO_FORNECEDOR, TipoTransacao.INCLUSAO, i_produtoFornecedor.id_produto, observacao, i_produtoFornecedor.id);
            }

            stm.close();
            Conexao.commit();

        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }
    
// INICIO METODOS PADRÕES 
    /*     CASO PRECISE ALTERAR ALGUM METODO POR UMA PARTICULARIDADE 
     DE UM CLIENTE, CRIAR O METODO IGUAL E COM O NOME DELE.       */
    @Deprecated
    public void salvar(List<ProdutoFornecedorVO> v_produtoForncedor) throws Exception {

        StringBuilder sql = null;
        Statement stm = null,
                stm2 = null,
                stm3 = null,
                stm4 = null;
        ResultSet rst2 = null,
                rst3 = null;
        double idProduto, idFornecedor;
        java.sql.Date dataalteracao;
        try {

            Conexao.begin();

            stm = Conexao.createStatement();
            stm2 = Conexao.createStatement();
            stm3 = Conexao.createStatement();
            stm4 = Conexao.createStatement();

            ProgressBar.setMaximum(v_produtoForncedor.size());
            ProgressBar.setStatus("Importando Produto Fornecedor...");

            CodigoAnteriorFornecedorDAO fornAnteriores = new CodigoAnteriorFornecedorDAO();

            for (ProdutoFornecedorVO i_produtoFornecedor : v_produtoForncedor) {
                if (i_produtoFornecedor.id_produtoDouble > 0) {
                    idProduto = i_produtoFornecedor.id_produtoDouble;
                } else {
                    idProduto = i_produtoFornecedor.id_produto;
                }
                if (i_produtoFornecedor.id_fornecedorDouble > 0) {
                    idFornecedor = i_produtoFornecedor.id_fornecedorDouble;
                } else {
                    idFornecedor = i_produtoFornecedor.id_fornecedor;
                }
                /*sql = new StringBuilder();
                 sql.append("SELECT f.id, id_estado FROM fornecedor f ");
                 sql.append("INNER JOIN implantacao.codigoanteriorforn ant ");
                 sql.append("ON ant.codigoatual = f.id ");
                 sql.append("WHERE cast(ant.codigoanterior as numeric(14,0)) = cast(" + idFornecedor+" as numeric(14,0));");
                
                 rst = stm.executeQuery(sql.toString());
                
                 if (rst.next()) {*/

                CodigoAnteriorFornecedorVO fornAnterior = fornAnteriores.get((long) idFornecedor, 1);

                if (fornAnterior != null) {

                    sql = new StringBuilder();
                    sql.append("SELECT p.id, pc.custocomimposto, p.qtdembalagem FROM produto p ");
                    sql.append("INNER JOIN produtocomplemento pc ON pc.id_produto = p.id ");
                    sql.append("INNER JOIN implantacao.codigoanterior ant ON ant.codigoatual = p.id ");
                    sql.append("WHERE cast(ant.codigoanterior as numeric(14,0)) = cast(" + idProduto + " as numeric(14,0)) ");
                    sql.append("AND pc.id_loja = " + Global.idLojaFornecedor);
                    rst2 = stm2.executeQuery(sql.toString());

                    if (rst2.next()) {

                        sql = new StringBuilder();
                        sql.append("SELECT id from produtofornecedor ");
                        sql.append("WHERE id_fornecedor = " + fornAnterior.getCodigoAtual() + " ");
                        sql.append("AND id_produto = " + rst2.getInt("id") + " ");
                        sql.append("AND id_estado = " + fornAnterior.getId_uf() + ";");

                        rst3 = stm3.executeQuery(sql.toString());

                        if (!rst3.next()) {

                            sql = new StringBuilder();
                            sql.append("INSERT INTO produtofornecedor( ");
                            sql.append("id_produto, id_fornecedor, id_estado, custotabela, codigoexterno, ");
                            sql.append("qtdembalagem, id_divisaofornecedor, dataalteracao, desconto, ");
                            sql.append("tipoipi, ipi, tipobonificacao, bonificacao, tipoverba, verba, ");
                            sql.append("custoinicial, tipodesconto, pesoembalagem, id_tipopiscofins, ");
                            sql.append("csosn, fatorembalagem) ");
                            sql.append("VALUES ( ");
                            sql.append(rst2.getInt("id") + ", ");
                            sql.append(fornAnterior.getCodigoAtual() + ", ");
                            sql.append(fornAnterior.getId_uf() + ", ");
                            sql.append(rst2.getDouble("custocomimposto") + ", ");
                            sql.append("'" + i_produtoFornecedor.codigoexterno + "', ");
                            sql.append((i_produtoFornecedor.qtdembalagem > 0 ? i_produtoFornecedor.qtdembalagem : rst2.getInt("qtdembalagem")) + ", ");
                            sql.append("0, ");

                            if (i_produtoFornecedor.dataalteracao == null) {
                                dataalteracao = new java.sql.Date(new java.util.Date().getTime());
                                sql.append("'" + dataalteracao + "',");

                            } else {
                                sql.append("'" + i_produtoFornecedor.dataalteracao + "', ");
                            }

                            sql.append("0, 0, 0, 0, 0, 0, 0,");
                            sql.append(rst2.getDouble("custocomimposto") + ", ");
                            sql.append("0, 0, 0, NULL, 1);");

                            stm4.execute(sql.toString());
                        } else {

                            if (!i_produtoFornecedor.codigoexterno.trim().isEmpty()) {

                                sql = new StringBuilder();
                                sql.append("INSERT INTO produtofornecedorcodigoexterno( ");
                                sql.append("id_produtofornecedor, codigoexterno) ");
                                sql.append("VALUES ( ");
                                sql.append(rst3.getInt("id") + ", ");
                                sql.append("'" + i_produtoFornecedor.codigoexterno + "');");

                                stm4.execute(sql.toString());
                            }
                        }
                    }
                }

                ProgressBar.next();
            }

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
// FIM METODOS PADRÕES     

// INICIO METODOS ESPECIFICAS PARA CLIENTES 
    /**
     * Método para gravar uma listagem de produtosfornecedor. Caso haja mais de
     * um códigoexterno para um mesmo produtofornecedor o método inclui na
     * tabela de produtofornecedorcodigoexterno, caso contrário inclui na
     * produtofornecedor
     *
     * @param v_produtoForncedor
     * @throws Exception
     */
    public void salvar2(List<ProdutoFornecedorVO> v_produtoForncedor) throws Exception {

        String erro = "";

        //<editor-fold desc="PREPARO DAS LISTAGENS AUXILIARES">
        ProgressBar.setStatus("Importando Produto Fornecedor...Preparando listagens");
        /**
         * Carrega os códigos anteriors de fornecedor e de produto
         */
        CodigoAnteriorFornecedorDAO fornAnteriores = new CodigoAnteriorFornecedorDAO();
        Map<String, CodigoAnteriorVO> prodAnteriores = new CodigoAnteriorDAO().carregarCodigoAnteriorV2();

        /**
         * LISTAGEMS PARA VERIFICAR A EXISTENCIA DE UM CÓDIGO
         */
        Map<String, Integer> prodFornExistente = new LinkedHashMap<>();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "       pf.id,\n"
                    + "	pf.id_fornecedor,\n"
                    + "	pf.id_produto,\n"
                    + "	f.id_estado\n"
                    + "from\n"
                    + "	produtofornecedor pf\n"
                    + "	join fornecedor f on pf.id_fornecedor = f.id\n"
                    + "order by\n"
                    + "       pf.id,\n"
                    + "	pf.id_fornecedor,\n"
                    + "	pf.id_produto,\n"
                    + "	f.id_estado"
            )) {
                while (rst.next()) {
                    prodFornExistente.put(rst.getInt("id_fornecedor") + "-" + rst.getInt("id_produto") + "-" + rst.getInt("id_estado"), rst.getInt("id"));
                }
            }
        }
        Set<String> codExtExistente = new LinkedHashSet<>();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	pf.id_fornecedor,\n"
                    + "	pf.id_produto,\n"
                    + "	f.id_estado,\n"
                    + "	ext.codigoexterno\n"
                    + "from \n"
                    + "	produtofornecedorcodigoexterno ext\n"
                    + "	join produtofornecedor pf on ext.id_produtofornecedor = pf.id\n"
                    + "	join fornecedor f on pf.id_fornecedor = f.id\n"
                    + "order by\n"
                    + "	pf.id_fornecedor,\n"
                    + "	pf.id_produto,\n"
                    + "	f.id_estado,\n"
                    + "	ext.codigoexterno"
            )) {
                while (rst.next()) {
                    codExtExistente.add(rst.getInt("id_fornecedor") + "-" + rst.getInt("id_produto") + "-" + rst.getInt("id_estado") + "-" + rst.getString("codigoexterno"));
                }
            }
        }
        /**
         * Obtem o custo e a quantidade da embalagem dos.
         */
        class ProdutoCustoEmbalagem {

            public int id = 0;
            public double custoComImposto = 0;
            public double custoSemImposto = 0;
            public int qtdEmbalagem = 0;
        }

        Map<Integer, ProdutoCustoEmbalagem> custos = new LinkedHashMap<>();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + "	p.id, \n"
                    + "	pc.custosemimposto, \n"
                    + " pc.custocomimposto, "
                    + "	p.qtdembalagem \n"
                    + "FROM \n"
                    + "	produto p\n"
                    + "	join produtocomplemento pc on pc.id_produto = p.id\n"
                    + "WHERE pc.id_loja = 1"
            )) {
                while (rst.next()) {
                    ProdutoCustoEmbalagem pce = new ProdutoCustoEmbalagem();
                    pce.id = rst.getInt("id");
                    pce.custoSemImposto = rst.getDouble("custosemimposto");
                    pce.custoComImposto = rst.getDouble("custocomimposto");
                    pce.qtdEmbalagem = rst.getInt("qtdembalagem");
                    custos.put(pce.id, pce);
                }
            }
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="GRAVANDO OS PRODUTOSFORNECEDORES">       
        Conexao.begin();
        try {
            ProgressBar.setMaximum(v_produtoForncedor.size());
            ProgressBar.setStatus("Importando Produto Fornecedor...Gravando Produto Fornecedor");
            /*
             Efetua a inclusão do produto fornecedor primeiro.
             */
            for (ProdutoFornecedorVO vo : v_produtoForncedor) {

                CodigoAnteriorFornecedorVO fornAnt = fornAnteriores.get((long) (vo.getId_fornecedor() == 0 ? vo.getId_fornecedorDouble() : vo.getId_fornecedor()), 1); //TODO: Corrigir eliminar a verificação por loja
                String codigoAnterior = null;
                if (vo.getId_produtoStr() != null && !"".equals(vo.getId_produtoStr().trim())) {
                    codigoAnterior = vo.getId_produtoStr();
                } 
                if (codigoAnterior == null) {
                    if (vo.getId_produtoDouble() > 0) {
                        codigoAnterior =  String.valueOf((long) vo.getId_produtoDouble());
                    } else {
                        codigoAnterior = String.valueOf(vo.getId_produto());
                    }
                }
                CodigoAnteriorVO prodAnt = prodAnteriores.get(codigoAnterior);

                if (fornAnt != null && prodAnt != null) {
                    erro = fornAnt.getChaveUnica() + "|" + vo.getId_produto();

                    int idFornecedorAtual = fornAnt.getCodigoAtual();
                    int idEstadoFornecedor = fornAnt.getId_uf();
                    int idProdutoAtual = (int) prodAnt.getCodigoatual();

                    //Prepara os custos
                    ProdutoCustoEmbalagem pce = custos.get(idProdutoAtual);
                    vo.setCustotabela(pce.custoComImposto);
                    //vo.setQtdembalagem(pce.qtdEmbalagem);

                    if (!prodFornExistente.containsKey(idFornecedorAtual + "-" + idProdutoAtual + "-" + idEstadoFornecedor)) {
                        
                        if ("".equals(vo.getCodigoexterno())) {
                            vo.setCodigoexterno(String.valueOf(idFornecedorAtual) + String.valueOf(idProdutoAtual));
                        }
                        
                        try (Statement stm = Conexao.createStatement()) {
                            try (ResultSet rst = stm.executeQuery(
                                    
                                    
                                    "INSERT INTO produtofornecedor( "
                                    + "id_produto, id_fornecedor, id_estado, custotabela, codigoexterno, "
                                    + "qtdembalagem, id_divisaofornecedor, dataalteracao, desconto, "
                                    + "tipoipi, ipi, tipobonificacao, bonificacao, tipoverba, verba, "
                                    + "custoinicial, tipodesconto, pesoembalagem, id_tipopiscofins, "
                                    + "csosn, fatorembalagem) "
                                    + "VALUES ( "
                                    + idProdutoAtual + ", "
                                    + idFornecedorAtual + ", "
                                    + Global.idEstado + ", "
                                    + vo.getCustotabela() + ", "
                                    + "'" + vo.getCodigoexterno() + "', "
                                    + vo.getQtdembalagem() + ", "
                                    + "0, '" + vo.getDataalteracao() + "', "
                                    + "0, 0, 0, 0, 0, 0, 0,"
                                    + vo.getCustotabela() + ", "
                                    + "0, 0, 0, NULL, 1) returning id;"
                            )) {
                                //Obtem o id incluso
                                if (rst.next()) {
                                    /*
                                     Inclui na listagem dos ids cadastrados.
                                     */
                                    vo.setId(rst.getInt("id"));
                                }
                            }
                        }
                        prodFornExistente.put(idFornecedorAtual + "-" + idProdutoAtual + "-" + idEstadoFornecedor, vo.getId());                        
                    } else {
                        if (!codExtExistente.contains(idFornecedorAtual + "-" + idProdutoAtual + "-" + idEstadoFornecedor + "-" + vo.getCodigoexterno())) {
                            try (Statement stm = Conexao.createStatement()) {
                                String sql = "do $$\n" +
                                        "declare existente integer;\n" +
                                        "begin\n" +
                                        "	select \n" +
                                        "		id \n" +
                                        "	into\n" +
                                        "		existente\n" +
                                        "	from \n" +
                                        "		produtofornecedor pf\n" +
                                        "	where\n" +
                                        "		pf.id_produto = " + idProdutoAtual + " and\n" +
                                        "		pf.id_fornecedor = " + idFornecedorAtual + " and\n" +
                                        "		pf.id_estado = " + Global.idEstado + " and\n" +
                                        "		pf.codigoexterno = " + Utils.quoteSQL(vo.getCodigoexterno()) + ";\n" +
                                        "\n" +
                                        "	if (existente is null) then\n" +
                                        "           INSERT INTO produtofornecedorcodigoexterno(\n"
                                        + "             id_produtofornecedor, codigoexterno)\n"
                                        + "             VALUES (\n"
                                        + "             " + prodFornExistente.get(idFornecedorAtual + "-" + idProdutoAtual + "-" + idEstadoFornecedor) + ",\n"
                                        + "             '" + vo.getCodigoexterno() + "');\n" +
                                        "       end if;\n" +
                                        "end\n" +
                                        "$$";
                                stm.executeUpdate(sql);
                            }
                            codExtExistente.add(idFornecedorAtual + "-" + idProdutoAtual + "-" + idEstadoFornecedor + "-" + vo.getCodigoexterno());
                        }
                    }
                }

                ProgressBar.next();
            }
            Conexao.commit();
        } catch (Exception e) {
            Conexao.rollback();
            e.printStackTrace();
            throw new Exception(erro + " - " + e.getMessage(), e);
        }
        //</editor-fold>        
    }

    public void salvarUnificacao(List<ProdutoFornecedorVO> v_produtoForncedor) throws Exception {

        String erro = "";

        //<editor-fold desc="PREPARO DAS LISTAGENS AUXILIARES">
        ProgressBar.setStatus("Importando Produto Fornecedor...Unificação...");
        /**
         * LISTAGEMS PARA VERIFICAR A EXISTENCIA DE UM CÓDIGO
         */
        Map<String, Integer> prodFornExistente = new LinkedHashMap<>();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "       pf.id,\n"
                    + "	pf.id_fornecedor,\n"
                    + "	pf.id_produto,\n"
                    + "	f.id_estado\n"
                    + "from\n"
                    + "	produtofornecedor pf\n"
                    + "	join fornecedor f on pf.id_fornecedor = f.id\n"
                    + "order by\n"
                    + "       pf.id,\n"
                    + "	pf.id_fornecedor,\n"
                    + "	pf.id_produto,\n"
                    + "	f.id_estado"
            )) {
                while (rst.next()) {
                    prodFornExistente.put(rst.getInt("id_fornecedor") + "-" + rst.getInt("id_produto") + "-" + Global.idEstado, rst.getInt("id"));
                }
            }
        }
        Set<String> codExtExistente = new LinkedHashSet<>();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	pf.id_fornecedor,\n"
                    + "	pf.id_produto,\n"
                    + "	f.id_estado,\n"
                    + "	ext.codigoexterno\n"
                    + "from \n"
                    + "	produtofornecedorcodigoexterno ext\n"
                    + "	join produtofornecedor pf on ext.id_produtofornecedor = pf.id\n"
                    + "	join fornecedor f on pf.id_fornecedor = f.id\n"
                    + "order by\n"
                    + "	pf.id_fornecedor,\n"
                    + "	pf.id_produto,\n"
                    + "	f.id_estado,\n"
                    + "	ext.codigoexterno"
            )) {
                while (rst.next()) {
                    codExtExistente.add(rst.getInt("id_fornecedor") + "-" + rst.getInt("id_produto") + "-" + Global.idEstado + "-" + rst.getString("codigoexterno"));
                }
            }
        }
        /**
         * Obtem o custo e a quantidade da embalagem dos.
         */
        class ProdutoCustoEmbalagem {

            public int id = 0;
            public double custoSemImposto = 0;
            public int qtdEmbalagem = 0;
        }

        Map<Integer, ProdutoCustoEmbalagem> custos = new LinkedHashMap<>();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + "	p.id, \n"
                    + "	pc.custosemimposto, \n"
                    + "	p.qtdembalagem \n"
                    + "FROM \n"
                    + "	produto p\n"
                    + "	join produtocomplemento pc on pc.id_produto = p.id\n"
                    + "WHERE pc.id_loja = 1"
            )) {
                while (rst.next()) {
                    ProdutoCustoEmbalagem pce = new ProdutoCustoEmbalagem();
                    pce.id = rst.getInt("id");
                    pce.custoSemImposto = rst.getDouble("custosemimposto");
                    pce.qtdEmbalagem = rst.getInt("qtdembalagem");
                    custos.put(pce.id, pce);
                }
            }
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="GRAVANDO OS PRODUTOSFORNECEDORES">       
        Conexao.begin();
        try {
            ProgressBar.setMaximum(v_produtoForncedor.size());
            ProgressBar.setStatus("Importando Produto Fornecedor...Gravando Produto Fornecedor");
            /*
             Efetua a inclusão do produto fornecedor primeiro.
             */
            for (ProdutoFornecedorVO vo : v_produtoForncedor) {


                    //Prepara os custos
                    ProdutoCustoEmbalagem pce = custos.get(vo.id_produto);
                    vo.setCustotabela(pce.custoSemImposto);
                    //vo.setQtdembalagem(pce.qtdEmbalagem);

                    if (!prodFornExistente.containsKey(vo.id_fornecedor + "-" + vo.id_produto + "-" + Global.idEstado)) {
                        
                        if ("".equals(vo.getCodigoexterno())) {
                            vo.setCodigoexterno(String.valueOf(vo.id_fornecedor) + String.valueOf(vo.id_produto));
                        }
                        
                        try (Statement stm = Conexao.createStatement()) {
                            try (ResultSet rst = stm.executeQuery(
                                    
                                    
                                    "INSERT INTO produtofornecedor( "
                                    + "id_produto, id_fornecedor, id_estado, custotabela, codigoexterno, "
                                    + "qtdembalagem, id_divisaofornecedor, dataalteracao, desconto, "
                                    + "tipoipi, ipi, tipobonificacao, bonificacao, tipoverba, verba, "
                                    + "custoinicial, tipodesconto, pesoembalagem, id_tipopiscofins, "
                                    + "csosn, fatorembalagem) "
                                    + "VALUES ( "
                                    + vo.id_produto + ", "
                                    + vo.id_fornecedor + ", "
                                    + Global.idEstado + ", "
                                    + vo.getCustotabela() + ", "
                                    + "'" + vo.getCodigoexterno() + "', "
                                    + vo.getQtdembalagem() + ", "
                                    + "0, '" + vo.getDataalteracao() + "', "
                                    + "0, 0, 0, 0, 0, 0, 0,"
                                    + vo.getCustotabela() + ", "
                                    + "0, 0, 0, NULL, 1) returning id;"
                            )) {
                                //Obtem o id incluso
                                if (rst.next()) {
                                    /*
                                     Inclui na listagem dos ids cadastrados.
                                     */
                                    vo.setId(rst.getInt("id"));
                                }
                            }
                        }
                        prodFornExistente.put(vo.id_fornecedor + "-" + vo.id_produto + "-" + Global.idEstado, vo.getId());                        
                    } else {
                        if (!codExtExistente.contains(vo.id_fornecedor + "-" + vo.id_produto + "-" + Global.idEstado + "-" + vo.getCodigoexterno())) {
                            try (Statement stm = Conexao.createStatement()) {
                                String sql = "do $$\n" +
                                        "declare existente integer;\n" +
                                        "begin\n" +
                                        "	select \n" +
                                        "		id \n" +
                                        "	into\n" +
                                        "		existente\n" +
                                        "	from \n" +
                                        "		produtofornecedor pf\n" +
                                        "	where\n" +
                                        "		pf.id_produto = " + vo.id_produto + " and\n" +
                                        "		pf.id_fornecedor = " + vo.id_fornecedor + " and\n" +
                                        "		pf.id_estado = " + Global.idEstado + " and\n" +
                                        "		pf.codigoexterno = " + Utils.quoteSQL(vo.getCodigoexterno()) + ";\n" +
                                        "\n" +
                                        "	if (existente is null) then\n" +
                                        "           INSERT INTO produtofornecedorcodigoexterno(\n"
                                        + "             id_produtofornecedor, codigoexterno)\n"
                                        + "             VALUES (\n"
                                        + "             " + prodFornExistente.get(vo.id_fornecedor + "-" + vo.id_produto + "-" + Global.idEstado) + ",\n"
                                        + "             '" + vo.getCodigoexterno() + "');\n" +
                                        "       end if;\n" +
                                        "end\n" +
                                        "$$";
                                stm.executeUpdate(sql);
                            }
                            codExtExistente.add(vo.id_fornecedor + "-" + vo.id_produto + "-" + Global.idEstado + "-" + vo.getCodigoexterno());
                        }
                    }
                ProgressBar.next();
            }
            Conexao.commit();
        } catch (Exception e) {
            Conexao.rollback();
            e.printStackTrace();
            throw new Exception(erro + " - " + e.getMessage(), e);
        }
        //</editor-fold>        
    }
    
    public void salvarMilenio(List<ProdutoFornecedorVO> v_produtoForncedor) throws Exception {

        StringBuilder sql = null;
        Statement stm = null,
                stm2 = null,
                stm3 = null,
                stm4 = null;
        ResultSet rst = null,
                rst2 = null,
                rst3 = null;

        try {

            Conexao.begin();

            stm = Conexao.createStatement();
            stm2 = Conexao.createStatement();
            stm3 = Conexao.createStatement();
            stm4 = Conexao.createStatement();

            ProgressBar.setMaximum(v_produtoForncedor.size());
            ProgressBar.setStatus("Importando Produto Fornecedor...");

            for (ProdutoFornecedorVO i_produtoFornecedor : v_produtoForncedor) {

                sql = new StringBuilder();
                sql.append("SELECT f.id, id_estado FROM fornecedor f ");
                sql.append("where f.cnpj = " + i_produtoFornecedor.cnpFornecedor + " ");
                //sql.append("INNER JOIN implantacao.codigoanteriorforn ant ");
                //sql.append("ON ant.codigoatual = f.id ");
                //sql.append("WHERE ant.codigoanterior = " + i_produtoFornecedor.id_fornecedor+";");

                rst = stm.executeQuery(sql.toString());

                if (rst.next()) {

                    sql = new StringBuilder();
                    sql.append("SELECT p.id, pc.custosemimposto FROM produto p ");
                    sql.append("INNER JOIN produtocomplemento pc ON pc.id_produto = p.id ");
                    sql.append("INNER JOIN implantacao.codigoanterior ant ON ant.codigoatual = p.id ");
                    sql.append("WHERE ant.referencia = " + i_produtoFornecedor.id_produto + " ");
                    sql.append("AND ant.produto_novo = true ");

                    rst2 = stm2.executeQuery(sql.toString());

                    if (rst2.next()) {

                        sql = new StringBuilder();
                        sql.append("SELECT id from produtofornecedor ");
                        sql.append("WHERE id_fornecedor = " + rst.getInt("id") + " ");
                        sql.append("AND id_produto = " + rst2.getInt("id") + " ");
                        sql.append("AND id_estado = " + Global.idEstado + ";");

                        rst3 = stm3.executeQuery(sql.toString());

                        if (!rst3.next()) {

                            sql = new StringBuilder();
                            sql.append("INSERT INTO produtofornecedor( ");
                            sql.append("id_produto, id_fornecedor, id_estado, custotabela, codigoexterno, ");
                            sql.append("qtdembalagem, id_divisaofornecedor, dataalteracao, desconto, ");
                            sql.append("tipoipi, ipi, tipobonificacao, bonificacao, tipoverba, verba, ");
                            sql.append("custoinicial, tipodesconto, pesoembalagem, id_tipopiscofins, ");
                            sql.append("csosn, fatorembalagem) ");
                            sql.append("VALUES ( ");
                            sql.append(rst2.getInt("id") + ", ");
                            sql.append(rst.getInt("id") + ", ");
                            sql.append(Global.idEstado + ", ");
                            sql.append(rst2.getDouble("custosemimposto") + ", ");
                            sql.append("'" + i_produtoFornecedor.codigoexterno + "', ");
                            sql.append(i_produtoFornecedor.qtdembalagem + ", ");
                            sql.append("0, '" + i_produtoFornecedor.dataalteracao + "', ");
                            sql.append("0, 0, 0, 0, 0, 0, 0,");
                            sql.append(rst2.getDouble("custosemimposto") + ", ");
                            sql.append("0, 0, 0, NULL, 1);");

                            stm4.execute(sql.toString());
                        } else {

                            if (!i_produtoFornecedor.codigoexterno.isEmpty()) {

                                sql = new StringBuilder();
                                sql.append("INSERT INTO produtofornecedorcodigoexterno( ");
                                sql.append("id_produtofornecedor, codigoexterno) ");
                                sql.append("VALUES ( ");
                                sql.append(rst3.getInt("id") + ", ");
                                sql.append("'" + i_produtoFornecedor.codigoexterno + "');");

                                stm4.execute(sql.toString());
                            }
                        }
                    }
                }

                ProgressBar.next();
            }

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

    public void salvarJMaster(List<ProdutoFornecedorVO> v_produtoForncedor) throws Exception {

        StringBuilder sql = null;
        Statement stm = null,
                stm2 = null,
                stm3 = null,
                stm4 = null;
        ResultSet rst = null,
                rst2 = null,
                rst3 = null;

        try {

            Conexao.begin();

            stm = Conexao.createStatement();
            stm2 = Conexao.createStatement();
            stm3 = Conexao.createStatement();
            stm4 = Conexao.createStatement();

            ProgressBar.setMaximum(v_produtoForncedor.size());
            ProgressBar.setStatus("Importando Produto Fornecedor...");

            for (ProdutoFornecedorVO i_produtoFornecedor : v_produtoForncedor) {

                sql = new StringBuilder();
                sql.append("SELECT f.id, id_estado FROM fornecedor f ");
                sql.append("INNER JOIN implantacao.codigoanteriorforn ant ");
                sql.append("ON ant.codigoatual = f.id ");
                sql.append("WHERE ant.codigoanterior = " + i_produtoFornecedor.id_fornecedor + ";");

                rst = stm.executeQuery(sql.toString());

                if (rst.next()) {

                    sql = new StringBuilder();
                    sql.append("SELECT p.id, pc.custosemimposto FROM produto p ");
                    sql.append("INNER JOIN produtocomplemento pc ON pc.id_produto = p.id ");
                    sql.append("INNER JOIN implantacao.codigoanterior ant ON ant.codigoatual = p.id ");
                    sql.append("WHERE ant.codigoanterior = " + i_produtoFornecedor.id_produto + " ");
                    sql.append("AND ant.produto_novo = true ");

                    rst2 = stm2.executeQuery(sql.toString());

                    if (rst2.next()) {

                        sql = new StringBuilder();
                        sql.append("SELECT id from produtofornecedor ");
                        sql.append("WHERE id_fornecedor = " + rst.getInt("id") + " ");
                        sql.append("AND id_produto = " + rst2.getInt("id") + " ");
                        sql.append("AND id_estado = " + rst.getInt("id_estado") + ";");

                        rst3 = stm3.executeQuery(sql.toString());

                        if (!rst3.next()) {

                            sql = new StringBuilder();
                            sql.append("INSERT INTO produtofornecedor( ");
                            sql.append("id_produto, id_fornecedor, id_estado, custotabela, codigoexterno, ");
                            sql.append("qtdembalagem, id_divisaofornecedor, dataalteracao, desconto, ");
                            sql.append("tipoipi, ipi, tipobonificacao, bonificacao, tipoverba, verba, ");
                            sql.append("custoinicial, tipodesconto, pesoembalagem, id_tipopiscofins, ");
                            sql.append("csosn, fatorembalagem) ");
                            sql.append("VALUES ( ");
                            sql.append(rst2.getInt("id") + ", ");
                            sql.append(rst.getInt("id") + ", ");
                            sql.append(rst.getInt("id_estado") + ", ");
                            sql.append(rst2.getDouble("custosemimposto") + ", ");
                            sql.append("'" + i_produtoFornecedor.codigoexterno + "', ");
                            sql.append(i_produtoFornecedor.qtdembalagem + ", ");
                            sql.append("0, '" + i_produtoFornecedor.dataalteracao + "', ");
                            sql.append("0, 0, 0, 0, 0, 0, 0,");
                            sql.append(rst2.getDouble("custosemimposto") + ", ");
                            sql.append("0, 0, 0, NULL, 1);");

                            stm4.execute(sql.toString());
                        } else {

                            if (!i_produtoFornecedor.codigoexterno.isEmpty()) {

                                sql = new StringBuilder();
                                sql.append("INSERT INTO produtofornecedorcodigoexterno( ");
                                sql.append("id_produtofornecedor, codigoexterno) ");
                                sql.append("VALUES ( ");
                                sql.append(rst3.getInt("id") + ", ");
                                sql.append("'" + i_produtoFornecedor.codigoexterno + "');");

                                stm4.execute(sql.toString());
                            }
                        }
                    }
                }

                ProgressBar.next();
            }

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
    
    public void salvarComCnpj(List<ProdutoFornecedorVO> v_produtoForncedor, int idLojaVR) throws Exception {
        Conexao.begin();
        try {
            ProgressBar.setMaximum(v_produtoForncedor.size());
            ProgressBar.setStatus("Importando Produto Fornecedor...");

            //<editor-fold defaultstate="collapsed" desc="Obter os fornecedores cadastrados, codigoatual, codigoanterior e loja.">
            CodigoAnteriorFornecedorDAO fornAnteriores = new CodigoAnteriorFornecedorDAO();
            //</editor-fold>
               
            //Obter os produtos e o código anterior cadastrado através da unificação.                
            Map<Double, ProdutosUnificacaoVO> prodAnteriores = new ProdutoUnificacaoDAO().carregarUnificados();

            //<editor-fold defaultstate="collapsed" desc="Trazer uma listagem de todos os códigos de fornecedor cadastrados, div por forn, prod e codigo">
            class CodigoExterior {
                int id_produtofornecedor;
                String codigoexterno;
                double id_produto;
                double id_fornecedor;
                int id_estado;
                boolean produtofornecedor;

                @Override
                public String toString() {
                    return (int)id_fornecedor + "-" + (int)id_produto + "-" + codigoexterno;
                }
                
            }
            Map<String, CodigoExterior> porCodigoExterior = new LinkedHashMap<>();
            Map<Double, CodigoExterior> porIdProduto = new LinkedHashMap<>();
            try (Statement stm = Conexao.createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select id id_produtofornecedor, codigoexterno, id_produto, id_fornecedor, id_estado, true as produtoforn from produtofornecedor\n" +
                        "union\n" +
                        "select cex.id_produtofornecedor, cex.codigoexterno, pf.id_produto, id_fornecedor, pf.id_estado, false as produtoforn from produtofornecedorcodigoexterno cex\n" +
                        "join produtofornecedor pf on cex.id_produtofornecedor = pf.id\n" +
                        "order by id_produtofornecedor"
                )) {
                    while (rst.next()) {
                        CodigoExterior ext = new CodigoExterior();
                        ext.id_produtofornecedor = rst.getInt("id_produtofornecedor");
                        ext.codigoexterno = rst.getString("codigoexterno");
                        ext.id_produto = rst.getInt("id_produto");
                        ext.id_estado = rst.getInt("id_estado");
                        ext.id_fornecedor = rst.getInt("id_fornecedor");
                        ext.produtofornecedor = rst.getBoolean("produtoforn");
                        porCodigoExterior.put(ext.toString(), ext);
                        if (ext.produtofornecedor) {
                            porIdProduto.put(ext.id_produto, ext);
                        }
                    }
                }
            }
            //</editor-fold>
            class ProdutoCustoEmbalagem {

                public int id = 0;
                public double custoSemImposto = 0;
                public int qtdEmbalagem = 0;
            }

            Map<Integer, ProdutoCustoEmbalagem> custos = new LinkedHashMap<>();
            try (Statement stm = Conexao.createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "SELECT \n"
                        + "	p.id, \n"
                        + "	pc.custosemimposto, \n"
                        + "	p.qtdembalagem \n"
                        + "FROM \n"
                        + "	produto p\n"
                        + "	join produtocomplemento pc on pc.id_produto = p.id\n"
                        + "WHERE pc.id_loja = " + idLojaVR
                )) {
                    while (rst.next()) {
                        ProdutoCustoEmbalagem pce = new ProdutoCustoEmbalagem();
                        pce.id = rst.getInt("id");
                        pce.custoSemImposto = rst.getDouble("custosemimposto");
                        pce.qtdEmbalagem = rst.getInt("qtdembalagem");
                        custos.put(pce.id, pce);
                    }
                }
            }
            
            try (Statement stm = Conexao.createStatement()) {
                for (ProdutoFornecedorVO pf: v_produtoForncedor) {               

                    long idFornecedor;
                    if (pf.getId_produtoDouble() > 0) {
                        idFornecedor = (long) pf.getId_fornecedorDouble();
                    } else {
                        idFornecedor = pf.getId_fornecedor();
                    }

                    double idProduto;
                    if (pf.getId_produtoDouble() > 0) {
                        idProduto = pf.getId_produtoDouble();
                    } else {
                        idProduto = pf.getId_produto();
                    }

                    //Se o fornecedor existir, continua 
                    CodigoAnteriorFornecedorVO antForn = fornAnteriores.get(idFornecedor, idLojaVR);                
                    if (antForn != null) {
                        //Se o produto existir, continua
                        ProdutosUnificacaoVO antProd = prodAnteriores.get(idProduto);
                        if (antProd != null) {
                            //Se o código externo não estiver cadastrado, continua
                            if (!porCodigoExterior.containsKey(antForn.getCodigoAtual() + "-" + (int)antProd.codigoatual + "-" + pf.getCodigoexterno())) {
                                CodigoExterior ext = new CodigoExterior();

                                ext.codigoexterno = pf.getCodigoexterno();
                                ext.id_fornecedor = antForn.getCodigoAtual();
                                ext.id_produto = antProd.codigoatual;
                                ext.id_estado = pf.getId_estado();
                                //Se o produto não possui produto fornecedor cadastrado
                                CodigoExterior codigoExt = porIdProduto.get(ext.id_produto);
                                if (codigoExt == null) {
                                    StringBuilder sql = new StringBuilder();
                                    sql.append("INSERT INTO produtofornecedor( ");
                                    sql.append("id_produto, id_fornecedor, id_estado, custotabela, codigoexterno, ");
                                    sql.append("qtdembalagem, id_divisaofornecedor, dataalteracao, desconto, ");
                                    sql.append("tipoipi, ipi, tipobonificacao, bonificacao, tipoverba, verba, ");
                                    sql.append("custoinicial, tipodesconto, pesoembalagem, id_tipopiscofins, ");
                                    sql.append("csosn, fatorembalagem) ");
                                    sql.append("VALUES ( ");
                                    sql.append(ext.id_produto + ", ");
                                    sql.append(ext.id_fornecedor + ", ");
                                    sql.append(ext.id_estado + ", ");
                                    ProdutoCustoEmbalagem custo = custos.get((int) ext.id_produto);
                                    sql.append(custo.custoSemImposto + ", ");
                                    sql.append(Utils.quoteSQL(ext.codigoexterno) + ", ");
                                    sql.append(custo.qtdEmbalagem + ", ");
                                    sql.append("0, ");
                                    sql.append(Utils.dateSQL(pf.dataalteracao) + ", ");
                                    sql.append("0, 0, 0, 0, 0, 0, 0,");
                                    sql.append(custo.custoSemImposto + ", ");
                                    sql.append("0, 0, 0, NULL, 1) returning id;");

                                    try (ResultSet rst = stm.executeQuery(
                                            sql.toString()
                                    )) {
                                        rst.next();
                                        ext.id_produtofornecedor = rst.getInt("id");
                                    }
                                    ext.produtofornecedor = true;  
                                } else {
                                    StringBuilder sql = new StringBuilder();
                                    sql.append("INSERT INTO produtofornecedorcodigoexterno( ");
                                    sql.append("id_produtofornecedor, codigoexterno) ");
                                    sql.append("VALUES ( ");
                                    sql.append(codigoExt.id_produtofornecedor + ", ");
                                    sql.append(Utils.quoteSQL(codigoExt.codigoexterno) + ");");
                                    stm.executeUpdate(sql.toString());
                                    ext.id_produtofornecedor = codigoExt.id_produtofornecedor;
                                    ext.produtofornecedor = false;
                                    //Inclui um produtofornecedorcodigoexterno
                                }                          
                                porCodigoExterior.put(ext.toString(), ext);
                                if (ext.produtofornecedor) {
                                    porIdProduto.put(ext.id_produto, ext);
                                }
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
    
    public int idLojaVR = 1;

    public void salvarCnpjFornecedorProdUnificacao(List<ProdutoFornecedorVO> v_produtoForncedor) throws Exception {

        StringBuilder sql = null;
        Statement stm = null,
                stm2 = null,
                stm3 = null,
                stm4 = null;
        ResultSet rst = null,
                rst2 = null,
                rst3 = null;
        double idProduto, idFornecedor;
        java.sql.Date dataalteracao;
        try {

            Conexao.begin();

            stm = Conexao.createStatement();
            stm2 = Conexao.createStatement();
            stm3 = Conexao.createStatement();
            stm4 = Conexao.createStatement();

            ProgressBar.setMaximum(v_produtoForncedor.size());
            ProgressBar.setStatus("Importando Produto Fornecedor...");

            CodigoAnteriorFornecedorDAO fornAnteriores = new CodigoAnteriorFornecedorDAO();

            for (ProdutoFornecedorVO i_produtoFornecedor : v_produtoForncedor) {
                if (i_produtoFornecedor.id_produtoDouble > 0) {
                    idProduto = i_produtoFornecedor.id_produtoDouble;
                } else {
                    idProduto = i_produtoFornecedor.id_produto;
                }
                if (i_produtoFornecedor.id_fornecedorDouble > 0) {
                    idFornecedor = i_produtoFornecedor.id_fornecedorDouble;
                } else {
                    idFornecedor = i_produtoFornecedor.id_fornecedor;
                }

                sql = new StringBuilder();
                sql.append("SELECT f.id, id_estado FROM fornecedor f ");
                sql.append("where cnpj = " + i_produtoFornecedor.cnpFornecedor);

                rst = stm.executeQuery(sql.toString());

                if (rst.next()) {

                        sql = new StringBuilder();
                        sql.append("SELECT p.id, pc.custosemimposto, p.qtdembalagem ");
                        sql.append("FROM produto p ");
                        sql.append("INNER JOIN produtocomplemento pc ON pc.id_produto = p.id ");
                        sql.append("INNER JOIN implantacao.produtos_unificacao ant on ant.codigoatual = p.id ");
                        sql.append("WHERE cast(ant.codigoanterior as numeric(14,0)) = cast(" + idProduto + " as numeric(14,0)) ");
                        sql.append("AND pc.id_loja = " + idLojaVR);
                        rst2 = stm2.executeQuery(sql.toString());

                        if (rst2.next()) {

                            sql = new StringBuilder();
                            sql.append("SELECT id from produtofornecedor ");
                            sql.append("WHERE id_fornecedor = " + rst.getInt("id") + " ");
                            sql.append("AND id_produto = " + rst2.getInt("id") + " ");
                            sql.append("AND id_estado = " + rst.getInt("id_estado") + ";");

                            rst3 = stm3.executeQuery(sql.toString());

                            if (!rst3.next()) {

                                sql = new StringBuilder();
                                sql.append("INSERT INTO produtofornecedor( ");
                                sql.append("id_produto, id_fornecedor, id_estado, custotabela, codigoexterno, ");
                                sql.append("qtdembalagem, id_divisaofornecedor, dataalteracao, desconto, ");
                                sql.append("tipoipi, ipi, tipobonificacao, bonificacao, tipoverba, verba, ");
                                sql.append("custoinicial, tipodesconto, pesoembalagem, id_tipopiscofins, ");
                                sql.append("csosn, fatorembalagem) ");
                                sql.append("VALUES ( ");
                                sql.append(rst2.getInt("id") + ", ");
                                sql.append(rst.getInt("id") + ", ");
                                sql.append(rst.getInt("id_estado") + ", ");
                                sql.append(rst2.getDouble("custosemimposto") + ", ");
                                sql.append("'" + i_produtoFornecedor.codigoexterno + "', ");
                                sql.append((i_produtoFornecedor.qtdembalagem > 0 ? i_produtoFornecedor.qtdembalagem : rst2.getInt("qtdembalagem")) + ", ");
                                sql.append("0, ");

                                if (i_produtoFornecedor.dataalteracao == null) {
                                    dataalteracao = new java.sql.Date(new java.util.Date().getTime());
                                    sql.append("'" + dataalteracao + "',");

                                } else {
                                    sql.append("'" + i_produtoFornecedor.dataalteracao + "', ");
                                }

                                sql.append("0, 0, 0, 0, 0, 0, 0,");
                                sql.append(rst2.getDouble("custosemimposto") + ", ");
                                sql.append("0, 0, 0, NULL, 1);");

                                stm4.execute(sql.toString());
                            } else {
                                
                                try (ResultSet rst5 = stm.executeQuery(
                                        "select id from produtofornecedor\n" +
                                        "where\n" +
                                        "	id_produto = " + rst2.getInt("id") + " and\n" +
                                        "	id_fornecedor = " + rst.getInt("id") + " and\n" +
                                        "	id_estado = " + rst.getInt("id_estado") + " and\n" +
                                        "	codigoexterno = " + Utils.quoteSQL(i_produtoFornecedor.getCodigoexterno())
                                )) {
                                    if (!rst5.next()) {
                                        if ((!i_produtoFornecedor.codigoexterno.isEmpty()) ||
                                                (i_produtoFornecedor.codigoexterno != null)) {

                                            sql = new StringBuilder();
                                            sql.append("INSERT INTO produtofornecedorcodigoexterno( ");
                                            sql.append("id_produtofornecedor, codigoexterno) ");
                                            sql.append("VALUES ( ");
                                            sql.append(rst3.getInt("id") + ", ");
                                            sql.append("'" + i_produtoFornecedor.codigoexterno + "');");

                                            stm4.execute(sql.toString());
                                        }
                                    }
                                }
                            }
                        }
                }

                ProgressBar.next();
            }

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

// INICIO METODOS ESPECIFICAS PARA CLIENTES    

    public void alterarQtdEmbalagem(List<ProdutoFornecedorVO> vProdutoFornecedor) throws Exception {
        StringBuilder sql = null;
        Statement stm = null,
                stm2 = null,
                stm3 = null,
                stm4 = null;
        ResultSet rst2 = null,
                rst3 = null;
        double idProduto, idFornecedor;
        java.sql.Date dataalteracao;
        try {

            Conexao.begin();

            stm = Conexao.createStatement();
            stm2 = Conexao.createStatement();
            stm3 = Conexao.createStatement();
            stm4 = Conexao.createStatement();

            ProgressBar.setMaximum(vProdutoFornecedor.size());
            ProgressBar.setStatus("Importando Produto Fornecedor (Qtd. Embalagem)...");

            CodigoAnteriorFornecedorDAO fornAnteriores = new CodigoAnteriorFornecedorDAO();

            for (ProdutoFornecedorVO i_produtoFornecedor : vProdutoFornecedor) {
                if (i_produtoFornecedor.id_produtoDouble > 0) {
                    idProduto = i_produtoFornecedor.id_produtoDouble;
                } else {
                    idProduto = i_produtoFornecedor.id_produto;
                }
                if (i_produtoFornecedor.id_fornecedorDouble > 0) {
                    idFornecedor = i_produtoFornecedor.id_fornecedorDouble;
                } else {
                    idFornecedor = i_produtoFornecedor.id_fornecedor;
                }

                CodigoAnteriorFornecedorVO fornAnterior = fornAnteriores.get((long) idFornecedor, 1);

                if (fornAnterior != null) {

                    sql = new StringBuilder();
                    sql.append("SELECT p.id, pc.custosemimposto, p.qtdembalagem FROM produto p ");
                    sql.append("INNER JOIN produtocomplemento pc ON pc.id_produto = p.id ");
                    sql.append("INNER JOIN implantacao.codigoanterior ant ON ant.codigoatual = p.id ");
                    sql.append("WHERE cast(ant.codigoanterior as numeric(14,0)) = cast(" + idProduto + " as numeric(14,0)) ");
                    sql.append("AND pc.id_loja = " + Global.idLojaFornecedor);
                    rst2 = stm2.executeQuery(sql.toString());

                    if (rst2.next()) {
                        Conexao.createStatement().execute(
                                "update produtofornecedor set\n"
                                        + "qtdembalagem = " + (i_produtoFornecedor.qtdembalagem > 0 ? i_produtoFornecedor.qtdembalagem : rst2.getInt("qtdembalagem")) + "\n"
                                        + "WHERE id_fornecedor = " + fornAnterior.getCodigoAtual() + "\n"
                                        + "AND id_produto = " + rst2.getInt("id") + "\n"
                                        + "AND id_estado = " + fornAnterior.getId_uf() + ";"
                        ); 
                    }
                }

                ProgressBar.next();
            }

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
    
    public void acertarQtdEmbalagem(List<ProdutoFornecedorVO> v_result) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        
        try {
            Conexao.begin();
            stm = Conexao.createStatement();            
            ProgressBar.setMaximum(v_result.size());
            ProgressBar.setStatus("Importando dados...Acertando Quantidade Embalagem...");
            
            for (ProdutoFornecedorVO i_result : v_result) {
                sql = new StringBuilder();
                sql.append("update produtofornecedor set "
                        + "qtdembalagem = " + i_result.qtdembalagem + " "
                        + "where id_produto = " + i_result.getId_produto() + " "
                        + "and id_fornecedor = " + i_result.getId_fornecedor() + " "
                        + "and id_estado = " + Global.idEstado);
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
}
