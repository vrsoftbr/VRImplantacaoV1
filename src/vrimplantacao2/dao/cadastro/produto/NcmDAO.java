package vrimplantacao2.dao.cadastro.produto;

import java.sql.ResultSet;
import java.sql.Statement;
import vrframework.classe.Conexao;
import vrimplantacao.utils.Utils;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.enums.NcmVO;

public class NcmDAO {
    
    private MultiMap<String, NcmVO> ncms;
    private NcmVO ncmPadrao;
    
    /**
     * Retorna uma listagem com todos os NCMs cadastrados no sistema.
     * @throws Exception 
     */
    public void obterNcms() throws Exception {
        MultiMap<String, NcmVO> result = new MultiMap<>(1);
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                "select\n" +
                "	id,\n" +
                "	ncm1,\n" +
                "	ncm2,\n" +
                "	ncm3,\n" +
                "	descricao\n" +
                "from \n" +
                "	ncm \n" +
                "where \n" +
                "	nivel = 3 \n" +
                "order by \n" +
                "	ncm1, ncm2, ncm3"
            )) {
                while (rst.next()) {
                    NcmVO ncm = new NcmVO();
                    ncm.setId(rst.getInt("id"));
                    ncm.setNcm1(rst.getInt("ncm1"));
                    ncm.setNcm2(rst.getInt("ncm2"));
                    ncm.setNcm3(rst.getInt("ncm3"));
                    ncm.setDescricao(rst.getString("descricao"));
                    String chave = String.format("%04d%02d%02d", ncm.getNcm1(), ncm.getNcm2(), ncm.getNcm3());
                    result.put(ncm, chave);
                }
            }
        }
        
        ncmPadrao = result.get("04029900");
        ncms = result;
    }

    public NcmVO getNcm(String ncmStr) throws Exception {
        if (ncms == null) {
            obterNcms();
        }
        ncmStr = String.format("%08d", Utils.stringToInt(ncmStr));
        NcmVO result = ncms.get(ncmStr);
        if (result == null) {
            result = ncmPadrao;
        }
        return result;
    }
    
    public NcmVO getNcmSemPadrao(String ncmStr) throws Exception {
        if (ncms == null) {
            obterNcms();
        }
        ncmStr = String.format("%08d", Utils.stringToInt(ncmStr));
        return ncms.get(ncmStr);
    }
    
    
    
}
