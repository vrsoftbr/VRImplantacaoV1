package vrimplantacao.dao.notafiscal;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrframework.classe.Util;
import vrframework.classe.VRException;
import vrimplantacao.classe.Global;
import vrimplantacao.dao.LogTransacaoDAO;
import vrimplantacao.dao.ParametroDAO;
import vrimplantacao.dao.ParametroPdvDAO;
import vrimplantacao.dao.cadastro.ProdutoDAO;
import vrimplantacao.dao.estoque.EstoqueDAO;
import vrimplantacao.gui.interfaces.rfd.ItensNaoExistentesController;
import vrimplantacao.gui.interfaces.rfd.ProdutoMapa;
import vrimplantacao.gui.interfaces.rfd.ProdutoMapa.TipoMapa;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.Formulario;
import vrimplantacao.vo.TipoBaixaReceita;
import vrimplantacao.vo.TipoTransacao;
import vrimplantacao.vo.administrativo.AcertoEstoqueVO;
import vrimplantacao.vo.administrativo.TipoEntradaSaida;
import vrimplantacao.vo.administrativo.TipoMovimentacao;
import vrimplantacao.vo.estoque.TipoBaixaPerda;
import vrimplantacao.vo.fiscal.ModeloNotaFiscal;
import vrimplantacao.vo.interfaces.DivergenciaVO;
import vrimplantacao.vo.interfaces.TipoDivergencia;
import vrimplantacao.vo.venda.Finalizadora;
import vrimplantacao.vo.venda.VendaFinalizadoraVO;
import vrimplantacao.vo.venda.VendaItemVO;
import vrimplantacao.vo.venda.VendaVO;
import vrimplantacao2.dao.cadastro.produto.ProdutoAnteriorDAO;
import vrimplantacao2.utils.multimap.MultiMap;

public class NotaSaidaNfceDAO {

    private ArrayList<DivergenciaVO> vDivergencia = null;

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

    
    private final SimpleDateFormat xmlDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");    
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private final SimpleDateFormat timeStampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final SimpleDateFormat horaFormat = new SimpleDateFormat("HH:mm:ss");
            
    public VendaVO importar(String i_xml, boolean verificarCodigoAnterior, boolean verificarCodigoBarras, boolean exibirDivergencias) throws Exception {
        //abre arquivo
        boolean importacaoV2 = isImportacaoV2();
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = dbf.newDocumentBuilder();
            docBuilder.setErrorHandler(null);
            Integer codigoProduto = -1;
            vDivergencia = new ArrayList();

            ItensNaoExistentesController dao = new ItensNaoExistentesController();

            MultiMap<String, ProdutoMapa> mapa = new MultiMap<>();
            //TODO: Incluir mais opções            
            ProdutoMapa.TipoMapa tipo = ProdutoMapa.TipoMapa.EAN;
            for (ProdutoMapa mp : dao.carregarMapa(true, tipo)) {
                mapa.put(mp, mp.getTipo().toString(), mp.getCodrfd());
            }

            VendaVO oVenda = new VendaVO();

            Document doc = docBuilder.parse(new ByteArrayInputStream(getXML(i_xml).getBytes("utf-8")));

            Element infNFe = (Element) doc.getDocumentElement().getElementsByTagName("infNFe").item(0);

            Attr attr = (Attr) infNFe.getAttributeNode("Id");

            String attrNfceCompleta = attr.getNodeValue();
            String chaveNfce = attrNfceCompleta.split("NFe")[1];

            Element ide = (Element) infNFe.getElementsByTagName("ide").item(0);
            Element modelo = (Element) ide.getElementsByTagName("mod").item(0);

            if (modelo != null && !modelo.getTextContent().equals(ModeloNotaFiscal.NFCE.getModelo())) {
                throw new VRException("Arquivo inválido!");
            }

            Element serie = (Element) ide.getElementsByTagName("serie").item(0);
            Element nNF = (Element) ide.getElementsByTagName("nNF").item(0);
            Element dhEmi = (Element) infNFe.getElementsByTagName("dhEmi").item(0);
            Element tpEmis = (Element) infNFe.getElementsByTagName("tpEmis").item(0);

            Element dest = (Element) infNFe.getElementsByTagName("dest").item(0);
            Element CNPJ = null;
            Element CPF = null;

            if (dest != null) {
                CNPJ = (Element) dest.getElementsByTagName("CNPJ").item(0);
                CPF = (Element) dest.getElementsByTagName("CPF").item(0);
            }

            Element emit = (Element) infNFe.getElementsByTagName("emit").item(0);
            Element CNPJEmit = (Element) emit.getElementsByTagName("CNPJ").item(0);

            Element total = (Element) infNFe.getElementsByTagName("total").item(0);
            Element ICMSTot = (Element) total.getElementsByTagName("ICMSTot").item(0);
            Element vDesc = (Element) ICMSTot.getElementsByTagName("vDesc").item(0);
            Element vNF = (Element) ICMSTot.getElementsByTagName("vNF").item(0);

            NodeList vDet = infNFe.getElementsByTagName("det");
            NodeList vPags = infNFe.getElementsByTagName("pag");

            int idLoja = getIdLoja(Long.parseLong(CNPJEmit.getTextContent()));

            //String data = Format.data(dhEmi.getTextContent(), "yyyy-MM-dd'T'HH:mm:ss", "dd/MM/yyyy");
            Date dataEmissao = xmlDateFormat.parse(dhEmi.getTextContent());
            //String data = Util.formatData(dhEmi.getTextContent(), "yyyy-MM-dd'T'HH:mm:ss", "dd/MM/yyyy");
            //String dataEmissaoNfce = Util.formatData(dhEmi.getTextContent(), "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd HH:mm:ss");
            //String horaInicio = Util.formatData(dhEmi.getTextContent(), "yyyy-MM-dd'T'HH:mm:ss", "HH:mm:ss");
            //String horaTermino = horaInicio;
            
            double valorSubTotal = Double.parseDouble(vNF.getTextContent());
            double valorDesconto = Double.parseDouble(vDesc.getTextContent());

            String protocoloRecebimento = "";
            Element infProt = (Element) doc.getDocumentElement().getElementsByTagName("infProt").item(0);

            if (infProt != null) {
                protocoloRecebimento = ((Element) infProt.getElementsByTagName("nProt").item(0)).getTextContent();
            }

            long cpf = 0;

            if (CNPJ == null && CPF != null) {
                if (!CPF.getTextContent().trim().isEmpty()) {
                    cpf = Long.parseLong(CPF.getTextContent());
                }

            } else if (CPF == null && CNPJ != null) {
                if (!CNPJ.getTextContent().trim().isEmpty()) {
                    cpf = Long.parseLong(CNPJ.getTextContent());
                }
            }

            oVenda.idLoja = idLoja;
            oVenda.cnpjEmitente = Long.parseLong(CNPJEmit.getTextContent());
            oVenda.dataHoraEmissaoNfce = timeStampFormat.format(dataEmissao);
            oVenda.data = simpleDateFormat.format(dataEmissao);
            oVenda.finalizado = true;
            oVenda.horaInicio = horaFormat.format(dataEmissao);
            oVenda.horaTermino = oVenda.horaInicio;
            oVenda.protocoloRecebimentoNfce = protocoloRecebimento;
            oVenda.ecf = Integer.parseInt(serie.getTextContent());
            oVenda.subTotalImpressora = valorSubTotal;
            oVenda.idClientePreferencial = -1;
            oVenda.matricula = 500001; //administrador
            oVenda.matriculaCancelamento = -1;
            oVenda.idTipoCancelamento = -1;
            oVenda.idClienteEventual = -1;
            oVenda.valorDesconto = valorDesconto;

            int idModelo = getIdModelo(oVenda.ecf, oVenda.idLoja);

            oVenda.modeloImpressora = getModelo(idModelo);
            oVenda.numeroSerie = getNumeroSerie(oVenda.ecf, oVenda.idLoja);
            oVenda.chaveNfce = chaveNfce;
            oVenda.cpf = cpf;
            oVenda.xml = getXML(i_xml).trim();
            oVenda.transmitido = true;
            oVenda.contingencia = (!tpEmis.getTextContent().equals("1"));

            for (int i = 0; i < vDet.getLength(); i++) {
                Element det = (Element) vDet.item(i);
                Attr attrNItem = (Attr) det.getAttributeNode("nItem");
                String sequencia = attrNItem.getNodeValue();

                Element prod = (Element) det.getElementsByTagName("prod").item(0);
                Element imposto = (Element) det.getElementsByTagName("imposto").item(0);

                Element ICMS00 = (Element) imposto.getElementsByTagName("ICMS00").item(0);
                Element ICMS40 = (Element) imposto.getElementsByTagName("ICMS40").item(0);
                Element ICMS60 = (Element) imposto.getElementsByTagName("ICMS60").item(0);
                Element ICMS90 = (Element) imposto.getElementsByTagName("ICMS90").item(0);

                Element pICMS = (Element) imposto.getElementsByTagName("pICMS").item(0);

                Element cProd = (Element) prod.getElementsByTagName("cProd").item(0);
                Element vProd = (Element) prod.getElementsByTagName("vProd").item(0);
                Element xProd = (Element) prod.getElementsByTagName("xProd").item(0);
                Element vUnTrib = (Element) prod.getElementsByTagName("vUnTrib").item(0);
                Element vDescUn = (Element) prod.getElementsByTagName("vDesc").item(0);
                Element cEAN = (Element) prod.getElementsByTagName("cEAN").item(0);
                Element uCom = (Element) prod.getElementsByTagName("uCom").item(0);
                String unidadeMedida = uCom.getTextContent().substring(0, 2);

                Element qCom = (Element) prod.getElementsByTagName("qCom").item(0);

                if (verificarCodigoAnterior) {
                    String codigo = cProd.getTextContent().replace("'", "").replace("\n", "").trim();
                    ProdutoMapa mp = mapa.get(tipo.toString(), codigo);
                    if (importacaoV2) {
                        daoV2.setImportSistema(impLoja.impSistema);
                        daoV2.setImportLoja(impLoja.impLoja);

                        if (mp != null && mp.getCodigoAtual() > 0) {
                            codigoProduto = mp.getCodigoAtual();
                        } else {
                            codigoProduto = daoV2.getCodigoAnterior2(daoV2.getImportSistema(), daoV2.getImportLoja(), codigo);
                        }
                    } else {
                        if (mp != null && mp.getCodigoAtual() > 0) {
                            codigoProduto = mp.getCodigoAtual();
                        } else {
                            codigoProduto = new ProdutoDAO().getIdAnterior(Long.parseLong(cProd.getTextContent()));
                        }
                    }

                    if (codigoProduto <= 0) {
                        if (exibirDivergencias) {
                            vDivergencia.add(new DivergenciaVO("Código do Produto :" + cProd.getTextContent().replace("'", "").replace("\n", "").trim()
                                    + "Descrição :" + xProd.getTextContent() + "não cadastrado", TipoDivergencia.ERRO.getId()));
                            dao.armazenar(TipoMapa.EAN, Utils.formataNumero(cProd.getTextContent()), xProd.getTextContent());
                            ProgressBar.next();
                            continue;
                        } else {
                            codigoProduto = new ParametroPdvDAO().get(28).getInt();
                        }
                    }
                } else if (verificarCodigoBarras) {
                    ProdutoMapa mp = mapa.get(tipo.toString(), String.valueOf(Utils.formataNumero(cProd.getTextContent())));
                    if (importacaoV2) {
                        daoV2.setImportSistema(impLoja.impSistema);
                        daoV2.setImportLoja(impLoja.impLoja);

                        if (mp != null && mp.getCodigoAtual() > 0) {
                            codigoProduto = mp.getCodigoAtual();
                        } else {
                            codigoProduto = daoV2.getCodigoAtualEANant(daoV2.getImportSistema(), daoV2.getImportLoja(), cProd.getTextContent().replace("'", "").replace("\n", "").trim());
                        }
                    } else {

                        if (mp != null && mp.getCodigoAtual() > 0) {
                            codigoProduto = mp.getCodigoAtual();
                        } else {
                            codigoProduto = new ProdutoDAO().getIdAnterior(Long.parseLong(cProd.getTextContent()));
                        }
                    }

                    if (codigoProduto <= 0) {
                        if (exibirDivergencias) {
                            vDivergencia.add(new DivergenciaVO("Código de Barras:" + cProd.getTextContent().replace("'", "").replace("\n", "").trim()
                                    + "Descrição :" + xProd.getTextContent() + "não cadastrado", TipoDivergencia.ERRO.getId()));
                            dao.armazenar(TipoMapa.EAN, Utils.formataNumero(cProd.getTextContent()), xProd.getTextContent());
                            ProgressBar.next();
                            continue;
                        } else {
                            codigoProduto = new ParametroPdvDAO().get(28).getInt();
                        }
                    }
                } else {
                    codigoProduto = Integer.parseInt(cProd.getTextContent().replace("'", "").replace("\n", "").trim());
                }

                int idAliquotaICMS = 0;

                if (ICMS00 != null) {
                    Element CST = (Element) ICMS00.getElementsByTagName("CST").item(0);
                    idAliquotaICMS = getIdAliquotaICMS(Integer.parseInt(CST.getTextContent()), Double.parseDouble(pICMS.getTextContent()));

                } else if (ICMS40 != null) {
                    Element CST = (Element) ICMS40.getElementsByTagName("CST").item(0);
                    idAliquotaICMS = getIdAliquotaICMS(Integer.parseInt(CST.getTextContent()), 0);

                } else if (ICMS60 != null) {
                    Element CST = (Element) ICMS60.getElementsByTagName("CST").item(0);
                    idAliquotaICMS = getIdAliquotaICMS(Integer.parseInt(CST.getTextContent()), 0);

                } else if (ICMS90 != null) {
                    Element CST = (Element) ICMS90.getElementsByTagName("CST").item(0);
                    idAliquotaICMS = getIdAliquotaICMS(Integer.parseInt(CST.getTextContent()), Double.parseDouble(pICMS.getTextContent()));
                }

                String codigoBarras;
                
                codigoBarras = ("0".equals(Utils.formataNumero(cEAN.getTextContent())) ? String.valueOf(codigoProduto) : cEAN.getTextContent());

                VendaItemVO oVendaItem = new VendaItemVO();
                oVendaItem.idProduto = codigoProduto;
                oVendaItem.quantidade = Double.parseDouble(qCom.getTextContent());
                oVendaItem.valorTotal = Double.parseDouble(vProd.getTextContent());
                oVendaItem.precoVenda = Double.parseDouble(vUnTrib.getTextContent());
                oVendaItem.idAliquota = idAliquotaICMS;
                oVendaItem.codigoBarras = Long.parseLong(codigoBarras);
                oVendaItem.valorDesconto = (vDescUn == null ? 0 : Double.parseDouble(vDescUn.getTextContent()));
                oVendaItem.regraCalculo = "T";
                oVendaItem.sequencia = Integer.parseInt(sequencia);
                oVendaItem.unidadeMedida = unidadeMedida;
                oVendaItem.idTipoCancelamento = -1;
                oVendaItem.matriculaCancelamento = -1;

                oVenda.vItem.add(oVendaItem);
            }

            for (int i = 0; i < vPags.getLength(); i++) {
                Element pag = (Element) vPags.item(i);
                Element tPag = (Element) pag.getElementsByTagName("tPag").item(0);
                Element vPag = (Element) pag.getElementsByTagName("vPag").item(0);

                int idFinalizadora = getFinalizadoraVenda(tPag.getTextContent());

                double valorFinalizadora = Double.parseDouble(vPag.getTextContent());

                VendaFinalizadoraVO oVendaFinalizadora = new VendaFinalizadoraVO();

                oVendaFinalizadora.idFinalizadora = idFinalizadora;
                oVendaFinalizadora.idTipoTef = -1;
                oVendaFinalizadora.idTipoTicket = -1;
                oVendaFinalizadora.valor = valorFinalizadora;

                oVenda.vFinalizadora.add(oVendaFinalizadora);
            }

            oVenda.numeroNFCe = Integer.parseInt(nNF.getTextContent());
            oVenda.numeroCupom = oVenda.numeroNFCe;
            dao.gravar();

            if (!vDivergencia.isEmpty()) {
                throw new VRException("Existe(m) produto(s) do arquivo não cadastrado(s). Verificar clicando no Botão DIVERGÊNCIAS");
            }

            return oVenda;

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }

    private String getXML(String i_arquivo) throws Exception {
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

    public void salvarVenda(VendaVO i_oVenda) throws Exception {
        Statement stm = null;
        StringBuilder sql = null;
        ResultSet rst = null;

        try {
            stm = Conexao.createStatement();
            Conexao.begin();

            if (isVendaCadastrada(i_oVenda)) {
                throw new VRException("Venda já cadastrada no sistema!");
            }

            //Inclui a nova venda vinda do xml da receita
            sql = new StringBuilder();
            sql.append("INSERT INTO pdv.venda (id_loja, numerocupom, ecf, data, id_clientepreferencial,");
            sql.append(" matricula, horainicio, horatermino, cancelado, subtotalimpressora, matriculacancelamento,");
            sql.append(" id_tipocancelamento, cpf, contadordoc, valordesconto, valoracrescimo, canceladoemvenda, numeroserie,");
            sql.append(" mfadicional, modeloimpressora, numerousuario, nomecliente, enderecocliente, id_clienteeventual, cpfcrm, chavecfe,");
            sql.append(" xml, chavenfce, protocolorecebimentonfce, datahoraemissaonfce, datahorarecebimentonfce, recibonfce, razaosocialentidade, cpfcnpjentidade) VALUES (");
            sql.append(i_oVenda.idLoja + ", ");
            sql.append(i_oVenda.numeroCupom + ", ");
            sql.append(i_oVenda.ecf + ", ");
            sql.append("'" + Util.formatDataBanco(i_oVenda.data) + "', ");
            sql.append((i_oVenda.idClientePreferencial == -1 ? "NULL" : i_oVenda.idClientePreferencial) + ", ");
            sql.append((i_oVenda.matricula == -1 ? "NULL" : i_oVenda.matricula) + ", ");
            sql.append("'" + i_oVenda.horaInicio + "', ");
            sql.append("'" + i_oVenda.horaTermino + "', ");
            sql.append(i_oVenda.cancelado + ", ");
            sql.append(i_oVenda.subTotalImpressora + ", ");
            sql.append((i_oVenda.matriculaCancelamento == -1 ? "NULL" : i_oVenda.matriculaCancelamento) + ", ");
            sql.append((i_oVenda.idTipoCancelamento == -1 ? "NULL" : i_oVenda.idTipoCancelamento) + ", ");
            sql.append(i_oVenda.cpf + ", ");
            sql.append(i_oVenda.contadorDoc + ", ");
            sql.append(i_oVenda.valorDesconto + ", ");
            sql.append(i_oVenda.valorAcrescimo + ", ");
            sql.append(i_oVenda.canceladoEmVenda + ", ");
            sql.append("'" + i_oVenda.numeroSerie + "', ");
            sql.append(i_oVenda.mfAdicional + ", ");
            sql.append("'" + i_oVenda.modeloImpressora + "', ");
            sql.append(i_oVenda.numeroUsuario + ", ");
            sql.append("'" + i_oVenda.nomeCliente + "', ");
            sql.append("'" + i_oVenda.enderecoCliente + "', ");
            sql.append((i_oVenda.idClienteEventual == -1 ? "NULL" : i_oVenda.idClienteEventual) + ",");
            sql.append(i_oVenda.cpfCrm + ", ");
            sql.append("'" + i_oVenda.chaveCFe + "', ");
            sql.append("'" + i_oVenda.xml.replace("'", "") + "', ");
            sql.append("'" + i_oVenda.chaveNfce + "', ");
            sql.append("'" + i_oVenda.protocoloRecebimentoNfce + "', ");
            sql.append((i_oVenda.dataHoraEmissaoNfce.isEmpty() ? "NULL" : "'" + i_oVenda.dataHoraEmissaoNfce + "'") + ", ");
            sql.append((i_oVenda.dataHoraRecebimentoNfce.isEmpty() ? "NULL" : "'" + i_oVenda.dataHoraRecebimentoNfce + "'") + ", ");
            sql.append("'" + i_oVenda.reciboNfce + "',");
            sql.append("'" + i_oVenda.razaoSocialEntidade + "',");
            sql.append("" + i_oVenda.cpfCnpjEntidade + ");");
            
            stm.execute(sql.toString());

            rst = stm.executeQuery("SELECT CURRVAL('pdv.venda_id_seq') AS id");
            rst.next();

            long idVenda = rst.getLong("id");

            for (VendaItemVO oItem : i_oVenda.vItem) {
                sql = new StringBuilder();
                sql.append("INSERT INTO pdv.vendaitem (id_venda, id_produto, quantidade, precovenda, valortotal, id_aliquota,");
                sql.append(" cancelado, valorcancelado, id_tipocancelamento, matriculacancelamento, contadordoc, valordesconto,");
                sql.append(" valoracrescimo, valordescontocupom, valoracrescimocupom, regracalculo, codigobarras, unidademedida,");
                sql.append(" totalizadorparcial, sequencia, valoracrescimofixo, valordescontopromocao) VALUES (");
                sql.append(idVenda + ", ");
                sql.append(oItem.idProduto + ", ");
                sql.append(oItem.quantidade + ", ");
                sql.append(oItem.precoVenda + ", ");
                sql.append(oItem.valorTotal + ", ");
                sql.append(oItem.idAliquota + ", ");
                sql.append(oItem.cancelado + ", ");
                sql.append(oItem.valorCancelado + ", ");
                sql.append((oItem.idTipoCancelamento == -1 ? "NULL" : oItem.idTipoCancelamento) + ", ");
                sql.append((oItem.matriculaCancelamento == -1 ? "NULL" : oItem.matriculaCancelamento) + ", ");
                sql.append(oItem.contadorDoc + ", ");
                sql.append(oItem.valorDesconto + ", ");
                sql.append(oItem.valorAcrescimo + ", ");
                sql.append(oItem.valorDescontoCupom + ", ");
                sql.append(oItem.valorAcrescimoCupom + ", ");
                sql.append("'" + oItem.regraCalculo + "', ");
                sql.append(oItem.codigoBarras + ", ");
                sql.append("'" + oItem.unidadeMedida + "', ");
                sql.append("'" + oItem.totalizadorParcial + "', ");
                sql.append(oItem.sequencia + ",");
                sql.append(oItem.valorAcrescimoFixo + ",");
                sql.append(oItem.valorDescontoPromocao + ")");

                stm.execute(sql.toString());

                if (i_oVenda.baixarEstoque) {
                    baixarEstoqueOnLine(oItem);
                }
            }

            for (VendaFinalizadoraVO oFinalizadora : i_oVenda.vFinalizadora) {
                sql = new StringBuilder();
                sql.append("INSERT INTO pdv.vendafinalizadora (id_venda, id_finalizadora, id_tipotef, id_tipoticket, valor, troco) VALUES (");
                sql.append(idVenda + ", ");
                sql.append(oFinalizadora.idFinalizadora + ", ");
                sql.append((oFinalizadora.idTipoTef == -1 ? "NULL" : oFinalizadora.idTipoTef) + ", ");
                sql.append((oFinalizadora.idTipoTicket == -1 ? "NULL" : oFinalizadora.idTipoTicket) + ", ");
                sql.append(oFinalizadora.valor + ", ");
                sql.append(oFinalizadora.troco + ")");

                stm.execute(sql.toString());
            }

            if (i_oVenda.chaveNfce != null && !i_oVenda.chaveNfce.trim().isEmpty()) {
                sql = new StringBuilder();
                sql.append("INSERT INTO pdv.vendanfce (id_venda, xml, chavenfce, protocolorecebimentonfce, datahoraemissaonfce,");
                sql.append(" datahorarecebimentonfce, recibonfce, transmitido, contingencia, protocolocancelamentonfce,");
                sql.append(" datahoracancelamentonfce, justificativacancelamento) VALUES (");
                sql.append(idVenda + ", ");
                sql.append("'" + i_oVenda.xml.replace("'", "") + "', ");
                sql.append("'" + i_oVenda.chaveNfce + "', ");
                sql.append("'" + i_oVenda.protocoloRecebimentoNfce + "', ");
                sql.append((i_oVenda.dataHoraEmissaoNfce.isEmpty() ? "NULL" : "'" + i_oVenda.dataHoraEmissaoNfce + "'") + ", ");
                sql.append((i_oVenda.dataHoraRecebimentoNfce.isEmpty() ? "NULL" : "'" + i_oVenda.dataHoraRecebimentoNfce + "'") + ", ");
                sql.append("'" + i_oVenda.reciboNfce + "',");
                sql.append(i_oVenda.transmitido + ",");
                sql.append(i_oVenda.contingencia + ",");
                sql.append("'" + i_oVenda.protocoloCancelamentoNfce + "', ");
                sql.append((i_oVenda.dataHoraCancelamentoNfce.equals("") ? "NULL" : "'" + i_oVenda.dataHoraCancelamentoNfce + "'") + ", ");
                sql.append("'" + i_oVenda.justificativaCancelamento + "')");

                stm.execute(sql.toString());
            }

            new LogTransacaoDAO().gerar(Formulario.INTERFACE_IMPORTACAO_NFCE, TipoTransacao.IMPORTACAO, i_oVenda.numeroCupom, "", idVenda);

            Conexao.commit();
            stm.close();

        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void atualizarVendaNFCe(long i_idVendaNFCe, VendaVO i_oVenda) throws Exception {
        Statement stm = null;
        StringBuilder sql = null;
        ResultSet rst = null;
        int idVenda = -1;

        try {
            stm = Conexao.createStatement();
            Conexao.begin();

            // VENDA
            sql = new StringBuilder();
            sql.append("UPDATE pdv.vendanfce SET");
            sql.append(" xml = '" + i_oVenda.xml.replace("'", "") + "', ");
            sql.append(" chavenfce = '" + i_oVenda.chaveNfce + "', ");
            sql.append(" protocolorecebimentonfce = '" + i_oVenda.protocoloRecebimentoNfce + "', ");
            sql.append(" datahoraemissaonfce = " + (i_oVenda.dataHoraEmissaoNfce.isEmpty() ? "NULL" : "'" + i_oVenda.dataHoraEmissaoNfce + "'") + ", ");
            sql.append(" datahorarecebimentonfce = " + (i_oVenda.dataHoraRecebimentoNfce.isEmpty() ? "NULL" : "'" + i_oVenda.dataHoraRecebimentoNfce + "'") + ", ");
            sql.append(" recibonfce = '" + i_oVenda.reciboNfce + "',");
            sql.append(" transmitido = " + i_oVenda.transmitido + ",");
            sql.append(" contingencia = " + i_oVenda.contingencia + ",");
            sql.append(" protocolocancelamentonfce = '" + i_oVenda.protocoloCancelamentoNfce + "', ");
            sql.append(" datahoracancelamentonfce = " + (i_oVenda.dataHoraCancelamentoNfce.equals("") ? "NULL" : "'" + i_oVenda.dataHoraCancelamentoNfce + "'") + ", ");
            sql.append(" justificativacancelamento = '" + i_oVenda.justificativaCancelamento + "'");
            sql.append(" WHERE id = " + i_idVendaNFCe);

            stm.execute(sql.toString());

            // VENDA NFCE
            sql = new StringBuilder();
            sql.append("SELECT id_venda FROM pdv.vendanfce WHERE id = " + i_idVendaNFCe);
            rst = stm.executeQuery(sql.toString());

            if (rst.next()) {
                idVenda = rst.getInt("id_venda");

                sql = new StringBuilder();
                sql.append("UPDATE pdv.venda SET");
                sql.append("  xml = '" + i_oVenda.xml.replace("'", "") + "', ");
                sql.append("  chavenfce = '" + i_oVenda.chaveNfce + "', ");
                sql.append("  protocolorecebimentonfce = '" + i_oVenda.protocoloRecebimentoNfce + "', ");
                sql.append("  datahoraemissaonfce = " + (i_oVenda.dataHoraEmissaoNfce.isEmpty() ? "NULL" : "'" + i_oVenda.dataHoraEmissaoNfce + "'") + ", ");
                sql.append("  datahorarecebimentonfce = " + (i_oVenda.dataHoraRecebimentoNfce.isEmpty() ? "NULL" : "'" + i_oVenda.dataHoraRecebimentoNfce + "'") + ", ");
                sql.append("  recibonfce = '" + i_oVenda.reciboNfce + "'");
                sql.append(" WHERE id = " + idVenda);

                stm.execute(sql.toString());
            }

            //new LogTransacaoDAO().gerar(Formulario.INTERFACE_IMPORTACAO_NFCE, TipoTransacao.IMPORTACAO, i_oVenda.numeroCupom, "ATUALIZACAO NFCE " + Format.number(i_oVenda.numeroNFCe, 6), i_oVenda.id);
            new LogTransacaoDAO().gerar(Formulario.INTERFACE_IMPORTACAO_NFCE, TipoTransacao.IMPORTACAO, i_oVenda.numeroCupom, "ATUALIZACAO NFCE " + Util.formatNumber(i_oVenda.numeroNFCe, 6), i_oVenda.id);

            Conexao.commit();
            stm.close();

        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    private int getIdModelo(int i_ecf, int i_idLoja) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;

        stm = Conexao.createStatement();

        sql = new StringBuilder();
        sql.append("SELECT id_tipomodelo FROM pdv.ecf");
        sql.append(" WHERE ecf = " + i_ecf + " AND id_loja = " + i_idLoja);

        rst = stm.executeQuery(sql.toString());

        if (!rst.next()) {
            throw new VRException("Tipo modelo não encontrado!");
        }

        return rst.getInt("id_tipomodelo");
    }

    private boolean isVendaCadastrada(VendaVO i_oVenda) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;

        stm = Conexao.createStatement();

        sql = new StringBuilder();
        sql.append("SELECT COUNT(1) AS total FROM pdv.venda");
        sql.append(" WHERE ecf=" + i_oVenda.ecf);
        sql.append(" AND id_loja=" + i_oVenda.idLoja);
        //sql.append(" AND data='" + Format.dataBanco(i_oVenda.data) + "'");
        sql.append(" AND data='" + Util.formatDataBanco(i_oVenda.data) + "'");
        sql.append(" AND numerocupom=" + i_oVenda.numeroCupom);

        rst = stm.executeQuery(sql.toString());

        if (rst.next()) {
            if (rst.getInt("total") > 0) {
                return true;
            }
        }

        return false;
    }

    public long getIdVendaNFCe(VendaVO i_oVenda) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;

        stm = Conexao.createStatement();

        long id = -1;

        sql = new StringBuilder();
        sql.append("SELECT nfce.id, nfce.id_venda, v.numeroCupom FROM pdv.vendanfce AS nfce");
        sql.append(" INNER JOIN pdv.venda AS v ON v.id = nfce.id_venda");
        sql.append(" WHERE v.cancelado = FALSE AND v.canceladoemvenda = FALSE");
        sql.append(" AND v.id_loja = " + i_oVenda.idLoja);
        sql.append(" AND (CASE WHEN length(COALESCE(nfce.chavenfce, '')) < 35 THEN '-1' ELSE substring(nfce.chavenfce from 26 for 9) END)::int = " + i_oVenda.numeroNFCe);
        sql.append(" AND (CASE WHEN length(COALESCE(nfce.chavenfce, '')) < 35 THEN '-1' ELSE substring(nfce.chavenfce from 23 for 3) END)::int = " + i_oVenda.ecf);
        sql.append(" AND (CASE WHEN length(COALESCE(nfce.chavenfce, '')) < 35 THEN '-1' ELSE substring(nfce.chavenfce from 3 for 4) END) = '" + (i_oVenda.data.substring(8, 10) + i_oVenda.data.substring(3, 5)) + "'");

        rst = stm.executeQuery(sql.toString());

        if (rst.next()) {
            id = rst.getLong("id");
            i_oVenda.id = rst.getLong("id_venda");
            i_oVenda.numeroCupom = rst.getInt("numeroCupom");
        }

        return id;
    }

    private String getModelo(int i_idModelo) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;

        stm = Conexao.createStatement();

        sql = new StringBuilder();
        sql.append("SELECT descricao FROM pdv.tipomodelo");
        sql.append(" WHERE id = " + i_idModelo);

        rst = stm.executeQuery(sql.toString());

        if (!rst.next()) {
            throw new VRException("Modelo ecf não encontrado!");
        }

        return Util.substring(rst.getString("descricao"), 0, 19);
    }

    private String getNumeroSerie(int i_ecf, int i_idLoja) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;

        stm = Conexao.createStatement();

        sql = new StringBuilder();
        sql.append("SELECT numeroserie FROM pdv.ecf");
        sql.append(" WHERE ecf = " + i_ecf + " AND id_loja = " + i_idLoja);

        rst = stm.executeQuery(sql.toString());

        if (!rst.next()) {
            throw new VRException("ECF não encontrado!");
        }

        return Util.substring(rst.getString("numeroserie"), 0, 20);
        //return Texto.substring(rst.getString("numeroserie"), 0, 20);
    }

    private int getFinalizadoraVenda(String i_finalizadora) throws Exception {

        if (i_finalizadora.equals("01")) {
            return Finalizadora.DINHEIRO.getId();

        } else if (i_finalizadora.equals("02")) {
            return Finalizadora.CHEQUE.getId();

        } else if (i_finalizadora.equals("03") || i_finalizadora.equals("04")) {
            return Finalizadora.TEF.getId();

        } else if (i_finalizadora.equals("05")) {
            return Finalizadora.CREDITO_ROTATIVO.getId();

        } else {
            return Finalizadora.DINHEIRO.getId();
        }
    }

    private int getIdLoja(long i_cnpj) throws Exception {
        Statement stm = null;
        ResultSet rst = null;

        try {
            stm = Conexao.createStatement();

            rst = stm.executeQuery("SELECT l.id FROM loja l INNER JOIN fornecedor f ON f.id = l.id_fornecedor WHERE f.cnpj = " + i_cnpj);

            if (!rst.next()) {
                throw new VRException("Loja não encontrada!");
            }

            return rst.getInt("id");

        } catch (Exception ex) {
            throw ex;
        }
    }

    private int getIdAliquotaICMS(int i_situacaoTributaria, double i_porcentagem) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        
        if (i_situacaoTributaria == 0 && i_porcentagem == 0) {
            i_situacaoTributaria = 40;
        }

        try {
            stm = Conexao.createStatement();

            rst = stm.executeQuery("SELECT id FROM aliquota WHERE situacaotributaria = " + i_situacaoTributaria + " AND porcentagem = " + i_porcentagem);

            if (!rst.next()) {
                throw new VRException("Alíquota não encontrada!");
            }

            return rst.getInt("id");

        } catch (Exception ex) {
            throw ex;
        }
    }

    private void baixarEstoqueOnLine(VendaItemVO i_oVenda) throws Exception {
        AcertoEstoqueVO oEstoque = new AcertoEstoqueVO();
        oEstoque.idProduto = i_oVenda.idProduto;
        oEstoque.idTipoEntradaSaida = TipoEntradaSaida.SAIDA.getId();
        oEstoque.idTipoMovimentacao = TipoMovimentacao.VENDA.getId();
        oEstoque.quantidade = i_oVenda.quantidade;
        oEstoque.baixaReceita = new ParametroDAO().get(105).getInt() == TipoBaixaReceita.MOVIMENTACAO.getId();
        oEstoque.baixaAssociado = true;
        oEstoque.baixaPerda = (new ParametroDAO().get(193).getInt() == TipoBaixaPerda.SAIDA.getId());
        oEstoque.idLoja = Global.idLoja;

        new EstoqueDAO().alterar(oEstoque);
    }

}
