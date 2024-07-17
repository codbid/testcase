package com.example.testcase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class FileController {

    private final FIleRepository fileRepository;

    @Autowired
    public FileController(FIleRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    @PostMapping("/testcase")
    public ResponseEntity<Object> testcasePost(@RequestBody File file) {
        return fileRepository.addFile(file);
    }

    @GetMapping("/testcase")
    public ResponseEntity<Object> testcaseGet(@RequestParam int id) {
        return fileRepository.getFileById(id);
    }

    @GetMapping("/testcase/all")
    public ResponseEntity<Object> testcaseGetAll(@RequestParam(defaultValue = "5") int size, @RequestParam(defaultValue = "0") int page) {
        return fileRepository.getAllFiles(size, page);
    }
}