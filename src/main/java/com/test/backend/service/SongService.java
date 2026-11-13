package com.test.backend.service;

import com.test.backend.dto.SongDTO;
import com.test.backend.entity.Song;
import com.test.backend.repository.SongRepository;
import com.test.backend.utils.EntityDTOMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class SongService {

    @Autowired
    private SongRepository songRepository;

    public List<SongDTO> getAllSongs() {
        return songRepository.findAll().stream()
                .filter(song -> !Boolean.FALSE.equals(song.getActive()))
                .map(song -> EntityDTOMapper.toDTO(song, SongDTO.class))
                .toList();
    }

    public List<SongDTO> getAllSongsForAdmin() {
        return songRepository.findAll().stream()
                .map(song -> EntityDTOMapper.toDTO(song, SongDTO.class))
                .toList();
    }

    public SongDTO getSongById(String id) {
        return EntityDTOMapper.toDTO(findSong(id), SongDTO.class);
    }

    public SongDTO createSong(SongDTO songDTO) {
        Song saved = songRepository.save(EntityDTOMapper.toEntity(songDTO, Song.class));
        return EntityDTOMapper.toDTO(saved, SongDTO.class);
    }

    public List<SongDTO> createSongs(List<SongDTO> songDTOs) {
        List<Song> songs = songDTOs.stream().map(dto -> EntityDTOMapper.toEntity(dto, Song.class)).toList();
        return songRepository.saveAll(songs).stream().map(song -> EntityDTOMapper.toDTO(song, SongDTO.class)).toList();
    }

    public SongDTO updateSong(String id, SongDTO songDTO) {
        Song existing = findSong(id);
        BeanUtils.copyProperties(songDTO, existing, "id");
        existing.setId(id);
        return EntityDTOMapper.toDTO(songRepository.save(existing), SongDTO.class);
    }

    public void deleteSong(String id) {
        Song existing = findSong(id);
        existing.setActive(false);
        songRepository.save(existing);
    }

    private Song findSong(String id) {
        return songRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Song not found"));
    }
}
