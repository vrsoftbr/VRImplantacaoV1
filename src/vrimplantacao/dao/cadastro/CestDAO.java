package vrimplantacao.dao.cadastro;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.CestVO;
import vrimplantacao.vo.vrimplantacao.NcmVO;

/**
 * @author Leandro
 */
public class CestDAO {
    
    private static final Logger LOG = Logger.getLogger(CestDAO.class.getName());
    
    public int validar(int cest) throws Exception {
        
        int cest1Parts = -1;
        int cest2Parts = -1;
        int cest3Parts = -1;
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        
        try {
            if (String.valueOf(cest).trim().length() == 5) {
                cest1Parts = Integer.parseInt(String.valueOf(cest).trim().substring(0, 1));
                cest2Parts = Integer.parseInt(String.valueOf(cest).trim().substring(1, 3));
                cest3Parts = Integer.parseInt(String.valueOf(cest).trim().substring(3, 5));
            } else if (String.valueOf(cest).trim().length() == 6) {
                cest1Parts = Integer.parseInt(String.valueOf(cest).trim().substring(0, 1));
                cest2Parts = Integer.parseInt(String.valueOf(cest).trim().substring(1, 4));
                cest3Parts = Integer.parseInt(String.valueOf(cest).trim().substring(4, 6));
            } else if (String.valueOf(cest).trim().length() == 7) {
                cest1Parts = Integer.parseInt(String.valueOf(cest).trim().substring(0, 2));
                cest2Parts = Integer.parseInt(String.valueOf(cest).trim().substring(2, 5));
                cest3Parts = Integer.parseInt(String.valueOf(cest).trim().substring(5, 7));
            }

            stm = Conexao.createStatement();
            sql = new StringBuilder();
            sql.append("select id from cest ");
            sql.append("where cest1 = " + cest1Parts + " ");
            sql.append("and cest2 = " + cest2Parts + " ");
            sql.append("and cest3 = " + cest3Parts + ";");
            rst = stm.executeQuery(sql.toString());

            if (rst.next()) {
                return rst.getInt("id");
            } else {
                return -1;
            }

        } catch (Exception ex) {
            throw ex;
        }
    }
    
    public static CestVO parse(String cest) {
        try {
            if (cest != null) {
                cest = Utils.formataNumero(cest);
                if (!"0".equals(cest)) {              
                    String arr = new StringBuilder(cest).reverse().toString();
                    String[] string = new String[7];

                    for (int i = 0; i < arr.length(); i++) {
                        string[i] = arr.substring(i, i + 1);
                    }

                    for (int i = 0; i < string.length; i++) {
                        if (string[i] == null) {
                            string[i] = "0";
                        }
                    }

                    CestVO oCest = new CestVO();
                    oCest.setCest1(0);
                    oCest.setCest2(0);
                    oCest.setCest3(0);

                    String aux = "";
                    for (int i = 0; i < string.length; i++) {
                        if (i <= 1) {
                            aux += string[i];
                            if (i == 1) {
                                oCest.setCest3(Integer.parseInt(new StringBuilder(aux).reverse().toString()));                            
                                aux = "";
                            }
                        } else if (i < string.length && i <= 4) {
                            aux += string[i];
                            if (i == 4) {
                                oCest.setCest2(Integer.parseInt(new StringBuilder(aux).reverse().toString()));                            
                                aux = "";
                            }
                        } else if (i < string.length && i <= 6) {
                            aux += string[i];
                            if (i == 6) {
                                oCest.setCest1(Integer.parseInt(new StringBuilder(aux).reverse().toString()));                            
                                aux = "";
                            }
                        }
                    }

                    return oCest;
                }
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Erro ao executar o parse do CEST " + cest, e);
        }
        return new CestVO(); 
    }
    
    private Map<String, CestVO> cestCadastrados;

    public CestVO getCestValido(String cest, boolean localizarNcmsRelacionados) throws Exception{        
        if (!Utils.formataNumero(cest).equals("")) {
            if (cestCadastrados == null) {
                carregarCestCadastrados(localizarNcmsRelacionados);
            }

            //Verifica se o ncm existe e retorna a lista de cests relacionados
            CestVO oCest = cestCadastrados.get(CestDAO.parse(cest).getChave());
            //Se o ncm selecionado está vinculado com esse cest no VR executa if
            return oCest != null ? oCest : new CestVO();
        }
        
        return new CestVO();        
    }
    
    public CestVO getCestValido(String cest) throws Exception{ 
        return getCestValido(cest, false);
    }
    
    public CestVO getCestValido(int cest1, int cest2, int cest3, boolean localizarNcmsRelacionados) throws Exception {
        if (cestCadastrados == null) {
            carregarCestCadastrados(localizarNcmsRelacionados);
        }

        //Verifica se o ncm existe e retorna a lista de cests relacionados
        CestVO oCest = cestCadastrados.get(cest1 + "-" + cest2 + "-" + cest3);
        //Se o ncm selecionado está vinculado com esse cest no VR executa if
        return oCest != null ? oCest : new CestVO();
    }
    
    public CestVO getCestValido(int cest1, int cest2, int cest3) throws Exception {
        return getCestValido(cest1, cest2, cest3, false);
    }

    /**
     * Atualiza a listagem de CESTs cadastrados no vo;
     * @throws java.lang.Exception
     */
    public void carregarCestCadastrados(boolean localizarNcmsRelacionados) throws Exception {
        cestCadastrados = new LinkedHashMap<>();
        
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                "select \n" +
                "	cest.id, cest.cest1, cest.cest2, cest.cest3, cest.descricao \n" +
                "from \n" +
                "	cest\n" +
                "order by \n" +
                "	cest.id"
            )) {
                while (rst.next()) {
                    CestVO cest = new CestVO();
                    
                    cest.setId(rst.getInt("id"));
                    cest.setDescricao(rst.getString("descricao"));
                    cest.setCest1(rst.getInt("cest1"));
                    cest.setCest2(rst.getInt("cest2"));
                    cest.setCest3(rst.getInt("cest3"));
                    
                    if (localizarNcmsRelacionados) {
                        try (Statement stm2 = Conexao.createStatement()) {
                            try (ResultSet rst2 = stm2.executeQuery(
                                    "select \n" +
                                    "	ncm.id,\n" +
                                    "	ncm.ncm1,\n" +
                                    "	ncm.ncm2,\n" +
                                    "	ncm.ncm3,\n" +
                                    "	ncm.nivel,\n" +
                                    "	ncm.descricao\n" +
                                    "from 	\n" +
                                    "	ncm\n" +
                                    "	join ncmcest nc on ncm.id = nc.id_ncm\n" +
                                    "where \n" +
                                    "	nc.id_cest = " + cest.getId() + " and\n" +
                                    "	ncm.nivel = 3"
                            )) {
                                while (rst2.next()) {
                                    NcmVO ncm = new NcmVO();
                                    ncm.setId(rst2.getInt("id"));
                                    ncm.setNcm1(rst2.getInt("ncm1"));
                                    ncm.setNcm2(rst2.getInt("ncm2"));
                                    ncm.setNcm3(rst2.getInt("ncm3"));
                                    ncm.setNivel(rst2.getInt("nivel"));
                                    ncm.setDescricao(rst2.getString("descricao"));

                                    cest.getNcms().add(ncm);
                                }
                            }
                        }
                    }
                    
                    cestCadastrados.put(cest.getChave(), cest);
                }
            }
        }
    }
    
    public void salvar(List<CestVO> cestMapeados) throws Exception {
        try {
            Conexao.begin();
            
            ProgressBar.setStatus("Gravando CEST....");
            ProgressBar.setMaximum(cestMapeados.size());
            
            carregarCestCadastrados(true);
            
            try (Statement stm = Conexao.createStatement()) {
                for (CestVO cest: cestMapeados) {
                    //Se o CEST não existir, inclui no banco de dados.
                    CestVO existente = getCestValido(cest.getCest1(), cest.getCest2(), cest.getCest3());
                    if (existente.getCest1() == -1) {
                        try (ResultSet rst = stm.executeQuery(
                                "insert into cest (cest1, cest2, cest3, descricao) values \n" +
                                "(" + cest.getCest1() + ",\n" +
                                cest.getCest2() + ",\n" +
                                cest.getCest3() + ",\n" +
                                Utils.quoteSQL(cest.getDescricao()) + ") returning id"
                        )) {
                            while (rst.next()) {
                                cest.setId(rst.getLong("id"));
                                cestCadastrados.put(cest.getChave(), cest);
                            }
                        }
                    } else {
                        cest.setId(existente.getId());
                    }

                    for (NcmVO ncm: cest.getNcms()) {
                        if (!existente.getNcms().contains(ncm)) {
                            stm.execute("insert into ncmcest (id_ncm, id_cest) values (" + ncm.getId() + "," + cest.getId() + ")");
                            existente.getNcms().add(ncm);
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
}
