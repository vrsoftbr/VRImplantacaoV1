package vrimplantacao.dao.cadastro;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrframework.classe.Conexao;
import vrframework.classe.Util;
import vrframework.classe.VRException;
import vrimplantacao.dao.CodigoInternoDAO;
import vrimplantacao.dao.DataProcessamentoDAO;
import vrimplantacao.vo.loja.LojaFiltroConsultaVO;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao.vo.loja.SituacaoCadastro;
import vrimplantacao2.parametro.Versao;

public class LojaDAO {

    public List<LojaVO> consultar(LojaFiltroConsultaVO i_filtro) throws Exception {
        Statement stm = null;
        StringBuilder sql = null;
        ResultSet rst = null;

        stm = Conexao.createStatement();

        sql = new StringBuilder();
        sql.append("SELECT lj.*, sc.descricao AS situacaocadastro, r.descricao AS regiao FROM loja AS lj");
        sql.append(" INNER JOIN situacaocadastro AS sc ON sc.id = lj.id_situacaocadastro");
        sql.append(" INNER JOIN regiao AS r ON r.id = lj.id_regiao");
        sql.append(" WHERE 1 = 1");

        if (i_filtro.id > -1) {
            sql.append(" AND lj.id = " + i_filtro.id);
        }

        if (!i_filtro.descricao.trim().equals("")) {
            sql.append(" AND " + Util.getGoogle("lj.descricao", i_filtro.descricao));
        }

        if (i_filtro.ordenacao.isEmpty()) {
            sql.append(" ORDER BY lj.descricao");
        } else {
            sql.append(" ORDER BY " + i_filtro.ordenacao + ", lj.descricao");
        }

        if (i_filtro.limite > 0) {
            sql.append(" LIMIT " + i_filtro.limite);
        }

        rst = stm.executeQuery(sql.toString());

        List<LojaVO> vLoja = new ArrayList();

        while (rst.next()) {
            LojaVO oLoja = new LojaVO();
            oLoja.id = rst.getInt("id");
            oLoja.descricao = rst.getString("descricao");
            oLoja.idFornecedor = rst.getInt("id_fornecedor");
            oLoja.nomeServidor = rst.getString("nomeservidor");
            oLoja.servidorCentral = rst.getBoolean("servidorcentral");
            oLoja.idSituacaoCadastro = rst.getInt("id_situacaocadastro");
            oLoja.situacaoCadastro = rst.getString("situacaocadastro");
            oLoja.idRegiao = rst.getInt("id_regiao");
            oLoja.geraConcentrador = rst.getBoolean("geraconcentrador");
            oLoja.regiao = rst.getString("regiao");

            vLoja.add(oLoja);
        }

        stm.close();

        return vLoja;
    }

    public LojaVO carregar(int i_id) throws Exception {
        Statement stm = null;
        StringBuilder sql = null;
        ResultSet rst = null;

        stm = Conexao.createStatement();

        sql = new StringBuilder();
        sql.append("SELECT * FROM loja WHERE id = " + i_id);

        rst = stm.executeQuery(sql.toString());

        if (!rst.next()) {
            throw new VRException("Loja não encontrada!");
        }

        LojaVO oLoja = new LojaVO();
        oLoja.id = rst.getInt("id");
        oLoja.descricao = rst.getString("descricao");
        oLoja.idFornecedor = rst.getInt("id_fornecedor");
        oLoja.nomeServidor = rst.getString("nomeservidor");
        oLoja.servidorCentral = rst.getBoolean("servidorcentral");
        oLoja.idRegiao = rst.getInt("id_regiao");
        oLoja.geraConcentrador = rst.getBoolean("geraconcentrador");

        stm.close();

        return oLoja;
    }

    public LojaVO carregar2(int i_id) throws Exception {
        Statement stm = null;
        StringBuilder sql = null;
        ResultSet rst = null;
        LojaVO oLoja = new LojaVO();
        stm = Conexao.createStatement();

        sql = new StringBuilder();
        sql.append("SELECT * FROM loja WHERE id = " + i_id);
        rst = stm.executeQuery(sql.toString());

        if (rst.next()) {
            oLoja.id = rst.getInt("id");
            oLoja.descricao = rst.getString("descricao");
            oLoja.idFornecedor = rst.getInt("id_fornecedor");
            oLoja.nomeServidor = rst.getString("nomeservidor");
            oLoja.servidorCentral = rst.getBoolean("servidorcentral");
            oLoja.idRegiao = rst.getInt("id_regiao");
            oLoja.geraConcentrador = rst.getBoolean("geraconcentrador");
        }
        stm.close();
        return oLoja;
    }

    public List<LojaVO> carregar() throws Exception {
        Statement stm = null;
        ResultSet rst = null;

        try {
            stm = Conexao.createStatement();

            rst = stm.executeQuery("SELECT * FROM loja ORDER BY loja ASC");

            List<LojaVO> vLoja = new ArrayList<>();

            while (rst.next()) {
                LojaVO oLoja = new LojaVO();
                oLoja.id = rst.getInt("id");
                oLoja.descricao = rst.getString("descricao");

                vLoja.add(oLoja);
            }

            return vLoja;

        } catch (Exception e) {
            throw e;

        } finally {
            Conexao.destruir(null, stm, rst);
        }

    }

    public void salvar(LojaVO i_loja) throws Exception {
        Statement stm = null;
        Statement stm2 = null;
        Statement stm3 = null;
        StringBuilder sql = null;
        ResultSet rst = null;
        ResultSet rst2 = null;
        ResultSet rst3 = null;

        try {
            Conexao.begin();
            stm = Conexao.createStatement();
            stm2 = Conexao.createStatement();
            stm3 = Conexao.createStatement();

            rst = stm.executeQuery("SELECT id FROM loja WHERE id = " + i_loja.id);

            if (!rst.next()) {
                sql = new StringBuilder();
                sql.append("INSERT INTO loja (id, descricao, id_fornecedor, id_situacaocadastro, nomeservidor, servidorcentral, id_regiao, geraconcentrador) VALUES (");
                sql.append(i_loja.id + ",");
                sql.append("'" + i_loja.descricao + "',");
                sql.append(i_loja.idFornecedor + ",");
                sql.append(SituacaoCadastro.ATIVO.getId() + ",");
                sql.append("'" + i_loja.nomeServidor + "',");
                sql.append(i_loja.servidorCentral + ",");
                sql.append(i_loja.idRegiao + ",");
                sql.append(i_loja.geraConcentrador + ")");

                stm.execute(sql.toString());

                sql = new StringBuilder();
                sql.append("INSERT INTO produtocomplemento (id_produto, prateleira, secao, estoqueminimo, estoquemaximo, valoripi, dataultimopreco,");
                sql.append(" dataultimaentrada, custosemimposto, custocomimposto, custosemimpostoanterior, custocomimpostoanterior, precovenda, precovendaanterior,");
                sql.append(" precodiaseguinte, estoque, troca, emiteetiqueta, custosemperdasemimposto, custosemperdasemimpostoanterior, customediocomimposto,");
                sql.append(" customediosemimposto, id_aliquotacredito, dataultimavenda, teclaassociada, id_situacaocadastro, id_loja, descontinuado,");
                sql.append(" quantidadeultimaentrada, centralizado, operacional, valoricmssubstituicao, dataultimaentradaanterior, cestabasica, valoroutrassubstituicao,");
                sql.append("id_tipocalculoddv");
                if (Versao.maiorQue(3,17,10)) {
                    sql.append(", id_tipoproduto, fabricacaopropria");
                }
                sql.append(")");
                sql.append(" (SELECT id_produto, prateleira, secao, estoqueminimo, estoquemaximo, valoripi, null, null, " + (i_loja.copiaCusto ? "custosemimposto" : "0") + ",");
                sql.append(" " + (i_loja.copiaCusto ? "custocomimposto" : "0") + ", 0, 0, " + (i_loja.copiaPrecoVenda ? "precovenda" : "0") + ",");
                sql.append("  0, precodiaseguinte, 0, 0, emiteetiqueta, 0, 0, 0, 0, id_aliquotacredito,");
                sql.append(" null, teclaassociada, id_situacaocadastro, " + i_loja.id + ", descontinuado, 0, centralizado, operacional,");
                sql.append(" valoricmssubstituicao, null, cestabasica, 0, 3");
                if (Versao.maiorQue(3,17,10)) {
                    sql.append(", 0, false");
                }
                sql.append(" from produtocomplemento where id_loja = " + i_loja.idCopiarLoja + ")");

                stm.execute(sql.toString());

                sql = new StringBuilder();
                sql.append("INSERT INTO fornecedorprazo(id_fornecedor, id_loja, id_divisaofornecedor,");
                sql.append(" prazoentrega,prazovisita, prazoseguranca)");
                sql.append(" (SELECT id_fornecedor, " + i_loja.id + ", id_divisaofornecedor, prazoentrega, prazovisita, prazoseguranca ");
                sql.append(" FROM fornecedorprazo WHERE id_loja = " + i_loja.idCopiarLoja + ");");

                stm.execute(sql.toString());

                sql = new StringBuilder();
                sql.append("INSERT INTO fornecedorprazopedido(id_fornecedor, id_loja,");
                sql.append(" diasentregapedido,diasatualizapedidoparcial)");
                sql.append(" (SELECT id_fornecedor, " + i_loja.id + ", diasentregapedido,diasatualizapedidoparcial ");
                sql.append(" FROM fornecedorprazopedido WHERE id_loja = " + i_loja.idCopiarLoja + ");");
                stm.execute(sql.toString());

                sql = new StringBuilder();
                sql.append("INSERT INTO parametrovalor (id_loja, id_parametro, valor)");
                sql.append(" (SELECT " + i_loja.id + ", id_parametro, valor FROM parametrovalor WHERE id_loja = " + i_loja.idCopiarLoja + ")");

                stm.execute(sql.toString());

                sql = new StringBuilder();
                sql.append("INSERT INTO pdv.funcaoniveloperador (id_loja, id_funcao, id_tiponiveloperador)");
                sql.append(" (SELECT " + i_loja.id + ", id_funcao, id_tiponiveloperador FROM pdv.funcaoniveloperador WHERE id_loja = " + i_loja.idCopiarLoja + ")");

                stm.execute(sql.toString());

                sql = new StringBuilder();
                sql.append("INSERT INTO pdv.parametrovalor (id_loja,id_parametro,valor)");
                sql.append(" (SELECT " + i_loja.id + ",id_parametro,valor FROM pdv.parametrovalor WHERE id_loja = " + i_loja.idCopiarLoja + ")");

                stm.execute(sql.toString());

                sql = new StringBuilder();
                sql.append("UPDATE pdv.parametrovalor SET valor = " + i_loja.id);
                sql.append(" WHERE id_loja = " + i_loja.id);
                sql.append(" AND id_parametro = 99");

                stm.execute(sql.toString());

                sql = new StringBuilder();
                sql.append("SELECT * FROM pdv.cartaolayout WHERE id_loja = " + i_loja.idCopiarLoja);

                rst2 = stm.executeQuery(sql.toString());

                while (rst2.next()) {
                    int proximoId = new CodigoInternoDAO().get("pdv.cartaolayout");

                    sql = new StringBuilder();
                    sql.append("INSERT INTO pdv.cartaolayout (id, id_loja, id_tipocartao, posicao, tamanho, id_tipocartaocampo)");
                    sql.append(" VALUES (");
                    sql.append(proximoId + ",");
                    sql.append(i_loja.id + ",");
                    sql.append(rst2.getInt("id_tipocartao") + ",");
                    sql.append(rst2.getInt("posicao") + ",");
                    sql.append(rst2.getInt("tamanho") + ",");
                    sql.append(rst2.getInt("id_tipocartaocampo") + ")");

                    stm2.execute(sql.toString());
                }

                sql = new StringBuilder();
                sql.append("SELECT * FROM pdv.balancaetiquetalayout WHERE id_loja = " + i_loja.idCopiarLoja);

                rst2 = stm.executeQuery(sql.toString());

                while (rst2.next()) {
                    int proximoId = new CodigoInternoDAO().get("pdv.balancaetiquetalayout");

                    sql = new StringBuilder();
                    sql.append("INSERT INTO pdv.balancaetiquetalayout (id, id_loja, id_tipobalancaetiqueta, id_tipobalancoetiquetacampo, iniciopeso,");
                    sql.append(" tamanhopeso, iniciopreco, tamanhopreco) VALUES (");
                    sql.append(proximoId + ",");
                    sql.append(i_loja.id + ",");
                    sql.append(rst2.getInt("id_tipobalancaetiqueta") + ",");
                    sql.append(rst2.getInt("id_tipobalancoetiquetacampo") + ",");
                    sql.append(rst2.getInt("iniciopeso") + ",");
                    sql.append(rst2.getInt("tamanhopeso") + ",");
                    sql.append(rst2.getInt("iniciopreco") + ",");
                    sql.append(rst2.getInt("tamanhopreco") + ")");

                    stm2.execute(sql.toString());
                }

                if (i_loja.copiaTecladoLayout) {
                    sql = new StringBuilder();
                    sql.append("SELECT * FROM pdv.tecladolayout WHERE id_loja = " + i_loja.idCopiarLoja);

                    rst2 = stm.executeQuery(sql.toString());

                    while (rst2.next()) {
                        int proximoIdTecladoLayout = new CodigoInternoDAO().get("pdv.tecladolayout");

                        sql = new StringBuilder();
                        sql.append("INSERT INTO pdv.tecladolayout (id, id_loja, descricao) VALUES (");
                        sql.append(proximoIdTecladoLayout + ",");
                        sql.append(i_loja.id + ",");
                        sql.append("'" + rst2.getString("descricao") + "')");

                        stm2.execute(sql.toString());

                        sql = new StringBuilder();
                        sql.append("SELECT * FROM pdv.tecladolayoutfuncao AS tlf");
                        sql.append(" INNER JOIN pdv.tecladolayout AS tl ON tl.id = tlf.id_tecladolayout");
                        sql.append(" WHERE tl.id_loja = " + i_loja.idCopiarLoja);
                        sql.append(" AND tl.id = " + rst2.getInt("id"));

                        rst3 = stm2.executeQuery(sql.toString());

                        while (rst3.next()) {
                            int proximoIdLayoutFuncao = new CodigoInternoDAO().get("pdv.tecladolayoutfuncao");

                            sql = new StringBuilder();
                            sql.append("INSERT INTO pdv.tecladolayoutfuncao (id, id_tecladolayout, codigoretorno, id_funcao) VALUES (");
                            sql.append(proximoIdLayoutFuncao + ",");
                            sql.append(proximoIdTecladoLayout + ",");
                            sql.append(rst3.getInt("codigoretorno") + ",");
                            sql.append(rst3.getInt("id_funcao") + ")");

                            stm3.execute(sql.toString());
                        }
                    }
                }

                sql = new StringBuilder();
                sql.append("INSERT INTO pdv.finalizadoraconfiguracao (id_loja,id_finalizadora,aceitatroco,aceitaretirada,aceitaabastecimento,");
                sql.append(" aceitarecebimento,utilizacontravale,retiradatotal,valormaximotroco,juros,tipomaximotroco,aceitaretiradacf,retiradatotalcf,utilizado)");
                sql.append(" (SELECT " + i_loja.id + ",id_finalizadora,aceitatroco,aceitaretirada,aceitaabastecimento,aceitarecebimento,");
                sql.append(" utilizacontravale,retiradatotal,valormaximotroco,juros,tipomaximotroco,aceitaretiradacf,retiradatotalcf,utilizado FROM pdv.finalizadoraconfiguracao WHERE id_loja = " + i_loja.idCopiarLoja + ")");

                stm.execute(sql.toString());

                sql = new StringBuilder();
                sql.append("INSERT INTO dataprocessamento (id_loja, data) VALUES (");
                sql.append(i_loja.id + ",");
                sql.append("'" + Util.formatDataBanco(new DataProcessamentoDAO().get()) + "')");

                stm.execute(sql.toString());

                sql = new StringBuilder();
                sql.append("insert into comprovante select id, " + i_loja.id + " as id_loja, descricao, cabecalho, ");
                sql.append("detalhe, rodape from comprovante where id_loja = " + i_loja.idCopiarLoja);

                stm.execute(sql.toString());

                sql = new StringBuilder();
                sql.append("insert into pdv.operador (id_loja, matricula,nome,senha,codigo,id_tiponiveloperador,id_situacaocadastro) ");
                sql.append("select " + i_loja.id + ", matricula, nome, senha, codigo, id_tiponiveloperador, id_situacaocadastro ");
                sql.append("from pdv.operador ");
                sql.append("where id_loja = " + i_loja.idCopiarLoja + " ");
                sql.append("and matricula = 500001 ");

                stm.execute(sql.toString());
                
                sql = new StringBuilder();
                sql.append(
                        "insert into notasaidasequencia (id_loja, numerocontrole, serie) values (" + i_loja.id + ", 1, 1); \n " +
                        "insert into tiposaidanotasaidasequencia (id_loja, id_tiposaida, id_notasaidasequencia)\n" +
                        "select \n" +
                        "	" + i_loja.id + ",\n" +
                        "	id_tiposaida,\n" +
                        "	" + i_loja.id +"\n" +
                        "from \n" +
                        "	tiposaidanotasaidasequencia\n" +
                        "where\n" +
                        "	id_notasaidasequencia not in (" + i_loja.id + ") and id_notasaidasequencia = 1");
                
                stm.execute(sql.toString());
                
            } else {
                sql = new StringBuilder();
                sql.append("UPDATE loja SET");
                sql.append(" descricao = '" + i_loja.descricao + "',");
                sql.append(" id_fornecedor = " + i_loja.idFornecedor + ",");
                sql.append(" id_situacaocadastro = " + SituacaoCadastro.ATIVO.getId() + ",");
                sql.append(" nomeservidor = '" + i_loja.nomeServidor + "',");
                sql.append(" servidorcentral = " + i_loja.servidorCentral + ",");
                sql.append(" id_regiao = " + i_loja.idRegiao + ",");
                sql.append(" geraconcentrador = " + i_loja.geraConcentrador);
                sql.append(" WHERE id = " + i_loja.id);

                stm.execute(sql.toString());
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

    public boolean isLoja(int i_idLoja) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        stm = Conexao.createStatement();
        rst = stm.executeQuery("SELECT id from loja where id = " + i_idLoja);

        if (rst.next()) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isFornecedor(int i_idFornecedor) throws Exception {
        Statement stm = null;
        ResultSet rst = null;

        stm = Conexao.createStatement();

        rst = stm.executeQuery("SELECT id FROM loja WHERE id_fornecedor = " + i_idFornecedor);

        if (rst.next()) {
            return true;
        } else {
            return false;
        }
    }

    public int getId(int i_idFornecedor) throws Exception {
        Statement stm = null;
        ResultSet rst = null;

        stm = Conexao.createStatement();

        rst = stm.executeQuery("SELECT id FROM loja WHERE id_fornecedor = " + i_idFornecedor);

        if (rst.next()) {
            return rst.getInt("id");
        } else {
            return 0;
        }
    }

    public int getIdFornecedor(int i_idLoja) throws Exception {
        Statement stm = null;
        ResultSet rst = null;

        stm = Conexao.createStatement();

        rst = stm.executeQuery("SELECT id_fornecedor FROM loja WHERE id = " + i_idLoja);

        if (rst.next()) {
            return rst.getInt("id_fornecedor");
        } else {
            return 0;
        }
    }
}
