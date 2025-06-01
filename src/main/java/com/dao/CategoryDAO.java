package com.dao;

import com.model.Category;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.util.DatabaseConnection;

/**
 * Data Access Object for Category entities.
 */
public class CategoryDAO {

    /**
     * Retrieves all categories from the database.
     *
     * @return a List of Category objects
     * @throws SQLException if a database access error occurs
     */
    public List<Category> getAllCategories() throws SQLException {
        String sql = "SELECT CategoryID, CategoryName, CategoryDescription FROM Category";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            List<Category> list = new ArrayList<>();
            while (rs.next()) {
                Category cat = new Category();
                cat.setCategoryId(rs.getInt("CategoryID"));
                cat.setCategoryName(rs.getString("CategoryName"));
                cat.setDescription(rs.getString("CategoryDescription"));
          
                list.add(cat);
            }
            return list;
        }
    }

    /**
     * Retrieves a single category by its ID.
     *
     * @param categoryId the ID of the category
     * @return the Category object, or null if not found
     * @throws SQLException if a database access error occurs
     */
    public Category getCategoryById(int categoryId) throws SQLException {
        String sql = "SELECT CategoryID, CategoryName, CategoryDescription, CreatedAt FROM Category WHERE CategoryID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, categoryId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Category cat = new Category();
                    cat.setCategoryId(rs.getInt("CategoryID"));
                    cat.setCategoryName(rs.getString("CategoryName"));
                    cat.setDescription(rs.getString("CategoryDescription"));
                    cat.setCreatedAt(rs.getTimestamp("CreatedAt"));
                    return cat;
                }
                return null;
            }
        }
    }

    /**
     * Inserts a new category into the database.
     *
     * @param category the Category to create
     * @throws SQLException if a database access error occurs
     */
    public void createCategory(Category category) throws SQLException {
        String sql = "INSERT INTO Category (CategoryName, CategoryDescription, CreatedAt) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, category.getCategoryName());
            stmt.setString(2, category.getDescription());
            stmt.setTimestamp(3, category.getCreatedAt());
            stmt.executeUpdate();
        }
    }

    /**
     * Updates an existing category.
     *
     * @param category the Category to update
     * @throws SQLException if a database access error occurs
     */
    public void updateCategory(Category category) throws SQLException {
        String sql = "UPDATE Category SET CategoryName = ?, CategoryDescription = ? WHERE CategoryID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, category.getCategoryName());
            stmt.setString(2, category.getDescription());
            stmt.setInt(3, category.getCategoryId());
            stmt.executeUpdate();
        }
    }

    /**
     * Deletes a category by its ID.
     *
     * @param categoryId the ID of the category to delete
     * @throws SQLException if a database access error occurs
     */
    public void deleteCategory(int categoryId) throws SQLException {
        String sql = "DELETE FROM Category WHERE CategoryID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, categoryId);
            stmt.executeUpdate();
        }
    }
}
