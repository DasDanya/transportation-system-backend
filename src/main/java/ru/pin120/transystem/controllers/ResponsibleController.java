package ru.pin120.transystem.controllers;


import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.pin120.transystem.models.Responsible;
import ru.pin120.transystem.payload.response.MessageResponse;
import ru.pin120.transystem.services.BindingService;
import ru.pin120.transystem.services.ResponsibleService;

import java.io.ByteArrayInputStream;
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
    public ResponseEntity<?> getResponsibles(@RequestParam(value = "field",required = false) String field, @RequestParam(value="value", required = false) String value){
        List<Responsible> responsibles;
        try {
            if(field == null || value == null) {
                responsibles = responsibleService.findAllResponsibles();
            } else{
                responsibles = responsibleService.findByField(field,value);
            }
        } catch(Exception e){
            return ResponseEntity.internalServerError().body(new MessageResponse("не удалось получить список ответственных"));
        }

        return new ResponseEntity<>(responsibles, HttpStatus.OK);
    }

    @GetMapping("/delete/{id}")
    public ResponseEntity<?> getDeleteResponsible(@PathVariable("id") int id){
        return findResponsibleById(id);
    }

    @DeleteMapping("delete/{id}")
    @Transactional
    public ResponseEntity<?> deleteResponsible(@PathVariable("id") int id){
        try{
            responsibleService.deleteResponsible(id);
        } catch (Exception e){
            return new ResponseEntity<>(new MessageResponse("Ошибка удаления ответственного с id "+id+""),HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(new MessageResponse("Ответственный успешно удален!"),HttpStatus.OK);
    }

    private ResponseEntity<?> findResponsibleById(int id){
        Responsible responsible;
        try{
            responsible = responsibleService.findResponsibleById(id);
        } catch (Exception e){
            return new ResponseEntity(new MessageResponse(e.getMessage()), HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(responsible, HttpStatus.OK);
    }

    @GetMapping("/update/{id}")
    public ResponseEntity<?> getUpdateResponsible(@PathVariable("id") int id){
       return findResponsibleById(id);
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateResponsible(@RequestPart("responsible") @Valid Responsible responsible, BindingResult bindingResult,
                                               @RequestPart(value = "photo",required = false) MultipartFile photo){

        if(bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(new MessageResponse(bindingService.getErrors(bindingResult)));
        }
        try{
            responsibleService.updateResponsible(responsible,photo);
        }  catch (Exception e){
            return handlingSaveException(e);
        }

        return new ResponseEntity<>(new MessageResponse("Данные об ответственном успешно обновлены!"),HttpStatus.OK);
    }


    private ResponseEntity<?> handlingSaveException(Exception exception){
        String errorMessage = exception.getMessage();
        if(exception instanceof DataIntegrityViolationException){
            errorMessage = "Введенный номер телефона принадлежит другому ответственному";
        }
        return new ResponseEntity<>(new MessageResponse(errorMessage), HttpStatus.NOT_FOUND);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addResponsible(@RequestPart("responsible") @Valid Responsible responsible, BindingResult bindingResult,
                                            @RequestPart("photo") MultipartFile photo){

        if(bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(new MessageResponse(bindingService.getErrors(bindingResult)));
        }
        try {
            responsibleService.addResponsible(responsible,photo);

        } catch (Exception e) {
            return handlingSaveException(e);
        }

        return new ResponseEntity<>(new MessageResponse("Ответственный успешно добавлен!"),HttpStatus.CREATED);
    }


    @GetMapping("/excel/{id}")
    public ResponseEntity<Resource> generateReport(@PathVariable("id") int id){
        String filename = "warehouses.xlsx";

        ByteArrayInputStream actualData = responsibleService.generateReportInExcel(id);
        InputStreamResource file = new InputStreamResource(actualData);

        ResponseEntity<Resource> body =  ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename="+filename+"")
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(file);

        return body;
    }
}
