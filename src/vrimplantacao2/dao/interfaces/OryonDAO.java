package vrimplantacao2.dao.interfaces;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import vrimplantacao2.dao.cadastro.Estabelecimento;

/**
 *
 * @author Importacao
 */
public class OryonDAO extends InterfaceDAO {

    @Override
    public String getSistema() {
        return "Oryon";
    }
    
    public List<Estabelecimento> getLojaCliente() {
        return new ArrayList<>(Arrays.asList(new Estabelecimento("1", "SUPERMERCADO ANDREA")));
    }
}

/*
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
-- Scrip de Mercadologico
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
-- Scrip Produto Fornecedor
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
-- Scrip de Cliente
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
-- Scrip de Fornecedor
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
-- Scrip Rotativo
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
-- Scrip Conta Pagar
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
-- Scrip Venda
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
-- Scrip Venda Item
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