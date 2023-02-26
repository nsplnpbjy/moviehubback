package com.comradegenrr.moviehubback.service.mainfunc;

import com.comradegenrr.moviehubback.standerio.StanderInput;
import com.comradegenrr.moviehubback.standerio.StanderOutput;
import java.io.IOException;

import javax.script.ScriptException;

public interface MainService {
    
    public StanderOutput doSearchWithInternet(StanderInput standerInput) throws IOException, NoSuchMethodException, ScriptException;

    public StanderOutput doSearchWithMongoDB(StanderInput standerInput) throws IOException;

    public StanderOutput doSearchSmart(StanderInput standerInput) throws IOException, NoSuchMethodException, ScriptException;

}
