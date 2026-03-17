package com.github.donovan_dead;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    private static File output;
    private static FileInputStream fsi;
    private static FileOutputStream fso;
    private static String lang;

    private static Scanner sc = new Scanner(System.in);

    public static String token = System.getenv("OpenAIToken"); 


    public static String MakeRequest() throws Exception {
        String fileString = new String(fsi.readAllBytes(), StandardCharsets.UTF_8);
        fileString = fileString.replace("\\", "\\\\") 
                               .replace("\"", "\\\"") 
                               .replace("\n", "\\n")  
                               .replace("\r", "\\r");

        String body =
        "{"
        + "\"model\":\"gpt-4.1-nano\","
        + "\"input\":["
        + " { \"role\" : \"user\", \"content\" : \" Considera que eres un traductor de textos el cual se va a encargar de traducir el texto de entrada a " + lang 
        + " asi que a partir de aqui comienza el texto a traducir (solo responde con el texto traducido y no cambies el significado del mismo, solo haz trabajo de traducción): " + fileString +  " \" }"
        + " ] "
        + "}";    

        File tempJson = File.createTempFile("openai_req", ".json");
        try (FileOutputStream fos = new FileOutputStream(tempJson)) {
            fos.write(body.getBytes(StandardCharsets.UTF_8));
        }

        ProcessBuilder pb = new ProcessBuilder(
            "curl", "https://api.openai.com/v1/responses",
            "-H", "Content-Type: application/json",
            "-H", "Authorization: Bearer " + token,
            "-d", "@" + tempJson.getAbsolutePath() 
        );

        System.out.println("Starting the process");
        Process p = pb.start();

        boolean finished = p.waitFor(10, TimeUnit.SECONDS);
        
        if (!finished) {
            p.destroyForcibly();
            tempJson.delete(); 
            throw new RuntimeException("Process timeout");
        }

        System.out.println("Finishing the process");

        InputStream is = p.getInputStream();
        byte[] responseBytes = is.readAllBytes();
        
        tempJson.delete();

        return new String(responseBytes, StandardCharsets.UTF_8);
    }    
    
    public static void main(String[] args) {
        System.out.println(token);
        try {
            if(token.isEmpty()){
                System.out.println("No se encontro la variable de entorno.");
                return;
            }
            
            System.out.print("Enter the route where your file is located (with the file included):\t");
            String path = sc.nextLine();
            System.out.println();

            File file = new File(path);
            if(!file.exists()) throw new Exception("The given file doesnt exists");
            fsi = new FileInputStream(file);

            System.out.print("Enter the route where you want to save the traduction:\t");
            path = sc.nextLine();
            System.out.println();

            output = new File(path);
            if(!output.exists()) output.createNewFile();
            fso = new FileOutputStream(output);

            System.out.print("Enter the language to translate the text:\t");
            lang = sc.nextLine();
            System.out.println();

            String result = MakeRequest();
            
            Pattern pattern = Pattern.compile("\"text\":\\s*\"(.*?)\"");
            Matcher matcher = pattern.matcher(result);

            if (matcher.find()) {
                String traducido = matcher.group(1);

                traducido = traducido.replace("\\\"", "\"");

                Pattern unicodePattern = Pattern.compile("\\\\u([0-9a-fA-F]{4})");
                Matcher unicodeMatcher = unicodePattern.matcher(traducido);
                StringBuilder sb = new StringBuilder();
                
                while (unicodeMatcher.find()) {
                    char code = (char) Integer.parseInt(unicodeMatcher.group(1), 16);
                    unicodeMatcher.appendReplacement(sb, Character.toString(code));
                }
                unicodeMatcher.appendTail(sb);
            
                fso.write(sb.toString().getBytes());
                fso.flush();
            }
            fsi.close();
            fso.close();
        } catch (Exception e) {
            System.out.print(e.getMessage());
        } finally {
            sc.close();
        } 
        

    }
}