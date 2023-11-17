package eu.luminis.politicsrag.model;

public class QuestionForm {
    private String messageText;

    public QuestionForm(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }
}
