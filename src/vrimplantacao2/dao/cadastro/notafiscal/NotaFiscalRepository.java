package vrimplantacao2.dao.cadastro.notafiscal;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import vrframework.classe.Conexao;
import vrimplantacao.utils.Utils;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.notafiscal.NotaEntrada;
import vrimplantacao2.vo.cadastro.notafiscal.NotaEntradaItem;
import vrimplantacao2.vo.cadastro.notafiscal.NotaSaida;
import vrimplantacao2.vo.cadastro.notafiscal.NotaSaidaItem;
import vrimplantacao2.vo.cadastro.notafiscal.SituacaoNfe;
import vrimplantacao2.vo.cadastro.notafiscal.SituacaoNotaEntrada;
import vrimplantacao2.vo.cadastro.notafiscal.SituacaoNotaSaida;
import vrimplantacao2.vo.importacao.NotaFiscalIMP;
import vrimplantacao2.vo.importacao.NotaFiscalItemIMP;
import vrimplantacao2.vo.importacao.NotaOperacao;

/**
 * Repositório de operações com a nota fiscal.
 * @author Leandro
 */
public class NotaFiscalRepository {
    
    private static final Logger LOG = Logger.getLogger(NotaFiscalRepository.class.getName());
    
    private final NotaFiscalRepositoryProvider provider;
    
    private final int tipoNotaEntrada;
    private final int tipoNotaSaida;
    private MultiMap<String, NotaFiscalAnteriorVO> anteriores;
    private Map<String, Integer> produtosAnteiores;
    private Map<String, Integer> aliquotasPorId;
    private Map<String, Integer> aliquotasPorValor;
    private Map<Integer, Double> custoProduto;
    private Map<Integer, Integer> pisCofins;
    private int idFornecedorLoja = -1;
    
    private Map<String, Integer> fornecedores;
    private Map<String, Integer> clientesEventuais;

    public NotaFiscalRepository(NotaFiscalRepositoryProvider provider) throws Exception {
        this.provider = provider;
        this.tipoNotaEntrada = provider.getTipoNotaEntrada();
        this.tipoNotaSaida = provider.getTipoNotaSaida();
    }

    public void importar(List<NotaFiscalIMP> notas, HashSet<OpcaoNotaFiscal> opt) throws Exception {
        try {
            Conexao.begin();
        
            this.provider.notificar("Notas Fiscais...Carregando anteriores...");
            this.anteriores = provider.getAnteriores();
            this.produtosAnteiores = provider.getProdutosAnteriores();
            this.aliquotasPorId = provider.getAliquotaPorId();
            this.aliquotasPorValor = provider.getAliquotaPorValor();
            this.custoProduto = provider.getCustoProduto();
            this.pisCofins = provider.getPisCofins();
            this.idFornecedorLoja = provider.getIdFornecedorLoja();
            this.provider.notificar("Notas Fiscais...Gravando notas fiscais...", notas.size());

            boolean apagarNotas = opt.contains(OpcaoNotaFiscal.IMP_EXCLUIR_NOTAS_EXISTENTES_IMPORTADAS);
            boolean apagarApenasItens = opt.contains(OpcaoNotaFiscal.IMP_REIMPORTAR_ITENS_DE_NOTAS_IMPORTADAS);
            
            fornecedores = provider.getFornecedores();
            clientesEventuais = provider.getClientesEventuais();
            
            for (NotaFiscalIMP imp: notas) {
                
                //<editor-fold defaultstate="collapsed" desc="Preparando dados para importação">
                //Verifica a existência de anterior
                NotaFiscalAnteriorVO anterior = anteriores.get(
                        String.valueOf(imp.getOperacao().getId()),
                        imp.getId()
                );
                Integer idNotaEntrada = null;
                Integer idNotaSaida = null;
                boolean notaImportada = anterior != null;
                anterior = converterAnterior(imp);
                //</editor-fold>
                
                if (imp.getOperacao() == NotaOperacao.ENTRADA) {
                    
                    //<editor-fold defaultstate="collapsed" desc="Verifica a existencia do fornecedor e da nota de entrada">
                    Integer fornecedor = fornecedores.get(imp.getIdDestinatario());
                    if (fornecedor == null) {
                        throw new Exception("Fornecedor não encontrado " + imp.getIdDestinatario());
                    }
                    idNotaEntrada = provider.getIdNotaEntrada(imp, fornecedor);
                    //</editor-fold>
                    
                    //<editor-fold defaultstate="collapsed" desc="Apagar nota importada">
                    if (idNotaEntrada != null) {
                        if (apagarApenasItens && notaImportada) {
                            provider.eliminarItensNotaEntrada(idNotaEntrada);
                        } else if (apagarNotas && notaImportada) {
                            provider.eliminarNotaEntrada(idNotaEntrada);
                            idNotaEntrada = null;
                        } else {
                            //Rotina não deve mexer em notas inclusas pelo VR.
                            provider.notificar();
                            continue;     
                        }
                    }
                    //</editor-fold>
                    
                    //<editor-fold defaultstate="collapsed" desc="Grava a nota de entrada">
                    NotaEntrada ne = converterNotaEntrada(imp);
                    if (idNotaEntrada == null) {//Nota não existe
                        this.provider.salvarEntrada(ne);
                        idNotaEntrada = ne.getId();
                    } else {
                        ne.setId(idNotaEntrada);
                        this.provider.atualizarEntrada(ne);
                    }
                    //Incluir itens da nota
                    List<NotaEntradaItem> itens = converterNotaEntradaItem(idNotaEntrada, imp.getItens());
                    //Grava os itens
                    for (NotaEntradaItem item: itens) {
                        this.provider.salvarEntradaItem(item);
                    }
                    //</editor-fold>
                    
                } else if (imp.getOperacao() == NotaOperacao.SAIDA) {
                    
                    //<editor-fold defaultstate="collapsed" desc="Verifica a existencia da nota de saída">
                    idNotaSaida = provider.getIdNotaSaida(imp);
                    //</editor-fold>
                    
                    //<editor-fold defaultstate="collapsed" desc="Apagar nota de saída importada">
                    if (idNotaSaida != null) {
                        if (apagarApenasItens && notaImportada) {
                            provider.eliminarItensNotaSaida(idNotaSaida);
                        } else if (apagarNotas && notaImportada) {
                            provider.eliminarNotaSaida(idNotaSaida);
                            idNotaSaida = null;
                        } else {
                            //Rotina não deve mexer em notas inclusas pelo VR.
                            provider.notificar();
                            continue;     
                        }
                    }
                    //</editor-fold>
                    
                    //<editor-fold defaultstate="collapsed" desc="Grava a nota de saída">
                    NotaSaida ns = converterNotaSaida(imp);
                    if (idNotaSaida == null) {//Nota não existe
                        this.provider.salvarSaida(ns);
                        idNotaSaida = ns.getId();
                    } else {
                        ns.setId(idNotaSaida);
                        this.provider.atualizarSaida(ns);
                    }
                    //Incluir itens na nota
                    List<NotaSaidaItem> itens = converterNotaSaidaItem(idNotaSaida, imp.getItens());
                    //Grava os itens
                    for (NotaSaidaItem item: itens) {
                        this.provider.salvarSaidaItem(item);
                    }
                    //</editor-fold>
                }
                
                //<editor-fold defaultstate="collapsed" desc="Incluí ou atualiza o código anterior">                
                anterior.setIdNotaEntrada(idNotaEntrada);
                anterior.setIdNotaSaida(idNotaSaida);
                
                if (notaImportada) {
                    provider.atualizarAnterior(anterior);
                } else {
                    provider.incluirAnterior(anterior);
                    addAnterior(anterior);
                }
                //</editor-fold>
                
                provider.notificar();
                
            }
                        
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
            throw ex;
        } finally {
            //Limpando variaveis
            if (this.anteriores != null) {
                this.anteriores.clear();
                this.anteriores = null;
            }
            System.gc();
        }
    }

    /**
     * Método criado encapsular para a gravação da nota de saída no banco.
     * @param imp Nota a ser convertida.
     * @return Nota convertida.
     * @throws Exception 
     */
    private NotaSaida gravarNotaSaida(NotaFiscalIMP imp, Integer id) throws Exception {
        //Converte a nota
        NotaSaida ns = converterNotaSaida(imp);
        if (id != null) {
            ns.setId(id);
        }
        if (id == null) {
            //Salva o cabeçalho
            this.provider.salvarSaida(ns);
        }
        
        List<NotaSaidaItem> itens = converterNotaSaidaItem(ns.getId(), imp.getItens());
        
        //Grava os itens
        for (NotaSaidaItem item: itens) {
            this.provider.salvarSaidaItem(item);
        }
        
        if (id != null) {
            this.provider.atualizarSaida(ns);
        }
        
        return ns;
    }

    /**
     * Método criado encapsular para a gravação da nota de entrada no banco.
     * @param imp Nota a ser convertida.
     * @return Nota convertida.
     * @throws Exception 
     */
    private NotaEntrada gravarNotaEntrada(NotaFiscalIMP imp, Integer id) throws Exception {
        //Converte a nota
        NotaEntrada ne = converterNotaEntrada(imp);
        if (id != null) {
            ne.setId(id);
        }
        if (id == null) {
            //Salva o cabeçalho
            this.provider.salvarEntrada(ne);
        }
        
        List<NotaEntradaItem> itens = converterNotaEntradaItem(ne.getId(), imp.getItens());
        
        //Grava os itens
        for (NotaEntradaItem item: itens) {
            this.provider.salvarEntradaItem(item);
        }
        
        if (id != null){
            this.provider.atualizarEntrada(ne);
        }
        
        return ne;
    }

    /**
     * Encapsula a operação de inclusão do cadastro anterior.
     * @param anterior 
     */
    private void addAnterior(NotaFiscalAnteriorVO anterior) {
        anteriores.put(anterior, String.valueOf(anterior.getOperacao().getId()), anterior.getId());
    }
    
    private static Timestamp getTimestamp(Date date) {
        if (date != null) {
            return new Timestamp(date.getTime());
        } else {
            return null;
        }
    }
    
    public NotaEntrada converterNotaEntrada(NotaFiscalIMP imp) throws Exception {
        NotaEntrada n = new NotaEntrada();
        
        double valorIcmsBaseCalculo = 0;
        double valorIcms = 0;
        double valorIcmsST = 0;
        double valorIpi = 0;
        double valorFrete = 0;
        double valorDesconto = 0;
        double valorOutraDespesa = 0;
        double valorProduto = 0;
        double valorTotal = 0;
        
        for (NotaFiscalItemIMP item: imp.getItens()) {
            valorIcmsBaseCalculo += item.getIcmsBaseCalculo();
            valorIcms += item.getIcmsValor();
            valorIcmsST += item.getIcmsValorST();
            valorIpi += item.getIpiValor();
            valorFrete += item.getValorFrete();
            valorDesconto += item.getValorDesconto();
            valorOutraDespesa += item.getValorOutras();
            valorProduto += item.getValorTotalProduto();
            valorTotal += item.getValorTotal();
        }
        
        n.setIdLoja(this.provider.getLojaVR());
        n.setNumeroNota(imp.getNumeroNota());
        Integer fornecedor = fornecedores.get(imp.getIdDestinatario());
        if (fornecedor == null) {
            throw new Exception("Fornecedor não encontrado " + imp.getIdDestinatario());
        }
        n.setIdFornecedor(fornecedor);
        n.setDataEntrada(imp.getDataEmissao());
        n.setIdTipoEntrada(this.tipoNotaEntrada);
        n.setDataEmissao(imp.getDataEmissao());
        n.setDataHoraLancamento(getTimestamp(imp.getDataHoraAlteracao()));
        
        n.setValorIpi(valorIpi);
        n.setValorFrete(valorFrete);
        n.setValorDesconto(valorDesconto);
        n.setValorOutraDespesa(valorOutraDespesa);
        n.setValorDespesaAdicional(0);
        n.setValorIcms(valorIcms);
        n.setValorIcmsSubstituicao(valorIcmsST);
        
        n.setValorMercadoria(valorProduto);
        n.setValorTotal(valorTotal);
        n.setIdUsuario(0);
        // private boolean impressao = false;// boolean NOT NULL,
        n.setProdutorRural(imp.isProdutorRural());
        // private boolean aplicaCustoDesconto = true;// boolean NOT NULL,
        // private boolean aplicaIcmsDesconto = true;// boolean NOT NULL,
        // private boolean aplicaCustoEncargo = true;// boolean NOT NULL,
        // private boolean aplicaIcmsEncargo = true;// boolean NOT NULL,
        // private boolean aplicaDespesaAdicional = true;// boolean NOT NULL,
        n.setSituacaoNotaEntrada(SituacaoNotaEntrada.FINALIZADO);
        n.setSerie(imp.getSerie());
        // private double valorGuiaSubstituicao = 0;// numeric(11,2),
        n.setValorBaseCalculo(valorIcmsBaseCalculo);
        // private int aplicaAliquota = -1;// integer NOT NULL,
        // private double valorBaseSubstituicao = 0;// numeric(11,2) NOT NULL,
        // private double valorFunrural = 0;// numeric(11,2) NOT NULL,
        // private double valorDescontoBoleto = 0;// numeric(11,2) NOT NULL,
        n.setChaveNfe(imp.getChaveNfe());
        // private boolean conferido = false;// boolean NOT NULL,
        n.setTipoFreteNotaFiscal(imp.getTipoFreteNotaFiscal());
        // private String observacao = "";// text NOT NULL DEFAULT ''::character varying,
        // private long idNotaSaida = -1;//id_notasaida bigint,
        n.setTipoNota(imp.getTipoNota());
        n.setModelo(imp.getModelo());
        // private boolean liberadoPedido = false;// boolean NOT NULL DEFAULT false,
        n.setDataHoraFinalizacao(getTimestamp(imp.getDataHoraAlteracao()));
        // private boolean importadoXml = false;// boolean NOT NULL DEFAULT false,
        // private boolean aplicaIcmsIpi = false;// boolean NOT NULL DEFAULT false,
        // private int liberadoBonificacao = 1;// integer NOT NULL DEFAULT '-1'::integer,
        n.setInformacaoComplementar(imp.getInformacaoComplementar());
        n.setDataHoraAlteracao(new Timestamp(imp.getDataHoraAlteracao().getTime()));
        // private int liberadoVencimento = -1;// integer NOT NULL DEFAULT '-1'::integer,
        // private String justificativaDivergencia;// character varying(50),
        // private boolean consistido = false;// boolean DEFAULT false,
        // private int quantidadePaletes = 0;// integer NOT NULL DEFAULT 0,
        // private long idNotaDespesa = -1;//id_notadespesa bigint,
        // private double valorDespesaFrete = 0;// numeric(11,2),
        // private boolean liberadoValidadeProduto = false;// boolean NOT NULL DEFAULT false,
        // private double valorFcp = 0;// numeric(11,2),
        // private double valorFcpST = 0;// numeric(11,2),
        // private double valorIcmsDesonerado = 0;// numeric(11,2),
        // private boolean liberadoDivergenciaColetor = false;// boolean DEFAULT false,
        // private double valorSuframa = -1;// numeric(11,2),
        
        return n;
    }
    
    public NotaSaida converterNotaSaida(NotaFiscalIMP imp) throws Exception {
        NotaSaida n = new NotaSaida();        
               
        double valorIcmsBaseCalculo = 0;
        double valorIpi = 0;
        double valorProduto = 0;
        double valorIcms = 0;
        double valorIcmsST = 0;
        double valorFrete = 0;
        double valorDesconto = 0;
        double valorOutraDespesa = 0;
        double valorTotal = 0;
        
        for (NotaFiscalItemIMP item: imp.getItens()) {
            valorIcmsBaseCalculo += item.getIcmsBaseCalculo();
            valorIpi += item.getIpiValor();
            valorProduto += item.getValorTotalProduto();
            valorIcms += item.getIcmsValor();
            valorIcmsST += item.getIcmsValorST();
            valorFrete += item.getValorFrete();
            valorDesconto += item.getValorDesconto();
            valorOutraDespesa += item.getValorOutras();
            valorTotal += item.getValorTotal();
        }
        
        n.setIdLoja(provider.getLojaVR());
        n.setNumeroNota(imp.getNumeroNota());
        n.setTipoNota(imp.getTipoNota());
        switch (imp.getTipoDestinatario()) {
            case FORNECEDOR:
                Integer fornecedor = fornecedores.get(imp.getIdDestinatario());
                if (fornecedor == null) {
                    throw new Exception("Fornecedor não encontrado " + imp.getIdDestinatario());
                }
                n.setIdFornecedor(fornecedor);
                break;
            case CLIENTE_EVENTUAL:
                Integer clienteeventual = clientesEventuais.get((String) imp.getIdDestinatario());
                if (clienteeventual == null) {
                    throw new Exception("Cliente eventual não encontrado " + imp.getIdDestinatario());
                }
                n.setIdClienteEventual(clienteeventual);
                break;
        }                
        n.setIdTipoSaida(this.tipoNotaSaida);
        n.setDataHoraEmissao(getTimestamp(imp.getDataEmissao()));
        n.setDataSaida(imp.getDataEntradaSaida());
        
        n.setValorIpi(valorIpi);
        n.setValorFrete(valorFrete);
        n.setValorOutrasDespesas(valorOutraDespesa);
        n.setValorProduto(valorProduto); 
        n.setValorIcms(valorIcms);
        n.setValorSeguro(imp.getValorSeguro());
        n.setValorDesconto(valorDesconto);
        
        n.setValorTotal(valorTotal);
        n.setValorBaseCalculo(valorIcmsBaseCalculo);
        //private double valorBaseSubstituicao = 0;// numeric(11,2) NOT NULL,
        n.setValorIcmsSubstituicao(valorIcmsST);
        //private boolean impressao = true;//boolean NOT NULL,
        n.setSituacaoNotaSaida(SituacaoNotaSaida.FINALIZADO);
        
        //TODO: INCLUIR IMPORTAÇÃO DE TRANSPORTADORES DEPOIS        
        //private int idMotoristaTransportador = -1;// id_motoristatransportador integer,
        n.setIdFornecedorTransportador(idFornecedorLoja);
        //private int idClienteEventualTransportador = -1;//id_clienteeventualtransportador integer,
        //private String placa = "";// character varying(7) NOT NULL,
        
        //private int idTipoDevolucao = -1;//id_tipodevolucao integer,
        n.setInformacaoComplementar(imp.getInformacaoComplementar());
        //private String senha = "";// character varying(8) NOT NULL,
        //private int tipoLocalBaixa = -1;// integer NOT NULL,
        //private double valorBaseIpi = 0;// numeric(11,2) NOT NULL,
        n.setVolume(imp.getVolume());
        n.setPesoLiquido(imp.getPesoLiquido());
        n.setSituacaoNfe(imp.getSituacaoNfe());
        n.setChaveNfe(imp.getChaveNfe());
        //private String reciboNfe;// character varying(15) DEFAULT ''::character varying,
        //private String motivoRejeicaoNfe;// character varying(200) DEFAULT ''::character varying,
        //private String protocoloRecebimentoNfe;// character varying(15) DEFAULT ''::character varying,
        //private Timestamp dataHoraRecebimentoNfe;// timestamp without time zone,
        //private String justificativaCancelamentoNfe;// character varying(200) DEFAULT ''::character varying,
        //private String protocoloCancelamentoNfe;// character varying(15) DEFAULT ''::character varying,
        if (n.getSituacaoNfe() == SituacaoNfe.CANCELADA) {
            n.setDataHoraCancelamentoNfe(getTimestamp(imp.getDataEmissao()));
        }
        n.setTipoFreteNotaFiscal(imp.getTipoFreteNotaFiscal());        
        //private long idNotaSaidaComplemento = -1;// bigint,
        //private boolean emailNfe = false;// boolean NOT NULL,
        //private boolean contingenciaNfe = false;// boolean NOT NULL DEFAULT false,
        //private long idNotaEntrada = -1;// bigint,
        //private boolean aplicaIcmsDesconto = true;// boolean NOT NULL DEFAULT false,
        //private boolean aplicaIcmsEncargo = true;// boolean NOT NULL DEFAULT false,
        n.setPesoBruto(imp.getPesoBruto());
        n.setDataHoraAlteracao(getTimestamp(imp.getDataHoraAlteracao()));
        //private int idLocalEntrega = 0;// integer,
        //private double valorIcmsUsoConsumo = 0;// numeric(11,2),
        //private boolean aplicaIcmsIpi = false;// boolean NOT NULL DEFAULT false,
        //private int idTipoViaTransporteInteracional = -1;// integer,
        //private double valorafrmm = 0;// numeric(13,2),
        //private int idTipoFormaImportacao = -1;// integer,
        //private boolean aplicaIcmsStIpi = true;// boolean DEFAULT true,
        //private String especie;// character varying(60),
        //private String marca;// character varying(60),
        //private String numeracao;// character varying(60),
        //private boolean aplicaPisCofinsDesconto = false;// boolean,
        //private boolean aplicaPisCofinsEncargo = false;// boolean,
        n.setSerie(imp.getSerie());
        //private int idEscritaSaldo = -1;// integer,
        //private double valorFcp = 0;// numeric(11,2),
        //private double valorFcpSt = 0;// numeric(11,2),
        //private double valorIcmsDesonerado = 0;// numeric(11,2),// numeric(11,2),// numeric(11,2),// numeric(11,2),
        
        return n;
    }

    private NotaFiscalAnteriorVO converterAnterior(NotaFiscalIMP imp) {
        NotaFiscalAnteriorVO vo = new NotaFiscalAnteriorVO();
        
        vo.setSistema(provider.getSistema());
        vo.setLoja(provider.getLojaOrigem());
        vo.setOperacao(imp.getOperacao());
        vo.setId(imp.getId());
        vo.setTipoNota(imp.getTipoNota());
        vo.setModelo(imp.getModelo());
        vo.setSerie(imp.getSerie());
        vo.setNumeroNota(imp.getNumeroNota());
        vo.setDataEmissao(imp.getDataEmissao());
        vo.setValorProduto(imp.getValorProduto());
        vo.setValorTotal(imp.getValorTotal());

        vo.setTipoDestinatario(imp.getTipoDestinatario());
        vo.setIdDestinatario(imp.getIdDestinatario());
        
        return vo;
    }

    private List<NotaEntradaItem> converterNotaEntradaItem(long idNotaEntrada, ArrayList<NotaFiscalItemIMP> itens) throws Exception {
        LinkedHashMap<String, NotaFiscalItemIMP> unico = new LinkedHashMap<>();
        
        for (NotaFiscalItemIMP item: itens) {
            unico.put(item.getId(), item);
        }
        itens.clear();
        
        Map<String, NotaEntradaItem> result = new LinkedHashMap<>();
        for (NotaFiscalItemIMP imp: unico.values()) {
            Integer idProduto = this.produtosAnteiores.get(imp.getIdProduto());
            if (idProduto == null) {
                throw new Exception("Produto '" + imp.getIdProduto() + "' - '" + imp.getDescricao() + "' não encontrado ");
            }
            String cfop = String.format("%,d", Utils.stringToInt(imp.getCfop()));
            
            NotaEntradaItem ni = result.get(String.format("%d-%d-%s", idNotaEntrada, idProduto, cfop));
            
            if (ni == null) {
                ni = new NotaEntradaItem();
            }
            
            //private int id;// integer NOT NULL DEFAULT nextval('notaentradaitem_id_seq'::regclass),
            ni.setIdNotaEntrada(idNotaEntrada);
            ni.setIdProduto(idProduto);
            ni.setQuantidade(ni.getQuantidade() + imp.getQuantidade());
            ni.setQtdEmbalagem(imp.getQuantidadeEmbalagem());
            ni.setValorTotal(ni.getValorTotal() + imp.getValorTotalProduto());
            
            Integer idAliquota = obterAliquotaICMS(imp);
            ni.setIdAliquota(idAliquota);            
            //ni.setIdAliquotaPautaFiscal(idAliquota);  
            ni.setCustoComImposto(obterCustoComImposto(idProduto));            
            ni.setCfop(cfop);
            Integer pisCof = obterTipoPisCofins(imp);
            if (pisCof != null) {
                ni.setIdTipoPisCofins(pisCof);
            } else {
                ni.setIdTipoPisCofins(13);//ISENTO
            }
            ni.setCfopNota(ni.getCfop());
            ni.setValorIpi(ni.getValorIpi() + imp.getIpiValor());
            
            ni.setValorIsento(ni.getValorIsento() + imp.getValorIsento());
            ni.setValorOutras(ni.getValorOutras() + imp.getValorOutras());
            ni.setSituacaoTributaria(imp.getIcmsCst()); 
            ni.setValorFrete(ni.getValorFrete() + imp.getValorFrete());
            //private double valorOutrasDespesas = 0;// numeric(11,2) NOT NULL DEFAULT 0,
            ni.setValorDesconto(ni.getValorDesconto() + imp.getValorDesconto());
            
            ni.setIdAliquotaCreditoForaEstado(idAliquota);            
            ni.setIdTipoEntrada(210);//TODO: Incluir um campo para especificar o ID VR.
            

            ni.setValorBaseCalculo(ni.getValorBaseCalculo() + imp.getIcmsBaseCalculo());
            ni.setValorIcms(ni.getValorIcms() + imp.getIcmsValor());
            ni.setValorIcmsSubstituicao(ni.getValorIcmsSubstituicao() + imp.getIcmsValorST());
            //private double custoComImpostoAnterior = 0;// numeric(13,4) NOT NULL,
            //private double valorBonificacao = 0;// numeric(11,2) NOT NULL,
            //private double valorVerba = 0;// numeric(11,2) NOT NULL,
            //private double quantidadeDevolvida = 0;// numeric(12,3) NOT NULL,
            ni.setValorPisCofins(ni.getValorPisCofins() + imp.getPisCofinsValor());//private double valorPisCofins = 0;// numeric(11,2) NOT NULL,
            //private boolean contabilizaValor = true;// boolean NOT NULL,
            ni.setValorBaseSubstituicao(ni.getValorBaseSubstituicao() + imp.getIcmsBaseCalculoST());
            ni.setValorEmbalagem(ni.getValorEmbalagem() + (imp.getValorUnidade() * imp.getQuantidadeEmbalagem()));
            //private double valorIcmsSubstituicaoXml = 0;// numeric(11,2) NOT NULL,
            //private int idAliquotaPautaFiscal = -1;//id_aliquotapautafiscal = -1;// integer,
            
            //private double valorOutrasSubstituicao = 0;// numeric(11,2) NOT NULL DEFAULT 0,
            //private double quantidadeBonificacao = 0;// numeric(11,2),
            //private double valorSubstituicaoEstadual = 0;// numeric(11,2) DEFAULT 0,
            //private String descricaoXml;// character varying(120),
            //private double valorDespesaFrete = 0;// numeric(11,4),
            //private double valorBaseFcp = 0;// numeric(11,2),
            //private double valorFcp = 0;// numeric(11,2),
            //private double valorBaseFcpSt = 0;// numeric(11,2),
            //private double valorFcpSt = 0;// numeric(11,2),
            //private double valorIcmsDesonerado = 0;// numeric(11,2),
            //private int idMotivoDesoneracao = -1;// integer,
            //private double valorBaseCalculoIcmsDesonerado = 0;// numeric(11,2),
            //private double valorIcmsDiferido = 0;// numeric(11,2)

            result.put(String.format("%d-%d-%s", idNotaEntrada, idProduto, cfop),ni);
        }
        
        return new ArrayList<>(result.values());
    }

    private List<NotaSaidaItem> converterNotaSaidaItem(long idNotaSaida, ArrayList<NotaFiscalItemIMP> itens) throws Exception {
        LinkedHashMap<String, NotaFiscalItemIMP> unico = new LinkedHashMap<>();
        
        //Removendo duplicações
        for (NotaFiscalItemIMP item: itens) {
            unico.put(item.getId(), item);
        }
        itens.clear();
        
        Map<String, NotaSaidaItem> result = new LinkedHashMap<>();
        for (NotaFiscalItemIMP imp: unico.values()) {
            
            Integer idProduto = this.produtosAnteiores.get(imp.getIdProduto());
            if (idProduto == null) {
                throw new Exception("Produto '" + imp.getIdProduto() + "' - '" + imp.getDescricao() + "' não encontrado ");
            }
            String cfop = String.format("%,d", Utils.stringToInt(imp.getCfop()));
            
            NotaSaidaItem ni = result.get(String.format("%d-%d-%s", idNotaSaida, idProduto, cfop));
            
            if (ni == null) {
                ni = new NotaSaidaItem();
            }
            
            //private int id;// integer NOT NULL DEFAULT nextval('notasaidaitem_id_seq'::regclass),
            ni.setIdNotaSaida(idNotaSaida);
            ni.setIdProduto(idProduto);
            ni.setQuantidade(ni.getQuantidade() + imp.getQuantidade());
            ni.setQtdEmbalagem(imp.getQuantidadeEmbalagem());
            ni.setValorTotal(ni.getValorTotal() + imp.getValorTotalProduto());          
            
            ni.setIdAliquota(obterAliquotaICMS(imp));
            
            ni.setCfop(cfop);//private String cfop;// character varying(5),
            ni.setSituacaoTributaria(imp.getIcmsCst()); //private int situacaoTributaria = 0;// integer NOT NULL DEFAULT 0,
            ni.setNumeroAdicao(imp.getNumeroItem());//private int numeroAdicao = 0;// integer NOT NULL DEFAULT 0,
            ni.setValorIpi(ni.getValorIpi() + imp.getIpiValor());
                      
            ni.setValorDesconto(ni.getValorDesconto() + imp.getValorDesconto());//private double valorDesconto = 0;// numeric(11,2) NOT NULL DEFAULT 0,
            ni.setValorIsento(ni.getValorIsento() + imp.getValorIsento());//private double valorIsento = 0;// numeric(11,2) NOT NULL DEFAULT 0,
            ni.setValorOutras(ni.getValorOutras() + imp.getValorOutras());//private double valorOutras = 0;// numeric(11,2) NOT NULL DEFAULT 0,              

            ni.setValorBaseCalculo(ni.getValorBaseCalculo() + imp.getIcmsBaseCalculo());
            ni.setValorIcms(ni.getValorIcms() + imp.getIcmsValor());
            ni.setValorBaseSubstituicao(ni.getValorBaseSubstituicao() + imp.getIcmsBaseCalculoST());
            ni.setValorIcmsSubstituicao(ni.getValorIcmsSubstituicao() + imp.getIcmsValorST());
            ni.setValorPisCofins(ni.getValorPisCofins() + imp.getPisCofinsValor());
            
/*            
            //TODO: Incluir a vinculação da pauta fiscal //private int tipoIva = 0;// integer NOT NULL DEFAULT 0,
            
            //TODO: Incluir a vinculação da pauta fiscal //private int idAliquotaPautaFiscal = -1;//id_aliquotapautafiscal;// integer,
            
            //private double valorIcmsDispensado = 0;// numeric(12,3) NOT NULL DEFAULT 0,
            //private int idAliquotaDispensado = -1;//id_aliquotadispensado;// integer,
            //private int tipoNaturezaReceita = -1;// integer,
            //private Timestamp dataDesembaraco;// timestamp without time zone,
            //private int idEstadoDesembaraco = -1;//id_estadodesembaraco;// integer,
            //private String localDesembaraco = "";// character varying(50) NOT NULL DEFAULT ''::character varying,
            
            //TODO: Incluir algo para incluir uma saida padrão. //private int idTipoSaida = -1;//id_tiposaida;// integer,
            
            //private int idAliquotaInterestadual = -1;//id_aliquotainterestadual;// integer,
            //private int idAliquotaDestino = -1;//id_aliquotadestino;// integer,
            //private int idTipoOrigemApuracao = -1;//id_tipoorigemapuracao;// integer,
            //private double valorBaseFcp = 0;// numeric(11,2),
            //private double valorFcp = 0;// numeric(11,2),
            //private double valorBaseFcpSt = 0;// numeric(11,2),
            //private double valorFcpSt = 0;// numeric(11,2),
            //private double valorIcmsDesonerado = 0;// numeric(11,2),
            //private int idMotivoDesoneracao = -1;// integer,
            //private double valorBaseCalculoIcmsDesonerado = 0;// numeric(11,2),
            //private int idEscritaFundamento = -1;//id_escritafundamento;// integer,
            //private int idEscritaCodigoAjuste = -1;//id_escritacodigoajuste;// integer,
            //private double valorIcmsDiferido = 0;// numeric(11,2)
*/
            result.put(String.format("%d-%d-%s", idNotaSaida, idProduto, cfop),ni);
        }
        
        return new ArrayList<>(result.values());
    }
    
    private int obterAliquotaICMS(NotaFiscalItemIMP imp) {
        
        if (imp.getIdIcms() != null) {
            Integer id = aliquotasPorId.get(imp.getIdIcms());
            if (id != null) {
                return id;
            }
        }
        
        int cst = imp.getIcmsCst();
        double aliq = imp.getIcmsAliquota();
        double red = imp.getIcmsReduzido();
        
        if (
                cst == 40 || 
                cst == 41 ||
                cst == 50 ||
                cst == 51 ||
                cst == 60
        ) {
            aliq = 0;
            red = 0;
        }
        
        Integer id = aliquotasPorValor.get(String.format("%d-%.2f-%.2f", cst, aliq, red));
        if (id != null) {
            return id;
        }            
        
        return 6;//Isento;
    }

    private double obterCustoComImposto(int idProduto) {        
        return custoProduto.get(idProduto);        
    }

    private Integer obterTipoPisCofins(NotaFiscalItemIMP imp) {
        Integer idPisCofins = pisCofins.get(imp.getPisCofinsCst());
        if (idPisCofins != null) {
            return idPisCofins;
        }
        return null;
    }
}