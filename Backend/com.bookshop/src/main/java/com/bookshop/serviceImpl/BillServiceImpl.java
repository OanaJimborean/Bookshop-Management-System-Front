package com.bookshop.serviceImpl;

import com.bookshop.JWT.JwtFilter;
import com.bookshop.constents.BookshopConstants;
import com.bookshop.dao.BillDAO;
import com.bookshop.model.Bill;
import com.bookshop.service.BillService;
import com.bookshop.utils.BookshopUtility;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.io.IOUtils;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@Service
public class BillServiceImpl implements BillService {

    @Autowired
    JwtFilter jwtFilter;
    @Autowired
    BillDAO billDAO;
    @Override
    public ResponseEntity<String> generateReport(Map<String, Object> requestMap) {
        log.info("Inside generateReport");
        try{
            String fileName;
            if(validateRequestMap(requestMap)){
                if(requestMap.containsKey("isGenerate") && !(Boolean)requestMap.get("isGenerate")){
                    fileName = (String) requestMap.get("uuid");
                }else{
                    fileName = BookshopUtility.getUUID();
                    requestMap.put("uuid", fileName);
                    insertBill(requestMap);
                }
                String data = "Username: "+requestMap.get("username") +"\n"+"Email: "+requestMap.get("email") +
                        "\n"+"Payment Method: "+requestMap.get("paymentMethod") + "\n";
                Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(BookshopConstants.STORE_LOCATION+"\\"+fileName+".pdf"));
                document.open();

                setRectangleInPdf(document);

                Paragraph chunk = new Paragraph("Bookshop Management System", getFont("Header"));
                chunk.setAlignment(Element.ALIGN_CENTER);
                document.add(chunk);

                Paragraph paragraph = new Paragraph(data + "\n \n", getFont("Data"));
                document.add(paragraph);

                PdfPTable table = new PdfPTable(5);
                table.setWidthPercentage(100);
                addTableHeader(table);

                JSONArray jsonArray = BookshopUtility.getJsonArrayFromString((String) requestMap.get("bookDetails"));
                for(int i = 0; i < jsonArray.length(); i++) {
                    addRows(table, BookshopUtility.getMapFromJson(jsonArray.getString(i)));
                }
                document.add(table);

                Paragraph footer = new Paragraph("Total : " + requestMap.get("totalAmount") + "\n" +
                        "Thank you for visiting. Please visit again!!", getFont("Data"));
                document.add(footer);
                document.close();
                return new ResponseEntity<>("{\"uuid\":\"" + fileName + "\"}", HttpStatus.OK);
            }
            return BookshopUtility.getResponseEntity("Required data not found.", HttpStatus.BAD_REQUEST);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return BookshopUtility.getResponseEntity(BookshopConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }



    private void addRows(PdfPTable table, Map<String, Object> data) {
        log.info("Inside addRows");
        table.addCell((String) data.get("title"));
        table.addCell((String) data.get("category"));
        table.addCell((String) data.get("quantity"));
        table.addCell(Double.toString((Double) data.get("price")));
        table.addCell(Double.toString((Double) data.get("total")));
    }

    private void addTableHeader(PdfPTable table) {
        log.info("Inside addTableHeader.");
        Stream.of("Title", "Category", "Quantity", "Price", "Sub Total")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setBorderWidth(2);
                    header.setPhrase(new Phrase(columnTitle));
                    header.setBackgroundColor(BaseColor.YELLOW);
                    header.setHorizontalAlignment(Element.ALIGN_CENTER);
                    header.setVerticalAlignment(Element.ALIGN_CENTER);
                    table.addCell(header);
                });
    }

    private Font getFont(String type) {
        log.info("Inside getFont");
        switch (type) {

            case "Header":
                Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLDOBLIQUE, 18, BaseColor.GREEN);
                headerFont.setStyle(Font.BOLD);
                return headerFont;

            case "Data":
                Font dataFont = FontFactory.getFont(FontFactory.COURIER, 11, BaseColor.BLACK);
                dataFont.setStyle(Font.BOLD);
                return dataFont;

            default:
                return new Font();

        }

    }

    private void setRectangleInPdf(Document document) throws DocumentException {
        log.info("Inside setRectangleInPdf");
        Rectangle rectangle = new Rectangle(577, 825, 18, 15);
        rectangle.enableBorderSide(1);
        rectangle.enableBorderSide(2);
        rectangle.enableBorderSide(4);
        rectangle.enableBorderSide(8);
        rectangle.setBorderColor(BaseColor.CYAN);
        rectangle.setBorderWidth(1);
        document.add(rectangle);
    }


    private boolean validateRequestMap(Map<String, Object> requestMap) {

        return requestMap.containsKey("username") &&
                requestMap.containsKey("email") &&
                requestMap.containsKey("paymentMethod") &&
                requestMap.containsKey("bookDetails") &&
                requestMap.containsKey("totalAmount");

    }

    private void insertBill(Map<String, Object> requestMap) {
        try{
            Bill bill = new Bill();
            bill.setUuid((String) requestMap.get("uuid"));
            bill.setUsername((String)requestMap.get("username"));
            bill.setEmail((String)requestMap.get("email"));
            bill.setPaymentMethod((String)requestMap.get("paymentMethod"));
            bill.setTotal(Integer.parseInt((String) requestMap.get("totalAmount")));
            bill.setBookDetail((String) requestMap.get("bookDetails"));
            bill.setCreatedBy(jwtFilter.getCurrentUser());
            billDAO.save(bill);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    public ResponseEntity<List<Bill>> getBills() {
        List<Bill> list = new ArrayList<>();
        if(jwtFilter.isAdmin()) {
            list = billDAO.getAllBills();
        } else {
            list = billDAO.getBillByUsername(jwtFilter.getCurrentUser());
        }
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<byte[]> getPdf(Map<String, Object> requestMap) {
        log.info("Inside getPdf : requestMap {}", requestMap);
        try {
            byte[] byteArray = new byte[0];
            if (!requestMap.containsKey("uuid") && validateRequestMap(requestMap))
                return new ResponseEntity<>(byteArray, HttpStatus.BAD_REQUEST);
            String filePath = BookshopConstants.STORE_LOCATION + "\\" + (String) requestMap.get("uuid") + ".pdf";
            if(BookshopUtility.isFileExist(filePath)){
                byteArray = getByteArray(filePath);
                return new ResponseEntity<>(byteArray, HttpStatus.OK);
            }else{
                requestMap.put("isGenerate", false);
                generateReport(requestMap);
                byteArray = getByteArray(filePath);
                return new ResponseEntity<>(byteArray, HttpStatus.OK);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    private byte[] getByteArray(String filePath) throws Exception{
        File initialFile = new File(filePath);
        InputStream targetStream = new FileInputStream(initialFile);
        byte[] byteArray = IOUtils.toByteArray(targetStream);
        targetStream.close();
        return byteArray;
    }

    @Override
    public ResponseEntity<String> deleteBill(Integer id) {
        try {
            Optional optional = billDAO.findById(id);
            if(!optional.isEmpty()) {
                billDAO.deleteById(id);
                return BookshopUtility.getResponseEntity("Bill Deleted Successfully!", HttpStatus.OK);
            }
            return BookshopUtility.getResponseEntity("Bill Id does not exist!", HttpStatus.OK);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return BookshopUtility.getResponseEntity(BookshopConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
