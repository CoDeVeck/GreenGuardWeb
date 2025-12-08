package com.Cibertec.GreenGuard.service;

import com.Cibertec.GreenGuard.model.TipoIncidentes;
import com.Cibertec.GreenGuard.repository.ITipoIncidenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TipoIncidenteService {

    @Autowired
    ITipoIncidenteRepository incidenteRepo;


    public List<TipoIncidentes>listaCompleta(){
        return incidenteRepo.findAll();
    }
}
