package com.Cibertec.GreenGuard.service;

import com.Cibertec.GreenGuard.repository.IUsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    @Autowired
    IUsuarioRepository usuarioRepo;
}
