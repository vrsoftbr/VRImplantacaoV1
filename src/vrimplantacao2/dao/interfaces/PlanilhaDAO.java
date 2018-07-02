package vrimplantacao2.dao.interfaces;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import vrframework.classe.ProgressBar;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.utils.arquivo.Arquivo;
import vrimplantacao2.utils.arquivo.ArquivoFactory;
import vrimplantacao2.utils.arquivo.LinhaArquivo;
import vrimplantacao2.utils.arquivo.csv.ArquivoCSV2;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.vo.enums.OpcaoFiscal;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoCancelamento;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoDesconto;
import vrimplantacao2.vo.enums.TipoEmbalagem;
import vrimplantacao2.vo.enums.TipoEstadoCivil;
import vrimplantacao2.vo.enums.TipoIndicadorIE;
import vrimplantacao2.vo.enums.TipoOrgaoPublico;
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoPagamentoAgrupadoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorContatoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.PautaFiscalIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;

public class PlanilhaDAO extends InterfaceDAO implements MapaTributoProvider {
    
    private static final Logger LOG = Logger.getLogger(PlanilhaDAO.class.getName());
    
    private String arquivo;
    private String sistema = "PLANILHA";
    private Map<String, String> opcoes = new LinkedHashMap<>();

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        
        Set<OpcaoProduto> result = super.getOpcoesDisponiveisProdutos();
        result.add(OpcaoProduto.PAUTA_FISCAL);
        result.add(OpcaoProduto.PAUTA_FISCAL_PRODUTO);
        
        return result;
    }
    
    
    
    public void setSistema(String sistema) {
        if (sistema == null) {
            sistema = "PLANILHA";
        }
        this.sistema = sistema;
    }
    
    @Override
    public String getSistema() {
        return this.sistema;
    }
    
    public String getArquivo() {
        return arquivo;
    }

    public void setArquivo(String arquivo) {
        this.arquivo = arquivo;
    }

    public Map<String, String> getOpcoes() {
        return opcoes;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        Arquivo mercadologicos = ArquivoFactory.getArquivo(this.arquivo, getOpcoes());
        ProgressBar.setStatus("Carregando família de produtos...");
        int cont = 0;
        
        for (LinhaArquivo linha: mercadologicos) {
            MercadologicoIMP helper = new MercadologicoIMP();
            
            helper.setImportSistema(getSistema());
            helper.setImportLoja(getLojaOrigem());
            
            helper.setMerc1ID(linha.getString("cod_mercadologico1"));
            helper.setMerc1Descricao(linha.getString("mercadologico1"));
            
            helper.setMerc2ID(linha.getString("cod_mercadologico2"));
            helper.setMerc2Descricao(linha.getString("mercadologico2"));
            
            helper.setMerc3ID(linha.getString("cod_mercadologico3"));
            helper.setMerc3Descricao(linha.getString("mercadologico3"));
            
            helper.setMerc4ID(linha.getString("cod_mercadologico4"));
            helper.setMerc4Descricao(linha.getString("mercadologico4"));
            
            helper.setMerc5ID(linha.getString("cod_mercadologico5"));
            helper.setMerc5Descricao(linha.getString("mercadologico5"));
            
            result.add(helper);
            cont++;
            ProgressBar.setStatus("Carregando mercadológico..." + cont);
        }
        
        return result;        
    }
    
    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();
        
        Arquivo familias = ArquivoFactory.getArquivo(this.arquivo, getOpcoes());        
        ProgressBar.setStatus("Carregando família de produtos...");
        int cont = 0;
        
        for (LinhaArquivo linha: familias) {
            String id = linha.getString("id_familiaproduto");
            if (id != null && !"".equals(id.trim())) {
                FamiliaProdutoIMP familia = new FamiliaProdutoIMP();

                familia.setImportSistema(getSistema());
                familia.setImportLoja(getLojaOrigem());
                familia.setImportId(id);
                familia.setDescricao(linha.getString("familiaproduto"));
                familia.setSituacaoCadastro(SituacaoCadastro.ATIVO);

                result.add(familia);
            }
            cont++;
            ProgressBar.setStatus("Carregando família de produtos..." + cont);
        }
        
        return result;
    }
    
    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        
        Arquivo produtos = ArquivoFactory.getArquivo(this.arquivo, getOpcoes());  
        ProgressBar.setStatus("Carregando produtos...");
        
        int cont1 = 0;
        int cont2 = 0;
        
        Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().getProdutosBalanca();
        
        for (LinhaArquivo linha: produtos) {
            String id = linha.getString("id");
            if (id != null && !"".equals(id.trim())) {
                ProdutoIMP produto = new ProdutoIMP();

                produto.setImportSistema(getSistema());
                produto.setImportLoja(getLojaOrigem());
                produto.setImportId(id);
                produto.setEan(linha.getString("codigobarras"));
                produto.setQtdEmbalagem(linha.getInt("qtdembalagem"));
                produto.setTipoEmbalagem(linha.getString("unidade"));
                switch (Utils.acertarTexto(linha.getString("balanca"))) {
                    case "S": {
                        produto.seteBalanca(true);
                    }; break;
                    case "P": {
                        produto.seteBalanca(true);
                        produto.setTipoEmbalagem(TipoEmbalagem.KG.getSigla());
                    }; break;
                    case "U": {
                        produto.seteBalanca(true);
                        produto.setTipoEmbalagem(TipoEmbalagem.UN.getSigla());
                    }; break;
                    default: {
                        produto.seteBalanca(false);
                    }; break;
                }
                int eanBal;
                if ("0".equals(Utils.stringLong(produto.getEan()))) {
                    eanBal = Utils.stringToInt(produto.getImportId());
                } else {
                    eanBal = Utils.stringToInt(produto.getEan());
                }
                ProdutoBalancaVO bal = produtosBalanca.get(eanBal);
                if (bal != null) {
                    produto.seteBalanca(true);
                    switch (bal.getPesavel()) {
                        case "U": {
                            produto.setTipoEmbalagem(TipoEmbalagem.UN.getSigla());
                        }; break;
                        default: {
                            produto.setTipoEmbalagem(TipoEmbalagem.KG.getSigla());
                        }; break;
                    }
                    if (bal.getValidade() != 0) {
                        produto.setValidade(bal.getValidade());
                    }
                }
                produto.setDescricaoCompleta(linha.getString("descricaocompleta"));
                produto.setDescricaoReduzida(linha.getString("descricaoreduzida"));
                produto.setDescricaoGondola(linha.getString("descricaogondola"));
                produto.setCodMercadologico1(linha.getString("cod_mercadologico1"));
                produto.setCodMercadologico2(linha.getString("cod_mercadologico2"));
                produto.setCodMercadologico3(linha.getString("cod_mercadologico3"));
                produto.setCodMercadologico4(linha.getString("cod_mercadologico4"));
                produto.setCodMercadologico5(linha.getString("cod_mercadologico5"));
                produto.setIdFamiliaProduto(linha.getString("id_familiaproduto"));
                produto.setPesoBruto(linha.getDouble("pesobruto"));
                produto.setPesoLiquido(linha.getDouble("pesoliquido"));
                produto.setDataCadastro(linha.getData("datacadastro"));
                produto.setValidade(linha.getInt("validade"));
                produto.setMargem(linha.getDouble("margem"));
                produto.setEstoqueMaximo(linha.getDouble("estoquemaximo"));
                produto.setEstoqueMinimo(linha.getDouble("estoqueminimo"));
                produto.setEstoque(linha.getDouble("estoque"));
                produto.setCustoComImposto(linha.getDouble("custocomimposto"));
                produto.setCustoSemImposto(linha.getDouble("custosemimposto"));
                produto.setPrecovenda(linha.getDouble("precovenda"));
                switch (Utils.acertarTexto(linha.getString("ativo"))) {
                    case "N": produto.setSituacaoCadastro(SituacaoCadastro.EXCLUIDO); break;
                    default: produto.setSituacaoCadastro(SituacaoCadastro.ATIVO);
                }
                produto.setNcm(linha.getString("ncm"));
                produto.setCest(linha.getString("cest"));
                produto.setPiscofinsCstCredito(linha.getString("piscofins_cst_credito"));
                produto.setPiscofinsCstDebito(linha.getString("piscofins_cst_debito"));
                produto.setPiscofinsNaturezaReceita(linha.getString("piscofins_natureza_receita"));
                produto.setIcmsCst(linha.getInt("icms_cst"));
                produto.setIcmsAliq(linha.getDouble("icms_aliquota"));
                produto.setIcmsReducao(linha.getDouble("icms_reduzido"));
                produto.setIcmsCreditoId(linha.getString("icms_credito_id"));
                produto.setIcmsDebitoId(linha.getString("icms_debito_id"));
                produto.setPautaFiscalId(linha.getString("id_pautafiscal"));

                result.add(produto);
            }
            cont2++;
            cont1++;
            if (cont2 == 1000) {
                cont2 = 0;
                ProgressBar.setStatus("Carregando produtos..." + cont1);
            }
        }
        
        return result;        
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        
        Arquivo produtos = ArquivoFactory.getArquivo(this.arquivo, getOpcoes());  
        ProgressBar.setStatus("Carregando fornecedores...");
        
        int cont1 = 0;
        int cont2 = 0;

        for (LinhaArquivo linha: produtos) {
            String id = linha.getString("id");
            if (id != null && !"".equals(id.trim())) {
                FornecedorIMP forn = new FornecedorIMP();

                forn.setImportSistema(getSistema());
                forn.setImportLoja(getLojaOrigem());
                forn.setImportId(id);
                
                forn.setRazao(linha.getString("razao"));
                forn.setFantasia(linha.getString("fantasia"));
                forn.setCnpj_cpf(linha.getString("cnpj_cpf"));
                forn.setIe_rg(linha.getString("ie_rg"));
                forn.setInsc_municipal(linha.getString("insc_municipal"));
                forn.setSuframa(linha.getString("suframa"));
                forn.setAtivo(!"N".equals(linha.getString("ativo")));
                
                forn.setEndereco(linha.getString("endereco"));
                forn.setNumero(linha.getString("numero"));
                forn.setComplemento(linha.getString("complemento"));
                forn.setBairro(linha.getString("bairro"));
                forn.setIbge_municipio(linha.getInt("ibge_municipio"));
                forn.setMunicipio(linha.getString("municipio"));
                forn.setIbge_uf(linha.getInt("ibge_uf"));
                forn.setUf(linha.getString("uf"));
                forn.setCep(linha.getString("cep"));
                
                forn.setCob_endereco(linha.getString("cob_endereco"));
                forn.setCob_numero(linha.getString("cob_numero"));
                forn.setCob_complemento(linha.getString("cob_complemento"));
                forn.setCob_bairro(linha.getString("cob_bairro"));
                forn.setCob_ibge_municipio(linha.getInt("cob_ibge_municipio"));
                forn.setCob_municipio(linha.getString("cob_municipio"));
                forn.setCob_ibge_uf(linha.getInt("cob_ibge_uf"));
                forn.setCob_uf(linha.getString("cob_uf"));
                forn.setCob_cep(linha.getString("cob_cep"));
                
                forn.setTel_principal(linha.getString("tel_principal"));
                forn.setQtd_minima_pedido(linha.getInt("qtd_minima_pedido"));
                forn.setValor_minimo_pedido(linha.getDouble("valor_minimo_pedido"));
                forn.setDatacadastro(linha.getData("datacadastro"));
                forn.setObservacao(linha.getString("observacao"));    
                
                int i = 1;
                while (true) {
                    String prefixo = "cont" + i + "_";
                    if (linha.existsColumn(prefixo + "nome")) {
                        if (!"".equals(linha.getString(prefixo + "nome").trim())); {
                            FornecedorContatoIMP contato = forn.getContatos().make(
                                    forn.getImportSistema(),
                                    forn.getImportLoja(),
                                    forn.getImportId(),
                                    String.valueOf(i)
                            );

                            contato.setImportId(String.valueOf(i));
                            contato.setNome(linha.getString(prefixo + "nome"));
                            contato.setTelefone(linha.getString(prefixo + "telefone"));
                            contato.setCelular(linha.getString(prefixo + "celular"));
                            contato.setEmail(linha.getString(prefixo + "email"));
                            contato.setTipoContato(TipoContato.getByDescricao(linha.getString(prefixo + "tipo")));
                        }

                        i++;
                    } else {
                        break;
                    }
                }
                
                result.add(forn);
            }
            cont2++;
            cont1++;
            if (cont2 == 1000) {
                cont2 = 0;
                ProgressBar.setStatus("Carregando fornecedores..." + cont1);
            }
        }
        
        return result; 
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();
        
        Arquivo produtos = ArquivoFactory.getArquivo(this.arquivo, getOpcoes());  
        
        ProgressBar.setStatus("Carregando Produtos Fornecedores");
        
        int cont1 = 0;
        int cont2 = 0;
        for (LinhaArquivo linha: produtos) {
            ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
            imp.setImportSistema(getSistema());
            imp.setImportLoja(getLojaOrigem());
            imp.setIdFornecedor(linha.getString("id_fornecedor"));
            imp.setIdProduto(linha.getString("id_produto"));
            imp.setDataAlteracao(linha.getData("dataalteracao"));
            imp.setCodigoExterno(linha.getString("cod_produto_fornecedor"));
            imp.setPesoEmbalagem(linha.getDouble("pesoembalagem"));
            imp.setQtdEmbalagem(linha.getInt("qtdembalagem"));
            imp.setCustoTabela(linha.getDouble("custo_tabelado"));
            result.add(imp);
            cont2++;
            cont1++;
            if (cont2 == 1000) {
                cont2 = 0;
                ProgressBar.setStatus("Carregando Produtos Fornecedores..." + cont1);
            }
        }
        
        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        
        Map<String, TipoEstadoCivil> estCivil = new HashMap<>();
        for (TipoEstadoCivil est: TipoEstadoCivil.values()) {
            estCivil.put(est.toString().substring(0, 3), est);
        }
        
        Arquivo arq = ArquivoFactory.getArquivo(this.arquivo, getOpcoes());
        
        for (LinhaArquivo linha: arq) {
            ClienteIMP imp = new ClienteIMP();
            try {
            imp.setId(linha.getString("id"));
            imp.setCnpj(linha.getString("cnpj"));
            imp.setInscricaoestadual(linha.getString("inscricaoestadual"));
            imp.setOrgaoemissor(linha.getString("orgaoemissor"));
            imp.setRazao(linha.getString("razao"));
            imp.setFantasia(linha.getString("fantasia"));
            imp.setAtivo(!"N".equalsIgnoreCase(linha.getString("ativo")));
            imp.setBloqueado("N".equalsIgnoreCase(linha.getString("bloqueado")));
            imp.setDataBloqueio(linha.getData("dataBloqueio"));
            imp.setEndereco(linha.getString("endereco"));
            imp.setNumero(linha.getString("numero"));
            imp.setComplemento(linha.getString("complemento"));
            imp.setBairro(linha.getString("bairro"));
            imp.setMunicipioIBGE(linha.getInt("municipioIBGE"));
            imp.setMunicipio(linha.getString("municipio"));
            imp.setUfIBGE(linha.getInt("ufIBGE"));
            imp.setUf(linha.getString("uf"));
            imp.setCep(linha.getString("cep"));
            String civil = linha.getString("estadoCivil") + "   ";
            civil = (civil != null ? civil.substring(1, 3) : "NAO");
            imp.setEstadoCivil(estCivil.get(civil));
            imp.setDataNascimento(linha.getData("dataNascimento"));
            imp.setDataCadastro(linha.getData("dataCadastro"));
            String sexo = linha.getString("sexo") != null ? linha.getString("sexo") : "";
            imp.setSexo("F".startsWith(sexo.toUpperCase()) ? TipoSexo.FEMININO : TipoSexo.MASCULINO);
            imp.setEmpresa(linha.getString("empresa"));
            imp.setEmpresaEndereco(linha.getString("empresaEndereco"));
            imp.setEmpresaNumero(linha.getString("empresaNumero"));
            imp.setEmpresaComplemento(linha.getString("empresaComplemento"));
            imp.setEmpresaBairro(linha.getString("empresaBairro"));
            imp.setEmpresaMunicipioIBGE(linha.getInt("empresaMunicipioIBGE"));
            imp.setEmpresaMunicipio(linha.getString("empresaMunicipio"));
            imp.setEmpresaUfIBGE(linha.getInt("empresaUfIBGE"));
            imp.setEmpresaUf(linha.getString("empresaUf"));
            imp.setEmpresaCep(linha.getString("empresaCep"));
            imp.setEmpresaTelefone(linha.getString("empresaTelefone"));
            imp.setDataAdmissao(linha.getData("dataAdmissao"));
            imp.setCargo(linha.getString("cargo"));
            imp.setSalario(linha.getDouble("salario"));
            imp.setValorLimite(linha.getDouble("valorLimite"));
            imp.setNomeConjuge(linha.getString("nomeConjuge"));
            imp.setNomePai(linha.getString("nomePai"));
            imp.setNomeMae(linha.getString("nomeMae"));
            imp.setObservacao(linha.getString("observacao"));
            imp.setDiaVencimento(linha.getInt("diaVencimento"));
            imp.setPermiteCreditoRotativo(!"N".equalsIgnoreCase(linha.getString("permiteCreditoRotativo")));
            imp.setPermiteCheque(!"N".equalsIgnoreCase(linha.getString("permiteCheque")));
            imp.setTelefone(linha.getString("telefone"));
            imp.setCelular(linha.getString("celular"));
            imp.setEmail(linha.getString("email"));
            //EVENTUAL
            imp.setFax(linha.getString("fax"));
            imp.setCobrancaTelefone(linha.getString("cobrancaTelefone"));
            imp.setPrazoPagamento(linha.getInt("prazopagamento"));
            imp.setCobrancaEndereco(linha.getString("cobrancaendereco"));
            imp.setCobrancaNumero(linha.getString("cobrancanumero"));
            imp.setCobrancaComplemento(linha.getString("cobrancacomplemento"));
            imp.setCobrancaBairro(linha.getString("cobrancabairro"));
            imp.setCobrancaMunicipioIBGE(linha.getInt("cobrancamunicipioibge"));
            imp.setCobrancaMunicipio(linha.getString("cobrancamunicipio"));
            imp.setCobrancaUfIBGE(linha.getInt("cobrancaufibge"));
            imp.setCobrancaUf(linha.getString("cobrancauf"));
            imp.setCobrancaCep(linha.getString("cobrancacep"));
            String tipoOrgaoPublicoStr = linha.getString("tipoorgaopublico");
            tipoOrgaoPublicoStr = Utils.acertarTexto(tipoOrgaoPublicoStr, "NENHUM");
            switch (tipoOrgaoPublicoStr) {
                case "ESTADUAL": imp.setTipoOrgaoPublico(TipoOrgaoPublico.ESTADUAL); break;
                case "FEDERAL": imp.setTipoOrgaoPublico(TipoOrgaoPublico.FEDERAL); break;
                default: imp.setTipoOrgaoPublico(TipoOrgaoPublico.NENHUM); break;
            }
            imp.setLimiteCompra(linha.getDouble("limitecompra"));
            imp.setInscricaoMunicipal(linha.getString("inscricaomunicipal"));
            String tipoIndicadorIeStr = Utils.acertarTexto(linha.getString("tipoindicadorie"), "NAO CONTRIBUINTE");
            switch (tipoIndicadorIeStr) {
                case "ICMS": imp.setTipoIndicadorIe(TipoIndicadorIE.CONTRIBUINTE_ICMS); break;
                case "ISENTO": imp.setTipoIndicadorIe(TipoIndicadorIE.CONTRIBUINTE_ISENTO); break;
                default: imp.setTipoIndicadorIe(TipoIndicadorIE.NAO_CONTRIBUINTE); break;
            }
            
            int i = 1;
            while (true) {
                String prefixo = "cont" + i + "_";
                if (linha.existsColumn(prefixo + "nome")) {
                    if (!"".equals(linha.getString(prefixo + "nome").trim())); {
                        String email = linha.getString(prefixo + "email");
                        String cel = linha.getString(prefixo + "celular");
                        String fone = linha.getString(prefixo + "telefone");
                        String nome = linha.getString(prefixo + "nome");
                        if (
                                !"".equals(nome) ||
                                !"".equals(cel) ||
                                !"".equals(fone) ||
                                !"".equals(email)
                        ) {
                            imp.addContato(String.valueOf(i), nome, fone, cel, email);
                        }
                    }

                    i++;
                } else {
                    break;
                }
            }
            } catch (Exception e) {
                throw e;
            }
            
            result.add(imp);
        }
        
        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();
        
        Arquivo arq = ArquivoFactory.getArquivo(this.arquivo, getOpcoes());
        
        for (LinhaArquivo linha: arq) {
            CreditoRotativoIMP imp = new CreditoRotativoIMP();
            
            try {
                imp.setId(linha.getString("id"));
                imp.setCnpjCliente(linha.getString("cnpj"));
                imp.setDataEmissao(linha.getData("emissao"));
                imp.setDataVencimento(linha.getData("vencimento"));
                imp.setEcf(linha.getString("ecf"));
                imp.setIdCliente(linha.getString("idcliente"));
                imp.setJuros(linha.getDouble("juros"));
                imp.setMulta(linha.getDouble("multa"));
                imp.setNumeroCupom(linha.getString("cupom"));
                imp.setObservacao(linha.getString("observacao"));
                imp.setParcela(linha.getInt("parcela"));
                imp.setValor(linha.getDouble("valor"));
            } catch (Exception e) {
                throw e;
            }
            
            result.add(imp);
        }
        
        return result;
    }

    @Override
    public List<CreditoRotativoPagamentoAgrupadoIMP> getCreditoRotativoPagamentoAgrupado() throws Exception {
        List<CreditoRotativoPagamentoAgrupadoIMP> result = new ArrayList<>();
        
        Arquivo arq = ArquivoFactory.getArquivo(this.arquivo, getOpcoes());
        
        for (LinhaArquivo linha: arq) {
            CreditoRotativoPagamentoAgrupadoIMP imp = new CreditoRotativoPagamentoAgrupadoIMP();
            
            imp.setIdCliente(linha.getString("idcliente"));
            imp.setValor(linha.getDouble("valor"));
                        
            result.add(imp);
        }
        
        return result;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        
        Arquivo arq = ArquivoFactory.getArquivo(this.arquivo, getOpcoes());
        
        for (LinhaArquivo linha: arq) {                        
            result.add(new MapaTributoIMP(
                    linha.getString("codtrib"),
                    linha.getString("descricao"),
                    linha.getInt("cst"),
                    linha.getDouble("aliquota"),
                    linha.getDouble("reduzido")
            ));
        }
        
        return result;
    }
    
    
    private String arquivoVendas;
    private String arquivoVendasItens;

    public void setArquivoVendas(String arquivoVendas) {
        this.arquivoVendas = arquivoVendas;
    }    
    public void setArquivoVendasItens(String arquivoVendasItens) {
        this.arquivoVendasItens = arquivoVendasItens;
    }

    @Override
    public Iterator<VendaIMP> getVendaIterator() throws Exception {
        
        String delimiter = ";";
        char stringQuote = '\"';
        if (opcoes != null) {
            delimiter = getOpcoes().get("delimiter") != null ? getOpcoes().get("delimiter") : ";"; 
            stringQuote = (getOpcoes().get("quote") != null ? getOpcoes().get("quote") : "\"").charAt(0);
        }
        
        return new VendaIterator(new ArquivoCSV2(arquivoVendas, delimiter.charAt(0), stringQuote));
        
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        String delimiter = ";";
        char stringQuote = '\"';
        if (opcoes != null) {
            delimiter = getOpcoes().get("delimiter") != null ? getOpcoes().get("delimiter") : ";"; 
            stringQuote = (getOpcoes().get("quote") != null ? getOpcoes().get("quote") : "\"").charAt(0);
        }
        
        return new VendaItemIterator(new ArquivoCSV2(arquivoVendasItens, delimiter.charAt(0), stringQuote));
    }
    
    private class VendaIterator implements Iterator<VendaIMP> {
        
        private ArquivoCSV2 csv;

        public VendaIterator(ArquivoCSV2 csv) {
            this.csv = csv;
        }
        
        @Override
        public boolean hasNext() {
            return this.csv.hasNext();
        }

        @Override
        public VendaIMP next() {
            
            LinhaArquivo ln = csv.next();

            VendaIMP imp = new VendaIMP();

            imp.setId(ln.getString("id"));
            imp.setNumeroCupom(ln.getInt("numerocupom"));
            imp.setEcf(ln.getInt("ecf"));
            imp.setData(ln.getData("data"));
            imp.setIdClientePreferencial(ln.getString("clientepreferencial"));
            imp.setHoraInicio(ln.getTime("horainicio"));
            imp.setHoraTermino(ln.getTime("horatermino"));
            imp.setCancelado("S".equals(ln.getString("cancelado")));
            imp.setSubTotalImpressora(ln.getDouble("subtotalimpressora"));
            imp.setTipoCancelamento(TipoCancelamento.getById(ln.getInt("tipocancelamento")));
            imp.setCpf(ln.getString("cpf"));
            imp.setValorDesconto(ln.getDouble("valordesconto"));
            imp.setValorAcrescimo(ln.getDouble("valoracrescimo"));
            imp.setCanceladoEmVenda("S".equals(ln.getString("canceladoemvenda")));
            imp.setNumeroSerie(ln.getString("numeroserie"));
            imp.setModeloImpressora(ln.getString("modeloimpressora"));
            imp.setNomeCliente(ln.getString("nomecliente"));
            imp.setEnderecoCliente(ln.getString("enderecocliente"));
            imp.setClienteEventual(ln.getString("clienteeventual"));
            imp.setChaveCfe(ln.getString("chavecfe"));
            imp.setChaveNfCe(ln.getString("chavenfce"));
            imp.setXml(ln.getString("xml"));
            imp.setTipoDesconto(TipoDesconto.getById(ln.getInt("tipodesconto")));
            imp.setChaveNfCeContingencia(ln.getString("chavenfcecontingencia"));
            
            /*if (!hasNext()) {
                try {
                    csv.close();
                } catch (IOException ex) {
                    LOG.log(Level.SEVERE, "Erro ao gerar a venda", ex);
                    throw new RuntimeException(ex);
                }
            }*/

            return imp;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported."); //To change body of generated methods, choose Tools | Templates.
        }
        
    }
    
    private class VendaItemIterator implements Iterator<VendaItemIMP> {
        
        private ArquivoCSV2 csv;

        public VendaItemIterator(ArquivoCSV2 csv) {
            this.csv = csv;
        }
        
        @Override
        public boolean hasNext() {
            return this.csv.hasNext();
        }

        @Override
        public VendaItemIMP next() {
            
            LinhaArquivo ln = csv.next();

            VendaItemIMP imp = new VendaItemIMP();

            imp.setId(ln.getString("id"));
            imp.setSequencia(ln.getInt("sequencia"));
            VendaIMP vendaIMP = new VendaIMP();
            vendaIMP.setId(ln.getString("cod_venda"));
            imp.setVenda(vendaIMP);
            imp.setProduto(ln.getString("cod_produto"));
            imp.setDescricaoReduzida(ln.getString("descricaoreduzida"));
            imp.setQuantidade(ln.getDouble("quantidade"));
            imp.setPrecoVenda(ln.getDouble("precovenda"));
            imp.setCancelado("S".equals(Utils.acertarTexto(ln.getString("cancelado"))));
            if (!"".equals(ln.getString("tipocancelamento"))) {
                imp.setTipoCancelamento(TipoCancelamento.getById(ln.getInt("tipocancelamento")));
            }
            imp.setValorDesconto(ln.getDouble("valordesconto"));
            imp.setValorAcrescimo(ln.getDouble("valoracrescimo"));
            imp.setCodigoBarras(ln.getString("codigobarras"));
            imp.setUnidadeMedida(ln.getString("unidademedida"));
            if (!"".equals(ln.getString("tipodesconto"))) {
                imp.setTipoDesconto(TipoDesconto.getById(ln.getInt("tipodesconto")));
            }
            imp.setIcmsCst(ln.getInt("icms_cst"));
            imp.setIcmsAliq(ln.getDouble("icms_aliq"));
            imp.setIcmsReduzido(ln.getDouble("icms_red"));
            imp.setContadorDoc(ln.getInt("contadordoc"));
            
            /*if (!hasNext()) {
                try {
                    csv.close();
                } catch (IOException ex) {
                    LOG.log(Level.SEVERE, "Erro ao gerar a venda", ex);
                    throw new RuntimeException(ex);
                }
            }*/

            return imp;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported."); //To change body of generated methods, choose Tools | Templates.
        }
    }

    @Override
    public List<PautaFiscalIMP> getPautasFiscais(Set<OpcaoFiscal> opcoes) throws Exception {
        List<PautaFiscalIMP> result = new ArrayList<>();
        
        Arquivo produtos = ArquivoFactory.getArquivo(this.arquivo, getOpcoes());  
        
        ProgressBar.setStatus("Carregando Pautas Fiscais");
        
        for (LinhaArquivo linha: produtos) {
            PautaFiscalIMP imp = new PautaFiscalIMP();

            imp.setId(linha.getString("id"));
            imp.setNcm(linha.getString("ncm"));            
            imp.setIva(linha.getDouble("iva"));
            imp.setIvaAjustado(linha.getDouble("ivaajustado"));
            
            imp.setIcmsRecolhidoAntecipadamente(linha.getBoolean("recolhidoantecipado"));
            if (linha.existsColumn("id_aliquotadebito")) {
                imp.setAliquotaDebitoId(linha.getString("id_aliquotadebito"));
            } else {
                imp.setAliquotaDebito(
                        linha.getInt("aliquotadebito_cst"),
                        linha.getDouble("aliquotadebito_aliquota"), 
                        linha.getDouble("aliquotadebito_reduzido")
                );
            }
            
            if (linha.existsColumn("id_aliquotacredito")) {
                imp.setAliquotaCreditoId(linha.getString("id_aliquotacredito"));
            } else {
                imp.setAliquotaCredito(
                        linha.getInt("aliquotacredito_cst"),
                        linha.getDouble("aliquotacredito_aliquota"), 
                        linha.getDouble("aliquotacredito_reduzido")
                );
            }
            
            if (linha.existsColumn("id_aliquotacreditoforaestado")) {
                imp.setAliquotaCreditoForaEstadoId(linha.getString("id_aliquotacreditoforaestado"));
            } else {
                imp.setAliquotaCreditoForaEstado(
                        linha.getInt("aliquotacreditoforaestado_cst"),
                        linha.getDouble("aliquotacreditoforaestado_aliquota"), 
                        linha.getDouble("aliquotacreditoforaestado_reduzido")
                );
            }
            
            if (linha.existsColumn("id_aliquotadebitoforaestado")) {
                imp.setAliquotaDebitoForaEstadoId(linha.getString("id_aliquotadebitoforaestado"));
            } else {
                imp.setAliquotaDebitoForaEstado(
                        linha.getInt("aliquotadebitoforaestado_cst"),
                        linha.getDouble("aliquotadebitoforaestado_aliquota"), 
                        linha.getDouble("aliquotadebitoforaestado_reduzido")
                );
            }
            
            result.add(imp);
            
        }
        
        return result;
    }

    
    
}
