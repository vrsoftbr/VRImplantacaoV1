package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import vrimplantacao.classe.ConexaoAccess;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Importacao
 */
public class OryonDAO extends InterfaceDAO {

    @Override
    public String getSistema() {
        return "Oryon";
    }
    
    public List<Estabelecimento> getLojaCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        
        try (Statement stm = ConexaoAccess.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select Codigo, Descricao from Tabela_Unidade_Negocio order by Codigo"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("Codigo"), rst.getString("Descricao")));
                }
            }
        }
        
        return result;
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                OpcaoProduto.MERCADOLOGICO_PRODUTO
        ));
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoAccess.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "  g.grupo as mercadologico1,\n" +
                    "  g.sub_grupo as mercadologico2,\n" +
                    "  g.Nome as mercadologico3\n" +
                    "from\n" +
                    "  tabela_categ g \n" +
                    "order by\n" +
                    "  1, 2, 3 "
            )) {
                while (rst.next()) {
                    
                    MercadologicoIMP imp = new MercadologicoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    
                    String g1 = Utils.acertarTexto(rst.getString("mercadologico1"));
                    String g2 = Utils.acertarTexto(rst.getString("mercadologico2"));
                    String g3 = Utils.acertarTexto(rst.getString("mercadologico3"));
                    
                    if ("".equals(g2)) {
                        g2 = g3;
                    }
                    if ("".equals(g1)) {
                        g1 = g2;
                    }                    
                    
                    imp.setMerc1ID(g1);
                    imp.setMerc1Descricao(g1);
                    imp.setMerc2ID(g2);
                    imp.setMerc2Descricao(g2);
                    imp.setMerc3ID(g3);
                    imp.setMerc3Descricao(g3);
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoAccess.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	p.codigo as id,\n" +
                    "	p.codigo as codigobarras,\n" +
                    "	p.descricao as descricaocompleta,\n" +
                    "	p.descricao as descricaoreduzida,\n" +
                    "	p.descricao as descricaogondola,\n" +
                    "	g.grupo as mercadologico1,\n" +
                    "	g.sub_grupo as mercadologico2,\n" +
                    "	g.nome as mercadologico3,\n" +
                    "	p.familia,\n" +
                    "	p.unidade,\n" +
                    "	p.qt_embalagem as qtdembalagem,\n" +
                    "	p.situacao as ativo,\n" +
                    "	p.qt_estoque as estoque,\n" +
                    "	p.qt_minimo as estoqueminimo,\n" +
                    "	p.qt_maximo as estoquemaximo,\n" +
                    "	p.preco_venda as precovenda,\n" +
                    "	p.preco_compra as custocomimposto,\n" +
                    "	p.preco_compra as custosemimposto,\n" +
                    "	p.margem_lucro as margem,\n" +
                    "	p.usa_balanca as balanca,\n" +
                    "	p.dias_validade as validade,\n" +
                    "	p.data_cadastro as datacadastro,\n" +
                    "	p.peso as pesobruto,\n" +
                    "	p.pesoliquido,\n" +
                    "	p.ncm,\n" +
                    "	p.cest,\n" +
                    "	p.situacao_tributaria_icm_entrada as cst_e,\n" +
                    "	p.situacao_tributaria_icm_saida_ne as cst_ne_s,\n" +
                    "	p.aliquota_icm_saida_ne as icms_s,\n" +
                    "	p.aliquota_st_ret as icmsretencao,\n" +
                    "	p.situacao_tributaria_icm_saida_fe as cst_fe_s,\n" +
                    "	p.situacao_tributaria_pis as pis_s,\n" +
                    "	p.situacao_tributaria_pis_entrada as pis_e,\n" +
                    "	p.situacao_tributaria_cofins as cofins_s,\n" +
                    "	p.situacao_tributaria_cofins_entrada as cofins_e,\n" +
                    "	p.codigo_natureza_receita_pis_cofins as natreceita,\n" +
                    "	p.margem_valor_agregado_fe as mva_fe,\n" +
                    "	p.margem_valor_agregado as mva\n" +
                    "from\n" +
                    "	tabela_pro p\n" +
                    "	left join tabela_categ g on\n" +
                    "		p.categoria = g.codigo\n" +
                    "order by\n" +
                    "	1"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setEan(rst.getString("codigobarras"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(rst.getString("descricaogondola"));
                    
                    String g1 = Utils.acertarTexto(rst.getString("mercadologico1"));
                    String g2 = Utils.acertarTexto(rst.getString("mercadologico2"));
                    String g3 = Utils.acertarTexto(rst.getString("mercadologico3"));
                    
                    if ("".equals(g2)) {
                        g2 = g3;
                    }
                    if ("".equals(g1)) {
                        g1 = g2;
                    }                   
                    imp.setCodMercadologico1(g1);
                    imp.setCodMercadologico2(g2);
                    imp.setCodMercadologico3(g3);
                    imp.setIdFamiliaProduto(rst.getString("familia"));
                    imp.setTipoEmbalagem(rst.getString("unidade"));
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                    imp.setSituacaoCadastro(rst.getBoolean("ativo") ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
                    imp.setEstoqueMaximo(rst.getDouble("estoquemaximo"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.seteBalanca(rst.getBoolean("balanca"));
                    imp.setValidade(rst.getInt("validade"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setPesoBruto(rst.getDouble("pesobruto"));
                    imp.setPesoLiquido(rst.getDouble("pesoliquido"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setIcmsCstEntrada(rst.getInt("cst_e"));
                    imp.setIcmsCstSaida(rst.getInt("icms_s"));
                    imp.setPiscofinsCstCredito(rst.getString("pis_s"));
                    imp.setPiscofinsCstDebito(rst.getString("pis_e"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("natreceita"));
                    imp.setPautaFiscalId(rst.getString("mva"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }
    
    
}

/*
--- Script Loja
select
   codigo,
   descricao
from
   tabela_unidade_negocio
*/

/*
--- Script de Produto
select
  p.codigo as id,
  p.codigo as codigobarras,
  descricao as descricaocompleta,
  descricao as descricaoreduzida,
  descricao as descricaogondola,
  categoria as cod_mercadologico1,
  g.grupo as mercadologico1,
  1 as cod_mercadologico2,
  g.sub_grupo as mercadologico2,
  1 as cod_mercadologico3,
  g.sub_grupo as mercadologico3,
  familia,
  unidade,
  qt_embalagem as qtdembalagem,
  situacao as ativo,
  qt_estoque as estoque,
  qt_minimo as estoqueminimo,
  qt_maximo as estoquemaximo,
  preco_venda as precovenda,
  preco_compra as custocomimposto,
  preco_compra as custosemimposto,
  margem_lucro as margem,
  usa_balanca as balanca,
  dias_validade as validade,
  data_cadastro as datacadastro,
  peso as pesobruto,
  pesoliquido,
  ncm,
  cest,
  situacao_tributaria_icm_entrada as cst_e,
  situacao_tributaria_icm_saida_ne as cst_ne_s,
  aliquota_icm_saida_ne as icms_s,
  aliquota_st_ret as icmsretencao,
  situacao_tributaria_icm_saida_fe as cst_fe_s,
  situacao_tributaria_pis as pis_s,
  situacao_tributaria_pis_entrada as pis_e,
  situacao_tributaria_cofins as cofins_s,
  situacao_tributaria_cofins_entrada as cofins_e,
  codigo_natureza_receita_pis_cofins as natreceita,
  margem_valor_agregado_fe as mva_fe,
  margem_valor_agregado as mva
from
  tabela_pro p,
  tabela_categ g
where
  p.categoria = g.codigo
order by
  1
*/

/*
-- Script de Mercadologico
select 
   codigo as merc1,
   grupo as descmerc1,
   1 as merc2,
   sub_grupo as descmerc2,
   1 as merc3,
   nome as descmerc3 
from
   tabela_categ
order by
   1, 2
*/

/*
-- Script Produto Fornecedor
select
  codpro as idproduto,
  codfor as idfornecedor,
  codigo_forn as codigoexterno
from
  tabela_profor
order by
  codpro, codfor
  
 -----------------------------
 
 select 
   codigo as idproduto,
   fornecedor as idfornecedor,
   codigo_forn as codigoexterno 
from
   tabela_pro
where
   fornecedor is not null and 
   fornecedor <> -1 and
   codigo_forn is not null
order by
   1
*/

/*
-- Script de Cliente
select 
   codigo as id,
   nome as razao,
   fantasia,
   endereco_logradouro as endereco,
   endereco_numero as numero,
   endereco_complemento as complemento,
   bairro,
   cidade,
   codigo_cidade as ibgemunicipio,
   cep,
   uf,
   telefone,
   fax,
   rg,
   cic as cpf,
   cnpj,
   ie,
   contato,
   limite_credito,
   data_cadastro,
   pai,
   mae,
   email,
   cancelado,
   inativo,
   sexomasc as sexo,
   profissao 
from
   tabela_cli
order by
   1
*/

/*
-- Script de Fornecedor
select 
   codigo as id,
   nome as razao,
   fantasia,
   endereco_logradouro as rua,
   endereco_numero as numero,
   endereco_complemento as complemento,
   bairro,
   cidade,
   codigo_cidade as ibgemunicipio,
   cep,
   uf,
   telefone,
   fax,
   cnpj,
   ie,
   contato,
   data_cadastro,
   email,
   inativo,
   regime_tributario as tipoempresa 
from
   tabela_for
order by
   1
*/

/*
-- Script Rotativo
select
   codigo_fluxo as id,
   duplicata,
   num_maquina as ecf,
   dia as datalanc,
   vencimento,
   numero as coo,
   prazo as valor,
   cliente,
   descricao as observacao
from
   tabela_fluxo
where
   cliente is not null and
   data_baixa is null and
   duplicata is not null
order by
   vencimento
*/

/*
-- Script Conta Pagar
select
   codigo_fluxo as id,
   duplicata,
   num_maquina as ecf,
   dia as datalanc,
   vencimento,
   numero as coo,
   prazo as valor,
   fornecedor,
   descricao as observacao
from
   tabela_fluxo
where
   fornecedor is not null and
   data_baixa is null and
   duplicata is not null
order by
   vencimento
*/

/*
-- Script Venda
select
    n.link as id,
    n.dia as data,
    n.cliente as clientepreferencial,
    c.nome as razao,
    c.cnpj,
    c.cic,
    n.numero as numerocupom,
    n.desconto as valordesconto,
    n.valor as subtotalimpressora,
    n.num_maquina as ecf,
    n.situacao as cancelado
from
    tabela_nota1 n,
    tabela_cli c
where
    n.cliente = c.codigo and
    n.data_inclusao between #01/01/2018# and #21/08/2019# and
    n.cupom = True
order by
    dia
*/

/*
-- Script Venda Item
select 
   vi.link as cod_venda,
   vi.dia as data,
   vi.numero as numerocupom,
   vi.item as sequencia,
   vi.codigo as cod_produto,
   p.descricao as descricaocompleta,
   p.unidade as unidademedida,
   vi.quantidade,
   vi.valor,
   vi.desconto,
   vi.situacao,
   vi.situacao_tributaria_icm as icms_cst,
   vi.aliquota_icm as icms_aliq
from 
   tabela_nota2 vi,
   tabela_nota1 v,
   tabela_pro p
where
   vi.link = v.link and
   vi.codigo = p.codigo and
   v.data_inclusao between #01/01/2018# and #21/08/2019#
order by
   1, 2, 4
*/