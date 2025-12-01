package com.Cibertec.GreenGuard.service;

import com.Cibertec.GreenGuard.repository.ICuponRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CuponService {

    @Autowired
    ICuponRepository cuponRepo;
}
