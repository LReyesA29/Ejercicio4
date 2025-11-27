package view;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import persistence.HandlingPersistence;
import Model.*;
import enums.ETypeFile;

public class Interfaz {
    private HandlingPersistence handlingPersistence = new HandlingPersistence();
    private Scanner sc = new Scanner(System.in);

    public void showInfo() {
        System.out.println("Manejo de Datos de Transacciones");
        boolean exit = false;
        while (!exit) {
            exit = showMenu();
        }
        System.out.println("Saliendo del sistema... ¡Hasta luego!");
    }

    public boolean showMenu() {
        System.out.println("\n--- MENÚ PRINCIPAL ---");
        System.out.println("1. Ver Transacciones ");
        System.out.println("2. Aplicacion de Reglas");
        System.out.println("3. Graficos");
        System.out.println("4. Salir");
        System.out.print("Opción: ");
        int opc = readInt();
        switch (opc) {
            case 1:
                listTransactions();
                break;
            case 2:
                showSetRules();
                break;
            case 3:
                showReports();
                break;
            case 4:
                return true;
            default:
                System.out.println("Opción inválida.");
                break;
        }
        return false;
    }

    public void showSetRules() {
        listRules();
        System.out.println("Opción: ");
        int opc = readInt();
        switch (opc) {
            case 1:
                rule_FILTER_REPEAT();
                showDump();
                break;
            case 2:
                rule_FILTER_ID_CLIENT();
                showDump();
                break;
            case 3:
                rule_FILTER_ID_CLIENT_AND_REPEAT();
                showDump();
                break;
            default:
                System.out.println("Opción inválida.");
                System.out.println(opc);
                break;
        }
    }

    private void rule_FILTER_ID_CLIENT_AND_REPEAT() {
        Rule ruleIdClient = handlingPersistence.getListRules().stream()
                .filter(r -> "FILTER_ID_CLIENT".equalsIgnoreCase(r.getName()))
                .findFirst()
                .orElse(null);

        Integer clientId = null;
        if (ruleIdClient == null) {
            System.out.println("No se encontró la regla FILTER_ID_CLIENT. No se filtra por cliente.");
        } else {
            try {
                clientId = Integer.parseInt(ruleIdClient.getValue());
            } catch (NumberFormatException e) {
                System.out.println("Error de formato en el valor de la regla FILTER_ID_CLIENT: " + ruleIdClient.getValue());
            }
        }

        List<Transaction> list = handlingPersistence.getListTransactions();
        List<Transaction> listAfterClientFilter = new ArrayList<>();

        if (clientId != null) {
            System.out.println("\nFiltrando Transacciones por Id de cliente ..." + clientId);
            for (Transaction t : list) {
                if (t.getClientId() == clientId) {
                    listAfterClientFilter.add(t);
                }
            }

            if (listAfterClientFilter.isEmpty()) {
                System.out.println("No hay transacciones con el ID cliente: " + clientId);
                handlingPersistence.setListFilterTransactions(listAfterClientFilter);
                return;
            }
            System.out.println("Filtro de id cliente aplicado");
        } else {
            listAfterClientFilter.addAll(list);
        }

        Rule ruleRepeat = handlingPersistence.getListRules().stream()
                .filter(r -> "FILTER_REPEAT".equalsIgnoreCase(r.getName()))
                .findFirst()
                .orElse(null);

        boolean bandRepeatFilter = false;
        List<Transaction> finalList = new ArrayList<>();
        if (ruleRepeat == null) {
            System.out.println("No se encontró la regla FILTER_REPEAT. No se filtran repetidos.");
        } else {
            String value = ruleRepeat.getValue() != null ? ruleRepeat.getValue().trim() : "0";
            if ("1".equals(value)) {
                bandRepeatFilter = true;
            } else {
                System.out.println("La regla FILTER_REPEAT está desactivada.");
            }
        }

        if (bandRepeatFilter) {
            List<Transaction> listToProcess = listAfterClientFilter;
            List<Transaction> listFilterNoRepeat = new ArrayList<>();
            Set<String> validateSet = new HashSet<>();

            for (Transaction t : listToProcess) {
                String key = t.getId() + "-"
                        + t.getClientId() + "-"
                        + t.getAmount() + "-"
                        + t.getDate() + "-"
                        + t.getPaymentMethod();

                if (validateSet.add(key)) {
                    listFilterNoRepeat.add(t);
                }
            }
            finalList = listFilterNoRepeat;
            System.out.println("Filtro de repetidos aplicado");
        } else {
            finalList.addAll(listAfterClientFilter);
        }

        handlingPersistence.setListFilterTransactions(finalList);

        for (Transaction r : finalList) {
            System.out.println("ID: " + r.getId()
                    + " | ID Cliente: " + r.getClientId()
                    + " | Monto: " + r.getAmount()
                    + " | Fecha: " + r.getDate()
                    + " | Método de Pago: " + r.getPaymentMethod());
        }
    }

    public void showDump() {
        System.out.println("\n-Desea Persistir a: " + handlingPersistence.getListFiles().getFirst().getTypeFile());
        System.out.println("1. SI ");
        System.out.println("2. NO... Volver");
        System.out.println("3. Salir");

        System.out.print("Opción: ");
        int opc = readInt();
        switch (opc) {
            case 1:
                dumpFileByTypeFile();
                break;
            case 2:
                handlingPersistence.setListFilterTransactions(handlingPersistence.getListTransactions());
                showMenu();
                break;
            case 3:
                System.exit(0);
                break;
            default:
                System.out.println("Opción inválida.");
                break;
        }
    }

    public void showReports() {
        System.out.println("1. Mostrar Reportes Filtrados " + handlingPersistence.getListFiles().getFirst().getTypeFile());
        System.out.println("2. Mostrar Reportes Sin filtar");
        System.out.println("3. Volver");

        System.out.print("Opción: ");
        int opc = readInt();
        switch (opc) {
            case 1:
                handlingPersistence.loadFile(handlingPersistence.getListFiles().getFirst().getTypeFile());
                if (handlingPersistence.getTestList() == null || handlingPersistence.getTestList().isEmpty()) {
                    System.out.println("No hay datos filtrados");
                    return;
                }
                Reports();
                break;
            case 2:
                handlingPersistence.setTestList(handlingPersistence.getListTransactions());
                Reports();
                break;
            case 3:
                showMenu();
                break;
            default:
                System.out.println("Opción inválida.");
                break;
        }
    }

    public void Reports() {
        Graphic barras = new Graphic(handlingPersistence);
        barras.showBarChartStockByCategory();
    }

    private void rule_FILTER_ID_CLIENT() {
        Rule rule = handlingPersistence.getListRules().stream()
                .filter(r -> "FILTER_ID_CLIENT".equalsIgnoreCase(r.getName()))
                .findFirst()
                .orElse(null);

        if (rule == null) {
            System.out.println("No se encontró la regla FILTER_ID_CLIENT");
            return;
        }
        int clientId;
        try {
            clientId = Integer.parseInt(rule.getValue());
        } catch (NumberFormatException e) {
            System.out.println("Error de formato en el valor de la regla: " + rule.getValue());
            return;
        }

        System.out.println("\nFiltrando Transacciones por Id de cliente ... " + clientId);

        List<Transaction> t = handlingPersistence.getListTransactions();
        List<Transaction> listFilter = new ArrayList<>();

        for (Transaction r : t) {
            if (r.getClientId() == clientId) {
                listFilter.add(r);
            }
        }
        if (listFilter.isEmpty()) {
            System.out.println("No hay transacciones con el ID cliente: " + clientId);
            handlingPersistence.setListFilterTransactions(listFilter);
            return;
        }
        handlingPersistence.setListFilterTransactions(listFilter);
        for (Transaction r : listFilter) {
            System.out.println("ID: " + r.getId()
                    + " | ID Cliente: " + r.getClientId()
                    + " | Monto: " + r.getAmount()
                    + " | Fecha: " + r.getDate()
                    + " | Método de Pago: " + r.getPaymentMethod());
        }
        System.out.println("Filtro de id cliente aplicado");
    }

    private void rule_FILTER_REPEAT() {
        Rule rule = handlingPersistence.getListRules().stream()
                .filter(r -> "FILTER_REPEAT".equalsIgnoreCase(r.getName()))
                .findFirst()
                .orElse(null);

        if (rule == null) {
            System.out.println("No se encontró la regla FILTER_REPEAT");
            return;
        }
        String value = "0";
        if (rule.getValue() != null) {
            value = rule.getValue().trim();
        }
        boolean bandFilter = false;
        if ("1".equals(value)) {
            bandFilter = true;
        }
        if (!bandFilter) {
            System.out.println("La regla FILTER_REPEAT esta desactivada ");
            return;
        }

        List<Transaction> listT = handlingPersistence.getListTransactions();
        List<Transaction> listFilter = new ArrayList<>();
        Set<String> validateSet = new HashSet<>();
        for (Transaction t : listT) {
            String key = t.getId() + "-"
                    + t.getClientId() + "-"
                    + t.getAmount() + "-"
                    + t.getDate() + "-"
                    + t.getPaymentMethod();
            if (validateSet.add(key)) {
                listFilter.add(t);
            }
        }
        handlingPersistence.setListFilterTransactions(listFilter);
        for (Transaction r : listFilter) {
            System.out.println("ID: " + r.getId()
                    + " | ID Cliente: " + r.getClientId()
                    + " | Monto: " + r.getAmount()
                    + " | Fecha: " + r.getDate()
                    + " | Método de Pago: " + r.getPaymentMethod());
        }
        System.out.println("Filtro de repetidos aplicado");
    }

    public void dumpFileByTypeFile() {
        ETypeFile file = handlingPersistence.getListFiles().getFirst().getTypeFile();
        if (file == null) {
            System.out.println("No se encontró el archivo");
            return;
        }
        if (file == ETypeFile.CSV) {
            handlingPersistence.dumpFile(file);
        }
        if (file == ETypeFile.TXT) {
            handlingPersistence.dumpFile(file);
        }
        if (file == ETypeFile.JSON) {
            handlingPersistence.dumpFile(file);
        }
    }

    private void listTransactions() {
        System.out.println("\n--- Listado de Transacciones ---");
        List<Transaction> t = handlingPersistence.getListTransactions();
        if (t.isEmpty()) {
            System.out.println("No hay Transacciones registradas");
            return;
        }
        for (Transaction r : t) {
            System.out.println("ID: " + r.getId()
                    + " | ID CLiente: " + r.getClientId()
                    + " | Monto: " + r.getAmount()
                    + " | Fecha: " + r.getDate()
                    + " | Metodo de Pago: " + r.getPaymentMethod());
        }
    }

    private void listRules() {
        System.out.println("\n--- Listado de Reglas ---");
        List<Rule> t = handlingPersistence.getListRules();
        if (t.isEmpty()) {
            System.out.println("No hay Reglas registradas");
            return;
        }
        int i = 1;
        for (Rule r : t) {
            System.out.println(i + ". " + r.getName());
            i++;
        }
        System.out.println(i + ". Todas");
    }

    private int readInt() {
        while (true) {
            try {
                String line = sc.nextLine();
                return Integer.parseInt(line.trim());
            } catch (NumberFormatException e) {
                System.out.print("Ingrese un número válido: ");
            }
        }
    }
}
