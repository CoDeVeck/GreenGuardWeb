package com.Cibertec.GreenGuard.service;

import com.Cibertec.GreenGuard.repository.IRolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RolService {

    @Autowired
    IRolRepository rolRepo;


}
