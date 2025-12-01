package com.Cibertec.GreenGuard.service;

import com.Cibertec.GreenGuard.repository.IUsuarioCuponRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsuarioCuponService {

    @Autowired
    IUsuarioCuponRepository usuCupoRepo;
}
