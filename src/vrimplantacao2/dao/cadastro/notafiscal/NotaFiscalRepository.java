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
            this.provider.notificar("Notas Fiscais...Gravando notas fiscais...", notas.size());

            boolean apagarNotasExistentes = opt.contains(OpcaoNotaFiscal.IMP_EXCLUIR_NOTAS_EXISTENTES);

            for (NotaFiscalIMP imp: notas) {
                //Verifica a existência de anterior
                NotaFiscalAnteriorVO anterior = anteriores.get(
                        String.valueOf(imp.getOperacao().getId()),
                        imp.getId()
                );
                //Se não existir
                if (anterior == null) {
                    anterior = converterAnterior(imp);
                    //Verifica a existencia da nota no banco e cria o anterior.
                    if (imp.getOperacao() == NotaOperacao.ENTRADA) {
                        Integer idNotaEntrada = provider.getIdNotaEntrada(imp);
                        //Se a nota existir
                        if (idNotaEntrada != null) {
                            //Se a ordem for para excluir a nota se ela existir.
                            if (apagarNotasExistentes) {
                                //Elimina ela do banco

                                /**
                                 * TODO: Incluir código que ao ocorrer um erro, ele vincula a nota 
                                 * ao anterior e vai para o próximo
                                 */                            
                                provider.eliminarNotaEntrada(idNotaEntrada);
                            } else {
                                //Grava o anterior no banco e segue para a próxima nota
                                anterior.setIdNotaEntrada(idNotaEntrada);
                                provider.incluirAnterior(anterior);
                                addAnterior(anterior);
                                provider.notificar();
                                continue;                            
                            }
                        }

                        NotaEntrada ne = gravarNotaEntrada(imp);
                        //Vincula o anterior
                        anterior.setIdNotaEntrada(ne.getId());  

                    } else {
                        Integer idNotaSaida = provider.getIdNotaSaida(imp);
                        //Se a nota existir
                        if (idNotaSaida != null) {
                            //Se a ordem for para excluir a nota se ela existir.
                            if (apagarNotasExistentes) {
                                //Elimina ela do banco
                                provider.eliminarNotaSaida(idNotaSaida);
                            } else {
                                //Grava o anterior no banco e segue para a próxima nota
                                anterior.setIdNotaSaida(idNotaSaida);
                                provider.incluirAnterior(anterior);
                                addAnterior(anterior);
                                provider.notificar();
                                continue;                            
                            }
                        }

                        NotaSaida ns = gravarNotaSaida(imp);
                        //Vincula o anterior
                        anterior.setIdNotaSaida(ns.getId()); 

                    }
                    //Incluindo o código anterior no banco.
                    this.provider.incluirAnterior(anterior);
                    addAnterior(anterior);                
                } else {
                    //Se a ordem for apagar as notas, elimina as notas se existirem
                    //senão vai para a próxima nota.
                    if (anterior.getOperacao() == NotaOperacao.ENTRADA && anterior.getIdNotaEntrada() != null) {
                        if (apagarNotasExistentes) {
                            provider.eliminarNotaEntrada(anterior.getIdNotaEntrada());
                        } else {
                            provider.notificar();
                            continue;      
                        }
                    } else if (anterior.getOperacao() == NotaOperacao.SAIDA && anterior.getIdNotaSaida() != null) {
                        if (apagarNotasExistentes) {
                            provider.eliminarNotaSaida(anterior.getIdNotaSaida());
                        } else {
                            provider.notificar();
                            continue;
                        }
                    }
                    anterior.setIdNotaEntrada(null);
                    anterior.setIdNotaSaida(null);

                    //Conversão e inclusão da nota
                    if (imp.getOperacao() == NotaOperacao.ENTRADA) {
                        NotaEntrada ne = gravarNotaEntrada(imp);
                        anterior.setIdNotaEntrada(ne.getId());  
                    } else {
                        NotaSaida ns = gravarNotaSaida(imp);
                        anterior.setIdNotaSaida(ns.getId());  
                    }
                    //Incluindo o código anterior no banco.
                    this.provider.atualizarAnterior(anterior);
                    addAnterior(anterior);
                }            
                provider.notificar();
            }
            
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
            ex.printStackTrace();
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
    private NotaSaida gravarNotaSaida(NotaFiscalIMP imp) throws Exception {
        //Converte a nota
        NotaSaida ns = converterNotaSaida(imp);
        //Salva o cabeçalho
        this.provider.salvarSaida(ns);        
        
        List<NotaSaidaItem> itens = converterNotaSaidaItem(ns.getId(), imp.getItens());
        
        //Grava os itens
        for (NotaSaidaItem item: itens) {
            this.provider.salvarSaidaItem(item);
        }
        
        return ns;
    }

    /**
     * Método criado encapsular para a gravação da nota de entrada no banco.
     * @param imp Nota a ser convertida.
     * @return Nota convertida.
     * @throws Exception 
     */
    private NotaEntrada gravarNotaEntrada(NotaFiscalIMP imp) throws Exception {
        //Converte a nota
        NotaEntrada ne = converterNotaEntrada(imp);
        //Salva o cabeçalho
        this.provider.salvarEntrada(ne);        
        
        List<NotaEntradaItem> itens = converterNotaEntradaItem(ne.getId(), imp.getItens());
        
        //Grava os itens
        for (NotaEntradaItem item: itens) {
            this.provider.salvarEntradaItem(item);
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
        
        n.setIdLoja(this.provider.getLojaVR());
        n.setNumeroNota(imp.getNumeroNota());
        Integer fornecedor = provider.getFornecedorById(imp.getIdDestinatario());
        if (fornecedor == null) {
            throw new Exception("Fornecedor não encontrado " + imp.getIdDestinatario());
        }
        n.setIdFornecedor(fornecedor);
        n.setDataEntrada(imp.getDataEmissao());
        n.setIdTipoEntrada(this.tipoNotaEntrada);
        n.setDataEmissao(imp.getDataEmissao());
        n.setDataHoraLancamento(getTimestamp(imp.getDataHoraAlteracao()));
        n.setValorIpi(imp.getValorIpi());
        n.setValorFrete(imp.getValorFrete());
        n.setValorDesconto(imp.getValorDesconto());
        n.setValorOutraDespesa(imp.getValorOutrasDespesas());
        n.setValorDespesaAdicional(0);
        n.setValorMercadoria(imp.getValorProduto());
        n.setValorTotal(imp.getValorTotal());
        n.setValorIcms(imp.getValorIcms());
        n.setValorIcmsSubstituicao(imp.getValorIcmsSubstituicao());
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
        // private double valorBaseCalculo = 0;// numeric(11,2),
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
        
        n.setIdLoja(provider.getLojaVR());
        n.setNumeroNota(imp.getNumeroNota());
        n.setTipoNota(imp.getTipoNota());
        switch (imp.getTipoDestinatario()) {
            case FORNECEDOR:
                Integer fornecedor = provider.getFornecedorById(imp.getIdDestinatario());
                if (fornecedor == null) {
                    throw new Exception("Fornecedor não encontrado " + imp.getIdDestinatario());
                }
                n.setIdFornecedor(fornecedor);
                break;
            case CLIENTE_EVENTUAL:
                Integer clienteeventual = provider.getClienteEventual(imp.getIdDestinatario());
                if (clienteeventual == null) {
                    throw new Exception("Cliente eventual não encontrado " + imp.getIdDestinatario());
                }
                n.setIdClienteEventual(clienteeventual);
                break;
        }                
        n.setIdTipoSaida(this.tipoNotaSaida);
        n.setDataHoraEmissao(getTimestamp(imp.getDataEmissao()));
        n.setDataSaida(imp.getDataEntradaSaida());
        n.setValorIpi(imp.getValorIpi());
        n.setValorFrete(imp.getValorFrete());
        n.setValorOutrasDespesas(imp.getValorOutrasDespesas());
        n.setValorProduto(imp.getValorProduto());        
        n.setValorTotal(imp.getValorTotal());
        //private double valorBaseCalculo = 0;// numeric(11,2) NOT NULL,
        n.setValorIcms(imp.getValorIcms());
        //private double valorBaseSubstituicao = 0;// numeric(11,2) NOT NULL,
        n.setValorIcmsSubstituicao(imp.getValorIcmsSubstituicao());
        n.setValorSeguro(imp.getValorSeguro());
        n.setValorDesconto(imp.getValorDesconto());
        //private boolean impressao = true;//boolean NOT NULL,
        //private SituacaoNotaSaida situacaoNotaSaida = SituacaoNotaSaida.FINALIZADO;// id_situacaonotasaida integer NOT NULL,
        
        //TODO: INCLUIR IMPORTAÇÃO DE TRANSPORTADORES DEPOIS        
        //private int idMotoristaTransportador = -1;// id_motoristatransportador integer,
        //private int idFornecedorTransportador = -1;//id_fornecedortransportador integer,
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
        
        List<NotaEntradaItem> result = new ArrayList<>();
        for (NotaFiscalItemIMP imp: unico.values()) {
            NotaEntradaItem ni = new NotaEntradaItem();
            
            Integer idProduto = this.produtosAnteiores.get(imp.getIdProduto());
            if (idProduto == null) {
                throw new Exception("Produto '" + imp.getIdProduto() + "' - '" + imp.getDescricao() + "' não encontrado ");
            }     
            
            //private int id;// integer NOT NULL DEFAULT nextval('notaentradaitem_id_seq'::regclass),
            ni.setIdNotaEntrada(idNotaEntrada);
            ni.setIdProduto(idProduto);
            ni.setQuantidade(imp.getQuantidade());
            ni.setQtdEmbalagem((int) Math.round(imp.getQuantidadeEmbalagem()));
            ni.setValor(imp.getValorTotal());

            ni.setValorIpi(imp.getIpiValor());
            
            Integer idAliquota = obterAliquotaICMS(imp);
            ni.setIdAliquota(idAliquota);
            
            //ni.setIdAliquotaPautaFiscal(idAliquota);                        
            
            ni.setCustoComImposto(obterCustoComImposto(idProduto));            
            
            ni.setValorBaseCalculo(imp.getIcmsBaseCalculo());
            ni.setValorIcms(imp.getIcmsValor());
            ni.setValorIcmsSubstituicao(imp.getIcmsValorST());
            //private double custoComImpostoAnterior = 0;// numeric(13,4) NOT NULL,
            //private double valorBonificacao = 0;// numeric(11,2) NOT NULL,
            //private double valorVerba = 0;// numeric(11,2) NOT NULL,
            //private double quantidadeDevolvida = 0;// numeric(12,3) NOT NULL,
            ni.setValorPisCofins(imp.getPisCofinsValor());//private double valorPisCofins = 0;// numeric(11,2) NOT NULL,
            //private boolean contabilizaValor = true;// boolean NOT NULL,
            ni.setValorBaseSubstituicao(imp.getIcmsBaseCalculoST());
            ni.setValorEmbalagem(imp.getValorUnidade());
            ni.setCfop(String.format("%04d", Utils.stringToInt(imp.getCfop())));
            //private double valorIcmsSubstituicaoXml = 0;// numeric(11,2) NOT NULL,
            ni.setValorIsento(imp.getValorIsento());
            ni.setValorOutras(imp.getValorOutras());
            ni.setSituacaoTributaria(imp.getIcmsCst()); 
            ni.setValorFrete(imp.getValorFrete());
            //private double valorOutrasDespesas = 0;// numeric(11,2) NOT NULL DEFAULT 0,
            ni.setValorDesconto(imp.getValorDesconto());
            
            Integer pisCofins = obterTipoPisCofins(imp);
            if (pisCofins != null) {
                ni.setIdTipoPisCofins(pisCofins);
            } else {
                ni.setIdTipoPisCofins(13);//ISENTO
            }
            
            ni.setIdAliquotaCreditoForaEstado(idAliquota);            
            //private int idAliquotaPautaFiscal = -1;//id_aliquotapautafiscal = -1;// integer,
            
            ni.setIdTipoEntrada(210);//TODO: Incluir um campo para especificar o ID VR.
            //private double valorOutrasSubstituicao = 0;// numeric(11,2) NOT NULL DEFAULT 0,
            //private double quantidadeBonificacao = 0;// numeric(11,2),
            //private double valorSubstituicaoEstadual = 0;// numeric(11,2) DEFAULT 0,
            //private String descricaoXml;// character varying(120),
            //private double valorDespesaFrete = 0;// numeric(11,4),
            ni.setCfopNota(ni.getCfop());
            //private double valorBaseFcp = 0;// numeric(11,2),
            //private double valorFcp = 0;// numeric(11,2),
            //private double valorBaseFcpSt = 0;// numeric(11,2),
            //private double valorFcpSt = 0;// numeric(11,2),
            //private double valorIcmsDesonerado = 0;// numeric(11,2),
            //private int idMotivoDesoneracao = -1;// integer,
            //private double valorBaseCalculoIcmsDesonerado = 0;// numeric(11,2),
            //private double valorIcmsDiferido = 0;// numeric(11,2)

            result.add(ni);
        }
        
        return result;
    }

    private List<NotaSaidaItem> converterNotaSaidaItem(long idNotaSaida, ArrayList<NotaFiscalItemIMP> itens) throws Exception {
        LinkedHashMap<String, NotaFiscalItemIMP> unico = new LinkedHashMap<>();
        
        //Removendo duplicações
        for (NotaFiscalItemIMP item: itens) {
            unico.put(item.getId(), item);
        }
        itens.clear();
        
        List<NotaSaidaItem> result = new ArrayList<>();
        for (NotaFiscalItemIMP imp: unico.values()) {
            NotaSaidaItem ni = new NotaSaidaItem();
            
            Integer idProduto = this.produtosAnteiores.get(imp.getIdProduto());
            if (idProduto == null) {
                throw new Exception("Produto '" + imp.getIdProduto() + "' - '" + imp.getDescricao() + "' não encontrado ");
            }     
            
            //private int id;// integer NOT NULL DEFAULT nextval('notasaidaitem_id_seq'::regclass),
            ni.setIdNotaSaida(idNotaSaida);
            ni.setIdProduto(idProduto);
            ni.setQuantidade(imp.getQuantidade());
            ni.setQtdEmbalagem((int) Math.round(imp.getQuantidadeEmbalagem()));
            ni.setValor(imp.getValorTotal());
            ni.setValorIpi(imp.getIpiValor());
            
            ni.setIdAliquota(obterAliquotaICMS(imp));
            
            ni.setValorBaseCalculo(imp.getIcmsBaseCalculo());
            ni.setValorIcms(imp.getIcmsValor());
            ni.setValorBaseSubstituicao(imp.getIcmsBaseCalculoST());
            ni.setValorIcmsSubstituicao(imp.getIcmsValorST());
            ni.setValorPisCofins(imp.getPisCofinsValor());
            ni.setValorIpi(imp.getIpiValor());//private double valorBaseIpi = 0;// numeric(11,2) NOT NULL,
            ni.setCfop(String.format("%04d", Utils.stringToInt(imp.getCfop())));//private String cfop;// character varying(5),
            
            //TODO: Incluir a vinculação da pauta fiscal //private int tipoIva = 0;// integer NOT NULL DEFAULT 0,
            
            //TODO: Incluir a vinculação da pauta fiscal //private int idAliquotaPautaFiscal = -1;//id_aliquotapautafiscal;// integer,
            
            ni.setValorDesconto(imp.getValorDesconto());//private double valorDesconto = 0;// numeric(11,2) NOT NULL DEFAULT 0,
            ni.setValorIsento(imp.getValorIsento());//private double valorIsento = 0;// numeric(11,2) NOT NULL DEFAULT 0,
            ni.setValorOutras(imp.getValorOutras());//private double valorOutras = 0;// numeric(11,2) NOT NULL DEFAULT 0,
            ni.setSituacaoTributaria(imp.getIcmsCst()); //private int situacaoTributaria = 0;// integer NOT NULL DEFAULT 0,
            //private double valorIcmsDispensado = 0;// numeric(12,3) NOT NULL DEFAULT 0,
            //private int idAliquotaDispensado = -1;//id_aliquotadispensado;// integer,
            //private int tipoNaturezaReceita = -1;// integer,
            //private Timestamp dataDesembaraco;// timestamp without time zone,
            //private int idEstadoDesembaraco = -1;//id_estadodesembaraco;// integer,
            ni.setNumeroAdicao(imp.getNumeroItem());//private int numeroAdicao = 0;// integer NOT NULL DEFAULT 0,
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

            result.add(ni);
        }
        
        return result;
    }
    
    private int obterAliquotaICMS(NotaFiscalItemIMP imp) {
        
        if (imp.getIdIcms() != null) {
            Integer id = aliquotasPorId.get(imp.getIdIcms());
            if (id != null) {
                return id;
            }
        }
        
        Integer id = aliquotasPorValor.get(String.format("%d-%02f-%02f", imp.getIcmsCst(), imp.getIcmsAliquota(), imp.getIcmsReduzido()));
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