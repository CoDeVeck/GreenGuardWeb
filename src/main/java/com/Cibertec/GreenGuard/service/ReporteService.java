package com.Cibertec.GreenGuard.service;

import com.Cibertec.GreenGuard.repository.IReporteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReporteService {

    @Autowired
    IReporteRepository reporRepo;
}
