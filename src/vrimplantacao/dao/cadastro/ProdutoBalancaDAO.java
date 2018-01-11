package vrimplantacao.dao.cadastro;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao2.utils.arquivo.Arquivo;
import vrimplantacao2.utils.arquivo.ArquivoFactory;
import vrimplantacao2.utils.arquivo.LinhaArquivo;

public class ProdutoBalancaDAO {
    
    
    public List<ProdutoBalancaVO> carregar(String arquivo, int opcao) throws Exception {
        
        Utils util = new Utils();        
        List<ProdutoBalancaVO> vProdutoBalanca = new ArrayList<>();
        
        if (opcao == 4) { //Planilha
            Arquivo balanca = ArquivoFactory.getArquivo(arquivo, null);

            for (LinhaArquivo linha: balanca) {
                ProdutoBalancaVO vo = new ProdutoBalancaVO();
                
                vo.setCodigo(linha.getInt("codigo"));
                vo.setDescricao(linha.getString("descricao"));
                String pesavel = Utils.acertarTexto(linha.getString("pesavel"), 2);
                switch (pesavel) {
                    case "UN": vo.setPesavel("U"); break;
                    case "PC": vo.setPesavel("U"); break;
                    case "KG": vo.setPesavel("P"); break;
                    default: vo.setPesavel(pesavel);
                }
                vo.setValidade(linha.getInt("validade"));
                
                vProdutoBalanca.add(vo);
            }
            
            return vProdutoBalanca;
        } else {
            
            try {
                
                List<String> vDadosBalanca = util.lerArquivoBalanca(arquivo);

                for (int i = 0; i < vDadosBalanca.size(); i++) {

                    ProdutoBalancaVO oProdutoBalanca = new ProdutoBalancaVO();

                    if (opcao == 1) { // CADTXT                    
                        if (!vDadosBalanca.get(i).trim().isEmpty()) {
                            oProdutoBalanca.codigo = Integer.parseInt(vDadosBalanca.get(i).substring(0, 6));
                            oProdutoBalanca.pesavel = vDadosBalanca.get(i).substring(6, 7);
                            oProdutoBalanca.descricao = util.acertarTexto(vDadosBalanca.get(i).substring(7, 29).replace("'", "").trim());
                            oProdutoBalanca.validade = Integer.parseInt(vDadosBalanca.get(i).substring(vDadosBalanca.get(i).trim().length() - 3).trim());
                        }
                    } else if (opcao == 2) { // TXTITENS
                       if (!vDadosBalanca.get(i).trim().isEmpty()) {
                            if ("00".equals(vDadosBalanca.get(i).substring(4, 6))) {
                                oProdutoBalanca.codigo = Integer.parseInt(vDadosBalanca.get(i).substring(5, 11));
                                oProdutoBalanca.pesavel = "P";
                                oProdutoBalanca.descricao = util.acertarTexto(vDadosBalanca.get(i).substring(20).replace("'", "").trim());
                                oProdutoBalanca.validade = Integer.parseInt(vDadosBalanca.get(i).substring(18, 20));
                            } else {
                                oProdutoBalanca.codigo = Integer.parseInt(vDadosBalanca.get(i).substring(5, 11));
                                oProdutoBalanca.pesavel = "U";
                                oProdutoBalanca.descricao = util.acertarTexto(vDadosBalanca.get(i).substring(20).replace("'", "").trim());
                                oProdutoBalanca.validade = Integer.parseInt(vDadosBalanca.get(i).substring(18, 20));
                            }
                        }
                    } else if (opcao == 3) { // ITENSMGV
                        if (!vDadosBalanca.get(i).trim().isEmpty()) {
                            if ("0".equals(vDadosBalanca.get(i).substring(2, 3))) {
                                oProdutoBalanca.codigo = Integer.parseInt(vDadosBalanca.get(i).substring(3, 9));
                                oProdutoBalanca.pesavel = "P";
                                oProdutoBalanca.descricao = util.acertarTexto(vDadosBalanca.get(i).substring(18, 67).replace("'", "").trim());
                                oProdutoBalanca.validade = Integer.parseInt(vDadosBalanca.get(i).substring(15, 18));
                            } else {
                                oProdutoBalanca.codigo = Integer.parseInt(vDadosBalanca.get(i).substring(3, 9));
                                oProdutoBalanca.pesavel = "U";
                                oProdutoBalanca.descricao = util.acertarTexto(vDadosBalanca.get(i).substring(18, 67).replace("'", "").trim());
                                oProdutoBalanca.validade = Integer.parseInt(vDadosBalanca.get(i).substring(15, 18));
                            }
                        }
                    }

                    vProdutoBalanca.add(oProdutoBalanca);
                }

                return vProdutoBalanca;

            } catch(Exception ex) {
                throw ex;
            }
        }
    }
    
    public List<ProdutoBalancaVO> carregar2(String arquivo, int opcao) throws Exception {
        
        Utils util = new Utils();        
        List<ProdutoBalancaVO> vProdutoBalanca = new ArrayList<>();
        List<String> vDadosBalanca = util.lerArquivoBalanca(arquivo);
        
        try {
            
            for (int i = 0; i < vDadosBalanca.size(); i++) {
                
                ProdutoBalancaVO oProdutoBalanca = new ProdutoBalancaVO();
                
                if (opcao == 1) { // CADTXT
                    if (!vDadosBalanca.get(i).trim().isEmpty()) {
                        oProdutoBalanca.codigo = Integer.parseInt(vDadosBalanca.get(i).substring(0, 6));
                        oProdutoBalanca.pesavel = vDadosBalanca.get(i).substring(6, 7);
                        oProdutoBalanca.descricao = util.acertarTexto(vDadosBalanca.get(i).substring(7, 29).replace("'", "").trim());
                        oProdutoBalanca.validade = Integer.parseInt(vDadosBalanca.get(i).substring(vDadosBalanca.get(i).trim().length() - 3).trim());
                    }
                    
                } else if (opcao == 2) { // TXTITENS
                    if (!vDadosBalanca.get(i).trim().isEmpty()) {
                        if ("0".equals(vDadosBalanca.get(i).substring(4, 5))) {
                            oProdutoBalanca.codigo = Integer.parseInt(vDadosBalanca.get(i).substring(5, 11));
                            oProdutoBalanca.pesavel = "P";
                            oProdutoBalanca.descricao = util.acertarTexto(vDadosBalanca.get(i).substring(20).replace("'", "").trim());
                            oProdutoBalanca.validade = Integer.parseInt(vDadosBalanca.get(i).substring(18, 20));
                        } else {
                            oProdutoBalanca.codigo = Integer.parseInt(vDadosBalanca.get(i).substring(5, 11));
                            oProdutoBalanca.pesavel = "U";
                            oProdutoBalanca.descricao = util.acertarTexto(vDadosBalanca.get(i).substring(20).replace("'", "").trim());
                            oProdutoBalanca.validade = Integer.parseInt(vDadosBalanca.get(i).substring(18, 20));
                        }
                    }
                } else if (opcao == 3) { // ITENSMGV
                    if (!vDadosBalanca.get(i).trim().isEmpty()) {
                        if ("0".equals(vDadosBalanca.get(i).substring(2, 3))) {
                            oProdutoBalanca.codigo = Integer.parseInt(vDadosBalanca.get(i).substring(3, 9));
                            oProdutoBalanca.pesavel = "P";
                            oProdutoBalanca.descricao = util.acertarTexto(vDadosBalanca.get(i).substring(18, 67).replace("'", "").trim());
                            oProdutoBalanca.validade = Integer.parseInt(vDadosBalanca.get(i).substring(15, 18));
                        } else {
                            oProdutoBalanca.codigo = Integer.parseInt(vDadosBalanca.get(i).substring(3, 9));
                            oProdutoBalanca.pesavel = "U";
                            oProdutoBalanca.descricao = util.acertarTexto(vDadosBalanca.get(i).substring(18, 67).replace("'", "").trim());
                            oProdutoBalanca.validade = Integer.parseInt(vDadosBalanca.get(i).substring(15, 18));
                        }
                    }
                }
                
                vProdutoBalanca.add(oProdutoBalanca);
            }
            
            return vProdutoBalanca;
            
        } catch(Exception ex) {
            
            throw ex;
        }
    }
    
    public void salvar(List<ProdutoBalancaVO> v_produtoBalanca) throws Exception {
        
        StringBuilder sql = null;
        Statement stm = null;
        
        try {
            
            Conexao.begin();
            
            stm = Conexao.createStatement();
            
            ProgressBar.setMaximum(v_produtoBalanca.size());
            ProgressBar.setStatus("Importando Produtos da Balanca...");
            
            sql = new StringBuilder();
            sql.append("DELETE FROM implantacao.produtobalanca; ");
            stm.execute(sql.toString());
            
            for (ProdutoBalancaVO i_produtoBalanca: v_produtoBalanca) {
                
                sql = new StringBuilder();
                sql.append("INSERT INTO implantacao.produtobalanca( ");
                sql.append("codigo, descricao, pesavel, validade, valida) ");
                sql.append("VALUES ( ");
                sql.append(i_produtoBalanca.codigo+", '"+i_produtoBalanca.descricao+"', ");
                sql.append("'"+i_produtoBalanca.pesavel+"', "+i_produtoBalanca.validade+", NULL);");
                
                stm.execute(sql.toString());
                
                ProgressBar.next();
            }
            
            stm.close();
            Conexao.commit();
            
        } catch(Exception ex) {
            
            Conexao.rollback();
            throw ex;
        }
    }

    public void salvarSemDelete(List<ProdutoBalancaVO> v_produtoBalanca) throws Exception {
        
        StringBuilder sql = null;
        Statement stm = null;
        
        try {
            
            Conexao.begin();
            
            stm = Conexao.createStatement();
            
            ProgressBar.setMaximum(v_produtoBalanca.size());
            ProgressBar.setStatus("Importando Produtos da Lanchonete...");
            
            stm.execute("alter table implantacao.produtobalanca "
                    + "add lanchonete boolean default false;");
            
            for (ProdutoBalancaVO i_produtoBalanca: v_produtoBalanca) {
                
                sql = new StringBuilder();
                sql.append("INSERT INTO implantacao.produtobalanca( ");
                sql.append("codigo, descricao, pesavel, validade, valida, lanchonete) ");
                sql.append("VALUES ( ");
                sql.append(i_produtoBalanca.codigo+", '"+i_produtoBalanca.descricao+"', ");
                sql.append("'"+i_produtoBalanca.pesavel+"', "+i_produtoBalanca.validade+", NULL, true);");
                
                stm.execute(sql.toString());
                
                ProgressBar.next();
            }
            
            stm.close();
            Conexao.commit();
            
        } catch(Exception ex) {
            
            Conexao.rollback();
            throw ex;
        }
    }
    
    /**
     * Atalho para importar arquivo de balança.
     * @param arquivo Nome do arquivo de balança.
     * @param opcao Opção do arquivo de balança
     * @throws Exception 
     */
    public static void importarProdutoBalanca(String arquivo, int opcao) throws Exception {
        ProgressBar.setStatus("Carregando dados...Produtos de Balanca...");
        List<ProdutoBalancaVO> vProdutoBalanca = new ProdutoBalancaDAO().carregar(arquivo, opcao);

        new ProdutoBalancaDAO().salvar(vProdutoBalanca);
    }
    
    public Map<Integer, ProdutoBalancaVO> carregarProdutosBalanca() throws Exception {
        Map<Integer, ProdutoBalancaVO> produtos = new LinkedHashMap<>();
        
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery("select * from implantacao.produtobalanca order by codigo")) {
                
                while (rst.next()) {
                    ProdutoBalancaVO vo = new ProdutoBalancaVO();

                    vo.codigo = rst.getInt("codigo");
                    vo.descricao = rst.getString("descricao");
                    vo.pesavel = rst.getString("pesavel");
                    vo.validade = rst.getInt("validade");
                    vo.valida = rst.getInt("valida");

                    produtos.put(vo.codigo, vo);
                }                
            }
        }
        
        return produtos;
    }

    public ProdutoBalancaVO localizar(long codigoBarras) throws Exception {     
        
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery("select * from implantacao.produtobalanca where codigo = " + codigoBarras)) {
                
                if (rst.next()) {
                    ProdutoBalancaVO vo = new ProdutoBalancaVO();
                    vo.codigo = rst.getInt("codigo");
                    vo.descricao = rst.getString("descricao");
                    vo.pesavel = rst.getString("pesavel");
                    vo.validade = rst.getInt("validade");
                    vo.valida = rst.getInt("valida");
                    return vo;
                }                
            }
        }
        
        return null;
    }
}