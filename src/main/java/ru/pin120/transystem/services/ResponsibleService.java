package ru.pin120.transystem.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.pin120.transystem.exceptions.FileIsNotImageException;
import ru.pin120.transystem.models.Responsible;
import ru.pin120.transystem.repositories.ResponsibleRepository;

import java.io.IOException;
import java.util.List;

@Service
public class ResponsibleService {
    private final ResponsibleRepository responsibleRepository;
    private final FileService fileService;

    @Autowired
    public ResponsibleService(ResponsibleRepository responsibleRepository, FileService fileService) {
        this.responsibleRepository = responsibleRepository;
        this.fileService = fileService;
    }

    public Responsible addResponsible(Responsible responsible, MultipartFile photo) throws Exception {

        if(!fileService.fileIsImage(photo)){
            throw new FileIsNotImageException("Данный файл не подходит для фотографии ответственного");
        }

        byte[] photoAsBytes = fileService.convertFile2Bytes(photo);
        responsible.setPhoto(photoAsBytes);

        return responsibleRepository.save(responsible);
    }

    public List<Responsible> findAllResponsibles(){
        return responsibleRepository.findAll();
    }

    public Responsible updateResponsible(Responsible responsible){
        return responsibleRepository.save(responsible);
    }



    public void deleteResponsible(int id){
        responsibleRepository.deleteResponsibleById(id);
    }


}
