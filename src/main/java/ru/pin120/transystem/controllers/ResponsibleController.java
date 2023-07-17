package ru.pin120.transystem.controllers;


import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
@Tag(name="ResponsibleController",description = "Контроллер для работы с данными об ответственых лицах")
public class ResponsibleController {

    private final ResponsibleService responsibleService;
    private final BindingService bindingService;

    public ResponsibleController(ResponsibleService responsibleService, BindingService bindingService) {
        this.responsibleService = responsibleService;
        this.bindingService = bindingService;
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR') or hasRole('USER')")
    @Tag(name="getResponsibles",description = "Получение списка ответственных")
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
    @PreAuthorize("hasRole('ADMIN')")
    @Tag(name="getDeleteResponsible",description = "Получение удаляемого ответственного")
    public ResponseEntity<?> getDeleteResponsible(@PathVariable("id") int id){
        return findResponsibleById(id);
    }

    @DeleteMapping("delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Tag(name="deleteResponsible",description = "Удаление ответственного")
    @Transactional
    public ResponseEntity<?> deleteResponsible(@PathVariable("id") int id){
        try{
            responsibleService.deleteResponsible(id);
        } catch (Exception e){
            return new ResponseEntity<>(new MessageResponse("Ошибка удаления ответственного с id "+id+""),HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(new MessageResponse("Ответственный успешно удален!"),HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    @Tag(name="findResponsibleById",description = "Получение ответственного")
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
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    @Tag(name="getUpdateResponsible",description = "Получение изменяемого ответственного")
    public ResponseEntity<?> getUpdateResponsible(@PathVariable("id") int id){
       return findResponsibleById(id);
    }

    @PutMapping("/update")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    @Tag(name="updateResponsible",description = "Изменение данных об ответственном")
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

    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    @Tag(name="handlingSaveException",description = "Получение сообщения об ошибки при сохранении данных об ответственном")
    private ResponseEntity<?> handlingSaveException(Exception exception){
        String errorMessage = exception.getMessage();
        if(exception instanceof DataIntegrityViolationException){
            errorMessage = "Введенный номер телефона принадлежит другому ответственному";
        }
        return new ResponseEntity<>(new MessageResponse(errorMessage), HttpStatus.NOT_FOUND);
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    @Tag(name="addResponsible",description = "Добавление ответственного")
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
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    @Tag(name="generateReport",description = "Получение excel файла с данными о складах ответственного")
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
