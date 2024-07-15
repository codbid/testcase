package com.example.testcase;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Base64;

@Repository
public class FIleRepository {

    FIleRepository() {}

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public int addFile(File file) {
        String sql = "INSERT INTO files (file, title, description) VALUES (?, ?, ?)";
        byte[] binaryFile = Base64.getDecoder().decode(file.getFile());
        jdbcTemplate.update(sql, binaryFile, file.getTitle(), file.getDescription());
        sql = "SELECT COUNT(*) FROM files";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    public File getFileById(int id) {
        String sql = "SELECT * FROM files WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{id}, (rs, rowNum) -> {
            File file = new File();
            file.setId(rs.getInt("id"));
            file.setFile(Base64.getEncoder().encodeToString(rs.getBytes("file")));
            file.setTitle(rs.getString("title"));
            file.setCreation_date(rs.getString("creation_date"));
            file.setDescription(rs.getString("description"));
            return file;
        });
    }
}
