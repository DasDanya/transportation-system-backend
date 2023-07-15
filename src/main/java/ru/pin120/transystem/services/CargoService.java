package ru.pin120.transystem.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.pin120.transystem.basics.Converter;
import ru.pin120.transystem.exceptions.CargoNotFoundException;
import ru.pin120.transystem.exceptions.FileIsNotImageException;
import ru.pin120.transystem.models.Cargo;
import ru.pin120.transystem.models.CargoPhoto;
import ru.pin120.transystem.repositories.CargoRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class CargoService {

    private final CargoRepository cargoRepository;

    private final FileService fileService;

    public CargoService(CargoRepository cargoRepository, FileService fileService) {
        this.cargoRepository = cargoRepository;
        this.fileService = fileService;
    }

    public List<Cargo> findAllCargos(){
        List<Cargo> cargos = cargoRepository
                .findAll()
                .stream()
                .sorted(Comparator.comparing(Cargo::getName))
                .collect(Collectors.toList());

        return cargos;
    }

    public Cargo findCargoById(int id){
        Cargo cargo = cargoRepository.findCargoById(id)
                .orElseThrow(()-> new CargoNotFoundException("Груз с порядковым номером "+id+" не был найден"));

        return cargo;
    }

    public List<Cargo> findByField(String field, String value){
        List<Cargo> cargos = new ArrayList<>();
        Predicate<Cargo> filter = null;

        if(field.equals("name")){
           filter = c->c.getName().equals(value);
        } else if(field.equals("category")){
            filter = c->c.getCategory().equals(value);
        } else{
            Integer convertedValue = Converter.tryValueOfInteger(value);
            if(convertedValue != null){
                if(field.equals("startWarehouse")){
                    filter = c-> c.getStartWarehouse().getId().equals(convertedValue);
                } else if(field.equals("actualWarehouse")){
                    filter = c->c.getActualWarehouse() != null && c.getActualWarehouse().getId().equals(convertedValue);
                } else{
                    filter = c->c.getEndWarehouse().getId().equals(convertedValue);
                }
            }
        }

        if(filter != null){
            cargos = findAllCargos()
                    .stream()
                    .filter(filter)
                    .collect(Collectors.toList());
        }

        return cargos;
    }


    public List<Cargo> findByFieldWithConditional(String field, String value, String conditional){
        List<Cargo> cargos = new ArrayList<>();
        Predicate<Cargo> filter = null;
        if(field.equals("cost")){
            BigDecimal cost = Converter.tryConvertToBigDecimal(value);
            if(cost != null){
                switch(conditional){
                    case "less-than":
                        filter = c->c.getCost().compareTo(cost) < 0;
                        break;
                    case "greater-than":
                        filter = c->c.getCost().compareTo(cost) > 0;
                        break;
                    case "less-than-or-equals":
                        filter = c->c.getCost().compareTo(cost) <= 0;
                        break;
                    case "greater-than-or-equals":
                        filter = c->c.getCost().compareTo(cost) >= 0;
                        break;
                    case "not-equals":
                        filter = c->c.getCost().compareTo(cost) != 0;
                        break;
                    default:
                        filter = c->c.getCost().compareTo(cost) == 0;
                }
            }
        }else{
            int count = Converter.tryParseInt(value);
            if(count != -1){
                switch (conditional){
                    case "less-than":
                        filter = c->c.getCount() < count;
                        break;
                    case "greater-than":
                        filter = c->c.getCount() > count;
                        break;
                    case "less-than-or-equals":
                        filter = c->c.getCount() <= count;
                        break;
                    case "greater-than-or-equals":
                        filter = c->c.getCount() >= count;
                        break;
                    case "not-equals":
                        filter = c->c.getCount() != count;
                        break;
                    default:
                        filter = c->c.getCount() == count;
                }
            }
        }

        if(filter != null){
            cargos = findAllCargos()
                    .stream()
                    .filter(filter)
                    .collect(Collectors.toList());
        }

        return cargos;
    }
    public void addCargo(Cargo cargo, List<MultipartFile> photos) throws Exception {
        setCargoPhotos(cargo,photos);
        cargoRepository.save(cargo);
    }

    private void setCargoPhotos(Cargo cargo, List<MultipartFile> photos) throws Exception {
        List<CargoPhoto> cargoPhotos = new ArrayList<>();

        for(MultipartFile photo: photos){
            if(!fileService.fileIsImage(photo)){
                throw new FileIsNotImageException(""+photo.getOriginalFilename()+" не подходит для фотографии груза");
            }else{
                byte[] photoAsBytes = photo.getBytes();

                CargoPhoto cargoPhoto = new CargoPhoto(photoAsBytes);
                cargoPhotos.add(cargoPhoto);
            }

            cargo.setPhotos(cargoPhotos);
        }
    }

    public void deleteCargo(int id){
        cargoRepository.deleteCargoById(id);
    }
}
