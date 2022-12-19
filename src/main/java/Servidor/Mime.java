package Servidor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Mime {
	public static List<List<String>> mimeTypes;

	public Mime() {
		mimeTypes = new ArrayList<>();
		mimeTypes.add(Arrays.asList("doc", "application/msword"));
                mimeTypes.add(Arrays.asList("pdf", "application/pdf"));
                mimeTypes.add(Arrays.asList("rar", "application/x-rar-compressed"));
                mimeTypes.add(Arrays.asList("mp3", "audio/mpeg"));
                mimeTypes.add(Arrays.asList("jpg", "image/jpeg"));
                mimeTypes.add(Arrays.asList("jpeg", "image/jpeg"));
                mimeTypes.add(Arrays.asList("png", "image/png"));
                mimeTypes.add(Arrays.asList("html", "text/html"));
                mimeTypes.add(Arrays.asList("htm", "text/html"));
                mimeTypes.add(Arrays.asList("c", "text/plain"));
                mimeTypes.add(Arrays.asList("txt", "text/plain"));
                mimeTypes.add(Arrays.asList("java", "text/plain"));
                mimeTypes.add(Arrays.asList("mp4", "video/mp4"));

	}

	public String get(String extension) {
            for (List<String> mimeType : mimeTypes) {
                if (mimeType.get(0).equals(extension)) {
                    return mimeType.get(1);
                }
            }
            return "application/octet-stream";
        }

}