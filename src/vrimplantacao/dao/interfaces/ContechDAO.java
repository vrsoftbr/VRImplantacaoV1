package vrimplantacao.dao.interfaces;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao.dao.cadastro.ClientePreferencialDAO;
import vrimplantacao.dao.cadastro.FamiliaProdutoDAO;
import vrimplantacao.dao.cadastro.FornecedorDAO;
import vrimplantacao.dao.cadastro.LojaDAO;
import vrimplantacao.dao.cadastro.MercadologicoDAO;
import vrimplantacao.dao.cadastro.NcmDAO;
import vrimplantacao.dao.cadastro.OfertaDAO;
import vrimplantacao.dao.cadastro.PlanoDAO;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.dao.cadastro.ProdutoDAO;
import vrimplantacao.dao.cadastro.ReceberChequeDAO;
import vrimplantacao.dao.cadastro.ReceberCreditoRotativoDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao.vo.vrimplantacao.ClientePreferencialVO;
import vrimplantacao.vo.vrimplantacao.CodigoAnteriorVO;
import vrimplantacao.vo.vrimplantacao.FamiliaProdutoVO;
import vrimplantacao.vo.vrimplantacao.FornecedorVO;
import vrimplantacao.vo.vrimplantacao.MercadologicoVO;
import vrimplantacao.vo.vrimplantacao.NcmVO;
import vrimplantacao.vo.vrimplantacao.OfertaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAliquotaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAutomacaoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoComplementoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoVO;
import vrimplantacao.vo.vrimplantacao.ReceberChequeVO;
import vrimplantacao.vo.vrimplantacao.ReceberCreditoRotativoVO;

public class ContechDAO {

    // IMPORTAÇÕES
    
    public void importarProdutosMercadologico(String arquivo) throws Exception {
        try {
            ProgressBar.setStatus("Carregando dados...Produto Mercadologico...");
            List<ProdutoVO> vMercadologico = carregarProdutosMercadologico(arquivo);
            
            ProdutoDAO produtoDAO = new ProdutoDAO();
            //produtoDAO.alterarProdutoMercadologicoContech(vMercadologico);
        } catch(Exception ex) {
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
    
    public void importarFamiliaProduto(String arquivo) throws Exception {        
        try {
            ProgressBar.setStatus("Carregando dados...Familia Produto...");
            List<FamiliaProdutoVO> vFamiliaProduto = carregarFamiliaProduto(arquivo);
            
            FamiliaProdutoDAO familiaProdutoDAO = new FamiliaProdutoDAO();
            familiaProdutoDAO.gerarCodigo = false;
            familiaProdutoDAO.salvar(vFamiliaProduto);
        } catch(Exception ex) {
            throw ex;
        }
    }
    
    public void importarMercadologico(String arquivo) throws Exception {        

        List<MercadologicoVO> vMercadologicoDAO =  new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Mercadologico...");
            
            vMercadologicoDAO = carregarMercadologico1(arquivo);
            new MercadologicoDAO().salvar(vMercadologicoDAO, false);

            //vMercadologicoDAO = carregarMercadologico(arquivo, 2);
            //new MercadologicoDAO().salvar(vMercadologicoDAO, false);

            //vMercadologicoDAO = carregarMercadologico(arquivo, 3);
            //new MercadologicoDAO().salvar(vMercadologicoDAO, false);
            
        } catch(Exception ex) {
            throw ex;
        }
    }

    public void importarMercadologico2(String arquivo) throws Exception {        

        List<MercadologicoVO> vMercadologicoDAO =  new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Mercadologico 2...");
            
            vMercadologicoDAO = carregarMercadologico2(arquivo);
            new MercadologicoDAO().salvar(vMercadologicoDAO, false);

        } catch(Exception ex) {
            throw ex;
        }
    }

    public void importarMercadologico3(String arquivo) throws Exception {        

        List<MercadologicoVO> vMercadologicoDAO =  new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Mercadologico 3...");
            
            vMercadologicoDAO = carregarMercadologico3(arquivo);
            new MercadologicoDAO().salvar(vMercadologicoDAO, false);

        } catch(Exception ex) {
            throw ex;
        }
    }
    
    public void importarProduto(String arquivo, int idLoja) throws Exception {
        
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();
        
        try {
            
            ProgressBar.setStatus("Carregando dados...Produtos...");
            Map<Integer, ProdutoVO> vProduto = carregarProduto(arquivo);
            
            List<LojaVO> vLoja = new LojaDAO().carregar();
            
            ProgressBar.setMaximum(vProduto.size());
            
            for (Integer keyId : vProduto.keySet()) {
                
                ProdutoVO oProduto = vProduto.get(keyId);

                oProduto.idProdutoVasilhame = -1;
                oProduto.excecao = -1;
                oProduto.idTipoMercadoria = -1;

                vProdutoNovo.add(oProduto);
                
                
                ProgressBar.next();
            }
            
            produto.implantacaoExterna = true;
            produto.salvar(vProdutoNovo, idLoja, vLoja);
            
        } catch(Exception ex) {
            
            throw ex;
        }
    }

    public void importarProdutoPrecoVendaMercadologico(String arquivo, int idLoja) throws Exception {
        
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();
        
        try {
            
            ProgressBar.setStatus("Carregando dados...Preço e Mercadologico...");
            Map<Integer, ProdutoVO> vProduto = carregarProdutoPrecoMercadologico(arquivo, idLoja);
            
            ProgressBar.setMaximum(vProduto.size());
            
            for (Integer keyId : vProduto.keySet()) {
                
                ProdutoVO oProduto = vProduto.get(keyId);


                vProdutoNovo.add(oProduto);
                
                
                ProgressBar.next();
            }
            
            produto.alterarPrecoVendaMercadologicoContech(vProdutoNovo, idLoja);
            
        } catch(Exception ex) {
            
            throw ex;
        }
    }

    public void importarProdutosInativos(String arquivo, int idLoja) throws Exception {
        
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();
        
        try {
            
            ProgressBar.setStatus("Carregando dados...Produtos Inativos...");
            Map<Integer, ProdutoVO> vProduto = carregarProdutosInativos(arquivo, idLoja);
            
            ProgressBar.setMaximum(vProduto.size());
            
            for (Integer keyId : vProduto.keySet()) {
                
                ProdutoVO oProduto = vProduto.get(keyId);


                vProdutoNovo.add(oProduto);
                
                
                ProgressBar.next();
            }
            
            produto.alterarSituacaoCadastroProduto(vProdutoNovo, idLoja);
            
        } catch(Exception ex) {
            
            throw ex;
        }
    }

    public void importarOferta(String arquivo, int idLoja) throws Exception {
        try {
            ProgressBar.setStatus("Carregando dados...Produtos em Oferta...");
            List<OfertaVO> vOferta = carregarProdutosOferta(arquivo, idLoja);
            
            OfertaDAO ofertaDAO = new OfertaDAO();
            ofertaDAO.salvar(vOferta, idLoja);
            
        } catch(Exception ex) {
            throw ex;
        }
    }
    
    public void importarFornecedor(String arquivo) throws Exception {

        try {

            ProgressBar.setStatus("Carregando dados...Fornecedor...");
            List<FornecedorVO> vFornecedor = carregarFornecedor(arquivo);

            new FornecedorDAO().salvar(vFornecedor);

        } catch (Exception ex) {

            throw ex;
        }
    }
    
    public void importarClientePreferencial(String arquivo, int id_loja) throws Exception {

        try {

            ProgressBar.setStatus("Carregando dados...Cliente Preferencial...");
            List<ClientePreferencialVO> vClientePreferencial = carregarClientePreferencial(arquivo);

            new PlanoDAO().salvar(id_loja);
            new ClientePreferencialDAO().salvar(vClientePreferencial, 0,0);

        } catch (Exception ex) {
            throw ex;
        }
    }

    public void importarProdutoNcmPisCofins(String arquivo) throws Exception {        
        try {
            ProgressBar.setStatus("Carregando dados...Produto...Ncm, Pis Cofins");
            List<ProdutoVO> vProduto = carregarProdutoNcmPisCofins(arquivo);

            new ProdutoDAO().alterarNcmPisCofinsContech(vProduto);
        } catch(Exception ex) {
            throw ex;
        }
    }

    public void importarProdutoNcm(String arquivo) throws Exception {        
        try {
            ProgressBar.setStatus("Carregando dados...Ncm Produto...");
            List<ProdutoVO> vProduto = carregarProdutoNcm(arquivo);

            new ProdutoDAO().alterarNcmContech(vProduto);
        } catch(Exception ex) {
            throw ex;
        }
    }
    
    public void importarReceberCreditoRotativo(String arquivo, int idLoja) throws Exception {
        try {
            ProgressBar.setStatus("Carregando dados...Receber Crédito Rotativo...");
            List<ReceberCreditoRotativoVO> vRotativo = carregarReceberCreditoRotativo(arquivo, idLoja);
            
            new ReceberCreditoRotativoDAO().salvar(vRotativo, idLoja);
        } catch(Exception ex) {
            throw ex;
        }
    }
    
    public void importarClientesLimite(String arquivo) throws Exception {
        try {
            ProgressBar.setStatus("Carregando dados...Clientes Valor Limite...");
            List<ClientePreferencialVO> vClientePreferencial = carregarClienteLimite(arquivo);
            
            new ClientePreferencialDAO().alterarLimiteClienteContech(vClientePreferencial);
            
        } catch(Exception ex) {
            throw ex;
        }
    }
    
    public void importarReceberCheque(String arquivo, int id_loja) throws Exception {

        try {

            ProgressBar.setStatus("Carregando dados...Receber Cheque...");
            List<ReceberChequeVO> vReceberCheque = carregarReceberCheque(arquivo, id_loja);

            new ReceberChequeDAO().salvar(vReceberCheque,id_loja);

        } catch (Exception ex) {

            throw ex;
        }
    }      
    
    // CARREGAMENTOS
    
    public List<FamiliaProdutoVO> carregarFamiliaProduto(String arquivo) throws Exception {
        BufferedReader br = null;
        StringBuilder linha = null;
        String strPode = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789 /?!@#$%&*()-_=+[{]}:.>,<'|'",
                descFamilia = "", descFamiliaAux = "";
        Utils util = new Utils();
        int cont = 1;        
        
        List<FamiliaProdutoVO> vFamiliaProduto = new ArrayList<>();
        
        try {
            
            br = new BufferedReader(new InputStreamReader(new FileInputStream(arquivo), "UTF-8"));

            linha = new StringBuilder();
            linha.append(br.readLine());
            
            while (linha != null) {
                            
                for (int i = 0; i < linha.toString().length(); i++) {                    

                    if (strPode.indexOf(linha.toString().charAt(i)) != -1) {
                        descFamilia = descFamilia + linha.toString().charAt(i);
                    } else {
                        descFamiliaAux = descFamilia.trim();
                        descFamilia = "";
                        
                        if ((!descFamiliaAux.isEmpty()) &&
                                (descFamiliaAux.trim().length() >= 4) &&
                                (!"ABD01.00".equals(descFamiliaAux)) &&
                                (!"FAMILIA".equals(descFamiliaAux))) {
                            
                            FamiliaProdutoVO oFamiliaProduto = new FamiliaProdutoVO();
                            
                            descFamiliaAux = util.acertarTexto(descFamiliaAux.trim());
                            
                            oFamiliaProduto.id = cont;
                            oFamiliaProduto.descricao = descFamiliaAux;
                            
                            vFamiliaProduto.add(oFamiliaProduto);
                            
                            cont = cont + 1;
                        }
                    }
                }                
                linha = null;
            }
            
            return vFamiliaProduto;
        } catch(Exception ex) {
            throw ex;
        }        
    }
    
    public List<MercadologicoVO> carregarMercadologico(String arquivo, int nivel) throws Exception {
        BufferedReader br = null;
        StringBuilder linha = null;
        String strPode = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789 /?!@#$%&*()-_=+[{]}:.>,<'|'",
                descricao = "", descricaoAux = "";
        Utils util = new Utils();
        int contador = 1;
        
        
        List<MercadologicoVO> vMercadologico = new ArrayList<>();
        
        try {
            
            br = new BufferedReader(new InputStreamReader(new FileInputStream(arquivo), "UTF-8"));

            linha = new StringBuilder();
            linha.append(br.readLine());
            
            while (linha != null) {
                            
                for (int i = 0; i < linha.toString().length(); i++) {                    

                    if (strPode.indexOf(linha.toString().charAt(i)) != -1) {
                        descricao = descricao + linha.toString().charAt(i);
                    } else {
                        descricaoAux = descricao.trim();
                        descricao = "";
                        
                        if ((!descricaoAux.isEmpty()) &&
                                (descricaoAux.trim().length() >= 3) &&
                                (!"ABD01.00".equals(descricaoAux)) &&
                                (!"DEPARTAMENTO".equals(descricaoAux)) &&
                                (!"DESCONTO".equals(descricaoAux)) &&
                                (!"MARIA".equals(descricaoAux)) &&
                                (!"LEO".equals(descricaoAux)) &&
                                (!"ROL".equals(descricaoAux))) {
                            
                            MercadologicoVO oMercadologico = new MercadologicoVO();
                            
                            descricaoAux = util.acertarTexto(descricaoAux.trim());
                            
                            if (nivel == 1) {
                                oMercadologico.mercadologico1 = contador;
                                oMercadologico.mercadologico2 = 0;
                                oMercadologico.mercadologico3 = 0;
                                oMercadologico.mercadologico4 = 0;
                                oMercadologico.mercadologico5 = 0;
                                oMercadologico.nivel = 1;
                                oMercadologico.descricao = descricaoAux;
                                vMercadologico.add(oMercadologico);
                            } else if (nivel == 2) {
                                oMercadologico.mercadologico1 = contador;
                                oMercadologico.mercadologico2 = contador;
                                oMercadologico.mercadologico3 = 0;
                                oMercadologico.mercadologico4 = 0;
                                oMercadologico.mercadologico5 = 0;
                                oMercadologico.nivel = 2;
                                vMercadologico.add(oMercadologico);
                                oMercadologico.descricao = descricaoAux;
                            } else if (nivel == 3) {                            
                                oMercadologico.mercadologico1 = contador;
                                oMercadologico.mercadologico2 = contador;
                                oMercadologico.mercadologico3 = contador;
                                oMercadologico.mercadologico4 = 0;
                                oMercadologico.mercadologico5 = 0;
                                oMercadologico.nivel = 3;
                                oMercadologico.descricao = descricaoAux;
                                vMercadologico.add(oMercadologico);
                            }
                            
                            contador = contador + 1;
                        }
                    }
                }                
                linha = null;
            }
            
            return vMercadologico;
        } catch(Exception ex) {
            throw ex;
        }        
    }
    
    public List<MercadologicoVO> carregarMercadologico1(String i_arquivo) throws Exception {
        List<MercadologicoVO> vMercadologico = new ArrayList<>();
        int linha, mercad1;
        String descr = "";
        Utils util = new Utils();
        WorkbookSettings settings = new WorkbookSettings();
        settings.setEncoding("CP1250");

        Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);

        Sheet[] sheets = arquivo.getSheets();

        try {
            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;
                
                for (int i = 0; i < sheet.getRows(); i++) {                    
                    linha++;
                    
                     if (linha == 1) {
                        continue;

                    } else {
                         
                         Cell cellMercadologico1 = sheet.getCell(0, i);
                         Cell cellDescricao = sheet.getCell(0, i);
                         
                         mercad1 = Integer.parseInt(util.formataNumero(cellMercadologico1.getContents().trim()));
                         descr = util.acertarTexto(cellDescricao.getContents().trim().substring(
                                 cellDescricao.getContents().indexOf("-") + 1).replace("'", ""));
                         
                         MercadologicoVO oMercadologico = new MercadologicoVO();
                         
                         oMercadologico.mercadologico1 = mercad1;
                         oMercadologico.mercadologico2 = 0;
                         oMercadologico.mercadologico3 = 0;
                         oMercadologico.mercadologico4 = 0;
                         oMercadologico.mercadologico5 = 0;
                         oMercadologico.nivel = 1;
                         oMercadologico.descricao = descr;
                         vMercadologico.add(oMercadologico);
                         
                    }
                }
            }
            
            return vMercadologico;
        } catch(Exception ex) {
            throw ex;
        }
            
    }

    public List<MercadologicoVO> carregarMercadologico2(String i_arquivo) throws Exception {
        List<MercadologicoVO> vMercadologico = new ArrayList<>();
        int linha, mercad1, mercad2 = 0;
        String descr = "";
        Utils util = new Utils();
        WorkbookSettings settings = new WorkbookSettings();
        settings.setEncoding("CP1250");

        Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);

        Sheet[] sheets = arquivo.getSheets();

        try {
            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;
                
                for (int i = 0; i < sheet.getRows(); i++) {                    
                    linha++;
                    
                     if (linha == 1) {
                        continue;

                    } else {
                         
                         Cell cellMercadologico1 = sheet.getCell(0, i);
                         Cell cellMercadologico2 = sheet.getCell(1, i);
                         Cell cellDescricao = sheet.getCell(1, i);
                         
                         if (Integer.parseInt(util.formataNumero(cellMercadologico2.getContents().trim())) > 0) {
                         
                             mercad1 = Integer.parseInt(util.formataNumero(cellMercadologico1.getContents().trim()));
                             mercad2 = Integer.parseInt(util.formataNumero(cellMercadologico2.getContents().trim()));

                             descr = util.acertarTexto(cellDescricao.getContents().trim().substring(
                                     cellDescricao.getContents().indexOf("-") + 1).replace("'", ""));

                             MercadologicoVO oMercadologico = new MercadologicoVO();

                             oMercadologico.mercadologico1 = mercad1;
                             oMercadologico.mercadologico2 = mercad2;
                             oMercadologico.mercadologico3 = 0;
                             oMercadologico.mercadologico4 = 0;
                             oMercadologico.mercadologico5 = 0;
                             oMercadologico.nivel = 2;
                             oMercadologico.descricao = descr;
                             vMercadologico.add(oMercadologico);
                         
                         }
                         
                    }
                }
            }
            
            return vMercadologico;
        } catch(Exception ex) {
            throw ex;
        }
            
    }

    public List<MercadologicoVO> carregarMercadologico3(String i_arquivo) throws Exception {
        List<MercadologicoVO> vMercadologico = new ArrayList<>();
        int linha, mercad1, mercad2 = 0, mercad3;
        String descr = "";
        Utils util = new Utils();
        WorkbookSettings settings = new WorkbookSettings();
        settings.setEncoding("CP1250");

        Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);

        Sheet[] sheets = arquivo.getSheets();

        try {
            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;
                
                for (int i = 0; i < sheet.getRows(); i++) {                    
                    linha++;
                    
                     if (linha == 1) {
                        continue;

                    } else {
                         
                         Cell cellMercadologico1 = sheet.getCell(0, i);
                         Cell cellMercadologico2 = sheet.getCell(1, i);
                         Cell cellMercadologico3 = sheet.getCell(1, i);
                         Cell cellDescricao = sheet.getCell(1, i);
                         
                         if (Integer.parseInt(util.formataNumero(cellMercadologico3.getContents().trim())) > 0) {
                         
                             mercad1 = Integer.parseInt(util.formataNumero(cellMercadologico1.getContents().trim()));
                             mercad2 = Integer.parseInt(util.formataNumero(cellMercadologico2.getContents().trim()));
                             mercad3 = Integer.parseInt(util.formataNumero(cellMercadologico3.getContents().trim()));

                             descr = util.acertarTexto(cellDescricao.getContents().trim().substring(
                                     cellDescricao.getContents().indexOf("-") + 1).replace("'", ""));

                             MercadologicoVO oMercadologico = new MercadologicoVO();

                             oMercadologico.mercadologico1 = mercad1;
                             oMercadologico.mercadologico2 = mercad2;
                             oMercadologico.mercadologico3 = mercad3;
                             oMercadologico.mercadologico4 = 0;
                             oMercadologico.mercadologico5 = 0;
                             oMercadologico.nivel = 3;
                             oMercadologico.descricao = descr;
                             vMercadologico.add(oMercadologico);
                         
                         }
                         
                    }
                }
            }
            
            return vMercadologico;
        } catch(Exception ex) {
            throw ex;
        }
            
    }
    
    
    public Map<Integer, ProdutoVO> carregarProduto(String i_arquivo) throws Exception {
        Map<Integer, ProdutoVO> vProduto = new HashMap<>();
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        Utils util = new Utils();
        int idTipoEmbalagem = 0, qtdEmbalagem, idTipoPisCofins, idTipoPisCofinsCredito, tipoNaturezaReceita,
               idAliquota, idFamilia, mercadologico1, mercadologico2, mercadologico3, idSituacaoCadastro, 
               ncm1, ncm2, ncm3, codigoBalanca, referencia = -1, idProduto, validade, linha = 0;
        String descriaoCompleta, descricaoReduzida, descricaoGondola, ncmAtual, strCodigoBarras;
        boolean eBalanca, pesavel = false, bPesavel = false;
        long codigoBarras = 0;
        double precoVenda, custo, margem, codigoAnterior = 0, estoque = 0;
        
        WorkbookSettings settings = new WorkbookSettings();
        settings.setEncoding("CP1250");

        Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);

        Sheet[] sheets = arquivo.getSheets();
        
        try {
            Conexao.begin();
            
            stm = Conexao.createStatement();
                  
            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;
                
                for (int i = 0; i < sheet.getRows(); i++) {                    
                    linha++;
                    
                    if ("--------".equals(sheet.getCell(0, i).getContents().trim()) ||
                            ("LISTA GE".equals(sheet.getCell(0, i).getContents().trim())) ||
                            ("SUPERMER".equals(sheet.getCell(0, i).getContents().trim())) ||
                            ("--------".equals(sheet.getCell(0, i).getContents().trim())) ||
                            ("TOTAL DE".equals(sheet.getCell(0, i).getContents().trim())) ||
                            ("CODIGO".equals(sheet.getCell(0, i).getContents().trim()))) {
                        
                        continue;
                    } else {
                        
                        Cell cellIdProduto = sheet.getCell(0, i);
                        Cell cellCodigoBarras = sheet.getCell(1, i);
                        Cell cellDescricao = sheet.getCell(2, i);
                        Cell cellIcms = sheet.getCell(3, i);
                        Cell cellCusto = sheet.getCell(6, i);
                        Cell cellPrecoVenda = sheet.getCell(7, i);
                        Cell cellMargem = sheet.getCell(8, i);
                        Cell cellPesavel = sheet.getCell(9, i);
 
                        idProduto = Integer.parseInt(cellIdProduto.getContents().trim());
                        codigoAnterior = Double.parseDouble(cellIdProduto.getContents().trim());
                        qtdEmbalagem = 1;
                        idTipoPisCofins = 0;
                        idTipoPisCofinsCredito = 12;
                        tipoNaturezaReceita = -1;
                        idSituacaoCadastro = 1;
                        idFamilia = -1;
                        ncm1 = -1;
                        ncm2 = -1;
                        ncm3 = -1;                        
                        mercadologico1 = 178;
                        mercadologico2 = 1;
                        mercadologico3 = 1;

                        if ((cellCodigoBarras.getContents() != null) &&
                                (!cellCodigoBarras.getContents().trim().isEmpty()) &&
                                (util.encontrouLetraCampoNumerico(cellCodigoBarras.getContents()) == false)) {
                            
                            if (cellCodigoBarras.getContents().contains("/")) {
                                
                                strCodigoBarras = cellCodigoBarras.getContents().substring(0, 
                                        cellCodigoBarras.getContents().indexOf("/"));
                                
                            } else {
                            
                                strCodigoBarras = util.formataNumero(cellCodigoBarras.getContents().trim());
                                strCodigoBarras = cellCodigoBarras.getContents().substring(0,
                                        cellCodigoBarras.getContents().length() - 3);
                            
                            }
                            

                        } else {
                            strCodigoBarras = "";                            
                        }
                        
                        if ((cellPesavel.getContents() != null) &&
                                (!cellPesavel.getContents().trim().isEmpty())) {
                            
                            if ("N".equals(cellPesavel.getContents().trim())) {
                                bPesavel = false;
                            } else {
                                bPesavel = true;
                            }            
                        } else {
                            bPesavel = false;
                        }
                        
                        
                        if (!"".equals(strCodigoBarras) && 
                                (bPesavel == true)) {
                        
                            sql = new StringBuilder();
                            sql.append("select codigo, descricao, pesavel, validade ");
                            sql.append("from implantacao.produtobalanca ");
                            sql.append("where codigo = "+Long.parseLong(strCodigoBarras));

                            rst = stm.executeQuery(sql.toString());

                            if (rst.next()) {

                                codigoBalanca = rst.getInt("codigo");
                                validade = rst.getInt("validade");
                                eBalanca = true;

                                if ("P".equals(rst.getString("pesavel"))) {
                                    idTipoEmbalagem = 4;
                                    pesavel = false;
                                } else {
                                    idTipoEmbalagem = 0;
                                    pesavel = true;
                                }

                            } else {
                                codigoBalanca = -1;
                                validade = 0;
                                pesavel = false;
                                eBalanca = false;
                                idTipoEmbalagem = 0;
                            }                                                
                        } else {
                            codigoBalanca = -1;
                            validade = 0;
                            pesavel = false;
                            eBalanca = false;
                            idTipoEmbalagem = 0;
                        }
                        
                        if ((cellDescricao.getContents() != null) &&
                                (!cellDescricao.getContents().trim().isEmpty())) {
                            descriaoCompleta = util.acertarTexto(cellDescricao.getContents().trim().replace("'", ""));
                            
                            if (descriaoCompleta.contains("BOVINO-")) {
                                idTipoEmbalagem = 4;
                            }
                        } else {
                            descriaoCompleta = "PRODUTO SEM DESCRICAO";
                        }
                        
                        descricaoReduzida = descriaoCompleta;
                        descricaoGondola = descriaoCompleta;
                                                
                        if (eBalanca == true) {
                            codigoBarras = idProduto;
                        } else {
                            
                            if (strCodigoBarras.isEmpty()) {
                                codigoBarras = -1;
                            } else {
                                if (strCodigoBarras.length() < 7) {
                                    codigoBarras = -1;
                                } else if (strCodigoBarras.length() > 14) {
                                    strCodigoBarras = strCodigoBarras.substring(0, 14);
                                    codigoBarras = Long.parseLong(strCodigoBarras); 
                                } else {
                                    codigoBarras = Long.parseLong(strCodigoBarras);
                                }
                            }
                        }
                                                
                        if ((cellIcms.getContents() != null) &&
                                (!cellIcms.getContents().trim().isEmpty())) {
                            idAliquota = retornarAliquota(cellIcms.getContents().trim());
                        } else {
                            idAliquota = 8;
                        }
                        
                        if ((cellCusto.getContents() != null) &&
                                (!cellCusto.getContents().trim().isEmpty())) {
                            custo = Double.parseDouble(util.retirarLetra(cellCusto.getContents().trim()));
                        } else {
                            custo = 0;
                        }
                        
                        if ((cellPrecoVenda.getContents() != null) &&
                                (!cellPrecoVenda.getContents().trim().isEmpty())) {
                            precoVenda = Double.parseDouble(util.retirarLetra(cellPrecoVenda.getContents().trim()));
                        } else {
                            precoVenda = 0;
                        }
                        
                        if ((cellMargem.getContents() != null) &&
                                (!cellMargem.getContents().trim().isEmpty())) {
                            margem = Double.parseDouble(util.retirarLetra(cellMargem.getContents().trim()));
                        } else {
                            margem = 0;
                        }
                        
                        if (descriaoCompleta.length() > 60) {

                            descriaoCompleta = descriaoCompleta.substring(0, 60);
                        }

                        if (descricaoReduzida.length() > 22) {

                            descricaoReduzida = descricaoReduzida.substring(0, 22);
                        }

                        if (descricaoGondola.length() > 60) {

                            descricaoGondola = descricaoGondola.substring(0, 60);
                        }
                
                        ProdutoVO oProduto = new ProdutoVO();                        
                        oProduto.id = idProduto;
                        oProduto.descricaoCompleta = descriaoCompleta;
                        oProduto.descricaoReduzida = descricaoReduzida;
                        oProduto.descricaoGondola = descricaoGondola;
                        oProduto.idTipoEmbalagem = idTipoEmbalagem;
                        oProduto.qtdEmbalagem = qtdEmbalagem;
                        oProduto.idTipoPisCofinsDebito = idTipoPisCofins;
                        oProduto.idTipoPisCofinsCredito = idTipoPisCofinsCredito;
                        oProduto.tipoNaturezaReceita = tipoNaturezaReceita;
                        oProduto.pesavel = pesavel;
                        oProduto.mercadologico1 = mercadologico1;
                        oProduto.mercadologico2 = mercadologico2;
                        oProduto.mercadologico3 = mercadologico3;
                        oProduto.ncm1 = ncm1;
                        oProduto.ncm2 = ncm2;
                        oProduto.ncm3 = ncm3;
                        oProduto.idFamiliaProduto = idFamilia;
                        oProduto.idFornecedorFabricante = 1;
                        oProduto.sugestaoPedido = true;
                        oProduto.aceitaMultiplicacaoPdv = true;
                        oProduto.sazonal = false;
                        oProduto.fabricacaoPropria = false;
                        oProduto.consignado = false;
                        oProduto.ddv = 0;
                        oProduto.permiteTroca = true;
                        oProduto.vendaControlada = false;
                        oProduto.vendaPdv = true;
                        oProduto.conferido = true;
                        oProduto.permiteQuebra = true;
                        oProduto.permitePerda = true;
                        oProduto.utilizaTabelaSubstituicaoTributaria = false;
                        oProduto.utilizaValidadeEntrada = false;
                        oProduto.margem = margem;
                        oProduto.validade = validade;
                        
                        ProdutoComplementoVO oProdutoComplemento = new ProdutoComplementoVO();
                        oProdutoComplemento.idSituacaoCadastro = idSituacaoCadastro;
                        oProdutoComplemento.precoVenda = precoVenda;
                        oProdutoComplemento.precoDiaSeguinte = precoVenda;
                        oProdutoComplemento.custoComImposto = custo;
                        oProdutoComplemento.custoSemImposto = custo;
                        
                        oProduto.vComplemento.add(oProdutoComplemento);
                        
                        ProdutoAliquotaVO oProdutoAliquota = new ProdutoAliquotaVO();
                        oProdutoAliquota.idEstado = 35;
                        oProdutoAliquota.idAliquotaDebito = idAliquota;
                        oProdutoAliquota.idAliquotaCredito = idAliquota;
                        oProdutoAliquota.idAliquotaDebitoForaEstado = idAliquota;
                        oProdutoAliquota.idAliquotaCreditoForaEstado = idAliquota;
                        oProdutoAliquota.idAliquotaDebitoForaEstadoNF = idAliquota;
                        
                        oProduto.vAliquota.add(oProdutoAliquota);
                        
                        ProdutoAutomacaoVO oProdutoAutomacao = new ProdutoAutomacaoVO();
                        oProdutoAutomacao.codigoBarras = codigoBarras;
                        oProdutoAutomacao.qtdEmbalagem = qtdEmbalagem;
                        oProdutoAutomacao.idTipoEmbalagem = idTipoEmbalagem;
                        
                        oProduto.vAutomacao.add(oProdutoAutomacao);
                        
                        CodigoAnteriorVO oCodigoAnterior = new CodigoAnteriorVO();

                        oCodigoAnterior.codigoanterior = codigoAnterior;
                        oCodigoAnterior.codigoatual = idProduto;

                        if (!strCodigoBarras.isEmpty()) {
                            oCodigoAnterior.barras = Long.parseLong(strCodigoBarras);
                        } else {
                            oCodigoAnterior.barras = 0;
                        }

                        oCodigoAnterior.naturezareceita = tipoNaturezaReceita;
                        oCodigoAnterior.piscofinsdebito = -1;
                        oCodigoAnterior.piscofinscredito = -1;
                        
                        if (cellIcms.getContents().length() > 5) {
                            oCodigoAnterior.ref_icmsdebito = cellIcms.getContents().substring(0, 5);
                        } else {
                            oCodigoAnterior.ref_icmsdebito = cellIcms.getContents();
                        }
                        
                        oCodigoAnterior.estoque = estoque;
                        oCodigoAnterior.e_balanca = eBalanca;
                        oCodigoAnterior.codigobalanca = codigoBalanca;
                        oCodigoAnterior.custosemimposto = custo;
                        oCodigoAnterior.custocomimposto = custo;
                        oCodigoAnterior.margem = margem;
                        oCodigoAnterior.precovenda = precoVenda;
                        oCodigoAnterior.referencia = referencia;
                        oCodigoAnterior.ncm = "";

                        oProduto.vCodigoAnterior.add(oCodigoAnterior);
                        
                        vProduto.put(idProduto, oProduto);
                    }
                }
            }
            
            Conexao.commit();
            return vProduto;
        } catch(Exception ex) {
            JOptionPane.showMessageDialog(null, ex+" "+linha);
            throw ex;
        }
    }
    
    public Map<Integer, ProdutoVO> carregarProdutoPrecoMercadologico(String i_arquivo, int idLoja) throws Exception {
        Map<Integer, ProdutoVO> vProduto = new HashMap<>();
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        Utils util = new Utils();
        int linha = 0, idProduto, mercadologico1, mercadologico2, mercadologico3;
        double precoVenda;
        
        WorkbookSettings settings = new WorkbookSettings();
        settings.setEncoding("CP1250");

        Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);

        Sheet[] sheets = arquivo.getSheets();
        
        try {
            
            Conexao.begin();
            
            stm = Conexao.createStatement();
            
            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;
                
                for (int i = 0; i < sheet.getRows(); i++) {                    
                    linha++;
                    
                    if ("--------".equals(sheet.getCell(0, i).getContents().trim()) ||
                            ("LISTA D".equals(sheet.getCell(0, i).getContents().trim())) ||
                            ("SUPERME".equals(sheet.getCell(0, i).getContents().trim())) ||
                            ("TOTAL D".equals(sheet.getCell(0, i).getContents().trim())) ||
                            ("CODIGO".equals(sheet.getCell(0, i).getContents().trim())) ||
                            ("-------".equals(sheet.getCell(0, i).getContents().trim()))) {
                        
                        continue;
                    } else {
                        
                        Cell cellIdProduto = sheet.getCell(0, i);
                        Cell cellPrecoVenda = sheet.getCell(4, i);
                        Cell cellMercadologico = sheet.getCell(6, i);
                        
                        idProduto = Integer.parseInt(cellIdProduto.getContents().trim());
                        
                        if ((cellPrecoVenda.getContents() != null) &&
                                (!cellPrecoVenda.getContents().trim().isEmpty())) {
                            precoVenda = Double.parseDouble(cellPrecoVenda.getContents().trim());
                        } else {
                            precoVenda = 0;
                        }
                        
                        if ((cellMercadologico.getContents() != null) &&
                                (!cellMercadologico.getContents().trim().isEmpty()) &&
                                (!"0".equals(cellMercadologico.getContents().trim()))) {
                            
                            mercadologico1 = Integer.parseInt(cellMercadologico.getContents().substring(0, 
                                    cellMercadologico.getContents().indexOf("-")));
                            mercadologico2 = mercadologico1;
                            mercadologico3 = mercadologico1;
                            
                            sql = new StringBuilder();
                            sql.append("select * from mercadologico ");
                            sql.append("where mercadologico1 = " + mercadologico1 + " ");
                            sql.append("and mercadologico2 = " + mercadologico2 + " ");
                            sql.append("and mercadologico3 = " + mercadologico3 + " ");
                            rst = stm.executeQuery(sql.toString());
                            
                            if (rst.next()) {
                                
                                mercadologico1 = rst.getInt("mercadologico1");
                                mercadologico2 = rst.getInt("mercadologico2");
                                mercadologico3 = rst.getInt("mercadologico3");
                                
                            } else {
                                mercadologico1 = 178;
                                mercadologico2 = 1;
                                mercadologico3 = 1;
                            }
                            
                            
                        } else {
                            mercadologico1 = 178;
                            mercadologico2 = 1;
                            mercadologico3 = 1;
                        }
                        
                        ProdutoVO oProduto = new ProdutoVO();
                        
                        oProduto.id = idProduto;
                        oProduto.mercadologico1 = mercadologico1;
                        oProduto.mercadologico2 = mercadologico2;
                        oProduto.mercadologico3 = mercadologico3;
                        
                        ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                        
                        oComplemento.idLoja = idLoja;
                        oComplemento.precoVenda = precoVenda;
                        oComplemento.precoDiaSeguinte = precoVenda;
                        
                        oProduto.vComplemento.add(oComplemento);
                        
                        vProduto.put(idProduto, oProduto);
                    }
                }
            }
            
            Conexao.commit();            
            return vProduto;
        } catch(Exception ex) {
            throw ex;
        }
    }
    
    public Map<Integer, ProdutoVO> carregarProdutosInativos(String i_arquivo, int idLoja) throws Exception {
        Map<Integer, ProdutoVO> vProduto = new HashMap<>();
        int idProduto, idSituacaoCadastro, linha;

        WorkbookSettings settings = new WorkbookSettings();
        settings.setEncoding("CP1250");

        Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);

        Sheet[] sheets = arquivo.getSheets();
        
        try {

            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;
                
                for (int i = 0; i < sheet.getRows(); i++) {                    
                    linha++;
                    
                    if ("--------".equals(sheet.getCell(0, i).getContents().trim()) ||
                            ("LISTA D".equals(sheet.getCell(0, i).getContents().trim())) ||
                            ("SUPERME".equals(sheet.getCell(0, i).getContents().trim())) ||
                            ("TOTAL D".equals(sheet.getCell(0, i).getContents().trim())) ||
                            ("CODIGO".equals(sheet.getCell(0, i).getContents().trim())) ||
                            ("-------".equals(sheet.getCell(0, i).getContents().trim()))) {
                        
                        continue;
                    } else {
                    
                        
                        Cell cellIdProduto = sheet.getCell(0, i);
                        
                        idProduto = Integer.parseInt(cellIdProduto.getContents().trim());
                        idSituacaoCadastro = 0;
                        
                        ProdutoVO oProduto = new ProdutoVO();
                        oProduto.id = idProduto;
                        
                        ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                        oComplemento.idLoja = idLoja;
                        oComplemento.idSituacaoCadastro = idSituacaoCadastro;
                        
                        oProduto.vComplemento.add(oComplemento);
                        
                        vProduto.put(idProduto, oProduto);                        
                    }
                }
            }
                        
            return vProduto;
        } catch(Exception ex) {
            throw ex;
        }
    }
    
    public List<OfertaVO> carregarProdutosOferta(String i_arquivo, int idLoja) throws Exception {
        List<OfertaVO> vOferta = new ArrayList<>();
        int idProduto, linha;
        double precoOferta;
        String dataInicio, dataFim;

        WorkbookSettings settings = new WorkbookSettings();
        settings.setEncoding("CP1250");

        Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);

        Sheet[] sheets = arquivo.getSheets();
        
        try {
            
            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;
                
                for (int i = 0; i < sheet.getRows(); i++) {                    
                    linha++;
                    
                    if ("---------".equals(sheet.getCell(0, i).getContents().trim()) ||
                            ("LISTA PRO".equals(sheet.getCell(0, i).getContents().trim())) ||
                            ("SUPERMERC".equals(sheet.getCell(0, i).getContents().trim())) ||
                            ("---------".equals(sheet.getCell(0, i).getContents().trim())) ||
                            ("CODIGO".equals(sheet.getCell(0, i).getContents().trim())) ||
                            ("TOTAL DE".equals(sheet.getCell(0, i).getContents().trim()))) {
                        
                        continue;
                    } else {
                        
                        Cell cellIdProduto = sheet.getCell(0, i);
                        Cell cellPrecoOferta = sheet.getCell(5, i);
                        Cell cellDataInicio = sheet.getCell(6, i);
                        Cell cellDataFim = sheet.getCell(7, i);
                        
                        idProduto = Integer.parseInt(cellIdProduto.getContents().trim());
                        precoOferta = Double.parseDouble(cellPrecoOferta.getContents().trim());
                        
                        dataInicio = cellDataInicio.getContents().substring(6, 10).trim();
                        dataInicio = dataInicio +"/"+cellDataInicio.getContents().substring(3, 5).trim();
                        dataInicio = dataInicio +"/"+cellDataInicio.getContents().substring(0, 2).trim();

                        dataFim = cellDataFim.getContents().substring(6, 10).trim();
                        dataFim = dataFim +"/"+cellDataFim.getContents().substring(3, 5).trim();
                        dataFim = dataFim +"/"+cellDataFim.getContents().substring(0, 2).trim();
                        
                        OfertaVO oOferta = new OfertaVO();
                        
                        oOferta.id_produto = idProduto;
                        oOferta.precooferta = precoOferta;
                        oOferta.datainicio = dataInicio;
                        oOferta.datatermino = dataFim;
                        
                        vOferta.add(oOferta);
                        
                    }
                }
            }
            
            return vOferta;
        } catch(Exception ex) {
            throw ex;
        }
    } 
    
    public List<FornecedorVO> carregarFornecedor(String i_arquivo) throws Exception {
        List<FornecedorVO> vFornecedor = new ArrayList<>();
        int linha, idFornecedor, idMunicipio = 0, idEstado;
        long cnpj, cep;
        String razaoSocial, nomeFantasia, telefone, fax, contato, endereco, bairro;
        Utils util = new Utils();
        
        WorkbookSettings settings = new WorkbookSettings();
        settings.setEncoding("CP1250");

        Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);

        Sheet[] sheets = arquivo.getSheets();
        
        try {

            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;
                
                for (int i = 0; i < sheet.getRows(); i++) {                    
                    linha++;
                    
                    if ("------".equals(sheet.getCell(0, i).getContents().trim()) ||
                            ("LISTA".equals(sheet.getCell(0, i).getContents().trim())) ||
                            ("SUPERM".equals(sheet.getCell(0, i).getContents().trim())) ||
                            ("------".equals(sheet.getCell(0, i).getContents().trim())) ||
                            ("CODIGO".equals(sheet.getCell(0, i).getContents().trim())) ||
                            ("TOTAL".equals(sheet.getCell(0, i).getContents().trim()))) {
                        
                        continue;
                    } else {
                    
                        Cell cellIdFornecedor = sheet.getCell(0, i);
                        Cell cellRazaoSocial = sheet.getCell(1, i);
                        Cell cellTelefone = sheet.getCell(2, i);
                        Cell cellFax = sheet.getCell(3, i);
                        Cell cellContato = sheet.getCell(4, i);
                        Cell cellCidade = sheet.getCell(5, i);
                        Cell cellEndereco = sheet.getCell(6, i);
                        Cell cellBairro = sheet.getCell(7, i);
                        Cell cellUf = sheet.getCell(8, i);
                        
                        idFornecedor = Integer.parseInt(cellIdFornecedor.getContents().trim());
                        
                        if ((cellRazaoSocial.getContents() != null) &&
                                (!cellRazaoSocial.getContents().trim().isEmpty())) {
                            razaoSocial = util.acertarTexto(cellRazaoSocial.getContents().trim().replace("'", ""));
                        } else {
                            razaoSocial = "FORNECEDOR SEM RAZAO SOCIAL";
                        }
                        
                        if (razaoSocial.isEmpty()) {
                            nomeFantasia = "FORNECEDOR SEM NOME FANTASIA";
                        } else {
                            nomeFantasia = razaoSocial;
                        }
                        
                        if ((cellTelefone.getContents() != null) &&
                                (!cellTelefone.getContents().trim().isEmpty())) {
                            telefone = util.formataNumero(cellTelefone.getContents().trim());
                        } else {
                            telefone = "0000000000";
                        }
                        
                        if ((cellFax.getContents() != null) &&
                                (!cellFax.getContents().trim().isEmpty())) {
                            fax = util.formataNumero(cellFax.getContents().trim());
                        } else {
                            fax = "";
                        }
                        
                        if ((cellContato.getContents() != null) &&
                                (!cellContato.getContents().trim().isEmpty())) {
                            contato = util.acertarTexto(cellContato.getContents().trim().replace("'", ""));
                        } else {
                            contato = "";
                        }
                        
                        if ((cellEndereco.getContents() != null) &&
                                (!cellEndereco.getContents().trim().isEmpty())) {
                            endereco = util.acertarTexto(cellEndereco.getContents().trim().replace("'", ""));
                        } else {
                            endereco = "";
                        }
                        
                        if ((cellBairro.getContents() != null) &&
                                (!cellBairro.getContents().trim().isEmpty())) {
                            bairro = util.acertarTexto(cellBairro.getContents().trim().replace("'", ""));
                        } else {
                            bairro = "";
                        }
                        
                        if ((cellCidade.getContents() != null)
                                && (!cellCidade.getContents().trim().isEmpty())) {

                            if ((cellUf.getContents() != null)
                                    && (!cellUf.getContents().trim().isEmpty())) {

                                idMunicipio = util.retornarMunicipioIBGEDescricao(
                                        util.acertarTexto(cellCidade.getContents().trim().replace("'", "")),
                                        util.acertarTexto(cellUf.getContents().trim().replace("'", "")));

                                if (idMunicipio == 0) {
                                    idMunicipio = 3534302;
                                }
                            }
                        } else {
                            idMunicipio = 3534302;
                        }

                        if ((cellUf.getContents() != null)
                                && (!cellUf.getContents().trim().isEmpty())) {

                            idEstado = util.retornarEstadoDescricao(
                                    util.acertarTexto(cellUf.getContents().replace("'", "").trim()));

                            if (idEstado == 0) {
                                idEstado = 35;
                            }
                        } else {
                            idEstado = 35;
                        }
                        
                        cnpj = -1;
                 
                        if (razaoSocial.length() > 40) {
                            razaoSocial = razaoSocial.substring(0, 40);
                        }

                        if (nomeFantasia.length() > 30) {
                            nomeFantasia = nomeFantasia.substring(0, 30);
                        }

                        if (endereco.length() > 40) {
                            endereco = endereco.substring(0, 40);
                        }

                        if (bairro.length() > 30) {
                            bairro = bairro.substring(0, 30);
                        }

                        if (telefone.length() > 14) {
                            telefone = telefone.substring(0, 14);
                        }

                        if (String.valueOf(cnpj).length() > 14) {
                            cnpj = Long.parseLong(String.valueOf(cnpj).substring(0, 14));
                        }
                        
                        if (fax.length() > 14) {
                            fax = fax.substring(0, 14);
                        }

                        FornecedorVO oFornecedor = new FornecedorVO();
                        
                        oFornecedor.codigoanterior = idFornecedor;
                        oFornecedor.razaosocial = razaoSocial;
                        oFornecedor.nomefantasia = nomeFantasia;
                        oFornecedor.id_tipoinscricao = 0;
                        oFornecedor.cnpj = cnpj;
                        oFornecedor.endereco = endereco;
                        oFornecedor.bairro = bairro;
                        oFornecedor.numero = "0";
                        oFornecedor.id_municipio = idMunicipio;
                        oFornecedor.id_estado = idEstado;
                        oFornecedor.telefone = telefone;
                        oFornecedor.observacao = contato;
                        oFornecedor.fax = fax;
                        oFornecedor.cep = 14620000;
                        oFornecedor.inscricaoestadual = "ISENTO";
                        
                        vFornecedor.add(oFornecedor);
                        
                    }
                }
            }
            
            return vFornecedor;
        } catch(Exception ex) {
            throw ex;
        }
    }
    
    public List<ClientePreferencialVO> carregarClientePreferencial(String i_arquivo) throws Exception {
        List<ClientePreferencialVO> vClientePreferencial = new ArrayList<>();
        int idCliente, idMunicipio = 0, idEstado, linha, idTipoInscricao;
        String nome, endereco, bairro, telefone, dataNascimento, rg;
        long cnpj;
        Utils util = new Utils();
        
        WorkbookSettings settings = new WorkbookSettings();
        settings.setEncoding("CP1250");

        Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);

        Sheet[] sheets = arquivo.getSheets();
        
        try {
        
            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;
                
                for (int i = 0; i < sheet.getRows(); i++) {                    
                    linha++;
                    
                    if ("-------".equals(sheet.getCell(0, i).getContents().trim()) ||
                            ("LISTA P".equals(sheet.getCell(0, i).getContents().trim())) ||
                            ("SUPERME".equals(sheet.getCell(0, i).getContents().trim())) ||
                            ("-------".equals(sheet.getCell(0, i).getContents().trim())) ||
                            ("CODIGO".equals(sheet.getCell(0, i).getContents().trim())) ||
                            ("TOTAL I".equals(sheet.getCell(0, i).getContents().trim()))) {
                        
                        continue;
                    } else {
                        
                        Cell cellIdCliente = sheet.getCell(0, i);
                        Cell cellNome = sheet.getCell(1, i);
                        Cell cellCnpj = sheet.getCell(2, i);
                        Cell cellRg = sheet.getCell(3, i);
                        Cell cellDataNascimento = sheet.getCell(4, i);
                        Cell cellTelefone = sheet.getCell(5, i);
                        Cell cellEndereco = sheet.getCell(6, i);
                        Cell cellBairro = sheet.getCell(7, i);
                        Cell cellCidade = sheet.getCell(8, i);
                        Cell cellUf = sheet.getCell(9, i);
                        
                        idCliente = Integer.parseInt(cellIdCliente.getContents().trim());
                        idTipoInscricao = 1;
                        
                        if ((cellNome.getContents() != null) &&
                                (!cellNome.getContents().trim().isEmpty())) {
                            nome = util.acertarTexto(cellNome.getContents().trim().replace("'", ""));
                        } else {
                            nome = "CLIENTE SEM NOME";
                        }
                        
                        if ((cellCnpj.getContents() != null) &&
                                (!cellCnpj.getContents().trim().isEmpty())) {
                            cnpj = Long.parseLong(util.formataNumero(cellCnpj.getContents().trim()));
                            
                            if (String.valueOf(cnpj).length() > 11) {
                                idTipoInscricao = 0;
                            } else {
                                idTipoInscricao = 1;
                            }
                        } else {                            
                            cnpj = idCliente;
                        }
                        
                        if ((cellRg.getContents() != null) &&
                                (!cellRg.getContents().trim().isEmpty())) {
                            rg = util.acertarTexto(cellRg.getContents().trim().replace("'", ""));
                            rg = rg.replace(".", "");
                            rg = rg.replace("-", "");
                            rg = rg.replace(",", "");
                            rg = rg.replace("/", "");
                        } else {
                            rg = "ISENTO";
                        }
                        
                        if ((cellDataNascimento.getContents() != null) &&
                                (!cellDataNascimento.getContents().trim().isEmpty())) {
                            dataNascimento = cellDataNascimento.getContents().substring(6, 10).trim();
                            dataNascimento = dataNascimento+"/"+cellDataNascimento.getContents().substring(3, 5).trim();
                            dataNascimento = dataNascimento+"/"+cellDataNascimento.getContents().substring(0, 2).trim();
                        } else {
                            dataNascimento = null;
                        }
                        
                        if ((cellTelefone.getContents() != null) &&
                                (!cellTelefone.getContents().trim().isEmpty())) {
                            telefone = util.formataNumero(cellTelefone.getContents().trim());
                        } else {
                            telefone = "0000000000";
                        }
                        
                        if ((cellEndereco.getContents() != null) &&
                                (!cellEndereco.getContents().trim().isEmpty())) {
                            endereco = util.acertarTexto(cellEndereco.getContents().trim().replace("'", ""));
                        } else {
                            endereco = "";
                        }
                        
                        if ((cellBairro.getContents() != null) &&
                                (!cellBairro.getContents().trim().isEmpty())) {
                            bairro = util.acertarTexto(cellBairro.getContents().trim().replace("'", ""));
                        } else {
                            bairro = "";
                        }
                        
                        if ((cellCidade.getContents() != null)
                                && (!cellCidade.getContents().trim().isEmpty())) {

                            if ((cellUf.getContents() != null)
                                    && (!cellUf.getContents().trim().isEmpty())) {

                                idMunicipio = util.retornarMunicipioIBGEDescricao(
                                        util.acertarTexto(cellCidade.getContents().trim().replace("'", "")),
                                        util.acertarTexto(cellUf.getContents().trim().replace("'", "")));

                                if (idMunicipio == 0) {
                                    idMunicipio = 3534302;
                                }
                            }
                        } else {
                            idMunicipio = 3534302;
                        }

                        if ((cellUf.getContents() != null)
                                && (!cellUf.getContents().trim().isEmpty())) {

                            idEstado = util.retornarEstadoDescricao(
                                    util.acertarTexto(cellUf.getContents().replace("'", "").trim()));

                            if (idEstado == 0) {
                                idEstado = 35;
                            }
                        } else {
                            idEstado = 35;
                        }
                        
                        if (nome.length() > 40) {
                            nome = nome.substring(0, 40);
                        }

                        if (endereco.length() > 40) {
                            endereco = endereco.substring(0, 40);
                        }

                        if (bairro.length() > 30) {
                            bairro = bairro.substring(0, 30);
                        }

                        if (telefone.length() > 14) {
                            telefone = telefone.substring(0, 14);
                        }

                        if (String.valueOf(cnpj).length() > 14) {
                            cnpj = Long.parseLong(String.valueOf(cnpj).substring(0, 14));
                        }

                        ClientePreferencialVO oClientePreferencial = new ClientePreferencialVO();
                        oClientePreferencial.id = idCliente;
                        oClientePreferencial.nome = nome;
                        oClientePreferencial.cnpj = cnpj;
                        oClientePreferencial.id_tipoinscricao = idTipoInscricao;
                        oClientePreferencial.endereco = endereco;
                        oClientePreferencial.bairro = bairro;
                        oClientePreferencial.numero = "0";
                        oClientePreferencial.id_municipio = idMunicipio;
                        oClientePreferencial.id_estado = idEstado;
                        oClientePreferencial.telefone = telefone;
                        oClientePreferencial.inscricaoestadual = rg;
                        oClientePreferencial.datanascimento = dataNascimento;
                        oClientePreferencial.datacadastro = "";
                        oClientePreferencial.cep = 14620000;
                        
                        vClientePreferencial.add(oClientePreferencial);
                        
                    }
                }
            }            
            
            return vClientePreferencial;
        } catch(Exception ex) {
            throw ex;
        }
    }
    
    public List<ProdutoVO> carregarProdutoNcmPisCofins(String i_arquivo) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        int idProduto, ncm1, ncm2, ncm3, idTipoPisCofins, 
            idTipoCofinsCredito, tipoNaturezaReceita, linha, pisCofinsDebitoAnt,
            pisCofinsCreditoAnt;
        String ncmAtual;
        Utils util = new Utils();

        WorkbookSettings settings = new WorkbookSettings();
        settings.setEncoding("CP1250");

        Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);

        Sheet[] sheets = arquivo.getSheets();
        
        try {
            
            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;
                
                for (int i = 0; i < sheet.getRows(); i++) {                    
                    linha++;
                    
                    if ("-------".equals(sheet.getCell(0, i).getContents().trim()) ||
                        ("LISTA P".equals(sheet.getCell(0, i).getContents().trim())) ||
                        ("SUPERME".equals(sheet.getCell(0, i).getContents().trim())) ||
                        ("-------".equals(sheet.getCell(0, i).getContents().trim())) ||
                        ("CODIGO".equals(sheet.getCell(0, i).getContents().trim())) ||
                        ("TOTAL I".equals(sheet.getCell(0, i).getContents().trim())) ||
                        ("NUMERO D".equals(sheet.getCell(0, i).getContents().trim()))) {
                        
                        continue;
                    } else if (linha == 1) {
                        continue;
                    } else {
                        
                        Cell cellIdProduto = sheet.getCell(0, i);
                        Cell cellNcm = sheet.getCell(1, i);
                        Cell cellPisCofins = sheet.getCell(2, i);
                        
                        idProduto = Integer.parseInt(cellIdProduto.getContents().trim());
                        
                        if ((cellNcm.getContents() != null) &&
                                (!cellNcm.getContents().trim().isEmpty()) &&
                                (!"0".equals(cellNcm.getContents().trim()))) {
                            
                            ncmAtual = util.formataNumero(cellNcm.getContents().trim());

                            NcmVO oNcm = new NcmDAO().validar(ncmAtual);

                            ncm1 = oNcm.ncm1;
                            ncm2 = oNcm.ncm2;
                            ncm3 = oNcm.ncm3;
                            
                        } else {
                            ncm1 = -1;
                            ncm2 = -1;
                            ncm3 = -1;
                        }
                        
                        
                        if ((cellPisCofins.getContents() != null) &&
                                (!cellPisCofins.getContents().trim().isEmpty())) {
                        
                            pisCofinsDebitoAnt = Integer.parseInt(cellPisCofins.getContents().trim());
                            pisCofinsCreditoAnt = Integer.parseInt(cellPisCofins.getContents().trim());
                            
                            if (Integer.parseInt(cellPisCofins.getContents().trim()) == 2) {
                                idTipoPisCofins = 0;
                                idTipoCofinsCredito = 12;
                                tipoNaturezaReceita = -1;
                            } else if ((Integer.parseInt(cellPisCofins.getContents().trim()) >= 3)
                                    && (Integer.parseInt(cellPisCofins.getContents().trim()) <= 27)) {
                                idTipoPisCofins = 7;
                                idTipoCofinsCredito = 19;
                                tipoNaturezaReceita = util.retornarTipoNaturezaReceita(idTipoPisCofins, "");
                            } else if ((Integer.parseInt(cellPisCofins.getContents().trim()) >= 29)
                                    && (Integer.parseInt(cellPisCofins.getContents().trim()) <= 40)) {
                                idTipoPisCofins = 3;
                                idTipoCofinsCredito = 15;
                                tipoNaturezaReceita = util.retornarTipoNaturezaReceita(idTipoPisCofins, "");
                            } else if ((Integer.parseInt(cellPisCofins.getContents().trim()) >= 52)
                                    && (Integer.parseInt(cellPisCofins.getContents().trim()) <= 54)) {
                                idTipoPisCofins = 2;
                                idTipoCofinsCredito = 14;
                                tipoNaturezaReceita = util.retornarTipoNaturezaReceita(idTipoPisCofins, "");
                            } else if ((Integer.parseInt(cellPisCofins.getContents().trim()) >= 60)
                                    && (Integer.parseInt(cellPisCofins.getContents().trim()) <= 62)) {
                                idTipoPisCofins = 7;
                                idTipoCofinsCredito = 19;
                                tipoNaturezaReceita = util.retornarTipoNaturezaReceita(idTipoPisCofins, "");
                            } else if ((Integer.parseInt(cellPisCofins.getContents().trim())) == 47) {
                                idTipoPisCofins = 8;
                                idTipoCofinsCredito = 20;
                                tipoNaturezaReceita = util.retornarTipoNaturezaReceita(idTipoPisCofins, "");
                            } else {
                                idTipoPisCofins = 1;
                                idTipoCofinsCredito = 13;
                                tipoNaturezaReceita = 999;
                                pisCofinsDebitoAnt = -1;
                                pisCofinsCreditoAnt = -1;                                
                            }
                        } else {
                            idTipoPisCofins = 1;
                            idTipoCofinsCredito = 13;
                            tipoNaturezaReceita = 999;
                            pisCofinsDebitoAnt = -1;
                            pisCofinsCreditoAnt = -1;
                        }
                        
                        ProdutoVO oProduto = new ProdutoVO();
                        oProduto.id = idProduto;
                        oProduto.idTipoPisCofinsDebito = idTipoPisCofins;
                        oProduto.idTipoPisCofinsCredito = idTipoCofinsCredito;
                        oProduto.tipoNaturezaReceita = tipoNaturezaReceita;
                        oProduto.ncm1 = ncm1;
                        oProduto.ncm2 = ncm2;
                        oProduto.ncm3 = ncm3;
                        oProduto.pisCofinsDebitoAnt = pisCofinsDebitoAnt;
                        oProduto.pisCofinsCreditoAnt = pisCofinsCreditoAnt;
                        
                        vProduto.add(oProduto);
                    }
                }
            }
                        
            return vProduto;
        } catch(Exception ex) {
            throw ex;
        }        
    }
    
    public List<ProdutoVO> carregarProdutoNcm(String i_arquivo) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        int idProduto, ncm1, ncm2, ncm3, linha;
        String ncmAtual;
        Utils util = new Utils();

        WorkbookSettings settings = new WorkbookSettings();
        settings.setEncoding("CP1250");

        Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);

        Sheet[] sheets = arquivo.getSheets();
        
        try {
            
            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;
                
                for (int i = 0; i < sheet.getRows(); i++) {                    
                    linha++;
                    
                    if ("-------".equals(sheet.getCell(0, i).getContents().trim()) ||
                        ("LISTA P".equals(sheet.getCell(0, i).getContents().trim())) ||
                        ("SUPERME".equals(sheet.getCell(0, i).getContents().trim())) ||
                        ("-------".equals(sheet.getCell(0, i).getContents().trim())) ||
                        ("CODIGO".equals(sheet.getCell(0, i).getContents().trim())) ||
                        ("TOTAL I".equals(sheet.getCell(0, i).getContents().trim())) ||
                        ("NUMERO D".equals(sheet.getCell(0, i).getContents().trim())) ||
                        (sheet.getCell(0, i).getContents().trim().isEmpty()) ||
                        (sheet.getCell(0, i).getContents() == null) ||
                        ("TOTAL".equals(sheet.getCell(0, i).getContents().trim()))) {
                        
                        continue;
                    } else {
                        
                        Cell cellIdProduto = sheet.getCell(0, i);
                        Cell cellNcm = sheet.getCell(3, i);

                        
                        idProduto = Integer.parseInt(cellIdProduto.getContents().trim());
                        
                        if ((cellNcm.getContents() != null) &&
                                (!cellNcm.getContents().trim().isEmpty()) &&
                                (!"0".equals(cellNcm.getContents().trim())) &&
                                (!"NCM".equals(cellNcm.getContents().trim())) &&
                                (Integer.parseInt(cellNcm.getContents().trim()) >= 100000)) {
                            
                            ncmAtual = util.formataNumero(cellNcm.getContents().trim());

                            NcmVO oNcm = new NcmDAO().validar(ncmAtual);

                            ncm1 = oNcm.ncm1;
                            ncm2 = oNcm.ncm2;
                            ncm3 = oNcm.ncm3;
                            
                        } else {
                            ncm1 = -1;
                            ncm2 = -1;
                            ncm3 = -1;
                        }
                        
                        ProdutoVO oProduto = new ProdutoVO();
                        oProduto.id = idProduto;
                        oProduto.ncm1 = ncm1;
                        oProduto.ncm2 = ncm2;
                        oProduto.ncm3 = ncm3;
                        
                        vProduto.add(oProduto);
                    }
                }
            }
                        
            return vProduto;
        } catch(Exception ex) {
            throw ex;
        }        
    }
    
    public List<ReceberCreditoRotativoVO> carregarReceberCreditoRotativo(String i_arquivo, int idLoja) throws Exception {
        List<ReceberCreditoRotativoVO> vRotativo = new ArrayList<>();
        int linha, numeroCupom, idCliente = 0;
        long cnpj; 
        String dataEmissao, dataVencimento, nome;
        double valor;
        Utils util = new Utils();
        
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null, rst2 = null;
        
        WorkbookSettings settings = new WorkbookSettings();
        settings.setEncoding("CP1250");

        Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);

        Sheet[] sheets = arquivo.getSheets();
        
        try {

            Conexao.begin();
            
            stm = Conexao.createStatement();
            
            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;
                
                for (int i = 0; i < sheet.getRows(); i++) {                    
                    linha++;
                    
                    if (("-----------".equals(sheet.getCell(0, i).getContents().trim()) ||
                            ("LISTA CONTA".equals(sheet.getCell(0, i).getContents().trim())) ||
                            ("SUPERMERCAD".equals(sheet.getCell(0, i).getContents().trim())) ||
                            ("VENCIMENTO".equals(sheet.getCell(0, i).getContents().trim())) ||
                            ("CONTAS LOCA".equals(sheet.getCell(0, i).getContents().trim())) ||
                            ("TOTAL DAS C".equals(sheet.getCell(0, i).getContents().trim())))) {
                        
                        continue;
                    } else {
                    
                        Cell cellDataVencimento = sheet.getCell(0, i);
                        Cell cellNome = sheet.getCell(1, i);
                        Cell cellCnpj = sheet.getCell(2, i);
                        Cell cellValor = sheet.getCell(3, i);
                        Cell cellNumeroCupom = sheet.getCell(5, i);
                        Cell cellDataEmissao = sheet.getCell(6, i);
                        
                        nome = util.acertarTexto(cellNome.getContents().trim().replace("'", ""));
                        
                        if (!nome.contains("CHEQUE")) {
                                                
                            cnpj = Long.parseLong(util.formataNumero(cellCnpj.getContents().trim()));
                            valor = Double.parseDouble(cellValor.getContents().trim());
                            numeroCupom = Integer.parseInt(cellNumeroCupom.getContents().trim());

                            dataEmissao = cellDataEmissao.getContents().substring(6, 10).trim();
                            dataEmissao = dataEmissao + "/" + cellDataEmissao.getContents().substring(3, 5).trim();
                            dataEmissao = dataEmissao + "/" + cellDataEmissao.getContents().substring(0, 2).trim();

                            dataVencimento = cellDataVencimento.getContents().substring(6, 10).trim();
                            dataVencimento = dataVencimento + "/" + cellDataVencimento.getContents().substring(3, 5).trim();
                            dataVencimento = dataVencimento + "/" + cellDataVencimento.getContents().substring(0, 2).trim();

                            /*sql = new StringBuilder();
                            sql.append("select id from clientepreferencial ");
                            sql.append("where cnpj = " + cnpj);

                            rst = stm.executeQuery(sql.toString());

                            if (rst.next()) {
                                idCliente = rst.getInt("id");
                            } else {*/

                                sql = new StringBuilder();
                                sql.append("select id from clientepreferencial ");
                                sql.append("where nome like '%" + nome + "%'");

                                rst2 = stm.executeQuery(sql.toString());

                                if (rst2.next()) {
                                    idCliente = rst2.getInt("id");
                                }
                            //}

                            ReceberCreditoRotativoVO oRotativo = new ReceberCreditoRotativoVO();

                            oRotativo.id_loja = idLoja;
                            oRotativo.id_clientepreferencial = idCliente;
                            oRotativo.dataemissao = dataEmissao;
                            oRotativo.datavencimento = dataVencimento;
                            oRotativo.numerocupom = numeroCupom;
                            oRotativo.valor = valor;
                            oRotativo.ecf = 0;

                            vRotativo.add(oRotativo);
                        
                        }
                        
                    }
                }
            }
            
            Conexao.commit();
            
            return vRotativo;
        } catch(Exception ex) {
            throw ex;
        }
    }
    
    public List<ProdutoVO> carregarProdutosMercadologico(String i_arquivo) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        int idProduto, mercadologico1, mercadologico2, mercadologico3, linha = 0;
        String descricao = "";
        Utils util = new Utils();
        
        WorkbookSettings settings = new WorkbookSettings();
        settings.setEncoding("CP1250");

        Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);

        Sheet[] sheets = arquivo.getSheets();
        
        try {
            
            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;
                
                for (int i = 0; i < sheet.getRows(); i++) {                    
                    linha++;
                    
                    if (("-------".equals(sheet.getCell(0, i).getContents().trim()) ||
                            ("LISTA P".equals(sheet.getCell(0, i).getContents().trim())) ||
                            ("SUPERME".equals(sheet.getCell(0, i).getContents().trim())) ||
                            ("-------".equals(sheet.getCell(0, i).getContents().trim())) ||
                            ("CODIGO".equals(sheet.getCell(0, i).getContents().trim())) ||
                            ("TOTAL D".equals(sheet.getCell(0, i).getContents().trim())))) {
                        
                        continue;
                    } else {
                        
                        Cell cellIdProduto = sheet.getCell(0, i);
                        Cell cellMercadologico1 = sheet.getCell(3, i);
                        Cell cellMercadologico2 = sheet.getCell(4, i);
                        
                        if ((cellMercadologico1.getContents() != null) &&
                                (!cellMercadologico1.getContents().trim().isEmpty()) &&
                                (Integer.parseInt(util.formataNumero(cellMercadologico1.getContents().trim())) > 0)) {

                        
                            idProduto = Integer.parseInt(cellIdProduto.getContents().trim());

                            mercadologico1 = Integer.parseInt(util.formataNumero(cellMercadologico1.getContents().trim()));

                            if (mercadologico1 == 42) {
                                System.out.println("aqui");
                            }

                            if ((cellMercadologico2.getContents() != null)
                                    && (!cellMercadologico2.getContents().trim().isEmpty())
                                    && (Integer.parseInt(util.formataNumero(cellMercadologico2.getContents().trim())) > 0)) {

                                mercadologico2 = Integer.parseInt(util.formataNumero(cellMercadologico2.getContents().trim()));
                                mercadologico3 = mercadologico2;

                                Cell cellDescricaoMercadologico = sheet.getCell(4, i);
                                descricao = util.acertarTexto(cellDescricaoMercadologico.getContents().trim().replace("'", "").substring(2));

                            } else {

                                mercadologico2 = mercadologico1;
                                mercadologico3 = mercadologico1;

                                Cell cellDescricaoMercadologico = sheet.getCell(3, i);
                                descricao = util.acertarTexto(cellDescricaoMercadologico.getContents().trim().replace("'", "").substring(2));

                            }

                            ProdutoVO oProduto = new ProdutoVO();

                            oProduto.id = idProduto;
                            oProduto.mercadologico1 = mercadologico1;
                            oProduto.mercadologico2 = mercadologico2;
                            oProduto.mercadologico3 = mercadologico3;
                            oProduto.descricaoCompleta = descricao;

                            vProduto.add(oProduto);
                        
                        }
                        
                    }
                }
            }
            
            return vProduto;
        } catch(Exception ex) {
            JOptionPane.showMessageDialog(null, ex + " => " +linha);
            throw ex;
        }
    } 
    
    public Map<Integer, ProdutoVO> carregarProdutoCodigoBarra(String i_arquivo) throws Exception {
        Map<Integer, ProdutoVO> vProduto = new HashMap<>();
        int idProduto, linha;
        String strCodigoBarras;
        long codigoBarras = 0;
        Utils util = new Utils();
        
        WorkbookSettings settings = new WorkbookSettings();
        settings.setEncoding("CP1250");

        Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);

        Sheet[] sheets = arquivo.getSheets();
        
        try {
         
            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;
                
                for (int i = 0; i < sheet.getRows(); i++) {                    
                    linha++;
                    
                    if ("--------".equals(sheet.getCell(0, i).getContents().trim()) ||
                            ("LISTA GE".equals(sheet.getCell(0, i).getContents().trim())) ||
                            ("SUPERMER".equals(sheet.getCell(0, i).getContents().trim())) ||
                            ("--------".equals(sheet.getCell(0, i).getContents().trim())) ||
                            ("TOTAL DE".equals(sheet.getCell(0, i).getContents().trim())) ||
                            ("CODIGO".equals(sheet.getCell(0, i).getContents().trim()))) {
                        
                        continue;
                    } else {
                        
                        Cell cellIdProduto = sheet.getCell(0, i);
                        Cell cellCodigoBarras = sheet.getCell(1, i);
 
                        idProduto = Integer.parseInt(cellIdProduto.getContents().trim());

                        if ((cellCodigoBarras.getContents() != null) &&
                                (!cellCodigoBarras.getContents().trim().isEmpty()) &&
                                (util.encontrouLetraCampoNumerico(cellCodigoBarras.getContents()) == false)) {
                            
                            if (cellCodigoBarras.getContents().contains("/")) {
                                
                                strCodigoBarras = cellCodigoBarras.getContents().substring(0, 
                                        cellCodigoBarras.getContents().indexOf("/"));
                                
                            } else {
                            
                                strCodigoBarras = util.formataNumero(cellCodigoBarras.getContents().trim());
                                strCodigoBarras = cellCodigoBarras.getContents().substring(0,
                                        cellCodigoBarras.getContents().length() - 3);
                            }
                            
                        } else {
                            strCodigoBarras = "";                            
                        }
                        
                        if (!strCodigoBarras.isEmpty()) {
                            
                            if (strCodigoBarras.length() >= 7) {
                                
                                ProdutoVO oProduto = new ProdutoVO();
                                
                                oProduto.id = idProduto;
                                
                                ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();
                                
                                oAutomacao.codigoBarras = codigoBarras;
                                
                                oProduto.vAutomacao.add(oAutomacao);
                                
                                vProduto.put(idProduto, oProduto);
                            }
                        }
                    }
                }
            }
                        
            return vProduto;
        } catch(Exception ex) {
            throw ex;
        }        
    }

    public List<ClientePreferencialVO> carregarClienteLimite(String i_arquivo) throws Exception {
        List<ClientePreferencialVO> v_clientePreferencial = new ArrayList<>();
        Utils util = new Utils();
        int linha = 0;
        long cpf;
        double valorLimite = 0;
        String nome = "";
        
        WorkbookSettings settings = new WorkbookSettings();
        settings.setEncoding("CP1250");

        Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);

        Sheet[] sheets = arquivo.getSheets();

        try {

            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;
                
                for (int i = 0; i < sheet.getRows(); i++) {                    
                    linha++;
                    
                    if ("-----------".equals(sheet.getCell(0, i).getContents().trim()) ||
                            ("LISTA RESUM".equals(sheet.getCell(0, i).getContents().trim())) ||
                            ("SUPERMERCAD".equals(sheet.getCell(0, i).getContents().trim())) ||
                            ("-----------".equals(sheet.getCell(0, i).getContents().trim())) ||
                            ("VENCIMENTO".equals(sheet.getCell(0, i).getContents().trim())) ||
                            ("TOTAL DAS C".equals(sheet.getCell(0, i).getContents().trim())) ||
                            ("CONTAS LOCA".equals(sheet.getCell(0, i).getContents().trim()))) {
                        
                        continue;
                    } else {
                        
                        Cell cellCpf = sheet.getCell(2, i);
                        Cell cellNome = sheet.getCell(1, i);
                        Cell cellValor = sheet.getCell(5, i);
                        
                        nome = util.acertarTexto(cellNome.getContents().trim().replace("", ""));
                        valorLimite = Double.parseDouble(cellValor.getContents().trim());
                        
                        
                        if ((cellCpf.getContents() != null) &&
                                (!cellCpf.getContents().trim().isEmpty())) {
                            cpf = Long.parseLong(util.formataNumero(cellCpf.getContents().trim()));
                        } else {
                            cpf = -1;
                        }
                        
                        ClientePreferencialVO oClientePreferencial = new ClientePreferencialVO();
                        
                        oClientePreferencial.nome = nome;
                        oClientePreferencial.cnpj = cpf;
                        oClientePreferencial.valorlimite = valorLimite;
                        
                        v_clientePreferencial.add(oClientePreferencial);
                        
                    }
                }
            }
            
            return v_clientePreferencial;
        } catch(Exception ex) {
            throw ex;
        }
    }
    
    public List<ReceberChequeVO> carregarReceberCheque(String i_arquivo, int idLoja) throws Exception {
        List<ReceberChequeVO> vReceberCheque = new ArrayList<>();
        int linha = 0, idBanco, numeroCheque, idTipoInscricao;
        long cnpj = 0;
        String dataEmissao, dataVencimento, observacao, agencia, conta, nome;
        double valor = 0;
        Utils util = new Utils();

        WorkbookSettings settings = new WorkbookSettings();
        settings.setEncoding("CP1250");

        Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);

        Sheet[] sheets = arquivo.getSheets();
        
        try {
            
            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;
                
                for (int i = 0; i < sheet.getRows(); i++) {                    
                    linha++;
                    
                    if ("---------------".equals(sheet.getCell(0, i).getContents().trim()) ||
                            ("CHEQUE".equals(sheet.getCell(0, i).getContents().trim())) ||
                            ("SUPERMERCADOS R".equals(sheet.getCell(0, i).getContents().trim())) ||
                            ("---------------".equals(sheet.getCell(0, i).getContents().trim())) ||
                            ("VENCIMENTO".equals(sheet.getCell(0, i).getContents().trim())) ||
                            ("CONTAS LOCALIZA".equals(sheet.getCell(0, i).getContents().trim())) ||
                            ("TOTAL DAS CONTA".equals(sheet.getCell(0, i).getContents().trim()))) {
                        
                        continue;
                    } else {
                        Cell cellVencimento = sheet.getCell(0, i);
                        Cell cellEmitente = sheet.getCell(1, i);
                        Cell cellCnpj = sheet.getCell(2, i);
                        Cell cellBanco = sheet.getCell(3, i);
                        Cell cellNumeroCheque = sheet.getCell(4, i);
                        Cell cellDataEmissao = sheet.getCell(5, i);
                        Cell cellValor = sheet.getCell(6, i);
                        Cell cellPraca = sheet.getCell(7, i);
                        Cell cellAgencia = sheet.getCell(8, i);
                        Cell cellConta = sheet.getCell(9, i);
                        
                        dataVencimento = cellVencimento.getContents().trim().substring(6, 10);
                        dataVencimento = dataVencimento+"/"+cellVencimento.getContents().trim().substring(3, 5);
                        dataVencimento = dataVencimento+"/"+cellVencimento.getContents().trim().substring(0, 2);
                        
                        dataEmissao = cellDataEmissao.getContents().trim().substring(6, 10);
                        dataEmissao = dataEmissao+"/"+cellDataEmissao.getContents().trim().substring(3, 5);
                        dataEmissao = dataEmissao+"/"+cellDataEmissao.getContents().trim().substring(0, 2);
                        
                        numeroCheque = Integer.parseInt(cellNumeroCheque.getContents().trim());
                        
                        cnpj = Long.parseLong(util.formataNumero(cellCnpj.getContents().trim()));
                        
                        nome = util.acertarTexto(cellEmitente.getContents().trim().replace("'", ""));
                        
                        idBanco = util.retornarBanco(Integer.parseInt(cellBanco.getContents().trim()));
                        
                        observacao = util.acertarTexto(cellPraca.getContents().trim().replace("'", ""));
                        
                        agencia = util.acertarTexto(cellAgencia.getContents().trim().replace("'", ""));
                        
                        conta = util.acertarTexto(cellConta.getContents().trim().replace("'", ""));
                        
                        valor = Double.parseDouble(cellValor.getContents().trim());
                        
                        if (String.valueOf(cnpj).length() > 11) {
                            idTipoInscricao = 0;
                        } else {
                            idTipoInscricao = 1;
                        }
                        
                        ReceberChequeVO oReceberCheque = new ReceberChequeVO();
                        oReceberCheque.id_loja = idLoja;
                        oReceberCheque.id_tipoalinea = 0;
                        oReceberCheque.data = dataEmissao;
                        oReceberCheque.cpf = cnpj;
                        oReceberCheque.numerocheque = numeroCheque;
                        oReceberCheque.id_banco = idBanco;
                        oReceberCheque.agencia = agencia;
                        oReceberCheque.conta = conta;
                        oReceberCheque.numerocupom = 0;
                        oReceberCheque.valor = valor;
                        oReceberCheque.observacao = observacao;
                        oReceberCheque.rg = "";
                        oReceberCheque.telefone = "";
                        oReceberCheque.nome = nome;
                        oReceberCheque.id_tipoinscricao = idTipoInscricao;
                        oReceberCheque.datadeposito = dataVencimento;
                        oReceberCheque.valorjuros = 0;
                        oReceberCheque.valorinicial = valor;
                        
                        vReceberCheque.add(oReceberCheque);
                        
                    }
                }
            }
            
            return vReceberCheque;
        } catch(Exception ex) {
            throw ex;
        }
    }

    // MÉTODOS
    public int retornarAliquota(String aliquota) {
        int retorno;
        
        if ("0.00".equals(aliquota)) {
            retorno = 8; // outras
        } else if ("12.00".equals(aliquota)) {
            retorno = 1; // 12%
        } else if ("18.00".equals(aliquota)) {
            retorno = 2; // 18%
        } else if ("7.00".equals(aliquota)) {
            retorno = 0; // 7%
        } else if ("IS".equals(aliquota)) {
            retorno = 6; // isento
        } else if ("ST".equals(aliquota)) {
            retorno = 7; // substituido
        } else if ("25.00".equals(aliquota)) {
            retorno = 3;
        } else {
            retorno = 8; // outras
        }
        
        return retorno;
    }   
}