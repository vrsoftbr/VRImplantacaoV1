/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao.dao.cadastro;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao.dao.CodigoInternoDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.CodigoAnteriorVO;
import vrimplantacao.vo.vrimplantacao.NutricionalFilizolaItemVO;
import vrimplantacao.vo.vrimplantacao.NutricionalFilizolaVO;
import vrimplantacao2.dao.cadastro.produto.ProdutoAnteriorDAO;
import vrimplantacao2.utils.arquivo.LinhaArquivo;
import vrimplantacao2.utils.arquivo.delimited.ArquivoTXT;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.cadastro.ProdutoAnteriorVO;

public class NutricionalFilizolaDAO {

    private static final Logger LOG = Logger.getLogger(NutricionalFilizolaDAO.class.getName());
    Utils util = new Utils();

    public List<NutricionalFilizolaVO> getNutricionalFilizola(String arquivo) throws Exception {
        List<NutricionalFilizolaVO> result = new ArrayList<>();
        List<String> vFilizola = util.lerArquivoBalanca(arquivo);

        for (int i = 0; i < vFilizola.size(); i++) {
            if (!vFilizola.get(i).trim().isEmpty()) {

                NutricionalFilizolaVO vo = new NutricionalFilizolaVO();
                vo.setId(Utils.stringToInt(vFilizola.get(i).substring(0, 6)));
                vo.setDescricao(vFilizola.get(i).substring(7, 29).trim());
                vo.setPorcao(vFilizola.get(i).substring(40, 75).trim());
                vo.setCaloria(Utils.stringToInt(vFilizola.get(i).substring(75, 80)));
                vo.setPercentualcaloria(Utils.stringToInt(vFilizola.get(i).substring(80, 84)));
                vo.setCarboidrato(Utils.stringToDouble(vFilizola.get(i).substring(84, 89)) / 10);
                vo.setPercentualcarboidrato(Utils.stringToInt(vFilizola.get(i).substring(89, 93)));
                vo.setProteina(Utils.stringToDouble(vFilizola.get(i).substring(93, 98)) / 10);
                vo.setPercentualproteina(Utils.stringToInt(vFilizola.get(i).substring(98, 102)));
                vo.setGordura(Utils.stringToDouble(vFilizola.get(i).substring(102, 107)) / 10);
                vo.setPercentualgordura(Utils.stringToInt(vFilizola.get(i).substring(107, 111)));
                vo.setGordurasaturada(Utils.stringToDouble(vFilizola.get(i).substring(111, 116)) / 10);
                vo.setPercentualgordurasaturada(Utils.stringToInt(vFilizola.get(i).substring(116, 120)));
                vo.setGorduratrans(Utils.stringToDouble(vFilizola.get(i).substring(120, 125)) / 10);
                // 125, 129
                vo.setFibra(Utils.stringToDouble(vFilizola.get(i).substring(129, 134)) / 10);
                vo.setPercentualfibra(Utils.stringToInt(vFilizola.get(i).substring(134, 138)));
                // 138, 143
                // 143, 147
                // 147, 152
                // 152, 156
                vo.setSodio(Utils.stringToDouble(vFilizola.get(i).substring(156, 161)) / 10);
                vo.setPercentualsodio(Utils.stringToInt(vFilizola.get(i).substring(161, 165)));
                result.add(vo);
            }
        }

        return result;
    }

    public void importarNutricionalFilizola(String arquivo, String sistema, String loja) throws Exception {
        ProgressBar.setStatus("Carregando dados...Nutricional Filizola...");
        List<NutricionalFilizolaVO> result = getNutricionalFilizola(arquivo);
        new NutricionalFilizolaDAO().salvarV4(result, sistema, loja);
    }

    public static void importarArquivoRdc360(String sistema, String loja, String arquivo) throws Exception {
        ArquivoTXT arq = new ArquivoTXT(arquivo);

        List<NutricionalFilizolaVO> list = new ArrayList<>();
        for (LinhaArquivo ln : arq) {
            StringBuilder linha = new StringBuilder(ln.getString(""));

            NutricionalFilizolaVO vo = new NutricionalFilizolaVO();

            vo.addProduto(Utils.stringLong(trim(linha, 6)));
            vo.setPorcao(trim(linha, 35));
            vo.setCaloria(trimInt(linha, 5));
            vo.setPercentualcaloria(trimInt(linha, 4));
            vo.setCarboidrato(trimInt(linha, 5) / 10);
            vo.setPercentualcarboidrato(trimInt(linha, 4));
            vo.setProteina(trimInt(linha, 5) / 10);
            vo.setPercentualproteina(trimInt(linha, 4));
            vo.setGordura(trimInt(linha, 5) / 10);
            vo.setPercentualgordura(trimInt(linha, 4));
            vo.setGordurasaturada(trimInt(linha, 5) / 10);
            vo.setPercentualgordurasaturada(trimInt(linha, 4));
            vo.setGorduratrans(trimInt(linha, 5) / 10);
            trim(linha, 4); //pula 4 digitos
            vo.setFibra(trimInt(linha, 5) / 10);
            vo.setPercentualfibra(trimInt(linha, 4));
            trim(linha, 5);//pula registros
            trim(linha, 4);//pula registros
            trim(linha, 5);//pula registros
            trim(linha, 4);//pula registros
            vo.setSodio(trimInt(linha, 5) / 10);
            vo.setPercentualsodio(trimInt(linha, 4));

            list.add(vo);
        }
        LOG.fine(list.size() + " nutricionais para serem importados");
        new NutricionalFilizolaDAO().salvarV3(list, sistema, loja);
    }

    private static String trim(StringBuilder linha, int i) {
        String result = linha.toString().substring(0, i).trim();
        linha.delete(0, i);
        return result;
    }

    private static int trimInt(StringBuilder linha, int i) {
        return Utils.stringToInt(trim(linha, i));
    }

    public void salvar(List<NutricionalFilizolaVO> v_nutricionalFilizola) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;

        try {
            Conexao.begin();
            stm = Conexao.createStatement();

            ProgressBar.setStatus("Importando dados...Nutricional Filizola...");
            ProgressBar.setMaximum(v_nutricionalFilizola.size());
            Map<Double, CodigoAnteriorVO> anteriores = new CodigoAnteriorDAO().carregarCodigoAnterior();

            for (NutricionalFilizolaVO i_nutricionalFilizola : v_nutricionalFilizola) {

                for (NutricionalFilizolaItemVO i_nutricionalFilizolaItem
                        : i_nutricionalFilizola.vNutricionalFilizolaItem) {

                    CodigoAnteriorVO codigoAnterior = anteriores.get(i_nutricionalFilizolaItem.getId_produtoDouble());

                    if (codigoAnterior != null) {

                        i_nutricionalFilizola.setId(new CodigoInternoDAO().get("nutricionalfilizola"));

                        sql = new StringBuilder();
                        sql.append("INSERT INTO nutricionalfilizola( ");
                        sql.append("id, descricao, id_situacaocadastro, caloria, carboidrato, carboidratoinferior, ");
                        sql.append("proteina, proteinainferior, gordura, gordurasaturada, gorduratrans, ");
                        sql.append("colesterolinferior, fibra, fibrainferior, calcio, ferro, sodio, ");
                        sql.append("percentualcaloria, percentualcarboidrato, percentualproteina, ");
                        sql.append("percentualgordura, percentualgordurasaturada, percentualfibra, ");
                        sql.append("percentualcalcio, percentualferro, percentualsodio, porcao) ");
                        sql.append("VALUES ( ");
                        sql.append(i_nutricionalFilizola.getId() + ", ");
                        sql.append("'" + i_nutricionalFilizola.getDescricao() + "', ");
                        sql.append(i_nutricionalFilizola.getId_situacaocadastro() + ", ");
                        sql.append(i_nutricionalFilizola.getCaloria() + ", ");
                        sql.append(i_nutricionalFilizola.getCarboidrato() + ", ");
                        sql.append(i_nutricionalFilizola.isCarboidratoinferior() + ", ");
                        sql.append(i_nutricionalFilizola.getProteina() + ", ");
                        sql.append(i_nutricionalFilizola.isProteinainferior() + ", ");
                        sql.append(i_nutricionalFilizola.getGordura() + ", ");
                        sql.append(i_nutricionalFilizola.getGordurasaturada() + ", ");
                        sql.append(i_nutricionalFilizola.getGorduratrans() + ", ");
                        sql.append(i_nutricionalFilizola.isColesterolinferior() + ", ");
                        sql.append(i_nutricionalFilizola.getFibra() + ", ");
                        sql.append(i_nutricionalFilizola.isFibrainferior() + ", ");
                        sql.append(i_nutricionalFilizola.getCalcio() + ", ");
                        sql.append(i_nutricionalFilizola.getFerro() + ", ");
                        sql.append(i_nutricionalFilizola.getSodio() + ", ");
                        sql.append(i_nutricionalFilizola.getPercentualcaloria() + ", ");
                        sql.append(i_nutricionalFilizola.getPercentualcarboidrato() + ", ");
                        sql.append(i_nutricionalFilizola.getPercentualproteina() + ", ");
                        sql.append(i_nutricionalFilizola.getPercentualgordura() + ", ");
                        sql.append(i_nutricionalFilizola.getPercentualgordurasaturada() + ", ");
                        sql.append(i_nutricionalFilizola.getPercentualfibra() + ", ");
                        sql.append(i_nutricionalFilizola.getPercentualcalcio() + ", ");
                        sql.append(i_nutricionalFilizola.getPercentualferro() + ", ");
                        sql.append(i_nutricionalFilizola.getPercentualsodio() + ", ");
                        sql.append("'" + i_nutricionalFilizola.getPorcao() + "'");
                        sql.append(");");
                        stm.execute(sql.toString());

                        i_nutricionalFilizolaItem.setId_nutricionalfilizola(i_nutricionalFilizola.getId());
                        i_nutricionalFilizolaItem.setId_produto((int) codigoAnterior.getCodigoatual());

                        sql = new StringBuilder();
                        sql.append("INSERT INTO nutricionalfilizolaitem( ");
                        sql.append("id_nutricionalfilizola, id_produto) ");
                        sql.append("VALUES ( ");
                        sql.append(i_nutricionalFilizolaItem.getId_nutricionalfilizola() + ", ");
                        sql.append(i_nutricionalFilizolaItem.getId_produto() + "");
                        sql.append(");");
                        stm.execute(sql.toString());

                        ProgressBar.next();
                    }
                }
            }

            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void salvarV2(List<NutricionalFilizolaVO> v_nutricionalFilizola, String sistema, String loja) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;

        try {
            Conexao.begin();
            stm = Conexao.createStatement();

            ProgressBar.setStatus("Importando dados...Nutricional Filizola...");
            ProgressBar.setMaximum(v_nutricionalFilizola.size());
            ProdutoAnteriorDAO dao = new ProdutoAnteriorDAO();
            dao.setImportLoja(loja);
            dao.setImportSistema(sistema);
            MultiMap<String, ProdutoAnteriorVO> anteriores = dao.getCodigoAnterior();

            for (NutricionalFilizolaVO i_nutricionalFilizola : v_nutricionalFilizola) {

                for (NutricionalFilizolaItemVO i_nutricionalFilizolaItem
                        : i_nutricionalFilizola.vNutricionalFilizolaItem) {

                    ProdutoAnteriorVO codigoAnterior = anteriores.get(sistema, loja, i_nutricionalFilizolaItem.getStrID());

                    if (codigoAnterior != null && codigoAnterior.getCodigoAtual() != null) {

                        i_nutricionalFilizola.setId(new CodigoInternoDAO().get("nutricionalfilizola"));

                        if (i_nutricionalFilizola.getDescricao() != null && "".equals(i_nutricionalFilizola.getDescricao().trim())) {
                            i_nutricionalFilizola.setDescricao(codigoAnterior.getCodigoAtual().getDescricaoCompleta());
                        }

                        sql = new StringBuilder();
                        sql.append("INSERT INTO nutricionalfilizola( ");
                        sql.append("id, descricao, id_situacaocadastro, caloria, carboidrato, carboidratoinferior, ");
                        sql.append("proteina, proteinainferior, gordura, gordurasaturada, gorduratrans, ");
                        sql.append("colesterolinferior, fibra, fibrainferior, calcio, ferro, sodio, ");
                        sql.append("percentualcaloria, percentualcarboidrato, percentualproteina, ");
                        sql.append("percentualgordura, percentualgordurasaturada, percentualfibra, ");
                        sql.append("percentualcalcio, percentualferro, percentualsodio, porcao) ");
                        sql.append("VALUES ( ");
                        sql.append(i_nutricionalFilizola.getId() + ", ");
                        sql.append("'" + i_nutricionalFilizola.getDescricao() + "', ");
                        sql.append(i_nutricionalFilizola.getId_situacaocadastro() + ", ");
                        sql.append(i_nutricionalFilizola.getCaloria() + ", ");
                        sql.append(i_nutricionalFilizola.getCarboidrato() + ", ");
                        sql.append(i_nutricionalFilizola.isCarboidratoinferior() + ", ");
                        sql.append(i_nutricionalFilizola.getProteina() + ", ");
                        sql.append(i_nutricionalFilizola.isProteinainferior() + ", ");
                        sql.append(i_nutricionalFilizola.getGordura() + ", ");
                        sql.append(i_nutricionalFilizola.getGordurasaturada() + ", ");
                        sql.append(i_nutricionalFilizola.getGorduratrans() + ", ");
                        sql.append(i_nutricionalFilizola.isColesterolinferior() + ", ");
                        sql.append(i_nutricionalFilizola.getFibra() + ", ");
                        sql.append(i_nutricionalFilizola.isFibrainferior() + ", ");
                        sql.append(i_nutricionalFilizola.getCalcio() + ", ");
                        sql.append(i_nutricionalFilizola.getFerro() + ", ");
                        sql.append(i_nutricionalFilizola.getSodio() + ", ");
                        sql.append(i_nutricionalFilizola.getPercentualcaloria() + ", ");
                        sql.append(i_nutricionalFilizola.getPercentualcarboidrato() + ", ");
                        sql.append(i_nutricionalFilizola.getPercentualproteina() + ", ");
                        sql.append(i_nutricionalFilizola.getPercentualgordura() + ", ");
                        sql.append(i_nutricionalFilizola.getPercentualgordurasaturada() + ", ");
                        sql.append(i_nutricionalFilizola.getPercentualfibra() + ", ");
                        sql.append(i_nutricionalFilizola.getPercentualcalcio() + ", ");
                        sql.append(i_nutricionalFilizola.getPercentualferro() + ", ");
                        sql.append(i_nutricionalFilizola.getPercentualsodio() + ", ");
                        sql.append("'" + i_nutricionalFilizola.getPorcao() + "'");
                        sql.append(");");
                        stm.execute(sql.toString());

                        i_nutricionalFilizolaItem.setId_nutricionalfilizola(i_nutricionalFilizola.getId());
                        i_nutricionalFilizolaItem.setId_produto((int) codigoAnterior.getCodigoAtual().getId());

                        sql = new StringBuilder();
                        sql.append("INSERT INTO nutricionalfilizolaitem( ");
                        sql.append("id_nutricionalfilizola, id_produto) ");
                        sql.append("VALUES ( ");
                        sql.append(i_nutricionalFilizolaItem.getId_nutricionalfilizola() + ", ");
                        sql.append(i_nutricionalFilizolaItem.getId_produto() + "");
                        sql.append(");");
                        stm.execute(sql.toString());

                        ProgressBar.next();
                    }
                }
            }

            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void salvarV3(List<NutricionalFilizolaVO> v_nutricionalFilizola, String sistema, String loja) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;

        LOG.fine("Sistema: '" + sistema + "' Loja: '" + loja + "'");

        try {
            Conexao.begin();
            stm = Conexao.createStatement();

            ProgressBar.setStatus("Importando dados...Nutricional Filizola...");
            ProgressBar.setMaximum(v_nutricionalFilizola.size());
            ProdutoAnteriorDAO dao = new ProdutoAnteriorDAO();
            dao.setImportLoja(loja);
            dao.setImportSistema(sistema);
            MultiMap<String, ProdutoAnteriorVO> anteriores = dao.getCodigoAnterior();

            LOG.fine(anteriores.size() + " códigos anteriores encontrados");

            for (NutricionalFilizolaVO i_nutricionalFilizola : v_nutricionalFilizola) {

                for (String idProduto : i_nutricionalFilizola.getProdutoId()) {

                    LOG.finer("Localizando o anterior de " + idProduto);

                    ProdutoAnteriorVO codigoAnterior = anteriores.get(sistema, loja, idProduto);

                    if (codigoAnterior != null) {

                        LOG.finer("Produto " + idProduto + " encontrado como " + codigoAnterior.getCodigoAtual().getId());

                        i_nutricionalFilizola.setId(new CodigoInternoDAO().get("nutricionalfilizola"));

                        sql = new StringBuilder();
                        sql.append("INSERT INTO nutricionalfilizola( ");
                        sql.append("id, descricao, id_situacaocadastro, caloria, carboidrato, carboidratoinferior, ");
                        sql.append("proteina, proteinainferior, gordura, gordurasaturada, gorduratrans, ");
                        sql.append("colesterolinferior, fibra, fibrainferior, calcio, ferro, sodio, ");
                        sql.append("percentualcaloria, percentualcarboidrato, percentualproteina, ");
                        sql.append("percentualgordura, percentualgordurasaturada, percentualfibra, ");
                        sql.append("percentualcalcio, percentualferro, percentualsodio, porcao) ");
                        sql.append("VALUES ( ");
                        sql.append(i_nutricionalFilizola.getId() + ", ");
                        sql.append("'" + i_nutricionalFilizola.getDescricao() + "', ");
                        sql.append(i_nutricionalFilizola.getId_situacaocadastro() + ", ");
                        sql.append(i_nutricionalFilizola.getCaloria() + ", ");
                        sql.append(i_nutricionalFilizola.getCarboidrato() + ", ");
                        sql.append(i_nutricionalFilizola.isCarboidratoinferior() + ", ");
                        sql.append(i_nutricionalFilizola.getProteina() + ", ");
                        sql.append(i_nutricionalFilizola.isProteinainferior() + ", ");
                        sql.append(i_nutricionalFilizola.getGordura() + ", ");
                        sql.append(i_nutricionalFilizola.getGordurasaturada() + ", ");
                        sql.append(i_nutricionalFilizola.getGorduratrans() + ", ");
                        sql.append(i_nutricionalFilizola.isColesterolinferior() + ", ");
                        sql.append(i_nutricionalFilizola.getFibra() + ", ");
                        sql.append(i_nutricionalFilizola.isFibrainferior() + ", ");
                        sql.append(i_nutricionalFilizola.getCalcio() + ", ");
                        sql.append(i_nutricionalFilizola.getFerro() + ", ");
                        sql.append(i_nutricionalFilizola.getSodio() + ", ");
                        sql.append(i_nutricionalFilizola.getPercentualcaloria() + ", ");
                        sql.append(i_nutricionalFilizola.getPercentualcarboidrato() + ", ");
                        sql.append(i_nutricionalFilizola.getPercentualproteina() + ", ");
                        sql.append(i_nutricionalFilizola.getPercentualgordura() + ", ");
                        sql.append(i_nutricionalFilizola.getPercentualgordurasaturada() + ", ");
                        sql.append(i_nutricionalFilizola.getPercentualfibra() + ", ");
                        sql.append(i_nutricionalFilizola.getPercentualcalcio() + ", ");
                        sql.append(i_nutricionalFilizola.getPercentualferro() + ", ");
                        sql.append(i_nutricionalFilizola.getPercentualsodio() + ", ");
                        sql.append("'" + i_nutricionalFilizola.getPorcao() + "'");
                        sql.append(");");
                        stm.execute(sql.toString());

                        LOG.finest(sql.toString());

                        sql = new StringBuilder();
                        sql.append("INSERT INTO nutricionalfilizolaitem( ");
                        sql.append("id_nutricionalfilizola, id_produto) ");
                        sql.append("VALUES ( ");
                        sql.append(i_nutricionalFilizola.getId()).append(", ");
                        sql.append(codigoAnterior.getCodigoAtual().getId());
                        sql.append(");");

                        LOG.finest(sql.toString());

                        stm.execute(sql.toString());

                        ProgressBar.next();
                    }
                }
            }

            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void salvarV4(List<NutricionalFilizolaVO> filizola, String sistema, String loja) throws Exception {
        ProgressBar.setMaximum(filizola.size());
        ProgressBar.setStatus("Importando Nutricional Filizola...");

        for (NutricionalFilizolaVO vo : filizola) {
            try (Statement stm = Conexao.createStatement()) {
                StringBuilder sql = new StringBuilder();
                sql.append("INSERT INTO nutricionalfilizola( ");
                sql.append("id, descricao, id_situacaocadastro, caloria, carboidrato, carboidratoinferior, ");
                sql.append("proteina, proteinainferior, gordura, gordurasaturada, gorduratrans, ");
                sql.append("colesterolinferior, fibra, fibrainferior, calcio, ferro, sodio, ");
                sql.append("percentualcaloria, percentualcarboidrato, percentualproteina, ");
                sql.append("percentualgordura, percentualgordurasaturada, percentualfibra, ");
                sql.append("percentualcalcio, percentualferro, percentualsodio, porcao) ");
                sql.append("VALUES ( ");
                sql.append(vo.getId() + ", ");
                sql.append("'" + vo.getDescricao() + "', ");
                sql.append(vo.getId_situacaocadastro() + ", ");
                sql.append(vo.getCaloria() + ", ");
                sql.append(vo.getCarboidrato() + ", ");
                sql.append(vo.isCarboidratoinferior() + ", ");
                sql.append(vo.getProteina() + ", ");
                sql.append(vo.isProteinainferior() + ", ");
                sql.append(vo.getGordura() + ", ");
                sql.append(vo.getGordurasaturada() + ", ");
                sql.append(vo.getGorduratrans() + ", ");
                sql.append(vo.isColesterolinferior() + ", ");
                sql.append(vo.getFibra() + ", ");
                sql.append(vo.isFibrainferior() + ", ");
                sql.append(vo.getCalcio() + ", ");
                sql.append(vo.getFerro() + ", ");
                sql.append(vo.getSodio() + ", ");
                sql.append(vo.getPercentualcaloria() + ", ");
                sql.append(vo.getPercentualcarboidrato() + ", ");
                sql.append(vo.getPercentualproteina() + ", ");
                sql.append(vo.getPercentualgordura() + ", ");
                sql.append(vo.getPercentualgordurasaturada() + ", ");
                sql.append(vo.getPercentualfibra() + ", ");
                sql.append(vo.getPercentualcalcio() + ", ");
                sql.append(vo.getPercentualferro() + ", ");
                sql.append(vo.getPercentualsodio() + ", ");
                sql.append("'" + vo.getPorcao() + "'");
                sql.append(");");
                stm.execute(sql.toString());
            }

            int idProduto = -1;
            idProduto = new ProdutoAnteriorDAO().getCodigoAtualEANantCPGestor(sistema, loja, String.valueOf(vo.getId()));

            if (idProduto != -1) {
                try (Statement stm = Conexao.createStatement()) {
                    StringBuilder sql = new StringBuilder();
                    sql.append("INSERT INTO nutricionalfilizolaitem( ");
                    sql.append("id_nutricionalfilizola, id_produto) ");
                    sql.append("VALUES ( ");
                    sql.append(vo.getId() + ", ");
                    sql.append(idProduto);
                    sql.append(");");

                    stm.execute(sql.toString());
                }
            }
            ProgressBar.next();
        }
    }
}
