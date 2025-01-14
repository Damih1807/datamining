package Preprocessing;

public class Preprocessing {
    private Dataset dataset;
    private double[][] statistics;
    private final double threshold = 2;
    private String filePath;

    public Preprocessing(String filePath) {
        this.filePath = filePath;
    }

    public void loadData(String filePath) {
        dataset = new Dataset(filePath);
    }

    public void printMockDataset() {
        dataset.printTop10Data();
    }

    public void cleanData() {
        dataset.cleanNullValue();
    }

    public void removeLabel(String label) {
        dataset.removeLabel(label);
    }

    public void sortByColumn(String columnName) {
        dataset.sortByColumn(columnName);
    }

    private void saveToCSV() {
        dataset.saveToCSV();
    }

    private void calculateStatistics() {
        this.statistics = dataset.calculateStatistics();
    }

    public double[][] getStatistics() {
        return statistics;
    }

    private double[] getIQR() {
        if (statistics == null) {
            calculateStatistics();
        }

        double[] iqr = new double[statistics.length]; // Initialize array to store IQR for each column

        for (int i = 0; i < statistics.length; i++) {
            double q1 = statistics[i][2]; // Q1
            double q3 = statistics[i][4]; // Q3
            iqr[i] = q3 - q1; // Compute IQR for current column
        }

        return iqr;
    }

    public void printIQR() {
        double[] iqr = getIQR();
        String[] labels = dataset.getLabelStrings();

        if (iqr.length != labels.length) {
            System.out.println("Error: Unable to compute IQR.");
            return;
        }

        System.out.println("-------------------------------");
        System.out.printf("| %-12s | %-12s |\n", "Column", "IQR");
        System.out.println("-------------------------------");
        for (int i = 0; i < iqr.length; i++) {
            System.out.printf("| %-12s | %-12.2f |\n", labels[i], iqr[i]);
        }
        System.out.println("-------------------------------");
    }

    private void removeOutliers() {
        // Check if statistics are available
        if (statistics == null) {
            System.out.println("Error: Statistics not available.");
            return;
        }
        String[] exceptLabel = { "IND", "IND.1", "IND.2" };
        // Call the removeOutliers method from Dataset class
        dataset.removeOutliers(threshold, statistics, exceptLabel);

        System.out.println("Outliers removed from the dataset.");
    }

    public void printDash() {
        System.out.println("--------------------------------------------------------------------------------");
    }

    public void printCorrelationMatrix() {
        CorrelationMatrixGenerator c = new CorrelationMatrixGenerator(dataset);
        c.generateAndPrintCorrelationMatrix();
    }

    public void start() {
        printDash();
        System.out.println("Open file" + filePath);
        loadData(filePath);
        printDash();

        System.out.println("Cleaning Data");
        cleanData();
        printDash();

        System.out.println("Drop DATE column!");
        removeLabel("DATE");
        printDash();

        // System.out.println("Drop IND column!");
        // removeLabel("IND");
        // printDash();

        // System.out.println("Drop IND.1 column!");
        // removeLabel("IND.1");
        // printDash();

        // System.out.println("Drop IND.2 column!");
        // removeLabel("IND.2");
        // printDash();

        System.out.println("Using IQR: ");
        printIQR();
        printDash();

        System.out.println("Dropping Outlier");
        removeOutliers();
        printDash();

        System.out.println("Preprocess successfully");
        saveToCSV();
        printDash();

        System.out.println("Heatmap: ");
        printCorrelationMatrix();
        printDash();
    }
}
