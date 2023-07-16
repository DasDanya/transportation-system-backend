package ru.pin120.transystem.services;


import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.pin120.transystem.exceptions.WarehouseNotFoundException;
import ru.pin120.transystem.models.Cargo;
import ru.pin120.transystem.models.Warehouse;
import ru.pin120.transystem.repositories.WarehouseRepository;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
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

       //removeInfiniteNesting(warehouses);

        return warehouses;
    }

    public Warehouse findWarehouseById(int id){
        Warehouse warehouse = warehouseRepository.findWarehouseById(id)
                .orElseThrow(()-> new WarehouseNotFoundException("Склад с номером "+id+" не был найден!"));

        //removeInfiniteNesting(warehouse);

        return warehouse;
    }

    public List<Warehouse> findByField(String field, String value){
        List<Warehouse> warehouses = new ArrayList<>();

        if(field.equals("id")){
            Warehouse warehouse = findWarehouseById(Integer.parseInt(value));
            warehouses.add(warehouse);
        }else{
            Predicate<Warehouse> filter = null;
            switch(field){
                case "state":
                    filter = w-> w.getAddress().getState().equals(value);
                    break;
                case "city":
                    filter = w->w.getAddress().getCity().equals(value);
                    break;
                case "street":
                    filter = w->w.getAddress().getStreet().equals(value);
                    break;
                case "house":
                    filter = w->w.getAddress().getHouse().equals(value);
                    break;
                case "responsible":
                    filter = w-> w.getResponsible() != null && w.getResponsible().getSurname().startsWith(value);

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
                    break;
            }
            if(filter != null) {
                warehouses = findAllWarehouses()
                        .stream()
                        .filter(filter)
                        .collect(Collectors.toList());
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

    public void saveWarehouse(Warehouse warehouse){
        warehouseRepository.save(warehouse);
    }

    public void deleteWarehouse(int id){
        warehouseRepository.deleteWarehouseById(id);
    }


    public ByteArrayInputStream generateReportInExcel(int id){
        Warehouse warehouse = findWarehouseById(id);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Грузы");
        Row row = sheet.createRow(0);

        row.createCell(0).setCellValue("Название");
        row.createCell(1).setCellValue("Категория");
        row.createCell(2).setCellValue("Стоимость");
        row.createCell(3).setCellValue("Количество");
        row.createCell(4).setCellValue("Адрес отправки");
        row.createCell(5).setCellValue("Адрес доставки");

        int rowIndex = 1;

        List<Cargo> cargos = warehouse.getCargos()
                .stream()
                .sorted(Comparator.comparing(Cargo::getName))
                .collect(Collectors.toList());

        for(Cargo cargo: cargos){
            Row dataRow = sheet.createRow(rowIndex);

            dataRow.createCell(0).setCellValue(cargo.getName());
            dataRow.createCell(1).setCellValue(cargo.getCategory());
            dataRow.createCell(2).setCellValue(cargo.getCost().toString());
            dataRow.createCell(3).setCellValue(cargo.getCount());

            String startAddress = cargo.getStartWarehouse().getAddress().getState() + ";" + cargo.getStartWarehouse().getAddress().getCity() +
                    ";" + cargo.getStartWarehouse().getAddress().getStreet() + ";" + cargo.getStartWarehouse().getAddress().getHouse();

            dataRow.createCell(4).setCellValue(startAddress);

            String endAddress = cargo.getEndWarehouse().getAddress().getState() + ";" + cargo.getEndWarehouse().getAddress().getCity() +
                    ";" + cargo.getEndWarehouse().getAddress().getStreet() + ";" + cargo.getEndWarehouse().getAddress().getHouse();

            dataRow.createCell(5).setCellValue(endAddress);

            rowIndex++;
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            workbook.write(out);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}
