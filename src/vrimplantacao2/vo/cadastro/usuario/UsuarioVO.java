package vrimplantacao2.vo.cadastro.usuario;

import java.time.LocalDate;
import vrimplantacao2.vo.cadastro.fornecedor.*;
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

public class UsuarioVO {

    private int id;
    private String login;
    private String nome = "SEM NOME";
    private String senha = "**********";
    private int idTipoSetor = 1;
    private SituacaoCadastro situacaoCadastro = SituacaoCadastro.ATIVO;
    private int idLoja = 1;
    private LocalDate dataHoraUltimoAcesso = null;
    private boolean verificaAtualizacao = true;
    private int idTema = 1;
    private boolean exibePopupsNovidades = true;
    private boolean exibepopupestoquecongelado = false;
    private int tempoVerificacaoPopup = 180;
    private boolean exibePopupOfertaContingencia = false;

//    private Integer idPais = 1058;
//    
//    private TipoIndicadorIE tipoIndicadorIe;
//
//    public TipoIndicadorIE getTipoIndicadorIe() {
//        return tipoIndicadorIe;
//    }
//
//    public void setTipoIndicadorIe(TipoIndicadorIE tipoIndicadorIe) {
//        this.tipoIndicadorIe = tipoIndicadorIe;
//    }
//    
//    public static boolean testing = false;
//    
//    private final MultiMap<String, FornecedorContatoVO> contatos = new MultiMap<>(
//        new Factory<FornecedorContatoVO>() {
//            @Override
//            public FornecedorContatoVO make() {
//                FornecedorContatoVO vo = new FornecedorContatoVO();
//                vo.setFornecedor(UsuarioVO.this);
//                return vo;
//            }
//        }
//    );
//    
//    private final MultiMap<String, FornecedorAnteriorVO> anteriores = new MultiMap<>(
//        new Factory<FornecedorAnteriorVO>() {
//            @Override
//            public FornecedorAnteriorVO make() {
//                FornecedorAnteriorVO vo = new FornecedorAnteriorVO();
//                vo.setCodigoAtual(UsuarioVO.this);
//                return vo;
//            }
//        }
//    );
//    
//    public int getId() {
//        return id;
//    }
//
//    public String getRazaoSocial() {
//        return razaoSocial;
//    }
//
//    public String getNomeFantasia() {
//        return nomeFantasia;
//    }
//
//    public TipoInscricao getTipoInscricao() {
//        return tipoInscricao;
//    }
//
//    public long getCnpj() {
//        return cnpj;
//    }
//
//    public String getInscricaoEstadual() {
//        return inscricaoEstadual;
//    }
//
//    public String getInscricaoSuframa() {
//        return inscricaoSuframa;
//    }
//
//    public String getInscricaoMunicipal() {
//        if (this.inscricaoMunicipal != null && inscricaoMunicipal.equals("null")) {
//            return "";
//        }
//        return  inscricaoMunicipal;
//    }
//
//    public Date getDataCadastro() {
//        return dataCadastro;
//    }
//
//    public SituacaoCadastro getSituacaoCadastro() {
//        return situacaoCadastro;
//    }
//
//    public boolean isBloqueado() {
//        return bloqueado;
//    }
//
//    public String getEndereco() {
//        return endereco;
//    }
//
//    public String getNumero() {
//        return numero;
//    }
//
//    public String getComplemento() {
//        return complemento;
//    }
//
//    public String getBairro() {
//        return bairro;
//    }
//
//    public MunicipioVO getMunicipio() {
//        return municipio;
//    }
//
//    public EstadoVO getEstado() {
//        return estado;
//    }
//
//    public int getCep() {
//        return cep;
//    }
//
//    public String getEnderecoCobranca() {
//        return enderecoCobranca;
//    }
//
//    public String getNumeroCobranca() {
//        return numeroCobranca;
//    }
//
//    public String getComplementoCobranca() {
//        return complementoCobranca;
//    }
//
//    public String getBairroCobranca() {
//        return bairroCobranca;
//    }
//
//    public MunicipioVO getMunicipioCobranca() {
//        return municipioCobranca;
//    }
//
//    public EstadoVO getEstadoCobranca() {
//        return estadoCobranca;
//    }
//
//    public int getCepCobranca() {
//        return cepCobranca;
//    }
//    
//    public FamiliaFornecedorVO getFamiliaFornecedorVO() {
//        return familiaFornecedor;
//    }
//
//    public String getTelefone() {
//        return telefone;
//    }
//
//    public int getPedidoMinimoQtd() {
//        return pedidoMinimoQtd;
//    }
//
//    public double getPedidoMinimoValor() {
//        return pedidoMinimoValor;
//    }
//
//    public String getObservacao() {
//        return observacao;
//    }
//
//    public TipoFornecedor getTipoFornecedor() {
//        return tipoFornecedor;
//    }
//
//    public void setId(int id) {
//        this.id = id;
//    }
//
//    public void setRazaoSocial(String razaoSocial) {
//        this.razaoSocial = Utils.acertarTexto(razaoSocial, 40, "SEM NOME");
//    }
//
//    public void setNomeFantasia(String nomeFantasia) {
//        this.nomeFantasia = Utils.acertarTexto(nomeFantasia, 30, Utils.acertarTexto(razaoSocial, 30, "SEM NOME"));
//    }
//
//    public void setTipoInscricao(TipoInscricao tipoInscricao) {
//        this.tipoInscricao = tipoInscricao;
//    }
//
//    public void setCnpj(long cnpj) {
//        if (cnpj > 99999999999999L) {
//            cnpj = this.id;
//        }
//        this.cnpj = cnpj;
//    }
//
//    public void setInscricaoEstadual(String inscricaoEstadual) {
//        this.inscricaoEstadual = Utils.acertarTexto(inscricaoEstadual, 20, "ISENTO");
//    }
//
//    public void setInscricaoSuframa(String inscricaoSuframa) {
//        this.inscricaoSuframa = Utils.formataNumero(inscricaoSuframa, 9, "0");
//    }
//
//    public void setInscricaoMunicipal(String inscricaoMunicipal) {
//        this.inscricaoMunicipal = inscricaoMunicipal;
//    }
//
//    public void setDataCadastro(Date dataCadastro) {
//        this.dataCadastro = dataCadastro;
//    }
//
//    public void setSituacaoCadastro(SituacaoCadastro situacaoCadastro) {
//        this.situacaoCadastro = situacaoCadastro;
//    }
//
//    public void setBloqueado(boolean bloqueado) {
//        this.bloqueado = bloqueado;
//    }
//
//    public void setEndereco(String endereco) {
//        this.endereco = Utils.acertarTexto(endereco, 40, "SEM ENDERECO");
//    }
//
//    public void setNumero(String numero) {
//        this.numero = Utils.acertarTexto(numero, 6, "0");
//    }
//
//    public void setComplemento(String complemento) {
//        this.complemento = Utils.acertarTexto(complemento, 30);
//    }
//
//    public void setBairro(String bairro) {
//        this.bairro = Utils.acertarTexto(bairro, 30, "SEM BAIRRO");
//    }
//
//    public void setMunicipio(MunicipioVO municipio) {
//        this.municipio = municipio;
//    }
//
//    public void setEstado(EstadoVO estado) {
//        this.estado = estado;
//    }
//
//    public void setCep(int cep) {
//        if (cep > 99999999) {
//            cep = Integer.parseInt(String.valueOf(cep).substring(0, 7));
//        }
//        this.cep = cep;
//    }
//
//    public void setEnderecoCobranca(String enderecoCobranca) {
//        this.enderecoCobranca = Utils.acertarTexto(enderecoCobranca, 40, "SEM ENDERECO");
//    }
//
//    public void setNumeroCobranca(String numeroCobranca) {
//        this.numeroCobranca = Utils.acertarTexto(numeroCobranca, 6, "0");
//    }
//
//    public void setComplementoCobranca(String complementoCobranca) {
//        this.complementoCobranca = Utils.acertarTexto(complementoCobranca, 30);
//    }
//
//    public void setBairroCobranca(String bairroCobranca) {
//        this.bairroCobranca = Utils.acertarTexto(bairroCobranca, 30, "SEM BAIRRO");
//    }
//
//    public void setMunicipioCobranca(MunicipioVO municipioCobranca) {
//        this.municipioCobranca = municipioCobranca;
//    }
//
//    public void setEstadoCobranca(EstadoVO estadoCobranca) {
//        this.estadoCobranca = estadoCobranca;
//    }
//
//    public void setCepCobranca(int cepCobranca) {
//        this.cepCobranca = cepCobranca;
//    }
//    
//    public void setFamiliaFornecedor(FamiliaFornecedorVO familiaFornecedor) {
//        this.familiaFornecedor = familiaFornecedor;
//    }
//
//    public void setTelefone(String telefone) {
//        this.telefone = Utils.acertarTexto(telefone, 14, "0000000000");
//    }
//
//    public void setPedidoMinimoQtd(int pedidoMinimoQtd) {
//        this.pedidoMinimoQtd = pedidoMinimoQtd;
//    }
//
//    public void setPedidoMinimoValor(double pedidoMinimoValor) {
//        this.pedidoMinimoValor = pedidoMinimoValor;
//    }
//
//    public void setObservacao(String observacao) {
//        this.observacao = Utils.acertarObservacao(observacao, 2500);
//    }
//
//    public void setTipoFornecedor(TipoFornecedor tipoFornecedor) {
//        this.tipoFornecedor = tipoFornecedor != null ? tipoFornecedor : TipoFornecedor.ATACADO;
//    }
//
//    public MultiMap<String, FornecedorAnteriorVO> getAnteriores() {
//        return anteriores;
//    }
//
//    public MultiMap<String, FornecedorContatoVO> getContatos() {
//        return contatos;
//    }
//
//    public TipoEmpresa getTipoEmpresa() {
//        return tipoEmpresa;
//    }
//
//    public void setTipoEmpresa(TipoEmpresa tipoEmpresa) {
//        this.tipoEmpresa = tipoEmpresa == null ? TipoEmpresa.LUCRO_REAL : tipoEmpresa;
//    }
//
//    public TipoPagamento getTipoPagamento() {
//        return tipoPagamento;
//    }
//
//    public void setTipoPagamento(TipoPagamento tipoPagamento) {
//        this.tipoPagamento = tipoPagamento == null ? Parametros.get().getTipoPagamento(): tipoPagamento;
//    }
//
//    public int getIdBanco() {
//        return idBanco;
//    }
//
//    public void setIdBanco(int idBanco) {
//        this.idBanco = idBanco;
//    }
//
//    public boolean isUtilizaNfe() {
//        return utilizaNfe;
//    }
//
//    public void setUtilizaNfe(boolean utilizaNfe) {
//        this.utilizaNfe = utilizaNfe;
//    }
//
//    public boolean isPermiteNfSemPedido() {
//        return permiteNfSemPedido;
//    }
//
//    public void setPermiteNfSemPedido(boolean permiteNfSemPedido) {
//        this.permiteNfSemPedido = permiteNfSemPedido;
//    }
//
//    public boolean isUtilizaiva() {
//        return utilizaiva;
//    }
//
//    public void setUtilizaiva(boolean utilizaiva) {
//        this.utilizaiva = utilizaiva;
//    }   
//    
//    public boolean getRevenda() {
//        return revenda;
//    }
//
//    public void setRevenda(boolean revenda) {
//        this.revenda = revenda;
//    }    
//
//    public Integer getIdPais() {
//        return idPais;
//    }
//
//    public void setIdPais(Integer idPais) {
//        this.idPais = idPais;
//    }
//    
//    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public int getIdTipoSetor() {
        return idTipoSetor;
    }

    public void setIdTipoSetor(int idTipoSetor) {
        this.idTipoSetor = idTipoSetor;
    }

    public SituacaoCadastro getSituacaoCadastro() {
        return situacaoCadastro;
    }

    public void setSituacaoCadastro(SituacaoCadastro situacaoCadastro) {
        this.situacaoCadastro = situacaoCadastro;
    }
    
    public int getIdLoja() {
        return idLoja;
    }

    public void setIdLoja(int idLoja) {
        this.idLoja = idLoja;
    }

    public LocalDate getDataHoraUltimoAcesso() {
        return dataHoraUltimoAcesso;
    }

    public void setDataHoraUltimoAcesso(LocalDate dataHoraUltimoAcesso) {
        this.dataHoraUltimoAcesso = dataHoraUltimoAcesso;
    }

    public boolean isVerificaAtualizacao() {
        return verificaAtualizacao;
    }

    public void setVerificaAtualizacao(boolean verificaAtualizacao) {
        this.verificaAtualizacao = verificaAtualizacao;
    }

    public int getIdTema() {
        return idTema;
    }

    public void setIdTema(int idTema) {
        this.idTema = idTema;
    }

    public boolean getExibePopupsNovidades() {
        return exibePopupsNovidades;
    }

    public void setExibePopupsNovidades(boolean exibePopupsNovidades) {
        this.exibePopupsNovidades = exibePopupsNovidades;
    }

    public boolean getExibepopupestoquecongelado() {
        return exibepopupestoquecongelado;
    }

    public void setExibepopupestoquecongelado(boolean exibepopupestoquecongelado) {
        this.exibepopupestoquecongelado = exibepopupestoquecongelado;
    }

    public int getTempoVerificacaoPopup() {
        return tempoVerificacaoPopup;
    }

    public void setTempoVerificacaoPopup(int tempoVerificacaoPopup) {
        this.tempoVerificacaoPopup = tempoVerificacaoPopup;
    }

    public boolean getExibePopupOfertaContingencia() {
        return exibePopupOfertaContingencia;
    }

    public void setExibePopupOfertaContingencia(boolean exibePopupOfertaContingencia) {
        this.exibePopupOfertaContingencia = exibePopupOfertaContingencia;
    }

}
