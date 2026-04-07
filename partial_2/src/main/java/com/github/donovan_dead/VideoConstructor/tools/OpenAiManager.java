package com.github.donovan_dead.VideoConstructor.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.core.http.HttpResponse;
import com.openai.models.ChatModel;
import com.openai.models.audio.speech.SpeechCreateParams;
import com.openai.models.audio.speech.SpeechModel;
import com.openai.models.chat.completions.ChatCompletionContentPart;
import com.openai.models.chat.completions.ChatCompletionContentPartImage;
import com.openai.models.chat.completions.ChatCompletionContentPartText;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import com.openai.models.chat.completions.ChatCompletionMessageParam;
import com.openai.models.chat.completions.ChatCompletionUserMessageParam;
import com.openai.models.chat.completions.StructuredChatCompletion;
import com.openai.models.chat.completions.StructuredChatCompletionCreateParams;
import com.openai.models.images.ImageGenerateParams;
import com.openai.models.images.ImageModel;
import com.openai.models.images.ImagesResponse;
import com.openai.models.images.ImageGenerateParams.Size;

public class OpenAiManager {
    private static OpenAIClient client;

    public static void InitClient(){
        OpenAiManager.client = OpenAIOkHttpClient
        .builder()
        .apiKey(System.getenv("OpenAIToken"))
        .build();
    }

    public static <T> T GenerateStructuredOutput(String prompt, List<String> base64Images, Class<T> responseFormatClass) {
        System.out.println("[DEBUG] OpenAI: Generando salida estructurada (Vision)...");
        List<ChatCompletionContentPart> contentParts = new ArrayList<>();
        contentParts.add(
            ChatCompletionContentPart.ofText(
                ChatCompletionContentPartText.builder().text(prompt).build()
            )
        );

        for (String base64 : base64Images) {
            contentParts.add(
                ChatCompletionContentPart.ofImageUrl(
                    ChatCompletionContentPartImage.builder().imageUrl(
                        ChatCompletionContentPartImage.ImageUrl.builder()
                            .url("data:image/jpeg;base64," + base64)
                            .detail(ChatCompletionContentPartImage.ImageUrl.Detail.LOW) 
                            .build()
                    ).build()
            ));
        }

        StructuredChatCompletionCreateParams<T> params = ChatCompletionCreateParams.builder()
            .model(ChatModel.GPT_4O_MINI)
            .messages(List.of(
                ChatCompletionMessageParam.ofUser(
                    ChatCompletionUserMessageParam.builder()
                    .contentOfArrayOfContentParts(contentParts)
                    .build()
                )  
            ))
            .responseFormat(responseFormatClass)
            .build();
        
        StructuredChatCompletion<T> response = client.chat().completions().create(params);

        var choice = response.choices().get(0);
        System.out.println("[DEBUG] OpenAI: Respuesta recibida.");
        return choice.message().content().get();
    }

    public static InputStream GenerateAudioMp3(String script){
        System.out.println("[DEBUG] OpenAI: Generando audio TTS...");
        SpeechCreateParams params = SpeechCreateParams.builder()
        .model(SpeechModel.TTS_1)
        .voice("coral")
        .speed(1)
        .input(script)
        .build();

        HttpResponse res = client.audio().speech().create(params);
        System.out.println("[DEBUG] OpenAI: Audio generado.");
        return res.body();
        
    }

    public static File GenerateImage(String prompt, int size, String name) throws Exception{
        System.out.println("[DEBUG] OpenAI: Generando imagen (DALL-E)...");
        ImageGenerateParams params = ImageGenerateParams.builder()
        .prompt(prompt)
        .n(1)
        .model(ImageModel.DALL_E_2)
        .responseFormat(ImageGenerateParams.ResponseFormat.B64_JSON)
        .size(Size._1024X1024)
        .build();

        ImagesResponse res = client.images().generate(params);

        String b64data = res.data()
        .orElseThrow(()-> new RuntimeException("No se generaron las imagenes"))
        .get(0)
        .b64Json()
        .orElseThrow(()-> new RuntimeException("No se genero correctamente la imagen"));

        byte[] bytes = Base64.getDecoder().decode(b64data);

        File f = new File(name);
        f.delete();
        FileOutputStream fos = new FileOutputStream(f);

        fos.write(bytes);
        fos.close();
        
        System.out.println("[DEBUG] OpenAI: Imagen generada y guardada en: " + name);
        return f;
    }
}
