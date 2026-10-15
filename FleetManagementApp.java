
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

public class FleetManagementApp extends JFrame {

    private FleetManager manager;

    private JPanel contentPanel;
    private CardLayout cardLayout;

    private JLabel vehicleCount;
    private JLabel driverCount;
    private JLabel routeCount;
    private JLabel tripCount;

    private JLabel pageTitle;

    private DefaultTableModel vehicleModel;
    private DefaultTableModel driverModel;
    private DefaultTableModel routeModel;
    private DefaultTableModel tripModel;
    private DefaultTableModel recentTripModel;

    private JTable vehicleTable;
    private JTable driverTable;
    private JTable routeTable;
    private JTable tripTable;
    private JTable recentTripTable;

    private JButton activeButton;

    private final Color NAVY = new Color(24, 39, 68);
    private final Color SIDEBAR = new Color(29, 45, 76);
    private final Color BLUE = new Color(48, 108, 190);
    private final Color BACKGROUND = new Color(245, 247, 251);
    private final Color TEXT = new Color(40, 50, 70);
    private final Color MUTED = new Color(115, 125, 145);
    private final Color WHITE = Color.WHITE;

    private final DateTimeFormatter dateFormat =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public FleetManagementApp() {
        manager = new FleetManager();

        setTitle("Fleet Management System");
        setSize(1250, 780);
        setMinimumSize(new Dimension(1000, 650));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        getContentPane().setBackground(BACKGROUND);
        setLayout(new BorderLayout());

        add(createHeader(), BorderLayout.NORTH);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(BACKGROUND);

        contentPanel.add(createDashboard(), "Dashboard");
        contentPanel.add(createVehiclePanel(), "Vehicles");
        contentPanel.add(createDriverPanel(), "Drivers");
        contentPanel.add(createRoutePanel(), "Routes");
        contentPanel.add(createTripPanel(), "Trips");

        add(createSidebar(), BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);

        showPage("Dashboard", null);
        refreshAll();
    }

    // ================= HEADER =================

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(NAVY);
        header.setBorder(BorderFactory.createEmptyBorder(
                17, 25, 17, 25));

        JPanel titles = new JPanel(new GridLayout(2, 1, 0, 5));
        titles.setOpaque(false);

        JLabel title = new JLabel("FLEET MANAGEMENT SYSTEM");
        title.setFont(new Font("Arial", Font.BOLD, 23));
        title.setForeground(WHITE);

        JLabel subtitle = new JLabel(
                "Vehicle Administration  |  Route Scheduling");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 12));
        subtitle.setForeground(new Color(195, 207, 230));

        titles.add(title);
        titles.add(subtitle);

        JLabel version = new JLabel("DESKTOP EDITION");
        version.setFont(new Font("Arial", Font.BOLD, 11));
        version.setForeground(new Color(210, 220, 240));

        header.add(titles, BorderLayout.WEST);
        header.add(version, BorderLayout.EAST);

        return header;
    }

    // ================= SIDEBAR =================

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(SIDEBAR);
        sidebar.setPreferredSize(new Dimension(210, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(
                22, 12, 20, 12));

        JLabel menu = new JLabel("MAIN MENU");
        menu.setFont(new Font("Arial", Font.BOLD, 11));
        menu.setForeground(new Color(165, 182, 210));
        menu.setBorder(BorderFactory.createEmptyBorder(
                0, 12, 15, 0));
        menu.setAlignmentX(Component.LEFT_ALIGNMENT);

        sidebar.add(menu);

        String[] pages = {
                "Dashboard", "Vehicles", "Drivers", "Routes", "Trips"
        };

        String[] symbols = {
                "▦", "▣", "♙", "⇄", "◷"
        };

        for (int i = 0; i < pages.length; i++) {
            final String page = pages[i];

            JButton button = new JButton(
                    symbols[i] + "   " + page);

            button.setFont(new Font("Arial", Font.PLAIN, 14));
            button.setHorizontalAlignment(SwingConstants.LEFT);
            button.setForeground(WHITE);
            button.setBackground(SIDEBAR);
            button.setFocusPainted(false);
            button.setBorderPainted(false);
            button.setOpaque(true);
            button.setMaximumSize(new Dimension(190, 46));
            button.setPreferredSize(new Dimension(190, 46));
            button.setAlignmentX(Component.LEFT_ALIGNMENT);
            button.setCursor(Cursor.getPredefinedCursor(
                    Cursor.HAND_CURSOR));

            button.addActionListener(e -> showPage(page, button));

            button.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    if (button != activeButton) {
                        button.setBackground(new Color(45, 64, 101));
                    }
                }

                public void mouseExited(java.awt.event.MouseEvent e) {
                    if (button != activeButton) {
                        button.setBackground(SIDEBAR);
                    }
                }
            });

            sidebar.add(button);
            sidebar.add(Box.createVerticalStrut(7));

            if (page.equals("Dashboard")) {
                activeButton = button;
            }
        }

        sidebar.add(Box.createVerticalGlue());

        JPanel info = new JPanel(new GridLayout(2, 1, 0, 7));
        info.setBackground(new Color(36, 54, 87));
        info.setMaximumSize(new Dimension(190, 70));
        info.setBorder(BorderFactory.createEmptyBorder(
                12, 12, 12, 12));

        JLabel infoTitle = new JLabel("Fleet Operations");
        infoTitle.setFont(new Font("Arial", Font.BOLD, 12));
        infoTitle.setForeground(WHITE);

        JLabel infoText = new JLabel("System is ready");
        infoText.setFont(new Font("Arial", Font.PLAIN, 11));
        infoText.setForeground(new Color(190, 205, 230));

        info.add(infoTitle);
        info.add(infoText);

        sidebar.add(info);

        return sidebar;
    }

    private void showPage(String page, JButton selectedButton) {
        cardLayout.show(contentPanel, page);

        pageTitle = null;

        if (activeButton != null) {
            activeButton.setBackground(SIDEBAR);
        }

        if (selectedButton != null) {
            selectedButton.setBackground(BLUE);
            activeButton = selectedButton;
        }

        refreshAll();
    }

    // ================= COMMON DESIGN =================

    private JPanel createPagePanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 18));
        panel.setBackground(BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(
                25, 25, 25, 25));
        return panel;
    }

    private JPanel createPageHeading(String title, String subtitle) {
        JPanel heading = new JPanel(new GridLayout(2, 1, 0, 6));
        heading.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 25));
        titleLabel.setForeground(TEXT);

        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        subtitleLabel.setForeground(MUTED);

        heading.add(titleLabel);
        heading.add(subtitleLabel);

        return heading;
    }

    private JPanel createWhitePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        new Color(226, 231, 239)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        return panel;
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBackground(BLUE);
        button.setForeground(WHITE);
        button.setCursor(Cursor.getPredefinedCursor(
                Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(
                10, 16, 10, 16));
        return button;
    }

    private JButton createSecondaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBackground(WHITE);
        button.setForeground(TEXT);
        button.setCursor(Cursor.getPredefinedCursor(
                Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        new Color(215, 222, 234)),
                BorderFactory.createEmptyBorder(9, 14, 9, 14)));
        return button;
    }

    private DefaultTableModel createModel(String[] columns) {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private void styleTable(JTable table) {
        table.setRowHeight(32);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.setForeground(TEXT);
        table.setBackground(WHITE);
        table.setGridColor(new Color(235, 238, 244));
        table.setShowVerticalLines(false);
        table.setSelectionBackground(new Color(220, 233, 252));
        table.setSelectionForeground(TEXT);
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(true);

        table.getTableHeader().setFont(
                new Font("Arial", Font.BOLD, 12));
        table.getTableHeader().setBackground(
                new Color(238, 242, 248));
        table.getTableHeader().setForeground(TEXT);
        table.getTableHeader().setReorderingAllowed(false);
        table.setIntercellSpacing(new Dimension(8, 0));
    }

    private JPanel createTablePanel(JTable table) {
        JPanel panel = createWhitePanel();

        styleTable(table);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(WHITE);

        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private void addButtons(
            JPanel panel, JButton first, JButton second) {

        JPanel buttons = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttons.setOpaque(false);

        buttons.add(first);
        buttons.add(second);

        panel.add(buttons, BorderLayout.SOUTH);
    }

    private String ask(String message) {
        return JOptionPane.showInputDialog(this, message);
    }

    private void showError(Exception e) {
        String message = e.getMessage();

        if (message == null || message.isBlank()) {
            message = "An unexpected error occurred.";
        }

        JOptionPane.showMessageDialog(
                this, message, "Operation Error",
                JOptionPane.ERROR_MESSAGE);
    }

    private String selectedId(JTable table) {
        int selectedRow = table.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                    this, "Please select a record first.");
            return null;
        }

        int modelRow = table.convertRowIndexToModel(selectedRow);

        return table.getModel().getValueAt(modelRow, 0).toString();
    }

    private void addSearchBar(
            JPanel panel, JTable table, String placeholder) {

        JTextField search = new JTextField(20);
        search.setFont(new Font("Arial", Font.PLAIN, 13));
        search.setPreferredSize(new Dimension(220, 34));

        JButton searchButton = createSecondaryButton("Search");
        JButton resetButton = createSecondaryButton("Clear");

        JLabel label = new JLabel("Search:");
        label.setFont(new Font("Arial", Font.PLAIN, 13));
        label.setForeground(TEXT);

        JPanel searchPanel = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 8, 0));
        searchPanel.setOpaque(false);

        searchPanel.add(label);
        searchPanel.add(search);
        searchPanel.add(searchButton);
        searchPanel.add(resetButton);

        search.setToolTipText(placeholder);

        Runnable applySearch = () -> {
            String query = search.getText().trim();

            TableRowSorter<?> sorter =
                    (TableRowSorter<?>) table.getRowSorter();

            if (query.isEmpty()) {
                sorter.setRowFilter(null);
            } else {
                sorter.setRowFilter(
                        RowFilter.regexFilter(
                                "(?i)" + java.util.regex.Pattern.quote(query)));
            }
        };

        searchButton.addActionListener(e -> applySearch.run());

        search.addActionListener(e -> applySearch.run());

        resetButton.addActionListener(e -> {
            search.setText("");
            TableRowSorter<?> sorter =
                    (TableRowSorter<?>) table.getRowSorter();
            sorter.setRowFilter(null);
        });

        panel.add(searchPanel, BorderLayout.NORTH);
    }

    // ================= DASHBOARD =================

    private JPanel createDashboard() {
        JPanel panel = createPagePanel();

        panel.add(createPageHeading(
                "Dashboard Overview",
                "Monitor your vehicles, drivers, routes and trips."),
                BorderLayout.NORTH);

        JPanel main = new JPanel(new BorderLayout(15, 18));
        main.setOpaque(false);

        JPanel cards = new JPanel(new GridLayout(1, 4, 14, 14));
        cards.setOpaque(false);

        vehicleCount = new JLabel("0");
        driverCount = new JLabel("0");
        routeCount = new JLabel("0");
        tripCount = new JLabel("0");

        cards.add(createCard(
                "TOTAL VEHICLES", vehicleCount,
                new Color(48, 108, 190), "Vehicle records"));
        cards.add(createCard(
                "TOTAL DRIVERS", driverCount,
                new Color(38, 145, 112), "Driver records"));
        cards.add(createCard(
                "TOTAL ROUTES", routeCount,
                new Color(130, 92, 190), "Defined routes"));
        cards.add(createCard(
                "TOTAL TRIPS", tripCount,
                new Color(205, 135, 48), "Scheduled trips"));

        main.add(cards, BorderLayout.NORTH);

        JPanel recent = createWhitePanel();

        JLabel recentTitle = new JLabel("Recent Trips");
        recentTitle.setFont(new Font("Arial", Font.BOLD, 17));
        recentTitle.setForeground(TEXT);

        recentTripModel = createModel(new String[]{
                "Trip ID", "Vehicle", "Driver",
                "Route", "Start Time", "Status"
        });

        recentTripTable = new JTable(recentTripModel);
        styleTable(recentTripTable);

        JScrollPane recentScroll = new JScrollPane(recentTripTable);
        recentScroll.setBorder(BorderFactory.createEmptyBorder());
        recentScroll.getViewport().setBackground(WHITE);

        recent.add(recentTitle, BorderLayout.NORTH);
        recent.add(recentScroll, BorderLayout.CENTER);

        main.add(recent, BorderLayout.CENTER);

        panel.add(main, BorderLayout.CENTER);

        JLabel footer = new JLabel(
                "Fleet Operations  •  Use the sidebar to manage your records");
        footer.setFont(new Font("Arial", Font.PLAIN, 12));
        footer.setForeground(MUTED);

        panel.add(footer, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createCard(
            String title, JLabel number, Color accent, String description) {

        JPanel card = new JPanel(new BorderLayout(8, 12));
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(
                        0, 4, 0, 0, accent),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(228, 232, 240)),
                        BorderFactory.createEmptyBorder(
                                16, 15, 16, 12))));

        JLabel heading = new JLabel(title);
        heading.setFont(new Font("Arial", Font.BOLD, 11));
        heading.setForeground(MUTED);

        number.setFont(new Font("Arial", Font.BOLD, 31));
        number.setForeground(accent);

        JLabel detail = new JLabel(description);
        detail.setFont(new Font("Arial", Font.PLAIN, 11));
        detail.setForeground(MUTED);

        JPanel middle = new JPanel(new BorderLayout());
        middle.setOpaque(false);
        middle.add(number, BorderLayout.CENTER);

        card.add(heading, BorderLayout.NORTH);
        card.add(middle, BorderLayout.CENTER);
        card.add(detail, BorderLayout.SOUTH);

        return card;
    }

    // ================= VEHICLES =================

    private JPanel createVehiclePanel() {
        vehicleModel = createModel(new String[]{
                "ID", "Registration", "Type", "Capacity", "Status"
        });

        vehicleTable = new JTable(vehicleModel);

        JPanel panel = createPagePanel();

        panel.add(createPageHeading(
                "Vehicle Management",
                "Add, search and manage your fleet vehicles."),
                BorderLayout.NORTH);

        JPanel tablePanel = createTablePanel(vehicleTable);
        addSearchBar(tablePanel, vehicleTable,
                "Search vehicles");

        JButton add = createButton("+ Add Vehicle");
        JButton delete = createSecondaryButton("Delete Selected");
        JButton refresh = createSecondaryButton("Refresh");

        add.addActionListener(e -> addVehicle());
        delete.addActionListener(e -> deleteVehicle());
        refresh.addActionListener(e -> refreshAll());

        addButtons(tablePanel, add, delete);
        JPanel wrapper = new JPanel(new BorderLayout(0, 10));
        wrapper.setOpaque(false);
        wrapper.add(tablePanel, BorderLayout.CENTER);

        JPanel actions = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 10, 0));
        actions.setOpaque(false);
        actions.add(add);
        actions.add(delete);
        actions.add(refresh);

        wrapper.add(actions, BorderLayout.SOUTH);

        panel.add(wrapper, BorderLayout.CENTER);

        return panel;
    }

    // ================= DRIVERS =================

    private JPanel createDriverPanel() {
        driverModel = createModel(new String[]{
                "ID", "Driver Name", "Phone", "Eligible"
        });

        driverTable = new JTable(driverModel);

        JPanel panel = createPagePanel();

        panel.add(createPageHeading(
                "Driver Management",
                "Maintain driver details and eligibility."),
                BorderLayout.NORTH);

        JPanel tablePanel = createTablePanel(driverTable);
        addSearchBar(tablePanel, driverTable,
                "Search drivers");

        JButton add = createButton("+ Add Driver");
        JButton delete = createSecondaryButton("Delete Selected");
        JButton refresh = createSecondaryButton("Refresh");

        add.addActionListener(e -> addDriver());
        delete.addActionListener(e -> deleteDriver());
        refresh.addActionListener(e -> refreshAll());

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(tablePanel, BorderLayout.CENTER);

        JPanel actions = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 10, 5));
        actions.setOpaque(false);
        actions.add(add);
        actions.add(delete);
        actions.add(refresh);

        wrapper.add(actions, BorderLayout.SOUTH);

        panel.add(wrapper, BorderLayout.CENTER);

        return panel;
    }

    // ================= ROUTES =================

    private JPanel createRoutePanel() {
        routeModel = createModel(new String[]{
                "ID", "Origin", "Destination", "Stops"
        });

        routeTable = new JTable(routeModel);

        JPanel panel = createPagePanel();

        panel.add(createPageHeading(
                "Route Management",
                "Define and maintain transport routes."),
                BorderLayout.NORTH);

        JPanel tablePanel = createTablePanel(routeTable);
        addSearchBar(tablePanel, routeTable,
                "Search routes");

        JButton add = createButton("+ Add Route");
        JButton delete = createSecondaryButton("Delete Selected");
        JButton refresh = createSecondaryButton("Refresh");

        add.addActionListener(e -> addRoute());
        delete.addActionListener(e -> deleteRoute());
        refresh.addActionListener(e -> refreshAll());

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(tablePanel, BorderLayout.CENTER);

        JPanel actions = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 10, 5));
        actions.setOpaque(false);
        actions.add(add);
        actions.add(delete);
        actions.add(refresh);

        wrapper.add(actions, BorderLayout.SOUTH);

        panel.add(wrapper, BorderLayout.CENTER);

        return panel;
    }

    // ================= TRIPS =================

    private JPanel createTripPanel() {
        tripModel = createModel(new String[]{
                "ID", "Vehicle", "Driver", "Route",
                "Start Time", "End Time", "Status"
        });

        tripTable = new JTable(tripModel);

        JPanel panel = createPagePanel();

        panel.add(createPageHeading(
                "Trip Scheduling",
                "Schedule trips and monitor their current status."),
                BorderLayout.NORTH);

        JPanel tablePanel = createTablePanel(tripTable);
        addSearchBar(tablePanel, tripTable,
                "Search trips");

        JButton add = createButton("+ Schedule Trip");
        JButton update = createSecondaryButton("Update Status");
        JButton refresh = createSecondaryButton("Refresh");

        add.addActionListener(e -> addTrip());
        update.addActionListener(e -> updateTripStatus());
        refresh.addActionListener(e -> refreshAll());

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(tablePanel, BorderLayout.CENTER);

        JPanel actions = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 10, 5));
        actions.setOpaque(false);
        actions.add(add);
        actions.add(update);
        actions.add(refresh);

        wrapper.add(actions, BorderLayout.SOUTH);

        panel.add(wrapper, BorderLayout.CENTER);

        return panel;
    }

    // ================= ADD VEHICLE =================

    private void addVehicle() {
        try {
            String registration = ask("Registration number:");
            if (registration == null) return;

            String type = ask(
                    "Vehicle type (Car, Bus, Truck, etc.):");
            if (type == null) return;

            String capacityText = ask("Vehicle capacity:");
            if (capacityText == null) return;

            int capacity = Integer.parseInt(capacityText.trim());

            String[] statuses = {"Available", "Maintenance"};

            String status = (String) JOptionPane.showInputDialog(
                    this, "Select operational status:",
                    "Vehicle Status", JOptionPane.QUESTION_MESSAGE,
                    null, statuses, statuses[0]);

            if (status == null) return;

            manager.addVehicle(
                    registration, type, capacity, status);

            refreshAll();

            JOptionPane.showMessageDialog(
                    this, "Vehicle added successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                    this, "Capacity must be a valid whole number.",
                    "Invalid Capacity", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            showError(e);
        }
    }

    private void deleteVehicle() {
        try {
            String id = selectedId(vehicleTable);
            if (id == null) return;

            int confirm = JOptionPane.showConfirmDialog(
                    this, "Delete vehicle " + id + "?",
                    "Confirm Deletion", JOptionPane.YES_NO_OPTION);

            if (confirm != JOptionPane.YES_OPTION) return;

            manager.deleteVehicle(id);
            refreshAll();

            JOptionPane.showMessageDialog(
                    this, "Vehicle deleted successfully!");

        } catch (Exception e) {
            showError(e);
        }
    }

    // ================= ADD DRIVER =================

    private void addDriver() {
        try {
            String name = ask("Driver name:");
            if (name == null) return;

            String phone = ask("Driver contact number:");
            if (phone == null) return;

            String[] choices = {"Yes", "No"};

            String answer = (String) JOptionPane.showInputDialog(
                    this, "Is the driver eligible?",
                    "Driver Eligibility", JOptionPane.QUESTION_MESSAGE,
                    null, choices, choices[0]);

            if (answer == null) return;

            manager.addDriver(
                    name, phone, answer.equals("Yes"));

            refreshAll();

            JOptionPane.showMessageDialog(
                    this, "Driver added successfully!");

        } catch (Exception e) {
            showError(e);
        }
    }

    private void deleteDriver() {
        try {
            String id = selectedId(driverTable);
            if (id == null) return;

            int confirm = JOptionPane.showConfirmDialog(
                    this, "Delete driver " + id + "?",
                    "Confirm Deletion", JOptionPane.YES_NO_OPTION);

            if (confirm != JOptionPane.YES_OPTION) return;

            manager.deleteDriver(id);
            refreshAll();

            JOptionPane.showMessageDialog(
                    this, "Driver deleted successfully!");

        } catch (Exception e) {
            showError(e);
        }
    }

    // ================= ADD ROUTE =================

    private void addRoute() {
        try {
            String origin = ask("Route origin:");
            if (origin == null) return;

            String destination = ask("Route destination:");
            if (destination == null) return;

            String stops = ask(
                    "Intermediate stops (optional):");
            if (stops == null) stops = "";

            manager.addRoute(origin, destination, stops);

            refreshAll();

            JOptionPane.showMessageDialog(
                    this, "Route added successfully!");

        } catch (Exception e) {
            showError(e);
        }
    }

    private void deleteRoute() {
        try {
            String id = selectedId(routeTable);
            if (id == null) return;

            int confirm = JOptionPane.showConfirmDialog(
                    this, "Delete route " + id + "?",
                    "Confirm Deletion", JOptionPane.YES_NO_OPTION);

            if (confirm != JOptionPane.YES_OPTION) return;

            manager.deleteRoute(id);
            refreshAll();

            JOptionPane.showMessageDialog(
                    this, "Route deleted successfully!");

        } catch (Exception e) {
            showError(e);
        }
    }

    // ================= SCHEDULE TRIP =================

    private void addTrip() {
        try {
            ArrayList<Vehicle> vehicles = manager.getVehicles();
            ArrayList<Driver> drivers = manager.getDrivers();
            ArrayList<Route> routes = manager.getRoutes();

            if (vehicles.isEmpty()
                    || drivers.isEmpty()
                    || routes.isEmpty()) {

                JOptionPane.showMessageDialog(
                        this,
                        "Add at least one vehicle, driver, "
                                + "and route before scheduling a trip.");

                return;
            }

            Object vehicleChoice = JOptionPane.showInputDialog(
                    this, "Select vehicle:", "Schedule Trip",
                    JOptionPane.QUESTION_MESSAGE, null,
                    vehicles.toArray(), vehicles.get(0));

            if (vehicleChoice == null) return;

            Object driverChoice = JOptionPane.showInputDialog(
                    this, "Select driver:", "Schedule Trip",
                    JOptionPane.QUESTION_MESSAGE, null,
                    drivers.toArray(), drivers.get(0));

            if (driverChoice == null) return;

            Object routeChoice = JOptionPane.showInputDialog(
                    this, "Select route:", "Schedule Trip",
                    JOptionPane.QUESTION_MESSAGE, null,
                    routes.toArray(), routes.get(0));

            if (routeChoice == null) return;

            String startText = ask(
                    "Start time (yyyy-MM-dd HH:mm):");
            if (startText == null) return;

            String endText = ask(
                    "End time (yyyy-MM-dd HH:mm):");
            if (endText == null) return;

            LocalDateTime start = LocalDateTime.parse(
                    startText.trim(), dateFormat);

            LocalDateTime end = LocalDateTime.parse(
                    endText.trim(), dateFormat);

            Vehicle vehicle = (Vehicle) vehicleChoice;
            Driver driver = (Driver) driverChoice;
            Route route = (Route) routeChoice;

            manager.addTrip(
                    vehicle.getId(),
                    driver.getId(),
                    route.getId(),
                    start,
                    end);

            refreshAll();

            JOptionPane.showMessageDialog(
                    this, "Trip scheduled successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);

        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Invalid date or time format.\n"
                            + "Example: 2026-10-20 14:30",
                    "Invalid Time", JOptionPane.ERROR_MESSAGE);

        } catch (Exception e) {
            showError(e);
        }
    }

    // ================= UPDATE TRIP STATUS =================

    private void updateTripStatus() {
        try {
            String id = selectedId(tripTable);
            if (id == null) return;

            String[] statuses = {
                    "Ongoing", "Completed", "Cancelled"
            };

            String status = (String) JOptionPane.showInputDialog(
                    this, "Select new status:",
                    "Update Trip Status",
                    JOptionPane.QUESTION_MESSAGE,
                    null, statuses, statuses[0]);

            if (status == null) return;

            manager.updateTripStatus(id, status);

            refreshAll();

            JOptionPane.showMessageDialog(
                    this, "Trip status updated successfully!");

        } catch (Exception e) {
            showError(e);
        }
    }

    // ================= REFRESH ALL DATA =================

    private void refreshAll() {
        if (vehicleModel == null
                || driverModel == null
                || routeModel == null
                || tripModel == null) {
            return;
        }

        vehicleModel.setRowCount(0);

        for (Vehicle v : manager.getVehicles()) {
            vehicleModel.addRow(new Object[]{
                    v.getId(),
                    v.getRegistrationNumber(),
                    v.getType(),
                    v.getCapacity(),
                    v.getStatus()
            });
        }

        driverModel.setRowCount(0);

        for (Driver d : manager.getDrivers()) {
            driverModel.addRow(new Object[]{
                    d.getId(),
                    d.getName(),
                    d.getPhone(),
                    d.isEligible() ? "Yes" : "No"
            });
        }

        routeModel.setRowCount(0);

        for (Route r : manager.getRoutes()) {
            routeModel.addRow(new Object[]{
                    r.getId(),
                    r.getOrigin(),
                    r.getDestination(),
                    r.getStops()
            });
        }

        tripModel.setRowCount(0);

        ArrayList<Trip> trips = manager.getTrips();

        for (Trip t : trips) {
            Vehicle v = manager.findVehicle(t.getVehicleId());
            Driver d = manager.findDriver(t.getDriverId());
            Route r = manager.findRoute(t.getRouteId());

            tripModel.addRow(new Object[]{
                    t.getId(),
                    v == null ? t.getVehicleId()
                            : v.getRegistrationNumber(),
                    d == null ? t.getDriverId() : d.getName(),
                    r == null ? t.getRouteId()
                            : r.getOrigin() + " to "
                                    + r.getDestination(),
                    t.getFormattedStart(),
                    t.getFormattedEnd(),
                    t.getStatus()
            });
        }

        vehicleCount.setText(
                String.valueOf(manager.getVehicles().size()));

        driverCount.setText(
                String.valueOf(manager.getDrivers().size()));

        routeCount.setText(
                String.valueOf(manager.getRoutes().size()));

        tripCount.setText(
                String.valueOf(trips.size()));

        if (recentTripModel != null) {
            recentTripModel.setRowCount(0);

            int start = Math.max(0, trips.size() - 5);

            for (int i = trips.size() - 1; i >= start; i--) {
                Trip t = trips.get(i);

                Vehicle v = manager.findVehicle(t.getVehicleId());
                Driver d = manager.findDriver(t.getDriverId());
                Route r = manager.findRoute(t.getRouteId());

                recentTripModel.addRow(new Object[]{
                        t.getId(),
                        v == null ? t.getVehicleId()
                                : v.getRegistrationNumber(),
                        d == null ? t.getDriverId() : d.getName(),
                        r == null ? t.getRouteId()
                                : r.getOrigin() + " to "
                                        + r.getDestination(),
                        t.getFormattedStart(),
                        t.getStatus()
                });
            }
        }

        if (contentPanel != null) {
            contentPanel.revalidate();
            contentPanel.repaint();
        }
    }

    // ================= MAIN =================

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(
                        UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }

            try {
                FleetManagementApp app = new FleetManagementApp();
                app.setVisible(true);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                        null,
                        "Could not start the application:\n"
                                + e.getMessage(),
                        "Startup Error",
                        JOptionPane.ERROR_MESSAGE);

                e.printStackTrace();
            }
        });
    }
}
