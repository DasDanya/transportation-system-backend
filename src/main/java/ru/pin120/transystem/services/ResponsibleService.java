package ru.pin120.transystem.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.pin120.transystem.exceptions.FileIsNotImageException;
import ru.pin120.transystem.exceptions.ResponsibleNotFoundException;
import ru.pin120.transystem.models.Responsible;
import ru.pin120.transystem.models.Warehouse;
import ru.pin120.transystem.repositories.ResponsibleRepository;


import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ResponsibleService {
    private final ResponsibleRepository responsibleRepository;
    private final FileService fileService;

    @Autowired
    public ResponsibleService(ResponsibleRepository responsibleRepository, FileService fileService) {
        this.responsibleRepository = responsibleRepository;
        this.fileService = fileService;
    }

    public List<Responsible> findAllResponsibles(){

        List<Responsible> responsibles =  responsibleRepository
                .findAll()
                .stream()
                .sorted(Comparator.comparing(Responsible::getSurname))
                .collect(Collectors.toList());

       removeInfiniteNesting(responsibles);

        return responsibles;
    }
    public List<Responsible> findByField(String field, String value){
        List<Responsible> responsibles;

        switch(field){
            case "surname":
                responsibles = responsibleRepository.findResponsibleBySurname(value);
                break;
            case "name":
                responsibles = responsibleRepository.findResponsibleByName(value);
                break;
            case "patronymic":
                responsibles = responsibleRepository.findResponsibleByPatronymic(value);
                break;
            case "phone":
                responsibles = responsibleRepository.findResponsibleByPhone(value);
                break;
            default:
                responsibles = responsibleRepository.findAll();
        }

        removeInfiniteNesting(responsibles);

        return responsibles;
    }

    public Responsible findResponsibleById(int id){
        Responsible responsible =  responsibleRepository.findResponsibleById(id)
                .orElseThrow(() -> new ResponsibleNotFoundException("Ответственный с id "+id+" не был найден"));

        removeInfiniteNesting(responsible);

        return responsible;
    }

    private void removeInfiniteNesting(List<Responsible> responsibles){
        for(Responsible responsible:responsibles){
            removeInfiniteNesting(responsible);
        }
    }
    private void removeInfiniteNesting(Responsible responsible){
//        Set<Warehouse> warehouses = responsible.getWarehouses();
//        for(Warehouse warehouse:warehouses){
////            if(warehouse.getResponsible().equals(responsible)){
////                warehouse.setResponsible(null);
////            }
//            warehouse.setResponsible(null);
        //}
    }

    public void addResponsible(Responsible responsible, MultipartFile photo) throws Exception {

        setPhotoResponsible(responsible,photo);
        responsibleRepository.save(responsible);
    }

    public void updateResponsible(Responsible responsible, MultipartFile photo) throws Exception {

        if(photo != null){
            setPhotoResponsible(responsible,photo);
        }

        responsibleRepository.save(responsible);
    }


    private void setPhotoResponsible(Responsible responsible,MultipartFile photo) throws Exception {
        if(!fileService.fileIsImage(photo)){
            throw new FileIsNotImageException("Данный файл не подходит для фотографии ответственного");
        }

        byte[] photoAsBytes = fileService.convertFile2Bytes(photo);
        responsible.setPhoto(photoAsBytes);
    }


    public void deleteResponsible(int id){
        responsibleRepository.deleteResponsibleById(id);
    }


}
