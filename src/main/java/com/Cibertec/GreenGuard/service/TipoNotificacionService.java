package com.Cibertec.GreenGuard.service;

import com.Cibertec.GreenGuard.repository.ITipoNotificacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TipoNotificacionService {

    @Autowired
    ITipoNotificacionRepository notificaRepo;
}
