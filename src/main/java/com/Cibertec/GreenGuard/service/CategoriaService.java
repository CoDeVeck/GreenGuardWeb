package com.Cibertec.GreenGuard.service;

import com.Cibertec.GreenGuard.model.Categoria;
import com.Cibertec.GreenGuard.repository.ICategoriaRepository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoriaService {

    @Autowired
    ICategoriaRepository cateRepo;
    
    public List<Categoria> getAll()
    {
    	return cateRepo.findAll();
    }
}
