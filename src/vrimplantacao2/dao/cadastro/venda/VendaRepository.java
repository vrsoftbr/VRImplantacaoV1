package vrimplantacao2.dao.cadastro.venda;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import vrimplantacao.utils.Utils;
import vrimplantacao2.parametro.Parametros;
import vrimplantacao2.vo.cadastro.cliente.ClienteEventualVO;
import vrimplantacao2.vo.cadastro.cliente.ClientePreferencialVO;
import vrimplantacao2.vo.cadastro.venda.PdvVendaItemVO;
import vrimplantacao2.vo.cadastro.venda.PdvVendaVO;
import vrimplantacao2.vo.enums.Icms;
import vrimplantacao2.vo.enums.TipoCancelamento;
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
    
    private VendaRepositoryProvider provider;

    public VendaRepository(VendaRepositoryProvider provider) {
        this.provider = provider;
    }
    
    public boolean importar(Set<OpcaoVenda> opt) throws Exception {
        
        try {
            provider.begin();
            
            boolean haDivergencia = false;              
   
            LOG.info("Iniciando o processo de importação das vendas");        
            LOG.config("Opções de importação: " + Arrays.toString(opt.toArray()));   

            provider.notificar("Vendas...Carregando listas auxiliares");
            
            Map<String, ClientePreferencialVO> cliPreferencialAnterior = provider.getClientesPreferenciaisAnteriores();
            Map<Long, ClientePreferencialVO> cliPreferencialCnpj = provider.getClientesPorCnpj();
            Map<String, ClienteEventualVO> cliEventualAnterior = provider.getClientesEventuaisAnteriores();
            Map<Long, ClienteEventualVO> cliEventualCnpj = provider.getClientesEventuaisPorCnpj();   
            
            System.gc();
            
            provider.notificar("Vendas...Convertendo as vendas", (int) provider.getVendaImpSize());
            
            int produtoPadrao = Parametros.get().getItemVendaPadrao();

            for ( Iterator<VendaIMP> iterator = provider.getVendaIMP(); iterator.hasNext(); ) {
                
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
                        haDivergencia = true;
                        LOG.warning("01-Sem cliente preferencial " + impVenda.getIdClientePreferencial() + " na venda " + impVenda.getId());
                    }

                }
                
                /**
                 * Se houver informações sobre o cliente eventual
                 */
                if (impVenda.getClienteEventual()!= null && !"".equals(impVenda.getClienteEventual().trim())) {

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
                        haDivergencia = true;
                        LOG.warning("01-Sem cliente eventual " + impVenda.getClienteEventual()+ " na venda " + impVenda.getId());
                    }

                }

                int cont = 1;

                float subTotalImpressora = 0;
                
                for (VendaItemIMP impItem: provider.getVendaItemIMP(impVenda.getId())) {

                    PdvVendaItemVO item = converter(impItem);

                    item.setVenda(venda);
                    item.setSequencia(cont);
                    cont++;
                    venda.getItens().add(item);

                    Integer produto = provider.getProdutoPorMapeamento(impItem.getCodigoBarras());

                    if ( produto == null && opt.contains(OpcaoVenda.IMPORTAR_POR_CODIGO_ANTERIOR) ) {    
                        produto = provider.getProdutoPorCodigoAnterior(impItem.getProduto());
                    }
                    if ( produto == null && opt.contains(OpcaoVenda.IMPORTAR_POR_EAN_ANTERIOR) ) {
                        produto = provider.getProdutoPorEANAnterior(impItem.getCodigoBarras());
                    }
                    if ( produto == null && String.valueOf(item.getCodigoBarras()).length() > 6 ) {
                        produto = provider.getProdutoPorEANAtual(item.getCodigoBarras());
                    }  
                    if (produto == null && produtoPadrao != 0) {
                        produto = produtoPadrao;
                    }
                    if ( produto == null ) {
                        haDivergencia = true;
                        LOG.warning(
                            String.format(
                                "Produto não encontrado - código:%s ean:%s descricao:%s",
                                impItem.getProduto(),
                                impItem.getCodigoBarras(),
                                impItem.getDescricaoReduzida()
                            )
                        );
                        provider.gravarMapa(
                                impItem.getProduto(),
                                impItem.getCodigoBarras(),
                                impItem.getDescricaoReduzida()
                        );
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
                
                provider.gerarRegistrosGenericos();
                
                provider.gerarMapaResumo();
                
                provider.gerarECFs();
                
                provider.commit();
                return true;
            } else {
                provider.rollback();
                return false;
            }
            
        } catch (Exception e) {
            provider.rollback();
            LOG.log(Level.SEVERE, e.getMessage(), e);
            throw e;            
        }

    }
    
    private String strVenda(VendaIMP venda) {
        return 
                "{ecf:" + venda.getEcf() + 
                ",cupom:" + venda.getNumeroCupom() + 
                ",data:" + DATE_FORMAT.format(venda.getData()) +
                ",hora:" + TIME_FORMAT.format(venda.getHoraInicio()) +
                ",idclientepreferencial:" + venda.getClienteEventual() +
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
            vo.setNumeroSerie(vo.getChaveCfe().substring(22, 31));/**
             * Se houver número de série, tenta vincular com a impressora correta.
             * Caso não encontre dispara uma exceção.
             */
            EcfVO ecf = provider.getEcf(vo.getNumeroSerie());
            if (ecf == null) {                
                String msg = "O numero de série " + vo.getNumeroSerie() + " não está cadastrado para nenhuma ECF!\n";
                msg += strVenda(venda);                
                LOG.log(Level.SEVERE, msg);                
                throw new Exception(msg);
            }            
            
            vo.setModeloImpressora("SAT-CF-e " + ecf.getMarca());
            vo.setEcf(ecf.getId());
            
            LOG.finest("SAT-CF-e | Chave: '" + vo.getChaveCfe() + "' NºSerie: " + vo.getNumeroSerie());
        } else if (vo.getChaveNfce() != null && vo.getChaveNfce().length() == 44) {
            vo.setModeloImpressora("NFC-e");
            vo.setNumeroSerie("00000001");
            LOG.finest("NFC-e | Chave: '" + vo.getChaveNfce() + "' NºSerie: " + vo.getNumeroSerie());
        }
        
        return vo;
    }

    public PdvVendaItemVO converter(VendaItemIMP imp) throws Exception {
        
        PdvVendaItemVO item = new PdvVendaItemVO();
        
        item.setQuantidade(imp.getQuantidade());
        if (imp.getTotalBruto() > 0) {
            item.setPrecoVenda(imp.getTotalBruto() / imp.getQuantidade());
        } else {
            item.setPrecoVenda(imp.getPrecoVenda());
        }
        item.setValorDesconto(imp.getValorDesconto());
        item.setValorAcrescimo(imp.getValorAcrescimo());
        
        Icms aliquota = provider.getAliquota(imp.getIcmsCst(), imp.getIcmsAliq(), imp.getIcmsReduzido());
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
        item.setCancelado(imp.isCancelado());        
        if (item.isCancelado()) {
            item.setValorCancelado(item.getValorTotal());
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
 
        return item;
    
    }
    
}
