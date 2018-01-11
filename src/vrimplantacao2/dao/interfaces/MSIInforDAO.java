package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoDBF;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 * DAO desenvolvido para importar o sistema MSIInfor.
 * @author Leandro
 */
public class MSIInforDAO extends InterfaceDAO {

    @Override
    public String getSistema() {
        return "MSIInfor";
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try (Statement stm = ConexaoDBF.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select cmempcod, cmempnomfa from cmemp"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("cmempcod"), rst.getString("cmempnomfa")));
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoDBF.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	ceprocod,\n" +
                    "	ceprodtaca,\n" +
                    "	ceprocod,\n" +
                    "	ceproqtdpa,\n" +
                    "	cepround,\n" +
                    "	ceproprzva,\n" +
                    "	ceprodes,\n" +
                    "	cepropes,\n" +
                    "	ceproqtdul,	\n" +
                    "	cepromrglu,\n" +
                    "	cepro1cus,\n" +
                    "	ceprocusfi,\n" +
                    "	cepropre1t,	\n" +
                    "	ceproncm,\n" +
                    "	cepropisco,\n" +
                    "	ceprocst\n" +
                    "from\n" +
                    "	cepro"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("ceprocod"));
                    imp.setDataCadastro(rst.getDate("ceprodtaca"));
                    imp.setEan(rst.getString("ceprocod"));
                    imp.setQtdEmbalagem(rst.getInt("ceproqtdpa"));
                    imp.setTipoEmbalagem(rst.getString("cepround"));
                    imp.setValidade(rst.getInt("ceproprzva"));
                    imp.setDescricaoCompleta(rst.getString("ceprodes"));
                    imp.setDescricaoGondola(rst.getString("ceprodes"));
                    imp.setDescricaoReduzida(rst.getString("ceprodes"));
                    imp.setPesoLiquido(rst.getDouble("cepropes"));
                    imp.setPesoBruto(rst.getDouble("cepropes"));
                    imp.setEstoque(rst.getDouble("ceproqtdul"));
                    imp.setMargem(rst.getDouble("cepromrglu"));
                    imp.setCustoSemImposto(rst.getDouble("cepro1cus"));
                    imp.setCustoComImposto(rst.getDouble("ceprocusfi"));
                    imp.setPrecovenda(rst.getDouble("cepropre1t"));
                    imp.setNcm(rst.getString("ceproncm"));
                    imp.setPiscofinsCstDebito(rst.getInt("cepropisco"));
                    imp.setIcmsCst(rst.getInt("ceprocst"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }
    
}
