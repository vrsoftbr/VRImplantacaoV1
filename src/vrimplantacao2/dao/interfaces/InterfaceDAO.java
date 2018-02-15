package vrimplantacao2.dao.interfaces;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import vrimplantacao.vo.vrimplantacao.NutricionalFilizolaVO;
import vrimplantacao.vo.vrimplantacao.NutricionalToledoVO;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.venda.OpcaoVenda;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.venda.VendaHistoricoIMP;
import vrimplantacao2.vo.cadastro.mercadologico.MercadologicoNivelIMP;
import vrimplantacao2.vo.enums.OpcaoFiscal;
import vrimplantacao2.vo.importacao.ChequeIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.ConveniadoIMP;
import vrimplantacao2.vo.importacao.ConvenioEmpresaIMP;
import vrimplantacao2.vo.importacao.ConvenioTransacaoIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoPagamentoAgrupadoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.OfertaIMP;
import vrimplantacao2.vo.importacao.PautaFiscalIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.RecebimentoCaixaIMP;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;

/**
 * Classe padrão para cria interfaces de importação.
 * @author Leandro
 */
public abstract class InterfaceDAO {
    
    private String lojaOrigem = "";
    
    /**
     * Retorna o nome do sistema que será importado.
     * @return Nome do sistema.
     */
    public abstract String getSistema();
    
    /**
     * Retorna o código da loja de origem selecionada.
     * @return 
     */
    public String getLojaOrigem() {
        return lojaOrigem;
    }

    /**
     * Seta o código da loja origem selecionada.
     * @param LojaOrigem Código da loja de origem.
     */
    public void setLojaOrigem(String LojaOrigem) {
        this.lojaOrigem = LojaOrigem;
    }
    
    /**
     * Retorna uma listagem de mercadológicos.
     * @return Lista com os mercadológicos a serem importados.
     * @throws Exception 
     */
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        return new ArrayList<>();        
    }
    
    /**
     * Retorna um mercadológico organizado por nível.
     * @return
     * @throws Exception 
     */
    public List<MercadologicoNivelIMP> getMercadologicoPorNivel() throws Exception {
        return new ArrayList<>();
    }
    
    /**
     * Retorna uma listagem com as famílias dos produtos.
     * @return Listagem das famílias dos produtos.
     * @throws Exception 
     */
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        return new ArrayList<>();        
    }
    
    /**
     * Retorna uma listagem com os produtos.
     * @return Retorna uma listagem com os produtos cadastrados.
     * @throws Exception 
     */
    public List<ProdutoIMP> getProdutos() throws Exception {
        return new ArrayList<>();
    }
    
    /**
     * Permite o retorno listas especificadas dependendo da {@link OpcaoProduto}.
     * @param opcao Opção de importação.
     * @return
     * @throws Exception 
     */
    public List<ProdutoIMP> getProdutos(OpcaoProduto opcao) throws Exception {
        return null;
    }
    
    /**
     * Retorna uma listagem com os fornecedores.
     * @return Retorna uma listagem com os fornecedores.
     * @throws Exception 
     */
    public List<FornecedorIMP> getFornecedores() throws Exception {
        return new ArrayList<>();
    }

    /**
     * Permite o retorno listas especificadas dependendo da {@link OpcaoFornecedor}.
     * @param opcao Opção de importação.
     * @return
     * @throws Exception 
     */    
    public List<FornecedorIMP> getFornecedores(OpcaoFornecedor opcao) throws Exception {
        return null;
    }

    /**
     * Retorna uma lista com os EANs dos produtos
     * @return 
     * @throws java.lang.Exception 
     */
    public List<ProdutoIMP> getEANs() throws Exception {
        return getProdutos();
    }

    /**
     * Retorna uma lista dos produto fornecedores.
     * @return 
     * @throws java.lang.Exception 
     */
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        return new ArrayList<>();
    }
    
    /**
     * Retorna uma lista com o histórico das vendas {@link VendaHistoricoIMP}.
     * @return
     * @throws Exception 
     */
    public List<VendaHistoricoIMP> getHistoricoVenda() throws Exception {
        return new ArrayList<>();
    }

    /**
     * Retorna uma lista com dados de clientes a serem importados.
     * @return
     * @throws Exception 
     */
    public List<ClienteIMP> getClientes() throws Exception {
        return new ArrayList<>();
    }

    /**
     * Permite o retorno listas especificadas dependendo da {@link OpcaoCliente}.
     * @param opcao Opção de importação.
     * @return
     * @throws Exception 
     */    
    public List<ClienteIMP> getClientes(OpcaoCliente opcao) throws Exception {
        return null;
    }
    
    /**
     * Este método foi criado caso houver a necessidade de separar a importação
     * entre cliente eventual e preferêncial. Por padrão este método encapsula
     * uma chamada ao método {@link InterfaceDAO#getClientes()}
     * @return Lista de Clientes
     * @throws Exception 
     */
    public List<ClienteIMP> getClientesPreferenciais() throws Exception {
        return getClientes();
    }
    
    /**
     * Este método foi criado caso houver a necessidade de separar a importação
     * entre cliente eventual e preferêncial. Por padrão este método encapsula
     * uma chamada ao método {@link InterfaceDAO#getClientes()}
     * @return Lista de Clientes
     * @throws Exception 
     */
    public List<ClienteIMP> getClientesEventuais() throws Exception {
        return getClientes();
    }
    
    /**
     * Retorna uma listagem com os créditos rotativos do cliente.
     * @return Listgem com os créditos rotativos.
     * @throws Exception 
     */
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        return new ArrayList<>();
    }
    
    /**
     * Retorna uma lista com os cheques cadastrados no sistema do cliente.
     * @return Listagem de cheques a serem importados.
     * @throws Exception 
     */
    public List<ChequeIMP> getCheques() throws Exception {
        return new ArrayList<>();
    }
    
    /**
     * Nutricional da balança Filizola.
     * @return Lista de Nutricionais Filizola.
     * @throws Exception 
     */
    public List<NutricionalFilizolaVO> getNutricionalFilizola() throws Exception {
        return new ArrayList<>();
    }
    
    /**
     * Nutricional da balança Toledo.
     * @return Lista de Nutricionais Toledo.
     * @throws Exception 
     */
    public List<NutricionalToledoVO> getNutricionalToledo() throws Exception {
        return new ArrayList<>();
    }

    /**
     * Retorna uma lista com as empresas conveniadas.
     * @return Lista das empresas conveniadas.
     * @throws java.lang.Exception
     */
    public List<ConvenioEmpresaIMP> getConvenioEmpresa() throws Exception {
        return new ArrayList<>();
    }

    /**
     * Retorna uma lista com os conveniados.
     * @return Lista de conveniados.
     * @throws java.lang.Exception
     */
    public List<ConveniadoIMP> getConveniado() throws Exception {
        return new ArrayList<>();
    }

    /**
     * Retorna uma lista com os "à receber" dos conveniados.
     * @return Lista dos recebimentos.
     * @throws Exception 
     */
    public List<ConvenioTransacaoIMP> getConvenioTransacao() throws Exception {
        return new ArrayList<>();
    }

    /**
     * Retorna uma listagem com os as contas à pagar para gravar em outras dispesas.
     * @return Lista de contas à pagar.
     * @throws Exception 
     */
    public List<ContaPagarIMP> getContasPagar() throws Exception {
        return new ArrayList<>();
    }

    /**
     * Retorna uma listagem com os recebimentos do caixa.
     * @return  Listagem com os recebimentos.
     * @throws Exception
     */
    public List<RecebimentoCaixaIMP> getRecebimentosCaixa() throws Exception {
        return new ArrayList<>();
    }

    /**
     * Retorna uma listagem com os pagamentos agrupados por cliente.
     * @return Lista com os pagamentos agrupados.
     * @throws Exception 
     */
    public List<CreditoRotativoPagamentoAgrupadoIMP> getCreditoRotativoPagamentoAgrupado() throws Exception {
        return new ArrayList<>();
    }
    
    /**
     * Retorna a lista de ofertas do sistema cuja data de termino seja maior ou igual ao periodo informado.
     * @param dataTermino Todas as ofertas com data de termino maior ou igual a este parâmetro serã importadas.
     * @return Lista com todas as ofertas.
     * @throws Exception 
     */
    public List<OfertaIMP> getOfertas(Date dataTermino) throws Exception {  
        return new ArrayList<>();
    }

    /**
     * Retorna uma listagem com as pautas fiscais utilizadas pelo sistema do cliente.
     * @param opcoes Opções de importação.
     * @return Listagem com as pauta fiscal.
     * @throws Exception 
     */
    public List<PautaFiscalIMP> getPautasFiscais(Set<OpcaoFiscal> opcoes) throws Exception {
        return new ArrayList<>();
    }
    
    
    public List<VendaIMP> getVendas(Set<OpcaoVenda> opcoes) throws Exception {
        return new ArrayList<>();
    }

    /**
     * Retorna uma listagem com as diversas vendas.
     * @return Listagem com as vendas.
     * @throws Exception 
     */
    public Iterator<VendaIMP> getVendaIterator() throws Exception {
        return null;
    }
    
    /**
     * Retorna uma listagem com os itens da venda.
     * @return Listagem com as vendas.
     * @throws Exception 
     */
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        return null;
    }
    
}
