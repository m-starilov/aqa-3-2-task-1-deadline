package ru.netology.web.page;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.Keys;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

public class VerificationPage {
    private final SelenideElement codeField = $("[data-test-id=code] input");
    private final SelenideElement verifyButton = $("[data-test-id=action-verify]");
    private final SelenideElement invalidCodeMessage = $(byText("Неверно указан код! Попробуйте ещё раз."));
    private final SelenideElement blockMessage = $(byText("Учётная запись временно заблокирована"));
    private final String deleteString = Keys.chord(Keys.CONTROL, "a") + Keys.DELETE;

    public VerificationPage() {
        codeField.shouldBe(visible);
    }

    public DashboardPage verify(String verificationCode) {
        codeField.setValue(deleteString).setValue(verificationCode);
        verifyButton.click();
        return new DashboardPage();
    }

    public void verifyWithInvalidCode(String verificationCode) {
        codeField.setValue(deleteString).setValue(verificationCode);
        verifyButton.click();
    }

    public void shouldHaveInvalidCodeMessage() {
        invalidCodeMessage.shouldBe(visible);
    }

    public void shouldHaveBlockMessage() {
        blockMessage.shouldBe(visible);
    }
}
