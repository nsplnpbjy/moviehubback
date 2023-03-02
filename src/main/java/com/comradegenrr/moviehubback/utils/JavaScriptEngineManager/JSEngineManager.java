package com.comradegenrr.moviehubback.utils.JavaScriptEngineManager;

import java.io.FileNotFoundException;
import java.io.FileReader;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JSEngineManager {

    @Bean(name = "jsEngine")
    public ScriptEngine jsEngine(){
        return new ScriptEngineManager().getEngineByName("javascript");
    }

    @Bean(name = "jsInvocable")
    public Invocable jsInvocable() throws FileNotFoundException, ScriptException{
        ScriptEngine jsEngine = jsEngine();
        jsEngine.eval(new FileReader("base64.js"));
        return (Invocable) jsEngine;
    }
    
}
