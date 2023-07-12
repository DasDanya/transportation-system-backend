package ru.pin120.transystem.controllers;


import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.pin120.transystem.models.Responsible;
import ru.pin120.transystem.models.Warehouse;
import ru.pin120.transystem.payload.response.MessageResponse;
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
    public ResponseEntity<?> getAllWarehouses(){
        List<Warehouse> warehouses;
        try{
            warehouses = warehouseService.findAllWarehouses();
        } catch(Exception e){

            return new ResponseEntity<>(new MessageResponse("Ошибка получения списка складов"), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(warehouses, HttpStatus.OK);
    }


    @GetMapping("/add")
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
    public ResponseEntity<?> addWarehouse(@RequestBody @Valid Warehouse warehouse, BindingResult bindingResult){

        if(bindingResult.hasErrors()) {
            return new ResponseEntity(new MessageResponse(bindingService.getErrors(bindingResult)), HttpStatus.BAD_REQUEST);
        }
        try{
            warehouseService.addWarehouse(warehouse);
        }catch(Exception e){
            return new ResponseEntity<>(new MessageResponse("Произошла ошибка добавления склада"), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(new MessageResponse("Склад успешно добавлен!"), HttpStatus.CREATED);
    }

}
