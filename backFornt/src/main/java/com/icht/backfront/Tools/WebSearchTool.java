package com.icht.backfront.Tools;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface WebSearchTool {
    String search(String text) throws JsonProcessingException;
}
