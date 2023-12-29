package com.internevaluation.formfiller.controller;


import com.internevaluation.formfiller.entity.*;
import com.internevaluation.formfiller.repo.FileRepo;
import com.internevaluation.formfiller.repo.UserRepo;
import com.internevaluation.formfiller.service.CustomUserDetailService;
import com.internevaluation.formfiller.service.PrivateMailSender;
import com.internevaluation.formfiller.service.TwoFactorAuthService;
import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.mail.MessagingException;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping(path = "/api")
public class PdfController {
    private Map<String, String> userCaptchaMap = new HashMap<>();

    private String storedCaptchaText;
    @Autowired
    CustomUserDetailService customUserDetailService;
    @Autowired
    TwoFactorAuthService twoFactorAuthService;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    UserRepo userRepo;
    @Autowired
    PrivateMailSender privateMailSender;
    @Autowired
    FileRepo fileRepo;

    @PostMapping("/register")
    public String userRegistration(@RequestBody UserEntity user) throws Exception {
        user.setVerify_email(false);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        customUserDetailService.saveUserDetails(user);
        return "success";
    }
    @GetMapping("/register/otp/{username}/{otp}")
    public Boolean checkOtp(@PathVariable("username") String username,
                            @PathVariable("otp") String otp) {
        UserEntity dummy = userRepo.findByEmail(username);
        return twoFactorAuthService.isOtpValid(dummy.getSecret_key(),otp);
    }
    @GetMapping("/register/verified/{username}")
    public String verifyRegistration(@PathVariable(name = "username") String username){
        UserEntity temp=userRepo.findByEmail(username);
        temp.setVerify_email(true);
        userRepo.save(temp);
        return "success";
    }
    @GetMapping("/register/generateQr")
    public String generateQr(@RequestParam String email){

        UserEntity temp=userRepo.findByEmail(email);

        return twoFactorAuthService.generateQrCodeImageUri(temp.getSecret_key());
    }
    @GetMapping("/register/validateOtp")
    public Boolean checkOtp(@RequestBody QrUserEntity validateUser){
        UserEntity temp=userRepo.findByEmail(validateUser.getEmail());
        return twoFactorAuthService.isOtpValid(temp.getSecret_key(),validateUser.getOtp());
    }
    @GetMapping("/register/login")
    public String loginUser(@RequestBody LoginEntity loginEntity) {
        UserEntity temp = userRepo.findByEmail(loginEntity.getEmail());
        UserEntity temp2 = userRepo.findByPassword(loginEntity.getPassword());
        UserEntity temp3 = userRepo.findByEmail(loginEntity.getEmail());
        if (temp == null) {
            return new String("User not found");
        } else if (temp2 == null) {
            return new String("User is not login with specified Password");
        } else if (temp3 == null) {
            return new String("user is not registered with specified email");
        } else if (temp3.getVerify_email()==false) {
            return "Please verify your email.";
        } else {
            return "Success";
        }
    }
    @PostMapping("/residenceform")
    public String getResidenceForm(@RequestBody ResidenceCertificateForm certificateForm){
        Document document = new Document();
        try {
            List<FileList> fileList = fileRepo.findAllByUsername(certificateForm.getName());
            if (fileList.isEmpty()) {
                Path path = Paths.get("C:\\Users\\Jathin\\Downloads\\form-backend-internion-branch-2\\src\\main\\java\\com\\internevaluation\\formfiller\\datafolder\\"+certificateForm.getName());
                Files.createDirectories(path);
                //FileList user = FileList.builder().username(certificateForm.getName()).filename(certificateForm.getName() + "-" + "1").build();
               FileList user=new FileList(certificateForm.getName(),certificateForm.getName() + "-" + "1");
                PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("C:\\Users\\Jathin\\Downloads\\form-backend-internion-branch-2\\src\\main\\java\\com\\internevaluation\\formfiller\\datafolder\\" + certificateForm.getName() + "\\" + user.getFilename() + ".pdf"));
                writer.setEncryption(certificateForm.getName().getBytes(), certificateForm.getName().getBytes(), PdfWriter.ALLOW_PRINTING, PdfWriter.ENCRYPTION_AES_128);
                //FileList tempUser = FileList.builder().username(user.getUsername()).filename(user.getFilename()).count(1).build();
                FileList tempUser=new FileList(1,user.getUsername(),user.getFilename(),certificateForm.getEmail());
                fileRepo.save(tempUser);
            } else {
                Integer count = fileList.get(fileList.size()-1).getCount();
                int newCount = (count != null) ? count + 1 : 1;
                PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("C:\\Users\\Jathin\\Downloads\\form-backend-internion-branch-2\\src\\main\\java\\com\\internevaluation\\formfiller\\datafolder\\" + certificateForm.getName() + "\\" + certificateForm.getName() + "-" + newCount + ".pdf"));
                writer.setEncryption(certificateForm.getName().getBytes(), certificateForm.getName().getBytes(), PdfWriter.ALLOW_PRINTING, PdfWriter.ENCRYPTION_AES_128);
                //FileList tempUser = FileList.builder().username(fileList.getUsername()).filename(fileList.getFilename()).count(newCount).build();
                FileList tempUser=new FileList(newCount,certificateForm.getName(),  certificateForm.getName() + "-" + newCount,certificateForm.getEmail());
                fileRepo.save(tempUser);
            }
        } catch (Exception e) {
            return e.toString();
        }

        document.open();
        Path imagePath = Paths.get("C:\\Users\\Jathin\\Downloads\\form-backend-internion-branch-2\\src\\main\\java\\com\\internevaluation\\formfiller\\image\\bharat_logo.png");

        Font titleFont = FontFactory.getFont(FontFactory.COURIER_BOLD, 18, BaseColor.BLACK);
        Paragraph title = new Paragraph("Residence Certification Form", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        Font contentFont = FontFactory.getFont(FontFactory.COURIER_OBLIQUE, 12, BaseColor.BLACK);
        Paragraph content=new Paragraph("This is to certify that Sri/Smt -",contentFont);
        Font boldFont = FontFactory.getFont(FontFactory.COURIER_BOLD, 12, Font.BOLD, BaseColor.BLACK);
        Chunk boldChunk = new Chunk(certificateForm.getName(), boldFont);
        content.add(boldChunk);
        content.add(new Chunk(" s/o, w/o ",contentFont));
        content.add(new Chunk(certificateForm.getParent_name(), boldFont));

        Paragraph content2 = new Paragraph("has been residing at the following address in Village/Town ", contentFont);
        content2.add(new Chunk(certificateForm.getVillage(),boldFont));
        content2.add(new Chunk(" of",contentFont));
        Paragraph content3=new Paragraph("the ");
        Chunk talukaChunk=new Chunk(certificateForm.getTaluka(),boldFont);
        content3.add(talukaChunk);
        content3.add(new Chunk(" taluska of ",contentFont));
        Chunk districtChunk=new Chunk(certificateForm.getDistrict(),boldFont);
        content3.add(districtChunk);
        content3.add(new Chunk(" District during the period noted below.",contentFont));
        //Paragraph content3 = new Paragraph("Kota taluska of Chittor District during the period noted below.", contentFont);
        Paragraph content4 = new Paragraph(certificateForm.getVillage(), boldFont);
        Paragraph content5 = new Paragraph(certificateForm.getDate_of_register(), boldFont);

        content4.setAlignment(Element.ALIGN_LEFT);
        content4.setLeading(14f);
        content5.setAlignment(Element.ALIGN_LEFT);
        content5.setLeading(14f);
//        content4.setAlignment(Element.ALIGN_LEFT);
//        content4.setLeading(14f);
//        content5.setAlignment(Element.ALIGN_LEFT);
//        content5.setLeading(14f);
        //been residing at the following address in Village/Town Rawatbhata of Kota taluska of Chittor District during the period noted below.
        try {
            Image img = Image.getInstance(imagePath.toAbsolutePath().toString());
            document.add(Chunk.NEWLINE);
            img.scaleAbsolute(60f, 60f);
            float xPosition = (document.getPageSize().getWidth() - img.getScaledWidth()) / 2;
            img.setAbsolutePosition(xPosition, document.getPageSize().getHeight() - img.getScaledHeight() - 50); // Adjust the Y position as needed
            document.add(img);
            document.add(new Paragraph());
            document.add(new Paragraph());
            document.add(new Paragraph());
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(title);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(content);
            document.add(Chunk.NEWLINE);
            document.add(content2);
            document.add(Chunk.NEWLINE);
            document.add(content3);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(content4);
            document.add(Chunk.NEWLINE);
            document.add(content5);
        }
        catch (Exception e){
            return e.toString();
        }
        document.close();
        return "added success";
    }
    @PostMapping("/sendprivatemail")
    public String sendPrivateMail(@RequestBody ListOfUser listOfUser) throws MessagingException {
        privateMailSender.sendPrivateMail(listOfUser);
        return "mail sent successfully";
    }
    @GetMapping("/getdata")
    public java.util.List<UserEntity> getUserDetails(){
        List<UserEntity> userDetailsList=customUserDetailService.findAllUser();
        return userDetailsList;
    }
    @GetMapping("/generate/{userId}")
    public ResponseEntity<Map<String, String>> generateCaptcha(@PathVariable("userId")String userId ) throws IOException {
        String captchaText = generateRandomText();
        userCaptchaMap.put(userId,captchaText);
        storedCaptchaText = captchaText;
        BufferedImage captchaImage = generateCaptchaImage(captchaText);


        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(captchaImage, "jpg", byteArrayOutputStream);
        String base64Image = Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());


        String imageUrl = "data:image/jpeg;base64," + base64Image;


        Map<String, String> response = new HashMap<>();
        response.put("imageUrl", imageUrl);

        return ResponseEntity.ok(response);
    }
    private String generateRandomText() {

        int length = 4;
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder randomText = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int index = new Random().nextInt(characters.length());
            randomText.append(characters.charAt(index));
        }

        return randomText.toString();
    }
    private BufferedImage generateCaptchaImage(String text) {
        int width = 200;
        int height = 80;




        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);



        g2d.setFont(new java.awt.Font("Arial", Font.BOLD, 30));
        g2d.setColor(Color.BLACK);

        // Draw the text on the image
        int x = 40;
        int y = height / 2 + 10;
        g2d.drawString(text, x, y);


        addNoise(g2d, width, height);

        g2d.dispose();

        return image;
    }

    private void addNoise(Graphics2D g2d, int width, int height) {

        int numDots = 5000;
        g2d.setColor(Color.GRAY);

        for (int i = 0; i < numDots; i++) {
            int x = new Random().nextInt(width);
            int y = new Random().nextInt(height);
            g2d.fillOval(x, y, 2, 2);
        }
    }
    @PostMapping("/validate/{userId}")
    public ResponseEntity<String> validateCaptcha(@PathVariable("userId") String userId,@RequestParam String enteredText) {

        String storedCaptchaText = userCaptchaMap.get(userId);
        if (enteredText.equals(storedCaptchaText)) {
            return ResponseEntity.ok("Captcha is valid");
        } else {
            return ResponseEntity.badRequest().body("Captcha is invalid");
        }
    }
    @GetMapping("/users/email/{email}")
    public UserEntity getUserByEmail(@PathVariable("email") String email) {
        System.out.println("hello");
        return this.customUserDetailService.getUserByEmail(email);
    }
    @GetMapping("/signin/{email}/{password}")
    public UserEntity getUserByEmailAndPassword(@PathVariable("email") String email,
                                                @PathVariable("password") String password) {
        return this.customUserDetailService.getUserByEmailAndPassword(email, password);
    }
    @GetMapping("/getallusers")
    public List<FileList> getAllUsers(){
        return fileRepo.findAll();
    }

    @GetMapping("/getsingleuserfiles/{username}")
    public List<FileList> getSingleUserFile(@PathVariable(name = "username") String username){
        return fileRepo.findAllByUsername(username);
    }
    @PostMapping("/grantpermission/{filename}")
    public String grantPermissionToUSer(@PathVariable(name = "filename") String filename){
        FileList file=fileRepo.findByFilename(filename);
        file.setPermission(true);
        fileRepo.save(file);
        return "permission Changed";
    }
    @GetMapping("/getfilebyemail/{email}")
    public  List<FileList> getFileByEmail(@PathVariable(name = "email") String email){
        return fileRepo.findAllByEmail(email);
    }
    @GetMapping("/grantpermission/{filename}")
    public String grantPermissionToUser(@PathVariable(name = "filename") String filename) {
        List<FileList> files = fileRepo.findAllByFilename(filename);

        if (!files.isEmpty()) {
            // Handle the list of files (you might want to update all of them, or choose a specific one)
            for (FileList file : files) {
                file.setPermission(true);
                fileRepo.save(file);
            }

            return "Permission changed for " + files.size() + " files";
        } else {
            return "No file found with filename: " + filename;
        }


    }
    @PostMapping("/approveuserpermission")
    public String approveUserPermission(@RequestBody UserMailDto userMailDto){
        privateMailSender.sendEmailWithAttachmentToSingleUser("jatinjain.2011@gmail.com",null,userMailDto.getSender(),userMailDto.getFilename());
        return "Mail sent successfully";
    }
    @GetMapping("/downloadFile/{filename}")
    public ResponseEntity<Resource> downloadFile(
                                                 @PathVariable("filename") String filename) {
        try {
            FileList file = fileRepo.findByFilename(filename);

            if (file != null ) {
                Path filePath = Paths.get("C:\\Users\\Jathin\\Downloads\\form-backend-internion-branch-2\\src\\main\\java\\com\\internevaluation\\formfiller\\datafolder\\" + "jatin" + "\\" + filename + ".pdf");
                Resource resource = new InputStreamResource(Files.newInputStream(filePath));

                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename + ".pdf")
                        .contentType(MediaType.APPLICATION_PDF)
                        .body(resource);
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

    }
    @GetMapping("/download")
    public ResponseEntity<InputStreamResource> downloadPdf(@RequestParam String fileName ,@RequestParam String username) throws IOException {
        // Set the path to your PDF file
        String directory = "C:\\Users\\Jathin\\Downloads\\form-backend-internion-branch-2\\src\\main\\java\\com\\internevaluation\\formfiller\\datafolder\\";
        String filePath = directory + username + "\\"+fileName;

        File file = new File(filePath);
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(file.length())
                .body(resource);
    }




    }
