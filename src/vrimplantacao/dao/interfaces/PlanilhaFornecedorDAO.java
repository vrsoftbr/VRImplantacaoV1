package vrimplantacao.dao.interfaces;

import java.io.File;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import vrframework.classe.ProgressBar;
import vrimplantacao.dao.cadastro.FornecedorDAO;
import vrimplantacao.dao.cadastro.ProdutoFornecedorDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.FornecedorVO;
import vrimplantacao.vo.vrimplantacao.ProdutoFornecedorVO;

/**
 *
 * @author Leandro.Caires
 */
public class PlanilhaFornecedorDAO {

    private List<FornecedorVO> carregarFornecedorRabelo(String i_arquivo) throws Exception {
        List<FornecedorVO> result = new ArrayList<>();

        WorkbookSettings settings = new WorkbookSettings();
        settings.setEncoding("CP1250");

        Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);

        Sheet sheet = arquivo.getSheets()[0];

        for (int i = 0; i < sheet.getRows(); i++) {
            FornecedorVO oFornecedor = new FornecedorVO();

            Cell cellCodigo = sheet.getCell(0, i);
            Cell cellIdTipoInscricao = sheet.getCell(1, i);
            Cell cellCnpj = sheet.getCell(2, i);
            Cell cellRazao = sheet.getCell(3, i);
            Cell cellFantasia = sheet.getCell(4, i);
            Cell cellInscricaoEstadual = sheet.getCell(5, i);
            Cell cellInscrMunicip = sheet.getCell(6, i);
            Cell cellEndereco = sheet.getCell(7, i);
            Cell cellNumero = sheet.getCell(8, i);
            Cell cellComplemento = sheet.getCell(9, i);
            Cell cellBairro = sheet.getCell(10, i);
            Cell cellCep = sheet.getCell(11, i);
            Cell cellCidade = sheet.getCell(12, i);
            Cell cellUf = sheet.getCell(13, i);
            Cell cellTelefone = sheet.getCell(14, i);
            Cell cellEmail = sheet.getCell(15, i);

            oFornecedor.setId(Utils.stringToInt(cellCodigo.getContents()));
            oFornecedor.setDatacadastro(Utils.getDataAtual());
            oFornecedor.setCodigoanterior(Utils.stringToInt(cellCodigo.getContents()));
            oFornecedor.setRazaosocial(cellRazao.getContents());
            oFornecedor.setNomefantasia(cellFantasia.getContents());
            oFornecedor.setEndereco(cellEndereco.getContents());
            oFornecedor.setNumero(cellNumero.getContents());
            oFornecedor.setComplemento(cellComplemento.getContents());
            oFornecedor.setBairro(cellBairro.getContents());
            oFornecedor.setId_estado(Utils.getEstadoPelaSigla(cellUf.getContents()));
            oFornecedor.setId_municipio(Utils.retornarMunicipioIBGEDescricao(cellCidade.getContents(), cellUf.getContents()));
            oFornecedor.setCep(Utils.stringToInt(cellCep.getContents()));
            oFornecedor.setTelefone(cellTelefone.getContents());
            oFornecedor.setInscricaoestadual(cellInscricaoEstadual.getContents());
            oFornecedor.setInscricaomunicipal(cellInscrMunicip.getContents());
            oFornecedor.setCnpj(Utils.stringToLong(cellCnpj.getContents(), -1));
            switch (Utils.acertarTexto(cellIdTipoInscricao.getContents())) {
                case "FISICA":
                    oFornecedor.setId_tipoinscricao(1);
                    break;
                default:
                    oFornecedor.setId_tipoinscricao(0);
                    break;
            }
            oFornecedor.setObservacao("IMPORTADO VR");
            oFornecedor.setEmail(cellEmail.getContents());
            oFornecedor.setId_situacaocadastro(1);
            oFornecedor.setId_tipoindicadorie();

            result.add(oFornecedor);
        }

        return result;
    }

    public void importarFornecedorRabelo(String arquivo, int idLojaVR) throws Exception {
        ProgressBar.setStatus("Carregando dados...Fornecedor...");
        List<FornecedorVO> vFornecedor = carregarFornecedorRabelo(arquivo);

        new FornecedorDAO().salvar(vFornecedor, idLojaVR);
    }

    private List<ProdutoFornecedorVO> carregarProdutoFornecedor(String i_arquivo) throws Exception {
        List<ProdutoFornecedorVO> vResult = new ArrayList<>();
        WorkbookSettings settings = new WorkbookSettings();
        settings.setEncoding("CP1250");
        Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);
        Sheet sheet = arquivo.getSheets()[0];
        int linha = 0;
        java.sql.Date dataAlteracao;
        DateFormat fmt = new SimpleDateFormat("yyyy/MM/dd");
        

        for (int i = 0; i < sheet.getRows(); i++) {
            linha++;
            if (linha == 1) {
                continue;
            }
            
            Cell cellCodigoProduto = sheet.getCell(0, i);
            Cell cellCodigoFornecedor = sheet.getCell(1, i);
            Cell cellCodigoExterno = sheet.getCell(2, i);

            dataAlteracao = new Date(new java.util.Date().getTime());
            
            ProdutoFornecedorVO vo = new ProdutoFornecedorVO();
            vo.setId_fornecedorDouble(Double.parseDouble(cellCodigoFornecedor.getContents().trim()));
            vo.setId_produtoDouble(Double.parseDouble(cellCodigoProduto.getContents().trim()));
            vo.setCodigoexterno(Utils.acertarTexto(cellCodigoExterno.getContents().trim()));
            vo.setId_produtoStr(cellCodigoProduto.getContents().trim());
            vo.setDataalteracao(dataAlteracao);
            vResult.add(vo);            
        }
        return vResult;
    }

    public void importarProdutoFornecedor(String i_arquivo) throws Exception {
        List<ProdutoFornecedorVO> vResult = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Produto Fornecedor");
            vResult = carregarProdutoFornecedor(i_arquivo);
            if (!vResult.isEmpty()) {
                new ProdutoFornecedorDAO().salvar2(vResult);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

}
