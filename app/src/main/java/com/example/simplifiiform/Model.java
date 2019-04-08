package com.example.simplifiiform;

public class Model {

    private String type, label, name;
    private int min, max, interval;
    private String inputType;
    private String validationName, validationMsg;
    private String action;
    private String apiUri;
    private boolean authEnabled;

    public boolean getAuthEnabled() {
        return authEnabled;
    }

    public void setAuthEnabled(boolean authEnabled) {
        this.authEnabled = authEnabled;
    }

    public String getApiUri() {
        return apiUri;
    }

    public void setApiUri(String apiUri) {
        this.apiUri = apiUri;
    }

    public String getApiMethod() {
        return apiMethod;
    }

    public void setApiMethod(String apiMethod) {
        this.apiMethod = apiMethod;
    }

    private String apiMethod;


    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    //for range
    public Model(){

    }
    public Model(String type, String label, String name, int min, int max, int interval) {
        this.type = type;
        this.label = label;
        this.name = name;
        this.min = min;
        this.max = max;
        this.interval = interval;
    }

    //for email input
    public Model(String type, String label, String name, String inputType, String validationName, String validationMsg) {
        this.type = type;
        this.label = label;
        this.name = name;
        this.inputType = inputType;
        this.validationName = validationName;
        this.validationMsg = validationMsg;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public String getInputType() {
        return inputType;
    }

    public void setInputType(String inputType) {
        this.inputType = inputType;
    }

    public String getValidationName() {
        return validationName;
    }

    public void setValidationName(String validationName) {
        this.validationName = validationName;
    }

    public String getValidationMsg() {
        return validationMsg;
    }

    public void setValidationMsg(String validationMsg) {
        this.validationMsg = validationMsg;
    }
}
