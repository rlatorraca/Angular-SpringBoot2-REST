package com.rlsp.moneyapi.resource;

import java.net.URI;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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
	
	/**
	 * @RequestBody ==> pega o que FOI ENVIADO para o servidor
	 * -  O código ideal para resposta de armazenamento no DB é do 201 (created)
	 * 
	 * @ResponseStatus(value = HttpStatus.CREATED) ==> apos criado retornara com um 201 (created)
	 * 
	 * - O REST pede um location no retorno, mostrando como deve ser recuperado o recurso no futuro
	 * 
	 *  @Valid ==> valida os campos usando jakarta.validation 
	 */
	
	/**
	 * Utilizando "findOne"
	 * 
	 *  @GetMapping("/{codigo}")
	 *	public Categoria buscarPeloCodigo(@PathVariable Long codigo) {
	 *  	Categoria categoriaExample = new Categoria();
     * 		categoriaExample.setCodigo(codigo);
     * 
     * 		Example<Categoria> example = Example.of(categoriaExample);
     *
     * 		return this.categoriaRepository.findOne(example).orElse(null);
	 * 
	 */
	
	/**
	 * Utilizando "findById"
	 * 	@GetMapping("/{codigo}")
	 *	public Categoria buscarPeloCodigo(@PathVariable Long codigo) {
  	 *		return this.categoriaRepository.findById(codigo).orElse(null);
	 *	}
	 */
	
	// @ResponseStatus(value = HttpStatus.CREATED) == ResponseEntity.created(uri).body(categoriaSalva);
	@PostMapping
	public ResponseEntity<Categoria> salvar(@Valid @RequestBody Categoria categoria, HttpServletResponse response) {
		
		Categoria categoriaSalva = categoriaRepository.save(categoria);
		
		// Construindo a LOCATION		
		URI uri = ServletUriComponentsBuilder
						.fromCurrentRequestUri()    // pega a URI atual
						.path("/{codigo}")
						.buildAndExpand(categoriaSalva.getCodigo()) // Pega o codigo
						.toUri();
		
		response.setHeader("Location", uri.toASCIIString()); // resposta cm a Location
		
		// Devolvendo a categoria criada ou salva
		return ResponseEntity.created(uri).body(categoriaSalva);
	}
	
	@GetMapping("/{codigo}")
	public ResponseEntity<Categoria> buscarPeloCodigo(@PathVariable Long codigo) {
		
		// Utilizando MAP
		return this.categoriaRepository.findById(codigo)
			      .map(categoria -> ResponseEntity.ok(categoria))
			      .orElse(ResponseEntity.notFound().build());
		
		//Utilizando isPresent()
		/* Optional<Categoria> categoria = this.categoriaRepository.findById(codigo);
		    return categoria.isPresent() ? ResponseEntity.ok(categoria.get()) : ResponseEntity.notFound().build();
		*/
		
	}

}
