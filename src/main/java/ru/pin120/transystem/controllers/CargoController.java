package ru.pin120.transystem.controllers;


import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.pin120.transystem.models.Cargo;
import ru.pin120.transystem.models.Warehouse;
import ru.pin120.transystem.payload.response.MessageResponse;
import ru.pin120.transystem.sendModels.CargoWithWarehouses;
import ru.pin120.transystem.services.BindingService;
import ru.pin120.transystem.services.CargoService;
import ru.pin120.transystem.services.WarehouseService;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/cargo")
public class CargoController {

    private final CargoService cargoService;
    private final WarehouseService warehouseService;
    private final BindingService bindingService;

    public CargoController(CargoService cargoService, WarehouseService warehouseService, BindingService bindingService) {
        this.cargoService = cargoService;
        this.warehouseService = warehouseService;
        this.bindingService = bindingService;
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR') or hasRole('USER')")
    public ResponseEntity<?> getCargos(@RequestParam(value = "field",required = false) String field,
                                       @RequestParam(value="value", required = false) String value,
                                       @RequestParam(value="conditional",required = false) String conditional){
        List<Cargo> cargos;
        try{
            if(field == null || value == null) {
                cargos = cargoService.findAllCargos();
            } else{
                if(conditional != null){
                    cargos = cargoService.findByFieldWithConditional(field, value, conditional);
                }else{
                    cargos = cargoService.findByField(field,value);
                }
            }
        }catch(Exception e){
            return new ResponseEntity<>(new MessageResponse("Ошибка получения списка грузов"), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(cargos, HttpStatus.OK);
    }


    @GetMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<?> getDeleteCargo(@PathVariable("id") int id){
        Cargo cargo;
        try{
            cargo = cargoService.findCargoById(id);
        }catch (Exception e){
            return new ResponseEntity<>(new MessageResponse(e.getMessage()),HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(cargo, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    @Transactional
    public ResponseEntity<?> deleteCargo(@PathVariable("id") int id){
        try{
            cargoService.deleteCargo(id);
        }catch (Exception e){
            return new ResponseEntity<>(new MessageResponse("Ошибка удаления груза с порядковым номером "+id+""),HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(new MessageResponse("Груз успешно удален!"), HttpStatus.OK);
    }

    @GetMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<?> getUpdateCargo(@PathVariable("id") int id){
        CargoWithWarehouses cargoWithWarehouses;
        try{
            Cargo cargo = cargoService.findCargoById(id);
            List<Warehouse> warehouses = warehouseService.findAllWarehouses();

            cargoWithWarehouses = new CargoWithWarehouses(cargo,warehouses);

        }catch(Exception e){
            return new ResponseEntity<>(new MessageResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(cargoWithWarehouses, HttpStatus.OK);
    }

    @PutMapping("/update")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<?> updateCargo(@RequestPart("cargo") @Valid Cargo cargo, BindingResult bindingResult,
                                         @RequestPart(value = "photos",required = false) List<MultipartFile> photos){

        if(bindingResult.hasErrors()) {
            return new ResponseEntity(new MessageResponse(bindingService.getErrors(bindingResult)), HttpStatus.BAD_REQUEST);
        }
        try{
            cargoService.updateCargo(cargo,photos);
        }catch (Exception e){
            return new ResponseEntity<>(new MessageResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(new MessageResponse("Данные о грузе успешно обновлены!"), HttpStatus.OK);
    }

    @GetMapping("/add")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<?> getAddCargo(){
        List<Warehouse> warehouses;
        try{
            warehouses = warehouseService.findAllWarehouses();
        } catch (Exception e){
            return new ResponseEntity<>(new MessageResponse("Ошибка получения данных для добавления груза"), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(warehouses, HttpStatus.OK);
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<?> addCargo(@RequestPart("cargo") @Valid Cargo cargo, BindingResult bindingResult,
                                      @RequestPart(value = "photos") List<MultipartFile> photos){

        if(bindingResult.hasErrors()) {
            return new ResponseEntity(new MessageResponse(bindingService.getErrors(bindingResult)), HttpStatus.BAD_REQUEST);
        }
        try{
            cargoService.addCargo(cargo,photos);
        }catch (Exception e){
            return new ResponseEntity<>(new MessageResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(new MessageResponse("Груз успешно добавлен!"), HttpStatus.CREATED);
    }
}
