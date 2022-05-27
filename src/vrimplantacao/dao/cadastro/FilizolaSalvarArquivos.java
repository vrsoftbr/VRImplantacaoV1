/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao.dao.cadastro;

import java.sql.Statement;
import java.util.List;
import java.util.logging.Logger;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao.dao.CodigoInternoDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.NutricionalFilizolaVO;
import vrimplantacao2.dao.cadastro.produto.ProdutoAnteriorDAO;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.ProdutoAnteriorVO;

/**
 *
 * @author Michael
 */
public class FilizolaSalvarArquivos {
    
    FilizolaOperacoesArquivo filizolaOperacoesArquivo = new FilizolaOperacoesArquivo();
    NutricionalFilizolaDAO nutricionalFilizolaDAO = new NutricionalFilizolaDAO();
    
    private static final Logger LOG = Logger.getLogger(NutricionalFilizolaDAO.class.getName());
    Utils util = new Utils();
    
    public void salvarArquivoRdc360(String arquivo, String sistema, String loja) throws Exception {
        NutricionalFilizolaDAO nutricionalFilizolaDAO = new NutricionalFilizolaDAO();
        List<NutricionalFilizolaVO> v_nutricionalFilizola = filizolaOperacoesArquivo.getArquivoRdc360(arquivo);
        LOG.fine("Sistema: '" + sistema + "' Loja: '" + loja + "'");

        try {
            Conexao.begin();

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

                        nutricionalFilizolaDAO.gravar(i_nutricionalFilizola);

                        int id = i_nutricionalFilizola.getId();
                        int codigoAtual = i_nutricionalFilizola.getId();

                        nutricionalFilizolaDAO.gravarItemArquivoRdc360(id, codigoAtual);

                        ProgressBar.next();
                    }
                }
            }
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void salvarArquivo(String arquivo, String sistema, String loja) throws Exception {
        ProgressBar.setStatus("Carregando dados...Nutricional Filizola...");
        List<NutricionalFilizolaVO> filizola = filizolaOperacoesArquivo.getNutricionalFilizola(arquivo);
        
        ProgressBar.setMaximum(filizola.size());
        ProgressBar.setStatus("Importando Nutricional Filizola...");
        try {
            Conexao.begin();
            for (NutricionalFilizolaVO vo : filizola) {
                nutricionalFilizolaDAO.gravar(vo);

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
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }
}
