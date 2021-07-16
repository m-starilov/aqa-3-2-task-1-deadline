package ru.netology.web.page;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.Keys;
import ru.netology.web.data.DataHelper;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

public class LoginPage {
    private final SelenideElement loginField = $("[data-test-id=login] input");
    private final SelenideElement passwordField = $("[data-test-id=password] input");
    private final SelenideElement loginButton = $("[data-test-id=action-login]");
    private final SelenideElement wrongLoginOrPassMassage = $(byText("Неверно указан логин или пароль"));
    private final SelenideElement blockMassage = $(byText("Учётная запись временно заблокирована"));
    private final String deleteString = Keys.chord(Keys.CONTROL, "a") + Keys.DELETE;

    public VerificationPage login(DataHelper.AuthInfo info) {
        loginField.setValue(deleteString).setValue(info.getLogin());
        passwordField.setValue(deleteString).setValue(info.getPassword());
        loginButton.click();
        return new VerificationPage();
    }

    public void loginWithInvalidData(DataHelper.AuthInfo info) {
        loginField.setValue(deleteString).setValue(info.getLogin());
        passwordField.setValue(deleteString).setValue(info.getPassword());
        loginButton.click();
    }

    public void shouldHaveErrorMassage() {
        wrongLoginOrPassMassage.shouldBe(visible);
    }

    public void shouldHaveBlockMassage() {
        blockMassage.shouldBe(visible);
    }
}
