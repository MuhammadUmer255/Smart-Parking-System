import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

class SmartParkingSystem extends JFrame {
    private static final int rows = 4, cols = 4;

    HashMap<String, Vehicle> parkedVehicles = new HashMap<>();
    LinkedList<String> logList = new LinkedList<>();
    PriorityQueue<Vehicle> vipQueue = new PriorityQueue<>((a, b) -> b.number.compareTo(a.number));
    Queue<String> regularQueue = new LinkedList<>();

    int[][] carSlots = new int[rows][cols];
    int[][] bikeSlots = new int[rows][cols];
    int[][] truckSlots = new int[rows][cols];

    JPanel actionPanel, parkingPanel;
    JComboBox<String> vehicleTypeCombo;
    String reservationMode = "add";

    public SmartParkingSystem() {
        setTitle("Smart Parking System with DSA");
        setSize(900, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel topButtons = new JPanel(new FlowLayout());
        JButton btnVehicle = new JButton("Vehicle Entry");
        JButton btnCheckout = new JButton("Check Out");
        JButton btnStatus = new JButton("Parking Status");
        JButton btnReserve = new JButton("Reservation Parking");
        JButton btnQueue = new JButton("View Queues");

        topButtons.add(btnVehicle);
        topButtons.add(btnCheckout);
        topButtons.add(btnStatus);
        topButtons.add(btnReserve);
        topButtons.add(btnQueue);
        add(topButtons, BorderLayout.NORTH);

        actionPanel = new JPanel(new FlowLayout());
        parkingPanel = new JPanel();
        add(actionPanel, BorderLayout.CENTER);
        add(parkingPanel, BorderLayout.SOUTH);

        btnVehicle.addActionListener(e -> showVehicleEntry());
        btnReserve.addActionListener(e -> showReservationLayout());
        btnStatus.addActionListener(e -> showParkedStatus());
        btnCheckout.addActionListener(e -> checkoutVehicle());
        btnQueue.addActionListener(e -> showQueues());

        preloadQueue();
        setVisible(true);
    }

    private void preloadQueue() {
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                regularQueue.offer("car:" + i + "," + j);
    }

    private void showVehicleEntry() {
        actionPanel.removeAll();
        parkingPanel.removeAll();

        JTextField typeField = new JTextField(10);
        JTextField numberField = new JTextField(10);
        JButton showSlots = new JButton("Show Slots");

        actionPanel.add(new JLabel("Vehicle Type (car/bike/truck):"));
        actionPanel.add(typeField);
        actionPanel.add(new JLabel("Vehicle Number:"));
        actionPanel.add(numberField);
        actionPanel.add(showSlots);

        showSlots.addActionListener(e -> {
            String type = typeField.getText().trim().toLowerCase();
            String number = numberField.getText().trim().toUpperCase();

            if (!type.matches("car|bike|truck") || number.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Invalid input!");
                return;
            }

            if (parkedVehicles.containsKey(number)) {
                Vehicle v = parkedVehicles.get(number);
                JOptionPane.showMessageDialog(this,
                        "This vehicle (" + number + ") is already parked at: " + v.slot,
                        "Duplicate Parking Attempt",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            displaySlotSelectionForParking(type, number);
        });

        refreshPanels();
    }

    private void displaySlotSelectionForParking(String type, String number) {
        parkingPanel.removeAll();

        JLabel title = new JLabel("Select a slot to park your " + type.toUpperCase());
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setPreferredSize(new Dimension(800, 30));
        parkingPanel.setLayout(new BorderLayout());

        int[][] slots = getSlotArray(type);
        JPanel grid = new JPanel(new GridLayout(rows, cols, 5, 5));
        int slotCounter = 1;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                String slotName = type.substring(0, 1).toUpperCase() + type.substring(1) + "-" + slotCounter;
                JButton btn = new JButton(slotName);
                btn.setPreferredSize(new Dimension(80, 40));

                if (slots[i][j] == 0) {
                    btn.setBackground(Color.GREEN);
                    final int row = i, col = j;
                    btn.addActionListener(e -> {
                        slots[row][col] = 1;
                        Vehicle v = new Vehicle(type, number);
                        v.slot = slotName;
                        parkedVehicles.put(number, v);

                        if (number.startsWith("VIP")) {
                            vipQueue.offer(v);
                        } else {
                            regularQueue.offer(type + ":" + row + "," + col);
                        }

                        btn.setText(number);
                        btn.setBackground(Color.RED);
                        btn.setEnabled(false);
                        logList.add("Entry: " + number + " at " + slotName);

                        JOptionPane.showMessageDialog(this, "Vehicle " + number + " parked at " + slotName);
                        for (Component comp : btn.getParent().getComponents()) {
                            if (comp instanceof JButton && comp != btn) {
                                JButton otherBtn = (JButton) comp;
                                if (otherBtn.getBackground().equals(Color.GREEN)) {
                                    otherBtn.setEnabled(false);
                                }
                            }
                        }
                    });
                } else if (slots[i][j] == 1) {
                    btn.setBackground(Color.RED);
                    btn.setEnabled(false);
                    btn.setText(getVehicleNumberAt(type, i, j));
                } else {
                    btn.setBackground(Color.BLUE);
                    btn.setEnabled(false);
                }

                grid.add(btn);
                slotCounter++;
            }
        }

        parkingPanel.add(title, BorderLayout.NORTH);
        parkingPanel.add(grid, BorderLayout.CENTER);
        refreshPanels();
    }

    private String getVehicleNumberAt(String type, int row, int col) {
        for (Vehicle v : parkedVehicles.values()) {
            if (v.type.equals(type)) {
                String[] slotParts = v.slot.split("-");
                int slotNum = Integer.parseInt(slotParts[1]);
                int i = (slotNum - 1) / cols;
                int j = (slotNum - 1) % cols;
                if (i == row && j == col) return v.number;
            }
        }
        return "Occupied";
    }

    private void showParkedStatus() {
        actionPanel.removeAll();
        parkingPanel.removeAll();

        JTextArea area = new JTextArea(20, 70);
        area.setEditable(false);
        area.setText("Parking Status Overview:\n\n");

        boolean hasData = false;

        if (!parkedVehicles.isEmpty()) {
            area.append(" Parked Vehicles:\n");
            for (Vehicle v : parkedVehicles.values()) {
                area.append(v + "\n");
            }
            area.append("\n");
            hasData = true;
        }

        String[] types = {"car", "bike", "truck"};
        for (String type : types) {
            int[][] slots = getSlotArray(type);
            StringBuilder reservedList = new StringBuilder();
            boolean foundReserved = false;
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    if (slots[i][j] == 2) {
                        foundReserved = true;
                        reservedList.append("Reserved Slot → Type: ").append(type.toUpperCase())
                                .append(" at (Row ").append(i + 1).append(", Col ").append(j + 1).append(")\n");
                    }
                }
            }
            if (foundReserved) {
                area.append(" Reserved Slots for " + type.toUpperCase() + ":\n" + reservedList + "\n");
                hasData = true;
            }
        }

        area.append("\n--- Activity Log ---\n");
        for (String log : logList) {
            area.append(log + "\n");
        }

        if (!hasData && logList.isEmpty()) area.setText("No vehicles parked or reserved.");
        actionPanel.add(new JScrollPane(area));
        refreshPanels();
    }

    private void checkoutVehicle() {
        actionPanel.removeAll();
        parkingPanel.removeAll();

        JTextField numberField = new JTextField(10);
        JButton removeBtn = new JButton("Check Out");

        actionPanel.add(new JLabel("Enter Vehicle Number:"));
        actionPanel.add(numberField);
        actionPanel.add(removeBtn);

        removeBtn.addActionListener(e -> {
            String num = numberField.getText().trim().toUpperCase();
            if (!parkedVehicles.containsKey(num)) {
                JOptionPane.showMessageDialog(this, "Vehicle not found!");
                return;
            }

            Vehicle v = parkedVehicles.get(num);
            String[] slotParts = v.slot.split("-");
            int slotNum = Integer.parseInt(slotParts[1]);
            int i = (slotNum - 1) / cols;
            int j = (slotNum - 1) % cols;

            getSlotArray(v.type)[i][j] = 0;
            parkedVehicles.remove(num);

            if (num.startsWith("VIP")) {
                vipQueue.remove(v);
            }

            logList.add("Exit: " + num + " from " + v.slot);
            JOptionPane.showMessageDialog(this, "Vehicle checked out from " + v.slot);
            numberField.setText("");
        });

        refreshPanels();
    }

    private void showReservationLayout() {
        actionPanel.removeAll();
        parkingPanel.removeAll();
        reservationMode = "add";

        String[] types = {"Car", "Bike", "Truck"};
        vehicleTypeCombo = new JComboBox<>(types);

        JButton showBtn = new JButton("Show Parking");
        JButton addReservationBtn = new JButton("Add Reservation");
        JButton cancelReservationBtn = new JButton("Cancel Reservation");

        actionPanel.add(new JLabel("Select Type:"));
        actionPanel.add(vehicleTypeCombo);
        actionPanel.add(showBtn);
        actionPanel.add(addReservationBtn);
        actionPanel.add(cancelReservationBtn);

        showBtn.addActionListener(e -> {
            reservationMode = "add";
            displayGrid(((String) vehicleTypeCombo.getSelectedItem()).toLowerCase());
        });

        addReservationBtn.addActionListener(e -> {
            reservationMode = "add";
            displayGrid(((String) vehicleTypeCombo.getSelectedItem()).toLowerCase());
        });

        cancelReservationBtn.addActionListener(e -> {
            reservationMode = "cancel";
            displayGrid(((String) vehicleTypeCombo.getSelectedItem()).toLowerCase());
        });

        refreshPanels();
    }

    private void displayGrid(String type) {
        parkingPanel.removeAll();

        JLabel title = new JLabel(type.substring(0, 1).toUpperCase() + type.substring(1) + " Parking Slots");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setPreferredSize(new Dimension(800, 30));
        parkingPanel.setLayout(new BorderLayout());

        int[][] slots = getSlotArray(type);
        JPanel grid = new JPanel(new GridLayout(rows, cols, 5, 5));
        int slotCounter = 1;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                String slotLabel = type.substring(0, 1).toUpperCase() + type.substring(1) + "-" + slotCounter;
                JButton btn = new JButton(slotLabel);
                btn.setPreferredSize(new Dimension(80, 40));
                final int row = i, col = j;

                if (slots[i][j] == 0) {
                    btn.setBackground(Color.GREEN);
                    if (reservationMode.equals("add")) {
                        btn.addActionListener(e -> {
                            slots[row][col] = 2;
                            btn.setBackground(Color.BLUE);
                            btn.setEnabled(false);
                            JOptionPane.showMessageDialog(this, slotLabel + " reserved.");
                        });
                    } else {
                        btn.setEnabled(false);
                    }
                } else if (slots[i][j] == 1) {
                    btn.setBackground(Color.RED);
                    btn.setEnabled(false);
                } else if (slots[i][j] == 2) {
                    btn.setBackground(Color.BLUE);
                    if (reservationMode.equals("cancel")) {
                        btn.addActionListener(e -> {
                            int confirm = JOptionPane.showConfirmDialog(this,
                                    "Cancel reservation for " + slotLabel + "?",
                                    "Confirm", JOptionPane.YES_NO_OPTION);
                            if (confirm == JOptionPane.YES_OPTION) {
                                slots[row][col] = 0;
                                btn.setBackground(Color.GREEN);
                                btn.setEnabled(false);
                                btn.setText(slotLabel);
                                JOptionPane.showMessageDialog(this, "Reservation cancelled.");
                            }
                        });
                    } else {
                        btn.setEnabled(false);
                    }
                }

                grid.add(btn);
                slotCounter++;
            }
        }

        parkingPanel.add(title, BorderLayout.NORTH);
        parkingPanel.add(grid, BorderLayout.CENTER);
        refreshPanels();
    }

    private void showQueues() {
        actionPanel.removeAll();
        parkingPanel.removeAll();

        JTextArea area = new JTextArea(15, 50);
        area.setEditable(false);
        area.append("--- VIP Queue (Priority) ---\n");
        for (Vehicle v : vipQueue) {
            area.append(v.number + "\n");
        }

        area.append("\n--- Regular Queue ---\n");
        for (String slot : regularQueue) {
            area.append(slot + "\n");
        }

        actionPanel.add(new JScrollPane(area));
        refreshPanels();
    }

    private int[][] getSlotArray(String type) {
        switch (type) {
            case "car": return carSlots;
            case "bike": return bikeSlots;
            case "truck": return truckSlots;
            default: return new int[rows][cols];
        }
    }

    private void refreshPanels() {
        actionPanel.revalidate();
        actionPanel.repaint();
        parkingPanel.revalidate();
        parkingPanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SmartParkingSystem::new);
    }

    class Vehicle {
        String type, number, slot;
        public Vehicle(String type, String number) {
            this.type = type;
            this.number = number;
        }
        public String toString() {
            return number + " [" + type + "] @ " + slot;
        }
    }
}