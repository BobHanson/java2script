package test.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;

public class TableEdit extends JFrame {

		private JTable table;
	    private DefaultTableModel model;

	    public TableEdit() {
	        setTitle("Single-Click Editable JTable");
	        setSize(550, 320);
	        setDefaultCloseOperation(EXIT_ON_CLOSE);
	        setLayout(new BorderLayout(10, 10));

	        // 1. Setup Columns & Initial Data
	        String[] columns = {"ID (Locked)", "Product Name", "Quantity", "Price"};
	        Object[][] data = {
	            {101, "Mechanical Keyboard", 15, 89.99},
	            {102, "Wireless Mouse", 42, 29.50},
	            {103, "USB-C Hub", 8, 45.00}
	        };

	        // 2. Setup Model (Lock Column 0, Allow Editing on Columns 1-3)
	        model = new DefaultTableModel(data, columns) {
	            @Override
	            public boolean isCellEditable(int row, int column) {
	                return column != 0;
	            }
	        };

	        table = new JTable(model);
	        table.setRowHeight(28);

	        // Save cell edits automatically if the user clicks outside the table
	        table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

	        // 3. Configure Single-Click JTextField Editor
	        JTextField editorField = new JTextField();
	        editorField.setBorder(BorderFactory.createLineBorder(new Color(0, 120, 215), 2));

	        DefaultCellEditor singleClickEditor = new DefaultCellEditor(editorField);
	        singleClickEditor.setClickCountToStart(1); // Activates on 1 click

	        // Apply to every column in the table
	        for (int i = 0; i < table.getColumnCount(); i++) {
	            table.getColumnModel().getColumn(i).setCellEditor(singleClickEditor);
	        }

	        // 4. Listen for Edits
	        model.addTableModelListener(e -> {
	            if (e.getType() == TableModelEvent.UPDATE) {
	                int row = e.getFirstRow();
	                int col = e.getColumn();
	                System.out.printf("Saved [Row %d, Col %d]: %s%n", row, col, model.getValueAt(row, col));
	            }
	        });

	        add(new JScrollPane(table), BorderLayout.CENTER);

	        // 5. Add Row Button
	        JButton addBtn = new JButton("Add Row");
	        addBtn.addActionListener(e -> {
	            int nextId = model.getRowCount() + 101;
	            model.addRow(new Object[]{nextId, "New Item", 1, 0.00});
	        });

	        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
	        southPanel.add(addBtn);
	        add(southPanel, BorderLayout.SOUTH);
	    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TableEdit().setVisible(true));
    }
}
