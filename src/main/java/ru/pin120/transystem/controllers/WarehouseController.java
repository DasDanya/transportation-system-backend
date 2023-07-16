package ru.pin120.transystem.controllers;


import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.pin120.transystem.models.Responsible;
import ru.pin120.transystem.models.Warehouse;
import ru.pin120.transystem.payload.response.MessageResponse;
import ru.pin120.transystem.sendModels.WarehouseWithResponsibles;
import ru.pin120.transystem.services.BindingService;
import ru.pin120.transystem.services.ResponsibleService;
import ru.pin120.transystem.services.WarehouseService;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/warehouse")
public class WarehouseController {

    private final WarehouseService warehouseService;
    private final BindingService bindingService;
    private final ResponsibleService responsibleService;

    public WarehouseController(WarehouseService warehouseService, BindingService bindingService, ResponsibleService responsibleService) {
        this.warehouseService = warehouseService;
        this.bindingService = bindingService;
        this.responsibleService = responsibleService;
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR') or hasRole('USER')")
    public ResponseEntity<?> getWarehouses(@RequestParam(value = "field",required = false) String field, @RequestParam(value="value", required = false) String value){
        List<Warehouse> warehouses;
        try{
            if(field == null || value == null) {
                warehouses = warehouseService.findAllWarehouses();
            }else{
                warehouses = warehouseService.findByField(field,value);
            }
        } catch(Exception e){
            return new ResponseEntity<>(new MessageResponse("Ошибка получения списка складов"), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(warehouses, HttpStatus.OK);
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getDeleteWarehouse(@PathVariable("id") int id){
        Warehouse warehouse;
        try{
            warehouse = warehouseService.findWarehouseById(id);
        }catch (Exception e){
            return new ResponseEntity<>(new MessageResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(warehouse, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<?> deleteWarehouse(@PathVariable("id") int id){
        try{
            warehouseService.deleteWarehouse(id);
        }catch (Exception e){
            return new ResponseEntity<>(new MessageResponse("Ошибка удаления склада с номером "+id+""), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(new MessageResponse("Склад успешно удалён!"), HttpStatus.OK);
    }

    @GetMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<?> getUpdateWarehouse(@PathVariable("id") int id){
        WarehouseWithResponsibles warehouseWithResponsibles;
        try{
            List<Responsible> responsibles = responsibleService.findAllResponsibles();
            Warehouse warehouse = warehouseService.findWarehouseById(id);

            warehouseWithResponsibles = new WarehouseWithResponsibles(warehouse,responsibles);

        }catch (Exception e){
            return new ResponseEntity<>(new MessageResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(warehouseWithResponsibles, HttpStatus.OK);
    }

    @PutMapping("/update")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<?> updateWarehouse(@RequestBody @Valid Warehouse warehouse, BindingResult bindingResult){
        if(bindingResult.hasErrors()) {
            return new ResponseEntity(new MessageResponse(bindingService.getErrors(bindingResult)), HttpStatus.BAD_REQUEST);
        }
        try{
            warehouseService.saveWarehouse(warehouse);
        } catch (Exception e){
            return handlingSaveException(e);
        }

        return new ResponseEntity<>(new MessageResponse("Данные о складе успешно обновлены!"), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    private ResponseEntity<?> handlingSaveException(Exception exception){
        String errorMessage = exception.getMessage();
        if(exception instanceof DataIntegrityViolationException){
            errorMessage = "Уже имеется склад с таким адресом";
        }
        return new ResponseEntity<>(new MessageResponse(errorMessage), HttpStatus.NOT_FOUND);
    }

    @GetMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAddWarehouse(){
        List<Responsible> responsibles;
        try{
            responsibles = responsibleService.findAllResponsibles();
        }catch(Exception e){
            return new ResponseEntity<>(new MessageResponse("Ошибка получения списка ответственных"), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(responsibles, HttpStatus.OK);
    }


    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addWarehouse(@RequestBody @Valid Warehouse warehouse, BindingResult bindingResult){

        if(bindingResult.hasErrors()) {
            return new ResponseEntity(new MessageResponse(bindingService.getErrors(bindingResult)), HttpStatus.BAD_REQUEST);
        }
        try{
            warehouseService.saveWarehouse(warehouse);
        }catch(Exception e){
            handlingSaveException(e);
        }

        return new ResponseEntity<>(new MessageResponse("Склад успешно добавлен!"), HttpStatus.CREATED);
    }
}
