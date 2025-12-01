package com.Cibertec.GreenGuard.service;

import com.Cibertec.GreenGuard.repository.IDistritoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DistritoService {

    @Autowired
    IDistritoRepository distritoRepo;
}
