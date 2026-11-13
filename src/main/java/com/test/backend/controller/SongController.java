package com.test.backend.controller;

import com.test.backend.dto.SongDTO;
import com.test.backend.service.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/songs")
public class SongController {

    @Autowired
    private SongService songService;

    @GetMapping
    public ResponseEntity<List<SongDTO>> getAll() {
        return new ResponseEntity<>(songService.getAllSongs(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SongDTO> getById(@PathVariable String id) {
        return new ResponseEntity<>(songService.getSongById(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<SongDTO> create(@RequestBody SongDTO songDTO) {
        SongDTO created = songService.createSong(songDTO);
        return ResponseEntity.created(URI.create("/api/v1/songs/" + created.getId())).body(created);
    }

    @PostMapping("/batch")
    public ResponseEntity<List<SongDTO>> createBatch(@RequestBody List<SongDTO> songs) {
        return new ResponseEntity<>(songService.createSongs(songs), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SongDTO> update(@PathVariable String id, @RequestBody SongDTO songDTO) {
        return new ResponseEntity<>(songService.updateSong(id, songDTO), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        songService.deleteSong(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

