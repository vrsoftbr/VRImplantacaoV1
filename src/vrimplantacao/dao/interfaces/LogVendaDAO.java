package vrimplantacao.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import vrimplantacao.dao.LogTransacaoDAO;
import vrimplantacao.dao.ParametroPdvDAO;
import vrimplantacao.dao.administrativo.VendaPdvDAO;
import vrimplantacao.dao.cadastro.AliquotaDAO;
import vrimplantacao.dao.cadastro.EcfDAO;
import vrimplantacao.dao.cadastro.ProdutoDAO;
import vrimplantacao.dao.fiscal.MapaResumoDAO;
import vrimplantacao.vo.Formulario;
import vrimplantacao.vo.TipoTransacao;
import vrimplantacao.vo.cadastro.SituacaoCadastro;
import vrimplantacao.vo.cadastro.VendaItemVO;
import vrimplantacao.vo.fiscal.MapaResumoItemVO;
import vrimplantacao.vo.fiscal.MapaResumoVO;
import vrimplantacao.vo.interfaces.DivergenciaVO;
import vrimplantacao.vo.interfaces.ImportacaoLogVendaFinalizadoraVO;
import vrimplantacao.vo.interfaces.ImportacaoLogVendaItemVO;
import vrimplantacao.vo.interfaces.ImportacaoLogVendaVO;
import vrimplantacao.vo.interfaces.TipoDivergencia;
import vrframework.classe.Conexao;
import vrframework.classe.Database;
import vrframework.classe.ProgressBar;
import vrframework.classe.Util;
import vrframework.classe.VRException;
import vrframework.remote.Arquivo;
import vrimplantacao.dao.cadastro.ClientePreferencialDAO;
import vrimplantacao.dao.cadastro.CodigoAnteriorDAO;
import vrimplantacao.gui.interfaces.rfd.ItensNaoExistentesController;
import vrimplantacao.gui.interfaces.rfd.ProdutoMapa;
import vrimplantacao.gui.interfaces.rfd.ProdutoMapa.TipoMapa;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.CodigoAnteriorVO;
import vrimplantacao2.dao.cadastro.produto.ProdutoAnteriorDAO;
import vrimplantacao2.utils.multimap.MultiMap;

public class LogVendaDAO {

    private ArrayList<DivergenciaVO> vDivergencia = null;

    public static class LojaV2 {

        public String impSistema;
        public String impLoja;

        @Override
        public String toString() {
            return impSistema + " - " + impLoja;
        }

    }

    public LojaV2 impLoja;

    public List<LojaV2> carregarLojaV2() throws Exception {
        List<LojaV2> result = new ArrayList<>();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select distinct impsistema, imploja from implantacao.codant_produto order by impsistema, imploja"
            )) {
                while (rst.next()) {
                    LojaV2 loja = new LojaV2();
                    loja.impSistema = rst.getString("impsistema");
                    loja.impLoja = rst.getString("imploja");
                    result.add(loja);
                }
            }
        }
        return result;
    }

    public void clearAnteriores() {
        daoV2.clearAnteriores();
    }

    private final ProdutoAnteriorDAO daoV2 = new ProdutoAnteriorDAO(false);

    public ArrayList<DivergenciaVO> importar(String i_arquivo, boolean i_exibeDivergenciaProduto,
            boolean i_verificarCodAnterior, boolean i_verificarCodigoBarras, int idLoja) throws Exception {

        StringBuilder sql = new StringBuilder();
        ResultSet rst = null;
        Statement stm = null;
        int l = 0;
        long codigoBarrasAnterior;
        String codigoProduto = "0";

        List<Integer> vIdCancelado = new ArrayList();
        List<Integer> vIdDescontos = new ArrayList();
        List<Integer> vIdAcrescimo = new ArrayList();
        List<Integer> vIdVenda = new ArrayList();

        try {
            Conexao.begin();
            stm = Conexao.createStatement();

            ProgressBar.setStatus("Importando Log Venda...");

            Arquivo arquivo = new Arquivo(i_arquivo, "r", "UTF-8");

            ProgressBar.setMaximum(arquivo.getLineCount());

            String linha = "";
            String data = "";

            vDivergencia = new ArrayList();
            MapaResumoVO oMapaResumo = new MapaResumoVO();

            ItensNaoExistentesController dao = new ItensNaoExistentesController();

            MultiMap<String, ProdutoMapa> mapa = new MultiMap<>();
            //TODO: Incluir mais opções            
            TipoMapa tipo = TipoMapa.EAN;
            for (ProdutoMapa mp : dao.carregarMapa(true, tipo)) {
                mapa.put(mp, mp.getTipo().toString(), mp.getCodrfd());
            }

            int codigoInicio = 2, codigoTamanho = 6;

            try (Statement stm2 = Conexao.createStatement()) {
                stm2.execute(
                        "do $$\n"
                        + "declare\n"
                        + "	vid_loja integer = " + idLoja + ";\n"
                        + "	vutilizapeso boolean;\n"
                        + "	codigo_inicio integer;\n"
                        + "	codigo_tamanho integer;\n"
                        + "begin\n"
                        + "	select\n"
                        + "		valor::boolean\n"
                        + "	from\n"
                        + "		pdv.parametrovalor pv\n"
                        + "	where\n"
                        + "		id_parametro = 13\n"
                        + "		and id_loja = vid_loja\n"
                        + "	into\n"
                        + "		vutilizapeso;\n"
                        + "\n"
                        + "	select \n"
                        + "		case vutilizapeso when true then iniciopeso else iniciopreco end inicio,\n"
                        + "		case vutilizapeso when true then tamanhopeso else tamanhopreco end tamanho\n"
                        + "	from \n"
                        + "		pdv.balancaetiquetalayout\n"
                        + "	where \n"
                        + "		id_tipobalancoetiquetacampo = 0 \n"
                        + "		and id_loja = vid_loja\n"
                        + "	into\n"
                        + "		codigo_inicio,		\n"
                        + "		codigo_tamanho;\n"
                        + "\n"
                        + "	create temp table temp_parametros (\n"
                        + "		codigoinicio int, \n"
                        + "		codigotamanho int\n"
                        + "	) on commit drop;\n"
                        + "\n"
                        + "	insert into temp_parametros values (codigo_inicio, codigo_tamanho);\n"
                        + "\n"
                        + "end;$$;"
                );

                try (ResultSet rst2 = stm2.executeQuery(
                        "select * from temp_parametros;"
                )) {
                    while (rst2.next()) {
                        codigoInicio = rst2.getInt("codigoinicio");
                        codigoTamanho = rst2.getInt("codigotamanho");
                    }
                }
            }

            while (arquivo.ready()) {
                linha = arquivo.readLine();
                l++;

                codigoBarrasAnterior = -1;
                String modeloImpressora = "";

                if (linha.substring(1, 3).equals("01")) {
                    modeloImpressora = linha.substring(51, 71).trim();
                }

                if (linha.substring(1, 3).equals("02")) {
                    oMapaResumo.gtFinal = Double.parseDouble(linha.substring(252, 270)) / 100;
                    oMapaResumo.gtInicial = oMapaResumo.gtFinal;
                    oMapaResumo.contadorReinicio = Integer.parseInt(linha.substring(246, 252));
                }

                if (linha.substring(1, 3).equals("12")) {
                    oMapaResumo.dataHoraEmissaoRz = Util.formatData(linha.substring(72, 80), "yyyyMMdd", "dd/MM/yyyy") + " " + Util.formatData(linha.substring(80, 86), "hhmmss", "hh:mm:ss");
                }

                if (linha.substring(1, 3).equals("13")) {
                    oMapaResumo.reducao = Integer.parseInt(linha.substring(46, 52));

                }

                if (linha.substring(1, 3).equals("14")) {
                    ImportacaoLogVendaVO oVenda = new ImportacaoLogVendaVO();
                    oVenda.numeroCupom = Integer.parseInt(linha.substring(52, 58).trim());
                    oVenda.data = Util.formatData(linha.substring(58, 66), "yyyyMMdd", "dd/MM/yyyy");
                    oVenda.numeroSerie = linha.substring(3, 23).trim();
                    oVenda.cancelado = linha.substring(122, 123).equals("N") ? false : true;

                    oVenda.valorDesconto = Double.parseDouble(linha.substring(82, 93)) / 100;
                    oVenda.valorAcrescimo = Double.parseDouble(linha.substring(94, 107)) / 100;

                    oVenda.subtotalImpressora = Double.parseDouble(linha.substring(108, 122)) / 100;
                    oVenda.cpf = Long.parseLong(linha.substring(177, 191));
                    oVenda.modeloImpressora = modeloImpressora;

                    data = oVenda.data;

                    oVenda.ecf = new EcfDAO().get(oVenda.numeroSerie, idLoja);

                    oMapaResumo.ecf = oVenda.ecf;

                    oMapaResumo.data = oVenda.data;

                    oMapaResumo.desconto += oVenda.valorDesconto;
                    oMapaResumo.acrescimo += oVenda.valorAcrescimo;

                    oMapaResumo.totalNaoFiscal = 0;
                    oMapaResumo.lancamentoManual = false;

                    if (oVenda.ecf == -1) {
                        vDivergencia.add(new DivergenciaVO("O numero de série " + oVenda.numeroSerie + " não está cadastrado para nenhuma ECF", TipoDivergencia.ERRO.getId()));
                        ProgressBar.next();
                        continue;
                    }

                    rst = stm.executeQuery("SELECT matricula FROM pdv.operador WHERE id_loja = " + idLoja + " AND id_situacaocadastro = " + SituacaoCadastro.ATIVO.getId() + " LIMIT 1");

                    if (rst.next()) {
                        oVenda.matricula = rst.getInt("matricula");
                    } else {
                        vDivergencia.add(new DivergenciaVO("Nenhum operador cadastrado para esta loja", TipoDivergencia.ERRO.getId()));
                        ProgressBar.next();
                        continue;
                    }

                    oVenda.id = new VendaPdvDAO().getId(oVenda.numeroCupom, oVenda.data, oVenda.ecf);

                    if (oVenda.id != -1) {
                        if (Database.tabelaExiste("parana.vendaoperadorbalanca")) {
                            stm.execute("DELETE FROM parana.vendaoperadorbalanca WHERE id_vendaitem IN (SELECT id FROM pdv.vendaitem WHERE id_venda = " + oVenda.id + ")");
                        }

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
                    //sql.append(Global.idLoja + ",");
                    sql.append(idLoja + ",");
                    sql.append(oVenda.numeroCupom + ",");
                    sql.append(oVenda.ecf + ",");
                    sql.append("'" + Util.formatDataBanco(oVenda.data) + "',");
                    sql.append(oVenda.matricula + ",");
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

                }

                if (linha.substring(1, 3).equals("15")) {
                    ImportacaoLogVendaItemVO oItem = new ImportacaoLogVendaItemVO();

                    //verifica casas decimais
                    oItem.casasDecimaisQuantidade = Integer.parseInt(linha.substring(265, 266));
                    oItem.casasDecimaisValor = Integer.parseInt(linha.substring(266, 267));

                    oItem.numeroSerie = linha.substring(3, 23).trim();
                    oItem.numeroCupom = Integer.parseInt(linha.substring(46, 52));
                    oItem.contadorDoc = Integer.parseInt(linha.substring(52, 58));
                    oItem.sequencia = Integer.parseInt(linha.substring(58, 61));

                    if (i_verificarCodAnterior) {
                        oItem.codigoAnterior = Long.parseLong(linha.substring(61, 75).trim());
                    } else if (i_verificarCodigoBarras) {
                        oItem.codigoBarras = Long.parseLong(linha.substring(61, 75).trim());
                        oItem.descricaoProduto = Utils.acertarTexto(linha.substring(75, 175).trim());
                    } else {
                        oItem.codigoBarras = Long.parseLong(linha.substring(61, 75).trim());
                    }
                    

                    /*if (!i_verificarCodAnterior) {
                     oItem.codigoBarras = Long.parseLong(linha.substring(61, 75).trim());
                     } else {
                     oItem.codigoAnterior = Long.parseLong(linha.substring(61, 75).trim());
                     }*/
                    oItem.quantidade = Double.parseDouble(linha.substring(175, 182)) / Math.pow(10, oItem.casasDecimaisQuantidade);
                    oItem.precoVenda = Double.parseDouble(linha.substring(185, 193)) / Math.pow(10, oItem.casasDecimaisValor);
                    oItem.valorDesconto = Double.parseDouble(linha.substring(193, 201)) / Math.pow(10, oItem.casasDecimaisValor);
                    oItem.valorAcrescimo = Double.parseDouble(linha.substring(201, 209)) / Math.pow(10, oItem.casasDecimaisValor);

                    oItem.valorTotal = (Double.parseDouble(linha.substring(209, 223)) / 100) + oItem.valorDesconto - oItem.valorAcrescimo;
                    oItem.aliquota = linha.substring(223, 230);

                    if (linha.substring(230, 231).toUpperCase().equals("S")) {
                        oItem.cancelado = true;
                        oItem.valorCancelado = oItem.valorTotal;
                        oMapaResumo.cancelamento += oItem.valorTotal;
                    }

                    oMapaResumo.gtInicial -= oItem.valorTotal;

                    oItem.ecf = new EcfDAO().get(oItem.numeroSerie, idLoja);

                    if (oItem.ecf == -1) {
                        vDivergencia.add(new DivergenciaVO("O numero de série " + oItem.numeroSerie + " não está cadastrado para nenhuma ECF", TipoDivergencia.ERRO.getId()));
                        ProgressBar.next();
                        continue;
                    }

                    long eanDiv = oItem.codigoBarras;

                    if (i_verificarCodAnterior) {
                        if ((String.valueOf(oItem.codigoAnterior).length() == 7)
                                && ("2".equals(String.valueOf(oItem.codigoAnterior).substring(0, 1)))) {
                            oItem.idProduto = new ProdutoDAO().getIdAnterior(
                                    Long.parseLong(String.valueOf(oItem.codigoAnterior).substring(0, 6)));
                        } else if ((String.valueOf(oItem.codigoAnterior).length() == 7)
                                && (!"2".equals(String.valueOf(oItem.codigoAnterior).substring(0, 1)))) {
                            oItem.idProduto = new ProdutoDAO().getId(oItem.codigoAnterior);
                        } else if ((String.valueOf(oItem.codigoAnterior).length() >= 7)
                                && ("2".equals(String.valueOf(oItem.codigoAnterior).substring(0, 1)))) {
                            oItem.idProduto = new ProdutoDAO().getIdAnterior2(oItem.codigoAnterior);
                            if (oItem.idProduto == -1) {
                                oItem.idProduto = new ProdutoDAO().getIdAnterior4(oItem.codigoAnterior);
                            }
                            if (oItem.idProduto == -1) {
                                oItem.idProduto = new ProdutoDAO().getIdAnterior3(
                                        Long.parseLong(String.valueOf(oItem.codigoAnterior).substring(
                                                        String.valueOf(oItem.codigoAnterior).length() - 11)));
                            }
                        } else if ((String.valueOf(oItem.codigoAnterior).length() >= 7)
                                && (!"2".equals(String.valueOf(oItem.codigoAnterior).substring(0, 1)))) {
                            oItem.idProduto = new ProdutoDAO().getId(oItem.codigoAnterior);
                        } else {
                            oItem.idProduto = new ProdutoDAO().getIdAnterior(oItem.codigoAnterior);
                            if (oItem.idProduto == -1) {
                                oItem.idProduto = new ProdutoDAO().getIdAnterior3(oItem.codigoAnterior);
                            }
                        }
                    } else if (i_verificarCodigoBarras) {
                        String strEan = String.valueOf(oItem.codigoBarras);
                        //JOptionPane.showConfirmDialog(null, "aqui");                                                
                        if (strEan.length() == 13) {
                            //Se iniciar com esse digito, significa que é código de balança
                            if (strEan.startsWith(String.valueOf(codigoInicio))) {
                                eanDiv = Long.parseLong(strEan.substring(1, codigoTamanho + 1));
                                System.out.println("EAN: " + oItem.codigoBarras + " TRATADO: " + eanDiv);
                            } else {
                                eanDiv = oItem.codigoBarras;
                            }
                        } else {
                            eanDiv = oItem.codigoBarras;
                        }
                        ProdutoMapa mp = mapa.get(tipo.toString(), String.valueOf(eanDiv));
                        if (mp != null && mp.getCodigoAtual() > 0) {
                            oItem.idProduto = mp.getCodigoAtual();
                            codigoBarrasAnterior = eanDiv;
                        } else {
                            oItem.idProduto = new ProdutoDAO().getIdCodigoBarrasAnterior(eanDiv, oItem.descricaoProduto.trim());
                            codigoBarrasAnterior = new ProdutoDAO().getIdCodigoBarrasAnterior2(eanDiv, oItem.descricaoProduto.trim());
                        }
                    } else {
                        //JOptionPane.showConfirmDialog(null, "aqui não");
                        oItem.idProduto = new ProdutoDAO().getId(oItem.codigoBarras);
                    }                  

                    if (oItem.idProduto == -1) {
                        if (i_exibeDivergenciaProduto) {
                            vDivergencia.add(new DivergenciaVO("Código de barras " + eanDiv + "Cód.Produto: " + oItem.idProduto + " descricao: " + oItem.descricaoProduto + " não cadastrado", TipoDivergencia.ERRO.getId()));
                            dao.armazenar(TipoMapa.EAN, String.valueOf(eanDiv), oItem.descricaoProduto);
                            ProgressBar.next();
                            continue;
                        } else {
                            oItem.idProduto = new ParametroPdvDAO().get(28).getInt();
                        }
                    }

                    if (codigoBarrasAnterior == -1) {
                        if (i_exibeDivergenciaProduto) {
                            vDivergencia.add(new DivergenciaVO("Código de barras " + eanDiv + "Cód.Produto: " + oItem.idProduto + " descricao: " + oItem.descricaoProduto + " não cadastrado", TipoDivergencia.ERRO.getId()));
                            dao.armazenar(TipoMapa.EAN, String.valueOf(eanDiv), oItem.descricaoProduto);
                            ProgressBar.next();
                            continue;
                        } else {
                            oItem.codigoBarras = new ParametroPdvDAO().get(28).getInt();
                        }
                    }

                    if (oItem.aliquota.trim().equals("I1")) {
                        oItem.idAliquota = new AliquotaDAO().getIdIsento();

                    } else if (oItem.aliquota.trim().equals("F1")) {
                        oItem.idAliquota = new AliquotaDAO().getIdSubstituido();

                    } else if (oItem.aliquota.trim().equals("N1")) {
                        oItem.idAliquota = new AliquotaDAO().getIdOutras();

                    } else if (oItem.aliquota.trim().equals("Can-T")) {
                        oItem.idAliquota = 0;
                        oItem.cancelado = true;

                    } else {
                        double percentual = Double.parseDouble(oItem.aliquota.substring(3, 7)) / 100;

                        oItem.idAliquota = new AliquotaDAO().getId(percentual);
                    }

                    boolean achou = false;

                    for (MapaResumoItemVO oMapaItem : oMapaResumo.vItem) {
                        if (oMapaItem.idAliquota == oItem.idAliquota) {
                            oMapaItem.valor += oItem.valorTotal - oItem.valorCancelado;

                            achou = true;
                        }

                    }

                    if (!achou) {
                        MapaResumoItemVO oMapaItem = new MapaResumoItemVO();
                        oMapaItem.idAliquota = oItem.idAliquota;
                        oMapaItem.valor = oItem.valorTotal - oItem.valorCancelado;

                        oMapaResumo.vItem.add(oMapaItem);

                    }

                    oItem.idVenda = new VendaPdvDAO().getId(oItem.numeroCupom, data, oItem.ecf);

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

                            if (i_verificarCodigoBarras) {
                                sql.append(codigoBarrasAnterior + ",");
                            } else {
                                sql.append(oItem.codigoBarras + ",");
                            }

                            sql.append("'" + oItem.unidadeMedida + "',");
                            sql.append("'" + oItem.totalizadorParcial + "')");

                            stm.execute(sql.toString());
                        }
                    }
                }

                if (linha.substring(1, 3).equals("16")) {
                    oMapaResumo.contadorCDC = Integer.parseInt(linha.substring(64, 68));
                    oMapaResumo.contadorGerencial = Integer.parseInt(linha.substring(58, 64));
                }

                if (linha.substring(1, 3).equals("21")) {
                    ImportacaoLogVendaFinalizadoraVO oFinalizadora = new ImportacaoLogVendaFinalizadoraVO();

                    oFinalizadora.numeroSerie = linha.substring(3, 23).trim();
                    oFinalizadora.numeroCupom = Integer.parseInt(linha.substring(46, 52));
                    oFinalizadora.finalizadora = linha.substring(64, 79).trim().toUpperCase();
                    oFinalizadora.valor = Double.parseDouble(linha.substring(79, 92)) / 100;
                    if (oMapaResumo.contadorInicial == 0) {
                        oMapaResumo.contadorInicial = Integer.parseInt(linha.substring(46, 52));
                        oMapaResumo.contadorFinal = Integer.parseInt(linha.substring(46, 52));

                    } else {
                        oMapaResumo.contadorFinal = Integer.parseInt(linha.substring(46, 52));

                    }

                    if (oMapaResumo.contadorInicial == 0) {
                        oMapaResumo.contadorInicial = oFinalizadora.numeroCupom;
                        oMapaResumo.contadorFinal = oFinalizadora.numeroCupom;

                    } else {
                        oMapaResumo.contadorFinal = oFinalizadora.numeroCupom;

                    }

                    oMapaResumo.valorContabil += Double.parseDouble(linha.substring(79, 92)) / 100;

                    oFinalizadora.ecf = new EcfDAO().get(oFinalizadora.numeroSerie, idLoja);

                    if (oFinalizadora.ecf == -1) {
                        vDivergencia.add(new DivergenciaVO("O numero de série " + oFinalizadora.numeroSerie + " não está cadastrado para nenhuma ECF", TipoDivergencia.ERRO.getId()));
                        ProgressBar.next();
                        continue;
                    }

                    rst = stm.executeQuery("SELECT id FROM pdv.finalizadora WHERE UPPER(descricao) LIKE '%" + oFinalizadora.finalizadora + "%'");

                    if (rst.next()) {
                        oFinalizadora.idFinalizadora = rst.getInt("id");
                    } else {
                        oFinalizadora.idFinalizadora = 1;
                    }

                    oFinalizadora.idVenda = new VendaPdvDAO().getId(oFinalizadora.numeroCupom, data, oFinalizadora.ecf);

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
                }

                ProgressBar.next();
            }

            if (oMapaResumo.ecf != -1 && !oMapaResumo.dataHoraEmissaoRz.isEmpty()) {
                new MapaResumoDAO().salvar(oMapaResumo);

            }

            arquivo.close();

            stm.close();

            verificarTroco(vIdVenda);
            ratearDescontoCupom(vIdDescontos);
            ratearAcrescimoCupom(vIdAcrescimo);
            salvarCupomCancelado(vIdCancelado);

            if (!vDivergencia.isEmpty()) {
                Conexao.rollback();
            } else {
                Conexao.commit();

                new LogTransacaoDAO().gerar(Formulario.INTERFACE_IMPORTACAO_LOGVENDA, TipoTransacao.IMPORTACAO, 0, i_arquivo.replace("\\", "\\\\"));
            }

            dao.gravar();

            return vDivergencia;

        } catch (Exception ex) {
            Conexao.rollback();

            if (l > 0) {
                throw new VRException("Linha " + l + ": " + ex.getMessage());
            } else {
                throw ex;
            }
        }
    }

    public ArrayList<DivergenciaVO> salvar(List<ImportacaoLogVendaVO> vendas, boolean i_exibeDivergenciaProduto,
            boolean i_verificarCodAnterior, boolean i_verificarCodigoBarras, int idLoja) throws Exception {
        StringBuilder sql = new StringBuilder();
        ResultSet rst = null;
        Statement stm = null;
        int l = 0;
        long codigoBarrasAnterior = -1;

        List<Integer> vIdCancelado = new ArrayList();
        List<Integer> vIdDescontos = new ArrayList();
        List<Integer> vIdAcrescimo = new ArrayList();
        List<Integer> vIdVenda = new ArrayList();

        ItensNaoExistentesController dao2 = new ItensNaoExistentesController();

        MultiMap<String, ProdutoMapa> mapa = new MultiMap<>();
        //TODO: Incluir mais opções            
        TipoMapa tipo = TipoMapa.EAN;
        for (ProdutoMapa mp : dao2.carregarMapa(true, tipo)) {
            mapa.put(mp, mp.getTipo().toString(), mp.getCodrfd());
        }

        try {
            Conexao.begin();

            int codigoInicio = 2, codigoTamanho = 6;

            try (Statement stm2 = Conexao.createStatement()) {
                stm2.execute(
                        "do $$\n"
                        + "declare\n"
                        + "	vid_loja integer = " + idLoja + ";\n"
                        + "	vutilizapeso boolean;\n"
                        + "	codigo_inicio integer;\n"
                        + "	codigo_tamanho integer;\n"
                        + "begin\n"
                        + "	select\n"
                        + "		valor::boolean\n"
                        + "	from\n"
                        + "		pdv.parametrovalor pv\n"
                        + "	where\n"
                        + "		id_parametro = 13\n"
                        + "		and id_loja = vid_loja\n"
                        + "	into\n"
                        + "		vutilizapeso;\n"
                        + "\n"
                        + "	select \n"
                        + "		case vutilizapeso when true then iniciopeso else iniciopreco end inicio,\n"
                        + "		case vutilizapeso when true then tamanhopeso else tamanhopreco end tamanho\n"
                        + "	from \n"
                        + "		pdv.balancaetiquetalayout\n"
                        + "	where \n"
                        + "		id_tipobalancoetiquetacampo = 0 \n"
                        + "		and id_loja = vid_loja\n"
                        + "	into\n"
                        + "		codigo_inicio,		\n"
                        + "		codigo_tamanho;\n"
                        + "\n"
                        + "	create temp table temp_parametros (\n"
                        + "		codigoinicio int, \n"
                        + "		codigotamanho int\n"
                        + "	) on commit drop;\n"
                        + "\n"
                        + "	insert into temp_parametros values (codigo_inicio, codigo_tamanho);\n"
                        + "\n"
                        + "end;$$;"
                );

                try (ResultSet rst2 = stm2.executeQuery(
                        "select * from temp_parametros;"
                )) {
                    while (rst2.next()) {
                        codigoInicio = rst2.getInt("codigoinicio");
                        codigoTamanho = rst2.getInt("codigotamanho");
                    }
                }
            }

            stm = Conexao.createStatement();

            ProgressBar.setStatus("Importando Venda...");
            ProgressBar.setMaximum(vendas.size());

            vDivergencia = new ArrayList();

            Map<Double, CodigoAnteriorVO> produtoCodigoAnterior = new CodigoAnteriorDAO().carregarCodigoAnterior();
            Map<Long, Long> clienteCodigoAnterior = new ClientePreferencialDAO().getCodigoAnterior(1);

            MultiMap<String, MapaResumoVO> mapasResumo = new MultiMap<>();

            for (ImportacaoLogVendaVO oVenda : vendas) {
                //<editor-fold defaultstate="collapsed" desc="INCLUSÃO DA VENDA">                   
                if ((oVenda.chavenfce != null
                        && !"".equals(oVenda.chavenfce)
                        && oVenda.chavenfce.length() == 44)) {
                    oVenda.numeroSerie = oVenda.chavenfce.substring(22, 31);
                    oVenda.modeloImpressora = "SAT-CF-e DIMEP";
                } else if ((oVenda.chavecfe != null
                        && !"".equals(oVenda.chavecfe)
                        && oVenda.chavecfe.length() == 44)) {
                    oVenda.numeroSerie = oVenda.chavecfe.substring(22, 31);
                    oVenda.modeloImpressora = "SAT-CF-e DIMEP";
                }

                oVenda.ecf = new EcfDAO().get(oVenda.numeroSerie, idLoja);

                if (oVenda.ecf == -1
                        && (oVenda.chavenfce == null || "".equals(oVenda.chavenfce))
                        && (oVenda.chavecfe == null || "".equals(oVenda.chavecfe))) {
                    vDivergencia.add(new DivergenciaVO("O numero de série " + oVenda.numeroSerie + " não está cadastrado para nenhuma ECF", TipoDivergencia.ERRO.getId()));
                    ProgressBar.next();
                    continue;
                }

                rst = stm.executeQuery("SELECT matricula FROM pdv.operador WHERE id_loja = " + idLoja + " AND id_situacaocadastro = " + SituacaoCadastro.ATIVO.getId() + " LIMIT 1");

                if (rst.next()) {
                    oVenda.matricula = rst.getInt("matricula");
                } else {
                    vDivergencia.add(new DivergenciaVO("Nenhum operador cadastrado para esta loja", TipoDivergencia.ERRO.getId()));
                    ProgressBar.next();
                    continue;
                }

                oVenda.id = new VendaPdvDAO().getId(oVenda.numeroCupom, oVenda.data, oVenda.ecf);

                if (oVenda.id != -1) {
                    if (Database.tabelaExiste("parana.vendaoperadorbalanca")) {
                        stm.execute("DELETE FROM parana.vendaoperadorbalanca WHERE id_vendaitem IN (SELECT id FROM pdv.vendaitem WHERE id_venda = " + oVenda.id + ")");
                    }

                    stm.execute("DELETE FROM pdv.vendakititem WHERE id_vendakit IN (SELECT id FROM pdv.vendakit WHERE id_venda = " + oVenda.id + ")");
                    stm.execute("DELETE FROM pdv.vendakit WHERE id_venda = " + oVenda.id);
                    stm.execute("DELETE FROM pdv.vendapromocao WHERE id_venda = " + oVenda.id);
                    stm.execute("DELETE FROM pdv.vendapromocaocupom WHERE id_venda = " + oVenda.id);
                    stm.execute("DELETE FROM pdv.vendafinalizadora WHERE id_venda = " + oVenda.id);
                    stm.execute("DELETE FROM pdv.vendaitem WHERE id_venda = " + oVenda.id);
                    stm.execute("DELETE FROM pdv.venda WHERE id = " + oVenda.id);
                }

                sql = new StringBuilder();
                sql.append("INSERT INTO pdv.venda (id_loja, numerocupom, ecf, data, id_clientepreferencial, matricula, horainicio, horatermino, cancelado,");
                sql.append(" subtotalimpressora, matriculacancelamento, id_tipocancelamento, canceladoemvenda, contadordoc, cpf, valordesconto, valoracrescimo, numeroserie, ");
                sql.append(" mfadicional, modeloimpressora, numerousuario, nomecliente, enderecocliente, chavecfe, chavenfce, xml, cpfcrm, cpfcnpjentidade, razaosocialentidade)");
                sql.append(" VALUES (");
                //sql.append(Global.idLoja + ",");
                sql.append(idLoja + ",");
                sql.append(oVenda.numeroCupom + ",");
                sql.append(oVenda.ecf + ",");
                sql.append("'" + Util.formatDataBanco(oVenda.data) + "',");
                Long idCliente = clienteCodigoAnterior.get(oVenda.idClientePreferencial);
                sql.append(idCliente != null ? Utils.longIntSQL(idCliente, 0) : null + ",");
                sql.append(oVenda.matricula + ",");
                sql.append("'" + Util.formatDataHoraBanco(oVenda.data + " " + Util.getHoraAtual()) + "',");
                sql.append("'" + Util.formatDataHoraBanco(oVenda.data + " " + Util.getHoraAtual()) + "',");
                sql.append(oVenda.cancelado + ",");
                sql.append(oVenda.subtotalImpressora + ",");
                sql.append(Utils.longIntSQL(oVenda.matriculacancelamento, 0) + ",");
                sql.append(Utils.longIntSQL(oVenda.id_tipocancelamento, 0) + ",");
                sql.append(oVenda.canceladoEmVenda + ",");
                sql.append(oVenda.contadorDoc + ",");
                sql.append(oVenda.cpf + ",");
                sql.append(oVenda.valorDesconto + ",");
                sql.append(oVenda.valorAcrescimo + ",");
                sql.append(Utils.quoteSQL(oVenda.numeroSerie != null ? oVenda.numeroSerie.trim() : null) + ",");
                sql.append(oVenda.mfadicional + ",");
                sql.append(Utils.quoteSQL(oVenda.modeloImpressora != null ? oVenda.modeloImpressora.trim() : null) + ",");
                sql.append(oVenda.numeroUsuario + ",");
                sql.append(Utils.quoteSQL(Utils.acertarTexto(oVenda.nomeCliente, 45)) + ","); //nomecliente
                sql.append(Utils.quoteSQL(Utils.acertarTexto(oVenda.enderecoCliente, 50)) + ","); //end cliente
                sql.append(Utils.quoteSQL(oVenda.chavecfe) + ",");
                sql.append(Utils.quoteSQL(oVenda.chavenfce) + ",");
                sql.append(Utils.quoteSQL(oVenda.xml) + ",");
                sql.append("0,");
                sql.append("0,");
                sql.append("'') returning id");

                try (ResultSet rst2 = stm.executeQuery(sql.toString())) {
                    if (rst2.next()) {
                        oVenda.id = rst2.getInt("id");
                    }
                }

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
                //</editor-fold>

                for (ImportacaoLogVendaItemVO oItem : oVenda.vLogVendaItem) {

                    oItem.ecf = oVenda.ecf;

                    if (oItem.ecf == -1
                            && (oVenda.chavenfce == null || "".equals(oVenda.chavenfce))
                            && (oVenda.chavecfe == null || "".equals(oVenda.chavecfe))) {
                        vDivergencia.add(new DivergenciaVO("O numero de série " + oItem.numeroSerie + " não está cadastrado para nenhuma ECF", TipoDivergencia.ERRO.getId()));
                        ProgressBar.next();
                        continue;
                    }

                    long eanDiv = oItem.codigoBarras;

                    if (i_verificarCodAnterior) {
                        oItem.idProduto = (int) produtoCodigoAnterior.get((double) oItem.codigoAnterior).getCodigoatual();
                    } else if (i_verificarCodigoBarras) {

                        String strEan = String.valueOf(oItem.codigoBarras);
                        //JOptionPane.showConfirmDialog(null, "aqui");                                                
                        if (strEan.length() == 13) {
                            //Se iniciar com esse digito, significa que é código de balança
                            if (strEan.startsWith(String.valueOf(codigoInicio))) {
                                eanDiv = Long.parseLong(strEan.substring(1, codigoTamanho + 1));
                                System.out.println("EAN: " + oItem.codigoBarras + " TRATADO: " + eanDiv);
                            } else {
                                eanDiv = oItem.codigoBarras;
                            }
                        } else {
                            eanDiv = oItem.codigoBarras;
                        }

                        ProdutoMapa mp = mapa.get(tipo.toString(), String.valueOf(eanDiv));
                        if (mp != null && mp.getCodigoAtual() > 0) {
                            oItem.idProduto = mp.getCodigoAtual();
                            codigoBarrasAnterior = eanDiv;
                        } else {
                            //Se o código de barras for menor que 7 dígito, faz a busca por descrição
                            if (strEan.length() < 7) {
                                eanDiv = -999;
                            }
                            oItem.idProduto = new ProdutoDAO().getIdCodigoBarrasAnterior(eanDiv, oItem.descricaoProduto.trim());
                            codigoBarrasAnterior = new ProdutoDAO().getIdCodigoBarrasAnterior2(eanDiv, oItem.descricaoProduto.trim());
                        }
                    } else {
                        oItem.idProduto = new ProdutoDAO().getId(oItem.codigoBarras);
                    }

                    if (oItem.idProduto == -1) {
                        if (i_exibeDivergenciaProduto) {
                            vDivergencia.add(new DivergenciaVO("Código de barras " + eanDiv + " Cód.Produto: " + oItem.idProduto + " descricao: " + oItem.descricaoProduto + " não cadastrado", TipoDivergencia.ERRO.getId()));
                            dao2.armazenar(TipoMapa.EAN, String.valueOf(eanDiv), oItem.descricaoProduto);
                            ProgressBar.next();
                            continue;
                        } else {
                            oItem.idProduto = new ParametroPdvDAO().get(28).getInt();
                        }
                    }

                    if (!"".equals(Utils.acertarTexto(oItem.aliquota))) {
                        if (oItem.aliquota.trim().equals("I1")) {
                            oItem.idAliquota = new AliquotaDAO().getIdIsento();

                        } else if (oItem.aliquota.trim().equals("F1")) {
                            oItem.idAliquota = new AliquotaDAO().getIdSubstituido();

                        } else if (oItem.aliquota.trim().equals("N1")) {
                            oItem.idAliquota = new AliquotaDAO().getIdOutras();

                        } else if (oItem.aliquota.trim().equals("Can-T")) {
                            oItem.idAliquota = 0;
                            oItem.cancelado = true;

                        } else {
                            double percentual = Double.parseDouble(oItem.aliquota.substring(3, 7)) / 100;

                            oItem.idAliquota = new AliquotaDAO().getId(percentual);
                        }
                    }

                    oItem.idVenda = oVenda.id; //new VendaPdvDAO().getId(oItem.numeroCupom, oVenda.data, oItem.ecf);

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
                            sql.append(" regracalculo, codigobarras, unidademedida, totalizadorparcial, id_tipocancelamento, matriculacancelamento,");
                            sql.append(" valordescontopromocao, oferta)");
                            sql.append(" VALUES (");
                            sql.append(oVenda.id + ",");
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

                            if (i_verificarCodigoBarras) {
                                sql.append(codigoBarrasAnterior + ",");
                            } else {
                                sql.append(oItem.codigoBarras + ",");
                            }

                            sql.append("'" + oItem.unidadeMedida + "',");
                            sql.append("'" + oItem.totalizadorParcial + "',");
                            sql.append(Utils.longIntSQL(oItem.idTipoCancelamento, 0) + ",");
                            sql.append(Utils.longIntSQL(oItem.matriculaCancelamento, 0) + ",");
                            sql.append("0,");
                            sql.append("false)");

                            stm.execute(sql.toString());
                        }
                    }
                }

                preparaMapaResumo(idLoja, oVenda, mapasResumo);

                ProgressBar.next();
            }

            MapaResumoDAO dao = new MapaResumoDAO();
            for (MapaResumoVO mapa2 : mapasResumo.values()) {
                dao.salvar(mapa2);
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

                new LogTransacaoDAO().gerar(Formulario.INTERFACE_IMPORTACAO_LOGVENDA, TipoTransacao.IMPORTACAO, 0, "");
            }

            return vDivergencia;

        } catch (Exception ex) {
            Conexao.rollback();

            if (l > 0) {
                throw new VRException("Linha " + l + ": " + ex.getMessage());
            } else {
                throw ex;
            }
        }
    }

    private void preparaMapaResumo(int idLojaVR, ImportacaoLogVendaVO oVenda, MultiMap<String, MapaResumoVO> mapasResumo) {
        //Obter o mapa resumo do dia e daquela ecf
        MapaResumoVO mapa = mapasResumo.get(String.valueOf(idLojaVR), String.valueOf(oVenda.ecf), oVenda.data);
        if (mapa == null) {
            mapa = new MapaResumoVO();
            mapasResumo.put(mapa, String.valueOf(idLojaVR), String.valueOf(oVenda.ecf), oVenda.data);
            mapa.setEcf(oVenda.ecf);
            mapa.setContadorReinicio(1);
            mapa.setReducao(0);
            mapa.setData(oVenda.data);
            mapa.setDataHoraEmissaoRz(oVenda.data + " 22:00:00");
            mapa.setContadorGerencial(0);
            mapa.setContadorCDC(0);
            mapa.setTotalNaoFiscal(0);
            mapa.setGtInicial(0);
            mapa.setGtFinal(0);
            mapa.setCancelamento(0);
            mapa.setDesconto(0);
            mapa.setAcrescimo(0);
            mapa.setValorContabil(0);
            mapa.setLancamentoManual(false);
        }
        //Acerta o contador
        if (oVenda.numeroCupom < mapa.getContadorInicial() || mapa.getContadorInicial() == 0) {
            mapa.setContadorInicial(oVenda.numeroCupom);
        }
        if (oVenda.numeroCupom > mapa.getContadorFinal()) {
            mapa.setContadorFinal(oVenda.numeroCupom);
        }

        mapa.setGtFinal(mapa.getGtFinal() + oVenda.getTotalItens());
        mapa.setCancelamento(mapa.getCancelamento() + oVenda.getTotalCancelado());
        mapa.setAcrescimo(mapa.getAcrescimo() + oVenda.getTotalAcrescimo());
        mapa.setDesconto(mapa.getDesconto() + oVenda.getTotalDesconto());
        mapa.calcularValorContabil();

        //Preencho os valores das aliquotas        
        //Organizando as aliquotas
        Map<Integer, MapaResumoItemVO> aliquotas = new LinkedHashMap<>();
        for (MapaResumoItemVO aliq : mapa.getvItem()) {
            aliquotas.put(aliq.idAliquota, aliq);
        }
        for (ImportacaoLogVendaItemVO item : oVenda.vLogVendaItem) {
            MapaResumoItemVO aliq = aliquotas.get(item.idAliquota);
            if (aliq == null) {
                aliq = new MapaResumoItemVO();
                aliq.idAliquota = item.idAliquota;
                //Inclui a aliquota nas listagens
                aliquotas.put(aliq.idAliquota, aliq);
                mapa.getvItem().add(aliq);
            }

            aliq.valor += item.valorTotal - item.valorCancelado;
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

    public ArrayList<DivergenciaVO> alterar(String i_arquivo, boolean i_exibeDivergenciaProduto,
            boolean i_verificarCodAnterior, boolean i_verificarCodigoBarras, int idLoja) throws Exception {
        StringBuilder sql = new StringBuilder();
        ResultSet rst = null;
        Statement stm = null;
        int l = 0;
        long codigoBarrasAnterior;

        List<Integer> vIdCancelado = new ArrayList();
        List<Integer> vIdDescontos = new ArrayList();
        List<Integer> vIdAcrescimo = new ArrayList();
        List<Integer> vIdVenda = new ArrayList();

        try {
            Conexao.begin();
            stm = Conexao.createStatement();

            ProgressBar.setStatus("Importando Log Venda...");

            Arquivo arquivo = new Arquivo(i_arquivo, "r", "UTF-8");

            ProgressBar.setMaximum(arquivo.getLineCount());

            String linha = "";
            String data = "";

            vDivergencia = new ArrayList();
            MapaResumoVO oMapaResumo = new MapaResumoVO();

            ItensNaoExistentesController dao = new ItensNaoExistentesController();

            MultiMap<String, ProdutoMapa> mapa = new MultiMap<>();
            //TODO: Incluir mais opções            
            TipoMapa tipo = TipoMapa.EAN;
            for (ProdutoMapa mp : dao.carregarMapa(true, tipo)) {
                mapa.put(mp, mp.getTipo().toString(), mp.getCodrfd());
            }

            int codigoInicio = 2, codigoTamanho = 6;

            try (Statement stm2 = Conexao.createStatement()) {
                stm2.execute(
                        "do $$\n"
                        + "declare\n"
                        + "	vid_loja integer = " + idLoja + ";\n"
                        + "	vutilizapeso boolean;\n"
                        + "	codigo_inicio integer;\n"
                        + "	codigo_tamanho integer;\n"
                        + "begin\n"
                        + "	select\n"
                        + "		valor::boolean\n"
                        + "	from\n"
                        + "		pdv.parametrovalor pv\n"
                        + "	where\n"
                        + "		id_parametro = 13\n"
                        + "		and id_loja = vid_loja\n"
                        + "	into\n"
                        + "		vutilizapeso;\n"
                        + "\n"
                        + "	select \n"
                        + "		case vutilizapeso when true then iniciopeso else iniciopreco end inicio,\n"
                        + "		case vutilizapeso when true then tamanhopeso else tamanhopreco end tamanho\n"
                        + "	from \n"
                        + "		pdv.balancaetiquetalayout\n"
                        + "	where \n"
                        + "		id_tipobalancoetiquetacampo = 0 \n"
                        + "		and id_loja = vid_loja\n"
                        + "	into\n"
                        + "		codigo_inicio,		\n"
                        + "		codigo_tamanho;\n"
                        + "\n"
                        + "	create temp table temp_parametros (\n"
                        + "		codigoinicio int, \n"
                        + "		codigotamanho int\n"
                        + "	) on commit drop;\n"
                        + "\n"
                        + "	insert into temp_parametros values (codigo_inicio, codigo_tamanho);\n"
                        + "\n"
                        + "end;$$;"
                );

                try (ResultSet rst2 = stm2.executeQuery(
                        "select * from temp_parametros;"
                )) {
                    while (rst2.next()) {
                        codigoInicio = rst2.getInt("codigoinicio");
                        codigoTamanho = rst2.getInt("codigotamanho");
                    }
                }
            }

            while (arquivo.ready()) {
                linha = arquivo.readLine();
                l++;

                codigoBarrasAnterior = -1;
                String modeloImpressora = "";

                if (linha.substring(1, 3).equals("01")) {
                    modeloImpressora = linha.substring(51, 71).trim();
                }

                if (linha.substring(1, 3).equals("02")) {
                    oMapaResumo.gtFinal = Double.parseDouble(linha.substring(252, 270)) / 100;
                    oMapaResumo.gtInicial = oMapaResumo.gtFinal;
                    oMapaResumo.contadorReinicio = Integer.parseInt(linha.substring(246, 252));
                }

                if (linha.substring(1, 3).equals("12")) {
                    oMapaResumo.dataHoraEmissaoRz = Util.formatData(linha.substring(72, 80), "yyyyMMdd", "dd/MM/yyyy") + " " + Util.formatData(linha.substring(80, 86), "hhmmss", "hh:mm:ss");
                }

                if (linha.substring(1, 3).equals("13")) {
                    oMapaResumo.reducao = Integer.parseInt(linha.substring(46, 52));

                }

                if (linha.substring(1, 3).equals("14")) {
                    ImportacaoLogVendaVO oVenda = new ImportacaoLogVendaVO();
                    oVenda.numeroCupom = Integer.parseInt(linha.substring(52, 58).trim());
                    oVenda.data = Util.formatData(linha.substring(58, 66), "yyyyMMdd", "dd/MM/yyyy");
                    oVenda.numeroSerie = linha.substring(3, 23).trim();
                    oVenda.cancelado = linha.substring(122, 123).equals("N") ? false : true;

                    oVenda.valorDesconto = Double.parseDouble(linha.substring(82, 93)) / 100;
                    oVenda.valorAcrescimo = Double.parseDouble(linha.substring(94, 107)) / 100;

                    oVenda.subtotalImpressora = Double.parseDouble(linha.substring(108, 122)) / 100;
                    oVenda.cpf = Long.parseLong(linha.substring(177, 191));
                    oVenda.modeloImpressora = modeloImpressora;

                    data = oVenda.data;

                    oVenda.ecf = new EcfDAO().get(oVenda.numeroSerie, idLoja);

                    oMapaResumo.ecf = oVenda.ecf;

                    oMapaResumo.data = oVenda.data;

                    oMapaResumo.desconto += oVenda.valorDesconto;
                    oMapaResumo.acrescimo += oVenda.valorAcrescimo;

                    oMapaResumo.totalNaoFiscal = 0;
                    oMapaResumo.lancamentoManual = false;

                    if (oVenda.ecf == -1) {
                        vDivergencia.add(new DivergenciaVO("O numero de série " + oVenda.numeroSerie + " não está cadastrado para nenhuma ECF", TipoDivergencia.ERRO.getId()));
                        ProgressBar.next();
                        continue;
                    }

                    rst = stm.executeQuery("SELECT matricula FROM pdv.operador WHERE id_loja = " + idLoja + " AND id_situacaocadastro = " + SituacaoCadastro.ATIVO.getId() + " LIMIT 1");

                    if (rst.next()) {
                        oVenda.matricula = rst.getInt("matricula");
                    } else {
                        vDivergencia.add(new DivergenciaVO("Nenhum operador cadastrado para esta loja", TipoDivergencia.ERRO.getId()));
                        ProgressBar.next();
                        continue;
                    }

                    oVenda.id = new VendaPdvDAO().getId(oVenda.numeroCupom, oVenda.data, oVenda.ecf);

                    if (oVenda.id != -1) {
                        if (Database.tabelaExiste("parana.vendaoperadorbalanca")) {
                            stm.execute("DELETE FROM parana.vendaoperadorbalanca WHERE id_vendaitem IN (SELECT id FROM pdv.vendaitem WHERE id_venda = " + oVenda.id + ")");
                        }

                        stm.execute("DELETE FROM pdv.vendakititem WHERE id_vendakit IN (SELECT id FROM pdv.vendakit WHERE id_venda = " + oVenda.id + ")");
                        stm.execute("DELETE FROM pdv.vendakit WHERE id_venda = " + oVenda.id);
                        stm.execute("DELETE FROM pdv.vendapromocao WHERE id_venda = " + oVenda.id);
                        stm.execute("DELETE FROM pdv.vendapromocaocupom WHERE id_venda = " + oVenda.id);
                        stm.execute("DELETE FROM pdv.vendafinalizadora WHERE id_venda = " + oVenda.id);
                        stm.execute("DELETE FROM pdv.vendaitem WHERE id_venda = " + oVenda.id);
                        stm.execute("DELETE FROM pdv.venda WHERE id = " + oVenda.id);
                    }

                    /*sql = new StringBuilder();
                     sql.append("INSERT INTO pdv.venda (id_loja, numerocupom, ecf, data, matricula, horainicio, horatermino, cancelado,");
                     sql.append(" subtotalimpressora, canceladoemvenda, contadordoc, cpf, valordesconto, valoracrescimo, numeroserie, ");
                     sql.append(" mfadicional, modeloimpressora, numerousuario, nomecliente, enderecocliente)");
                     sql.append(" VALUES (");
                     //sql.append(Global.idLoja + ",");
                     sql.append(idLoja + ",");
                     sql.append(oVenda.numeroCupom + ",");
                     sql.append(oVenda.ecf + ",");
                     sql.append("'" + Util.formatDataBanco(oVenda.data) + "',");
                     sql.append(oVenda.matricula + ",");
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

                     vIdVenda.add(oVenda.id);*/
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
                }

                if (linha.substring(1, 3).equals("15")) {
                    ImportacaoLogVendaItemVO oItem = new ImportacaoLogVendaItemVO();

                    //verifica casas decimais
                    oItem.casasDecimaisQuantidade = Integer.parseInt(linha.substring(265, 266));
                    oItem.casasDecimaisValor = Integer.parseInt(linha.substring(266, 267));

                    oItem.numeroSerie = linha.substring(3, 23).trim();
                    oItem.numeroCupom = Integer.parseInt(linha.substring(46, 52));
                    oItem.contadorDoc = Integer.parseInt(linha.substring(52, 58));
                    oItem.sequencia = Integer.parseInt(linha.substring(58, 61));

                    if (i_verificarCodAnterior) {
                        oItem.codigoAnterior = Long.parseLong(linha.substring(61, 75).trim());
                    } else if (i_verificarCodigoBarras) {
                        oItem.codigoBarras = Long.parseLong(linha.substring(61, 75).trim());
                        oItem.descricaoProduto = Utils.acertarTexto(linha.substring(75, 175).trim());
                    } else {
                        oItem.codigoBarras = Long.parseLong(linha.substring(61, 75).trim());
                    }

                    /*if (!i_verificarCodAnterior) {
                     oItem.codigoBarras = Long.parseLong(linha.substring(61, 75).trim());
                     } else {
                     oItem.codigoAnterior = Long.parseLong(linha.substring(61, 75).trim());
                     }*/
                    if (oItem.codigoAnterior == Long.parseLong("20021200510052")) {
                        System.out.println(oItem.codigoAnterior);
                    }

                    oItem.quantidade = Double.parseDouble(linha.substring(175, 182)) / Math.pow(10, oItem.casasDecimaisQuantidade);

                    oItem.precoVenda = Double.parseDouble(linha.substring(185, 193)) / Math.pow(10, oItem.casasDecimaisValor);
                    oItem.valorDesconto = Double.parseDouble(linha.substring(193, 201)) / Math.pow(10, oItem.casasDecimaisValor);
                    oItem.valorAcrescimo = Double.parseDouble(linha.substring(201, 209)) / Math.pow(10, oItem.casasDecimaisValor);

                    oItem.valorTotal = (Double.parseDouble(linha.substring(209, 223)) / 100) + oItem.valorDesconto - oItem.valorAcrescimo;
                    oItem.aliquota = linha.substring(223, 230);

                    if (linha.substring(230, 231).toUpperCase().equals("S")) {
                        oItem.cancelado = true;
                        oItem.valorCancelado = oItem.valorTotal;
                        oMapaResumo.cancelamento += oItem.valorTotal;
                    }

                    oMapaResumo.gtInicial -= oItem.valorTotal;

                    oItem.ecf = new EcfDAO().get(oItem.numeroSerie, idLoja);

                    if (oItem.ecf == -1) {
                        vDivergencia.add(new DivergenciaVO("O numero de série " + oItem.numeroSerie + " não está cadastrado para nenhuma ECF", TipoDivergencia.ERRO.getId()));
                        ProgressBar.next();
                        continue;
                    }

                    long eanDiv = oItem.codigoBarras;

                    if (i_verificarCodAnterior) {
                        if ((String.valueOf(oItem.codigoAnterior).length() == 7)
                                && ("2".equals(String.valueOf(oItem.codigoAnterior).substring(0, 1)))) {

                            oItem.idProduto = new ProdutoDAO().getIdAnterior(
                                    Long.parseLong(String.valueOf(oItem.codigoAnterior).substring(0, 6)));
                        } else if ((String.valueOf(oItem.codigoAnterior).length() == 7)
                                && (!"2".equals(String.valueOf(oItem.codigoAnterior).substring(0, 1)))) {

                            oItem.idProduto = new ProdutoDAO().getId(oItem.codigoAnterior);
                        } else if ((String.valueOf(oItem.codigoAnterior).length() >= 7)
                                && ("2".equals(String.valueOf(oItem.codigoAnterior).substring(0, 1)))) {

                            oItem.idProduto = new ProdutoDAO().getIdAnterior2(oItem.codigoAnterior);

                            if (oItem.idProduto == -1) {
                                oItem.idProduto = new ProdutoDAO().getIdAnterior4(oItem.codigoAnterior);
                            }

                            if (oItem.idProduto == -1) {
                                oItem.idProduto = new ProdutoDAO().getIdAnterior3(
                                        Long.parseLong(String.valueOf(oItem.codigoAnterior).substring(
                                                        String.valueOf(oItem.codigoAnterior).length() - 11)));
                            }

                        } else if ((String.valueOf(oItem.codigoAnterior).length() >= 7)
                                && (!"2".equals(String.valueOf(oItem.codigoAnterior).substring(0, 1)))) {

                            oItem.idProduto = new ProdutoDAO().getId(oItem.codigoAnterior);
                        } else {

                            oItem.idProduto = new ProdutoDAO().getIdAnterior(oItem.codigoAnterior);

                            if (oItem.idProduto == -1) {
                                oItem.idProduto = new ProdutoDAO().getIdAnterior3(oItem.codigoAnterior);
                            }
                        }
                    } else if (i_verificarCodigoBarras) {
                        String strEan = String.valueOf(oItem.codigoBarras);
                        //JOptionPane.showConfirmDialog(null, "aqui");                                                
                        if (strEan.length() == 13) {
                            //Se iniciar com esse digito, significa que é código de balança
                            if (strEan.startsWith(String.valueOf(codigoInicio))) {
                                eanDiv = Long.parseLong(strEan.substring(1, codigoTamanho + 1));
                                System.out.println("EAN: " + oItem.codigoBarras + " TRATADO: " + eanDiv);
                            } else {
                                eanDiv = oItem.codigoBarras;
                            }
                        } else {
                            eanDiv = oItem.codigoBarras;
                        }
                        ProdutoMapa mp = mapa.get(tipo.toString(), String.valueOf(eanDiv));
                        if (mp != null && mp.getCodigoAtual() > 0) {
                            oItem.idProduto = mp.getCodigoAtual();
                            codigoBarrasAnterior = eanDiv;
                        } else {
                            oItem.idProduto = new ProdutoDAO().getIdCodigoBarrasAnterior(eanDiv, oItem.descricaoProduto.trim());
                            codigoBarrasAnterior = new ProdutoDAO().getIdCodigoBarrasAnterior2(eanDiv, oItem.descricaoProduto.trim());
                        }
                    } else {
                        //JOptionPane.showConfirmDialog(null, "aqui não");
                        oItem.idProduto = new ProdutoDAO().getId(oItem.codigoBarras);
                    }

                    if (oItem.idProduto == -1) {
                        if (i_exibeDivergenciaProduto) {
                            vDivergencia.add(new DivergenciaVO("Código de barras " + eanDiv + "Cód.Produto: " + oItem.idProduto + " descricao: " + oItem.descricaoProduto + " não cadastrado", TipoDivergencia.ERRO.getId()));
                            dao.armazenar(TipoMapa.EAN, String.valueOf(eanDiv), oItem.descricaoProduto);
                            ProgressBar.next();
                            continue;
                        } else {
                            oItem.idProduto = new ParametroPdvDAO().get(28).getInt();
                        }
                    }

                    if (codigoBarrasAnterior == -1) {
                        if (i_exibeDivergenciaProduto) {
                            vDivergencia.add(new DivergenciaVO("Código de barras " + eanDiv + "Cód.Produto: " + oItem.idProduto + " descricao: " + oItem.descricaoProduto + " não cadastrado", TipoDivergencia.ERRO.getId()));
                            dao.armazenar(TipoMapa.EAN, String.valueOf(eanDiv), oItem.descricaoProduto);
                            ProgressBar.next();
                            continue;
                        } else {
                            oItem.codigoBarras = new ParametroPdvDAO().get(28).getInt();
                        }
                    }

                    if (oItem.aliquota.trim().equals("I1")) {
                        oItem.idAliquota = new AliquotaDAO().getIdIsento();

                    } else if (oItem.aliquota.trim().equals("F1")) {
                        oItem.idAliquota = new AliquotaDAO().getIdSubstituido();

                    } else if (oItem.aliquota.trim().equals("N1")) {
                        oItem.idAliquota = new AliquotaDAO().getIdOutras();

                    } else if (oItem.aliquota.trim().equals("Can-T")) {
                        oItem.idAliquota = 0;
                        oItem.cancelado = true;

                    } else {
                        double percentual = Double.parseDouble(oItem.aliquota.substring(3, 7)) / 100;

                        oItem.idAliquota = new AliquotaDAO().getId(percentual);
                    }

                    boolean achou = false;

                    for (MapaResumoItemVO oMapaItem : oMapaResumo.vItem) {
                        if (oMapaItem.idAliquota == oItem.idAliquota) {
                            oMapaItem.valor += oItem.valorTotal - oItem.valorCancelado;

                            achou = true;
                        }

                    }

                    if (!achou) {
                        MapaResumoItemVO oMapaItem = new MapaResumoItemVO();
                        oMapaItem.idAliquota = oItem.idAliquota;
                        oMapaItem.valor = oItem.valorTotal - oItem.valorCancelado;

                        oMapaResumo.vItem.add(oMapaItem);

                    }

                    oItem.idVenda = new VendaPdvDAO().getId(oItem.numeroCupom, data, oItem.ecf);

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

                            if (i_verificarCodigoBarras) {
                                sql.append(codigoBarrasAnterior + ",");
                            } else {
                                sql.append(oItem.codigoBarras + ",");
                            }

                            sql.append("'" + oItem.unidadeMedida + "',");
                            sql.append("'" + oItem.totalizadorParcial + "')");

                            stm.execute(sql.toString());
                        }
                    }
                }

                if (linha.substring(1, 3).equals("16")) {
                    oMapaResumo.contadorCDC = Integer.parseInt(linha.substring(64, 68));
                    oMapaResumo.contadorGerencial = Integer.parseInt(linha.substring(58, 64));
                }

                if (linha.substring(1, 3).equals("21")) {
                    ImportacaoLogVendaFinalizadoraVO oFinalizadora = new ImportacaoLogVendaFinalizadoraVO();

                    oFinalizadora.numeroSerie = linha.substring(3, 23).trim();
                    oFinalizadora.numeroCupom = Integer.parseInt(linha.substring(46, 52));
                    oFinalizadora.finalizadora = linha.substring(64, 79).trim().toUpperCase();
                    oFinalizadora.valor = Double.parseDouble(linha.substring(79, 92)) / 100;
                    if (oMapaResumo.contadorInicial == 0) {
                        oMapaResumo.contadorInicial = Integer.parseInt(linha.substring(46, 52));
                        oMapaResumo.contadorFinal = Integer.parseInt(linha.substring(46, 52));

                    } else {
                        oMapaResumo.contadorFinal = Integer.parseInt(linha.substring(46, 52));

                    }

                    if (oMapaResumo.contadorInicial == 0) {
                        oMapaResumo.contadorInicial = oFinalizadora.numeroCupom;
                        oMapaResumo.contadorFinal = oFinalizadora.numeroCupom;

                    } else {
                        oMapaResumo.contadorFinal = oFinalizadora.numeroCupom;

                    }

                    oMapaResumo.valorContabil += Double.parseDouble(linha.substring(79, 92)) / 100;

                    oFinalizadora.ecf = new EcfDAO().get(oFinalizadora.numeroSerie, idLoja);

                    if (oFinalizadora.ecf == -1) {
                        vDivergencia.add(new DivergenciaVO("O numero de série " + oFinalizadora.numeroSerie + " não está cadastrado para nenhuma ECF", TipoDivergencia.ERRO.getId()));
                        ProgressBar.next();
                        continue;
                    }

                    rst = stm.executeQuery("SELECT id FROM pdv.finalizadora WHERE UPPER(descricao) LIKE '%" + oFinalizadora.finalizadora + "%'");

                    if (rst.next()) {
                        oFinalizadora.idFinalizadora = rst.getInt("id");
                    } else {
                        oFinalizadora.idFinalizadora = 1;
                    }

                    oFinalizadora.idVenda = new VendaPdvDAO().getId(oFinalizadora.numeroCupom, data, oFinalizadora.ecf);

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
                }

                ProgressBar.next();
            }

            if (oMapaResumo.ecf != -1 && !oMapaResumo.dataHoraEmissaoRz.isEmpty()) {
                new MapaResumoDAO().salvar(oMapaResumo);

            }

            arquivo.close();

            stm.close();

            verificarTroco(vIdVenda);
            ratearDescontoCupom(vIdDescontos);
            ratearAcrescimoCupom(vIdAcrescimo);
            salvarCupomCancelado(vIdCancelado);

            if (!vDivergencia.isEmpty()) {
                Conexao.rollback();
            } else {
                Conexao.commit();

                new LogTransacaoDAO().gerar(Formulario.INTERFACE_IMPORTACAO_LOGVENDA, TipoTransacao.IMPORTACAO, 0, i_arquivo.replace("\\", "\\\\"));
            }

            dao.gravar();

            return vDivergencia;

        } catch (Exception ex) {
            Conexao.rollback();

            if (l > 0) {
                throw new VRException("Linha " + l + ": " + ex.getMessage());
            } else {
                throw ex;
            }
        }
    }
}
