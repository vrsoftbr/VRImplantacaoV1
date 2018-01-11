package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoParadox;
import vrimplantacao.utils.Utils;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.importacao.ProdutoIMP;

public class BrainSoftDAO extends InterfaceDAO {

    private final ConexaoParadox connParadox = new ConexaoParadox();
    public String pathBancoParadox;    

    @Override
    public String getSistema() {
        return "BrainSoft";
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> vResult = new ArrayList<>();
        ConexaoParadox.usarOdbc = true;
        connParadox.abrirConexao(pathBancoParadox);
        try (Statement stm = connParadox.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "P.CODPROD, P.ATIVO, P.ESTOQUE, P.DESCRICAO, P.VALIDADE, "
                    + "P.NOMEGENERICO, P.PRECOVENDA, P.PRECOCUSTO, P.UNIDMED, "
                    + "P.CLASFISCAL, P.ESTOQMIN, P.CODEAN, P.ST, P.ALIQICMS, "
                    + "P.REDBASE, P.PORCCOMIS, P.REDBASEFORA, P.REDALICFORA, "
                    + "P.DATAINC, M.PERCVENDA, P.TIPO "
                    + "  from PRODUTOS P "
                    + "  left join CADMARKP M on M.CODIGO = P.CODMARKP "
                    + " order by P.CODPROD"
            )) {
                while (rst.next()) {                    
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(Utils.formataNumero(Utils.formataNumero(rst.getString("CODPROD"))));
                    imp.setEan(Utils.formataNumero(Utils.formataNumero(rst.getString("CODEAN"))));
                    imp.setDescricaoCompleta(rst.getString("DESCRICAO"));
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setTipoEmbalagem(rst.getString("UNIDMED"));
                    imp.setQtdEmbalagem(1);
                    imp.setSituacaoCadastro(SituacaoCadastro.ATIVO);
                    imp.setValidade(rst.getInt("VALIDADE"));
                    imp.setPrecovenda(rst.getDouble("PRECOVENDA"));
                    imp.setCustoComImposto(rst.getDouble("PRECOCUSTO"));
                    imp.setCustoSemImposto(rst.getDouble("PRECOCUSTO"));
                    imp.setEstoque(rst.getDouble("ESTOQUE"));
                    imp.setEstoqueMinimo(rst.getDouble("ESTOQMIN"));
                    imp.setIcmsCst(rst.getInt("ST"));
                    imp.setIcmsAliq(rst.getDouble("ALIQICMS"));
                    imp.setIcmsReducao(rst.getDouble("REDBASE"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }
}
