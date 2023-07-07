package ru.pin120.transystem.services;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BindingService {

    public String getErrors(BindingResult bindingResult){
        String error = bindingResult
                .getAllErrors()
                .stream()
                .map(err-> err.getDefaultMessage())
                .collect(Collectors.toList())
                .toString();

        return error;
    }
}
