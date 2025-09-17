package com.example.demo.Repository;

import com.example.demo.Entity.Todo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class TodoRepositoryDBimp {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // 映射数据库结果集到 Todo 对象
    private final RowMapper<Todo> todoRowMapper = new RowMapper<>() {
        @Override
        public Todo mapRow(ResultSet rs, int rowNum) throws SQLException {
            Todo todo = new Todo();
            todo.setId(rs.getLong("id"));
            todo.setText(rs.getString("text"));
            todo.setCompleted(rs.getBoolean("completed"));
            return todo;
        }
    };

    // 查询所有 Todo
    public List<Todo> findAll() {
        String sql = "SELECT * FROM todolist_database";
        return jdbcTemplate.query(sql, todoRowMapper);
    }

    // 根据 ID 查询 Todo
    public Optional<Todo> findById(Long id) {
        String sql = "SELECT * FROM todolist_database WHERE id = ?";
        List<Todo> result = jdbcTemplate.query(sql, todoRowMapper, id);
        return result.stream().findFirst();
    }

    // 新增 Todo
    public int save(Todo todo) {
        String sql = "INSERT INTO todolist_database (text, completed) VALUES (?, ?)";
        return jdbcTemplate.update(sql, todo.getText(), todo.isCompleted());
    }

    // 更新 Todo
    public int update(Long id, Todo todo) {
        String sql = "UPDATE todolist_database SET text = ?, completed = ? WHERE id = ?";
        return jdbcTemplate.update(sql, todo.getText(), todo.isCompleted(), id);
    }

    // 删除 Todo
    public int delete(Long id) {
        String sql = "DELETE FROM todolist_database WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }
}
