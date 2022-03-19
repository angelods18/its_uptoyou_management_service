package it.itsuptoyou.models;

import java.util.Map;

import lombok.Data;

@Data
public class NoteFirebase {

	private String subject;
    private String content;
    private Map<String, String> data;
    private String image;
}
