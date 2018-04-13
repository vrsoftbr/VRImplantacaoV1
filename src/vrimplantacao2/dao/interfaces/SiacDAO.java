package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import vrimplantacao.classe.ConexaoOracle;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.cadastro.mercadologico.MercadologicoNivelIMP;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.OfertaIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Leandro
 */
public class SiacDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "Siac";
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        
        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select empresa_id, fantasia from empresas order by empresa_id"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("empresa_id"), rst.getString("fantasia")));
                }
            }
        }
        
        return result;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select grupo_icms_id, descricao from grupo_icms order by 1"
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(rst.getString("grupo_icms_id"), rst.getString("descricao")));
                }
            }
        }
        
        return result;
    }

    @Override
    public List<MercadologicoNivelIMP> getMercadologicoPorNivel() throws Exception {
        Map<String, MercadologicoNivelIMP> result = new LinkedHashMap<>();
        
        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select grupo_id, descricao from grupos order by grupo_id"
            )) {
                while (rst.next()) {
                    String[] ids = rst.getString("grupo_id").split("\\.");
                    
                    if (ids.length == 1) {
                        if (!result.containsKey(ids[0])) {
                            MercadologicoNivelIMP imp = new MercadologicoNivelIMP(ids[0], rst.getString("descricao"));
                            result.put(imp.getId(), imp);
                        }
                    } else if (ids.length == 2) {
                        MercadologicoNivelIMP pai = result.get(ids[0]);
                        pai.addFilho(ids[1], rst.getString("descricao"));
                    }
                }
            }
        }
        
        return new ArrayList<>(result.values());
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select familia_id, descricao from produtos_familias order by familia_id"
            )) {
                while (rst.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("familia_id"));
                    imp.setDescricao(rst.getString("descricao"));
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "  p.produto_id id,\n" +
                    "  p.dt_cadastro datacadastro,\n" +
                    "  ean.ean,\n" +
                    "  p.emb_fracionada qtd_cotacao,\n" +
                    "  ean.qtd_embalagem,\n" +
                    "  p.unidade,\n" +
                    "  case when p.permite_venda_fracionada = 'S' then 1 else 0 end e_balanca,\n" +
                    "  case when p.pesavel = 'S' then 1 else 0 end pesavel,\n" +
                    "  p.nome_generico descricaocompleta,\n" +
                    "  p.nome_fracionado descricaoreduzida,\n" +
                    "  p.validade,\n" +
                    "  coalesce(p.grupo_id, '') grupo_id,\n" +
                    "  p.familia_id,\n" +
                    "  p.peso_unidade pesobruto,\n" +
                    "  p.peso_unidade_liquido pesoliquido,\n" +
                    "  est.estoque_atual,\n" +
                    "  est.estoque_minimo,\n" +
                    "  coalesce(p.perc_lucro, 0) margem,\n" +
                    "  p.custo_compra custosemimposto,\n" +
                    "  p.custo_venda preco,\n" +
                    "  case p.ativo when 'S' then 1 else 0 end situacaocadastro,\n" +
                    "  p.codigo_fiscal ncm,\n" +
                    "  pe.codigo_cest cest,\n" +
                    "  p.codigo_natureza_prod_pis pis_natureza_rec,\n" +
                    "  pe.grupo_icms_id id_icms,\n" +
                    "  p.grupo_pis_id,\n" +
                    "  p.codigo_fabrica id_fabricante,\n" +
                    "  pis_e.cst_pis piscofins_entrada,\n" +
                    "  pis_s.cst_pis piscofins_saida,\n" +
                    "  case when p.bloquear_venda = 'N' then 1 else 0 end vendapdv,\n" +
                    "  case when p.exibir_sugestao_compras = 'S' then 1 else 0 end sugestaocotacao,\n" +
                    "  case when p.descontinuado = 'S' then 1 else 0 end descontinuado\n" +
                    "from\n" +
                    "  produtos p\n" +
                    "  join empresas emp on emp.empresa_id = '00.096.427/0001-35'\n" +
                    "  join produtos_empresas pe on\n" +
                    "       pe.produto_id = p.produto_id and\n" +
                    "       pe.empresa_id = emp.empresa_id\n" +
                    "  left join(\n" +
                    "          select\n" +
                    "            p.produto_id,\n" +
                    "            p.codigo_barra ean,\n" +
                    "            p.fator_mutiplicacao qtd_embalagem\n" +
                    "          from\n" +
                    "            produtos p\n" +
                    "          where\n" +
                    "            not nullif(trim(p.codigo_barra),'') is null\n" +
                    "          union  \n" +
                    "          select\n" +
                    "            ean.produto_id,\n" +
                    "            ean.codigo_barra,\n" +
                    "            1 qtd_embalagem\n" +
                    "          from\n" +
                    "            codigo_barras ean\n" +
                    "  ) ean on p.produto_id = ean.produto_id\n" +
                    "  join estoques est on\n" +
                    "       est.produto_id = p.produto_id and\n" +
                    "       est.empresa_id = emp.empresa_id\n" +
                    "  left join new_grupo_piscofins pis on\n" +
                    "       pis.grupo_piscofins_id = pe.new_grupo_pis_cofins_id\n" +
                    "  left join new_itens_grupo_piscofins pis_e on\n" +
                    "       pis_e.grupo_pis_id = pis.grupo_piscofins_id and\n" +
                    "       pis_e.movimento = 'E'\n" +
                    "  left join new_itens_grupo_piscofins pis_s on\n" +
                    "       pis_s.grupo_pis_id = pis.grupo_piscofins_id and\n" +
                    "       pis_s.movimento = 'S'\n" +
                    "order by\n" +
                    "      1"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setEan(rst.getString("ean"));
                    imp.setQtdEmbalagemCotacao(rst.getInt("qtd_cotacao"));
                    imp.setQtdEmbalagem(rst.getInt("qtd_embalagem"));
                    
                    if (rst.getBoolean("e_balanca")) {
                        imp.seteBalanca(true);
                        if (rst.getBoolean("pesavel")) {
                            imp.setTipoEmbalagem("KG");
                        } else {
                            imp.setTipoEmbalagem("UN");
                        }
                    } else {
                        imp.seteBalanca(false);
                        imp.setTipoEmbalagem(rst.getString("unidade"));
                    }
                    
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoGondola(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setValidade(rst.getInt("validade"));
                    
                    String[] ids = rst.getString("grupo_id").split("\\.");
                    if (ids.length > 0) {
                        imp.setCodMercadologico1(ids[0]);
                        if (ids.length > 1) {
                            imp.setCodMercadologico2(ids[1]);
                        }
                    }
                    
                    imp.setIdFamiliaProduto(rst.getString("familia_id"));
                    imp.setPesoBruto(rst.getDouble("pesobruto"));
                    imp.setPesoLiquido(rst.getDouble("pesoliquido"));
                    imp.setEstoque(rst.getDouble("estoque_atual"));
                    imp.setEstoqueMinimo(rst.getDouble("estoque_minimo"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    imp.setCustoComImposto(rst.getDouble("custosemimposto"));
                    imp.setPrecovenda(rst.getDouble("preco"));
                    imp.setSituacaoCadastro(rst.getInt("situacaocadastro"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstCredito(rst.getString("piscofins_entrada"));
                    imp.setPiscofinsCstDebito(rst.getString("piscofins_saida"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("pis_natureza_rec"));
                    imp.setIcmsDebitoId(rst.getString("id_icms"));
                    imp.setIcmsCreditoId(rst.getString("id_icms"));
                    imp.setFornecedorFabricante(rst.getString("id_fabricante"));
                    imp.setVendaPdv(rst.getBoolean("vendapdv"));
                    imp.setSugestaoCotacao(rst.getBoolean("sugestaocotacao"));
                    imp.setSugestaoPedido(rst.getBoolean("sugestaocotacao"));
                    imp.setDescontinuado(rst.getBoolean("descontinuado"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "  f.cadastro_id id,\n" +
                    "  f.razao_social razao,\n" +
                    "  f.fantasia,\n" +
                    "  f.cadastro_id cnpj,\n" +
                    "  f.insc_estadual,\n" +
                    "  f.codigo_cliente,\n" +
                    "  case when upper(f.situacao) != 'NORMAL' then 0 else 1 end ativo,\n" +
                    "  f.endereco_fat,\n" +
                    "  f.numero_end_fat,\n" +
                    "  f.complemento_end_fat,\n" +
                    "  f.bairro_fat,\n" +
                    "  cid.nome municipio_fat,\n" +
                    "  cid.estado_id uf_fat,\n" +
                    "  f.cep_fat,\n" +
                    "  f.endereco_cob,\n" +
                    "  f.numero_end_cob,\n" +
                    "  f.complemento_end_cob,\n" +
                    "  f.bairro_cob,\n" +
                    "  cid.nome municipio_cob,\n" +
                    "  cid.estado_id uf_cob,\n" +
                    "  f.cep_cob,\n" +
                    "  f.ddd_fat,\n" +
                    "  f.fone_voz_fat,\n" +
                    "  f.fone_dados_fat,\n" +
                    "  f.fone_fax_fat,\n" +
                    "  f.fone_outros,\n" +
                    "  f.dt_cadastro,\n" +
                    "  f.tipo_cadastro\n" +
                    "from\n" +
                    "  cadastros f\n" +
                    "  left join cidades cid on\n" +
                    "       f.cidade_fat_id = cid.cidade_id\n" +
                    "  left join cidades cob on\n" +
                    "       f.cidade_cob_id = cob.cidade_id\n" +
                    "where\n" +
                    "  f.tipo_cadastro in ('F','I','A','T','B','E','D')\n" +
                    "order by\n" +
                    "  f.razao_social"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    imp.setIe_rg(rst.getString("insc_estadual"));
                    imp.setAtivo(rst.getBoolean("ativo"));
                    imp.setEndereco(rst.getString("endereco_fat"));
                    imp.setNumero(rst.getString("numero_end_fat"));
                    imp.setComplemento(rst.getString("complemento_end_fat"));
                    imp.setBairro(rst.getString("bairro_fat"));
                    imp.setMunicipio(rst.getString("municipio_fat"));
                    imp.setUf(rst.getString("uf_fat"));
                    imp.setCep(rst.getString("cep_fat"));
                    imp.setCob_endereco(rst.getString("endereco_cob"));
                    imp.setCob_numero(rst.getString("numero_end_cob"));
                    imp.setCob_complemento(rst.getString("complemento_end_cob"));
                    imp.setCob_bairro(rst.getString("bairro_cob"));
                    imp.setCob_municipio(rst.getString("municipio_cob"));
                    imp.setCob_uf(rst.getString("uf_cob"));
                    imp.setCob_cep(rst.getString("cep_cob"));                  
                    String ddd = Utils.stringLong(rst.getString("ddd_fat"));                    
                    imp.setTel_principal(ddd + Utils.stringLong(rst.getString("fone_voz_fat")));
                    imp.addTelefone("DADOS", ddd + Utils.stringLong(rst.getString("fone_dados_fat")));
                    imp.addTelefone("FAX", ddd + Utils.stringLong(rst.getString("fone_fax_fat")));
                    imp.addTelefone("OUTROS", ddd + Utils.stringLong(rst.getString("fone_outros")));
                    imp.setDatacadastro(rst.getDate("dt_cadastro"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select produto_id, fornecedor_id, codigo_produto_no_fornecedor from siac_produtos_fornecedores order by 1,2"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setIdFornecedor(rst.getString("fornecedor_id"));
                    imp.setIdProduto(rst.getString("produto_id"));
                    imp.setCodigoExterno(rst.getString("codigo_produto_no_fornecedor"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "  ca.cadastro_id id,\n" +
                    "  ca.cadastro_id cnpj,\n" +
                    "  ca.insc_estadual inscricaoestadual,\n" +
                    "  ca.orgao_expedidor orgaoemissor,\n" +
                    "  ca.razao_social,\n" +
                    "  ca.fantasia,\n" +
                    "  case when coalesce(upper(ca.situacao),'') in ('NORMAL','') then 1 else 0 end ativo,\n" +
                    "  case when coalesce(upper(ca.situacao),'') in ('SUSPENSO') then 1 else 0 end bloqueado,\n" +
                    "  ca.endereco_fat,\n" +
                    "  ca.numero_end_fat,\n" +
                    "  ca.complemento_end_fat,\n" +
                    "  ca.bairro_fat,\n" +
                    "  cd.nome municipio,\n" +
                    "  cd.estado_id uf,\n" +
                    "  ca.cep_fat,\n" +
                    "  ca.endereco_cob,\n" +
                    "  ca.numero_end_cob,\n" +
                    "  ca.complemento_end_cob,\n" +
                    "  ca.bairro_cob,\n" +
                    "  cd1.nome municipio_cob,\n" +
                    "  cd1.estado_id uf_cob,\n" +
                    "  ca.cep_cob,\n" +
                    "  ca.estado_civil,\n" +
                    "  ca.dt_nascimento,\n" +
                    "  ca.dt_cadastro,\n" +
                    "  ca.sexo,\n" +
                    "  ca.ddd_trabalho,\n" +
                    "  ca.fone_voz_trabalho,\n" +
                    "  ca.cargo,\n" +
                    "  ca.renda_liquida,\n" +
                    "  ca.limite_credito,\n" +
                    "  ca.nome_conjuge,\n" +
                    "  ca.nome_pai,\n" +
                    "  ca.nome_mae,\n" +
                    "  ca.dia_faturar diavencimento,\n" +
                    "  ca.e_mail,\n" +
                    "  ca1.email_compras,\n" +
                    "  ca1.email_financeiro,\n" +
                    "  ca1.email_vendas,\n" +
                    "  ca.ddd_fat,\n" +
                    "  ca.fone_dados_fat,\n" +
                    "  ca.fone_fax_fat,\n" +
                    "  ca.fone_outros,\n" +
                    "  ca.fone_voz_fat\n" +
                    "from\n" +
                    "  cadastros ca\n" +
                    "  left join cadastros1 ca1 on\n" +
                    "       ca.cadastro_id = ca1.cadastro_id and\n" +
                    "       ca.compl_cadastro_id = ca1.compl_cadastro_id\n" +
                    "  left join cidades cd on\n" +
                    "       cd.cidade_id = ca.cidade_fat_id\n" +
                    "  left join cidades cd1 on\n" +
                    "       cd1.cidade_id = ca.cidade_cob_id\n" +
                    "where\n" +
                    "       ca.tipo_cadastro in ('A','C','O','D','E')\n" +
                    "order by\n" +
                    "      id"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    
                    imp.setId(rst.getString("id"));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setInscricaoestadual(rst.getString("inscricaoestadual"));
                    imp.setOrgaoemissor(rst.getString("orgaoemissor"));
                    imp.setRazao(rst.getString("razao_social"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setAtivo(rst.getBoolean("ativo"));
                    imp.setBloqueado(rst.getBoolean("bloqueado"));
                    imp.setEndereco(rst.getString("endereco_fat"));
                    imp.setNumero(rst.getString("numero_end_fat"));
                    imp.setComplemento(rst.getString("complemento_end_fat"));
                    imp.setBairro(rst.getString("bairro_fat"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep_fat"));
                    imp.setCobrancaEndereco(rst.getString("endereco_cob"));
                    imp.setCobrancaNumero(rst.getString("numero_end_cob"));
                    imp.setCobrancaComplemento(rst.getString("complemento_end_cob"));
                    imp.setCobrancaBairro(rst.getString("bairro_cob"));
                    imp.setCobrancaMunicipio(rst.getString("municipio_cob"));
                    imp.setCobrancaUf(rst.getString("uf_cob"));
                    imp.setCobrancaCep(rst.getString("cep_cob"));
                    //imp.set(rst.getString("estado_civil"));
                    imp.setDataNascimento(rst.getDate("dt_nascimento"));
                    imp.setDataCadastro(rst.getDate("dt_cadastro"));
                    imp.setSexo("F".equals(rst.getString("sexo")) ? TipoSexo.FEMININO : TipoSexo.MASCULINO);
                    imp.setEmpresaTelefone(formatarTelefone(rst.getString("ddd_trabalho"), rst.getString("fone_voz_trabalho")));
                    imp.setCargo(rst.getString("cargo"));
                    imp.setSalario(rst.getDouble("renda_liquida"));
                    imp.setValorLimite(rst.getDouble("limite_credito"));
                    imp.setNomeConjuge(rst.getString("nome_conjuge"));
                    imp.setNomePai(rst.getString("nome_pai"));
                    imp.setNomeMae(rst.getString("nome_mae"));
                    imp.setDiaVencimento(rst.getInt("diavencimento"));
                    imp.setEmail(rst.getString("e_mail"));
                    imp.addEmail("COMPRAS", rst.getString("email_compras"), TipoContato.COMERCIAL);
                    imp.addEmail("FINANCEIRO", rst.getString("email_financeiro"), TipoContato.FINANCEIRO);
                    imp.addEmail("VENDAS", rst.getString("email_vendas"), TipoContato.COMERCIAL);
                    imp.setTelefone(formatarTelefone(rst.getString("ddd_fat"), rst.getString("fone_voz_fat")));
                    imp.addTelefone("FAX", formatarTelefone(rst.getString("ddd_fat"), rst.getString("fone_fax_fat")));
                    imp.addTelefone("OUTROS", formatarTelefone(rst.getString("ddd_fat"), rst.getString("fone_outros")));
                    imp.addTelefone("DADOS", formatarTelefone(rst.getString("ddd_fat"), rst.getString("fone_dados_fat")));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }
    
    private String formatarTelefone(String ddd, String numero) {
        numero = Utils.stringLong(numero);
        if ("0".equals(numero)) {
            return "";
        }
        return Utils.stringLong(ddd) + numero;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "  f.empresa_id||'-'||f.tipo_conta||'-'||f.tipo_doc||'-'||f.cadastro_id||'-'||f.compl_cadastro_id||'-'||f.documento_id id,\n" +
                    "  f.dt_emissao,\n" +
                    "  f.documento_id numerocupom,\n" +
                    "  f.vlr_titulo valor,\n" +
                    "  f.cadastro_id cliente_id,\n" +
                    "  f.dt_vencto vencimento,\n" +
                    "  f.parcela,\n" +
                    "  f.vlr_juros,\n" +
                    "  f.cadastro_id cnpj,\n" +
                    "  f.status\n" +
                    "from\n" +
                    "  financeiro f\n" +
                    "where\n" +
                    "  f.empresa_id = '" + getLojaOrigem() + "' and\n" +
                    "  f.tipo_conta = 'CR' and\n" +
                    "  f.status = 'A' and\n" +
                    "  f.cadastro_id != '111.111.111/11'\n" +
                    "order by\n" +
                    "  f.dt_emissao"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    
                    imp.setId(rst.getString("id"));
                    imp.setDataEmissao(rst.getDate("dt_emissao"));
                    imp.setNumeroCupom(rst.getString("numerocupom"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setIdCliente(rst.getString("cliente_id"));
                    imp.setDataVencimento(rst.getDate("vencimento"));
                    imp.setParcela(Utils.stringToInt(rst.getString("parcela")));
                    imp.setJuros(rst.getDouble("vlr_juros"));
                    imp.setCnpjCliente(rst.getString("cnpj"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<OfertaIMP> getOfertas(Date dataTermino) throws Exception {
        List<OfertaIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "  p.produto_id,\n" +
                    "  p.dt_inicio,\n" +
                    "  p.dt_final,\n" +
                    "  p.vlr_unitario\n" +
                    "from\n" +
                    "  promocoes p\n" +
                    "where\n" +
                    "  p.empresa_id = '" + getLojaOrigem() + "' and\n" +
                    "  p.dt_inicio <= current_date and\n" +
                    "  p.dt_final >= current_date"
            )) {
                while (rst.next()) {
                    OfertaIMP imp = new OfertaIMP();
                    
                    imp.setIdProduto(rst.getString("produto_id"));
                    imp.setDataInicio(rst.getDate("dt_inicio"));
                    imp.setDataFim(rst.getDate("dt_final"));
                    imp.setPrecoOferta(rst.getDouble("vlr_unitario"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }
    
    
    
}
