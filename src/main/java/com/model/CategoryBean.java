package com.model;

public class CategoryBean {
    private Integer categoryID;      // DE-2001: Integer(10)
    private String categoryName;     // DE-2002: Varchar(50)
    private String categoryDescription; // DE-2003: Text(255)
    
    public CategoryBean() {
        this.categoryID = null;
        this.categoryName = "";
        this.categoryDescription = "";
    }
    
    public Integer getCategoryID() {
        return categoryID;
    }
    
    public void setCategoryID(Integer categoryID) {
        this.categoryID = categoryID;
    }
    
    public String getCategoryName() {
        return categoryName;
    }
    
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
    
    public String getCategoryDescription() {
        return categoryDescription;
    }
    
    public void setCategoryDescription(String categoryDescription) {
        this.categoryDescription = categoryDescription;
    }
} 