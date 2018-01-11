package vrimplantacao.dao.cadastro;

import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrframework.classe.Util;
import vrimplantacao.dao.ParametroPdvDAO;
import vrimplantacao.dao.administrativo.VendaPdvDAO;
import vrimplantacao.vo.cadastro.VendaItemVO;
import vrimplantacao.vo.interfaces.DivergenciaVO;
import vrimplantacao.vo.interfaces.ImportacaoLogVendaFinalizadoraVO;
import vrimplantacao.vo.interfaces.ImportacaoLogVendaItemVO;
import vrimplantacao.vo.interfaces.ImportacaoLogVendaVO;
import vrimplantacao.vo.interfaces.TipoDivergencia;

public class PdvVendaDAO {

    private ArrayList<DivergenciaVO> vDivergencia = null;

    public void salvar(List<ImportacaoLogVendaVO> v_venda, int i_idLoja, boolean i_exibeDivergenciaProduto,
            String numeroSerie) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        List<Integer> vIdCancelado = new ArrayList();
        List<Integer> vIdDescontos = new ArrayList();
        List<Integer> vIdAcrescimo = new ArrayList();
        List<Integer> vIdVenda = new ArrayList();
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        java.sql.Date data;

        try {
            Conexao.begin();
            stm = Conexao.createStatement();
            ProgressBar.setStatus("Importando Pdv.Vendas...");
            ProgressBar.setMaximum(v_venda.size());
            vDivergencia = new ArrayList();
            
            for (ImportacaoLogVendaVO oVenda : v_venda) {
                
                if ((oVenda.pagina == 0)) {
                    oVenda.ecf = new EcfDAO().get(oVenda.numeroSerie, i_idLoja);

                    if (oVenda.ecf == -1) {
                        vDivergencia.add(new DivergenciaVO("O numero de série " + oVenda.numeroSerie + " não está cadastrado para nenhuma ECF", TipoDivergencia.ERRO.getId()));
                        ProgressBar.next();
                        continue;
                    }

                    rst = stm.executeQuery("SELECT matricula FROM pdv.operador WHERE id_loja = " + i_idLoja + " AND id_situacaocadastro = " + vrimplantacao.vo.cadastro.SituacaoCadastro.ATIVO.getId() + " LIMIT 1");

                    if (rst.next()) {
                        oVenda.matricula = rst.getInt("matricula");
                    } else {
                        vDivergencia.add(new DivergenciaVO("Nenhum operador cadastrado para esta loja", TipoDivergencia.ERRO.getId()));
                        ProgressBar.next();
                        continue;
                    }

                    data = new java.sql.Date(format.parse(oVenda.data).getTime());
                    oVenda.id = new VendaPdvDAO().getId(oVenda.numeroCupom, Util.formatDataGUI(data), oVenda.ecf);

                    if (oVenda.id != -1) {
                        stm.execute("DELETE FROM pdv.vendakititem WHERE id_vendakit IN (SELECT id FROM pdv.vendakit WHERE id_venda = " + oVenda.id + ")");
                        stm.execute("DELETE FROM pdv.vendakit WHERE id_venda = " + oVenda.id);
                        stm.execute("DELETE FROM pdv.vendapromocao WHERE id_venda = " + oVenda.id);
                        stm.execute("DELETE FROM pdv.vendapromocaocupom WHERE id_venda = " + oVenda.id);
                        stm.execute("DELETE FROM pdv.vendafinalizadora WHERE id_venda = " + oVenda.id);
                        stm.execute("DELETE FROM pdv.vendaitem WHERE id_venda = " + oVenda.id);
                        stm.execute("DELETE FROM pdv.venda WHERE id = " + oVenda.id);
                    }

                    sql = new StringBuilder();
                    sql.append("INSERT INTO pdv.venda (id_loja, numerocupom, ecf, data, matricula, horainicio, horatermino, cancelado,");
                    sql.append(" subtotalimpressora, canceladoemvenda, contadordoc, cpf, valordesconto, valoracrescimo, numeroserie, ");
                    sql.append(" mfadicional, modeloimpressora, numerousuario, nomecliente, enderecocliente)");
                    sql.append(" VALUES (");
                    sql.append(i_idLoja + ", ");
                    sql.append(oVenda.numeroCupom + ", ");
                    sql.append(oVenda.ecf + ", ");
                    sql.append("'" + oVenda.data + "', ");
                    sql.append(oVenda.matricula + ", ");
                    sql.append("'" + Util.formatDataHoraBanco(oVenda.data + " " + Util.getHoraAtual()) + "',");
                    sql.append("'" + Util.formatDataHoraBanco(oVenda.data + " " + Util.getHoraAtual()) + "',");
                    sql.append(oVenda.cancelado + ",");
                    sql.append(oVenda.subtotalImpressora + ",");
                    sql.append(oVenda.canceladoEmVenda + ",");
                    sql.append(oVenda.contadorDoc + ",");
                    sql.append(oVenda.cpf + ",");
                    sql.append(oVenda.valorDesconto + ",");
                    sql.append(oVenda.valorAcrescimo + ",");
                    sql.append("'" + oVenda.numeroSerie + "',");
                    sql.append(oVenda.mfadicional + ",");
                    sql.append("'" + oVenda.modeloImpressora + "',");
                    sql.append(oVenda.numeroUsuario + ",");
                    sql.append("'',"); //nomecliente
                    sql.append("'')"); //end cliente

                    stm.execute(sql.toString());

                    rst = stm.executeQuery("SELECT CURRVAL('pdv.venda_id_seq') AS id");
                    rst.next();

                    oVenda.id = rst.getInt("id");

                    vIdVenda.add(oVenda.id);

                    if (oVenda.cancelado) {
                        vIdCancelado.add(oVenda.id);
                    }

                    if (oVenda.valorDesconto > 0) {
                        vIdDescontos.add(oVenda.id);
                    }

                    if (oVenda.valorAcrescimo > 0) {
                        vIdAcrescimo.add(oVenda.id);
                    }
                    
                    ImportacaoLogVendaFinalizadoraVO oFinalizadora = new ImportacaoLogVendaFinalizadoraVO();
                    oFinalizadora.numeroSerie = numeroSerie;
                    oFinalizadora.numeroCupom = oVenda.numeroCupom;
                    oFinalizadora.idFinalizadora = oVenda.idFinalizadora;
                    oFinalizadora.ecf = oVenda.ecf;
                    oFinalizadora.valor = oVenda.valorTotal;

                    oFinalizadora.idVenda = new VendaPdvDAO().getId2(oFinalizadora.numeroCupom, oVenda.data, oFinalizadora.ecf);

                    if (oFinalizadora.idVenda != -1) {
                        sql = new StringBuilder();
                        sql.append(" SELECT * ");
                        sql.append(" FROM pdv.vendafinalizadora");
                        sql.append(" WHERE id_venda = " + oFinalizadora.idVenda);
                        sql.append(" AND id_finalizadora = " + oFinalizadora.idFinalizadora);

                        rst = stm.executeQuery(sql.toString());

                        if (!rst.next()) {
                            sql = new StringBuilder();
                            sql.append(" INSERT INTO pdv.vendafinalizadora (");
                            sql.append(" id_venda,id_finalizadora,valor,troco) ");
                            sql.append(" VALUES ( ");
                            sql.append(oFinalizadora.idVenda + ",");
                            sql.append(oFinalizadora.idFinalizadora + ",");
                            sql.append(oFinalizadora.valor + ",");
                            sql.append(oFinalizadora.troco + ")");

                            stm.execute(sql.toString());
                        }
                    }
                    
                } else if (oVenda.pagina == 1) {
                    for (ImportacaoLogVendaItemVO oItem : oVenda.vLogVendaItem) {
                        
                        oItem.ecf = new EcfDAO().get(numeroSerie, i_idLoja);
                        if (oItem.ecf == -1) {
                            vDivergencia.add(new DivergenciaVO("O numero de série " + numeroSerie + " não está cadastrado para nenhuma ECF", TipoDivergencia.ERRO.getId()));
                            ProgressBar.next();
                            continue;
                        }
                        
                        oItem.idProduto = new ProdutoDAO().getIdAnterior(
                                    Long.parseLong(String.valueOf(oItem.codigoAnterior)));

                        if (oItem.idProduto == -1) {
                            if (i_exibeDivergenciaProduto) {
                                vDivergencia.add(new DivergenciaVO("Código de barras " + oItem.codigoBarras + " codigoanterior: " + oItem.codigoAnterior + " não cadastrado", TipoDivergencia.ERRO.getId()));
                                ProgressBar.next();
                                continue;
                            } else {
                                oItem.idProduto = new ParametroPdvDAO().get(28).getInt();
                            }
                        }
                        
                        for (ImportacaoLogVendaVO iVenda : v_venda) {
                            if ((iVenda.numeroCupom == oItem.numeroCupom) &&
                                    (iVenda.ecf == oItem.ecf)) {
                                
                                oItem.idVenda = new VendaPdvDAO().getId2(oItem.numeroCupom, 
                                        iVenda.data, oItem.ecf);
                                
                                if (oItem.idVenda != -1) {
                                    sql = new StringBuilder();
                                    sql.append("SELECT id ");
                                    sql.append(" FROM pdv.vendaitem");
                                    sql.append(" WHERE id_venda = " + oItem.idVenda);
                                    sql.append(" AND sequencia = " + oItem.sequencia);
                                    rst = stm.executeQuery(sql.toString());

                                    if (!rst.next()) {
                                        sql = new StringBuilder();
                                        sql.append("INSERT into pdv.vendaitem (id_venda,sequencia,id_produto,quantidade,precovenda,valortotal,id_aliquota,cancelado,");
                                        sql.append(" valorcancelado,contadordoc, valordesconto, valoracrescimo, valordescontocupom, valoracrescimocupom,");
                                        sql.append(" regracalculo, codigobarras, unidademedida, totalizadorparcial)");
                                        sql.append(" VALUES (");
                                        sql.append(oItem.idVenda + ",");
                                        sql.append(oItem.sequencia + ",");
                                        sql.append(oItem.idProduto + ",");
                                        sql.append(oItem.quantidade + ",");
                                        sql.append(oItem.precoVenda + ",");
                                        sql.append(oItem.valorTotal + ",");
                                        sql.append(oItem.idAliquota + ",");
                                        sql.append(oItem.cancelado + ",");
                                        sql.append(oItem.valorCancelado + ",");
                                        sql.append(oItem.contadorDoc + ",");
                                        sql.append(oItem.valorDesconto + ",");
                                        sql.append(oItem.valorAcrescimo + ",");
                                        sql.append(oItem.valorDescontoCupom + ",");
                                        sql.append(oItem.valorAcrescimoCupom + ",");
                                        sql.append("'" + oItem.regraCalculo + "',");
                                        sql.append(oItem.codigoBarras + ",");
                                        sql.append("'" + oItem.unidadeMedida + "',");
                                        sql.append("'" + oItem.totalizadorParcial + "');");
                                        stm.execute(sql.toString());
                                    }
                                }
                            }
                        }
                    }
                }
                ProgressBar.next();
            }

            stm.close();
            verificarTroco(vIdVenda);
            ratearDescontoCupom(vIdDescontos);
            ratearAcrescimoCupom(vIdAcrescimo);
            salvarCupomCancelado(vIdCancelado);

            if (!vDivergencia.isEmpty()) {
                Conexao.rollback();
            } else {
                Conexao.commit();
            }

        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    private void verificarTroco(List<Integer> i_idVenda) throws Exception {
        StringBuilder sql = null;
        ResultSet rst = null;
        Statement stm = null;
        Statement stm2 = null;
        try {
            Conexao.begin();
            stm = Conexao.createStatement();
            stm2 = Conexao.createStatement();

            for (Integer idVenda : i_idVenda) {
                sql = new StringBuilder();
                sql.append("SELECT v.numerocupom");
                sql.append(" FROM pdv.venda AS v");
                sql.append(" INNER JOIN pdv.vendaitem AS vi ON vi.id_venda = v.id");
                sql.append(" WHERE v.id = " + idVenda);
                sql.append(" AND v.cancelado = false");
                sql.append(" GROUP BY v.id, v.numerocupom");
                sql.append(" HAVING SUM(vi.valortotal - vi.valorcancelado - vi.valordesconto - vi.valordescontocupom + vi.valoracrescimo + vi.valoracrescimocupom) <> (SELECT SUM(valor - troco) FROM pdv.vendafinalizadora WHERE id_venda = v.id)");

                rst = stm.executeQuery(sql.toString());

                if (rst.next()) {

                    sql = new StringBuilder();

                    sql.append(" UPDATE pdv.vendafinalizadora SET troco = x.troco FROM (");
                    sql.append(" SELECT (SELECT SUM(valor - troco) FROM pdv.vendafinalizadora WHERE id_venda = v.id) - ");
                    sql.append(" SUM(vi.valortotal - vi.valorcancelado - vi.valordesconto - vi.valordescontocupom + vi.valoracrescimo + ");
                    sql.append(" vi.valoracrescimocupom) AS troco,(SELECT MAX(id) FROM pdv.vendafinalizadora WHERE id_venda = v.id) AS id_vendafinalizadora");
                    sql.append(" FROM pdv.venda AS v");
                    sql.append(" INNER JOIN pdv.vendaitem AS vi ON vi.id_venda = v.id");
                    sql.append(" WHERE v.id = " + idVenda);
                    sql.append(" AND v.cancelado = false");
                    sql.append(" GROUP BY v.id");
                    sql.append(" HAVING SUM(vi.valortotal - vi.valorcancelado - vi.valordesconto - vi.valordescontocupom + vi.valoracrescimo + vi.valoracrescimocupom) <> (select sum(valor - troco) FROM pdv.vendafinalizadora ");
                    sql.append(" WHERE id_venda = v.id) AND (SELECT SUM(valor - troco) ");
                    sql.append(" FROM pdv.vendafinalizadora WHERE id_venda = v.id) - SUM(vi.valortotal - vi.valorcancelado - vi.valordesconto - vi.valordescontocupom + vi.valoracrescimo + vi.valoracrescimocupom) > 0) AS x");
                    sql.append(" WHERE x.id_vendafinalizadora = pdv.vendafinalizadora.id");

//                    sql.append(" UPDATE pdv.vendafinalizadora SET troco =");
//                    sql.append(" (SELECT (SELECT sum(valor - troco) from pdv.vendafinalizadora where id_venda = v.id) - sum(vi.valortotal - vi.valorcancelado) AS troco");
//                    sql.append(" FROM pdv.venda AS v");
//                    sql.append(" INNER JOIN pdv.vendaitem AS vi ON vi.id_venda = v.id");
//                    sql.append(" WHERE v.id = " + idVenda);
//                    sql.append(" AND v.cancelado = false");
//                    sql.append(" GROUP BY v.id");
//                    sql.append(" HAVING SUM(vi.valortotal - vi.valorcancelado) <> (select sum(valor - troco) FROM pdv.vendafinalizadora WHERE id_venda = v.id))");
//                    sql.append(" WHERE id_venda = " + idVenda);
//                    sql.append(" AND id = (SELECT MAX(id) FROM pdv.vendafinalizadora where id_venda = " + idVenda + " LIMIT 1)");
                    stm2.execute(sql.toString());
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

    private void ratearDescontoCupom(List<Integer> i_vDesconto) throws Exception {
        StringBuilder sql = new StringBuilder();
        ResultSet rst = null;
        ResultSet rstItem = null;
        Statement stm = null;
        Statement stmItem = null;

        stm = Conexao.createStatement();
        stmItem = Conexao.createStatement();

        for (Integer idVenda : i_vDesconto) {
            List<VendaItemVO> vItem = new ArrayList();

            sql = new StringBuilder();
            sql.append("SELECT v.valordesconto, v.subtotalimpressora, v.valoracrescimo,");
            sql.append(" SUM(vi.valordescontocupom) AS valordescontocupom,");
            sql.append(" SUM(vi.valoracrescimocupom) AS valoracrescimocupom");
            sql.append(" FROM pdv.venda AS v");
            sql.append(" INNER JOIN pdv.vendaitem AS vi ON vi.id_venda = v.id");
            sql.append(" WHERE v.id = " + idVenda);
            sql.append(" GROUP BY v.valordesconto, v.subtotalimpressora, v.valoracrescimo");

            rst = stm.executeQuery(sql.toString());

            if (rst.next()) {
                double valor = rst.getDouble("subtotalimpressora") + rst.getDouble("valordesconto") - rst.getDouble("valoracrescimo");
                double valorDescontoCupom = rst.getDouble("valordesconto");

                sql = new StringBuilder();
                sql.append("SELECT id, valortotal, valordesconto, valoracrescimo, ");
                sql.append(" valordescontocupom, valoracrescimocupom, cancelado");
                sql.append(" FROM pdv.vendaitem");
                sql.append(" WHERE id_venda = " + idVenda);

                rstItem = stmItem.executeQuery(sql.toString());

                while (rstItem.next()) {
                    double valorProduto = rstItem.getDouble("valortotal") - rstItem.getDouble("valordesconto") + rstItem.getDouble("valoracrescimo") - rstItem.getDouble("valordescontocupom") + rstItem.getDouble("valoracrescimocupom");

                    VendaItemVO oItem = new VendaItemVO();
                    oItem.id = rstItem.getInt("id");

                    if (valor > 0) {
                        oItem.valorDescontoCupom = Util.round(valorDescontoCupom * (valorProduto / valor), 2);
                        oItem.valorDescontoPromocao = Util.round(valorDescontoCupom * (valorProduto / valor), 2);
                    }

                    if (rstItem.getBoolean("cancelado")) {
                        oItem.valorDescontoCupom = 0;
                        oItem.valorDescontoPromocao = 0;
                    }

                    vItem.add(oItem);
                }

                for (VendaItemVO oVendaItem : vItem) {
                    sql = new StringBuilder();
                    sql.append("UPDATE pdv.vendaitem SET");
                    sql.append(" valordescontocupom = " + oVendaItem.valorDescontoCupom + ", ");
                    sql.append(" valordescontopromocao = " + oVendaItem.valorDescontoPromocao);
                    sql.append(" WHERE id = " + oVendaItem.id);

                    stmItem.execute(sql.toString());
                }
            }
        }

        stm.close();
        stmItem.close();
    }

    private void ratearAcrescimoCupom(List<Integer> i_vAcrescimo) throws Exception {
        StringBuilder sql = new StringBuilder();
        ResultSet rst = null;
        ResultSet rstItem = null;
        Statement stm = null;
        Statement stmItem = null;

        stm = Conexao.createStatement();
        stmItem = Conexao.createStatement();

        for (Integer idVenda : i_vAcrescimo) {
            List<VendaItemVO> vItem = new ArrayList();

            sql = new StringBuilder();
            sql.append("SELECT v.valordesconto, v.subtotalimpressora, v.valoracrescimo,");
            sql.append(" SUM(vi.valordescontocupom) AS valordescontocupom,");
            sql.append(" SUM(vi.valoracrescimocupom) AS valoracrescimocupom");
            sql.append(" FROM pdv.venda AS v");
            sql.append(" INNER JOIN pdv.vendaitem AS vi ON vi.id_venda = v.id");
            sql.append(" WHERE v.id = " + idVenda);
            sql.append(" GROUP BY v.valordesconto, v.subtotalimpressora, v.valoracrescimo");

            rst = stm.executeQuery(sql.toString());

            if (rst.next()) {
                double valor = rst.getDouble("subtotalimpressora") + rst.getDouble("valordesconto") - rst.getDouble("valoracrescimo");
                double valorAcrescimoCupom = rst.getDouble("valoracrescimo");

                sql = new StringBuilder();
                sql.append("SELECT id, valortotal, valordesconto, valoracrescimo, ");
                sql.append(" valordescontocupom, valoracrescimocupom, cancelado");
                sql.append(" FROM pdv.vendaitem");
                sql.append(" WHERE id_venda = " + idVenda);

                rstItem = stmItem.executeQuery(sql.toString());

                while (rstItem.next()) {
                    double valorProduto = rstItem.getDouble("valortotal") - rstItem.getDouble("valordesconto") + rstItem.getDouble("valoracrescimo") - rstItem.getDouble("valordescontocupom") + rstItem.getDouble("valoracrescimocupom");

                    VendaItemVO oItem = new VendaItemVO();
                    oItem.id = rstItem.getInt("id");

                    if (valor > 0) {
                        oItem.valorAcrescimoCupom = Util.round(valorAcrescimoCupom * (valorProduto / valor), 2);
                    }

                    if (rstItem.getBoolean("cancelado")) {
                        oItem.valorAcrescimoCupom = 0;
                    }

                    vItem.add(oItem);
                }

                for (VendaItemVO oVendaItem : vItem) {
                    sql = new StringBuilder();
                    sql.append("UPDATE pdv.vendaitem SET");
                    sql.append(" valoracrescimocupom = " + oVendaItem.valorAcrescimoCupom);
                    sql.append(" WHERE id = " + oVendaItem.id);

                    stmItem.execute(sql.toString());
                }
            }
        }

        stm.close();
        stmItem.close();
    }

    private void salvarCupomCancelado(List<Integer> i_vCancelado) throws Exception {
        StringBuilder sql = new StringBuilder();
        ResultSet rst = null;
        Statement stm = null;

        stm = Conexao.createStatement();

        for (Integer idVenda : i_vCancelado) {
            List<VendaItemVO> vItem = new ArrayList();

            sql = new StringBuilder();
            sql.append("SELECT id, valortotal FROM pdv.vendaitem WHERE id_venda = " + idVenda);

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {
                VendaItemVO oItem = new VendaItemVO();
                oItem.id = rst.getInt("id");
                oItem.valorTotal = rst.getDouble("valortotal");
                oItem.valorCancelado = oItem.valorTotal;

                vItem.add(oItem);
            }

            for (VendaItemVO oVendaItem : vItem) {
                sql = new StringBuilder();
                sql.append("UPDATE pdv.vendaitem SET");
                sql.append(" valorcancelado = " + oVendaItem.valorCancelado);
                sql.append(" WHERE id = " + oVendaItem.id);

                stm.execute(sql.toString());
            }
        }

        stm.close();
    }

}
