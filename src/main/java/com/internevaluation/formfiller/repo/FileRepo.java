package com.internevaluation.formfiller.repo;

import com.internevaluation.formfiller.entity.FileList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileRepo extends JpaRepository<FileList,Integer> {
    public List<FileList> findAllByUsername(String username);
    public FileList findByFilename(String filename);
    public List<FileList> findAllByEmail(String email);

    public List<FileList> findAllByFilename(String filename);
}
