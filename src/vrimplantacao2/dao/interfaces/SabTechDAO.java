/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.interfaces;

/**
 *
 * @author lucasrafael
 */
public class SabTechDAO extends InterfaceDAO {

    @Override
    public String getSistema() {
        return "SabTech";
    }
    
    /*
 
    user: Todos
    pass: 123
    
select 
	icm.Codigo,
	icm.ST as cst,
	icm.SubsTrib as tributacao
from dbo.CPro_TabICMS icm
where icm.Codigo in (select ICMSTabela from dbo.CPro_Produto)	
order by icm.Codigo

select 
	distinct 
	m1.Depto as merc1, m1.Descricao as desc_merc1,
	m2.Classe as merc2, m2.Descricao as desc_merc2
from dbo.CPro_Produto p
inner join dbo.CPro_Depto m1 on m1.Depto = p.Depto
inner join dbo.CPro_Classe m2 on m2.Classe = p.Classe
order by m1.Depto, m2.Classe

select 
	p.Produto as id,
	p.CodBarras as ean,
	p.Balanca,
	p.Validade,
	p.Unidade as tipoembalagem,
	p.Descricao as descricaocompleta,
	p.DescricaoCurta as descricaoreduzida,
	p.Depto,
	p.Classe,
	p.EstMin as estoqueminimo,
	p.EstMax as estoquemaximo,
	p.EstAtual as estoque,
	p.ValorCusto as custo,
	p.VlVenda as precovenda,
	p.Lucro as margem,
	p.ICMSTabela,
	p.Inativo as situacaocadastro,
	p.ClaFiscal as ncm,
	REPLACE(ces.CEST_Codigo, '.', '') as cest,
	pis.ST as cst_pis,
	cof.ST as cst_cofins	
from dbo.CPro_Produto p
left join dbo.CPro_TabCEST ces on ces.CEST = p.CESTTabela
left join dbo.CPro_TabPIS pis on pis.PISTabela = p.PISTabela
left join dbo.CPro_TabCOFINS cof on cof.COFINSTabela = p.COFINSTabela

select 
	ean.Produto, 
	ean.CodBarras, 
	ean.Unidade 
from dbo.CPro_CodBarras ean

select 
	a.Produto as idproduto,
	a.Unidade as tpoembalagem,
	a.Qtde as qtdembalagem,
	a.Unitario as precoatacado,
	p.VlVenda as precovenda
from dbo.CPro_Preco a
inner join dbo.CPro_Produto p on p.Produto = a.Produto
where a.Qtde > 1
and a.Qtde < 100
and a.Unitario < p.VlVenda
order by a.Produto    
    
    */
}
