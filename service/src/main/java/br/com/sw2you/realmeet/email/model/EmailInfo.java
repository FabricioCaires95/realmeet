package br.com.sw2you.realmeet.email.model;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class EmailInfo {
    private final String from;
    private final List<String> to;
    private final List<String> cc;
    private final List<String> bcc;
    private final String subject;
    private final List<Attachment> attachements;
    private final String template;
    private final Map<String, Object> templateData;

    private EmailInfo(Builder builder) {
        from = builder.from;
        to = builder.to;
        cc = builder.cc;
        bcc = builder.bcc;
        subject = builder.subject;
        attachements = builder.attachements;
        template = builder.template;
        templateData = builder.templateData;
    }

    public String getFrom() {
        return from;
    }

    public List<String> getTo() {
        return to;
    }

    public List<String> getCc() {
        return cc;
    }

    public List<String> getBcc() {
        return bcc;
    }

    public String getSubject() {
        return subject;
    }

    public List<Attachment> getAttachements() {
        return attachements;
    }

    public String getTemplate() {
        return template;
    }

    public Map<String, Object> getTemplateData() {
        return templateData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmailInfo emailInfo = (EmailInfo) o;
        return (
            Objects.equals(from, emailInfo.from) &&
            Objects.equals(to, emailInfo.to) &&
            Objects.equals(cc, emailInfo.cc) &&
            Objects.equals(bcc, emailInfo.bcc) &&
            Objects.equals(subject, emailInfo.subject) &&
            Objects.equals(attachements, emailInfo.attachements) &&
            Objects.equals(template, emailInfo.template) &&
            Objects.equals(templateData, emailInfo.templateData)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, cc, bcc, subject, attachements, template, templateData);
    }

    @Override
    public String toString() {
        return (
            "EmailInfo{" +
            "from='" +
            from +
            '\'' +
            ", to=" +
            to +
            ", cc=" +
            cc +
            ", bcc=" +
            bcc +
            ", subject='" +
            subject +
            '\'' +
            ", attachements=" +
            attachements +
            ", template='" +
            template +
            '\'' +
            ", templateData=" +
            templateData +
            '}'
        );
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder {
        private String from;
        private List<String> to;
        private List<String> cc;
        private List<String> bcc;
        private String subject;
        private List<Attachment> attachements;
        private String template;
        private Map<String, Object> templateData;

        private Builder() {}

        public static Builder anEmailInfo() {
            return new Builder();
        }

        public Builder from(String from) {
            this.from = from;
            return this;
        }

        public Builder to(List<String> to) {
            this.to = to;
            return this;
        }

        public Builder cc(List<String> cc) {
            this.cc = cc;
            return this;
        }

        public Builder bcc(List<String> bcc) {
            this.bcc = bcc;
            return this;
        }

        public Builder subject(String subject) {
            this.subject = subject;
            return this;
        }

        public Builder attachements(List<Attachment> attachements) {
            this.attachements = attachements;
            return this;
        }

        public Builder template(String template) {
            this.template = template;
            return this;
        }

        public Builder templateData(Map<String, Object> templateData) {
            this.templateData = templateData;
            return this;
        }

        public EmailInfo build() {
            return new EmailInfo(this);
        }
    }
}