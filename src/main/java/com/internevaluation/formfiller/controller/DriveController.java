package com.internevaluation.formfiller.controller;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;

import com.google.api.client.json.JsonFactory;

import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.internevaluation.formfiller.entity.FileList;
import com.internevaluation.formfiller.repo.FileRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStreamReader;

import java.util.Collections;
import java.util.List;


@Controller
@CrossOrigin("*")
@RequestMapping(path = "/drive")
public class DriveController {
    @Autowired
    FileRepo fileRepo;
    private static HttpTransport HTTP_TRANSPORT=new NetHttpTransport();
    private static JsonFactory JSON_FACTORY= new JacksonFactory();

    private static  final List<String> SCOPES= Collections.singletonList(DriveScopes.DRIVE);

    private  String USER_IDENTIFIER_KEY="MY_DUMMY_USER";
    @Value("${google.oauth.callback.uri}")
    private String CALLBACK_URI;

    //private String email;
    @Value("${google.secret.key.path}")
    private Resource gdSecretKeys;
    @Value("${google.credentials.folder.path}")
    private Resource credentialsFolder;
    private GoogleAuthorizationCodeFlow flow;

    @PostConstruct
    public void init() throws Exception {
        GoogleClientSecrets secrets=GoogleClientSecrets.load(JSON_FACTORY,new InputStreamReader(gdSecretKeys.getInputStream()));
        flow=new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT,JSON_FACTORY,secrets,SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(credentialsFolder.getFile())).build();
    }
    @GetMapping("/googlesignin")
    public void doGoogleSignIn(HttpServletResponse response) throws Exception{
        try {
            GoogleAuthorizationCodeRequestUrl url = flow.newAuthorizationUrl();
            String redirectURL = url.setRedirectUri(CALLBACK_URI).setAccessType("offline").build();
            response.sendRedirect(redirectURL);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @GetMapping("/oauth")
    public void getDataFromGoogle(HttpServletRequest httpServletRequest) throws Exception{
        String code=httpServletRequest.getParameter("code");
        if(code!=null){
            saveToken(code);
        }
    }
    private void saveToken(String code) throws Exception{
        GoogleTokenResponse response=flow.newTokenRequest(code).setRedirectUri(CALLBACK_URI).execute();
        flow.createAndStoreCredential(response,USER_IDENTIFIER_KEY);
    }
    @GetMapping("/create/{email}")
    public void createFile(HttpServletResponse response, @PathVariable(name="email") String email) throws Exception{
        try {

            Credential cred = flow.loadCredential(USER_IDENTIFIER_KEY);
            Drive drive = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, cred).setApplicationName("internion").build();
            List<FileList> fileLists=fileRepo.findAllByEmail(email);
            for(int i=0;i<fileLists.size();i++){
                File file = new File();
                file.setName(fileLists.get(i).getFilename());
                FileContent content = new FileContent("application/pdf", new java.io.File("C:\\Users\\Jathin\\Downloads\\form-backend-internion-branch-2\\src\\main\\java\\com\\internevaluation\\formfiller\\datafolder\\"+fileLists.get(i).getUsername()+"\\"+fileLists.get(i).getFilename()+".pdf"));
                File uploadedFile = drive.files().create(file, content).setFields("id").execute();
                String fileReference = String.format("{fileID: '%s'}", uploadedFile.getId());
                response.getWriter().write(fileReference);
            }

        }catch (GoogleJsonResponseException e){
            System.out.println(e.getDetails());
        }
    }
//    @GetMapping("/getuser/{useremail}")
//    public void getUsername(@PathVariable(name = "useremail") String useremail) {
//        System.out.println(useremail);
//        this.email = useremail;
//    }
}

