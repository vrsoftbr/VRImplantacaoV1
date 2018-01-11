package vrimplantacao.dao.interfaces;

import java.io.File;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao.dao.cadastro.LojaDAO;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.dao.cadastro.ProdutoDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao.vo.vrimplantacao.CodigoAnteriorVO;
import vrimplantacao.vo.vrimplantacao.EstadoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAliquotaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAutomacaoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoComplementoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoVO;
import vrimplantacao2.dao.cadastro.produto.NcmDAO;
import vrimplantacao2.parametro.Parametros;

public class LogusParadoxDAO {

    public Map<Long, ProdutoVO> carregarCodigoBarrasEmBranco() throws Exception {
        StringBuilder sql = null;
        Statement stmPostgres = null;
        ResultSet rst;
        Map<Long, ProdutoVO> vProduto = new HashMap<>();
        int qtdeEmbalagem;
        double idProduto = 0;
        long codigobarras = -1;

        try {
            stmPostgres = Conexao.createStatement();
            sql = new StringBuilder();
            sql.append("select id, id_tipoembalagem ");
            sql.append(" from produto p ");
            sql.append(" where not exists(select pa.id from produtoautomacao pa where pa.id_produto = p.id) ");
            rst = stmPostgres.executeQuery(sql.toString());

            while (rst.next()) {

                idProduto = Double.parseDouble(rst.getString("id"));

                if ((rst.getInt("id_tipoembalagem") == 4) || (idProduto <= 9999)) {
                    codigobarras = Utils.gerarEan13((int) idProduto, false);
                } else {
                    codigobarras = Utils.gerarEan13((int) idProduto, true);
                }

                qtdeEmbalagem = 1;

                ProdutoVO oProduto = new ProdutoVO();
                oProduto.id = (int) idProduto;
                ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();
                oAutomacao.idTipoEmbalagem = rst.getInt("id_tipoembalagem");
                oAutomacao.codigoBarras = codigobarras;
                oAutomacao.qtdEmbalagem = qtdeEmbalagem;
                oProduto.vAutomacao.add(oAutomacao);
                vProduto.put(codigobarras, oProduto);
            }
            return vProduto;
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    public Map<Double, ProdutoVO> carregarProduto(String i_arquivo, int idLoja) throws Exception {
        Map<Double, ProdutoVO> vResult = new HashMap<>();
        int linha, diasValidade;
        long codProduto, codBarra;
        String tipoUnidade;
        try {
            WorkbookSettings settings = new WorkbookSettings();
            settings.setEncoding("CP1250");
            Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);
            Sheet[] sheets = arquivo.getSheets();

            int contator = 1;
            Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().carregarProdutosBalanca();
            NcmDAO ncmDAO = new NcmDAO();

            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;

                for (int i = 0; i < sheet.getRows(); i++) {
                    linha++;

                    if (linha == 1) {
                        continue;
                    } else {

                        Cell codigoProduto = sheet.getCell(0, i);
                        Cell codigoBarras = sheet.getCell(1, i);
                        Cell descricao = sheet.getCell(2, i);
                        Cell icms = sheet.getCell(3, i);
                        Cell preco = sheet.getCell(4, i);
                        Cell validade = sheet.getCell(7, i);
                        Cell unidade = sheet.getCell(8, i);
                        Cell qtdEmbalagem = sheet.getCell(9, i);
                        Cell cstPisCofins = sheet.getCell(10, i);
                        Cell ncm = sheet.getCell(12, i);
                        Cell cest = sheet.getCell(13, i);
                        Cell natReceita = sheet.getCell(14, i);

                        ProdutoVO oProduto = new ProdutoVO();
                        CodigoAnteriorVO oCodigoAnterior = new CodigoAnteriorVO();
                        ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();
                        ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                        ProdutoAliquotaVO oAliquota = new ProdutoAliquotaVO();

                        oProduto.getvCodigoAnterior().add(oCodigoAnterior);
                        oProduto.getvAutomacao().add(oAutomacao);
                        oProduto.getvComplemento().add(oComplemento);
                        oProduto.getvAliquota().add(oAliquota);

                        oProduto.setIdDouble(Double.parseDouble(Utils.formataNumero(codigoProduto.getContents().trim())));
                        oProduto.setDescricaoCompleta((descricao.getContents() == null ? "" : descricao.getContents().trim()));
                        oProduto.setDescricaoReduzida(oProduto.getDescricaoCompleta());
                        oProduto.setDescricaoGondola(oProduto.getDescricaoCompleta());

                        if ((unidade.getContents() != null)
                                && (!unidade.getContents().trim().isEmpty())) {
                            tipoUnidade = unidade.getContents().trim();
                        } else {
                            tipoUnidade = "UN";
                        }

                        oComplemento.setIdSituacaoCadastro(1);
                        oComplemento.setPrecoVenda(Double.parseDouble(preco.getContents().trim().replace(".", "").replace(",", ".")));
                        oComplemento.setPrecoDiaSeguinte(oComplemento.getPrecoVenda());
                        oComplemento.setIdLoja(idLoja);

                        if ((ncm.getContents() != null)
                                && (!ncm.getContents().trim().isEmpty())
                                && (ncm.getContents().trim().length() > 5)) {

                            vrimplantacao2.vo.enums.NcmVO oNcm = ncmDAO.getNcm(ncm.getContents().trim());

                            if (oNcm == null) {
                                oProduto.setNcm1(402);
                                oProduto.setNcm2(99);
                                oProduto.setNcm3(0);
                            } else {
                                oProduto.setNcm1(oNcm.getNcm1());
                                oProduto.setNcm2(oNcm.getNcm2());
                                oProduto.setNcm3(oNcm.getNcm3());
                            }
                        } else {
                            oProduto.setNcm1(402);
                            oProduto.setNcm2(99);
                            oProduto.setNcm3(0);
                        }

                        if ((cest.getContents() != null)
                                && (!cest.getContents().trim().isEmpty())) {

                            if (cest.getContents().trim().length() == 5) {
                                oProduto.setCest1(Integer.parseInt(cest.getContents().trim().substring(0, 1)));
                                oProduto.setCest2(Integer.parseInt(cest.getContents().trim().substring(1, 3)));
                                oProduto.setCest3(Integer.parseInt(cest.getContents().trim().substring(3, 5)));
                            } else if (cest.getContents().trim().length() == 6) {
                                oProduto.setCest1(Integer.parseInt(cest.getContents().trim().substring(0, 1)));
                                oProduto.setCest2(Integer.parseInt(cest.getContents().trim().substring(1, 4)));
                                oProduto.setCest3(Integer.parseInt(cest.getContents().trim().substring(4, 6)));
                            } else if (cest.getContents().trim().length() == 7) {
                                oProduto.setCest1(Integer.parseInt(cest.getContents().trim().substring(0, 2)));
                                oProduto.setCest2(Integer.parseInt(cest.getContents().trim().substring(2, 5)));
                                oProduto.setCest3(Integer.parseInt(cest.getContents().trim().substring(5, 7)));
                            }
                        } else {
                            oProduto.setCest1(-1);
                            oProduto.setCest2(-1);
                            oProduto.setCest3(-1);
                        }

                        if ((validade.getContents() != null)
                                && (!validade.getContents().trim().isEmpty())) {
                            diasValidade = Integer.parseInt(validade.getContents().trim());
                        } else {
                            diasValidade = 0;
                        }

                        if ((codigoBarras.getContents() != null)
                                && (!codigoBarras.getContents().trim().isEmpty())) {
                            codBarra = Long.parseLong(Utils.formataNumero(codigoBarras.getContents().trim()));
                        } else {
                            codBarra = -2;
                        }

                        //<editor-fold defaultstate="collapsed" desc="PRODUTOS DE BALANÇA E EMBALAGEM">
                        //Tratando o id da balança.
                        codProduto = Long.parseLong(codigoProduto.getContents().trim());
                        ProdutoBalancaVO produtoBalanca = null;
                        if (codProduto > 0 && codProduto <= 999999) {
                            produtoBalanca = produtosBalanca.get(
                                    Integer.parseInt(String.valueOf(codProduto).substring(0,
                                                    String.valueOf(codProduto).length() - 1)));

                            if (produtoBalanca != null) {
                                oAutomacao.setCodigoBarras(codProduto);
                                oProduto.setValidade(produtoBalanca.getValidade() >= 1 ? produtoBalanca.getValidade() : diasValidade);

                                if ("P".equals(produtoBalanca.getPesavel())) {
                                    oAutomacao.setIdTipoEmbalagem(4);
                                    oProduto.setPesavel(false);
                                } else {
                                    oAutomacao.setIdTipoEmbalagem(0);
                                    oProduto.setPesavel(true);
                                }

                                oProduto.eBalanca = true;
                                oProduto.setIdTipoEmbalagem(oAutomacao.getIdTipoEmbalagem());
                                oCodigoAnterior.setBarras(codProduto);
                                oCodigoAnterior.setCodigobalanca(produtoBalanca.getCodigo());
                                oCodigoAnterior.setE_balanca(true);
                            } else {
                                oProduto.setIdTipoEmbalagem(Utils.converteTipoEmbalagem(tipoUnidade));
                                oAutomacao.setIdTipoEmbalagem(oProduto.getIdTipoEmbalagem());

                                if (codBarra > 999999) {
                                    oAutomacao.setCodigoBarras(codBarra);
                                } else {
                                    oAutomacao.setCodigoBarras(-1);
                                }

                                oProduto.eBalanca = false;
                                oProduto.setValidade(0);
                                oProduto.setPesavel(false);
                                oCodigoAnterior.setBarras(codBarra);
                                oCodigoAnterior.setCodigobalanca(0);
                                oCodigoAnterior.setE_balanca(false);
                            }
                        } else {
                            if (codBarra > 999999) {
                                oAutomacao.setCodigoBarras(codBarra);
                            } else {
                                oAutomacao.setCodigoBarras(-2);
                            }

                            oProduto.setIdTipoEmbalagem(Utils.converteTipoEmbalagem(tipoUnidade));
                            oAutomacao.setIdTipoEmbalagem(oProduto.getIdTipoEmbalagem());

                            oProduto.eBalanca = false;
                            oProduto.setValidade(0);
                            oProduto.setPesavel(false);
                            oCodigoAnterior.setBarras(-2);
                            oCodigoAnterior.setCodigobalanca(0);
                            oCodigoAnterior.setE_balanca(false);
                        }

                        //</editor-fold>
                        
                        oProduto.setMargem(0);
                        oProduto.setQtdEmbalagem(1);
                        oProduto.setIdComprador(1);
                        oProduto.setIdFornecedorFabricante(1);
                        oProduto.setSugestaoPedido(true);
                        oProduto.setAceitaMultiplicacaoPdv(true);
                        oProduto.setSazonal(false);
                        oProduto.setFabricacaoPropria(false);
                        oProduto.setConsignado(false);
                        oProduto.setDdv(0);
                        oProduto.setPermiteTroca(true);
                        oProduto.setVendaControlada(false);
                        oProduto.setVendaPdv(true);
                        oProduto.setConferido(true);
                        oProduto.setPermiteQuebra(true);
                        oProduto.setIdTipoPisCofinsDebito(Utils.retornarPisCofinsDebito2("0".equals(cstPisCofins.getContents().trim()) ? -1 : Integer.parseInt(cstPisCofins.getContents().trim())));
                        oProduto.setIdTipoPisCofinsCredito(Utils.retornarPisCofinsCredito2("0".equals(cstPisCofins.getContents().trim()) ? -1 : Integer.parseInt(cstPisCofins.getContents().trim())));
                        oProduto.setTipoNaturezaReceita(Utils.retornarTipoNaturezaReceita(oProduto.getIdTipoPisCofins(),
                                (natReceita.getContents() == null ? "" : natReceita.getContents().trim())));

                        int idAliquota;

                        if ((icms.getContents() != null)
                                && (!icms.getContents().trim().isEmpty())) {

                            if ("F".equals(icms.getContents().trim())) {
                                idAliquota = 7;
                            } else if ("I".equals(icms.getContents().trim())) {
                                idAliquota = 6;
                            } else if ("T01".equals(icms.getContents().trim())) {
                                idAliquota = 0;
                            } else if ("T02".equals(icms.getContents().trim())) {
                                idAliquota = 1;
                            } else if ("T03".equals(icms.getContents().trim())) {
                                idAliquota = 19;
                            } else if ("T04".equals(icms.getContents().trim())) {
                                idAliquota = 3;
                            } else {
                                idAliquota = 8;
                            }
                        } else {
                            idAliquota = 8;
                        }

                        EstadoVO uf = Parametros.get().getUfPadrao();
                        oAliquota.idEstado = uf.getId();
                        oAliquota.setIdAliquotaDebito(idAliquota);
                        oAliquota.setIdAliquotaCredito(idAliquota);
                        oAliquota.setIdAliquotaDebitoForaEstado(idAliquota);
                        oAliquota.setIdAliquotaCreditoForaEstado(idAliquota);
                        oAliquota.setIdAliquotaDebitoForaEstadoNF(idAliquota);
                        oAliquota.setIdAliquotaConsumidor(idAliquota);

                        oCodigoAnterior.setCodigoanterior(oProduto.getIdDouble());

                        if ((ncm.getContents() != null)
                                && (!ncm.getContents().trim().isEmpty())) {
                            oCodigoAnterior.setNcm(ncm.getContents().trim());
                        }

                        oCodigoAnterior.setId_loja(idLoja);

                        oCodigoAnterior.setPiscofinsdebito(Integer.parseInt(cstPisCofins.getContents().trim()));
                        oCodigoAnterior.setPiscofinscredito(Integer.parseInt(cstPisCofins.getContents().trim()));
                        oCodigoAnterior.setNaturezareceita(Integer.parseInt(natReceita.getContents().trim()));
                        
                        if ((codigoBarras.getContents() != null) &&
                                (!codigoBarras.getContents().trim().isEmpty())) {
                            oCodigoAnterior.setBarras(Long.parseLong(Utils.formataNumero(codigoBarras.getContents().trim())));
                        } else {
                            oCodigoAnterior.setBarras(-2);
                        }

                        if ((natReceita.getContents() != null)
                                && (!natReceita.getContents().trim().isEmpty())) {
                            oCodigoAnterior.setRef_icmsdebito(icms.getContents().trim());
                        } else {
                            oCodigoAnterior.setRef_icmsdebito("");
                        }

                        if ((cest.getContents() != null)
                                && (!cest.getContents().trim().isEmpty())) {
                            oCodigoAnterior.setCest(Utils.formataNumero(cest.getContents().trim()));
                        } else {
                            oCodigoAnterior.setCest("");
                        }

                        vResult.put(oProduto.getIdDouble(), oProduto);

                        ProgressBar.setStatus("Carregando dados...Produto..." + contator);
                        contator++;
                    }
                }
            }
        } catch (Exception ex) {
            throw ex;
        }
        return vResult;
    }

    private List<ProdutoVO> carregarCustoProduto(String i_arquivo, int idLoja) throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        int linha;
        try {
            WorkbookSettings settings = new WorkbookSettings();
            settings.setEncoding("CP1250");
            Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);
            Sheet[] sheets = arquivo.getSheets();

            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;

                for (int i = 0; i < sheet.getRows(); i++) {
                    linha++;

                    if (linha == 1) {
                        continue;
                    }
                    
                    Cell codigoProduto = sheet.getCell(1, i);
                    Cell custoComImposto = sheet.getCell(3, i);
                    Cell custoSemImposto = sheet.getCell(18, i);
                    
                    ProdutoVO oProduto = new ProdutoVO();
                    ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                    oProduto.setIdDouble(Double.parseDouble(codigoProduto.getContents().trim()));
                    oComplemento.setIdLoja(idLoja);
                    oComplemento.setCustoComImposto(Double.parseDouble(custoComImposto.getContents().trim()));
                    oComplemento.setCustoSemImposto(Double.parseDouble(custoSemImposto.getContents().trim()));
                    oProduto.vComplemento.add(oComplemento);
                    vResult.add(oProduto);
                }
            }
            return vResult;
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    public void importarProdutoBalanca(String arquivo, int opcao) throws Exception {
        try {
            ProgressBar.setStatus("Carregando dados...Produtos de Balanca...");
            List<ProdutoBalancaVO> vProdutoBalanca = new ProdutoBalancaDAO().carregar(arquivo, opcao);
            new ProdutoBalancaDAO().salvar(vProdutoBalanca);
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void importarProduto(String i_arquivo, int idLoja) throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();
        try {
            ProgressBar.setStatus("Carregando dados...Produto...");
            Map<Double, ProdutoVO> vProduto = carregarProduto(i_arquivo, idLoja);
            ProgressBar.setMaximum(vProduto.size());
            List<LojaVO> vLoja = new LojaDAO().carregar();

            for (Double keyId : vProduto.keySet()) {
                ProdutoVO oProduto = vProduto.get(keyId);
                oProduto.idProdutoVasilhame = -1;
                oProduto.excecao = -1;
                oProduto.idTipoMercadoria = -1;
                vResult.add(oProduto);
                ProgressBar.next();
            }
            produto.usarMercadoligicoProduto = false;
            produto.implantacaoExterna = true;
            produto.salvar(vResult, idLoja, vLoja);
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    public void importarCustoProduto(String i_arquivo, int idLoja) throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Custo Produto Loja "+idLoja+"...");
            vResult = carregarCustoProduto(i_arquivo, idLoja);
            if (!vResult.isEmpty()) {
                new ProdutoDAO().alterarCustoProduto(vResult, idLoja);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    public void importarCodigoBarraEmBranco() throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();
        try {

            ProgressBar.setStatus("Carregando dados...Produtos...Codigo Barras...");
            Map<Long, ProdutoVO> vCodigoBarra = carregarCodigoBarrasEmBranco();
            ProgressBar.setMaximum(vCodigoBarra.size());

            for (Long keyId : vCodigoBarra.keySet()) {
                ProdutoVO oProduto = vCodigoBarra.get(keyId);
                vProdutoNovo.add(oProduto);
                ProgressBar.next();
            }

            produto.addCodigoBarrasEmBranco(vProdutoNovo);
        } catch (Exception ex) {
            throw ex;
        }
    }
}
