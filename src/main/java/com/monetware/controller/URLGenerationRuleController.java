package com.monetware.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@RestController
public class URLGenerationRuleController {

    @RequestMapping(value = "/url/single", method = RequestMethod.POST,
            produces = "text/plain;charset=UTF-8")
    public String generateSingleURL(HttpServletRequest request) {
        // Get the parameters
        String path = request.getParameter("path");

        // TODO: Replace this statement with real handling code
        System.out.println("path = \"" + path + "\"");

        return path;
    }

    @RequestMapping(value = "/url/list", method = RequestMethod.POST,
            produces = "application/json;charset=UTF-8")
    public List<String> generateListURL(HttpServletRequest request) {
        // Get the parameters
        String template = request.getParameter("template");
        int start = Integer.parseInt(request.getParameter("start"));
        int interval = Integer.parseInt(request.getParameter("interval"));
        int num = Integer.parseInt(request.getParameter("num"));
        String placeholder = request.getParameter("placeholder");

        System.out.println("template = \"" + template + "\"");
        System.out.println("start = " + start);
        System.out.println("interval = " + interval);
        System.out.println("num = " + num);

        // Generate a list of urls
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < num; i++) {
            list.add(template.replaceAll(placeholder, String.valueOf(start + i * interval)));
        }

        // TODO: Replace this statement with real handling code
        System.out.println(list);

        return list;
    }

    @RequestMapping(value = "/url/click", method = RequestMethod.POST,
            produces = "text/plain;charset=UTF-8")
    public String generateClickURL(HttpServletRequest request) {
        // Get the parameters
        String initialURL = request.getParameter("initial_url");
        String xpath = request.getParameter("xpath");

        // TODO: Replace this statement with real handling code
        System.out.println("initialURL = \"" + initialURL + "\"");
        System.out.println("xpath = \"" + xpath + "\"");

        return "initialURL = \"" + initialURL + "\", xpath = \"" + xpath + "\"";
    }

    @RequestMapping(value = "/url/file", method = RequestMethod.POST,
            produces = "text/plain;charset=UTF-8")
    public String generateURLFromFile(@RequestParam("filename") MultipartFile file) {
        if (file.isEmpty()) {
            return null;
        }

        StringBuilder stringBuilder = new StringBuilder();
        try {
            InputStream inputStream = file.getInputStream();
            byte[] buffer = new byte[1024];
            int length = -1;
            while ((length = inputStream.read(buffer)) != -1) {
                stringBuilder.append(new String(buffer, 0, length));
            }
        } catch (Exception exception) {
            System.out.println("exception.getMessage() = \"" + exception.getMessage() + "\"");
        }

        System.out.println(stringBuilder.toString());

//        String fileName = file.getOriginalFilename();
//        int size = (int) file.getSize();
//        System.out.println(fileName + "-->" + size);
//
//        String path = "D:/big_research_uploaded_files";
//        File destination = new File(path + "/" + fileName);
//        if (!destination.getParentFile().exists()) {
//            destination.getParentFile().mkdir();
//        }
//        try {
//            file.transferTo(destination);
//        } catch (Exception exception) {
//            System.out.println("exception.getMessage() = \"" + exception.getMessage() + "\"");
//        }

        return stringBuilder.toString();
    }

}
