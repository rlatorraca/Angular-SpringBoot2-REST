package com.rlsp.moneyapi.resource;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rlsp.moneyapi.model.Categoria;
import com.rlsp.moneyapi.repository.CategoriaRepository;

/*
 * É o RECURSO para as CATEGORIAs
 *  - pois o REST dispoe, expoe recursos
 *  - Expõe os recursos de categoria
 *  - É um CONTROLLER
 *  - @RestController ==> sera um CONTROLLER e facilitara o retorno de requisicoes REST com retorno com arquivos JSON por exemplo, dispensando anotacoes nos metodos
 *  - @RequestMapping ==> faz o mapeamento da requisicao
 */

@RestController // ==> mostra que eh um CONTROLADOR REST (Controller) e vai facilitar o retorno com JSON, XML, etc
@RequestMapping("/categorias") // ==> o mapeameno da REQUISICAO HTML
public class CategoriaResource {
	
	/**
	 * INJETA a Interface "CategoriaRepository" (repository/CategoriaRespository.java)
	 */
	@Autowired // ==> diz ao SPRING : procure um implementacao de "categoriaRepository e injete aqui
	private CategoriaRepository categoriaRepository;
	
	
	/*
	 * ResponseEntity<?> ==> entidade de resposta SEM TIPO de RETORNO definido 
	 * Se VAZIO, retorna 404 nao achou nada, senao retorna Categorias se receber um resposta 200 | o BUILD serve para retornar um Response Entity
	 *   - ResponseEntity.notFound().build() ==> retorna 404 (nao achou a pagina)
	 *   - ResponseEntity.noContent().build() ==> retorna 204 (sem conteudo)
	 *   
	 *   Ex: return categorias.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(categorias);
	 */
	@GetMapping//==> mapeamento do GET
	public List<Categoria> listar(){
		List<Categoria> categorias = categoriaRepository.findAll();
		return categorias;
	}

}
