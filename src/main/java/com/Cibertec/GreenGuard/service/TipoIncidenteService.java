package com.Cibertec.GreenGuard.service;

import com.Cibertec.GreenGuard.repository.ITipoIncidenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TipoIncidenteService {

    @Autowired
    ITipoIncidenteRepository incidenteRepo;
}
