package ru.pin120.transystem.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.pin120.transystem.models.Responsible;
import ru.pin120.transystem.models.Warehouse;
import ru.pin120.transystem.repositories.WarehouseRepository;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;

    @Autowired
    public WarehouseService(WarehouseRepository warehouseRepository) {
        this.warehouseRepository = warehouseRepository;
    }

    public List<Warehouse> findAllWarehouses(){

        List<Warehouse> warehouses =  warehouseRepository
                .findAll()
                .stream()
                .sorted(Comparator.comparing(Warehouse::getId).reversed())
                .collect(Collectors.toList());

       removeInfiniteNesting(warehouses);

        return warehouses;
    }

    private void removeInfiniteNesting(List<Warehouse> warehouses){
        for(Warehouse warehouse: warehouses){
            removeInfiniteNesting(warehouse);
        }
    }

     private void removeInfiniteNesting(Warehouse warehouse){
         if(warehouse.getResponsible() != null) {
             warehouse.getResponsible().setWarehouses(null);
         }
     }

    public void addWarehouse(Warehouse warehouse){
         warehouseRepository.save(warehouse);
    }

}
