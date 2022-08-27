package cc.envkeeper.app.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the reCAPTCHA siteKey.
 */
public class CaptchaDTO implements Serializable {

    private String siteKey;

    public CaptchaDTO(String siteKey) {
        this.siteKey = siteKey;
    }

    public String getSiteKey() {
        return siteKey;
    }

    public void setSiteKey(String siteKey) {
        this.siteKey = siteKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CaptchaDTO that = (CaptchaDTO) o;
        return Objects.equals(siteKey, that.siteKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(siteKey);
    }

    @Override
    public String toString() {
        return "CaptchaDTO{" +
            "siteKey='" + siteKey + '\'' +
            '}';
    }
}
