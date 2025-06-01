package com.controller;

import com.dao.CategoryDAO;
import com.dao.DeviceDAO;
import com.model.Device;
import com.model.Category;
import com.model.User;

import java.sql.SQLException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/devices/*")
public class DeviceController extends HttpServlet {

    private DeviceDAO deviceDAO;
    private CategoryDAO categoryDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        deviceDAO = new DeviceDAO();
        categoryDAO = new CategoryDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            listDevices(request, response);
        } else if (pathInfo.equals("/search")) {
            searchDevices(request, response);
        } else if (pathInfo.equals("/create")) {
            showCreateForm(request, response);
        } else if (pathInfo.equals("/edit")) {
            showEditForm(request, response);
        } else if (pathInfo.equals("/remove")) {
            handleDelete(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null || !user.isStaff()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Only staff users are allowed to perform this action.");
            return;
        }

        if (pathInfo == null || pathInfo.equals("/create")) {
            createDevice(request, response);
        } else if (pathInfo.equals("/update")) {
            updateDevice(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    // ==== Handler Methods ====

    private void listDevices(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<Device> devices = deviceDAO.getAllDevices();
            List<Category> categories = categoryDAO.getAllCategories();

            request.setAttribute("devices", devices);
            request.setAttribute("categories", categories);

            request.getRequestDispatcher("/BrowseDevices.jsp").forward(request, response);
        } catch (Exception e) {
            throw new ServletException("Error listing devices", e);
        }
    }

    private void searchDevices(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String searchTerm = request.getParameter("search");
        String categoryParam = request.getParameter("categoryId");

        Integer categoryId = null;
        if (categoryParam != null && !categoryParam.isEmpty()) {
            try {
                categoryId = Integer.parseInt(categoryParam);
            } catch (NumberFormatException ignored) {}
        }

        try {
            List<Device> devices = deviceDAO.searchDevices(searchTerm, categoryId);
            List<Category> categories = categoryDAO.getAllCategories();

            request.setAttribute("devices", devices);
            request.setAttribute("categories", categories);

            request.getRequestDispatcher("/BrowseDevices.jsp").forward(request, response);
        } catch (Exception e) {
            throw new ServletException("Error searching devices", e);
        }
    }

    private void createDevice(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int categoryId = Integer.parseInt(request.getParameter("categoryId"));
            String deviceName = request.getParameter("deviceName");
            String description = request.getParameter("description");
            BigDecimal price = new BigDecimal(request.getParameter("price"));
            int stockQuantity = Integer.parseInt(request.getParameter("stockQuantity"));

            Device device = new Device(categoryId, deviceName, description, price, stockQuantity);
            deviceDAO.createDevice(device);

            response.sendRedirect(request.getContextPath() + "/devices/");
        } catch (Exception e) {
            throw new ServletException("Error creating device", e);
        }
    }

    private void updateDevice(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int deviceId = Integer.parseInt(request.getParameter("id"));
            int categoryId = Integer.parseInt(request.getParameter("categoryId"));
            String name = request.getParameter("name");
            String description = request.getParameter("description");
            BigDecimal price = new BigDecimal(request.getParameter("price"));
            int stock = Integer.parseInt(request.getParameter("stock"));

            Device device = new Device(deviceId, categoryId, name, description, price, stock);
            deviceDAO.updateDevice(device);

            response.sendRedirect(request.getContextPath() + "/devices/");
        } catch (Exception e) {
            throw new ServletException("Error updating device", e);
        }
    }

    private void handleDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = request.getParameter("id");
        if (idParam == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No device ID provided.");
            return;
        }

        try {
            int deviceId = Integer.parseInt(idParam);
            deviceDAO.deleteDevice(deviceId);
            response.sendRedirect(request.getContextPath() + "/devices/");
        } catch (Exception e) {
            throw new ServletException("Error deleting device", e);
        }
    }

    private void showCreateForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<Category> categories = categoryDAO.getAllCategories();
            request.setAttribute("categories", categories);
            request.getRequestDispatcher("/CreateDevice.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException("Error loading device form", e);
        }
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = request.getParameter("id");
        if (idParam == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No device ID provided.");
            return;
        }

        try {
            int deviceId = Integer.parseInt(idParam);
            Device device = deviceDAO.getDeviceById(deviceId);
            List<Category> categories = categoryDAO.getAllCategories();

            if (device == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Device not found.");
                return;
            }

            request.setAttribute("device", device);
            request.setAttribute("categories", categories);
            request.getRequestDispatcher("/EditDevice.jsp").forward(request, response);
        } catch (Exception e) {
            throw new ServletException("Error loading device for editing", e);
        }
    }
}



