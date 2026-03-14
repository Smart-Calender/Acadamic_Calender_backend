package com.academiccalender.service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class EmailJsService {

    private static final String EMAILJS_API = "https://api.emailjs.com/api/v1.0/email/send";

    // TODO: Replace with your EmailJS values
    private static final String SERVICE_ID = "service_lkp2atf";
    private static final String TEMPLATE_ID = "template_pvyh8it";
    private static final String TEMPLATE_ID1 = "template_urub6kh";
    private static final String TEMPLATE_ID_REJECTED= "template_9ce3hhm";
    private static final String PUBLIC_KEY1 = "XBEH-L1BfAJbHQUDk";
    private static final String PUBLIC_KEY = "xoBLJNkyjseJaPApW";
    private static final String TEMPLATE_ID_STUDENT = "template_kolcns8";
    private final RestTemplate restTemplate;

    public EmailJsService() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Sends a lab request email via EmailJS
     *
     * @param toName      Name of the recipient (TO_Name)
     * @param labName     Lab name (Lab_Name)
     * @param requestDate Request date (Request_Date)
     * @param requestTime Request time (Request_Time)
     */
    public void sendLabRequestEmail(String email,String toName, String labName, String requestDate, String requestTime) {
        Map<String, Object> body = new HashMap<>();
        body.put("service_id", SERVICE_ID);
        body.put("template_id", TEMPLATE_ID);
        body.put("user_id", PUBLIC_KEY);

        Map<String, String> templateParams = new HashMap<>();
        templateParams.put("email", email);
        templateParams.put("TO_Name", toName);
        templateParams.put("Lab_Name", labName);
        templateParams.put("Request_Date", requestDate);
        templateParams.put("Request_Time", requestTime);

        body.put("template_params", templateParams);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(EMAILJS_API, request, String.class);
            System.out.println("EmailJS response: " + response.getStatusCode() + " - " + response.getBody());
        } catch (Exception e) {
            System.err.println("Failed to send EmailJS email: " + e.getMessage());
        }
    }




    public void sendLabApprovedEmail(String toEmail, String instructorName,
                                     String labName, String requestDate, String requestTime) {

        Map<String, Object> body = new HashMap<>();
        body.put("service_id", SERVICE_ID);
        body.put("template_id", TEMPLATE_ID1);
        body.put("user_id", PUBLIC_KEY);

        // Map template variables
        Map<String, String> templateParams = new HashMap<>();
        templateParams.put("email", toEmail);                // Recipient email (dynamic)
        templateParams.put("Instructor_Name", instructorName);
        templateParams.put("Lab_Name", labName);
        templateParams.put("Request_Date", requestDate);
        templateParams.put("Request_Time", requestTime);

        body.put("template_params", templateParams);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(EMAILJS_API, request, String.class);
            System.out.println("EmailJS response: " + response.getStatusCode() + " - " + response.getBody());
        } catch (Exception e) {
            System.err.println("Failed to send EmailJS email: " + e.getMessage());
        }
    }

    public void sendLabRejectedEmail(String toEmail, String instructorName,
                                     String labName, String requestDate, String requestTime) {

        Map<String, Object> body = new HashMap<>();
        body.put("service_id", SERVICE_ID);
        body.put("template_id", TEMPLATE_ID_REJECTED); // use your rejection template ID
        body.put("user_id", PUBLIC_KEY1);

        Map<String, String> templateParams = new HashMap<>();
        templateParams.put("email", toEmail);
        templateParams.put("Instructor_Name", instructorName);
        templateParams.put("Lab_Name", labName);
        templateParams.put("Request_Date", requestDate);
        templateParams.put("Request_Time", requestTime);

        body.put("template_params", templateParams);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(EMAILJS_API, request, String.class);
            System.out.println("EmailJS response: " + response.getStatusCode() + " - " + response.getBody());
        } catch (Exception e) {
            System.err.println("Failed to send EmailJS rejection email: " + e.getMessage());
        }}


    public void sendLabScheduledToStudent(String toEmail, String studentName,String Event_Name,
                                          String courseName,
                                          String sessionDate, String sessionTime) {

        Map<String, Object> body = new HashMap<>();
        body.put("service_id", SERVICE_ID);
        body.put("template_id", TEMPLATE_ID_STUDENT); // Your student template ID
        body.put("user_id", PUBLIC_KEY1);

        Map<String, String> templateParams = new HashMap<>();
        templateParams.put("email", toEmail);
        templateParams.put("Student_Name", studentName);
        templateParams.put("Event_Name", Event_Name);
        templateParams.put("Course_Name", courseName);
        templateParams.put("Session_Date", sessionDate);
        templateParams.put("Session_Time", sessionTime);

        body.put("template_params", templateParams);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(EMAILJS_API, request, String.class);
            System.out.println("EmailJS response: " + response.getStatusCode() + " - " + response.getBody());
        } catch (Exception e) {
            System.err.println("Failed to send EmailJS student email: " + e.getMessage());
        }
    }
}