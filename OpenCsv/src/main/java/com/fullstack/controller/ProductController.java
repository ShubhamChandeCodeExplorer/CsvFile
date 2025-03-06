package com.fullstack.controller;
;
import com.fullstack.model.Product;
import com.fullstack.service.ProductServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductServiceImpl productService;

    @PostMapping(value = "/upload", consumes = {"multipart/form-data"})
    @Operation(
            summary = "Upload CSV file",
            description = "Uploads a CSV file and stores product details in the database",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "multipart/form-data",
                            schema = @Schema(type = "string", format = "binary")
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "File uploaded successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid file format"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    public ResponseEntity<Integer> uploadCSV(@RequestParam( value = "file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(0);
        }
        return ResponseEntity.ok(productService.uploadCsv(file));
    }

    @GetMapping("/getdata")
    public ResponseEntity<List<Product>> getData() {
        return ResponseEntity.ok(productService.findAll());
    }

    @GetMapping("/readcsv")
    public ResponseEntity<List<Product>> readCsv(@RequestParam(value = "path") String path){
        return ResponseEntity.ok(productService.readCsvFile(path));
    }

    @GetMapping("/exportcsv")
    public ResponseEntity<String> exportCsv(@RequestParam(defaultValue = "products.csv")String fileName){
        productService.exportProductToCSV(fileName);
        return new ResponseEntity<>("File Exported Successfully",  HttpStatus.OK);
    }

}
