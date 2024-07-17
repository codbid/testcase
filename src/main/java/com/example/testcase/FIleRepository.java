package com.example.testcase;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Base64;
import java.util.List;

@Repository
public class FIleRepository {

    FIleRepository() {}
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public ResponseEntity<Object> addFile(File file) {
        try {
            String sql = "INSERT INTO files (file, title, description) VALUES (?, ?, ?)";
            byte[] binaryFile = Base64.getDecoder().decode(file.getFile());
            jdbcTemplate.update(sql, binaryFile, file.getTitle(), file.getDescription());
            sql = "SELECT COUNT(*) FROM files";
            return ResponseEntity.status(HttpStatus.CREATED).body(jdbcTemplate.queryForObject(sql, Integer.class));
        }
        catch (DataAccessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to insert file: " + e);
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File must be in base64 format");
        }
    }

    public ResponseEntity<Object> getFileById(int id) {
        String sql = "SELECT * FROM files WHERE id = ?";
        try {
            File file = jdbcTemplate.queryForObject(sql, new Object[]{id}, (rs, rowNum) -> {
                File f = new File();
                f.setId(rs.getInt("id"));
                f.setFile(Base64.getEncoder().encodeToString(rs.getBytes("file")));
                f.setTitle(rs.getString("title"));
                f.setCreation_date(rs.getString("creation_date"));
                f.setDescription(rs.getString("description"));
                return f;
            });
            return ResponseEntity.ok(file);
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File with id " + id + " not found");
        }
    }

    public ResponseEntity<Object> getAllFiles(int limit, int offset) {
        String sql = "SELECT * FROM files ORDER BY creation_date DESC LIMIT ? OFFSET ?";
        List<File> files = jdbcTemplate.query(sql, new Object[]{limit, offset * limit}, (rs, rowNum) -> {
            File f = new File();
            f.setId(rs.getInt("id"));
            f.setFile(Base64.getEncoder().encodeToString(rs.getBytes("file")));
            f.setTitle(rs.getString("title"));
            f.setCreation_date(rs.getString("creation_date"));
            f.setDescription(rs.getString("description"));
            return f;
        });
        return ResponseEntity.ok(files);
    }
}
