package com.academiccalender.controller;

import com.academiccalender.model.Labs;
import com.academiccalender.repository.LabRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/labs")
public class LabController {

    @Autowired
    LabRepository labRepository;

    @GetMapping("/getLabs")
    public ResponseEntity<?> getLabs() {

        List<Labs> labs = labRepository.findAll();
        if (labs.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(Map.of("message", "No labs found"));
        }
        return ResponseEntity.ok(labs);
    }
}