package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.OpcaoFiscal;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.PautaFiscalIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Importacao
 */
public class WeberDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "Weber";
    }
    
    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                new OpcaoProduto[] {
                    OpcaoProduto.MERCADOLOGICO,
                    OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                    OpcaoProduto.MERCADOLOGICO_PRODUTO,
                    OpcaoProduto.FAMILIA,
                    OpcaoProduto.FAMILIA_PRODUTO,
                    OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                    OpcaoProduto.PRODUTOS,
                    OpcaoProduto.EAN,
                    OpcaoProduto.EAN_EM_BRANCO,
                    OpcaoProduto.DATA_CADASTRO,
                    OpcaoProduto.TIPO_EMBALAGEM_EAN,
                    OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                    OpcaoProduto.PESAVEL,
                    OpcaoProduto.VALIDADE,
                    OpcaoProduto.DESC_COMPLETA,
                    OpcaoProduto.DESC_GONDOLA,
                    OpcaoProduto.DESC_REDUZIDA,
                    OpcaoProduto.ESTOQUE_MAXIMO,
                    OpcaoProduto.ESTOQUE_MINIMO,
                    OpcaoProduto.PRECO,
                    OpcaoProduto.CUSTO,
                    OpcaoProduto.ESTOQUE,
                    OpcaoProduto.ATIVO,
                    OpcaoProduto.NCM,
                    OpcaoProduto.CEST,
                    OpcaoProduto.PIS_COFINS,
                    OpcaoProduto.NATUREZA_RECEITA,
                    OpcaoProduto.ICMS,
                    OpcaoProduto.PAUTA_FISCAL,
                    OpcaoProduto.PAUTA_FISCAL_PRODUTO,
                    OpcaoProduto.MARGEM
                }
        ));
    }
    
    public List<Estabelecimento> getLojaCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try(Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "    id_loja,\n" +
                    "    fantasia\n" +
                    "from\n" +
                    "    loja"
            )) {
                while(rs.next()) {
                    result.add(new Estabelecimento(rs.getString("id_loja"), rs.getString("fantasia")));
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try(Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "    p.id_produto as importid,\n" +
                    "    p.data_cadastro as datacadastro,\n" +
                    "    p.data_alteracao as dataalteracao,\n" +
                    "    e.est_max estmaximo,\n" +
                    "    e.est_min estminimo,\n" +
                    "    e.est_atual estoque,\n" +
                    "    p.balanca,\n" +
                    "    replace(p.situacao, '*', 1) situacao,\n" +
                    "    p.unm_emb_qtd as qtdembalagem,\n" +
                    "    p.unm as tipoembalagem,\n" +
                    "    p.dias_validade as validade,\n" +
                    "    nome_produto as descricaocompleta,\n" +
                    "    nome_reduzido as descricaoreduzida,\n" +
                    "    nome_reduzido as descricaogondola,\n" +
                    "    peso_bruto as pesobruto,\n" +
                    "    peso_liquido as pesoliquido,\n" +
                    "    perc_margem as margem,\n" +
                    "    preco_custo as custosemimposto,\n" +
                    "    preco_venda as precovenda,\n" +
                    "    ncm,\n" +
                    "    cest,\n" +
                    "    cofinse_aliq as piscofinscstdebito,\n" +
                    "    pis_cofins_entrada as piscofinscstcredito,\n" +
                    "    pis_nat_rec as piscofinsnaturezareceita,\n" +
                    "    icm_cst as icms_cst_credito,\n" +
                    "    icm_aliq as icms_credito,\n" +
                    "    icm_pbc icms_reducao_credito,\n" +
                    "    icm.tabicm_st icms_cst_debito,\n" +
                    "    icm.tabicm_aliq icms_debito,\n" +
                    "    icm.tabicm_pbc icms_reducao_debito,\n" +
                    "    tipo_prod as tipoproduto,\n" +
                    "    p.icm_mva\n" +
                    "from\n" +
                    "    est_produtos p\n" +
                    "left join est_atual e on p.id_produto = e.id_produto and\n" +
                    "    e.id_loja = " + getLojaOrigem() + "\n" +
                    "left join tab_icm icm on p.tabicm = icm.id_tabicm\n" +
                    "order by\n" +
                    "    p.id_produto"
            )) {
               while(rs.next()) {
                   ProdutoIMP imp = new ProdutoIMP();
                   imp.setImportLoja(getLojaOrigem());
                   imp.setImportSistema(getSistema());
                   imp.setImportId(rs.getString("importid"));
                   imp.setEan(imp.getImportId());
                   imp.setDescricaoCompleta(rs.getString("descricaocompleta"));
                   if(rs.getString("descricaoreduzida") == null && "".equals(rs.getString("descricaoreduzida"))) {
                       imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                   } else {
                       imp.setDescricaoReduzida(rs.getString("descricaoreduzida"));
                   }
                   imp.setDescricaoGondola(rs.getString("descricaocompleta"));
                   imp.setEstoque(rs.getDouble("estoque"));
                   imp.setEstoqueMaximo(rs.getDouble("estmaximo"));
                   imp.setEstoqueMinimo(rs.getDouble("estminimo"));
                   imp.setDataCadastro(rs.getDate("datacadastro"));
                   imp.setDataAlteracao(rs.getDate("dataalteracao"));
                   imp.seteBalanca("S".equals(rs.getString("balanca")));
                   imp.setValidade(rs.getInt("validade"));
                   imp.setSituacaoCadastro(rs.getInt("situacao") == 1 ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                   imp.setQtdEmbalagem(rs.getInt("qtdembalagem"));
                   imp.setTipoEmbalagem(rs.getString("tipoembalagem"));
                   imp.setPesoBruto(rs.getDouble("pesobruto"));
                   imp.setPesoLiquido(rs.getDouble("pesoliquido"));
                   imp.setMargem(rs.getDouble("margem"));
                   imp.setCustoComImposto(rs.getDouble("custosemimposto"));
                   imp.setCustoSemImposto(rs.getDouble("custosemimposto"));
                   imp.setPrecovenda(rs.getDouble("precovenda"));
                   imp.setNcm(rs.getString("ncm"));
                   imp.setCest(rs.getString("cest"));
                   imp.setPiscofinsCstCredito(rs.getString("piscofinscstcredito"));
                   imp.setPiscofinsCstDebito(rs.getString("piscofinscstdebito"));
                   imp.setPiscofinsNaturezaReceita(rs.getString("piscofinsnaturezareceita"));
                   
                   //Aliquota de saída
                   imp.setIcmsAliqSaida(rs.getDouble("icms_debito"));
                   imp.setIcmsCstSaida(rs.getInt("icms_cst_debito"));
                   
                   double reducao = rs.getDouble("icms_reducao_debito");
                   imp.setIcmsReducaoSaida(reducao == 100 ? 0 : reducao);
                   
                   //Alíquota de saída fora estado
                   imp.setIcmsAliqSaidaForaEstado(rs.getDouble("icms_debito"));
                   imp.setIcmsCstSaidaForaEstado(rs.getInt("icms_cst_debito"));
                   imp.setIcmsReducaoSaidaForaEstado(reducao == 100 ? 0 : reducao);
                   
                   //Aliquota de entrada
                   imp.setIcmsAliqEntrada(rs.getDouble("icms_credito"));
                   imp.setIcmsCstEntrada(Integer.parseInt(Utils.formataNumero(rs.getString("icms_cst_credito"))));
                   
                   reducao = rs.getDouble("icms_reducao_credito");
                   imp.setIcmsReducaoEntrada(reducao == 100 ? 0 : reducao);
                   
                   //Aliquota de entrada fora estado
                   imp.setIcmsAliqEntradaForaEstado(rs.getDouble("icms_credito"));
                   imp.setIcmsCstEntradaForaEstado(Integer.parseInt(Utils.formataNumero(rs.getString("icms_cst_credito"))));
                   imp.setIcmsReducaoEntradaForaEstado(reducao == 100 ? 0 : reducao);
                   
                   //Pauta Fiscal
                   imp.setPautaFiscalId(imp.getImportId());
                   
                   result.add(imp);
               } 
            }
        }
        return result;
    }
    
    @Override
    public List<PautaFiscalIMP> getPautasFiscais(Set<OpcaoFiscal> opcoes) throws Exception {
        List<PautaFiscalIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    id_produto,\n" +
                    "    nome_produto,\n" +
                    "    ncm,\n" +
                    "    tabicm aliquota_debito_id,\n" +
                    "    icm.tabicm_st cst_debito,\n" +
                    "    icm.tabicm_aliq icms_aliquota_debito,\n" +
                    "    icm_aliq aliquota_credito,\n" +
                    "    icm_cst cst_credito,\n" +
                    "    icm_pbc aliquota_reducao_credito,\n" +
                    "    icm_stperc aliquota_final_credito,\n" +
                    "    icm_mva\n" +
                    "from\n" +
                    "    est_produtos p\n" +
                    "join tab_icm icm on p.tabicm = icm.id_tabicm\n" +
                    "where\n" +
                    "    icm_mva > 0\n" +
                    "order by\n" +
                    "    2"
            )) {
                while (rst.next()) {
                    PautaFiscalIMP imp = new PautaFiscalIMP();
                    
                    imp.setId(rst.getString("id_produto"));
                    imp.setIva(rst.getDouble("icm_mva"));
                    imp.setIvaAjustado(imp.getIva());
                    imp.setNcm(rst.getString("ncm"));
                    imp.setAliquotaDebito(rst.getInt("cst_debito"), rst.getDouble("icms_aliquota_debito"), 0.0);
                    imp.setAliquotaDebitoForaEstado(rst.getInt("cst_debito"), rst.getDouble("icms_aliquota_debito"), 0.0);
                    double reducao = 0;
                    if(rst.getDouble("aliquota_reducao_credito") == 100) {
                        reducao = 0;
                    } else {
                        reducao = rst.getDouble("aliquota_reducao_credito");
                    }
                    imp.setAliquotaCredito(0, rst.getDouble("aliquota_credito"), reducao);
                    imp.setAliquotaCreditoForaEstado(0, rst.getDouble("aliquota_credito"), reducao);
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        try(Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "    c.id_cliente as id,\n" +
                    "    c.cnpj_cpf as cnpj,\n" +
                    "    c.ie_rg as inscricaoestadual,\n" +
                    "    c.nome_razao as razao,\n" +
                    "    c.fantasia,\n" +
                    "    c.bloqueado,\n" +
                    "    c.endereco,\n" +
                    "    c.numero,\n" +
                    "    c.endcomplmto as complemento,\n" +
                    "    c.bairro,\n" +
                    "    c.cod_cidade as municipioibge,\n" +
                    "    c.cidade as municipio,\n" +
                    "    c.uf,\n" +
                    "    c.cep,\n" +
                    "    c.data_nascimento as datanascimento,\n" +
                    "    c.data_cadastro as datacadastro,\n" +
                    "    c.sexo as tiposexo,\n" +
                    "    c2.empr_atual as empresa,\n" +
                    "    c2.empr_endereco as empresaendereco,\n" +
                    "    c2.empr_bairro as empresabairro,\n" +
                    "    c2.empr_cidade as empresamunicipio,\n" +
                    "    c2.empr_fone as empresatelefone,\n" +
                    "    c2.empr_data_admissao as dataadmissao,\n" +
                    "    c2.empr_funcao as cargo,\n" +
                    "    c2.empr_rendimentos as salario,\n" +
                    "    c.vlr_limite as valorlimite,\n" +
                    "    c2.conj_nome as nomeconjuge,\n" +
                    "    c2.nome_pai as nomepai,\n" +
                    "    c2.nome_mae as nomemae,\n" +
                    "    c.obs_memo as observacao,\n" +
                    "    c.dia_vcto as diavencimento,\n" +
                    "    c.cred_rotativo as permitecreditorotativo,\n" +
                    "    c.fone1 as telefone,\n" +
                    "    c.fone2 as celular,\n" +
                    "    c.email as email,\n" +
                    "    c.vlr_limite as limitecompra,\n" +
                    "    c.inscr_municipal as inscricaomunicipal,\n" +
                    "    c.situacao indicadorie\n" +
                    "from\n" +
                    "    clie_dados c\n" +
                    "    left join clie_compl1 as c2\n" +
                    "        on c.id_cliente = c2.id_cliente\n" +
                    "where\n" +
                    "    upper(tipo_forn) = 'S'\n" +
                    "order by 1"
            )) {
                while(rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setCnpj_cpf(Utils.formataNumero(rs.getString("cnpj")));
                    imp.setIe_rg(rs.getString("inscricaoestadual"));
                    //imp.setAtivo(rs.getInt("situacao") == 1);
                    imp.setAtivo("N".equals(rs.getString("bloqueado")));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("municipio"));
                    imp.setIbge_municipio(rs.getInt("municipioibge"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));
                    imp.setDatacadastro(rs.getDate("datacadastro"));
                    imp.setCondicaoPagamento(rs.getInt("diavencimento"));
                    imp.setTel_principal(rs.getString("telefone"));
                    if(rs.getString("celular") != null && !"".equals(rs.getString("celular"))) {
                        imp.addContato("1", "CELULAR", null, rs.getString("celular"), TipoContato.NFE, null);
                    }
                    if(rs.getString("email") != null && !"".equals(rs.getString("email"))) {
                        imp.addContato("2", "EMAIL", null, null, TipoContato.NFE, rs.getString("email"));
                    }
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        try(Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "    c.id_cliente as id,\n" +
                    "    c.cnpj_cpf as cnpj,\n" +
                    "    c.ie_rg as inscricaoestadual,\n" +
                    "    c.nome_razao as razao,\n" +
                    "    c.fantasia,\n" +
                    "    c.bloqueado,\n" +
                    "    c.endereco,\n" +
                    "    c.numero,\n" +
                    "    c.endcomplmto as complemento,\n" +
                    "    c.bairro,\n" +
                    "    c.cod_cidade as municipioibge,\n" +
                    "    c.cidade as municipio,\n" +
                    "    c.uf,\n" +
                    "    c.cep,\n" +
                    "    c.data_nascimento as datanascimento,\n" +
                    "    c.data_cadastro as datacadastro,\n" +
                    "    c.sexo as tiposexo,\n" +
                    "    c2.empr_atual as empresa,\n" +
                    "    c2.empr_endereco as empresaendereco,\n" +
                    "    c2.empr_bairro as empresabairro,\n" +
                    "    c2.empr_cidade as empresamunicipio,\n" +
                    "    c2.empr_fone as empresatelefone,\n" +
                    "    c2.empr_data_admissao as dataadmissao,\n" +
                    "    c2.empr_funcao as cargo,\n" +
                    "    c2.empr_rendimentos as salario,\n" +
                    "    c.vlr_limite as valorlimite,\n" +
                    "    c2.conj_nome as nomeconjuge,\n" +
                    "    c2.nome_pai as nomepai,\n" +
                    "    c2.nome_mae as nomemae,\n" +
                    "    c.obs_memo as observacao,\n" +
                    "    c.dia_vcto as diavencimento,\n" +
                    "    c.cred_rotativo as permitecreditorotativo,\n" +
                    "    c.fone1 as telefone,\n" +
                    "    c.fone2 as celular,\n" +
                    "    c.email as email,\n" +
                    "    c.vlr_limite as limitecompra,\n" +
                    "    c.inscr_municipal as inscricaomunicipal,\n" +
                    "    c.situacao as tipoindicadorie\n" +
                    "from\n" +
                    "    clie_dados c\n" +
                    "    left join clie_compl1 as c2\n" +
                    "        on c.id_cliente = c2.id_cliente\n" +
                    "where\n" +
                    "    upper(tipo_clie) = 'S'\n" +
                    "order by 1"
            )) {
                while(rs.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rs.getString("id"));
                    imp.setCnpj(Utils.formataNumero(rs.getString("cnpj")));
                    imp.setInscricaoestadual(rs.getString("inscricaoestadual"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setAtivo("N".equals(rs.getString("bloqueado")));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("municipio"));
                    imp.setMunicipioIBGE(rs.getInt("municipioibge"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));
                    imp.setDataNascimento(rs.getDate("datanascimento"));
                    imp.setDataCadastro(rs.getDate("datacadastro"));
                    imp.setValorLimite(rs.getDouble("valorlimite"));
                    imp.setTelefone(rs.getString("telefone"));
                    imp.setCelular(rs.getString("celular"));
                    imp.setEmail(rs.getString("email"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();
        try(Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "    c.id_cliente as idFornecedor,\n" +
                    "    cf.cod_prod as idProduto,\n" +
                    "    fator as qtdEmbalagem,\n" +
                    "    cf.id_cod codigoexterno\n" +
                    "from\n" +
                    "    codigo_fornec cf\n" +
                    "        left join clie_dados c\n" +
                    "            on cf.id_cnpj = replace(replace(replace(c.cnpj_cpf,'.',''), '/', ''), '-', '')\n" +
                    "where\n" +
                    "    cf.cod_prod is not null\n" +
                    "order by\n" +
                    "    idFornecedor"
            )) {
                while(rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdFornecedor(rs.getString("idFornecedor"));
                    imp.setIdProduto(rs.getString("idProduto"));
                    imp.setQtdEmbalagem(rs.getDouble("qtdEmbalagem"));
                    imp.setCodigoExterno(rs.getString("codigoexterno"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();
        try(Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "    id_lcto as id,\n" +
                    "    dt_lcto as dataemissao,\n" +
                    "    documento as numerocupom,\n" +
                    "    term as ecf,\n" +
                    "    vlr_doc valorliquido,\n" +
                    "    vlr_doc - coalesce(tot_pago, 0) as valor,\n" +
                    "    vlr_juros as juros,\n" +
                    "    cr.origem_lcto || ' ' || cr.obs observacao,\n" +
                    "    cliente as idcliente,\n" +
                    "    dt_vcto as vencimento,\n" +
                    "    c.cnpj_cpf as cnpjcliente,\n" +
                    "    c.id_cliente\n" +
                    "from\n" +
                    "    cr_nota cr\n" +
                    "    left join clie_dados c\n" +
                    "        on c.id_cliente = cr.cliente\n" +
                    "where\n" +
                    "    cr.quitado = 'F' and\n" +
                    "    loja = " + getLojaOrigem() + "\n" +
                    "order by dt_vcto"
            )) {
                while(rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rs.getString("id"));
                    imp.setDataEmissao(rs.getDate("dataemissao"));
                    imp.setNumeroCupom(Utils.formataNumero(rs.getString("numerocupom")));
                    imp.setEcf(rs.getString("ecf"));
                    imp.setValor(rs.getDouble("valor"));
                    imp.setObservacao(rs.getString("observacao"));
                    imp.setIdCliente(rs.getString("idcliente"));
                    imp.setDataVencimento(rs.getDate("vencimento"));
                    imp.setJuros(rs.getDouble("juros"));
                    imp.setCnpjCliente(Utils.formataNumero(rs.getString("cnpjcliente")));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ContaPagarIMP> getContasPagar() throws Exception {
        List<ContaPagarIMP> result = new ArrayList<>();
        try(Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "    id_lcto as id,\n" +
                    "    cedente as idFornecedor,\n" +
                    "    documento as numeroDocumento,\n" +
                    "    dt_emissao as dataEmissao,\n" +
                    "    dt_vcto vencimento,\n" +
                    "    dt_lcto as dataEntrada,\n" +
                    "    vlr_doc as valor,\n" +
                    "    vlr_juros juros,\n" +
                    "    obs_memo as observacao\n" +
                    "from\n" +
                    "    cp_nota\n" +
                    "where\n" +
                    "    quitado = 'F' and\n" +
                    "    cedente is not null and\n" +
                    "    cedente in (select id_cliente from clie_dados where upper(tipo_forn) = 'S') and\n" +
                    "    loja = " + getLojaOrigem() + "\n" +
                    "order by\n" +
                    "    dt_lcto"
            )) {
                while(rs.next()) {
                    ContaPagarIMP imp = new ContaPagarIMP();
                    imp.setId(rs.getString("id"));
                    imp.setIdFornecedor(rs.getString("idFornecedor"));
                    imp.setNumeroDocumento(Utils.formataNumero(rs.getString("numeroDocumento")));
                    imp.setDataEmissao(rs.getDate("dataEmissao"));
                    imp.setDataEntrada(rs.getDate("dataEntrada"));
                    imp.addVencimento(rs.getDate("vencimento"), rs.getDouble("valor"));
                    imp.setObservacao(rs.getString("observacao"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        try(Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "    id_tabicm id,\n" +
                    "    tabicm_descricao descricao\n" +
                    "from\n" +
                    "    tab_icm"
            )) {
                while(rs.next()) {
                    result.add(new MapaTributoIMP(rs.getString("id"), rs.getString("descricao")));
                }
            }
        }
        return result;
    }
}
