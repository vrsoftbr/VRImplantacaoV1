package vrimplantacao2.dao.cadastro.notafiscal;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import vrimplantacao2.vo.cadastro.notafiscal.NotaEntrada;
import vrimplantacao2.vo.cadastro.notafiscal.NotaSaida;
import vrimplantacao2.vo.cadastro.notafiscal.SituacaoNfe;
import vrimplantacao2.vo.cadastro.notafiscal.SituacaoNotaEntrada;
import vrimplantacao2.vo.importacao.NotaFiscalIMP;

/**
 * Repositório de operações com a nota fiscal.
 * @author Leandro
 */
public class NotaFiscalRepository {
    
    private final NotaFiscalRepositoryProvider provider;
    
    private final int tipoNotaEntrada;
    private final int tipoNotaSaida;

    public NotaFiscalRepository(NotaFiscalRepositoryProvider provider) throws Exception {
        this.provider = provider;
        this.tipoNotaEntrada = provider.getTipoNotaEntrada();
        this.tipoNotaSaida = provider.getTipoNotaSaida();
    }

    public void importar(List<NotaFiscalIMP> notas, HashSet<OpcaoNotaFiscal> opt) throws Exception {
        
        
        
    }
    
    private static Timestamp getTimestamp(Date date) {
        if (date != null) {
            return new Timestamp(date.getTime());
        } else {
            return null;
        }
    }

    public int getFornecedor(String id) throws Exception {
        return provider.getFornecedorById(id);
    }
    
    public int getClienteEventual(String id) throws Exception {
        return provider.getClienteEventual(id);
    }
    
    public NotaEntrada converterNotaEntrada(NotaFiscalIMP imp) throws Exception {
        NotaEntrada n = new NotaEntrada();
        
        n.setIdLoja(this.provider.getLojaVR());
        n.setNumeroNota(imp.getNumeroNota());
        n.setIdFornecedor(getFornecedor(imp.getDestinatario().getId()));
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
        switch (imp.getDestinatario().getTipo()) {
            case FORNECEDOR:
                n.setIdFornecedor(getFornecedor(imp.getDestinatario().getId()));
                break;
            case CLIENTE_EVENTUAL:
                n.setIdClienteEventual(getClienteEventual(imp.getDestinatario().getId()));
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
    
    
}
