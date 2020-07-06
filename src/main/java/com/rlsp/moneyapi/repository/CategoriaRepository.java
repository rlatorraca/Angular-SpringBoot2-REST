package com.rlsp.moneyapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rlsp.moneyapi.model.Categoria;

/**
 * JpaRepository ==> traz varios metodos como: findAll(); save(); delete(); getOne(); 
 *  - usado para operacoes de CRUD *
 */

public interface CategoriaRepository extends JpaRepository<Categoria, Long>{

}
