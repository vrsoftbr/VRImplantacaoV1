package vrimplantacao2.vo.cadastro.fornecedor;

import java.util.Date;
import vrimplantacao.utils.Utils;
import vrimplantacao2.parametro.Parametros;
import vrimplantacao2.utils.Factory;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.local.EstadoVO;
import vrimplantacao2.vo.cadastro.local.MunicipioVO;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoEmpresa;
import vrimplantacao2.vo.enums.TipoFornecedor;
import vrimplantacao2.vo.enums.TipoIndicadorIE;
import vrimplantacao2.vo.enums.TipoInscricao;
import vrimplantacao2.vo.enums.TipoPagamento;

public class FornecedorVO {
    private int id;
    private String razaoSocial = "SEM NOME";
    private String nomeFantasia = "SEM FANTASIA";
    private TipoInscricao tipoInscricao = TipoInscricao.JURIDICA;
    private long cnpj = -1;
    private String inscricaoEstadual = "ISENTO";
    private String inscricaoSuframa = "0";
    private String inscricaoMunicipal = "";
    private Date dataCadastro;
    private SituacaoCadastro situacaoCadastro = SituacaoCadastro.ATIVO;
    private boolean bloqueado = false;
    
    private String endereco = "SEM ENDERECO";
    private String numero = "0";
    private String complemento = "";
    private String bairro = "SEM BAIRRO";
    private MunicipioVO municipio;
    private EstadoVO estado;
    private int cep = -1;
    
    private String enderecoCobranca = "SEM ENDERECO";
    private String numeroCobranca = "0";
    private String complementoCobranca = "";
    private String bairroCobranca = "SEM BAIRRO";
    private MunicipioVO municipioCobranca;
    private EstadoVO estadoCobranca;
    private int cepCobranca = -1;
    
    private String telefone = "0000000000";
    private int pedidoMinimoQtd = 0;
    private double pedidoMinimoValor = 0.0;
    
    private String observacao = "IMPORTADO VR";
    
    private TipoFornecedor tipoFornecedor = TipoFornecedor.ATACADO;
    private TipoEmpresa tipoEmpresa = TipoEmpresa.LUCRO_REAL;
    private TipoPagamento tipoPagamento = testing ? new TipoPagamento(0, "TESTE") : Parametros.get().getTipoPagamento();
    private int idBanco;
    private boolean utilizaNfe = false;
    private boolean permiteNfSemPedido = false;
    private boolean utilizaiva = false;
    private boolean revenda = false;
    
    private Integer idPais = 1058;
    
    private TipoIndicadorIE tipoIndicadorIe;

    public TipoIndicadorIE getTipoIndicadorIe() {
        return tipoIndicadorIe;
    }

    public void setTipoIndicadorIe(TipoIndicadorIE tipoIndicadorIe) {
        this.tipoIndicadorIe = tipoIndicadorIe;
    }
    
    public static boolean testing = false;
    
    private final MultiMap<String, FornecedorContatoVO> contatos = new MultiMap<>(
        new Factory<FornecedorContatoVO>() {
            @Override
            public FornecedorContatoVO make() {
                FornecedorContatoVO vo = new FornecedorContatoVO();
                vo.setFornecedor(FornecedorVO.this);
                return vo;
            }
        }
    );
    
    private final MultiMap<String, FornecedorAnteriorVO> anteriores = new MultiMap<>(
        new Factory<FornecedorAnteriorVO>() {
            @Override
            public FornecedorAnteriorVO make() {
                FornecedorAnteriorVO vo = new FornecedorAnteriorVO();
                vo.setCodigoAtual(FornecedorVO.this);
                return vo;
            }
        }
    );
    
    public int getId() {
        return id;
    }

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public String getNomeFantasia() {
        return nomeFantasia;
    }

    public TipoInscricao getTipoInscricao() {
        return tipoInscricao;
    }

    public long getCnpj() {
        return cnpj;
    }

    public String getInscricaoEstadual() {
        return inscricaoEstadual;
    }

    public String getInscricaoSuframa() {
        return inscricaoSuframa;
    }

    public String getInscricaoMunicipal() {
        if (this.inscricaoMunicipal != null && inscricaoMunicipal.equals("null")) {
            return "";
        }
        return  inscricaoMunicipal;
    }

    public Date getDataCadastro() {
        return dataCadastro;
    }

    public SituacaoCadastro getSituacaoCadastro() {
        return situacaoCadastro;
    }

    public boolean isBloqueado() {
        return bloqueado;
    }

    public String getEndereco() {
        return endereco;
    }

    public String getNumero() {
        return numero;
    }

    public String getComplemento() {
        return complemento;
    }

    public String getBairro() {
        return bairro;
    }

    public MunicipioVO getMunicipio() {
        return municipio;
    }

    public EstadoVO getEstado() {
        return estado;
    }

    public int getCep() {
        return cep;
    }

    public String getEnderecoCobranca() {
        return enderecoCobranca;
    }

    public String getNumeroCobranca() {
        return numeroCobranca;
    }

    public String getComplementoCobranca() {
        return complementoCobranca;
    }

    public String getBairroCobranca() {
        return bairroCobranca;
    }

    public MunicipioVO getMunicipioCobranca() {
        return municipioCobranca;
    }

    public EstadoVO getEstadoCobranca() {
        return estadoCobranca;
    }

    public int getCepCobranca() {
        return cepCobranca;
    }

    public String getTelefone() {
        return telefone;
    }

    public int getPedidoMinimoQtd() {
        return pedidoMinimoQtd;
    }

    public double getPedidoMinimoValor() {
        return pedidoMinimoValor;
    }

    public String getObservacao() {
        return observacao;
    }

    public TipoFornecedor getTipoFornecedor() {
        return tipoFornecedor;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setRazaoSocial(String razaoSocial) {
        this.razaoSocial = Utils.acertarTexto(razaoSocial, 40, "SEM NOME");
    }

    public void setNomeFantasia(String nomeFantasia) {
        this.nomeFantasia = Utils.acertarTexto(nomeFantasia, 30, Utils.acertarTexto(razaoSocial, 30, "SEM NOME"));
    }

    public void setTipoInscricao(TipoInscricao tipoInscricao) {
        this.tipoInscricao = tipoInscricao;
    }

    public void setCnpj(long cnpj) {
        if (cnpj > 99999999999999L) {
            cnpj = this.id;
        }
        this.cnpj = cnpj;
    }

    public void setInscricaoEstadual(String inscricaoEstadual) {
        this.inscricaoEstadual = Utils.acertarTexto(inscricaoEstadual, 20, "ISENTO");
    }

    public void setInscricaoSuframa(String inscricaoSuframa) {
        this.inscricaoSuframa = Utils.formataNumero(inscricaoSuframa, 9, "0");
    }

    public void setInscricaoMunicipal(String inscricaoMunicipal) {
        this.inscricaoMunicipal = inscricaoMunicipal;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public void setSituacaoCadastro(SituacaoCadastro situacaoCadastro) {
        this.situacaoCadastro = situacaoCadastro;
    }

    public void setBloqueado(boolean bloqueado) {
        this.bloqueado = bloqueado;
    }

    public void setEndereco(String endereco) {
        this.endereco = Utils.acertarTexto(endereco, 40, "SEM ENDERECO");
    }

    public void setNumero(String numero) {
        this.numero = Utils.acertarTexto(numero, 6, "0");
    }

    public void setComplemento(String complemento) {
        this.complemento = Utils.acertarTexto(complemento, 30);
    }

    public void setBairro(String bairro) {
        this.bairro = Utils.acertarTexto(bairro, 30, "SEM BAIRRO");
    }

    public void setMunicipio(MunicipioVO municipio) {
        this.municipio = municipio;
    }

    public void setEstado(EstadoVO estado) {
        this.estado = estado;
    }

    public void setCep(int cep) {
        if (cep > 99999999) {
            cep = Integer.parseInt(String.valueOf(cep).substring(0, 7));
        }
        this.cep = cep;
    }

    public void setEnderecoCobranca(String enderecoCobranca) {
        this.enderecoCobranca = Utils.acertarTexto(enderecoCobranca, 40, "SEM ENDERECO");
    }

    public void setNumeroCobranca(String numeroCobranca) {
        this.numeroCobranca = Utils.acertarTexto(numeroCobranca, 6, "0");
    }

    public void setComplementoCobranca(String complementoCobranca) {
        this.complementoCobranca = Utils.acertarTexto(complementoCobranca, 30);
    }

    public void setBairroCobranca(String bairroCobranca) {
        this.bairroCobranca = Utils.acertarTexto(bairroCobranca, 30, "SEM BAIRRO");
    }

    public void setMunicipioCobranca(MunicipioVO municipioCobranca) {
        this.municipioCobranca = municipioCobranca;
    }

    public void setEstadoCobranca(EstadoVO estadoCobranca) {
        this.estadoCobranca = estadoCobranca;
    }

    public void setCepCobranca(int cepCobranca) {
        this.cepCobranca = cepCobranca;
    }

    public void setTelefone(String telefone) {
        this.telefone = Utils.acertarTexto(telefone, 14, "0000000000");
    }

    public void setPedidoMinimoQtd(int pedidoMinimoQtd) {
        this.pedidoMinimoQtd = pedidoMinimoQtd;
    }

    public void setPedidoMinimoValor(double pedidoMinimoValor) {
        this.pedidoMinimoValor = pedidoMinimoValor;
    }

    public void setObservacao(String observacao) {
        this.observacao = Utils.acertarObservacao(observacao, 2500);
    }

    public void setTipoFornecedor(TipoFornecedor tipoFornecedor) {
        this.tipoFornecedor = tipoFornecedor != null ? tipoFornecedor : TipoFornecedor.ATACADO;
    }

    public MultiMap<String, FornecedorAnteriorVO> getAnteriores() {
        return anteriores;
    }

    public MultiMap<String, FornecedorContatoVO> getContatos() {
        return contatos;
    }

    public TipoEmpresa getTipoEmpresa() {
        return tipoEmpresa;
    }

    public void setTipoEmpresa(TipoEmpresa tipoEmpresa) {
        this.tipoEmpresa = tipoEmpresa == null ? TipoEmpresa.LUCRO_REAL : tipoEmpresa;
    }

    public TipoPagamento getTipoPagamento() {
        return tipoPagamento;
    }

    public void setTipoPagamento(TipoPagamento tipoPagamento) {
        this.tipoPagamento = tipoPagamento == null ? Parametros.get().getTipoPagamento(): tipoPagamento;
    }

    public int getIdBanco() {
        return idBanco;
    }

    public void setIdBanco(int idBanco) {
        this.idBanco = idBanco;
    }

    public boolean isUtilizaNfe() {
        return utilizaNfe;
    }

    public void setUtilizaNfe(boolean utilizaNfe) {
        this.utilizaNfe = utilizaNfe;
    }

    public boolean isPermiteNfSemPedido() {
        return permiteNfSemPedido;
    }

    public void setPermiteNfSemPedido(boolean permiteNfSemPedido) {
        this.permiteNfSemPedido = permiteNfSemPedido;
    }

    public boolean isUtilizaiva() {
        return utilizaiva;
    }

    public void setUtilizaiva(boolean utilizaiva) {
        this.utilizaiva = utilizaiva;
    }   
    
    public boolean getRevenda() {
        return revenda;
    }

    public void setRevenda(boolean revenda) {
        this.revenda = revenda;
    }    

    public Integer getIdPais() {
        return idPais;
    }

    public void setIdPais(Integer idPais) {
        this.idPais = idPais;
    }
    
    
}
