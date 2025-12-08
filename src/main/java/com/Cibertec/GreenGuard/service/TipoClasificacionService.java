package com.Cibertec.GreenGuard.service;

import com.Cibertec.GreenGuard.model.TipoClasificacion;
import com.Cibertec.GreenGuard.repository.ITipoClasificacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TipoClasificacionService {

    @Autowired
    ITipoClasificacionRepository clasificaRepo;


    public List<TipoClasificacion> listaCompleta(){
        return clasificaRepo.findAll();
    }
}
