package vrimplantacao2.dao.cadastro.venda;

import java.sql.Statement;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import vrframework.classe.Conexao;
import vrimplantacao.dao.cadastro.LojaDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao2.dao.cadastro.produto.ProdutoAnteriorDAO;
import vrimplantacao2.dao.cadastro.produto2.ProdutoIDStack;
import vrimplantacao2.dao.cadastro.produto2.ProdutoIDStackProvider;
import vrimplantacao2.dao.cadastro.produto2.ProdutoRepositoryProvider;
import vrimplantacao2.parametro.Parametros;
import vrimplantacao2.utils.MathUtils;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.cadastro.MercadologicoVO;
import vrimplantacao2.vo.cadastro.ProdutoAliquotaVO;
import vrimplantacao2.vo.cadastro.ProdutoAnteriorVO;
import vrimplantacao2.vo.cadastro.ProdutoAutomacaoVO;
import vrimplantacao2.vo.cadastro.ProdutoComplementoVO;
import vrimplantacao2.vo.cadastro.cliente.ClienteEventualVO;
import vrimplantacao2.vo.cadastro.cliente.ClientePreferencialVO;
import vrimplantacao2.vo.cadastro.venda.PdvVendaItemVO;
import vrimplantacao2.vo.cadastro.venda.PdvVendaVO;
import vrimplantacao2.vo.enums.Icms;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoCancelamento;
import vrimplantacao2.vo.enums.TipoEmbalagem;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;

/**
 * Repositorio para gerenciar a importação das vendas.
 * @author Leandro
 */
public class VendaRepository {
    
    private static final Logger LOG = Logger.getLogger(VendaRepository.class.getName());
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("hh:mm:ss");    
    private static final int LIMIT_OFFSET = 20000;
    
    private VendaRepositoryProvider provider;
    private List<LojaVO> lojas;
    private ProdutoIDStack produtoIDStack;
    private ProdutoRepositoryProvider providerProduto;
    private ProdutoAnteriorDAO produtoAnteriorDAO;
    public boolean idProdutoSemUltimoDigito = false;
    public boolean eBancoUnificado = false;
    
    public VendaRepository(VendaRepositoryProvider provider) {
        this.provider = provider;
    }
    
    public boolean importar(Set<OpcaoVenda> opt) throws Exception {
        
        if (!existeProdutosDivergentes(opt)) {
        
            Map<String, ClientePreferencialVO> cliPreferencialAnterior;
            Map<Long, ClientePreferencialVO> cliPreferencialCnpj;
            Map<String, ClienteEventualVO> cliEventualAnterior;
            Map<Long, ClienteEventualVO> cliEventualCnpj;

            int produtoPadrao;
            boolean ignorarClienteImpVenda, forcarCadastroProdutoNaoExistente;
            boolean haDivergencia = false;

            

            provider.begin();
            try {

                LOG.info("Iniciando o processo de importação das vendas");        
                LOG.config("Opções de importação: " + Arrays.toString(opt.toArray()));   

                provider.notificar("Vendas...Carregando listas auxiliares");
                LOG.info("Carregando listas auxiliares"); 

                cliPreferencialAnterior = provider.getClientesPreferenciaisAnteriores();
                cliPreferencialCnpj = provider.getClientesPorCnpj();
                cliEventualAnterior = provider.getClientesEventuaisAnteriores();
                cliEventualCnpj = provider.getClientesEventuaisPorCnpj();

                System.gc();

                LOG.info("Listas auxiliares carregadas...");
                provider.notificar("Vendas...Convertendo as vendas", (int) provider.getVendaImpSize());

                produtoPadrao = Parametros.get().getItemVendaPadrao();
                ignorarClienteImpVenda = Parametros.get().isIgnorarClienteImpVenda();
                forcarCadastroProdutoNaoExistente = Parametros.get().isForcarCadastroProdutoNaoExistente();

                produtoIDStack = new ProdutoIDStack(new ProdutoIDStackProvider());
                lojas = new LojaDAO().carregar();
                providerProduto = new ProdutoRepositoryProvider();
                produtoAnteriorDAO = new ProdutoAnteriorDAO();

                if (opt.contains(OpcaoVenda.IMPORTAR_POR_CODIGO_ANTERIOR)) {
                    provider.vincularMapaDivergenciaComAnteriores();
                }

                provider.commit();
            } catch (Exception e) {
                provider.rollback();
                LOG.log(Level.SEVERE, e.getMessage(), e);
                throw e;            
            }
            long recordsCount = provider.getVendaImpSize();

            int steps = (int) MathUtils.trunc(recordsCount / LIMIT_OFFSET, 0);
            if (steps == 0 && recordsCount > 0)
                steps = 1;
            LOG.info("Iniciando as vendas: steps " + steps + " recordsCount " + recordsCount); 
            for (int offSet = 0; offSet < steps; offSet++) { 
                provider.begin();
                try {
                    //<editor-fold defaultstate="collapsed" desc="PROCESSAR AS VENDAS">            
                    for ( Iterator<VendaIMP> iterator = provider.getVendaIMP(LIMIT_OFFSET, LIMIT_OFFSET * offSet); iterator.hasNext(); ) {

                        VendaIMP impVenda = iterator.next();

                        LOG.finer("01-Importando a venda " + strVenda(impVenda));

                        PdvVendaVO venda = converter(impVenda);

                        /**
                         * Se houver informações sobre o cliente preferencial
                         */


                        if (impVenda.getIdClientePreferencial() != null && !"".equals(impVenda.getIdClientePreferencial().trim())) {

                            ClientePreferencialVO cliente = cliPreferencialAnterior.get(impVenda.getIdClientePreferencial());
                            if (cliente == null) {
                                cliente = cliPreferencialCnpj.get(venda.getCpf());
                            }

                            if (cliente != null) {
                                LOG.finest(String.format("01-Cliente preferencial localizado %d - %d - %s", cliente.getId(), cliente.getCnpj(), cliente.getNome()));
                                venda.setId_clientePreferencial(cliente.getId());
                                venda.setNomeCliente(cliente.getNome());
                                venda.setCpf(cliente.getCnpj());
                            } else {
                                if (ignorarClienteImpVenda) {
                                    haDivergencia = false;
                                } else {
                                    haDivergencia = true;
                                }

                                LOG.warning("01-Sem cliente preferencial " + impVenda.getIdClientePreferencial() + " na venda " + impVenda.getId());
                            }
                        }

                        /**
                         * Se houver informações sobre o cliente eventual
                         */
                        if (impVenda.getClienteEventual() != null && !"".equals(impVenda.getClienteEventual().trim())) {

                            ClienteEventualVO cliente = cliEventualAnterior.get(impVenda.getClienteEventual());
                            if (cliente == null) {
                                cliente = cliEventualCnpj.get(venda.getCpf());
                            }

                            if (cliente != null) {
                                LOG.finest(String.format("01-Cliente eventual localizado %d - %d - %s", cliente.getId(), cliente.getCnpj(), cliente.getNome()));
                                venda.setId_clienteEventual(cliente.getId());
                                venda.setNomeCliente(cliente.getNome());
                                venda.setCpf(cliente.getCnpj());
                            } else {
                                if (ignorarClienteImpVenda) {
                                    haDivergencia = false;
                                } else {
                                    haDivergencia = true;
                                }

                                LOG.warning("01-Sem cliente eventual " + impVenda.getClienteEventual() + " na venda " + impVenda.getId());
                            }

                        }

                        int cont = 1;

                        float subTotalImpressora = 0;

                        for (VendaItemIMP impItem: provider.getVendaItemIMP(impVenda.getId())) {

                            PdvVendaItemVO item = converter(impItem, venda.isCancelado());

                            item.setVenda(venda);
                            item.setSequencia(cont);
                            item.setData(impVenda.getData());
                            cont++;
                            venda.getItens().add(item);

                            Integer produto = provider.getProdutoPorMapeamento(impItem.getProduto());

                            if ( produto == null && opt.contains(OpcaoVenda.IMPORTAR_POR_CODIGO_ANTERIOR) ) {
                                if (!idProdutoSemUltimoDigito) {
                                    produto = provider.getProdutoPorCodigoAnterior(impItem.getProduto());
                                } else {
                                    produto = provider.getProdutoPorCodigoAnteriorSemUltimoDigito(impItem.getProduto());
                                }
                            }
                            if ( produto == null && opt.contains(OpcaoVenda.IMPORTAR_POR_EAN_ANTERIOR) ) {
                                produto = provider.getProdutoPorEANAnterior(impItem.getCodigoBarras());
                            }
                            if ( produto == null && String.valueOf(item.getCodigoBarras()).length() > 6 ) {
                                produto = provider.getProdutoPorEANAtual(item.getCodigoBarras());
                            }
                            if ( produto == null && opt.contains(OpcaoVenda.IMPORTAR_POR_EAN_ATUAL)) {
                                produto = provider.getProdutoPorEANAtual(item.getCodigoBarras());
                            }
                            if (produto == null && produtoPadrao != 0) {
                                produto = produtoPadrao;
                            }
                            if ( produto == null ) {
                                throw new NullPointerException("Algum produto deveria ter sido ser encontrado");
                            } else {
                                item.setId_produto(produto);
                            }

                            subTotalImpressora += item.getValorTotal() - item.getValorDesconto() + item.getValorAcrescimo();
                        }

                        venda.setSubTotalImpressora(subTotalImpressora);

                        /**
                         * Se houver alguma divergencia, não executa os processos de
                         * gravação mas continua a localizar todos os produtos que não
                         * foram encontrados e lança-los no mapeamento.
                         */
                        if (haDivergencia) {
                            provider.notificar();
                            continue;
                        }

                        /**
                         * Se a venda já existir, elimina ela do cadastro.
                         */
                        provider.eliminarVenda(
                                venda.getEcf(),
                                venda.getNumeroCupom(),
                                venda.getData(),
                                venda.getSubTotalImpressora()
                        );

                        /**
                         * Efetua a gravação da venda, dos seus itens e registra como
                         * uma venda importada para facilitar futuras operações no
                         * banco de dados.
                         */
                        provider.gravar(venda);
                        for (PdvVendaItemVO item: venda.getItens()) {
                            provider.gravar(item);
                        }
                        provider.logarVendaImportadas(venda.getId());

                        provider.notificar();

                    }

                    if (!haDivergencia) {
                        LOG.info("Gravando incluindo consistência do step " + offSet);
                        provider.gerarConsistencia();
                    }

                    //</editor-fold>
                    provider.commit();
                    LOG.info("Step " + offSet + " commitado");
                } catch (Exception e) {
                    provider.rollback();
                    LOG.log(Level.SEVERE, e.getMessage(), e);
                    throw e;            
                }

            }

            provider.begin();
            try {
                
                if (!haDivergencia) {

                    provider.gerarRegistrosGenericos();

                    provider.gerarMapaResumo();

                    provider.gerarECFs();

                    provider.gerarConsistencia();

                    provider.commit();
                    return true;
                } else {

                    provider.rollback();

                    provider.begin();
                    try {
                        //TODO: Remover essa repetição de código.
                        provider.gerarRegistrosGenericos();

                        provider.gerarECFs();

                        provider.gerarConsistencia();

                        provider.commit();
                    } catch (Exception ex) {
                        provider.rollback();
                        LOG.log(Level.SEVERE, ex.getMessage(), ex);
                        throw ex;
                    }

                    return false;
                }

            } catch (Exception ex) {
                provider.rollback();
                LOG.log(Level.SEVERE, ex.getMessage(), ex);
                throw ex;
            }
        }
        throw new Exception("Existem produtos com divergências!");
    }
    
    private String strVenda(VendaIMP venda) {
        return 
                "{ecf:" + venda.getEcf() + 
                ",cupom:" + venda.getNumeroCupom() + 
                ",data:" + DATE_FORMAT.format(venda.getData()) +
                ",hora:" + TIME_FORMAT.format(venda.getHoraInicio()) +
                ",idclientepreferencial:" + venda.getIdClientePreferencial() +
                ",subtotalimpressora:" + String.format("%.2f", venda.getSubTotalImpressora()) + "}";
    }

    /**
     * Converte a {@link VendaIMP} em uma cabeçalho da {@link PdvVendaVO}.
     * @param venda Venda a ser convertida.
     * @return
     * @throws Exception 
     */
    public PdvVendaVO converter(VendaIMP venda) throws Exception {
        
        PdvVendaVO vo = new PdvVendaVO();
        
        vo.setId_loja(provider.getLojaVR());
        vo.setNumeroCupom(venda.getNumeroCupom());
        vo.setEcf(venda.getEcf());
        vo.setData(venda.getData());
        vo.setHoraInicio(new Time(venda.getHoraInicio() != null ? venda.getHoraInicio().getTime() : TIME_FORMAT.parse("00:00:00").getTime()));
        vo.setHoraTermino(new Time(venda.getHoraTermino() != null ? venda.getHoraTermino().getTime() : TIME_FORMAT.parse("00:00:00").getTime()));
        vo.setCancelado(venda.isCancelado());
        vo.setSubTotalImpressora(venda.getSubTotalImpressora());
        vo.setTipoCancelamento(venda.getTipoCancelamento());
        vo.setCpf(Utils.stringToLong(venda.getCpf(), 0));
        vo.setValorDesconto(venda.getValorDesconto());
        vo.setValorAcrescimo(venda.getValorAcrescimo());
        vo.setCanceladoEmVenda(venda.isCanceladoEmVenda());
        vo.setNumeroSerie(venda.getNumeroSerie());
        vo.setNomeCliente(venda.getNomeCliente());
        vo.setEnderecoCliente(venda.getEnderecoCliente());
        vo.setChaveCfe(venda.getChaveCfe());
        vo.setChaveNfce(venda.getChaveNfCe());
        vo.setChaveNfceContingencia(venda.getChaveNfCeContingencia());
        vo.setTipoDesconto(venda.getTipoDesconto());
        vo.setXml(venda.getXml());
        
        vo.setMatricula(provider.getMatricula());
        
        if (vo.getChaveCfe() != null && vo.getChaveCfe().length() == 44) {            
            /**
             * Se a chave estiver preenchida, trata ela para obter o número de 
             * série da impressora.
             */            
            vo.setNumeroSerie(vo.getChaveCfe().substring(22, 31));
            /**
             * Se houver número de série, tenta vincular com a impressora correta.
             * Caso não encontre dispara uma exceção.
             */
            /*EcfVO ecf = provider.getEcf(vo.getNumeroSerie());
            if (ecf == null) {
                //System.out.println("SAT-CF-e | Chave: '" + vo.getChaveCfe() + "' NºSerie: " + vo.getNumeroSerie());                
                String msg = "O numero de série " + vo.getNumeroSerie() + " não está cadastrado para nenhuma ECF!\n";
                msg += strVenda(venda);                
                LOG.log(Level.SEVERE, msg);                
                //throw new Exception(msg);
            }            */
            
            //vo.setModeloImpressora("SAT-CF-e " + ecf.getMarca() == null ? "" : ecf.getMarca());
            vo.setModeloImpressora("SAT-CF-e");
            //vo.setEcf(ecf.getId());
            
            LOG.finest("SAT-CF-e | Chave: '" + vo.getChaveCfe() + "' NºSerie: " + vo.getNumeroSerie());
        } else if (vo.getChaveNfce() != null && vo.getChaveNfce().length() == 44) {
            vo.setModeloImpressora("NFC-e");
            vo.setNumeroSerie("00000001");
            LOG.finest("NFC-e | Chave: '" + vo.getChaveNfce() + "' NºSerie: " + vo.getNumeroSerie());
        }
        
        return vo;
    }

    public PdvVendaItemVO converter(VendaItemIMP imp, boolean cupomCancelado) throws Exception {
        
        PdvVendaItemVO item = new PdvVendaItemVO();
        
        item.setQuantidade(imp.getQuantidade());
        if (imp.getTotalBruto() > 0) {
            item.setPrecoVenda(imp.getTotalBruto() / imp.getQuantidade());
        } else {
            item.setPrecoVenda(imp.getPrecoVenda());
        }        
        item.setValorDesconto(imp.getValorDesconto());
        item.setValorAcrescimo(imp.getValorAcrescimo());
        
        Icms aliquota = provider.getAliquota(imp.getIcmsAliquotaId());
        if (aliquota == null) aliquota = provider.getAliquota(imp.getIcmsCst(), imp.getIcmsAliq(), imp.getIcmsReduzido());
        if (aliquota != null) {
            item.setId_aliquota(aliquota.getId());
        } else {
            LOG.warning(String.format(
                    "Aliquota CST: %03d Aliq: %.2f Red: %.2f não "
                            + "encontrada na tabela aliquota", 
                    imp.getIcmsCst(), 
                    imp.getIcmsAliq(),
                    imp.getIcmsReduzido()
            ));
            item.setId_aliquota(provider.getIsento().getId());
        }
        
        item.setCancelado(imp.isCancelado() || cupomCancelado);
        if (item.isCancelado()) {
            item.setValorCancelado(item.getValorTotal() - item.getValorDesconto() + item.getValorAcrescimo());
            item.setMatriculaCancelamento(provider.getMatricula());
            item.setTipoCancelamento(imp.getTipoCancelamento());
            if (item.getTipoCancelamento() == null) {
                item.setTipoCancelamento(TipoCancelamento.ERRO_DE_REGISTRO);
            }
        }
        item.setContadorDoc(imp.getContadorDoc());
        item.setRegraCalculo("T");
        item.setCodigoBarras(Utils.stringToLong(imp.getCodigoBarras(), -1));
        item.setUnidadeMedida(imp.getUnidadeMedida());
        item.setSequencia(imp.getSequencia());
        item.setTipoDesconto(imp.getTipoDesconto());
        item.setOferta(imp.isOferta());
        item.setCustoComImposto(imp.getCustoComImposto());
        item.setCustoSemImposto(imp.getCustoSemImposto());
        item.setCustoMedioComImposto(imp.getCustoMedioComImposto());
        item.setCustoMedioSemImposto(imp.getCustoMedioSemImposto());
 
        return item;
    
    }

    /**
     * Rotina que verifica se todos os produtos vendidos existentes no banco SQL
     * existem no VR e estão mapeados.
     * @param opt opções de importação.
     * @return True caso tudo esteja correto.
     * @throws Exception 
     */
    private boolean existeProdutosDivergentes(Set<OpcaoVenda> opt) throws Exception {
        boolean forcarCadastroProdutoNaoExistente = Parametros.get().isForcarCadastroProdutoNaoExistente();
        List<VendaItemIMP> produtos = provider.getProdutosVendidos();
        boolean haDivergencia = false;
        List<VendaItemIMP> divergentes = new ArrayList<>();
        
        for (VendaItemIMP impItem: produtos) {
            PdvVendaItemVO item = converter(impItem, false);
            Integer produto = provider.getProdutoPorMapeamento(impItem.getProduto());

            if ( produto == null && opt.contains(OpcaoVenda.IMPORTAR_POR_CODIGO_ANTERIOR) ) {
                if (!idProdutoSemUltimoDigito) {
                    produto = provider.getProdutoPorCodigoAnterior(impItem.getProduto());
                } else {
                    produto = provider.getProdutoPorCodigoAnteriorSemUltimoDigito(impItem.getProduto());
                }
            }
            if ( produto == null && opt.contains(OpcaoVenda.IMPORTAR_POR_EAN_ANTERIOR) ) {
                produto = provider.getProdutoPorEANAnterior(impItem.getCodigoBarras());
            }            
            if ( produto == null && String.valueOf(item.getCodigoBarras()).length() > 6 ) {
                produto = provider.getProdutoPorEANAtual(item.getCodigoBarras());
            }
            if ( produto == null && opt.contains(OpcaoVenda.IMPORTAR_POR_EAN_ATUAL)) {
                produto = provider.getProdutoPorEANAtual(item.getCodigoBarras());
            }
            if ( produto == null ) {
                haDivergencia = true;                
                if (!forcarCadastroProdutoNaoExistente) {
                    final String msg = String.format(
                            "Produto não encontrado - código:%s ean:%s descricao:%s",
                            impItem.getProduto(),
                            impItem.getCodigoBarras(),
                            impItem.getDescricaoReduzida()
                    );
                    LOG.warning(msg);

                    divergentes.add(impItem);

                } else {
                    //<editor-fold defaultstate="collapsed" desc="Inclusão de Produto">
                    LOG.warning(
                            String.format(
                                    "Produto não encontrado - código:%s ean:%s descricao:%s",
                                    impItem.getProduto(),
                                    impItem.getCodigoBarras(),
                                    impItem.getDescricaoReduzida()
                            )
                    );
                    divergentes.add(impItem);

                    try {

                        ProdutoAnteriorVO ant = null;

                        if (!idProdutoSemUltimoDigito) {
                            ant = produtoAnteriorDAO.getProdutoAnterior(provider.getSistema(), provider.getLoja(), impItem.getProduto());
                        } else {
                            ant = produtoAnteriorDAO.getProdutoAnteriorSemUltimoDigito(provider.getSistema(), provider.getLoja(), impItem.getProduto());
                        }

                        if (ant == null) {

                            vrimplantacao2.vo.cadastro.ProdutoVO vo = new vrimplantacao2.vo.cadastro.ProdutoVO();

                            if (eBancoUnificado) {

                                ProdutoAnteriorVO ant2 = produtoAnteriorDAO.getProdutoAnteriorUnificado(provider.getSistema(), impItem.getProduto());

                                if (ant2 == null) {

                                    int codigoAtual = produtoIDStack.obterID(impItem.getProduto(), false);
                                    MercadologicoVO merc = providerProduto.getMercadologico("-1", "-1", "-1", "0", "0");

                                    vo.setId(codigoAtual);
                                    vo.setDescricaoCompleta(impItem.getDescricaoReduzida());
                                    vo.setDescricaoReduzida(vo.getDescricaoCompleta());
                                    vo.setDescricaoGondola(vo.getDescricaoCompleta());
                                    vo.setMercadologico(merc);
                                    vo.setIdFornecedorFabricante(1);

                                    providerProduto.salvar(vo);

                                    for (LojaVO lj : lojas) {
                                        ProdutoComplementoVO compl = vo.getComplementos().make(lj.getId());
                                        compl.setIdLoja(lj.getId());
                                        compl.setDescontinuado(true);
                                        compl.setSituacaoCadastro(SituacaoCadastro.EXCLUIDO);
                                        compl.setPrecoVenda(impItem.getPrecoVenda());
                                        providerProduto.complemento().salvar(compl, false);
                                    }

                                    ProdutoAliquotaVO aliq = vo.getAliquotas().make(Parametros.get().getUfPadrao().getId(), 1);
                                    aliq.setEstado(Parametros.get().getUfPadrao());
                                    aliq.setAliquotaCredito(Icms.getIcms(impItem.getIcmsCst(), impItem.getIcmsAliq(), 0));
                                    aliq.setAliquotaConsumidor(Icms.getIcms(impItem.getIcmsCst(), impItem.getIcmsAliq(), 0));
                                    aliq.setAliquotaCreditoForaEstado(Icms.getIcms(impItem.getIcmsCst(), impItem.getIcmsAliq(), 0));
                                    aliq.setAliquotaDebito(Icms.getIcms(impItem.getIcmsCst(), impItem.getIcmsAliq(), 0));
                                    aliq.setAliquotaDebitoForaEstado(Icms.getIcms(impItem.getIcmsCst(), impItem.getIcmsAliq(), 0));
                                    aliq.setAliquotaDebitoForaEstadoNf(Icms.getIcms(impItem.getIcmsCst(), impItem.getIcmsAliq(), 0));
                                    providerProduto.aliquota().salvar(aliq);

                                    ProdutoAutomacaoVO ean = vo.getEans().make(Long.parseLong(impItem.getCodigoBarras()));
                                    ean.setCodigoBarras(Long.parseLong(impItem.getCodigoBarras()));
                                    ean.setTipoEmbalagem("KG".equals(impItem.getUnidadeMedida().trim()) ? TipoEmbalagem.KG : TipoEmbalagem.UN);
                                    ean.setProduto(vo);
                                    providerProduto.automacao().salvar(ean);

                                    try (Statement stm = Conexao.createStatement()) {
                                        SQLBuilder sql = new SQLBuilder();

                                        sql.setSchema("implantacao");
                                        sql.setTableName("codant_produto");
                                        sql.put("impsistema", provider.getSistema());
                                        sql.put("imploja", provider.getLoja());
                                        sql.put("impid", impItem.getProduto());
                                        sql.put("descricao", impItem.getDescricaoReduzida());
                                        sql.put("codigoatual", codigoAtual);
                                        sql.put("obsimportacao", "PRODUTO IMPORTADO DA VENDA");
                                        sql.put("novo", true);
                                        stm.execute(sql.getInsert());
                                    }

                                    item.setId_produto(codigoAtual);

                                } else {

                                    try (Statement stm = Conexao.createStatement()) {
                                        SQLBuilder sql = new SQLBuilder();

                                        sql.setSchema("implantacao");
                                        sql.setTableName("codant_produto");
                                        sql.put("impsistema", provider.getSistema());
                                        sql.put("imploja", provider.getLoja());
                                        sql.put("impid", impItem.getProduto());
                                        sql.put("descricao", impItem.getDescricaoReduzida());
                                        sql.put("codigoatual", ant2.getCodigoAtual().getId());
                                        sql.put("obsimportacao", "PRODUTO IMPORTADO DA VENDA");
                                        sql.put("novo", true);
                                        stm.execute(sql.getInsert());
                                    }

                                    item.setId_produto(ant2.getCodigoAtual().getId());
                                }
                            }
                        } else {

                            ProdutoAnteriorVO anterior = null;

                            if (eBancoUnificado) {

                                if (ant.getCodigoAtual() == null) {

                                    anterior = produtoAnteriorDAO.getProdutoAnteriorUnificado(provider.getSistema(), ant.getImportId());

                                    if (anterior == null) {

                                        vrimplantacao2.vo.cadastro.ProdutoVO vo = new vrimplantacao2.vo.cadastro.ProdutoVO();

                                        int codigoAtual = produtoIDStack.obterID(impItem.getProduto(), false);
                                        MercadologicoVO merc = providerProduto.getMercadologico("-1", "-1", "-1", "0", "0");

                                        vo.setId(codigoAtual);
                                        vo.setDescricaoCompleta(impItem.getDescricaoReduzida());
                                        vo.setDescricaoReduzida(vo.getDescricaoCompleta());
                                        vo.setDescricaoGondola(vo.getDescricaoCompleta());
                                        vo.setMercadologico(merc);
                                        vo.setIdFornecedorFabricante(1);

                                        providerProduto.salvar(vo);

                                        for (LojaVO lj : lojas) {
                                            ProdutoComplementoVO compl = vo.getComplementos().make(lj.getId());
                                            compl.setIdLoja(lj.getId());
                                            compl.setDescontinuado(true);
                                            compl.setSituacaoCadastro(SituacaoCadastro.EXCLUIDO);
                                            compl.setPrecoVenda(impItem.getPrecoVenda());
                                            providerProduto.complemento().salvar(compl, false);
                                        }

                                        ProdutoAliquotaVO aliq = vo.getAliquotas().make(Parametros.get().getUfPadrao().getId(), 1);
                                        aliq.setEstado(Parametros.get().getUfPadrao());
                                        aliq.setAliquotaCredito(Icms.getIcms(impItem.getIcmsCst(), impItem.getIcmsAliq(), 0));
                                        aliq.setAliquotaConsumidor(Icms.getIcms(impItem.getIcmsCst(), impItem.getIcmsAliq(), 0));
                                        aliq.setAliquotaCreditoForaEstado(Icms.getIcms(impItem.getIcmsCst(), impItem.getIcmsAliq(), 0));
                                        aliq.setAliquotaDebito(Icms.getIcms(impItem.getIcmsCst(), impItem.getIcmsAliq(), 0));
                                        aliq.setAliquotaDebitoForaEstado(Icms.getIcms(impItem.getIcmsCst(), impItem.getIcmsAliq(), 0));
                                        aliq.setAliquotaDebitoForaEstadoNf(Icms.getIcms(impItem.getIcmsCst(), impItem.getIcmsAliq(), 0));
                                        providerProduto.aliquota().salvar(aliq);

                                        ProdutoAutomacaoVO ean = vo.getEans().make(Long.parseLong(impItem.getCodigoBarras()));
                                        ean.setCodigoBarras(Long.parseLong(impItem.getCodigoBarras()));
                                        ean.setTipoEmbalagem("KG".equals(impItem.getUnidadeMedida().trim()) ? TipoEmbalagem.KG : TipoEmbalagem.UN);
                                        ean.setProduto(vo);
                                        providerProduto.automacao().salvar(ean);

                                        try (Statement stm = Conexao.createStatement()) {
                                            SQLBuilder sql = new SQLBuilder();

                                            sql.setSchema("implantacao");
                                            sql.setTableName("codant_produto");
                                            sql.setWhere("impid = " + Utils.quoteSQL(ant.getImportId())
                                                    + " and imploja = '" + provider.getLoja() + "'"
                                                    + " and impsistema = '" + provider.getSistema() + "'"
                                                    + " and codigoatual is null"
                                            );

                                            sql.put("codigoatual", codigoAtual);
                                            stm.execute(sql.getUpdate());
                                        }

                                        item.setId_produto(codigoAtual);

                                    } else {

                                        try (Statement stm = Conexao.createStatement()) {
                                            SQLBuilder sql = new SQLBuilder();

                                            sql.setSchema("implantacao");
                                            sql.setTableName("codant_produto");
                                            sql.setWhere("impid = " + Utils.quoteSQL(ant.getImportId())
                                                    + " and imploja = '" + provider.getLoja() + "'"
                                                    + " and impsistema = '" + provider.getSistema() + "'"
                                                    + " and codigoatual is null"
                                            );

                                            sql.put("codigoatual", anterior.getCodigoAtual().getId());
                                            stm.execute(sql.getUpdate());
                                        }

                                        item.setId_produto(anterior.getCodigoAtual().getId());
                                    }
                                } else {
                                    item.setId_produto(ant.getCodigoAtual().getId());
                                }
                            }
                        }
                    } catch (Exception ex) {
                        throw ex;
                    }
                    //</editor-fold>
                }
            }
            
        }
        
        if (forcarCadastroProdutoNaoExistente) {
            if (!divergentes.isEmpty()) {
                for (VendaItemIMP impItem : divergentes) {
                    provider.gravarMapa(
                            impItem.getProduto(),
                            impItem.getCodigoBarras(),
                            impItem.getDescricaoReduzida()
                    );
                }
            }
        }
        
        return haDivergencia;
    }
    
}
