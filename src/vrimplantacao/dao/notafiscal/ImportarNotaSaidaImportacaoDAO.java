package vrimplantacao.dao.notafiscal;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.StringWriter;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import vrframework.classe.Conexao;
import vrimplantacao.dao.ParametroPdvDAO;
import vrimplantacao.dao.cadastro.AliquotaDAO;
import vrimplantacao.dao.cadastro.FornecedorDAO;
import vrimplantacao.dao.cadastro.LojaDAO;
import vrimplantacao.dao.cadastro.ProdutoDAO;
import vrimplantacao.dao.sistema.EstadoDAO;
import vrimplantacao.vo.cadastro.TipoFornecedorVO;
import vrimplantacao.vo.cadastro.TipoPercentualValor;
import vrimplantacao.vo.interfaces.DivergenciaVO;
import vrimplantacao.vo.interfaces.TipoDivergencia;
import vrimplantacao.vo.notafiscal.NotaSaidaItemVO;
import vrimplantacao.vo.notafiscal.NotaSaidaVO;
import vrimplantacao.vo.notafiscal.NotaSaidaVencimentoVO;
import vrimplantacao.vo.notafiscal.SituacaoNfe;
import vrimplantacao.vo.notafiscal.TipoFreteNotaFiscal;
import vrimplantacao.vo.notafiscal.TipoNota;
import vrimplantacao.vo.notafiscal.TipoSaidaVO;
import vrframework.classe.Util;
import vrimplantacao.dao.cadastro.ClienteEventuallDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.produto.ProdutoAnteriorDAO;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.ProdutoAnteriorVO;

public class ImportarNotaSaidaImportacaoDAO {

    public String carregarCFOP(String i_xml) throws Exception {
        String cfop = "";

        //abre arquivo
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = dbf.newDocumentBuilder();
        docBuilder.setErrorHandler(null);

        Document doc = docBuilder.parse(new ByteArrayInputStream(getXML(i_xml).getBytes("utf-8")));

        Element infNFe = (Element) doc.getDocumentElement().getElementsByTagName("infNFe").item(0);
        Element ide = (Element) infNFe.getElementsByTagName("ide").item(0);
        Element det = (Element) infNFe.getElementsByTagName("det").item(0);
        Element prod = (Element) det.getElementsByTagName("prod").item(0);

        cfop = prod.getElementsByTagName("CFOP").item(0).getTextContent();
        cfop = cfop.substring(0, 1).concat(".").concat(cfop.substring(1, 4));

        return cfop;
    }

    public String carregarNatOperacao(String i_xml) throws Exception {
        String natOp = "";

        //abre arquivo
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = dbf.newDocumentBuilder();
        docBuilder.setErrorHandler(null);

        Document doc = docBuilder.parse(new ByteArrayInputStream(getXML(i_xml).getBytes("utf-8")));

        Element infNFe = (Element) doc.getDocumentElement().getElementsByTagName("infNFe").item(0);
        Element ide = (Element) infNFe.getElementsByTagName("ide").item(0);

        natOp = ide.getElementsByTagName("natOp").item(0).getTextContent();

        return natOp;
    }

    public static class LojaV2 {
        public String impSistema;
        public String impLoja;

        @Override
        public String toString() {
            return impSistema + " - " + impLoja;
        }
        
    }
    
    public LojaV2 impLoja;
    
    public List<LojaV2> carregarLojaV2() throws Exception {
        List<LojaV2> result = new ArrayList<>();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select distinct impsistema, imploja from implantacao.codant_produto order by impsistema, imploja"
            )) {
                while (rst.next()) {
                    LojaV2 loja = new LojaV2();
                    loja.impSistema = rst.getString("impsistema");
                    loja.impLoja = rst.getString("imploja");
                    result.add(loja);
                }
            }
        }
        return result;
    }

    public void clearAnteriores() {
        daoV2.clearAnteriores();
    }
    
    public boolean isImportacaoV2() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select table_catalog from "
                    + "information_schema.tables where "
                    + "table_schema = 'implantacao' and "
                    + "table_name = 'codant_produto'"
            )) {
                return rst.next();
            }            
        }
    }
    private final ProdutoAnteriorDAO daoV2 = new ProdutoAnteriorDAO(false);
    
    public NotaSaidaVO carregar(String i_xml, int i_idTipoNotaSaida, boolean verificarCodigoAnterior) throws Exception {
        
        boolean importacaoV2 = isImportacaoV2();
        
        //abre arquivo
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = dbf.newDocumentBuilder();
        docBuilder.setErrorHandler(null);

        Document doc = docBuilder.parse(new ByteArrayInputStream(getXML(i_xml).getBytes("utf-8")));

        Element infNFe = (Element) doc.getDocumentElement().getElementsByTagName("infNFe").item(0);

        NotaSaidaVO oNotaSaida = new NotaSaidaVO();
        oNotaSaida.id = -1;
        oNotaSaida.idSituacaoNfe = SituacaoNfe.AUTORIZADA.getId();

        TipoSaidaVO oTipoSaida = new TipoSaidaDAO().carregar(i_idTipoNotaSaida);
        oNotaSaida.idTipoSaida = i_idTipoNotaSaida;

        //emitente (loja)
        Element emit = (Element) infNFe.getElementsByTagName("emit").item(0);
        Element eCNPJ = (Element) emit.getElementsByTagName("CNPJ").item(0);
        oNotaSaida.idLoja = new LojaDAO().getId(new FornecedorDAO().getId(Long.parseLong(eCNPJ.getTextContent())));

        //destinatario
        Element dest = (Element) infNFe.getElementsByTagName("dest").item(0);
        Element dEnderDest = (Element) dest.getElementsByTagName("enderDest").item(0);
        Element dUF = (Element) dEnderDest.getElementsByTagName("UF").item(0);
        Element dCNPJ = (Element) dest.getElementsByTagName("CNPJ").item(0);

        if (dCNPJ == null) {
            dCNPJ = (Element) dest.getElementsByTagName("CPF").item(0);
        }

        //carrega nota
        oNotaSaida.chaveNfe = infNFe.getAttribute("Id").substring(3, 47);

        Element ide = (Element) infNFe.getElementsByTagName("ide").item(0);

        Element numeronota = (Element) ide.getElementsByTagName("nNF").item(0);
        oNotaSaida.numeroNota = Integer.parseInt(numeronota.getTextContent());

        Element dataemissao = (Element) ide.getElementsByTagName("dhEmi").item(0);
        oNotaSaida.dataHoraEmissao = Util.formatDataHoraGUI(new SimpleDateFormat("yyyy-MM-dd").parse(dataemissao.getTextContent()));

        Element tpEmis = (Element) ide.getElementsByTagName("tpEmis").item(0);

        if (tpEmis.getTextContent().equals("5")) {
            oNotaSaida.contingenciaNfe = true;
        } else if (tpEmis.getTextContent().equals("1")) {
            oNotaSaida.contingenciaNfe = false;
        }

        Element indPag = (Element) ide.getElementsByTagName("indPag").item(0);

        Element finNFe = (Element) ide.getElementsByTagName("finNFe").item(0);

        if (finNFe.getTextContent().equals("1")) {
            oNotaSaida.idTipoNota = TipoNota.NORMAL.getId();
        } else if (finNFe.getTextContent().equals("2")) {
            oNotaSaida.idTipoNota = TipoNota.COMPLEMENTO.getId();
        } else if (finNFe.getTextContent().equals("3")) {
            oNotaSaida.idTipoNota = TipoNota.LANCAMENTO_ICMS.getId();
        }

        TipoFornecedorVO oTipoFornecedor = new TipoFornecedorVO();

        if (dCNPJ != null && !dCNPJ.getTextContent().equals("")) {
            oTipoFornecedor = new FornecedorDAO().carregarTipo(Long.parseLong(dCNPJ.getTextContent()));

            if (oTipoSaida.destinatarioCliente || oTipoSaida.geraReceber) {
                oNotaSaida.idClienteEventualDestinatario = new ClienteEventuallDAO().getId(Long.parseLong(dCNPJ.getTextContent()));
            } else {
                oNotaSaida.idFornecedorDestinatario = new FornecedorDAO().getId(Long.parseLong(dCNPJ.getTextContent()));
            }
        }

        oNotaSaida.impressao = true;
        oNotaSaida.aplicaIcmsDesconto = false;
        oNotaSaida.aplicaIcmsEncargo = false;
        oNotaSaida.aplicaPisCofinsDesconto = false;
        oNotaSaida.aplicaPisCofinsEncargo = false;
        oNotaSaida.idEstadoDestinatario = new EstadoDAO().getId(dUF.getTextContent());

        Element datasaida = (Element) ide.getElementsByTagName("dhSaiEnt").item(0);

        if (datasaida != null) {
            oNotaSaida.dataSaida = Util.formatDataGUI(new SimpleDateFormat("yyyy-MM-dd").parse(datasaida.getTextContent()));

        } else {
            oNotaSaida.dataSaida = Util.formatDataHoraGUI(new SimpleDateFormat("yyyy-MM-dd").parse(dataemissao.getTextContent()));
        }

        //itens
        NodeList vDet = infNFe.getElementsByTagName("det");

        MultiMap<Integer, NotaSaidaItemVO> somados = new MultiMap<>();
        for (int i = 0; i < vDet.getLength(); i++) {
            Element det = (Element) vDet.item(i);
            Element prod = (Element) det.getElementsByTagName("prod").item(0);
            Element cProd = (Element) det.getElementsByTagName("cProd").item(0);
            Element CFOP = (Element) prod.getElementsByTagName("CFOP").item(0);
            String cfop = CFOP.getTextContent().substring(0, 1) + "." + CFOP.getTextContent().substring(1);

            //codigo produto
            int idProduto = 0;

            //dados produto
            Element cEAN = (Element) prod.getElementsByTagName("cEAN").item(0);
            Element xProd = (Element) prod.getElementsByTagName("xProd").item(0);

            if (verificarCodigoAnterior) {
                if (importacaoV2) {
                    daoV2.setImportSistema(impLoja.impSistema);
                    daoV2.setImportLoja(impLoja.impLoja);                    
                    idProduto = daoV2.getCodigoAnterior2(daoV2.getImportSistema(), daoV2.getImportLoja(), cProd.getTextContent());
                    //ProdutoAnteriorVO anterior = daoV2.getCodigoAnterior2(daoV2.getImportSistema(), daoV2.getImportLoja(), cProd.getTextContent());
                    //ProdutoAnteriorVO anterior = daoV2.getCodigoAnterior().get(impLoja.impSistema, impLoja.impLoja, cProd.getTextContent());
                    //if (anterior != null && anterior.getCodigoAtual() != null) {
                    //    idProduto = anterior.getCodigoAtual().getId();
                    //} else {
                    //    idProduto = -1
                    //}
                } else {
                    idProduto = new ProdutoDAO().getIdAnterior(Long.parseLong(cProd.getTextContent()));
                }
            } else {
                
                if (cEAN == null || cEAN.getTextContent().isEmpty()) {
                    oNotaSaida.vDivergencia.add(new DivergenciaVO("O produto " + xProd.getTextContent() + " está sem código de barras ou código externo.", TipoDivergencia.ERRO.getId()));
                    continue;
                }
                
                idProduto = new ProdutoDAO().getId(Long.parseLong(cEAN.getTextContent()));
            }

            if (idProduto == -1) {
                if (new ProdutoDAO().isProduto(Integer.parseInt(cProd.getTextContent()))) {
                    idProduto = Integer.parseInt(cProd.getTextContent());
                } else {
                    idProduto = new ParametroPdvDAO().get(28).getInt();
                }
            }

            NotaSaidaItemVO oItem = new NotaSaidaDAO().carregarProduto(idProduto, cfop, oNotaSaida.idEstadoDestinatario, oTipoFornecedor.id);

            oItem = new NotaSaidaItemVO();
            oItem.idProduto = idProduto;
            oItem.cfop = cfop;

            //Dados de Importação
            Element DI = (Element) prod.getElementsByTagName("DI").item(0);

            if (DI != null) {
                Element xLocDesemb = (Element) DI.getElementsByTagName("xLocDesemb").item(0);
                Element UFDesemb = (Element) DI.getElementsByTagName("UFDesemb").item(0);
                Element dDesemb = (Element) DI.getElementsByTagName("dDesemb").item(0);
                Element adi = (Element) DI.getElementsByTagName("adi").item(0);

                oItem.localDesembaraco = xLocDesemb.getTextContent();
                oItem.idEstadoDesembaraco = new EstadoDAO().getId(UFDesemb.getTextContent());
                oItem.dataDesembaraco = Util.formatDataGUI(Date.valueOf(dDesemb.getTextContent()));
                oItem.numeroAdicao = Integer.parseInt(adi.getTextContent());
            }

            oNotaSaida.idTipoSaida = i_idTipoNotaSaida;
            oItem.idTipoSaida = i_idTipoNotaSaida;

            //valores do item da nota
            Element qCom = (Element) prod.getElementsByTagName("qCom").item(0);
            oItem.quantidade = Double.parseDouble(qCom.getTextContent());

            Element uCom = (Element) prod.getElementsByTagName("uCom").item(0);

            if (uCom.getTextContent().isEmpty() || !Util.isNumero(uCom.getTextContent().substring(2))) {
                oItem.qtdEmbalagem = 1;
            } else {
                oItem.qtdEmbalagem = Integer.parseInt(uCom.getTextContent().substring(2));
            }

            if (!Util.isTipoEmbalagemFracionado(uCom.getTextContent().substring(0, 2))) {
                oItem.quantidade = Util.round(oItem.quantidade, 0);
            } else {
                oItem.quantidade = Double.parseDouble(Util.formatDecimal3(oItem.quantidade).replace(".", "").replace(",", "."));
            }

            Element vProdItem = (Element) prod.getElementsByTagName("vProd").item(0);
            oItem.valorTotal = Double.parseDouble(vProdItem.getTextContent());

            Element vOutroItem = (Element) prod.getElementsByTagName("vOutro").item(0);

            if (vOutroItem != null) {
                oItem.valorOutras = Double.parseDouble(vOutroItem.getTextContent());
            }

            Element vUnCom = (Element) prod.getElementsByTagName("vUnCom").item(0);
            oItem.valor = Double.parseDouble(vUnCom.getTextContent());

            Element vDescItem = (Element) prod.getElementsByTagName("vDesc").item(0);

            if (vDescItem != null && !oTipoSaida.notaProdutor) {
                //oItem.valorDesconto = Double.parseDouble(vDescItem.getTextContent());
                //oItem.valorTotal += Double.parseDouble(vDescItem.getTextContent());
            }
            //impostos
            //IPI
            Element imposto = (Element) det.getElementsByTagName("imposto").item(0);
            Element PIS = (Element) imposto.getElementsByTagName("PIS").item(0);
            Element PISST = (Element) imposto.getElementsByTagName("PISST").item(0);
            Element ICMS = (Element) imposto.getElementsByTagName("ICMS").item(0);
            Element COFINS = (Element) imposto.getElementsByTagName("COFINS").item(0);
            Element IPI = (Element) imposto.getElementsByTagName("IPI").item(0);

            if (IPI != null) {
                Element IPINT = (Element) IPI.getElementsByTagName("IPINT").item(0);
                Element IPITrib = (Element) IPI.getElementsByTagName("IPITrib").item(0);

                if (IPINT != null) {
                    Element CST = (Element) IPINT.getElementsByTagName("CST").item(0);

                    if (CST.getTextContent().equals("99")) {
                        Element vUnid = (Element) IPINT.getElementsByTagName("vUnid").item(0);
                        Element vIPI = (Element) IPINT.getElementsByTagName("vIPI").item(0);

                        oItem.valorIpi = Double.parseDouble(vUnid.getTextContent());
                        oNotaSaida.valorIpi += oItem.valorIpi;
                        oItem.valorTotalIpi = Double.parseDouble(vIPI.getTextContent());

                        if (oItem.valorTotalIpi > 0) {
                            oItem.valorBaseIpi = oItem.valorTotal;
                        }
                    }

                } else if (IPITrib != null) {
                    Element CST = (Element) IPITrib.getElementsByTagName("CST").item(0);
                    Element vUnid = (Element) IPITrib.getElementsByTagName("vUnid").item(0);
                    Element vIPI = (Element) IPITrib.getElementsByTagName("vIPI").item(0);

                    if (vUnid != null && !vUnid.getTextContent().isEmpty()) {
                        oItem.valorIpi = Double.parseDouble(vUnid.getTextContent());
                        oNotaSaida.valorIpi += oItem.valorIpi;
                    }

                    oItem.valorTotalIpi = Double.parseDouble(vIPI.getTextContent());

                    if (oItem.valorTotalIpi > 0) {
                        oItem.valorBaseIpi = oItem.valorTotal;
                    }
                }
            }

            //pis/cofins
            if (PIS != null) {
                Element PISAliq = (Element) PIS.getElementsByTagName("PISAliq").item(0);
                Element PISQtde = (Element) PIS.getElementsByTagName("PISQtde").item(0);
                Element PISNT = (Element) PIS.getElementsByTagName("PISNT").item(0);
                Element PISOutr = (Element) PIS.getElementsByTagName("PISOutr").item(0);

                Element CST = null;

                if (PISAliq != null) {
                    CST = (Element) PISAliq.getElementsByTagName("CST").item(0);
                    oItem.valorPisCofins += Double.parseDouble(PISAliq.getElementsByTagName("vPIS").item(0).getTextContent());
                    oItem.cstPisCofins = Integer.parseInt(CST.getTextContent());

                } else if (PISQtde != null) {
                    CST = (Element) PISQtde.getElementsByTagName("CST").item(0);
                    oItem.valorPisCofins += Double.parseDouble(PISQtde.getElementsByTagName("vPIS").item(0).getTextContent());
                    oItem.cstPisCofins = Integer.parseInt(CST.getTextContent());

                } else if (PISNT != null) {
                    CST = (Element) PISNT.getElementsByTagName("CST").item(0);
                    oItem.cstPisCofins = Integer.parseInt(CST.getTextContent());

                } else if (PISOutr != null) {
                    CST = (Element) PISOutr.getElementsByTagName("CST").item(0);
                    oItem.valorPisCofins += Double.parseDouble(PISOutr.getElementsByTagName("vPIS").item(0).getTextContent());
                    oItem.cstPisCofins = Integer.parseInt(CST.getTextContent());
                }
            }

            if (COFINS != null) {
                Element COFINSAliq = (Element) COFINS.getElementsByTagName("COFINSAliq").item(0);
                Element COFINSQtde = (Element) COFINS.getElementsByTagName("COFINSQtde").item(0);
                Element COFINSNT = (Element) COFINS.getElementsByTagName("COFINSNT").item(0);
                Element COFINSOutr = (Element) COFINS.getElementsByTagName("COFINSOutr").item(0);

                Element CST = null;

                if (COFINSAliq != null) {
                    CST = (Element) COFINSAliq.getElementsByTagName("CST").item(0);
                    oItem.valorPisCofins += Double.parseDouble(COFINSAliq.getElementsByTagName("vCOFINS").item(0).getTextContent());

                } else if (COFINSQtde != null) {
                    CST = (Element) COFINSQtde.getElementsByTagName("CST").item(0);
                    oItem.valorPisCofins += Double.parseDouble(COFINSQtde.getElementsByTagName("vCOFINS").item(0).getTextContent());

                } else if (COFINSNT != null) {
                    CST = (Element) COFINSNT.getElementsByTagName("CST").item(0);

                } else if (COFINSOutr != null) {
                    CST = (Element) COFINSOutr.getElementsByTagName("CST").item(0);
                    oItem.valorPisCofins += Double.parseDouble(COFINSOutr.getElementsByTagName("vCOFINS").item(0).getTextContent());
                }
            }

            //ICMS
            if (ICMS != null) {
                Element ICMSItem = (Element) ICMS.getChildNodes().item(0);
                Element pRedBC = (Element) ICMSItem.getElementsByTagName("pRedBC").item(0);
                Element pICMS = (Element) ICMSItem.getElementsByTagName("pICMS").item(0);
                Element vBC = (Element) ICMSItem.getElementsByTagName("vBC").item(0);
                Element vICMS = (Element) ICMSItem.getElementsByTagName("vICMS").item(0);
                Element orig = (Element) ICMSItem.getElementsByTagName("orig").item(0);
                Element modBCST = (Element) ICMSItem.getElementsByTagName("modBCST").item(0);
                Element vBCST = (Element) ICMSItem.getElementsByTagName("vBCST").item(0);
                Element vBCSTRet = (Element) ICMSItem.getElementsByTagName("vBCSTRet").item(0);
                Element vICMSST = (Element) ICMSItem.getElementsByTagName("vICMSST").item(0);
                Element vICMSSTRet = (Element) ICMSItem.getElementsByTagName("vICMSSTRet").item(0);

                if (vBC != null) {
                    oItem.valorBaseCalculo = Double.parseDouble(vBC.getTextContent());
                }

                if (vICMS != null) {
                    oItem.valorIcms = Double.parseDouble(vICMS.getTextContent());
                }

                if (modBCST != null && !modBCST.getTextContent().equals("0")) {
                    if (modBCST.getTextContent().equals("5")) {
                        oItem.tipoIva = TipoPercentualValor.VALOR.getId();

                    } else if (modBCST.getTextContent().equals("4")) {
                        oItem.tipoIva = TipoPercentualValor.PERCENTUAL.getId();
                    }
                }

                if (vBCST != null) {
                    oItem.valorBaseSubstituicao = Double.parseDouble(vBCST.getTextContent());
                } else if (vBCSTRet != null) {
                    oItem.valorBaseSubstituicao = Double.parseDouble(vBCSTRet.getTextContent());
                }

                if (vICMSST != null) {
                    oItem.valorIcmsSubstituicao = Double.parseDouble(vICMSST.getTextContent());
                } else if (vICMSSTRet != null) {
                    oItem.valorIcmsSubstituicao = Double.parseDouble(vICMSSTRet.getTextContent());
                }

                ICMSItem.getTagName();
                Element CstCsosn = (Element) ICMSItem.getElementsByTagName("CST").item(0);

                if (CstCsosn == null) {
                    CstCsosn = (Element) ICMSItem.getElementsByTagName("CSOSN").item(0);
                }

                oItem.situacaoTributaria = Integer.parseInt(CstCsosn.getTextContent());
                oItem.idTipoOrigemMercadoria = Integer.parseInt(orig.getTextContent());

                if (oItem.situacaoTributaria == 20) {
                    oItem.valorIsento = Util.round(oItem.valorTotal - oItem.valorBaseCalculo, 2);
                } else if (oItem.situacaoTributaria == 40 || oItem.situacaoTributaria == 41 || oItem.situacaoTributaria == 50) {
                    oItem.valorIsento = oItem.valorTotal;
                } else {
                    oItem.valorIsento = 0;
                }

                //Aliquota
                if (oItem.situacaoTributaria == 90 || oItem.situacaoTributaria == 51) {
                    oItem.idAliquota = new AliquotaDAO().getIdOutras();

                } else if (oItem.situacaoTributaria == 60 || oItem.situacaoTributaria == 70 || oItem.situacaoTributaria == 10 || oItem.situacaoTributaria == 30) {
                    oItem.idAliquota = new AliquotaDAO().getIdSubstituido();

                } else if (oItem.situacaoTributaria == 40 || oItem.situacaoTributaria == 41 || oItem.situacaoTributaria == 50) {
                    oItem.idAliquota = new AliquotaDAO().getIdIsento();

                } else if (oItem.situacaoTributaria == 0) {
                    oItem.idAliquota = new AliquotaDAO().getId(Double.parseDouble(pICMS.getTextContent()), 0);

                } else if (oItem.situacaoTributaria == 20) {
                    oItem.idAliquota = new AliquotaDAO().getId(Double.parseDouble(pICMS.getTextContent()) , Double.parseDouble(pRedBC.getTextContent()));
                }
                
                if (oItem.idAliquota == -1) {
                    throw new Exception("Produto '" + cProd.getTextContent() + "' não possui uma aliquota válida no VR (ST: " + oItem.situacaoTributaria + " Aliq.: " + Double.parseDouble(pICMS.getTextContent()) + " Red.: " + Double.parseDouble(pRedBC.getTextContent()) + ")");
                }

                //ValorTotal
                if (oItem.situacaoTributaria == 90 || oItem.situacaoTributaria == 60 || oItem.situacaoTributaria == 30) {
                    oItem.valorOutras = oItem.valorTotal;
                }
            }
            
            NotaSaidaItemVO tp = somados.get(idProduto, Utils.stringToInt(cfop));
            
            if (tp != null) {
                //Converto a quantidade comprada usando a unidade por embalagem.
                oItem.quantidade = oItem.quantidade * oItem.qtdEmbalagem;
                oItem.qtdEmbalagem = 1;
                
                oItem.quantidade += tp.quantidade * tp.qtdEmbalagem;
                
                oItem.valorTotal += tp.valorTotal;
                oItem.valorBaseCalculo += tp.valorBaseCalculo;
                oItem.valorBaseIpi += tp.valorBaseIpi;
                oItem.valorBaseSubstituicao += tp.valorBaseSubstituicao;
                oItem.valorDesconto += tp.valorDesconto;
                oItem.valorIcms += tp.valorIcms;
                oItem.valorIcmsDispensado += tp.valorIcmsDispensado;
                oItem.valorIcmsSubstituicao += tp.valorIcmsSubstituicao;
                oItem.valorIpi += tp.valorIpi;
                oItem.valorIsento += tp.valorIsento;
                oItem.valorOutras += tp.valorOutras;
                oItem.valorPisCofins += tp.valorPisCofins;
                oItem.valorTotalIpi += tp.valorTotalIpi;
            }       
            
            oItem.valor = Util.round(oItem.valorTotal / (oItem.quantidade * oItem.qtdEmbalagem), 2);
            
            somados.put(oItem, idProduto, Utils.stringToInt(cfop));
            
        }
        
        for (NotaSaidaItemVO tp: somados.values()) {
            oNotaSaida.vItem.add(tp);
        }
        
        //total nota
        Element total = (Element) infNFe.getElementsByTagName("total").item(0);
        Element ICMSTot = (Element) total.getElementsByTagName("ICMSTot").item(0);

        Element vBC = (Element) ICMSTot.getElementsByTagName("vBC").item(0);
        oNotaSaida.valorBaseCalculo = Double.parseDouble(vBC.getTextContent());

        Element vICMS = (Element) ICMSTot.getElementsByTagName("vICMS").item(0);
        oNotaSaida.valorIcms = Double.parseDouble(vICMS.getTextContent());

        Element vBCST = (Element) ICMSTot.getElementsByTagName("vBCST").item(0);
        oNotaSaida.valorBaseSubstituicao = Double.parseDouble(vBCST.getTextContent());

        Element vST = (Element) ICMSTot.getElementsByTagName("vST").item(0);
        oNotaSaida.valorIcmsSubstituicao = Double.parseDouble(vST.getTextContent());

        Element vFrete = (Element) ICMSTot.getElementsByTagName("vFrete").item(0);
        oNotaSaida.valorFrete = Double.parseDouble(vFrete.getTextContent());

        Element vDesc = (Element) ICMSTot.getElementsByTagName("vDesc").item(0);
        oNotaSaida.valorDesconto = Double.parseDouble(vDesc.getTextContent());

        Element vIPI = (Element) ICMSTot.getElementsByTagName("vIPI").item(0);
        oNotaSaida.valorIpi = Double.parseDouble(vIPI.getTextContent());

        Element vOutro = (Element) ICMSTot.getElementsByTagName("vOutro").item(0);
        oNotaSaida.valorOutrasDespesas = Double.parseDouble(vOutro.getTextContent());

        Element vProd = (Element) ICMSTot.getElementsByTagName("vProd").item(0);
        oNotaSaida.valorProduto = Double.parseDouble(vProd.getTextContent()) - Double.parseDouble(vDesc.getTextContent()) + Double.parseDouble(vOutro.getTextContent()) + Double.parseDouble(vFrete.getTextContent());

        oNotaSaida.valorBaseIpi = oNotaSaida.valorProduto;

        Element vNF = (Element) ICMSTot.getElementsByTagName("vNF").item(0);
        oNotaSaida.valorTotal = Double.parseDouble(vNF.getTextContent());

        Element vSeg = (Element) ICMSTot.getElementsByTagName("vSeg").item(0);
        oNotaSaida.valorSeguro = Double.parseDouble(vSeg.getTextContent());

        Element transp = (Element) infNFe.getElementsByTagName("transp").item(0);

        if (transp != null) {
            Element modFrete = (Element) transp.getElementsByTagName("modFrete").item(0);

            if (modFrete.getTextContent().equals("0")) {
                oNotaSaida.idTipoFreteNotaFiscal = TipoFreteNotaFiscal.EMITENTE.getId();

            } else if (modFrete.getTextContent().equals("1")) {
                oNotaSaida.idTipoFreteNotaFiscal = TipoFreteNotaFiscal.DESTINATARIO.getId();

            } else if (modFrete.getTextContent().equals("2")) {
                oNotaSaida.idTipoFreteNotaFiscal = TipoFreteNotaFiscal.TERCEIRO.getId();

            } else {
                oNotaSaida.idTipoFreteNotaFiscal = TipoFreteNotaFiscal.SEM_COBRANCA.getId();
            }

            Element transporta = (Element) transp.getElementsByTagName("transporta").item(0);
            Element cpfMotorista = null;
            
            if (transporta != null) {
                cpfMotorista = (Element) transporta.getElementsByTagName("CPF").item(0);

                if (cpfMotorista == null) {
                    cpfMotorista = (Element) transporta.getElementsByTagName("CNPJ").item(0);
                }

                if (cpfMotorista == null) {
                    cpfMotorista = (Element) dest.getElementsByTagName("CNPJ").item(0);
                }

                if (cpfMotorista == null) {
                    cpfMotorista = (Element) dest.getElementsByTagName("CPF").item(0);
                }
            } else {
                cpfMotorista = (Element) dest.getElementsByTagName("CPF").item(0);
                if (cpfMotorista == null) {
                    cpfMotorista = (Element) dest.getElementsByTagName("CNPJ").item(0);
                }
            }
            
            TipoFornecedorVO motorista = new TipoFornecedorVO();
            if (cpfMotorista != null && !cpfMotorista.getTextContent().equals("")) {
                motorista = new FornecedorDAO().carregarTipo(Long.parseLong(cpfMotorista.getTextContent()));

                if (motorista.tipo.equals("F")) {
                    oNotaSaida.idFornecedorTransportador = motorista.id;

                } else if (motorista.tipo.equals("C")) {
                    oNotaSaida.idClienteEventualTransportador = motorista.id;

                } else {
                    oNotaSaida.idMotoristaTransportador = motorista.id;
                }
            }
        }

        Element cobr = (Element) infNFe.getElementsByTagName("cobr").item(0);

        if (cobr != null) {
            NodeList vCobr = cobr.getElementsByTagName("dup");

            for (int i = 0; i < vCobr.getLength(); i++) {
                NotaSaidaVencimentoVO oVencimento = new NotaSaidaVencimentoVO();
                Element dup = (Element) vCobr.item(i);
                Element dVenc = (Element) dup.getElementsByTagName("dVenc").item(0);
                Element vDup = (Element) dup.getElementsByTagName("vDup").item(0);

                oVencimento.dataVencimento = Util.formatData(dVenc.getTextContent(), "yyyy-MM-dd", "dd/MM/yyyy");
                oVencimento.valor = Double.parseDouble(vDup.getTextContent());

                oNotaSaida.vVencimento.add(oVencimento);
            }
        }

        Element infProt = (Element) doc.getDocumentElement().getElementsByTagName("infProt").item(0);

        if (infProt != null) {
            Element dhRecbto = (Element) infProt.getElementsByTagName("dhRecbto").item(0);
            Element nProt = (Element) infProt.getElementsByTagName("nProt").item(0);

            oNotaSaida.dataHoraRecebimentoNfe = dhRecbto.getTextContent().replace("T", " ");
            oNotaSaida.protocoloRecebimentoNfe = nProt.getTextContent().length() > 15 ? nProt.getTextContent().substring(0, 15) : nProt.getTextContent();

        } else {
            oNotaSaida.dataHoraRecebimentoNfe = "1900-01-01 00:00:00";
            oNotaSaida.protocoloRecebimentoNfe = "";
        }

        //obtem xml
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer trans = tf.newTransformer();

        StreamResult destination = new StreamResult(new StringWriter());
        trans.transform(new DOMSource(doc), destination);

        oNotaSaida.xml = destination.getWriter().toString();

        return oNotaSaida;
    }

    public String getXML(String i_arquivo) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = dbf.newDocumentBuilder();
        Document doc = docBuilder.parse(new File(i_arquivo));

        DOMSource domSource = new DOMSource(doc);
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);

        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();

        transformer.transform(domSource, result);

        return writer.toString();
    }
}
