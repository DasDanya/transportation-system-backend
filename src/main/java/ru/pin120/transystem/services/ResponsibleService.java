package ru.pin120.transystem.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.pin120.transystem.exceptions.FileIsNotImageException;
import ru.pin120.transystem.exceptions.ResponsibleNotFoundException;
import ru.pin120.transystem.models.Responsible;
import ru.pin120.transystem.repositories.ResponsibleRepository;


import java.util.Comparator;
import java.util.List;
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

        return responsibleRepository
                .findAll()
                .stream()
                .sorted(Comparator.comparing(Responsible::getSurname))
                .collect(Collectors.toList());
    }

    public Responsible findResponsibleById(int id){
        return responsibleRepository.findResponsibleById(id)
                .orElseThrow(() -> new ResponsibleNotFoundException("Ответственный с id "+id+" не был найден"));
    }


    public Responsible addResponsible(Responsible responsible, MultipartFile photo) throws Exception {

        setPhotoResponsible(responsible,photo);
        return responsibleRepository.save(responsible);
    }

    public Responsible updateResponsible(Responsible responsible, MultipartFile photo) throws Exception {

        if(photo != null){
            setPhotoResponsible(responsible,photo);
        }

        return responsibleRepository.save(responsible);
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
