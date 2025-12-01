package com.Cibertec.GreenGuard.service;

import com.Cibertec.GreenGuard.repository.ITiendaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TiendaService {

    @Autowired
    ITiendaRepository tiendaRepo;
}
