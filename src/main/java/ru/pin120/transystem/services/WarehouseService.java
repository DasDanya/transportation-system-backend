package ru.pin120.transystem.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.pin120.transystem.exceptions.WarehouseNotFoundException;
import ru.pin120.transystem.models.Responsible;
import ru.pin120.transystem.models.Warehouse;
import ru.pin120.transystem.repositories.WarehouseRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
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

    public Warehouse findWarehouseById(int id){
        Warehouse warehouse = warehouseRepository.findWarehouseById(id)
                .orElseThrow(()-> new WarehouseNotFoundException("Склад с номером "+id+" не был найден!"));

        removeInfiniteNesting(warehouse);

        return warehouse;
    }

    public List<Warehouse> findByField(String field, String value){
        List<Warehouse> warehouses = new ArrayList<>();

        if(field.equals("id")){
            Warehouse warehouse = findWarehouseById(Integer.parseInt(value));
            warehouses.add(warehouse);
        }else{
            switch(field){
                case "state":
                    warehouses = findAllWarehouses()
                            .stream()
                            .filter(w-> w.getAddress().getState().equals(value))
                            .collect(Collectors.toList());
                break;
                case "city":
                    warehouses = findAllWarehouses()
                            .stream()
                            .filter(w->w.getAddress().getCity().equals(value))
                            .collect(Collectors.toList());
                    break;
                case "street":
                    warehouses = findAllWarehouses()
                            .stream()
                            .filter(w->w.getAddress().getStreet().equals(value))
                            .collect(Collectors.toList());
                    break;
                case "house":
                    warehouses = findAllWarehouses()
                            .stream()
                            .filter(w->w.getAddress().getHouse().equals(value))
                            .collect(Collectors.toList());
                    break;
                case "responsible":
                    Predicate<Warehouse> filter = w-> w.getResponsible() != null && w.getResponsible().getSurname().startsWith(value);

                    long numberOfSpaces = value
                            .trim()
                            .chars()
                            .filter(c -> c == (int)' ')
                            .count();

                    if(numberOfSpaces != 0) {
                        String[] responsible = value.split(" ");
                        if (numberOfSpaces == 1) {
                            filter = w -> w.getResponsible() != null && w.getResponsible().getSurname().equals(responsible[0]) && w.getResponsible().getName().startsWith(responsible[1]);
                        } else {
                            filter = w -> w.getResponsible() != null && w.getResponsible().getSurname().equals(responsible[0]) && w.getResponsible().getName().equals(responsible[1]) && w.getResponsible().getPatronymic().startsWith(responsible[2]);
                        }
                    }

                    warehouses = findAllWarehouses()
                            .stream()
                            .filter(filter)
                            .collect(Collectors.toList());
                    break;
                default:
                    warehouses = findAllWarehouses();
            }
        }

        return warehouses;
    }

    private void removeInfiniteNesting(List<Warehouse> warehouses){
        for(Warehouse warehouse: warehouses){
            removeInfiniteNesting(warehouse);
        }
    }

     private void removeInfiniteNesting(Warehouse warehouse){
//         if(warehouse.getResponsible() != null) {
//             Set<Warehouse> warehouses = warehouse.getResponsible().getWarehouses();
//             if(warehouses.contains(warehouse)){
//                 warehouses.remove(warehouse);
//             }
//             //warehouse.getResponsible().setWarehouses(null);
//         }
     }

    public void addWarehouse(Warehouse warehouse){
         warehouseRepository.save(warehouse);
    }

    public void deleteWarehouse(int id){
        warehouseRepository.deleteWarehouseById(id);
    }

}
