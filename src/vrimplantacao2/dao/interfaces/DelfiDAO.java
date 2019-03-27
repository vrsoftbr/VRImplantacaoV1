package vrimplantacao2.dao.interfaces;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import vrframework.classe.Conexao;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoEstadoCivil;
import vrimplantacao2.vo.importacao.ChequeIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorContatoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

public class DelfiDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "Delphi";
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> vResult = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select 1 id, cnpj, 1 || ' - ' ||razao_social descricao "
                    + "from configuracoes"
            )) {
                while (rst.next()) {
                    vResult.add(new Estabelecimento(rst.getString("id"), rst.getString("descricao")));
                }
            }
        }
        return vResult;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "codigo_aliquota,\n"
                    + "descricao,\n"
                    + "aliquota_ecf,\n"
                    + "percentual_icms\n"
                    + "from aliquotas\n"
                    + "order by codigo_aliquota"
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(rst.getString("codigo_aliquota"), (rst.getString("descricao") + " - " + rst.getString("aliquota_ecf"))));
                }
            }
        }
        
        return result;
    }
    
    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select codigo_fabricante, descricao, inativo\n"
                    + "from fabricantes\n"
                    + "order by codigo_fabricante"
            )) {
                while (rst.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("codigo_fabricante"));
                    imp.setDescricao(rst.getString("descricao"));
                    imp.setSituacaoCadastro("N".equals(rst.getString("inativo")) ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select codigo_linha, descricao\n"
                    + "from linhas\n"
                    + "order by codigo_linha"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setMerc1ID(rst.getString("codigo_linha"));
                    imp.setMerc1Descricao(rst.getString("descricao"));
                    imp.setMerc2ID("1");
                    imp.setMerc2Descricao(imp.getMerc1Descricao());
                    imp.setMerc3ID("1");
                    imp.setMerc3Descricao(imp.getMerc1Descricao());
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select p.codigo_produto, p.codigo_barra, p.codigo_fabricante, p.codigo_linha,\n"
                    + "p.codigo_aliquota, p.descricao, substring(p.unidade from 1 for 2) as unidade, p.estoque, p.preco_custo, p.lucro_percentual,\n"
                    + "p.lucro_real, p.preco_venda, p.inativo, p.data_cad, p.situacao_tributaria,  p.icms_credito,\n"
                    + "p.aliquota, p.peso_liquido, p.peso_bruto, p.pis_cofins, p.ncm, descricao_abreviada,\n"
                    + "p.cst_pis, p.cst_cofins, p.cst_icms_entrada, p.cst_pis_entrada, p.cst_cofins_entrada,\n"
                    + "p.cest, a.descricao desc_aliquota, a.aliquota_ecf, a.percentual_icms, f.descricao desc_fabricante,\n"
                    + "l.descricao desc_linha, p.codigo_aliquota\n"
                    + "from produtos p\n"
                    + "inner join aliquotas a on a.codigo_aliquota = p.codigo_aliquota\n"
                    + "left join fabricantes f on f.codigo_fabricante = p.codigo_fabricante\n"
                    + "left join linhas l on l.codigo_linha = p.codigo_linha\n"
                    + "order by p.codigo_produto"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("codigo_produto"));
                    imp.setEan(rst.getString("codigo_barra"));
                    imp.setSituacaoCadastro("N".equals(rst.getString("inativo")) ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    imp.setDataCadastro(rst.getDate("data_cad"));
                    imp.setDescricaoCompleta(rst.getString("descricao"));
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setIdFamiliaProduto(rst.getString("codigo_fabricante"));
                    imp.setCodMercadologico1(rst.getString("codigo_linha"));
                    imp.setCodMercadologico2("1");
                    imp.setCodMercadologico3("1");
                    imp.setTipoEmbalagem(rst.getString("unidade"));
                    imp.setPesoBruto(rst.getDouble("peso_bruto"));
                    imp.setPesoLiquido(rst.getDouble("peso_liquido"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getInt("cst_pis"));
                    imp.setPiscofinsCstCredito(rst.getInt("cst_pis_entrada"));
                    imp.setMargem(rst.getDouble("lucro_percentual"));
                    imp.setCustoComImposto(rst.getDouble("preco_custo"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setPrecovenda(rst.getDouble("preco_venda"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setIcmsDebitoId(rst.getString("codigo_aliquota"));
                    imp.setIcmsCreditoId(rst.getString("codigo_aliquota"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select codigo_fornecedor, nome, fantasia, inscricao, cnpj, endereco,\n"
                    + "bairro, cidade, uf, cep, fone, fax, email, site, contato, data_cad,\n"
                    + "codigo_cidade, numero\n"
                    + "from fornecedores\n"
                    + "order by codigo_fornecedor"
            )) {
                SimpleDateFormat format = new SimpleDateFormat("ddMMyy");
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("codigo_fornecedor"));
                    imp.setRazao(rst.getString("nome"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setIe_rg(rst.getString("inscricao"));
                    imp.setCnpj_cpf(Utils.formataNumero(rst.getString("cnpj")));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setTel_principal(Utils.formataNumero(rst.getString("fone")));
                    if ((rst.getString("data_cad") != null)
                            && (!rst.getString("data_cad").trim().isEmpty())) {
                        imp.setDatacadastro(format.parse(rst.getString("data_cad")));
                    }

                    if ((rst.getString("contato") != null)
                            && (!rst.getString("contato").trim().isEmpty())) {
                        imp.setObservacao("CONTATO - " + Utils.acertarTexto(rst.getString("contato")));
                    }

                    if (Utils.stringToLong(rst.getString("fax")) > 0) {
                        FornecedorContatoIMP cont = new FornecedorContatoIMP();
                        cont.setImportSistema(getSistema());
                        cont.setImportLoja(getLojaOrigem());
                        cont.setImportId("1");
                        cont.setNome("FAX");
                        cont.setTelefone(Utils.stringLong(rst.getString("fax")));
                    }
                    if ((rst.getString("email") != null)
                            && (!rst.getString("email").trim().isEmpty())) {
                        FornecedorContatoIMP cont = imp.getContatos().make("2");
                        cont.setImportSistema(getSistema());
                        cont.setImportLoja(getLojaOrigem());
                        cont.setImportId("2");
                        cont.setNome("EMAIL");
                        cont.setEmail(rst.getString("email"));
                    }
                    if ((rst.getString("site") != null)
                            && (!rst.getString("site").trim().isEmpty())) {
                        FornecedorContatoIMP cont = imp.getContatos().make("3");
                        cont.setImportSistema(getSistema());
                        cont.setImportLoja(getLojaOrigem());
                        cont.setImportId("3");
                        cont.setNome("SITE");
                        cont.setEmail(rst.getString("site"));
                    }
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select id_produto_fornecedor, codigo_produto, \n"
                    + "codigo_fornecedor, codigo_profor\n"
                    + "from produto_fornecedor"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setIdFornecedor(rst.getString("codigo_fornecedor"));
                    imp.setIdProduto(rst.getString("codigo_produto"));
                    imp.setCodigoExterno(rst.getString("codigo_profor"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select codigo_cliente, nome, fantasia, cnpj_cpf, inscricao,\n"
                    + "numero_ident, orgexp_ident, uf_ident, endereco, bairro, cidade,\n"
                    + "cep, uf, nascimento, fone, fax, celular, contato, naturalidade,\n"
                    + "estado_civil, pai, mae, conjuge, endereco_cobranca, bairro_cobranca,\n"
                    + "cidade_cobranca, cep_cobranca, uf_cobranca, empresa, endereco_empresa,\n"
                    + "bairro_empresa, cidade_empresa, cep_empresa, uf_empresa, funcao_empresa,\n"
                    + "renda, limite_credito, situacao, obs, data_cad, telefone_cobranca,\n"
                    + "email_site, endereco_entrega, bairro_entrega, cep_entrega, uf_entrega,\n"
                    + "referencia_1, referencia_2, codigo_cidade, email, numero\n"
                    + "from clientes\n"
                    + "order by codigo_cliente"
            )) {
                SimpleDateFormat format = new SimpleDateFormat("ddMMyy");
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("codigo_cliente"));
                    imp.setRazao(rst.getString("nome"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj(Utils.formataNumero(rst.getString("cnpj_cpf")));
                    imp.setInscricaoestadual(rst.getString("numero_ident"));
                    imp.setOrgaoemissor(rst.getString("orgexp_ident"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("uf"));
                    
                    if ((rst.getString("cep") != null) &&
                            (!rst.getString("cep").trim().isEmpty()) &&
                            (rst.getString("cep").trim().length() > 8)) {
                        imp.setCep(Utils.formataNumero(rst.getString("cep").trim().substring(0, 8)));
                    } else {
                        imp.setCep(rst.getString("cep"));
                    }
                    
                    imp.setDataNascimento(rst.getDate("nascimento"));
                    imp.setTelefone(Utils.formataNumero(rst.getString("fone")));
                    imp.setFax(Utils.formataNumero(rst.getString("fax")));
                    imp.setCelular(Utils.formataNumero(rst.getString("celular")));
                    imp.setEmail(Utils.formataNumero(rst.getString("email")));

                    if ((rst.getString("data_cad") != null)
                            && (!rst.getString("data_cad").trim().isEmpty())) {
                        imp.setDataCadastro(format.parse(rst.getString("data_cad")));
                    }
                    if ((rst.getString("estado_civil") != null)
                            && (!rst.getString("estado_civil").trim().isEmpty())) {
                        switch (rst.getString("estado_civil")) {
                            case "S":
                                imp.setEstadoCivil(TipoEstadoCivil.SOLTEIRO);
                                break;
                            case "C":
                                imp.setEstadoCivil(TipoEstadoCivil.CASADO);
                                break;
                            case "V":
                                imp.setEstadoCivil(TipoEstadoCivil.VIUVO);
                                break;
                            case "D":
                                imp.setEstadoCivil(TipoEstadoCivil.DIVORCIADO);
                                break;
                            default:
                                imp.setEstadoCivil(TipoEstadoCivil.NAO_INFORMADO);
                                break;
                        }
                    } else {
                        imp.setEstadoCivil(TipoEstadoCivil.NAO_INFORMADO);
                    }
                    imp.setNomePai(rst.getString("pai"));
                    imp.setNomeMae(rst.getString("mae"));
                    imp.setNomeConjuge(rst.getString("conjuge"));
                    imp.setEmpresa(rst.getString("empresa"));
                    imp.setEmpresaEndereco(rst.getString("endereco_empresa"));
                    imp.setEmpresaBairro(rst.getString("bairro_empresa"));
                    imp.setEmpresaMunicipio(rst.getString("cidade_empresa"));
                    imp.setEmpresaCep(rst.getString("cep_empresa"));
                    imp.setEmpresaUf(rst.getString("uf_empresa"));
                    imp.setCargo(rst.getString("funcao_empresa"));
                    imp.setSalario(rst.getDouble("renda"));
                    imp.setValorLimite(rst.getDouble("limite_credito"));
                    imp.setAtivo("A".equals(rst.getString("situacao")));
                    imp.setObservacao(rst.getString("obs"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> vResult = new ArrayList<>();
        String observacao;
        java.sql.Date dataPagamento;
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select r.codigo_receber, r.codigo_cliente, r.data_emissao, r.numero_venda, r.forma,\n"
                    + "r.duplicata, r.data_vencimento, r.valor, r.valor_pag, (r.valor - coalesce(r.valor_pag, 0)) valor_conta,\n"
                    + "r.obs, c.nome, c.cnpj_cpf\n"
                    + "from receber r\n"
                    + "left join clientes c on c.codigo_cliente = r.codigo_cliente\n"
                    + "where r.status = 'A'\n"
                    + "and r.forma = 'P'"
            )) {
                dataPagamento = new Date(new java.util.Date().getTime());
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rst.getString("codigo_receber"));
                    imp.setDataEmissao(rst.getDate("data_emissao"));
                    imp.setDataVencimento(rst.getDate("data_vencimento"));
                    imp.setNumeroCupom(Utils.formataNumero(rst.getString("numero_venda")));
                    imp.setValor(rst.getDouble("valor_conta"));
                    imp.setIdCliente(Utils.formataNumero(rst.getString("codigo_cliente")));
                    imp.setCnpjCliente(Utils.formataNumero(rst.getString("cnpj_cpf")));
                    observacao = (rst.getString("duplicata") == null ? "" : "DUPLICATA - " + rst.getString("duplicata") + " ");
                    
                    if ("NC".equals(rst.getString("forma"))) {
                        observacao = observacao + "NOTA CREDITO VALOR " + " " + rst.getString("valor_conta");
                    }
                    imp.setObservacao(observacao + (rst.getString("obs") == null ? "" : rst.getString("obs")));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<ChequeIMP> getCheques() throws Exception {
        List<ChequeIMP> vResult = new ArrayList<>();
        String observacao;
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select ch.codigo_receber, ch.codigo_cliente, ch.data_emissao, ch.numero_venda, forma,\n"
                    + "ch.duplicata, ch.data_vencimento, ch.valor, ch.valor_pag, (ch.valor - coalesce(ch.valor_pag, 0)) valor_conta,\n"
                    + "ch.obs, ch.cheque, ch.banco, ch.agencia, c.nome, c.cnpj_cpf, c.inscricao, c.inscricao_municipal, c.numero_ident, c.fone, ch.conta\n"
                    + "from receber ch\n"
                    + "inner join clientes c on c.codigo_cliente = ch.codigo_cliente\n"
                    + "where ch.forma =  'CH'\n"
                    + "and ch.status = 'A'"
            )) {
                while (rst.next()) {
                    ChequeIMP imp = new ChequeIMP();
                    imp.setId(rst.getString("codigo_receber"));
                    imp.setDate(rst.getDate("data_emissao"));
                    imp.setValor(rst.getDouble("valor_conta"));
                    imp.setCpf(rst.getString("cnpj_cpf"));
                    imp.setRg(rst.getString("numero_ident"));
                    imp.setNome(rst.getString("nome"));
                    imp.setTelefone(rst.getString("fone"));
                    observacao = (rst.getString("duplicata") == null ? "" : "DUPLICATA - " + rst.getString("duplicata") + " ");
                    imp.setObservacao(observacao + (rst.getString("obs") == null ? "" : rst.getString("obs")));
                    imp.setNumeroCheque(rst.getString("cheque"));
                    imp.setBanco(rst.getInt("banco"));
                    imp.setAgencia(rst.getString("agencia"));
                    imp.setConta(rst.getString("conta"));
                    imp.setCmc7("0");
                    imp.setAlinea(0);
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }
    
    private List<ProdutoIMP> getProdutosNaoImportados() throws Exception {
        List<ProdutoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select p.codigo_produto, p.codigo_barra, p.codigo_fabricante, p.codigo_linha,\n"
                    + "p.codigo_aliquota, p.descricao, p.unidade, p.estoque, p.preco_custo, p.lucro_percentual,\n"
                    + "p.lucro_real, p.preco_venda, p.inativo, p.data_cad, p.situacao_tributaria,  p.icms_credito,\n"
                    + "p.aliquota, p.peso_liquido, p.peso_bruto, p.pis_cofins, p.ncm, descricao_abreviada,\n"
                    + "p.cst_pis, p.cst_cofins, p.cst_icms_entrada, p.cst_pis_entrada, p.cst_cofins_entrada,\n"
                    + "p.cest, a.descricao desc_aliquota, a.aliquota_ecf, a.percentual_icms, f.descricao desc_fabricante,\n"
                    + "l.descricao desc_linha\n"
                    + "from produtos p\n"
                    + "inner join aliquotas a on a.codigo_aliquota = p.codigo_aliquota\n"
                    + "left join fabricantes f on f.codigo_fabricante = p.codigo_fabricante\n"
                    + "left join linhas l on l.codigo_linha = p.codigo_linha\n"
                    + "order by p.codigo_produto"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("codigo_produto"));
                    imp.setEan(rst.getString("codigo_barra"));
                    imp.setSituacaoCadastro("N".equals(rst.getString("inativo")) ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    imp.setDataCadastro(rst.getDate("data_cad"));
                    imp.setDescricaoCompleta(rst.getString("descricao"));
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setIdFamiliaProduto(rst.getString("codigo_fabricante"));
                    imp.setCodMercadologico1(rst.getString("codigo_linha"));
                    imp.setCodMercadologico2("1");
                    imp.setCodMercadologico3("1");
                    imp.setTipoEmbalagem(rst.getString("unidade"));
                    imp.setPesoBruto(rst.getDouble("peso_bruto"));
                    imp.setPesoLiquido(rst.getDouble("peso_liquido"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getInt("cst_pis"));
                    imp.setPiscofinsCstCredito(rst.getInt("cst_pis_entrada"));
                    imp.setMargem(rst.getDouble("lucro_percentual"));
                    imp.setCustoComImposto(rst.getDouble("preco_custo"));
                    imp.setPrecovenda(rst.getDouble("preco_venda"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setIcmsCstSaida(rst.getInt("situacao_tributaria"));
                    imp.setIcmsAliq(rst.getDouble("percentual_icms"));
                    imp.setIcmsCstEntrada(rst.getInt("cst_icms_entrada"));
                    vResult.add(imp);                    
                }
            }
        }
        return vResult;
    }
    
    public void importarProdutosNaoImportados() throws Exception {
        List<String> list = new ArrayList<>();
        List<ProdutoIMP> vProduto = new ArrayList<>();
        vProduto = getProdutos();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select impid "
                    + "from implantacao.codant_produto "
                    + "where codigoatual is null "
                    + "and imploja = '" + getLojaOrigem() + "'"
            )) {
                while (rst.next()) {
                    list.add(rst.getString("impid"));
                }
                
                /* depois que preencer o list */
                for (ProdutoIMP i_result : vProduto) {
                    for (String list1 : list) {                        
                        if (i_result.getImportId().equals(list1)) {
                            System.out.println(list1);
                        }
                    }
                }
            }
        }
    }
}
