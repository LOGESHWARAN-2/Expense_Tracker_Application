package com.isa.expensetracker.ui;

import com.isa.expensetracker.dao.ExpenseDao;
import com.isa.expensetracker.model.Category;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign2.MaterialDesignC;
import org.kordamp.ikonli.materialdesign2.MaterialDesignS;

import java.math.BigDecimal;

public class ExpenseFormController {
    @FXML private ComboBox<Category> cbCategory;
    @FXML private TextField tfAmount;
    @FXML private DatePicker dpDate;
    @FXML private TextField tfMerchant;
    @FXML private TextArea taNote;
    @FXML private Button btnSave, btnReset;

    private final ExpenseDao dao = new ExpenseDao();

    @FXML
    private void initialize() {
        // Load real categories from DB
        cbCategory.getItems().setAll(dao.listCategories());
        dpDate.setValue(java.time.LocalDate.now());

        // Add icons to buttons
        btnSave.setGraphic(new FontIcon(MaterialDesignS.SHIELD_CHECK));
        btnReset.setGraphic(new FontIcon(MaterialDesignC.CLOSE_CIRCLE_OUTLINE));

        // Event handlers
        btnReset.setOnAction(e -> resetForm());
        btnSave.setOnAction(e -> onSave());
    }

    private void onSave() {
        Category cat = cbCategory.getValue();
        if (cat == null) {
            warn("Please select a category."); return;
        }
        BigDecimal amount;
        try {
            amount = new BigDecimal(tfAmount.getText().trim());
            if (amount.signum() <= 0) throw new NumberFormatException("must be positive");
        } catch (Exception ex) {
            warn("Enter a valid, positive amount."); return;
        }
        var date = dpDate.getValue();
        if (date == null) { warn("Please pick a date."); return; }

        String merchant = tfMerchant.getText() == null ? "" : tfMerchant.getText().trim();
        String note = taNote.getText() == null ? "" : taNote.getText().trim();

        try {
            dao.insertExpense(cat.id(), amount, "INR", date, merchant, note);
            info("Expense saved successfully!");
            resetForm();
        } catch (Exception ex) {
            error("Failed to save expense:\n" + ex.getMessage());
        }
    }

    private void resetForm() {
        cbCategory.getSelectionModel().clearSelection();
        tfAmount.clear();
        dpDate.setValue(java.time.LocalDate.now());
        tfMerchant.clear();
        taNote.clear();
    }

    private void warn(String msg)  { show(Alert.AlertType.WARNING, msg); }
    private void info(String msg)  { show(Alert.AlertType.INFORMATION, msg); }
    private void error(String msg) { show(Alert.AlertType.ERROR, msg); }

    private void show(Alert.AlertType type, String msg) {
        var a = new Alert(type, msg, ButtonType.OK);
        a.setHeaderText(null);
        a.setTitle("Expense Tracker");
        a.showAndWait();
    }
}
