package ru.pin120.transystem.controllers;


import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.pin120.transystem.exceptions.FileIsNotImageException;
import ru.pin120.transystem.models.Responsible;
import ru.pin120.transystem.payload.response.MessageResponse;
import ru.pin120.transystem.services.BindingService;
import ru.pin120.transystem.services.ResponsibleService;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/responsible")
public class ResponsibleController {

    private final ResponsibleService responsibleService;
    private final BindingService bindingService;

    public ResponsibleController(ResponsibleService responsibleService, BindingService bindingService) {
        this.responsibleService = responsibleService;
        this.bindingService = bindingService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<Responsible>> getAllResponsibles(){
        List<Responsible> responsibles = responsibleService.findAllResponsibles();

        return new ResponseEntity<>(responsibles, HttpStatus.OK);
    }

    /*@PostMapping("/add")
    public ResponseEntity<?> addResponsible(@RequestBody @Valid Responsible responsible, BindingResult bindingResult){

        if(bindingResult.hasErrors()) {
            return new ResponseEntity<>(bindingService.getErrors(bindingResult), HttpStatus.BAD_REQUEST);
        }
        try {
            responsibleService.addResponsible(responsible);

        } catch (Exception e) {
            return new ResponseEntity<>(new MessageResponse("121"), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(new MessageResponse("Ответственный успешно добавлен!"),HttpStatus.CREATED);
    }*/

    @PostMapping("/add")
    public ResponseEntity<?> addResponsible(@RequestPart("responsible") @Valid Responsible responsible, BindingResult bindingResult,
                                            @RequestPart("photo") MultipartFile photo){

        if(bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(new MessageResponse(bindingService.getErrors(bindingResult)));
        }
        try {
            responsibleService.addResponsible(responsible,photo);

        } catch (Exception e) {
            String errorMessage = e.getMessage();
            if(e instanceof DataIntegrityViolationException){
                errorMessage = "Введенный номер телефона принадлежит друкому ответственному";
            }
            System.out.println(e.getClass());
            return new ResponseEntity<>(new MessageResponse(errorMessage), HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(new MessageResponse("Ответственный успешно добавлен!"),HttpStatus.CREATED);
    }
    
}
