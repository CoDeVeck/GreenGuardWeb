package com.Cibertec.GreenGuard.service;

import com.Cibertec.GreenGuard.repository.INotificacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificacionService {

    @Autowired
    INotificacionRepository notificaRepo;
}
