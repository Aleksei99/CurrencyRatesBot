package com.smuraha.service;

import java.io.IOException;

public interface JsoupParserService {
    void parseAndUpdate(String url) throws IOException;
}
