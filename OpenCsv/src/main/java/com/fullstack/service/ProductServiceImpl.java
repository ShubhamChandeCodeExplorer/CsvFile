package com.fullstack.service;

import com.fullstack.model.Product;
import com.fullstack.repo.ProductRepo;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl {

    @Autowired
    private ProductRepo productRepo;

    @Transactional
    private Set<Product> parsCSV(MultipartFile file) throws IOException {
        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            HeaderColumnNameMappingStrategy<ProductCSVStrategy> strategy = new HeaderColumnNameMappingStrategy<>();
            strategy.setType(ProductCSVStrategy.class);

            CsvToBean<ProductCSVStrategy> csvToBean = new CsvToBeanBuilder<ProductCSVStrategy>(reader)
                    .withMappingStrategy(strategy)
                    .withIgnoreLeadingWhiteSpace(true)
                    .withIgnoreEmptyLine(true)
                    .build();

            return csvToBean.parse().stream().map(csvLine -> Product.builder()
                    .name(csvLine.getName())
                    .category(csvLine.getCategory())
                    .price(csvLine.getPrice())
                    .sku(csvLine.getSku())
                    .stock(csvLine.getStock())
                    .build()).collect(Collectors.toSet());
        }
    }


    public Integer uploadCsv(MultipartFile file) throws IOException {
        Set<Product> products=parsCSV(file);
        productRepo.saveAll(products);
        return products.size();

    }


    public List<Product> findAll(){
        return productRepo.findAll();
    }


    public List<Product> readCsvFile(String filePath) {
        try (Reader reader = new FileReader(filePath)) {
            CsvToBean<Product> csvToBean = new CsvToBeanBuilder<Product>(reader)
                    .withType(Product.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            return csvToBean.parse();
        } catch (Exception e) {
            throw new RuntimeException("Error reading CSV file: " + e.getMessage());
        }
    }

    public String exportProductToCSV(String fileName) {
        // Define directory and file path
        String directoryPath = "D:\\Chalenging Task";
       String filePath = directoryPath + File.separator + fileName;

        // Ensure the directory exists
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            boolean dirCreated = directory.mkdirs();
            if (!dirCreated) {
                return "File is Already Exists: " + directoryPath;
            }
        }

        // Fetch data from database
        List<Product> products = productRepo.findAll();

        // Write data to CSV
        try (FileWriter fileWriter = new FileWriter(filePath);
             CSVPrinter csvPrinter = new CSVPrinter(fileWriter,
                     CSVFormat.DEFAULT.withHeader("id", "name", "category", "price", "sku", "stock"))) {

            for (Product product : products) {
                csvPrinter.printRecord(product.getId(), product.getName(), product.getCategory(),
                        product.getPrice(), product.getSku(), product.getStock());
            }

            csvPrinter.flush();
            return "CSV file successfully created at: " + filePath;

        } catch (IOException e) {
            e.printStackTrace();
            return "Error generating CSV file: " + e.getMessage();
        }
    }
}
