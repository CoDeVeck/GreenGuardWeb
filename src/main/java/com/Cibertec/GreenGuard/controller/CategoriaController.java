package com.Cibertec.GreenGuard.controller;

import com.Cibertec.GreenGuard.model.Categoria;
import com.Cibertec.GreenGuard.service.CategoriaService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/categoria")
public class CategoriaController {

    @Autowired
    CategoriaService categoriaService;
    
    @GetMapping("/list")
    public ResponseEntity<?> getAll(){
    	List<Categoria> categorias = categoriaService.getAll();
    	return ResponseEntity.ok(categorias);
    }
}
