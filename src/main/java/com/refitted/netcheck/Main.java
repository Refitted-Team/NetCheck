package com.refitted.netcheck;

import com.google.gson.*;

import java.io.*;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Main {
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        JsonObject jsonObject = JsonParser.parseReader(new FileReader("supportedVersions.json")).getAsJsonObject();

        for (JsonElement jsonElement : jsonObject.asMap().values()) {
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            for (JsonElement jsonElement1 : jsonArray) {
                String path = jsonElement1.getAsJsonObject().get("url").getAsString();
                String sha1 = sha1Code("." + path);
                jsonElement1.getAsJsonObject().addProperty("sha1", sha1);
            }
        }

        File file = new File("supportedVersions.json");
        file.delete();
        file.createNewFile();

        FileWriter fileWriter = new FileWriter(file);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        fileWriter.write(gson.toJson(jsonObject));
        fileWriter.close();

        File fileSha1 = new File("supportedVersions.json.sha1");
        fileSha1.delete();
        fileSha1.createNewFile();

        FileWriter fileWriterSha1 = new FileWriter(fileSha1);
        fileWriterSha1.write(sha1Code("supportedVersions.json"));
        fileWriterSha1.close();
    }

    private static String sha1Code(String filePath) throws IOException, NoSuchAlgorithmException {
        FileInputStream fileInputStream = new FileInputStream(filePath);
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        DigestInputStream digestInputStream = new DigestInputStream(fileInputStream, digest);
        byte[] bytes = new byte[1024];
        while (digestInputStream.read(bytes) > 0);

        byte[] resultByteArry = digest.digest();
        return bytesToHexString(resultByteArry);
    }

    private static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            int value = b & 0xFF;
            if (value < 16) {
                sb.append("0");
            }
            sb.append(Integer.toHexString(value).toUpperCase());
        }
        return sb.toString();
    }
}
